#!/usr/bin/env bash

mvn clean package

# TODO: see if there is a mvn plugin for jpackage
# TODO: package only with the required modules
# TODO: set the dock name and icon somehow
# TODO: set the about dialog content
cd target
jpackage --name "LTC Rallye Tag Manager" --input . --main-jar tagmanager.jar
