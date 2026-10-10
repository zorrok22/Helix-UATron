package pages.ITSM.bwf;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BWFCaseTemplate extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(BWFCaseTemplate.class.getName());
    CommonWaits commonWaits;

    public BWFCaseTemplate() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    // String locators
    public String TEMPLATE_TABLE_ROW = "//tr[contains(@class,'at-data-row')]";
    public String TEMPLATE_TABLE_CELL = "//tr[contains(@class,'at-data-row')][%d]//td[contains(@class,'at-data-cell')][%d]";
    public String TEMPLATE_TABLE_LINK = "//tr[contains(@class,'at-data-row')][%d]//td[contains(@class,'at-data-cell')][%d]//a";
    public String COLUMN_HEADER = "//th[contains(@class,'at-header-cell')]";
    public String DROPDOWN_OPTION = "//button[contains(@class,'dropdown-item') and contains(normalize-space(.),'%s')]";
    
    // By locators (for relative or dynamic elements)
    public By TREE_EXPAND_BUTTON = By.xpath(".//ancestor::div[contains(@class,'a-tree__content')]//span[contains(@class,'a-tree__toggle')]");
    
    // WebElement locators
    @FindBy(xpath = "//rx-record-editor[contains(@class,'bwf-template-metadata-editor')]//button[@btn-type='tertiary' and @size='small' and contains(normalize-space(.),'Edit')]")
    WebElement editButton;
    
    @FindBy(xpath = "//rx-record-editor//div[@class='d-flex justify-content-end']//button[@btn-type='tertiary' and @size='small' and contains(.,'Edit')]")
    WebElement editTemplateButton;
    
    @FindBy(xpath = "//button[contains(@class,'btn-primary') and contains(@class,'btn-lg') and normalize-space(.)='Save']")
    WebElement saveTemplateButton;
    
    @FindBy(xpath = "//button[contains(., 'Back')]")
    WebElement backButton;
    
    @FindBy(xpath = "//rx-record-editor[contains(@class,'bwf-template-metadata-editor')]//button[contains(@class,'d-icon-left-check_adapt') and contains(.,'Save')]")
    WebElement saveButton;
    
    @FindBy(xpath = "//rx-action-button[contains(@class,'rx-action-button_secondary')]//button[contains(@class,'btn-secondary') and normalize-space(.)='Cancel']")
    WebElement cancelButton;

    @FindBy(xpath = "//rx-action-button[contains(@class,'rx-action-button_secondary')]//button[contains(@class,'btn-secondary') and normalize-space(.)='Cancel']")
    WebElement cancelButtonOnTemplateMetadataPage;
    
    
    @FindBy(xpath = "//div[@aria-label='Grid']//input[contains(@class,'adapt-rx-search__input')]")
    WebElement searchInput;
    
    @FindBy(xpath = "//rx-record-editor[contains(@class,'bwf-template-metadata-editor')]//rx-selection-field[not(ancestor::*[@hidden])]//label[contains(.,'Template Status')]/ancestor::adapt-rx-select//button[@role='combobox']")
    WebElement templateStatusDropdown;

    @FindBy(xpath = "//label[normalize-space()='Template Status']/following-sibling::div[contains(@class,'read-only-content')]")
    WebElement templateStatusReadOnly;
    
    @FindBy(xpath = "//label[contains(.,'Allow Case Reopen')]/ancestor::adapt-rx-select//button[@role='combobox']")
    WebElement allowCaseReopenDropdown;
    
    @FindBy(xpath = "//span[contains(text(), 'Saved successfully')]")
    WebElement successMessage;
    
    @FindBy(xpath = "//a[@href='#/com.bmc.dsm.bwfa/settings']")
    WebElement settingsIcon;    
    
    @FindBy(xpath = "//input[contains(@class,'rx-search__input')]")
    WebElement settingsSearchBox;
    
    @FindBy(xpath = "//div[@role='treeitem'][@aria-label='Case Management']")
    WebElement caseManagementTreeItem;
    
    @FindBy(xpath = "//div[@role='treeitem'][@aria-label='Templates']")
    WebElement templatesTreeItem;
    
    @FindBy(xpath = "//label[contains(., 'Line of Business')]/ancestor::adapt-rx-select//button | //bwf-lob-selection//button")
    WebElement lineOfBusinessDropdown;
    
    /**
     * Navigate to Case Management Templates
     */
    public BWFCaseTemplate navigateToCaseManagementTemplates() {
        log.info("Navigating to Case Management -> Templates");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        // Click on Settings/Gear icon
        clickWithoutInterception(settingsIcon);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        // Search for Case Management
        commonWaits.waitForElementVisible(settingsSearchBox, 10);
        settingsSearchBox.clear();
        settingsSearchBox.sendKeys("Case Management");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        // Find Case Management tree item and click expand button
        commonWaits.waitForElementVisible(caseManagementTreeItem, 10);
        WebElement expandButton = caseManagementTreeItem.findElement(TREE_EXPAND_BUTTON);
        
        // Check if already expanded
        String ariaExpanded = caseManagementTreeItem.getAttribute("aria-expanded");
        if (!"true".equals(ariaExpanded)) {
            clickWithoutInterception(expandButton);
        }
        
        // Wait for Templates child item to become visible after expansion
        commonWaits.waitForElementVisible(templatesTreeItem, 10);
        clickWithoutInterception(templatesTreeItem);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        log.info("Successfully navigated to Templates");
        return this;
    }
    
    /**
     * Set Line of Business filter
     * @param lob Line of Business name
     */
    public BWFCaseTemplate setLineOfBusiness(String lob) {
        log.info("Setting Line of Business: " + lob);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        // Click on LOB dropdown
        commonWaits.waitForElementVisible(lineOfBusinessDropdown, 10);
        commonWaits.clickWithoutInterception(lineOfBusinessDropdown);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        // Select LOB
        commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_OPTION, lob)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        log.info("Set Line of Business to: " + lob);
        return this;
    }
    
    /**
     * Search and open template by name
     * @param templateName Template name to search and open
     */
    public BWFCaseTemplate searchAndOpenTemplate(String templateName) {
        log.info("Searching for template: " + templateName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        // Search for template
        if (commonWaits.isElementPresent(searchInput)) {
            searchInput.clear();
            searchInput.sendKeys(templateName);
            searchInput.sendKeys(Keys.ENTER);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        
        // Get column names
        List<WebElement> headers = findElements(By.xpath(COLUMN_HEADER));
        int nameColumnIndex = -1;
        
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().contains("Template Name") || headers.get(i).getText().trim().contains("Name")) {
                nameColumnIndex = i + 1;
                break;
            }
        }
        
        if (nameColumnIndex == -1) {
            throw new RuntimeException("Could not find Template Name column");
        }
        
        // Find and click on template
        List<WebElement> rows = findElements(By.xpath(TEMPLATE_TABLE_ROW));
        for (int i = 1; i <= rows.size(); i++) {
            WebElement cell = commonWaits.waitForByElementToBeVisible(
                    By.xpath(String.format(TEMPLATE_TABLE_CELL, i, nameColumnIndex)));

            if (cell.getText().trim().equals(templateName)) {
                log.info("Found template at row " + i);
                WebElement link = commonWaits.waitForByElementToBeVisible(
                        By.xpath(String.format(TEMPLATE_TABLE_LINK, i, nameColumnIndex)));
                commonWaits.clickWithoutInterception(link);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                log.info("Opened template: " + templateName);
                return this;
            }
        }
        
        throw new RuntimeException("Template not found: " + templateName);
    }
    
    /**
     * Click Edit button on template view page
     */
    public BWFCaseTemplate clickEditOnTemplate() {
        log.info("Clicking Edit button on template view");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(editTemplateButton, 10);
        commonWaits.clickWithoutInterception(editTemplateButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Clicked Edit button on template");
        return this;
    }
    
    /**
     * Click Edit button on template metadata section
     */
    public BWFCaseTemplate clickEditOnTemplateMetadata() {
        log.info("Clicking Edit button on template metadata");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(editButton);
        commonWaits.clickWithoutInterception(editButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Clicked Edit button on metadata");
        return this;
    }
    
    /**
     * Set Template Status
     * @param status Status value (Draft, Active, Retired)
     */
    public BWFCaseTemplate setTemplateStatus(String status) {
        log.info("Setting Template Status to: " + status);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        commonWaits.clickWithoutInterception(templateStatusDropdown);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_OPTION, status)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        log.info("Set Template Status to: " + status);
        return this;
    }
    
    /**
     * Get Template Status
     */
    public String getTemplateStatus() {
        log.info("Getting Template Status");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        String status = commonWaits.waitForElementVisible(templateStatusReadOnly).getText().trim();
        log.info("Template Status is: " + status);
        return status;
    }

    /**
     * Set Allow case reopen
     * @param value Yes or No
     */
    public BWFCaseTemplate setAllowCaseReopen(String value) {
        log.info("Setting Allow case reopen to: " + value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.scrollToBottomOfPage();
        commonWaits.clickWithoutInterception(allowCaseReopenDropdown);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_OPTION, value)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        log.info("Set Allow case reopen to: " + value);
        return this;
    }
    
    /**
     * Click Save button on template metadata section
     */
    public BWFCaseTemplate SaveTemplateMetadata() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (commonWaits.waitForElementVisible(saveButton,30).isEnabled()) {
            log.info("Clicking Save button on template metadata");
            commonWaits.clickWithoutInterception(saveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        } else {
            
            log.info("Save button is disabled, clicking on cancel button");
            commonWaits.clickWithoutInterception(cancelButtonOnTemplateMetadataPage);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        return this;
    }
    
    /**
     * Click Save button on template edit page
     */
    public BWFCaseTemplate saveTemplate() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (commonWaits.waitForElementVisible(saveTemplateButton, 30).isEnabled()) {
            log.info("Clicking Save button on template");
            commonWaits.clickWithoutInterception(saveTemplateButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Template saved successfully");
        } else {
            log.info("Save button is disabled, skipping click");
        }
        return this;
    }
    
    /**
     * Click Back button
     */
    public BWFCaseTemplate clickBack() {
        log.info("Clicking Back button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(backButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Clicked Back button");
        return this;
    }
    
    /**
     * Click Cancel button
     */
    public BWFCaseTemplate clickCancel() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (commonWaits.isElementPresent(cancelButton, 5)) {
            log.info("Clicking Cancel button");
            commonWaits.clickWithoutInterception(cancelButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Clicked Cancel button successfully");
        } else {
            log.info("Cancel button not available, skipping");
        }
        return this;
    }

    /**
     * Extracts the current template record id from the URL.
     * NOTE: Used as a UI-based alternative to Umbrella API helpers for template-id discovery.
     */
    public String getTemplateRecordIdFromCurrentUrl() {
        String url = getDriver().getCurrentUrl();
        if (url == null || url.trim().isEmpty()) {
            throw new AssertionError("Current URL is empty; cannot extract template id.");
        }
        // BWF record ids are typically long base32/base64-like tokens. Prefer the longest token.
        Pattern p = Pattern.compile("([A-Z0-9]{20,})");
        String upperUrl = url.toUpperCase();
        Matcher m = p.matcher(upperUrl);
        String best = null;
        while (m.find()) {
            String token = m.group(1);
            if (best == null || token.length() > best.length()) {
                best = token;
            }
        }
        Assert.assertTrue(best != null && !best.trim().isEmpty(),
                "Unable to extract template record id from URL: " + url);
        log.info("Extracted template record id from URL: " + best);
        return best;
    }
}