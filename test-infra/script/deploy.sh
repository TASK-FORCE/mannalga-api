#!/bin/bash
sh ./shutdown.sh

rm -f ./app.jar

mv ./temp/app.jar ./app.jar

sh ./startup.sh

rm -f ./temp/app.jar