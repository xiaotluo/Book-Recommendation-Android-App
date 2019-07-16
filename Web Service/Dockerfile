# Pull base image 
From tomcat:alpine

# Maintainer is you - add your name and email 
MAINTAINER "Xiaotong Luo xluo2@andrew.cmu.edu"

# Copy the war to the Tomcat path
# The war is named ROOT to be the root web app 
ADD ROOT.war /usr/local/tomcat/webapps/

# Add the script to start the tomcat server 
ADD tomcat_starter.sh /home/

# Define the command which runs when the container starts
CMD chmod +x /home/tomcat_starter.sh; /home/tomcat_starter.sh