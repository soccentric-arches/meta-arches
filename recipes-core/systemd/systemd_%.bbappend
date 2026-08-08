# systemd bbappend — normalize polkit rules.d permissions.
#
# Both systemd (systemd-networkd.rules) and polkit (50-default.rules,
# 50-webconfigd.rules) ship polkit rules in /usr/share/polkit-1/rules.d.
# systemd's do_install sets the directory to 700, while polkit's ends up 755 —
# the mismatched %attr in the two RPM specs trips a file conflict at rootfs
# time. Make systemd match polkit (755) so RPM allows the shared directory.

do_install:append() {
    if [ -d ${D}${datadir}/polkit-1/rules.d ]; then
        chmod 755 ${D}${datadir}/polkit-1/rules.d
    fi
}
