package ITSM.DWP;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.*;

public class DWPTests extends Launcher {

    static SolqaLogger log = new SolqaLogger(DWPTests.class.getName());
    DWPFeatureMethods dwpFeatureMethod;
    ReadJson readJson = new ReadJson();
    Map<Object, String> dwpUserData, dwpTestData;
    WebDriver dwpDriver;
    UserUpdater dwpUsers;
    int counter;

    @BeforeTest(alwaysRun = true)
    public void setupUsers() {
        counter = Integer.parseInt(configProp.get("runCounter"));
        if (counter > 0) {
            // Load the JSON file once
            String filename = "DWPUsers";
            dwpUsers = new UserUpdater();
            String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appDWP + File.separator + filename + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appDWP + File.separator + filename + ".json";

            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);

            // Load the JSON file
            dwpUsers.loadJsonFile(jsonFilePath);

            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            dwpUsers.updateFromExcelRow(excelFilePath, counter);
            log.info("Updated DWPUsers.json for run counter: " + counter + " using row: " + counter);
            // Verify the update by reading the JSON back
        } else {
            //No change required
            log.info("runCounter is 0, no user updates required");
        }
    }

    //previous owner: shudeshp
    @Test(description = "Login to application with or without permissions", groups = {"MAT", "DWP"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA_11799", Products = {Product.Helix_DWP})
    public void DRSQA_11799_VerifyDWPAdminPageForCorrectAndIncorrectPassword(ITestContext context) throws IOException {

        log.info("------------DRSQA-11799: Login to application with or without permissions-------------");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);

        log.info("---- Login with correct username and wrong password in DWP----------");
        context.setAttribute("DRSQA_11799_Driver", dwpDriver);
        context.setAttribute("DRSQA_11805_Product", Product.Helix_DWP);

        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        login(dwpUserData.get("UserName"), "WrongPassword", SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
        dwpFeatureMethod.captureScreenshot("DRSQA_11799_LoginWithCorrectUnameAndWrongPassword", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        log.info("---- Login with wrong username and correct password in DWP----------");

        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        // Navigate back to login page for next test
        context.setAttribute("DRSQA_11799_Driver", dwpDriver);
        context.setAttribute("DRSQA_11805_Product", Product.Helix_DWP);
        login("Test", dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
        dwpFeatureMethod.captureScreenshot("DRSQA_11799_LoginWithWrongUnameAndCorrectPassword", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("------------- Login to DWP with correct username & password and verify activity page-------");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        // Navigate back to login page for next test
        context.setAttribute("DRSQA_11799_Driver", dwpDriver);
        context.setAttribute("DRSQA_11805_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_11799_VerifyActivityPageAfterSuccessfullyLogin", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------- Login to DWP with end user and verify admin page is Not loaded without admin permission");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        context.setAttribute("DRSQA_11799_Driver", getDriver());
        context.setAttribute("DRSQA_11805_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_11799_VerifyDWPHomepageOpenedForEndUserWhenLoginToDWPAdmin", SolQAConstants.appDWP);
    }

    //previous owner: shudeshp
    @Test(description = "DWP - Search for Catalog published services in DWP --> Catalog tab", enabled = false, groups = {"MAT", "DWP"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11790", Products = {Product.Helix_DWP})
    public void DRSQA_11790_Search_service_in_DWP(ITestContext context) throws IOException {
        log.info("*********** DRSQA_11790_Search_service *******************");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11790_Driver", dwpDriver);
        context.setAttribute("DRSQA_11790_Product", Product.Helix_DWP);

        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        log.info("Open Catalog Page");
        dwpFeatureMethod.openCatalogPage();
        Assert.assertTrue(dwpFeatureMethod.verifyCatalogSection(), "DWP catalog service are not displayed");
        dwpFeatureMethod.captureScreenshot("DRSQA_11790_catalogSection_view", SolQAConstants.appDWP);
        log.info("*********** DRSQA_11790_Search_service end *******************");
    }

    @Test(description = "DWP - Search BWF and ITSM knowledge article", groups = {"ZDT","MAT", "DWP"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11791", Products = {Product.Helix_DWP})
    public void DRSQA_11791_SearchBWFAndITSMKnowledgeArticle(ITestContext context) throws IOException {

        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11791_Driver", dwpDriver);
        context.setAttribute("DRSQA_11791_Product", Product.Helix_DWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);

        log.info("Configuring HKM Configuration in DWP.");
        dwpFeatureMethod.navigateToAdminPage();
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11815_Data");
        String configuration = dwpTestData.get("configuration");

        log.info("Step 4: Login to DWP with " + CommonMethods.readConfig("adminUserName"));
        dwpFeatureMethod.verifyLoginToDWPAdmin();

        log.info("Step 5: login to DWP Admin console and set the comaround provider configurations and enable the provider ");
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(configuration);
        dwpFeatureMethod.enableDisableComAroundPluggableProvider(false);
        dwpFeatureMethod.ReloadProviders();
        dwpFeatureMethod.closeBrowser();

        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11791_Driver", dwpDriver);
        context.setAttribute("DRSQA_11791_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "BMCOpsArticleSearch");

        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.captureScreenshot("DRSQA_11791_DRSQA_11792_Login successfully to DWP", SolQAConstants.appDWP);

        log.info("Search an article in the search bar article = " + dwpTestData.get("BWF_ArticleName"));
        dwpFeatureMethod.searchItemInGlobalSearch(dwpTestData.get("BWF_ArticleName"), SolQAConstants.ARTICLES);
        dwpFeatureMethod.captureScreenshot("DRSQA_11792 BWF article search verification", SolQAConstants.appDWP);

        log.info("Search an article in the search bar article = " + dwpTestData.get("ITSM_ArticleName"));
        dwpFeatureMethod.searchItemInGlobalSearch(dwpTestData.get("ITSM_ArticleName"), "Articles");
        dwpFeatureMethod.captureScreenshot("DRSQA_11791 ITSM article search verification", SolQAConstants.appDWP);

        log.info("Close the browser");
    }


    @Test(description = "HA- DWP search BWF and RKM knowledge Article", groups = {"Regression_P1", "DWP", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11703", Products = {Product.Helix_DWP})
    public void DRSQA_11703_SearchBWFAndRKMKnowledgeArticle(ITestContext context) throws IOException {

        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11703_WebDriver", dwpDriver);
        context.setAttribute("DRSQA_11703_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "nishaUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11703_ArticleSearch");

        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.captureScreenshot("DRSQA_11703_Login successfully to DWP", SolQAConstants.appDWP);

        log.info("Search an article in the search bar article = " + dwpTestData.get("BWF_ArticleName"));
        dwpFeatureMethod.searchItemInGlobalSearch(dwpTestData.get("BWF_ArticleName"), SolQAConstants.ARTICLES);
        dwpFeatureMethod.clickOnKnowledgeArticleUsefulLink();
        dwpFeatureMethod.captureScreenshot("DRSQA_11703 BWF article search verification", SolQAConstants.appDWP);

        log.info("Search an article in the search bar article = " + dwpTestData.get("RKM_ArticleName"));
        dwpFeatureMethod.searchItemInGlobalSearch(dwpTestData.get("RKM_ArticleName"), SolQAConstants.ARTICLES);
        dwpFeatureMethod.clickOnKnowledgeArticleUsefulLink();
        dwpFeatureMethod.captureScreenshot("DRSQA_11703 RKM article search verification", SolQAConstants.appDWP);
        log.info("Close the browser");
    }

    @Test(description = "Login to application with or without permissions", groups = {"Regression_P1"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11504", Products = {Product.Helix_DWP})
    public void DRSQA_11504_VerifyDWPAdminPageForCorrectAndIncorrectPassword(ITestContext context) {

        log.info("------------DRSQA-11504: Login to application with or without permissions-------------");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        log.info("---- Login with correct username and wrong password in DWP----------");
        context.setAttribute("DRSQA_11504_Driver", dwpDriver);
        context.setAttribute("DRSQA_11504_Product", Product.Helix_DWP);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bhillUser");

        login(dwpUserData.get("UserName"), "WrongPassword", SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
        dwpFeatureMethod.closeBrowser();

        log.info("---- Login with wrong username and correct password in DWP----------");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        context.setAttribute("DRSQA_11504_Driver", dwpDriver);
        context.setAttribute("DRSQA_11504_Product", Product.Helix_DWP);
        login("Test", dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
        dwpFeatureMethod.closeBrowser();

        log.info("------------- Login to DWP with correct username & password and verify activity page-------");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        context.setAttribute("DRSQA_11504_Driver", dwpDriver);
        context.setAttribute("DRSQA_11504_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.closeBrowser();

        log.info("-------- Login to DWP with end user and verify admin page is Not loaded without admin permission");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        context.setAttribute("DRSQA_11504_Driver", dwpDriver);
        context.setAttribute("DRSQA_11504_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyDWPHomePageOpened();
    }

    @Test(description = "Request 360 - Allow admins to configure what field to show.", groups = {"Regression_P1", "DWP","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11686", Products = {Product.Helix_DWP})
    public void DRSQA11686_allowsAdminsToConfigureWhatFieldToShow(ITestContext context) throws Exception {

      //  -------------------- Admin: configure additional fields for Request 360 --------------------
      dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      context.setAttribute("DRSQA_11686_Driver", dwpDriver);
      context.setAttribute("DRSQA_11686_Product", Product.Helix_DWP);
      login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);
      dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA11686_SB_Create_MultiITSM");
      dwpFeatureMethod = new DWPFeatureMethods();
      dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
      dwpFeatureMethod.navigateToAdminPage();
      dwpFeatureMethod.verifyLoginToDWPAdmin();
      dwpFeatureMethod.clickOnHamburgerMenu();
      dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
      dwpFeatureMethod.clickOnSubMenu(SolQAConstants.Fulfillment_Details);
      dwpFeatureMethod.selectAdditionalFieldsFulfillmentDetails(SolQAConstants.CHANGE_REQUEST_TYPE, dwpTestData.get("ChangeAdditionalFields"));
      dwpFeatureMethod.selectAdditionalFieldsFulfillmentDetails(SolQAConstants.INCIDENT_REQUEST_TYPE, dwpTestData.get("IncidentAdditionalFields"));
      dwpFeatureMethod.selectAdditionalFieldsFulfillmentDetails(SolQAConstants.WORKORDER_REQUEST_TYPE, dwpTestData.get("WorkOrderAdditionalFields"));
      dwpFeatureMethod.closeBrowser();

      dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      context.setAttribute("DRSQA_11686_Driver", dwpDriver);
      context.setAttribute("DRSQA_11686_Product", Product.Helix_DWP);
      dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA11686_SB_Create_MultiITSM");
      dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
      login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
      dwpFeatureMethod = new DWPFeatureMethods();
      dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
      dwpFeatureMethod.verifyDWPHomePageOpened();
      dwpFeatureMethod.fillSubmitDWPService(dwpTestData, dwpTestData.get("ServiceRequestName"));
      log.info(dwpTestData.get("ServiceRequestName") + " service request submitted successfully in DWP.");
      String DRSQA_11611_RequestID= dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceRequestName"));
      log.info("---- DRSQA_11611_ Generated Request ID -" + DRSQA_11611_RequestID);
      Assert.assertTrue(DRSQA_11611_RequestID != null && !DRSQA_11611_RequestID.trim().isEmpty(), "Submitted request id is empty.");
      log.info("Request ID: " + DRSQA_11611_RequestID);
      dwpFeatureMethod.openSrmRequestByRequestID(DRSQA_11611_RequestID);
      dwpFeatureMethod.verifySrdTitle(dwpTestData.get("ServiceRequestName"));
    
      log.info("Get the fulfillment IDs --> ChangeID , Incident ID , WorkOrder ID of above create request in Card view");
      dwpDriver.navigate().refresh();
      dwpFeatureMethod.navigateToMyActivityPage();
      Map<String, String> mapOfFullFillmentIDsFromCardView = dwpFeatureMethod.getFulFillmentIdsFromActiveEventsInCardView(DRSQA_11611_RequestID);
      log.info("Get the fulfillment IDs --> ChangeID , Incident ID , WorkOrder ID of above create request in List view");
      Map<String, String> mapOfFullFillmentIDsFromListView = dwpFeatureMethod.getFulFillmentIdsFromActiveEventsInListView(DRSQA_11611_RequestID);
      Assert.assertEquals(mapOfFullFillmentIDsFromCardView, mapOfFullFillmentIDsFromListView, "Fulfillment IDs in Card View and List view are not equal");
      String changeId = mapOfFullFillmentIDsFromCardView.get("ChangeID");
      String incidentId = mapOfFullFillmentIDsFromCardView.get("IncidentID");
      String workOrderId = mapOfFullFillmentIDsFromCardView.get("WorkOrderID");
      log.info("Generate Fulfillment ID --> ChangeID is " + changeId + " Incident ID is " + incidentId + " and WorkOrder ID is " + workOrderId);
      dwpFeatureMethod.clickCardViewButton();
      log.info("Open Submitted Request having Request ID " + DRSQA_11611_RequestID);
      dwpFeatureMethod.openSrmRequestByRequestID(DRSQA_11611_RequestID);
      log.info("Add Comment in Change ID Fulfillment " + dwpTestData.get("DWPChangeRequestComment"));
      commonWaits.waitForSometime(commonWaits.moreDelay);
      dwpFeatureMethod.addCommentInSubmittedRequest(dwpTestData.get("DWPChangeRequestComment"));
      dwpFeatureMethod.verifyComment(dwpTestData.get("DWPChangeRequestComment"));
      log.info("Add Comment in Incident ID request Fulfillment " + dwpTestData.get("DWPIncidentComment"));
      commonWaits.waitForSometime(commonWaits.moreDelay);
      dwpFeatureMethod.addCommentInSubmittedRequest(dwpTestData.get("DWPIncidentComment"));
      dwpFeatureMethod.verifyComment(dwpTestData.get("DWPIncidentComment"));
      log.info("Add Comment in WorkOrder ID Request Fulfillment " + dwpTestData.get("DWPWorkOrderComment"));
      commonWaits.waitForSometime(commonWaits.moreDelay);
      dwpFeatureMethod.scrollToPageTop();
      dwpFeatureMethod.addCommentInSubmittedRequest(dwpTestData.get("DWPWorkOrderComment"));
      dwpFeatureMethod.verifyComment(dwpTestData.get("DWPWorkOrderComment"));
      log.info("Logout from DWP application");
      dwpFeatureMethod.closeBrowser();

    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (dwpFeatureMethod != null) {
                dwpFeatureMethod.closeBrowser();
            }
            // Only cleanup if driver is still valid
            if (getDriver() != null) {
                WebDriverManager.cleanupDriver();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }

}