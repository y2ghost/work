# 使用示例: ./generate_link_params.sh /files/test.mp4 1800


SECRET_KEY="dev_secret_$(date +%Y%m)"
FILE_PATH="$1"
EXPIRE_SECONDS="${2:-3600}"
EXPIRE_TIME=$(date -d "+${EXPIRE_SECONDS} seconds" +%s)

SIGNATURE=$(echo -n "${EXPIRE_TIME}${FILE_PATH} ${SECRET_KEY}" |
    openssl md5 -binary |
    openssl base64 |
    tr +/ -_ |
    tr -d =)

echo "http://localhost${FILE_PATH}?s=${SIGNATURE}&ts=${EXPIRE_TIME}"
exit 0

