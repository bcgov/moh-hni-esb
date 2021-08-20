/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

public class PopulateJMSMessageHeader {
	private static final Logger logger = LoggerFactory.getLogger(PopulateJMSMessageHeader.class);

	@Handler
	public void populateReqHeader(Exchange exchange, String v2Message) {
		final String methodName = LoggingUtil.getMethodName();
		String msgControlId = Util.getMsgCnrtlId(v2Message);
		exchange.getIn().setHeader("JMSMessageId", "3230313730313235313232313235");

		logger.info("{} - Transaction Id : {}, JMS messageId is set to : {} ", methodName, exchange.getExchangeId(),
				msgControlId);
	}

}
