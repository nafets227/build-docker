# archbuildpkg
Docker Images for creating packages in Arch Linux.
Supports building in Jenkins and Kubernetes.

# Configuration
This image can be configured using Environment Variables. They can be set either in commandline (docker run -e var=value) or in docker-compose or Kubernetes or any other tool that launches Docker Containers.
## Repositories
Example:
PACMAN_REPO="myrepo1 myrepo2
PACMAN_REPO_MYREPO2="Server = myserver\nSigLevel = optional"
NB: PACMAN_REPO_MYREPO1 is not set, then the default ist used (use mirrorlist)
## Repository Keys
PACMAN_KEY="/config/mygpgpubkey.gpg /config/mykey2"
This Key will be added to the local key store and trusted.
## SSL Trusted CA
SSL_CAFILE="/config/myCa.pem"
Trust this CA when connecting via https to a repository
## Mirrorlist
PACMAN_MIRRORLIST="http://myserver/$arch/$os\nhttp://myserver2/mydir/$arch/pkg/$os"

