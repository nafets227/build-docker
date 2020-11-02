# build-docker
Build Arch Linux packages in containers using Jenking

Build 
[Arch Linux](https://archlinux.org)
packages from
[AUR](https://aur.archlinux.org) fully automated, triggered by each commit
 and push it to a local ArchLinux Repo. Run in Kubernetes

## Usage
Activate Kubernetes Plugin in your jenkins, e.g. by using the 
[Helm-chart](https://github.com/jenkinsci/helm-charts).

Then define podTemplates that use the image nafets227/archbuildpkg.

# Discontinued Jenkins
Jenkins image present in previous versions has been discontinued in favor of using the Jenkins
[Helm-chart](https://github.com/jenkinsci/helm-charts)

## Example, to be put in Jenkins Config as Code
	agent:
		podTemplates:
		archbuildpkg: |
			- name: archbuildpkg
			label: archbuildpkg
			activeDeadlineSeconds: "1800"
			# serviceAccount: jenkins
			containers:
				- name: jnlp
				image: "nafets227/archbuildpkg:latest"
				command: ""
				args: "^\${computer.jnlpmac} ^\${computer.name}"
				ttyEnabled: true
				workingDir: "/home/jenkins/agent"
