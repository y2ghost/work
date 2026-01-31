echo 构建服务器私钥 private/server.key.pem
openssl genrsa -aes256 -out private/server.key.pem 4096
echo 生成服务器证书签发申请 private/server.csr
openssl req -new -key private/server.key.pem -out private/server.csr \
    -subj "/C=CN/ST=HN/L=changsha/O=IT/OU=dev/CN=www.study.ywork"
echo 签发服务器证书 certs/server.cer
openssl x509 -req -days 750 -sha256 -CA certs/ca.cer -CAkey private/ca.key.pem \
    -CAserial ca.srl -CAcreateserial -in private/server.csr -out certs/server.cer
echo 服务证书转换
openssl pkcs12 -export -inkey private/server.key.pem \
    -in certs/server.cer -out certs/server.p12
echo 查看密钥库信息
keytool -list -keystore certs/server.p12 -storetype pkcs12 -v

exit 0
