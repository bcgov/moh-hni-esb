package ca.bc.gov.hlth.hnsecure.audit.entities;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Audit entity AffectedParty
 */
@Entity
@Table(name="affected_party")
public class AffectedParty {
	
	@Id
	@Column(name="affected_party_id", columnDefinition="bigserial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long affectedPartyId;

	/**
	 * 	identifier number, such as a PHN or MRN
	 */
	@Basic
	private String identifier;

	/**
	 * the type of identifier (PHN, MRN, drivers license no, etc)
	 */
	@Basic
	@Column(name="identifier_type")
	private String identifierType;

	/**
	 * Specifies the direction of the identifier in the transaction. (Inbound, Outbound)
	 */
	@Basic
	@Column(name = "direction", nullable = false)
	private String direction;

	@Column(name = "transaction_id", columnDefinition = "uuid")
	private UUID transactionId;

	public AffectedParty() {
	}

	public AffectedParty(long affectedPartyId) {
		this.affectedPartyId = affectedPartyId;
	}

	public long getAffectedPartyId() {
		return affectedPartyId;
	}

	public void setAffectedPartyId(long affectedPartyId) {
		this.affectedPartyId = affectedPartyId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(UUID transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "AffectedParty [affectedPartyId=" + affectedPartyId + ", identifier=" + identifier
				 + ", identifierType=" + identifierType 
				+ ", direction=" + direction + " transactionId=" + transactionId + "]";
	}

}