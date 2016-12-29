#!/bin/bash

# raise an error if any command fails!
set -e

# existance of this file indicates that all dependencies were previously installed, and any changes to this file will use a different filename.
INITIALIZATION_FILE="$ANDROID_HOME/.initialized-dependencies-$(git log -n 1 --format=%h -- $0)"

if [ ! -e ${INITIALIZATION_FILE} ]; then
    # fetch and initialize $ANDROID_HOME
    download-android

    echo y | android update sdk --no-ui --filter android-10,android-16,android-24,android-25 # > /dev/null
    echo y | android update sdk --no-ui --filter tools,platform-tools > /dev/null
    echo y | android update sdk --no-ui --all --filter build-tools-25.0.2 # > /dev/null
    echo y | android update sdk --no-ui --all --filter build-tools-24.0.3 # > /dev/null
    echo y | android update sdk --no-ui --all --filter extra-android-support > /dev/null
    echo y | android update sdk --no-ui --filter extra-android-m2repository > /dev/null

    echo y | android update sdk --no-ui --all --filter sys-img-armeabi-v7a-android-10 > /dev/null
    echo y | android update sdk --no-ui --all --filter sys-img-armeabi-v7a-android-16 > /dev/null
    echo y | android update sdk --no-ui --all --filter sys-img-armeabi-v7a-android-24 > /dev/null

    ## give up
    #sudo yum update
    #sudo yum install -y glibc.i686
    #curl -L ci/spoon-runner.jar
    touch ${INITIALIZATION_FILE}
fi

## to merge report
sudo pip install lxml libpulse0
