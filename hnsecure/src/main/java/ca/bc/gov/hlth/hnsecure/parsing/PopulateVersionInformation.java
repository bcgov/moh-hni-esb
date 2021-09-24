/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.HnsEsbMainMethod;
import net.minidev.json.JSONObject;


public class PopulateVersionInformation implements Processor{
	private static final Logger logger = LoggerFactory.getLogger(PopulateVersionInformation.class);
	
	private static final String versionInformation = getVersionInformation().toJSONString();

	protected static JSONObject getVersionInformation() {
		Package pck  = HnsEsbMainMethod.class.getPackage();
		
		final String methodName = LoggingUtil.getMethodName();
		// init a JSON object
		JSONObject v2JsonObj = new JSONObject();
		v2JsonObj.put("Implementation-Title", pck.getImplementationTitle());
		v2JsonObj.put("Implementation-Version", pck.getImplementationVersion());
		v2JsonObj.put("Implementation-Vendor", pck.getImplementationVendor());
		logger.debug("{} - The JSON Message is: {}", methodName, v2JsonObj.toJSONString());
		//return the JSON object
		return v2JsonObj;
	}


	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setBody(versionInformation);
		
	}
	
	
	
}
