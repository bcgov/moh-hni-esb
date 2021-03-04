package ca.bc.gov.hlth.hnsecure.samplemessages;

public class SamplesToSend {

    public static String r03V2Msg = "00000352MSH|^~\\&|HNWEB|VIHA|RAIGT-PRSN-DMGR|BC00001013|20170125122125|train96|R03|20170125122125|D|2.4||\n"
            + "ZHD|20170125122125|^^00000010|HNAIADMINISTRATION||||2.4\n"
            + "PID||1234567890^^^BC^PH";

    public static String r03Base64 ="TVNIfF5+XCZ8SE5XZWJ8QkMwMTAwMDAzMHxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMjA0MXwyMDE5MTEwODA4MzI0NHx0cmFpbjk2fFIwM3wyMDE5MTEwODA4MzI0NHxEfDIuNHx8DQpaSER8MjAxOTExMDgwODMyNDR8Xl4wMDAwMDAxMHxITkFJQURNSU5JU1RSQVRJT058fHx8Mi40DQpQSUR8fDAwMDAwNTM2NTVeXl5CQ15QSA0K";
    public static String e45Base64 = "TVNIfF5+XCZ8SFJ8QkMwMDAwMDA5OHxSQUlHRVQtRE9DLVNVTXxCQzAwMDMwMDB8MTk5OTEwMDQxMDMwMzl8bGhhcnJpc3xFNDV8MTk5ODA5MTUwMDAwMTV8RHwyLjNIRFJ8fHxUUkFJTklOR0FkbWluDQpTRlR8MS4wfHx0ZXN0b3JnXl5vcmdpZF5eXk1PSHwxLjB8YmFyZWJvbmVzfHwNClFQRHxFNDVeXkhORVQwMDAzfDF8Xl4wMDAwMDAwMV5eXkNBTkJDXlhYXk1PSHxeXjAwMDAwMDAxXl5eQ0FOQkNeWFheTU9IfF5eMDAwMDA3NTReXl5DQU5CQ15YWF5NT0h8OTAyMDE5ODc0Nl5eXkNBTkJDXkpITl5NT0h8fDE5NDIwMTEyfHx8fHx8MTk5ODA2MDF8fFBWQ15eSE5FVDk5MDl8fA0KUkNQfEl8";

    public static String r03JsonMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8SE5XZWJ8QkMwMTAwMDAzMHxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMjA0MXwyMDE5MTEwODA4MzI0NHx0cmFpbjk2fFIwM3wyMDE5MTEwODA4MzI0NHxEfDIuNHx8DQpaSER8MjAxOTExMDgwODMyNDR8Xl4wMDAwMDAxMHxITkFJQURNSU5JU1RSQVRJT058fHx8Mi40DQpQSUR8fDAwMDAwNTM2NTVeXl5CQ15QSA0K\"\n" +
            "}\n" +
            "}]\n" +
            "}";

    public static String e45JsonMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8SFJ8QkMwMDAwMDA5OHxSQUlHRVQtRE9DLVNVTXxCQzAwMDMwMDB8MTk5OTEwMDQxMDMwMzl8bGhhcnJpc3xFNDV8MTk5ODA5MTUwMDAwMTV8RHwyLjNIRFJ8fHxUUkFJTklOR0FkbWluDQpTRlR8MS4wfHx0ZXN0b3JnXl5vcmdpZF5eXk1PSHwxLjB8YmFyZWJvbmVzfHwNClFQRHxFNDVeXkhORVQwMDAzfDF8Xl4wMDAwMDAwMV5eXkNBTkJDXlhYXk1PSHxeXjAwMDAwMDAxXl5eQ0FOQkNeWFheTU9IfF5eMDAwMDA3NTReXl5DQU5CQ15YWF5NT0h8OTAyMDE5ODc0Nl5eXkNBTkJDXkpITl5NT0h8fDE5NDIwMTEyfHx8fHx8MTk5ODA2MDF8fFBWQ15eSE5FVDk5MDl8fA0KUkNQfEl8\"\n" +
            "}\n" +
            "}]\n" +
            "}";
}
