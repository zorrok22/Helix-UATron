package ITOM.Aiops;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AiopsMasterSituationTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsMasterSituationTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String base_url = config.get("ADE_PLATFORM_URL");
    public static final String configuration_end_point = "/aiops-config/api/v1.0/situation_configurations/search";

    private static final int event_cnt = 1;
    private static final long situationTimeDiff = 300000;
    public static final String aiopssituationconfigUrl = "/aiops-config/api/v1.0/situation_configurations/";
    public static final String event_tag = "";
    private static String eventClass = "NAGIOS_EV1";
    private static String location = "Pune";
    HMEvents hm_events = null;
    CommonWaits commonWait = null;
    Aiops aiopsApiCore = null;
    private SoftAssert softAssert = null;
    CommonMethods commonmethod = null;
    String eventSeverity = "CRITICAL";
    String jwtToken = null;
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
        commonmethod = new CommonMethods();
        commonWait = new CommonWaits();
        softAssert = new SoftAssert();
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
    
    @Test(description = "[MasterSituation]:Validate Master situation when situation created on multiple nodes when nodes are topologically connected", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12450", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12450_Validate_Master_situation_when_situation_created_on_multiple_nodes_when_nodes_are_topologically_connected(ITestContext context)
            throws IOException, InterruptedException {
        String MasterSituationFile = "msearchMastersituationquery.json";
        int testcase_number = 1;
        log.info("Starting test: DRSQA_12450_Validate_Master_situation_when_situation_created_on_multiple_nodes_when_nodes_are_topologically_connected");
        if(jwtToken == null) {
            prerquirements();
        }
        String mlSheetName = "MasterSituation";
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        String serviceNameExcel = row_data.get(0).get("Service Name").toString();
        String nodeNameExcel = row_data.get(0).get("Impact Node").toString();
        String[] ServiceList = serviceNameExcel.split(",");
        String[] NodeList = nodeNameExcel.split(",");
        String situationJsonFile = "msearchsituationquery.json";
        if (aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is enabled; skipping test as passed.");
            return;
        }
        if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
            Assert.fail("Failed to enable ml-situations feature.");
        }
        for(String serviceName: ServiceList){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            log.info("Resetting health score for service " + serviceName);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"),jwtToken);
        }

        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            log.info("Sending event to " + hostname + " with message: " + msg);
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(ServiceList[0], jwtToken);
        log.info("Waiting for situation creation for service " + ServiceList[0]);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("JWT token refreshed post situation creation");
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationJsonFile, situationTimeDiff);
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        String situationIdentifier = "";
        for( Object hit : hitsobject.getJSONArray("hits")) {
            JSONObject hitObject = (JSONObject) hit;
            String EventClass = hitObject.getJSONObject("_source").getString("class");
            if(EventClass.equals("Situation")){
                situationIdentifier = hitObject.getJSONObject("_source").getString("_identifier");
                log.info("Situation identifier: " + situationIdentifier);
                break;
            }
        }
        
        log.info("Wait for situation to get saturated");
        for (int j=0;j<21;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Saturation check attempt " + (j+1));
            if (aiopsApiCore.verifySituationSaturated(situationIdentifier,jwtToken)) {
                log.info("Situation saturated");
                break;
            }
            commonWait.waitForSometime(15000);
        }
        boolean founddata = false;
        log.info("Polling for master situation creation");
        for (int j=0;j<10;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Master situation poll attempt " + (j+1));
            situationObject = hm_events.runMSearchQueryGetSituation(ServiceList[0], jwtToken, MasterSituationFile, situationTimeDiff);
            int hitCount = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (hitCount > 0) {
                //$.responses[0].hits.total.value
                log.info("Master situation found");
                founddata = true;
                break;
            }
            commonWait.waitForSometime(15000);
        }
        if(!founddata){
            softAssert.fail("Master situation not created");
        }
        softAssert.assertAll();
    }
}
