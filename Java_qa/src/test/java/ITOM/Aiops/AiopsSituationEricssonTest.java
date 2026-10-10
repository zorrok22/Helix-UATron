package ITOM.Aiops;
import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pages.ITOM.Aiops.ADESituationHelixGPT;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import org.json.JSONArray;
import util.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static pages.ITOM.Aiops.ADESituationHelixGPT.openSituationHelixGPT;

public class AiopsSituationEricssonTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationEricssonTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = new CommonMethods();
    public static final String event_tag = "";
    public static final String logInsightEndPoint = "/logs-service/api/v1.0/logs/log-insights/summary/";
    public static final String aiopsRunRanker = "/situation-best-action-recommender/api/v1.0/generate_ranker";
    public static final String helixGPTQueryendpoint = "/situation-best-action-recommender/api/v1.0/pre-canned-question";
    CommonWaits commonwait = new CommonWaits();
    LogAnalyticsPage logAnalyticsPage = null;
    String location = "Pune";
    String mlSheetName = "logAgentLogs";
    HMEvents hm_events = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    SoftAssert softAssert = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    SemanticSimilarityONNX similarityCalculator = null;
    String eventClass = "NAGIOS_EV1";
    String jwtToken = null;
    int no_of_event_per_node = 1;
    String sheetName = "mlcheck";
    String situationjsonfile = "msearchsituationquery.json";
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();
        
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Cleaning up after the test...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
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
        aiopsDeepRCA = new AiopsDeepRCA();
        logAnalyticsPage = new LogAnalyticsPage();
        try {
            similarityCalculator = new SemanticSimilarityONNX();
        } catch (OrtException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    }

    
    @Test(description = "[Ericsson] : Provide way to exclude specific events from situation irrespective of event severity and service enrichment", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "DRSQA-12531", Products = {Product.Service_Monitoring})
    public void DRSQA_12531_ExcludeSpecificEventsFromSituationIrrespectiveEventSeverity(ITestContext context) throws Exception {
        log.info("Starting test: DRSQA_15272_Verify_when_Change_and_logs_both_are_participating_but_change_is_not_a_RC_for_a_situation");
        if(jwtToken == null) {
            prerquirements();
        }
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled; skipping test as passed.");
            return;
        }
        int testcase_number = 5;
        int log_Testcase = 1;
        String eventSeverity = "MINOR";
        int eventcount = 0;
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Business Name").toString();
        String[] host_list = logagentTC.get(0).get("Impact Element").toString().split(",");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        for(String host : host_list) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "CPU utilization of " + host + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, 1, jwtToken, host, msg, eventSeverity, location, event_tag);
            eventcount++;
        }

        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Get situation identifier for service " + serviceName);
        JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
        String situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        String summary = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        if(!summary.contains("CPU utilization")) {
            softAssert.fail("Summary does not contain CPU utilization");
        }
        log.info("Send event with tag priority-causal-root");
        eventSeverity = "CRITICAL";
        for(String host : host_list) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + host + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, 1, jwtToken, host, msg, eventSeverity, location, "priority-causal-root");
            eventcount++;
        }

        log.info("Wait for event to get added into situation");
        aiopsSituation.waitForNewEventAddedToSituation(eventcount, situationIdentifier, jwtToken);

        log.info("get Event details");
        String eventDetails = hm_events.eventDetails(situationIdentifier, jwtToken);
        JSONObject eventDetailsObject = new JSONObject(eventDetails);
        String eventSummary = eventDetailsObject.getJSONObject("response").getJSONObject("allslots").getString("msg");
        if(!eventSummary.contains("Memory utilization")) {
            softAssert.fail("Event summary does not contain Memory utilization");
        }
        log.info("Get situation summary");

        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    @Test(description = "[Ericsson] : Provide way to exclude specific events from situation irrespective of event severity and service enrichment", groups = {"Regression", "PMDemo", "PreProd", "AIOPS"})
    @SolqaDetails(authorName = "Haris", JiraID = "14359", Products = {Product.Service_Monitoring})
    public void DRSQA_14359_Verify_Situation_root_cause_should_be_selected_based_on_tags_with_the_value_priority_causal_root_when_all_events_occur_on_a_single_node(ITestContext context) throws Exception {

        if(jwtToken == null) {
            prerquirements();
        }
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled; skipping test as passed.");
            return;
        }
        int testcase_number = 5;
        String eventSeverity = "MINOR";
        int eventcount = 0;
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Business Name").toString();
        String[] host_list = logagentTC.get(0).get("Impact Element").toString().split(",");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        for(String host : host_list) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "CPU utilization of " + host + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, 1, jwtToken, host, msg, eventSeverity, location, event_tag);
            eventcount++;
        }
        log.info("Total Event without tag" + eventcount);
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        String msg = "CPU utilization of " + host_list[0] + " exceed threshold of 80% " + randomStr;
        hm_events.streamRestApiEvents(eventClass, 1, jwtToken, host_list[0], msg, eventSeverity, location, SolQAConstants.EXCLUDE_FROM_SITUATION);
        eventcount++;

        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Get situation identifier for service " + serviceName);
        JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
        String situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        log.info("Get event ids associated with situation " + situationIdentifier);
        List<String> eventIds = aiopsApiCore.getEventIdsAssociatedWithSituation(situationIdentifier, jwtToken);
        log.info("Event in Situation " + eventIds.size() + " Expected Event " + (eventcount - 1));
        log.info("Except for 1 event which has tag rest of the event should be part of situation");
        if ((eventcount - 1) < eventIds.size()) {
            softAssert.fail("Situation has excluded event");
        }
        softAssert.assertAll();

    }
}
