package ITSM.ITSM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITSM.CMDB.CMDBFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.RSSO.RSSOFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.WebDriverManager;
import util.CommonMethods;
import util.CommonWaits;
import util.Listener;
import org.openqa.selenium.WebDriver;

public class ITSM_Dashboard extends Launcher {

    static SolqaLogger log = new SolqaLogger(ITSM_Dashboard.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> itsmUserData, drsqa_12575_data, rssoUserData, rssoTestData, isUserData, isTestData,smartITUserData,smartITTestData;
    ITSMFeatureMethods itsmFeatureMethods;
    DashboadFeatureMethods helixDashboardFeatureMethods;
    WebDriver itsmdriver, rssoDriver,isDriver;
    RSSOFeatureMethods rssoFeatureMethods;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    CommonMethods commonMethods;
    CommonWaits commonWaits;
    CMDBFeatureMethods cmdbFeatureMethods;
    List<String> folderNames;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        itsmdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmdriver);
    }

    @Test(description = "Verify Cross Launch Helix Dashboard From ITSM Web Report", groups = {"Regression_P1", "SmartIT", "ITSM","HelixDashboard"},enabled = false)
    @SolqaDetails(authorName = "hpareek",  TTID="", JiraID = "DRSQA-12575",Products = {Product.Helix_ITSM,Product.Helix_SmartIT})
    //disabling test as it requires test step updates
    public void DRSQA_12575_Verify_Cross_Launch_Helix_Dashboard_From_ITSM_Web_Report(ITestContext context) {

        log.info("**************** Start DRSQA_12575 Verify Cross Launch Helix Dashboard From ITSM Web Report ******************");

        log.info("****** Step 1: Login to ITSM **********");
        // Use existing WebDriver from ThreadLocal
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_12575_Driver", getDriver());
        context.setAttribute("DRSQA_12575_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        drsqa_12575_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12575_Data");
        folderNames = Arrays.asList(drsqa_12575_data.get("folderNames").split(","));
        
        log.info("****** Step 2: Navigate to AR System Administrator -> AR system Report Console ******");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.AR_SYSTEM_ADMINISTRATION, SolQAConstants.AR_SYSTEM_REPORT_CONSOLE);
        
        log.info("****** Step 3: Click on New Report, select web report and navigate to helix dashboard  ******");
        itsmFeatureMethods.clickOnNewReportButton();
        itsmFeatureMethods.selectTypeMenu(drsqa_12575_data.get("typeMenu"));
        itsmFeatureMethods.selectFormMenu(drsqa_12575_data.get("formMenu"));
        itsmFeatureMethods.verifyDisabledOKButton();
        
        itsmFeatureMethods.clickOnHelixDashboardLink();
        helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
        
        log.info("****** Step 4: Open Incident Dashboard and navigate to incident details page ******");
		helixDashboardFeatureMethods.openMenu(drsqa_12575_data.get("menuItem"));
		helixDashboardFeatureMethods.openFoldersFromMenu(folderNames);
		helixDashboardFeatureMethods.clickOnMediumAssignedCount();
		helixDashboardFeatureMethods.clickOnFirstIncident();
		SmartITFeatureMethodsPage smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
		closeBrowser();
		
		log.info("****** Step 5: Login to ITSM and navigate to Incident management console ******");
		// Use existing WebDriver from ThreadLocal
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_12575_Driver", getDriver());
        context.setAttribute("DRSQA_12575_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.INCIDENT_MANAGEMENT_CONSOLE);		
        
        log.info("****** Step 6: Click on print Icon and click on link  ******");
        itsmFeatureMethods.clickOnPrintIcon();
        itsmFeatureMethods.verifyPrintMessage();
        itsmFeatureMethods.clickOnPrintPopupLink();
        itsmFeatureMethods.verifyDocPageOpened();

        log.info("**************** End DRSQA_12575 Verify Cross Launch Helix Dashboard From ITSM Web Report ******************");

    }

    @Test(description = "Option to consume RSSO/HSSO UI Idle Timeout implementation for ITSM (Mid-Tier)", groups = {"OneTime", "ITSM","RSSO"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-16986", Products = {Product.SmartIT, Product.Helix_ITSM, Product.RSSO})
	public void DRSQA16986_Option_to_consume_RSSO_HSSO_UI_Idle_Timeout_implementation_for_ITSM_Mid_Tier(ITestContext context) throws Exception {
        try{
		log.info("************** START:- Option to consume RSSO/HSSO UI Idle Timeout implementation for ITSM (Mid-Tier)  *************");
		log.info("------------Precondition 1:- Log in to the BMC Helix SSO Admin Console as a SaaS administrator.------------");
		rssoDriver = launchWeb(SolQAConstants.AppRSSO, SolQAConstants.AppRSSO);
		setDriver(rssoDriver);
		context.setAttribute("DRSQA_16986_Driver", rssoDriver);
		context.setAttribute("DRSQA_16986_Product", Product.RSSO);
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

        log.info("------------Step 1: Login to ITSM using pbunyon -------------");
        itsmFeatureMethods = new ITSMFeatureMethods();
        setDriver(itsmdriver);  
        context.setAttribute("DRSQA_16986_Driver", itsmdriver);
        context.setAttribute("DRSQA_16986_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();

        log.info("------------Step 2: Verify when Midtier is open in one tab with No user Responce. Logged in to Midtier and keep the session Idle ( No Mouse/keboard activity ) for more than allowed time which we set as 4 min. Make sure user does not respond to warning message as well.-------------");
        commonWaits.waitForSometime(300000); //This wait is added for verify session logout
        smartITFeatureMethods=new SmartITFeatureMethodsPage();
		rssoFeatureMethods.verifySessionLogout();
        itsmdriver.quit();

        log.info("------------Step 3: Validate this feature when Midtier is open in one tab with user Responce. Logged in to Midtier and keep the session Idle for more than allowed time which we set as 10 min. Make sure user responds to warning message.-------------");
        itsmFeatureMethods = new ITSMFeatureMethods();
        setDriver(itsmdriver);  
        context.setAttribute("DRSQA_16986_Driver", itsmdriver);
        context.setAttribute("DRSQA_16986_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        smartITFeatureMethods=new SmartITFeatureMethodsPage();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods=new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        itsmFeatureMethods.verifyITSMLogin();
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForSometime(150000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.clickOnOkButton();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        itsmFeatureMethods.captureScreenshot("DRSQA16987_SessionIdle", SolQAConstants.appITSM);
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForSometime(150000); 
        rssoFeatureMethods.verifySessionLogout();
        itsmdriver.quit();

        log.info("------------Step 4 and 5: Validate feature on Midtier when Child Windows are open.  From Midtier -> Asset mgmt -> SAM console . Click on Manage CI's . This will open child dialogure window. Now Make sure it hits Idle timeout -------------");
        itsmFeatureMethods = new ITSMFeatureMethods();
        setDriver(itsmdriver);  
        context.setAttribute("DRSQA_16986_Driver", itsmdriver);
        context.setAttribute("DRSQA_16986_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ASSET_MANAGEMENT, SolQAConstants.SAM_CONSOLE);
        itsmFeatureMethods.navigateToManageCIs();
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        rssoFeatureMethods.clickOnOkButton();
        itsmFeatureMethods.verifyManageCIsOptionIsVisible();

        log.info("------------Step 6: Validate People creation : Login to Midtier -> Application Admin console -> Create People -> Open Permission window -------------");
        itsmFeatureMethods.LoadForm(SolQAConstants.CTM_PEOPLE, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        rssoFeatureMethods.clickOnOkButton();
        rssoFeatureMethods.verifySessionLogoutIsNotPresent();
        itsmdriver.quit();

        log.info("------------Step 7: Validate cross launch with Midtier. Logged In with Midtier and cross launch to CMDB URL and Smart Reporting URL. Now Make sure It hits Idle timeOut in Midtier. -------------");
        context.setAttribute("DRSQA_16986_Driver", getDriver());
        context.setAttribute("DRSQA_16986_Product", Product.Helix_ITSM);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");      
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

        log.info("**** Login to ITSM and cross launch CMDB from atrium core ****");
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        log.info("------- Atrium Core > Configuration Manager Dashboard >  CI > Create CI, provide required information and create CI--------");
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        rssoFeatureMethods.clickOnOkButton();
        rssoFeatureMethods.verifySessionLogoutIsNotPresent();
        itsmdriver.quit();

        log.info("------------Step 8: Validate Midtier with Approval central scenario. Logged in to Midtier and open Approval center. Now Hit the Idel timeout . -------------");
        context.setAttribute("DRSQA_16986_Driver", getDriver());
        context.setAttribute("DRSQA_16986_Product", Product.Helix_ITSM);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");      
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

        log.info("**** Login to ITSM and cross launch CMDB from atrium core ****");
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
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
        log.info("*********** END:- Option to consume RSSO/HSSO UI Idle Timeout implementation for ITSM (Mid-Tier) *************");
	}

    @Test(description = "Option to consume RSSO/HSSO UI Idle Timeout implementation for CMDB UI", groups = {"OneTime", "ITSM","RSSO"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-16787", Products = {Product.SmartIT, Product.CMDB, Product.RSSO})
	public void DRSQA16787_Option_to_consume_RSSO_HSSO_UI_Idle_Timeout_implementation_for_CMDB_UI(ITestContext context) throws Exception {
        try{
		log.info("************** START:- Option to consume RSSO/HSSO UI Idle Timeout implementation for CMDB UI  *************");
		log.info("------------Precondition 1:- Log in to the BMC Helix SSO Admin Console as a SaaS administrator.------------");
		rssoDriver = launchWeb(SolQAConstants.AppRSSO, SolQAConstants.AppRSSO);
		setDriver(rssoDriver);
		context.setAttribute("DRSQA_16787_Driver", rssoDriver);
		context.setAttribute("DRSQA_16787_Product", Product.RSSO);
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

        log.info("------------Step 1: Login to CMDB Application-------------");
        itsmFeatureMethods = new ITSMFeatureMethods();
        setDriver(itsmdriver);  
        context.setAttribute("DRSQA_16787_Driver", itsmdriver);
        context.setAttribute("DRSQA_16787_Product", Product.CMDB);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.verifyLoginTo_CMDB();

        log.info("------------Step 2: Open CMDB duplicate tab and verify idle timeout-------------");
        rssoFeatureMethods.openNewTab();
        cmdbFeatureMethods.navigateToCMDBURLInNewOpenedTab();
        cmdbFeatureMethods.verifyLoginTo_CMDB();
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForSometime(200000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        commonWaits.switchToTabByIndex(2);
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        commonWaits.switchToTabByIndex(1);
        rssoFeatureMethods.clickOnOkButton();
        commonWaits.switchToTabByIndex(2);
        rssoFeatureMethods.verifySessionIdleWarningMessageIsNotPresent();
        itsmdriver.quit();
        rssoDriver.quit();

        log.info("------------Step 3: Open CMDB and ITSM tab and verify idle timeout-------------");
        itsmFeatureMethods = new ITSMFeatureMethods();
        setDriver(itsmdriver);  
        context.setAttribute("DRSQA_16787_Driver", itsmdriver);
        context.setAttribute("DRSQA_16787_Product", Product.CMDB);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.verifyLoginTo_CMDB();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmFeatureMethods.verifyITSMLogin();
        commonWaits.switchToTabByIndex(2);
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
        log.info("*********** END:- Option to consume RSSO/HSSO UI Idle Timeout implementation for ITSM (Mid-Tier) *************");
	}


    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (helixDashboardFeatureMethods != null) {
            helixDashboardFeatureMethods.closeBrowser();
        }
        WebDriverManager.cleanupDriver();
        Listener.cleanupThreadLocal();
    }
}
