SUMMARY = "Arches Telecom & Communications image"
DESCRIPTION = "Arches (NVIDIA Jetson) Telecom & Communications image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Telecom & Communications-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Telecom & Communications-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Telecom & Communications-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " frr openl2tp ptpd linuxptp net-snmp strongswan openvpn keepalived miniupnpd radvd lighttpd"
