#! /bin/bash

./gradlew --daemon

MYDIR=$(dirname $(realpath "$0"))

#TODO: build only if no device is connected
fswatch app/src app/build.gradle libsrc/podcast_parser/src | grep --line-buffered -v '\.\#' | tr -u '\n' ' ' | \
    xargs -n1 -I{} sh -c "echo {}; ./gradlew --console plain --offline lint installDebug assembleAndroidTest && adb shell am start com.mamewo.malarm24/.MalarmActivity; echo ------"   
#python $MYDIR/monitor_for_ci.py sh -c "cat > /dev/null; ./gradlew --offline installDebug assembleAndroidTest && adb shell am start com.mamewo.malarm24/.MalarmActivity; echo ------"
