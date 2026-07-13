SUMMARY = "Arches SDK validation and development target support"
DESCRIPTION = "Target development headers, test runner, profiling and debugging support exported by the Arches SDK"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    packagegroup-arches-debug \
    ptest-runner \
    gdbserver \
    perf \
    trace-cmd \
    valgrind \
"

RRECOMMENDS:${PN} = " \
    kernel-devsrc \
    ${@bb.utils.contains('ARCHES_MIDDLEWARE', '1', 'packagegroup-arches-middleware', '', d)} \
    ${@bb.utils.contains('ARCHES_CLOUD', '1', 'packagegroup-arches-cloud', '', d)} \
"