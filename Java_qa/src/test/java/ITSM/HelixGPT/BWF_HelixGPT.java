package ITSM.HelixGPT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.Product;
import util.MultiProviderConfig;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class BWF_HelixGPT extends Launcher {
    SolqaLogger log;
    ReadJson readJson;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    BWFFeatureMethods bwfFeatureMethods;
    Map<Object, String> ISUsersData, ISTestData, BWFUsersData, BWFTestData;
    WebDriver isDriver, helixGPTDriver, BWFDriver = null;

    public BWF_HelixGPT() {
        log = new SolqaLogger(BWF_HelixGPT.class.getName());
        readJson = new ReadJson();

    }

	@Test(description = "Generate Resolution description using HelixGPT", groups = {"HelixGPT", "BWF","gsutar"}, dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", TTID = "38606", JiraID = "DRSQA-12282", Products = {Product.Helix_BWF, Product.HelixGPT })
	public void DRSQA12282_GenerateResolutionDescriptionUsingHelixGPT(ITestContext context,String provider,String model) throws Exception {

		log.info("------------START :Generate Resolution description using HelixGPT----------");
		log.info("Running test with Provider = " + provider + " | Model = " + model);

		log.info("Preconditions:- Generate Case Resolution skill seed data should be disabled-----");
		// Add code to disable the skill seed data

		log.info("Login to IS and setup model and provider");
		log.info("-------Step1:- Login to the IS------");
       
        ISTestData  = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_12282Data");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		context.setAttribute("DRSQA_12282_Driver", isDriver);
        context.setAttribute("DRSQA_12282_Product", Product.Helix_Platform);

		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

        log.info("Step 2 and step3: Navigate to the HelixGPT Agent Studio Page");
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_BUSINESS_WORKFLOWS, ISTestData.get("ResolutionSkillName"), provider, model);
		innovationSuiteFeatureMethodsPage.selectSkillSearchSetting(ISTestData.get("InfoProvider1"));
		isDriver.quit();

		log.info("----- Step 1: Login to BWF with case agent -------");
		log.info("Logging in to BWF");
        this.BWFDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(BWFDriver);
		context.setAttribute("DRSQA_12282_Driver", BWFDriver);
		context.setAttribute("DRSQA_12282_Product", Product.Helix_BWF);
        BWFUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        BWFTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_12282Data");
        login(BWFUsersData.get("UserName"), BWFUsersData.get("Password"), SolQAConstants.appBWF);
        log.info("Logged in user: " + BWFUsersData.get("UserName"));
		bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);

		bwfFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
		 log.info("Create a case");
        String caseID = bwfFeatureMethods.createCase(BWFTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_12282_Case created successfully", SolQAConstants.appBWF);
        log.info("Case ID: " + caseID);
		bwfFeatureMethods.assignCaseToMe();
		getDriver().navigate().refresh();
		log.info("step2:- Case the status to resolved , status reason to resolved and click on Generate  Case Resolution ");
		bwfFeatureMethods.changeStatusTo(SolQAConstants.IN_PROGRESS);
		bwfFeatureMethods.changeCaseStatusToWithoutSaving(SolQAConstants.RESOLVED);
		bwfFeatureMethods.clickOnGenerateCaseResolution();
		bwfFeatureMethods.saveStatusTransition();

		log.info("step3:- Once case is resolved check on the Case Resolution Description it should have Helix GPT prompt ");
		String Context = BWFTestData.get("Context");
		String input = BWFTestData.get("Input");
		String expected = BWFTestData.get("ExpectedResponse");
		String output = bwfFeatureMethods.getResolutionDescription();
        log.info("Got response from HelixGPT:-"+output);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);
		BWFDriver.quit();

		log.info("------------END : Generate Resolution description using HelixGPT----------");

	}

	@Test(description = "Global Chat check with BWF HelixGPT Agent", groups = {"HelixGPT", "BWF"})
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-12838", Products = {Product.Helix_BWF, Product.HelixGPT})
	public void DRSQA_12838_GlobalChatCheckWithBWFHelixGPTAgent(ITestContext context) throws Exception {
        
		log.info("------------START: Global Chat check with BWF HelixGPT Agent----------");
        
		log.info("Step 1: Login to BWF Agent with Elizabeth");
		this.BWFDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
		setDriver(BWFDriver);
		BWFUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
		BWFTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_12838_Data");
		login(BWFUsersData.get("UserName"), BWFUsersData.get("Password"), SolQAConstants.appBWF);
		log.info("Logged in user: " + BWFUsersData.get("UserName"));
		context.setAttribute("DRSQA_12838_Driver", getDriver());
		context.setAttribute("DRSQA_12838_Product", Product.Helix_BWF);
        
		bwfFeatureMethods = new BWFFeatureMethods();
		bwfFeatureMethods.verifyLoginToBWF();
        
		log.info("Step 2: Navigate to Settings --> Manage Line of Business --> Helix GPT --> Features --> Helix GPT Global search --> true");
		bwfFeatureMethods.navigateToSettings();
		bwfFeatureMethods.selectHelixGPTTab();
		bwfFeatureMethods.clickOnHelixGPTConfigNameAndSetValue(BWFTestData.get("HelixGPTGlobalSearchConfig"), BWFTestData.get("ConfigValue"));
		log.info("Successfully enabled '" + BWFTestData.get("HelixGPTGlobalSearchConfig") + "' feature to " + BWFTestData.get("ConfigValue"));
        
		log.info("Step 3: Verify Global Search button is visible after HelixGPT search is enabled");
		bwfFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
		log.info("✅ Global Search button is visible after HelixGPT search is enabled");
        
		log.info("Step 4: Login to BWF with Liam");
		BWFDriver.quit();
		this.BWFDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
		setDriver(BWFDriver);
		Map<Object, String> liamUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "LiamUsers");
		login(liamUserData.get("UserName"), liamUserData.get("Password"), SolQAConstants.appBWF);
		log.info("Logged in user: " + liamUserData.get("UserName"));
		context.setAttribute("DRSQA_12838_Driver", getDriver());
		context.setAttribute("DRSQA_12838_Product", Product.Helix_BWF);
		
		bwfFeatureMethods = new BWFFeatureMethods();
		bwfFeatureMethods.verifyLoginToBWF();
		log.info("✅ Successfully logged in to BWF with Liam");

		log.info("Step 5: Change Line of Business to '" + BWFTestData.get("LineOfBusiness") + "'");
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(BWFTestData.get("LineOfBusiness"));
		log.info("✅ Line of Business changed to '" + BWFTestData.get("LineOfBusiness") + "'");

		log.info("Step 6: Open the Global Chat");
		bwfFeatureMethods.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
		bwfFeatureMethods.navigateToChatOption();
		log.info("✅ Global chat opened successfully");

		log.info("Step 7: Search for support group worked on highest number of cases");
		String query1 = BWFTestData.get("Query1");
		bwfFeatureMethods.sendMessageInGPTChat(query1);
		String output1 = bwfFeatureMethods.getAllBWFGPTFullResponseString();
		log.info("Got response from HelixGPT for Query1: " + output1);
		
		log.info("Step 8: Verify response using DeepEval for Query1");
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		String context1 = BWFTestData.get("Context1");
		String expected1 = BWFTestData.get("ExpectedResponse1");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context1, query1, expected1, output1);
		log.info("✅ DeepEval verification passed for Query1");
        
		log.info("Step 9: Ask query - List of cases belongs to Finance LOB");
		String query2 = BWFTestData.get("Query2");
		bwfFeatureMethods.sendMessageInGPTChat(query2);
		String output2 = bwfFeatureMethods.getAllBWFGPTFullResponseString();
		log.info("Got response from HelixGPT for Query2: " + output2);
		
		log.info("Step 10: Verify response using DeepEval for Query2");
		String context2 = BWFTestData.get("Context2");
		String expected2 = BWFTestData.get("ExpectedResponse2");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context2, query2, expected2, output2);
		log.info("✅ DeepEval verification passed for Query2");
        
		log.info("Step 11: Click on any case ID from Query2 response and open in new tab");
		bwfFeatureMethods.clickFirstLinkInGPTResponse();
		log.info("Clicked on first case ID link from Query2 response");
		
		log.info("Step 12: Verify Case page opened in new tab and validate Case Display ID");
		String caseDisplayId = bwfFeatureMethods.verifyAndCloseCasePageInNewTab();
		log.info("Verified and closed case page with Display ID: " + caseDisplayId);
        
		log.info("Step 13: Ask Query3 - Show list of cases which needs attention");
		String query3 = BWFTestData.get("Query3");
		bwfFeatureMethods.sendMessageInGPTChat(query3);
		String output3 = bwfFeatureMethods.getAllBWFGPTFullResponseString();
		log.info("Got response from HelixGPT for Query3: " + output3);
		
		log.info("Step 14: Verify response using DeepEval for Query3");
		String context3 = BWFTestData.get("Context3");
		String expected3 = BWFTestData.get("ExpectedResponse3");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context3, query3, expected3, output3);
		log.info("✅ DeepEval verification passed for Query3");

		log.info("Step 15: Ask Query4 - Show me cases requested by Fritz");
		String query4 = BWFTestData.get("Query4");
		bwfFeatureMethods.sendMessageInGPTChat(query4);
		String output4 = bwfFeatureMethods.getAllBWFGPTFullResponseString();
		log.info("Got response from HelixGPT for Query4: " + output4);
		
		log.info("Step 16: Verify response using DeepEval for Query4");
		String context4 = BWFTestData.get("Context4");
		String expected4 = BWFTestData.get("ExpectedResponse4");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context4, query4, expected4, output4);
		log.info("✅ DeepEval verification passed for Query4");

		log.info("Step 17: Ask Query5 - Show Cases which are Pending Approval");
		String query5 = BWFTestData.get("Query5");
		bwfFeatureMethods.sendMessageInGPTChat(query5);
		String output5 = bwfFeatureMethods.getAllBWFGPTFullResponseString();
		log.info("Got response from HelixGPT for Query5: " + output5);
		
		log.info("Step 18: Verify response using DeepEval for Query5");
		String context5 = BWFTestData.get("Context5");
		String expected5 = BWFTestData.get("ExpectedResponse5");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context5, query5, expected5, output5);
		log.info("✅ DeepEval verification passed for Query5");

		log.info("Step 19: Ask Query6 - Show all Cases created between date range (7 days before to 1 day before current date)");
		// Calculate dynamic dates
		java.time.LocalDate today = java.time.LocalDate.now();
		java.time.LocalDate startDate = today.minusDays(7);
		java.time.LocalDate endDate = today.minusDays(1);
		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy");
		String startDateStr = startDate.format(formatter);
		String endDateStr = endDate.format(formatter);
		
		String query6Template = BWFTestData.get("Query6");
		String query6 = query6Template.replace("START_DATE", startDateStr).replace("END_DATE", endDateStr);
		log.info("Query6 with dynamic dates: " + query6);
		
		bwfFeatureMethods.sendMessageInGPTChat(query6);
		String output6 = bwfFeatureMethods.getAllBWFGPTFullResponseString();
		log.info("Got response from HelixGPT for Query6: " + output6);
		
		log.info("Step 20: Verify response using DeepEval for Query6");
		String context6 = BWFTestData.get("Context6");
		String expected6 = BWFTestData.get("ExpectedResponse6");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context6, query6, expected6, output6);
		log.info("✅ DeepEval verification passed for Query6");

		BWFDriver.quit();
		log.info("------------END: Global Chat check with BWF HelixGPT Agent----------");
	}

	@AfterMethod(alwaysRun=true)
	public void closeAllDrivers() {
		WebDriver[] drivers = {isDriver, BWFDriver};
		String[] driverNames = {"isDriver", "BWFDriver"};
		for (int i = 0; i < drivers.length; i++) {
			try {
				if (drivers[i] != null && drivers[i].toString().contains("Session")) {
					drivers[i].quit();
					log.info("Closed " + driverNames[i] + " browser.");
				}
			} catch (Exception e) {
				log.warn("Exception while closing " + driverNames[i] + ": " + e.getMessage());
			}
		}
		try {
			WebDriver mainDriver = getDriver();
			if (mainDriver != null && mainDriver.toString().contains("Session")) {
				mainDriver.quit();
				log.info("Closed getDriver browser.");
			}
		} catch (Exception e) {
			log.warn("Exception while closing getDriver: " + e.getMessage());
		}
	}


}
