package ITSM.SmartIT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import framework.Launcher;
import org.testng.annotations.AfterMethod;

import org.testng.Assert;
import org.testng.ITestContext;

public class SmartIT_HelixGPT extends Launcher{
    // String/xpath declarations
    private static final String[] COMPONENT_MENU_SHARED = {SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*"};
    private static final String[] COMPONENT_MENU_SMARTIT = {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
    private static final String[] COMPONENT_MENU_SMARTIT_ITSM = {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
    
    SolqaLogger log = new SolqaLogger(SmartIT_HelixGPT.class.getName());
    ReadJson readJson;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    Map<Object, String> smartITUserData,smartITTestData,ITSM_TestData;
    WebDriver smartITdriver, ITSMdriver;

    public SmartIT_HelixGPT() {
    readJson = new ReadJson();
	smartITFeatureMethods = new SmartITFeatureMethodsPage();
    itsmFeatureMethods = new ITSMFeatureMethods();
    innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
    
    }
	

    @Test(description = "[25.3.00] Verify ITSM Agent Assist - predefined prompts (Summarize activity logs and Summarize incident ticket )", enabled=false, groups = {"HelixGPT", "SmartIT","gsutar"})
    @SolqaDetails(authorName = "gsutar",  JiraID = "DRSQA-11285", Products = {Product.SmartIT, Product.HelixGPT})
    public void DRSQA_11285_VerifyITSMAgentAssistPredefinedPrompts(ITestContext context) throws Exception {

		log.info("*************** START: [25.3.00] Verify ITSM Agent Assist - predefined prompts (Summarize activity logs and Summarize incident ticket ) *********************");
		log.info("****** Precondition Login to ITSM **********");
        launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        
        context.setAttribute("DRSQA_11285_Driver", getDriver());
        context.setAttribute("DRSQA_11285_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        log.info("logged in user: " + configProp.get("adminUserName"));        
        log.info("****** Setup  HELIXGPT_ASSISTANT_SERVICE_URL ******");
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SHARED);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.HELIXGPT_ASSISTANT_SERVICE_URL, SolQAConstants.HELIXGPT_ASSISTANT_SERVICE_URL_VALUE);
        log.info("******Entered HELIXGPT_ASSISTANT_SERVICE_URL ******");
        itsmFeatureMethods.closeBrowser();

        log.info("****** Step 1: Login to Smart_IT **********");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon"); 
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "CreateIncident_DRSQA_11285");
        
        launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
        log.info("logged in user: " + smartITUserData.get("UserName"));

        log.info("**** Step2 and Step3:- Create New Incident  with required all fields ********");
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.navigateToCreateNewIncident();
        smartITFeatureMethods.captureScreenshot("DRSQA_11285_navigatedToCreateIncident",SolQAConstants.appSmartIT);
        smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
        smartITFeatureMethods.saveIncident();
        
        String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
        smartITFeatureMethods.captureScreenshot("DRSQA_11285_incidentCreated", SolQAConstants.appSmartIT);
        log.pass("************ created incident is: " + incidentID);
            
         log.info("**** Step4:- Verify the predefined prompts under AskGPT Tab 1.Summarize activity logs 2.summarize Incident ticket********");
        smartITFeatureMethods.navigateToAskGPTOption();
        log.info("Clicked on Ask HelixGPT Tab");
        smartITFeatureMethods.verifySummarizeActivityAndTicketOptions(smartITTestData.get("SummarizeOption1"), smartITTestData.get("SummarizeOption2")); 
        log.info("Both "+ smartITTestData.get("SummarizeOption1") +" and "+ smartITTestData.get("SummarizeOption2")+" are availables");
        
        log.info("**** Step5:- Click on Summarize activity logs ********");
        smartITFeatureMethods.clickSummarizeOptions(smartITTestData.get("SummarizeOption1"));
        log.info("Clicked on Summarize activity logs option");
        String Context = smartITTestData.get("Context_1");
		String input = smartITTestData.get("Input_1");
		String expected = smartITTestData.get("ExpectedResponse_1");
		String output = smartITFeatureMethods.getAllAskGPTResponseString();
        log.info("Got response from HelixGPT:-"+output);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		log.info("Step 5: Validating response from HelixGPT in Smart IT");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expected, output);

        log.info("**** Step6:- Click on Summarize Incident ticket ********");
        smartITFeatureMethods.clickSummarizeOptions(smartITTestData.get("SummarizeOption2"));
        log.info("Clicked on Summarize Incident ticket option");
         String Context_2 = smartITTestData.get("Context_1");
		String input_2 = smartITTestData.get("Input_1");
		String expected_2 = smartITTestData.get("ExpectedResponse_2");
		String output_2 = smartITFeatureMethods.getAllAskGPTResponseString();
        log.info("Got response from HelixGPT:-"+output_2);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context_2, input_2, expected_2, output_2);

