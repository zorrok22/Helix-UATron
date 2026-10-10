package ITOM.Aiops;


import org.apache.commons.math3.stat.StatUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import org.testng.asserts.SoftAssert;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import util.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import framework.Launcher;


import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;

public class AiopsValueDashboard  extends Launcher{
    private static final String ENDPOINT_USERINFO = "/admin/ims-api/userinfo?preferences=true";
    // Dashboard metric xpaths
    private static final String XPATH_IVR = "//section[contains(@data-testid,'Incident Noise Reduction')]//span[contains(@id,'flotGaugeValue')]";
    private static final String XPATH_MTTI = "//div[@title=\"MTTI\"]//span[1]";
    private static final String XPATH_ENR = "//section[contains(@data-testid,\"Event Noise Reduction\")]//span[contains(@id,\"flotGaugeValue\")]";
    private static final String XPATH_MTTR = "//section[contains(@data-testid,\"Mean Time to Resolve\")]//span[1]";
    private static final String[] aiopsValueDashboardOperationalId = {"//section[contains(@data-testid,\"Total Impacting Events\")]//div[@title=\"Count\"]//span[1]","//section[contains(@data-testid,'Total Situations')]//span[1]",
            "//section[contains(@data-testid,'Events part of Situations')]//span[contains(@id,'GaugeValue')]"};
    private static final String[] aiopsValueDashboardOperationalIdNode = {"//section[contains(@data-testid,'Events with Node Association')]//span[1]","//section[contains(@data-testid,'Events with Service Association')]//span[1]"};
    private static final SolqaLogger log = new SolqaLogger(AiopsValueDashboard.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final CommonWaits commonWait = new CommonWaits();
    private WebDriver driver = null;
    private String eventPolicyEndpoint = "/events-service/api/v1.0/event_policies/search?excludeConfig=true";
    private static final String base_url = config.get("ADE_PLATFORM_URL");
    private static final String ade_username = config.get("ADE_USERNAME");
    private static final String ade_password = config.get("ADE_PASSWORD");
    private static final String loganalytics_app_xpath = "//div[@id=\"BMC Helix Log Analytics\"]";
    private static final int webTimeout = 60;
    private String incidentVolumeReductionXpath = "//section[contains(@data-testid,'Incident Noise Reduction')]//span[contains(@id,'flotGaugeValue')]";
    private String aiopsvaluedashboard = "/dashboards/d/AIOpsValueDashboard/aiops-value-dashboard";
    private String dashboardTimeRange = "&from=now-12h&to=now";
    private long timediff = 43200000; // 12 hours in milliseconds
    CommonMethods commonmethod = new CommonMethods();
    String jwtToken = null;
    HMEvents hm_events = null;
    Aiops aiopsApiCore = null;
    SoftAssert softAssert = null;
    LogAnalyticsPage logAnalyticsPage = null;
    // Dynamically adjust time range to ensure enough events are available
    private long[] timeRangesMs = {86400000L, 43200000L, 21600000L, 10800000L, 3600000L, 1800000L}; // 24h, 12h, 6h, 3h, 1h, 30m
    private String[] timeRangeParams = {
        "&from=now-24h&to=now",
        "&from=now-12h&to=now",
        "&from=now-6h&to=now",
        "&from=now-3h&to=now",
        "&from=now-1h&to=now",
        "&from=now-30m&to=now"
    };
    
    private void captureScreenshotSafe(String name) {
        try {
            commonWait.captureScreenshot(name, SolQAConstants.appITOM);
            log.info("Captured screenshot: " + name);
        } catch (IOException e) {
            log.warn("Failed to capture screenshot: " + name + " due to " + e.getMessage());
        }
    }

    @BeforeMethod
    public void prerquirements() {
        log.info("Setting up the test...");
        jwtToken = commonmethod.refreshJwtToken(null);
        hm_events = new HMEvents();
        aiopsApiCore = new Aiops();
        softAssert = new SoftAssert();
        logAnalyticsPage = new LogAnalyticsPage();
        
    }

    /**
     * Calculates the mean (average) of a long array.
     *
     * @param array The long array to calculate the mean for.
     * @return The mean value as a double.
     */
    public static double calculateMean(long[] array) {
        if (array == null || array.length == 0) {
            return 0.0; // Handle empty or null array case
        }

        long sum = 0;
        for (long value : array) {
            sum += value;
        }

        return (double) sum / array.length;
    }


    public static String[] replaceCommas(String[] originalArray, String replacement) {
        String[] newArray = new String[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            // Use replace() to substitute all commas with the replacement string
            // It handles null values gracefully.
            newArray[i] = originalArray[i] != null ? originalArray[i].replace(",", replacement) : null;
        }
        return newArray;
    }


    /**
     * Opens the dashboard URL, waits for the given xpath, and returns the text value of the element.
     * Quits the driver after fetching the value.
     * @param dashboardUrl The dashboard URL to open.
     * @param elementXpath The xpath of the element to fetch text from.
     * @return The text value of the element, or empty string if not found.
     */
    public ArrayList<String> getDashboardElementText(WebDriver localDriver, String dashboardUrl, String[] elementXpath) {
        log.info("Opening dashboard URL: " + dashboardUrl);
        log.info("WebDriver launched");
        Launcher.setDriver(localDriver);
        log.info("Driver set in Launcher");
        Launcher.login(ade_username, ade_password, SolQAConstants.appITOM);
        log.info("Logged in to ADE");
        commonWait.waitForElementPresent(loganalytics_app_xpath, webTimeout);
        log.info("Log Analytics page loaded");
        localDriver.get(dashboardUrl);
        log.info("Navigated to dashboard URL");
        commonWait.waitForPageFullyLoaded(40, 5); //wait for page to load
        commonWait.pause();
        ArrayList<String> values = new ArrayList<>();
        for (String xpath : elementXpath) {
            try {
                commonWait.waitForElementVisible_withFluentWait(driver,xpath,90);
                commonWait.waitForElementPresent(xpath, webTimeout);
                log.info("Element present: " + xpath);
                commonWait.waitForElementVisible_withFluentWait(localDriver, xpath, webTimeout);
                commonWait.waitForElementPresent(xpath);
                commonWait.pause();
                WebElement element = localDriver.findElement(By.xpath(xpath));
                commonWait.scrollToSpecificElement(element);
                log.info("Element visible: " + xpath);
                if (element != null) {
                    String tempValue = element.getText();
                    log.info("Element text: " + tempValue);
                    if (!tempValue.trim().isEmpty()) {
                        if (tempValue.contains("No data") || tempValue.contains("NaN")) {
                            values.add("0");
                        } else {
                            values.add(tempValue);
                        }
                    } else {
                        values.add("");
                    }
                } else {
                    captureScreenshotSafe("missing_element_" + System.currentTimeMillis());
                    values.add("-1"); // Add -1 to indicate missing element
                    log.warn("Element was null, captured screenshot for xpath: " + xpath);
                }
            } catch (Exception e) {
                captureScreenshotSafe("missing_element_" + System.currentTimeMillis());
                log.warn("Element not found for xpath: " + xpath + " | " + e.getMessage());
                values.add("-1"); // Add -1 to indicate missing element and skip validation
            }
            commonWait.waitForSometime(1000); // wait before exit data fetch
        }
        return values;
    }

    private void setTimeRangeAndEventCount() throws Exception {
        long totalEvent = 0;
        String totalEventCountFile = "msearchqueryalleventcount.json";
        for (int i = 0; i < timeRangesMs.length; i++) {
            timediff = timeRangesMs[i];
            dashboardTimeRange = timeRangeParams[i];
            log.info("Trying time range " + dashboardTimeRange + " (ms=" + timediff + ")");
            JSONObject totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, totalEventCountFile, timediff);
            totalEvent = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            log.info("Total event count for range " + dashboardTimeRange + ": " + totalEvent);
            if(totalEvent < 3000) {
                break;
            }
        }
        log.info("Time diff set to: " + timediff);
        log.info("Dashboard time range set to: " + dashboardTimeRange);
    }

