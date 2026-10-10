package ITSM.DWP;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.dwp.DWPAdministrationPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import pages.ITSM.SSPLiveChat.SspLiveChatFeatureMethods;
import util.*;

public class DWP_HKM extends Launcher {

	static SolqaLogger log = new SolqaLogger(DWP_HKM.class.getName());
	ReadJson readJson = new ReadJson();
	Map<Object, String> isTestData, dwpUserData, dwpTestData, dwpAdminUserData;
	InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	DWPFeatureMethods dwpFeatureMethods;
	ITSMFeatureMethods itsmFeatureMethods;
	SspLiveChatFeatureMethods sspLiveChatFeatureMethods;
	WebDriver dwpDriver, isDriver;
    UserUpdater dwpUsers;
    int counter;
    String appName="DWP";
	    
    @BeforeTest(alwaysRun=true)
    public void setupUsers() {
        counter = Integer.parseInt(configProp.get("runCounter"));
        if (counter > 0) {
            // Load the JSON file once
            String filename = "DWPUsers";
            dwpUsers = new UserUpdater();
            String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + appName + File.separator + filename + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + appName + File.separator + filename + ".json";

            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);

            // Load the JSON file
            dwpUsers.loadJsonFile(jsonFilePath);

            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            dwpUsers.updateFromExcelRow(excelFilePath, counter);
            log.info("Updated DWPUsers.json for run counter: " + counter + " using row: " + counter);
            // Verify the update by reading the JSON back
        } else {
            //No change required
            log.info("runCounter is 0, no user updates required");
        }
    }
	    
    //previous owner: shudeshp
	@Test(description = "[HKM] Searching HKM KA in DWP", groups = {"MAT", "DWP", "HKM"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11815", Products = {Product.Helix_DWP, Product.Helix_Platform, Product.ComAround_Integration})
	public void DRSQA_11815_SearchAndVerifyComaroundArticleInDWP(ITestContext context) throws IOException {

		log.info("*********** DRSQA-11815 : [HKM] Searching HKM KA in DWP *******************");
		
		isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		context.setAttribute("DRSQA_11815_Driver", isDriver);
		context.setAttribute("DRSQA_11815_Product", Product.Helix_InnovationStudio);
		log.info("Step 1: Login to IS with "+CommonMethods.readConfig("adminUserName"));
		isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS,"ISTestData","ComAroundDetails");
		login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"),SolQAConstants.appIS);
    innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		String knowledgeManagement = isTestData.get("knowledgeManagement");
		log.info("Step 2: Navigate to Administration > Knowledge Management > Knowledge Configuration and set HKM configuration");
		innovationSuiteFeatureMethodsPage.navigateToAdminPage();
		innovationSuiteFeatureMethodsPage.navigateToConfiguration(knowledgeManagement);
		innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);
		log.info("Step 3: Navigate to Administration > Knowledge Management > Knowledge access mapping and set mapping for "+SolQAConstants.PETRAMCO_COMPANY);
        commonWaits.waitForExecutionComplete();
		innovationSuiteFeatureMethodsPage.setAccessMappingComAround(SolQAConstants.BMCOPSMONITORING_COMPANY);
    innovationSuiteFeatureMethodsPage.closeBrowser();

		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
    context.setAttribute("DRSQA_11815_Driver", dwpDriver);
    context.setAttribute("DRSQA_11815_Product", Product.Helix_DWP);
		login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
    dwpFeatureMethods = new DWPFeatureMethods();
    dwpFeatureMethods.verifyDWPHomePageOpened();
		log.info("Configuring HKM Configuration in DWP.");
		dwpFeatureMethods.navigateToAdminPage();
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11815_Data");
		String configuration = dwpTestData.get("configuration");
		log.info("Step 4: Login to DWP with " + CommonMethods.readConfig("adminUserName"));
		dwpFeatureMethods.verifyLoginToDWPAdmin();
		log.info("Step 5: login to DWP Admin console and set the comaround provider configurations and enable the provider ");
		dwpFeatureMethods.clickOnHamburgerMenu();
		dwpFeatureMethods.clickOnMenu(configuration);
		String serverUrl = getBaseURL(SolQAConstants.appIS);
		String serverUser = CommonMethods.readConfig("adminUserName");
		String serverPassword = CommonMethods.readConfig("adminPassword");
		log.info("------set value to url: " + serverUrl + " -----user: " + serverUser + "------ password: " + serverPassword);
		String providerName = dwpFeatureMethods.enableDisableComAroundPluggableProvider(true);
		dwpFeatureMethods.openProviderSettings(providerName, 30);
		if(!dwpFeatureMethods.getTextFieldValuesInProviderSettings(dwpTestData.get("url")).equals(serverUrl)) {
			dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("url"), serverUrl);
		}
		dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("user"), serverUser);
		dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("password"), serverPassword);
		dwpFeatureMethods.updateSettingsAndReloadProviders();
		dwpFeatureMethods.captureScreenshot("DRSQA_11815_Comaround_Configuration", SolQAConstants.appDWP);
    dwpFeatureMethods.closeBrowser();

		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		context.setAttribute("DRSQA_11815_Driver", dwpDriver);
		context.setAttribute("DRSQA_11815_Product", Product.Helix_DWP);
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
		login(dwpUserData .get("UserName"), dwpUserData .get("Password"), SolQAConstants.appDWP);
    dwpFeatureMethods = new DWPFeatureMethods();
		log.info("Step 6: Login to DWP with " +dwpUserData.get("UserName"));
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11815_Data");
		String comaroundArticleInDWP = dwpTestData.get("comaroundArticle");
		log.info("Comaround Article name to search in DWP is : " + comaroundArticleInDWP);
		dwpFeatureMethods.openActivityPage();
		log.info("Step 7: Global search " +comaroundArticleInDWP+ " and click on articles tab");
		dwpFeatureMethods.searchItemInGlobalSearch(comaroundArticleInDWP, SolQAConstants.ARTICLES);
		dwpFeatureMethods.captureScreenshot("DRSQA_11815_Search_ComaroundArticle_FromGlobalSearch", SolQAConstants.appDWP);
		log.info("Open Comaround Article link and verify title");
		Assert.assertTrue(getDriver().getCurrentUrl().contains(dwpTestData.get("urlContains")), "Url is not pointing to comaround article");
		dwpFeatureMethods.captureScreenshot("DRSQA_11815_OpenComaroundArticleLinkAndVerifyTitle", SolQAConstants.appDWP);
		dwpFeatureMethods.clickOnKnowledgeArticleUsefulLink();
		log.info("*" +getDriver().getCurrentUrl());
		Assert.assertTrue(getDriver().getCurrentUrl().contains("comaround"),"URL not contains any provider value");
    dwpFeatureMethods.closeBrowser();
		log.info("------------END DRSQA-11815 - [HKM] Searching HKM KA in DWP --------");
	}

	@Test(description = "[HKM] Accessing ComAround article from quick links on self service portal", groups = {"Regression_P1"})
	@SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11618", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.ComAround_Integration})
	public void DRSQA_11618_Accessing_ComAround_Article(ITestContext context) {

		log.info("*********** DRSQA-11618 : [HKM] Accessing ComAround article from SSP Quick Links *******************");

		isDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(isDriver);
		context.setAttribute("DRSQA_11618_ITSM_Driver", getDriver());
		context.setAttribute("DRSQA_11618_ITSM_Product", Product.Helix_ITSM);

		itsmFeatureMethods = new ITSMFeatureMethods();
		login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
		itsmFeatureMethods.verifyITSMLogin();

		itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.Live_Chat, SolQAConstants.ADMINISTRATION_CONSOLE);
		itsmFeatureMethods.enableSSPQuickLinksForComAround();
		itsmFeatureMethods.closeBrowser();

		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		context.setAttribute("DRSQA_11618_DWP_Driver", getDriver());
		context.setAttribute("DRSQA_11618_DWP_Product", Product.Helix_DWP);

		dwpFeatureMethods = new DWPFeatureMethods();
		sspLiveChatFeatureMethods = new SspLiveChatFeatureMethods();

		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
		dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
		dwpFeatureMethods.openActivityPage();

		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11618_Data");
		String linkText = dwpTestData.get("quickLinkText");
		String urlContains = dwpTestData.get("urlContains");

		log.info("Opening SSP console and verifying Quick Link cross-launch: " + linkText);
		sspLiveChatFeatureMethods.openLiveChatPage();
		sspLiveChatFeatureMethods.openQuickLinksTab();
		sspLiveChatFeatureMethods.openQuickLinkAndVerifyUrlContainsAndClose(linkText, urlContains);

		dwpFeatureMethods.closeBrowser();
		log.info("------------END DRSQA-11618 - [HKM] Accessing ComAround article from SSP Quick Links --------");
  }
  
    @Test(description = "[DWP] Verify DWP Comaround Integration by searching Comaround Articles", groups = {"Regression_P1", "DWP", "HKM","OneTime"},enabled = false)
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11604", Products = {Product.Helix_DWP, Product.Helix_Knowledge_Management, Product.ComAround_Integration})
    public void DRSQA_11604_Verify_DWP_Comaround_Integration_by_searching_Comaround_Articles(ITestContext context) throws IOException {

      log.info("*********** DRSQA-11604 : [DWP] Verify DWP Comaround Integration by searching Comaround Articles *******************");
      log.info("Step 1: DRSQA-11604_Pre-requisite Steps: START *********");
      this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
      setDriver(isDriver);
      context.setAttribute("DRSQA_11604_Driver", getDriver());
      context.setAttribute("DRSQA_11604_Product", Product.Helix_Knowledge_Management);
      innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();

      log.info("Step 1: Login to IS with " + CommonMethods.readConfig("adminUserName"));
      isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "ComAroundDetails");
      login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appIS);

      String knowledgeManagement = isTestData.get("knowledgeManagement");
      log.info("Step 2: Navigate to Administration > Knowledge Management > Knowledge Configuration and set HKM configuration");
      innovationSuiteFeatureMethodsPage.navigateToAdminPage();
      innovationSuiteFeatureMethodsPage.navigateToConfiguration(knowledgeManagement);
      innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);

      log.info("Step 3: Navigate to Administration > Knowledge Management > Knowledge access mapping and set mapping for " + SolQAConstants.PETRAMCO_COMPANY);
      innovationSuiteFeatureMethodsPage.setAccessMappingComAround(SolQAConstants.BMCOPSMONITORING_COMPANY);
      isDriver.close();

      this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      dwpFeatureMethods = new DWPFeatureMethods();
      login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);

      log.info("Configuring HKM Configuration in DWP.");
      dwpFeatureMethods.navigateToAdminPage();
      dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11815_Data");
      String configuration = dwpTestData.get("configuration");

      log.info("Step 4: Login to DWP with " + CommonMethods.readConfig("adminUserName"));
      dwpFeatureMethods.verifyLoginToDWPAdmin();

      log.info("Step 5: Login to DWP as an end user. Type an article which is available in Comaround. For Install Flash Player");
      dwpFeatureMethods.clickOnHamburgerMenu();
      dwpFeatureMethods.clickOnMenu(configuration);
      String serverUrl = getBaseURL(SolQAConstants.appIS);
      String serverUser = CommonMethods.readConfig("adminUserName");
      String serverPassword = CommonMethods.readConfig("adminPassword");

      log.info("------set value to url: " + serverUrl + " -----user: " + serverUser + "------ password: " + serverPassword);
      String providerName = dwpFeatureMethods.enableDisableComAroundPluggableProvider(true);
      dwpFeatureMethods.openProviderSettings(providerName, 20);
      if (!dwpFeatureMethods.getTextFieldValuesInProviderSettings(dwpTestData.get("url")).equals(serverUrl)) {
          dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("url"), serverUrl);
      }
      dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("user"), serverUser);
      dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("password"), serverPassword);
      dwpFeatureMethods.updateSettingsAndReloadProviders();
      dwpFeatureMethods.captureScreenshot("DRSQA_11815_Comaround_Configuration", SolQAConstants.appDWP);
      dwpDriver.close();

      this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      dwpFeatureMethods = new DWPFeatureMethods();
      context.setAttribute("DRSQA_11815_WebDriver", getDriver());
      context.setAttribute("DRSQA_11815_Product", Product.Helix_DWP);
      dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
      login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

      log.info("Step 6: Login to DWP with " + dwpUserData.get("UserName"));
      dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11815_Data");
      String comaroundArticleInDWP = dwpTestData.get("comaroundArticle");
      log.info("Comaround Article name to search in DWP is : " + comaroundArticleInDWP);
      dwpFeatureMethods.openActivityPage();

      log.info("Step 7: Global search " + comaroundArticleInDWP + " and click on articles tab and Check no duplicate article are visible");
      dwpFeatureMethods.VerifyOnlyOneArticleVisibleAfterSearching(comaroundArticleInDWP, SolQAConstants.ARTICLES);
      dwpFeatureMethods.captureScreenshot("DRSQA_11815_Search_ComaroundArticle_FromGlobalSearch", SolQAConstants.appDWP);
      dwpFeatureMethods.openExactMatchingArticle(comaroundArticleInDWP);
      log.info("step 8 : Get counter value before clicking Yes button");
      int counterBefore = dwpFeatureMethods.getHelpfulCounter();
      log.info("Counter before clicking Yes: " + counterBefore);

      log.info("step 9 : Click on Yes :Was this Resource helpful");
      dwpFeatureMethods.clickOnYesWasThisResourceHelpful();

      log.info("step 10 : Refresh page to see updated counter");
      dwpFeatureMethods.refreshBrowser();

      log.info("step 11 : Verify counter increased by 1");
      Assert.assertTrue(dwpFeatureMethods.verifyCounterIncreased(counterBefore), "Counter is not increased by 1");
      dwpFeatureMethods.captureScreenshot("DRSQA_11604_CounterIncreased", SolQAConstants.appDWP);
      log.info("------------END DRSQA-11604 - [DWP] Verify DWP Comaround Integration by searching Comaround Articles --------");
  }

    @Test(description = "DSI - Update the Search-bar & Search-result UX ; MVP (with/without Autocompletion)", groups = {"DWP", "HKM", "Regression_P2"})
    @SolqaDetails(authorName = "Preethi", JiraID = "DRSQA-11659", Products = {Product.Helix_DWP, Product.Helix_Platform, Product.ComAround_Integration})
    public void DRSQA11659_updateSearchbarAndSearchResultUX_MVP(ITestContext context) throws IOException {
      log.info("*************** START: DSI - Update the Search-bar & Search-result UX ; MVP (with/without Autocompletion) *************");

      // Umbrella reference includes Studio custom-page creation + Navigation Builder enablement to host Search Bar component.
      // SolutionsQA currently does not have stable/reusable DWP Studio/NAV builder automation wrappers; to preserve the UX intent,
      // this migration validates the same Search Bar / Search Results UX via DWP Global Search (Articles tab) while toggling providers.

      dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA11659_Details");
      dwpAdminUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("DWPAdminUserKey"));
      dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpTestData.get("DWPEndUserKey"));

      String configurationMenu = dwpTestData.get("configuration");
      String studioExposePagesSetting = dwpTestData.get("configSetting");
      String bwfProvider = dwpTestData.get("bwfProvider");
      String rkmProvider = dwpTestData.get("rkmProvider");
      String bwfArticle = dwpTestData.get("bwfArticle");
      String rkmArticle = dwpTestData.get("rkmArticle");
      String comAroundArticle = dwpTestData.get("comAroundArticle");
      String urlContains = dwpTestData.get("urlContains");

      log.info("-------- Precondition: login to DWP Admin console and Enable providers ---------");
      dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      context.setAttribute("DRSQA_11659_DWP_Admin_Driver", getDriver());
      context.setAttribute("DRSQA_11659_Product", Product.Helix_DWP);
      dwpFeatureMethods = new DWPFeatureMethods();

      login(dwpAdminUserData.get("UserName"), dwpAdminUserData.get("Password"), SolQAConstants.appDWP);
      dwpFeatureMethods.navigateToAdminPage();
      dwpFeatureMethods.verifyLoginToDWPAdmin();
      dwpFeatureMethods.enableFeatureSettingByNameInDWPAdmin(studioExposePagesSetting, true);

      dwpFeatureMethods.clickOnHamburgerMenu();
      dwpFeatureMethods.clickOnMenu(configurationMenu);

      // Enable BWF + RKM providers.
      DWPAdministrationPage dwpAdministrationPage = new DWPAdministrationPage();
      dwpAdministrationPage.selectProviderSetting(bwfProvider, true);
      dwpAdministrationPage.selectProviderSetting(rkmProvider, true);

      // Disable ComAround/HKM provider for first verification.
      dwpFeatureMethods.enableDisableComAroundPluggableProvider(false);
      dwpFeatureMethods.captureScreenshot("DRSQA_11659_ProvidersEnabled_ComaroundDisabled", SolQAConstants.appDWP);
      dwpDriver.close();

      log.info("------ Step: Login to DWP as End user and verify BWF/RKM articles are searchable ---------");
      dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      context.setAttribute("DRSQA_11659_DWP_EndUser_Driver", getDriver());
      dwpFeatureMethods = new DWPFeatureMethods();
      login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
      dwpFeatureMethods.openActivityPage();

      dwpFeatureMethods.searchItemInGlobalSearch(bwfArticle, SolQAConstants.ARTICLES);
      dwpFeatureMethods.verifyItemFound(bwfArticle, true);
      dwpFeatureMethods.openActivityPage();

      dwpFeatureMethods.searchItemInGlobalSearch(rkmArticle, SolQAConstants.ARTICLES);
      dwpFeatureMethods.verifyItemFound(rkmArticle, true);
      dwpDriver.close();

      log.info("-------------- Configure ComAround HKM Integration settings in Innovation Studio ---------");
      isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
      setDriver(isDriver);
      context.setAttribute("DRSQA_11659_IS_Driver", getDriver());
      context.setAttribute("DRSQA_11659_IS_Product", Product.Helix_InnovationStudio);
      innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();

      isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "ComAroundDetails");
      login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appIS);
      innovationSuiteFeatureMethodsPage.navigateToAdminPage();
      innovationSuiteFeatureMethodsPage.navigateToConfiguration(isTestData.get("knowledgeManagement"));
      innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);
      innovationSuiteFeatureMethodsPage.setAccessMappingComAround(SolQAConstants.PETRAMCO_COMPANY);
      isDriver.close();

      log.info("----- Step: Enable ComAround provider in DWP Admin and reload providers ------");
      dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      dwpFeatureMethods = new DWPFeatureMethods();
      login(dwpAdminUserData.get("UserName"), dwpAdminUserData.get("Password"), SolQAConstants.appDWP);
      dwpFeatureMethods.navigateToAdminPage();
      dwpFeatureMethods.verifyLoginToDWPAdmin();
      dwpFeatureMethods.clickOnHamburgerMenu();
      dwpFeatureMethods.clickOnMenu(configurationMenu);
      dwpFeatureMethods.enableDisableComAroundPluggableProvider(true);
      dwpFeatureMethods.updateSettingsAndReloadProviders();
      dwpDriver.close();

      log.info("----- Step: Login to DWP as end user and verify ComAround article is searchable -----");
      dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
      setDriver(dwpDriver);
      context.setAttribute("DRSQA_11659_DWP_EndUser_Driver_AfterComaround", getDriver());
      dwpFeatureMethods = new DWPFeatureMethods();
      login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
      dwpFeatureMethods.openActivityPage();
      dwpFeatureMethods.searchItemInGlobalSearch(comAroundArticle, SolQAConstants.ARTICLES);
      dwpFeatureMethods.verifyItemFound(comAroundArticle, true);
      Assert.assertTrue(getDriver().getCurrentUrl().contains(urlContains), "Url is not pointing to comaround article");
      dwpFeatureMethods.captureScreenshot("DRSQA_11659_Search_ComAroundArticle_FromGlobalSearch", SolQAConstants.appDWP);
      dwpDriver.close();

      log.info("*************** END: DSI - Update the Search-bar & Search-result UX ; MVP (with/without Autocompletion) ************************");
  }

}
