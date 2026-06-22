#include "pk.h"
#include "dmi.h"
#include "cjson.h"
#include <mbedtls/pk.h>
#include <mbedtls/entropy.h>
#include <mbedtls/ctr_drbg.h>
#include <stdlib.h>
#include <stdio.h>
#include <syslog.h>
#include <string.h>
#include <sys/stat.h>

static pk_buffer_t sign(const char *keyfile, pk_buffer_t src)
{
    mbedtls_pk_context ctx;
    mbedtls_pk_init(&ctx);

    mbedtls_entropy_context entropy;
    mbedtls_entropy_init(&entropy);

    mbedtls_ctr_drbg_context ctr_drbg;
    mbedtls_ctr_drbg_init(&ctr_drbg);

    // 加载密钥
    mbedtls_ctr_drbg_seed(&ctr_drbg, mbedtls_entropy_func, &entropy, NULL, 0);
    mbedtls_pk_parse_keyfile(&ctx, keyfile, "", mbedtls_ctr_drbg_random, &ctr_drbg);

    u8 hash[32] = {0};
    mbedtls_md(mbedtls_md_info_from_type(MBEDTLS_MD_SHA256), src.data, src.len, hash);
    size_t olen = 0;
    u8 sign_data[MBEDTLS_PK_SIGNATURE_MAX_SIZE] = {0};
    mbedtls_pk_sign(&ctx, MBEDTLS_MD_SHA256, hash, 0, sign_data, sizeof(sign_data),
        &olen, mbedtls_ctr_drbg_random, &ctr_drbg);

    pk_buffer_t signed_data = {sign_data, olen};
    pk_buffer_t enc_signed_data = base64_encode(signed_data);
    mbedtls_pk_free(&ctx);
    mbedtls_entropy_free(&entropy);
    mbedtls_ctr_drbg_free(&ctr_drbg);
    return enc_signed_data;
}

static int pk_decrypt(const char *keyfile, const char *filename)
{
    mbedtls_ctr_drbg_context ctr_drbg;
    mbedtls_ctr_drbg_init(&ctr_drbg);

    mbedtls_pk_context ctx;
    mbedtls_pk_init(&ctx);

    mbedtls_entropy_context entropy;
    mbedtls_entropy_init(&entropy);

    mbedtls_ctr_drbg_seed(&ctr_drbg, mbedtls_entropy_func, &entropy, NULL, 0);
    mbedtls_pk_parse_keyfile(&ctx, keyfile, "", mbedtls_ctr_drbg_random, &ctr_drbg);

    // 获取文件大小并读取全部的文件内容
    struct stat statbuf;
    stat(filename, &statbuf);
    u8 *file_data = malloc(statbuf.st_size);
    FILE *fp = fopen(filename, "r");
    size_t olen = fread(file_data, 1, statbuf.st_size, fp);
    fclose(fp);

    // base64解码
    pk_buffer_t enc_data = {file_data, olen};
    pk_buffer_t dec_data = base64_decode(enc_data);
    free(file_data);

    // RSA解密数据
    olen = 0;
    u8 out_buffer[4096] = {0};
    int ret = mbedtls_pk_decrypt(&ctx, dec_data.data, dec_data.len,
        out_buffer, &olen, sizeof(out_buffer),
        mbedtls_ctr_drbg_random, &ctr_drbg);
    free(dec_data.data);
    
    if (0 != ret) {
        return -1;
    }


    printf("RSA解码的数据: %s\n", out_buffer);
    // 释放资源
    mbedtls_pk_free(&ctx);
    mbedtls_ctr_drbg_free(&ctr_drbg);
    mbedtls_entropy_free(&entropy);
    return 0;
}

static void do_sign(const char* keyfile) {
    cJSON *jsonData = get_apply_data();
    char *json = cJSON_PrintUnformatted(jsonData);
    cJSON_Delete(jsonData);
    pk_buffer_t src = {(u8*)json, strlen(json)};
    // 对JSON数据进行签名
    pk_buffer_t sign_data = sign(keyfile, src);
    printf("签名数据:\n%s\n", sign_data.data);
    free(src.data);
    free(sign_data.data);
}

int main(int argc, char * const argv[])
{
    if (3 == argc && 0 == strcmp(argv[1], "-s")) {
        do_sign(argv[2]);
        return 0;
    }

    if (4 == argc && 0 == strcmp(argv[1], "-d")) {
        pk_decrypt(argv[2], argv[3]);
        return 0;
    }

    printf("使用说明:\n");
    printf("签名: sudo %s -s <秘钥文件>\n", argv[0]);
    printf("解密: sudo %s -d <秘钥文件> <加密文件>\n", argv[0]);
    return 0;
}

