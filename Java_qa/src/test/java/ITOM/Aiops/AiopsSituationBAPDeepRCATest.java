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

public class AiopsSituationBAPDeepRCATest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationBAPDeepRCATest.class.getName());
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
    String eventClass = "NAGIOS_EV1";
    String jwtToken = null;
    int no_of_event_per_node = 1;
    String sheetName = "DeepRCATC";
    String situationjsonfile = "msearchsituationquery.json";
    private static final long situationTimeDiff = 300000;
    String situationConfigurationId = null;
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();
    
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Cleaning up after the test...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        try {
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            if (situationConfigurationId != null) {
                aiopsSituation.getSituationConfiguration(situationConfigurationId, 5, 5, 14400, 60, jwtToken);
            }
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
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
    
    @Test(description = "[DEEP RCA] Verify Situation summary for Multiservice situation.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15713", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15713_Verify_Situation_summary_for_Multiservice_situation(ITestContext context) throws Exception {
        
        int testcase_number = 1;
        log.info("Starting test: DRSQA_15713_Verify_Situation_summary_for_Multiservice_situation");
        if(jwtToken == null) {
            prerquirements();
        }
        
        /*/
        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        */
        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("Validate ml-situation-across-service-boundary enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled This TC cannot run");
            return;
        }

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);  
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
               
        
        log.info("Send the event to the Bhom");
        List<String> logData = new ArrayList<>();
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
            logData.add(msg);
        }
        
        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Wait for 1 minute before proceeding with Deep RCA so that situation is sync to ITSM");
        commonwait.waitForSometime(60000);

        log.info("get the situation identifier");

        Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
        if (situationIDAndSourceID == null) {
            Assert.fail("Get situation identifier and source identifier failed");
        }
        String situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
        String sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");
        
        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        

        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }


        JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String situationSummary = deepRCASummary.getString("situation_summary");

        log.info("Find sematic Similarity");
        String llmQuery = "provide me the recommendation for below event messages \n";
        llmQuery = llmQuery + String.join("\n", logData);
        double rate = aiopsDeepRCA.getDeepRCAScore(llmQuery, situationSummary);
        if (rate < 0.70) {
            log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
            softAssert.fail("Situation summary should be matching with the event messages");
        }
        
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    @Test(description = "[DEEP RCA] Verify Situation summary for similar situations with incident resolution.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15714", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15714_Verify_Situation_summary_for_similar_situations_with_incident_resolution(ITestContext context) throws Exception {
        String similarSituationJsonFile = "getSimilarSituationSearchString.json";
        log.info("Starting test: DRSQA_15714_Verify_Situation_summary_for_similar_situations_with_incident_resolution");
        if(jwtToken == null) {
            prerquirements();
        }

        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        String similarSituationSummary = "ML-TSOM - host-01-04 Memory > 90%";
        String modelName = "ML-TSOM";
        long similarSituationTimeDiff = 86400000;

        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("validate IA-PSR is enabled");
        if (!hm_events.getIncidentPolicyEnable(jwtToken)) {
            Assert.fail("IA-PSR is not enabled cannot run this test case");
        }
        
        JSONObject msquery = hm_events.runMSearchQueryGetSituation(similarSituationSummary, jwtToken,similarSituationJsonFile,similarSituationTimeDiff);
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord == 0){
            log.info("create a Situation and exit");
            int testcase_number = 2;
            String mlSheetName = "mlcheck";
            List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
           
            log.info("Check Service Model Exist");
            String[] bussness_service = row_data.get(0).get("Business Name").toString().split((","));
            
            aiopsApiCore.searchAndGetServiceDetails(bussness_service[0],jwtToken);
            
            log.info("Get init service health score");
            List<Map<String, String>> businessHealthList = new ArrayList<>();
            for(String service_name: bussness_service){
                Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
                aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
                commonwait.waitForSometime(5000);
                healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
                log.info("Health Score for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
                businessHealthList.add(healthScore);
            }

            String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));
            String[] ci_priority = row_data.get(0).get("Impact Priority").toString().split((","));
            String[] ci_severity = row_data.get(0).get("Impact Severity").toString().split((","));
            String randomStr = RandomStringUtils.randomNumeric(7);
            String msg;
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            int no_of_event_per_node = (int)Double.parseDouble(row_data.get(0).get("No of Event per Node").toString());
            int total_rec = ci_list.length;
            for(int i =0;i< total_rec;i+= 1){
                msg = ci_list[i] + " Memory > 90% " + randomStr;
                hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[i], msg, ci_severity[i], location, event_tag);
            }
            log.info("start timer to check how long it to take situation creation");
            long sepochTime = Instant.now().getEpochSecond();
            log.info("Waiting for situation creation...");

            Map<String, String> healthScore = businessHealthList.get(0);

            if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
            if (situationIDAndSourceID == null) {
                Assert.fail("Get situation identifier and source identifier failed");
            }
            String situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
            String sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");
            if (!aiopsDeepRCA.updateIncidentwithResolution(situationIdentifier, modelName)) {
                Assert.fail("Failed to update incident with resolution");
            }
            return;
            
        }

        
        log.info("get the situation identifier");
        JSONObject similarSituation = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets").getJSONObject(0).getJSONObject("top_hits#latest_doc");
        String situationIdentifier = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
        String sourceIdentifier = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("source_identifier");

        JSONObject soltvalue = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getJSONObject("_slots");
        Iterator<String> keys = soltvalue.keys();
        
        String fingerprint = "";
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject slotObject = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getJSONObject("_slots").getJSONObject(key);
            String slotName = slotObject.getString("value_name");
            String slotValue = slotObject.getString("value_string");
            if (slotName.equals("fingerprint")) {
                fingerprint = slotValue;
                break;
            }
        }

        log.info("Get Situation Event Messages");
        
        List<String> eventIds = aiopsApiCore.getEventIdsAssociatedWithSituation(situationIdentifier, jwtToken);
        List<String> logData = new ArrayList<>();
        List<String> source_hostname = new ArrayList<>();
        for (String eventId : eventIds) {
            JSONObject eventMessage = new JSONObject(hm_events.eventDetails(eventId, jwtToken));
            logData.add(eventMessage.getJSONObject("response").getJSONObject("allslots").getString("msg"));
            source_hostname.add(eventMessage.getJSONObject("response").getJSONObject("allslots").getString("source_hostname"));
        }
        if (logData == null || logData.isEmpty()) {
            Assert.fail("No event messages found");
        }

        if (!aiopsDeepRCA.updatesimilarsituationIncidentwithResolution(fingerprint, sourceIdentifier)) {
            softAssert.fail("Failed to update similar situation incident with resolution");
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Run ranker");
        if(!aiopsDeepRCA.runSituationRanker()){
            Assert.fail("Run ranker failed");
        }
        

        log.info("create a similar sitation");
        for(int i = 0; i < source_hostname.size(); i++) {
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, source_hostname.get(i), logData.get(i), "CRITICAL", location, event_tag);
        }

        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }



        Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
        if (situationIDAndSourceID == null) {
            Assert.fail("Get situation identifier and source identifier failed");
        }
        situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
        sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");

        log.info("Wait for siutation to get saturated it take 5 minutes to get saturated");
        for (int j=0;j<21;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Saturation check attempt " + (j+1));
            if (aiopsApiCore.verifySituationSaturated(situationIdentifier,jwtToken)) {
                log.info("Situation saturated");
                break;
            }
            commonwait.waitForSometime(15000);
        }
        if (!aiopsDeepRCA.updateIncidentwithResolution(situationIdentifier, modelName)) {
            softAssert.fail("Failed to update incident with resolution");
        }

        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        
        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }
        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);

        JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String situationSummary = deepRCASummary.getString("situation_summary");

        log.info("Find sematic Similarity");
        String llmQuery = "provide me the recommendation for below event messages \n";
        llmQuery = llmQuery + String.join("\n", logData);
        double rate = aiopsDeepRCA.getDeepRCAScore(llmQuery, situationSummary);
        if (rate < 0.70) {
            log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
            softAssert.fail("Situation summary should be matching with the event messages");
        }
        
        log.info("get the Code tool to validate the Incident resolution");
        
        softAssert.assertAll();

    }
    
    @Test(description = "[BAP]: Verify BAP tools to verify steps are as per Incident resolution and validate Change tool",groups={"bap"})
    @SolqaDetails(authorName = "almahaja", JiraID = "DRSQA-13104", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_13104_BAP_verify_steps_are_as_per_Incident_resolution_worklog_and_validate_Change_tool_similar_situation(ITestContext context) throws Exception {
        String similarSituationJsonFile = "getSimilarSituationSearchString.json";
        log.info("Starting test: DRSQA_15714_Verify_Situation_summary_for_similar_situations_with_incident_resolution");
        if(jwtToken == null) {
            prerquirements();
        }

        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        String similarSituationSummary = "ML-TSOM - host-01-04 patrol Agent utilization > 90%";
        String modelName = "ML-TSOM";
        long similarSituationTimeDiff = 86400000;

        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("validate IA-PSR is enabled");
        if (!hm_events.getIncidentPolicyEnable(jwtToken)) {
            Assert.fail("IA-PSR is not enabled cannot run this test case");
        }
        int change_testcase_number = 13;
        String change_sheetName =  "DeepRCATC";
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(change_testcase_number, change_sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        Map<String, String> serviceHealthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        
        String title = logagentTC.get(0).get("Change Title").toString() + " " + RandomStringUtils.randomNumeric(5);
        String description = logagentTC.get(0).get("Change Description").toString() + " " + RandomStringUtils.randomNumeric(5);
        
        JSONArray updatedChangeList = aiopsApiCore.getChangeAssociatedWithService(serviceHealthScore.get("key"),serviceHealthScore.get("id"),jwtToken);
        if (updatedChangeList == null || updatedChangeList.length() == 0) {
            aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
        }
        else {
            long scheduletime = Long.MIN_VALUE;
            for (int i = 0; i < updatedChangeList.length(); i++) {
                JSONObject src = updatedChangeList.getJSONObject(i).optJSONObject("_source");
                if (src != null && src.has("scheduled_start_date")) {
                    scheduletime = Math.max(scheduletime, src.getLong("scheduled_start_date"));
                }
            }
            long currenttime = hm_events.getBhomServerTime(jwtToken);
            log.info("Scheduled time: " + scheduletime + " Current time: " + currenttime);
            //get 1 day time in milliseconds
            long oneDay = 24 * 60 * 60 * 1000;
            if (scheduletime < (currenttime - oneDay)) {
                aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
            }
        }

        JSONObject msquery = hm_events.runMSearchQueryGetSituation(similarSituationSummary, jwtToken,similarSituationJsonFile,similarSituationTimeDiff);
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord == 0){
            log.info("create a Situation and exit");
            int testcase_number = 2;
            String mlSheetName = "mlcheck";
            List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
           
            log.info("Check Service Model Exist");
            String[] bussness_service = row_data.get(0).get("Business Name").toString().split((","));
            
            aiopsApiCore.searchAndGetServiceDetails(bussness_service[0],jwtToken);
            
            log.info("Get init service health score");
            List<Map<String, String>> businessHealthList = new ArrayList<>();
            for(String service_name: bussness_service){
                Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
                aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
                commonwait.waitForSometime(5000);
                healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
                log.info("Health Score for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
                businessHealthList.add(healthScore);
            }

            String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));
            String[] ci_priority = row_data.get(0).get("Impact Priority").toString().split((","));
            String[] ci_severity = row_data.get(0).get("Impact Severity").toString().split((","));
            String randomStr = RandomStringUtils.randomNumeric(7);
            String msg;
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            int no_of_event_per_node = (int)Double.parseDouble(row_data.get(0).get("No of Event per Node").toString());
            int total_rec = ci_list.length;
            for(int i =0;i< total_rec;i+= 1){
                msg = ci_list[i] + " patrol Agent utilization > 90% " + randomStr;
                hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[i], msg, ci_severity[i], location, event_tag);
            }
            log.info("start timer to check how long it to take situation creation");
            long sepochTime = Instant.now().getEpochSecond();
            log.info("Waiting for situation creation...");

            Map<String, String> healthScore = businessHealthList.get(0);

            if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
            if (situationIDAndSourceID == null) {
                Assert.fail("Get situation identifier and source identifier failed");
            }
            String situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
            String sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");
            if (!aiopsDeepRCA.updateIncidentwithResolution(situationIdentifier, modelName)) {
                Assert.fail("Failed to update incident with resolution");
            }
            log.info("Update Work logs Notes");
            String eventDetails = hm_events.eventDetails(situationIdentifier, this.jwtToken);
            JSONObject eventDetailsJson = new JSONObject(eventDetails);
            String incidentid = eventDetailsJson.getJSONObject("response").getJSONObject("allslots").getString("incident_id");
            if (incidentid.length() < 3) {
                Assert.fail("Failed to update incident with resolution");
            }
            
            String workLogDescription = logagentTC.get(0).get("Worklog Notes").toString();
            String workLogTitle = workLogDescription.substring(0,50);
            aiopsDeepRCA.updateIncidentWorkLog(incidentid,workLogTitle, workLogDescription);
            return;
            
        }

        
        log.info("get the situation identifier");
        JSONObject similarSituation = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets").getJSONObject(0).getJSONObject("top_hits#latest_doc");
        String situationIdentifier = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
        String sourceIdentifier = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("source_identifier");

        JSONObject soltvalue = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getJSONObject("_slots");
        Iterator<String> keys = soltvalue.keys();
        
        String fingerprint = "";
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject slotObject = similarSituation.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getJSONObject("_slots").getJSONObject(key);
            String slotName = slotObject.getString("value_name");
            String slotValue = slotObject.getString("value_string");
            if (slotName.equals("fingerprint")) {
                fingerprint = slotValue;
                break;
            }
        }

        log.info("Get Situation Event Messages");
        
        List<String> eventIds = aiopsApiCore.getEventIdsAssociatedWithSituation(situationIdentifier, jwtToken);
        List<String> logData = new ArrayList<>();
        List<String> source_hostname = new ArrayList<>();
        for (String eventId : eventIds) {
            JSONObject eventMessage = new JSONObject(hm_events.eventDetails(eventId, jwtToken));
            logData.add(eventMessage.getJSONObject("response").getJSONObject("allslots").getString("msg"));
            source_hostname.add(eventMessage.getJSONObject("response").getJSONObject("allslots").getString("source_hostname"));
        }
        if (logData == null || logData.isEmpty()) {
            Assert.fail("No event messages found");
        }

        if (!aiopsDeepRCA.updatesimilarsituationIncidentwithResolution(fingerprint, sourceIdentifier)) {
            softAssert.fail("Failed to update similar situation incident with resolution");
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Run ranker");
        if(!aiopsDeepRCA.runSituationRanker()){
            Assert.fail("Run ranker failed");
        }
        

        log.info("create a similar sitation");
        for(int i = 0; i < source_hostname.size(); i++) {
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, source_hostname.get(i), logData.get(i), "CRITICAL", location, event_tag);
        }

        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }



        Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
        if (situationIDAndSourceID == null) {
            Assert.fail("Get situation identifier and source identifier failed");
        }
        situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
        sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");

        log.info("Wait for siutation to get saturated it take 5 minutes to get saturated");
        for (int j=0;j<21;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Saturation check attempt " + (j+1));
            if (aiopsApiCore.verifySituationSaturated(situationIdentifier,jwtToken)) {
                log.info("Situation saturated");
                break;
            }
            commonwait.waitForSometime(15000);
        }
        if (!aiopsDeepRCA.updateIncidentwithResolution(situationIdentifier, modelName)) {
            softAssert.fail("Failed to update incident with resolution");
        }

        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        
        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }
        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);

        JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String situationSummary = deepRCASummary.getString("situation_summary");

        log.info("Find sematic Similarity");
        String llmQuery = "provide me the recommendation for below event messages \n";
        llmQuery = llmQuery + String.join("\n", logData);
        double rate = aiopsDeepRCA.getDeepRCAScore(llmQuery, situationSummary);
        if (rate < 0.70) {
            log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
            softAssert.fail("Situation summary should be matching with the event messages");
        }
        
        log.info("get the Code tool to validate the Incident resolution");
        
        softAssert.assertAll();

    }

    @Test(description = "[Deep RCA] Verify Summary output when change/logs are not associated to the situation.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15718", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15718_Verify_Summary_output_when_change_logs_are_not_associated_to_the_situation(ITestContext context) throws Exception {
        
        int testcase_number = 6;
        log.info("Starting test: DRSQA_15718_Verify_Summary_output_when_change_logs_are_not_associated_to_the_situation");
        if(jwtToken == null) {
            prerquirements();
        }

        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        /*/
        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        */
        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("Validate ml-situation-across-service-boundary enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled This TC cannot run");
            return;
        }

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);

        
        
        log.info("Send the event to the Bhom");
        List<String> logData = new ArrayList<>();
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
            logData.add(msg);
        }
        
        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Wait for 1 minute before proceeding with Deep RCA so that situation is sync to ITSM");
        commonwait.waitForSometime(60000);

        log.info("get the situation identifier");
        Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
        if (situationIDAndSourceID == null) {
            Assert.fail("Get situation identifier and source identifier failed");
        }
        String situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
        String sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");
        
        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        

        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }

       
        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("change tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                }
            }
            if (actionTool.equals("log tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
            }
        }

        log.info("Get Deep RCA Summary");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String situationSummary = deepRCASummary.getString("situation_summary");

        log.info("Find sematic Similarity");
        String llmQuery = "provide me the recommendation for below event messages \n";
        llmQuery = llmQuery + String.join("\n", logData);
        double rate = aiopsDeepRCA.getDeepRCAScore(llmQuery, situationSummary);
        if (rate < 0.70) {
            log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
            softAssert.fail("Situation summary should be matching with the event messages");
        }
        
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    @Test(description = "[DEEP RCA] Update Situation by sending events and check Situation summary is getting updated if DeepRCA triggered before saturation.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15716", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15716_Update_Situation_by_sending_events_and_check_Situation_summary_is_getting_updated_if_DeepRCA_triggered_before_saturation(ITestContext context) throws Exception {
        int testcase_number = 8;
        log.info("Starting test: DRSQA_15716_Update_Situation_by_sending_events_and_check_Situation_summary_is_getting_updated_if_DeepRCA_triggered_before_saturation");
        if(jwtToken == null) {
            prerquirements();
        }

        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        
        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("Validate ml-situation-across-service-boundary enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled This TC cannot run");
            return;
        }

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        situationConfigurationId = aiopsSituation.getSituationConfigurationId();

        aiopsSituation.getSituationConfiguration(situationConfigurationId, 15, 15, 14400, 60, jwtToken);
        
        log.info("Send the event to the Bhom");
        List<String> logData = new ArrayList<>();
        int event_send_count = 1;
        for (event_send_count = 1; event_send_count <= 2; event_send_count++) {
            String titeName = "Event " + String.valueOf(event_send_count);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[event_send_count-1].trim(), msg, "MAJOR", location, event_tag);
            logData.add(msg);
        }
        
        log.info("Wait for the situation");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Wait for 10 seconds before proceeding with Deep RCA so that situation is sync to ITSM");
        commonwait.waitForSometime(10000);

        log.info("get the situation identifier");
        Map<String, String> situationIDAndSourceID = aiopsDeepRCA.getSituationIDAndSourceID(healthScore.get("key"));
        if (situationIDAndSourceID == null) {
            Assert.fail("Get situation identifier and source identifier failed");
        }
        String situationIdentifier = situationIDAndSourceID.get("situationIdentifier");
        String sourceIdentifier = situationIDAndSourceID.get("sourceIdentifier");
        
        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        

        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }

       
        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("change tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                }
            }
            if (actionTool.equals("log tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
            }
        }

        log.info("Get Deep RCA Summary");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String situationSummary = deepRCASummary.getString("situation_summary");

       for (int i = event_send_count; i <= 4; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
            logData.add(msg);
        }

        log.info("Wait for event to get added into situation");
        aiopsSituation.waitForNewEventAddedToSituation(event_send_count, situationIdentifier, jwtToken);
        log.info("Wait for 5 seconds before proceeding with Deep RCA so that situation is sync to ITSM");
        commonwait.waitForSometime(5000);

        log.info("Get Deep RCA Summary");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);
        if (deepRCAResult == null) {
            Assert.fail("Trigger and return deep RCA result failed");
        }
        log.info("Wait for 10 seconds before proceeding with Deep RCA summary");
        commonwait.waitForSometime(10000);
        JSONObject newdeepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String oldSummaryNormalized = normalizeSummary(situationSummary);
        String newSummaryNormalized = normalizeSummary(newdeepRCASummary.getString("situation_summary"));

        int oldSummaryLength = oldSummaryNormalized.length();
        int newSummaryLength = newSummaryNormalized.length();

        if (oldSummaryLength == newSummaryLength) {
            if (!(oldSummaryNormalized.equals(newSummaryNormalized) )) {
                // Expected: summaries should match; if they differ, fail
                softAssert.fail("Situation summary is not getting updated");
                log.error("Situation summary is not getting updated");
                log.error("Old situation summary: " + oldSummaryNormalized);
                log.error("New situation summary: " + newSummaryNormalized);
            }
        }
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    private String normalizeSummary(String summary) {
        if (summary == null) {
            return "";
        }
        return summary.trim().replaceAll("\\s+", " ");
    }

}
