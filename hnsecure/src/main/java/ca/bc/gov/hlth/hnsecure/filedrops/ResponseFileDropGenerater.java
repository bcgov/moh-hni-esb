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
public class ResponseFileDropGenerater {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponseFileDropGenerater.class);
	public final static String RESPONSE_FILE = "response.txt";
	
	@Handler
	public void createFileDrops(Exchange exchange) {
		
		String v2MsgRequest = exchange.getIn().getBody().toString();
		String accessToken = (String) exchange.getIn().getHeader("Authorization");
		String msgType = (String)exchange.getIn().getHeader("messageType");
		String sendingFacility = (String)exchange.getIn().getHeader("sendingFacility");
		
		//In case of validation error, headers are not populated
		if(msgType == null)
			msgType = Util.getMsgType(v2MsgRequest);
		
		if(sendingFacility == null)
			sendingFacility = Util.getSendingFacility(accessToken);
		
		/*
		 * When we use wire tap then the tapped exchange has its own unique exchange
		 * id. But the wire tap will store the exchange id from its parent as a
		 * "correlated exchange id".
		 */
		String corId = exchange.getProperty(Exchange.CORRELATION_ID, String.class);		
		
		String fileName = Util.buildFileName(v2MsgRequest,sendingFacility,corId,msgType);	   
	    writeResponse(exchange,fileName);
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
			logger.info("{} - TransactionId: {}, Successfully created file drops for response: {}",Util.getMethodName(),  exchange.getProperty(Exchange.CORRELATION_ID, String.class), responseFileName);	        
	    
	    } catch (IOException ioe) {
	      logger.error(ioe.getMessage());
	    }
	}


}
