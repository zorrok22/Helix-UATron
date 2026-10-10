package ITOM.Aiops;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.AiopsSituation.IdleTimeAdjustResult;
import pages.ITOM.Api.AiopsSituation.IdleTimeMetricChange;
import pages.ITOM.Api.HMEvents;
//import pages.ITOM.Api.IntelligentAutomation;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;

public class changeriskadvisortest {
    static SolqaLogger log = new SolqaLogger(changeriskadvisortest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
        System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String getServicemetricList = "/service-management/api/v1.0/service/metric/list";
    private static final String getGPTInsightEndPoint = "/change-risk-analysis/api/v1.0/gpt-insights/";
    private static final String CRAInsightEndPoint = "/change-risk-analysis/api/v1.1/insights/";
    private static final String getChangeListEndPoint = "/service-management/api/v1.0/helixgpt/assistant/agents/CRA/invoke";
    String cmdbUrl = config.get("CMDBURL");
    String username = config.get("ADE_USERNAME");
    String password = config.get("ADE_PASSWORD");
    String jwtToken = null;
    HMEvents hm_events = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    LogAnalyticsPage logAnalyticsPage = null;
    CommonWaits commonwait = null;
    CMDBRestApi cmdbRestApi = null;
    String eventClass = "NAGIOS_EV1";
    SoftAssert softAssert = null;
    CommonMethods commonmethod = null;

    @BeforeMethod
    public void prerquirements() {
        
        hm_events = new HMEvents();
        aiopsApiCore = new Aiops();
        aiopsSituation = new AiopsSituation();
        commonmethod = new CommonMethods();
        softAssert = new SoftAssert();
        aiopsDeepRCA = new AiopsDeepRCA();
        logAnalyticsPage = new LogAnalyticsPage();
        commonwait = new CommonWaits();
        RemedyApiClient remedyApiClient = new RemedyApiClient(cmdbUrl);
        log.info("Setting up the test...");
        jwtToken = commonmethod.refreshJwtToken(null);
        try {
            String authToken = remedyApiClient.getRemedyApiKey(username, password);
            cmdbRestApi = new CMDBRestApi(cmdbUrl, authToken);
            cmdbRestApi.setCreatebackdatatedChange(true);
            log.info("Close all open Event");
            log.info("CLose all open event on Nagios class");
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            log.info("Close all open Situation");
            hm_events.closeAllEventByClass("Situation", jwtToken);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    

    @Test(description = "[Change Risk Advisor]: Verify HelixGPT CRA AIOPS change risk assessment summarization Best Action Risk Mitigation", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "12516", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12516_Verify_HelixGPT_CRA_AIOPS_change_risk_assessment_summarization_Best_Action_Risk_Mitigation(ITestContext context) throws Exception {
        log.info("Starting test: DRSQA_12516_Verify_HelixGPT_CRA_AIOPS_change_risk_assessment_summarization_Best_Action_Risk_Mitigation");
        if(jwtToken == null) {
            prerquirements();
        }
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Change Risk Advisor")){
            log.info("CRA feature is not enabled; skipping test as passed.");
            return;
        }
        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String ChangeListFileName = "getchangerisklist.json";
        String getchangemetriclistFileName = "getchangemetriclist.json";
        
        
        log.info("Starting DRSQA_12516_Verify_HelixGPT_CRA_AIOPS_change_risk_assessment_summarization_Best_Action_Risk_Mitigation test");
        

        String jwtToken = commonmethod.refreshJwtToken(logAnalyticsPage.auth_token);
        log.info("Generated JWT token and set access credentials");



        String body = aiopsApiCore.readJSon(ChangeListFileName);

        log.info("Fetching change records from endpoint: " + getChangeListEndPoint);
        logAnalyticsPage.Send_RestCall("POST", getChangeListEndPoint, new String[]{"200",body});
        JSONObject record =  logAnalyticsPage.jsonObject;
        JSONArray  changerecord = logAnalyticsPage.jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("changes");
        log.info("Retrieved " + changerecord.length() + " change records");

        if (changerecord.length() < 1){
            Assert.fail("No change Record fetch");
        }

        // Valid change statuses to process
        List<String> validChangeStatuses = new ArrayList<>();
        validChangeStatuses.add("Closed");
        validChangeStatuses.add("Implementation In Progress");
        validChangeStatuses.add("Completed");

        for (int i =0;i<8;i = i+1){
            String changeId = changerecord.getJSONObject(i).getString("change_id");
            String changeStatus = changerecord.getJSONObject(i).getString("change_status");
            String changeShortDesc = changerecord.getJSONObject(i).getString("change_short_desc");
            log.info("Processing change " + (i+1) + "/5 - Change ID: " + changeId + ", Status: " + changeStatus);

            // Validate change status
            if (!validChangeStatuses.contains(changeStatus)) {
                log.warn("Skipping change " + changeId + " - Status '" + changeStatus + "' is not in valid statuses list");
                continue;
            }

            String end_point = CRAInsightEndPoint + changeId;
            log.info("Fetching insights for change ID: " + changeId + " from endpoint: " + end_point);
            logAnalyticsPage.Send_RestCall("GET", end_point, new String[]{"200"});
            JSONObject changeSitRecord = logAnalyticsPage.jsonObject;
            if (logAnalyticsPage.response_code > 299){
                log.warn("Insights could not be retrieved for change request: " + changeId);
                continue;
            }
            String serviceName = changeSitRecord.getJSONArray("serviceCi").getString(0);
            log.info("Service name for change: " + serviceName);

            jwtToken = logAnalyticsPage.auth_token;
            Map<String, String> bsHealthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            log.info("Retrieved health score for service: " + serviceName);

            ArrayList<String>  metricArray = new ArrayList<>();
            JSONArray crcMetricIndicators =  changeSitRecord.getJSONArray("crcMetricIndicators");
            for (Object obj: crcMetricIndicators){
                String metricName = ((JSONObject)obj).getString("metricName");
                metricArray.add(metricName);
            }
            log.info("Collected " + metricArray.size() + " CRC metric indicators");

            //GPT Insight
            end_point = getGPTInsightEndPoint + changeId;
            log.info("Fetching GPT insights from endpoint: " + end_point);
            logAnalyticsPage.Send_RestCall("GET", end_point, new String[]{"200"});
            String changeGPTInsight = logAnalyticsPage.jsonObject.getString("gptInsights");
            log.info("Retrieved GPT insights for change: " + changeId);

            String llmQuery = "What possible issues can be caused by this change? " + changeShortDesc;
            double similarity = aiopsDeepRCA.getDeepRCAScore(llmQuery, changeGPTInsight);
            log.info("Semantic similarity between LLM output and gptInsights: " + similarity);

            if (similarity < 0.70) {
                softAssert.fail("Semantic similarity below threshold for change_id: " + changeId + " (value: " + similarity + ")");
                log.warn("Semantic similarity below threshold for change_id: " + changeId);
            }

            body = aiopsApiCore.readJSon(getchangemetriclistFileName);
            body = body.replace("<SERVICE_NAME>",bsHealthScore.get("key"));
            log.info("Fetching service metrics from endpoint: " + getServicemetricList);
            logAnalyticsPage.Send_RestCall("POST", getServicemetricList, new String[]{"200",body});
            JSONArray metricList =  logAnalyticsPage.jsonObject.getJSONArray("metricDisplayNames");
            log.info("Retrieved " + metricList.length() + " metrics for validation");

            for(Object obj: metricList){
                JSONObject metricName = (JSONObject)obj;
                String displayNames = metricName.getString("metricDisplayName");
                boolean found = metricArray.stream()
                        .anyMatch(s -> s.contains(displayNames));
                if (!found) {
                    log.warn("metric not present" + displayNames + " array list" + metricArray);
                }
            }
            log.info("Validated all metrics against CRC indicators");

            log.info("Validate Health Score");
            float CRAhealthscore = changeSitRecord.getJSONArray("serviceDetails").getJSONObject(0).getFloat("health");
            if (CRAhealthscore != Float.parseFloat(bsHealthScore.get("health"))){
                softAssert.fail("health Score does not match not present for service " + serviceName);
                log.warn("Health score mismatch for service: " + serviceName);
            } else {
                log.info("Health score validated successfully for service: " + serviceName);
            }
            log.info("Completed processing change " + (i+1) + "/5");
        }

        softAssert.assertAll();
        log.info("DRSQA_12516_Verify_HelixGPT_CRA_AIOPS_change_risk_assessment_summarization_Best_Action_Risk_Mitigation test completed successfully");

    }

    @Test(description = "[CRA] Verify data in CRA for crq which is not associated with service", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "17580", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17580_Verify_data_in_CRA_for_crq_which_is_not_associated_with_service(ITestContext context) throws Exception {
        String ChangeListFileName = "getchangerisklist.json";
        log.info("Starting test: DRSQA_17580_Verify_data_in_CRA_for_crq_which_is_not_associated_with_service");
        if(jwtToken == null) {
            prerquirements();
        }
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Change Risk Advisor")){
            log.info("CRA feature is not enabled; skipping test as passed.");
            return;
        }
        softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String getchangemetriclistFileName = "getchangemetriclist.json";
        
        log.info("Starting DRSQA_17580_Verify_data_in_CRA_for_crq_which_is_not_associated_with_service test");
        
        jwtToken = commonmethod.refreshJwtToken(logAnalyticsPage.auth_token);
        log.info("Create a change request which is not associated with service");
        String changeSummary = "Memory increase request without any associated service or CI - " + RandomStringUtils.insecure().nextNumeric(5);
        String changeDescription = "Increase the memory allocation of virtual machine to remediate sustained memory saturation impacting service stability.";
        Map<String, String> changeIdentifiers = cmdbRestApi.createChangeRequestUpdateStatus(changeSummary, changeDescription, "Implementation In Progress",username,"US Support 3");
        if (changeIdentifiers == null) {
            Assert.fail("Failed to create and progress change request without any associated service or CI");
            return;
        }
        commonwait.waitForSometime(10000);

        log.info("changeIdentifiers: " + changeIdentifiers);
        ZonedDateTime actualStartTimeUtc = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime actualEndTimeUtc = actualStartTimeUtc.plusMinutes(5);
        DateTimeFormatter remedyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.000+0000'");
        
        cmdbRestApi.moveChangeStatusComplete(
            changeIdentifiers.get("infrachangeid"),
            changeIdentifiers.get("RequestID"),
            actualStartTimeUtc.format(remedyTimeFormatter),
            actualEndTimeUtc.format(remedyTimeFormatter));
        log.info("Moved change " + changeIdentifiers.get("infrachangeid") + " to Completed status");
        String jwtToken = commonmethod.refreshJwtToken(logAnalyticsPage.auth_token);
        log.info("Generated JWT token and set access credentials");

        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }

        String body = aiopsApiCore.readJSon(ChangeListFileName);

        log.info("Fetching change records from endpoint: " + getChangeListEndPoint);
        logAnalyticsPage.Send_RestCall("POST", getChangeListEndPoint, new String[]{"200",body});
        JSONObject record =  logAnalyticsPage.jsonObject;
        JSONArray  changerecord = logAnalyticsPage.jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("changes");
        log.info("Retrieved " + changerecord.length() + " change records");

        if (changerecord.length() < 1){
            Assert.fail("No change Record fetch");
        }

        // Valid change statuses to process
        List<String> validChangeStatuses = new ArrayList<>();
        validChangeStatuses.add("Closed");
        validChangeStatuses.add("Implementation In Progress");
        validChangeStatuses.add("Completed");

        for (int i =0;i<8;i = i+1){
            String changeId = changerecord.getJSONObject(i).getString("change_id");
            String changeStatus = changerecord.getJSONObject(i).getString("change_status");
            String changeShortDesc = changerecord.getJSONObject(i).getString("change_short_desc");
            log.info("Processing change " + (i+1) + "/5 - Change ID: " + changeId + ", Status: " + changeStatus);

            if(!changeId.equals(changeIdentifiers.get("infrachangeid"))){
                continue;
            }
            // Validate change status

            String end_point = CRAInsightEndPoint + changeId;
            log.info("Fetching insights for change ID: " + changeId + " from endpoint: " + end_point);
            logAnalyticsPage.Send_RestCall("GET", end_point, new String[]{"200"});
            JSONObject changeSitRecord = logAnalyticsPage.jsonObject;
            log.info("Return code will be 404 as no service is associated with the change");
            if (logAnalyticsPage.response_code < 299){
                Assert.fail("Return code is not 404 as no service is associated with the change");
            }
            break;
        }

        softAssert.assertAll();
        log.info("DRSQA_17580_Verify_data_in_CRA_for_crq_which_is_not_associated_with_service test completed successfully");

    }



    @Test(description = "[CRA] Verify the scenario where the service in already processed CRM data is deleted", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "17578", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17578_Verify_the_scenario_where_the_service_in_already_processed_CRM_data_is_deleted(ITestContext context) throws Exception {
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Change Risk Advisor")){
            log.info("CRA feature is not enabled; skipping test as passed.");
            return;
        }
        String ChangeListFileName = "getchangerisklist.json";
        log.info("Starting test: DRSQA_17578_Verify_the_scenario_where_the_service_in_already_processed_CRM_data_is_deleted");
        if(jwtToken == null) {
            prerquirements();
        }
        softAssert = new SoftAssert();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String getchangemetriclistFileName = "getchangemetriclist.json";
        String serviceName = "DB-Machine";
        String[] ciList = {"host-15"};
        
        
        log.info("Starting DRSQA_17580_Verify_data_in_CRA_for_crq_which_is_not_associated_with_service test");
        
        
        log.info("Create a change request which is not associated with service");
        String changeSummary = "Memory increase request for service " + serviceName + " - " + RandomStringUtils.insecure().nextNumeric(5);
        String changeDescription = "Increase the memory allocation of virtual machine for service " + serviceName + " to remediate sustained memory saturation impacting service stability.";
        Map<String, String> changeIdentifiers = cmdbRestApi.createChangeRequestUpdateStatus(changeSummary, changeDescription, serviceName, ciList, username,"US Support 3");
        if (changeIdentifiers == null) {
            Assert.fail("Failed to create and progress change request for service " + serviceName);
        }
        if(!aiopsDeepRCA.moveChangeTorequestStatus(changeIdentifiers,"Implementation In Progress")){
            Assert.fail("Failed to progress change request for service " + serviceName);
        }

        commonwait.waitForSometime(10000);

        log.info("changeIdentifiers: " + changeIdentifiers);
        ZonedDateTime actualStartTimeUtc = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime actualEndTimeUtc = actualStartTimeUtc.plusMinutes(5);
        DateTimeFormatter remedyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.000+0000'");
        
        cmdbRestApi.moveChangeStatusComplete(
            changeIdentifiers.get("infrachangeid"),
            changeIdentifiers.get("RequestID"),
            actualStartTimeUtc.format(remedyTimeFormatter),
            actualEndTimeUtc.format(remedyTimeFormatter));
        log.info("Moved change " + changeIdentifiers.get("infrachangeid") + " to Completed status");
        String jwtToken = commonmethod.refreshJwtToken(logAnalyticsPage.auth_token);
        log.info("Generated JWT token and set access credentials");

        boolean isBrowserLevelProfile = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isBrowserLevelProfile) {
            Assert.fail("Set browser level profile failed");
        }

        String body = aiopsApiCore.readJSon(ChangeListFileName);

        log.info("Fetching change records from endpoint: " + getChangeListEndPoint);
        logAnalyticsPage.Send_RestCall("POST", getChangeListEndPoint, new String[]{"200",body});
        JSONObject record =  logAnalyticsPage.jsonObject;
        JSONArray  changerecord = logAnalyticsPage.jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("changes");
        log.info("Retrieved " + changerecord.length() + " change records");

        if (changerecord.length() < 1){
            Assert.fail("No change Record fetch");
        }

        // Valid change statuses to process
        List<String> validChangeStatuses = new ArrayList<>();
        validChangeStatuses.add("Closed");
        validChangeStatuses.add("Implementation In Progress");
        validChangeStatuses.add("Completed");

        for (int i =0;i<8;i = i+1){
            String changeId = changerecord.getJSONObject(i).getString("change_id");
            String changeStatus = changerecord.getJSONObject(i).getString("change_status");
            String changeShortDesc = changerecord.getJSONObject(i).getString("change_short_desc");
            log.info("Processing change " + (i+1) + "/5 - Change ID: " + changeId + ", Status: " + changeStatus);

            if(!changeId.equals(changeIdentifiers.get("infrachangeid"))){
                continue;
            }
            // Validate change status

            log.info("Fetching CRA summary for change ID: " + changeId);

            Map.Entry<Integer, JSONObject> changeSitRecord = aiopsDeepRCA.getCRAGPTInsights(changeId);
            if (changeSitRecord.getKey() > 299){
                Assert.fail("Get CRA summary failed: " + changeSitRecord.getKey());
            }
            String changeGPTInsight = changeSitRecord.getValue().getString("gptInsights");
            String llmQuery = "What possible issues can be caused by this change? " + changeShortDesc;
            double similarity = aiopsDeepRCA.getDeepRCAScore(llmQuery, changeGPTInsight);
            log.info("Semantic similarity between LLM output and gptInsights: " + similarity);

            if (similarity < 0.70) {
                softAssert.fail("Semantic similarity below threshold for change_id: " + changeId + " (value: " + similarity + ")");
                log.warn("Semantic similarity below threshold for change_id: " + changeId);
            }
            break;
        }

        softAssert.assertAll();
        log.info("DRSQA_17580_Verify_data_in_CRA_for_crq_which_is_not_associated_with_service test completed successfully");

    }




    /* 
    @Test(description = "DRSQA-12516 [Change Risk Advisor]: Dummy", groups = {"SolQA"})
    @SolqaDetails(authorName = "Haris", JiraID = "12516", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12516_Dummy(ITestContext context) throws Exception {
        LogAnalyticsPage loga = new LogAnalyticsPage();
        loga.auth_type = 2;
        loga.getloganalyticscookies();

        IntelligentAutomation ia = new IntelligentAutomation();
        ia.setxsrftoken(loga.seleniumCookies);
        ia.setrestassuredcookies(loga.seleniumCookies);
        ia.enableBlackoutIntegration(false);
        ia.delete_change_data_workflow();
        ia.configureIaBlackoutPolicyWorkflow();
        ia.updateBlackoutpolicyStatus();
        ia.enableBlackoutIntegration(true);


    }
    */

    
    @Test(description = "[CRA] Verify CRA for addition and deletion of metrics in Health indicators", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "17583", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17583_Verify_CRA_for_addition_and_deletion_of_metrics_in_Health_indicators(ITestContext context) throws Exception {
        String serviceName = "ML-TSOM";
        softAssert = new SoftAssert();
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Change Risk Advisor")){
            log.info("CRA feature is not enabled; skipping test as passed.");
            return;
        }
        
        log.info("Starting test: DRSQA_17578_Verify_the_scenario_where_the_service_in_already_processed_CRM_data_is_deleted");
        boolean isBrowserLevelProfile = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isBrowserLevelProfile) {
            Assert.fail("Set browser level profile failed");
        }


        log.info("ML-TSOM: resolve model key, load service/get, then add or remove IdleTime metric via models/multi PATCH");
        IdleTimeAdjustResult idleMetricAdjusted = aiopsSituation.adjustIdleTimeMetricOnServiceModel("ML-TSOM", jwtToken);
        softAssert.assertTrue(idleMetricAdjusted.success,
                "Failed to adjust IdleTime on ML-TSOM (service/get or PATCH /smart-graph-api/api/v1.11/models/multi)");

        log.info("Fetching change records from endpoint: " + getChangeListEndPoint);
        JSONObject record =  aiopsDeepRCA.getCRAList();
        if (record == null){
            Assert.fail("Get CRA list failed");
        }
        JSONArray  changerecord = record.getJSONArray("data").getJSONObject(0).getJSONArray("changes");
        log.info("Retrieved " + changerecord.length() + " change records");

        if (changerecord.length() < 1){
            Assert.fail("No change Record fetch");
        }

        // Valid change statuses to process
        List<String> validChangeStatuses = new ArrayList<>();
        validChangeStatuses.add("Closed");
        validChangeStatuses.add("Implementation In Progress");
        validChangeStatuses.add("Completed");
        
        
        for (int i =0;i<changerecord.length();i = i+1){
            if(i> 19){
                break;
            }
            String changeId = changerecord.getJSONObject(i).getString("change_id");
            String changeStatus = changerecord.getJSONObject(i).getString("change_status");
            String changeShortDesc = changerecord.getJSONObject(i).getString("change_short_desc");
            log.info("Processing change " + (i+1) + "/5 - Change ID: " + changeId + ", Status: " + changeStatus);

            // Validate change status
            if (!validChangeStatuses.contains(changeStatus)) {
                log.warn("Skipping change " + changeId + " - Status '" + changeStatus + "' is not in valid statuses list");
                continue;
            }

            JSONObject changeSitRecord = aiopsDeepRCA.getCRAInsightDetails(changeId);
            if (changeSitRecord == null){
                continue;
            }

            Map.Entry<Integer, JSONObject> changeGPTInsight = aiopsDeepRCA.getCRAGPTInsights(changeId);
            if (changeGPTInsight.getKey() > 299){
                continue;
            }

            JSONArray crcMetricIndicators = changeSitRecord.getJSONArray("crcMetricIndicators");
            // GPT insights body: { "tenantId": "...", "serviceCi": [ "ML-TSOM" ] }
            JSONArray serviceCiArr = changeSitRecord.optJSONArray("serviceCi");
            String gptServiceName = (serviceCiArr != null && serviceCiArr.length() > 0)
                    ? serviceCiArr.getString(0)
                    : "";
            if (gptServiceName.isEmpty()) {
                continue;
            }
            log.info("Service name from GPT insights (serviceCi): " + gptServiceName);
            if(crcMetricIndicators.length() < 1){
                continue;
            }
            if (!gptServiceName.equals(serviceName)) {
                log.warn("Skipping change " + changeId + " — GPT serviceCi (" + gptServiceName + ") does not match expected (" + serviceName + ")");
                continue;
            }

            log.info("Check if require Metric is present.");
            for(Object obj: crcMetricIndicators){
                JSONObject metricIndicator = (JSONObject)obj;
                String metricName = metricIndicator.getString("metricName");
                if (metricName.equals("IdleTime") && idleMetricAdjusted.change == IdleTimeMetricChange.ADDED) {
                    softAssert.fail("IdleTime metric is present in CRC metric indicators");
                    break;
                }
                
            }

        }

        softAssert.assertAll();
        log.info("DRSQA_17583_Verify_CRA_for_addition_and_deletion_of_metrics_in_Health_indicators test completed successfully");
    }

}