
//-------------------------------------------------------------------
// File: jenkins.groovy
// Description: A util file for hnsesb jenkins jobs
//-------------------------------------------------------------------

def project(val) {
	return "c5839f-" + val.toLowerCase()
}

def lower(val) {
	return val.toLowerCase()
}

def appName(val) {
	return "hnsesb-" + val.toLowerCase()
}

def deployTag(DEPLOY_BUILD_NUMBER, DEPLOY_BUILD_TYPE){
	def tag = "BUILD-DEPLOY-${DEPLOY_BUILD_NUMBER}"
	if ("${DEPLOY_BUILD_TYPE}"=='Release'){
		tag = "RELEASE-BUILD-DEPLOY-${DEPLOY_BUILD_NUMBER}"
	}
	return tag
}
// Method to verfiy pods 
def verifyPods(val){
	def pods =  openshift.selector( 'pods', [ app: val ] )
	// This will throw error, intended, if there are no pods with that application name
	def podsObj = pods.objects()
	echo "Printing object ${podsObj}"
	assert podsObj.count() > 0
	/*
	if(podsObj.count()==0){
		echo "No pods found with that name."
		sh 'False'
	}
	*/
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

def example1() {
  println 'Hello from example1'
}

def example2() {
  println 'Hello from example2'
}

return this