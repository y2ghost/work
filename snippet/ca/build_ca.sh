rm -rf certs private
echo 构建已发行证书存放目录 certs
mkdir certs
echo 构建私钥存放目录 private
mkdir private
echo 构建跟证书私钥 private/ca.key.pem
openssl genrsa -aes256 -out private/ca.key.pem 4096
echo 生成根证书签发申请 private/ca.csr
openssl req -new -key private/ca.key.pem -out private/ca.csr \
    -subj "/C=CN/ST=HN/L=changsha/O=IT/OU=dev/CN=*.study.ywork"
echo 签发根证书 certs/ca.cer
openssl x509 -req -days 750 -sha256 -key private/ca.key.pem \
    -in private/ca.csr -out certs/ca.cer
echo 根证书转换
openssl pkcs12 -export -inkey private/ca.key.pem \
    -in certs/ca.cer -out certs/ca.p12
echo 查看密钥库信息
keytool -list -keystore certs/ca.p12 -storetype pkcs12 -v

exit 0

