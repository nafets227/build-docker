FROM jenkins/jenkins:lts
MAINTAINER Stefan Schallenberg <infos (at) nafets.de>
LABEL Description="Jenkins with Docker client and plugin installed"
ARG DOCKER_TAG=latest

ENV ARCHBUILDIMG=nafets227/archbuildpkg:$DOCKER_TAG
RUN echo "Info: Default ArchBuildImg=$ARCHBUILDIMG"

USER root
# Install Docker Engine for Debian
RUN \
    apt-get update && \
    apt-get install -y apt-transport-https ca-certificates gnupg2 software-properties-common && \
    add-apt-repository \
        "deb [arch=amd64] https://download.docker.com/linux/debian \
        $(lsb_release -cs) \
        stable" && \
    apt-get update && \
    apt-get install -y --allow-unauthenticated docker-ce docker-ce-cli containerd.io && \
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

COPY executors.groovy \
	/usr/share/jenkins/ref/init.groovy.d/executors.groovy.override
COPY kubecloud.groovy \
	/usr/share/jenkins/ref/init.groovy.d/kubecloud.groovy.override


USER ${user}
