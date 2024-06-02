#!/bin/sh

export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/server
mkdir -p private
chmod 0700 private

# 生成密钥对
openssl genpkey \
    -algorithm ED448 \
    -out private/server_keypair.pem

# 生成证书请求
openssl req \
    -config server.cnf \
    -new \
    -key private/server_keypair.pem \
    -out server_csr.pem \
    -text

# 签发证书
openssl ca \
    -config $CA_HOME/intermediate/intermediate.cnf \
    -in  server_csr.pem \
    -out server_cert.pem

