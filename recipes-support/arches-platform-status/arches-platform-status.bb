SUMMARY = "Arches BSP platform-status file for WebConfig's Secure Boot page"
DESCRIPTION = "Installs /usr/share/webconfig/platform-status.json — the \
read-only BSP-provided trust-status source webconfigd's Security > Secure \
Boot page reads (update_signing_key_id / verity_status / encryption_status). \
Without this file the page must honestly report 'not available on this \
platform' for all three fields (never a fabricated value) rather than a real \
read; this recipe is what makes it a real read on Arches images. Content is \
derived from the same ARCHES_SECURE_BOOT / ARCHES_SWUPDATE_PUBLIC_KEY \
variables arches-image.bb already uses to install the SWUpdate verification \
key (kas/common.yaml, per-deployment override)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# openssl-native for the SHA-256 fingerprint computed below (build-host tool
# only — the target image already ships openssl via arches-image-common.inc
# for its own TLS/cert-store needs, but do_install here runs at build time).
DEPENDS = "openssl-native"

# Rebuild if the deployment's secure-boot inputs change.
ARCHES_SECURE_BOOT ?= "0"
ARCHES_SWUPDATE_PUBLIC_KEY ?= ""
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install() {
    # ARCHES_SECURE_BOOT=0 (dev/default builds): there is no real trust chain
    # to report yet — ship nothing at all so the daemon's read_platform_status()
    # correctly reports platform_status_available=false instead of a
    # fabricated "not_enabled" reading, matching secure_boot's own manifest
    # flag being the build-time capability declaration, not a live status.
    # Installing nothing (rather than installing then deleting) also avoids
    # leaving behind empty parent directories that trip the packaging QA
    # installed-vs-shipped check.
    if [ "${ARCHES_SECURE_BOOT}" != "1" ]; then
        return
    fi

    install -d ${D}${datadir}/webconfig

    key_id="null"
    if [ -n "${ARCHES_SWUPDATE_PUBLIC_KEY}" ] && [ -f "${ARCHES_SWUPDATE_PUBLIC_KEY}" ]; then
        # Short, stable identifier for the installed SWUpdate verification
        # key: SHA-256 of the DER-encoded SubjectPublicKeyInfo, first 16 hex
        # chars. ARCHES_SWUPDATE_PUBLIC_KEY may be either a bare public-key
        # PEM (what swupdate's public-key-file wants) or an x509 cert
        # containing one — try both rather than assuming the format.
        fp=$(openssl pkey -pubin -in "${ARCHES_SWUPDATE_PUBLIC_KEY}" -outform DER 2>/dev/null | sha256sum | cut -c1-16)
        if [ -z "$fp" ]; then
            fp=$(openssl x509 -in "${ARCHES_SWUPDATE_PUBLIC_KEY}" -pubkey -noout 2>/dev/null \
                 | openssl pkey -pubin -outform DER 2>/dev/null | sha256sum | cut -c1-16)
        fi
        if [ -n "$fp" ]; then
            key_id="\"sha256:${fp}\""
        fi
    fi

    # Neither dm-verity nor block-device encryption is wired into this image
    # (Arches-OS confirms integrity via A/B slot signature verification, not
    # verity/dm-crypt) — report the real, honest "not_enabled" state rather
    # than a bare "unknown" (spec: distinguish real-read-came-back-negative
    # from feature-not-implemented-on-this-platform).
    cat > ${D}${datadir}/webconfig/platform-status.json <<EOF
{
    "update_signing_key_id": ${key_id},
    "verity_status": "not_enabled",
    "encryption_status": "not_enabled"}
EOF
}

FILES:${PN} = "${datadir}/webconfig/platform-status.json"
ALLOW_EMPTY:${PN} = "1"
