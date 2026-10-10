package Apex_MAT;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class DWP_IS_Apex extends Launcher {
	
	public static SolqaLogger log = new SolqaLogger(DWP_IS_Apex.class.getName());
	ReadJson readJson = new ReadJson();
	DWPFeatureMethods dwpFeatureMethods;
	InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	Map<Object, String> dwpUserData, dwpTestData, isUserData;
	WebDriver dwpDriver, isDriver;
	
	@Test(description = "Apex - DWP - Search BWF knowledge article")
	@SolqaDetails(authorName = "shudeshp", JiraID="DRSQA-15662", Products = {Product.Helix_DWP})
	public void DRSQA_15662_Apex_DWP_Search_BWF_knowledge_article(ITestContext context) throws IOException {

		log.info("***** START: DRSQA_15662_Apex_DWP_Search_BWF_knowledge_article *****");
		
		log.info("Configuration for BWF Knowledge source instead of HKM.");
		isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_15662_WebDriver", isDriver);
		context.setAttribute("DRSQA_15662_Product", Product.Innovation_Suite);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appIS, "ISUsers", "Apex_AdminUser_Seth");

        log.info("Login with admin user to IS application");
        login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
        innovationSuiteFeatureMethodsPage.updateBWFKnowledgeSource();
        innovationSuiteFeatureMethodsPage.closeBrowser();
        
		log.info("Step 1 : Login with end user to DWP application");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
		context.setAttribute("DRSQA_15662_WebDriver", dwpDriver);
		context.setAttribute("DRSQA_15662_Product", Product.Helix_DWP);
		
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Seth");
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPTestData", "DRSQA_15662_Data");

		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods = new DWPFeatureMethods();
		dwpFeatureMethods.verifyDWPHomePageOpened();
		dwpFeatureMethods.captureScreenshot("DRSQA_15662_Login successfully to DWP", SolQAConstants.appDWP);

		log.info("Step 2 : In the search box search for the knowledge article and Step 3: Click on the article from the list");
		dwpFeatureMethods.searchItemInGlobalSearch(dwpTestData.get("BWF_ArticleName"), SolQAConstants.ARTICLES);
		dwpFeatureMethods.captureScreenshot("DRSQA_15662 BWF article search verification", SolQAConstants.appDWP);

		log.info("***** END: DRSQA_15662_Apex_DWP_Search_BWF_knowledge_article *****");

	}
	
	@AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (dwpFeatureMethods != null) {
        	dwpFeatureMethods.closeBrowser();
        }
        if(innovationSuiteFeatureMethodsPage != null) {
			innovationSuiteFeatureMethodsPage.closeBrowser();
		}
    }

}
