package pages.ITSM.FSM;

import framework.BasePage;

public class FSMFeatureMethodsPage extends BasePage{
	
	FSMDispatcherConsolePage fsmDispatcherConsolePage = new FSMDispatcherConsolePage();
	
	public void verifyFSMPageLoaded() {
		fsmDispatcherConsolePage.verifyFSMPageLoaded();
	}
	public void navigateToDispatcherConsoleItem(String menuName) {	
		fsmDispatcherConsolePage.navigateToDispatcherConsoleItem(menuName);
	}
	
	public void verifyPageIsNavigated(String moduleName) {	
		fsmDispatcherConsolePage.verifyPageIsNavigated(moduleName);
	}
	
	public void selectSchedulingOptions(String optionName) {	
		fsmDispatcherConsolePage.selectSchedulingOptions(optionName);
	}
	
	public void selectFirstWorkOrderFromList() {	
		fsmDispatcherConsolePage.selectFirstWorkOrderFromList();
	}
	
	public void clickOnSelectGroup() {	
		fsmDispatcherConsolePage.clickOnSelectGroup();
	}
	
	public void selectCompanyAndSupportGroup(String company, String groupName) {	
		fsmDispatcherConsolePage.selectCompanyAndSupportGroup(company, groupName);
	}
	
	public void verifySupportGroupIsSelected(String groupName) {	
		fsmDispatcherConsolePage.verifySupportGroupIsSelected(groupName);
	}
	
	public void clickOnShowMap() {	
		fsmDispatcherConsolePage.clickOnShowMap();
	}
	
	public void clickOnShowUserRoute() {	
		fsmDispatcherConsolePage.clickOnShowUserRoute();
	}
	
	public void verifyMapIsDisplayed() {	
		fsmDispatcherConsolePage.verifyMapIsDisplayed();
	}
	
	public void verifyRouteIsDisplayed() {	
		fsmDispatcherConsolePage.verifyRouteIsDisplayed();
	}
	
	public void clickOnBackButton() {	
		fsmDispatcherConsolePage.clickOnBackButton();
	}
	
	public void clickOnInventorySearch() {	
		fsmDispatcherConsolePage.clickOnInventorySearch();
	}
	
	public void verifyInventorySearchIsDisplayed() {	
		fsmDispatcherConsolePage.verifyInventorySearchIsDisplayed();
	}
	
	public void selectInventoryTypeAndInventoryLocation(String type, String location) {
		fsmDispatcherConsolePage.selectInventoryTypeAndInventoryLocation(type, location);
	}
	
	public void selectFirstInventoryFromList() {	
		fsmDispatcherConsolePage.selectFirstInventoryFromList();
	}
	
	public void selectWorkOrderTemplate(String templateName) {	
		fsmDispatcherConsolePage.selectWorkOrderTemplate(templateName);
	}
	
	public void clickOnAssignWorkOrder() {
		fsmDispatcherConsolePage.clickOnAssignWorkOrder();
	}
	
	public void addDurationOnSite(String duration) {	
		fsmDispatcherConsolePage.addDurationOnSite(duration);
	}
	
	public void verifyInventoryTypeAndLocationIsPresent(String type, String location) {	
		fsmDispatcherConsolePage.verifyInventoryTypeAndLocationIsPresent(type, location);
	}
	
	public void selectTimelineSupportGroupAndRouteName(String groupName, String routeName) {	
		fsmDispatcherConsolePage.selectTimelineSupportGroupAndRouteName(groupName, routeName);
	}
	
	public void verifyTimeineSupportGroupAndRouteName(String supportGroup, String routeName) {	
		fsmDispatcherConsolePage.verifyTimeineSupportGroupAndRouteName(supportGroup, routeName);
	}
	
	public void dragAndDropFirstUnscheduledWorkOrderCardToDate() {	
		fsmDispatcherConsolePage.dragAndDropFirstUnscheduledWorkOrderCardToDate();
	}
	
	public void verifyWOIsNotPresentInDatePanel() {	
		fsmDispatcherConsolePage.verifyWOIsNotPresentInDatePanel();
	}
	
}
