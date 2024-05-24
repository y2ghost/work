echo 构建客户端私钥 private/client.key.pem
openssl genrsa -aes256 -out private/client.key.pem 4096
echo 生成客户端证书签发申请 private/client.csr
openssl req -new -key private/client.key.pem -out private/client.csr \
    -subj "/C=CN/ST=HN/L=changsha/O=IT/OU=dev/CN=ywork"
echo 签发客户端证书 certs/client.cer
openssl x509 -req -days 750 -sha256 -CA certs/ca.cer -CAkey private/ca.key.pem \
    -CAserial ca.srl -CAcreateserial -in private/client.csr -out certs/client.cer
echo 客户证书转换
openssl pkcs12 -export -inkey private/client.key.pem \
    -in certs/client.cer -out certs/client.p12
echo 查看密钥库信息
keytool -list -keystore certs/client.p12 -storetype pkcs12 -v

exit 0
