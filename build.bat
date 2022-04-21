@echo off
:: use "call" because mvn itself is a cmd/bat file which would cause this script to stop after mvn
call mvn clean package
cd target
md package
move tagmanager.jar package
jpackage --name "LTC Rallye Tagmanager" --input package --main-jar tagmanager.jar --type app-image
