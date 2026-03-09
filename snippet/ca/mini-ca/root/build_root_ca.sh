#!/bin/sh

# 创建CA证书
# 创建必要的目录
export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/root
mkdir -p issued
touch index.txt
echo 01 > crlnumber.txt

# 创建CA证书密钥对
mkdir -p private
chmod 0700 private
openssl genpkey \
    -algorithm ED448 \
    -out private/root_keypair.pem

# 创建证书请求
openssl req \
    -config root.cnf \
    -new -key private/root_keypair.pem \
    -out root_csr.pem -text

# 自己签发证书
openssl ca \
    -config root.cnf \
    -extensions v3_root_cert \
    -selfsign \
    -in root_csr.pem \
    -out root_cert.pem

# 生成撤销证书
openssl ca -config root.cnf -gencrl -out root_crl.pem
openssl crl -in root_crl.pem -out root_crl.der -outform DER
openssl crl -in root_crl.der -inform DER -out root_crl.pem -text

