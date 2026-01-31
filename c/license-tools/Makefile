CC     ?= gcc
CFLAGS ?= -O2
# 启用调试时禁用-O2选项，启用-g选项
# CFLAGS ?= -g

CFLAGS += -W -Wall -Wshadow -Wstrict-prototypes -Wpointer-arith -Wcast-qual \
          -Wcast-align -Wwrite-strings -Wmissing-prototypes -Winline -Wundef

# lseek和mmap支持64位偏移值
CFLAGS += -D_FILE_OFFSET_BITS=64
CFLAGS += -fPIC

# CFLAGS += -DBIGENDIAN
# CFLAGS += -DALIGNMENT_WORKAROUND

MBEDTLSDIR ?= /opt/mbedtls
LDFLAGS ?=
DESTDIR =
prefix  = /opt/license-tools
bindir = $(prefix)/bin
etcdir = $(prefix)/etc
libdir = $(prefix)/lib
outdir = $(prefix)/output

INSTALL         := install
INSTALL_DATA    := $(INSTALL) -m 644
INSTALL_DIR     := $(INSTALL) -m 755 -d
INSTALL_PROGRAM := $(INSTALL) -m 755
RM              := rm -f

CRYPTO_LIB := $(MBEDTLSDIR)/lib/libmbedcrypto.a
PROGRAMS := generate-apply-file import-license-file print-license-file

all : $(PROGRAMS) license-tools.so

# 编译程序
gen_key : gen_key.c types.h util.o
	$(CC) $(CFLAGS) $(LDFLAGS) util.o -I $(MBEDTLSDIR)/include $< $(CRYPTO_LIB) -o $@

sign_decrypt : sign_decrypt.c dmi.o util.o cjson.o pk.o
	$(CC) $(CFLAGS) $(LDFLAGS) dmi.o util.o cjson.o pk.o -I $(MBEDTLSDIR)/include $< $(CRYPTO_LIB) -o $@

generate-apply-file : generate-apply-file.c dmi.o util.o cjson.o pk.o
	$(CC) $(CFLAGS) $(LDFLAGS) dmi.o util.o cjson.o pk.o -I $(MBEDTLSDIR)/include $< $(CRYPTO_LIB) -o $@

import-license-file : import-license-file.c dmi.o util.o cjson.o pk.o
	$(CC) $(CFLAGS) $(LDFLAGS) dmi.o util.o cjson.o pk.o -I $(MBEDTLSDIR)/include $< $(CRYPTO_LIB) -o $@

print-license-file : print-license-file.c dmi.o util.o cjson.o pk.o
	$(CC) $(CFLAGS) $(LDFLAGS) dmi.o util.o cjson.o pk.o -I $(MBEDTLSDIR)/include $< $(CRYPTO_LIB) -o $@

dyload : dyload.c
	$(CC) $(CFLAGS) $(LDFLAGS) $< -o $@ -ldl

# 编译对象
util.o : util.c types.h util.h config.h
	$(CC) $(CFLAGS) -c $< -o $@

cjson.o : cjson.c cjson.h
	$(CC) $(CFLAGS) -c $< -o $@

pk.o : pk.c pk.h
	$(CC) $(CFLAGS) -I $(MBEDTLSDIR)/include -L$(MBEDTLSDIR)/lib -lmbedcrypto -c $< -o $@

dmi.o : dmi.c dmi.h types.h
	$(CC) $(CFLAGS) -c $< -o $@

# 编译动态库
license-tools.so: util.o cjson.o pk.o dmi.o $(CRYPTO_LIB)
	$(CC) $(CFLAGS) -shared -Wl,-soname,license-tools.so.1 -o $@ $^

# 命令相关
strip : $(PROGRAMS) license-tools.so
	strip $(PROGRAMS) license-tools.so

install : install-bin

uninstall : uninstall-bin

install-bin : $(PROGRAMS) license-tools.so
	$(INSTALL_DIR) $(DESTDIR)$(bindir)
	$(INSTALL_DIR) $(DESTDIR)$(etcdir)
	$(INSTALL_DIR) $(DESTDIR)$(libdir)
	$(INSTALL_DIR) $(DESTDIR)$(outdir)
	for program in $(PROGRAMS) ; do \
	$(INSTALL_PROGRAM) $$program $(DESTDIR)$(bindir) ; done
	$(INSTALL_DATA) license-tools.so $(DESTDIR)$(libdir)
	$(INSTALL_DATA) public.key $(DESTDIR)$(etcdir)

uninstall-bin :
	rm -rf $(DESTDIR)$(prefix)/*

clean :
	$(RM) *.o $(PROGRAMS) core license-tools.so *.pem dyload sign_decrypt gen_key

