ARG DOCKER_TAG
FROM nafets227/archbuildbase:$DOCKER_TAG

LABEL Maintainer="Stefan Schallenberg aka nafets227 <infos@nafets.de>"
LABEL Description="Build container for Arch Packages in Jenkins"

RUN \
    if [ "$HOSTARCH" == "x86_64" ] ; then \
        printf '\n\
[multilib]\n\
Include = /etc/pacman.d/mirrorlist\n\
\n\
' \
            >> /etc/pacman.conf || exit 1 ; \
    fi && \
    pacman -Sy

RUN \
    pacman -Suy --needed --noconfirm base-devel intltool && \
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
