package pages.ITSM.bwf;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import framework.BasePage;
import framework.Launcher;
import pages.ITSM.Apex.ApexBWFPage;
import pages.ITSM.HelixDashboard.HelixDashboard;
import pages.ITSM.SmartReporting.SmartReportingLandingPage;
import util.SolQAConstants;
import util.SolqaLogger;

public class BWFFeatureMethods extends BasePage {

    static SolqaLogger log = new SolqaLogger(BWFFeatureMethods.class.getName());
    BWFDashboard bwfDashboard = new BWFDashboard();
    BWFLiveChat bwfLiveChat = new BWFLiveChat();
    BWFKnowledge bwfKnowledge = new BWFKnowledge();
    BWFCase bwfCase = new BWFCase();
    SmartReportingLandingPage smartReporting = new SmartReportingLandingPage();
    HelixDashboard helixDashboard = new HelixDashboard();
    BWFSettings bwfSettings = new BWFSettings();
    BWFGPT bwfGPT = new BWFGPT();
    ApexBWFPage apexBWFPage = new ApexBWFPage();
    BWFQuickCasePage bwfQuickCasePage = new BWFQuickCasePage();
    UserProfilePage userProfilePage = new UserProfilePage();
    BWFTask bwfTask = new BWFTask();
    BWFCaseTemplate bwfCaseTemplate = new BWFCaseTemplate();
    BWFToDoTasksPage bwfToDoTasksPage = new BWFToDoTasksPage();

    public BWFFeatureMethods verifyLoginToBWF() {
        bwfDashboard.verifyLoginToBWF();
        return this;
    }

    public BWFFeatureMethods openLiveChat() {
        bwfDashboard.openLiveChat();
        return this;
    }

    public BWFFeatureMethods verifyLiveChatOpened() {
        bwfDashboard.verifyLiveChatOpened();
        return this;
    }

    public String getLiveAgentName() {
        return bwfDashboard.getLiveAgentName();
    }

    public BWFFeatureMethods clickQueueLink() {
        bwfDashboard.clickQueueLink();
        return this;
    }

    public BWFFeatureMethods selectCustomerFromQueue(String message, String customerName) {
        bwfDashboard.selectCustomerFromQueue(message, customerName);
        return this;
    }

    public BWFFeatureMethods switchToBWFLiveChat(WebDriver webDriver) {
        log.info("----------- Switching to BWF window ----------------------");
        String windowHandle = webDriver.getWindowHandle();
        webDriver.switchTo().window(windowHandle);
        Launcher.setDriver(webDriver);
        return this;
    }

    public BWFFeatureMethods verifyMessageOnLiveChat(String expectedMessage) {
        bwfLiveChat.verifyMessageOnLiveChat(expectedMessage);
        return this;
    }

    public BWFFeatureMethods uploadAttachment(File file) {
        bwfLiveChat.uploadAttachment(file);
        return this;
    }

    public BWFFeatureMethods sendPrivateNoteInLiveChat(String message) {
        bwfLiveChat.sendPrivateNoteInLiveChat(message);
        return this;
    }

    public BWFFeatureMethods selectResourceTabFromRightPanelAndShareTheKnowledge(String searchKnowledge) {
        bwfLiveChat.selectResourceTabFromRightPanelAndShareTheKnowledge(searchKnowledge);
        return this;
    }

    public BWFFeatureMethods openSharedKnowledgeArticleAndSwitchBack(String sharedArticle) {
        bwfLiveChat.openSharedKnowledgeArticleAndSwitchBack(sharedArticle);
        return this;
    }

    public BWFFeatureMethods createCaseTicketForLiveChatRequest() {
        bwfLiveChat.createCaseTicketForLiveChatRequest();
        return this;
    }

    public String getCaseCreatedId() {
        return bwfLiveChat.getCaseCreatedId();
    }

    public BWFFeatureMethods previewAndVerifyCaseTicketDetailsOnLiveChatPage(String expectedUser) {
        bwfLiveChat.previewAndVerifyCaseTicketDetailsOnLiveChatPage(expectedUser);
        return this;
    }

    public BWFFeatureMethods viewCaseAndVerifyAttachmentFilesSharedAndDetailsOnActivityPage(List<String> attachments, String initialMessage) {
        bwfLiveChat.viewCaseAndVerifyAttachmentFilesSharedAndDetailsOnActivityPage(attachments, initialMessage);
        return this;
    }

    public BWFFeatureMethods setLineOfBusiness_CaseConsole(String lobValueToSet) {
        bwfDashboard.setLineOfBusiness_CaseConsole(lobValueToSet);
        return this;
    }

    public BWFFeatureMethods selectLOBFromDropDown(String lobName) {
        bwfSettings.selectLOBFromDropDown(lobName);
        return this;
    }

    public BWFFeatureMethods removeAllFiltersOnCaseWorkspaceIfPresent() {
        bwfDashboard.removeAllFiltersOnCaseWorkspaceIfPresent();
        return this;
    }

    public BWFFeatureMethods setSearchTextField_CaseConsole(String searchValue) {
        bwfDashboard.setSearchTextField_CaseConsole(searchValue);
        return this;
    }

    public BWFFeatureMethods clickSearchBtn_CaseConsole() {
        bwfDashboard.clickSearchBtn_CaseConsole();
        return this;
    }

    public BWFFeatureMethods verify_Activity_Tab(String value) {
        bwfDashboard.verify_Activity_Tab(value);
        return this;
    }

    public BWFFeatureMethods verify_UserName_In_Activity_Tab(String Username) {
        bwfDashboard.verifyUserName_In_SocialFeed(Username);
        return this;
    }

    public BWFFeatureMethods addActivityNotes(String value) {
        bwfDashboard.addActivityNotes(value);
        return this;
    }

    public BWFFeatureMethods navigateToPage(String menuGroupName, String menuItemName) {
        bwfDashboard.navigateToPage(menuGroupName, menuItemName);
        return this;
    }

    public BWFFeatureMethods navigateToWorkspaceKnowledge() {
        bwfDashboard.clickSubMenuItem(SolQAConstants.WORKSPACE, "Knowledge");
        return this;
    }

    public BWFFeatureMethods openGridRecordByLinkText(String linkText) {
        bwfDashboard.openGridRecordByLinkText(linkText);
        return this;
    }