		log.info("*************** END: [25.3.00] Verify ITSM Agent Assist - predefined prompts (Summarize activity logs and Summarize incident ticket ) *********************");
	}

    @Test(description = "Verify Global Search Functionality in Smart IT", groups = {"MAT", "SmartIT"})
    @SolqaDetails(authorName = "gpreethi", TTID = "", JiraID = "DRSQA-11777", Products = {Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_11777_Verify_Global_Search_Functionality_in_Smart_IT() throws IOException {

        log.info("--------- DRSQA_11777_Pre-requisite: Enable Global Search with HelixGPT ---------");
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();

        itsmFeatureMethods.navigateToCentralizedConfiguration();
        itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SMARTIT);
        itsmFeatureMethods.centralizedConfigurationSetting("enableHelixGPTGlobalChat", SolQAConstants.TRUE);
        itsmFeatureMethods.centralizedConfigurationSetting("helixgptGlobalSearchDefaultMode", "search");

        String[] sharedComponent = new String[] {SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(sharedComponent);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_PROGRESSIVE_VIEWS, SolQAConstants.TRUE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.HELIXGPT_ASSISTANT_SERVICE_URL, SolQAConstants.HELIXGPT_ASSISTANT_SERVICE_URL_VALUE);

        itsmFeatureMethods.closeBrowser();
        log.info("--------- DRSQA_11777_Pre-requisite: Completed ---------");

        log.info("--------- DRSQA_11777_Step1: Login to Smart IT ---------");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");
        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        log.info("Logged in as Smart IT user: " + smartITUserData.get("UserName"));

        log.info("--------- DRSQA_11777_Step2: Verify Helix GPT Global Search icon (sparkles) ---------");
        smartITFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
        smartITFeatureMethods.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
        log.info("Helix GPT Global Search icon displayed with sparkles and clicked successfully.");

        log.info("--------- DRSQA_11777_Step3: Verify search filter options ---------");
        smartITFeatureMethods.verifyFilterOptionsOnSearchDropdownMenus();
           log.info("All expected search filter options are visible.");

        log.info("--------- DRSQA_11777_Step4: Search 'Sample BMCOPs RKM Article' with All filter and validate categories ---------");
        smartITFeatureMethods.verifyFilterOptionsOnSearchDropdownMenus();
        smartITFeatureMethods.searchBmcopsEnduserAndVerifyListCardView();
        log.info("Results for Ticket, Request, and Knowledge opened in the same window.");

        log.info("--------- DRSQA_11777_Step6: Search Asset 'TestComp15559' and open profile ---------");
        smartITFeatureMethods.searchAssetAndOpenProfile("TestComp15559");
        log.info("Asset result opened in the same window for TestComp15559.");

        log.info("--------- DRSQA_11777: END ---------");
    }


    @Test(description = "ITSM Helix GPT Chat should answer questions asked based of Ticket to Support Group relations")
    @SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-11287", Products = {Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_11287_ITSM_Helix_GPT_Chat_should_answer_questions() throws IOException {
		
		log.info("--------- DRSQA_11283_ITSM_Helix_GPT_Chat_should_answer_questions *** START");
		
		log.info("--------- DRSQA_11283_Pre-requisite Steps: START *********");
		
		ITSMdriver = launchWeb(SolQAConstants.appITSM,SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		login(configProp.get("hannahAdminUserName"),configProp.get("hannahAdminUserPassword"),SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();
		
        log.info("Loading Enabling Helix GPT Chat : True");

		itsmFeatureMethods.navigateToCentralizedConfiguration();
        itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SMARTIT_ITSM);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_HELIXGPT_GLOBAL_CHAT, SolQAConstants.TRUE);
        
        itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SHARED);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_PROGRESSIVE_VIEWS, SolQAConstants.TRUE);
        itsmFeatureMethods.closeBrowser();
        
        log.info("--------- DRSQA_11283_Pre-requisite Steps : END *********");
		
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData",
	            "Helix_GPT_ChatQuestions_DRSQA_11283");
        
        smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
        
        log.info("--------- Initializing SmartITFeatureMethods handler");
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        
        log.info("**** Navigating to ITSM Global Search ********");
        smartITFeatureMethods.navigateToHelixGPTGlobalSearch();
        smartITFeatureMethods.verifyGlobalSearchStudioPageLaunchedSuccessfully();
        smartITFeatureMethods.clickOnChatSearchButton(smartITTestData.get("ChatButton"));
        
        log.info("**** Asking questions on global chat ********");
        smartITFeatureMethods.askQuestionInChat(smartITTestData.get("Question1"));
        //validate response using DeepEval code
        
        smartITFeatureMethods.askFollowUpQuestionInChat(smartITTestData.get("Question2"));
        //validate response using DeepEval code
        
        smartITFeatureMethods.askFollowUpQuestionInChat(smartITTestData.get("Question3"));
        //validate response using DeepEval code
        
        log.info("--------- DRSQA_11283_ITSM_Helix_GPT_Chat_should_answer_questions *** END");
        
	}
    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (smartITFeatureMethods != null) {
            smartITFeatureMethods.closeBrowser();
        }
        if (itsmFeatureMethods != null) {
        	itsmFeatureMethods.closeBrowser();
        }
    }
}

