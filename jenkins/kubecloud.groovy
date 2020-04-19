// based on the great article of Kayan Azimov
// https://ifritltd.com/2018/03/18/advanced-jenkins-setup-creating-jenkins-configuration-as-code-and-applying-changes-without-downtime-with-java-groovy-docker-vault-consul-template-and-jenkins-job/

import hudson.model.*
import jenkins.model.*
import org.csanchez.jenkins.plugins.kubernetes.*
import org.csanchez.jenkins.plugins.kubernetes.volumes.workspace.EmptyDirWorkspaceVolume
 
final BUILD = "archbuildpkg"
final CLOUD = "Local Kubernetes Cluster"
KubernetesCloud kc = null
PodTemplate pt = null
ContainerTemplate ct = null
try {
	img=System.getenv("ARCHBUILDIMG")
	if (!img || img == "") {
		println "Error: ARCHBUILDIMG is not set."
		throw new Exception("ARCHBUILDIMG not set")
	}

	ns=System.getenv("NAMESPACE")
	if (!ns || ns == "") {
		println "Error: NAMESPACE is not set."
		throw new Exception("NAMESPACE not set")
	}

	nsbuild=System.getenv("NAMESPACEBUILD")
	if (!nsbuild || nsbuild == "") {
		nsbuild="build-${ns}"
		println "NAMESPACEBUILD not set. Using Default ${nsbuild}"
	}

	jenkinsurl=System.getenv("JENKINSBUILDURL")
	if (!jenkinsurl || jenkinsurl == "") {
		jenkinsurl="http://jenkins.${ns}.svc.cluster.local:8080"
		println "JENKINSBUILDURL not set. Using Default ${jenkinsurl}"
	}

	println "Configuring Jenkins Cloud '${CLOUD}'"

	pt = new PodTemplate()
	pt.setName(BUILD)
	pt.setLabel(BUILD)
	pt.setNamespace(nsbuild)
	// pt.setNodeUsageMode(podTemplateConfig.nodeUsageMode)
	// pt.setCustomWorkspaceVolumeEnabled(podTemplateConfig.customWorkspaceVolumeEnabled)
	pt.setWorkspaceVolume(new EmptyDirWorkspaceVolume(false)) // false: no in-memory volume

	/* Workspace volumes not yet implemented. Coud look like:

	import org.csanchez.jenkins.plugins.kubernetes.volumes.ConfigMapVolume
	import org.csanchez.jenkins.plugins.kubernetes.volumes.PersistentVolumeClaim
	import org.csanchez.jenkins.plugins.kubernetes.PodEnvVar

	def volumes = []
	volumes << new PersistentVolumeClaim("/home/jenkins", "build.build", false)
	volumes << new ConfigMapVolume("/config", "build.ca")
	pt.setVolumes(volumes)
	*/

	ct = new ContainerTemplate("jnlp", img)

	ct.setAlwaysPullImage(true)
	// ct.setPrivileged(podTemplateConfig.containerTemplate.privileged ?: conf.kubernetes.containerTemplateDefaults.privileged)
	ct.setTtyEnabled(true)
	ct.setWorkingDir("/home/jenkins/agent")
	ct.setCommand("")
	ct.setArgs("\${computer.jnlpmac} \${computer.name}")
	pt.setContainers([ct])

	if (Jenkins.instance.clouds) {
		Jenkins.instance.clouds.each { cloud ->
			if (cloud.name == CLOUD) {
				kc = cloud
				fAdd = false
        			println "Updating existing cloud ${cloud.name}"
			} else 
				println "Ignoring existing cloud ${cloud.name}"
		}
	}
	if (!kc) {
		kc = new KubernetesCloud(CLOUD)
		fAdd = true
		println "created cloud ${Jenkins.instance.clouds}"
	}
 
	kc.setServerUrl("https://kubernetes.default.svc.cluster.local/")
	kc.setNamespace(nsbuild)
	kc.setJenkinsUrl(jenkinsurl)
	kc.setMaxRequestsPerHostStr("")
	// kc.setContainerCapStr(conf.kubernetes.containerCapStr)
	// kc.setSkipTlsVerify(false)
	// kc.setCredentialsId(conf.kubernetes.credentialsId)
	// kc.setConnectTimeout(conf.kubernetes.connectTimeout)
	// kc.setReadTimeout(conf.kubernetes.readTimeout)
 
	if (kc.templates) {
		kc.templates.each { podTempl ->
			if (podTempl.name == BUILD) {
				println "deleting existing podTemplate ${podTempl.name}"
				kc.templates.remove(podTempl)
			} else
				println "Ignoring existing podTemplate ${podTempl.name}"
		}
	}

	println "adding PodTemplate ${pt.name}"
	kc.templates << pt

	if (fAdd == true) {
		println "Adding Cloud."
		Jenkins.instance.clouds.add(kc)
	}

	kc = null
	pt = null
	ct = null
	println "Configuring Jenkins Cloud '${CLOUD}' completed"
}
finally {
	// if we don't null kc, jenkins will try to serialise k8s objects
	//  and that will fail, so we won't see actual error
	kc = null
	pt = null
	ct = null
}
