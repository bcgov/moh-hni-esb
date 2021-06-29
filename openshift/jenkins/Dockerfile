# Base image docker pull jenkins/jenkins:lts-jdk11
FROM jenkins/jenkins:lts-jdk11
# if we want to install via apt
USER root
RUN apt-get update && apt-get install -y maven
# drop back to the regular jenkins user - good practice
USER jenkins

ENV JENKINS_HOME /var/jenkins_home