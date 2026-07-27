SUMMARY = "Arches time-sync default service state (Clock/NTP/PTP/GPS pages)"
DESCRIPTION = "WebConfig's Time & Sync pages (Clock, NTP, PTP, GPS/GNSS) are \
mutually exclusive and switched at runtime with 'systemctl restart/stop' \
only — the daemon never calls enable/disable, so the *default* systemd \
enablement decides what runs before the daemon has applied any saved \
config. Without this preset, chrony/linuxptp/gpsd's own upstream recipes \
could each default to enabled and race systemd-timesyncd for UDP/123 and \
the PPS/GPS device. Default to plain NTP via systemd-timesyncd (matches \
SystemConfig.time's documented default) and leave PTP/GPS/chrony disabled \
until an operator enables one from the UI."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://85-arches-timesync.preset"

S = "${WORKDIR}"

RDEPENDS:${PN} = "chrony linuxptp gpsd systemd"

do_install() {
    install -d ${D}${systemd_unitdir}/system-preset
    install -m 0644 ${WORKDIR}/85-arches-timesync.preset \
        ${D}${systemd_unitdir}/system-preset/85-arches-timesync.preset
}

FILES:${PN} += "${systemd_unitdir}/system-preset/"
