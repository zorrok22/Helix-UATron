package ITSM.SmartIT;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITSM.CMDB.CMDBFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.ITSM.Incident;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.RSSO.RSSOFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import framework.Launcher;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.*;

import org.openqa.selenium.WebDriver;

public class SmartIT_PV extends Launcher {

	SolqaLogger log = new SolqaLogger(SmartIT_PV.class.getName());
	ReadJson readJson = new ReadJson();
	SmartITFeatureMethodsPage smartITFeatureMethods;
	DWPFeatureMethods dwpFeatureMethod;
	ITSMFeatureMethods itsmFeatureMethods;
	CMDBFeatureMethods cmdbFeatureMethods;
	InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	Map<Object, String> smartITUserData, smartITTestData, itsmUserData, itsmTestData, dwpUserData,cmdbTestData, isTestData,isUserData,hkmUserData;
	CommonWaits commonWaits;
	WebDriver smartITdriver, ITSMdriver, DWPdriver, rssoDriver, itsmDriver, isDriver;
	UserUpdater smtitUsers;
	int counter;
	String appName = "SmartIT";
	private static final String[] COMPONENT_MENU_SMARTIT = {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};

	@BeforeTest(alwaysRun = true)
	public void setupUsers() {
		counter = Integer.parseInt(configProp.get("runCounter"));
		if (counter > 0) {
			// Load the JSON file once
			smtitUsers = new UserUpdater();
			String filename = "SmartITUsers";

			String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator
					+ appName + File.separator + filename + ".xlsx";
			String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator
					+ appName + File.separator + filename + ".json";

			log.info("Setting up users with runCounter: " + counter);
			log.info("Excel file path: " + excelFilePath);
			log.info("JSON file path: " + jsonFilePath);

			// Load the JSON file
			smtitUsers.loadJsonFile(jsonFilePath);

			// Read the specific row based on runCounter
			// Note: row index 1 corresponds to first data row (after header)
			// So if runCounter is 3, we read row 3 from Excel
			smtitUsers.updateMultiUserRuntimeUsersFromExcelRow(excelFilePath, counter);

			log.info("Updated SmartITUsers.json for run counter: " + counter + " using row: " + counter);

			// Verify the update by reading the JSON back
			try {
				ReadJson verifyJson = new ReadJson();

				// Verify bmcopssupportagent
				Map<Object, String> supportAgentData = verifyJson.getInputValuesMapIncludingLabelField(
						SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");
				log.info("Verification - bmcopssupportagent keys: " + supportAgentData.keySet());
				log.info("Verification - bmcopssupportagent Username: " + supportAgentData.get("UserName"));
				log.info("Verification - bmcopssupportagent Password: " + supportAgentData.get("Password"));

				// Verify bmcopsenduser
				Map<Object, String> endUserData = verifyJson.getInputValuesMapIncludingLabelField(
						SolQAConstants.appSmartIT, "SmartITUsers", "bmcopsenduser");
				log.info("Verification - bmcopsenduser keys: " + endUserData.keySet());
				log.info("Verification - bmcopsenduser Username: " + endUserData.get("UserName"));
				log.info("Verification - bmcopsenduser Password: " + endUserData.get("Password"));

			} catch (Exception e) {
				log.error("Error verifying JSON update: " + e.getMessage());
			}
		} else {
			// No change required
			log.info("runCounter is 0, no user updates required");
		}
	}

	public SmartIT_PV() {
		commonWaits = new CommonWaits();
	}

	@Test(description = "Create WO - search HKM articles on SmartRecorder", groups = {"Regression_P1", "SmartIT","gsutar"})
	@SolqaDetails(authorName = "gsutar", TTID = "18012", JiraID = "DRSQA-11647", Products = {Product.SmartIT, Product.Helix_ITSM})
	public void DRSQA_11647_Create_WO_Search_HKM_Articles_After_Ticket(ITestContext context) throws Exception {

		log.info("------- START: DRSQA_11647_Create_WO_Search_HKM_Articles_on_SmartRecorder");
		log.info("Precondition: Login to ITSM and enable PV and HKM settings");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_11647_Driver", ITSMdriver);
        context.setAttribute("DRSQA_11647_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		   itsmFeatureMethods = new ITSMFeatureMethods();
		   log.info("Login to ITSM and enable PV and HKM settings");
		   itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
		   itsmFeatureMethods.setSelfService_HKM(true, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
		   itsmFeatureMethods.closeBrowser();

		log.info("-------------- Configure ComAround HKM Integration settings in Innovation Studio ---------");
        isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_11647_Driver", getDriver());
        context.setAttribute("DRSQA_11647_Product", Product.Helix_InnovationStudio);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();

        isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "ComAroundDetails");
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appIS);
        innovationSuiteFeatureMethodsPage.navigateToAdminPage();
        innovationSuiteFeatureMethodsPage.navigateToConfiguration(isTestData.get("knowledgeManagement"));
        innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);
        innovationSuiteFeatureMethodsPage.setAccessMappingComAround(SolQAConstants.PETRAMCO_COMPANY);
        isDriver.close();

		log.info("Step1:- Login to PV enabled and HKM enabled SmartIT with user have Knowledge access permission");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11647_Driver", smartITdriver);
		context.setAttribute("DRSQA_11647_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "lgreenUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11647_Data");
		hkmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "hkmUser");
		log.info("Login to SmartIT with " + smartITUserData.get("UserName"));
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("----------Step 2: Click on Smart recorder -----------");
		log.info("Navigate to Smart Recorder");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_14637_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

		log.info("----------Step 3: Select customer and enter article name in smart recorder -----------");
		String customerName = smartITTestData.get("CustomerName");
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(customerName);
		smartITFeatureMethods.enterStringInSmartRecorder(smartITTestData.get("ArticleName"));

		log.info("Step4:- Click on Search Knowledge Article and then click on create a new article");
		smartITFeatureMethods.clickSearchForKnowledgeResourcesButton();
		log.info("******* Click on Create New --> Knowledge Article ******");
		smartITFeatureMethods.clickCreateNewArticleButton();
		
		log.info("Step5:- Create an Article with Approved status");
		RandomStringUtils.randomNumeric(5);
		String hkmArticleTitle = smartITTestData.get("ArticleName") + RandomStringUtils.randomNumeric(5);
		boolean isArticleCreated = smartITFeatureMethods.createHKMArticle(hkmArticleTitle);
		Assert.assertTrue(isArticleCreated);

		log.info("Step6:- Come back to Smart IT screen");
		commonWaits.switchToTabByIndex(0);

		log.info("Step7:- Click on Create work order");
		smartITFeatureMethods.clickCreateIncidentOrWorkOrderFromCreateTicketBtn(smartITTestData.get("TicketType"));
		commonWaits.waitForElementVisible(smartITFeatureMethods.getCustomerOnWOPV());
		smartITFeatureMethods.saveWorkorder();
		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
		Assert.assertTrue(workorderID != null && workorderID.trim().toUpperCase().contains("WO"), "WO not created");
		log.info("Workorder ID: " + workorderID);

		log.info("------- END: DRSQA_11647_Create_WO_Search_HKM_Articles_After_Ticket");
	}