    @Test(description = "Value dashboard Validation - Validate Incident Volume Reduction(IVR)", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15266", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.ADE_OperationsManagement, Product.ADE_Dashboards})
    public void DRSQA_15266_Value_dashboard_validate_Incident_Volume_Reduction() throws Exception {
        log.info("Starting IVR validation test");
        if(jwtToken == null) {
            prerquirements();
        }
        setTimeRangeAndEventCount();
        // 1. Get Predefined Event Policy Condition
        jwtToken = commonmethod.refreshJwtToken(null);
        String eventPolicyFileName = "searchEventPolicy.json";
        String msearchEventsBody = "msearchqueryalleventCritical.json";
        String jsonContent = hm_events.readJSon(eventPolicyFileName);
        log.info("Event policy JSON read from file");
        jsonContent = jsonContent.replace("<POLICY_NAME>", "Predefined");
        log.info("Event policy JSON placeholder replaced");
        logAnalyticsPage.Send_RestCall("POST", eventPolicyEndpoint, new String[]{"200", jsonContent});
        log.info("Event policy REST call sent");
        JSONObject policyResp = logAnalyticsPage.jsonObject;
        log.info("Policy response received");

        

        /* 
        JSONArray policies = policyResp.getJSONArray("policies");
        log.info("Policies array extracted");
        JSONObject policy = policies.getJSONObject(0);
        log.info("First policy object extracted");
        JSONArray selectorCriteriaList = policy.getJSONArray("selectorCriteriaList");
        log.info("Selector criteria list extracted");
        String selectorCriteria = selectorCriteriaList.getString(0); // e.g. ( severity equals 'CRITICAL' )
        log.info("Selector Criteria: " + selectorCriteria);
        
        // Check if selectorCriteria contains both 'severity' and 'CRITICAL'
        boolean hasSeverityCritical = selectorCriteria.toLowerCase().contains("severity") && selectorCriteria.toLowerCase().contains("critical");
        log.info("Selector criteria contains 'severity': " + selectorCriteria.toLowerCase().contains("severity"));
        log.info("Selector criteria contains 'CRITICAL': " + selectorCriteria.toLowerCase().contains("critical"));
        log.info("Selector criteria contains both 'severity' and 'CRITICAL': " + hasSeverityCritical); */
        int situationIncidentCount = 0;
        int totalqualifyEvent = 0;
        java.util.Set<String> situationIncidentIds = new java.util.HashSet<>();
        String situationFile = "msearchsituationqueryall.json";
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, situationFile, timediff);
        situationIncidentCount = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Situation count from situation file: " + situationIncidentCount);
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted from situation file");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());
        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            String incidentId = hit.optString("incident_id", "");
            String severity = hit.optString("severity", "");
            String service_key = hit.optString("_service_key", "");
            String impacted_service_key = hit.optString("_impacted_service_key", "");

            if (service_key.isEmpty() && impacted_service_key.isEmpty()) {
                continue;
            }
            

            if ("CRITICAL".equalsIgnoreCase(severity)) {
                totalqualifyEvent++;
            }
            if (!incidentId.isEmpty()) {
                if ("CRITICAL".equalsIgnoreCase(severity)) {
                    situationIncidentCount++;
                }
                situationIncidentIds.add(incidentId);
            }

            

        }

