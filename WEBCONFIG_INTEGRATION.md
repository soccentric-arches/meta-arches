# WebConfig integration — what changed and why

This layer (`meta-arches`) now ships the full on-device WebConfig UI
(`webconfigd` + frontend + schemas + board manifest) described in
`Arches-Configuration/Feature.md`, plus every system package/service that UI
manages or shells out to. Reviewed against `Feature.md` page by page; this
file records the non-obvious decisions so they don't get silently reverted.

## Layout

- `recipes-core/webconfig/` — `webconfigd.bb`, `webconfig-static.bb`,
  `webconfig-schemas.bb`, `webconfig-manifest.bb`. Ported directly into
  meta-arches (not kept as a separate `meta-webconfig` layer pulled in via
  kas) — meta-arches is already the one product layer this build includes,
  and the daemon/frontend/schemas/manifest are Arches-only, so a second layer
  bought nothing. Application source stays owned by `Arches-Configuration`;
  only Yocto packaging moved. The two fetched tarballs
  (`webconfigd-0.1.0.tar.gz`, `vendor.tar.gz`) are `Arches-Configuration`
  release artifacts (`tools/release-artifacts.sh` / `make
  release-artifacts`) copied into `recipes-core/webconfig/files/` with their
  real, already-pinned SHA-256 checksums. Refresh both + the checksums on
  every Arches-Configuration release.
- `recipes-core/packagegroups/packagegroup-arches-webconfig.bb` — the new
  package group; installed from `arches-image.bb` (covers the base
  `arches-image` build target and, transitively, every domain variant that
  requires it).
- `recipes-support/arches-platform-status/` — generates
  `/usr/share/webconfig/platform-status.json`, the BSP-provided trust-status
  file the Secure Boot page reads. See below.
- `recipes-support/arches-rauc-shim/` — `/usr/bin/rauc` compatibility shim.
  See below — this is the one item that's a genuine compromise, not a
  straightforward port.
- `recipes-connectivity/arches-timesync/` — default systemd preset so
  chrony/linuxptp/gpsd don't fight `systemd-timesyncd` before the daemon has
  applied a saved Clock/NTP/PTP/GPS config.
- `recipes-kernel/linux/files/arches-connectivity.cfg` — kernel fragment for
  WireGuard, USB CDC-ACM (STM32 companion MCU), PPS (GPS), CAN-FD.
- `recipes-swupdate/arches-swupdate-config/files/swupdate-bootcheck.sh` —
  changed from "auto-confirm on first matching boot" to a boot-attempt
  counter with auto-rollback, to actually match Feature.md's documented
  "Confirm booted image ... cancels bootloader auto-rollback" semantics.
- `conf/layer.conf` — `DISTRO_FEATURES:append = " polkit"` (polkit's own
  recipe is gated on `REQUIRED_DISTRO_FEATURES = "polkit"`; without this the
  package doesn't build at all).

## The RAUC gap (read this before touching Updates/OTA)

`Arches-Configuration/crates/webconfigd/src/update_backend.rs` and
`crates/webconfig-engine/src/update.rs` talk to update state through a
literal `rauc` binary on `PATH` — `rauc --version` (probe),
`rauc status --output-format=json`, `rauc info <bundle>`, `rauc install
<bundle>`, `rauc status mark-good`. If `rauc` isn't present on a real device,
the daemon *silently falls back to a mock backend with fabricated slot data*
(logged as a warning, but the Updates page just shows fake numbers).

Arches-OS's actual OTA transport has always been **SWUpdate**
(`arches-swupdate-config`, `.swu` bundles, extlinux/PARTUUID slot switching)
— there is no RAUC anywhere in this build, and no `meta-rauc` layer in
`kas/common.yaml`. Adding one is out of this change's scope (it's a new
external layer + kas repo entry, not a meta-arches-only change, and swapping
the actual OTA transport to RAUC is a much larger project: bootloader
env integration, partition/GUID changes, bundle format change from `.swu` to
`.raucb`, rewriting `sw-description`, etc.).

