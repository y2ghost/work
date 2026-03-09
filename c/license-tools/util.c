#include "config.h"
#include "types.h"
#include "util.h"
#include <sys/types.h>
#include <sys/stat.h>

#ifdef USE_MMAP
#include <sys/mman.h>
#ifndef MAP_FAILED
#define MAP_FAILED ((void *) -1)
#endif /* !MAP_FAILED */
#endif /* USE MMAP */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>
#include <syslog.h>

static int myread(int fd, u8 *buf, size_t count, const char *prefix)
{
    ssize_t r = 1;
    size_t r2 = 0;

    while (r2 != count && r != 0) {
        r = read(fd, buf + r2, count - r2);
        if (r == -1) {
            if (errno != EINTR) {
                syslog(LOG_ERR, "%s: 文件读取异常", prefix);
                return -1;
            }
        } else {
            r2 += r;
        }
    }

    if (r2 != count) {
        syslog(LOG_ERR, "%s: 文件不正常的结束", prefix);
        return -1;
    }

    return 0;
}

int checksum(const u8 *buf, size_t len)
{
    u8 sum = 0;
    for (size_t a = 0; a < len; a++) {
        sum += buf[a];
    }

    return (sum == 0);
}

/*
 * 从指定文件偏移处读取所有的文件数据，直到读取max_len字节
 * 返回分配的缓存，同时更新max_len为实际读取的字节数
 * 如果返回null表示出错
 */
void *read_file(off_t base, size_t *max_len, const char *filename)
{
    struct stat statbuf;
    int fd;
    u8 *p;

    // 文件不打印错误信息，可能只是暂时不存在
    if ((fd = open(filename, O_RDONLY)) == -1) {
        syslog(LOG_ERR, "%s: 打开文件异常: %s", filename, strerror(errno));
        return NULL;
    }

    if (fstat(fd, &statbuf) == 0) {
        if (base >= statbuf.st_size) {
            syslog(LOG_ERR, "%s: 不能读取EOF之后的数据", filename);
            p = NULL;
            goto out;
        }

        if (*max_len > (size_t)statbuf.st_size - base) {
            *max_len = statbuf.st_size - base;
        }
    }

    if ((p = malloc(*max_len)) == NULL) {
        syslog(LOG_ERR, "分配缓存失败");
        goto out;
    }

    if (lseek(fd, base, SEEK_SET) == -1) {
        syslog(LOG_ERR, "%s: 定位文件偏移失败", filename);
        goto err_free;
    }

    if (myread(fd, p, *max_len, filename) == 0) {
        goto out;
    }

err_free:
    free(p);
    p = NULL;

out:
    if (close(fd) == -1) {
        syslog(LOG_ERR, "%s: 关闭文件失败", filename);
    }

    return p;
}

#ifdef USE_MMAP
static void safe_memcpy(void *dest, const void *src, size_t n)
{
#ifdef USE_SLOW_MEMCPY
    for (size_t i = 0; i < n; i++) {
        *((u8 *)dest + i) = *((const u8 *)src + i);
    }
#else
    memcpy(dest, src, n);
#endif /* USE_SLOW_MEMCPY */
}
#endif /* USE_MMAP */

// 内存拷贝，返回分配的缓存
void *mem_chunk(off_t base, size_t len, const char *devmem)
{
    struct stat statbuf;
    void *p = NULL;
    int fd;
#ifdef USE_MMAP
    off_t mmoffset;
    void *mmp;
#endif

    // devmem预期为字符设备文件
    if ((fd = open(devmem, O_RDONLY)) == -1
     || fstat(fd, &statbuf) == -1
     || (geteuid() == 0 && !S_ISCHR(statbuf.st_mode))) {
        syslog(LOG_ERR, "%s: 无法读取文件内容", devmem);
        if (fd == -1) {
            return NULL;
        }

        goto out;
    }

    if ((p = malloc(len)) == NULL) {
        syslog(LOG_ERR, "内存分配失败");
        goto out;
    }

#ifdef USE_MMAP
    if (S_ISREG(statbuf.st_mode) && base + (off_t)len > statbuf.st_size) {
        syslog(LOG_ERR, "%s: 文件不正常的映射", devmem);
        goto err_free;
    }

#ifdef _SC_PAGESIZE
    mmoffset = base % sysconf(_SC_PAGESIZE);
#else
    mmoffset = base % getpagesize();
#endif /* _SC_PAGESIZE */
    // 使用mmap避免workaround问题
    mmp = mmap(NULL, mmoffset + len, PROT_READ, MAP_SHARED, fd, base - mmoffset);
    if (mmp == MAP_FAILED) {
        goto try_read;
    }

    safe_memcpy(p, (u8 *)mmp + mmoffset, len);
    if (munmap(mmp, mmoffset + len) == -1) {
        syslog(LOG_ERR, "%s: 解除内存映射失败", devmem);
    }

    goto out;

try_read:
#endif /* USE_MMAP */
    if (lseek(fd, base, SEEK_SET) == -1) {
        syslog(LOG_ERR, "%s: 定位文件偏移失败", devmem);
        goto err_free;
    }

    if (myread(fd, p, len, devmem) == 0) {
        goto out;
    }

err_free:
    free(p);
    p = NULL;

out:
    if (close(fd) == -1) {
        syslog(LOG_ERR, "%s: 关闭文件失败", devmem);
    }

    return p;
}

int write_file(const char *filename, u8 *data, size_t len)
{
    FILE *fp = fopen(filename, "wb");
    if (NULL == fp) {
        syslog(LOG_ERR, "%s: 打开文件异常", filename);
        return -1;
    }

    fwrite(data, 1, len, fp);
    fclose(fp);
    return 0;
}

