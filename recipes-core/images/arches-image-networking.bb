SUMMARY = "Arches Networking & Routers image"
DESCRIPTION = "Arches (NVIDIA Jetson) Networking & Routers image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Networking & Routers-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Networking & Routers-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Networking & Routers-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
	frr openl2tp hostapd dnsmasq radvd miniupnpd keepalived \
	lighttpd rpcbind nfs-utils cifs-utils wireguard-tools \
	strongswan openvpn ptpd linuxptp net-snmp bridge-utils vlan bind \
	mosquitto paho-mqtt-c python3-paho-mqtt can-utils \
"
