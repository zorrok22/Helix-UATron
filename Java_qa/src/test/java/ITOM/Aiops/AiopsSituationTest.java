package ITOM.Aiops;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import framework.Launcher;


public class AiopsSituationTest extends Launcher {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationTest.class.getName());
    public static final String configuration_end_point = "/aiops-config/api/v1.0/situation_configurations/search";
    static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static final int event_cnt = 1;
    private static final long situationTimeDiff = 300000;
    public static final String aiopssituationconfigUrl = "/aiops-config/api/v1.0/situation_configurations/";
    public static final String event_tag = "";
    private static String eventClass = "NAGIOS_EV1";
    private static String location = "Pune";
    CommonMethods commonmethod = new CommonMethods();
    SoftAssert softAssert = null;
    AiopsSituation aiopsSituation = new AiopsSituation();
    CommonWaits commonwait = new CommonWaits();
    private ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();


    @Test(description = "[Highest Severity] Verify the situation highest severity when highest severity event is sent on a node which is outside the service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12529", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12529_Verify_the_situation_highest_severity_when_highest_severity_event_sent_on_node(ITestContext context)
            throws IOException, InterruptedException {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        log.info("Starting DRSQA_12529 scenario");
        int testcase_number = 1;
        String mlSheetName = "pca";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String[] host_list = row_data.get(0).get("Impact Element").toString().split(",");
        String serviceName = row_data.get(0).get("Business Name").toString();
        String jwtToken = null;
        int noOfEventsSent = 0;

        Aiops aiopsApiCore = new Aiops();
        HMEvents eventApi = new HMEvents();
                
       
        // Get and enable situation
        String situationId = aiopsSituation.getSituationId();
        log.info("Fetched situationId=" + situationId);

        try {
            aiopsSituation.updateSituationConfiguration(true, situationId);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
            
            log.info("Enabled situation configuration");

            // Clean up old events
            eventApi.closeAllEventByClass(eventClass, jwtToken);
            aiopsSituation.CloseAllEventOnService(serviceName);
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
            aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);

            healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            if (Integer.parseInt(healthScore.get("actualHealth")) < 100) {
                Assert.fail("Service healthscore not in proper state");
            }

            // Send events
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg;

            msg = "Memory utilization of " + host_list[0] + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list[0], msg, "MINOR", location,event_tag);
            noOfEventsSent++;

            msg = "Memory utilization of " + host_list[1] + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list[1], msg, "MINOR", location,event_tag);
            noOfEventsSent++;

            log.info("Waiting for situation creation...");
            if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
            String situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
            String severity = situationObject.getJSONArray("eventList").getJSONObject(0).getString("severity");
            if(!severity.equals("MINOR")){
                log.error("Severity: " + severity);
                softAssert.fail("Severity is not MINOR");
            }

            // Send MAJOR event
            msg = "Memory utilization of " + host_list[2] + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list[2], msg, "MAJOR", location,event_tag);
            noOfEventsSent++;
            aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);

            for(int i = 0; i < 10; i++){
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
                severity = situationObject.getJSONArray("eventList").getJSONObject(0).getString("severity");
                if(severity.equals("MAJOR")){
                    break;
                }
                commonwait.waitForSometime(15000);
            }
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
            severity = situationObject.getJSONArray("eventList").getJSONObject(0).getString("severity");
            if(!severity.equals("MAJOR")){
                log.error("Severity: " + severity);
                softAssert.fail("Severity is not MAJOR");
            }
            

            // Send CRITICAL event
            msg = "Memory utilization of " + host_list[3] + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list[3], msg, "CRITICAL", location, event_tag);
            noOfEventsSent++;
            aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);

            for(int i = 0; i < 10; i++){
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
                severity = situationObject.getJSONArray("eventList").getJSONObject(0).getString("severity");
                if(severity.equals("CRITICAL")){
                    break;
                }
                commonwait.waitForSometime(15000);
            }

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            situationObject = aiopsApiCore.getSituationsForService(serviceName, jwtToken);
            severity = situationObject.getJSONArray("eventList").getJSONObject(0).getString("severity");
            if(!severity.equals("CRITICAL")){
                log.error("Severity: " + severity);
                softAssert.fail("Severity is not CRITICAL");
            }


            // Cleanup
            eventApi.closeAllEventByClass(eventClass, jwtToken);
            aiopsSituation.CloseAllEventOnService(serviceName);
            aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);

        } finally {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Disabling situation-severity...");
            aiopsSituation.updateSituationConfiguration(false, situationId);
        }
        softAssert.assertAll();
    }

    @Test(description = "[ERICSSON]: Verify Situation whitelist for incident creation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12505", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12505_Verify_situation_whitelist_for_incident_creation(ITestContext context)
            throws Exception {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        log.info("Starting DRSQA_12505 scenario");
        String eventSeverity = "CRITICAL";
        int testcase_number = 2;
        String mlSheetName = "pca";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String[] whitelistServices = row_data.get(0).get("Business Name").toString().split(",");
        String[] whitelistHosts = row_data.get(0).get("Impact Element").toString().split(",");
        String[] testEventHosts = row_data.get(0).get("testEventHosts").toString().split(",");
        String[] serviceCheckList = row_data.get(0).get("serviceCheckList").toString().split(",");
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        CommonWaits commonWait = new CommonWaits();
        Aiops aiopsApiCore = new Aiops();
        HMEvents eventApi = new HMEvents();
        String jwtToken = commonmethod.refreshJwtToken(null);
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }
        StackTraceElement caller = stackTrace[1];
        log.info("Validate incident creation enabled");
        if (!eventApi.checkIncidentCreationEnabled(jwtToken)) {
            Assert.fail("Incident creation is not enabled This TC cannot run");
        }

        if (!aiopsApiCore.getTenantFeatureFlagStatus("situation_incident_consolidation",jwtToken)){
            Assert.fail("situation_incident_consolidation is not enabled This TC cannot run");
        }
        log.info("Validate ml-situation-across-service-boundary enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled This TC cannot run");
            return;
        }
        log.info("Get situationId");
        String situationId = aiopsSituation.getSituationId();
        aiopsSituation.setSituationWhitelist(null, false, situationId);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info(caller.getLineNumber() + " Close all the Open event");
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        eventApi.CloseAllOpenEvent(jwtToken);
        for (String serviceName: serviceCheckList) {
            aiopsSituation.CloseAllEventOnService(serviceName);
        }
        
        commonWait.waitForSometime(15000);
        aiopsSituation.close_service_situation(serviceCheckList);
        commonWait.waitForSometime(15000);

        for (String serviceName: serviceCheckList) {
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
            aiopsApiCore.updateServiceHealthProfile(healthScore.get("id"),jwtToken);
            commonWait.waitForSometime(15000);
        }
        for (String serviceName : serviceCheckList) {
            aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info(caller.getLineNumber() + " Make sure healthscore is 100 before start of automation");
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            if (Integer.parseInt(healthScore.get("actualHealth")) < 100) {
                Assert.fail("Service healthscore not in proper state");
            }
        }

        // Send events
        log.info(caller.getLineNumber() + " Validate situation consolidation when whitelist is not set");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        for (String hostname : whitelistHosts) {
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }

        for (String hostname : testEventHosts) {
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }
        commonWait.waitForSometime(15000);

        // Check situations
        List<String> situationList = aiopsSituation.validate_situation_creation(serviceCheckList);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        for (String servicesId : situationList) {
            aiopsSituation.validate_event_under_situation(servicesId, false);
        }
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        eventApi.CloseAllOpenEvent(jwtToken);

        log.info(caller.getLineNumber() + " Close all the event after situation consolidation validation is done");
        try{
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            aiopsSituation.setSituationWhitelist(whitelistServices, true, situationId);

            commonWait.waitForSometime(15000);
            for (String serviceName: serviceCheckList) {
                Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
                aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
                aiopsApiCore.updateServiceHealthProfile(healthScore.get("id"),jwtToken);
            }
            log.info(caller.getLineNumber() + " Close all situation");
            for (String servicesId : situationList) {
                commonWait.waitForSometime(5000);
                eventApi.closeEventApi(servicesId,"closing Event by api", jwtToken);
            }

            // Enable whitelist
            commonWait.waitForSometime(10000);
            for (String serviceName: serviceCheckList) {
                Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
                aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
                aiopsApiCore.updateServiceHealthProfile(healthScore.get("id"),jwtToken);
                commonWait.waitForSometime(10000);
            }
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            aiopsApiCore.waitForHealthScoreExpectedValue(90, serviceCheckList[0], jwtToken,2);
            aiopsApiCore.waitForHealthScoreExpectedValue(90, serviceCheckList[1], jwtToken,2);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);

            // Resend events
            randomStr = RandomStringUtils.randomAlphanumeric(7);
            for (String hostname : whitelistHosts) {
                String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
                eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
            }

            for (String hostname : testEventHosts) {
                String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
                eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
            }

            // Check after enabling whitelist
            situationList.clear();
            commonWait.waitForSometime(15000);
            situationList = aiopsSituation.validate_situation_creation(serviceCheckList);

            // Check non-whitelisted service
            log.info("Checking for none whilelist Service i.e ML-TSOM ");
            aiopsSituation.validate_event_under_situation(situationList.get(0), true);

            // Check whitelisted service
            log.info("Checking for whilelist Service i.e ML-HW-AIX ");
            aiopsSituation.validate_event_under_situation(situationList.get(1), false);


            // Final cleanup
            eventApi.closeAllEventByClass(eventClass, jwtToken);
            for (String servicesId : situationList) {
                commonWait.waitForSometime(5000);
                eventApi.closeEventApi(servicesId,"closing Event by api", jwtToken);
            }
            for (String serviceName: serviceCheckList) {
                Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
                aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
                aiopsApiCore.updateServiceHealthProfile(healthScore.get("id"),jwtToken);
                commonWait.waitForSometime(10000);
            }
            aiopsApiCore.waitForHealthScoreExpectedValue(91, whitelistServices[0], jwtToken,2);

        } finally {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Disabling situation-severity...");
            eventApi.closeAllEventByClass(eventClass, jwtToken);
            aiopsSituation.setSituationWhitelist(null, false, situationId);
            eventApi.CloseAllOpenEvent(jwtToken);
        }
        softAssert.assertAll();
    }

    @Test(description = "[multi-service] Verify Similar situations validation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "13109", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13109_Verify_Similar_situations_validation(ITestContext context) throws IOException, InterruptedException {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        log.info("Starting DRSQA_13109 scenario");
        String situationJsonFile = "msearchsituationquery.json";
        String similarSituationJsonFile = "getSimilarSituationSearchString.json";
        String eventSeverity = "CRITICAL";
        int testcase_number = 5;
        String mlSheetName = "pca";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String modelName = row_data.get(0).get("Business Name").toString();
        String situationCountEndPoint = "/situation-fingerprint-service/api/v1.0/situation_count";
        String situationsummary = "utilization on disk";
        String[] nodeToImpact = row_data.get(0).get("Impact Element").toString().split(",");;
        String[] EventMessage = {"I/O utilization on disk 'sdd' is over 90% for extended period","Application 'Infrastructure Management Integration 11.3.05' received invalid response from downstream service","Application 'TrueSight Infrastructure Mgmt 11.3.05' is unreachable or down"};
        CommonWaits commonWait = new CommonWaits();
        Aiops aiopsApiCore = new Aiops();
        
        int currentSimilarSituationCount = 0;
        long similarSituationTimeDiff = 86400000;
        String SituationKey = "";
        HMEvents eventApi = new HMEvents();
        String jwtToken = commonmethod.refreshJwtToken(null);
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }
        // Get and enable situation
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is disabled; skipping test as passed.");
            return;
        }
        log.info("Get Situation ID");
        String situationId = aiopsSituation.getSituationId();
        String situationUrl = aiopssituationconfigUrl + situationId;
        String jsonContent = aiopsApiCore.readJSon("updatesituationconfigurationsetting.json");
        String[] arry = {"200", jsonContent};
        log.info("Update Situation saturation to 5 min");
        logAnalyticsPage.Send_RestCall("PUT", situationUrl, arry);
        log.info("Close all Event and situation");
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        commonWait.waitForSometime(15000);
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);
        JSONObject msquery = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken,situationJsonFile,situationTimeDiff);
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord > 0){
            String sit_id = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
            eventApi.closeEventApi(sit_id,"closing Event by api", jwtToken);
        }
        commonWait.waitForSometime(15000);
        log.info("Get Similar Situtation Count");
        msquery = eventApi.runMSearchQueryGetSituation(modelName, jwtToken,similarSituationJsonFile,similarSituationTimeDiff);
        totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");;
        if (totalrecord > 0){
            JSONArray situation_list = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets");
            for (Object similarsituation: situation_list){
                JSONObject featureBody = (JSONObject)similarsituation;
                SituationKey = featureBody.getString("key");
                String msg = featureBody.getJSONObject("top_hits#latest_doc").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("msg");
                if (msg.contains(situationsummary)){
                    arry[1] = "[\"" +  SituationKey  + "\"]";
                    logAnalyticsPage.Send_RestCall("POST", situationCountEndPoint, arry);
                    JSONObject record = logAnalyticsPage.jsonObject;
                    currentSimilarSituationCount = record.getInt(SituationKey);
                    break;
                }
            }
        }

        log.info("Impact the Model");
        for (int i = 0; i<3;i++){
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, nodeToImpact[i], EventMessage[i], eventSeverity, location, event_tag);
        }
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        if (currentSimilarSituationCount == 0){
            log.info("This is first time TC is run on this tenant, on second rn situation will be checked");
        }
        else{
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            JSONObject situationObject = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationJsonFile, situationTimeDiff);
            JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
            String situationIdentifier = hitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
            log.info("Wait for situation to get saturated");
            for (int j=0;j<21;j++) {
                if (aiopsApiCore.verifySituationSaturated(situationIdentifier,jwtToken)) {
                    break;
                }
                commonWait.waitForSometime(15000);
            }
            log.info("Validated Similar situation count has increased");
            logAnalyticsPage.Send_RestCall("POST", situationCountEndPoint, arry);
            JSONObject record = logAnalyticsPage.jsonObject;
            int SimilarSituationCount = record.getInt(SituationKey);
            if (currentSimilarSituationCount > SimilarSituationCount){
                softAssert.fail("Similar Situation does not got updated.");
            }
        }
        softAssert.assertAll();
    }
    @Test(description = "[Situation across service boundary]:Multi-service: Verify the situation severity when events are on host Pod deployment and namespace and all the nodes are topological connected", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12513", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12513_Multi_service_Verify_the_situation_severity_when_events_host_Pod_deployment_and_namespace_all_nodes_topological_connected(ITestContext context)
            throws IOException, InterruptedException {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        log.info("Starting DRSQA_12513 scenario");
        int testcase_number = 4;
        String mlSheetName = "pca";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String serviceName = row_data.get(0).get("Business Name").toString();
        String eventClass = "NAGIOS_EV1";
        String jwtToken = null;
        String situationjsonfile = "msearchsituationquery.json";
        String outside_node = "driftplugin";
        String[] host_list_itsm = row_data.get(0).get("Impact Element").toString().split(",");
        int noOfEventsSent = 0;

        Aiops aiopsApiCore = new Aiops();
        HMEvents eventApi = new HMEvents();

        // Get and enable situation
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is disabled; skipping test as passed.");
            return;
        }
        // Get and enable situation
        String situationId = aiopsSituation.getSituationId();


        // Clean up old events
        log.info("Close all Event on Nagios...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        log.info("Validate Health Score before start of event...");
        healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        if (Integer.parseInt(healthScore.get("actualHealth")) < 100) {
            Assert.fail("Service healthscore not in proper state");
        }

        try{

            log.info("Set the config parameter...");
            aiopsSituation.updateSituationConfiguration(true, situationId);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);

            // Send events
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg;
            /*

            Send one each event on inside service node and one event outside service node
            Send events on host Pod deployment and namespace and all the nodes are topological coneected
            ev1-->host-->Minor
            ev2-->pod--->Minor
            ev3-->deployment-->Minor
            ev4-->Namespace-->Major
            */
            log.info("Send a Event to outside CI's...");
            msg = "Memory utilization of " + outside_node + " exceed threshold of 80% " + randomStr;
            String responseString = eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, outside_node, msg, "MAJOR", location, event_tag);
            noOfEventsSent++;
            log.info("Send a Event to inside service ci's...");
            for (int i = 0; i < 3; i++) {
                msg = "Memory utilization of " + host_list_itsm[i] + " exceed threshold of 80% " + randomStr;
                eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list_itsm[i], msg, "MINOR", location, event_tag);
                noOfEventsSent++;
            }

            log.info("Waiting for situation creation...");
            if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Validate Situation severity...");


            String situationIdentifier = "";

            JSONObject msquery = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationjsonfile, situationTimeDiff);
            if (msquery == null){
                Assert.fail("Fail to get situation from msearch query.");
            }
            int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord > 0) {
                JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
                for (Object jsonobj: jsonarray) {
                    JSONObject sit_obj =  (JSONObject)jsonobj;
                    String EventClass = sit_obj.getJSONObject("_source").getString("class");
                    if(!EventClass.equals("Situation")){
                        continue;
                    }
                    String status = sit_obj.getJSONObject("_source").getString("status");
                    if (!status.equals("CLOSED")){
                        log.info("Service is impacted and situation is created, exiting loop.");
                        situationIdentifier = sit_obj.getJSONObject("_source").getString("_identifier");
                        break;
                    }
                }
            }


            JSONObject situationObject = new JSONObject(eventApi.eventDetails(situationIdentifier, jwtToken));
            
            String situation_severity = situationObject.getJSONObject("response").getJSONObject("allslots").getString("severity");
            softAssert.assertEquals(situation_severity, "MAJOR");

            /* Sending 5th critical event on pod which is inside service
               ev5-->pod-->Critical

             */
            log.info("Send a Event with critical event...");
            msg = "Memory utilization of " + host_list_itsm[1] + " exceed threshold of 80% " + randomStr;
            responseString = eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list_itsm[1], msg, "CRITICAL", location, event_tag);
            noOfEventsSent++;
            JSONObject eventid = new JSONObject(responseString);
            String eventid_critical = eventid.getJSONObject("0").getJSONArray("resourceId").getString(0);

            log.info("Close critical event...");
            aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);
            eventApi.closeEventApi(eventid_critical,"Close Event",jwtToken);

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Send minor event so that siutation get updated...");
            randomStr = RandomStringUtils.randomAlphanumeric(7);
            msg = "Memory utilization of " + host_list_itsm[1] + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list_itsm[1], msg, "MINOR", location, event_tag);
            noOfEventsSent++;
            aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Validate situation Severity ...");
            situationObject = new JSONObject(eventApi.eventDetails(situationIdentifier, jwtToken));
            situation_severity = situationObject.getJSONObject("response").getJSONObject("allslots").getString("severity");
            softAssert.assertEquals(situation_severity, "CRITICAL");
            eventApi.closeAllEventByClass(eventClass, jwtToken);
            eventApi.closeEventApi(situationIdentifier,"Close Event",jwtToken);

            /*
            Again Send one each event on inside service node and one event outside service node
            Send events on host Pod deployment and namespace and all the nodes are topological coneected
            ev1-->host-->Minor
            ev2-->pod--->Minor
            ev3-->deployment-->Major
            ev4-->Namespace-->Minor
             */
            noOfEventsSent = 0;
            randomStr = RandomStringUtils.randomAlphanumeric(7);
            msg = "Memory utilization of " + outside_node + " exceed threshold of 80% " + randomStr;
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, outside_node, msg, "MINOR", location, event_tag);
            noOfEventsSent++;
            String[] severity1 = {"MINOR", "MAJOR", "MINOR"};
            for (int i = 0; i < 3; i++) {
                msg = "Memory utilization of " + host_list_itsm[i] + " exceed threshold of 80% " + randomStr;
                eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, host_list_itsm[i], msg, severity1[i], location, event_tag);
                noOfEventsSent++;
            }
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Waiting for situation creation...");
            if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }

            
            msquery = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationjsonfile, situationTimeDiff);
            if (msquery == null){
                Assert.fail("Fail to get situation from msearch query.");
            }
            totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord > 0) {
                JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
                for (Object jsonobj: jsonarray) {
                    JSONObject sit_obj =  (JSONObject)jsonobj;
                    String EventClass = sit_obj.getJSONObject("_source").getString("class");
                    if(!EventClass.equals("Situation")){
                        continue;
                    }
                    String status = sit_obj.getJSONObject("_source").getString("status");
                    if (!status.equals("CLOSED")){
                        log.info("Service is impacted and situation is created, exiting loop.");
                        situationIdentifier = sit_obj.getJSONObject("_source").getString("_identifier");
                        break;
                    }
                }
            }

            situationObject = new JSONObject(eventApi.eventDetails(situationIdentifier, jwtToken));
            situation_severity = situationObject.getJSONObject("response").getJSONObject("allslots").getString("severity");
            softAssert.assertEquals(situation_severity, "MAJOR");

            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, outside_node, msg, "CRITICAL", location, event_tag);
            noOfEventsSent++;

            aiopsSituation.waitForNewEventAddedToSituation(noOfEventsSent, situationIdentifier, jwtToken);
            situationObject = new JSONObject(eventApi.eventDetails(situationIdentifier, jwtToken));
            situation_severity = situationObject.getJSONObject("response").getJSONObject("allslots").getString("severity");
            softAssert.assertEquals(situation_severity, "CRITICAL");

            // Cleanup
            eventApi.closeAllEventByClass(eventClass, jwtToken);
            eventApi.closeEventApi(situationIdentifier,"Close Event",jwtToken);

            aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);

        } finally {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Disabling situation-severity...");
            aiopsSituation.updateSituationConfiguration(false, situationId);
        }
        softAssert.assertAll();
    }

    @Test(description = "[Similar Situation]:Verify Similar Situation is generated for a service model", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12451", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12451_Verify_Similar_Situation_generated_for_service_model(ITestContext context) throws IOException, InterruptedException {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        log.info("Starting DRSQA_12451 scenario");
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String situationJsonFile = "msearchsituationquery.json";
        String similarSituationJsonFile = "getSimilarSituationSearchString.json";
        String eventSeverity = "CRITICAL";
        int testcase_number = 5;
        String mlSheetName = "pca";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String modelName = row_data.get(0).get("Business Name").toString();
        String situationCountEndPoint = "/situation-fingerprint-service/api/v1.0/situation_count";
        String situationsummary = "utilization on disk";
        String[] nodeToImpact = row_data.get(0).get("Impact Element").toString().split(",");
        String[] EventMessage = {"I/O utilization on disk 'sdd' is over 90% for extended period","Application 'Infrastructure Management Integration 11.3.05' received invalid response from downstream service","Application 'TrueSight Infrastructure Mgmt 11.3.05' is unreachable or down"};
        CommonWaits commonWait = new CommonWaits();
        Aiops aiopsApiCore = new Aiops();
        int currentSimilarSituationCount = 0;
        long similarSituationTimeDiff = 86400000;
        String SituationKey = "";
        HMEvents eventApi = new HMEvents();
        String jsonContent = aiopsApiCore.readJSon("updatesituationconfigurationsetting.json");
        String jwtToken = commonmethod.refreshJwtToken(null);
        // Get and enable situation
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }
        if (aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is enabled; skipping test as passed.");
            return;
        }

        // Get and enable situation
        log.info("Get Situation ID");
        String situationId = aiopsSituation.getSituationId();
        String situationUrl = aiopssituationconfigUrl + situationId;
        String[] arry = {"200", jsonContent};
        log.info("Update Situation saturation to 5 min");
        logAnalyticsPage.Send_RestCall("PUT", situationUrl, arry);
        log.info("Close all Event and situation");
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        commonWait.waitForSometime(15000);
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(modelName, jwtToken);
        JSONObject msquery = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken,situationJsonFile,situationTimeDiff);
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord > 0){
            String sit_id = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
            eventApi.closeEventApi(sit_id,"closing Event by api", jwtToken);
        }
        commonWait.waitForSometime(15000);
        log.info("Get Similar Situtation Count");
        for(int i=0;i<2;i++){
            msquery = eventApi.runMSearchQueryGetSituation(modelName, jwtToken,similarSituationJsonFile,similarSituationTimeDiff);
            if(msquery == null){
                log.error("Fail to get situation from msearch query.");
                commonWait.waitForSometime(15000);
                continue;
            }
        }

        totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");;
        if (totalrecord > 0){
            JSONArray situation_list = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("aggregations").getJSONObject("sterms#result").getJSONArray("buckets");
            for (Object similarsituation: situation_list){
                JSONObject featureBody = (JSONObject)similarsituation;
                SituationKey = featureBody.getString("key");
                String msg = featureBody.getJSONObject("top_hits#latest_doc").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("msg");
                if (msg.contains(situationsummary)){
                    arry[1] = "[\"" +  SituationKey  + "\"]";
                    logAnalyticsPage.Send_RestCall("POST", situationCountEndPoint, arry);
                    JSONObject record = logAnalyticsPage.jsonObject;
                    currentSimilarSituationCount = record.getInt(SituationKey);
                    break;
                }
            }
        }

        log.info("Impact the Model");
        for (int i = 0; i<3;i++){
            eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, nodeToImpact[i], EventMessage[i], eventSeverity, location, event_tag);
        }
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        if (currentSimilarSituationCount == 0){
            log.info("This is first time TC is run on this tenant, on second rn situation will be checked");
        }
        else{
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            JSONObject situationObject = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationJsonFile, situationTimeDiff);
            JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
            String situationIdentifier = hitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
            log.info("Wait for situation to get saturated");
            for (int j=0;j<21;j++) {
                if (aiopsApiCore.verifySituationSaturated(situationIdentifier,jwtToken)) {
                    break;
                }
                commonWait.waitForSometime(15000);
            }
            log.info("Validated Similar situation count has increased");
            logAnalyticsPage.Send_RestCall("POST", situationCountEndPoint, arry);
            JSONObject record = logAnalyticsPage.jsonObject;
            int SimilarSituationCount = record.getInt(SituationKey);
            if (currentSimilarSituationCount > SimilarSituationCount){
                softAssert.fail("Similar Situation does not got updated.");
            }
        }
        softAssert.assertAll();
    }

}
