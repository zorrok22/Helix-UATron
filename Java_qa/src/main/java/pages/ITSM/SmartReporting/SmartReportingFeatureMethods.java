package pages.ITSM.SmartReporting;

import framework.BasePage;

public class SmartReportingFeatureMethods extends BasePage {


	SmartReportingLandingPage smartReportingLandingPage = new SmartReportingLandingPage();

	public void verifyNavigationToSmartReporting() {
		smartReportingLandingPage.verifyNavigationToSmartReporting();
	}

	public void waitForSmartReportingSpinnersToDisappear () {
		smartReportingLandingPage.waitForSmartReportingSpinnersToDisappear();
	}

	public void searchFolder(String foldername) throws Exception {
		smartReportingLandingPage.searchFolder(foldername);
	}

	public void clickOnFolder(String foldername) {
		smartReportingLandingPage.clickOnFolder(foldername);
	}

	public void clickOnSubFolder(String foldername) {
		smartReportingLandingPage.clickOnSubFolder(foldername);
	}

	public void clickContentDashBoardLinkForVerifyingDetails(String contentName) throws Exception {
		smartReportingLandingPage.clickContentDashBoardLinkForVerifyingDetails(contentName);
	}

	public void enterDateAndclickOnGo() {
		smartReportingLandingPage.enterDateAndclickOnGo();
	}

	public void openFirstIncidentAndVerifyDetails() {
		smartReportingLandingPage.openFirstIncidentAndVerifyDetails();
	}

	public void verifyCaseDetailsInNewTab(){
		smartReportingLandingPage.verifyCaseDetailsInNewTab();
	}

	public void verifySmartReportingHomePage(){
		smartReportingLandingPage.verifySmartReportingHomePage();
	}

}
