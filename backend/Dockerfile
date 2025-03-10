FROM tomcat:10.1-jdk17-openjdk

# Set the working directory
WORKDIR /usr/local/tomcat/webapps

# Copy the WAR file from the target directory
COPY target/leanx-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

# Expose the Tomcat server port (default is 8080)
EXPOSE 8080

ARG SECRET_KEY
ARG RDS_MYSQL_ENDPOINT
ARG RDS_MYSQL_PORT
ARG RDS_MYSQL_USER
ARG RDS_MYSQL_PASSWORD

ENV SECRET_KEY=$SECRET_KEY
ENV RDS_MYSQL_ENDPOINT=$RDS_MYSQL_ENDPOINT
ENV RDS_MYSQL_PORT=$RDS_MYSQL_PORT
ENV RDS_MYSQL_USER=$RDS_MYSQL_USER
ENV RDS_MYSQL_PASSWORD=$RDS_MYSQL_PASSWORD

# Start Tomcat server
CMD ["catalina.sh", "run"]