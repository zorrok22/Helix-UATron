package pages.ITSM.ITSM;

import java.io.IOException;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import framework.BasePage;
import pages.ITSM.Api.Itsm_Helper;
import pages.ITSM.Apex.ApexProblemPage;
import pages.ITSM.Apex.ApexReleasePage;
import pages.ITSM.Apex.ApexWorkOrderPage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;

public class ITSMFeatureMethods extends BasePage {

    ITSMDashboard itsmDashboard = new ITSMDashboard();
    CommonWaits commonWaits = new CommonWaits();
    Change change = new Change();
    DMTJob dmtJob = new DMTJob();
    Incident incident = new Incident();
    Knowledge knowledge = new Knowledge();
    Problem problem = new Problem();
    Release release = new Release();
    WorkOrder workOrder = new WorkOrder();
    Report report = new Report();
    ApexWorkOrderPage apexWorkOrderPage = new ApexWorkOrderPage();
    ApexReleasePage apexReleasePage = new ApexReleasePage();
    ApexProblemPage apexProblemPage = new ApexProblemPage();
    ITSMCommonOperations itsmCommonOperations = new ITSMCommonOperations();
    AddRemoveLicensesConsole addRemoveLicensesConsole = new AddRemoveLicensesConsole();
    CTMPeople ctmPeople = new CTMPeople();

