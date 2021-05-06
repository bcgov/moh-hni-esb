package ca.bc.gov.hlth.hnsecure;

import java.util.UUID;

import org.apache.camel.spi.UuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation uses a Java.util.UUID to generate a unique Id
 * for logging and tracing
 */
public class IDGenerator implements UuidGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(IDGenerator.class);

	@Override
	public String generateUuid() {
	    UUID uuid = UUID.randomUUID();		
        String id=""+uuid;        
        int uid=id.hashCode();
        String transactionId=String.valueOf(uid);
        id=transactionId.replaceAll("-", "");
        return id;
	}

}
