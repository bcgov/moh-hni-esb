package ca.bc.gov.hlth.hnsecure.audit.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Audit entity EventMessage
 */
@Entity
@Table(name="event_message")
public class EventMessage {

	@Id
	@Column(name="event_message_id", columnDefinition="bigserial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventMessageId;

	@Basic
	@Column(name="error_code")
	private String errorCode;

	@Basic
	@Column(name="error_level")
	private String errorLevel;

	@Basic
	@Column(name="message_text", columnDefinition="text", length=2147483647)
	private String messageText;

	@Column(name="transaction_event_id", columnDefinition="int8")
	private Long transactionEventId;

	public EventMessage() {
	}

	public EventMessage(long eventMessageId) {
		this.eventMessageId = eventMessageId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorLevel() {
		return errorLevel;
	}

	public void setErrorLevel(String errorLevel) {
		this.errorLevel = errorLevel;
	}

	public Long getEventMessageId() {
		return eventMessageId;
	}

	public void setEventMessageId(Long eventMessageId) {
		this.eventMessageId = eventMessageId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public Long getTransactionEventId() {
		return transactionEventId;
	}

	public void setTransactionEventId(Long transactionEventId) {
		this.transactionEventId = transactionEventId;
	}

	
}