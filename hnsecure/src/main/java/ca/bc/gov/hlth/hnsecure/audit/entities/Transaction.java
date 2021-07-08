package ca.bc.gov.hlth.hnsecure.audit.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema="hnsecure", name = "TRANSACTION")
public class Transaction {
	
	/** 
	 * unique identifier for the transaction. this is to be issued by the ESB, not the database. 
	 */
	@Id
	@Column(name = "transaction_id", updatable = false, nullable = false)
	private UUID transactionId;
	
	/**
	 * type of transaction. Pulled from the message header. for example E45, R15
	 */
	@Basic
	@Column(name="type")
	private String type;
	
	/**
	 * name of the server that processed the transaction. (this may not be relevant in openshift)
	 */
	@Basic
	@Column(name="server")
	private String server;
	
	/**
	 * source system that sent the transaction. Use MSH.3 Sending Application
	 */
	@Basic
	@Column(name="source")
	private String source;
	
	/**
	 * Organization that initiated the transaction
	 */
	@Basic
	@Column(name="organization")
	private String organization;
	
	/**
	 * ID of the user that initiated the transaction
	 */
	@Basic
	@Column(name="user_id")
	private String userId;
	
	/**
	 * use MSH.4 Sending Facility
	 */
	@Basic
	@Column(name="facility_id")
	private String facilityId;
	
	/**
	 * time that the transaction was started/created
	 */
	@Basic
	@Column(name="start_time", columnDefinition="timestamptz")
	private Date timestamp;

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(UUID transactionId) {
		this.transactionId = transactionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
