package ca.bc.gov.hlth.hnsecure.message.v2.datatype;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v24.datatype.ST;

/**
 * <p>Represents an HL7 ZAD(Extended Address) data type. 
 * This type consists of the following components:</p>
 * <ul>
 * <li>Address Line1 (ST)
 * <li>Address Line2 (ST)
 * <li>Address Line3 (ST)
 * <li>Address Line4 (ST)
 * <li>Address Line5 (ST)
 * <li>Address Line6 (ST)
 * <li>Street Number (ST)
 * <li>Street Number Suffix (ST)
 * <li>Street Name (ST)
 * <li>Street Type (ST) HNET:9936 Coded as per Postal Code Address Data - Technical Specifications, June 1997
 * <li>Street Direction (ST) HNET:9937 Coded as per Postal Code Address Data - Technical Specifications, June 1997
 * <li>Unit Identifier (ST)
 * <li>Unit Designator (ST) HNET:9935
 * <li>Delivery Installation Area (ST)
 * <li>Delivery Installation Type (ST) HNET:9939
 * <li>Delivery Installation Qualifier (ST)
 * <li>Mode of Delivery Designator (ST)
 * <li>Mode of Delivery ID (ST)
 * <li>Physical Description (ST)
 * <li>City (ST)
 * <li>State or Province (ST)
 * <li>Zip or Postal Code (ST) Canadian postal code, US Zip Code
 * <li>Country (ID) HNET:9950
 * <li>Address Type (ID) HNET:0190
 * <li>Other Geographic Region (ST) e.g. electoral district
 * <li>County/Parish (IS) HL7:0289
 * <li>Census Tract (IS) HL7:0288
 * <li>Valid CPC Address Indicator (ID) HNET:9941 Is this a Canadian Postal Code (CPC) valid address?
 * <li>CPC Validation Date(DT)
 * <li>Valid for Residence Indicator (ID) HNET:9941
 * <li>Valid for Residence Date(DT)
 * <li>Valid for Residence Category (ID) HNET:9943
 * <li>Valid for Mailing Indicator (ID) HNET:9941
 * <li>Valid Physical Address Indicator (ID) HNET:9941
 * <li>Address Validation Best Guess Indicator (ID) HL7:0136
 * <li>Effective Start Date(DT) Start date of address, may be blank 
 * <li>Effective End Date(DT)
 * <ul>
 */
@SuppressWarnings("serial")
public class ZAD extends AbstractComposite {
    
    private Type[] data;
    
    public ZAD(Message message) {
        super(message);
        init();
    }
    
    private void init() {
        data = new Type[35];
        for (int i = 0; i < data.length; i++) {
            data[i] = new ST(getMessage());
        }
    }
    /**
     * Returns an array containing the data elements.
     * @return 
     */
    public Type[] getComponents() { 
        return this.data; 
    }
    
    /**
     * Returns an individual data component.
     *
     * @param number The component number (0-indexed)
     * @return 
     * @throws DataTypeException if the given element number is out of range.
     */
    @Override
    public Type getComponent(int number) throws DataTypeException { 

        try { 
            return this.data[number]; 
        } catch (ArrayIndexOutOfBoundsException e) { 
            throw new DataTypeException("Element " + number + " doesn't exist (Type " + getClass().getName() + " has only " + this.data.length + " components)"); 
        } 
    } 
    
    public ST getZAD1_AddressLine1() {
        return getTyped(0, ST.class);
    }
    public ST getZAD2_AddressLine2() {
        return getTyped(1, ST.class);
    }
    public ST getZAD3_AddressLine3() {
        return getTyped(2, ST.class);
    }
    public ST getZAD4_AddressLine4() {
        return getTyped(3, ST.class);
    }
    public ST getZAD5_AddressLine5() {
        return getTyped(4, ST.class);
    }
    public ST getZAD6_AddressLine6() {
        return getTyped(5, ST.class);
    }
    public ST getZAD7_StreetNumber() {
        return getTyped(6, ST.class);
    }
    public ST getZAD8_StreetNumberSuffix() {
        return getTyped(7, ST.class);
    }
    public ST getZAD9_StreetName() {
        return getTyped(8, ST.class);
    }
    public ST getZAD10_StreetType() {
        return getTyped(9, ST.class);
    }
    public ST getZAD11_StreetDirection() {
        return getTyped(10, ST.class);
    }
    public ST getZAD12_UnitIdentifier() {
        return getTyped(11, ST.class);
    }
    public ST getZAD13_UnitDesignator() {
        return getTyped(12, ST.class);
    }
    public ST getZAD14_DeliveryInstallationArea() {
        return getTyped(13, ST.class);
    }
    public ST getZAD15_DeliveryInstallationType() {
        return getTyped(14, ST.class);
    }
    public ST getZAD16_DeliveryInstallationQualifier() {
        return getTyped(15, ST.class);
    }
    public ST getZAD17_ModeOfDeliveryDesignator() {
        return getTyped(16, ST.class);
    }
    public ST getZAD18_ModeOfDeliveryId() {
        return getTyped(17, ST.class);
    }
    public ST getZAD19_PhysicalDescription() {
        return getTyped(18, ST.class);
    }
    public ST getZAD20_City() {
        return getTyped(19, ST.class);
    }
    public ST getZAD21_Province() {
        return getTyped(20, ST.class);
    }
    public ST getZAD22_PostalCode() {
        return getTyped(21, ST.class);
    }
    public ST getZAD23_Country() {
        return getTyped(22, ST.class);
    }
    public ST getZAD24_AddressType() {
        return getTyped(23, ST.class);
    }
    public ST getZAD25_OtherGeographicRegion() {
        return getTyped(24, ST.class);
    }
    public ST getZAD26_CountyOrParish() {
        return getTyped(25, ST.class);
    }
    public ST getZAD27_CensusTract() {
        return getTyped(26, ST.class);
    }
    public ST getZAD28_ValidAddressIndicator() {
        return getTyped(27, ST.class);
    }
    public ST getZAD29_ValidationDate() {
        return getTyped(28, ST.class);
    }
    public ST getZAD30_ValidForResidenceIndicator() {
        return getTyped(29, ST.class);
    }
    public ST getZAD31_ValidForResidenceDate() {
        return getTyped(30, ST.class);
    }
    public ST getZAD32_ValidForResidenceCategory() {
        return getTyped(31, ST.class);
    }
    public ST getZAD33_ValidForMailingIndicator() {
        return getTyped(32, ST.class);
    }
    public ST getZAD34_ValidPhysicalAddressIndicator() {
        return getTyped(33, ST.class);
    }
    public ST getZAD35_AddressValidationBestGuessIndicator() {
        return getTyped(34, ST.class);
    }
}
