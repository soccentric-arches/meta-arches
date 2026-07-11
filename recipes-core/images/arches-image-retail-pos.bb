SUMMARY = "Arches Retail & Point of Sale image"
DESCRIPTION = "Arches (NVIDIA Jetson) Retail & Point of Sale image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Retail & Point of Sale-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Retail & Point of Sale-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Retail & Point of Sale-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " python3-flask lighttpd dnsmasq open62541 libmodbus python3-pymodbus mosquitto paho-mqtt-c python3-paho-mqtt"
