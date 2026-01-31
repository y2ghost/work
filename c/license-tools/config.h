#ifndef LIC_CONFIG_H
#define LIC_CONFIG_H

#define USE_MMAP 1
#define _GNU_SOURCE

#ifdef __aarch64__
#define USE_SLOW_MEMCPY
#endif

#endif

