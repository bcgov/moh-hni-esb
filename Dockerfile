# Base image
FROM adoptopenjdk:11-jre-hotspot

#Echo steps
RUN echo Step 1 completed

#Create application and logs folder
#we need to mount log folder with the pod
#Can not create a log folder in the image due to root user restrictions in OC4


#Copy hns-esb jar from target folder
COPY /hnsecure/target/hni-esb.jar /tmp


#Setting environment variables
#We are using secrets for PHARMANET environment variables
#The YAML file containing secrets command is checked into SVN

#we moved hns-esb jar to tmp folder. So setting the work dir as tmp coz
WORKDIR /tmp


#Start HNI-ESB
CMD ["java","-jar","hni-esb.jar"]
