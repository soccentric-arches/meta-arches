SUMMARY = "Arches Drones & UAV image"
DESCRIPTION = "Arches (NVIDIA Jetson) Drones & UAV image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Drones & UAV-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Drones & UAV-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Drones & UAV-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " gpsd linuxptp ptpd can-utils python3-can opencv gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-libav v4l-utils mosquitto paho-mqtt-c python3-paho-mqtt fio stress-ng"
