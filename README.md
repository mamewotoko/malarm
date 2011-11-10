# malarm - Good-morning with your favorite tunes
## What is this?

This is a simple alarm application of android (version 2.3.3).

## OVERVIEW
                sleep music (1h)                     wake-up music w vibration
                w sleep music                        application starts
     Alarm set ------------------> ..... Alarm time --------------->
     (press "set/stop alarm"
      button)

 If you want to stop vibration only, please use "stop vibraiton" menu
 This application has appwidget, but it has no function for now.... very simple!

## SCREENSHOT
![Japanese screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_ja.png)
![english screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_en.png)
![Preference](https://github.com/mamewotoko/malarm/raw/master/doc/malarm_pref.png)

## HOW TO RUN:
1. Put your music file into /sdcard/music directory of android device
2. Prepare m3u format play list for sleep (named sleep.m3u) and wakeup (named wakeup.m3u).
m3u file contains one music filename in one line.
3. Put playlists and stop.m4a file in /sdcard/music/ directory of android device.
4. Download apk file from 
https://github.com/mamewotoko/malarm/downloads
or Build malarm and install it on android device
5. Run malarm on android device
6. Enjoy!

## TODO:
- implement music player as Service to play long time
- add setting of music file path and m3u playlist path
- write help page
- restart music after phone call ends
- preset volume of sleep / wakeup mode
- raise wakeup volume by timer or thread
- record wakeup time
- fix vibration bug (onNewIntent is called after unlocking device?)
- switch web page by gesture (2 finger) or horizontal scroll bar?
- add UI to edit playlist
- use au music player?
- make COOL widget to set alarm (make clock widget?)
- add stop music into resource?
- implement more smart scroll
- link to music store (where?)
- add test

## TESTED DEVICE
- Xperia Acro made by Sony Ericsson

## APPENDIX
### HOW TO BUILD FROM COMMAND LINE
1. Create local.properties file and set sdk.dir property to location where you installed android SDK
    sdk.dir=<path to android SDK>
2. In project top directory execute following command
    ant debug
3. malarm-cmd.apk will be created in bin directory, if successed

### START ANDROID EMULATOR AND INSTALL APK FROM COMMAND LINE
1. start android emulator
    emulator -avd <avdname>
2. install application on android emulator
    adb install bin/malarm-cmd.apk
(build.xml and *.property files are created by android create command)

### MEMO:
Alarm application starts when user push appwidget of malarm.

----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/
