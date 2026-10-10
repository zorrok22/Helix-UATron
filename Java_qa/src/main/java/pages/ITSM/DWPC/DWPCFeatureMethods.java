package pages.ITSM.DWPC;

import framework.BasePage;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.util.Map;

public class DWPCFeatureMethods extends BasePage {
	
	static SolqaLogger log = new SolqaLogger(DWPCFeatureMethods.class.getName());
	
	DWPCDashboardPage dwpcDashboardPage = new DWPCDashboardPage();
	DWPCServicesPage servicesPage = new DWPCServicesPage();
	DWPCSurveysPage surveysPage = new DWPCSurveysPage();
	DWPCServiceRequests dwpcServiceRequests = new DWPCServiceRequests();
    DWPC_APICommonOperations dwpc_apiCommonOperations = new DWPC_APICommonOperations();

    public DWPCFeatureMethods verifyDWPCPageLaunchedSuccessfully() {
    	dwpcDashboardPage.verifyDWPCPageLaunchedSuccessfully();
    	return this;
    }
    
	public DWPCFeatureMethods openServicesPage() {
		dwpcDashboardPage.openServicesPage();
		return this;
	}
	
	public DWPCFeatureMethods verifyServicesPageLaunchedSuccessfully() {
		servicesPage.verifyServicesPageLaunchedSuccessfully();
		return this;
	}

	public String searchForService(String serviceName) {
		return servicesPage.searchForService(serviceName);
	}

	public DWPCFeatureMethods doubleClickOnService(String serviceName) {
		servicesPage.doubleClickOnService(serviceName);
		return this;
	}

	public DWPCFeatureMethods verifyServiceOptionsPanelOpened() {
		servicesPage.verifyServiceOptionsPanelOpened();
		return this;
	}

	public DWPCFeatureMethods clickEditForSection(String sectionName) {
		servicesPage.clickEditForSection(sectionName);
		return this;
	}

	public String getSelectedDropdownValue() {
		return servicesPage.getSelectedDropdownValue();
	}

	public DWPCFeatureMethods selectDropdownOption(String optionValue) {
		servicesPage.selectDropdownOption(optionValue);
		return this;
	}

	public DWPCFeatureMethods clickSave() {
		servicesPage.clickSave();
		return this;
	}

	public DWPCFeatureMethods clickCancel() {
		servicesPage.clickCancel();
		return this;
	}

	public DWPCFeatureMethods clickClose() {
		servicesPage.clickClose();
		return this;
	}

	public DWPCFeatureMethods configureServiceOption(String serviceName, String sectionName, String expectedValue) {
		servicesPage.configureServiceOption(serviceName, sectionName, expectedValue);
		return this;
	}

	public DWPCFeatureMethods validateServiceNameCaseSensitivity(String serviceName) {
		servicesPage.validateServiceNameCaseSensitivity(serviceName);
		return this;
	}
	
	public void openSurveysPage() {
		dwpcDashboardPage.openSurveysPage();
	}
	
	public void verifySurveysPageLaunchedSuccessfully() {
		surveysPage.verifySurveysPageLaunchedSuccessfully();
	}
	
	public String createNewSurvey(String surveyNamePrefix) {
		log.info("Creating New Survey");
		surveysPage.clickOnNewSurvey();
		surveysPage.verifySurveyDesignerPopupDisplayed();
		surveysPage.editAndCheckSurveyFields();
		String surveyName = surveysPage.saveCreatedSurvey(surveyNamePrefix);
		log.info("Survey saved: " + surveyName);
		return surveyName;
	}
	
	public void navigateBackToDashboard() {
		surveysPage.navigateBackToDashboard();
	}
	
	public void verifyPostRequestSurveySettings(String ratingScaleValue) {
		dwpcDashboardPage.verifyPostRequestSurveySettings(ratingScaleValue);
	}
	
	public void openPostRequestSurveySettings() {
		dwpcDashboardPage.openPostRequestSurveySettings();
	}
	
	public void selectRatingScale(String ratingScaleValue) {
		dwpcDashboardPage.selectRatingScale(ratingScaleValue);
	}
	
	public void handleChangeScaleWarningPopup() {
		dwpcDashboardPage.handleChangeScaleWarningPopup();
	}
	
	public void closeSettings() {
		dwpcDashboardPage.closeSettings();
	}

	public DWPCFeatureMethods enableRequestPriority(boolean enable) {
		dwpcDashboardPage.enableRequestPriority(enable);
        return this;
	}

