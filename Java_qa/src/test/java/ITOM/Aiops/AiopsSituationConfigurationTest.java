package ITOM.Aiops;

import util.CommonWaits;
import util.SolQAConstants;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.asserts.SoftAssert;
import com.fasterxml.jackson.core.JsonProcessingException;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;

public class AiopsSituationConfigurationTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    public static final String event_tag = "";

    private SoftAssert softAssert = null;
    private static String eventClass = "NAGIOS_EV1";
    private static String mlSheetName = "mlcheck";
    private static String location = "Pune";
    String jwtToken = null;
    HMEvents hm_events = null;
    CommonWaits commonwait = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    String getSituationConfigurationEndPoint = "/aiops-config/api/v1.1/situation_configurations/search";
    private static final String base_url =  config.get("ADE_PLATFORM_URL");
    private static final String aiops_url =  base_url + "/aiops/#/configurations/manage-situations";
    private static final String loganalytics_app_xpath = "//div[@id=\"BMC Helix Log Analytics\"]";
    private static final String multiserviceHeaderXpath = "//h5[contains(.,'Multi-service Situations')]";
    private static final String multiserviceToggleXpath = "//input[@id='multiservice-situations']";
    private static final String multiserviceToggleCheckedXpath = "//input[@id='multiservice-situations'  and @aria-checked='true']";
    private static final String multiserviceToggleUncheckedXpath = "//input[@id='multiservice-situations'  and @aria-checked='false']";
    private static final String multiserviceConfirmationButtonXpath = "//button[@id='confirmation-yes-btn']";
    private static final String multiserviceSaveButtonXpath = "//button[@id='situation-save-changes']";
    private int testcase_number = 6;
    CommonMethods commonmethod = null;
    LogAnalyticsPage logAnalyticsPage = null;
    String sourceKey = null;
    String destinationKey = null;
    String childlist = null;
    Boolean propagationStatus = false;
    boolean alarmPolicyCreated = false;
    AiopsPrediction aiopsPrediction = null;
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();


    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Cleaning up after the test...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Close all open Event");
        log.info("CLose all open event on Nagios class");
        try {
            if(alarmPolicyCreated){
                aiopsApiCore.deleteServiceModel("ML-UNAVAILABLE", jwtToken);
                hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
            }
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            if(propagationStatus){
                aiopsSituation.validateParentLevelHealthStatusPropagationOverride(sourceKey, destinationKey, childlist, "enabled", jwtToken);
            }
        } catch (Exception e) {
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
        commonmethod = new CommonMethods();
        commonwait = new CommonWaits();
        softAssert = new SoftAssert();
        logAnalyticsPage = new LogAnalyticsPage();
        aiopsDeepRCA = new AiopsDeepRCA();
        aiopsPrediction = new AiopsPrediction();
        propagationStatus = false;
        alarmPolicyCreated = false;
        jwtToken = commonmethod.refreshJwtToken(null); 
        try {
            log.info("Close all open Event");
            hm_events.closeAllEventByClass("ALARM", jwtToken);
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

    private void updateServiceModelprofile(String serviceId, boolean updateGlobalHealthProfile, int[] eventThresholds) throws IOException, InterruptedException {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String end_point = "/service-management/api/v1.0/service/get";
        logAnalyticsPage.Send_RestCall("POST", end_point, new String[]{"200","{\"modelKey\":\""+ serviceId + "\",\"includeDownstream\":true,\"includeUpstream\":true}"});
        JSONObject serviceModel = logAnalyticsPage.jsonObject.getJSONArray("nodes").getJSONObject(0);
        String id = serviceModel.getString("id");
        String compatibleOnly = serviceModel.getString("compatibleOnly");
        JSONArray associatedElements = serviceModel.getJSONArray("associatedElements");

        // Build the increments JSON structure
        JSONObject increments = new JSONObject();
        JSONArray nodesArr = new JSONArray();
        JSONObject nodeObj = new JSONObject();
        nodeObj.put("id", id);
        nodeObj.put("compatibleOnly", compatibleOnly);

        JSONArray assocArr = new JSONArray();
        // health_processing
        for (int i = 0; i < associatedElements.length(); i++) {
            JSONObject assocElem = associatedElements.getJSONObject(i);
            String type = assocElem.getString("type");
            if (type.equals("health_processing")) {
                if (updateGlobalHealthProfile) {
                    // Create health_processing with empty scoreMapping
                    JSONObject healthProcessing = new JSONObject();
                    healthProcessing.put("type", "health_processing");

                    JSONArray elements = new JSONArray();
                    JSONObject element = new JSONObject();
                    element.put("element_type", "health_processing");
                    element.put("statusMapping", new JSONObject());
                    element.put("selectors", new JSONArray());
                    element.put("scoreMapping", new JSONObject());
                    element.put("propagation", "enabled");
                    element.put("globalConfigurationBalancingRules", "disabled");
                    elements.put(element);

                    healthProcessing.put("elements", elements);
                    assocArr.put(healthProcessing);
                } else {
                    // Create health_processing with event threshold values from array
                    JSONObject healthProcessing = new JSONObject();
                    healthProcessing.put("type", "health_processing");

                    JSONArray elements = new JSONArray();
                    JSONObject element = new JSONObject();
                    element.put("element_type", "health_processing");
                    element.put("statusMapping", new JSONObject());
                    element.put("selectors", new JSONArray());

                    // Create scoreMapping with event and metric thresholds
                    JSONObject scoreMapping = new JSONObject();

                    // Event thresholds from array [critical, major, minor, warning]
                    JSONObject event = new JSONObject();
                    event.put("critical", new JSONObject().put("threshold", eventThresholds[0]));
                    event.put("major", new JSONObject().put("threshold", eventThresholds[1]));
                    event.put("minor", new JSONObject().put("threshold", eventThresholds[2]));
                    event.put("warning", new JSONObject().put("threshold", eventThresholds[3]));

                    // Metric thresholds (fixed values)
                    JSONObject metric = new JSONObject();
                    metric.put("critical", new JSONObject().put("threshold", 20));
                    metric.put("major", new JSONObject().put("threshold", 16));
                    metric.put("minor", new JSONObject().put("threshold", 12));
                    metric.put("warning", new JSONObject().put("threshold", 8));

                    scoreMapping.put("event", event);
                    scoreMapping.put("metric", metric);

                    element.put("scoreMapping", scoreMapping);
                    element.put("propagation", "enabled");
                    element.put("globalConfigurationBalancingRules", "disabled");
                    elements.put(element);

                    healthProcessing.put("elements", elements);
                    assocArr.put(healthProcessing);
                }
            } else if (type.equals("metric_group")) {
                assocArr.put(assocElem);
            } else if (type.equals("event_configuration")) {
                // Replace elements with required event_rule
                assocArr.put(assocElem);
            }
        }
        nodeObj.put("associatedElements", assocArr);
        nodesArr.put(nodeObj);
        increments.put("links", new JSONArray());
        increments.put("nodes", nodesArr);

        JSONObject result = new JSONObject();
        result.put("increments", increments);
        // Return the result variable
        // You can return or use 'result' as needed
        // Example: return result;
        // For now, just log it
        log.info("updateEventProfile increments JSON: " + result.toString());
    }

    private void runTestScenario(String[] ci_list, String[] bussness_service) throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        int no_of_event_per_node = 1;
        String randomStr = RandomStringUtils.randomAlphanumeric(7);

        String msg = "Memory utilization of " + ci_list[0] + " exceed threshold of 80% " + randomStr;
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[0], msg, "WARNING", location, event_tag);
        log.info("Waiting for health score expected value 90");
        boolean healthScoreExpected = aiopsApiCore.waitForHealthScoreExpectedValue(90, bussness_service[0], jwtToken, 3);
        Map<String, String> bsHealthScore = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
        log.info("Health Score for business service" + bussness_service[0] + "Health Score " + bsHealthScore.get("health"));
        if (Integer.parseInt(bsHealthScore.get("health")) > 90) {
            log.error("Health Score not correct for business service " + bussness_service[0] + " expected 100 actual Health Score " + bsHealthScore.get("health"));
            softAssert.fail("Health Score not correct for business service " + bussness_service[0] + " expected 100 actual Health Score " + bsHealthScore.get("health"));
        }


        log.info("Close all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(91, bussness_service[0], jwtToken,2);
        log.info("Waiting for health score expected value 100");
        msg = "Memory utilization of " + ci_list[0] + " exceed threshold of 80% " + randomStr;
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[0], msg, "MINOR", location, event_tag);
        aiopsApiCore.waitForHealthScoreExpectedValue(80, bussness_service[0], jwtToken,2);
        aiopsApiCore.waitForHealthScoreExpectedValue(90, bussness_service[0], jwtToken,3);
        bsHealthScore = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
        log.info("Service Data 85 " +  bsHealthScore.toString());
        log.info("Health Score for business service" + bussness_service[0] + "Health Score " + bsHealthScore.get("health"));
        if (Integer.parseInt(bsHealthScore.get("health")) > 85){
            log.error("Health Score not correct for business service " + bussness_service[0] + " expected 85 actual Health Score " + bsHealthScore.get("health"));
            softAssert.fail("Health Score not correct for business service " + bussness_service[0] + " expected 85 actual Health Score " + bsHealthScore.get("health"));
        }

        log.info("CLose all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(91, bussness_service[0], jwtToken,2);

        msg = "Memory utilization of " + ci_list[0] + " exceed threshold of 80% " + randomStr;
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[0], msg, "CRITICAL", location, event_tag);
        aiopsApiCore.waitForHealthScoreExpectedValue(85, bussness_service[0], jwtToken,3);
        bsHealthScore = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
        log.info("Health Score for business service" + bussness_service[0] + "Health Score " + bsHealthScore.get("health"));
        if (Integer.parseInt(bsHealthScore.get("health")) > 75){
            log.error("Health Score not correct for business service " + bussness_service[0] + " expected 75 actual Health Score " + bsHealthScore.get("health"));
            softAssert.fail("Health Score not correct for business service " + bussness_service[0] + " expected 75 actual Health Score " + bsHealthScore.get("health"));
        }

        log.info("Close all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        log.info("Waiting for health score expected value 100");
        healthScoreExpected = aiopsApiCore.waitForHealthScoreExpectedValue(90, bussness_service[0], jwtToken, 2);
        bsHealthScore = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
        log.info("Health Score for business service" + bussness_service[0] + "Health Score " + bsHealthScore.get("health") + " Expected 100");
        if (!healthScoreExpected) {
            log.error("Health Score not correct for business service " + bussness_service[0] + " expected 100 actual Health Score " + bsHealthScore.get("health") + " Expected 100");
            softAssert.fail("Health Score not correct for business service " + bussness_service[0] + " expected 100 actual Health Score " + bsHealthScore.get("health") + " Expected 100");
        }

    }


    @Test(description = "[Global health profile support] To validate that the Global Health Score & Status Configurations are applied and Service-level overrides are honored.", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15263", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15263_validate_Global_Health_Score_Status_Configurations_are_applied_and_Service_level_overrides_are_honored(ITestContext context)
            throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Tenant Name " + base_url);
        String globalHealthProfileFile = "AiopsGlobalHealthProfile.json";


        hm_events.CloseAllOpenEvent(jwtToken);
        commonwait.waitForSometime(5000);
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        log.info("Close all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        log.info("Check Service Model Exist ");
        String[] businessservice = row_data.get(0).get("Business Name").toString().split((","));
        aiopsApiCore.searchAndGetServiceDetails(businessservice[0],jwtToken);
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        String serviceKey = healthMap.get("key");
        log.info("Close all open event on Service " + businessservice[0]);
        aiopsSituation.CloseAllEventOnService(businessservice[0]);
        log.info("Reset service health score for service " + businessservice[0]);
        aiopsApiCore.resetServiceHealthScore(healthMap.get("key"), jwtToken);
        String jsonContent = aiopsApiCore.readJSon(globalHealthProfileFile);
        aiopsApiCore.updateSituationConfiguration(jsonContent, jwtToken);


        commonwait.waitForSometime(5000);
        log.info("Get init service health score");
        List<Map<String, String>> businessHealthList = new ArrayList<>();
        for(String service_name: businessservice){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service" + service_name + "Health Score " + healthScore.get("actualHealth"));
            businessHealthList.add(healthScore);
            if (Integer.parseInt(healthScore.get("actualHealth")) != 100){
                Assert.fail("Start of test case Health Score not correct for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
            }
        }

        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));



        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        String[] arry = {"200", ""};
        try {
            updateServiceModelprofile(serviceKey, true, new int[]{10, 8, 6, 4});
            log.info("Updated service to take Global parameter");

            String updateconfigfile = "aiopsservicehealthprofilesetLocal.json";
            aiopsSituation.updateSituationConfiguration(updateconfigfile, serviceKey, jwtToken);

            log.info(String.valueOf(logAnalyticsPage.jsonObject));
            commonwait.waitForSometime(5000);
            runTestScenario(ci_list, businessservice);


        }finally {
            // Reset to default configuration
            String updateconfigfile = "aiopsservicehealthprofilesetGlobal.json";
            aiopsSituation.updateSituationConfiguration(updateconfigfile, serviceKey, jwtToken);

            log.info("Update service to take Global parameter");
            updateServiceModelprofile(serviceKey, true, new int[]{10, 8, 6, 4});
        }

        softAssert.assertAll();
    }

    @Test(description = "[Global health profile support] To validate that the Global Health Score & Status Configurations are applied correctly across all services", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15262", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15262_validate_Global_Health_Score_Status_Configurations_are_applied_correctly_across_all_services(ITestContext context)
            throws IOException, InterruptedException {

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Tenant Name " + base_url);
        String globalHealthProfileFile = "AiopsGlobalHealthProfile.json";

        hm_events.CloseAllOpenEvent(jwtToken);
        commonwait.waitForSometime(5000);
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        log.info("Close all open event on Nagios class");
        hm_events.CloseAllOpenEvent(jwtToken);
        commonwait.waitForSometime(5000);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        log.info("Check Service Model Exist");
        String[] bussness_service = row_data.get(0).get("Business Name").toString().split((","));
        aiopsApiCore.searchAndGetServiceDetails(bussness_service[0],jwtToken);
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
        String serviceKey = healthMap.get("key");
        log.info("Close all open event on Service " + bussness_service[0]);
        aiopsSituation.CloseAllEventOnService(bussness_service[0]);
        log.info("Reset service health score for service " + bussness_service[0]);
        aiopsApiCore.resetServiceHealthScore(healthMap.get("key"), jwtToken);
        commonwait.waitForSometime(5000);
        log.info("Get init service health score");
        List<Map<String, String>> businessHealthList = new ArrayList<>();
        for(String service_name: bussness_service){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
            businessHealthList.add(healthScore);
            if (Integer.parseInt(healthScore.get("actualHealth")) != 100){
                Assert.fail("Health Score not correct for business service" + service_name + "Health Score " + healthScore.get("actualHealth"));
            }
        }

        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        String jsonContent = aiopsApiCore.readJSon(globalHealthProfileFile);
        JSONObject configJson = new JSONObject(jsonContent);
        String[] arry = {"200", ""};

        try {
            updateServiceModelprofile(serviceKey, false, new int[]{25, 20, 15, 10});
            commonwait.waitForSometime(5000);
            log.info("Updated service to take Global parameter");

            // Update threshold values for elements[0].scoreMapping.event
            JSONObject elements0 = configJson.getJSONArray("globalHealthConfiguration")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0);
            JSONObject scoreMapping = elements0.getJSONObject("scoreMapping");
            JSONObject eventMapping = scoreMapping.getJSONObject("event");
            eventMapping.getJSONObject("critical").put("threshold", 35);
            eventMapping.getJSONObject("major").put("threshold", 30);
            eventMapping.getJSONObject("minor").put("threshold", 25);
            eventMapping.getJSONObject("warning").put("threshold", 20);
            arry[1] = configJson.toString();
            log.info(configJson.toString());
            aiopsApiCore.updateSituationConfiguration(configJson.toString(), jwtToken);
            log.info(String.valueOf(logAnalyticsPage.jsonObject));
            commonwait.waitForSometime(5000);
            log.info("Running test scenario");
            runTestScenario(ci_list, bussness_service);

        } finally {
            // Reset to default configuration
            String updateconfigfile = "aiopsservicehealthprofilesetGlobal.json";
            aiopsSituation.updateSituationConfiguration(updateconfigfile, serviceKey, jwtToken);

            log.info("Update service to take Global parameter");
            updateServiceModelprofile(serviceKey, true, new int[]{10, 8, 6, 4});
        }

        softAssert.assertAll();

    }

    @Test(description = "Verify enabling and disabling multi-service situation option from UI", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15725", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15725_Verify_enabling_and_disabling_multi_service_situation_option_from_UI(ITestContext context) throws IOException, InterruptedException{
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Refreshed JWT token");

        log.info("Prepared search endpoint " + getSituationConfigurationEndPoint);
        logAnalyticsPage.Send_RestCall("POST", getSituationConfigurationEndPoint, new String[]{"200", "{}"});
        log.info("Fetched situation configurations");
        JSONObject jsonobject = logAnalyticsPage.jsonObject;
        log.info("Stored configuration response");
        String situationId = jsonobject.getJSONArray("situations").getJSONObject(0).getString("id");
        log.info("Initialized situation id store");
        String updatedSituationPayload = null;
        log.info("Initialized payload store");
        aiopsApiCore.settenantfeaturesresponse(null);
        boolean pastStatus = aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary", jwtToken);
        boolean originalCapability = pastStatus;
        log.info("Captured multiservice flag status " + pastStatus);
        boolean enableExternalCiHealthCalculation = false;
        log.info("Defaulted external CI health flag");
        if (pastStatus){
            aiopsApiCore.settenantfeaturesresponse(null);
            enableExternalCiHealthCalculation = aiopsApiCore.getTenantFeatureFlagStatus("enable-external-ci-health-calculation", jwtToken);
            log.info("Captured external CI health flag " + enableExternalCiHealthCalculation);
        }

        try{
            boolean desiredStatus = !pastStatus;
            aiopsSituation.updateMultiServiceSituationConfiguration(desiredStatus, situationId, jwtToken);
            commonwait.waitForSometime(5000);
            aiopsApiCore.settenantfeaturesresponse(null);
            boolean currentStatus = aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary", jwtToken);
            log.info("Post-update feature flag status " + currentStatus);
            if (currentStatus != desiredStatus){
                softAssert.fail("Multi-service Situations not updated to desired state");
                log.error("Assertion failure recorded for unchanged status");
            }
        }finally{
            aiopsSituation.updateMultiServiceSituationConfiguration(pastStatus, situationId, jwtToken);
        }
        softAssert.assertAll();
        log.info("Soft asserts completed");
    }

    @Test(description = "Validate parent-level Health status propagation override", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "17945", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17945_Validate_parent_level_Health_status_propagation_override(ITestContext context) throws IOException, InterruptedException{
        int testcase_number = 10;
        String mlSheetName = "mlcheck";

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        
        log.info("Check Require feature flag is enabled");
        if(!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary", jwtToken)){
            log.info("ml-situation-across-service-boundary feature flag is not enabled; skipping test as passed.");
            return;
        }
        
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String[] businessservice = row_data.get(0).get("Business Name").toString().split((","));
        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));

        
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        String destinationServiceKey = healthMap.get("key");
        log.info("Close all open event on Service " + businessservice[0]);
        aiopsSituation.CloseAllEventOnService(businessservice[0]);
        log.info("Reset service health score for service " + businessservice[0]);
        aiopsApiCore.resetServiceHealthScore(healthMap.get("key"), jwtToken);
        commonwait.waitForSometime(5000);
        log.info("Get init service health score");

        Map<String, String> SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[1], jwtToken);
        String SourceServiceKey = SourceHealthMap.get("key");
        log.info("Close all open event on Service " + businessservice[0]);
        aiopsSituation.CloseAllEventOnService(businessservice[0]);
        log.info("Reset service health score for service " + businessservice[0]);
        aiopsApiCore.resetServiceHealthScore(healthMap.get("key"), jwtToken);
        commonwait.waitForSometime(5000);

        Map<String, String> topologyMap = aiopsApiCore.aiopsGetCITopology(destinationServiceKey, jwtToken);
        String ci0 = ci_list[0].trim();
        String impactingTopologyKey = null;
        for (String topoKey : topologyMap.keySet()) {
            if (topoKey.endsWith("_kind")) {
                continue;
            }
            if (topoKey.contains(ci0)) {
                impactingTopologyKey = topoKey;
                break;
            }
        }
        Assert.assertNotNull(impactingTopologyKey, "No topology node name containing Impact Element: " + ci0);
        childlist = "";
        Assert.assertTrue(
                aiopsSituation.validateParentLevelHealthStatusPropagationOverride(SourceServiceKey, destinationServiceKey, childlist,"enabled", jwtToken),
                "Failed to disable parent-level health propagation");
        sourceKey = SourceServiceKey;
        destinationKey = destinationServiceKey;
        propagationStatus = true;

        int no_of_event_per_node = 1;
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        String msg = "Memory utilization of " + impactingTopologyKey + " exceed threshold of 80% " + randomStr;
        log.info("Sending impact event for topology node: " + impactingTopologyKey);
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactingTopologyKey, msg, "CRITICAL", location, event_tag);

        // With propagation enabled, child impact should surface on a parent: impactingSubService == 1 (not 0).
        log.info("Check after all event is closed Parent should indicate no sub service is impacted.");
        boolean impactPropagatedToParent = false;
        for (int i = 0; i < 12; i++) {
            SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[1], jwtToken);
            if ("1".equals(SourceHealthMap.get("impactingSubService"))) {
                impactPropagatedToParent = true;
                break;
            }
            log.info("Check another parent is impacted");
            SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[2], jwtToken);
            if ("1".equals(SourceHealthMap.get("impactingSubService"))) {
                impactPropagatedToParent = true;
                break;
            }
            commonwait.waitForSometime(5000);
        }
        
        Assert.assertTrue(impactPropagatedToParent,
                "Expected impactingSubService to be 1 (child impact propagated to parent); last value: " + SourceHealthMap.get("impactingSubService"));

    }


    @Test(description = "Validate health status propagation by disabling a specific child service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "17944", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17944_Validate_health_status_propagation_by_disabling_a_specific_child_service(ITestContext context) throws IOException, InterruptedException{
        int testcase_number = 10;
        String mlSheetName = "mlcheck";

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        
        log.info("Check Require feature flag is enabled");
        if(!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary", jwtToken)){
            log.info("ml-situation-across-service-boundary feature flag is not enabled; skipping test as passed.");
            return;
        }
        
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String[] businessservice = row_data.get(0).get("Business Name").toString().split((","));
        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));

        
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        String destinationServiceKey = healthMap.get("key");
        log.info("Close all open event on Service " + businessservice[0]);
        aiopsSituation.CloseAllEventOnService(businessservice[0]);
        log.info("Reset service health score for service " + businessservice[0]);
        aiopsApiCore.resetServiceHealthScore(healthMap.get("key"), jwtToken);
        commonwait.waitForSometime(5000);
        log.info("Get init service health score");

        Map<String, String> SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[1], jwtToken);
        String SourceServiceKey = SourceHealthMap.get("key");
        log.info("Close all open event on Service " + businessservice[0]);
        aiopsSituation.CloseAllEventOnService(businessservice[0]);
        log.info("Reset service health score for service " + businessservice[0]);
        aiopsApiCore.resetServiceHealthScore(healthMap.get("key"), jwtToken);
        commonwait.waitForSometime(5000);

        Map<String, String> topologyMap = aiopsApiCore.aiopsGetCITopology(destinationServiceKey, jwtToken);
        String ci0 = ci_list[0].trim();
        String impactingTopologyKey = null;
        for (String topoKey : topologyMap.keySet()) {
            if (topoKey.endsWith("_kind")) {
                continue;
            }
            if (topoKey.contains(ci0)) {
                impactingTopologyKey = topoKey;
                break;
            }
        }
        Assert.assertNotNull(impactingTopologyKey, "No topology node name containing Impact Element: " + ci0);
        childlist = "";
        Assert.assertTrue(
                aiopsSituation.validateParentLevelHealthStatusPropagationOverride(SourceServiceKey, destinationServiceKey, childlist,"disabled", jwtToken),
                "Failed to disable parent-level health propagation");
        sourceKey = SourceServiceKey;
        destinationKey = destinationServiceKey;
        propagationStatus = true;

        int no_of_event_per_node = 1;
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        String msg = "Memory utilization of " + impactingTopologyKey + " exceed threshold of 80% " + randomStr;
        log.info("Sending impact event for topology node: " + impactingTopologyKey);
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, impactingTopologyKey, msg, "CRITICAL", location, event_tag);

        boolean impactingSubServiceZero = false;
        for (int i = 0; i < 12; i++) {
            SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[1], jwtToken);
            if ("0".equals(SourceHealthMap.get("impactingSubService"))) {
                impactingSubServiceZero = true;
                break;
            }
            log.info("Check another parent is impacted");
            SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[2], jwtToken);
            if ("1".equals(SourceHealthMap.get("impactingSubService"))) {
                impactingSubServiceZero = true;
                break;
            }
            commonwait.waitForSometime(5000);
        }
        Assert.assertTrue(impactingSubServiceZero,
                "Expected source health impactingSubService to be 0; last value: " + SourceHealthMap.get("impactingSubService"));

    }

    @Test(description = "Validate health status propagation with all child services enabled", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "17943", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17943_Validate_health_status_propagation_with_all_child_services_enabled(ITestContext context) throws IOException, InterruptedException{    
        int testcase_number = 13;
        String mlSheetName = "mlcheck";
        String childAssociatedElementsLink = "\":{\"_associatedElements\":\"[]\"}";
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        
        log.info("Check Require feature flag is enabled");
        if(!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary", jwtToken)){
            log.info("ml-situation-across-service-boundary feature flag is not enabled; skipping test as passed.");
            return;
        }
        
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String[] businessservice = row_data.get(0).get("Business Name").toString().split((","));
        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));

        Map<String, String> SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        String destinationServiceKey = SourceHealthMap.get("key");


        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[1], jwtToken);
        String SourceServiceKey = healthMap.get("key");

        List<String> childServiceKeyList = new ArrayList<>();
        for( String service : businessservice){
            Map<String, String> ThealthMap = aiopsApiCore.getHealthScore(service, jwtToken);
            childServiceKeyList.add(ThealthMap.get("key"));
            log.info("Close all open event on Service " + service);
            aiopsSituation.CloseAllEventOnService(service);
            log.info("Reset service health score for service " + service);
            aiopsApiCore.resetServiceHealthScore(ThealthMap.get("key"), jwtToken);
        }

        childlist = ",\"" + childServiceKeyList.get(2) + childAssociatedElementsLink + ",\"" + childServiceKeyList.get(3) + childAssociatedElementsLink;


        log.info("get Impacted Service Name");

        Map<String, String> topologyMap = aiopsApiCore.aiopsGetCITopology(SourceServiceKey, jwtToken);
        
        List<String> impactedServiceList = new ArrayList<>();
        for( String ciname : ci_list){
            String ci0 = ciname.trim();
            for (String topoKey : topologyMap.keySet()) {
                if (topoKey.endsWith("_kind")) {
                    continue;
                }
                if (topoKey.contains(ci0)) {
                    impactedServiceList.add(topoKey);
                    break;
                }
            }
            
        }

        Assert.assertTrue(
                aiopsSituation.validateParentLevelHealthStatusPropagationOverride(SourceServiceKey, destinationServiceKey, childlist,"disabled", jwtToken),
                "Failed to disable parent-level health propagation");
        sourceKey = SourceServiceKey;
        destinationKey = destinationServiceKey;
        propagationStatus = true;

        int no_of_event_per_node = 1;
        for( String serviceCIName : impactedServiceList){
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + serviceCIName + " exceed threshold of 80% " + randomStr;
            log.info("Sending impact event for topology node: " + serviceCIName);
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, serviceCIName, msg, "CRITICAL", location, event_tag);
        }

        boolean impactingSubServiceZero = false;
        for (int i = 0; i < 12; i++) {
            SourceHealthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
            
            if ("3".equals(SourceHealthMap.get("impactingSubService"))) {
                impactingSubServiceZero = true;
                break;
            }
            commonwait.waitForSometime(5000);
        }
        Assert.assertTrue(impactingSubServiceZero,
                "Expected source health impactingSubService to be 0; last value: " + SourceHealthMap.get("impactingSubService"));

    }

    @Test(description = "Validate Global and Local Service Unavailability Impact Rules", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18974", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18974_Validate_Global_and_Local_Service_Unavailability_Impact_Rules(ITestContext context) throws IOException, InterruptedException{    
        String[] businessservice = {"ML-UNAVAILABLE-PARENT", "ML-UNAVAILABLE-CHILD1"};
        String alarmPolicyName = "predict_global_health";

        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("service_unavailable");
        if(!isFeatureEnabled){
            log.info("topology-unavailability-hi feature flag is not enabled; skipping test as passed.");
            return;
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        aiopsApiCore.deleteServiceModel("ML-UNAVAILABLE", jwtToken);
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);

        JSONObject situationConfiguration = aiopsApiCore.getSituationGlobalHealthConfiguration(jwtToken);
        if (situationConfiguration == null) {
            Assert.fail("Failed to get situation configuration");
        }
        
        situationConfiguration.remove("resultMessage");

        JSONArray globalHealthConfiguration = situationConfiguration.getJSONArray("globalHealthConfiguration");
        for(int i = 0; i < globalHealthConfiguration.length(); i++){
            JSONObject healthElement = globalHealthConfiguration.getJSONObject(i);
            if(healthElement.getString("type").equals("health_processing")){
                globalHealthConfiguration.getJSONObject(i).getJSONArray("elements").getJSONObject(0).put("minEventCount", 3);
            }
        }
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
        String updatePayload = situationConfiguration.toString();
        boolean isUpdated = aiopsApiCore.updateSituationConfiguration(updatePayload, jwtToken);
        Assert.assertTrue(isUpdated, "Failed to update situation configuration with runtime minEventCount");
        log.info("Check if service Exist else create a new service");
        JSONObject serviceDetails = aiopsApiCore.searchAndGetServiceDetails(businessservice[0], jwtToken);


        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        String[] policy = patrolPolicies.get(0);


        if(serviceDetails == null || serviceDetails.length() == 0){
            boolean isCreated = aiopsPrediction.createService(businessservice[1], businessservice[0], policy[0]);
            Assert.assertTrue(isCreated, "Failed to create service");
        }
        commonwait.waitForSometime(10000);
        isUpdated = aiopsPrediction.updateServiceLevelConfiguration(businessservice[1], 1);
        Assert.assertTrue(isUpdated, "Failed to update service level configuration");

        
        log.info("Create Linux CPU policy for service " + businessservice[1]);
        boolean isCreated = hm_events.createPatrolAgentAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], false, jwtToken);
        Assert.assertTrue(isCreated, "Failed to create patrol agent alarm policy");

        alarmPolicyCreated = true;
        commonwait.waitForSometime(10000);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("get the health score for the service");
        boolean healthScoreZero = false;
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        for(int i = 0; i < 15; i++){
            healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
            if(healthMap != null){
                if(healthMap.get("health").equals("0")){
                    healthScoreZero = true;
                    break;
                }
            }
            commonwait.waitForSometime(5000);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }

        if(!healthScoreZero){
            Assert.fail("Expected health score to be 0; last value: " + healthMap.get("health") );
        }
        healthScoreZero = false;

        for(int i = 0; i < 2; i++){
            healthMap = aiopsApiCore.getHealthScore(businessservice[1], jwtToken);
            if(healthMap != null){
                if(healthMap.get("health").equals("0")){
                    healthScoreZero = true;
                    break;
                }
            }
            commonwait.waitForSometime(5000);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }
        
        if(!healthScoreZero){
            Assert.fail("Expected health score to be 0; last value: " + healthMap.get("health") );
        }
        
        
    }

    @Test(description = "Validate Service Unavailability Impact Rule – Any Event vs Minimum Events Threshold", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18973", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18973_Validate_Service_Unavailability_Impact_Rule_Any_Event_vs_Minimum_Events_Threshold(ITestContext context) throws IOException, InterruptedException{    
        String[] businessservice = {"ML-UNAVAILABLE-CHILD1"};
        String alarmPolicyName = "predict_global_health";

        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("service_unavailable");
        if(!isFeatureEnabled){
            log.info("topology-unavailability-hi feature flag is not enabled; skipping test as passed.");
            return;
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        aiopsApiCore.deleteServiceModel("ML-UNAVAILABLE", jwtToken);
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);

        JSONObject situationConfiguration = aiopsApiCore.getSituationGlobalHealthConfiguration(jwtToken);
        if (situationConfiguration == null) {
            Assert.fail("Failed to get situation configuration");
        }
        
        situationConfiguration.remove("resultMessage");

        log.info("Update Global level configuration ");
        JSONArray globalHealthConfiguration = situationConfiguration.getJSONArray("globalHealthConfiguration");
        for(int i = 0; i < globalHealthConfiguration.length(); i++){
            JSONObject healthElement = globalHealthConfiguration.getJSONObject(i);
            if(healthElement.getString("type").equals("health_processing")){
                globalHealthConfiguration.getJSONObject(i).getJSONArray("elements").getJSONObject(0).put("minEventCount", 1);
            }
        }

        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
        String updatePayload = situationConfiguration.toString();
        boolean isUpdated = aiopsApiCore.updateSituationConfiguration(updatePayload, jwtToken);
        Assert.assertTrue(isUpdated, "Failed to update situation configuration with runtime minEventCount");
        log.info("Check if service Exist else create a new service");
        JSONObject serviceDetails = aiopsApiCore.searchAndGetServiceDetails(businessservice[0], jwtToken);


        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        String[] policy = patrolPolicies.get(0);


        if(serviceDetails == null || serviceDetails.length() == 0){
            boolean isCreated = aiopsPrediction.createService(businessservice[0], null, policy[0]);
            Assert.assertTrue(isCreated, "Failed to create service");
        }
        commonwait.waitForSometime(10000);
        
        log.info("Get Service Key");
        Map<String, String> serviceHealthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        if (serviceHealthMap == null) {
            Assert.fail("Failed to get service model");
        }
        String serviceKey = serviceHealthMap.get("key");

        log.info("Get Service Model Detail");
        JSONArray serviceModelDetail = aiopsApiCore.getServiceModelDetail(serviceKey, jwtToken);
        if (serviceModelDetail == null) {
            Assert.fail("Failed to get service model detail");
        }
        JSONObject node = serviceModelDetail.getJSONObject(0);
        JSONObject metadata = node.getJSONObject("metadata");
        String associatedElements = metadata.getString("_associatedElements");
        if(associatedElements == null || associatedElements.isEmpty()){
            Assert.fail("Failed to get associated elements");
        }

        JSONArray associatedElementsArray = new JSONArray(associatedElements);
        String serviceModelFileName = "ServiceHealthProcessing.json";
        String jsonContent = aiopsApiCore.readJSon(serviceModelFileName);
        JSONObject ServiceHealthProcessing = new JSONObject(jsonContent);

        if (associatedElementsArray.length() == 1) {
            associatedElementsArray.put(ServiceHealthProcessing);
        }else{
            Assert.fail("Expected associated elements array length to be 1; actual value: " + associatedElementsArray.length());
        }

        JSONArray mainupdatebody = new JSONArray();
        JSONObject arrayBodyObj = new JSONObject();
        JSONObject updatebodyObj = new JSONObject();
        updatebodyObj.put("key", serviceKey);
        updatebodyObj.put("metadata", new JSONObject().put("_associatedElements", associatedElementsArray.toString()));
        arrayBodyObj.put("body", updatebodyObj);
        arrayBodyObj.put("method", "PATCH");
        mainupdatebody.put(arrayBodyObj);

        isUpdated = aiopsApiCore.updateServiceHealthProfileConfiguration(mainupdatebody.toString(), jwtToken);
        Assert.assertTrue(isUpdated, "Failed to update service model detail");
        commonwait.waitForSometime(10000);

        log.info("Create Linux CPU policy for service " + businessservice[0]);
        boolean isCreated = hm_events.createPatrolAgentAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], false, jwtToken);
        Assert.assertTrue(isCreated, "Failed to create patrol agent alarm policy");

        alarmPolicyCreated = true;
        commonwait.waitForSometime(10000);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("get the health score for the service");
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        for(int i = 0; i < 15; i++){
            healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
            if(healthMap != null){
                int healthScore = Integer.parseInt(healthMap.get("health"));
                int eventCount = Integer.parseInt(healthMap.get("impactedEntityCount"));
                if(healthScore != 0 && eventCount >= 5){
                    softAssert.fail("Expected health score to be 0; last value: " + healthMap.get("health") );
                }else if(healthScore == 0 && eventCount < 5){
                    softAssert.fail("Expected event count to be greater than 5; last value: " + healthMap.get("impactedEntityCount") );
                }
            }
            commonwait.waitForSometime(5000);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }

        softAssert.assertAll();
    }


    @Test(description = "Verify Global Preset for Services Console page", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18972", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18972_Verify_Global_Preset_for_Services_Console_page(ITestContext context) throws IOException, InterruptedException{    
        String[] businessservice = {"ML-UNAVAILABLE-CHILD1"};
        String alarmPolicyName = "predict_global_health";

        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("service_unavailable");
        if(!isFeatureEnabled){
            log.info("topology-unavailability-hi feature flag is not enabled; skipping test as passed.");
            return;
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        aiopsApiCore.deleteServiceModel("ML-UNAVAILABLE", jwtToken);
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);

        JSONObject situationConfiguration = aiopsApiCore.getSituationGlobalHealthConfiguration(jwtToken);
        if (situationConfiguration == null) {
            Assert.fail("Failed to get situation configuration");
        }
        
        situationConfiguration.remove("resultMessage");

        JSONArray globalHealthConfiguration = situationConfiguration.getJSONArray("globalHealthConfiguration");
        for(int i = 0; i < globalHealthConfiguration.length(); i++){
            JSONObject healthElement = globalHealthConfiguration.getJSONObject(i);
            if(healthElement.getString("type").equals("health_processing")){
                globalHealthConfiguration.getJSONObject(i).getJSONArray("elements").getJSONObject(0).put("minEventCount", 5);
            }
        }

        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
        String updatePayload = situationConfiguration.toString();
        boolean isUpdated = aiopsApiCore.updateSituationConfiguration(updatePayload, jwtToken);
        Assert.assertTrue(isUpdated, "Failed to update situation configuration with runtime minEventCount");
        log.info("Check if service Exist else create a new service");
        JSONObject serviceDetails = aiopsApiCore.searchAndGetServiceDetails(businessservice[0], jwtToken);


        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        String[] policy = patrolPolicies.get(0);


        if(serviceDetails == null || serviceDetails.length() == 0){
            boolean isCreated = aiopsPrediction.createService(businessservice[0], null, policy[0]);
            Assert.assertTrue(isCreated, "Failed to create service");
        }
        commonwait.waitForSometime(10000);
        
        
        log.info("Create Linux CPU policy for service " + businessservice[0]);
        boolean isCreated = hm_events.createPatrolAgentAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], false, jwtToken);
        Assert.assertTrue(isCreated, "Failed to create patrol agent alarm policy");

        alarmPolicyCreated = true;
        commonwait.waitForSometime(10000);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("get the health score for the service");
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
        for(int i = 0; i < 15; i++){
            healthMap = aiopsApiCore.getHealthScore(businessservice[0], jwtToken);
            if(healthMap != null){
                int healthScore = Integer.parseInt(healthMap.get("health"));
                int eventCount = Integer.parseInt(healthMap.get("impactedEntityCount"));
                if(healthScore != 0 && eventCount >= 5){
                    softAssert.fail("Expected health score to be 0; last value: " + healthMap.get("health") );
                }else if(healthScore == 0 && eventCount < 5){
                    softAssert.fail("Expected event count to be greater than 5; last value: " + healthMap.get("impactedEntityCount") );
                }
            }
            commonwait.waitForSometime(5000);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }

        softAssert.assertAll();
    }

}
