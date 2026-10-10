package ITOM.Aiops;

import ITOM.Intelligent_Automation.IntelligentAutomationTest;
import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ITSM.SmartIT.SmartITCalender;
import pages.ITOM.Intelligent_Automation.HistoryPage;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Intelligent_Automation.PoliciesPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import util.*;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import org.testng.asserts.SoftAssert;

public class AIOpsCRMtest extends Launcher {
    public static SolqaLogger log = new SolqaLogger(AIOpsCRMtest.class.getName());
    WebDriver driver;
    String app = "ITOM";
    String serviceName;
    String smartITCalenderURL = "/helix/index.html#/com.bmc.dsm.itsm-applications/view/com.bmc.dsm.itsm-applications:Calendar";
    private static final String CRAInsightEndPoint = "/change-risk-analysis/api/v1.1/insights/";
    private static final String getChangeListEndPoint = "/service-management/api/v1.0/helixgpt/assistant/agents/CRA/invoke";
    Map<Object, String> aiopsTestData;
    String jwtToken = null;
    Aiops aiopsApiCore = null;
    AiopsSituation aiopsSituation = null;
    AiopsDeepRCA aiopsDeepRCA = null;
    LogAnalyticsPage logAnalyticsPage = null;
    CommonWaits commonwait = null;
    String eventClass = "NAGIOS_EV1";
    SoftAssert softAssert = null;
    CommonMethods commonmethod = null;
    SmartITCalender smartITCalender = null;
    String smartITURL = configProp.get("SmartIT_URL");


    public void doPreconditions() {
        
        aiopsApiCore = new Aiops();
        aiopsSituation = new AiopsSituation();
        commonmethod = new CommonMethods();
        softAssert = new SoftAssert();
        aiopsDeepRCA = new AiopsDeepRCA();
        logAnalyticsPage = new LogAnalyticsPage();
        commonwait = new CommonWaits();
        
        log.info("Setting up the test...");
        jwtToken = commonmethod.refreshJwtToken(null);
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception{
        doPreconditions();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() throws Exception{
        log.info("Closing the browser");
        if(driver != null){
            driver.quit();
        }
    }


    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "CRM Feature - ITSM to AIOPS", groups = {"bap"})
    @SolqaDetails(authorName = "Haris",  JiraID = "DRSQA-11730", Products = {Product.intelligent_automation,Product.AiOPS, Product.Helix_SmartIT})
    public void DRSQA_11730_Verify_CRM_Feature_ITSM_to_AIOps(ITestContext context ) throws Exception {
        String changeId = null;
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("Change Risk Advisor")){
            log.info("CRA feature is not enabled; skipping test as passed.");
            return;
        } 
        String ChangeListFileName = "getchangerisklist.json";
        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        log.info("Generated JWT token and set access credentials");

        String body = aiopsApiCore.readJSon(ChangeListFileName);

        log.info("Fetching change records from endpoint: " + getChangeListEndPoint);
        logAnalyticsPage.Send_RestCall("POST", getChangeListEndPoint, new String[]{"200",body});
        JSONArray  changerecord = logAnalyticsPage.jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("changes");
        log.info("Retrieved " + changerecord.length() + " change records");
        int changerecordLength = changerecord.length();
        if (changerecordLength < 1){
            Assert.fail("No change Record fetch");
        }

        // Valid change statuses to process
        List<String> validChangeStatuses = new ArrayList<>();
        validChangeStatuses.add("Closed");
        validChangeStatuses.add("Implementation In Progress");
        validChangeStatuses.add("Completed");
        
        for (int i =0;i<changerecordLength;i = i+1){
            String selectedChangeId = changerecord.getJSONObject(i).getString("change_id");
            String changeStatus = changerecord.getJSONObject(i).getString("change_status");
            String changeShortDesc = changerecord.getJSONObject(i).getString("change_short_desc");
            log.info("Processing change " + (i+1) + "/5 - Change ID: " + changeId + ", Status: " + changeStatus);

            // Validate change status
            if (!validChangeStatuses.contains(changeStatus)) {
                log.warn("Skipping change " + changeId + " - Status '" + changeStatus + "' is not in valid statuses list");
                continue;
            }
            String end_point = CRAInsightEndPoint + changeId;
            log.info("Fetching insights for change ID: " + changeId + " from endpoint: " + end_point);
            logAnalyticsPage.Send_RestCall("GET", end_point, new String[]{"200"});
            JSONObject changeSitRecord = logAnalyticsPage.jsonObject;
            if (logAnalyticsPage.response_code > 299){
                log.warn("Insights could not be retrieved for change request: " + changeId);
                continue;
            }
            log.info("Insights retrieved successfully for change ID: " + changeId);
            changeId = selectedChangeId;
            break;
            
        }
        //changeId = "CRQ000000004103";
        
        if(changeId == null){
            Assert.fail("No change Record fetch");
        }

        driver = this.launchWeb(app, app);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),app);
        setDriver(driver);
        smartITCalender = new SmartITCalender();
        smartITCalender.waitForHelixITSMLinkToBeVisible();
        
        try
        {
            log.info("Loading SmartIT Calender");
            boolean isSmartITCalenderLoaded = smartITCalender.loadSmartITCalender(smartITURL + smartITCalenderURL);
            if(!isSmartITCalenderLoaded){
                Assert.fail("SmartIT Calender not loaded");
            }
            log.info("Searching change in SmartIT Calender");
            smartITCalender.searchChange(changeId);
            log.info("Getting service name from SmartIT Calender");
            boolean isCRMwindowsPopup = smartITCalender.popupCRMwindows();
            if(!isCRMwindowsPopup){
                Assert.fail("CRM windows not popup");
            }
            if(smartITCalender.getServiceName() == null){
                softAssert.fail("Service not found in SmartIT Calender");
            }
            log.info("Getting current service health status from SmartIT Calender");
            if(smartITCalender.getCurrentServiceHealthStatus() == null){
                softAssert.fail("Current service health status not found in SmartIT Calender");
            }
            log.info("Getting current open situations from SmartIT Calender");
            if(smartITCalender.getCurrentOpenSituations() == null){
                softAssert.fail("Current open situations not found in SmartIT Calender");
            }
            log.info("Getting past situations from SmartIT Calender");
            if(smartITCalender.getPastSituations() == null){
                softAssert.fail("Past situations not found in SmartIT Calender");
            }
            log.info("Getting completed changes from SmartIT Calender");
            if(smartITCalender.getCompletedChanges() == null){
                softAssert.fail("Completed changes not found in SmartIT Calender");
            }
            log.info("Getting work orders from SmartIT Calender");
            if(smartITCalender.getWorkOrders() == null){
                softAssert.fail("Work orders not found in SmartIT Calender");
            }
            log.info("Getting incidents from SmartIT Calender");
            if(smartITCalender.getIncidents() == null){
                softAssert.fail("Incidents not found in SmartIT Calender");
            }

        }
        catch (Exception e){
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        softAssert.assertAll();
        log.info("DRSQA_12374_Verify_Create_Automation_On_Intelligent_Automation_From_AIOps test completed successfully");
    }

}