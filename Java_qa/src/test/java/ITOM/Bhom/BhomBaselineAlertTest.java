package ITOM.Bhom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BhomBaselineAlertTest {
    private static final SolqaLogger log = new SolqaLogger(BhomBaselineAlertTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");

    private static final String BASE_URL = config.get("ADE_PLATFORM_URL");
    HMEvents hm_events = new HMEvents();
    CommonWaits commonWait = new CommonWaits();
    CommonMethods commonmethod = new CommonMethods();
    SoftAssert softAssert = new SoftAssert();
    private String jwtToken = null;
    AiopsPrediction aiopsPrediction = new AiopsPrediction();


    @Test(description = "Baseline Alerting: Verify Metric above/below baseline threshold with Violates Baseline option selected", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12316", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12316_Verify_Metric_above_below_baseline_threshold_with_Violates_Baseline_option_selected_create(ITestContext context) throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("JWT token refreshed");
        String alarmPolicyName = "predict";
        String msearchQueryFile = "msearchqueryonOpenClass.json";
        String className = "ALARM";
        int maxAttempts = 20;
        log.info("Delete existing alarm policies with name: " + alarmPolicyName);
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);


        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();
        // Create baseline/prediction alarm on CPU utilization for the node

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        for (String[] policy : patrolPolicies) {
            if(policy[5].equals("false")){
                continue;
            }
            boolean success = hm_events.createBaselineAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], jwtToken);
            if(!success){
                Assert.fail("Failed to create baseline alarm policy for " + policy[0] + " " + policy[1] + " " + policy[2] + " " + policy[3] + " " + policy[4]);
            }

        }
        log.info("Initial wait after policy creation");
        commonWait.waitForSometime(1000);
    }

}
