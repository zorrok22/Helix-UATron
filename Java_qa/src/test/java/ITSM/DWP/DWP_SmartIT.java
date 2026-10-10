package ITSM.DWP;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.ITSM.ITSMDashboard;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.UserUpdater;

public class DWP_SmartIT extends Launcher {

    static SolqaLogger log = new SolqaLogger(DWP_SmartIT.class.getName());
    DWPFeatureMethods dwpFeatureMethod;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    ITSMDashboard itsmDashboard;
    ITSMFeatureMethods itsmFeatureMethods;
    BWFFeatureMethods bwfFeatureMethods;
    DWPCFeatureMethods dwpcFeatureMethods;
    ReadJson readJson = new ReadJson();
    Map<Object, String> dwpUserData, dwpTestData, smartITUserData, itsmUserData, smartITTestData, bwfUserData, bwfTestData, dwpcTestData, itsmTestData, dwpcUserData;
    WebDriver dwpDriver, smartITDriver, itsmDriver, bwfDriver, dwpcDriver, bwfDriver1, bwfDriver2, ITSMdriver;
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

    @Test(description = "ITSM to BWF integration", groups = {"Regression_P1"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12276", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_12276_ITSMToBWFIntegration(ITestContext context) throws Exception {

        log.info("----------START:- DRSQA_12276_ITSMToBWFIntegration---------------");

        log.info("-------------------- Precondition 1: BWF settings --------------------");
        log.info("Precondition 1: Login to BWF and enable ITSM_CASE_INTEGRATION + CREATE_SERVICE_REQUEST");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_12276_Driver", bwfDriver);
        context.setAttribute("DRSQA_12276_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.enterValueInTextField(SolQAConstants.CommonConfig);
        bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.CommonConfig);
        bwfFeatureMethods.clickCommonConfigurationsAndSetValue(SolQAConstants.ITSM_CASE_INTEGRATION, SolQAConstants.TRUE);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.enterValueInTextField(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
        bwfFeatureMethods.clickApplicationConfigNameAndSetValue(SolQAConstants.CREATE_SERVICE_REQUEST, SolQAConstants.TRUE);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Precondition 2: ITSM Centralized Configuration --------------------");
        log.info("Precondition 2: Login to ITSM and set CCS parameter enableITSMBWFIntegration=true under com.bmc.arsys.smartit");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12276_Driver", itsmDriver);
        context.setAttribute("DRSQA_12276_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[]{SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.addCCSParameters(SolQAConstants.ENABLE_ITSM_BWF_INTEGRATION);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_ITSM_BWF_INTEGRATION, SolQAConstants.TRUE);
        itsmFeatureMethods.refreshBrowser();
        itsmFeatureMethods.LoadForm(SolQAConstants.CFG_RULES_FORM, SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.setServiceRequestOnSubmitToValue(SolQAConstants.PETRAMCO_COMPANY, SolQAConstants.IN_DIGITAL_WORKPLACE, "Create Request on Submit");
        itsmFeatureMethods.setSelfService_PV(true, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        log.info("-------------------- Step 1: Create incident in SmartIT --------------------");
        log.info("Step 1: Login to SmartIT and create an Incident (Requester=bhill)");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        Map<Object, String> smartITIncidentData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "CreateIncident_DRSQA_11286");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_12276_TestData");

        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_12276_Driver", smartITDriver);
        context.setAttribute("DRSQA_12276_Product", Product.SmartIT);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.navigateToCreateNewIncident();
        smartITFeatureMethodsPage.FillIncidentTemplate(smartITIncidentData);
        smartITFeatureMethodsPage.saveIncident();
        String incidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
        log.pass("************ created incident is: " + incidentID);

        // Best-effort: add a public note (uses shared activity feed locators; may vary by ticket type)
        smartITFeatureMethodsPage.addComment_InActivityFeed(smartITTestData.get("SmartITNote"));

        log.info("-------------------- Step 2: Verify generated DWP request and comments --------------------");
        log.info("Step 2: Login to DWP as requester and verify generated request + comments");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_12276_DWP_Driver", dwpDriver);
        context.setAttribute("DRSQA_12276_DWP_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();

        String incidentTitle = smartITIncidentData.get("Incident Title");
        String dwpRequestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(incidentTitle);
        Assert.assertFalse(dwpRequestId.isEmpty(), "DWP request ID should not be empty");
        dwpFeatureMethod.openSrmRequestByRequestID(dwpRequestId);
        dwpFeatureMethod.verifyOpenedRequestTitle(incidentTitle);
        dwpFeatureMethod.verifyComment(smartITTestData.get("SmartITNote"));
        log.info("----------END:- DRSQA_12276_ITSMToBWFIntegration---------------");
    }

    //previous owner: shudeshp
    @Test(description = "DWP - Bi-directional communication between DWP request & Remedy ITSM ticket (via new request)", groups = {"ZDT","MAT", "DWP", "ITSM", "SmartIT"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11782", Products = {Product.Helix_DWP, Product.Helix_ITSM})
    public void DRSQA_11782_Communication_DWP_ITSM(ITestContext context) throws Exception {

        log.info("--------Precondition:- Log into DWP with admin user and enable the fulfillment----------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11782_Driver", getDriver());
        context.setAttribute("DRSQA_11782_Product", Product.Helix_DWP);

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11782_Data");
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        String SERVICE_NAME = dwpTestData.get("serviceName");
        String attachmentName = dwpTestData.get("attachmentName");
        String configuration = dwpTestData.get("configuration");
        String fulfillmentDetails = dwpTestData.get("fulfillmentDetails");

        log.info("Configuring fulfillment details in DWP.");
        dwpFeatureMethod.navigateToAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(configuration);
        dwpFeatureMethod.clickOnSubMenu(fulfillmentDetails);
        dwpFeatureMethod.configureFulfillmentDetails();
        dwpDriver.close();

        log.info("-----------Step1:- Login to DWP Application with bmcopsenduser user-----------");
        log.info("*********** DRSQA_11782_Communication_DWP_ITSM *******************");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11782_Driver", getDriver());
        context.setAttribute("DRSQA_11782_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        log.info("--------Step2:- Go to Catalog tab and request service having ITSM - WO fulfillment --------");
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, SERVICE_NAME);
        dwpFeatureMethod.captureScreenshot("DRSQA_11782_requestCreated", SolQAConstants.appDWP);
        String firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        log.info("------------------Step3:- Click on a request having WorkOrder fulfillment from Active events section & add a comment with attachment to the request.---------");
        log.info(" ************ SRD created ID: " + firstSRD);
        Assert.assertFalse(firstSRD.isEmpty(), "SR is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(firstSRD);
        String comment = "Adding attachment";
        File file = CommonMethods.createTextFile(attachmentName);

        log.info("Open submitted request and add comment with attachment");
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment, file);
        dwpFeatureMethod.verifyAttachments("Adding attachment", file);

        String fulfillmentID = dwpFeatureMethod.getFulfillmentID();

        log.info("---------Step4:- Login to SmartIT as an IT agent that can view WorkOrder & click on the workorder record linked to DWP request from Step#2.---------");
        log.info("Login to SmartIT and verify Workorder");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        context.setAttribute("DRSQA_11782_WebDriver", getDriver());
        context.setAttribute("DRSQA_11782_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");
        log.info("SmartIT user is: " + smartITUserData.get("UserName"));
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        log.info("Navigate to Ticket Console");
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11782_navigatedToConsole", SolQAConstants.appSmartIT);
        Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");

        log.info("Applying Filters and opening created workorder");
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SMARTIT_TICKET_TYPE_WORK_ORDER, fulfillmentID);
        smartITFeatureMethodsPage.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(attachmentName), "Attachment is not present on the ticket");

        log.info("---------Step5:- As the IT Agent, add a public comment to the workorder Activity section.---------");
        log.info("Add public note in SmartIT");
        String publicComment = "Adding public note from smartIT";
        smartITFeatureMethodsPage.addComment_InActivityFeed(publicComment);
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11782_addedPublicComment", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("Switch to DWP window and verifying the comment added from SMARTIT");
        dwpFeatureMethod.switchToDWP(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();

        log.info("Goto DWP and verify comments added from SMARTIT side");
        dwpFeatureMethod.verifyComment(publicComment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11782_smartITCommentInDWP", SolQAConstants.appDWP);
        dwpDriver.close();
        log.pass("*********** DRSQA_11782_Communication_DWP_ITSM end *******************");
    }

    //previous owner: shudeshp
    @Test(description = "BMCOPS DWPC Approval", enabled=false, groups = {"MAT", "DWP", "ITSM", "SmartIT"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11810", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11810_BMCOpsSBApproval(ITestContext context) throws Exception {

        log.info("********* START: DRSQA_11810_BMCOpsSBApproval **************");
        log.info("---------- Enable fulfillment --------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11810_Data");
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
        String configuration = dwpTestData.get("configuration");
        String fulfillmentDetails = dwpTestData.get("fulfillmentDetails");
        String REJECTED_STATUS = dwpTestData.get("REJECTED_STATUS");
        log.info("Configuring fulfillment details in DWP.");
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(configuration);
        dwpFeatureMethod.clickOnSubMenu(fulfillmentDetails);
        dwpFeatureMethod.configureFulfillmentDetails();
        dwpFeatureMethod.closeBrowser();

        log.info("*********** DRSQA_11810_Communication_DWP_ITSM *******************");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        log.info("Open Catalog Page");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        } else {
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
        }

        log.info("request a service: " + SERVICE_NAME);
        dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
        log.info("Request is submitted");
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestCreated", SolQAConstants.appDWP);
        String firstRequest = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        dwpFeatureMethod.closeBrowser();

        log.info("---------- Login to DWP and approve the request --------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        readJson = new ReadJson();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcOpsManagerUser");
        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(firstRequest);
        dwpFeatureMethod.verifyServiceRequestStatus("Waiting Approval");
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestListedOnMyActivity", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("---------- Login to DWP and get created Workorder ID --------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        readJson = new ReadJson();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        log.info("Open Activity Page");
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestListedOnMyActivity", SolQAConstants.appDWP);
        dwpFeatureMethod.openSrmRequestByRequestID(firstRequest);
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_RequestID_requestOpened", SolQAConstants.appDWP);
        String fulfillmentID = dwpFeatureMethod.getFulfillmentID();
        log.info("Work Order ID: " + fulfillmentID);
        dwpFeatureMethod.closeBrowser();

        log.info("---------- Login to SmartIT and verify Workorder is created --------------");
        log.info("Login to SmartIT and verify Workorder");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11810_Driver", smartITDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");
        log.info("SmartIT user is: " + smartITUserData.get("UserName"));
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        log.info("Navigate to Ticket Console");
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11810_navigatedToConsole", SolQAConstants.appSmartIT);
        log.info("Applying Filters and opening created workorder");
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SMARTIT_TICKET_TYPE_WORK_ORDER, fulfillmentID);
        Assert.assertEquals(smartITFeatureMethodsPage.getWOCustomerName(), dwpTestData.get("CustomerName"), "Customer name is not matching in SmartIT");
        smartITFeatureMethodsPage.closeBrowser();

        log.info("******** Login to DWP and submit request ************");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        } else {
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
        }
        log.info("request a service: " + SERVICE_NAME);
        dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
        log.info("Request is submitted");
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_RequestSubmitted", SolQAConstants.appDWP);
        log.info(dwpTestData.get("ServiceRequestName") + " service request submitted successfully in DWP.");
        dwpFeatureMethod.openActivityPage();
        String secondRequest = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        if (firstRequest.equals(secondRequest)) {
            dwpFeatureMethod.openActivityPage();
            secondRequest = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        }
        dwpFeatureMethod.openSrmRequestByRequestID(secondRequest);
        Assert.assertFalse(secondRequest.isEmpty(), "SR is not created");
        log.pass("-----------------DRSQA-11810 SR created ID: " + secondRequest);
        dwpFeatureMethod.closeBrowser();

        log.info("---------- Login to DWP and and reject the service --------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcOpsManagerUser");
        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestListedOnMyActivity", SolQAConstants.appDWP);
        dwpFeatureMethod.openSrmRequestByRequestID(secondRequest);
        dwpFeatureMethod.clickRejectRequestInEventDetails(dwpTestData.get("RejectReason"), true);
        dwpFeatureMethod.verifyServiceRequestStatus(REJECTED_STATUS);
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_RequestID", SolQAConstants.appDWP);
        log.pass("-------------DRSQA_11810_RequestID_requestApproved---------------");
        dwpFeatureMethod.closeBrowser();

        log.info("---------- Login to DWP and verify fulfillment is not processed--------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11810_Driver", dwpDriver);
        context.setAttribute("DRSQA_11810_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestListedOnMyActivity", SolQAConstants.appDWP);
        dwpFeatureMethod.openSubmittedRequestInPastEvents(secondRequest);
        dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestOpened", SolQAConstants.appDWP);
        dwpFeatureMethod.verifyStatusDetailPage(REJECTED_STATUS);
        dwpFeatureMethod.closeBrowser();
    }

    @Test(description = "Validate Approval for DWP requests on ITSM screen for DWP request", enabled = false, groups = {"Regression_P2", "SmartIT", "DWP","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-14636", Products = {Product.Helix_DWP, Product.SmartIT})
    public void DRSQA_14636_SBApprovalServiceRequest(ITestContext context) throws Exception {
        log.info("********* START: DRSQA_14636_SBApprovalServiceRequest **************");
        try {
            log.info("---------Precondition: Disable the PV ---------");
            log.info("logging in into ITSM");

            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_14636_Driver", itsmDriver);
            context.setAttribute("DRSQA_14636_Product", Product.Helix_ITSM);
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            log.info("Logged in user: " + itsmUserData.get("UserName"));
            itsmDashboard = new ITSMDashboard();
            itsmDashboard.verifyLogin();
            itsmDashboard.setSelfService_PV(false, configProp.get("ARServerAlias"));
            log.info("PV is disabled successfully in ITSM");

            log.info("---------Step 1: Login to DWP Application with bhill user-------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_14636_Driver", dwpDriver);
            context.setAttribute("DRSQA_14636_Product", Product.Helix_DWP);

            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
            dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_14636_Data");
            log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();

            log.info("-------- Step 2: Submit a Approval Service request with details (service name:- SB_Approval service) --------");
            String SERVICE_NAME = dwpTestData.get("serviceName");
            log.info("Open Catalog Page");
            dwpFeatureMethod.openCatalogPage();

            if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
                dwpFeatureMethod.openAllItemsPage();
                dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            } else {
                dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
                dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
            }

            log.info("Request a service: " + SERVICE_NAME);
            dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
           dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("ApprovalSummaryLabel"), dwpTestData.get("ApprovalSummaryValue"));
            dwpFeatureMethod.clickOnSubmitRequest();
            dwpFeatureMethod.captureScreenshot("DRSQA_14636_SBApprovalRequestCreated", SolQAConstants.appDWP);
            log.info("Request is submitted");
           String submittedRequestID = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);

            log.info("Submitted Request ID: " + submittedRequestID);
            Assert.assertFalse(submittedRequestID.isEmpty(), "Service Request is not created");

            log.info("-------- Step 3: Open submitted request and verify the status (Status should be 'Waiting Approval') --------");
            log.info("Open submitted request and verify status");
            dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestID);
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
            dwpFeatureMethod.captureScreenshot("DRSQA_14636_SBApprovalStatusVerified", SolQAConstants.appDWP);

            log.info("-------- Step 4: Login to SmartIT Application with pbunyon user --------");
            smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITDriver);
            context.setAttribute("DRSQA_14636_Driver", smartITDriver);
            context.setAttribute("DRSQA_14636_Product", Product.SmartIT);
            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
            log.info("Logged in to SmartIT with " + smartITUserData.get("UserName"));
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
            log.info("-------- Step 5: Open DWP request in SmartIT and verify the yellow approver banner --------");
            log.info("Navigate to Global Search and open the DWP request in SmartIT");
            smartITFeatureMethodsPage.globalSearchAndOpenTicket(submittedRequestID);
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_14636_DWPRequestOpenedInSmartIT", SolQAConstants.appSmartIT);

            log.info("Verifying yellow approver banner is present");
            Assert.assertTrue(smartITFeatureMethodsPage.verifyYellowApprovalBannerPresent(), "Yellow approver banner is not present in SmartIT for DWP request");
            log.info("Yellow approver banner is verified successfully in SmartIT for DWP request");
            Assert.assertTrue(smartITFeatureMethodsPage.verifyYellowApprovalBannerApprovalText(dwpTestData.get("ApprovalBannerText")), "Approval banner text is not matching");

            log.info("-------- Step 6: In Fulfillment section click on fulfilment ID hyperlink --------");
            smartITFeatureMethodsPage.clickOnFulfillmentIDHyperLink();
            Assert.assertTrue(smartITFeatureMethodsPage.verifyApprovalIsAvailable(dwpTestData.get("ApprovalName")), "Approval is not available for the DWP request");

            log.info("-------- Step 7: Login to approver with DWP application and approve the request with fcat user --------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            dwpFeatureMethod = new DWPFeatureMethods();
            context.setAttribute("DRSQA_14636_Driver", dwpDriver);
            context.setAttribute("DRSQA_14636_Product", Product.SmartIT);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "fcatUser");
            log.info("Login to DWP with approver " + dwpUserData.get("UserName"));
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestID);
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
            dwpFeatureMethod.clickApproveRequestInEventDetails();
            dwpFeatureMethod.refreshDWPPage();
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
            dwpFeatureMethod.captureScreenshot("DRSQA_14636_RequestApproved", SolQAConstants.appDWP);

            log.info("---------Login to DWP Application with bhill user and get the Fulfillment ID-------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_14636_Driver", dwpDriver);
            context.setAttribute("DRSQA_14636_Product", Product.Helix_DWP);

            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
            dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_14636_Data");
            log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestID);
            String fulfillmentID = dwpFeatureMethod.getFulfillmentID();
            log.info("Fulfillment ID: " + fulfillmentID);