Given that, `arches-rauc-shim` installs `/usr/bin/rauc` implementing exactly
the four subcommands the engine calls, backed by the real SWUpdate/extlinux
state (`/boot/extlinux/arches-ota-pending`, the new
`/boot/extlinux/arches-ota-boot-attempts`). `rauc install` hands off to real
`swupdate -f /etc/swupdate/swupdate.cfg -i <bundle>`; `rauc status
mark-good` clears the pending/attempt markers so
`swupdate-bootcheck.service` never rolls the boot back.

This makes the Updates page reflect real device state instead of the mock
fallback, but it is **not upstream RAUC** — `rauc info`'s `Compatible:` line
is hardcoded to `'arches'` (our `.swu` format has no per-bundle
platform/variant manifest field the way a real RAUC bundle does), and the
inactive slot's version is always reported as `"unknown"` (its rootfs isn't
mounted to read `/etc/os-release` — reporting a fabricated version would be
worse than an honest "unknown"). If Arches-Configuration ever needs real
variant-mismatch enforcement or accurate dual-slot version reporting, that
requires either embedding rauc-shaped metadata in the `.swu` build step or
adopting real RAUC as the OTA transport — a decision for a follow-up, not
silently done here.

## Secure Boot page

`board-manifests/arches.json` declares `"secure_boot": true, "has_tpm":
false` — those are the *build-time capability* flags and were already
correct data (owned by Arches-Configuration, untouched here). The *live*
trust-status fields (`update_signing_key_id`, `verity_status`,
`encryption_status`) come from a separate file,
`/usr/share/webconfig/platform-status.json`, that nothing in Arches-OS
produced before this change — so the page always reported "not available on
this platform" for those three fields. `arches-platform-status` now derives
that file from the same `ARCHES_SECURE_BOOT` / `ARCHES_SWUPDATE_PUBLIC_KEY`
variables `arches-image.bb` already uses to install the SWUpdate
verification key: when secure boot is off (the default), it ships no file at
all (still an honest "not available"); when on, it reports the installed
key's fingerprint and an honest `"not_enabled"` for verity/encryption, since
neither dm-verity nor block encryption is wired into this rootfs.

## Package additions and why (Feature.md cross-reference)

| Feature.md area | Package(s) added | Why |
|---|---|---|
| Security > WireGuard | `wireguard-tools` | `wg`/`wg-quick` shelled directly (`api/security.rs`, `appliers/wireguard.rs`) |
| Security > Firewall | `nftables` | firewall applier writes/loads `nft` rulesets, not iptables |
| Security (D-Bus/polkit) | `polkit` (+ `DISTRO_FEATURES += polkit`) | webconfigd's only path to privileged systemd unit ops / reboot |
| Connectivity > Wireless (AP mode) | `hostapd`, `dnsmasq`, `iw`, `wireless-regdb` | `appliers/ap.rs` / `appliers/wifi.rs` systemctl-manage these directly; not in the manifest's `service_allowlist` (that list is Services-page-only) but still required packages |
| Time & Sync (NTP/PTP/GPS) | `linuxptp`, `gpsd`, `gpsd-clients` (`chrony` already present) | `ptp4l@<iface>`, `phc2sys`, `gpsd`, `chrony` all shelled by `appliers/time.rs` depending on selected mode |
| Monitoring > eMMC wear | `mmc-utils` | `monitoring.rs::read_emmc_wear` falls back to `mmc extcsd read` when the sysfs node is absent |
| Updates (RAUC CLI contract) | `arches-rauc-shim` | see above |
| Security > Secure Boot | `arches-platform-status` | see above |

Everything else in `Feature.md` (schema-driven rendering, RBAC, audit log,
CSRF, SSE, dirty-guard, command palette, GPIO/I2C/SPI/serial/CAN/camera
peripheral tables, MCU flashing, users/groups, backup/restore, remote
logs/updates) is served entirely by `webconfigd` itself plus tooling already
present in `arches-image.bb` / `arches-image-common.inc` before this change
(`libgpiod`, `i2c-tools`, `spitools`, `can-utils`, `v4l-utils`, `shadow`
(`chpasswd`/`useradd`/`groupadd`), `ca-certificates`, journald/systemd) — no
further OS changes were needed for those.
