DESCRIPTION = "Arches OS image for NVIDIA Jetson Orin Nano Super Developer Kit"
SUMMARY     = "Full-featured embedded Linux image with A/B OTA, gRPC, and hardware interface tools"
LICENSE     = "MIT"

require recipes-core/images/core-image-base.bb

# OTA via SWUpdate — A/B rootfs slot switching, web UI on :8080
IMAGE_INSTALL:append = " \
    swupdate \
    swupdate-www \
    arches-swupdate-config \
"

# Persistent /home partition init — creates /home/arches on first boot after
# the empty ext4 home partition (p4) is mounted over /home.
IMAGE_INSTALL:append = " arches-home-init"

# Connectivity — Ethernet DHCP, WiFi (requires M.2 Key E WiFi card)
IMAGE_INSTALL:append = " \
    arches-connectivity \
    wpa-supplicant \
    iw \
    rfkill \
    bluez5 \
    bluez5-dev \
"

# gRPC + Protocol Buffers (C++ runtime, compiler plugin, and Python bindings)
IMAGE_INSTALL:append = " \
    protobuf \
    protobuf-compiler \
    protobuf-lite \
    grpc \
    python3-grpcio \
    python3-protobuf \
    sudo \
"

# --- Hardware interface tools ------------------------------------------------

# I2C
IMAGE_INSTALL:append = " \
    i2c-tools \
    python3-smbus \
    python3-smbus2 \
"

# SPI
IMAGE_INSTALL:append = " \
    spitools \
    python3-spidev \
"

# GPIO
IMAGE_INSTALL:append = " \
    libgpiod \
    libgpiod-tools \
    python3-gpiod \
"

# UART / serial
IMAGE_INSTALL:append = " \
    picocom \
    minicom \
    python3-pyserial \
"

# USB
IMAGE_INSTALL:append = " \
    usbutils \
"

# PCI / NVMe / SCSI / storage (Jetson Orin Nano has PCIe M.2 NVMe slot)
IMAGE_INSTALL:append = " \
    pciutils \
    nvme-cli \
    sg3-utils \
    hdparm \
    mtd-utils \
"

# CAN bus
IMAGE_INSTALL:append = " \
    can-utils \
"

# System / hardware inspection
IMAGE_INSTALL:append = " \
    lshw \
    usbutils \
    ethtool \
    devmem2 \
    cpupower \
    watchdog \
    sysstat \
    iotop \
    strace \
    v4l-utils \
    evtest \
    python3-periphery \
"

# Add local user 'arches' with password 'arches' and sudo access
inherit extrausers

EXTRA_USERS_PARAMS = "\
    useradd -m -s /bin/bash -G sudo arches; \
    usermod -p '\$6\$S4A8.AjcC/35XUuw\$RmUXpzhNIoZFmLYCwk7tJT1Lonm2bqf/xalVqQD54i2mKwIB.wf9i1tCJsY1ND8gCBvyhiF6oi2jZBAOIV7wD/' arches; \
"
