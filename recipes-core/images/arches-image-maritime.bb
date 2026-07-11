SUMMARY = "Arches Maritime image"
DESCRIPTION = "Arches (NVIDIA Jetson) Maritime image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Maritime-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Maritime-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    gpsd \
    linuxptp \
    ptpd \
    mosquitto \
    python3-paho-mqtt \
    libiio \
    can-utils \
    strongswan \
    socat \
    htop \
"
