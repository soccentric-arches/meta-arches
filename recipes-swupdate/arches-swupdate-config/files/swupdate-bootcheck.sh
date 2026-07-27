#!/bin/bash
# swupdate-bootcheck — auto-rollback-unless-confirmed boot gate + restores the
# previous extlinux configuration if the firmware did not boot the requested
# slot at all.
#
# Jetson Orin Nano SD card:
#   /dev/mmcblk1p2  → slot a (rootfs-a)
#   /dev/mmcblk1p3  → slot b (rootfs-b)
#
# Feature.md's Updates page describes a manual "Confirm booted image" action
# ("RAUC mark-good, cancels bootloader auto-rollback") — that implies an
# update boots into a *pending* state that auto-rolls-back unless explicitly
# confirmed. The extlinux/U-Boot boot path here has no native boot-counter
# (unlike RAUC's usual grub/u-boot integration), so this script implements
# that gate itself: each boot while a slot is pending increments an attempt
# counter; once it exceeds MAX_BOOT_ATTEMPTS without a mark-good (see
# /usr/bin/rauc, arches-rauc-shim), the previous extlinux.conf is restored
# and the device reboots back into the last-known-good slot. mark-good clears
# both the pending marker and the counter, so a confirmed boot is never
# rolled back.

set -euo pipefail

MAX_BOOT_ATTEMPTS=3

ACTIVE_DEV=$(findmnt -n -o SOURCE / 2>/dev/null || echo "unknown")
case "${ACTIVE_DEV}" in
    /dev/mmcblk1p2) SLOT=a ;;
    /dev/mmcblk1p3) SLOT=b ;;
    *)              SLOT=unknown ;;
esac

logger -t swupdate-bootcheck "Boot check: slot=${SLOT} dev=${ACTIVE_DEV}"
echo "slot=${SLOT}" > /run/swupdate-active-slot

EXTLINUX_CONF="/boot/extlinux/extlinux.conf"
EXTLINUX_BACKUP="${EXTLINUX_CONF}.arches-previous"
OTA_PENDING="/boot/extlinux/arches-ota-pending"
OTA_ATTEMPTS="/boot/extlinux/arches-ota-boot-attempts"

if [[ -f "${OTA_PENDING}" ]]; then
    EXPECTED_PARTUUID=$(cat "${OTA_PENDING}")
    ACTIVE_PARTUUID=$(blkid -s PARTUUID -o value "${ACTIVE_DEV}" 2>/dev/null || true)

    mount -o remount,rw /boot

    if [[ "${ACTIVE_PARTUUID}" != "${EXPECTED_PARTUUID}" ]]; then
        # The bootloader did not boot the slot we just flashed at all — roll
        # back immediately, no retry budget to burn.
        if [[ -f "${EXTLINUX_BACKUP}" ]]; then
            cp -f "${EXTLINUX_BACKUP}" "${EXTLINUX_CONF}"
        fi
        rm -f "${OTA_PENDING}" "${EXTLINUX_BACKUP}" "${OTA_ATTEMPTS}"
        logger -t swupdate-bootcheck "OTA boot mismatch (expected ${EXPECTED_PARTUUID}, got ${ACTIVE_PARTUUID:-none}); restored previous slot"
        mount -o remount,ro /boot
        systemctl reboot
        exit 0
    fi

    # Correct slot booted, but not yet confirmed (mark-good). Count this
    # attempt; auto-rollback once the budget is exhausted, otherwise wait for
    # either a manual confirm or another boot attempt.
    attempts=$(( $(cat "${OTA_ATTEMPTS}" 2>/dev/null || echo 0) + 1 ))
    echo "${attempts}" > "${OTA_ATTEMPTS}"

    if (( attempts > MAX_BOOT_ATTEMPTS )); then
        if [[ -f "${EXTLINUX_BACKUP}" ]]; then
            cp -f "${EXTLINUX_BACKUP}" "${EXTLINUX_CONF}"
        fi
        rm -f "${OTA_PENDING}" "${EXTLINUX_BACKUP}" "${OTA_ATTEMPTS}"
        logger -t swupdate-bootcheck "OTA boot unconfirmed after ${attempts} attempts; auto-rollback to previous slot"
        mount -o remount,ro /boot
        systemctl reboot
        exit 0
    fi

    logger -t swupdate-bootcheck "OTA boot ${EXPECTED_PARTUUID} pending confirmation (attempt ${attempts}/${MAX_BOOT_ATTEMPTS})"
    mount -o remount,ro /boot
fi
