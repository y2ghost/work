#include "pk.h"
#include "dmi.h"
#include "cjson.h"
#include <mbedtls/base64.h>
#include <mbedtls/pk.h>
#include <mbedtls/entropy.h>
#include <mbedtls/ctr_drbg.h>
#include <stdlib.h>
#include <stdio.h>
#include <syslog.h>
#include <sys/stat.h>
#include <string.h>
#include <time.h>

pk_buffer_t base64_encode(const pk_buffer_t src)
{
    size_t olen = 0;
    mbedtls_base64_encode(NULL, 0, &olen, src.data, src.len);
    u8 *data = calloc(olen+1, 1);
    pk_buffer_t out = {NULL};
    out.data = data;
    mbedtls_base64_encode(out.data, olen, &(out.len), src.data, src.len);
    return out;
}

pk_buffer_t base64_decode(const pk_buffer_t src)
{
    size_t olen = 0;
    pk_buffer_t out = {NULL, 0};
    int err = mbedtls_base64_decode(NULL, 0, &olen, src.data, src.len);

    if (MBEDTLS_ERR_BASE64_INVALID_CHARACTER == err) {
        syslog(LOG_ERR, "解码数据异常(%d)", err);
        return out;
    }

    u8 *data = calloc(olen+1, 1);
    out.data = data;
    mbedtls_base64_decode(out.data, olen, &(out.len), src.data, src.len);
    return out;
}

int verify(pk_license_t license)
{
    mbedtls_pk_context ctx;
    mbedtls_pk_init(&ctx);
    mbedtls_pk_parse_public_keyfile(&ctx, PUBLIC_KEY);

    pk_buffer_t src = {(u8*)license.data, strlen(license.data)};
    pk_buffer_t sig = {(u8*)license.sig, strlen(license.sig)};

    // 签名base64解码
    pk_buffer_t dec_sign = base64_decode(sig);
    // 校验HASH和签名
    u8 hash[32] = {0};
    mbedtls_md(mbedtls_md_info_from_type(MBEDTLS_MD_SHA256), src.data, src.len, hash);
    int ret = mbedtls_pk_verify(&ctx, MBEDTLS_MD_SHA256, hash, 0, dec_sign.data, dec_sign.len);
    free(dec_sign.data);
    mbedtls_pk_free(&ctx);

    if (0 != ret) {
        return -1;
    }

    cJSON *licJsonData = cJSON_Parse((char*)license.data);
    if (NULL == licJsonData) {
        syslog(LOG_ERR, "解析授权数据异常");
        return -1;
    }

    const char *expire_time_str = get_item_value(licJsonData, "expireTime");
    // 提前存储超时时间
    char time_str[24] = {0};
    snprintf(time_str, sizeof(time_str), "%s 23:59:59", expire_time_str);
    // 判断授权数据是否合法
    cJSON_DeleteItemFromObject(licJsonData, "signer");
    cJSON_DeleteItemFromObject(licJsonData, "issueTime");
    cJSON_DeleteItemFromObject(licJsonData, "expireTime");
    cJSON *sysJsonData = get_apply_data();
    ret = cJSON_Compare(sysJsonData, licJsonData, 0);
    cJSON_Delete(sysJsonData);
    cJSON_Delete(licJsonData);

    if (0 == ret) {
        return -1;
    }

    struct tm expire_tm = {0};
    strptime(time_str, "%Y-%m-%d %H:%M:%S", &expire_tm);
    time_t expire_time = mktime(&expire_tm);
    time_t now = time(NULL);

    if (now > expire_time) {
        return -2;
    }

    return 0;
}

pk_buffer_t pk_encrypt(pk_buffer_t src)
{
    mbedtls_ctr_drbg_context ctr_drbg;
    mbedtls_ctr_drbg_init(&ctr_drbg);

    mbedtls_pk_context ctx;
    mbedtls_pk_init(&ctx);

    mbedtls_entropy_context entropy;
    mbedtls_entropy_init(&entropy);

    mbedtls_ctr_drbg_seed(&ctr_drbg, mbedtls_entropy_func, &entropy, NULL, 0);
    mbedtls_pk_parse_public_keyfile(&ctx, PUBLIC_KEY);

    u8 enc_buffer[4096] = {0};
    size_t olen = 0;
    int ret = mbedtls_pk_encrypt(&ctx, src.data, src.len,
        enc_buffer, &olen, sizeof(enc_buffer),
        mbedtls_ctr_drbg_random, &ctr_drbg);

    if (0 != ret) {
        pk_buffer_t result = {NULL, 0};
        return result;
    }

    pk_buffer_t enc_data = {enc_buffer, olen};
    pk_buffer_t out_data = base64_encode(enc_data);
    // 释放资源
    mbedtls_pk_free(&ctx);
    mbedtls_ctr_drbg_free(&ctr_drbg);
    mbedtls_entropy_free(&entropy);
    return out_data;
}

pk_license_t load_license_file(void)
{
    return load_license_file2(LICENSE_KEY);
}

pk_license_t load_license_file2(const char *filename)
{
     // 获取文件大小并读取全部的文件内容
    pk_license_t result = {NULL, NULL};
    struct stat statbuf;

    if (NULL==filename || 0!=stat(filename, &statbuf)) {
        syslog(LOG_ERR, "加载授权文件异常");
        return result;
    }

    if (statbuf.st_size > MAX_LICENSE_SIZE) {
        syslog(LOG_ERR, "授权文件大小异常");
        return result;
    }

    u8 *file_data = malloc(statbuf.st_size);
    FILE *fp = fopen(filename, "r");
    size_t olen = fread(file_data, 1, statbuf.st_size, fp);
    fclose(fp);

    // base64解码
    pk_buffer_t enc_data = {file_data, olen};
    pk_buffer_t dec_data = base64_decode(enc_data);
    free(file_data);

    // JSON数据解析
    cJSON *jsonData = cJSON_Parse((char*)dec_data.data);
    if (NULL == jsonData) {
        syslog(LOG_ERR, "解析授权文件异常");
        return result;
    }

    free(dec_data.data);
    const char *license_data = get_item_value(jsonData, "licenseData");
    const char *signature = get_item_value(jsonData, "signature");
    result.data = strdup(license_data);
    result.sig = strdup(signature);
    cJSON_Delete(jsonData);
    return result;
}

