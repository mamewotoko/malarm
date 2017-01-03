#! /bin/sh
./gradlew installDebug spoonDebug -P spoonClassName=com.mamewo.malarm_test.TestPortraitUI
./gradlew installDebug spoonDebug -P spoonClassName=com.mamewo.malarm_test.TestLandsapeUI -PspoonOutput=spoon_land
