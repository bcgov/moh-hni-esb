package ca.bc.gov.hlth.hnsecure.audit;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_SENDING_FACILITY;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.SegmentType;

public class TransactionProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		
    	String methodName = LoggingUtil.getMethodName();
        logger.debug("Begin {}", methodName);

    	Map<String, Object> headers = exchange.getIn().getHeaders();
    	String v2Message = (String)exchange.getIn().getBody();

    	String transactionId = exchange.getProperty(Exchange.CORRELATION_ID, String.class);
		Transaction transaction = populateTransaction(headers, transactionId);
        	
        try {        	
        	insert(transaction);
        } catch (Exception ex) {
        	logger.error("Exception {}", ex.getMessage());
        }			
        
        
        //Affected Party - Get info for R03, R09, R15, E45, R50, R09(only for response)        
        AffectedParty affectedParty = populateAffectedParty(v2Message, transactionId);
        insert(affectedParty);
        
        logger.debug("End {}", methodName);
	}


	private Transaction populateTransaction(Map<String, Object> headers, String transactionId) {
		Transaction transaction = new Transaction();
    	transaction.setTransactionId(UUID.fromString(transactionId));
		transaction.setType((String)headers.get(HEADER_MESSAGE_TYPE));
		transaction.setServer(""); //TODO (dbarrett) check if we need this, it's the name of the server that processed the transaction. (this may not be relevant in openshift), and if so where it is taken from.
		transaction.setSource((String)headers.get(HEADER_SENDING_FACILITY));
//		t.setOrganization();
//		t.setUserId();	
    	transaction.setFacilityId((String)headers.get(HEADER_SENDING_FACILITY));
    	transaction.setTimestamp(new Date());
		return transaction;
	}
	
	
	public AffectedParty populateAffectedParty(String v2Message, String transactionId) {
		AffectedParty affectedParty = new AffectedParty();
//	    affectedParty.setTransaction(transactionId);						
		
		if (StringUtils.isNotEmpty(v2Message)) {
			String[] segments = V2MessageUtil.getMessageSegments(v2Message);				
			String segment;
			String[] segmentFields;
			String[] patientIdentifierSections;			
			
			String msgType = V2MessageUtil.getMsgType(v2Message);
			if (StringUtils.isNotEmpty(msgType)) {
				MessageType messageType = MessageType.valueOf(msgType);
				
				switch (messageType) {
				case ZPN:
					//PharmaNet has client info in ZCC and ZPA
					//ZCC e.g.
					//ZCC||||||||||0009735000001|
					segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZCC);
					segmentFields = V2MessageUtil.getSegmentFields(segment);
					affectedParty.setIdentifier(V2MessageUtil.getIdentifierSectionZCC(segmentFields));
//					affectedParty.setIdentifierSource(patientIdentifierSections[3]);
//					affectedParty.setIdentifierType(patientIdentifierSections[4]);
//					affectedParty.setStatus("");
					break;
				case E45:
					//QPD e.g.
					//QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19421112||||||19980601||PVC^^HNET9909
					segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.QPD);
					segmentFields = V2MessageUtil.getSegmentFields(segment);
					patientIdentifierSections = V2MessageUtil.getIdentifierSectionsQPD(segmentFields);			
					affectedParty.setIdentifier(patientIdentifierSections[0]);
					affectedParty.setIdentifierSource(patientIdentifierSections[3]);
					affectedParty.setIdentifierType(patientIdentifierSections[4]);
					affectedParty.setStatus("");
					break;
				case R03:;
				case R09:;
				case R15:;
				case R50:
					/* PID e.g. 
					PID||0891250000^^^BC^PH 
					*/
					segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.PID);
					segmentFields = V2MessageUtil.getSegmentFields(segment);
					patientIdentifierSections = V2MessageUtil.getIdentifierSectionsPID(segmentFields);					
					//affectedParty
					affectedParty.setIdentifier(patientIdentifierSections[0]);
					affectedParty.setIdentifierSource(patientIdentifierSections[3]);
					affectedParty.setIdentifierType(patientIdentifierSections[4]);
					affectedParty.setStatus("");
					break;				
				}
			}
		}
		return affectedParty;
	}

}
