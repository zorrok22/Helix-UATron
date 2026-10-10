package pages.ITOM.BHAC;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.CommonMethods;
import util.CommonWaits;
import util.SolqaLogger;

import java.time.Duration;
import java.util.List;

/**
 * Page object for BHAC Risk &gt; Vulnerabilities (unique vulnerabilities list).
 */
public class BHACRiskPage {

    private static final SolqaLogger log = new SolqaLogger(BHACRiskPage.class.getName());

    WebDriver driver;
    CommonWaits commonWaits;

    /** Dynamic XPath: format with {@link CommonMethods#escapeSingleQuotesForXPath(String)} vulnerability name. */
    String dataRowWithVulnNameXpath =
            "//adapt-table[@id='uniqueVulnerabilities-grid']//tbody/tr[contains(@class,'at-data-row') and not(contains(@class,'at-expansion-row'))]"
                    + "[.//span[contains(@class,'btn-content') and contains(normalize-space(),'%s')]]";

    /**
     * Map modal grid {@code #map-table}: row whose name cell matches remediation text.
     * Clicks the visible {@code label.radio__item} — the native {@code input} is not displayed (adapt-rx-radiobutton).
     */
    String remediationRowSelectLabelByContentXpath =
            "//adapt-table[@id='map-table']//tr[contains(@class,'at-data-row')]"
                    + "[.//span[contains(@class,'text-default') and contains(normalize-space(),'%s')]]"
                    + "//label[contains(@class,'radio__item')]";

    /** Relative to a unique-vulnerabilities data row {@code tr} (use with {@link #dataRowWithVulnNameXpath}). */
    private static final By UNIQUE_VULN_ROW_EXPAND_BUTTON = By.xpath(".//button[contains(@class,'expansion-btn')]");

    /** Relative to the same row {@code tr} as {@link #UNIQUE_VULN_ROW_EXPAND_BUTTON}. */
    private static final By UNIQUE_VULN_ROW_ACTION_BUTTON = By.xpath(".//button[contains(@class,'at-action-dropdown-toggle')]");

    /**
     * Impacted-assets child grid under an expanded unique-vulnerability row: first column is Asset Name
     * ({@code adapt-table_column-0_row-*} in DOM; table index in {@code data-testid} may vary).
     */
    private static final By EXPANSION_ROW_SIBLING = By.xpath("./following-sibling::tr[contains(@class,'at-expansion-row')][1]");

    /**
     * Relative to expansion row {@code tr}: Asset Name cell by {@code adapt-table_column-0_row} test id.
     * Format with {@link CommonMethods#escapeSingleQuotesForXPath(String)} asset name.
     */
    String impactedAssetNameCellByTestIdXpath =
            ".//td[contains(@data-testid,'adapt-table_column-0_row')][contains(normalize-space(),'%s')]";

    /**
     * Fallback under expansion row: first column of each data row.
     * Format with {@link CommonMethods#escapeSingleQuotesForXPath(String)} asset name.
     */
    String impactedAssetNameCellFirstColumnFallbackXpath =
            ".//tr[contains(@class,'at-data-row')]//td[1][contains(normalize-space(),'%s')]";

    /** Map vulnerability dialog: "All Targets" target scope radio (same locator as {@link #allTargetsRadio}). */
    private static final String ALL_TARGETS_RADIO_XPATH =
            "//div[@role='dialog']//input[@name='targetScope' and @value='ALL_TARGETS']";

    @FindBy(xpath = "//*[@role='tab' and contains(normalize-space(),'Vulnerabilities')]")
    List<WebElement> vulnerabilitiesSubTabs;

    @FindBy(xpath = "//div[contains(@class,'dp-body')]//button[.//span[contains(normalize-space(),'Map Remediation Content')]]")
    WebElement mapRemediationContentButton;

    /** Map vulnerability modal: remediation grid root ({@code adapt-table#map-table}). */
    @FindBy(xpath = "//adapt-table[@id='map-table']")
    WebElement mapTable;

    /** Search in Vulnerability Mapping modal map grid ({@code #map-table} toolbar). */
    @FindBy(xpath = "//adapt-table[@id='map-table']//input[contains(@data-testid,'rx-search') and contains(@data-testid,'_input')]")
    WebElement mapPanelRxSearchInput;

    @FindBy(xpath = ALL_TARGETS_RADIO_XPATH)
    WebElement allTargetsRadio;

    @FindBy(xpath = "//div[@role='dialog']//div[contains(@class,'manual-map-add')]//button[contains(@class,'btn-primary')][normalize-space()='Add']")
    WebElement manualMapAddButton;

