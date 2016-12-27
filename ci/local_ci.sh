#! /bin/bash

./gradlew --daemon
fswatch -l 5 app/src app/build.gradle libsrc/podcast_parser/src | grep --line-buffered -v '\.\#' | tr -u '\n' ' ' | xargs -n1 -I{} sh -c "echo {}; ./gradlew --offline installDebug; echo ------"
