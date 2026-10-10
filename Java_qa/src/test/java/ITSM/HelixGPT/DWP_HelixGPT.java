package ITSM.HelixGPT;
    import java.io.IOException;
    import java.util.List;
	import java.util.Map;

	import org.hamcrest.Matchers;
	import org.openqa.selenium.WebDriver;
	import org.testng.Assert;
	import org.testng.ITestContext;
	import org.testng.annotations.AfterMethod;
	import org.testng.annotations.Test;

	import framework.Launcher;
	import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
    import pages.ITSM.bwf.BWFFeatureMethods;
    import pages.ITSM.dwp.DWPFeatureMethods;
	import util.CommonMethods;
	import util.Product;
	import util.MultiProviderConfig;
	import util.ReadJson;
	import util.SolQAConstants;
	import util.SolqaDetails;
	import util.SolqaLogger;


	public class DWP_HelixGPT extends Launcher {
		SolqaLogger log; 
		ReadJson readJson;
		WebDriver isDriver, dwpDriver=null, bwfDriver=null;
		InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
		DWPFeatureMethods dwpFeatureMethod;
		BWFFeatureMethods bwfFeatureMethod;
		Map<Object, String> dwpUserData, dwpTestData, ISUsersData, ISTestData, ISUsersDataBWF_User;

		public DWP_HelixGPT() {
			log = new SolqaLogger(DWP_HelixGPT.class.getName());
			readJson = new ReadJson();
		}

		@Test(description = "Employee navigator - Add additional question types to initiate requests via conversations to DWP.", enabled=false, timeOut = 1800000, groups = {"HelixGPT", "DWP"}, dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "36150", JiraID = "DRSQA-17550", Products = { Product.Helix_DWP,Product.HelixGPT })
		public void DRSQA_11383_HelixGPTInDWPAddAdditionalQuestionTypesToInitiateRequestsViaConversationsToDWP(ITestContext context,String provider,String model) throws Exception {
			log.info("************START:- HelixGPT in DWP - Add additional question types to initiate requests via conversations to DWP.************");
			log.info("Running test with Provider = " + provider + " | Model = " + model);

			log.info("Following precondition must be set step 1,2 and 3:\n" + 
						"1. Seed data for DWP Starter Prompt should be disabled\n"
					+ "2. DWP_GPT skill should be imported");

			log.info("Precondition:- Login to IS and Publish the Service");
			
			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_17550Data");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			context.setAttribute("DRSQA_17550_Driver", getDriver());
			context.setAttribute("DRSQA_17550_Product", Product.Helix_Platform);

			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkillAndAgent(SolQAConstants.BMC_HELIX_DIGITAL_WORKPLACE, ISTestData.get("skillName"), ISTestData.get("agentName"), provider, model);
			
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_17550_Driver", getDriver());
			context.setAttribute("DRSQA_17550_Product", Product.Helix_DWP);

			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_17550_Data");
			login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			dwpFeatureMethod.enableHelixGPTGlobalSearch(true, dwpTestData.get("Skill"));
			getDriver().close();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			dwpFeatureMethod = new DWPFeatureMethods();
			context.setAttribute("DRSQA_17550_Driver", getDriver());
			context.setAttribute("DRSQA_17550_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_17550_Data");

			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.verifyDWPHomePageOpened();
			log.info("Step2:- Raise the service request which we imported.");
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("ServiceName"));

				log.info("Step3:- Ans all the questions helixgpt should show a summary of the request with user provided answers and ask user for confirmation to submit..");
				String previousResponse = "";
		        int sameResponseCount = 0;
		
		while (true) {

			String response = dwpFeatureMethod
					.getAllGPTResponseString()
					.toLowerCase();
		
			boolean hasMsg1 = response.contains(dwpTestData.get("SummaryConfirmationMessage").toLowerCase());
			boolean hasMsg2 = response.contains(dwpTestData.get("SummaryConfirmationMessage2").toLowerCase());
			boolean hasMsg3 = response.contains(dwpTestData.get("SummaryConfirmationMessage3").toLowerCase());
			boolean hasMsg4 = response.contains(dwpTestData.get("SummaryConfirmationMessage4").toLowerCase());
		
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
			boolean containsAnyTestDataKey = dwpTestData.keySet().stream()
					.map(key -> String.valueOf(key).toLowerCase())
					.anyMatch(response::contains);
		
			if (!containsAnyTestDataKey) {
				Assert.fail("None of the test data keys found in GPT response: " + response);
				dwpDriver.quit();
				break;
			}
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.serviceRequestQuestion(response, dwpTestData);
		}
        log.info("----step7:- Thank you for providing the details. Here's a summary of your time off request----");
        Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("SummaryField1")), "Chatbot does not respond correctly");
        Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("SummaryField2")), "Chatbot does not respond correctly");
        Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("SummaryField3")), "Chatbot does not respond correctly");
        innovationSuiteFeatureMethodsPage.sendMessage(dwpTestData.get("submit"));
		Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyRequestOpenedInDWP(), "Request ID not opened in dwp");
		getDriver().quit();
		dwpDriver.quit();
			
			log.info("************END:- HelixGPT in DWP - Add additional question types to initiate requests via conversations to DWP.************");
		}

		@Test(description = "HelixGPT/DSI - Searchbar/Results Leftovers.",enabled=false, dataProvider = "providerConfig", groups = {"HelixGPT", "DWP","gsutar"},dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "20682", JiraID = "DRSQA-11401", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_11401_HelixGPTSearchServices(ITestContext context,String provider,String model) throws Exception {
			log.info("---------- START-------HelixGPT/DSI - Searchbar/Results Leftovers.----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);
			

			log.info("-------Step1:- Login to the IS------");
			context.setAttribute("DRSQA_11401_Driver", getDriver());
			context.setAttribute("DRSQA_11401_Product", Product.Helix_Platform);

			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11401Data");

			log.info("Following precondition must be set:\n"
					+ "1. "+ISTestData.get("skillName")+" should be imported successfully\n"
					+ "2. GPT page should be imported successfully");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkillAndAgent(SolQAConstants.BMC_HELIX_DIGITAL_WORKPLACE, ISTestData.get("skillName"), ISTestData.get("agentName"), provider, model);
			isDriver.quit();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11401_Driver", getDriver());
			context.setAttribute("DRSQA_11401_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11401Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.verifyDWPHomePageOpened();

		    log.info("--------- Step 3: Onboarding word related all result will be display. ---------");
			dwpFeatureMethod.openGPTPage();
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("Input"));

			log.info("-------: Verify chat is also showing results related to keyward  ------------");
			InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			String Context = dwpTestData.get("Context");
			String input = dwpTestData.get("Input");
			String expected = dwpTestData.get("ExpectedResponse");
			String output = dwpFeatureMethod.getAllGPTResponseString();
			log.info("Got response from HelixGPT:-" + dwpFeatureMethod.getAllGPTResponseString());
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
			dwpDriver.quit();

			log.info("---------- END-------HelixGPT/DSI - Searchbar/Results Leftovers----------");

		}

		@Test(description = "HelixGPT in DWP - Add screenshot with error message and find knowledge, raise request or livechat.", enabled=false, groups = {"HelixGPT", "DWP","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "34147", JiraID = "DRSQA-11407", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_11407_HelixGPTAddScreenshotWithErrorMessage(ITestContext context,String provider,String model) throws Exception {
			log.info("---------- START-------HelixGPT in DWP - Add screenshot with error message and find knowledge, raise request or livechat.----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);
			
			log.info("-------Step1:- Login to the IS------");
			context.setAttribute("DRSQA_11407_Driver", getDriver());
			context.setAttribute("DRSQA_11407_Product", Product.Helix_Platform);
 
			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11407Data");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
			String Skill_NAME = ISTestData.get("skillName");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(ISTestData.get("applicationName"), Skill_NAME, provider, model);
			isDriver.quit();

			log.info("Precondition:- Login to DWP admin and enable employee navigator functionality");
			dwpDriver = launchWeb(SolQAConstants.DWP_ADMIN, SolQAConstants.DWP_ADMIN);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11407_Driver", getDriver());
			context.setAttribute("DRSQA_11407_Product", Product.Helix_DWP);

			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11407Data");
			login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			dwpFeatureMethod.verifyDWPAdminPageLaunchedSuccessfully();
			String configuration = dwpTestData.get("configuration");
			dwpFeatureMethod.clickOnHamburgerMenu();
			dwpFeatureMethod.clickOnMenu(configuration);
			dwpFeatureMethod.clickOnSubMenu(SolQAConstants.SEARCH);
			dwpFeatureMethod.stateOfAIChat_CorePages(true, dwpTestData.get("agentDropdownValues"));
			dwpDriver.quit();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11407_Driver", getDriver());
			context.setAttribute("DRSQA_11407_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11407Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.verifyDWPHomePageOpened();
			log.info("-----------Step 2: click on Helix GPT(global search) and start convesation Type \"Hi\"-------------");
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("message1"));

			log.info("--------- Step 3: Check in GPT input field there is an Attachment Icon (Clip) present to upload an image ---------");
			dwpFeatureMethod.isAttachmentIconPresentOnHelixGPT();

			log.info("------- Step 4: Attach an file which is not supported ------------");
			dwpFeatureMethod.attachFileInHelixGPT(dwpTestData.get("notSupportedFile"));
			dwpFeatureMethod.verifyErrorMessageForNotSupportedFilesOnGPT(dwpTestData.get("errorMessageForNotSupported"));

			log.info("--------- Step 5: Attach an image which is supported and Wait for GPT to analyse the Image.--------");
			dwpFeatureMethod.attachFileInHelixGPT(dwpTestData.get("supportedFile"));
			dwpFeatureMethod.sendMessageInGPTChat(dwpTestData.get("Input"));

			log.info("--------- Step 6: Wait for GPT to analyse the Image. ---------");
			 innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		    String Context = dwpTestData.get("Context");
		    String input = dwpTestData.get("Input");
		    String expected = dwpTestData.get("ExpectedResponse");
		    String output = dwpFeatureMethod.getAllGPTResponseString();
            log.info("Got response from HelixGPT:-"+output);
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
			dwpDriver.quit();

			log.info("---------- END-------HelixGPT in DWP - Add screenshot with error message and find knowledge, raise request or livechat.----------");

		}

		@Test(description = "HelixGPT - Allow users to query service health items.", enabled=false, groups = {"HelixGPT", "DWP","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "34148", JiraID = "DRSQA-11408", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_11408_HelixGPTAllowUsersToQueryServiceHealthItems(ITestContext context,String provider,String model) throws Exception {
			log.info("---------- START-------HelixGPT - Allow users to query service health items.----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);

			
			log.info("-------Step1:- Login to the IS------");
			context.setAttribute("DRSQA_11408_Driver", getDriver());
			context.setAttribute("DRSQA_11408_Product", Product.Helix_Platform);

			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11408Data");
             log.info("Following precondition must be set:\n"
					+ "1. " + ISTestData.get("skillName") + " should be imported successfully\n"
					+ "2. Medical InsurancefuicL, Microsoft outlook services should be available in DWP admin Services");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
			String SKILL_NAME = ISTestData.get("skillName");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(ISTestData.get("applicationName"), SKILL_NAME, provider, model);
			isDriver.quit();

			log.info("Precondition:- Login to DWP admin and enable employee navigator functionality");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11408_Driver", getDriver());
			context.setAttribute("DRSQA_11408_Product", Product.Helix_DWP);

			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11408Data");
			login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			String configuration = dwpTestData.get("configuration");
			String Chat = dwpTestData.get("Chat");
			dwpFeatureMethod.navigateToAdminPage();
			dwpFeatureMethod.clickOnHamburgerMenu();
			dwpFeatureMethod.clickOnMenu(configuration);
			dwpFeatureMethod.clickOnSubMenu(Chat);
			dwpFeatureMethod.selectValueInDropDownByLabel(dwpTestData.get("technologyDropdown"),dwpTestData.get("techDropdownValues"));
			dwpFeatureMethod.selectSkillValueFromDropDown(dwpTestData.get("skillDropdownValues"));
			dwpFeatureMethod.saveHelixChatbotConfigurationForm(true);
			dwpFeatureMethod.clickOnCloseButtonOnDWPChatbotConfigurationPage();
			dwpDriver.close();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11408_Driver", getDriver());
			context.setAttribute("DRSQA_11408_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "jhardy");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11408Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
			dwpFeatureMethod.verifyDWPHomePageOpened();

			log.info("-----------Step 2: click on Helix GPT(global search) and send message to check health items-------------");
			dwpFeatureMethod.clickOnHelixGPTSearchIcon();
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("message"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		    String Context = dwpTestData.get("Context");
		    String input = dwpTestData.get("message");
		    String expected = dwpTestData.get("ExpectedResponse");
		    String output = dwpFeatureMethod.getAllGPTResponseString();
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

			log.info("------- Step 3: Click on Source i.e reference link and verify -----------");
			List<String> serviceHealthItems = dwpFeatureMethod.getSourceLinksOnSearchedResult();
			dwpFeatureMethod.allureStep("Verify if service health items are present", serviceHealthItems.size() > 0, Matchers.equalTo(true));
			Assert.assertTrue(serviceHealthItems.get(0).equalsIgnoreCase(dwpTestData.get("source1")),"Expected source i.e reference link is not responded by HelixGPT - Expected -"+ dwpTestData.get("source1")+" actual - "+ serviceHealthItems.get(0));
			dwpFeatureMethod.clickOnArticleLink(serviceHealthItems.get(0));
			dwpFeatureMethod.waitForArticleToLoad();
			Assert.assertEquals(dwpFeatureMethod.gettingOpenedArticleText(),dwpTestData.get("source1"),"Opened service health item is not as expected");
			dwpFeatureMethod.clickOnBackButton();
			dwpFeatureMethod.clearHelixGPTSearch();

			log.info("--------- Step 4: Send message to check outlook ---------");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("message2"));
		   	Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("response2")), "HelixGPT response does not contain expected string- "+ dwpTestData.get("response2") + "actual - "+dwpFeatureMethod.getAllGPTResponseString());

			log.info("------- Step 5: Click on Source i.e reference link and verify -----------");
			List<String> HealthItems = dwpFeatureMethod.getSourceLinksOnSearchedResult();
			dwpFeatureMethod.allureStep("Verify if service health items are present", HealthItems.size() > 0, Matchers.equalTo(true));
			Assert.assertTrue(HealthItems.get(0).equalsIgnoreCase(dwpTestData.get("source2")),"Expected source i.e reference link is not responded by HelixGPT - Expected -"+ dwpTestData.get("source2")+" actual - "+ HealthItems.get(0));
			dwpFeatureMethod.clickOnArticleLink(HealthItems.get(0));
			dwpFeatureMethod.waitForArticleToLoad();
			Assert.assertEquals(dwpFeatureMethod.gettingOpenedArticleText(),dwpTestData.get("source2"),"Opened service health item is not as expected");
			dwpFeatureMethod.clickOnBackButton();

			 dwpDriver.quit();

			log.info("---------- END-------DRSQA_11408_HelixGPT_Allow_Users_To_Query_Service_health_items----------");

		}

		@Test(description = "HelixGPT - Ability to query people attributes.", enabled=false, groups = {"HelixGPT", "DWP","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "34148", JiraID = "DRSQA-11409", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_11409_HelixGPTAbilityToQueryPeopleAttributes(ITestContext context,String provider,String model) throws Exception {
			log.info("---------- START-------HelixGPT - Ability to query people attributes.----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);

			log.info("-------Step1:- Login to the IS------");
			context.setAttribute("DRSQA_11409_Driver", getDriver());
			context.setAttribute("DRSQA_11409_Product", Product.Helix_Platform);

			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11409Data");

			log.info("Following precondition must be set:\n"
					+ "1. " + ISTestData.get("skillName") + " should be imported successfully\n"
					+ "2. ");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
			String SKILL_NAME = ISTestData.get("skillName");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(ISTestData.get("applicationName"), SKILL_NAME, provider, model);
			isDriver.quit();

			log.info("Precondition:- Login to DWP admin and enable employee navigator functionality");
			dwpDriver = launchWeb(SolQAConstants.DWP_ADMIN, SolQAConstants.DWP_ADMIN);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11409_Driver", dwpDriver);
			context.setAttribute("DRSQA_11409_Product", Product.Helix_DWP);

			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11409Data");
			login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			String configuration = dwpTestData.get("configuration");
			String Chat = dwpTestData.get("Chat");
			dwpFeatureMethod.verifyDWPAdminPageLaunchedSuccessfully();
			dwpFeatureMethod.clickOnHamburgerMenu();
			dwpFeatureMethod.clickOnMenu(configuration);
			dwpFeatureMethod.clickOnSubMenu(Chat);
			dwpFeatureMethod.selectValueInDropDownByLabel(dwpTestData.get("technologyDropdown"),dwpTestData.get("techDropdownValues"));
			dwpFeatureMethod.selectSkillValueFromDropDown(dwpTestData.get("skillDropdownValues"));
			dwpFeatureMethod.saveHelixChatbotConfigurationForm(true);
			dwpFeatureMethod.clickOnCloseButtonOnDWPChatbotConfigurationPage();
			dwpFeatureMethod.clickOnSubMenu(SolQAConstants.SEARCH);
			dwpFeatureMethod.stateOfAIChat_CorePages(true, dwpTestData.get("skillDropdownValues"));
			dwpDriver.close();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11409_Driver", getDriver());
			context.setAttribute("DRSQA_11409_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "liamUser");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11409Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.verifyDWPHomePageOpened();
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("question3"));
			log.info("--------- Step 3: Navigate to Chat option ---------");
			dwpFeatureMethod.navigateToChatOption();

			log.info("------- Step 4: Send message and verify Response for "+dwpTestData.get("question1")+"  ------------");
			 innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		    String Context = dwpTestData.get("Context1");
		    String input = dwpTestData.get("question1");
		    String expected = dwpTestData.get("ExpectedResponse1");
		    String output = dwpFeatureMethod.getAllGPTResponseString();
		    innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

			log.info("------- Step : Send message and verify Response for "+dwpTestData.get("question2")+"  ------------");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("question2"));
			String Context2 = dwpTestData.get("Context2");
			String input2 = dwpTestData.get("question2");
			String expected2 = dwpTestData.get("ExpectedResponse2");
			String output2 = dwpFeatureMethod.getAllGPTResponseString();
			innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context2, input2, expected2, output2);

			log.info("------- Step : Send message and verify Response for "+dwpTestData.get("question3")+"  ------------");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("question3"));
			String Context3 = dwpTestData.get("Context3");
			String input3 = dwpTestData.get("question3");
			String expected3 = dwpTestData.get("ExpectedResponse3");
			String output3 = dwpFeatureMethod.getAllGPTResponseString();
			innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context3, input3, expected3, output3);

			dwpDriver.quit();

			log.info("---------- END-------HelixGPT - Ability to query people attributes.----------");

		}

		@Test(description = "HelixGPT in DWP - Generate answers from Requests and Approvals.", groups = {"HelixGPT", "DWP","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "20685", JiraID = "DRSQA-11771", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_11771_HelixGPTInDWPGenerateAnswersFromRequestsAndApprovals(ITestContext context,String provider,String model) throws Exception {
			log.info("---------- START-------HelixGPT in DWP - Generate answers from Requests and Approvals.----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);
			

			log.info("-------Precondition:- Login to the IS------");
			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11771Data");
            log.info("Following precondition must be set:\n"
					+ "1. " + ISTestData.get("skillName") + " should be imported successfully\n"
					+ "2. GPT page should be already imported in DWP");
			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			context.setAttribute("DRSQA_11771_Driver", getDriver());
			context.setAttribute("DRSQA_11771_Product", Product.Helix_Platform);

			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
			String SKILL_NAME = ISTestData.get("skillName");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_DIGITAL_WORKPLACE, SKILL_NAME, provider, model);
			isDriver.quit();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11771_Driver", getDriver());
			context.setAttribute("DRSQA_11771_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11771Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
			dwpFeatureMethod.verifyDWPHomePageOpened();

			log.info("-----------Step 2 : Ask HelixGPT in DWP the following questions from test data-------------");
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("SearchText"));
			dwpFeatureMethod.navigateToChatOption();

			log.info("-----------Step3: Check whether the output is consistent in terms of formatting, that is it should be in a table format with fields like Request ID, Title, Status, Requested by.-------------");
            String response = dwpFeatureMethod.getTheLatestChatTableResponse();

             Assert.assertTrue(response.contains(dwpTestData.get("RequestID")),"The latest chat table response does not contain Request ID.");
             Assert.assertTrue(response.contains(dwpTestData.get("Title")),"The latest chat table response does not contain Title");
             Assert.assertTrue(response.contains(dwpTestData.get(SolQAConstants.STATUS)),"The latest chat table response does not contain Status");
             Assert.assertTrue(response.contains(dwpTestData.get("RequestedBy")),"The latest chat table response does not contain Requested By");

			log.info("-----------Step4:- Now login to DWP with approver.-------------");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11771_Driver", getDriver());
			context.setAttribute("DRSQA_11771_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "friedaUser");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11771Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
			dwpFeatureMethod.verifyDWPHomePageOpened();

			log.info("-----------Step5:- Ask approver following questions.-------------");
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("SearchText_2"));
			dwpFeatureMethod.navigateToChatOption();

			 String response3 = dwpFeatureMethod.getTheLatestChatTableResponse();

             Assert.assertTrue(response.contains(dwpTestData.get("RequestID")),"The latest chat table response does not contain Request ID");
             Assert.assertTrue(response.contains(dwpTestData.get("Title")),"The latest chat table response does not contain Title");
			 String firstRequestID = dwpFeatureMethod.getFirstRequestID();
			 log.info("First Request ID: " + firstRequestID);
			 dwpFeatureMethod.sendMessageInGPTChat(dwpTestData.get("SecondQuestion"));
			 String input2 = dwpTestData.get("SecondQuestion");
			 String context2 = dwpTestData.get("Context2");
			 String expected2 = dwpTestData.get("ExpectedResponse2");
			 String output2 = dwpFeatureMethod.getAllGPTResponseString();
			 innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			 innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context2, input2, expected2, output2);
			
		
			 log.info("-----------Step6:- Now reassign to request to to other user. -------------");
			 dwpFeatureMethod.enterNewQuestionInSearch("approve " + firstRequestID);
			
			 log.info("-----------Step7:- verify response askingcould you please provide a justification for approving this request.. -------------");
			Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ReassignText_1")) 
			|| dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ReassignText_2").toLowerCase()) 
			|| dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ReassignText_3").toLowerCase())
			|| dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ReassignText_4").toLowerCase()),
					"The latest chat table response does not contain Justification Text. expected "+dwpTestData.get("ReassignText_1")+" or "+dwpTestData.get("ReassignText_2")+" or "+dwpTestData.get("ReassignText_3") + " but found "+dwpFeatureMethod.getAllGPTResponseString());
 
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("ApproveText"));

			if(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("JustificationText"))){
				Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("JustificationText")) ,
						"The latest chat table response does not contain Approve Text. expected "+dwpTestData.get("JustificationText")+" but found "+dwpFeatureMethod.getAllGPTResponseString());
						dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("JustificationAnswer"));
						}

			Assert.assertTrue(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ApproveSuccessText")) || dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ApproveSuccessText_1").toLowerCase())|| dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ApproveSuccessText_2").toLowerCase()),
					"The latest chat table response does not contain Approve success Text. expected "+dwpTestData.get("ApproveSuccessText")+" or "+dwpTestData.get("ApproveSuccessText_1")+" but found "+dwpFeatureMethod.getAllGPTResponseString());

			log.info("---------- END-------HelixGPT in DWP - Generate answers from Requests and Approvals.----------");

		}

		@Test(description = "HelixGPT-Prompts in DWP - Propose KA or generic request (Livechat postponed to Phase 2),", enabled=false, groups = {"HelixGPT", "DWP","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "20687", JiraID = "DRSQA-11773", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_11773_HelixGPTPromptsInDWPProposeKAorGenericRequest(ITestContext context,String provider,String model) throws Exception {
			log.info("---------- START-------HelixGPT-Prompts in DWP - Propose KA or generic request (Livechat postponed to Phase 2),----------");
			log.info("Following precondition must be set:\n"
					+ "1. GPTDemo service should should be imported successfully\n"
					+ "2. GPT page should be already imported in DWP");

			log.info("-------Step1:- Login to the IS------");
			context.setAttribute("DRSQA_11773_Driver", getDriver());
			context.setAttribute("DRSQA_11773_Product", Product.Helix_Platform);

			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11773Data");

			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_DIGITAL_WORKPLACE, ISTestData.get("skillName"), provider, model);

			String[] prompts = { ISTestData.get("DWPRouterPrompt"), ISTestData.get("KnowledgeCitationsUniPrompt"),
					ISTestData.get("DWPEventSummarizer"), ISTestData.get("DWPEventRetriever"), ISTestData.get("DWPFallbackPrompt") };

			for (String promptKey : prompts) {
				if (!innovationSuiteFeatureMethodsPage.isPromptPresent(promptKey)) {
					innovationSuiteFeatureMethodsPage.linkNewPrompt(promptKey);
				}
			}
			innovationSuiteFeatureMethodsPage.selectSkillPrompt(ISTestData.get("DWPRouterPrompt"));

			log.info("Step:- Login to IS and Publish the Service");
			context.setAttribute("DRSQA_11773_Driver", getDriver());
			context.setAttribute("DRSQA_11773_Product", Product.Helix_Platform);

			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11773Data");

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11773_Driver", getDriver());
			context.setAttribute("DRSQA_11773_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_11773Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
			dwpFeatureMethod.verifyDWPHomePageOpened();

			log.info("-----------Step 2: Ask HelixGPT in DWP the following questions from test data-------------");
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("SearchText"));
			dwpFeatureMethod.navigateToChatOption();

				log.info("Step3:- Ans all the questions helixgpt should show a summary of the request with user provided answers and ask user for confirmation to submit..");
				while (!dwpFeatureMethod.getAllGPTResponseString().contains("Request ID")) {
					String response = dwpFeatureMethod.getAllGPTResponseString().toLowerCase();

					// Check if response contains any key from the JSON test data
					boolean containsAnyTestDataKey = dwpTestData.keySet().stream()
							.map(key -> String.valueOf(key).toLowerCase())
							.anyMatch(response::contains);

					if (!containsAnyTestDataKey) {
						Assert.fail("None of the test data keys found in GPT response: " + response);
						getDriver().close();
						break;
					}

					dwpFeatureMethod.serviceRequestQuestion(response, dwpTestData);
				}

				Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyRequestOpenedInDWP(), "Request ID not opened in dwp");
				getDriver().close();
			
				log.info("---------- END-------HelixGPT-Prompts in DWP - Propose KA or generic request (Livechat postponed to Phase 2),----------");
		
			}

		@Test(description = "HelixGPT in DWP - BWF, RKM, HKM article searching.", groups = {"HelixGPT", "DWP","gsutar"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
		@SolqaDetails(authorName = "gsutar", TTID = "38571", JiraID = "DRSQA-12256", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_12256_HelixGPTInDWPBWFArticleSearching(ITestContext context,String provider,String model) throws Exception {
			
			log.info("---------- START-------HelixGPT in DWP - BWF, RKM, HKM article searching.----------");
			log.info("Running test with Provider = " + provider + " | Model = " + model);
			log.info("-------Precondition:- Login to the IS------");
			ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11771Data");
            log.info("Following precondition must be set:\n"
					+ "1. " + ISTestData.get("skillName") + " should be imported successfully\n"
					+ "2. GPT page should be already imported in DWP");
			log.info("Logging in to Innovation Suite");
			this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
			setDriver(isDriver);
			context.setAttribute("DRSQA_12256_Driver", getDriver());
			context.setAttribute("DRSQA_12256_Product", Product.Helix_Platform);

			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
			log.info("Logged in user: " + configProp.get("adminUserName"));
			innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

			log.info("Precondition: Navigate to the HelixGPT Agent Studio Page");
			String SKILL_NAME = ISTestData.get("skillName");
			innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
			innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
			innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_DIGITAL_WORKPLACE, SKILL_NAME, provider, model);
			isDriver.quit();

			log.info("Step1:- Login to DWP with end user.");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_12256_Driver", getDriver());
			context.setAttribute("DRSQA_12256_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_12256Data");
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
			dwpFeatureMethod.verifyDWPHomePageOpened();

			log.info("-----------Step 2: Show my in progress requests ask this question to gpt.-------------");
			log.info("Step2.1:This step is already covered on DRSQA_11771-------------");

			log.info("------Step3:- Search BWF article ---------");
			dwpFeatureMethod.performSearchWOWaitingResults(dwpTestData.get("BWFArticle"));
			dwpFeatureMethod.navigateToChatOption();
            String response1 = dwpFeatureMethod.getTheLatestChatTableResponse();
			String input1 = dwpTestData.get("BWFArticle");
			String context1 = dwpTestData.get("Context");
			String expected1 = dwpTestData.get("ExpectedResponse");
			innovationSuiteFeatureMethodsPage=new InnovationSuiteFeatureMethodsPage();
			innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context1, input1, expected1, response1);
			innovationSuiteFeatureMethodsPage.openGPTArticle(input1);
			Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.BMC_HELIX_BUSINESS_WORKFLOWS), SolQAConstants.BMC_HELIX_BUSINESS_WORKFLOWS+" Provider name not found in URL");

			log.info("------Step4:- Search RKM article ---------");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("RKMArticle"));
			String response2 = dwpFeatureMethod.getTheLatestChatTableResponse();
			String input2 = dwpTestData.get("RKMArticle");
			String context2 = dwpTestData.get("Context_2");
			String expected2 = dwpTestData.get("ExpectedResponse_2");
			innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context2, input2, expected2, response2);
			innovationSuiteFeatureMethodsPage.openGPTArticle(input2);
	        Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.OfficePrinterArticleURLID), SolQAConstants.OfficePrinterArticleURLID+" Provider name not found in URL");


			log.info("------Step5:- Search HKM article ---------");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("HKMArticle"));
			String response3 = dwpFeatureMethod.getTheLatestChatTableResponse();
			String input3 = dwpTestData.get("HKMArticle");
			String context3 = dwpTestData.get("Context_3");
			String expected3 = dwpTestData.get("ExpectedResponse_3");
			innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context3, input3, expected3, response3);
			innovationSuiteFeatureMethodsPage.openGPTArticle(input3);
	        Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyProviderNameInURL(SolQAConstants.COMAROUND), SolQAConstants.COMAROUND+" Provider name not found in URL");


			log.info("---------Step6:- Now ask for a article which agent does not have access -----");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("ArticleNotFound"));
			innovationSuiteFeatureMethodsPage.verifyArticleIsNotPresent(dwpTestData.get("ArticleNotFound"));

			log.info("---------Step7:- Ask External world question to gpt -----");
			dwpFeatureMethod.enterNewQuestionInSearch(dwpTestData.get("ExternalWorldQuestion"));
			Assert.assertFalse(dwpFeatureMethod.getAllGPTResponseString().contains(dwpTestData.get("ExternalWorldResponse")), "The latest chat table response does not contain External World Response");
		
			log.info("---------- END-------HelixGPT in DWP - BWF, RKM, HKM article searching.----------");
		}

		@Test(description = "HelixGPT disclaimer text control,",enabled = false,groups = {"HelixGPT", "DWP"})
		@SolqaDetails(authorName = "gsutar", TTID = "38578", JiraID = "DRSQA-12263", Products = { Product.HelixGPT, Product.Helix_DWP })
		public void DRSQA_12263_HelixGPTDisclaimerTextControl(ITestContext context) throws Exception {
			log.info("---------- START-------HelixGPT disclaimer text control,----------");
			log.info("Following precondition must be set:\n"
					+ "");
					
			log.info("Step1:- Login into DWP as an DWP Admin user");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_12263_Driver", getDriver());
			context.setAttribute("DRSQA_12263_Product", Product.Helix_DWP);

			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_12263Data");
			login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			String AI_DISCLAIMER = dwpTestData.get("AI_DISCLAIMER");
			dwpFeatureMethod.navigateToAdminPage();
			dwpFeatureMethod.clickOnHamburgerMenu();
			dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);

			log.info("Step2:- Go to Configuration > then go to sub-menu 'AI Disclaimer' present under Configuration");
			dwpFeatureMethod.clickOnSubMenu(AI_DISCLAIMER);
			
			log.info("Step3:- Check to see the radio button 'AI disclaimer' exist in that page. If it does, then click on it and enable the feature.");
			dwpFeatureMethod.setAIDisclaimerToggle(true);

			log.info("Step4:- Select the 'Custom' type and hit save. And made some changes.And refresh the page and Now open the 'Global Panel/Employee Navigator' and make sure it shows the short message at the bottom of the chat");
			dwpFeatureMethod.fillRadioButtonByIndex(SolQAConstants.CUSTOM,"1");
			dwpFeatureMethod.editCustomMessageInAIDisclaimer(dwpTestData.get("messageType"),dwpTestData.get("messageText"));
			dwpFeatureMethod.saveAIDisclaimerChanges();
			dwpFeatureMethod.refreshBrowser();
			dwpFeatureMethod.clickOnEmployeeNavigatorIcon();
			Assert.assertTrue(dwpFeatureMethod.getChatDisclaimerMsg().contains(dwpTestData.get("shortMessageText")), "Employee navigator search is not contains expected short message");
			dwpDriver.quit();

			log.info("Step5:- Now Login to DWP with end user and check it shows the short message at the bottom of the chat");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_12263_Driver", getDriver());
			context.setAttribute("DRSQA_12263_Product", Product.Helix_DWP);
			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData= readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP,"DWPTestData","DRSQA_12263Data");

			log.info("Login to DWP with " +dwpUserData.get("UserName")+ " and verify its logged in successfully");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"),SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			dwpFeatureMethod.verifyDWPHomePageOpened();
			dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
			String viewName=dwpTestData.get("viewName");
			dwpFeatureMethod.navigateToDWPNavigationTab(viewName);
			dwpFeatureMethod.navigateToChatOption();
			Assert.assertTrue(dwpFeatureMethod.getChatDisclaimerMsg().contains(dwpTestData.get("messageText")), "dwp gpt end user not contains expected long message");
			dwpDriver.quit();


			log.info("---------- END-------HelixGPT disclaimer text control,----------");
		}
		
		@Test(description = "HKM Dynamic tag filtering", groups = {"Regression_P1", "SmartIT"})
		@SolqaDetails(authorName = "mjamadad", JiraID = "DRSQA-11741", Products = {Product.Helix_ITSM, Product.SmartIT, Product.Helix_DWP})
		public void DRSQA_11741_HKM_Dynamic_tag_filtering(ITestContext context) throws IOException {

			log.info("-----START : HKM Dynamic tag filtering ------");
			log.info("Precondition:- Login to DWP admin and enable employee navigator functionality");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_11741_Driver", getDriver());
			context.setAttribute("DRSQA_11741_Product", Product.Helix_DWP);

			dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData",
					"DRSQA_11741Data");
			login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
			dwpFeatureMethod = new DWPFeatureMethods();
			log.info("Step -1 : succefully logged in to DWP");
			log.info("Step -2 : Validating the responce of Chat");
			dwpFeatureMethod.validateTheResponse(dwpTestData.get("input"));
		log.info("Step 3: Validated all the responce by the chat");
		dwpDriver.quit();
		log.info("-----END : HKM Dynamic tag filtering ------");
		log.info("---------- END-------HelixGPT disclaimer text control,----------");

	}

	@Test(description = "Support BWF cognitive features using HelixGPT", groups = { "HelixGPT", "DWP", "BWF" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15127", Products = { Product.Helix_DWP, Product.HelixGPT })
	public void DRSQA_15127_Support_BWF_Cognitive_Features_Using_HelixGPT(ITestContext context) throws Exception {
		log.info("************START:- Support BWF cognitive features using HelixGPT************");

		try {
			log.info("Step 1: Read user credentials from ISUsers.json");
			ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers", "Fritz");
			ISUsersDataBWF_User = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISUsers",
					"Elizabeth");

			log.info("Step 2: Read test data from DWPTestData.json");
			dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData",
					"DRSQA_15127_Data");

			log.info("Step 3: Launch DWP application");
			dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
			setDriver(dwpDriver);
			context.setAttribute("DRSQA_15127_Driver", getDriver());
			context.setAttribute("DRSQA_15127_Product", Product.Helix_DWP);

			log.info("Step 4: Login to DWP with user: "+ISUsersData.get("UserName"));
			login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appDWP);
			log.info("Logged in user: " + ISUsersData.get("UserName"));

			dwpFeatureMethod = new DWPFeatureMethods();

			log.info("Step 5: Verify DWP home page opened successfully");
			dwpFeatureMethod.verifyDWPHomePageOpened();
			log.info("DWP home page opened successfully - Login verified");

			log.info("Step 6: Search and open Paid Time Off Request service");
			dwpFeatureMethod.searchAndOpenService(dwpTestData.get("ServiceName"));
			log.info("Paid Time Off Request service opened successfully");

			log.info("Step 7: Click on Request Now button");
			dwpFeatureMethod.clickOnRequestNow(dwpTestData.get("ServiceName"));
			log.info("Request Now button clicked");

			log.info("Step 8: Generate random string for mandatory parameter");
			CommonMethods commonMethods = new CommonMethods();
			String randomRequestSummary = "Automation Test - Paid Time Off - " + commonMethods.randomString(8);
			log.info("Generated random string: " + randomRequestSummary);

			log.info("Step 9: Fill mandatory field, submit request and verify 'Request submitted' message");
			dwpFeatureMethod.fillServiceRequest("Summary of your request", randomRequestSummary);
			log.info("Request submitted successfully with summary: " + randomRequestSummary);

			log.info(
					"Step 10: Navigate to Activity page, validate 'Request Time Off' tile, click Details, verify summary and fetch Fulfillment ID");
			String fulfillmentId = dwpFeatureMethod.validateRequestTileAndClickDetails(dwpTestData.get("requestTileName"),
					randomRequestSummary);
			log.info("Successfully validated 'Request Time Off' tile, clicked Details link and verified summary: "
					+ randomRequestSummary);
			log.info("Fetched Fulfillment ID: " + fulfillmentId);
			Assert.assertNotNull(fulfillmentId, "Fulfillment ID should not be null");
			Assert.assertFalse(fulfillmentId.isEmpty(), "Fulfillment ID should not be empty");

			log.info("Step 11: Logout from user: " + ISUsersData.get("UserName"));
			dwpFeatureMethod.closeBrowser();
			log.info("Logged out from Fritz user successfully");

			log.info("Step 12: Login to BWF with the Agent as:" + ISUsersDataBWF_User.get("UserName"));
			bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
			setDriver(bwfDriver);
			login(ISUsersDataBWF_User.get("UserName"), ISUsersDataBWF_User.get("Password"), SolQAConstants.appBWF);
			log.info("Logged in as Elizabeth user: " + ISUsersDataBWF_User.get("UserName"));

			bwfFeatureMethod = new BWFFeatureMethods();

			log.info("Step 13: Navigate to Cases page and search and open for Fulfillment ID: " + fulfillmentId);
			bwfFeatureMethod.searchAndOpenCase(fulfillmentId);
			log.info("Successfully searched for case: " + fulfillmentId);

			bwfFeatureMethod.validateCaseField(dwpTestData.get("CaseTemplateFieldName"),
					dwpTestData.get("expectedCaseTemplate"));
			log.info("Successfully validated Case Template = Paid Time Off Request");

			log.info("Step 16: Validate Category Tier 1 = Total Rewards");
			bwfFeatureMethod.validateCaseField(dwpTestData.get("CaseCategoryFieldName1"),
					dwpTestData.get("expectedCategoryValue1"));
			log.info("Successfully validated Category Tier 1 = Total Rewards");

			log.info("Step 17: Validate Category Tier 2 = Leave");
			bwfFeatureMethod.validateCaseField(dwpTestData.get("CaseCategoryFieldName2"),
					dwpTestData.get("expectedCategoryValue2"));
			log.info("Successfully validated Category Tier 2 = Leave");

			log.info("Step 18: Validate Category Tier 3 = PTO");
			bwfFeatureMethod.validateCaseField(dwpTestData.get("CaseCategoryFieldName3"),
					dwpTestData.get("expectedCategoryValue3"));
			log.info("Successfully validated Category Tier 3 = PTO");

			log.info("Step 19: Validate Auto Categorize option while creating case with Requester and Summary");
			bwfFeatureMethod.validateAutoCategorizeOptioncWhileCaseCreate(dwpTestData);
			log.info("Successfully validated Auto Categorize with Requester: " + dwpTestData.get("Requester")
					+ " and Summary: " + dwpTestData.get("Summary"));

		} catch (Exception e) {
			log.error("Test DRSQA_15127 failed with exception: " + e.getMessage());
			throw e;
		} 
		log.info("************END:- Support BWF cognitive features using HelixGPT************");
	}


	@AfterMethod(alwaysRun=true)
	public void closeAllDrivers() {
		WebDriver[] drivers = {isDriver, dwpDriver, getDriver()};
		String[] driverNames = {"isDriver", "dwpDriver", "getDriver"};

		for (int i = 0; i < drivers.length; i++) {
			if (drivers[i] != null) {
				try {
					drivers[i].quit();
					log.info("Closed " + driverNames[i] + " browser.");
				} catch (Exception e) {
					log.warn("Exception while closing " + driverNames[i] + ": " + e.getMessage());
				} finally {
					drivers[i] = null;
				}
			}
		}
	}
}



