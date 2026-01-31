#include "dmi.h"
#include "util.h"
#include "cjson.h"
#include "config.h"
#include "version.h"
#include "pk.h"
#include <stdio.h>
#include <string.h>
#include <syslog.h>
#include <stdlib.h>

#define FLAG_NO_FILE_OFFSET     (1 << 0)
#define FLAG_STOP_AT_EOT        (1 << 1)
#define UUID_BUFFER_SIZE        (64)

#define SYS_FIRMWARE_DIR "/sys/firmware/dmi/tables"
#define SYS_ENTRY_FILE SYS_FIRMWARE_DIR "/smbios_entry_point"
#define SYS_TABLE_FILE SYS_FIRMWARE_DIR "/DMI"

struct dmi_header {
    u8 type;
    u8 length;
    u16 handle;
    u8 *data;
};

// 使用.号替换非ASCII字符
static void ascii_filter(char *bp, size_t len)
{
    for (size_t i = 0; i < len; i++) {
        if (bp[i] < 32 || bp[i] >= 127) {
            bp[i] = '.';
        }
    }
}

static char *_dmi_string(const struct dmi_header *dm, u8 s, int filter)
{
    char *bp = (char *)dm->data;
    bp += dm->length;

    while (s > 1 && *bp) {
        bp += strlen(bp);
        bp++;
        s--;
    }

    if (!*bp) {
        return NULL;
    }

    if (filter) {
        ascii_filter(bp, strlen(bp));
    }

    return bp;
}

static const char *dmi_string(const struct dmi_header *dm, u8 s)
{
    if (s == 0) {
        return "Not Specified";
    }

    char *bp = _dmi_string(dm, s, 0);
    if (bp == NULL) {
        return "<BAD INDEX>";
    }

    return bp;
}

