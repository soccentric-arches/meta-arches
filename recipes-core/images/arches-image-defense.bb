SUMMARY = "Arches Defense & Secure Systems image"
DESCRIPTION = "Arches (NVIDIA Jetson) Defense & Secure Systems image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Defense & Secure Systems-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Defense & Secure Systems-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Defense & Secure Systems-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " strongswan openvpn wireguard-tools keepalived gpsd linuxptp ptpd can-utils python3-can"
