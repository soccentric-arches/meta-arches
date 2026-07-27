FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# The security fragment is intentionally limited to generic kernel hardening
# and tracing capabilities. NVIDIA secure-boot signing remains a board
# release step and is enabled only when ARCHES_SECURE_BOOT is set by the
# deployment.
#
# The connectivity fragment adds the kernel-side support WebConfig's
# Security/Hardware pages need but the stock Tegra defconfig does not
# guarantee: WireGuard, USB CDC-ACM (STM32 companion MCU), PPS (GPS), CAN-FD.
SRC_URI:append = " file://arches-security.cfg file://arches-connectivity.cfg"
KERNEL_CONFIG_FRAGMENTS:append = " ${WORKDIR}/arches-security.cfg ${WORKDIR}/arches-connectivity.cfg ${ARCHES_KERNEL_CONFIG_FRAGMENTS}"