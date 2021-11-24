// This groovy script is used for jenkins jobs

def project(NAMESPACE) {
	def ns = "${NAMESPACE}"
	return "c5839f-" + ns.toLowerCase()
}

def lower(val) {
	def app = "${val}"
	return val.toLowerCase()
}

def appName() {
	def ns = "${NAMESPACE}"
	return "hnsesb-" + ns.toLowerCase()
}

def deployTag(DEPLOY_BUILD_NUMBER, DEPLOY_BUILD_TYPE){
	def tag = "BUILD-DEPLOY-${DEPLOY_BUILD_NUMBER}"
	if ("${DEPLOY_BUILD_TYPE}"=='Release'){
		tag = "RELEASE-BUILD-DEPLOY-${DEPLOY_BUILD_NUMBER}"
	}
	return tag
}
// Method to verfiy pods 
def verifyPods(APP_NAME){
    def pods =  openshift.selector( 'pods', [ app: '${APP_NAME}' ] )
	// This will throw error, intended, if there are no pods with that application name
	def podsObj = pods.objects()
	// This loop will check if all pods are running status
	pods.untilEach(1){
	    echo "Checking pod status"
	    def podObj = it.object()
	    def podStatus = podObj.status.phase
        if('Running'=="${podStatus}")								    {
            // Got to next pod status check
            return true;
        }else{
            sh 'False'
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