package ITOM.Aiops;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import util.*;
import java.util.Map;

public class AiopsSituationBAPJiraTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationBAPJiraTest.class.getName());
    public static final String event_tag = "";
    private static String eventClass = "NAGIOS_EV1";
    private static String location = "Pune";
    HMEvents hm_events = null;
    CommonWaits commonWait = null;
    Aiops aiopsApiCore = null;
    private SoftAssert softAssert = null;
    CommonMethods commonmethod = null;
    String eventSeverity = "CRITICAL";
    String jwtToken = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    private static final String situationMessage = "Error: Could not load plugin";
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Cleaning up after the test...");
        if (commonmethod != null) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        try {
            if (hm_events != null) {
                hm_events.closeAllEventByClass(eventClass, jwtToken);
            }
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @BeforeMethod(alwaysRun = true)
    public void prerquirements() {
        log.info("Setting up the test...");
        hm_events = new HMEvents();
        aiopsDeepRCA = new AiopsDeepRCA();
        aiopsApiCore = new Aiops();
        commonmethod = new CommonMethods();
        commonWait = new CommonWaits();
        softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(null);
        try {
            log.info("Close all open Event");
            log.info("CLose all open event on Nagios class");
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            log.info("Close all open Situation");
            hm_events.closeAllEventByClass("Situation", jwtToken);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("Pre-requirments completed...");
    }
    
    @Test(description = "Verify BAP with Jira connector when Incident resolution is also updated along with jira id close notes", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "13105", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13105_Verify_BAP_with_Jira_connector_when_Incident_resolution_is_also_updated_along_with_jira_id_close_notes(ITestContext context)
            throws Exception {

        log.info("Check if BAP feature is enabled");
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("BAP")){
            log.info("BAP feature is not enabled; skipping test as passed.");
            return;
        }

        log.info("Check if DEEPRCA feature is enabled");
        if(aiopsDeepRCA.TenantFeatureStatusCheck("DEEPRCA")){
            log.info("DEEPRCA feature is enabled; skipping test as passed.");
            return;
        }


        if(!hm_events.getIncidentPolicyEnable(jwtToken)){
            Assert.fail("IA PSR is not enabled cannot run this test case");
        }

        log.info("Run ranker");
        if(!aiopsDeepRCA.runSituationRanker()){
            Assert.fail("Run ranker failed");
        }
        //create a similar situation
        String sheetName = "DeepRCATC";
        int testcase_number = 11;
        int no_of_event_per_node = 1;
        
        String requestId = null;

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");
        String jiraTicketNumber = logagentTC.get(0).get("Jira Ticket Number").toString();

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        //hm_events.closeAllEventByClass(eventClass, jwtToken);

        
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "CRITICAL", location, event_tag);
            commonWait.waitForSometime(1000);
        }
        
        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }
        
        log.info("Update Incident resolution and vendor notes in Jira");
        String incidentId = "";
        String source_identifier = "";
        String situationIdentifier = "";
        for (int j = 0; j < 6; j += 1) {
            JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
            situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).optString("_identifier", "");
            source_identifier = situationObject.getJSONArray("eventList").getJSONObject(0).optString("source_identifier", "");
            if (situationIdentifier.length() < 3) {
                commonWait.waitForSometime(10000);
                continue;
            }
            incidentId = situationObject.getJSONArray("eventList").getJSONObject(0).getString("incident_id");
            if (incidentId.length() < 3) {
                commonWait.waitForSometime(10000);
            } else {
                break;
            }
        }
        requestId = aiopsDeepRCA.getIncidentRequestId(incidentId);
        if (requestId == null) {
            Assert.fail("Unable to fetch Request ID for incident " + incidentId);
        }
        
        aiopsDeepRCA.updateITSMResolutionField(incidentId, serviceName, null, null, "Refer Jira Ticket");
        if (!aiopsDeepRCA.updateIncidentTicketField(requestId, "Vendor Ticket Number", jiraTicketNumber)) {
            Assert.fail("Unable to update Vendor Information in Incident Ticket");
        }
            
        String similarFileJson = "msearchsimilarsituationquery.json";
        JSONObject situationrecord = hm_events.runMSearchQueryGetSituation(situationMessage, jwtToken, similarFileJson, 86400000);
        int similarrecord = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (similarrecord <= 0) {
            return;
        }
        
        source_identifier = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets").getJSONObject(0).getJSONObject("top_hits#latest_doc").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("source_identifier");
        String _identifier = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets").getJSONObject(0).getJSONObject("top_hits#latest_doc").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
        
        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }

        List<String> similarSituationSummary = aiopsDeepRCA.getSituationGPTSummary(source_identifier);
        log.info("Similar Situation Summary: " + similarSituationSummary);

        log.info("Get the Code tool to validate the Incident resolution");
        List<String> BapTools = aiopsDeepRCA.getBapTools(source_identifier, _identifier);
        if (BapTools != null) {
            String bapString = BapTools.get(0);
            if (bapString == null || bapString.length() < 5) {
                log.error("bapstring failed");
            }else{
                JSONObject bapobject = new JSONObject(bapString);
                JSONArray bapTools = bapobject.getJSONArray("best_action_plan");
                for(int i = 0; i < bapTools.length(); i++) {
                    JSONObject bapToolObject = bapTools.getJSONObject(i);
                    String toolName = bapToolObject.getJSONArray("steps").getJSONObject(0).getString("tool");
                    if(toolName.equals("code tool")) {
                        String action = bapToolObject.getJSONArray("steps").getJSONObject(0).getString("action");
                        if(!action.contains("pod")) {
                            log.error("action failed");
                            softAssert.fail("action failed");
                        }
                        break;
                    }
                }
                
            }
        }
        softAssert.assertAll();
    }

    @Test(description = "[BAP] Verify BAP with Jira connector when Incident resolution is also updated along with jira id close notes", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "13105", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13105_Verify_BAP_with_Jira_connector_when_Incident_resolution_is_also_updated_along_with_jira_id_close_notes_for_DeepRCA(ITestContext context)
            throws Exception {

        log.info("Check if BAP feature is enabled");
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("BAP")){
            log.info("BAP feature is not enabled; skipping test as passed.");
            return;
        }

        log.info("Check if DEEPRCA feature is enabled");
        if(aiopsDeepRCA.TenantFeatureStatusCheck("DEEPRCA")){
            log.info("DEEPRCA feature is enabled; skipping test as passed.");
            return;
        }

        if(!hm_events.getIncidentPolicyEnable(jwtToken)){
            Assert.fail("IA PSR is not enabled cannot run this test case");
        }

        log.info("Run ranker");
        if(!aiopsDeepRCA.runSituationRanker()){
            Assert.fail("Run ranker failed");
        }
        //create a similar situation
        String sheetName = "DeepRCATC";
        int testcase_number = 12;
        int no_of_event_per_node = 1;
        
        String requestId = null;

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");
        String jiraTicketNumber = logagentTC.get(0).get("Jira Ticket Number").toString();
        String workLogDetail = logagentTC.get(0).get("Change Description").toString();
        String workLogTitle = logagentTC.get(0).get("Change Title").toString();

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        //hm_events.closeAllEventByClass(eventClass, jwtToken);

        
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "CRITICAL", location, event_tag);
            commonWait.waitForSometime(1000);
        }
        
        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }
        
        log.info("Update Incident resolution and vendor notes in Jira");
        String incidentId = "";
        String source_identifier = "";
        String situationIdentifier = "";
        for (int j = 0; j < 6; j += 1) {
            JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
            situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).optString("_identifier", "");
            source_identifier = situationObject.getJSONArray("eventList").getJSONObject(0).optString("source_identifier", "");
            if (situationIdentifier.length() < 3) {
                commonWait.waitForSometime(10000);
                continue;
            }
            incidentId = situationObject.getJSONArray("eventList").getJSONObject(0).getString("incident_id");
            if (incidentId.length() < 3) {
                commonWait.waitForSometime(10000);
            } else {
                break;
            }
        }
        requestId = aiopsDeepRCA.getIncidentRequestId(incidentId);
        if (requestId == null) {
            Assert.fail("Unable to fetch Request ID for incident " + incidentId);
        }

        log.info("Update Work Log in Incident");
        if (!aiopsDeepRCA.updateIncidentWorkLog(incidentId,  workLogTitle, workLogDetail)) {
            Assert.fail("Unable to update Work Log in Incident");
        }

        aiopsDeepRCA.updateITSMResolutionField(incidentId, serviceName, null, null, "Refer Jira Ticket");
        if (!aiopsDeepRCA.updateIncidentTicketField(requestId, "Vendor Ticket Number", jiraTicketNumber)) {
            Assert.fail("Unable to update Vendor Information in Incident Ticket");
        }
        
        String similarFileJson = "msearchsimilarsituationquery.json";
        JSONObject situationrecord = hm_events.runMSearchQueryGetSituation(situationMessage, jwtToken, similarFileJson, 86400000);
        int similarrecord = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (similarrecord <= 0) {
            return;
        }
        
        source_identifier = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets").getJSONObject(0).getJSONObject("top_hits#latest_doc").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("source_identifier");
        String _identifier = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets").getJSONObject(0).getJSONObject("top_hits#latest_doc").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
        
        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }

        List<String> similarSituationSummary = aiopsDeepRCA.getSituationGPTSummary(source_identifier);
        log.info("Similar Situation Summary: " + similarSituationSummary);

        log.info("Get the Code tool to validate the Incident resolution");
        List<String> BapTools = aiopsDeepRCA.getBapTools(source_identifier, _identifier);
        if (BapTools != null) {
            String bapString = BapTools.get(0);
            if (bapString == null || bapString.length() < 5) {
                log.error("bapstring failed");
            }else{
                JSONObject bapobject = new JSONObject(bapString);
                JSONArray bapTools = bapobject.getJSONArray("best_action_plan");
                for(int i = 0; i < bapTools.length(); i++) {
                    JSONObject bapToolObject = bapTools.getJSONObject(i);
                    String toolName = bapToolObject.getJSONArray("steps").getJSONObject(0).getString("tool");
                    if(toolName.equals("code tool")) {
                        String action = bapToolObject.getJSONArray("steps").getJSONObject(0).getString("action");
                        if(!action.contains("pod")) {
                            log.error("action failed");
                            softAssert.fail("action failed");
                        }
                        break;
                    }
                }
                
            }
        }
        softAssert.assertAll();
    }
    
}
