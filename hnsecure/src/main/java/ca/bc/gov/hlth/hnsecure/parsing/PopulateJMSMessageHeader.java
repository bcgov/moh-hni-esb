/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * Add hexadecimal message ControlId with a prefix ID: to stop any encoding in MQRFH2
 * Refer : https://www.ibm.com/docs/en/ibm-mq/7.5?topic=messages-mapping-jms-header-fields-send-publish
 * @author anumeha.srivastava
 *
 */
public class PopulateJMSMessageHeader {
	private static final Logger logger = LoggerFactory.getLogger(PopulateJMSMessageHeader.class);

	@Handler
	public void populateJMSReqHeader(Exchange exchange, String v2Message) {
		final String methodName = LoggingUtil.getMethodName();
		String msgControlId = Util.getMsgCnrtlId(v2Message);
		String hexString = Util.convertStringToHex(msgControlId);
		
		exchange.getIn().setHeader("JMSMessageID", hexString);
		exchange.getIn().setHeader("JMSCorrelationID", "ID:" + hexString);
		
		String jmsMsgId =(String) exchange.getIn().getHeader("JMSMessageID");
		String jmsCorId =(String) exchange.getIn().getHeader("JMSCorrelationID");
		
		logger.info("{} - Transaction Id : {}, JMS messageId is set to : {}, JMS correlationId is set to : {}  ", methodName, exchange.getExchangeId(),
				jmsMsgId,jmsCorId);
	}

}
