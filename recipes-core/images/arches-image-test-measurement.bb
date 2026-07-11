SUMMARY = "Arches Test & Measurement image"
DESCRIPTION = "Arches (NVIDIA Jetson) Test & Measurement image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Test & Measurement-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Test & Measurement-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    libiio \
    gpsd \
    python3-pyserial \
    iperf3 \
    fio \
    stress-ng \
    bonnie++ \
    iozone3 \
    netperf \
    htop \
    tmux \
    nano \
"