    @FindBy(xpath = "//div[@role='dialog']//div[contains(@class,'dp-footer')]//button[contains(@class,'btn-primary')][normalize-space()='Save']")
    WebElement mapDrawerFooterSaveButton;

    @FindBy(xpath = "//input[@data-testid='unique-vulnerabilities-list-search-input_input']")
    WebElement uniqueVulnerabilitiesListSearchInput;

    @FindBy(xpath = "//button[@data-testid='unique-vulnerabilities-list-search-input_button']")
    WebElement uniqueVulnerabilitiesListSearchButton;

    @FindBy(xpath = "//div[contains(@class,'at-actions-dropdown-options')]//button[contains(normalize-space(),'Map')]")
    WebElement mapMenuItem;

    /** Success Remediate control above the unique vulnerabilities grid (enabled after IA mapping is saved). */
    @FindBy(xpath = "//div[@id='vulnerabilities-list-view']//button[.//span[contains(normalize-space(),'Remediate')]]")
    WebElement remediateButton;

    public BHACRiskPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    /**
     * On the Unique Vulnerabilities list: search for the vulnerability name, run search,
     * expand the matching row, open Action, and choose Map.
     *
     * @param vulnerabilityName exact vulnerability name text (e.g. from test data)
     * @return true if all steps complete without exception
     */
    public boolean searchExpandAndMapVulnerability(String vulnerabilityName) {
        log.info("Risk/Vulnerabilities: search, expand, and Map for: " + vulnerabilityName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        ensureVulnerabilitiesSearchVisible(wait);
        searchUniqueVulnerabilitiesList(vulnerabilityName, wait);
        WebElement dataRow = findUniqueVulnerabilityDataRowByName(vulnerabilityName, wait);
        expandUniqueVulnerabilityDataRow(dataRow, wait);

        WebElement actionBtn = dataRow.findElement(UNIQUE_VULN_ROW_ACTION_BUTTON);
        commonWaits.clickWithoutInterception(actionBtn);
        wait.until(ExpectedConditions.elementToBeClickable(mapMenuItem));
        mapMenuItem.click();
        commonWaits.waitForPageLoad(driver);
        log.info("Clicked Map for vulnerability: " + vulnerabilityName);
        return true;
    }

    /**
     * In the Map vulnerability side panel: + Map Remediation Content → search remediation → select row radio →
     * All Targets → Add → Save.
     *
     * @param remediationContentName row label to match (e.g. {@code install_pkg})
     */
    public boolean completeMapRemediationContent(String remediationContentName) {
        log.info("Map remediation content: " + remediationContentName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        commonWaits.clickWithoutInterception(mapRemediationContentButton);
        commonWaits.waitForPageLoad(driver);
        commonWaits.typeKeysWithEnter(mapPanelRxSearchInput, remediationContentName);
        commonWaits.waitForElementClickable(mapPanelRxSearchInput);
        commonWaits.waitForPageLoad(driver);

        String rowLabelXpath = String.format(remediationRowSelectLabelByContentXpath,
                CommonMethods.escapeSingleQuotesForXPath(remediationContentName));
        WebElement rowSelectLabel = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(rowLabelXpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", rowSelectLabel);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(rowSelectLabel));
            commonWaits.clickWithoutInterception(rowSelectLabel);
        } catch (Exception e) {
            log.info("Map row label click fallback (JS): " + e.getMessage());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", rowSelectLabel);
        }
        commonWaits.waitForPageLoad(driver);

        commonWaits.waitForByElementToBeVisible(By.xpath(ALL_TARGETS_RADIO_XPATH));
        if (!allTargetsRadio.isSelected()) {
            commonWaits.clickWithoutInterception(allTargetsRadio);
        }

        commonWaits.clickWithoutInterception(manualMapAddButton);
        commonWaits.waitForPageLoad(driver);
       
        commonWaits.clickWithoutInterception(mapDrawerFooterSaveButton);
        commonWaits.waitForPageLoad(driver);
        log.info("Saved map remediation for content: " + remediationContentName);
        return true;
    }

    /**
     * After remediation is mapped to IA, the green Remediate button above the vulnerabilities grid must be enabled.
     */
    public boolean isRemediationButtonEnabled() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(remediateButton));
       return (btn.isEnabled());
    }

    /** Clicks the Remediate control above the unique vulnerabilities grid ({@link #remediateButton}). */
    public void clickRemediationButton() {
        commonWaits.clickWithoutInterception(remediateButton);
        commonWaits.waitForPageLoad(driver);
    }

