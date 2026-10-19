# Build and test instructions

## Versions

- Java 17 (this host: Eclipse Temurin 17.0.8 ARM64)
- Android Gradle plugin 8.9.2 / Gradle 8.11.1
- Kotlin and Compose compiler plugin 2.0.21
- Compose BOM 2024.12.01
- compile/target SDK 35, minimum 26; all packaged dependencies include ARM64 support

Compatibility reference: https://developer.android.com/build/releases/agp-8-9-0-release-notes

## Prepared local tools

`./scripts/gradle.sh` keeps Gradle cache and Android user files under `.tools/` and uses the downloaded SDK. No global tooling install was performed.

For a new workstation install the official Android command-line tools or Android Studio, review/accept SDK licenses, then install `platform-tools`, `platforms;android-35`, and `build-tools;35.0.0`. Point `local.properties` to your SDK with `sdk.dir=/absolute/path`. Set `JAVA_HOME` to a JDK 17 installation. Use `./gradlew` from the project; the wrapper downloads Gradle 8.11.1 with its pinned SHA-256.

## Build and automated checks

```sh
./scripts/gradle.sh :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest
```

Unit tests run locally, including Robolectric Compose UI flows. Results live in `app/build/reports/tests/testDebugUnitTest/` and XML in `app/build/test-results/testDebugUnitTest/`. Android lint produces `app/build/reports/lint-results-debug.html`.

Instrumented UI tests require an authorized emulator or physical device and install a test companion APK. On a physical phone, obtain user installation permission first. To target one device set `ANDROID_SERIAL` before `:app:connectedDebugAndroidTest`. Do not run install tasks against all attached devices inadvertently.

## Release signing

This workspace contains a generated random password in `.signing/password`, mode 0600. `scripts/sign-release.sh` creates a 3072-bit RSA PKCS12 key if absent, builds the release, verifies the APK signature and prints its SHA-256. The key is for this local app only. Do not reuse the debug key for distribution. Preserve the release key and password securely for compatible updates.

On a fresh checkout create `.signing/password` using a strong random password and set mode 0600, or configure your own signing key and `READING_STORE_PASSWORD` environment variable. Do not put secrets in source or command-line arguments.

## Galaxy S23 Ultra

Read-only detection: `scripts/inspect-device.sh [DEVICE_SERIAL]`.

After explicit approval: `scripts/install-confirmed.sh DEVICE_SERIAL --confirmed` installs the release and launches it. Debug and release use different certificates; do not uninstall an existing build or wipe progress without approval. Use a matching key for updates.

Check app-only logs and memory:

```sh
adb -s DEVICE_SERIAL shell pidof com.learning.readingadventure
adb -s DEVICE_SERIAL logcat -d -s AndroidRuntime ReadingAdventure
adb -s DEVICE_SERIAL shell dumpsys meminfo com.learning.readingadventure
adb -s DEVICE_SERIAL shell dumpsys gfxinfo com.learning.readingadventure
```

## Manual acceptance checklist

1. Choose a word; repeat speech; drag each tile into the correct home in different orders.
2. Drop outside targets and onto an incorrect target: no progress loss, gentle feedback and matching hint.
3. Use repeated letters (e.g. `little`, `green`); identical letters should fit either available matching home.
4. Complete the missing-word sentence; hear narration and see celebration; replay and continue.
5. Rotate during a puzzle, speech, and celebration; confirm state, insets and touch alignment.
6. Background and resume; no speech should continue in background or complete a stale animation.
7. Parent gate, mute, voice test, phonetic toggle, reset confirmation.
8. With an installed offline English voice, disable network with user consent, cold launch and complete words. Restore prior connectivity afterward. Do not equate lack of Internet permission with empirical offline voice validation.
9. Inspect frame timing during sustained dragging and particles, separately from startup; report percentile frame times and memory, not just a target FPS.
10. Test TalkBack tap placement; inspect longest words and both orientations.

Physical performance, audio quality and child usability require real measurements and observation. A finite smoke run is not comprehensive educational or accessibility validation.
