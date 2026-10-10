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

import java.util.ArrayList;
import java.util.List;

import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import util.*;

import java.io.File;
import java.util.Map;

public class AiopsSituationDeepRCAJiraTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationDeepRCAJiraTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String base_url = config.get("ADE_PLATFORM_URL");

    private static final int event_cnt = 1;
    private static final long situationTimeDiff = 300000;
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
    private static final String jiraTicketNumber = "https://jira.example.com/browse/SSR-21639";
    private static final String situationMessage = "Could not load plugin: ChunkLoadError";
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
    
    @Test(description = "[DEEP RCA] Verify BAP with Jira connector when Incident resolution is also updated along with jira id", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15715", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15715_Verify_BAP_with_Jira_connector_when_Incident_resolution_is_also_updated_along_with_jira_id(ITestContext context)
            throws Exception {

        log.info("Check if Deep RCA feature is enabled");
        if (!aiopsDeepRCA.isDeepRCAFeatureEnabled()) {
            log.info("Deep RCA feature is not enabled; skipping test as passed.");
            return;
        }

        if(!hm_events.getIncidentPolicyEnable(jwtToken)){
            Assert.fail("IA PSR is not enabled cannot run this test case");
        }

        log.info("Run ranker");
        log.info("If the Lot of situation is present, Ranger will take longer time, which result in Gateway Timeout.");
        aiopsDeepRCA.runSituationRanker();
        
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
        String source_identifier = "";
        String situationIdentifier = "";
        Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
        if (situationIDAndSourceID == null) {
            Assert.fail("Get situation identifier and source identifier failed");
        }
        situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
        source_identifier = situationIDAndSourceID.get("sourceIdentifier");

        log.info("Wait for siutation to get saturated it take 5 minutes to get saturated");
        for (int j=0;j<21;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Saturation check attempt " + (j+1));
            if (aiopsApiCore.verifySituationSaturated(situationIdentifier,jwtToken)) {
                log.info("Situation saturated");
                break;
            }
            commonWait.waitForSometime(15000);
        }

        log.info("Update Incident resolution and vendor notes in Jira");
        String incidentId = "";
        source_identifier = "";
        situationIdentifier = "";
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
        

        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(source_identifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }

        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("code tool")) {

                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Tool output: " + tooloutput);
                if(!tooloutput.contains("pod")) {
                    softAssert.fail("Code tool output does not contain pod");
                }
            }
        }
        softAssert.assertAll();
        
    }

    
}
