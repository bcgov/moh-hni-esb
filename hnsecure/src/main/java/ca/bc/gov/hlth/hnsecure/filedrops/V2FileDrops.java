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
public class V2FileDrops {
	
	private static final Logger logger = LoggerFactory.getLogger(V2FileDrops.class);
	
	public final static String REQUEST_FILE = "request.txt";
	public final static String RESPONSE_FILE = "response.txt";
	
	@Handler
	public void createFileDrops(Exchange exchange) {		
		String v2MsgRequest = exchange.getProperty("origInBody", String.class);
		String accessToken = (String) exchange.getIn().getHeader("Authorization");
		String fileName = buildFileName(v2MsgRequest,accessToken );
	    writeRequest(exchange,fileName);
	    writeResponse(exchange,fileName);
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
	        //writeRequestFile(exchange, methodName, fileName, out);
	    	PrintWriter printWriter = new PrintWriter(out);	
			printWriter.printf(exchange.getProperty("origInBody", String.class));		
			printWriter.close();
			logger.info("{} - TransactionId: {}, Successfully created file drops for request: {}",Util.getMethodName(), exchange.getIn().getMessageId(), requestFileName);	        
	    
	    } catch (IOException ioe) {
	      logger.error(ioe.getMessage());
	    }
	}

	/**
	 *writes v2 response message to file
	 * @param exchange
	 * @param fileName
	 */
	private void writeResponse(Exchange exchange, String fileName) {
		String responseFileName = fileName+RESPONSE_FILE;
		
		Path p = Paths.get("./"+ responseFileName);

	    try (OutputStream out = new BufferedOutputStream(
	      Files.newOutputStream(p, CREATE))) {    		        
	    	PrintWriter printWriter = new PrintWriter(out);						
			printWriter.print(exchange.getIn().getBody());
			printWriter.close();
			logger.info("{} - TransactionId: {}, Successfully created file drops for response: {}",Util.getMethodName(), exchange.getIn().getMessageId(), responseFileName);	        
	    
	    } catch (IOException ioe) {
	      logger.error(ioe.getMessage());
	    }
	}
	
	/**
	 * @param exchange
	 * @return filename in the format {messageid}-{messagetype}-{facilityid}-{messagedate}-{request/response}.txt
	 */
	public String buildFileName(String v2MsgRequest, String accessToken) {		
		String msgId = Util.getMsgId(v2MsgRequest);
		String msgType = Util.getMsgType(v2MsgRequest);		
		String sendingFacility = Util.getSendingFacility(accessToken);
		String dateTime = Util.getDateTime();		
		String fileName = msgId+"-"+msgType+"-"+sendingFacility+"-"+dateTime+"-";
		return fileName;
	}
	

}
