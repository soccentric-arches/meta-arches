SUMMARY = "Arches Aerospace image"
DESCRIPTION = "Arches (NVIDIA Jetson) Aerospace image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Aerospace-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Aerospace-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    gpsd \
    linuxptp \
    ptpd \
    can-utils \
    python3-can \
    socat \
    strongswan \
    htop \
    tmux \
"
