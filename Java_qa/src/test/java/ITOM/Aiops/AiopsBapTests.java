package ITOM.Aiops;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.*;
import java.io.File;
import java.util.*;

public class AiopsBapTests extends Launcher {
    public static SolqaLogger log = new SolqaLogger(AiopsBapTests.class.getName());
    ReadJson readJson;
    ITSMFeatureMethods itsmFeatureMethods;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    Map<Object, String> itsmUserData, itsmTestData;
    Aiops aiops_lib = new Aiops();
    HMEvents eventsApi = new HMEvents();
    WebDriver itsmDriver;
    WebDriver itomDriver;
    AiopsDeepRCA aiopsDeepRCA = new AiopsDeepRCA();
    Events_API api = new Events_API();
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    String app = "ITOM";

    BhomFeatureMethods bhomFeatureMethods;


    @Test(description = "[BAP]When change has single CI configured and that change is associated with Situation",groups={"bap"})
    @SolqaDetails(authorName = "almahaja", JiraID = "DRSQA-12530", Products = {Product.AiOPS})
    public void DRSQA_12530_BAP_Change_Associated_With_Situation_Has_Single_CI(ITestContext context) throws Exception {
        CommonWaits commonWaits = new CommonWaits();

        log.info("*************** Start: DRSQA_12530_BA" +
                "P_Change_Associated_With_Situation_Has_Single_CI ****************");

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
        // ITSM Steps to create Change Request
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        context.setAttribute("DRSQA-12530_Driver", getDriver());
        context.setAttribute("DRSQA-12530_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12530_TestData");

        log.info("******Step 1: Login to ITSM **********");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Change Management > New Change");
        itsmFeatureMethods.navigateToPageFromApplication("Change Management", "New Change");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_NewChange", SolQAConstants.appITSM);

        log.info("Fill Change Data");
        String changeID = itsmFeatureMethods.createChange(itsmTestData);
        log.info("Created Change ID: " + changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeCreated", SolQAConstants.appITSM);

        log.info("Search for the created Change");
        itsmFeatureMethods.navigateToPageFromApplication("Change Management", "Search Change");
        itsmFeatureMethods.searchChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_SearchChange", SolQAConstants.appITSM);

        log.info("Assign Change Manager");
        itsmFeatureMethods.assignManager(itsmTestData);

        log.info("Change status to 'Request For Authorization'");
        itsmFeatureMethods.updateChangeStatus("Request For Authorization");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeStatusToRequestForAuthorization", SolQAConstants.appITSM);


        log.info("Change status to 'Request for Change'");
        itsmFeatureMethods.refreshITSMPage();
        itsmFeatureMethods.updateChangeStatus("Request For Change");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeStatusToPlanningInProgress", SolQAConstants.appITSM);

        itsmFeatureMethods.refreshITSMPage();
        log.info("Change status to 'Scheduled For Review'");
        itsmFeatureMethods.updateChangeStatus("Scheduled For Review");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeStatusToClosed", SolQAConstants.appITSM);

        itsmFeatureMethods.relateCIToChange(itsmTestData.get("RelatedCI"), itsmDriver);

        log.info("Change status to 'Scheduled For Approval'");
        itsmFeatureMethods.refreshITSMPage();
        itsmFeatureMethods.updateChangeStatus("Scheduled For Approval");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeStatusToScheduledForApproval", SolQAConstants.appITSM);

        log.info("Change status to 'Implementation In Progress'");
        itsmFeatureMethods.updateChangeStatus("Implementation In Progress");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeStatusToImplementationInProgress", SolQAConstants.appITSM);

        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateChangeStatus("Completed");
        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_ChangeStatusToCompleted", SolQAConstants.appITSM);

        itsmFeatureMethods.captureScreenshot("DRSQA-12530_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);

        itsmFeatureMethods.closeBrowser();


        // ITOM Steps Check Situation and BAP
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        String hostName = itsmTestData.get("RelatedCI");
        String serviceName = itsmTestData.get("Service");
        String changeSummary = itsmTestData.get("Summary");
        log.info("Service Name is: "+serviceName);
        String helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        String eventsCommonMsg = "Auto - LPAR experiencing sustained high CPU utilization of 75%_";

        String events_sent = eventsApi.streamRestApiEvents("PATROL_EV", 1, helixJwtToken, hostName,
                eventsCommonMsg + RandomStringUtils.randomAlphanumeric(5), "CRITICAL", "Pune", "");
        log.info("Sent Events: " + events_sent);
        JSONObject root = new JSONObject(events_sent);
        JSONObject inner = root.getJSONObject("0");
        JSONArray resourceIdArray = inner.getJSONArray("resourceId");
        List <String> value = new ArrayList<>();
        value.add(resourceIdArray.getString(0));

        events_sent = eventsApi.streamRestApiEvents("PATROL_EV", 1, helixJwtToken,hostName ,
                eventsCommonMsg + RandomStringUtils.randomAlphanumeric(5), "CRITICAL", "Pune", "");
        log.info("Sent Events: " + events_sent);
        root = new JSONObject(events_sent);
        inner = root.getJSONObject("0");
        resourceIdArray = inner.getJSONArray("resourceId");
        value.addAll(List.of(resourceIdArray.getString(0)));
        log.info("Sent EVent ID list is: "+value);

        Map<String, String> filters = new HashMap<>();
        filters.put("class", "Situation");
        filters.put("source_hostname", hostName);
        filters.put("msg",serviceName+" - "+eventsCommonMsg);
        filters.put("status","OPEN");
        helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        Boolean situationCreated;
        List<String> event_ids = List.of();
        int counter = 0;

        do {
            counter +=1;
            log.info("Checking if Situation is created with sent events.");
            commonWaits.waitForSometime(60000);
            event_ids = eventsApi.getEventIdentifierApi(filters, helixJwtToken);
            log.info("Run Event Query Op is: "+event_ids);
            situationCreated = !event_ids.isEmpty();
        } while(!situationCreated && counter <4);
        if (situationCreated) log.info("Situation got created.");
        else {
            log.info("Situation not created.. Failing test.");
            throw new AssertionError("Situation not created.. Failing test.");
        }
        String situationId = event_ids.get(0);
        log.info("Situation ID is: " + situationId);

        List<String> changeIds = aiops_lib.getChangeIdsAssociatedWithSituation(situationId, helixJwtToken);
        if (changeIds.isEmpty()) {
            log.info("No Change IDs associated with Situation.");
            throw new AssertionError("No Change IDs associated with Situation.");
        } else {
            log.info("Change IDs associated with Situation is: " + changeIds);
            if (changeIds.contains(changeID)) {
                log.info("Change ID is associated with Situation as expected.");
            } else {
                log.info("Change ID is not associated with Situation as expected.");
                throw new AssertionError("Change ID is not associated with Situation as expected.");
            }
        }

        // ITOM Steps to verify BAP

        itomDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        commonWaits.waitForPageLoad(itomDriver);
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        log.info("******Step 1: Login to ADE Portal **********");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);

        commonWaits.waitForPageLoad(itomDriver);

        log.info("------------- Navigate to AIOPS Application and go to Services page------>");

        String situationName = serviceName+" - "+eventsCommonMsg;

        aiOpsFeatureMethods.navigateToAIOps();
        aiOpsFeatureMethods.navigateToSituationPage();
        aiOpsFeatureMethods.searchAndOpenSituation(situationName);

        String expectedRootCauseSummary = "Change in System Configuration on Host "+hostName+" is the root cause of the situation.";
        log.info("Expected Root Cause Summary: " + expectedRootCauseSummary);
        log.info("Expected Change Tool Text: " + changeSummary);
        aiOpsFeatureMethods.verifyChangeBAP(expectedRootCauseSummary, changeSummary);
    }

    @Test(description = "[BAP]:Validate reoccuring summary generated for similar situation",groups={"bap"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-13341", Products = {Product.AiOPS})
    public void DRSQA_13341_BAP_Validate_Reccurring_Summary_generated_for_similar_situation(ITestContext context) throws Exception {
        readJson = new ReadJson();

        log.info("*************** Start: DRSQA_13341_BAP_Validate_Reccurring_Summary_generated_for_similar_situation ****************");

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

        String expected= "The output should mention that the situation occurred multiple times in the past at different times of day. It should identify a recurring pattern in the dates of occurrence, particularly towards the end of the month. ";//"The output should suggest possible related events or processes such as batch jobs, scheduled tasks, or peaks in user activity.";//"Should contain the best action plan";//"occur at different times of the day";//"situations occurring periodically.";

        String gptContext = "[\\\"situation should occur at different times in the past\\\", " +
                "\\\"there should be a pattern related to specific events\\\", " +
                "\\\"past dates of occurrence should be mentioned\\\"]";

        log.info("Create situation with events");
        readJson = new ReadJson();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12530_TestData");

        String hostName = itsmTestData.get("RelatedCI");
        String serviceName = itsmTestData.get("Service");
        log.info("Service Name is: " + serviceName);

        String helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        String eventsCommonMsg = "Auto - LPAR experiencing sustained high CPU utilization of 75%_";

        String events_sent = eventsApi.streamRestApiEvents("PATROL_EV", 1, helixJwtToken, hostName,
                eventsCommonMsg + RandomStringUtils.randomAlphanumeric(5), "CRITICAL", "Pune", "");
        log.info("Sent Events: " + events_sent);

        events_sent = eventsApi.streamRestApiEvents("PATROL_EV", 1, helixJwtToken, hostName,
                eventsCommonMsg + RandomStringUtils.randomAlphanumeric(5), "CRITICAL", "Pune", "");
        log.info("Sent Events: " + events_sent);

        log.info("Login to ITOM.");
        launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), SolQAConstants.appITOM);
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        bhomFeatureMethods = new BhomFeatureMethods();

        log.info("Go to AiOps.");
        aiOpsFeatureMethods.openAiOps();

        log.info("Navigate to Situations.");
        aiOpsFeatureMethods.navigateToSituationPage();
        aiOpsFeatureMethods.apply30DaysFilter();

        log.info("Open similar situation and verify if Helix GPT shows the message after clicking Reoccurring." );
        aiOpsFeatureMethods.openSimilarSituation();
        aiOpsFeatureMethods.verifyHelixGPTMessage(gptContext, expected);

        log.info("Go to BHOM. Close events based on unique message.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(eventsCommonMsg, "");
    }
    @Test(description = "DRSQA_13103_Verify_Ask_HelixGPT_BAR_questions_for_a_situation",groups={"bap"})
    @SolqaDetails(authorName = "almahaja", JiraID = "DRSQA-13103", Products = {Product.AiOPS})
    public void DRSQA_13103_Verify_Ask_HelixGPT_BAR_questions_for_a_situation(ITestContext context) throws Exception {
        CommonWaits commonWaits = new CommonWaits();

        log.info("*************** Start: DRSQA_13103_Verify_Ask_HelixGPT_BAR_questions_for_a_situation ****************");
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

        itomDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        commonWaits.waitForPageLoad(itomDriver);
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        log.info("******Step 1: Login to ADE Portal **********");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);

        commonWaits.waitForPageLoad(itomDriver);

        log.info("------------- Navigate to AIOPS Application and go to Multi Service Situations page------>");

        aiOpsFeatureMethods.navigateToAIOps();
        aiOpsFeatureMethods.navigateToSituationPage();
        aiOpsFeatureMethods.openFirstSimilarSituationWithIncident();
        aiOpsFeatureMethods.verifyHelixGptMenus();
        aiOpsFeatureMethods.verify5Whys();

    }

    @Test(description = "[BAP]: Verify BAP tools to verify steps are as per Incident resolution and validate Change tool",groups={"bap"})
    @SolqaDetails(authorName = "almahaja", JiraID = "DRSQA-13104", Products = {Product.AiOPS})
    public void DRSQA_13104_BAP_verify_steps_are_as_per_Incident_resolution_and_validate_Change_tool(ITestContext context) throws Exception {

        CommonWaits commonWaits = new CommonWaits();
        readJson = new ReadJson();

        log.info("*************** Start: DRSQA_13104_BAP_verify_steps_are_as_per_Incident_resolution_and_validate_Change_tool ****************");
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

        org.json.simple.JSONObject testData = (org.json.simple.JSONObject) readJson.readSimpleJson("resources/ITOM/AIOPS/testSpecificServiceModelsAndCIs.json").get("DRSQA_13104");
        log.info(String.valueOf(testData));
        String serviceName = testData.get("ServiceModel").toString();
        List<String> eventsMessages = (List<String>) testData.get("EventMsgs");
        String host = testData.get("CI").toString();
        log.info("Service Name is: "+serviceName);
        String helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        String eventsCommonMsg = "High CPU utilization of 75%_"+RandomStringUtils.randomAlphanumeric(5);
        for (String eventMsg : eventsMessages) {
            log.info("Sending event for host: "+host+" with message: "+eventMsg);
            String events_sent = eventsApi.streamRestApiEvents("PATROL_EV", 1, helixJwtToken, host, eventMsg, "CRITICAL", "Pune", "");
            log.info("Sent Event: " + events_sent);
        }

        String situationName = eventsMessages.get(0);
        Map<String, String> filters = new HashMap<>();
        filters.put("class", "Situation");
        filters.put("msg", situationName);
        filters.put("status","OPEN");
        helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        Boolean situationCreated;
        List<String> situationIds = List.of();
        int counter = 0;
        do {
            counter +=1;
            log.info("Checking if Situation is created with sent events.");
            commonWaits.waitForSometime(60000);
            situationIds = eventsApi.getEventIdentifierApi(filters, helixJwtToken);
            log.info("Run Event Query Op is: "+situationIds);
            situationCreated = !situationIds.isEmpty();
        } while(!situationCreated && counter <4);
        if (situationCreated) log.info("Situation got created.");
        else {
            log.info("Situation not created.. Failing test.");
            throw new AssertionError("Situation not created.. Failing test.");
        }
        String situationId = situationIds.get(0);

        // Search Incident ID associated with Situation
        String incidentId = aiops_lib.getIncidentIdsAssociatedWithSituation(situationId, helixJwtToken);
        String resolutionNote = "Added 2 CPU cores to the LPARs to handle the increased load.";
        // Resolve the Incident in SmartIT
        itomDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        commonWaits.waitForPageLoad(itomDriver);
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        log.info("******Step 1: Loin to ADE Portal **********");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        commonWaits.waitForPageLoad(itomDriver);
        log.info("------------- Navigate to AIOPS Application and go to Situations page------>");
        aiOpsFeatureMethods.navigateToAIOps();
        aiOpsFeatureMethods.navigateToSituationPage();
        log.info("------------- Open Situations ------>");
        aiOpsFeatureMethods.searchAndOpenSituation(situationName);
        aiOpsFeatureMethods.searchSituationAndOpenIncidentAssociated(situationName, incidentId);
        aiOpsFeatureMethods.switchToSmartIt();
        smartITFeatureMethodsPage.resolveIncident(resolutionNote);

        eventsApi.closeEventApi(situationId, "Closed Incident - "+resolutionNote, helixJwtToken);

        long epochEndTimeLong = System.currentTimeMillis();                // current time in ms
        long epochStartTimeLong = epochEndTimeLong - (10 * 60 * 1000);

        String epochEndTime = String.valueOf(epochEndTimeLong);
        String epochStartTime = String.valueOf(epochStartTimeLong);
        helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        if (aiops_lib.runRanker(epochStartTime, epochEndTime, helixJwtToken)) {
            log.info("Ranker ran successfully before creating situation.");
        }
        else {
            log.info("Ranker did not run successfully before creating situation. Failing the test.");
            throw new AssertionError("Ranker did not run successfully before creating situation. Failing the test.");
        }

        // Generate Situation again
        for (String eventMsg : eventsMessages) {
            log.info("Sending event for host: "+host+" with message: "+eventMsg);
            String events_sent = eventsApi.streamRestApiEvents("PATROL_EV", 1, helixJwtToken, host, eventMsg, "CRITICAL", "Pune", "");
            log.info("Sent Event: " + events_sent);
        }

        helixJwtToken = api.getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY);
        situationCreated = false;
        counter = 0;
        do {
            counter +=1;
            log.info("Checking if Situation is created with sent events.");
            commonWaits.waitForSometime(60000);
            situationIds = eventsApi.getEventIdentifierApi(filters, helixJwtToken);
            log.info("Run Event Query Op is: "+situationIds);
            situationCreated = !situationIds.isEmpty();
        } while(!situationCreated && counter <4);
        if (situationCreated) log.info("Situation got created.");
        else {
            log.info("Situation not created.. Failing test.");
            throw new AssertionError("Situation not created.. Failing test.");
        }
        situationId = situationIds.get(0);

        // Verify Situation is saturated
        Boolean situationSaturated;
        counter = 0;
        do {
            counter +=1;
            log.info("Checking if Situation is saturated.");
            commonWaits.waitForSometime(60000);
            situationSaturated = aiops_lib.verifySituationSaturated(situationId, helixJwtToken);
            log.info("Situation Saturated?: "+situationSaturated);
        } while(!situationSaturated && counter < 5);
        if (!situationSaturated) {
            log.info("Situation not saturated. Failing the test");
            throw new AssertionError("Situation not saturated.. Failing test.");
        }

        // UI Validation
        itomDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        commonWaits.waitForPageLoad(itomDriver);
        String expectedRecommendationSubSTring = "Automate the addition of 2 CPU cores";
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        log.info("******Step 1: Loin to ADE Portal **********");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        commonWaits.waitForPageLoad(itomDriver);
        log.info("------------- Navigate to AIOPS Application and go to Situations page------>");
        aiOpsFeatureMethods.navigateToAIOps();
        aiOpsFeatureMethods.navigateToSituationPage();
        log.info("------------- Open Situations ------>");
        aiOpsFeatureMethods.searchAndOpenSituation(situationName);
        log.info("------ Verify the Similar situation BAP ------>");
        aiOpsFeatureMethods.verifyBAPWithNoRecommendations(expectedRecommendationSubSTring);

    }
    
    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
    }
}