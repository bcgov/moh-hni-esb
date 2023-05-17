package ca.bc.gov.hlth.hnsecure.message;

import org.apache.commons.lang3.StringUtils;

import ca.bc.gov.hlth.hnsecure.message.v2.segment.HDR;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIA;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIH;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.Version;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.segment.ERR;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSA;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.util.Terser;

/**
 * Contains utility methods related to HL7 V2 messages
 *
 */
public class V2MessageUtil {

	public static final String FIELD_SEPARATOR = "|";

	public static final String ENCODING_CHARACTERS = "^~\\&";
	
	public static final String DATE_FORMAT_DATE_ONLY = "yyyyMMdd";
	
	public static final String  DEFAULT_VERSION_ID = "2.4";
		
	public enum SegmentType {
		MSH, PID, QPD, ADJ
	}
	
	public enum AddressType {
		H, M;
	}
	
	public enum TelePhoneUseCode {
		PRN, ORN, WPN, EMR;
	}

    /**
     * Populate MSH segment
     * 
     * @param msh
     * @param sendingApplication
     * @param sendingFacility
     * @param receivingApplication
     * @param receivingFacility
     * @param dateTimeOfMessage
     * @param security
     * @param messageControlID
     * @param processingID
     * @param versionID
     * @param messageType 
     * @throws HL7Exception
     */
	public static void setMshValues(MSH msh, String sendingApplication, String sendingFacility, String receivingApplication, 
    								String receivingFacility, String dateTimeOfMessage, String security, String messageType,  
    								String messageControlID, String processingID) throws HL7Exception {
    	//e.g. MSH|^~\&|HNSECURE|BC01000030|RAIENROL-EMP|BC00001013|20200529114230|10-ANother|R50^Z06|20200529114230|D|2.4||^M
		//e.g. MSH|^~\&|RAIGT-CNT-PRDS|BC00001013|HNWeb|E13DD6BB-69197950C48|20230130113850|anu-test|R32|1963986|D|2.4

    	msh.getMsh3_SendingApplication().parse(sendingApplication);
		msh.getMsh4_SendingFacility().parse(sendingFacility);
		msh.getMsh5_ReceivingApplication().parse(receivingApplication);
		msh.getMsh6_ReceivingFacility().parse(receivingFacility);
		msh.getMsh7_DateTimeOfMessage().parse(dateTimeOfMessage);
		msh.getMsh8_Security().parse(security);
		msh.getMsh9_MessageType().parse(messageType);
		msh.getMsh10_MessageControlID().parse(messageControlID);
		msh.getMsh11_ProcessingID().parse(processingID);
		msh.getMsh12_VersionID().parse(Version.V24.getVersion());
    }

    /**
     * Populate PID segment
	 * 
     * @param pid
     * @param patientIdId
     * @param patientIdAssigningAuthority
     * @param patientIdIdentifierTypeCode
     * @param alternatePatientIdPid
     * @param dateTimeOfBirth
     * @param administrativeSex
     * @throws HL7Exception
     */
    public static void setPidValues(PID pid, String patientId, String patientIdAssigningAuthority, String patientIdIdentifierTypeCode, 
    		String alternatePatientIdPid, String dateTimeOfBirth, String administrativeSex) throws HL7Exception {
    	//e.g. PID||9999999999^^^BC^PH|||||19700303|M^M
    	if(!StringUtils.isEmpty(patientId)) {
    		pid.getPid2_PatientID().getCx1_ID().parse(patientId);
    		pid.getPid2_PatientID().getCx4_AssigningAuthority().parse(patientIdAssigningAuthority);
    		pid.getPid2_PatientID().getCx5_IdentifierTypeCode().parse(patientIdIdentifierTypeCode);
    	  //Pid3_PatientIdentifierList Not Supported     
    		pid.getPid4_AlternatePatientIDPID(0).parse(alternatePatientIdPid);
    	  //Pid5 PatientName Not Supported 
    	}
        pid.getPid7_DateTimeOfBirth().parse(dateTimeOfBirth);
        pid.getPid8_AdministrativeSex().parse(administrativeSex);
    }
    
