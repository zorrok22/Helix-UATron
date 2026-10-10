package ITSM.HelixGPT;

import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SSPLiveChat.SspLiveChatFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonWaits;
import util.MultiProviderConfig;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class LiveChat_HelixGPT extends Launcher{
    SolqaLogger log = new SolqaLogger(LiveChat_HelixGPT.class.getName());
    WebDriver ITSMdriver,isDriver,dwpDriver,smartITdriver,smartITdriver2;
    ITSMFeatureMethods itsmFeatureMethods;
    CommonWaits commonWaits;
    ReadJson readJson;
    Map<Object, String> ITSM_TestData,ISTestData,dwpTestData,dwpUserData,smartITUserData,smartITTestData;
    SolQAConstants solqaConstants;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    DWPFeatureMethods dwpFeatureMethods;
    SspLiveChatFeatureMethods sspLiveChat;
    private static final String CUSTOMER_NAME_FMT = "(%s) %s";


    public LiveChat_HelixGPT() {
        log = new SolqaLogger(LiveChat_HelixGPT.class.getName());
        readJson = new ReadJson();
        solqaConstants = new SolQAConstants();
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        dwpFeatureMethods = new DWPFeatureMethods();
        sspLiveChat = new SspLiveChatFeatureMethods();
        commonWaits = new CommonWaits();
    }

    
    @Test(description = "[Agent Based] SSP: Chat Summary on Work Order", groups = {"HelixGPT", "SmartIT","LiveChat"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-18318", Products = {Product.HelixGPT, Product.SmartIT})
	public void DRSQA_18318_AgentBasedSSPChatSummaryOnWorkOrder(ITestContext context,String provider,String model) throws Exception {
        log.info("---------- START-------[Agent Based] SSP: Chat Summary on Work Order----------");
        log.info("--------Precondition:- in Live Chat self service portal configure Helix GPT------------------");
        log.info(" configureSelfServicePortal Should be completed before running this test for automation");

        log.info("-------Precondition: Step1:- Login to the IS------");
        
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_18318_Driver", isDriver);
        context.setAttribute("DRSQA_18318_Product", Product.Helix_Platform);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_LIVE_CHAT, SolQAConstants.SUMMARIZATION_SKILL_AGENT, provider, model);

        log.info("-------Step1:- Loginto DWP live chat------");
		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		dwpFeatureMethods = new DWPFeatureMethods();
        context.setAttribute("DRSQA_18318_Driver", dwpDriver);
        context.setAttribute("DRSQA_18318_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_18318_Data");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
		dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
		sspLiveChat = new SspLiveChatFeatureMethods();
		sspLiveChat.openLiveChatPage();
		sspLiveChat.setTopicFromChatTopics(dwpTestData.get("topic"));
		String initialChatMessage = dwpTestData.get("initialMsg") + RandomStringUtils.randomAlphanumeric(5);

        log.info("-------Step2:- Login to SmartIT Live Chat Agent Console------");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
        context.setAttribute("DRSQA_18317_Driver", smartITdriver);
        context.setAttribute("DRSQA_18317_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallenUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_18318_Data");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
		smartITFeatureMethods.navigateToLiveChatAgentConsole();
		String agentName = smartITFeatureMethods.getLiveChatAgentName();
		String customerName = String.format(CUSTOMER_NAME_FMT, dwpUserData.get("UserName"), dwpUserData.get("FirstName"));
		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(initialChatMessage);
		sspLiveChat.clickChatNowOption();

        log.info("-------Step3:- Accept chat and request control------");
		setDriver(smartITdriver);
		smartITFeatureMethods.selectChatFromQueueAndAccept(initialChatMessage, customerName);
		smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage1"));
		setDriver(dwpDriver);
		sspLiveChat.verifyMessageOnSSPConsole(smartITTestData.get("agentMessage1"));
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage1"));
		setDriver(smartITdriver);
        smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage2"));
		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage2"));
		setDriver(smartITdriver);
        smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage3"));
        setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage3"));

        log.info("-------Step4:- The live agent click on Create Ticket - Create Work Order.------");
        setDriver(smartITdriver);
        smartITFeatureMethods.clickCreateWorkOrderButton();
        smartITFeatureMethods.closeNotificationAlerts();
        smartITFeatureMethods.verifyTicketCreatedFromResourceTab();
        String workOrderId = smartITFeatureMethods.getTicketIdFromResourceTab();
        log.info("---- Generated work order ID - "+ workOrderId);
        smartITFeatureMethods.openTicketFromResources();
        commonWaits.waitForNumberWindowsToBe(2);
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchToTabByIndex(0);
        smartITFeatureMethods.endChatAndConfirmYes();
        commonWaits.switchToTabByIndex(1);

        log.info("-------Step5:- Verify the chat summary on the work order-----");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        String Context = smartITTestData.get("Context");
        String input = smartITTestData.get("Input");
        String expectedResponse = smartITTestData.get("ExpectedResponse");
        int attempt = 0;
        int maxAttempts = 3;
        while(!smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline().contains("Summary") && attempt < maxAttempts) {
            smartITFeatureMethods.refreshTicket();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            attempt++;
        }
        Assert.assertTrue(smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline().contains("Summary"), "Summary not found in the activity timeline");
        String output = smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline();
        innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expectedResponse, output);

        log.info("---------END : [Agent Based] SSP: Chat Summary on Work Order---------");
    }

    @Test(description = "[Agent based] Live Agent Console: Summarization on transferring to another live agent.", groups = {"HelixGPT", "SmartIT","LiveChat"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-18317", Products = {Product.HelixGPT, Product.SmartIT})
	public void DRSQA_18317_AgentBasedLiveAgentConsoleSummarizationOnTransferringToAnotherLiveAgent(ITestContext context,String provider,String model) throws Exception {
        log.info("---------- START-------[Agent based] Live Agent Console: Summarization on transferring to another live agent.----------");
        log.info("--------Precondition:- in Live Chat self service portal configure Helix GPT------------------");
        log.info(" configureSelfServicePortal Should be completed before running this test for automation");

        log.info("-------Precondition: Step1:- Login to the IS------");

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_18317_Driver", isDriver);
        context.setAttribute("DRSQA_18317_Product", Product.Helix_Platform);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_LIVE_CHAT, SolQAConstants.SUMMARIZATION_SKILL_AGENT, provider, model);

        log.info("-------Step1:- Loginto DWP live chat------");
		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
        context.setAttribute("DRSQA_18317_Driver", dwpDriver);
        context.setAttribute("DRSQA_18317_Product", Product.Helix_DWP);
		dwpFeatureMethods = new DWPFeatureMethods();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_18317_Data");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
		dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
		sspLiveChat = new SspLiveChatFeatureMethods();
		sspLiveChat.openLiveChatPage();
		String initialChatMessage = dwpTestData.get("initialMsg") + RandomStringUtils.randomAlphanumeric(5);

        log.info("-------Step2:- Login to SmartIT Live Chat Agent Console------");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
        context.setAttribute("DRSQA_18317_Driver", smartITdriver);
        context.setAttribute("DRSQA_18317_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ipiatti");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_18317_Data");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
		smartITFeatureMethods.navigateToLiveChatAgentConsole();
		String agentName = smartITFeatureMethods.getLiveChatAgentName();
		String customerName = String.format(CUSTOMER_NAME_FMT, dwpUserData.get("UserName"), dwpUserData.get("FirstName"));
		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(initialChatMessage);
		sspLiveChat.clickChatNowOption();

		setDriver(smartITdriver);
		smartITFeatureMethods.selectChatFromQueueAndAccept(initialChatMessage, customerName);
		smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage1"));
		
        log.info("-------Step3:- Loginto SmartIT with another user------");
        smartITdriver2 = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartITdriver2);
        context.setAttribute("DRSQA_18317_Driver", smartITdriver);
        context.setAttribute("DRSQA_18317_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallenUser");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethods.navigateToLiveChatAgentConsole();

        log.info("-------Step4:- The first live agent clicks on Chat Actions - Transfer.------");
        setDriver(smartITdriver);
        smartITFeatureMethods.openLiveChatActionDropdownForChat(initialChatMessage);
        smartITFeatureMethods.selectLiveChatAction("Transfer");

        log.info("-------Step5:- The first live agent clicks [OK] for the summary and transfers to the second live agent.------");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iallenUser");
        smartITFeatureMethods.transferChatToAgentFromDialog(smartITUserData.get("UserName"));
       
        log.info("-------Step6:- The second live agent accepts the transfer.------");
        setDriver(smartITdriver2);
        smartITFeatureMethods.selectChatFromTransferAndAccept(initialChatMessage, customerName);
        smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage3"));
       
        log.info("-------Step7:- Verify the 2nd agent and customer are able to extend the conversation.------");
        setDriver(dwpDriver);
        sspLiveChat.verifyMessageOnSSPConsole(smartITTestData.get("agentMessage3"));
        sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage3"));

        setDriver(smartITdriver2);
        smartITFeatureMethods.verifyMessageInLiveChat(dwpTestData.get("endUserMessage3"));

        log.info("-------Step8:- Verify on clicking transfer chat agent is able to verify new summary and is able to update it with some added remarks -----");
        smartITFeatureMethods.openLiveChatActionDropdownForChat(initialChatMessage);
        smartITFeatureMethods.selectLiveChatAction("Transfer");
        smartITFeatureMethods.verifySummarization();

        log.info("-------Step9:- Agent transfers the chat to the original agent-----");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "ipiattiUser");
        smartITFeatureMethods.transferChatToAgentFromDialog(smartITUserData.get("ipiattiFullName"));

        log.info("-------Step10:- Login back to smartIT Live chat with original agent -----");
        setDriver(smartITdriver);
        smartITFeatureMethods.selectChatFromTransferAndAccept(initialChatMessage, customerName);

        log.info("-------Step11:- Accept the transferred chat and verify the new summary with custom remarks is shared as a system message ----");
        smartITFeatureMethods.openLiveChatActionDropdownForChat(initialChatMessage);
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        String input = smartITTestData.get("Input");
        String Context = smartITTestData.get("Context");
        String expectedResponse = smartITTestData.get("ExpectedResponse");
        String output = smartITFeatureMethods.verifySummarization();
        innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expectedResponse, output);
       
        log.info("---------END :[Agent based] Live Agent Console: Summarization on transferring to another live agent.---------");
    }

    @Test(description = "[Agent Based] Live chat conversation using HelixGPT", groups = {"HelixGPT", "SmartIT","LiveChat"},dataProvider = "providerConfig",dataProviderClass = MultiProviderConfig.class)
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-18319", Products = {Product.HelixGPT, Product.SmartIT})
	public void DRSQA_18319_AgentBasedSSPChatSummaryOnIncident(ITestContext context,String provider,String model) throws Exception {
        log.info("---------- START-------[Agent Based] SSP: Chat Summary on Work Order----------");
        log.info("--------Precondition:- in Live Chat self service portal configure Helix GPT------------------");
        log.info(" configureSelfServicePortal Should be completed before running this test for automation");

        log.info("-------Precondition: Step1:- Login to the IS------");
        
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
        context.setAttribute("DRSQA_18319_Driver", isDriver);
        context.setAttribute("DRSQA_18319_Product", Product.Helix_Platform);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		innovationSuiteFeatureMethodsPage.clickVisitDeployedApplicationLink();
		innovationSuiteFeatureMethodsPage.selectModelAndProviderForSkill(SolQAConstants.BMC_HELIX_LIVE_CHAT, SolQAConstants.SUMMARIZATION_SKILL_AGENT, provider, model);

        log.info("-------Step1:- Loginto DWP live chat------");
		dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		dwpFeatureMethods = new DWPFeatureMethods();
        context.setAttribute("DRSQA_18319_Driver", dwpDriver);
        context.setAttribute("DRSQA_18319_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_18319_Data");
		login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
		dwpFeatureMethods.closeBurgerMenuIfPresentDWP();
		sspLiveChat = new SspLiveChatFeatureMethods();
		sspLiveChat.openLiveChatPage();
		sspLiveChat.setTopicFromChatTopics(dwpTestData.get("topic"));
		String initialChatMessage = dwpTestData.get("initialMsg") + RandomStringUtils.randomAlphanumeric(5);

        log.info("-------Step2:- Login to SmartIT Live Chat Agent Console------");
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
        context.setAttribute("DRSQA_18319_Driver", smartITdriver);
        context.setAttribute("DRSQA_18319_Product", Product.Helix_SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "iazalea");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_18319_Data");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
		smartITFeatureMethods.navigateToLiveChatAgentConsole();
		String agentName = smartITFeatureMethods.getLiveChatAgentName();
		String customerName = String.format(CUSTOMER_NAME_FMT, dwpUserData.get("UserName"), dwpUserData.get("FirstName"));
		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(initialChatMessage);
		sspLiveChat.clickChatNowOption();

        log.info("-------Step3:- Accept chat and request control------");
		setDriver(smartITdriver);
		smartITFeatureMethods.selectChatFromQueueAndAccept(initialChatMessage, customerName);
		smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage1"));
		setDriver(dwpDriver);
		sspLiveChat.verifyMessageOnSSPConsole(smartITTestData.get("agentMessage1"));
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage1"));
		setDriver(smartITdriver);
        smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage2"));
		setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage2"));
		setDriver(smartITdriver);
        smartITFeatureMethods.sendMessageToCustomerInLiveChat(smartITTestData.get("agentMessage3"));
        setDriver(dwpDriver);
		sspLiveChat.typeMessageInTextArea(dwpTestData.get("endUserMessage3"));

        log.info("-------Step4:- The live agent click on Create Ticket - Create Work Order.------");
        setDriver(smartITdriver);
        smartITFeatureMethods.clickCreateIncidentButton();
        smartITFeatureMethods.closeNotificationAlerts();
        smartITFeatureMethods.verifyTicketCreatedFromResourceTab();
        String incidentId = smartITFeatureMethods.getTicketIdFromResourceTab();
        log.info("---- Generated incident ID - "+ incidentId);
        smartITFeatureMethods.openTicketFromResources();
        commonWaits.waitForNumberWindowsToBe(2);
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchToTabByIndex(0);
        smartITFeatureMethods.endChatAndConfirmYes();
        commonWaits.switchToTabByIndex(1);

        log.info("-------Step5:- Verify the chat summary on the work order-----");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        String Context = smartITTestData.get("Context");
        String input = smartITTestData.get("Input");
        String expectedResponse = smartITTestData.get("ExpectedResponse");
        int attempt = 0;
        int maxAttempts = 3;
        while(!smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline().contains("Summary") && attempt < maxAttempts) {
            smartITFeatureMethods.refreshTicket();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            attempt++;
        }
        Assert.assertTrue(smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline().contains("Summary"), "Summary not found in the activity timeline");
        String output = smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline();
        innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(Context, input, expectedResponse, output);

        log.info("---------END : [Agent Based] SSP: Chat Summary on Work Order---------");
    }

    @AfterMethod(alwaysRun=true)
    public void closeAllDrivers() {
        WebDriver[] drivers = {isDriver, dwpDriver, smartITdriver, smartITdriver2, getDriver()};
        String[] driverNames = {"isDriver", "dwpDriver", "smartITdriver", "smartITdriver2"};
        for (int i = 0; i < drivers.length; i++) {
            drivers[i].quit();
            log.info("Closed " + driverNames[i] + " browser.");
        }
    }
    
}
