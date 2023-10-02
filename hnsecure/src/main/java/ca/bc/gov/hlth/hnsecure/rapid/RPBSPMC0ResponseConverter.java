package ca.bc.gov.hlth.hnsecure.rapid;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_PROCESSING_DOMAIN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_USER_INFO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.R32;
import ca.bc.gov.hlth.hnsecure.message.R32Beneficiary;
import ca.bc.gov.hlth.hnsecure.message.R32ContractPeriod;
import ca.bc.gov.hlth.hnsecure.message.V2MessageSegmentUtil;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIA;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIH;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSA;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PID;

public class RPBSPMC0ResponseConverter {

	private static final Logger logger = LoggerFactory.getLogger(RPBSPMC0ResponseConverter.class);

	private static final String PID_NAMESPACE_ID = "BC";

	private static final String PID_ID_TYPE_CODE = "PH";

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddkkmmss");

	private static DateTimeFormatter outputDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private String messageDateTime = LocalDateTime.now().format(dateTimeFormatter);

	public RPBSPMC0ResponseConverter() {
		super();
	}

	@Handler
	public String convertResponse(String responseMessage, Exchange exchange) throws CustomHNSException {
		String methodName = LoggingUtil.getMethodName();

		logger.debug("{} - TransactionId: {}, RAPID response for R32 transaction {}", methodName,
				exchange.getExchangeId(), responseMessage);

		StringBuffer v2Response = new StringBuffer();
		RPBSPMC0 rpbspmc0 = new RPBSPMC0(responseMessage);
		HL7Message mshdefaults = getHL7Message(exchange);
		R32 r32 = new R32();

		try {
			populateMSH(r32.getMSH(), mshdefaults);
			populateMSA(r32.getMSA(), rpbspmc0.getRpbsHeader().getStatusText().trim());
		} catch (HL7Exception e) {
			logger.error("{} - TransactionId: {}, Exception while converting R32 data {}", methodName,
					exchange.getExchangeId(), e.getMessage());
			throw new CustomHNSException(ErrorMessage.CUSTOM_ERROR_INVALID_RESPONSE_MAPPING);
		}
		v2Response.append(r32.toString());

		String err = "ERR|^^^" + rpbspmc0.getRpbsHeader().getStatusCode() + "&"
				+ rpbspmc0.getRpbsHeader().getStatusText().trim();
		v2Response.append(err);
		v2Response.append("\r");

		rpbspmc0.getRpbsmc0Data().getBeneficiaries().forEach(b -> {
			// Output must be a hl7v2 structure so map contract period to a
			// hl7v2 message
			try {
				R32Beneficiary r32Beneficiary = new R32Beneficiary();
				// Remove beneficiary MSH segment
				populateZIA(r32Beneficiary.getZIA(), "", b.getLastName().trim(), b.getFirstName().trim(), "", "", "", "");
				populatePID(r32Beneficiary.getPID(), b.getPhn());
				StringBuffer formattedResponse = new StringBuffer(r32Beneficiary.toString()).delete(0, 8);

				// Remove any leading empty line or whitespace
				v2Response.append(formattedResponse.toString().stripLeading());

			} catch (HL7Exception e) {
				logger.error("{} - TransactionId: {}, Exception while converting beneficiary data {}", methodName,
						exchange.getExchangeId(), e.getMessage());
				throw new IllegalArgumentException("HL7V2 response mapping error detected");
			}
			b.getContractPeriods().forEach(cp -> {
				try {
					R32ContractPeriod r32ContractPeriod = new R32ContractPeriod();
					populatePID(r32ContractPeriod.getPID(), cp.getPhn());
					populateNK1(r32ContractPeriod.getNK1(), cp.getRelationship());
					if (StringUtils.isNotBlank(cp.getReasonCode())) {
						populateZIH(r32ContractPeriod.getZIH(), cp.getReasonCode());
					}

					populateIN1(r32ContractPeriod.getIN1(), convertDate(cp.getEffectiveDate()),
							convertDate(cp.getCancelDate()), cp.getGroupNumber(), "", "");

					// Remove beneficiary contract period MSH segment
					StringBuffer formattedResponse = new StringBuffer(r32ContractPeriod.toString()).delete(0, 8);

					// Remove any leading empty line or whitespace
					v2Response.append(formattedResponse.toString().stripLeading());

				} catch (HL7Exception e) {
					logger.error("{} - TransactionId: {}, Exception while converting contract period {}", methodName,
							exchange.getExchangeId(), e.getMessage());
					throw new IllegalArgumentException("HL7V2 response mapping error detected");

				}

			});
		});

		logger.debug(v2Response.toString());

		return v2Response.toString();
	}

