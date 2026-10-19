# Reading Adventure delivery plan

1. Discover installed REA and tools; isolate new tooling under `.tools/`.
2. Establish reference evidence from publisher documentation and, only following permission, an APK. Keep original artifacts outside application sources. Never reuse protected assets.
3. Build an independent Kotlin/Compose game: local content, deterministic puzzle logic, animated Canvas characters and letters, snapping, progress, accessible alternative tap controls.
4. Add an audio coordinator with utterance completion events, offline TTS selection, generated interaction tones, lifecycle cancellation, adjustable volume, and a recorded-audio replacement interface.
5. Add 100+ words, original sentences, gradual unlocking, parent gate and local persistence.
6. Test matching (including repeated letters), progression, animation transitions, stale audio callbacks, content integrity, and basic UI flows. Build debug and locally signed release artifacts; inspect APK manifest and signature.
7. Detect connected devices read-only. Ask for confirmation with concrete APK identity before installation, launch, runtime logs and performance checks.

## Acceptance boundaries
- Physical Galaxy S23 Ultra performance and functionality require a connected authorized device and installation confirmation.
- Offline Android TTS requires an installed English voice that does not require a network. No app network permission or downloads during child play.
- No unsupported claims about the reference app's rendering engine or internal synchronization.
