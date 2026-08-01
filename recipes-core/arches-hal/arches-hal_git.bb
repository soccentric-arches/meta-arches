SUMMARY = "Arches HAL/gRPC middleware"
DESCRIPTION = "Hardware abstraction layer and gRPC middleware service consumed by the \
Arches Client and Configuration applications"
LICENSE = "CLOSED"

# Private repo — fetched over SSH. The build container already maps the
# host user's ~/.ssh in via KAS_SSH_DIR (see Makefile KAS_ENV / kas-container
# --ssh-dir), so no additional credential wiring is needed here.
SRC_URI = "git://git@github.com/SiliconCentric/Arches-HAL.git;protocol=ssh;branch=develop"

# Tracks the tip of develop. Not reproducible build-to-build — pin SRCREV to
# a specific commit once develop stabilizes.
SRCREV = "${AUTOREV}"
PV = "0.1+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = "grpc-native grpc protobuf protobuf-native"
RDEPENDS:${PN} += "grpc protobuf"

inherit cmake pkgconfig

# The Arches-HAL CMake build installs shared libraries as unversioned .so
# files (no libfoo.so.N symlink chain). Yocto's QA check expects .so files
# in -dev packages to be symlinks; skip that check since the upstream build
# does not produce versioned shared objects.
INSANE_SKIP:${PN}-dev = "dev-elf dev-so"

# gRPC 1.60.1's CMake config does not export the gRPC::grpc_cpp_plugin target
# (added in gRPC 1.62). Replace the get_target_property call with find_program
# so the plugin binary is located by name instead.
do_configure:prepend() {
    sed -i '/get_target_property(GRPC_CPP_PLUGIN gRPC::grpc_cpp_plugin LOCATION)/c\
find_program(GRPC_CPP_PLUGIN grpc_cpp_plugin\
    HINTS ${gRPC_BIN_DIR} /usr/local/bin /usr/bin /usr/lib/grpc\
          /usr/lib/aarch64-linux-gnu /usr/lib/x86_64-linux-gnu\
    DOC "Path to the grpc_cpp_plugin protoc plugin")\
if(NOT GRPC_CPP_PLUGIN)\
  message(FATAL_ERROR "[hal_protos] grpc_cpp_plugin not found via CMake config")\
endif()' ${S}/protos/CMakeLists.txt
}
