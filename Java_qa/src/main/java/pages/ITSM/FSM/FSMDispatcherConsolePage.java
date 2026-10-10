package pages.ITSM.FSM;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class FSMDispatcherConsolePage extends BasePage {
	
	static SolqaLogger log;
	CommonWaits commonWaits;

	public FSMDispatcherConsolePage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(FSMDispatcherConsolePage.class.getName());
		commonWaits=new CommonWaits();
	}
	
    public String moduleHeader = "//div[contains(@class,'special-containers') and contains(.,'%s')]";
	public String compactNavigation = "//button[contains(@class,'a-hamburger ng-tns') and not(@disabled)]";
	public String menuGroupItems_VerticalMenu = "//button[contains(@class,'a-hamburger__menu-link btn-link ng-tns')]";
	public String menuGroupItemsParent_VerticalMenu = "//ul[contains(@class,'a-hamburger__menu')]//li//a";
	public String menuGroupItemsChild_VerticalMenu = "//button[contains(@class,'a-hamburger__menu-link btn-link')]";
	public String menuGroupItems = "//button[contains(@class,'a-menu__link btn btn-link')]";
	public String menuGroupItemsParent = "//button[contains(@class,'adapt-menu__link btn btn-link')]";
    public String menuGroupItemsChild = "//*[contains(@class,'adapt-submenu__link')]";
    public String buttonContainsText = "//button[contains(.,'%s')]";
    public String headerValues = "//div[contains(@class,'ui-table')]//th[contains(@data-testid,'field-id')]";
    public String firstWorkOrderLink = "(//td[1])[%s]//adapt-row-selection-checkbox";
    public String workOrderTemplateRadioButton = "//td[contains(.,'%s')]//input";
    public String firstInventory = "(//rx-runtime-view-modal[contains(.,'Inventory Search Criteria')]//td[%s]//input)[1]";
    public String inventoryTableHeaders = "(//rx-runtime-view-modal[contains(.,'Inventory Search Criteria')]//table)[2]//th";
    public String inventoryDetailsLocator = "//button[contains(@aria-label,'%s')]";
    public String timelineDropdownLocator = "//adapt-rx-select[contains(.,'%s')]//button";
    public String firstUnscheduledWorkOrderCardLocator = "//div[contains(@class,'unscheduled-wo-card')][1]//div[contains(@class,'wo-title')]";
    public String datePanelLocator = "//div[contains(@id,'buessing-%s')]";
    
    //FindBy locators
    @FindBy(xpath = "//header[contains(.,' Field Service Management')]")
    public WebElement fsmHeader;
    @FindBy(xpath = "//button[contains(.,'Scheduling Options')]")
    public WebElement schedulingOptionsButton;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Company')]//button")
    public WebElement companyDropdown;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Support Group')]//button")
    public WebElement supportGroupDropdown;
    @FindBy(xpath = "//button[contains(.,'Select') and contains(@class,'circle')]")
    public WebElement selectAssignmentGroupButton;
    @FindBy(xpath = "//button[contains(.,'Back')]")
    public WebElement backButton;
    @FindBy(xpath = "//div[contains(.,'Selected Group') and contains(@class,'text')]")
    public WebElement selectedGroupLabel;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Inventory Type')]//button")
    public WebElement inventoryTypeDropdown;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Inventory Location')]//button")
    public WebElement inventoryLocationDropdown;
    @FindBy(xpath = "//span[text()='Select']//parent::button")
    public WebElement selectInventoryButton;
    @FindBy(xpath = "//rx-runtime-view-modal[contains(.,'Work Order Template')]//span[text()='Select']//parent::button")
    public WebElement selectWorkOrderTemplateButton;
    @FindBy(xpath = "//label[contains(.,'Duration On Site')]//ancestor::rx-decimal-field//input")
    public WebElement durationOnSiteInputBox;
    @FindBy(xpath = "//strong[contains(.,'Inventory Search')]")
    public WebElement inventorySearchLabel;
    @FindBy(xpath = "//strong[contains(.,'Route:')]")
    public WebElement routeLabel;
    @FindBy(xpath = "//div[contains(@class,'map-container')]")
    public WebElement mapContainer;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Route Name')]//button")
    public WebElement routeNameDropdown;
    @FindBy(xpath = "//div[contains(@id,'buessing') and contains(.,'WO')]")
    public WebElement workOrderInDatePanel;

    //String variables
    String menuGroupName = "Dispatcher Console";
    String assignWorkOrder = "Assign Work Order";
    String serialNumber = "Serial Number";
    String inventorySearch = "Inventory Search";
    String showUserRoute = "Show User Route";
    String showMap = "Show Map";
    String selectGroup = "Select Group";
    String workOrderID = "Work Order ID";
    String inventoryTypeLabel = "Inventory Type";
    String inventoryLocationLabel = "Inventory Location";
    String supportGroupLabel = "Support Group";
    String routeNameLabel = "Route Name";
    String Inventorysearch = "Inventory search";
	
    public void verifyFSMPageLoaded() {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
    	Assert.assertTrue(commonWaits.isElementPresent(fsmHeader), "FSM Header is not displayed");
    }
    
    public void navigateToDispatcherConsoleItem(String menuName) {
		log.info("Navigating to FSM Dispatcher Console page");
		
		WebElement groupElement = null;
        if (!getDriver().findElements(By.xpath(compactNavigation)).isEmpty()) {
            log.info("---Vertical menu---");
            getDriver().findElement(By.xpath(compactNavigation)).click();
            groupElement = ((WebElement) getDriver().findElements(By.xpath(menuGroupItems_VerticalMenu)).stream().filter((element) ->  {
                return element.getText().equals(menuGroupName); } ).findFirst().orElse(null));

            if (groupElement==null) {
                log.info("---Looking for more menu items in Vertical Parent Menus");
                groupElement = ((WebElement) getDriver().findElements(By.xpath(menuGroupItemsParent_VerticalMenu)).stream().filter((element) ->  {
                    return element.getText().trim().equals(menuGroupName); } ).findFirst().orElseThrow(() -> {
                        return new AssertionError(String.format("----There is no menu item with '%s' name", menuGroupName));
                    }));

            }

            (new Actions(getDriver())).moveToElement(groupElement).click().perform();


            ((WebElement) getDriver().findElements(By.xpath(menuGroupItemsChild_VerticalMenu)).stream().filter((element) -> {
                return element.getText().trim().equals(menuName);
            }).findFirst().orElseThrow(() -> {
                return new AssertionError(String.format("There is no menu item with '%s' name", menuName));
            })).click();
        } else {
            log.info("--- Horizontal menu---");
            WebElement horizontalgroupElement = ((WebElement) getDriver().findElements(By.xpath(menuGroupItems)).stream().filter((element) ->  {
                return element.getText().trim().equals(menuGroupName); } ).findFirst().orElse(null));

            if (horizontalgroupElement==null) {
                log.info("---Looking for more menu items in Horizontal Parent Menus");
                horizontalgroupElement = ((WebElement) getDriver().findElements(By.xpath(menuGroupItemsParent)).stream().filter((element) ->  {
                    return element.getText().trim().equals(menuGroupName); } ).findFirst().orElseThrow(() -> {
                        return new AssertionError(String.format("----There is no menu item with '%s' name", menuName));
                    }));

            }
            (new Actions(getDriver())).moveToElement(horizontalgroupElement).click().perform();
            ((WebElement) horizontalgroupElement.findElements(By.xpath(menuGroupItemsChild)).stream().filter((element) -> {
                return element.getText().equals(menuName);
            }).findFirst().orElseThrow(() -> {
                return new AssertionError(String.format("There is no menu item with '%s' name", menuName));
            })).click();
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);	
	}
	
	public void verifyPageIsNavigated(String moduleName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(moduleHeader, moduleName))), moduleName + " page is not displayed");
	}
	
	public void selectSchedulingOptions(String optionName) {
		log.info("Clicking on Scheduling Options button");
		clickWithoutInterception(schedulingOptionsButton);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, optionName)));
	}
	
	public void selectFirstWorkOrderFromList() {
		log.info("Selecting first work order from the list");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(By.xpath(String.format(firstWorkOrderLink,getColumnIndex(workOrderID))));
	}
	
	public int getColumnIndex(String columnName) {
        List<WebElement> values = findElements(By.xpath(headerValues));
        int index = 0;
        for( int i=0 ; i < values.size(); i++) {
            String headerText = values.get(i).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
            if(headerText.contains(columnName)) {
                index = i;
                break;
            }
        }
       return (index + 1);
    }
	
	public void clickOnSelectGroup() {
		log.info("Clicking on Select Group button");
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, selectGroup)));
	}
	
	public void selectCompanyAndSupportGroup(String company, String groupName) {
		log.info("Selecting Company and Support Group");
		clickWithoutInterception(companyDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, company)));
		clickWithoutInterception(supportGroupDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, groupName)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(selectAssignmentGroupButton);
	}
	
	public void verifySupportGroupIsSelected(String groupName) {
		log.info("Verifying selected support group");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		Assert.assertTrue(selectedGroupLabel.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).contains(groupName), "Selected group is not matching with expected group name");
	}
	
	public void clickOnShowMap() {
		log.info("Clicking on Show Map button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, showMap)));
	}
	
	public void clickOnShowUserRoute() {
		log.info("Clicking on Show User Route button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, showUserRoute)));
	}
	
	public void verifyMapIsDisplayed() {
		log.info("Verifying Map is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		Assert.assertTrue(commonWaits.isElementPresent(mapContainer), "Map is not displayed");
	}
	
	public void verifyRouteIsDisplayed() {
		log.info("Verifying Route is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		Assert.assertTrue(commonWaits.isElementPresent(routeLabel), "Route map is not displayed");
	}
	
	public void clickOnBackButton() {
		log.info("Clicking on Back button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(backButton);
	}
	
	public void clickOnInventorySearch() {
		log.info("Clicking on Inventory Search button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		if(isElementPresent(By.xpath(String.format(buttonContainsText, inventorySearch)))) {
			clickWithoutInterception(By.xpath(String.format(buttonContainsText, inventorySearch)));
		} else {
			clickWithoutInterception(By.xpath(String.format(buttonContainsText, Inventorysearch)));
		}
		
	}
	
	public void verifyInventorySearchIsDisplayed() {
		log.info("Verifying Inventory Search is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		Assert.assertTrue(commonWaits.isElementPresent(inventorySearchLabel), "Inventory Search is not displayed");
	}
	
	public void selectInventoryTypeAndInventoryLocation(String type, String location) {
		log.info("Selecting Inventory Type and Inventory Location");
		clickWithoutInterception(inventoryTypeDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, type)));
		clickWithoutInterception(inventoryLocationDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, location)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
	}
	
	public void selectFirstInventoryFromList() {
		log.info("Selecting first inventory from the list");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(By.xpath(String.format(firstInventory,getInventoryTableColumnIndex(serialNumber))));
		clickWithoutInterception(selectInventoryButton);
	}
	
	public void selectWorkOrderTemplate(String templateName) {
		log.info("Selecting Work Order Template");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(By.xpath(String.format(workOrderTemplateRadioButton, templateName)));
		clickWithoutInterception(selectWorkOrderTemplateButton);
	}
	
	public int getInventoryTableColumnIndex(String columnName) {
        List<WebElement> values = findElements(By.xpath(inventoryTableHeaders));
        int index = 0;
        for( int i=0 ; i < values.size(); i++) {
            String headerText = values.get(i).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
            if(headerText.contains(columnName)) {
                index = i;
                break;
            }
        }
       return (index + 1);
    }
	
	public void clickOnAssignWorkOrder() {
		log.info("Clicking on Assign Work Order button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, assignWorkOrder)));
	}
	
	public void addDurationOnSite(String duration) {
		log.info("Adding Duration On Site");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		durationOnSiteInputBox.clear();
		durationOnSiteInputBox.sendKeys(duration);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.SUBMIT)));
	}
	
	public void verifyInventoryTypeAndLocationIsPresent(String type, String location) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		String inventoryTypeValue = findElement(By.xpath(String.format(inventoryDetailsLocator, inventoryTypeLabel))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
		String inventoryLocationValue = findElement(By.xpath(String.format(inventoryDetailsLocator, inventoryLocationLabel))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
		Assert.assertEquals(inventoryTypeValue, type, "Inventory detail value is not matching with expected value");
		Assert.assertEquals(inventoryLocationValue, location, "Inventory location value is not matching with expected value");
	}
	
	public void selectTimelineSupportGroupAndRouteName(String groupName, String routeName) {
		log.info("Selecting Support Group and Route Name");
		clickWithoutInterception(supportGroupDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, groupName)));
		clickWithoutInterception(routeNameDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, routeName)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
	}
	
	public void verifyTimeineSupportGroupAndRouteName(String supportGroup, String routeName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		String supportGroupValue = findElement(By.xpath(String.format(timelineDropdownLocator, supportGroupLabel))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
		String routeNameValue = findElement(By.xpath(String.format(timelineDropdownLocator, routeNameLabel))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
		Assert.assertEquals(supportGroupValue, supportGroup, "Inventory detail value is not matching with expected value");
		Assert.assertEquals(routeNameValue, routeName, "Inventory location value is not matching with expected value");
	}
	
	public void dragAndDropFirstUnscheduledWorkOrderCardToDate() {
        log.info("Dragging and dropping first unscheduled work order card to the date");
		LocalDateTime now = LocalDateTime.now();
        int minute = now.getMinute();
        if (minute < 15) {
            minute = 0;
        } else if (minute < 45) {
            minute = 30;
        } else {
            minute = 0;
        }
        LocalDateTime roundedTime = now.withMinute(minute).withSecond(0).withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        String formattedTime = roundedTime.format(formatter);
        commonWaits.waitForSometime(commonWaits.moreDelay);	// Hard wait to stabilize the timeline panel before drag and drop. If wait is not it is giving stale element exception.
		WebElement firstUnscheduledWorkOrderCard = findElement(By.xpath(firstUnscheduledWorkOrderCardLocator));
		WebElement dateElement = findElement(By.xpath(String.format(datePanelLocator, formattedTime)));
		Actions actions = new Actions(getDriver());
		actions.moveToElement(firstUnscheduledWorkOrderCard).clickAndHold().pause(Duration.ofMillis(500)).moveByOffset(10, 10).pause(Duration.ofMillis(300))
	       .moveToElement(dateElement).pause(Duration.ofMillis(500)).release().build().perform();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
	}
	
	public void verifyWOIsNotPresentInDatePanel() {
		log.info("Verifying work order is not present in date panel");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appFSM);
		Assert.assertFalse(commonWaits.isElementPresent(workOrderInDatePanel), "Work Order is present in date panel");
	}
}
