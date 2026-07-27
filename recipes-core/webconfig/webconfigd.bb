# webconfigd — on-device WebConfig daemon recipe (Arches-Configuration spec §2/§5).
#
# Ported into meta-arches directly (this project's own product layer) rather
# than kept as a standalone "meta-webconfig" layer pulled in via kas: the
# daemon/frontend/schemas/manifest are Arches-only, and meta-arches is already
# the one product layer this image build includes, so there is no reason to
# carry a second layer just for these four recipes. The Rust source, vendored
# crates, frontend, schemas, and board manifest are still owned by
# Arches-Configuration (SoCcentric global rule: application code lives in its
# own repo); only the Yocto packaging lives here, fed by the release tarballs
# `make release-artifacts` (Arches-Configuration/tools/release-artifacts.sh)
# produces into that repo's dist/ directory. Re-run that script and copy the
# two tarballs + refresh the checksums below whenever Arches-Configuration
# cuts a new webconfigd release.
#
# Builds the Rust workspace with the exact release profile (opt-level=z, lto,
# codegen-units=1, panic=abort, stripped) committed in Arches-Configuration's
# Cargo.toml, using the vendored crate tarball so the build is fully offline
# and reproducible. No network access at build time. cargo.bbclass comes from
# oe-core (scarthgap+); no separate rust/meta-rust layer is required.
#
# The daemon runs as a non-root user with a hardened systemd unit (see
# webconfigd.service) and talks to systemd over D-Bus via polkit (see
# 50-webconfigd.polkit.rules) — polkit's "polkit" DISTRO_FEATURES gate and its
# rules.d JS engine are enabled in meta-arches's own conf/layer.conf.

SUMMARY = "SoCcentric on-device web configuration daemon"
DESCRIPTION = "Browser-based device configuration UI for embedded Linux; one Rust daemon, TLS, no database."
HOMEPAGE = "https://soccentric.example/webconfig"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3382002c026d2dcb5817068a8f184313"

SRC_URI = " \
    file://webconfigd-${PV}.tar.gz;unpack=1;name=src \
    file://vendor.tar.gz;unpack=1;subdir=webconfigd-${PV};name=vendor \
    file://webconfigd.service \
    file://webconfigd.env \
    file://50-webconfigd.polkit.rules \
"
# SHA-256 of the source + vendored tarballs (Arches-Configuration
# dist/CHECKSUMS.sha256, produced by tools/release-artifacts.sh). Regenerate
# both at every Arches-Configuration release cut — valid only for the exact
# tarball contents they were computed from.
SRC_URI[src.sha256sum] = "92fb5966b71ef40989ead01fdd7f0d81993c056c72e9f68e377df5a0e98041d2"
SRC_URI[vendor.sha256sum] = "32045b440cc598ca862c198fd2aac8d15b3018dd20ab2d59cd3a077f375a56dc"

PV = "0.1.0"

S = "${WORKDIR}/webconfigd-${PV}"

inherit cargo cargo-update-recipe-crates pkgconfig systemd useradd

# --- Build configuration (release profile is in Arches-Configuration's Cargo.toml) --
CARGO_BUILD_FLAGS = "--release"

# Offline build: use the committed vendor/ tarball, never the network.
CARGO_VENDORING_DIR = "${S}/vendor"
export CARGO_NET_OFFLINE = "1"

# Target the image's target triple (cross-compile handled by the toolchain).
CARGO_SRC_DIR = "${S}"

# --- Runtime user (non-root) -------------------------------------------------
WEBCONFIG_USER = "webconfig"
WEBCONFIG_GROUP = "webconfig"
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-r -M -d /var/lib/webconfig -s /usr/sbin/nologin ${WEBCONFIG_USER}"
GROUPADD_PARAM:${PN} = "-r ${WEBCONFIG_GROUP}"

# --- Installed files ---------------------------------------------------------
do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/release/webconfigd ${D}${bindir}/webconfigd

    # Data partition owned by the runtime user (config.json, users.json, certs).
    install -d -o ${WEBCONFIG_USER} -g ${WEBCONFIG_GROUP} ${D}/var/lib/webconfig

    # Hardened systemd unit + environment.
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/webconfigd.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/webconfigd.env ${D}${sysconfdir}/default/webconfigd

    # polkit rule granting the daemon the limited D-Bus actions it needs.
    install -d ${D}${datadir}/polkit-1/rules.d
    install -m 0644 ${WORKDIR}/50-webconfigd.polkit.rules \
        ${D}${datadir}/polkit-1/rules.d/50-webconfigd.rules
}

SYSTEMD_SERVICE:${PN} = "webconfigd.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# Pull in CA certs for TLS verification against upstream (OTA) servers, and
# the privileged-op path (D-Bus Manager unit ops + polkit authorization).
RDEPENDS:${PN} = "ca-certificates polkit"
# systemd is the supervisor (D-Bus actions via polkit).
RRECOMMENDS:${PN} = "systemd polkit"

# SPDX/SBOM: emit the cargo dependency manifest as part of the recipe metadata
# (supply-chain concern; new deps require approval upstream in Arches-Configuration).
INHERIT += "create-spdx"
