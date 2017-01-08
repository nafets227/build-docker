FROM nafets227/archbuildbase

MAINTAINER Stefan Schallenberg aka nafets227 <infos@nafets.de>
LABEL Description="Build container for Arch Packages in Jenkins"

COPY infos_nafets.de.gpgpub /root/infos_nafets.de.gpgpub


# @TODO: find a way to handle the nafets pacman repository, that is only available
#        to our intranet and not in the public internet.
RUN printf '\n\
[multilib]\n\
Include = /etc/pacman.d/mirrorlist\n\
\n\
#[nafets]\n\
#Include = /etc/pacman.d/mirrorlist\n\
#\n\
' \
>> /etc/pacman.conf && \ 
    pacman-key -a /root/infos_nafets.de.gpgpub && \
    pacman-key --lsign $(gpg  </root/infos_nafets.de.gpgpub | \
        sed -n "s:^      ::p") && \
    pacman -Sy

RUN pacman -S --needed --noconfirm binutils make pkg-config gcc fakeroot base-devel sudo

# Remove temporary files to save space
RUN \
    paccache -r -k0 && \
    rm -rf /usr/share/man/* && \
    rm -rf /tmp/* && \
    rm -rf /var/tmp/*

RUN printf '\
jenkins ALL = NOPASSWD: /usr/bin/pacman\n\
jenkins ALL = NOPASSWD: /usr/local/bin/ref_repo.sh\n\
' >/etc/sudoers.d/nafetsde-aur

EXPOSE 22


