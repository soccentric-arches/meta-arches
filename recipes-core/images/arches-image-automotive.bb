SUMMARY = "Arches Automotive image"
DESCRIPTION = "Arches (NVIDIA Jetson) Automotive image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Automotive-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Automotive-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    can-utils \
    python3-can \
    iproute2 \
    chrony \
    linuxptp \
    socat \
    nmap \
    htop \
"