    public String createKnowledgeArticle(Map<Object, String> bwfTestData) {
        bwfKnowledge.setLineOfBusiness(bwfTestData.get("Line of Business"));
        return bwfKnowledge.createKnowledgeArticle(bwfTestData);
    }

    public String getArticleStatus() {
        return bwfKnowledge.getArticleTransitionStatus();
    }

    public BWFFeatureMethods changeStatusTo(String status) {
        bwfKnowledge.changeArticleStatusTo(status);
        return this;
    }

    public String createCase(Map<Object, String> caseData) {
        bwfDashboard.setLineOfBusiness_CaseConsole(caseData.get("Line of Business"));
        bwfDashboard.navigateToPage("Create", "Case");
        return bwfCase.fillCaseData(caseData)
                .ClickOnGoToCaseIfPresent()
                .getCaseID();
    }

    public BWFFeatureMethods searchAndOpenCase(String caseID) {
        bwfDashboard.searchCase(caseID);
        bwfDashboard.openCase(caseID);                                                                                                      
        return this;
    }

    public String getCaseStatus() {
        return bwfCase.getCaseTransitionStatus();
    }

    public BWFFeatureMethods changeCaseStatusTo(String status) {
        bwfCase.changeCaseStatusTo(status);
        return this;
    }

    public BWFFeatureMethods verifyCaseDetails(Map<Object, String> caseData) {
        bwfCase.verifyCaseData(caseData);
        return this;
    }

    public BWFFeatureMethods crosslaunchManageCatalog() {
        bwfDashboard.crosslaunchManageCatalog();
        return this;
    }

    public BWFFeatureMethods crossLaunchReports() {
        bwfDashboard.crossLaunchReports();
        smartReporting.verifyCrossLaunch();
        return this;
    }

    public BWFFeatureMethods openReport(String reportName) {
        smartReporting.openReport(reportName);
        return this;
    }

    public BWFFeatureMethods openFirstRequest(String companyName) {
        smartReporting.openFirstRequest(companyName);
        return this;
    }

    public BWFFeatureMethods crossLaunchApplication(String applicationName) {
        bwfDashboard.crossLaunchApplication(applicationName);
        return this;
    }

    public BWFFeatureMethods searchAndOpenDashboard(String dashboardName) {
        helixDashboard.searchAndOpenDashboard(dashboardName);
        return this;
    }

    public BWFFeatureMethods applyFiltersOnDashboard(Map<Object, String> filterData) {
        helixDashboard.applyFilters(filterData);
        return this;
    }

    public BWFFeatureMethods openCaseFromDashboard(String caseID) {
        helixDashboard.openCaseFromDashboard(caseID);
        return this;
    }

    public String createNewKnowledge() {
        return bwfCase.createNewKnowledge();
    }

    public BWFFeatureMethods navigateToActivityTab() {
        bwfCase.navigateToActivityTab();
        return this;
    }

    public BWFFeatureMethods navigateToResourceTab() {
        bwfCase.navigateToResourceTab();
        return this;
    }

    public String getKnowledgeID(String articleTitle) {
        return bwfCase.getKnowledgeID(articleTitle);
    }

    public BWFFeatureMethods changeCaseStatusToResolvedWithNotes(Map<Object, String> caseData) {
        bwfCase.changeCaseStatusToResolvedWithNotes(caseData);
        return this;
    }

    public BWFFeatureMethods verifyArticleCreatedForCase(Map<Object, String> caseData, String articleID) {
        bwfCase.verifyArticleCreatedForCase(caseData, articleID);
        return this;
    }

    public BWFFeatureMethods searchAndOpenSettings(String settingName) {
        bwfDashboard.searchAndOpenSettings(settingName);
        return this;
    }

    public BWFFeatureMethods checkKnowledgeSourceConfigValue() {
        bwfDashboard.checkKnowledgeSourceConfigValue();
        return this;
    }

    public BWFFeatureMethods updateKnowledgeCuratorValue(String value) {
        bwfDashboard.updateKnowledgeCuratorValue(value);
        return this;
    }

    public BWFFeatureMethods navigateToSettings() {
        bwfDashboard.navigateToSettings();
        return this;
    }

    public BWFFeatureMethods enterValueInTextField(String value) {
        bwfSettings.enterValueInTextField(value);
        return this;
    }

    public BWFFeatureMethods clickOnSearchedAdminSetting(String settingName) throws Exception {
        bwfSettings.clickOnSearchedAdminSetting(settingName);
        return this;
    }

    public BWFFeatureMethods clickOnForwardArrowInManageLOB() {
        bwfSettings.clickOnForwardArrowInManageLOB();
        return this;
    }

    public BWFFeatureMethods clickTabNameOnLineOfBusinessManagement(String tabName) {
        bwfSettings.clickTabNameOnLineOfBusinessManagement(tabName);
        return this;
    }

    public BWFFeatureMethods clickApplicationConfigurationsTab() {
        bwfSettings.clickApplicationConfigurationsTab();
        return this;
    }

    public BWFFeatureMethods clickOnHelixGPTConfigNameAndSetValue(String value, String configValue) {
        bwfSettings.clickOnHelixGPTConfigNameAndSetValue(value, configValue);
        return this;
    }

    public BWFFeatureMethods clickApplicationConfigNameAndSetValue(String configName, String configValue) {
        bwfSettings.clickApplicationConfigNameAndSetValue(configName, configValue);
        return this;
    }

    public BWFFeatureMethods verifyGlobalSearchButtonAfterHelixGPTSearchEnabled() {
        bwfDashboard.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
        return this;
    }

    public BWFFeatureMethods clickGlobalSearchButtonAfterHelixGPTSearchEnabled() {
        bwfDashboard.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
        return this;
    }

    public BWFFeatureMethods navigateToChatOption() {
        bwfDashboard.navigateToChatOption();
        return this;
    }

    public BWFFeatureMethods sendMessageInGPTChat(String message) {
        bwfDashboard.sendMessageInGPTChat(message);
        return this;
    }

    public String getAllBWFGPTResponseString() {
        return bwfGPT.getAllBWFGPTResponseString();
    }

