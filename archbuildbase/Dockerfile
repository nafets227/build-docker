FROM pritunl/archlinux

MAINTAINER Stefan Schallenberg aka nafets227 <infos@nafets.de>
LABEL Description="Build Base container for Arch Linux Builds in Jenkins"

EXPOSE 22

RUN printf '\n\
[options]\n\
IgnorePkg=cryptsetup\n\
IgnorePkg=device-mapper\n\
IgnorePkg=cryptsetup\n\ 
IgnorePkg=device-mapper\n\
IgnorePkg=dhcpcd\n\
IgnorePkg=iproute2\n\
IgnorePkg=jfsutils\n\
IgnorePkg=linux\n\
IgnorePkg=lvm2\n\
IgnorePkg=man-db\n\
IgnorePkg=man-pages\n\ 
IgnorePkg=mdadm\n\
IgnorePkg=nano\n\
IgnorePkg=netctl\n\
IgnorePkg=openresolv\n\
IgnorePkg=pciutils\n\
IgnorePkg=pcmciautils, \
IgnorePkg=reiserfsprogs\n\
IgnorePkg=s-nail\n\
IgnorePkg=systemd-sysvcompat\n\
IgnorePkg=usbutils\n\
IgnorePkg=vi\n\
IgnorePkg=xfsprogs\n\
' \
>> /etc/pacman.conf && pacman -Sy

RUN \
    groupadd -r jenkins -g 981 && \
    useradd -r -d /home/jenkins -u 981 -m -g jenkins jenkins && \
    printf "jenkins\njenkins\n" | passwd jenkins

RUN \
    pacman -S --needed --noconfirm openssh && \
    /usr/bin/ssh-keygen -A

RUN \
    pacman -S --needed --noconfirm \
        git \
        jre8-openjdk \
        subversion

# Remove temporary files to save space
RUN \
    paccache -r -k0 && \
    rm -rf /usr/share/man/* && \
    rm -rf /tmp/* && \
    rm -rf /var/tmp/*

COPY jenkins-slave /usr/local/bin
RUN \
    chown root:users /usr/local/bin/jenkins-slave && \
    chmod 755 /usr/local/bin/jenkins-slave

ENTRYPOINT [ "/usr/local/bin/jenkins-slave" ]

