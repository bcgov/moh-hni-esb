package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.HnsEsbMainMethod;
import ca.bc.gov.hlth.hnsecure.message.DateTimeResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.ResponseSegment;
import net.minidev.json.JSONObject;

/**
 * This class is created to get current date and time of the HNSecure server
 * It is used by clients to test connectivity and check server is running
 */
public class DateTimeResponseProcessor implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(DateTimeResponseProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		// Initialize the incoming hl7Message used to build the response
		HL7Message hl7Message = new HL7Message();
		String v2 = (String) exchange.getIn().getBody();
		String[] v2Segments = V2MessageUtil.getMshSegmentFields(v2);
		// Since this can be invoked under multiple scenarios it is possible
		// for the segments to be null
		if (v2Segments != null) {
			ResponseSegment.initSegment(v2Segments, hl7Message);
		}
		// Changing the IN message directly is possible in Camel as it don't mind. Camel will detect that the Exchange has no OUT message
		// and therefore use the IN message instead.
		exchange.getIn().setBody(new DateTimeResponse().constructResponse(hl7Message));
		
		logger.info(exchange.getIn().getBody().toString());
	}

}
