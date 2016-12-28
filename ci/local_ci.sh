#! /bin/bash

./gradlew --daemon

#TODO: build only if no device is connected
fswatch app/src app/build.gradle libsrc/podcast_parser/src | grep --line-buffered -v '\.\#' | tr -u '\n' ' ' | xargs -n1 -I{} sh -c "echo {}; ./gradlew --offline installDebug && adb shell am start com.mamewo.malarm24/.MalarmActivity; echo ------"
