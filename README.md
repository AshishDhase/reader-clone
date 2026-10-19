# Reading Adventure — The Little Word Garden

An independently written Android reading game for ages 3–6. Kotlin, Jetpack Compose, Canvas characters, coroutines, local JSON, and Android offline TextToSpeech. Package: `com.learning.readingadventure`.

## Play

Choose a word, drag its colorful letter tiles into matching homes (or tap a tile then a home), and select the missing word in an original sentence. Pip and Moss celebrate with falling leaves. Grow 12 distinct words in each garden to open the next; completed words remain replayable. There are 108 words in three gardens, no timers or penalties, no accounts, ads, purchases, tracking, or app Internet permission.

Parents can change volume, test the installed English voice, choose letter names versus approximate TTS letter sounds, and reset progress after an arithmetic gate plus reset confirmation. English TTS must already have an offline voice installed. The app does not download voice data. Without an offline voice, visual play remains available and parent settings explains the problem. TTS sound approximations are not professionally recorded phonemes.

## Build

Requirements: JDK 17, Android SDK platform 35/build tools 35.0.0, Gradle 8.11.1. Dependencies are downloaded during the first build; gameplay requires no network. Open this directory in Android Studio and select JDK 17. `local.properties` supplies the local SDK path.

On this prepared workspace:

```sh
./scripts/gradle.sh :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
./scripts/sign-release.sh
```

The project-local SDK, Gradle, caches and signing files are excluded from source. `scripts/gradle.sh` uses `.tools/`; for a portable checkout use the Gradle wrapper and your own SDK in `local.properties`. See `docs/build-and-test.md` for setup and device validation.

Debug: `app/build/outputs/apk/debug/app-debug.apk`.
Release: `artifacts/reading-adventure-release.apk` after signing. The local release key and random password are under `.signing/`. Back them up privately to keep future updates compatible; never commit them. This is a locally signed development release, not a Play Store publication.

## Source map

- `GameModel.kt`: pure letter matching, progression, animation curves, audio generation tokens.
- `GameViewModel.kt`: game transitions and persistent local progress.
- `MainActivity.kt`: Compose menus and controls, Canvas characters, gestures and animations.
- `SpeechEngine.kt`: replaceable voice interface, offline Android TTS, tone feedback.
- `Content.kt`: validated, extensible JSON content packs loaded off the main thread.
- `app/src/main/assets/content/core.json`: 108 original words and example sentences.
- `scripts/create_content.py`: reproducible content authoring source.
- `app/src/test`: matching, progression, audio state, motion, content and Robolectric UI tests.
- `app/src/androidTest`: device UI smoke tests.

Original character shapes and animation logic are in Kotlin Canvas drawing code; the original launcher icon is a local Android vector drawable. No source code, graphics, animation tracks or recordings from Endless Reader are bundled.

## Reports

- [Development plan](docs/PLAN.md)
- [Environment discovery](docs/environment.md)
- [Reference app investigation](docs/reverse-engineering.md)
- [Gameplay evidence and independent specification](docs/gameplay-analysis.md)
- [Implementation architecture](docs/architecture.md)
- [Build and test instructions](docs/build-and-test.md)
- [Validation results](docs/test-results.md)

Reference-app observations are separated from design choices. Test results document what was actually run and any remaining limitations; successful compilation alone does not establish device performance or speech quality.
