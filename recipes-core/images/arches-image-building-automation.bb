SUMMARY = "Arches Building Automation image"
DESCRIPTION = "Arches (NVIDIA Jetson) Building Automation image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Building Automation-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Building Automation-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    mosquitto \
    python3-paho-mqtt \
    libcoap \
    open62541 \
    python3-requests \
    sqlite3 \
    htop \
"
