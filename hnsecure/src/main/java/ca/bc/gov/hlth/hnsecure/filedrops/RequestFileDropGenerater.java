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
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

/**
 * This implementation logs each HL7v2 request/response message in a file in its raw/text form.
 * file name format:{messageid}-{messagetype}-{facilityid}-{messagedate}-{request/response}.txt
 * @author anumeha.srivastava
 *
 */
public class RequestFileDropGenerater {
	
private static final Logger logger = LoggerFactory.getLogger(RequestFileDropGenerater.class);
	
	private static final String REQUEST_FILE = "request.txt";
	private static final ApplicationProperties properties = ApplicationProperties.getInstance();
	
	
	@Handler
	public void createFileDrops(Exchange exchange) {		
		String v2MsgRequest = exchange.getIn().getBody().toString();		
		String accessToken = (String) exchange.getIn().getHeader("Authorization");
		String msgType = Util.getMsgType(v2MsgRequest);
		String sendingFacility = Util.getSendingFacility(accessToken);
		String fileName = Util.buildFileName(sendingFacility,exchange.getIn().getMessageId(), msgType);
	    writeRequest(exchange,fileName);   
	}

	/**
	 * writes v2 request message to file
	 * @param exchange
	 * @param fileName
	 */
	private void writeRequest(Exchange exchange, String fileName) {
		String requestFileName = fileName+REQUEST_FILE;		
		String fileLocation = properties.getValue(FILE_DROPS_LOCATION);
		Path p = Paths.get(fileLocation + requestFileName);

	    try (OutputStream out = new BufferedOutputStream(
	      Files.newOutputStream(p, CREATE))) {    		       
	    	PrintWriter printWriter = new PrintWriter(out);	
	    	printWriter.print(exchange.getIn().getBody());			
			printWriter.close();
			logger.info("{} - TransactionId: {}, Successfully created file drops for request: {}",Util.getMethodName(), exchange.getIn().getMessageId(), requestFileName);	        
	    
	    } catch (IOException ioe) {
	      logger.error(ioe.getMessage());
	    }
	}
	

}
