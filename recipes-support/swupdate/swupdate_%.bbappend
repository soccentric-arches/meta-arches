FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Keep signature and hash verification available in the target SWUpdate
# binary. Secure production images additionally require a configured key and
# signed bundle through ARCHES_SECURE_BOOT.
SRC_URI:append = " file://arches-swupdate.cfg"