# arches-image-common.inc — shared "completeness" layer for arches-image-<domain>
# variants. Each domain recipe does:
#
#     require recipes-core/images/arches-image.bb
#     require recipes-core/images/arches-image-common.inc
#     IMAGE_INSTALL:append = " <domain-specific packages>"
#
# This file is required AFTER arches-image.bb, so the appends below layer on
# top of the full Arches base (A/B OTA via SWUpdate, arches-connectivity,
# gRPC/Protobuf, hardware interface tools, arches user). It makes every
# domain variant a complete, self-contained image: SSH server, debug tools,
# a full command-line + build environment, monitoring/diagnostics, network
# utilities, and a broad Python toolchain. Domain recipes then add only the
# packages specific to their vertical.
#
# Every package and packagegroup below was verified to exist as an exact
# recipe (or as a standard sub-package / IMAGE_FEATURES) in the included
# Yocto layers (poky/meta, meta-oe, meta-python, meta-networking,
# meta-multimedia, meta-tegra, meta-swupdate), so the images parse and the
# packages resolve at do_rootfs. LICENSE_FLAGS_ACCEPTED += "commercial" in
# kas/common.yaml covers any codec packages pulled in by domain variants.
#
# No networkmanager/connman here — the base uses systemd-networkd via
# arches-connectivity and a second active network manager would conflict.

# --- Production runtime foundation ------------------------------------------
# These packages provide the expected recovery, storage, identity, time,
# logging, and service-management primitives on every domain image.
IMAGE_INSTALL:append = " \
    dbus libpam shadow shadow-base \
    kmod udev util-linux \
    e2fsprogs e2fsprogs-resize2fs dosfstools parted \
    tzdata \
    systemd-analyze \
    logrotate rsyslog \
"

# SSH server + debug tools on every variant (the base image does not enable
# an SSH server; this is the one base-level feature the domain variants add).
IMAGE_FEATURES:append = " ssh-server-openssh tools-debug ptest-pkgs"

# Export development, source, profiling, and validation support to both SDK
# variants while retaining the same target package composition as the image.
SDKIMAGE_FEATURES:append = " dev-pkgs dbg-pkgs src-pkgs"
TOOLCHAIN_TARGET_TASK:append = " packagegroup-arches-sdk"

# --- Full command-line + native build environment ----------------------------
IMAGE_INSTALL:append = " \
    packagegroup-core-full-cmdline \
    packagegroup-core-buildessential \
    git make cmake pkgconfig autoconf automake libtool flex bison \
    gcc g++ gdb binutils diffutils patch cpio bc file gawk \
    bzip2 xz gzip tar unzip zip lz4 zstd \
"

# --- Python toolchain (interpreter + widely-used modules) --------------------
IMAGE_INSTALL:append = " \
    python3 python3-pip python3-setuptools python3-wheel \
    python3-numpy \
    python3-jsonschema python3-dateutil python3-six python3-pytz \
    python3-psutil python3-dbus python3-pyserial \
    python3-periphery python3-pyusb python3-smbus2 python3-spidev \
    python3-evdev python3-aiohttp \
    python3-pillow python3-matplotlib python3-sqlalchemy \
"

# --- Local data / serialization ----------------------------------------------
IMAGE_INSTALL:append = " \
    sqlite3 leveldb json-c jansson \
"

# --- Network client utilities + diagnostics (no second manager) -------------
IMAGE_INSTALL:append = " \
    curl wget rsync lsof socat netcat-openbsd \
    iproute2 iptables tcpdump nmap ethtool traceroute mtr ipcalc \
    net-tools iputils \
"

# --- Time sync, certs, crypto, entropy ---------------------------------------
IMAGE_INSTALL:append = " \
    chrony ca-certificates openssl gnupg gnutls rng-tools haveged \
"

# --- System monitoring, editors, shells -------------------------------------
IMAGE_INSTALL:append = " \
    htop tmux screen nano vim mc procps sysstat iotop \
    usbutils pciutils nvme-cli smartmontools \
"

# --- Profiling / tracing / debug helpers -------------------------------------
IMAGE_INSTALL:append = " \
    valgrind ltrace perf trace-cmd elfutils \
    logrotate rsyslog audit \
"

# --- Hardware I/O libraries (Python bindings above; C libs here) -------------
IMAGE_INSTALL:append = " \
    libgpiod i2c-tools spitools libiio \
"