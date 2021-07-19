# Build the application first using Maven
FROM maven:3.8-openjdk-11 as build
WORKDIR /app
COPY . .
WORKDIR /app/hn-common
RUN mvn install
WORKDIR /app/hnsecure
RUN mvn install

# Inject the JAR file into a new container to keep the file small
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /tmp
COPY --from=build /app/hnsecure/target/hni-esb.jar /tmp/hni-esb.jar
Run mkdir -p keystore
COPY /hnsecure/src/main/resources/keystore/CGI-HNI-DEV.pfx /tmp/keystore
EXPOSE 14885
CMD ["java","-jar","hni-esb.jar"]

#Old docker file
#FROM adoptopenjdk:11-jre-hotspot

#Create application and logs folder
#we need to mount log folder with the pod
#Can not create a log folder in the image due to root user restrictions in OC4


#Copy hns-esb jar from target folder
#COPY /hnsecure/target/hni-esb.jar /tmp

#Setting environment variables
#We are using secrets for PHARMANET environment variables
#The YAML file containing secrets command is checked into SVN

#we moved hns-esb jar to tmp folder. So setting the work dir as tmp coz
#WORKDIR /tmp

#Make keystore dir in tmp
#Run mkdir -p keystore
#COPY /hnsecure/src/main/resources/keystore/CGI-HNI-DEV.pfx /tmp/keystore


#Expose is for documenting purpose. This is added for deployment support to inform the team that they will need to map this port number for access to application
#This does not expose the port in container after image is run. While running ops team has to pass the configuration to map the port
#For ex: docker run -p 14885:8080 <image name> . This will map the container's port 14885 to host's port 8080

#EXPOSE 14885

#Start HNI-ESB
#CMD ["java","-jar","hni-esb.jar"]
