package ca.bc.gov.hlth.hnsecure.message;

public interface ResponseSegment {
	
	String constructMSH(HL7Message messageObj);
	
	String constructResponse(HL7Message messageObj, ErrorMessage error);

}
