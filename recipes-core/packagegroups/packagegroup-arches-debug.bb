SUMMARY = "Arches validation and diagnostic tools"
DESCRIPTION = "Target-side tools for SDK validation, profiling, debugging, and hardware diagnostics"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    bash \
    gdb \
    gdbserver \
    perf \
    strace \
    ltrace \
    valgrind \
    lsof \
    iproute2 \
    ethtool \
    sysstat \
    ptest-runner \
"