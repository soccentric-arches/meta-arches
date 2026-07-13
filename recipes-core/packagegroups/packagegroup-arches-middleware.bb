SUMMARY = "Arches middleware runtime"
DESCRIPTION = "IPC, RPC, serialization, TLS, and media middleware used by Arches applications"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    protobuf \
    protobuf-compiler \
    protobuf-lite \
    grpc \
    python3-grpcio \
    python3-protobuf \
    libgpiod \
    libgpiod-tools \
    openssl \
    curl \
"