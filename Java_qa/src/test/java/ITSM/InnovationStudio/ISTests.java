package ITSM.InnovationStudio;

import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.CMDB.CMDBFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.RSSO.RSSOFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class ISTests extends Launcher {
    public static SolqaLogger log = new SolqaLogger(ISTests.class.getName());
    ReadJson readJson = new ReadJson();
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    WebDriver rssoDriver, isDriver, itsmdriver;
    Map<Object, String> smartITUserData,smartITTestData,ITSM_TestData,dwpUserData;
    RSSOFeatureMethods rssoFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    CMDBFeatureMethods cmdbFeatureMethods;
    DWPFeatureMethods dwpFeatureMethods;
    CommonWaits commonWaits;
    CommonMethods commonMethods;
   
    @Test(description = "Create Application, Record Definition and Process Definition in Innovation Studio", groups = {"Regression_P3", "InnovationStudio"})
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-13334", Products = {Product.Innovation_Suite})
    public void DRSQA_13334_Create_Application_Record_And_Process_Definition() throws IOException {
        log.info("***** START : DRSQA_13334_Create_Application_Record_And_Process_Definition *****");
        
            // Launch Innovation Studio
            WebDriver isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);

            Map<Object, String> isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_13334_Data");
            
            // Step 1: Login to Innovation Studio
            log.info("Step 1: Login to Innovation Studio with user");
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_IS_Login", SolQAConstants.appIS);
            
            // Step 2: Navigate to New -> Application and create application
            log.info("Step 2: Navigate to New -> Application");
            long uniqueAppName = System.currentTimeMillis();
            String applicationName = isTestData.get("ApplicationDisplayName");
            String applicationShortName = isTestData.get("ApplicationShortName");
            String groupId = isTestData.get("GroupID");
            
            log.info("Creating Application with Display Name: " + applicationName + uniqueAppName);
            innovationSuiteFeatureMethodsPage.navigateToCreateNewApplication();
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_Navigate_To_Create_Application", SolQAConstants.appIS);
            
            innovationSuiteFeatureMethodsPage.fillApplicationDetails(applicationName + uniqueAppName, applicationShortName + uniqueAppName, groupId + uniqueAppName);
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_Application_Created", SolQAConstants.appIS);
            
            log.info("Step 2 Verification: Verify application is created successfully");
            innovationSuiteFeatureMethodsPage.verifyApplicationCreated(applicationName + uniqueAppName);
            innovationSuiteFeatureMethodsPage.closeNewApplicationDialog();
            
            // Step 3: Create a New Record Definition -> New -> Regular Record
            log.info("Step 3: Create a New Record Definition -> New -> Regular Record");
            String recordDefinitionName = isTestData.get("RecordDefinitionName") + uniqueAppName;
            
            log.info("Creating Record Definition with Name: " + recordDefinitionName);
            innovationSuiteFeatureMethodsPage.navigateToCreateNewRecordDefinition();
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_Navigate_To_Create_Record_Definition", SolQAConstants.appIS);
            
            innovationSuiteFeatureMethodsPage.selectRecordDefinitionType(isTestData.get("RecordType"));
            innovationSuiteFeatureMethodsPage.fillRecordDefinitionName(recordDefinitionName);
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_Record_Definition_Created", SolQAConstants.appIS);
            
            log.info("Step 3 Verification: Verify record definition is created successfully");
            Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyRecordDefinitionCreated(recordDefinitionName), 
                "Record Definition should be created successfully");
            
            // Step 4: Create a Process Definition -> New
            log.info("Step 4: Create a Process Definition -> New");
            String processDefinitionName = isTestData.get("ProcessDefinitionName") +uniqueAppName;
            
            log.info("Creating Process Definition with Name: " + processDefinitionName);
            innovationSuiteFeatureMethodsPage.navigateToCreateNewProcessDefinition();
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_Navigate_To_Create_Process_Definition", SolQAConstants.appIS);
            
            log.info("Drag and Drop create record pallet from left side");
            innovationSuiteFeatureMethodsPage.dragAndDropCreateRecordActivity();
            innovationSuiteFeatureMethodsPage.enterProcessName(processDefinitionName);
            innovationSuiteFeatureMethodsPage.selectInputMap(isTestData.get("InputMap"));
            innovationSuiteFeatureMethodsPage.saveProcessDefinition();
            innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_13334_Process_Definition_Created", SolQAConstants.appIS);
            
            log.info("Step 4 Verification: Verify process definition is created successfully");
            Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProcessDefinitionCreated(processDefinitionName), 
                "Process Definition should be created successfully");
            
            log.info("***** END : DRSQA_13334_Create_Application_Record_And_Process_Definition *****");
    }

    @Test(description = "Option to consume RSSO/HSSO UI Idle Timeout implementation for Converged Server IS UI", groups = {"OneTime", "IS","RSSO","gsutar"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-14998", Products = {Product.SmartIT, Product.Helix_InnovationStudio, Product.RSSO})
	public void DRSQA14998_Option_to_consume_RSSO_HSSO_UI_Idle_Timeout_implementation_for_Converged_Server_IS_UI(ITestContext context) throws Exception {
        try{
		log.info("************** START:- Option to consume RSSO/HSSO UI Idle Timeout implementation for Converged Server IS UI  *************");
		log.info("------------Precondition 1:- Log in to the BMC Helix SSO Admin Console as a SaaS administrator.------------");
		rssoDriver = launchWeb(SolQAConstants.AppRSSO, SolQAConstants.AppRSSO);
		setDriver(rssoDriver);
		context.setAttribute("DRSQA_14998_Driver", rssoDriver);
		context.setAttribute("DRSQA_14998_Product", Product.RSSO);
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "RSSOAdmin");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "RSSO_UIIdleTimeoutData");
		RSSOFeatureMethods rssoFeatureMethods = new RSSOFeatureMethods();
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appRSSO);
		rssoFeatureMethods.verifyLogin();

		log.info("------------Precondition 2:- On the navigation panel, click Tenant.------------");
		rssoFeatureMethods.clickOnRSSOTab(SolQAConstants.TENANT);

		log.info("------------Precondition 3:- Edit a tenant or create a new one for which you want to enable the UI idle timeout.------------");
		rssoFeatureMethods.verifyTenantsIsPresent(configProp.get("setupName"));

		log.info("------------Precondition 4 and Step 5:- Enable the UI idle timeout checkbox.------------");
		rssoFeatureMethods.clickOnEnableUIIdleTimeout(configProp.get("setupName"));
		rssoFeatureMethods.verifyTenantsIsPresent(configProp.get("setupName"));
		rssoFeatureMethods.clickOnPinButton();

		log.info("------------Precondition 6:- Go to General tab and set the time------------");
		rssoFeatureMethods.clickOnRSSOTab(SolQAConstants.GENERAL);
		rssoFeatureMethods.setUIIdealTimeOutAndWarningTime(smartITTestData.get("UIIdleTimeoutAllowedTime"), smartITTestData.get("UIIdleTimeoutWarningTime"));
        rssoDriver.quit();

        log.info("------------Step 1: Login to Innovation studio with Admin user -------------");
        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		context.setAttribute("DRSQA_14998_Driver", getDriver());
		context.setAttribute("DRSQA_14998_Product", Product.Helix_InnovationStudio);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
         
        log.info("------------Step 2: Verify HSSO session timeout warning popup is displayed before being logged out due to inactivity.-------------");
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
		innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_14998_SessionIdle", SolQAConstants.appIS);
		rssoFeatureMethods.verifySessionIdleWarningMessage();

        log.info("------------Step 3: Verify session should be retain if user interact with timeout warning popup within specified time.-------------");
        rssoFeatureMethods.clickOnOkButton();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		
        log.info("------------Step 4: Verify session should be logged out in all apps if user explicitly clicked logged out/close button from popup.-------------");
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
		innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_14998_SessionIdle", SolQAConstants.appIS);
		rssoFeatureMethods.clickOnCrossButton();
        rssoFeatureMethods.verifySessionLogout();

        log.info("------------Step 5: Verify idle session will be extended automatically if user is active in other app and vice versa-------------");
        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		context.setAttribute("DRSQA_14998_Driver", getDriver());
		context.setAttribute("DRSQA_14998_Product", Product.Helix_InnovationStudio);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
        commonWaits.waitForSometime(100000); //This wait is added for the session to be idle for more than allowed time
		innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_14998_SessionIdle", SolQAConstants.appIS);
		rssoFeatureMethods.verifySessionIdleWarningMessage();
        rssoFeatureMethods.clickOnOkButton();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        commonWaits.waitForSometime(100000); //This wait is added for the session to be idle for more than allowed time
		innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_14998_SessionIdle", SolQAConstants.appIS);
		rssoFeatureMethods.verifySessionIdleWarningMessageIsNotPresent();
       
        log.info("------------Step 6: Login to Innovation studio and open DWP application in one tab and 2nd tab open only DWP application and verify the idle popup-------------");
        commonWaits.switchToTabByIndex(0);
        rssoFeatureMethods.openNewTab();
        dwpFeatureMethods=new DWPFeatureMethods();
        dwpFeatureMethods.navigateToDWPURLInNewOpenedTab();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bhill");
        dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        commonWaits.switchToTabByIndex(1);
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        }
        finally{
            log.info("------------Disable UI Idle Timeout------------");
            rssoDriver = launchWeb(SolQAConstants.AppRSSO, SolQAConstants.AppRSSO);
            setDriver(rssoDriver);
            context.setAttribute("DRSQA_16986_Driver", rssoDriver);
            context.setAttribute("DRSQA_16986_Product", Product.RSSO);
            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "RSSOAdmin");
            smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "RSSO_UIIdleTimeoutData");
            RSSOFeatureMethods rssoFeatureMethods = new RSSOFeatureMethods();
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appRSSO);
            rssoFeatureMethods.verifyLogin();
            rssoFeatureMethods.clickOnRSSOTab(SolQAConstants.TENANT);
            rssoFeatureMethods.verifyTenantsIsPresent(configProp.get("setupName"));
            rssoFeatureMethods.clickOnDisableUIIdleTimeout(configProp.get("setupName"));
        }
        log.info("*********** END:- Option to consume RSSO/HSSO UI Idle Timeout implementation for Converged Server IS UI *************");
	}
    
    

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (innovationSuiteFeatureMethodsPage != null) {
        	innovationSuiteFeatureMethodsPage.closeBrowser();
        }
        if (rssoDriver != null) {
            rssoDriver.quit();
        }
        if (isDriver != null) {
            isDriver.quit();
        }
        if (itsmdriver != null) {
            itsmdriver.quit();
        }
    }
}
