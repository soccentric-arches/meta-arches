FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# The fragment is intentionally limited to generic kernel hardening and
# tracing capabilities. NVIDIA secure-boot signing remains a board release
# step and is enabled only when ARCHES_SECURE_BOOT is set by the deployment.
SRC_URI:append = " file://arches-security.cfg"
KERNEL_CONFIG_FRAGMENTS:append = " ${WORKDIR}/arches-security.cfg ${ARCHES_KERNEL_CONFIG_FRAGMENTS}"