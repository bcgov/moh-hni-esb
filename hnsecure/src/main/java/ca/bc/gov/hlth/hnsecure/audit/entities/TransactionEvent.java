package ca.bc.gov.hlth.hnsecure.audit.entities;

import java.util.Date;

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
 * Audit entity TransactionEvent
 */
@Entity
@Table(schema="hnsecure", name="transaction_event")
public class TransactionEvent {

	@Id
	@Column(name="transaction_event_id", columnDefinition="bigserial")
	private long transactionEventId;

	@Basic
	@Column(name="event_time", columnDefinition="timestamptz")
	private Date eventTime;

	@Basic
	@Column(name="message_id")
	private String messageId;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinColumn(name="transaction_id", columnDefinition="uuid")
	private Transaction transaction;

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

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public long getTransactionEventId() {
		return transactionEventId;
	}

	public void setTransactionEventId(long transactionEventId) {
		this.transactionEventId = transactionEventId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}