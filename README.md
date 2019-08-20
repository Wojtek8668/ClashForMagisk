# Clash for Magisk

A rule-based tunnel in Go. This module is wrapper for [clash](https://github.com/Dreamacro/clash) 

[中文说明](README_zh.md)

## Requirements

* arm64 ABI

## Feature

See also https://github.com/Dreamacro/clash

## Configure

Data Path  `/sdcard/Android/data/com.github.kr328.clash`

In data directory

* Clash configure file `config.yaml`
* Clash GEOIP database `Country.mmdb`
* Clash starter configure file `starter.yaml`
* Clash status file `RUNNING` or `STOPPED`

## Control

Data Path  `/sdcard/Android/data/com.github.kr328.clash`

Create the following file to control clash

* `START` - Start clash if stopped
* `STOP` - Stop clash if running
* `RESTART` - Restart clash 



## Build

1. Install JDK ,Gradle ,Android SDK ,Android NDK  

2. Create `local.properties` on project root directory  
   ```properties
   sdk.dir=/path/to/android-sdk
   ndk.dir=/path/to/android-ndk
   cmake.dir=/path/to/android-cmake/*version*
   ```

3. Run command   
   ```bash
   ./gradlew build
   ```

4. Pick clash-for-magisk.zip from module/build/outputs  
