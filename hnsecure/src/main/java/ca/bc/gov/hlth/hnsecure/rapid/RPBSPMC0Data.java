package ca.bc.gov.hlth.hnsecure.rapid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class RPBSPMC0Data {

	/** 1 PHN String No 0...10 .. */
	private String phn; // 145-154 The PHN from the request

	private List<RPBSPMC0Beneficiary> beneficiaries = new ArrayList<>();

	public RPBSPMC0Data() {
		super();
	}

	public RPBSPMC0Data(String message) {
		super();
		this.phn = StringUtils.substring(message, 0, 10);

		// Build the remaining response from repeating beneficiaries

		String beneficiariesStr = StringUtils.substring(message, 10);

		if (StringUtils.isNotBlank(beneficiariesStr)) {
			int count = 0;
			String beneficiary = StringUtils.substring(beneficiariesStr, 0, RPBSPMC0Beneficiary.SEGMENT_LENGTH);
			while (StringUtils.isNotBlank(beneficiary)) {
				RPBSPMC0Beneficiary rpbspmc0Beneficiary = new RPBSPMC0Beneficiary(beneficiary);
				beneficiaries.add(rpbspmc0Beneficiary);
				count++;
				beneficiary = StringUtils.substring(beneficiariesStr, RPBSPMC0Beneficiary.SEGMENT_LENGTH * count,
						RPBSPMC0Beneficiary.SEGMENT_LENGTH * (count + 1));
			}
		}
	}

	public String serialize() {
		// Serialize is only used when creating the request
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.rightPad(phn, 10));

		return sb.toString();
	}

	public String getPhn() {
		return phn;
	}

	public void setPhn(String phn) {
		this.phn = phn;
	}

	public List<RPBSPMC0Beneficiary> getBeneficiaries() {
		return beneficiaries;
	}

	public void setBeneficiaries(List<RPBSPMC0Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	@Override
	public String toString() {
		return "RPBSPMC0Data [phn=" + phn + "]";
	}

}
