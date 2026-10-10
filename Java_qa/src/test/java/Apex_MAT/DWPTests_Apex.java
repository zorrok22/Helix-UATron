package Apex_MAT;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class DWPTests_Apex extends Launcher {
	public static SolqaLogger log = new SolqaLogger(DWPTests_Apex.class.getName());
	ReadJson readJson = new ReadJson();
	DWPFeatureMethods dwpFeatureMethods;
	Map<Object, String> dwpUserData, dwpTestData;
	WebDriver dwpdriver = null;

	@BeforeMethod
	public void setup() {
		// Use ThreadLocal WebDriver management - only create one WebDriver per thread
		dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpdriver);
	}

	@Test(description = "Apex - DWP - Search ITSM knowledge article")
	@SolqaDetails(authorName = "shudeshp", JiraID="DRSQA-15662", Products = {Product.Helix_DWP})
	public void DRSQA_15662_Apex_DWP_Search_ITSM_knowledge_article(ITestContext context) throws IOException {

		log.info("***** START: DRSQA_15662_Apex_DWP_Search_ITSM_knowledge_article *****");
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Alex");
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPTestData", "DRSQA_15662_Data");

		context.setAttribute("DRSQA_15662_WebDriver", dwpdriver);
		context.setAttribute("DRSQA_15662_Product", Product.Helix_DWP);

		log.info("Step 1 : Login with end user to DWP application");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods = new DWPFeatureMethods();
		dwpFeatureMethods.verifyDWPHomePageOpened();
		dwpFeatureMethods.captureScreenshot("DRSQA_15662_Login successfully to DWP", SolQAConstants.appDWP);

		log.info("Step 2 : In the search box search for the knowledge article");
		dwpFeatureMethods.searchItemInGlobalSearch(dwpTestData.get("ITSM_ArticleName"), SolQAConstants.ARTICLES);
		dwpFeatureMethods.captureScreenshot("DRSQA_15662 ITSM article search verification", SolQAConstants.appDWP);

		log.info("***** END: DRSQA_15662_Apex_DWP_Search_ITSM_knowledge_article *****");

	}
	
	@Test(description = "Approval Operation on DWP")
	@SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12067", Products = {Product.Helix_DWP})
	public void DRSQA_12067_Approval_Operation_on_DWP(ITestContext context) throws Exception {
		log.info("********* START: DRSQA_12067_Approval_Operation_on_DWP **************");
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPTestData", "DRSQA_12067_Data");
		String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");

		context.setAttribute("DRSQA_12067_Driver", getDriver());
		context.setAttribute("DRSQA_12067_Product", Product.Helix_DWP);

		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Max");

		log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods = new DWPFeatureMethods();

		log.info("Step 1 : End user:*goes to DWP -> Catalog,*requests created service,*and just submits request.");
		dwpFeatureMethods.searchAndOpenService(SERVICE_NAME);
		dwpFeatureMethods.clickOnRequestNow(SERVICE_NAME);

		log.info("request a service: " + SERVICE_NAME);
		dwpFeatureMethods.verifySrdTitle(SERVICE_NAME);
		dwpFeatureMethods.addVPNAccessRequestDetails(dwpTestData);

		log.info("Request is submitted");
		dwpFeatureMethods.clickOnSubmitRequest();
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_requestCreated", SolQAConstants.appDWP);
		String firstRequest = dwpFeatureMethods.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
		dwpFeatureMethods.openSrmRequestByRequestID(firstRequest);
		dwpFeatureMethods.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
		dwpdriver.close();

		log.info("Step 2 : Service approver in DWP:*sees approval request in his timeline;*clicks on approval record to see details of the request;*and Approves service.");
		dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpdriver);
		dwpFeatureMethods = new DWPFeatureMethods();
		readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP,"DWPUsers","PrishaUser");

		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods.openActivityPage();
		dwpFeatureMethods.openSrmRequestByRequestID(firstRequest);
		dwpFeatureMethods.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
		
		log.info("Step 3 : Approve the request");
		dwpFeatureMethods.clickApproveRequestInEventDetails();
		dwpFeatureMethods.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_requestListedOnMyActivity", SolQAConstants.appDWP);
		dwpdriver.close();

		log.info("Step 4 : Repeat Step#1-2");
		dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpdriver);
		dwpFeatureMethods = new DWPFeatureMethods();
		context.setAttribute("DRSQA_12067_Driver", getDriver());
		context.setAttribute("DRSQA_12067_Product", Product.Helix_DWP);
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP,"DWPUsers","Apex_AdminUser_Max");

		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"),dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods.openCatalogPage();
		dwpFeatureMethods.openAllItemsPage();
		dwpFeatureMethods.searchAndOpenService(SERVICE_NAME);

		log.info("request a service: " + SERVICE_NAME);
		dwpFeatureMethods.verifySrdTitle(SERVICE_NAME);
		dwpFeatureMethods.addVPNAccessRequestDetails(dwpTestData);

		log.info("Request is submitted");
		dwpFeatureMethods.clickOnSubmitRequest();
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_requestCreated", SolQAConstants.appDWP);
		String secondRequest = dwpFeatureMethods.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
		
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_RequestSubmitted", SolQAConstants.appDWP);
		log.info(dwpTestData.get("ServiceRequestName") + " service request submitted successfully in DWP.");
		dwpFeatureMethods.openActivityPage();
		
		if(firstRequest.equals(secondRequest)){
			dwpFeatureMethods.refreshDWPPage();
			secondRequest=dwpFeatureMethods.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
		}

		Assert.assertFalse(secondRequest.isEmpty(), "SR is not created");
		log.info("DRSQA-12067 SR created ID: " + secondRequest);
		dwpdriver.close();

		log.info("Login to DWP and and reject the service.");
		dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpdriver);
		dwpFeatureMethods = new DWPFeatureMethods();
		context.setAttribute("DRSQA_12067_Driver", getDriver());
		context.setAttribute("DRSQA_12067_Product", Product.Helix_DWP);
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP,"DWPUsers","PrishaUser");

		log.info("Login to DWP with " + dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_requestListedOnMyActivity", SolQAConstants.appDWP);
		dwpFeatureMethods.openActivityPage();
		dwpFeatureMethods.openSrmRequestByRequestID(secondRequest);
		
		log.info("Step 5 : Reject the approval with justification");
		dwpFeatureMethods.clickRejectRequestInEventDetails(dwpTestData.get("RejectReason"), true);
		dwpFeatureMethods.verifyServiceRequestStatus(SolQAConstants.REJECTED);
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_RequestID", SolQAConstants.appDWP);
		dwpdriver.close();

		log.info("Login to DWP and verify fulfillment is not processed");
		dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpdriver);
		dwpFeatureMethods = new DWPFeatureMethods();
		context.setAttribute("DRSQA_12067_Driver", getDriver());
		context.setAttribute("DRSQA_12067_Product", Product.Helix_DWP);
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP,"DWPUsers","Apex_AdminUser_Max");

		log.info("Login to DWP with " +dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"),dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods.openActivityPage();
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_requestListedOnMyActivity", SolQAConstants.appDWP);
		dwpFeatureMethods.openSubmittedRequestInPastEvents(secondRequest);
		dwpFeatureMethods.captureScreenshot("DRSQA_12067_requestOpened", SolQAConstants.appDWP);
		dwpFeatureMethods.verifyStatusDetailPage(SolQAConstants.REJECTED);
		dwpFeatureMethods.verifyNoFulfillmentCreatedForRejectedService();
		
		log.info("***** END: DRSQA_12067_Approval_Operation_on_DWP *****");
	}

	@AfterMethod(alwaysRun = true)
	public void closeAllBrowsers() {
		// Use ThreadLocal cleanup to prevent memory leaks
		if (dwpFeatureMethods != null) {
			dwpFeatureMethods.closeBrowser();
		}
	}
}
