SUMMARY = "Arches Security & Surveillance image"
DESCRIPTION = "Arches (NVIDIA Jetson) Security & Surveillance image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Security & Surveillance-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Security & Surveillance-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Security & Surveillance-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly gstreamer1.0-libav gstreamer1.0-rtsp-server ffmpeg opencv v4l-utils sox alsa-utils pulseaudio mosquitto paho-mqtt-c python3-paho-mqtt"
