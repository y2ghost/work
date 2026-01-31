#!/bin/sh

export CA_HOME=${CA_HOME:=".."}

openssl ocsp \
    -url http://localhost:4480 \
    -sha256 \
    -CAfile $CA_HOME/root/root_cert.pem \
    -issuer $CA_HOME/intermediate/intermediate_cert.pem \
    -cert $CA_HOME/server2/server2_cert.pem

