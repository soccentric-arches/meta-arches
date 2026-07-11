SUMMARY = "Arches Agriculture / Smart Farming image"
DESCRIPTION = "Arches (NVIDIA Jetson) Agriculture / Smart Farming image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Agriculture / Smart Farming-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Agriculture / Smart Farming-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    mosquitto \
    python3-paho-mqtt \
    gpsd \
    libiio \
    libmodbus \
    python3-pymodbus \
    python3-requests \
    sqlite3 \
    htop \
"
