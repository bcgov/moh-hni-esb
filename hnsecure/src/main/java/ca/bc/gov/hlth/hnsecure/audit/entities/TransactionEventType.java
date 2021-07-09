package ca.bc.gov.hlth.hnsecure.audit.entities;

public enum TransactionEventType {

	ERROR("ERROR"),
	INVALID("Invalid"),
	MESSAGE_RECEIVED("Message Received"),
	MESSAGE_SENT("Message Sent"),
	TRANSACTION_COMPLETE("Transaction Complete"),
	UNAUTHENTICATED("Unauthenticated");

	private String value;

	private TransactionEventType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
