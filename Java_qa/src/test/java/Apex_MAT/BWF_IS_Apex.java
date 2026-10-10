package Apex_MAT;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class BWF_IS_Apex extends Launcher {
	
	public static SolqaLogger log = new SolqaLogger(BWF_IS_Apex.class.getName());
	ReadJson readJson = new ReadJson();
	BWFFeatureMethods bwfFeatureMethods;
	InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	Map<Object, String> bwfUserData, bwfTestData, isUserData;
	WebDriver bwfDriver, isDriver;
	
	@Test(description = "[BWF] Create Knowledge article manually from BWFA console")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12052",Products = {Product.Helix_BWF})
	public void DRSQA_12052_BWF_Create_Knowledge_article_manually_from_BWFA_console(ITestContext context) throws IOException {
		
		log.info("***** START: DRSQA_12052_BWF_Create_Knowledge_article_manually_from_BWFA_console *****");

		log.info("Configuration for BWF Knowledge source instead of HKM.");
		isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_12052_WebDriver", isDriver);
		context.setAttribute("DRSQA_12052_Product", Product.Innovation_Suite);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appIS, "ISUsers", "Apex_AdminUser_Seth");

        log.info("Login with admin user to IS application");
        login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
        innovationSuiteFeatureMethodsPage.updateBWFKnowledgeSource();
        innovationSuiteFeatureMethodsPage.closeBrowser();

        log.info("Step 1 : Login to BWF application as a Case Agent");
		bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_12052_WebDriver", bwfDriver);
        context.setAttribute("DRSQA_12052_Product", Product.Helix_BWF);
        
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFUsers", "Apex_AdminUser_Alex");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFTestData", "DRSQA_12052_Data");

        log.info("Login to BWF with : " + bwfUserData.get("UserName")+ " and verify its logged in successfully");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"),SolQAConstants.appBWF);
        bwfFeatureMethods.captureScreenshot("DRSQA_12052_Login successfully to BWF", SolQAConstants.appBWF);

        log.info("Step 2: Click on a Create Knowledge. Select How To template & populate data in mandatory fields, leave default selection and submit");
        bwfFeatureMethods.captureScreenshot("DRSQA_12052 Navigate Create Knowledge", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToPage(SolQAConstants.CREATE, SolQAConstants.FILTER_KNOWLEDGE);
        String knowledgeArticleID = bwfFeatureMethods.createKnowledgeArticleApex(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_12052_Create Knowledge article", SolQAConstants.appBWF);

        Assert.assertFalse(knowledgeArticleID.isEmpty(), "KA is not created");
        log.info("--------  Knowledge Article ID: " + knowledgeArticleID);
        bwfFeatureMethods.captureScreenshot("DRSQA_12052_Knowledge created successfully", SolQAConstants.appBWF);
        
        log.info("Step 3: Open the article created in Step#2");
        log.info("verify the article status is "+SolQAConstants.IN_PROGRESS);
        Assert.assertEquals(bwfFeatureMethods.getArticleStatus(), SolQAConstants.IN_PROGRESS,"Failed to display In progress Status");

        log.info("Step 4: Click on the Status to change to Draft and save");
        log.info("Change the article status to "+SolQAConstants.DRAFT);
        bwfFeatureMethods.updateArticleStatusApex(SolQAConstants.DRAFT);
        bwfFeatureMethods.captureScreenshot("DRSQA_12052_Knowledge status changed", SolQAConstants.appBWF);

        log.info("verify the article status is "+SolQAConstants.DRAFT);
        Assert.assertEquals(bwfFeatureMethods.getArticleStatus(), SolQAConstants.DRAFT,"Failed to display Draft Status");

        log.info("Step 5:Click on the Status to change to Publish Approval and save");
        log.info("Change the article status to "+SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.updateArticleStatusApex(SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.captureScreenshot("DRSQA_12052_Knowledge status changed", SolQAConstants.appBWF);

        log.info("verify the article status is "+SolQAConstants.PUBLISHED);
        bwfFeatureMethods.captureScreenshot("DRSQA_12052_Knowledge status changed", SolQAConstants.appBWF);
        Assert.assertEquals(bwfFeatureMethods.getArticleStatus(), SolQAConstants.PUBLISHED,"Failed to display Published Status");
        
        log.info("***** END: DRSQA_12052_BWF_Create_Knowledge_article_manually_from_BWFA_console *****");
    }
	
	@AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (bwfFeatureMethods != null) {
            bwfFeatureMethods.closeBrowser();
        }
        if(innovationSuiteFeatureMethodsPage != null) {
			innovationSuiteFeatureMethodsPage.closeBrowser();
		}
    }

}
