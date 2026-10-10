package CSM;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.CSM.CSMFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class CSMTests extends Launcher {
	
    public static SolqaLogger log = new SolqaLogger(CSMTests.class.getName());
    ReadJson readJson = new ReadJson();
    CSMFeatureMethodsPage csmFeatureMethodsPage;
    DWPFeatureMethods dwpFeatureMethods;
    Map<Object, String> isUserData, isTestData;
    WebDriver isDriver;
    
    @BeforeMethod
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
    	isDriver = launchWeb(SolQAConstants.CUSTOMER_SERVICE_MANAGEMENT , SolQAConstants.appIS);
        setDriver(isDriver);
    }

    @Test(description = "Authorized User Login and Session Initiation for Frontliner Workspace", groups = {"CSM", "InnovationStudio", "Regression_P1"})
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12542",Products = {Product.Customer_Service_Management})
    public void DRSQA_12542_Authorized_User_Login_and_Session_Initiation_for_Frontliner_Workspace(ITestContext context) throws IOException {
    	log.info("***** START : DRSQA_12542_Authorized_User_Login_and_Session_Initiation_for_Frontliner_Workspace *****");

    	context.setAttribute("DRSQA_12542_Driver", getDriver());
		context.setAttribute("DRSQA_12542_Product", Product.Customer_Service_Management);

		isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCSM + File.separator + SolQAConstants.appIS, "ISUsers", "JackT");
		
		log.info("Step 1 : The Authorized user should have access to the login URL");
		log.info("Step 2 : Login to right credentials: User Name & Password");
		login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
		csmFeatureMethodsPage = new CSMFeatureMethodsPage();
		
		//Verify CSM Page is loaded
		csmFeatureMethodsPage.verifyCSMPageLoaded();
		csmFeatureMethodsPage.clickStartSessionButton();
		csmFeatureMethodsPage.verifyFrontlinerWorkspacePageLoaded();
		csmFeatureMethodsPage.csmLogout();
		
		isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCSM + File.separator + SolQAConstants.appIS, "ISUsers", "JackP");
		isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCSM + File.separator + SolQAConstants.appIS, "ISTestData", "DRSQA_12542_Data");
		
		log.info("Step 3 : Login to unauthorized user to CSM application");
		login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
		dwpFeatureMethods = new DWPFeatureMethods();
		dwpFeatureMethods.verifyErrorMessage(isTestData.get("loginErrorMessage"));
	
    	log.info("***** END : DRSQA_12542_Authorized_User_Login_and_Session_Initiation_for_Frontliner_Workspace *****");	
    }
    
    @Test(description = "Create a case from case creation console For Frontliner Workspace", groups = {"CSM", "InnovationStudio", "Regression_P1"})
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12543",Products = {Product.Customer_Service_Management})
    public void DRSQA_12543_Create_a_case_from_case_creation_console_For_Frontliner_Workspace(ITestContext context) throws IOException {
    	log.info("***** START : DRSQA_12543_Create_a_case_from_case_creation_console_For_Frontliner_Workspace *****");

    	context.setAttribute("DRSQA_12543_Driver", getDriver());
		context.setAttribute("DRSQA_12543_Product", Product.Customer_Service_Management);

		isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCSM + File.separator + SolQAConstants.appIS, "ISUsers", "JackT");
		isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCSM + File.separator + SolQAConstants.appIS, "ISTestData", "DRSQA_12543_Data");
		
		log.info("Step 1 : Login to Customer Service Management URL and click on New Case");
		login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
		csmFeatureMethodsPage = new CSMFeatureMethodsPage();
		
		csmFeatureMethodsPage.verifyCSMPageLoaded();
		csmFeatureMethodsPage.clickStartSessionButton();
		csmFeatureMethodsPage.verifyFrontlinerWorkspacePageLoaded();
		
		log.info("Step 2 : Click on open Console then click on New Case");
		csmFeatureMethodsPage.clickOnNewCaseButton();
		
		log.info("Step 3 : Create Case from Case creation Console");
		log.info("Step 4 : Enters a Customer ID and clicks Fetch Customer Info to auto-fill the form with details. If the form does not auto-fill, the customer is marked as a potential customer, and the user manually enters the information. Afterward, the user navigates to the Case Classification tab.");
		log.info("Step 5 : Enter the all mandatory details in case classification tab and click on next");
		log.info("Step 6 : Verify that agents can propose KB solutions based on case classification using FCR or the Knowledgebase console, which categorizes solutions into Known Issues, How-to Articles, FAQs, Tutorials, and Category-based solutions.");
		log.info("Step 7 : Verify that answering all questions is configurable and depends on the case classification (mandatory or optional) and After filling the section, ensure the \"Next\" button at the bottom works, or clicking the \"Additional Details\" title navigates to the next tab.");
		log.info("Step 8 : Verify case will be automatically assigned to the relevant group");
		log.info("Step 9 : Verify the channel field by default selected");
		log.info("Step 10 : Click on attach file button and upload any attachment and proceed to the next tab by clicking the next button at the bottom \r\n"
				+ "or the First Contact Resolution Tab title on the top");
		log.info("Step 11 : Select First Contact Resolution if the customer is provided a solution on the first call itself by the agent");
		csmFeatureMethodsPage.fillCaseCreationConsoleDataAndVerify(isTestData);
	
    	log.info("***** END : DRSQA_12543_Create_a_case_from_case_creation_console_For_Frontliner_Workspace *****");	
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (csmFeatureMethodsPage != null) {
        	csmFeatureMethodsPage.closeBrowser();
        }
    }

}
