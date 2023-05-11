package ca.bc.gov.hlth.hnsecure.message.v2.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v24.datatype.DT;
import ca.uhn.hl7v2.model.v24.datatype.ID;
import ca.uhn.hl7v2.model.v24.datatype.IS;
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 *<p>Represents an HL7 ZIH message segment. </p>
*/
@SuppressWarnings("serial")
public class ZIH extends AbstractSegment {

    /** 
     * Creates a new ZIH segment
     */
    public ZIH(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init();
    }

	private void init() {
		try {

			this.add(DT.class, true, 1, 1, new Object[] { getMessage() }, "IMS Start Date");
			this.add(DT.class, true, 1, 8, new Object[] { getMessage() }, "Latest GIS Payment Date");
			this.add(DT.class, true, 1, 8, new Object[] { getMessage() }, "Final GIS Payment Date");
			this.add(ST.class, true, 1, 8, new Object[] { getMessage() }, "GIS Recipient Entitlement/Renewal Date");
			this.add(IS.class, true, 1, 1, new Object[] { getMessage(), Integer.valueOf("0002") }, "GIS Marital Status");	//HNET:9966
			this.add(IS.class, true, 1, 1, new Object[] { getMessage() }, "GIS Account Status Code");						//HNET:9967
			this.add(ST.class, true, 5, 30, new Object[] { getMessage() }, "GIS Address");
			this.add(ID.class, true, 1, 1, new Object[] { getMessage(), Integer.valueOf("0136") }, "Verified GIS Flag");
			this.add(IS.class, true, 1, 1, new Object[] { getMessage() }, "Accommodation Rate Code");						//HNET:9986
			this.add(DT.class, true, 1, 8, new Object[] { getMessage() }, "Accommodation RateEffective Date");
			this.add(IS.class, true, 1, 1, new Object[] { getMessage() }, "CCD Set Code");									//HNET:9985
			this.add(NM.class, true, 1, 6, new Object[] { getMessage() }, "Facility User Fee");
			this.add(IS.class, true, 1, 2, new Object[] { getMessage() }, "Agreement Class");								//HNET:9947
			this.add(ST.class, true, 1, 4, new Object[] { getMessage() }, "Agreement Type");								//HNET:9946
			this.add(ST.class, true, 1, 4, new Object[] { getMessage() }, "End Reason");									//HNET:9945
			this.add(DT.class, true, 1, 8, new Object[] { getMessage() }, "End Date");
			this.add(ST.class, true, 1, 255, new Object[] { getMessage() }, "Client Instruction");
			this.add(SI.class, true, 1, 4, new Object[] { getMessage() }, "Set ID – ZIH");
			this.add(IS.class, true, 1, 1, new Object[] { getMessage() }, "Payer Cancel Reason");							//HNET:9942		Reasons supplied by employer for ending premium contributions for an employee.
			this.add(IS.class, true, 1, 1, new Object[] { getMessage() }, "Display Cancel Reason");							//HNET:9934
			
		} catch (HL7Exception e) {
			log.error("Unexpected error creating ZIH - this is probably a bug in the source code generator.", e);
		}
	}

    /**
     * Returns
     * ZIH-19: "Payer Cancel Reason" - creates it if necessary
     */
    public IS getPayerCancelReason() { 
    	IS retVal = this.getTypedField(19, 0);
        return retVal;
    }

    /**
     * Returns
     * ZIH-19: "Payer Cancel Reason" - creates it if necessary
     */
    public IS getZih19_PayerCancelReason() {
        IS retVal = this.getTypedField(19, 0);
        return retVal;
    }
    
}
