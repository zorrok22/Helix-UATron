package ITSM.DWP;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.BasePage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonMethods;
import util.Listener;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.ThreadSafeUserManager;
import util.WebDriverManager;

public class UPGRADE_DWP_BWF_SmartIT extends BasePage {
    static SolqaLogger log = new SolqaLogger(UPGRADE_DWP_BWF_SmartIT.class.getName());
	DWPFeatureMethods dwpFeatureMethod;
	SmartITFeatureMethodsPage smartITFeatureMethodsPage;
	BWFFeatureMethods bwfFeatureMethods;
	ReadJson readJson = new ReadJson();
	Map<Object, String> dwpUserData, dwpTestData, smartITUserData, smartITTestData;
	WebDriver dwpDriver, dwpDriver2,bwfDriver,smartITDriver;
    String DRSQA_11566_BeforeUpgradeRequestID1="";
	String DRSQA_11566_BeforeUpgradeRequestID2="";
	String DRSQA_11566_BeforeUpgradeRequestID3="";
    String DRSQA_11565_BeforeUpgradeRequestID1="";
    String DRSQA_11565_BeforeUpgradeRequestID2="";
	String DRSQA_11564_BeforeUpgradeRequestID1="";
	File textFile = new File("DRSQA_11566_Attachment.txt");
	File textFile2 = new File("DRSQA_11566_Attachment2.txt");
	File textFile3 = new File("DRSQA_11566_Attachment3.txt");

