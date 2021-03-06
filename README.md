Android GUI for PrefixLinux
============================

This is a fork of botbrew-gui (https://github.com/jyio/botbrew-gui) application
for the need of PrefixLinux-Android project. Main changes are
re-implementing support for opkg as the package manager and making it run without
root privileges. Ideally, it would be nice to have support for both dpkg and opkg,
and both root and non-root, but botbrew-gui codebase is not really well layered
and written to support that without major refactorings. So rather, this is a fork,
which tears off complicated code from botbrew-gui to make it do one thing in
more simple and clean way.

Note that I currently build application using provided build.xml (which includes
some customizations to handle assets in sane way), so building using Maven as
described below may or may not work. It's recommended to install dependent
libraries manually and use build.xml for now.


Original content from botbrew-gui:

Dependencies
============

- Android Support Library from http://developer.android.com/tools/extras/support-library.html
- ActionBarSherlock from https://github.com/JakeWharton/ActionBarSherlock/
- ViewPagerIndicator from https://github.com/JakeWharton/Android-ViewPagerIndicator/
- ACRA from http://code.google.com/p/acra/
- emulatorview from https://github.com/jackpal/Android-Terminal-Emulator/

Building
========

```
   ./external.sh
   ./mkjni.sh
   mvn clean install
```
