package ITOM.Aiops;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import ai.onnxruntime.OrtException;
import util.SolqaDetails;
import util.SolqaLogger;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.HMEvents;
import java.io.IOException;
import java.util.*;
import util.*;


/**
 * Java translation (API-first) of Robot test "create-service-configure-for-prediction-and-runAutomation".
 * This focuses on the core REST steps without invoking Robot Framework.
 * NOTE: Several Robot keywords map to REST calls shown here; any missing mappings are marked TODO.
 */
public class AiopsAskHelixGPTTest {

    private static final SolqaLogger log = new SolqaLogger(AiopsAskHelixGPTTest.class.getName());

    // Endpoints used in the Robot keywords
    Aiops aiopsApiCore;
    HMEvents hm_events;
    CommonWaits commonWait;
    CommonMethods commonmethod;
    AiopsPrediction aiopsPrediction;
    AiopsDeepRCA aiopsDeepRCA;
    private SoftAssert softAssert = null;
    private String jwtToken = null;
    private long timediff = 86400000; // 24 hours in milliseconds
   
    @BeforeMethod(alwaysRun = true)
    public void setUpHelpers() {
        aiopsApiCore = new Aiops();
        hm_events = new HMEvents();
        commonWait = new CommonWaits();
        commonmethod = new CommonMethods();
        aiopsPrediction = new AiopsPrediction();
        aiopsDeepRCA = new AiopsDeepRCA();
    }

    @Test(description = "[Ask-GPT][AiOps] Verify Multi-Query response in chat window using Incident ID", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18963", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18963_Verify_Multi_Query_response_in_chat_window_using_Incident_ID(ITestContext context) throws Exception {
        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(null);
        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("ask-helixgpt");
        if(!isFeatureEnabled){
            log.info("ask-helixgpt feature flag is not enabled; skipping test as passed.");
            return;
        }
        log.info("Get the situation having incident id in last 24 hours");
        String msearchEventsBody = "msearchsituationqueryall.json";

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());
        String situationId = null;
        String incidentId = null;
        String sourceIdentifier = null;
        String changeId = null;
        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            if (!"Situation".equalsIgnoreCase(classVal)) continue;

            situationId = hit.optString("_identifier", "");
            incidentId = hit.optString("incident_id", "");
            sourceIdentifier = hit.optString("source_identifier", "");
            changeId = hit.optString("change_id", "");

            if (incidentId.trim().isEmpty() || incidentId.length() < 3) {
                continue;
            }
            
            if (changeId.trim().isEmpty() || changeId.length() < 3) {
                continue;
            }

