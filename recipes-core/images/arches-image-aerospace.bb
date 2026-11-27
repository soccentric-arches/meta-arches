SUMMARY = "Arches Aerospace & Avionics image"
DESCRIPTION = "Arches (NVIDIA Jetson) Aerospace & Avionics image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Aerospace & Avionics-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Aerospace & Avionics-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Aerospace & Avionics-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
	gpsd linuxptp ptpd can-utils python3-can \
	strongswan openvpn keepalived \
	v4l-utils opencv gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
	libiio fio stress-ng \
"
