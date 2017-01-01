#! /bin/sh
# ANDROID_HOME and PATH should be set

echo y | android update sdk --no-ui --all --filter android-10,android-16,android-24,android-25 # > /dev/null
echo y | android update sdk --no-ui --filter tools,platform-tools > /dev/null
echo y | android update sdk --no-ui --filter build-tools-25.1.0 # > /dev/null
echo y | android update sdk --no-ui --all --filter build-tools-24.0.3 # > /dev/null
echo y | android update sdk --no-ui --all --filter extra-android-m2repository > /dev/null

echo y | android update sdk --no-ui --all --filter sys-img-armeabi-v7a-android-10
echo y | android update sdk --no-ui --all --filter sys-img-armeabi-v7a-android-16
echo y | android update sdk --no-ui --all --filter sys-img-armeabi-v7a-android-24
