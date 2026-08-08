# webconfig-manifest — the Arches board manifest.
#
# Installs the canonical JSON board manifest at
# /usr/share/webconfig/board-manifest.json — the daemon's loader order reads
# this path first. This repo/image is Arches-only, so there is exactly one
# manifest file in the common case.
#
# Single source of truth (same pattern as webconfig-static.bb): board-manifests/
# lives at the Arches-Configuration repo root and is packaged inside the same
# source tarball webconfigd.bb fetches — never copied/duplicated into this
# layer's files/, so there is no risk of a stale duplicate drifting from the
# data the engine and its tests actually load.

SUMMARY = "SoCcentric WebConfig — Arches board manifest"
LICENSE = "CLOSED"

PV = "0.1.0"

# Same source tarball as webconfigd.bb / webconfig-static.bb (the whole repo,
# including board-manifests/).
SRC_URI = "file://webconfigd-${PV}.tar.gz;unpack=1"
SRC_URI[sha256sum] = "3664216eb92e65e9cec37ebe0aacc30d3a0bb0982012fa046769e37f560856ef"

S = "${WORKDIR}/webconfigd-${PV}"

# Install-only recipe — skip the default base_do_configure, which would run
# `make clean` from the source tarball's Makefile (that target calls
# `cargo clean`, and cargo isn't in PATH here).
do_configure[noexec] = "1"

# The real manifest file. Override per-MACHINE only if a future board
# revision needs a distinct manifest file — the real Jetson MACHINE this
# image targets (Arches-OS default board) is bound below.
WEBCONFIG_BOARD_FILE ?= "arches.json"
WEBCONFIG_BOARD_FILE:jetson-orin-nano = "arches.json"

do_install() {
    install -d ${D}${datadir}/webconfig
    install -m 0644 ${S}/board-manifests/${WEBCONFIG_BOARD_FILE} \
        ${D}${datadir}/webconfig/board-manifest.json

    # Fail loud (not a silent substitution) if the resolved manifest file is
    # missing from the source tarball.
    test -f ${D}${datadir}/webconfig/board-manifest.json || \
        bbfatal "webconfig-manifest: board-manifests/${WEBCONFIG_BOARD_FILE} missing from source tarball"
}

FILES:${PN} = "${datadir}/webconfig/board-manifest.json"
