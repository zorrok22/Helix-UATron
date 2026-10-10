package ITOM.Bhom;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Bhom.BhomCompositeAlarm;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BhomCompositeAlarmTest {
    private static final SolqaLogger log = new SolqaLogger(BhomCompositeAlarmTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");

    private static final String BASE_URL = config.get("ADE_PLATFORM_URL");
    HMEvents hm_events = null;
    BhomCompositeAlarm bhomCompositeAlarm = null;
    CommonWaits commonWait = null;
    CommonMethods commonmethod = null;
    SoftAssert softAssert = null;
    private String jwtToken = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    String compositeAlarmNamePrefix = "Composite Alarm Test";
    String compositeAlarmSheet = "CompositeAlarm";
    String className = "CompositeAlarm";
    String msearchQueryFile = "msearchqueryonOpenClass.json";
    /** When true, {@link #afterMethod()} skips cleanup (e.g. feature-disabled early exit). */
    private boolean skipAfterMethodCleanup = false;
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    @BeforeMethod(alwaysRun = true) 
    public void beforeMethod() {
        skipAfterMethodCleanup = false;
        log.info("Setting up the test...");
        commonmethod = new CommonMethods();
        jwtToken = commonmethod.refreshJwtToken(null);
        commonWait = new CommonWaits();
        hm_events = new HMEvents();
        bhomCompositeAlarm = new BhomCompositeAlarm();
        softAssert = new SoftAssert();
        aiopsDeepRCA = new AiopsDeepRCA();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (skipAfterMethodCleanup) {
            log.info("Skipping after-method cleanup (test exited early, e.g. feature not enabled).");
            skipAfterMethodCleanup = false;
            return;
        }
        log.info("Cleanup composite alarm");
        boolean success = bhomCompositeAlarm.deleteSearchCompositeAlarms(compositeAlarmNamePrefix);
        if(!success){
            log.error("Failed to delete composite alarm");
        }
    }

    @Test(description = "[Composite threshold] Validate composite alarm with multi-metric expression", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18206", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18206_verify_composite_alarm_with_multi_metric_expression(ITestContext context) throws IOException {
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("Check if BAP feature is enabled");
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Composite Alarm")){
            log.info("Composite Alarm feature is not enabled; skipping test as passed.");
            skipAfterMethodCleanup = true;
            return;
        }

        String compositeAlarmName = compositeAlarmNamePrefix + "-" + RandomStringUtils.randomAlphabetic(5);

        boolean success = bhomCompositeAlarm.deleteSearchCompositeAlarms(compositeAlarmNamePrefix);
        if(!success){
            log.error("Failed to delete composite alarm");
        }

        int testcase = 2;
        List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(testcase, compositeAlarmSheet);
        String compositeExpression = rows.get(0).get("Composite Expression").toString();
        String compositeEventMessage = rows.get(0).get("Composite Event Message").toString();
        boolean createSuccess = bhomCompositeAlarm.createCompositeAlarm(compositeAlarmName, compositeExpression, compositeEventMessage);
        if(!createSuccess){
            log.error("Failed to create composite alarm");
            Assert.fail("Failed to create composite alarm");
        }

        log.info("Re-try for 10 minutes below to check if composite alarm is created");
        boolean found = false;
        for (int i = 0; i < 41; i++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(className, jwtToken, msearchQueryFile, 36000);
            JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
            if (hitsobject.length() < 1) {
                log.info("No hits found, waiting before next attempt");
                commonWait.waitForSometime(15000);
                continue;
            }
            boolean recordfound = false;
            for (Object obj : hitsobject) {
                JSONObject event = (JSONObject) obj;
                String msg = event.getJSONObject("_source").getString("msg");
                String status = event.getJSONObject("_source").getString("status");
                if (!status.equals("OPEN")) {
                    log.info("Composite alarm is not open, waiting before next attempt");
                    continue;
                }
                if (msg.contains(compositeEventMessage)) {
                    log.info("Found composite alarm message: " + msg);
                    recordfound = true;
                    break;
                }
                
            }
            if (recordfound) {
                log.info("Composite alarm found");
                found = true;
                break;
            }else{
                log.info("Composite alarm not found, waiting before next attempt");
                commonWait.waitForSometime(15000);
            }
        }
        if (!found) {
            log.error("Composite alarm not found");
            Assert.fail("Composite alarm not found");
        }
    }

    @Test(description = "[Composite threshold] Validate E2E flow of Composite Alarm Policy", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18201", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18201_verify_e2e_flow_of_composite_alarm_policy(ITestContext context) throws IOException {
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("Check if BAP feature is enabled");
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Composite Alarm")){
            log.info("Composite Alarm feature is not enabled; skipping test as passed.");
            skipAfterMethodCleanup = true;
            return;
        }

        String compositeAlarmName = compositeAlarmNamePrefix + "-" + RandomStringUtils.randomAlphabetic(5);

        boolean success = bhomCompositeAlarm.deleteSearchCompositeAlarms(compositeAlarmNamePrefix);
        if(!success){
            log.error("Failed to delete composite alarm");
        }

        

        success = bhomCompositeAlarm.deleteSearchCompositeAlarms(compositeAlarmNamePrefix);
        if(!success){
            log.error("Failed to delete composite alarm");
            Assert.fail("Failed to delete composite alarm");
        }

        int testcase = 1;
        List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(testcase, compositeAlarmSheet);
        String compositeExpression = rows.get(0).get("Composite Expression").toString();
        String compositeEventMessage = rows.get(0).get("Composite Event Message").toString();
        boolean createSuccess = bhomCompositeAlarm.createCompositeAlarm(compositeAlarmName, compositeExpression, compositeEventMessage);
        if(!createSuccess){
            log.error("Failed to create composite alarm");
            Assert.fail("Failed to create composite alarm");
        }

        log.info("Re-try for 5 minutes below to check if composite alarm is created");
        boolean found = false;
        for (int i = 0; i < 25; i++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(className, jwtToken, msearchQueryFile, 36000);
            JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
            if (hitsobject.length() < 1) {
                log.info("No hits found, waiting before next attempt");
                commonWait.waitForSometime(15000);
                continue;
            }
            boolean recordfound = false;
            for (Object obj : hitsobject) {
                JSONObject event = (JSONObject) obj;
                String msg = event.getJSONObject("_source").getString("msg");
                String status = event.getJSONObject("_source").getString("status");
                if (!status.equals("OPEN")) {
                    log.info("Composite alarm is not open, waiting before next attempt");
                    continue;
                }
                if (msg.contains(compositeEventMessage)) {
                    log.info("Found composite alarm message: " + msg);
                    recordfound = true;
                    break;
                }
                
            }
            if (recordfound) {
                log.info("Composite alarm found");
                found = true;
                break;
            }else{
                log.info("Composite alarm not found, waiting before next attempt");
                commonWait.waitForSometime(15000);
            }
        }
        if (!found) {
            log.error("Composite alarm not found");
            Assert.fail("Composite alarm not found");
        }
    }
}
