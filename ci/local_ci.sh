#! /bin/bash

# mac
fswatch app/src app/build.gradle libsrc/podcast_parser/src | grep --line-buffered -v '\.\#' | tr '\n' ' ' | xargs -n1 -I{} sh -c "echo {}; ./gradlew installDebug; echo ------"
