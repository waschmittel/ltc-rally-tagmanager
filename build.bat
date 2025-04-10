@echo off
:: use "call" because mvn itself is a cmd/bat file which would cause this script to stop after mvn
call mvn clean package
cd target
md package
move tagmanager.jar package
cd package
jpackage ^
    --name "LTC Rallye Tag Manager" ^
    --input . --icon ../../res/icon/app-icon.png ^
    --main-jar tagmanager.jar ^
    --about-url "https://github.com/waschmittel/ltc-rally-tagmanager" ^
    --copyright "(C) 2025 Daniel Flassak" ^
    --description "Client for LTC Rallye Software for assigning NFC tags and counting laps." ^
    --win-menu ^
    --vendor "Flubba" ^
    --win-per-user-install ^
    --type "exe" ^
    --app-version "3.14"
