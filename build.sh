#!/usr/bin/env bash

# set -x # set this for debugging to print all called commands
set -e # fail non non-zero exit status
set -u # fail on unset variable
set -o pipefail # fail if a pipe fails

mvn clean package

cd target
jpackage \
    --name "LTC Rallye Tag Manager" \
    --input . --icon ../res/icon/app-icon.icns \
    --main-jar tagmanager.jar \
    --about-url 'https://github.com/waschmittel/ltc-rally-tagmanager' \
    --copyright '(C) 2024 Daniel Flassak' \
    --description 'Client for LTC Rallye Software for assigning NFC tags and counting laps.' \
    --vendor 'Flubba' \
    --app-version '3.141592'
