package pages.ITSM.InnovationSuite;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import framework.BasePage;
import pages.ITSM.Apex.ApexInnovationSuitePage;
import pages.ITSM.Api.API_CommonOperations;
import pages.ITSM.HelixGPT.HelixGPTAgentStudio;
import util.CommonMethods;
import util.CommonWaits;
import util.HelixGPTUtil;
import util.SolQAConstants;

public class InnovationSuiteFeatureMethodsPage extends BasePage {

	public static final Logger log = Logger.getLogger(InnovationSuiteFeatureMethodsPage.class.getName());
	InnovationSuite innovationSuite = new InnovationSuite();
	KnowledgeManagementPage knowledgeManagementPage = new KnowledgeManagementPage();
	InnovationSuiteTicketConsolePage innovationSuiteTicketConsolePage = new InnovationSuiteTicketConsolePage();
	InnovationSuiteAssetConsolePage innovationSuiteAssetConsolePage = new InnovationSuiteAssetConsolePage();
	InnovationSuiteApplications innovationSuiteApplications = new InnovationSuiteApplications();
	HelixGPTAgentStudio helixGPTAgentStudio = new HelixGPTAgentStudio();
	HelixGPTUtil helixGPTUtil = new HelixGPTUtil();
	CommonWaits commonWaits = new CommonWaits();
	CommonMethods commonMethods = new CommonMethods();
	API_CommonOperations apiCommonOperations = new API_CommonOperations();
	ApexInnovationSuitePage apexInnovationSuitePage = new ApexInnovationSuitePage();
	CSMBundle csmBundle = new CSMBundle();


	public void navigateToAdminPage() {
		innovationSuite.navigateToAdminPage();
	}

	public void navigateToConfiguration(String parentConfigName) {
		knowledgeManagementPage.navigateToParentConfiguration(parentConfigName);
	}

	public void configurationComaroundIntegrations(Map<Object, String> isTestData) {
		knowledgeManagementPage.configurationComaroundIntegrations(isTestData);
	}

	public void setAccessMappingComAround(String CompanyName) {
		knowledgeManagementPage.setAccessMappingComAround(CompanyName);
	}

	public void applyFilterAndSearchTicket(String ticketType, String ticketID) {
		innovationSuiteTicketConsolePage.applyFilterAndSearchTicket(ticketType, ticketID);
	}

	public void clearFilters() {
		innovationSuiteTicketConsolePage.ClearFiler();
	}
	
	public void applyFilterAndSearchAsset(String assetType, String status) {
		innovationSuiteAssetConsolePage.applyFilterAndSearchAsset(assetType, status);
	}
	
	public void searchByTicketId(String ticketId) {
		innovationSuiteTicketConsolePage.searchByTicketId(ticketId);
	}

	public void searchCIInAssetConsole(String ciName) {
		innovationSuiteAssetConsolePage.searchCIInAssetConsole(ciName);
	}
	
	public void validateCIInSearchResults(String ciName) {
		innovationSuiteAssetConsolePage.validateCIInSearchResults(ciName);
	}
	
	public void clickThreeDotsAndViewRelatedCI() {
		innovationSuiteAssetConsolePage.clickThreeDotsAndViewRelatedCI();
	}
	
	public void validateRelatedCIsModalDisplayed() {
		innovationSuiteAssetConsolePage.validateRelatedCIsModalDisplayed();
	}
	
	public void validateThreeButtonsVisible() {
		innovationSuiteAssetConsolePage.validateThreeButtonsVisible();
	}
	
	public void clickDynamicServiceModelButton() {
		innovationSuiteAssetConsolePage.clickDynamicServiceModelButton();
	}
	
	public void clickDSMNodeTopologyButton() {
		innovationSuiteAssetConsolePage.clickDSMNodeTopologyButton();
	}
	
	public void clickCIExplorerButton() {
		innovationSuiteAssetConsolePage.clickCIExplorerButton();
	}
	
	public void validateDiscoveryPageLaunched(String discoveryUrl) {
		innovationSuiteAssetConsolePage.validateDiscoveryPageLaunched(discoveryUrl);
	}
	
	public void validateCMDBExplorerWidgetOpened(String urlKeyword) {
		innovationSuiteAssetConsolePage.validateCMDBExplorerWidgetOpened(urlKeyword);
	}

	public void openFirstTicket(String tiketId) {
		innovationSuiteTicketConsolePage.openFirstTicket(tiketId);
	}

	public void addVisibleColumn(String columnName) {
		innovationSuiteTicketConsolePage.addVisibleColumn(columnName);
	}

	public String getColumnValue(String columnName) {
		return innovationSuiteTicketConsolePage.getColumnValue(columnName);
	}

	public void clickOnEditButton() {
		innovationSuiteTicketConsolePage.clickOnEditButton();
	}

	public void clickOnSettings() {
		innovationSuiteApplications.clickOnSettings();
	}

	public void clickOnConfiguration(String configurationName) {
		innovationSuiteApplications.clickOnConfiguration(configurationName);
	}

	public void enableConfiguration(String configurationName) {
		innovationSuiteApplications.selectConfiguration(configurationName);
		innovationSuiteApplications.clickOnEnableButton();
	}
	
	public void setDiscoveryUrl(String discoveryUrl) {
		innovationSuiteApplications.setDiscoveryUrl(discoveryUrl);
	}

	public void navigateToCreateNewIncident() {
		innovationSuite.navigateToCreateNewIncident();
	}
	
	public void navigateToCreateNewChangeRequest() {
		innovationSuite.navigateToCreateNewChangeRequest();
	}

	public void fillIncidentTemplate(Map<Object, String> itsmTestData) {
		innovationSuite.fillIncidentTemplate(itsmTestData);
	}
	
	public void fillChangeTemplate(Map<Object, String> itsmTestData) {
		innovationSuite.fillChangeTemplate(itsmTestData);
	}
	
	public void saveChange() {
		innovationSuite.saveChange();
	}

	public void saveIncident() {
		innovationSuite.saveIncident();
	}

	public String getIncidentNumber() {
		return innovationSuite.getIncidentNumber();
	}
	
	public String getChangeNumber() {
		return innovationSuite.getChangeNumber();
	}

	public void navigateToTicketConsole() {
		innovationSuite.navigateToTicketConsole();
	}

	public void navigateToTicketConsoleThroughUrl() {
		innovationSuite.navigateToTicketConsoleThroughUrl();
	}
	
