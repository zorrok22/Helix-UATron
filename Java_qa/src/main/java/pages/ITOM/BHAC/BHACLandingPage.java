package pages.ITOM.BHAC;

import framework.BasePage;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import util.CommonMethods;
import util.CommonWaits;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.SolqaLogger;
import java.time.Duration;
import java.util.List;

public class BHACLandingPage extends BasePage{

    public enum BHACTab {
        DASHBOARD("Dashboard"),
        ASSETS("Assets"),
        RISKS("Risks"),
        OPERATIONS("Operations"),
        MANAGE("Manage"),
        CONFIGURATION("Configuration");

        private final String tabName;

        BHACTab(String tabName) {
            this.tabName = tabName;
        }

        public String getTabName() {
            return tabName;
        }
    }

    WebDriver driver;
    CommonWaits commonWaits;
    static SolqaLogger log = new SolqaLogger(BHACLandingPage.class.getName());

    /**
     * Advanced filter docked panel (vulnerability selector). Overlay may rebuild; use explicit waits on these fields.
     */
    @FindBy(id = "vulnerability-filters-apply-btn-advance-filters")
    WebElement advancedFilterApplyButton;

    @FindBy(id = "vulnerability-close-child-advance-filters")
    WebElement advancedFilterChildDoneButton;

    /** Foreground drill-in child panel; wait visible before {@link #advancedFilterChildSearchInput}. */
    @FindBy(xpath = "//fieldset[not(@disabled)]//div[@id='vulnerability-child-advance-filters']")
    WebElement advancedFilterChildPanelBody;

    @FindBy(xpath = "//fieldset[not(@disabled)]//adapt-rx-textfield[@id='vulnerability-search-child-advance-filters']//input")
    WebElement advancedFilterChildSearchInput;

    @FindBy(xpath = "//button[contains(@class,'adapt-rx-uploader-attach-btn')]")
    WebElement attachFileButton;

    @FindBy(xpath = "//button[.//span[text()='Clear']]")
    WebElement clearButton;

    @FindBy(xpath = "//adapt-table[@id='vulnerability-options-table']//button[contains(@class,'d-icon-file_task_o')]")
    WebElement configureScriptParamsButton;

    @FindBy(xpath = "//button[@id='modal-select']")
    WebElement confirmContinueButton;

    @FindBy(xpath = "//button[@id='create-with-operations']")
    WebElement createOperationButton;

    @FindBy(xpath = "//app-operations-policy-create//h1[contains(normalize-space(),'Create Operation')]")
    WebElement createOperationHeading;

    // Visible label for the Execute Now radio button
    @FindBy(xpath = "//adapt-rx-radiobutton-group[.//input[@name='nowWindow']]//label[.//span[contains(normalize-space(),'Execute Now')]]")
    WebElement executeNowLabel;

    @FindBy(xpath = "//input[@type='radio'][@name='nowWindow'][@value='Execute Now']")
    WebElement executeNowRadio;

    // Visible label for the IA radio button (used when the input itself is CSS-hidden)
    @FindBy(xpath = "//adapt-rx-radiobutton-group[@name='remediationTool']//label[.//span[contains(normalize-space(),'IA')]]")
    WebElement iaRemediationToolLabel;

    @FindBy(xpath = "//input[@type='radio'][@name='remediationTool'][@value='ADE']")
    WebElement iaRemediationToolRadio;

    @FindBy(xpath = "//button[@id='import-scan-action']")
    WebElement importButton;

    @FindBy(xpath = "//button[@id='import-scans-modal-import']")
    WebElement modalImportButton;

    @FindBy(xpath = "//button[@id='next-operation-policy-step']")
    WebElement nextButton;

    @FindBy(xpath = "//textarea[@data-testid='operation-policy-name-description']")
    WebElement operationDescriptionInput;

    @FindBy(xpath = "//input[@data-testid='operation-policy-name']")
    WebElement operationNameInput;

    @FindBy(xpath = "//button[contains(@class,'adapt-rx-search__search-button')]")
    WebElement operationsSearchButton;

    @FindBy(xpath = "//div[contains(@class,'adapt-rx-search__input-wrapper')]//input[@name='SearchText']")
    WebElement operationsSearchInput;

    @FindBy(xpath = "//adapt-table[@id='operations-grid']//th[@aria-label='Status']")
    WebElement operationsStatusHeader;

    @FindBy(xpath = "//adapt-table[@id='operations-grid']//tbody//tr[contains(@class,'at-data-row')]")
    List<WebElement> operationsTableRows;

