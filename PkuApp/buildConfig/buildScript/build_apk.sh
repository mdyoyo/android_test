#!/bin/bash -xv

BUILD_OUTPUT_DIR="app/build/outputs/apk"

export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export GRADLE_HOME="/data/rdm/apps/gradle/gradle-3.3"
export PATH=$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH

# 输出可用的build tools版本
ls $ANDROID_HOME/build-tools

buildTypes=$1
buildTypes=${buildTypes//,/ }

nowDate=$(date +"%m_%d_%H")
BaseLine="${BaseLine//\./_}"
BaseLine="${BaseLine//qqnews/TencentNews}"
filename=${BaseLine}_${nowDate}
mkdir bin/
for i in ${buildTypes[@]}
 do
	gradle assemble${i}
    if ! [ $? = 0 ] ;then
        exit 1
    fi
	rm -f $BUILD_OUTPUT_DIR/*-unaligned.apk
	for f in `ls $BUILD_OUTPUT_DIR/`
	do
	    cp $BUILD_OUTPUT_DIR/${f} bin/${filename}_${f}
	    md5sum bin/${filename}_${f} > bin/${f}.txt
	    echo '  encrypted MD5: ' >> bin/${f}.txt
	    md5sum bin/${filename}_${f} | cut -f1 -d' ' | openssl rsautl -sign -inkey ./buildConfig/patch//privateKey/keyn.txt | base64 | tr -d '\n' >> bin/${f}.txt
	done
	zip -j bin/${i}_config.zip app/build/outputs/patch/${i}/*
done