#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
export JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home}"
export GRADLE_USER_HOME="$PWD/.tools/gradle-user-home"
export ANDROID_USER_HOME="$PWD/.tools/android-user-home"
export ANDROID_HOME="$PWD/.tools/android-sdk"
exec "$PWD/.tools/gradle-8.11.1/bin/gradle" "$@"
