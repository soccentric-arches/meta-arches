# webconfig-static — frontend assets served by webconfigd.
#
# Vanilla JS, native ES modules, no build step, no node at build time. The
# frontend is a directory tree (core/, components/, pages/, styles/ + index.html
# + main.css) wired with real import/export and CSS @import — browsers resolve
# the module graph straight from static files, so the *whole tree* must be
# installed, not a handful of monolithic files. Total <500 KB.
#
# Single source of truth: the frontend lives at the Arches-Configuration repo
# root in `frontend/` and is packaged inside the same `webconfigd-${PV}.tar.gz`
# source tarball webconfigd.bb fetches. We unpack that tarball and install its
# `frontend/` subtree recursively into /usr/share/webconfig/static, which is
# the path the daemon serves (spec: resolve_static_dir → /usr/share/webconfig/static).

SUMMARY = "SoCcentric WebConfig frontend static assets"
DESCRIPTION = "Vanilla-JS, no-build frontend for the on-device WebConfig UI."
LICENSE = "CLOSED"

PV = "0.1.0"

# Same source tarball as webconfigd.bb (the whole repo, incl. frontend/).
SRC_URI = "file://webconfigd-${PV}.tar.gz;unpack=1"
SRC_URI[sha256sum] = "3664216eb92e65e9cec37ebe0aacc30d3a0bb0982012fa046769e37f560856ef"

S = "${WORKDIR}/webconfigd-${PV}"

# Install-only recipe — skip the default base_do_configure, which would run
# `make clean` from the source tarball's Makefile (that target calls
# `cargo clean`, and cargo isn't in PATH here).
do_configure[noexec] = "1"

STATIC_DIR = "${D}/usr/share/webconfig/static"

do_install() {
    install -d ${STATIC_DIR}
    # Copy the frontend tree preserving subdirectory structure. `cp -a` keeps
    # the module/CSS import graph intact; --no-preserve=ownership avoids
    # leaking the build host's uid/gid into the image.
    cp -a --no-preserve=ownership ${S}/frontend/. ${STATIC_DIR}/

    # Fail loud if the tarball did not actually contain the frontend tree — a
    # silent skip would ship a daemon with no UI.
    test -f ${STATIC_DIR}/index.html || bbfatal "webconfig-static: frontend/index.html missing from source tarball"
    test -f ${STATIC_DIR}/main.css   || bbfatal "webconfig-static: frontend/main.css missing from source tarball"
    test -f ${STATIC_DIR}/core/app.js || bbfatal "webconfig-static: frontend/core/app.js missing from source tarball"

    # Spec budget: total payload <500 KB. Warn (not fail) so a slightly-over
    # image still builds but the size regression is visible in the build log.
    size=$(du -sk ${STATIC_DIR} | cut -f1)
    if [ "$size" -gt 512 ]; then
        bbnote "webconfig-static: frontend is ${size} KB (>500 KB spec budget) — trim assets"
    fi
}

FILES:${PN} = "/usr/share/webconfig/static"
