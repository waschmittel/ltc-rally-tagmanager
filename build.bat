@echo off
mvn clean package
cd target
md package
move tagmanager.jar package
jpackage --name "LTC Rallye Tagmanager" --input package --main-jar tagmanager.jar --type app-image
