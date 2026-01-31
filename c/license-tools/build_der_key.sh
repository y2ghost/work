#!/bin/sh

openssl pkcs8 -topk8 -inform PEM -outform DER \
    -in private.key -out private.der -nocrypt
exit 0

