package ca.bc.gov.hlth.hnsecure.audit.entities;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Audit entity EventMessage
 */
@Entity
@Table(schema="hnsecure", name="event_message")
public class EventMessage {

	@Id
	@Column(name="event_message_id", columnDefinition="bigserial")
	private long eventMessageId;

	@Basic
	@Column(name="error_code")
	private String errorCode;

	@Basic
	@Column(name="error_level")
	private String errorLevel;

	@Basic
	@Column(name="message_text", columnDefinition="text", length=2147483647)
	private String messageText;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinColumn(name="transaction_event_id", columnDefinition="int8")
	private TransactionEvent transactionEvent;


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

	public long getEventMessageId() {
		return eventMessageId;
	}

	public void setEventMessageId(long eventMessageId) {
		this.eventMessageId = eventMessageId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public TransactionEvent getTransactionEvent() {
		return transactionEvent;
	}

	public void setTransactionEvent(TransactionEvent transactionEvent) {
		this.transactionEvent = transactionEvent;
	}
}