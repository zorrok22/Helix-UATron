package ITOM.Aiops;

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
import pages.ITOM.Api.HMEvents;
import java.io.IOException;
import java.util.*;
import util.*;


/**
 * Java translation (API-first) of Robot test "create-service-configure-for-prediction-and-runAutomation".
 * This focuses on the core REST steps without invoking Robot Framework.
 * NOTE: Several Robot keywords map to REST calls shown here; any missing mappings are marked TODO.
 */
public class AiopsPredictionTest {

    private static final SolqaLogger log = new SolqaLogger(AiopsPredictionTest.class.getName());

    // Endpoints used in the Robot keywords
    Aiops aiopsApiCore;
    HMEvents hm_events;
    CommonWaits commonWait;
    CommonMethods commonmethod;
    AiopsPrediction aiopsPrediction;
    AiopsDeepRCA aiopsDeepRCA;
    private SoftAssert softAssert = null;
    private String jwtToken = null;
    private String SERVICE_PREFIX = "predict-pa-";
    
   

    @BeforeMethod(alwaysRun = true)
    public void setUpHelpers() {
        aiopsApiCore = new Aiops();
        hm_events = new HMEvents();
        commonWait = new CommonWaits();
        commonmethod = new CommonMethods();
        aiopsPrediction = new AiopsPrediction();
        aiopsDeepRCA = new AiopsDeepRCA();
    }

    @Test(description = "[Service Prediction] Verify Service failure predictions and validate create/request automation flow and status in AIOPS", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12442", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12442_Verify_Service_failure_predictions_and_create_request_automation_flow_and_status_in_AIOPS(ITestContext context) throws IOException, InterruptedException {
        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(null);
        String alarmPolicyName = "predict";
        String serviceName = SERVICE_PREFIX + randomSuffix();
        if (!aiopsApiCore.enablePredictionFeature(jwtToken)) {
            Assert.fail("Failed to enable predictions feature.");
        }
        // Cleanup: delete prediction alarms and service if present
        log.info("Search and delete exisitng Alarm Policy");
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
        log.info("Delete exisitng Predict Service Model");
        aiopsApiCore.deleteServiceModel(SERVICE_PREFIX, jwtToken);
        log.info("Wait for 60 sec");
        commonWait.waitForSometime(60000); // wait for service sync happen

        // Discover Linux devices (subset of Robot get_linux_device)
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Fetch Linux PA machine List");
        boolean linuxHosts = aiopsPrediction.fetchLinuxHosts();
        Assert.assertTrue(linuxHosts, "Failed to fetch Linux hosts");
        log.info("Fetch Windows PA machine List");
        aiopsPrediction.fetchWindowsHosts();
        List<String> HostsList = aiopsPrediction.getHostsList();
        List<String> NodeIDList = aiopsPrediction.getNodeIDList();
        Assert.assertFalse(HostsList.isEmpty(), "No Linux or Windows hosts discovered to build service.");
        for(String ciname:HostsList) {
            String hostNoDomain = ciname.replaceAll("\\..*$", "");
            String addmId = aiopsPrediction.getAddmId(hostNoDomain);
            if (addmId != null && !NodeIDList.contains(addmId)) {
                NodeIDList.add(addmId);
            }
        }
        // Create service model with the first discovered host
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String associateList = aiopsPrediction.prepareAssociatedElements(50);
        String serviceId = aiopsApiCore.createServiceWithStaticNodeusingMulti(serviceName, associateList, NodeIDList, false, null, jwtToken);
        Assert.assertNotNull(serviceId, "Fail to create Service");
        commonWait.waitForSometime(120000); // New service take approx 2 min to reflect.
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();
        // Create baseline/prediction alarm on CPU utilization for the node

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        // If condition fail Alarm policy will be created in next test case
        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("kpi-predictions");
        if(!isFeatureEnabled){
            for (String[] policy : patrolPolicies) {
                hm_events.createPatrolAgentAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], false, jwtToken);
            }
        }
        softAssert.assertAll();
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    @Test(description = "[KPI Prediction] Verify prediction for same device added to multiple services", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18968", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18968_Verify_prediction_for_same_device_added_to_multiple_services(ITestContext context) throws IOException, InterruptedException {
        this.softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(null);

        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("kpi-predictions");
        if(!isFeatureEnabled){
            log.info("manage-kpi-predictions-enabled feature flag is not enabled; skipping test as passed.");
            return;
        }

        String alarmPolicyName = "predict";
        String serviceName = SERVICE_PREFIX + "kpi-" + randomSuffix();
        if (!aiopsApiCore.enablePredictionFeature(jwtToken)) {
            Assert.fail("Failed to enable predictions feature.");
        }
        // Cleanup: delete prediction alarms and service if present
        
        // Discover Linux devices (subset of Robot get_linux_device)
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Fetch Linux PA machine List");
        boolean linuxHosts = aiopsPrediction.fetchLinuxHosts();
        Assert.assertTrue(linuxHosts, "Failed to fetch Linux hosts");
        log.info("Fetch Windows PA machine List");
        aiopsPrediction.fetchWindowsHosts();
        List<String> HostsList = aiopsPrediction.getHostsList();
        List<String> NodeIDList = aiopsPrediction.getNodeIDList();
        Assert.assertFalse(HostsList.isEmpty(), "No Linux or Windows hosts discovered to build service.");
        for(String ciname:HostsList) {
            String hostNoDomain = ciname.replaceAll("\\..*$", "");
            String addmId = aiopsPrediction.getAddmId(hostNoDomain);
            if (addmId != null && !NodeIDList.contains(addmId)) {
                NodeIDList.add(addmId);
            }
        }
        // Create service model with the first discovered host
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String associateList = "[]";
        String serviceId = aiopsApiCore.createServiceWithStaticNodeusingMulti(serviceName, associateList, NodeIDList, false, null, jwtToken);
        Assert.assertNotNull(serviceId, "Fail to create Service");
        commonWait.waitForSometime(120000); // New service take approx 2 min to reflect.
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();
        // Create baseline/prediction alarm on CPU utilization for the node

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        for (String[] policy : patrolPolicies) {
            hm_events.createPatrolAgentAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], false, jwtToken);
        }

        // Wait for prediction records to appear
        aiopsPrediction.waitForPrediction(serviceName, 5, 15);


        softAssert.assertAll();
    }


}