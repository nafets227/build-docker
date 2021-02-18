# Workaround Bug in current archlinux that prevents pacman -Sy
# from working, erroneously reporting no access to /var/lib/pacman
# as of 7.2.2021

#FROM archlinux/archlinux:base-devel as base-amd64
FROM archlinux/archlinux:base-devel-20210131.0.14634 AS base-amd64
# Workaround
RUN \
    echo "IgnorePkg = glibc glib2" >>/etc/pacman.conf

FROM agners/archlinuxarm-arm32v7 as base-arm
RUN \
    pacman -Sy --needed --noconfirm shadow && \
    groupadd users -g 1000

FROM agners/archlinuxarm-arm64v8 as base-arm64
RUN \
    pacman -Sy --needed --noconfirm shadow && \
    groupadd users -g 1000

FROM base-${TARGETARCH}

LABEL Maintainer="Stefan Schallenberg aka nafets227 <infos@nafets.de>"
LABEL Description="Build Base container for Arch Linux Builds in Jenkins"

EXPOSE 22

RUN \
    groupadd -r jenkins -g 900 && \
    useradd -r -d /home/jenkins -u 900 -m -g jenkins jenkins && \
    printf "jenkins\njenkins\n" | passwd jenkins

RUN \
    pacman -Suy --noconfirm && \
    pacman -S --needed --noconfirm openssh && \
    /usr/bin/ssh-keygen -A && \
    pacman -S --needed --noconfirm \
        git \
        jre-openjdk \
        sudo && \
    rm -rf \
      /var/tmp/* \
      /usr/share/man/* \
      /var/cache/pacman/pkg/* \
      /var/lib/pacman/sync/* \
      /README \
      /etc/pacman.d/mirrorlist.pacnew    
    
COPY jenkins-slave /usr/local/bin
RUN \
    chown root:users /usr/local/bin/jenkins-slave && \
    chmod 755 /usr/local/bin/jenkins-slave && \
    mkdir /home/jenkins/workspace && \
    chown jenkins:jenkins /home/jenkins/workspace

ENTRYPOINT [ "/usr/local/bin/jenkins-slave" ]

