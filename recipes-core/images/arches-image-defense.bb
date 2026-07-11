SUMMARY = "Arches Defense image"
DESCRIPTION = "Arches (NVIDIA Jetson) Defense image — arches-image base (A/B OTA via SWUpdate, gRPC/Protobuf, hardware interface tools, arches user) plus Defense-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image rather than a bare core image, so
# every domain variant is a full image (OTA, connectivity, gRPC, hw tools).
require recipes-core/images/arches-image.bb

# --- Defense-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
    strongswan \
    openvpn \
    openssl \
    gnupg \
    nmap \
    tcpdump \
    rng-tools \
    python3-cryptography \
    htop \
    tmux \
"
