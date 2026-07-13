SUMMARY = "Arches Transportation & Rail image"
DESCRIPTION = "Arches (NVIDIA Jetson) Transportation & Rail image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Transportation & Rail-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Transportation & Rail-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Transportation & Rail-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
	can-utils python3-can linuxptp ptpd gpsd \
	mosquitto paho-mqtt-c python3-paho-mqtt \
	libmodbus python3-pymodbus open62541 \
	strongswan openvpn v4l-utils opencv \
"
