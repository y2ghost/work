#ifndef LIC_TYPES_H
#define LIC_TYPES_H

#include "config.h"
#include <stddef.h>

typedef unsigned char u8;
typedef unsigned short u16;
typedef signed short i16;
typedef unsigned int u32;

// 调整类型定义，区分大小端和内存对齐方式
#ifdef BIGENDIAN
typedef struct {
    u32 h;
    u32 l;
} u64;
#else
typedef struct {
    u32 l;
    u32 h;
} u64;
#endif

typedef struct pk_buffer_t {
    u8* data;
    size_t len;
} pk_buffer_t;

typedef struct pk_license_t {
    char* data; // 授权数据
    char* sig;  // 签名数据
} pk_license_t;

#if defined(ALIGNMENT_WORKAROUND) || defined(BIGENDIAN)
static inline u64 U64(u32 low, u32 high)
{
    u64 self;
    self.l = low;
    self.h = high;
    return self;
}
#endif

// SMBIOS v2.8.0 以后结构都是小端序
#if defined(ALIGNMENT_WORKAROUND) || defined(BIGENDIAN)
#define WORD(x) (u16)((x)[0] + ((x)[1] << 8))
#define DWORD(x) (u32)((x)[0] + ((x)[1] << 8) + ((x)[2] << 16) + ((x)[3] << 24))
#define QWORD(x) (U64(DWORD(x), DWORD(x + 4)))
#else /* ALIGNMENT_WORKAROUND || BIGENDIAN */
#define WORD(x) (u16)(*(const u16 *)(x))
#define DWORD(x) (u32)(*(const u32 *)(x))
#define QWORD(x) (*(const u64 *)(x))
#endif /* ALIGNMENT_WORKAROUND || BIGENDIAN */

#endif

