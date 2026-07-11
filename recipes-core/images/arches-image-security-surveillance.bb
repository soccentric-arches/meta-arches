SUMMARY = "Arches Security & Surveillance image"
DESCRIPTION = "Arches (NVIDIA Jetson) Security & Surveillance image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Security & Surveillance-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Security & Surveillance-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    ffmpeg \
    opencv \
    python3-numpy \
    sqlite3 \
    htop \
    tmux \
"
