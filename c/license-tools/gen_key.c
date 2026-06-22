#include "types.h"
#include "util.h"
#include <mbedtls/pk.h>
#include <mbedtls/entropy.h>
#include <mbedtls/ctr_drbg.h>
#include <stdio.h>
#include <string.h>
#include <syslog.h>

#define KEY_SIZE    (4096)

static const char *PUBLIC_KEY = "public.pem";
static const char *PRIVATE_KEY = "private.pem";

static void gen_key_pair(void)
{
    mbedtls_ctr_drbg_context ctr_drbg;
    mbedtls_ctr_drbg_init(&ctr_drbg);

    mbedtls_pk_context ctx;
    mbedtls_pk_init(&ctx);

    mbedtls_entropy_context entropy;
    mbedtls_entropy_init(&entropy);

    // 安装RSA算法
    mbedtls_ctr_drbg_seed(&ctr_drbg, mbedtls_entropy_func, &entropy, NULL, 0);
    mbedtls_pk_setup(&ctx, mbedtls_pk_info_from_type(MBEDTLS_PK_RSA));
    mbedtls_rsa_gen_key(mbedtls_pk_rsa(ctx), mbedtls_ctr_drbg_random, &ctr_drbg, KEY_SIZE, 65537);

    u8 data[KEY_SIZE] = {0};
    mbedtls_pk_write_key_pem(&ctx, data, KEY_SIZE);
    write_file(PRIVATE_KEY, data, strlen((char*)data));

    data[0] = 0;
    mbedtls_pk_write_pubkey_pem(&ctx, data, KEY_SIZE);
    write_file(PUBLIC_KEY, data, strlen((char*)data));

    mbedtls_pk_free(&ctx);
    mbedtls_ctr_drbg_free(&ctr_drbg);
    mbedtls_entropy_free(&entropy);
}

int main(void)
{
    openlog("LICENSE", LOG_CONS, LOG_DAEMON);
    printf("%s\n", "开始生成密钥对");
    gen_key_pair();
    printf("私钥已经写入%s\n", PRIVATE_KEY);
    printf("公钥已经写入%s\n", PUBLIC_KEY);
    closelog();
    return 0;
}

