package pages.ITSM.DWPC;

import java.time.Duration;
import java.io.File;
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

public class DWPCServicesPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    // Dynamic locators
    public String serviceSearchResult = "//div[contains(@class, 'ui-grid-cell-contents')]//span[normalize-space()='%s']";
    String serviceVersionOptionXpath = "//*[contains(@class, 'sb-service-options-version_options')]//span[text()='%s']/following-sibling::*";
    String optionTitleXpath = "//h3[@class='sb-blade-title' and text()='%s']";
    public String editButtonBySection = "//span[contains(text(),'%s')]/following-sibling::button[contains(@class,'d-link')]";
    public String dropdownOptionXpath = "//button[@type='button' and contains(@class,'ui-select-choices-row-inner')]//span[contains(text(),'%s')]";
    public String selectedDropdownValue = "//span[contains(@class,'sb-reopen-workflow-reopen-select__label')]//following-sibling::span[contains(@class,'ng-binding')]";
    public String sectionPanelXpath = "//div[contains(@class,'sb-blade')]//span[contains(text(),'%s')]";
    static final String NG_EMPTY = "ng-empty";
    public String scriptToMakeFileElementVisible = "document.querySelector('input[type=file]').setAttribute('style', 'display:inline !important; visibility:visible !important');";
    String serviceEditButton = "//*[contains(@class, 'sb-service-options_component-action') and contains(normalize-space(.), '%s')]//button";
    String expressionEditBtn = "//rx-inspector-expression-node-field[contains(normalize-space(.), '%s')]//a[text()='Edit']";
    String sbAdapterFrame = "//iframe[@class='sb-adapter-frame']";

    //WebElements Locators
    @FindBy(xpath = "//*[@class='sb-attach-workflow-action' and contains(normalize-space(.), 'Workflow')]//button[text()='Edit']")
    WebElement workFlowEdit;

    @FindBy(xpath = "//rx-designer-canvas//*[text()='Create Remedy Work Order']")
    WebElement workFlowCanvas;

    @FindBy(xpath = "//*[contains(@class, 'd-icon-gear')]/ancestor::a[contains(@class, 'rx-blade-tab')]")
    WebElement elementPropertiesTab;

    @FindBy(xpath = "//rx-rich-expression-editor//*[@role='textbox']")
    WebElement expressionEditorTextBox;

    @FindBy(xpath = "//button[text()='OK']")
    WebElement expressionEditorOkBtn;

    @FindBy(xpath = "//rx-process-designer//button[text()='Save']")
    WebElement processDesignerSaveBtn;

    @FindBy(xpath = "//rx-process-designer//button[text()='Close']")
    WebElement processDesignerCloseBtn;

    @FindBy(xpath = "//*[contains(@class, 'sb-alert-success') and contains(normalize-space(.), 'Process Definition saved successfully')]")
    WebElement processSavedSuccessMsg;

    @FindBy(xpath = "//span[text()='My Services']")
    WebElement myServicesTab;

    @FindBy(xpath = "//input[@placeholder='Search']")
    WebElement searchBox;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Actions')]")
    WebElement actionsDropdownButton;

    @FindBy(xpath = "//*[@class='sb-grid-action-dropdown-button ' and normalize-space()='Open']")
    WebElement openButtonElement;

    @FindBy(xpath = "//*[text()='Collaborator delegation']/parent::div//input")
    WebElement collaboratorDelegationInputXpath;

    @FindBy(xpath = "//*[text()='Collaborator delegation']/parent::div//input/following-sibling::*[@class='switch__slider']")
    WebElement collaboratorDelegationToggleXpath;

    @FindBy(xpath = "//button[text()='Save']")
    WebElement saveBtnXpath;

    @FindBy(xpath = "//button[text()='Close']")
    WebElement closeBtnXpath;

    @FindBy(xpath = "//button[text()='Cancel']")
    WebElement cancelBtnXpath;

    @FindBy(xpath = "//div[contains(@class,'sb-blade') and contains(@class,'sb-service-options-blade')]")
    WebElement serviceOptionsPanel;

    @FindBy(xpath = "//span[contains(@class,'sb-reopen-workflow-reopen-select')]//button[contains(@class,'ui-select-toggle')]")
    WebElement reopenProcessDropdown;

    @FindBy(xpath = "//span[contains(@class,'sb-reopen-workflow-reopen-select')]//span[@ng-bind='$select.selected']")
    WebElement selectedDropdownText;

    @FindBy(xpath = "//button[@type='submit' and contains(@class,'d-button_primary')]")
    WebElement saveButton;

    @FindBy(xpath = "//button[contains(@class,'d-button_secondary') and @ng-click='cancel()']")
    WebElement cancelButton;

    @FindBy(xpath = "//button[@ng-click='close()']")
    WebElement closeButton;

    @FindBy(xpath = "//button[contains(@class,'sb-grid-button')]//span[text()='Import From File']")
    WebElement importFromFileButton;

    @FindBy(xpath = "//input[@type='file' and contains(@class,'sb-file-select-delegate')]")
    WebElement fileUploadInput;

    @FindBy(xpath = "//label[.//input[@name='serviceAndBundle' and @value='SOURCE']]")
    WebElement sourceFileStatusRadio;

    @FindBy(xpath = "//label[.//input[@name='workflows' and @value='true']]")
    WebElement overwriteWorkflowsRadio;

    @FindBy(xpath = "//label[.//input[@name='document' and @value='true']]")
    WebElement overwriteDocumentRadio;

    @FindBy(xpath = "//label[.//input[@name='categories' and @value='ALL_CATEGORIES']]")
    WebElement importAllCategoriesRadio;

    @FindBy(xpath = "//button[@sb-i18n='com.bmc.sb.common.button.confirm']")
    WebElement confirmImportButton;

    public DWPCServicesPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(DWPCServicesPage.class.getName());
        commonWaits = new CommonWaits();
    }

    public DWPCServicesPage verifyServicesPageLaunchedSuccessfully() {
        commonWaits.waitForElementVisible(myServicesTab);
        Assert.assertTrue(commonWaits.isElementPresent(myServicesTab), "Services Page is not launched successfully");
        return this;
    }

    public String searchForService(String serviceName) {
        log.info("Searching for service: " + serviceName);
        commonWaits.waitForElementClickable(searchBox).click();
        searchBox.clear();
        typeKeys(searchBox, serviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(serviceSearchResult, serviceName)), 5)) {
            WebElement resultElement = commonWaits.waitForByElementToBeVisible(
                    By.xpath(String.format(serviceSearchResult, serviceName)));
            String foundService = resultElement.getText();
            log.info("Service found: " + foundService);
            return foundService;
        } else {
            log.info("Service not found: " + serviceName);
            Assert.fail("Service not found: " + serviceName);
            return null;
        }
    }

    public DWPCServicesPage doubleClickOnService(String serviceName) {
        log.info("Double-clicking on service: " + serviceName);
        WebElement serviceElement = commonWaits.waitForByElementToBeVisible(
                By.xpath(String.format(serviceSearchResult, serviceName)));
        new Actions(getDriver()).doubleClick(serviceElement).perform();
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public DWPCServicesPage verifyServiceOptionsPanelOpened() {
        log.info("Verifying Service Options panel is opened");
        commonWaits.waitForElementVisible(serviceOptionsPanel);
        Assert.assertTrue(serviceOptionsPanel.isDisplayed(), "Service Options panel is not displayed");
        return this;
    }

    public DWPCServicesPage clickEditForSection(String sectionName) {
        log.info("Clicking Edit for section: " + sectionName);
        WebElement editButton = commonWaits.waitForByElementToBeVisible(
                By.xpath(String.format(editButtonBySection, sectionName)));
        commonWaits.clickWithoutInterception(editButton);
        commonWaits.waitForPageFullyLoaded(30, 3);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(sectionPanelXpath, sectionName)));
        return this;
    }

    public String getSelectedDropdownValue() {
        log.info("Getting currently selected dropdown value");
        commonWaits.waitForPageFullyLoaded(30, 3);
        commonWaits.waitForElementVisible(reopenProcessDropdown);
        String currentValue = "";
        if (commonWaits.isElementPresent(selectedDropdownText)) {
            currentValue = selectedDropdownText.getText().trim();
        } else {
            currentValue = reopenProcessDropdown.getText().trim();
        }
        log.info("Current value: " + currentValue);
        return currentValue;
    }

    public DWPCServicesPage selectDropdownOption(String optionValue) {
        log.info("Selecting dropdown option: " + optionValue);
        commonWaits.waitForElementVisible(reopenProcessDropdown);
        commonWaits.clickWithoutInterception(reopenProcessDropdown);
        commonWaits.waitForPageFullyLoaded(30, 3);

        WebElement option = commonWaits.waitForByElementToBeVisible(
                By.xpath(String.format(dropdownOptionXpath, optionValue)));
        commonWaits.clickWithoutInterception(option);
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public DWPCServicesPage clickSave() {
        log.info("Clicking Save");
        commonWaits.waitForElementVisible(saveButton);
        commonWaits.clickWithoutInterception(saveButton);
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public DWPCServicesPage clickCancel() {
        log.info("Clicking Cancel");
        if (commonWaits.isElementPresent(cancelButton)) {
            commonWaits.clickWithoutInterception(cancelButton);
            commonWaits.waitForPageFullyLoaded(30, 3);
        }
        return this;
    }

    public DWPCServicesPage clickClose() {
        log.info("Clicking Close");
        if (commonWaits.isElementPresent(closeButton)) {
            commonWaits.clickWithoutInterception(closeButton);
            commonWaits.waitForPageFullyLoaded(30, 3);
        }
        return this;
    }

    public DWPCServicesPage configureServiceOption(String serviceName, String sectionName, String expectedValue) {
        log.info("Configuring " + sectionName + " for service: " + serviceName + " with value: " + expectedValue);

        searchForService(serviceName);
        doubleClickOnService(serviceName);
        verifyServiceOptionsPanelOpened();
        clickEditForSection(sectionName);

        String currentValue = getSelectedDropdownValue();

        if (currentValue.equalsIgnoreCase(expectedValue) || currentValue.contains(expectedValue)) {
            log.info("Correct value already selected: " + currentValue);
            clickCancel();
            clickClose();
        } else {
            log.info("Updating value from '" + currentValue + "' to '" + expectedValue + "'");
            selectDropdownOption(expectedValue);
            clickSave();
            clickClose();
        }
        log.info("Configuration completed for service: " + serviceName);
        return this;
    }

    public DWPCServicesPage validateServiceNameCaseSensitivity(String serviceName) {
        log.info("Validating case sensitivity for Service Name: " + serviceName);

        String upperCaseService = serviceName.toUpperCase();
        String resultUpper = searchForService(upperCaseService);
        Assert.assertNotNull(resultUpper, "Service not found when searched in uppercase");
        Assert.assertTrue(resultUpper.contains(upperCaseService), "Service name mismatch in uppercase search result");
        log.pass("Service found successfully with uppercase search: " + resultUpper);

        String lowerCaseService = serviceName.toLowerCase();
        String resultLower = searchForService(lowerCaseService);
        Assert.assertNull(resultLower, "Service should NOT be found when searched in lowercase");
        log.pass("No result found with lowercase search as expected.");
        return this;
    }

    /**
     * Verify if service is displayed in the search results
     *
     * @param serviceName - The service name to verify
     * @return boolean - true if service is displayed, false otherwise
     */
    public boolean isServicePresent(String serviceName) {
        log.info("Searching for service: " + serviceName);
        commonWaits.waitForElementClickable(searchBox).click();
        searchBox.clear();
        typeKeys(searchBox, serviceName);
        commonWaits.waitForPageFullyLoaded(30, 3);
        log.info("Checking if service is displayed: " + serviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        By serviceLocator = By.xpath(String.format(serviceSearchResult, serviceName));
        boolean isDisplayed = commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceLocator, 20);
        log.info("Service displayed status: " + isDisplayed);
        return isDisplayed;
    }

    /**
     * Click on Import From File button
     *
     * @return DWPCServicesPage
     */
    public DWPCServicesPage clickImportFromFile() {
        log.info("Clicking on Import From File button");
        commonWaits.waitForElementVisible(importFromFileButton);
        commonWaits.clickWithoutInterception(importFromFileButton);
        log.info("Import From File button clicked successfully");
        return this;
    }

    /**
     * Upload service file
     *
     * @param filePath - The absolute path to the service file
     * @return DWPCServicesPage
     */
    public DWPCServicesPage uploadServiceFile(String filePath) {
        log.info("Uploading service file: " + filePath);
        File file1 = new File(filePath);

        // Make file input visible using JavaScript
        executeJS(scriptToMakeFileElementVisible);

        // Send file path directly to the file input element
        // This approach doesn't trigger the Windows file dialog
        fileUploadInput.sendKeys(file1.getAbsolutePath());
        log.info("File path sent to input: " + file1.getAbsolutePath());

        // Wait for page to load after file upload
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        log.info("Service file uploaded successfully");
        return this;
    }

    /**
     * Select import options in the confirmation modal
     * Selects: Source file statuses, Overwrite workflows, Overwrite documents, Import all categories
     *
     * @return DWPCServicesPage
     */
    public DWPCServicesPage selectImportOptions() {
        log.info("Selecting import options");

        // Select Source file statuses and attributes will be preserved
        clickByJS(sourceFileStatusRadio);
        log.info("Selected: Source file statuses and attributes will be preserved");

        // Select Overwrite workflows
        clickByJS(overwriteWorkflowsRadio);
        log.info("Selected: Overwrite workflows");

        // Select Overwrite documents
        clickByJS(overwriteDocumentRadio);
        log.info("Selected: Overwrite documents");

        // Select Import all service template categories
        clickByJS(importAllCategoriesRadio);
        log.info("Selected: Import all service template categories");

        log.info("All import options selected successfully");
        return this;
    }

    /**
     * Click Confirm button to complete the import
     *
     * @return DWPCServicesPage
     */
    public DWPCServicesPage confirmImport() {
        log.info("Clicking Confirm button to complete import");
        commonWaits.waitForElementVisible(confirmImportButton);
        commonWaits.clickWithoutInterception(confirmImportButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        log.info("Import confirmed successfully");
        return this;
    }

    /**
     * Complete method to import service file with all options
     *
     * @param filePath - The absolute path to the service file
     * @return DWPCServicesPage
     */
    public DWPCServicesPage importServiceFile(String filePath) {
        log.info("Starting service import process");
        uploadServiceFile(filePath);
        selectImportOptions();
        confirmImport();
        log.info("Service import process completed");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        refreshBrowser();
        return this;
    }

    public DWPCServicesPage openServiceOption(String serviceName) {
        searchForService(serviceName);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(serviceSearchResult, serviceName))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        commonWaits.waitForSometime(5000);
        commonWaits.waitForElementVisible(actionsDropdownButton, 5).click();
        commonWaits.waitForSometime(5000);
        commonWaits.waitForElementVisible(openButtonElement, 5).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        return this;
    }

    public DWPCServicesPage editServiceOption(String optionName) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(serviceVersionOptionXpath, optionName))).click();
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(optionTitleXpath, optionName))),
                "Service option '" + optionName + "' is not opened");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        return this;
    }

    public DWPCServicesPage setCollaborationDelegation(boolean enable) {
        if (enable) {
            if (collaboratorDelegationInputXpath.getAttribute("class").contains(NG_EMPTY)) {
                new Actions(getDriver()).click(collaboratorDelegationToggleXpath).pause(Duration.ofSeconds(5)).perform();
                commonWaits.waitForExecutionComplete();
            }
        } else {
            if (collaboratorDelegationInputXpath.getAttribute("class").contains(SolQAConstants.NOT_NG_EMPTY_CLASS)) {
                new Actions(getDriver()).click(collaboratorDelegationToggleXpath).pause(Duration.ofSeconds(5)).perform();
                commonWaits.waitForExecutionComplete();
            }
        }
        try {
            commonWaits.waitForElementClickable(saveBtnXpath).click();
            commonWaits.waitForElementClickable(closeBtnXpath).click();
        } catch (Exception e) {
            commonWaits.waitForElementClickable(cancelBtnXpath).click();
            commonWaits.waitForElementClickable(closeBtnXpath).click();
        }
        return this;
    }

    public DWPCServicesPage updateWorkOrderTemplateExpression(String templateName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        commonWaits.clickWithoutInterception(By.xpath(String.format(serviceEditButton, "Workflow")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        commonWaits.clickWithoutInterception(workFlowEdit);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);

        commonWaits.switchtoDefaultFrame();
        switchToFrame(By.xpath(sbAdapterFrame));
        commonWaits.clickWithoutInterception(workFlowCanvas);
        commonWaits.clickWithoutInterception(elementPropertiesTab);
        commonWaits.clickWithoutInterception(By.xpath(String.format(expressionEditBtn, "Template Name")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        commonWaits.clickWithoutInterception(expressionEditorTextBox);
        expressionEditorTextBox.clear();
        expressionEditorTextBox.sendKeys("\"" + templateName + "\"");
        commonWaits.clickWithoutInterception(expressionEditorOkBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        if (processDesignerSaveBtn.isEnabled()) {
            commonWaits.clickWithoutInterception(processDesignerSaveBtn);
        } else {
            commonWaits.clickWithoutInterception(processDesignerCloseBtn);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        }
        return this;
    }

}