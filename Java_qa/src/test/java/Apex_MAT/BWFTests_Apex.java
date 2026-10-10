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
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class BWFTests_Apex extends Launcher {
    public static SolqaLogger log = new SolqaLogger(BWFTests_Apex.class.getName());
    ReadJson readJson = new ReadJson();
    BWFFeatureMethods bwfFeatureMethods;
    Map<Object, String> bwfUserData, bwfTestData;
    WebDriver bwfdriver = null;
    String importFileLocation = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF + File.separator + "BMCOpsMonitoring_BWFService.zip";
    
    @BeforeMethod
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        bwfdriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfdriver);
    }

    @Test(description = "Apex - BWF - Create Case manually from BWFA console and Apex - BWF - Search/view Case record from Case console")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15633, DRSQA-15631",Products = {Product.Helix_BWF})
    public void DRSQA_15633_Apex_BWF_Create_Case_manually_from_BWFA_console_DRSQA_15631_Apex_BWF_Search_view_Case_record_from_Case_console(ITestContext context)throws IOException {

    	log.info("***** START : DRSQA_15633_Apex_BWF_Create_Case_manually_from_BWFA_console_DRSQA_15631_Apex_BWF_Search_view_Case_record_from_Case_console *****");
        context.setAttribute("DRSQA_15633_WebDriver", bwfdriver);
        context.setAttribute("DRSQA_15633_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFUsers", "Apex_AdminUser_Alex");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFTestData", "DRSQA_15633_Data");

        log.info("DRSQA: 15633 - Step 1 : Login to BWF application as a Case Agent");
        log.info("DRSQA: 15631 - Step 1 : Login to BWF application as a Case Agent");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"),SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.captureScreenshot("DRSQA_15633_DRSQA_15631_Login successfully to BWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Step 2 : Select Human Resources Line of Business.");
        log.info("Step 3 : Click on a Create Case & populate data in mandatory fields, leave default selection and submit");
        log.info("Step 5 : Add Assignee");
        String caseID = bwfFeatureMethods.createCase(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_15633_DRSQA_15631_Case created successfully", SolQAConstants.appBWF);
        log.info("Case ID: " + caseID);
        
        log.info("Step 4 : Open the case created in Step#2 by clicking on Go to the case");
        log.info("DRSQA-15631 - Step 2 : Click on a CaseID of the record from the list");
        bwfFeatureMethods.searchAndOpenCase(caseID);
        bwfFeatureMethods.captureScreenshot("DRSQA_15633_DRSQA_15631_Case searched successfully", SolQAConstants.appBWF);

        log.info("Verify case details");
        bwfFeatureMethods.verifyCaseDetails(bwfTestData);

        log.info("Verify the case status is " + SolQAConstants.ASSIGNED);
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.ASSIGNED,"Failed to display Assigned Status");

        log.info("Step 6 : Click on the Status to change to In Progress and save");
        log.info("Change the case status to " + SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.captureScreenshot("DRSQA_15633_DRSQA_15631_Case status changed", SolQAConstants.appBWF);

        log.info("Verify the case status is " + SolQAConstants.IN_PROGRESS);
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.IN_PROGRESS,"Failed to display In progress Status");

        log.info("Step 7 : Click on the Status to change to Resolved and save");
        log.info("Change the case status to " + SolQAConstants.RESOLVED);
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.RESOLVED);
        bwfFeatureMethods.captureScreenshot("DRSQA_15633_DRSQA_15631_Case status changed", SolQAConstants.appBWF);

        log.info("Verify the case status is " + SolQAConstants.RESOLVED);
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.RESOLVED,"Failed to display Resolved Status");
        
        log.info("***** END : DRSQA_15633_Apex_BWF_Create_Case_manually_from_BWFA_console_DRSQA_15631_Apex_BWF_Search_view_Case_record_from_Case_console *****");

    }
    
    @Test(description = "Apex-BWF - Manage Catalog cross launch")
    @SolqaDetails(authorName = "shudeshp",JiraID = "DRSQA-15632",Products = {Product.Helix_BWF})
    public void DRSQA_15632_Apex_BWF_Manage_Catalog_cross_launch(ITestContext context) throws IOException {
        
    	log.info("***** START : DRSQA_15632_BWF_Manage_Catalog_cross_launch *****");
        context.setAttribute("DRSQA_15632_WebDriver", bwfdriver);
        context.setAttribute("DRSQA_15632_Product", Product.Helix_BWF);
        
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appBWF, "BWFUsers", "Apex_AdminUser_Alex");
        
        log.info("Step 1 : Log into BWFA as a Case Business Analyst with the Case Catalog Administrator functional role.");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"),SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        
        log.info("Step 2 : Click on Manage Catalog link");
        bwfFeatureMethods.crosslaunchManageCatalog();
        bwfFeatureMethods.captureScreenshot("DRSQA_15632_navigatedToManageCatalog", SolQAConstants.appBWF);

        log.info("Step 3 : On the Services tab, click Import from file");
        bwfFeatureMethods.importService(importFileLocation);
        bwfFeatureMethods.captureScreenshot("DRSQA_15632_serviceImported", SolQAConstants.appBWF);
        
        log.info("***** END : DRSQA_15632_BWF_Manage_Catalog_cross_launch *****");
        
    }
    
    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (bwfFeatureMethods != null) {
            bwfFeatureMethods.closeBrowser();
        }
    }
}
