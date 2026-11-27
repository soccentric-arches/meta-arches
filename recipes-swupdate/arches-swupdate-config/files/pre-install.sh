#!/bin/bash
# SWUpdate pre-install script for Jetson Orin Nano — A/B slot detection
#
# Identifies the inactive rootfs slot from the active root device and creates
# the /dev/swupdate-inactive symlink that the raw image handler writes to.
# Saves the target slot/device to /run/ for use by post-install.sh.
#
# Jetson Orin Nano devkit SD card partition layout:
#   /dev/mmcblk1p1  /boot      (FAT32 — kernel, DTBs, extlinux.conf)
#   /dev/mmcblk1p2  rootfs-a   (ext4  — active  slot)
#   /dev/mmcblk1p3  rootfs-b   (ext4  — standby slot)
#   /dev/mmcblk1p4  /pst       (ext4  — persistent instrumentation state)
#   /dev/mmcblk1p5  /home      (ext4  — persistent home)
#
# Only p2/p3 matter below: /pst and /home are outside both slots and are
# deliberately untouched by an update -- that is what makes them persistent.

set -euo pipefail

# ── Find the currently active root device ────────────────────────────────────
ACTIVE_DEV=$(findmnt -n -o SOURCE /)
echo "[swupdate pre-install] active root: ${ACTIVE_DEV}"

if [[ "${ACTIVE_DEV}" =~ ^(/dev/mmcblk[0-9]+p|/dev/nvme[0-9]+n[0-9]+p)(2|3)$ ]]; then
    PART_PREFIX="${BASH_REMATCH[1]}"
    ACTIVE_PART="${BASH_REMATCH[2]}"
elif [[ "${ACTIVE_DEV}" =~ ^(/dev/[a-z]+)(2|3)$ ]]; then
    PART_PREFIX="${BASH_REMATCH[1]}"
    ACTIVE_PART="${BASH_REMATCH[2]}"
else
    echo "[swupdate pre-install] ERROR: unexpected active device '${ACTIVE_DEV}'" >&2
    exit 1
fi

INACTIVE_PART=$((5 - ACTIVE_PART))
INACTIVE_DEV="${PART_PREFIX}${INACTIVE_PART}"
if [[ "${INACTIVE_PART}" == "2" ]]; then
    INACTIVE_SLOT=a
else
    INACTIVE_SLOT=b
fi

echo "[swupdate pre-install] target slot: ${INACTIVE_SLOT} (${INACTIVE_DEV})"

# ── Ensure the inactive partition is not mounted ──────────────────────────────
if mountpoint -q /mnt/rootfs-inactive 2>/dev/null; then
    umount /mnt/rootfs-inactive
fi

# ── Create the stable symlink the image handler will write to ─────────────────
ln -sf "${INACTIVE_DEV}" /dev/swupdate-inactive

# ── Persist slot info for post-install ────────────────────────────────────────
echo "${INACTIVE_SLOT}" > /run/swupdate-target-slot
echo "${INACTIVE_DEV}"  > /run/swupdate-target-dev
