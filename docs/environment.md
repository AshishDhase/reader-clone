# Development environment

Inspected on 2026-10-19. All project dependencies described here are local to this workspace unless an existing installation path is stated. No global software installation was performed for this project. User authorization for local Android-tool downloads and the APKMirror static-analysis attempt was obtained separately.

## Verified host and toolchain

| Component | Evidence from this run |
|---|---|
| Host | Apple Silicon `arm64`; macOS 15.6.1, build 24G90 (`uname`, `sw_vers`) |
| JDK | Existing Temurin OpenJDK 17.0.8+7 at `/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home` |
| REA | Existing `rea-agents` 4.0.1 in npm cache; direct CLI help/doctor executed |
| Node for REA | 25.9.0, reported healthy by REA doctor |
| Hopper | Existing app found at `/Users/ashishdhase/Applications/Hopper Disassembler.app`; not launched against an original APK |
| Ghidra | Not configured; optional native-analysis provider |
| Gradle | Local `.tools/gradle-8.11.1`; launcher JAR identifies 8.11.1 |
| Android Gradle Plugin | Project pins 8.9.2 |
| Kotlin / Compose compiler plugin | Project pins 2.0.21 |
| Android SDK | Local `.tools/android-sdk` |
| SDK command-line tools | Local `cmdline-tools/latest`; installed metadata revision 12.0 |
| Android platform | API 35, Android 15, installed platform revision 2 |
| Build tools | 35.0.0, verified from installed `source.properties` |
| ADB | 1.0.41; platform-tools version 37.0.1-15733141; Darwin arm64 |
| JADX / APKTool / FFmpeg | Not found by `command -v` in the review shell; not required to compile this procedural-art/TTS app |
| System Gradle | Not found by `command -v`; local Gradle is used |

The full REA capability/readiness assessment and actual supported CLI syntax are in [reverse-engineering.md](reverse-engineering.md). Ghidra is not a required implementation dependency. JADX/APKTool would be complementary for a lawfully supplied original APK; there was no reason to install them after acquisition was blocked. FFmpeg is unnecessary because this implementation generates no video or recorded-audio assets.

## Local build entry point

`scripts/gradle.sh` resolves the project root, chooses the existing JDK 17 unless `JAVA_HOME` is supplied, and sets local Gradle/Android state directories. It then invokes the pinned local Gradle distribution. The project uses Google's Maven repository and Maven Central for dependencies, plus the Gradle plugin repository for plugins.

```sh
./scripts/gradle.sh :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
```

This is a reproducible invocation, **not a statement that the running build/test tasks passed**. Consult the latest build log and generated reports. A fresh checkout requires an Android SDK, JDK 17 and Gradle 8.11.1 installed at the documented paths or equivalent script configuration; `.tools/` is ignored by Git. The script is a local launcher, not a Gradle Wrapper distribution downloader.

Release configuration reads `READING_STORE_PASSWORD` and expects `.signing/reading-adventure.p12`, alias `reading-adventure`. Secrets and signing material are local and ignored. A release assembled without signing configuration may be unsigned; verify the actual output with `apksigner` before describing it as installable signed release. Keep the release key if future APKs must update an installed build.

## Isolation and device boundaries

`.tools/`, `.gradle/`, `local.properties`, build outputs, `.signing/` and `analysis-inputs/` are ignored. Original application analysis inputs are separate from `app/src/main/assets` and are never packaging inputs. The approved APKMirror request returned HTTP 403 and left `analysis-inputs/` empty.

ADB's executable availability is verified; this does not establish that a Galaxy S23 Ultra is connected, authorized or compatible with the final binary. Device detection, install confirmation, installation, launch, log collection and performance measurement are separate steps. No physical-device performance evidence is claimed in this document.