    @FindBy(xpath = "//adapt-table[@id='operations-grid']//th[@aria-label='Type']")
    WebElement operationsTypeHeader;

    @FindBy(xpath = "//adapt-rx-select[@name='os-filter']//button[@role='combobox']")
    WebElement osFilterDropdown;

    @FindBy(xpath = "//button[normalize-space()='Remove' and @role='menuitem']")
    WebElement removeMenuOption;

    @FindBy(xpath = "//button[@id='save-operation-policy']")
    WebElement saveOperationButton;

    @FindBy(xpath = "//div[@id='properties-data']//form//button[contains(@class,'d-icon-check')]")
    WebElement scriptParamCheckButton;

    @FindBy(xpath = "//div[@id='properties-data']//table")
    WebElement scriptParamsTable;

    @FindBy(xpath = "//input[@data-testid='property-value']")
    WebElement scriptParamValueInput;

    @FindBy(xpath = "//button[.//span[text()='Select all']]")
    WebElement selectAllButton;

    @FindBy(xpath = "//adapt-rx-select[@name='severity-filter']//button[@role='combobox']")
    WebElement severityFilterDropdown;

    @FindBy(xpath = "//button[normalize-space()='Select all']")
    WebElement severitySelectAllButton;

    @FindBy(xpath = "//adapt-rx-select[@name='auth-type']//button[@role='combobox']")
    WebElement vendorDropdown;

    @FindBy(xpath = "//adapt-table[@id='vulnerabilitySelector-grid']//tbody//tr[contains(@class,'at-data-row')]")
    List<WebElement> vulnerabilityResultRows;

    @FindBy(id = "vulnerabilities-selector-advanced-search")
    WebElement vulnerabilitySelectorAdvancedFilterButton;

    @FindBy(xpath = "//button[@data-testid='vulnerabilities-selector-search-input_button']")
    WebElement vulnerabilitySearchButton;

    @FindBy(xpath = "//input[@data-testid='vulnerabilities-selector-search-input_input']")
    WebElement vulnerabilitySearchInput;

    // Dynamic XPath to find the Action button in the same row as a given file name
    String actionButtonByFileXpath = "//adapt-table[@id='importedScanList-grid']//tr[.//div[contains(normalize-space(),'%s')]]//button[contains(@class,'at-action-dropdown-toggle')]";
    /**
     * Active drill-in child panel (Asset, Vulnerability Name, …) lives under the enabled {@code fieldset} in the
     * front {@code adapt-docked-panel}. When that child is open, the parent "Advanced filter" stack uses
     * {@code fieldset[@disabled]} (CDK overlay); scoping to {@code fieldset[not(@disabled)]} targets the foreground panel only.
     * Checkbox / label XPaths below are rooted here. Format {@code %s} with {@link CommonMethods#escapeSingleQuotesForXPath(String)}.
     */
    String advancedFilterChildOptionCheckboxInputXpath = "//fieldset[not(@disabled)]//div[@id='vulnerability-child-advance-filters']//adapt-rx-checkbox[.//span[normalize-space()='%s']]//input[@type='checkbox']";
    /** Visible label for the same row (reliable click target when native input is not interactable). */
    String advancedFilterChildOptionLabelXpath = "//fieldset[not(@disabled)]//div[@id='vulnerability-child-advance-filters']//adapt-rx-checkbox[.//span[normalize-space()='%s']]//label";
    /** Advanced filter docked panel root ({@code div#vulnerability-advance-filters}). */
    String vulnerabilityAdvanceFiltersPanelXpath = "//div[@id='vulnerability-advance-filters']";
    /** Advanced filter panel: criterion row by visible label (e.g. Asset, Vulnerability Name). Format with {@link CommonMethods#escapeSingleQuotesForXPath(String)}. */
    String advancedFilterCriterionRowXpath = vulnerabilityAdvanceFiltersPanelXpath
            + "//div[contains(@class,'cursor-pointer')][.//span[normalize-space()='%s']]";
    // Dynamic XPath to find a file name in the import scan list table
    String importedFileXpath = "//adapt-table[@id='importedScanList-grid']//div[contains(@class,'ellipsis') and contains(normalize-space(),'%s')]";
    // Dynamic XPath for navigation tabs by visible text
    String navTabXpath = "//span[@class='a-menu__text' and normalize-space()='%s']";
    String operationNameLinkXpath = "//adapt-table[@id='operations-grid']//a[contains(normalize-space(),'%s')]";
    String operationStatusByNameXpath = "//adapt-table[@id='operations-grid']//tr[.//a[contains(normalize-space(),'%s')]]//span[contains(@class,'operation-status')]";
    // Dynamic XPath for OS filter checkbox option by visible text
    String osOptionXpath = "//adapt-rx-select[@name='os-filter']//button[@role='option' and .//adapt-highlight[normalize-space()='%s']]";
    // Dynamic XPath to click the pencil icon for a specific script parameter row by name
    String scriptParamPencilXpath = "//div[@id='properties-data']//tr[td[contains(normalize-space(),'%s')]]//button[contains(@class,'d-icon-pencil')]";
    // Dynamic XPath for vendor dropdown option by visible text
    String vendorOptionXpath = "//button[@role='option']//adapt-highlight[normalize-space()='%s']";
    // Dynamic XPath to find and click the result row matching the given asset name in the vulnerability selector table
    String vulnerabilityAssetRowXpath = "//adapt-table[@id='vulnerabilitySelector-grid']//tbody//tr[contains(@class,'at-data-row')][.//div[contains(@class,'ellipsis') and contains(normalize-space(),'%s')]]//button[contains(@class,'expansion-btn')]";

