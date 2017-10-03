#!/bin/bash

buildTypes=\
"standard_normal_debug,standard_normal_release"

SHELL_PATH="./buildConfig/buildScript/"

chmod +x $SHELL_PATH/build_apk.sh
$SHELL_PATH/build_apk.sh ${buildTypes[@]}