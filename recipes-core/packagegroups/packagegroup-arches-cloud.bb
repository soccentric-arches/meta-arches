SUMMARY = "Arches cloud connectivity runtime"
DESCRIPTION = "Transport and trust material for the device-to-HQ link: gRPC over a \
WireGuard tunnel, with TLS trust anchors."
LICENSE = "MIT"

inherit packagegroup

# This group depends on packages whose RPM output names are dynamically
# renamed, so it cannot be shared as an allarch package.
PACKAGE_ARCH = "${MACHINE_ARCH}"

# MQTT was removed here (Task.md D-15). This group used to pull mosquitto,
# paho-mqtt-c and paho-mqtt-cpp and describe itself as "MQTT clients and TLS
# transport for cloud connectivity and telemetry". Nothing in the fleet speaks
# MQTT: a search across Arches-Instrumentation, Arches-Configuration,
# Arches-Client, Arches-Server and Arches-HQ finds no reference to mosquitto,
# paho or mqtt in any source file.
#
# Both candidate device-to-HQ protocols are gRPC -- HQ serves
# archeshq.agent.AgentLink and istate dials instr.istate.HqPeer -- so this
# holds whichever way Task.md C-01 is decided. The packagegroup was not merely
# unused; it described an architecture the product does not have, which is the
# more expensive kind of wrong.
#
# What the cloud link actually needs is here instead: the tunnel it runs
# inside, the gRPC/protobuf runtime it speaks, and the trust anchors it
# validates against.
RDEPENDS:${PN} = " \
    wireguard-tools \
    grpc \
    protobuf \
    ca-certificates \
    openssl \
    curl \
"
