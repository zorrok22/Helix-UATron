package BCM;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BCM.BCMFeatureMethods;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.Product;

import java.util.Map;

/**
 * Test class for BMC Client Management automation
 * Covers complete BCM workflow: login, use features, logout, invalid login, re-login
 * 
 * @author hpareek
 */
public class BCMTest extends Launcher {
    
    public static SolqaLogger log = new SolqaLogger(BCMTest.class.getName());
    ReadJson readJson = new ReadJson();
    BCMFeatureMethods bcmFeatureMethods;
    Map<Object, String> bcmUserData, bcmTestData, invalidUserData;
    WebDriver bcmDriver = null;
    String appName = SolQAConstants.appBCM;
    
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        log.info("Setting up BCM test");
        log.info("BCM test setup completed");
    }
    
    @AfterMethod (alwaysRun=true)
	public void closeBrowser(ITestContext context) {
		log.info("Closing the browser");
		if (bcmDriver != null) {
			bcmFeatureMethods.closeBrowser();
		}
		if (getDriver() != null) {
			getDriver().quit();
		}
	}
    
    @Test(description = "BCM to CMDB Integration - Verify Jobs and Data Sync", groups = {"BCM"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11591", Products = {Product.BCM, Product.CMDB})
    public void DRSQA_11591_BCM_CMDB_Integration_Test(ITestContext context) throws Exception {
        log.info("*********** BCM to CMDB Integration Test - Verify Jobs and Data Sync *******************");
        
        try {
            // Load test data
            log.info("Loading test data for BCM-CMDB integration");
            bcmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            bcmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_11591_TestData");
            
            log.info("Step 1: Login to CMDB application with admin user: " + bcmUserData.get("UserName"));
            bcmDriver = launchWeb(SolQAConstants.appCMDB, SolQAConstants.appCMDB);
            setDriver(bcmDriver);
            context.setAttribute("DRSQA_11591_Driver", getDriver());
            context.setAttribute("DRSQA_11591_Product", Product.CMDB);
            
            login(bcmUserData.get("UserName"), bcmUserData.get("Password"), SolQAConstants.appCMDB);
            
            // Initialize feature methods
            pages.ITSM.CMDB.CMDBFeatureMethods cmdbFeatureMethods = new pages.ITSM.CMDB.CMDBFeatureMethods();
            cmdbFeatureMethods.verifyLoginTo_CMDB();
            log.info("Successfully logged in to CMDB application");
            
            log.info("Step 2: Navigate to Jobs -> Manage Atrium Integrator");
            cmdbFeatureMethods.navigateToMenu(bcmTestData.get("JobsMenu"), bcmTestData.get("ManageAtriumIntegratorMenuItem"), "");
            
            log.info("Step 3: Verify that BCM Hardware and Software jobs are present in Atrium Integrator");
            String[] atriumIntegratorJobs = {
                bcmTestData.get("BCM_Hardware_Delete_Job"),
                bcmTestData.get("BCM_Hardware_Input_Job"),
                bcmTestData.get("BCM_Software_Delete_Job"),
                bcmTestData.get("BCM_Software_Input_Job")
            };
            cmdbFeatureMethods.verifyAtriumIntegratorJobsPresent(atriumIntegratorJobs);
            log.info("All BCM jobs verified successfully in Manage Atrium Integrator");
            
            log.info("Step 4: Navigate to Jobs -> Manage Reconciliation");
            cmdbFeatureMethods.navigateToMenu(bcmTestData.get("JobsMenu"), bcmTestData.get("ManageReconciliationMenuItem"), "");
            
            log.info("Step 5: Verify that 'BCM - Identification and Merge' job is present in Reconciliation");
            String[] reconciliationJobs = {bcmTestData.get("BCM_Identification_Merge_Job")};
            cmdbFeatureMethods.verifyReconciliationJobsPresent(reconciliationJobs);
            log.info("BCM - Identification and Merge job verified successfully");
            
            log.info("Step 6: Navigate back to Jobs -> Manage Atrium Integrator");
            cmdbFeatureMethods.navigateToMenu(bcmTestData.get("JobsMenu"), bcmTestData.get("ManageAtriumIntegratorMenuItem"), "");
            
            log.info("Step 7: Execute BCM_Software_Input job and verify number of software records");
            String softwareRecordsProcessed = cmdbFeatureMethods.executeAtriumIntegratorJob(bcmTestData.get("BCM_Software_Input_Job"));
            log.info("BCM_Software_Input job completed. Records processed: " + softwareRecordsProcessed);
            
            log.info("Step 8: Navigate back to Jobs -> Manage Atrium Integrator");
            cmdbFeatureMethods.navigateToMenu(bcmTestData.get("JobsMenu"), bcmTestData.get("ManageAtriumIntegratorMenuItem"), "");
            
            log.info("Step 9: Execute BCM_Hardware_Input job");
            String hardwareRecordsProcessed = cmdbFeatureMethods.executeAtriumIntegratorJob(bcmTestData.get("BCM_Hardware_Input_Job"));
            log.info("BCM_Hardware_Input job completed. Records processed: " + hardwareRecordsProcessed);
            
            log.info("Step 10: Navigate to Jobs -> Manage Reconciliation");
            cmdbFeatureMethods.navigateToMenu(bcmTestData.get("JobsMenu"), bcmTestData.get("ManageReconciliationMenuItem"), "");
            
            log.info("Step 11: Execute 'BCM - Identification and Merge' job");
            log.info("This job will add data from BCM to CMDB dataset (BMC.ASSET dataset)");
            cmdbFeatureMethods.runReconciliationJob(bcmTestData.get("BCM_Identification_Merge_Job"));
            log.info("BCM - Identification and Merge job completed successfully");
            
            log.info("Step 12: Verify assets by searching BMC.ASSET with class name, attribute, and device");
            log.info("Searching for: Class=" + bcmTestData.get("SearchClassName") + 
                    ", Attribute=" + bcmTestData.get("SearchAttributeName") + 
                    ", Device=" + bcmTestData.get("SearchDeviceName"));
            cmdbFeatureMethods.searchAndVerifyCI(
                bcmTestData.get("SearchClassName"),
                bcmTestData.get("SearchAttributeName"),
                bcmTestData.get("SearchDeviceName")
            );
            log.info("Assets from BCM successfully verified in CMDB");
            
            log.info("*********** BCM to CMDB Integration Test completed successfully *******************");
            
        } catch (Exception e) {
            log.error("TEST FAILED: BCM to CMDB Integration - " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "SmartIT to BCM Integration - Asset Details and Actions", groups = {"BCM"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11595", Products = {Product.SmartIT, Product.BCM})
    public void DRSQA_11595_SmartIT_BCM_Integration_Test(ITestContext context) throws Exception {
        log.info("*********** DRSQA-11595: SmartIT to BCM Integration - Asset Details and Actions *******************");
        
        try {
            // Load test data
            log.info("Loading test data for SmartIT-BCM integration");
            bcmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
            bcmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11595_TestData");
            
            log.info("Step 1: Login to SmartIT application with administrative user: " + bcmUserData.get("UserName"));
            bcmDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(bcmDriver);
            context.setAttribute("DRSQA_11595_Driver", getDriver());
            context.setAttribute("DRSQA_11595_Product", Product.SmartIT);
            
            login(bcmUserData.get("UserName"), bcmUserData.get("Password"), SolQAConstants.appSmartIT);
            
            // Initialize SmartIT feature methods
            pages.ITSM.SmartIT.SmartITFeatureMethodsPage smartITFeatureMethods = new pages.ITSM.SmartIT.SmartITFeatureMethodsPage();
            pages.ITSM.SmartIT.SmartITDashboardPage smartITDashboard = new pages.ITSM.SmartIT.SmartITDashboardPage();
            smartITDashboard.verifySmartITPageLaunchedSuccessfully();
            log.info("Successfully logged in to SmartIT application");
            
            log.info("Step 2: Navigate to Asset Console");
            smartITFeatureMethods.navigateToAssetConsole();
            
            log.info("Step 3: Search Asset by name: " + bcmTestData.get("AssetName"));
            smartITFeatureMethods.searchAssetByName(bcmTestData.get("AssetName"));
            
            log.info("Step 4: Open Full Asset Information");
            smartITFeatureMethods.openFullAssetInformation();
            
            log.info("Step 5: Click on More dropdown and select Asset Details");
            smartITFeatureMethods.openAssetDetailsFromMore();
            
            log.info("Step 6: Enter BCM Admin credentials and login");
            log.info("BCM Username: " + bcmTestData.get("BCMUsername"));
            smartITFeatureMethods.loginToBCMAdmin(bcmTestData.get("BCMUsername"), bcmTestData.get("BCMPassword"));
            log.info("BCM login successful");
            
            log.info("Step 7: Select different actions from More menu");
            log.info("Note: User can select actions like Audit Now, Check Connection, Reboot, Shut down, Wake up");
            
            // Select one action as an example (Audit Now)
            log.info("Step 7.1: Selecting action: " + bcmTestData.get("BCMAction1"));
            smartITFeatureMethods.selectBCMAction(bcmTestData.get("BCMAction1"));
            log.info("Action selected successfully: " + bcmTestData.get("BCMAction1"));
            
            log.info("Step 8: Verify action notification in Activities tab");
            log.info("Action should be submitted successfully and notification shown in activities tab");
            smartITFeatureMethods.verifyActivityNotification(bcmTestData.get("ExpectedActivity"));
            log.info("Activity notification verified successfully");
            
            log.info("Step 9: Verify assets from BCM are present in CMDB");
            log.info("Verifying asset: " + bcmTestData.get("AssetName") + " with class: " + bcmTestData.get("SearchClassName"));
            log.info("All assets present in BCM should be added in CMDB with dataset: " + bcmTestData.get("DatasetName"));
            smartITFeatureMethods.verifyAssetInCMDB(bcmTestData.get("SearchClassName"), bcmTestData.get("AssetName"));
            log.info("Asset verification in CMDB completed");
            
            log.info("*********** DRSQA-11595: SmartIT to BCM Integration test completed successfully *******************");
            
        } catch (Exception e) {
            log.error("TEST FAILED: DRSQA-11595 - " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "BCM - Complete Workflow with Login, Logout, Invalid Credentials, and Session Timeout", groups = {"BCM"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11570", Products = {Product.BCM})
    public void DRSQA_11570_BCM_CompleteWorkflow(ITestContext context) throws Exception {
        log.info("*********** DRSQA-11570 : BCM Complete Workflow with Session Management *******************");
        
        try {
            log.info("*********** DRSQA-11570 : BCM Complete Workflow with Session Management *******************");
		    bcmDriver = launchWeb(SolQAConstants.appBCM, SolQAConstants.appBCM);
		    setDriver(bcmDriver);
		    bcmFeatureMethods = new BCMFeatureMethods();
		    context.setAttribute("DRSQA_11570_Driver", getDriver());
		    context.setAttribute("DRSQA_11570_Product", Product.BCM);
	
            // Load test data
            log.info("Loading test data for BCM users and test scenarios");
            bcmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBCM, "BCMUsers", "pbunyon");
            invalidUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBCM, "BCMUsers", "invalidUser");
            bcmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBCM, "BCMTestData", "FeatureUsageTest");
           
            log.info("Step 1: Enter BCM URL in browser and navigate to login page");
            
            log.info("Step 2: Login to BCM with valid credentials");
            log.info("Login to BCM with user: " + bcmUserData.get("UserName") + " and verify login is successful");
            login(bcmUserData.get("UserName"), bcmUserData.get("Password"), SolQAConstants.appBCM);
            
            log.info("Verify welcome message is displayed after successful login");
            bcmFeatureMethods.verifyWelcomeMessage();
            bcmFeatureMethods.captureScreenshot("DRSQA_11570_LoginSuccessful", SolQAConstants.appBCM);
            log.info("User logged in successfully and welcome message verified");
            
            log.info("Step 3: Click on Logout option and verify logout is successful");
            bcmFeatureMethods.performLogout();
            bcmFeatureMethods.verifyLogoutSuccessful();
            bcmFeatureMethods.captureScreenshot("DRSQA_11570_LogoutSuccessful", SolQAConstants.appBCM);
            log.info("User logged out successfully and redirected to login page");
            
            log.info("Step 4: Try to login with invalid credentials");
            log.info("Attempting login with invalid user: " + invalidUserData.get("UserName"));
            login(invalidUserData.get("UserName"), invalidUserData.get("Password"), SolQAConstants.appBCM);
            
            log.info("Verify error message is displayed for invalid credentials");
            bcmFeatureMethods.verifyLoginErrorDisplayed();
            bcmFeatureMethods.captureScreenshot("DRSQA_11570_InvalidLoginError", SolQAConstants.appBCM);
            log.info("Invalid login attempt failed as expected with error message");
            
            log.info("Step 5: Login again with valid credentials for session timeout test");
            log.info("Login to BCM with valid user: " + bcmUserData.get("UserName"));
            login(bcmUserData.get("UserName"), bcmUserData.get("Password"), SolQAConstants.appBCM);
            bcmFeatureMethods.verifyWelcomeMessage();
            log.info("User logged in successfully for session timeout validation");
            
            log.info("Step 6: Wait for idle timeout (6 minutes) to trigger RSSO token expiration");
            log.info("Starting idle timeout period - waiting for session to expire...");
            bcmFeatureMethods.waitForIdleTimeout(6);
            
            log.info("Step 7: Refresh page and verify re-authentication form is displayed");
            log.info("Refreshing page to check for re-authentication prompt after inactivity");
            bcmFeatureMethods.refreshPageAndVerifyReAuthForm();
            bcmFeatureMethods.captureScreenshot("DRSQA_11570_ReAuthFormDisplayed", SolQAConstants.appBCM);
            log.info("Re-authentication form displayed successfully after idle timeout");
            
            log.info("Step 8: Click Cancel button on re-authentication form");
            bcmFeatureMethods.clickCancelButton();
            bcmFeatureMethods.captureScreenshot("DRSQA_11570_CancelButtonClicked", SolQAConstants.appBCM);
            log.info("Cancel button clicked successfully on re-authentication form");
            
            log.info("*********** DRSQA-11570 test completed successfully *******************");
            
        } catch (Exception e) {
            log.error("TEST FAILED: DRSQA-11570 - " + e.getMessage());
            throw e;
        }
    }

}
