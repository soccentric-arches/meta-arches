FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://arches-security.cfg"

KERNEL_CONFIG_FRAGMENTS:append = " ${WORKDIR}/arches-security.cfg ${ARCHES_KERNEL_CONFIG_FRAGMENTS}"