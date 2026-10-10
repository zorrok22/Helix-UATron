package ITOM.Aiops;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;

import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AiopsSituationpriorityTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationpriorityTest.class.getName());
    public static final String configuration_end_point = "/aiops-config/api/v1.0/situation_configurations/search";
    CommonMethods commonmethod = new CommonMethods();
    private static final int event_cnt = 1;
    private static final long situationTimeDiff = 300000;
    public static final String aiopssituationconfigUrl = "/aiops-config/api/v1.0/situation_configurations/";
    public static final String event_tag = "";
    CommonWaits commonWait = new CommonWaits();    
    SoftAssert softAssert = null;
    LogAnalyticsPage logAnalyticsPage = null;
    String location = "Pune";
    String mlSheetName = "logAgentLogs";
    HMEvents hm_events = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    String eventClass = "NAGIOS_EV1";
    String jwtToken = null;
    int no_of_event_per_node = 1;
    String situationId = null;
    ExcelReaderForMLSituation excelReaderForMLSituation;

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Cleaning up after the test...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        if(situationId != null){
            try {
                aiopsSituation.updateSituationConfigurationsituationseverity("root-cause-severity", situationId);
            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            hm_events.closeAllEventByClass(eventClass, jwtToken);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @BeforeMethod(alwaysRun = true)
    public void prerquirements() {
        log.info("Setting up the test...");
        jwtToken = commonmethod.refreshJwtToken(null);
        hm_events = new HMEvents();
        aiopsApiCore = new Aiops();
        aiopsSituation = new AiopsSituation();
        softAssert = new SoftAssert();
        logAnalyticsPage = new LogAnalyticsPage();
        excelReaderForMLSituation = new ExcelReaderForMLSituation();

    }

    /**
     * Retrieves the first situation ID from the situation configurations.
     */

    @Test(description = "[Situation Priority] Verify situation priority inheritance from highest severity events", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "16329", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_16329_Verify_Situation_Priority_Inheritance_from_Highest_Severity_Events(ITestContext context)
            throws IOException, InterruptedException {
        softAssert = new SoftAssert();
        int testcase_number = 1;
        String mlSheetName = "pca";
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled; skipping test as passed.");
            return;
        }
        String situationjsonfile = "msearchsituationquery.json";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String[] host_list = row_data.get(0).get("Impact Element").toString().split(",");
        String serviceName = row_data.get(0).get("Business Name").toString();
        situationId = aiopsSituation.getSituationId();
        log.info("Situation ID: " + situationId);
        boolean updateSituationSeverity = false;
        try {
            updateSituationSeverity = aiopsSituation.updateSituationConfigurationsituationseverity("priority-from-highest-severity", situationId);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(!updateSituationSeverity){
            Assert.fail("Failed to update situation severity.");
        }

        
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        String serviceKey = healthScore.get("key");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);

        // Helper lambdas
        String targetHost = host_list[0];
        java.util.function.BiConsumer<String, String> sendEvent = (severity, priority) -> {
            String msg = "Highest Severity and Priority value " + RandomStringUtils.randomAlphanumeric(6);
            String response = hm_events.streamRestApiEvents(eventClass, 1, jwtToken, targetHost, msg, severity, location, event_tag, priority);
            log.info("Sent event via streamRestApiEventsWithPriority severity=" + severity + " priority=" + priority + " host=" + targetHost + " resp=" + response);
        };

        java.util.function.BiFunction<String, Integer, JSONObject> waitAndFetchSituation = (svcKey, timeoutMs) -> {
            if (!aiopsApiCore.waitForSituationCreationMsearch(svcKey, jwtToken, timeoutMs)) {
                softAssert.fail("Situation did not get created for service key " + svcKey);
                return null;
            }
            JSONObject msquery = hm_events.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationjsonfile, situationTimeDiff);
            if (msquery == null){
                Assert.fail("Fail to get situation from msearch query.");
            }
            int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord > 0) {
                JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
                for (Object jsonobj: jsonarray) {
                    JSONObject sit_obj =  (JSONObject)jsonobj;
                    String EventClass = sit_obj.getJSONObject("_source").getString("class");
                    if(!EventClass.equals("Situation")){
                        continue;
                    }
                    return sit_obj.getJSONObject("_source");
                }
            }
            return null;
        };

        // Scenario 1
        sendEvent.accept("MAJOR", "PRIORITY_1");
        sendEvent.accept("CRITICAL", "PRIORITY_3");
        JSONObject situation = waitAndFetchSituation.apply(serviceKey, 180000);
        if (situation != null) {
            String sitSeverity = situation.optString("severity", "");
            String sitPriority = situation.optString("priority", "");
            log.info("Situation severity=" + sitSeverity + " priority=" + sitPriority);
            softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
            softAssert.assertEquals(sitPriority, "PRIORITY_3", "Situation priority should be P3");
        }
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);
        // Scenario 2
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        sendEvent.accept("CRITICAL", "PRIORITY_1");
        sendEvent.accept("CRITICAL", "PRIORITY_1");
        situation = waitAndFetchSituation.apply(serviceKey, 180000);
        if (situation != null) {
            String sitSeverity = situation.optString("severity", "");
            String sitPriority = situation.optString("priority", "");
            log.info("Situation severity=" + sitSeverity + " priority=" + sitPriority);
            softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
            softAssert.assertEquals(sitPriority, "PRIORITY_2", "Situation priority should be P2");
        }
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);

        // Scenario 3
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        sendEvent.accept("MAJOR", "PRIORITY_2");
        sendEvent.accept("MINOR", "PRIORITY_2");
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        situation = waitAndFetchSituation.apply(serviceKey, 180000);
        if (situation != null) {
            String sitSeverity = situation.optString("severity", "");
            String sitPriority = situation.optString("priority", "");
            log.info("Situation severity=" + sitSeverity + " priority=" + sitPriority);
            softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
            softAssert.assertEquals(sitPriority, "PRIORITY_2", "Situation priority should be P2");
        }

        softAssert.assertAll();

    }

    @Test(description = "[Situation Priority] Verify situation priority dynamically updates based on highest severity event", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "16328", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_16328_Verify_Situation_Priority_Dynamically_Updates_Based_on_Highest_Severity_Event(ITestContext context)
            throws IOException, InterruptedException {
        softAssert = new SoftAssert();
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled; skipping test as passed.");
            return;
        }
        int testcase_number = 1;
        String mlSheetName = "pca";
        String situationjsonfile = "msearchsituationquery.json";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);   
        String[] host_list = row_data.get(0).get("Impact Element").toString().split(",");
        String serviceName = row_data.get(0).get("Business Name").toString();
        situationId = aiopsSituation.getSituationId();
        log.info("Situation ID: " + situationId);
        boolean updateSituationSeverity = false;
        try {
            updateSituationSeverity = aiopsSituation.updateSituationConfigurationsituationseverity("priority-from-highest-severity", situationId);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(!updateSituationSeverity){
            Assert.fail("Failed to update situation severity.");
        }

        
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        String serviceKey = healthScore.get("key");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);
        
        // Helper lambdas
        String targetHost = host_list[0];
        java.util.function.BiConsumer<String, String> sendEvent = (severity, priority) -> {
            String msg = "Highest Severity and Priority value " + RandomStringUtils.randomAlphanumeric(6);
            String response = hm_events.streamRestApiEvents(eventClass, 1, jwtToken, targetHost, msg, severity, location, event_tag, priority);
            log.info("Sent event via streamRestApiEventsWithPriority severity=" + severity + " priority=" + priority + " host=" + targetHost + " resp=" + response);
        };

        java.util.function.BiFunction<String, Integer, JSONObject> waitAndFetchSituation = (svcKey, timeoutMs) -> {
            if (!aiopsApiCore.waitForSituationCreationMsearch(svcKey, jwtToken, timeoutMs)) {
                softAssert.fail("Situation did not get created for service key " + svcKey);
                return null;
            }
            JSONObject msquery = hm_events.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationjsonfile, situationTimeDiff);
            if (msquery == null){
                Assert.fail("Fail to get situation from msearch query.");
            }
            int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord > 0) {
                JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
                for (Object jsonobj: jsonarray) {
                    JSONObject sit_obj =  (JSONObject)jsonobj;
                    String EventClass = sit_obj.getJSONObject("_source").getString("class");
                    if(!EventClass.equals("Situation")){
                        continue;
                    }
                    return sit_obj.getJSONObject("_source");
                }
            }
            return null;
        };

        // Scenario 1
        sendEvent.accept("MAJOR", "PRIORITY_4");
        sendEvent.accept("CRITICAL", "PRIORITY_3");
        sendEvent.accept("CRITICAL", "PRIORITY_3");
        JSONObject situation = waitAndFetchSituation.apply(serviceKey, 180000);
        String situationIdentifier = situation.getString("_identifier");
        if (situation != null) {
            String sitSeverity = situation.optString("severity", "");
            String sitPriority = situation.optString("priority", "");
            situationIdentifier = situation.getString("_identifier");
            log.info("Situation severity=" + sitSeverity + " priority=" + sitPriority);
            softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
            softAssert.assertEquals(sitPriority, "PRIORITY_3", "Situation priority should be P3");
        }

        log.info("Update the sitation by sending new event with priority 1");
        sendEvent.accept("CRITICAL", "PRIORITY_1");
        log.info("Wait for situation to get updated with new event with priority 1");
        int noOfEventsSent = 4;
        aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);
        String eventDetails = hm_events.eventDetails(situationIdentifier, jwtToken);
        JSONObject jsonObject = new JSONObject(eventDetails);
        String sitSeverity = jsonObject.getJSONObject("response").getJSONObject("allslots").get("severity").toString();
        String sitPriority = jsonObject.getJSONObject("response").getJSONObject("allslots").get("priority").toString();
        softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
        softAssert.assertEquals(sitPriority, "PRIORITY_1", "Situation priority should be P1");
        

        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);
        // Scenario 2
        sendEvent.accept("MAJOR", "PRIORITY_2");
        sendEvent.accept("MAJOR", "PRIORITY_3");
        sendEvent.accept("MAJOR", "PRIORITY_3");
        
        situation = waitAndFetchSituation.apply(serviceKey, 180000);
        situationIdentifier = situation.getString("_identifier");
        if (situation != null) {
            sitSeverity = situation.optString("severity", "");
            sitPriority = situation.optString("priority", "");
            log.info("Situation severity=" + sitSeverity + " priority=" + sitPriority);
            softAssert.assertEquals(sitSeverity, "MAJOR", "Situation severity should be CRITICAL");
            softAssert.assertEquals(sitPriority, "PRIORITY_2", "Situation priority should be P2");
        }

        log.info("Update the sitation by sending new event with priority 1");
        sendEvent.accept("MINOR", "PRIORITY_1");
        sendEvent.accept("MINOR", "PRIORITY_3");
        log.info("Wait for situation to get updated with new event with priority 1");
        noOfEventsSent = 5;
        aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);
        eventDetails = hm_events.eventDetails(situationIdentifier, jwtToken);
        jsonObject = new JSONObject(eventDetails);
        sitSeverity = jsonObject.getJSONObject("response").getJSONObject("allslots").get("severity").toString();
        sitPriority = jsonObject.getJSONObject("response").getJSONObject("allslots").get("priority").toString();
        softAssert.assertEquals(sitSeverity, "MAJOR", "Situation severity should be CRITICAL");
        softAssert.assertEquals(sitPriority, "PRIORITY_2", "Situation priority should be P1");

        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);

        // Scenario 3
        sendEvent.accept("MAJOR", "PRIORITY_4");
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        
        situation = waitAndFetchSituation.apply(serviceKey, 180000);
        situationIdentifier = situation.getString("_identifier");
        if (situation != null) {
            sitSeverity = situation.optString("severity", "");
            sitPriority = situation.optString("priority", "");
            log.info("Situation severity=" + sitSeverity + " priority=" + sitPriority);
            softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
            softAssert.assertEquals(sitPriority, "PRIORITY_2", "Situation priority should be P2");
        }

        log.info("Update the sitation by sending new event with priority 1");
        sendEvent.accept("CRITICAL", "PRIORITY_3");
        sendEvent.accept("CRITICAL", "PRIORITY_2");
        log.info("Wait for situation to get updated with new event with priority 1");
        noOfEventsSent = 5;
        aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);
        eventDetails = hm_events.eventDetails(situationIdentifier, jwtToken);
        jsonObject = new JSONObject(eventDetails);
        sitSeverity = jsonObject.getJSONObject("response").getJSONObject("allslots").get("severity").toString();
        sitPriority = jsonObject.getJSONObject("response").getJSONObject("allslots").get("priority").toString();
        softAssert.assertEquals(sitSeverity, "CRITICAL", "Situation severity should be CRITICAL");
        softAssert.assertEquals(sitPriority, "PRIORITY_2", "Situation priority should be P1");

        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);

        softAssert.assertAll();

    }

}
