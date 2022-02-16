package com.cgi.mockendpoints.rest.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cgi.mockendpoints.rest.model.JMBMessageModel;
import com.cgi.mockendpoints.util.JsonUtil;

import net.minidev.json.JSONArray;

/**
 * Contains stubbed out endpoints for JMB.
 * 
 */
@RestController
public class JMBHl7Api {

	private static final String JMB_RESPONSE_ENCODED = "TVNIfF5+XCZ8UkFJR1QtQ05ULVBSRFN8QkMwMDAwMTAxM3xITldlYnxCQzAxMDAwMDMwfDIwMjEwODI3MDkxNjM5fGFudS0yNi1ibGFua01zZ0NudHJsfFIzMnwyMDIxMDgyNzA5MTYzOXxEfDIuNA1NU0F8QUV8fEhKTUIwMDFFUmVxdWlyZWQgZmllbGQgbWlzc2luZzpNU0gvTWVzc2FnZUNvbnRyb2xJRA1FUlJ8Xl5eSEpNQjAwMUUmUmVxdWlyZWQgZmllbGQgbWlzc2luZzpNU0gvTWVzc2FnZUNvbnRyb2xJRA0=";
	private static final Logger logger = LoggerFactory.getLogger(JMBHl7Api.class);

	/**
	 * Mocked out endpoint for imitating a request sent to jmb
	 * @param jMBMessageModel
	 * @return
	 */
	@PostMapping("/jmb")
	public ResponseEntity<JMBMessageModel> createJMBMessage(@Valid @RequestBody JMBMessageModel jMBMessageModel) {

		logger.info("Received Request Message: {}" , jMBMessageModel.toString());
		// Set the Hl7 response as JSON
		JSONArray contentArray = JsonUtil.createFHIRJsonArray(JMB_RESPONSE_ENCODED);
		jMBMessageModel.setContent(contentArray);
		jMBMessageModel.setResourceType("DocumentReference");
		jMBMessageModel.setStatus("current");
		logger.info("Returning HL7 Response: {}" , jMBMessageModel.toString());
		return new ResponseEntity<JMBMessageModel>(jMBMessageModel, HttpStatus.OK);
	}
}
