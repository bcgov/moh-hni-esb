# Base image docker pull jenkins/jenkins:lts-jdk11
FROM jenkins/jenkins:lts-jdk11
# if we want to install via apt
USER root

RUN apt-get update && apt-get install -y sudo \
maven 
ENV PATH=$PATH:/usr/share/maven
COPY settings.xml /usr/share/maven/conf
RUN mkdir -p /tmp/repository
RUN mkdir -p /tmp/bin
COPY oc.tar /tmp/bin
WORKDIR /tmp/bin
RUN tar -xf oc.tar
ENV PATH=$PATH:/tmp/bin

WORKDIR /
# drop back to the regular jenkins user - good practice and for access to folder for jenkins user
USER jenkins
