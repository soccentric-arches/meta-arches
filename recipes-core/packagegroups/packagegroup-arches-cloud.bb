SUMMARY = "Arches cloud and telemetry runtime"
DESCRIPTION = "MQTT clients and TLS transport for cloud connectivity and telemetry"
LICENSE = "MIT"

# Set BEFORE inheriting packagegroup: the class snapshots PACKAGE_ARCH at
# parse time (inherit_defer allarch only when the snapshot is "all"). This
# group depends on packages whose RPM output names are dynamically renamed,
# so it cannot be shared as an allarch package.
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    mosquitto \
    paho-mqtt-c \
    paho-mqtt-cpp \
    ca-certificates \
    openssl \
    curl \
"