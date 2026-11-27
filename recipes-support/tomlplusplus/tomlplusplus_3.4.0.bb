SUMMARY = "Header-only C++ TOML parser and serialiser, conforming to TOML v1.0"
DESCRIPTION = "toml++ is a header-only TOML library for C++17 or later."
HOMEPAGE = "https://github.com/marzer/tomlplusplus"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90960f22c10049c117d56ed2ee5ee167"

# Issue #49: SRCREV is a commit SHA pinned to a specific snapshot of
# upstream; PV is hardcoded to 3.4.0 because the recipe file is named
# tomlplusplus_3.4.0.bb (PN_3.4.0.bb is the Yocto convention for
# pinning an out-of-tree version).  The two values are deliberately
# decoupled — if upstream's v3.4.0 tag ever moves, update SRCREV
# here.  Do NOT switch SRCREV to ${AUTOREV} — that breaks
# reproducibility (issue #23/#24 — see the kas-repos pinning work).
SRCREV = "30172438cee64926dc41fdd9c11fb3ba5b2ba9de"
SRC_URI = "git://github.com/marzer/tomlplusplus.git;protocol=https;nobranch=1"

# 3.4.0 + git-stamp is the Yocto convention for a pinned snapshot.
# The recipe is named _3.4.0.bb so PV lives in the filename; PV
# itself stays at 3.4.0 (no +git suffix) to match the recipe name.
PV = "3.4.0"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DTOMLPLUSPLUS_BUILD_TESTS=OFF -DTOMLPLUSPLUS_BUILD_EXAMPLES=OFF"

# toml++ is header-only; no runtime library is produced, so ${PN}
# packages nothing and only ${PN}-dev is non-empty.  ALLOW_EMPTY on
# PN keeps the recipe parseable when nothing is installed at all.
ALLOW_EMPTY:${PN} = "1"
FILES:${PN}     = ""
FILES:${PN}-dev = "${includedir} ${libdir}/cmake ${datadir}/cmake ${libdir}/pkgconfig ${datadir}/${BPN}"
