# 注意common name不用特别处理
FILENAME=client
openssl genrsa -out $FILENAME.key 2048
openssl req -new -key $FILENAME.key -x509 -days 3653 -out $FILENAME.crt
cat $FILENAME.key $FILENAME.crt >$FILENAME.pem
chmod 600 $FILENAME.key $FILENAME.pem

