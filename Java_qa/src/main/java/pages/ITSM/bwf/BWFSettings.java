package pages.ITSM.bwf;

import static framework.Launcher.getDriver;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class BWFSettings {

    public static final SolqaLogger log = new SolqaLogger(BWFSettings.class.getName());
    CommonWaits commonWaits;

    public BWFSettings() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    // -------------------- Locators (keep strings centralized) --------------------
    String ITEM_PRESENT_ON_PAGE = "//li/div[contains(.,'%s')]";
    String APPLICATION_CONFIGURATION_TAB = "//button[@role='tab' and contains(., '%s')]";
    String APPLICATION_CONFIG_LINK_BY_NAME = "//span[contains(text(), '%s')]/parent::a";
    String TEMPLATE_PRESENT_IN_TEMPLATES_LIST_XPATH = "//*[(@role='row' or self::tr or self::li or self::div) and contains(normalize-space(.), '%s')]";
    String CONFIG_VALUE_BUTTON_BY_RXID = "//div[@aria-label='Configuration Value']//button[@rx-id='%s-button']";
    String commonConfigLinkByName = "//*[@rx-view-component-id='2f651a53-d8e7-4d9e-963a-2adf23490817']//a[contains(normalize-space(.), '%s')]";
    String configurationValueLocatorForCommonConfig = "//div[@aria-label='Configuration Value']";
    String configValue = "//div[@aria-label='Configuration Value']//button[@rx-id='%s-button']";
    String applicationConfigLinkByName = "//a[contains(normalize-space(.), '%s')]";
    String APPLICATION_CONFIGURATIONS_TAB_BTN = "//button[@role='tab' and .//span[contains(@class,'nav-link-title') and normalize-space()='Application Configurations']]";
    String EDIT_MODAL_ROOT = "//rx-runtime-view-modal[contains(.,'Edit') or contains(.,'edit')]";
    String LOB_LINK_BY_NAME = "//div[contains(@class,'rx-custom-cell')]//a[normalize-space()='%s']";
    String LOB_STATUS_DROPDOWN_BUTTON_IN_MODAL = EDIT_MODAL_ROOT + "//*[self::label or self::span][normalize-space()='Status']/ancestor::adapt-rx-control-label[1]/following-sibling::div//button";
    String RX_SELECT_OPTION_BY_TEXT = "//button[contains(@class,'rx-select__option') or @role='option']//*[normalize-space()='%s']/ancestor::button[1]";
    String MODAL_SAVE_BUTTON = EDIT_MODAL_ROOT + "//button[normalize-space()='Save' or contains(normalize-space(.),'Save')]";
    String MODAL_CANCEL_BUTTON = EDIT_MODAL_ROOT + "//button[normalize-space()='Cancel' or contains(normalize-space(.),'Cancel')]";
    String APP_CONFIG_LINK_OR_BUTTON_BY_TEXT = "//*[self::a or self::button][contains(normalize-space(.), '%s') or .//*[contains(normalize-space(.), '%s')]]";
    String APP_CONFIG_ROW_BY_TEXT = "//*[(@role='row' or self::tr) and (.//*[contains(normalize-space(.), '%s')] or contains(normalize-space(.), '%s'))]";
    String APP_CONFIG_CELL_BY_TEXT = "//*[self::td or self::div or self::span][contains(normalize-space(.), '%s')]";
    String applicationConfigEntryByName = "//*[@rx-view-component-id='3fbf5670-e9fc-4c7a-87e4-c5503cf80fe9']//*[self::a or self::button or self::span][normalize-space()='%s' or .//*[normalize-space()='%s']]";
    String dynamicFieldForAllQuestions = "//bwf-field-config-layout//div//span[contains(. ,'%s')]";
    String dynamicButton = "//*[text()='Dynamic']/ancestor::label//input";
    String listOptions = "//*[contains(text(), 'List Options')]/ancestor::bwf-dynamic-field-named-list//rx-definition-picker//button";
    String searchButton = "//button[@rx-id='search-button']";
    String searchBox = "//*[@rx-id='back-button']/following-sibling::adapt-rx-textfield//input";
    String nameListValue = "//li[contains(. ,'Shared Services')]//button[contains(. ,'Persons')]";
    String manageDynamicFieldSaveButton = "//div[contains(@class,'modal-footer')]//button[normalize-space()='Save']";
    String manageDynamicFieldCancelButton = "//*[contains(@class, 'modal-footer')]//button[normalize-space()= 'Cancel']";
    String ManageDynamicFieldButton = "//*[@rx-view-component-id='3cd9b535-36f6-4718-bede-9154ca02ae22']//button";
    String tempStatus = "//*[@rx-view-component-id='88cf66ca-8be6-46b2-93e0-52890187dffb']//button[contains(@class,'dropdown-toggle')]";
    String casePriority	="//*[@rx-view-component-id = 'c933ab70-9004-4347-9537-3ae65ec633b9']//button[contains(@class,'dropdown-toggle')]";
    String selectValueFromDropdown = "//button[contains(@class,'rx-select__option')]//span[text()='%s']";
    String templateStatusAfterSave = "//*[@rx-view-component-id='e5e9bf3e-8135-4d53-b03f-484545a64a5a']/rx-read-only-field//div[text()='%s']";
    String gridItem = "//div[contains(@class, 'rx-custom-cell') and contains(.,'%s')]//a";
    String gridCell = "//div[contains(@class, 'rx-custom-cell')]";
    String itemPresentOnPageSetting = "//adapt-tree-node//*[@aria-label='%s']";
    String fieldRow ="//bwf-dynamic-field-simple[contains(. ,'%s')]";
    String fieldHiddenRadio = "//bwf-dynamic-field-simple[contains(. ,'%s')]//adapt-rx-switch[contains(. ,'Hidden')]//input";

    //WebElements
    @FindBy(xpath = "//div[@aria-label='Configuration Value']")
    WebElement configurationValueLocator;

    @FindBy(xpath = "//div[contains(@class,'adapt-rx-search') and @role='search']//input")
    WebElement searchSettingsInput;

    @FindBy(xpath = "///button[@role='tab' and .//span[contains(@class,'nav-link-title') and normalize-space()='Application Configurations']]")
    WebElement applicationConfigurationsTabButton;

    @FindBy(xpath = "(//span[contains(@class, 'a-tree__toggle')])[last()]")
    WebElement collapsedMenuItem;

    @FindBy(xpath = "(//adapt-tree-node//span[contains(@data-testid, 'adapt-tree')])[1]")
    WebElement treeCollapsed;

    @FindBy(xpath = "//span[contains(text(),'Line of Business Management')]")
    WebElement lobManagement;

    @FindBy(xpath = "//button[contains(@aria-label,'Next')]")
    public WebElement forwardArrowInManageLOB;

    @FindBy(xpath = "//*[@rx-view-component-id='50d0c749-98bc-4f71-91cc-a346cafa4272']//input[contains(@id,'rx-search')]")
    WebElement searchTextboxHelixGPTConfig;

    @FindBy(xpath = "//rx-runtime-view-modal[contains(. ,'Edit')]//button[contains(. ,'Save')]")
    WebElement applicationConfigSaveButton;

    @FindBy(xpath = "//div[contains(@class,'adapt-rx-search') and @role='search']//input[contains(@placeholder,'Search')]")
    WebElement searchTextboxInTemplates;

    @FindBy(xpath = "//div[contains(@class, 'adapt-table-toolbar-item_visible')]/following-sibling::div//input[contains(@id,'rx-search')]")
    WebElement searchCommonConfigApplicationConfig;

    @FindBy(xpath = "//*[@rx-view-component-id='2f651a53-d8e7-4d9e-963a-2adf23490817']//input[contains(@placeholder, 'Search')]")
    WebElement commonConfigSearchInput;

    @FindBy(xpath = "//*[@rx-view-component-id='2f651a53-d8e7-4d9e-963a-2adf23490817']//button[contains(normalize-space(.), 'Search')]")
    WebElement commonConfigSearchButton;

    @FindBy(xpath = "//rx-runtime-view-modal[contains(normalize-space(.), 'Edit Configuration')]")
    WebElement editConfigurationModal;

    @FindBy(xpath = "//rx-runtime-view-modal[contains(normalize-space(.), 'Edit Configuration')]//button[normalize-space(.)='Save']")
    WebElement saveButtonInEditConfigurationModal;

    @FindBy(xpath = "//*[@rx-view-component-id='2def1a32-0713-4e65-b2c9-9cde85c0352f']//button[contains(normalize-space(.), 'Save')]")
    WebElement saveButtonInEditCommonConfigurationModal;

    @FindBy(xpath = "//*[@rx-view-component-id='3fbf5670-e9fc-4c7a-87e4-c5503cf80fe9']//input")
    WebElement applicationConfigSearchInput;

    @FindBy(xpath = "//*[@rx-view-component-id='3fbf5670-e9fc-4c7a-87e4-c5503cf80fe9']//button[contains(normalize-space(.), 'Search')]")
    WebElement applicationConfigSearchButton;

    @FindBy(xpath = "//*[@id='dropdownMenu' and contains(@class,'bwf-lob-list-menu')]")
    WebElement lobDropDownButton;

    @FindBy(xpath = "//*[@id='dropdownMenu']//span")
    WebElement lobDropDownCurrentValue;

    @FindBy(xpath = "//*[@aria-labelledby='dropdownMenu']//button[contains(@class,'dropdown-item')]")
    List<WebElement> lobDropDownOptions;

    @FindBy(xpath = "//bwf-dynamic-field-group-selector//button[contains(. ,'Dynamic Field')]")
    WebElement addDynamicField;

    @FindBy(xpath = "//*[text()='Field Name']/parent::div//input")
    WebElement dynamicFieldName;

    @FindBy(xpath = "//*[text()='Field Description']/parent::div//input")
    WebElement dynamicFieldDescription;

    @FindBy(xpath = "//label[.='Field Value Type']/../adapt-rx-select/div/button")
    WebElement dynamicFieldValueType;

    @FindBy(xpath = "//span[text()='LIST']/ancestor::button")
    WebElement fieldValueTypeLIST;

    @FindBy(xpath = "//*[@rx-view-component-id='1c10246e-18ed-4201-91b7-210e7a975f9c']//input[@placeholder='Search']")
    WebElement searchCaseTemplate;

    @FindBy(xpath = "//*[@rx-view-component-id='1c10246e-18ed-4201-91b7-210e7a975f9c']//div[contains(@class,'search-button')]//button")
    WebElement searchCaseTemplateBtn;

    @FindBy(xpath ="//*[contains(@rx-id,'refresh-button')]")
    WebElement refreshButton;

    @FindBy(xpath = "//*[@rx-view-component-id='c9f48c1b-75e2-411c-929c-76bdce069a3d']//button[contains(.,'Edit')]")
    WebElement editMetadataLink;

    @FindBy(xpath = "//*[@rx-view-component-id='c9db0571-b703-4d5d-830c-d3a98f243c94']//span[text()='Save']")
    WebElement editSaveMetadataButton;

    public void enterValueInTextField(String value) {
        commonWaits.waitForAngularRequestsToFinish();
        log.info("Waiting for all spinners to hide");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Waiting for search settings input to be visible");
        commonWaits.waitForElementVisible(searchSettingsInput).clear();
        log.info("Entering value '" + value + "' in search settings input");
        commonWaits.waitForElementVisible(searchSettingsInput).sendKeys(value);
    }

    public void clickOnSearchedAdminSetting(String settingName) throws Exception {
        log.info("Attempting to click on searched admin setting: " + settingName);
        String itemPresent = String.format(ITEM_PRESENT_ON_PAGE, settingName);
        commonWaits.waitForByElementToBeVisible(By.xpath(itemPresent));
        int flag = getDriver().findElements(By.xpath(itemPresent)).size();
        log.info("Value of flag: " + flag);
        try {
            log.info("Waiting for collapsed menu item to be visible");
            commonWaits.waitForElementVisible_withFluentWait(getDriver(), collapsedMenuItem, 15);
        } catch (Exception e) {
            log.info(settingName + " is not configured on server, throwing exception");
            throw new Exception(settingName + " is not configured on server, Please check the admin setting configuration in Innovation Studio.");
        }
        if (flag != 0) {
            log.info("Setting found, clicking on it");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickWithoutInterception(By.xpath(itemPresent));
        } else {
            log.info("Setting not found, expanding tree and retrying");
            commonWaits.waitForElementVisible(treeCollapsed).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickWithoutInterception(By.xpath(itemPresent));
            log.info("Clicked on setting, waiting for LOB Management element");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), lobManagement, 15), "LOB Management should be visible after clicking on setting: " + settingName);
        }
    }

    public void clickOnForwardArrowInManageLOB() {
        log.info("Waiting for forward arrow in Manage LOB to be visible");
        commonWaits.waitForElementVisible(forwardArrowInManageLOB);
        log.info("Waiting for forward arrow in Manage LOB to be clickable");
        commonWaits.waitForElementClickable(forwardArrowInManageLOB);
        log.info("Clicking forward arrow in Manage LOB using JS");
        commonWaits.clickByJS(forwardArrowInManageLOB);
        log.info("Waiting for execution to complete after clicking forward arrow");
        commonWaits.waitForExecutionComplete();
    }

    public BWFSettings clickTabNameOnLineOfBusinessManagement(String tabName) {
        log.info("Waiting for execution to complete before clicking tab: " + tabName);
        commonWaits.waitForExecutionComplete();
        log.info("Waiting for some time before clicking tab: " + tabName);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        String applicationConfigurationXpath = String.format(APPLICATION_CONFIGURATION_TAB, tabName);
        // Some builds never transition this element to "invisible". Don't block the test here.
        log.info("Best-effort wait for tab to be invisible (short): " + tabName);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), applicationConfigurationXpath, 5),
                "Tab '" + tabName + "' should be visible on Line of Business Management page");
        log.info("Waiting for tab to be visible: " + tabName);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(applicationConfigurationXpath));
        log.info("Clicking on tab: " + tabName);
        commonWaits.clickByJS(ele);
        commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFSettings clickApplicationConfigurationsTab() {
        log.info("Waiting for application configurations tab to be visible");
        commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), By.xpath(APPLICATION_CONFIGURATIONS_TAB_BTN), 15);
        log.info("Clicking on application configurations tab");
        commonWaits.clickWithoutInterception(By.xpath(APPLICATION_CONFIGURATIONS_TAB_BTN));
        log.info("Waiting for execution to complete after clicking application configurations tab");
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public void setSearchValueOnCommonConfigApplicationConfig(String value) {
        commonWaits.waitForElementVisible(searchCommonConfigApplicationConfig).clear();
        commonWaits.waitForElementVisible(searchCommonConfigApplicationConfig).sendKeys(value + Keys.ENTER);
        log.info("Set" + value + " for Search field on LOB Application Configuration console");
        commonWaits.waitForExecutionComplete();
    }

    public void setSearchValueOnLobHelixGPTConfig(String value) {
        log.info("Waiting for search textbox in Helix GPT Config to be visible");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(searchTextboxHelixGPTConfig).clear();
        log.info("Entering value '" + value + "' in search textbox and pressing ENTER");
        commonWaits.waitForElementVisible(searchTextboxHelixGPTConfig).sendKeys(value + Keys.ENTER);
        log.info("Set " + value + " for Search field on Helix GPT Configuration console");
        commonWaits.waitForExecutionComplete();
    }

    public void setConfigValueForHelixGPT(String configurationValue) {
        log.info("Waiting for some time before setting config value for Helix GPT: " + configurationValue);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        String capitalizedconfigurationValue = configurationValue.substring(0, 1).toUpperCase() + configurationValue.substring(1);
        String ConfigValueLocator = "//*[@rx-view-component-id='076f1569-c615-45e1-b15a-72a0fa1c1d63']//div[@aria-label='Configuration Value']"
                + "/button[@aria-label='" + capitalizedconfigurationValue + "']";
        log.info("Getting actual toggle button value for: " + configurationValue);
        String ActualToggleButtonValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ConfigValueLocator, SolQAConstants.aria_label))).getAttribute(SolQAConstants.aria_pressed);
        log.info("Actual toggle button value for " + configurationValue + " is: " + ActualToggleButtonValue);
        if (!ActualToggleButtonValue.equals(SolQAConstants.trueValue) && configurationValue.equalsIgnoreCase(SolQAConstants.True)) {
            log.info("Toggle button is not enabled, enabling it for: " + configurationValue);
            commonWaits.clickWithoutInterception(By.xpath(String.format(CONFIG_VALUE_BUTTON_BY_RXID, SolQAConstants.trueValue)));
        } else if (!ActualToggleButtonValue.equals(SolQAConstants.falseValue) && configurationValue.equalsIgnoreCase(SolQAConstants.False)) {
            log.info("Toggle button is disabled, enabling it for: " + configurationValue);
            commonWaits.clickWithoutInterception(By.xpath(String.format(CONFIG_VALUE_BUTTON_BY_RXID, SolQAConstants.falseValue)));
        } else {
            log.info("Toggle button is already enabled for: " + configurationValue);
        }
    }

    public void saveApplicationConfigSettingValue() {
        log.info("Clicking on application config save button");
        commonWaits.clickWithoutInterception(applicationConfigSaveButton);
        log.info("Waiting for execution to complete after saving application config setting value");
        commonWaits.waitForExecutionComplete();
    }

    public void clickOnHelixGPTConfigNameAndSetValue(String configName, String configValue) {
        log.info("Setting search value on LOB Helix GPT Config: " + configName);
        setSearchValueOnLobHelixGPTConfig(configName);

        if (commonWaits.waitForElementPresent(String.format(APPLICATION_CONFIG_LINK_BY_NAME, configName), 50)) {
            log.info("Helix GPT Config name found, clicking: " + configName);
            commonWaits.clickWithoutInterception(By.xpath(String.format(APPLICATION_CONFIG_LINK_BY_NAME, configName)));
            log.info("Setting config value for Helix GPT: " + configValue);
            setConfigValueForHelixGPT(configValue);
            log.info("Saving application config setting value for: " + configName);
            saveApplicationConfigSettingValue();
        } else {
            log.info("Helix GPT Config name not found: " + configName);
        }
    }

    public BWFSettings verifyTemplatePresent(String templateName) {
        log.info("Verifying if template '" + templateName + "' is present in the list");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        searchTextboxInTemplates.clear();
        searchTextboxInTemplates.sendKeys(templateName + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(
                commonWaits.isElementPresent(String.format(TEMPLATE_PRESENT_IN_TEMPLATES_LIST_XPATH, templateName)),
                "Template '" + templateName + "' should be present in the list");
        return this;
    }

    public BWFSettings setConfigValueForCommonConfiguration(String configurationValue) {
        log.info("Setting configuration value for Common Configuration: " + configurationValue);
        
        // Validate input parameter
        Assert.assertTrue(configurationValue != null && !configurationValue.trim().isEmpty(), 
                "Configuration value must not be null or empty");
        
        String normalizedValue = configurationValue.trim();
        
        // Capitalize first letter safely
        String capitalizedConfigurationValue = normalizedValue.substring(0, 1).toUpperCase() 
                + (normalizedValue.length() > 1 ? normalizedValue.substring(1) : "");
        log.info("Capitalized configuration value: " + capitalizedConfigurationValue);
        
        // Build XPath for the configuration button
        String configValueXpath = configurationValueLocatorForCommonConfig + "/button[@aria-label='" + capitalizedConfigurationValue + "']";
        
        try {
            // Wait for configuration value section to be visible
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), configurationValueLocator, 30), 
                    "Configuration Value section should be visible in Edit Configuration modal");
            
            // Get the button element and check current state
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), configValueXpath, 15),
                    "Configuration button with aria-label '" + capitalizedConfigurationValue + "' should be visible");
            WebElement configButton = commonWaits.waitForByElementToBeVisible(By.xpath(configValueXpath));
            String currentAriaPressed = configButton.getAttribute("aria-pressed");
            log.info("Current aria-pressed value: " + currentAriaPressed);
            
            // Click button if aria-pressed is not already true
            if (currentAriaPressed == null || !currentAriaPressed.equals(SolQAConstants.trueValue)) {
                log.info("Clicking configuration value button for: " + capitalizedConfigurationValue);
                commonWaits.waitForElementClickable(configButton);
                try {
                    commonWaits.clickWithoutInterception(configButton);
                } catch (Exception clickEx) {
                    log.info("Normal click failed. Retrying with JS click. Error: " + clickEx.getMessage());
                    commonWaits.clickByJS(configButton);
                }
                log.info("Configuration value button clicked successfully");
            } else {
                log.info("Configuration value is already set to: " + capitalizedConfigurationValue);
            }
        } catch (Exception e) {
            log.error("Error setting configuration value: " + e.getMessage(), e);
            throw new AssertionError("Failed to set configuration value '" + normalizedValue + "': " + e.getMessage(), e);
        }
        
        return this;
    }
  
    public BWFSettings clickApplicationConfigNameAndSetValue(String configName, String configValue) {
        Assert.assertTrue(configName != null && !configName.trim().isEmpty(), "Config name must not be null/empty");
        Assert.assertTrue(configValue != null && !configValue.trim().isEmpty(), "Config value must not be null/empty");
        String normalizedName = configName.trim();
        String normalizedValue = configValue.trim();
        By configLinkBy = By.xpath(String.format(applicationConfigEntryByName, normalizedName, normalizedName));
        int maxAttempts = 3;
        boolean modalOpened = false;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("Attempt " + attempt + "/" + maxAttempts + " to open and update Application Config: " + normalizedName);
            commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), applicationConfigSearchInput, 20), "Application Config search input should be visible");
            WebElement searchInput = commonWaits.waitForElementVisible(applicationConfigSearchInput);
            searchInput.clear();
            searchInput.sendKeys(normalizedName + Keys.ENTER);
            try {
                commonWaits.waitForElementVisible(applicationConfigSearchButton).click();
            } catch (Exception ignored) {
                // Enter key generally triggers search; explicit button click is the best effort.
            }
            commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), configLinkBy, 20)) {
                log.info("Application Config entry '" + normalizedName + "' not visible on attempt " + attempt + ". Refreshing and retrying.");
                getDriver().navigate().refresh();
                commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
                clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
                continue;
            }
            WebElement configLink = commonWaits.waitForByElementToBeVisible(configLinkBy);
            try {
                commonWaits.clickWithoutInterception(configLink);
            } catch (Exception clickEx) {
                log.info("Normal click failed for config '" + normalizedName + "'. Retrying with JS click. Error: " + clickEx.getMessage());
                commonWaits.clickByJS(configLink);
            }
            modalOpened = commonWaits.waitForElementVisible_withFluentWait(getDriver(), editConfigurationModal, 12);
            if (modalOpened) {
                break;
            }
            log.info("Edit modal did not open for '" + normalizedName + "' on attempt " + attempt + ". Refreshing and retrying.");
            getDriver().navigate().refresh();
            commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
            clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
        }
        Assert.assertTrue(modalOpened, "Edit Configuration modal did not open for config: " + normalizedName + " after " + maxAttempts + " attempts");
        setConfigValueForCommonConfiguration(normalizedValue);
        try {
            commonWaits.waitForElementClickable(saveButtonInEditConfigurationModal).click();
        } catch (Exception saveClickEx) {
            log.info("Normal save click failed for application config '" + normalizedName
                    + "'. Retrying with JS click. Error: " + saveClickEx.getMessage());
            commonWaits.clickByJS(saveButtonInEditConfigurationModal);
        }
        commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), editConfigurationModal, 15), "Edit Configuration modal should close after saving config: " + normalizedName);
        return this;
    }

    public void clickCommonConfigurationsAndSetValue(String configName, String configValue) {
        Assert.assertTrue(configName != null && !configName.trim().isEmpty(), "Common config name must not be null/empty");
        Assert.assertTrue(configValue != null && !configValue.trim().isEmpty(), "Common config value must not be null/empty");
        String normalizedName = configName.trim();
        String normalizedValue = configValue.trim();
        By configLinkBy = By.xpath(String.format(commonConfigLinkByName, normalizedName));
        int maxAttempts = 3;
        boolean modalOpened = false;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("Attempt " + attempt + "/" + maxAttempts + " to open and update Common Configuration: " + normalizedName);
            commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), commonConfigSearchInput, 20), "Common Config search input should be visible");
            WebElement searchInput = commonWaits.waitForElementVisible(commonConfigSearchInput);
            searchInput.clear();
            searchInput.sendKeys(normalizedName);
            commonWaits.waitForElementVisible(commonConfigSearchButton).click();
            commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), configLinkBy, 15), "Common Configuration link '" + normalizedName + "' should be visible after search");
            WebElement configLink = commonWaits.waitForByElementToBeVisible(configLinkBy);
            try {
                commonWaits.clickWithoutInterception(configLink);
            } catch (Exception clickEx) {
                log.info("Normal click failed for common config '" + normalizedName + "'. Retrying with JS click. Error: " + clickEx.getMessage());
                commonWaits.clickByJS(configLink);
            }
            modalOpened = commonWaits.waitForElementVisible_withFluentWait(getDriver(), editConfigurationModal, 12);
            if (modalOpened) {
                break;
            }
            log.info("Edit modal did not open for common config '" + normalizedName + "' on attempt " + attempt + ". Refreshing and retrying.");
            getDriver().navigate().refresh();
            commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
        }
        Assert.assertTrue(modalOpened, "Edit Configuration modal did not open for common config: " + normalizedName + " after " + maxAttempts + " attempts");
        setConfigValueForCommonConfiguration(normalizedValue);
        try {
            commonWaits.waitForElementClickable(saveButtonInEditCommonConfigurationModal).click();
        } catch (Exception saveClickEx) {
            log.info("Normal save click failed for common config '" + normalizedName
                    + "'. Retrying with JS click. Error: " + saveClickEx.getMessage());
            commonWaits.clickByJS(saveButtonInEditCommonConfigurationModal);
        }
        commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), editConfigurationModal, 15), "Edit Configuration modal should close after saving common config: " + normalizedName);
    }

    public void setLOBStatus(String lobName, String desiredStatus) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();

        WebElement lobLink = commonWaits.waitForByElementToBeVisible( By.xpath(String.format(LOB_LINK_BY_NAME, lobName)));
        commonWaits.clickWithoutInterception(lobLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();

        WebElement statusButtonEl = commonWaits.waitForByElementToBeVisible(By.xpath(LOB_STATUS_DROPDOWN_BUTTON_IN_MODAL));
        String current = statusButtonEl.getText() == null ? "" : statusButtonEl.getText().trim();

        if (!current.equalsIgnoreCase(desiredStatus)) {
            commonWaits.clickWithoutInterception(statusButtonEl);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.clickWithoutInterception(By.xpath(String.format(RX_SELECT_OPTION_BY_TEXT, desiredStatus)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.clickWithoutInterception(By.xpath(MODAL_SAVE_BUTTON));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
        } else {
            if (commonWaits.isElementPresent(By.xpath(MODAL_CANCEL_BUTTON))) {
                commonWaits.clickWithoutInterception(By.xpath(MODAL_CANCEL_BUTTON));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                commonWaits.waitForExecutionComplete();
            }
        }
    }

    public void selectLOBFromDropDown(String lobName) {
        String expectedLobName = lobName == null ? "" : lobName.trim();
        Assert.assertTrue(!expectedLobName.isEmpty(), "LOB name should not be empty");

        WebElement lobDropdown = commonWaits.waitForElementVisible(lobDropDownButton);
        commonWaits.waitForElementClickable(lobDropdown);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        String currentLob = "";
        try {
            currentLob = lobDropDownCurrentValue.getText().trim();
        } catch (Exception ignored) {
        }
        if (currentLob.equalsIgnoreCase(expectedLobName)) {
            log.info("LOB already set to expected value: " + expectedLobName);
            return;
        }

        commonWaits.clickWithoutInterception(lobDropdown);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        WebElement lobOption = getLobDropDownOptionByText(expectedLobName);
        Assert.assertTrue(lobOption != null, "Could not find LOB option '" + expectedLobName + "' in dropdown");

        try {
            commonWaits.waitForElementClickable(lobOption, 10);
            commonWaits.clickWithoutInterception(lobOption);
        } catch (Exception e) {
            log.info("Could not click LOB option directly; trying JS click. Reason: " + e.getMessage());
            commonWaits.clickByJS(lobOption);
        }

        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    private WebElement getLobDropDownOptionByText(String lobName) {
        String expectedLob = lobName.trim();
        String expectedLobNormalized = lobName.replaceAll("\\s+", "");

        List<WebElement> lobOptions = lobDropDownOptions;
        for (WebElement option : lobOptions) {
            String optionText = option.getText() == null ? "" : option.getText().trim();
            String optionTestId = option.getAttribute("data-testid");

            if (optionText.equalsIgnoreCase(expectedLob)) {
                return option;
            }
            if (optionTestId != null && optionTestId.replaceAll("\\s+", "").equalsIgnoreCase(expectedLobNormalized)) {
                return option;
            }
        }
        return null;
    }

    public BWFSettings navigateToSubmenuInSettings(String settingName) throws Exception {
        commonWaits.waitForAngularRequestsToFinish();
        enterValueInTextField(settingName);
        clickOnSearchedAdminSetting(settingName);
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public BWFSettings setSearchValueOnCaseTemplatesGrid(String value) {
        commonWaits.waitForElementVisible(searchCaseTemplate).clear();
        searchCaseTemplate.sendKeys(value);
        searchCaseTemplate.sendKeys(Keys.ENTER);
        log.info("Set" + value + " for Search field on Case Template Console");
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFSettings clickSearchBtnOnCaseTemplateGrid() {
        log.info("--------Clicked on Search button");
        commonWaits.waitForElementClickable(searchCaseTemplateBtn);
        commonWaits.clickWithoutInterception(commonWaits.waitForElementVisible(searchCaseTemplateBtn));
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public void clickRefreshBtn() {
        commonWaits.clickWithoutInterception(refreshButton);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAngularRequestsToFinish();
    }

    public boolean verifyValueInCaseTemplateGrid(String value) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForExecutionComplete();
        clickRefreshBtn();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForByElementToBeVisible(By.xpath(gridCell));
        boolean bfound = false;
        int icount=0;
        while (icount < 4 && !bfound) {
            try {
                List<WebElement> dataCells = commonWaits.findElements(By.xpath(gridCell));
                for(WebElement cellData : dataCells) {
                    if (cellData.getText().trim().equalsIgnoreCase(value)) {
                        bfound =  true;
                        log.info("-------- verifyValueInKnowledgeSetGrid() - Found matched text - " + cellData.getText().trim());

                        break;
                    }
                }
                icount = icount + 1;
            } catch (Exception e) {
                //Expecting to catch Stale Element Reference exception
                log.info("-------- Caught Exception - " + e.getMessage());
                icount = icount + 1;
            }
        }
        return bfound;
    }

    public BWFSettings openGridItemByName(String value) {
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementClickable(commonWaits.findElement(By.xpath(String.format(gridItem,value))));
        commonWaits.waitForSometime(2000);
        commonWaits.clickWithoutInterception(By.xpath(String.format(gridItem,value)));
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public void clickEditMetadataLink() {
        log.info("----- Click on Edit Template Metadata Link");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();
        if (commonWaits.isElementPresent(editMetadataLink)) {
            commonWaits.clickWithoutInterception(editMetadataLink);
        }
    }

    public BWFSettings changeTemplateStatus(String TemplateStatus){
        boolean isDisplayed = commonWaits.isElementPresent(By.xpath(String.format(templateStatusAfterSave, TemplateStatus)));
        if(!isDisplayed){
            clickEditMetadataLink();
            setDropDownValuesOfEditCaseTemplate("TEMPSTATUS", TemplateStatus);
            clickEditMetadataSaveButton();
        }
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(templateStatusAfterSave, TemplateStatus)));
        log.info("Template Status Changed to: "+ TemplateStatus);
        return this;
    }

    public BWFSettings setDropDownValuesOfEditCaseTemplate(String labelName, String value) {
        commonWaits.waitForExecutionComplete();
        log.info("set DropDown Values Of EditCaseTemplate");
        String sLocator=null;
        switch (labelName) {
            case "TEMPSTATUS":
                sLocator= tempStatus;
                break;
            case "PRIORITY":
                sLocator= casePriority;
                break;
            default:
                break;
        }
        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(sLocator)));
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectValueFromDropdown, value))));
        commonWaits.waitForExecutionComplete();
        log.info(value+" for TextField"+labelName+" in Template");
        return this;
    }

    public void clickEditMetadataSaveButton(){
        commonWaits.waitForElementVisible(editSaveMetadataButton);
        if (editSaveMetadataButton.isDisplayed() && editSaveMetadataButton.isEnabled()){
            commonWaits.clickWithoutInterception(editSaveMetadataButton);
        }
    }

    public BWFSettings clickOnManageDynamicFields_ByChangingTemplateStatus(){
        log.info("----- Clicking on Manage dynamic fields link-------- ");
        WebElement element = commonWaits.findElement(By.xpath(ManageDynamicFieldButton));
        commonWaits.waitForElementVisible(element);
        commonWaits.waitForElementClickable(element);
        element.click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFSettings addDynamicField(String value){
        boolean isDisplayed = commonWaits.isElementPresent(By.xpath(String.format(dynamicFieldForAllQuestions, value)));
        if(!isDisplayed) {
            commonWaits.waitForElementClickable(addDynamicField).click();
            commonWaits.waitForElementVisible(dynamicFieldName).sendKeys(value);
            commonWaits.waitForElementVisible(dynamicFieldDescription).sendKeys(value);
            commonWaits.waitForElementVisible(dynamicFieldValueType).click();
            commonWaits.waitForElementVisible(fieldValueTypeLIST).click();
            WebElement we = commonWaits.waitForElementPresenceLocated(By.xpath(dynamicButton));
            if (we.getAttribute("aria-checked").equalsIgnoreCase("false")){
                we.click();
            }
            commonWaits.waitForByElementToBeVisible(By.xpath(listOptions)).click();
            WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(searchButton));
            commonWaits.clickWithoutInterception(ele);
            commonWaits.waitForByElementToBeVisible(By.xpath(searchBox)).sendKeys("Persons");
            commonWaits.waitForByElementToBeVisible(By.xpath(nameListValue)).click();
            commonWaits.waitForByElementToBeVisible(By.xpath(manageDynamicFieldSaveButton)).click();
        }
        else{
            commonWaits.clickWithoutInterception(By.xpath(String.format(manageDynamicFieldCancelButton)));
        }
        return this;
    }

    public void clickOnSearchedAdminSettingWithoutClickToExpand(String settingName) {
        String itemPresent = String.format(itemPresentOnPageSetting, settingName);
        int flag = commonWaits.findElements(By.xpath(itemPresent)).size();
        log.info("Value of flag:" + flag);
        if (flag != 0) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), itemPresent, 20), "Expected admin setting '" + settingName + "' to be visible on the page");
            commonWaits.waitForByElementToBeVisible(By.xpath(itemPresent)).click();
        } else {
            commonWaits.waitForElementVisible(treeCollapsed).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForByElementToBeVisible(By.xpath(itemPresent)).click();
        }
        commonWaits.waitForSometime(3000);
    }

    public BWFSettings setDynamicFieldToHidden(String fieldName, boolean hidden) {
        boolean isDisplayed = commonWaits.isElementPresent(By.xpath(String.format(fieldRow, fieldName)));
        if (isDisplayed) {
            commonWaits.clickWithoutInterception(By.xpath(String.format(fieldRow, fieldName)));
            WebElement we = commonWaits.waitForElementPresenceLocated(By.xpath(String.format(fieldHiddenRadio, fieldName)));
            if (!we.getAttribute("aria-checked").equalsIgnoreCase(String.valueOf(hidden))) {
                we.click();
                if (commonWaits.waitForByElementToBeVisible(By.xpath(manageDynamicFieldSaveButton)).isEnabled()) {
                    commonWaits.waitForByElementToBeVisible(By.xpath(manageDynamicFieldSaveButton)).click();
                }
                else {
                    commonWaits.clickWithoutInterception(By.xpath(String.format(manageDynamicFieldCancelButton)));
                }
            }
        }
        return this;
    }


}