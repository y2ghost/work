SECRET="my_test_key_1q2w3e"
URL_PATH="/downloads/test.mp4"
EXPIRE_TIME=1783588021

echo -n "${SECRET}${URL_PATH}${EXPIRE_TIME}" | \
    openssl md5 -binary | openssl base64 | tr +/ -_ | tr -d =