    @Test(description = "[Upgrade] Validate bi-directional communication on request continues to work after upgrade",groups = {"UPGRADE", "DWP", "DWPC", "SMARTIT"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11566",Products = {Product.Helix_DWP, Product.Helix_DWPC, Product.Helix_SmartIT})
	public void DRSQA_11566_Validate_bi_directional_communication_on_request_continues_to_work_after_upgrade(ITestContext context) throws Exception {

        log.info("***** START : DRSQA_11566_Validate_bi_directional_communication_on_request_continues_to_work_after_upgrade *****");
        log.info("----------Step1:- Once upgrade is done again login to DWP as a end user and serach request the request submitted before upgrade from pre-condition-----------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		context.setAttribute("DRSQA_11566_Driver", getDriver());
        context.setAttribute("DRSQA_11566_Product", Product.Helix_DWP);

		dwpFeatureMethod = new DWPFeatureMethods();
        readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPUsers","jproUser");
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11566_Data");
		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
		dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchItemInGlobalSearch(DRSQA_11566_BeforeUpgradeRequestID1, SolQAConstants.REQUESTS);

		log.info("---------Step2:- Download & view the attachment from the request questionnaire and attachment from collaborator and Smart IT Agents comment---------");
		String comment = dwpTestData.get("Comment");
		String NewComment = dwpTestData.get("NewComment");
		dwpFeatureMethod.verifyAttachments(comment, textFile);

		log.info("---------Step3:- Add a new comment with  attachment to the request.---------");
		dwpFeatureMethod.addCommentInSubmittedRequest(NewComment);
		dwpDriver.quit();

		log.info("---------Step4:- Login to DWP as collaborators Search the request from Pre-condition.---------");
		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		context.setAttribute("DRSQA_11566_Driver", getDriver());
        context.setAttribute("DRSQA_11566_Product", Product.Helix_DWP);

		dwpFeatureMethod = new DWPFeatureMethods();
        readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPUsers","DBriggsUser");
		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
		dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchItemInGlobalSearch(DRSQA_11566_BeforeUpgradeRequestID2, SolQAConstants.REQUESTS);

		log.info("---------Step5:- Verify that attachment to the request before upgrade (pre-condition) and after upgrade (Step#3)is present and can be downloaded & viewed easily---------");
		String CollaboratorComment = dwpTestData.get("CollaboratorComment");
		String NewCollaboratorComment = dwpTestData.get("NewCollaboratorComment");
		File newCollaboratorFile = CommonMethods.createTextFile("DRSQA_11566_Attachment3.txt");
		dwpFeatureMethod.verifyAttachments(CollaboratorComment, textFile2);

		log.info("---------Step6:- Add a new comment with  attachment to the request.---------");
		dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(NewCollaboratorComment, newCollaboratorFile);
		dwpDriver.quit();

		log.info("-----------Step 7: Login into Smart it and Search the request from Pre-condition-------------");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "DBriggsUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11566_Data");
		this.smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		context.setAttribute("DRSQA_11566_Driver", getDriver());
		context.setAttribute("DRSQA_11566_Product", Product.Helix_SmartIT);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("Logged in user: " + smartITUserData.get("UserName"));
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		smartITFeatureMethodsPage.navigateToTicketConsole();
		smartITFeatureMethodsPage.captureScreenshot("DRSQA_11566_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");
		smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.SERVICE_REQUEST, DRSQA_11566_BeforeUpgradeRequestID3);

		log.info("---------Step8:- Verify that attachment to the request before upgrade (pre-condition) and after upgrade (Step#3 & Step#6)is present and can be downloaded & viewed easily---------");
		smartITFeatureMethodsPage.verifyActivityNotes(smartITTestData.get("BeforeUpgradeComment"));

		log.info("**********END : DRSQA_11566_Validate_bi_directional_communication_on_request_continues_to_work_after_upgrade *****");

    }

    @Test(description = "[Upgrade] Validate pending Approvals request progress after upgrade",groups = {"UPGRADE", "DWP", "DWPC", "SMARTIT"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11565",Products = {Product.Helix_DWP, Product.Helix_DWPC, Product.Helix_SmartIT})
	public void DRSQA_11565_Validate_pending_Approvals_request_progress_after_upgrade(ITestContext context) throws Exception {

        log.info("***** START : [Upgrade] Validate pending Approvals request progress after upgrade *****");
        log.info("----------Step1:- Once upgrade is done again login to DWP as a end user and serach request the request submitted before upgrade from pre-condition-----------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
        context.setAttribute("DRSQA_11565_Driver", getDriver());
        context.setAttribute("DRSQA_11565_Product", Product.Helix_DWP);

		dwpFeatureMethod = new DWPFeatureMethods();
		readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPUsers","pbunyon");
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11565_Data");
		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
		dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchItemInGlobalSearch(DRSQA_11565_BeforeUpgradeRequestID1, SolQAConstants.REQUESTS);

		log.info("------Step2:- Verify first request status and approve the request----------");
        dwpFeatureMethod.doApproveRequest();
        dwpFeatureMethod.refreshDWPPage();
		dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
		dwpFeatureMethod.captureScreenshot("DRSQA_11565_requestListedOnMyActivity", SolQAConstants.appDWP);

		log.info("---------- Login to DWP and get created Workorder ID --------------");
		dwpDriver2 = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver2);
		dwpFeatureMethod = new DWPFeatureMethods();
		context.setAttribute("DRSQA_11565_Driver", getDriver());
		context.setAttribute("DRSQA_11565_Product", Product.Helix_DWP);
		readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPUsers","pbunyon");

		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"),dwpUserData.get("Password"),SolQAConstants.appDWP);
		log.info("Open Activity Page");
		dwpFeatureMethod.openActivityPage();
		dwpFeatureMethod.captureScreenshot("DRSQA_11565_requestListedOnMyActivity", SolQAConstants.appDWP);
		dwpFeatureMethod.openSrmRequestByRequestID(DRSQA_11565_BeforeUpgradeRequestID1);
		dwpFeatureMethod.captureScreenshot("DRSQA_11565_RequestID_requestOpened", SolQAConstants.appDWP);
		String fulfillmentID = dwpFeatureMethod.getFulfillmentID();
		log.info("Work Order ID: " + fulfillmentID);

        log.info("---------Step3:- Login to SmartIT and search the approved request by request id and verify Work Order is created --------------");
		log.info("Login to SmartIT and verify Workorder");
		smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		context.setAttribute("DRSQA_11565_WebDriver", getDriver());
		context.setAttribute("DRSQA_11565_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
		log.info("SmartIT user is: " + smartITUserData.get("UserName"));
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Navigate to Ticket Console");
		smartITFeatureMethodsPage.navigateToTicketConsole();
		smartITFeatureMethodsPage.captureScreenshot("DRSQA_11565_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");

		log.info("Applying Filters and opening created workorder");
		smartITFeatureMethodsPage.applyFilterAndOpenTicket("Work Order", fulfillmentID);		
		Assert.assertEquals(smartITFeatureMethodsPage.getWOCustomerName(), dwpTestData.get("CustomerName"), "Customer name is not matching in SmartIT");

		log.info("---------Step4:- Verify the request and reject the 2nd request----------");
		dwpFeatureMethod.switchToDWP(dwpDriver);
		dwpFeatureMethod.refreshDWPPage();
		dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchItemInGlobalSearch(DRSQA_11565_BeforeUpgradeRequestID2, SolQAConstants.REQUESTS);
		dwpFeatureMethod.clickRejectRequestInEventDetails(dwpTestData.get("RejectReason"), true);
		dwpFeatureMethod.refreshDWPPage();
		dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.REJECTED);
		dwpFeatureMethod.captureScreenshot("DRSQA_11565_requestListedOnMyActivity", SolQAConstants.appDWP);

		log.info("---------END : DRSQA_11565_Validate_pending_Approvals_request_progress_after_upgrade *****");
    }

	@Test(description = "[Upgrade] Validate request/ticket status lifecycle progresses correctly post upgrade",groups = {"UPGRADE", "DWP", "SMARTIT"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11564",Products = {Product.Helix_DWP, Product.Helix_SmartIT})
	public void DRSQA_11564_Validate_request_ticket_status_lifecycle_progresses_correctly_post_upgrade(ITestContext context) throws Exception {

        log.info("***** START : [Upgrade] Validate request/ticket status lifecycle progresses correctly post upgrade *****");
        log.info("----------Step1:- Once upgrade is done login to DWP as a request approver and serach th request submitted before upgrade-----------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
        dwpFeatureMethod = new DWPFeatureMethods();
		readJson = new ReadJson();
		context.setAttribute("DRSQA_11564_Driver", getDriver());
        context.setAttribute("DRSQA_11564_Product", Product.Helix_DWP);

		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPUsers","pbunyon");
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11565_Data");
		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
		dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchItemInGlobalSearch(DRSQA_11564_BeforeUpgradeRequestID1, SolQAConstants.REQUESTS);

		log.info("------Step2:- Verify the request status= On Hold and approve the request----------");
        dwpFeatureMethod.doApproveRequest();
        dwpFeatureMethod.refreshDWPPage();
		dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.ON_HOLD);
		dwpFeatureMethod.captureScreenshot("DRSQA_11564_requestListedOnMyActivity", SolQAConstants.appDWP);

		log.info("---------- Login to DWP and get created Workorder ID --------------");
		dwpDriver2 = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver2);
		dwpFeatureMethod = new DWPFeatureMethods();
		context.setAttribute("DRSQA_11565_Driver", getDriver());
		context.setAttribute("DRSQA_11565_Product", Product.Helix_DWP);
		readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPUsers","pbunyon");

		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"),dwpUserData.get("Password"),SolQAConstants.appDWP);
		log.info("Open Activity Page");
		dwpFeatureMethod.openActivityPage();
		dwpFeatureMethod.captureScreenshot("DRSQA_11564_requestListedOnMyActivity", SolQAConstants.appDWP);
		dwpFeatureMethod.openSrmRequestByRequestID(DRSQA_11564_BeforeUpgradeRequestID1);
		dwpFeatureMethod.captureScreenshot("DRSQA_11564_RequestID_requestOpened", SolQAConstants.appDWP);
		String fulfillmentID = dwpFeatureMethod.getFulfillmentID();
		log.info("Work Order ID: " + fulfillmentID);

        log.info("---------Step3:- Login to SmartIT and search the approved request by request id and verify Work Order is created --------------");
		log.info("Login to SmartIT and verify Workorder");
		smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		context.setAttribute("DRSQA_11564_WebDriver", getDriver());
		context.setAttribute("DRSQA_11564_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
		log.info("SmartIT user is: " + smartITUserData.get("UserName"));
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Navigate to Ticket Console");
		smartITFeatureMethodsPage.navigateToTicketConsole();
		smartITFeatureMethodsPage.captureScreenshot("DRSQA_11564_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");

		log.info("Applying Filters and opening created workorder");
		smartITFeatureMethodsPage.applyFilterAndOpenTicket("Work Order", fulfillmentID);		
		Assert.assertEquals(smartITFeatureMethodsPage.getWOCustomerName(), dwpTestData.get("CustomerName"), "Customer name is not matching in SmartIT");

		log.info("---------END : DRSQA_11564_Validate_request_ticket_status_lifecycle_progresses_correctly_post_upgrade *****");
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
            Listener.cleanupThreadLocal();
            // Clear thread-specific user data
            ThreadSafeUserManager.clearThreadData();
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }
}