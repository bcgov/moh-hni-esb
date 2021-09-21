package ca.bc.gov.hlth.hnsecure.samplemessages;

public class SamplesToSend {

    public static final String r03V2Msg = "00000352MSH|^~\\&|HNWEB|VIHA|RAIGT-PRSN-DMGR|BC00001013|20170125122125|train96|R03|20170125122125|D|2.4||\n"
            + "ZHD|20170125122125|^^00000010|HNAIADMINISTRATION||||2.4\n"
            + "PID||1234567890^^^BC^PH";

    public static final String r03Base64 ="TVNIfF5+XCZ8SE5XZWJ8QkMwMTAwMDAzMHxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMjA0MXwyMDE5MTEwODA4MzI0NHx0cmFpbjk2fFIwM3wyMDE5MTEwODA4MzI0NHxEfDIuNHx8DQpaSER8MjAxOTExMDgwODMyNDR8Xl4wMDAwMDAxMHxITkFJQURNSU5JU1RSQVRJT058fHx8Mi40DQpQSUR8fDAwMDAwNTM2NTVeXl5CQ15QSA0K";
    
    public static final String e45Base64 = "TVNIfF5+XCZ8SFJ8QkMwMDAwMDA5OHxSQUlHRVQtRE9DLVNVTXxCQzAwMDMwMDB8MTk5OTEwMDQxMDMwMzl8bGhhcnJpc3xFNDV8MTk5ODA5MTUwMDAwMTV8RHwyLjNIRFJ8fHxUUkFJTklOR0FkbWluDQpTRlR8MS4wfHx0ZXN0b3JnXl5vcmdpZF5eXk1PSHwxLjB8YmFyZWJvbmVzfHwNClFQRHxFNDVeXkhORVQwMDAzfDF8Xl4wMDAwMDAwMV5eXkNBTkJDXlhYXk1PSHxeXjAwMDAwMDAxXl5eQ0FOQkNeWFheTU9IfF5eMDAwMDA3NTReXl5DQU5CQ15YWF5NT0h8OTAyMDE5ODc0Nl5eXkNBTkJDXkpITl5NT0h8fDE5NDIwMTEyfHx8fHx8MTk5ODA2MDF8fFBWQ15eSE5FVDk5MDl8fA0KUkNQfEl8";

    public static final String r03JsonMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8SE5XZWJ8QkMwMTAwMDAzMHxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMjA0MXwyMDE5MTEwODA4MzI0NHx0cmFpbjk2fFIwM3wyMDE5MTEwODA4MzI0NHxEfDIuNHx8DQpaSER8MjAxOTExMDgwODMyNDR8Xl4wMDAwMDAxMHxITkFJQURNSU5JU1RSQVRJT058fHx8Mi40DQpQSUR8fDAwMDAwNTM2NTVeXl5CQ15QSA0K\"\n" +
            "}\n" +
            "}]\n" +
            "}";
    public static final String r03JsonMsgLocal = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8SE5XRUJ8bW9oX2huY2xpZW50X2RldnxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMTAxM3wyMDE3MDEyNTEyMjEyNXx0cmFpbjk2fFIwM3wyMDE3MDEyNTEyMjEyNXxEfDIuNHx8ClpIRHwyMDE3MDEyNTEyMjEyNXxeXjAwMDAwMDEwfEhOQUlBRE1JTklTVFJBVElPTnx8fHwyLjQKUElEfHwxMjM0NTY3ODkwXl5eQkNeUEgN\"\n" +
            "}\n" +
            "}]\n" +
            "}";

    public static final String e45JsonMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8SFJ8QkMwMDAwMDA5OHxSQUlHRVQtRE9DLVNVTXxCQzAwMDMwMDB8MTk5OTEwMDQxMDMwMzl8bGhhcnJpc3xFNDV8MTk5ODA5MTUwMDAwMTV8RHwyLjNIRFJ8fHxUUkFJTklOR0FkbWluDQpTRlR8MS4wfHx0ZXN0b3JnXl5vcmdpZF5eXk1PSHwxLjB8YmFyZWJvbmVzfHwNClFQRHxFNDVeXkhORVQwMDAzfDF8Xl4wMDAwMDAwMV5eXkNBTkJDXlhYXk1PSHxeXjAwMDAwMDAxXl5eQ0FOQkNeWFheTU9IfF5eMDAwMDA3NTReXl5DQU5CQ15YWF5NT0h8OTAyMDE5ODc0Nl5eXkNBTkJDXkpITl5NT0h8fDE5NDIwMTEyfHx8fHx8MTk5ODA2MDF8fFBWQ15eSE5FVDk5MDl8fA0KUkNQfEl8\"\n" +
            "}\n" +
            "}]\n" +
            "}";
    
