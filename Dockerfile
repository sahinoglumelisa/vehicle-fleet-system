FROM openjdk:17-jdk-slim
LABEL authors="deniz"

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]