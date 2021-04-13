package com.cgi.mockendpoints.rest.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cgi.mockendpoints.rest.model.PharmaNetMessageModel;

/**
 * Contains stubbed out endpoints for PharmaNet.
 * 
 * @author dave.p.barrett
 *
 */
@RestController
public class PharmaNetHl7sApi {

	private static final String PHARMANET_RESPONSE_ENCODED = "TVNIfF5+XCZ8UkFJR1QtUFJTTi1ETUdSfEJDMDAwMDIwNDF8UE5QfEJDMDEwMDAwMzB8MjAyMDAyMDYxMjM4NDF8dHJhaW45NnxaUE58MTgxOTkyNHxEfDIuNF5NCk1TQXxBQXwyMDIwMDIwNjEyMzg0MHxISk1CMDAxSVNVQ0NFU1NGVUxMWSBDT01QTEVURUReTQpFUlJ8Xl5eSEpNQjAwMUkmU1VDQ0VTU0ZVTExZIENPTVBMRVRFRF5NClBJRHx8MTIzNDU2Nzg5Xl5eQkNeUEheTU9IfHx8fHwxOTg0MDIyNXxNXk0KWklBfHx8fHx8fHx8fHx8fHx8TEFTVE5BTUVeRklSU1ReU15eXl5MfDkxMiBWSUVXIFNUXl5eXl5eXl5eXl5eXl5eXl5eXlZJQ1RPUklBXkJDXlY4VjNNMl5DQU5eSF5eXl5OfF5QUk5eUEheXl4yNTBeMTIzNDU2OA0=";
	private static final Logger logger = LoggerFactory.getLogger(PharmaNetHl7sApi.class);
	
	@GetMapping("/pnp")
	public ResponseEntity<PharmaNetMessageModel> getPharmanetMessage() {
		
		PharmaNetMessageModel pharmaNetMessageModel = new PharmaNetMessageModel();
		logger.info("Returning new empty HL7 Message: \n" + pharmaNetMessageModel.toString());
		return new ResponseEntity<PharmaNetMessageModel>(pharmaNetMessageModel, HttpStatus.OK);
	}
	
	/**
	 * Mocked out endpoint for imitating a request sent to PharmaNet
	 *  
	 * @param pharmaNetMessageModel
	 * @return 
	 */
	@PostMapping("/pnp")
	public ResponseEntity<PharmaNetMessageModel> createPharmanetMessage(@Valid @RequestBody PharmaNetMessageModel pharmaNetMessageModel) {
		
		logger.info("Received Request Message: \n" + pharmaNetMessageModel.toString());
		//Set the Hl7 content to sample provided, keep the transaction UUID the same.
		pharmaNetMessageModel.setHl7Message(PHARMANET_RESPONSE_ENCODED);
		return new ResponseEntity<PharmaNetMessageModel>(pharmaNetMessageModel, HttpStatus.OK);
	}
}
