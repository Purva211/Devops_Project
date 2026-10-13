# Multi-stage Dockerfile for Devops_Project
FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Compile classes, run all unit tests, and package the WAR file
RUN mvn clean test package

# Runtime stage: Apache Tomcat 9
FROM tomcat:9.0-jre8-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/LibraryManagementSystem.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=build /app/target/LibraryManagementSystem.war /usr/local/tomcat/webapps/LibraryManagementSystem.war

ENV DB_HOST=mysql \
    DB_PORT=3306 \
    DB_NAME=library_db \
    DB_USER=root \
    DB_PASSWORD=root

EXPOSE 8080

CMD ["catalina.sh", "run"]
