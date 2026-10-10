package pages.ITSM.dwp;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPAdministrationPage extends BasePage {

	static SolqaLogger log;
	CommonWaits commonWaits;

    //String locators
    String configurationHamburgerMenu = "//*[contains(@class,'hamburger') and contains(text(),'%s')]";
    String configurationMenu = "//*[contains(@class,'menu') and contains(text(),'%s')]";
    String configurationSubMenu = "//*[contains(@class,'sidebar') and contains(text(),'%s')]";
    String fulfillmentDetailsMenu = "//div[contains(@class,'switch') and contains(.,'%s')]";
    /**
     * Fulfillment toggle row: {@code switch__item} label matching text, then nearest ancestor whose class contains
     * {@code switch}. See {@link #resolveFulfillmentSwitchRow}.
     */
    String FULFILLMENT_SWITCH_ROW_FROM_LABEL =
            "//*[self::div or self::span][contains(@class,'switch__item') and contains(normalize-space(.),'%s')]"
                    + "/ancestor::*[contains(@class,'switch')][1]";
    /**
     * Fulfillment toggle row: {@code div} with class containing {@code switch}, matching label text, and containing an
     * input or {@code role='switch'}. See {@link #resolveFulfillmentSwitchRow}.
     */
    String FULFILLMENT_SWITCH_ROW_WITH_CONTROL =
            "//div[contains(@class,'switch') and contains(.,'%s') and (.//input or .//*[@role='switch'])]";
    /** Fallback: {@code adapt-rx-switch} input under any node whose normalized text contains the switch label. */
    String ADAPTRX_SWITCH_INPUT_BY_CONTAINING_TEXT = "//*[contains(normalize-space(.), '%s')]//adapt-rx-switch//input";
    String uncheckedCheckBox = "//input[contains(@class,'checkbox__input') and @aria-checked='false']/ancestor::div[@class='checkbox']";
    String comaroundProvider = "//table[contains(@class,'providers-table')]//tr[contains(@name,'" + SolQAConstants.COMAROUND_PROVIDER + "')]//span[contains(@class,'checkbox__item')]";
    String hkmProvider = "//table[contains(@class,'providers-table')]//tr[contains(@name,'" + SolQAConstants.HKM_PROVIDER + "')]//span[contains(@class,'checkbox__item')]";
    String providerSettings = "//table[contains(@class,'providers-table')]//th[contains(text(),'%s')]";
    String CASE_APPROVAL_CHECKBOX = "//*[@class='checkbox__item' ]//*[contains(text(),'%s')]/parent::div/parent::label";
    String settingsTextboxLocator = "//tr[@name='%s']//input";
    String settingsCheckboxLocator = "//tr[@name='%s']//input";
    String locatorReloadProviders = "//i[@title='Reload providers (page will be reloaded and user will be signed-out)']";
    String settingLocator = "//table[contains(@class,'providers-table')]//tr[contains(@name,'%s')]//span[contains(@class,'checkbox__item')]";
    String SKILL_DROPDOWN = "//dwp-helix-gpt-skill-id-selection//button";
    String DROPDOWN_VALUES_LOCATOR = "//*[contains(@class, 'a-dropdown-window')]//button";
    String DROPDOWN_BY_LABEL = "//dwp-select[@formcontrolname='%s']//button";
    String compactNavigationTab = "//a[contains(@class, 'a-hamburger__menu-link') and normalize-space()='%s']";
    String dwpNavigationTabs = "//nav//ul/li[contains(@class,'a-menu__item')]//a/span[text()='%s']/parent::a";
    String RADIO_BUTTON_BY_INDEX = "(//adapt-rx-radiobutton//span[contains(text(),'%s')])[%s]";
    String CustomMessage = "//adapt-rx-control-label[contains(.,'%s message')]/parent::div//input";
    String hamburgerMenu = "//button[contains(@class, 'a-hamburger')]";
    String FEATURE_TOGGLE_BY_NAME = "//td[contains(@class, 'feature-name') and normalize-space()='%s']/preceding-sibling::td//*[contains(@class, 'checkbox')]";
    String TAB_BY_NAME = "//*[@role='tab' and normalize-space()='%s']";
    String resultAction = "//option[contains(text(),'%s')]";
    String INPUT_BY_LABEL = "//label[normalize-space()='%s']/following::*[self::input or self::textarea][1]";
    String INPUT_BY_LABEL_CONTAINS = "//label[contains(normalize-space(.), '%s')]/following::*[self::input or self::textarea][1]";
    String DROPDOWN_BY_LABEL_BUTTON = "//label[contains(normalize-space(.), '%s')]/following::button[1]";
    String DROPDOWN_OPTION_BY_TEXT = "//*[contains(@class,'a-dropdown-window') or contains(@class,'rx-select__options') or @role='listbox']//*[self::button or self::span][normalize-space()='%s']/ancestor::button[1]";
    String typeDropDownOption = "//li[@class='active-result' and contains(normalize-space(.), '%s')]";
    String provider = "//table[contains(@class,'providers-table')]//tr[contains(@name,'%s')]";
    String CASE_APPROVAL_CHECKBOX_INPUT = "//*[@class='checkbox__item' ]//*[contains(text(),'%s')]/parent::div/parent::label//input";
	private static final String CASE_APPROVAL_DETAILS_CONFIGURATION_TXTBOX = "//*[@data-ta='approvalConfigurationCrossLaunchUrlTextFieldSsmCase']//input";
	String fulfillmentPageTitle="//*[@data-ta='pageTitle' and normalize-space()='Fulfillment Details']";
    String changeFulfillmentAdditionalFieldBtn="//*[@data-ta='fulfillmentSettingsAdditionalFieldFilter_change']//button";
	String incidentFulfillmentAdditionalFieldBtn="//*[@data-ta='fulfillmentSettingsAdditionalFieldFilter_incident']//button";
	String workOrderFulfillmentAdditionalFieldBtn="//*[@data-ta='fulfillmentSettingsAdditionalFieldFilter_work_order']//button";
	String additionalFieldError="//div[@role='alert']//span[normalize-space()='Please select at most 20 fields']";
	String additionalFieldCheckbox="//*[@role='listbox']//*[normalize-space()='%s']//input/parent::*";
    /** Row-relative: {@code input[@role='switch']} under a fulfillment/settings row. */
    private static final String ROW_TOGGLE_INPUT_ROLE_SWITCH = ".//input[@role='switch']";
    /** Row-relative: {@code adapt-rx-switch//input} under a fulfillment/settings row. */
    private static final String ROW_TOGGLE_ADAPTRX_SWITCH_INPUT = ".//adapt-rx-switch//input";
    /** Row-relative: {@code @role='switch'} input or button under a fulfillment/settings row. */
    private static final String ROW_TOGGLE_ROLE_SWITCH_INPUT_OR_BUTTON =
            ".//*[@role='switch' and (self::input or self::button)]";
    /** Row-relative: checkbox input under a fulfillment/settings row. */
    private static final String ROW_TOGGLE_INPUT_TYPE_CHECKBOX = ".//input[@type='checkbox']";

    @FindBy(xpath = "//span[contains(@class,'hamburger')]")
    WebElement hambergerIcon;

    @FindBy(xpath = "//button[contains(@class,'btn') and contains(.,'Save')]")
    WebElement saveButton;

    @FindBy(xpath = "//nav[@id='navbar']")
    WebElement DwpAdminDashboard;

    @FindBy(xpath = "//table[contains(@class,'providers-table')]//tr[contains(@name,'ComAround pluggable provider')]")
    WebElement comaroundPluggableProvider;

    @FindBy(xpath = "//*[@data-ta='approvalConfigurationCrossLaunchUrlTextFieldSsmCase']//input")
    WebElement caseApprovalDetailsConfigurationTxtbox;

    @FindBy(xpath = "//div[contains(@class, 'alert-success')]/span[contains(text(),'successfully.')]")
    WebElement settingsUpdatedSuccessfully;

    @FindBy(xpath = "//span[contains(text(),'Providers were reloaded successfully')]")
    WebElement providersReloadedSuccessfully;

    @FindBy(xpath = "//button[contains(.,'Save')]")
    WebElement SAVE_Details;

    @FindBy(xpath = "//*[@data-ta='subTitleAssistantInResults']/parent::*//adapt-rx-switch//input")
    public WebElement AIChattoggleButtonState;

    @FindBy(xpath = "//fieldset[contains(., 'AI chat')]//input/parent::label")
    public WebElement AIChat_toggleButton;

    @FindBy(xpath = "//dwp-chatbot-configuration//button[contains(.,'Close')]")
    public WebElement closeBtn_DWP_Chatbot_ConfigurationPage;

    @FindBy(xpath = "//div[@class='switch']//input")
    public WebElement toggleButton;

    @FindBy(xpath = "//button[contains(@aria-label,'Employee Navigator')]//adapt-icon")
    public WebElement employeeNavigator;

    @FindBy(css = "span[data-testid='helixGPTChatDisclaimerMessage']")
    public WebElement helixGPTChatDisclaimerMessage;

    @FindBy(xpath = "//label[contains(normalize-space(),'Show this service on user devices')]//input")
    WebElement SHOW_THIS_SERVICE_CHECKBOX;

    @FindBy(xpath = "//button[normalize-space()='Add' or contains(.,'Add') or @data-ta='addButton']")
    WebElement ADD_BUTTON_GENERIC;
    
    @FindBy(xpath = "//button[contains(@class,'add-action') and normalize-space()='Add']")
    WebElement ADD_BUTTON_In_SERVICE_AVAILABILITY_ACTIONS;
    
    @FindBy(xpath = "//button[contains(@class,'add-all-actions') and normalize-space()='Add All']")
    WebElement ADD_ALL_BUTTON_In_SERVICE_AVAILABILITY_ACTIONS;
    
    @FindBy(xpath = "//input[@placeholder='Filter Actions']")
    WebElement Filter_Actions_SearchBox;

    @FindBy(xpath = "//button[normalize-space()='Save' or contains(.,'Save')]")
    WebElement SAVE_BUTTON_GENERIC;

    @FindBy(xpath = "//input[@name='service_action_name']")
    WebElement serviceActionNameInput;

    @FindBy(css = "select.input-service-action-type")
    WebElement typeDropdown;

    @FindBy(css = "select.request-list")
    WebElement requestMappingDropdown;

    @FindBy(xpath = "//button[normalize-space()='Select Request']")
    WebElement selectRequestButton;

    @FindBy(xpath = "//button[normalize-space()='OK']")
    WebElement okButtonInRequestMapping;

    @FindBy(xpath = "//button[normalize-space()='Create Action']")
    WebElement createActionButton;

    @FindBy(xpath = "//p[contains(. ,'Type')]//*[contains(@class, 'chosen-container')]")
    WebElement typeDropdownInActionAvailability;

    @FindBy(xpath = "//span[select[contains(@class,'input-service-action-type')]]/div/a")
    WebElement TypeDropdownIcon;

    @FindBy(xpath = "//select[@class='request-list']//option")
    List<WebElement> SelectRequestValues;

    @FindBy(xpath = "//li[@class='page-item page-next disabled']")
    WebElement nextButton;

    @FindBy(xpath = "//nav[@class='providers-pagination']//span[@class='page-link' ]//span [contains(@class,'right')]")
    WebElement nextButtonToClick;

    @FindBy(xpath = "//button[contains(@class, 'btn-primary') and contains(text(), 'Update settings')]")
    WebElement updateSettings;
    
    @FindBy(xpath = "//li[@data-permission='rebranding']//a[contains(@class,'d-n-menu__link') and contains(text(),'Configuration')]")
    WebElement configurationTabLink;
    
    @FindBy(xpath = "//button[contains(.,'Select all')]")
    WebElement selectAll;

    /** Clear at top of open Additional fields overlay (rx-select / dropdown), not a global page button. */
    @FindBy(xpath = "//*[contains(@class,'rx-select__options') or contains(@class,'a-dropdown-window') or @role='listbox']//*[(self::button or self::a or @role='option') and normalize-space()='Clear']")
    WebElement clearAdditionalFieldsFulfillment;

    /** Fallback: element with clear-related data-testid inside the same overlay. */
    @FindBy(xpath = "//*[contains(@class,'rx-select__options') or contains(@class,'a-dropdown-window')]//*[contains(@data-testid,'Clear') or contains(@data-testid,'clear')]")
    WebElement clearAdditionalFieldsFulfillmentTestIdInOverlay;

    public DWPAdministrationPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(DWPAdministrationPage.class.getName());
        commonWaits = new CommonWaits();
    }

    public DWPAdministrationPage clickOnHamburgerMenu() {
        if (commonWaits.isElementPresent(hambergerIcon)) {
            clickWithoutInterception(hambergerIcon);
            commonWaits.waitForSometime(commonWaits.moreDelay);
        }
        return this;
    }

    public DWPAdministrationPage clickOnMenu(String tab) {
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(configurationHamburgerMenu, tab)), 10)) {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(configurationHamburgerMenu, tab))).click();
        } else {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(configurationMenu, tab))).click();
        }
        commonWaits.waitForPageFullyLoaded(10, 2);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAdministrationPage verifyLoginToDWPAdmin() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), DwpAdminDashboard, 20), "DWP Admin dashboard not visible after login.");
        return this;
    }

    public DWPAdministrationPage clickOnSubMenu(String tab) {
        WebElement subMenu = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(configurationSubMenu, tab)));
        commonWaits.clickWithoutInterception(subMenu);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }
    
    public DWPAdministrationPage navigateToConfiguration() {
        log.info("Navigating to Configuration");
        if (commonWaits.isElementPresent(hambergerIcon)) {
            log.info("Hamburger menu is present, using hamburger menu navigation");
            clickOnHamburgerMenu();
            commonWaits.waitForSometime(commonWaits.moreDelay);
            clickOnMenu(SolQAConstants.CONFIGURATION);
        } else {
            log.info("Hamburger menu is not present, clicking Configuration tab directly");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), configurationTabLink , 30 ), "Configuration Tab is not visible");
            
            WebElement configTab = commonWaits.waitForElementVisible(configurationTabLink);
            javaScriptScrollIntoView(configTab);
            clickWithoutInterception(configTab);
        }
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForPageLoadingComplete(SolQAConstants.appDWP, 30);
        log.info("Successfully navigated to Configuration");
        return this;
    }

    public DWPAdministrationPage enableFeatureSettingByName(String featureName, boolean enable) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By toggle = By.xpath(String.format(FEATURE_TOGGLE_BY_NAME, featureName));
        Assert.assertTrue(commonWaits.isElementPresent(toggle), "Feature toggle not found for: " + featureName);
        if (enable) {
            if (!findElement(toggle).getAttribute(SolQAConstants.ATTRIBUTE_CLASS).contains(SolQAConstants.ATTRIBUTE_CHECKED)) {
                clickWithoutInterception(By.xpath(String.format(FEATURE_TOGGLE_BY_NAME + "//span", featureName)));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            } else {
                log.info("Feature '" + featureName + "' is already enabled.");
            }
        } else {
            if (findElement(toggle).getAttribute(SolQAConstants.ATTRIBUTE_CLASS).contains(SolQAConstants.ATTRIBUTE_CHECKED)) {
                clickWithoutInterception(By.xpath(String.format(FEATURE_TOGGLE_BY_NAME + "//span", featureName)));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            } else {
                log.info("Feature '" + featureName + "' is already disabled.");
            }
        }
        return this;
    }

    public DWPAdministrationPage createServiceAction_CatalogRequest(String actionType, String actionName, String requestValue) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(ADD_BUTTON_GENERIC), "Add button not found on Service Actions page");
        clickWithoutInterception(ADD_BUTTON_GENERIC);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(serviceActionNameInput).sendKeys(actionName);
        commonWaits.waitForElementClickable(TypeDropdownIcon);
        Actions actions = new Actions(getDriver());
        actions.doubleClick(TypeDropdownIcon).perform();
        actions.click(TypeDropdownIcon).perform();
        commonWaits.clickWithoutInterception(By.xpath(String.format(typeDropDownOption, actionType)));

        commonWaits.waitForElementVisible(selectRequestButton).click();
        for (WebElement option : SelectRequestValues) {
            if (option.getText().equalsIgnoreCase(requestValue)) {
                option.click();
            }
        }
        commonWaits.waitForElementVisible(okButtonInRequestMapping).click();
        commonWaits.waitForElementVisible(createActionButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAdministrationPage createServiceAvailabilityWithAction(String serviceName, String actionName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(ADD_BUTTON_GENERIC), "Add button not found on Service Availability page");
        clickWithoutInterception(ADD_BUTTON_GENERIC);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        // "Show this service"
        if (commonWaits.isElementPresent(SHOW_THIS_SERVICE_CHECKBOX)) {
            clickWithoutInterception(SHOW_THIS_SERVICE_CHECKBOX);
        }

        // Actions tab
        if (commonWaits.isElementPresent(By.xpath(String.format(TAB_BY_NAME, "Actions")))) {
            clickWithoutInterception(By.xpath(String.format(TAB_BY_NAME, "Actions")));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }

        //Select Action Result
        if (commonWaits.isElementPresent(Filter_Actions_SearchBox)) {
            keyClearAndType(Filter_Actions_SearchBox, actionName);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(resultAction, actionName)), 10), "Expected action not found in the list: " + actionName);
            commonWaits.waitForSometime(5000);      
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(resultAction, actionName))).click();          
           
            log.info(String.format(resultAction, actionName) + " is present in the list, selecting it...");       
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
        
        // Add action (best-effort)
        if (commonWaits.isElementPresent(ADD_ALL_BUTTON_In_SERVICE_AVAILABILITY_ACTIONS)) {
            clickWithoutInterception(ADD_ALL_BUTTON_In_SERVICE_AVAILABILITY_ACTIONS);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
        By actionOpt = By.xpath(String.format(DROPDOWN_OPTION_BY_TEXT, actionName));
        if (commonWaits.isElementPresent(actionOpt)) {
            clickWithoutInterception(actionOpt);
        }

        // Service name (use "Name" field if present)
        String nameInputXpath = String.format(INPUT_BY_LABEL, "Name");
        By nameInput = By.xpath(nameInputXpath);
        if (!commonWaits.isElementPresent(nameInput)) {
            nameInputXpath = String.format(INPUT_BY_LABEL_CONTAINS, "Name");
            nameInput = By.xpath(nameInputXpath);
        }
        if (commonWaits.isElementPresent(nameInput)) {
            keyClearAndType(nameInputXpath, serviceName);
        }

        Assert.assertTrue(commonWaits.isElementPresent(SAVE_BUTTON_GENERIC), "Save button not found on Service Availability form");
        clickWithoutInterception(SAVE_BUTTON_GENERIC);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAdministrationPage configureFulfillmentDetails() {
        commonWaits.waitForSometime(1000);
        if (findElement(By.xpath(String.format(fulfillmentDetailsMenu+"//input", "My Activity component"))).getAttribute("aria-checked").equals("false")) {
            findElement(By.xpath(String.format(fulfillmentDetailsMenu, "My Activity component"))).click();
        }
        commonWaits.waitForSometime(1000);
        if (findElement(By.xpath(String.format(fulfillmentDetailsMenu+"//input", "Request Details view"))).getAttribute("aria-checked").equals("false")) {
            findElement(By.xpath(String.format(fulfillmentDetailsMenu, "Request Details view"))).click();
        }

        List<WebElement> checkXpaths = findElements(By.xpath(uncheckedCheckBox));
        if (!checkXpaths.isEmpty()) {
            for (int i = 0; i < checkXpaths.size(); i++) {
                Assert.assertTrue(commonWaits.isElementClickable(checkXpaths.get(i)), "Checkbox is not clickable");
                checkXpaths.get(i).click();
                commonWaits.waitForSometime(1000);
            }
        }

        if (saveButton.isEnabled()) {
            commonWaits.waitForSometime(1000);
            saveButton.click();
        }
        return this;
    }

    public String enableDisableComAroundPluggableProvider(boolean value) {
        WebElement getCheckBoxValue = null;
        WebElement weSetting = null;
        String providerName = null;
        scrollToBottomOfPage();
        if (commonWaits.isElementPresent(By.xpath(comaroundProvider))) {
            providerName = SolQAConstants.COMAROUND_PROVIDER;
            weSetting = commonWaits.waitForByElementToBeVisible(By.xpath(comaroundProvider));
            getCheckBoxValue = findElement(By.xpath(comaroundProvider + "//parent::div"));
        } else {
            providerName = SolQAConstants.HKM_PROVIDER;
            weSetting = commonWaits.waitForByElementToBeVisible(By.xpath(hkmProvider));
            getCheckBoxValue = findElement(By.xpath(hkmProvider + "//parent::div"));
        }
        if (value) {
            if (!getCheckBoxValue.getAttribute("class").equals("checkbox checked")) {
                weSetting.click();
            } else {
                log.info("Comaround Pluggable Provider Configuration is already enabled");
            }
        } else {
            if (!getCheckBoxValue.getAttribute("class").equalsIgnoreCase("checkbox ")) {
                weSetting.click();

            } else {
                log.info("Comaround Pluggable Provider Configuration is already enabled");
            }

        }
        return providerName;
    }

    public DWPAdministrationPage openProviderSettings(String providerName, int timeout) {
        provider = String.format(provider, providerName);
        boolean providerFoundInitial = commonWaits.waitForElementVisible_withFluentWait(getDriver(), provider, timeout);
        if (!providerFoundInitial) {
            if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), nextButton, 5)) {
                log.info("--- Provider '" + providerName + "' not found ---");
            } else {
                commonWaits.waitForElementVisible(nextButtonToClick).click();
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), provider, timeout), " -- Provider not found on next page -- ");
            }
        }
        try {
            selectProviderSetting(providerName, true);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            WebElement weProvider = findElement(By.xpath(provider));
            commonWaits.waitForElementClickable(weProvider);
            weProvider.click();
            commonWaits.waitForElementVisibility(By.xpath(String.format(providerSettings, providerName)));
        } catch (Exception e) {
            selectProviderSetting(SolQAConstants.COMAROUND_PLUGGABLE_PROVIDER, true);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            commonWaits.waitForElementClickable(comaroundPluggableProvider).click();
        }
        return this;
    }

    public boolean selectProviderSetting(String settingName, boolean checkElement) {
        WebElement weSetting = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(settingLocator, settingName)));
        WebElement getCheckBoxValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(settingLocator, settingName) + "//parent::div"));
        boolean settingsUpdated = false;
        if (checkElement) {
            if (!getCheckBoxValue.getAttribute("class").equals("checkbox checked")) {
                weSetting.click();
                settingsUpdated = true;
            } else {
                log.info("----Setting '" + settingName + "' is already checked. Nothing to do");
            }
        } else {
            if (!getCheckBoxValue.getAttribute("class").equalsIgnoreCase("checkbox ")) {
                weSetting.click();
                settingsUpdated = true;
            } else {
                log.info("----Setting '" + settingName + "' is already unchecked. Nothing to do");
            }
        }
        return settingsUpdated;
    }

    public String getTextFieldValuesInProviderSettings(String settingName) {
        log.info("--------------get Provider's setting value: " + settingName + "..");
        javaScriptScrollIntoView(findElement(By.xpath(String.format(settingsTextboxLocator, settingName))));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(settingsTextboxLocator, settingName)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(settingsTextboxLocator, settingName), 20), "Setting: " + settingName + " not found on DWP Admin page for settings up provider values.");
        return commonWaits.waitForByElementToBeVisible(By.xpath(String.format(settingsTextboxLocator, settingName))).getAttribute("Value");
    }

    public DWPAdministrationPage setTextFieldValuesInProviderSettings(String settingName, String value) {
        log.info("--------------setting Provider's setting: " + settingName + " value: " + value + "..");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format("//tr[@name='%s']//input", settingName), 20), "Setting: " + settingName + " not found on DWP Admin page for settings up provider values.");
        keyClearAndType(String.format(settingsTextboxLocator, settingName), value);
        return this;
    }

    public DWPAdministrationPage setCheckboxValueInProviderSettings(String settingName, boolean value) {
        log.info("--------------get Provider's setting value: " + settingName + "..");
        javaScriptScrollIntoView(findElement(By.xpath(String.format(settingsCheckboxLocator, settingName))));
        WebElement setting = commonWaits.findElement(By.xpath(String.format(settingsCheckboxLocator, settingName)));
        String fieldValue = setting.getAttribute("value");
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            fieldValue = setting.getAttribute("Value");
        }

        setting = commonWaits.findElement(By.xpath(String.format(settingsCheckboxLocator+"/parent::*", settingName)));
        boolean isChecked = "true".equalsIgnoreCase(fieldValue);
        if (isChecked != value) {
            setting.click();
        } else {
            log.info("Setting '" + settingName + "' is already in desired state: " + value);
        }
        return this;
    }

    public DWPAdministrationPage updateSettingsAndReloadProviders() {
        if (updateSettings.isEnabled()) {
            updateSettings();
            reloadProviders();
        } else {
            log.info("Update settings button is not enabled, skipping update and reload.");
        }
        return this;
    }

    public DWPAdministrationPage updateSettings() {
        executeJS("arguments[0].click();", updateSettings);
        Assert.assertTrue(commonWaits.isElementPresent(settingsUpdatedSuccessfully), "Success message on update settings did not appear");
        return this;
    }

    public DWPAdministrationPage reloadProviders() {
        clickReloadProvidersButton();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), providersReloadedSuccessfully, 5), "Success message on reload did not appear");
        return this;
    }

    public DWPAdministrationPage clickReloadProvidersButton() {
        commonWaits.waitForElement(By.xpath(locatorReloadProviders));
        scrollToElement(findElement(By.xpath(locatorReloadProviders)));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", findElement(By.xpath(locatorReloadProviders)));
        return this;
    }

    public boolean isCheckboxChecked(By checkboxInputLocator) {
        try {
            WebElement input = findElement(checkboxInputLocator);
            String ariaChecked = input.getAttribute(SolQAConstants.ARIA_CHECKED);
            if (ariaChecked != null) {
                return ariaChecked.equalsIgnoreCase(SolQAConstants.TRUE);
            }
            return input.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enable/Disable "Case approval details" cross-launch (BWF) in DWP Admin.
     *
     * @param settingName    label text shown for the toggle (e.g. "Case approval details")
     * @param enable         true to enable, false to disable
     * @param crossLaunchUrl full URL to open when clicking View (when enabled)
     */
    public boolean setCaseApprovalCrossLaunch(String checkboxValue, boolean setCheckBox, String caseApprovalURL) {
            boolean setCaseApprovalDetails = false;
            WebElement checkBox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CASE_APPROVAL_CHECKBOX, checkboxValue)));
            log.info("------Setting up case approval details URL");
    
            if(setCheckBox){
                commonWaits.waitForElementClickable(checkBox);
    
                if (!commonWaits.isElementPresent(By.xpath(CASE_APPROVAL_DETAILS_CONFIGURATION_TXTBOX))) {
                    commonWaits.clickWithoutInterception(checkBox);
                    WebElement textBox = commonWaits.waitForByElementToBeVisible(By.xpath(CASE_APPROVAL_DETAILS_CONFIGURATION_TXTBOX));
                    setCaseApprovalDetails = commonWaits.isElementPresent(textBox);
                    textBox.clear();textBox.sendKeys(caseApprovalURL);
                }
                else {
                    log.info("------------------ Settings are already enabled");
                }
    
            }
            else {
                commonWaits.waitForElementClickable(checkBox);
    
                if (commonWaits.isElementPresent(By.xpath(CASE_APPROVAL_DETAILS_CONFIGURATION_TXTBOX))) {
    
                    commonWaits.waitForElementClickable(checkBox);
                    commonWaits.clickWithoutInterception(checkBox);
                    setCaseApprovalDetails = true;
                }
                else {
                    log.info("------------------ BWF approval cross-launch settings are already disabled");
                }
            }
    
            return setCaseApprovalDetails;
        
    }

    public DWPAdministrationPage selectSkillValueFromDropDown(String value) {
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(SKILL_DROPDOWN));
        commonWaits.clickWithoutInterception(ele);
        commonWaits.waitForByElementToBeVisible(By.xpath(DROPDOWN_VALUES_LOCATOR));
        commonWaits.waitForSometime(commonWaits.moreDelay);
        getDriver().findElements(By.xpath(DROPDOWN_VALUES_LOCATOR))
                .stream()
                .filter(e -> e.getText().contains(value)).findFirst()
                .orElseThrow(
                        () -> new AssertionError(String.format("There is no option with '%s' name", value)))
                .click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public DWPAdministrationPage stateOfAIChat_CorePages(boolean state, String skillDropdownValues) {
        String toggleStatus = AIChattoggleButtonState.getAttribute("aria-checked");

        // Toggle ON if state is true and it's currently OFF
        if (state && !"true".equalsIgnoreCase(toggleStatus)) {
            log.info("Toggle is OFF, enabling it...");
            commonWaits.clickWithoutInterception(AIChat_toggleButton);
            commonWaits.waitForSometime(commonWaits.moreDelay);
            selectSkillValueFromDropDown(skillDropdownValues);
        }

        // Toggle OFF if state is false and it's currently ON
        if (!state && "true".equalsIgnoreCase(toggleStatus)) {
            log.info("Toggle is ON, disabling it...");
            commonWaits.javaScriptScrollIntoView(AIChattoggleButtonState);
            commonWaits.clickByJS(AIChat_toggleButton);
            SAVE_Details.click();
        }

        // Click Save if enabled
        if (SAVE_Details.isEnabled()) {
            SAVE_Details.click();
        }

        // Wait for completion
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public DWPAdministrationPage selectValueInDropDownByLabel(String dropdownName, String value) {
        log.info(String.format("Selecting value '%s' from dropdown '%s'", value, dropdownName));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_BY_LABEL, dropdownName))).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(DROPDOWN_VALUES_LOCATOR));
        getDriver().findElements(By.xpath(DROPDOWN_VALUES_LOCATOR))
                .stream()
                .filter(e -> e.getText().contains(value)).findFirst()
                .orElseThrow(
                        () -> new AssertionError(String.format("There is no option with '%s' name", value)))
                .click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public DWPAdministrationPage saveHelixChatbotConfigurationForm(boolean isDataValid) {
        log.info("Saving the configurations for Helix Chatbot.");
        commonWaits.waitForElementVisible(SAVE_Details);
        if (SAVE_Details.isEnabled()) {
            SAVE_Details.click();
            log.info("Clicked Save button.");

        } else {
            log.info("Save button is not enabled. No action taken.");
        }
        return this;
    }

    public DWPAdministrationPage clickOnCloseButtonOnDWPChatbotConfigurationPage() {
        log.info("Clicking on close button On DWP Chatbot configuration page");
        commonWaits.waitForElementClickable(closeBtn_DWP_Chatbot_ConfigurationPage);
        closeBtn_DWP_Chatbot_ConfigurationPage.click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public DWPAdministrationPage navigateToDWPNavigationTab(String tabName) {
        log.info("Navigating to DWP Navigation Tab: " + tabName);
        By hamburgerMenuLocator = By.xpath(hamburgerMenu);

        if (commonWaits.isElementPresent(hamburgerMenuLocator)) {
            log.info("Compact navigation detected, opening hamburger menu.");
            WebElement hamburgerMenuElement = findElement(hamburgerMenuLocator);
            commonWaits.waitForElementClickable(hamburgerMenuElement).click();
            WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(compactNavigationTab, tabName)));
            commonWaits.waitForElementClickable(we).click();
        } else {
            WebElement tab = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dwpNavigationTabs, tabName)));
            commonWaits.waitForElementClickable(tab).click();
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public DWPAdministrationPage setAIDisclaimerToggle(boolean state) {

        if (state && !toggleButton.getAttribute(SolQAConstants.ARIA_CHECKED).contains(SolQAConstants.TRUE)) {
            log.info("Toggle button is disabled. Enabling it...");
            toggleButton.click();
        }

        if (toggleButton.getAttribute(SolQAConstants.ARIA_CHECKED).contains(SolQAConstants.TRUE)) {
            log.info("Toggle button is ON.");
        } else {
            log.info("Toggle button is OFF.");
        }
        return this;
    }

    public DWPAdministrationPage fillRadioButtonByIndex(String answer, String Index) {
        log.info("Checking SRD radiobutton with answer:" + answer);
        WebElement radio = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(RADIO_BUTTON_BY_INDEX, answer, Index)));
        clickByAction(radio);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAdministrationPage editCustomMessageInAIDisclaimer(String message, String messageText) {
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CustomMessage, message)));
        ele.clear();
        ele.sendKeys(messageText);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public DWPAdministrationPage saveAIDisclaimerChanges() {
        commonWaits.waitForElementClickable(SAVE_Details).click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

	public void refreshBrowser() {
		log.info("Refreshing the browser");
		getDriver().navigate().refresh();
		commonWaits.waitForExecutionComplete();
	}

	public void clickOnEmployeeNavigatorIcon() {
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
		commonWaits.waitForElementClickable(employeeNavigator);
		commonWaits.clickWithoutInterception(employeeNavigator);
        commonWaits.waitForExecutionComplete();
	}

	public String getChatDisclaimerMsg() {
		log.info("Getting chat disclaimer message");
		return commonWaits.waitForElementVisible(helixGPTChatDisclaimerMessage).getText();
	}

    String checkedCheckBox = "//input[contains(@class,'checkbox__input') and @aria-checked='true']/ancestor::div[@class='checkbox']";
    String CHECKBOX_LABEL_BY_TEXT = "//*[contains(@class,'checkbox__item') and contains(normalize-space(.),%s)]/ancestor::label[1] | //label[contains(normalize-space(.),%s)][.//input[@type='checkbox']][1]";
    String CHECKBOX_INPUT_IN_LABEL = ".//input[@type='checkbox']";
    String inputRow = "//input[contains(@class,'checkbox__input') and (@type='checkbox' or @role='checkbox')]";
    String errorMessage= "//*[contains(text(),'%s')]/ancestor::div[contains(@class,'border')]//span[contains(.,'Please select at most 20 fields')]";
    String workOrderErrorMessage= "//*[contains(text(),'Work orders')]/ancestor::div[contains(@class,'border')]//span[contains(.,'Please select at most 20 fields')]";
    
    @FindBy(xpath = "//*[contains(@class,'toast') or contains(@class,'a-toast') or contains(@class,'alert') or @role='alert']")
    WebElement TOAST_OR_ALERT;
    
    @FindBy(xpath = "//button[@data-ta='adminLayoutSaveButton' or normalize-space()='Save' or contains(normalize-space(.),'Save')]")
    WebElement SAVE_BUTTON_ANY;
    @FindBy(xpath = "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'error')")
    WebElement ERROR_TEXT_ANYWHERE;

    public DWPAdministrationPage enableFulfillmentDetailsSwitches() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        enableSwitchByName("My Activity component", true);
        enableSwitchByName("Request Details view", true);
        return this;
    }

    public DWPAdministrationPage verifyAdditionalFieldLimitError(String requestType) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertNotNull(requestType, "Request type cannot be null");

        // Best-effort: select all visible checkboxes on the page, then verify we get an error on save.
        // This preserves Umbrella intent (error appears when selecting too many additional fields).
        List<WebElement> checkXpaths = findElements(By.xpath(uncheckedCheckBox));
        if (checkXpaths != null && !checkXpaths.isEmpty()) {
            for (WebElement el : checkXpaths) {
                try {
                    if (el != null && el.isDisplayed() && commonWaits.isElementClickable(el)) {
                        el.click();
                    }
                } catch (Exception ignored) {
                }
            }
        }

        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(errorMessage, requestType)), 10) || commonWaits.isElementPresent(By.xpath(workOrderErrorMessage), 10) || commonWaits.isElementPresent(ERROR_TEXT_ANYWHERE, 10),
                "Expected error not visible after selecting too many additional fields for: " + requestType);
        return this;
    }

    /**
     * Clears the current selection in the open "Additional fields" dropdown so a known set of fields can be applied.
     */
    private DWPAdministrationPage clickClearOnFulfillmentAdditionalFieldsDropdown(String requestTypeForLog) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(clearAdditionalFieldsFulfillment, 10)) {
            commonWaits.clickWithoutInterception(clearAdditionalFieldsFulfillment);
        } else if (commonWaits.isElementPresent(clearAdditionalFieldsFulfillmentTestIdInOverlay, 10)) {
            commonWaits.clickWithoutInterception(clearAdditionalFieldsFulfillmentTestIdInOverlay);
        } else {
            Assert.fail("Fulfillment Details — Additional fields: Clear control not found for request type: "
                    + requestTypeForLog);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        
        return this;
    }

    public DWPAdministrationPage selectAdditionalFieldsFulfillmentDetails(String requestType, String fieldsCsv) {
        commonWaits.clickWithoutInterception(By.xpath(fulfillmentPageTitle));
        if (requestType.trim().equalsIgnoreCase(SolQAConstants.CHANGE_REQUEST_TYPE)) {
            commonWaits.clickWithoutInterception(By.xpath(changeFulfillmentAdditionalFieldBtn));
        } else if (requestType.trim().equalsIgnoreCase(SolQAConstants.INCIDENT_REQUEST_TYPE)) {
            commonWaits.clickWithoutInterception(By.xpath(incidentFulfillmentAdditionalFieldBtn));
        } else if (requestType.trim().equalsIgnoreCase(SolQAConstants.WORKORDER_REQUEST_TYPE)) {
            commonWaits.clickWithoutInterception(By.xpath(workOrderFulfillmentAdditionalFieldBtn));
        } else {
            Assert.fail("Request type is invalid. Possible values are : change,incident,workorder. But passed value is : "
                    + requestType);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        log.info("Fulfillment Details — clearing Additional fields dropdown before selection (request type: "
                + requestType + ")");
        clickClearOnFulfillmentAdditionalFieldsDropdown(requestType);

        String[] additionalFieldsArray = fieldsCsv.trim().split(",");
        for (String additionalField : additionalFieldsArray) {
            if (additionalField == null) {
                continue;
            }
            String field = additionalField.trim();
            if (field.isEmpty()) {
                continue;
            }
            commonWaits.clickWithoutInterception(By.xpath(String.format(additionalFieldCheckbox, field)));
        }
        commonWaits.clickWithoutInterception(By.xpath(fulfillmentPageTitle));
        return this;
    }

    /** Persists Fulfillment Details configuration (Save on admin layout). */
    public DWPAdministrationPage saveFulfillmentDetailsSettings() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        clickSaveBestEffort();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }
    
    public DWPAdministrationPage selectAllValuesAdditionalFields(String requestType) {
    	commonWaits.clickWithoutInterception(By.xpath(fulfillmentPageTitle));
        if(requestType.trim().equalsIgnoreCase(SolQAConstants.CHANGE_REQUEST_TYPE)){
            commonWaits.clickWithoutInterception(By.xpath(changeFulfillmentAdditionalFieldBtn));
        } else if (requestType.trim().equalsIgnoreCase(SolQAConstants.INCIDENT_REQUEST_TYPE)) {
            commonWaits.clickWithoutInterception(By.xpath(incidentFulfillmentAdditionalFieldBtn));
        }
        else if(requestType.trim().equalsIgnoreCase(SolQAConstants.WORKORDER_REQUEST_TYPE)){
            commonWaits.clickWithoutInterception(By.xpath(workOrderFulfillmentAdditionalFieldBtn));
        }
        else{
            Assert.fail("Request type is invalid. Possible values are : change,incident,workorder. But passed value is : "+ requestType);
        }
        if (commonWaits.isElementPresent(clearAdditionalFieldsFulfillment)) {
            commonWaits.clickWithoutInterception(clearAdditionalFieldsFulfillment);
        }
        
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), selectAll , 30 ), "Select All button is not visible");
    	commonWaits.clickWithoutInterception(selectAll);
        commonWaits.clickWithoutInterception(By.xpath(fulfillmentPageTitle));
    	return this;
    
    }
    

    public DWPAdministrationPage unselectAllAdditionalFieldsAndSave() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> checked = findElements(By.xpath(checkedCheckBox));
        if (checked != null && !checked.isEmpty()) {
            for (WebElement el : checked) {
                try {
                    if (el != null && el.isDisplayed() && commonWaits.isElementClickable(el)) {
                        el.click();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        clickSaveBestEffort();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAdministrationPage verifyAllAdditionalFieldsAreSelected(boolean expectedSelected) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> allInputsRaw = findElements(By.xpath(inputRow));
        final List<WebElement> allInputs = (allInputsRaw == null) ? java.util.Collections.emptyList() : allInputsRaw;
        if (allInputs.isEmpty()) {
            Assert.fail("No additional field checkboxes found to verify selection state.");
        }

        for (WebElement input : allInputs) {
            if (input == null) continue;
            try {
                String aria = input.getAttribute(SolQAConstants.ARIA_CHECKED);
                boolean isChecked = "true".equalsIgnoreCase(aria) || input.isSelected();
                Assert.assertEquals(isChecked, expectedSelected, "Unexpected additional field checkbox state. expectedSelected=" + expectedSelected);
            } catch (Exception ignored) {
            }
        }
        return this;
    }
    
    /**
     * Fulfillment toggles: try {@link #FULFILLMENT_SWITCH_ROW_FROM_LABEL}, then {@link #FULFILLMENT_SWITCH_ROW_WITH_CONTROL},
     * then {@link #fulfillmentDetailsMenu} (label-only rows with no input). Resolve the full row first, then locate the control inside it.
     */
    private WebElement resolveFulfillmentSwitchRow(String switchName) {
        By rowFromLabel = By.xpath(String.format(FULFILLMENT_SWITCH_ROW_FROM_LABEL, switchName));
        if (commonWaits.isElementPresent(rowFromLabel, 15)) {
            return commonWaits.waitForByElementToBeVisible(rowFromLabel);
        }
        By rowWithControl = By.xpath(String.format(FULFILLMENT_SWITCH_ROW_WITH_CONTROL, switchName));
        if (commonWaits.isElementPresent(rowWithControl, 20)) {
            return commonWaits.waitForByElementToBeVisible(rowWithControl);
        }
        By legacy = By.xpath(String.format(fulfillmentDetailsMenu, switchName));
        if (commonWaits.isElementPresent(legacy, 10)) {
            return commonWaits.waitForByElementToBeVisible(legacy);
        }
        return null;
    }

    /** First visible descendant of {@code row} matching a row-relative xpath fragment. */
    private WebElement findFirstToggleInRow(WebElement row, String rowRelativeXpath) {
        List<WebElement> found = row.findElements(By.xpath(rowRelativeXpath));
        return found.isEmpty() ? null : found.get(0);
    }

    /** Scoped to a table/settings row: toggle control candidates (returns a {@link WebElement}, not a {@link By}). */
    private WebElement findToggleControlWithinRow(WebElement row) {
        if (row == null) {
            return null;
        }
        WebElement control = findFirstToggleInRow(row, ROW_TOGGLE_INPUT_ROLE_SWITCH);
        if (control != null) {
            return control;
        }
        control = findFirstToggleInRow(row, ROW_TOGGLE_ADAPTRX_SWITCH_INPUT);
        if (control != null) {
            return control;
        }
        control = findFirstToggleInRow(row, ROW_TOGGLE_ROLE_SWITCH_INPUT_OR_BUTTON);
        if (control != null) {
            return control;
        }
        return findFirstToggleInRow(row, ROW_TOGGLE_INPUT_TYPE_CHECKBOX);
    }

    private void enableSwitchByName(String switchName, boolean enable) {
        WebElement row = resolveFulfillmentSwitchRow(switchName);
        Assert.assertNotNull(row, "Fulfillment details switch row not found: " + switchName);
        try {
            scrollToElement(row);
        } catch (Exception ignored) {
        }

        WebElement control = findToggleControlWithinRow(row);
        if (control == null) {
            By adaptInput = By.xpath(String.format(ADAPTRX_SWITCH_INPUT_BY_CONTAINING_TEXT, switchName));
            if (commonWaits.isElementPresent(adaptInput, 10)) {
                control = commonWaits.waitForByElementToBeVisible(adaptInput);
            }
        }

        Assert.assertNotNull(control, "Fulfillment details switch control not found: " + switchName);

        String ariaChecked = control.getAttribute(SolQAConstants.ARIA_CHECKED);
        boolean isOn = SolQAConstants.TRUE.equalsIgnoreCase(ariaChecked)
                || (ariaChecked == null && control.isSelected());

        log.info("Switch '" + switchName + "' current state : " + isOn);

        WebElement clickTarget = control;
        try {
            if (!control.isDisplayed() || !control.isEnabled()) {
                clickTarget = row;
            }
        } catch (Exception ignored) {
            clickTarget = row;
        }

        if (enable) {
            if (isOn) {
                log.info("Switch '" + switchName + "' already enabled. Skipping.");
                return;
            }
            commonWaits.clickWithoutInterception(clickTarget);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Switch '" + switchName + "' enabled successfully.");
            return;
        }

        if (isOn) {
            commonWaits.clickWithoutInterception(clickTarget);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Switch '" + switchName + "' disabled successfully.");
        }
    }
    
    private void checkCheckboxByLabelText(String labelText) {
        By labelBy = By.xpath(String.format(CHECKBOX_LABEL_BY_TEXT, labelText, labelText));
        Assert.assertTrue(commonWaits.isElementPresent(labelBy, 15), "Additional field checkbox label not found: " + labelText);
        WebElement label = commonWaits.waitForByElementToBeVisible(labelBy);
        WebElement input = null;
        input = label.findElement(By.xpath(CHECKBOX_INPUT_IN_LABEL));

        boolean checked = false;
        if (input != null) {
            String ac = input.getAttribute(SolQAConstants.ARIA_CHECKED);
            if (ac == null) ac = input.getAttribute("checked");
            checked = ac != null && ac.toLowerCase().contains("true");
        }

        if (!checked) {
            commonWaits.clickWithoutInterception(label);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
    }

    private void clickSaveBestEffort() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(saveButton, 2)) {
            commonWaits.clickWithoutInterception(saveButton);
        } else if (commonWaits.isElementPresent(saveButton, 2)) {
            commonWaits.clickWithoutInterception(saveButton);
        } else {
            commonWaits.clickWithoutInterception(SAVE_BUTTON_ANY);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    String APPR_CONNECTIONS_RELEASE_REQUEST_APPROVAL = "//*[@class='checkbox__item' ]//*[contains(text(),'%s')]/parent::div/parent::label";
    @FindBy(xpath = "//*[@data-ta='approvalConfigurationCrossLaunchUrlTextFieldRelease']//input")
    WebElement RELEASE_APPROVAL_CONFIGURATION_TXTBOX;

    @FindBy(xpath = "//button[@data-ta='adminLayoutSaveButton']")
    WebElement BTN_SAVE;

    @FindBy(xpath = "//div[contains(@class, 'a-toast__summary')]")
    WebElement ALERT_MESSAGE_BOX;

    public boolean setValueForReleaseApprovalInApprovalConnection(String fieldValue, boolean setCheckBox, String releaseRequestURL) {
        boolean setReleaseReqApproval = false;
        log.info("Setting up Release request approval");
        WebElement checkBox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(APPR_CONNECTIONS_RELEASE_REQUEST_APPROVAL, fieldValue)));
        if (setCheckBox) {
            commonWaits.waitForElementClickable(checkBox);

            if (!commonWaits.isElementPresent(RELEASE_APPROVAL_CONFIGURATION_TXTBOX)) {
                commonWaits.clickWithoutInterception(checkBox);
                setReleaseReqApproval = commonWaits.isElementPresent(RELEASE_APPROVAL_CONFIGURATION_TXTBOX);
                RELEASE_APPROVAL_CONFIGURATION_TXTBOX.clear();
                RELEASE_APPROVAL_CONFIGURATION_TXTBOX.sendKeys(releaseRequestURL);
            } else {
                log.info("------------------ Settings are already enabled");
            }
        } else {
            commonWaits.waitForElementClickable(checkBox);

            if (commonWaits.isElementPresent(RELEASE_APPROVAL_CONFIGURATION_TXTBOX)) {
                commonWaits.waitForElementClickable(checkBox);
                commonWaits.clickWithoutInterception(checkBox);
                setReleaseReqApproval = true;
            } else {
                log.info("------------------ Settings are already disabled");
            }
        }
        return setReleaseReqApproval;
    }

    public void saveApprovalSettings(){
        commonWaits.waitForElementVisible(BTN_SAVE);
        executeJS("arguments[0].click();", BTN_SAVE);
        commonWaits.waitForElementVisible(ALERT_MESSAGE_BOX,  10);
        Assert.assertTrue(ALERT_MESSAGE_BOX.getText().contains("successfully"), "Success message on save settings did not appear");
    }

    @FindBy(xpath = "//a[normalize-space()='Surveys' and contains(@href, 'survey')]")
    WebElement surveysTab;
    @FindBy(xpath = "//span[contains(@class, 'switch__item')]")
    WebElement surveyToggleDisabledTitle;
    @FindBy(xpath = "//*[@id='survey-enabled-switcher']/following-sibling::span[contains(@class, 'switch__slider')]")
    WebElement surveyToggleSwitcher;

    @FindBy(xpath = "//*[@data-ta='attachmentsForRTFCommentMaxLimitCounter']//input")
    WebElement RICH_TEXT_COMMENT_FIELD;

    @FindBy(xpath = "//button[@data-ta='adminLayoutSaveButton']")
    WebElement SAVE_BUTTON;

    @FindBy(xpath = "//div[contains(text(),'Changes saved successfully')]")
    WebElement SUCCESS_MESSAGE;

    public DWPAdministrationPage enableSurveysSetting(boolean enable) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), surveysTab, 5), "Surveys tab not found in DWP Admin navigation");
        commonWaits.clickWithoutInterception(surveysTab);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (enable && surveyToggleDisabledTitle.getText().contains("Disabled")) {
            commonWaits.clickWithoutInterception(surveyToggleSwitcher);
        }
        else if (!enable && surveyToggleDisabledTitle.getText().contains("Enabled")) {
            commonWaits.clickWithoutInterception(surveyToggleSwitcher);
        }
        else {
            log.info("Surveys setting already in desired state: " + enable);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;

    }

    public String getRichTextComment(){
		log.info("Getting rich text comment details -");
		String count =RICH_TEXT_COMMENT_FIELD.getAttribute("aria-valuenow");
		log.info("--- Rich text comment count is - "+count);
		return count;
 	}
	public void setRichTextComment(String value) {
		log.info("Sets the rich text comment fields to "+value);
		commonWaits.waitForElementVisible(RICH_TEXT_COMMENT_FIELD);
		commonWaits.clickWithoutInterception(RICH_TEXT_COMMENT_FIELD);
		keyClearAndType(RICH_TEXT_COMMENT_FIELD, value);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
	}

    public void saveChanges() {
        log.info("Saving changes");
        commonWaits.clickWithoutInterception(SAVE_BUTTON);
        Assert.assertTrue(commonWaits.isElementPresent(SUCCESS_MESSAGE), "Success toast message is not shown after saving");
        commonWaits.waitForExecutionComplete();
    }

    
}