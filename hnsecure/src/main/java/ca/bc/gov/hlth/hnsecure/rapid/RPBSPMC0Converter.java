package ca.bc.gov.hlth.hnsecure.rapid;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_PROCESSING_DOMAIN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_USER_INFO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
import ca.bc.gov.hlth.hnsecure.message.RapidV2MessageUtil;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIA;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIH;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSA;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PID;

public class RPBSPMC0Converter {

	private static final Logger logger = LoggerFactory.getLogger(RPBSPMC0Converter.class);
	
	private static final String PID_NAMESPACE_ID = "BC";

	private static final String PID_ID_TYPE_CODE = "PH";

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddkkmmss");

	private String messageDateTime = LocalDateTime.now().format(dateTimeFormatter);

	public RPBSPMC0Converter() {
		super();
	}

	@Handler
	public String convertResponse(String responseMessage, Exchange exchange) {
		String methodName = LoggingUtil.getMethodName();
		StringBuffer v2Response = new StringBuffer();
		RPBSPMC0 rpbspmc0 = new RPBSPMC0(responseMessage);
		HL7Message mshdefaults = getHL7Message(exchange);
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
				// Remove beneficiary MSH segment
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

					populateIN1(contractPeriod.getIN1(), convertDate(cp.getEffectiveDate()),
							convertDate(cp.getCancelDate()), cp.getGroupNumber(), "", "");
					// Remove beneficiary contract period MSH segment
					StringBuffer formattedResponse = new StringBuffer(contractPeriod.toString()).replace(0, 8, "");
					v2Response.append(formattedResponse.toString());
					// v2Response.append(formattedResponse.toString().trim());

				} catch (HL7Exception e) {

					logger.error("{} - TransactionId: {}, Exception while converting contract period {}", methodName,
							exchange.getExchangeId(), e.getMessage());

				}

			});
		});

		logger.debug(v2Response.toString());
		return v2Response.toString();
	}

	private HL7Message getHL7Message(Exchange exchange) {
		// MSH|^~\&|HNWeb|E13DD6BB-69197950C48|RAIGT-CNT-PRDS|BC00001013|20210820125|anu-test|R32|20220920115331|D
		HL7Message mshdefaults = new HL7Message();
		mshdefaults.setProcessingId(exchange.getProperty(PROPERTY_PROCESSING_DOMAIN).toString());
		mshdefaults.setReceivingFacility(exchange.getProperty(PROPERTY_SENDING_FACILITY).toString());
		mshdefaults.setReceivingApplication(exchange.getProperty(PROPERTY_SENDING_APP).toString());
		mshdefaults.setSendingApplication(exchange.getProperty(PROPERTY_RECEIVING_APP).toString());
		mshdefaults.setSendingFacility(exchange.getProperty(PROPERTY_RECEIVING_FACILITY).toString());
		mshdefaults.setUser(exchange.getProperty(PROPERTY_USER_INFO).toString());
		mshdefaults.setMessageType(exchange.getProperty(PROPERTY_MESSAGE_TYPE).toString());
		mshdefaults.setMessageControlId(messageDateTime);
		return mshdefaults;
	}

	private void populateMSH(MSH msh, HL7Message mshDefaults) throws HL7Exception {
		RapidV2MessageUtil.setMshValues(msh, mshDefaults.getSendingApplication(), mshDefaults.getSendingFacility(),
				mshDefaults.getReceivingApplication(), mshDefaults.getReceivingFacility(), messageDateTime,
				mshDefaults.getUser(), mshDefaults.getMessageType(),
				StringUtils.substring(mshDefaults.getMessageControlId(), 0, 20), mshDefaults.getProcessingId());
	}

	private void populatePID(PID pid, String phn) throws HL7Exception {
		RapidV2MessageUtil.setPidValues(pid, phn, PID_NAMESPACE_ID, PID_ID_TYPE_CODE, "", "", "");
	}

	private void populateZIA(ZIA zia, String bcResidencyDate, String surname, String firstGivenName,
			String secondGivenName, String telephone, String immigrationOrVisaCode, String priorResidenceCode)
			throws HL7Exception {

		RapidV2MessageUtil.setZiaValues(zia, bcResidencyDate, surname, firstGivenName, secondGivenName, telephone,
				immigrationOrVisaCode, priorResidenceCode);
	}

	private void populateMSA(MSA msa, String message) throws HL7Exception {
		RapidV2MessageUtil.setMSAValues(msa, message);
	}

	private void populateNK1(NK1 nk1, String relationship) throws HL7Exception {
		RapidV2MessageUtil.setNK1Values(nk1, relationship);
	}

	private void populateIN1(IN1 in1, String planEffectiveDate, String planCancellationDate, String groupNumber,
			String groupMemberNumber, String departmentNumber) throws HL7Exception {
		RapidV2MessageUtil.setIn1Values(in1, groupNumber, groupMemberNumber, departmentNumber, planEffectiveDate,
				planCancellationDate);
	}

	private void populateZIH(ZIH zih, String payerCancelReason) throws HL7Exception {
		RapidV2MessageUtil.setZihValues(zih, payerCancelReason);
	}

	private String convertDate(String date) {
		if (date.contentEquals("0000-00-00")) {
			return "";
		}
		String strDate = date;
		DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date formattedDate = new Date();
		try {
			formattedDate = (Date) inputFormatter.parse(strDate);

		} catch (ParseException e) {
			logger.error("Error whhile parsing effective/cancellation date: {}", e.getMessage());
		}

		DateFormat outputFormatter = new SimpleDateFormat("yyyyMMdd");
		String strDateTime = outputFormatter.format(formattedDate);

		return strDateTime;
	}

}