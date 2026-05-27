# Deploy extlinux.conf (and initrd if present) to DEPLOY_DIR_IMAGE so that
# the WIC bootimg-partition source plugin can populate the FAT32 boot partition
# via IMAGE_BOOT_FILES.

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}/extlinux
    install -m 0644 ${B}/extlinux.conf ${DEPLOYDIR}/extlinux/extlinux.conf
    if [ -f "${B}/initrd" ]; then
        install -m 0644 ${B}/initrd ${DEPLOYDIR}/initrd
    fi
}

addtask do_deploy after do_install before do_build
