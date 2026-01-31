#!/bin/sh

export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/ocsp
mkdir -p private
chmod 0700 private

# 生成密钥对
openssl genpkey \
    -algorithm ED448 \
    -out private/ocsp_keypair.pem

# 生成证书请求
openssl req \
    -config ocsp.cnf \
    -new -key private/ocsp_keypair.pem \
    -out ocsp_csr.pem \
    -text

# 签发证书
openssl ca \
    -config $CA_HOME/intermediate/intermediate.cnf \
    -extensions v3_ocsp_cert \
    -in  ocsp_csr.pem \
    -out ocsp_cert.pem

