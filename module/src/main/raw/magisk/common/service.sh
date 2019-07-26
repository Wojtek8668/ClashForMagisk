#!/system/bin/sh
# Please don't hardcode /magisk/modname/... ; instead, please use $MODDIR/...
# This will make your scripts compatible even if Magisk change its mount point in the future
MODDIR=${0%/*}

# This script will be executed in late_start service mode
# More info in the main Magisk thread

ROOT="/dev/clash___"

CORE_INTERNAL_DIR="$MODDIR/core"
TEMP_INTERNAL_DIR="$MODDIR/temp"

CORE_DIR="$ROOT/core"
DATA_DIR="/sdcard/Android/data/com.github.kr328.clash"
TEMP_DIR="$ROOT/temp"

mkdir -p "$ROOT"
mkdir -p "$CORE_DIR"
mkdir -p "$TEMP_DIR"

mount -o bind "$CORE_INTERNAL_DIR" "$CORE_DIR"
mount -o bind "$TEMP_INTERNAL_DIR" "$TEMP_DIR"

if [[ ! -f "$TEMP_DIR/Country.mmdb" ]];then
    cp "$CORE_DIR/Country.mmdb" "$TEMP_DIR/Country.mmdb"
fi

CLASSPATH="$CORE_DIR/starter.jar" "$CORE_DIR/daemonize" /system/bin/app_process /system/bin com.github.kr328.clash.Starter "$CORE_DIR" "$DATA_DIR" "$TEMP_DIR"