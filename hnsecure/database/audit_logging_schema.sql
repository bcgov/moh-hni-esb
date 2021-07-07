
-- Clear existing schema

DROP TABLE IF EXISTS hnsecure.EVENT_MESSAGE;
DROP TABLE IF EXISTS hnsecure.TRANSACTION_EVENT;
DROP TABLE IF EXISTS hnsecure.TRANSACTION_INFO;
DROP TABLE IF EXISTS hnsecure.AFFECTED_PARTY;
DROP TABLE IF EXISTS hnsecure.TRANSACTION;
DROP SCHEMA IF EXISTS hnsecure;

-- Beging Creating schema

CREATE SCHEMA hnsecure;


--TRANSACTION

CREATE TABLE hnsecure.TRANSACTION (
	TRANSACTION_ID	UUID CONSTRAINT PK_TRANSACTION PRIMARY KEY,
	TYPE	VARCHAR(255),
	SERVER	VARCHAR(255),
	SOURCE	VARCHAR(255),
	ORGANIZATION	VARCHAR(255),
	USER_ID	VARCHAR(255),
	FACILITY_ID	VARCHAR(255),
	START_TIME TIMESTAMP WITH TIME ZONE
);

COMMENT ON TABLE hnsecure.TRANSACTION IS 'The transaction table stores a complete list of transactions that area processed by the ESB. This stores top level information';
COMMENT ON COLUMN hnsecure.TRANSACTION.TRANSACTION_ID IS 'unique identifier for the transaction. this is to be issued by the ESB, not the database.';
COMMENT ON COLUMN hnsecure.TRANSACTION.TYPE IS 'type of transaction. Pulled from the message header. for example E45, R15';
COMMENT ON COLUMN hnsecure.TRANSACTION.SERVER IS 'name of the server that processed the transaction. (this may not be relevant in openshift)';
COMMENT ON COLUMN hnsecure.TRANSACTION.SOURCE IS 'source system that sent the transaction. Use MSH.3 Sending Application';
COMMENT ON COLUMN hnsecure.TRANSACTION.ORGANIZATION IS 'Organization that initiated the transaction';
COMMENT ON COLUMN hnsecure.TRANSACTION.USER_ID IS 'ID of the user that initiated the transaction';
COMMENT ON COLUMN hnsecure.TRANSACTION.FACILITY_ID IS 'use MSH.4 Sending Facility';
COMMENT ON COLUMN hnsecure.TRANSACTION.START_TIME IS 'time that the transaction was started/created';

--AFFECTED_PARTY

CREATE TABLE hnsecure.AFFECTED_PARTY(
	AFFECTED_PARTY_ID BIGSERIAL CONSTRAINT PK_AFFECTED_PARTY	primary KEY,
	IDENTIFIER VARCHAR(255),
	IDENTIFIER_SOURCE VARCHAR(255),
	IDENTIFIER_TYPE VARCHAR(255),
	STATUS VARCHAR(255),
	TRANSACTION_ID	UUID,
	CONSTRAINT FK_AFFECTED_PARTY_TRANSACTION
      FOREIGN KEY(TRANSACTION_ID) 
	  REFERENCES hnsecure.TRANSACTION(TRANSACTION_ID)
);

CREATE UNIQUE INDEX IXFX_AFFECTED_PARTY_TRANSACTION ON hnsecure.AFFECTED_PARTY (TRANSACTION_ID);

COMMENT ON TABLE hnsecure.AFFECTED_PARTY IS 'Affected party stores data relevant to the parties that are the subject of the transaction. An example of an affected party would be a patient.';
COMMENT ON COLUMN hnsecure.AFFECTED_PARTY.AFFECTED_PARTY_ID IS 'primary key';
COMMENT ON COLUMN hnsecure.AFFECTED_PARTY.IDENTIFIER IS 'identifier number, such as a PHN or MRN';
COMMENT ON COLUMN hnsecure.AFFECTED_PARTY.IDENTIFIER_SOURCE IS 'name of the source system that issued the identifier';
COMMENT ON COLUMN hnsecure.AFFECTED_PARTY.IDENTIFIER_TYPE IS 'the type of identifier (PHN, MRN, drivers license no, etc)';
COMMENT ON COLUMN hnsecure.AFFECTED_PARTY.STATUS IS 'Status of the identifier (Active, Merged, Deleted)';
COMMENT ON COLUMN hnsecure.AFFECTED_PARTY.TRANSACTION_ID IS 'Foreign key to the transaction the party is the subject OF.';


--TRANSACTION_INFO

