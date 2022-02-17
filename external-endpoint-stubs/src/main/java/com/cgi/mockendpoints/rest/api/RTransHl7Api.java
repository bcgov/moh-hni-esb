package com.cgi.mockendpoints.rest.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contains stubbed out endpoints for RTrans.
 * 
 */
@RestController
public class RTransHl7Api {

	private static final String R09_RESPONSE_MESSAGE = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
			+ "MSA|AA|20191108082211|HJMB001ISUCCESSFULLY COMPLETED\r\n" + "ZTL|2^RD\r\n"
			+ "PID|1|1314500002^^^BC^PH|||||1989|M\r\n" + "PID|2|2564500001^^^BC^PH|||||1973|M\r\n"
			+ "ZIA|||||||||||||||Branton^James^^^^^|||||||1\r\n" + "ZIA|||||||||||||||Branton^Debbie^^^^^|||||||2\r\n";
	
	private static final String HTTP_CONTENT_TYPE = "text/plain; charset=utf-8";	
	private static final Logger logger = LoggerFactory.getLogger(RTransHl7Api.class);

	/**
	 * Mocked out endpoint for imitating a request sent to RTrans
	 * @param String v2Message
	 * @return
	 */
	@PostMapping("/rtrans")
	public ResponseEntity<String> createRTransMessage(@Valid @RequestBody String requestBody) {
		logger.info("Received RTras HL7 Message: {}", requestBody);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, HTTP_CONTENT_TYPE );
		logger.info("Returning new HL7 Message: {}", R09_RESPONSE_MESSAGE);
		return new ResponseEntity<>(R09_RESPONSE_MESSAGE, headers, HttpStatus.OK);
	}
}
