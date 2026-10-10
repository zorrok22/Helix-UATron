package ITSM.CMDB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.CMDB.CMDBFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class CMDBTests extends Launcher{

    static SolqaLogger log = new SolqaLogger(CMDBTests.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> itsmUserData, cmdbTestData, ISUsersData, ISTestData;
    ITSMFeatureMethods itsmFeatureMethods;
    CMDBFeatureMethods cmdbFeatureMethods;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    WebDriver cmdbdriver, isDriver, itsmDriver, smartITdriver;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        cmdbdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(cmdbdriver);
    }

    //previous owner: shudeshp
    @Test(description = "CMDB BMCOPs CreateCI With Recon", groups = {"MAT", "CMDB", "ITSM"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11817", Products = {Product.ITSM, Product.CMDB})
    public void DRSQA_11817_CMDB_BMCOPs_CreateCI_with_Recon(ITestContext context) throws InterruptedException {

        log.info("**************** Start DRSQA-11817: CMDB-BMCOPs-CreateCI_wtih_Recon ******************");
        // Use existing WebDriver from ThreadLocal
        log.info("----step1:- Login to ITSM application ----");
        context.setAttribute("DRSQA_11817_Driver", getDriver());
        context.setAttribute("DRSQA_11817_Product", Product.Helix_ITSM);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_11817_TestData");
      
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

        log.info("**** Login to ITSM and cross launch CMDB from atrium core ****");
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        
        log.info("-------Step2:- Atrium Core > Configuration Manager Dashboard >  CI > Create CI, provide required information and create CI--------");
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();
        String CIName = cmdbFeatureMethods.createCI(cmdbTestData);

        log.info("**** Search on BMC.CORE:BMC_ComputerSystem a CIName = "+CIName+"  ****");
        itsmFeatureMethods.switchToITSMTab("BMC Helix (Search)");
        itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_COMPUTERSYSTEM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
        itsmFeatureMethods.searchCI(cmdbTestData.get("dataSetValue"),CIName);
        log.info("************ getReconID and it should be 0 before recon job run ID = "+itsmFeatureMethods.getReconciliationIdentity()+ "*************");
        Assert.assertEquals(itsmFeatureMethods.getReconciliationIdentity(), "0", "Before recon job execution value should 0");

        log.info("--------Step3:- Now on CMDB Dashboard >  Select  Reconciliation Jobs / BMC Asset Management - SandBox and click Start to run the job-------");		
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem1"), "");
        log.info("**** Run recon job "+cmdbTestData.get("reconciliationJob")+" ****");
        cmdbFeatureMethods.runReconciliationJob(cmdbTestData.get("reconciliationJob"));

        log.info("-------step4:- Verify the  Reconciliation Job status -------");
        itsmFeatureMethods.switchToITSMTab("BMC.CORE:BMC_ComputerSystem (Modify)");
        itsmFeatureMethods.refreshBrowser();
        itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_COMPUTERSYSTEM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));

        log.info("**** Search on BMC.CORE:BMC_ComputerSystem a CIName = "+CIName+"  ****");
        itsmFeatureMethods.searchCI(cmdbTestData.get("dataSetValue"),CIName);

        log.info("---------Step5:- Verify the Recon id --------");
        log.info("************ getReconID and it should not be 0 after recon job run ReconID = "+itsmFeatureMethods.getReconciliationIdentity()+ "*************");
        Assert.assertNotEquals(itsmFeatureMethods.getReconciliationIdentity(), "0", "After recon job execution value should not be 0");
    }

    @Test(description = "[25.4.00] Gainsight for CMDB UI: add Gainsight 'Analytics' option and 'opt out of analytics' UI", groups = {"Regression_P1", "CMDB", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-14638", Products = {Product.ITSM, Product.CMDB})
    public void DRSQA_14638_Gainsight_for_CMDB_UI_Analytics(ITestContext context) {

        log.info("**************** Start DRSQA-14638: Gainsight for CMDB UI - Analytics option and opt out UI ******************");
        
        try {
        log.info("Step 1: DRSQA-14638_Pre-requisite Steps: START *********");
        
        log.info("Step 1.1: Login to Innovation Suite");
        context.setAttribute("DRSQA_14638_Driver", getDriver());
        context.setAttribute("DRSQA_14638_Product", Product.Helix_Platform);

        ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Liam");
        ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_14638Data");

        log.info("Logging in to Innovation Suite");
        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appIS);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 1.2: Loading IS Form - Feature Descriptor and validate form loaded");
        log.info("Navigating to Feature Descriptor URL: " + SolQAConstants.FEATURE_DESCRIPTOR_URL);
        innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.FEATURE_DESCRIPTOR_URL);
        innovationSuiteFeatureMethodsPage.validateFormHeading(SolQAConstants.FEATURE_DESCRIPTOR_FORM_HEADING);
        
        log.info("Step 1.3: Enabling Feature flag in Feature Descriptor form");
        String searchCMDBGainsight = ISTestData.get("SearchCMDBGainsight");
        innovationSuiteFeatureMethodsPage.enableFeatureFlagIfNotEnabled(searchCMDBGainsight);
        
        log.info("Step 1.4: Loading IS Form - Gainsight settings and enable gainsight feature");
        log.info("Navigating to Gainsight Settings URL: " + SolQAConstants.GAINSIGHT_SETTINGS_URL);
        innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.GAINSIGHT_SETTINGS_URL);
        innovationSuiteFeatureMethodsPage.validateGainsightSettingsHeading(SolQAConstants.GAINSIGHT_SETTINGS_HEADING);
        innovationSuiteFeatureMethodsPage.enableGainsightTogglesIfNotEnabled(ISTestData.get("DeploymentType"));
        
        log.info("Step 1.5: Loading IS form - Gainsight Bundle Product Tag Mapping");
        log.info("Navigating to Gainsight Bundle Product Tag Mapping URL: " + SolQAConstants.GAINSIGHT_BUNDLE_PRODUCT_TAG_MAPPING_URL);
        innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.GAINSIGHT_BUNDLE_PRODUCT_TAG_MAPPING_URL);
        innovationSuiteFeatureMethodsPage.validateFormHeading(SolQAConstants.GAINSIGHT_BUNDLE_PRODUCT_TAG_MAPPING_HEADING);
        
        log.info("Step 1.6: Enabling Bundle ID mapping for CMDB QA");
        String searchCMDBQA = ISTestData.get("SearchCMDBQA");
        innovationSuiteFeatureMethodsPage.enableBundleIdMappingIfNotEnabled(searchCMDBQA);
        
        log.info("Step 1.7: Loading IS form - Gainsight view global context mapping");
        log.info("Navigating to Gainsight view global context mapping URL: " + SolQAConstants.GAINSIGHT_VIEW_GLOBAL_CONTEXT_MAPPING_URL);
        innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.GAINSIGHT_VIEW_GLOBAL_CONTEXT_MAPPING_URL);
        innovationSuiteFeatureMethodsPage.validateFormHeading(SolQAConstants.GAINSIGHT_VIEW_GLOBAL_CONTEXT_MAPPING_HEADING);
        String searchCMDBMapping = ISTestData.get("SearchCMDBMapping");
        
        // Read global context value from separate JSON file
        String globalContextValue = "";
        try {
            String filePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + 
                            SolQAConstants.appIS + File.separator + "DRSQA_14638_GlobalContextValue.json";
            globalContextValue = new String(Files.readAllBytes(Paths.get(filePath)));
            log.info("Successfully read global context value from DRSQA_14638_GlobalContextValue.json");
        } catch (IOException e) {
            log.error("Failed to read global context value from JSON file: " + e.getMessage());
            Assert.fail("Failed to read global context value from JSON file: " + e.getMessage());
        }
        
        innovationSuiteFeatureMethodsPage.updateGlobalContextMappingAndSave(searchCMDBMapping, globalContextValue);
        log.info("Completed Step 1.7: Global context mapping updated successfully");
        
        log.info("Step 1: DRSQA-14638_Pre-requisite Steps: END *********");
        
        this.itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_14638_TestData");

        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

        log.info("Step 2: DRSQA-14638: Login to ITSM and cross launch CMDB from atrium core");
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);

        log.info("Step 3: DRSQA-14638: Verify CMDB login successful");
        cmdbFeatureMethods.verifyLoginTo_CMDB();

        log.info("Step 4: DRSQA-14638: Validate Self Help Icon (Gainsight KC Bot) and What's New Tour");
        cmdbFeatureMethods.validateSelfHelpIconAndHelpGuides();

        log.info("Step 5: DRSQA-14638: Validate Documentation and Videos links");
        String cmdbDocumentationURL = cmdbTestData.get("CMDBDocumentationURL");
        cmdbFeatureMethods.validateDocumentationAndVideosLinks(cmdbDocumentationURL);

        log.info("Step 6: DRSQA-14638: Verify Analytics and Improvements popup");
        cmdbFeatureMethods.verifyAnalyticsAndImprovementsPopup();
        
        }
        
        finally {
            log.info("---- Step 7: PostCondition: Disable Gainsight Configuration --------");
			isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_14638_Driver", isDriver);
            context.setAttribute("DRSQA_14638_Product", Product.Innovation_Suite);

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

        log.info("**************** End DRSQA-14638: Gainsight for CMDB UI - Analytics option and opt out UI ******************");
    }

    @Test(description = "CMDB-AAA-CreateAutomationForCI-to-People_relationship", groups = {"Regression_P2", "CMDB", "Innovation_Suite", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11774", Products = {Product.ITSM, Product.Innovation_Suite, Product.CMDB})
    public void DRSQA_11774_CMDB_AAA_CreateAutomationForCI_to_People_relationship(ITestContext context) {

        log.info("**************** Start DRSQA-11774: CMDB-AAA-CreateAutomationForCI-to-People_relationship ******************");
        
        log.info("Step 1: Login to Innovation Studio with admin user");
        context.setAttribute("DRSQA_11774_Driver", getDriver());
        context.setAttribute("DRSQA_11774_Product", Product.Helix_Platform);

        ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11774Data");

        log.info("Launching Innovation Suite");
        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
        log.info("Logged in user: " + configProp.get("adminUserName"));
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to Automated Asset Assignment bundle");
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.AUTOMATED_ASSET_ASSIGNMENT);
        log.info("Navigated to Automated Asset Assignment bundle");
        
        log.info("Step 3: Click on Visit Deployed Application link");
        innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
        innovationSuiteFeatureMethodsPage.verifyAutomatedAssetAssignmentPageLaunched();
        log.info("Automated Asset Assignment application opened and verified successfully");

        log.info("Step 4: Click on Asset Automations tab");
        innovationSuiteFeatureMethodsPage.clickAssetAutomationsTab();
        log.info("Asset Automations tab clicked successfully");

        log.info("Step 5: Click on Create automation button");
        innovationSuiteFeatureMethodsPage.clickCreateAutomation();
        innovationSuiteFeatureMethodsPage.verifyCreateAutomationPageDisplayed();
        log.info("Create automation page displayed and verified successfully");

        log.info("Step 6: Fill Automation Rule Template and Save");
        String automationName = innovationSuiteFeatureMethodsPage.createAutomationRule(ISTestData);
        log.info("Automation rule created successfully: " + automationName);

        log.info("Step 7: Enable the automation rule and verify status changes to Active");
        innovationSuiteFeatureMethodsPage.enableAutomationRule(automationName);
        log.info("Automation rule enabled successfully");

        log.info("Step 8: Run Automation Job - Add automation job, Preview, Select Asset, Run Job");
        innovationSuiteFeatureMethodsPage.runAutomationJob(automationName);
        log.info("Automation job created successfully");

        log.info("Step 9: Verify Automation Job status in Automation Job Console");
        innovationSuiteFeatureMethodsPage.verifyAutomationJobInConsole();
        log.info("Automation job status verified as Completed");

        log.info("**************** End DRSQA-11774: CMDB-AAA-CreateAutomationForCI-to-People_relationship ******************");
    }

    @Test(description = "[25.4.00] Validate CMDB Data Monitor Functionality", groups = {"Regression_P3", "CMDB", "Innovation_Suite", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-14659", Products = {Product.ITSM, Product.Innovation_Suite, Product.CMDB})
    public void DRSQA_14659_Validate_CMDB_Data_Monitor_Functionality(ITestContext context) {

        log.info("**************** Start DRSQA-14659: Validate CMDB Data Monitor Functionality ******************");
        
        log.info("Step 1: Login to Innovation Studio with admin user");
        context.setAttribute("DRSQA_14659_Driver", getDriver());
        context.setAttribute("DRSQA_14659_Product", Product.Helix_Platform);

        ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_14659Data");

        log.info("Launching Innovation Suite");
        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
        log.info("Logged in user: " + configProp.get("adminUserName"));
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to Automated Asset Assignment bundle");
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.AUTOMATED_ASSET_ASSIGNMENT);
        log.info("Navigated to Automated Asset Assignment bundle");
        
        log.info("Step 3: Click on Visit Deployed Application link");
        innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
        innovationSuiteFeatureMethodsPage.verifyAutomatedAssetAssignmentPageLaunched();
        log.info("Automated Asset Assignment application opened and verified successfully");

        log.info("Step 4: Click on Data Monitor tab");
        innovationSuiteFeatureMethodsPage.clickDataMonitorTab();
        log.info("Data Monitor tab clicked successfully");

       log.info("Step 5: Verify Data Monitor columns are displayed");
       innovationSuiteFeatureMethodsPage.verifyDataMonitorColumns();
       log.info("Data Monitor page verified with all columns: Name, CI Matches, Last Run, Priority, Status");

       log.info("Step 6: Verify Data Monitor tiles are displayed with counts");
       innovationSuiteFeatureMethodsPage.verifyDataMonitorTiles();
       log.info("Data Monitor tiles verified: Total rules, Rules with open issues, Active rules, Inactive rules");

        log.info("Step 7: Create new monitoring rule with Daily frequency");
        String createdRuleName = innovationSuiteFeatureMethodsPage.createMonitoringRuleWithFrequency(ISTestData, SolQAConstants.MONITORING_RULE_FREQUENCY_DAILY);
        log.info("New monitoring rule created successfully: " + createdRuleName);

        log.info("Step 8: Edit the created monitoring rule");
        String editedDescription = "Edited description - " + ISTestData.get("MonitoringRuleDescription");
        String updatedRuleName = innovationSuiteFeatureMethodsPage.editMonitoringRule(createdRuleName, editedDescription);
        log.info("Monitoring rule edited successfully. Updated name: " + updatedRuleName);

        log.info("Step 9: Verify updated rule reflects changes in main grid");
        innovationSuiteFeatureMethodsPage.verifyMonitoringRuleInGrid(updatedRuleName);
        log.info("Updated monitoring rule verified in grid: " + updatedRuleName);

        log.info("Step 10: Enable monitoring rule");
        innovationSuiteFeatureMethodsPage.enableMonitoringRule(updatedRuleName);
        innovationSuiteFeatureMethodsPage.verifyMonitoringRuleStatusActive(updatedRuleName);
        
        log.info("Step 11: Run monitoring rule on-demand");
        innovationSuiteFeatureMethodsPage.runMonitoringRuleOnDemand(updatedRuleName);
        
        log.info("Step 12: Wait for rule execution and refresh grid");
        innovationSuiteFeatureMethodsPage.clickRefreshButton();
        
        log.info("Step 13: Verify rule execution status - Completed");
        innovationSuiteFeatureMethodsPage.verifyMonitoringRuleCompletionStatus(updatedRuleName);
        
        log.info("Step 14: View monitoring rule results");
        innovationSuiteFeatureMethodsPage.viewMonitoringRuleResults(updatedRuleName);
        
        log.info("Step 15: Click on CI link and verify cross-launch to CMDB");
        String ciName = innovationSuiteFeatureMethodsPage.clickCIResultAndVerifyCrossLaunch();
        log.pass("CI cross-launched to CMDB tab successfully. CI Name: " + ciName);
       
        log.info("Step 16: Disable monitoring rule");
        innovationSuiteFeatureMethodsPage.disableMonitoringRule(updatedRuleName);
        innovationSuiteFeatureMethodsPage.verifyMonitoringRuleStatusInactive(updatedRuleName);
        
        log.info("Step 17: Delete monitoring rule");
        innovationSuiteFeatureMethodsPage.deleteMonitoringRule(updatedRuleName);
        log.pass("Monitoring rule deleted successfully: " + updatedRuleName);
        
        log.info("Step 18: Verify rule is removed from grid");
        innovationSuiteFeatureMethodsPage.verifyMonitoringRuleNotInGrid(updatedRuleName);
        log.pass("Monitoring rule verified removed from grid: " + updatedRuleName);
        
        log.info("Step 19: Create monitoring rule with Weekly frequency");
        String weeklyRuleName = innovationSuiteFeatureMethodsPage.createMonitoringRuleWithFrequency(ISTestData, SolQAConstants.MONITORING_RULE_FREQUENCY_WEEKLY);
        log.pass("Weekly frequency monitoring rule created: " + weeklyRuleName);
        
        log.info("Step 20: Create monitoring rule with Monthly frequency");
        String monthlyRuleName = innovationSuiteFeatureMethodsPage.createMonitoringRuleWithFrequency(ISTestData, SolQAConstants.MONITORING_RULE_FREQUENCY_MONTHLY);
        log.pass("Monthly frequency monitoring rule created: " + monthlyRuleName);

        log.info("**************** End DRSQA-14659: Validate CMDB Data Monitor Functionality ******************");
    }

	@Test(description = "Data Monitor HelixGPT Agentic assisted resolution", groups = { "Regression", "CMDB", "Innovation_Suite" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16269", Products = { Product.Innovation_Suite })
	public void DRSQA_16269_Validate_Data_Monitor_HelixGPT_Assisted_Resolution(ITestContext context) {

		log.info("**************** Start DRSQA-16269: Data Monitor HelixGPT Agentic assisted resolution ******************");

		log.info("Step 1: Login to Innovation Studio with admin user");
		context.setAttribute("DRSQA_16269_Driver", getDriver());
		context.setAttribute("DRSQA_16269_Product", Product.Helix_Platform);

		ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
				"DRSQA_16269Data");

		log.info("Launching Innovation Suite");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

		log.info("Step 2: Navigate to Automated Asset Assignment (AAA) bundle");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.AUTOMATED_ASSET_ASSIGNMENT);
		log.info("Navigated to Automated Asset Assignment bundle successfully");

		log.info("Step 3: Click on Visit Deployed Application link to open AAA Application");
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.verifyAutomatedAssetAssignmentPageLaunched();
		log.info("AAA Application opened and verified successfully");

		log.info("Step 4: Click on Data Monitor tab");
		innovationSuiteFeatureMethodsPage.clickDataMonitorTab();
		log.info("Data Monitor tab clicked and page opened successfully");

		log.info("Step 5: Create monitoring rule with details from test data");
		String ruleName = innovationSuiteFeatureMethodsPage.createMonitoringRule(ISTestData.get("MonitoringRuleName"),
				ISTestData.get("MonitoringRuleDescription"), ISTestData.get("MonitoringRuleClass"),
				ISTestData.get("MonitoringRuleFrequency"), ISTestData.get("MonitoringRulePriority"), null,
				ISTestData.get("ExpressionField"), ISTestData.get("ExpressionOperator"),
				ISTestData.get("ExpressionValue"));
		log.info("Monitoring rule created successfully: " + ruleName);

		log.info("Step 6: Verify rule created in grid");
		innovationSuiteFeatureMethodsPage.verifyMonitoringRuleInGrid(ruleName);
		log.info("Rule verified in grid: " + ruleName);

		log.info("Step 7: Enable the monitoring rule");
		innovationSuiteFeatureMethodsPage.enableMonitoringRule(ruleName);
		log.info("Monitoring rule enabled successfully");

		log.info("Step 8: Run the monitoring rule on-demand (three dots → Run now)");
		innovationSuiteFeatureMethodsPage.runMonitoringRuleOnDemand(ruleName);
		log.info("Monitoring rule run successfully");

		log.info("Step 9: Verify rule execution completed successfully (auto-refresh until Completed status)");
		innovationSuiteFeatureMethodsPage.verifyMonitoringRuleCompletionStatusWithRefresh(ruleName);
		log.info("Rule execution status verified as Completed");

		log.info("Step 11: Click View Insight and validate Analysis Summary");
		// Build list of expected analysis summary texts from test data
		List<String> analysisSummaryTexts = new ArrayList<>();
		analysisSummaryTexts.add(ISTestData.get("AnalysisSummaryText1"));
		analysisSummaryTexts.add(ISTestData.get("AnalysisSummaryText2"));

		// Validate View Insight result with all expected texts (true = View Insights option should be visible)
		innovationSuiteFeatureMethodsPage.validateViewInsightResult(ruleName, analysisSummaryTexts, true);
		log.info("View Insight validated successfully with all expected analysis summary texts");

		log.info("Step 12: Create second monitoring rule for Normalization Failed");
		String ruleName2 = innovationSuiteFeatureMethodsPage.createMonitoringRule(ISTestData.get("MonitoringRuleName2"),
				ISTestData.get("MonitoringRuleDescription2"), ISTestData.get("MonitoringRuleClass2"),
				ISTestData.get("MonitoringRuleFrequency2"), ISTestData.get("MonitoringRulePriority2"),
				ISTestData.get("MonitoringRuleClassType"),
				ISTestData.get("ExpressionField2"), ISTestData.get("ExpressionOperator2"),
				ISTestData.get("ExpressionValue2"));
		log.info("Second monitoring rule created successfully: " + ruleName2);

		log.info("Step 13: Verify second rule created in grid");
		innovationSuiteFeatureMethodsPage.verifyMonitoringRuleInGrid(ruleName2);
		log.info("Second rule verified in grid: " + ruleName2);

		log.info("Step 14: Enable the second monitoring rule");
		innovationSuiteFeatureMethodsPage.enableMonitoringRule(ruleName2);
		log.info("Second monitoring rule enabled successfully");

		log.info("Step 15: Run the second monitoring rule on-demand (three dots → Run now)");
		innovationSuiteFeatureMethodsPage.runMonitoringRuleOnDemand(ruleName2);
		log.info("Second monitoring rule run successfully");

		log.info("Step 16: Verify second rule execution completed successfully (auto-refresh until Completed status)");
		innovationSuiteFeatureMethodsPage.verifyMonitoringRuleCompletionStatusWithRefresh(ruleName2);
		log.info("Second rule execution status verified as Completed");

		log.info("Step 17: Click View Insight for second rule and validate Analysis Summary");
		innovationSuiteFeatureMethodsPage.validateViewInsightResult(ruleName2, analysisSummaryTexts, true);
		log.info("View Insight for second rule validated successfully with all expected analysis summary texts");

		log.info("Step 18: Create third monitoring rule for Normalization Failed (without expression)");
		String ruleName3 = innovationSuiteFeatureMethodsPage.createMonitoringRule(ISTestData.get("MonitoringRuleName3"),
				ISTestData.get("MonitoringRuleDescription3"), ISTestData.get("MonitoringRuleClass3"),
				ISTestData.get("MonitoringRuleFrequency3"), ISTestData.get("MonitoringRulePriority3"), null, 
				ISTestData.get("ExpressionField3"), ISTestData.get("ExpressionOperator3"),
				ISTestData.get("ExpressionValue3"));
		log.info("Third monitoring rule created successfully (Normalization Failed): " + ruleName3);

		log.info("Step 19: Verify third rule created in grid");
		innovationSuiteFeatureMethodsPage.verifyMonitoringRuleInGrid(ruleName3);
		log.info("Third rule verified in grid: " + ruleName3);

		log.info("Step 20: Enable the third monitoring rule");
		innovationSuiteFeatureMethodsPage.enableMonitoringRule(ruleName3);
		log.info("Third monitoring rule enabled successfully");

		log.info("Step 21: Run the third monitoring rule on-demand (three dots → Run now)");
		innovationSuiteFeatureMethodsPage.runMonitoringRuleOnDemand(ruleName3);
		log.info("Third monitoring rule run successfully");

		log.info("Step 22: Verify third rule execution completed successfully (auto-refresh until Completed status)");
		innovationSuiteFeatureMethodsPage.verifyMonitoringRuleCompletionStatusWithRefresh(ruleName3);
		log.info("Third rule execution status verified as Completed");

		log.info("Step 23: Validate that View Insights is NOT enabled for third rule (Normalization Failed)");
		innovationSuiteFeatureMethodsPage.validateViewInsightResult(ruleName3, Collections.emptyList(), false);
		log.info("View Insights option verified as NOT enabled for third rule (as expected)");

		log.info(
				"**************** End DRSQA-16269: Login to IS, Open AAA Application, Create and Run Data Monitor Rule ******************");
	}

    @Test(description = "Verify Purge and Delete activity deletes endpoint CIs when qualification is only for relationship CIs",groups = {"Regression_P3", "CMDB", "ITSM","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-15861", Products = {Product.ITSM, Product.CMDB})
    public void DRSQA_15861_Verify_Purge_And_Delete_When_Qualification_For_Relationship_CIs(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA-15861: Verify Purge and Delete activity deletes endpoint CIs when qualification is only for relationship CIs ******************");
        log.info("Precondition: Step 1: Set CCS parameters RE-Enable-MiscService-Delete and RE-Enable-MiscService-Purge to 'F' (precondition)");
        context.setAttribute("DRSQA_15861_Driver", getDriver());
        context.setAttribute("DRSQA_15861_Product", Product.Helix_ITSM);

        itsmFeatureMethods = new ITSMFeatureMethods();
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_15861_TestData");
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] reconciliationComponentPath = new String[] { SolQAConstants.COM_BMC_ARSYS_OTHER, SolQAConstants.RECONCILIATION_ENGINE };
        itsmFeatureMethods.selectMenuItemFromComponentMenu(reconciliationComponentPath);
        itsmFeatureMethods.addCCSParameters(SolQAConstants.RE_ENABLE_MISC_SERVICE_DELETE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.RE_ENABLE_MISC_SERVICE_DELETE, SolQAConstants.F);
        itsmFeatureMethods.addCCSParameters(SolQAConstants.RE_ENABLE_MISC_SERVICE_PURGE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.RE_ENABLE_MISC_SERVICE_PURGE, SolQAConstants.F);

        log.info("Precondition: Step 2: Create a Rule:Go to Configuration->Manage Reconciliation Rules->Qualification Rule-> Add Ruleset give name=Test_Rule and click on save Click on Add Qualification Rule and Create Qualification Rule >Rule Name=SolQA_Rule and click on save >Click on Add Qualification Rule and Create Qualification Rule > Class Name=BMC.CORE:BMC_BaseRelationship and click on save >'MarkAsDeleted' = \"Yes\" and click on save");
        itsmFeatureMethods.LoadForm(SolQAConstants.SHR_LANDING_CONSOLE, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("MainMenu"), cmdbTestData.get("SubChildMenu"),cmdbTestData.get("SubMenu"));
        cmdbFeatureMethods.addQualificationRuleAndSave(cmdbTestData.get("RuleSetName"), cmdbTestData.get("RuleName"), cmdbTestData.get("QualificationValue"), cmdbTestData.get("ClassName"));

        log.info("Precondition: Step 3: Create a Dataset->Datasource->Manage Datasource->Add Dataset > Name=BMC.SOLQA > DatasetID=BMC.SOLQA > Permissions=Public");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("DatasourceMainMenu"), cmdbTestData.get("DatasourceSubMenu"),"");
        cmdbFeatureMethods.addDatasetAndSave(cmdbTestData.get("DatasetName"), cmdbTestData.get("DatasetID"), cmdbTestData.get("Permissions"), "");

        log.info("Precondition: Step 4: Create 10 CI > Open ComputerSystem form BMC.CORE:BMC_ComputerSystem > CI1=Comsys1 > Datasetid:BMC.SOLQA > Mark As deleted=Yes > Create upto 10 CI in BMC.CORE:BMC_ComputerSystem form");
        itsmFeatureMethods.switchToParentWindow();
        itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_COMPUTERSYSTEM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
        cmdbFeatureMethods.createMultipleCIs(cmdbTestData, 11);
       
        log.info("Precondition: Step 5: Create 5 Relationship with above CI with MAD=Yes");
        itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_BASERELATIONSHIP, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
        cmdbFeatureMethods.createMultipleRelationships(cmdbTestData, 5);
        itsmFeatureMethods.closeBrowser();

        log.info("--------Step 1: Login to ITSM Application --------------");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_15861_Driver", getDriver());
        context.setAttribute("DRSQA_15861_Product", Product.Helix_ITSM);
        
        itsmFeatureMethods = new ITSMFeatureMethods();
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_15861_TestData");
        itsmFeatureMethods.verifyITSMLogin();

        log.info("--------Step 2: go to manage dashboard --------------");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();

        log.info("--------Step 3: Create Reconciliation Job with Purge Activity --------------");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem1"), "");
        cmdbFeatureMethods.createReconciliationJobWithRequiredActivities(cmdbTestData, SolQAConstants.PURGE, cmdbTestData.get("PurgeJobName"));

        log.info("--------Step 4: Executed the created job in step 4 and verify the CI and Relationship --------------");
        cmdbFeatureMethods.runReconciliationJob(cmdbTestData.get("PurgeJobName"));
        cmdbFeatureMethods.validateJobRunStatistics(cmdbTestData);

        log.info("--------Step 5 and 6: validate for Delete Activity, Create a CI with strong relationship same with above --------------");
         itsmFeatureMethods.switchToParentWindow();
         itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_COMPUTERSYSTEM, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
         cmdbFeatureMethods.createMultipleCIs(cmdbTestData, 11);
       
         log.info("Precondition: Step 5: Create 5 Relationship with above CI with MAD=Yes");
         itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_BASERELATIONSHIP, SolQAConstants.DEFAULT, configProp.get("ARServerAlias"));
         cmdbFeatureMethods.createMultipleRelationships(cmdbTestData, 5);
       
         log.info("--------Step 7: Create Reconciliation Job with Delete Activity --------------");
         cmdbFeatureMethods.switchToTabByIndex(0);
         itsmFeatureMethods.refreshBrowser();
         itsmFeatureMethods.verifyITSMLogin();
         itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
         cmdbFeatureMethods = new CMDBFeatureMethods();
         cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
         cmdbFeatureMethods.verifyLoginTo_CMDB();
         cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem1"), "");
         cmdbFeatureMethods.createReconciliationJobWithRequiredActivities(cmdbTestData, SolQAConstants.DELETE, cmdbTestData.get("DeleteJobName"));  

        log.info("--------Step 8: Executed the created job in step 7 and verify the CI and Relationship--------------");
        cmdbFeatureMethods.runReconciliationJob(cmdbTestData.get("DeleteJobName"));
        cmdbFeatureMethods.validateJobRunStatistics(cmdbTestData);

        log.info("**************** End DRSQA-15861: Verify Purge and Delete activity deletes endpoint CIs when qualification is only for relationship CIs ******************");
    }

    @Test(description = "Rerun Impact Analysis", enabled = false, groups = {"Regression_P2", "CMDB", "ITSM","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-13298", Products = {Product.ITSM, Product.Innovation_Suite, Product.CMDB})
    public void DRSQA_13298_Rerun_Impact_Analysis(ITestContext context) throws InterruptedException {

        log.info("**************** Start DRSQA-13298: Rerun Impact Analysis ******************");
        try{       
        log.info("(Assuming this is already done via pipeline) Precondition Step1:  Log in to Innovation Studio, open the shared Service Library → Application Configuration data editor, search for ENABLE_NETWORK_IMPACT_ANALYSIS with Application ID com.bmc.dsm.network-impact-analysis, set Value = true, and ensure BULK_RELATE_ACTION_CI_COUNT = 100.");
        log.info("ADE should be integrated to execute the impact analysis.");
        log.info("Precondition Step2: CCS configuration under com.bmc.arsys.itsm. > Check the following values: > EnableNetOps=true > Change_Impact_Analyzer_Option=both");
        context.setAttribute("DRSQA_13298_Driver", itsmDriver);
        context.setAttribute("DRSQA_13298_Product", Product.Helix_ITSM);

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

        log.info(": Enable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
        isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_13298_Driver", isDriver);
        context.setAttribute("DRSQA_13298_Product", Product.Helix_SmartIT);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.ITSM_APPLICATION_BUNDLE);
        innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
        innovationSuiteFeatureMethodsPage.clickOnSettings();
        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
        innovationSuiteFeatureMethodsPage.enableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);

        log.info("----------- Precondition Step4: .CMDB-DSM Sync Ensure Atrium Core → Configure Manager Dashboard → Configuration → SmartGraph Sync is scheduled with the Enable Schedule toggle ON. Set it to once and save it.    ----------");
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

        log.info("----------Step1:- Login to Smart IT with pbunyon user  ------------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_13298_Driver", smartITdriver);
        context.setAttribute("DRSQA_13298_Product", Product.Helix_SmartIT);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13298_TestData");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        log.info("Smart IT page launched successfully");

        log.info("--------- Step2:- Open Console and navigate to Impact analysis Console -------------");
        smartITFeatureMethods.navigateToOptionInAdvancedResponsiveNavigation(SolQAConstants.CONSOLE, SolQAConstants.IMPACT_ANALYSIS_CONSOLE);

        log.info("Step 3: Click on create the impact analysis run and verify open New impact analysis run panel and fill description");
        String description = cmdbTestData.get("Description") + "_" + UUID.randomUUID().toString().substring(0, 8);
        smartITFeatureMethods.clickOnImactAnalysisRunAndRunAnalysis(description, cmdbTestData.get("TicketType"), cmdbTestData.get("TicketValue"));
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.getImpactAnalysisRunStatus().contains(SolQAConstants.SUCCESSFUL), "Impact Analysis Run Status is not Successful");
        log.info("Impact Analysis Run Status is Successful");
        
        log.info("Step 4: Three Dots click on Rerun Impact Analysis option ");
        smartITFeatureMethods.navigateToRequiredOptionThrough3DotsMenu(SolQAConstants.RERUN_IMPACT_ANALYSIS);
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.getImpactAnalysisDescription().contains(SolQAConstants.RERUN), "Impact Analysis Run Status is not Rerun. Got: " + smartITFeatureMethods.getImpactAnalysisRunStatus() + " but Expected: " + SolQAConstants.RERUN);

        log.info("Step 5: Click on right corner 3 dot and create incident with required field");
        smartITFeatureMethods.navigateToRequiredOptionThrough3DotsMenu(SolQAConstants.CREATE_INCIDENT);
        smartITFeatureMethods.FillIncidentTemplateinImpactAnalysisPage(cmdbTestData);
        smartITFeatureMethods.clickOnSaveIncidentButton();

        log.info("Step 6: Open Incident created in step 4 and verify configuration is realted to and check activity log");
        smartITFeatureMethods.openFirstIncident();
        String requestID =smartITFeatureMethods.getIncidentRequestID().replace(":", "").trim();
        log.info("Request ID is: " + requestID);
        smartITFeatureMethods.verifyActivityNotesinImpactAnalysisPage(cmdbTestData.get("ActivityFirstNoteText"));
        log.info("Activity First Note Text is as expected");
        String iarId = smartITFeatureMethods.getActivityFirstNoteText_ActivityIARID();
        Assert.assertNotNull(iarId, "IAR ID was not found in activity message");
        log.info("IAR ID is: " + iarId);

        log.info("Step 7: Login to ITSM and open AR System Email Messages to check for the request.");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_13298_Driver", itsmDriver);
        context.setAttribute("DRSQA_13298_Product", Product.Helix_ITSM);

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.LoadForm(SolQAConstants.AR_SYSTEM_EMAIL_MESSAGES, SolQAConstants.DEFAULT_USER_VIEW, configProp.get("ARServerAlias"));
        itsmFeatureMethods.searchINCRequestIDInEmailMessages(requestID);
        Assert.assertTrue(itsmFeatureMethods.getINCRequestIDFoundInEmailMessages().contains(requestID), "Request ID is not found in email messages");   
        log.info("Request ID is found in email messages");   
        }
        finally{    
            log.info(": Disable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
            isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_13298_Driver", isDriver);
            context.setAttribute("DRSQA_13298_Product", Product.Helix_InnovationStudio);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
            innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.ITSM_APPLICATION_BUNDLE);
            innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
            innovationSuiteFeatureMethodsPage.clickOnSettings();
            innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
            innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
            isDriver.quit();

            
            log.info("Step 9: Disable the shared ticket console settings");
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
        log.info("**************** End DRSQA-13298: Rerun Impact Analysis - Configuration Preconditions ******************");
    }
    
    @Test(description = "Verify the CMDB Functionality Create CI, Normalization Job, Recon Job, Dataset and Datasource", groups = {"Regression_P3",  "CMDB", "ITSM", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-13335", Products = {Product.ITSM, Product.CMDB})
    public void DRSQA_13335_Verify_the_CMDB_Functionality_Create_CI_Normalization_Job_Recon_Job_Dataset_and_Datasource(ITestContext context) throws InterruptedException, IOException {

        log.info("***** Start DRSQA_13335_Verify_the_CMDB_Functionality_Create_CI_Normalization_Job_Recon_Job_Dataset_and_Datasource *****");
        
        log.info("Step 1: Login to ITSM application");
        context.setAttribute("DRSQA_13335_Driver", getDriver());
        context.setAttribute("DRSQA_13335_Product", Product.Helix_ITSM);

        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13335_TestData");
      
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.captureScreenshot("DRSQA_13335_ITSM_Login", SolQAConstants.appITSM);
        
        log.info("Step 2: Navigate to Atrium Core > Configuration Manager Dashboard");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_CMDB_Dashboard", SolQAConstants.CMDB);
        
        log.info("Step 3: Navigate to CIs > Create CI with Dataset=BMC.ASSET.SANDBOX, Class=BMC_ComputerSystem");
        String CIName = cmdbFeatureMethods.createCI(cmdbTestData);
        log.info("Created CI with name: " + CIName);
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_CI_Created", SolQAConstants.CMDB);
        Assert.assertNotNull(CIName, "CI should be created successfully");

        log.info("Step 4: Navigate to Job > Manage Normalization > Create Job");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem2"), "");
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Navigate_Normalization_Jobs", SolQAConstants.CMDB);
        
        String normalizationJobName = cmdbFeatureMethods.createNormalizationJob(cmdbTestData);
        log.info("Created Normalization Job with name: " + normalizationJobName);
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Normalization_Job_Created", SolQAConstants.CMDB);
        
        Assert.assertTrue(cmdbFeatureMethods.verifyJobCreated(normalizationJobName), 
            "Normalization Job should be created successfully");

        log.info("Step 5: Navigate to Job > Manage Reconciliation > Create Job");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem1"), "");
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Navigate_Reconciliation_Jobs", SolQAConstants.CMDB);
        
        String reconciliationJobName = cmdbFeatureMethods.createReconciliationJob(cmdbTestData);
        log.info("Created Reconciliation Job with name: " + reconciliationJobName);
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Reconciliation_Job_Created", SolQAConstants.CMDB);
        
        Assert.assertTrue(cmdbFeatureMethods.verifyJobCreated(reconciliationJobName), 
            "Reconciliation Job should be created successfully");

        log.info("Step 6: Navigate to Datasource > Manage Datasource > Add Dataset");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu2"), cmdbTestData.get("MenuItem3"), "");
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Navigate_Manage_Datasource", SolQAConstants.CMDB);
        
        String datasetName = cmdbFeatureMethods.createNewDataset(cmdbTestData);
        log.info("Created Dataset with name: " + datasetName);
        cmdbFeatureMethods.searchDataset(datasetName);
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Dataset_Created", SolQAConstants.CMDB);
        
        cmdbFeatureMethods.verifyDataPresent(datasetName);

        log.info("Step 7: Navigate to Datasource > Manage Datasource > Add Datasource");
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Navigate_Add_Datasource", SolQAConstants.CMDB);
        
        String datasourceName = cmdbFeatureMethods.createNewDatasource(cmdbTestData, datasetName);
        log.info("Created Datasource with name: " + datasourceName);
        cmdbFeatureMethods.captureScreenshot("DRSQA_13335_Datasource_Created", SolQAConstants.CMDB);
        
        cmdbFeatureMethods.verifyDataPresent(datasourceName);

        log.info("***** End DRSQA_13335_Verify_the_CMDB_Functionality_Create_CI_Normalization_Job_Recon_Job_Dataset_and_Datasource *****");
    }

    @Test(description = "HelixGPT based Impact Summarization", enabled = false, groups = {"Regression_P2", "CMDB", "HelixGPT"}, dependsOnMethods = {"DRSQA_13298_Rerun_Impact_Analysis"})
    @SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-13324", Products = {Product.ITSM, Product.CMDB, Product.HelixGPT})
    public void DRSQA_13324_HelixGPT_Based_Impact_Summarization(ITestContext context) {

        log.info("**************** Start DRSQA-13324: HelixGPT based Impact Summarization ******************");

        log.info("Step 1: Login to Smart IT as pbunyon");
        context.setAttribute("DRSQA_13324_Driver", getDriver());
        context.setAttribute("DRSQA_13324_Product", Product.Helix_SmartIT);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13324_TestData");

        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appSmartIT);
        log.info("Logged in user: " + itsmUserData.get("UserName"));

        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        log.info("Smart IT login completed, proceeding to navigate to Impact Analysis Console");

        log.info("Step 2: Navigate to Console --> Impact Analysis Console");
        smartITFeatureMethods.navigateToOptionInAdvancedResponsiveNavigation(SolQAConstants.CONSOLE,SolQAConstants.Impact_Analysis_Console);
        log.info("Navigated to Impact Analysis Console successfully");
        
        log.info("Step 3: Check if 'Summarize using Helix GPT' option is available");
        smartITFeatureMethods.refreshBrowser();
        boolean isSummarizeOptionAvailable = smartITFeatureMethods.isSummarizeUsingHelixGPTOptionAvailable();
        
        if (!isSummarizeOptionAvailable) {
            log.info("'Summarize using Helix GPT' option is not available - Running 'Rerun Impact Analysis'");
            smartITFeatureMethods.navigateToRequiredOptionThrough3DotsMenu(SolQAConstants.Rerun_Impact_Analysis);
            log.info("Clicked on 'Rerun Impact Analysis' option");
            
            smartITFeatureMethods.refreshBrowser();
            log.info("Page refreshed after rerun");
            
            String impactAnalysisStatus = smartITFeatureMethods.getImpactAnalysisRunStatus();
            Assert.assertTrue(impactAnalysisStatus.contains("Successful") || impactAnalysisStatus.contains("Rerun"),
                "Impact Analysis Run should complete successfully");
            log.info("Impact Analysis Run completed with status: " + impactAnalysisStatus);
        } else {
            log.info("'Summarize using Helix GPT' option is already available");
        }

        log.info("Step 4: Click on 3 dots --> Summarize using Helix GPT option");
        smartITFeatureMethods.navigateToRequiredOptionThrough3DotsMenu(cmdbTestData.get("SummarizeOption"));
        log.info("Clicked on 'Summarize using Helix GPT' option");

        log.info("Step 5: Verify Impact CI summarize is displayed");
        boolean isSummarizeVisible = smartITFeatureMethods.verifyImpactCISummarizeVisible();
        Assert.assertTrue(isSummarizeVisible, "Impact CI summarize should be visible after HelixGPT summarization");
        log.info("✅ Impact CI summarize is visible - HelixGPT summarization successful");

        log.info("Step 6: Close and click on 3 dots --> View Summary option");
        smartITFeatureMethods.refreshBrowser();
        smartITFeatureMethods.navigateToRequiredOptionThrough3DotsMenu(cmdbTestData.get("ViewSummaryOption"));
        log.info("Clicked on 'View Summary' option");

        log.info("Step 7: Verify summary is displayed after clicking View Summary");
        boolean isSummaryDisplayedAgain = smartITFeatureMethods.verifyImpactCISummarizeVisible();
        Assert.assertTrue(isSummaryDisplayedAgain, "Summary should be displayed when 'View Summary' is clicked");
        log.info("✅ Summary is displayed successfully - 'View Summary' option working as expected");

        log.info("**************** End DRSQA-13324: HelixGPT based Impact Summarization ******************");
    }

    @Test(description = "Rule based configuration for automatic impact runs",enabled = false,groups = {"Regression_P2", "CMDB", "ITSM","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-13326", Products = {Product.ITSM, Product.Innovation_Suite, Product.CMDB})
    public void DRSQA_13326_Rule_based_configuration_for_automatic_impact_runs(ITestContext context) throws InterruptedException {

        log.info("**************** Start DRSQA-13326: Rule based configuration for automatic impact runs ******************");
        try {
            log.info("(Assuming this is already done via pipeline) Precondition Step1:  Log in to Innovation Studio, open the shared Service Library → Application Configuration data editor, search for ENABLE_NETWORK_IMPACT_ANALYSIS with Application ID com.bmc.dsm.network-impact-analysis, set Value = true, and ensure BULK_RELATE_ACTION_CI_COUNT = 100.");
            log.info("ADE should be integrated to execute the impact analysis.");
            log.info("Precondition Step2: CCS configuration under com.bmc.arsys.itsm. > Check the following values: > EnableNetOps=true > Change_Impact_Analyzer_Option=both");
            context.setAttribute("DRSQA_13326_Driver", itsmDriver);
            context.setAttribute("DRSQA_13326_Product", Product.Helix_ITSM);
    
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
            itsmFeatureMethods.closeBrowser();
    
            log.info(": Enable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
             isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
              setDriver(isDriver);
             context.setAttribute("DRSQA_13326_Driver", isDriver);
             context.setAttribute("DRSQA_13326_Product", Product.Helix_SmartIT);
             login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
             innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
             innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
             innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.ITSM_APPLICATION_BUNDLE);
             innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
             innovationSuiteFeatureMethodsPage.clickOnSettings();
             innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
             innovationSuiteFeatureMethodsPage.enableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
             isDriver.quit();
    
            log.info("----------- Precondition Step4: .CMDB-DSM Sync Ensure Atrium Core → Configure Manager Dashboard → Configuration → SmartGraph Sync is scheduled with the Enable Schedule toggle ON. Set it to once and save it.    ----------");
            log.info("----Login to ITSM application ----");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_13326_Driver", itsmDriver);
            context.setAttribute("DRSQA_13326_Product", Product.Helix_ITSM);
    
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
            cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13326_TestData");
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
            cmdbFeatureMethods.closeBrowser();

            log.info("------Precondition :- Login to shared ticket console  --> settings --> Network Impact Analysis --> Automated Rule for Incident ");
            smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
            setDriver(smartITdriver);
            context.setAttribute("DRSQA_13326_Driver", isDriver);
            context.setAttribute("DRSQA_13326_Product", Product.Helix_SmartIT);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
            cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13326_TestData");
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            smartITFeatureMethods = new SmartITFeatureMethodsPage();
            smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
            innovationSuiteFeatureMethodsPage.clickOnSettings();
            innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.ADVANCED_IMPACT_ANALYSIS);
            innovationSuiteFeatureMethodsPage.selectConfiguration(SolQAConstants.AUTOMATED_RULE_FOR_INCIDENT);
    
        log.info("-------Precondition step 2, 3 and 4:- Click on + Rule ----------------");
        smartITFeatureMethods.addNewRuleForIncident(cmdbTestData.get("RuleName"), cmdbTestData.get("CompanyName"), cmdbTestData.get("TriggerEvent1"), cmdbTestData.get("TriggerEvent2"), cmdbTestData.get("Qualification"), cmdbTestData.get("Incident Type"));
        log.info("Rule added successfully: " + cmdbTestData.get("RuleName"));
        log.info("Precondition step completed successfully");
        smartITFeatureMethods.closeBrowser();

        log.info("---------- Step 1:- Login to smartIT and click on Create Incident ----------------");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_13326_Driver", smartITdriver);
        context.setAttribute("DRSQA_13326_Product", Product.Helix_SmartIT);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_13326_TestData");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        log.info("Smart IT page launched successfully");
        smartITFeatureMethods = new SmartITFeatureMethodsPage();

        log.info("**** Navigating to Create New Incident ********");
        smartITFeatureMethods.navigateToOptionInAdvancedResponsiveNavigation(SolQAConstants.CREATE, SolQAConstants.INCIDENT);
        smartITFeatureMethods.FillIncidentInImpactAnalysisPage(cmdbTestData);
        smartITFeatureMethods.saveIncidentInImpactAnalysisPage();
        
        String incidentID = smartITFeatureMethods.FetchIncidentTicketIDinImpactAnalysisPage();
        log.pass("************ created incident is: " + incidentID);

        log.info("Relate a CI to the incident");
        smartITFeatureMethods.relateCIToIncidentinImpactAnalysisPage(cmdbTestData.get("CIValue"), cmdbTestData.get("RelationshipType"));
        log.info("CI related to the incident successfully");

        log.info("------------Step 2:- After few seconds , check under Activity -----------------");
        smartITFeatureMethods.verifyActivityNotesinImpactAnalysisPage(cmdbTestData.get("ActivityFirstNoteText"));
        log.info("Activity First Note Text is as expected");

        log.info("------------Step 3:- Click on the impact analysis Run  -----------------");
        smartITFeatureMethods.navigateToOptionInAdvancedResponsiveNavigation(SolQAConstants.CONSOLE, SolQAConstants.IMPACT_ANALYSIS_CONSOLE);
        smartITFeatureMethods.navigateToRequiredOptionThrough3DotsMenu(SolQAConstants.IMPACT_ANALYSIS_RUN);
        smartITFeatureMethods.runImpactAnalysis(cmdbTestData.get("Description"), cmdbTestData.get("TicketType"), cmdbTestData.get("TicketValue"));

        log.info("------------Step 4:- On Impact analysis Run ,Under description  it should display the Run name  -----------------");
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.getImpactAnalysisDescription().contains(cmdbTestData.get("Description")), "Impact Analysis Description is not contains the description");
        smartITFeatureMethods.closeBrowser();
        }
        finally{    
            log.info(": Disable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
            isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_13326_Driver", isDriver);   
            context.setAttribute("DRSQA_13326_Product", Product.Helix_SmartIT);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
            innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.ITSM_APPLICATION_BUNDLE);
            innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
            innovationSuiteFeatureMethodsPage.clickOnSettings();
            innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
            innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
            isDriver.quit();


            log.info(" Disable the shared ticket console settings");
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
       
        log.info("**************** End DRSQA-13326: Rule based configuration for automatic impact runs ******************");
    }

    @Test(description = "Verify DSM Impact Analysis for ITSM incident",enabled = false,groups = {"Regression_P2", "CMDB", "ITSM","gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-16203", Products = {Product.ITSM, Product.CMDB})
    public void DRSQA_16203_Verify_DSM_Impact_Analysis_for_ITSM_Incident(ITestContext context) throws InterruptedException {

        log.info("**************** Start DRSQA-16203: Verify DSM Impact Analysis for ITSM incident ******************");
        try {
        log.info("(Assuming this is already done via pipeline) Precondition Step1: Log in to Innovation Studio, open the shared Service Library → Application Configuration data editor, search for ENABLE_NETWORK_IMPACT_ANALYSIS with Application ID com.bmc.dsm.network-impact-analysis, set Value = true, and ensure BULK_RELATE_ACTION_CI_COUNT = 100.");
        log.info("ADE should be integrated to execute the impact analysis.");
        log.info("Precondition Step2: CCS configuration under com.bmc.arsys.itsm. > Check the following values: > EnableNetOps=true > Change_Impact_Analyzer_Option=both");
        context.setAttribute("DRSQA_16203_Driver", itsmDriver);
        context.setAttribute("DRSQA_16203_Product", Product.Helix_ITSM);

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();

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
        itsmFeatureMethods.closeBrowser();

        log.info(": Enable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
        isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_16203_Driver", isDriver);
        context.setAttribute("DRSQA_16203_Product", Product.Helix_SmartIT);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.ITSM_APPLICATION_BUNDLE);
        innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
        innovationSuiteFeatureMethodsPage.clickOnSettings();
        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
        innovationSuiteFeatureMethodsPage.enableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
        isDriver.quit();

        log.info("----------- Precondition Step4: .CMDB-DSM Sync Ensure Atrium Core → Configure Manager Dashboard → Configuration → SmartGraph Sync is scheduled with the Enable Schedule toggle ON. Set it to once and save it.    ----------");
        log.info("----Login to ITSM application ----");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_16203_Driver", itsmDriver);
        context.setAttribute("DRSQA_16203_Product", Product.Helix_ITSM);

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
        cmdbFeatureMethods.closeBrowser();

        log.info("Step 1: Login to Smart IT with pbunyon user");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        context.setAttribute("DRSQA_16203_Driver", smartITdriver);
        context.setAttribute("DRSQA_16203_Product", Product.Helix_SmartIT);

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_16203_TestData");

        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifyImpactAnalysisConsoleLaunchedSuccessfully();
        log.info("Smart IT page launched successfully");

        log.info("Step 2: Navigate to More -> Create New -> Incident and create an incident");
        smartITFeatureMethods.navigateToOptionInAdvancedResponsiveNavigation(SolQAConstants.CREATE, SolQAConstants.INCIDENT);
        smartITFeatureMethods.FillIncidentInImpactAnalysisPage(cmdbTestData);
        smartITFeatureMethods.saveIncidentInImpactAnalysisPage();
        String incidentID = smartITFeatureMethods.FetchIncidentTicketIDinImpactAnalysisPage();
        log.pass("Incident created successfully with ID: " + incidentID);

        log.info("Step 3: Click on configuration item and add 4 to 5 CIs to the incident");
        smartITFeatureMethods.relateCIToIncidentinImpactAnalysisPage(cmdbTestData.get("CIValue1"), cmdbTestData.get("RelationshipType"));
        smartITFeatureMethods.relateCIToIncidentinImpactAnalysisPage(cmdbTestData.get("CIValue2"), cmdbTestData.get("RelationshipType"));
        smartITFeatureMethods.relateCIToIncidentinImpactAnalysisPage(cmdbTestData.get("CIValue3"), cmdbTestData.get("RelationshipType"));
        smartITFeatureMethods.relateCIToIncidentinImpactAnalysisPage(cmdbTestData.get("CIValue4"), cmdbTestData.get("RelationshipType"));
        
        log.info("Step 4: Navigate to Impact Analysis Console from More menu");
        smartITFeatureMethods.navigateToRequiredOptionFromMoreMenu(SolQAConstants.ADVANCED_IMPACT_ANALYSIS_CONSOLE);

        log.info("Step 5: Click on + Impact analysis run -> Use Associated CI and run analysis");
        smartITFeatureMethods.switchToLastWindow();
        smartITFeatureMethods.navigateToRequiredImpactAnalysisRunOption(SolQAConstants.USE_ASSOCIATED_CI);

        log.info("Step 6: Verify the impact analysis run status for 'Use associated CI'");
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.getImpactAnalysisRunStatus().contains(SolQAConstants.SUCCESSFUL),"Impact Analysis Run Status for 'Use associated CI' is not Successful. Got: " + smartITFeatureMethods.getImpactAnalysisRunStatus() + " but Expected: " + SolQAConstants.SUCCESSFUL);

        log.info("Step 7: Click on + Impact analysis run -> Select input CI and run analysis");
        smartITFeatureMethods.navigateToRequiredImpactAnalysisRunOption(SolQAConstants.SELECT_INPUT_CI);
        smartITFeatureMethods.clickOnSelectAllCheckboxInSelectInputCIs();
        smartITFeatureMethods.clickOnAddSelectedButton();
        smartITFeatureMethods.clickOnSaveButton();
        smartITFeatureMethods.clickOnRunAnalysisButton();

        log.info("Step 8: Verify the impact analysis run status for 'Select input CI'");
        smartITFeatureMethods.refreshBrowser();
        Assert.assertTrue(smartITFeatureMethods.getImpactAnalysisRunStatus().contains(SolQAConstants.SUCCESSFUL),"Impact Analysis Run Status for 'Select input CI' is not Successful. Got: " + smartITFeatureMethods.getImpactAnalysisRunStatus() + " but Expected: " + SolQAConstants.SUCCESSFUL);

        }

        finally {
            log.info(": Disable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
            isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
            setDriver(isDriver);
            context.setAttribute("DRSQA_16203_Driver", isDriver);
            context.setAttribute("DRSQA_16203_Product", Product.Helix_SmartIT);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
            innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.ITSM_APPLICATION_BUNDLE);
            innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
            innovationSuiteFeatureMethodsPage.clickOnSettings();
            innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
            innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
            isDriver.quit();

            log.info("Step 9: Disable the shared ticket console settings");
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_16203_Driver", itsmDriver);
            context.setAttribute("DRSQA_16203_Product", Product.Helix_ITSM);
    
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

        log.info("**************** End DRSQA-16203: Verify DSM Impact Analysis for ITSM incident ******************");
    }

    @Test(description = "CMDB:Create Normalization Job and Execute to correct PCT", groups = {"Regression_P2", "CMDB", "ITSM"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11742", Products = {Product.ITSM, Product.CMDB})
    public void DRSQA_11742_Create_Normalization_Job_and_Execute_to_correct_PCT(ITestContext context) throws Exception {

        context.setAttribute("DRSQA-11742_Driver", cmdbdriver);
        context.setAttribute("DRSQA-11742_Product", Product.Helix_ITSM);

        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "DRSQA_11742_TestData");

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();

        log.info("**** Login to ITSM and cross launch CMDB from Atrium Core ****");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();

        log.info("**** Create dataset and datasource in CMDB ****");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu1"), cmdbTestData.get("MenuItem1"), "");
        String datasetName = cmdbTestData.get("datasetName") + "_" + UUID.randomUUID().toString().substring(0, 6);
        String datasetId = cmdbTestData.get("datasetID") + "_" + UUID.randomUUID().toString().substring(0, 6);
        cmdbFeatureMethods.addDatasetAndSave(datasetName, datasetId, cmdbTestData.get("permission1"), cmdbTestData.get("permission2"));
        cmdbFeatureMethods.searchDataset(datasetId);
        cmdbFeatureMethods.verifyDatasetPresent(datasetName);

        String datasourceName = cmdbFeatureMethods.createNewDatasource(cmdbTestData, datasetId);
        cmdbFeatureMethods.verifyDataSourceNamePresent(datasourceName);

        log.info("**** Edit dataset configurations (Normalization Type) ****");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu4"), cmdbTestData.get("MenuItem4"), cmdbTestData.get("SubMenuItem4"));
        cmdbFeatureMethods.setDatasetConfiguration(datasetId, cmdbTestData);

        log.info("**** Create catalog mapping rule for normalization ****");
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu2"), cmdbTestData.get("MenuItem2"), cmdbTestData.get("SubMenuItem2"));
        String discoveredProductName = cmdbTestData.get("discoveryProductNameValue") + "_" + UUID.randomUUID().toString().substring(0, 6);
        cmdbFeatureMethods.createCatalogMapping(cmdbTestData, discoveredProductName);

        log.info("**** Create CI in ITSM (BMC.CORE:BMC_ComputerSystem) using the new dataset ****");
        cmdbFeatureMethods.switchToTabByIndex(0);
        itsmFeatureMethods.refreshBrowser();
        itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_COMPUTERSYSTEM, SolQAConstants.SEARCH, configProp.get("ARServerAlias"));
        String ciName = cmdbTestData.get("CIName") + "_" + UUID.randomUUID().toString().substring(0, 6);
        Map<Object, String> ciData = new HashMap<>(cmdbTestData);
        ciData.put("DatasetID", datasetId);
        ciData.put("CIName", ciName);
        ciData.put("ProductNameValue", discoveredProductName);
        cmdbFeatureMethods.createCIInComputerSystemForm(ciData);

        log.info("**** Create and run normalization job for the new dataset ****");
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.refreshBrowser();
        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu3"), cmdbTestData.get("MenuItem3"), "");

        Map<Object, String> jobData = new HashMap<>(cmdbTestData);
        jobData.put("dataSetValue", datasetId);
        String normalizationJobName = cmdbFeatureMethods.createNormalizationJob(jobData);
        Assert.assertTrue(cmdbFeatureMethods.verifyJobCreated(normalizationJobName), "Normalization Job should be created successfully");
        cmdbFeatureMethods.runNormalizationJob(normalizationJobName, cmdbTestData.get("normalizeType"));

        log.info("**** Verify normalized PCT values in ITSM ****");
        cmdbFeatureMethods.switchToTabByIndex(0);
        itsmFeatureMethods.refreshBrowser();
        itsmFeatureMethods.LoadForm(SolQAConstants.BMC_CORE_BMC_COMPUTERSYSTEM, SolQAConstants.SEARCH, configProp.get("ARServerAlias"));
        itsmFeatureMethods.searchCI(datasetId, ciName);

        Assert.assertEquals(cmdbFeatureMethods.getMidTierTextFieldValue(cmdbTestData.get("TypeLabel")), cmdbTestData.get("mappedTypeValue"), "Type is not normalized");
        Assert.assertEquals(cmdbFeatureMethods.getMidTierTextFieldValue(cmdbTestData.get("CategoryLabel")), cmdbTestData.get("mappedCategoryValue"), "Category is not normalized");
        Assert.assertEquals(cmdbFeatureMethods.getMidTierTextFieldValue(cmdbTestData.get("ItemLabel")), cmdbTestData.get("mappedItemValue"), "Item is not normalized");
        Assert.assertEquals(cmdbFeatureMethods.getMidTierTextFieldValue(cmdbTestData.get("ModelLabel")), cmdbTestData.get("mappedProductNameValue"), "Model/Product Name is not normalized");
        Assert.assertEquals(cmdbFeatureMethods.getMidTierTextFieldValue(cmdbTestData.get("ManufacturerLabel")), cmdbTestData.get("mappedManufacturerValue"), "Manufacturer Name is not normalized");
        itsmFeatureMethods.closeBrowser();
    }
    
    //Previous owner aramulwa
    @Test(description = "CMDB:Create Dataset and Map to Datastore", groups = {"Regression_P2", "CMDB", "ITSM", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11729", Products = {Product.ITSM, Product.CMDB})
    public void TTID_20341_Create_Dataset_and_Map_to_Datastore(ITestContext context) throws Exception {

        context.setAttribute("TTID_20341_Driver", getDriver());
        context.setAttribute("TTID_20341_Product", Product.Helix_ITSM);

        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "TTID_20341_TestData");

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();

        log.info("**** Login to ITSM and cross launch CMDB from Atrium Core ****");
        itsmFeatureMethods.navigateMenu(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();
        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();

        cmdbFeatureMethods.navigateToMenu(cmdbTestData.get("Menu"), cmdbTestData.get("MenuItem"), "");

        log.info("**** Create dataset ****");
        String datasetName = cmdbTestData.get("datasetName") + "_" + UUID.randomUUID().toString().substring(0, 6);
        String datasetId = cmdbTestData.get("datasetID") + "_" + UUID.randomUUID().toString().substring(0, 6);
        cmdbFeatureMethods.addDatasetAndSave(datasetName, datasetId, cmdbTestData.get("permission1"), cmdbTestData.get("permission2"));
        log.info("Dataset created: " + datasetName);

        log.info("**** Map dataset by adding datasource (datastore) ****");
        String datasourceName = cmdbFeatureMethods.createNewDatasource(cmdbTestData, datasetId);
        log.info("Datasource created and mapped to dataset: " + datasourceName);

        log.info("**** Verify dataset exists after mapping ****");
        cmdbFeatureMethods.searchDataset(datasetId);
        cmdbFeatureMethods.verifyDataSourcePresent(datasetName);
    }

    //Previous owner aramulwa
    @Test(description = "CMDB:Edit Instance from Dashboard in midtier, set MAD true.", groups = {"Regression_P2", "CMDB", "ITSM", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", TTID = "20340", JiraID = "DRSQA-11728", Products = {Product.CMDB})
    public void TTID_20340_Edit_Instance_from_Dashboard_in_midtier_set_MAD_true(ITestContext context) {

        context.setAttribute("TTID_20340_Driver", getDriver());
        context.setAttribute("TTID_20340_Product", Product.Helix_ITSM);

        cmdbTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appCMDB, "CMDBTestData", "TTID_20340_TestData");

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();

        log.info("**** Login to ITSM and cross launch CMDB from Atrium Core ****");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.ATRIUM_CORE, SolQAConstants.CONFIGURATION_MANAGER_DASHBOARD);
        cmdbFeatureMethods = new CMDBFeatureMethods();

        cmdbFeatureMethods.switchToCMDBTab(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        cmdbFeatureMethods.verifyLoginTo_CMDB();

        log.info("**** Create CI from CMDB *****");
        String ciName = cmdbFeatureMethods.createCI(cmdbTestData);

        log.info("**** Enable Edit Mode *****");
        cmdbFeatureMethods.enableEditMode();

        log.info("**** Right click on CI and Edit Instance (cross launch to Mid-tier) *****");
        cmdbFeatureMethods.rightClickOnCINode();
        cmdbFeatureMethods.clickOnOptionFromRightClickOfCI(cmdbTestData.get("editOption"));

        
        cmdbFeatureMethods.switchToTabByTitle(SolQAConstants.BMC_CORE_BMC_Application);
        log.info("**** Update CI details from Mid-tier *****");
        cmdbFeatureMethods.setMidTierFieldValueById(cmdbTestData.get("updateFieldId1"), cmdbTestData.get("updateFieldValue1"));
        cmdbFeatureMethods.setMidTierFieldValueById(cmdbTestData.get("updateFieldId2"), cmdbTestData.get("updateFieldValue2"));
        cmdbFeatureMethods.setMidTierFieldValueById(cmdbTestData.get("updateFieldId3"), cmdbTestData.get("updateFieldValue3"));
        cmdbFeatureMethods.setMidTierFieldValueById(cmdbTestData.get("updateFieldId4"), cmdbTestData.get("updateFieldValue4"));
        cmdbFeatureMethods.clickMidTierButtonByText(cmdbTestData.get("midTierSaveButtonText"));

        cmdbFeatureMethods.switchToTabByIndex(1);
        log.info("**** Right click on CI and Mark As Delete in CMDB *****");
        cmdbFeatureMethods.rightClickOnCINode();
        cmdbFeatureMethods.clickOnOptionFromRightClickOfCI(cmdbTestData.get("deleteOption"));

        log.info("**** Verify the CI name is blurred *****");
        cmdbFeatureMethods.verifyMarkAsDelete();

        cmdbFeatureMethods.switchToTabByTitle(SolQAConstants.BMC_CORE_BMC_Application);
        cmdbFeatureMethods.clickMidTierButtonByText(cmdbTestData.get("midTierRefreshButtonText"));

        log.info("**** Verify Mark As delete value = Yes in Mid-tier *****");
        String mad = cmdbFeatureMethods.getMidTierFieldValueById(cmdbTestData.get("markAsDeleteFieldId"));
        if (mad == null || mad.trim().isEmpty()) {
            mad = cmdbFeatureMethods.getMidTierTextFieldValue(cmdbTestData.get("markAsDeleteFieldLabel"));
        }
        Assert.assertEquals(mad, cmdbTestData.get("markAsDeleteExpectedValue"),
                "Mark As Delete value is different on Mid-tier and CMDB Dashboard for CI: " + ciName);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (innovationSuiteFeatureMethodsPage != null) {
            innovationSuiteFeatureMethodsPage.closeBrowser();
        }
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (cmdbFeatureMethods != null) {
            cmdbFeatureMethods.closeBrowser();
        }
        if(smartITFeatureMethods != null) {
			smartITFeatureMethods.closeBrowser();
        }
        
    }
}