    public static final String pnpJsonMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8UExFWElBUE5QfG1vaF9obmNsaWVudF9kZXZ8UE5QfFBQfDIwMjAvMTEvMjYgMjE6NTI6NTN8SkhFV0gkIyE6MTkyLjE2OC4yMi42NnxaUE58MTh8RHwyLjF8fApaWlp8VElEfHwxOHw5MXxYWUFDQXx8fHwKWkNBfHwwM3wwMHxQRXwwMgpaQ0J8QkNYWDAwMDAyNHwyMDExMjZ8MTgKWkNDfHx8fHx8fHx8fDAwMDk3MzUwMDAwMDF8DQ==\"\n" +
            "}\n" +
            "}]\n" +
            "}";
    public static final String jmbJsonMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8UkFJR1QtQ05ULVBSRFN8QkMwMDAwMTAxM3xITldlYnxCQzAxMDAwMDMwfDIwMjEwODI3MDkxNjM5fGFudS0yNi1ibGFua01zZ0NudHJsfFIzMnwyMDIxMDgyNzA5MTYzOXxEfDIuNA1NU0F8QUV8fEhKTUIwMDFFUmVxdWlyZWQgZmllbGQgbWlzc2luZzpNU0gvTWVzc2FnZUNvbnRyb2xJRA1FUlJ8Xl5eSEpNQjAwMUUmUmVxdWlyZWQgZmllbGQgbWlzc2luZzpNU0gvTWVzc2FnZUNvbnRyb2xJRA0=\"\n" +        
            "}\n" +
            "}]\n" +
            "}";
    
    public static final String pnpJsonErrorMsg = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfGRkXCZ8UExFWElBUE5QfG1vaF9obmNsaWVudF9kZXZ8UE5QfFBQfDIwMjAvMTEvMjYgMjE6NTI6NTN8SkhFV0gkIyE6MTkyLjE2OC4yMi42NnxaUE58MTh8RHwyLjF8fApaWlp8VElEfHwxOHw5MXxYWUFDQXx8fHwKWkNBfHwwM3wwMHxQRXwwMgpaQ0J8QkNYWDAwMDAyNHwyMDExMjZ8MTgKWkNDfHx8fHx8fHx8fDAwMDk3MzUwMDAwMDF8DQ\"\n" +
            "}\n" +
            "}]\n" +
            "}";
    
    public static final String hibcJsonMsg = "{\r\n"
    		+ "    \"content\":[\r\n"
    		+ "        {\r\n"
    		+ "            \"attachment\":{\r\n"
    		+ "                \"data\":\"TVNIfF5+XCZ8SE5XZWJ8bW9oX2huY2xpZW50X2RldnxSQUlDSEstQk5GLUNWU1R8QkMwMDAwMTAxM3wyMDIxMDUxMzE4Mjk0MXx0cmFpbjk2fFIxNXwyMDIxMDUxMzE4Mjk0MXxEfDIuNHx8DQpaSER8MjAyMTA1MTMxODI5NDF8Xl4wMDAwMDAxMHxITkFJQURNSU5JU1RSQVRJT058fHx8Mi40DQpQSUR8fDk4Nzk4NzU5MTReXl5CQ15QSA0KSU4xfHx8fHx8fHx8fHx8MjAyMTA1MTM=\",\r\n"
    		+ "                \"contentType\":\"x-application\\/hl7-v2+er7\"\r\n"
    		+ "                }\r\n"
    		+ "            }],\r\n"
    		+ "            \"resourceType\":\"DocumentReference\",\r\n"
    		+ "                \"status\":\"current\"\r\n"
    		+ "    }";
    
