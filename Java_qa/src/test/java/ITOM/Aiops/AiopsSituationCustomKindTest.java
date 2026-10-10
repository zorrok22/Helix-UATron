package ITOM.Aiops;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import util.SolqaDetails;
import util.SolqaLogger;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import java.io.IOException;
import java.util.*;
import util.*;


/**
 * Java translation (API-first) of Robot test "create-service-configure-for-prediction-and-runAutomation".
 * This focuses on the core REST steps without invoking Robot Framework.
 * NOTE: Several Robot keywords map to REST calls shown here; any missing mappings are marked TODO.
 */
public class AiopsSituationCustomKindTest {

    private static final SolqaLogger log = new SolqaLogger(AiopsSituationCustomKindTest.class.getName());

    // Endpoints used in the Robot keywords
    Aiops aiopsApiCore;
    HMEvents hm_events;
    CommonWaits commonWait;
    CommonMethods commonmethod;
    AiopsPrediction aiopsPrediction;
    AiopsDeepRCA aiopsDeepRCA;
    private SoftAssert softAssert = null;
    private String jwtToken = null;
    AiopsSituation aiopsSituation;
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();
    String eventClass = "NAGIOS_EV1";
    String location = "Pune";
    String event_tag = "";
   

    @BeforeMethod(alwaysRun = true)
    public void setUpHelpers() {
        aiopsApiCore = new Aiops();
        hm_events = new HMEvents();
        commonWait = new CommonWaits();
        commonmethod = new CommonMethods();
        aiopsPrediction = new AiopsPrediction();
        aiopsDeepRCA = new AiopsDeepRCA();
        aiopsSituation = new AiopsSituation();
    }

    @Test(description = "Validate situation create update flow for service model with custom node kind", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "19669", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_19669_Validate_situation_create_update_flow_for_service_model_with_custom_node_kind(ITestContext context) throws Exception {
        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(null);
        int testcase_number = 1;
        String sheetName = "customkind";
        log.info("Starting DRSQA_19669 scenario");
        log.info("get the tenant host Name");
        List<Map<String, Object>> logagentTC = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, sheetName);
        Map<String, Object> row = logagentTC.get(0);
        String modelName = Objects.toString(row.get("serviceName"), "");
        String infrastructureNode = Objects.toString(row.get("infrastructureNode"), "");
        String networkNode = Objects.toString(row.get("networkNode"), "");
        String applicationNode = Objects.toString(row.get("applicationNode"), "");
        String impactCi = Objects.toString(row.get("Impact_CI"), "");
        String[] host_list = impactCi.isEmpty() ? new String[0] : impactCi.split(",");

        log.info("Check Model Exist");
        JSONObject model = aiopsApiCore.searchAndGetServiceDetails(modelName, jwtToken);
        if(model == null){
            log.error("Model not found");
            Assert.fail("Model not found");
            return;
        }

        log.info("Close all event on service " + modelName);
        aiopsSituation.CloseAllEventOnService(modelName);

        log.info("Close all Event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);


        log.info("Get health score");
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);

        log.info("Configure Update situation configuration as below \"configuration\": {  \"show_policy_based_situations\": \"\",    \"infrastructure_custom_nodes\": \"ExternalElement\",   \"network_custom_nodes\": \"\",  \"application_custom_nodes\" : \"\"}");
        String situationId = aiopsSituation.getSituationId();
        log.info("Situation ID: " + situationId);

        String updateSituationKind = "updatesituationconfigurationCustomKind.json";
        boolean updateSituationKindResult = aiopsSituation.updateSituationConfigurationKind(updateSituationKind, situationId, "true", infrastructureNode, networkNode, applicationNode);
        if(!updateSituationKindResult){
            log.error("Failed to update situation kind");
            Assert.fail("Failed to update situation kind");
        }
        log.info("Situation kind updated successfully");

        log.info("Create event for service " + modelName);
        String eventSeverity = "MINOR";
        int eventcount = 0;
        for(String host : host_list) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "CPU utilization of " + host + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, 1, jwtToken, host, msg, eventSeverity, location, event_tag);
            eventcount++;
        }

        log.info("Wait for situation creation");
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            softAssert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Check Health Score");
        healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);
        if(Integer.parseInt(healthScore.get("actualHealth")) == 100){
            log.error("Health Score is 100");
            softAssert.fail("Health Score is 100");
        }
        

        log.info("Close all event on service " + modelName);
        aiopsSituation.CloseAllEventOnService(modelName);
        log.info("Wait for health score to be 100");
        if (!aiopsApiCore.waitForHealthScoreExpectedValue(95, modelName, jwtToken, 2)) {
            softAssert.fail("Health Score did not reach 100 within 3 minutes.");
        }

        healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);
        if(Integer.parseInt(healthScore.get("actualHealth")) != 100){
            log.error("Health Score is not 100");
            softAssert.fail("Health Score is not 100");
        }

        updateSituationKindResult = aiopsSituation.updateSituationConfigurationKind(updateSituationKind, situationId, "false", infrastructureNode, networkNode, applicationNode);
        if(!updateSituationKindResult){
            log.error("Failed to update situation kind");
            softAssert.fail("Failed to update situation kind");
        }

        softAssert.assertAll();
    }

}