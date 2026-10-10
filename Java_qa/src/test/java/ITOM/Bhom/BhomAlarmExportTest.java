package ITOM.Bhom;

import com.jcraft.jsch.JSchException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Bhom.BhomMonitoring;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BhomAlarmExportTest {
    private static final SolqaLogger log = new SolqaLogger(BhomAlarmExportTest.class.getName());
    HMEvents hm_events = null;
    CommonWaits commonWait = null;
    CommonMethods commonmethod = null;
    SoftAssert softAssert = null;
    private String jwtToken = null;
    BhomMonitoring hm_monitoring = null;
    private String createdPolicyName = null;
    private String alarmPolicyName = "predict";
    private String className = "ALARM";
    private String AlarmSheetName = "MultiAlarm";
    AiopsPrediction aiopsPrediction = null;
    private String SERVICE_PREFIX = "predict";

    @BeforeMethod(alwaysRun = true)
    public void prerquirements() throws IOException {
        log.info("Setting up the test...");
        hm_events = new HMEvents();
        commonWait = new CommonWaits();
        commonmethod = new CommonMethods();
        softAssert = new SoftAssert();
        hm_monitoring = new BhomMonitoring();
        hm_monitoring.setRemoteHostname();
        jwtToken = commonmethod.refreshJwtToken(null);
        aiopsPrediction = new AiopsPrediction();
        try {
            log.info("Deleting existing alarm policies with key: " + alarmPolicyName);
            hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        } catch (Exception e) {
            log.warn("Failed to delete alarm policies: " + e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() throws Exception {
        log.info("Running cleanup for test...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        try {
            log.info("Deleting alarm policies with key: " + alarmPolicyName);
            hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        } catch (Exception e) {
            log.warn("Failed to delete alarm policies: " + e.getMessage());
        }
    }

    @Test(description = "[Export alarm policy] Verify option on UI to download a CSV report for all monitored servers and their associated global and instance thresholds.", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "17020", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_17020_Verify_option_on_UI_to_download_a_CSV_report_for_all_monitored_servers_and_their_associated_global_and_instance_thresholds(ITestContext context) throws IOException, InterruptedException, JSchException {
        log.info("Starting the test...");       
        log.info("Starting test: DRSQA_17020_Verify_option_on_UI_to_download_a_CSV_report_for_all_monitored_servers_and_their_associated_global_and_instance_thresholds");
        if(jwtToken == null) {
            prerquirements();
        }
        JSONObject alarmPolicyList = hm_monitoring.getAlarmPolicyList(jwtToken);
        if(alarmPolicyList == null){
            log.error("Failed to get alarm policy list");
            Assert.fail("Failed to get alarm policy list");
        }

        String ExportAlarmPolicyJsonContent = hm_monitoring.exportAlarmPolicy(jwtToken);

        JSONArray policyNameArray = alarmPolicyList.getJSONArray("policies");
        for(Object policyName : policyNameArray){
            JSONObject policyNameObject = (JSONObject) policyName;
            String policyNameString = policyNameObject.getString("policyName");
            if (!ExportAlarmPolicyJsonContent.contains(policyNameString + ",")) {
                log.error("Exported alarm policy CSV does not contain policy name: " + policyNameString);
                softAssert.fail("Exported alarm policy CSV does not contain policy name: " + policyNameString);
            }
            log.info("Exported alarm policy CSV contains policy: " + policyNameString);
        }

        log.info("Create a Alarm Policy to verify the export");
        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();
        log.info("create max 5 alarm policy");
        int policyCout = 0;
        for (String[] policy : patrolPolicies) {
            hm_events.createPatrolAgentAlarmPolicy(alarmPolicyName, policy[0], policy[1], policy[2], policy[3], policy[4], false, jwtToken);
            policyCout++;
            if(policyCout >= 5){
                break;
            }
        }
        log.info("Wait for 10 seconds to create the alarm policy");
        commonWait.waitForSometime(20000);
        alarmPolicyList = hm_monitoring.getAlarmPolicyList(jwtToken);
        ExportAlarmPolicyJsonContent = hm_monitoring.exportAlarmPolicy(jwtToken);
        for(Object policyName : policyNameArray){
            JSONObject policyNameObject = (JSONObject) policyName;
            String policyNameString = policyNameObject.getString("policyName");
            if (!ExportAlarmPolicyJsonContent.contains(policyNameString + ",")) {
                log.error("Exported alarm policy CSV does not contain policy name: " + policyNameString);
                softAssert.fail("Exported alarm policy CSV does not contain policy name: " + policyNameString);
            }
        }

        log.info("delete alarm policy");
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        log.info("Search and delete exisitng Alarm Policy");
        commonWait.waitForSometime(10000);
        alarmPolicyList = hm_monitoring.getAlarmPolicyList(jwtToken);
        ExportAlarmPolicyJsonContent = hm_monitoring.exportAlarmPolicy(jwtToken);
        for(Object policyName : policyNameArray){
            JSONObject policyNameObject = (JSONObject) policyName;
            String policyNameString = policyNameObject.getString("policyName");
            if (!ExportAlarmPolicyJsonContent.contains(policyNameString + ",")) {
                log.error("Exported alarm policy CSV does not contain policy name: " + policyNameString);
                softAssert.fail("Exported alarm policy CSV does not contain policy name: " + policyNameString);
            }
        }

        if (ExportAlarmPolicyJsonContent.contains(SERVICE_PREFIX + ",")) {
            log.error("Exported alarm policy CSV contains policy name: " + SERVICE_PREFIX);
            softAssert.fail("Exported alarm policy CSV contains policy name: " + SERVICE_PREFIX);
        }

        softAssert.assertAll();
    }
}
