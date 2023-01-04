package ca.bc.gov.hlth.hnsecure.test;

/**
 * We use sample messages in JUnit testing.
 * This class contains all the test messages.
 *  New messages can be added for more tests
 *
 */

public class TestMessages {
	
	public static final String MSG_PHARMANET_REQUEST = "00000352MSH|^~\\&|PLEXIAPNP|BC01000176|PNP|MD|2020/11/26 21:52:53|JHEWH$#!:192.168.22.66|ZPN|18|D|2.1||\n"
			+ "ZZZ|TID||18|91|XYACA||||\n" 
			+ "ZCA||03|00|PE|02\n" + "ZCB|BCXX000024|201126|18\n"
			+ "ZCC||||||||||0009735000001|";
	
	public static final String MSG_PHARMANET_RESPONSE = "MSH|^~\\&|123456789|123456789|123456789|123456789|||ZPN|000001|P|2.1\r\n" 
			+ "ZCB|PHARMACYXX|DATE|000001\r\n"  
			+ "ZZZ|TRP|0|1|P1|XXXXX||0 Operation successful\r\n"
			+ "ZCC||||||||||123456789|\r\n" 
			+ "ZPB|ZPB1^CLINICAL CONDITION DESCRIPTION^N^PA^19950101^CLINICAL";
	
	public static final String MSG_PHARMANET_ERROR_RESPONSE = "MSH|^~\\&|123456789|123456789|123456789|123456789|||ZPN|000001|P|2.1\r\n" 
			+ "ZCB|PHARMACYXX|DATE|000001\r\n"  
			+ "ZZZ||1|||||143 Request cannot be processed : Call Help Desk :Incident#12279159\r\n";

	public static final String MSG_E45_REQUEST = "MSH|^~\\&|HR|BC00000098|RAIGET-DOC-SUM|BC0003000|19991004103039|lharris|E45|19980915000015|D|2.3"
			+ "HDR|||TRAININGAdmin\r\n" + "SFT|1.0||testorg^^orgid^^^MOH|1.0|barebones||\r\n"
			+ "QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19421112||||||19980601||PVC^^HNET9909||\r\n"
			+ "RCP|I|";

	public static final String MSG_E45_RESPONSE = "MSH|^~\\&|RAIELG-CNFRM|BC00001013|HNCLIENT|moh_hnclient_dev|20210916161246|rajan.reddy|E45||D|2.4||\r\n"
			+ "MSA|AA||HJMB001ISUCCESSFULLY COMPLETED\r\n" 
			+ "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED\r\n" 
			+ "QAK|1|AA|E45^^HNET0003\r\n" 
			+ "QPD|E45^^HNET0003|1|^^00000010^^^CANBC^XX^MOH|^^00000010^^^CANBC^XX^MOH|^^00000745^^^CANBC^XX^MOH|9390352021^^^CANBC^JHN^MOH||19570713||||||20200505||PVC^^HNET9909~EYE^^HNET9909~PRS^^HNET9909\r\n" 
			+ "PID|||9390352021||GRIDDLEXB^BASHERXO^SIGURDURXP^^^^L||19570713|M\r\n"
			+ "IN1|||00000745^^^CANBC^XX^MOH||||||||||||||||||||||Y\r\n" 
			+ "ADJ|1|IN|||PVC^^HNET9908|0\r\n"
			+ "ADJ|2|IN|||EYE^^HNET9908\r\n" 
			+ "ADJ|3|IN|||PRS^^HNET9908|N";
	/**
	 * Sample R50^Z05 - Enroll Visa Subscriber without PHN
	 */
	public static final String MSG_R50_Z05 = "MSH|^~\\&|HNWeb|BC01000030|RAIENROL-EMP|BC00002041|20210121120533|train96|R50^Z05|20210121120533|D|2.4||\r\n"
			+ "ZHD|20210121120533|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "PID|||||||19990101|M\r\n"
			+ "ZIA||20200801|||||||||||||Gordon^Tom^^^^^L|1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^H~1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^M||||||||S|AB\r\n"
			+ "IN1||||||||6337109||||20210101|20221";
	
	public static final String MSG_R50_ZO6 = "MSH|^~\\&|HNWeb|HN-WEB|RAIENROL-EMP|BC00001013|20220329123956|ansrivas@idir|R50^Z06|20220329123956|D|2.4\r\n" 
			+ "ZHD|20220329123956|^^00000010|TRAININGHEALTHAUTH E45||||2.4\r\n"  
			+ "PID||9878259011^^^BC^PH|||||19610314|M\r\n" 
			+ "ZIA||20211201|||||||||||||DUMPTY^HUMPTY^^^^^L|5932 TORONTO AVE^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V6K8K3^^H~^^^^^^^^^^^^^^^^^^^^^^^M||||||||W|AB\r\n"  
			+ "IN1||||||||6243109||||20220301|20221231\r\n" 
			+ "ZIH|||||||||||||||||||D\r\n"
			+ "ZIK||||VISA_ISSUE^20211201~VISA_XPIRY^20221231\r\n";
			
	
	public static final String MSG_R09_REQUEST = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
			+ "ZHD|20191108082211|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "QRD|||||||^RD||PSN\r\n"
			+ "PID||^^^BC^PH|||||1989|M\r\n" 
			+ "ZIA|||||||||||||||branton\r\n";
	
