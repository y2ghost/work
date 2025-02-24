# 子证书的私钥和证书签名请求
openssl req -new \
    -config sub-ca.conf \
    -out sub-ca.csr \
    -keyout private/sub-ca.key

# 生成子证书签名
openssl ca \
    -config root-ca.conf \
    -in sub-ca.csr \
    -out sub-ca.crt \
    -extensions sub_ca_ext

# 签名服务端证书
# openssl ca \
#     -config sub-ca.conf \
#     -in server.csr \
#     -out server.crt \
#     -extensions server_ext

# 签名客户端证书
# openssl ca \
#     -config sub-ca.conf \
#     -in client.csr \
#     -out client.crt \
#     -extensions client_ext
