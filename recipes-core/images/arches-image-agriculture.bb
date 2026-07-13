SUMMARY = "Arches Agriculture & Smart Farming image"
DESCRIPTION = "Arches (NVIDIA Jetson) Agriculture & Smart Farming image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Agriculture & Smart Farming-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Agriculture & Smart Farming-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Agriculture & Smart Farming-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
	mosquitto paho-mqtt-c python3-paho-mqtt \
	gpsd libmodbus python3-pymodbus open62541 libcoap \
	python3-flask v4l-utils can-utils python3-can \
	linuxptp opencv gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
	libiio \
"
