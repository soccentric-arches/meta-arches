# Disable GCC LTO for AARCH64 - LTO-generated ELF binaries are incompatible
# with the GenFw ELF-to-PE converter used in this EDK2/meta-tegra version.
# Override apply_tools_def_updates to strip -flto and GccLto plugin flags
# from tools_def.txt after it is populated from the template.

apply_tools_def_updates() {
    if [ -e ${S_EDK2_NVIDIA}/Silicon/NVIDIA/tools_def.inc ]; then
        echo "!include Silicon/NVIDIA/tools_def.inc" >> ${S}/Conf/tools_def.txt
    fi
    # Remove -flto and associated LTO linker plugin flags for AARCH64
    # Also add -Wno-maybe-uninitialized to suppress GCC false-positive in
    # upstream NVIDIA EDK2 PlatformResourceLib.c (CpuMapOffset warning)
    sed -i \
        -e 's/\r//' \
        -e 's/ -flto\b//g' \
        -e 's/ -DUSING_LTO\b//g' \
        -e 's/ -L\$(WORKSPACE)\/BaseTools\/Bin\/GccLto -llto-aarch64//g' \
        -e 's/ -Wl,-plugin-opt=-pass-through=-llto-aarch64//g' \
        -e 's/ -Wno-lto-type-mismatch//g' \
        -e '/GCC5_AARCH64_CC_FLAGS/ s/$/ -Wno-maybe-uninitialized/' \
        ${S}/Conf/tools_def.txt
}
