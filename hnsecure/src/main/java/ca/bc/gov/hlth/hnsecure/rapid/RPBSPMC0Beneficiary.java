package ca.bc.gov.hlth.hnsecure.rapid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class RPBSPMC0Beneficiary {
	public static final int SEGMENT_LENGTH = 491; //155-645
	
	//Total Beneficiary fields length 101	
	/** 1 PHN String No 0...10 .. */
	private String phn;	
	/** 2 Last Name No 0...35 .. */
	private String lastName;
	/** 3 First Name No 0...45 .. */
	private String firstName;
	/** 4 Birth Date No 0...10 .. */
	private String birthDate;
	/** 5 Gender 0...1 .. */
	private String gender;

	/** 6 Contract Periods 0...390 .. */
	private List<RPBSPMC0ContractPeriod> contractPeriods = new ArrayList<>();
		
	public RPBSPMC0Beneficiary(String message) {
		super();
		this.phn = StringUtils.substring(message, 0, 10);
		this.lastName = StringUtils.substring(message, 10, 45);
		this.firstName = StringUtils.substring(message, 45, 90);
		this.birthDate = StringUtils.substring(message, 90, 100);
		this.gender = StringUtils.substring(message, 100, 101);
		
		//Build the remaining response from repeating coverage period segments for each beneficiary

		String contractPeriodsStr = StringUtils.substring(message, 101);

		if (StringUtils.isNotBlank(contractPeriodsStr)) {
			int count = 0;
			String contractPeriod = StringUtils.substring(contractPeriodsStr, 0, RPBSPMC0ContractPeriod.SEGMENT_LENGTH);
			while (StringUtils.isNotBlank(contractPeriod)) {
				RPBSPMC0ContractPeriod rpbspmc0ContractPeriod = new RPBSPMC0ContractPeriod(contractPeriod);
				contractPeriods.add(rpbspmc0ContractPeriod);
				count++;
				contractPeriod = StringUtils.substring(contractPeriodsStr, RPBSPMC0ContractPeriod.SEGMENT_LENGTH * count, RPBSPMC0ContractPeriod.SEGMENT_LENGTH * (count + 1));
			}
		}
	}
	
	public String getPhn() {
		return phn;
	}

	public void setPhn(String phn) {
		this.phn = phn;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<RPBSPMC0ContractPeriod> getContractPeriods() {
		return contractPeriods;
	}

	public void setContractPeriods(List<RPBSPMC0ContractPeriod> contractPeriods) {
		this.contractPeriods = contractPeriods;
	}

	@Override
	public String toString() {
		return "RPBSPMC0Beneficiary [phn=" + phn + ", lastName=" + lastName + ", firstName=" + firstName
				+ ", birthDate=" + birthDate + ", gender=" + gender + "]";
	}

}
