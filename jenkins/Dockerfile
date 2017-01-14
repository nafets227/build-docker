FROM jenkins:latest
MAINTAINER Stefan Schallenberg <infos (at) nafets.de>
LABEL Description="Jenkins with Docker client and plugin installed"

USER root
# Install Docker Engine for Jessie, that is the base for Jenkins image
RUN \
    apt-get update && \
    apt-get install -y apt-transport-https ca-certificates gnupg2 && \
    apt-key adv \
       --keyserver hkp://ha.pool.sks-keyservers.net:80 \
       --recv-keys 58118E89F3A912897C070ADBF76221572C52609D && \
    echo "deb https://apt.dockerproject.org/repo debian-jessie main" \
       >/etc/apt/sources.list.d/docker.list
RUN \
    apt-get update && \
    apt-get install -y docker-engine

RUN \
    rm -rf /var/lib/apt/lists/*

RUN /usr/local/bin/install-plugins.sh \
    durable-task \
    docker-plugin \
    git \
    http-post \
    jclouds-jenkins \
    subversion \
    token-macro

#COPY plugins.sh /tmp/plugins.sh
#COPY plugins.txt /tmp/plugins.txt
#RUN \
#    /tmp/plugins.sh /tmp/plugins.txt; \
#    rm /tmp/plugins.sh /tmp/plugins.txt
    
USER ${user}
