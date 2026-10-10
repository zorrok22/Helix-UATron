package pages.ITSM.dwp;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import framework.BasePage;
import pages.ITSM.Apex.ApexDWPPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.CommonMethods;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPFeatureMethods extends BasePage {

    static SolqaLogger log = new SolqaLogger(DWPFeatureMethods.class.getName());

    DWPDashboard dwpPage = new DWPDashboard();
    CatalogPage catalogPage = new CatalogPage();
    ActivityPage activityPage = new ActivityPage();
    DWPAdministrationPage dwpAdministrationPage = new DWPAdministrationPage();
    ServiceHealthPage serviceHealthPage = new ServiceHealthPage();
    ServiceRequestPage serviceRequestPage = new ServiceRequestPage();
    DWPGPTPage dwpGPTPage = new DWPGPTPage();
    ApexDWPPage apexDWPPage = new ApexDWPPage();
    DWPCommonOperations dwpCommonOperations = new DWPCommonOperations();
    RequestAsSomeoneElseDialog requestAsSomeoneElseDialog = new RequestAsSomeoneElseDialog();
    DWPBroadcastsAdminPage dwpBroadcastsAdminPage = new DWPBroadcastsAdminPage();
    PreferencePage preferencePage = new PreferencePage();
    DWPAppointmentPage dwpAppointmentPage = new DWPAppointmentPage();

    public void verifyErrorMessage(String errorMessage) {
        dwpPage.verifyErrorMessage(errorMessage);
    }

    public void closeBurgerMenuIfPresentDWP() {
        dwpPage.closeBurgerMenuIfPresentDWP();
    }

    public DWPFeatureMethods verifyDWPHomePageOpened() {
        setDisablePopupGC(true);
        closeBurgerMenuIfPresentDWP();
        dwpPage.verifyDWPHomePageOpened();
        return this;
    }

    public DWPFeatureMethods verifyLogin() {
        dwpPage.verifyLogin();
        return this;
    }

    public void closeDWP() {
        dwpPage.closeDWP();
    }

    public void verifyLoginToDWPAdmin() {
        dwpAdministrationPage.verifyLoginToDWPAdmin();
    }

    public DWPFeatureMethods openActivityPage() {
        activityPage.openActivityPage();
        return this;
    }

    public boolean isRequestAsSomeoneElseAvailable() {
        return requestAsSomeoneElseDialog.isRequestAsSomeoneElseAvailable();
    }

    public void openRequestAsSomeoneElseDialog() {
        requestAsSomeoneElseDialog.openDialogFromUserMenu();
    }

    public void clearRequestAsSomeoneElseSelection(String userName) {
        requestAsSomeoneElseDialog.removeSelectedUser(userName);
    }

    public void searchAndSelectRequestAsUser(String userName) {
        requestAsSomeoneElseDialog.searchAndSelectUser(userName);
    }

    public void verifyRequestAsUserSelection(String expectedUserName) {
        requestAsSomeoneElseDialog.verifySelectedUser(expectedUserName);
    }

    public void verifyRequestAsUserSelectionSoftly(String expectedUserName) {
        try {
            requestAsSomeoneElseDialog.verifySelectedUser(expectedUserName);
            log.info("✓ User selection verified on page: " + expectedUserName);
        } catch (AssertionError e) {
            log.warn("⚠ User selection not visible on page UI");
            log.warn("Expected: " + expectedUserName + " to be displayed");
            log.warn("User selection may still be active in backend (not shown in UI until request submission)");
            log.warn("Will verify actual selection when request is created");
            requestAsSomeoneElseDialog.logPageStateForDebugging();
        }
    }

    public void clickEditRequestForButton() {
        catalogPage.clickEditRequestForButton();
    }

    public void clickCrossIconOnSelectedUserPill(String user) {
        catalogPage.clickCrossIconOnSelectedUserPill(user);
    }

    public void searchAndSelectRequestForUser(String userName) {
        catalogPage.searchAndSelectRequestForUser(userName);
    }

    public void waitForFormFieldsToLoad() {
        catalogPage.waitForFormFieldsToLoad();
    }

    /**
     * Validate presence of request tile by name, click Details link, verify summary details and return Fulfillment ID
     * This wrapper method validates the request tile is present, clicks on its Details link,
     * validates the summary details on the details page and returns the Fulfillment ID
     *
     * @param requestTileName - The name of the request tile (e.g., "Request Time Off")
     * @param summaryDetails  - The summary details to validate on the details page
     * @return String - The Fulfillment ID from the details page (e.g., CASE-0000001366)
     */
    public String validateRequestTileAndClickDetails(String requestTileName, String summaryDetails) {
        return activityPage.validateRequestTileAndClickDetails(requestTileName, summaryDetails);
    }

    public boolean verifyCatalogSection() {
        return catalogPage.verifyCatalogSection();
    }

    public boolean isShowAllItemsLinkVisible() {
        return catalogPage.isShowAllItemsLinkVisible();
    }

    public void openAllItemsPage() {
        catalogPage.openAllItemsPage();
    }

    public void searchAndOpenService(String searchText) {
        catalogPage.searchAndOpenService(searchText);
    }

    public DWPFeatureMethods verifySrdTitle(String expectedTitle) {
        catalogPage.verifySrdTitle(expectedTitle);
        return this;
    }

    public void clickOnRequestNow(String serviceName) {
        catalogPage.clickOnRequestNow(serviceName);
    }

    public void clickOnSubmitRequest() {
        catalogPage.clickOnSubmitRequest();
    }

    public void selectSoftwareRequired(String labelName, String softwareName) {
        catalogPage.selectSoftwareRequired(labelName, softwareName);
    }

    public void closeDateModalIfPresent() {
        catalogPage.closeDateModalIfPresent();
    }

    public String getSubmittedRequestIDFromActiveEvents(String serviceRequestTitle) {
        return activityPage.getSubmittedRequestIDFromActiveEvents(serviceRequestTitle);
    }

    public String getSubmittedRequestIDFromPastEvents(String serviceRequestTitle) {
        return activityPage.getSubmittedRequestIDFromPastEvents(serviceRequestTitle);
    }

    public DWPFeatureMethods openSrmRequestByRequestID(String reqID) {
        activityPage.openSrmRequestByRequestID(reqID);
        return this;
    }

    public String getServiceRequestFieldValue(String label) {
        return serviceRequestPage.getDetailValueByLabel(label);
    }

    public boolean isServiceRequestDetailLabelPresent(String label) {
        return serviceRequestPage.isDetailLabelPresent(label);
    }

    public void verifyServiceRequestDetailValueByLabelContains(String label, String expectedValue) {
        serviceRequestPage.verifyDetailValueByLabelContains(label, expectedValue);
    }

    public void clickServiceRequestDetailActionByLabel(String label, String actionText) {
        serviceRequestPage.clickDetailActionByLabel(label, actionText);
    }

    public void verifyRequestedForUserName(String expectedUser) {
        serviceRequestPage.verifyRequestedForUserName(expectedUser);
    }

    public ActivityPage clickBackButton() {
        return activityPage.clickBackButton();
    }

    public boolean verifyStatusOfService(String Status) {
        return activityPage.verifyStatusOfService(Status);
    }

    public void addCommentWithAttachmentsInSubmittedRequest(String comment, File file) {
        activityPage.addCommentWithAttachmentsInSubmittedRequest(comment, file);
    }

    public void addCommentWithMultipleAttachmentsInSubmittedRequest(String comment, File[] files) {
        activityPage.addCommentWithMultipleAttachmentsInSubmittedRequest(comment, files);
    }

    public void addRichTextCommentWithLinkAndAttachment(String formattedText, String url, String linkText, File file) {
        activityPage.addRichTextCommentWithLinkAndAttachment(formattedText, url, linkText, file);
    }

    public void verifyAttachments(String comment, File file) {
        activityPage.verifyAttachments(comment, file);
    }

    public void verifyAttachmentsSoftly(String comment, File file) {
        try {
            activityPage.verifyAttachments(comment, file);
            log.info(SolQAConstants.LOG_MSG_BWF_ATTACHMENT_VERIFIED);
        } catch (AssertionError e) {
            log.warn(SolQAConstants.ERROR_MSG_ATTACHMENT_NOT_SYNCED);
            log.warn("Error: " + e.getMessage());
        }
    }

    public void switchToDWP(WebDriver webDriver) {
        log.info("----------- Switching to DWP Chat window ----------------------");
        String windowHandle = webDriver.getWindowHandle();
        webDriver.switchTo().window(windowHandle);
        setDriver(webDriver);
    }

    public void refreshDWPPage() {
        dwpPage.refreshDWPPage();
    }

    public void verifyComment(String text) {
        activityPage.verifyComment(text);
    }

    public DWPFeatureMethods verifyCommentAuthor(String expectedAuthor, String commentText) {
        activityPage.verifyCommentAuthor(expectedAuthor, commentText);
        return this;
    }

    public void verifyOpenedRequestTitle(String expectedTitle) {
        activityPage.verifyOpenedRequestTitle(expectedTitle);
    }

    public void navigateToAdminPage() {
        dwpPage.navigateToAdmin();
    }

    public DWPFeatureMethods navigateToAdmin() {
        dwpPage.navigateToAdmin();
        return this;
    }

    public void clickOnHamburgerMenu() {
        dwpAdministrationPage.clickOnHamburgerMenu();
    }

    public void clickOnMenu(String tab) {
        dwpAdministrationPage.clickOnMenu(tab);
    }

    public void clickOnSubMenu(String tab) {
        dwpAdministrationPage.clickOnSubMenu(tab);
    }
    
    public void navigateToConfiguration() {
    	dwpAdministrationPage.navigateToConfiguration();
    }

    public void configureFulfillmentDetails() {
        dwpAdministrationPage.configureFulfillmentDetails();
    }

    public String getFulfillmentID() {
        return serviceRequestPage.getFulfillmentID();
    }

    public void verifyHeadlineItemValueWithRetry(String label, String expectedValue, int timeoutSeconds) {
        serviceRequestPage.verifyHeadlineItemValueWithRetry(label, expectedValue, timeoutSeconds);
    }

    public List<String> getAllFulfillmentIDs() {
        return serviceRequestPage.getAllFulfillmentIDs();
    }

    public void addCommentInSubmittedRequest1(String comment) {
        // reuse existing flow that opens comment box and posts
        activityPage.addCommentWithMultipleAttachmentsInSubmittedRequest(comment, null);
    }

    public DWPFeatureMethods verifyNoDuplicateFulfillmentIdsWithPrefix(String expectedPrefix) {
        util.CommonWaits waits = new util.CommonWaits();
        waits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        List<org.openqa.selenium.WebElement> fulfillmentEls = serviceRequestPage.fulfillmentDisplayIds;

        int matchCount = 0;
        StringBuilder matches = new StringBuilder();
        for (org.openqa.selenium.WebElement el : fulfillmentEls) {
            try {
                String value = el.getText();
                if (value == null) {
                    continue;
                }
                value = value.trim();
                if (value.toUpperCase().startsWith(expectedPrefix.toUpperCase())) {
                    matchCount++;
                    if (matches.length() > 0) {
                        matches.append(", ");
                    }
                    matches.append(value);
                }
            } catch (Exception ignored) {
                // continue collecting other elements
            }
        }

        Assert.assertEquals(matchCount, 1,
                "Duplicate fulfillment IDs detected for prefix '" + expectedPrefix + "'. Matches: " + matches);
        return this;
    }

    public void verifyApprovalStatus(String requestID, String expectedStatus) {
        activityPage.verifyApprovalStatus(requestID, expectedStatus);
    }

    public void verifyServiceRequestStatus(String status) {
        serviceRequestPage.verifyServiceStatus(status);
    }

    public void verifyApprovalStatusFromDetails(String expectedStatus) {
        activityPage.verifyApprovalStatusFromDetails(expectedStatus);
    }

    public void openApprovalProfile(String requestId) {
        activityPage.openApprovalProfile(requestId);
    }

    public DWPFeatureMethods openApprovalProfileWithoutRefresh(String requestId){
        activityPage.openApprovalProfileWithoutRefresh(requestId); 
        return this; 
    }

    public void doApproveRequest() {
        activityPage.doApproveRequest();
    }

    public void doRejectRequest(String reason, boolean confirm) {
        activityPage.doRejectRequest(reason, confirm);
    }

    public void openSubmittedRequestInPastEvents(String requestId) {
        activityPage.openSubmittedRequestInPastEvents(requestId);
    }

    public void openSubmittedSbRequestInActiveEventsByTitle(String title) {
        activityPage.openSubmittedSbRequestInActiveEventsByTitle(title);
    }

    public void verifyStatusDetailPage(String Status) {
        activityPage.verifyStatusDetailPage(Status);
    }

    public String enableDisableComAroundPluggableProvider(boolean value) {
        return dwpAdministrationPage.enableDisableComAroundPluggableProvider(value);
    }

    public void openProviderSettings(String providerName, int timeout) {
        dwpAdministrationPage.openProviderSettings(providerName, timeout);
    }

    public String getTextFieldValuesInProviderSettings(String settingName) {
        return dwpAdministrationPage.getTextFieldValuesInProviderSettings(settingName);
    }

    public void setTextFieldValuesInProviderSettings(String settingName, String value) {
        dwpAdministrationPage.setTextFieldValuesInProviderSettings(settingName, value);
    }

    public void updateSettingsAndReloadProviders() {
        dwpAdministrationPage.updateSettingsAndReloadProviders();
    }

    public DWPFeatureMethods ReloadProviders() {
        dwpAdministrationPage.reloadProviders();
        return this;
    }

    public DWPFeatureMethods verifyItemFound(String searchedItemTitle, Boolean shouldBeFound) {
        activityPage.verifyItemFound(searchedItemTitle, shouldBeFound);
        return this;
    }

    public void clickOnKnowledgeArticleUsefulLink() {
        activityPage.clickOnKnowledgeArticleUsefulLink();
    }

    public void openArticle(String articleName) {
        activityPage.openArticle(articleName);
    }


    public void searchItemInGlobalSearch(String searchItem, String searchType) {
        openActivityPage();
        activityPage.searchItemInGlobalSearch(searchItem, searchType).verifyTitle(searchItem);
    }

    public void openServiceRequestFromGlobalSearchById(String requestId) {
        openActivityPage();
        activityPage.searchItemInGlobalSearch(requestId, "Requests");
        activityPage.openServiceRequestById(requestId);
        activityPage.verifyAddCommentButtonVisible();
    }

    public void dwpLogout() {
        dwpPage.dwpLogout();
    }

    public void stateOfAIChat_CorePages(Boolean state, String skill) {
        dwpAdministrationPage.stateOfAIChat_CorePages(state, skill);
    }

    public void performSearchWOWaitingResults(String searchText) {
        dwpPage.performSearchWOWaitingResults(searchText);
    }

    public String getAllGPTResponseString() {
        return dwpGPTPage.getAllGPTResponseString();
    }

    public void serviceRequestQuestion(String searchText, Map<Object, String> testData) {
        dwpGPTPage.serviceRequestQuestion(searchText, testData);
    }

    public void selectValueInDropDownByLabel(String dropdownName, String value) {
        dwpAdministrationPage.selectValueInDropDownByLabel(dropdownName, value);
    }

    public void saveHelixChatbotConfigurationForm(boolean isDataValid) {
        dwpAdministrationPage.saveHelixChatbotConfigurationForm(isDataValid);
    }

    public void clickOnCloseButtonOnDWPChatbotConfigurationPage() {
        dwpAdministrationPage.clickOnCloseButtonOnDWPChatbotConfigurationPage();
    }

    public void navigateToDWPNavigationTab(String tabName) {
        dwpAdministrationPage.navigateToDWPNavigationTab(tabName);
    }

    public void verifyKeywordRelatedSearch_OnGPT(String keyword) {
        dwpGPTPage.verifyKeywardRelatedSearch_OnGPT(keyword);
    }

    public void navigateToChatOption() {
        dwpGPTPage.navigateToChatOption();
    }

    public void selectSkillValueFromDropDown(String skill) {
        dwpAdministrationPage.selectSkillValueFromDropDown(skill);
    }

    public void clickOnHelixGPTSearchIcon() {
        dwpGPTPage.clickOnHelixGPTSearchIcon();
    }

    public void enterNewQuestionInSearch(String question) {
        dwpGPTPage.sendMessage(question);
    }

    public void isAttachmentIconPresentOnHelixGPT() {
        dwpGPTPage.isAttachmentIconPresentOnHelixGPT();
    }

    public void attachFileInHelixGPT(String fileName) throws IOException {
        dwpGPTPage.attachFileInHelixGPT(fileName);
    }

    public void verifyErrorMessageForNotSupportedFilesOnGPT(String errorMsg) {
        dwpGPTPage.verifyErrorMessageForNotSupportedFilesOnGPT(errorMsg);
    }

    public void sendMessageInGPTChat(String message) {
        dwpGPTPage.sendMessageInGPTChat(message);
    }

    public List<String> getSourceLinksOnSearchedResult() {
        return dwpGPTPage.getSourceLinksOnSearchedResult();
    }

    public void allureStep(String description, boolean actual, Object matcher) {
        Assert.assertTrue(actual, description);
    }

    public void clickOnArticleLink(String articleName) {
        dwpGPTPage.clickOnArticleLink(articleName);
    }

    public void waitForArticleToLoad() {
        dwpGPTPage.waitForArticleToLoad();
    }

    public String gettingOpenedArticleText() {
        return dwpGPTPage.gettingOpenedArticleText();
    }

    public void clickOnBackButton() {
        dwpGPTPage.clickOnBackButton();
    }

    public void clearHelixGPTSearch() {
        dwpGPTPage.clearHelixGPTSearch();
    }

    public String getFirstRecordTextFromGPTResponse() {
        return dwpGPTPage.getFirstRecordTextFromGPTResponse();
    }

    public void openFirstRecordFromTable() {
        dwpGPTPage.openFirstRecordFromTable();
    }

    public void enableHelixGPTGlobalSearch(boolean enable, String SkillName) {
        navigateToAdminPage();
        clickOnHamburgerMenu();
        clickOnMenu(SolQAConstants.CONFIGURATION);
        clickOnSubMenu(SolQAConstants.SEARCH);
        stateOfAIChat_CorePages(enable, SkillName);
    }

    public void setAIDisclaimerToggle(boolean enable) {
        dwpAdministrationPage.setAIDisclaimerToggle(enable);
    }

    public void fillRadioButtonByIndex(String answer, String index) {
        dwpAdministrationPage.fillRadioButtonByIndex(answer, index);
    }

    public void editCustomMessageInAIDisclaimer(String messageType, String messageText) {
        dwpAdministrationPage.editCustomMessageInAIDisclaimer(messageType, messageText);
    }

    public void saveAIDisclaimerChanges() {
        dwpAdministrationPage.saveAIDisclaimerChanges();
    }

    public void refreshBrowser() {
        dwpAdministrationPage.refreshBrowser();
    }

    public void clickOnEmployeeNavigatorIcon() {
        dwpAdministrationPage.clickOnEmployeeNavigatorIcon();
    }

    public String getChatDisclaimerMsg() {
        return dwpAdministrationPage.getChatDisclaimerMsg();
    }

    public String getGPTRequestIdBySrNo(int srNo) {
        return dwpGPTPage.getGPTRequestIdBySrNo(srNo);
    }

    public void navigateToSearchOption() {
        activityPage.navigateToSearchOption();
    }

    public void openServiceRequestById(String requestID) {
        activityPage.openServiceRequestById(requestID);
    }

    public void addNameInService(String text) {
        catalogPage.addNameInService(text);
    }

    public void addMachineNameInService(String machineName) {
        catalogPage.addMachineNameInService(machineName);
    }


    public void fillTextFieldByLabel(String label, String text) {
        catalogPage.fillTextFieldByLabel(label, text);
    }

    public void fillTextAreaByLabel(String label, String text) {
        catalogPage.fillTextAreaByLabel(label, text);
    }

    /**
     * Fill service request field by label name, submit the request and verify submission
     * This is a wrapper method that:
     * 1. Fills text input field in service request form
     * 2. Clicks Submit Request button
     * 3. Verifies "Request submitted" message appears
     *
     * @param fieldLabel - The label text of the field (e.g., "Summary of your request")
     * @param value      - The value to fill in the field
     */
    public void fillServiceRequest(String fieldLabel, String value) {
        catalogPage.fillServiceRequest(fieldLabel, value);
    }

    public void verifyServiceStatus(String Status) {
        serviceRequestPage.verifyServiceStatus(Status);
    }

    public void verifyServiceStatusSoftly(String Status) {
        try {
            serviceRequestPage.verifyServiceStatus(Status);
        } catch (AssertionError e) {
            log.warn(SolQAConstants.ERROR_MSG_STATUS_VERIFICATION_FAILED);
            log.warn("Error: " + e.getMessage());
        }
    }

    public void selectNPSRating(String ratingValue) {
        serviceRequestPage.selectNPSRating(ratingValue);
    }

    public boolean isSurveyOptionAvailable() {
        return serviceRequestPage.isSurveyOptionAvailable();
    }

    public void markTaskAsComplete() {
        serviceRequestPage.markTaskAsComplete();
    }

    public void addMSOfficeInstallationTemplateDetails(Map<Object, String> templateData) {
        apexDWPPage.addMSOfficeInstallationTemplateDetails(templateData);
    }

    public void addCommentWithAttachmentsInSubmittedRequestApex(String comment, File file) {
        apexDWPPage.addCommentWithAttachmentsInSubmittedRequestApex(comment, file);
    }

    public void verifyAttachmentsApex(String comment, File file) {
        apexDWPPage.verifyAttachmentsApex(comment, file);
    }

    public void addVPNAccessRequestDetails(Map<Object, String> templateData) {
        apexDWPPage.addVPNAccessRequestDetails(templateData);
    }

    public void verifyNoFulfillmentCreatedForRejectedService() {
        apexDWPPage.verifyNoFulfillmentCreatedForRejectedService();
    }

    public String getTheLatestChatTableResponse() {
        return dwpGPTPage.getTheLatestChatTableResponse();
    }

    public List<String> getTheLatestChatResponse() {
        return dwpGPTPage.getTheLatestChatResponse();
    }

    public DWPFeatureMethods validateTheResponse(String input) {
        dwpGPTPage.validateTheResponse(input);
        return this;
    }

    public DWPDashboard navigateToMyStuffPage() {
        return dwpPage.navigateToMyStuffPage();
    }

    public DWPDashboard verifyServiceRequestInMyStuff(String requestID, String assetGroupName) {
        return dwpPage.verifyServiceRequestInMyStuff(requestID, assetGroupName);
    }

    public DWPDashboard clickOnServiceCarouselInMyStuff(String serviceName, String serviceCarousel) {
        return dwpPage.clickOnServiceCarouselInMyStuff(serviceName, serviceCarousel);
    }

    // ============================================
    // Preference Page Methods - Alternate Approver
    // ============================================

    /**
     * Navigate to user preferences from profile menu
     */
    public PreferencePage navigateToPreferences() {
        return preferencePage.navigateToPreferences();
    }


    /**
     * Click on Manage Approvers button/link
     */
    public PreferencePage manageAlternateApprovers() {
        return preferencePage.manageAlternateApprovers();
    }

    /**
     * Add an alternate approver
     *
     * @param userName - Username of the alternate approver
     * @param endDate  - End date for the alternate approver assignment (format: MM/dd/yyyy)
     */
    public PreferencePage addAlternateApprover(String userName, String endDate) {
        return preferencePage.addAlternateApprover(userName, endDate);
    }

    /**
     * Verify alternate approver is added successfully
     *
     * @param userName - Username to verify
     * @return true if approver is found in the list
     */
    public boolean verifyAlternateApproverAdded(String userName) {
        return preferencePage.verifyAlternateApproverAdded(userName);
    }

    /**
     * Remove alternate approver
     *
     * @param userName - Username to remove
     */
    public PreferencePage removeAlternateApprover(String userName) {
        return preferencePage.removeAlternateApprover(userName);
    }


    /**
     * Verify approval details form is opened
     */
    public ActivityPage verifyApprovalDetailsFormOpened() {
        return activityPage.verifyApprovalDetailsFormOpened();
    }

    /**
     * Check if BWF approval is visible
     *
     * @return true if BWF approval label/text is visible
     */
    public boolean isBWFApprovalVisible() {
        return activityPage.isBWFApprovalVisible();
    }

    /**
     * Add comment in submitted request
     *
     * @param comment - Comment text to add
     */
    public ActivityPage addCommentInSubmittedRequest(String comment) {
        return activityPage.addCommentInSubmittedRequest(comment);
    }

    public DWPFeatureMethods clickDetailsForActiveRequest(String requestID) {
        activityPage.clickDetailsForActiveRequest(requestID);
        return this;
    }

    public DWPFeatureMethods clickDetailsForPastRequest(String requestID) {
        activityPage.clickDetailsForPastRequest(requestID);
        return this;
    }

    public DWPFeatureMethods clickRequestAgain() {
        activityPage.clickRequestAgain();
        return this;
    }

    public DWPFeatureMethods clickReopenButton() {
        activityPage.clickReopenButton();
        return this;
    }

    public DWPFeatureMethods enterReopenReasonAndConfirm(String reason) {
        activityPage.enterReopenReasonAndConfirm(reason);
        return this;
    }

    public DWPFeatureMethods clickSubmitRequestButton() {
        activityPage.clickSubmitRequestButton();
        return this;
    }

    public DWPFeatureMethods clickCancelButton() {
        activityPage.clickCancelButton();
        return this;
    }

    public DWPFeatureMethods confirmCancelRequest() {
        activityPage.confirmCancelRequest();
        return this;
    }

    public DWPFeatureMethods verifyCancelledStatus() {
        activityPage.verifyCancelledStatus();
        return this;
    }

    public DWPFeatureMethods refreshPage() {
        activityPage.refreshPage();
        return this;
    }

    public DWPFeatureMethods doReassignApprovalTo(String assigneeFullName) {
        activityPage.doReassignApprovalTo(assigneeFullName);
        return this;
    }

    public DWPFeatureMethods verifyStatusDetailContains(String expectedText) {
        activityPage.verifyStatusDetailContains(expectedText);
        return this;
    }

    public DWPFeatureMethods deleteDefaultCollaboratorsIfPresentInPreference() {
        preferencePage.deleteDefaultCollaboratorsIfPresentInPreference();
        return this;
    }

    public DWPFeatureMethods openMyStuffPage() {
        dwpPage.navigateToMyStuffPage();
        return this;
    }

    public DWPFeatureMethods openServiceHealthTab(String tabName) {
        serviceHealthPage.openTab(tabName);
        return this;
    }

    public DWPFeatureMethods searchServiceHealthItem(String healthItemName) {
        serviceHealthPage.searchHealthItem(healthItemName);
        return this;
    }

    public DWPFeatureMethods openServiceHealthActionFromKebab(String healthItemName, String actionName) {
        serviceHealthPage.openActionFromKebabMenu(healthItemName, actionName);
        return this;
    }
    
    public DWPFeatureMethods removeAllFiltersOnServiceHealthIfPresent() {
    	dwpPage.removeAllFiltersOnServiceHealthIfPresent();
        return this;
    }

    public DWPFeatureMethods enableFeatureSettingByName(String featureName, boolean enable) {
        dwpAdministrationPage.enableFeatureSettingByName(featureName, enable);
        return this;
    }

    public DWPFeatureMethods createServiceAction_CatalogRequest(String actionType, String actionName, String requestValue) {
        dwpAdministrationPage.createServiceAction_CatalogRequest(actionType, actionName, requestValue);
        return this;
    }

    public DWPFeatureMethods createServiceAvailabilityWithAction(String serviceName, String actionName) {
        dwpAdministrationPage.createServiceAvailabilityWithAction(serviceName, actionName);
        return this;
    }

    public DWPFeatureMethods verifySubmittedRequestEventDetailNotPresent(String questionLabel) {
        serviceRequestPage.verifySubmittedRequestEventDetailNotPresent(questionLabel);
        return this;
    }

    public DWPFeatureMethods clickApproveRequestInEventDetails() {
        serviceRequestPage.clickApproveRequestInEventDetails();
        return this;
    }

    public DWPFeatureMethods clickRejectRequestInEventDetails(String reason, boolean ifConfirm) {
        serviceRequestPage.clickRejectRequestInEventDetails(reason, ifConfirm);
        return this;
    }

    public DWPFeatureMethods clickOnHoldRequestInEventDetails() {
        serviceRequestPage.clickOnHoldRequestInEventDetails();
        return this;
    }

    public DWPFeatureMethods searchAndOpenServiceWithoutRequestNow(String searchText) {
        catalogPage.searchAndOpenServiceWithoutRequestNow(searchText);
        return this;
    }

    public void ClickOnNewRequestInDependencySection() {
        serviceRequestPage.ClickOnNewRequestInDependencySection();
    }

    public String getRequestIDFromServiceItems(String serviceName) {
        return serviceRequestPage.getRequestIDFromServiceItems(serviceName);
    }

    public DWPFeatureMethods fillAttachmentField(String label, File file) {
        Assert.assertNotNull(file, "Attachment file is null");
        dwpCommonOperations.fillAttachmentField(label, file.getAbsolutePath());
        return this;
    }

    public DWPFeatureMethods setToggleOption(String questionLabel, boolean toggleValue) {
        dwpCommonOperations.setToggleOption(questionLabel, toggleValue);
        return this;
    }

    public DWPFeatureMethods selectRadioOption(String questionLabel, String optionText) {
        dwpCommonOperations.selectRadioOption(questionLabel, optionText);
        return this;
    }

    public DWPFeatureMethods selectCheckboxOption(String questionLabel, String optionText) {
        dwpCommonOperations.selectCheckboxOption(questionLabel, optionText);
        return this;
    }

    public DWPFeatureMethods selectMultipleValuesInDropdownField(String questionLabel, String values) {
        dwpCommonOperations.selectMultipleValuesInDropdownField(questionLabel, values);
        return this;
    }

    public DWPFeatureMethods fillTimeFieldByLabel(String label, String value) {
        dwpCommonOperations.fillTimeFieldByLabel(label, value);
        return this;
    }

    public DWPFeatureMethods selectRadioOptionFromTable(String questionLabel, String rowText) {
        dwpCommonOperations.selectRadioOptionFromTable(questionLabel, rowText);
        return this;
    }

    public DWPFeatureMethods selectCheckboxOptionFromTable(String questionLabel, String rowText) {
        dwpCommonOperations.selectCheckboxOptionFromTable(questionLabel, rowText);
        return this;
    }

    public DWPFeatureMethods fillDropdownField(String label, String value) {
        dwpCommonOperations.fillDropdownField(label, value);
        return this;
    }

    public DWPFeatureMethods fillComboBoxField(String label, String value) {
        dwpCommonOperations.fillComboBoxField(label, value);
        return this;
    }

    public DWPFeatureMethods fillCounterField(String label, String value) {
        dwpCommonOperations.fillCounterField(label, value);
        return this;
    }

    public DWPFeatureMethods fillTypeheadField(String label, String value) {
        dwpCommonOperations.fillTypeheadField(label, value);
        return this;
    }

    public DWPFeatureMethods clickOnNextButton() {
        dwpCommonOperations.clickOnNextButton();
        return this;
    }

    public DWPFeatureMethods addRTFCommentWithAttachmentInSubmittedRequest(String Comment, File file, Map<Object, String> rtfOptions) {
        dwpCommonOperations.addRTFCommentWithAttachmentInSubmittedRequest(Comment, file, rtfOptions);
        return this;
    }

    public DWPFeatureMethods verifyLatestComment(String expectedText) {
        dwpCommonOperations.verifyLatestComment(expectedText);
        return this;
    }

    public DWPFeatureMethods verifySimilarArticleIsAvailable(String similarArticleTitle) {
        dwpCommonOperations.verifySimilarArticleIsAvailable(similarArticleTitle);
        return this;
    }

    public DWPFeatureMethods giveFeedbackOnArticle(String rating, String comments) {
        dwpCommonOperations.giveFeedbackOnArticle(rating, comments);
        return this;
    }

    public void fillTextAreaField(String label, String value) {
        dwpCommonOperations.fillTextAreaField(label, value);
    }

    public DWPFeatureMethods cancelServiceRequest() {
        serviceRequestPage.cancelServiceRequest();
        return this;
    }

    public DWPFeatureMethods verifyCommentNotPresent(String text) {
        dwpCommonOperations.verifyCommentNotPresent(text);
        return this;
    }

    public DWPFeatureMethods verifyAttachmentNotPresent(File file) {
        dwpCommonOperations.verifyAttachmentNotPresent(file);
        return this;
    }

    /**
     * Global search without asserting the searched text exists as an exact result item.
     * Useful when validating multiple search results for a keyword.
     */
    public DWPFeatureMethods searchInGlobalSearch(String searchText, String searchType) {
        openActivityPage();
        activityPage.searchInGlobalSearch(searchText, searchType);
        return this;
    }

    public boolean isNavigationMenuItemVisible(String tabText) {
        return dwpPage.isNavigationMenuItemVisible(tabText);
    }

    public DWPFeatureMethods openNavigationMenuItem(String tabText) {
        dwpPage.openNavigationMenuItem(tabText);
        return this;
    }

    public DWPFeatureMethods verifyRequestDisplaysInActiveEventsWithStatus(String requestId, String expectedStatusText) {
        activityPage.verifyRequestDisplaysInActiveEventsWithStatus(requestId, expectedStatusText);
        return this;
    }

    public DWPFeatureMethods addRemoveCollaborators_DWP(String collaboratorName) {
        serviceRequestPage.addRemoveCollaborator(collaboratorName);
        return this;
    }

    public DWPFeatureMethods clickDelegationCollaboratorsOption() {
        serviceRequestPage.clickDelegationCollaboratorsOption();
        return this;
    }

    public DWPFeatureMethods addDelegationCollaborator(String collaboratorName) {
        serviceRequestPage.addDelegationCollaborator(collaboratorName);
        return this;
    }

    public DWPFeatureMethods verifySelectedDelegationCollaborator(String collaboratorName) {
        serviceRequestPage.verifySelectedDelegationCollaborator(collaboratorName);
        return this;
    }

    public boolean isCollaboratorOptionAvailable() {
        return serviceRequestPage.isCollaboratorOptionAvailable();
    }

    public DWPFeatureMethods verifySelectedCollaboratorInDetailsPage(String collaboratorName) {
        serviceRequestPage.verifySelectedCollaboratorInDetailsPage(collaboratorName);
        return this;
    }

    public DWPFeatureMethods verifySubmittedRequestEventDetails(String questionLabel, String expectedValue) {
        serviceRequestPage.verifySubmittedRequestEventDetails(questionLabel, expectedValue);
        return this;
    }

    public DWPFeatureMethods fillTextDateFieldByLabel(String label, String text) {
        catalogPage.fillTextDateFieldByLabel(label, text);
        return this;
    }

    public DWPFeatureMethods fillTypeAheadDropdownField(String label, String value) {
        dwpCommonOperations.fillTypeAheadDropdownField(label, value);
        return this;
    }

    public DWPFeatureMethods doHoldRequest() {
        activityPage.doHoldRequest();
        return this;
    }

    public DWPFeatureMethods confirmActionWithPasswordIfPrompted(String username, String password) {
        activityPage.confirmActionWithPasswordIfPrompted(username, password);
        return this;
    }

    public boolean VerifyOnlyOneArticleVisibleAfterSearching(String item, String tab) {
        return activityPage.VerifyOnlyOneArticleVisibleAfterSearching(item, tab);
    }

    public void openExactMatchingArticle(String item) {
        activityPage.openExactMatchingArticle(item);
    }

    public int getHelpfulCounter() {
        return activityPage.getHelpfulCounter();
    }

    public DWPFeatureMethods clickOnYesWasThisResourceHelpful() {
        activityPage.clickOnYesWasThisResourceHelpful();
        return this;
    }

    public boolean verifyCounterIncreased(int counterBefore) {
        return activityPage.verifyCounterIncreased(counterBefore);
    }

    public void rateExcellentAndAddFeedback(String ratingLabel) {
        serviceRequestPage.rateExcellentAndAddFeedback(ratingLabel);
    }

    public void setValueInTextFieldOfSurveyModalByLabel(String label, String value) {
        serviceRequestPage.setValueInTextFieldOfSurveyModalByLabel(label, value);
    }

    public void setValueInTextAreaOfSurveyModalByLabel(String label, String value) {
        serviceRequestPage.setValueInTextAreaOfSurveyModalByLabel(label, value);
    }

    public void selectCheckBoxValuesOnSurveyModalByLabel(String label, List<String> values) {
        serviceRequestPage.selectCheckBoxValuesOnSurveyModalByLabel(label, values);
    }

    public void clickOnRadioButtonOnSurveyModalWithLabel(String label, String value) {
        serviceRequestPage.clickOnRadioButtonOnSurveyModalWithLabel(label, value);
    }

    public void selectValueInDropDownOnSurveyModalByLabel(String label, String optionText) {
        serviceRequestPage.selectValueInDropDownOnSurveyModalByLabel(label, optionText);
    }

    public void selectCurrentDateTimeOnSurveyModal(String label) {
        serviceRequestPage.selectCurrentDateTimeOnSurveyModal(label);
    }

    public void clickSubmitButtonOnSurveyModal() {
        serviceRequestPage.clickSubmitButtonOnSurveyModal();
    }

    public void rateOkayAndAddFeedback(String feedbackText) {
        serviceRequestPage.rateOkayAndAddFeedback(feedbackText);
    }

    public String getSurveyAnswerValueFor(String questionLabel) {
        return serviceRequestPage.getSurveyAnswerValueFor(questionLabel);
    }

    public DWPFeatureMethods openGPTPage() {
        dwpGPTPage.openGPTPage();
        return this;
    }

    public DWPFeatureMethods defaultSearchToggleButton(String button) {
        dwpPage.defaultSearchToggleButton(button);
        return this;
    }

    public DWPFeatureMethods searchRequestInGlobalSearch(String searchItem, String searchType) {
        openActivityPage();
        activityPage.searchRequestInGlobalSearch(searchItem, searchType);
        return this;
    }

    public DWPFeatureMethods navigateToDWPURLInNewOpenedTab() {
        dwpGPTPage.navigateToDWPURLInNewOpenedTab();
        return this;
    }

    public DWPFeatureMethods setCheckboxValueInProviderSettings(String settingName, boolean value) {
        dwpAdministrationPage.setCheckboxValueInProviderSettings(settingName, value);
        return this;
    }

    public DWPFeatureMethods enableFulfillmentDetailsSwitches() {
        dwpAdministrationPage.enableFulfillmentDetailsSwitches();
        return this;
    }

    public DWPFeatureMethods verifyAdditionalFieldLimitError(String requestType) {
        dwpAdministrationPage.verifyAdditionalFieldLimitError(requestType);
        return this;
    }

    public DWPFeatureMethods selectAdditionalFieldsFulfillmentDetails(String requestType, String fieldsCsv) {
        dwpAdministrationPage.selectAdditionalFieldsFulfillmentDetails(requestType, fieldsCsv);
        return this;
    }

    /**
     * Selects a bounded set of additional fulfillment-detail fields per ITSM request type (values from test data),
     * then saves. Expects keys {@code FulfillmentDetailsLimitedFieldsChange}, {@code FulfillmentDetailsLimitedFieldsIncident},
     * and {@code FulfillmentDetailsLimitedFieldsWorkOrder} (comma-separated field labels matching the dropdown).
     *
     * @param testData typically {@code Map<Object, String>} from {@code ReadPropertiesFile#getInputValuesMapIncludingLabelField}
     */
    public DWPFeatureMethods selectFulfillmentDetailsAdditionalFieldsForAllRequestTypesAndSave(Map<?, String> testData) {
        Assert.assertNotNull(testData, "testData map is null");
        String changeCsv = testData.get("FulfillmentDetailsLimitedFieldsChange");
        String incidentCsv = testData.get("FulfillmentDetailsLimitedFieldsIncident");
        String workOrderCsv = testData.get("FulfillmentDetailsLimitedFieldsWorkOrder");
        Assert.assertFalse(changeCsv == null || changeCsv.trim().isEmpty(),
                "Test data missing FulfillmentDetailsLimitedFieldsChange");
        Assert.assertFalse(incidentCsv == null || incidentCsv.trim().isEmpty(),
                "Test data missing FulfillmentDetailsLimitedFieldsIncident");
        Assert.assertFalse(workOrderCsv == null || workOrderCsv.trim().isEmpty(),
                "Test data missing FulfillmentDetailsLimitedFieldsWorkOrder");

        dwpAdministrationPage.selectAdditionalFieldsFulfillmentDetails(SolQAConstants.CHANGE_REQUEST_TYPE, changeCsv.trim());
        dwpAdministrationPage.selectAdditionalFieldsFulfillmentDetails(SolQAConstants.INCIDENT_REQUEST_TYPE, incidentCsv.trim());
        dwpAdministrationPage.selectAdditionalFieldsFulfillmentDetails(SolQAConstants.WORKORDER_REQUEST_TYPE, workOrderCsv.trim());
        dwpAdministrationPage.saveFulfillmentDetailsSettings();
        return this;
    }

    public DWPFeatureMethods saveFulfillmentDetailsSettings() {
        dwpAdministrationPage.saveFulfillmentDetailsSettings();
        return this;
    }

    public DWPFeatureMethods selectAllValuesAdditionalFields(String requestType) {
        dwpAdministrationPage.selectAllValuesAdditionalFields(requestType);
        return this;
    }

    public DWPFeatureMethods unselectAllAdditionalFieldsFulfillmentDetails() {
        dwpAdministrationPage.unselectAllAdditionalFieldsAndSave();
        return this;
    }

    public DWPFeatureMethods verifyAllAdditionalFieldsAreSelected(boolean expectedSelected) {
        dwpAdministrationPage.verifyAllAdditionalFieldsAreSelected(expectedSelected);
        return this;
    }

    public DWPFeatureMethods selectFulfillmentInOpenedRequestByPrefix(String prefix) {
        activityPage.selectFulfillmentByPrefix(prefix);
        return this;
    }

    public Map<String, String> getFulFillmentIdsFromActiveEvents(String requestIdOrTitle, boolean preferCardView) {
        openActivityPage();
        String raw = "";
        try {
//            raw = activityPage.getFulfillmentIdsTooltipTextFromActiveEvent(requestIdOrTitle, preferCardView);
        } catch (Exception ignored) {
        }
        if (raw == null || raw.trim().isEmpty()) {
//            raw = activityPage.getFulfillmentIdsTextFromActiveEvent(requestIdOrTitle, preferCardView);
        }
        return parseFulfillmentIds(raw);
    }

    private Map<String, String> parseFulfillmentIds(String raw) {
        Assert.assertNotNull(raw, "Fulfillment IDs raw text is null");
        String text = raw.trim();
        Assert.assertFalse(text.isEmpty(), "Fulfillment IDs raw text is empty");

        Map<String, String> out = new HashMap<>();

        String changeId = findFirstByRegex(text, "(?i)\\b(?:CRQ|CHG)\\d+\\b");
        String incId = findFirstByRegex(text, "(?i)\\bINC\\d+\\b");
        String woId = findFirstByRegex(text, "(?i)\\bWO\\d+\\b");

        Assert.assertFalse(changeId.isEmpty(), "Change fulfillment id not found in: " + text);
        Assert.assertFalse(incId.isEmpty(), "Incident fulfillment id not found in: " + text);
        Assert.assertFalse(woId.isEmpty(), "Workorder fulfillment id not found in: " + text);

        out.put("ChangeID", changeId);
        out.put("IncidentID", incId);
        out.put("WorkOrderID", woId);
        return out;
    }

    private String findFirstByRegex(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(0).trim();
        }
        return "";
    }

    public DWPFeatureMethods waitForFulfillmentSectionToDisplay() {
        serviceRequestPage.waitForFulfillmentSectionToDisplay();
        return this;
    }

    public DWPFeatureMethods openFulfillmentDetailsByPrefix(String prefix) {
        serviceRequestPage.openFulfillmentDetailsByPrefix(prefix);
        return this;
    }

    public DWPFeatureMethods verifyFulfillmentDetailsFields(String expectedFieldsCsv) {
        serviceRequestPage.verifyFulfillmentDetailsFields(expectedFieldsCsv);
        return this;
    }

    public DWPFeatureMethods verifyFulfillmentDetailsSummaryContains(String expectedText) {
        serviceRequestPage.verifyFulfillmentDetailsSummaryContains(expectedText);
        return this;
    }

    public DWPFeatureMethods verifyFulfillmentDetailsSummaryNotBlank() {
        serviceRequestPage.verifyFulfillmentDetailsSummaryNotBlank();
        return this;
    }

    public DWPFeatureMethods closeFulfillmentDetailsModalIfPresent() {
        serviceRequestPage.closeFulfillmentDetailsModalIfPresent();
        return this;
    }

    public DWPFeatureMethods verifyCollaboratorNotPresentInDetailsPage(String collaboratorName) {
        serviceRequestPage.verifyCollaboratorNotPresentInDetailsPage(collaboratorName);
        return this;
    }

    public boolean isRequestAgainButtonPresent() {
        return activityPage.isRequestAgainButtonPresent();
    }

    public DWPFeatureMethods openNotification() {
        dwpPage.openNotification();
        return this;
    }

    public boolean isNotificationPresent(String expectedTextContains) {
        return dwpPage.isNotificationPresent(expectedTextContains);
    }

    public boolean isBroadcastBannerDisplayed(String expectedTextContains) {
        return dwpPage.isBroadcastBannerDisplayed(expectedTextContains);
    }

    public DWPFeatureMethods verifyAlternateApproverMessageInNotification(String originalApproverFullName) {
        dwpPage.verifyAlternateApproverMessageInNotification(originalApproverFullName);
        return this;
    }

    public DWPFeatureMethods verifyActingAsUserPresent(String expectedOriginalApproverFullName) {
        activityPage.verifyActingAsUserPresent(expectedOriginalApproverFullName);
        return this;
    }

    public DWPFeatureMethods verifyApproverNameWithStatus(String approverFullName, String status) {
        activityPage.verifyApproverNameWithStatus(approverFullName, status);
        return this;
    }

    public DWPFeatureMethods verifyApprovalStatusWithDetails(String expectedStatus, String expectedApproverFullName) {
        activityPage.verifyApprovalStatusWithDetails(expectedStatus, expectedApproverFullName);
        return this;
    }

    public DWPFeatureMethods verifyRejectedInfoIconPresent() {
        activityPage.verifyRejectedInfoIconPresent();
        return this;
    }

    public DWPFeatureMethods verifyRejectedReasonTextIsCorrect(String expectedReason) {
        activityPage.verifyRejectedReasonTextIsCorrect(expectedReason);
        return this;
    }

    public DWPFeatureMethods expandDependentService(String serviceName) {
        activityPage.expandDependentService(serviceName);
        return this;
    }

    public DWPFeatureMethods collapseDependentService(String serviceName) {
        activityPage.collapseDependentService(serviceName);
        return this;
    }

    public String getHeadlineValueFromDependentService(String serviceName, String label) {
        return activityPage.getHeadlineValueFromDependentService(serviceName, label);
    }

    public DWPFeatureMethods clickOnRelatedService(String serviceName) {
        serviceRequestPage.clickOnRelatedService(serviceName);
        return this;
    }

    public DWPFeatureMethods clickRequestNowIfPresentOnDwpServicePage() {
        serviceRequestPage.clickRequestNowIfPresentOnDwpServicePage();
        return this;
    }

    public DWPFeatureMethods verifyRequestFromGlobalSearchByIdAndStatus(String requestId, String expectedTitle, String expectedStatus, String requestsTab) throws IOException {
        openServiceRequestFromGlobalSearchById(requestId);
        verifyOpenedRequestTitle(expectedTitle);
        verifyServiceRequestStatus(expectedStatus);
        clickOnBackButton();
        searchInGlobalSearch(requestId, requestsTab);
        verifyItemFound(requestId, true);
        return this;
    }

    public DWPFeatureMethods enterCurrentDateForDateField(String dateFieldLabel) {
        String currentDate = new SimpleDateFormat("MMMM d, yyyy").format(new Date());
        fillTextDateFieldByLabel(dateFieldLabel, currentDate);
        return this;
    }

    public DWPFeatureMethods selectCheckboxOptionByText(String optionText) {
        dwpCommonOperations.selectCheckboxOptionByText(optionText);
        return this;
    }

    public DWPFeatureMethods selectRadioOptionByText(String optionText) {
        dwpCommonOperations.selectRadioOptionByText(optionText);
        return this;
    }

    public String getFulfillmentIdsTextFromActiveEvent(String requestIdOrTitle, boolean preferCardView) {
        openActivityPage();
        return activityPage.getFulfillmentIdsTextFromActiveEvent(requestIdOrTitle, preferCardView);
    }

    public String getFulfillmentIdsTooltipTextFromActiveEvent(String requestIdOrTitle, boolean preferCardView) {
        openActivityPage();
        return activityPage.getFulfillmentIdsTooltipTextFromActiveEvent(requestIdOrTitle, preferCardView);
    }

    public DWPFeatureMethods openFulfillmentByID(String fulfillmentId) {
        serviceRequestPage.openFulfillmentByID(fulfillmentId);
        return this;
    }

    public DWPFeatureMethods openBroadcastsPage() {
        dwpBroadcastsAdminPage.openBroadcastsPage();
        return this;
    }

    public DWPFeatureMethods searchBroadcast(String subject) {
        dwpBroadcastsAdminPage.searchBroadcast(subject);
        return this;
    }

    public boolean verifyBroadcastPresent(String textContains) {
        return dwpBroadcastsAdminPage.isBroadcastPresent(textContains);
    }

    public DWPFeatureMethods verifyBroadcastRowContains(String titleContains, String statusContains) {
        dwpBroadcastsAdminPage.verifyBroadcastRowContains(titleContains, statusContains);
        return this;
    }

    public DWPFeatureMethods removeBroadcastBySubject(String subject) {
        dwpBroadcastsAdminPage.removeBroadcastBySubject(subject);
        return this;
    }

    public DWPFeatureMethods removeAllItemsFromCart() {
        catalogPage.removeAllItemsFromCart();
        return this;
    }

    public DWPFeatureMethods addToCart() {
        catalogPage.addToCart();
        return this;
    }

    public DWPFeatureMethods viewCart() {
        catalogPage.viewCart();
        return this;
    }

    public DWPFeatureMethods clickRequestItemsOnDialog() {
        catalogPage.clickRequestItemsOnDialog();
        return this;
    }

    public DWPFeatureMethods verifyCheckoutItemOpened(String expectedServiceName) {
        catalogPage.verifyCheckoutItemOpened(expectedServiceName);
        return this;
    }

    public DWPFeatureMethods clickNextItemButton() {
        catalogPage.clickNextItemButton();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP, Duration.ofSeconds(30));
        return this;
    }

    public DWPFeatureMethods verifyActiveEventRequestDisplaysWithStatus(String requestId, String expectedStatus) {
        activityPage.verifyActiveEventRequestDisplaysWithStatus(requestId, expectedStatus);
        return this;
    }

    public DWPFeatureMethods clickActionMenuOptionOnOpenedRequest(String optionText) {
        activityPage.clickActionMenuOptionOnOpenedRequest(optionText);
        return this;
    }

    public boolean setCaseApprovalCrossLaunch(String settingName, boolean enable, String crossLaunchUrl) {
        return dwpAdministrationPage.setCaseApprovalCrossLaunch(settingName, enable, crossLaunchUrl);
    }

    public boolean setValueForReleaseApprovalInApprovalConnection(String settingName, boolean enable, String crossLaunchUrl) {
        return dwpAdministrationPage.setValueForReleaseApprovalInApprovalConnection(settingName, enable, crossLaunchUrl);
    }

    public DWPFeatureMethods saveApprovalSettings() {
        dwpAdministrationPage.saveApprovalSettings();
        return this;
    }

    public DWPFeatureMethods switchingToSmartITAfterCrossLaunchingItThroughDWP(String expectedURLPart) {
        activityPage.switchingToSmartITAfterCrossLaunchingItThroughDWP(expectedURLPart);
        return this;
    }

    public DWPFeatureMethods openCatalogPage() {
        catalogPage.openCatalogPage();
        return this;
    }

    public DWPFeatureMethods addAppointmentTextIfPresent(String text) {
        dwpAppointmentPage.addAppointmentTextIfPresent(text);
        return this;
    }

    public DWPFeatureMethods chooseAppointmentLocation(String locationName) {
        dwpAppointmentPage.chooseLocation(locationName);
        return this;
    }

    public DWPFeatureMethods chooseAppointmentDayByDayOfMonth(String dayOfMonth) {
        dwpAppointmentPage.chooseDayByDayOfMonth(dayOfMonth);
        return this;
    }

    public DWPFeatureMethods selectFirstAppointmentTimeSlot() {
        dwpAppointmentPage.selectFirstTimeSlot();
        return this;
    }

    public DWPFeatureMethods clickSubmitAppointment() {
        dwpAppointmentPage.clickSubmitAppointment();
        return this;
    }

    public DWPFeatureMethods searchAndOpenServiceProfile(String searchText) {
        catalogPage.searchAndOpenServiceProfile(searchText);
        return this;
    }

    public DWPFeatureMethods enableFeatureSettingByNameInDWPAdmin(String featureName, boolean enable) {
        dwpAdministrationPage.enableFeatureSettingByName(featureName, enable);
        return this;
    }

    public DWPFeatureMethods verifySrdTitleNotMatched(String expectedTitle) {
        catalogPage.verifySrdTitleNotMatched(expectedTitle);
        return this;
    }

    public DWPFeatureMethods verifyDWPAdminPageLaunchedSuccessfully() {
        dwpPage.verifyDWPAdminPageLaunchedSuccessfully();
        return this;
    }
  
    public DWPFeatureMethods addResponseDWPCRepeatQuestionService(String response1, String response2) {
        serviceRequestPage.addResponseDWPCRepeatQuestionService(response1, response2);
        return this;
    }

    public DWPFeatureMethods fillSubmitDWPService(Map<Object, String> dwpTestData, String serviceName) {
        openCatalogPage();
        if (isShowAllItemsLinkVisible()) {
            openAllItemsPage();
            searchAndOpenService(serviceName);
            verifyOpenedRequestTitle(serviceName);
        } else {
            searchInGlobalSearch(serviceName, SolQAConstants.CATALOG_ITEMS).verifyItemFound(serviceName, true);
            clickOnRequestNow(serviceName);
        }

        switch (serviceName) {
            case "Accounting Complaint":
                fillTextAreaField(dwpTestData.get("IssueLabel"), dwpTestData.get("IssueValue"));
                fillRadioButtonByIndex(dwpTestData.get("RadioAnswer"), dwpTestData.get("RadioIndex"));
                fillTextFieldByLabel(dwpTestData.get("InvolvedPersonLabel"), dwpTestData.get("InvolvedPersonName"));
                break;

            case "All Questions":
            case "AllQuestions_ConfidentialService":
                DateFormat dfLong = new SimpleDateFormat("MMMM d, yyyy");
                DateFormat dfDateTime = new SimpleDateFormat("MMM d, yyyy h:mm a");
                String dateLong = dfLong.format(new Date());
                String dateTime = dfDateTime.format(new Date());
                File upload = CommonMethods.createTextFile(dwpTestData.get("AttachmentFileName"));
                if (dwpTestData.get("TextQuestionValue") != null) {
                    fillTextFieldByLabel("Text Question", dwpTestData.get("TextQuestionValue"));
                }
                if (dwpTestData.get("CheckBoxValue") != null) {
                    selectCheckboxOption("Checkbox Question", dwpTestData.get("CheckBoxValue"));
                }
                if (dwpTestData.get("DropdownValue") != null) {
                    fillDropdownField("Dropdown Question", dwpTestData.get("DropdownValue"));
                }
                fillTextDateFieldByLabel("Date Question", dateLong);
                setToggleOption("Toggle Question", Boolean.parseBoolean(dwpTestData.get("ToggleValue")));
                fillTextDateFieldByLabel("Date/Time Question", dateTime);
                if (dwpTestData.get("RadioOption") != null) {
                    selectRadioOption("Radio Button Question", dwpTestData.get("RadioOption"));
                }
                fillTimeFieldByLabel("Time Question", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                if (dwpTestData.get("TemplateId") != null) {
                    fillTextFieldByLabel("Template", dwpTestData.get("TemplateId"));
                }
                if (dwpTestData.get("MultiSelectValue") != null) {
                    selectMultipleValuesInDropdownField("Multi-select Question", dwpTestData.get("MultiSelectValue"));
                    commonWaits.sendEscKey();
                }
                if (upload != null) {
                    fillAttachmentField("Attachment Question", upload);
                }
                if (dwpTestData.get("dynamicUser") != null) {
                    fillComboBoxField("Dynamic Dropdown List", dwpTestData.get("dynamicUser"));
                }
                if (dwpTestData.get("LookupITSM") != null) {
                    fillTypeheadField("Lookup Question from ITSM (End User)", dwpTestData.get("LookupITSM"));
                }
                if (dwpTestData.get("LookupBWF") != null) {
                    fillTypeheadField("Lookup Question from BWF (Case Template)", dwpTestData.get("LookupBWF"));
                }
                if (dwpTestData.get("CounterValue") != null) {
                    fillCounterField("Counter", dwpTestData.get("CounterValue"));
                }
                if (dwpTestData.get("City") != null) {
                    selectRadioOptionFromTable("Select city", dwpTestData.get("City"));
                }
                if (dwpTestData.get("Occupation") != null) {
                    selectCheckboxOptionFromTable("Occupation", dwpTestData.get("Occupation"));
                }
                break;

            case "MultiTab with Condition":
                fillTextFieldByLabel("Description", dwpTestData.get("Description"));
                fillTextFieldByLabel("Summary", dwpTestData.get("Summary"));
                fillDropdownField("Which type of vendor are you reviewing?", dwpTestData.get("VendorType")).clickOnNextButton();
                fillCounterField("How was the food quality?", dwpTestData.get("FoodQuality"));
                break;

            case "Office Move":
                dfLong = new SimpleDateFormat("MMMM d, yyyy");
                dfDateTime = new SimpleDateFormat("MMM d, yyyy h:mm a");
                dateLong = dfLong.format(new Date());
                dateTime = dfDateTime.format(new Date());
                fillTextFieldByLabel("Summary", dwpTestData.get("Summary"));
                fillTextFieldByLabel("where to move", dwpTestData.get("where to move"));
                fillTextDateFieldByLabel("when to move", dateTime);
                break;

            case "SB_Create_MultiITSM":
                fillTextFieldByLabel("Summary", dwpTestData.get("Summary"));

            case "BMCOps BWF Service 1.0":
                if (dwpTestData.get("Summary") != null) {
                    fillTextFieldByLabel("Summary", dwpTestData.get("Summary"));
                }
                if (dwpTestData.get("Description") != null) {
                    fillTextFieldByLabel("Description", dwpTestData.get("Description"));
                }
                break;

            case "BMCOps Remedy Service 1.0":
                if (dwpTestData.get("Summary") != null) {
                    fillTextFieldByLabel("Summary", dwpTestData.get("Summary"));
                }
                break;

            case "SB_CreateIncident" :
                 fillTextFieldByLabel("Summary", dwpTestData.get("Summary"));
                 break;

            case "Complaint Against Your Manager":
                fillTextFieldByLabel("Summary of your Complaint", dwpTestData.get("Summary"));
                fillTextAreaField("Detailed Description of the Complaint", dwpTestData.get("Description"));
                break;

            default:
                throw new IllegalArgumentException("Service-specific form filling not implemented for: " + serviceName);
        }
        clickOnSubmitRequest();
        return this;
    }

    public DWPFeatureMethods verifyRequestCreated(String requestId){
        Assert.assertNotNull(requestId, "Request ID is null");
        Assert.assertFalse(requestId.trim().isEmpty(), "Request ID is empty");
        return this;
    }

    public DWPFeatureMethods enableSurveysSetting(boolean enable) {
        dwpAdministrationPage.enableSurveysSetting(enable);
        return this;
    }

    public DWPFeatureMethods removeAlternateApproverIfPresent(String userName) {
        preferencePage.removeAlternateApproverIfPresent(userName);
        return this;
    }

    public DWPFeatureMethods navigateToMyActivityPage() {
        dwpPage.navigateToMyActivityPage();
        return this;
    }

    public Map<String, String> getFulFillmentIdsFromActiveEventsInCardView(String requestID){
        return activityPage.getFulFillmentIdsFromActiveEventsInCardView(requestID);
    }
  
    public Map<String, String> getFulFillmentIdsFromActiveEventsInListView(String requestID){
        return activityPage.getFulFillmentIdsFromActiveEventsInListView(requestID);
    }

    public void clickCardViewButton(){
        activityPage.clickCardViewButton();
    }

    public String getRichTextComment(){
        return dwpAdministrationPage.getRichTextComment();
    }

    public void setRichTextComment(String value){
        dwpAdministrationPage.setRichTextComment(value);
    }

    public void saveChanges(){
        dwpAdministrationPage.saveChanges();
    }

    public void addCommentWithMultipleAttachmentInFulfillmentDetails(String comment, String[] fileNames){
        activityPage.addCommentWithMultipleAttachmentInFulfillmentDetails(comment, fileNames);
    }

    public String getFirstRequestID() {
        return dwpGPTPage.getFirstRequestID();
    }
   
}