	public String getServiceRequestPriorityFromReports(String reportsMenu, String serviceRequestsSubMenu, String requestId, String priorityColumnLabel) {
		return dwpcDashboardPage.getServiceRequestPriorityFromReports(reportsMenu, serviceRequestsSubMenu, requestId, priorityColumnLabel);
	}

    public boolean isServiceRequestPresentInReports(String reportsMenu, String serviceRequestsSubMenu, String requestId) {
        return dwpcDashboardPage.isServiceRequestPresentInReports(reportsMenu, serviceRequestsSubMenu, requestId);
    }

    public DWPCFeatureMethods navigateToDWPCPage(String parentMenu, String subMenu) {
        dwpcDashboardPage.navigateToDWPCPage(parentMenu, subMenu);
        return this;
    }

    public DWPCFeatureMethods setCollaborationDelegation(String serviceName, String serviceOptionName, boolean enable) {
        openServicesPage();
        verifyServicesPageLaunchedSuccessfully();
        servicesPage.openServiceOption(serviceName);
        servicesPage.editServiceOption(serviceOptionName);
        servicesPage.setCollaborationDelegation(enable);
        return this;
    }

    public DWPCFeatureMethods openServiceOption(String serviceName) {
        servicesPage.openServiceOption(serviceName);
        return this;
    }

	public DWPCDashboardPage setFrequencyAdjustment(boolean enabled, int deliveryLimitDays) {
		return dwpcDashboardPage.setFrequencyAdjustment(enabled, deliveryLimitDays);
	}
	
	public DWPCFeatureMethods navigateToSubmenu(String parentMenu, String subMenu) {
		dwpcDashboardPage.navigateToSubmenu(parentMenu, subMenu);
		return this;
	}

	public DWPCFeatureMethods verifyConnectorManagementPageLoaded() {
		dwpcDashboardPage.verifyConnectorManagementPageLoaded();
		return this;
	}
	
	public DWPCFeatureMethods clickNewConnection() {
		dwpcDashboardPage.clickNewConnection();
		return this;
	}
	
	public DWPCFeatureMethods enterConnectionName(String connectionName) {
		dwpcDashboardPage.enterConnectionName(connectionName);
		return this;
	}
	
	public DWPCFeatureMethods selectConnector(String connectorName) {
		dwpcDashboardPage.selectConnector(connectorName);
		return this;
	}
	
	public DWPCFeatureMethods saveConnection() {
		dwpcDashboardPage.saveConnection();
		return this;
	}
	
	public DWPCFeatureMethods clickCloseIfExists() {
		dwpcDashboardPage.clickCloseIfExists();
		return this;
	}
	
	/**
	 * Create a new connector by executing all steps sequentially
	 * @param connectionName - The name for the new connection
	 * @param connectorName - The connector type to select (e.g., "AWS Catalog", "Azure", "REST")
	 * @return DWPCFeatureMethods
	 */
	public DWPCFeatureMethods createNewConnector(String connectionName, String connectorName) {
		log.info("Starting to create new connector: " + connectionName + " with connector type: " + connectorName);
		
		dwpcDashboardPage.clickNewConnection()
		                  .enterConnectionName(connectionName)
		                  .selectConnector(connectorName)
		                  .saveConnection()
		                  .clickCloseIfExists();
		
		log.info("New connector created successfully: " + connectionName);
		return this;
	}
	
	public boolean isServicePresent(String serviceName) {
		return servicesPage.isServicePresent(serviceName);
	}
	
	public DWPCFeatureMethods clickImportFromFile() {
		servicesPage.clickImportFromFile();
		return this;
	}
	
	public DWPCFeatureMethods uploadServiceFile(String filePath) {
		servicesPage.uploadServiceFile(filePath);
		return this;
	}
	
	public DWPCFeatureMethods selectImportOptions() {
		servicesPage.selectImportOptions();
		return this;
	}
	
	public DWPCFeatureMethods confirmImport() {
		servicesPage.confirmImport();
		return this;
	}
	
	public DWPCFeatureMethods importServiceFile(String filePath) {
		servicesPage.importServiceFile(filePath);
		return this;
	}
	
	/**
	 * Check if service exists, if not import it from file
	 * @param serviceName - The service name to check
	 * @return DWPCFeatureMethods
	 */
	public DWPCFeatureMethods importServiceIfNotFound(String serviceName, String serviceFileNameToImport) {
		log.info("Checking if service exists: " + serviceName);
        String ServiceToImportFileLocation = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appDWPC + File.separator + serviceFileNameToImport + ".zip";
		if (!isServicePresent(serviceName)) {
			log.info("Service not found, importing from file: " + ServiceToImportFileLocation);
			importServiceFile(ServiceToImportFileLocation);
			log.info("Service imported successfully");
		} else {
			log.info("Service already exists: " + serviceName);
		}
		
		return this;
	}
	
