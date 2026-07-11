SUMMARY = "Arches Industrial Automation & PLC image"
DESCRIPTION = "Arches (NVIDIA Jetson) Industrial Automation & PLC image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Industrial Automation & PLC-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Industrial Automation & PLC-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Industrial Automation & PLC-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " can-utils python3-can mosquitto paho-mqtt-c python3-paho-mqtt libmodbus python3-pymodbus open62541 libcoap"