CREATE TABLE hnsecure.TRANSACTION_INFO(
	TRANSACTION_ID	UUID,	
	STATUS	VARCHAR(255),
	TYPE	VARCHAR(255),
	CONSTRAINT FK_TRANSACTION_INFO_TRANSACTION
      FOREIGN KEY(TRANSACTION_ID) 
	  REFERENCES hnsecure.TRANSACTION(TRANSACTION_ID)
);

CREATE UNIQUE INDEX IXFX_TRANSACTION_INFO_TRANSACTION ON hnsecure.TRANSACTION_INFO (TRANSACTION_ID);

COMMENT ON TABLE hnsecure.TRANSACTION_INFO IS 'Transaction info is a table used to store status changes of a transaction.';
COMMENT ON COLUMN hnsecure.TRANSACTION_INFO.TRANSACTION_ID IS 'foreign key to the transaction this event belongs TO.';
COMMENT ON COLUMN hnsecure.TRANSACTION_INFO.STATUS IS 'Status of the transaction. For example transaction_complete';
COMMENT ON COLUMN hnsecure.TRANSACTION_INFO.TYPE IS 'transation type';


--TRANSACTION_EVENT

CREATE TABLE hnsecure.TRANSACTION_EVENT(
	TRANSACTION_EVENT_ID	BIGSERIAL CONSTRAINT PK_TRANSACTION_EVENT	PRIMARY KEY,
	EVENT_TIME	TIMESTAMP WITH TIME ZONE,	
	MESSAGE_ID	VARCHAR(255),
	TYPE	VARCHAR(255),
	TRANSACTION_ID	UUID,
	CONSTRAINT FK_TRANSACTION_EVENT_TRANSACTION
      FOREIGN KEY(TRANSACTION_ID) 
	  REFERENCES hnsecure.TRANSACTION(TRANSACTION_ID)
);

CREATE UNIQUE INDEX IXFX_TRANSACTION_EVENT_TRANSACTION ON hnsecure.TRANSACTION_EVENT (TRANSACTION_ID);

COMMENT ON TABLE hnsecure.TRANSACTION_EVENT IS 'The transaction event table is used to store information about events that occured during the course of a transaction.';
COMMENT ON COLUMN hnsecure.TRANSACTION_EVENT.TRANSACTION_EVENT_ID IS 'primary key';
COMMENT ON COLUMN hnsecure.TRANSACTION_EVENT.EVENT_TIME IS 'time that the event occured';
COMMENT ON COLUMN hnsecure.TRANSACTION_EVENT.MESSAGE_ID IS 'message id created by the esb. could be an incoming, or the id generated for a response.';
COMMENT ON COLUMN hnsecure.TRANSACTION_EVENT.TYPE IS 'transaction event type, for example error.';
COMMENT ON COLUMN hnsecure.TRANSACTION_EVENT.TRANSACTION_ID IS 'foreign key to the transaction this event belongs TO.';


--EVENT_MESSAGE

CREATE TABLE hnsecure.EVENT_MESSAGE(
	EVENT_MESSAGE_ID BIGSERIAL CONSTRAINT PK_EVENT_MESSAGE	PRIMARY KEY,
	ERROR_CODE	VARCHAR(255),
	ERROR_LEVEL	VARCHAR(255),
	MESSAGE_TEXT	TEXT,
	TRANSACTION_EVENT_ID	BIGINT,
	CONSTRAINT FK_EVENT_MESSAGE_TRANSACTION_EVENT
      FOREIGN KEY(TRANSACTION_EVENT_ID) 
	  REFERENCES hnsecure.TRANSACTION_EVENT(TRANSACTION_EVENT_ID)
);

CREATE UNIQUE INDEX IXFX_EVENT_MESSAGE_TRANSACTION_EVENT ON hnsecure.EVENT_MESSAGE (TRANSACTION_EVENT_ID);

COMMENT ON TABLE hnsecure.EVENT_MESSAGE IS 'The event message table stores data such as errors and warnings relevant to a transaction event that occurred. An event can have multiple messages.';
COMMENT ON COLUMN hnsecure.EVENT_MESSAGE.EVENT_MESSAGE_ID IS 'primary key';
COMMENT ON COLUMN hnsecure.EVENT_MESSAGE.ERROR_CODE IS 'Response or error code returned as a part of the event.';
COMMENT ON COLUMN hnsecure.EVENT_MESSAGE.ERROR_LEVEL IS 'Error level, similar to logging levels (INFO, WARNING, ERROR, ETC)';
COMMENT ON COLUMN hnsecure.EVENT_MESSAGE.MESSAGE_TEXT IS 'Complete response/error message text.';
COMMENT ON COLUMN hnsecure.EVENT_MESSAGE.TRANSACTION_EVENT_ID IS 'foreign key to the original event';