            log.info("-------- Step 8: Go to the fulfillment section in SmartIT. First, verify the work order ID and fulfillment ID. Then, click the hyperlink and verify the details in the Approval List panel --------");
            smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            smartITFeatureMethodsPage.refreshSmartITPage();
            log.info("Verifying work order ID and fulfillment ID in SmartIT");
            Assert.assertEquals(smartITFeatureMethodsPage.getWorkOrderIDFromFulfillmentSection(), fulfillmentID, "Fulfillment ID is not matching in SmartIT");
            Assert.assertTrue(smartITFeatureMethodsPage.getWorkOrderIDStatusFromFulfillmentSection().contains(SolQAConstants.ASSIGNED), "Work Order ID Status is not matching in SmartIT");
            Assert.assertTrue(smartITFeatureMethodsPage.getApprovalRecordStatusFromFulfillmentSection().contains(SolQAConstants.APPROVED), "Approval Record Status is not matching in SmartIT");

            log.info("-------- Step 9: Click on Workorder ID hyperlink and verify the request --------");
            smartITFeatureMethodsPage.clickOnWorkOrderIDHyperLink();
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_14636_WorkOrderDetailsOpenedInSmartIT", SolQAConstants.appSmartIT);
            Assert.assertTrue(smartITFeatureMethodsPage.verifyWorkorderIDFromSmartITViewPageWhenPVDisabled(fulfillmentID), "Workorder ID is not matching in SmartIT view page");

        }
         finally {
            log.info("logging in into ITSM for enabling the PV back");
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            log.info("Logged in user: " + itsmUserData.get("UserName"));
            itsmDashboard = new ITSMDashboard();
            itsmDashboard.verifyLogin();
            itsmDashboard.setSelfService_PV(true, configProp.get("ARServerAlias"));
            log.info("PV is enabled successfully in ITSM");
        }
        log.pass("********* END: DRSQA_14636_SBApprovalServiceRequest - All steps completed successfully **************");
    }

    @Test(description = "Verify Approval of service request with approval submitted via Smart Recorder", enabled = false, groups = {"Regression_P2", "SmartIT", "DWP", "DWPC","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-14637", Products = {Product.Helix_DWP, Product.SmartIT, Product.Helix_DWPC})
    public void DRSQA_14637_VerifyApprovalOfServiceRequestWithApprovalSubmittedViaSmartRecorder(ITestContext context) throws Exception {
        log.info("********* START: DRSQA_14637_VerifyApprovalOfServiceRequestWithApprovalSubmittedViaSmartRecorder **************");
        try {
            log.info("---------Precondition: Disable the PV ---------");
            log.info("logging in into ITSM");

            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_14637_Driver", itsmDriver);
            context.setAttribute("DRSQA_14637_Product", Product.Helix_ITSM);
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            log.info("Logged in user: " + itsmUserData.get("UserName"));
            context.setAttribute("DRSQA_14637_Driver", getDriver());
            context.setAttribute("DRSQA_14637_Product", Product.Helix_ITSM);
            itsmDashboard = new ITSMDashboard();
            itsmDashboard.verifyLogin();
            itsmDashboard.setSelfService_PV(false, configProp.get("ARServerAlias"));
            log.info("PV is disabled successfully in ITSM");

            log.info("---------Step 1: Login to SmartIT with pbunyon user ---------");
            smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITDriver);
            context.setAttribute("DRSQA_14637_Driver", smartITDriver);
            context.setAttribute("DRSQA_14637_Product", Product.SmartIT);

            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
            smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_14637_Data");
            log.info("Login to SmartIT with " + smartITUserData.get("UserName"));
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
            log.info("----------Step 2: Click on Smart Recorder and type customer name and search for service -----------");
            log.info("Navigate to Smart Recorder");
            smartITFeatureMethodsPage.navigateToSmartRecorder();
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_14637_navigatedToSmartRecorder", SolQAConstants.appSmartIT);
            String customerName = smartITTestData.get("CustomerName");
            smartITFeatureMethodsPage.selectCustomerOrAssetInSmartRecorder(customerName);

            log.info("------------- Step 3 and Step 4: In Smart Recorder remove approval and enter ! mark !approval --------");
            smartITFeatureMethodsPage.enterStringInSmartRecorder(smartITTestData.get("ServiceName"));
            smartITFeatureMethodsPage.selectRecommendedDWPTemplate(smartITTestData);

            log.info("---------- Step 5: Click on Create Service request");
            smartITFeatureMethodsPage.clickCreateServiceRequestButton();
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_14637_createServiceRequestClicked", SolQAConstants.appSmartIT);

            log.info("---------- Step 6: Fill the details and click on Submit request");
            smartITFeatureMethodsPage.Fill_ApprovalPersonSRD_smartRecorder(smartITTestData.get("ApprovalPersonFullName"));
            smartITFeatureMethodsPage.clickSubmitRequestButton();
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_14637_serviceRequestSubmitted", SolQAConstants.appSmartIT);
            String submittedRequestID = smartITFeatureMethodsPage.getFulfillmentID();
            log.info("Submitted Request ID: " + submittedRequestID);
            Assert.assertFalse(submittedRequestID.isEmpty(), "Service Request is not created");

            log.info("---------- Step 7: Click on refresh icon next to print option ------------");
            smartITFeatureMethodsPage.clickOnRefreshButton();

            log.info("---------- Step 8: Scroll down and Open the Request ID from fulfillment record and click on show approvers");
            log.info("Open Activity Page and find the submitted request");
            smartITFeatureMethodsPage.clickOnFulfillmentIDHyperLink();
            log.info("Verifying yellow approver banner is present");
            Assert.assertTrue(smartITFeatureMethodsPage.verifyYellowApprovalBannerPresent(), "Yellow approver banner is not present in SmartIT for DWP request");
            log.info("Yellow approver banner is verified successfully in SmartIT for DWP request");
            Assert.assertTrue(smartITFeatureMethodsPage.verifyYellowApprovalBannerApprovalText(smartITTestData.get("ApprovalBannerText")), "Approval banner text is not matching");
            smartITFeatureMethodsPage.clickOnBannerShowApprovers();
            Assert.assertTrue(smartITFeatureMethodsPage.verifyApprovalIsAvailable(smartITTestData.get("ApprovalPersonFullName")), "Approval is not available for the DWP request");

            log.info("---------- Step 9: Login to DWP as customer --------------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_14637_Driver", dwpDriver);
            context.setAttribute("DRSQA_14637_Product", Product.Helix_DWP);

            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "pbunyon");
            log.info("Login to DWP with customer " + dwpUserData.get("UserName"));
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();


            log.info("---------- Step 10: Login to DWP as Approver and open the service request from activity-------");
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestID);
            dwpFeatureMethod.captureScreenshot("DRSQA_14637_approvalProfileOpened", SolQAConstants.appDWP);

            log.info("---------- Step 11: As a approver approve or reject the service request-------");
            log.info("Approving the service request");
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
            dwpFeatureMethod.clickApproveRequestInEventDetails();
            dwpFeatureMethod.refreshDWPPage();
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.INITIATED);
            dwpFeatureMethod.captureScreenshot("DRSQA_14637_requestApproved", SolQAConstants.appDWP);
            dwpFeatureMethod.closeBrowser();

            log.info("---------- Step 12: After request is approved in fulfillment record WO is displayed. Open the WO from fulfillment------");
            smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            smartITFeatureMethodsPage.refreshSmartITPage();
            String WorkOrderID = smartITFeatureMethodsPage.getWorkOrderID();
            smartITFeatureMethodsPage.clickOnWorkOrderIDHyperLink();
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_14636_WorkOrderDetailsOpenedInSmartIT", SolQAConstants.appSmartIT);
            Assert.assertTrue(smartITFeatureMethodsPage.verifyWorkorderIDFromSmartITViewPageWhenPVDisabled(WorkOrderID), "Workorder ID is not matching in SmartIT view page");

        } finally {
            log.info("logging in into ITSM for enabling the PV back");
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            log.info("Logged in user: " + itsmUserData.get("UserName"));
            itsmDashboard = new ITSMDashboard();
            itsmDashboard.verifyLogin();
            itsmDashboard.setSelfService_PV(true, configProp.get("ARServerAlias"));
            log.info("PV is enabled successfully in ITSM");
        }
        log.pass("********* END: DRSQA_14637_VerifyApprovalOfServiceRequestWithApprovalSubmittedViaSmartRecorder - All steps completed successfully **************");
    }

    @Test(description = "[DWP][PWA] Show DWPC service request on fulfillment incident page", groups = {"Regression_P1"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11629", Products = {Product.Helix_DWP, Product.SmartIT})
    public void DRSQA_11629_Show_DWPC_Service_Request_OnFulfillment_IncidentPage(ITestContext context) throws Exception {

        log.info("-------- START : DRSQA_11629_ [DWP][PWA] Show DWPC service request on fulfillment incident page ------------");

        log.info("Precondition: Login to DWP admin and enable fulfillment display in My Activity and Request Details");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11629_Driver", dwpDriver);
        context.setAttribute("DRSQA_11629_Product", Product.Helix_DWP);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.clickOnSubMenu(SolQAConstants.Fulfillment_Details);
        dwpFeatureMethod.configureFulfillmentDetails();
        dwpFeatureMethod.closeBrowser();

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11629_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11629_Product", Product.Helix_DWPC);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11629_TestData");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("serviceName"), dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(dwpcTestData.get("serviceName")), "Service is not present/published in DWPC: " + dwpcTestData.get("serviceName"));
        dwpcFeatureMethods.closeBrowser();

        log.info("Step 1: Submit service request in DWP");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11629_TestData");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11629_Driver", dwpDriver);
        context.setAttribute("DRSQA_11629_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        String serviceName = dwpTestData.get("serviceName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }

        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("AssetLabel"), dwpTestData.get("AssetValue"));
        dwpFeatureMethod.fillTextFieldByLabel("First Name", dwpTestData.get("FirstName"));
        dwpFeatureMethod.fillDropdownField("Priority", dwpTestData.get("Priority"));
        dwpFeatureMethod.clickOnNextButton();
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11629_submitRequest", SolQAConstants.appDWP);

        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "Service request ID is not created");
        log.pass("DRSQA_11629: Generated Request ID - " + requestId);

        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        String incidentId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(incidentId != null && !incidentId.trim().isEmpty(), "Fulfillment Incident ID is not available on request details");
        log.pass("DRSQA_11629: Generated Fulfillment Incident ID - " + incidentId);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 2: Open fulfillment incident in SmartIT PV and verify DWP request is shown");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11629_Driver", smartITDriver);
        context.setAttribute("DRSQA_11629_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");

        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.INCIDENT_REQUEST_TYPE, incidentId);
        String incidentUrl = smartITDriver.getCurrentUrl();
        Assert.assertTrue(incidentUrl.contains("incidentPV"), "Opened incident is not in PV. URL: " + incidentUrl);

        String dwpRequestIDOnSmartIT = smartITFeatureMethodsPage.getDWPRequestID_FromSmartIT();
        log.info("DWP Service Request ID on incident view page: " + dwpRequestIDOnSmartIT);

        Assert.assertEquals(dwpRequestIDOnSmartIT, requestId.trim(), "DWP Service Request ID is not matching on incident view page.");
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11629_IncidentFulfillment_Verified", SolQAConstants.appSmartIT);

        log.info("-------- END : DRSQA_11629_ [DWP][PWA] Show DWPC service request on fulfillment incident page ------------");
    }

    @Test(description = "DWP Catalog - Imported SRD request bi-directional communication with Incident fulfillment", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11477", Products = {Product.Helix_DWP, Product.SmartIT, Product.Helix_ITSM})
    public void DRSQA_11477_ImportSRDRequest_BidirectionalCommunication_IncidentFulfillment(ITestContext context) throws Exception {

        log.info("-------- START: DRSQA_11477 - Imported SRD request bi-directional communication with Incident fulfillment ------------");

        log.info("Precondition: Enable fulfillment visibility in DWP admin (My Activity + Request Details)");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11477_Driver", dwpDriver);
        context.setAttribute("DRSQA_11477_Product", Product.Helix_DWP);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.clickOnSubMenu(SolQAConstants.Fulfillment_Details);
        dwpFeatureMethod.configureFulfillmentDetails();
        dwpFeatureMethod.closeBrowser();

        log.info("Step 1: Submit imported SRD in DWP and add a comment + attachment");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11477_service_info");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11477_Driver", dwpDriver);
        context.setAttribute("DRSQA_11477_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("serviceName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("Question_1"), dwpTestData.get("Answer_1"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11477_submitRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.refreshBrowser();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "Service request ID is not created");
        log.pass("DRSQA_11477: Generated Request ID - " + requestId);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        String incidentId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(incidentId != null && !incidentId.trim().isEmpty(), "Fulfillment Incident ID is not available on request details");
        log.pass("DRSQA_11477: Fulfillment Incident ID - " + incidentId);
        String dwpComment = dwpTestData.get("Comment");
        String dwpAttachmentName = dwpTestData.get("AttachmentName");
        File dwpAttachment = CommonMethods.createTextFile(dwpAttachmentName);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(dwpComment, dwpAttachment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11477_DWPCommentAttachmentAdded", SolQAConstants.appDWP);

        log.info("Step 2: Verify DWP comment/attachment on SRM request in SmartIT and add a public comment + attachment");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11477_Driver", smartITDriver);
        context.setAttribute("DRSQA_11477_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nishaUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SERVICE_REQUEST, requestId);
        smartITFeatureMethodsPage.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(dwpComment), "DWP comment is not present on Service Request ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(dwpAttachmentName), "DWP attachment is not present on Service Request ticket");
        String srmComment = "SRM comment addition on request through ITSM";
        File srmAttachment = CommonMethods.createTextFile("DRSQA_11477_SRMRequest_Attachment.txt");
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed_ForDWPRequest(srmComment, srmAttachment);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(srmComment), "SRM comment is not present on Service Request ticket");
        Assert.assertNotNull(srmAttachment);
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(srmAttachment.getName()), "SRM attachment is not present on Service Request ticket");

        log.info("Step 3: Verify SRM comment/attachment on DWP request");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyAttachments(srmComment, srmAttachment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11477_VerifySRMAttachmentInDWP", SolQAConstants.appDWP);

        log.info("Step 4: Add public and non-public comments with attachments on incident in SmartIT");
        setDriver(smartITDriver);
        smartITFeatureMethodsPage.refreshBrowser();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.INCIDENT_REQUEST_TYPE, incidentId);
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11477_IncidentInfo");
        String publicComment = smartITTestData.get("publicComment");
        String publicAttachmentName = smartITTestData.get("publicAttachment");
        String nonpublicComment = smartITTestData.get("nonpublicComment");
        String nonpublicAttachmentName = smartITTestData.get("nonpublicAttachment");
        File publicAttachment = CommonMethods.createTextFile(publicAttachmentName);
        File nonpublicAttachment = CommonMethods.createTextFile(nonpublicAttachmentName);
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(publicComment, publicAttachment, true);
        smartITFeatureMethodsPage.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(publicComment), "Public comment is not present on Incident ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(publicAttachmentName), "Public attachment is not present on Incident ticket");
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(nonpublicComment, nonpublicAttachment, false);
        smartITFeatureMethodsPage.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(nonpublicComment), "Non-public comment is not present on Incident ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(nonpublicAttachmentName), "Non-public attachment is not present on Incident ticket");
        if (smartITFeatureMethodsPage.getIncidentStatus_viewPage().contains(SolQAConstants.PENDING)) {
            smartITFeatureMethodsPage.updateIncidentStatus(SolQAConstants.IN_PROGRESS, null, "Sample Note");
            smartITFeatureMethodsPage.saveIncident();
            smartITFeatureMethodsPage.refreshBrowser();
            smartITFeatureMethodsPage.verifyStatusViewPage(SolQAConstants.IN_PROGRESS);
        }

        log.info("Step 5: Verify incident public comment/attachment is visible in DWP and non-public is NOT visible");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyAttachments(publicComment, publicAttachment);
        dwpFeatureMethod.verifyCommentNotPresent(nonpublicComment);
        dwpFeatureMethod.verifyAttachmentNotPresent(nonpublicAttachment);
        log.info("Step 6: Cancel the request and verify status is updated");
        dwpFeatureMethod.cancelServiceRequest();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("StatusAfterCancellingRequest"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11477_SRDCancelled", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 7: Verify incident status in ITSM after cancellation");
        smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
        smartITFeatureMethodsPage.refreshSmartITPage();
        smartITFeatureMethodsPage.verifyStatusViewPage(SolQAConstants.CANCELLED);
        smartITFeatureMethodsPage.closeBrowser();
        log.info("-------- END: DRSQA_11477 - Imported SRD request bi-directional communication with Incident fulfillment ------------");
    }

    @Test(description = "DWP Catalog - Imported SRD request bi-directional communication with WorkOrder fulfillment", groups = {"Regression_P2"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11568", Products = {Product.Helix_DWP, Product.SmartIT, Product.Helix_ITSM})
    public void DRSQA_11568_ImportSRDRequest_BidirectionalCommunication_WorkorderFulfillment(ITestContext context) throws Exception {

        log.info("-------- START: DRSQA_11568 - Imported SRD request bi-directional communication with WorkOrder fulfillment ------------");
        log.info("Precondition: Enable fulfillment visibility in DWP admin (My Activity + Request Details)");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11568_Driver", dwpDriver);
        context.setAttribute("DRSQA_11568_Product", Product.Helix_DWP);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.clickOnSubMenu(SolQAConstants.Fulfillment_Details);
        dwpFeatureMethod.configureFulfillmentDetails();
        dwpFeatureMethod.closeBrowser();

        log.info("------------------- Step 1: Submit SRD in DWP --------------------");
        log.info("Step 1: Submit imported SRD in DWP and add a comment + attachment");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11568_SRDInfo");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11568_Driver", dwpDriver);
        context.setAttribute("DRSQA_11568_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("serviceName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("Question_1"), dwpTestData.get("Answer_1"));
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("Question_2"), dwpTestData.get("Answer_2"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11568_submitRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "Service request ID is not created");
        log.pass("DRSQA_11568: Generated Request ID - " + requestId);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        String workOrderId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(workOrderId != null && !workOrderId.trim().isEmpty(), "Fulfillment Work Order ID is not available on request details");
        log.pass("DRSQA_11568: Fulfillment Work Order ID - " + workOrderId);
        String dwpComment = dwpTestData.get("Comment");
        String dwpAttachmentName = dwpTestData.get("AttachmentName");
        File dwpAttachment = CommonMethods.createTextFile(dwpAttachmentName);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(dwpComment, dwpAttachment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11568_DWPCommentAttachmentAdded", SolQAConstants.appDWP);

        log.info("-------------------- Step 2: Verify DWP comment/attachment in SmartIT Service Request --------------------");
        log.info("Step 2: Verify DWP comment/attachment on SRM request in SmartIT and add a public comment + attachment");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11568_Driver", smartITDriver);
        context.setAttribute("DRSQA_11568_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nishaUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SERVICE_REQUEST, requestId);
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(dwpComment), "DWP comment is not present on Service Request ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(dwpAttachmentName), "DWP attachment is not present on Service Request ticket");
        String srmComment = "SRM comment addition on request through ITSM";
        File srmAttachment = CommonMethods.createTextFile("DRSQA_11568_SRMRequest_Attachment.txt");
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed_ForDWPRequest(srmComment, srmAttachment);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(srmComment), "SRM comment is not present on Service Request ticket");
        Assert.assertNotNull(srmAttachment);
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(srmAttachment.getName()), "SRM attachment is not present on Service Request ticket");

        log.info("-------------------- Step 3: Verify SRM comment/attachment back in DWP --------------------");
        log.info("Step 3: Verify SRM comment/attachment on DWP request");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyAttachments(srmComment, srmAttachment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11568_VerifySRMAttachmentInDWP", SolQAConstants.appDWP);

        smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
        log.info("-------------------- Step 5: Add public + non-public notes on Work Order in SmartIT --------------------");
        log.info("Step 5: Add public and non-public comments with attachments on Work Order in SmartIT");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11568_WorkorderInfo");
        String publicComment = smartITTestData.get("publicComment");
        String publicAttachmentName = smartITTestData.get("publicAttachment");
        String nonpublicComment = smartITTestData.get("nonpublicComment");
        String nonpublicAttachmentName = smartITTestData.get("nonpublicAttachment");
        File publicAttachment = CommonMethods.createTextFile(publicAttachmentName);
        File nonpublicAttachment = CommonMethods.createTextFile(nonpublicAttachmentName);
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.Work_Order, workOrderId);
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(publicComment, publicAttachment, true);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(publicComment), "Public comment is not present on Work Order ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(publicAttachmentName), "Public attachment is not present on Work Order ticket");
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(nonpublicComment, nonpublicAttachment, false);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(nonpublicComment), "Non-public comment is not present on Work Order ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(nonpublicAttachmentName), "Non-public attachment is not present on Work Order ticket");

        log.info("-------------------- Step 6: Verify public is visible in DWP but non-public is not, then cancel SRD --------------------");
        log.info("Step 6: Verify public comment/attachment is visible in DWP and non-public is NOT visible; cancel SRD");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyAttachments(publicComment, publicAttachment);
        dwpFeatureMethod.verifyCommentNotPresent(nonpublicComment);
        dwpFeatureMethod.verifyAttachmentNotPresent(nonpublicAttachment);
        dwpFeatureMethod.cancelServiceRequest();
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("StatusAfterCancellingRequest"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11568_SRDCancelled", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 7: Verify Work Order status in ITSM after cancellation --------------------");
        smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
        smartITFeatureMethodsPage.refreshBrowser();
        smartITFeatureMethodsPage.verifyStatusViewPage(SolQAConstants.CANCELLED);
        smartITFeatureMethodsPage.closeBrowser();
        log.info("-------- END: DRSQA_11568 - Imported SRD request bi-directional communication with WorkOrder fulfillment ------------");
    }

    @Test(description = "DWP Catalog - Imported SRD request bi-directional communication with Change fulfillment", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11567", Products = {Product.Helix_DWP, Product.SmartIT, Product.Helix_ITSM})
    public void DRSQA_11567_ImportSRDRequest_BidirectionalCommunication_ChangeFulfillment(ITestContext context) throws Exception {

        log.info("-------- START: DRSQA_11567 - Imported SRD request bi-directional communication with Change fulfillment ------------");

        log.info("Precondition: Enable fulfillment visibility in DWP admin (My Activity + Request Details)");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11567_Driver", dwpDriver);
        context.setAttribute("DRSQA_11567_Product", Product.Helix_DWP);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdmin();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.clickOnSubMenu(SolQAConstants.Fulfillment_Details);
        dwpFeatureMethod.configureFulfillmentDetails();
        dwpFeatureMethod.closeBrowser();

        log.info("Step 1: Submit imported SRD in DWP and add a comment + attachment");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "apavlikUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11567_service_info");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11567_Driver", dwpDriver);
        context.setAttribute("DRSQA_11567_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("serviceName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillComboBoxField(dwpTestData.get("Question_1"), "1LLZ6BS");
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11567_submitRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "Service request ID is not created");
        log.pass("DRSQA_11567: Generated Request ID - " + requestId);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        String changeId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(changeId != null && !changeId.trim().isEmpty(), "Fulfillment Change ID is not available on request details");
        log.pass("DRSQA_11567: Fulfillment Change ID - " + changeId);
        String dwpComment = dwpTestData.get("Comment");
        String dwpAttachmentName = dwpTestData.get("AttachmentName");
        File dwpAttachment = CommonMethods.createTextFile(dwpAttachmentName);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(dwpComment, dwpAttachment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11567_DWPCommentAttachmentAdded", SolQAConstants.appDWP);

        log.info("Precondition: Login to ITSM and enable PV and HKM settings");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11567_Driver", itsmDriver);
        context.setAttribute("DRSQA_11567_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        log.info("Login to ITSM and enable PV and HKM settings");
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
        itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
        itsmFeatureMethods.closeBrowser();

        log.info("Step 2: Verify DWP comment/attachment on SRM request in SmartIT and add a public comment + attachment");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11567_Driver", getDriver());
        context.setAttribute("DRSQA_11567_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ejohnUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SERVICE_REQUEST, requestId);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(dwpComment), "DWP comment is not present on Service Request ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(dwpAttachmentName), "DWP attachment is not present on Service Request ticket");
        String srmComment = "SRM comment addition on request through ITSM";
        File srmAttachment = CommonMethods.createTextFile("DRSQA_11567_SRMRequest_Attachment.txt");
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed_ForDWPRequest(srmComment, srmAttachment);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(srmComment), "SRM comment is not present on Service Request ticket");
        Assert.assertNotNull(srmAttachment);
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(srmAttachment.getName()), "SRM attachment is not present on Service Request ticket");
        smartITFeatureMethodsPage.closeBrowser();

        log.info("Step 3: Verify SRM comment/attachment on DWP request");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyAttachments(srmComment, srmAttachment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11567_VerifySRMAttachmentInDWP", SolQAConstants.appDWP);

        log.info("Step 4: Add public and non-public comments with attachments on Change Request in SmartIT");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11567_Driver", smartITDriver);
        context.setAttribute("DRSQA_11567_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ctheronUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11567_ChangeInfo");
        String publicComment = smartITTestData.get("publicComment");
        String publicAttachmentName = smartITTestData.get("publicAttachment");
        String nonpublicComment = smartITTestData.get("nonpublicComment");
        String nonpublicAttachmentName = smartITTestData.get("nonpublicAttachment");
        File publicAttachment = CommonMethods.createTextFile(publicAttachmentName);
        File nonpublicAttachment = CommonMethods.createTextFile(nonpublicAttachmentName);
        smartITFeatureMethodsPage.applyFilterAndOpenTicket("Change Request", changeId);
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(publicComment, publicAttachment, true);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(publicComment), "Public comment is not present on Change Request ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(publicAttachmentName), "Public attachment is not present on Change Request ticket");
        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(nonpublicComment, nonpublicAttachment, false);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(nonpublicComment), "Non-public comment is not present on Change Request ticket");
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(nonpublicAttachmentName), "Non-public attachment is not present on Change Request ticket");

        log.info("Step 5: Verify public comment/attachment is visible in DWP and non-public is NOT visible; cancel SRD");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyAttachments(publicComment, publicAttachment);
        dwpFeatureMethod.verifyCommentNotPresent(nonpublicComment);
        dwpFeatureMethod.verifyAttachmentNotPresent(nonpublicAttachment);
        dwpFeatureMethod.cancelServiceRequest();
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("StatusAfterCancellingRequest"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11567_SRDCancelled", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 6: Verify Change status in ITSM after cancellation --------------------");
        smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
        smartITFeatureMethodsPage.refreshSmartITPage();
        smartITFeatureMethodsPage.verifyChangeStatus(SolQAConstants.CANCELLED);
        smartITFeatureMethodsPage.closeBrowser();
        log.info("-------- END: DRSQA_11567 - Imported SRD request bi-directional communication with Change fulfillment ------------");
    }

    @Test(description = "[DWP][SmartIT][ITSM][Approval] Enable/disable 'Notes' and 'Summary' fields in Change approval (UI-only)", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11519", Products = {Product.Helix_DWP, Product.SmartIT, Product.Helix_ITSM})
    public void DRSQA_11519_Enable_Disable_NotesAndSummary_Field(ITestContext context) throws Exception {

        log.info("-------- START: DRSQA_11519_Enable_Disable_NotesAndSummary_Field --------");
        String settingName;
        String providerName;
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11519_Enable_Disable_NotesAndSummary_Field");
        providerName = dwpTestData.get("ProviderName");
        settingName = dwpTestData.get("SettingName");

        try {
            log.info("-------------------- Step 0: Enable provider setting in DWP Admin --------------------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11519_Driver", dwpDriver);
            context.setAttribute("DRSQA_11519_Product", Product.Helix_DWP);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "liamUser");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.navigateToAdminPage();
            dwpFeatureMethod.verifyLoginToDWPAdmin();
            dwpFeatureMethod.clickOnHamburgerMenu();
            dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
            dwpFeatureMethod.openProviderSettings(providerName, 20);
            dwpFeatureMethod.setCheckboxValueInProviderSettings(settingName, true);
            dwpFeatureMethod.updateSettingsAndReloadProviders();
            dwpFeatureMethod.closeBrowser();

            log.info("-------------------- Step 1: Create Change Request in SmartIT --------------------");
            smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITDriver);
            context.setAttribute("DRSQA_11519_Driver", smartITDriver);
            context.setAttribute("DRSQA_11519_Product", Product.SmartIT);
            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
            smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11519_CreateChangeRequest");
            String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String changeTitle = smartITTestData.get("Change Title") + suffix;
            String changeDescription = smartITTestData.get("Change Description") + suffix;
            smartITTestData.put("Change Title", changeTitle);
            smartITTestData.put("Change Description", changeDescription);
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
            smartITFeatureMethodsPage.createChange(smartITTestData);
            String changeId = smartITFeatureMethodsPage.FetchChangeTicketID();
            Assert.assertTrue(changeId != null && !changeId.trim().isEmpty(), "Change is not created");
            smartITFeatureMethodsPage.captureScreenshot("DRSQA_11519_ChangeCreated", SolQAConstants.appSmartIT);
            smartITFeatureMethodsPage.closeBrowser();

            log.info("-------------------- Step 2: Login to ITSM and add ad-hoc approver --------------------");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_11519_Driver", itsmDriver);
            context.setAttribute("DRSQA_11519_Product", Product.Helix_ITSM);
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.navigateMenu(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
            itsmFeatureMethods.searchChange(changeId);
            itsmFeatureMethods.addApproverToChange(dwpTestData.get("ApproverType"), dwpTestData.get("ApproverFirstName"), dwpTestData.get("ApproverLastName"));
            itsmFeatureMethods.captureScreenshot("DRSQA_11519_ApproverAdded", SolQAConstants.appITSM);
            itsmFeatureMethods.closeBrowser();

            log.info("-------------------- Step 3: Login to DWP as approver and verify Summary/Notes are present --------------------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11519_Driver", dwpDriver);
            context.setAttribute("DRSQA_11519_Product", Product.Helix_DWP);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "pbunyon");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openSrmRequestByRequestID(changeId);
            dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("SummaryFieldLabel"), changeTitle);
            dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("NotesFieldLabel"), changeDescription);
            dwpFeatureMethod.captureScreenshot("DRSQA_11519_SummaryNotesEnabled", SolQAConstants.appDWP);
            dwpFeatureMethod.closeBrowser();

            log.info("-------------------- Step 4: Disable provider setting in DWP Admin --------------------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11519_Driver", dwpDriver);
            context.setAttribute("DRSQA_11519_Product", Product.Helix_DWP);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "liamUser");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.navigateToAdminPage();
            dwpFeatureMethod.verifyLoginToDWPAdmin();
            dwpFeatureMethod.clickOnHamburgerMenu();
            dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
            dwpFeatureMethod.openProviderSettings(providerName, 20);
            dwpFeatureMethod.setCheckboxValueInProviderSettings(settingName, false);
            dwpFeatureMethod.updateSettingsAndReloadProviders();
            dwpFeatureMethod.closeBrowser();

            log.info("-------------------- Step 5: Login to DWP as approver and verify Summary/Notes are NOT present --------------------");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11519_Driver", dwpDriver);
            context.setAttribute("DRSQA_11519_Product", Product.Helix_DWP);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "pbunyon");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openSrmRequestByRequestID(changeId);
            dwpFeatureMethod.verifySubmittedRequestEventDetailNotPresent(dwpTestData.get("SummaryFieldLabel"));
            dwpFeatureMethod.verifySubmittedRequestEventDetailNotPresent(dwpTestData.get("NotesFieldLabel"));
            dwpFeatureMethod.captureScreenshot("DRSQA_11519_SummaryNotesDisabled", SolQAConstants.appDWP);
            dwpFeatureMethod.closeBrowser();
            log.info("-------- END: DRSQA_11519_Enable_Disable_NotesAndSummary_Field --------");
        } finally {
            try {
                dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
                setDriver(dwpDriver);
                context.setAttribute("DRSQA_11519_Driver", dwpDriver);
                context.setAttribute("DRSQA_11519_Product", Product.Helix_DWP);
                dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "liamUser");
                login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
                dwpFeatureMethod = new DWPFeatureMethods();
                dwpFeatureMethod.verifyDWPHomePageOpened();
                dwpFeatureMethod.navigateToAdminPage();
                dwpFeatureMethod.verifyLoginToDWPAdmin();
                dwpFeatureMethod.clickOnHamburgerMenu();
                dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
                dwpFeatureMethod.clickOnSubMenu(SolQAConstants.PROVIDER);
                dwpFeatureMethod.openProviderSettings(providerName, 20);
                dwpFeatureMethod.setCheckboxValueInProviderSettings(settingName, true);
                dwpFeatureMethod.updateSettingsAndReloadProviders();
                dwpFeatureMethod.closeBrowser();
            } catch (Exception e) {
                log.error("Failed to restore DWP provider setting '" + settingName + "': " + e.getMessage());
            }
        }
    }

    //Previous owner aramulwa
    @Test(description = "[DWP][SmartIT] A WO created in SmartIT will generate a service and it shows up on DWP", groups = {"Regression_P2", "sonalsha"}, singleThreaded = true)
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11483", Products = {Product.Helix_DWP, Product.SmartIT, Product.Helix_ITSM})
    public void DRSQA_11483_Create_WO_on_SmartIT_verifyOnDWP(ITestContext context) throws Exception {

        log.info("-------- START: DRSQA_11483_Create_WO_on_SmartIT_verifyOnDWP ------------");

        log.info("Precondition: Login to ITSM and set Service Request on Submit = In Service Request Management");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11483_ITSM_Driver", getDriver());
        context.setAttribute("DRSQA_11483_ITSM_Product", Product.Helix_ITSM);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.LoadForm(SolQAConstants.WOI_CFG_RULES_FORM, SolQAConstants.SEARCH, configProp.get("ARServerAlias"));
        itsmFeatureMethods.setServiceRequestOnSubmitToValue(SolQAConstants.PETRAMCO_COMPANY, "In Service Request Management", "Create Service Request on Submit");
        itsmFeatureMethods.captureScreenshot("DRSQA_11483_ITSM_Precondition", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();

        // -------------------- Step 1: Create Work Order in SmartIT --------------------
        log.info("Step 1: Login to SmartIT and create Work Order for customer bhill");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11483_SmartIT_Driver", getDriver());
        context.setAttribute("DRSQA_11483_SmartIT_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11483_WorkOrderData");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToCreateNewWorkorder();
        smartITFeatureMethodsPage.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethodsPage.clickAssigntoMeInWorkorderCreatePage();
        smartITFeatureMethodsPage.saveWorkorder();

        String workOrderId = smartITFeatureMethodsPage.FetchWorkorderTicketID();
        log.info("Work Order created: " + workOrderId);
        String requestID = smartITFeatureMethodsPage.getDWPRequestID_FromSmartIT();
        log.info("Generated DWP/SRM Request ID from Work Order: " + requestID);
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11483_WOCreated", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        // -------------------- Step 2: Verify generated request is visible/openable in DWP --------------------
        log.info("Step 2: Login to DWP as bhill and open request from global search using Request ID");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11483_DWP_Driver", getDriver());
        context.setAttribute("DRSQA_11483_DWP_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openSrmRequestByRequestID(requestID);
        dwpFeatureMethod.captureScreenshot("DRSQA_11483_DWP_RequestOpened", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------- END: DRSQA_11483_Create_WO_on_SmartIT_verifyOnDWP ------------");
    }

    @Test(description = "Auto-create DWPC request for new ITSM fulfillment record (WO)", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11691", Products = {Product.Helix_ITSM, Product.Helix_SmartIT, Product.Helix_DWPC, Product.Helix_DWP})
    public void DRSQA_11691_AutoCreateDWPCRequestForNewITSMFulfillmentRecord_WO(ITestContext context) {

        log.info("------------ START: DRSQA_11691_AutoCreateDWPCRequestForNewITSMFulfillmentRecord_WO ----------------------");
        String srmModeRequestId;
        String dwpModeRequestId;
        String dwpModeWorkOrderTitle;
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11691_DWPCData");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMTestData, "DRSQA_11691_Data");

        try {
            log.info("-------------------- Precondition 1: Set WorkOrder rule to SRM --------------------");
            log.info("Precondition 1: Login to ITSM and set WO Service Request on Submit = In Service Request Management");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_11691_Driver", itsmDriver);
            context.setAttribute("DRSQA_11691_Product", Product.Helix_ITSM);
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMUsers, "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.LoadForm(SolQAConstants.WOI_CFG_RULES_FORM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
            itsmFeatureMethods.setServiceRequestOnSubmitToValue(itsmTestData.get("Company"), itsmTestData.get("CreateRequestInSRM"), "Create Service Request on Submit");

            log.info("-------------------- Step 1: Create Work Order in SmartIT and verify SRM request id --------------------");
            log.info("Step 1: Create Work Order in SmartIT and ensure generated request is SRM (REQ*)");
            smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITDriver);
            context.setAttribute("DRSQA_11691_Driver", smartITDriver);
            context.setAttribute("DRSQA_11691_Product", Product.SmartIT);
            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITUsers, "pbunyon");
            smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITTestData, "DRSQA_11691_WorkOrderData");
            smartITTestData.put("Summary", "DRSQA11691_SRM_" + System.currentTimeMillis());
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
            smartITFeatureMethodsPage.navigateToCreateNewWorkorder();
            smartITFeatureMethodsPage.FillWorkorderTemplate(smartITTestData);
            smartITFeatureMethodsPage.clickAssigntoMeInWorkorderCreatePage();
            smartITFeatureMethodsPage.saveWorkorder();
            String workOrderIdSrm = smartITFeatureMethodsPage.FetchWorkorderTicketID();
            String workOrderTitleSrm = smartITFeatureMethodsPage.getTitle_viewPage();
            srmModeRequestId = smartITFeatureMethodsPage.getDWPRequestID_FromSmartIT();
            log.info("SRM mode WO: " + workOrderIdSrm + ", Title: " + workOrderTitleSrm + ", RequestID: " + srmModeRequestId);
            Assert.assertTrue(srmModeRequestId != null && srmModeRequestId.startsWith("REQ"), "Expected SRM request id to start with REQ but found: " + srmModeRequestId);

            log.info("-------------------- Step 2: Verify SRM-mode request is NOT present in DWPC Service Requests report --------------------");
            log.info("Step 2: Login to DWPC and verify SRM-mode request is not present in Service Requests report");
            dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(dwpcDriver);
            context.setAttribute("DRSQA_11691_Driver", dwpcDriver);
            context.setAttribute("DRSQA_11691_Product", Product.Helix_DWPC);
            Map<Object, String> dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCUsers, "Liam_User");
            login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
            dwpcFeatureMethods = new DWPCFeatureMethods();
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            Assert.assertFalse(dwpcFeatureMethods.isServiceRequestPresentInReports(dwpcTestData.get("dwpcReportsMenu"), dwpcTestData.get("dwpcServiceRequestsSubMenu"), srmModeRequestId),
                    "SRM-mode request should not be present in DWPC reports for request: " + srmModeRequestId);

            log.info("-------------------- Precondition 2: Set WorkOrder rule to DWP --------------------");
            log.info("Precondition 2: Login to ITSM and set WO Service Request on Submit = In Digital Workplace");
            setDriver(itsmDriver);
            itsmFeatureMethods.LoadForm(SolQAConstants.WOI_CFG_RULES_FORM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
            itsmFeatureMethods.setServiceRequestOnSubmitToValue(itsmTestData.get("Company"), itsmTestData.get("CreateRequestInDWP"), "Create Service Request on Submit");
            itsmFeatureMethods.closeBrowser();

            log.info("-------------------- Step 3: Create Work Order in SmartIT (DWP mode) and capture request id --------------------");
            log.info("Step 3: Create Work Order in SmartIT (DWP mode) and capture generated request id");
            setDriver(smartITDriver);
            smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITTestData, "DRSQA_11691_WorkOrderData");
            smartITTestData.put("Workorder Title", "DRSQA11691_DWP_" + System.currentTimeMillis());
            smartITFeatureMethodsPage.refreshBrowser();
            smartITFeatureMethodsPage.navigateToCreateNewWorkorder();
            smartITFeatureMethodsPage.FillWorkorderTemplate(smartITTestData);
            smartITFeatureMethodsPage.clickAssigntoMeInWorkorderCreatePage();
            smartITFeatureMethodsPage.saveWorkorder();
            String workOrderIdDwp = smartITFeatureMethodsPage.FetchWorkorderTicketID();
            dwpModeWorkOrderTitle = smartITFeatureMethodsPage.getTitle_viewPage();
            dwpModeRequestId = smartITFeatureMethodsPage.getDWPRequestID_FromSmartIT();
            log.info("DWP mode WO: " + workOrderIdDwp + ", Title: " + dwpModeWorkOrderTitle + ", RequestId: " + dwpModeRequestId);
            Assert.assertTrue(dwpModeRequestId != null && !dwpModeRequestId.trim().isEmpty(), "DWP-mode request id is empty");
            smartITFeatureMethodsPage.closeBrowser();

            log.info("-------------------- Step 4: Verify request is visible in DWP and title matches WO title --------------------");
            log.info("Step 4: Login to DWP and verify request exists and has expected title");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11691_Driver", dwpDriver);
            context.setAttribute("DRSQA_11691_Product", Product.Helix_DWP);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(dwpModeRequestId);
            dwpFeatureMethod.verifySrdTitle(dwpModeWorkOrderTitle);
            dwpFeatureMethod.closeBrowser();

            log.info("-------------------- Step 5: Verify request is present in DWPC Service Requests report --------------------");
            log.info("Step 5: Login to DWPC and verify DWP-mode request is present in Service Requests report");
            setDriver(dwpcDriver);
            dwpcFeatureMethods.refreshBrowser();
            Assert.assertTrue(dwpcFeatureMethods.isServiceRequestPresentInReports(dwpcTestData.get("dwpcReportsMenu"), dwpcTestData.get("dwpcServiceRequestsSubMenu"), dwpModeRequestId),
                    "DWP-mode request should be present in DWPC reports for request: " + dwpModeRequestId);
            dwpcFeatureMethods.closeBrowser();
            log.info("------------ END: DRSQA_11691_AutoCreateDWPCRequestForNewITSMFulfillmentRecord_WO ----------------------");
        } finally {
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_11691_Driver", itsmDriver);
            context.setAttribute("DRSQA_11691_Product", Product.Helix_ITSM);
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMUsers, "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.LoadForm(SolQAConstants.WOI_CFG_RULES_FORM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
            itsmFeatureMethods.setServiceRequestOnSubmitToValue(itsmTestData.get("Company"), itsmTestData.get("CreateRequestInSRM"), "Create Service Request on Submit");
            itsmFeatureMethods.closeBrowser();
        }
    }

    @Test(description = "Verify request ID visibility on workorder for Progressive view", groups = {"Regression_P1", "SmarTIT", "DWP"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11580", Products = {Product.Helix_DWP, Product.SmartIT})
    public void DRSQA11580_RequestID_visibility_on_workorder_PV(ITestContext context) throws Exception {

        log.info("------- START: DRSQA11580_RequestID_visibility_on_workorder_PV");

        log.info("Step 1: Login to DWP and submit SRD (Donations SRD)");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11580_Driver", dwpDriver);
        context.setAttribute("DRSQA_11580_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA11580_TestData");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("serviceName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("Question_1"), dwpTestData.get("Answer_1"));
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("Question_2"), dwpTestData.get("Answer_2"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA11580_submitRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "Service request ID is not created in DWP");
        log.info("Submitted Request ID: " + requestId);
        dwpFeatureMethod.captureScreenshot("DRSQA11580_RequestCreatedInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 2: Login to SmartIT, open SRM Service Request, then open Fulfillment Work Order and verify Request ID");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11580_Driver", smartITDriver);
        context.setAttribute("DRSQA_11580_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nishaUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SERVICE_REQUEST, requestId);
        smartITFeatureMethodsPage.captureScreenshot("DRSQA11580_ServiceRequestOpenedInSmartIT", SolQAConstants.appSmartIT);
        String workOrderId = smartITFeatureMethodsPage.getWorkOrderIDFromFulfillmentSection();
        Assert.assertTrue(workOrderId != null && !workOrderId.trim().isEmpty(), "Work Order ID is not present in Fulfillment section");
        log.info("Fulfillment Work Order ID: " + workOrderId);
        smartITFeatureMethodsPage.clickOnWorkOrderIDHyperLink();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA11580_WorkOrderOpenedInSmartIT", SolQAConstants.appSmartIT);
        String workOrderPVUrl = smartITDriver.getCurrentUrl();
        Assert.assertTrue(workOrderPVUrl.contains("workorderPV"), "Work order is not opened in progressive view. Expected workorderPV but found - " + workOrderPVUrl);
        String reqIdOnWorkOrder = smartITFeatureMethodsPage.getDWPRequestID_FromSmartIT();
        log.info("Request ID on Work Order: " + reqIdOnWorkOrder);
        Assert.assertTrue(reqIdOnWorkOrder != null && reqIdOnWorkOrder.trim().contains(requestId), "Request ID on Work Order is not matching expected: " + requestId + " but found: " + reqIdOnWorkOrder);
        smartITFeatureMethodsPage.captureScreenshot("DRSQA11580_RequestIdOnWorkOrderVerified", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();
        log.info("------- END: DRSQA11580_RequestID_visibility_on_workorder_PV");
    }

    @Test(description = "Verify responses of service with conditional questions submitted via Smart Recorder in DWP", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11555", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_11555_Allow_SD_agentsToSubmit_Service_smartRecorder(ITestContext context) throws Exception {

        log.info("********* START: DRSQA_11555_Allow_SD_agentsToSubmit_Service_smartRecorder **************");
        String submittedRequestId;
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11555_Driver", smartITDriver);
        context.setAttribute("DRSQA_11555_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11555_SmartRecorder");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11555_Data");
        String serviceName = dwpTestData.get("ServiceRequestName");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        log.info("Navigate to Smart Recorder");
        smartITFeatureMethodsPage.navigateToSmartRecorder();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11555_navigatedToSmartRecorder", SolQAConstants.appSmartIT);
        String customerName = smartITTestData.get("CustomerName");
        smartITFeatureMethodsPage.selectCustomerOrAssetInSmartRecorder(customerName);
        log.info("Select the service template from Digital Workplace Catalog panel");
        smartITFeatureMethodsPage.searchAndSelectTemplateInSmartRecorderFromTemplateList(serviceName);
        log.info("Click on Create Service request");
        smartITFeatureMethodsPage.clickCreateServiceRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11555_createServiceRequestClicked", SolQAConstants.appSmartIT);
        log.info("Submit service by answering conditional questions");
        smartITFeatureMethodsPage
                .fillTextFieldByLabelInSmartRecorderDWPWidget(SolQAConstants.DESCRIPTION_LABEL, dwpTestData.get("Description"))
                .fillTextFieldByLabelInSmartRecorderDWPWidget(SolQAConstants.SUMMARY_LABEL, dwpTestData.get("Summary"))
                .fillDropdownFieldInSmartRecorderDWPWidget(dwpTestData.get("VendorQuestion"), dwpTestData.get("VendorType"))
                .clickNextInSmartRecorderDWPWidget()
                .fillCounterFieldInSmartRecorderDWPWidget(dwpTestData.get("FoodQualityQuestion"), dwpTestData.get("FoodQuality"));
        log.info("Click on Submit request");
        smartITFeatureMethodsPage.clickSubmitRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11555_serviceRequestSubmitted", SolQAConstants.appSmartIT);
        submittedRequestId = smartITFeatureMethodsPage.getFulfillmentID();
        Assert.assertTrue(submittedRequestId != null && !submittedRequestId.trim().isEmpty(), "Request is not submitted");
        log.info("Submitted Request ID: " + submittedRequestId);
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11555_serviceRequestCreated", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        // -------------------- Step 7-8: Login to DWP as service customer and verify request + answers --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11555_Driver", dwpDriver);
        context.setAttribute("DRSQA_11555_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestId);
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("VendorQuestion"), dwpTestData.get("VendorType"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("FoodQualityQuestion"), dwpTestData.get("FoodQuality"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11555_requestVerifiedInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        log.pass("********* END: DRSQA_11555_Allow_SD_agentsToSubmit_Service_smartRecorder **************");
    }

    @Test(description = "DWP Service Broker catalog items in Smart Recorder with Catalog based service requests", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11554", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.SmartIT})
    public void DRSQA_11554_Verify_DWPServiceBroker_Submit_Service_RequestTemplate_smartRecorder(ITestContext context) throws Exception {

        log.info("********Start ** DRSQA_11554_Verify_DWPServiceBroker_Submit_Service_RequestTemplate_smartRecorder *******************");

        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11554_Driver", smartITDriver);
        context.setAttribute("DRSQA_11554_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "SlashCharacterUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11554_Data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11554_service");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        log.info("Navigate to Smart Recorder");
        smartITFeatureMethodsPage.navigateToSmartRecorder();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11554_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

        log.info("Select customer and service template from Digital Workplace Catalog");
        smartITFeatureMethodsPage.selectCustomerOrAssetInSmartRecorder(dwpUserData.get("UserName"));
        smartITFeatureMethodsPage.searchAndSelectTemplateInSmartRecorderFromTemplateList(dwpcTestData.get("serviceName"));

        log.info("Click Create Service Request");
        smartITFeatureMethodsPage.clickCreateServiceRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11554_creatingServicerequest", SolQAConstants.appSmartIT);

        File uploadFile = CommonMethods.createTextFile(dwpTestData.get("AttachmentFileName"));
        log.info("Fill details and attach reference document");
        smartITFeatureMethodsPage.fillTextFieldByLabelInSmartRecorderDWPWidget(dwpTestData.get("NewNameLabel"), dwpTestData.get("NewNameValue"))
                .fillAttachmentFieldByLabelInSmartRecorderDWPWidget(dwpTestData.get("AttachmentLabel"), uploadFile);

        smartITFeatureMethodsPage.clickSubmitRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11554_serviceRequestSubmitted", SolQAConstants.appSmartIT);

        String submittedRequestId = smartITFeatureMethodsPage.getFulfillmentID();
        Assert.assertTrue(submittedRequestId != null && !submittedRequestId.trim().isEmpty(), "Request is not submitted");
        log.info("Submitted Request ID: " + submittedRequestId);

        smartITFeatureMethodsPage.clickOnRefreshButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11554_serviceRequestCreated", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        // -------------------- Step 6-7: Login to DWP as customer and verify request + answers --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11554_Driver", dwpDriver);
        context.setAttribute("DRSQA_11554_Product", Product.Helix_DWP);

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestId);
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("NewNameLabel"), dwpTestData.get("NewNameValue"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("AttachmentLabel"), dwpTestData.get("AttachmentFileName"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11554_CaseInProgress", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 8-12: Login to BWF and resolve case tasks + close case --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11554_Driver", bwfDriver);
        context.setAttribute("DRSQA_11554_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11554_BWFData");

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(submittedRequestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(submittedRequestId, "Case ID");
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(SolQAConstants.PENDING);
        bwfFeatureMethods.setStatusReason(bwfTestData.get("StatusReasonPending"));
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.clickMarkAsCompleteOnTaskInBWFCase();
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.clickMarkAsCompleteOnTaskInBWFCase();

        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.RESOLVED);
        bwfFeatureMethods.refreshBrowser();
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.RESOLVED, "Failed to display Resolved Status");
        bwfFeatureMethods.captureScreenshot("DRSQA_11554_caseResolvedInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("********END ** DRSQA_11554_Verify_DWPServiceBroker_Submit_Service_RequestTemplate_smartRecorder *******************");
    }

    @Test(description = "DWP Service Broker catalog items in Smart Recorder to submit Different types of Question Answers of service", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11551", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.SmartIT})
    public void DRSQA_11551_Verify_DWPServiceBroker_Submit_Service_smartRecorder(ITestContext context) throws Exception {

        log.info("********Start ** DRSQA_11551_Verify_DWPServiceBroker_Submit_Service_smartRecorder *******************");

        log.info("-------------------- Step 1-5: Login to SmartIT as SD agent and submit service from Smart Recorder --------------------");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11551_SmartIT_Driver", smartITDriver);
        context.setAttribute("DRSQA_11551_SmartIT_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITUsers, "pbunyon");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "SlashCharacterUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITTestData, "DRSQA_11551_SmartRecorder");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11551_Data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11551_service");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11551_BWFData");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethodsPage.navigateToSmartRecorder();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11551_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.selectCustomerOrAssetInSmartRecorder(dwpUserData.get("UserName"));
        smartITFeatureMethodsPage.searchAndSelectTemplateInSmartRecorder(dwpcTestData.get("serviceName"), false);

        smartITFeatureMethodsPage.clickCreateServiceRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11551_creatingServicerequest", SolQAConstants.appSmartIT);

        File uploadFile = CommonMethods.createTextFile(dwpTestData.get("AttachmentFileName"));

        // Fill different question types inside the embedded DWP widget
        smartITFeatureMethodsPage
                .fillTextFieldByLabelInSmartRecorderDWPWidget(dwpTestData.get("TextQuestionLabel"), smartITTestData.get("TextQuestion"))
                .selectCheckboxOptionInSmartRecorderDWPWidget(dwpTestData.get("CheckboxQuestionLabel"), smartITTestData.get("CheckboxQuestion"))
                .selectRadioOptionInSmartRecorderDWPWidget(dwpTestData.get("RadioButtonQuestionLabel"), smartITTestData.get("RadioButtonQuestion"))
                .fillAttachmentFieldByLabelInSmartRecorderDWPWidget(dwpTestData.get("AttachmentQuestionLabel"), uploadFile)
                .fillCounterFieldInSmartRecorderDWPWidget(dwpTestData.get("CounterLabel"), smartITTestData.get("Counter"));

        smartITFeatureMethodsPage.clickSubmitRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11551_serviceRequestSubmitted", SolQAConstants.appSmartIT);

        String submittedRequestId = smartITFeatureMethodsPage.getFulfillmentID();
        Assert.assertTrue(submittedRequestId != null && !submittedRequestId.trim().isEmpty(), "Request is not submitted");
        log.info("Request id is: " + submittedRequestId);

        smartITFeatureMethodsPage.clickOnRefreshButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11551_serviceRequestCreated", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("-------------------- Step 6-7: Login to DWP as service customer and verify request answers --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11551_DWP_Driver", dwpDriver);
        context.setAttribute("DRSQA_11551_DWP_Product", Product.Helix_DWP);

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        String reqID= dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpcTestData.get("serviceName"));
        dwpFeatureMethod.openSrmRequestByRequestID(submittedRequestId);

        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("TextQuestionLabel"), dwpTestData.get("TextQuestionValue"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("CheckboxQuestionLabel"), dwpTestData.get("CheckboxQuestionValue"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("RadioButtonQuestionLabel"), dwpTestData.get("RadioButtonQuestionValue"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("AttachmentQuestionLabel"), dwpTestData.get("AttachmentFileName"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("CounterLabel"), dwpTestData.get("CounterValue"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11551_CaseInProgress", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 8: Login to BWF and verify the generated case is in Pending --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11551_BWF_Driver", bwfDriver);
        context.setAttribute("DRSQA_11551_BWF_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(reqID);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(reqID);

        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.STATUS_NEW, "Case is not in New status");
        bwfFeatureMethods.closeBrowser();
        log.info("********End ** DRSQA_11551_Verify_DWPServiceBroker_Submit_Service_smartRecorder *******************");
    }

    @Test(description = "Verify status of SRD based service request with approval submitted via Smart Recorder", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11550", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_11550_Allow_SD_agentsToSubmit_SRD_smartRecorder(ITestContext context) throws Exception {

        log.info("********Start ** DRSQA_11550_Allow_SD_agentsToSubmit_SRD_smartRecorder *******************");

        // Umbrella imports "Approval SRD - Person" via DWPC API if missing. SolutionsQA does not include that client.
        // We assume the service is already available in the Smart Recorder DWP Catalog panel.

        log.info("-------------------- Step 1-7: Submit SRD from Smart Recorder and verify approval banner --------------------");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11550_SmartIT_Driver", smartITDriver);
        context.setAttribute("DRSQA_11550_SmartIT_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11550_Data");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11550_Data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11550_service");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethodsPage.navigateToSmartRecorder();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11550_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.selectCustomerOrAssetInSmartRecorder(dwpUserData.get("UserName"));
        smartITFeatureMethodsPage.searchAndSelectTemplateInSmartRecorderFromTemplateList(dwpcTestData.get("serviceName"));
        smartITFeatureMethodsPage.clickCreateServiceRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11550_creatingServicerequest", SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.Fill_ApprovalPersonSRD_smartRecorder(dwpUserData.get("FullName"));
        smartITFeatureMethodsPage.clickSubmitRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11550_serviceRequestSubmitted", SolQAConstants.appSmartIT);

        String serviceReqTicketId = smartITFeatureMethodsPage.getFulfillmentID();
        Assert.assertTrue(serviceReqTicketId != null && !serviceReqTicketId.trim().isEmpty(), "Request is not submitted");
        log.info("DRSQA_11550 Service request id : " + serviceReqTicketId);

        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(smartITTestData.get("TicketType"), serviceReqTicketId);
        smartITFeatureMethodsPage.clickOnRefreshButton();

        Assert.assertTrue(smartITFeatureMethodsPage.verifyYellowApprovalBannerPresent(), "Approval banner did not load in SmartIT service request.");
        Assert.assertTrue(smartITFeatureMethodsPage.verifyYellowApprovalBannerApprovalText(smartITTestData.get("approvalBannerText")), "Approval banner text did not match expected value.");

        smartITFeatureMethodsPage.clickOnBannerShowApprovers();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11550_showingApprovers", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("-------------------- Step 9-10: Customer sees request Waiting Approval in DWP --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11550_DWP_Customer_Driver", dwpDriver);
        context.setAttribute("DRSQA_11550_DWP_Customer_Product", Product.Helix_DWP);

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(serviceReqTicketId);
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
        dwpFeatureMethod.captureScreenshot("DRSQA_11550_CaseWaitingApproval", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 11: Approver approves the request in DWP --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11550_DWP_Approver_Driver", dwpDriver);
        context.setAttribute("DRSQA_11550_DWP_Approver_Product", Product.Helix_DWP);

        Map<Object, String> approverUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "pbunyon");
        login(approverUserData.get("UserName"), approverUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.verifyApprovalStatus(serviceReqTicketId, dwpTestData.get("WaitingYourApprovalStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11550_VerifyRequestStatusBeforeApproval", SolQAConstants.appDWP);
        dwpFeatureMethod.openSrmRequestByRequestID(serviceReqTicketId);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.APPROVED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11550_VerifyRequestStatusAfterApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 12: Customer sees request Initiated after approval --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11550_DWP_Customer_PostApproval_Driver", dwpDriver);
        context.setAttribute("DRSQA_11550_DWP_Customer_PostApproval_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(serviceReqTicketId);
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.INITIATED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11550_RequestInitiated", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 13-14: SmartIT shows Work Order Assigned after approval --------------------");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11550_SmartIT_PostApproval_Driver", smartITDriver);
        context.setAttribute("DRSQA_11550_SmartIT_PostApproval_Product", Product.SmartIT);
        Map<Object, String> smartITEndUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITEndUserData.get("UserName"), smartITEndUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(smartITTestData.get("TicketType"), serviceReqTicketId);
        String workOrderCreated = smartITFeatureMethodsPage.getWorkOrderIDFromFulfillmentSection();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11550_WorkOrderInSmartIT", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("-------------------- Step 15: Verify Work Order status in ITSM --------------------");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11550_ITSM_Driver", itsmDriver);
        context.setAttribute("DRSQA_11550_ITSM_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
        itsmFeatureMethods.searchWorkOrder(workOrderCreated);
        itsmFeatureMethods.verifyWorkOrderStatus(SolQAConstants.ASSIGNED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11550_WorkOrderInITSM", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();

        log.info("********End ** DRSQA_11550_Allow_SD_agentsToSubmit_SRD_smartRecorder *******************");
    }

    @Test(description = "DWP Service Broker catalog items in Smart Recorder to submit SRD service requests", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11548", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_11548_Verify_DWPServiceBroker_Submit_Service_smartRecorder(ITestContext context) throws Exception {

        log.info("********Start ** DRSQA_11548_Verify_DWPServiceBroker_Submit_Service_smartRecorder *******************");

        log.info("-------------------- Step 1-7: Create SRD from Smart Recorder and capture generated Work Order --------------------");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11548_SmartIT_Driver", smartITDriver);
        context.setAttribute("DRSQA_11548_SmartIT_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11548_createServiceReqInSmartRecorder");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11548_Data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11548_service");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethodsPage.navigateToSmartRecorder();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11548_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.selectCustomerOrAssetInSmartRecorder(dwpUserData.get("UserName"));
        smartITFeatureMethodsPage.searchAndSelectTemplateInSmartRecorder(dwpcTestData.get("serviceName"), false);
        smartITFeatureMethodsPage.clickCreateServiceRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11548_creatingServicerequest", SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.fillTextFieldByLabelInSmartRecorderDWPWidget(dwpTestData.get("RoomQuestionLabel"), smartITTestData.get("RoomName"));
        smartITFeatureMethodsPage.clickSubmitRequestButton();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11548_serviceRequestCreated", SolQAConstants.appSmartIT);

        // Work Order ID should appear in the fulfillment section once fulfillment is created
        String workOrderCreated = smartITFeatureMethodsPage.getWorkOrderIDFromFulfillmentSection();
        Assert.assertTrue(workOrderCreated != null && !workOrderCreated.trim().isEmpty(), "Work order Id is not shown on SmartIT View page");
        log.info("DRSQA_11548 WorkOrder id : " + workOrderCreated);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("-------------------- Step 8: Verify in ITSM that Work Order is Assigned --------------------");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11548_ITSM_Driver", itsmDriver);
        context.setAttribute("DRSQA_11548_ITSM_Product", Product.Helix_ITSM);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
        itsmFeatureMethods.searchWorkOrder(workOrderCreated);
        itsmFeatureMethods.verifyWorkOrderStatus(SolQAConstants.ASSIGNED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11548_WorkOrderOpened", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();

        log.info("-------------------- Step 9-14: In SmartIT, assignee changes Work Order status to Completed --------------------");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11548_SmartIT_Assignee_Driver", smartITDriver);
        context.setAttribute("DRSQA_11548_SmartIT_Assignee_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethodsPage.applyFilterAndOpenTicket("Work Order", workOrderCreated);
        smartITFeatureMethodsPage.clickAssignToMeInWorkorderViewPageIfPresent();
        smartITFeatureMethodsPage.verifyWorkorderStatus(SolQAConstants.ASSIGNED);

        smartITFeatureMethodsPage.updateWorkorderStatus(SolQAConstants.PENDING);
        smartITFeatureMethodsPage.saveWorkorder();
        smartITFeatureMethodsPage.verifyWorkorderStatus(SolQAConstants.PENDING);

        smartITFeatureMethodsPage.updateWorkorderStatus(SolQAConstants.PLANNING);
        smartITFeatureMethodsPage.saveWorkorder();
        smartITFeatureMethodsPage.verifyWorkorderStatus(SolQAConstants.PLANNING);

        smartITFeatureMethodsPage.updateWorkorderStatus(SolQAConstants.IN_PROGRESS);
        smartITFeatureMethodsPage.saveWorkorder();
        smartITFeatureMethodsPage.verifyWorkorderStatus(SolQAConstants.IN_PROGRESS);

        smartITFeatureMethodsPage.updateWorkorderStatus(SolQAConstants.COMPLETED_STATUS);
        smartITFeatureMethodsPage.saveWorkorder();
        smartITFeatureMethodsPage.verifyWorkorderStatus(SolQAConstants.COMPLETED_STATUS);

        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11548_WorkOrderCompleted", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("********End ** DRSQA_11548_Verify_DWPServiceBroker_Submit_Service_smartRecorder *******************");
    }

    @Test(description = "Verify User Cancel/Reopen functionality for SBE Request", groups = {"Regression_P1", "DWP", "ITSM", "SmartIT", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-16523", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_16523_VerifyUserCancelReopenFunctionalityForSBERequest(ITestContext context) throws Exception {
        log.info("--------- DRSQA-16523: VerifyUserCancelReopenFunctionalityForSBERequest *** START ---------");

        try {
        log.info("--------- Precondition Step1: Configure Reopen Request for SB_CreateIncident service ---------");
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_16523_DWPC_Driver", dwpcDriver);

        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_16523_Data");
        log.info("Login to DWPC with " + dwpcUserData.get("UserName"));
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();

        log.info("Open Services page and configure Reopen Request");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.verifyServicesPageLaunchedSuccessfully();
        dwpcFeatureMethods.configureServiceOption(
                dwpcTestData.get("serviceName"),
                SolQAConstants.DWPC_SECTION_REOPEN_REQUEST,
                SolQAConstants.DWPC_REOPEN_ASSOCIATED_ACTIVITIES);

        if (dwpcDriver != null) {
            dwpcDriver.quit();
            log.info("Closed DWPC browser");
        }	
        
        log.info("Precondition Step2:- Log in to ITSM and go to centralized configuration and enable the enableSharedTicketConsole");
        
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_16523_Driver", getDriver());
		context.setAttribute("DRSQA_16523_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();		
        
        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
		String[] componentMenu = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
		itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.TRUE);
        
        log.info("--------- Precondition Completed ---------");

        log.info("Step 1: Login to DWP Application with Fritz user");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_16523_Driver", getDriver());
        context.setAttribute("DRSQA_16523_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_16523_Data");

        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        log.info("Step 2: Submit a Service Request - SRD: SB_CreateIncident");
        String SERVICE_NAME = dwpTestData.get("serviceName");
        log.info("Open Catalog Page");
        dwpFeatureMethod.openCatalogPage();

        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        } else {
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
        }

        log.info("Request a service: " + SERVICE_NAME);
        dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_ServiceRequestSubmitted", SolQAConstants.appDWP);

        String submittedRequestID = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        log.info("Submitted Request ID: " + submittedRequestID);
        Assert.assertFalse(submittedRequestID.isEmpty(), "Service Request is not created");
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_ServiceRequestCreated", SolQAConstants.appDWP);
 
        log.info("Step 3: Login to SmartIT Application with pbunyon user");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_16523_SmartIT_Driver", smartITDriver);
        context.setAttribute("DRSQA_16523_SmartIT_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16523_Data");
        log.info("Login to SmartIT with " + smartITUserData.get("UserName"));
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        log.info("Step 4: Click on search, select Request dropdown and search the SBE request ID - Open the Request ID(SBE)");
        smartITFeatureMethodsPage.searchAndOpenRequest(submittedRequestID, smartITTestData.get("DropdownOptionRequest"));
        log.info("SBE Request " + submittedRequestID + " opened successfully in SmartIT");

        log.info("Step 5: Open related incident in new tab and validate");
        String incidentID = smartITFeatureMethodsPage.openRelatedIncidentInNewTab();
        String fetchedIncidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
        Assert.assertTrue(fetchedIncidentID.contains(incidentID), "Incident ID mismatch. Expected: " + incidentID + ", Actual: " + fetchedIncidentID);
        log.info("Incident " + fetchedIncidentID + " validated successfully in new tab");

        log.info("Step 6: Change the incident status from Assigned to In Progress");
        smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("InProgressStatus"), "", "");
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.verifyStatusViewPage(smartITTestData.get("InProgressStatus"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16523_statusChangedToInProgress", SolQAConstants.appSmartIT);
        log.info("Incident status changed to In Progress successfully");

        log.info("Step 7: Click on Tasks tab and close all tasks one by one");
        smartITFeatureMethodsPage.closeAllTasks(smartITTestData.get("ClosedStatus"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16523_allTasksClosed", SolQAConstants.appSmartIT);
        log.info("All tasks closed successfully");

        log.info("Step 8: Update incident status to Resolved");
        smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("ResolvedStatus"), smartITTestData.get("StatusReason"), smartITTestData.get("ResolutionNote"));
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.verifyStatusViewPage(smartITTestData.get("ResolvedStatus"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16523_statusChangedToResolved", SolQAConstants.appSmartIT);
        log.info("Incident status changed to Resolved successfully");

        log.info("Step 9: Switch to DWP driver, open request and click Request Again");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.clickDetailsForPastRequest(submittedRequestID);
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_RequestDetailsPage", SolQAConstants.appDWP);
        dwpFeatureMethod.clickReopenButton();
        log.info("Reopen reason: " +  dwpTestData.get("ReopenReason"));
        dwpFeatureMethod.enterReopenReasonAndConfirm(dwpTestData.get("ReopenReason"));
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_RequestResubmitted", SolQAConstants.appDWP);
        log.info("Request reopened and submitted successfully in DWP");

        log.info("Step 10: Switch to SmartIT driver, refresh and verify Activity log");
        setDriver(smartITDriver);
        smartITFeatureMethodsPage.refreshPage();
        smartITFeatureMethodsPage.verifyActivityNotes(smartITTestData.get("ActivityLogAssigned"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16523_ActivityLogVerified", SolQAConstants.appSmartIT);
        log.info("Activity log verified: Status marked as Assigned by Fritz Schulz");

        log.info("Step 11: Switch to DWP driver, open active request and cancel it");
        setDriver(dwpDriver);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.clickDetailsForActiveRequest(submittedRequestID);
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_ActiveRequestDetails", SolQAConstants.appDWP);
        dwpFeatureMethod.clickCancelButton();
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_CancelConfirmationPopup", SolQAConstants.appDWP);
        dwpFeatureMethod.confirmCancelRequest();
        dwpFeatureMethod.verifyCancelledStatus();
        dwpFeatureMethod.captureScreenshot("DRSQA_16523_RequestCancelled", SolQAConstants.appDWP);
        log.info("Request cancelled successfully in DWP");

        log.info("Step 12: Switch to SmartIT driver, refresh and verify Activity log for Cancelled status");
        setDriver(smartITDriver);
        smartITFeatureMethodsPage.refreshPage();
        smartITFeatureMethodsPage.verifyActivityNotes(smartITTestData.get("ActivityLogCancelled"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16523_ActivityLogCancelledVerified", SolQAConstants.appSmartIT);
        log.info("Activity log verified: Status marked as Cancelled by Fritz Schulz");

        log.info("Step 13: Login to ITSM, search incident and verify Activity log");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_16523_ITSM_Driver", itsmDriver);
        context.setAttribute("DRSQA_16523_ITSM_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        log.info("Logged in to ITSM with " + itsmUserData.get("UserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();
        
        itsmFeatureMethods.searchIncident(incidentID);
        itsmFeatureMethods.captureScreenshot("DRSQA_16523_ITSMIncidentOpened", SolQAConstants.appITSM);
        Assert.assertTrue(itsmFeatureMethods.verifyIncidentActivityLog(smartITTestData.get("ActivityLogCancelled")),
                "Activity log does not contain expected entry: " + smartITTestData.get("ActivityLogCancelled"));
        itsmFeatureMethods.captureScreenshot("DRSQA_16523_ITSMActivityLogVerified", SolQAConstants.appITSM);
        log.info("ITSM Activity log verified: Status marked as Cancelled by Fritz Schulz");
        }
        
        finally{   
        
        	log.info("Step 14: Disable the shared ticket console settings");
	       itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	       setDriver(itsmDriver);
	   
	       login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	       log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
	       itsmFeatureMethods = new ITSMFeatureMethods();
	    
	       itsmFeatureMethods.navigateToCentralizedConfiguration();
	       String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	       itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
	       itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
	       itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
	       
    }

        log.pass("--------- DRSQA-16523: VerifyUserCancelReopenFunctionalityForSBERequest *** END ---------");
    }

    @Test(description = "Verify User Cancel/Reopen functionality for REQ Request", groups = {"Regression_P1", "DWP", "SmartIT", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-16205", Products = {Product.Helix_DWP, Product.SmartIT})
    public void DRSQA_16205_VerifyUserCancelReopenFunctionalityForREQRequest(ITestContext context) throws Exception {
        log.info("--------- DRSQA-16205: VerifyUserCancelReopenFunctionalityForREQRequest *** START ---------");

        try {
        log.info("--------- Precondition Step 1: Configure Reopen Request for Chatter Login Issue SRD service ---------");
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_16205_DWPC_Driver", dwpcDriver);

        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_16205_Data");
        log.info("Login to DWPC with " + dwpcUserData.get("UserName"));
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();

        log.info("Open Services page and configure Reopen Request");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.verifyServicesPageLaunchedSuccessfully();
        dwpcFeatureMethods.configureServiceOption(
                dwpcTestData.get("serviceName"),
                SolQAConstants.DWPC_SECTION_REOPEN_REQUEST,
                SolQAConstants.DWPC_REOPEN_ASSOCIATED_ACTIVITIES);
        log.info("--------- Precondition Completed ---------");

        if (dwpcDriver != null) {
            dwpcDriver.quit();
            log.info("Closed DWPC browser");
        }
        
        log.info("Precondition Step2:- Log in to ITSM and go to centralized configuration and enable the enableSharedTicketConsole");
        
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_16205_Driver", getDriver());
		context.setAttribute("DRSQA_16205_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();		
        
        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
		String[] componentMenu = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
		itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.TRUE);
        
        log.info("--------- Precondition Completed ---------");

        log.info("Step 1: Login to DWP Application with Fritz user");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_16205_Driver", getDriver());
        context.setAttribute("DRSQA_16205_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_16205_Data");

        log.info("Login to DWP with " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        log.info("Step 2: Submit a Service Request - SRD: " + dwpcTestData.get("serviceName"));
        String SERVICE_NAME = dwpTestData.get("serviceName");
        log.info("Open Catalog Page");
        dwpFeatureMethod.openCatalogPage();

        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        } else {
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
        }

        log.info("Request a service: " + SERVICE_NAME);
        dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("problemField"), dwpTestData.get("problemValue"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_ServiceRequestSubmitted", SolQAConstants.appDWP);

        String submittedRequestID = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        log.info("Submitted Request ID: " + submittedRequestID);
        Assert.assertFalse(submittedRequestID.isEmpty(), "Service Request is not created");
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_ServiceRequestCreated", SolQAConstants.appDWP);

        log.info("Step 3: Login to SmartIT Application with pbunyon user");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_16205_SmartIT_Driver", smartITDriver);
        context.setAttribute("DRSQA_16205_SmartIT_Product", Product.SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16205_Data");
        log.info("Login to SmartIT with " + smartITUserData.get("UserName"));
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();

        log.info("Step 4: Click on search, select Request dropdown and search the REQ request ID - Open the Request ID(REQ)");
        smartITFeatureMethodsPage.searchAndOpenRequest(submittedRequestID, smartITTestData.get("requestDropdownOptionAll"));
        log.info("REQ Request " + submittedRequestID + " opened successfully in SmartIT");

        log.info("Step 5: From REQ open incident in another tab");
        String incidentID = smartITFeatureMethodsPage.openRelatedIncidentInNewTab();
        String fetchedIncidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
        Assert.assertTrue(fetchedIncidentID.contains(incidentID), "Incident ID mismatch. Expected: " + incidentID + ", Actual: " + fetchedIncidentID);
        log.info("Incident " + fetchedIncidentID + " opened successfully in new tab");

        log.info("Step 6: Change the incident status Assigned -> In Progress -> Resolved");
        smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("InProgressStatus"), "", "");
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16205_statusChangedToInProgress", SolQAConstants.appSmartIT);
        smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("ResolvedStatus"), smartITTestData.get("StatusReason"), smartITTestData.get("ResolutionNote"));
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.verifyStatusViewPage(smartITTestData.get("ResolvedStatus"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16205_statusChangedToResolved", SolQAConstants.appSmartIT);
        log.info("Incident status changed to Resolved successfully");

        log.info("Step 7: Switch to DWP driver, open request and click Reopen");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.clickDetailsForPastRequest(submittedRequestID);
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_RequestDetailsPage", SolQAConstants.appDWP);
        dwpFeatureMethod.clickReopenButton();
        dwpFeatureMethod.enterReopenReasonAndConfirm(dwpTestData.get("ReopenReason"));
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_RequestReopened", SolQAConstants.appDWP);
        log.info("Request reopened successfully in DWP");

        log.info("Step 8: Back to SmartIT and verify the Activity log for Request");
        setDriver(smartITDriver);
        // Switch back to the first tab (Request page opened in Step 4)
        new util.CommonWaits().switchToTabByIndex(smartITDriver, 0);
        smartITFeatureMethodsPage.refreshPage();
        smartITFeatureMethodsPage.verifyRequestActivityNotes(smartITTestData.get("ActivityLogInProgress"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16205_ActivityLogInProgressVerified", SolQAConstants.appSmartIT);
        log.info("Activity log verified: Status marked as In Progress by Fritz Schulz");

        log.info("Step 9: Now Cancel the request ID from DWP");
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.clickDetailsForActiveRequest(submittedRequestID);
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_ActiveRequestDetails", SolQAConstants.appDWP);
        dwpFeatureMethod.clickCancelButton();
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_CancelConfirmationPopup", SolQAConstants.appDWP);
        dwpFeatureMethod.confirmCancelRequest();
        dwpFeatureMethod.verifyCancelledStatus();
        dwpFeatureMethod.captureScreenshot("DRSQA_16205_RequestCancelled", SolQAConstants.appDWP);
        log.info("Request cancelled successfully in DWP");

        log.info("Step 10: Back to SmartIT and Verify the Activity log for Request");
        setDriver(smartITDriver);
        // Switch back to the first tab (Request page)
        new util.CommonWaits().switchToTabByIndex(smartITDriver, 0);
        smartITFeatureMethodsPage.refreshPage();
        smartITFeatureMethodsPage.verifyRequestActivityNotes(smartITTestData.get("ActivityLogCancelled"));
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16205_ActivityLogCancelledVerified", SolQAConstants.appSmartIT);
        log.info("Activity log verified: Status marked as Cancelled by Fritz Schulz");
        }
        
        finally{   
            
        	log.info("Step 14: Disable the shared ticket console settings");
	       itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	       setDriver(itsmDriver);
	   
	       login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	       log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
	       itsmFeatureMethods = new ITSMFeatureMethods();
	    
	       itsmFeatureMethods.navigateToCentralizedConfiguration();
	       String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	       itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
	       itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
	       itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
	       
    }

        log.pass("--------- DRSQA-16205: VerifyUserCancelReopenFunctionalityForREQRequest *** END ---------");
    }

    @Test(description = "Delegate Service request (Imported SRD) to other users as Collaborators", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "407", JiraID = "DRSQA-11475", Products = {Product.Helix_BWF, Product.Helix_DWP, Product.Helix_ITSM})
    public void DRSQA_11475_Delegate_Service_request_to_other_users_as_Collaborators(ITestContext context) throws Exception {

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11475_RequestDetails");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITTestData, "DRSQA_11475_WorkOrderNotes");

        String serviceName = dwpTestData.get("ServiceRequestName");
        String whatRoomLabel = dwpTestData.get("WhatRoomLabel");
        String whatRoomValue = dwpTestData.get("WhatRoomValue");

        String collaborator1FullName = dwpTestData.get("COLLABORATOR_1_FULLNAME");
        String collaborator2FullName = dwpTestData.get("COLLABORATOR_2_FULLNAME");
        String collaborator3FullName = dwpTestData.get("COLLABORATOR_3_FULLNAME");
        String group1Name = dwpTestData.get("GROUP_1");

        Map<Object, String> requesterData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("RequesterUserKey"));
        Map<Object, String> collaborator1Data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("Collaborator1UserKey"));
        Map<Object, String> collaborator2Data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("Collaborator2UserKey"));
        Map<Object, String> collaborator3Data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("Collaborator3UserKey"));
        Map<Object, String> groupMemberData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("GroupMemberUserKey"));
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", smartITTestData.get("SmartITUserKey"));

        String comment1 = dwpTestData.get("COMMENT_1");
        String fileName1 = dwpTestData.get("FileName_1");
        File file1 = CommonMethods.createTextFile(fileName1);

        String comment2 = smartITTestData.get("Comment_2");
        String fileName2 = smartITTestData.get("FileName_2");
        File file2 = CommonMethods.createTextFile(fileName2);

        String comment3 = smartITTestData.get("Comment_3");
        String fileName3 = smartITTestData.get("FileName_3");
        File file3 = CommonMethods.createTextFile(fileName3);

        String comment4 = dwpTestData.get("COMMENT_4");
        String fileName4 = dwpTestData.get("FileName_4");
        File file4 = CommonMethods.createTextFile(fileName4);

        String comment5 = dwpTestData.get("COMMENT_5");
        String fileName5 = dwpTestData.get("FileName_5");
        File file5 = CommonMethods.createTextFile(fileName5);

        String comment6 = dwpTestData.get("COMMENT_6");
        String fileName6 = dwpTestData.get("FileName_6");
        File file6 = CommonMethods.createTextFile(fileName6);

        String comment7 = dwpTestData.get("COMMENT_7");
        String fileName7 = dwpTestData.get("FileName_7");
        File file7 = CommonMethods.createTextFile(fileName7);

        log.info("Step 1: Submit SR + add collaborators + add comment with attachment");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.clickOnRequestNow(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(whatRoomLabel, whatRoomValue);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_RequestSubmitted", SolQAConstants.appDWP);

        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Request ID is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifySrdTitle(serviceName);

        String workOrderId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(workOrderId != null && !workOrderId.trim().isEmpty(), "Work Order ID is not created");

        // NOTE: Umbrella creates a People Group via DWPC APIs; SolutionsQA assumes `GROUP_1` exists and contains the intended member.
        dwpFeatureMethod.addRemoveCollaborators_DWP(collaborator1FullName);
        dwpFeatureMethod.verifySelectedCollaboratorInDetailsPage(collaborator1FullName);
        dwpFeatureMethod.addRemoveCollaborators_DWP(collaborator2FullName);
        dwpFeatureMethod.verifySelectedCollaboratorInDetailsPage(collaborator2FullName);
        dwpFeatureMethod.addRemoveCollaborators_DWP(group1Name);
        dwpFeatureMethod.verifySelectedCollaboratorInDetailsPage(group1Name);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment1, file1);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_1_VerifyServiceRequestDetails", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 2: Verify Work Order in SmartIT + add public/private notes with attachments");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11475_Driver", smartITDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_SmartIT);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.applyFilterAndOpenTicket(smartITTestData.get("TicketType"), workOrderId);

        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(comment1), "DWP comment is not present in Work Order activity");
        Assert.assertNotNull(file1);
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(file1.getName()), "DWP attachment is not present in Work Order activity");

        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(comment2, file2, true);
        Assert.assertTrue(smartITFeatureMethodsPage.isActivityNoteAppearedUnderActivityTab_simpleText(comment2), "Public note is not present in Work Order activity");
        Assert.assertNotNull(file2);
        Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(file2.getName()), "Public note attachment is not present in Work Order activity");

        smartITFeatureMethodsPage.addAttachmentNote_InActivityFeed(comment3, file3, false);
        smartITFeatureMethodsPage.closeBrowser();

        log.info("Step 3: Requester verifies notes/attachments in DWP");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_3_VerifyRequesterDetails", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 4: Collaborator 1 adds comment with attachment");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator1Data.get("UserName"), collaborator1Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment4, file4);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_4_VerifyCollaborator1Details", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 5: Collaborator 2 adds comment with attachment");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator2Data.get("UserName"), collaborator2Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment5, file5);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.verifyAttachments(comment5, file5);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_5_VerifyCollaborator2Details", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 6: Group member verifies request details + adds comment with attachment");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(groupMemberData.get("UserName"), groupMemberData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment6, file6);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.verifyAttachments(comment5, file5);
        dwpFeatureMethod.verifyAttachments(comment6, file6);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_6_VerifyGroupMemberDetails", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 7: Requester removes collaborator 2 + adds collaborator 3 + removes group");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.addRemoveCollaborators_DWP(collaborator2FullName);
        dwpFeatureMethod.verifyCollaboratorNotPresentInDetailsPage(collaborator2FullName);
        dwpFeatureMethod.addRemoveCollaborators_DWP(collaborator3FullName);
        dwpFeatureMethod.verifySelectedCollaboratorInDetailsPage(collaborator3FullName);
        dwpFeatureMethod.addRemoveCollaborators_DWP(group1Name);
        dwpFeatureMethod.verifyCollaboratorNotPresentInDetailsPage(group1Name);
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.verifyAttachments(comment5, file5);
        dwpFeatureMethod.verifyAttachments(comment6, file6);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_7_VerifyAfterCollaboratorUpdates", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 8: Removed collaborator 2 should not be able to see the request");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator2Data.get("UserName"), collaborator2Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.searchInGlobalSearch(requestId, "Requests");
        dwpFeatureMethod.verifyItemFound(requestId, false);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 9: Group member should not be able to see the request after group removal");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(groupMemberData.get("UserName"), groupMemberData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.searchInGlobalSearch(requestId, "Requests");
        dwpFeatureMethod.verifyItemFound(requestId, false);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 10: Collaborator 3 adds comment with attachment");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator3Data.get("UserName"), collaborator3Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment7, file7);
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.verifyAttachments(comment5, file5);
        dwpFeatureMethod.verifyAttachments(comment6, file6);
        dwpFeatureMethod.verifyAttachments(comment7, file7);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_10_VerifyCollaborator3Details", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 11: Collaborator 1 should not be able to request again");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator1Data.get("UserName"), collaborator1Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        Assert.assertFalse(dwpFeatureMethod.isRequestAgainButtonPresent(), "Request Again button is seen present");
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_11_VerifyNoRequestAgain", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 12: Collaborator 3 cancels the request");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator3Data.get("UserName"), collaborator3Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.cancelServiceRequest();
        dwpFeatureMethod.verifyCancelledStatus();
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_12_VerifyCancelledByCollaborator3", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 13: Requester verifies request in past events");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId);
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.verifyAttachments(comment5, file5);
        dwpFeatureMethod.verifyAttachments(comment6, file6);
        dwpFeatureMethod.verifyAttachments(comment7, file7);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_13_VerifyRequesterPastEvents", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 14: Collaborator 3 verifies request in past events");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11475_Driver", dwpDriver);
        context.setAttribute("DRSQA_11475_Product", Product.Helix_DWP);
        login(collaborator3Data.get("UserName"), collaborator3Data.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId);
        dwpFeatureMethod.verifyAttachments(comment1, file1);
        dwpFeatureMethod.verifyAttachments(comment2, file2);
        dwpFeatureMethod.verifyAttachments(comment4, file4);
        dwpFeatureMethod.verifyAttachments(comment5, file5);
        dwpFeatureMethod.verifyAttachments(comment6, file6);
        dwpFeatureMethod.verifyAttachments(comment7, file7);
        dwpFeatureMethod.captureScreenshot("DRSQA_11475_Step_14_VerifyCollaborator3PastEvents", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllDrivers() {
        WebDriver[] drivers = {smartITDriver, dwpDriver, itsmDriver, bwfDriver1, bwfDriver2, getDriver()};
        String[] driverNames = {"smartITDriver", "dwpDriver", "itsmDriver", "bwfDriver1", "bwfDriver2", "getDriver"};

        for (int i = 0; i < drivers.length; i++) {
            if (drivers[i] != null) {
                try {
                    drivers[i].quit();
                    log.info("Closed " + driverNames[i] + " browser.");
                } catch (Exception e) {
                    log.warn("Exception while closing " + driverNames[i] + ": " + e.getMessage());
                } finally {
                    drivers[i] = null; // avoid dangling references
                }
            }
        }

        try {
            WebDriver mainDriver = getDriver();
            if (mainDriver != null) {
                mainDriver.quit();
                log.info("Closed getDriver browser.");
            }
        } catch (Exception e) {
            log.warn("Exception while closing getDriver: " + e.getMessage());
        }
    }

}