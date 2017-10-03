#!/bin/bash

BUILD_TYPE="standard_normal_release_for_patch"
SHELL_PATH="./buildConfig/buildScript/"

chmod +x $SHELL_PATH/build_apk.sh
$SHELL_PATH/build_apk.sh  ${BUILD_TYPE[@]}

if ! [ $? = 0 ] ;then
exit 1
fi

chmod +x $SHELL_PATH//make_patch.sh
$SHELL_PATH/make_patch.sh  ${BUILD_TYPE[@]}

#versionName=`cat main/build.gradle | sed -n -e '/defaultConfig\s*{/,/}/p' | grep versionName | sed -e 's/^[ \t]*//;s/[ \t]*$//' | cut -f2 -d ' ' | sed -e 's/\"//g'`
#version=`echo $versionName | sed -e 's/\.//g'`
#tagsSvnPath="http://bj-scm.tencent.com/web/web_qqnews_rep/qnReading_Android_proj/tags/TencentReading_v${versionName}_patch"
#svnOptions="--username ${P_USERNAME} --password ${P_PASSWORD} --no-auth-cache --non-interactive --trust-server-cert"
#svnURL=`svn info  $svnOptions | grep ^URL: | cut -f2 -d " "`
#log=`svn log -l 1 $svnOptions | sed -e 's/-//g' | sed -e 's/|//g' | tr -d '\n'`


#svn log -l 1 $svnOptions $tagsSvnPath
#if [ $? = 0 ]
#then
#   svn delete -m "auto delete old tags, version:$version,versionName:$versionName" $svnOptions $tagsSvnPath
#else
#   echo "not exist $tagsSvnPath"
#fi
#svn copy -m "auto backup tag version:$version,versionName:$versionName\n $log" $svnOptions $svnURL $tagsSvnPath