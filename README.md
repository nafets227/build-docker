# build-docker
Build Arch Linux packages in containers using Jenking

Build 
[Arch Linux](https://archlinux.org)
packages from
[AUR](https://aur.archlinux.org) fully automated, triggered by each commit
 and push it to a local ArchLinux Repo. Run in Kubernetes

## Usage
Create a Kubernetes Deployment for Jenkins and make the Web-Interface 
available to outside the cluster. See below for a fully running Example of
Kubernetes yaml files


## Environment Variables in Jenkins container
- JENKINSWEBURL: URL where Jenkins Web-UI is accessible from users, e.g. 
https://jenkins.myintranet.example.com
- JENKINSBUILDURL URL of Jenkins for Build nodes, defaults to
http://jenkins.$NAMESPACE.svc.cluster.local:8080.
Build Agents will connect to this URL when a container is started and Jenkins
will wait for the connection.
Typically a Kubernetes-cluster internal URL is used.
- ARCHBUILDIMG: Docker image name and tag to be used when building packages,
defaults to nafets227/archbuildpkg:<tag>.
Only change if you know what you are doing.
- NAMESPACE: Kubernetes namespace Jenkins is running in.
Mandatory, suggest to use the downword API of Kubernetes (see example)
- NAMESPACEBUILD: kubernetes namespace Build containers will be created in.
Defaults to build-$NAMESPACE
  
## Persistence
In order to persist the Jenkins data simply mount a persistent storage to 
/var/jenkins in the jenkins container, as done in the full example below. 

## Full Example
	apiVersion: apps/v1
	kind: Deployment
	metadata:
	  creationTimestamp: null
	  name: jenkins
	  labels:
	    app: myintranetjenkins
	    svc: jenkins
	spec:
	  replicas: 1
	  strategy:
	    type: Recreate
	  selector:
	    matchLabels:
	      app: myintranetjenkins
	      svc: jenkins
	  template:
	    metadata:
	      creationTimestamp: null
	      labels:
	        app: myintranetjenkins
	        svc: jenkins
	    spec:
	      containers:
	      - name: jenkins
	        image: nafets227/jenkins
	        imagePullPolicy: Always
	        ports:
	        - containerPort: 8080
	        - containerPort: 50000
	        volumeMounts:
	        - mountPath: /var/jenkins_home
	          name: jenkins
	        env:
	        - name: NAMESPACE
	          valueFrom:
	            fieldRef:
	              fieldPath: metadata.namespace
	      serviceAccountName: jenkins
	      restartPolicy: Always
	      volumes:
	      - name: jenkins
	        persistentVolumeClaim:
	   claimName: jenkins
	---
	apiVersion: v1
	kind: Service
	metadata:
	  name: jenkins
	  labels:
	    app: myintranetjenkins
	    svc: jenkins
	spec:
	  ports:
	  - name: headless
	    port: 8080
	  - name: jnlp
	    port: 50000
	  selector:
	    app: myintranetjenkins
	    svc: jenkins
	---
	apiVersion: networking.k8s.io/v1beta1
	kind: Ingress
	metadata:
	  name: jenkins
	  labels:
	    app: myintranetjenkins
	  annotations:
	    kubernetes.io/ingress.class: "nginx"
	spec:
	  rules:
	    - http:
	        paths:
	        - path: /
	          backend:
	            serviceName: jenkins
	            servicePort: 8080
	      host: jenkins.myintranet.example.com
	  tls:
	  - hosts:
	    - jenkins.myintranet.example.com
	    secretName: jenkins.myintranet.example.com
	---
	apiVersion: v1
	kind: ServiceAccount
	metadata:
	  name: jenkins
	---
	apiVersion: rbac.authorization.k8s.io/v1
	kind: Role
	metadata:
	  name: buildadmin
	rules:
	- apiGroups: [""] # "" indicates the core API group
	  resources: ["pods", "pods/log"]
	  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
	- apiGroups: [""] # "" indicates the core API group
	  resources: ["events"]
	  verbs: ["get", "list", "watch"]
	---
	apiVersion: rbac.authorization.k8s.io/v1
	kind: RoleBinding
	metadata:
	  name: jenkins-buildadmin
	subjects:
	- kind: ServiceAccount
	  name: jenkins
	  namespace: myintranetjenkins
	roleRef:
	  kind: Role
	  name: buildadmin
	  apiGroup: rbac.authorization.k8s.io
