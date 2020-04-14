#!groovy

import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

myurl=System.getenv("JENKINSWEBURL")
if (!myurl || myurl == "") {
	println "JENKINSWEBURL not set. Skipping."
} else {
	def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()
	jenkinsLocationConfiguration.setUrl(myurl)
	// jenkinsLocationConfiguration.setAdminAddress(jenkinsParameters.email)
	jenkinsLocationConfiguration.save()
}