    public BWFFeatureMethods selectHelixGPTTab() {
        bwfDashboard.selectHelixGPTTab();
        return this;
    }

    public BWFFeatureMethods setStatusReason(String statusValue) {
        bwfCase.setStatusReason(statusValue);
        return this;
    }

    public BWFFeatureMethods clickOnGenerateCaseResolution() {
        bwfCase.clickOnGenerateCaseResolution();
        return this;
    }

    public String getResolutionDescription() {
        return bwfCase.getResolutionDescription();
    }

    public BWFFeatureMethods saveStatusTransition() {
        bwfCase.saveStatusTransition();
        return this;
    }

    public BWFFeatureMethods defaultSearchToggleButton(String buttonName) {
        bwfDashboard.defaultSearchToggleButton(buttonName);
        return this;
    }

    public BWFFeatureMethods verifyFilterOptionsOnSearchDropdownMenus() {
        bwfDashboard.verifyFilterOptionsOnSearchDropdownMenus();
        return this;
    }

    public BWFFeatureMethods selectFilterOptionAndSearchOnHelixGPTSearchbox(String filterOption, String searchValue) {
        bwfDashboard.selectFilterOptionAndSearchOnHelixGPTSearchbox(filterOption, searchValue);
        return this;
    }

    public BWFFeatureMethods verifyCountOfItemsReturnedAndOpenFullView(String itemType, String itemTypeValue, int itemlevel, int itemline) {
        bwfDashboard.verifyCountOfItemsReturnedAndOpenFullView(itemType, itemTypeValue, itemlevel, itemline);
        return this;
    }

    public BWFFeatureMethods importService(String fileLocation) {
        bwfDashboard.importService(fileLocation);
        return this;
    }

    public String createKnowledgeArticleApex(Map<Object, String> knowledgeData) {
        bwfKnowledge.setLineOfBusiness(knowledgeData.get("Line of Business"));
        return apexBWFPage.createKnowledgeArticleApex(knowledgeData);
    }

    public BWFFeatureMethods updateArticleStatusApex(String status) {
        apexBWFPage.updateArticleStatusApex(status);
        return this;
    }

    public BWFFeatureMethods changeCaseStatusToWithoutSaving(String status) {
        bwfCase.changeCaseStatusToWithoutSaving(status);
        return this;
    }

    public BWFFeatureMethods verifyCaseIsSetToFlagged() {
        bwfCase.verifyCaseIsSetToFlagged();
        return this;
    }

    public BWFFeatureMethods addActivityNoteWithAttachment(String note, File file) {
        bwfCase.addActivityNoteWithAttachment(note, file);
        return this;
    }

    public BWFFeatureMethods addComment_In_SocialFeed_WithPeopleMention(String comment, String peopleToMention) {
        bwfCase.addComment_In_SocialFeed_WithPeopleMention(comment, peopleToMention);
        return this;
    }

    public String createKnowledgeArticleWithExactTitle(Map<Object, String> bwfTestData, String exactTitle) {
        bwfKnowledge.setLineOfBusiness(bwfTestData.get("Line of Business"));
        return bwfKnowledge.createKnowledgeArticleWithExactTitle(bwfTestData, exactTitle);
    }

    public BWFFeatureMethods verifyKnowledgeArticleStatus(String expectedStatus) {
        bwfKnowledge.verifyStatus(expectedStatus);
        return this;
    }

    public BWFFeatureMethods setKnowledgeArticleExternal(String externalValue) {
        bwfKnowledge.setExternalMetadata(externalValue);
        return this;
    }

    public BWFFeatureMethods majorEditCreateVersion(String versionTitle, boolean publishApproval, String assignToUserText) {
        bwfKnowledge.majorEditCreateVersion(versionTitle, publishApproval, assignToUserText);
        return this;
    }

    public BWFFeatureMethods verifyKnowledgeArticleActivityCommentPresent(String expectedText) {
        bwfKnowledge.verifyCommentPresent(expectedText);
        return this;
    }

    public BWFFeatureMethods verifyKnowledgeArticleActivityCommentNotPresent(String expectedText) {
        bwfKnowledge.verifyCommentNotPresent(expectedText);
        return this;
    }

    public BWFFeatureMethods refreshKnowledgeArticle() {
        bwfKnowledge.refresh();
        return this;
    }

    public BWFFeatureMethods setStatus(String status) {
        bwfKnowledge.setStatus(status).saveStatus();
        return this;
    }

    public BWFFeatureMethods assignToMe(String userDisplayText) {
        bwfKnowledge.assignToMe(userDisplayText);
        return this;
    }

    public BWFFeatureMethods saveKnowledgeArticle() {
        bwfKnowledge.save_Knowledge();
        return this;
    }

    public BWFFeatureMethods setExternalMetadata(String externalValue) {
        bwfKnowledge.setExternalMetadata(externalValue);
        return this;
    }

    public boolean isViewedCaseShown() {
        return bwfCase.isViewedCaseShown();
    }

    public BWFFeatureMethods clickAssignToMeBtn() {
        bwfCase.clickOnAssignToMeButton();
        return this;
    }

    public BWFFeatureMethods verifyCaseDetailEquals(String fieldLabel, String expectedValue) {
        bwfCase.verifyCaseDetailEquals(fieldLabel, expectedValue);
        return this;
    }


    public BWFFeatureMethods verifyActivityContainsText(String expectedText) {
        bwfCase.verifyActivityContainsText(expectedText);
        return this;
    }

    public String getCaseDetail(String fieldLabel) {
        return bwfCase.getCaseDetail(fieldLabel);
    }

    public BWFFeatureMethods resolveTaskByName(String taskName) {
        bwfCase.resolveTaskByName(taskName);
        return this;
    }

    public BWFFeatureMethods clickAcceptChat() {
        bwfDashboard.clickAcceptChat();
        return this;
    }

    public BWFFeatureMethods verifyAttachmentIsShared(String attachmentFileName) {
        bwfLiveChat.verifyAttachmentIsShared(attachmentFileName);
        return this;
    }

    // Overloaded method with default columnName
    public BWFFeatureMethods clickCaseIDLink_CaseConsole(String value) {
        clickCaseIDLink_CaseConsole(value, "Request ID");
        return this;
    }

