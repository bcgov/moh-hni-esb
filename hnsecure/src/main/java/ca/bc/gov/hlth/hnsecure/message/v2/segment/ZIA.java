package ca.bc.gov.hlth.hnsecure.message.v2.segment;

import ca.bc.gov.hlth.hnsecure.message.v2.datatype.ZAD;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v24.datatype.DT;
import ca.uhn.hl7v2.model.v24.datatype.ID;
import ca.uhn.hl7v2.model.v24.datatype.IS;
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.XAD;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.datatype.XTN;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 *<p>Represents an HL7 ZIA message segment. </p>
*/
@SuppressWarnings("serial")
public class ZIA extends AbstractSegment {
    
    /** 
     * Creates a new ZIA segment
     * @param parent
     * @param factory
     */
    public ZIA(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init();
    }
    
    /**
     * Initialize ZIA
     * @param factory ModelClassFactory
     */
    private void init() {
        try {   
            this.add(ST.class, true, 1, 1, new Object[]{ getMessage() }, "Client Research Code"); 
            this.add(DT.class, true, 1, 8, new Object[]{ getMessage() }, "BC Residency Date"); 
            this.add(NM.class, true, 1, 2, new Object[]{ getMessage() }, "Family Unit Size"); 
            this.add(TS.class, true, 1, 26, new Object[]{ getMessage() }, "Last Change Timestamp"); 
            this.add(ST.class, true, 1, 20, new Object[]{ getMessage() }, "Last Change Id"); 
            this.add(DT.class, true, 1, 8, new Object[]{ getMessage() }, "Latest Assessment Effective Date"); 
            this.add(NM.class, true, 1, 1, new Object[]{ getMessage() }, "Adult Day Care Count"); 
            this.add(NM.class, true, 1, 1, new Object[]{ getMessage() }, "Homemaker Care Count "); 
            this.add(NM.class, true, 1, 1, new Object[]{ getMessage() }, "Group Home Count"); 
            this.add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "Home Nursing Care Indicator"); 
            this.add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "Physiotherapy Indicator"); 
            this.add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "Occupational Therapy Indicator"); 
            this.add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "PHN Verified Flag"); 
            this.add(IS.class, true, 1, 1, new Object[]{ getMessage() }, "BC Residency Flag"); 
            this.add(XPN.class, true, 1, 162, new Object[]{ getMessage() }, "Extended Person Name"); 
            this.add(ZAD.class, true, 2, 532, new Object[]{ getMessage() }, "Extended Address"); 
            this.add(XTN.class, true, 1, 233, new Object[]{ getMessage() }, "Extended Telephone Number"); 
            this.add(ST.class, true, 1, 50, new Object[]{ getMessage() }, "Patient Display Address"); 
            this.add(XAD.class, true, 1, 137, new Object[]{ getMessage() }, "Birth Location"); 
            this.add(XAD.class, true, 1, 137, new Object[]{ getMessage() }, "Death Location"); 
            this.add(ST.class, true, 1, 4, new Object[]{ getMessage() }, "Death Event Source"); 
            this.add(SI.class, true, 1, 4, new Object[]{ getMessage() }, "Set ID – ZIA"); 
            this.add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "Address Validation Override Indicator"); 
            this.add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "Immigration or Visa Code"); 
            this.add(ID.class, true, 1, 2, new Object[]{ getMessage() }, "Prior Residence Code"); 

       } catch(HL7Exception e) {
            log.error("Unexpected error creating ZIA - this is probably a bug in the source code generator.", e);
       }
    }
    
    /**
     * Returns
     * ZIA-2: "BC Residency Date" - creates it if necessary
     */
    public DT getBCResidencyDate() {
        DT retVal = this.getTypedField(2, 0);
        return retVal;
    }
    
    /**
     * Returns
     * ZIA-2: "BC Residency Date" - creates it if necessary
     */
    public DT getZia2_BCResidencyDate() {
        DT retVal = this.getTypedField(2, 0);
        return retVal;
    }    
    
    
    /**
     * Returns
     * ZIA-15: "Extended Person Name" - creates it if necessary
     */
    public XPN getExtendedPersonName() {
        XPN retVal = this.getTypedField(15, 0);
        return retVal;
    }
    
    /**
     * Returns
     * ZIA-15: "Extended Person Name" - creates it if necessary
     */
    public XPN getZia15_ExtendedPersonName() {
        XPN retVal = this.getTypedField(15, 0);
        return retVal;
    }
    
    
	/**
     * Returns all repetitions of Extended Address (ZIA-16).
     */
    public ZAD[] getExtendedAddress() {
    	ZAD[] retVal = this.getTypedField(16, new ZAD[0]);
    	return retVal;
    }

    /**
     * Returns all repetitions of Extended Address (ZIA-16).
     */
    public ZAD[] getZia16_ExtendedAddress() {
    	ZAD[] retVal = this.getTypedField(16, new ZAD[0]);
    	return retVal;
    }

    /**
     * Returns a count of the current number of repetitions of Extended Address (ZIA-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getExtendedAddressReps() {
    	return this.getReps(16);
    }

    /**
     * Returns a count of the current number of repetitions of Extended Address (ZIA-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getZia16_ExtendedAddressReps() {
    	return this.getReps(16);
    }

    /**
     * Returns a specific repetition of
     * ZIA-16: "Extended Address" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public ZAD getExtendedAddress(int rep) { 
		ZAD retVal = this.getTypedField(16, rep);
		return retVal;
    }

    /**
     * Returns a specific repetition of
     * ZIA-16: "Extended Address" - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public ZAD getZia16_ExtendedAddress(int rep) { 
		ZAD retVal = this.getTypedField(16, rep);
		return retVal;
    }

    /**
     * Inserts a repetition of
     * ZIA-16: "Extended Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ZAD insertExtendedAddress(int rep) throws HL7Exception { 
        return (ZAD) super.insertRepetition(16, rep);
    }

    /**
     * Inserts a repetition of
     * ZIA-16: "Extended Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ZAD insertZia16_ExtendedAddress(int rep) throws HL7Exception { 
        return (ZAD) super.insertRepetition(16, rep);
    }

    /**
     * Removes a repetition of
     * ZIA-16: "Extended Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ZAD removeExtendedAddress(int rep) throws HL7Exception { 
        return (ZAD) super.removeRepetition(16, rep);
    }

    /**
     * Removes a repetition of
     * ZIA-16: "Extended Address" at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public ZAD removeZia16_ExtendedAddress(int rep) throws HL7Exception { 
        return (ZAD) super.removeRepetition(16, rep);
    }
   
    
    /**
     * Returns
     * ZIA-17: "Extended Telephone Number" - creates it if necessary
     */
    public XTN getExtendedTelephoneNumber() {
        XTN retVal = this.getTypedField(17, 0);
        return retVal;
    }
    
    /**
     * Returns
     * ZIA-17: "Extended Telephone Number" - creates it if necessary
     */
    public XTN getZia17_ExtendedTelephoneNumber() {
        XTN retVal = this.getTypedField(17, 0);
        return retVal;
    }
    

    /**
     * Returns
     * ZIA-18: "Patient Display Address" - creates it if necessary
     */
    public ST getPatientDisplayAddress() {
        ST retVal = this.getTypedField(18, 0);
        return retVal;
    }
    
    /**
     * Returns
     * ZIA-18: "Patient Display Address" - creates it if necessary
     */
    public ST getZia18_PatientDisplayAddress() {
        ST retVal = this.getTypedField(18, 0);
        return retVal;
    }
    
    
    /**
     * Returns
     * ZIA-22: "Set ID – ZIA" - creates it if necessary
     */
    public SI getSetIdZIA() {
        SI retVal = this.getTypedField(22, 0);
        return retVal;
    }
    
    /**
     * Returns
     * ZIA-22: "Set ID – ZIA" - creates it if necessary
     */
    public SI getZia22_SetIdZIA() {
        SI retVal = this.getTypedField(22, 0);
        return retVal;
    }
    

    /**
     * Returns
     * ZIA-24: "Immigration or Visa Code" - creates it if necessary
     */
    public ID getImmigrationOrVisaCode() {
    	ID retVal = this.getTypedField(24, 0);
        return retVal;
    }    
    
    /**
     * Returns
     * ZIA-24: "Immigration or Visa Code" - creates it if necessary
     */
    public ID getZia24_ImmigrationOrVisaCode() {
    	ID retVal = this.getTypedField(24, 0);
        return retVal;
    }    
    
    
    /**
     * Returns
     * ZIA-25: "Prior Residence Code" - creates it if necessary
     */
    public ID getPriorResidenceCode() {
        ID retVal = this.getTypedField(25, 0);
        return retVal;
    }    
    
    /**
     * Returns
     * ZIA-25: "Prior Residence Code" - creates it if necessary
     */
    public ID getZia25_PriorResidenceCode() {
        ID retVal = this.getTypedField(25, 0);
        return retVal;
    }    
    
}
