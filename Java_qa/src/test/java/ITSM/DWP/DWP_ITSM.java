package ITSM.DWP;

import framework.Launcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class DWP_ITSM extends Launcher {

    static SolqaLogger log = new SolqaLogger(DWP_ITSM.class.getName());
    DWPFeatureMethods dwpFeatureMethod, dwpFeatureMethod2;
    DWPCFeatureMethods dwpcFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethod;
    BWFFeatureMethods bwfFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    ReadJson readJson = new ReadJson();
    Map<Object, String> dwpUserData, dwpTestData, itsmUserData, dwpcTestData, itsmTestData, smartITUserData, bwfUserData;
    WebDriver dwpDriver, itsmDriver, dwpcDriver, smartItDriver, bwfDriver;

    @Test(description = "Knowledge article (RKM) operations in DWP", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11630", Products = {Product.Helix_DWP})
    public void DRSQA_11630_KnowledgeArticleOperations_WithPetramcoData(ITestContext context) {
        log.info("***************** START: DRSQA-11630 Knowledge article (RKM) operations in DWP *****************");

        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        context.setAttribute("DRSQA_11630_Driver", dwpDriver);
        context.setAttribute("DRSQA_11630_Product", Product.Helix_DWP);
        setDriver(dwpDriver);
        dwpFeatureMethod = new DWPFeatureMethods();
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu("Configuration");
        dwpFeatureMethod.enableDisableComAroundPluggableProvider(false);
        dwpFeatureMethod.closeBrowser();

        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        context.setAttribute("DRSQA_11630_Driver", dwpDriver);
        context.setAttribute("DRSQA_11630_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "nishaUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11630_KnowledgeArticleSearch");

        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        log.info("Search an article in the search bar article = " + dwpTestData.get("BWF_ArticleName"));
        dwpFeatureMethod.searchItemInGlobalSearch(dwpTestData.get("PaperJamToSearch"), "Articles");
        dwpFeatureMethod.verifySimilarArticleIsAvailable(dwpTestData.get("SimilarArticleOfPaperJam"));
        dwpFeatureMethod.giveFeedbackOnArticle("Good: Usable but needs improvement", "DRSQA_11630_FeedbackOnArticle");
        dwpFeatureMethod.closeBrowser();
        log.info("Close the browser");

        this.itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        context.setAttribute("DRSQA_11630_Driver", itsmDriver);
        context.setAttribute("DRSQA_11630_Product", Product.Helix_ITSM);
        setDriver(itsmDriver);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethod = new ITSMFeatureMethods();
        itsmFeatureMethod.verifyITSMLogin();
        itsmFeatureMethod.navigateMenu("Knowledge Management", "Search Knowledge");
        itsmFeatureMethod.searchAndOpenArticle(dwpTestData.get("KnowledgeID"));
        itsmFeatureMethod.verifyFeedBackIsAvailable("DRSQA_11630_FeedbackOnArticle");
        itsmFeatureMethod.closeBrowser();
        log.info("***************** END: DRSQA-11630 Knowledge article (RKM) operations in DWP *****************");
    }

    @Test(description = "[DWP][Catalog] Catalog Approval triggers WO creation", groups = {"Regression_P1"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11518", Products = {Product.Helix_DWP, Product.Helix_ITSM})
    public void DRSQA_11518_submitSimpleRequestSRD(ITestContext context) throws Exception {

        log.info("-----------------------DRSQA_11518_Catalog Approval triggers WO creation-------------------");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11518_Driver", dwpDriver);
        context.setAttribute("DRSQA_11518_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11518_Data");
        log.info("Step 1: Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        log.info("Step 2: Verify DWP Home page is opened");
        dwpFeatureMethod.verifyDWPHomePageOpened();

        String serviceName = dwpTestData.get("serviceName");
        log.info("Step 3:Searching for service: " + serviceName + " and submitting a request for that service");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        log.info("Step 4: Filling out the service request form and submitting the request");

        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("summaryLabel"), dwpTestData.get("summaryValue"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11518_requestSubmitted", SolQAConstants.appDWP);

        log.info("Step 5: Getting the request ID of the submitted request");
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "SR is not created");
        log.info("DRSQA_11518: SR created ID: " + requestId);
        dwpFeatureMethod.closeBrowser();

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "friedaUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11518_Driver", dwpDriver);
        context.setAttribute("DRSQA_11518_Product", Product.Helix_DWP);

        log.info("Step 6: Logging in as approver: " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        log.info("Step 7: Opening activity page and approving the request with ID: " + requestId);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        log.info("Approving the request");
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.captureScreenshot("DRSQA_11518_requestApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11518_Driver", dwpDriver);
        context.setAttribute("DRSQA_11518_Product", Product.Helix_DWP);

        log.info("Step 8: Logging back in as requester: " + dwpUserData.get("UserName") + " to verify fulfillment/work order created for the approved request");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);

        String fulfillmentId = dwpFeatureMethod.getFulfillmentID();

        Assert.assertTrue(fulfillmentId != null && !fulfillmentId.trim().isEmpty(),
                "Fulfillment ID (Work Order ID) is not available for request: " + requestId);
        log.info("DRSQA_11518: Fulfillment/Work Order ID: " + fulfillmentId);
        dwpFeatureMethod.captureScreenshot("DRSQA_11518_fulfillmentId", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11518_Driver", itsmDriver);
        context.setAttribute("DRSQA_11518_Product", Product.Helix_ITSM);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        log.info("Step 9: Logging in to ITSM with " + itsmUserData.get("UserName") + " to verify the work order created for the approved request is visible in ITSM");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethod = new ITSMFeatureMethods();
        itsmFeatureMethod.verifyITSMLogin();

        log.info("Step 10:Navigating to Search Work Order page and searching for Work Order with ID: " + fulfillmentId);
        itsmFeatureMethod.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
        itsmFeatureMethod.searchWorkOrder(fulfillmentId);
        itsmFeatureMethod.captureScreenshot("DRSQA_11518_ITSM_WorkOrderFound", SolQAConstants.appITSM);
        itsmFeatureMethod.closeBrowser();

        log.info("----------------------- END: DRSQA_11518_Catalog Approval triggers WO creation-------------------");
    }

    @Test(description = "Approver is able to Approve/Reject/OnHold request with password (with RSSO)", groups = {"Regression_P2", "hpareek"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11478", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.Helix_DWPC})
    public void DRSQA_11478_submitApprovalSRDGroupOneSign(ITestContext context) throws Exception {

        log.info("-----------------------DRSQA-11478 Approver is able to Approve/Reject/OnHold request with password (with RSSO)-------------------");

        log.info("Reading test data for DRSQA_11478");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11478_Data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11478_service");

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11478_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11478_Product", Product.Helix_DWPC);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("serviceName"), dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(dwpcTestData.get("serviceName")), "Service is not present/published in DWPC: " + dwpcTestData.get("serviceName"));
        dwpcFeatureMethods.closeBrowser();

        log.info("-------------------- Submit 3 requests as requester --------------------");
        log.info("Preparing to submit 3 requests as requester (all for the same service)");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        String serviceName = dwpTestData.get("ServiceRequestName");

        // --- Submit first request ---
        log.info("==================== [1/3] START submitting first service request for '" + serviceName + "' ====================");
        // Submit first request
        log.info("Launching DWP and logging in as requester: " + dwpUserData.get("UserName"));
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11478_Driver", dwpDriver);
        context.setAttribute("DRSQA_11478_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        String dateTime = new SimpleDateFormat("MMM d, yyyy h:mm a").format(new Date());
        dwpFeatureMethod.fillTextDateFieldByLabel(dwpTestData.get("DateTimeLabel"), dateTime);
        dwpFeatureMethod.fillTypeAheadDropdownField(dwpTestData.get("PersonLabel"), dwpUserData.get("FullName"));
        log.info("Submitting first service request");
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11478_requestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        log.info("First request submitted. Request ID: " + requestId1);
        Assert.assertFalse(requestId1.isEmpty(), "Service Request is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("WaitingApprovalStatus"));
        log.info("==================== [1/3] END submitting first service request. Request ID: " + requestId1 + " ====================");
        // --- Submit second request ---
        log.info("==================== [2/3] START submitting second service request for '" + serviceName + "' ====================");
        // Submit second request
        log.info("Submitting second service request");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dateTime = new SimpleDateFormat("MMM d, yyyy h:mm a").format(new Date());
        dwpFeatureMethod.fillTextDateFieldByLabel(dwpTestData.get("DateTimeLabel"), dateTime);
        dwpFeatureMethod.fillTypeAheadDropdownField(dwpTestData.get("PersonLabel"), dwpUserData.get("FullName"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11478_requestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        log.info("Second request submitted. Request ID: " + requestId2);
        Assert.assertFalse(requestId2.isEmpty(), "Service Request is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("WaitingApprovalStatus"));
        log.info("==================== [2/3] END submitting second service request. Request ID: " + requestId2 + " ====================");
        // --- Submit third request ---
        log.info("==================== [3/3] START submitting third service request for '" + serviceName + "' ====================");
        // Submit third request
        log.info("Submitting third service request");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dateTime = new SimpleDateFormat("MMM d, yyyy h:mm a").format(new Date());
        dwpFeatureMethod.fillTextDateFieldByLabel(dwpTestData.get("DateTimeLabel"), dateTime);
        dwpFeatureMethod.fillTypeAheadDropdownField(dwpTestData.get("PersonLabel"), dwpUserData.get("FullName"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11478_requestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId3 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        log.info("Third request submitted. Request ID: " + requestId3);
        Assert.assertFalse(requestId3.isEmpty(), "Service Request is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId3);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("WaitingApprovalStatus"));
        log.info("==================== [3/3] END submitting third service request. Request ID: " + requestId3 + " ====================");
        log.info("All three requests submitted for the same service: '" + serviceName + "'. Proceeding to approval actions.");
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Approver approves / rejects / on-holds requests --------------------");
        log.info("Logging in as approver and processing requests");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "pbunyon");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11478_Driver", dwpDriver);
        context.setAttribute("DRSQA_11478_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        log.info("Approving first request: " + requestId1);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.confirmActionWithPasswordIfPrompted(dwpUserData.get("UserName"), dwpUserData.get("Password"));
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.APPROVED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11478_Request1_Approved", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        log.info("Rejecting second request: " + requestId2);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.clickRejectRequestInEventDetails(dwpTestData.get("RejectReason"), true);
        dwpFeatureMethod.confirmActionWithPasswordIfPrompted(dwpUserData.get("UserName"), dwpUserData.get("Password"));
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.REJECTED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11478_Request2_Rejected", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        log.info("Putting third request on hold: " + requestId3);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId3);
        dwpFeatureMethod.clickOnHoldRequestInEventDetails();
        dwpFeatureMethod.confirmActionWithPasswordIfPrompted(dwpUserData.get("UserName"), dwpUserData.get("Password"));
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("OnHoldStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11478_Request3_OnHold", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Verify Work Order created in ITSM for approved request --------------------");
        log.info("Verifying Work Order creation in ITSM for approved request: " + requestId1);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        String fulfillmentId = dwpFeatureMethod.getFulfillmentID();
        log.info("Fulfillment ID (Work Order ID) for approved request: " + fulfillmentId);
        Assert.assertTrue(fulfillmentId != null && !fulfillmentId.trim().isEmpty(), "Fulfillment ID (Work Order ID) is not available for request: " + requestId1);
        dwpFeatureMethod.closeBrowser();

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11478_Driver", getDriver());
        context.setAttribute("DRSQA_11478_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMUsers, "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethod = new ITSMFeatureMethods();
        itsmFeatureMethod.verifyITSMLogin();
        itsmFeatureMethod.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
        itsmFeatureMethod.searchWorkOrder(fulfillmentId);
        itsmFeatureMethod.captureScreenshot("DRSQA_11478_ITSM_WorkOrderFound", SolQAConstants.appITSM);
        itsmFeatureMethod.closeBrowser();
        log.info("----------------------- END: DRSQA-11478 Approver is able to Approve/Reject/OnHold request with password (with RSSO)-------------------");
    }

    @Test(description = "Reassign Approval - Approval of imported SRD into DWP Catalog requiring password", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11470", Products = {Product.Helix_DWP, Product.Helix_ITSM})
    public void DRSQA_11470_submitSimpleRequestSRD(ITestContext context) throws Exception {

        log.info("-----------------------DRSQA-11470 Reassign Approval - Approval of imported SRD into DWP Catalog requiring password-------------------");

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11470_Data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11470_service");

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11470_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_DWPC);

        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("serviceName"), dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(dwpcTestData.get("serviceName")), "Service is not present/published in DWPC: " + dwpcTestData.get("serviceName"));
        dwpcFeatureMethods.captureScreenshot("DRSQA-11470_DWPC_ServicePresent", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();

        // -------------------- Step 1: Submit request as requester --------------------
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "Fritz");
        String serviceName = dwpcTestData.get("serviceName");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11470_Driver", dwpDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11470_requestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceRequestName"));
        Assert.assertFalse(requestId.isEmpty(), "SR is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("WaitingApprovalStatus"));

        // -------------------- Step 2: Approver reassigns approval to another user --------------------
        Map<Object, String> approverData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "friedaUser");
        WebDriver ApproverDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(ApproverDriver);
        context.setAttribute("DRSQA_11470_Driver", ApproverDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_DWP);
        login(approverData.get("UserName"), approverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod2 = new DWPFeatureMethods();
        dwpFeatureMethod2.verifyDWPHomePageOpened();
        dwpFeatureMethod2.openActivityPage();
        dwpFeatureMethod2.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod2.doReassignApprovalTo(dwpTestData.get("ReassignUserFullName"));
        dwpFeatureMethod2.confirmActionWithPasswordIfPrompted(approverData.get("UserName"), approverData.get("Password"));
        dwpFeatureMethod2.verifyStatusDetailContains(dwpTestData.get("ReassignedBannerPrefix") + dwpTestData.get("ReassignUserFullName"));
        dwpFeatureMethod2.captureScreenshot("DRSQA_11470_approvalReassigned", SolQAConstants.appDWP);
        dwpFeatureMethod2.closeBrowser();

        // -------------------- Step 3: Reassigned user approves --------------------
        Map<Object, String> reassignedApproverData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "MonikaUser");
        ApproverDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(ApproverDriver);
        context.setAttribute("DRSQA_11470_Driver", ApproverDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_DWP);
        login(reassignedApproverData.get("UserName"), reassignedApproverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod2 = new DWPFeatureMethods();
        dwpFeatureMethod2.verifyDWPHomePageOpened();
        dwpFeatureMethod2.openActivityPage();
        dwpFeatureMethod2.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod2.clickApproveRequestInEventDetails();
        dwpFeatureMethod2.confirmActionWithPasswordIfPrompted(reassignedApproverData.get("UserName"), reassignedApproverData.get("Password"));
        dwpFeatureMethod2.verifyApprovalStatusFromDetails(SolQAConstants.APPROVED);
        dwpFeatureMethod2.captureScreenshot("DRSQA_11470_requestApproved", SolQAConstants.appDWP);
        dwpFeatureMethod2.closeBrowser();

        // -------------------- Step 4: Verify fulfillment exists in ITSM --------------------
        setDriver(dwpDriver);
        dwpFeatureMethod.refreshBrowser();
        String fulfillmentId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(fulfillmentId != null && !fulfillmentId.trim().isEmpty(), "Fulfillment ID is not available for request: " + requestId);
        dwpFeatureMethod.closeBrowser();

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11470_Driver", itsmDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMUsers, "cbhagatUser");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethod = new ITSMFeatureMethods();
        itsmFeatureMethod.verifyITSMLogin();
        itsmFeatureMethod.navigateMenu(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
        itsmFeatureMethod.searchChange(fulfillmentId);
        itsmFeatureMethod.addApproverToChange("Individual", "Frieda", "Hoffmann");
        itsmFeatureMethod.clickOnNextButton();
        itsmFeatureMethod.verifyChangeStatus("Request For Change");
        itsmFeatureMethod.captureScreenshot("DRSQA_11470_ITSM_ChangeFound", SolQAConstants.appITSM);
        itsmFeatureMethod.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11470_Driver", dwpDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_DWP);
        login(approverData.get("UserName"), approverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyLogin();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.doReassignApprovalTo(dwpTestData.get("ReassignUserFullName2"));
        dwpFeatureMethod.confirmActionWithPasswordIfPrompted(approverData.get("UserName"), approverData.get("Password"));
        dwpFeatureMethod.verifyStatusDetailContains(dwpTestData.get("ReassignedBannerPrefix") + dwpTestData.get("ReassignUserFullName2"));
        dwpFeatureMethod.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11470_Driver", dwpDriver);
        context.setAttribute("DRSQA_11470_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.confirmActionWithPasswordIfPrompted(dwpUserData.get("UserName"), dwpUserData.get("Password"));
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.APPROVED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11470_requestApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        log.info("----------------------- END: DRSQA-11470 Reassign Approval flow completed successfully -------------------");
    }

    @Test(description = "Conditionally handle case sensitive database.", groups = {"Regression_P1","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11643", Products = {Product.Helix_DWP, Product.Helix_ITSM})
    public void DRSQA_11643_ConditionallyHandleCaseSensitiveDatabase(ITestContext context) throws Exception {

        log.info("***************** START: DRSQA-11643 Conditionally handle case sensitive database. *****************");
        log.info("-------Step1: Login to ITSM with pbunyon/__USER_PASSWORD__.Create new enduser in userform.--------------");
          itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
          setDriver(itsmDriver);
          context.setAttribute("DRSQA_11643_Driver", getDriver());
          context.setAttribute("DRSQA_11643_Product", Product.Helix_ITSM);
          itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11643_data");
          itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
          login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
          itsmFeatureMethod = new ITSMFeatureMethods();
          itsmFeatureMethod.captureScreenshot("DRSQA_11643_MT_Login_with_Admin", SolQAConstants.appITSM);
  
          log.info(" Navigate to People form to create new user if user not available " + itsmTestData.get("LoginID"));
          itsmFeatureMethod.captureScreenshot("DRSQA_11643_userSearch", SolQAConstants.appITSM);
          String username = itsmFeatureMethod.createUserInCTMPeopleForm(itsmTestData, configProp.get("ARServerAlias"), false);

          log.info("----------Step2: Login with the user in DWP verify the login is successful.--------------");
          dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
          setDriver(dwpDriver);
          context.setAttribute("DRSQA_11643_Driver", dwpDriver);
          context.setAttribute("DRSQA_11643_Product", Product.Helix_DWP);
          login(username, itsmTestData.get("Password"), SolQAConstants.appDWP);
          dwpFeatureMethod = new DWPFeatureMethods();
          dwpFeatureMethod.verifyDWPHomePageOpened();
          dwpFeatureMethod.captureScreenshot("DRSQA_11643_DWP_Login_Successful", SolQAConstants.appDWP);

          log.info("----------Step3: Login to ITSM with pbunyon/__USER_PASSWORD__.Modify the Login Id to Upper case in User form--------------");
          itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
          setDriver(itsmDriver);
          context.setAttribute("DRSQA_11643_Driver", itsmDriver);
          context.setAttribute("DRSQA_11643_Product", Product.Helix_ITSM);
  
          itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11643_data_2");
          login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
          itsmFeatureMethod = new ITSMFeatureMethods();
          itsmFeatureMethod.captureScreenshot("DRSQA_11643_MT_Login_with_Admin", SolQAConstants.appITSM);
  
          log.info(" Navigate to People form to create new user if user not available " + itsmTestData.get("LoginID"));
          itsmFeatureMethod.captureScreenshot("DRSQA_11643_userSearch", SolQAConstants.appITSM);
          String USERNAME = itsmFeatureMethod.createUserInCTMPeopleForm(itsmTestData, configProp.get("ARServerAlias"), false);

          log.info("----------Step4: Login with the Uppercase Login Id into DWP.--------------");
          dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
          setDriver(dwpDriver);
          context.setAttribute("DRSQA_11643_Driver", dwpDriver);
          context.setAttribute("DRSQA_11643_Product", Product.Helix_DWP);
          login(itsmTestData.get("LowercaseLoginID"), itsmTestData.get("Password"), SolQAConstants.appDWP);
          dwpFeatureMethod = new DWPFeatureMethods();
          dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
          dwpFeatureMethod.captureScreenshot("DRSQA_11643_DWP_Login_Failed", SolQAConstants.appDWP);
  
        log.info("***************** END: DRSQA-11643 Conditionally handle case sensitive database. *****************");
    }

    @Test(description = "Make Max Number of Attachments Configurable for Comments", groups = {"Regression_P1", "gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "18053", JiraID = "DRSQA-11658", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.Helix_DWPC, Product.Helix_BWF})
    public void DRSQA_11658_MaxNumberOfAttachmentsConfigurableForComments(ITestContext context) throws Exception {
        log.info("***************** START: DRSQA-11658 Max Number of Attachments Configurable for Comments *****************");
      
        log.info("Precondition: Step 1: Modify Rich-text comment field in Attachments");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11658__Details");
        context.setAttribute("DRSQA_11658_Driver", dwpDriver);
        context.setAttribute("DRSQA_11658_Product", Product.Helix_DWP);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(dwpTestData.get("AdminMenuConfiguration"));
        dwpFeatureMethod.clickOnSubMenu(dwpTestData.get("AdminSubMenuAttachmentRules"));
        if(!dwpFeatureMethod.getRichTextComment().equalsIgnoreCase(dwpTestData.get("richtextCommentValue"))) {
			dwpFeatureMethod.setRichTextComment(dwpTestData.get("richtextCommentValue"));
			dwpFeatureMethod.saveChanges();
		}

        log.info("----------Step1:- Login to DWP as another user:");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11658__Details");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillEnduser");
        context.setAttribute("DRSQA_11658_Driver", dwpDriver);
        context.setAttribute("DRSQA_11658_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();

        log.info("----------Step2:- Submit the service request:");
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, dwpTestData.get("ServiceRequestName"));
        String requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceRequestName"));
        Assert.assertTrue(requestId1 != null && !requestId1.trim().isEmpty(), dwpTestData.get("ServiceRequestName") + ": Ticket is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1).verifySrdTitle(dwpTestData.get("ServiceRequestName"));

        log.info("----------Step3 and Step4:- Add 3 attachments in comment:");
        File a1 = CommonMethods.createTextFile(dwpTestData.get("Attachment1"));
        File a2 = CommonMethods.createTextFile(dwpTestData.get("Attachment2"));
        File a3 = CommonMethods.createTextFile(dwpTestData.get("Attachment3"));
        File[] commentAttachments = new File[]{a1, a2, a3};
        dwpFeatureMethod.addCommentWithMultipleAttachmentsInSubmittedRequest(dwpTestData.get("Comment1"), commentAttachments);
      
        log.info("----------Step5:- Verify the attachments are added in the comment:");
        dwpFeatureMethod.verifyAttachments(dwpTestData.get("Comment1"), a1);
        dwpFeatureMethod.verifyAttachments(dwpTestData.get("Comment1"), a2);
        dwpFeatureMethod.verifyAttachments(dwpTestData.get("Comment1"), a3);
        dwpFeatureMethod.captureScreenshot("DRSQA_11658_CommentAttachmentInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.waitForFulfillmentSectionToDisplay();
    
        log.info("----------Step9:- Add a comment and attachment in fulfillment similar to step 8:");
        dwpFeatureMethod.addCommentWithMultipleAttachmentInFulfillmentDetails(dwpTestData.get("Comment2"), new String[]{dwpTestData.get("Attachment4"), dwpTestData.get("Attachment5"), dwpTestData.get("Attachment6")});
        dwpFeatureMethod.closeFulfillmentDetailsModalIfPresent();
        dwpFeatureMethod.captureScreenshot("DRSQA_11658_CommentAttachmentInFulfillmentSection", SolQAConstants.appDWP);
        String incidentId = dwpFeatureMethod.getFulfillmentID();
        Assert.assertTrue(incidentId != null && !incidentId.trim().isEmpty(), "Incident fulfillment ID is blank for request: " + requestId1);
        log.info("DRSQA_11658 Incident ID: " + incidentId);
        dwpFeatureMethod.closeBrowser();
        
        log.info("----------Step6:- Login to DWPC:");
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
		setDriver(dwpcDriver);
		context.setAttribute("DRSQA_11658_Driver", dwpcDriver);
		context.setAttribute("DRSQA_11658_Product", Product.Helix_DWPC);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWPC);
		dwpcFeatureMethods = new DWPCFeatureMethods();
		dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
		dwpcFeatureMethods.openServiceRequestsReports();

		log.info("----------Step7:- Navigate to Reports - Service Request - Click on the recently added request:");
		dwpcFeatureMethods.openDWPRequest(requestId1);

		log.info("----------Step8:- Check comments section for the request:");
		dwpcFeatureMethods.verifyCommentAndAttachmentsInRequest(dwpTestData.get("Comment1"), commentAttachments);
		dwpcFeatureMethods.closeBrowser();

        log.info("----------Step10:- Login to ITSM ");
        smartItDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        setDriver(smartItDriver);
        context.setAttribute("DRSQA_11658_Driver", smartItDriver);
        context.setAttribute("DRSQA_11658_Product", Product.Helix_ITSM);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethods.navigateToTicketConsole();

        log.info("----------Step11:- Search for the same incident ID in global search and open it:");
        smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.INCIDENT, incidentId);

        log.info("----------Step12:- Check Work Detail for comments and attachments:");
        smartITFeatureMethods.captureScreenshot("DRSQA_11658_IncidentFound", SolQAConstants.appSmartIT);
        Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(dwpTestData.get("Attachment1")),dwpTestData.get("Attachment1")+" is not present");
        Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(dwpTestData.get("Attachment2")),dwpTestData.get("Attachment2")+" is not present");
        Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(dwpTestData.get("Attachment3")),dwpTestData.get("Attachment3")+" is not present");
        Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(dwpTestData.get("Attachment4")),dwpTestData.get("Attachment4")+" is not present");
        Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(dwpTestData.get("Attachment5")),dwpTestData.get("Attachment5")+" is not present");
        Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(dwpTestData.get("Attachment6")),dwpTestData.get("Attachment6")+" is not present");
        smartITFeatureMethods.captureScreenshot("DRSQA_11658_AttachmentsVerified", SolQAConstants.appSmartIT);
        smartITFeatureMethods.closeBrowser();

        log.info("---------Step 13:- Go to DWP and create a BWF service:--------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11658_Driver", dwpDriver);
        context.setAttribute("DRSQA_11658_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, dwpTestData.get("ServiceName2"));
        String requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceName2"));
        Assert.assertTrue(requestId2 != null && !requestId2.trim().isEmpty(), dwpTestData.get("ServiceName2") + ": Ticket is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2).verifySrdTitle(dwpTestData.get("ServiceName2"));

        log.info("---------Step 14:- Open the created BWF service and add an attachment as in step 8:");
        dwpFeatureMethod.addCommentWithMultipleAttachmentsInSubmittedRequest(dwpTestData.get("Comment1"), commentAttachments);
        dwpFeatureMethod.verifyAttachments(dwpTestData.get("Comment1"), a1);
        dwpFeatureMethod.verifyAttachments(dwpTestData.get("Comment1"), a2);
        dwpFeatureMethod.verifyAttachments(dwpTestData.get("Comment1"), a3);
        dwpFeatureMethod.captureScreenshot("DRSQA_11658_CommentAttachmentInDWP_2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("---------Step 15:- Login to BWF:");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        context.setAttribute("DRSQA_11658_Driver", bwfDriver);
        context.setAttribute("DRSQA_11658_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();

        log.info("---------Step 16:- Open the request submitted in step 16:");
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId2);

        log.info("---------Step 17:- Check the activity section for comments and attachments:");
        Assert.assertTrue(bwfFeatureMethods.verifyAttachmentUpdate_In_SocialFeed(dwpTestData.get("Attachment1")),dwpTestData.get("Attachment1")+" is not present");
        Assert.assertTrue(bwfFeatureMethods.verifyAttachmentUpdate_In_SocialFeed(dwpTestData.get("Attachment2")),dwpTestData.get("Attachment2")+" is not present");
        Assert.assertTrue(bwfFeatureMethods.verifyAttachmentUpdate_In_SocialFeed(dwpTestData.get("Attachment3")),dwpTestData.get("Attachment3")+" is not present");
        bwfFeatureMethods.captureScreenshot("DRSQA_11658_BWF_AttachmentsVerified", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("***************** END: DRSQA-11658 Max Number of Attachments Configurable for Comments *****************");
    }

    @AfterMethod(alwaysRun=true)
    public void closeAllBrowsers() {
        log.info("Starting cleanup for DWP_ITSM test method.");

        if (dwpFeatureMethod != null) {
            try {
                dwpFeatureMethod.closeBrowser();
                log.info("DWP feature browser close invoked.");
            } catch (Exception e) {
                log.info("DWP feature browser close skipped/failed: " + e.getMessage());
            }
        }

        if (itsmFeatureMethod != null) {
            try {
                itsmFeatureMethod.closeBrowser();
                log.info("ITSM feature browser close invoked.");
            } catch (Exception e) {
                log.info("ITSM feature browser close skipped/failed: " + e.getMessage());
            }
        }

        safelyQuitDriver(itsmDriver, "ITSM raw");
        if (dwpDriver != itsmDriver) {
            safelyQuitDriver(dwpDriver, "DWP raw");
        } else if (dwpDriver != null) {
            log.info("DWP and ITSM driver references are the same; skipping duplicate quit.");
        }

        WebDriver activeThreadDriver = getDriver();
        if (activeThreadDriver != null && activeThreadDriver != dwpDriver && activeThreadDriver != itsmDriver) {
            safelyQuitDriver(activeThreadDriver, "ThreadLocal active");
        }

        log.info("Completed cleanup for DWP_ITSM test method.");
    }

}