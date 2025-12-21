#!/bin/sh

export CA_HOME=$PWD
$CA_HOME/root/build_root_ca.sh
$CA_HOME/intermediate/build_intermediate_ca.sh
$CA_HOME/server/build_server_cert.sh
$CA_HOME/server2/build_server_cert.sh
$CA_HOME/client/build_client_cert.sh
$CA_HOME/ocsp/build_ocsp_cert.sh

exit 0

