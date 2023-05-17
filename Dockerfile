FROM gradle:8.1.0-jdk17-alpine AS build

COPY --chown=gradle:gradle . /opt/app

WORKDIR /opt/app

RUN gradle bootJar


FROM openjdk:17.0.1-jdk-slim

ARG JAR_FILE=opt/app/build/libs/*SNAPSHOT.jar

WORKDIR /opt/app

COPY --from=build ${JAR_FILE} yandex-lavka.jar

ENTRYPOINT ["java","-jar","yandex-lavka.jar"]