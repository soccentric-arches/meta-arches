SUMMARY = "Arches Consumer Electronics image"
DESCRIPTION = "Arches (NVIDIA Jetson) Consumer Electronics image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Consumer Electronics-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Consumer Electronics-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-libav \
    alsa-utils \
    pulseaudio \
    ffmpeg \
    htop \
"