    /**
     * Populate ZIA segment
	 * 
     * @param zia
     * @param bcResidencyDate
     * @param extendedPersonName
     * @param extendedAddress1
     * @param extendedAddress2
     * @param extendedTelephoneNumber
     * @param immigrationOrVisaCode
     * @param priorResidenceCode
     * @throws HL7Exception
     */
    public static void setZiaValues(ZIA zia, String bcResidencyDate, String surname, String firstGivenName, String secondGivenName,  String telephone, String immigrationOrVisaCode, String priorResidenceCode) throws HL7Exception {
    	//e.g. ZIA||20210101|||||||||||||HELP^RERE^^^^^L|898 RETER ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V8V8^^H~123 UIYUI ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V8V8^^M|^PRN^PH^^^250^8578974|||||||S|AB^M
    	String areaCode = null;
    	String phoneNumber = null;  
    	
    	String[] names = firstGivenName.split("\\s+");
    	String firstName  = names[0];
    	String secondName = names[1];
    	zia.getZia2_BCResidencyDate().parse(bcResidencyDate);
    	zia.getZia15_ExtendedPersonName().parse(StringUtils.trim(surname));   	
    	zia.getZia15_ExtendedPersonName().getGivenName().parse(StringUtils.trim(firstName));
    	zia.getZia15_ExtendedPersonName().getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().parse(StringUtils.trim(secondName));
    	zia.getZia15_ExtendedPersonName().getNameTypeCode().parse("L");;
    
    	if(StringUtils.isNotBlank(telephone)) {   		
    		areaCode = telephone.substring(0,3);
    		phoneNumber = telephone.substring(3);
        	
    		zia.getZia17_ExtendedTelephoneNumber().getAreaCityCode().parse(areaCode);
    		zia.getZia17_ExtendedTelephoneNumber().getTelecommunicationEquipmentType().parse("PH");
    	
    		zia.getZia17_ExtendedTelephoneNumber().getPhoneNumber().parse(phoneNumber);
    		zia.getZia17_ExtendedTelephoneNumber().getTelecommunicationUseCode().parse(TelePhoneUseCode.PRN.name());
    	}
    	
    	zia.getZia24_ImmigrationOrVisaCode().parse(immigrationOrVisaCode);
    	zia.getZia25_PriorResidenceCode().parse(priorResidenceCode);
    }
    
    public static void setZiaExtendedAddrees1(ZIA zia, String addressLine1, String addressLine2, String addressLine3, String city, String province, String postalCode) throws HL7Exception {

    	zia.getZia16_ExtendedAddress(0).getZAD1_AddressLine1().parse(addressLine1);
    	zia.getZia16_ExtendedAddress(0).getZAD2_AddressLine2().parse(addressLine2);  
    	zia.getZia16_ExtendedAddress(0).getZAD3_AddressLine3().parse(addressLine3);  
    	zia.getZia16_ExtendedAddress(0).getZAD20_City().parse(city);
    	zia.getZia16_ExtendedAddress(0).getZAD21_Province().parse(province);
    	zia.getZia16_ExtendedAddress(0).getZAD22_PostalCode().parse(postalCode);
    	zia.getExtendedAddress(0).getZAD24_AddressType().parse(AddressType.H.name());
    }
    
