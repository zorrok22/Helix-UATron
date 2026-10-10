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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BhomRBACTest {
    private static final SolqaLogger log = new SolqaLogger(BhomRBACTest.class.getName());
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
    LogAnalyticsPage loganalyticspage = null;
    String compositeAlarmNamePrefix = "Composite Alarm Test";
    String compositeAlarmSheet = "CompositeAlarm";
    String className = "CompositeAlarm";
    String msearchQueryFile = "msearchqueryonOpenClass.json";
    private static final int RBAC_CONFIG_TC = 2;
    private static final String RBAC_CONFIG_SHEET = "rbacconfig";
    /** When true, {@link #afterMethod()} skips cleanup (e.g. feature-disabled early exit). */
    private boolean skipAfterMethodCleanup = false;
    long timediff = 1000 * 60 * 60 * 24 * 1; // 1 day in milliseconds
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
        loganalyticspage = new LogAnalyticsPage();
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

    @Test(description = "Verify msearch to support unrestricted service access for RBAC users", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "17952", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17952_verify_msearch_to_support_unrestricted_service_access_for_RBAC_users(ITestContext context) throws Exception { 
        jwtToken = commonmethod.refreshJwtToken(null);

        List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(RBAC_CONFIG_TC, RBAC_CONFIG_SHEET);
        String rbacUsername = (String) rows.get(0).get("username");
        String rbacPassword = (String) rows.get(0).get("password");

        if(!aiopsDeepRCA.TenantFeatureStatusCheck("RBAC")){
            log.info("BAP feature is not enabled; skipping test as passed.");
            skipAfterMethodCleanup = true;
            return;
        }
        
        log.info("Get the msearch at admin level");
        String msearchEventsBody = "msearchsituationqueryall.json";

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        int adminrecordcount = hitsArray.length();
        log.info("Admin record count: " + adminrecordcount);

        
        loganalyticspage.setadeuserandpassword(rbacUsername, rbacPassword);

        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }

        log.info("Get the msearch at rbac level");
        JSONObject rbacSituationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject rbacHitsobject = rbacSituationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray rbacHitsArray = rbacHitsobject.getJSONArray("hits");
        int rbacrecordcount = rbacHitsArray.length();
        log.info("RBAC record count: " + rbacrecordcount);

        if(rbacrecordcount != adminrecordcount){
            Assert.fail("RBAC record count does not match admin record count");
        }
        log.info("RBAC record count matches admin record count");

    }
}
