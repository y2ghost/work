#!/bin/sh

export CA_HOME=${CA_HOME:=".."}
cd $CA_HOME/ocsp

openssl ocsp \
    -port 4480 \
    -index $CA_HOME/intermediate/index.txt \
    -CA $CA_HOME/intermediate/intermediate_cert.pem \
    -rkey private/ocsp_keypair.pem \
    -rsigner ocsp_cert.pem

