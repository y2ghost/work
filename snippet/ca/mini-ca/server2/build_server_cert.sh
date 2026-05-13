#!/bin/sh

export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/server2
mkdir -p private
chmod 0700 private

# 生成密钥对
openssl genpkey \
    -algorithm ED448 \
    -out private/server2_keypair.pem

# 生成证书请求
openssl req \
    -config server2.cnf \
    -new -key private/server2_keypair.pem \
    -out server2_csr.pem -text

# 签发证书
openssl ca \
    -config $CA_HOME/intermediate/intermediate.cnf \
    -in  server2_csr.pem \
    -out server2_cert.pem

# 撤销证书
openssl ca \
    -config $CA_HOME/intermediate/intermediate.cnf \
    -revoke server2_cert.pem \
    -crl_reason keyCompromise