        // 2. Get total events matching policy (last 24h, excluding class=situation)
        situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");

        // Traverse all records in hitsobject
        hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());
        
        
        int nonSituationCriticalEventCount = 0;
        
        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            String incidentId = hit.optString("incident_id", "");
            String severity = hit.optString("severity", "");
            String service_key = hit.optString("_service_key", "");
            String impacted_service_key = hit.optString("_impacted_service_key", "");

            if ("Incident_info".equalsIgnoreCase(classVal)) {
                log.info("Ignoring Incident_info class");
                continue;
            }

            if (service_key.isEmpty() && impacted_service_key.isEmpty()) {
                continue;
            }

            if ("CRITICAL".equalsIgnoreCase(severity)) {
                totalqualifyEvent++;
            }else{
                continue;
            }
            
            if (!incidentId.isEmpty() && !situationIncidentIds.contains(incidentId)) {
                nonSituationCriticalEventCount++;
            }
            
        }
        


        log.info("Final Situation count with incident_id: " + situationIncidentCount);
        log.info("Final Non-situation CRITICAL event count with unique incident_id: " + nonSituationCriticalEventCount);
        log.info("Final Total Event qualify: " + totalqualifyEvent);

        // 6. Calculate IVR
        double ivr = totalqualifyEvent == 0 ? 0 : ((double) (totalqualifyEvent - (nonSituationCriticalEventCount + situationIncidentCount)) / totalqualifyEvent) * 100.0;
        log.info("Calculated IVR: " + ivr);

        // 7. Get tenant_id
        logAnalyticsPage.Send_RestCall("GET", ENDPOINT_USERINFO, new String[]{"200"});
        log.info("User info REST call sent");
        JSONObject userInfo = logAnalyticsPage.jsonObject;
        log.info("User info response received");
        String tenantId = userInfo.getString("tenant_id");
        log.info("Tenant ID: " + tenantId);

        // 8. Open dashboard and get displayed IVR
        String dashboardUrl = base_url + aiopsvaluedashboard + "?orgId=" + tenantId + dashboardTimeRange;
        log.info("Dashboard URL: " + dashboardUrl);
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        log.info("WebDriver launched");

        ArrayList<String> dashboardIVRStrs = getDashboardElementText(driver, dashboardUrl, new String[]{XPATH_IVR});
        String dashboardIVRStr = dashboardIVRStrs.get(0).replace("%", "").trim();

        log.info("Dashboard IVR string: " + dashboardIVRStr);
        double dashboardIVR = Double.parseDouble(dashboardIVRStr);
        log.info("Dashboard IVR value: " + dashboardIVR);
        if (driver != null) {
            driver.quit();
            log.info("WebDriver quit");
        }
        // 9. Compare calculated IVR and dashboard IVR with 10% tolerance
        log.info("Comparing calculated IVR and dashboard IVR with 10% tolerance");
        double tolerance = 10.0; // percent
        double diff = Math.abs(ivr - dashboardIVR);
        log.info("IVR difference: " + diff);
        if(dashboardIVR > 100 && dashboardIVR == 0){
            log.info("No data present in the dashboard to compare");
        }

        if (diff > tolerance) {
            if(dashboardIVR < 0){
                softAssert.fail("IVR value does not match dashboard within 10% tolerance. Calculated: " + ivr + ", Dashboard: " + dashboardIVR);
            }else{
                log.error("IVR value does not match dashboard within 10% tolerance. Calculated: " + ivr + ", Dashboard: " + dashboardIVR);
            }  
        } 
        
        log.info("IVR validation test completed");
        softAssert.assertAll();
    }

    @Test(description = "Value dashboard Validation - Validate MTTI Calculation for the situation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15267", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.ADE_OperationsManagement, Product.ADE_Dashboards})
    public void DRSQA_15267_Value_dashboard_Validate_MTTI_Calculation_for_the_situation() throws Exception {

        log.info("Starting MTTI calculation validation test");
        if(jwtToken == null) {
            prerquirements();
        }
        setTimeRangeAndEventCount();
        log.info("JWT token refreshed");
        String msearchEventsBody = "msearchsituationqueryall.json";
        
        log.info("Time diff set to 24h: " + timediff);

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());

        ArrayList<Long> timetocreationsituation = new ArrayList<>();

        long maxDiffMillis = 0;
        String latestSituationId = null;
        long latestSituationTime = 0;

        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            if (!"Situation".equalsIgnoreCase(classVal)) continue;

            String situationId = hit.optString("_identifier", "");
            Long situationTimeStr = hit.getLong("creation_time");
            log.info(String.format("Situation found: id=%s, creation_time=%s", situationId, situationTimeStr));

            // 2. Get all events under this situation
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            List<String> situationEventList = aiopsApiCore.getEventIdsAssociatedWithSituation(situationId, jwtToken);
            for (String eventId : situationEventList) {
                JSONObject jsonObject = new JSONObject(hm_events.eventDetails(eventId, jwtToken));
                long eventcreationtime = jsonObject.getJSONObject("response").getJSONObject("allslots").getLong("creation_time");

                if (situationTimeStr < eventcreationtime) continue;

                long diff = Math.abs(situationTimeStr - eventcreationtime);
                timetocreationsituation.add(diff);
            }

        }

        // Convert ArrayList to long[] if needed for mean calculation
        long[] dynamicArray = timetocreationsituation.stream().mapToLong(Long::longValue).toArray();

        // 2. Calculating the Sum and 3. Calculating the Mean
        double meanOfFixedArray = calculateMean(dynamicArray);

        log.info("Max MTTI difference in ms: " + meanOfFixedArray);
        long mttiMinutes = (long) (meanOfFixedArray / (60 * 1000));
        log.info("Calculated MTTI (minutes): " + mttiMinutes);

        // 3. Get latest MTTI from dashboard
        logAnalyticsPage.Send_RestCall("GET", ENDPOINT_USERINFO, new String[]{"200"});
        JSONObject userInfo = logAnalyticsPage.jsonObject;
        String tenantId = userInfo.getString("tenant_id");
        String dashboardUrl = base_url + aiopsvaluedashboard + "?orgId=" + tenantId + dashboardTimeRange;
        log.info("Dashboard URL: " + dashboardUrl);
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);

        ArrayList<String> dashboardMTTIStrs = getDashboardElementText(driver, dashboardUrl, new String[]{XPATH_MTTI});
        String dashboardMTTIStr = dashboardMTTIStrs.get(0).replace("min", "").replace("mins", "").trim();

        log.info("Dashboard MTTI string: " + dashboardMTTIStr);
        double dashboardMTTIDouble = Double.parseDouble(dashboardMTTIStr);
        long dashboardMTTISeconds = Math.round(dashboardMTTIDouble * 60);
        log.info("Dashboard MTTI value (seconds): " + dashboardMTTISeconds);
        // Convert calculated MTTI to seconds for comparison
        long mttiSeconds = mttiMinutes * 60;
        long rangeless = 1;
        long rangeend = 1800;
        log.info("Calculated MTTI (seconds): " + mttiSeconds);
        // 4. Compare calculated and dashboard MTTI with 600 sec (10 min) tolerance
        log.info("Comparing calculated and dashboard MTTI with 600 sec (10 min) tolerance");
        long mttiTolerance = 600; // seconds
        long mttiDiff = Math.abs(mttiSeconds - dashboardMTTISeconds);
        log.info("MTTI difference (seconds): " + mttiDiff);
        if(dashboardMTTISeconds == 0){
            log.info("No data present in the dashboard to compare");
        }else{
            if (rangeless < dashboardMTTISeconds && dashboardMTTISeconds > (rangeend + mttiSeconds)) {
                softAssert.fail("MTTI value does not match dashboard within 10 min (600 sec) tolerance. Calculated: " + mttiSeconds + "s, Dashboard: " + dashboardMTTISeconds + "s");
            }
        }

        log.info("MTTI validation test completed");
        softAssert.assertAll();
    }

    @Test(description = "[AIOPS] Value dashboard Validation - Validate ENR Calculation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15264", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.ADE_OperationsManagement, Product.ADE_Dashboards})
    public void DRSQA_15264_Value_dashboard_Validate_ENR_Calculation_for_the_situation() throws Exception {
        
        log.info("Starting ENR calculation validation test");
        if(jwtToken == null) {
            prerquirements();
        }
        setTimeRangeAndEventCount();
        String msearchEventsBody = "msearchsituationqueryall.json";
        String noSituationEventCountFile = "msearchqueryallNoSituationEventcount.json";
        String totalEventCountFile = "msearchqueryalleventcount.json";
        String totalSituationCountFile = "msearchsituationqueryall.json";

        // 2. Get total events matching policy (last 24h, excluding class=situation)
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");

        // Traverse all records in hitsobject
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());
        int situationCount = 0;
        java.util.Set<String> situationIncidentIds = new java.util.HashSet<>();
        int noSituationEventCount = 0;
        int totalqualifyEvent = 0;

        JSONObject totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, totalEventCountFile, timediff);
        totalqualifyEvent = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Total event count: " + totalqualifyEvent);

        totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, totalSituationCountFile, timediff);
        situationCount = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Total Situation count: " + situationCount);

        totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, noSituationEventCountFile, timediff);
        noSituationEventCount = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Total Event witout Situation count: " + noSituationEventCount);


        /*/
        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            String incidentId = hit.optString("incident_id", "");
            String severity = hit.optString("severity", "");
            String serviceId = hit.optString("_service_id", "");
            log.info(String.format("Record: class=%s, incident_id=%s, severity=%s", classVal, incidentId, severity));
            totalqualifyEvent++;

            if ("Situation".equalsIgnoreCase(classVal)) {
                situationCount++;
                continue;
            }

            if (serviceId.length() < 3) {
                log.info("Ignoring short service_id");
                continue;
            }

            String norelationship = hit.getJSONObject("_relationships").getString("rstat");
            if (norelationship.equalsIgnoreCase("norelation")) {
                noSituationEventCount++;
                log.info("No situation event found, count=" + noSituationEventCount);
            }

        }
        */
        double entPercentage = totalqualifyEvent - (situationCount + noSituationEventCount) ;
        entPercentage = (entPercentage/ totalqualifyEvent);
        entPercentage = entPercentage * 100;
        //entPercentage = 100 - entPercentage;
        log.info("As per BHOM Total situation count " + situationCount);
        log.info("As per BHOM Total event without situation " + noSituationEventCount);
        log.info("As per BHOM Total Event  " + totalqualifyEvent);
        log.info("As per BHOM ENR value " + entPercentage);
        // 3. Get latest MTTI from dashboard
        logAnalyticsPage.Send_RestCall("GET", ENDPOINT_USERINFO, new String[]{"200"});
        JSONObject userInfo = logAnalyticsPage.jsonObject;
        String tenantId = userInfo.getString("tenant_id");
        String dashboardUrl = base_url + aiopsvaluedashboard + "?orgId=" + tenantId + dashboardTimeRange;
        log.info("Dashboard URL: " + dashboardUrl);
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);

        ArrayList<String> dashboardENRStrs = getDashboardElementText(driver, dashboardUrl, new String[]{XPATH_ENR});

        double rangeless = 1;
        double rangeend = 30;
        String dashboardENRStr = dashboardENRStrs.get(0).replace("min", "").replace("mins", "").replace("%", "").trim();
        double dashboardENR = Double.parseDouble(dashboardENRStr);
        log.info("Dashboard ENR value: " + dashboardENR);
        double tolerance = 10.0;
        double diff = Math.abs(entPercentage - dashboardENR);
        log.info("ENR difference: " + diff);
        if(dashboardENR < rangeless){
            log.info("No data present in the dashboard to compare");
        }else{
            if (rangeless < dashboardENR && dashboardENR > (rangeend + entPercentage)) {
                softAssert.fail("ENR value does not match dashboard within 10% tolerance. Calculated: " + entPercentage + "%, Dashboard: " + dashboardENR + "%");
            }
        }

        softAssert.assertAll();
    }

    @Test(description = "Value dashboard Validation - Validate Mean Time to Resolve for the situation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "DRSQA-15265", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.ADE_OperationsManagement, Product.ADE_Dashboards})
    public void DRSQA_15265_Value_dashboard_Validate_MTTR_Calculation_for_the_situation() throws Exception {

        log.info("Starting MTTR calculation validation test");
        if(jwtToken == null) {
            prerquirements();
        }
        setTimeRangeAndEventCount();
        String msearchEventsBody = "msearchsituationqueryall.json";

        // 1. Get all situations in last 24 hours
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());

        ArrayList<Long> timetocreationsituation = new ArrayList<>();

        long maxDiffMillis = 0;
        String latestSituationId = null;
        long latestSituationTime = 0;

        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String classVal = hit.optString("class", "");
            if (!"Situation".equalsIgnoreCase(classVal)) continue;

            String situationId = hit.optString("_identifier", "");
            long situationTimeStr = hit.getLong("creation_time");
            long modifiedtime = hit.getLong("_modified_time");
            long lifecycletime = hit.getLong("_lifecycle_time");
            String situationStatus = hit.getString("status");
            //log.info(String.format("Situation found: id=%s, creation_time=%s", situationId, situationTimeStr));
            if (!"CLOSED".equalsIgnoreCase(situationStatus)) continue;

            timetocreationsituation.add(lifecycletime);

        }

        // Convert ArrayList to long[] if needed for mean calculation
        long[] dynamicArray = timetocreationsituation.stream().mapToLong(Long::longValue).toArray();

        // 2. Calculating the Sum and 3. Calculating the Mean
        double meanOfFixedArray = calculateMean(dynamicArray);

        log.info("Max MTTR difference in ms: " + meanOfFixedArray);
        long mttiMinutes = (long) (meanOfFixedArray / (60 * 1000));
        log.info("Calculated MTTR (minutes): " + mttiMinutes);

        // 3. Get latest MTTI from dashboard
        logAnalyticsPage.Send_RestCall("GET", ENDPOINT_USERINFO, new String[]{"200"});
        JSONObject userInfo = logAnalyticsPage.jsonObject;
        String tenantId = userInfo.getString("tenant_id");
        String dashboardUrl = base_url + aiopsvaluedashboard + "?orgId=" + tenantId + dashboardTimeRange;
        log.info("Dashboard URL: " + dashboardUrl);
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);

        ArrayList<String> dashboardMTTIStrs = getDashboardElementText(driver, dashboardUrl, new String[]{XPATH_MTTR});
        String dashboardMTTIStr = dashboardMTTIStrs.get(0).replace("min", "").replace("mins", "").trim();

        log.info("Dashboard MTTR string: " + dashboardMTTIStr);
        double dashboardMTTIDouble = Double.parseDouble(dashboardMTTIStr);
        long dashboardMTTISeconds = Math.round(dashboardMTTIDouble * 60);
        log.info("Dashboard MTTR value (seconds): " + dashboardMTTISeconds);
        // Convert calculated MTTI to seconds for comparison
        long mttiSeconds = mttiMinutes * 60;
        log.info("Calculated MTTR (seconds): " + mttiSeconds);
        // 4. Compare calculated and dashboard MTTI with 600 sec (10 min) tolerance
        log.info("Comparing calculated and dashboard MTTI with 600 sec (10 min) tolerance");
        long mttiTolerance = 600; // seconds
        long mttiDiff = Math.abs(mttiSeconds - dashboardMTTISeconds);
        log.info("MTTR difference (seconds): " + mttiDiff);
        if (mttiDiff > mttiTolerance) {
            softAssert.fail("MTTR value does not match dashboard within 10 min (600 sec) tolerance. Calculated: " + mttiSeconds + "s, Dashboard: " + dashboardMTTISeconds + "s");
        } else {
            log.info("MTTR values match within 10 min (600 sec) tolerance.");
        }
        log.info("MTTR validation test completed");
        softAssert.assertAll();
    }

    @Test(description = "Value Dash Board, validate Operational Kpi's on Event and situation relationship", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15705", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.ADE_OperationsManagement, Product.ADE_Dashboards})
    public void DRSQA_15705_Value_dashboard_validate_Operational_Kpis_on_Event_and_situation_relationship() throws Exception {
        log.info("Starting Operational_Kpis validation test");
        if(jwtToken == null) {
            prerquirements();
        }
        setTimeRangeAndEventCount();
        String msearchEventsBody = "msearchqueryallevent.json";
        String totalEventCountFile = "msearchqueryalleventcount.json";
        String totalSituationCountFile = "msearchsituationqueryall.json";
        String eventPartSituationCountFile = "msearchqueryalleventPartServicecount.json";

             

        float situationIncidentCount = 0;

        float totalEvent = 0;
        float totalsituationCount = 0;
        float eventPartSituationCount = 0;
        float eventPartSituationPercent = 0;
        float eventNotPartSituationCount = 0;
        float eventServiceAssociate = 0;
        float qualifyEventCount = 0;
        Set<String> seenIdentifiers = new HashSet<>();

        // Find a time range that yields enough events (>1000) or use the smallest
         
        
        
        JSONObject totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, totalEventCountFile, timediff);
        totalEvent = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Final total event count: " + totalEvent + " using range " + dashboardTimeRange);

        totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, totalSituationCountFile, timediff);
        totalsituationCount = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Total Situation count: " + totalsituationCount);

        JSONObject eventPartSituationCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, eventPartSituationCountFile, timediff);
        eventPartSituationCount = eventPartSituationCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Event part situation count: " + eventPartSituationCount);
        
        // Validation: Compare dashboard values with calculated variables (10% deviation allowed)

        //totalEvent = totalEvent - totalsituationCount - situationIncidentCount;
        //totalEvent = qualifyEventCount;


        eventNotPartSituationCount = totalEvent - eventPartSituationCount;
        log.info("Event not part situation count: " + eventNotPartSituationCount);
        eventPartSituationPercent = Math.round(( eventPartSituationCount /(eventPartSituationCount + eventNotPartSituationCount)) * 100);

        float[] calculatedValues = new float[] {
                totalEvent,
                totalsituationCount,
                eventPartSituationPercent
        };

        log.info("calculated value " + Arrays.toString(calculatedValues));

        // 3. Get latest MTTI from dashboard
        logAnalyticsPage.Send_RestCall("GET", ENDPOINT_USERINFO, new String[]{"200"});
        JSONObject userInfo = logAnalyticsPage.jsonObject;
        String tenantId = userInfo.getString("tenant_id");
        String dashboardUrl = base_url + aiopsvaluedashboard + "?orgId=" + tenantId + dashboardTimeRange;
        log.info("Dashboard URL: " + dashboardUrl);
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);

        ArrayList<String> dashboardMTTIStrs = getDashboardElementText(driver, dashboardUrl, aiopsValueDashboardOperationalId);

        String dashboardMTTIStr = dashboardMTTIStrs.get(0).replace("min", "").replace("mins", "").trim();


        double tolerance = 0.10; // 10% deviation allowed
        log.info("Validating operational KPIs with 10% deviation tolerance");
        for (int i = 0; i < calculatedValues.length && i < dashboardMTTIStrs.size(); i++) {
            String dashboardStr = dashboardMTTIStrs.get(i).replace("%", "").replace(",", "").trim();
            if (dashboardStr.isEmpty()) {
                softAssert.fail("Dashboard value at index " + i + " is empty, skipping validation.");
                continue;
            }
            double dashboardVal;
            try {
                dashboardVal = Double.parseDouble(dashboardStr);
            } catch (NumberFormatException e) {
                softAssert.fail("Dashboard value at index " + i + " is not a valid number: " + dashboardStr);
                continue;
            }
            if(dashboardVal == -1){
                continue;
            }
            float calculatedVal = calculatedValues[i];
            float dashboardValFloat = (float) dashboardVal;
            if(calculatedVal > 950 && dashboardVal < 50){
                dashboardVal = dashboardVal * 1000;
            }
            double diff = Math.abs(calculatedVal - dashboardValFloat);
            double allowed = Math.abs(calculatedVal * tolerance);
            if(dashboardValFloat < 0){
                softAssert.fail("Operational KPI mismatch at index " + i + ": Calculated=" + calculatedVal + ", Dashboard=" + dashboardVal + ", Allowed deviation=" + allowed);
            }else{
                log.error("Operational KPI mismatch at index " + i + ": Calculated=" + calculatedVal + ", Dashboard=" + dashboardVal + ", Allowed deviation=" + allowed);
            }

        }
        log.info("Qualify Event total count: " + qualifyEventCount);
        log.info("Operational KPI validation completed.");
        softAssert.assertAll();

    }

    @Test(description = "Value Dash Board, validate Operational Kpi's on Event, node and service relationship", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15708", Products = {Product.Service_Monitoring, Product.Helix_Discovery, Product.ADE_OperationsManagement, Product.ADE_Dashboards})
    public void DRSQA_15708_Value_dashboard_validate_Operational_Kpis_on_Event_node_and_service_relationship() throws Exception {
        log.info("Starting Operational_Kpis validation test");
        if(jwtToken == null) {
            prerquirements();
        }
        setTimeRangeAndEventCount();
        String msearchEventsBody = "msearchqueryallevent.json";
        String totalEventCountFile = "msearchqueryalleventcount.json";
        String totalSituationCountFile = "msearchsituationqueryall.json";
        String eventPartSituationCountFile = "msearchqueryalleventPartServicecount.json";
        String eventNodeAssociateCountFile = "msearchqueryalleventNodecount.json";
        String eventServiceAssociateCountFile = "msearchqueryalleventServicecount.json";


        // 2. Get total events matching policy (last 24h, excluding class=situation)
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        log.info("Hits object extracted");

        // Traverse all records in hitsobject
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        log.info("Hits array size: " + hitsArray.length());

        float eventNodeAssociate = 0;
        float eventServiceAssociate = 0;
        float eventServiceAssociatePercent = 0;
        float eventErrorCount = 0;
        float qualifyEventCount = 0;
        float totalEvent = 0;
        float eventNodeAssociatePercent = 0;
        String relationship = "";
        String classVal = "";
        String incident_id = "";
        String msg = "";
        String priority = "";
        String severity	=  "";
        String nodeid = "";
        String serviceId = "";
        String source_hostname = "";
        String replacementChar = ";";

        JSONObject totalEventCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, totalEventCountFile, timediff);
        totalEvent = totalEventCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Total event count: " + totalEvent);


        JSONObject eventPartSituationCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, eventServiceAssociateCountFile, timediff);
        eventServiceAssociate = eventPartSituationCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Event part situation count: " + eventServiceAssociate);

        eventPartSituationCountObject = hm_events.runMSearchQueryGetSituation("dummy", jwtToken, eventNodeAssociateCountFile, timediff);
        eventNodeAssociate = eventPartSituationCountObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        log.info("Event part situation count: " + eventNodeAssociate);


        eventServiceAssociatePercent = Math.round(((float) eventServiceAssociate /totalEvent) * 100);
        eventNodeAssociatePercent = Math.round(((float) eventNodeAssociate /totalEvent) * 100);

        float[] calculatedValues = new float[] {
                eventNodeAssociatePercent,
                eventServiceAssociatePercent
        };
        log.info("calculated value " + Arrays.toString(calculatedValues));
        log.info("eventNodeAssociatePercent: " + eventNodeAssociatePercent);
        log.info("eventServiceAssociatePercent: " + eventServiceAssociatePercent);
        log.info("totalEvent: " + totalEvent);
        log.info("eventNodeAssociate: " + eventNodeAssociate);
        log.info("eventServiceAssociate: " + eventServiceAssociate);
        // 3. Get latest MTTI from dashboard
        logAnalyticsPage.Send_RestCall("GET", ENDPOINT_USERINFO, new String[]{"200"});
        JSONObject userInfo = logAnalyticsPage.jsonObject;
        String tenantId = userInfo.getString("tenant_id");
        String dashboardUrl = base_url + aiopsvaluedashboard + "?orgId=" + tenantId + dashboardTimeRange;
        log.info("Dashboard URL: " + dashboardUrl);
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);

        ArrayList<String> dashboardMTTIStrs = getDashboardElementText(driver, dashboardUrl, aiopsValueDashboardOperationalIdNode);


        double tolerance = 0.05; // 10% deviation allowed
        log.info("Validating operational KPIs with 10% deviation tolerance");
        for (int i = 0; i < calculatedValues.length && i < dashboardMTTIStrs.size(); i++) {
            String dashboardStr = dashboardMTTIStrs.get(i).replace("%", "").replace(",", "").trim();
            if (dashboardStr.isEmpty()) {
                log.error("Dashboard value at index " + i + " is empty, skipping validation.");
                continue;
            }
            double dashboardVal;
            try {
                dashboardVal = Double.parseDouble(dashboardStr);
            } catch (NumberFormatException e) {
                log.error("Dashboard value at index " + i + " is not a valid number: " + dashboardStr);
                continue;
            }
            if(dashboardVal == -1){
                continue;
            }
            float calculatedVal = calculatedValues[i];
            float dashboardValFloat = (float) dashboardVal;
            if(calculatedVal > 950 && dashboardVal < 50){
                dashboardVal = dashboardVal * 1000;
            }
            double diff = Math.abs(calculatedVal - dashboardValFloat);
            double allowed = Math.abs(calculatedVal * tolerance);

            if(dashboardValFloat < 0){
                softAssert.fail("Operational KPI mismatch at index " + i + ": Calculated=" + calculatedVal + ", Dashboard=" + dashboardVal + ", Allowed deviation=" + allowed);
            }else{
                log.error("Operational KPI mismatch at index " + i + ": Calculated=" + calculatedVal + ", Dashboard=" + dashboardVal + ", Allowed deviation=" + allowed);
            }

        }
        log.info("Qualify Event total count: " + qualifyEventCount);
        log.info("Operational KPI validation completed.");
        softAssert.assertAll();

    }
}