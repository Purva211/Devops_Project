
FROM tomcat:9.0-jdk17-temurin

COPY target/library-management.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