	private HL7Message getHL7Message(Exchange exchange) {
		// MSH|^~\&|HNWeb|E13DD6BB-69197950C48|RAIGT-CNT-PRDS|BC00001013|20210820125|anu-test|R32|20220920115331|D
		HL7Message hl7Message = new HL7Message();
		hl7Message.setProcessingId(exchange.getProperty(PROPERTY_PROCESSING_DOMAIN).toString());
		hl7Message.setReceivingFacility(exchange.getProperty(PROPERTY_SENDING_FACILITY).toString());
		hl7Message.setReceivingApplication(exchange.getProperty(PROPERTY_SENDING_APP).toString());
		hl7Message.setSendingApplication(exchange.getProperty(PROPERTY_RECEIVING_APP).toString());
		hl7Message.setSendingFacility(exchange.getProperty(PROPERTY_RECEIVING_FACILITY).toString());
		hl7Message.setUser(exchange.getProperty(PROPERTY_USER_INFO).toString());
		hl7Message.setMessageType(exchange.getProperty(PROPERTY_MESSAGE_TYPE).toString());
		hl7Message.setMessageControlId(messageDateTime);
		return hl7Message;
	}

	private void populateMSH(MSH msh, HL7Message mshDefaults) throws HL7Exception {
		V2MessageSegmentUtil.setMshValues(msh, mshDefaults.getSendingApplication(), mshDefaults.getSendingFacility(),
				mshDefaults.getReceivingApplication(), mshDefaults.getReceivingFacility(), messageDateTime,
				mshDefaults.getUser(), mshDefaults.getMessageType(),
				StringUtils.substring(mshDefaults.getMessageControlId(), 0, 20), mshDefaults.getProcessingId());
	}

	private void populatePID(PID pid, String phn) throws HL7Exception {
		V2MessageSegmentUtil.setPidValues(pid, phn, PID_NAMESPACE_ID, PID_ID_TYPE_CODE, "", "", "");
	}

	private void populateZIA(ZIA zia, String bcResidencyDate, String surname, String firstGivenName,
			String secondGivenName, String telephone, String immigrationOrVisaCode, String priorResidenceCode)
			throws HL7Exception {

		V2MessageSegmentUtil.setZiaValues(zia, bcResidencyDate, surname, firstGivenName, secondGivenName, telephone,
				immigrationOrVisaCode, priorResidenceCode);
	}

	private void populateMSA(MSA msa, String message) throws HL7Exception {
		V2MessageSegmentUtil.setMSAValues(msa, message);
	}

	private void populateNK1(NK1 nk1, String relationship) throws HL7Exception {
		if (StringUtils.isNotEmpty(relationship)) {
			switch (relationship) {
			case "S":
				relationship = "SP";
				break;
			case "D":
				relationship = "DP";
				break;
			case "C":
				relationship = "SB";
				break;
			}
		}
		V2MessageSegmentUtil.setNK1Values(nk1, relationship);
	}

	private void populateIN1(IN1 in1, String planEffectiveDate, String planCancellationDate, String groupNumber,
			String groupMemberNumber, String departmentNumber) throws HL7Exception {
		V2MessageSegmentUtil.setIn1Values(in1, groupNumber, groupMemberNumber, departmentNumber, planEffectiveDate,
				planCancellationDate);
	}

	private void populateZIH(ZIH zih, String payerCancelReason) throws HL7Exception {
		V2MessageSegmentUtil.setZihValues(zih, payerCancelReason);
	}

	private String convertDate(String date) {
		if (date.equals("0000-00-00")) {
			return "";
		}

		LocalDate parsedDate = LocalDate.parse(date);

		String strDateTime = parsedDate.format(outputDateTimeFormatter);

		return strDateTime;
	}

}