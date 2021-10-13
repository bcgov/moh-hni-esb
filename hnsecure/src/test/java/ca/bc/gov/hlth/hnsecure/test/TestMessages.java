package ca.bc.gov.hlth.hnsecure.test;

/**
 * We use sample messages in JUnit testing.
 * This class contains all the test messages.
 *  New messages can be added for more tests
 * @author pankaj.kathuria
 *
 */

public class TestMessages {
	
	public static String MSG_PHARMANET = "00000352MSH|^~\\&|PLEXIAPNP|BC01000176|PNP|MD|2020/11/26 21:52:53|JHEWH$#!:192.168.22.66|ZPN|18|D|2.1||\n"
			+ "ZZZ|TID||18|91|XYACA||||\n" 
			+ "ZCA||03|00|PE|02\n" + "ZCB|BCXX000024|201126|18\n"
			+ "ZCC||||||||||0009735000001|";

	public static String MSG_E45 = "MSH|^~\\&|HR|BC00000098|RAIGET-DOC-SUM|BC0003000|19991004103039|lharris|E45|19980915000015|D|2.3"
			+ "HDR|||TRAININGAdmin\r\n" + "SFT|1.0||testorg^^orgid^^^MOH|1.0|barebones||\r\n"
			+ "QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19421112||||||19980601||PVC^^HNET9909||\r\n"
			+ "RCP|I|";
	

	/**
	 * Sample R50^Z05 - Enroll Visa Subscriber without PHN
	 */
	public static String MSG_R50_Z05 = "MSH|^~\\&|HNWeb|BC01000030|RAIENROL-EMP|BC00002041|20210121120533|train96|R50^Z05|20210121120533|D|2.4||\r\n"
			+ "ZHD|20210121120533|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "PID|||||||19990101|M\r\n"
			+ "ZIA||20200801|||||||||||||Gordon^Tom^^^^^L|1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^H~1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^M||||||||S|AB\r\n"
			+ "IN1||||||||6337109||||20210101|20221";
	
	public static String MSG_R09 = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
			+ "ZHD|20191108082211|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "QRD|||||||^RD||PSN\r\n"
			+ "PID||^^^BC^PH|||||1989|M\r\n" + "ZIA|||||||||||||||branton\r\n";

	public static String MSG_R09_WITH_PREFIX = "00000165MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
			+ "ZHD|20191108082211|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "QRD|||||||^RD||PSN\r\n"
			+ "PID||^^^BC^PH|||||1989|M\r\n" + "ZIA|||||||||||||||branton\r\n";

	public static String R09_RESPONSE_MESSAGE = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
            + "MSA|AA|20191108082211|HJMB001ISUCCESSFULLY COMPLETED\r\n"
			+ "ZTL|2^RD\r\n"
			+ "PID|1|1314500002^^^BC^PH|||||1989|M\r\n" 
			+ "PID|2|2564500001^^^BC^PH|||||1973|M\r\n" 
			+ "ZIA|||||||||||||||Branton^James^^^^^|||||||1\r\n"
			+ "ZIA|||||||||||||||Branton^Debbie^^^^^|||||||2\r\n";

	public static String MSG_R03 = "MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n"
			+ "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "PID||0891250000^^^BC^PH\r\n";

	public static String MSG_R15 = "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n"
			+ "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" 
			+ "PID||0314500001^^^BC^PH\r\n"
			+ "IN1||||||||||||20190228\r\n";

	public static String INVALID_MSG_R32 = "00000352MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-CNT-PRDS|BC00001013|20210820126|train96|R32|%s|D|2.4||\r"
			+ "ZHD|20210831112325|^^00000010|HNAIADMINISTRATION||||2.4\r" 
			+ "PID||9306448169^^^BC^PH";
}