    public void closeBrowser() {
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

	public String setAndGetCreateRequestOnSubmitToValue(String expectedValue) {
		return itsmDashboard.setAndGetCreateRequestOnSubmitToValue(expectedValue);
	}

    public ITSMFeatureMethods navigateToPageFromApplication(String mainMenu, String subMenu) {
        itsmDashboard.navigateToPageFromApplication(mainMenu, subMenu);
        return this;
    }

    public void navigateMenu(String... menuPath) {
        itsmDashboard.navigateMenu(menuPath);
    }

    public ITSMFeatureMethods LoadForm(String form, String view, String ARServerAlias) {
        itsmDashboard.LoadForm(form, view, ARServerAlias);
        return this;
    }

    public ITSMFeatureMethods verifyHPD_CFG_RulesFormLoaded() {
        itsmDashboard.verifyHPD_CFG_RulesFormLoaded();
        return this;
    }

    public ITSMFeatureMethods selectCompany(String companyName) {
        itsmDashboard.selectCompany(companyName);
        return this;
    }

    public ITSMFeatureMethods advanceMajorIncidentSettings() {
        itsmDashboard.advanceMajorIncidentSettings();
        return this;
    }

    public ITSMFeatureMethods searchCI(String searchCIAssetType, String ciName) {
        itsmDashboard.searchCI(searchCIAssetType, ciName);
        return this;
    }

    public String getReconciliationIdentity() {
        return itsmDashboard.getReconciliationIdentity();
    }

    public ITSMFeatureMethods switchToITSMTab(String tabName) {
        commonWaits.switchToTabByTitle(tabName);
        return this;
    }

    public Itsm_Helper createIncidentViaPlatformAPI(String jsonFilePath, String dataKey, String username, String password) throws IOException {
        Itsm_Helper itsm_helper = new Itsm_Helper(configProp.get("CMDB_URL"), username, password);
        return itsm_helper.createIncidentViaPlatformAPI(jsonFilePath, dataKey, username, password);
    }

    public Itsm_Helper createAlarmsViaNetOps(String jsonFilePath, String dataKey, String username, String password) throws IOException {
        Itsm_Helper itsm_helper = new Itsm_Helper(configProp.get("CMDB_URL"), username, password);
        return itsm_helper.createAlarmsViaNetOps(jsonFilePath, dataKey, username, password);
    }
    
    public String createUserInCTMPeopleForm(Map<Object, String> UserDetails, String ARServerAlias, boolean isRandomStringRequired) {
        return itsmDashboard.createUserInCTMPeopleForm(UserDetails, ARServerAlias, isRandomStringRequired);
    }

    public String createIncident(Map<Object, String> incidentData) {
        return incident.fillIncidentData(incidentData);
    }

    public String getIncidentFieldValue(String labelName) {
        return incident.getFieldValue(labelName);
    }

    public String createChange(Map<Object, String> changeData) {
        return change.fillChangeData(changeData);
    }

    public String createWorkOrder(Map<Object, String> workOrderData) {
        return workOrder.fillWorkOrderData(workOrderData);
    }

    public String createProblem(Map<Object, String> problemData) {
        return problem.fillProblemData(problemData);
    }

    public String createRelease(Map<Object, String> releaseData) {
        return release.fillReleaseData(releaseData);
    }

    public String createKnowledgeArticle(Map<Object, String> knowledgeData) {
        return knowledge.fillKnowledgeData(knowledgeData);
    }

    public ITSMFeatureMethods searchIncident(String incidentID) {
        incident.searchIncident(incidentID);
        return this;
    }

    public ITSMFeatureMethods searchChange(String changeID) {
        change.searchChange(changeID);
        return this;
    }

    public ITSMFeatureMethods searchWorkOrder(String ticketID) {
        workOrder.searchWorkOrder(ticketID);
        return this;
    }

    public ITSMFeatureMethods searchKnowledgeArticle(String articleID) {
        knowledge.searchKnowledgeArticle(articleID);
        return this;
    }

    public boolean verifyIncidentActivityLog(String expectedActivityText) {
        return incident.verifyActivityLog(expectedActivityText);
    }

    public ITSMFeatureMethods searchProblem(String problemID) {
        problem.searchProblem(problemID);
        return this;
    }

    public ITSMFeatureMethods searchRelease(String releaseID) {
        release.searchRelease(releaseID);
        return this;
    }

    public ITSMFeatureMethods updateIncidentStatus(String status, String resolution, String statusReason) {
        incident.updateStatus(status, resolution, statusReason);
        return this;
    }

    public ITSMFeatureMethods updateChangeStatus(String status) {
        change.updateChangeStatus(status);
        return this;
    }

    public ITSMFeatureMethods approveChange(String changeID) {
        change.approveChange(changeID);
        return this;
    }

    public ITSMFeatureMethods updateWorkOrderStatus(String status) {
        workOrder.updateStatus(status);
        return this;
    }

    public ITSMFeatureMethods updateWorkOrderPriority(String priority) {
        workOrder.updatePriority(priority);
        return this;
    }

    public ITSMFeatureMethods updateArticleStatus(String status) {
        knowledge.updateArticleStatus(status);
        return this;
    }

    public ITSMFeatureMethods updateProblemStatus(String status, String statusReason) {
        problem.updateProblemStatus(status, statusReason);
        return this;
    }

    public ITSMFeatureMethods updateReleaseStatus(String status) {
        release.updateReleaseStatus(status);
        release.refreshTicket();
        return this;
    }

    public ITSMFeatureMethods updateTicketStatusUsingNextStageButton() {
        release.clickOnNextStage();
        release.refreshTicket();
        return this;
    }

    public ITSMFeatureMethods verifyFieldValue(String fieldName, String expectedValue) {
        Assert.assertEquals(release.getFieldValue(fieldName), expectedValue, "Field value is not as expected: " + fieldName);
        return this;
    }

    public ITSMFeatureMethods verifyIncidentStatus(String status) {
        Assert.assertEquals(incident.getStatus(), status, "Status is not as expected");
        return this;
    }

    public ITSMFeatureMethods verifyChangeStatus(String status) {
        Assert.assertEquals(change.getStatus(), status, "Status is not as expected");
        return this;
    }

    public ITSMFeatureMethods verifyWorkOrderStatus(String status) {
        Assert.assertEquals(workOrder.getStatus(), status, "Status is not as expected");
        return this;
    }

    public ITSMFeatureMethods verifyArticleStatus(String status) {
        Assert.assertEquals(knowledge.getStatus(), status, "Status is not as expected");
        return this;
    }

    public ITSMFeatureMethods verifyArticleResult() {
        Assert.assertTrue(knowledge.getSearchResultCount() > 0, " No search results found");
        return this;
    }

    public ITSMFeatureMethods verifyProblemStatus(String status) {
        Assert.assertEquals(problem.getStatus(), status, "Status is not as expected");
        return this;
    }

    public ITSMFeatureMethods verifyReleaseStatus(String status) {
        Assert.assertEquals(release.getStatus(), status, "Status is not as expected");
        return this;
    }

    public ITSMFeatureMethods createAndExecuteDMTJob(Map<Object, String> jobData) throws Exception {
        Assert.assertEquals(dmtJob.createAndExecuteDMTJob(jobData), SolQAConstants.SUCCESSFUL, "Job execution failed");
        return this;
    }

    public ITSMFeatureMethods refreshITSMPage() {
        commonWaits.waitForElementClickable(change.refreshButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMFeatureMethods switchToITSM(WebDriver webDriver) {
        String windowHandle = webDriver.getWindowHandle();
        webDriver.switchTo().window(windowHandle);
        setDriver(webDriver);
        return this;
    }

    public ITSMFeatureMethods navigateToCentralizedConfiguration() {
        itsmDashboard.navigateToCentralizedConfiguration();
        return this;
    }

    public ITSMFeatureMethods selectMenuItemFromComponentMenu(String[] menuPath) {
        itsmDashboard.selectMenuItemFromComponentMenu(menuPath);
        return this;
    }

    public ITSMFeatureMethods centralizedConfigurationSetting(String configSettingName, String settingValue) {
        itsmDashboard.centralizedConfigurationSetting(configSettingName, settingValue);
        return this;
    }

    public String getCentralizedConfigurationValue(String configSettingName) {
        return itsmDashboard.getCentralizedConfigurationValue(configSettingName);
    }

    public ITSMFeatureMethods validateCentralizedConfigurationNotEmpty(String configSettingName) {
        itsmDashboard.validateCentralizedConfigurationNotEmpty(configSettingName);
        return this;
    }

    public ITSMFeatureMethods clickOnNewReportButton() {
        report.clickOnNewReportButton();
        return this;
    }

    public ITSMFeatureMethods selectTypeMenu(String menuItem) {
        report.selectTypeMenu(menuItem);
        return this;
    }

    public ITSMFeatureMethods selectFormMenu(String menuItem) {
        report.selectFormMenu(menuItem);
        return this;
    }

    public ITSMFeatureMethods verifyDisabledOKButton() {
        report.verifyDisabledOKButton();
        return this;
    }

    public ITSMFeatureMethods clickOnHelixDashboardLink() {
        report.clickOnHelixDashboardLink();
        return this;
    }

    public ITSMFeatureMethods clickOnPrintIcon() {
        itsmDashboard.clickOnPrintIcon();
        return this;
    }

    public ITSMFeatureMethods verifyDocPageOpened() {
        itsmDashboard.verifyDocPageOpened();
        return this;
    }

    public ITSMFeatureMethods verifyPrintMessage() {
        itsmDashboard.verifyPrintMessage();
        return this;
    }

    public ITSMFeatureMethods clickOnPrintPopupLink() {
        itsmDashboard.clickOnPrintPopupLink();
        return this;
    }

    public ITSMFeatureMethods enableSelfServiceHKM() {
        itsmDashboard.enableSelfServiceHKM();
        return this;
    }

    public ITSMFeatureMethods enableSSPQuickLinksForComAround() {
        itsmDashboard.enableSSPQuickLinksForComAround();
        return this;
    }

    public ITSMFeatureMethods enableAndAddHKMDetails(String hkmURL, String hkmUsername, String hkmPassword, String ARServerAlias) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        LoadForm(SolQAConstants.CFG_HKM_CONFIGURATIONSETTINGS, SolQAConstants.DEFAULT, ARServerAlias);
        itsmDashboard.enableHKMModule();
        if (hkmURL != null && !hkmURL.isEmpty() && hkmUsername != null && !hkmUsername.isEmpty() && hkmPassword != null && !hkmPassword.isEmpty()) {
            itsmDashboard.updateHKMURL(hkmURL, hkmUsername, hkmPassword);
        }
        return this;
    }

    public ITSMFeatureMethods enableHKMModule(String ARServerAlias) {
        LoadForm(SolQAConstants.CFG_HKM_CONFIGURATIONSETTINGS, SolQAConstants.DEFAULT, ARServerAlias);
        itsmDashboard.enableHKMModule();
        return this;
    }

    public ITSMFeatureMethods assignManager(Map<Object, String> changeData) {
        change.assignManager(changeData);
        return this;
    }

    public ITSMFeatureMethods relateCIToChange(String ci, WebDriver driver) {
        change.relateCIToChange(ci, driver);
        return this;
    }

    public ITSMFeatureMethods setSelfService_HKM(boolean enable, String ARServerAlias, String adminUsername, String adminPassword) {
        itsmDashboard.setSelfService_HKM(enable, ARServerAlias, adminUsername, adminPassword);
        return this;
    }

    public ITSMFeatureMethods searchViaGlobalSearch(String searchText) {
        itsmDashboard.searchViaGlobalSearch(searchText);
        return this;
    }

    public ITSMFeatureMethods validateIncidentIDCaseSensitivity(String incidentID) {
        itsmDashboard.validateIncidentIDCaseSensitivity(incidentID);
        return this;
    }

    public ITSMFeatureMethods verifyITSMLogin() {
        itsmDashboard.handlePostLoginPopups().verifyLogin();
        return this;
    }

    public ITSMFeatureMethods setSelfService_PV(boolean enable, String ARServerAlias) {
        itsmDashboard.setSelfService_PV(enable, ARServerAlias);
        return this;
    }

    public String createWorkOrderApex(Map<Object, String> workOrderData) {
        return apexWorkOrderPage.fillWorkOrderDataApex(workOrderData);
    }

    public String createReleaseApex(Map<Object, String> releaseData) {
        return apexReleasePage.fillReleaseDataApex(releaseData);
    }

    public String createProblemApex(Map<Object, String> problemData) {
        return apexProblemPage.fillProblemDataApex(problemData);
    }

    public ITSMFeatureMethods searchChangeRequest(String changeID) {
        change.searchChangeRequest(changeID);
        return this;
    }

    public ITSMFeatureMethods searchServerName(String serverName) {
        itsmDashboard.searchServerName(serverName);
        return this;
    }

    public boolean isServerNameSearchResultRowPresent(String serverName) {
        return itsmDashboard.isServerNameSearchResultRowPresent(serverName);
    }

    public ITSMFeatureMethods deleteServerName(String serverName) {
        itsmDashboard.deleteServerName(serverName);
        return this;
    }

    public ITSMFeatureMethods searchAndOpenArticle(String articleID) {
        knowledge.searchKnowledgeArticle(articleID);
        verifyArticleResult();
        knowledge.openArticle(articleID);
        return this;
    }

    public ITSMFeatureMethods verifyFeedBackIsAvailable(String expectedFeedbackText) {
        knowledge.verifyFeedBackIsAvailable(expectedFeedbackText);
        return this;
    }

    public ITSMFeatureMethods setServiceRequestOnSubmitToValue(String Company, String value, String settingName) {
        itsmCommonOperations.setServiceRequestOnSubmitToValue(Company, value, settingName);
        return this;
    }

    public ITSMFeatureMethods modifyAssetSandboxEnabledSettings(String company, String sandboxEnableValue) {
        LoadForm("AST:AppSettings", SolQAConstants.DEFAULT, CommonMethods.readConfig("ARServerAlias"));
        itsmCommonOperations.modifyAssetSandboxEnabledSettings(company, sandboxEnableValue);
        return this;
    }

    public ITSMFeatureMethods verifyHtmlBodyOnEmailPage(String requestId) {
        itsmDashboard.verifyHtmlBodyOnEmailPage(requestId);
        return this;
    }

    public ITSMDashboard verifyCreatedUserIsExistsWithoutLoginID(Map<Object, String> UserDetails, String ARServerAlias) {
        return itsmDashboard.verifyCreatedUserIsExistsWithoutLoginID(UserDetails, ARServerAlias);
    }

    public ITSMFeatureMethods updateWorkorderServiceRequestCreation(String companyName, String value, String ARServerAlias) {
        LoadForm(SolQAConstants.WORKORDER_RULES, SolQAConstants.DEFAULT, ARServerAlias);
        itsmDashboard.updateWorkorderServiceRequestCreation(companyName, value);
        return this;
    }

    public ITSMDashboard searchRequestID(String value) {
        return itsmDashboard.searchRequestID(value);
    }

    public ITSMDashboard verifyFormNames(String expectedFormNames) {
        return itsmDashboard.verifyFormNames(expectedFormNames);
    }

    /**
     * Switches to the active (most recently opened) window after waiting for the page to fully load.
     *
     * @param timeInterval The time interval to wait before switching windows (in seconds)
     */
    public ITSMFeatureMethods switchToActiveWindow(int timeInterval) {
        commonWaits.switchToActiveWindow(timeInterval);
        return this;
    }

    public ITSMFeatureMethods addApproverToChange(String typeOfApprover, String firstName, String lastName) {
        change.addApproverToChange(typeOfApprover, firstName, lastName);
        return this;
    }

    public ITSMFeatureMethods fillScheduleDatesSection(Map<Object, String> itsmTestData) {
        itsmCommonOperations.fillScheduleDatesSection(itsmTestData);
        return this;
    }

    public ITSMFeatureMethods clickOnNextButton() {
        itsmCommonOperations.clickOnNextButton();
        return this;
    }

    public ITSMFeatureMethods searchINCRequestIDInEmailMessages(String requestID) {
        itsmDashboard.searchINCRequestIDInEmailMessages(requestID);
        return this;
    }

    public String getINCRequestIDFoundInEmailMessages() {
        return itsmDashboard.getINCRequestIDFoundInEmailMessages();
    }

    /**
     * Search in BMC_BusinessService form by DatasetId
     *
     * @param datasetId The dataset ID to search for (e.g., "BMC.ADDM")
     */
    public ITSMFeatureMethods searchByDatasetId(String datasetId) {
        itsmDashboard.searchByDatasetId(datasetId);
        return this;
    }

    /**
     * Select first result from search results and get the InstanceId value
     *
     * @return The InstanceId value of the selected record
     */
    public String selectFirstResultAndGetInstanceId() {
        return itsmDashboard.selectFirstResultAndGetInstanceId();
    }

    /**
     * Verify Impact Analysis email status
     */
    public ITSMFeatureMethods verifyImpactAnalysisEmailStatus(String expectedStatus) {
        itsmDashboard.verifyImpactAnalysisEmailStatus(expectedStatus);
        return this;
    }

    /**
     * Set rules page value using radio button
     * Navigates to Incident Rules form, searches for company, and sets rule value
     *
     * @param company   Company name to search for
     * @param ruleName  Name of the rule to configure (e.g., "Cognitive Support Group Prediction")
     * @param ruleValue Value to set (e.g., "Yes" or "No")
     */
    public ITSMFeatureMethods setRulesPageValueUsingRadioButton(String company, String ruleName, String ruleValue) {
        itsmDashboard.setRulesPageValueUsingRadioButton(company, ruleName, ruleValue);
        return this;
    }

    /**
     * Train cognitive data for support group prediction or other AI features
     *
     * @param dataType    Type of cognitive data (e.g., "Support Group Prediction")
     * @param company     Company name
     * @param application Application name (e.g., "Incident Management")
     * @param template    Template name for training data
     */
    public ITSMFeatureMethods trainCognitiveData(String dataType, String company, String application, String template) {
        itsmDashboard.trainCognitiveData(dataType, company, application, template);
        return this;
    }

    /**
     * Configures Cognitive Service settings in ITSM Centralized Configuration
     * Enables cognitive service and sets classification service provider to HELIX
     *
     * @return ITSMFeatureMethods instance for method chaining
     */
    public ITSMFeatureMethods configureCognitiveService() {
        String[] menuPath = {SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED};
        selectMenuItemFromComponentMenu(menuPath);
        centralizedConfigurationSetting(SolQAConstants.ENABLE_COGNITIVE_SERVICE, "true");
        commonWaits.waitForSometime(commonWaits.minDelay);
        centralizedConfigurationSetting(SolQAConstants.CLASSIFICATION_SERVICE_PROVIDER, "HELIX");
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMFeatureMethods navigateToITSMURLInNewOpenedTab() {
		itsmDashboard.navigateToITSMURLInNewOpenedTab();
		return this;
	}

    public ITSMFeatureMethods navigateToManageCIs() {
		itsmDashboard.navigateToManageCIs();
		return this;
	}

	public ITSMFeatureMethods verifyManageCIsOptionIsVisible() {
		itsmDashboard.verifyManageCIsOptionIsVisible();
		return this;
	}

    public ITSMFeatureMethods verifyAddRemoveLicensesPageLoaded() {
        addRemoveLicensesConsole.verifyPageLoaded();
        return this;
    }

    public ITSMFeatureMethods verifyCTMPeoplePageLoaded() {
        ctmPeople.verifyPageLoaded();
        return this;
    }

    public ITSMFeatureMethods selectLicenseType(String licenseType) {
        addRemoveLicensesConsole.selectLicenseType(licenseType);
        return this;
    }

    public ITSMFeatureMethods setNumberOfLicensesInputValue(String numberOfLicensesValue) {
        addRemoveLicensesConsole.setNumberOfLicensesInputValue(numberOfLicensesValue).clickSaveButton();
        return this;
    }

    public ITSMFeatureMethods updateLicenseValue(String licenseType, String numberOfLicensesValue) {
        selectLicenseType(licenseType);
        setNumberOfLicensesInputValue(numberOfLicensesValue);
        return this;
    }

    public ITSMFeatureMethods searchPeopleWithUserName(String userName) {
        ctmPeople.searchPeopleWithUserName(userName);
        return this;
    }

    public ITSMFeatureMethods updateISApplicationLicenses(String licenseValue) {
        ctmPeople.updateISApplicationLicenses(licenseValue);
        return this;
    }

    public ITSMFeatureMethods updateISPersonas(String applicationName, String personaValue) {
        ctmPeople.updateISPersonas(applicationName, personaValue);
        return this;
    }

    public ITSMFeatureMethods addCCSParameters(String expectedCCSParameter) {
        itsmDashboard.addCCSParameters(expectedCCSParameter);
        return this;
    }

    // public ITSMFeatureMethods verifyTaskStatusOnIncident(String incidentID, String taskName, String taskStatus) {
    //     itsmDashboard.verifyTaskStatusOnIncident(incidentID, taskName, taskStatus);
    //     return this;
    // }


    public ITSMFeatureMethods clickDataSourceByValue(String dataSourceValue) {
        itsmCommonOperations.clickDataSourceByValue(dataSourceValue);
        return this;
    }

    public ITSMFeatureMethods clickOnTab(String tabName) {
        itsmCommonOperations.clickOnTab(tabName);
        return this;
    }

    public ITSMFeatureMethods verifyAllRuleBuildStatusesAreBuiltSuccessfully() {
        itsmCommonOperations.verifyAllRuleBuildStatusesAreBuiltSuccessfully();
        return this;
    }

    public ITSMFeatureMethods clickOnServiceTargetsTab() {
        itsmCommonOperations.clickOnServiceTargetsTab();
        return this;
    }

    public ITSMFeatureMethods clickServiceTargetRowById(String serviceTargetId) {
        itsmCommonOperations.clickServiceTargetRowById(serviceTargetId);
        return this;
    }

    public ITSMFeatureMethods verifyBuildStatusForServiceTargetId(String serviceTargetId, String expectedBuildStatus) {
        itsmCommonOperations.verifyBuildStatusForServiceTargetId(serviceTargetId, expectedBuildStatus);
        return this;
    }

    public String getBuildStatusForServiceTargetId(String serviceTargetId) {
        return itsmCommonOperations.getBuildStatusForServiceTargetId(serviceTargetId);
    }
    public ITSMFeatureMethods clickOnBuildBtn() {
        itsmCommonOperations.clickOnBuildBtn();
        return this;
    }

    public ITSMFeatureMethods clickViewButton() {
        itsmCommonOperations.clickViewButton();
        return this;
    }

    public ITSMFeatureMethods confirmOperationInPopup() {
        itsmCommonOperations.confirmOperationInPopup();
        return this;
    }

    public ITSMFeatureMethods setSingleGoalAndCostValues(String Hours, String Minutes) {
        itsmCommonOperations.setSingleGoalAndCostValues(Hours, Minutes);
        return this;
    }

    public ITSMFeatureMethods modifySelfServicePortal(String option) {
        itsmDashboard.navigateToConfiguration();
        itsmDashboard.modifySelfServicePortal(option);
        return this;
    }

    public ITSMFeatureMethods enterRequiredDetails() throws IOException {
        itsmDashboard.enterRequiredDetails();
        return this;
    }
}