static void dmi_system_uuid(int (*print_cb)(char *str, size_t size, const char *format, ...),
    char *buffer, const u8 *p, u16 ver)
{
    int only0xFF = 1;
    int only0x00 = 1;

    for (int i = 0; i < 16 && (only0x00 || only0xFF); i++) {
        if (p[i] != 0x00) only0x00 = 0;
        if (p[i] != 0xFF) only0xFF = 0;
    }

    if (only0xFF) {
        print_cb(buffer, UUID_BUFFER_SIZE, "Not Present");
        return;
    }

    if (only0x00) {
        print_cb(buffer, UUID_BUFFER_SIZE, "Not Settable");
        return;
    }

    /*
     * SMBIOS2.6版本之后UUID前三个字节小端序
     * 老版本则使用网络序(大端法)
     */
    if (ver >= 0x0206) {
        print_cb(buffer, UUID_BUFFER_SIZE,
            "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
            p[3], p[2], p[1], p[0], p[5], p[4], p[7], p[6],
            p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
    } else {
        print_cb(buffer, UUID_BUFFER_SIZE,
            "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
            p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
            p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
    }
}

static void dmi_decode(const struct dmi_header *h, u16 ver, cJSON *jsonData)
{
    const u8 *data = h->data;
    switch (h->type) {
        case 1: /* 系统信息 */
            if (h->length < 0x08) break;
            cJSON_AddStringToObject(jsonData, "productVendor", dmi_string(h, data[0x04]));
            cJSON_AddStringToObject(jsonData, "productName", dmi_string(h, data[0x05]));
            cJSON_AddStringToObject(jsonData, "productSerial", dmi_string(h, data[0x07]));
            if (h->length < 0x19) break;
            char uuid_buffer[UUID_BUFFER_SIZE] = {0};
            dmi_system_uuid(snprintf, uuid_buffer, data + 0x08, ver);
            cJSON_AddStringToObject(jsonData, "productUuid", uuid_buffer);
            if (h->length < 0x1B) break;
            cJSON_AddStringToObject(jsonData, "productFamily", dmi_string(h, data[0x1A]));
            break;
        case 2: /* 主板信息 */
            if (h->length < 0x08) break;
            cJSON_AddStringToObject(jsonData, "boardVendor", dmi_string(h, data[0x04]));
            cJSON_AddStringToObject(jsonData, "boardName", dmi_string(h, data[0x05]));
            break;
        default: /* 不做事儿 */
            break;
    }
}

static void to_dmi_header(struct dmi_header *h, u8 *data)
{
    h->type = data[0];
    h->length = data[1];
    h->handle = WORD(data + 2);
    h->data = data;
}

static void dmi_table_decode(u8 *buf, u32 len, u16 num,
    u16 ver, u32 flags, cJSON *jsonData)
{
    u8 *data = buf;
    int i = 0;

    // SMBIOS结构头的长度为4
    while ((i < num || !num) && data + 4 <= buf + len) {
        struct dmi_header h;
        to_dmi_header(&h, data);

        if (h.length < 4) {
            syslog(LOG_ERR, "解析系统数据结构长度(%u)异常",
                (unsigned int)h.length);
            break;
        }

        i++;
        u8 *next = data + h.length;

        while ((unsigned long)(next - buf + 1) < len
            && (next[0] != 0 || next[1] != 0)) {
            next++;
        }

        next += 2;
        if ((unsigned long)(next - buf) > len) {
            syslog(LOG_ERR, "系统数据长度截断");
            data = next;
            break;
        }

        dmi_decode(&h, ver, jsonData);
        data = next;

        // SMBIOSv3要求此处停止解析
        if (127==h.type && (flags&FLAG_STOP_AT_EOT)) {
            break;
        }
    }

    // SMBIOSv3 64位没有说明结构数量，只是说明了整体表大小
    if (num && i != num) {
        syslog(LOG_ERR, "系统数据结构数:%d, 解析数: %d, 不匹配", num, i);
        if ((unsigned long)(data - buf) > len
         || (num && (unsigned long)(data - buf) < len)) {
            syslog(LOG_ERR, "系统数据字节数: %u, 解析数: %lu, 不匹配",
                len, (unsigned long)(data - buf));
        }
    }
}

// 分配缓存，调用者释放
static u8 *dmi_table_get(off_t base, u32 *len, u16 num,
    const char *devmem, u32 flags)
{
    u8 *buf;
    if (flags & FLAG_NO_FILE_OFFSET) {
        size_t size = *len;
        buf = read_file(0, &size, devmem);

        if (num && size != (size_t)*len) {
            syslog(LOG_ERR, "读取系统数据异常%u:%lu",
                *len, (unsigned long)size);
        }

        *len = size;
    } else {
        buf = mem_chunk(base, *len, devmem);
    }

    if (NULL == buf) {
        syslog(LOG_ERR, "读取系统数据异常");
    }

    return buf;
}

static int smbios3_decode(u8 *buf, const char *devmem, u32 flags, cJSON *jsonData)
{
    if (buf[0x06] > 0x20) {
        syslog(LOG_ERR, "校验数据%u字节,期望%u字节",
            (unsigned int)buf[0x06], 0x18U);
        return 0;
    }

    if (buf[0x06] < 0x18
     || !checksum(buf, buf[0x06])) {
        return 0;
    }

    u64 offset = QWORD(buf + 0x10);
    if (!(flags&FLAG_NO_FILE_OFFSET) && offset.h && sizeof(off_t)<8) {
        syslog(LOG_ERR, "64位偏移不支持");
        return 0;
    }

    u32 ver = (buf[0x07] << 16) + (buf[0x08] << 8) + buf[0x09];
    u32 len = DWORD(buf + 0x0C);
    u8 *table = dmi_table_get(((off_t)offset.h << 32) | offset.l, &len, 0,
        devmem, flags | FLAG_STOP_AT_EOT);

    if (NULL == table) {
        return 1;
    }

    dmi_table_decode(table, len, 0, ver>>8, flags|FLAG_STOP_AT_EOT, jsonData);
    free(table);
    return 1;
}

static int smbios_decode(u8 *buf, const char *devmem, u32 flags, cJSON *jsonData)
{
    // 检查校验字节长度
    if (buf[0x05] > 0x20) {
        syslog(LOG_ERR, "校验数据%u字节,期望%u字节",
            (unsigned int)buf[0x05], 0x1FU);
        return 0;
    }

    // 因为SMBIOS规格的错误，允许结构长度0x1F字节
    if (buf[0x05] < 0x1E
     || !checksum(buf, buf[0x05])
     || memcmp(buf + 0x10, "_DMI_", 5) != 0
     || !checksum(buf + 0x10, 0x0F)) {
        return 0;
    }

    u16 ver = (buf[0x06] << 8) + buf[0x07];
    u32 len = WORD(buf + 0x16);
    u16 num = WORD(buf + 0x1C);
    u8 *table = dmi_table_get(DWORD(buf+0x18), &len, num, devmem, flags);

    if (NULL == table) {
        return 1;
    }

    dmi_table_decode(table, len, num, ver, flags, jsonData);
    free(table);
    return 1;
}

cJSON *get_apply_data(void)
{
    if (1!=sizeof(u8) || 2!=sizeof(u16) || 4!=sizeof(u32) || 0!='\0') {
        syslog(LOG_ERR, "校验模块和系统不兼容");
        return NULL;
    }

    size_t size = 0x20;
    cJSON *jsonData = cJSON_CreateObject();
    u8 *buf = read_file(0, &size, SYS_ENTRY_FILE);

    if (NULL != buf) {
        if (size >= 24 && memcmp(buf, "_SM3_", 5) == 0) {
            smbios3_decode(buf, SYS_TABLE_FILE, FLAG_NO_FILE_OFFSET, jsonData);
        } else if (size >= 31 && memcmp(buf, "_SM_", 4) == 0) {
            smbios_decode(buf, SYS_TABLE_FILE, FLAG_NO_FILE_OFFSET, jsonData);
        } else {
            syslog(LOG_ERR, "系统数据解析异常");
            return NULL;
        }

        free(buf);
    }

    cJSON_AddStringToObject(jsonData, "version", VERSION);
    return jsonData;
}

const char *get_item_value(const cJSON *jsonData, const char *key)
{
    cJSON *item = cJSON_GetObjectItem(jsonData, key);
    if (NULL == item) {
        return "";
    }

    const char *value = cJSON_GetStringValue(item);
    if (NULL == value) {
        value = "";
    }

    return value;
}

