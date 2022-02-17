package com.cgi.mockendpoints.rest.model;

import net.minidev.json.JSONArray;

/**
 * JMB Message model
 * 
 */
public class JMBMessageModel {

	private String resourceType;
	private String status;
	private String contentType;	
	private JSONArray content;

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public JSONArray getContent() {
		return content;
	}

	public void setContent(JSONArray content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "JMBMessageModel [hl7Message=" + content + "]";
	}
		
}
