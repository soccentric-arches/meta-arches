# webconfig-schemas — schema files for schema-driven rendering.
#
# Drops every *.schema.json into /usr/share/webconfig/schemas/. Other recipes
# (customer apps, platform packs) append their own schema files via a
# bbappend to this recipe so adding a setting requires zero UI code:
#
#   WEBCONFIG_SCHEMAS_EXTRA:append = " file://my-app.schema.json"
#
# The engine loads this dir at startup; the per-section /api/v1/schemas/:section
# endpoint exposes descriptors to the frontend renderer.

SUMMARY = "SoCcentric WebConfig schema files"
LICENSE = "CLOSED"

# Schemas shipped by default (mirrors Arches-Configuration's schemas/ dir:
# the generic app-example plus the real STM32 companion-MCU settings schema
# referenced by board-manifests/arches.json's "mcu-stm32" peripheral).
SRC_URI = "file://app-example.schema.json file://app-mcu-stm32.schema.json"

# Extra schemas contributed by bbappends (the schema-driven extension point).
WEBCONFIG_SCHEMAS_EXTRA ?= ""
SRC_URI += "${WEBCONFIG_SCHEMAS_EXTRA}"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/usr/share/webconfig/schemas
    for f in ${S}/*.schema.json; do
        [ -e "$f" ] || continue
        install -m 0644 "$f" ${D}/usr/share/webconfig/schemas/
    done
}

FILES:${PN} = "/usr/share/webconfig/schemas"