	public void navigateToAssetConsole() {
		innovationSuiteApplications.navigateToAssetConsole();
	}

	public void editContact(String contact) {
		innovationSuite.editContact(contact);
		saveIncident();
	}

	public void verifyLogin() {
		innovationSuite.verifyInnovationSuitePageLaunchedSuccessfully();
	}

	public void verifyInnovationSuitePageLaunchedSuccessfully() {
		innovationSuite.verifyInnovationSuitePageLaunchedSuccessfully();
	}

	public void navigateToBundle(String bundleName) {
		innovationSuite.navigateToBundle(bundleName);
	}

	public void clickVisitDeployedApplicationLink() {
		helixGPTAgentStudio.clickVisitDeployedApplicationLink();
	}

	public void selectApplication(String applicationName) {
		helixGPTAgentStudio.selectApplication(applicationName);
	}

	public void selectSkill(String skillName) {
		helixGPTAgentStudio.selectSkill(skillName);
	}

	public void setModelValues(String label, String value) {
		helixGPTAgentStudio.setModelValues(label, value);
	}

	public boolean getFieldValues(String Field, String Value) {
		return helixGPTAgentStudio.getFieldValues(Field, Value);
	}

	public void selectSkillSearchSetting(String informationProvider) {
		helixGPTAgentStudio.selectSkillSearchSetting(informationProvider);
	}

	public void verifyLoginToHelixGPTBot() {
		helixGPTAgentStudio.verifyLoginToHelixGPTBot();
	}

	public void sendMessage(String message) {
		helixGPTAgentStudio.sendMessage(message);
	}

	public String getAllGPTResponseString() {
		return helixGPTAgentStudio.getAllGPTResponseString();
	}

	public String getAllSkillGPTResponseString() {
		return helixGPTAgentStudio.getAllSkillGPTResponseString();
	}

	public void switchToHelixGPTPage(WebDriver webDriver) {
		helixGPTAgentStudio.switchToHelixGPTPage(webDriver);
	}

	public void clickOnThumbIcon(String icon) {
		helixGPTAgentStudio.clickOnThumbIcon(icon);
	}

	public void enterFeedbackAndSubmit(String feedback) {
		helixGPTAgentStudio.enterFeedbackAndSubmit(feedback);
	}

	public void verifyIsThumbsIconPresent(String icon) {
		helixGPTAgentStudio.verifyIsThumbsIconPresent(icon);
	}

	public void enableAndPublishChatbotWithGPTProvider(String ApplicationName, String SkillName, String serviceName) {
		helixGPTAgentStudio.enableAndPublishChatbotWithGPTProvider(ApplicationName, SkillName, serviceName);
	}

	public String serviceRequestQuestion(String question, Map<Object, String> TestData) {
		return helixGPTAgentStudio.serviceRequestQuestion(question, TestData);
	}

	public String getRequestIdFromLastResponse() {
		return helixGPTAgentStudio.getRequestIdFromLastresponse();
	}

	public boolean verifyRequestOpenedInDWP() {
		return helixGPTAgentStudio.verifyRequestOpenedInDWP();
	}

	public List<String> getPresentBundleNames() {
		return innovationSuite.getPresentBundleNames();
	}

	public void installApplication(String application) {
		innovationSuite.installApplication(application);
	}

	public void navigateRequiredISURL(String URL) {
		innovationSuite.navigateRequiredISURL(URL);
	}

	public void clickOnPalette() {
		innovationSuite.clickOnPalette();
	}

	public String componentPropertiesSkill() {
		return innovationSuite.componentPropertiesSkill();
	}

	public void clickOnPreviewButton() {
		innovationSuite.clickOnPreviewButton();
	}

	public void waitForNumberOfWindows(int expectedWindowCount) {
		commonWaits.waitForNumberOfWindows(getDriver(), expectedWindowCount, 30);
	}

	public void switchToLastWindow() {
		commonWaits.switchToLastWindow();
	}

	public void waitForUrlToBe(String url) {
		commonWaits.waitForUrlToBe(getDriver(), url, 30);
	}

	public void waitForSometime() {
		commonWaits.waitForSometime(5000);
	}

	public String getIDOfImportedSkill(String skillName) {
		return helixGPTAgentStudio.getIDOfImportedSkill(skillName);
	}

	public void selectRecordDefinition(String definitionName) {
		innovationSuite.selectRecordDefinition(definitionName);
	}

	public void clickEditData(String definationName) {
		innovationSuite.clickEditData(definationName);
	}

	public boolean selectRecord() {
		return innovationSuite.selectRecord();
	}

	public String getID() {
		return innovationSuite.getID();
	}

	public void searchUserText(String searchText) {
		innovationSuite.searchUserText(searchText);
	}

	public void clickSearchFilterButton() {
		innovationSuite.clickSearchFilterButton();
	}

	public void setValueOfField(String valueName, String value) {
		innovationSuite.setValueOfField(valueName, value);
	}

	public boolean isPromptPresent(String promptKey) {
		return helixGPTAgentStudio.isPromptPresent(promptKey);
	}

	public void linkNewPrompt(String promptKey) {
		helixGPTAgentStudio.linkNewPrompt(promptKey);
	}

	public void selectSkillPrompt(String promptKey) {
		helixGPTAgentStudio.selectSkillPrompt(promptKey);
	}

	public void selectModelAndProviderForSkill(String Application, String skillName, String provider, String model) {
		selectApplication(Application);
		selectSkill(skillName);
		setModelValues(SolQAConstants.PROVIDER, provider);
		Assert.assertTrue(getFieldValues(SolQAConstants.PROVIDER, provider));
		setModelValues(SolQAConstants.MODEL, model);
		Assert.assertTrue(getFieldValues(SolQAConstants.MODEL, model), "Model name not matched");
	}

	public String copySubTenantIDFromDigitalWorkspace() {
		navigateToBundle(SolQAConstants.DIGITAL_WORKSPACE);
		selectRecordDefinition(SolQAConstants.SUBTENANT);
		clickEditData(SolQAConstants.SUBTENANT);
		Assert.assertTrue(selectRecord(), "Record selection failed");
		return getID();
	}

	public void setSkillIDForDigitalWorkspace(String SkillID) {
		navigateToBundle(SolQAConstants.DIGITAL_WORKSPACE);
		selectRecordDefinition(SolQAConstants.CONFIGURATION_PARAMS);
		clickEditData(SolQAConstants.CONFIGURATION_PARAMS);
		searchUserText(SolQAConstants.SkillID);
		selectRecord();
		setValueOfField(SolQAConstants.VALUE, SkillID);
	}

