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

public class AiopsSituationBAPLogAgentTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationBAPLogAgentTest.class.getName());
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
    String sheetName = "LogAgentTC";
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

    private Long getLongValueFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject == null || !jsonObject.has(fieldName) || jsonObject.isNull(fieldName)) {
            return null;
        }

        Object value = jsonObject.opt(fieldName);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            String textValue = ((String) value).trim();
            if (textValue.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(textValue);
            } catch (NumberFormatException ex) {
                Long epochMillis = parseDateTimeStringToEpoch(textValue);
                if (epochMillis != null) {
                    return epochMillis;
                }
                log.info("Skipping unsupported value for " + fieldName + ": " + textValue);
                return null;
            }
        }

        return null;
    }

    private Long parseDateTimeStringToEpoch(String dateValue) {
        List<DateTimeFormatter> supportedFormats = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
        );

        for (DateTimeFormatter formatter : supportedFormats) {
            try {
                return ZonedDateTime.parse(dateValue, formatter).toInstant().toEpochMilli();
            } catch (Exception ignored) {
                // Try next supported format.
            }
        }

        return null;
    }

    
    @Test(description = "[Log Agent] Verify when Change and logs both are participating but change is not a RC for a situation, and if we execute the log agent and find another RC in the logs, then summary and RC classification should be updated to logs only.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15272/15721", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15272_Verify_when_Change_and_logs_both_are_participating_but_change_is_not_a_RC_for_a_situation(ITestContext context) throws Exception {
        log.info("Starting test: DRSQA_15272_Verify_when_Change_and_logs_both_are_participating_but_change_is_not_a_RC_for_a_situation");
        if(jwtToken == null) {
            prerquirements();
        }
        int testcase_number = 1;
        int log_Testcase = 1;

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);  
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        String title = logagentTC.get(0).get("Change Title").toString() + " " + RandomStringUtils.randomNumeric(5);
        String description = logagentTC.get(0).get("Change Description").toString() + " " + RandomStringUtils.randomNumeric(5);

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);

        JSONArray updatedChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"),healthScore.get("id"),jwtToken);
        if (updatedChangeList == null || updatedChangeList.length() == 0) {
            String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
            if (changeid == null){
                softAssert.fail("Unable to create Change request");
            }
        }
        else {
            long scheduletime = Long.MIN_VALUE;
            for (int i = 0; i < updatedChangeList.length(); i++) {
                JSONObject src = updatedChangeList.getJSONObject(i).optJSONObject("_source");
                Long scheduledStartDate = getLongValueFromJson(src, "scheduled_start_date");
                if (scheduledStartDate != null) {
                    scheduletime = Math.max(scheduletime, scheduledStartDate);
                }
            }
            long currenttime = hm_events.getBhomServerTime(jwtToken);
            log.info("Scheduled time: " + scheduletime + " Current time: " + currenttime);
            //get 1 day time in milliseconds
            long oneDay = 24 * 60 * 60 * 1000;
            if (scheduletime < (currenttime - oneDay)) {
                String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
                if (changeid == null){
                    Assert.fail("Unable to create Change request");
                }
            }
        }

        log.info("Send Log related to Oracle Database on host " + logHost);
        
        
        List<String> logData = aiopsDeepRCA.sendLogAgentEventsFromExcel(log_Testcase, mlSheetName, logHost);
        if (logData.isEmpty() || logData == null) {
            Assert.fail("Log data is empty or null");
        }
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
        }
        
        log.info("Wait for the situation");
        healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
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
                if (!actionStatus.equals("NOT_APPLICABLE") && !actionStatus.equals("COMPLETE")) {
                    softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                }
            }
            if (actionTool.equals("log tool")) {
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
                JSONArray toolOutputs = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                if(toolOutputs.length() == 0) {
                    log.error("No tool outputs found" + actionObject.getJSONObject("step").toString());
                    softAssert.fail("No tool outputs found");
                    break;
                }
                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Find sematic Similarity");
                String llmQuery = "provide me the recommendation for the below issue reported by <KINDTYPE> monitoring agent \n";
                llmQuery = llmQuery + String.join("\n", logData);
                String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
                double rate = similarityCalculator.calculateSemanticSimilarity(tooloutput, llmOutput);
                if (rate < 0.70) {
                    log.info("Tool output: " + tooloutput);
                    log.info("BAP Summary: " + llmOutput);
                    log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                }
            }
        }
        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);
        
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    
    @Test(description = "[Log Agent] Verify when nodes are topologically connected and the RC is Host previously, but in logs, a crash has been observed in another connected host; the RC should be changed as per the logs entry.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15269", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15269_Verify_when_nodes_are_topologically_connected_and_the_RC_is_Host_previously_but_in_logs_a_crash_has_been_observed_in_another_connected_host_the_RC_should_be_changed_as_per_the_logs_entry(ITestContext context) throws Exception {
        int testcase_number = 2;
        int log_Testcase = 1;
        log.info("Starting test: DRSQA_15269_Verify_when_nodes_are_topologically_connected_and_the_RC_is_Host_previously_but_in_logs_a_crash_has_been_observed_in_another_connected_host_the_RC_should_be_changed_as_per_the_logs_entry");
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

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        String title = logagentTC.get(0).get("Change Title").toString() + " " + RandomStringUtils.randomNumeric(5);
        String description = logagentTC.get(0).get("Change Description").toString() + " " + RandomStringUtils.randomNumeric(5);

        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        
        JSONArray updatedChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"),healthScore.get("id"),jwtToken);
        if (updatedChangeList == null || updatedChangeList.length() == 0) {
            String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
            if (changeid == null){
                Assert.fail("Unable to create Change request");
            }
        }
        else {
            long scheduletime = Long.MIN_VALUE;
            for (int i = 0; i < updatedChangeList.length(); i++) {
                JSONObject src = updatedChangeList.getJSONObject(i).optJSONObject("_source");
                Long scheduledStartDate = getLongValueFromJson(src, "scheduled_start_date");
                if (scheduledStartDate != null) {
                    scheduletime = Math.max(scheduletime, scheduledStartDate);
                }
            }
            long currenttime = hm_events.getBhomServerTime(jwtToken);
            log.info("Scheduled time: " + scheduletime + " Current time: " + currenttime);
            //get 1 day time in milliseconds
            long oneDay = 24 * 60 * 60 * 1000;
            if (scheduletime < (currenttime - oneDay)) {
                String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
                if (changeid == null){
                    Assert.fail("Unable to create Change request");
                }
            }
        }
        log.info("Send Log related to Oracle Database on host " + logHost);
        
        
        List<String> logData = aiopsDeepRCA.sendLogAgentEventsFromExcel(log_Testcase, mlSheetName, logHost);
        if (logData.isEmpty() || logData == null) {
            Assert.fail("Log data is empty or null");
        }
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
        }
        
        log.info("Wait for the situation");
        healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
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
                if (!actionStatus.equals("NOT_APPLICABLE") && !actionStatus.equals("COMPLETE")) {
                    JSONArray situationRCA = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                    for (Object toolOutput : situationRCA) {
                        JSONObject toolOutputObject = (JSONObject) toolOutput;
                        String toolOutputStatus = toolOutputObject.getString("tool_output_problem_root_cause_status");
                        if(!(toolOutputStatus.equals("refuted") || toolOutputStatus.equals("no insight"))) {
                            log.error("Change tool is not applicable, status: " + actionStatus);
                            softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                            continue;
                        }
                        
                    }
                }else{
                        log.info("Change tool is applicable, no insight found");
                }
                continue;
            }
            
            if (actionTool.equals("log tool")) {
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
                JSONArray toolOutputs = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                if(toolOutputs.length() == 0) {
                    log.error("No tool outputs found" + actionObject.getJSONObject("step").toString());
                    softAssert.fail("No tool outputs found");
                    break;
                }
                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Find sematic Similarity");
                String llmQuery = "provide me the recommendation for the below issue reported by <KINDTYPE> monitoring agent \n";
                llmQuery = llmQuery + String.join("\n", logData);
                String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
                double rate = similarityCalculator.calculateSemanticSimilarity(tooloutput, llmOutput);
                if (rate < 0.70) {
                    log.info("Tool output: " + tooloutput);
                    log.info("BAP Summary: " + llmOutput);
                    log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                }
                JSONObject situationRCA = aiopsDeepRCA.getSituationRCA(situationIdentifier, jwtToken);
                if (situationRCA == null) {
                    softAssert.fail("Get situation RCA failed");
                }else{
                    JSONObject nodes = situationRCA.optJSONObject("nodes");
                    if (nodes == null || nodes.length() == 0) {
                        softAssert.fail("No nodes present in situation RCA response");
                        continue;
                    }

                    String topNodeId = null;
                    String topKind = null;
                    String topType = null;
                    double topScore = Double.NEGATIVE_INFINITY;

                    for (String nodeId : nodes.keySet()) {
                        JSONObject node = nodes.optJSONObject(nodeId);
                        if (node == null) {
                            continue;
                        }
                        double score = node.optDouble("rcaScore", Double.NEGATIVE_INFINITY);
                        if (score > topScore) {
                            topScore = score;
                            topNodeId = nodeId;
                            topKind = node.optString("kind", "");
                            topType = node.optString("type", "");
                        }
                    }

                    if (topNodeId == null) {
                        softAssert.fail("Unable to determine highest RCA score node");
                    } else {
                        log.info("Highest RCA node: " + topNodeId + " kind: " + topKind + " type: " + topType + " score: " + topScore);
                        if ("AI Tool".equalsIgnoreCase(topKind)) {
                            if (!"log tool".equalsIgnoreCase(topType)) {
                                softAssert.fail("Top RCA node kind 'AI Tool' should have type 'log tool' but was: " + topType);
                            }
                        }
                    }
                }
                
            }
        }
        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }


    @Test(description = "[Log Agent] Verify when no RC-related details match from the logs, the situation summary should not get changed and should as per the event messages.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15270", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15270_Verify_when_no_RC_related_details_match_from_the_logs_the_situation_summary_should_not_get_changed_and_should_as_per_the_event_messages(ITestContext context) throws Exception {
        int testcase_number = 3;
        int log_Testcase = 2;
        log.info("Starting test: DRSQA_15270_Verify_when_no_RC_related_details_match_from_the_logs_the_situation_summary_should_not_get_changed_and_should_as_per_the_event_messages");
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

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        String title = logagentTC.get(0).get("Change Title").toString() + " " + RandomStringUtils.randomNumeric(5);
        String description = logagentTC.get(0).get("Change Description").toString() + " " + RandomStringUtils.randomNumeric(5);
        
        jwtToken = commonmethod.refreshJwtToken(jwtToken);


        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);

        JSONArray updatedChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"),healthScore.get("id"),jwtToken);
        if (updatedChangeList == null || updatedChangeList.length() == 0) {
            String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
            if (changeid == null){
                Assert.fail("Unable to create Change request");
            }
        }
        else {
            long scheduletime = Long.MIN_VALUE;
            for (int i = 0; i < updatedChangeList.length(); i++) {
                JSONObject src = updatedChangeList.getJSONObject(i).optJSONObject("_source");
                Long scheduledStartDate = getLongValueFromJson(src, "scheduled_start_date");
                if (scheduledStartDate != null) {
                    scheduletime = Math.max(scheduletime, scheduledStartDate);
                }
            }
            long currenttime = hm_events.getBhomServerTime(jwtToken);
            log.info("Scheduled time: " + scheduletime + " Current time: " + currenttime);
            //get 1 day time in milliseconds
            long oneDay = 24 * 60 * 60 * 1000;
            if (scheduletime < (currenttime - oneDay)) {
                String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
                if (changeid == null){
                    Assert.fail("Unable to create Change request");
                }
            }
        }
        
        log.info("Send Log related to Oracle Database on host " + logHost);        
        List<String> logData = aiopsDeepRCA.sendLogAgentEventsFromExcel(log_Testcase, mlSheetName, logHost);
        if (logData.isEmpty() || logData == null) {
            Assert.fail("Log data is empty or null");
        }
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
        }
        
        log.info("Wait for the situation");
        healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
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
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("change tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    JSONArray situationRCA = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                    for (Object toolOutput : situationRCA) {
                        JSONObject toolOutputObject = (JSONObject) toolOutput;
                        String toolOutputStatus = toolOutputObject.getString("tool_output_problem_root_cause_status");
                        if(!toolOutputStatus.equals("refuted")) {
                            log.info("Change tool is not applicable, status: " + actionStatus);
                            softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                            continue;
                        }
                        
                    }
                }else{
                        log.info("Change tool is applicable, no insight found");
                }
                continue;
            }

            if (actionTool.equals("log tool")) {
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    continue;
                }
                JSONArray toolOutputs = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                if(toolOutputs.length() == 0) {
                    log.error("No tool outputs found" + actionObject.getJSONObject("step").toString());
                    softAssert.fail("No tool outputs found");
                    continue;
                }
                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Find sematic Similarity");
                String llmQuery = "provide me the recommendation for below logs \n";
                llmQuery = llmQuery + String.join("\n", logData);
                String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
                double rate = similarityCalculator.calculateSemanticSimilarity(tooloutput, llmOutput);
                if (rate < 0.70) {
                    log.info("Tool output: " + tooloutput);
                    log.info("BAP Summary: " + llmOutput);
                    log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                }
                
            }
            continue;
        }

        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);
        commonwait.waitForSometime(10000);
        JSONObject situationRCA = aiopsDeepRCA.getSituationRCA(situationIdentifier, jwtToken);
        if (situationRCA == null) {
            softAssert.fail("Get situation RCA failed");
        }else{
            JSONObject nodes = situationRCA.optJSONObject("nodes");
            if (nodes == null || nodes.length() == 0) {
                softAssert.fail("No nodes present in situation RCA response");
            }else{

                String topNodeId = null;
                String topKind = null;
                String topType = null;
                double topScore = Double.NEGATIVE_INFINITY;

                for (String nodeId : nodes.keySet()) {
                    JSONObject node = nodes.optJSONObject(nodeId);
                    if (node == null) {
                        continue;
                    }
                    double score = node.optDouble("rcaScore", Double.NEGATIVE_INFINITY);
                    if (score > topScore) {
                        topScore = score;
                        topNodeId = nodeId;
                        topKind = node.optString("kind", "");
                        topType = node.optString("type", "");
                    }
                }

                if (topNodeId == null) {
                    softAssert.fail("Unable to determine highest RCA score node");
                } else {
                    log.info("Highest RCA node: " + topNodeId + " kind: " + topKind + " type: " + topType + " score: " + topScore);
                    if ("AI Tool".equalsIgnoreCase(topKind)) {
                        softAssert.fail("Top RCA node kind is 'AI Tool' but should be Host: " + topType);
                    }
                }
            }
        }
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }    

    @Test(description = "[Log Agent] Verify when no logs are available for some exceptions, errors, or failure messages present in ClickHouse for the Causal node.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15271", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15271_Verify_when_no_logs_are_available_for_some_exceptions_errors_or_failure_messages_present_in_ClickHouse_for_the_Causal_node(ITestContext context) throws Exception {
        int testcase_number = 4;
        int log_Testcase = 3;
        log.info("Starting test: DRSQA_15271_Verify_when_no_logs_are_available_for_some_exceptions_errors_or_failure_messages_present_in_ClickHouse_for_the_Causal_node");
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

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        String title = logagentTC.get(0).get("Change Title").toString() + " " + RandomStringUtils.randomNumeric(5);
        String description = logagentTC.get(0).get("Change Description").toString() + " " + RandomStringUtils.randomNumeric(5);
        
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("Close all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);

        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);

        JSONArray updatedChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"),healthScore.get("id"),jwtToken);
        if (updatedChangeList == null || updatedChangeList.length() == 0) {
            String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
            if (changeid == null){
                Assert.fail("Unable to create Change request");
            }
        }
        else {
            long scheduletime = Long.MIN_VALUE;
            for (int i = 0; i < updatedChangeList.length(); i++) {
                JSONObject src = updatedChangeList.getJSONObject(i).optJSONObject("_source");
                Long scheduledStartDate = getLongValueFromJson(src, "scheduled_start_date");
                if (scheduledStartDate != null) {
                    scheduletime = Math.max(scheduletime, scheduledStartDate);
                }
            }
            long currenttime = hm_events.getBhomServerTime(jwtToken);
            log.info("Scheduled time: " + scheduletime + " Current time: " + currenttime);
            //get 1 day time in milliseconds
            long oneDay = 24 * 60 * 60 * 1000;
            if (scheduletime < (currenttime - oneDay)) {
                String changeid = aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
                if (changeid == null){
                    Assert.fail("Unable to create Change request");
                }
            }
        }

        log.info("Send Log related to Oracle Database on host " + logHost);
        
        
        List<String> logData = aiopsDeepRCA.sendLogAgentEventsFromExcel(log_Testcase, mlSheetName, logHost);
        if (logData.isEmpty() || logData == null) {
            Assert.fail("Log data is empty or null");
        }
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
        }
        
        log.info("Wait for the situation");
        healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
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
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("change tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE") && !actionStatus.equals("COMPLETE")) {
                    softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                }
            }
            if (actionTool.equals("log tool")) {
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
                JSONArray toolOutputs = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                if(toolOutputs.length() == 0) {
                    log.error("No tool outputs found" + actionObject.getJSONObject("step").toString());
                    softAssert.fail("No tool outputs found");
                    continue;
                }
                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Find sematic Similarity");
                String llmQuery = "provide me the recommendation for the below issue reported by <KINDTYPE> monitoring agent \n";
                llmQuery = llmQuery + String.join("\n", logData);
                String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
                double rate = similarityCalculator.calculateSemanticSimilarity(tooloutput, llmOutput);
                if (rate < 0.70) {
                    log.info("Tool output: " + tooloutput);
                    log.info("BAP Summary: " + llmOutput);
                    log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                }
                
            }
        }
        aiopsDeepRCA.updateDeepRCA(situationIdentifier);
        commonwait.waitForSometime(10000);
        JSONObject situationRCA = aiopsDeepRCA.getSituationRCA(situationIdentifier, jwtToken);
        if (situationRCA == null) {
            softAssert.fail("Get situation RCA failed");
        }else{
            JSONObject nodes = situationRCA.optJSONObject("nodes");
            if (nodes == null || nodes.length() == 0) {
                softAssert.fail("No nodes present in situation RCA response");
            }else{

                String topNodeId = null;
                String topKind = null;
                String topType = null;
                double topScore = Double.NEGATIVE_INFINITY;

                for (String nodeId : nodes.keySet()) {
                    JSONObject node = nodes.optJSONObject(nodeId);
                    if (node == null) {
                        continue;
                    }
                    double score = node.optDouble("rcaScore", Double.NEGATIVE_INFINITY);
                    if (score > topScore) {
                        topScore = score;
                        topNodeId = nodeId;
                        topKind = node.optString("kind", "");
                        topType = node.optString("type", "");
                    }
                }

                if (topNodeId == null) {
                    softAssert.fail("Unable to determine highest RCA score node");
                } else {
                    log.info("Highest RCA node: " + topNodeId + " kind: " + topKind + " type: " + topType + " score: " + topScore);
                    if (!"AI Tool".equalsIgnoreCase(topKind)) {
                        softAssert.fail("Top RCA node kind is not 'AI Tool': " + topType);
                    }
                }
            }
        }
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    @Test(description = "[Log Agent] Verify if the deep rca log traces show memory issue but, first RC events occured for disk, then after log tool execution the RC should change to memory.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15268", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15268_Verify_if_the_deep_rca_log_traces_show_memory_issue_but_first_rc_events_occured_for_disk_then_after_log_tool_execution_the_rc_should_change_to_memory(ITestContext context) throws Exception {
        int testcase_number = 5;
        int log_Testcase = 4;
        log.info("Starting DRSQA_15268 scenario");
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
        log.info("Getting test data from Excel");
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        String serviceName = logagentTC.get(0).get("Service Name").toString();
        String changeHost = logagentTC.get(0).get("Change Host").toString();
        String logHost = logagentTC.get(0).get("Log Host").toString();
        String[] impactElement = logagentTC.get(0).get("Impact Node").toString().split(",");

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("Close all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        hm_events.closeAllEventByClass("Situation", jwtToken);

        
        log.info("Send Log related to Oracle Database on host " + logHost);
        
        List<String> logData = aiopsDeepRCA.sendLogAgentEventsFromExcel(log_Testcase, mlSheetName, logHost);
        if (logData.isEmpty() || logData == null) {
            Assert.fail("Log data is empty or null");
        }
        
        log.info("Send the event to the Bhom");
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String msg = logagentTC.get(0).get(titeName).toString() + " " + RandomStringUtils.randomNumeric(5);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
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
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            log.info("actionObject: " + actionObject.toString());
            if (actionTool.equals("change tool")) {
                log.info("Change tool is not applicable, status: " + actionStatus);
                continue;
            }
            if (actionTool.equals("log tool")) {
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
                JSONArray toolOutputs = actionObject.getJSONObject("step").getJSONArray("tool_outputs");
                if(toolOutputs.length() == 0) {
                    log.error("No tool outputs found" + actionObject.getJSONObject("step").toString());
                    softAssert.fail("No tool outputs found");
                    continue;
                }
                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Find sematic Similarity");
                String llmQuery = "provide me the recommendation for the below issue reported by <KINDTYPE> monitoring agent \n";
                llmQuery = llmQuery + String.join("\n", logData);
                String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
                double rate = similarityCalculator.calculateSemanticSimilarity(tooloutput, llmOutput);
                if (rate < 0.70) {
                    log.info("Tool output: " + tooloutput);
                    log.info("BAP Summary: " + llmOutput);
                    log.error("BAP summary not matching for situation " + situationIdentifier + " similarity rate " + rate);
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                }
                
            }
        }
        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);
        commonwait.waitForSometime(10000);
        JSONObject situationRCA = aiopsDeepRCA.getSituationRCA(situationIdentifier, jwtToken);
        if (situationRCA == null) {
            softAssert.fail("Get situation RCA failed");
        }else{
            JSONObject nodes = situationRCA.optJSONObject("nodes");
            if (nodes == null || nodes.length() == 0) {
                softAssert.fail("No nodes present in situation RCA response");
            }else{

                String topNodeId = null;
                String topKind = null;
                String topType = null;
                double topScore = Double.NEGATIVE_INFINITY;

                for (String nodeId : nodes.keySet()) {
                    JSONObject node = nodes.optJSONObject(nodeId);
                    if (node == null) {
                        continue;
                    }
                    double score = node.optDouble("rcaScore", Double.NEGATIVE_INFINITY);
                    if (score > topScore) {
                        topScore = score;
                        topNodeId = nodeId;
                        topKind = node.optString("kind", "");
                        topType = node.optString("type", "");
                    }
                }

                if (topNodeId == null) {
                    softAssert.fail("Unable to determine highest RCA score node");
                } else {
                    log.info("Highest RCA node: " + topNodeId + " kind: " + topKind + " type: " + topType + " score: " + topScore);
                    if (!"AI Tool".equalsIgnoreCase(topKind)) {
                        softAssert.fail("Top RCA node kind is not 'AI Tool': " + topType);
                    }
                }
            }
        }
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

}
