package ITSM.SmartIT;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import ITSM.ITSM.ITSM_IS_SmartIT;
import framework.Launcher;
import pages.BCM.BCMDashboard;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.ITSM.ITSMDashboard;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SSPLiveChat.SspLiveChatFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.Listener;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.WebDriverManager;

public class SmartIT_ITSM_DWPC extends Launcher {
	
	static SolqaLogger log = new SolqaLogger(ITSM_IS_SmartIT.class.getName());
    ReadJson readJson = new ReadJson();
    SmartITFeatureMethodsPage smartITFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    DWPCFeatureMethods dwpcFeatureMethods;
    DWPFeatureMethods dwpFeatureMethods;
    SspLiveChatFeatureMethods sspLiveChat;
    ITSMDashboard itsmDashboard;
    
    Map<Object, String> smartITUserData, smartITTestData, itsmUserData, itsmTestData, dwpcUserData, dwpcTestData, dwpUserData, dwpTestData;
    WebDriver smartITdriver, ITSMdriver, DWPCdriver, dwpDriver;
    
    private static final String CUSTOMER_NAME_FMT = "(%s) %s";
   
    public SmartIT_ITSM_DWPC() {
		log.info("*********** beforeClassSmartIT in SmartIT_ITSM_DWPC ********************");
	}

@Test(description = "Request from Live chat and Resolve the incident via smart IT.", groups = {"Regression_P2", "DWP", "SmartIT", "ITSM","gsutar"})
@SolqaDetails(authorName = "gsutar", TTID = "292", JiraID = "DRSQA-11439",Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.LiveChat, Product.VirtualChat})
    public void DRSQA_11439_Request_From_LiveChat_Resolve_Incident_Via_SmartIT(ITestContext context) throws Exception {

	log.info("******Step 1: Enable Progressive View in ITSM**********");
	ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	setDriver(ITSMdriver);
        context.setAttribute("DRSQA_11439_Driver", ITSMdriver);
        context.setAttribute("DRSQA_11439_Product", Product.Helix_ITSM);

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));

	log.info("******Step 2: Login to DWP and start SSP Live Chat**********");
	dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
	setDriver(dwpDriver);
	context.setAttribute("DRSQA_11439_Driver", dwpDriver);
	context.setAttribute("DRSQA_11439_Product", Product.Helix_DWP);

	dwpFeatureMethods = new DWPFeatureMethods();
	dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillEnduser");
	dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11439_Data");

	login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
	dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
	sspLiveChat = new SspLiveChatFeatureMethods();
	sspLiveChat.openLiveChatPage();
	sspLiveChat.setTopicFromChatTopics(dwpTestData.get("topic"));

	String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
	String initialChatMessage = dwpTestData.get("initialMsg") + randomSuffix;
	sspLiveChat.typeMessageInTextArea(initialChatMessage);
	sspLiveChat.clickChatNowOption();

	log.info("******Step 3: Login to SmartIT Live Chat Agent Console and accept chat**********");
	smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
	setDriver(smartITdriver);
	context.setAttribute("DRSQA_11439_Driver", getDriver());
	context.setAttribute("DRSQA_11439_Product", Product.Helix_SmartIT);

	smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallenUser");
	smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11439_Data");
	login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

	smartITFeatureMethods = new SmartITFeatureMethodsPage();
	smartITFeatureMethods.navigateToLiveChatAgentConsole();

	String agentName = smartITFeatureMethods.getLiveChatAgentName();
	String customerName = String.format(CUSTOMER_NAME_FMT, dwpUserData.get("UserName"), dwpUserData.get("FirstName"));
	String customerFullName = (dwpUserData.get("FirstName") + " " + dwpUserData.get("LastName")).trim();

	smartITFeatureMethods.selectChatFromQueueAndAccept(initialChatMessage, customerName);
	smartITFeatureMethods.sendMessageToCustomerInLiveChat(dwpTestData.get("message1"));

	log.info("******Step 4: Verify join/system message on DWP SSP console**********");
	setDriver(dwpDriver);
	String joinTemplate = dwpTestData.get("systemMessageForJoiningTheSession");
	sspLiveChat.verifySystemMessageOnSSPConsole(String.format(joinTemplate, agentName));
	sspLiveChat.verifyMessageOnSSPConsole(dwpTestData.get("message1"));
	sspLiveChat.typeMessageInTextArea(dwpTestData.get("message2"));

	log.info("******Step 5: Continue conversation and create Incident from SmartIT Live Chat**********");
	setDriver(smartITdriver);
	smartITFeatureMethods.verifyMessageInLiveChat(dwpTestData.get("message2"));
    smartITFeatureMethods.sendMessageToCustomerInLiveChat(dwpTestData.get("message3"));

	setDriver(dwpDriver);
	sspLiveChat.verifyMessageOnSSPConsole(dwpTestData.get("message3"));
	sspLiveChat.typeMessageInTextArea(dwpTestData.get("message4"));

	setDriver(smartITdriver);
	smartITFeatureMethods.verifyMessageInLiveChat(dwpTestData.get("message4"));

	log.info("******Step 6: Create Incident and open it from Resources tab**********");
	smartITFeatureMethods.clickCreateIncidentButton();
	smartITFeatureMethods.closeNotificationAlerts();
	
	smartITFeatureMethods.verifyTicketCreatedFromResourceTab(); // the incident or wo id will be in resource tab
	String incidentId = smartITFeatureMethods.getTicketIdFromResourceTab();
	log.info("---- Generated incident ID - "+ incidentId);
	smartITFeatureMethods.openTicketFromResources();
	commonWaits.waitForNumberWindowsToBe(2);
	commonWaits.switchToTabByIndex(1);
	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	String URL = getDriver().getCurrentUrl();

	log.info("--- Current URL- " + URL);
	try{
	Assert.assertTrue(URL.contains("incidentPV"), "incident is opened in Non progressive view. Expected PV page but found -" + URL);
	}
	catch(AssertionError e){
		smartITFeatureMethods.refreshBrowser();
		String RefreshedURL = getDriver().getCurrentUrl();
		Assert.assertTrue(RefreshedURL.contains("incidentPV"), "incident is opened in Non progressive view. Expected PV page but found -" + RefreshedURL);
	}
	Assert.assertEquals(smartITFeatureMethods.FetchIncidentTicketID(), incidentId, "Incident ID not matched");
	Assert.assertEquals(smartITFeatureMethods.getCustomerValue_viewPage(), customerFullName, "Workorder ID not matched");
	commonWaits.switchToTabByIndex(0);
	smartITFeatureMethods.endChatAndConfirmYes();
	smartITFeatureMethods.closeNotificationAlerts();
	commonWaits.switchToTabByIndex(1);
	smartITFeatureMethods.refreshBrowser();
	log.info("--- Current URL- " + URL);
	try{
	Assert.assertTrue(URL.contains("incidentPV"), "incident is opened in Non progressive view. Expected PV page but found -" + URL);
	}
	catch(AssertionError e){
		smartITFeatureMethods.refreshBrowser();
		String RefreshedURL = getDriver().getCurrentUrl();
		Assert.assertTrue(RefreshedURL.contains("incidentPV"), "incident is opened in Non progressive view. Expected PV page but found -" + RefreshedURL);
	}
	smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.RESOLVED);

	log.info("**************** End DRSQA_11439_Request_From_LiveChat_Resolve_Incident_Via_SmartIT ******************");
}

	//previous owner preethi
	@Test(description = "Live chat integration with BCM", groups = {"Regression_P3", "DWP", "SmartIT", "ITSM", "BCM", "LiveChat", "sonalsha"})
	@SolqaDetails(authorName = "sonalsha", TTID = "17943", JiraID = "DRSQA-11581",
			Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.Helix_SmartIT, Product.BCM, Product.LiveChat})
	public void DRSQA_11581_Live_Chat_Integration_With_BCM(ITestContext context) throws Exception {

		log.info("**************** Start DRSQA_11581_Live_Chat_Integration_With_BCM ******************");

		final Map<Object, String> testUsers = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "user", "DRSQA_11581_Data");
		final String itsmAdminUserKey = testUsers.get("ITSMAdminUserKey").trim(),
				dwpEndUserKey = testUsers.get("DWPEndUserKey").trim(),
				smartITLiveAgentUserKey = testUsers.get("SmartITLiveAgentUserKey").trim(),
				bcmUserKey = testUsers.get("BCMUserKey").trim();

		itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", itsmAdminUserKey);
		itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11581_Data");
		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", dwpEndUserKey);
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11581_Data");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", smartITLiveAgentUserKey);
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11581_Data");

		// ITSM: configure BCM URLs
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
		itsmFeatureMethods = new ITSMFeatureMethods();
		itsmDashboard = new ITSMDashboard();
		itsmDashboard.verifyLogin();
		String bcmWebConsoleUrl = configProp.get("BCM_Webconsole");
		URL bcmUrl = new URL(bcmWebConsoleUrl);
		String bcmBaseUrl = bcmUrl.getProtocol() + "://" + bcmUrl.getHost();
		itsmDashboard.navigateToCentralizedConfiguration();
		itsmDashboard.centralizedConfigurationSetting(itsmTestData.get("BCMMasterUrlSettingName"), bcmBaseUrl);
		itsmDashboard.centralizedConfigurationSetting(itsmTestData.get("BCMConsoleUrlSettingName"), bcmWebConsoleUrl);
		itsmFeatureMethods.closeBrowser();

		// DWP: start SSP live chat
		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		dwpFeatureMethods = new DWPFeatureMethods();
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
		dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
		sspLiveChat = new SspLiveChatFeatureMethods();
		sspLiveChat.openLiveChatPage();
		sspLiveChat.setTopicFromChatTopics(dwpTestData.get("topic"));
		String initialChatMessage = dwpTestData.get("initialMsg") + UUID.randomUUID().toString().replace("-", "").substring(0, 5);

		// SmartIT: accept chat and request control
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.navigateToLiveChatAgentConsole();
		String agentName = smartITFeatureMethods.getLiveChatAgentName();
		String customerName = String.format(CUSTOMER_NAME_FMT, dwpUserData.get("UserName"), dwpUserData.get("FirstName"));

		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(initialChatMessage);
		sspLiveChat.clickChatNowOption();

		setDriver(smartITdriver);
		smartITFeatureMethods.selectChatFromQueueAndAccept(initialChatMessage, customerName);
		smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage1"));

		setDriver(dwpDriver);
		sspLiveChat.verifySystemMessageOnSSPConsole(String.format(dwpTestData.get("systemMessageForJoiningTheSession"), agentName));
		sspLiveChat.verifyMessageOnSSPConsole(smartITTestData.get("agentMessage1"));
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage1"));

		setDriver(smartITdriver);
		smartITFeatureMethods.verifyMessageInLiveChat(dwpTestData.get("endUserMessage1"));

		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage2"));

		setDriver(smartITdriver);
		smartITFeatureMethods.verifyMessageInLiveChat(dwpTestData.get("endUserMessage2"));

		smartITFeatureMethods.openLiveChatActionDropdownForChat(initialChatMessage);
		smartITFeatureMethods.selectLiveChatAction(SolQAConstants.REQUEST_CONTROL);
		smartITFeatureMethods.waitForNumberWindowsToBe(2);
		smartITFeatureMethods.switchToLastWindow();
		Map<Object, String> bcmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBCM, "BCMUsers", bcmUserKey);
		login(bcmUserData.get("UserName"), bcmUserData.get("Password"), SolQAConstants.appBCM);
		new BCMDashboard().performLogout();
		try { getDriver().close(); } catch (Exception ignored) {}
		smartITFeatureMethods.switchToLastWindow();
		setDriver(getDriver());

		// ITSM: clear BCM URLs
		ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
		login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
		itsmDashboard = new ITSMDashboard();
		itsmDashboard.verifyLogin();
		itsmDashboard.navigateToCentralizedConfiguration();
		itsmDashboard.centralizedConfigurationSetting(itsmTestData.get("BCMMasterUrlSettingName"), "");
		itsmDashboard.centralizedConfigurationSetting(itsmTestData.get("BCMConsoleUrlSettingName"), "");
	    itsmFeatureMethods.closeBrowser();

		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage3"));
		sspLiveChat.verifyMessageOnSSPConsole(dwpTestData.get("endUserMessage3"));
		setDriver(smartITdriver);
		smartITFeatureMethods.verifyMessageInLiveChat(dwpTestData.get("endUserMessage3"));

		log.info("**************** End DRSQA_11581_Live_Chat_Integration_With_BCM ******************");
	}

	//Disabling the usecase right now as this will fail on case insensitive stack
	@Test(description = "Global Search and Case sensitive check", groups = {"Regression_P1", "SmartIT", "ITSM", "DWPC", "sonalsha"}, enabled = false)
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11404",Products = {Product.Helix_ITSM,Product.Helix_SmartIT, Product.Helix_DWPC})
    public void DRSQA_11404_Global_Search_And_Case_Sensitive_Check(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA_11404 Global Search and Case sensitive check ******************");
        
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon"); 
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "CreateIncident_DRSQA_11404");
        
        // Use existing WebDriver from ThreadLocal
        smartITdriver = launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
        log.info("--------- Initializing SmartITFeatureMethods handler");
        smartITFeatureMethods = new SmartITFeatureMethodsPage();

        log.info("**** Navigating to Create New Incident ********");
        smartITFeatureMethods.navigateToCreateNewIncident();
        smartITFeatureMethods.captureScreenshot("DRSQA_11404_navigatedToCreateIncident",SolQAConstants.appSmartIT);
        
        smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
        smartITFeatureMethods.saveIncident();
        
        String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
        smartITFeatureMethods.captureScreenshot("DRSQA_11404_incidentCreated", SolQAConstants.appSmartIT);
        log.pass("************ created incident is: " + incidentID);
        
        log.info("--------- DRSQA_11404_ Search article in ITSM ***");
        
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        
        // Use existing WebDriver from ThreadLocal
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(ITSMdriver);
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        log.info("--------- Initializing ITSMFeatureMethods handler");
        itsmFeatureMethods = new ITSMFeatureMethods();
        
        log.info("**** Navigating to Global Search ITSM ********");
		itsmFeatureMethods.searchViaGlobalSearch(incidentID);
        itsmFeatureMethods.validateIncidentIDCaseSensitivity(incidentID);
        
        log.info("--------- DRSQA_11404_ Search article in DWPC ***");

        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "SearchService_DRSQA_11404");

        // Use existing WebDriver from ThreadLocal
        DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(DWPCdriver);
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);

        log.info("--------- Initializing DWPCFeatureMethods handler");
        dwpcFeatureMethods = new DWPCFeatureMethods();

        log.info("**** Navigating to Global Search DWPC ********");
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.verifyServicesPageLaunchedSuccessfully();
        dwpcFeatureMethods.searchForService(dwpcTestData.get("SERVICE_NAME"));
        dwpcFeatureMethods.validateServiceNameCaseSensitivity(dwpcTestData.get("SERVICE_NAME"));
      
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
        if (dwpFeatureMethods != null) {
        	dwpFeatureMethods.closeBrowser();
        }
        WebDriverManager.cleanupDriver();
        Listener.cleanupThreadLocal();
    }
}
