#!/bin/bash
#
# Build:
#   - git clone -b dev https://github.com/Dreamacro/clash
#   - cd clash
#   - ANDROID_NDK=/path/to/android/ndk /path/to/this/script
#

if [[ ! -d "$ANDROID_NDK" ]];then
echo "ANDROID_NDK is empty"
exit 1
fi

export GOPATH=`realpath ./gopath`

NAME=clash
BINDIR=bin
VERSION=$(git describe --tags || echo "unknown version")
BUILDTIME=$(LANG=C date -u)
ANDROID_CC=$ANDROID_NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android21-clang
ANDROID_CXX=$ANDROID_NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android21-clang++
ANDROID_LD=$ANDROID_NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android-ld

export GOARCH=arm64 
export GOOS=android 
export CXX=$ANDROID_CXX
export CC=$ANDROID_CC 
export LD=$ANDROID_LD 
export CGO_ENABLED=1

git clone -b dev "https://github.com/Dreamacro/clash"

pushd clash
git pull --force
popd

go build -ldflags "-X \"github.com/Dreamacro/clash/constant.Version=$VERSION\" -X \"github.com/Dreamacro/clash/constant.BuildTime=$BUILDTIME\" -w -s" \
            -o clash/clash-android