    public static final String AUTH_HEADER = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WEtsZHlfcFR3V3hpMWx5eU1VSDRzYllBTUwzWWg1VGlkZ0NGWk9iX1pVIn0.eyJleHAiOjE2MTU5MTM3MzksImlhdCI6MTYxNTkxMzQzOSwianRpIjoiYmQ3ZjExYzAtOTdhMi00MjcwLThjNzMtNzNjZTFkYzViNDRkIiwiaXNzIjoiaHR0cHM6Ly9jb21tb24tbG9nb24tZGV2LmhsdGguZ292LmJjLmNhL2F1dGgvcmVhbG1zL3YyX3BvcyIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI1NDY2NDA2MC1kZTc2LTQ5MjYtYTA3Yy0zZTRjNjNhMjAyMDEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJtb2hfaG5jbGllbnRfZGV2Iiwic2Vzc2lvbl9zdGF0ZSI6IjNhZGMzYjM0LTRjN2YtNDNhZS1hZmViLTRiZWZkY2Q2YTQ0MiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoic3lzdGVtLyoud3JpdGUgcHJvZmlsZSIsImNsaWVudElkIjoibW9oX2huY2xpZW50X2RldiIsImNsaWVudEhvc3QiOiIxNDIuMzQuMTQ3LjQiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtbW9oX2huY2xpZW50X2RldiIsImNsaWVudEFkZHJlc3MiOiIxNDIuMzQuMTQ3LjQifQ.VlKomOSLRkz-tVIPw1IkWsnp8bcm3NGSPeWUIvuCdibyHuW5ba7hIJIuJrzCwOAwrrMAqB-w0V52R2B8UcTqTkv38Tzi_bZB21v99_l6x-dLMKpH8kuzHQeDTOGkb_b6s-mzVX9YBxf_WbkJZXkUPRE1bK9sB0s6Cm1qXq7vqDC-kBhcOL4CH5PhPbGZTb2kVUl_PyHXCFprUxxJ1pBRunKCAo8fubXJ9Mt94Fd1Om2c9EDHNH5PCLd_yLh_rOw3bSiHcB7_eY6_BLCdjuAZ4wDye4ZKWAl_TV2MC2ea_u30Io4v-eAW5Btl7ur-kyGK-gtDiJNOeO9Af824fRPJUA";

    public static final String msgInvalidMSH="MSH|^~\\%|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";
    
    public static final String msgMissingMSH="|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    public static final String msgR03="MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    public static final String msgR15= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n" +
            "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000073721^^^BC^PH\r\n" +
            "IN1||||||||||||20190228\r\n";
    
    public static final String msgInvalidFormat= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D\r\n" +
            "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION|||2.4\r\n" +
            "PID||0000073721^^^BC^PH\r\n" +
            "IN1||||||||||||20190228\r\n";
    
    public static final String msgMissingEncodingChar="MSH|~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";
    
    public static final String invalidRecevingApp="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMG|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    public static final String missingReceivingFacility="MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-PRSN-DMGR||20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";
    
    public static final String unknownReceivingApplication="MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-PRSN-DMGRX|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";
    
    public static final String validPharmanetMessage= "MSH|^~\\&|TRXTOOL|moh_hnclient_dev|PNP|PP|||ZPN||P|2.1||\r\n"
            + "ZZZ|TDR||9286|P1|2F3P2||||\r\n"
            + "ZCA||03|00|KC|13|\r\n"
            + "ZCB|BC00007007|201222|9286\r\n"
            + "ZPC|2240579||||||Y|ZPC1^^^766720\r\n"
            + "\r\n";
 
    public static final String inValidPhramanetMessage= "MSH|^~\\&|TRXTOOL|moh_hnclient_dev|PNP|PP|||ZPN|9286|P|2.1||\r\n"
            + "ZZZ|TDR||9286|P1|2F3P2||||\r\n"
            + "ZCA||03|00|KC|13|\r\n"	           
            + "ZPC|2240579||||||Y|ZPC1^^^766720\r\n";  
    
    public static final String invalidFhirJsonMsg = "{\"content\":[{\"attachment\":{\"data\":\"MDAwMDAzNTJNU0h8Xn5cJnxITldFQnxWSUhBfFJBSUdULVBSU04tRE1HUnxCQzAwMDAxMDEzfDIwMTcwMTI1MTIyMTI1fHRyYWluOTZ8UjAzfDIwMTcwMTI1MTIyMTI1fER8Mi40fHwKWkhEfDIwMTcwMTI1MTIyMTI1fF5eMDAwMDAwMTB8SE5BSUFETUlOSVNUUkFUSU9OfHx8fDIuNApQSUR8fDEyMzQ1Njc4OTBeXl5CQ15QSA=B=\",\"contentType\":\"x-application\\/hl7-v2+er7\"}}],\"resourceType\":\"DocumentReference\",\"status\":\"current\"}";
}
