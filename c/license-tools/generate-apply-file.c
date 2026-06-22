#include "pk.h"
#include "dmi.h"
#include "util.h"
#include "cjson.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <syslog.h>

static const char *APPLY_KEY_FILE = "/opt/license-tools/output/apply.key";
static const char *ITEM_NAMES[] = {
    "产品厂商",
    "主板型号",
    "产品型号",
    "产品序号",
    "系统UUID"
};
static const char *ITEM_KEYS[] = {
    "productVendor",
    "boardName",
    "productName",
    "productSerial",
    "productUuid"
};

int main(void)
{
    openlog("LICENSE", LOG_CONS, LOG_DAEMON);
    cJSON *jsonData = get_apply_data();

    if (NULL == jsonData) {
        fprintf(stderr, "获取系统数据失败\n");
        closelog();
        return -1;
    }

    fprintf(stdout, "系统数据:\n");
    for (size_t i = 0; i < sizeof(ITEM_NAMES) / sizeof(ITEM_NAMES[0]); i++) {
        const char *value = get_item_value(jsonData, ITEM_KEYS[i]);
        fprintf(stdout, "%s: %s\n", ITEM_NAMES[i], value);
    }

    // 加密并入数据到申请文件中去
    char *json = cJSON_PrintUnformatted(jsonData);
    cJSON_Delete(jsonData);
    pk_buffer_t src = {(u8*)json, strlen(json)};
    pk_buffer_t enc = pk_encrypt(src);
    free(src.data);

    if (NULL == enc.data) {
        fprintf(stderr, "\n无法正常加密系统数据: 请检查公钥文件\n");
        return -1;
    }

    int rc = write_file(APPLY_KEY_FILE, enc.data, enc.len);
    free(enc.data);

    if (0 == rc) {
        fprintf(stdout, "\n生成的申请文件路径: %s\n\n", APPLY_KEY_FILE);
    } else {
        fprintf(stderr, "\n无法写入数据到文件: %s\n\n", APPLY_KEY_FILE);
    }

    closelog();
    return rc;
}