	public void clickTestButtonForSkill(String skillName) {
		helixGPTAgentStudio.clickTestButtonForSkill(skillName);
	}

	public void verifySkillIDForTestSkill(String expectedSkillID) {
		helixGPTAgentStudio.verifySkillIDForTestSkill(expectedSkillID);
	}

	public void clickOnNewTopic() {
		helixGPTAgentStudio.clickOnNewTopic();
	}

	public void verifyNewTopicisClearingAllChat() {
		helixGPTAgentStudio.verifyNewTopicisClearingAllChat();
	}

	public void clickOnTestLog() {
		helixGPTAgentStudio.clickOnTestLog();
	}

	public void selectAllSkillsFromSearchSetting() {
		helixGPTAgentStudio.selectAllSkillsFromSearchSetting();
	}

	public void importSkill(String skillName) {
		helixGPTAgentStudio.importSkill(skillName);
	}

	public boolean isSkillPresent(String skillName) {
		return helixGPTAgentStudio.isSkillPresent(skillName);
	}

	public boolean isRightTabArrow() {
		return innovationSuite.isRightTabArrow();
	}

	public void clickOnRightTabArrow() {
		innovationSuite.clickOnRightTabArrow();
	}

	public void clickOnTabFromBMCChatbot(String TabName) {
		innovationSuite.clickOnTabFromBMCChatbot(TabName);
	}

	public boolean isChatBotPresent(String CHATBOT_NAME) {
		return innovationSuite.isChatBotPresent(CHATBOT_NAME);
	}

	public void searchgrid(String CHATBOT_NAME) {
		innovationSuite.searchGrid(CHATBOT_NAME);
	}

	public void initiateNewChatbot(String CHATBOT_NAME) {
		innovationSuite.initiateNewChatbot(CHATBOT_NAME);
	}

	public void addHelixGPTChatbotSkill(String SKILL_NAME, String CHATBOT_NAME) {
		innovationSuite.addHelixGPTChatbotSkill(SKILL_NAME, CHATBOT_NAME);
	}

	public void selectAssistantServiceProvider(String CHATBOT_NAME) {
		innovationSuite.selectAssistantServiceProvider(CHATBOT_NAME);
	}

	public void openBot(String BotName) {
		innovationSuite.openBot(BotName);
	}

	public void launchHelixGPTBot() {
		innovationSuite.launchHelixGPTBot();
	}

	public void verifyHelixGPTResult(String context, String situationSummaryTxt, String expected, String output) {
		helixGPTUtil.verifyHelixGPTResult(context, situationSummaryTxt, expected, output);
	}

	public void openGPTArticle(String article) {
		helixGPTAgentStudio.openArticle(article);
	}

	public boolean verifyProviderNameInURL(String providerName) {
		return helixGPTAgentStudio.verifyProviderNameInURL(providerName);
	}

	public void switchToTabByIndex(WebDriver driver, int index) {
		commonWaits.switchToTabByIndex(driver, index);
	}

	public void navigateToSettings() {
		helixGPTAgentStudio.navigateToSettings();
	}

	public void enterValueInTextField(String value) {
		helixGPTAgentStudio.enterValueInTextField(value);
	}

	public void clickOnSearchedAdminSetting(String settingName) throws Exception {
		helixGPTAgentStudio.clickOnSearchedAdminSetting(settingName);
	}

	public boolean isConnectionPresent(String connectionName) {
		return helixGPTAgentStudio.isConnectionPresent(connectionName);
	}

	public String addNewConnectionForSharePointAndPickID(Map<Object, String> connectionData) {
		return innovationSuite.addNewConnectionForSharePointAndPickID(connectionData);
	}

	public void clearExistingFilter() {
		innovationSuiteTicketConsolePage.clearFilter();
	}
	
	public void updateBWFKnowledgeSource() {
		apexInnovationSuitePage.updateBWFKnowledgeSource();
	}

	public void unLinkPrompt(String promptKey) {
		helixGPTAgentStudio.unLinkPrompt(promptKey);
	}

	public void validateFormHeading(String expectedHeading) {
		innovationSuite.validateFormHeading(expectedHeading);
	}

	public void validateGainsightSettingsHeading(String expectedHeading) {
		innovationSuite.validateGainsightSettingsHeading(expectedHeading);
	}

	public void enableGainsightTogglesIfNotEnabled(String deploymentType) {
		log.info("Enabling Gainsight toggles if not enabled");
		innovationSuite.enableGainsightToggleIfNotEnabled();
		innovationSuite.enableLoadGainsightFromBmcItToggleIfNotEnabled();
		innovationSuite.selectDeploymentType(deploymentType);
		innovationSuite.environmentType("QA");
		innovationSuite.saveGainsightSettingsIfEnabled();
		log.info("Completed enabling Gainsight toggles and saving settings");
	}

	public void searchInFormSearchBox(String searchText) {
		innovationSuite.searchInFormSearchBox(searchText);
	}

	public void validateSearchResult(String expectedResult) {
		innovationSuite.validateSearchResult(expectedResult);
	}

	public void clickOnSearchResultLink() {
		innovationSuite.clickOnSearchResultLink();
	}

	public void validateEditRecordModalDisplayed() {
		innovationSuite.validateEditRecordModalDisplayed();
	}

	public void enableFeatureFlagIfNotEnabled(String searchText) {
		log.info("Enabling Feature flag for: " + searchText);
		log.info("Searching in form search box for: " + searchText);
		innovationSuite.searchInFormSearchBox(searchText);
		log.info("Validating search result for: " + searchText);
		innovationSuite.validateSearchResult(searchText);
		log.info("Clicking on search result link");
		innovationSuite.clickOnSearchResultLink();
		log.info("Validating Edit Record modal is displayed");
		innovationSuite.validateEditRecordModalDisplayed();
		log.info("Enabling feature flag if not enabled");
		innovationSuite.enableFeatureFlagIfNotEnabled();
		log.info("Closing modal by clicking Save or Cancel button");
		innovationSuite.clickSaveOrCancelButton();
		log.info("Completed enabling feature flag for: " + searchText);
	}

	public void clickSaveOrCancelButton() {
		log.info("Closing modal by clicking Save or Cancel button");
		innovationSuite.clickSaveOrCancelButton();
	}

