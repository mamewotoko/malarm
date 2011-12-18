# malarm - Good-morning with your favorite tunes
## What is this?

This is a simple alarm application of android (version 2.3.3).

## Overview
                sleep music (1h)                     wake-up music w vibration
                w sleep music                        application starts
     Alarm set ------------------> ..... Alarm time --------------->
     (press "set/stop alarm"
      button)

 If you want to stop vibration only, please use "stop vibraiton" menu
 This application has appwidget, but it has no function for now.... very simple!

## Screenshot
![Japanese screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_ja.png)
![english screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_en.png)
![Preference](https://github.com/mamewotoko/malarm/raw/master/doc/malarm_pref.png)

## How to run
1. Put your music file into /sdcard/music directory of android device
2. Prepare m3u format play list for sleep (named sleep.m3u) and wakeup (named wakeup.m3u).
m3u file contains one music filename in one line.
3. Put playlists and stop.m4a file in /sdcard/music/ directory of android device.
4. Download apk file from 
https://github.com/mamewotoko/malarm/downloads
or Build malarm and install it on android device
5. Run malarm on android device
6. Enjoy!

You can change path to playlist and music files by "Playlist directory" preference screen

## Tested Device
- Xperia acro made by Sony Ericsson

## TODO
- implement music player as Service to play long time
- make COOL widget to set alarm (make clock widget?)
- write help page
- fix crash bug when photo is long pressed
- fix vibrator timing
- fix bug when native player is used....
- preset volume of sleep / wakeup mode
- record wakeup time
- add UI to edit playlist
- add stop music into resource?
- implement more smart scroll
- link to music store (where?)
- add more test cases
- use PMD and BugFinder to check source
- build check using 2.x SDK

## Appendix
### How To Build From Command Line
1. Create local.properties file and set sdk.dir property to location where you installed android SDK
    sdk.dir=<path to android SDK>
2. In project top directory execute following command
    ant debug
3. malarm-debug.apk will be created in bin directory, if successed

### Start Android Emulator And Install APK From Command Line
1. start android emulator
    emulator -avd <avdname>
2. install application on android emulator
    adb install bin/malarm-cmd.apk
(build.xml and *.property files are created by android create command)

### Automated UI Testing
There is a GUI automated test using Robotium
https://github.com/mamewotoko/malarm_test

### Memo:
Alarm application starts when user push appwidget of malarm.

----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/
