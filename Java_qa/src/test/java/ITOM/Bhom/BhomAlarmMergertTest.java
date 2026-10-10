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

import pages.ITOM.Api.HMEvents;
import pages.ITOM.Bhom.BhomMonitoring;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BhomAlarmMergertTest {
    private static final SolqaLogger log = new SolqaLogger(BhomAlarmMergertTest.class.getName());
    HMEvents hm_events = null;
    CommonWaits commonWait = null;
    CommonMethods commonmethod = null;
    SoftAssert softAssert = null;
    private String jwtToken = null;
    BhomMonitoring hm_monitoring = null;
    private String createdPolicyName = null;
    private String alarmPolicyName = "predict";
    private String AlarmSheetName = "MultiAlarm";
    private ExcelReaderForMLSituation excelReaderForMLSituation;

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
        excelReaderForMLSituation = new ExcelReaderForMLSituation();
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

    @Test(description = "[Alarm Merger] Validate Alarm Merging for GLOBAL and MULTI policies with same precedence levels", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "16326", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_16326_Validate_Alarm_Merging_for_GLOBAL_and_MULTI_Policies_with_Different_Precedence_Levels_with_same_and_changing_severity(ITestContext context) throws IOException, InterruptedException, JSchException {
        log.info("Starting the test...");       
        String alarmTC = "1";
        log.info("Starting test: DRSQA_16326_Validate_Alarm_Merging_for_GLOBAL_and_MULTI_Policies_with_Different_Precedence_Levels_with_same_and_changing_severity");
        if(jwtToken == null) {
            prerquirements();
        }
        List<Map<String, Object>> alarmdataRows = excelReaderForMLSituation.getTestDataFromExcel(Integer.parseInt(alarmTC), AlarmSheetName);
        if (alarmdataRows == null || alarmdataRows.isEmpty()) {
            failWithLog("No alarm configuration rows found for sheet " + AlarmSheetName + " testcase " + alarmTC);
        }

        String deviceName = alarmdataRows.get(0).get("deviceName").toString();
        String moTypeId = alarmdataRows.get(0).get("moTypeId").toString();
        String SearchMetricName = alarmdataRows.get(0).get("SearchMetricName").toString();
        String attributeId = alarmdataRows.get(0).get("attributeId").toString();


        

        Map<String, String> metricIdMap = hm_monitoring.getDeviceMetricId(deviceName, SearchMetricName, moTypeId, jwtToken);
        if(metricIdMap == null){
            failWithLog("Failed to get device metric ID for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        String monitorId = metricIdMap.get("monitorId");
        String serverId = metricIdMap.get("serverId");
        String deviceid = metricIdMap.get("deviceid");
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        JSONObject metricValue = hm_monitoring.getDeviceMetricvalue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        if(metricValue == null){
            failWithLog("Failed to get device metric value for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        
        double lastvalue = hm_monitoring.extractLatestMetricValue(metricValue);
        if(lastvalue == -1.0){
            failWithLog("Failed to get latest metric value no data collected yet");
        }
        log.info("Last metric value: " + lastvalue);
        String sheetName = "patrolPolicies";
        String testcase = "1";
        List<Map<String, Object>> alarmRows = excelReaderForMLSituation.getTestDataFromExcel(Integer.parseInt(testcase), sheetName);
        if (alarmRows == null || alarmRows.isEmpty()) {
            failWithLog("No alarm configuration rows found for sheet " + sheetName + " testcase " + testcase);
        }

        Map<String, Object> row = null;
        for(int i = 0; i < alarmRows.size(); i++){
            row = alarmRows.get(i);
            if(row.get("entityTypeId").toString().equals(moTypeId) && row.get("metricId").toString().equals(attributeId)){
                break;
            }
        }
        if(row == null){
            failWithLog("No alarm configuration found for " + moTypeId + " " + attributeId);
        }

        // Build lists for three thresholds/severities
        List<String> entityTypeIds = new ArrayList<>();
        List<String> metricIds = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> systemTypes = new ArrayList<>();
        List<String> units = new ArrayList<>();
        List<Integer> thresholds = List.of(25, 50, 75);
        List<String> severities = List.of("MINOR", "MAJOR", "CRITICAL");

        for (int i = 0; i < 3; i++) {
            entityTypeIds.add(row.get("entityTypeId").toString());
            metricIds.add(row.get("metricId").toString());
            labels.add(row.get("label").toString());
            systemTypes.add(row.get("systemType").toString());
            units.add(row.get("unit").toString());
        }

        createdPolicyName = alarmPolicyName;
        String devicesub = deviceName.substring(0, deviceName.indexOf("."));
        boolean alarmsCreated = hm_monitoring.createMultipleAlarms(alarmPolicyName + "-MULTI", entityTypeIds, metricIds, labels, systemTypes, units, thresholds, severities, 9999, "MULTI", devicesub, jwtToken);
        if (!alarmsCreated) {
            failWithLog("Failed to create multiple alarms");
        }
        alarmsCreated = hm_monitoring.createMultipleAlarms(alarmPolicyName + "-ALL", entityTypeIds, metricIds, labels, systemTypes, units, thresholds, severities, 991, "ALL", "", jwtToken);
        if (!alarmsCreated) {
            failWithLog("Failed to create multiple alarms");
        }
        
        for(int i = 0; i < 3; i++){
            double currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
            if (currentValue < 25) {
                hm_monitoring.increaseCPUUtilization();
                continue;
            }

            if (currentValue < 50) {
                JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("MINOR", deviceName, jwtToken);
                if(alarm == null){
                    log.error("Failed to get alarm for " + deviceName);
                    commonWait.waitForSometime(15000);
                    continue;
                }
                log.info("Get the Log and Notes");
                String logandnotes = alarm.getJSONObject("_source").getString("_operations");
                if(!logandnotes.contains(alarmPolicyName + "-ALL")){
                    softFailWithLog("Alarm " + alarm.getJSONObject("_source").getString("alarm_name") + " is not merged for " + deviceName);
                }
                hm_monitoring.increaseCPUUtilization();
                currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
            }

            if (currentValue < 75) {
                JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("MAJOR", deviceName, jwtToken);
                if(alarm == null){
                    softFailWithLog("Failed to get alarm for " + deviceName);
                }
                log.info("Get the Log and Notes");
                String logandnotes = alarm.getJSONObject("_source").getString("_operations");
                if(!logandnotes.contains(alarmPolicyName + "-ALL")){
                    softFailWithLog("Alarm " + alarm.getJSONObject("_source").getString("alarm_name") + " is not merged for " + deviceName);
                }
                hm_monitoring.increaseCPUUtilization();
                currentValue = hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
            }
            if(currentValue >= 75){
                JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("CRITICAL", deviceName, jwtToken);
                if(alarm == null){
                    softFailWithLog("Failed to get alarm for " + deviceName);
                }
                log.info("Get the Log and Notes");
                String logandnotes = alarm.getJSONObject("_source").getString("_operations");
                if(!logandnotes.contains(alarmPolicyName + "-ALL")){
                    softFailWithLog("Alarm " + alarm.getJSONObject("_source").getString("alarm_name") + " is not merged for " + deviceName);
                }
                break;
            }
            
        }
        
        softAssert.assertAll();
        
    }

    @Test(description = "[Alarm Merger] Validate Alarm Merging for GLOBAL and MULTI Policies with Different Precedence Levels with same and changing severity", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "16327", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_16327_Validate_Alarm_Merging_for_GLOBAL_and_MULTI_Policies_with_Different_Precedence_Levels_with_same_and_changing_severity(ITestContext context) throws IOException, InterruptedException, JSchException {
        log.info("Starting the test...");       
        String alarmTC = "2";
        log.info("Starting test: DRSQA_16327_Validate_Alarm_Merging_for_GLOBAL_and_MULTI_Policies_with_Different_Precedence_Levels_with_same_and_changing_severity");
        if(jwtToken == null) {
            prerquirements();
        }
        softAssert = new SoftAssert();
        List<Map<String, Object>> alarmdataRows = excelReaderForMLSituation.getTestDataFromExcel(Integer.parseInt(alarmTC), AlarmSheetName);
        if (alarmdataRows == null || alarmdataRows.isEmpty()) {
            failWithLog("No alarm configuration rows found for sheet " + AlarmSheetName + " testcase " + alarmTC);
        }

        String deviceName = alarmdataRows.get(0).get("deviceName").toString();
        String moTypeId = alarmdataRows.get(0).get("moTypeId").toString();
        String SearchMetricName = alarmdataRows.get(0).get("SearchMetricName").toString();
        String attributeId = alarmdataRows.get(0).get("attributeId").toString();
        
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        Map<String, String> metricIdMap = hm_monitoring.getDeviceMetricId(deviceName, SearchMetricName, moTypeId, jwtToken);
        if(metricIdMap == null){
            failWithLog("Failed to get device metric ID for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        String monitorId = metricIdMap.get("monitorId");
        String serverId = metricIdMap.get("serverId");
        String deviceid = metricIdMap.get("deviceid");
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName, jwtToken);
        JSONObject metricValue = hm_monitoring.getDeviceMetricvalue(moTypeId, monitorId, serverId, attributeId, jwtToken);
        if(metricValue == null){
            failWithLog("Failed to get device metric value for " + deviceName + " " + SearchMetricName + " " + moTypeId);
        }
        
        double lastvalue = hm_monitoring.extractLatestMetricValue(metricValue);
        if(lastvalue == -1.0){
            failWithLog("Failed to get latest metric value no data collected yet");
        }
        log.info("Last metric value: " + lastvalue);
        String sheetName = "patrolPolicies";
        String testcase = "1";
        List<Map<String, Object>> alarmRows = excelReaderForMLSituation.getTestDataFromExcel(Integer.parseInt(testcase), sheetName);
        if (alarmRows == null || alarmRows.isEmpty()) {
            failWithLog("No alarm configuration rows found for sheet " + sheetName + " testcase " + testcase);
        }

        Map<String, Object> row = null;
        for(int i = 0; i < alarmRows.size(); i++){
            row = alarmRows.get(i);
            if(row.get("entityTypeId").toString().equals(moTypeId) && row.get("metricId").toString().equals(attributeId)){
                break;
            }
        }
        if(row == null){
            failWithLog("No alarm configuration found for " + moTypeId + " " + attributeId);
        }

        // Build lists for three thresholds/severities
        List<String> entityTypeIds = new ArrayList<>();
        List<String> metricIds = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> systemTypes = new ArrayList<>();
        List<String> units = new ArrayList<>();
        List<Integer> thresholds = List.of(25, 50, 75);
        List<String> severities = List.of("MINOR", "MAJOR", "CRITICAL");

        createdPolicyName = alarmPolicyName;
        int executionOrder = 9991;
        String type = "MULTI";
        String devicesub = deviceName.substring(0, deviceName.indexOf("."));
        for (int i = 0; i < 3; i++) {
            entityTypeIds.add(row.get("entityTypeId").toString());
            metricIds.add(row.get("metricId").toString());
            labels.add(row.get("label").toString());
            systemTypes.add(row.get("systemType").toString());
            units.add(row.get("unit").toString());
            if(i%2 == 0){
                type = "MULTI";
            } else {
                type = "ALL";
            }
            boolean alarmsCreated = hm_monitoring.createMultipleAlarms(alarmPolicyName + "-" + type, entityTypeIds, metricIds, labels, systemTypes, units, List.of(thresholds.get(i)), List.of(severities.get(i)), executionOrder, type, devicesub, jwtToken);
            if (!alarmsCreated) {
                failWithLog("Failed to create multiple alarms");
            }
            entityTypeIds.clear();
            metricIds.clear();
            labels.clear();
            systemTypes.clear();
            units.clear();
        }

        log.info("wait for 1 minute to get the alarm");
        commonWait.waitForSometime(60000);
        for(int i = 0; i < 3; i++){
            hm_monitoring.getDeviceLatestMetricValue(moTypeId, monitorId, serverId, attributeId, jwtToken);
            metricIdMap = hm_monitoring.getDeviceMetricId(deviceName, SearchMetricName, moTypeId, jwtToken);
            double currentValue = hm_monitoring.extractLatestMetricValue(metricValue);
            if (currentValue < 25) {
                hm_monitoring.increaseCPUUtilization();
                continue;
            }

            if (currentValue < 50) {
                JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("MINOR", deviceName, jwtToken);
                if(alarm == null){
                    softFailWithLog("Failed to get alarm for " + deviceName);
                }
                log.info("Get the Log and Notes");
                String logandnotes = alarm.getJSONObject("_source").getString("_operations");
                if(!logandnotes.contains(alarmPolicyName + "-MULTI")){
                    softFailWithLog("Alarm " + alarm.getJSONObject("_source").getString("alarm_name") + " is not merged for " + deviceName);
                }
                hm_monitoring.increaseCPUUtilization();
                currentValue = hm_monitoring.extractLatestMetricValue(metricValue);
            }

            if (currentValue < 75) {
                JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("MAJOR", deviceName, jwtToken);
                if(alarm == null){
                    softFailWithLog("Failed to get alarm for " + deviceName);
                }
                log.info("Get the Log and Notes");
                String logandnotes = alarm.getJSONObject("_source").getString("_operations");
                if(!logandnotes.contains(alarmPolicyName + "-ALL")){
                    softFailWithLog("Alarm " + alarm.getJSONObject("_source").getString("alarm_name") + " is not merged for " + deviceName);
                }
                hm_monitoring.increaseCPUUtilization();
                currentValue = hm_monitoring.extractLatestMetricValue(metricValue);
            }
            if(currentValue >= 75){
                JSONObject alarm = hm_monitoring.getAlarmsSeverityAndHost("CRITICAL", deviceName, jwtToken);
                if(alarm == null){
                    softFailWithLog("Failed to get alarm for " + deviceName);
                }
                log.info("Get the Log and Notes");
                String logandnotes = alarm.getJSONObject("_source").getString("_operations");
                if(!logandnotes.contains(alarmPolicyName + "-MULTI")){
                    softFailWithLog("Alarm " + alarm.getJSONObject("_source").getString("alarm_name") + " is not merged for " + deviceName);
                }
                break;
            }
            
        }
        
        softAssert.assertAll();
    }

    private void failWithLog(String message){
        log.error(message);
        Assert.fail(message);
    }

    private void softFailWithLog(String message){
        log.error(message);
        softAssert.fail(message);
    }
}
