python -m grpc_tools.protoc \
    --proto_path=. ./grpcserver.proto \
    --python_out=. \
    --grpc_python_out=.