	public void enableBundleIdMappingIfNotEnabled(String searchText) {
		log.info("Enabling Bundle ID mapping for: " + searchText);
		log.info("Searching in form search box for: " + searchText);
		innovationSuite.searchInFormSearchBox(searchText);
		log.info("Validating search result for: " + searchText);
		innovationSuite.validateSearchResult(searchText);
		log.info("Enabling mapping if not already enabled");
		innovationSuite.enableMappingIfNotAlreadyEnabled();
		log.info("Closing modal by clicking Save or Cancel button");
		innovationSuite.clickSaveOrCancelButton();
		log.info("Completed enabling Bundle ID mapping for: " + searchText);
	}

	public void enableMappingIfNotAlreadyEnabled() {
		log.info("Checking and enabling mapping if not already enabled");
		innovationSuite.enableMappingIfNotAlreadyEnabled();
	}

	public void updateGlobalContextMappingAndSave(String searchText, String globalContextValue) {
		log.info("Updating global context mapping for: " + searchText);
		log.info("Searching and opening global context mapping");
		innovationSuite.searchAndOpenGlobalContextMapping(searchText);
		log.info("Replacing global context value");
		innovationSuite.replaceGlobalContextValue(globalContextValue);
		log.info("Closing modal by clicking Save button");
		innovationSuite.clickSaveOrCancelButton();
		log.info("Completed updating global context mapping for: " + searchText);
	}

	public void verifyAutomatedAssetAssignmentPageLaunched() {
		innovationSuite.verifyAutomatedAssetAssignmentPageLaunched();
	}

	public void clickAssetAutomationsTab() {
		innovationSuite.clickAssetAutomationsTab();
	}

	public void clickCreateAutomation() {
		innovationSuite.clickCreateAutomation();
	}

	public void verifyCreateAutomationPageDisplayed() {
		innovationSuite.verifyCreateAutomationPageDisplayed();
	}

	public String fillAutomationRuleTemplate(Map<Object, String> automationData) {
		log.info("Filling Automation Rule Template with provided data");
		return innovationSuite.fillAutomationRuleTemplate(automationData);
	}

	public String createAutomationRule(Map<Object, String> automationData) {
		log.info("Creating Automation Rule with provided data");
		String uniqueAutomationName = fillAutomationRuleTemplate(automationData);
		log.info("Saving Automation Rule");
		innovationSuite.clickSaveAutomation();
		log.info("Closing Automation details popup if present");
		closeAutomationDetailsPopupIfPresent();
		log.info("Verifying Automation entry in list");
		innovationSuite.verifyAutomationEntryInList(uniqueAutomationName);
		log.info("Automation Rule created and verified successfully");
		return uniqueAutomationName;
	}

	public void closeAutomationDetailsPopupIfPresent() {
		innovationSuite.closeAutomationDetailsPopupIfPresent();
	}

	public void enableAutomationRule(String automationName) {
		log.info("Enabling automation rule: " + automationName);
		log.info("Selecting automation entry checkbox");
		innovationSuite.selectAutomationEntry(automationName);
		log.info("Clicking Enable button");
		innovationSuite.clickEnableButton();
		log.info("Verifying automation status is Active");
		innovationSuite.verifyAutomationStatusActive(automationName);
		log.info("Automation rule enabled successfully");
	}

	public void runAutomationJob(String automationName) {
		log.info("Running automation job for: " + automationName);
		log.info("Selecting automation entry checkbox");
		innovationSuite.selectAutomationEntry(automationName);
		log.info("Clicking Add automation job button");
		innovationSuite.clickAddAutomationJobButton();
		log.info("Verifying Create job popup is displayed");
		innovationSuite.verifyCreateJobPopupDisplayed(automationName);
		log.info("Clicking Preview button");
		innovationSuite.clickPreviewButton();
		log.info("Selecting asset in preview");
		innovationSuite.selectAssetInPreview();
		log.info("Clicking Run Job button");
		innovationSuite.clickRunJobButton();
		log.info("Verifying job created toast message");
		innovationSuite.verifyJobCreatedToastMessage();
		log.info("Automation job run completed successfully");
	}

	public void verifyAutomationJobInConsole() {
		log.info("Clicking Automation job console link");
		innovationSuite.clickAutomationJobConsoleLink();
		log.info("Verifying job status is Completed");
		innovationSuite.verifyJobStatusCompleted();
		log.info("Automation job status verified as Completed");

	}

	public void selectAgent(String agentName) {
		helixGPTAgentStudio.selectAgent(agentName);
	}

	public void selectModelAndProviderForAgents(String Application, String agentName, String provider, String model) {
		selectApplication(Application);
		selectAgent(agentName);
		setModelValues(SolQAConstants.PROVIDER, provider);
		Assert.assertTrue(getFieldValues(SolQAConstants.PROVIDER, provider));
		setModelValues(SolQAConstants.MODEL, model);
		Assert.assertTrue(getFieldValues(SolQAConstants.MODEL, model), "Model name not matched");
	}
	
	public String copyInsightFinderSupervisorSkillId() {
		navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
		selectRecordDefinition(SolQAConstants.SKILL);
		clickEditData(SolQAConstants.SKILL);
		searchUserText(SolQAConstants.INSIGHT_FINDER_SUPERVISOR_SKILL);
		selectRecord();
		Assert.assertTrue(selectRecord(), "Record selection failed");
		return getID();
	}

	public void clickDataMonitorTab() {
		log.info("Clicking on Data Monitor tab");
		innovationSuite.clickDataMonitorTab();
		log.info("Data Monitor tab clicked successfully");
	}

	public void verifyDataMonitorColumns() {
		log.info("Verifying Data Monitor page is displayed");
		innovationSuite.verifyDataMonitorPageDisplayed();
		log.info("Verifying Data Monitor columns: Name, CI Matches, Last Run, Priority, Status");
		innovationSuite.verifyDataMonitorColumns();
		log.info("Verifying Data Monitor rules list is displayed");
		innovationSuite.verifyDataMonitorRulesListDisplayed();
		log.info("Data Monitor columns and rules list verified successfully");
	}

	public void verifyDataMonitorTiles() {
		log.info("Verifying Data Monitor tiles: Total rules, Rules with open issues, Active rules, Inactive rules");
		innovationSuite.verifyDataMonitorTiles();
		log.info("Data Monitor tiles verified successfully");
	}

