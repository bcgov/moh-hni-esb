package com.cgi.mockendpoints.rest.model;

/**
 * PharmaNet Message model
 * 
 */
public class PharmaNetMessageModel {

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

	@Override
	public String toString() {
		return "PharmaNetMessageModel [transactionUUID=" + transactionUUID + ",\nhl7Message=" + hl7Message + "]";
	}
		
}
