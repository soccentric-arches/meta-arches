SUMMARY = "Arches Mining & Heavy Industry image"
DESCRIPTION = "Arches (NVIDIA Jetson) Mining & Heavy Industry image — the complete arches-image base (A/B OTA via SWUpdate, arches-connectivity, gRPC/Protobuf, hardware interface tools, arches user) plus the shared arches-image-common completeness layer (SSH server, debug tools, full command-line + build environment, monitoring, Python toolchain) and Mining & Heavy Industry-specific packages."
LICENSE = "MIT"

# Build on the complete Arches base image, then layer the shared completeness
# set (see arches-image-common.inc), then add only Mining & Heavy Industry-specific packages.
require recipes-core/images/arches-image.bb
require recipes-core/images/arches-image-common.inc

# --- Mining & Heavy Industry-specific packages (verified present in the included Yocto layers) ---
IMAGE_INSTALL:append = " \
	mosquitto paho-mqtt-c python3-paho-mqtt \
	libmodbus can-utils python3-can gpsd \
	linuxptp ptpd open62541 libcoap \
	v4l-utils opencv fio stress-ng iperf3 \
"
