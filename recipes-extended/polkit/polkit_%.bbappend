# polkit bbappend — Arches WebConfig polkit rule.
#
# The webconfigd daemon talks to systemd over D-Bus via polkit, so it ships a
# small rule granting the daemon exactly the limited set of D-Bus actions it
# needs. It is installed here (into the polkit package) rather than from
# webconfigd because the polkit rules directory `/usr/share/polkit-1/rules.d`
# is owned by polkit (and systemd) — shipping it from a second package would
# trip an RPM file conflict on that shared directory.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://50-webconfigd.polkit.rules"

do_install:append() {
    install -d ${D}${datadir}/polkit-1/rules.d
    install -m 0644 ${WORKDIR}/50-webconfigd.polkit.rules \
        ${D}${datadir}/polkit-1/rules.d/50-webconfigd.rules
}
