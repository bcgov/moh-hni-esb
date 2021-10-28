# Base image docker pull jenkins/jenkins:lts-jdk11
FROM jenkins/jenkins:lts-jdk11
# if we want to install via apt
USER root
RUN apt-get update && apt-get install -y sudo \
maven 
ENV PATH=$PATH:/usr/share/maven
COPY settings.xml /usr/share/maven/conf

RUN mkdir -p /tmp/repository

# Created .ssh folder to copy git hub deploy keys
RUN mkdir -p /.ssh
RUN chown -R 1009040000 /.ssh
# These keys are not checked in github
# When we need to create a new image of jenkins, we can get this files from SVN
# copy the files from svn and keep at same location as this docker file 
COPY id_rsa /.ssh
COPY id_rsa.pub /.ssh

# set the git config. This is required as we need to push the tags
RUN git config --global user.email "jenkins@jenkins.com"
RUN git config --global user.name "jenkins"

# Oc file is copied for running oc cli commands in jenkins
RUN mkdir -p /tmp/bin
COPY oc.tar /tmp/bin
WORKDIR /tmp/bin
RUN tar -xf oc.tar
ENV PATH=$PATH:/tmp/bin

WORKDIR /
# drop back to the regular jenkins user - good practice and for access to folder for jenkins user
USER jenkins
