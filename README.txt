## What is this?

This is a simple alarm application of android (version 2.3.3).

## OVERVIEW
            sleep music (1h)                     wake-up music w/ vibration
                                                 application starts
 Alarm set ------------------> ..... Alarm time --------------->
 (press "set/stop alarm"
  button)

 If you want to stop vibration only, please use "stop vibraiton" menu
 This application has appwidget, but it has no function for now.... very simple!

## HOW TO RUN:
1. Rename Playlist_tmpl.java into Playlist.java and put it into src/com/mamewo/hello directory.
2. prepare playlist by writing Playlist class in following way "a" or "b". Playlist_tmpl.java is a sample file.
a) Fill Playlist.WAKEUP_PLAYLIST and Playlist.SLEEP_PLAYLIST by music
  filename in "/sdcard/music/" folder
OR
b) put m3u file, which lists one music filename as one line and put them as "wakeup.m3u" and
"sleep.m3u" into "/sdcard/music/" folder
3. Build and run on your target device

## TODO (bugs?):
- implement music player as Service to play long time
-- or use native player
- editable playlist support
- use au music player?
- make COOL widget to set alarm
- add option not to play sleep music?
- add stop music into resource
- change vibration timing
- preset volume of sleep / wakeup mode.
- add license file of this source
- fix vibration bug (onNewIntent is called after unlocking device?)
- implement more smart scroll
- link to music store (where?)

## APPENDIX
### BUILD FROM COMMAND LINE
1. Create local.properties file and set sdk.dir property to location where you installed android SDK

sdk.dir=<path to android SDK>

2. In project top directory execute following command

ant debug

3. malarm-cmd.apk will be created in bin directory, if successed

### START EMULATOR AND INSTALL APK FROM COMMAND LINE
1. 
emulator -avd <avdname>

2.
adb install bin/malarm-cmd.apk

(build.xml and *.property files are created by android create command)

### MEMO:
Alarm application starts when user push appwidget.
After set/stop alarm button
   Play sleep music one hour
   Play alarm music at specified time

----
Takashi Masuyama <mamewotoko@gmail.com>
http://www002.upp.so-net.ne.jp/mamewo/
