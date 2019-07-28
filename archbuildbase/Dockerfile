FROM archlinux/base

MAINTAINER Stefan Schallenberg aka nafets227 <infos@nafets.de>
LABEL Description="Build Base container for Arch Linux Builds in Jenkins"

EXPOSE 22

RUN \
    groupadd -r jenkins -g 900 && \
    useradd -r -d /home/jenkins -u 900 -m -g jenkins jenkins && \
    printf "jenkins\njenkins\n" | passwd jenkins

RUN \
    pacman -Suy --noconfirm && \
    pacman -S --needed --noconfirm \
        subversion \
    	openssh && \
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
    chmod 755 /usr/local/bin/jenkins-slave

ENTRYPOINT [ "/usr/local/bin/jenkins-slave" ]

