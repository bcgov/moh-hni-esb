//-------------------------------------------------------------------
// File: jenkins.groovy
// Description: A util file for hnsesb jenkins jobs
//-------------------------------------------------------------------
//This method returns namespace name with license plate prefix
def project(val) {
	return "c5839f-" + val.toLowerCase()
}
// Convert value to lowercase
def lower(val) {
	return val.toLowerCase()
}
// Passed value is prefixed with hnesb-. This method is used for defining application name
def appName(val) {
	return "hnsesb-" + val.toLowerCase()
}

// Method get build number and type to define tag
// If build number is 121 and type is release then tag is RELEASE-BUILD-DEPLOY-121
// For all other types, tag will be BUILD-DEPLOY-121
def deployTag(DEPLOY_BUILD_NUMBER, DEPLOY_BUILD_TYPE){
	def tag = "BUILD-DEPLOY-${DEPLOY_BUILD_NUMBER}"
	if ("${DEPLOY_BUILD_TYPE}"=='Release'){
		tag = "RELEASE-BUILD-DEPLOY-${DEPLOY_BUILD_NUMBER}"
	}
	return tag
}
// Method to verfiy pods 
// Pass application name as argument to check if any pods exist for the application
def verifyPods(val){
	def pods =  openshift.selector( 'pods', [ app: val ] )
	// This will throw error, intended, if there are no pods with that application name
	def podsObj = pods.objects()
	assert podsObj.size() > 0

	// This loop will check if all pods are running status
	timeout (time: 1, unit: 'MINUTES') {
		pods.untilEach(1){
		echo "Checking pod status"
		def podObj = it.object()
		def podStatus = podObj.status.phase
		if('Running'==podStatus)								    {
			// Got to next pod status check
			return true;
		}else{
			sh 'False'
		}
	}
	}
}

return this