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

# The IV&V product itself: the HAL and the validation client that drives it.
# Gated on ARCHES_IVV so a board profile can build a plain platform image.
#
# Arches-Server is deliberately absent and that is not an omission: it runs on
# an x86 host, not the Jetson, and is the peer this client streams results TO.
# Packaging it for the device image would be wrong, not merely unnecessary.
# See Task.md D-10/D-11.
IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('ARCHES_IVV', '1', 'arches-hal arches-client', '', d)} \
"

# OTA via SWUpdate — A/B rootfs slot switching, web UI on :8080
IMAGE_INSTALL:append = " \
    swupdate \
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

# WebConfig (on-device configuration UI) + every system service/tool it
# manages or shells out to (polkit, WireGuard, AP-mode hostapd/dnsmasq, nft
# firewall, time-sync backends, mmc-utils for eMMC wear, RAUC-CLI shim over
# the real SWUpdate A/B scheme). Placed here (not only in
# arches-image-common.inc) so it ships on the plain "arches-image" build
# target too, not just the domain-specific variants. See
# Arches-Configuration/Feature.md for the full page-by-page inventory this
# backs, and packagegroup-arches-webconfig.bb for the package list itself.
IMAGE_INSTALL:append = " packagegroup-arches-webconfig"

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

# ---------------------------------------------------------------------------
# Image self-check (Task.md D-16)
#
# Every failure in Task.md's D group was of the same shape: something the
# image was supposed to contain was simply absent, and nothing noticed until a
# board was flashed. A unit whose ReadWritePaths target does not exist does not
# fail at build time -- it fails at boot, on hardware, in a way that reads like
# a runtime bug.
#
# So assert the composition here, where it is cheap. Each check below
# corresponds to a defect that actually shipped:
#   D-02  the wrong board's devices.yaml installed on every Jetson
#   D-03  no /pst partition, so instr-istate could not start
#   D-04  /pst never mounted or provisioned
#   D-05  a unit naming /mnt/boot, which exists on no image
#   D-08  the suite in no image at all
# ---------------------------------------------------------------------------
arches_check_image_composition() {
    fail=0
    _bad() { bbwarn "image check: $1"; fail=1; }

    if [ "${ARCHES_INSTRUMENTATION}" = "1" ]; then
        # The ten units must be installed AND enabled. Installed-but-disabled
        # is the quiet failure: everything looks present and nothing runs.
        for u in istate networkd monitord hardware faults alarms logmanager \
                 telemetry ota backup; do
            unit="${IMAGE_ROOTFS}${systemd_unitdir}/system/instr-$u.service"
            [ -f "$unit" ] || _bad "instr-$u.service is not installed"
            [ -L "${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/instr-$u.service" ] \
                || _bad "instr-$u.service is installed but not enabled"
        done

        # D-05: every path a unit declares writable must exist in the rootfs or
        # be a mount point, unless it is '-'-prefixed (optional). This is the
        # check that would have caught /mnt/boot.
        for unit in ${IMAGE_ROOTFS}${systemd_unitdir}/system/instr-*.service; do
            [ -f "$unit" ] || continue
            for p in $(sed -n 's/^ReadWritePaths=//p' "$unit"); do
                case "$p" in
                    -*) continue ;;                      # optional by design
                    /pst*|/run/*|/var/*) continue ;;     # mount points / tmpfiles
                esac
                [ -e "${IMAGE_ROOTFS}$p" ] \
                    || _bad "$(basename $unit) declares ReadWritePaths=$p, which is not in the rootfs"
            done
        done

        # D-03/D-04: /pst has to be mounted, not merely partitioned.
        [ -f "${IMAGE_ROOTFS}${systemd_unitdir}/system/pst.mount" ] \
            || _bad "pst.mount is missing: /pst will never be mounted"
        [ -f "${IMAGE_ROOTFS}${nonarch_libdir}/tmpfiles.d/pst.conf" ] \
            || _bad "pst.conf tmpfiles fragment is missing: /pst subdirectories will not exist"

        # D-02: the installed descriptors must be this MACHINE's, not another
        # board's. Guards the x86-64-on-a-Jetson failure permanently.
        dev="${IMAGE_ROOTFS}${sysconfdir}/instrumentation/devices.yaml"
        if [ -f "$dev" ]; then
            case "${MACHINE}" in
                *orin*|p3768-*|p3767-*|p3509-*|p3737-*|p3701-*)
                    grep -qi "x86" "$dev" && _bad "MACHINE=${MACHINE} but devices.yaml looks like the x86-64 descriptor set (Task.md D-02)"
                    ;;
            esac
        else
            _bad "instrumentation is installed but /etc/instrumentation/devices.yaml is missing"
        fi
    fi

    if [ "${ARCHES_IVV}" = "1" ]; then
        [ -f "${IMAGE_ROOTFS}${systemd_unitdir}/system/arches-client.service" ] \
            || _bad "arches-client.service is not installed"
    fi

    if [ "$fail" = "1" ]; then
        bbfatal "image composition check failed -- see the warnings above"
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "arches_check_image_composition;"
