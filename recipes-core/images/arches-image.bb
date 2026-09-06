DESCRIPTION = "Arches OS image for NVIDIA Jetson Orin Nano Super Developer Kit"
SUMMARY     = "Full-featured embedded Linux image with A/B OTA, gRPC, and hardware interface tools"
LICENSE     = "MIT"

require recipes-core/images/core-image-base.bb

# Optional product profiles are controlled from kas/common.yaml. The package
# groups keep image composition explicit and make SDK contents reproducible.
IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('ARCHES_MIDDLEWARE', '1', 'packagegroup-arches-middleware', '', d)} \
    ${@bb.utils.contains('ARCHES_CLOUD', '1', 'packagegroup-arches-cloud', '', d)} \
    ${@bb.utils.contains('ARCHES_DEBUG', '1', 'packagegroup-arches-debug', '', d)} \
    ${@bb.utils.contains('ARCHES_SDK_SUPPORT', '1', 'packagegroup-arches-sdk', '', d)} \
"

# On-device management and observability (Arches-Instrumentation): the istate
# hub and nine spokes, plus the ten *_ctrl operator tools. Gated on
# ARCHES_INSTRUMENTATION so a board profile can drop it, matching the other
# capability flags above.
#
# Everything it needs at runtime is in the base image: nftables,
# wireguard-tools, hostapd, linuxptp and gpsd (Task.md D-06/D-07/D-12), and
# /pst is partitioned, mounted and provisioned by arches-pst (D-03/D-04).
# Without those this installs and then fails to start.
IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('ARCHES_INSTRUMENTATION', '1', 'instrumentation', '', d)} \
"

# OTA via SWUpdate — A/B rootfs slot switching, web UI on :8080
IMAGE_INSTALL:append = " \
    swupdate \
    swupdate-www \
    arches-swupdate-config \
"

# Persistent /home partition init — creates /home/arches on first boot after
# the empty ext4 home partition (p5) is mounted over /home.
IMAGE_INSTALL:append = " arches-home-init"

# Persistent instrumentation state — mounts /pst (p4, label "pst") and creates
# its subdirectory tree with the ownership the instr-* daemons expect.
#
# This is not optional dressing: five instrumentation units declare
# ReadWritePaths under /pst with ProtectSystem=strict, and systemd refuses to
# start a unit whose ReadWritePaths entry does not exist. Without it
# instr-istate -- the hub every spoke depends on -- fails at boot.
# See Task.md D-03/D-04.
IMAGE_INSTALL:append = " arches-pst"

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

# Add the local service/operator user with sudo access. The account is locked by
# default so production images do not ship with a shared password. A deployment
# can provide ARCHES_USER_PASSWORD_HASH through its distro or machine config.
inherit extrausers

ARCHES_USER_PASSWORD_HASH ?= "!"
ARCHES_SSH_AUTHORIZED_KEYS ?= ""
ARCHES_SWUPDATE_PUBLIC_KEY ?= ""

python __anonymous() {
    if d.getVar("ARCHES_SECURE_BOOT") == "1":
        if not d.getVar("ARCHES_SWUPDATE_PUBLIC_KEY"):
            bb.fatal("ARCHES_SECURE_BOOT=1 requires ARCHES_SWUPDATE_PUBLIC_KEY")
        if not os.path.isfile(d.getVar("ARCHES_SWUPDATE_PUBLIC_KEY")):
            bb.fatal("SWUpdate public key does not exist: %s" % d.getVar("ARCHES_SWUPDATE_PUBLIC_KEY"))
}

EXTRA_USERS_PARAMS = "\
    useradd -m -s /bin/bash -G sudo arches; \
    usermod -p '${ARCHES_USER_PASSWORD_HASH}' arches; \
    usermod -L root; \
"

# Install deployment-provided operator keys without baking a shared credential
# into the image. Keys are newline-separated when supplied through machine or
# distro configuration.
arches_install_authorized_keys() {
    if [ -n "${ARCHES_SSH_AUTHORIZED_KEYS}" ]; then
        install -d -m 0700 ${IMAGE_ROOTFS}/home/arches/.ssh
        printf '%s\n' "${ARCHES_SSH_AUTHORIZED_KEYS}" > ${IMAGE_ROOTFS}/home/arches/.ssh/authorized_keys
        chmod 0600 ${IMAGE_ROOTFS}/home/arches/.ssh/authorized_keys
        chown -R arches:arches ${IMAGE_ROOTFS}/home/arches/.ssh
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "arches_install_authorized_keys;"

arches_install_swupdate_key() {
    if [ "${ARCHES_SECURE_BOOT}" = "1" ]; then
        install -d -m 0755 ${IMAGE_ROOTFS}/etc/swupdate
        install -m 0644 "${ARCHES_SWUPDATE_PUBLIC_KEY}" \
            ${IMAGE_ROOTFS}/etc/swupdate/swupdate-public.pem
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "arches_install_swupdate_key;"
