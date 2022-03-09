package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.HnsEsbMainMethod;
import net.minidev.json.JSONObject;

/**
 * This class is created to get the manifest information and provide the same in version route as JSON object.
 * Manifest information is set in pom.xml 
 */
public class PopulateVersionInformation implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(PopulateVersionInformation.class);
	private static final String IMPLEMENTATION_VERSION_KEY = "Implementation-Version";
	private static final String VERSION_INFORMATION = getVersionInformation().toJSONString();

	/**
	 * Loading the version information using Package class.
	 * Version information is set in jar's META-INF/Manifest.mf file
	 * @return JSONObject
	 */
	public static JSONObject getVersionInformation() {
		Package pck  = HnsEsbMainMethod.class.getPackage();
		final String methodName = LoggingUtil.getMethodName();
		// init a JSON object
		JSONObject v2JsonObj = new JSONObject();
		v2JsonObj.put(IMPLEMENTATION_VERSION_KEY, pck.getImplementationVersion());
		logger.debug("{} - The JSON Message is: {}", methodName, v2JsonObj.toJSONString());
		//return the JSON object
		return v2JsonObj;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setBody(VERSION_INFORMATION);
	}
	
}
