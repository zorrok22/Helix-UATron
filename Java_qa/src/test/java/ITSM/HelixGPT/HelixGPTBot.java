package ITSM.HelixGPT;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.MultiProviderConfig;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class HelixGPTBot extends Launcher{
    SolqaLogger log; 
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    BWFFeatureMethods bwfFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    ReadJson readJson;
    Map<Object, String> ISUsersData,ISTestData,BWFUsersData, BWFTestData,RBACUsersData;
    WebDriver isDriver, helixGPTDriver,BWFDriver=null;

    public HelixGPTBot() {
        log = new SolqaLogger(HelixGPTBot.class.getName());	    
        readJson = new ReadJson();
    }

    @Test(description = "Sharepoint (SaaS) document retrieval via HelixGPT", enabled=false, groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "20303", JiraID = "DRSQA-11718", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA11718_SharePointSaaSDocRetrivalViwHelixGPT(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------Sharepoint (SaaS) document retrieval via HelixGPT----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
                + "3. Sharepoint information source should be configured\n"
				+ "4. Sharepoint information source for BWF should be configured\n"
				+ "5. Data ingest for sharepoint should be completed\n"
				+ "6. Seed data for BWF Global Chat Skill should be disabled");
		log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_11718_Driver", getDriver());
        context.setAttribute("DRSQA_11718_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11718Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);

        log.info("Step4:- Select search setting");
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("SearchSetting"));

		log.info("Step5:- Log into HelixGPT bot and verify result");
		context.setAttribute("DRSQA_11718_Driver", getDriver());
        context.setAttribute("DRSQA_11718_Product", Product.HelixGPT);

		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11718Data");

        log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("input"));
		String Context = ISTestData.get("Context");
		String input = ISTestData.get("input");
		String expected = ISTestData.get("ExpectedResponse");
		String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		helixGPTDriver.quit();

		log.info("Step7:- Switch back to HelixGPT Agent studio page and configure Application and skill for BWF");
        innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(isDriver);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.selectApplication(ISTestData.get("BWFapplicationName"));
        String BWFSKILL_NAME = ISTestData.get("BWFskillName");
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_BUSINESS_WORKFLOWS, BWFSKILL_NAME, provider, model);

        log.info("Step8:- Select search setting");
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("SearchSetting"));
		getDriver().quit();

		log.info("Logging in to BWF");
        this.BWFDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(BWFDriver);
        BWFUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Peter");
        BWFTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11718Data");
        login(BWFUsersData.get("UserName"), BWFUsersData.get("Password"), SolQAConstants.appBWF);
        log.info("Logged in user: " + BWFUsersData.get("UserName"));
		bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
		bwfFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
		bwfFeatureMethods.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
		bwfFeatureMethods.navigateToChatOption();
		bwfFeatureMethods.sendMessageInGPTChat(BWFTestData.get("input"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		String Context_2 = BWFTestData.get("Context");
		String input_2 = BWFTestData.get("input");
		String expected_2 = BWFTestData.get("ExpectedResponse");
		String output_2 = bwfFeatureMethods.getAllBWFGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_2, input_2, expected_2, output_2);
		BWFDriver.quit();
		getDriver().quit();
		log.info("---------- END-------Sharepoint (SaaS) document retrieval via HelixGPT----------");

	}

	@Test(description = "[HelixGPT] Accessing user profile with HelixGPT", enabled=false, groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
   	@SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-11744", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA11744_AccessingUserProfileWithHelixGPT(ITestContext context,String provider,String model) throws Exception {

		try{
   log.info("Running test with Provider = " + provider + " | Model = " + model);
   // Login
   log.info("Logging in to Innovation Suite");
  
   ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11744Data");
   this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
   setDriver(isDriver);
   login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
   context.setAttribute("DRSQA_11744_Driver", getDriver());
   context.setAttribute("DRSQA_11744_Product", Product.Helix_Platform);
   innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
   innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

   // Navigate to HelixGPT Agent Studio
   log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
       innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);
     innovationSuiteFeatureMethodsPage.linkNewPrompt(ISTestData.get("PromptName"));
		innovationSuiteFeatureMethodsPage.selectAllSkillsFromSearchSetting();
   getDriver().quit();

    context.setAttribute("DRSQA_11744_Driver", getDriver());
    context.setAttribute("DRSQA_11744_Product", Product.Helix_Platform);

    ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Liam");
    ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11744Data");

    log.info("Logging in to HelixGPT Bot");
    this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
    setDriver(helixGPTDriver);
    login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
    innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
    innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

    log.info("Step3:- Send message to HelixGPT bot and verify response");
    innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Input"));
	String Context = ISTestData.get("Context");
	String input = ISTestData.get("Input");
	String expected = ISTestData.get("ExpectedResponse");
	String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
    log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
	innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
    getDriver().quit();
		}
		finally{
   log.info("Logging in to Innovation Suite for unlink the User knowledge prompt");
   ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11744Data");
   this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
   setDriver(isDriver);
   login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
   context.setAttribute("DRSQA_11744_Driver", getDriver());
   context.setAttribute("DRSQA_11744_Product", Product.Helix_Platform);
   innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
   innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

   // Navigate to HelixGPT Agent Studio
   log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
   innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
   innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
   innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);
   innovationSuiteFeatureMethodsPage.unLinkPrompt(ISTestData.get("PromptName"));
   innovationSuiteFeatureMethodsPage.selectAllSkillsFromSearchSetting();
   getDriver().quit();
		}
    log.info("---------- END-------[HelixGPT] Accessing user profile with HelixGPT---------");
}

	@Test(description = "[HelixGPT] HelixGPT retrieving CSM content", groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "20598", JiraID = "DRSQA-11735", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA11735_HelixGPTRetrieveCSMContent(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------[HelixGPT] HelixGPT retrieving CSM content----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
				+ "3. DataSource for CSM should be enabled\n"
				+ "4. Data connection for CSM should be created\n"
				+ "5. Data inject for CSM should be completed");
		log.info("-------Step1:- Login to the IS------");
       
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11735Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		 context.setAttribute("DRSQA_11735_Driver", isDriver);
        context.setAttribute("DRSQA_11735_Product", Product.Helix_Platform);

		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);
        innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("SearchSetting"));
		getDriver().quit();

		log.info("Step5:- Log into HelixGPT bot and verify result");
		

		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11735Data");

        log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
		context.setAttribute("DRSQA_11735_Driver", helixGPTDriver);
        context.setAttribute("DRSQA_11735_Product", Product.HelixGPT);
        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Input"));
		String Context = ISTestData.get("Context");
		String input = ISTestData.get("Input");
		String expected = ISTestData.get("ExpectedResponse");
		String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
        log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		getDriver().quit();
		log.info("---------- END-------[HelixGPT] HelixGPT retrieving CSM content---------");

	}

	@Test(description = "[HelixGPT] Salesforce KA (SaaS) document retrieval", groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "20599", JiraID = "DRSQA-11736", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA11736_HelixGPTSalesforceKADocumentRetrieval(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------[HelixGPT] Salesforce KA (SaaS) document retrieval----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
				+ "3. DataSource for Salesforce should be enabled\n"
				+ "4. Data connection for Salesforce should be created\n"
				+ "5. Data inject for Salesforce should be completed");
		log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_11736_Driver", getDriver());
        context.setAttribute("DRSQA_11736_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11736Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);
        innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("SearchSetting"));
		getDriver().quit();

		log.info("Step5:- Log into HelixGPT bot and verify result");
		context.setAttribute("DRSQA_11736_Driver", getDriver());
        context.setAttribute("DRSQA_11736_Product", Product.HelixGPT);

		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11736Data");

        log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Input"));
		String Context = ISTestData.get("Context");
		String input = ISTestData.get("Input");
		String expected = ISTestData.get("ExpectedResponse");
		String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
        log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		getDriver().quit();
		log.info("---------- END-------[HelixGPT] Salesforce KA (SaaS) document retrieval---------");

	}

	@Test(description = "[HelixGPT] Searching Article on Web using HelixGPT", groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "20596", JiraID = "DRSQA-11734", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA11734_HelixGPTSearchingArticleOnWebUsingHelixGPT(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------[HelixGPT] Searching Article on Web using HelixGPT----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
				+ "3. DataSource for Web should be enabled\n"
				+ "4. Data connection for Web should be created\n"
				+ "5. Data inject for Web should be completed");
		log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_11734_Driver", getDriver());
        context.setAttribute("DRSQA_11734_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11734Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("SearchSetting"));
		getDriver().quit();

		log.info("Step5:- Log into HelixGPT bot and verify result");
		context.setAttribute("DRSQA_11734_Driver", getDriver());
        context.setAttribute("DRSQA_11734_Product", Product.HelixGPT);

		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11734Data");

        log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
        innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Input"));
		String Context = ISTestData.get("Context");
		String input = ISTestData.get("Input");
		String expected = ISTestData.get("ExpectedResponse");
		String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
        log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

		log.info("Step7:- Verify feedback options are present and submit feedback");
		innovationSuiteFeatureMethodsPage.verifyIsThumbsIconPresent(ISTestData.get("FeedbackUP"));
		innovationSuiteFeatureMethodsPage.verifyIsThumbsIconPresent(ISTestData.get("FeedbackDown"));
		innovationSuiteFeatureMethodsPage.clickOnThumbIcon(ISTestData.get("FeedbackDown"));
		innovationSuiteFeatureMethodsPage.enterFeedbackAndSubmit(ISTestData.get("FeedbackMessage"));

		innovationSuiteFeatureMethodsPage.openGPTArticle(input);
	    Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.LANGCHAIN), SolQAConstants.LANGCHAIN+" name not found in URL");

		getDriver().quit();
		log.info("---------- END-------[HelixGPT] Searching Article on Web using HelixGPT---------");

	}

	@Test(description = "HelixGPT (search the BWF article , RKM article and HKM article)", groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "19540", JiraID = "DRSQA-11695", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA_11695_HelixGPTSearchBWFRKMHKMArticle(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------HelixGPT (search the BWF article , RKM article and HKM article)----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
				+ "3. Data inject for HKM RKM and BWF should be completed");
		log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_11695_Driver", getDriver());
        context.setAttribute("DRSQA_11695_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11695Data");

        log.info("Logging in to Innovation Suite");
		isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);

        
		log.info("Step4:- Select search setting");
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("BWFSearchSetting"));

		log.info("Step5:- Log into HelixGPT bot and verify result");
		
		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11695Data");

        log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
		context.setAttribute("DRSQA_11695_Driver", helixGPTDriver);
        context.setAttribute("DRSQA_11695_Product", Product.HelixGPT);

        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("BWFArticle"));
		String Context = ISTestData.get("Context");
		String input = ISTestData.get("BWFArticle");
		String expected = ISTestData.get("ExpectedResponse");
		String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
        log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		innovationSuiteFeatureMethodsPage.openGPTArticle(input);
	    Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.SSM), SolQAConstants.SSM+" Provider name not found in URL");


		log.info("Step7:- Switch back to search settings and make RKM article search setting");
		innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(isDriver);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("RKMSearchSetting"));

		log.info("Step8:- Send message to HelixGPT bot and verify response for RKM article");
		innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(helixGPTDriver);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.switchToTabByIndex(helixGPTDriver,0);
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("RKMArticle"));
		String Context_2 = ISTestData.get("Context_2");
		String input_2 = ISTestData.get("RKMArticle");
		String expected_2 = ISTestData.get("ExpectedResponse_2");
		String output_2 = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
       log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_2, input_2, expected_2, output_2);
		innovationSuiteFeatureMethodsPage.openGPTArticle(input_2);
	    Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.OfficePrinterArticleURLID), SolQAConstants.OfficePrinterArticleURLID+" Provider name not found in URL");


		log.info("Step9:- Switch back to search settings and make HKM article search setting");
		innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(isDriver);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("HKMSearchSetting"));
       isDriver.quit();

		log.info("Step10:- Send message to HelixGPT bot and verify response for HKM article");
		innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(helixGPTDriver);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.switchToTabByIndex(helixGPTDriver,0);
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("HKMArticle"));
		String Context_3 = ISTestData.get("Context_3");
		String input_3 = ISTestData.get("HKMArticle");
		String expected_3 = ISTestData.get("ExpectedResponse_3");
		String output_3 = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
       log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_3, input_3, expected_3, output_3);
		innovationSuiteFeatureMethodsPage.openGPTArticle(input_3);
	    Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.COMAROUND), SolQAConstants.COMAROUND+" Provider name not found in URL");

		getDriver().quit();
		log.info("---------- END-------HelixGPT (search the BWF article , RKM article and HKM article)---------");

	}

	@Test(description = "Helix GPT (Raise the DWP-C service , imported SRD service)", groups = {"HelixGPT", "HelixGPTBOT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "19541", JiraID = "DRSQA-11696", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA_11696_HelixGPTSearchServices(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------Helix GPT (Raise the DWP-C service , imported SRD service)\"----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
				+ "3. Router Prompt skill seed data should be disabled.");

		log.info("-------Step1:- Login to the IS------");
       
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11696Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		context.setAttribute("DRSQA_11696_Driver", getDriver());
        context.setAttribute("DRSQA_11696_Product", Product.Helix_Platform);

		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);

        isDriver.quit();

		log.info("Step5:- Log into HelixGPT bot and verify result");
		
      

        log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
		context.setAttribute("DRSQA_11696_Driver", getDriver());
        context.setAttribute("DRSQA_11696_Product", Product.Helix_Platform);

		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
		ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11696Data");
        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
        innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("DWPCserviceName"));
		String previousResponse = "";
		int sameResponseCount = 0;
		
		while (true) {

			String response = innovationSuiteFeatureMethodsPage
					.getAllGPTResponseString()
					.toLowerCase();
		
			boolean hasMsg1 = response.contains(ISTestData.get("SummaryConfirmationMessage").toLowerCase());
			boolean hasMsg2 = response.contains(ISTestData.get("SummaryConfirmationMessage2").toLowerCase());
			boolean hasMsg3 = response.contains(ISTestData.get("SummaryConfirmationMessage3").toLowerCase());
			boolean hasMsg4 = response.contains(ISTestData.get("SummaryConfirmationMessage4").toLowerCase());
		
			// ✅ Break if ANY system alert message exists
			if (hasMsg1 || hasMsg2 || hasMsg3 || hasMsg4) {
				log.info("System alert message found. Breaking loop.");
				break;
			}
		
			// ✅ Same response check
			if (response.equals(previousResponse)) {
				sameResponseCount++;
			} else {
				sameResponseCount = 1;
			}
		
			if (sameResponseCount >= 3) {
				log.info("Same response received 3 times. Breaking loop.");
				break;
			}
		
			previousResponse = response;
		
			// ✅ Validate response contains expected keys
			boolean containsAnyTestDataKey = ISTestData.keySet().stream()
					.map(key -> String.valueOf(key).toLowerCase())
					.anyMatch(response::contains);
		
			if (!containsAnyTestDataKey) {
				Assert.fail("None of the test data keys found in GPT response: " + response);
				helixGPTDriver.quit();
				break;
			}
		
			innovationSuiteFeatureMethodsPage.serviceRequestQuestion(response, ISTestData);
		}
        log.info("----step7:- Thank you for providing the details. Here's a summary of your time off request----");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.getAllGPTResponseString().contains(ISTestData.get("SummaryField1")), "Chatbot does not respond correctly");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.getAllGPTResponseString().contains(ISTestData.get("SummaryField2")), "Chatbot does not respond correctly");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.getAllGPTResponseString().contains(ISTestData.get("SummaryField3")), "Chatbot does not respond correctly");
        innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("submit"));
     
        log.info("----step8: Click on the request id  and open in new tab ---");
        innovationSuiteFeatureMethodsPage.getRequestIdFromLastResponse();
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyRequestOpenedInDWP(), "Request ID not opened in dwp");
        helixGPTDriver.quit();
       

		log.info("Logging in to HelixGPT Bot");
        this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
        setDriver(helixGPTDriver);
		context.setAttribute("DRSQA_11696_Driver", getDriver());
        context.setAttribute("DRSQA_11696_Product", Product.Helix_Platform);

        login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
        log.info("Logged in user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

        log.info("Step6:- Send message to HelixGPT bot and verify response");
        innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("SecondServiceName"));
		while (true) {

			String response = innovationSuiteFeatureMethodsPage
					.getAllGPTResponseString()
					.toLowerCase();
		
			boolean hasMsg1 = response.contains(ISTestData.get("SummaryConfirmationMessage").toLowerCase());
			boolean hasMsg2 = response.contains(ISTestData.get("SummaryConfirmationMessage2").toLowerCase());
			boolean hasMsg3 = response.contains(ISTestData.get("SummaryConfirmationMessage3").toLowerCase());
			boolean hasMsg4 = response.contains(ISTestData.get("SummaryConfirmationMessage4").toLowerCase());
		
			// ✅ Break if ANY system alert message exists
			if (hasMsg1 || hasMsg2 || hasMsg3 || hasMsg4) {
				log.info("System alert message found. Breaking loop.");
				break;
			}
		
			// ✅ Same response check
			if (response.equals(previousResponse)) {
				sameResponseCount++;
			} else {
				sameResponseCount = 1;
			}
		
			if (sameResponseCount >= 3) {
				log.info("Same response received 3 times. Breaking loop.");
				break;
			}
		
			previousResponse = response;
		
			// ✅ Validate response contains expected keys
			boolean containsAnyTestDataKey = ISTestData.keySet().stream()
					.map(key -> String.valueOf(key).toLowerCase())
					.anyMatch(response::contains);
		
			if (!containsAnyTestDataKey) {
				Assert.fail("None of the test data keys found in GPT response: " + response);
				helixGPTDriver.quit();
				break;
			}
		
			innovationSuiteFeatureMethodsPage.serviceRequestQuestion(response, ISTestData);
		}
        log.info("----step7:- Thank you for providing the details. Here's a summary of your time off request----");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.getAllGPTResponseString().contains(ISTestData.get("SecondServiceSummaryField1")), "Chatbot does not respond correctly");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.getAllGPTResponseString().contains(ISTestData.get("SecondServiceSummaryField2")), "Chatbot does not respond correctly");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.getAllGPTResponseString().contains(ISTestData.get("SecondServiceSummaryField3")), "Chatbot does not respond correctly");
        innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("submit"));
     
        log.info("----step8: Click on the request id  and open in new tab ---");
        innovationSuiteFeatureMethodsPage.getRequestIdFromLastResponse();
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyRequestOpenedInDWP(), "Request ID not opened in dwp");
        helixGPTDriver.quit();


		log.info("---------- END-------Helix GPT (Raise the DWP-C service , imported SRD service)----------");

	}

	@Test(description = "Helix GPT : Configuring Launch URL Actions in gpt", enabled=false,groups = {"HelixGPT", "HelixGPTBOT"})
	@SolqaDetails(authorName = "gsutar", TTID = "20628", JiraID = "DRSQA-11757", Products = {Product.Helix_Platform, Product.HelixGPT})
	public void DRSQA11757_HelixGPT_Configuring_Launch_URL_Actions_In_GPT(ITestContext context) {

		log.info("---------- START------- Helix GPT : Configuring Launch URL Actions in gpt----------");

	    log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_11757_Driver", getDriver());
        context.setAttribute("DRSQA_11757_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11757Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

		if (!innovationSuiteFeatureMethodsPage.getPresentBundleNames().contains(ISTestData.get("applicationName"))) {
			log.info("************* Install Package*******************");
			log.info("Go to IS >> Install");
			innovationSuiteFeatureMethodsPage.installApplication(ISTestData.get("application"));
			isDriver.navigate().refresh();
		}

		log.info("------ Step2: Open to imported application view ------");
		innovationSuiteFeatureMethodsPage.navigateToBundle(ISTestData.get("applicationName"));

		log.info("------ Step3: Navigate to url and open widget ------");
		innovationSuiteFeatureMethodsPage.navigateRequiredISURL(ISTestData.get("SolQAGPT_URL"));
		innovationSuiteFeatureMethodsPage.clickOnPalette();
		log.info("------ Step4: Click preview button ------");
		innovationSuiteFeatureMethodsPage.clickOnPreviewButton();

		log.info("------ Step5: enter exit and make sure navigated to bmc.com ------");
		innovationSuiteFeatureMethodsPage.switchToLastWindow();
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("message1"));
		innovationSuiteFeatureMethodsPage.waitForNumberOfWindows(2);
		innovationSuiteFeatureMethodsPage.switchToLastWindow();
		try{
		innovationSuiteFeatureMethodsPage.waitForUrlToBe(ISTestData.get("LaunchWebsiteURL"));
		}
		catch(Exception e){
			log.error("URL did not match expected value: " + ISTestData.get("LaunchWebsiteURL"));
			innovationSuiteFeatureMethodsPage.waitForSometime();
			innovationSuiteFeatureMethodsPage.switchToLastWindow();
		} 
		Assert.assertTrue(isDriver.getCurrentUrl().equals(ISTestData.get("LaunchWebsiteURL")),
				"User not navigated to bmc.com");
		log.info("---------- END------- DRSQA11757_HelixGPT_Configuring_Launch_URL_Actions_In_GPT----------");
	}

	@Test(description = "[25.2.00] Testing gpt response in Helixgpt manager", enabled=false,groups = {"HelixGPT", "HelixGPTBOT"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "38604", JiraID = "DRSQA-12280", Products = { Product.HelixGPT, Product.Helix_Platform })
	public void DRSQA_12280_TestingGptResponseInHelixgptManager(ITestContext context,String provider,String model) throws Exception {
		log.info("---------- START-------[25.2.00] Testing gpt response in Helixgpt manager----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		log.info("Following precondition must be set:\n"
				+ "1. Solqa_HVA_Skill should be imported successfully\n"
				+ "2. HelixGPT bot should be created and Solqa_HVA_Skill should be linked to it\n"
				+ "3. RKM data should be ingested");
		log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_12280_Driver", getDriver());
        context.setAttribute("DRSQA_12280_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_12280Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2 and step3: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_VIRTUAL_AGENT, ISTestData.get("skillName"), provider, model);
		
		log.info("Step4:- Go to prompts and make Knowledge prompt as starter prompt");
		boolean isPromptPresent = innovationSuiteFeatureMethodsPage.isPromptPresent(ISTestData.get("Knowledge_Prompt"));
		if (!isPromptPresent) {
			innovationSuiteFeatureMethodsPage.linkNewPrompt(ISTestData.get("Knowledge_Prompt"));
		}
		innovationSuiteFeatureMethodsPage.selectSkillPrompt(ISTestData.get("Knowledge_Prompt"));
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("RKMSearchSetting"));

		log.info("Step5,6,7,8,9:- These steps are covered in the preconditions");
		log.info("Step10 and 11:- Launch HelixGPT manager and click on Test");
		innovationSuiteFeatureMethodsPage.clickTestButtonForSkill(ISTestData.get("skillName"));

		log.info("Step12:- Verify that test results are displayed on the right panel and confirm that the user can click on test results to view the query response");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("RKMArticle"));

		String Context = ISTestData.get("Context");
		String input = ISTestData.get("RKMArticle");
		String expected = ISTestData.get("ExpectedResponse");
		String output = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
        log.info("Got response from HelixGPT:-"+innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString());
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

		log.info("Step13:- Verify that the user can view all Skill IDs for the query with the same ID and confirm that the test results display the Skill ID");
		innovationSuiteFeatureMethodsPage.verifySkillIDForTestSkill(ISTestData.get("SkillID"));

		log.info("Step14:- Verify that the user can click the \"New Topic\" option, ensuring it resets the conversation and starts a new conversation as expected");
		innovationSuiteFeatureMethodsPage.clickOnNewTopic();
		innovationSuiteFeatureMethodsPage.verifyNewTopicisClearingAllChat();

		log.info("Step15:- Verify that the user can click the \"Test Log\" button and confirm that the log file is downloaded successfully");
		innovationSuiteFeatureMethodsPage.clickOnTestLog();

		log.info("---------- END------- [25.2.00] Testing gpt response in Helixgpt manager----------");

	}
	
		@Test(description = "[25.3.00] HelixGPT SharePoint RBAC support", enabled=false, groups = {"Regression_P3","HelixGPT", "HelixGPTBOT"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "dpatare", TTID = "", JiraID = "DRSQA-13286", Products = { Product.HelixGPT, Product.Helix_Platform })
		public void DRSQA13286_HelixGPT_SharePoint_RBAC_support(ITestContext context, String provider, String model)
				throws Exception {
					try{
			log.info("---------- START-------HelixGPT SharePoint RBAC support----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);
			log.info("-------Step1:- Login to the IS------");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
	        setDriver(isDriver);
			context.setAttribute("DRSQA_13286_Driver",getDriver());
			context.setAttribute("DRSQA_13286_Product", Product.Helix_Platform);
			ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Liam");
			RBACUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "rbacUser");

			ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
					"DRSQA_13286Data");

			log.info("Logging in to Innovation Suite");
			
			login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appIS);
			log.info("Logged in user: " + ISUsersData.get("UserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
			log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
			innovationSuiteFeatureMethodsPage.navigateRequiredISURL(BWFFeatureDescriptionURL);
			innovationSuiteFeatureMethodsPage.searchInFormSearchBox(ISTestData.get("FetureDescription"));
			innovationSuiteFeatureMethodsPage.selectRecord();
			innovationSuiteFeatureMethodsPage.enabledOrDisabledFeatureDescription("Enabled");
			innovationSuiteFeatureMethodsPage.clickSaveOrCancelButton();
			log.info("Logging in to HelixGPT Bot");
			this.helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
			setDriver(helixGPTDriver);
			login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
			log.info("Logged in user: " + ISUsersData.get("UserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			log.info("----------HelixGPTBot login ----------");
			innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();
			log.info("Step6:- Send message to HelixGPT bot and verify response");
			innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("input"));
			String Context = ISTestData.get("Context");
			String input = ISTestData.get("input");
			String expected = ISTestData.get("ExpectedResponse");
			String output = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
			log.info("----------Authenticate with RBAC account & get GPT result ----------");
			output=innovationSuiteFeatureMethodsPage.VerifyAuthenticationForRBAC(output, ISTestData, RBACUsersData);
			log.info("gptOutput " + output);
			log.info("----------GPT Deepeval verification ----------");
			innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
			innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(isDriver);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.selectRecord();
			innovationSuiteFeatureMethodsPage.enabledOrDisabledFeatureDescription("Disabled");
			innovationSuiteFeatureMethodsPage.clickSaveOrCancelButton();
			log.info("---------- END-------HelixGPT SharePoint RBAC support----------");
					}

					finally{
						log.info("-------Step1:- Disable the Feature Description------");
						this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
						setDriver(isDriver);
						context.setAttribute("DRSQA_13286_Driver",getDriver());
						context.setAttribute("DRSQA_13286_Product", Product.Helix_Platform);
						ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Liam");
						RBACUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "rbacUser");
			
						ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
								"DRSQA_13286Data");
			
						log.info("Logging in to Innovation Suite");
						
						login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appIS);
						log.info("Logged in user: " + ISUsersData.get("UserName"));
						innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
						innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
						log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
						innovationSuiteFeatureMethodsPage.navigateRequiredISURL(BWFFeatureDescriptionURL);
						innovationSuiteFeatureMethodsPage.searchInFormSearchBox(ISTestData.get("FetureDescription"));
						innovationSuiteFeatureMethodsPage.selectRecord();
						innovationSuiteFeatureMethodsPage.enabledOrDisabledFeatureDescription("Disabled");
						innovationSuiteFeatureMethodsPage.clickSaveOrCancelButton();
					}
		}

		@AfterMethod(alwaysRun=true)
	public void closeAllDrivers() {
		WebDriver[] drivers = {isDriver, helixGPTDriver, BWFDriver};
		String[] driverNames = {"isDriver", "helixGPTDriver", "BWFDriver"};

		for (int i = 0; i < drivers.length; i++) {
			if (drivers[i] != null) {
				try {
					drivers[i].quit();
					log.info("Closed " + driverNames[i] + " browser.");
				} catch (Exception e) {
					log.warn("Exception while closing " + driverNames[i] + ": " + e.getMessage());
				} finally {
					drivers[i] = null; // avoid dangling references
				}
			}
		}

		try {
			WebDriver mainDriver = getDriver();
			if (mainDriver != null) {
				mainDriver.quit();
				log.info("Closed getDriver browser.");
			}
		} catch (Exception e) {
			log.warn("Exception while closing getDriver: " + e.getMessage());
		}
	}

}
