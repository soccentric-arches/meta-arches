SUMMARY = "Arches Robotics & Autonomous Systems image"
DESCRIPTION = "Arches (NVIDIA Jetson) Robotics & Autonomous Systems image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Robotics & Autonomous Systems-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Robotics & Autonomous Systems-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Robotics & Autonomous Systems-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " can-utils python3-can opencv gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-libav v4l-utils fio stress-ng"
