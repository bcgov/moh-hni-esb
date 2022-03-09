package ca.bc.gov.hlth.hnsecure.messagevalidation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

public class ExceptionHandlerTest extends TestPropertiesLoader {

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    @Test
    public void testProcess_CustomError_Msg_MissingAuthKey() {    	
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new CustomHNSException(ErrorMessage.CUSTOM_ERROR_MISSING_AUTH_KEY));
        new ExceptionHandler().process(exchange);
        assertErrorMessageHandled(ErrorMessage.CUSTOM_ERROR_MISSING_AUTH_KEY, HttpStatus.SC_UNAUTHORIZED);
    }
    
    @Test
    public void testProcess_CustomError_Msg_InvalidAuthKey() {    	
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new CustomHNSException(ErrorMessage.CUSTOM_ERROR_INVALID_AUTH_KEY));
        new ExceptionHandler().process(exchange);
        assertErrorMessageHandled(ErrorMessage.CUSTOM_ERROR_INVALID_AUTH_KEY, HttpStatus.SC_UNAUTHORIZED);
    }
    
    @Test
    public void testProcess_CustomError_Msg_DownstreamConnectionFailed() {    	
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new HttpHostConnectException(new IOException(), null));
        new ExceptionHandler().process(exchange);
        assertErrorMessageHandled(ErrorMessage.CUSTOM_ERROR_DOWN_STREAM_CONNECTION_FAILED, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
    
    @Test
    public void testProcess_CustomError_Msg_InvalidRequest() {    	
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new CustomHNSException(ErrorMessage.CUSTOM_ERROR_INVALID_REQUEST));
        new ExceptionHandler().process(exchange);
        assertErrorMessageHandled(ErrorMessage.CUSTOM_ERROR_INVALID_REQUEST, HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void testProcess_HL7Error_Msg_Unknown() {    	
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new CamelException());
        new ExceptionHandler().process(exchange);
        assertErrorMessageHandled(ErrorMessage.HL7_ERROR_UNKNOWN, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
    
    @Test
    public void testProcess_HL7Error_Msg_NoInputHL7() {    	
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new CustomHNSException(ErrorMessage.HL7_ERROR_NO_INPUT));
        new ExceptionHandler().process(exchange);
        assertErrorMessageHandled(ErrorMessage.HL7_ERROR_NO_INPUT, HttpStatus.SC_BAD_REQUEST);
    }
    
    private void assertErrorMessageHandled(ErrorMessage errorMessage, int httpStatusCode) {
        String body = exchange.getIn().getBody().toString(); 
        assertTrue(body.contains(errorMessage.getErrorSequence()));
        assertTrue(body.contains(errorMessage.getMessage()));
        
        assertEquals(httpStatusCode, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));

    }
 
}
