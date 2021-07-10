package ca.bc.gov.hlth.hnsecure.audit.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * Audit entity TransactionEvent
 */
@Entity
@Table(schema = "hnsecure", name = "transaction_event")
public class TransactionEvent {

	@Id
	@Column(name = "transaction_event_id", columnDefinition = "bigserial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionEventId;

	@Basic
	@Column(name = "event_time", columnDefinition = "timestamptz")
	private Date eventTime;

	@Basic
	@Column(name = "message_id")
	private String messageId;

	@Column(name = "transaction_id", columnDefinition = "uuid")
	private UUID transactionId;

	@Basic
	private String type;

	public TransactionEvent() {
	}

	public TransactionEvent(long transactionEventId) {
		this.transactionEventId = transactionEventId;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(UUID transactionId) {
		this.transactionId = transactionId;
	}

	public Long getTransactionEventId() {
		return transactionEventId;
	}

	public void setTransactionEventId(Long transactionEventId) {
		this.transactionEventId = transactionEventId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@PrePersist
	public void prePersist() {
		if (eventTime == null) {
			eventTime = new Date();
		}
	}
}