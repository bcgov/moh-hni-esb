package ca.bc.gov.hlth.hnsecure.rapid;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.R32;
import ca.bc.gov.hlth.hnsecure.message.R32Beneficiary;
import ca.bc.gov.hlth.hnsecure.message.R32ContractPeriods;
import ca.bc.gov.hlth.hnsecure.message.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIA;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIH;
import ca.bc.gov.hlth.hnsecure.parsing.FormatRTransResponse;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.segment.ERR;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSA;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PID;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_USER_INFO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RPBSPMC0Converter {

	private static final Logger logger = LoggerFactory.getLogger(FormatRTransResponse.class);
	protected static final String PID_NAMESPACE_ID = "BC";

	protected static final String PID_ID_TYPE_CODE = "PH";

	protected static final String SUCCESS_MESSAGE = "TRANSACTION COMPLETED";

	protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddkkmmss");

	protected String messageDateTime = LocalDateTime.now().format(dateTimeFormatter);

	protected HL7Message mshDefaults;

	protected static DateTimeFormatter dateOnlyFormatter = DateTimeFormatter
			.ofPattern(V2MessageUtil.DATE_FORMAT_DATE_ONLY);

	private static final String TRAN_CODE = "RPBSPMC0";
	private static final String ZERO_DATE = "0000-00-00"; // Downstream returns this value when no date is available
	private static final String R32_SUCCESS = "        RPBSPMC000000010                                RESPONSERPBS9014TRANSACTION SUCCESSFUL                                                  98736722489873672248SPBIGDATASNAME                     SPBIGDATAFNAME                               1983-01-01F98736722550000001S2022-02-010000-00-00 98736722484044574C2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                         9873672255BIGDATASNAME                       BIGDATAFNAME                                 1983-09-09M98736722550000001C2022-02-010000-00-00 98736722484044574S2022-02-012022-02-28E                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              ";

	private static final String R32_WARNING_MORE_THAN_20_PERSONS_FOUND = "        RPBSPMC000000010                                INFO    RPBS0086MORE THAN 20 PERSONS FOUND - NOT ALL DISPLAYED                          98736722559873672255BIGDATASNAME                       BIGDATAFNAME                                 1983-09-09M98736722550000001C2022-02-010000-00-00 98736722484044574S2022-02-012022-02-28E                                                                                                                                                                                                                                                                                                                        9873672248SPBIGDATASNAME                     SPBIGDATAFNAME                               1983-01-01F98736722550000001S2022-02-010000-00-00 98736722484044574C2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                         9873671593CHSIXTNBIGDSNAME                   CHSIXTNBIGFNAME                              2018-01-16M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671601CHFRTENBDSNAME                     CHFRTNBDFNAME                                2018-01-14M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671769CHTRTBIGDTSNAME                    CHTRTBIGDATFNAM                              2018-01-13M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671776CHTWTREBIGDTSNAME                  CHTWTREBIGDTFNA                              2018-01-23M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671783CHTWNTTWBISNAME                    CHETWNTTWEBFNAM                              2018-01-21M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671809CHTWTONBIGSNAME                    CHTWTONBIGFNAME                              2018-01-21M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671816CHTNTYBIGDTSNAME                   CHNINTYBIGDTFNA                              2018-01-20M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671823CHNINTNBIGDTSNAME                  CHNINTBIGDAFNAM                              2018-01-19F98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671848CHEITNBIGDTSNAME                   CHDITNBIDTAFNAM                              2018-01-18M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671941CHSETNBIGDTSNAME                   CHSEVTNBDTFNAME                              2018-01-17M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671959CHFIFTNBIGDTSNAME                  CHFIFTNBIGDAFNA                              2018-01-15M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671966CHTLEBIGDTSNAME                    CHTLEBIGDTFNAME                              2018-12-12M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671973CHELNBIGDASNAME                    CHELNBIGDFNAME                               2018-11-11M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671998CHTENBIGSNAME                      CHTENBIGDFNAME                               2019-10-10M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672001CHNINBIGDTSNAME                    CHNINBIGDATFNAM                              2019-09-09M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672019CHETBIGDASNAME                     CHETBIGDTFNAME                               2018-08-08M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672026CHSVNBIGDSNAME                     CHSVNBIGDFNAME                               2019-07-07M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672033CHSIXBIGDTSNAME                    CHSIXBIGDTFNAME                              2018-06-06M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                ";

	private static final String R32_ERROR_PHN_NOT_FOUND = "        RPBSPMC000000010                                ERRORMSGRPBS9145PHN NOT FOUND                                                           9159869673		";

	public RPBSPMC0Converter() {
		super();
	}

	/**
	 * public static void main(String args[]) {
	 * 
	 * RPBSPMC0Converter obj = new RPBSPMC0Converter(); RPBSPMC0 request =
	 * obj.convertRequest(""); System.out.println(request.serialize()); RPBSPMC0
	 * rpbspmc0 = new RPBSPMC0(R32_WARNING_MORE_THAN_20_PERSONS_FOUND); try {
	 * obj.convertResponse(rpbspmc0); } catch (HL7Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * public RPBSPMC0 convertRequest(String request) { RPBSHeader rpbsHeader = new
	 * RPBSHeader(); rpbsHeader.setOrganization("MSP");
	 * rpbsHeader.setTranCode(TRAN_CODE);
	 * 
	 * RPBSPMC0Data rpbsmc0Data = new RPBSPMC0Data();
	 * rpbsmc0Data.setPhn("9879869673");
	 * 
	 * RPBSPMC0 rpbspmc0 = new RPBSPMC0(); rpbspmc0.setRpbsHeader(rpbsHeader);
	 * rpbspmc0.setRpbsmc0Data(rpbsmc0Data);
	 * 
	 * return rpbspmc0; }
	 * 
	 * @throws HL7Exception
	 **/
	@Handler
	public String convertResponse(RPBSPMC0 rpbspmc0, Exchange exchange) {
		String methodName = LoggingUtil.getMethodName();
		HL7Message mshdefaults = getHL7Message(exchange);

		StringBuffer v2Response = new StringBuffer();
	
		R32 r32 = new R32();

		try {
			populateMSH(r32.getMSH(), mshdefaults);
			populateMSA(r32.getMSA(), rpbspmc0.getRpbsHeader().getStatusText());
		} catch (HL7Exception e) {
			logger.error("{} - TransactionId: {}, Exception while converting R32 data {}", methodName,
					exchange.getExchangeId(), e.getMessage());
		}
		v2Response.append(r32.toString());
		v2Response.append(
				"ERR|^^^" + rpbspmc0.getRpbsHeader().getStatusCode() + "&" + rpbspmc0.getRpbsHeader().getStatusText());

		rpbspmc0.getRpbsmc0Data().getBeneficiaries().forEach(b -> {
			// Output must be a hl7v2 structure so map contract period to a
			// hl7v2 message
			try {
				R32Beneficiary beneficiary = new R32Beneficiary();
				populateZIA(beneficiary.getZIA(), "", b.getLastName(), b.getFirstName(), "", "", "", "");
				populatePID(beneficiary.getPID(), b.getPhn());
				// remove beneficiary MSH segment
				StringBuffer formattedResponse = new StringBuffer(beneficiary.toString()).replace(0, 8, "");
				v2Response.append(formattedResponse.toString());

			} catch (HL7Exception e) {
				logger.error("{} - TransactionId: {}, Exception while converting beneficiary data {}", methodName,
						exchange.getExchangeId(), e.getMessage());
			}
			b.getContractPeriods().forEach(cp -> {
				try {
					R32ContractPeriods contractPeriod = new R32ContractPeriods();
					populatePID(contractPeriod.getPID(), cp.getPhn());
					populateNK1(contractPeriod.getNK1(), cp.getRelationship());
					if (StringUtils.isNotBlank(cp.getReasonCode())) {
						populateZIH(contractPeriod.getZIH(), cp.getReasonCode());
					}
					populateIN1(contractPeriod.getIN1(), cp.getEffectiveDate(), cp.getCancelDate(),
							cp.getGroupNumber(), "", "");
					// remove beneficiary contract period MSH segment
					StringBuffer formattedResponse = new StringBuffer(contractPeriod.toString()).replace(0, 8, "");
					v2Response.append(formattedResponse.toString());
					v2Response.append(formattedResponse.toString().trim());

				} catch (HL7Exception e) {

					logger.error("{} - TransactionId: {}, Exception while converting contract period {}", methodName,
							exchange.getExchangeId(), e.getMessage());

				}

			});
		});

		logger.debug(v2Response.toString());
		return v2Response.toString();
	}

	/**
	 * @return
	 */
	protected HL7Message getHL7Message(Exchange exchange) {
		// MSH|^~\&|HNWeb|E13DD6BB-69197950C48|RAIGT-CNT-PRDS|BC00001013|20210820125|anu-test|R32|20220920115331|D
		HL7Message mshdefaults = new HL7Message();
		mshdefaults.setProcessingId("D");
		mshdefaults.setReceivingFacility(exchange.getProperty(PROPERTY_RECEIVING_FACILITY).toString());
		mshdefaults.setReceivingApplication(exchange.getProperty(PROPERTY_RECEIVING_APP).toString());
		mshdefaults.setSendingApplication(exchange.getProperty(PROPERTY_SENDING_APP).toString());
		mshdefaults.setSendingFacility(exchange.getProperty(PROPERTY_SENDING_FACILITY).toString());
		mshdefaults.setUser(exchange.getProperty(PROPERTY_USER_INFO).toString());
		mshdefaults.setMessageType(exchange.getProperty(PROPERTY_MESSAGE_TYPE).toString());
		return mshdefaults;
	}

	protected void populateMSH(MSH msh, HL7Message mshdefaults) throws HL7Exception {
		V2MessageUtil.setMshValues(msh, mshDefaults.getSendingApplication(), mshDefaults.getSendingFacility(),
				mshDefaults.getReceivingApplication(), mshDefaults.getReceivingFacility(), messageDateTime,
				mshDefaults.getUser(), mshDefaults.getMessageType(), StringUtils.substring(mshdefaults.getMessageControlId(), 0, 20),
				mshDefaults.getProcessingId());
	}

	protected void populatePID(PID pid, String phn) throws HL7Exception {
		V2MessageUtil.setPidValues(pid, phn, PID_NAMESPACE_ID, PID_ID_TYPE_CODE, "", "", "");
	}

	protected void populateZIA(ZIA zia, String bcResidencyDate, String surname, String firstGivenName,
			String secondGivenName, String telephone, String immigrationOrVisaCode, String priorResidenceCode)
			throws HL7Exception {

		V2MessageUtil.setZiaValues(zia, bcResidencyDate, surname, firstGivenName, secondGivenName, telephone,
				immigrationOrVisaCode, priorResidenceCode);
	}

	protected void populateMSA(MSA msa, String message) throws HL7Exception {
		V2MessageUtil.setMSAValues(msa, message);
	}

	protected void populateNK1(NK1 nk1, String relationship) throws HL7Exception {
		V2MessageUtil.setNK1Values(nk1, relationship);
	}

	protected void populateIN1(IN1 in1, String planEffectiveDate, String planCancellationDate, String groupNumber,
			String groupMemberNumber, String departmentNumber) throws HL7Exception {
		V2MessageUtil.setIn1Values(in1, groupNumber, groupMemberNumber, departmentNumber, planEffectiveDate,
				planCancellationDate);
	}

	protected void populateERR(ERR err, String message) throws HL7Exception {
		V2MessageUtil.setERRValues(err, message);
	}

	protected void populateZIH(ZIH zih, String payerCancelReason) throws HL7Exception {
		V2MessageUtil.setZihValues(zih, payerCancelReason);
	}

}