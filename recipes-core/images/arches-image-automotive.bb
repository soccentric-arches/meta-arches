SUMMARY = "Arches Automotive & Vehicle image"
DESCRIPTION = "Arches (NVIDIA Jetson) Automotive & Vehicle image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Automotive & Vehicle-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Automotive & Vehicle-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Automotive & Vehicle-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " can-utils python3-can linuxptp ptpd gpsd v4l-utils fio stress-ng iperf3"
