SUMMARY = "Arches Energy & Smart Grid image"
DESCRIPTION = "Arches (NVIDIA Jetson) Energy & Smart Grid image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Energy & Smart Grid-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Energy & Smart Grid-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Energy & Smart Grid-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " libmodbus python3-pymodbus open62541 mosquitto paho-mqtt-c python3-paho-mqtt libcoap linuxptp ptpd gpsd"
