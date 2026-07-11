SUMMARY = "Arches Energy / Smart Grid image"
DESCRIPTION = "Arches (NVIDIA Jetson) Energy / Smart Grid image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Energy / Smart Grid-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Energy / Smart Grid-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    libmodbus \
    python3-pymodbus \
    open62541 \
    mosquitto \
    python3-paho-mqtt \
    chrony \
    linuxptp \
    openssl \
    gnupg \
    htop \
"
