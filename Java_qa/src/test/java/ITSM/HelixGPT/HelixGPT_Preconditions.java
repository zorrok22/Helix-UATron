package ITSM.HelixGPT;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pages.ITSM.HelixGPT.HVA_Skill_Import_API;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.CommonMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaLogger;

public class HelixGPT_Preconditions extends Launcher {
    SolqaLogger log;
    ReadJson readJson;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    BWFFeatureMethods bwfFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    WebDriver isDriver, BWFDriver, itsmDriver, ITSMdriver;
    String BASE_URL;

    public HelixGPT_Preconditions() {
        log = new SolqaLogger(HelixGPT_Preconditions.class.getName());
        readJson = new ReadJson();
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        bwfFeatureMethods = new BWFFeatureMethods();
        itsmFeatureMethods = new ITSMFeatureMethods();
        isDriver = null;
        BASE_URL = getBaseURL(SolQAConstants.appIS);
    }
    Map<Object, String> ISUsersData, BWFUsersData, BWFTestData, itsmUserData;
    private String jwtToken;

    @BeforeSuite(alwaysRun = true)
    public void runPreconditions() throws Exception {
        importSolqaHVASkill();
        UnseedSkill();
        createHelixGPTBotAndSaveBotIDIntoFile();
        enableDataSourcesInHelixGPTAgentStudio();
        createSharePointConnectionAndInjectTheData();
        createWebConnectionAndInjectTheData();
        createCSMConnectionAndInjectTheData();
        injectRKMArticleData();
        injectHKMArticleData();
        injectBWFArticleData();
        enableBWFHelixGPTSearchAndGenerateCaseResolution();
        enableSmartITHelixGPTSearch();
        configureSelfServicePortal();
    }

    // Common request builder
    private RequestSpecification baseRequest() {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .header("X-Requested-By", "XMLHttpRequest");
    }

    public void login() {
        String username = CommonMethods.readConfig("IS_DEMO_USERNAME");
        String password = CommonMethods.readConfig("IS_DEMO_PASSWORD");

        String requestBody = "{"
                + "\"userName\": \"" + username + "\","
                + "\"password\": \"" + password + "\","
                + "\"locale\": \"en\""
                + "}";

        Response response = baseRequest()
                .accept("*/*")
                .body(requestBody)
                .post(BASE_URL + "/api/rx/authentication/loginrequest");

        if (response.getStatusCode() == 200) {
            jwtToken = response.asString();
            System.out.println("Logged in. AR-JWT Token: " + jwtToken);
        } else {
            throw new RuntimeException("Login failed! Status: " + response.getStatusCode()
                    + " | Body: " + response.getBody().asString());
        }
    }

    public void unseedSkill(String skillId, String payload) {
        String uri = BASE_URL + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Skill/" + skillId;
        Response response = baseRequest()
                .header("Authorization", "AR-JWT " + jwtToken)
                .body(payload)
                .put(uri);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        switch (statusCode) {
            case 204:
                log.info("Skill " + skillId + " unseeded successfully.");
                break;
            case 412:
                log.warn("Skill " + skillId + " was modified by another user.");
                break;

            case 404:
                // Skill ID not present / record not found – log and continue with next skill
                log.warn("Skill " + skillId + " not found while unseeding. Skipping to next skill.");
                break;
            default:
                throw new AssertionError("Error while unseeding skill " + skillId +
                        ". Got status: " + statusCode + " | Body: " + responseBody);
        }
    }

    //Utility method for payload
    private String buildUnseedPayload(String skillId, String displayId, String timestamp) {
        return "{"
                + "\"id\":\"" + skillId + "\","
                + "\"resourceType\":\"com.bmc.arsys.rx.services.record.domain.RecordInstance\","
                + "\"displayId\":\"" + displayId + "\","
                + "\"recordDefinitionName\":\"com.bmc.dsom.hgm:Skill\","
                + "\"permittedGroupsBySecurityLabels\":{\"access_permission\":[\"0\"]},"
                + "\"permittedUsersBySecurityLabels\":{},"
                + "\"permittedRolesBySecurityLabels\":{},"
                + "\"fieldInstances\":{"
                + "\"6\":{\"resourceType\":\"com.bmc.arsys.rx.services.record.domain.FieldInstance\",\"id\":6,\"value\":\"" + timestamp + "\",\"permissionType\":null},"
                + "\"490000110\":{\"resourceType\":\"com.bmc.arsys.rx.services.record.domain.FieldInstance\",\"id\":490000110,\"value\":0,\"permissionType\":null}"
                + "}"
                + "}";
    }

