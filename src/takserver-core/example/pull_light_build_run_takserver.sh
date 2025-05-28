#!/bin/bash

./stop_takserver.sh || exit 1

cd ~/TAK-Server/

# Pull from the remote repository
git pull origin main || exit 1

# Rebuild the TAK server
cd src/takserver-core
../gradlew clean bootWar bootJar || exit 1

cd example/

./run_takserver.sh
