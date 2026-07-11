SUMMARY = "Arches Telecom image"
DESCRIPTION = "Arches (NVIDIA Jetson) Telecom image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Telecom-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Telecom-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    frr \
    openl2tp \
    ptpd \
    linuxptp \
    chrony \
    net-snmp \
    strongswan \
    tcpdump \
    iperf3 \
    socat \
    htop \
"
