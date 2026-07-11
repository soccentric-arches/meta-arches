SUMMARY = "Arches Medical Devices image"
DESCRIPTION = "Arches (NVIDIA Jetson) Medical Devices image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Medical Devices-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Medical Devices-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    libiio \
    python3-pyserial \
    openssl \
    python3-cryptography \
    sqlite3 \
    python3-requests \
    chrony \
    htop \
"
