#! /bin/bash

./gradlew --daemon

# build(){
#     echo $*
#     ./gradlew --offline installDebug && adb shell am start com.mamewo.malarm24/.MalarmActivity
#     echo ------
# }

# # http://stackoverflow.com/questions/26019751/asynchronously-consuming-pipe-with-bash
# flushpipe() {
#     # wait until the next line becomes available
#     read -d "" buffer
#     # consume any remaining elements — a small timeout ensures that 
#     # rapidly fired events are batched together
#     while read -d "" -t 1 line; do buffer="$buffer\n$line"; done
#     echo $buffer
# }

#TODO: build only if no device is connected
fswatch app/src app/build.gradle libsrc/podcast_parser/src | grep --line-buffered -v '\.\#' | tr -u '\n' ' ' | xargs -n1 -I{} sh -c "echo {}; ./gradlew --offline installDebug assembleAndroidTest && adb shell am start com.mamewo.malarm24/.MalarmActivity; echo ------"
