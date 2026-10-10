package ITOM.Aiops;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import util.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITSM.InnovationSuite.InnovationSuiteOpsSwarmerTest;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.SolqaDetails;
import util.SolqaLogger;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import org.testng.asserts.SoftAssert;

public class AIOpsOpsSwarmertest extends Launcher {
    public static SolqaLogger log = new SolqaLogger(AIOpsOpsSwarmertest.class.getName());
    WebDriver driver;
    String app = "ITOM";
    String serviceName;
    String opsSwarmersURL = "/helix/index.html#/com.bmc.dsom.hgm/view/com.bmc.dsom.hgm:Home";
    String helixGPTAgentStudio = "/helix/index.html#/com.bmc.arsys.rx.innovationstudio/com.bmc.dsom.hgm/record-definitions";
    String helixopstesturl = "/helix/index.html#/com.bmc.dsom.hgm/view/com.bmc.dsom.hgm:Test%20Page?skillId=";
    Map<Object, String> aiopsTestData;
    String jwtToken = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    LogAnalyticsPage logAnalyticsPage = null;
    CommonWaits commonwait = null;
    SoftAssert softAssert = null;
    CommonMethods commonmethod = null;
    InnovationSuiteOpsSwarmerTest innovationSuiteOpsSwarmerTest = null;
    String innovationSuiteURL = configProp.get("IS_URL");
    HMEvents hm_events = null;
    String opsswamerssheetname = "OpsSwammerConfig";
    int  opsswamersrownumber  = 1;
    long timediff = 1000 * 60 * 60 * 24 * 7; // 1 day in milliseconds
    String base_url = configProp.get("ADE_PLATFORM_URL");
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    public void doPreconditions() {
        log.info("Setting up the test...");
        aiopsApiCore = new Aiops();
        aiopsSituation = new AiopsSituation();
        commonmethod = new CommonMethods();
        softAssert = new SoftAssert();
        aiopsDeepRCA = new AiopsDeepRCA();
        logAnalyticsPage = new LogAnalyticsPage();
        commonwait = new CommonWaits();
        hm_events = new HMEvents();
        jwtToken = commonmethod.refreshJwtToken(null);
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception{
        doPreconditions();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() throws Exception{
        log.info("Closing the browser");
        if(driver != null){
            driver.quit();
        }
    }


    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "[Ops Swarmer]DeepRCA:Verify DeepRCA for invalid situation ID", groups = {"bap"})
    @SolqaDetails(authorName = "Haris",  JiraID = "DRSQA-17951", Products = {Product.intelligent_automation,Product.AiOPS, Product.Helix_SmartIT})
    public void DRSQA_17951_Verify_DeepRCA_For_Invalid_Situation_ID(ITestContext context ) throws Exception {
        log.info("Starting DRSQA_17951_Verify_DeepRCA_For_Invalid_Situation_ID test");


        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        String msearchEventsBody = "msearchsituationqueryall.json";

        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());

        if (hitsArray.length() == 0){
            Assert.fail("No situations found");
        }

        String situationId = hitsArray.getJSONObject(0).getJSONObject("_source").getString("_identifier");
        log.info("Situation ID is: " + situationId);

        log.info("Make it invalid by adding a random number to the situation ID");
        UUID uuid = UUID.randomUUID(); 
        String randomUUID = uuid.toString();
        situationId = situationId + "-" + randomUUID;
        log.info("Situation ID is: " + situationId);

        String situationSourceIdentifier = hitsArray.getJSONObject(0).getJSONObject("_source").getString("source_identifier");
        log.info("Situation Source Identifier is: " + situationSourceIdentifier);
        

        driver = this.launchWeb(app, app);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),app);
        setDriver(driver);
        innovationSuiteOpsSwarmerTest = new InnovationSuiteOpsSwarmerTest();
        innovationSuiteOpsSwarmerTest.waitForInnovationSuiteLinkToBeVisible();

        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(opsswamersrownumber, opsswamerssheetname);
        String opsSwarmers = logagentTC.get(0).get("Skill Name").toString();
        try
        {
            log.info("Opening HelixGPT Agent Studio");
            boolean isHelixGPTAgentStudioOpened = innovationSuiteOpsSwarmerTest.navigateToHelixGPTAgentStudio(innovationSuiteURL + helixGPTAgentStudio);
            if(!isHelixGPTAgentStudioOpened){
                Assert.fail("HelixGPT Agent Studio not navigated");
            }

            String skillID = innovationSuiteOpsSwarmerTest.searchSkill(opsSwarmers);
            if(skillID == null){
                Assert.fail("Skill not found");
            }
            log.info("Skill ID is: " + skillID);

            log.info("Loading Innovation Suite application");
            boolean isInnovationSuiteApplicationLoaded = innovationSuiteOpsSwarmerTest.loadInnovationSuiteapplication(innovationSuiteURL + opsSwarmersURL);
            if(!isInnovationSuiteApplicationLoaded){
                Assert.fail("Innovation Suite application not loaded");
            }
            log.info("Searching Ops Swarmers in Innovation Suite");
           /*  
            boolean isOpsSwarmersSearched = innovationSuiteOpsSwarmerTest.searchOpsSwarmers(opsSwarmers);
            if(!isOpsSwarmersSearched){
                log.info("Getting result from Ops Swarmers");
            }*/
            boolean isOpsSwarmerTestPromptNavigated = innovationSuiteOpsSwarmerTest.navigatetoopsSwarmerstestprompt(innovationSuiteURL + helixopstesturl + skillID);
            if(!isOpsSwarmerTestPromptNavigated){
                Assert.fail("Ops Swarmer test prompt not navigated");
            }

            String result = innovationSuiteOpsSwarmerTest.getopsSwarmersresult("provide me a deep RCA for the situation ID: " + situationId);
            if(result == null){
                Assert.fail("Result is null");
            }
            log.info("Result from Ops Swarmers: " + result);
            String hostname = excelReaderForMLSituation.getHostnameRegex(base_url);
            if(hostname == null){
                Assert.fail("Hostname is null");
            }
            log.info("Hostname is: " + hostname);
            if(result.contains(hostname)){
                softAssert.fail("Hostname found in the result");
            }

        }
        catch (Exception e){
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        softAssert.assertAll();
        log.info("DRSQA_17951_Verify_DeepRCA_For_Invalid_Situation_ID test completed successfully");
    }

    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "[Ops Swarmer]:Verify DeepRCA is getting triggered from Ops Swarmer and situation url should be available", groups = {"bap"})
    @SolqaDetails(authorName = "Haris",  JiraID = "DRSQA-17950", Products = {Product.intelligent_automation,Product.AiOPS, Product.Helix_SmartIT})
    public void DRSQA_17950_Verify_DeepRCA_Is_Getting_Triggered_From_Ops_Swarmer_And_Situation_Url_Should_Be_Available(ITestContext context ) throws Exception {
        log.info("Starting DRSQA_17950_Verify_DeepRCA_Is_Getting_Triggered_From_Ops_Swarmer_And_Situation_Url_Should_Be_Available test");


        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        String msearchEventsBody = "msearchsituationqueryall.json";

        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());

        if (hitsArray.length() == 0){
            Assert.fail("No situations found");
        }

        String situationId = hitsArray.getJSONObject(0).getJSONObject("_source").getString("_identifier");
        log.info("Situation ID is: " + situationId);

        String situationSourceIdentifier = hitsArray.getJSONObject(0).getJSONObject("_source").getString("source_identifier");
        log.info("Situation Source Identifier is: " + situationSourceIdentifier);
        

        driver = this.launchWeb(app, app);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),app);
        setDriver(driver);
        innovationSuiteOpsSwarmerTest = new InnovationSuiteOpsSwarmerTest();
        innovationSuiteOpsSwarmerTest.waitForInnovationSuiteLinkToBeVisible();
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(opsswamersrownumber, opsswamerssheetname);
        String opsSwarmers = logagentTC.get(0).get("Skill Name").toString();
        try
        {
            log.info("Opening HelixGPT Agent Studio");
            boolean isHelixGPTAgentStudioOpened = innovationSuiteOpsSwarmerTest.navigateToHelixGPTAgentStudio(innovationSuiteURL + helixGPTAgentStudio);
            if(!isHelixGPTAgentStudioOpened){
                Assert.fail("HelixGPT Agent Studio not navigated");
            }

            String skillID = innovationSuiteOpsSwarmerTest.searchSkill(opsSwarmers);
            if(skillID == null){
                Assert.fail("Skill not found");
            }
            log.info("Skill ID is: " + skillID);

            log.info("Loading Innovation Suite application");
            boolean isInnovationSuiteApplicationLoaded = innovationSuiteOpsSwarmerTest.loadInnovationSuiteapplication(innovationSuiteURL + opsSwarmersURL);
            if(!isInnovationSuiteApplicationLoaded){
                Assert.fail("Innovation Suite application not loaded");
            }
            log.info("Searching Ops Swarmers in Innovation Suite");
           /*  
            boolean isOpsSwarmersSearched = innovationSuiteOpsSwarmerTest.searchOpsSwarmers(opsSwarmers);
            if(!isOpsSwarmersSearched){
                log.info("Getting result from Ops Swarmers");
            }*/
            boolean isOpsSwarmerTestPromptNavigated = innovationSuiteOpsSwarmerTest.navigatetoopsSwarmerstestprompt(innovationSuiteURL + helixopstesturl + skillID);
            if(!isOpsSwarmerTestPromptNavigated){
                Assert.fail("Ops Swarmer test prompt not navigated");
            }
            String result = innovationSuiteOpsSwarmerTest.getopsSwarmersresult("provide me a deep RCA for the situation ID: " + situationId );
            log.info("Result from Ops Swarmers: " + result);
            
            if(!result.contains("aiops/#/situations/situation")){
                softAssert.fail("I retrieve the deep RCA for the situation ID: " + situationId + " in the result");
            }
            

        }
        catch (Exception e){
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        softAssert.assertAll();
        log.info("DRSQA_17950_Verify_DeepRCA_Is_Getting_Triggered_From_Ops_Swarmer_And_Situation_Url_Should_Be_Available test completed successfully");
    }

    @Test(description = "[OpsSwarmer] Verifying change analysis details on Teams", groups = {"bap"})
    @SolqaDetails(authorName = "Haris",  JiraID = "DRSQA-15038", Products = {Product.intelligent_automation,Product.AiOPS, Product.Helix_SmartIT})
    public void DRSQA_15038_Verify_Change_Analysis_Details_On_Teams(ITestContext context ) throws Exception {
        log.info("Starting DRSQA_15038_Verify_Change_Analysis_Details_On_Teams test");
        String InputQuery = "Give me Change associated with Incident ";

        log.info("Check if Deep RCA feature is enabled");
        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("get the situation id from the msearch query");
        String msearchEventsBody = "msearchsituationqueryall.json";

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());


        long maxDiffMillis = 0;
        String latestSituationId = null;
        long latestSituationTime = 0;

        String incidentQueryId = null;


        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            if (!"Situation".equalsIgnoreCase(classVal)) continue;

            String situationId = hit.optString("_identifier", "");
            String sourceIdentifier = hit.optString("source_identifier", "");
            String incidentId = hit.optString("incident_id", "");
            String changeId = hit.optString("change_id", "");
            
            if(incidentId.isEmpty() && changeId.isEmpty()){
                continue;
            }
            if(incidentId.length() > 3 && changeId.length() < 3){
                continue;
            }

            int incidentOccurrences = aiopsDeepRCA.countSituationHitsWithIncidentId(hitsArray, incidentId);
            if (incidentOccurrences != 1) {
                log.warn("Incident id is not unique in hits (count=" + incidentOccurrences + ") for id: " + incidentId + "; skipping");
                continue;
            }

            incidentQueryId = incidentId;
            break;

        }

        if(incidentQueryId == null){
            Assert.fail("No incident found with unique incident_id in hits");
        }

        InputQuery = InputQuery + incidentQueryId;
        log.info("Input Query is: " + InputQuery);

        driver = this.launchWeb(app, app);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),app);
        setDriver(driver);
        innovationSuiteOpsSwarmerTest = new InnovationSuiteOpsSwarmerTest();
        innovationSuiteOpsSwarmerTest.waitForInnovationSuiteLinkToBeVisible();
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(opsswamersrownumber, opsswamerssheetname);    
        String opsSwarmers = logagentTC.get(0).get("Skill Name").toString();
        try
        {
            log.info("Opening HelixGPT Agent Studio");
            boolean isHelixGPTAgentStudioOpened = innovationSuiteOpsSwarmerTest.navigateToHelixGPTAgentStudio(innovationSuiteURL + helixGPTAgentStudio);  
            if(!isHelixGPTAgentStudioOpened){
                Assert.fail("HelixGPT Agent Studio not navigated");
            }

            String skillID = innovationSuiteOpsSwarmerTest.searchSkill(opsSwarmers);
            if(skillID == null){
                Assert.fail("Skill not found");
            }
            log.info("Skill ID is: " + skillID);

            log.info("Loading Innovation Suite application");
            boolean isInnovationSuiteApplicationLoaded = innovationSuiteOpsSwarmerTest.loadInnovationSuiteapplication(innovationSuiteURL + opsSwarmersURL); 
            if(!isInnovationSuiteApplicationLoaded){
                Assert.fail("Innovation Suite application not loaded");
            }
            log.info("Searching Ops Swarmers in Innovation Suite");
           /*  
            boolean isOpsSwarmersSearched = innovationSuiteOpsSwarmerTest.searchOpsSwarmers(opsSwarmers);
            if(!isOpsSwarmersSearched){
                log.info("Getting result from Ops Swarmers");
            }*/
            String opsSwarmerTestPromptUrl = innovationSuiteURL + helixopstesturl + skillID;
            boolean isOpsSwarmerTestPromptNavigated = innovationSuiteOpsSwarmerTest.navigatetoopsSwarmerstestprompt(opsSwarmerTestPromptUrl);
            if(!isOpsSwarmerTestPromptNavigated){
                Assert.fail("Ops Swarmer test prompt not navigated");
            }
            String result = innovationSuiteOpsSwarmerTest.getopsSwarmersresult(InputQuery );
            log.info("Result from Ops Swarmers: " + result);
            
            if(!result.contains("aiops/#/situations/situation")){
                softAssert.fail("I retrieve the change analysis details for the incident ID: " + incidentQueryId + " in the result");
            }
            

        }
        catch (Exception e){
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        softAssert.assertAll();
        log.info("DRSQA_15038_Verify_Change_Analysis_Details_On_Teams test completed successfully");
    }

    @Test(description = "[OpsSwarmer-SNOW] Retrieving situation details using service name", groups = {"bap"})
    @SolqaDetails(authorName = "Haris",  JiraID = "DRSQA-15039", Products = {Product.intelligent_automation,Product.AiOPS, Product.Helix_SmartIT})
    public void DRSQA_15039_Retrieving_situation_details_using_service_name(ITestContext context ) throws Exception {
        log.info("Starting DRSQA_15039_Retrieving_situation_details_using_service_name test");
        String InputQuery = "Give me Change associated with Incident ";
        String searchincidentsearch = "msearchmessageinfoquery.json";
        long situationTimeDiff = 1000;

        log.info("Check if Deep RCA feature is enabled");
        boolean isDeepRCAFeatureEnabled = aiopsDeepRCA.isDeepRCAFeatureEnabled();
        if (!isDeepRCAFeatureEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return;
        }

        log.info("get the situation id from the msearch query");
        String msearchEventsBody = "msearchsituationqueryall.json";

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());


        long maxDiffMillis = 0;
        String latestSituationId = null;
        long latestSituationTime = 0;

        String incidentQueryId = null;
        String situationQueryIdentifier = null;
        String situationQueryServiceName = null;
        String situationQuerySourceIdentifier = null;

        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            if (!"Situation".equalsIgnoreCase(classVal)) continue;

            String incidentId = hit.optString("incident_id", "");
            String changeId = hit.optString("change_id", "");
            String situationservicekey = hit.optString("_service_name", "");
            String situationsourceidentifier = hit.optString("source_identifier", "");

            String situationIdentifier = hit.optString("_identifier", "");

            if(incidentId.isEmpty() && changeId.isEmpty()){
                continue;
            }
            if(incidentId.length() > 3 && changeId.length() < 3){
                continue;
            }

            log.info("Check if Incident ID is from Service Now");
            JSONObject incidentObject = hm_events.runMSearchQueryGetSituation(incidentId, jwtToken, searchincidentsearch, situationTimeDiff);
            JSONObject incidenthitsobject = incidentObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
            if(incidenthitsobject.getJSONArray("hits").length() == 0){
                log.info("No incident found with unique incident_id in hits");
                continue;
            }
            String incidentsituationIdentifier = incidenthitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("source_identifier");

            if(!incidentsituationIdentifier.contains("ServiceNow")){
                log.info("Incident ID is not from Service Now, test case cannot be executed");
                return;
            }

            int incidentOccurrences = aiopsDeepRCA.countSituationHitsWithIncidentId(hitsArray, incidentId);
            if (incidentOccurrences != 1) {
                log.warn("Incident id is not unique in hits (count=" + incidentOccurrences + ") for id: " + incidentId + "; skipping");
                continue;
            }

            incidentQueryId = incidentId;
            situationQueryIdentifier = situationIdentifier;
            situationQuerySourceIdentifier = situationsourceidentifier;
            situationQueryServiceName = firstSegmentFromBracketedServiceList(situationservicekey);
            log.info("First service key from _service_name (e.g. abc from [abc,xyz]): " + situationQueryServiceName);
            break;

        }

        if(incidentQueryId == null){
            Assert.fail("No incident found with unique incident_id in hits");
        }

        InputQuery = InputQuery + incidentQueryId;
        log.info("Input Query is: " + InputQuery);

        driver = this.launchWeb(app, app);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),app);
        setDriver(driver);
        innovationSuiteOpsSwarmerTest = new InnovationSuiteOpsSwarmerTest();
        innovationSuiteOpsSwarmerTest.waitForInnovationSuiteLinkToBeVisible();
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(opsswamersrownumber, opsswamerssheetname);
        String opsSwarmers = logagentTC.get(0).get("Skill Name").toString();
        try
        {
            log.info("Opening HelixGPT Agent Studio");
            boolean isHelixGPTAgentStudioOpened = innovationSuiteOpsSwarmerTest.navigateToHelixGPTAgentStudio(innovationSuiteURL + helixGPTAgentStudio);  
            if(!isHelixGPTAgentStudioOpened){
                Assert.fail("HelixGPT Agent Studio not navigated");
            }

            String skillID = innovationSuiteOpsSwarmerTest.searchSkill(opsSwarmers);
            if(skillID == null){
                Assert.fail("Skill not found");
            }
            log.info("Skill ID is: " + skillID);

            log.info("Loading Innovation Suite application");
            boolean isInnovationSuiteApplicationLoaded = innovationSuiteOpsSwarmerTest.loadInnovationSuiteapplication(innovationSuiteURL + opsSwarmersURL); 
            if(!isInnovationSuiteApplicationLoaded){
                Assert.fail("Innovation Suite application not loaded");
            }
            log.info("Searching Ops Swarmers in Innovation Suite");
           
            boolean isOpsSwarmerTestPromptNavigated = innovationSuiteOpsSwarmerTest.navigatetoopsSwarmerstestprompt(innovationSuiteURL + helixopstesturl + skillID);
            if(!isOpsSwarmerTestPromptNavigated){
                Assert.fail("Ops Swarmer test prompt not navigated");
            }

            InputQuery = "provide situation details for the incident ID: " + incidentQueryId;
            String result = innovationSuiteOpsSwarmerTest.getopsSwarmersresult(InputQuery );
            log.info("Result from Ops Swarmers: " + result);
            
            if(!result.contains(situationQueryIdentifier)){
                softAssert.fail("I retrieve the change analysis details for the incident ID: " + incidentQueryId + " in the result");
            }

            InputQuery = "I need situation summary for: " + situationQuerySourceIdentifier;
            result = innovationSuiteOpsSwarmerTest.getopsSwarmersresult(InputQuery );
            log.info("Result from Ops Swarmers: " + result);
            JSONObject deepRCASummary = aiopsDeepRCA.getDeepRCASituationSummary(situationQuerySourceIdentifier);
            String situationSummary = deepRCASummary.getString("situation_summary");
            double rate = aiopsDeepRCA.getDeepRCAScore(result, situationSummary);
            if (rate < 0.70) {
                log.error("BAP summary not matching for situation " + situationQuerySourceIdentifier + " similarity rate " + rate);
                softAssert.fail("Situation summary should be matching with the event messages");
            }
            
            InputQuery = "show me situations for service : " + situationQueryServiceName;
            result = innovationSuiteOpsSwarmerTest.getopsSwarmersresult(InputQuery );
            log.info("Result from Ops Swarmers: " + result);
            
            if(!result.contains(situationQueryIdentifier)){
                log.error("I retrieve the change analysis details for the incident ID: " + incidentQueryId + " in the result");
                log.error("Result from Ops Swarmers: " + result);
                log.error("Situation Query Identifier: " + situationQueryIdentifier);
            }


        }
        catch (Exception e){
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        softAssert.assertAll();
        log.info("DRSQA_15038_Verify_Change_Analysis_Details_On_Teams test completed successfully");
    }

    /**
     * Parses values like {@code [abc,xyz]} and returns the first segment ({@code abc}).
     * If there are no brackets, returns the substring before the first comma, or the whole trimmed string.
     */
    private static String firstSegmentFromBracketedServiceList(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        String t = raw.trim();
        if (t.startsWith("[") && t.endsWith("]")) {
            t = t.substring(1, t.length() - 1).trim();
        }
        int comma = t.indexOf(',');
        return comma >= 0 ? t.substring(0, comma).trim() : t;
    }

}