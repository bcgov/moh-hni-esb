package ca.bc.gov.hlth.hnsecure.audit;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_SENDING_FACILITY;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;

public class TransactionProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		
    	String methodName = LoggingUtil.getMethodName();
        logger.debug("Begin {}", methodName);

    	Map<String, Object> headers = exchange.getIn().getHeaders();

    	String transactionId = exchange.getProperty(Exchange.CORRELATION_ID, String.class);
		Transaction t = new Transaction();
    	t.setTransactionId(UUID.fromString(transactionId));
		t.setType((String)headers.get(HEADER_MESSAGE_TYPE));
		t.setServer(""); //TODO (dbarrett) check if we need this, it's the name of the server that processed the transaction. (this may not be relevant in openshift), and if so where it is taken from.
		t.setSource((String)headers.get(HEADER_SENDING_FACILITY));
//		t.setOrganization();
//		t.setUserId();	
    	t.setFacilityId((String)headers.get(HEADER_SENDING_FACILITY));
    	t.setTimestamp(new Date());
        	
        try {        	
        	insert(t);
        } catch (Exception ex) {
        	logger.error("Exception {}", ex.getMessage());
        }							
        logger.debug("End {}", methodName);
	}
}
