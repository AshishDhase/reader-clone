#!/bin/sh
# Read-only inspection. Does not install, launch, reset, or change settings.
set -eu
cd "$(dirname "$0")/.."
ADB=.tools/android-sdk/platform-tools/adb
"$ADB" devices -l
if [ "$#" -eq 1 ]; then
    for property in ro.product.manufacturer ro.product.model ro.build.version.release ro.build.version.sdk ro.product.cpu.abilist; do
        "$ADB" -s "$1" shell getprop "$property"
    done
fi
