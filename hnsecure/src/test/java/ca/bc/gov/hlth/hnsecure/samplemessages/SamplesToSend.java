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
    public static String r03JsonMsgLocal = "{\n" +
            "\"resourceType\": \"DocumentReference\",\n" +
            "\"status\" : \"current\",\n" +
            "\"content\": [{\n" +
            "\"attachment\": {\n" +
            "\"contentType\": \"x-application/hl7-v2+er7\",\n" +
            "\"data\": \"TVNIfF5+XCZ8SE5XRUJ8bW9oX2huY2xpZW50X2RldnxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMTAxM3wyMDE3MDEyNTEyMjEyNXx0cmFpbjk2fFIwM3wyMDE3MDEyNTEyMjEyNXxEfDIuNHx8ClpIRHwyMDE3MDEyNTEyMjEyNXxeXjAwMDAwMDEwfEhOQUlBRE1JTklTVFJBVElPTnx8fHwyLjQKUElEfHwxMjM0NTY3ODkwXl5eQkNeUEgN\"\n" +
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
    
    public static String AUTH_HEADER = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WEtsZHlfcFR3V3hpMWx5eU1VSDRzYllBTUwzWWg1VGlkZ0NGWk9iX1pVIn0.eyJleHAiOjE2MTU5MTM3MzksImlhdCI6MTYxNTkxMzQzOSwianRpIjoiYmQ3ZjExYzAtOTdhMi00MjcwLThjNzMtNzNjZTFkYzViNDRkIiwiaXNzIjoiaHR0cHM6Ly9jb21tb24tbG9nb24tZGV2LmhsdGguZ292LmJjLmNhL2F1dGgvcmVhbG1zL3YyX3BvcyIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI1NDY2NDA2MC1kZTc2LTQ5MjYtYTA3Yy0zZTRjNjNhMjAyMDEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJtb2hfaG5jbGllbnRfZGV2Iiwic2Vzc2lvbl9zdGF0ZSI6IjNhZGMzYjM0LTRjN2YtNDNhZS1hZmViLTRiZWZkY2Q2YTQ0MiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoic3lzdGVtLyoud3JpdGUgcHJvZmlsZSIsImNsaWVudElkIjoibW9oX2huY2xpZW50X2RldiIsImNsaWVudEhvc3QiOiIxNDIuMzQuMTQ3LjQiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtbW9oX2huY2xpZW50X2RldiIsImNsaWVudEFkZHJlc3MiOiIxNDIuMzQuMTQ3LjQifQ.VlKomOSLRkz-tVIPw1IkWsnp8bcm3NGSPeWUIvuCdibyHuW5ba7hIJIuJrzCwOAwrrMAqB-w0V52R2B8UcTqTkv38Tzi_bZB21v99_l6x-dLMKpH8kuzHQeDTOGkb_b6s-mzVX9YBxf_WbkJZXkUPRE1bK9sB0s6Cm1qXq7vqDC-kBhcOL4CH5PhPbGZTb2kVUl_PyHXCFprUxxJ1pBRunKCAo8fubXJ9Mt94Fd1Om2c9EDHNH5PCLd_yLh_rOw3bSiHcB7_eY6_BLCdjuAZ4wDye4ZKWAl_TV2MC2ea_u30Io4v-eAW5Btl7ur-kyGK-gtDiJNOeO9Af824fRPJUA";
}
