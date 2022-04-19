@echo off
mvn clean package
cd target
jpackage --name "LTC Rallye Tagmanager" --input . --main-jar tagmanager.jar --type app-image
