# Adapt meta-webconfig's webconfigd.bb to Arches-OS's kirkstone + meta-rust-bin
# toolchain.
#
# The recipe was written against oe-core's scarthgap-era cargo.bbclass layout
# (CARGO_BUILD_FLAGS = "--release", artifacts at ${B}/release/<name>), but this
# build inherits meta-rust-bin's cargo_bin class instead (see the recipe header
# for why: kirkstone's rust-native is below the daemon's MSRV). Two mismatches
# follow, both fixed here:
#
# 1. Build flags. cargo_bin_do_compile embeds `cargo build ${CARGO_BUILD_FLAGS}`,
#    which bitbake expands at script-generation time — so the recipe's
#    `CARGO_BUILD_FLAGS = "--release"` is the entire command. cargo then runs in
#    ${B} (cargo_bin builds out-of-tree) and cannot find Cargo.toml:
#      error: could not find `Cargo.toml` in `.../target` or any parent directory
#    Set the class-style full flags instead: manifest path, cross target, and the
#    release profile (Cargo.toml's [profile.release] then supplies opt-level=z,
#    lto, codegen-units=1, panic=abort, strip=symbols as the recipe intends).
#
#    The recipe also exports CARGO_NET_OFFLINE = "1" — cargo only accepts
#    "true"/"false" and errors out at dependency resolution:
#      error in environment variable `CARGO_NET_OFFLINE`: provided string was
#      not `true` or `false`
#    Re-export with the accepted spelling (same intent: committed vendor/
#    tarball, no network access at build time).
#
# 2. Install path. With --target=${RUST_TARGET} the binary lands in
#    ${CARGO_BINDIR} = ${B}/${RUST_TARGET}/release, not ${B}/release, so the
#    recipe's do_install:append `install ${B}/release/webconfigd` cannot stat
#    the artifact. Same-name task fragments concatenate across .bb and
#    .bbappend, so that line cannot be replaced from here — instead a
#    do_install:prepend stages the binary at the path the recipe already
#    installs from. Everything else in the recipe's install block is unchanged.

# --- Build flags (fix 1) -----------------------------------------------------
CARGO_BUILD_FLAGS = " \
    --manifest-path ${CARGO_MANIFEST_PATH} \
    --target=${RUST_TARGET} \
    --profile=${CARGO_BUILD_PROFILE} \
"

# --- Build flags (fix 1, continued) ------------------------------------------
export CARGO_NET_OFFLINE = "true"

# --- Cargo config discovery (fix 1c) -----------------------------------------
# Cargo looks for .cargo/config only in the current directory and its parents —
# --manifest-path does not count. cargo_bin_do_compile runs from ${B}, so the
# workspace's committed .cargo/config (source replacement to the vendored
# directory) was never seen and resolution failed with:
#   error: no matching package named `anyhow` found
#   location searched: crates.io index
# Run cargo from the manifest directory so the vendored config applies.
do_compile:prepend() {
    cd '${S}'
}

# --- Staging for the recipe's install block (fix 2) --------------------------
do_install:prepend() {
    mkdir -p '${B}/release'
    cp -L '${CARGO_BINDIR}/webconfigd' '${B}/release/webconfigd'
}

# --- Packaging (fix 3) -------------------------------------------------------
# a) The binary is stripped at build time (Cargo.toml's [profile.release]
#    strip = "symbols" — the recipe's size budget, spec §2), so bitbake's
#    [already-stripped] QA check fires and fails do_package. Skip it.
INSANE_SKIP:${PN} += "already-stripped"

# The recipe installs its polkit rule and the runtime data partition but ships
# neither (its FILES:${PN} is unset, so only the default ${bindir}/${sysconfdir}
# globs apply). Ship both — /var/lib/webconfig as an empty, webconfig-owned
# directory (the unit has ReadWritePaths but no StateDirectory).
FILES:${PN} += "${datadir}/polkit-1 /var/lib/webconfig"

# --- Sysroot useradd (fix 4) -------------------------------------------------
# meta-webconfig's webconfigd.bb declares the webconfig group (GROUPADD_PARAM)
# and the webconfig user (USERADD_PARAM) as two separate useradd operations.
# useradd.bbclass runs groupadd first, then useradd — and `useradd -r` without
# -g tries to auto-create a same-named group, which already exists from the
# GROUPADD step, so the sysroot useradd fails outright:
#
#   useradd: group webconfig exists - if you want to add this user to that
#   group, use -g.
#   ERROR: webconfigd: useradd command did not succeed.
#
# Point useradd at the pre-created group so the do_prepare_recipe_sysroot
# useradd succeeds and do_install's chowns (e.g. /var/lib/webconfig) can
# resolve webconfig via PSEUDO_PASSWD. Same parameters as the recipe, plus -g.
USERADD_PARAM:${PN} = "-r -M -g ${WEBCONFIG_GROUP} -d /var/lib/webconfig -s /usr/sbin/nologin ${WEBCONFIG_USER}"