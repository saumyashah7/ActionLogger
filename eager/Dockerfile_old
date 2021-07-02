#FROM openjdk:8-jdk-alpine 
FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080
ADD target/eager-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java","-jar","app.war"]