    public static void setZiaExtendedAddrees2(ZIA zia, String mailingAddressLine1,  String mailingAddressLine2,  String mailingAddressLine3, String city, String province, String postalCode) throws HL7Exception {

    	zia.getZia16_ExtendedAddress(1).getZAD1_AddressLine1().parse(mailingAddressLine1);
    	zia.getZia16_ExtendedAddress(1).getZAD2_AddressLine2().parse(mailingAddressLine2);
    	zia.getZia16_ExtendedAddress(1).getZAD3_AddressLine3().parse(mailingAddressLine3);
    	zia.getZia16_ExtendedAddress(1).getZAD20_City().parse(city);
    	zia.getZia16_ExtendedAddress(1).getZAD21_Province().parse(province);
    	zia.getZia16_ExtendedAddress(1).getZAD22_PostalCode().parse(postalCode);
    	zia.getExtendedAddress(1).getZAD24_AddressType().parse(AddressType.M.name());
    }

    /**
     * Populate IN1 segment
	 * 
     * @param in1
     * @param groupNumber
     * @param insuredSGroupEmpID
     * @param insuredSGroupEmpNameIdNumber
     * @param planEffectiveDate
     * @param planExpirationDate
     * @throws HL7Exception
     */
    public static void setIn1Values(IN1 in1, String groupNumber, String insuredSGroupEmpID, String insuredSGroupEmpNameIdNumber, 
    								String planEffectiveDate, String planExpirationDate) throws HL7Exception {
    	//e.g. IN1||||||||6337109||789446|^^123456|20190501|20201231^M
    	
    	in1.getIn18_GroupNumber().parse(groupNumber);
    	in1.getIn110_InsuredSGroupEmpID(0).parse(insuredSGroupEmpID);
    	in1.getIn111_InsuredSGroupEmpName(0).getXon3_IDNumber().parse(insuredSGroupEmpNameIdNumber);
    	in1.getIn112_PlanEffectiveDate().parse(planEffectiveDate);
    	in1.getIn113_PlanExpirationDate().parse(planExpirationDate);
    }
    
    /**
     * Populate IN1 segment
	 * 
     * @param in1
     * @param groupNumber
     * @param insuredSGroupEmpID
     * @param insuredSGroupEmpNameIdNumber
     * @param planEffectiveDate
     * @param planExpirationDate
     * @throws HL7Exception
     */
    public static void setNK1Values(NK1 nk1, String relationship) throws HL7Exception {
    	//e.g.NK1|||SB
    	nk1.getRelationship().parse(relationship);
    }

    /**
     * Populate ZIH segment
	 * 
     * @param zih
     * @param payerCancelReason
     * @throws HL7Exception
     */
    public static void setZihValues(ZIH zih, String payerCancelReason) throws HL7Exception {
    	//e.g. ZIH|||||||||||||||||||D^M
   	
    	zih.getZih19_PayerCancelReason().parse(payerCancelReason);
    }
    
    /**
     * Populate HDR segment
	 * 
     * @param hdr
     * 
     * @throws HL7Exception
     */
	public static void setHdrValues(HDR hdr, String businessUserGroup) throws HL7Exception {
		//e.g. HDR|||TRAININGAdmin
    	
    	hdr.getHDR3_BusinessUserGroup().parse(businessUserGroup);
    }

	
	public static void setMSAValues(MSA msa, String message) throws HL7Exception {
	
		msa.getMsa1_AcknowledgementCode().parse("AA");
		msa.getMsa2_MessageControlID().parse("202204041623");
		msa.getMsa3_TextMessage().parse(message);
	}
	
	public static void setERRValues(ERR err, String message) throws HL7Exception {
	
		// e.g. ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED
		err.getErr1_ErrorCodeAndLocation();
		

	}
	
 
	public static String correctMSH9(String v2, String messageType) {
		// XXX This is to fix the following parsing issue: Can't determine message structure from MSH-9: R15 HINT: there are only 1 of 3 components present]
    	return v2.replaceAll("\\|" + messageType + "\\|", "|" + messageType + "^^|");
	}
	
	public static String getMessageID(Message message) {
    	try {
			return new Terser(message).get("/.MSH-10");
		} catch (HL7Exception e) {
			return null;
		}
   
	}

}
