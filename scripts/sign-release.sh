#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
export JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home}"
export READING_STORE_PASSWORD="$(cat .signing/password)"
if [ ! -f .signing/reading-adventure.p12 ]; then
    "$JAVA_HOME/bin/keytool" -genkeypair -keystore .signing/reading-adventure.p12 -storetype PKCS12 -storepass:env READING_STORE_PASSWORD -keypass:env READING_STORE_PASSWORD -alias reading-adventure -keyalg RSA -keysize 3072 -validity 10000 -dname 'CN=Reading Adventure Local Development' -noprompt
    chmod 600 .signing/reading-adventure.p12
fi
./scripts/gradle.sh :app:assembleRelease --console=plain
cp app/build/outputs/apk/release/app-release.apk artifacts/reading-adventure-release.apk
.tools/android-sdk/build-tools/35.0.0/apksigner verify --verbose --print-certs artifacts/reading-adventure-release.apk
shasum -a 256 artifacts/reading-adventure-release.apk
