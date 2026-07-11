SUMMARY = "Arches Retail / POS image"
DESCRIPTION = "Arches (NVIDIA Jetson) Retail / POS image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Retail / POS-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Retail / POS-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    openssl \
    python3-cryptography \
    sqlite3 \
    python3-requests \
    python3-flask \
    dnsmasq \
    chrony \
    htop \
"
