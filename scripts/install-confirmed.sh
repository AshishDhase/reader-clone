#!/bin/sh
# Run only after explicit user approval for the selected physical device.
set -eu
cd "$(dirname "$0")/.."
if [ "$#" -ne 2 ] || [ "$2" != '--confirmed' ]; then
    echo 'Usage: scripts/install-confirmed.sh DEVICE_SERIAL --confirmed'
    echo 'Obtain explicit device-installation permission first.'
    exit 2
fi
ADB=.tools/android-sdk/platform-tools/adb
"$ADB" -s "$1" install -r artifacts/reading-adventure-release.apk
"$ADB" -s "$1" shell am start -n com.learning.readingadventure/.MainActivity
