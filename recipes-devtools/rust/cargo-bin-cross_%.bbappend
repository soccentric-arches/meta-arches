# cargo-bin-cross installs cargo to /usr/, which collides with cargo-native
# (pulled in by librsvg's standard cargo.bbclass) in the eSDK sysroot —
# breaking `make esdk-image`. Relocate the whole install to a subdirectory so
# the two toolchains don't collide; webconfigd.bb adds the relocated bin dir
# to PATH (see do_compile:prepend there).
CARGO_BIN_PREFIX = "${prefix}/cargo-bin-cross"

fakeroot do_install() {
    ${S}/install.sh --destdir="${D}" --prefix="${CARGO_BIN_PREFIX}"
    rm -f ${D}${CARGO_BIN_PREFIX}/lib/rustlib/uninstall.sh
    rm -f ${D}${CARGO_BIN_PREFIX}/lib/rustlib/install.log
    rm -f ${D}${CARGO_BIN_PREFIX}/lib/rustlib/components
    rm -f ${D}${CARGO_BIN_PREFIX}/lib/rustlib/rust-installer-version
}
