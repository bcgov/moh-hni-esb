package ca.bc.gov.hlth.hnsecure.message.v2.datatype;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v24.datatype.IS;
import ca.uhn.hl7v2.model.v24.datatype.ST;

/**
 * <p>Represents an HL7 ZRG(Argument list) data type. Use for passing name/value pairs. 
 * This type consists of the following components:</p> * 
 * </p>
 * <ul>
 * <li><argument (IS)>
 * <li><argument value (ST)>
 * </ul>
 */
@SuppressWarnings("serial")
public class ZRG extends AbstractComposite {
    
    private Type[] data;

    /** 
     * Creates a new ZRG type
     */
    public ZRG(Message message) {
        super(message);
        init();
    }

    private void init() {
        data = new Type[2];    
        data[0] = new IS(getMessage());
        data[1] = new ST(getMessage());
    }

    /**
     * Returns an array containing the data elements.
     */
    public Type[] getComponents() { 
        return this.data; 
    }

    /**
     * Returns an individual data component.
     *
     * @param number The component number (0-indexed)
     * @throws DataTypeException if the given element number is out of range.
     */
    public Type getComponent(int number) throws DataTypeException { 

        try { 
            return this.data[number]; 
        } catch (ArrayIndexOutOfBoundsException e) { 
            throw new DataTypeException("Element " + number + " doesn't exist (Type " + getClass().getName() + " has only " + this.data.length + " components)"); 
        } 
    } 


    /**
     * Returns argument name (ST) (component 1).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public IS getArgumentName() {
       return getTyped(0, IS.class);
    }

    
    /**
     * Returns argument name (ST) (component 1).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public IS getZrg1_ArgumentName() {
       return getTyped(0, IS.class);
    }


    /**
     * Returns argument value (component 2).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getArgumentValue() {
       return getTyped(1, ST.class);
    }

    
    /**
     * Returns argument value (component 2).  This is a convenience method that saves you from 
     * casting and handling an exception.
     */
    public ST getZrg2_ArgumentValue() {
       return getTyped(1, ST.class);
    }

}
