#! /bin/bash
set -e
./gradlew --info clean assembleDebug assembleDebugAndroidTest lint
