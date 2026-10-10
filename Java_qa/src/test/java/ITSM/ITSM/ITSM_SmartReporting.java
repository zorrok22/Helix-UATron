package ITSM.ITSM;

import java.io.File;
import java.util.Map;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import framework.BasePage;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartReporting.SmartReportingFeatureMethods;
import util.CommonMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.UserUpdater;
import org.openqa.selenium.WebDriver;

public class ITSM_SmartReporting extends BasePage {

    static SolqaLogger log = new SolqaLogger(ITSM_SmartReporting.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> itsmTestData, itsmUserData;
    ITSMFeatureMethods itsmFeatureMethods;
    SmartReportingFeatureMethods smartReportingFeatureMethods;
    WebDriver itsmDriver;
    UserUpdater itsmUsers;
    int counter;

    @BeforeTest(alwaysRun = true)
    public void setupUsers() {
        counter = Integer.parseInt(configProp.get("runCounter"));
        if (counter > 0) {
            // Load the JSON file once
            String filename = "ITSMUsers";
            itsmUsers = new UserUpdater();
            String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITSM + File.separator + filename + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITSM + File.separator + filename + ".json";

            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);

            // Load the JSON file
            itsmUsers.loadJsonFile(jsonFilePath);

            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            itsmUsers.updateFromExcelRow(excelFilePath, counter);

            log.info("Updated ITSMUsers.json for run counter: " + counter + " using row: " + counter);

            // Verify the update by reading the JSON back

        } else {
            //No change required
            log.info("runCounter is 0, no user updates required");
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
    }

    @Test(description = "Incident Cross Launch from Reporting Console to ITSM Midtier", enabled=false, groups = {"MAT", "ITSM", "SmartReporting"})
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-11809",Products = {Product.Helix_ITSM,Product.Helix_SmartReporting})
    public void DRSQA_11809_Incident_Cross_Launch_from_Reporting_Console_to_ITSM_Midtier(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA_11809 Incident Cross Launch from Reporting Console to ITSM Midtier ******************");

        log.info("******Step 1: Login to ITSM **********");
        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_11809_Driver", getDriver());
        context.setAttribute("DRSQA_11809_Product", Product.Helix_ITSM);
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11809_TestData");
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();

        log.info("******Step 2: Navigate to smart reporting ******");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.SMART_REPORTING, SolQAConstants.SMART_REPORTING_CONSOLE);
        smartReportingFeatureMethods = new SmartReportingFeatureMethods();
        smartReportingFeatureMethods.verifyNavigationToSmartReporting();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11809_reportingTabOpened", SolQAConstants.SMART_REPORTING);

        log.info("***** Step 3: Navigating to Incident management report ******");
        smartReportingFeatureMethods.searchFolder(itsmTestData.get("FolderName"));
        smartReportingFeatureMethods.clickOnFolder(itsmTestData.get("FolderName"));
        smartReportingFeatureMethods.clickOnSubFolder(itsmTestData.get("SubFolderName"));

        log.info("******Step 4:Double click on Incident details **********");
        smartReportingFeatureMethods.clickContentDashBoardLinkForVerifyingDetails(itsmTestData.get("DashboardLink"));
        smartReportingFeatureMethods.enterDateAndclickOnGo();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11809_searchResults", SolQAConstants.SMART_REPORTING);

        log.info("******Step 5: Open and verify incident open**********");
        smartReportingFeatureMethods.verifyCaseDetailsInNewTab();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11809_incidentOpened", SolQAConstants.SMART_REPORTING);

        log.info("**************** Start DRSQA_11809 Incident Cross Launch from Reporting Console to ITSM Midtier ******************");
    }

