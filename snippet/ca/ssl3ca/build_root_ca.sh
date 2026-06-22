# 创建证书、索引、私钥目录
mkdir certs db private
chmod 700 private
touch db/index
openssl rand -hex 16  > db/serial
echo 1001 > db/crlnumber

# 创建根证书的私钥、证书签名请求(CSR)
openssl req -new \
    -config root-ca.conf \
    -out root-ca.csr \
    -keyout private/root-ca.key

# 根证书自签名
openssl ca -selfsign \
    -config root-ca.conf \
    -in root-ca.csr \
    -out root-ca.crt \
    -extensions ca_ext

# 生成吊销文件列表
openssl ca -gencrl \
    -config root-ca.conf \
    -out root-ca.crl

# 签发子证书
# openssl ca \
#     -config root-ca.conf \
#     -in sub-ca.csr \
#     -out sub-ca.crt \
#     -extensions sub_ca_ext

# 撤销证书
# openssl ca \
#     -config root-ca.conf \
#     -revoke certs/1002.pem \
#     -crl_reason keyCompromise

