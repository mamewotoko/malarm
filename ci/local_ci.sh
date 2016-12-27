#! /bin/bash

# mac
# -o: one-per-batch
fswatch  app/src app/build.gradle libsrc/podcast_parser/src | xargs -n1 -I{} sh -c "echo {}; ./gradlew installDebug"
