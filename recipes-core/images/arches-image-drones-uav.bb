SUMMARY = "Arches Drones / UAV image"
DESCRIPTION = "Arches (NVIDIA Jetson) Drones / UAV image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Drones / UAV-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Drones / UAV-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    gpsd \
    linuxptp \
    can-utils \
    python3-can \
    opencv \
    python3-numpy \
    mosquitto \
    python3-paho-mqtt \
    libiio \
    htop \
    tmux \
"
