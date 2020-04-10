// based on the great article of Kayan Azimov
// https://ifritltd.com/2018/03/18/advanced-jenkins-setup-creating-jenkins-configuration-as-code-and-applying-changes-without-downtime-with-java-groovy-docker-vault-consul-template-and-jenkins-job/

import hudson.model.*
import jenkins.model.*
import org.csanchez.jenkins.plugins.kubernetes.*
 
def kc
try {
	def NAMESPACE = "preprod" //@TODO replace
	println "Configuring Jenkins Cloud 'Local Kubernetes Cluster'"
	if (Jenkins.instance.clouds) {
		Jenkins.instance.clouds.each { cloud ->
			if (cloud.name == "Local kubernetes Cluster") {
				kc = cloud
        			println "Updating existing cloud ${cloud.name}"
			} else 
				println "Ignoring existing cloud ${cloud.name}"
		}
	} else {
		kc = new KubernetesCloud("Local kubernetes Cluster")
		Jenkins.instance.clouds.add(kc)
		println "added cloud ${Jenkins.instance.clouds}"
	}
 
	kc.setServerUrl("https://kubernetes.default.svc.cluster.local/")
	kc.setNamespace("build-$NAMESPACE")
	kc.setJenkinsUrl("http://jenkins.${NAMESPACE}.svc.cluster.local:8080")
	kc.setMaxRequestsPerHostStr("")
	// kc.setContainerCapStr(conf.kubernetes.containerCapStr)
	// kc.setSkipTlsVerify(false)
	// kc.setCredentialsId(conf.kubernetes.credentialsId)
	// kc.setConnectTimeout(conf.kubernetes.connectTimeout)
	// kc.setReadTimeout(conf.kubernetes.readTimeout)
 
	kc = null
	println "Configuring k8s completed"
}
finally {
	// if we don't null kc, jenkins will try to serialise k8s objects
	//  and that will fail, so we won't see actual error
	kc = null
}
