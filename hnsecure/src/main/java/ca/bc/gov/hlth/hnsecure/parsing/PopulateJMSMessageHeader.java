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
		exchange.getIn().setHeader("JMSMessageId", "20210819115331");
		//exchange.getIn().setHeader("JMS_IBM_MQMD_MsgId", "20210819115331");
		 logger.info("Message id: "+exchange.getIn().getMessageId());
		 
		logger.info("{} - Transaction Id : {}, JMS messageId is set to : {} ", methodName, exchange.getExchangeId(),
				msgControlId);
	}

}
