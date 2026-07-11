SUMMARY = "Arches Industrial Automation image"
DESCRIPTION = "Arches (NVIDIA Jetson) Industrial Automation image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Industrial Automation-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Industrial Automation-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    libmodbus \
    python3-pymodbus \
    can-utils \
    python3-can \
    mosquitto \
    python3-paho-mqtt \
    open62541 \
    libiio \
    htop \
"
