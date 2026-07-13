SUMMARY = "Arches cloud and telemetry runtime"
DESCRIPTION = "MQTT clients and TLS transport for cloud connectivity and telemetry"
LICENSE = "MIT"

inherit packagegroup

# This group depends on packages whose RPM output names are dynamically
# renamed, so it cannot be shared as an allarch package.
PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS:${PN} = " \
    mosquitto \
    paho-mqtt-c \
    paho-mqtt-cpp \
    ca-certificates \
    openssl \
    curl \
"