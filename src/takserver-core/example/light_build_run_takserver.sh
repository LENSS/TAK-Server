#!/bin/bash

./stop_takserver.sh || exit 1

cd ../

../gradlew clean bootWar bootJar || exit 1

cd example/

./run_takserver.sh
