SUMMARY = "Arches Oil & Gas image"
DESCRIPTION = "Arches (NVIDIA Jetson) Oil & Gas image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Oil & Gas-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Oil & Gas-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    mosquitto \
    python3-paho-mqtt \
    libmodbus \
    python3-pymodbus \
    open62541 \
    gpsd \
    chrony \
    strongswan \
    openssl \
    htop \
"
