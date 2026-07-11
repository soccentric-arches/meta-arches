SUMMARY = "Arches Test & Measurement image"
DESCRIPTION = "Arches (NVIDIA Jetson) Test & Measurement image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Test & Measurement-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Test & Measurement-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Test & Measurement-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " gpsd iperf3 netperf fio stress-ng bonnie++ iozone3 v4l-utils can-utils python3-can"
