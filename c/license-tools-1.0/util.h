#ifndef LIC_UTIL_H
#define LIC_UTIL_H

#include "types.h"
#include <sys/types.h>

int checksum(const u8 *buf, size_t len);
void *read_file(off_t base, size_t *len, const char *filename);
void *mem_chunk(off_t base, size_t len, const char *devmem);
int write_file(const char *filename, u8 *data, size_t len);

#endif