	@Test(description = "[PV] SmartIT- Search and pin HKM KA on SmartRecorder", groups = { "Regression_P1", "SmartIT","gsutar" })
	@SolqaDetails(authorName = "gsutar", TTID = "18013", JiraID = "DRSQA-11648", Products = { Product.SmartIT,Product.Helix_ITSM })
	public void DRSQA_11648_PV_SmartIT_Search_And_Pin_HKM_KA_On_SmartRecorder(ITestContext context) throws Exception {
		log.info("------- START: DRSQA_11648_PV_SmartIT_Search_And_Pin_HKM_KA_On_SmartRecorder ");
		log.info("Precondition: Login to ITSM and enable PV and HKM settings");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_11647_Driver", ITSMdriver);
        context.setAttribute("DRSQA_11647_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		   itsmFeatureMethods = new ITSMFeatureMethods();
		   log.info("Login to ITSM and enable PV and HKM settings");
		   itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
		   itsmFeatureMethods.setSelfService_HKM(true, configProp.get("ARServerAlias"), configProp.get("adminPassword"), configProp.get("adminPassword"));
		   itsmFeatureMethods.closeBrowser();

		log.info("-------------- Configure ComAround HKM Integration settings in Innovation Studio ---------");
        isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_11647_Driver", getDriver());
        context.setAttribute("DRSQA_11647_Product", Product.Helix_InnovationStudio);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();

        isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "ComAroundDetails");
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appIS);
        innovationSuiteFeatureMethodsPage.navigateToAdminPage();
        innovationSuiteFeatureMethodsPage.navigateToConfiguration(isTestData.get("knowledgeManagement"));
        innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);
        innovationSuiteFeatureMethodsPage.setAccessMappingComAround(SolQAConstants.PETRAMCO_COMPANY);
        isDriver.close();

		log.info("Step1:- Login to PV enabled and HKM enabled SmartIT with user have Knowledge access permission");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11647_Driver", smartITdriver);
		context.setAttribute("DRSQA_11647_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "lgreenUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11647_Data");
		hkmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "hkmUser");
		String smartIT_Url = CommonMethods.readConfig("SmartIT_URL");
		String hkmUserName = smartITTestData.get("HKM_UserName");
		String hkmPassword = smartITTestData.get("HKM_Password");
		log.info("Login to SmartIT with " + smartITUserData.get("UserName"));
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("----------Step 2: Click on Smart recorder -----------");
		log.info("Navigate to Smart Recorder");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_14637_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

		log.info("----------Step 3: Select customer and enter article name in smart recorder -----------");
		String customerName = smartITTestData.get("CustomerName");
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(customerName);
		smartITFeatureMethods.enterStringInSmartRecorder(smartITTestData.get("ArticleName"));

		log.info("Step4:- Click on Search Knowledge Article and then click on create a new article");
		smartITFeatureMethods.clickSearchForKnowledgeResourcesButton();
		log.info("******* Click on Create New --> Knowledge Article ******");
		smartITFeatureMethods.clickCreateNewArticleButton();
		log.info("*************** Create Knowledge Article in HKM ******************");
		smartITFeatureMethods.setCredentials(smartIT_Url, hkmUserName, hkmPassword);

		log.info("Step5:- Create an Article with Approved status");
		RandomStringUtils.randomNumeric(5);
		String hkmArticleTitle = smartITTestData.get("ArticleName") + RandomStringUtils.randomNumeric(5);
		boolean isArticleCreated = smartITFeatureMethods.createHKMArticle(hkmArticleTitle);
		Assert.assertTrue(isArticleCreated);

		log.info("Step6:- Come back to Smart IT screen");
		commonWaits.switchToTabByIndex(0);

		log.info("Step7:- Click on Create work order");
		smartITFeatureMethods.clickCreateIncidentOrWorkOrderFromCreateTicketBtn(smartITTestData.get("TicketType"));
		commonWaits.waitForElementVisible(smartITFeatureMethods.getCustomerOnWOPV());
		smartITFeatureMethods.saveWorkorder();
		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
		Assert.assertTrue(workorderID != null && workorderID.trim().toUpperCase().contains("WO"), "WO not created");
		log.info("Workorder ID: " + workorderID);

		log.info("------- END: DRSQA_11648_PV_SmartIT_Search_And_Pin_HKM_KA_On_SmartRecorder ");
	}

	@Test(description = "[25.2.00] Create Change Freeze From IS Calendar", groups = { "Regression_P1", "SmartIT","gsutar" })
	@SolqaDetails(authorName = "gsutar", TTID = "36144", JiraID = "DRSQA-11379", Products = { Product.Helix_ITSM,Product.SmartIT })
	public void DRSQA11379_CreateChangeFreezeFromISCalendar(ITestContext context) {

		log.info("****** START: [25.2.00] Create Change Freeze From IS Calendar ************************");

		try {        
			log.info("****** START: [25.2.00] Create Change Freeze From IS Calendar ************************");
			log.info("Precondition: Login to ITSM and enable Change Freeze and enableISCalendar configuration");

			ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(ITSMdriver);
        
			context.setAttribute("DRSQA_11379_Driver", ITSMdriver);
			context.setAttribute("DRSQA_11379_Product", Product.Helix_ITSM);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
			log.info("logged in user: " + configProp.get("adminUserName"));        
			log.info("****** Setup  HELIXGPT_ASSISTANT_SERVICE_URL ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			itsmFeatureMethods.navigateToCentralizedConfiguration();
			itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SMARTIT);
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_CHANGE_FREEZE, "true");
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_CALENDAR, "true");

	
			log.info("Step 1: Login to Smart IT and navigate to Calendar");
			smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon"); 
			smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11379_Data");
			
			smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
			setDriver(smartITdriver);
			login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
			log.info("logged in user: " + smartITUserData.get("UserName"));
			context.setAttribute("DRSQA_11379_Driver", smartITdriver);
			context.setAttribute("DRSQA_11379_Product", Product.SmartIT);
	
			log.info("**** Step2:- verify smartIt page opened successfully ********");
			smartITFeatureMethods = new SmartITFeatureMethodsPage();
			smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
			
			log.info("Step 2: Access the calender through smartit");
			smartITFeatureMethods.navigateToCalendar();
	
			log.info("Step 3: Create Change Freeze");
			smartITFeatureMethods.navigateToChangeFreeze();
			smartITFeatureMethods.fillScheduleDatesInChangeFreeze(0, 2); 
			smartITFeatureMethods.fillScheduleStartTimeAndEndTime(smartITTestData.get("StartTime"),smartITTestData.get("EndTime"));
			smartITFeatureMethods.setDropDownValues(SolQAConstants.COMPANY,smartITTestData.get("CompanyValue"));
			smartITFeatureMethods.setDropDownValues(SolQAConstants.TYPE,smartITTestData.get("TypeValue"));
			smartITFeatureMethods.setDropDownValues(SolQAConstants.BASED_ON,smartITTestData.get("BasedOnValue"));
			smartITFeatureMethods.setDropDownValues(SolQAConstants.REGION,smartITTestData.get("RegionValue"));
			smartITFeatureMethods.setDropDownValues(SolQAConstants.SITE_Group,smartITTestData.get("SiteGroupValue"));
			smartITFeatureMethods.setDropDownValues(SolQAConstants.SITE,smartITTestData.get("SiteValue"));
			smartITFeatureMethods.setSummaryInChangeFreeze(smartITTestData.get("SummaryValue"));
			smartITFeatureMethods.waitFor10SecondsToChangeFreeze();
			smartITFeatureMethods.saveChangeFreeze();
	
			log.info("Step 4: Create Change Request during Freeze Period");
			smartITFeatureMethods.navigateToCreateNewChange();
			smartITFeatureMethods.setSummary(smartITTestData.get("ChangeTitle"));
			smartITFeatureMethods.setChangeLocation(smartITTestData.get("ChangeLocation"));
			smartITFeatureMethods.fillScheduleDates(0, 2);
			smartITFeatureMethods.waitFor10SecondsToChangeFreeze();
			smartITFeatureMethods.setRiskLevel(smartITTestData.get("RiskLevel"));
			smartITFeatureMethods.saveChange();
			Assert.assertTrue(commonWaits.isElementPresent(smartITFeatureMethods.getChangeFreezeErrorMessage()), "Conflict message not displayed for change during freeze period");
			log.info("Conflict message displayed for change during freeze period successfully");
			
		 }
		 finally {
			ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(ITSMdriver);
			context.setAttribute("DRSQA_11379_Driver", ITSMdriver);
			context.setAttribute("DRSQA_11379_Product", Product.Helix_ITSM);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
			log.info("logged in user: " + configProp.get("adminUserName"));        
			log.info("****** Setup  HELIXGPT_ASSISTANT_SERVICE_URL ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			itsmFeatureMethods.navigateToCentralizedConfiguration();
			itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SMARTIT);
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_CHANGE_FREEZE, "false");
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_CALENDAR, "false");
		 }
			
		log.info("****** END: [25.2.00] Create Change Freeze From IS Calendar ************************");
	}

	@Test(description = "[Bmcops] Creating Incident with PV", groups = {"ZDT", "MAT", "SmartIT" })
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11801", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11801_createIncidentInSmartIT(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11801_createIncidentInSmartIT *** START");

		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11801_Driver", getDriver());
		context.setAttribute("DRSQA_11801_Product", Product.Helix_ITSM);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"bmcopssupportagent");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateIncident_DRSQA_11801");

		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("**** Navigating to Create New Incident ********");
		smartITFeatureMethods.navigateToCreateNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_navigatedToCreateIncident", SolQAConstants.appSmartIT);

		smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_incidentCreated", SolQAConstants.appSmartIT);
		log.pass("************ created incident is: " + incidentID);

		log.info("**** Verifying Incident Data ********");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"VerifyIncident_DRSQA_11801");
		smartITFeatureMethods.verifyIncidentData(smartITTestData);

		log.info("**** Calling testData for Create Incident Again ********");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateIncident_DRSQA_11801");

		log.info("**** Changing status to Pending ********");
		smartITFeatureMethods.updateIncidentStatus(smartITTestData.get("PendingStatus"),
				smartITTestData.get("PendingStatusReason"), "");
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.verifyStatusViewPage(smartITTestData.get("PendingStatus"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_statusChangedToPending", SolQAConstants.appSmartIT);

		log.info("**** Changing status to Resolved ********");
		smartITFeatureMethods.updateIncidentStatus(smartITTestData.get("ResolvedStatus"),
				smartITTestData.get("ResolvedStatusReason"), smartITTestData.get("ResolvedResolutionNote"));
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.verifyStatusViewPage(smartITTestData.get("ResolvedStatus"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_statusChangedToResolved", SolQAConstants.appSmartIT);

		log.info("**** Changing status to Closed ********");
		smartITFeatureMethods.updateIncidentStatus(smartITTestData.get("ClosedStatus"),
				smartITTestData.get("ClosedStatusReason"), smartITTestData.get("ClosedResolutionNote"));
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.verifyStatusViewPage(smartITTestData.get("ClosedStatus"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_statusChangedToClosed", SolQAConstants.appSmartIT);

		log.info("--------- DRSQA_11801_createIncidentInSmartIT *** END");
	}

	@Test(description = "Create Incident Using Incident Template With Task Templates", groups = {"Regression_P1", "SmartIT", "ITSM","gsutar" })
	@SolqaDetails(authorName = "gsutar", TTID = "2495", JiraID = "DRSQA-11545", Products = { Product.Helix_ITSM,Product.SmartIT })
	public void DRSQA_11545_Create_Incident_Using_Incident_Template_With_Task_Templates(ITestContext context)throws Exception {
		log.info("--------- DRSQA_11545_createIncidentFromTemplateWithTasks_InSmartIT *** START");

		log.info(" ------ Enabling Progressive view Settings ---------------");
		itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11545_Driver", itsmDriver);
        context.setAttribute("DRSQA_11545_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.setSelfService_PV(true, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

		log.info("------ Step 1: Log into Smart IT as an Incident User with login id 'ipiattiUser' ------");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11545_Driver", smartITdriver);
		context.setAttribute("DRSQA_11545_Product", Product.SmartIT);
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ipiatti");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11545_Data");
		log.info("Login to SmartIT with " + smartITUserData.get("UserName"));
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        log.info("------ Step 2: In the navigation bar, select Create New -> Incident. ------");
		smartITFeatureMethods.navigateToCreateNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_11545_navigatedToCreateIncident", SolQAConstants.appSmartIT);

		log.info("------ Step 3: Fill in Customer the details and Select Template ------");
		smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethods.saveIncident();

		log.info("------ Step 4: Click on Save Ticket and Copy the Incident Number ------");
		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.closeBrowser();
	
		log.info("------ Step 5: Now Login into ITSM with same Incident User------");
		itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(itsmDriver);
		context.setAttribute("DRSQA_11545_Driver", itsmDriver);
		context.setAttribute("DRSQA_11545_Product", Product.Helix_ITSM);
		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();
		itsmFeatureMethods.verifyITSMLogin();

		log.info("------ Step 6: Go to Incident Management console and Search the Incident created in last step in Smart IT ------");
        itsmFeatureMethods.searchIncident(incidentID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11545_ITSMIncidentOpened", SolQAConstants.appITSM);

		 log.info("-------Step 7: Verify the incident was created using the template and has 2 task templates.--------");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11545_Data");
	    String taskStatus = smartITTestData.get("TaskStatus");

	    Incident incident = new Incident();
	    Assert.assertTrue(incident.isTaskWithStatusPresent(smartITTestData.get("Task1"), taskStatus),
			"Task '" + smartITTestData.get("Task1") + "' with status '" + taskStatus + "' not found on incident.");
	    Assert.assertTrue(incident.isTaskWithStatusPresent(smartITTestData.get("Task2"), taskStatus),
			"Task '" + smartITTestData.get("Task2") + "' with status '" + taskStatus + "' not found on incident.");

		log.info("--------- DRSQA_11545_Create_Incident_Using_Incident_Template_With_Task_Templates *** END");
	}

	@Test(description = "Create work order for customer with/without login id", groups = { "Regression_P3", "SmartIT","gsutar" })
	@SolqaDetails(authorName = "gsutar", TTID = "8570", JiraID = "DRSQA-11596", Products = { Product.SmartIT })
	public void DRSQA_11596_CreateWorkorderForCustomerWithWithOutLoginId(ITestContext context) throws Exception {
		log.info("--------- DRSQA_11596_CreateWorkorderForCustomerWithWithOutLoginId *** START");
		log.info("--------Step1:- Login to SmartIT");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11596_TestData");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		context.setAttribute("DRSQA_11596_Driver", getDriver());
		context.setAttribute("DRSQA_11596_Product", Product.SmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("--------Step2:- Navigate to Create New Workorder");
		smartITFeatureMethods.navigateToCreateNewWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11596_navigatedToWorkorder", SolQAConstants.appSmartIT);

		log.info("--------Step3 and 4:- click on '+' next to customer field and create new customer without entering login id and click on save");
		smartITFeatureMethods.clickOnCreateNewCustomerButton();
		smartITFeatureMethods.fillNewCustomerDetailsAndSave(smartITTestData);

		log.info("--------Step5:- submit workorder and check if all fields are populated as per entered data");
		smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethods.saveWorkorder();
		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
        Assert.assertNotNull(workorderID, "Workorder ID is null");
        smartITFeatureMethods.verifyWorkorderData(smartITTestData);

		log.info("--------Step6:- Again create a new workorder but this time with customer having valid login id");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11596_TestData_2");
		smartITFeatureMethods.clickOnDismissAlerts();
		try {
        smartITFeatureMethods.navigateToCreateNewWorkorder();
		}
		catch (Exception | AssertionError e ) {
			log.error("Error navigating to create new workorder: " + e.getMessage());
			smartITFeatureMethods.navigateRequiredSmartITURL(SolQAConstants.SMARTIT_CREATE_WORKORDER_URL);
		}
        smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethods.saveWorkorder();
		workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
        Assert.assertNotNull(workorderID, "Workorder ID is null");
        smartITFeatureMethods.verifyWorkorderData(smartITTestData);
        smartITFeatureMethods.closeBrowser();

		log.info("--------Step7:- Login to ITSM as an Administrator and open CTM:People form");
		itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_16133_Driver", getDriver());
        context.setAttribute("DRSQA_16133_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11596_TestData");   
        itsmFeatureMethods.verifyCreatedUserIsExistsWithoutLoginID(smartITTestData, configProp.get("ARServerAlias"));
		itsmDriver.quit();

		log.info("--------- DRSQA_11596_CreateWorkorderForCustomerWithWithOutLoginId *** END");
	}

	@Test(description = "[Bmcops] Creating Workorder with PV", groups = { "ZDT","MAT", "SmartIT" })
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11802", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11802_creatingWorkorderInSmartIT(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11802_creatingWorkorderInSmartIT *** START");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "WorkOrderRequiredData_DRSQA_11802");

		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		context.setAttribute("DRSQA_11802_Driver", getDriver());
		context.setAttribute("DRSQA_11802_Product", Product.Helix_ITSM);

		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("**** Navigating to Create New Workorder ********");
		smartITFeatureMethods.navigateToCreateNewWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_navigatedToWorkorder", SolQAConstants.appSmartIT);

		log.info("************ Create Work order with Existing Customer ************");
		smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
		smartITFeatureMethods.clickAssigntoMeInWorkorderCreatePage();
		smartITFeatureMethods.saveWorkorder();

		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_workorderCreated", SolQAConstants.appSmartIT);
		log.pass("************ created workorder is: " + workorderID);

		log.info("**** Changing status to Waiting Approval ********");
		smartITFeatureMethods.updateWorkorderStatus(smartITTestData.get("WaitingApproval"));
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_statusChangedToWaitingApproval", SolQAConstants.appSmartIT);

		log.info("**** Changing status to Planning ********");
		smartITFeatureMethods.updateWorkorderStatus(smartITTestData.get("Planning"));
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_statusChangedToPlanning", SolQAConstants.appSmartIT);

		log.info("**** Changing status to In Progress ********");
		smartITFeatureMethods.updateWorkorderStatus(smartITTestData.get("InProgress"));
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_statusChangedToInProgress", SolQAConstants.appSmartIT);

		log.info("**** Changing status to Completed ********");
		smartITFeatureMethods.updateWorkorderStatus(smartITTestData.get("Completed"));
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_statusChangedToCompleted", SolQAConstants.appSmartIT);

		log.info("**** Changing status to Closed ********");
		smartITFeatureMethods.updateWorkorderStatus(smartITTestData.get("Closed"));
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11802_statusChangedToClosed", SolQAConstants.appSmartIT);

		log.info("---------** End ** DRSQA_11802_creatingWorkorderInSmartIT ***");
	}

	@Test(description = "[Bmcops] Creating Change with PV", groups = { "MAT", "SmartIT" })
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11803", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11803_createChangeInSmartIT(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11803_createChangeInSmartIT *** START");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"bmcopssupportagent");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateChangeRequest_DRSQA_11803");

		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		context.setAttribute("DRSQA_11803_Driver", getDriver());
		context.setAttribute("DRSQA_11803_Product", Product.Helix_ITSM);

		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("**** Navigating to Create New Change ********");
		smartITFeatureMethods.navigateToCreateNewChange();
		smartITFeatureMethods.captureScreenshot("DRSQA_11803_navigatedToCreateChange", SolQAConstants.appSmartIT);

		smartITFeatureMethods.FillChangeTemplate(smartITTestData);
		smartITFeatureMethods.clickAssigntoMeInCreateChangePage();
		smartITFeatureMethods.saveChange();

		String changeID = smartITFeatureMethods.FetchChangeTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11803_changeCreated", SolQAConstants.appSmartIT);
		log.pass("************ created change is: " + changeID);
	}

    @Test(description = "[PWA] Create change on smartIT and verify create/view page with enabled progressive view", groups = {"Regression_P3", "SmartIT" })
    @SolqaDetails(authorName = "gpreethi", TTID = "7402", JiraID = "DRSQA-11610", Products = { Product.SmartIT })
    public void DRSQA_11610_CreationOfChangeRequest_PV(ITestContext context) throws Exception {
        log.info("------- START: DRSQA_11610_CreationOfChangeRequest_PV");
        String arID;

        // Precondition: Enable PV in ITSM
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        context.setAttribute("DRSQA_11610_Driver", ITSMdriver);
        context.setAttribute("DRSQA_11610_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "hannah_admin");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] { SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*" };
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_PROGRESSIVE_VIEWS, SolQAConstants.TRUEALPHABET);
        log.info("enabled the Progressive View (Progressive View) ");
        itsmFeatureMethods.closeBrowser();

        // Step 1: Login to SmartIT PV and navigate to Create Change
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "canandaUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11610_addCommentsInActivity");

        smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11610_Driver", smartITdriver);
        context.setAttribute("DRSQA_11610_Product", Product.SmartIT);

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethods.navigateToCreateNewChange();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        smartITFeatureMethods.captureScreenshot("DRSQA_11610_navigatedToCreate_Change", SolQAConstants.appSmartIT);

        String changeCreateUrl = getDriver().getCurrentUrl();
        log.info("--- Current URL for change creation - " + changeCreateUrl);
        Assert.assertTrue(changeCreateUrl.contains("changePV"), "Created change is not opened in progressive view. Expected '" + "changePV" + "' but found: " + changeCreateUrl);

        // Step 2: Create Change request
        Map<Object, String> baseChangeData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11610_addCommentsInActivity");
        Map<Object, String> changeData = new HashMap<>(baseChangeData);
        changeData.put("Change Title", "DRSQA-11610 Change " + RandomStringUtils.randomAlphabetic(6));

        smartITFeatureMethods.FillChangeTemplate(changeData);
        smartITFeatureMethods.clickAssigntoMeInCreateChangePage();
        smartITFeatureMethods.saveChange();

        arID = smartITFeatureMethods.FetchChangeTicketID();
        log.info("**** Created change id is: " + arID);
        smartITFeatureMethods.captureScreenshot("DRSQA_11610_newChangeCreated", SolQAConstants.appSmartIT);

        // Step 3: Add notes in activity (private/public + mention)
        smartITFeatureMethods.addNoteOnChange(smartITTestData.get("privateNote"), false);
        smartITFeatureMethods.verifyNoteOnChange(smartITTestData.get("privateNote"));

        smartITFeatureMethods.addNoteWithPersonMentionOnChange(smartITTestData.get("personName"), smartITTestData.get("personNote"), false);
        smartITFeatureMethods.verifyNoteOnChange(smartITTestData.get("personNote"));

        smartITFeatureMethods.addNoteOnChange(smartITTestData.get("publicNote"), true);
        smartITFeatureMethods.verifyNoteOnChange(smartITTestData.get("publicNote"));
        smartITFeatureMethods.captureScreenshot("DRSQA_11610_addedNotesOnActivity", SolQAConstants.appSmartIT);

        // Step 4: Relate a configuration item
        smartITFeatureMethods.relateCIToChange(smartITTestData.get("CIValue"), smartITTestData.get("RelationshipType"));
        smartITFeatureMethods.captureScreenshot("DRSQA_11610_AddedRelatedConfigurationItem", SolQAConstants.appSmartIT);

        smartITdriver.close();
        log.info("------- END: DRSQA_11610_CreationOfChangeRequest_PV");
    }

    @Test(description = "[PWA] Verify Edit page for change management with enabled progressive view", groups = {"Regression_P3", "SmartIT" })
    @SolqaDetails(authorName = "gpreethi", TTID = "7404", JiraID = "DRSQA-11612", Products = { Product.Helix_ITSM, Product.SmartIT })
    public void DRSQA_11612_CreationOfChangeAndVerifyInSmartIT_PV(ITestContext context) throws Exception {
        log.info("------- START: DRSQA_11612_CreationOfChangeAndVerifyInSmartIT_PV");
        String arID;

        // Precondition: Enable PV in ITSM
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] { SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*" };
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_PROGRESSIVE_VIEWS, SolQAConstants.TRUEALPHABET);
        log.info("enabled the Progressive View (Progressive View) ");
        itsmFeatureMethods.closeBrowser();

        // Step 1: Login to SmartIT PV and create a Change
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "creyUser");
        smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11612_Driver", smartITdriver);
        context.setAttribute("DRSQA_11612_Product", Product.SmartIT);

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethods.navigateToCreateNewChange();
        String changeCreateUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(changeCreateUrl.contains("changePV"), "Create change page is not opened in PV. Expected '" + "changePV" + "' but found: " + changeCreateUrl);

        Map<Object, String> baseChangeData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "CreateChangeRequest_DRSQA_11803");
        Map<Object, String> changeData = new HashMap<>(baseChangeData);
        changeData.put("Change Title", "DRSQA-11612 Change" + RandomStringUtils.randomAlphabetic(6));

        smartITFeatureMethods.FillChangeTemplate(changeData);
        smartITFeatureMethods.clickAssigntoMeInCreateChangePage();
        smartITFeatureMethods.saveChange();
        arID = smartITFeatureMethods.FetchChangeTicketID();

        // Step 2: Navigate to Ticket Console and open created Change in PV view
        smartITFeatureMethods.navigateToTicketConsole();
        smartITFeatureMethods.captureScreenshot("DRSQA_11612_navigatedToConsole", SolQAConstants.appSmartIT);
        smartITFeatureMethods.verifyUserIsNavigatedToTicketConsole();
        smartITFeatureMethods.applyFilterAndOpenTicket("Change Request", arID);
        smartITFeatureMethods.captureScreenshot("DRSQA_11612_changeRequestOpen", SolQAConstants.appSmartIT);

        String changeViewUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(changeViewUrl.contains("changePV"), "Opened change is not in PV view. Expected '" + "changePV" + "' but found: " + changeViewUrl);
        Assert.assertEquals(smartITFeatureMethods.FetchChangeTicketID(), arID, "Opened Change ID mismatch.");

        // Step 3: Verify Edit page (PV) and Add task button presence
        smartITFeatureMethods.clickEditOnChange();
        String changeEditUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(changeEditUrl.contains("changePV"), "Edit change is not opened in PV. Expected '" + "changePV" + "' but found: " + changeEditUrl);
        Assert.assertTrue(smartITFeatureMethods.isAddTaskButtonPresentOnChange(), "Add task button is not present on Change edit page.");
        smartITFeatureMethods.captureScreenshot("DRSQA_11612_changeEditOpen", SolQAConstants.appSmartIT);
        smartITFeatureMethods.closeBrowser();
        log.info("------- END: DRSQA_11612_CreationOfChangeAndVerifyInSmartIT_PV");
    }

	@Test(description = "ITSM-insight job creation.", groups = { "MAT", "SmartIT" })
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11818", Products = { Product.SmartIT, Product.ITSMInsight })
	public void DRSQA_11818_ITSM_Insight_One_Time_Job(ITestContext context) throws Exception {

		log.info("*************** START: DRSQA_11818_ITSM_insightJobCreation *********************");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");

		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		context.setAttribute("DRSQA_11818_Driver", getDriver());
		context.setAttribute("DRSQA_11818_Product", Product.Helix_ITSM);

		smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Step 2: Go to More >> Workspaces and click on 'Proactive problem management'");
		smartITFeatureMethods.navigateToProactiveProblemManagement();
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_navigateToProactiveProblemManagement", SolQAConstants.appSmartIT);

		log.info("Step 3: Now go to Manage Jobs.");
		smartITFeatureMethods.navigateToManageJobsITSMInsights();
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_navigateToManageJobsITSMInsights", SolQAConstants.appSmartIT);

		String jobName = "Test_Solqa_" + RandomStringUtils.randomAlphabetic(10);
		log.info("Step 4 and 5 : click on Create one-time job and fill all required fields.Select previous weeks start data and recent week end date.And then Run the job.");
		smartITFeatureMethods.createOneTimeJob(jobName);
		log.info("Job name is : " + jobName);
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_createOneTimeJob", SolQAConstants.appSmartIT);

		log.info("Step 6: Refresh the page until job status would be Successfully.");
		smartITFeatureMethods.verifyJobStatus(jobName);
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_verifiedJobStatus", SolQAConstants.appSmartIT);
		log.info("------- END: DRSQA_11818_ITSM_insightJobCreation ---------------------- ");
	}

	// Previous owner sonalsha
	@Test(description = "[Bmcops]Smart Recorder PV Test	", groups = { "MAT", "SmartIT" })
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11804", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11804_smartRecorderInSmartIT(ITestContext context) throws Exception {

		log.info("--------- DRSQA_11804_smartRecorderInSmartIT *** START");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"bmcopssupportagent");

		log.info("---------Step1:- Login to smartIT PV---------");
		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		context.setAttribute("DRSQA_11804_Driver", getDriver());
		context.setAttribute("DRSQA_11804_Product", Product.Helix_ITSM);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"SmartRecorder_DRSQA_11804");

		log.info(
				"-------Step2:- Click on SmartRecorder and type @bmcopsenduser BMCOps Approval Service 1.0--------------");
		log.info("**** Navigating to Smart Recorder ********");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11804_navigatedToSmartRecorder", SolQAConstants.appSmartIT);
		log.info("---------Enter DPW end user ------------------------");
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(smartITTestData.get("BMCOpsEndUser"));
		String ServiceName = smartITTestData.get("ServiceName");
		log.info("---- Enter service name " + ServiceName);
		smartITFeatureMethods.enterStringInSmartRecorder(smartITTestData.get("ServiceName"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11804_selectedCustomerInSmartRecorder",
				SolQAConstants.appSmartIT);
		log.info("----- Select service from recommended template section -------");
		smartITFeatureMethods.selectRecommendedDWPTemplate(smartITTestData);

		log.info(
				"---------Step3:-  Select BMCOps Approval Service 1.0 and click on create service request.--------------");
		log.info("----------Click on Create Service Request button ----------");
		smartITFeatureMethods.clickCreateServiceRequestButton();
		smartITFeatureMethods.captureScreenshot("DRSQA_11804_DWPServiceRequestInSmartIT", SolQAConstants.appSmartIT);
		log.info("---Submit service request ----------------");
		smartITFeatureMethods.clickSubmitRequestButton();
		smartITFeatureMethods.captureScreenshot("DRSQA_11804_ServiceRequestSubmittedInSmartIT",
				SolQAConstants.appSmartIT);
		String dwpRequestID = smartITFeatureMethods.FetchDWPRequestID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11804_DWPRequestID", SolQAConstants.appSmartIT);
		log.pass("************ created incident is: " + dwpRequestID);

		log.info("****** Open Service request from DWP ************** ");
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
		DWPdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(DWPdriver);
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
		DWPFeatureMethods dwpFeatureMethods = new DWPFeatureMethods();
		dwpFeatureMethods.openSrmRequestByRequestID(dwpRequestID);
        dwpFeatureMethods.verifySrdTitle(smartITTestData.get("DWPServiceName"));
        dwpFeatureMethods.captureScreenshot("DRSQA_11804_CreatedRequestIDInDWPPage", SolQAConstants.appDWP);

        log.info("------------ Step 4: Verify request is created with status Waiting for Approval ----------- ");
        dwpFeatureMethods.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);
        dwpFeatureMethods.captureScreenshot("DRSQA_11804_RequestStatusWaitingForApproval", SolQAConstants.appDWP);
        log.pass("Request status verified as Waiting for Approval");

        log.info("------------- Step 5: Verify Approval yellow banner  with This request is not yet approved by all required parties.-----------");
        smartITFeatureMethods.switchToSmartITPage(smartITdriver);
        smartITFeatureMethods.refreshSmartITPage();
        Assert.assertTrue(smartITFeatureMethods.verifyYellowApprovalBannerPresent(), "Yellow approval banner is not present in SmartIT for DWP request");
        log.pass("Yellow approval banner is verified successfully");

        log.info("-------------- Step 6: Click on show approver and verify status ------------");
        smartITFeatureMethods.clickOnBannerShowApprovers();
        smartITFeatureMethods.captureScreenshot("DRSQA_11804_ShowApproversClicked", SolQAConstants.appSmartIT);
        log.pass("Show approvers clicked and status displayed");

        log.info("-------------- Step 7: Click on fullfillment ID from bottom and verify Approval list panel ************** ");
        smartITFeatureMethods.refreshSmartITPage();
        smartITFeatureMethods.clickOnFulfillmentIDHyperLink();
        Assert.assertTrue(smartITFeatureMethods.verifyApprovalIsAvailable(smartITTestData.get("ApprovalPersonFullName")), "Approval is not available in the approval list panel");
        smartITFeatureMethods.captureScreenshot("DRSQA_11804_ApprovalListPanelVerified", SolQAConstants.appSmartIT);
        log.pass("Approval list panel verified successfully");

        log.info("------------- Step 8 Login to DWP with approver ----------------- ");
        DWPdriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(DWPdriver);
		dwpFeatureMethod = new DWPFeatureMethods();
		readJson = new ReadJson();
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers",
				"bmcOpsManagerUser");
		log.info("Login to DWP with " + dwpUserData.get("UserName"));
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

		log.info("------------- Step 9:-  Open the request created in step 4 from my activity--------------- ");
		dwpFeatureMethod.openActivityPage();
		dwpFeatureMethod.openSrmRequestByRequestID(dwpRequestID);

		log.info("------------- Step 10:- Enduser approver the request the request goes in progress --------------- ");
		dwpFeatureMethod.clickApproveRequestInEventDetails();
		dwpFeatureMethod.refreshDWPPage();
		dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
		dwpFeatureMethod.captureScreenshot("DRSQA_11810_requestListedOnMyActivity", SolQAConstants.appDWP);
		DWPdriver.close();

		log.info("------------- Step 11:- Login to smartIT and verify the workorder created --------------- ");
		smartITFeatureMethods.switchToSmartITPage(smartITdriver);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.refreshSmartITPage();
		String WorkOrderID = smartITFeatureMethods.getWorkOrderID();
		smartITFeatureMethods.clickOnWorkOrderIDHyperLink();
		smartITFeatureMethods.captureScreenshot("DRSQA_11804_WorkOrderDetailsOpenedInSmartIT", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethods.verifyWorkorderIDFromSmartITViewPage(WorkOrderID), "Workorder ID is not matching in SmartIT view page");
		smartITdriver.close();
		log.info("--------- DRSQA_11804_smartRecorderInSmartIT *** END");
	}

	@Test(description = "[25.3.00] Verify DORA - Additional fields on creating incident from SmartIT/PV (During major incident management)", groups = {"Regression_P1", "ITSM", "SmartIT", "sonalsha" })
	@SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-11286", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11286_VerifyDORAAdditionalFieldsOnCreatingIcidentFromSmartIT(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11286_Preconditions START - ITSM Form: HPD:CFG-Rules ***");

		// Use existing WebDriver from ThreadLocal
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_11286_Driver", getDriver());
		context.setAttribute("DRSQA_11286_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);

		log.info("--------- Initializing ITSMFeatureMethods handler");
		itsmFeatureMethods = new ITSMFeatureMethods();

		itsmFeatureMethods.LoadForm(SolQAConstants.HPD_CFG_RULES, SolQAConstants.DEFAULT,
				configProp.get("ARServerAlias"));
		itsmFeatureMethods.selectCompany(SolQAConstants.PETRAMCO_COMPANY);

		itsmFeatureMethods.advanceMajorIncidentSettings();

		log.info("--------- DRSQA_11286_Preconditions END ***");

		log.info("--------- DRSQA_11286_createIncidentInSmartIT *** START");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"ppanUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateIncident_DRSQA_11286");

		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11286_Driver", getDriver());
		context.setAttribute("DRSQA_11286_Product", Product.Helix_ITSM);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("**** Navigating to Create New Incident ********");
		smartITFeatureMethods.navigateToCreateNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_navigatedToCreateIncident", SolQAConstants.appSmartIT);

		smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11801_incidentCreated", SolQAConstants.appSmartIT);
		log.pass("************ created incident is: " + incidentID);

		log.info("Updating Major Incident status");
		smartITFeatureMethods.clickOnDropdownAndSelectOptionFromMajorIncident(smartITTestData.get("CandidateValue"));

		log.info("Verify Additional Field Values");
		smartITFeatureMethods.verifyAdditionalFields();

		log.info("Verify error message: Please fill in all mentioned below required Major Incident Candidate fields");
		smartITFeatureMethods.verifyErrorMessage();

		log.info("Fill Additional Field Values");
		smartITFeatureMethods.FillAdditionalFields(smartITTestData.get("optionYesValue"),
				smartITTestData.get("downTimeValue"));

		log.info("Fill Service CI");
		smartITFeatureMethods.FillServiceCI(smartITTestData.get("serviceValue"));

		log.info("Fill Impacted Area");
		smartITFeatureMethods.FillImpactedArea(smartITTestData.get("companyValue"));

		smartITFeatureMethods.saveIncidentVieIncidentPage();

		log.info("Verify Reputaional Impact from mail Icon");
		smartITFeatureMethods.verifyReputaionalImpact(smartITTestData.get("reputationalInput"));
	}

	@Test(description = "Create Contract from SmartIT/PV", groups = { "Regression_P1", "ITSM", "SmartIT", "sonalsha" })
	@SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-11283", Products = { Product.Helix_ITSM,
			Product.SmartIT })
	public void DRSQA_11283_CreateContractFromSmartIT(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11283_createContractInSmartIT *** START");
		
			log.info("Step 1: DRSQA-DRSQA_11283-requisite Steps: START *********");

			ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(ITSMdriver);
			context.setAttribute("DRSQA_11283_Driver", getDriver());
			context.setAttribute("DRSQA_11283_Product", Product.Helix_ITSM);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
			itsmFeatureMethods = new ITSMFeatureMethods();

			log.info("Step 1.1: Enabling IS Contract Console");
			itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDINGCONSOLE_DEFAULT_ADMINISTRATOR, SolQAConstants.DEFAULT,
					configProp.get("ARServerAlias"));
			itsmFeatureMethods.navigateToCentralizedConfiguration();
			String[] componentMenu = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
			itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_CONTRACT_CONSOLE,
					SolQAConstants.TRUE);

		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateContract_DRSQA_11283");

		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11283_Driver", getDriver());
		context.setAttribute("DRSQA_11283_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);

		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("**** Navigating to Create New Contract ********");
		smartITFeatureMethods.navigateToCreateNewContract();
		smartITFeatureMethods.captureScreenshot("DRSQA_11283_navigatedToCreateContract", SolQAConstants.appSmartIT);

		smartITFeatureMethods.FillContractTemplate(smartITTestData);
		smartITFeatureMethods.setCostCenter();
		smartITFeatureMethods.updateNotificationDate();
		smartITFeatureMethods.saveContract();

		String contractID = smartITFeatureMethods.FetchContractID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11283_ContractCreated", SolQAConstants.appSmartIT);
		log.pass("************ created contract ID is: " + contractID);

		log.info("**** Changing status to Historical ********");
		smartITFeatureMethods.updateContractStatus(smartITTestData.get("HistoricalStatus"));
		smartITFeatureMethods.saveContract();
		smartITFeatureMethods.verifyStatusViewPage(smartITTestData.get("HistoricalStatus"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11283_statusChangedToHistorical", SolQAConstants.appSmartIT);

		log.info("**** Changing status to Executed ********");
		smartITFeatureMethods.updateContractStatus(smartITTestData.get("ExecutedStatus"));
		smartITFeatureMethods.saveContract();
		smartITFeatureMethods.verifyStatusViewPage(smartITTestData.get("ExecutedStatus"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11283_statusChangedToExecuted", SolQAConstants.appSmartIT);

		log.info("**** Relating CI ********");
		smartITFeatureMethods.relateCIToContract(smartITTestData.get("CIValue"),
				smartITTestData.get("RelationshipType"));

		log.info("--------- DRSQA_11283_createContractInSmartIT *** END");
		

	}

	//Disabling the usecase right now to avoid conflict with other usecases in SMARTIT (NAVBAR and ticketConsole related)
	@Test(description = "Verify ITSM-HKM integration inline view, flag, edit articles using iFrame", groups = {"Regression_P1", "SmartIT", "ITSM", "HKM", "sonalsha", "OneTime"}, enabled = false)
	@SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-11304", Products = { Product.Helix_ITSM, Product.SmartIT})
	public void DRSQA_11304_Verify_ITSM_HKM_integration(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11304_Verify_ITSM_HKM_integration *** START");

		try {
		log.info("Step 1: DRSQA-11304_Pre-requisite Steps: START *********");

		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_11304_Driver", getDriver());
		context.setAttribute("DRSQA_11304_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();

		log.info("Step 1.1: Loading ITSM Form: CFG:HKM:ConfigurationSettings and enabling HKM");
		itsmFeatureMethods.enableAndAddHKMDetails(configProp.get("hkmURL"), configProp.get("adminUserName"),
				configProp.get("adminPassword"), configProp.get("ARServerAlias"));

		log.info("Step 1.2: Enabling Shared Ticket Console and adding the ccs parameter enableHKMArticlePreview");
		itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDINGCONSOLE_DEFAULT_ADMINISTRATOR, SolQAConstants.DEFAULT,
				configProp.get("ARServerAlias"));
		itsmFeatureMethods.navigateToCentralizedConfiguration();
		String[] componentMenu = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
		itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE,
				SolQAConstants.TRUE);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_HKM_ARTICLE_PREVIEW,
				SolQAConstants.TRUE);
		itsmFeatureMethods.closeBrowser();

		log.info("Step 1: DRSQA-11304_Pre-requisite Steps : END *********");

		log.info("Step 2: DRSQA_11304_Login to SmartIT and search the knowledge article");

		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA_11304_Verify_ITSM_HKM_integration");

		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11304_Driver", getDriver());
		context.setAttribute("DRSQA_11304_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);

		log.info("Step 3: Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.isGptGlobalSearchPresent();

		log.info("Step 4: Navigating to ITSM Global Search");
		smartITFeatureMethods.navigateToHelixGPTGlobalSearch();
		smartITFeatureMethods.verifyGlobalSearchStudioPageLaunchedSuccessfully();
		smartITFeatureMethods.clickOnChatSearchButton(smartITTestData.get("SearchButton"));

		log.info("Step 5: Searching Knowledge Article");
		smartITFeatureMethods.selectTicketAndKnowledgeDropdown(smartITTestData.get("KnowledgeOption"));
		smartITFeatureMethods.searchRequestedFilter(smartITTestData.get("KnowledgeArticle"));

		log.info("Step 6: Verifying and clicking on searched Knowledge Article (List View)");
		smartITFeatureMethods.selectViewStyle(smartITTestData.get("ListView"));
		smartITFeatureMethods.verifyAndClickSearchedResult(smartITTestData.get("KnowledgeArticle"));
		smartITFeatureMethods.verifyResultPreviewListView(smartITTestData.get("KnowledgeArticle"));
		smartITFeatureMethods.clickOnViewFullResultListView(smartITTestData.get("articleLink"));
		smartITFeatureMethods.clearSearchedResult();

		log.info("Step 7: Navigating to Smart Recorder");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_11304_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

		log.info("Step 8: Enter ppan user");
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(smartITTestData.get("ppanUser"));

		String KnowledgeArticle = smartITTestData.get("KnowledgeArticle");
		log.info("Step 9: Enter Knowledge Article " + KnowledgeArticle);
		smartITFeatureMethods.enterStringInSmartRecorder(smartITTestData.get("KnowledgeArticle"));
		smartITFeatureMethods.captureScreenshot("DRSQA_11304_selectedCustomerInSmartRecorder",
				SolQAConstants.appSmartIT);

		log.info("Step 10: Select knowledge from recommended Knowledge section");
		smartITFeatureMethods.selectRecommendedKnowledge(smartITTestData.get("KnowledgeArticle"));

		log.info("Step 11: Verifying the knowledge preview Screen in SmartRecorder");
		smartITFeatureMethods.verifyKnowledgeArticlePreviewSmartRecorder(smartITTestData.get("KnowledgeArticle"));
		smartITFeatureMethods.clickOnViewFullArticleFromSmartRecorder(smartITTestData.get("articleLink"));

		log.info("Step 12: Navigating to Helix GPT Global Search");
		smartITFeatureMethods.navigateToHelixGPTGlobalSearch();
		smartITFeatureMethods.verifyGlobalSearchStudioPageLaunchedSuccessfully();
		smartITFeatureMethods.clickOnChatSearchButton(smartITTestData.get("SearchButton"));

		log.info("Step 13: Searching Knowledge Article");
		smartITFeatureMethods.selectTicketAndKnowledgeDropdown(smartITTestData.get("KnowledgeOption"));
		smartITFeatureMethods.searchRequestedFilter(smartITTestData.get("KnowledgeArticle"));

		log.info("Step 14: Verifying and clicking on searched Knowledge Article (Card View)");
		smartITFeatureMethods.selectViewStyle(smartITTestData.get("CardView"));
		smartITFeatureMethods.verifyAndClickSearchedResult(smartITTestData.get("KnowledgeArticle"));
		smartITFeatureMethods.verifyResultPreviewCardView(smartITTestData.get("KnowledgeArticle"));
		smartITFeatureMethods.clickOnViewFullResultCardView(smartITTestData.get("articleLink"));
		}
		finally {    
	            
	       log.info("Step 15: Disable the shared ticket console settings");
	       itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	       setDriver(itsmDriver);
	   
	       login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	       log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
	       itsmFeatureMethods = new ITSMFeatureMethods();
	    
	       itsmFeatureMethods.navigateToCentralizedConfiguration();
	       String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	       itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
	       itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
	       itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
	        
		}

		log.info("--------- DRSQA_11304_Verify_ITSM_HKM_integration *** END");

	}

    @Test(description = "[PPM] Ability for users to perform advanced analytics", groups = {"Regression_P1", "SmartIT", "ITSMInsight","gsutar","HelixDashboard"})
	@SolqaDetails(authorName = "gsutar",  JiraID = "DRSQA-12776", Products = {Product.SmartIT, Product.ITSMInsight})
	public void DRSQA_12776_AbilityForUsersToPerformAdvancedAnalytics(ITestContext context) throws Exception {

		log.info("*************** START: [PPM] Ability for users to perform advanced analytics *********************");
		log.info("****** Precondition:- Stack should be integrated with ADE **********");
		log.info("Step1: Login to SmartIT");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers","pbunyon");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData","DRSQA_12776_TestData");

		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12776_Driver", getDriver());
		context.setAttribute("DRSQA_12776_Product", Product.Helix_ITSM);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		log.info("SmartIT page launched successfully");
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Step 2: Click on more->Workspace->Proactive Problem management");
		smartITFeatureMethods.navigateToProactiveProblemManagement();
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_navigateToProactiveProblemManagement",
				SolQAConstants.appSmartIT);

		log.info("Step 3: Click on Manage Jobs and create a one time job and run now.");
		commonWaits.switchToLastWindow();
		smartITFeatureMethods.navigateToManageJobsITSMInsights();
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_navigateToManageJobsITSMInsights",
				SolQAConstants.appSmartIT);
		String jobName = "Test_Solqa_" + RandomStringUtils.randomAlphabetic(10);
		smartITFeatureMethods.createOneTimeJob(jobName);
		log.info("Created Job name is : " + jobName);
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_createOneTimeJob", SolQAConstants.appSmartIT);
		smartITFeatureMethods.verifyJobStatus(jobName);
		smartITFeatureMethods.captureScreenshot("DRSQA_11818_ITSM_verifiedJobStatus",SolQAConstants.appSmartIT);

        log.info("Step 4: Click on Advanced Analytics and verify the page is loaded successfully.");
        smartITFeatureMethods.navigateBackToProactiveProblemManagement();
        smartITFeatureMethods.selectJob(jobName);
        log.info("Selected job is: " + jobName);
        smartITFeatureMethods.navigateToViewAdvancedAnalytics();

        log.info("Step 5: On Proactive problem management Advanced Analytics verify chart based visualization");
        smartITFeatureMethods.verifyAdvancedAnalyticsDataPage();
        log.info("Advanced Analytics data page is verified successfully");
        smartITFeatureMethods.captureScreenshot("DRSQA_12776_AdvancedAnalyticsData", SolQAConstants.appSmartIT);

        log.info("Step 6: Select PPM Job Name and verify the incident for all Volume");
        smartITFeatureMethods.setFilters(smartITTestData.get("FirstFilterName"), jobName);
		smartITFeatureMethods.setFiltersAndSelectValue(smartITTestData.get("SecondFilterName"), smartITTestData.get("SecondFilterValue"));
        smartITFeatureMethods.setFiltersAndSelectValue(smartITTestData.get("ThirdFilterName"), smartITTestData.get(SolQAConstants.STATUS));
        smartITFeatureMethods.setFiltersAndSelectValue(smartITTestData.get("ForthFilterName"), smartITTestData.get("ForthFilterValue"));
      
		log.info("Selected filter is: " + smartITTestData.get("FirstFilterName") + " with value: " + jobName);
        Assert.assertTrue(smartITFeatureMethods.verifyIncidentDataAsPerFilter(smartITTestData.get("FirstFilterValue")),"Value not found for filter: " + smartITTestData.get("FirstFilterValue"));
        log.info("Verified incidents for filter: " + smartITTestData.get("FirstFilterValue"));
     
        log.info("*************** END: [PPM] Ability for users to perform advanced analytics *********************");

	}

	@Test(description = "[23.3.04] Create outage from ticket", groups = { "Regression_P1", "SmartIT", "ITSM", "sonalsha" })
	@SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-11767", Products = { Product.Helix_ITSM,
			Product.SmartIT })
	public void DRSQA_11767_Create_Outage_From_Ticket(ITestContext context) throws IOException {

		log.info("--------- DRSQA_11767_Create_Outage_From_Ticket *** START");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"pbunyon");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateIncident_DRSQA_11767");

		// Use existing WebDriver from ThreadLocal
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11767_Driver", getDriver());
		context.setAttribute("DRSQA_11767_Product", Product.Helix_ITSM);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("--------- DRSQA_11767_createOutageInIncidentInSmartIT ----");

		log.info("**** Navigating to Create New Incident ********");
		smartITFeatureMethods.navigateToCreateNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_11767_navigatedToCreateIncident", SolQAConstants.appSmartIT);

		smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11767_IncidentCreated", SolQAConstants.appSmartIT);
		log.pass("************ created incident is: " + incidentID);

		log.info("**** Relating CI ********");
		smartITFeatureMethods.relateCIToIncident(smartITTestData.get("CIValue"),
				smartITTestData.get("RelationshipType"));

		log.info("**** Creating Outage from Incident ********");
		smartITFeatureMethods.createRelatedOutage(smartITTestData.get("CIValue"),
				smartITTestData.get("RelationshipType"), smartITTestData.get("outageDescription"));

		log.info("**** Verifying Outage Created ********");
		smartITFeatureMethods.verifyOutageCreated(smartITTestData.get("CIValue"));

		log.info("--------- DRSQA_11767_createOutageInChangeInSmartIT ----");

		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateChangeRequest_DRSQA_11767");

		log.info("**** Navigating to Create New Change ********");
		smartITFeatureMethods.navigateToCreateNewChange();
		smartITFeatureMethods.captureScreenshot("DRSQA_11767_navigatedToCreateChange", SolQAConstants.appSmartIT);

		smartITFeatureMethods.FillChangeTemplate(smartITTestData);
		smartITFeatureMethods.saveChange();

		String changeID = smartITFeatureMethods.FetchChangeTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_11767_changeCreated", SolQAConstants.appSmartIT);
		log.pass("************ created change is: " + changeID);

		log.info("**** Relating CI ********");
		smartITFeatureMethods.relateCIToChange(smartITTestData.get("CIValue"),
				smartITTestData.get("RelationshipType"));

		log.info("**** Creating Outage from Incident ********");
		smartITFeatureMethods.createRelatedOutage(smartITTestData.get("CIValue"),
				smartITTestData.get("RelationshipType"), smartITTestData.get("outageDescription"));

		log.info("**** Verifying Outage Created ********");
		smartITFeatureMethods.verifyOutageCreated(smartITTestData.get("CIValue"));

		log.info("--------- DRSQA_11767_Create_Outage_From_Ticket *** END");

	}
	
	//Disabling the usecase right now to avoid conflict with other usecases in SMARTIT (NAVBAR and ticketConsole related)
	@Test(description = "[25.4.00] Verify ITSM-] integration: Save & Resolve functionality for workorder drafts screen", groups = {"Regression_P2", "SmartIT", "ITSM", "sonalsha", "HKM", "OneTime"}, enabled = false)
	@SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-14658", Products = { Product.Helix_ITSM,
			Product.SmartIT })
	public void DRSQA_14658_Verify_ITSM_HKM_integration_Save_Resolve_functionality_for_workorder_drafts_screen(ITestContext context) throws IOException {

		log.info("** DRSQA_14658_Verify_ITSM_HKM_integration *** START");

		try {
		log.info("Step 1: DRSQA-14658_Pre-requisite Steps: START *********");

		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_14658_Driver", getDriver());
		context.setAttribute("DRSQA_14658_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();

		log.info("Step 1.1: Loading ITSM Form: CFG:HKM:ConfigurationSettings and enabling HKM");
		itsmFeatureMethods.enableAndAddHKMDetails(configProp.get("hkmURL"), configProp.get("adminUserName"),
				configProp.get("adminPassword"), configProp.get("ARServerAlias"));

		log.info("Step 1.2: Enabling Shared Ticket Console and adding the ccs parameter enableHKMArticlePreview");
		itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDINGCONSOLE_DEFAULT_ADMINISTRATOR, SolQAConstants.DEFAULT,
				configProp.get("ARServerAlias"));
		itsmFeatureMethods.navigateToCentralizedConfiguration();
		String[] componentMenu = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
		itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE,
				SolQAConstants.TRUE);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_HKM_ARTICLE_PREVIEW,
				SolQAConstants.TRUE);
		itsmFeatureMethods.closeBrowser();

		log.info("Step 1: DRSQA-14658_Pre-requisite Steps : END *********");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"ppanUser");

		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA_14658_Verify_ITSM_HKM_integration");

		log.info("Step 2: DRSQA_14658_Login to SmartIT to create the knowledge article with user:"
				+ smartITUserData.get("UserName"));

		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_14658_Driver", getDriver());
		context.setAttribute("DRSQA_14658_Product", Product.Helix_ITSM);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Step 3: Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.isGptGlobalSearchPresent();

		log.info("Step 4: Navigating to Knowledge Page and create Knowledge Article");
		smartITFeatureMethods.navigateToCreateNewKnowledge(smartITTestData.get("articleLink"));
		String knowledgeTitle = RandomStringUtils.randomAlphanumeric(5) + "_" + smartITTestData.get("KnowledgeTitle");
		smartITFeatureMethods.createKnowledge(knowledgeTitle);
		smartITFeatureMethods.captureScreenshot("DRSQA_14658_createdKnowledge", SolQAConstants.appSmartIT);
		log.info("Step 4: Created Knowledge Article with the user Peter Pan");

		log.info("Step 5: Login to SmartIT to search the knowledge article with admin user :"
				+ smartITUserData.get("UserName"));

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"pbunyon");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("Step 6: Navigating to Smart Recorder");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_14658_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

		log.info("Step 7: Entering End user:" + smartITTestData.get("endUser"));
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(smartITTestData.get("endUser"));

		log.info("Step 8: Enter Knowledge Article " + knowledgeTitle);
		smartITFeatureMethods.enterStringInSmartRecorder(knowledgeTitle);
		smartITFeatureMethods.captureScreenshot("DRSQA_14658_selectedCustomerInSmartRecorder",
				SolQAConstants.appSmartIT);

		log.info("Step 9: Select knowledge from recommended Knowledge section");
		smartITFeatureMethods.selectRecommendedKnowledge(knowledgeTitle);

		log.info("Step 10: Verifying the knowledge preview Screen in SmartRecorder");
		smartITFeatureMethods.verifyKnowledgeArticlePreviewSmartRecorder(knowledgeTitle);
		smartITFeatureMethods.clickOnViewFullArticleFromSmartRecorder(smartITTestData.get("articleLink"));

		log.info("Step 11: Validating Save To Ticket Button for workorder");
		smartITFeatureMethods.saveToTicket();

		log.info("Step 12: Validating Delete from Ticket Button for workorder");
		smartITFeatureMethods.deleteFromTicket();

		log.info("Step 13: Creating workorder and verifying Save and Resolve Button for workorder");
		smartITFeatureMethods.CreateTicketWorkorder();
		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_14658_workorderCreated", SolQAConstants.appSmartIT);
		log.pass("************ created workorder is: " + workorderID);
		}
		
		finally {    
            
		       log.info("Step 14: Disable the shared ticket console settings");
		       itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		       setDriver(itsmDriver);
		   
		       login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		       log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
		       itsmFeatureMethods = new ITSMFeatureMethods();
		    
		       itsmFeatureMethods.navigateToCentralizedConfiguration();
		       String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
		       itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
		       itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
		       itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
		        
			}

		log.info("*** DRSQA_11304_Verify_ITSM_HKM_integration *** END");
	}

	//Disabling the usecase right now to avoid conflict with other usecases in SMARTIT (NAVBAR and ticketConsole related)
	@Test(description = "[25.4.00] Verify ITSM-HKM integration: Save & Resolve functionality for Incident screen", groups = {"Regression_P2", "SmartIT", "ITSM", "sonalsha", "HKM", "OneTime"}, enabled = false)
	@SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-14657", Products = { Product.Helix_ITSM,
			Product.SmartIT })
	public void DRSQA_14657_Verify_ITSM_HKM_integration_Save_Resolve_functionality_for_incident_screen(ITestContext context) throws IOException {

		log.info("** DRSQA_14657_Verify_ITSM_HKM_integration *** START");

		try {
		log.info("Step 1: DRSQA-14657_Pre-requisite Steps: START *********");

		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_14657_Driver", getDriver());
		context.setAttribute("DRSQA_14657_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();

		log.info("Step 1.1: Loading ITSM Form: CFG:HKM:ConfigurationSettings and enabling HKM");
		itsmFeatureMethods.enableAndAddHKMDetails(configProp.get("hkmURL"), configProp.get("adminUserName"),
				configProp.get("adminPassword"), configProp.get("ARServerAlias"));

		log.info("Step 1.2: Enabling Shared Ticket Console and adding the ccs parameter enableHKMArticlePreview");
		itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDINGCONSOLE_DEFAULT_ADMINISTRATOR, SolQAConstants.DEFAULT,
				configProp.get("ARServerAlias"));
		itsmFeatureMethods.navigateToCentralizedConfiguration();
		String[] componentMenu = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
		itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE,
				SolQAConstants.TRUE);
		itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_HKM_ARTICLE_PREVIEW,
				SolQAConstants.TRUE);
		itsmFeatureMethods.closeBrowser();

		log.info("Step 1: DRSQA-14657_Pre-requisite Steps : END *********");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"ppanUser");

		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA_14657_Verify_ITSM_HKM_integration");

		log.info("Step 2: DRSQA_14657_Login to SmartIT to create the knowledge article with user:"
				+ smartITUserData.get("UserName"));

		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_14657_Driver", getDriver());
		context.setAttribute("DRSQA_14657_Product", Product.Helix_ITSM);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Step 3: Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.isGptGlobalSearchPresent();

		log.info("Step 4: Navigating to Knowledge Page and create Knowledge Article");
		smartITFeatureMethods.navigateToCreateNewKnowledge(smartITTestData.get("articleLink"));
		String knowledgeTitle = RandomStringUtils.randomAlphanumeric(5) + "_" + smartITTestData.get("KnowledgeTitle");
		smartITFeatureMethods.createKnowledge(knowledgeTitle);
		smartITFeatureMethods.captureScreenshot("DRSQA_14657_createdKnowledge", SolQAConstants.appSmartIT);
		log.info("Step 4: Created Knowledge Article with the user Peter Pan");

		log.info("Step 5: Login to SmartIT to search the knowledge article with admin user :"
				+ smartITUserData.get("UserName"));

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"pbunyon");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("Step 6: Navigating to Smart Recorder");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_14657_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

		log.info("Step 7: Entering End user:" + smartITTestData.get("endUser"));
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(smartITTestData.get("endUser"));

		log.info("Step 8: Enter Knowledge Article " + knowledgeTitle);
		smartITFeatureMethods.enterStringInSmartRecorder(knowledgeTitle);
		smartITFeatureMethods.captureScreenshot("DRSQA_14657_selectedCustomerInSmartRecorder",
				SolQAConstants.appSmartIT);

		log.info("Step 9: Select knowledge from recommended Knowledge section");
		smartITFeatureMethods.selectRecommendedKnowledge(knowledgeTitle);

		log.info("Step 10: Verifying the knowledge preview Screen in SmartRecorder");
		smartITFeatureMethods.verifyKnowledgeArticlePreviewSmartRecorder(knowledgeTitle);
		smartITFeatureMethods.clickOnViewFullArticleFromSmartRecorder(smartITTestData.get("articleLink"));

		log.info("Step 11: Validating Save To Ticket Button for incident");
		smartITFeatureMethods.saveToTicket();

		log.info("Step 12: Validating Delete from Ticket Button for incident");
		smartITFeatureMethods.deleteFromTicket();

		log.info("Step 13: Creating incident and verifying Save and Resolve Button for incident");
		smartITFeatureMethods.CreateTicketIncident();
		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_14657_incidentCreated", SolQAConstants.appSmartIT);
		log.pass("************ created incident is: " + incidentID);
		}
		
		finally {    
            
		       log.info("Step 14: Disable the shared ticket console settings");
		       itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		       setDriver(itsmDriver);
		   
		       login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		       log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
		       itsmFeatureMethods = new ITSMFeatureMethods();
		    
		       itsmFeatureMethods.navigateToCentralizedConfiguration();
		       String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
		       itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
		       itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
		       itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
		        
			}

		log.info("*** DRSQA_14657_Verify_ITSM_HKM_integration *** END");
	}

	@Test(description = "Automated Impact Analysis in change requests", groups = { "Regression_P2", "SmartIT", "ITSM", "sonalsha" })
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-12920", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_12920_AutomatedImpactAnalysis_ChangeRequest(ITestContext context) throws IOException {

		log.info("--------- DRSQA-12920: AutomatedImpactAnalysis_ChangeRequest *** START");

		log.info("Step 1: Load test data and user credentials");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"pbunyon");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"CreateChange_DRSQA_12920");

		log.info("Step 2: Launch SmartIT application and login with pbunyon user");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12920_Driver", getDriver());
		context.setAttribute("DRSQA_12920_Product", Product.Helix_ITSM);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("Step 3: Initialize SmartIT feature methods");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("Step 4: Create Change Request with automated impact analysis data");
		smartITFeatureMethods.createChange(smartITTestData);

		log.info("Step 5: Fetch Change Request ID and capture screenshot");
		String changeID = smartITFeatureMethods.FetchChangeTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_12920_Step5_changeCreated", SolQAConstants.appSmartIT);
		log.pass("************ Successfully created Change Request for Automated Impact Analysis: " + changeID);

		log.info("Step 6: Relate CI to Change Request");
		smartITFeatureMethods.relateCIToChange(smartITTestData.get("CI"), smartITTestData.get("AssociationType"));
		smartITFeatureMethods.captureScreenshot("DRSQA_12920_Step6_ciRelated", SolQAConstants.appSmartIT);

		log.info("Step 7: Change status from Draft to Request for Authorization");
		smartITFeatureMethods.updateChangeStatus(smartITTestData.get(SolQAConstants.STATUS));
		smartITFeatureMethods.captureScreenshot("DRSQA_12920_Step7_statusChangeAttempt", SolQAConstants.appSmartIT);
		smartITFeatureMethods.saveChange();

		log.info("Step 8: Verify automated impact analysis blocking message");
		smartITFeatureMethods.verifyImpactAnalysisBlockingMessage(smartITTestData.get("BlockingMessage"));
		smartITFeatureMethods.captureScreenshot("DRSQA_12920_Step8_blockingMessage", SolQAConstants.appSmartIT);

		log.info("--------- DRSQA-12920: AutomatedImpactAnalysis_ChangeRequest *** END");
	}
	
	//Disabling the usecase right now to avoid conflict with other usecases in SMARTIT (NAVBAR and ticketConsole related)
	@Test(description = "Network Impact Analysis for Change Request", enabled = false, groups = { "Regression_P2", "SmartIT", "ITSM" })
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11380", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11380_NetworkImpactAnalysis_ChangeRequest(ITestContext context)throws InterruptedException {

		log.info("--------- DRSQA-11380: NetworkImpactAnalysis_ChangeRequest *** START");
		try {

			log.info("(Assuming this is already done via pipeline) Precondition Step1:  Login to Innovation Studio > Navigate to the shared Service library and open the Application Configuration record data editor. > Search for the Configuration Name: ENABLE_NETWORK_IMPACT_ANALYSIS and Application ID: com.bmc.dsm.network-impact-analysis. > Set the Value field to \"true\". Also, ensure BULK_RELATE_ACTION_CI_COUNT has a display value of 100.");
			log.info("ADE should be integrated to execute the impact analysis.");
			log.info("Precondition Step2: AR System Administrator Console > Login to AR System Administrator and navigate to System → General → CCS configuration under com.bmc.arsys.itsm. > Check the following values: > EnableNetOps=true > Change_Impact_Analyzer_Option=both");
			context.setAttribute("DRSQA_11380_Driver", ITSMdriver);
			context.setAttribute("DRSQA_11380_Product", Product.Helix_ITSM);

			itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
	        itsmFeatureMethods = new ITSMFeatureMethods();
	        itsmFeatureMethods.verifyITSMLogin();
	        itsmFeatureMethods.navigateToCentralizedConfiguration();
	        String[] componentMenu = new String[] {SolQAConstants.ARSYSTEM_BMC_ARSYS_ITSM, "*"};
	        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_NET_OPS, SolQAConstants.TRUE);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.CHANGE_IMPACT_ANALYZER_OPTION, SolQAConstants.BOTH);


	        log.info("Precondition Step3:- Log in to ITSM and go to centralized configuration and enable the enableISNavbar, EnableISTicketConsole, enableISAssetConsole, enableSharedTicketConsole, PV enabled settings.");
	        String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_NAVBAR, SolQAConstants.TRUE);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE, SolQAConstants.TRUE);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.TRUE);
	        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));

	        log.info("Precondition Step4: Enable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
	        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
	        setDriver(smartITdriver);
	        context.setAttribute("DRSQA_13298_Driver", smartITdriver);
	        context.setAttribute("DRSQA_13298_Product", Product.Helix_SmartIT);
	        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
	        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
	        smartITFeatureMethods = new SmartITFeatureMethodsPage();
	        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
	        innovationSuiteFeatureMethodsPage.clickOnSettings();
	        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
	        innovationSuiteFeatureMethodsPage.enableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);

	        log.info("----------- Precondition Step5: .CMDB-DSM Sync Ensure Atrium Core → Configure Manager Dashboard → Configuration → SmartGraph Sync is scheduled with the Enable Schedule toggle ON. Set it to once and save it.    ----------");
	        log.info("----Login to ITSM application ----");
	        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	        setDriver(itsmDriver);
	        context.setAttribute("DRSQA_13298_Driver", itsmDriver);
	        context.setAttribute("DRSQA_13298_Product", Product.Helix_ITSM);

	        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
	        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13298_TestData");
	        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

	        log.info("**** Login to ITSM and cross launch CMDB from atrium core ****");
	        itsmFeatureMethods = new ITSMFeatureMethods();
	        itsmFeatureMethods.verifyITSMLogin();
	        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
	        cmdbFeatureMethods = new CMDBFeatureMethods();
	        
	        log.info("------- Atrium Core > Configuration Manager Dashboard >  CI > Create CI, provide required information and create CI--------");
	        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
	        cmdbFeatureMethods.verifyLoginTo_CMDB();
	        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem2"),cmdbTestData.get("MenuItem1"));
	        cmdbFeatureMethods.enableSmartGraphSyncSchedule();
	        cmdbFeatureMethods.setSmartGraphSyncSchedule();

			log.info("Step 1: Login to Smart IT with pbunyon user");
			smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
					"pbunyon");
			smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT,
					"SmartITTestData", "CreateChange_DRSQA_11380");

			smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
			setDriver(smartITdriver);
			context.setAttribute("DRSQA_11380_Driver", getDriver());
			context.setAttribute("DRSQA_11380_Product", Product.SmartIT);
			login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
			log.info("User logged in successfully: " + smartITUserData.get("UserName"));

			smartITFeatureMethods = new SmartITFeatureMethodsPage();
			smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
	        log.info("Smart IT page launched successfully");

			log.info("Step 2: Create a Change Request with Summary: Test NIA, Customer: pbunyon, Risk: 1");
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.navigateToCreateNewChangeRequest();
			
			log.info("Step 3: Fill Change Template");
			innovationSuiteFeatureMethodsPage.fillChangeTemplate(smartITTestData);
			
			log.info("Step 4: Save the change request");
	        innovationSuiteFeatureMethodsPage.saveChange();
	        
		}  finally{    
            log.info("Step 11: Disable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
            smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITdriver);
            context.setAttribute("DRSQA_13326_Driver", smartITdriver);
            context.setAttribute("DRSQA_13326_Product", Product.Helix_SmartIT);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            smartITFeatureMethods = new SmartITFeatureMethodsPage();
            smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
            innovationSuiteFeatureMethodsPage.clickOnSettings();
            innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
            innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
            smartITdriver.quit();
            
            log.info("Step 12: Disable the shared ticket console settings");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
    
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
            log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
            itsmFeatureMethods = new ITSMFeatureMethods();
    
            itsmFeatureMethods.navigateToCentralizedConfiguration();
            String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
            itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
            itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_NAVBAR, SolQAConstants.FALSE);
            itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE, SolQAConstants.FALSE);
            itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
            itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
        }
		log.info("--------- DRSQA-11380: NetworkImpactAnalysis_ChangeRequest *** END");
	}

	//Disabling the usecase right now to avoid conflict with other usecases in SMARTIT (NAVBAR and ticketConsole related)
	@Test(description = "IS Asset console to DSM(Discovery) Cross-launch", enabled = false, groups = { "Regression_P1", "SmartIT", "sonalsha"})
	@SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11649", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11649_IS_Asset_Console_to_DSM_CrossLaunch(ITestContext context) throws IOException {

		log.info("--------- DRSQA-11649: IS Asset Console to DSM Cross-launch *** START");
		try {
			log.info("Step 1: Precondition - Login to ITSM with admin user and enable IS Asset Console");
			ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(ITSMdriver);
			context.setAttribute("DRSQA_11649_Driver", getDriver());
			context.setAttribute("DRSQA_11649_Product", Product.Helix_ITSM);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
			itsmFeatureMethods = new ITSMFeatureMethods();

			log.info("Step 1.1: Navigate to Centralized Configuration and enable IS Asset Console");
			itsmFeatureMethods.navigateToCentralizedConfiguration();
			String[] componentMenu1 = new String[] { SolQAConstants.ITSM_SMARTIT_COMPONENT, "*" };
			itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE,
					SolQAConstants.TRUE);

			log.info("Step 1.2: Enable Shared Ticket Console");
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE,
					SolQAConstants.TRUE);

			log.info("Step 1.3: Navigate to Centralized Configuration and validate Helix Portal configuration");
			String[] componentMenu2 = new String[] { SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*" };
			itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu2);
			itsmFeatureMethods.validateCentralizedConfigurationNotEmpty(SolQAConstants.HELIX_PORTAL_URL);
			itsmFeatureMethods.validateCentralizedConfigurationNotEmpty(SolQAConstants.HELIX_PORTAL_TENANT_ID);
			log.pass("Validated that Helix-Portal-URL and Helix-Portal-Tenant-Id are not empty");
			itsmFeatureMethods.closeBrowser();

			log.info("Step 1.4: Launch Innovation Suite and enable Advanced Responsive Navigation");
			launchWeb(SolQAConstants.ITSM_APPLICATION, SolQAConstants.appIS);
			context.setAttribute("DRSQA_11649_Driver", getDriver());
			context.setAttribute("DRSQA_11649_Product", Product.Helix_ITSM);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			innovationSuiteFeatureMethodsPage.clickOnSettings();
			innovationSuiteFeatureMethodsPage.clickOnConfiguration("Common Configurations");
			innovationSuiteFeatureMethodsPage.enableConfiguration("Advanced Responsive Navigation");
			log.pass("Enabled Advanced Responsive Navigation in Innovation Suite");

			log.info("Step 1.5: Navigate to General configuration and set Discovery URL");
			innovationSuiteFeatureMethodsPage.clickOnConfiguration("General");
			innovationSuiteFeatureMethodsPage.setDiscoveryUrl(configProp.get("ADE_SMART_GRAPH_URL"));
			log.pass("Set Discovery URL: " + configProp.get("ADE_SMART_GRAPH_URL"));
			getDriver().close();
			log.info("Precondition End - All configurations completed successfully");

			log.info("Step 2: Load test data and launch SmartIT with admin user");
			readJson = new ReadJson();
			smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT,
					"SmartITTestData", "DRSQA_11649_AssetConsole");
			
			smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
			setDriver(smartITdriver);
			context.setAttribute("DRSQA_11649_Driver", getDriver());
			context.setAttribute("DRSQA_11649_Product", Product.Helix_ITSM);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
			log.info("Logged in with admin user: " + configProp.get("adminUserName"));

			log.info("Step 3: Initialize SmartIT feature methods and verify SmartIT launched successfully");
			smartITFeatureMethods = new SmartITFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			commonWaits.waitForURLContains(configProp.get("IS_URL"));
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step3_SmartITLaunched", SolQAConstants.appSmartIT);
			log.pass("SmartIT launched successfully with admin user");

			log.info("Step 4: Navigate to Asset Console using Innovation Suite method");
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.navigateToAssetConsole();
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step4_AssetConsoleSelected",
					SolQAConstants.appSmartIT);
			log.pass("Successfully navigated to Asset Console");

			log.info("Step 5: Clear search and apply filters - Asset Type: Business Service, Status: Deployed");
			innovationSuiteFeatureMethodsPage.applyFilterAndSearchAsset(smartITTestData.get("AssetType"),
					smartITTestData.get(SolQAConstants.STATUS));
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step5_FiltersApplied", SolQAConstants.appSmartIT);
			log.pass("Successfully applied filters: Asset Type=" + smartITTestData.get("AssetType") + ", Status="
					+ smartITTestData.get(SolQAConstants.STATUS));

			
			String CIName = smartITTestData.get("CIName");
			log.info("Step 9: Search for CI in search box and validate results" + smartITTestData.get("CIName"));
			innovationSuiteFeatureMethodsPage.searchCIInAssetConsole(CIName);
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step9_CISearched", SolQAConstants.appSmartIT);
			log.pass("Successfully searched for CI: " + CIName);

			log.info("Step 10: Validate CI in search results");
			innovationSuiteFeatureMethodsPage.validateCIInSearchResults(CIName);
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step10_CIValidated", SolQAConstants.appSmartIT);
			log.pass("Successfully validated CI: " + CIName + " in search results");

			log.info("Step 11: Click on three dots and select 'View related CI'");
			innovationSuiteFeatureMethodsPage.clickThreeDotsAndViewRelatedCI();
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step11_ViewRelatedCIClicked",
					SolQAConstants.appSmartIT);
			log.pass("Successfully clicked 'View related CI' option");

			log.info("Step 12: Validate 'Related CIs' modal is displayed");
			innovationSuiteFeatureMethodsPage.validateRelatedCIsModalDisplayed();
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step12_RelatedCIsModal", SolQAConstants.appSmartIT);
			log.pass("Successfully validated 'Related CIs' modal is displayed");

			log.info("Step 13: Validate three DSM cross-launch buttons are visible");
			innovationSuiteFeatureMethodsPage.validateThreeButtonsVisible();
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step13_ThreeButtons", SolQAConstants.appSmartIT);
			log.pass(
					"Successfully validated Dynamic Service Model, DSM Node Topology, and CI Explorer buttons are visible");

			log.info("Step 14: Click Dynamic Service Model button and validate Discovery page launched");
			innovationSuiteFeatureMethodsPage.clickDynamicServiceModelButton();
			innovationSuiteFeatureMethodsPage.validateDiscoveryPageLaunched(configProp.get("ADE_SMART_GRAPH_URL"));
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step14_DynamicServiceModel",
					SolQAConstants.appSmartIT);
			log.pass("Successfully validated Dynamic Service Model cross-launch to Discovery");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

			log.info("Step 15: Click DSM Node Topology button and validate Discovery page launched");
			innovationSuiteFeatureMethodsPage.clickDSMNodeTopologyButton();
			innovationSuiteFeatureMethodsPage.validateDiscoveryPageLaunched(configProp.get("ADE_SMART_GRAPH_URL"));
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step15_DSMNodeTopology", SolQAConstants.appSmartIT);
			log.pass("Successfully validated DSM Node Topology cross-launch to Discovery");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

			log.info("Step 16: Click CI Explorer button and validate CMDB Explorer Widget opened");
			innovationSuiteFeatureMethodsPage.clickCIExplorerButton();
			innovationSuiteFeatureMethodsPage.validateCMDBExplorerWidgetOpened("AtriumWidget");
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step16_CIExplorer", SolQAConstants.appSmartIT);
			log.pass("Successfully validated CI Explorer opened CMDB Explorer Widget");

		} finally {

			log.info("Step 17: Launch Innovation Suite and disable Advanced Responsive Navigation");
			launchWeb(SolQAConstants.ITSM_APPLICATION, SolQAConstants.appIS);
			context.setAttribute("DRSQA_11649_Driver", getDriver());
			context.setAttribute("DRSQA_11649_Product", Product.Helix_ITSM);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			innovationSuiteFeatureMethodsPage.clickOnSettings();
			innovationSuiteFeatureMethodsPage.clickOnConfiguration("Common Configurations");
			innovationSuiteFeatureMethodsPage.disableConfiguration("Advanced Responsive Navigation");
			smartITFeatureMethods.captureScreenshot("DRSQA_11649_Step17_DisabledAdvancedResponsiveNavigation", SolQAConstants.appIS);
			log.pass("Disabled Advanced Responsive Navigation in Innovation Suite");

			log.info("Step 18: Disable the shared ticket console settings");
	        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	        setDriver(itsmDriver);
	        context.setAttribute("DRSQA_13298_Driver", itsmDriver);
	        context.setAttribute("DRSQA_13298_Product", Product.Helix_ITSM);

	        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
	        itsmFeatureMethods = new ITSMFeatureMethods();

	        itsmFeatureMethods.navigateToCentralizedConfiguration();
	        String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_NAVBAR, SolQAConstants.FALSE);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE, SolQAConstants.FALSE);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
	        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));

		}

		log.info("--------- DRSQA-11649: IS Asset Console to DSM Cross-launch *** END");
	}

	@Test(description = "Create Incident from Incident Management console and verify it on smartIT with enabled progressive view", groups = {"Regression_P1"})
	@SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11586", Products = { Product.Helix_ITSM, Product.SmartIT })
	public void DRSQA_11586_CreationOfIncidentAndVerifyInSmartIT_PV(ITestContext context) throws Exception {
		log.info("------- START: DRSQA_11586_CreationOfIncidentAndVerifyInSmartIT_PV");

		log.info("Step 1: Disable HKM (precondition) in ITSM");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA-11586_ITSM_HKM_Driver", getDriver());
		context.setAttribute("DRSQA-11586_ITSM_F_Product", Product.Helix_ITSM);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();
		log.info("Step 1.1: Disabling HKM for Self-Service");
		itsmFeatureMethods.verifyITSMLogin();
		itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
		itsmFeatureMethods.closeBrowser();

		log.info("Step 2: Login to ITSM and create Incident");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA-11586_ITSM_Driver", getDriver());
		context.setAttribute("DRSQA-11586_ITSM_Product", Product.Helix_ITSM);
		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
		itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData","DRSQA_11586_TestData");

		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();
		itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT,SolQAConstants.NEW_INCIDENT);
		String incidentId = itsmFeatureMethods.createIncident(itsmTestData);
		itsmFeatureMethods.captureScreenshot("DRSQA_11586_ITSM_IncidentCreated", SolQAConstants.appITSM);
		log.info("Created Incident ID: " + incidentId);
		itsmFeatureMethods.closeBrowser();

		log.info("Step 3: Login to SmartIT and verify incident in Progressive View");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA-11586_SmartIT_Driver", getDriver());
		context.setAttribute("DRSQA-11586_SmartIT_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nishaUser");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
		smartITFeatureMethods.navigateToTicketConsole();
		smartITFeatureMethods.captureScreenshot("DRSQA_11586_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethods.isUserNavigateToTicketConsole(),"User is not navigated to ticket console");

		log.info("Step 4: Apply filter and open created incident");
		smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.INCIDENT_REQUEST_TYPE, incidentId);
		smartITFeatureMethods.captureScreenshot("DRSQA_11586_incidentOpen", SolQAConstants.appSmartIT);

		log.info("Step 5: Verify Progressive View (PV) is enabled");
		String incidentCreatedURL = smartITdriver.getCurrentUrl();
		log.info("Current URL for incident: " + incidentCreatedURL);
		Assert.assertTrue(incidentCreatedURL.contains("incidentPV"),"Created incident is not opened in progressive view. Expected incidentPV but found - "+ incidentCreatedURL);

		log.info("Step 6: Verify chat textbox and send email button are present in PV");
		Assert.assertTrue(smartITFeatureMethods.isChatTextBoxPresentOnChatWindow(),"Chat text box is not present on incident PV");
		Assert.assertTrue(smartITFeatureMethods.isSendEmailButtonPresent(),"Send Email button is not present on incident PV");

		log.info("Step 7: Navigate to Resource tab and search knowledge article");
		smartITFeatureMethods.searchKnowledgeArticleSaveToTicket(SolQAConstants.KBA_articleID_paperJam);
		
		log.info("Step 8: Verify Knowledge Article is pinned under resource tab");
		Assert.assertTrue(smartITFeatureMethods.isKnowledgeArticlePinnedUnderResourcesTab(SolQAConstants.KBA_articleID_paperJam),"Article pinning is not successful. Article Id: " + SolQAConstants.KBA_articleID_paperJam);

		log.info("Step 9: Check print preview functionality");
		smartITFeatureMethods.clickPrinterIcon();
		smartITFeatureMethods.switchToLastWindow();
		smartITFeatureMethods.clickCancelButtonFromPrintBlade_PV();

		log.info("------- END: DRSQA_11586_CreationOfIncidentAndVerifyInSmartIT_PV");
	}

    @Test(description = "Create Workorder from workorder console and verify it on smartIT with enabled progressive view", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11587", Products = { Product.Helix_ITSM, Product.SmartIT })
    public void DRSQA11587_CreationOfWorkorderAndVerifyInSmartIT_PV(ITestContext context) throws Exception {
        log.info("------- START: DRSQA11587_CreationOfWorkorderAndVerifyInSmartIT_PV");

        log.info("Step 1: Disable HKM (precondition) in ITSM");
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        context.setAttribute("DRSQA11587_ITSM_HKM_Driver", ITSMdriver);
        context.setAttribute("DRSQA11587_ITSM_HKM_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        log.info("Step 1.1: Disabling HKM for Self-Service");
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        log.info("Step 2: Login to ITSM and create Work Order");
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        context.setAttribute("DRSQA11587_ITSM_Driver", ITSMdriver);
        context.setAttribute("DRSQA11587_ITSM_Product", Product.Helix_ITSM);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11587_TestData");

        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.NEW_WORK_ORDER);
        String workOrderId = itsmFeatureMethods.createWorkOrder(itsmTestData);
        itsmFeatureMethods.captureScreenshot("DRSQA11587_ITSM_WorkOrderCreated", SolQAConstants.appITSM);
        log.info("Created Work Order ID: " + workOrderId);
        itsmFeatureMethods.closeBrowser();

        log.info("Step 3: Login to SmartIT and verify work order in Progressive View");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA11587_SmartIT_Driver", smartITdriver);
        context.setAttribute("DRSQA11587_SmartIT_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nishaUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethods.navigateToTicketConsole();
        smartITFeatureMethods.captureScreenshot("DRSQA11587_navigatedToConsole", SolQAConstants.appSmartIT);
        Assert.assertTrue(smartITFeatureMethods.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");

        log.info("Step 4: Apply filter and open created work order");
        smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.Work_Order, workOrderId);
        smartITFeatureMethods.captureScreenshot("DRSQA11587_workorderOpen", SolQAConstants.appSmartIT);

        log.info("Step 5: Verify Progressive View (PV) is enabled for work order");
        String workOrderCreatedURL = smartITdriver.getCurrentUrl();
        log.info("Current URL for work order: " + workOrderCreatedURL);
        Assert.assertTrue(workOrderCreatedURL.contains(SolQAConstants.Work_Order_PV), "Created work order is not opened in progressive view. Expected workorderPV but found - " + workOrderCreatedURL);

        log.info("Step 6: Verify chat textbox and send email button are present in PV");
        Assert.assertTrue(smartITFeatureMethods.isChatTextBoxPresentOnChatWindow(), "Chat text box is not present on workorder PV");
        Assert.assertTrue(smartITFeatureMethods.isSendEmailButtonPresent(), "Send Email button is not present on workorder PV");

        log.info("Step 7: Navigate to Resource tab and search knowledge article");
        smartITFeatureMethods.clickResourceTab();

        log.info("Step 8: Search and pin knowledge article under resource tab");
        smartITFeatureMethods.searchKnowledgeArticleSaveToTicket(SolQAConstants.KBA_articleID_paperJam);
        Assert.assertTrue(smartITFeatureMethods.isKnowledgeArticlePinnedUnderResourcesTab(SolQAConstants.KBA_articleID_paperJam), "Article pinning is not successful. Article Id: " + SolQAConstants.KBA_articleID_paperJam);

        log.info("Step 9: Check print preview functionality");
        smartITFeatureMethods.clickPrinterIcon();
        smartITFeatureMethods.switchToLastWindow();
        smartITFeatureMethods.clickCancelButtonFromPrintBlade_PV();
        log.info("------- END: DRSQA11587_CreationOfWorkorderAndVerifyInSmartIT_PV");
    }

    @Test(description = "Creation of workorder from SmartIT with enabled Progressive View", groups = { "Regression_P2", "SmartIT" })
	@SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11559", Products = { Product.SmartIT, Product.Helix_ITSM })
	public void DRSQA11559_WorkOrderCreation_smartITPV(ITestContext context) throws Exception {

		log.info("------- START: DRSQA11559_WorkOrderCreation_smartITPV");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11559_Driver", smartITdriver);
		context.setAttribute("DRSQA_11559_Product", Product.SmartIT);
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ppanUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11559_TestData");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
		log.info("Step 1: Navigate to Create New Workorder");
		smartITFeatureMethods.navigateToCreateNewWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA11559_navigatedToCreateWorkorder", SolQAConstants.appSmartIT);
		log.info("Step 2: Fill Workorder template and create Workorder");
		smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
		smartITFeatureMethods.clickAssigntoMeInWorkorderCreatePage();
		smartITFeatureMethods.saveWorkorder();
		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA11559_workorderCreated", SolQAConstants.appSmartIT);
		log.pass("Workorder created successfully with ID - " + workorderID);
		log.info("Step 3: Add and verify private note on workorder timeline");
		smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("privateNote"), false);
		smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("privateNote"));
		log.info("Step 4: Add and verify public note on workorder timeline");
		smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("publicNote"), true);
		smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("publicNote"));
		smartITFeatureMethods.captureScreenshot("DRSQA11559_PublicPrivateNoteAdded", SolQAConstants.appSmartIT);
		log.info("Step 5: Add attachment note and verify attachment on timeline");
		File attachment = CommonMethods.createTextFile(smartITTestData.get("attachmentFile"));
		smartITFeatureMethods.addAttachmentNote_InActivityFeed(smartITTestData.get("noteWithAttachment"), attachment, true);
		Assert.assertNotNull(attachment);
		Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(attachment.getName()), "Attachment is not visible under activity timeline: " + attachment.getName());
		log.info("Step 6: Add note with @people mention");
		String mentionNote = smartITTestData.get("noteWithPeople") + " @" + smartITTestData.get("mentionUser");
		smartITFeatureMethods.addComment_InActivityFeed(mentionNote, true);
		CommonMethods commonMethods = new CommonMethods();
		commonMethods.pressEscapeWithActions();
		smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("noteWithPeople"));
		log.info("Step 7: Relate configuration item and verify CI field");
		smartITFeatureMethods.relateCIToWorkorder(smartITTestData.get("CIName"), SolQAConstants.Related_To);
		smartITFeatureMethods.captureScreenshot("DRSQA11559_AddedRelatedConfigurationItem", SolQAConstants.appSmartIT);
		log.info("------- END: DRSQA11559_WorkOrderCreation_smartITPV");
	}

    @Test(description = "Creation of Asset from SmartIT with enabled Progressive view", groups = { "Regression_P2" }, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11588", Products = { Product.SmartIT })
    public void DRSQA11588_CreationOfAsset_PV(ITestContext context) throws Exception {

        log.info("------- START: DRSQA11588_CreationOfAsset_PV");
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        context.setAttribute("DRSQA11587_Driver", ITSMdriver);
        context.setAttribute("DRSQA11587_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.modifyAssetSandboxEnabledSettings(SolQAConstants.PETRAMCO_COMPANY, "No");
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
        itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
        itsmFeatureMethods.closeBrowser();

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11588_Driver", smartITdriver);
        context.setAttribute("DRSQA_11588_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "canandaUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11588_TestData");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("Step 1: Navigate to Create New Asset");
        smartITFeatureMethods.navigateToCreateNewAsset();
        smartITFeatureMethods.captureScreenshot("DRSQA11588_navigatedToAsset", SolQAConstants.appSmartIT);
        String assetCreateURL = smartITdriver.getCurrentUrl();
        Assert.assertTrue(assetCreateURL.contains("assetPV"), "Asset create page is not opened in progressive view. Expected assetPV but found - " + assetCreateURL);
        log.info("Step 2: Create asset in PV");
        smartITFeatureMethods.createAsset(smartITTestData);
        smartITFeatureMethods.captureScreenshot("DRSQA11588_AssetCreated", SolQAConstants.appSmartIT);
        log.info("Step 3: Put asset into inventory");
        smartITFeatureMethods.enableEditMode();
        smartITFeatureMethods.setStatus(smartITTestData.get("inventoryStatus")).closeInfoAlertIfPresent().selectInventoryLocation("File Room").saveTicket();
        smartITFeatureMethods.captureScreenshot("DRSQA11588_AssetIn_inventory", SolQAConstants.appSmartIT);
        log.info("Step 4: Create outage");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11588_OutageTestData");
        smartITFeatureMethods.openOutagesTab();
        smartITFeatureMethods.createOutage(smartITTestData);
        smartITFeatureMethods.captureScreenshot("DRSQA11588_outageCreated", SolQAConstants.appSmartIT);
        log.info("Step 5: Remove outage");
        smartITFeatureMethods.openOutagesTab();
        smartITFeatureMethods.removeOutage();
        smartITFeatureMethods.captureScreenshot("DRSQA11588_outageRemoved", SolQAConstants.appSmartIT);
        log.info("------- END: DRSQA11588_CreationOfAsset_PV");
    }

    @Test(description = "Creation of Known Error from SmartIT with enabled Progressive View", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11584", Products = { Product.SmartIT })
    public void DRSQA_11584_CreationOfKnowError_PV(ITestContext context) throws Exception {

        log.info("------- START: DRSQA_11584_CreationOfKnowError_PV");

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA-11584_Driver", smartITdriver);
        context.setAttribute("DRSQA-11584_Product", Product.Helix_SmartIT);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11584_KnownErrorData");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        log.info("Step 1: Navigate to Create New Known Error (PV)");
        smartITFeatureMethods.navigateToCreateNewKnownError();
        smartITFeatureMethods.captureScreenshot("DRSQA_11584_navigatedToNewKnownError", SolQAConstants.appSmartIT);

        log.info("Step 2: Click Save without filling required fields and verify system alert");
        smartITFeatureMethods.saveTicket();
        String msg = smartITFeatureMethods.getSystemAlertText();
        Assert.assertTrue(msg.contains("Required field not specified") || msg.contains("Required field(s) not specified"), "Error message is not shown for submitting blank known error request. Actual: " + msg);
        smartITFeatureMethods.captureScreenshot("DRSQA_11584_errorMessage", SolQAConstants.appSmartIT);
        smartITFeatureMethods.dismissSystemAlerts();

        String createdUrl = smartITdriver.getCurrentUrl();
        log.info("Current URL for known error creation - " + createdUrl);
        Assert.assertTrue(createdUrl.contains("knownerrorPV"), "Known error is not opened in progressive view. Expected known errorPV but found - " + createdUrl);

        log.info("Step 3: Create known error and verify title/description");
        smartITFeatureMethods.createKnownError(smartITTestData);
        smartITFeatureMethods.captureScreenshot("DRSQA_11584_newKnownErrorCreated", SolQAConstants.appSmartIT);
        String knownErrorID = smartITFeatureMethods.getKnownErrorID();

        log.info("Created Known Error ID - " + knownErrorID);

        Assert.assertEquals(smartITFeatureMethods.getTitle_viewPage().trim(), smartITTestData.get("Summary").trim(), "Summary is not matching");
        Assert.assertEquals(smartITFeatureMethods.getDescription_viewPage().trim(), smartITTestData.get("Description").trim(), "Description is not matching");

        log.info("Step 4: Add notes in activity tab and verify");
        smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("privateNote"), false);
        smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("privateNote"));

        String noteWithPerson = smartITTestData.get("personNote") + " @" + smartITTestData.get("personName");
        smartITFeatureMethods.addComment_InActivityFeed(noteWithPerson, true);
        CommonMethods commonMethods = new CommonMethods();
        commonMethods.pressEscapeWithActions();
        smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("personNote"));

        smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("publicNote"), true);
        smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("publicNote"));
        smartITFeatureMethods.captureScreenshot("DRSQA_11584_addedNotesOnActivity", SolQAConstants.appSmartIT);

        log.info("------- END: DRSQA_11584_CreationOfKnowError_PV");
    }

    @Test(description = "[SmartIT]Creation of Workorder through Smart Recorder with Progressive view enabled", groups = {"Regression_P2", "SmartIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11560", Products = { Product.SmartIT, Product.Helix_ITSM })
    public void DRSQA11560_WorkOrderCreationWithProgressiveViewEnabled(ITestContext context) throws Exception {

        log.info("------- START: DRSQA11560_WorkOrderCreationWithProgressiveViewEnabled");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA-11560_Driver", smartITdriver);
        context.setAttribute("DRSQA-11560_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallenUser");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11560_TestData");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("Step 1: Navigate to Smart Recorder");
        smartITFeatureMethods.navigateToSmartRecorder();
        smartITFeatureMethods.captureScreenshot("DRSQA11560_navigatedToSmartRecorder", SolQAConstants.appSmartIT);
        log.info("Step 2: Select end user in Smart Recorder: " + dwpUserData.get("UserName"));
        smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(dwpUserData.get("UserName"));
        smartITFeatureMethods.captureScreenshot("DRSQA11560_selectedCustomerInSmartRecorder", SolQAConstants.appSmartIT);
        log.info("Step 3: Create Workorder from Create Ticket");
        smartITFeatureMethods.CreateTicketWorkorder();
        String draftUrl = smartITdriver.getCurrentUrl();
        log.info("Current URL after clicking Create Workorder: " + draftUrl);
        log.info("Step 4: Assign to me and Save workorder");
        smartITFeatureMethods.clickAssigntoMeInWorkorderCreatePage();
        smartITFeatureMethods.saveWorkorder();
        smartITFeatureMethods.captureScreenshot("DRSQA11560_WorkOrderSavedSuccessfully", SolQAConstants.appSmartIT);
        String createdUrl = smartITdriver.getCurrentUrl();
        log.info("Current URL after workorder creation: " + createdUrl);
        String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
        log.pass("Workorder created successfully with ID - " + workorderID);
        log.info("Step 5: Add and verify public and private notes on workorder activity timeline");
        smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("publicNote"), true);
        smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("publicNote"));
        smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("privateNote"), false);
        smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("privateNote"));
        smartITFeatureMethods.captureScreenshot("DRSQA11560_PublicPrivateNoteAdded", SolQAConstants.appSmartIT);
        log.info("Step 6: Relate Configuration Item/Asset and verify");
        smartITFeatureMethods.relateCIToWorkorder(smartITTestData.get("CIName"), SolQAConstants.Related_To);
        smartITFeatureMethods.captureScreenshot("DRSQA11560_AddedRelatedConfigurationItem", SolQAConstants.appSmartIT);
        log.info("------- END: DRSQA11560_WorkOrderCreationWithProgressiveViewEnabled");
    }

    @Test(description = "[SmartIT]Creation of Incident through Smart Recorder without using Template")
	@SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11877", Products = { Product.SmartIT, Product.Helix_ITSM })
	public void DRSQA11877_IncidentCreationThroughSmartRecorderWithProgressiveViewEnabled(ITestContext context) throws Exception {

		log.info("------- START: DRSQA11877_IncidentCreationThroughSmartRecorderWithProgressiveViewEnabled");

		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA-11877_WebDriver", getDriver());
		context.setAttribute("DRSQA-11877_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"iallenUser");
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA11877_TestData");

		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Step 1: Navigate to Smart Recorder");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA11877_navigatedToSmartRecorder", SolQAConstants.appSmartIT);

		log.info("Step 2: Select end user in Smart Recorder: " + dwpUserData.get("UserName"));
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(dwpUserData.get("UserName"));
		smartITFeatureMethods.captureScreenshot("DRSQA11877_selectedCustomerInSmartRecorder",
				SolQAConstants.appSmartIT);

		log.info("Step 3: Create Incident draft from Create Ticket");
		smartITFeatureMethods.CreateTicketIncidentDraft();

		String draftUrl = smartITdriver.getCurrentUrl();
		log.info("Current URL after clicking Create Incident: " + draftUrl);
		Assert.assertTrue(draftUrl.contains("pwa-draft-incident"),
				"Incident is not opened in progressive draft view. Expected pwa-draft-incident but found - "
						+ draftUrl);

		log.info("Step 4: Assign to me, set status In Progress and Save incident");
		smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA11877_IncidentSavedSuccessfully", SolQAConstants.appSmartIT);

		String incidentId = smartITFeatureMethods.FetchIncidentTicketID();
		log.info("Incident created with ID - " + incidentId);

		String createdUrl = smartITdriver.getCurrentUrl();
		log.info("Current URL after incident creation - " + createdUrl);
		Assert.assertTrue(createdUrl.contains("incidentPV"),
				"Created Incident is not opened in progressive view. Expected incidentPV but found - " + createdUrl);

		log.info("Step 5: Add public note with attachment and verify");
		File attachment = CommonMethods.createTextFile(smartITTestData.get("attachmentFile"));
		smartITFeatureMethods.addAttachmentNote_InActivityFeed(smartITTestData.get("publicNote"), attachment, true);
		Assert.assertNotNull(attachment);
		Assert.assertTrue(smartITFeatureMethods.isAttachedFileAppearedUnderActivityTab(attachment.getName()),
				"Attachment is not visible under activity timeline: " + attachment.getName());
		smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("publicNote"));

		log.info("Step 6: Add private note and verify");
		smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("privateNote"), false);
		smartITFeatureMethods.verifyActivityNotes(smartITTestData.get("privateNote"));
		smartITFeatureMethods.captureScreenshot("DRSQA11877_PublicPrivateNoteAdded", SolQAConstants.appSmartIT);

		log.info("Step 7: Resolve incident");
		smartITFeatureMethods.updateIncidentStatus(SolQAConstants.RESOLVED, smartITTestData.get("statusReason"),
				smartITTestData.get("resolutionNote"));
		smartITFeatureMethods.saveTicket();

		log.info("Step 8: Close incident");
		smartITFeatureMethods.updateIncidentStatus(SolQAConstants.STATUS_CLOSED, smartITTestData.get("statusReason"),
				smartITTestData.get("resolutionNote"));
		smartITFeatureMethods.saveTicket();
		smartITFeatureMethods.captureScreenshot("DRSQA11877_closeIncident", SolQAConstants.appSmartIT);

		log.info("Step 9: Reopen incident (fallback: set status In Progress + add reopen note)");
		smartITFeatureMethods.addComment_InActivityFeed(smartITTestData.get("reopenComment"), true);
		smartITFeatureMethods.updateIncidentStatus(SolQAConstants.IN_PROGRESS, null, null);
		smartITFeatureMethods.saveTicket();
		Assert.assertTrue(
				smartITFeatureMethods.getIncidentStatus_viewPage().trim().contains(SolQAConstants.IN_PROGRESS),
				"Incident is not in progress after reopen action");
		smartITFeatureMethods.captureScreenshot("DRSQA11877_reopenIncident", SolQAConstants.appSmartIT);

		log.info("------- END: DRSQA11877_IncidentCreationThroughSmartRecorderWithProgressiveViewEnabled");
	}

	@Test(description = "[PWA] Creating change on SmartIT and approving it on ITSM", groups = { "Regression_P1" }, singleThreaded = true)
	@SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11615", Products = { Product.SmartIT, Product.Helix_ITSM })
	public void DRSQA11615_Create_CRQ_ITSM_Approve_PV(ITestContext context) throws Exception {

		log.info("------- START: DRSQA11615_Create_CRQ_ITSM_Approve_PV");
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        context.setAttribute("DRSQA_11615_Driver", getDriver());
        context.setAttribute("DRSQA_11615_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        log.info("Step 1.1: Disabling HKM for Self-Service");
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

		log.info(" Step 1: Create Change in SmartIT (PV)");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11615_Driver", getDriver());
		context.setAttribute("DRSQA_11615_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "creyUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11615_TestData");
		smartITTestData.put("Change Title", "DRSQA11615_" + RandomStringUtils.randomAlphanumeric(8));

		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Navigate to Create New Change");
		smartITFeatureMethods.navigateToCreateNewChange();
		smartITFeatureMethods.captureScreenshot("DRSQA11615_navigatedToCreateChange", SolQAConstants.appSmartIT);

		String changeCreatedUrl = smartITdriver.getCurrentUrl();
		Assert.assertTrue(changeCreatedUrl.contains("changePV"), "Created change is not opened in progressive view. Expected changePV but found - " + changeCreatedUrl);

		smartITFeatureMethods.FillChangeTemplate(smartITTestData).clickAssigntoMeInCreateChangePage().saveChange();

		String changeId = smartITFeatureMethods.FetchChangeTicketID();
		log.info("Created Change ID: " + changeId);
		smartITFeatureMethods.captureScreenshot("DRSQA11615_changeCreated", SolQAConstants.appSmartIT);

		log.info("Verify Change status as Draft");
		smartITFeatureMethods.verifyChangeStatus(SolQAConstants.DRAFT);

		log.info("Step 2: Update status to approval-related statuses");
		log.info("Update Change status to " + SolQAConstants.REQUEST_FOR_AUTHORIZATION);
		smartITFeatureMethods.updateChangeStatus(SolQAConstants.REQUEST_FOR_AUTHORIZATION);
		smartITFeatureMethods.saveTicket();
		smartITFeatureMethods.verifyChangeStatus(SolQAConstants.REQUEST_FOR_AUTHORIZATION);
		smartITFeatureMethods.captureScreenshot("DRSQA11615_status_REQ_FOR_AUTH", SolQAConstants.appSmartIT);

		log.info("Update Change status to " + SolQAConstants.REQUEST_FOR_CHANGE);
		smartITFeatureMethods.updateChangeStatus(SolQAConstants.REQUEST_FOR_CHANGE);
		smartITFeatureMethods.saveTicket();
		smartITFeatureMethods.verifyChangeStatus(SolQAConstants.PLANNING_IN_PROGRESS);
		smartITFeatureMethods.captureScreenshot("DRSQA11615_status_REQ_FOR_CHG", SolQAConstants.appSmartIT);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "ppanUser");
        smartITFeatureMethods.addApprover(itsmUserData.get("UserName"));

        log.info("-------------------- Step 3: Approve the change in ITSM (Approval Central)");
		log.info("Login to ITSM and approve the Change from Approval Central");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA_11615_Driver", getDriver());
		context.setAttribute("DRSQA_11615_Product", Product.Helix_ITSM);

		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "ppanUser");
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();
		itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
		itsmFeatureMethods.approveChange(changeId);
		itsmFeatureMethods.captureScreenshot("DRSQA11615_changeApproved", SolQAConstants.appITSM);
		itsmFeatureMethods.closeBrowser();

        log.info("-------------------- Step 4: Verify status in SmartIT after approval");
		log.info("Verify approved Change status in SmartIT");
		smartITFeatureMethods.switchToSmartITPage(smartITdriver);
		smartITFeatureMethods.refreshSmartITPage();
		smartITFeatureMethods.verifyChangeStatus(SolQAConstants.PLANNING_IN_PROGRESS);
		smartITFeatureMethods.captureScreenshot("DRSQA11615_status_PLANN_IN_PRGRESS", SolQAConstants.appSmartIT);

		log.info("------- END: DRSQA11615_Create_CRQ_ITSM_Approve_PV");
	}

	@Test(description = "Creation of Broadcast from SmartIT with enabled progressive views", groups = { "Regression_P2", "SmartIT" })
	@SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11585", Products = { Product.SmartIT })
	public void DRSQA11585_CreationOfBroadcast_PV(ITestContext context) throws Exception {

		log.info("------- START: DRSQA11585_CreationOfBroadcast_PV");

		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_11585_Driver", getDriver());
		context.setAttribute("DRSQA_11585_Product", Product.SmartIT);

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "canandaUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11585_BroadcastData");
		smartITTestData.put("HeadLine", smartITTestData.get("HeadLine") + "_" + RandomStringUtils.randomAlphanumeric(6));

		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Navigate to Create New Broadcast");
		smartITFeatureMethods.navigateToCreateNewBroadcast();
		smartITFeatureMethods.captureScreenshot("DRSQA11585_navigatedToBroadcast", SolQAConstants.appSmartIT);

		String broadcastUrl = smartITdriver.getCurrentUrl();
		log.info("Current URL for broadcast creation - " + broadcastUrl);
		Assert.assertTrue(broadcastUrl.contains("broadcastPV"),
				"Created broadcast is not opened in progressive view. Expected broadcastPV but found - "
						+ broadcastUrl);

		smartITFeatureMethods.createBroadcast(smartITTestData);

		Assert.assertFalse(smartITFeatureMethods.getSystemAlertText().contains("ERROR (552)"),
				"Failed: error message is displayed while saving broadcast");

		smartITFeatureMethods.captureScreenshot("DRSQA11585_broadcastCreated", SolQAConstants.appSmartIT);
		log.info("------- END: DRSQA11585_CreationOfBroadcast_PV");
	}

    @Test(description = "Default Netops False", groups = { "Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11684", Products = { Product.Helix_ITSM, Product.Helix_SmartIT })
    public void DRSQA11684_DefaultNetopsFalse(ITestContext context) throws Exception {

        log.info("----------- START: DRSQA11684_DefaultNetopsFalse -----------");
        try {
            log.info("Step 1: Enable NetOps in ITSM Centralized Configuration");
            ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(ITSMdriver);
            context.setAttribute("DRSQA_11684_Driver", ITSMdriver);
            context.setAttribute("DRSQA_11684_Product", Product.Helix_ITSM);
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDINGCONSOLE_DEFAULT_ADMINISTRATOR, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
            itsmFeatureMethods.navigateToCentralizedConfiguration();
            String[] componentMenu = new String[]{SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
            itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
            itsmFeatureMethods.addCCSParameters(SolQAConstants.ENABLE_NET_OPS);
            itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_NET_OPS, SolQAConstants.TRUE);
            itsmFeatureMethods.closeBrowser();

            log.info("-------------------- Step 2: Login to SmartIT and create incident; verify");
            smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITdriver);
            context.setAttribute("DRSQA_11684_Driver", smartITdriver);
            context.setAttribute("DRSQA_11684_Product", Product.SmartIT);
            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
            smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11684_IncidentForNetopsCheck");
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
            smartITFeatureMethods = new SmartITFeatureMethodsPage();
            smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
            smartITFeatureMethods.navigateToCreateNewIncident();
            smartITFeatureMethods.captureScreenshot("DRSQA11684_navigatedToCreateIncident_NetOpsTrue", SolQAConstants.appSmartIT);
            smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
            smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
            smartITFeatureMethods.saveIncident();
            String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
            Assert.assertTrue(smartITFeatureMethods.verifyRelatedPartiesAndSourceInformationTabs(), "Related parties and Source information option is not available");
            smartITFeatureMethods.captureScreenshot("DRSQA11684_verifyTabsPresent_NetOpsTrue", SolQAConstants.appSmartIT);
            log.info("-------------------- Step 3: Create related party (Umbrella creates a Related");
            smartITFeatureMethods.openRelatedPartiesTab();
            smartITFeatureMethods.createRelatedParty(smartITTestData.get("RelatedPartyType"), smartITTestData.get("RelatedPartyName"), smartITTestData.get("RelatedPartyRole"));
            smartITFeatureMethods.captureScreenshot("DRSQA11684_relatedPartyCreated_NetOpsTrue", SolQAConstants.appSmartIT);
            smartITFeatureMethods.closeBrowser();

            log.info("-------------------- Step 4: Verify Email Messages -> HTML Body contains");
            ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(ITSMdriver);
            context.setAttribute("DRSQA_11684_Driver", ITSMdriver);
            context.setAttribute("DRSQA_11684_Product", Product.Helix_ITSM);
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.LoadForm(SolQAConstants.AR_SYSTEM_EMAIL_MESSAGES, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
            itsmFeatureMethods.verifyHtmlBodyOnEmailPage(incidentID);
            itsmFeatureMethods.closeBrowser();
        } finally {
            log.info("------------------- Cleanup: Set NetOps back to TRUE --------------------");
            ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(ITSMdriver);
            context.setAttribute("DRSQA_11684_Driver", ITSMdriver);
            context.setAttribute("DRSQA_11684_Product", Product.Helix_ITSM);
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDINGCONSOLE_DEFAULT_ADMINISTRATOR, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
            itsmFeatureMethods.navigateToCentralizedConfiguration();
            String[] componentMenu = new String[]{SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
            itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
            itsmFeatureMethods.addCCSParameters(SolQAConstants.ENABLE_NET_OPS);
            itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_NET_OPS, SolQAConstants.TRUE);
            itsmFeatureMethods.closeBrowser();
        }
        log.info("----------- END: DRSQA11684_DefaultNetopsFalse -----------");
    }

    @Test(description = "Support AI-based group prediction for assignment of incidents",groups = {"OneTime"})
	@SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11406", Products = {Product.SmartIT, Product.Helix_ITSM})
	public void DRSQA11406_Support_AI_based_group_prediction_for_assignment_of_incidents(ITestContext context) throws Exception {
		log.info("--------- DRSQA11406_Support_AI_based_group_prediction_for_assignment_of_incidents *** START");

		// Precondition: Login to ITSM and configure cognitive services
		log.info("Precondition: Setting up ITSM configuration for cognitive services");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA11406_Driver", getDriver());
		context.setAttribute("DRSQA11406_Product", Product.Helix_ITSM);

		// Load ITSM user data (using admin credentials)
		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

		// Load test data
		itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData",
				"DRSQA11406_TestData");

		// Initialize ITSM feature methods
		itsmFeatureMethods = new ITSMFeatureMethods();

		log.info("Step 1: Navigate to Centralized Configuration");
		itsmFeatureMethods.navigateToCentralizedConfiguration();

		log.info("Step 2: Set CCS value Enable-Cognitive-Service=true");
		log.info("Step 3: Set CCS value Classification-Service-Provider=HELIX");
		itsmFeatureMethods.configureCognitiveService();

		log.info("Step 4: Navigate to Incident Rules page and set Cognitive Support Group Prediction=Yes");
		itsmFeatureMethods.setRulesPageValueUsingRadioButton(itsmTestData.get("Company"), itsmTestData.get("ruleName"),
				"Yes");
		log.pass("Successfully set Incident Rules for Cognitive Support Group Prediction");

		// Close child window and switch back to main ITSM window if multiple
		// tabs/windows are open
		closeChildWindowAndSwitchToMain();

		log.info("Step 5: Train cognitive data for Incident Support Group Prediction");
		// Note: Training cognitive data requires AR form access and may take up to 10
		// minutes
		// This method will wait for training to complete or timeout after 10 minutes
		itsmFeatureMethods.trainCognitiveData(itsmTestData.get("dataType"), itsmTestData.get("Company"),
				SolQAConstants.INCIDENT_MANAGEMENT, itsmTestData.get("templateName"));
		log.pass("Successfully trained cognitive data for Support Group Prediction");

		// Close ITSM browser
		itsmFeatureMethods.closeBrowser();

		log.info("Step 6: Login to SmartIT and create new incident");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA11406_Driver", getDriver());
		context.setAttribute("DRSQA11406_Product", Product.SmartIT);

		// Load SmartIT user data
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"bmcopssupportagent");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		// Initialize SmartIT feature methods
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("Step 7: Navigate to Create New Incident page");
		smartITFeatureMethods.navigateToCreateNewIncident();

		log.info("Step 8: Fill incident template with test data");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA11406_IncidentCreation");
		smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethods.captureScreenshot("DRSQA11406_IncidentCreation", SolQAConstants.appSmartIT);

		log.info("Step 9: Save incident");
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA11406_IncidentSaved", SolQAConstants.appSmartIT);
		log.pass("Successfully created incident: " + incidentID);

		log.info("Step 10: Verify activity notes for AI-based support group assignment");
		smartITFeatureMethods.verifyAIBasedSupportGroupAssignmentActivityNotes();
		smartITFeatureMethods.captureScreenshot("DRSQA11406_ActivityNoteVerified", SolQAConstants.appSmartIT);

		// Close SmartIT browser
		smartITFeatureMethods.closeBrowser();

		log.info("--------- DRSQA11406_Support_AI_based_group_prediction_for_assignment_of_incidents *** END");
	}

	@Test(description = "Category Prediction for Incident using AI/Cognitive Service", groups = {"OneTime"})
	@SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-13471", Products = {Product.SmartIT, Product.Helix_ITSM})
	public void DRSQA13471_Category_Prediction_for_Incident_using_Cognitive_Service(ITestContext context) throws Exception {
		log.info("--------- DRSQA13471_Category_Prediction_for_Incident_using_Cognitive_Service *** START");

		// PREREQUISITE: Use DRSQA11406 setup steps (Enable CCS, Set Rules, Train
		// Cognitive Data)
		log.info("PREREQUISITE: Setting up ITSM configuration for cognitive services");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA13471_Driver", getDriver());
		context.setAttribute("DRSQA13471_Product", Product.Helix_ITSM);

		// Load ITSM admin user data (hannah_admin)
		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers",
				"hannah_admin");
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

		// Load test data
		itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData",
				"DRSQA13471_TestData");

		// Initialize ITSM feature methods
		itsmFeatureMethods = new ITSMFeatureMethods();

		log.info("PREREQUISITE Step 1: Navigate to Centralized Configuration and enable cognitive services");
		itsmFeatureMethods.navigateToCentralizedConfiguration();
		itsmFeatureMethods.configureCognitiveService();

		log.info("PREREQUISITE Step 2: Navigate to Incident Rules and enable Cognitive Support Group Prediction");
		itsmFeatureMethods.setRulesPageValueUsingRadioButton(itsmTestData.get("Company"), itsmTestData.get("ruleName"),
				"Yes");
		log.pass("Successfully enabled Incident Rules for Cognitive Support Group Prediction");

		// Close child window and switch back to main ITSM window if multiple
		// tabs/windows are open
		closeChildWindowAndSwitchToMain();

		log.info("PREREQUISITE Step 3: Train cognitive data for Incident Support Group Prediction");
		itsmFeatureMethods.trainCognitiveData(itsmTestData.get("dataType"), itsmTestData.get("Company"),
				SolQAConstants.INCIDENT_MANAGEMENT, itsmTestData.get("templateName"));
		log.pass("Successfully trained cognitive data for Support Group Prediction");
		log.info("Step 1: Navigate to Incident Management Console and search for a closed incident");
		itsmFeatureMethods.navigateToPageFromApplication("Application Administration Console",
				"Incident Management Console");

		log.info("Step 2: Filter incidents by Company and Status=Closed to find closed incidents with category");
		// Using existing search functionality - searching for closed incidents
		String closedIncidentSummary = "Account Lockout"; // Using a known incident summary for testing
		log.info("Using closed incident summary for category prediction: " + closedIncidentSummary);
		log.pass("Successfully identified closed incident summary from CP enabled company");

		// Close ITSM browser
		itsmFeatureMethods.closeBrowser();

		log.info("Step 3: Login to SmartIT with user from company with CP enabled");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA13471_Driver", getDriver());
		context.setAttribute("DRSQA13471_Product", Product.SmartIT);

		// Load SmartIT user data from CP enabled company
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"bmcopsenduser");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		// Initialize SmartIT feature methods
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("Step 4: Navigate to Create New Incident page");
		smartITFeatureMethods.navigateToCreateNewIncident();

		log.info("Step 5: Fill incident with customer and closed incident summary");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA13471_IncidentCreation");

		// Create a map with customer name and the copied summary
		Map<Object, String> incidentData = new java.util.LinkedHashMap<>();
		incidentData.put("Customer Name", smartITTestData.get("Customer Name"));
		incidentData.put("Incident Title", closedIncidentSummary);

		smartITFeatureMethods.FillIncidentTemplate(incidentData);

		log.info("Step 6: Click on brain icon (Category Prediction icon) to predict category");
		smartITFeatureMethods.clickCategoryPredictionIcon();

		log.info("Step 7: Save the incident");
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		log.pass("Successfully created incident: " + incidentID);

		log.info("Step 8: Verify category is predicted from Category Prediction call");
		log.info("Category should be auto-predicted and populated based on cognitive service");

		log.info("Step 9: Verify activity note shows category was added by Helix Cognitive Service");
		String expectedActivityNote = smartITTestData.get("ActivityNote");
		smartITFeatureMethods.verifyCategoryPredictionActivityNotes(expectedActivityNote);

		smartITFeatureMethods.captureScreenshot("DRSQA13471_CategoryPredictionVerified", SolQAConstants.appSmartIT);
		log.pass("Successfully verified Category Prediction functionality with AI/Cognitive Service");

		// Close SmartIT browser
		smartITFeatureMethods.closeBrowser();

		log.info("--------- DRSQA13471_Category_Prediction_for_Incident_using_Cognitive_Service *** END");
	}

	@Test(description = "Verify Category Prediction using Brain Icon with Cognitive Service", groups = {"OneTime"})
	@SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11727", Products = {Product.SmartIT, Product.Helix_ITSM})
	public void DRSQA11727_Verify_Category_Prediction_Brain_Icon_Cognitive_Service(ITestContext context) throws Exception {
		log.info("--------- DRSQA11727_Verify_Category_Prediction_Brain_Icon_Cognitive_Service *** START");

		// PREREQUISITE: Use DRSQA11406 setup steps (Enable CCS, Set Rules, Train
		// Cognitive Data)
		log.info("PREREQUISITE: Setting up ITSM configuration for cognitive services");
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		context.setAttribute("DRSQA11727_Driver", getDriver());
		context.setAttribute("DRSQA11727_Product", Product.Helix_ITSM);

		// Load ITSM admin user data (hannah_admin)
		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers",
				"hannah_admin");
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

		// Load test data
		itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData",
				"DRSQA13471_TestData");

		// Initialize ITSM feature methods
		itsmFeatureMethods = new ITSMFeatureMethods();

		log.info("PREREQUISITE Step 1: Navigate to Centralized Configuration and enable cognitive services");
		itsmFeatureMethods.navigateToCentralizedConfiguration();
		itsmFeatureMethods.configureCognitiveService();

		log.info("PREREQUISITE Step 2: Navigate to Incident Rules and enable Cognitive Support Group Prediction");
		itsmFeatureMethods.setRulesPageValueUsingRadioButton(itsmTestData.get("Company"), itsmTestData.get("ruleName"),
				"Yes");
		log.pass("Successfully enabled Incident Rules for Cognitive Support Group Prediction");

		// Close child window and switch back to main ITSM window if multiple
		// tabs/windows are open
		closeChildWindowAndSwitchToMain();

		log.info("PREREQUISITE Step 3: Train cognitive data for Incident Support Group Prediction");
		itsmFeatureMethods.trainCognitiveData(itsmTestData.get("dataType"), itsmTestData.get("Company"),
				SolQAConstants.INCIDENT_MANAGEMENT, itsmTestData.get("templateName"));
		log.pass("Successfully trained cognitive data for Support Group Prediction");

		log.info("Step 1: Login to ITSM and open closed incident from CP enabled company");
		itsmFeatureMethods.navigateToPageFromApplication("Application Administration Console",
				"Incident Management Console");

		log.info("Step 2: Copy the summary from closed incident with category available");
		String closedIncidentSummary = "Account Lockout";
		log.info("Using closed incident summary for category prediction: " + closedIncidentSummary);
		log.pass("Successfully identified closed incident summary from CP enabled company");

		// Close ITSM browser
		itsmFeatureMethods.closeBrowser();

		log.info("Step 3: Login to SmartIT with user from company with CP enabled");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA11727_Driver", getDriver());
		context.setAttribute("DRSQA11727_Product", Product.SmartIT);

		// Load SmartIT user data from CP enabled company
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"bmcopsenduser");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		// Initialize SmartIT feature methods
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("Step 4: Navigate to Create New Incident page");
		smartITFeatureMethods.navigateToCreateNewIncident();

		log.info("Step 5: Fill incident with customer and copied closed incident summary");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
				"DRSQA11727_IncidentCreation");

		// Create a map with customer name and the copied summary
		Map<Object, String> incidentData = new java.util.LinkedHashMap<>();
		incidentData.put("Customer Name", smartITTestData.get("Customer Name"));
		incidentData.put("Incident Title", smartITTestData.get("Incident Title"));

		smartITFeatureMethods.FillIncidentTemplate(incidentData);
		smartITFeatureMethods.captureScreenshot("DRSQA11727_IncidentFilled", SolQAConstants.appSmartIT);

		log.info("Step 6: Click on brain icon (Category Prediction icon) to trigger category prediction");
		smartITFeatureMethods.clickCategoryPredictionIcon();
		smartITFeatureMethods.captureScreenshot("DRSQA11727_BrainIconClicked", SolQAConstants.appSmartIT);

		log.info("Step 7: Verify category is predicted and response received from Category Prediction call");
		log.info("Category should be auto-predicted based on the cognitive service API call");

		log.info("Step 8: Save the incident");
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA11727_IncidentSaved", SolQAConstants.appSmartIT);
		log.pass("Successfully created incident: " + incidentID);

		log.info("Step 9: Verify category is fetched and populated from Category Prediction service");
		log.info("Step 10: Verify activity note shows category was added by Helix Cognitive Service");
		String expectedActivityNote = smartITTestData.get("ActivityNote");
		smartITFeatureMethods.verifyCategoryPredictionActivityNotes(expectedActivityNote);

		smartITFeatureMethods.captureScreenshot("DRSQA11727_CategoryPredictionVerified", SolQAConstants.appSmartIT);
		log.pass("Successfully verified Category Prediction via Brain Icon with Helix Cognitive Service");
		log.info("--------- DRSQA11727_Verify_Category_Prediction_Brain_Icon_Cognitive_Service *** END");
	}

	@Test(description = "Option to consume RSSO/HSSO UI Idle Timeout implementation for SmartIT/PV", groups = {"OneTime", "SmartIT","RSSO"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-16987", Products = {Product.SmartIT, Product.Helix_ITSM, Product.RSSO})
    public void DRSQA16987_Option_to_consume_RSSO_HSSO_UI_Idle_Timeout_implementation_for_SmartIT_PV(ITestContext context) throws Exception {
        log.info("************** START:- Option to consume RSSO/HSSO UI Idle Timeout implementation for SmartIT/PV  *************");
        try{
        log.info("------------Precondition 1:- Log in to the BMC Helix SSO Admin Console as a SaaS administrator.------------");
        rssoDriver = launchWeb(SolQAConstants.AppRSSO, SolQAConstants.AppRSSO);
        setDriver(rssoDriver);
        context.setAttribute("DRSQA_16987_Driver", rssoDriver);
        context.setAttribute("DRSQA_16987_Product", Product.RSSO);
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

        log.info("---------------Precondition 7:- enable the feature flag------------");
        log.info("---------Assuming the feature flag is already enabled------------");

        log.info("------------Step 1:Login to SmartIT using pbunyon -------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", smartITdriver);
        context.setAttribute("DRSQA_16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        log.info("------------Step 2 and 3: Once Logged in to Smart IT and keep the session Idle for more than allowed time which we set as 3 min. Make sure user does not respond to warning message as well.-------------");
        commonWaits.waitForSometime(100000); //This wait is added for the session to be idle for more than allowed time
        smartITFeatureMethods.captureScreenshot("DRSQA16987_SessionIdle", SolQAConstants.appSmartIT);
        rssoFeatureMethods.verifySessionIdleWarningMessage();
        commonWaits.waitForSometime(100000); //This wait is added for verify session logout
        rssoFeatureMethods.verifySessionLogout();
        smartITdriver.quit();

        log.info("------------Step 4: Verify when Smart IT is open in one tab and Midtier is Open in second tab and idle time hits only for Midtier. Logged in to Smart IT in one tab and Midtier in second tab. keep the session Idle in Midtier for more than allowed time which we set as 4 min. Make sure user is acitve in Smart IT. -------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", smartITdriver);
        context.setAttribute("DRSQA_16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods=new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        itsmFeatureMethods.verifyITSMLogin();
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForSometime(150000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.clickOnOkButton();
        smartITFeatureMethods.navigateToNewIncident();
        itsmFeatureMethods.captureScreenshot("DRSQA16987_SessionIdle", SolQAConstants.appITSM);
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForSometime(150000);
        rssoFeatureMethods.verifySessionLogout();
        commonWaits.switchToTabByIndex(0);
        rssoFeatureMethods.verifySessionLogoutIsNotPresent();
        smartITdriver.quit();

        log.info("----------Step 5: Validate this feature when Smart IT is open in one tab and Midtier is Open in second tab and idle time hits in both tab. Logged in to Smart IT in one tab and Midtier in second tab. keep the session Idle in both Midtier+ Smart IT for more than allowed time which we set as 10 min. Make sure user does not respond to warning message as well. -------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", getDriver());
        context.setAttribute("DRSQA16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        commonWaits.waitForSometime(300000);
        rssoFeatureMethods.verifySessionLogout();
        commonWaits.switchToTabByIndex(0);
        rssoFeatureMethods.verifySessionLogout();
        smartITdriver.quit();

        log.info("----------Step 6: Validate this feature when PV is open in one tab with IFrame use case.  Logged in to PV in one tab and Load Midtier Views ( Create new Incident case ) within IFrame in same tab. keep the session Idle in PV for more than allowed time which we set as 10 min. Make sure user does not respond to warning message as well. -------------");
        log.info("CCS configuration under com.bmc.arsys.itsm. > Check the following values: > EnableNetOps=true > Change_Impact_Analyzer_Option=both");
        context.setAttribute("DRSQA_16987_Driver", itsmDriver);
        context.setAttribute("DRSQA16987_Product", Product.Helix_ITSM);

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] {SolQAConstants.ARSYSTEM_BMC_ARSYS_ITSM, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_NET_OPS, SolQAConstants.TRUE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.CHANGE_IMPACT_ANALYZER_OPTION, SolQAConstants.BOTH);

        log.info("Log in to ITSM and go to centralized configuration and enable the enableISNavbar, EnableISTicketConsole, enableISAssetConsole, enableSharedTicketConsole, PV enabled settings.");
        String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_NAVBAR, SolQAConstants.TRUE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE, SolQAConstants.TRUE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.TRUE);
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        log.info(": Enable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_13298_Driver", smartITdriver);
        context.setAttribute("DRSQA_13298_Product", Product.Helix_SmartIT);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        innovationSuiteFeatureMethodsPage.clickOnSettings();
        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
        innovationSuiteFeatureMethodsPage.enableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
        smartITdriver.quit();

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", getDriver());
        context.setAttribute("DRSQA16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmFeatureMethods.verifyITSMLogin();
        rssoFeatureMethods.clickOnOkButton();
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForSometime(150000); //This wait is added for the session to be idle for more than allowed time
        smartITFeatureMethods.navigateToOptionInAdvancedResponsiveNavigation(SolQAConstants.CONSOLE, SolQAConstants.IMPACT_ANALYSIS_CONSOLE);
        itsmFeatureMethods.captureScreenshot("DRSQA16987_SessionIdle", SolQAConstants.appITSM);
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForSometime(150000);
        rssoFeatureMethods.verifySessionLogout();
        commonWaits.switchToTabByIndex(0);
        rssoFeatureMethods.verifySessionLogoutIsNotPresent();
        smartITdriver.quit();

        log.info("----------Step 7: Validate this feature when PV is open in one tab with IFrame use case and Midtier open in second tab following Idle timeout hitting both tabs. Logged in to PV in one tab and Load Midtier Views ( Create new Incident case ) within IFrame in same tab. Also Open Midtier in second tab. Now keep the session Idle in both PV+Midtier for more than allowed time which we set as 10 min. Make sure user does not respond to warning message as well. -------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", getDriver());
        context.setAttribute("DRSQA16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmFeatureMethods.verifyITSMLogin();
        rssoFeatureMethods.clickOnOkButton();
        commonWaits.waitForSometime(300000);
        rssoFeatureMethods.verifySessionLogout();
        commonWaits.switchToTabByIndex(0);
        rssoFeatureMethods.verifySessionLogout();
        smartITdriver.quit();

        log.info("----------Step 8: Validate this feature when PV is open in one tab with IFrame use case and Midtier open in second tab following Idle timeout hitting only PV. Logged in to PV in one tab and Load Midtier Views ( Create new Incident case ) within IFrame in same tab. Also Open Midtier in second tab. Now keep the session Idle in PV for more than allowed time which we set as 10 min. Make sure user is active in Midtier. -------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", getDriver());
        context.setAttribute("DRSQA_16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        rssoFeatureMethods.openNewTab();
        itsmFeatureMethods.navigateToITSMURLInNewOpenedTab();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        commonWaits.waitForSometime(150000); //This wait is added for the session to be idle for more than allowed time
        getDriver().navigate().refresh();
        itsmFeatureMethods.captureScreenshot("DRSQA16987_SessionIdle", SolQAConstants.appITSM);
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForSometime(150000);
        rssoFeatureMethods.verifySessionLogout();
        commonWaits.switchToTabByIndex(1);
        rssoFeatureMethods.verifySessionLogoutIsNotPresent();
        smartITdriver.quit();

        log.info("----------Step 10: Validate with IS NabBar env. Login to Smart where IS navBar is eabled and click on Smart Recorder and type something. Keep the session idle till it hit the timeout. -------------");
        log.info(": Disable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", smartITdriver);
        context.setAttribute("DRSQA_16987_Product", Product.Helix_SmartIT);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        innovationSuiteFeatureMethodsPage.clickOnSettings();
        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
        innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
        smartITdriver.quit();

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", smartITdriver);
        context.setAttribute("DRSQA_16987_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethods.navigateToSmartRecorder();
        smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(smartITUserData.get("FullName"));
        commonWaits.waitForSometime(300000); //This wait is added for the session to be idle for more than allowed time
        rssoFeatureMethods.verifySessionLogout();
        smartITdriver.quit();
        }

        finally{
        log.info(": Disable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16987_Driver", smartITdriver);
        context.setAttribute("DRSQA_16987_Product", Product.Helix_SmartIT);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        innovationSuiteFeatureMethodsPage.clickOnSettings();
        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
        innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
        smartITdriver.quit();

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
            rssoDriver.quit();

        }

        log.info("*********** END:- Option to consume RSSO/HSSO UI Idle Timeout implementation for SmartIT/PV *************");
    }

    @Test(description = "Gainsight:ITSM Bundle Mapping - Send global context to Gainsight", groups = {"Regression_P1", "SmartIT", "ITSM", "Innovation_Suite","gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "20285", JiraID = "DRSQA-11709", Products = {Product.Helix_ITSM, Product.SmartIT, Product.Innovation_Suite})
    public void DRSQA11709_Gainsight_ITSM_Bundle_Mapping_Send_global_context_to_Gainsight(ITestContext context) {

		log.info("*********** START : Gainsight:ITSM Bundle Mapping - Send global context to Gainsight ****************");

        try {
            log.info("---- Step 1: Precondition: Enable Gainsight Configuration --------");
			isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_11709_Driver", isDriver);
            context.setAttribute("DRSQA_11709_Product", Product.Innovation_Suite);

            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
			log.info("Navigate to Gainsight Settings Page");
            innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.GAINSIGHT_SETTINGS_URL);
            innovationSuiteFeatureMethodsPage.validateGainsightSettingsHeading(SolQAConstants.GAINSIGHT_SETTINGS_HEADING);
			log.info("Enable Gainsight configuration");
            innovationSuiteFeatureMethodsPage.enableGainsightTogglesIfNotEnabled(SolQAConstants.SAAS);

            log.info("----- Step 1 and 2: Open gainsight bundle product tag mapping, and open the record with description - ITSM Production, review the bundle value ------");
			isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_11709_Driver", isDriver);
            context.setAttribute("DRSQA_11709_Product", Product.Innovation_Suite);
			isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA11709_TestData");
			isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "pbunyon");
			login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
			innovationSuiteFeatureMethodsPage.navigateRequiredISURL(isTestData.get("ProductTagmappingFormName"));
			innovationSuiteFeatureMethodsPage.searchInFormSearchBox(isTestData.get("ProductTagdescription"));
			innovationSuiteFeatureMethodsPage.validateSearchResult(isTestData.get("ProductTagdescription"));
			innovationSuiteFeatureMethodsPage.clickOnSearchResultLink();
			innovationSuiteFeatureMethodsPage.validateEditRecordModalDisplayed();
			Assert.assertTrue(innovationSuiteFeatureMethodsPage.getValueFromBundleIDField().contains(isTestData.get("ValueOfBundleID")),"Bundle ID value doesn't match");

            log.info("----- Step 3: Open Gainsight global context mapping form-> Open " + isTestData.get("Contextdescription") + " -> Verify enteries ------");
			innovationSuiteFeatureMethodsPage.navigateRequiredISURL(isTestData.get("globalContextMappingForm"));
            innovationSuiteFeatureMethodsPage.searchInFormSearchBox(isTestData.get("Contextdescription"));
            innovationSuiteFeatureMethodsPage.validateSearchResult(isTestData.get("Contextdescription"));
            innovationSuiteFeatureMethodsPage.clickOnSearchResultLink();
            innovationSuiteFeatureMethodsPage.validateEditRecordModalDisplayed();
            Assert.assertTrue(innovationSuiteFeatureMethodsPage.getValueFromGlobalContextAreaField().contains(isTestData.get("contextArrayValue")),"global context area value doesn't match");

			log.info("------ Step 4: Login to SmartIT application with user ----------");
			smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITdriver);
            context.setAttribute("DRSQA_11709_Driver", smartITdriver);
            context.setAttribute("DRSQA_11709_Product", Product.SmartIT);

            smartITFeatureMethods = new SmartITFeatureMethodsPage();
            smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
            login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
            smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
            Assert.assertTrue(smartITFeatureMethods.isKnowledgeBotLoaded(), "Knowledge bot is not loaded in smart IT");
            smartITFeatureMethods.openKnowledgeBot();
            smartITFeatureMethods.verifyHelpsAndGuides(isTestData.get("help1"));
            smartITFeatureMethods.verifyHelpsAndGuides(isTestData.get("help2"));
            smartITFeatureMethods.closeKnowledgeBotIfOpened();

			log.info("----- Step 6: Navigate to smart Recorder -> verify self help icon ------");
            smartITFeatureMethods.navigateToSmartRecorder();
            smartITFeatureMethods.acceptNavigationAwayWarningByClickingYesIfPresent();
            Assert.assertTrue(smartITFeatureMethods.isUserNavigateToSmartRecorder(), "User is not navigated to smart recorder");
            Assert.assertTrue(smartITFeatureMethods.isKnowledgeBotLoaded(), "Knowledge bot is not loaded in smart recorder");
            smartITFeatureMethods.openKnowledgeBot();
            smartITFeatureMethods.verifyHelpsAndGuides(isTestData.get("help1"));
            smartITFeatureMethods.verifyHelpsAndGuides(isTestData.get("help2"));
            smartITFeatureMethods.closeKnowledgeBotIfOpened();

            log.info("----- Step 5: Navigate to Livechat Agent's console -> verify self help icon ------");
            smartITFeatureMethods.navigateToLiveChatAgentConsole();
            Assert.assertTrue(smartITFeatureMethods.isKnowledgeBotLoaded(), "Knowledge bot is not loaded in live chat agent console");
            smartITFeatureMethods.openKnowledgeBot();
            smartITFeatureMethods.verifyHelpsAndGuides(isTestData.get("help1"));
            smartITFeatureMethods.verifyHelpsAndGuides(isTestData.get("help2"));
            smartITFeatureMethods.closeKnowledgeBotIfOpened();

        }
        finally {
            log.info("---- Step : PostCondition: Disable Gainsight Configuration --------");
			isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_11709_Driver", isDriver);
            context.setAttribute("DRSQA_11709_Product", Product.Innovation_Suite);

            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
			log.info("Navigate to Gainsight Settings Page");
            innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.GAINSIGHT_SETTINGS_URL);
			log.info("Gainsight Settings URL is - " + SolQAConstants.GAINSIGHT_SETTINGS_URL);
            innovationSuiteFeatureMethodsPage.validateGainsightSettingsHeading(SolQAConstants.GAINSIGHT_SETTINGS_HEADING);
			log.info("Disable Gainsight configuration");
            innovationSuiteFeatureMethodsPage.disableGainsightTogglesIfEnabled();
        }

        log.info("*********** END: Gainsight:ITSM Bundle Mapping - Send global context to Gainsight ***********************");

    }

    @Test(description = "Verify if needs attention flag is flagged when INC SLA is breached", groups = {"Regression_P3", "SmartIT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa",  TTID="2477", JiraID = "DRSQA-11539", Products = {Product.Helix_ITSM, Product.Helix_SmartIT})
    public void DRSQA_11539_Verify_Needs_Attention_Flag_When_INC_SLA_Is_Breached(ITestContext context) {

        log.info("**************** START: DRSQA-11539 Verify if needs attention flag is flagged when INC SLA is breached ****************");
        log.info("DRSQA-11539 Pre-requisite: Enable PV + Needs Attention flag (Centralized Configuration)");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11539_Driver", itsmDriver);
        context.setAttribute("DRSQA_11539_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        log.info("DRSQA-11539 CCS: Enable Needs Attention flag under SmartIT component");
        String[] smartItComponentMenu = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(smartItComponentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.SHOW_NEEDS_ATTENTION_FLAG, SolQAConstants.TRUE);
        itsmFeatureMethods.setSelfService_PV(true, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.LoadForm("SLM:Console", SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.clickOnServiceTargetsTab();
        if (!itsmFeatureMethods.getBuildStatusForServiceTargetId("SLM90103").contains("Built Successfully")) {
            itsmFeatureMethods.clickServiceTargetRowById("SLM90103").clickOnBuildBtn().confirmOperationInPopup();
            commonWaits.waitForSometime(10000); //This wait is added to wait for the build to complete before proceeding with next steps
            Assert.assertTrue(itsmFeatureMethods.getBuildStatusForServiceTargetId("SLM90103").contains("Built Successfully"), "Build status for Service Target ID SLM90103 is not 'Built Successfully' after building it.");
        }
        itsmFeatureMethods.LoadForm("SLM:ConfigDataSource", SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.clickDataSourceByValue("HPD:Help Desk");
        itsmFeatureMethods.clickOnTab("Administration").verifyAllRuleBuildStatusesAreBuiltSuccessfully();
        itsmFeatureMethods.LoadForm("SLM:Console", SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.clickOnServiceTargetsTab();
        Assert.assertTrue(itsmFeatureMethods.getBuildStatusForServiceTargetId("SLM90103").contains("Built Successfully"), "Build status for Service Target ID SLM90103 is not 'Built Successfully'");
        itsmFeatureMethods.clickServiceTargetRowById("SLM90103").clickViewButton();
        itsmFeatureMethods.setSingleGoalAndCostValues("0", "3");
        itsmFeatureMethods.closeBrowser();

        log.info("DRSQA-11539 Step 1: Launch SmartIT and create an Incident");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11539_Driver", smartITdriver);
        context.setAttribute("DRSQA_11539_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nadamUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11539_Data");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("Creating incident in SmartIT");
        smartITFeatureMethods.navigateToCreateNewIncident();
        smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethods.saveIncident();
        String incidentId = smartITFeatureMethods.FetchIncidentTicketID();
        smartITFeatureMethods.closeBrowser();

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11539_Driver", smartITdriver);
        context.setAttribute("DRSQA_11539_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "jholder");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11539_Data");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("DRSQA-11539 Step 2: Open the incident and wait for SLA breach");
        smartITFeatureMethods.navigateToTicketConsole();
        smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.INCIDENT_REQUEST_TYPE, incidentId);
        log.info("DRSQA-11539 Step 3: Verify Needs Attention flag is available and toggle works");
        Assert.assertTrue(smartITFeatureMethods.isNeedsAttentionFlagIconVisibleOnIncident(), "Needs Attention flag icon is not visible on Incident view page.");
        smartITFeatureMethods.SetFlagAndUnFlag("Unflag", "Unflag the Incident");
        smartITFeatureMethods.refreshBrowser();
        commonWaits.waitForSometime(180000); //This wait is added for the SLA to be breached
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.isAttentionFlagSet(), "Needs Attention flag was not set after enabling it.");
        smartITFeatureMethods.clickAssignToMeOnViewPage("Assignee");
        smartITFeatureMethods.refreshBrowser();
        Assert.assertFalse(smartITFeatureMethods.isAttentionFlagSet(), "Needs Attention flag is still set after unsetting it.");
        smartITFeatureMethods.closeBrowser();
        log.info("**************** END: DRSQA-11539 Verify if needs attention flag is flagged when INC SLA is breached ****************");
    }

    @Test(description = "Verify if needs attention flag is flagged when WO SLA is breached", groups = {"Regression_P3", "SmartIT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa",  TTID="2478", JiraID = "DRSQA-11540", Products = {Product.Helix_ITSM, Product.Helix_SmartIT})
    public void DRSQA_11540_Verify_Needs_Attention_Flag_When_WO_SLA_Is_Breached(ITestContext context) {
        log.info("**************** START: DRSQA-11540 Verify if needs attention flag is flagged when WO SLA is breached ****************");

        log.info("DRSQA-11540 Pre-requisite: Enable PV + Needs Attention flag (Centralized Configuration)");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11540_Driver", itsmDriver);
        context.setAttribute("DRSQA_11540_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        log.info("DRSQA-11540 CCS: Enable Needs Attention flag under SmartIT component");
        String[] smartItComponentMenu = new String[]{SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(smartItComponentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.SHOW_NEEDS_ATTENTION_FLAG, SolQAConstants.TRUE);
        itsmFeatureMethods.setSelfService_PV(true, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.LoadForm("SLM:Console", SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.clickOnServiceTargetsTab();
        if (!itsmFeatureMethods.getBuildStatusForServiceTargetId("SLM90107").contains("Built Successfully")) {
            itsmFeatureMethods.clickServiceTargetRowById("SLM90107").clickOnBuildBtn().confirmOperationInPopup();
        }
        itsmFeatureMethods.LoadForm("SLM:ConfigDataSource", SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.clickDataSourceByValue("WOI:WorkOrder");
        itsmFeatureMethods.clickOnTab("Administration").verifyAllRuleBuildStatusesAreBuiltSuccessfully();
        itsmFeatureMethods.LoadForm("SLM:Console", SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.clickOnServiceTargetsTab();
        Assert.assertTrue(itsmFeatureMethods.getBuildStatusForServiceTargetId("SLM90107").contains("Built Successfully"), "Build status for Service Target ID SLM90107 is not 'Built Successfully'");
        itsmFeatureMethods.clickServiceTargetRowById("SLM90107").clickViewButton();
        itsmFeatureMethods.setSingleGoalAndCostValues("0", "3");
        itsmFeatureMethods.closeBrowser();

        log.info("DRSQA-11540 Step 1: Launch SmartIT and create a Work Order");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11540_Driver", smartITdriver);
        context.setAttribute("DRSQA_11540_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "nadamUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11540_Data");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("Creating workorder in SmartIT");
        smartITFeatureMethods.navigateToCreateNewWorkorder();
        smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);
        smartITFeatureMethods.saveWorkorder();
        String workorderId = smartITFeatureMethods.FetchWorkorderTicketID();
        log.info("Created Workorder ID: " + workorderId);
        smartITFeatureMethods.closeBrowser();

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_11540_Driver", smartITdriver);
        context.setAttribute("DRSQA_11540_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "jholder");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11540_Data");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("DRSQA-11540 Step 2: Open the workorder and wait for SLA breach");
        smartITFeatureMethods.navigateToTicketConsole();
        smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.SMARTIT_TICKET_TYPE_WORK_ORDER, workorderId);
        Assert.assertTrue(smartITFeatureMethods.isNeedsAttentionFlagIconVisible(), "Needs Attention flag icon is not visible on Incident view page.");
        smartITFeatureMethods.SetFlagAndUnFlag("Unflag", "Unflag the Incident");
        smartITFeatureMethods.refreshBrowser();
        commonWaits.waitForSometime(180000); //This wait is added for the SLA to be breached
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.isAttentionFlagSet(), "Needs Attention flag was not set after enabling it.");
        smartITFeatureMethods.clickAssignToMeOnViewPage("Assignee");
        smartITFeatureMethods.refreshBrowser();
        Assert.assertFalse(smartITFeatureMethods.isAttentionFlagSet(), "Needs Attention flag is still set after unsetting it.");
        smartITFeatureMethods.closeBrowser();
        log.info("**************** END: DRSQA-11540 Verify if needs attention flag is flagged when WO SLA is breached ****************");
    }
  
	@AfterMethod(alwaysRun = true)
	public void closeBrowsers() {
		if (smartITFeatureMethods != null) {
			smartITFeatureMethods.closeBrowser();
		}
		if (itsmFeatureMethods != null) {
			itsmFeatureMethods.closeBrowser();
		}
		if (dwpFeatureMethod != null) {
			dwpFeatureMethod.closeBrowser();
		}
		if (cmdbFeatureMethods != null) {
			cmdbFeatureMethods.closeBrowser();
		}
		if (innovationSuiteFeatureMethodsPage != null) {
			innovationSuiteFeatureMethodsPage.closeBrowser();
		}
		if (innovationSuiteFeatureMethodsPage != null) {
			innovationSuiteFeatureMethodsPage.closeBrowser();
		}
	}

}