    // Full method with both parameters
    public BWFFeatureMethods clickCaseIDLink_CaseConsole(String value, String columnName) {
        bwfDashboard.clickCaseIDLink_CaseConsole(value, columnName);
        return this;
    }

    public String waitForExternalIdToContain(String expectedContains, int maxAttempts, int waitMs) {
        return bwfCase.waitForExternalIdToContain(expectedContains, maxAttempts, waitMs);
    }

    public String getExternalIdValue() {
        return bwfCase.getExternalIdValue();
    }

    public BWFFeatureMethods clickLinkByText(String linkText) {
        bwfCase.clickLinkByText(linkText);
        return this;
    }

    public BWFFeatureMethods verifyTextPresent(String text) {
        bwfCase.verifyTextPresent(text);
        return this;
    }

    public BWFFeatureMethods openUserProfilePage() {
        userProfilePage.openUserProfilePage();
        return this;
    }

    public String getPersonNameFromProfilePage() {
        return userProfilePage.getPersonNameFromProfilePage();
    }

    public BWFFeatureMethods expandActivityOnProfilePage() {
        userProfilePage.expandActivityOnProfilePage();
        return this;
    }

    public BWFFeatureMethods verifyAttachmentInActivity(String attachmentFileName) {
        Assert.assertTrue(bwfCase.isAttachmentPresentInActivityTab(attachmentFileName),
                "Attachment '" + attachmentFileName + "' not found in activity tab.");
        return this;
    }

