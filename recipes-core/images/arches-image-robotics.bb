SUMMARY = "Arches Robotics image"
DESCRIPTION = "Arches (NVIDIA Jetson) Robotics image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Robotics-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Robotics-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    opencv \
    python3-numpy \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    libiio \
    python3-can \
    can-utils \
    htop \
    tmux \
"
