SUMMARY = "Arches Networking & Routers image"
DESCRIPTION = "Arches (NVIDIA Jetson) Networking & Routers image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Networking & Routers-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Networking & Routers-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    frr \
    openl2tp \
    dnsmasq \
    hostapd \
    iptables \
    iproute2 \
    bridge-utils \
    vlan \
    tcpdump \
    nmap \
    strongswan \
    openvpn \
    socat \
    htop \
"
