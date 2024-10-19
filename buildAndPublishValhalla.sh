#! /bin/env bash

./gradlew publishToMavenLocal -PvalhallaHome="/home/sam/code/openjdk/valhalla/build/linux-x86_64-server-release/images/jdk/" -x check -x javadoc -x doxygen -x generateJavaDocs -x :docs:publishJavaPublicationToMavenLocal
