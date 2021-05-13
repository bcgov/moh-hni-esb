package ca.bc.gov.hlth.hnsecure.messagevalidation;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_InvalidAuthKey;

/**
 * Custom ExceptionHandler added to verify access token in request
 * @author pankaj.kathuria
 *
 */
public class ExceptionHandler implements Processor {


    /**
     * Validates the Hl7V2 transaction type (MSH.8) against the list of valid transaction type (valid-v2-message-types)
     * @param v2Message the hl7v2 message to validate
     */
    
	@Override
	public void process(Exchange exchange) throws Exception {

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
        exchange.getIn().setBody("{ \"error\": \""+CustomError_Msg_InvalidAuthKey.getErrorMessage()+"\" }");
        return;
    
		
	}

}
