package ca.bc.gov.hlth.hnsecure.audit.entities;

public enum EventMessageErrorLevel {

	ERROR("ERROR"),
	REJECT("REJECT"),
	WARNING("WARNING");

	private String value;

	private EventMessageErrorLevel(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
