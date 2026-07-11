SUMMARY = "Arches Transportation / Rail image"
DESCRIPTION = "Arches (NVIDIA Jetson) Transportation / Rail image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Transportation / Rail-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Transportation / Rail-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    can-utils \
    python3-can \
    linuxptp \
    ptpd \
    chrony \
    mosquitto \
    python3-paho-mqtt \
    libmodbus \
    htop \
    tmux \
"
