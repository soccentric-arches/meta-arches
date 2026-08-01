# Central hand-editable kernel / device-tree / bootloader feature config.
#
# Edit this file to add or remove features. Every board image and SDK
# inherits these settings via kas/common.yaml → local.conf.
#
# == Kernel config fragments ==
# Drop a .cfg file into recipes-kernel/linux/files/<name>.cfg, then add
# <name> to ARCHES_KERNEL_FEATURES below. The linux-jammy-nvidia-tegra
# bbappend merges every listed fragment into KERNEL_CONFIG_FRAGMENTS.
#
# == Device-tree overlays ==
# Copy recipes-kernel/nvidia-kernel-oot/files/example-overlay.dts.template
# to <name>.dts in that same directory, then add <name> to:
#   ARCHES_DEVICETREE_OVERLAY_SOURCES  — makes it buildable
#   ARCHES_DEVICETREE_OVERLAYS         — applied from rootfs at boot
#   ARCHES_FLASH_OVERLAYS              — applied by UEFI from SPI flash
#   ARCHES_DEVICETREE_OVERLAY_INCLUDES — extra -I paths for cpp prep

# --- Kernel config fragments (space-separated list of names) ----------------
ARCHES_KERNEL_FEATURES ?= ""

# Resolved to full paths for the kernel bbappend (derived automatically).
ARCHES_KERNEL_CONFIG_FRAGMENTS ?= ""

# --- Device-tree overlays ----------------------------------------------------
ARCHES_DEVICETREE_OVERLAY_SOURCES ?= ""
ARCHES_DEVICETREE_OVERLAYS ?= ""
ARCHES_FLASH_OVERLAYS ?= ""
ARCHES_DEVICETREE_OVERLAY_INCLUDES ?= ""

# --- Bootloader --------------------------------------------------------------
# Extra kernel command-line tokens appended to extlinux.conf.
# Default is set in kas/common.yaml; override here per-product if needed.
# ARCHES_EXTLINUX_APPEND ?= ""
