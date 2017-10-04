#!/bin/bash -xv

export ANDROID_HOME=$ANDROID_SDK

cd ./buildConfig/patch/

DIFF="./bin/bsdiff"
OLDDEX="temp/olddex"
NEWDEX="temp/newdex"
OLDRES="temp/oldres"
NEWRES="temp/newres"
OUPUT="temp/outputs"
TARGET="../../bin"


buildTypes=$1
configType=$2
buildTypes=${buildTypes//,/ }

aaptBin=$ANDROID_HOME/build-tools/`ls $ANDROID_HOME/build-tools | tail -n 1`/aapt

for type in ${buildTypes[@]}
do
    _VERSION="../../app/build/outputs/patch/$type/version.txt"
    patchVersion=`sed -n 1p $_VERSION`
    patchVersionName=`sed -n 2p $_VERSION`

    mkdir -p $OLDDEX
    mkdir  -p $NEWDEX
    mkdir -p $OLDRES
    mkdir -p $NEWRES
    mkdir -p $OUPUT

    #######res 文件diff
    rm -rf $OLDRES/*
    rm -rf $NEWRES/*

    cp $TARGET/*.apk $NEWRES/new.apk
    cp ./patchConfigs/${type}/*.apk $OLDRES/old.apk

    # 解压新的apk
    unzip $NEWRES/*.apk -d $NEWRES/tmp/
    # 删除dex文件
    rm -f $NEWRES/tmp/classes*
    # 删除签名文件
    rm -rf $NEWRES/tmp/META-INF/
    # 删除manifest文件
    rm -rf $NEWRES/tmp/AndroidManifest.xml
    # 删除dex.ini文件，避免触发重新打包逻辑
    rm -rf $NEWRES/tmp/assets/dexes.ini
    # 删除channel文件，固定渠道号
    rm -rf $NEWRES/tmp/assets/channel

    # 解压新的apk
    unzip $OLDRES/*.apk -d $OLDRES/tmp/
    # 删除dex文件
    rm -f $OLDRES/tmp/classes*
    # 删除签名文件
    rm -rf $OLDRES/tmp/META-INF/

    # 查找所有diff
    diff -qr $NEWRES/tmp $OLDRES/tmp > $OUPUT/${buildType}_diff.tmp

    # 查找全新添加的文件
    grep "Only in $NEWRES/tmp" $OUPUT/${buildType}_diff.tmp | sed "s/:[ ]/\//" | cut -f3 -d " " > $OUPUT/${buildType}_add

    # 查找修改的文件
    grep "Files $NEWRES/tmp" $OUPUT/${buildType}_diff.tmp | cut -f2 -d " " > $OUPUT/${buildType}_modify

    # 合并文件
    cat $OUPUT/${buildType}_modify >> $OUPUT/${buildType}_add

    if [ -f $OUPUT/${buildType}_res_diff.zip ];then
    	rm -f $OUPUT/${buildType}_res_diff.zip
    fi
    # 遍历合并后的文件,逐个打入zip
    cat $OUPUT/${buildType}_add | while read line
    	do zip -r $OUPUT/${buildType}_res_diff.zip $line
    done

    #将文件目录筛选copy到patchdiff.zip中
    unzip $OUPUT/${buildType}_res_diff.zip -d $OUPUT/
    cd $OUPUT/$NEWRES/tmp/
    zip -r patchdiff.zip .
    #回到之前的目录
    cd -
    cp $OUPUT/$NEWRES/tmp/patchdiff.zip  $TARGET/


    #######class dex替换
    rm -rf $OLDDEX/*
    rm -rf  $NEWDEX/*

    cp $TARGET/*.apk $NEWDEX/new.apk
    cp ./patchConfigs/${type}/*.apk $OLDDEX/old.apk
    unzip ./$OLDDEX/*.apk "*.dex" -d ./$OLDDEX
    unzip ./$NEWDEX/*.apk "*.dex" -d ./$NEWDEX

    baseVersionName=`$aaptBin d badging ./$OLDDEX/*.apk | grep versionName | sed "s/.*versionName='\([^']*\)'.*/\1/"`
    baseVersionCode=`$aaptBin d badging ./$OLDDEX/*.apk | grep versionCode | sed "s/.*versionCode='\([0-9]*\)'.*/\1/"`

    chmod +x $DIFF

    for file_new in ./$NEWDEX/*.dex
    do
        file_old=./$OLDDEX/$(basename $file_new)
        if [ -f ${file_old} ];then
           $DIFF ${file_old} ${file_new} ./$OUPUT/$(basename $file_new .dex).patch &
        fi
    done
    wait

    zip -j -r $TARGET/patchdiff.zip $OUPUT/*.patch

    url="replace_url"

    newDexStr="\"new\":["
    oldDexStr="\"old\":["

    for dex_new in ./$NEWDEX/*.dex
    do
        newDexMd5=`md5sum ${dex_new} | cut -f1 -d' ' | openssl rsautl -sign -inkey ./privateKey/keyn.txt | base64 | tr -d '\n'`
        newDexStr=$newDexStr"{\"name\":\"$(basename $dex_new)\",\"md5\":\"$newDexMd5\"},"
    done
    #删除最后一个多余的,
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

    patchZipMd5=`md5sum $TARGET/patchdiff.zip | cut -f1 -d' '`
    patchZipSize=`du -h $TARGET/patchdiff.zip | cut -f1`

    echo "{\"ret\":0,\"url\":\"$url\","$newDexStr","$oldDexStr",""\"diffMethod\":1,\"patchType\":2,\"isForceInstall\":true,"\
    "\"version\":\"$patchVersion\",\"versionName\":\"$patchVersionName\",\"open\":1,\"patchPackageMd5\":\"$patchZipMd5\",\"size\":$patchZipSize}" >  $TARGET/patch.json

done