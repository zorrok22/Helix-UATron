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
import java.util.Random;

public class BhomAlarmRange {
    private static final SolqaLogger log = new SolqaLogger(BhomAlarmRange.class.getName());
    HMEvents hm_events = null;
    CommonMethods commonmethod = null;
    SoftAssert softAssert = null;
    private String jwtToken = null;
    BhomMonitoring hm_monitoring = null;
    private String alarmPolicyName = "predict";
    private String AlarmSheetName = "MultiAlarm";
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    @BeforeMethod(alwaysRun = true)
    public void prerequisites() throws IOException {
        log.info("Setting up the test...");
        hm_events = new HMEvents();
        commonmethod = new CommonMethods();
        softAssert = new SoftAssert();
        hm_monitoring = new BhomMonitoring();
        hm_monitoring.setRemoteHostname();
        jwtToken = commonmethod.refreshJwtToken(null);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() throws Exception {
        log.info("Running cleanup for test...");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        try {
            log.info("Attempting to decrease CPU utilization");
            hm_monitoring.decreaseCPUUtilization();
        } catch (Exception e) {
            log.warn("Failed to decrease CPU utilization: " + e.getMessage());
        }
        try {
            log.info("Deleting alarm policies with key: " + alarmPolicyName);
            hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        } catch (Exception e) {
            log.warn("Failed to delete alarm policies: " + e.getMessage());
        }
    }

    @Test(description = "[Alarm Range Threshold] Verify the In Range, Out Range Operators for lower, upper and boundary values and thresholds", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18971", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18971_Validate_In_Range_Operators_for_lower_upper_and_boundary_values_and_thresholds(ITestContext context) throws IOException, InterruptedException, JSchException {
        log.info("Starting the test...");
        AiopsPrediction aiopsPrediction = new AiopsPrediction();
        String alarmTC = "1";
        log.info("Starting test: DRSQA_18971_Validate_In_Range_Operators_for_lower_upper_and_boundary_values_and_thresholds");
        if(jwtToken == null) {
            prerequisites();
        }
        hm_monitoring.decreaseCPUUtilization();
        List<Map<String, Object>> alarmdataRows = excelReaderForMLSituation.getTestDataFromExcel(Integer.parseInt(alarmTC), AlarmSheetName);
        if (alarmdataRows == null || alarmdataRows.isEmpty()) {
            Assert.fail("No alarm configuration rows found for sheet " + AlarmSheetName + " testcase " + alarmTC);
        }

        String deviceName = alarmdataRows.get(0).get("deviceName").toString();
        String moTypeId = alarmdataRows.get(0).get("moTypeId").toString();
        String SearchMetricName = alarmdataRows.get(0).get("SearchMetricName").toString();
        String attributeId = alarmdataRows.get(0).get("attributeId").toString();


        

        Map<String, String> metricIdMap = hm_monitoring.getDeviceMetricId(deviceName, SearchMetricName, moTypeId, jwtToken);
        if(metricIdMap == null){
            Assert.fail("Failed to get device metric ID for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        String monitorId = metricIdMap.get("monitorId");
        String serverId = metricIdMap.get("serverId");
        String deviceid = metricIdMap.get("deviceid");
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        JSONObject metricValue = hm_monitoring.getDeviceMetricvalue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        if(metricValue == null){
            Assert.fail("Failed to get device metric value for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        
        double lastvalue = hm_monitoring.extractLatestMetricValue(metricValue);
        if(lastvalue == -1.0){
            Assert.fail("Failed to get latest metric value no data collected yet");
        }
        log.info("Create Alarm Policy for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();
        // Create baseline/prediction alarm on CPU utilization for the node

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        String policyName = alarmPolicyName + "_" + new Random().nextInt(1000);
        String label = patrolPolicies.get(0)[2];
        String metricId = patrolPolicies.get(0)[1];
        String unit = patrolPolicies.get(0)[4];
        String condition = "in_range";
        String severity = "MAJOR";
        int startRangeValue = (int)(lastvalue) + 10;
        int endRangeValue = 95;
        String startRange = String.valueOf(startRangeValue);
        String endRange = String.valueOf(endRangeValue);
        boolean isCreated = hm_monitoring.createConditionAlarmsPolicy(policyName, moTypeId, metricId, label, "Linux", unit, condition, severity, startRange, endRange, jwtToken);
        if(!isCreated){
            Assert.fail("Failed to create alarm policy for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        hm_monitoring.increaseCPUUtilization();
        double currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        if(currentValue == -1.0){
            Assert.fail("Failed to get latest metric value no data collected yet");
        }
        currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("MAJOR", deviceName, jwtToken);
        if(alarm == null){
            Assert.fail("Failed to get alarm for " + deviceName);
        }
    }

    @Test(description = "[Alarm Range Threshold] Verify the In Range, Out Range Operators for lower, upper and boundary values and thresholds", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18971", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18971_Validate_Out_Range_Operators_for_lower_upper_and_boundary_values_and_thresholds(ITestContext context) throws IOException, InterruptedException, JSchException {
        log.info("Starting the test...");
        AiopsPrediction aiopsPrediction = new AiopsPrediction();
        String alarmTC = "1";
        log.info("Starting test: DRSQA_18971_Validate_Out_Range_Operators_for_lower_upper_and_boundary_values_and_thresholds");
        if(jwtToken == null) {
            prerequisites();
        }
        hm_monitoring.decreaseCPUUtilization();
        List<Map<String, Object>> alarmdataRows = excelReaderForMLSituation.getTestDataFromExcel(Integer.parseInt(alarmTC), AlarmSheetName);
        if (alarmdataRows == null || alarmdataRows.isEmpty()) {
            Assert.fail("No alarm configuration rows found for sheet " + AlarmSheetName + " testcase " + alarmTC);
        }

        String deviceName = alarmdataRows.get(0).get("deviceName").toString();
        String moTypeId = alarmdataRows.get(0).get("moTypeId").toString();
        String SearchMetricName = alarmdataRows.get(0).get("SearchMetricName").toString();
        String attributeId = alarmdataRows.get(0).get("attributeId").toString();


        

        Map<String, String> metricIdMap = hm_monitoring.getDeviceMetricId(deviceName, SearchMetricName, moTypeId, jwtToken);
        if(metricIdMap == null){
            Assert.fail("Failed to get device metric ID for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        String monitorId = metricIdMap.get("monitorId");
        String serverId = metricIdMap.get("serverId");
        String deviceid = metricIdMap.get("deviceid");
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        JSONObject metricValue = hm_monitoring.getDeviceMetricvalue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        if(metricValue == null){
            Assert.fail("Failed to get device metric value for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        
        double lastvalue = hm_monitoring.extractLatestMetricValue(metricValue);
        if(lastvalue == -1.0){
            Assert.fail("Failed to get latest metric value no data collected yet");
        }
        log.info("Create Alarm Policy for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        List<String[]> patrolPolicies = aiopsPrediction.getPatrolPolicies();
        // Create baseline/prediction alarm on CPU utilization for the node

        if(patrolPolicies == null){
            log.info("No patrol policies found in Excel");
            Assert.fail("No patrol policies found in Excel");
        }

        String policyName = alarmPolicyName + "_" + new Random().nextInt(1000);
        String label = patrolPolicies.get(0)[2];
        String metricId = patrolPolicies.get(0)[1];
        String unit = patrolPolicies.get(0)[4];
        String condition = "out_range";
        String severity = "MAJOR";
        int startRangeValue = 0;
        int endRangeValue = (int)(lastvalue) - 1;
        String endRange = String.valueOf(endRangeValue);
        String startRange = String.valueOf(startRangeValue);
        boolean isCreated = hm_monitoring.createConditionAlarmsPolicy(policyName, moTypeId, metricId, label, "Linux", unit, condition, severity, startRange, endRange, jwtToken);
        if(!isCreated){
            Assert.fail("Failed to create alarm policy for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        hm_monitoring.increaseCPUUtilization();
        double currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        if(currentValue == -1.0){
            Assert.fail("Failed to get latest metric value no data collected yet");
        }
        currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("MAJOR", deviceName, jwtToken);
        if(alarm == null){
            Assert.fail("Failed to get alarm for " + deviceName);
        }


        softAssert.assertAll();
        
    }

}
