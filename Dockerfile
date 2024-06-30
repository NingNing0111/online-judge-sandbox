FROM openjdk:8-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/oj-ludd-sandbox-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]