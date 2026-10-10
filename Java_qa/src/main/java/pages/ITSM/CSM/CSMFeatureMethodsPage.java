package pages.ITSM.CSM;

import java.util.Map;
import framework.BasePage;

public class CSMFeatureMethodsPage extends BasePage{
	
	CSMDashboardPage csmDashboardPage = new CSMDashboardPage();
	
	public CSMDashboardPage verifyCSMPageLoaded() {
		return csmDashboardPage.verifyCSMPageLoaded();
	}
	
	public CSMDashboardPage clickStartSessionButton() {
		return csmDashboardPage.clickStartSessionButton();
	}
	
	public CSMDashboardPage verifyFrontlinerWorkspacePageLoaded() {
		return csmDashboardPage.verifyFrontlinerWorkspacePageLoaded();
	}
	
	public void csmLogout() {
		csmDashboardPage.csmLogout();
	}
	
	public CSMDashboardPage clickOnNewCaseButton() {
		return csmDashboardPage.clickOnNewCaseButton();
	}
	
	public CSMDashboardPage fillCaseCreationConsoleDataAndVerify(Map<Object, String> caseData) {
        csmDashboardPage.fillCustomerInformationData(caseData.get("CustomerID"));
		csmDashboardPage.clickOnNextButton();
		csmDashboardPage.verifyKnowledgeBaseNoResultsFound();
		csmDashboardPage.fillCaseClassificationData(caseData);
		csmDashboardPage.verifyKnowledgeBaseDataPresent();
		csmDashboardPage.clickOnNextButton();
		csmDashboardPage.verifyAssignedMainGroupIsSelected();
		csmDashboardPage.verifyChannelIsSelected();
		csmDashboardPage.clickOnNextButton();
		csmDashboardPage.clickOnSubmitButton();
		return new CSMDashboardPage();
	}

    public CSMFeatureMethodsPage navigateToTab(String tabName) {
        csmDashboardPage.navigateToTab(tabName);
        return this;
    }

    public CSMFeatureMethodsPage OpenTabFromCSMControlPanel(String tabName) {
        csmDashboardPage.OpenTabFromCSMControlPanel(tabName);
        return this;
    }

    public CSMFeatureMethodsPage setCoreConfiguration(Map<Object, String> coreConfigData) {
        csmDashboardPage.setCoreConfiguration(coreConfigData);
        return this;
    }

    public CSMFeatureMethodsPage setStandardConfiguration(Map<Object, String> standardConfigData) {
        csmDashboardPage.setStandardConfiguration(standardConfigData);
        return this;
    }
    public CSMFeatureMethodsPage setGroupSpecificConfiguration(Map<Object, String> groupSpecificConfigData) {
        csmDashboardPage.setGroupSpecificConfiguration(groupSpecificConfigData);
        return this;
    }

    public CSMFeatureMethodsPage setCustomerDatabaseConfiguration(Map<Object, String> customerDatabaseConfigData) {
        csmDashboardPage.setCustomerDatabaseConfiguration(customerDatabaseConfigData);
        return this;
    }

    public CSMFeatureMethodsPage fillCaseDataInCaseCreationConsole(Map<Object, String> caseData) {
        csmDashboardPage.fillCaseData(caseData);
        return this;
    }

    public String getCaseReferenceIdFromToast() {
        return csmDashboardPage.getCaseReferenceIdFromToast(30);
    }
}