	public String createMonitoringRule(String name, String description,String classType,  String className, String frequency, String priority, String expressionField, String expressionOperator, String expressionValue) {
		String uniqueRuleName = name + "_" + commonMethods.randomString(5);
		log.info("Creating new monitoring rule: " + uniqueRuleName);
		log.info("Clicking on New monitoring rule button");
		innovationSuite.clickNewMonitoringRuleButton();
		log.info("Verifying New monitoring rule panel is displayed");
		innovationSuite.verifyNewMonitoringRulePanelDisplayed();
		log.info("Entering monitoring rule name: " + uniqueRuleName);
		innovationSuite.enterMonitoringRuleName(uniqueRuleName);
		log.info("Entering monitoring rule description: " + description);
		innovationSuite.enterMonitoringRuleDescription(description);
		log.info("Selecting monitoring rule frequency: " + frequency);
		innovationSuite.selectMonitoringRuleFrequency(frequency);
		log.info("Selecting monitoring rule priority: " + priority);
		innovationSuite.selectMonitoringRulePriority(priority);
		log.info("Selecting monitoring rule class type: " + classType);
		innovationSuite.selectMonitoringRuleClassType(classType);
		log.info("Selecting monitoring rule class: " + className);
		innovationSuite.selectMonitoringRuleClass(className);
		log.info("Adding monitoring rule expression: " + expressionField + " " + expressionOperator + " " + expressionValue);
		innovationSuite.addMonitoringRuleExpression(expressionField, expressionOperator, expressionValue);
		log.info("Clicking Save button");
		innovationSuite.clickMonitoringRuleSaveButton();
		log.info("Verifying monitoring rule created successfully");
		return uniqueRuleName;
	}
	
	public String createMonitoringRuleWithFrequency(Map<Object, String> testData, String frequency) {
		String ruleName = createMonitoringRule(
			testData.get("MonitoringRuleName") + "_" + frequency,
			testData.get("MonitoringRuleDescription") + " - " + frequency,
			testData.get("MonitoringRuleClassType"),
			testData.get("MonitoringRuleClass"),
			frequency,
			SolQAConstants.MONITORING_RULE_PRIORITY,
			SolQAConstants.EXPRESSION_FIELD_NAME,
			SolQAConstants.EXPRESSION_OPERATOR_NOT_EQUAL,
			SolQAConstants.EXPRESSION_VALUE_NULL
		);
		verifyMonitoringRuleInGrid(ruleName);
		return ruleName;
	}

	public void verifyMonitoringRuleInGrid(String ruleName) {
		log.info("Verifying monitoring rule in grid: " + ruleName);
		innovationSuite.verifyMonitoringRuleInGrid(ruleName);
		log.info("Monitoring rule verified in grid: " + ruleName);
	}

	public String editMonitoringRule(String ruleName, String editedDescription) {
		log.info("Editing monitoring rule: " + ruleName);
		log.info("Clicking kebab menu (3-dot) for rule");
		innovationSuite.clickMonitoringRuleKebabMenu(ruleName);
		log.info("Clicking Edit option from dropdown");
		innovationSuite.clickEditOptionFromDropdown();
		log.info("Verifying Edit monitoring rule panel is displayed");
		innovationSuite.verifyEditMonitoringRulePanelDisplayed();
		log.info("Updating monitoring rule name with random string");
		String updatedName = innovationSuite.updateMonitoringRuleName(ruleName);
		log.info("Updating monitoring rule description: " + editedDescription);
		innovationSuite.updateMonitoringRuleDescription(editedDescription);
		log.info("Clicking Save button");
		innovationSuite.clickMonitoringRuleSaveButton();
		log.info("Monitoring rule edited successfully. Updated name: " + updatedName);
		return updatedName;
	}

	public void disableMonitoringRule(String ruleName) {
		log.info("Disabling monitoring rule: " + ruleName);
		log.info("Selecting monitoring rule checkbox");
		innovationSuite.selectMonitoringRuleCheckbox(ruleName);
		log.info("Clicking Disable button");
		innovationSuite.clickDisableMonitoringRuleButton();
		log.info("Verifying monitoring rule status is Inactive");
		innovationSuite.verifyMonitoringRuleStatusInactive(ruleName);
		log.info("Monitoring rule disabled successfully: " + ruleName);
	}

	public void enableMonitoringRule(String ruleName) {
		log.info("Enabling monitoring rule: " + ruleName);
		log.info("Selecting monitoring rule checkbox");
		innovationSuite.selectMonitoringRuleCheckbox(ruleName);
		log.info("Clicking Enable button");
		innovationSuite.clickEnableMonitoringRuleButton();
		log.info("Verifying monitoring rule status is Active");
		innovationSuite.verifyMonitoringRuleStatusActive(ruleName);
		log.info("Monitoring rule enabled successfully: " + ruleName);
	}

	public void verifyMonitoringRuleStatusInactive(String ruleName) {
		log.info("Verifying monitoring rule status is Inactive: " + ruleName);
		innovationSuite.verifyMonitoringRuleStatusInactive(ruleName);
	}

	public void verifyMonitoringRuleStatusActive(String ruleName) {
		log.info("Verifying monitoring rule status is Active: " + ruleName);
		innovationSuite.verifyMonitoringRuleStatusActive(ruleName);
	}

	public void runMonitoringRuleOnDemand(String ruleName) {
		log.info("Running monitoring rule on-demand: " + ruleName);
		innovationSuite.clickMonitoringRuleKebabMenu(ruleName);
		log.info("Clicked kebab menu for rule: " + ruleName);
		innovationSuite.clickRunNowOption();
		log.info("Clicked Run now option");
		innovationSuite.verifyRuleRunSuccessToast();
		log.info("Verified rule run success toast");
	}
	
	public void clickRefreshButton() {
		log.info("Clicking refresh button on Data Monitor grid");
		log.info("Waiting for rule execution to complete...");
		commonWaits.waitUntilNextTwoMinutes();
		innovationSuite.clickRefreshButton();
		log.info("Refresh button clicked and grid refreshed");
	}
	
	public void verifyMonitoringRuleCompletionStatus(String ruleName) {
		log.info("Verifying monitoring rule completion status: " + ruleName);
		innovationSuite.verifyMonitoringRuleCompletionStatus(ruleName);
		log.info("Monitoring rule completion status verified as Completed");
	}
	
	public void viewMonitoringRuleResults(String ruleName) {
		log.info("Viewing monitoring rule results: " + ruleName);
		innovationSuite.clickMonitoringRuleKebabMenu(ruleName);
		log.info("Clicked kebab menu for rule: " + ruleName);
		innovationSuite.clickViewResultsOption();
		log.info("Clicked View results option");
		innovationSuite.verifyCIMatchesResultsPage(ruleName);
		log.info("Verified CI matches results page");
	}
	
