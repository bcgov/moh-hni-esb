package ca.bc.gov.hlth.hnsecure.messagevalidation;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class V2PayloadValidatorTest {

    private AuthorizationProperties authProps = new AuthorizationProperties(
            "account",
            "moh_hnclient_dev",
            "system/*.write",
            "r03",
            "");

    private static String msgInvalidMSH="|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    private static String msgR03="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    private static String msgR15= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n" +
            "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000073721^^^BC^PH\r\n" +
            "IN1||||||||||||20190228\r\n";

    private final V2PayloadValidator v2PayloadValidator = new V2PayloadValidator(authProps);
    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    @Test
    public void messageDoesntBeginWithMSHTest() {
        v2PayloadValidator.validate(exchange, msgInvalidMSH);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        assertEquals(exchange.getIn().getBody(), "{ \"error\": \"Message does not start with MSH and is an invalid v2 message.\" }");
    }

    @Test
    public void messageIsNotValidV2MessageType() {
        v2PayloadValidator.validate(exchange, msgR15);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        assertEquals(exchange.getIn().getBody(), "{ \"error\": \"Unsupported v2 transaction type.\" }");
    }

    @Test
    public void validMessage() {
        v2PayloadValidator.validate(exchange, msgR03);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
    }
}
