package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.msg.client.commonservices.CSIException;
import com.ibm.msg.client.commonservices.Utils;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * Add hexadecimal message ControlId with a prefix ID: to add provider specific 
 * value to stop any encoding in MQRFH2.
 * Set custom JMS message Id by adding MQMD.
 * As per JMS specification JMS_IBM_MQMD_MsgId must be unique or null.
 * header Refer : https://www.ibm.com/docs/en/ibm-mq/7.5?topic=messages-mapping-jms-header-fields-send-publish
 * @author anumeha.srivastava
 *
 */
public class PopulateJMSMessageHeader {
	private static final Logger logger = LoggerFactory.getLogger(PopulateJMSMessageHeader.class);

	@Handler
	public void populateJMSRequestHeader(Exchange exchange, String v2Message) throws CSIException {
		final String methodName = LoggingUtil.getMethodName();
		String msgControlId = V2MessageUtil.getMsgControlId(v2Message);
		byte[] customMessageId = new byte[24];
		String hexString = Util.convertStringToHex(msgControlId);
		try {
			customMessageId = Utils.hexToBytes(hexString);
		} catch (CSIException e) {
			logger.error("{} - TransactionId: {}, Exception while converting hexadecimal message Control Id to byte {}",
				methodName, exchange.getExchangeId(), e.getMessage());
        	throw e;
		}
		exchange.getIn().setHeader("JMSCorrelationID", "ID:" + hexString);		
		exchange.getIn().setHeader("JMS_IBM_MQMD_MsgId", customMessageId);

		logger.info("{} - Transaction Id : {}, JMS messageId is set to : {}, JMS correlationId is set to : {}  ",
				methodName, exchange.getExchangeId(), hexString, hexString);
	}

}
