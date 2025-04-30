#!/bin/bash

cd ../../

./gradlew clean bootWar bootJar shadowJar || exit 1

cd takserver-schemamanager

../gradlew shadowJar || exit 1

java -jar build/libs/schemamanager-5.2-RELEASE-16-uber.jar upgrade || exit 1

cd ../takserver-core

../gradlew clean bootWar bootJar || exit 1

cd example/

./run_takserver.sh
