package ca.bc.gov.hlth.hnsecure.message;

import static ca.bc.gov.hlth.hnsecure.message.V2MessageSegmentUtil.ENCODING_CHARACTERS;
import static ca.bc.gov.hlth.hnsecure.message.V2MessageSegmentUtil.FIELD_SEPARATOR;

import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIH;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.util.Terser;

/**
 * Structure to represent a HL7 R32 Response Message.
 */
@SuppressWarnings("serial")
public class R32ContractPeriod extends AbstractMessage {

	public R32ContractPeriod() {
		this(new DefaultModelClassFactory());
	}

	/**
	 * Constructor.
	 * 
	 * @param theFactory ModelClassFactory is used to call parent constructor.
	 */
	public R32ContractPeriod(ModelClassFactory theFactory) {
		super(theFactory);
		init();
	}

	public void init() {
		try {
			this.add(MSH.class, true, false);
			this.add(PID.class, true, false);
			this.add(NK1.class, true, false);
			this.add(IN1.class, true, false);
			this.add(ZIH.class, true, false);

			Terser.set(this.getMSH(), 1, 0, 1, 1, FIELD_SEPARATOR);
			Terser.set(this.getMSH(), 2, 0, 1, 1, ENCODING_CHARACTERS);

		} catch (HL7Exception e) {
			log.error("Unexpected error creating R15 - this is probably a bug in the source code generator.", e);
		}
	}

	public MSH getMSH() {
		return getTyped("MSH", MSH.class);
	}

	public IN1 getIN1() {
		return getTyped("IN1", IN1.class);
	}

	public PID getPID() {
		return getTyped("PID", PID.class);
	}

	public ZIH getZIH() {
		return getTyped("ZIH", ZIH.class);
	}

	public NK1 getNK1() {
		return getTyped("NK1", NK1.class);
	}

}
