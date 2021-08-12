FROM adoptopenjdk:11-jre-hotspot

#Setting the work dir as tmp coz
WORKDIR /tmp

#Copy hns-esb jar from target folder
COPY /hnsecure/target/hni-esb.jar /tmp

#Make keystore dir in tmp
RUN mkdir -p keystore

#support folder for creating keystore pvc
RUN mkdir -p staging

#Expose is for documenting purpose. This is added for deployment support to inform the team that they will need to map this port number for access to application
#This does not expose the port in container after image is run. While running ops team has to pass the configuration to map the port
#For ex: docker run -p 14885:8080 <image name> . This will map the container's port 14885 to host's port 8080

EXPOSE 14885

#Start HNI-ESB
CMD ["java","-jar","hni-esb.jar"]