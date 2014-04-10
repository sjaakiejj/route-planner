#!/bin/sh

cd ..
mvn clean
mvn package
cp target/route-planner-1.0-SNAPSHOT-jar-with-dependencies.jar .
cd web_gui
meteor