    public BHACLandingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        commonWaits = new CommonWaits();
    }

    /**
     * Selects IA as the Remediation Tool if it is not already selected.
     */
    public BHACLandingPage selectIAAsRemediationTool() {
        log.info("Checking if IA remediation tool radio button is present and selected.");
        try {
            // Wait on the visible label — the underlying <input> may be CSS-hidden
            // by the adapt-rx-radiobutton component's custom styling
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.ignoring(StaleElementReferenceException.class);
            wait.until(ExpectedConditions.visibilityOf(iaRemediationToolLabel));
        } catch (Exception e) {
            Assert.fail("IA remediation tool label is not visible after 15 seconds. " + e.getMessage());
        }
        // Use JS to read the checked state — isSelected() works on hidden inputs too,
        // but JS is more reliable when the element has no focus/visible state
        boolean isChecked = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checked;", iaRemediationToolRadio);
        if (!isChecked) {
            log.info("IA radio button is not selected. Clicking the label to select it.");
            iaRemediationToolLabel.click();
            commonWaits.waitForPageLoad(driver);
            log.info("IA remediation tool radio button selected.");
        } else {
            log.info("IA remediation tool radio button is already selected.");
        }
        return this;
    }

    /**
     * Clears and types the given name into the Operation Name field.
     * @param name the operation name to enter
     */
    public BHACLandingPage selectExecuteNow() {
        log.info("Checking if Execute Now radio button is present and selected.");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.ignoring(StaleElementReferenceException.class);
            wait.until(ExpectedConditions.visibilityOf(executeNowLabel));
        } catch (Exception e) {
            Assert.fail("Execute Now radio label is not visible after 15 seconds. " + e.getMessage());
        }
        boolean isChecked = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checked;", executeNowRadio);
        if (!isChecked) {
            log.info("Execute Now radio button is not selected. Clicking the label to select it.");
            executeNowLabel.click();
            commonWaits.waitForPageLoad(driver);
            log.info("Execute Now radio button selected.");
        } else {
            log.info("Execute Now radio button is already selected.");
        }
        return this;
    }

    public BHACLandingPage provideOperationName(String name) {
        log.info("Entering operation name: " + name);
        commonWaits.keyClearAndType(operationNameInput, name);
        log.info("Operation name entered: " + name);
        return this;
    }

    /**
     * Clears and types the given text into the Operation Description field.
     * @param description the operation description to enter
     */
    public BHACLandingPage provideOperationDescription(String description) {
        log.info("Entering operation description: " + description);
        commonWaits.keyClearAndType(operationDescriptionInput, description);
        log.info("Operation description entered: " + description);
        return this;
    }

    public BHACLandingPage clickSaveButton() {
        log.info("Attempting to click Save button.");
        try {
            commonWaits.clickWithoutInterception(saveOperationButton);
            commonWaits.waitForPageLoad(driver);
            log.info("Successfully clicked Save button.");
        } catch (Exception e) {
            Assert.fail("Save button remained disabled after 10 seconds. Cannot proceed. " + e.getMessage());
        }
        return this;
    }

    /**
     * Clicks the "Configure Script Parameters" button in the vulnerability-options-table
     * and waits for the Configuration Script Parameters modal to open.
     */
    public BHACLandingPage openConfigureScriptParameters() {
        log.info("Clicking Configure Script Parameters button.");
        commonWaits.clickWithoutInterception(configureScriptParamsButton);
        commonWaits.waitForElementVisible(scriptParamsTable);
        log.info("Configuration Script Parameters modal is open.");
        return this;
    }

    /**
     * In the Configuration Script Parameters modal, finds the row for the given
     * parameter name, clicks its pencil icon, types the value, waits for the inline
     * check (OK) button to become enabled, then clicks it.
     *
     * @param paramName the script parameter name (e.g. "sf_action", "software_name")
     * @param value     the value to set for that parameter
     */
    public BHACLandingPage provideScriptParameter(String paramName, String value) {
        log.info("Setting script parameter '" + paramName + "' = '" + value + "'");

        String pencilXpath = String.format(scriptParamPencilXpath, paramName);
        WebElement pencilBtn = commonWaits.waitForByElementToBeVisible(By.xpath(pencilXpath));
        commonWaits.clickWithoutInterception(pencilBtn);
        log.info("Clicked pencil icon for parameter: " + paramName);

        commonWaits.waitForElementVisible(scriptParamValueInput);
        commonWaits.keyClearAndType(scriptParamValueInput, value);
        log.info("Typed value '" + value + "' for parameter: " + paramName);

        commonWaits.clickWithoutInterception(scriptParamCheckButton);
        log.info("Clicked OK button for parameter: " + paramName);
        return this;
    }

    /**
     * Clicks the Save button on the Configuration Script Parameters modal.
     * Fails the test immediately if Save is not enabled within 10 seconds.
     */
    public BHACLandingPage saveScriptParameters() {
        log.info("Clicking Save on Configuration Script Parameters modal.");
        try {
            commonWaits.clickWithoutInterception(confirmContinueButton);
            commonWaits.waitForPageLoad(driver);
            log.info("Script parameters saved successfully.");
        } catch (Exception e) {
            Assert.fail("Save button on Configuration Script Parameters modal was not enabled. " + e.getMessage());
        }
        return this;
    }

    /**
     * Types the given asset name into the vulnerability search box, clicks Search,
     * waits up to 15 seconds for results to appear, then clicks the matching asset row.
     * Fails the test if no results appear or no row matches the asset name.
     *
     * @param assetName the asset name to search for and select
     */
    public BHACLandingPage searchAndSelectVulnerability(String assetName) {
        log.info("Searching for asset: " + assetName);
        commonWaits.keyClearAndType(vulnerabilitySearchInput, assetName);
     
        log.info("Clicking Search button.");
        commonWaits.clickWithoutInterception(vulnerabilitySearchButton);

        log.info("Waiting for vulnerability results table to populate.");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(d -> !vulnerabilityResultRows.isEmpty());
        } catch (Exception e) {
            Assert.fail("Vulnerability results table did not load within 15 seconds for asset: " + assetName);
        }

        String rowXpath = String.format(vulnerabilityAssetRowXpath, assetName);
        WebElement assetRow = commonWaits.waitForByElementToBeVisible(By.xpath(rowXpath));
        log.info("Clicking asset row: " + assetName);
        commonWaits.clickWithoutInterception(assetRow);
        commonWaits.waitForPageLoad(driver);
        log.info("Asset row selected: " + assetName);
        return this;
    }

    /**
     * Opens the vulnerability selector Advanced filter docked panel (tertiary "Advanced filter" control).
     */
    public BHACLandingPage openVulnerabilityAdvancedFilterPanel() {
        log.info("Opening Advanced filter on vulnerability selector.");
        commonWaits.clickWithoutInterception(vulnerabilitySelectorAdvancedFilterButton);
        commonWaits.waitForByElementToBeVisible(By.xpath(vulnerabilityAdvanceFiltersPanelXpath));
        commonWaits.waitForPageLoad(driver);
        log.info("Advanced filter panel is visible.");
        return this;
    }

    /**
     * In the Advanced filter panel, clicks a search criterion row by its visible label
     * (e.g. {@code "Asset"}, {@code "CVE ID"}, {@code "Severity"}).
     *
     * @param criterionLabel exact text shown on the criterion row in the panel
     */
    public BHACLandingPage selectAdvancedFilterCriterion(String criterionLabel) {
        log.info("Selecting Advanced filter criterion: " + criterionLabel);
        String xpath = String.format(advancedFilterCriterionRowXpath,
                CommonMethods.escapeSingleQuotesForXPath(criterionLabel));
        WebElement row = commonWaits.waitForByElementToBeVisible(By.xpath(xpath));
        row.click();
        commonWaits.waitForPageLoad(driver);
        log.info("Clicked Advanced filter criterion: " + criterionLabel);
        return this;
    }

    /**
     * Generic Advanced filter flow: opens the panel, selects a criterion (e.g. Asset, Vulnerability Name),
     * types in the child search field, selects the checkbox whose label matches {@code optionLabelText},
     * clicks Done, then Apply. Same DOM path for Asset and Vulnerability Name child panels.
     *
     * @param criterionLabel row label in the main Advanced filter list (e.g. {@code "Asset"}, {@code "Vulnerability Name"})
     * @param optionLabelText exact text on the checkbox label (search string may differ if UI truncates; match the checkbox span)
     */
    public BHACLandingPage applyAdvancedFilterSelectOption(String criterionLabel, String optionLabelText) {
        if (criterionLabel == null || criterionLabel.trim().isEmpty()) {
            Assert.fail("applyAdvancedFilterSelectOption requires a non-empty criterion label.");
        }
        if (optionLabelText == null || optionLabelText.trim().isEmpty()) {
            Assert.fail("applyAdvancedFilterSelectOption requires a non-empty option label.");
        }
        String criterion = criterionLabel.trim();
        String optionText = optionLabelText.trim();
        log.info("Advanced filter: criterion='" + criterion + "', option='" + optionText + "'");

        openVulnerabilityAdvancedFilterPanel();
        selectAdvancedFilterCriterion(criterion);
        commonWaits.waitForElementVisible(advancedFilterChildPanelBody);
        keyClearAndType(advancedFilterChildSearchInput, optionText);
        selectAdvancedFilterChildCheckbox(optionText);
        log.info("Selected Advanced filter checkbox for option label: " + optionText);

        commonWaits.clickWithoutInterception(advancedFilterChildDoneButton);
        commonWaits.waitForPageLoad(driver);

        commonWaits.clickWithoutInterception(advancedFilterApplyButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Advanced filter Apply clicked (criterion='" + criterion + "').");
        return this;
    }

    /**
     * Same as {@link #applyAdvancedFilterSelectOption(String, String)} with criterion {@code "Asset"}.
     *
     * @param assetName exact asset name as shown on the checkbox label
     */
    public BHACLandingPage applyAdvancedFilterSelectAsset(String assetName) {
        return applyAdvancedFilterSelectOption("Asset", assetName);
    }

    /**
     * Advanced filter for {@code "Vulnerability Name"}: search and select one vulnerability (e.g. {@code Java-Important/Sec}).
     *
     * @param vulnerabilityName exact name as shown on the checkbox label
     */
    public BHACLandingPage applyAdvancedFilterSelectVulnerabilityName(String vulnerabilityName) {
        return applyAdvancedFilterSelectOption("Vulnerability Name", vulnerabilityName);
    }

    /**
     * Locator for the child-panel checkbox input whose visible label matches {@code optionLabelText}.
     */
    private By advancedFilterChildOptionCheckboxBy(String optionLabelText) {
        String xpath = String.format(advancedFilterChildOptionCheckboxInputXpath,
                CommonMethods.escapeSingleQuotesForXPath(optionLabelText));
        return By.xpath(xpath);
    }

    private By advancedFilterChildOptionLabelBy(String optionLabelText) {
        String xpath = String.format(advancedFilterChildOptionLabelXpath,
                CommonMethods.escapeSingleQuotesForXPath(optionLabelText));
        return By.xpath(xpath);
    }

    /**
     * Selects the child-panel option checkbox. The native {@code input} is often not "clickable" in Selenium's sense
     * (hidden for styling), so we wait for {@linkplain ExpectedConditions#presenceOfElementLocated(By) presence},
     * scroll, then use JavaScript click on the input and fall back to clicking the visible {@code label}.
     * Retries on {@link StaleElementReferenceException} when Angular rebuilds the overlay between steps.
     */
    private void selectAdvancedFilterChildCheckbox(String optionText) {
        By checkboxBy = advancedFilterChildOptionCheckboxBy(optionText);
        By labelBy = advancedFilterChildOptionLabelBy(optionText);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        final int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                selectAdvancedFilterChildCheckboxOnce(optionText, checkboxBy, labelBy, js);
                return;
            } catch (StaleElementReferenceException e) {
                log.info("Stale element while selecting Advanced filter checkbox (attempt " + attempt + "/" + maxAttempts
                        + "): " + e.getMessage());
                if (attempt == maxAttempts) {
                    throw e;
                }
            }
        }
    }

    private void selectAdvancedFilterChildCheckboxOnce(String optionText, By checkboxBy, By labelBy, JavascriptExecutor js) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.pollingEvery(Duration.ofMillis(400));

        WebElement input = commonWaits.waitForByElementToBeVisible(checkboxBy);
        js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", input);

        if (isAdvancedFilterCheckboxChecked(input, js)) {
            return;
        }

        js.executeScript("arguments[0].click();", input);

        input = wait.until(ExpectedConditions.presenceOfElementLocated(checkboxBy));
        if (!isAdvancedFilterCheckboxChecked(input, js)) {
            log.info("JS click on checkbox input did not check; clicking visible label for: " + optionText);
            WebElement label = commonWaits.waitForByElementToBeVisible(labelBy);
            js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", label);
            commonWaits.clickWithoutInterception(label);
            input = commonWaits.waitForByElementToBeVisible(checkboxBy);
        }

        if (!isAdvancedFilterCheckboxChecked(input, js)) {
            Assert.fail("Advanced filter: could not select checkbox for option: " + optionText);
        }
    }

    private static boolean isAdvancedFilterCheckboxChecked(WebElement input, JavascriptExecutor js) {
        return Boolean.TRUE.equals(js.executeScript("return arguments[0].checked;", input));
    }

    /**
     * Clicks the Next button on the Create Operation wizard.
     * If the button is disabled, waits up to 10 seconds for it to become enabled.
     * Fails the test immediately if the button remains disabled after the wait.
     */
    public BHACLandingPage clickNextButton() {
        log.info("Attempting to click Next button.");
        try {
            log.info("Next button is enabled. Clicking it.");
            commonWaits.clickWithoutInterception(nextButton);
            commonWaits.waitForPageLoad(driver);
            log.info("Successfully clicked Next button.");
        } catch (Exception e) {
            String msg = "Next button remained disabled after 10 seconds. Cannot proceed. " + e.getMessage();
            log.info(msg);
            Assert.fail(msg);
        }
        return this;
    }

    /**
     * Navigate to a main tab on the BHAC landing page.
     * @param tab the BHACTab enum value representing the target tab
     */
    public BHACLandingPage navigateToTab(BHACTab tab) {
        String tabName = tab.getTabName();
        log.info("Navigating to BHAC tab: " + tabName);
        String xpath = String.format(navTabXpath, tabName);
        WebElement tabElement = commonWaits.waitForByElementToBeVisible(By.xpath(xpath));
        commonWaits.waitForPageLoad(driver);
        commonWaits.clickWithoutInterception(tabElement);
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully navigated to tab: " + tabName);
        return this;
    }


    /**
     * Click the Import button on the Manage > Import page.
     */
    public BHACLandingPage clickImportButton() {
        log.info("Clicking Import button.");
        commonWaits.clickWithoutInterception(importButton);
        commonWaits.waitForPageLoad(driver);
        return this;
    }

    /**
     * Click the Create Operation button on the Operations list page.
     */
    public BHACLandingPage clickCreateOperationButton() {
        log.info("Clicking Create Operation button.");
        commonWaits.clickWithoutInterception(createOperationButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully clicked Create Operation button.");
        return this;
    }

    /**
     * Verifies the Create Operation window is open by checking the page heading.
     * Uses a fluent wait polling every 500ms for up to 20 seconds.
     *
     * @return true if the "Create Operation" heading is visible within 20 seconds
     */
    public boolean isCreateOperationWindowOpen() {
        log.info("Verifying Create Operation window is open.");
        try {
            WebDriverWait fluentWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            fluentWait.pollingEvery(Duration.ofMillis(500));
            fluentWait.ignoring(NoSuchElementException.class);
            fluentWait.ignoring(StaleElementReferenceException.class);
            fluentWait.until(ExpectedConditions.visibilityOf(createOperationHeading));
            log.info("Create Operation window is open. Heading text: '" + createOperationHeading.getText().trim() + "'");
            return true;
        } catch (Exception e) {
            log.info("Create Operation window did not open within 20 seconds. " + e.getMessage());
            return false;
        }
    }

    /**
     * Select a vendor from the Import Scan modal dropdown.
     * @param vendorName the vendor name to select (e.g. "Qualys", "Nessus", "Rapid7")
     */
    public BHACLandingPage selectVendor(String vendorName) {
        log.info("Selecting vendor: " + vendorName);
        commonWaits.clickWithoutInterception(vendorDropdown);
        commonWaits.waitForPageLoad(driver);
        String xpath = String.format(vendorOptionXpath, vendorName);
        WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(xpath));
        commonWaits.clickWithoutInterception(option);
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully selected vendor: " + vendorName);
        return this;
    }

    /**
     * Check if a file is already imported in the BHAC import scan list.
     * @param fileName the file name to search for (e.g. "Qualys_RHEL_CVEs.xml")
     * @return true if the file is found in the import list, false otherwise
     */
    public boolean isFileImportedInBHAC(String fileName) {
        log.info("Checking if file is already imported: " + fileName);
        commonWaits.waitForPageLoad(driver);
        String xpath = String.format(importedFileXpath, fileName);
        List<WebElement> matchingRows = driver.findElements(By.xpath(xpath));
        boolean isImported = !matchingRows.isEmpty();
        log.info("File '" + fileName + "' imported status: " + isImported);
        return isImported;
    }

    /**
     * Delete an imported file from the BHAC import scan list by clicking Action > Remove.
     * @param fileName the file name to delete (e.g. "Qualys_RHEL_CVEs.xml")
     */
    public BHACLandingPage deleteImportedFile(String fileName) {
        log.info("Deleting imported file: " + fileName);
        String xpath = String.format(actionButtonByFileXpath, fileName);
        WebElement actionButton = commonWaits.waitForByElementToBeVisible(By.xpath(xpath));
        commonWaits.clickWithoutInterception(actionButton);
        commonWaits.waitForPageLoad(driver);
        commonWaits.clickWithoutInterception(removeMenuOption);
        commonWaits.waitForPageLoad(driver);
        commonWaits.clickWithoutInterception(confirmContinueButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully deleted imported file: " + fileName);
        return this;
    }

    /**
     * Select operating systems from the OS filter dropdown in the Import Scan modal.
     * Clears existing selections first, then selects each specified OS.
     * @param osNames the OS names to select (e.g. "Windows", "Linux", "Others")
     */
    public BHACLandingPage selectOperatingSystem(String... osNames) {
        log.info("Selecting operating systems: " + String.join(", ", osNames));
        commonWaits.clickWithoutInterception(osFilterDropdown);
        commonWaits.waitForPageLoad(driver);
        commonWaits.clickWithoutInterception(clearButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Cleared OS filter selections.");
        commonWaits.clickWithoutInterception(selectAllButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Selected all OS filter selections.");
        new Actions(driver).sendKeys(Keys.ESCAPE).build().perform();
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully selected operating systems.");
        return this;
    }

    /**
     * Select all severity levels from the Severity filter dropdown.
     * Opens dropdown, clicks "Select all", then closes dropdown.
     */
    public BHACLandingPage selectAllSeverity() {
        log.info("Selecting all severity levels.");
        commonWaits.clickWithoutInterception(severityFilterDropdown);
        commonWaits.waitForPageLoad(driver);
        commonWaits.clickWithoutInterception(severitySelectAllButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully selected all severity levels.");
        return this;
    }

    /**
     * Click the Attach file button on the Import Scan modal.
     */
    public BHACLandingPage clickAttachFileButton() {
        log.info("Clicking Attach file button.");
        commonWaits.clickWithoutInterception(attachFileButton);
        commonWaits.waitForPageLoad(driver);
        return this;
    }

    /**
     * Click the Import button on the Import Scan modal footer.
     */
    public BHACLandingPage clickModalImportButton() {
        log.info("Clicking Import button on modal.");
        commonWaits.clickWithoutInterception(modalImportButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully clicked Import button on modal.");
        return this;
    }

    /**
     * Verifies that the operations table has at least one data row and that the
     * column headers for "Type" and "Status" are present.
     *
     * @return true if at least 1 row is present and both Type and Status headers exist
     */
    public boolean isOperationsListed() {
        log.info("Checking if operations are listed with Type and Status column headers.");
        commonWaits.waitForPageLoad(driver);

        if (operationsTableRows.isEmpty()) {
            log.info("No operation rows found in the table.");
            return false;
        }
        log.info("Found " + operationsTableRows.size() + " operation row(s).");

        if (!operationsTypeHeader.isDisplayed()) {
            log.info("Type column header not found.");
            return false;
        }
        if (!operationsStatusHeader.isDisplayed()) {
            log.info("Status column header not found.");
            return false;
        }

        log.info("Operations table verified: " + operationsTableRows.size() + " row(s) present with Type and Status headers.");
        return true;
    }

    public boolean searchOperationAndVerifyStatus(String operationName, String expectedStatus) {
        log.info("Searching for operation: " + operationName);
        commonWaits.waitForElementClickable(operationsSearchInput);
        operationsSearchInput.clear();
        operationsSearchInput.sendKeys(operationName);

        log.info("Clicking Search button.");
        commonWaits.waitForElementClickable(operationsSearchButton);
        operationsSearchButton.click();
        commonWaits.waitForPageLoad(driver);

        String operationLinkXpath = String.format(operationNameLinkXpath, operationName);
        boolean recordExists = !driver.findElements(By.xpath(operationLinkXpath)).isEmpty();
        Assert.assertTrue(recordExists, "Operation '" + operationName + "' not found in the operations table after search.");
        log.info("Operation '" + operationName + "' found in the table.");

        String statusXpath = String.format(operationStatusByNameXpath, operationName);
        WebElement statusElement = commonWaits.waitForElementVisible(driver.findElement(By.xpath(statusXpath)));
        String actualStatus = statusElement.getText().trim();
        log.info("Operation '" + operationName + "' status: " + actualStatus);
        Assert.assertTrue(actualStatus.contains(expectedStatus),
                "Expected status '" + expectedStatus + "' but found '" + actualStatus + "' for operation '" + operationName + "'.");
        return true;
    }

    /**
     * Click the refresh button to refresh the data in the table
     */
    public BHACLandingPage clickRefreshButton() {
        log.info("Clicking refresh button");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement refreshButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.refresh-button, button[title='Refresh Data']")));
        refreshButton.click();
        commonWaits.waitForPageLoad(driver);
        log.info("Successfully clicked refresh button");
        return this;
    }

    /**
     * Check if a file should NOT appear in the BHAC import scan table
     * @param fileName the file name that should not be present
     * @return true if the file is NOT found (which is expected), false if the file is still present
     */
    public boolean isFileNotInTable(String fileName) {
        log.info("Checking if file should NOT appear in table: " + fileName);
        commonWaits.waitForPageLoad(driver);
        String xpath = String.format(importedFileXpath, fileName);
        List<WebElement> matchingRows = driver.findElements(By.xpath(xpath));
        boolean isNotPresent = matchingRows.isEmpty();
        log.info("File '" + fileName + "' not present status (expected): " + isNotPresent);
        return isNotPresent;
    }

    /**
     * Verify that a file does not appear in the table with retry logic and refresh functionality
     * @param fileName the file name that should not be present
     * @param maxRetries maximum number of retry attempts (default: 10)
     * @param waitTimeSeconds wait time between retries in seconds (default: 2)
     * @return true if the file is not found after retries, false if it's still present
     */
    public boolean verifyFileNotInTableWithRetry(String fileName, int maxRetries, int waitTimeSeconds) {
        log.info("Starting verification that file '" + fileName + "' should not appear in table");
        log.info("Max retries: " + maxRetries + ", Wait time between retries: " + waitTimeSeconds + " seconds");
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            log.info("Attempt " + attempt + " of " + maxRetries);
            
            // Check if file is not in table
            if (isFileNotInTable(fileName)) {
                log.info("SUCCESS: File '" + fileName + "' is not present in table on attempt " + attempt);
                return true;
            }
            
            log.info("File '" + fileName + "' is still present in table. Refreshing and retrying...");
            
            // If not the last attempt, refresh and wait
            if (attempt < maxRetries) {
                try {
                    clickRefreshButton();
                    Thread.sleep(waitTimeSeconds * 1000);//wait file to appear in table
                } catch (InterruptedException e) {
                    log.error("Thread interrupted during wait: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        log.error("FAILED: File '" + fileName + "' is still present in table after " + maxRetries + " attempts");
        return false;
    }

    /**
     * Overloaded method with default retry parameters (10 retries, 2 seconds wait)
     * @param fileName the file name that should not be present
     * @return true if the file is not found after retries, false if it's still present
     */
    public boolean verifyFileNotInTableWithRetry(String fileName) {
        return verifyFileNotInTableWithRetry(fileName, 10, 2);
    }
}
