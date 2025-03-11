FROM tomcat:10.1-jdk17-openjdk

# Set the working directory
WORKDIR /usr/local/tomcat/webapps

# Copy the WAR file from the target directory
COPY target/backend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

# Start Tomcat server
CMD ["catalina.sh", "run"]