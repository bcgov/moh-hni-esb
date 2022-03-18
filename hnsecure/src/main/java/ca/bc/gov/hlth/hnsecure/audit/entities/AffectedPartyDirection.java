package ca.bc.gov.hlth.hnsecure.audit.entities;

public enum AffectedPartyDirection {

	INBOUND("Inbound"),
	OUTBOUND("Outbound");

	private String value;

	private AffectedPartyDirection(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
