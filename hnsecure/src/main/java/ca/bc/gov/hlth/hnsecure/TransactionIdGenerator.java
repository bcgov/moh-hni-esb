package ca.bc.gov.hlth.hnsecure;

import java.util.UUID;

import org.apache.camel.spi.UuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation uses a Java.util.UUID to generate a unique Id
 * for logging and tracing
 */
public class TransactionIdGenerator implements UuidGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionIdGenerator.class);

	@Override
	public String generateUuid() {
	    UUID uuid = UUID.randomUUID();		
        return uuid.toString();
	}

}
