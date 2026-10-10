package ITSM.HelixGPT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITSM.CMDB.CMDBFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.MultiProviderConfig;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class SmartIT_HelixGPT extends Launcher {
    SolqaLogger log;
    ReadJson readJson;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    ITSMFeatureMethods itsmFeatureMethods;
    DWPFeatureMethods dwpFeatureMethods;
    BWFFeatureMethods bwfFeatureMethods;
    CMDBFeatureMethods cmdbFeatureMethods;
	CommonWaits commonWaits;
	Portal_FeatureMethods portalFeatureMethods;
	DashboadFeatureMethods helixDashboardFeatureMethods;
    Map<Object, String> ISUsersData,ISTestData,smartITUserData,smartITTestData,itsmUserData,itsmTestData,dwpUserData,dwpTestData,BWFUsersData,BWFTestData;
    WebDriver isDriver, helixGPTDriver,BWFDriver,smartITDriver,dwpDriver,itsmDriver,adeDriver=null;
    public SmartIT_HelixGPT() {
        log = new SolqaLogger(SmartIT_HelixGPT.class.getName());
        readJson = new ReadJson();
    }


    @Test(description = "Helix GPT:ITSM:AskGPT connects to model and retrive information based on summary of the incident.",enabled=false, groups = {"HelixGPT", "SmartIT"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar",  TTID="20286", JiraID = "DRSQA-11710", Products = {Product.HelixGPT, Product.SmartIT})
	public void DRSQA11710_AskGPTConnectSToModelAndRetriveInfo(ITestContext context,String provider,String model) throws Exception {
	
		log.info("---------- START-------Helix GPT:ITSM:AskGPT connects to model and retrive information based on summary of the incident.----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		log.info("Following precondition must be set:\n"
               +"1. Seed data for Incident Resolution Generation should be disabled\n"
			   +"2. Data ingest for KA should be done as per step 2 and 3\n");
		
        log.info("-------Step1:- Login to the IS------");
        context.setAttribute("DRSQA_11710_Driver", getDriver());
        context.setAttribute("DRSQA_11710_Product", Product.Helix_Platform);

        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11710Data");

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
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(ISTestData.get("applicationName"), ISTestData.get("ResolutionSkillName"), provider, model);
        isDriver.quit();
        
        log.info("Step 4: Login to SmarttIT");
        context.setAttribute("DRSQA_11710_Driver", getDriver());
        context.setAttribute("DRSQA_11710_Product", Product.Helix_Platform);
        
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon"); 
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11710Data");

        log.info("Logging in to Innovation Suite");
		this.smartITDriver=launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		log.info("Logged in user: " + smartITUserData.get("UserName"));

        log.info("**** Navigating to Create New Incident ********");
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.navigateToCreateNewIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11801_navigatedToCreateIncident",SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethodsPage.clickAssigntoMeInIncidentCreatePage();
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11801_IncidentCreated",SolQAConstants.appSmartIT);
        String incidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
		smartITFeatureMethodsPage.navigateToAskGPT();
        log.info("Created Incident ID: " + incidentID);
		String Context = smartITTestData.get("Context");
		String input = smartITTestData.get("Input");
		String expected = smartITTestData.get("ExpectedResponse");
		String output = smartITFeatureMethodsPage.getAllAskGPTResponseString();
        log.info("Got response from HelixGPT:-"+output);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		log.info("Step 5: Validating response from HelixGPT in Smart IT");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
        smartITDriver.quit();
	}

    @Test(description = "[25.2] Verify Helix GPT and new Global Search Functionality in Smart IT, BWF",enabled = false, groups = {"HelixGPT", "SmartIT, BWF, ITSM"})
	@SolqaDetails(authorName = "powani", TTID = "37716", JiraID = "DRSQA-11777", Products = { Product.Helix_ITSM,Product.SmartIT, Product.Helix_BWF, Product.HelixGPT })
	public void DRSQA_11777_VerifyHelixGPTandNewGlobalSearchFunctionalityInSmartITBWF(ITestContext context) throws Exception {

		log.info("------------START :[25.2] Verify Helix GPT and new Global Search Functionality in Smart IT, BWF----------");

		log.info("-----Precondition : 1. Go to AR System Administrator -> AR System Administrator Console -> System -> General -> CCS configuration under " + SolQAConstants.ITSM_SMARTIT_COMPONENT + ".\n" +
				"\n" +
				"2. Set the following parameter:\n" +
				"    enableHelixGPTGlobalChat = true\n" +
				"    helixgptGlobalSearchDefaultMode = search\n" +
				"\n" +
				"3. Under com.bmc.arsys.shared, verify the URL:\n" +
				"HelixGPT-Assistant-Service-URL = http://helix-gpt-assistant:8001\n" +
				"\n" +
				"\n" +
				"BWF  --> setting --> Line Business  --> Manage Line of Business --> Helix GPT --> Chat with Helix GPT  = True , open the setting  enable the setting -----------");

			log.info("-----Precondition-----------");
			log.info("logging in into ITSM");
			
			itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
			itsmDriver=launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(itsmDriver);
			context.setAttribute("DRSQA_11777_Driver", getDriver());
			context.setAttribute("DRSQA_11777_Product", Product.Helix_ITSM);
			login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
			itsmFeatureMethods.captureScreenshot("DRSQA_11777_ITSM_Login", SolQAConstants.appITSM);
			log.info("Logged in user: " + itsmUserData.get("UserName"));
			log.info("****** Go to Centralized Configuration ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			log.info("Disable HKM setting in ITSM");
			itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
			itsmFeatureMethods.closeBrowser();
		log.info("Step 2: Login to SmarttIT");
        
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11777Data");
		this.smartITDriver=launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
		context.setAttribute("DRSQA_11777_Driver", getDriver());
        context.setAttribute("DRSQA_11777_Product", Product.Helix_Platform);

        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		log.info("Logged in user: " + smartITUserData.get("UserName"));

        log.info("**** Navigating to Create New Incident ********");
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.navigateToCreateNewIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11777_navigatedToCreateIncident",SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethodsPage.clickAssigntoMeInIncidentCreatePage();
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_11777_IncidentCreated",SolQAConstants.appSmartIT);
        String FirstIncidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
        log.info("Created Incident ID: " + FirstIncidentID);

		log.info("----- Step 2: Verify the Helix GPT Global Search icon --------");
		smartITFeatureMethodsPage.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();

		log.info("---- Step 3:- Click on the Helix GPT Global Search icon-----------");
		smartITFeatureMethodsPage.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
		smartITFeatureMethodsPage.defaultSearchToggleButton(SolQAConstants.SEARCH);

		log.info("---- Step 4:- Verify Helix GPT Global Search and verify url at the end----------");
		Assert.assertTrue(getDriver().getCurrentUrl().contains(smartITTestData.get("globalSearchURL")));

		log.info("---- Step 5:- Verify the filter options in the search bar-----------");
		smartITFeatureMethodsPage.verifyFilterOptionsOnSearchDropdownMenus();

		log.info("----- Step 6: Search for text in the Helix GPT Global Search and hit enter -> Verify count of items returned" +
				"--> Click on Knowledge Article (KA), Ticket, Asset, or Person and open full view -------");
		smartITFeatureMethodsPage.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_KNOWLEDGE, smartITTestData.get("knowledgeSearch"));
		log.info("Verifying count of items returned for Knowledge Article search");
        smartITFeatureMethodsPage.verifyCountOfItemsReturnedAndOpenFullView(smartITTestData.get("knowledgeSearch"),1,4);
		getDriver().navigate().refresh();
		smartITFeatureMethodsPage.waitForAllSmartITSpinnersToHide();
		smartITFeatureMethodsPage.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_TICKET, FirstIncidentID);
		smartITFeatureMethodsPage.verifyCountOfItemsReturnedAndOpenFullView(FirstIncidentID,1,3);
		smartITFeatureMethodsPage.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_ASSET, smartITTestData.get("assetSearch"));
		smartITFeatureMethodsPage.verifyCountOfItemsReturnedAndOpenFullView(smartITTestData.get("assetSearch"),1,4);
		smartITFeatureMethodsPage.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_PEOPLE, smartITTestData.get("peopleSearch"));
		smartITFeatureMethodsPage.verifyCountOfItemsReturnedAndOpenFullView(smartITTestData.get("peopleSearch"),1,5);

		log.info("----- Step 7: Login to DWP and raise a request ------------");
		log.info("*********** DRSQA_11777_Communication_DWP_ITSM *******************");
		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		context.setAttribute("DRSQA_11777_Driver", getDriver());
		context.setAttribute("DRSQA_11777_Product", Product.Helix_DWP);

		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11777Data");

        log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
        String SERVICE_NAME = dwpTestData.get("ServiceRequestName");
		log.info("Open Catalog Page");
        dwpFeatureMethods = new DWPFeatureMethods();
		dwpFeatureMethods.openCatalogPage();
		if (dwpFeatureMethods.isShowAllItemsLinkVisible()) {
			dwpFeatureMethods.openAllItemsPage();
			dwpFeatureMethods.searchAndOpenService(SERVICE_NAME);
		} else {
			dwpFeatureMethods.searchAndOpenService(SERVICE_NAME);
			dwpFeatureMethods.clickOnRequestNow(SERVICE_NAME);
		}

		log.info("request a service: " + SERVICE_NAME);
		dwpFeatureMethods.verifySrdTitle(SERVICE_NAME);

		log.info("Request is submitted");
		dwpFeatureMethods.clickOnSubmitRequest();
		dwpFeatureMethods.captureScreenshot("DRSQA_11777_requestCreated", SolQAConstants.appDWP);
		String firstRequest = dwpFeatureMethods.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
		dwpDriver.close();

		log.info("------ Step 8: Login to Smart IT and search the DWP request ID in Helix GPT Global Search ---------");
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		smartITFeatureMethodsPage.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_DWP_REQUEST, firstRequest);
		smartITFeatureMethodsPage.verifyCountOfItemsReturnedAndOpenFullView(SERVICE_NAME, 1, 4);

		log.info("----- Step 9: Login to BWF with case agent -------");
		log.info("Logging in to BWF");
        this.BWFDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(BWFDriver);
        BWFUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        BWFTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11777Data");
        login(BWFUsersData.get("UserName"), BWFUsersData.get("Password"), SolQAConstants.appBWF);
        log.info("Logged in user: " + BWFUsersData.get("UserName"));
		bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
		 log.info("Create a case");
        String caseID = bwfFeatureMethods.createCase(BWFTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_11783_DRSQA_11794_Case created successfully", SolQAConstants.appBWF);
        log.info("Case ID: " + caseID);
		getDriver().navigate().refresh();
		
		log.info("------ Step 12: Verify the new Global Search icon -------");
		bwfFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();

		log.info("------ Step 13: Click on the search icon -> Verify filter options -------");
		bwfFeatureMethods.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
		bwfFeatureMethods.defaultSearchToggleButton(SolQAConstants.SEARCH);
		bwfFeatureMethods.verifyFilterOptionsOnSearchDropdownMenus();

		log.info("---- Step 14:- Verify Helix GPT Global Search and verify url at the end----------");
		Assert.assertTrue(BWFDriver.getCurrentUrl().contains(BWFTestData.get("globalSearchURL")));

		log.info("------ Step 15: Search the requested data  -> Case -= with case id\n" +
				"People = Elizabeth \n" +
				"Knowledge article = FMLA\n -------");
		bwfFeatureMethods.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_PEOPLE, BWFTestData.get("personName"));
		bwfFeatureMethods.verifyCountOfItemsReturnedAndOpenFullView(SolQAConstants.FILTER_PEOPLE,BWFTestData.get("personName"), 1, 4);
		bwfFeatureMethods.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_KNOWLEDGE_ARTICLES, BWFTestData.get("kaName"));
		bwfFeatureMethods.verifyCountOfItemsReturnedAndOpenFullView(SolQAConstants.FILTER_KNOWLEDGE_ARTICLES,BWFTestData.get("kaName"), 1, 3);
		bwfFeatureMethods.selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_CASES,caseID);
		bwfFeatureMethods.verifyCountOfItemsReturnedAndOpenFullView(SolQAConstants.FILTER_CASES,caseID, 1, 2);
		BWFDriver.close();
		log.info("------------END : [25.2] Verify Helix GPT and new Global Search Functionality in Smart IT, BWF----------");

	}

	@Test(description = "[25.3.00] Searching Ticket Information and (HKM, RKM) knowledge article on GPT ITSM Global Chat From Smart IT", groups = {"HelixGPT", "SmartIT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "37719", JiraID = "DRSQA-11779", Products = { Product.Helix_ITSM,Product.SmartIT })
	public void DRSQA11779_SearchingHKMRKMKnowledgeArticleOnGPTITSMGlobalChat(ITestContext context,String provider,String model) throws Exception {
		log.info("------------START---------[25.3.00] Searching Ticket Information and (HKM, RKM) knowledge article on GPT ITSM Global Chat From Smart IT----------");
		try {
			log.info("-----Precondition-----------");
			log.info("logging in into ITSM");
			
			itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
			itsmDriver=launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(itsmDriver);
			context.setAttribute("DRSQA_11779_Driver", itsmDriver);
			context.setAttribute("DRSQA_11779_Product", Product.Helix_ITSM);
			login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
			itsmFeatureMethods = new ITSMFeatureMethods();
			itsmFeatureMethods.captureScreenshot("DRSQA_11779_ITSM_Login", SolQAConstants.appITSM);
			log.info("Logged in user: " + itsmUserData.get("UserName"));
			log.info("****** Go to Centralized Configuration ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			log.info("Enable HKM setting in ITSM");
			itsmFeatureMethods.setSelfService_HKM(true, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
			itsmFeatureMethods.closeBrowser();

			log.info("-------------- Configuring Comaround HKM Integration settings in Innovation Studio under Administrator -> Knowledge Configuration page ---------");
			isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			context.setAttribute("DRSQA_11779_Driver", isDriver);
			context.setAttribute("DRSQA_11779_Product", Product.Helix_InnovationStudio);
			log.info(" Login to IS with "+CommonMethods.readConfig("adminUserName"));
			ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS,"ISTestData","ComAroundDetails");
			login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"),SolQAConstants.appIS);
			String knowledgeManagement = ISTestData.get("knowledgeManagement");
			log.info(" Navigate to Administration > Knowledge Management > Knowledge Configuration and set HKM configuration");
			innovationSuiteFeatureMethodsPage.navigateToAdminPage();
			innovationSuiteFeatureMethodsPage.navigateToConfiguration(knowledgeManagement);
			innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(ISTestData);
			log.info(" Navigate to Administration > Knowledge Management > Knowledge access mapping and set mapping for "+SolQAConstants.PETRAMCO_COMPANY);
    		innovationSuiteFeatureMethodsPage.setAccessMappingComAround(SolQAConstants.PETRAMCO_COMPANY);
			isDriver.close();

			log.info("-------Step1:- Login to the IS------");
			

			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11779Data");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			context.setAttribute("DRSQA_11779_Driver", isDriver);
			context.setAttribute("DRSQA_11779_Product", Product.Helix_Platform);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			log.info("Step 2: Navigate to the HelixGPT Agent Studio Page");
		    innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_IT_SERVICE_MANAGEMENT, ISTestData.get("skillName"), provider, model);
            innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("InfoProvider1"));
			log.info("Step 4: Login to SmarttIT");
			

			smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
			smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11779Data");
			this.smartITDriver=launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
			setDriver(smartITDriver);
			context.setAttribute("DRSQA_11779_Driver", smartITDriver);
			context.setAttribute("DRSQA_11779_Product", Product.Helix_Platform);
			login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
			log.info("Logged in user: " + smartITUserData.get("UserName"));
            smartITFeatureMethodsPage= new SmartITFeatureMethodsPage();
			smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
			log.info("----- Step 5: Verify the Helix GPT Global Search icon --------");
			smartITFeatureMethodsPage.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();

			log.info("---- Step 6:- Click on the Helix GPT Global Search icon-----------");
			smartITFeatureMethodsPage.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
			smartITFeatureMethodsPage.defaultSearchToggleButton(SolQAConstants.CHAT);

			log.info("---Step7:- Enter 'Hi' on chat button -----------");
			smartITFeatureMethodsPage.sendMessageInHelixGPTChat(smartITTestData.get("FirstMessage"));

			log.info("---Step8:- Search the Install flash player article (HKM)-----------");
			smartITFeatureMethodsPage.sendMessageInHelixGPTChat(smartITTestData.get("HKMArticle"));

			log.info("---Step9:- Open HKM article in new tab and check the url-----------");
		    String Context = smartITTestData.get("Context");
		    String input = smartITTestData.get("HKMArticle");
		    String expected = smartITTestData.get("ExpectedResponse");
		    String output = smartITFeatureMethodsPage.getAllGPTResponseString();
		    innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		    log.info("Step 5: Validating response from HelixGPT in Smart IT");
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
            smartITFeatureMethodsPage.openGPTArticle(smartITTestData.get("HKMArticleLink"));
	        Assert.assertTrue(smartITFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.COMAROUND), SolQAConstants.COMAROUND+" Provider name not found in URL");

			log.info("logging in into ITSM");
			
			itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
			itsmDriver=launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(itsmDriver);
			context.setAttribute("DRSQA_11779_Driver", itsmDriver);
			context.setAttribute("DRSQA_11779_Product", Product.Helix_Platform);
			login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
			itsmFeatureMethods.captureScreenshot("DRSQA_11779_ITSM_Login", SolQAConstants.appITSM);
			log.info("Logged in user: " + itsmUserData.get("UserName"));
			log.info("****** Go to Centralized Configuration ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			log.info("Disable HKM setting in ITSM");
			itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
			itsmDriver.close();

			log.info("Switching to HelixGPT Page in Innovation Suite");
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(isDriver);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			log.info("Got to Search settings tab >> Enable RKM ");
			innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("InfoProvider2"));

			log.info("---- Search for the How To Connect To The Office Printer / Copier (RKM article)-----------");
			smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);

			log.info("----- Verify the Helix GPT Global Search icon --------");
			smartITFeatureMethodsPage.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();

			log.info("---- Click on the Helix GPT Global Search icon-----------");
			smartITFeatureMethodsPage.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
			smartITFeatureMethodsPage.defaultSearchToggleButton(SolQAConstants.CHAT);

			log.info("---- Verify Helix GPT Global Search and verify url at the end----------");
			Assert.assertTrue(getDriver().getCurrentUrl().contains(smartITTestData.get("globalSearchURL")));

			log.info("---Enter 'Hi' on chat button -----------");
			smartITFeatureMethodsPage.sendMessageInHelixGPTChat(smartITTestData.get("FirstMessage"));

			log.info("---Search the Install flash player article (RKM)-----------");
			smartITFeatureMethodsPage.sendMessageInHelixGPTChat(smartITTestData.get("RKMArticle"));

			log.info("---Open RKM article in new tab and check the url-----------");
			String Context_2 = smartITTestData.get("Context_2");
		    String input_2 = smartITTestData.get("RKMArticle");
		    String expected_2 = smartITTestData.get("ExpectedResponse_2");
		    String output_2 = smartITFeatureMethodsPage.getAllGPTResponseString();
            log.info("Got response from HelixGPT:-"+output_2);
		    innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		    log.info("Step 5: Validating response from HelixGPT in Smart IT");
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_2, input_2, expected_2, output_2);
			smartITFeatureMethodsPage.openGPTArticle(smartITTestData.get("RKMArticle"));
		    Assert.assertTrue(smartITFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.OfficePrinterArticleURLID), "URL not contains any provider value");

			log.info("Switching to HelixGPT Page in Innovation Suite");
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.switchToHelixGPTPage(isDriver);
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			log.info("Got to Search settings tab >> Enable RKM ");
			innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("InfoProvider3"));

			smartITFeatureMethodsPage.switchToSmartITPage(smartITDriver);
			log.info("--- Ask Question on Chat related ticket----------");
			smartITFeatureMethodsPage.sendMessageInHelixGPTChat(smartITTestData.get("INC_Question"));
			String Context_3 = smartITTestData.get("Context_3");
		    String input_3 = smartITTestData.get("INC_Question");
		    String expected_3 = smartITTestData.get("ExpectedResponse_3");
		    String output_3 = smartITFeatureMethodsPage.getAllGPTResponseString();
		    innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		    log.info("Step 5: Validating response from HelixGPT in Smart IT");
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_3, input_3, expected_3, output_3);

			log.info("---Step9 and step10:- List All Incident and click on incident hyperlink ----------");
			smartITFeatureMethodsPage.openFirstRecordFromTable();
			List<String> tab2 = new ArrayList<>(smartITDriver.getWindowHandles());
			commonWaits=new CommonWaits();
			commonWaits.switchToLastWindow();
			log.info("Verifying incident is opened in view page");
			log.info("Current URL: " + smartITDriver.getCurrentUrl());
			Assert.assertTrue(smartITDriver.getCurrentUrl().contains("incident"), "Incident is not opened in view page");
			smartITDriver.close();
			log.info("---Step11:- Click on pencil icon bottom of chat----------");
			smartITDriver.switchTo().window(tab2.get(0));
			smartITFeatureMethodsPage.sendMessageInHelixGPTChat(smartITTestData.get("FirstMessage"));
			smartITFeatureMethodsPage.clickOnNewTopic();
			Assert.assertFalse(smartITFeatureMethodsPage.isElementPresentAndDisplayed(smartITFeatureMethodsPage.allMessagesFromTable()), "The table is displayed. chat is not cleared");
			smartITDriver.quit();
		}
		finally {
			log.info("logging in into ITSM for cleanup");
			context.setAttribute("DRSQA_11779_Driver", itsmDriver);
			context.setAttribute("DRSQA_11779_Product", Product.Helix_ITSM);
			itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
			this.itsmDriver=launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(itsmDriver);
			login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
			log.info("Logged in user: " + itsmUserData.get("UserName"));

			log.info("****** Go to Centralized Configuration ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			log.info("Disable HKM setting in ITSM");
			itsmFeatureMethods.setSelfService_HKM(false, configProp.get("ARServerAlias"), configProp.get("adminUserName"), configProp.get("adminPassword"));
			itsmDriver.quit();
		}
		log.info("------------END---------[25.3.00] Searching Ticket Information and (HKM, RKM) knowledge article on GPT ITSM Global Chat From Smart IT----------");
	}

	@Test(description = "[25.2.00] ML-based Incident resolution notes population", enabled = false, groups = {"HelixGPT", "SmartIT","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "38620", JiraID = "DRSQA-12296", Products = { Product.Helix_ITSM,Product.SmartIT,Product.HelixGPT })
	public void DRSQA_12296_ML_basedIncidentResolutionNotesPopulation(ITestContext context,String provider,String model) throws Exception {

		log.info("------------START :[25.2.00] ML-based Incident resolution notes population----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

        log.info("Precondition:- Incident resolution generation skill seed data should be disabled");
        
		log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		context.setAttribute("DRSQA_12296_Driver", getDriver());
		context.setAttribute("DRSQA_12296_Product", Product.Helix_InnovationStudio);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_12296Data");
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_IT_SERVICE_MANAGEMENT, ISTestData.get("SkillName"), provider, model);
		isDriver.quit();

		log.info("Step 1: Login to SmarttIT");
       

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_12296Data");
		this.smartITDriver=launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
		context.setAttribute("DRSQA_12296_Driver", getDriver());
        context.setAttribute("DRSQA_12296_Product", Product.Helix_Platform);
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		log.info("Logged in user: " + smartITUserData.get("UserName"));

        log.info("Step2:- Click on Create New and create a new incident");
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.navigateToCreateNewIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_12296_navigatedToCreateIncident",SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_12296_IncidentCreated",SolQAConstants.appSmartIT);
        String FirstIncidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
        log.info("Created Incident ID: " + FirstIncidentID);

		log.info("Step3:- Edit incident and move to status Assigned to In progress and check the activity note is updated");
		smartITFeatureMethodsPage.addComment_InActivityFeed(smartITTestData.get("Note"));

		log.info("Step4:- Edit Incident and move to resolved status and click on Ask HelicGPT button under Resolution note and verify the resolution note");
		log.info("**** Changing status to Resolved ********");
		smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("ResolvedStatus"),smartITTestData.get("ResolvedStatusReason"), "");
		smartITFeatureMethodsPage.clickOnResolutionNoteAskHelixGPT();
		smartITFeatureMethodsPage.saveIncident();
		String Context = smartITTestData.get("Context");
		String input = smartITTestData.get("Input");
		String expected = smartITTestData.get("ExpectedResponse");
		String output = smartITFeatureMethodsPage.getResolutionNote_viewPage();
        log.info("Got response from HelixGPT:-"+output);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		log.info("Step 5: Validating response from HelixGPT in Smart IT");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		getDriver().quit();
		log.info("------------END : [25.2.00] ML-based Incident resolution notes population----------");

	}

	@Test(description = "[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Normalization Jobs", enabled=false, groups = {"HelixGPT", "SmartIT","CMDB","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-14662", Products = { Product.CMDB,Product.ITSM,Product.HelixGPT })
	public void DRSQA_14662_DataMonitorHelixGPTAgenticAssistedResolutionForNormalizationJobs(ITestContext context,String provider,String model) throws Exception {

		log.info("------------START :[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Normalization Jobs----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

        log.info("Precondition:- CMDB Data Monitor Azure OpenAI GPT 4.1 skill seed data should be disabled");
        
		log.info("Step1:- Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_14662Data");
		log.info("Logged in user: " + configProp.get("adminUserName"));
		context.setAttribute("DRSQA_14662_Driver", isDriver);
        context.setAttribute("DRSQA_14662_Product", Product.Helix_Platform);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step2: Go to HelixGPT agent Studio->Settings->Select Application: Automated Asset Assignment");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.AUTOMATED_ASSET_ASSIGNMENT, ISTestData.get("SkillName"), provider, model);
		
        log.info("Step3:- Click on CMDB Data Monitor Azure OpenAI GPT-4o Skill and click on Action and Select Test");
		innovationSuiteFeatureMethodsPage.clickTestButtonForSkill(ISTestData.get("SkillName"));

		log.info("Step4:- Ask question on Test Skill Confirm if normalization is enabled for class `BMC_ComputerSystem` in dataset `BMC.ASSET`.");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_1"));
		String Context = ISTestData.get("Context_1");
		String input = ISTestData.get("Question_1");
		String expected = ISTestData.get("ExpectedResponse_1");
		String output = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

		log.info("Step5:- Ask question on Test Skill Is normalization enabled for class `BMC_Printer` across all datasets?");
        innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_2"));
		String Context2 = ISTestData.get("Context_2");
		String input2 = ISTestData.get("Question_2");
		String expected2 = ISTestData.get("ExpectedResponse_2");
		String output2 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context2, input2, expected2, output2);

		log.info("Step6:- Ask question on Test Skill List all normalization jobs for dataset id BMC.ASSET and their recent run statuses.");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_3"));
		String Context3 = ISTestData.get("Context_3");
		String input3 = ISTestData.get("Question_3");
		String expected3 = ISTestData.get("ExpectedResponse_3");
		String output3 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context3, input3, expected3, output3);

		log.info("Step7:- Ask question on Test Skill For class `BMC_NetworkDevice`, confirm normalization is enabled and list the last three normalization job statuses.");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_4"));
		String Context4 = ISTestData.get("Context_4");
		String input4 = ISTestData.get("Question_4");
		String expected4 = ISTestData.get("ExpectedResponse_4");
		String output4 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context4, input4, expected4, output4);

		log.info("Step8:- Ask question on Test Skill List all normalized CIs where some attributes() are null");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_5"));
		String Context5 = ISTestData.get("Context_5");
		String input5 = ISTestData.get("Question_5");
		String expected5 = ISTestData.get("ExpectedResponse_5");
		String output5 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context5, input5, expected5, output5);

		log.info("Step9:- Ask question on Test Skill List all datasets where all inline/batch normalizations are enabled or assist me accordingly");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_6"));
		String Context6 = ISTestData.get("Context_6");
		String input6 = ISTestData.get("Question_6");
		String expected6 = ISTestData.get("ExpectedResponse_6");
		String output6 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context6, input6, expected6, output6);

		log.info("Step10:- Ask question on Test Skill What are the differnent normalization features enabled on a different dataset(name)");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_7"));
		String Context7 = ISTestData.get("Context_7");
		String input7 = ISTestData.get("Question_7");
		String expected7 = ISTestData.get("ExpectedResponse_7");
		String output7 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context7, input7, expected7, output7);

		log.info("Step11:- Ask question on Test Skill InVersionRollup rule enabled for a particular class/product?");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_8"));
		String Context8 = ISTestData.get("Context_8");
		String input8 = ISTestData.get("Question_8");
		String expected8 = ISTestData.get("ExpectedResponse_8");
		String output8 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context8, input8, expected8, output8);

		log.info("Step12:- Ask question on Test Skill Suiterollup rule enabled for particular class/product? or suggest me accordingly");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_9"));
		String Context9 = ISTestData.get("Context_9");
		String input9 = ISTestData.get("Question_9");
		String expected9 = ISTestData.get("ExpectedResponse_9");
		String output9 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context9, input9, expected9, output9);
		getDriver().quit();

		log.info("------------END :[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Normalization Jobs----------");

	}
	
	@Test(description = "[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Reconciliation Jobs", enabled=false, groups = {"HelixGPT", "SmartIT","CMDB","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-14661", Products = { Product.CMDB,Product.ITSM,Product.HelixGPT })
	public void DRSQA_14661_DataMonitorHelixGPTAgenticAssistedResolutionForReconciliationJobs(ITestContext context,String provider,String model) throws Exception {

		log.info("------------START :[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Reconciliation Jobs----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		context.setAttribute("DRSQA_14661_Driver", getDriver());
		context.setAttribute("DRSQA_14661_Product", Product.HelixGPT);

        log.info("Precondition:- CMDB Data Monitor Azure OpenAI GPT 4.1 skill seed data should be disabled");
        
		log.info("Step1:- Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_14661Data");
		log.info("Logged in user: " + configProp.get("adminUserName"));
		context.setAttribute("DRSQA_14661_Driver", isDriver);
        context.setAttribute("DRSQA_14661_Product", Product.Helix_Platform);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step2: Go to HelixGPT agent Studio->Settings->Select Application: Automated Asset Assignment");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.AUTOMATED_ASSET_ASSIGNMENT, ISTestData.get("SkillName"), provider, model);
		
        log.info("Step3:- Click on CMDB Data Monitor Azure OpenAI GPT-4o Skill and click on Action and Select Test");
		innovationSuiteFeatureMethodsPage.clickTestButtonForSkill(ISTestData.get("SkillName"));

		log.info("Step4:- Ask question on Test Skill How many recon jobs present around the dataset BMC.ADDM");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_1"));
		String Context = ISTestData.get("Context_1");
		String input = ISTestData.get("Question_1");
		String expected = ISTestData.get("ExpectedResponse_1");
		String output = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		
	  	log.info("Step5:- Login to CMDB and open BMC.CORE:BMC_ComputerSystem form and copy the instance ID search it on CMDB Data Monitor Azure OpenAI GPT-4o Skill below like");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_2"));
		String Context_2 = ISTestData.get("Context_2");
		String input_2 = ISTestData.get("Question_2");
		String expected_2 = ISTestData.get("ExpectedResponse_3");
		String output_2 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_2, input_2, expected_2, output_2);
		
		log.info("Step6:- Ask question on Test Skill List jobs that use multiple datasets");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_3"));
		String Context_3 = ISTestData.get("Context_3");
		String input_3 = ISTestData.get("Question_3");
		String expected_3 = ISTestData.get("ExpectedResponse_3");
		String output_3 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_3, input_3, expected_3, output_3);
		
		log.info("Step7:- Ask question on Test Skill For all CIs in any dataset, list reconciliation jobs and report any with non-successful statuses.");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_4"));
		String Context_4 = ISTestData.get("Context_4");
		String input_4 = ISTestData.get("Question_4");
		String expected_4 = ISTestData.get("ExpectedResponse_4");
		String output_4 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_4, input_4, expected_4, output_4);
		
		log.info("Step8:- Ask question on Test Skill List all the CIs that are failed to be identified and suggest ways to fix it");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_5"));
		String Context_5 = ISTestData.get("Context_5");
		String input_5 = ISTestData.get("Question_5");
		String expected_5 = ISTestData.get("ExpectedResponse_5");
		String output_5 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_5, input_5, expected_5, output_5);
		getDriver().quit();

		log.info("------------END :[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Normalization Jobs----------");

	}

	@Test(description = "[25.4.00] Data Monitor HelixGPT Agentic assisted resolution", enabled=false, groups = { "HelixGPT", "SmartIT",
			"CMDB" }, dataProvider = "providerConfig", dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "mhedaoo", TTID = "", JiraID = "DRSQA-14660", Products = { Product.CMDB, Product.ITSM,
			Product.HelixGPT })
	public void DRSQA_14660_DataMonitorHelixGPTAgenticAssistedResolution(ITestContext context, String provider,
			String model) throws Exception {

		log.info("------------START :[25.4.00] Data Monitor HelixGPT Agentic assisted resolution----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);
		context.setAttribute("DRSQA_14660_Driver", getDriver());
		context.setAttribute("DRSQA_14660_Product", Product.HelixGPT);

		log.info("Step1:- Logging in to Innovation Suite");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
				"DRSQA_14660Data");
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

		log.info("Step2: Go to HelixGPT agent Studio->Settings->Select Application: Automated Asset Assignment");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.AUTOMATED_ASSET_ASSIGNMENT,
				ISTestData.get("SkillName"), provider, model);

		log.info("Step3:- Click on CMDB Data Monitor Azure OpenAI GPT-4o Skill and click on Action and Select Test");
		innovationSuiteFeatureMethodsPage.clickTestButtonForSkill(ISTestData.get("SkillName"));

		log.info("Step4:- Ask question on Test Skill How many recon jobs present around the dataset BMC.ADDM");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_1"));
		String Context = ISTestData.get("Context_1");
		String input = ISTestData.get("Question_1");
		String expected = ISTestData.get("ExpectedResponse_1");
		String output = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

		log.info("Step5:- List name, short desc, serial no for CI with instance id ASHAA5V0GQBORANBA2HA85K4PSG6TT");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_2"));
		String Context_2 = ISTestData.get("Context_2");
		String input_2 = ISTestData.get("Question_2");
		String expected_2 = ISTestData.get("ExpectedResponse_3");
		String output_2 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_2, input_2, expected_2, output_2);

		log.info(
				"Step6:- I have incorrect value for the Model and ManufacturerName attributes for the CI with instance id: OIGFVX7SGPYXWATBPA56TBPA56NZPV.\r\n"
						+ "This belongs to the BMC.ADDM dataset under the BMC_ComputerSystem class.\r\n"
						+ "What is the root cause?");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_3"));
		String Context_3 = ISTestData.get("Context_3");
		String input_3 = ISTestData.get("Question_3");
		String expected_3 = ISTestData.get("ExpectedResponse_3");
		String output_3 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_3, input_3, expected_3, output_3);

		log.info("Step7:- Identify CIs in dataset BMC.ASSET with missing `Serial Number` attribute;");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_4"));
		String Context_4 = ISTestData.get("Context_4");
		String input_4 = ISTestData.get("Question_4");
		String expected_4 = ISTestData.get("ExpectedResponse_4");
		String output_4 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_4, input_4, expected_4, output_4);

		log.info("Step8:- Retrieve CIs from specific dataset with missing `Product Name` attribute;");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_5"));
		String Context_5 = ISTestData.get("Context_5");
		String input_5 = ISTestData.get("Question_5");
		String expected_5 = ISTestData.get("ExpectedResponse_5");
		String output_5 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_5, input_5, expected_5, output_5);

		log.info(
				"Step9:- For CI with instance id OIGDHUF7D7Z57ATCG6RKTCG6RKPW6Y in BMC.ASSET, if ReconciliationIdentity is '0', "
						+ "point out possible manual creation and refer to the `Submitter` attribute.");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_6"));
		String Context_6 = ISTestData.get("Context_6");
		String input_6 = ISTestData.get("Question_6");
		String expected_6 = ISTestData.get("ExpectedResponse_6");
		String output_6 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_6, input_6, expected_6, output_6);

		log.info(
				"Step10:- Ask question: List all CIs in BMC.ASSET where the `Owner` attribute is empty; for each, retrieve "
						+ "ReconciliationIdentity and check if it is '0'.");
		innovationSuiteFeatureMethodsPage.sendMessage(ISTestData.get("Question_7"));
		String Context_7 = ISTestData.get("Context_7");
		String input_7 = ISTestData.get("Question_7");
		String expected_7 = ISTestData.get("ExpectedResponse_7");
		String output_7 = innovationSuiteFeatureMethodsPage.getAllSkillGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_7, input_7, expected_7, output_7);

		getDriver().quit();

		log.info(
				"------------END :[25.4.00] Data Monitor HelixGPT Agentic assisted resolution for Normalization Jobs----------");

	}

	@Test(description = "Create a Major Incident Report using Helix GPT on Incident", enabled=false, groups = {"HelixGPT", "SmartIT","gsutar"}, dataProvider = "providerConfig", dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-16216", Products = { Product.Helix_ITSM, Product.SmartIT, Product.HelixGPT })
	public void DRSQA_16216_CreateMajorIncidentReportUsingHelixGPTOnIncident(ITestContext context, String provider, String model) throws Exception {

		log.info("------------START :[26.1.00] Create a Major Incident Report using Helix GPT on Incident----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

		log.info("Precondition: Enable the feature flag from Innovation Suite 'MI Report Generation' feature flag is enabled in ITSM Application Settings > HelixGPT Configuration > Features.");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		context.setAttribute("DRSQA_16216_Driver", getDriver());
		context.setAttribute("DRSQA_16216_Product", Product.Helix_InnovationStudio);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));

		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.ITSM_FEATURES_URL);
		innovationSuiteFeatureMethodsPage.enableRequiredFeatureFlag(SolQAConstants.MAJOR_INCIDENT_REPORT);

		log.info("Precondition: Configure HelixGPT Agent Studio skill 'Agent Assistance For ITSM Helix GPT' for application 'BMC Helix IT Service Management'.");
		innovationSuiteFeatureMethodsPage.navigateRequiredISURL(SolQAConstants.HELIXGPT_AGENT_STUDIO_URL);
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_IT_SERVICE_MANAGEMENT,SolQAConstants.AGENT_ASSISTANCE_FOR_ITSM_HELIX_GPT, provider, model);
		isDriver.quit();

		log.info("Step 1: Login to SmartIT as user 'pshah'. Expected:- pshah User should be logged in successfully");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pshahUser");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16216Data");

		this.smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		context.setAttribute("DRSQA_16216_Driver", getDriver());
		context.setAttribute("DRSQA_16216_Product", Product.Helix_Platform);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
		log.info("Logged in user: " + smartITUserData.get("UserName"));

		log.info("Step 2: Create an incident with required fields. Expected:- Incident should be created successfully");
		
		smartITFeatureMethodsPage.navigateToCreateNewIncident();
		smartITFeatureMethodsPage.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethodsPage.saveIncident();
		String incidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
		log.info("Created Incident ID: " + incidentID);

		log.info("Step 3: Click on edit (pencil icon) on the incident. Expected:- Edit mode should be enabled");
		smartITFeatureMethodsPage.clickOnEditButton();

		log.info("Step 4: select Major Incident field set to Candidate and Verify the massege on screen and assign Major incident manager click on save");
		smartITFeatureMethodsPage.clickOnDropdownAndSelectOptionFromMajorIncident(smartITTestData.get("CandidateValue"));
		Assert.assertEquals(smartITFeatureMethodsPage.getSystemAlertMessage(), smartITTestData.get("SystemAlertMessage"), "System alert message is not as expected");
		smartITFeatureMethodsPage.setSupportGroupAndPersonName(smartITTestData.get("SupportGroupName"), smartITTestData.get("PersonName"));
		smartITFeatureMethodsPage.saveIncidentVieIncidentPage();

		log.info("Step 5: Verify the Activity log after marking as Major Incident Candidate.");
		String firstActivity = smartITFeatureMethodsPage.getActivityFirstNoteText_ActivityTimeline();
		Assert.assertEquals(firstActivity, smartITTestData.get("ActivityNote"), "Activity log is not contains expected activity note after updating Major Incident Candidate.");

		if (smartITDriver != null) {
			smartITDriver.quit();
		}

		log.info("Step 6: Login to smartIT application with ppan and search the assigned incident created in step 4");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ppanUser");
		this.smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		context.setAttribute("DRSQA_16216_Driver", getDriver());
		context.setAttribute("DRSQA_16216_Product", Product.Helix_Platform);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("Logged in user: " + smartITUserData.get("UserName"));

		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		smartITFeatureMethodsPage.navigateToTicketConsole();
		smartITFeatureMethodsPage.captureScreenshot("DRSQA_16216_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethodsPage.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");
		smartITFeatureMethodsPage.applyFilterAndOpenTicket(SolQAConstants.INCIDENT, incidentID);

		log.info("Step 7: Set 'Major Incident' flag to 'Yes' and add communication activity note.");
		smartITFeatureMethodsPage.clickOnEditButton();
		smartITFeatureMethodsPage.clickOnDropdownAndSelectOptionFromMajorIncident(smartITTestData.get("MajorIncidentYesValue"));
		smartITFeatureMethodsPage.setSupportGroupAndPersonNameInCommunicationCoOrdinator(smartITTestData.get("CommunicationCoOrdinatorSupportGroupName"), smartITTestData.get("CommunicationCoOrdinatorPersonName"));
		smartITFeatureMethodsPage.saveIncidentVieIncidentPage();
		smartITFeatureMethodsPage.verifyFieldValueOnTicketViewPage(SolQAConstants.MAJOR_INCIDENT, smartITTestData.get("MajorIncidentYesValue"));

		log.info("Step 8: Change incident status to 'In Progress'.");
		smartITFeatureMethodsPage.updateIncidentStatus(SolQAConstants.IN_PROGRESS, "", "");
		smartITFeatureMethodsPage.saveIncidentVieIncidentPage();
		smartITFeatureMethodsPage.verifyTicketStatus(SolQAConstants.IN_PROGRESS);

		log.info("Step 9: Change incident status to 'Resolved'.");
		smartITFeatureMethodsPage.updateIncidentStatus(SolQAConstants.RESOLVED, SolQAConstants.FUTURE_ENHANCEMENT, smartITTestData.get("ResolvedResolutionNote"));
		smartITFeatureMethodsPage.saveIncidentVieIncidentPage();
		smartITFeatureMethodsPage.verifyTicketStatus(SolQAConstants.RESOLVED);

		log.info("Step 10 and 11:  wait for 10 sec and refresh the page and verify view report link.");
		commonWaits = new CommonWaits();
		commonWaits.waitForSometime(10000);
		smartITFeatureMethodsPage.refreshSmartITPage();

		log.info("Step 12: Click on View report link and verify the report details");
		Assert.assertEquals(smartITFeatureMethodsPage.openMajorIncidentReportAndVerifyINCDetails(), incidentID, "Incident ID is not as expected in report details page");
		String Context = smartITTestData.get("Context");
		String input = smartITTestData.get("Question");
		String expected = smartITTestData.get("ExpectedResponse");
		String output = smartITFeatureMethodsPage.getAllSummary();
		innovationSuiteFeatureMethodsPage=new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

		log.info("Step 15: Verify Activity log still contains the communication note.");
		String activityNote = smartITTestData.get("ActivityNoteAfterDownloadingReport");
		smartITFeatureMethodsPage.verifyActivityNotes(activityNote);


		log.info("Step 13: edit the report and do some operation like underline, undo, bold..etc.");
		smartITFeatureMethodsPage.performMajorIncidentReportFormattingActions();

		log.info("Step 14: Download the Major Incident Report as PDF.");
		smartITFeatureMethodsPage.verifyPrintOption();
		smartITFeatureMethodsPage.closeButtonOnReport();

		if (smartITDriver != null) {
			smartITDriver.quit();
		}

		log.info("------------END :[26.1.00] Create a Major Incident Report using Helix GPT on Incident----------");
	}

	//Marked enabled = false as this is not valid for 26.2.00 we have upgraded test case in 26.2.00
	@Test(description = "GPT-based Incident resolution notes population", enabled = false, groups = {"HelixGPT", "SmartIT"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-16101", Products = { Product.Helix_ITSM,Product.SmartIT,Product.HelixGPT })
	public void DRSQA_16101_GPT_basedIncidentResolutionNotesPopulation(ITestContext context,String provider,String model) throws Exception {

		log.info("------------START : GPT-based Incident resolution notes population----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

        log.info("--------------------------------Precondition:- Incident resolution generation skill seed data should be disabled--------------------------------");
        
		log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		context.setAttribute("DRSQA_16101_Driver", getDriver());
		context.setAttribute("DRSQA_16101_Product", Product.Helix_InnovationStudio);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_16101Data");
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("--------------------------------Precondition: Navigate to the HelixGPT Agent Studio Page--------------------------------");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_IT_SERVICE_MANAGEMENT, ISTestData.get("SkillName"), provider, model);
		isDriver.quit();

		log.info("--------------------------------Step 1: Login to SmarttIT--------------------------------");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16101Data");
		this.smartITDriver=launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
		context.setAttribute("DRSQA_16101_Driver", getDriver());
        context.setAttribute("DRSQA_16101_Product", Product.Helix_Platform);
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		log.info("Logged in user: " + smartITUserData.get("UserName"));

        log.info("--------------------------------Step2:- Click on Create New and create a new incident--------------------------------");
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.navigateToCreateNewIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16101_navigatedToCreateIncident",SolQAConstants.appSmartIT);

        smartITFeatureMethodsPage.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethodsPage.saveIncident();
        smartITFeatureMethodsPage.captureScreenshot("DRSQA_16101_IncidentCreated",SolQAConstants.appSmartIT);
        String FirstIncidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
        log.info("Created Incident ID: " + FirstIncidentID);

		log.info("--------------------------------Step3:- Edit incident and move to status Assigned to In progress and check the activity note is updated--------------------------------");
		smartITFeatureMethodsPage.updateIncidentStatus(SolQAConstants.IN_PROGRESS,"", "");
		smartITFeatureMethodsPage.saveIncident();
		smartITFeatureMethodsPage.verifyTicketStatus(SolQAConstants.IN_PROGRESS);

		log.info("--------------------------------Step4:- Edit Incident and move to resolved status and click on Ask HelicGPT button under Resolution note and verify the resolution note--------------------------------");
		log.info("**** Changing status to Resolved ********");
		smartITFeatureMethodsPage.updateIncidentStatus(SolQAConstants.RESOLVED,smartITTestData.get("ResolvedStatusReason"), "");
		smartITFeatureMethodsPage.clickOnResolutionNoteAskHelixGPT();
		smartITFeatureMethodsPage.saveIncident();
		String Context = smartITTestData.get("Context");
		String input = smartITTestData.get("Input");
		String expected = smartITTestData.get("ExpectedResponse");
		String output = smartITFeatureMethodsPage.getResolutionNote_viewPage();
        log.info("Got response from HelixGPT:-"+output);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		
		log.info("--------------------------------Step 5: Validating response from HelixGPT in Smart IT--------------------------------");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		

		log.info("--------------------------------Step6 and step7:- Closed the above incident--------------------------------");
		smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("ClosedStatus"),smartITTestData.get("ClosedStatusReason"), "");
		smartITFeatureMethodsPage.saveIncident();
		smartITFeatureMethodsPage.verifyTicketStatus(smartITTestData.get("ClosedStatus"));
		getDriver().quit();

		log.info("--------------------------------Step8:- Login to Helix Dashboard with hannah_admin user--------------------------------");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16101Data");
		this.adeDriver=launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
		context.setAttribute("DRSQA_16101_Driver", getDriver());
        context.setAttribute("DRSQA_16101_Product", Product.Helix_Platform);
        login(configProp.get("adminUserName"),configProp.get("adminPassword"),SolQAConstants.appITOM);
		log.info("Logged in user: " + configProp.get("adminUserName"));

		log.info("--------------------------------Step9:- Go to below Path Home-> Dashboards-> HelixGPT-> ITSM ->Service Collaborator Value Dashboard--------------------------------");
		portalFeatureMethods = new Portal_FeatureMethods();
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
		helixDashboardFeatureMethods.navigateTo_HelixGPT_ITSM_Service_Collaborator_Value_Dashboard();

		log.info("--------------------------------Step10:- Select Company and Support Group and verify the below Metricx select time filter last 5 min--------------------------------");
		helixDashboardFeatureMethods.selectRequiredTimePeriod(smartITTestData.get("TimePeriod"));
		log.info("-------- Verify the total incidents count is not 0--------");
		int intValue = Integer.parseInt(helixDashboardFeatureMethods.getTotalIncidentsCount());
		Assert.assertTrue(intValue > 0, "Total incidents count should be greater than zero");
		log.info("-------- Verify the MTTR value is not 0--------");
		double doubleValue = Double.parseDouble(helixDashboardFeatureMethods.getMTTRValue());
		Assert.assertTrue(doubleValue > 0, "MTTR value should be greater than zero");
		log.info("-------- Verify the Average Response Time without Service Collaborator is not 0--------");
		double averageResponseTimeWithoutServiceCollaborator = Double.parseDouble(helixDashboardFeatureMethods.getAverageResponseTimeWithoutServiceCollaborator());
		Assert.assertTrue(averageResponseTimeWithoutServiceCollaborator > 0, "Average Response Time without Service Collaborator should be greater than zero");
		log.info("-------- Verify the Average Response Time with Service Collaborator is not 0--------");
		double averageResponseTimeWithServiceCollaborator = Double.parseDouble(helixDashboardFeatureMethods.getAverageResponseTimeWithServiceCollaborator());
		Assert.assertTrue(averageResponseTimeWithServiceCollaborator > 0, "Average Response Time with Service Collaborator should be greater than zero");
		log.info("-------- Verify the Service Collaborator Skills Usage is not 0--------");
		int serviceCollaboratorSkillsUsage = Integer.parseInt(helixDashboardFeatureMethods.getServiceCollaboratorSkillsUsage());
		Assert.assertTrue(serviceCollaboratorSkillsUsage > 0, "Service Collaborator Skills Usage should be greater than zero");
		log.info("-------- Verify the MTTR Trend is present--------");
		boolean isMTTRTrendPresent = helixDashboardFeatureMethods.isMTTRTrendPresent();
		Assert.assertTrue(isMTTRTrendPresent, "MTTR Trend should be present");
		log.info("-------- Verify the Number of Total Incidents is not 0--------");
		int numberOfTotalIncidents = Integer.parseInt(helixDashboardFeatureMethods.getNumberOfTotalIncidents());
		Assert.assertTrue(numberOfTotalIncidents > 0, "Number of Total Incidents should be greater than zero");
		log.info("-------- Verify the Number of Unique Users Interacted with Service Collaborator is not 0--------");
		int numberOfUniqueUsersInteractedWithServiceCollaborator = Integer.parseInt(helixDashboardFeatureMethods.getNumberOfUniqueUsersInteractedWithServiceCollaborator());
		Assert.assertTrue(numberOfUniqueUsersInteractedWithServiceCollaborator > 0, "Number of Unique Users Interacted with Service Collaborator should be greater than zero");
		log.info("-------- Verify the SLA Breached Trend is present--------");
		boolean isSLABreachedTrendPresent = helixDashboardFeatureMethods.isSLABreachedTrendPresent();
		Assert.assertTrue(isSLABreachedTrendPresent, "SLA Breached Trend should be present");

		log.info("------------END : GPT-based Incident resolution notes population----------");

	}

	@Test(description = "Verify the Global Chat/AskHelixGPT correctly displays the worklog details for an incident", groups = {"HelixGPT", "SmartIT"}, dataProvider = "providerConfig", dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "", JiraID = "DRSQA-16236", Products = { Product.Helix_ITSM, Product.SmartIT, Product.HelixGPT })
	public void DRSQA_16236_VerifyGlobalChatWorklogDetailsForIncident(ITestContext context, String provider, String model) throws Exception {

		log.info("------------START : DRSQA-16236 Verify the Global Chat/AskHelixGPT correctly displays the worklog details for an incident----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

		log.info("Precondition: Login to IS -> Open HelixGPT Agent Studio - Application: BMC Helix IT Service Management, Skill: Agent in Global Context");
		ISTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_16236Data");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		context.setAttribute("DRSQA_16236_Driver", getDriver());
		context.setAttribute("DRSQA_16236_Product", Product.Helix_InnovationStudio);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(ISTestData.get("applicationName"), ISTestData.get("skillName"), provider, model);
		isDriver.quit();

		log.info("Step 1: Login to SmartIT Application with stagore/__USER_PASSWORD__ and verify smartIT page landed successfully");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_16236Data");
		this.smartITDriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITDriver);
		context.setAttribute("DRSQA_16236_Driver", getDriver());
		context.setAttribute("DRSQA_16236_Product", Product.Helix_Platform);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
		smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
		log.info("Logged in user: " + smartITUserData.get("UserName"));

		log.info("Step 2: Create an incident with all required fields");
		smartITFeatureMethodsPage.navigateToCreateNewIncident();
		smartITFeatureMethodsPage.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethodsPage.clickAssigntoMeInIncidentCreatePage();
		smartITFeatureMethodsPage.saveIncident();
		String incidentID = smartITFeatureMethodsPage.FetchIncidentTicketID();
		log.info("Created Incident ID: " + incidentID);

		log.info("Step 3: Edit the incident and add new note and click on Assign to me");
		smartITFeatureMethodsPage.addComment_InActivityFeed(smartITTestData.get("Note"));

		log.info("Step 4: Edit the incident and Change the status Assigned to In Progress and verify incident status should be changed");
		smartITFeatureMethodsPage.verifyTicketStatus(smartITTestData.get("InProgressStatus"));

		log.info("Step 6: Edit the incident and Change Support Group to AU Support 1 and Person=Adam Warlock");
		smartITFeatureMethodsPage.clickOnEditButton();
		smartITFeatureMethodsPage.setSupportGroupAndPersonNameForAssignee(smartITTestData.get("SupportGroupName"), smartITTestData.get("PersonName"));
		smartITFeatureMethodsPage.saveIncidentVieIncidentPage();

		log.info("Step 5: Edit the incident and Change the status In Progress to Resolved status and verify status");
		smartITFeatureMethodsPage.clickOnEditButton();
		smartITFeatureMethodsPage.updateIncidentStatus(smartITTestData.get("ResolvedStatus"), smartITTestData.get("StatusReason"), smartITTestData.get("ResolutionNote"));
		smartITFeatureMethodsPage.saveIncidentVieIncidentPage();
		smartITFeatureMethodsPage.verifyTicketStatus(smartITTestData.get("ResolvedStatus"));

		log.info("Step 7: Open Global Chat and ask questions about the incident, Ask First Question as Was this incident "+ incidentID + " assigned to someone else other than sadam?");
		smartITFeatureMethodsPage.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
		smartITFeatureMethodsPage.clickGlobalSearchButtonAfterHelixGPTSearchEnabledAndOpenInNewTab();
		smartITFeatureMethodsPage.defaultSearchToggleButton(SolQAConstants.CHAT);

		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question1Template"), incidentID));
		String Input = String.format(smartITTestData.get("Question1Template"), incidentID);
		String ExpectedResponse = smartITTestData.get("ExpectedResponse_1");
		String Context = smartITTestData.get("Context_1");
		String output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 8: Ask Second Question as is this incident ticket "+ incidentID + " moved to IN Support 3 Assigned Group?");
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question2Template"), incidentID));
		Input = String.format(smartITTestData.get("Question2Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_2"), incidentID);
		Context = String.format(smartITTestData.get("Context_2"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 9: Ask Third Question as Please provide assignment history of this incident ticket "+ incidentID + "?");
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question3Template"), incidentID));
		Input = String.format(smartITTestData.get("Question3Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_3"), incidentID);
		Context = String.format(smartITTestData.get("Context_3"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 10: who change the support group for this incident "+ incidentID + "?");
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question4Template"), incidentID));
		Input = String.format(smartITTestData.get("Question4Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_4"), incidentID);
		Context = String.format(smartITTestData.get("Context_4"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 11: Show all status changes for this incident ticket "+ incidentID + "?");
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question5Template"), incidentID));
		Input = String.format(smartITTestData.get("Question5Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_5"), incidentID);
		Context = String.format(smartITTestData.get("Context_5"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 13: Change the priority and validate that HelixGPT response includes the correct “Priority Change” worklog entries whenever the priority is updated.");
		//This step is not required as we are not changing the priority in the incident.

		log.info("Step 14: Change the flag and verify that HelixGPT correctly shows in response Need attention Flag ticket activity note details  for an incident  when the ticket is Flagged");
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question8Template"), incidentID));
		Input = String.format(smartITTestData.get("Question8Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_8"), incidentID);
		Context = String.format(smartITTestData.get("Context_8"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 15: Verify that HelixGPT correctly shows in response Need attention UnFlagged ticket activity note details  for an incident when the ticket is UnFlagged");
		smartITFeatureMethodsPage.unflagTicketWithNoteForGPT(smartITTestData.get("Note"));
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question6Template"), incidentID));
		Input = String.format(smartITTestData.get("Question6Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_6"), smartITTestData.get("Note"));
		Context = String.format(smartITTestData.get("Context_6"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 16: Summarize all the activities for this incident ticket "+ incidentID + "?");
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question9Template"), incidentID));
		Input = String.format(smartITTestData.get("Question9Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_9"), smartITTestData.get("Note"));
		Context = String.format(smartITTestData.get("Context_9"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		log.info("Step 12: Who closed this incident ticket "+ incidentID + "?");
		smartITFeatureMethodsPage.updateIncidentStatusWithSwitchingToIncidentPage(smartITTestData.get("ClosedStatus"), smartITTestData.get("StatusReason"), smartITTestData.get("ClosedResolutionNote"));
		smartITFeatureMethodsPage.sendMessageInHelixGPTChat(String.format(smartITTestData.get("Question6Template"), incidentID));
		Input = String.format(smartITTestData.get("Question6Template"), incidentID);
		ExpectedResponse = String.format(smartITTestData.get("ExpectedResponse_6"), incidentID);
		Context = String.format(smartITTestData.get("Context_6"), incidentID);
		output = smartITFeatureMethodsPage.getAllGPTResponseString();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, Input, ExpectedResponse, output);

		if (smartITDriver != null) {
			smartITDriver.quit();
		}
		log.info("------------END : DRSQA-16236 Verify the Global Chat/AskHelixGPT correctly displays the worklog details for an incident----------");
	}
	
		@AfterMethod(alwaysRun=true)
	public void closeAllDrivers() {
		WebDriver[] drivers = {isDriver, helixGPTDriver, BWFDriver, smartITDriver, dwpDriver, itsmDriver};
		String[] driverNames = {"isDriver", "helixGPTDriver", "BWFDriver", "smartITDriver", "dwpDriver", "itsmDriver"};

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