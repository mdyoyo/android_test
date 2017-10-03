#!/bin/bash -xv

export ANDROID_HOME=$ANDROID_SDK

cd ./buildConfig/patch/

DIFF="./bin/bsdiff"
OLDDEX="temp/olddex"
NEWDEX="temp/newdex"
OUPUT="temp/outputs"
TARGET="../../bin"


buildTypes=$1
buildTypes=${buildTypes//,/ }

aaptBin=$ANDROID_HOME/build-tools/`ls $ANDROID_HOME/build-tools | tail -n 1`/aapt

for type in ${buildTypes[@]}
do
    _VERSION="../../app/build/outputs/patch/$type/version.txt"
    mkdir -p $OLDDEX
    mkdir  -p $NEWDEX
    mkdir -p $OUPUT

    rm -rf $OLDDEX/*
    rm -rf  $NEWDEX/*

    cp $TARGET/*${type}.apk $NEWDEX/new.apk
    cp ./patchConfigs/${type}/*.apk $OLDDEX/old.apk
    unzip ./$OLDDEX/*.apk "*.dex" -d ./$OLDDEX
    unzip ./$NEWDEX/*.apk "*.dex" -d ./$NEWDEX

    baseVersionName=`$aaptBin d badging ./$OLDDEX/*.apk | grep versionName | sed "s/.*versionName='\([^']*\)'.*/\1/"`
    baseVersionCode=`$aaptBin d badging ./$OLDDEX/*.apk | grep versionCode | sed "s/.*versionCode='\([0-9]*\)'.*/\1/"`
    patchVersion=`sed -n 1p $_VERSION`
    patchVersionName=`sed -n 2p $_VERSION`

    chmod +x $DIFF
    for file_new in ./$NEWDEX/*.dex
    do
        file_old=./$OLDDEX/$(basename $file_new)
        if [ -f ${file_old} ];then
           $DIFF ${file_old} ${file_new} ./$OUPUT/$(basename $file_new .dex).patch &
        fi
    done
    wait

    zip -j -r $TARGET/patchdiff${BUILD_TYPE}.zip $OUPUT/*.patch

    url="replace_url"

    newDexStr="\"new\":["
    oldDexStr="\"old\":["

    for dex_new in ./$NEWDEX/*.dex
    do
        newDexMd5=`md5sum ${dex_new} | cut -f1 -d' ' | openssl rsautl -sign -inkey ./privateKey/keyn.txt | base64 | tr -d '\n'`
        newDexStr=$newDexStr"{\"name\":\"$(basename $dex_new)\",\"md5\":\"$newDexMd5\"},"
    done
    #删除最后一个多余的,  ${newDexStr::-1}
    newDexStr=${newDexStr%?}
    newDexStr=$newDexStr"]"

    for dex_old in ./$OLDDEX/*.dex
    do
        oldDexMd5=`md5sum ${dex_old} | cut -f1 -d' '`
        oldDexStr=$oldDexStr"{\"name\":\"$(basename $dex_old)\",\"md5\":\"$oldDexMd5\"},"
    done
    #删除最后一个多余的,
    oldDexStr=${oldDexStr%?}
    oldDexStr=$oldDexStr"]"



    patchZipMd5=`md5sum $TARGET/patchdiff${BUILD_TYPE}.zip | cut -f1 -d' '`
    patchZipSize=`du -h $TARGET/patchdiff${BUILD_TYPE}.zip | cut -f1`

    echo "{\"ret\":0,\"url\":\"$url\","$newDexStr","$oldDexStr",""\"diffMethod\":1,\"patchType\":1,\"isForceInstall\":true,"\
    "\"version\":\"$patchVersion\",\"versionName\":\"$patchVersionName\",\"open\":1,\"patchPackageMd5\":\"$patchZipMd5\",\"size\":$patchZipSize}" >  $TARGET/patch${BUILD_TYPE}.json

    rm -rf ./temp
done