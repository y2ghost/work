#!/bin/sh

# 创建中间证书
# 用来签发服务端、客户端证书
export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/intermediate
mkdir -p issued
touch index.txt
echo 01 > crlnumber.txt

# 创建密钥对
mkdir -p private
chmod 0700 private

openssl genpkey \
    -algorithm ED448 \
    -out private/intermediate_keypair.pem

# 创建证书请求
openssl req \
    -config intermediate.cnf \
    -new -key private/intermediate_keypair.pem \
    -out intermediate_csr.pem -text

# 签发证书
openssl ca \
    -config $CA_HOME/root/root.cnf \
    -extensions v3_intermediate_cert \
    -in  intermediate_csr.pem \
    -out intermediate_cert.pem

# 签发撤销证书
openssl ca \
    -config intermediate.cnf \
    -gencrl \
    -out intermediate_crl.pem

# CRL PEM转换DER
openssl crl \
    -in intermediate_crl.pem \
    -out intermediate_crl.der \
    -outform DER

# CRL DER转换PEM
openssl crl \
    -in intermediate_crl.der \
    -inform DER \
    -out intermediate_crl.pem \
    -text

# 查看撤销证书
openssl crl \
    -in intermediate_crl.pem \
    -noout -text

