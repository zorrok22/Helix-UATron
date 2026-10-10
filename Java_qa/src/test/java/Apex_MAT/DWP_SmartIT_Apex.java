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
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class DWP_SmartIT_Apex extends Launcher {
    public static SolqaLogger log = new SolqaLogger(DWP_SmartIT_Apex.class.getName());
    ReadJson readJson = new ReadJson();
    DWPFeatureMethods dwpFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    Map<Object, String> dwpUserData, dwpTestData, smartITUserData;
    WebDriver dwpdriver, smartITDriver;
    
    @BeforeMethod
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
    	dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpdriver);
    }

    @Test(description = "DWP - Search for Catalog published services in DWP --> Catalog tab")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15666",Products = {Product.Helix_DWP})
    public void DRSQA_15666_DWP_Search_for_Catalog_published_services_in_DWP_Catalog_tab(ITestContext context) throws IOException {
        log.info("***** START : DRSQA_15666_DWP_Search_for_Catalog_published_services_in_DWP_Catalog_tab*****");

        context.setAttribute("DRSQA_15666_Driver", getDriver());
        context.setAttribute("DRSQA_15666_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Alex");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPTestData", "DRSQA_15666_Data");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethods = new DWPFeatureMethods();
        String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
        
        log.info("Step 1 : Login to DWP as a end user & click on Catalog tab");
		dwpFeatureMethods.searchAndOpenService(SERVICE_NAME);
        dwpFeatureMethods.captureScreenshot("DRSQA_15666_catalogSection_view", SolQAConstants.appDWP);

        log.info("***** END : DRSQA_15666_DWP_Search_for_Catalog_published_services_in_DWP_Catalog_tab *****");
    }
    
    @Test(description = "Apex - DWP - Bi-directional communication between DWP request & Remedy ITSM ticket (via new request)")
	@SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15634",Products = {Product.Helix_DWP,Product.Helix_ITSM})
	public void DRSQA_15634_Apex_DWP_Bi_directional_communication_between_DWP_request_Remedy_ITSM_ticket_via_new_request(ITestContext context) throws Exception {

    	log.info("*********** START : DRSQA_15634_Apex_DWP_Bi_directional_communication_between_DWP_request_Remedy_ITSM_ticket_via_new_request *******************");
    	
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPTestData", "DRSQA_15634_Data");
		String SERVICE_NAME = dwpTestData.get("serviceName");
		String attachmentName=dwpTestData.get("attachmentName");

		log.info("*********** DRSQA_15634_Communication_DWP_ITSM *******************");
		context.setAttribute("DRSQA_15634_Driver", getDriver());
		context.setAttribute("DRSQA_15634_Product", Product.Helix_DWP);

		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Alex");

		log.info("Step 1 : Log into DWP as end user");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods = new DWPFeatureMethods();

		log.info("Step 2 : Go to Catalog tab and request service having ITSM - WO fullfillment");
		dwpFeatureMethods.searchAndOpenService(SERVICE_NAME);
		dwpFeatureMethods.clickOnRequestNow(SERVICE_NAME);

		log.info("request a service: " + SERVICE_NAME);
		dwpFeatureMethods.verifySrdTitle(SERVICE_NAME);

		log.info("Step 3 : Click on a request having WorkOrder fulfillment from Active events section & add a comment with attachment to the request.");
		log.info("Filling the form and submitting the request");
		dwpFeatureMethods.addMSOfficeInstallationTemplateDetails(dwpTestData);
		log.info("Request is submitted");
		dwpFeatureMethods.clickOnSubmitRequest();
		dwpFeatureMethods.captureScreenshot("DRSQA_15634_requestCreated", SolQAConstants.appDWP);
		String firstSRD = dwpFeatureMethods.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);

		log.info(" ************ SRD created ID: " + firstSRD);
		Assert.assertFalse(firstSRD.isEmpty(), "SR is not created");
		dwpFeatureMethods.openSrmRequestByRequestID(firstSRD);
		String comment = "Adding attachment";
		File file = CommonMethods.createTextFile(attachmentName);

		log.info("Open submitted request and add comment with attachment");
		dwpFeatureMethods.addCommentWithAttachmentsInSubmittedRequestApex(comment, file);
		dwpFeatureMethods.verifyAttachmentsApex("Adding attachment", file);

		String fulfillmentID = dwpFeatureMethods.getFulfillmentID();

		log.info("Step 4 : Login to SmartIT as an IT agent that can view WorkOrder & click on the workorder record linked to DWP request from Step#2.");
		log.info("Login to SmartIT and verify Workorder");
		smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		context.setAttribute("DRSQA_15634_WebDriver", getDriver());
		context.setAttribute("DRSQA_15634_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITUsers", "Apex_SupportAgent_Seth");
		log.info("SmartIT user is: " + smartITUserData.get("UserName"));
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Navigate to Ticket Console");
		smartITFeatureMethodsPage.navigateToTicketConsole();
		smartITFeatureMethodsPage.captureScreenshot("DRSQA_15634_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsoleApex(), "User is not navigated to ticket console");

		log.info("Applying Filters and opening created workorder");
		smartITFeatureMethodsPage.applyFilterAndSearchTicketApex(SolQAConstants.INCIDENT_REQUEST_TYPE, fulfillmentID);
		Assert.assertTrue(smartITFeatureMethodsPage.isAttachedFileAppearedUnderActivityTab(attachmentName), "Attachment is not present on the ticket");

		log.info("Step 5 : As the IT Agent, add a public comment to the workorder Activity section.");
		log.info("Add public note in SmartIT");
		String publicComment = "Adding public note from smartIT";
		smartITFeatureMethodsPage.addComment_InActivityFeed(publicComment);
		smartITFeatureMethodsPage.captureScreenshot("DRSQA_15634_addedPublicComment", SolQAConstants.appSmartIT);
		smartITFeatureMethodsPage.closeBrowser();

		log.info("Step 6 : Log into DWP as end user & click on the Request from Step#2");
		log.info("Switch to DWP window and verifying the comment added from SMARTIT");
		dwpFeatureMethods.switchToDWP(dwpdriver);
		dwpFeatureMethods.refreshDWPPage();

		log.info("Goto DWP and verify comments added from SMARTIT side");
		dwpFeatureMethods.verifyComment(publicComment);
		dwpFeatureMethods.captureScreenshot("DRSQA_15634_smartITCommentInDWP", SolQAConstants.appDWP);
		dwpdriver.close();
		log.pass("*********** DRSQA_15634_Communication_DWP_ITSM end *******************");

	}
        
    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (dwpFeatureMethods != null) {
        	dwpFeatureMethods.closeBrowser();
        }
    }
}