	public void validateViewInsightResult(String ruleName, List<String> analysisSummaryTexts, Boolean shouldViewInsightsBeVisible) {
		log.info("Validating View Insight result for rule: " + ruleName + " (shouldViewInsightsBeVisible: " + shouldViewInsightsBeVisible + ")");
		innovationSuite.validateViewInsightResult(ruleName, analysisSummaryTexts, shouldViewInsightsBeVisible);
		
		if (shouldViewInsightsBeVisible) {
			log.info("View Insight validation completed successfully for rule: " + ruleName);
		} else {
			log.info("View Insight option verified as not available/enabled for rule: " + ruleName);
		}
	}
	
	public String clickCIResultAndVerifyCrossLaunch() {
		log.info("Clicking CI result and verifying cross-launch to CMDB");
		String ciName = innovationSuite.clickFirstCIResultLink();
		log.info("Clicked CI link: " + ciName);
		innovationSuite.verifyCrossLaunchedCMDBTab();
		log.info("Switching back to Data Monitor tab");
		commonWaits.switchToTabByIndex(1);
		log.info("Closing CI Box");
		innovationSuite.closeCIBox();
		return ciName;
	}
	
	public void deleteMonitoringRule(String ruleName) {
		log.info("Deleting monitoring rule: " + ruleName);
		innovationSuite.selectMonitoringRuleCheckbox(ruleName);
		log.info("Selected monitoring rule checkbox");
		innovationSuite.clickDeleteMonitoringRuleButton();
		log.info("Clicked Delete button");
		innovationSuite.confirmDeleteMonitoringRule();
		log.info("Confirmed deletion");
		innovationSuite.verifyMonitoringRuleDeletedSuccessToast();
		log.info("Monitoring rule deleted successfully: " + ruleName);
	}
	
	public void verifyMonitoringRuleNotInGrid(String ruleName) {
		log.info("Verifying monitoring rule is removed from grid: " + ruleName);
		innovationSuite.verifyMonitoringRuleNotInGrid(ruleName);
		log.info("Monitoring rule is no longer in grid: " + ruleName);
	}
	
	public void verifyMonitoringRuleCompletionStatusWithRefresh(String ruleName) {
		log.info("Verifying monitoring rule completion status: " + ruleName);
		innovationSuite.verifyMonitoringRuleCompletionStatusWithRefresh(ruleName);
		log.info("Monitoring rule completion status verified as Completed");
	}

	public String getSelectedAssistantServiceProvider() {
		return innovationSuite.getSelectedAssistantServiceProvider();
	}
	
