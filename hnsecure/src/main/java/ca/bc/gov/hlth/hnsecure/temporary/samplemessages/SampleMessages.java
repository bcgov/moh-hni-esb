package ca.bc.gov.hlth.hnsecure.temporary.samplemessages;

public class SampleMessages {

    /*
     These aren't real response messages for these message types, they just serve to help test the message routing is working until we have
     actual message routing configured
     */
	private SampleMessages() {
	}

    public static final String E45_RESPONSE_MESSAGE = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|E45|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

    public static final String R50_RESPONSE_MESSAGE = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|R50|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

	public static final String R09_RESPONSE_MESSAGE = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
            + "MSA|AA|20191108082211|HJMB001ISUCCESSFULLY COMPLETED\r\n"
			+ "ZTL|2^RD\r\n"
			+ "PID|1|1314500002^^^BC^PH|||||1989|M\r\n" 
			+ "PID|2|2564500001^^^BC^PH|||||1973|M\r\n" 
			+ "ZIA|||||||||||||||Branton^James^^^^^|||||||1\r\n"
			+ "ZIA|||||||||||||||Branton^Debbie^^^^^|||||||2\r\n";

}
