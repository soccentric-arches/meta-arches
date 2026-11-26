SUMMARY = "Arches HAL — hardware abstraction layer and canonical gRPC contracts"
DESCRIPTION = "The C++ hardware abstraction layer for the Jetson target, plus the \
generated protobuf/gRPC contract libraries (hal, hal.common, hal.modules.*) that \
Arches-Client and Arches-Server consume. Also carries the STM32 co-processor link."
HOMEPAGE = "https://github.com/soccentric-arches/Arches-HAL"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://github.com/soccentric-arches/Arches-HAL.git;protocol=https;branch=develop"
# Pinned rather than AUTOREV, for the same reason instrumentation.bb is: an
# image with an OTA story must be reproducible from a tag (Task.md D-13).
SRCREV = "d4387596156a833f1b190aa8989134bd70e07953"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

inherit cmake pkgconfig

# HAL generates its own protobuf/gRPC sources at build time, so it needs the
# compilers and plugins on the host as well as the runtimes on the target.
DEPENDS = "grpc grpc-native protobuf protobuf-native"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_TESTING=OFF \
"

# The upstream CMakeLists already installs the aggregate libraries and the
# public headers, so cmake_do_install covers those. The module registry is
# data the dispatch layer reads at runtime and is not part of those rules.
do_install:append() {
    install -d ${D}${datadir}/arches-hal
    install -m 0644 ${S}/protos/registry/modules.yaml ${D}${datadir}/arches-hal/
}

FILES:${PN} += "${datadir}/arches-hal"

# Headers and the static archive are for building Arches-Client against, not
# for the device.
FILES:${PN}-dev += "${includedir}/arches_hal"
