package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.msg.client.commonservices.CSIException;
import com.ibm.msg.client.commonservices.Utils;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * Set Correlation Id to hexadecimal Exchange ID with a prefix ID: to add provider specific 
 * value to stop any encoding in MQRFH2.
 * Set hexadecimal V2 messageControlId as JMS message Id for broker's MQMD field.
 * Set CCSID to 819
 * Set DeliveryMode to 1 'MQPER_NOT_PERSISTENT'
 * As per JMS specification JMS_IBM_MQMD_MsgId must be unique or null.
 * header Refer : https://www.ibm.com/docs/en/ibm-mq/7.5?topic=messages-mapping-jms-header-fields-send-publish
 *
 */
public class PopulateJMSMessageHeader {
	private static final String HEXA_TO_BYTE_ERROR = "Exception while converting hexadecimal message Control Id to byte";
	private static final Logger logger = LoggerFactory.getLogger(PopulateJMSMessageHeader.class);
	private static final String CHAR_SET_ID = "819";
	private static final String DELIVERY_MODE = "1";

	@Handler
	public void populateJMSRequestHeader(Exchange exchange, String v2Message) throws CSIException {
		final String methodName = LoggingUtil.getMethodName();
		byte[] customMessageId = new byte[24];	
		
		String msgControlId = V2MessageUtil.getMsgControlId(v2Message);			
		String hexStringForMsgId = Util.convertStringToHex(msgControlId);
		try {
			customMessageId = Utils.hexToBytes(hexStringForMsgId);
		} catch (CSIException e) {
			logger.error("{} - TransactionId: {}, Exception while converting hexadecimal message Control Id to byte {}",
				methodName, exchange.getExchangeId(), e.getMessage());
        	throw new IllegalArgumentException(HEXA_TO_BYTE_ERROR);
		}
		
		String exchangeId = exchange.getExchangeId().replace("-", "");
		String hexStringForCorId = StringUtils.rightPad(exchangeId, 48, '0');
	
		exchange.getIn().setHeader("JMSCorrelationID", "ID:" + hexStringForCorId);		
		exchange.getIn().setHeader("JMS_IBM_MQMD_MsgId", customMessageId);
		exchange.getIn().setHeader("JMSDeliveryMode", DELIVERY_MODE);
		exchange.getIn().setHeader("JMS_IBM_Character_Set", CHAR_SET_ID);


		logger.info("{} - Transaction Id : {}, JMS messageId is set to : {}, JMS correlationId is set to : {}  ",
				methodName, exchange.getExchangeId(), hexStringForMsgId, hexStringForCorId);
	}

}
