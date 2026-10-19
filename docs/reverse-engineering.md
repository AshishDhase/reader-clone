# Endless Reader: bounded technical investigation

Investigation date: 2026-10-19. This report records inspected local tooling and public publisher documentation. **An Endless Reader APK has not been acquired, hashed, decompiled, installed, or executed in this investigation.** The original application's internal architecture remains unresolved. The independently written Reading Adventure does not depend on resolving those internals.

## Scope and evidence rules

The scope is permitted technical observation and an independent implementation. No purchase, licensing, DRM, or paywall bypass is authorized. No original artwork, character designs, animation tracks, voice recordings, or decompiled code may enter the new application. The user explicitly approved an APKMirror download for static analysis. The attempt was blocked by HTTP 403; no block was bypassed. A publicly listed download is not proof of redistribution authorization or of publisher authenticity.

Evidence labels below are local report references, **not REA-generated Evidence IDs**. No target graph or native session was opened, so no REA target Evidence ID exists yet.

| ID | Evidence inspected | Result and boundary |
|---|---|---|
| E1 | Installed `rea-agents/package.json` | Package version 4.0.1; executable entry `scripts/rea.mjs`; repository `https://github.com/morluto/rea`; Node requirement `^22.19.0 || >=24.11.0`. |
| E2 | Installed `README.md`, especially requirements, CLI, tool catalog, packages/resources | Documents native analysis through Hopper/Ghidra, package inventory, safe extraction, evidence bundles, browser/Electron observation, managed PE/CLI analysis. These are tool capabilities, not observations of Endless Reader. |
| E3 | Installed CLI `--help`, `analyze --help`, `inspect-artifact --help`, `decompile --help` | Confirms actual invocation syntax below. |
| E4 | Installed CLI `doctor --json` | Exit 1; Node and host healthy, Hopper present, Ghidra unconfigured, client-registration drift/missing. Skill identity aligned. |
| E5 | Installed CLI `providers --json` | No target open; composite provider includes Hopper, Ghidra, native macOS utilities, safe artifact graph, managed static analysis. Artifact inspection available. Enumeration does not prove each provider can open a given APK or ELF. |
| E6 | Installed `dist/domain/androidApplication.js`, lines 61–88 | Its internal APK projection explicitly limits observations to paths/hashes and requires a dedicated Android provider for manifest/resource/signing/bytecode semantics. This internal operation is absent from observed CLI help and was not invoked. |
| E7 | [Publisher's Android listing](https://play.google.com/store/apps/details?id=com.originatorkids.EndlessReader&hl=en) | Confirms published title, publisher and package identifier; supplies product behavior claims. It is not an APK manifest or runtime trace. |
| E8 | [Originator publisher feed](https://originatorkids.com/feed/atom/index.html), Endless Reader item | Corroborates broad educational mechanics. Direct product/support page attempts were unavailable through the browser tool. |
| E9 | [APKMirror 3.0.0.45 variant listing](https://www.apkmirror.com/apk/originator-inc/endless-reader/endless-reader-3-0-0-45-release/endless-reader-3-0-0-45-android-apk-download/) via indexed web result | Third-party metadata only; APK bytes were not available. |
| E10 | Authorized `curl -fL` request to E9 | Initial sandbox DNS failure; escalated retry reached host and returned HTTP 403, curl exit 56. No file saved. |

## Installed REA and readiness

The inspected package lives at:

```text
/Users/ashishdhase/.npm/_npx/9b9809298fe40d2c/node_modules/rea-agents
```

Commands below use that existing package, with no `npx` update, global installation, or setup mutation:

```sh
REA_CLI=/Users/ashishdhase/.npm/_npx/9b9809298fe40d2c/node_modules/rea-agents/scripts/rea.mjs
node "$REA_CLI" --help
node "$REA_CLI" doctor --json
node "$REA_CLI" providers --json
node "$REA_CLI" inspect-artifact --help
node "$REA_CLI" analyze --help
node "$REA_CLI" decompile --help
```

Observed diagnosis (E4):

| Check | Observed result |
|---|---|
| CLI | 4.0.1 |
| Node | 25.9.0; healthy |
| macOS | 15.6.1; healthy |
| Hopper | Found at `/Users/ashishdhase/Applications/Hopper Disassembler.app/Contents/MacOS/hopper` |
| Ghidra | `GHIDRA_INSTALL_DIR is not set`; optional provider unavailable |
| CLI catalog | 69 commands; 116 MCP tools; 6 MCP prompts |
| Managed skill | Version 24; 116 tools; aligned digest |
| Client registration | Doctor reports drift/missing registrations, including Codex |
| Live MCP identity | Unknown; no target bound |

A failed audit-wide health result does not mean the existing CLI cannot inventory packages. No setup command was run. Refreshing client integrations would require reviewing configuration writes and approval; it is unnecessary for using the inspected CLI directly. Hopper presence is not confirmation of a license, functioning bridge, or successful native decompilation. Ghidra is optional and was not installed. The installed README requires Ghidra 12.1.4 and a 64-bit JDK 21 for that provider.

Actual useful command signatures from E3:

```text
rea inspect-artifact <path> [--integrity-policy fail|record-and-continue]
rea analyze <path> [--snapshot <string>] [--provider <value>]
rea decompile <path> <address> [--snapshot <string>] [--provider <value>]
```

Global output options include `--format json`, `--filter-output`, `--token-limit`, `--schema`, and `--help`. The installed CLI also exposes `extract-artifact`, `search`, `function`, `xrefs`, `trace`, evidence import/export/compare, and provider capability discovery. Deep native analysis can support selected native libraries once an authorized artifact and functioning provider exist. REA does not promise original source recovery or Android DEX decompilation through the native provider. JADX and Android SDK tools remain complementary for Android-specific semantics.

## Original application's technical findings

The Google Play URL identifies `com.originatorkids.EndlessReader`, published as **Endless Reader** by **Originator Inc.** This is listing-derived identity and must be checked against any acquired APK before treating it as artifact identity. [Source: publisher listing](https://play.google.com/store/apps/details?id=com.originatorkids.EndlessReader&hl=en).

| Requested question | Status | Evidence needed to resolve |
|---|---|---|
| Authorized APK acquisition | Blocked | User approved APKMirror, but server returned HTTP 403. A lawful user-provided APK or publisher-accessible artifact is still needed. |
| SHA-256 and signature | Unknown | Exact APK bytes, SHA-256, `apksigner verify --verbose --print-certs`; publisher certificate comparison where independently available |
| Package name | Partially answered | Listing identifier above; parsed APK manifest still required |
| Version/version code | Third-party claim only | E9 reports 3.0.0.45/code 45; APK manifest unverified |
| Minimum/target SDK | Third-party claim only | E9 reports minimum 24, target 34; APK manifest unverified |
| Permissions/components | Third-party permission list only | Listed below; components and decoded manifest unverified |
| Native libraries / ABIs | Third-party ABI claim only | E9 reports arm64-v8a and armeabi-v7a; individual libraries unknown |
| APK structure / expansion files | Unknown | Artifact graph and any authorized separate splits/OBB inventory |
| Unity | Unknown | Corroborated engine binaries and entry points; asset names alone insufficient |
| Native Android UI | Unknown | Activities, views/renderers, call paths and observation |
| OpenGL ES | Unknown | EGL/GLES imports or Java calls plus execution trace when required |
| Custom animation engine / other framework | Unknown | Timeline/resource formats and executable call paths |
| Audio engine / synchronization | Unknown | Asset metadata and event/callback code paths, corroborated with timed observation |
| Offline runtime | Unknown | Controlled launch/play after content download, network disabled with consent |

No architecture diagram of the original is warranted yet. A chain such as touch → matching → animation → narration is an independent behavioral model, not a recovered code architecture.

## Approved acquisition attempt and unverified reference values

The exact APKMirror variant page returned HTTP 403 on the network-enabled retry. No APK, download-page body, or partial binary exists in `analysis-inputs/`; that directory is ignored by Git and is outside Android app resources. No alternate mirror, anti-bot bypass or original app installation was attempted. The [official Google Play listing](https://play.google.com/store/apps/details?id=com.originatorkids.EndlessReader&hl=en) remains accessible, but its inspected page supplies no direct APK file; triggering Play installation would modify a device and was not authorized for the original application.

The following are **APKMirror's published reference values**, not locally computed results (E9):

```text
Filename: com.originatorkids.EndlessReader_3.0.0.45-45_minAPI24(arm64-v8a,armeabi-v7a)(nodpi)_apkmirror.com.apk
Bytes: 54270089
File SHA-256: 4fc0bdb2f5f1cb0245a6bdd507557886060abe51af4b66fb4446975ad21b2587
Certificate SHA-256: 2a3e0c7162a7f21230812cf38573f159419eb27f5ff93e1f0c173fb307f8815d
Certificate subject: O=Originator Inc.
```

E9 lists permissions `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `INTERNET`, `WAKE_LOCK` (under `android.permission`), `com.android.vending.BILLING`, `com.android.vending.CHECK_LICENSE`, and `com.originatorkids.EndlessReader.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`. It reports faketouch, landscape and Wi-Fi features. [APKMirror metadata source](https://www.apkmirror.com/apk/originator-inc/endless-reader/endless-reader-3-0-0-45-release/endless-reader-3-0-0-45-android-apk-download/).

These values help identify a future supplied artifact. They do not verify its integrity, prove publisher provenance, establish runtime permission use or justify installation. No `apksigner` verification can be performed without APK bytes. No additional decompiler was downloaded because there is no artifact to inspect.

## Next permitted static-analysis pass

1. Record exact source URL, download time, user authorization, byte count and SHA-256. Preserve the original unchanged. Do not assume a valid APK signature proves official origin; verify certificate provenance separately.
2. Inventory via the confirmed `inspect-artifact` command and retain graph manifest/Evidence IDs. Read-only package inspection is sufficient initially; do not execute unknown code.
3. Decode manifest/SDK/permissions with installed Android SDK tooling. Inventory DEX, native libraries, ABIs, resources, assets, splits and any expansion content. Do not redistribute extracted assets in this project.
4. Use JADX/APKTool only if available or explicitly approved for installation. Map application/activity entry points, touch dispatch, matching decisions, render-loop ownership, audio events and lifecycle cleanup. Record tools, versions, search boundaries and unresolved paths.
5. For native execution paths, inspect chosen ELF libraries with REA/Hopper. Cite function addresses and Evidence IDs; treat pseudocode as recovered representation. Corroborate engine identity from multiple signals.
6. Investigate animation storage (sprite atlases, frame sequences, skeletal/timeline formats or procedural motion) and audio containers without copying their content into the new application. Metadata alone cannot establish when playback occurs.
7. Stop before license/purchase enforcement bypass. Close any opened native session after preserving evidence.

## Runtime observation protocol still pending

Use an authorized original installation and user-approved device interaction. Observe only accessible content. Record app version, device/OS, orientation and timing source. Capture: selecting an available word, pressing/dragging/cancelling a letter, correct/incorrect target drops, repeated-letter behavior, completion, sentence phase, replay, interruption, rotation and returning from background. Compare voice start/end with visible reactions. Test offline only after the user authorizes changing network state. Measure frame pacing and memory separately from subjective smoothness. A recording demonstrates observable timing, not the implementation mechanism behind it.

## Completion status

Environment/REA discovery: complete within the inspected CLI boundary. Public gameplay research: partial, documented in [gameplay-analysis.md](gameplay-analysis.md). APKMirror acquisition: explicitly authorized, attempted and blocked by HTTP 403. Original APK static analysis and runtime behavior: unresolved pending lawful artifact availability. Independent implementation can proceed from user requirements and general educational interaction patterns without claiming those missing phases are complete.
