# malarm - Good-morning with your favorite tunes
## What is this?

This is a simple alarm application of android (version 2.2.x - 2.3.3, 4.x).

## Overview
                sleep tunes (1h)                   wake-up tunes w vibration
                                                   application starts
     Alarm set ------------------> ..... Alarm time --------------->
     press "set alarm" button                      display google calendar/Gmail etc..
     

 If you want to stop vibration only, please use "stop vibraiton" menu
 This application has appwidget, but it has no function for now.... very simple!

## Screenshot
![Japanese screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_ja.png)
![English screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_en.png)
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
- Xperia acro made by Sony Ericsson (Android 2.3.4)
- Android 2.1 emulator
-- cannot login to twitter because of SSL error?
- Android 2.2 emulator
- Android 4.0 emulator

## TODO
- implement music player as Service to play long time
- display listbox when multiple candidates are offered by voice recognition engine
- make COOL widget to set alarm (make clock widget?)
- write help page using git pages
- fix crash bug when photo of bijo-linux is long pressed
- fix vibrator timing
- fix bug when native player is used....
- record wakeup time
- add UI to edit playlist
-- or put playlist on dropbox and sync?
- add stop.m4a into resource?
- add more test cases
- automated build check using 2.x, 4.x SDK
- change webview into photo view
- add source of malarm_test into this repository
- use multiple choice dialog to implement site preference

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
- MultiListPreference is based on the following web page
http://blog.350nice.com/wp/archives/240
- Alarm application starts when user push appwidget of malarm.
- Screen rotation and vibration.....
- Android Market
https://market.android.com/details?id=com.mamewo.malarm
----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/