    public HelixGPT_Preconditions importSolqaHVASkill() throws Exception {
        try {
            HVA_Skill_Import_API hvaSkillImportAPI = new HVA_Skill_Import_API();
            hvaSkillImportAPI.importHVASkills();   
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public HelixGPT_Preconditions UnseedSkill() {
    	try {
        login();
        String incidentAgentAssistConversation = "AGGBBXA9IIGX5AS1JFRLS1JFRLB82O";
        String incidentAgentAssistResolution = "AGGBBXA9IIGX5AS1JGB7S1JGB7B9TQ";
        String bwfCaseConversationSkill = "AGGBBXA9IIGX5AS2RD1TS2RD1TPFJ8";
        String agentInGlobalContext = "AGGAPTFZASVEUAS8D5D8S8D5D8URZM";
        String bwfGlobalChatSkill = "AGGGA2TSJ8258AS9AYEWS9AYEWN9SH";
        String incidentResolutionGeneration = "AGGCKS2IDOYBVASRJTJTSRJTJTQ1F0";
        String generateCaseResolution = "AGGIU2BVER8UMASQE1FKSQE1FK1R6O";
        String dwpEmployeeNavigatorAgentV1 = "AGGAK9BBG1OTSASP3KCPSP3KCPVTIO";
        String bwfCaseSummarizationSkill = "AGGC057NJW4LLASWK1HDSWK1HDAKR5";
        String employeeNavigatorForHelixSkill = "AGGC057NJW4LLASWKA8MSWKA8MXZ8X";
        String CMDBDataMonitorAzureOpenAIGPT41Skill = "AGGIZN0GH37JIATBHBC6TBHBC6PNVI";
        String summarizationSkillAgent = "AGGBZF0OBQ0PJAT0PWLXT0PWLX9QNN";

        String payload = buildUnseedPayload(incidentAgentAssistConversation, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload2 = buildUnseedPayload(incidentAgentAssistResolution, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload3 = buildUnseedPayload(bwfCaseConversationSkill, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload4 = buildUnseedPayload(agentInGlobalContext, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload5 = buildUnseedPayload(bwfGlobalChatSkill, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload6 = buildUnseedPayload(incidentResolutionGeneration, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload7 = buildUnseedPayload(generateCaseResolution, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload8 = buildUnseedPayload(dwpEmployeeNavigatorAgentV1, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload9 = buildUnseedPayload(bwfCaseSummarizationSkill, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload10 = buildUnseedPayload(employeeNavigatorForHelixSkill, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload11 = buildUnseedPayload(CMDBDataMonitorAzureOpenAIGPT41Skill, "000000000000815", "2025-04-18T03:16:13.000Z");
        String payload12 = buildUnseedPayload(summarizationSkillAgent, "000000000000815", "2025-04-18T03:16:13.000Z");

        unseedSkill(incidentAgentAssistConversation, payload);
        unseedSkill(incidentAgentAssistResolution, payload2);
        unseedSkill(bwfCaseConversationSkill, payload3);
        unseedSkill(agentInGlobalContext, payload4);
        unseedSkill(bwfGlobalChatSkill, payload5);
        unseedSkill(incidentResolutionGeneration, payload6);
        unseedSkill(generateCaseResolution, payload7);
        unseedSkill(dwpEmployeeNavigatorAgentV1, payload8);
        unseedSkill(bwfCaseSummarizationSkill, payload9);
        unseedSkill(employeeNavigatorForHelixSkill, payload10);
        unseedSkill(CMDBDataMonitorAzureOpenAIGPT41Skill, payload11);
        unseedSkill(summarizationSkillAgent, payload12);
    	}
    	catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public HelixGPT_Preconditions createHelixGPTBotAndSaveBotIDIntoFile(){
    	try {
         log.info("-------Step:- Login to the IS------");
        String SKILL_NAME = SolQAConstants.SOLQA_HVA_SKILL;

        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.BMC_Chatbot);
        if (innovationSuiteFeatureMethodsPage.isRightTabArrow()) {
    		    log.info("Clicking on Right Arrow button before clicking on Chatbot tab");
    		    innovationSuiteFeatureMethodsPage.clickOnRightTabArrow();
    		}
        	innovationSuiteFeatureMethodsPage.clickOnTabFromBMCChatbot(SolQAConstants.BMC_CHATBOT_TAB_NAME);
            String CHATBOT_NAME = SolQAConstants.HELIXGPT_BOT_NAME;
            innovationSuiteFeatureMethodsPage.searchgrid(CHATBOT_NAME);

        	if (!innovationSuiteFeatureMethodsPage.isChatBotPresent(CHATBOT_NAME)) {
        	    innovationSuiteFeatureMethodsPage.initiateNewChatbot(CHATBOT_NAME);
        	} 
            else {
        	    innovationSuiteFeatureMethodsPage.openBot(CHATBOT_NAME);
                String selectedAssistantServiceProvider = innovationSuiteFeatureMethodsPage.getSelectedAssistantServiceProvider().trim();
                if (!selectedAssistantServiceProvider.equals(CHATBOT_NAME)) {
                    innovationSuiteFeatureMethodsPage.selectAssistantServiceProvider(CHATBOT_NAME);
                    innovationSuiteFeatureMethodsPage.addHelixGPTChatbotSkill(SKILL_NAME, CHATBOT_NAME);
                }
               
        	}

        	// Common launch and botId file logic
        	innovationSuiteFeatureMethodsPage.launchHelixGPTBot();
        	innovationSuiteFeatureMethodsPage.switchToLastWindow();

        	// Get current URL
        	String currentUrl = isDriver.getCurrentUrl();

        	// Extract botId from URL
        	String botId = extractBotId(currentUrl);

        	// Define file path
        	String path = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appHelixGPT;
        	Path filePath = Paths.get(path+File.separator+"HelixGPTBot_id.txt");

            // Check and write logic
            try {
                if (Files.exists(filePath)) {
                    String savedBotId = new String(Files.readAllBytes(filePath)).trim();
                    if (!savedBotId.equals(botId)) {
                        Files.write(filePath, botId.getBytes());
                        log.info("Bot ID updated in file.");
                    } else {
                        log.info("Bot ID already exists and is the same. No update.");
                    }
                } else {
                    Files.write(filePath, botId.getBytes());
                    log.info("Bot ID file created.");
                }
            } catch (java.io.IOException e) {
                log.error("Error reading or writing Bot ID file: " + e.getMessage());
            }
    	}
    	catch (Throwable th){
            th.printStackTrace();
        }
        return this;
        }

    // Utility to extract botId from URL
    private static String extractBotId(String url) {
    	String marker = "/window/";
        int index = url.indexOf(marker);
        if (index != -1) {
            return url.substring(index + marker.length());
        }
        return "UNKNOWN";
    }


    public HelixGPT_Preconditions enableBWFHelixGPTSearchAndGenerateCaseResolution() throws Exception {
        try {
       log.info("Logging in to BWF");
        this.BWFDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(BWFDriver);
        BWFUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        login(BWFUsersData.get("UserName"), BWFUsersData.get("Password"), SolQAConstants.appBWF);
        log.info("Logged in user: " + BWFUsersData.get("UserName"));
		bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);

		bwfFeatureMethods.navigateToSettings();
		bwfFeatureMethods.enterValueInTextField(SolQAConstants.SETTING_NAME);
		bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.SETTING_NAME);
		try {
			bwfFeatureMethods.clickOnForwardArrowInManageLOB();
			bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.APP_CONFIG_TAB_NAME);
			bwfFeatureMethods.clickOnHelixGPTConfigNameAndSetValue(SolQAConstants.CONFIG_NAME, SolQAConstants.True);
			bwfFeatureMethods.clickOnHelixGPTConfigNameAndSetValue(SolQAConstants.GenerateCaseResolutionConfigName, SolQAConstants.True);
		}
		catch(Exception e) {
			BWFDriver.navigate().refresh();
			bwfFeatureMethods.clickOnForwardArrowInManageLOB();
			bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.APP_CONFIG_TAB_NAME);
			bwfFeatureMethods.clickOnHelixGPTConfigNameAndSetValue(SolQAConstants.CONFIG_NAME, SolQAConstants.True);
            bwfFeatureMethods.clickOnHelixGPTConfigNameAndSetValue(SolQAConstants.GenerateCaseResolutionConfigName, SolQAConstants.True);

		}

		bwfFeatureMethods.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
        }
        catch (Throwable th){
            th.printStackTrace();
        }
	   
	  
       log.info("------------END :Enable HelixGPT Search in BWF----------");
       return this;
    }

    public HelixGPT_Preconditions enableSmartITHelixGPTSearch(){
    	try {
        	log.info("logging in into ITSM");
			itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
			itsmDriver=launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
			setDriver(itsmDriver);
			login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
			log.info("Logged in user: " + itsmUserData.get("UserName"));
			log.info("****** Go to Centralized Configuration ******");
			itsmFeatureMethods = new ITSMFeatureMethods();
			itsmFeatureMethods.navigateToCentralizedConfiguration();
			String[] componentMenu = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
			itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
			itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_HELIXGPT_GLOBAL_CHAT, SolQAConstants.TRUE);
    	}
    	catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public HelixGPT_Preconditions enableDataSourcesInHelixGPTAgentStudio() {
        try {
        log.info("This method is to enable Data Sources in HelixGPT Agent Studio");
        log.info("Logging in to Innovation Suite");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATASOURCE);
		innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATASOURCE);
		enableDataSource(SolQAConstants.MICROSOFT_SHAREPOINT_ONLINE);
        enableDataSource(SolQAConstants.WEB);
        enableDataSource(SolQAConstants.HELIX_CUSTOMER_SERVICE_MANAGEMENT);
        enableDataSource(SolQAConstants.SALESFORCE_KNOWLEDGE);
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public HelixGPT_Preconditions enableDataSource(String dataSourceName) {
        try {
            log.info("Enabling Data Source: " + dataSourceName);
            innovationSuiteFeatureMethodsPage.searchUserText(dataSourceName);
            innovationSuiteFeatureMethodsPage.selectRecord();
            if(!innovationSuiteFeatureMethodsPage.getStatusValue().equals(SolQAConstants.ENABLED)) {
                innovationSuiteFeatureMethodsPage.updateStatusToEnabled();
            }
            else {
                log.info("Data Source is already enabled");
                innovationSuiteFeatureMethodsPage.clickOnCancelButton();
            }
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }


    public HelixGPT_Preconditions createSharePointConnectionAndInjectTheData() {
        try {
        log.info("This method is to configure Information Sources in HelixGPT Agent Studio for Microsoft SharePoint");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        Map<Object, String> SharePointTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "SharePoint");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		String SharePointID = innovationSuiteFeatureMethodsPage.addNewConnectionForSharePointAndPickID(SharePointTestData);
		log.info("SharePoint ID: " + SharePointID);
        innovationSuiteFeatureMethodsPage.navigateToWorkspaceTab();
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.triggerDataInjectionJob(SharePointTestData, SharePointID);
        log.info("------------END :Create SharePoint Connection and Inject the Data----------");
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public  HelixGPT_Preconditions createWebConnectionAndInjectTheData() {
        try {
        log.info("This method is to configure Information Sources in HelixGPT Agent Studio for Web");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        Map<Object, String> WebTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "Web");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		String WebID = innovationSuiteFeatureMethodsPage.addNewConnectionForWebAndPickID(WebTestData);
		log.info("Web ID: " + WebID);
        innovationSuiteFeatureMethodsPage.navigateToWorkspaceTab();
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.triggerDataInjectionJob(WebTestData, WebID);
        log.info("------------END :Create Web Connection and Inject the Data----------");
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

   
    public HelixGPT_Preconditions createCSMConnectionAndInjectTheData() {
        try {
        log.info("This method is to configure Information Sources in HelixGPT Agent Studio for Customer Service Management");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        Map<Object, String> CSMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "CSM");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		String CSMID = innovationSuiteFeatureMethodsPage.addNewConnectionForCSMAndPickID(CSMTestData);
		log.info("CSM ID: " + CSMID);
        innovationSuiteFeatureMethodsPage.navigateToWorkspaceTab();
        innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.triggerDataInjectionJob(CSMTestData, CSMID);
        log.info("------------END :Create CSM Connection and Inject the Data----------");
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public HelixGPT_Preconditions injectRKMArticleData() {
        try {
        log.info("This method is to inject RKM Article data into HelixGPT Agent Studio");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        Map<Object, String> ArticleTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "RKMArticle");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.triggerDataInjectionJob(ArticleTestData, "");
        log.info("------------END :Inject Article data of RKM into HelixGPT Agent Studio----------");
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

    public HelixGPT_Preconditions injectHKMArticleData() {
        try {
        log.info("This method is to inject HKM Article data into HelixGPT Agent Studio");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        Map<Object, String> ArticleTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "HKMArticle");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.triggerDataInjectionJob(ArticleTestData, "");
        log.info("------------END :Inject Article data of HKM into HelixGPT Agent Studio----------");
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }


    public HelixGPT_Preconditions injectBWFArticleData() {
        try {
        log.info("This method is to inject BWF Article data into HelixGPT Agent Studio");
        log.info("Logging in to Innovation Suite");
		this.isDriver=launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
        Map<Object, String> ArticleTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "BWFArticle");
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
        innovationSuiteFeatureMethodsPage.selectRecordDefinition(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.clickEditData(SolQAConstants.DATA_CONNECTION_JOB);
        innovationSuiteFeatureMethodsPage.triggerDataInjectionJob(ArticleTestData, "");
        log.info("------------END :Inject Article data of BWF into HelixGPT Agent Studio----------");
        }
        catch (Throwable th){
            th.printStackTrace();
        }
        return this;
    }

  
    public HelixGPT_Preconditions configureSelfServicePortal() throws Exception {
        try{
        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(ITSMdriver);
			login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
			log.info("logged in user: " + configProp.get("adminUserName"));        
			itsmFeatureMethods = new ITSMFeatureMethods();
            itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.LIVE_CHAT, SolQAConstants.ADMINISTRATION_CONSOLE);
            itsmFeatureMethods.modifySelfServicePortal(SolQAConstants.SYSTEM_CONFIGURATION);
            itsmFeatureMethods.enterRequiredDetails();
    }

    catch (Throwable th){
        th.printStackTrace();
    }
       return this;
    }

    @AfterSuite(alwaysRun = true)
    public void quitAllBrowsers() {
        log.info("Closing all HelixGPT precondition browsers");

        try {
            if (isDriver != null) {
                isDriver.quit();
                log.info("Closed Innovation Studio / HelixGPT browser (isDriver).");
                isDriver = null;
            }
        } catch (Exception e) {
            log.warn("Error while closing isDriver: " + e.getMessage());
        }

        try {
            if (BWFDriver != null) {
                BWFDriver.quit();
                log.info("Closed BWF browser (BWFDriver).");
                BWFDriver = null;
            }
        } catch (Exception e) {
            log.warn("Error while closing BWFDriver: " + e.getMessage());
        }

        try {
            if (itsmDriver != null) {
                itsmDriver.quit();
                log.info("Closed ITSM browser (itsmDriver).");
                itsmDriver = null;
            }
        } catch (Exception e) {
            log.warn("Error while closing itsmDriver: " + e.getMessage());
        }
    }

}