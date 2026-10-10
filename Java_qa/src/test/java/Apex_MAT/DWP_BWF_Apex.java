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
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.ThreadSafeUserManager;

public class DWP_BWF_Apex extends Launcher {

    static SolqaLogger log = new SolqaLogger(DWP_BWF_Apex.class.getName());
    DWPFeatureMethods dwpFeatureMethod;
    BWFFeatureMethods bwfFeatureMethods;
    ReadJson readJson = new ReadJson();
    Map<Object, String> dwpUserData, dwpTestData, bwfUserData, bwfTestData;
    WebDriver dwpdriver, bwfDriver;
    
    @BeforeMethod
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        this.dwpdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpdriver);
    }

    @Test(description = "Apex - Login to ITSM application with correct and incorrect credentials")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15630",Products = {Product.Helix_DWP})
    public void DRSQA_15630_Apex_Login_to_ITSM_application_with_correct_and_incorrect_credentials(ITestContext context) throws IOException {

        log.info("***** START : DRSQA_15630_Apex_Login_to_ITSM_application_with_correct_and_incorrect_credentials *****");

        log.info("Step 1 : Try to login to DWP with end user - correct username and wrong password");
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Beth");
        context.setAttribute("DRSQA_15630_Driver", dwpdriver);
        context.setAttribute("DRSQA_15630_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"),"WrongPassword",SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
        dwpFeatureMethod.captureScreenshot("DRSQA_15630_LoginWithCorrectUnameAndWrongPassword",SolQAConstants.appDWP);

        log.info("Step 2 : Try to login to DWP with end user - wrong username");
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Beth");
        // Navigate back to login page for next test
        getDriver().navigate().to(getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_15630_Driver", dwpdriver);
        context.setAttribute("DRSQA_15630_Product", Product.Helix_DWP);
        login("Test",dwpUserData.get("Password"),SolQAConstants.appDWP);
        dwpFeatureMethod.verifyErrorMessage("Failed to login. Please contact the Administrator.");
        dwpFeatureMethod.captureScreenshot("DRSQA_15630_LoginWithWrongUnameAndCorrectPassword",SolQAConstants.appDWP);

        log.info("Step 3 : Try to login to DWP with end user - correct username and correct password");
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Beth");
        // Navigate back to login page for next test
        getDriver().navigate().to(getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_15630_Driver", dwpdriver);
        context.setAttribute("DRSQA_15630_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.captureScreenshot("DRSQA_15630_VerifyActivityPageAfterSuccessfullyLogin",SolQAConstants.appDWP);
        dwpFeatureMethod.dwpLogout();

        log.info("Step 4 : Try to login to DWP Admin url with end user which does not have MyIT Admin permission");
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Beth");
        // Navigate back to login page for next test
        getDriver().navigate().to(getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_15630_Driver", getDriver());
        context.setAttribute("DRSQA_15630_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_15630_VerifyDWPHomepageOpenedForEndUserWhenLoginToDWPAdmin",SolQAConstants.appDWP);
        
        log.info("***** END : DRSQA_15630_Apex_Login_to_ITSM_application_with_correct_and_incorrect_credentials *****");

    }
    
    @Test(description = "Apex-DWP - Bi-directional communication between DWP request & BWF case (via new request)")
	@SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15635",Products = {Product.Helix_DWP,Product.Helix_BWF,Product.Helix_DWPC})
	public void DRSQA_15635_Apex_DWP_Bi_directional_communication_between_DWP_request_BWF_case_via_new_request(ITestContext context) throws Exception {

		log.info("***** START : DRSQA_15635_Apex_DWP_Bi_directional_communication_between_DWP_request_BWF_case_via_new_request *****");
		context.setAttribute("DRSQA_15635_Driver", getDriver());
		context.setAttribute("DRSQA_15635_Product", Product.Helix_DWP);
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Alex");
		dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP,"DWPTestData","DRSQA_15635_Data");

		log.info("Step 1 : Log into DWP as end user");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethod = new DWPFeatureMethods();

		
		String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
		log.info("Step 2 : Go to Catalog tab and request service having BWF - Case fullfillment");
		dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
		dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);

		log.info("request a service: " + SERVICE_NAME);
		dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
		dwpFeatureMethod.addNameInService(SolQAConstants.NEW_NAME);

		log.info("Request is submitted");
		dwpFeatureMethod.clickOnSubmitRequest();
		dwpFeatureMethod.captureScreenshot("DRSQA_15635_requestCreated", SolQAConstants.appDWP);
		String firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);

		log.info(" ************ SRD created ID: " + firstSRD);
		Assert.assertFalse(firstSRD.isEmpty(), "SR is not created");
		
		log.info("Step 3 : Click on a request having BWF fulfillment from Active events section & add a comment with attachment to the request.");
		dwpFeatureMethod.openSrmRequestByRequestID(firstSRD);
		String comment = "Adding attachment";
		File file2 = CommonMethods.createTextFile("DRSQA15635" + ".txt");

		log.info("Open submitted request and add comment with attachment");
		dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequestApex(comment, file2);
		dwpFeatureMethod.verifyAttachmentsApex("Adding attachment", file2);

		log.info("Step 4 : Login to BWFA as a support agent & Click on the case record linked to DWP request from Step#2.");
		bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
		setDriver(bwfDriver);
		bwfFeatureMethods = new BWFFeatureMethods();
		context.setAttribute("DRSQA_15635_Driver", getDriver());
		context.setAttribute("DRSQA_15635_Product", Product.Helix_BWF);
		bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFUsers", "Apex_SupportAgent_Seth");
		bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFTestData", "DRSQA_15635_Data");

		log.info("Login to BWF with user " + bwfUserData.get("UserName"));
		login(bwfUserData.get("UserName"), bwfUserData.get("Password"),SolQAConstants.appBWF);

		bwfFeatureMethods.verifyLoginToBWF();
		log.info("Set LOB as : "+bwfTestData.get("HR_LOB"));
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
		bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();

		log.info("Search and open requested service caseID: " +firstSRD);
		bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
		bwfFeatureMethods.clickSearchBtn_CaseConsole();
		bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
		bwfFeatureMethods.captureScreenshot("DRSQA_15635_ticketOpenInBWF", SolQAConstants.appBWF);
		bwfFeatureMethods.verify_Activity_Tab(comment);
		bwfFeatureMethods.verify_UserName_In_Activity_Tab(dwpUserData.get("FullName"));

		log.info("Step 5 : As the case agent, add a public comment to the case in Activity section.");
		String BWFComment = "BWF Public comment on case";
		log.info("Add public note from BWF side");
		bwfFeatureMethods.addActivityNotes(BWFComment);
		bwfFeatureMethods.captureScreenshot("DRSQA_15635_commentAddedInBWF", SolQAConstants.appBWF);
		bwfFeatureMethods.closeBrowser();

		log.info("Step 6 : Log into DWP as end user & click on the Request from Step#2");
		log.info("Switch to DWP window and verifying the comment added from BWF");
		dwpFeatureMethod.switchToDWP(dwpdriver);
		dwpFeatureMethod = new DWPFeatureMethods();
		dwpFeatureMethod.refreshDWPPage();

		log.info("Goto DWP and verify comments added from BWF side");
		dwpFeatureMethod.verifyComment(BWFComment);
		dwpFeatureMethod.captureScreenshot("DRSQA_15635_bwfCommentInDWP", SolQAConstants.appDWP);
		dwpFeatureMethod.closeBrowser();
		
		log.info("***** END : DRSQA_15635_Apex_DWP_Bi_directional_communication_between_DWP_request_BWF_case_via_new_request *****");
	}
    
    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
    	// Use ThreadLocal cleanup to prevent memory leaks
    	if (dwpFeatureMethod != null) {
    		dwpFeatureMethod.closeBrowser();
    	}
    }

}
