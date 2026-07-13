#!/bin/bash
# swupdate-bootcheck — confirms a pending OTA boot and restores the previous
# extlinux configuration if the firmware did not boot the requested slot.
#
# Jetson Orin Nano SD card:
#   /dev/mmcblk1p2  → slot a (rootfs-a)
#   /dev/mmcblk1p3  → slot b (rootfs-b)

set -euo pipefail

ACTIVE_DEV=$(findmnt -n -o SOURCE / 2>/dev/null || echo "unknown")
case "${ACTIVE_DEV}" in
    /dev/mmcblk1p2) SLOT=a ;;
    /dev/mmcblk1p3) SLOT=b ;;
    *)              SLOT=unknown ;;
esac

logger -t swupdate-bootcheck "Boot confirmed: slot=${SLOT} dev=${ACTIVE_DEV}"
echo "slot=${SLOT}" > /run/swupdate-active-slot

EXTLINUX_CONF="/boot/extlinux/extlinux.conf"
EXTLINUX_BACKUP="${EXTLINUX_CONF}.arches-previous"
OTA_PENDING="/boot/extlinux/arches-ota-pending"

if [[ -f "${OTA_PENDING}" ]]; then
    EXPECTED_PARTUUID=$(cat "${OTA_PENDING}")
    ACTIVE_PARTUUID=$(blkid -s PARTUUID -o value "${ACTIVE_DEV}" 2>/dev/null || true)

    mount -o remount,rw /boot
    if [[ "${ACTIVE_PARTUUID}" == "${EXPECTED_PARTUUID}" ]]; then
        rm -f "${OTA_PENDING}" "${EXTLINUX_BACKUP}"
        logger -t swupdate-bootcheck "OTA boot confirmed: ${EXPECTED_PARTUUID}"
    elif [[ -f "${EXTLINUX_BACKUP}" ]]; then
        cp -f "${EXTLINUX_BACKUP}" "${EXTLINUX_CONF}"
        rm -f "${OTA_PENDING}" "${EXTLINUX_BACKUP}"
        logger -t swupdate-bootcheck "OTA boot mismatch; restored previous slot"
        mount -o remount,ro /boot
        systemctl reboot
        exit 0
    fi
    mount -o remount,ro /boot
fi
