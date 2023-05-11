package ca.bc.gov.hlth.hnsecure.message.v2.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.CX;
import ca.uhn.hl7v2.model.v24.datatype.IS;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.datatype.XON;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 *<p>Represents an HL7 QPD message segment (Query Parameter Definition) for an E45 message. This has customized fields for each required query parameter instead of the 
 * generic {@link Varies} field in  which can not be used for the E45 QPD query parameters.
 * This segment has the following fields:</p>
 * <ul>
     * <li>QPD-1: Message Query Name (CE) <b>required </b>
     * <li>QPD-2: Query Tag (ST) <b>required </b>
     * <li>QPD-3: Submitting Organization <b>required </b>
     * <li>QPD-4: Provider Organization <b>required </b>
     * <li>QPD-5: Payor Organization <b>required </b>
     * <li>QPD-6: Patient Identifier List <b>required </b>
     * <li>QPD-7: Patient Name <b>optional </b>
     * <li>QPD-8: Date/Time of Birth <b>required </b>
     * <li>QPD-9: Accident Date/Time <b>optional </b>
     * <li>QPD-10: Insurance Plan ID <b>optional </b>
     * <li>QPD-11: Group Number <b>optional </b>
     * <li>QPD-12: Patient Member Number <b>optional </b>
     * <li>QPD-13: Plan Type <b>optional </b>
     * <li>QPD-14: Service Effective Date <b>required </b>
     * <li>QPD-15: Service Expiration Date <b>optional </b>
     * <li>QPD-16: Coverage Inquiry Code <b>optional </b>
     * <li>QPD-17: Role Person <b>optional </b>
     * <li>QPD-18: Provider Type <b>optional </b>
 * </ul>
 */
public class QPD extends AbstractSegment {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7385534991464384899L;


	/** 
     * Creates a new QPD segment
     */
    public QPD(Group parent, ModelClassFactory factory) {
       super(parent, factory);
       init();
    }

    private void init() {
       try {																													// Field
                                  this.add(CE.class, true, 1, 250, new Object[]{ getMessage() }, "Message Query Name");			//	1
                                  this.add(ST.class, true, 1, 32, new Object[]{ getMessage() }, "Query Tag");					//	2
                                  this.add(XON.class, true, 1, 60, new Object[]{ getMessage() }, "Submitting Organization");	//	3
                                  this.add(XON.class, true, 1, 60, new Object[]{ getMessage() }, "Provider Organization");		//	4
                                  this.add(XON.class, true, 1, 60, new Object[]{ getMessage() }, "Payor Organization");			//	5
                                  this.add(CX.class, true, 1, 60, new Object[]{ getMessage() }, "Patient Identifier List");		//	6
                                  this.add(XPN.class, false, 1, 60, new Object[]{ getMessage() }, "Patient Name");				//	7	Not supported
                                  this.add(TS.class, true, 1, 60, new Object[]{ getMessage() }, "Date/Time of Birth");			//	8
                                  this.add(TS.class, false, 1, 60, new Object[]{ getMessage() }, "Accident Date/Time");			//	9	Not supported
                                  this.add(CE.class, false, 1, 60, new Object[]{ getMessage() }, "Insurance Plan ID");			//	10	Not supported
                                  this.add(ST.class, false, 1, 60, new Object[]{ getMessage() }, "Group Number");				//	11	Not supported
                                  this.add(CX.class, false, 1, 60, new Object[]{ getMessage() }, "Patient Member Number");		//	12	Not supported
                                  this.add(IS.class, false, 1, 60, new Object[]{ getMessage() }, "Plan Type");					//	13	Not supported
                                  this.add(TS.class, true, 1, 60, new Object[]{ getMessage() }, "Service Effective Date");		//	14
                                  this.add(TS.class, false, 1, 60, new Object[]{ getMessage() }, "Service Expiration Date");	//	15	Not supported
                                  this.add(CE.class, false, 20, 60, new Object[]{ getMessage() }, "Coverage Inquiry Code");		//	16
                                  this.add(XCN.class, false, 1, 60, new Object[]{ getMessage() }, "Role Person");				//	17	Not supported
                                  this.add(CE.class, false, 1, 60, new Object[]{ getMessage() }, "Provider Type");				//	18	Not supported
       } catch(HL7Exception e) {
          log.error("Unexpected error creating QPD - this is probably a bug in the source code generator.", e);
       }
    }



    /**
     * Returns
     * QPD-1: "Message Query Name" - creates it if necessary
     */
    public CE getMessageQueryName() { 
		CE retVal = this.getTypedField(1, 0);
		return retVal;
    }
    
    /**
     * Returns
     * QPD-1: "Message Query Name" - creates it if necessary
     */
    public CE getQpd1_MessageQueryName() { 
		CE retVal = this.getTypedField(1, 0);
		return retVal;
    }



    /**
     * Returns
     * QPD-2: "Query Tag" - creates it if necessary
     */
    public ST getQueryTag() { 
		ST retVal = this.getTypedField(2, 0);
		return retVal;
    }
    
    /**
     * Returns
     * QPD-2: "Query Tag" - creates it if necessary
     */
    public ST getQpd2_QueryTag() { 
		ST retVal = this.getTypedField(2, 0);
		return retVal;
    }


    /**
     * Returns QPD-3 Submitting Organization - creates it if necessary
     */
    public XON getSubmittingOrganization() {
    	return this.getTypedField(3, 0);
    }
    
    /**
     * Returns QPD-3 Submitting Organization - creates it if necessary
     */
    public XON getQpd3_SubmittingOrganization() {
    	return this.getTypedField(3, 0);
    }
    
