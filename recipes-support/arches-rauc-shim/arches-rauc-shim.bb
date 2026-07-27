SUMMARY = "RAUC-CLI-compatible shim backed by the real SWUpdate A/B OTA scheme"
DESCRIPTION = "Arches-Configuration's webconfigd shells out to a literal \
`rauc` binary for Updates-page slot status/install/confirm \
(crates/webconfigd/src/update_backend.rs, crates/webconfig-engine/src/ \
update.rs::RaucUpdateBackend) and silently falls back to a mock backend with \
fabricated slot data if rauc is not on PATH. This image's real OTA transport \
is SWUpdate (arches-swupdate-config), not RAUC, and no meta-rauc layer is \
included in this build. This package installs /usr/bin/rauc implementing \
just the four subcommands the daemon actually calls, backed by the real \
SWUpdate/extlinux slot state, so the Updates page reflects reality."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://rauc"

S = "${WORKDIR}"

# util-linux (findmnt), cpio (bundle inspection), swupdate (real install),
# coreutils/sed/bash for the shim itself.
RDEPENDS:${PN} = "bash util-linux cpio swupdate arches-swupdate-config"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/rauc ${D}${bindir}/rauc
}

FILES:${PN} = "${bindir}/rauc"
