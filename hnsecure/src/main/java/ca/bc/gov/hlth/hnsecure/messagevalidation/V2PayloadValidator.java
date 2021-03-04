package ca.bc.gov.hlth.hnsecure.messagevalidation;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class V2PayloadValidator {

    private static final Logger logger = LoggerFactory.getLogger(V2PayloadValidator.class);

    private static Set<String> validV2MessageTypes;

    public V2PayloadValidator(AuthorizationProperties authorizationProperties) {
        validV2MessageTypes = authorizationProperties.getValidV2MessageTypes();
    }

    /**
     * Validates the Hl7V2 transaction type (MSH.8) against the list of valid transaction type (valid-v2-message-types)
     * @param v2Message the hl7v2 message to validate
     */
    @Handler
    public static void validate(Exchange exchange, String v2Message) {

        int mshIndex = v2Message.indexOf("MSH|");
        if (mshIndex != 0) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
            exchange.getIn().setBody("{ \"error\": \"Message does not start with MSH and is an invalid v2 message.\" }");
            return;
        }

        String transactionType = v2Message.split("\\|")[8];
        if (!validV2MessageTypes.stream().anyMatch(transactionType::equalsIgnoreCase)) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
            exchange.getIn().setBody("{ \"error\": \"Unsupported v2 transaction type.\" }");
            return;
        }

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

    }
}