	public static final String MSG_R09_RESPONSE = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
            + "MSA|AA|20191108082211|HJMB001ISUCCESSFULLY COMPLETED\r\n"
			+ "ZTL|2^RD\r\n"
			+ "PID|1|1314500002^^^BC^PH|||||1989|M\r\n" 
			+ "PID|2|2564500001^^^BC^PH|||||1973|M\r\n" 
			+ "PID|3|2564500001^^^BC^PH|||||1973|M\r\n"
			+ "ZIA|||||||||||||||Branton^James^^^^^|||||||1\r\n"
			+ "ZIA|||||||||||||||Branton^Debbie^^^^^|||||||2\r\n";

	public static final String MSG_R09_WITH_PREFIX =  "00000165MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
            + "MSA|AA|20191108082211|HJMB001ISUCCESSFULLY COMPLETED\r\n"
			+ "ZTL|2^RD\r\n"
			+ "PID|1|1314500002^^^BC^PH|||||1989|M\r\n" 
			+ "PID|2|2564500001^^^BC^PH|||||1973|M\r\n" 
			+ "PID|3|2564500001^^^BC^PH|||||1973|M\r\n"
			+ "ZIA|||||||||||||||Branton^James^^^^^|||||||1\r\n"
			+ "ZIA|||||||||||||||Branton^Debbie^^^^^|||||||2\r\n";

	public static final String MSG_R15_REQUEST = "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n"
			+ "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "PID||0314500001^^^BC^PH\r\n"
			+ "IN1||||||||||||20190228\r\n";
	
	public static final String MSG_R15_RESPONSE = "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n"
			+ "MSA|AA|20191108082211|HJMB001ISUCCESSFULLY COMPLETED\r\n"
			+ "ZIH|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 	
			+ "IN1||||||||||||20190228\r\n" 		
	 		+ "ZTL|2^RD\r\n";

	public static final String MSG_R03_REQUEST = "MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n"
			+ "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "PID||0891250000^^^BC^PH\r\n";
	
	public static final String MSG_R03_RESPONSE = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|R03|1819924|D|2.4^M\r\n"
			+ "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\r\n" 
			+ "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\r\n" 
			+ "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\r\n" 
			+ "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

	public static final String MSG_R32_CARRIAGE_RETURN_EOL = "00000352MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-CNT-PRDS|BC00001013|20210820126|train96|R32|%s|D|2.4||\r"
			+ "ZHD|20210831112325|^^00000010|HNAIADMINISTRATION||||2.4\r" 
			+ "PID||9306448169^^^BC^PH";
	
	public static final String MSG_R32_MULTI_PID_RESPONSE =  "MSH|^~\\&|RAIGT-CNT-PRDS|BC00001013|HNWeb|BC01000030|20200206123841|train96|R32|1819924|D|2.4^M\r\n" 
			+ "MSA|AA|20210819115331|HJMB001ISUCCESSFULLY COMPLETED\r\n" 
			+ "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED\r\n" 
			+ "ERR|^^^HJMB121I&MORE THAN 5 COVERAGE PERIODS FOUND. NOT ALL INFORMATION RETURNED.\r\n" 
			+ "ZIA|||||||||||||||C-RATIONXC^BLAIRXH^NELLOXG^^^^L\r\n"
			+ "PID||9337796509^^^BC^PH\r\n" 
			+ "PID||9360338021^^^BC^PH\r\n" 
			+ "NK1|||DP\r\n"
			+ "IN1||||||||0000001||||20150601|20150601\r\n" 
			+ "ZIH||||||||||||||||||||E\r\n" 
			+ "PID||9301073095^^^BC^PH\r\n" 
			+ "NK1|||DP\r\n" 
			+ "IN1||||||||6166052||||20150601|20150601\r\n" 
			+ "ZIH||||||||||||||||||||E\r\n" 
			+ "PID||9360338021^^^BC^PH\r\n" 
			+ "NK1|||DP\r\n" 
			+ "IN1||||||||6038483||||20150601|00000000\r\n"
			+ "PID||9360338021^^^BC^PH\r\n"
			+ "NK1|||DP\r\n" 
			+ "IN1||||||||0000001||||20130901|20130901\r\n" 
			+ "ZIH||||||||||||||||||||E\r\n" 
			+ "PID||9360338021^^^BC^PH\r\n" 
			+ "NK1|||DP\r\n" 
			+ "IN1||||||||6038483||||20130801|20150531\r\n"
			+ "ZIH||||||||||||||||||||E";

	public static final String MSG_R03_NO_EOL = "MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||"
			+ "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4" 
			+ "PID||0891250000^^^BC^PH";

}
