package ITSM.BWF;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class BWFTests extends Launcher {
    public static SolqaLogger log = new SolqaLogger(BWFTests.class.getName());
    ReadJson readJson = new ReadJson();
    BWFFeatureMethods bwfFeatureMethods;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    Map<Object, String> bwfUserData, bwfTestData;
    WebDriver bwfdriver = null;
    String ServiceImportFileLocation = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appBWF + File.separator + "ServiceImportValidation.zip";
    UserUpdater bwfUsers;
    int counter;
    String appName = "BWF";

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        bwfdriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfdriver);
    }

    @BeforeTest
    public void setupUsers() {
        counter = Integer.parseInt(configProp.get("runCounter"));
        if (counter > 0) {
            // Load the JSON file once
            bwfUsers = new UserUpdater();
            String filename = "BWFUsers";
            String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + appName + File.separator + filename + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + appName + File.separator + filename + ".json";

            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);

            // Load the JSON file
            bwfUsers.loadJsonFile(jsonFilePath);

            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            bwfUsers.updateFromExcelRow(excelFilePath, counter);

            log.info("Updated BWFUsers.json for run counter: " + counter + " using row: " + counter);

            // Verify the update by reading the JSON back
            try {
                ReadJson verifyJson = new ReadJson();
                Map<Object, String> verifyData = verifyJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
                log.info("Verification - Available keys after update: " + verifyData.keySet());
                log.info("Verification - Username value: " + verifyData.get("UserName"));
                log.info("Verification - Password value: " + verifyData.get("Password"));
            } catch (Exception e) {
                log.error("Error verifying JSON update: " + e.getMessage());
            }
        } else {
            //No change required
            log.info("runCounter is 0, no user updates required");
        }
    }

    @Test(description = "BWF - Create Knowledge article manually from BWFA console", groups = {"ZDT","MAT", "BWF"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11780", Products = {Product.Helix_BWF})
    public void DRSQA_11780_CreateKnowledge_BWF(ITestContext context) throws IOException {

        context.setAttribute("DRSQA_11780_Driver", bwfdriver);
        context.setAttribute("DRSQA_11780_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "BMCOpsKnowledgeData");

        log.info("Login to BWF with : " + bwfUserData.get("UserName") + " and verify its logged in successfully");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.captureScreenshot("DRSQA_11780_Login successfully to BWF", SolQAConstants.appBWF);

        log.info("Navigate to create Knowledge use template: " + bwfTestData.get("ArticleTemplate"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11780 Navigate Create Knowledge", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToPage("Create", "Knowledge");
        String knowledgeArticleID = bwfFeatureMethods.createKnowledgeArticle(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_11780_Create Knowledge article", SolQAConstants.appBWF);

        Assert.assertFalse(knowledgeArticleID.isEmpty(), "KA is not created");
        log.info("--------  Knowledge Article ID: " + knowledgeArticleID);
        bwfFeatureMethods.captureScreenshot("DRSQA_11780_Knowledge created successfully", SolQAConstants.appBWF);

        log.info("verify the article status is " + SolQAConstants.IN_PROGRESS);
        Assert.assertEquals(bwfFeatureMethods.getArticleStatus(), SolQAConstants.IN_PROGRESS, "Failed to display In progress Status");

        log.info("Change the article status to " + SolQAConstants.DRAFT);
        bwfFeatureMethods.changeStatusTo(SolQAConstants.DRAFT);
        bwfFeatureMethods.captureScreenshot("DRSQA_11780_Knowledge status changed", SolQAConstants.appBWF);

        log.info("verify the article status is " + SolQAConstants.DRAFT);
        Assert.assertEquals(bwfFeatureMethods.getArticleStatus(), SolQAConstants.DRAFT, "Failed to display Draft Status");

        log.info("Change the article status to " + SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.changeStatusTo(SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.captureScreenshot("DRSQA_11780_Knowledge status changed", SolQAConstants.appBWF);

        log.info("verify the article status is " + SolQAConstants.PUBLISHED);
        bwfFeatureMethods.captureScreenshot("DRSQA_11780_Knowledge status changed", SolQAConstants.appBWF);
        bwfFeatureMethods.refreshBrowser();
        Assert.assertEquals(bwfFeatureMethods.getArticleStatus(), SolQAConstants.PUBLISHED, "Failed to display Published Status");
        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "BWF - Create Case manually from BWFA console, Search and Verify the case details", groups = {"MAT", "BWF"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11783", Products = {Product.Helix_BWF})
    public void DRSQA_11783_CreateCaseManuallyFromBWFAConsoleSearchAndVerifyTheCaseDetails(ITestContext context) throws IOException {

        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_11783_Driver", bwfdriver);
        context.setAttribute("DRSQA_11783_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "BMCOpsCaseData");
        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Login successfully to BWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Create a case");
        String caseID = bwfFeatureMethods.createCase(bwfTestData);
        bwfFeatureMethods.assignCaseToMe();
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Case created successfully", SolQAConstants.appBWF);
        log.info("Case ID: " + caseID);

        log.info("Verify the case status is " + SolQAConstants.ASSIGNED);
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.ASSIGNED, "Failed to display Assigned Status");

        log.info("Change the case status to " + SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Case status changed", SolQAConstants.appBWF);

        log.info("Verify the case status is " + SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.refreshBrowser();
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.IN_PROGRESS, "Failed to display In progress Status");

        log.info("Change the case status to " + SolQAConstants.RESOLVED);
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.RESOLVED);
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Case status changed", SolQAConstants.appBWF);

        log.info("Verify the case status is " + SolQAConstants.RESOLVED);
        bwfFeatureMethods.refreshBrowser();
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.RESOLVED, "Failed to display Resolved Status");

        log.info("Search for the case ID: " + caseID);
        bwfFeatureMethods.navigateToPage("Workspace", "Case");
        bwfFeatureMethods.searchAndOpenCase(caseID);
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Case searched successfully", SolQAConstants.appBWF);

        log.info("Verify case details");
        bwfFeatureMethods.verifyCaseDetails(bwfTestData);

        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "SmartReporting - Cross launching of BWF and SmartReporting", enabled = false, groups = {"MAT", "BWF", "SmartReporting"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-15401", Products = {Product.Helix_BWF, Product.Helix_SmartReporting})
    public void Crosslaunch_Reverselaunch_OFReporting(ITestContext context) throws IOException {
        log.info("*****************  Crosslaunch And Reverse cross launch of reporting ********************* ");
        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_15401_Driver", bwfdriver);
        context.setAttribute("DRSQA_15401_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        readJson = new ReadJson();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "reverseCrossLaunchData");

        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("Login to BWF", SolQAConstants.appBWF);

        log.info("Cross launch to Smart Reporting");
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.crossLaunchReports();
        bwfFeatureMethods.captureScreenshot("Crosslaunch to Smart Reporting", SolQAConstants.SMART_REPORTING);

        log.info("Verify the cross launch to Smart Reporting");
        bwfFeatureMethods.openReport(bwfTestData.get("reportName"));
        bwfFeatureMethods.captureScreenshot("Smart Reporting opened", SolQAConstants.SMART_REPORTING);

        log.info("Open the first request");
        bwfFeatureMethods.openFirstRequest(bwfTestData.get("companyName"));
        bwfFeatureMethods.captureScreenshot("Opened first request", SolQAConstants.SMART_REPORTING);

        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "Cross launch between Helix Dashboard & BWF case", groups = {"MAT", "BWF", "HelixDashboard"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11812", Products = {Product.Helix_BWF, Product.Dashboards})
    public void DRSQA_11812_Crosslaunch_Between_HelixDashboardAndBWFCase(ITestContext context) throws IOException {

        log.info("***************** START:- DRSQA_11812_Crosslaunch_Between_HelixDashboardAndBWFCase ********************* ");
        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_11812_Driver", bwfdriver);
        context.setAttribute("DRSQA_11812_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "HelixDashboardCaseData");

        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("DRSQA-11812_Login to BWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        log.info("Navigate to create case");
        String caseID = bwfFeatureMethods.createCase(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA-11812_Case created successfully", SolQAConstants.appBWF);
        log.info("Case ID: " + caseID);

        log.info("Cross launching application from BWF , application name is " + SolQAConstants.BMC_HELIX_DASHBOARD);
        bwfFeatureMethods.crossLaunchApplication(SolQAConstants.BMC_HELIX_DASHBOARD);
        bwfFeatureMethods.captureScreenshot("DRSQA-11812_Crosslaunch to BMC Helix Dashboard", SolQAConstants.BMC_HELIX_DASHBOARD);

        log.info("Open the dashboard");
        bwfFeatureMethods.searchAndOpenDashboard("Business Workflows Dashboard");
        bwfFeatureMethods.captureScreenshot("DRSQA-11812_Business Workflows Dashboard opened", SolQAConstants.BMC_HELIX_DASHBOARD);

        log.info("Set the filters");
        bwfFeatureMethods.applyFiltersOnDashboard(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA-11812_Filters applied", SolQAConstants.BMC_HELIX_DASHBOARD);

        log.info("Open case from dashboard");
        bwfFeatureMethods.openCaseFromDashboard(caseID);
        bwfFeatureMethods.captureScreenshot("DRSQA-11812_Crosslaunch to BWF case", SolQAConstants.appBWF);

        log.info("Verify case details");
        bwfFeatureMethods.verifyCaseDetails(bwfTestData);

        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }
    
    @Test(description = "BWF - Manage Catalog cross launch", groups = {"MAT", "BWF"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11793", Products = {Product.Helix_BWF})
    public void DRSQA_11793_BWF_ManageCatalogCrossLaunch(ITestContext context) throws IOException {
        log.info("***************** START:- DRSQA_11793_BWF_ManageCatalogCrossLaunch ********************* ");

        log.info("---------------------Step1:-  Log into BWFA as a Case Business Analyst with the Case Catalog Administrator functional role .------Expected:- BWF login should be successful---------------");
        context.setAttribute("DRSQA_11793_Driver", bwfdriver);
        context.setAttribute("DRSQA_11793_Product", Product.Helix_BWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Login successfully to BWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("---------------------Step2:-  Click on Manage Catalog link--------Expected:- User should be able to cross launch DWPC service page-------------");
        bwfFeatureMethods.crosslaunchManageCatalog();
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Crosslaunch to Catalog", SolQAConstants.appBWF);

        log.info("---------------------Step3:-  Import any one service --------Expected:- Service import opearation should be successful-------------");
        bwfFeatureMethods.importService(ServiceImportFileLocation);
        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "BWF - Manage Catalog cross launch", groups = {"BWF", "Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11510", Products = {Product.Helix_BWF})
    public void DRSQA_11510_BWF_ManageCatalogCrossLaunch(ITestContext context) throws IOException {
        log.info("***************** START:- DRSQA_11510_BWF_ManageCatalogCrossLaunch ********************* ");

        log.info("---------------------Step1:-  Log into BWFA as a Case Business Analyst with the Case Catalog Administrator functional role .------Expected:- BWF login should be successful---------------");
        context.setAttribute("DRSQA_11510_Driver", bwfdriver);
        context.setAttribute("DRSQA_11510_Product", Product.Helix_BWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("Login successfully to BWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("---------------------Step2:-  Click on Manage Catalog link--------Expected:- User should be able to cross launch DWPC service page-------------");
        bwfFeatureMethods.crosslaunchManageCatalog();
        bwfFeatureMethods.captureScreenshot("Cross launch to Catalog", SolQAConstants.appBWF);

        log.info("---------------------Step3:-  Import any one service --------Expected:- Service import operation should be successful-------------");
        bwfFeatureMethods.importService(ServiceImportFileLocation);
        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "[SmartReporting] - Cross launching of BWF and SmartReporting", groups = {"SmartReporting", "OneTime"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11490", Products = {Product.Helix_BWF, Product.Helix_SmartReporting})
    public void DRSQA_11490_CrossLaunchingOfBWFandSmartReporting(ITestContext context) throws IOException {
        log.info("*****************  Cross launch And Reverse cross launch of reporting ********************* ");
        // Use existing WebDriver from ThreadLocal
        context.setAttribute("DRSQA_11490_Driver", bwfdriver);
        context.setAttribute("DRSQA_11490_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        readJson = new ReadJson();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "reverseCrossLaunchData");

        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("Login to BWF", SolQAConstants.appBWF);

        log.info("Cross launch to Smart Reporting");
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.crossLaunchReports();
        bwfFeatureMethods.captureScreenshot("Cross launch to Smart Reporting", SolQAConstants.SMART_REPORTING);

        log.info("Verify the cross launch to Smart Reporting");
        bwfFeatureMethods.openReport(bwfTestData.get("reportName"));
        bwfFeatureMethods.captureScreenshot("Smart Reporting opened", SolQAConstants.SMART_REPORTING);

        log.info("Open the first request");
        bwfFeatureMethods.openFirstRequest(bwfTestData.get("companyName"));
        bwfFeatureMethods.captureScreenshot("Opened first request", SolQAConstants.SMART_REPORTING);

        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "Verify Global Search Functionality in BWF", groups = {"MAT", "BWF"})
    @SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-16264", Products = {Product.Helix_ITSM, Product.Helix_BWF})
    public void DRSQA_16264_VerifyGlobalSearchFunctionalityInBWF(ITestContext context) throws Exception {
        log.info("***************** START:- DRSQA_16264_VerifyGlobalSearchFunctionalityInBWF ********************* ");

        log.info("Precondition:- Go to BWF Settings -> Line Business -> Manage Line of Business -> Helix GPT -> Chat with Helix GPT = True , open the setting  enable the setting");
        try {
            context.setAttribute("DRSQA_16264_Driver", bwfdriver);
            context.setAttribute("DRSQA_16264_Product", Product.Helix_BWF);
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
            log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
            login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
            bwfFeatureMethods.verifyLoginToBWF();
            bwfFeatureMethods.captureScreenshot("DRSQA_16264_Login successfully to BWF", SolQAConstants.appBWF);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.EnableHelixGPTSearchInBWFURL);
            bwfFeatureMethods.clickOnForwardArrowInManageLOB();
            bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.APP_CONFIG_TAB_NAME);
            bwfFeatureMethods.clickOnHelixGPTConfigNameAndSetValue(SolQAConstants.CONFIG_NAME, SolQAConstants.True);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Step1:- Log into BWF with the case agent user");
        bwfdriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfdriver);
        context.setAttribute("DRSQA_16264_Driver", bwfdriver);
        context.setAttribute("DRSQA_16264_Product", Product.Helix_BWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_16264Data");
        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("DRSQA_16264_Login successfully to BWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Create a case");
        String caseID = bwfFeatureMethods.createCase(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_16264_Case created successfully", SolQAConstants.appBWF);
        log.info("Case ID: " + caseID);

        log.info("Step2:- Verify the new Global Search icon -------");
        bwfFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();

        log.info("Step3:- Click on the search icon -> Verify filter options -------");
        bwfFeatureMethods.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
        bwfFeatureMethods.defaultSearchToggleButton(SolQAConstants.SEARCH);
        bwfFeatureMethods.verifyFilterOptionsOnSearchDropdownMenus();

        log.info("Step4:- Search the requested data  -> Case -= with case id\n" +
                "People = Elizabeth \n" +
                "Knowledge article = FMLA\n -------");

        log.info("Verifing the article is displayed in the search results: " + bwfTestData.get("kaName"));
        bwfFeatureMethods.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_KNOWLEDGE_ARTICLES, bwfTestData.get("kaName"));
        bwfFeatureMethods.verifyCountOfItemsReturnedAndOpenFullView(SolQAConstants.FILTER_KNOWLEDGE_ARTICLES, bwfTestData.get("kaName"), 1, 3);
        log.info("Verifing the person is displayed in the search results: " + bwfTestData.get("personName"));
        bwfFeatureMethods.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_PEOPLE, bwfTestData.get("personName"));
        bwfFeatureMethods.verifyCountOfItemsReturnedAndOpenFullView(SolQAConstants.FILTER_PEOPLE, bwfTestData.get("personName"), 1, 4);
        log.info("Verifing the case is displayed in the search results");
        bwfFeatureMethods.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_CASES, caseID);
        bwfFeatureMethods.verifyCountOfItemsReturnedAndOpenFullView(SolQAConstants.FILTER_CASES, caseID, 1, 2);
        log.info("Close the browser");
        bwfFeatureMethods.closeBrowser();

        log.info("------------END :DRSQA_16264 [25.2] Verify Helix GPT and new Global Search Functionality in Smart IT, BWF----------");
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (bwfFeatureMethods != null) {
            bwfFeatureMethods.closeBrowser();
        }
    }

}