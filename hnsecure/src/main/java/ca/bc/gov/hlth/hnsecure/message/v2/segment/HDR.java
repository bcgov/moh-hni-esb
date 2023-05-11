package ca.bc.gov.hlth.hnsecure.message.v2.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 *<p>Represents an HL7 HDR message segment. </p>
 * This segment has the following fields:</p>
 * <ul>
     * <li>HDR-3: Business User Group (ST) <b> required </b>
 * </ul>
*/
@SuppressWarnings("serial")
public class HDR extends AbstractSegment {

    /** 
     * Creates a new HDR segment
     */
    public HDR(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init();
    }

	private void init() {
		try {
            this.add(ST.class, true, 1, 30, new Object[]{ getMessage() }, "Dummy field"); 
            this.add(ST.class, true, 1, 30, new Object[]{ getMessage() }, "Dummy field"); 
            this.add(ST.class, true, 1, 30, new Object[]{ getMessage() }, "Business User Group"); 			
		} catch (HL7Exception e) {
			log.error("Unexpected error creating HDR - this is probably a bug in the source code generator.", e);
		}
	}

    /**
     * HDR3 - Create if necessary
     * @return retVal
     */
    public ST getHDR3_BusinessUserGroup() { 
    	return this.getTypedField(3, 0);
    }
    
    public ST getBusinessUserGroup() { 
    	return this.getTypedField(3, 0);
    }
    
}
