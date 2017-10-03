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
