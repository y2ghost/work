许可功能文件清单概述
- 许可文件: /opt/license-tools/etc/license.key
- 许可公钥: /opt/license-tools/etc/public.key
- 校验模块: /opt/license-tools/lib/license-checker.so
- 申请文件: /opt/license-tools/output/apply.key
- 申请工具: /opt/license-tools/bin/generate-apply-file
- 查看许可: /opt/license-tools/bin/print-license-file

构建密钥对工具
- make gen_key

构建签名解密测试工具
- make sign_decrypt

构建申请码生成工具
- make generate-apply-file

构建许可授权查看工具
- make print-license-file

允许工具打开root权限的文件
- sudo chown root:root /opt/license-tools/bin/*
- sudo chmod u+s /opt/license-tools/bin/*

安装和卸载
- sudo make install
- sudo make uninstall

mbedtls编译说明
- 解压mbedtls-mbedtls-3.5.0源码
- 修改源码目录下library/Makefile
- 配置CFLAGS的值为CFLAGS ?= -O2 -fPIC
- 编译: make
- 测试: make check
- 安装: make install DESTDIR=/opt/mbedtls

打包源代码
- tar -zcvf license-tools-1.0.tar.gz license-tools-1.0

构建软件包
- rpmbuild -ba --nodebuginfo license-tools.spec

