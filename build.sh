#!/usr/bin/env bash

mvn clean package

# TODO: see if there is a mvn plugin for jpackage
# TODO: set the dock name and icon somehow
cd target
jpackage --name "LTC Rallye Tag Manager" --input . --main-jar tagmanager.jar
