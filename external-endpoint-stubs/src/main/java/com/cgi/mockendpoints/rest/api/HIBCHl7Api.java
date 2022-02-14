package com.cgi.mockendpoints.rest.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cgi.mockendpoints.rest.model.HIBCMessageModel;
import com.cgi.mockendpoints.util.JsonUtil;

import net.minidev.json.JSONArray;

/**
 * Contains stubbed out endpoints for HIBC.
 * 
 */
@RestController
public class HIBCHl7Api {

	private static final String HIBC_RESPONSE_ENCODED = "TVNIfF5+XCZ8SFJ8QkMwMDAwMDA5OHxSQUlHRVQtRE9DLVNVTXxCQzAwMDMwMDB8MTk5OTEwMDQxMDMwMzl8bGhhcnJpc3xFNDV8MTk5ODA5MTUwMDAwMTV8RHwyLjNIRFJ8fHxUUkFJTklOR0FkbWluDQpTRlR8MS4wfHx0ZXN0b3JnXl5vcmdpZF5eXk1PSHwxLjB8YmFyZWJvbmVzfHwNClFQRHxFNDVeXkhORVQwMDAzfDF8Xl4wMDAwMDAwMV5eXkNBTkJDXlhYXk1PSHxeXjAwMDAwMDAxXl5eQ0FOQkNeWFheTU9IfF5eMDAwMDA3NTReXl5DQU5CQ15YWF5NT0h8OTAyMDE5ODc0Nl5eXkNBTkJDXkpITl5NT0h8fDE5NDIwMTEyfHx8fHx8MTk5ODA2MDF8fFBWQ15eSE5FVDk5MDl8fA0KUkNQfEl8";
	private static final Logger logger = LoggerFactory.getLogger(HIBCHl7Api.class);

	/**
	 * Mocked out endpoint for imitating a request sent to hibc
	 * 
	 * @param hIBCMessageModel
	 * @return
	 */
	@PostMapping("/hibc")
	public ResponseEntity<HIBCMessageModel> createRTransMessage(@Valid @RequestBody HIBCMessageModel hIBCMessageModel) {

		logger.info("Received Request Message: \n" + hIBCMessageModel.toString());
		// Set the Hl7 response as JSON
		JSONArray contentArray = JsonUtil.createFHIRJsonArray(HIBC_RESPONSE_ENCODED);
		hIBCMessageModel.setContent(contentArray);
		hIBCMessageModel.setResourceType("DocumentReference");
		hIBCMessageModel.setStatus("current");
		logger.info("Returning HL7 Response: \n" + hIBCMessageModel.toString());
		return new ResponseEntity<HIBCMessageModel>(hIBCMessageModel, HttpStatus.OK);
	}

}
