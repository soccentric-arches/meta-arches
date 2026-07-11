SUMMARY = "Arches Consumer Electronics & Multimedia image"
DESCRIPTION = "Arches (NVIDIA Jetson) Consumer Electronics & Multimedia image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Consumer Electronics & Multimedia-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Consumer Electronics & Multimedia-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Consumer Electronics & Multimedia-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly gstreamer1.0-libav gstreamer1.0-rtsp-server alsa-utils alsa-plugins pulseaudio pipewire ffmpeg v4l-utils sox lame libvpx libtheora gst-examples opencv"