    /**
     * Full flow: list search/expand/Action/Map, then map remediation content to IA and save.
     */
    public boolean mapVulnerabilityToIaWithRemediation(String vulnerabilityName, String remediationContentName) {
        if (!searchExpandAndMapVulnerability(vulnerabilityName)) {
            return false;
        }
        return completeMapRemediationContent(remediationContentName);
    }

    /**
     * Unique Vulnerabilities list: type search text and run search (same box as {@link #searchExpandAndMapVulnerability}).
     */
    public void searchUniqueVulnerabilitiesList(String searchText, WebDriverWait wait) {
        commonWaits.keyClearAndType(uniqueVulnerabilitiesListSearchInput, searchText);
        commonWaits.clickWithoutInterception(uniqueVulnerabilitiesListSearchButton);
        commonWaits.waitForPageLoad(driver);
    }

    /**
     * Returns the main (non-expansion) data row whose Vulnerability Name cell matches {@code vulnerabilityName}.
     */
    public WebElement findUniqueVulnerabilityDataRowByName(String vulnerabilityName, WebDriverWait wait) {
        String rowXpath = String.format(dataRowWithVulnNameXpath, CommonMethods.escapeSingleQuotesForXPath(vulnerabilityName));
        return commonWaits.waitForByElementToBeVisible(By.xpath(rowXpath));
    }

    /**
     * Clicks the row expansion control and waits until the row is marked expanded.
     */
    public void expandUniqueVulnerabilityDataRow(WebElement vulnerabilityDataRow, WebDriverWait wait) {
        WebElement expandBtn = vulnerabilityDataRow.findElement(UNIQUE_VULN_ROW_EXPAND_BUTTON);
        commonWaits.waitForElementClickable(expandBtn);
        expandBtn.click();
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.attributeContains(vulnerabilityDataRow, "class", "at-row--expanded"));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(vulnerabilityDataRow, EXPANSION_ROW_SIBLING));
    }

    /**
     * Within the expanded impacted-assets table, returns whether a row's Asset Name (first column) contains {@code assetName}.
     */
    public boolean isAssetPresentInExpandedImpactedAssets(WebElement vulnerabilityDataRow, String assetName, WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(vulnerabilityDataRow, EXPANSION_ROW_SIBLING));
        WebElement expansionTr = vulnerabilityDataRow.findElement(EXPANSION_ROW_SIBLING);
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(expansionTr, By.xpath(".//table//tbody")));

        String assetEscaped = CommonMethods.escapeSingleQuotesForXPath(assetName);
        String cellXpath = String.format(impactedAssetNameCellByTestIdXpath, assetEscaped);
        if (!expansionTr.findElements(By.xpath(cellXpath)).isEmpty()) {
            return true;
        }
        String fallback = String.format(impactedAssetNameCellFirstColumnFallbackXpath, assetEscaped);
        return !expansionTr.findElements(By.xpath(fallback)).isEmpty();
    }

    /**
     * Search the unique-vulnerabilities list, expand the matching row, and check whether {@code assetName} appears
     * in the impacted-assets grid under that row.
     *
     * @param vulnerabilityName text matching the Vulnerability Name column (same as other Risk flows)
     * @param assetName         asset / hostname substring to match in Asset Name column
     * @return true if the asset is found in the expanded table
     */
    public boolean searchExpandVulnerabilityAndVerifyAssetPresent(String vulnerabilityName, String assetName) {
        log.info("Risk/Vulnerabilities: search & expand vuln='" + vulnerabilityName + "', verify asset='" + assetName + "'");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        ensureVulnerabilitiesSearchVisible(wait);
        searchUniqueVulnerabilitiesList(vulnerabilityName, wait);
        WebElement dataRow = findUniqueVulnerabilityDataRowByName(vulnerabilityName, wait);
        expandUniqueVulnerabilityDataRow(dataRow, wait);
        return isAssetPresentInExpandedImpactedAssets(dataRow, assetName, wait);
    }

    private void ensureVulnerabilitiesSearchVisible(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOf(uniqueVulnerabilitiesListSearchInput));
            return;
        } catch (Exception e) {
            log.info("Unique vulnerabilities search not visible; trying Vulnerabilities sub-tab.");
        }
        if (!vulnerabilitiesSubTabs.isEmpty()) {
            vulnerabilitiesSubTabs.get(0).click();
            commonWaits.waitForPageLoad(driver);
        }
        wait.until(ExpectedConditions.visibilityOf(uniqueVulnerabilitiesListSearchInput));
    }
}
