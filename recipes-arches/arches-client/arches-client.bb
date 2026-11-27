SUMMARY = "Arches Client — IV&V validation client for the Jetson target"
DESCRIPTION = "Runs on the board under validation: drives hardware through the \
Arches-HAL C++ API, spools results durably to SQLite, and streams them to \
Arches-Server. Also serves HalService for the SDK/E2E path."
HOMEPAGE = "https://github.com/soccentric-arches/Arches-Client"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Arches-Client's CMakeLists consumes Arches-HAL as ${CMAKE_SOURCE_DIR}/../Arches-HAL,
# so the pinned HAL is fetched next to the client (same revision as arches-hal.bb).
SRC_URI = " \
    git://github.com/soccentric-arches/Arches-Client.git;protocol=https;branch=develop;name=client;destsuffix=git \
    git://github.com/soccentric-arches/Arches-HAL.git;protocol=https;branch=develop;name=hal;destsuffix=Arches-HAL \
"
SRCREV_client = "8814133df9f42728297f21f113bf68576870a196"
SRCREV_hal = "e1f7e711380d32fad54bbb5198ed3b58b2c17b3a"
SRCREV_FORMAT = "client_hal"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd

# Arches-Client consumes Arches-HAL as a sibling CMake subproject and links its
# pre-built proto library rather than regenerating it -- that is the fleet's
# ODR rule (see .claude/project_specs.md, "Dependency rules"). The recipe
# therefore needs HAL's build tree, not just its runtime.
DEPENDS = "arches-hal abseil-cpp grpc grpc-native protobuf protobuf-native spdlog tomlplusplus yaml-cpp sqlite3"
RDEPENDS:${PN} = "arches-hal"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_TESTS=OFF \
    -DGRPC_CPP_PLUGIN=${STAGING_BINDIR_NATIVE}/grpc_cpp_plugin \
"
# The HAL compiles with -Werror and ignores write()/read() results in places;
# gRPC exposes Abseil only transitively, so let the linker follow DT_NEEDED.
CXXFLAGS:append = " -Wno-error=unused-result"
LDFLAGS:append = " -Wl,--copy-dt-needed-entries"


# The upstream CMakeLists installs the binary, conf/client.conf.sample (as
# /etc/arches/client.conf) and systemd/arches-client.service, so
# cmake_do_install covers all three.

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "arches-client.service"
SYSTEMD_AUTO_ENABLE = "enable"

# The unit runs User=arches Group=arches with SupplementaryGroups=instr-apps.
#
# The `arches` user is NOT created here: arches-image.bb creates it through
# `inherit extrausers` (EXTRA_USERS_PARAMS: useradd -m -s /bin/bash -G sudo
# arches). Adding a useradd in this recipe would be a second, conflicting
# definition of the same account. Checked before writing this.
#
# The `instr-apps` group comes from the instrumentation recipe's sysusers.d.
# It is a soft dependency on purpose: the unit's ReadWritePaths entry for the
# instrumentation log sink is '-'-prefixed and the unit is After= but not
# Requires= instr-istate, so the client still starts on an image built with
# ARCHES_INSTRUMENTATION = "0". See Task.md E-01/E-02.

FILES:${PN} += "${sysconfdir}/arches ${systemd_unitdir}/system"
CONFFILES:${PN} += "${sysconfdir}/arches/client.conf"
