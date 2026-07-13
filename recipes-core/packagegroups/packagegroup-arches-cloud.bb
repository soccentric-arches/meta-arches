SUMMARY = "Arches cloud and telemetry runtime"
DESCRIPTION = "MQTT clients and TLS transport for cloud connectivity and telemetry"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    mosquitto \
    paho-mqtt-c \
    paho-mqtt-cpp \
    ca-certificates \
    openssl \
    curl \
"