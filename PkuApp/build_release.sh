#!/bin/bash -xv

export ANDROID_HOME=$ANDROID_SDK

buildTypes=\
"standard_normal_debug,target_23_normal_debug,standard_plus_debug,standard_normal_release,target_23_normal_release,standard_plus_release"

SHELL_PATH="./buildConfig/buildScript/"

chmod +x $SHELL_PATH/build_apk.sh
$SHELL_PATH/build_apk.sh ${buildTypes[@]}

aaptBin=$ANDROID_HOME/build-tools/`ls $ANDROID_HOME/build-tools | tail -n 1`/aapt
apkInfo=`$aaptBin d badging bin/*standard_normal_release.apk`
versionName=`echo $apkInfo | grep versionName | sed "s/.*versionName='\([^']*\)'.*/\1/"`
versionCode=`echo $apkInfo | grep versionCode | sed "s/.*versionCode='\([0-9]*\)'.*/\1/"`

tagsSvnPath="http://bj-scm.tencent.com/web/web_qqnews_rep/qnReading_Android_proj/tags/TencentReading_v$versionName"
apkBackupSvnPath="http://bj-scm.tencent.com/web/web_qqnews_rep/qnReading_Android_proj/document/apkbackup/TencentReading_v$versionName/"
svnOptions="--username ${P_USERNAME} --password ${P_PASSWORD} --no-auth-cache --non-interactive --trust-server-cert"
svnURL=`svn info  $svnOptions | grep ^URL: | cut -f2 -d " "`
log=`svn log -l 1 $svnOptions | sed -e 's/-//g' | sed -e 's/|//g' | tr -d '\n'`
oldApk=$SVN_URL/buildConfig/patch/patchConfigs/standard_normal_release_for_patch/old.apk
releaseConfig=$SVN_URL/buildConfig/patch/patchConfigs/standard_normal_release_for_patch/release_config.zip
oldApk23=$SVN_URL/buildConfig/patch/patchConfigs/target_23_normal_release_for_patch/old.apk
releaseConfig23=$SVN_URL/buildConfig/patch/patchConfigs/target_23_normal_release_for_patch/release_config.zip

### update old apk, releaseConfig.zip
svn log -l 1 $svnOptions $oldApk
if  [ $? = 0 ]
then
    svn delete -m "auto delete oldapk, version:$versionCode,versionName:$versionName" $svnOptions $oldApk
fi
svn import -m "auto update old apk" $svnOptions bin/*standard_normal_release.apk $oldApk

svn log -l 1 $svnOptions $releaseConfig
if  [ $? = 0 ]
then
    svn delete -m "auto delete old releaseConfig, version:$versionCode,versionName:$versionName" $svnOptions $releaseConfig
fi
svn import -m "auto update patch config" $svnOptions bin/standard_normal_release_config.zip $releaseConfig

### update old target 23 apk, releaseConfig.zip
svn log -l 1 $svnOptions $oldApk23
if  [ $? = 0 ]
then
    svn delete -m "auto delete oldapk, target23 version:$versionCode,versionName:$versionName" $svnOptions $oldApk23
fi
svn import -m "auto update old apk target23" $svnOptions bin/*target_23_normal_release.apk $oldApk23

svn log -l 1 $svnOptions $releaseConfig
if  [ $? = 0 ]
then
    svn delete -m "auto delete old releaseConfig, target23 version:$versionCode,versionName:$versionName" $svnOptions $releaseConfig23
fi
svn import -m "auto update patch config target 23" $svnOptions bin/target_23_normal_release_config.zip $releaseConfig23



## backup release apk, releaseConfig.zip
svn log -l 1 $svnOptions $apkBackupSvnPath
if [ $? = 0 ]
then
   svn delete -m "auto delete old backup release apk, version:$versionCode,versionName:$versionName" $svnOptions $apkBackupSvnPath
else
   echo "not exist $apkBackupSvnPath"
fi
svn import -m "auto backup release apk, version:$versionCode,versionName:$versionName\n $log" $svnOptions bin/ $apkBackupSvnPath

### create tags
svn log -l 1 $svnOptions $tagsSvnPath
if [ $? = 0 ]
then
   svn delete -m "auto delete old tags, version:$versionCode,versionName:$versionName" $svnOptions $tagsSvnPath
else
   echo "not exist $tagsSvnPath"
fi
svn copy -m "auto backup tag version:$versionCode,versionName:$versionName\n $log" $svnOptions $svnURL $tagsSvnPath