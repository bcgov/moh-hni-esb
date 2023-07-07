package ca.bc.gov.hlth.hnsecure.rapid;

import org.apache.commons.lang3.StringUtils;

public class RPBSPMC0ContractPeriod {
	public static final int SEGMENT_LENGTH = 39;

	/** 1 PHN String No 0...10 .. */
	private String phn;
	/** 2 Last Name No 0...7 .. */
	private String groupNumber;
	/** 3 First Name No 0...1 .. */
	private String relationship;
	/** 4 Birth Date No 0...10 .. */
	private String effectiveDate;
	/** 5 Cancel Date 0...10 .. */
	private String cancelDate;
	/** 6 Reason No 0...1 .. */
	private String reasonCode = "";

	public RPBSPMC0ContractPeriod(String message) {
		this.phn = StringUtils.substring(message, 0, 10);
		this.groupNumber = StringUtils.substring(message, 10, 17);
		this.relationship = StringUtils.substring(message, 17, 18);
		this.effectiveDate = StringUtils.substring(message, 18, 28);
		this.cancelDate = StringUtils.substring(message, 28, 38);
		this.reasonCode = StringUtils.substring(message, 38, 39);
	}

	public String getPhn() {
		return phn;
	}

	public void setPhn(String phn) {
		this.phn = phn;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public static int getSegmentLength() {
		return SEGMENT_LENGTH;
	}

	@Override
	public String toString() {
		return "RPBSPMC0ContractPeriod [phn=" + phn + ", groupNumber=" + groupNumber + ", relationship=" + relationship
				+ ", effectiveDate=" + effectiveDate + ", cancelDate=" + cancelDate + ", reasonCode=" + reasonCode
				+ "]";
	}

}