	public InnovationSuiteFeatureMethodsPage clickProcessesTab() {
		log.info("Clicking on Processes tab");
		innovationSuite.clickProcessesTab();
		log.info("Processes tab clicked successfully");
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage clickRecordTab() {
		log.info("Clicking on Records tab");
		innovationSuite.clickRecordTab();
		log.info("Records tab clicked successfully");
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage createDefinitionViaHelixGPT(String processType, String processDetails, String expectedResponse) {
		innovationSuite.createDefinitionViaHelixGPT(processType, processDetails, expectedResponse);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enableProcess(String processName) {
		log.info("Enabling process: " + processName);
		innovationSuite.enableProcess(processName);
		log.info("Process enabled successfully: " + processName);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage runCreateRecordProcess(String recordName, String processName) {
		log.info("Running process: " + processName);
		String assignee = "Assignee" + commonMethods.randomString(5);
		String description = "Description_" + commonMethods.randomString(5);
		innovationSuite.runProcess(processName, null, description, null, null);
		log.info("Process run completed successfully: " + processName);
		innovationSuite.validateRecordPresence(recordName, description, true);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage runUpdateRecordProcess(String recordName, String processName, String studentID) {
		innovationSuite.clickProcessesTab();
		log.info("Running process: " + processName);
		String updatedDescription = "UpdatedDescription_" + commonMethods.randomString(5);
		innovationSuite.runProcess(processName, studentID, updatedDescription, null, null);
		log.info("Process run completed successfully: " + processName);
		innovationSuite.validateRecordPresence(recordName, updatedDescription, true);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage runDeleteRecordProcess(String recordName, String processName, String recordID) {
		log.info("Running process: " + processName);
		innovationSuite.runProcess(processName, null, null, null, recordID);
		log.info("Process run completed successfully: " + processName);
		innovationSuite.validateRecordPresence(recordName, recordID, false);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage runGetRecordProcess(String processName, String studentRecordInstanceIdInput) {
		log.info("Running process: " + processName);
		innovationSuite.runProcess(processName, null, null, studentRecordInstanceIdInput, null);
		log.info("Process run completed successfully: " + processName);
		return this;
	}
	
	public String getFirstRecordId(String recordName) {
		log.info("Getting first Record ID");
		return innovationSuite.getFirstRecordId(recordName);
	}
	
	public InnovationSuiteFeatureMethodsPage createCustomApplication(String name, String shortName, String groupId) {
		log.info("Creating custom application: " + name);
		innovationSuite.createCustomApplication(name, shortName, groupId);
		
		log.info("Navigating back to 'Workspace' tab");
		innovationSuite.navigateToWorkspace();
		log.info("Custom application created successfully: " + name);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage verifyCustomApplicationCreated(String applicationName) {
		log.info("Verifying custom application created: " + applicationName);
		innovationSuite.verifyCustomApplicationCreated(applicationName);
		log.info("Custom application verified successfully: " + applicationName);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage validateRecordFields(String recordName, String... customFields) {
		log.info("Verifying OOTB record fields for record: " + recordName);
		if (customFields != null && customFields.length > 0) {
			log.info("Including " + customFields.length + " additional custom fields in validation");
		}
		innovationSuite.validateRecordFields(recordName, customFields);
		log.info("Validation complete for OOTB record fields for record: " + recordName);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage deleteRecords(String recordName) {
		log.info("Deleting Record: " + recordName);
		innovationSuite.deleteRecords(recordName);
		log.info("Deleted record: " + recordName);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage deleteProcesses(String processName) {
		log.info("Deleting Process: " + processName);
		innovationSuite.deleteProcesses(processName);
		log.info("Deleted Process: " + processName);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage configurationDWPIntegrations(Map<Object, String> isTestData) {
		knowledgeManagementPage.configurationDWPIntegrations(isTestData);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage verifyCSMLoginSuccess() {
		csmBundle.verifyCSMLoginSuccess();
		return this;
	}

	public boolean verifyCSMLoginFailed() {
		return csmBundle.verifyCSMLoginFailed();
	}

	public void disableConfiguration(String configurationName) {
		innovationSuiteApplications.selectConfiguration(configurationName);
		innovationSuiteApplications.clickOnDisableButton();
	}

	public InnovationSuiteApplications selectConfiguration(String configurationName) {
		innovationSuiteApplications.selectConfiguration(configurationName);
		return innovationSuiteApplications;
	}

	public InnovationSuiteFeatureMethodsPage navigateToCreateNewApplication() {
		innovationSuiteApplications.navigateToCreateNewApplication();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage fillApplicationDetails(String displayName, String shortName, String groupId) {
		innovationSuiteApplications.fillApplicationDetails(displayName, shortName, groupId);
		return this;
	}

	public InnovationSuiteApplications closeNewApplicationDialog() {
		innovationSuiteApplications.closeNewApplicationDialog();
		return 	innovationSuiteApplications;
	}

	public InnovationSuiteApplications verifyApplicationCreated(String applicationName) {
		return 	innovationSuiteApplications.verifyApplicationCreated(applicationName); 
	}

	public InnovationSuiteFeatureMethodsPage navigateToCreateNewRecordDefinition() {
		innovationSuiteApplications.navigateToCreateNewRecordDefinition();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage selectRecordDefinitionType(String recordType) {
		innovationSuiteApplications.selectRecordDefinitionType(recordType);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage fillRecordDefinitionName(String recordName) {
		innovationSuiteApplications.fillRecordDefinitionName(recordName);
		return this;
	}

	public boolean verifyRecordDefinitionCreated(String recordName) {
		return innovationSuiteApplications.verifyRecordDefinitionCreated(recordName);
	}

	public InnovationSuiteFeatureMethodsPage navigateToCreateNewProcessDefinition() {
		innovationSuiteApplications.navigateToCreateNewProcessDefinition();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage selectInputMap(String processName) {
		innovationSuiteApplications.selectInputMap(processName);
		return this;
	}
	
	public InnovationSuiteFeatureMethodsPage enterProcessName(String name) {
		innovationSuiteApplications.enterProcessName(name);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage dragAndDropCreateRecordActivity() {
		innovationSuiteApplications.dragAndDropCreateRecordActivity();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage saveProcessDefinition() {
		innovationSuiteApplications.saveProcessDefinition();
		return this;
	}

	public boolean verifyProcessDefinitionCreated(String processName) {
		return innovationSuiteApplications.verifyProcessDefinitionCreated(processName);
	}

	public InnovationSuiteFeatureMethodsPage enableRequiredFeatureFlag(String featureFlagName) {
		innovationSuite.enableRequiredFeatureFlag(featureFlagName);
		return this;
  }
  
	public InnovationSuiteFeatureMethodsPage enabledOrDisabledFeatureDescription(String input) {
		// TODO Auto-generated method stub
		innovationSuiteApplications.enabledOrDisabledFeatureDescription(input);
		return this;
		
	}

	public String VerifyAuthenticationForRBAC(String output, Map<Object, String> iSTestData, Map<Object, String> rBACUsersData) {
		// TODO Auto-generated method stub
		log.info("OutPut Data:"+output);
		if(output.contains("Access Set"))
		{
			helixGPTAgentStudio.LoginRBAC(rBACUsersData);
			sendMessage(iSTestData.get("input"));
			 output = getAllGPTResponseString();
		}	
		boolean result = output.contains("Creating high resolution screenshots from the Chrome browser.pdf");
		Assert.assertTrue(result, "Creating high resolution screenshots from the Chrome browser Not Success");
		return output;
		
	}

	public InnovationSuiteFeatureMethodsPage clickNewButtonForRecordDefinition() {
		helixGPTAgentStudio.clickNewButton();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage selectRegularRecordOption() {
		helixGPTAgentStudio.selectRegularRecord();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enterRecordDefinitionName(String recordName) {
		helixGPTAgentStudio.enterRecordName(recordName);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enableShouldExportDataToggle() {
		helixGPTAgentStudio.enableShouldExportData();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enableDataTranslationToggle() {
		helixGPTAgentStudio.enableDataTranslation();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage clickNewFieldButtonInRecordDefinition() {
		helixGPTAgentStudio.clickNewFieldButton();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage selectAttachmentFieldType() {
		helixGPTAgentStudio.selectAttachmentFieldType();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enterFieldNameInRecordDefinition(String fieldName) {
		helixGPTAgentStudio.enterFieldName(fieldName);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enableRequiredCheckboxForField() {
		helixGPTAgentStudio.enableRequiredCheckbox();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage clickSaveButtonForField() {
		helixGPTAgentStudio.clickSaveFieldButton();
		return this;
	}

	public boolean verifySuccessMessageDisplayed() {
		return helixGPTAgentStudio.verifySuccessMessage();
	}

	public InnovationSuiteFeatureMethodsPage waitForSuccessMessageDisappear() {
		helixGPTAgentStudio.waitForSuccessMessageToDisappear();
		return this;
	}

	public String copyFieldId(String fieldName) {
		return helixGPTAgentStudio.getFieldId(fieldName);
	}

	public InnovationSuiteFeatureMethodsPage clickNewInEditDataPanel() {
		innovationSuite.clickNewInEditDataPanel();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage fillTextField(String fieldName, String value) {
		innovationSuite.setTextField(fieldName, value);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage fillDropdownField(String fieldName, String option) {
		innovationSuite.setDropdownField(fieldName, option);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage clickSaveButtonInRecordForm() {
		innovationSuite.clickSaveButton();
		return this;
	}

	public String copyIDFromRecord() {
		return innovationSuite.getID();
	}

	public InnovationSuiteFeatureMethodsPage uploadAttachmentFileToField(String fieldName, File file) {
		innovationSuite.uploadAttachmentFile(fieldName, file);
		return this;
	}

	public InnovationSuiteFeatureMethodsPage enableCheckboxByFieldName(String fieldName) {
		innovationSuite.enableCheckboxField(fieldName);
		return this;
	}	
	
	/**
	 * Creates a record definition with an attachment field
	 * @param recordName - Name of the record definition
	 * @param fieldName - Name of the attachment field
	 * @param screenshotPrefix - Prefix for screenshot names (e.g., "DRSQA_12292")
	 * @return String - Field ID of the created attachment field
	 * @throws IOException if screenshot capture fails
	 */
	public String createRecordDefinitionWithAttachmentField(String recordName, String fieldName, String screenshotPrefix) throws IOException {
		log.info("Creating record definition with attachment field - Record: " + recordName + ", Field: " + fieldName);

		// Call the page class method that does all the work
		String fieldId = helixGPTAgentStudio.createRecordDefinitionWithAttachmentField(recordName, fieldName);

		// Validate the field ID
		Assert.assertFalse(fieldId == null || fieldId.isEmpty(), "Field ID is empty or null");
		log.info("Record definition created successfully with Field ID: " + fieldId);

		return fieldId;
	}

	/**
	 * Uploads an attachment instance to a record definition
	 * @param recordName - Name of the record definition
	 * @param fieldName - Name of the attachment field
	 * @param attachmentFile - File to upload
	 * @param description - Description for the attachment record
	 * @param status - Status value for the attachment record
	 * @param screenshotPrefix - Prefix for screenshot names (e.g., "DRSQA_12292")
	 * @throws IOException if screenshot capture fails
	 */
	public InnovationSuiteFeatureMethodsPage uploadAttachmentInstance(String recordName, String fieldName, File attachmentFile, String description, String status, String screenshotPrefix) throws IOException {
		log.info("Uploading attachment instance - Record: " + recordName + ", Field: " + fieldName + ", File: " + attachmentFile.getName());

		// Call the page class method that does all the work
		helixGPTAgentStudio.uploadAttachmentInstanceToRecord(recordName, fieldName, attachmentFile, description, status);

		log.info("Attachment instance uploaded successfully");
		return this;

	}

	/**
	 * Creates a connection record in HelixGPT Agent Studio to connect a field with a record definition
	 * 
	 * @param connectionRecordDefinitionName - Name of the connection record definition
	 * @param fieldId - Field ID to connect
	 * @param status - Connection status (e.g., "Enabled")
	 * @param connectionName - Name for the connection
	 * @param recordDefinition - Full record definition name
	 * @param dataSourceId - DataSource ID
	 * @return String - ID of the created connection record
	 */
	public String createConnectionRecord(String connectionRecordDefinitionName, String fieldId, String status, 
	                                    String connectionName, String recordDefinition, String dataSourceId) {
		log.info("Creating connection record in: " + connectionRecordDefinitionName);

		// Call the HelixGPT Agent Studio method
		String connectionRecordId = helixGPTAgentStudio.createConnectionRecord(
			connectionRecordDefinitionName, fieldId, status, connectionName, recordDefinition, dataSourceId
		);

		log.info("Connection record created with ID: " + connectionRecordId);
		return connectionRecordId;
	}

	/**
	 * Creates a Data Connection Job record
	 * 
	 * @param dataConnectionJobRecordDefinitionName - Name of the DataConnectionJob record definition
	 * @param description - Description for the job
	 * @param status - Status of the job
	 * @param type - Type of job
	 * @param connectionId - The Connection Record ID
	 * @param executeNow - Whether to execute immediately
	 * @param datasource - Datasource type
	 * @param screenshotPrefix - Prefix for screenshots
	 * @return The ID of the created Data Connection Job
	 */
	public String createDataConnectionJob(String dataConnectionJobRecordDefinitionName, String description, 
	                                      String status, String type, String connectionId, 
	                                      boolean executeNow, String datasource, String screenshotPrefix) {
		log.info("Creating data connection job - Description: " + description + ", Type: " + type);

		// Call the HelixGPT Agent Studio method
		String dataConnectionJobId = helixGPTAgentStudio.createDataConnectionJob(
			dataConnectionJobRecordDefinitionName, description, status, type, connectionId, executeNow, datasource, screenshotPrefix
		);

		log.info("Data connection job created with ID: " + dataConnectionJobId);
		return dataConnectionJobId;
	}

    public void disableGainsightTogglesIfEnabled() {
        log.info("Disabling Gainsight toggles if enabled");
        innovationSuite.disableGainsightToggleIfEnabled();
        innovationSuite.disableLoadGainsightFromBmcItToggleIfEnabled();
        innovationSuite.saveGainsightSettingsIfEnabled();
        log.info("Completed disabling Gainsight toggles and saving settings");
    }

    public String getValueFromBundleIDField() {
        return innovationSuite.getValueFromBundleIDField();
    }

    public String getValueFromGlobalContextAreaField() {
        return innovationSuite.getValueFromGlobalContextAreaField();
    }

	public void selectModelAndProviderForSkillAndAgent(String Application, String skillName, String agentName, String provider, String model) {
		selectApplication(Application);
		selectSkill(skillName);
		helixGPTAgentStudio.selectAgentForSkill(agentName);
		helixGPTAgentStudio.clickSkillTab(SolQAConstants.MODEL);
		setModelValues(SolQAConstants.PROVIDER, provider);
		Assert.assertTrue(getFieldValues(SolQAConstants.PROVIDER, provider));
		setModelValues(SolQAConstants.MODEL, model);
		Assert.assertTrue(getFieldValues(SolQAConstants.MODEL, model), "Model name not matched");
	}

	public String getStatusValue() {
		return innovationSuite.getStatusValue();
	}

	public InnovationSuiteFeatureMethodsPage clickOnCancelButton() {
		innovationSuite.clickOnCancelButton();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage updateStatusToEnabled() {
		innovationSuite.updateStatusToEnabled();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage navigateToWorkspaceTab() {
		innovationSuite.navigateToWorkspaceTab();
		return this;
	}

	public InnovationSuiteFeatureMethodsPage triggerDataInjectionJob(Map<Object, String> dataConnectionJobData, String connectionId) {
		innovationSuite.triggerDataInjectionJob(dataConnectionJobData, connectionId);
		return this;
	}

	public String addNewConnectionForWebAndPickID(Map<Object, String> connectionData) {
		return innovationSuite.addNewConnectionForWebAndPickID(connectionData);
	}

	public String addNewConnectionForCSMAndPickID(Map<Object, String> connectionData) {
		return innovationSuite.addNewConnectionForCSMAndPickID(connectionData);
	}

	public InnovationSuiteFeatureMethodsPage verifyArticleIsNotPresent(String article) {
		helixGPTAgentStudio.verifyArticleIsNotPresent(article);
		return this;
	}
}