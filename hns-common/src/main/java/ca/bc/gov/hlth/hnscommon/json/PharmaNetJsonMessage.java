package ca.bc.gov.hlth.hnscommon.json;

/**
 *	Represents the PhamraNet message as a java object
 *
 *	@author dave.p.barrett
 *
 */
public class PharmaNetJsonMessage {

	private String transactionUUID;
	private String hl7Message;
	
	public String getTransactionUUID() {
		return transactionUUID;
	}
	public void setTransactionUUID(String transactionUUID) {
		this.transactionUUID = transactionUUID;
	}
	public String getHl7Message() {
		return hl7Message;
	}
	public void setHl7Message(String hl7Message) {
		this.hl7Message = hl7Message;
	}	
	
}
