#!/bin/sh

export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/client
mkdir -p private
chmod 0700 private

# 生成密钥对
openssl genpkey \
    -algorithm ED448 \
    -out private/client_keypair.pem

# 生成证书请求
openssl req \
    -config client.cnf \
    -new -key private/client_keypair.pem \
    -out client_csr.pem -text

# 签发证书
openssl ca \
    -config $CA_HOME/intermediate/intermediate.cnf \
    -policy policy_client_cert \
    -extensions v3_client_cert \
    -in  client_csr.pem \
    -out client_cert.pem

# 打包证书示例
cat $CA_HOME/intermediate/intermediate_cert.pem \
    $CA_HOME/root/root_cert.pem \
    > certfile.pem

openssl pkcs12 -export \
    -inkey private/client_keypair.pem \
    -in client_cert.pem \
    -certfile certfile.pem \
    -passout 'pass:SuperPa$$w0rd' \
    -out client_cert.p12 

