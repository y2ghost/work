#ifndef LIC_PK_H
#define LIC_PK_H

#include "types.h"

#define KEY_SIZE            (4096)
#define MAX_LICENSE_SIZE    (8096)
#define PUBLIC_KEY  ("/opt/license-tools/etc/public.key")
#define LICENSE_KEY ("/opt/license-tools/etc/license.key")

// 输入数据，输出base64加密的字符串
pk_buffer_t base64_encode(const pk_buffer_t src);
// 输入加密字符串，输出二进制数据
pk_buffer_t base64_decode(const pk_buffer_t src);
// 验证授权数据，返回值说明: 0(有效), -1(无效), -2(过期)
int verify(pk_license_t license);
// RSA公钥加密数据
pk_buffer_t pk_encrypt(pk_buffer_t src);
// 加载授权文件数据
pk_license_t load_license_file(void);
pk_license_t load_license_file2(const char *filename);

#endif

