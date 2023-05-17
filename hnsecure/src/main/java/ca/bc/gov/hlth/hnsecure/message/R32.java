package ca.bc.gov.hlth.hnsecure.message;

import static ca.bc.gov.hlth.hnsecure.message.V2MessageUtil.ENCODING_CHARACTERS;
import static ca.bc.gov.hlth.hnsecure.message.V2MessageUtil.FIELD_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIA;
import ca.bc.gov.hlth.hnsecure.message.v2.segment.ZIH;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v24.segment.ERR;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSA;
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
public class R32 extends AbstractMessage {
	public List<R32Beneficiary>  list1= new ArrayList();
	public List<R32ContractPeriods>  list2= new ArrayList();
	public R32() {
        this(new DefaultModelClassFactory());
	}

    /**
     * Constructor.
     * 
     * @param theFactory ModelClassFactory is used to call parent constructor.
     */
    public R32(ModelClassFactory theFactory) {
        super(theFactory);
        init();
    }
    
    public void init() {
        try {
            this.add(MSH.class, true, false);         
            this.add(MSA.class, true, false);
            this.add(ERR.class, true, false);
       
            this.add(IN1.class, true, true);
            this.add(NK1.class, true, true);
            this.add(ZIA.class, true, true);
            this.add(ZIH.class, true, true);                  
            
            Terser.set(this.getMSH(), 1, 0, 1, 1, FIELD_SEPARATOR);
            Terser.set(this.getMSH(), 2, 0, 1, 1, ENCODING_CHARACTERS); 
            
                   

        } catch(HL7Exception e) {
            log.error("Unexpected error creating R15 - this is probably a bug in the source code generator.", e);
       }
    }
    

    
    /** 
     *
     * @return Returns "2.4"
     */
    @Override
    public String getVersion() {
       return V2MessageUtil.DEFAULT_VERSION_ID;
    }
    
	public MSH getMSH() {
		return getTyped("MSH", MSH.class);
	}
	
	public MSA getMSA() {
		return getTyped("MSA", MSA.class);
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
	
	public ZIA getZIA() {
		return getTyped("ZIA", ZIA.class);
	}
	
	public NK1 getNK1() {
		return getTyped("NK1", NK1.class);
	}
	
	public ERR getERR() {
		return getTyped("ERR", ERR.class);
	}

	public List<R32Beneficiary> getList1() {
		return list1;
	}

	public void setList1(List<R32Beneficiary> list1) {
		this.list1 = list1;
	}

	public List<R32ContractPeriods> getList2() {
		return list2;
	}

	public void setList2(List<R32ContractPeriods> list2) {
		this.list2 = list2;
	}

}