    @Test(description = "ITSM user should be synced to Smart Reporting server instantly",enabled=false, groups = {"MAT", "ITSM", "SmartReporting"})
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-11805", Products = {Product.Helix_ITSM, Product.Helix_SmartReporting})
    public void DRSQA_11805_ITSM_User_Sync_With_Smart_Reporting(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA_11805_ITSM_User_Sync_With_Smart_Reporting ******************");

        log.info("******Step 1: Login to ITSM **********");
        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_11805_Driver", getDriver());
        context.setAttribute("DRSQA_11805_Product", Product.Helix_ITSM);

        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11805_data");
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.captureScreenshot("DRSQA_11805_MT_Login_with_Admin", SolQAConstants.appITSM);

        log.info("******Step 2: Navigate to People form to create new user if user not available " + itsmTestData.get("LoginID"));
        itsmFeatureMethods.captureScreenshot("DRSQA_11805_userSearch", SolQAConstants.appITSM);
        String username = itsmFeatureMethods.createUserInCTMPeopleForm(itsmTestData, configProp.get("ARServerAlias"), true);

        log.info("******Step 3: Login to smart reporting with created user.******");
        // Use existing WebDriver from ThreadLocal
        setup();
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_11805_Driver", getDriver());
        context.setAttribute("DRSQA_11805_Product", Product.Helix_SmartReporting);
        login(username, itsmTestData.get("Password"), SolQAConstants.appITSM);

        log.info("******Step 4: Navigate to smart reporting ******");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.SMART_REPORTING, SolQAConstants.SMART_REPORTING_CONSOLE);
        smartReportingFeatureMethods = new SmartReportingFeatureMethods();
        smartReportingFeatureMethods.verifyNavigationToSmartReporting();
        smartReportingFeatureMethods.verifySmartReportingHomePage();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11805_reportingTabOpened", SolQAConstants.SMART_REPORTING);

        log.info("**************** End DRSQA_11805_ITSM_User_Sync_With_Smart_Reporting ******************");
    }

    @Test(description = "Incident Cross Launch from Reporting Console to MT", groups = {"Regression_P2"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11434", Products = {Product.Helix_ITSM, Product.Helix_SmartReporting})
    public void DRSQA_11434_Incident_Cross_Launch_From_Reporting_Console_To_MT(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA_11809 Incident Cross Launch from Reporting Console to ITSM Midtier ******************");

        log.info("******Step 1: Login to ITSM **********");
        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_11809_Driver", getDriver());
        context.setAttribute("DRSQA_11809_Product", Product.Helix_ITSM);
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11434_TestData");
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();

        log.info("******Step 2: Navigate to smart reporting ******");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.SMART_REPORTING, SolQAConstants.SMART_REPORTING_CONSOLE);
        smartReportingFeatureMethods = new SmartReportingFeatureMethods();
        smartReportingFeatureMethods.verifyNavigationToSmartReporting();
        smartReportingFeatureMethods.waitForSmartReportingSpinnersToDisappear();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11809_reportingTabOpened", SolQAConstants.SMART_REPORTING);

        log.info("***** Step 3: Navigating to Incident management report ******");
        smartReportingFeatureMethods.waitForSmartReportingSpinnersToDisappear();
        smartReportingFeatureMethods.searchFolder(itsmTestData.get("FolderName"));
        smartReportingFeatureMethods.clickOnFolder(itsmTestData.get("FolderName"));
        smartReportingFeatureMethods.clickOnSubFolder(itsmTestData.get("SubFolderName"));

        log.info("******Step 4:Double click on Incident details **********");
        smartReportingFeatureMethods.clickContentDashBoardLinkForVerifyingDetails(itsmTestData.get("DashboardLink"));
        smartReportingFeatureMethods.enterDateAndclickOnGo();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11809_searchResults", SolQAConstants.SMART_REPORTING);

        log.info("******Step 5: Open and verify incident open**********");
        smartReportingFeatureMethods.verifyCaseDetailsInNewTab();
        smartReportingFeatureMethods.captureScreenshot("DRSQA_11809_incidentOpened", SolQAConstants.SMART_REPORTING);

        log.info("**************** Start DRSQA_11809 Incident Cross Launch from Reporting Console to ITSM Midtier ******************");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (smartReportingFeatureMethods != null) {
            smartReportingFeatureMethods.closeBrowser();
        }
    }
}