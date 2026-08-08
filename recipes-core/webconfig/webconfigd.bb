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
"
# SHA-256 of the source + vendored tarballs (Arches-Configuration
# dist/CHECKSUMS.sha256, produced by tools/release-artifacts.sh). Regenerate
# both at every Arches-Configuration release cut — valid only for the exact
# tarball contents they were computed from.
SRC_URI[src.sha256sum] = "3664216eb92e65e9cec37ebe0aacc30d3a0bb0982012fa046769e37f560856ef"
SRC_URI[vendor.sha256sum] = "b5ea86f4ab0b261cf58d8b54c4f016b8e37f68fc7e456921b3db3667bd41a185"

PV = "0.1.0"

S = "${WORKDIR}/webconfigd-${PV}"

inherit cargo_bin cargo-update-recipe-crates pkgconfig systemd useradd

# --- Build configuration (release profile is in Arches-Configuration's Cargo.toml) --
# cargo_bin (meta-rust-bin) builds out-of-tree into ${B} and installs from
# ${CARGO_BINDIR}. It provides the prebuilt rust/cargo 1.85.1 toolchain
# (PREFERRED_VERSION in kas/common.yaml) that the vendored crates need —
# poky's rust-native 1.75.0 cannot parse edition2024 manifests.

# Offline build: use the committed vendor/ tarball, never the network.
CARGO_VENDORING_DIRECTORY = "${S}/vendor"
export CARGO_NET_OFFLINE = "1"

# cargo_bin does not generate a cargo_home/config the way poky's cargo.bbclass
# does, so point cargo at the vendored crates ourselves. Overwrite (not append)
# so a stale config left by a previous cargo.bbclass build can't cause a
# duplicate [source.crates-io] key. cargo_bin sets linkers/build-dir via env
# vars, so the vendoring section is all this config needs.
do_configure:append() {
    cat > "${CARGO_HOME}/config" << EOF

[source.crates-io]
replace-with = "vendored-sources"

[source.vendored-sources]
directory = "${CARGO_VENDORING_DIRECTORY}"
EOF
}

# Cargo.toml sits at the root of the S tarball, so CARGO_SRC_DIR (a subdir
# *relative to* S) stays at its default "" — do not set it to ${S} itself,
# that doubles up into ${S}/${S}/Cargo.toml via CARGO_MANIFEST_PATH.

# --- Runtime user (non-root) -------------------------------------------------
WEBCONFIG_USER = "webconfig"
WEBCONFIG_GROUP = "webconfig"
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-r -M -d /var/lib/webconfig -s /usr/sbin/nologin -g ${WEBCONFIG_GROUP} ${WEBCONFIG_USER}"
GROUPADD_PARAM:${PN} = "-r ${WEBCONFIG_GROUP}"

# --- Installed files ---------------------------------------------------------
do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${CARGO_BINDIR}/webconfigd ${D}${bindir}/webconfigd

    # Data partition owned by the runtime user (config.json, users.json, certs).
    install -d -o ${WEBCONFIG_USER} -g ${WEBCONFIG_GROUP} ${D}/var/lib/webconfig

    # Hardened systemd unit + environment.
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/webconfigd.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/webconfigd.env ${D}${sysconfdir}/default/webconfigd
}

SYSTEMD_SERVICE:${PN} = "webconfigd.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# Pull in CA certs for TLS verification against upstream (OTA) servers, and
# the privileged-op path (D-Bus Manager unit ops + polkit authorization).
RDEPENDS:${PN} = "ca-certificates polkit"
# systemd is the supervisor (D-Bus actions via polkit).
RRECOMMENDS:${PN} = "systemd polkit"

# The release profile (opt-level=z, lto, strip) ships an already-stripped
# binary — skip the already-stripped QA check.
INSANE_SKIP:${PN} += "already-stripped"

# SPDX/SBOM: emit the cargo dependency manifest as part of the recipe metadata
# (supply-chain concern; new deps require approval upstream in Arches-Configuration).
INHERIT += "create-spdx"
