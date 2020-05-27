#!/usr/bin/env bash
./mvnw package -Dmaven.test.skip=true
nohup java -jar ./target/spring-boot-redisson-0.0.1-SNAPSHOT.jar
#nohup java -jar ./target/spring-boot-redisson-0.0.1-SNAPSHOT.jar --spring.profiles.active=node1 &
