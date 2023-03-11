#!/usr/bin/env bash

# set -x # set this for debugging to print all called commands
set -e # fail non non-zero exit status
set -u # fail on unset variable
set -o pipefail # fail if a pipe fails

mvn clean package

# TODO: see if there is a mvn plugin for jpackage
# TODO: set the dock name and icon somehow
cd target
jpackage --name "LTC Rallye Tag Manager" --input . --main-jar tagmanager.jar
