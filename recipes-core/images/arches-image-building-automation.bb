SUMMARY = "Arches Building Automation & HVAC image"
DESCRIPTION = "Arches (NVIDIA Jetson) Building Automation & HVAC image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Building Automation & HVAC-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Building Automation & HVAC-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Building Automation & HVAC-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
	mosquitto paho-mqtt-c python3-paho-mqtt \
	libcoap open62541 libmodbus python3-pymodbus \
	python3-flask lighttpd bluez5 wpa-supplicant \
	can-utils python3-can linuxptp \
"
