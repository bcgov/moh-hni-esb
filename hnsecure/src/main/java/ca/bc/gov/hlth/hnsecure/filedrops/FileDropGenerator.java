package ca.bc.gov.hlth.hnsecure.filedrops;

import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.FILE_DROPS_LOCATION;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

/**
 * This implementation logs each HL7v2 request/response message in a file in its
 * raw/text form. file name
 * format:{messageid}-{messagetype}-{facilityid}-{messagedate}-{request/response}.txt
 *
 */
public abstract class FileDropGenerator {

	private static final Logger logger = LoggerFactory.getLogger(FileDropGenerator.class);
	private static final ApplicationProperties properties = ApplicationProperties.getInstance();

	/**
	 * @param exchange
	 * @return
	 */
	protected String buildFileNameParameters(Exchange exchange, String transactionid) {		
		String accessToken = (String) exchange.getIn().getHeader(Util.AUTHORIZATION);
		String msgType = (String)exchange.getProperty(Util.PROPERTY_MESSAGE_TYPE);
		String sendingFacility = (String)exchange.getProperty(Util.PROPERTY_SENDING_FACILITY);
		
		//In case of validation error, headers are not populated
		if(msgType == null) {
			String v2MsgRequest = exchange.getIn().getBody().toString();
			msgType = V2MessageUtil.getMsgType(v2MsgRequest);
		}
					
		if(sendingFacility == null) {
			sendingFacility = Util.getSendingFacility(accessToken);
		}
		
		return Util.buildFileName(sendingFacility,transactionid,msgType);	
	}

	/**
	 * writes v2 request/response message to file
	 * @param message
	 * @param fileName
	 */
	protected void writeFiledrop(String message, String fileName) {
		String requestFileName = fileName;	
		String fileLocation = properties.getValue(FILE_DROPS_LOCATION);
		Path p = Paths.get(fileLocation + requestFileName);

	    try (OutputStream out = new BufferedOutputStream(
	      Files.newOutputStream(p, CREATE))) {    		       
	    	PrintWriter printWriter = new PrintWriter(out);	
	    	printWriter.print(message);			
			printWriter.close();
	    
	    } catch (IOException ioe) {
	      logger.error(ioe.getMessage());
	    }
	}

}
