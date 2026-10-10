package pages.ITSM.SmartIT;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import framework.BasePage;
import pages.ITSM.Apex.ApexSmartITPage;
import pages.ITSM.CMDB.CMDBDashboard;
import pages.ITSM.Gainsight.GainsightSelfHelpWidget;
import pages.ITSM.Gainsight.SmartITGainsightApi;
import pages.ITSM.HelixDashboard.HelixDashboard;
import pages.ITSM.InnovationSuite.InnovationSuiteTicketConsolePage;
import pages.ITSM.dwp.DWPDashboard;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartITFeatureMethodsPage extends BasePage {

    static SolqaLogger log = new SolqaLogger(SmartITFeatureMethodsPage.class.getName());
    SmartITDashboardPage smartITDashboardPage = new SmartITDashboardPage();
    TicketConsolePage ticketConsolePage = new TicketConsolePage();
    AssetConsolePage assetConsolePage = new AssetConsolePage();
    WorkOrderPage workOrderPage = new WorkOrderPage();
    CreateIncidentPage incidentPV = new CreateIncidentPage();
    ViewIncidentPage viewIncident = new ViewIncidentPage();
    SmartITDashboardPage smartITDashboard = new SmartITDashboardPage();
    CreateWorkorderPage workorderPV = new CreateWorkorderPage();
    ViewWorkorderPage viewWorkorder = new ViewWorkorderPage();
    CreateChangePage changePV = new CreateChangePage();
    ViewChangePage viewChangePage = new ViewChangePage();
    SmartITCalendarPage smartITCalendarPage = new SmartITCalendarPage();
    ITSMInsightsPage itsmInsightsPage = new ITSMInsightsPage();
    SmartRecorderPage smartRecorderPage = new SmartRecorderPage();
    ViewRequestIDSmartRecorderPage viewRequestIDSmartRecorderPage = new ViewRequestIDSmartRecorderPage();
    DWPDashboard dwpDashboard = new DWPDashboard();
    CreateContractPage createContractPage = new CreateContractPage();
    ViewContractPage viewContractPage = new ViewContractPage();
    GlobalSearchStudioPage globalSearchStudioPage = new GlobalSearchStudioPage();
    SmartITHelixGPTPage smartITHelixGPTPage = new SmartITHelixGPTPage();
    HelixDashboard helixDashboard = new HelixDashboard();
    CommonWaits commonWaits = new CommonWaits();
    SmartITCommonOperations smartITCommonOperations = new SmartITCommonOperations();
    CreateKnowledgePage createKnowledge = new CreateKnowledgePage();
    ApexSmartITPage apexSmartITPage = new ApexSmartITPage();
    Asset asset = new Asset();
    KnownError knownError = new KnownError();
    Broadcast broadcastPV = new Broadcast();
    ImpactAnalysisPage impactAnalysisPage = new ImpactAnalysisPage();
    CMDBDashboard cmdbDashboard = new CMDBDashboard();
    SmartITLiveChatAgentConsolePage smartITLiveChatAgentConsolePage = new SmartITLiveChatAgentConsolePage();
    InnovationSuiteTicketConsolePage innovationSuiteTicketConsolePage = new InnovationSuiteTicketConsolePage();
    GainsightSelfHelpWidget gainsightSelfHelpWidget = new GainsightSelfHelpWidget();

    public void goToReportsTab() {
        smartITDashboardPage.clickReportsMenu();
    }

    public void navigateToTicketConsole() {
        smartITDashboardPage.navigateToTicketConsole();
    }

    public void navigateToLiveChatAgentConsole() {
        smartITDashboard.navigateToLiveChatAgentConsole();
    }

    public String getLiveChatAgentName() {
        return smartITLiveChatAgentConsolePage.getLiveAgentName();
    }

    public void selectChatFromQueueAndAccept(String initialMessage, String customerName) {
        try {
            smartITLiveChatAgentConsolePage
                    .clickQueueTab()
                    .selectCustomerFromChatQueue(initialMessage, customerName)
                    .clickAcceptChat();
        } catch (Exception e) {
            log.info("[SmartIT LiveChat] Falling back to latest queue chat selection. Reason: " + e.getMessage());
            smartITLiveChatAgentConsolePage
                    .selectLatestChatFromQueue()
                    .clickAcceptChat();
        }
    }

    public void sendMessageToCustomerInLiveChat(String message) {
        smartITLiveChatAgentConsolePage.typeReplyToCustomer(message);
    }

    public void uploadAttachmentInLiveChat(java.io.File file) {
        smartITLiveChatAgentConsolePage.uploadAttachment(file);
    }

    public void verifyAttachmentIsSharedInLiveChat(String attachmentFileName) {
        smartITLiveChatAgentConsolePage.verifyAttachmentIsShared(attachmentFileName);
    }

    public void verifyMessageInLiveChat(String expectedMessage) {
        smartITLiveChatAgentConsolePage.verifyMessageOnLiveChat(expectedMessage);
    }

    public void verifySystemMessageInLiveChat(String expectedMessage) {
        smartITLiveChatAgentConsolePage.verifySystemMessageOnLiveChat(expectedMessage);
    }

    public void selectArticleFromResourceTab(String articleSearchKeyword) {
        smartITLiveChatAgentConsolePage.selectArticleFromResourceTab(articleSearchKeyword);
    }

    public void shareKnowledgeArticleUnderResourceTab(String articleName) {
        smartITLiveChatAgentConsolePage.shareKnowledgeArticleUnderResourceTab(articleName);
    }

    public void verifySharedArticleOnLiveChat(String articleName) {
        smartITLiveChatAgentConsolePage.verifySharedArticleOnLiveChat(articleName);
    }

    public void endChatAndConfirmYes() {
        smartITLiveChatAgentConsolePage.clickEndChatAndConfirmYes();
    }

    public void verifyChatRemovedFromQueueAfterEnd(String initialMessage, String customerName) {
        smartITLiveChatAgentConsolePage.verifyCustomerIsNotPresentInQueueAfterChatEnded(initialMessage, customerName);
    }

    public void toggleLiveChatAutoRefresh() {
        smartITLiveChatAgentConsolePage.toggleAutoRefresh();
    }

    public void verifyLiveChatQueueTimeSLAColorForChat(String initialMessage, String expectedCssRgb) {
        smartITLiveChatAgentConsolePage.verifyQueueTimeSLAColorForChat(initialMessage, expectedCssRgb);
    }

    public void openLiveChatActionDropdownForChat(String initialMessage) {
        smartITLiveChatAgentConsolePage.clickChatActionDropdownForChat(initialMessage);
    }

    public void selectLiveChatAction(String actionLabel) {
        smartITLiveChatAgentConsolePage.selectActionFromOpenedDropdown(actionLabel);
    }

    public void transferChatToAgentFromDialog(String agentNameOrUserName) {
        smartITLiveChatAgentConsolePage.transferChatToAgentFromOpenDialog(agentNameOrUserName);
    }

    public void navigateToAssetConsole() {
        smartITDashboardPage.navigateToAssetConsole();
    }

    public void applyFiltersAndSearchInAssetConsole(String assetType, String status, String searchText) {
        assetConsolePage.applyFiltersAndSearch(assetType, status, searchText);
    }

    public boolean isUserNavigateToTicketConsole() {
        return smartITDashboardPage.isUserNavigateToTicketConsole();
    }

    public void verifyUserIsNavigatedToTicketConsole() {
        smartITDashboardPage.verifyUserIsNavigatedToTicketConsole();
    }

    public void applyFilterAndOpenTicket(String ticketType, String tiketId) {
        commonWaits.waitForSometime(10000);
        if (smartITCommonOperations.isStudioFrameVisible()) {
            innovationSuiteTicketConsolePage.applyFilterAndSearchTicket(ticketType, tiketId);
            innovationSuiteTicketConsolePage.openFirstTicket(tiketId);
        } else {
            ticketConsolePage.applyFilterAndOpenTicket(ticketType, tiketId);
        }
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void applyFilterAndOpenTicketWithNeedsAttention(String ticketType, String needsAttentionValue) {
        ticketConsolePage.applyFilterAndOpenTicketWithNeedsAttention(ticketType, needsAttentionValue);
    }

    public void applyFilterAndOpenTicketWithNeedsAttentionAndId(String ticketType, String needsAttentionValue,
                                                                String ticketID) {
        ticketConsolePage.applyFilterAndOpenTicketWithNeedsAttentionAndId(ticketType, needsAttentionValue, ticketID);
    }

    public void applyTicketConsoleKeywordsSearchAndOpenFirst(String ticketId) {
        ticketConsolePage.applyKeywordsFilterAndOpenFirstTicket(ticketId);
    }

    public String getWOCustomerName() {
        return ticketConsolePage.getWOCustomerName();
    }

    public String getWOSummary() {
        return ticketConsolePage.getWOSummary();
    }

    public String getWOPriority() {
        return ticketConsolePage.getWOPriority();
    }

    public String getWOStatus() {
        return ticketConsolePage.getWOStatus();
    }

    public boolean isAttachedFileAppearedUnderActivityTab(String expectedFileName) {
        return workOrderPage.isAttachedFileAppearedUnderActivityTab(expectedFileName);
    }

    public void addComment_InActivityFeed(String value) {
        workOrderPage.addComment_InActivityFeed(value);
    }

    public void addComment_InActivityFeed(String value, boolean isPublic) {
        workOrderPage.addComment_InActivityFeed(value, isPublic);
    }

    public void addAttachmentNote_InActivityFeed(String noteText, File fileToUpload, boolean isPublic) {
        workOrderPage.addAttachmentNote_InActivityFeed(noteText, fileToUpload, isPublic);
    }

    public void navigateToCreateNewIncident() {
        smartITDashboard.navigateToCreateNewIncident();
    }

    public void FillIncidentTemplate(Map<Object, String> smartITTestData) {
        incidentPV.switchToPWAFrame();
        incidentPV.FillIncidentTemplate(smartITTestData);
    }

    public void clickAssigntoMeInIncidentCreatePage() {
        incidentPV.clickAssigntoMe();
    }

    public void setAssigneePersonInIncidentCreatePage(String personName) {
        incidentPV.setAssigneePerson(personName);
    }

    public void setAssigneeSupportGroupInIncidentCreatePage(String supportGroup) {
        incidentPV.setAssigneeSupportGroup(supportGroup);
    }

    public void saveIncident() {
        incidentPV.saveIncident();
    }

    public String FetchIncidentTicketID() {
        String incidentID = viewIncident.FetchTicketID();
        Assert.assertNotNull(incidentID, "Incident ID is null");
        Assert.assertFalse(incidentID.trim().isEmpty(), "Incident ID is empty");
        return incidentID;
    }


    public String getDescription() {
        return incidentPV.getDescription();
    }

    public void verifyIncidentData(Map<Object, String> smartITTestData) {
        viewIncident.verifyIncidentData(smartITTestData);
    }

    public void updateIncidentStatus(String newStatus, String statusReason, String resolutionNote) {
        viewIncident.setStatus(newStatus, statusReason, resolutionNote);
        saveTicket();
    }

    public void verifyStatusViewPage(String Status) {
        verifyEquals(viewIncident.getStatus_viewPage(), Status,
                "Status verification failed on incident view page");
    }

    public String getIncidentStatus_viewPage() {
        return viewIncident.getStatus_viewPage();
    }

    public void verifyTicketStatus(String status) {
        Assert.assertEquals(viewIncident.getStatusOnViewPage(), status, "Status verification failed on ticket view page");
    }

    public void navigateToCreateNewWorkorder() {
        smartITDashboard.navigateToCreateNewWorkorder();
    }

    public void FillWorkorderTemplate(Map<Object, String> smartITTestData) {
        workorderPV.FillWorkOrderTemplate(smartITTestData);
    }

    public String createNewCustomerWithoutLoginInWorkorder(Map<Object, String> customerData) {
        workorderPV.createNewCustomerWithoutLogin(customerData);
        return workorderPV.getLastCreatedCustomerFullName();
    }

    public SmartITFeatureMethodsPage clickAssigntoMeInWorkorderCreatePage() {
        workorderPV.clickAssigntoMe();
        return this;
    }

    public SmartITFeatureMethodsPage saveWorkorder() {
        workorderPV.saveWorkorder();
        return this;
    }

    public String FetchWorkorderTicketID() {
        return viewWorkorder.FetchTicketID();
    }

    public void clickOnResourcesTabInWorkorder() {
        viewWorkorder.clickOnResourcesTab();
    }

    public void searchAndPinKnowledgeArticleInWorkorderResources(String searchKeyword, String articleTitle) {
        viewWorkorder.searchAndPinKnowledgeArticleInResources(searchKeyword, articleTitle);
    }

    public boolean isKnowledgeArticlePinnedUnderWorkorderResourcesTabByTitle(String articleTitle) {
        return viewWorkorder.isKnowledgeArticlePinnedUnderResourcesTabByTitle(articleTitle);
    }

    public void updateWorkorderStatus(String status) {
        viewWorkorder.setStatus(status);
    }

    public void clickAssignToMeInWorkorderViewPageIfPresent() {
        viewWorkorder.clickAssignToMeIfPresent();
    }

    public void verifyWorkorderStatus(String expectedStatus) {
        viewWorkorder.verifyStatus(expectedStatus);
    }

    public void navigateToCreateNewChange() {
        smartITDashboard.navigateToCreateNewChange();
    }

    public void navigateToProactiveProblemManagement() {
        smartITDashboard.navigateToProactiveProblemManagement();
    }

    public SmartITFeatureMethodsPage FillChangeTemplate(Map<Object, String> smartITTestData) {
        changePV.FillChangeTemplate(smartITTestData);
        return this;
    }

    public SmartITFeatureMethodsPage clickAssigntoMeInCreateChangePage() {
        changePV.clickAssignedToMe_ChangeManager();
        return this;
    }

    public void saveChange() {
        changePV.saveChange();
    }

    public void setChangeLocation(String commaSeparatedLocation) {
        changePV.setChangeLocation(commaSeparatedLocation);
    }

    public void fillScheduleDatesOnChange(int startOffsetDays, int endOffsetDays) {
        changePV.fillScheduleDates(startOffsetDays, endOffsetDays);
    }

    public void relateCIsOnCreateChange(String relationshipType, String[] ciNames) {
        changePV.relateCIs(relationshipType, ciNames);
    }

    public void verifyChangeFreezeConflictMessagePresent(String expectedContains) {
        changePV.verifyChangeFreezeConflictMessagePresent(expectedContains);
    }

    public String FetchChangeTicketID() {
        return viewChangePage.FetchTicketID();
    }

    public String FetchChangeRequestTicketID() {
        return FetchChangeTicketID();
    }

    public void createChange(Map<Object, String> smartITTestData) {
        log.info("Creating Change Request with provided data");
        navigateToCreateNewChange();
        FillChangeTemplate(smartITTestData);
        saveChange();
        log.info("Change Request created successfully");
    }

    public void updateChangeStatus(String newStatus) {
        viewChangePage.setStatus(newStatus);
    }

    public void verifyImpactAnalysisBlockingMessage(String expectedMessage) {
        viewChangePage.verifyImpactAnalysisBlockingMessage(expectedMessage);
    }

    public void refreshPage() {
        viewChangePage.refreshPage();
    }

    public SmartITFeatureMethodsPage clickEditOnChange() {
        viewChangePage.clickEditBtn();
        return this;
    }

    public boolean isAddTaskButtonPresentOnChange() {
        return viewChangePage.isAddTaskButtonPresent();
    }

    public void navigateToManageJobsITSMInsights() {
        itsmInsightsPage.navigateToManageJobs();
    }

    public void createOneTimeJob(String jobName) {
        itsmInsightsPage.createOneTimeJob(jobName);
    }

    public void verifyJobStatus(String jobName) {
        itsmInsightsPage.verifyJobStatus(jobName);
    }

    public void verifyServiceAttachedToIncident(String expectedService) {
        viewIncident.verifyServiceAttachedToIncident(expectedService);
    }

    public void enterStringInSmartRecorder(String inputString) {
        smartRecorderPage.enterStringInSmartRecorder(inputString, true);
    }

    public void selectRecommendedDWPTemplate(Map<Object, String> smartITTestData) {
        smartRecorderPage.selectRecommendedDWPTemplate(smartITTestData.get("DWPServiceName"));
    }

    public void clickCreateServiceRequestButton() {
        smartRecorderPage.clickCreateServiceRequestButton();
    }

    public void clickSubmitRequestButton() {
        smartRecorderPage.clickSubmitRequestButton();
    }

    public String FetchDWPRequestID() {
        return viewRequestIDSmartRecorderPage.FetchDWPRequestID();
    }

    public void openSrmRequestByRequestID(String reqID) {
        dwpDashboard.openSrmRequestByRequestID(reqID);
    }

    public void verifySRDTitle(Map<Object, String> smartITTestData) {
        dwpDashboard.verifySRDTitle(smartITTestData.get("DWPCServiceName"));
    }

    public SmartITFeatureMethodsPage verifySmartITPageLaunchedSuccessfully() {
        smartITDashboard.verifySmartITPageLaunchedSuccessfully();
        return this;
    }

    public SmartITFeatureMethodsPage handleAnalyticsPopup() {
        smartITDashboard.handleAnalyticsPopup();
        return this;
    }

    public void setClusterNameAndClickCluster(String clusterName) throws Exception {
        itsmInsightsPage.setClusterNameAndClickCluster(clusterName);
    }

    public void clickOnManageOriginalDuplicateLink() {
        itsmInsightsPage.clickOnManageOriginalDuplicateLink();
    }

    public void clickOnParentTicket() {
        itsmInsightsPage.clickOnParentTicket();
    }

    public void closeParentTicket() throws Exception {
        itsmInsightsPage.closeParentTicket();
    }

    public String clickOnDuplicateIncident() {
        return itsmInsightsPage.clickOnDuplicateIncident();
    }

    public void clickOnDropdownAndSelectOptionFromMajorIncident(String optionName) {
        viewIncident.clickOnDropdownAndSelectOptionFromMajorIncident(optionName);
    }

    public void updateAssignment_MajorIncidentManager(String personName) {
        viewIncident.updateAssignment_MajorIncidentManager(personName);
    }

    public void markAsDuplicate() {
        itsmInsightsPage.markAsDuplicate();
    }

    public String getFirstDuplicateID() {
        return itsmInsightsPage.getFirstDuplicateID();
    }

    public void clickOnRelatedItemEditIncidentAndVerifyChildItem() {
        viewIncident.clickOnRelatedItemEditIncidentAndVerifyChildItem();
    }

    public String actualDuplicateAssigned() {
        return viewIncident.actualDuplicateAssigned();
    }

    public void clickOnViewDetails() {
        viewIncident.clickOnViewDetails();
    }

    public void navigateToSmartRecorder() {
        smartITDashboard.navigateToSmartRecorder();
    }

    public void selectCustomerOrAssetInSmartRecorder(String customerOrAssetName) {
        smartRecorderPage.selectCustomerOrAssetInSmartRecorder(customerOrAssetName);
    }

    public void selectCustomerOrAssetInSmartRecorder(String searchText, String expectedDisplayName) {
        smartRecorderPage.selectCustomerOrAssetInSmartRecorder(searchText, expectedDisplayName);
    }

    public void createWorkorderFromSmartRecorder(Map<Object, String> workorderData) {
        smartRecorderPage.CreateTicketWorkorder();
        if (workorderData != null) {
            FillWorkorderTemplate(workorderData);
            clickAssigntoMeInWorkorderCreatePage();
        }
        saveWorkorder();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void createIncidentFromSmartRecorder() {
        smartRecorderPage.CreateTicketIncident();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void verifyCustomerDataOnSmartRecorderPage(String fullName, String company, String email, String telNo, String siteName) {
        smartRecorderPage.verifyCustomerDataOnSmartRecorderPage(fullName, company, email, telNo, siteName);
    }

    public void sendQueryOnSmartRecorder(String que) {
        smartRecorderPage.sendQueryOnSmartRecorder(que);
    }

    public void verifyRecommendedDWPCTemplate(String templateDesc, String templateType) {
        smartRecorderPage.verifyRecommendedDWPCTemplate(templateDesc, templateType);
    }

    public void verifyRecommendedKATemplate(String templateDesc, String templateType) {
        smartRecorderPage.verifyRecommendedKATemplate(templateDesc, templateType);
    }

    public void verifyRecommendedTemplate(String templateDesc, String templateType) {
        smartRecorderPage.verifyRecommendedTemplate(templateDesc, templateType);
    }

    public void clickOnRealTimeIncidentCorrelation() throws Exception {
        smartITDashboard.navigateToRealTimeIncidentCorrelation();
    }

    public String getActivityFirstNoteText_ActivityTimeline() {
        return viewIncident.getActivityFirstNoteText_ActivityTimeline();
    }

    public void navigateToAskGPTOption() {
        smartITHelixGPTPage.navigateToAskGPTOption();
    }

    public void verifySummarizeActivityAndTicketOptions(String Option1, String Option2) {
        smartITHelixGPTPage.verifySummarizeActivityAndTicketOptions(Option1, Option2);
    }

    public void clickSummarizeOptions(String Option) {
        smartITHelixGPTPage.clickSummarizeOptions(Option);
    }

    public void navigateBackToProactiveProblemManagement() {
        itsmInsightsPage.navigateBackToProactiveProblemManagement();
    }

    public void selectJob(String jobName) {
        itsmInsightsPage.selectJob(jobName);
    }

    public void navigateToViewAdvancedAnalytics() {
        itsmInsightsPage.navigateToViewAdvancedAnalytics();
    }

    public void verifyAdvancedAnalyticsDataPage() {
        itsmInsightsPage.verifyAdvancedAnalyticsDataPage();
    }

    public void setFilters(String filterType, String filterValue) {
        helixDashboard.setFilters(filterType, filterValue);
    }

    public boolean verifyIncidentDataAsPerFilter(String filterValue) {
        return helixDashboard.verifyIncidentDataAsPerFilter(filterValue);
    }

    public void verifyErrorMessage() {
        viewIncident.verifyErrorMessage();
    }

    public void verifyAdditionalFields() {
        viewIncident.verifyAdditionalFields();
    }

    public void FillAdditionalFields(String optionName, String downTimeValue) {
        viewIncident.FillAdditionalFields(optionName, downTimeValue);
    }

    public void FillServiceCI(String serviceValue) {
        viewIncident.FillServiceCI(serviceValue);
    }

    public void FillImpactedArea(String companyValue) {
        viewIncident.FillImpactedArea(companyValue);
    }

    public void saveIncidentVieIncidentPage() {
        viewIncident.saveIncidentVieIncidentPage();
    }

    public void verifyReputaionalImpact(String reputationalInput) {
        viewIncident.verifyReputaionalImpact(reputationalInput);
    }

    public void navigateToCreateNewContract() {
        smartITDashboard.navigateToCreateNewContract();
    }

    public void FillContractTemplate(Map<Object, String> smartITTestData) {
        createContractPage.FillContractTemplate(smartITTestData);
    }

    public void updateNotificationDate() {
        createContractPage.updateNotificationDate();
    }

    public void setCostCenter() {
        createContractPage.setCostCenter();
    }

    public void saveContract() {
        createContractPage.saveContract();
    }

    public String FetchContractID() {
        return viewContractPage.FetchContractID();
    }

    public void updateContractStatus(String newStatus) {
        viewContractPage.setStatus(newStatus);
    }

    public void relateCIToContract(String CIValue, String relationshipType) {
        viewContractPage.relateCI(CIValue, relationshipType);
    }

    public SmartITFeatureMethodsPage navigateToHelixGPTGlobalSearch() {
        smartITDashboard.navigateToHelixGPTGlobalSearch();
        return this;
    }

    public SmartITFeatureMethodsPage verifyGlobalSearchStudioPageLaunchedSuccessfully() {
        globalSearchStudioPage.verifyGlobalSearchStudioPageLaunchedSuccessfully();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnChatSearchButton(String buttonType) {
        globalSearchStudioPage.clickOnChatSearchButton(buttonType);
        return this;
    }

    public SmartITFeatureMethodsPage askQuestionInChat(String question) {
        globalSearchStudioPage.askQuestionInChat(question);
        return this;
    }

    public SmartITFeatureMethodsPage askFollowUpQuestionInChat(String followUpQuestion) {
        globalSearchStudioPage.askFollowUpQuestionInChat(followUpQuestion);
        return this;
    }

    public String createNewArticle() {
        return viewIncident.createNewArticle();
    }

    public void navigateToResourceTab() {
        viewIncident.navigateToResourceTab();
    }

    public void openArticle(String articleTitle) {
        viewIncident.openArticle(articleTitle);
    }

    public void clickOnViewFullArticle() {
        viewIncident.clickOnViewFullArticle();
    }

    public void navigateToAskGPT() {
        incidentPV.navigateToAskGPT();
    }

    public void clickGlobalSearchButtonAfterHelixGPTSearchEnabled() {
        smartITHelixGPTPage.clickGlobalSearchButtonAfterHelixGPTSearchEnabled();
    }

    public void verifyGlobalSearchButtonAfterHelixGPTSearchEnabled() {
        smartITHelixGPTPage.verifyGlobalSearchButtonAfterHelixGPTSearchEnabled();
    }

    public void defaultSearchToggleButton(String searchType) {
        smartITHelixGPTPage.defaultSearchToggleButton(searchType);
    }

    public void verifyFilterOptionsOnSearchDropdownMenus() {
        smartITHelixGPTPage.verifyFilterOptionsOnSearchDropdownMenus();
    }

    public void selectFilterOptionAndSearchOnHelixGPTSearchbox(String TicketType, String textToSearch) {
        smartITHelixGPTPage.selectFilterOptionAndSearchOnHelixGPTSearchbox(TicketType, textToSearch);
    }

    public void searchPeopleAndOpenProfile(String userName) {
        smartITHelixGPTPage.searchPeopleAndOpenProfile(userName);
    }

    public void searchAssetAndOpenProfile(String assetName) {
        smartITHelixGPTPage.searchAssetAndOpenProfile(assetName);
    }

    public void verifyCountOfItemsReturnedAndOpenFullView(String itemType, int itemlevel, int itemline) {
        smartITHelixGPTPage.verifyCountOfItemsReturnedAndOpenFullView(itemType, itemlevel, itemline);
    }

    public void waitForAllSmartITSpinnersToHide() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void switchToSmartITPage(WebDriver driver) {
        smartITHelixGPTPage.switchToSmartITPage(driver);
    }

    public void sendMessageInHelixGPTChat(String message) {
        smartITHelixGPTPage.sendMessageInHelixGPTChat(message);
    }

    public String getAllGPTResponseString() {
        return smartITHelixGPTPage.getAllGPTResponseString();
    }

    public boolean verifyProviderNameInURL(String providerName) {
        return smartITHelixGPTPage.verifyProviderNameInURL(providerName);
    }

    public void openFirstRecordFromTable() {
        smartITHelixGPTPage.openFirstRecordFromTable();
    }

    public void clickOnNewTopic() {
        smartITHelixGPTPage.clickOnNewTopic();
    }

    public WebElement allMessagesFromTable() {
        return smartITHelixGPTPage.allMessagesFromTable;
    }

    public boolean isElementPresentAndDisplayed(WebElement element) {
        return smartITHelixGPTPage.isElementPresentAndDisplayed(element);
    }

    public SmartITFeatureMethodsPage selectTicketAndKnowledgeDropdown(String searchDropdownOption) {
        globalSearchStudioPage.selectTicketAndKnowledgeDropdown(searchDropdownOption);
        return this;
    }

    public SmartITFeatureMethodsPage searchRequestedFilter(String filterName) {
        globalSearchStudioPage.searchRequestedFilter(filterName);
        return this;
    }

    public SmartITFeatureMethodsPage selectViewStyle(String viewStyle) {
        globalSearchStudioPage.selectViewStyle(viewStyle);
        return this;
    }

    public SmartITFeatureMethodsPage verifyAndClickSearchedResult(String articleName) {
        globalSearchStudioPage.verifyAndClickSearchedResult(articleName);
        return this;
    }

    public SmartITFeatureMethodsPage verifyResultPreviewListView(String articleName) {
        globalSearchStudioPage.verifyResultPreviewListView(articleName);
        return this;
    }

    public SmartITFeatureMethodsPage verifyResultPreviewCardView(String articleName) {
        globalSearchStudioPage.verifyResultPreviewCardView(articleName);
        return this;
    }

    public SmartITFeatureMethodsPage clickOnViewFullResultListView(String articleLink) {
        globalSearchStudioPage.clickOnViewFullResultListView(articleLink);
        return this;
    }

    public boolean isGptGlobalSearchPresent() {
        return smartITDashboard.isGptGlobalSearchPresent();
    }

    public SmartITFeatureMethodsPage clickOnViewFullRequest(String requestLink) {
        globalSearchStudioPage.clickOnViewFullRequest(requestLink);
        return this;
    }


    public SmartITFeatureMethodsPage clickOnViewFullResultCardView(String articleLink) {
        globalSearchStudioPage.clickOnViewFullResultCardView(articleLink);
        return this;
    }

    public void selectRecommendedKnowledge(String knowledgeName) {
        smartRecorderPage.selectRecommendedKnowledge(knowledgeName);
    }

    public void pinKnowledgeArticleFromRecommendedKnowledge(String knowledgeTitle) {
        smartRecorderPage.pinKnowledgeArticleFromRecommendedKnowledge(knowledgeTitle);
    }

    public String getViewCountTextFromRecommendedKnowledge(String knowledgeTitle) {
        return smartRecorderPage.getViewCountTextFromRecommendedKnowledge(knowledgeTitle);
    }

    public String getLikePercentTextFromRecommendedKnowledge(String knowledgeTitle) {
        return smartRecorderPage.getLikePercentTextFromRecommendedKnowledge(knowledgeTitle);
    }

    public boolean isKnowledgeArticlePinnedUnderIncidentResourcesTabByTitle(String articleTitle) {
        return viewIncident.isKnowledgeArticlePinnedUnderResourcesTabByTitle(articleTitle);
    }

    public String getIncidentDescription_viewPage() {
        return viewIncident.getDescription_viewPage();
    }

    public void verifyKnowledgeArticlePreviewSmartRecorder(String articleName) {
        commonWaits.waitUntilNextTwoMinutes();
        smartRecorderPage.verifyKnowledgeArticlePreview(articleName);
    }

    public void clickOnViewFullArticleFromSmartRecorder(String articleLink) {
        smartRecorderPage.clickOnViewFullArticle(articleLink);
    }

    public SmartITFeatureMethodsPage relateCIToIncident(String ci, String relationshipType) {
        viewIncident.relateCI(ci, relationshipType);
        return this;
    }

    public void relateCIToWorkorder(String ci, String relationshipType) {
        // Workorder PV uses the same configuration items tab pattern as incident PV.
        viewIncident.relateCI(ci, relationshipType);
    }

    public void createRelatedOutage(String ciName, String relationshipType, String outageDescription) {
        viewIncident.createRelatedOutage(ciName, relationshipType, outageDescription);
    }

    public void verifyOutageCreated(String ciName) {
        viewIncident.verifyOutageCreated(ciName);
    }

    public void relateCIToChange(String ci, String relationshipType) {
        viewChangePage.relateCI(ci, relationshipType);
    }

    public void navigateToCalendar() {
        smartITCalendarPage.navigateToCalendar();
    }

    public SmartITFeatureMethodsPage fillScheduleDatesInChangeFreeze(int startOffsetDays, int endOffsetDays) {
        smartITCalendarPage.fillScheduleDatesInChangeFreeze(startOffsetDays, endOffsetDays);
        return this;
    }

    public void initiateImpactAnalysisFromMoreMenu(String initiateImpactText) {
        viewChangePage.initiateImpactAnalysisFromMoreMenu(initiateImpactText);
    }

    public void clickShowMoreOnImpactAnalysisBanner(String showMoreText) {
        viewChangePage.clickShowMoreOnImpactAnalysisBanner(showMoreText);
    }

    public void openImpactedCIsTopologyView(String topologyText) {
        viewChangePage.openImpactedCIsTopologyView(topologyText);
    }

    public void openImpactedCIsListView(String listText) {
        viewChangePage.openImpactedCisListView(listText);
    }

    public void verifyShowServiceOnlyCheckboxEditable(String showServiceOnlyLabel) {
        viewChangePage.verifyShowServiceOnlyCheckboxEditable(showServiceOnlyLabel);
    }

    public void selectShowServiceOnlyAndVerifyService(String showServiceOnlyLabel, String expectedService) {
        viewChangePage.selectShowServiceOnlyAndVerifyService(showServiceOnlyLabel, expectedService);
    }

    public void expandTopologyParentAndChildren(String mainCiName, String expandText, String expandParentText, String expandChildText) {
        viewChangePage.expandTopologyParentAndChildren(mainCiName, expandText, expandParentText, expandChildText);
    }

    public void clickRelationshipArrowAndShowDetails(String showRelationshipDetailsText) {
        viewChangePage.clickRelationshipArrowAndShowDetails(showRelationshipDetailsText);
    }

    public void simulateImpactOnChildCi(String childCiName, String simulateImpactText) {
        viewChangePage.simulateImpactOnChildCi(childCiName, simulateImpactText);
    }

    public int getImpactIconCount() {
        return viewChangePage.getImpactIconCount();
    }

    public void verifyTopologyCiPresent(String ciName) {
        viewChangePage.verifyTopologyCiPresent(ciName);
    }

    public void addNoteOnChange(String noteText, boolean isPublic) {
        viewChangePage.addActivityNote(noteText, isPublic);
    }

    public void verifyNoteOnChange(String noteText) {
        viewChangePage.verifyActivityNotePresent(noteText);
    }

    public void addNoteWithPersonMentionOnChange(String personDisplayName, String noteText, boolean isPublic) {
        viewChangePage.addActivityNoteWithPersonMention(personDisplayName, noteText, isPublic);
    }

    public void globalSearchAndOpenTicket(String tickedID) {
        smartITDashboard.globalSearchAndOpenTicket(tickedID);
    }

    public void globalSearchAndOpenTicketUsingMessage(String message) {
        smartITDashboard.globalSearchAndOpenTicketUsingMessage(message);
    }

    public SmartITFeatureMethodsPage searchAndOpenRequest(String requestID, String dropdownOption) {
        if (smartITDashboard.isGptGlobalSearchPresent()) {
            gptGlobalSearchAndOpenRequest(requestID, dropdownOption);
        } else {
            standardSearchAndOpenRequest(requestID, dropdownOption);
        }
        return this;
    }

    /**
     * GPT Global Search flow to search and open a Request
     *
     * @param requestID      The request ID to search for
     * @param dropdownOption The dropdown option to select (e.g., "Request")
     */
    public SmartITFeatureMethodsPage gptGlobalSearchAndOpenRequest(String requestID, String dropdownOption) {
        navigateToHelixGPTGlobalSearch();
        verifyGlobalSearchStudioPageLaunchedSuccessfully();
        clickOnChatSearchButton("Search");
        selectTicketAndKnowledgeDropdown(dropdownOption);
        searchRequestedFilter(requestID);
        verifyAndClickSearchedResult(requestID);
        clickOnViewFullRequest(requestID);
        return this;
    }

    /**
     * Standard Search flow to search and open a Request
     *
     * @param requestID      The request ID to search for
     * @param dropdownOption The dropdown option to select (e.g., "Request")
     */
    public SmartITFeatureMethodsPage standardSearchAndOpenRequest(String requestID, String dropdownOption) {
        smartITDashboard.clickOnStandardGlobalSearch();
        selectTicketAndKnowledgeDropdown(dropdownOption);
        searchRequestedFilter(requestID);
        verifyAndClickSearchedResult(requestID);
        clickOnViewFullRequest(requestID);
        return this;
    }

    public String getPriority() {
        return viewIncident.getPriority();
    }

    public SmartITFeatureMethodsPage verifyPriority(String priority) {
        Assert.assertTrue(getPriority().equalsIgnoreCase(priority), "Priority is not as expected");
        return this;
    }

    public SmartITFeatureMethodsPage verifyTicketHeaderPriorityBadge(String expectedLabel) {
        Assert.assertTrue(
                viewIncident.getTicketSummaryPriorityBadgeText().equalsIgnoreCase(expectedLabel),
                "Ticket header priority badge text is not as expected");
        return this;
    }

    public SmartITFeatureMethodsPage verifyActivityNotes(String expectedNotes) {
        Assert.assertTrue(viewIncident.verifyActivityNotes(expectedNotes), "Activity notes do not match the expected value.");
        return this;
    }

    /**
     * Verifies AI-based support group assignment activity notes with multiple possible patterns
     * The activity note text varies depending on the support group assigned by AI
     *
     * @return SmartITFeatureMethodsPage for method chaining
     */
    public SmartITFeatureMethodsPage verifyAIBasedSupportGroupAssignmentActivityNotes() {
        log.info("Verifying activity notes for AI-based support group assignment");
        boolean activityNoteFound = false;
        String foundMessage = "";

        // Try first expected message pattern
        try {
            verifyActivityNotes("Assigned to BMCOpsMonitoring Support Group1 by Helix AI Service Management");
            activityNoteFound = true;
            foundMessage = "Assigned to BMCOpsMonitoring Support Group1 by Helix AI Service Management";
            log.info("Found activity note: " + foundMessage);
        } catch (AssertionError e) {
            log.info("First activity note pattern not found, trying second pattern");
        }

        // Try second expected message pattern
        if (!activityNoteFound) {
            try {
                verifyActivityNotes("Assigned to Service Desk by Helix AI Service Management");
                activityNoteFound = true;
                foundMessage = "Assigned to Service Desk by Helix AI Service Management";
                log.info("Found activity note: " + foundMessage);
            } catch (AssertionError e) {
                log.info("Second activity note pattern not found, trying third pattern");
            }
        }

        // Try third expected message pattern
        if (!activityNoteFound) {
            try {
                verifyActivityNotes("Assigned to Service Desk by cognitive service management");
                activityNoteFound = true;
                foundMessage = "Assigned to Service Desk by cognitive service management";
                log.info("Found activity note: " + foundMessage);
            } catch (AssertionError e) {
                log.info("Third activity note pattern not found");
            }
        }

        Assert.assertTrue(activityNoteFound,
                "Activity note showing AI-based support group assignment was not found. Expected one of: " +
                        "'Assigned to BMCOpsMonitoring Support Group1 by Helix AI Service Management', " +
                        "'Assigned to Service Desk by Helix AI Service Management', or " +
                        "'Assigned to Service Desk by cognitive service management'");

        log.pass("Successfully verified AI-based support group assignment in activity notes: " + foundMessage);
        return this;
    }

    /**
     * Click on brain icon (Category Prediction icon) to trigger category prediction
     *
     * @return SmartITFeatureMethodsPage for method chaining
     */
    public SmartITFeatureMethodsPage clickCategoryPredictionIcon() {
        log.info("Clicking category prediction/brain icon");
        incidentPV.clickCategoryPredictionIcon();
        return this;
    }

    /**
     * Clicks on the "View details" button (e.g., in incident list or search results).
     *
     * @return SmartITFeatureMethodsPage for method chaining
     */
    public SmartITFeatureMethodsPage clickViewDetails() {
        incidentPV.clickViewDetails();
        return this;
    }

    /**
     * Verifies the Alarm details text displayed on the incident view page.
     *
     * @param expectedText Expected alarm details text (required)
     * @return SmartITFeatureMethodsPage for method chaining
     */
    public SmartITFeatureMethodsPage verifyAlarmDetailsText(String expectedText) {
        incidentPV.verifyAlarmDetailsText(expectedText);
        return this;
    }

    /**
     * Verifies category prediction activity notes with retry logic
     *
     * @param expectedActivityNote Expected activity note pattern
     * @return SmartITFeatureMethodsPage for method chaining
     */
    public SmartITFeatureMethodsPage verifyCategoryPredictionActivityNotes(String expectedActivityNote) {
        log.info("Verifying category prediction activity notes");
        boolean activityNoteFound = viewIncident.verifyCategoryPredictionActivityNotes(expectedActivityNote);
        Assert.assertTrue(activityNoteFound, "Category prediction activity note was not found: " + expectedActivityNote);
        log.pass("Successfully verified category prediction activity note: " + expectedActivityNote);
        return this;
    }

    public SmartITFeatureMethodsPage verifyRequestActivityNotes(String expectedNotes) {
        Assert.assertTrue(viewIncident.verifyRequestActivityNotes(expectedNotes), "Request activity notes do not match the expected value: " + expectedNotes);
        return this;
    }

    public void clickOnResolutionNoteAskHelixGPT() {
        smartITHelixGPTPage.clickOnResolutionNoteAskHelixGPT();
    }

    public String getResolutionNote_viewPage() {
        return smartITHelixGPTPage.getResolutionNote_viewPage();
    }

    public String getAllAskGPTResponseString() {
        return smartITHelixGPTPage.getAllAskGPTResponseString();
    }

    public void openGPTArticle(String Article) {
        smartITHelixGPTPage.openArticle(Article);
    }

    public SmartITFeatureMethodsPage verifyFieldValueOnTicketViewPage(String fieldName, String fieldValue) {
        Assert.assertTrue(smartITCommonOperations.getFieldValue(fieldName).contains(fieldValue), "Expected value '" + fieldValue + "' not found in field '" + fieldName + "'.");
        return this;
    }

    public void resolveIncident(String resolutionNote) {
        smartITDashboardPage.resolveIncident(resolutionNote);
    }

    public SmartITFeatureMethodsPage clearSearchedResult() {
        globalSearchStudioPage.clearSearchedResult();
        return this;
    }

    public void navigateToCreateNewKnowledge(String articleLink) {
        smartITDashboard.navigateToKnowledge();
        createKnowledge.verifyCreateKnowledgePageLoaded(articleLink);
    }

    public void fillMailSeriveTemplateDetails(Map<Object, String> mailServiceData) {
        smartRecorderPage.fillMailSeriveTemplateDetails(mailServiceData);
    }

    public void applyFilterAndSearchTicketApex(String ticketType, String tiketId) {
        apexSmartITPage.applyFilterAndSearchTicketApex(ticketType, tiketId);
    }

    public boolean isUserNavigateToTicketConsoleApex() {
        return apexSmartITPage.isUserNavigateToTicketConsoleApex();
    }

    public void createKnowledge(String knowledgeTitle) {
        createKnowledge.enterKnowledgeTitle(knowledgeTitle);
        createKnowledge.saveCreatedKnowledge();
    }

    public void navigateToSmartITDashboard() {
        commonWaits.switchToTabByTitle(SolQAConstants.appSmartIT);
        log.info("Switched to Portal tab successfully.");
    }

    public void apexUpdateChangeStatus(String status) {
        clickOnChangeEditButton();
        apexSmartITPage.setStatus(status);
        clickOnSaveChangeButton();
    }

    public void verifyChangeStatus(String expectedStatus) {
        apexSmartITPage.verifyChangeStatus(expectedStatus);
    }

    public void clickOnChangeEditButton() {
        apexSmartITPage.clickOnChangeEditButton();
    }

    public void clickOnSaveChangeButton() {
        apexSmartITPage.clickOnSaveChangeButton();
    }

    public void addScheduledStartAndEndDate(int startDaysToAdd, int endDaysToAdd) {
        apexSmartITPage.addScheduledStartAndEndDate(startDaysToAdd, endDaysToAdd);
    }

    public void addActualStartAndEndDate(int startDaysToAdd, int endDaysToAdd) {
        apexSmartITPage.addActualStartAndEndDate(startDaysToAdd, endDaysToAdd);
    }

    public void approveRequestFromList() {
        apexSmartITPage.approveRequestFromList();
    }

    public void verifyApproverFromList(String approverName) {
        apexSmartITPage.verifyApproverFromList(approverName);
    }

    public boolean verifyYellowApprovalBannerPresent() {
        return ticketConsolePage.verifyYellowApprovalBannerPresent();
    }

    public boolean verifyYellowApprovalBannerApprovalText(String Text) {
        return ticketConsolePage.verifyYellowApprovalBannerApprovalText(Text);
    }

    public void clickOnFulfillmentIDHyperLink() {
        ticketConsolePage.clickOnFulfillmentIDHyperLink();
    }

    public boolean verifyApprovalIsAvailable(String approvalName) {
        return ticketConsolePage.verifyApprovalIsAvailable(approvalName);
    }

    public void refreshSmartITPage() {
        smartITDashboardPage.refreshSmartITPage();
    }

    public String getWorkOrderIDFromFulfillmentSection() {
        return ticketConsolePage.getWorkOrderIDFromFulfillmentSection();
    }

    public String getWorkOrderIDStatusFromFulfillmentSection() {
        return ticketConsolePage.getWorkOrderIDStatusFromFulfillmentSection();
    }

    public String getApprovalRecordStatusFromFulfillmentSection() {
        return ticketConsolePage.getApprovalRecordStatusFromFulfillmentSection();
    }

    public void clickOnWorkOrderIDHyperLink() {
        ticketConsolePage.clickOnWorkOrderIDHyperLink();
    }

    public boolean verifyWorkorderIDFromSmartITViewPage(String workorderID) {
        return ticketConsolePage.verifyWorkorderIDFromSmartITViewPage(workorderID);
    }

    public void searchAndSelectTemplateInSmartRecorder(String templateName, boolean clear) {
        smartRecorderPage.searchAndSelectTemplateInSmartRecorder(templateName, clear);
    }

    public void Fill_ApprovalPersonSRD_smartRecorder(String approvalPerson) {
        smartRecorderPage.Fill_ApprovalPersonSRD_smartRecorder(approvalPerson);
    }

    public void clickOnRefreshButton() {
        smartRecorderPage.clickOnRefreshButton();
    }

    public void clickOnBannerShowApprovers() {
        ticketConsolePage.clickOnBannerShowApprovers();
    }

    public String getFulfillmentID() {
        return ticketConsolePage.getFulfillmentID();
    }

    public String getWorkOrderID() {
        return ticketConsolePage.getWorkOrderID();
    }

    public void saveToTicket() {
        smartRecorderPage.clickOnSaveToTicket();
    }

    public void deleteFromTicket() {
        smartRecorderPage.clickOnDeleteFromTicket();
    }

    public void CreateTicketWorkorder() {
        log.info("Creating Workorder Ticket from Smart Recorder");
        smartRecorderPage.CreateTicketWorkorder();
        log.info("Clicking on save button from draft Workorder page");
        workorderPV.saveWorkorder();
    }

    public void CreateTicketIncidentDraft() {
        log.info("Creating Incident Draft from Smart Recorder");
        smartRecorderPage.CreateTicketIncidentDraft();
    }

    public void CreateTicketIncident() {
        log.info("Creating Incident Ticket from Smart Recorder");
        saveAndResolve();
    }

    public void saveAndResolve() {
        smartRecorderPage.clickOnSaveAndResolve();
    }

    public void updateIncident(Map<Object, String> smartITTestData) {
        incidentPV.updateIncident(smartITTestData);
    }

    public void validatePCA_CardForAiopsServiceHealth() {
        viewIncident.validatePCA_CardForAiopsServiceHealth();
    }

    public void setService(String serviceValue) {
        incidentPV.setService(serviceValue);
    }

    public void clickServiceCIRecommendationAndValidate(String expectedService) {
        incidentPV.clickServiceCIRecommendationAndValidate(expectedService);
    }

    public void navigateToNewIncident() {
        smartITDashboard.navigateToNewIncident();
    }

    public SmartITFeatureMethodsPage navigateToOptionInAdvancedResponsiveNavigation(String navigation_Option, String Option) {
        impactAnalysisPage.navigateToOptionInAdvancedResponsiveNavigation(navigation_Option, Option);
        return this;
    }

    public void clickResourceTab() {
        smartITCommonOperations.clickResourceTab();
    }

    public void searchKnowledgeInRecommendedKnowledgeSection(String knowledgeArticleId) {
        smartITCommonOperations.searchKnowledgeInRecommendedKnowledgeSection(knowledgeArticleId);
    }

    public boolean isKnowledgeArticlePinnedUnderResourcesTab(String knowledgeArticleId) {
        return smartITCommonOperations.isKnowledgeArticlePinnedUnderResourcesTab(knowledgeArticleId);
    }

    public boolean isChatTextBoxPresentOnChatWindow() {
        return smartITCommonOperations.isChatTextBoxPresentOnChatWindow();
    }

    public boolean isSendEmailButtonPresent() {
        return smartITCommonOperations.isSendEmailButtonPresent();
    }

    public void clickPrinterIcon() {
        smartITCommonOperations.clickPrinterIcon();
    }

    public void clickCancelButtonFromPrintBlade_PV() {
        smartITCommonOperations.clickCancelButtonFromPrintBlade_PV();
    }

    public void searchKnowledgeArticleSaveToTicket(String knowledgeArticleId) {
        log.info("Searching knowledge article and saving to ticket: " + knowledgeArticleId);
        clickResourceTab();
        searchKnowledgeInRecommendedKnowledgeSection(knowledgeArticleId);
        smartITCommonOperations.selectRecommendedKnowledge(knowledgeArticleId);
        log.info("Knowledge article saved to ticket successfully");
    }

    public SmartITFeatureMethodsPage clickOnBackToResourceTab() {
        smartITCommonOperations.clickOnBackToResourceTab();
        return this;
    }

    public void navigateToCreateNewAsset() {
        smartITDashboard.navigateToCreateNewAsset();
    }

    public SmartITFeatureMethodsPage enableEditMode() {
        smartITCommonOperations.enableEditMode();
        return this;
    }

    public SmartITFeatureMethodsPage selectCustomerOnIncidentViewAndSave(String customerDisplayName) {
        log.info("Selecting Customer on incident view: " + customerDisplayName);
        enableEditMode();
        smartITCommonOperations.setTypeaheadField("Customer", customerDisplayName);
        saveTicket();
        return this;
    }

    public SmartITFeatureMethodsPage createAsset(Map<Object, String> assetData) {
        asset.createAsset(assetData);
        return this;
    }

    public SmartITFeatureMethodsPage setStatus(String status) {
        smartITCommonOperations.setDropdownField("Status", status);
        return this;
    }

    public SmartITFeatureMethodsPage saveTicket() {
        smartITCommonOperations.clickSave();
        return this;
    }

    public SmartITFeatureMethodsPage switchToPWAFrame() {
        smartITCommonOperations.switchToPWAFrame();
        return this;
    }

    public SmartITFeatureMethodsPage openOutagesTab() {
        smartITCommonOperations.openOutagesTab();
        return this;
    }

    public SmartITFeatureMethodsPage createOutage(Map<Object, String> outageData) {
        asset.createOutage(outageData);
        return this;
    }

    public SmartITFeatureMethodsPage removeOutage() {
        asset.removeOutage();
        return this;
    }

    public void navigateToCreateNewKnownError() {
        smartITDashboard.navigateToCreateNewKnownError();
    }

    public void navigateToCreateNewBroadcast() {
        smartITDashboard.navigateToCreateNewBroadcast();
    }

    public void createBroadcast(Map<Object, String> smartITTestData) {
        broadcastPV.createBroadcast(smartITTestData);
    }

    public String getSystemAlertText() {
        return smartITCommonOperations.getSystemAlertText();
    }

    public SmartITFeatureMethodsPage dismissSystemAlerts() {
        switchtoDefaultFrame();
        smartITCommonOperations.switchToPWAFrame();
        viewIncident.clickOnDismissAlerts();
        return this;
    }

    public SmartITFeatureMethodsPage createKnownError(Map<Object, String> knownErrorData) {
        knownError.createKnownError(knownErrorData);
        return this;
    }

    public String getKnownErrorID() {
        return knownError.getKnownErrorID();
    }

    public String getTitle_viewPage() {
        return smartITCommonOperations.getTitle_viewPage();
    }

    public String getDescription_viewPage() {
        return smartITCommonOperations.getDescription_viewPage();
    }

    public String getDWPRequestID_FromSmartIT() {
        return smartITCommonOperations.getDWPRequestID_FromSmartIT();
    }

    public boolean isActivityNoteAppearedUnderActivityTab_simpleText(String expectedText) {
        return smartITCommonOperations.isActivityNoteAppearedUnderActivityTab_simpleText(expectedText);
    }

    public boolean isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(String expectedText) {
        return smartITCommonOperations.isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(expectedText);
    }

    public boolean isAttachedFileAppearedUnderActivityTab_ForDWPRequest(String fileName) {
        return smartITCommonOperations.isAttachedFileAppearedUnderActivityTab_ForDWPRequest(fileName);
    }

    public String openRelatedIncidentInNewTab() {
        return globalSearchStudioPage.openRelatedIncidentInNewTab();
    }

    // ==================== TASK METHODS ====================

    public SmartITFeatureMethodsPage clickOnTasksTab() {
        viewIncident.clickOnTasksTab();
        return this;
    }

    public int getTaskCount() {
        return viewIncident.getTaskCount();
    }

    public SmartITFeatureMethodsPage clickOnTaskByIndex(int index) {
        viewIncident.clickOnTaskByIndex(index);
        return this;
    }

    public SmartITFeatureMethodsPage updateTaskStatus(String newStatus) {
        viewIncident.updateTaskStatus(newStatus);
        return this;
    }

    public SmartITFeatureMethodsPage saveTask() {
        viewIncident.saveTask();
        return this;
    }

    public SmartITFeatureMethodsPage navigateBackToIncident() {
        viewIncident.navigateBackToIncident();
        return this;
    }

    public SmartITFeatureMethodsPage closeAllTasks(String closedStatus) {
        viewIncident.closeAllTasks(closedStatus);
        return this;
    }

    // ==================== NETWORK IMPACT ANALYSIS METHODS ====================

    public SmartITFeatureMethodsPage clickOnMoreOptions() {
        viewChangePage.clickOnMoreOptions();
        return this;
    }

    public SmartITFeatureMethodsPage verifyMoreOptionVisible(String optionName) {
        viewChangePage.verifyMoreOptionVisible(optionName);
        return this;
    }

    public SmartITFeatureMethodsPage closeMoreOptions() {
        viewChangePage.closeMoreOptions();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnMoreOption(String optionName) {
        viewChangePage.clickOnMoreOption(optionName);
        return this;
    }

    public SmartITFeatureMethodsPage selectCreateNewImpactAnalysisRun(String ciOption) {
        viewChangePage.selectCreateNewImpactAnalysisRun(ciOption);
        return this;
    }

    public SmartITFeatureMethodsPage verifyNIASuccessMessage(String expectedMessage) {
        viewChangePage.verifyNIASuccessMessage(expectedMessage);
        return this;
    }

    public SmartITFeatureMethodsPage clickOnCIsImpacted() {
        viewChangePage.clickOnCIsImpacted();
        return this;
    }

    public SmartITFeatureMethodsPage selectAndRelateAllImpactedCIs() {
        viewChangePage.selectAndRelateAllImpactedCIs();
        return this;
    }

    public SmartITFeatureMethodsPage verifyCIsDisplayedAsImpacted() {
        viewChangePage.verifyCIsDisplayedAsImpacted();
        return this;
    }

    public SmartITFeatureMethodsPage navigateBackToChangeRequest() {
        viewChangePage.navigateBackToChangeRequest();
        return this;
    }

    public SmartITFeatureMethodsPage verifyImpactedCIsInConfigurationItemSection() {
        viewChangePage.verifyImpactedCIsInConfigurationItemSection();
        return this;
    }

    public SmartITFeatureMethodsPage chooseCIsFromAllCIs() {
        viewChangePage.chooseCIsFromAllCIs();
        return this;
    }

    public SmartITFeatureMethodsPage clickAddSelected() {
        viewChangePage.clickAddSelected();
        return this;
    }

    public SmartITFeatureMethodsPage saveAndRunAnalysis() {
        viewChangePage.saveAndRunAnalysis();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnImactAnalysisRunAndRunAnalysis(String description, String ticketType, String Value) {
        impactAnalysisPage.clickOnImactAnalysisRun();
        impactAnalysisPage.addDescription(description);
        impactAnalysisPage.applyFilter(ticketType, Value);
        impactAnalysisPage.clickOnSelectAllCheckbox();
        impactAnalysisPage.clickOnAddSelectedButton();
        impactAnalysisPage.clickOnSaveButton();
        impactAnalysisPage.clickOnRunAnalysisButton();
        return this;
    }

    public SmartITFeatureMethodsPage navigateToRequiredOptionThrough3DotsMenu(String option) {
        impactAnalysisPage.navigateToRequiredOptionThrough3DotsMenu(option);
        return this;
    }

    public String getImpactAnalysisRunStatus() {
        return impactAnalysisPage.getImpactAnalysisRunStatus();
    }

    public SmartITFeatureMethodsPage FillIncidentTemplateinImpactAnalysisPage(Map<Object, String> smartITTestData) {
        impactAnalysisPage.switchToImpactAnalysisIframe();
        incidentPV.FillIncidentTemplate(smartITTestData);
        return this;
    }

    public String getActivityFirstNoteText_ActivityIARID() {
        String activityFirstNoteText = impactAnalysisPage.getActivityFirstNoteText_ActivityTimeline();
        return impactAnalysisPage.getActivityFirstNoteText_ActivityIARID(activityFirstNoteText);
    }

    public String getIncidentRequestID() {
        return impactAnalysisPage.getIncidentRequestID();
    }

    public void verifyImpactAnalysisConsoleLaunchedSuccessfully() {
        smartITDashboardPage.verifyImpactAnalysisConsoleLaunchedSuccessfully();
    }

    public boolean verifyRelatedPartiesAndSourceInformationTabs() {
        return smartITCommonOperations.verifyRelatedPartiesAndSourceInformationTabs();
    }

    public SmartITFeatureMethodsPage openRelatedPartiesTab() {
        smartITCommonOperations.openRelatedPartiesTab();
        return this;
    }

    public SmartITFeatureMethodsPage createRelatedParty(String relatedPartyType, String relatedPartyName, String relatedPartyRole) {
        smartITCommonOperations.createRelatedParty(relatedPartyType, relatedPartyName, relatedPartyRole);
        return this;
    }

    public SmartITFeatureMethodsPage fillTextFieldByLabelInSmartRecorderDWPWidget(String label, String value) {
        smartRecorderPage.fillTextFieldByLabelInDWPWidget(label, value);
        return this;
    }

    public SmartITFeatureMethodsPage fillDropdownFieldInSmartRecorderDWPWidget(String label, String value) {
        smartRecorderPage.fillDropdownFieldInDWPWidget(label, value);
        return this;
    }

    public SmartITFeatureMethodsPage clickNextInSmartRecorderDWPWidget() {
        smartRecorderPage.clickNextInDWPWidget();
        return this;
    }

    public SmartITFeatureMethodsPage fillCounterFieldInSmartRecorderDWPWidget(String label, String value) {
        smartRecorderPage.fillCounterFieldInDWPWidget(label, value);
        return this;
    }

    public SmartITFeatureMethodsPage fillAttachmentFieldByLabelInSmartRecorderDWPWidget(String label, File file) {
        smartRecorderPage.fillAttachmentFieldInDWPWidget(label, file);
        return this;
    }

    public SmartITFeatureMethodsPage selectRadioOptionInSmartRecorderDWPWidget(String questionLabel, String optionText) {
        smartRecorderPage.selectRadioOptionInDWPWidget(questionLabel, optionText);
        return this;
    }

    public SmartITFeatureMethodsPage selectCheckboxOptionInSmartRecorderDWPWidget(String questionLabel, String optionText) {
        smartRecorderPage.selectCheckboxOptionInDWPWidget(questionLabel, optionText);
        return this;
    }

    public SmartITFeatureMethodsPage fillTypeAheadDropdownFieldInSmartRecorderDWPWidget(String label, String value) {
        smartRecorderPage.fillTypeAheadDropdownFieldInDWPWidget(label, value);
        return this;
    }

    public SmartITFeatureMethodsPage addNewRuleForIncident(String ruleName, String companyName, String triggerEvent1, String triggerEvent2, String qualification, String incidentType) {
        impactAnalysisPage.addNewRuleForIncident(ruleName, companyName, triggerEvent1, triggerEvent2, qualification, incidentType);
        return this;
    }

    public String getImpactAnalysisDescription() {
        return impactAnalysisPage.getImpactAnalysisDescription();
    }

    public SmartITFeatureMethodsPage clickOnSaveIncidentButton() {
        impactAnalysisPage.clickOnSaveIncidentButton();
        return this;
    }

    public String getActivityFirstNoteText_ImpactAnalysis() {
        return impactAnalysisPage.getActivityFirstNoteText_ActivityTimeline();
    }

    public SmartITFeatureMethodsPage FillIncidentInImpactAnalysisPage(Map<Object, String> smartITTestData) {
        impactAnalysisPage.switchToCreateNewIncidentIframe();
        incidentPV.FillIncidentTemplate(smartITTestData);
        return this;
    }

    public SmartITFeatureMethodsPage saveIncidentInImpactAnalysisPage() {
        impactAnalysisPage.switchToCreateNewIncidentIframe();
        impactAnalysisPage.clickOnSaveIncidentButton();
        return this;
    }

    public String FetchIncidentTicketIDinImpactAnalysisPage() {
        return impactAnalysisPage.FetchTicketID();
    }

    public SmartITFeatureMethodsPage relateCIToIncidentinImpactAnalysisPage(String ci, String relationshipType) {
        impactAnalysisPage.switchToCreateNewIncidentIframe();
        impactAnalysisPage.relateCI(ci, relationshipType);
        return this;
    }

    public SmartITFeatureMethodsPage verifyActivityNotesinImpactAnalysisPage(String expectedActivityNotes) {
        impactAnalysisPage.verifyActivityNotesinImpactAnalysisPage(expectedActivityNotes);
        return this;
    }

    public SmartITFeatureMethodsPage runImpactAnalysis(String description, String ticketType, String Value) {
        impactAnalysisPage.addDescription(description);
        impactAnalysisPage.applyFilter(ticketType, Value);
        impactAnalysisPage.clickOnSelectAllCheckbox();
        impactAnalysisPage.clickOnAddSelectedButton();
        impactAnalysisPage.clickOnSaveButton();
        impactAnalysisPage.clickOnRunAnalysisButton();
        return this;
    }

    public SmartITFeatureMethodsPage openFirstIncident() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        impactAnalysisPage.openFirstIncident();
        return this;
    }

    public SmartITFeatureMethodsPage navigateToRequiredOptionFromMoreMenu(String option) {
        impactAnalysisPage.navigateToRequiredOptionFromMoreMenu(option);
        return this;
    }

    public SmartITFeatureMethodsPage waitForNumberWindowsToBe(int expectedWindowCount) {
        commonWaits.waitForNumberWindowsToBe(expectedWindowCount);
        return this;
    }

    public SmartITFeatureMethodsPage clickOnImactAnalysisRun() {
        impactAnalysisPage.clickOnImactAnalysisRun();
        return this;
    }

    public SmartITFeatureMethodsPage navigateToRequiredImpactAnalysisRunOption(String option) {
        impactAnalysisPage.navigateToRequiredImpactAnalysisRunOption(option);
        return this;
    }

    public SmartITFeatureMethodsPage clickOnSelectAllCheckbox() {
        impactAnalysisPage.clickOnSelectAllCheckbox();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnAddSelectedButton() {
        impactAnalysisPage.clickOnAddSelectedButton();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnSaveButton() {
        impactAnalysisPage.clickOnSaveButton();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnRunAnalysisButton() {
        impactAnalysisPage.clickOnRunAnalysisButton();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnSelectAllCheckboxInSelectInputCIs() {
        impactAnalysisPage.clickOnSelectAllCheckboxInSelectInputCIs();
        return this;
    }

    public boolean isSummarizeUsingHelixGPTOptionAvailable() {
        return impactAnalysisPage.isSummarizeUsingHelixGPTOptionAvailable();
    }

    public boolean verifyImpactCISummarizeVisible() {
        return impactAnalysisPage.verifyImpactCISummarizeVisible();
    }

    public CreateWorkorderPage clickOnCreateNewCustomerButton() {
        return workorderPV.clickOnCreateNewCustomerButton();
    }

    public CreateWorkorderPage fillNewCustomerDetailsAndSave(Map<Object, String> smartITCustomerData) {
        return workorderPV.fillNewCustomerDetailsAndSave(smartITCustomerData);
    }

    public ViewWorkorderPage verifyWorkorderData(Map<Object, String> smartITTestData) {
        return viewWorkorder.verifyWorkorderData(smartITTestData);
    }

    public ViewIncidentPage clickOnDismissAlerts() {
        return viewIncident.clickOnDismissAlerts();
    }

    public ViewWorkorderPage verifyAlertMessage(String expectedMessage) {
        return viewWorkorder.verifyAlertMessage(expectedMessage);
    }

    public String getRelatedDWPRequestID() {
        return viewWorkorder.getRelatedDWPRequestID();
    }

    public void searchBmcopsEnduserAndVerifyListCardView() {
        selectFilterOptionAndSearchOnHelixGPTSearchbox(SolQAConstants.FILTER_TICKET, "bmcopsenduser");
        // UI shows Card / List toggles
        selectViewStyle("CARD");
        selectViewStyle("LIST");
    }

    public SmartITFeatureMethodsPage addApprover(String approverName) {
        smartITCommonOperations.addApprover(approverName);
        return this;
    }

    public SmartITFeatureMethodsPage addAttachmentNote_InActivityFeed_ForDWPRequest(String noteText, File attachment) {
        smartITCommonOperations.addAttachmentNote_InActivityFeed(noteText, attachment);
        return this;
    }

    public SmartITFeatureMethodsPage verifyRelatedTicketRelationshipType(String relatedTicketId, String expectedRelationshipType) {
        viewIncident.verifyRelatedTicketRelationshipType(relatedTicketId, expectedRelationshipType);
        return this;
    }

    public SmartITFeatureMethodsPage tryDeleteRelatedTicketRelationshipExpectError(String relatedTicketId, String expectedErrorMessage) {
        viewIncident.tryDeleteRelatedTicketRelationshipExpectError(relatedTicketId, expectedErrorMessage);
        return this;
    }

    public SmartITFeatureMethodsPage openRelatedTicketInNewTab(String relatedTicketId) {
        viewIncident.openRelatedTicketInNewTab(relatedTicketId);
        return this;
    }

    public SmartITFeatureMethodsPage setIncidentTypeOnCreateIncident(String incidentType) {
        incidentPV.switchToPWAFrame();
        incidentPV.setIncidentType(incidentType);
        return this;
    }

    public SmartITFeatureMethodsPage acceptNavigationAwayWarningByClickingYesIfPresent() {
        smartITLiveChatAgentConsolePage.acceptNavigationAwayWarningByClickingYesIfPresent();
        return this;
    }

    public boolean isKnowledgeBotLoaded() {
        return gainsightSelfHelpWidget.isKnowledgeBotLoaded();
    }

    public SmartITFeatureMethodsPage openKnowledgeBot() {
        gainsightSelfHelpWidget.openBot();
        return this;
    }

    public SmartITFeatureMethodsPage verifyHelpsAndGuides(String expectedHelpOrGuideTitle) {
        gainsightSelfHelpWidget.verifyHelpsAndGuides(expectedHelpOrGuideTitle);
        return this;
    }

    public SmartITFeatureMethodsPage closeKnowledgeBotIfOpened() {
        gainsightSelfHelpWidget.closeBotIfOpened();
        return this;
    }

    public JSONObject getAptrinsicCalls_JsonFromCurrentSession() {
        String baseUrl = SmartITGainsightApi.baseUrlFromCurrentUrl(getDriver().getCurrentUrl());
        String cookieHeader = buildCookieHeaderFromBrowser();
        return SmartITGainsightApi.getAptrinsicConfigurationJsonForITSM(baseUrl, cookieHeader);
    }

    private String buildCookieHeaderFromBrowser() {
        Set<Cookie> cookies = getDriver().manage().getCookies();
        StringBuilder sb = new StringBuilder();
        if (cookies != null) {
            for (org.openqa.selenium.Cookie c : cookies) {
                if (c == null || c.getName() == null) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append("; ");
                }
                sb.append(c.getName()).append("=").append(c.getValue() == null ? "" : c.getValue());
            }
        }
        return sb.toString();
    }

    public boolean isUserNavigateToSmartRecorder() {
        return smartRecorderPage.isSmartRecorderLoaded();
    }

    public SmartITFeatureMethodsPage searchAndSelectTemplateInSmartRecorderFromTemplateList(String templateName) {
        smartRecorderPage.searchAndSelectTemplateInSmartRecorderFromTemplateList(templateName);
        return this;
    }

    /**
     * Search for asset by name in Asset Console
     *
     * @param assetName Name of the asset to search
     */
    public SmartITFeatureMethodsPage searchAssetByName(String assetName) {
        log.info("Searching for asset: " + assetName);
        assetConsolePage.searchInSearchBar(assetName);
        return this;
    }

    /**
     * Open full asset information and access More menu
     */
    public SmartITFeatureMethodsPage openFullAssetInformation() {
        log.info("Opening full asset information");
        assetConsolePage.openFullAssetInformation();
        return this;
    }

    /**
     * Click on More dropdown and select Asset Details
     */
    public SmartITFeatureMethodsPage openAssetDetailsFromMore() {
        log.info("Opening Asset Details from More menu");
        assetConsolePage.clickMoreDropdown()
                .selectMoreMenuItem("Asset Details");
        return this;
    }

    /**
     * Login to BCM with admin credentials
     *
     * @param username BCM admin username
     * @param password BCM admin password
     */
    public SmartITFeatureMethodsPage loginToBCMAdmin(String username, String password) {
        log.info("Logging in to BCM as admin");
        assetConsolePage.loginToBCM(username, password);
        return this;
    }

    /**
     * Select action from BCM More menu
     *
     * @param action Action to perform (e.g., "Audit Now", "Reboot", "Shut down", "Wake up", "Check Connection")
     */
    public SmartITFeatureMethodsPage selectBCMAction(String action) {
        log.info("Selecting BCM action: " + action);
        assetConsolePage.selectBCMAction(action);
        return this;
    }

    /**
     * Verify activity notification in activities tab
     *
     * @param expectedActivity Expected activity text
     */
    public SmartITFeatureMethodsPage verifyActivityNotification(String expectedActivity) {
        log.info("Verifying activity notification: " + expectedActivity);
        boolean isPresent = assetConsolePage.verifyActivityInFeed(expectedActivity);
        Assert.assertTrue(isPresent, "Activity notification not found: " + expectedActivity);
        return this;
    }

    /**
     * Verify asset is present in CMDB
     *
     * @param className Class name (e.g., BMC.CORE:BMC_ComputerSystem)
     * @param assetName Asset name to verify
     */
    public SmartITFeatureMethodsPage verifyAssetInCMDB(String className, String assetName) {
        log.info("Verifying asset in CMDB: " + assetName + " with class: " + className);

        try {
            // Wait for any spinners to hide before proceeding
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);

            // Perform CI search in CMDB
            // Using default dataset "BMC.ASSET" and attribute "Name"
            log.info("Performing CMDB search for asset: " + assetName);
            cmdbDashboard.performCISearch("BMC.ASSET", className, "Name", assetName);

            // Wait for search results to load
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
            commonWaits.waitForSometime(commonWaits.minDelay);

            // Check if asset is present in search results
            boolean isAssetPresent = cmdbDashboard.isAssetPresentInResults(assetName);

            // Assert that asset is present
            Assert.assertTrue(isAssetPresent, "Asset '" + assetName + "' not found in CMDB search results");
            log.info("Asset verification in CMDB completed successfully - Asset '" + assetName + "' found");

        } catch (Exception e) {
            log.error("Error verifying asset in CMDB: " + e.getMessage());
            Assert.fail("Failed to verify asset in CMDB: " + assetName + ". Error: " + e.getMessage());
        }
        return this;
    }

    public SmartITFeatureMethodsPage clickOnEditButton() {
        viewIncident.clickOnEditButton();
        return this;
    }

    public String getSystemAlertMessage() {
        return viewIncident.getSystemAlertMessage();
    }

    public SmartITFeatureMethodsPage setSupportGroupAndPersonName(String supportGroupName, String personName) {
        viewIncident.setSupportGroupAndPersonName(supportGroupName, personName);
        return this;
    }

    public SmartITFeatureMethodsPage setSupportGroupAndPersonNameInCommunicationCoOrdinator(String supportGroupName, String personName) {
        viewIncident.setSupportGroupAndPersonNameInCommunicationCoOrdinator(supportGroupName, personName);
        return this;
    }

    public String openMajorIncidentReportAndVerifyINCDetails() {
        return viewIncident.openMajorIncidentReportAndVerifyINCDetails();
    }

    public void performMajorIncidentReportFormattingActions() {
        log.info("Performing formatting actions on Major Incident report");
        viewIncident.MakeATextBoldInReport();
        viewIncident.verifyReportTextIsBold();
    }

    public ViewIncidentPage closeButtonOnReport() {
        return viewIncident.closeButtonOnReport();
    }

    public SmartITFeatureMethodsPage verifyPrintOption() {
        viewIncident.verifyPrintOption();
        return this;
    }

    public String getAllSummary() {
        return viewIncident.getAllSummary();
    }

    public SmartITFeatureMethodsPage setSupportGroupAndPersonNameForAssignee(String supportGroupName, String personName) {
        viewIncident.setSupportGroupAndPersonNameForAssignee(supportGroupName, personName);
        return this;
    }

    public SmartITFeatureMethodsPage clickGlobalSearchButtonAfterHelixGPTSearchEnabledAndOpenInNewTab() {
        smartITHelixGPTPage.clickGlobalSearchButtonAfterHelixGPTSearchEnabledAndOpenInNewTab();
        return this;
    }

    public SmartITFeatureMethodsPage unflagTicketWithNoteForGPT(String note) {
        commonWaits.switchToTabByIndex(0);
        viewIncident.unflagTicket();
        viewChangePage.addActivityNote(note, true);
        commonWaits.switchToTabByIndex(1);
        return this;
    }

    public void updateIncidentStatusWithSwitchingToIncidentPage(String newStatus, String statusReason, String resolutionNote) {
        commonWaits.switchToTabByIndex(0);
        viewIncident.setStatus(newStatus, statusReason, resolutionNote);
        commonWaits.switchToTabByIndex(1);
    }

    public SmartITFeatureMethodsPage setFiltersAndSelectValue(String filterName, String filterValue) {
        helixDashboard.setFiltersAndSelectValue(filterName, filterValue);
        return this;
    }

    public SmartITFeatureMethodsPage clickCreateIncidentButton() {
        smartITLiveChatAgentConsolePage.clickCreateIncidentButton();
        return this;
    }

    public SmartITFeatureMethodsPage closeNotificationAlerts() {
        smartITLiveChatAgentConsolePage.closeNotificationAlerts();
        return this;
    }

    public SmartITFeatureMethodsPage verifyTicketCreatedFromResourceTab() {
        smartITLiveChatAgentConsolePage.verifyTicketCreatedFromResourceTab();
        return this;
    }

    public String getTicketIdFromResourceTab() {
        return smartITLiveChatAgentConsolePage.getTicketIdFromResourceTab();
    }

    public SmartITFeatureMethodsPage openTicketFromResources() {
        smartITLiveChatAgentConsolePage.openTicketFromResources();
        return this;
    }

    public String getCustomerValue_viewPage() {
        return viewIncident.getCustomerValue_viewPage();
    }

    public boolean verifyWorkorderIDFromSmartITViewPageWhenPVDisabled(String workorderID) {
        return ticketConsolePage.verifyWorkorderIDFromSmartITViewPageWhenPVDisabled(workorderID);
    }

    public SmartITFeatureMethodsPage clickInitiateImpactAnalysis() {
        viewChangePage.clickInitiateImpactAnalysis();
        return this;
    }

    public SmartITFeatureMethodsPage verifyImpactSimulationBladeDetails() {
        viewChangePage.verifyImpactSimulationBladeDetails();
        return this;
    }

    public SmartITFeatureMethodsPage ClickonCloseButtonOnISBlade() {
        viewChangePage.ClickonCloseButtonOnISBlade();
        return this;
    }

    public SmartITFeatureMethodsPage clickShowMoreOptionOnImpactAnalysisBanner() {
        viewChangePage.clickShowMoreOptionOnImpactAnalysisBanner();
        return this;
    }

    public SmartITFeatureMethodsPage clickTopologyViewOnImpactedCI() {
        viewChangePage.clickTopologyViewOnImpactedCI();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnCIWithName(String ciName) {
        viewChangePage.clickOnCIWithName(ciName);
        return this;
    }

    public SmartITFeatureMethodsPage rightClickOnCINode() {
        viewChangePage.rightClickOnCINode();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnOptionFromRightClickOfCI(String option) {
        viewChangePage.clickOnOptionFromRightClickOfCI(option);
        return this;
    }

    public boolean verifyCIIspresent(String ciName) {
        return viewChangePage.verifyCIIspresent(ciName);
    }

    public int getCIIndicatorCount() {
        return viewChangePage.getCIIndicatorCount();
    }

    public SmartITFeatureMethodsPage verifyShowServiceOnlyEditableImpactedCI() {
        viewChangePage.verifyShowServiceOnlyEditableImpactedCI();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnListViewOnImpactedCI() {
        viewChangePage.clickOnListViewOnImpactedCI();
        return this;
    }

    public SmartITFeatureMethodsPage closeExploreDataSetTab() {
        viewChangePage.closeExploreDataSetTab();
        return this;
    }

    public SmartITFeatureMethodsPage verifyServiceDisplayedImpactedCIListView(String serviceName) {
        viewChangePage.verifyServiceDisplayedImpactedCIListView(serviceName);
        return this;
    }

    public SmartITFeatureMethodsPage navigateToChangeFreeze() {
        smartITCalendarPage.navigateToChangeFreeze();
        return this;
    }

    public SmartITFeatureMethodsPage fillScheduleStartTimeAndEndTime(String startTime, String endTime) {
        smartITCalendarPage.fillScheduleStartTimeAndEndTime(startTime, endTime);
        return this;
    }

    public SmartITFeatureMethodsPage setDropDownValues(String labelContains, String valueContains) {
        smartITCalendarPage.setDropDownValues(labelContains, valueContains);
        return this;
    }

    public SmartITFeatureMethodsPage setSummary(String summary) {
        changePV.switchToCreateChangeContext();
        changePV.setSummary(summary);
        return this;
    }

    public SmartITFeatureMethodsPage setSummaryInChangeFreeze(String summary) {
        smartITCalendarPage.setSummary(summary);
        return this;
    }

    public SmartITFeatureMethodsPage saveChangeFreeze() {
        smartITCalendarPage.saveChangeFreeze();
        return this;
    }

    public SmartITFeatureMethodsPage fillScheduleDates(int startOffsetDays, int endOffsetDays) {
        changePV.fillScheduleDates(startOffsetDays, endOffsetDays);
        return this;
    }

    public SmartITFeatureMethodsPage setRiskLevel(String riskLevel) {
        changePV.setRiskLevel(riskLevel);
        return this;
    }

    public WebElement getChangeFreezeErrorMessage() {
        return smartITCalendarPage.ChangeFreezeErrorMessage;
    }

    public SmartITFeatureMethodsPage addCIForChangeFreeze(String searchText) {
        smartITCalendarPage.addCIForChangeFreeze(searchText);
        return this;
    }

    public SmartITFeatureMethodsPage clickAddConfigurationItem() {
        smartITCalendarPage.clickAddConfigurationItem();
        return this;
    }

    public SmartITFeatureMethodsPage relateCIs(String relationshipType, String[] ciNames) {
        changePV.relateCIs(relationshipType, ciNames);
        return this;
    }

    public SmartITFeatureMethodsPage verifyRedDotIconPresentInCalender(String changeID, boolean isPresent) {
        smartITCalendarPage.verifyRedDotIconPresentInCalender(changeID, isPresent);
        return this;
    }

    public SmartITFeatureMethodsPage clickOnCancelButtonIfPresent() {
        changePV.clickOnCancelButtonIfPresent();
        return this;
    }

    public SmartITFeatureMethodsPage navigateRequiredSmartITURL(String URL) {
        smartITDashboardPage.navigateRequiredSmartITURL(URL);
        return this;
    }

    public SmartITFeatureMethodsPage waitFor10SecondsToChangeFreeze() {
        changePV.waitFor10SecondsToChangeFreeze();
        return this;
    }

    public SmartITFeatureMethodsPage clickSearchForKnowledgeResourcesButton() {
        smartRecorderPage.clickSearchForKnowledgeResourcesButton();
        return this;
    }

    public SmartITFeatureMethodsPage clickCreateNewArticleButton() {
        smartRecorderPage.clickCreateNewArticleButton();
        return this;
    }

    public boolean createHKMArticle(String articleTitle) {
        return smartRecorderPage.createHKMArticle(articleTitle);
    }

    public SmartITFeatureMethodsPage clickCreateIncidentOrWorkOrderFromCreateTicketBtn(String ticketType) {
        smartRecorderPage.clickCreateIncidentOrWorkOrderFromCreateTicketBtn(ticketType);
        return this;
    }

    public WebElement getCustomerOnWOPV() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(incidentPV.pwaFrame);
        return smartRecorderPage.setCustomerOnWOPV_xpath;
    }

    public boolean checkCaseRelationType() throws InterruptedException {
        return incidentPV.checkCaseRelationType();
    }

    public boolean TryToDeleteRelationship(String Error) throws InterruptedException {
        return incidentPV.TryToDeleteRelationship(Error);
    }

    public boolean cross_Launch_To_BWF() {
        return incidentPV.cross_Launch_To_BWF();
    }

    public SmartITFeatureMethodsPage closeInfoAlertIfPresent() {
        asset.closeInfoAlertIfPresent();
        return this;
    }

    public SmartITFeatureMethodsPage selectInventoryLocation(String location) {
        asset.selectInventoryLocation(location);
        return this;
    }

    public SmartITFeatureMethodsPage fillActualDates(int startOffsetDays, int endOffsetDays) {
        changePV.fillActualDates(startOffsetDays, endOffsetDays);
        return this;
    }

    public SmartITFeatureMethodsPage setCredentials(String url, String hkmUserName, String hkmPassword) {
        smartRecorderPage.setCredentials(url, hkmUserName, hkmPassword);
        return this;
    }

    public SmartITFeatureMethodsPage SetFlagAndUnFlag(String flagName, String FlagMessage) {
        viewWorkorder.SetFlagAndUnFlag(flagName, FlagMessage);
        return this;
    }

    public boolean isNeedsAttentionFlagIconVisible() {
        return viewWorkorder.isNeedsAttentionFlagIconVisible();
    }

    public boolean isNeedsAttentionFlagIconVisibleOnIncident() {
        return viewIncident.isNeedsAttentionFlagIconVisible();
    }

    public SmartITFeatureMethodsPage clickAssignToMeOnViewPage(String fieldNameOrLabel) {
        viewWorkorder.clickAssignToMeOnViewPage(fieldNameOrLabel);
        return this;
    }

    public boolean isAttentionFlagSet() {
        return smartITCommonOperations.isAttentionFlagSet();
    }

    public SmartITFeatureMethodsPage waitForOneMinuteJobToRun() {
        commonWaits.waitForSometime(60000);
        return this;
    }

    public SmartITFeatureMethodsPage switchToListViewInITSMInsights() {
        smartITDashboardPage.switchToListViewInITSMInsights();
        return this;
    }

    public SmartITFeatureMethodsPage verifyListViewContainsClusterandINCData() {
        smartITDashboardPage.switchToListViewInITSMInsights();
        smartITDashboardPage.verifyListViewContainsClusterandINCData();
        return this;
    }

    public SmartITFeatureMethodsPage clickOn3DotsDrilldownOnTopCornerOfClusterMap() {
        itsmInsightsPage.clickOn3DotsDrilldownOnTopCornerOfClusterMap();
        return this;
    }

    public SmartITFeatureMethodsPage selectClusterOption(String clusterOptionName) {
        itsmInsightsPage.selectClusterOption(clusterOptionName);
        return this;
    }

    public SmartITFeatureMethodsPage relateToExistingProblemAndVerifySuccessToast() {
        itsmInsightsPage.relateToExistingProblem();
        return this;
    }

    public String createProblemAndGetProblemID() {
        return itsmInsightsPage.createProblemAndGetProblemID();
    }

    public SmartITFeatureMethodsPage switchToTabByIndex(int tabIndex) {
        commonWaits.switchToTabByIndex(tabIndex);
        return this;
    }

    public SmartITFeatureMethodsPage verifyClusterDetailsPageContainsProblemID() {
        itsmInsightsPage.openTheFirstCluster();
        smartITDashboardPage.verifyClusterDetailsPageContainsProblemID();
        return this;
    }

    public SmartITFeatureMethodsPage openTheFirstCluster() {
        itsmInsightsPage.openTheFirstCluster();
        return this;
    }

    public SmartITFeatureMethodsPage openIncidentFromClusterDetailsPageAndVerifyIncidentID() {
        itsmInsightsPage.openIncidentFromClusterDetailsPageAndVerifyIncidentID();
        return this;
    }

    public SmartITFeatureMethodsPage checkForExploreProbableCausesButton() {
        itsmInsightsPage.checkForExploreProbableCausesButton();
        return this;
    }

    public SmartITFeatureMethodsPage clickOnExploreProbableCausesButtonAndVerifyCrossLaunch() {
        itsmInsightsPage.clickOnExploreProbableCausesButtonAndVerifyCrossLaunch();
        return this;
    }

    public SmartITFeatureMethodsPage clickCreateWorkOrderButton() {
        smartITLiveChatAgentConsolePage.clickCreateWorkOrderButton();
        return this;
    }

    public SmartITFeatureMethodsPage refreshTicket() {
        viewIncident.refreshTicket();
        return this;
    }

    public String verifySummarization() {
        return smartITLiveChatAgentConsolePage.verifySummarization();
    }

    public void selectChatFromTransferAndAccept(String initialMessage, String customerName) {
        try {
            smartITLiveChatAgentConsolePage
                    .clickTransferTab()
                    .selectCustomerFromChatQueue(initialMessage, customerName)
                    .clickAcceptChat();
        } catch (Exception e) {
            log.info("[SmartIT LiveChat] Falling back to latest queue chat selection. Reason: " + e.getMessage());
            smartITLiveChatAgentConsolePage
                    .selectLatestChatFromQueue()
                    .clickAcceptChat();
        }
    }

}