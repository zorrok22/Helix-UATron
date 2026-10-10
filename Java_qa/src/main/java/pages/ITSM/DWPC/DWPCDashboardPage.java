package pages.ITSM.DWPC;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPCDashboardPage extends BasePage{

	static SolqaLogger log;
    CommonWaits commonWaits;

    // Locators (keep at top; use String only for dynamic templates)
    private static final String RATING_SCALE_OPTION_XPATH_TEMPLATE =
			"//span[contains(text(),'%s')]/ancestor::button[@type='button' and contains(@class,'ui-select-choices-row-inner')]";

	private static final String DELIVERY_LIMIT_TOGGLE_INPUTS_ANY_XPATH =
			"//input[(@type='checkbox' or @type='radio')"
					+ " and contains(translate(@ng-model,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + SolQAConstants.TOKEN_DELIVERY + "')"
					+ " and contains(translate(@ng-model,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + SolQAConstants.TOKEN_LIMIT + "')]"
					+ " | //*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + SolQAConstants.TOKEN_DELIVERY + "')"
					+ " and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + SolQAConstants.TOKEN_LIMIT + "')]"
					+ "/ancestor::*[self::label or self::div][1]//input[@type='checkbox' or @type='radio']";
	private static final String REQUEST_LIFECYCLE_UNIT_OPTION_XPATH_TEMPLATE =
			"//ul[contains(@class,'ui-select-choices') and contains(@class,'dropdown-menu')]//button[contains(@class,'ui-select-choices-row-inner')]//span[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')=translate('%s', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')]";
	/** Request Lifecycle blade Cancel — matches whether disabled or not (used for clickable wait). */
	private static final String CANCEL_REQUEST_LIFECYCLE_BUTTON_XPATH =
			"//button[@type='button' and @sb-i18n='com.bmc.sb.common.button.cancel' and @ng-click='cancel()' and contains(@class,'d-button_secondary') and contains(@class,'d-button_large') and normalize-space()='Cancel']";

	/**
	 * Add Services catalog blade — search field. Scoped to the picker that contains the catalog grid Add button,
	 * so we never type into the Service Actions list search (same {@code sb-grid-search-box} pattern).
	 */
	private static final By ADD_SERVICES_CATALOG_SEARCH_INPUT = By.xpath(
			"//div[.//button[@type='submit' and normalize-space()='Add'] and .//sb-grid-search-box]//sb-grid-search-box//input[@placeholder='Search']");
	
	private static final By ASSET_SERVICE_ADD = By.xpath(
				"//sb-service-request-mapping//div[contains(@class,'sb-box_section')][.//span[normalize-space()='Services']]//button[@ng-click='manageServices()' and normalize-space()='Add']");

	/**
	 * Search input inside the Add Services picker opened from Asset Group options blade.
	 * Keep this separate from {@link #ASSET_SERVICE_ADD}, which is a button.
	 */
	private static final By ASSET_SERVICE_CATALOG_SEARCH_INPUT = By.xpath(
			"(//div[contains(@class,'modal-content')][.//h3[normalize-space()='Service Catalog']]"
					+ "//sb-grid-search-box//input[@placeholder='Search' and not(@type='hidden')]"
					+ " | "
					+ "//div[contains(@class,'sb-select-services-blade')]//sb-grid-search-box"
					+ "//sb-search-input[@ng-model='searchText']//input[@placeholder='Search' and not(@type='hidden')])[last()]");
		
    //String locators
    @FindBy(xpath = "//div[contains(text(), 'Service')]")
    WebElement dwpcPage;
    String ratingScaleOptionXpath="//span[contains(text(),'%s')]/ancestor::button[@type='button' and contains(@class,'ui-select-choices-row-inner')]";
    String dwpcMainMenu= "//*[contains(@class,'menu__link')][text()='%s']";
	String dwpcSubMenu = "//*[contains(@class,'menu__link')]/following-sibling::ul/li/a[text()='%s']";
    String serviceRequestsRows="//div[@class='ui-grid-canvas']//div[@role='row']";
    String columnValueInServiceRequests="((//div[@class='ui-grid-canvas']//div[@role='row'])[%d]//*[contains(@class,'ui-grid-cell-contents')])[%d]";
    String columnHeaderLabel="//*[@role='columnheader']//span[contains(@class,'ui-grid-header-cell-label')]";
    public String menuItemXpath="//button[contains(@class, 'd-n-menu__link') and contains(@class, 'dropdown-toggle') and text()='%s']";
    public String submenuItemXpath="//ul[contains(@class, 'd-n-dropdown') and contains(@class, 'dropdown-menu')]//a[contains(@class, 'd-n-dropdown__link') and text()='%s']";
    public String connectorOptionXpath="//button[contains(@class,'ui-select-choices-row-inner')]//span[text()='%s']";
    public String connectorRowCheckboxXpath="//span[contains(@class,'ng-binding') and text()='%s']/ancestor::div[contains(@class,'ui-grid-cell-contents')]//input[@type='checkbox']";
    public String connectorRowXpath="//div[contains(@class,'ui-grid-cell-contents')]//span[text()='%s']";
    public String connectorRowClickableXpath="//div[contains(@class,'ui-grid-row')]//span[text()='%s']";
    String switchSlider="/following-sibling::*[contains(@class, 'switch__slider')]";//we are concatenating this string with other i.e. created as string

	String ServiceTypeDropDowmnOption = "//div[contains(@class, 'ui-select-choices-row')]//span[normalize-space()='%s']";
	/**
	 * Add Services blade — Service Catalog ui-grid. Scoped with catalog Add + search so we do not match other ui-grids.
	 */
	String addServicesCatalogRowCheckboxVisible = "//div[.//button[@type='submit' and normalize-space()='Add'] and .//sb-grid-search-box]"
			+ "//div[contains(@class,'ui-grid-canvas')]//div[contains(@class,'ui-grid-row')]"
			+ "[.//span[normalize-space()='%s']]//span[contains(@class,'checkbox__item')]";
	/** Service Actions list grid — row whose cells include the action display name */
	String serviceActionNameInGridRow = "//div[@role='row'][.//span[normalize-space()='%s']]";
	/** Asset Groups list — Group Name column (first grid cell per row) */
	String assetGroupNameInGridFirstCell = "//div[contains(@class,'ui-grid-canvas')]//div[contains(@class,'ui-grid-row')]"
			+ "//div[@role='gridcell'][1]//span[normalize-space()='%s']";
	/**
	 * Asset Groups grid — selection control (styled checkbox span) on the row whose Group Name cell matches.
	 */
	String assetGroupRowCheckboxByGroupName = "//div[contains(@class,'ui-grid-canvas')]//div[contains(@class,'ui-grid-row')]"
			+ "[.//div[@role='gridcell'][1]//*[normalize-space()='%s']]//span[contains(@class,'checkbox__item')]";
	/** Service Action options blade — Active switch (ng-model stateModel.isActive) */
	String serviceActionActiveToggleInput = "//input[@ng-model='stateModel.isActive']";

    // Settings - Request Priority
	@FindBy(className = "d-icon-gear")
	WebElement gearIcon;

	@FindBy(xpath = "//*[text()='Request priority']/parent::div")
	WebElement settingRequestPriority;

	String requestPriorityCheckbox = "//*[text()='Request priority']/following-sibling::label/input";

	@FindBy(xpath = "//div[contains(@class, 'sb-request-priority-settings-blade')]//button[text()='Save']")
	WebElement requestPrioritySaveBtn;

    @FindBy(xpath = "//sb-search-input[@ng-model='searchText']//input")
	WebElement serviceRequestIDSearchBox;

    @FindBy(xpath = "//*[contains(@class,'sb-grid-no-results-notifier')]")
	WebElement noResult;

    @FindBy (xpath ="//div[contains(@class, 'dropdown')]//button[contains(text(), 'Services')]")
	WebElement servicesDropdown;

    @FindBy (xpath = "//ul[contains(@class, 'd-n-dropdown')]//a[text()='Services']")
    WebElement servicesOption;

    @FindBy (xpath = "//ul[contains(@class, 'd-n-dropdown')]//a[text()='Surveys']")
    WebElement surveysOption;

    @FindBy (xpath = "//button[@type='button' and @ng-click='openSettings()' and contains(@class,'d-icon-gear')]")
    WebElement settingsGearIcon;

    @FindBy (xpath = "//span[@sb-i18n='com.bmc.sb.core.settings.application.component.label.surveys' and contains(@class,'sb-settings-component-label')]")
    WebElement postRequestSurveyOption;

    @FindBy (xpath = "//span[@sb-i18n='com.bmc.sb.core.surveyRatingStyle.settings.scale.11']")
    WebElement ratingScaleNPSLabel;

    @FindBy (xpath = "//label[@class='switch' and .//input[@ng-model='settings.surveysEnabled']]")
    WebElement surveyToggleSlider;

    @FindBy (xpath = "//input[@ng-model='settings.surveysEnabled']")
    WebElement surveyToggleInput;

    @FindBy (xpath = "(//button[@type='button' and @ng-click='submit()' and contains(@class,'d-button_primary')])[2]")
    WebElement saveButton;

    @FindBy (xpath = "//button[@type='button' and @ng-click='close()' and contains(@class,'d-button_secondary')]")
    WebElement cancelButton;

    @FindBy (xpath = "//button[@type='button' and @ng-click='cancel()' and contains(@class,'d-button_secondary')]")
    WebElement secondCancelButton;

    @FindBy (xpath = "//div[@ng-model='settings.surveyRatingStyle']//button[@type='button' and contains(@class,'ui-select-toggle')]")
    WebElement ratingScaleDropdown;

    @FindBy (xpath = "//button[@type='button' and contains(@class,'d-button_primary')]//span[contains(text(),'Change scale')]/parent::button")
    WebElement changeScaleButton;

	@FindBy(xpath = DELIVERY_LIMIT_TOGGLE_INPUTS_ANY_XPATH)
	public List<WebElement> deliveryLimitToggleInputsAny;

	@FindBy(xpath =
			"//input[contains(@ng-model,'" + SolQAConstants.TOKEN_DELIVERY + "') and contains(@ng-model,'" + SolQAConstants.TOKEN_LIMIT + "') and (contains(@ng-model,'day') or contains(@ng-model,'Day'))]"
					+ " | //input[(@type='number' or @type='text') and (contains(@placeholder,'day') or contains(@aria-label,'day') or contains(@name,'day'))]")
	private List<WebElement> deliveryLimitDaysInputsAny;


    @FindBy (xpath = "//div[contains(@class, 'dropdown')]//button[contains(text(), 'Reports')]")
    WebElement reportsDropdown;
    @FindBy (xpath = "//ul[contains(@class, 'd-n-dropdown')]//a[text()='Service Requests']")
    WebElement serviceRequestsOption;
    @FindBy(xpath = "//span[contains(@sb-i18n,'request-lifecycle')]")
    WebElement requestLifeCycleOption;
    @FindBy(xpath = "//input[@name='serviceRequestTimeToCloseValue']")
    WebElement requestLifecycleInput;
    @FindBy(xpath = "//div[@name='serviceRequestTimeToCloseTimeUnit']//button[contains(@class,'ui-select-toggle')]")
    WebElement requestLifecycleTimeToCloseTimeUnitDropdown;
    
    @FindBy (xpath = "//div[@class='sb-page__title' and @sb-i18n='com.bmc.sb.connector.manage.title']")
    WebElement connectorManagementPageTitle;
    
    @FindBy (xpath = "//button[@type='button' and contains(@class,'d-button') and text()='New Connection']")
    WebElement newConnectionButton;
    
    @FindBy (xpath = "//input[@name='name' and @ng-model='connection.name']")
    WebElement connectionNameInput;
    
    @FindBy (xpath = "//div[@name='connector']//button[@type='button' and contains(@class,'ui-select-toggle')]")
    WebElement connectorDropdownToggle;
    
    @FindBy (xpath = "//button[@sb-i18n='com.bmc.sb.core.connector.connection.create.button.submit']")
    WebElement saveConnectionButton;
    
    @FindBy (xpath = "//button[@sb-i18n='com.bmc.sb.common.button.close']")
    WebElement closeButton;
    
    @FindBy (xpath = "//sb-search-input//input[@type='text' and @placeholder='Search']")
    WebElement connectorSearchBox;
    
    @FindBy (xpath = "//button[contains(@class,'d-button') and text()='Actions']")
    WebElement actionsDropdownButton;
    
    @FindBy (xpath = "//button[contains(@class,'sb-grid-action-dropdown-button') and text()='Delete']")
    WebElement deleteActionMenuItem;
    
    @FindBy (xpath = "//button[contains(@class,'d-button_primary')]//span[text()='Delete Connection']/parent::button")
    WebElement deleteConnectionConfirmButton;
    
	@FindBy (xpath = "//button[@type='button' and @sb-i18n='com.bmc.sb.common.button.cancel' and @ng-click='cancel()' and contains(@class,'d-button_secondary') and contains(@class,'d-button_large') and normalize-space()='Cancel' and (not(@disabled) or normalize-space(@disabled)='')]")
	WebElement cancelButtonLifeCycElement;

	@FindBy (xpath = "//button[@type='button' and @ng-click='submit()' and @sb-i18n='com.bmc.sb.common.button.save' and contains(@class,'d-button_primary') and normalize-space()='Save' and (not(@disabled) or normalize-space(@disabled)='')]")
	WebElement saveButtonLifeCycElement;
	
	@FindBy (xpath = "//button[normalize-space()='Create Action']")
	WebElement newServiceActionButton;
    
	@FindBy (xpath = "//input[@ng-model='action.name']")
	WebElement serviceActionNameInput;

	@FindBy (xpath = "//button[normalize-space()='New Group']")
	WebElement newAssetGroupButton;

	@FindBy (xpath = "//input[@name='name' and @ng-model='assetGroup.name']")
	WebElement assetGroupNameInput;

	@FindBy (xpath = "//button[@sb-i18n='com.bmc.sb.common.button.close' and contains(@class, 'd-button_secondary')]")
	WebElement CloseGroupOptionButton;

	@FindBy (xpath = "//button[@sb-i18n='com.bmc.sb.common.button.confirm']")
	WebElement NewAssetConfirmButton;

	@FindBy (xpath = "//div[@ng-model='action.type']//button[contains(@class, 'ui-select-toggle')]")
    WebElement ServiceTypeDropdown;

	@FindBy (xpath = "//button[normalize-space()='Confirm']")
    WebElement confirmButton;

	@FindBy (xpath = "//button[@ng-click='ServiceActionOptionsCtrl.configureAssets()']")
    WebElement assetMappingEditButton;

	@FindBy (xpath = "//button[@ng-click='manageServices()']")
    WebElement AddServicesButton;

	@FindBy (xpath = "//button[@type='submit' and normalize-space()='Add']")
    WebElement AddServicesAddButton;
	
	@FindBy (xpath = "(//button[@ng-click='close()'])[last()]")
	WebElement CloseAddServicesButton;

	@FindBy (xpath = "//button[@sb-i18n='com.bmc.sb.common.button.close' and contains(@class, 'd-button_primary')]")
	WebElement CloseActionsButton;

	@FindBy(xpath = "(//sb-grid-search-box//input)[1]")
	WebElement serviceActionsListSearchBox;

	/** Confirm on "Delete this group?" modal (Asset Groups delete). */
	@FindBy(xpath = "//div[contains(@class,'modal-content')][.//h3[contains(normalize-space(.),'Delete this group')]]"
			+ "//button[contains(@class,'d-button_primary')][.//span[normalize-space()='Confirm']]")
	WebElement deleteAssetGroupModalConfirmButton;

    String postRequestSurveyToggleInput = "//*[text()='Post-request survey']/following-sibling::label/input";
    String frequencyAdjustmentToggleInput = "//*[text()='Frequency adjustment']/following-sibling::label/input";

    public DWPCDashboardPage() {
      PageFactory.initElements(getDriver(), this);
		  log = new SolqaLogger(DWPCDashboardPage.class.getName());
		  commonWaits=new CommonWaits();
	  }

	public DWPCDashboardPage clickOnGearIcon() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.waitForElementVisible(gearIcon);
		commonWaits.clickWithoutInterception(gearIcon);
        commonWaits.waitForExecutionComplete();
        return this;
	}

	public DWPCDashboardPage enableRequestPriority(boolean enable) {
		log.info("Setting DWPC Request priority toggle to: " + enable);
		clickOnGearIcon();
		commonWaits.scrollToSpecificElement(settingRequestPriority);
		commonWaits.clickWithoutInterception(settingRequestPriority);
		commonWaits.waitForExecutionComplete();
        WebElement WebElement = commonWaits.waitForElementPresenceLocated(By.xpath(requestPriorityCheckbox));
		String clazz = WebElement.getAttribute(SolQAConstants.ATTRIBUTE_CLASS);
		boolean isEnabled = clazz != null && clazz.contains(SolQAConstants.NOT_NG_EMPTY_CLASS);
		if (enable && !isEnabled) {
			commonWaits.clickWithoutInterception(By.xpath(requestPriorityCheckbox+switchSlider));
			commonWaits.waitForElementClickable(requestPrioritySaveBtn).click();
		} else if (!enable && isEnabled) {
			commonWaits.clickWithoutInterception(By.xpath(requestPriorityCheckbox+switchSlider));
			commonWaits.waitForElementClickable(requestPrioritySaveBtn).click();
		} else {
			log.info("Request priority toggle already in desired state: " + enable);
		}
		commonWaits.waitForExecutionComplete();
        return this;
	}

	public DWPCDashboardPage navigateToDWPCPage(String mainMenu, String subMenu) {
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dwpcMainMenu, mainMenu))).click();
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dwpcSubMenu, subMenu))).click();
		commonWaits.waitForExecutionComplete();
        return this;
	}

	public String getServiceRequestPriorityFromReports(String reportsMenu, String serviceRequestsSubMenu, String requestId, String priorityColumnLabel) {
		log.info("Opening DWPC Reports -> " + serviceRequestsSubMenu + " and searching request: " + requestId);
        refreshBrowser();
		navigateToDWPCPage(reportsMenu, serviceRequestsSubMenu);

		commonWaits.waitForElementVisible(serviceRequestIDSearchBox).click();
		serviceRequestIDSearchBox.clear();
		serviceRequestIDSearchBox.sendKeys(requestId);
		serviceRequestIDSearchBox.sendKeys(Keys.ENTER);
		commonWaits.waitForExecutionComplete();

		if (commonWaits.isElementPresent(noResult)) {
			Assert.fail("No results found in DWPC Service Requests report for request id: " + requestId);
		}

		List<WebElement> headers = commonWaits.getElements(By.xpath(columnHeaderLabel));
		int priorityColIndex = -1;
		for (int i = 0; i < headers.size(); i++) {
			String h = headers.get(i).getText().trim();
			if (h.equalsIgnoreCase(priorityColumnLabel) || h.toLowerCase().contains(priorityColumnLabel.toLowerCase())) {
				priorityColIndex = i + 1; // 1-based for xpath index
				break;
			}
		}
		Assert.assertTrue(priorityColIndex > 0, "Could not find Priority column '" + priorityColumnLabel + "' in DWPC report.");

		List<WebElement> rows = commonWaits.getElements(By.xpath(serviceRequestsRows));
        Assert.assertFalse(rows.isEmpty(), "No rows loaded in DWPC Service Requests report after search for: " + requestId);

		int matchingRowIndex = -1;
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).getText().contains(requestId)) {
				matchingRowIndex = i + 1;
				break;
			}
		}
		Assert.assertTrue(matchingRowIndex > 0, "Could not find a row containing request id '" + requestId + "' in DWPC report table.");

		String cellXpath = String.format(columnValueInServiceRequests, matchingRowIndex, priorityColIndex);
		String priorityValue = commonWaits.waitForByElementToBeVisible(By.xpath(cellXpath)).getText().trim();
		log.info("DWPC Report priority for " + requestId + " is: " + priorityValue);
		return priorityValue;
	}

    public boolean isServiceRequestPresentInReports(String reportsMenu, String serviceRequestsSubMenu, String requestId) {
        log.info("Opening DWPC Reports -> " + serviceRequestsSubMenu + " and searching request (presence check): " + requestId);
        navigateToDWPCPage(reportsMenu, serviceRequestsSubMenu);

        commonWaits.waitForElementVisible(serviceRequestIDSearchBox).click();
        serviceRequestIDSearchBox.clear();
        serviceRequestIDSearchBox.sendKeys(requestId);
        serviceRequestIDSearchBox.sendKeys(Keys.ENTER);
        commonWaits.waitForExecutionComplete();

        if (commonWaits.isElementPresent(noResult)) {
            log.info("No results found in DWPC Service Requests report for request id: " + requestId);
            return false;
        }

        List<WebElement> rows = commonWaits.getElements(By.xpath(serviceRequestsRows));
        if (rows == null || rows.isEmpty()) {
            log.info("No rows loaded in DWPC Service Requests report after search for: " + requestId);
            return false;
        }

        for (WebElement row : rows) {
            if (row.getText().contains(requestId)) {
                log.info("Found request id in DWPC report table: " + requestId);
                return true;
            }
        }
        log.info("Could not find a row containing request id '" + requestId + "' in DWPC report table.");
        return false;
    }
    
    public void verifyDWPCPageLaunchedSuccessfully() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), dwpcPage, 20), " Digital Workplace Catalog application is not launched successfully");
    }

	public DWPCServicesPage openServicesPage() {

		log.info("Open Services page");
		final String servicesLabel = SolQAConstants.SERVICES;
		if (commonWaits.isElementPresent(servicesDropdown)) {
			log.info("Click on Services dropdown");
			commonWaits.waitForElementVisible(servicesDropdown);
			servicesDropdown.click();
			log.info("Click on Services option");
			commonWaits.waitForElementVisible(servicesOption);
			servicesOption.click();
		} else if (commonWaits.isElementPresent(By.xpath(String.format(menuItemXpath, servicesLabel)))) {
			log.info("Using new navigation menu to open Services");
			navigateToSubmenu(servicesLabel, servicesLabel);
		} else if (commonWaits.isElementPresent(By.xpath(String.format(dwpcMainMenu, servicesLabel)))) {
			log.info("Using legacy navigation menu to open Services");
			navigateToDWPCPage(servicesLabel, servicesLabel);
		} else {
			Assert.fail("Services menu not found on DWPC page");
		}

		log.info("Click on " + SolQAConstants.SERVICES + " dropdown");
		commonWaits.waitForElementVisible(servicesDropdown);
		servicesDropdown.click();
		log.info("Click on " + SolQAConstants.SERVICES + " option");
		commonWaits.waitForElementVisible(servicesOption);
		servicesOption.click();

		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return new DWPCServicesPage();
	}

	public DWPCSurveysPage openSurveysPage() {
		log.info("Click on " + SolQAConstants.SERVICES + " dropdown");
		commonWaits.waitForElementVisible(servicesDropdown);
		servicesDropdown.click();
		log.info("Click on " + SolQAConstants.SURVEYS + " option");
		commonWaits.waitForElementVisible(surveysOption);
		surveysOption.click();
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return new DWPCSurveysPage();
	}

	public DWPCDashboardPage selectRatingScale(String ratingScaleValue) {
		log.info("Selecting Rating scale: " + ratingScaleValue);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.waitForElementVisible(ratingScaleDropdown);
		commonWaits.clickWithoutInterception(ratingScaleDropdown);
		WebElement ratingScaleOption = commonWaits.waitForByElementToBeVisible(
				By.xpath(String.format(RATING_SCALE_OPTION_XPATH_TEMPLATE, ratingScaleValue))
		);
		commonWaits.clickWithoutInterception(ratingScaleOption);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("Rating scale selected: " + ratingScaleValue);
		return this;
	}

	public DWPCDashboardPage openPostRequestSurveySettings() {
		log.info("Opening Post-request survey settings");
		commonWaits.waitForElementVisible(settingsGearIcon);
		commonWaits.clickWithoutInterception(settingsGearIcon);
		commonWaits.waitForElementVisible(postRequestSurveyOption);
		commonWaits.clickWithoutInterception(postRequestSurveyOption);
		log.info("Post-request survey settings opened");

		return this;
	}

	public DWPCDashboardPage closeSettings() {
		log.info("Checking if Save button is enabled");
		commonWaits.waitForElementVisible(saveButton);

		boolean isSaveButtonEnabled = saveButton.isEnabled();
		String ngDisabledAttribute = saveButton.getAttribute("ng-disabled");
		String disabledAttribute = saveButton.getAttribute("disabled");

		log.info("Save button enabled: " + isSaveButtonEnabled);
		log.info("Save button ng-disabled attribute: " + ngDisabledAttribute);
		log.info("Save button disabled attribute: " + disabledAttribute);

		// Check if Save button is enabled (not disabled)
		if (saveButton.isEnabled()) {
			log.info("Save button is enabled, clicking Save");
			commonWaits.clickWithoutInterception(saveButton);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			commonWaits.waitForElementVisible_withFluentWait(getDriver(), secondCancelButton, 20);
			commonWaits.clickWithoutInterception(secondCancelButton);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Settings saved successfully");
		} else {
			log.info("Save button is disabled, clicking Cancel to close settings");
			if(cancelButton.isEnabled()) {
				clickWithoutInterception(cancelButton);
				commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
				commonWaits.waitForSometime(5000);
			}
			if(secondCancelButton.isEnabled()) {
				clickWithoutInterception(secondCancelButton);
				commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
				commonWaits.waitForSometime(5000);
			}
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
			commonWaits.waitForPageFullyLoaded(10, 2);
			log.info("Settings closed via Cancel");
		}
		return this;
	}

	public DWPCDashboardPage handleChangeScaleWarningPopup() {
		log.info("Checking if Change Scale warning popup is present");
		if (commonWaits.isElementPresent(changeScaleButton)) {
			log.info("Change Scale warning popup is present, clicking 'Change scale' button");
			commonWaits.waitForElementVisible(changeScaleButton);
			commonWaits.clickWithoutInterception(changeScaleButton);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Change scale confirmed successfully");
		} else {
			log.info("Change Scale warning popup is not present");
		}
		return this;
	}

	public DWPCDashboardPage closeSurveyOptionsPopupIfPresent() {
		// Close Survey Options popup if it appears
		log.info("Checking if Survey Options popup is present");
		if (commonWaits.isElementPresent(cancelButton)) {
			log.info("Survey Options popup is present, closing it");
			commonWaits.waitForElementClickable(cancelButton);
			clickWithoutInterception(cancelButton);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Survey Options popup closed successfully");
		} else {
			log.info("Survey Options popup is not present");
		}

		return this;
	}

	public DWPCDashboardPage verifyPostRequestSurveySettings(String ratingScaleValue) {
		log.info("Step 1: Click on Settings gear icon and select Post-request survey");
		openPostRequestSurveySettings();

		log.info("Step 2: Check and enable Survey toggle if not ON");
		commonWaits.waitForElementVisible(surveyToggleSlider);

		// Check if toggle is ON by checking the input's checked attribute (input may be hidden)
		String checkedValue = surveyToggleInput.getAttribute("checked");
		String ngModelValue = surveyToggleInput.getAttribute("ng-model");

		log.info("Toggle checked attribute value: " + checkedValue);
		log.info("Toggle ng-model: " + ngModelValue);

		boolean isToggleOn = checkedValue != null && checkedValue.equals("true");
		log.info("Toggle is ON: " + isToggleOn);

		if (!isToggleOn) {
			log.info("Survey toggle is OFF, enabling it now");
			commonWaits.clickWithoutInterception(surveyToggleSlider);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Survey toggle enabled successfully");
		} else {
			log.info("Survey toggle is already ON");
		}
		
		log.info("Step 3: Select Rating scale value");
		selectRatingScale(ratingScaleValue);
		handleChangeScaleWarningPopup();

		log.info("Step 4: Close settings");
		closeSettings();
		closeSurveyOptionsPopupIfPresent();
		return this;
	}

	public DWPCDashboardPage navigateToSubmenu(String parentMenu, String subMenu) {
		log.info("Navigating to " + parentMenu + " > " + subMenu);
		
		log.info("Clicking on parent menu: " + parentMenu);
		WebElement menuElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuItemXpath, parentMenu)));
		commonWaits.clickWithoutInterception(menuElement);
		
		log.info("Clicking on submenu: " + subMenu);
		WebElement submenuElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(submenuItemXpath, subMenu)));
		commonWaits.clickWithoutInterception(submenuElement);
		
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("Successfully navigated to " + parentMenu + " > " + subMenu);
		
		return this;
	}
	
	public DWPCDashboardPage verifyConnectorManagementPageLoaded() {
		log.info("Verifying Connector Management page is loaded");
		commonWaits.waitForElementVisible(connectorManagementPageTitle);
		Assert.assertTrue(connectorManagementPageTitle.isDisplayed(), "Connector Management page is not loaded successfully");
		log.info("Connector Management page loaded successfully");
		return this;
	}
	
	public DWPCDashboardPage clickNewConnection() {
		log.info("Clicking on New Connection button");
		commonWaits.waitForElementVisible(newConnectionButton);
		commonWaits.clickWithoutInterception(newConnectionButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("New Connection button clicked successfully");
		return this;
	}
	

	public DWPCDashboardPage enterConnectionName(String connectionName) {
		log.info("Entering Connection Name: " + connectionName);
		commonWaits.waitForElementVisible(connectionNameInput);
		connectionNameInput.clear();
		connectionNameInput.sendKeys(connectionName);
		log.info("Connection Name entered successfully");
		return this;
	}
	
	
	public DWPCDashboardPage selectConnector(String connectorName) {
		log.info("Selecting Connector: " + connectorName);
		
		// Click on dropdown toggle to open the dropdown
		commonWaits.waitForElementVisible(connectorDropdownToggle);
		commonWaits.clickWithoutInterception(connectorDropdownToggle);
		
		// Wait for dropdown to open and select the connector option
		WebElement connectorOption = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectorOptionXpath, connectorName)));
		commonWaits.clickWithoutInterception(connectorOption);
		
		log.info("Connector selected successfully: " + connectorName);
		return this;
	}
	
	
	public DWPCDashboardPage saveConnection() {
		log.info("Clicking on Save button to save the connection");
		commonWaits.waitForElementVisible(saveConnectionButton);
		commonWaits.clickWithoutInterception(saveConnectionButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("Connection saved successfully");
		return this;
	}
	
	
	public DWPCDashboardPage clickCloseIfExists() {
		log.info("Checking if Close button exists within 10 seconds");
		if (commonWaits.isElementPresent(closeButton, 10)) {
			log.info("Close button found, clicking on it");
			commonWaits.clickWithoutInterception(closeButton);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Close button clicked successfully");
		} else {
			log.info("Close button did not appear within 10 seconds, continuing without clicking");
		}
		return this;
	}
    
	
	public DWPCDashboardPage deleteConnectorIfExists(String connectionName) {
		log.info("Searching for connector: " + connectionName);
		
		// Search for connector in search box
		commonWaits.waitForElementVisible(connectorSearchBox);
		connectorSearchBox.clear();
		connectorSearchBox.sendKeys(connectionName);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("Search completed for: " + connectionName);
		
		// Check if connector exists in table
		By connectorRowLocator = By.xpath(String.format(connectorRowXpath, connectionName));
		boolean isConnectorExists = commonWaits.isElementPresent(connectorRowLocator);
		log.info("Connector exists: " + isConnectorExists);
		
		if (isConnectorExists) {
			log.info("Connector found, proceeding to delete: " + connectionName);
			
			// Click on the connector row to select it
			By rowLocator = By.xpath(String.format(connectorRowClickableXpath, connectionName));
			WebElement row = commonWaits.findElement(rowLocator);
			row.click();
			log.info("Connector row clicked and selected");
			
			commonWaits.clickWithoutInterception(actionsDropdownButton);
			log.info("Actions dropdown clicked");
			
			commonWaits.clickWithoutInterception(deleteActionMenuItem);
			log.info("Delete menu item clicked");
			
			// Confirm deletion
			commonWaits.clickWithoutInterception(deleteConnectionConfirmButton);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Connector deleted successfully: " + connectionName);
		} else {
			log.info("Connector does not exist: " + connectionName);
		}
		
		return this;
	}
	
	public DWPCServiceRequests openServiceRequestsReports() {
		log.info("Click on " + SolQAConstants.REPORTS + " dropdown");
		log.info("Click on Reports dropdown");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.waitForElementVisible(reportsDropdown);
		clickWithoutInterception(reportsDropdown);
		log.info("Click on " + SolQAConstants.SERVICE_REQUESTS + " option");
		commonWaits.waitForElementVisible(serviceRequestsOption);
		clickWithoutInterception(serviceRequestsOption);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return new DWPCServiceRequests();
	}
	
	public DWPCDashboardPage  openRequestLifeCycleSettings() {
		log.info("Opening Post-request survey settings");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.waitForElementVisible(settingsGearIcon);
		commonWaits.clickWithoutInterception(settingsGearIcon);
		commonWaits.waitForElementVisible(requestLifeCycleOption);
		commonWaits.clickWithoutInterception(requestLifeCycleOption);
		log.info("Request Lifecycle settings opened");
		return this;
	}
	
	public String getRequestLifecycleValue() {
        log.info("Getting current Request Lifecycle value");
        commonWaits.waitForAngularRequestsToFinish();
        String value = requestLifecycleInput.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
        log.info("Current Request Lifecycle value: " + value);
        return value;
    }
	
	public DWPCDashboardPage setRequestLifecycleValueDays(String days) {
		return setRequestLifecycleValue(days, "days");
	}

	private void selectServiceInAddServicesCatalogGrid(String serviceDisplayName) {
		Assert.assertNotNull(serviceDisplayName, "Service display name must not be null when selecting in Add Services catalog");
		String name = serviceDisplayName.trim();
		Assert.assertFalse(name.isEmpty(), "Service display name must not be empty when selecting in Add Services catalog");
		By checkbox = By.xpath(String.format(addServicesCatalogRowCheckboxVisible, name));
		WebElement el = commonWaits.waitForElementClickable(commonWaits.waitForElementPresenceLocated(checkbox));
		javaScriptScrollIntoView(el);
		commonWaits.clickWithoutInterception(el);
	}

	private void clickCancelRequestLifecycleIfReady(int timeoutSeconds, String successMsg, String skipMsgPrefix) {
		try {
			Wait<WebDriver> cancelWait = new FluentWait<>(getDriver())
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(StaleElementReferenceException.class)
					.ignoring(NoSuchElementException.class);
			WebElement cancelReady = cancelWait
					.until(ExpectedConditions.elementToBeClickable(By.xpath(CANCEL_REQUEST_LIFECYCLE_BUTTON_XPATH)));
			commonWaits.clickWithoutInterception(cancelReady);
			log.info(successMsg);
		} catch (Exception e) {
			log.info(skipMsgPrefix + e.getMessage());
		}
	}

	public DWPCDashboardPage setRequestLifecycleValue(String days, String timeToCloseUnit) {
        log.info("Setting Request Lifecycle value to: " + days + " and unit to: " + timeToCloseUnit);
        commonWaits.waitForAngularRequestsToFinish();
        keyClearAndType(requestLifecycleInput, days);
		commonWaits.waitForAngularRequestsToFinish();
		selectRequestLifecycleTimeToCloseTimeUnit(timeToCloseUnit);
	
		if (saveButtonLifeCycElement.isEnabled()) {
			log.info("Save button is enabled, clicking Save");
			commonWaits.clickWithoutInterception(saveButtonLifeCycElement);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Settings saved successfully");
		} else {
			log.info("Save button is disabled; waiting up to 20s for Cancel to be present and enabled");
			clickCancelRequestLifecycleIfReady(20, "Settings closed via Cancel",
					"Cancel button not present or not enabled within 20s; skipping Cancel click. ");
		}
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Attempting second Cancel if Request Lifecycle UI still exposes Cancel");
		clickCancelRequestLifecycleIfReady(20, "Second Cancel click completed",
				"Second Cancel not present or not enabled within 20s; skipping. ");
		return this;
	}

    @FindBy(xpath = "//*[text()='Post-request survey']/parent::div")
    WebElement postRequestSurveySetting;
    public DWPCDashboardPage setFrequencyAdjustment(boolean enable, int deliveryLimitDays) {
		log.info("Updating Post-request survey / Frequency adjustment settings. enabled=" + enable + ", days=" + deliveryLimitDays);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

		log.info("Opening DWPC settings via gear icon");
        clickOnGearIcon();

		log.info("Opening 'Post-request survey' settings");
        commonWaits.scrollToSpecificElement(postRequestSurveySetting);
        commonWaits.clickWithoutInterception(postRequestSurveySetting);
        commonWaits.waitForExecutionComplete();

		if (enable) {
			log.info("Ensuring 'Post-request survey' toggle is enabled");
			setToggleState(postRequestSurveyToggleInput, true, "Post-request survey");

			log.info("Ensuring 'Frequency adjustment' toggle is enabled");
			setToggleState(frequencyAdjustmentToggleInput, true, "Frequency adjustment");

			Assert.assertTrue(deliveryLimitDays >= 0,
					"Delivery limit days must be 0 or greater when enabling Frequency adjustment");

			log.info("Selecting 'Days since last survey' option");
			commonWaits.clickWithoutInterception(daysSinceLastSurveyOption);
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), deliveryLimitDaysInput, 5),
					"Delivery limit days input did not become visible after selecting 'Days since last survey' option");

			log.info("Setting delivery limit days to: " + deliveryLimitDays);
			keyClearAndType(deliveryLimitDaysInput, String.valueOf(deliveryLimitDays));
		} else {
			log.info("Ensuring 'Post-request survey' toggle is disabled");
			setToggleState(postRequestSurveyToggleInput, false, "Post-request survey");
            if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), disableSurveysButton, 5)) {
                log.info("Clicking on 'Disable Surveys' button to disable Frequency adjustment");
                commonWaits.clickWithoutInterception(disableSurveysButton);
            }
        }
        commonWaits.waitForExecutionComplete();
		log.info("Saving Post-request survey settings");
        if (postRequestSurveySaveBtn.isEnabled()) {
            commonWaits.clickWithoutInterception(postRequestSurveySaveBtn);
        }
		commonWaits.waitForExecutionComplete();
		log.info("Post-request survey settings saved successfully");
        return this;
    }

    @FindBy(xpath = "//button[contains(. ,'Disable Surveys')]")
    WebElement disableSurveysButton;
	private void setToggleState(String toggleInputXpath, boolean enable, String toggleName) {
		boolean isEnabled = isToggleEnabled(toggleInputXpath);
		if (isEnabled == enable) {
			log.info(toggleName + " toggle is already in desired state: " + enable);
			return;
		}

		log.info("Changing '" + toggleName + "' toggle to: " + enable);
		commonWaits.clickWithoutInterception(By.xpath(toggleInputXpath + switchSlider));
		commonWaits.waitForExecutionComplete();

		boolean updatedState = isToggleEnabled(toggleInputXpath);
		Assert.assertEquals(updatedState, enable,
				toggleName + " toggle did not change to expected state: " + enable);
		log.info("'" + toggleName + "' toggle changed successfully to: " + updatedState);
	}

	private boolean isToggleEnabled(String toggleInputXpath) {
		WebElement toggleInput = commonWaits.waitForElementPresenceLocated(By.xpath(toggleInputXpath));
		String clazz = toggleInput.getAttribute(SolQAConstants.ATTRIBUTE_CLASS);
		return (clazz != null && clazz.contains(SolQAConstants.NOT_NG_EMPTY_CLASS)) || toggleInput.isSelected();
	}
    @FindBy(xpath = "//input[@id='deliveryLimitDaysCount']")
    WebElement deliveryLimitDaysInput;
    @FindBy(xpath = "//*[contains(@class, 'sb-survey-settings-blade')]//button[contains(., 'Save')]")
    WebElement postRequestSurveySaveBtn;
    @FindBy(xpath = "//input[@name='deliveryLimitType']/following-sibling::span[normalize-space()='Days since last survey']")
    WebElement daysSinceLastSurveyOption;

    public String checkbox = "//*[normalize-space()='%s']/parent::div//input[@type='checkbox']";
    public String toggle = "//*[normalize-space()='%s']/parent::div//label//span";
    public String searchSetting = "//*[@class='sb-blade-body']//*[normalize-space()='%s']";

    public String getServiceRequestColumnValueFromReports(String reportsMenu, String serviceRequestsSubMenu, String requestId, String columnLabelToFind) {
        log.info("Opening DWPC Reports -> " + serviceRequestsSubMenu + " and reading column '" + columnLabelToFind + "' for request: " + requestId);
        navigateToDWPCPage(reportsMenu, serviceRequestsSubMenu);

        commonWaits.waitForElementVisible(serviceRequestIDSearchBox).click();
        serviceRequestIDSearchBox.clear();
        serviceRequestIDSearchBox.sendKeys(requestId);
        serviceRequestIDSearchBox.sendKeys(Keys.ENTER);
        commonWaits.waitForExecutionComplete();

        if (commonWaits.isElementPresent(noResult)) {
            Assert.fail("No results found in DWPC Service Requests report for request id: " + requestId);
        }

        List<WebElement> headers = commonWaits.getElements(By.xpath(columnHeaderLabel));
        int colIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i).getText().trim();
            if (h.equalsIgnoreCase(columnLabelToFind) || h.toLowerCase().contains(columnLabelToFind.toLowerCase())) {
                colIndex = i + 1; // 1-based for xpath index
                break;
            }
        }
        Assert.assertTrue(colIndex > 0, "Could not find column '" + columnLabelToFind + "' in DWPC report.");

        List<WebElement> rows = commonWaits.getElements(By.xpath(serviceRequestsRows));
        Assert.assertFalse(rows.isEmpty(), "No rows loaded in DWPC Service Requests report after search for: " + requestId);

        int matchingRowIndex = -1;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getText().contains(requestId)) {
                matchingRowIndex = i + 1;
                break;
            }
        }
        Assert.assertTrue(matchingRowIndex > 0, "Could not find a row containing request id '" + requestId + "' in DWPC report table.");

        String cellXpath = String.format(columnValueInServiceRequests, matchingRowIndex, colIndex);
        String value = commonWaits.waitForByElementToBeVisible(By.xpath(cellXpath)).getText().trim();
        log.info("DWPC Report [" + columnLabelToFind + "] for " + requestId + " is: " + value);
        return value;
    }

    /**
     * Reads a date/time cell from the DWPC Service Requests report and normalizes it to ISO yyyy-MM-dd.
     * Umbrella validated DWPC Service Request Details API timestamps as yyyy-MM-dd.
     */
    public String getServiceRequestDateColumnValueAsIsoDate(String reportsMenu, String serviceRequestsSubMenu, String requestId, String dateColumnLabel) {
        String raw = getServiceRequestColumnValueFromReports(reportsMenu, serviceRequestsSubMenu, requestId, dateColumnLabel);
        String iso = normalizeToIsoDate(raw);
        log.info("DWPC Report [" + dateColumnLabel + "] normalized ISO date for " + requestId + " is: " + iso);
        return iso;
    }

    private String normalizeToIsoDate(String raw) {
        if (raw == null) {
            Assert.fail("Date value is null; cannot normalize to ISO date");
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            Assert.fail("Date value is empty; cannot normalize to ISO date");
        }

        // Prefer ISO date if present.
        Matcher iso = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})").matcher(s);
        if (iso.find()) {
            return iso.group(1);
        }

        // Try common UI formats like M/d/yyyy or MM/dd/yyyy (with optional time).
        Matcher mdy = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4})").matcher(s);
        if (mdy.find()) {
            String datePart = mdy.group(1);
            DateTimeFormatter[] fmts = new DateTimeFormatter[]{
                    DateTimeFormatter.ofPattern("M/d/yyyy"),
                    DateTimeFormatter.ofPattern("MM/dd/yyyy")
            };
            for (DateTimeFormatter fmt : fmts) {
                try {
                    LocalDate d = LocalDate.parse(datePart, fmt);
                    return d.format(DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ignored) {
                }
            }
        }

        Assert.fail("Unable to normalize date to ISO yyyy-MM-dd from value: '" + raw + "'");
        return "";
    }

    public DWPCDashboardPage setDwpcAndWildcardSearchSettings(boolean enableAndLogic, boolean enableWildcard, Map<Object, String> dwpcSettingsData) {
        String searchSettingName = dwpcSettingsData.get("SearchSettingName");
        String andLogicSettingText = dwpcSettingsData.get("ANDLogicSettingText");
        String wildcardSettingText = dwpcSettingsData.get("WildcardSettingText");
        openDwpcSearchSettingsBlade(searchSettingName);
        setDwpcSearchToggle(andLogicSettingText, enableAndLogic);

        openDwpcSearchSettingsBlade(searchSettingName);
        setDwpcSearchToggle(wildcardSettingText, enableWildcard);
        return this;
    }

    public DWPCDashboardPage openDwpcSearchSettingsBlade(String searchSettingName) {
        commonWaits.waitForElementVisible(gearIcon).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchSetting, searchSettingName))).click();
        commonWaits.waitForExecutionComplete();
        return this;
    }
    @FindBy(xpath = "//*[@name='searchSettingsForm']//*[normalize-space()='Save']")
    WebElement saveBtn;
    public DWPCDashboardPage setDwpcSearchToggle(String settingLabelText, boolean enable) {

        WebElement checkboxEl = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(checkbox, settingLabelText)));
        String clazz = checkboxEl.getAttribute(SolQAConstants.ATTRIBUTE_CLASS) == null ? "" : checkboxEl.getAttribute(SolQAConstants.ATTRIBUTE_CLASS);
        boolean enabledNow = clazz.contains(SolQAConstants.NOT_NG_EMPTY_CLASS) || checkboxEl.isSelected();

        if (enable != enabledNow) {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(toggle, settingLabelText))).click();
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForElementVisible(saveBtn).click();
            commonWaits.waitForExecutionComplete();
        }

        // Verify state after save
        clazz = checkboxEl.getAttribute(SolQAConstants.ATTRIBUTE_CLASS) == null ? "" : checkboxEl.getAttribute(SolQAConstants.ATTRIBUTE_CLASS);
        enabledNow = clazz.contains(SolQAConstants.NOT_NG_EMPTY_CLASS) || checkboxEl.isSelected();
        Assert.assertEquals(enabledNow, enable, "DWPC Search setting '" + settingLabelText + "' did not match expected value after save");

        getDriver().navigate().refresh();
        commonWaits.waitForExecutionComplete();
        return this;
    }
	public DWPCDashboardPage selectRequestLifecycleTimeToCloseTimeUnit(String unit) {
		Assert.assertNotNull(unit, "Time unit cannot be null.");
		String normalizedUnit = unit.trim();
		Assert.assertFalse(normalizedUnit.isEmpty(), "Time unit cannot be empty.");
		log.info("Selecting Request Lifecycle time-to-close unit: " + normalizedUnit);
		commonWaits.waitForElementVisible(requestLifecycleTimeToCloseTimeUnitDropdown);
		commonWaits.clickWithoutInterception(requestLifecycleTimeToCloseTimeUnitDropdown);
		WebElement timeUnitOption = commonWaits.waitForByElementToBeVisible(
				By.xpath(String.format(REQUEST_LIFECYCLE_UNIT_OPTION_XPATH_TEMPLATE, normalizedUnit)));
		commonWaits.clickWithoutInterception(timeUnitOption);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Request Lifecycle time-to-close unit selected: " + normalizedUnit);
		return this;
	}

	public DWPCDashboardPage selectRequestLifecycleTimeToCloseTimeUnitAsDays() {
		return selectRequestLifecycleTimeToCloseTimeUnit("days");
	}

	public DWPCDashboardPage createServiceActionAndMapWithService(String serviceActionName, String serviceName) {
		log.info("Navigate to service actions page");
		navigateToDWPCPage("Services", "Service Actions");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.waitForElementVisible(newServiceActionButton);

		String actionNameToFind = serviceActionName != null ? serviceActionName.trim() : "";
		if (!actionNameToFind.isEmpty()) {
			log.info("Checking if Service Action already exists: " + actionNameToFind);
			commonWaits.waitForElementVisible(serviceActionsListSearchBox);
			keyClearAndType(serviceActionsListSearchBox, actionNameToFind);
			commonWaits.waitForAngularRequestsToFinish();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
			By existingServiceActionRow = By.xpath(String.format(serviceActionNameInGridRow, actionNameToFind));
			if (commonWaits.isElementPresent(existingServiceActionRow, 10)) {
				log.info("Service Action already present; skipping create and map flow: " + actionNameToFind);
				return this;
			}
			log.info("Service Action not found in list; proceeding to create: " + actionNameToFind);
		}

		log.info("Click on new service action button");
		commonWaits.clickWithoutInterception(newServiceActionButton);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.keyClearAndType(serviceActionNameInput, serviceActionName);
		log.info("Select Service Type");
		commonWaits.clickWithoutInterception(ServiceTypeDropdown);
		commonWaits.clickWithoutInterception(By.xpath(String.format(ServiceTypeDropDowmnOption, "Day 2 Action")));

		log.info("Click on confirm button");
		commonWaits.clickWithoutInterception(confirmButton);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Click on Asset mapping Edit button");
		commonWaits.clickWithoutInterception(assetMappingEditButton);

		log.info("Click on Add Services button");
		commonWaits.clickWithoutInterception(AddServicesButton);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		// Do not wait for catalog "Add" to be clickable here — it stays disabled until at least one grid row is selected.
		Assert.assertNotNull(serviceName, "serviceName must not be null when mapping a service to the Service Action");
		String serviceToMap = serviceName.trim();
		Assert.assertFalse(serviceToMap.isEmpty(), "serviceName must not be empty when mapping a service to the Service Action");

		log.info("Enter service name in Add Services catalog search");
		WebElement catalogSearch = commonWaits.waitForByElementToBeVisible(ADD_SERVICES_CATALOG_SEARCH_INPUT);
		Assert.assertTrue(
				commonWaits.waitForElementVisible_withFluentWait(getDriver(), catalogSearch, Math.max(commonWaits.defaultTimeout, 40)),
				"Add Services catalog search did not become interactable in time");
		commonWaits.keyClearAndType(catalogSearch, serviceToMap);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Click on service in search result");
		selectServiceInAddServicesCatalogGrid(serviceToMap);

		log.info("Click on Add button");
		commonWaits.waitForElementClickable(AddServicesAddButton);
		commonWaits.clickWithoutInterception(AddServicesAddButton);

		log.info("Click on Colse Add Service button");
		commonWaits.clickWithoutInterception(CloseAddServicesButton);

		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Ensure Service Action Active toggle is on");
		setToggleState(serviceActionActiveToggleInput, true, "Active");

		log.info("Click on Close Actions button");
		commonWaits.clickWithoutInterception(CloseActionsButton);

		return this;
	}

	public DWPCDashboardPage createAssetGroup(String assetGroupName, String serviceName) {
		log.info("Navigate to Asset Groups page");
		navigateToDWPCPage("Services", "Asset Groups");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

		if (assetGroupName != null && !assetGroupName.trim().isEmpty()) {
			if (isAssetGroupPresentInGrid(assetGroupName)) {
				log.info("Asset group already present; Deleting it." + assetGroupName.trim());
				deleteAssetGroupIfPresent(assetGroupName.trim());
				
			}
		}

		commonWaits.clickWithoutInterception(newAssetGroupButton);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

		commonWaits.keyClearAndType(assetGroupNameInput, assetGroupName);
		
		commonWaits.clickWithoutInterception(NewAssetConfirmButton);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		// Catalog "Add" is disabled until a service row is selected; readiness is asserted via catalog search below.

		log.info("Click Add (Services) on Asset Group options");
		commonWaits.waitForElementClickable(commonWaits.waitForByElementToBeVisible(ASSET_SERVICE_ADD));
		commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(ASSET_SERVICE_ADD));
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

		log.info("Enter service name in Add Services catalog search");
		Assert.assertNotNull(serviceName, "serviceName must not be null when mapping a service to the asset group");
		String serviceToMap = serviceName.trim();
		Assert.assertFalse(serviceToMap.isEmpty(), "serviceName must not be empty when mapping a service to the asset group");
		WebElement catalogSearch = commonWaits.waitForByElementToBeVisible(ASSET_SERVICE_CATALOG_SEARCH_INPUT);
		Assert.assertTrue(
				commonWaits.waitForElementVisible_withFluentWait(getDriver(), catalogSearch, Math.max(commonWaits.defaultTimeout, 40)),
				"Add Services catalog search did not become interactable in time");
		commonWaits.keyClearAndType(catalogSearch, serviceToMap);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Click on service in search result");
		selectServiceInAddServicesCatalogGrid(serviceToMap);

		log.info("Click on Add button");
		commonWaits.waitForElementClickable(AddServicesAddButton);
		commonWaits.clickWithoutInterception(AddServicesAddButton);

		log.info("Click on close Add Service button");
		commonWaits.clickWithoutInterception(CloseGroupOptionButton);

		return this;
	}

	/**
	 * On Services → Asset Groups: search by group name, select the row checkbox, open Actions → Delete, and confirm the modal.
	 * If the group is not in the grid after search, the method returns without failing.
	 *
	 * @param assetGroupName value shown in the Group Name column (trimmed)
	 */
	public DWPCDashboardPage deleteAssetGroupIfPresent(String assetGroupName) {
		Assert.assertNotNull(assetGroupName, "assetGroupName must not be null");
		String name = assetGroupName.trim();
		Assert.assertFalse(name.isEmpty(), "assetGroupName must not be empty");
		
		By checkboxSpan = By.xpath(String.format(assetGroupRowCheckboxByGroupName, name));
		WebElement rowCheckbox = commonWaits.waitForByElementToBeVisible(checkboxSpan);
		javaScriptScrollIntoView(rowCheckbox);
		commonWaits.clickWithoutInterception(rowCheckbox);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

		commonWaits.waitForElementClickable(actionsDropdownButton);
		commonWaits.clickWithoutInterception(actionsDropdownButton);
		commonWaits.waitForElementClickable(deleteActionMenuItem);
		commonWaits.clickWithoutInterception(deleteActionMenuItem);

		commonWaits.waitForElementClickable(deleteAssetGroupModalConfirmButton);
		commonWaits.clickWithoutInterception(deleteAssetGroupModalConfirmButton);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		log.info("Asset group deleted (or delete confirmed) for: " + name);
		return this;
	}

	private boolean isAssetGroupPresentInGrid(String assetGroupName) {
		String trimmed = assetGroupName != null ? assetGroupName.trim() : "";
		if (trimmed.isEmpty()) {
			return false;
		}
		log.info("Checking if Asset Group already exists in list: " + trimmed);
		commonWaits.waitForElementVisible(serviceActionsListSearchBox);
		commonWaits.keyClearAndType(serviceActionsListSearchBox, trimmed);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		By rowLocator = By.xpath(String.format(assetGroupNameInGridFirstCell, trimmed));
		boolean found = commonWaits.isElementPresent(rowLocator, 10);
		log.info("Asset group row visible after search: " + found);
		return found;
	}

}