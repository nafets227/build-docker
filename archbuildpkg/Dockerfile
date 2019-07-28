FROM nafets227/archbuildbase

MAINTAINER Stefan Schallenberg aka nafets227 <infos@nafets.de>
LABEL Description="Build container for Arch Packages in Jenkins"

RUN printf '\n\
[multilib]\n\
Include = /etc/pacman.d/mirrorlist\n\
\n\
' \
>> /etc/pacman.conf && \ 
    pacman -Sy

RUN \
    pacman -S --needed --noconfirm \
        $(pacman -S -g base-devel \
            | sed -e 's/^base-devel //') \
        binutils make pkg-config gcc fakeroot && \
    rm -rf /var/cache/pacman/pkg/* && \
    rm -rf /usr/share/man/* && \
    rm -rf /tmp/* && \
    rm -rf /var/tmp/*

RUN printf '\
jenkins ALL = NOPASSWD: /usr/bin/pacman\n\
jenkins ALL = NOPASSWD: /usr/local/bin/ref_repo.sh\n\
' >/etc/sudoers.d/nafetsde-aur

COPY archbuildpkg /usr/local/bin
RUN \
    chown root:users /usr/local/bin/archbuildpkg && \
    chmod 755 /usr/local/bin/archbuildpkg

ENTRYPOINT [ "/usr/local/bin/archbuildpkg" ]