            log.info("Get the Helix GPT query response for the incident id: " + incidentId);
            break;
        }

        if (situationId == null || situationId.trim().isEmpty() || incidentId == null || incidentId.trim().isEmpty() || sourceIdentifier == null || sourceIdentifier.trim().isEmpty() || changeId == null || changeId.trim().isEmpty()) {
            Assert.fail("No situation found with incident id in last 24 hours");
        }

        String query = "Give me the situation summary for Incident " +  incidentId;
        log.info("Query: " + query);

        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }

        
        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);

        
        String contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            log.info("Helix GPT query response: " + contents);
            JSONObject deepRCASituationSummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
            String deepRCASituationSummaryText = deepRCASituationSummary.getString("situation_summary");

            double rate = aiopsDeepRCA.getDeepEvalRCAScore(deepRCASituationSummaryText, contents, 0);
            if (rate < 0.70) {
                log.info("Tool output: " + deepRCASituationSummaryText);
                log.info("BAP Summary: " + contents);
                log.error("BAP summary not matching for situation " + sourceIdentifier + " similarity rate " + rate);
                softAssert.fail("Log tool should be applicable, status: ");
            }
        }

        log.info("next query");
        query = "Give me the 5 why analysis for Incident " +  incidentId;
        log.info("Query: " + query);

        contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            log.info("Helix GPT query response: " + contents);        
            String fiveWhyAnalysis = aiopsDeepRCA.prescannQuery(situationId,4);
            if(fiveWhyAnalysis == null){
                softAssert.fail("Five why analysis is null");
            }else{
                double rate = aiopsDeepRCA.getDeepEvalRCAScore(fiveWhyAnalysis, contents, 0);
                if (rate < 0.70) {
                    log.info("Tool output: " + fiveWhyAnalysis);
                    log.info("BAP Summary: " + contents);
                    log.error("BAP summary not matching for situation " + sourceIdentifier + " similarity rate " + rate);
                    softAssert.fail("Log tool should be applicable, status: ");
                }
            }
        }


        log.info("next query");
        query = "Get me the changes related to the INCIDENT " +  incidentId;
        log.info("Query: " + query);

        contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            log.info("Helix GPT query response: " + contents);        
            String[] changes = changeId.split(",");
            if(!contents.contains(changes[0])){
                softAssert.fail("Changes not found in Helix GPT query response");
            }
        }

        log.info("next query");
        query = "View all critical situations";
        log.info("Query: " + query);

        
        contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            if(!contents.contains("critical situations")){
                softAssert.fail("Critical situations not found in Helix GPT query response");
            }
            log.info("Helix GPT query response: " + contents);
        }

        softAssert.assertAll();
    }

    @Test(description = "[Ask-GPT][AiOps] Verify Multi-Query response in chat window using situation ID", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18962", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18962_Verify_Multi_Query_response_in_chat_window_using_Situation_ID(ITestContext context) throws Exception {
        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(null);
        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("ask-helixgpt");
        if(!isFeatureEnabled){
            log.info("ask-helixgpt feature flag is not enabled; skipping test as passed.");
            return;
        }
        log.info("Get the situation having incident id in last 24 hours");
        String msearchEventsBody = "msearchsituationqueryall.json";

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());
        String situationId = null;
        String incidentId = null;
        String sourceIdentifier = null;
        String changeId = null;
        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            if (!"Situation".equalsIgnoreCase(classVal)) continue;

            situationId = hit.optString("_identifier", "");
            incidentId = hit.optString("incident_id", "");
            sourceIdentifier = hit.optString("source_identifier", "");
            changeId = hit.optString("change_id", "");

            if(incidentId.isEmpty() || incidentId.length() < 3){
                continue;
            }
            
            if(changeId.isEmpty() || changeId.length() < 3){
                continue;
            }

            log.info("Get the Helix GPT query response for the incident id: " + incidentId);
            break;
        }

        if(situationId == null || incidentId == null || sourceIdentifier == null || changeId == null || situationId.trim().isEmpty() || incidentId.trim().isEmpty() || sourceIdentifier.trim().isEmpty() || changeId.trim().isEmpty()){
            Assert.fail("No situation found with incident id in last 24 hours");
        }
        String query = "Give me situation summary for " +  sourceIdentifier;
        log.info("Query: " + query);

        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }

        
        JSONObject deepRCAResult = aiopsDeepRCA.triggerandreturnDeepRCAResult(sourceIdentifier);

        
        String contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            log.info("Helix GPT query response: " + contents);
            JSONObject deepRCASituationSummary = aiopsDeepRCA.getDeepRCASituationSummary(sourceIdentifier);
            String deepRCASituationSummaryText = deepRCASituationSummary.getString("situation_summary");
            
            double rate = aiopsDeepRCA.getDeepEvalRCAScore(deepRCASituationSummaryText, contents, 0);
            if (rate < 0.70) {
                log.info("Tool output: " + deepRCASituationSummaryText);
                log.info("BAP Summary: " + contents);
                log.error("BAP summary not matching for situation " + sourceIdentifier + " similarity rate " + rate);
                softAssert.fail("Log tool should be applicable, status: ");
            }
        }

        log.info("next query");
        query = "get me the best action plan for the situation " +  sourceIdentifier;
        log.info("Query: " + query);

        contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            if(!contents.contains("log tool")){
                softAssert.fail("Log tool not found in Helix GPT query response");
            }
            if(!contents.contains("change tool")){
                softAssert.fail("Change tool not found in Helix GPT query response");
            }
            if(!contents.contains("code tool")){
                softAssert.fail("Code tool not found in Helix GPT query response");
            }
            if(!contents.contains("document tool")){
                softAssert.fail("Document tool not found in Helix GPT query response");
            }
            log.info("Helix GPT query response: " + contents);        
        }


        log.info("next query");
        query = "Get me the changes related to the situation " +  sourceIdentifier;
        log.info("Query: " + query);

        contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            log.info("Helix GPT query response: " + contents);        
            String[] changes = changeId.split(",");
            if(!contents.contains(changes[0])){
                softAssert.fail("Changes not found in Helix GPT query response");
            }
        }
        

        log.info("next query");
        query = "View all critical situations";
        log.info("Query: " + query);

        
        contents = aiopsDeepRCA.getHelixGPTQueryResponse(query);
        if(contents == null){
            softAssert.fail("Helix GPT query response is null");
        }else{
            if(!contents.contains("critical situations")){
                softAssert.fail("Critical situations not found in Helix GPT query response");
            }
            log.info("Helix GPT query response: " + contents);
        }

        softAssert.assertAll();
    }    

}