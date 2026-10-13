# Apache Tomcat 9 runtime
FROM tomcat:9.0-jre8-temurin

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the pre-built, tested WAR artifact produced by Jenkins Maven build
COPY target/LibraryManagementSystem.war /usr/local/tomcat/webapps/ROOT.war
COPY target/LibraryManagementSystem.war /usr/local/tomcat/webapps/LibraryManagementSystem.war

ENV DB_HOST=mysql \
    DB_PORT=3306 \
    DB_NAME=library_db \
    DB_USER=root \
    DB_PASSWORD=root

EXPOSE 8080

CMD ["catalina.sh", "run"]
