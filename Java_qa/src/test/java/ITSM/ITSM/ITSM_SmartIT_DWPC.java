package ITSM.ITSM;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class ITSM_SmartIT_DWPC extends Launcher {

    static SolqaLogger log = new SolqaLogger(ITSM_SmartIT_DWPC.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> smartITUserData, smartITTestData;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    DWPCFeatureMethods dwpcFeatureMethods;
    WebDriver smartitdriver, itsmDriver, dwpcDriver;
    
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
    	itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
    }

    @Test(description = "Create work order for customer with/without login id", groups = {"Regression_P3", "SmartIT", "ITSM", "DWPC"})
    @SolqaDetails(authorName = "shudeshp",  TTID="", JiraID = "DRSQA-16133",Products = {Product.Helix_ITSM, Product.Helix_SmartIT, Product.Helix_DWP})
    public void DRSQA_16133_Create_work_order_for_customer_with_without_login_id(ITestContext context) throws Exception {

        log.info("***** Start DRSQA_16133_Create_work_order_for_customer_with_without_login_id *****");

        log.info("Pre-requisite Steps");
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_16133_Driver", getDriver());
        context.setAttribute("DRSQA_16133_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);

        log.info("Enable PV, update Midtier and SmartIT URLs");
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] {SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_PROGRESSIVE_VIEWS, SolQAConstants.TRUEALPHABET);
        
        componentMenu = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.MID_TIER_BASE_URL, configProp.get("ITSM_URL"));
        
        componentMenu = new String[] {SolQAConstants.ARSYSTEM_BMC_ARSYS_MIDTIER, SolQAConstants.ARSYSTEM_BMC_ARSYS_MIDTIER_DOCKERMTCLUSTERINT};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ARSYSTEM_SECURITY_IFRAME_ALLOWFROMURLS , configProp.get("IS_URL") + " " + configProp.get("SmartIT_URL"));
        
        componentMenu = new String[] {SolQAConstants.ARSYSTEM_BMC_ARSYS_MIDTIER, SolQAConstants.ARSYSTEM_BMC_ARSYS_MIDTIER_DOCKERMTCLUSTERUSER };
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ARSYSTEM_SECURITY_IFRAME_ALLOWFROMURLS , configProp.get("IS_URL") + " " + configProp.get("SmartIT_URL"));
        
        log.info("Step 1: For service Request Creation = In Service Request Management");
        itsmFeatureMethods.updateWorkorderServiceRequestCreation(SolQAConstants.PETRAMCO_COMPANY, SolQAConstants.IN_SERVICE_REQUEST_MANAGEMENT, configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        log.info("Step 2: Login to SmartIT");
        smartitdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartitdriver);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        
        context.setAttribute("DRSQA_16133_Driver", getDriver());
        context.setAttribute("DRSQA_16133_Product", Product.Helix_SmartIT);
        
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallen"); 
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16133_Data_WithoutLoginID");       
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        
        log.info("Step 3: click on create new --> workorder");
        smartITFeatureMethods.navigateToCreateNewWorkorder();

        log.info("Step 4: click on '+' next to customer field");
        smartITFeatureMethods.clickOnCreateNewCustomerButton();
        
        log.info("Step 5: create new customer without entering login id and click on save");
        smartITFeatureMethods.fillNewCustomerDetailsAndSave(smartITTestData);
        
        log.info("Step 6: submit workorder and check if all fields are populated as per entered data");
        smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethods.saveWorkorder();
        
        log.info("Verify that workorder creation and data for customer without login id");
        smartITFeatureMethods.verifyAlertMessage(smartITTestData.get("alertMessage_CustomerWithoutLoginID"));
        String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
        Assert.assertNotNull(workorderID, "Workorder ID is null");
        smartITFeatureMethods.verifyWorkorderData(smartITTestData);
        
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16133_Data_WithLoginID");
        log.info("Step 7: Again create a new workorder but this time with customer having valid login id");
        smartITFeatureMethods.clickOnDismissAlerts();
        smartITFeatureMethods.navigateToCreateNewWorkorder();
        smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethods.saveWorkorder();
        
        log.info("Verify that workorder creation and data for customer with login id");
        workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
        Assert.assertNotNull(workorderID, "Workorder ID is null");
        smartITFeatureMethods.verifyWorkorderData(smartITTestData);
        smartITFeatureMethods.closeBrowser();
        
        log.info("Step 8: Login to ITSM as an Administrator and open CTM:People form");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_16133_Driver", getDriver());
        context.setAttribute("DRSQA_16133_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16133_Data_WithoutLoginID");   
        itsmFeatureMethods.verifyCreatedUserIsExistsWithoutLoginID(smartITTestData, configProp.get("ARServerAlias"));
        
        log.info("Step 9: service Request Creation =In Digital Workplace");
        itsmFeatureMethods.updateWorkorderServiceRequestCreation(SolQAConstants.PETRAMCO_COMPANY, SolQAConstants.IN_DIGITAL_WORKPLACE, configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        
        log.info("Step 10: click on create new --> workorder  -->create new customer without entering login id and click on save");
        smartitdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartitdriver);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        
        context.setAttribute("DRSQA_16133_Driver", getDriver());
        context.setAttribute("DRSQA_16133_Product", Product.Helix_SmartIT);
        
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallen"); 
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16133_Data_WithoutLoginID");       
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        
        smartITFeatureMethods.navigateToCreateNewWorkorder();
        smartITFeatureMethods.clickOnCreateNewCustomerButton();
        smartITFeatureMethods.fillNewCustomerDetailsAndSave(smartITTestData);
        smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethods.saveWorkorder();
        
        log.info("Verify that workorder creation and data for customer without login id");
        String relatedDWPRequestID = smartITFeatureMethods.getRelatedDWPRequestID();
        log.info("Related DWP Request ID: " + relatedDWPRequestID);
        Assert.assertNotNull(relatedDWPRequestID, "DWP Request ID is not created");
        
        log.info("Step 11: Login to DWPC--> Reports ");
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
		setDriver(dwpcDriver);
		context.setAttribute("DRSQA_15148_Driver", getDriver());
		context.setAttribute("DRSQA_15148_Product", Product.Helix_DWPC);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWPC);
		dwpcFeatureMethods = new DWPCFeatureMethods();
		dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
		
		dwpcFeatureMethods.openServiceRequestsReports();
		dwpcFeatureMethods.openDWPRequest(relatedDWPRequestID);
        
        log.info("***** End of DRSQA_16133_Create_work_order_for_customer_with_without_login_id *****");

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (smartITFeatureMethods != null) {
            smartITFeatureMethods.closeBrowser();
        }
        if (dwpcFeatureMethods != null) {
			dwpcFeatureMethods.closeBrowser();
		}
    }
}
