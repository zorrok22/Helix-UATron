package ITOM.Aiops;

import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;

import ai.onnxruntime.OrtException;
import util.Product;
import util.ReadPropertiesFile;
import util.SolqaDetails;
import util.SolqaLogger;
import org.testng.Assert;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.*;
import util.DeepEval;
import org.openqa.selenium.WebDriver;
import util.CommonWaits;
import java.util.concurrent.ThreadLocalRandom;

public class AiopsSituationDeepRCATest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationBAPTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = null;
    CommonWaits commonwait = null;
    DeepEval deepEval = null;
    public static final String event_tag = "";
    private String helixGPTSummaryEndPoint = "/situation-detector/api/v1.2/situation/description/";
    private String helixGPTDeepRCAEndPoint = "/situation-best-action-recommender/api/v1.0/deep-rca/";
    LogAnalyticsPage logAnalyticsPage = null;
    String location = "Pune";
    String mlSheetName = "logAgentLogs";
    String jwtToken = null;
    String eventClass = "NAGIOS_EV1";
    HMEvents hm_events = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    SoftAssert softAssert = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    LogAnalyticsPage loganalyticspage = null;
    int no_of_event_per_node = 1;
    String sheetName = "DeepRCATC";
    String situationjsonfile = "msearchsituationquery.json";
    private static final long situationTimeDiff = 300000;
    String[] OldfeatureList = {"deep-rca-analysis-enabled","gpt-situation-summary", "gpt-situation-recommendation-details", "bar-skip-code-gen", "gpt-situation-code-wizard", "use_bar_model_for_ai_summary", "gpt-situation-questions", "helixgpt-agenticai-enabled", "gpt-situation-best-recommendation-planner", "helixgpt-five-whys"};
    String[] newfeatureList = {"deep-rca-analysis-enabled","helixgpt-agenticai-enabled"};
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
        hm_events = new HMEvents();
        aiopsApiCore = new Aiops();
        aiopsSituation = new AiopsSituation();
        softAssert = new SoftAssert();
        aiopsDeepRCA = new AiopsDeepRCA();
        logAnalyticsPage = new LogAnalyticsPage();
        deepEval = new DeepEval();
        commonwait = new CommonWaits();
        commonmethod = new CommonMethods();
        loganalyticspage = new LogAnalyticsPage();
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
    }
    
    public List<String> getSituationGPTSummary(String identifier) {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String endPoint = helixGPTSummaryEndPoint + identifier;
        try {
            // Assuming bhomapu is an instance with similar methods as in Python
            logAnalyticsPage.Send_RestCall("GET", endPoint, new String[]{"200"});
            if (logAnalyticsPage.jsonObject == null || logAnalyticsPage.jsonObject.length() < 3) {
                return null;
            }
            JSONObject json = logAnalyticsPage.jsonObject;
            String situationSummary = json.optString("situation_summary", null);
            String rootCauseClassification = json.optString("root_cause_classification", null);
            String rootCauseSummary = json.optString("root_cause_summary", null);

            if (logAnalyticsPage.response_code != 200) {
                return null;
            }

            return Arrays.asList(situationSummary, rootCauseClassification, rootCauseSummary);
        } catch (Exception error) {
            log.info(error.getMessage());
            return null;
        }
    }

    public List<String> getSituationLogToolDeepRCA(String identifier) {
        return getSituationToolDeepRCA(identifier, "log tool");
    }

    public List<String> getSituationChangeToolDeepRCA(String identifier) {
        return getSituationToolDeepRCA(identifier, "change tool");
    }

    private List<String> getSituationToolDeepRCA(String identifier, String toolName) {
        CommonWaits commonWait = new CommonWaits();
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String endPoint = helixGPTDeepRCAEndPoint + identifier + "?force=false";
        try {
            JSONObject json = null;
            JSONObject toolStep = null;
            for(int i = 0; i < 10; i++) {
                logAnalyticsPage.Send_RestCall("POST", endPoint, new String[]{"200", "{}"});
                json = logAnalyticsPage.jsonObject;
                if (json == null || json.length() < 3) {
                    commonWait.waitForSometime(10000);
                    continue;
                }
                toolStep = getToolStep(json, toolName);
                if (toolStep == null) {
                    log.info("Deep RCA response missing " + toolName + " step. Retry attempt " + (i + 1));
                    commonWait.waitForSometime(10000);
                    continue;
                }
                String stepStatus = toolStep.optString("status", "");
                if ("UNSCHEDULED".equalsIgnoreCase(stepStatus) || "SCHEDULED".equalsIgnoreCase(stepStatus)) {
                    log.info(toolName + " step status " + stepStatus + " still in progress. Retry attempt " + (i + 1));
                    toolStep = null;
                    commonWait.waitForSometime(10000);
                    continue;
                }
                break;
            }
            if (logAnalyticsPage.jsonObject == null || logAnalyticsPage.jsonObject.length() < 3) {
                softAssert.fail("Deep RCA response is empty after retries");
                return null;
            }
            json = logAnalyticsPage.jsonObject;
            if (toolStep == null) {
                toolStep = getToolStep(json, toolName);
            }
            if (toolStep == null) {
                softAssert.fail("Deep RCA response does not contain " + toolName + " information");
                return null;
            }
            String status = toolStep.optString("status", "");
            if ("NOT_APPLICABLE".equalsIgnoreCase(status)) {
                softAssert.fail(toolName + " status is NOT_APPLICABLE");
                return null;
            }
            JSONArray toolOutputs = toolStep.optJSONArray("tool_outputs");
            if (toolOutputs == null || toolOutputs.length() == 0) {
                softAssert.fail(toolName + " outputs are missing");
                return null;
            }
            JSONObject toolOutput = toolOutputs.getJSONObject(0);
            String toolOutputStatus = toolOutput.optString("tool_output_problem_root_cause_status", "");
            if ("refuted".equalsIgnoreCase(toolOutputStatus)) {
                softAssert.fail("Problem root cause status is refuted for " + toolName);
                return null;
            }
            String toolOutputId = toolOutput.optString("id", "");
            if (toolOutputId.isEmpty()) {
                softAssert.fail("Tool output id missing for causal graph validation");
                return null;
            }
            JSONArray causalGraph = json.optJSONArray("causal_graph");
            if (!isToolOutputInCausalGraph(toolOutputId, causalGraph)) {
                softAssert.fail("Tool output id " + toolOutputId + " not present in causal graph targets");
                return null;
            }

            List<String> deepRcaDetails = new ArrayList<>();
            deepRcaDetails.add(json.optString("state", ""));
            deepRcaDetails.add(json.optString("root_cause_event_id", ""));
            deepRcaDetails.add(toolOutputId);
            deepRcaDetails.add(toolOutput.optString("tool_output", ""));
            deepRcaDetails.add(toolOutput.optString("tool_output_detailed", ""));
            return deepRcaDetails;
        } catch (Exception error) {
            log.info(error.getMessage());
            return null;
        }
    }

    private JSONObject getToolStep(JSONObject response, String toolName) {
        if (response == null) {
            return null;
        }
        JSONArray actions = response.optJSONArray("actions");
        if (actions == null) {
            return null;
        }
        for (int i = 0; i < actions.length(); i++) {
            JSONObject action = actions.optJSONObject(i);
            if (action == null) {
                continue;
            }
            JSONObject step = action.optJSONObject("step");
            if (step != null && toolName.equalsIgnoreCase(step.optString("tool", ""))) {
                return step;
            }
        }
        return null;
    }

    private boolean isToolOutputInCausalGraph(String toolOutputId, JSONArray causalGraph) {
        if (toolOutputId == null || toolOutputId.isEmpty() || causalGraph == null) {
            return false;
        }
        for (int i = 0; i < causalGraph.length(); i++) {
            JSONObject edge = causalGraph.optJSONObject(i);
            if (edge != null && toolOutputId.equals(edge.optString("tgt"))) {
                return true;
            }
        }
        return false;
    }

    @Test(description = "[Deep RCA] Verify Situation summary when only logs is associated to the situation with log as a RCA", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15720", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15720_Verify_Situation_Summary_when_only_logs_is_associated_to_the_situation_with_log_as_a_RCA(ITestContext context) throws Exception {
        
        int testcase_number = 7;
        int log_Testcase = 1;
        log.info("Starting test: DRSQA_15720_Verify_Situation_Summary_when_only_logs_is_associated_to_the_situation_with_log_as_a_RCA");
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
        
        List<String> logDataList = aiopsDeepRCA.sendLogAgentEventsFromExcel(log_Testcase, mlSheetName, logHost);
        if (logDataList.isEmpty() || logDataList == null) {
            Assert.fail("Log data is empty or null");
        }
        
        log.info("Send the event to the Bhom");
        List<String> eventMessageList = new ArrayList<>();
        int no_of_eventnode = (int) Double.parseDouble(logagentTC.get(0).get("Event Count").toString());
        for (int i = 1; i <= no_of_eventnode; i++) {
            String titeName = "Event " + String.valueOf(i);
            String result = String.format("%05d", ThreadLocalRandom.current().nextInt(100000));
            String msg = logagentTC.get(0).get(titeName).toString() + " " + result;
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactElement[i-1].trim(), msg, "MAJOR", location, event_tag);
            eventMessageList.add(msg);
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
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    break;
                }
            }
        }

        log.info("Get Deep RCA Summary");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
        String situationSummary = deepRCASummary.getString("situation_summary");

        log.info("Find semantic similarity using DeepEval");
        String llmQuery = "provide me the recommendation for below event messages \n" + String.join("\n", logDataList);
        double rate = aiopsDeepRCA.getDeepRCAScore(llmQuery, situationSummary);
        if (rate < 0.70) {
            log.error("BAP summary not matching for situation " + situationIdentifier + " similarity/relevancy rate " + rate);
            softAssert.fail("Situation summary should be matching with the event messages");
        }   
        
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

    @Test(description = "[Deep RCA] Verify Situation summary when only change is associated to the situation with change as a RCA", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15719", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15719_Verify_Situation_summary_when_only_change_is_associated_to_the_situation_with_change_as_RCA(ITestContext context) throws Exception {
        
        log.info("Starting test: DRSQA_15719_Verify_Situation_summary_when_only_change_is_associated_to_the_situation_with_change_as_RCA");
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

        String serviceName = "ML-TSOM";
        String[] ciList = {"host-01-02", "host-01-03", "host-01-04", "host-01-07"};
        String changeHost = "host-01-03";
        int no_of_event_per_node = 1;

        String cmdbUrl = config.get("CMDBURL");
        String username = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");
        RemedyApiClient remedyApiClient = new RemedyApiClient(cmdbUrl);
        String authToken = remedyApiClient.getRemedyApiKey(username, password);
        CMDBRestApi cmdbRestApi = new CMDBRestApi(cmdbUrl, authToken);

        String changeSummary = "Memory increase request for " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(5);
        String changeDescription = "Increase the memory allocation of virtual machine " + changeHost + " to remediate sustained memory saturation impacting service stability.";
         
        Map<String, String> changeIdentifiers = cmdbRestApi.createChangeRequestUpdateStatus(changeSummary, changeDescription, serviceName, ciList, username,"US Support 3");
        if (changeIdentifiers == null) {
            Assert.fail("Failed to create and progress change request for " + changeHost);
        }
        if(!aiopsDeepRCA.moveChangeTorequestStatus(changeIdentifiers,"Implementation In Progress")){
            Assert.fail("Failed to progress change request for service " + serviceName);
        }


        String jwtToken = commonmethod.refreshJwtToken(null);
        

        String[] eventMessages = {
                "Memory utilization exceeded threshold on " + changeHost,
                "Paging detected due to insufficient memory on " + changeHost
        };
        for (String msg : eventMessages) {
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, changeHost, msg, "MAJOR", location, event_tag);
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
            if (actionTool.equals("change tool")) {
                if (actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Change tool should be applicable, status: " + actionStatus);
                    continue;
                }
                String tooloutput = actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output");
                tooloutput = tooloutput + "\n" + actionObject.getJSONObject("step").getJSONArray("tool_outputs").getJSONObject(0).getString("tool_output_detailed");
                log.info("Find sematic Similarity");
                String llmQuery = "provide me the issue while cause by ITSM change request \n";
                llmQuery = llmQuery + changeSummary + '\n' + changeDescription;
                llmQuery = llmQuery + '\n' + "Below is the event messages reported by monitoring agent:";
                llmQuery = llmQuery + '\n' + String.join("\n", eventMessages);
                
                double rate = aiopsDeepRCA.getDeepRCAScore(llmQuery, tooloutput);
                if (rate < 0.70) {
                    log.error("BAP summary not matching for situation " + situationIdentifier + " similarity/relevancy rate " + rate);
                    softAssert.fail("Situation summary should be matching with the event messages");
                }

                continue;
            }
            if (actionTool.equals("log tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                }
                continue;
            }
        }


        softAssert.assertAll();
    }
    
    @Test(description = "[Deep RCA] Verify Situation summary when RCA host does not have any logs associated to it", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15723", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15723_Verify_Situation_summary_when_RCA_host_does_not_have_any_logs_associated_to_it(ITestContext context) throws Exception {
        int testcase_number = 9;
        log.info("Starting test: DRSQA_15723_Verify_Situation_summary_when_RCA_host_does_not_have_any_logs_associated_to_it");
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

        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("change tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                    continue;
                }
            }
            if (actionTool.equals("log tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE")) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    continue;
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

        softAssert.assertAll();
    }

    @Test(description = "[Deep RCA] Verify Situation summary when change and log agent is associated to the situation with change as an RCA.", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "15722", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.AiOPS, Product.HelixGPT})
    public void DRSQA_15722_Verify_Situation_summary_when_change_and_log_agent_is_associated_to_the_situation_with_change_as_an_RCA(ITestContext context) throws Exception {
        
        int testcase_number = 10;
        int log_Testcase = 1;

        log.info("Starting test: DRSQA_15722_Verify_Situation_summary_when_change_and_log_agent_is_associated_to_the_situation_with_change_as_an_RCA");
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

        String title = logagentTC.get(0).get("Change Title").toString() + " " + RandomStringUtils.secure().nextNumeric(5);
        String description = logagentTC.get(0).get("Change Description").toString() + " " + RandomStringUtils.secure().nextNumeric(5);

        jwtToken = commonmethod.refreshJwtToken(jwtToken);


        aiopsDeepRCA.createITSMChangeEvent(serviceName, title, description);
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

        JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
        for( Object action : deepRCAStatus) {
            JSONObject actionObject = (JSONObject) action;
            String actionTool = actionObject.getJSONObject("step").getString("tool");
            String actionStatus = actionObject.getJSONObject("step").getString("status");
            if (actionTool.equals("change tool")) {
                if (!actionStatus.equals("NOT_APPLICABLE") && !actionStatus.equals("COMPLETE")) {
                    softAssert.fail("Change tool is not applicable, status: " + actionStatus);
                    continue;
                }
            }
            if (actionTool.equals("log tool")) {
                if (actionStatus.equals("NOT_APPLICABLE") ) {
                    softAssert.fail("Log tool should be applicable, status: " + actionStatus);
                    continue;
                }
            }
        }
        aiopsDeepRCA.updateDeepRCA(sourceIdentifier);

        log.info("Get Deep RCA Summary");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
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
                    if (!"change tool".equalsIgnoreCase(topType)) {
                        softAssert.fail("Top RCA node kind 'AI Tool' should have type 'change tool' but was: " + topType);
                    }
                }
            }}
        }
        
        aiopsSituation.CloseAllEventOnService(serviceName);
        softAssert.assertAll();

    }

}
