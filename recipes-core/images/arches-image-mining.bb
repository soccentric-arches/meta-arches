SUMMARY = "Arches Mining image"
DESCRIPTION = "Arches (NVIDIA Jetson) Mining image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Mining-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Mining-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    mosquitto \
    python3-paho-mqtt \
    libmodbus \
    can-utils \
    gpsd \
    iperf3 \
    fio \
    stress-ng \
    htop \
    tmux \
"
