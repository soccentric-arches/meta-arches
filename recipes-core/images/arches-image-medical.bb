SUMMARY = "Arches Medical Devices & Healthcare image"
DESCRIPTION = "Arches (NVIDIA Jetson) Medical Devices & Healthcare image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Medical Devices & Healthcare-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Medical Devices & Healthcare-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Medical Devices & Healthcare-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " gpsd linuxptp ptpd python3-flask strongswan openvpn open62541 mosquitto paho-mqtt-c python3-paho-mqtt libmodbus python3-pymodbus libcoap"
