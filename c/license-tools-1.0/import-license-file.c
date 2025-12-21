#include "pk.h"
#include "dmi.h"
#include "util.h"
#include "cjson.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <syslog.h>

static const char *ITEM_NAMES[] = {
    "产品厂商",
    "主板型号",
    "产品型号",
    "产品序号",
    "产品UUID",
};
static const char *ITEM_KEYS[] = {
    "productVendor",
    "boardName",
    "productName",
    "productSerial",
    "productUuid",
};
static const char *LICENSE_NAMES[] = {
    "签发人",
    "签发日期",
    "到期日期",
};
static const char *LICENSE_KEYS[] = {
    "signer",
    "issueTime",
    "expireTime",
};

int main(int argc, char * const argv[])
{
    if (2 != argc) {
        fprintf(stderr, "用法: %s <授权文件>\n", argv[0]);
        return -1;
    }

    openlog("LICENSE", LOG_CONS, LOG_DAEMON);
    const char *filename = argv[1];
    pk_license_t license = load_license_file2(filename);

    if (NULL == license.data) {
        fprintf(stderr, "解析数据失败:\n文件不存在或是下载合法的授权文件\n");
        closelog();
        return -1;
    }

    cJSON *jsonData = cJSON_Parse((char*)license.data);
    fprintf(stdout, "许可系统数据:\n");
    for (size_t i = 0; i < sizeof(ITEM_NAMES) / sizeof(ITEM_NAMES[0]); i++) {
        const char *value = get_item_value(jsonData, ITEM_KEYS[i]);
        fprintf(stdout, "%s: %s\n", ITEM_NAMES[i], value);
    }

    fprintf(stdout, "\n许可授权数据:\n");
    for (size_t i = 0; i < sizeof(LICENSE_NAMES) / sizeof(LICENSE_NAMES[0]); i++) {
        const char *value = get_item_value(jsonData, LICENSE_KEYS[i]);
        fprintf(stdout, "%s: %s\n", LICENSE_NAMES[i], value);
    }

    cJSON_Delete(jsonData);
    int rc = verify(license);
    const char *err = "有效";

    if (-1 == rc) {
        err = "无效";
    } else if (-2 == rc) {
        err = "过期";
    }

    fprintf(stdout, "\n许可授权状态:\n%s\n", err);
    free(license.data);
    free(license.sig);
    closelog();

    if (0 == rc) {
        size_t olen = MAX_LICENSE_SIZE;
        u8 *buf = read_file(0, &olen, filename);
        rc = write_file(LICENSE_KEY, buf, olen);
        free(buf);
    }

    err = "失败";
    if (0 == rc) {
        err = "成功";
    }

    fprintf(stdout, "\n许可导入结果:\n%s\n", err);
    return 0;
}

