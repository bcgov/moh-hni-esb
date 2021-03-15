package ca.bc.gov.hlth.hnsecure.message;

public interface ResponseSegment {
	
	public String constructMSH(HL7Message messageObj);
	
	public String consructResponse(HL7Message messageObj, String Id, ErrorMessage error);

}
