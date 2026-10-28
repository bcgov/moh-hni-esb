# Add code for maven build
FROM maven:3.9-eclipse-temurin-11 AS build-stage
COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean install

FROM eclipse-temurin:11-jre

WORKDIR /tmp

COPY --from=build-stage /home/app/target/*.jar /tmp/mock-endpoint.jar
ENTRYPOINT ["java","-jar","/tmp/mock-endpoint.jar"]

EXPOSE 8443