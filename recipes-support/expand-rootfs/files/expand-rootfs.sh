#!/bin/bash
# expand-rootfs.sh — Grow root partition and filesystem to fill the SD card.
# Runs once on first boot via systemd.  A flag file prevents repeated runs.
#
# Jetson Orin Nano: SD card is /dev/mmcblk1; rootfs-a is p2, rootfs-b is p3.
# The A/B OTA layout uses fixed-size partitions so root expansion is not
# needed for the rootfs slots.  This script is kept for scenarios where a
# single-partition non-OTA layout is used.

set -euo pipefail

FLAG="/var/lib/expand-rootfs-done"

if [ -f "$FLAG" ]; then
    echo "expand-rootfs: already done, skipping."
    exit 0
fi

# ── Resolve root block device ───────────────────────────────────────────────
ROOT_DEV=$(findmnt -n -o SOURCE /)
ROOT_DEV_NAME="${ROOT_DEV#/dev/}"

if [[ "$ROOT_DEV_NAME" =~ ^(mmcblk[0-9]+)p([0-9]+)$ ]]; then
    DISK_NAME="${BASH_REMATCH[1]}"
    PART_NUM="${BASH_REMATCH[2]}"
elif [[ "$ROOT_DEV_NAME" =~ ^([a-z]+)([0-9]+)$ ]]; then
    DISK_NAME="${BASH_REMATCH[1]}"
    PART_NUM="${BASH_REMATCH[2]}"
else
    echo "expand-rootfs: cannot parse device '$ROOT_DEV', aborting." >&2
    exit 1
fi

DISK_DEV="/dev/$DISK_NAME"

echo "expand-rootfs: resizing partition $PART_NUM on $DISK_DEV ..."
parted -s "$DISK_DEV" resizepart "$PART_NUM" 100%

# Inform the kernel of the updated partition table
partprobe "$DISK_DEV" 2>/dev/null || true
udevadm settle

echo "expand-rootfs: growing filesystem on $ROOT_DEV ..."
resize2fs "$ROOT_DEV"

mkdir -p "$(dirname "$FLAG")"
touch "$FLAG"

NEW_SIZE=$(df -h / | awk 'NR==2{print $2}')
echo "expand-rootfs: done — root filesystem is now $NEW_SIZE."
