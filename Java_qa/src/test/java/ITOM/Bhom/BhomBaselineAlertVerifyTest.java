package ITOM.Bhom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BhomBaselineAlertVerifyTest {
    private static final SolqaLogger log = new SolqaLogger(BhomBaselineAlertVerifyTest.class.getName());
    HMEvents hm_events = new HMEvents();
    CommonWaits commonWait = new CommonWaits();
    CommonMethods commonmethod = new CommonMethods();
    SoftAssert softAssert = new SoftAssert();
    private String jwtToken = null;


    @Test(description = "Baseline Alerting: Verify Metric above/below baseline threshold with Violates Baseline option selected", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12316", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12316_Verify_Metric_above_below_baseline_threshold_with_Violates_Baseline_option_selected_verify(ITestContext context) throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("JWT token refreshed");
        String alarmPolicyName = "predict";
        String msearchQueryFile = "msearchqueryonOpenClass.json";
        String className = "ALARM";
        int maxAttempts = 20;
        
        boolean founddata = false;
        log.info("Start polling for baseline alarm events, maxAttempts=" + maxAttempts);
        for (int i = 0; i < maxAttempts; i++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Attempt " + (i + 1) + ": refreshed JWT and running msearch for class " + className);
            JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(className, jwtToken, msearchQueryFile, 2);
            JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
            if (hitsobject.length() < 1) {
                log.info("No hits found, waiting before next attempt");
                commonWait.waitForSometime(15000);
                continue;
            }
            for (Object obj : hitsobject) {
                JSONObject event = (JSONObject) obj;


                String msg = event.getJSONObject("_source").getString("msg");

                if (msg.contains("violated")) {
                    log.info("Found baseline violation message: " + msg);
                    founddata = true;
                    break;
                }

            }
            log.info("Waiting before next poll iteration");
            commonWait.waitForSometime(15000);

        }
        log.info("Cleanup alarm policies");
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
        if (!founddata) {
            Assert.fail("No Baseline Alarm generated.");
        }
    }

    @Test(description = "Baseline Alerting: Verify Alarm slot in Anomaly detection in Event details page", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12317", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12317_Verify_Alarm_slot_in_Anomaly_detection_in_Event_details_page(ITestContext context) throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("JWT token refreshed");
        String alarmPolicyName = "predict";
        String msearchQueryFile = "msearchqueryonallClass.json";
        String className = "ALARM";
        int maxAttempts = 20;
        String[] baselineAlertList = {"al_baseline_daily_high","al_baseline_daily_low","al_baseline_high","al_baseline_hourly_high","al_baseline_hourly_low","al_baseline_low","al_baseline_monthly_high","al_baseline_monthly_low","al_baseline_weekly_high","al_baseline_weekly_low"};
        hm_events.SearchanddeleteAlarmPolicies("",jwtToken);
        JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(className, jwtToken, msearchQueryFile, 36000);
        JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
        if (hitsobject.length() < 1) {
            log.info("No hits found, waiting before next attempt");
            Assert.fail("No hits found");
        }
        for (Object obj : hitsobject) {
            JSONObject event = (JSONObject) obj;

            String msg = event.getJSONObject("_source").getString("msg");

            if (msg.contains("violated")) {
                log.info("Found baseline violation message: " + msg);
                log.info("Validate al_baseline slot in event details page");
                for (String baselineAlert : baselineAlertList) {
                    String baselineAlertValue = event.getJSONObject("_source").optString(baselineAlert, "-1");
                    if (baselineAlertValue.equals("-1")) {
                        log.info("Baseline alert not found: " + baselineAlert);
                        softAssert.fail("Baseline alert not found: " + baselineAlert);
                    }
                }
                break;
            }

        }
        log.info("Baseline Slot present in event details page");
        softAssert.assertAll();
    }

}