	public DWPCFeatureMethods deleteConnectorIfExists(String connectionName) {
		dwpcDashboardPage.deleteConnectorIfExists(connectionName);
		return this;
	}
	
	public DWPCServiceRequests openDWPRequest(String serviceRequestID) {
		return dwpcServiceRequests.openDWPRequest(serviceRequestID);
	}
	
	public DWPCServiceRequests openServiceRequestsReports() {
		return dwpcDashboardPage.openServiceRequestsReports();
	}
	
	public DWPCDashboardPage  openRequestLifeCycleSettings() {
		return dwpcDashboardPage.openRequestLifeCycleSettings();
	}
	
	public String getRequestLifecycleValue() {
		return dwpcDashboardPage.getRequestLifecycleValue();
	}
	
	public DWPCDashboardPage setRequestLifecycleValueDays(String days) {
		return dwpcDashboardPage.setRequestLifecycleValueDays(days);
	}

	public DWPCFeatureMethods selectRequestLifecycleTimeToCloseTimeUnit(String timeUnit) {
		dwpcDashboardPage.selectRequestLifecycleTimeToCloseTimeUnit(timeUnit);
		return this;
	}

	public DWPCFeatureMethods selectRequestLifecycleTimeToCloseTimeUnitAsDays() {
		dwpcDashboardPage.selectRequestLifecycleTimeToCloseTimeUnitAsDays();
		return this;
	}

    public DWPCFeatureMethods updateWorkOrderTemplateExpression(String templateName) {
        servicesPage.updateWorkOrderTemplateExpression(templateName);
        return this;
    }

    public DWPCFeatureMethods restartFailedServiceProcess(String serviceRequestID) {
        dwpcServiceRequests.restartFailedServiceProcess(serviceRequestID);
        return this;
    }

    public String getRemedyConnectionId() {
        return dwpc_apiCommonOperations.getRemedyConnectionId();
    }

    public String getServiceRequestReportColumnValue(String reportsMenu, String serviceRequestsSubMenu, String requestId, String columnLabel) {
        return dwpcDashboardPage.getServiceRequestColumnValueFromReports(reportsMenu, serviceRequestsSubMenu, requestId, columnLabel);
    }

    public String getServiceRequestReportDateColumnValueAsIsoDate(String reportsMenu, String serviceRequestsSubMenu, String requestId, String dateColumnLabel) {
        return dwpcDashboardPage.getServiceRequestDateColumnValueAsIsoDate(reportsMenu, serviceRequestsSubMenu, requestId, dateColumnLabel);
    }

    public DWPCFeatureMethods editKnowledgeArticleAssociation(String knowledgeCategory, String value) {
        dwpcServiceRequests.editKnowledgeArticleAssociation(knowledgeCategory, value);
        return this;
    }

    public DWPCFeatureMethods closeKnowledgeArticleAssociation() {
        dwpcServiceRequests.closeKnowledgeArticleAssociation();
        return this;
    }

    public DWPCFeatureMethods closeServiceOption() {
        dwpcServiceRequests.closeServiceOption();
        return this;
    }

    public DWPCFeatureMethods setDwpcAndWildcardSearchSettings(boolean enableAndLogic, boolean enableWildcard, Map<Object, String> dwpcSettingsData) {
        dwpcDashboardPage.setDwpcAndWildcardSearchSettings(enableAndLogic, enableWildcard, dwpcSettingsData);
        return this;
    }

	public DWPCFeatureMethods createServiceActionAndMapWithService(String serviceActionName, String serviceName) {
		dwpcDashboardPage.createServiceActionAndMapWithService(serviceActionName, serviceName);
		return this;
	}
	
	public DWPCFeatureMethods createAssetGroup(String assetGroupName, String assetGroupDescription) {
		dwpcDashboardPage.createAssetGroup(assetGroupName, assetGroupDescription);
		return this;
	}

	public DWPCFeatureMethods verifyCommentAndAttachmentsInRequest(String comment, File[] attachments) {
		dwpcServiceRequests.verifyCommentInRequest(comment);
		dwpcServiceRequests.verifyAttachmentsInRequest(attachments);
		return this;
	}

	public DWPCFeatureMethods deleteAssetGroupIfPresent(String assetGroupName) {
		dwpcDashboardPage.deleteAssetGroupIfPresent(assetGroupName);
		return this;
	}
}