    /**
     * Returns QPD4- Provider Organization - creates it if necessary
     */
    public XON getProviderOrganization() {
       return this.getTypedField(4, 0);
    }
    
    /**
     * Returns QPD4- Provider Organization - creates it if necessary
     */
    public XON getQpd4_ProviderOrganization() {
       return this.getTypedField(4, 0);
    }


    /**
     * Returns QPD5- Payor Organization - creates it if necessary
     */
    public XON getPayorOrganization() {
       return this.getTypedField(5, 0);
    }
    
    /**
     * Returns QPD5- Payor Organization - creates it if necessary 
     * 
     */
    public XON getQpd5_PayorOrganization() {
       return this.getTypedField(5, 0);
    }

    
    /**
     * Returns QPD6- Patient Identifier List - creates it if necessary 
     * 
     */
    public CX getPatientIdentifierList() {
       return this.getTypedField(6, 0);
    }
    
    /**
     * Returns QPD6- Patient Identifier List - creates it if necessary 
     * 
     */
    public CX getQpd6_PatientIdentifierList() {
       return this.getTypedField(6, 0);
    }
    

    //Patient Name => Not Supported 
    

    /**
     * Returns QPD8- Date/Time of Birth - creates it if necessary 
     */
    public TS getDateTimeOfBirth() {
       return this.getTypedField(8, 0);
    }
    
    /**
     * Returns QPD8- Date/Time of Birth - creates it if necessary 
     * 
     */
    public TS getQpd8_DateTimeOfBirth() {
       return this.getTypedField(8, 0);
    }
    

    /**
     * Returns QPD14- Service Effective Date - creates it if necessary 
     * 
     */
    public TS getServiceEffectiveDate() {
       return this.getTypedField(14, 0);
    }
    
    /**
     * Returns QPD14- Service Effective Date - creates it if necessary 
     */
    public TS getQpd14_ServiceEffectiveDate() {
       return this.getTypedField(14, 0);
    }
    

    
	/**
     * Returns all repetitions of QPD16- Coverage Inquiry Code
     */
    public CE[] getCoverageInquiryCode() {
    	return this.getTypedField(16, new CE[0]);
    }

	/**
     * Returns all repetitions of QPD16- Coverage Inquiry Code
     */
    public CE[] getQpd16_CoverageInquiryCode() {
    	return this.getTypedField(16, new CE[0]);
    }


    /**
     * Returns a count of the current number of repetitions of Coverage Inquiry Code (QPD-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getCoverageInquiryCodeReps() {
    	return this.getReps(16);
    }

    /**
     * Returns a count of the current number of repetitions of Coverage Inquiry Code (QPD-16).
     * This method does not create a repetition, so if no repetitions have currently been defined or accessed,
     * it will return zero.
     */
    public int getQpd16_CoverageInquiryCodeReps() {
    	return this.getReps(16);
    }

    /**
     * Returns a specific repetition of
     * Coverage Inquiry Code (QPD-16) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getCoverageInquiryCode(int rep) { 
    	return this.getTypedField(16, rep);
    }

    /**
     * Returns a specific repetition of
     * Coverage Inquiry Code (QPD-16) - creates it if necessary
     *
     * @param rep The repetition index (0-indexed)
     */
    public CE getQpd16_CoverageInquiryCode(int rep) { 
    	return this.getTypedField(16, rep);
    }

    /**
     * Inserts a repetition of
     * Coverage Inquiry Code (QPD-16) at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertCoverageInquiryCode(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(16, rep);
    }

    /**
     * Inserts a repetition of
     * Coverage Inquiry Code (QPD-16) at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE insertQpd16_CoverageInquiryCode(int rep) throws HL7Exception { 
        return (CE) super.insertRepetition(16, rep);
    }

    /**
     * Removes a repetition of
     * Coverage Inquiry Code (QPD-16) at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeCoverageInquiryCode(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(16, rep);
    }

    /**
     * Removes a repetition of
     * Coverage Inquiry Code (QPD-16) at a specific index
     *
     * @param rep The repetition index (0-indexed)
     * @throws HL7Exception If the rep is invalid (below 0, or too high for the allowable repetitions)
     */
    public CE removeQpd16_CoverageInquiryCode(int rep) throws HL7Exception { 
        return (CE) super.removeRepetition(16, rep);
    }
   
    

    /**
     * Returns QPD17- Role Person - creates it if necessary 
     */
    public XCN getRolePerson() {
       return this.getTypedField(17, 0);
    }
    
    /**
     * Returns QPD17- Role Person - creates it if necessary 
     */
    public XCN getQpd17_RolePerson() {
       return this.getTypedField(17, 0);
    }
    

    
   /** {@inheritDoc} */   
    protected Type createNewTypeWithoutReflection(int field) {
       switch (field) {
          case 0: return new CE(getMessage());
          case 1: return new ST(getMessage());
          case 2: return new XON(getMessage());
          case 3: return new XON(getMessage());
          case 4: return new XON(getMessage());
          case 5: return new CX(getMessage());
          case 6: return new XPN(getMessage());
          case 7: return new TS(getMessage());
          case 8: return new TS(getMessage());
          case 9: return new CE(getMessage());
          case 10: return new ST(getMessage());
          case 11: return new CX(getMessage());
          case 12: return new IS(getMessage());
          case 13: return new TS(getMessage());
          case 14: return new TS(getMessage());
          case 15: return new CE(getMessage());
          case 16: return new XCN(getMessage());
          case 17: return new CE(getMessage());
          default: return null;

       }
   }


}
