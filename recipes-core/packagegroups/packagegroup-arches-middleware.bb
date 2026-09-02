SUMMARY = "Arches middleware runtime"
DESCRIPTION = "IPC, RPC, serialization, TLS, and media middleware used by Arches applications"
LICENSE = "MIT"

# This group depends on packages whose RPM output names are dynamically
# renamed, so it cannot be shared as an allarch package.
PACKAGE_ARCH = "${MACHINE_ARCH}"

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