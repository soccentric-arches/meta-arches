SUMMARY = "Mount and provision /pst, the persistent instrumentation state partition"
DESCRIPTION = "Installs the pst.mount unit and the tmpfiles.d fragment that creates \
/pst's subdirectories with the ownership Arches-Instrumentation's daemons expect. \
Separate from the instrumentation recipe because the partition, its label and its \
mount are properties of the image, not of the suite."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://pst.mount \
           file://pst.conf"

S = "${WORKDIR}"

inherit systemd

# The mount unit is enabled, not merely installed: /pst must be there before
# instr-istate starts, and every spoke depends on istate.
SYSTEMD_SERVICE:${PN} = "pst.mount"
SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN} = "e2fsprogs"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/pst.mount ${D}${systemd_unitdir}/system/pst.mount

    install -d ${D}${nonarch_libdir}/tmpfiles.d
    install -m 0644 ${S}/pst.conf ${D}${nonarch_libdir}/tmpfiles.d/pst.conf
}

FILES:${PN} += "${systemd_unitdir}/system ${nonarch_libdir}/tmpfiles.d"
