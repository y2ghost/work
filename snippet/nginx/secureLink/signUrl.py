import hashlib
import time
import base64


BASE_URL = "https://example.com"


#
# nginx的MD5值生成逻辑
# echo -n '2147483647/s/link127.0.0.1 secret' | \
#   openssl md5 -binary | openssl base64 | tr +/ -_ | tr -d =
def generateSecureLink(secretKey, uriPath, expireSeconds=3600):
    expireTime = int(time.time()) + expireSeconds
    signInfo = f"{secretKey}{uriPath}{expireTime}"
    signatureHash = hashlib.md5(signInfo.encode('utf-8')).digest()
    signature = base64.urlsafe_b64encode(signatureHash).rstrip(b'=').decode('utf-8')
    return f"?s={signature}&ts={expireTime}"


def generateMd5Url(uriPath, secretKey, expireSeconds=1800, clientIP=None):
    expireTime = int(time.time()) + expireSeconds
    if clientIP:
        signInfo = f"{expireTime}{uriPath}{clientIP} {secretKey}"
    else:
        signInfo = f"{expireTime}{uriPath} {secretKey}"

    signature = hashlib.md5(signInfo.encode('utf-8')).hexdigest()
    signedUrl = f"{BASE_URL}{uriPath}?s={signature}&ts={expireTime}"
    return signedUrl


SECRET = "my_test_key_1q2w3e"
URL_PATH = "/downloads/test.mp4"
EXPIRE_SECONDS = 600


if '__main__' == __name__:
    params = generateSecureLink(SECRET, URL_PATH, expireSeconds=EXPIRE_SECONDS)
    print(f"generateSecureLink {BASE_URL}{URL_PATH}{params}")
    signedUrl = generateMd5Url(URL_PATH, SECRET, expireSeconds=EXPIRE_SECONDS)
    print(f"generateMd5Url     {signedUrl}")

