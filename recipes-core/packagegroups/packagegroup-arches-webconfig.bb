SUMMARY = "Arches on-device WebConfig UI"
DESCRIPTION = "The webconfigd daemon, its static frontend, schema files, and \
board manifest, plus the system services WebConfig manages or shells out to \
(polkit authorization, WireGuard, AP-mode hostapd/dnsmasq, time-sync/PTP/GPS, \
CAN, and the RAUC-CLI-compatible A/B slot status/confirm shim). See \
Arches-Configuration/Feature.md for the full page-by-page feature inventory \
this package group exists to back."
LICENSE = "MIT"

inherit packagegroup

# Depends on packages whose RPM output names are dynamically renamed /
# machine-specific, so it cannot be shared as an allarch package.
PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS:${PN} = " \
    webconfigd \
    webconfig-static \
    webconfig-schemas \
    webconfig-manifest \
    arches-platform-status \
    arches-rauc-shim \
    arches-timesync \
    polkit \
    wireguard-tools \
    hostapd \
    dnsmasq \
    iw \
    wireless-regdb-static \
    nftables \
    mmc-utils \
    can-utils \
    linuxptp \
    gpsd \
    gps-utils \
    chrony \
"
