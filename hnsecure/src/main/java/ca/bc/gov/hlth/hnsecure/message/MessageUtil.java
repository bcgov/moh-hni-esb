package ca.bc.gov.hlth.hnsecure.message;

import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
	
	public static final Map<String, String> mTypeCollection = new HashMap<>();
	
	static {
		mTypeCollection.put("R15", "RAICHK-BNF-CVST");
		mTypeCollection.put("R33", "RAICMPL-PR-INFO");
		mTypeCollection.put("E45", "RAIELG-CNFRM");
		mTypeCollection.put("R35", "RAIEND-PYR-RL");
		mTypeCollection.put("R36", "RAIEND-PYR-RLDP");
		mTypeCollection.put("R30", "RAIEST-PYR-RL");
		mTypeCollection.put("R31", "RAIEST-PYR-RLDP");
		mTypeCollection.put("R37", "RAIGT-ACCT-ADDR");
		mTypeCollection.put("R16", "RAIGT-BNF-CVPRD");
		
		mTypeCollection.put("R32", "RAIGT-CNT-PRDS");
		mTypeCollection.put("R22", "RAIGT-DCMNT-DTL");
		mTypeCollection.put("R21", "RAIGT-DCMNT-SMY");
		mTypeCollection.put("R03", "RAIGT-PRSN-DMGR");
		mTypeCollection.put("R41", "RAINW-PYR-INQRY");
		mTypeCollection.put("R42", "RAIPHN-LOOKUP");
		mTypeCollection.put("R09", "RAIPRSN-NM-SRCH");
		mTypeCollection.put("R20", "RAIRCRD-DCMNT");
		mTypeCollection.put("R08", "RAIRCRD-DTH-VNT");
		
		
		mTypeCollection.put("R01", "RAIRCRD-NW-BRN");
		mTypeCollection.put("R02", "RAIRCRD-NW-PRSN");
		mTypeCollection.put("R38", "RAIUP-ACCT-ADDR");
		mTypeCollection.put("R39", "RAIUP-ACCT-PHON");
		mTypeCollection.put("R34", "RAINW-PYR-INQRY");
		mTypeCollection.put("R07", "RAIUPDT-PR-ADDR");
		mTypeCollection.put("R06", "RAIUPDT-PR-DEMO");
		mTypeCollection.put("R05", "RAIVLDT-ADDR");
		mTypeCollection.put("R70", "PHCGT-REGDATA");
		
	
		mTypeCollection.put("R71", "PHCADD-REG");
		mTypeCollection.put("R72", "PHCDE-REG");
		mTypeCollection.put("R73", "PHCCHG-REG");
		mTypeCollection.put("R74", "PHCGT-PENDATA");
		mTypeCollection.put("R75", "PHCPEND-OVRD");
		mTypeCollection.put("R76", "PHCQRY-REPORT");
		mTypeCollection.put("R49", "RAICOVPART-ENQ");
		mTypeCollection.put("R43", "RAIRNST-OA-DEP");
		mTypeCollection.put("R44", "RAIRNST-CNCL");
		
	
		mTypeCollection.put("R45", "RAIRNW-CNCL");
		mTypeCollection.put("R46", "RAIUPT-PREMCOV");
		mTypeCollection.put("R50", "RAIENROL-EMP");
		mTypeCollection.put("R51", "RAIEXTEND-VISA");
		mTypeCollection.put("R52", "RAIENROL-DEP");
		mTypeCollection.put("R53", "RAIEMP-FIND");
		mTypeCollection.put("R54", "RAIEXT-VISA-DEP");
		mTypeCollection.put("R55", "RAIEMP-LIST");
		mTypeCollection.put("*", "PNP");		

	}

}
