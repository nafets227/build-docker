FROM jenkins/jenkins:lts
MAINTAINER Stefan Schallenberg <infos (at) nafets.de>
LABEL Description="Jenkins with Docker client and plugin installed"

USER root
# Install Docker Engine for Jessie, that is the base for Jenkins image
RUN \
    apt-get update && \
    apt-get install -y apt-transport-https ca-certificates gnupg2 && \
    echo "deb https://apt.dockerproject.org/repo debian-jessie main" \
       >/etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y --allow-unauthenticated docker-engine && \
    rm -rf /var/lib/apt/lists/*

RUN /usr/local/bin/install-plugins.sh \
    durable-task \
    docker-plugin \
    git \
    http-post \
    jclouds-jenkins \
    job-dsl \
    kubernetes \
    token-macro \
    workflow-aggregator

#COPY plugins.sh /tmp/plugins.sh
#COPY plugins.txt /tmp/plugins.txt
#RUN \
#    /tmp/plugins.sh /tmp/plugins.txt; \
#    rm /tmp/plugins.sh /tmp/plugins.txt

COPY executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy
    
USER ${user}
