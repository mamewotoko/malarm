# malarm - Good-night & Good-morning with your favorite tunes
## What is this?

This is a simple alarm application of android (version 2.2.x - 2.3.3, 4.x).

## Overview
                sleep tunes (1h)                   wake-up tunes w vibration
                                                   application starts
     Alarm set ------------------> ..... Alarm time --------------->
     press "set alarm" button                      display google calendar/Gmail etc..
     

 If you want to stop vibration only, please use "stop vibration" menu

## Screenshot
old UI ...
![Japanese screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_ja.png)
![English screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_en.png)
![Preference](https://github.com/mamewotoko/malarm/raw/master/doc/malarm_pref.png)

## Help page
Japanese:
http://mamewotoko.github.com/malarm/doc/index.html

English:
http://mamewotoko.github.com/malarm/doc/index_en.html

## Google Play
https://play.google.com/store/apps/details?id=com.mamewo.malarm24

## Github
https://github.com/mamewotoko/malarm

## How to build & run
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
- Xperia acro
-- Android 2.3.4 (API level 10)
-- 480x854 (WVGA+) screen
- Android 2.1 emulator (API level 7)
- Android 2.2 emulator (API level 8)
- Android 4.0 emulator

## TODO
- implement music player as Service to play long time
- change webview into photo view, movie player...
- add UI to play ringtone on VolumePreference to check volume
- support podcast
- support HVGA, WVGA, WVGA+ resolution
-- http://developer.android.com/resources/dashboard/screens.html
-- hide time picker after malarm is set? (or count down?)
-- scale of web contents
- localize: French, Chinese
- add mode to display alert dialog if device is not charged when alarm is set
- improve UI to edit playlist
- refactor class design, activity, player and playlist
- show notification while playing music
- fix UI update bug when sleep timer expires (broadcast -> activity)
- conditional playlist
-- Sunday, holiday playlist etc...
- make COOL widget to set alarm (make clock widget?)
- add more test cases
-- double touch of webview
-- label text is updated when music is stopped or alarm is canceled
-- check default config value
- fix vibrator timing
- add source of malarm_test into this repository
- refactor code to clear browser history
- Design for tablets
- use Actionbar ?
- record wakeup time?
- support shuffle?
- fix bug when native player is used....

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

or install app using ant
    ant installd

### Automated UI Testing
There is a GUI automated test using Robotium
https://github.com/mamewotoko/malarm_test

### Memo:
- MultiListPreference is based on the following web page
http://blog.350nice.com/wp/archives/240
- Alarm application starts when user push appwidget of malarm.
- Dropsync is a useful Android app!
- To use @Overrides annotation, use following eclipse setting 
  org.eclipse.jdt.core.compiler.compliance=1.6

----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/
