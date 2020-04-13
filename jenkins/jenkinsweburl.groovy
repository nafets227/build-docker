#!groovy

import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

jenkinsweburl=System.getenv("JENKINSWEBURL")
if (!jenkinsweburl || jenkinsweburl == "") {
	println "JENKINSWEBURL not set. Skipping."
} else {
	def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()
	jenkinsLocationConfiguration.setUrl(jenkinsweburl)
	// jenkinsLocationConfiguration.setAdminAddress(jenkinsParameters.email)
	jenkinsLocationConfiguration.save()
}

