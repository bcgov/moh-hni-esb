package ca.bc.gov.hlth.hnsecure.temporary.samplemessages;

public class SampleMessages {

    /*
     These aren't real response messages for these message types, they just serve to help test the message routing is working until we have
     actual message routing configured
     */

    public static final String r03ResponseMessage = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|R03|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

    public static final String e45ResponseMessage = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|E45|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

    public static final String pnpResponseMessage = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|PNP|BC01000030|20200206123841|train96|ZPN|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

    public static final String r50ResponseMessage = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|R50|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";
}
