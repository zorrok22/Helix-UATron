package ITOM.Aiops;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class AiopsSituationModelTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationModelTest.class.getName());
    public static final String configuration_end_point = "/aiops-config/api/v1.0/situation_configurations/search";
    CommonMethods commonmethod = new CommonMethods();
    private static final int event_cnt = 1;
    private static final long situationTimeDiff = 300000;
    public static final String aiopssituationconfigUrl = "/aiops-config/api/v1.0/situation_configurations/";
    
    
    




    @Test(description = "AIOps[ML] : Validate ML Situation creation for ITSM model with Incident DRSQA-12510", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12510", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12510_AIOps_ML_Validate_ML_Situation_creation_for_ITSM_model_Incident(ITestContext context) throws IOException {
        AiopsSituation aiopsSituation = new AiopsSituation();
        SoftAssert softAssert = new SoftAssert();
        int ITSM_testCase = 66;
        AiopsSituation.incidentValidation = true;
        aiopsSituation.run_ml_scenario(ITSM_testCase, softAssert);
        softAssert.assertAll();

    }

    @Test(description = "AIOps[ML] : Validate ML Situation creation for ADE model with Incident DRSQA-12515", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12515", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12515_AIOps_ML_Validate_ML_Situation_creation_for_ADE_model_Incident(ITestContext context) throws IOException {
        int ITSM_testCase = 65;
        SoftAssert softAssert = new SoftAssert();
        AiopsSituation aiopsSituation = new AiopsSituation();
        AiopsSituation.incidentValidation = false;
        aiopsSituation.run_ml_scenario(ITSM_testCase, softAssert);
        softAssert.assertAll();

    }

    @Test(description = "AIOps[ML] : Validate ML Situation creation for TSOM HA model with Incident", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12401", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12401_Validate_ML_Situation_creation_for_TSOM_HA_model_with_Incident(ITestContext context) throws IOException {
        int ITSM_testCase = 2;
        SoftAssert softAssert = new SoftAssert();
        AiopsSituation aiopsSituation = new AiopsSituation();
        AiopsSituation.incidentValidation = false;
        aiopsSituation.setExpectedMinimumCICount(0);
        aiopsSituation.run_ml_scenario(ITSM_testCase, softAssert);
        softAssert.assertAll();

    }

    @Test(description = "AIOps[ML] : Validate ML Situation creation for different virtualization models", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12404", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12404_Validate_ML_Situation_creation_for_different_virtualization_models(ITestContext context) throws IOException {
        int ITSM_testCase = 3;
        SoftAssert softAssert = new SoftAssert();
        AiopsSituation aiopsSituation = new AiopsSituation();
        AiopsSituation.incidentValidation = false;
        aiopsSituation.run_ml_scenario(ITSM_testCase, softAssert);
        softAssert.assertAll();

    }

    @Test(description = "AIOps[ML] : Validate ML Situation creation for Kubernetes(k8s) model", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12403", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12403_Validate_ML_Situation_creation_for_Kubernetes_k8s_model(ITestContext context) throws IOException {
        int ITSM_testCase = 45;
        SoftAssert softAssert = new SoftAssert();
        AiopsSituation aiopsSituation = new AiopsSituation();
        AiopsSituation.incidentValidation = false;
        aiopsSituation.run_ml_scenario(ITSM_testCase, softAssert);
        softAssert.assertAll();

    }

    @Test(description = "AIOps[ML] : Validate ML Situation creation for cloud services model", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12402", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12402_Validate_ML_Situation_creation_for_cloud_services_model(ITestContext context) throws IOException {
        int ITSM_testCase = 11;
        SoftAssert softAssert = new SoftAssert();
        AiopsSituation aiopsSituation = new AiopsSituation();
        AiopsSituation.incidentValidation = false;
        aiopsSituation.run_ml_scenario(ITSM_testCase, softAssert);
        softAssert.assertAll();

    }

    @Test(description = "AIOps[ML] : Validate correlation event time and situation saturation time window", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12400", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12400_Validate_correlation_event_time_and_situation_saturation_time_window(ITestContext context) throws IOException {
        AiopsSituation aiopsSituation = new AiopsSituation();
        String eventClass = "NAGIOS_EV1";
        String location = "Pune";
        String event_tag = "";
        boolean incidentValidation = false;
        String serviceName = "ML-TSOM";
        String[] ciList = {"host-01-02", "host-01-03", "host-01-04", "host-01-07"};
        String changeHost = ciList[0];
        String situationJsonFile = "msearchsituationquery.json";
        SoftAssert softAssert = new SoftAssert();
        CommonWaits commonWait = new CommonWaits();
        Aiops aiopsApiCore = new Aiops();
        HMEvents eventApi = new HMEvents();
        String jwtToken = commonmethod.refreshJwtToken(null);
        log.info("Close all events for class " + eventClass);
        eventApi.CloseAllOpenEvent(jwtToken);
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        commonWait.waitForSometime(5000);
        log.info("Reset service health score for " + serviceName);
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        log.info("Send two events to " + changeHost + " with different messages to trigger first situation");
        eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, changeHost,
                "CPU utilization high on " + changeHost + " " + randomStr, "MAJOR", location, event_tag);
        eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, changeHost,
                "Memory utilization high on " + changeHost + " " + randomStr, "MAJOR", location, event_tag);

        log.info("Waiting for first situation creation");
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject msquery = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationJsonFile, situationTimeDiff);
        JSONObject hitsobject = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        int totalrecord = hitsobject.getJSONObject("total").getInt("value");
        if (totalrecord < 1) {
            Assert.fail("Expected at least one situation after sending initial events.");
        }
        String firstSituationIdentifier = hitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");

        log.info("Wait for situation to get saturated");
        boolean isSaturated = false;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        for (int i = 0; i < 25; i++) {
            if (aiopsApiCore.verifySituationSaturated(firstSituationIdentifier, jwtToken)) {
                isSaturated = true;
                break;
            }
            commonWait.waitForSometime(15000);
        }
        if (!isSaturated) {
            log.info("Situation did not get saturated within expected time.");
        }

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        randomStr = RandomStringUtils.randomAlphanumeric(7);
        log.info("Send another two events after saturation to validate new situation creation");
        eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, changeHost,
                "Disk utilization high on " + changeHost + " " + randomStr, "MAJOR", location, event_tag);
        eventApi.streamRestApiEvents(eventClass, event_cnt, jwtToken, changeHost,
                "Network latency high on " + changeHost + " " + randomStr, "MAJOR", location, event_tag);

        int expectedSituationCount = 2;
        int currentSituationCount = totalrecord;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        for (int i = 0; i < 12; i++) {
            msquery = eventApi.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationJsonFile, situationTimeDiff);
            hitsobject = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
            currentSituationCount = hitsobject.getJSONObject("total").getInt("value");
            if (currentSituationCount >= expectedSituationCount) {
                break;
            }
            commonWait.waitForSometime(15000);
        }

        if (currentSituationCount < expectedSituationCount) {
            softAssert.fail("Expected 2 situations after second event batch, but found " + currentSituationCount);
        }

        // Cleanup events and situations
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        for (int i = 0; i < hitsArray.length(); i++) {
            String situationIdentifier = hitsArray.getJSONObject(i).getJSONObject("_source").getString("_identifier");
            eventApi.closeEventApi(situationIdentifier, "Close Event", jwtToken);
        }
        eventApi.closeAllEventByClass(eventClass, jwtToken);
        softAssert.assertAll();
    }

    

}
