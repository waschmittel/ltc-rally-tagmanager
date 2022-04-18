#!/usr/bin/env bash

mvn clean package

# TODO: see if there is a mvn plugin for jpackage
cd target
jpackage --name "LTC Rallye Tag Manager" --input . --main-jar tagmanager.jar
