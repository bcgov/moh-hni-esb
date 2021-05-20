package ca.bc.gov.hlth.hnsecure.filedrops;


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

/**
 * This implementation logs each HL7v2 request/response message in a file in its raw/text form.
 * file name format:{messageid}-{messagetype}-{facilityid}-{messagedate}-{request/response}.txt
 * @author anumeha.srivastava
 *
 */
public class V2FileDropsRequest {
	
private static final Logger logger = LoggerFactory.getLogger(V2FileDropsRequest.class);
	
	public final static String REQUEST_FILE = "request.txt";
	public final static String RESPONSE_FILE = "response.txt";
	
	@Handler
	public void createFileDrops(Exchange exchange) {		
		String v2MsgRequest = exchange.getIn().getBody().toString();		
		String accessToken = (String) exchange.getIn().getHeader("Authorization");
		String fileName = Util.buildFileName(v2MsgRequest,accessToken,exchange.getIn().getMessageId());
	    writeRequest(exchange,fileName);   
	}

	/**
	 * writes v2 request message to file
	 * @param exchange
	 * @param fileName
	 */
	private void writeRequest(Exchange exchange, String fileName) {
		String requestFileName = fileName+REQUEST_FILE;
		Path p = Paths.get("./"+ requestFileName);

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
