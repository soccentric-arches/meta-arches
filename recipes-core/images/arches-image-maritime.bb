SUMMARY = "Arches Maritime & Marine Electronics image"
DESCRIPTION = "Arches (NVIDIA Jetson) Maritime & Marine Electronics image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Maritime & Marine Electronics-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Maritime & Marine Electronics-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Maritime & Marine Electronics-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " gpsd linuxptp ptpd mosquitto paho-mqtt-c python3-paho-mqtt can-utils python3-can strongswan openvpn libcoap"
