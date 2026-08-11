# rust-bin-cross installs rustc/rustdoc/rust-gdb/rust-gdbgui/rust-lldb and the
# target rustlib to /usr/, which collide with rust-native (pulled in by
# librsvg's standard cargo.bbclass) in the eSDK sysroot — breaking
# `make esdk-image`. Relocate the whole install to a subdirectory so the two
# toolchains don't collide; webconfigd.bb adds the relocated bin dir to PATH
# (see do_compile:prepend there).
RUST_BIN_PREFIX = "${prefix}/rust-bin-cross"

fakeroot do_install() {
    ${S}/install.sh --destdir="${D}" --prefix="${RUST_BIN_PREFIX}"
    cd ${COMPATIBLE_PACKDIR}/rust-std
    for subdir in *; do
        $subdir/install.sh --destdir="${D}" --prefix="${RUST_BIN_PREFIX}"
    done
    rm -f ${D}${RUST_BIN_PREFIX}/lib/rustlib/uninstall.sh
    rm -f ${D}${RUST_BIN_PREFIX}/lib/rustlib/install.log
    rm -f ${D}${RUST_BIN_PREFIX}/lib/rustlib/components
    rm -f ${D}${RUST_BIN_PREFIX}/lib/rustlib/rust-installer-version
}
