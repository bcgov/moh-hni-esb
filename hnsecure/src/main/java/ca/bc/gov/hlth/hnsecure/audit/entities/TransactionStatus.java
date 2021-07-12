package ca.bc.gov.hlth.hnsecure.audit.entities;

public enum TransactionStatus {

	/**
	 * Transaction has started (a v2 message has been recieved from the
	 */
	TRANSACTION_START("Transaction Start"),
	/**
	 * Message has been forwarded to an underlying system
	 */
	MESSAGE_SENT("Message Sent"),
	/**
	 * Response from underlying system has been received.
	 */
	MESSAGE_RECEIVED("Message Received"),
	/**
	 * Transaction has completed (not necessarily successfully).
	 */
	TRANSACTION_COMPLETE("Transaction Complete");

	private String value;

	private TransactionStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
