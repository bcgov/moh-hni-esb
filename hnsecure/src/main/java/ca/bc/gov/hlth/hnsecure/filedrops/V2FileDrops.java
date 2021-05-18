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
	public final static String DOUBLE_LINE_BREAK = "%n%n";
	
	@Handler
	public void createFileDrops(Exchange exchange) {
		String methodName = "createFileDrops";
		String v2MsgRequest = exchange.getProperty("origInBody", String.class);
		String accessToken = (String) exchange.getIn().getHeader("Authorization");
		String fileName = buildFileName(v2MsgRequest,accessToken );
	    Path p = Paths.get("./"+ fileName);

	    try (OutputStream out = new BufferedOutputStream(
	      Files.newOutputStream(p, CREATE))) {    	
	        writeFile(exchange, methodName, fileName, out);
	    
	    } catch (IOException ioe) {
	      logger.error(ioe.getMessage());
	    }
	}

	/**
	 * @param exchange
	 * @param methodName
	 * @param fileName
	 * @param out
	 */
	private void writeFile(Exchange exchange, String methodName, String fileName, OutputStream out) {
		PrintWriter printWriter = new PrintWriter(out);
		printWriter.printf("Transaction Id: %s", exchange.getIn().getMessageId());
		printWriter.printf(DOUBLE_LINE_BREAK );
		printWriter.printf("TimeStamp: %s", Util.getPharmanetDateTime());
		printWriter.printf(DOUBLE_LINE_BREAK );
		printWriter.printf("Request: %n%s", exchange.getProperty("origInBody", String.class));
		printWriter.printf(DOUBLE_LINE_BREAK );;
		printWriter.printf("Response: %n%s", exchange.getIn().getBody());
		printWriter.close();
		logger.info("{} - TransactionId: {}, Successfully created file drops: {}",methodName, exchange.getIn().getMessageId(), fileName);
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
		
		String fileName = msgId+"-"+msgType+"-"+sendingFacility+"-"+dateTime+"-"+"request.txt";
		return fileName;
	}
	

}