    public BWFFeatureMethods changeCaseStatusAndSave(String status, String statusReason) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        bwfCase.changeCaseStatusAndSave(status, statusReason);
        return this;
    }

    public BWFFeatureMethods verifyCaseStatus(String expectedStatus) {
        bwfCase.verifyCaseStatus(expectedStatus);
        return this;
    }

    public BWFFeatureMethods reopenCaseFromCaseView() {
        bwfCase.reopenCaseFromCaseView();
        return this;
    }

    public BWFFeatureMethods verifyActivityTitlePresent(String expectedTitleContains) {
        bwfCase.verifyActivityTitlePresent(expectedTitleContains);
        return this;
    }

    public BWFFeatureMethods verifyApproverPresentInApproversList(String approverFullName) {
        bwfCase.verifyApproverPresentInApproversList(approverFullName);
        return this;
    }

    public BWFFeatureMethods clickOnNotificationBell() {
        bwfCase.clickOnNotificationBell();
        return this;
    }

    public BWFFeatureMethods verifyNotificationContains(String expectedAlertContains) {
        bwfCase.verifyNotificationContains(expectedAlertContains);
        return this;
    }

    public boolean isActivityTitlePresent(String expectedTitleContains) {
        return bwfCase.isActivityTitlePresent(expectedTitleContains);
    }

    public BWFFeatureMethods clickTaskSummaryLinkOnCase(String taskSummary) {
        bwfCase.clickTaskSummaryLinkOnCase(taskSummary);
        return this;
    }

    public String getTaskId() {
        return bwfTask.getTaskId();
    }

    public boolean verifyTaskStatus(String expectedStatus) {
        return bwfTask.verifyTaskStatus(expectedStatus);
    }

    public BWFFeatureMethods verifyTaskStatusOnFullView(String taskStatus) {
        Assert.assertTrue(bwfTask.getTaskStatus().contains(taskStatus), "");
        return this;
    }

    public BWFFeatureMethods verifyTaskSummary(String expectedSummary) {
        bwfTask.verifyTaskSummary(expectedSummary);
        return this;
    }

    public BWFFeatureMethods clickTaskIdFromNotification(String taskId) {
        bwfCase.clickTaskIdFromNotification(taskId);
        return this;
    }

    public BWFFeatureMethods addAdhocApprover(String approverFullName) {
        bwfCase.addAdhocApproval(approverFullName);
        return this;
    }

    public BWFFeatureMethods verify_Value_Added_In_Audit_To_Field(String fieldLabel, String expectedValue) {
        bwfCase.verify_Value_Added_In_Audit_To_Field(fieldLabel, expectedValue);
        return this;
    }

    public BWFFeatureMethods expandAuditActivityToRevealAllFields() {
        bwfCase.expandAuditActivityToRevealAllFields();
        return this;
    }
    public String getValueFromAuditTabForField(String fieldLabel) {
        return bwfCase.getValueFromAuditTabForField(fieldLabel);
    }

    public BWFFeatureMethods clickEditCase() {
        bwfCase.clickEditCase();
        return this;
    }

    public BWFFeatureMethods updateDynamicListValue(String fieldLabel, String value) {
        clickEditCase();
        bwfCase.updateDynamicListValue(fieldLabel, value);
        return this;
    }

    public BWFFeatureMethods save_Edited_Case() {
        bwfCase.save_Edited_Case();
        return this;
    }

    public BWFFeatureMethods verifyResponseForTheQuestion(String sResponseQuestion, String expectedResponse) {
        bwfCase.verifyResponseForTheQuestion(sResponseQuestion, expectedResponse);
        return this;
    }

    public BWFFeatureMethods clickSearchBtn_CaseConsoleAndVerifyNoResults(int timeoutSeconds) {
        bwfDashboard.clickSearchBtn_CaseConsoleAndVerifyNoResults(timeoutSeconds);
        return this;
    }

    // ============================================
    // Quick Case Methods
    // ============================================

    /**
     * Click on Quick Case tab
     */
    public BWFQuickCasePage clickQuickCaseTab() {
        return bwfQuickCasePage.clickQuickCaseTab();
    }

    /**
     * Type customer name and select from dropdown in Quick Case
     *
     * @param customerName - Customer name to search (e.g., @swaugh)
     */
    public BWFQuickCasePage typeAndSelectCustomerInQuickCase(String customerName) {
        return bwfQuickCasePage.typeAndSelectCustomerInQuickCase(customerName);
    }

    /**
     * Type text in Quick Case search field
     *
     * @param searchText - Text to search (e.g., !move)
     */
    public BWFQuickCasePage typeInQuickCaseSearch(String searchText) {
        return bwfQuickCasePage.typeInQuickCaseSearch(searchText);
    }

    /**
     * Select template from Quick Case dropdown
     *
     * @param templateName - Template name to select
     */
    public BWFQuickCasePage selectTemplateFromQuickCase(String templateName) {
        return bwfQuickCasePage.selectTemplateFromQuickCase(templateName);
    }

    /**
     * Verify template search is successful
     *
     * @param templateName - Template name to verify
     * @return true if template is visible
     */
    public boolean isTemplateSearchSuccessful(String templateName) {
        return bwfQuickCasePage.isTemplateSearchSuccessful(templateName);
    }

    /**
     * Click Create Case button
     */
    public BWFQuickCasePage clickCreateCaseButton() {
        return bwfQuickCasePage.clickCreateCaseButton();
    }

    /**
     * Get the created case ID
     *
     * @return Case ID as String
     */
    public String getCreatedCaseID() {
        String caseID = bwfQuickCasePage.getCreatedCaseID();
        Assert.assertTrue(caseID != null && !caseID.trim().isEmpty(), "BWF case is not created");
        return caseID;
    }

    public BWFDashboard searchExpandAndOpenSettings(String menuName, String settingName) {
        return bwfDashboard.searchExpandAndOpenSettings(menuName, settingName);
    }

    public BWFSettings verifyTemplatePresent(String templateName) {
        return bwfSettings.verifyTemplatePresent(templateName);
    }

    public BWFCase clickOnViewCase(String caseID) {
        return bwfCase.clickOnViewCase(caseID);
    }

    public BWFCase openUpcomingTask(String taskName) {
        return bwfCase.openUpcomingTask(taskName);
    }

    public String createCaseForUdemyData(Map<Object, String> caseData) {
        bwfDashboard.setLineOfBusiness_CaseConsole(caseData.get("Line of Business"));
        bwfDashboard.navigateToPage(SolQAConstants.CREATE, SolQAConstants.CASE);
        bwfCase.fillCaseDataForUdemy(caseData);
        return bwfCase.getCaseID();
    }

    public BWFQuickCasePage clickCloseCaseButton() {
        return bwfQuickCasePage.clickCloseCaseButton();
    }

    public void navigateToMenuItem(String menuItemName, String submenuItemName) {
        bwfDashboard.navigateToMenuItem(menuItemName, submenuItemName);
    }

    /**
     * Validate a case field value on case details page
     * This wrapper method validates a field value on case details page
     *
     * @param fieldLabel    - The label of the field to validate (e.g., "Case Template", "Category Tier 1")
     * @param expectedValue - The expected value of the field
     */
    public void validateCaseField(String fieldLabel, String expectedValue) {
        bwfDashboard.validateCaseField(fieldLabel, expectedValue);
    }


    public void clickSubMenuItem(String menuGroupName, String menuItemName) {
        bwfDashboard.clickSubMenuItem(menuGroupName, menuItemName);
    }

    public BWFFeatureMethods setRequester(String requester) {
        bwfCase.setTypeHeadInput("Requester", requester);
        return this;
    }

    public BWFFeatureMethods setTemplateName(String templateName) {
        bwfCase.setTemplateName(templateName);
        return this;
    }

    public BWFFeatureMethods clickOnSaveCase() {
        bwfCase.clickOnSaveButton();
        return this;
    }

    public BWFFeatureMethods clickOnGoToCaseBtn() {
        bwfCase.clickOnGoToCaseButton();
        return this;
    }

    public String getCaseID() {
        return bwfCase.getCaseID();
    }


    public String getAllBWFGPTFullResponseString() {
        return bwfGPT.getAllBWFGPTFullResponseString();
    }

    public BWFGPT clickFirstLinkInGPTResponse() {
        return bwfGPT.clickFirstLinkInGPTResponse();
    }

    public String getCaseDisplayIdFromCasePage() {
        return bwfGPT.getCaseDisplayId();
    }

    /**
     * Verifies Case page opened in new tab by validating Case Display ID.
     * Handles tab switching, validation, and cleanup (close new tab and return to parent).
     *
     * @return Case Display ID from the newly opened case page
     */
    public String verifyAndCloseCasePageInNewTab() {
        return bwfGPT.verifyAndCloseCasePageInNewTab();
    }

    public void validateAutoCategorizeOptioncWhileCaseCreate(Map<Object, String> caseData) {
        bwfDashboard.navigateToPage(SolQAConstants.CREATE, SolQAConstants.CASE);
        bwfCase.validateAutoCategorizeOptioncWhileCaseCreate(caseData);
    }

    // ============================================
    // Case Template Methods
    // ============================================

    /**
     * Navigate to Case Management Templates
     */
    public BWFFeatureMethods navigateToCaseManagementTemplates() {
        bwfCaseTemplate.navigateToCaseManagementTemplates();
        return this;
    }

    /**
     * Set Line of Business filter
     *
     * @param lob Line of Business name
     */
    public BWFFeatureMethods setLineOfBusinessInTemplates(String lob) {
        bwfCaseTemplate.setLineOfBusiness(lob);
        return this;
    }

    /**
     * Search and open template by name
     *
     * @param templateName Template name to search and open
     */
    public BWFFeatureMethods searchAndOpenTemplate(String templateName) {
        bwfCaseTemplate.searchAndOpenTemplate(templateName);
        return this;
    }

    /**
     * Click Edit button on template view page
     */
    public BWFFeatureMethods clickEditOnTemplate() {
        bwfCaseTemplate.clickEditOnTemplate();
        return this;
    }

    /**
     * Click Edit button on template metadata section
     */
    public BWFFeatureMethods clickEditOnTemplateMetadata() {
        bwfCaseTemplate.clickEditOnTemplateMetadata();
        return this;
    }

    /**
     * Set Template Status
     *
     * @param status Status value (Draft, Active, Retired)
     */
    public BWFFeatureMethods setTemplateStatus(String status) {
        bwfCaseTemplate.setTemplateStatus(status);
        return this;
    }

    public String getTemplateStatus() {
        return bwfCaseTemplate.getTemplateStatus();
    }

    /**
     * Set Allow case reopen
     *
     * @param value Yes or No
     */
    public BWFFeatureMethods setAllowCaseReopen(String value) {
        bwfCaseTemplate.setAllowCaseReopen(value);
        return this;
    }

    /**
     * Save Template Metadata
     */
    public BWFFeatureMethods SaveTemplateMetadata() {
        bwfCaseTemplate.SaveTemplateMetadata();
        return this;
    }

    /**
     * Save Template
     */
    public BWFFeatureMethods saveTemplate() {
        bwfCaseTemplate.saveTemplate();
        return this;
    }

    /**
     * Click Back button on template
     */
    public BWFFeatureMethods clickBackOnTemplate() {
        bwfCaseTemplate.clickBack();
        return this;
    }

    /**
     * Click Cancel button on template
     */
    public BWFFeatureMethods clickCancelOnTemplate() {
        bwfCaseTemplate.clickCancel();
        return this;
    }


    /**
     * Complete all task actions in task flow
     */
    public BWFFeatureMethods completeAllTaskActions() {
        bwfCase.completeAllTaskActions();
        return this;
    }

    /**
     * Opens a task by clicking on the task link
     *
     * @param taskIdentifier - Can be either task title or task ID (e.g., "TASK-0000000315" or "Grant Udemy account access to an employee")
     * @return BWFFeatureMethods instance for method chaining
     */
    public BWFFeatureMethods openTask(String taskIdentifier) {
        bwfCase.openTask(taskIdentifier);
        return this;
    }

    /**
     * Clicks on "Assign to me" button for a task
     *
     * @return BWFFeatureMethods instance for method chaining
     */
    public BWFFeatureMethods clickAssignToMeOnTaskInBWFCase() {
        bwfCase.clickAssignToMeOnTaskInBWFCase();
        return this;
    }

    /**
     * Clicks on "Mark as complete" button for a task
     *
     * @return BWFFeatureMethods instance for method chaining
     */
    public BWFFeatureMethods clickMarkAsCompleteOnTaskInBWFCase() {
        bwfCase.clickMarkAsCompleteOnTaskInBWFCase();
        return this;
    }

    public BWFFeatureMethods verifyReopenButtonIsNotPresent() {
        bwfCase.verifyReopenButtonIsNotPresent();
        return this;
    }

    /**
     * Shares a Resource template from the right panel "Resources" tab in BWF Live Chat.
     * This is different from sharing a Knowledge article.
     */
    public void selectResourceTabFromRightPanelAndSelectTemplate(String templateName) {
        bwfLiveChat.selectResourceTabFromRightPanelAndSelectTemplate(templateName);
    }

    public String verifyAndGetSharedKnowledgeArticle() {
        return bwfLiveChat.verifyAndGetSharedKnowledgeArticle();
    }

    public BWFFeatureMethods sendMessageInLiveChat(String message) {
        bwfLiveChat.sendMessageInLiveChat(message);
        return this;
    }

    public BWFFeatureMethods endChatAndConfirmYes() {
        bwfLiveChat.endChatAndConfirmYes();
        return this;
    }

    public BWFFeatureMethods verifyChatRemovedFromQueueAfterEnd(String message, String customerName) {
        bwfLiveChat.verifyChatRemovedFromQueueAfterEnd(message, customerName);
        return this;
    }

    public BWFFeatureMethods verifySystemGeneratedMessageInLiveChatWindow(String expectedMessage) {
        bwfLiveChat.verifySystemGeneratedMessageInLiveChatWindow(expectedMessage);
        return this;
    }

    public BWFFeatureMethods selectChatAction(String action) {
        bwfLiveChat.selectChatAction(action);
        return this;
    }

    public BWFFeatureMethods clickOkButton() {
        bwfLiveChat.clickOkButton();
        return this;
    }

    public BWFFeatureMethods customerIsRemovedFromLiveAgentQueue(String message, String customerName) {
        bwfLiveChat.customerIsRemovedFromLiveAgentQueue(message, customerName);
        return this;
    }

    public void addActivityNotesWithAttachment(String comment, File file) {
        bwfDashboard.addComment_InSocialFeed_WithAttachment(comment, file);
    }

    public void verifyAttachmentInActivityTab(String fileName) {
        bwfDashboard.verifyAttachment_In_Activity_Tab(fileName);
    }

    public boolean verifyAttachmentUpdate_In_SocialFeed(String attachmentName) {
        return bwfDashboard.verifyAttachmentUpdate_In_SocialFeed(attachmentName);
    }

    public void verifyRequestIdPresentInOpenedCase(String expectedRequestId) {
        log.info("Verifying request id is present on BWF case page: " + expectedRequestId);
        String[] candidateFields = new String[]{
                "Service Request ID",
                "Request ID",
                "Service request id",
                "Request id"
        };

        for (String field : candidateFields) {
            try {
                String value = bwfCase.getCaseDetail(field);
                if (value != null && value.contains(expectedRequestId)) {
                    log.info("Found request id in field '" + field + "': " + value);
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        // Last-resort fallback for environments where labels differ.
        Assert.assertTrue(getDriver().getPageSource().contains(expectedRequestId),
                "Expected request id not found in BWF case page: " + expectedRequestId);
    }

    public void openTaskUsingArrow(String taskName) {
        bwfToDoTasksPage.openTaskUsingArrow(taskName);
    }

    public boolean verifyAddATaskOption(String option1, String option2) {
        return bwfToDoTasksPage.verifyAddATaskOption(option1, option2);
    }

    public boolean verifyAddATaskSubOption(String parentOption, String sub1, String sub2) {
        return bwfToDoTasksPage.verifyAddATaskSubOption(parentOption, sub1, sub2);
    }

    public void goToAddParallelAdhocToDoTask() {
        bwfToDoTasksPage.goToAddParallelAdhocToDoTask("Add a task", "Add parallel task", "Add Adhoc task", "Add To Do task");
    }

    public void goToAddSequentialAdhocToDoTask() {
        bwfToDoTasksPage.goToAddSequentialAdhocToDoTask("Add a task", "Add sequential task", "Add Adhoc task", "Add To Do task");
    }

    public void goToAddParallelAdhocToDoTask(String addTaskLabel, String addParallelLabel, String addAdhocLabel, String addToDoLabel) {
        bwfToDoTasksPage.goToAddParallelAdhocToDoTask(addTaskLabel, addParallelLabel, addAdhocLabel, addToDoLabel);
    }

    public void goToAddSequentialAdhocToDoTask(String addTaskLabel, String addSequentialLabel, String addAdhocLabel, String addToDoLabel) {
        bwfToDoTasksPage.goToAddSequentialAdhocToDoTask(addTaskLabel, addSequentialLabel, addAdhocLabel, addToDoLabel);
    }

    public void setAddAToDoTaskTextFields(String fieldLabel, String value) {
        bwfToDoTasksPage.setAddAToDoTaskTextFields(fieldLabel, value);
    }

    public void setAddAToDoTaskDropDownFields(String fieldLabel, String value) {
        bwfToDoTasksPage.setAddAToDoTaskDropDownFields(fieldLabel, value);
    }

    public void save_ToDoTask() {
        bwfToDoTasksPage.save_ToDoTask();
    }

    public void close_ToDoTask() {
        bwfToDoTasksPage.close_ToDoTask();
    }

    public void assignTaskToMe() {
        bwfToDoTasksPage.assignTaskToMe();
    }

    public void markCompleteTask(String taskName) {
        bwfToDoTasksPage.markCompleteTask(taskName);
    }

    public void assignCaseToMe() {
        bwfCase.clickOnAssignToMeButton();
    }

    public BWFFeatureMethods setCaseFieldDropdownValue(String labelName, String value) {
        bwfCase.setDropDownValues(labelName, value);
        return this;
    }

    public BWFFeatureMethods openCaseAccessTab() {
        bwfCase.openCaseAccessTab();
        return this;
    }

    public BWFFeatureMethods addAgentAccessForCase(String userLoginOrFullName, boolean canEdit) {
        bwfCase.addAgentAccessForCase(userLoginOrFullName, canEdit);
        return this;
    }

    public BWFFeatureMethods addConfidentialSupportGroupForCase(String supportGroupNameOrId, boolean canEdit) {
        bwfCase.addConfidentialSupportGroupForCase(supportGroupNameOrId, canEdit);
        return this;
    }

    public String getTemplateRecordIdFromCurrentUrl() {
        return bwfCaseTemplate.getTemplateRecordIdFromCurrentUrl();
    }

    public BWFFeatureMethods clickViewSurvey() {
        bwfCase.clickViewSurvey();
        return this;
    }

    public BWFFeatureMethods verifySurveyRatingAndComment(String ratingFieldLabel, String expectedRating, String commentFieldLabel, String expectedComment) {
        bwfCase.verifySurveyRatingAndComment(ratingFieldLabel, expectedRating, commentFieldLabel, expectedComment);
        return this;
    }

    public BWFFeatureMethods verifySurveyRatingDisplayed(String ratingFieldLabel, String expectedRating) {
        bwfCase.verifySurveyRatingDisplayed(ratingFieldLabel, expectedRating);
        return this;
    }

    public boolean isSurveyFieldDisplayed(String fieldLabel) {
        return bwfCase.isSurveyFieldDisplayed(fieldLabel);
    }

    public BWFFeatureMethods clickOnGoToCaseIfPresent() {
        bwfCase.clickOnGoToCaseIfPresent();
        return this;
    }

    public BWFFeatureMethods navigateToRelatedAssetsTab() {
        bwfCase.navigateToTabByName("Related Assets");
        return this;
    }

    public BWFFeatureMethods relateAssetToCase(String ciTypeLabel, String ciTypeValue, String assetName) {
        bwfCase.relateAssetToCase(ciTypeLabel, ciTypeValue, assetName);
        return this;
    }

    public BWFFeatureMethods verifyRelatedAssetPresent(String assetName) {
        bwfCase.verifyRelatedAssetPresent(assetName);
        return this;
    }

    public BWFFeatureMethods navigateToRelatedTicketsTab() {
        bwfCase.navigateToTabByName("Related Tickets");
        return this;
    }

    public BWFFeatureMethods openCreateMenuAndSelectRelatedTicketType(String expectedOptionsCsv, String optionToSelect) {
        bwfCase.openCreateMenuAndSelectRelatedTicketType(expectedOptionsCsv, optionToSelect);
        return this;
    }

    public BWFFeatureMethods verifyRelatedTicketPresent(String ticketId) {
        bwfCase.verifyRelatedTicketPresent(ticketId);
        return this;
    }

    public BWFFeatureMethods verifyRelatedTicketsTableHasAtLeastOneRecord() {
        bwfCase.verifyRelatedTicketsTableHasAtLeastOneRecord();
        return this;
    }

    public BWFFeatureMethods verifyRelatedTicketRelationshipType(String ticketId, String expectedRelationshipType) {
        bwfCase.verifyRelatedTicketRelationshipType(ticketId, expectedRelationshipType);
        return this;
    }

    public BWFFeatureMethods fillCaseData(Map<Object, String> caseData) {
        bwfCase.fillCaseData(caseData);
        return this;
    }

    public BWFFeatureMethods setDynamicFieldValues(String que, String value) {
        bwfCase.setDynamicFieldValues(que, value);
        return this;
    }

    public BWFFeatureMethods ok_Case() {
        bwfCase.ok_Case();
        return this;
    }

    public BWFFeatureMethods searchAndOpenKA(String caseID) {
        bwfDashboard.searchKnowledge(caseID);
        bwfDashboard.openKnowledge(caseID);
        return this;
    }

    public BWFFeatureMethods clickCommonConfigurationsAndSetValue(String configName, String configValue) {
        bwfSettings.clickCommonConfigurationsAndSetValue(configName, configValue);
        return this;
    }

    public int numberOfTimesRequestIDIsPresentInCaseConsoleGrid(String requestId) {
        return bwfDashboard.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(requestId);
    }

    public List<String> getVisibleValuesFromCaseConsoleColumn(String columnName) {
        return bwfDashboard.getVisibleValuesFromCaseConsoleColumn(columnName);
    }

    public String getCaseSummary() {
        return bwfCase.getCaseSummary();
    }

    public BWFFeatureMethods verifyCollaboratorPresent(String collaboratorName) {
        bwfCase.verifyCollaboratorPresent(collaboratorName);
        return this;
    }

    public BWFFeatureMethods verifyCollaboratorNotPresent(String collaboratorName) {
        bwfCase.verifyCollaboratorNotPresent(collaboratorName);
        return this;
    }

    public BWFFeatureMethods navigateToTasksTab() {
        bwfCase.navigateToTasksTab();
        return this;
    }

    public boolean isTaskPresentOnCase(String taskSummary) {
        bwfCase.navigateToTasksTab();
        return bwfCase.isTaskPresentOnCase(taskSummary);
    }

    public BWFFeatureMethods setLOBStatus(String lobName, String desiredStatus) {
        bwfSettings.setLOBStatus(lobName, desiredStatus);
        return this;
    }

    public String waitForCaseStatus(String expectedStatus, int maxAttempts, int waitMs) {
        String actualStatus = "";
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            refreshBrowser();
            actualStatus = getCaseStatus();
            if (expectedStatus.equalsIgnoreCase(actualStatus)) {
                return actualStatus;
            }
            log.info("Case status is '" + actualStatus + "' on attempt " + attempt + "/" + maxAttempts + "; retrying.");
            commonWaits.waitForSometime(waitMs);
        }
        return actualStatus;
    }

    public boolean verifyFieldPresent_in_Related_Tickets(String fieldLabel) {
        return bwfCase.verifyFieldPresent_in_Related_Tickets(fieldLabel);
    }

    public BWFFeatureMethods clickOption(String option) {
        bwfCase.clickOption(option);
        return this;
    }

    public boolean verifyDropDownValues(String dropdownValues) {
        return bwfCase.verifyDropDownValues(dropdownValues);
    }

    public BWFFeatureMethods clickIncidentOption() {
        bwfCase.clickIncidentOption();
        return this;
    }

    public BWFFeatureMethods SetDropdownValueforBWFIncident(String dropdown, String value) {
        bwfCase.SetDropdownValueforBWFIncident(dropdown, value);
        return this;
    }

    public BWFFeatureMethods saveIncident() {
        bwfCase.saveIncident();
        return this;
    }
    
    public boolean verifyRelationshipType(String relationshipType) {
        return bwfCase.verifyRelationshipType(relationshipType);
    }

    public void verify_Activity_Updated() {
        bwfCase.verify_Activity_Updated();
    }
    
    public String INCID() {
        return bwfCase.INCID();
    }

    public String UserID() {
        return bwfCase.UserID();
    }

    public boolean verifyStatusUpdate_In_SocialFeed(String value) {
        return bwfDashboard.verifyStatusUpdate_In_SocialFeed(value);
    }

    public BWFFeatureMethods clickOnAddWorkNote() {
        bwfCase.clickOnAddWorkNote();
        return this;
    }

    public BWFFeatureMethods selectWorkNoteType(String workNoteType) {
        bwfCase.selectWorkNoteType(workNoteType);
        return this;
    }

    public BWFFeatureMethods AddWorkNote(String workNoteText, String workNoteType) {
        bwfCase.AddWorkNote(workNoteText, workNoteType);
        return this;
    }

    public boolean verifyBody_In_SocialFeed(String body) {
        return bwfCase.verifyBody_In_SocialFeed(body);
    }

    public BWFFeatureMethods AddBoldItalicWorkNote(String workNoteText, String workNoteType) {
        bwfCase.AddBoldItalicWorkNote(workNoteText, workNoteType);
        return this;
    }

    public BWFFeatureMethods setSearchTextField(String value) {
        bwfDashboard.setSearchTextField(value);
        return this;
    }
    
    public BWFFeatureMethods clickShowApproversList(){
        bwfCase.clickShowApproversList();
        return this;
    }

    public BWFFeatureMethods openTicketID(String LOB, String requestID, String columnName) {
        setLineOfBusiness_CaseConsole(LOB);
        removeAllFiltersOnCaseWorkspaceIfPresent();
        setSearchTextField_CaseConsole(requestID);
        clickSearchBtn_CaseConsole();
        clickCaseIDLink_CaseConsole(requestID, columnName);
        return this;
    }

    public String getAndVerifyCaseID() {
        String caseID = getCaseID();
        Assert.assertNotNull(caseID, "BWF Case ID is null");
        Assert.assertFalse(caseID.trim().isEmpty(), "BWF Case ID is empty");
        return caseID;
    }

    public BWFFeatureMethods clickShoMoreLinkInAuditIfPresent() {
        bwfCase.expandAuditActivityToRevealAllFields();
        return this;
    }

    public BWFFeatureMethods navigateToSubmenuInSettings(String settingName) throws Exception {
        bwfSettings.navigateToSubmenuInSettings(settingName);
        return this;
    }

    public BWFFeatureMethods setSearchValueOnCaseTemplatesGrid(String searchValue) {
        bwfSettings.setSearchValueOnCaseTemplatesGrid(searchValue);
        return this;
    }

    public BWFFeatureMethods clickSearchBtnOnCaseTemplateGrid() {
        bwfSettings.clickSearchBtnOnCaseTemplateGrid();
        return this;
    }

    public boolean verifyValueInCaseTemplateGrid(String expectedValue) {
        return bwfSettings.verifyValueInCaseTemplateGrid(expectedValue);
    }

    public BWFFeatureMethods openGridItemByName(String itemName) {
        bwfSettings.openGridItemByName(itemName);
        return this;
    }

    public BWFFeatureMethods changeTemplateStatus(String TemplateStatus) {
        bwfSettings.changeTemplateStatus(TemplateStatus);
        return this;
    }

    public BWFFeatureMethods clickOnManageDynamicFields_ByChangingTemplateStatus() {
        bwfSettings.clickOnManageDynamicFields_ByChangingTemplateStatus();
        return this;
    }

    public BWFFeatureMethods addDynamicField(String value){
        bwfSettings.addDynamicField(value);
        return this;
    }

    public BWFFeatureMethods clickOnSearchedAdminSettingWithoutClickToExpand(String settingName) {
        bwfSettings.clickOnSearchedAdminSettingWithoutClickToExpand(settingName);
        return this;
    }

    public boolean verifyTitle_In_SocialFeed(String title){
        return bwfCase.verifyTitle_In_SocialFeed(title);
    }
    
    public String getDescriptionValue() {
        return bwfCase.getDescriptionValue();
    }

    public BWFFeatureMethods setDynamicFieldToHidden(String fieldName, boolean hidden) {
        bwfSettings.setDynamicFieldToHidden(fieldName, hidden);
        return this;
    }
}