package pages.ITOM.BHAC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.BasePage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import pages.ITOM.Platform.PlatformLandingPage;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class BHACFeatureMethods extends BasePage {
    public static SolqaLogger log = new SolqaLogger(BHACFeatureMethods.class.getName());

    CommonWaits commonWaits;
    pages.ITOM.BHAC.BHACLandingPage bhacLandingPage;
    BHACRiskPage bhacRiskPage;
    ServiceMonitoringPage smPage;
    pages.ITOM.Bhom.BhomPage monitoringPage;
    pages.ITOM.Platform.PlatformLandingPage platformLandingPage;
    Events_API eventsAPI;
    AIOpsPage aiopsPage;
    public static String AIOpsTitle=SolQAConstants.AIOpsTitle;

    Map<String, String> filters = new HashMap<>();
    HMEvents eventsApi = new HMEvents();
    HMEvents hmEvents = new HMEvents();

    public BHACFeatureMethods() {
        commonWaits = new CommonWaits();
        bhacLandingPage = new BHACLandingPage(getDriver());
        bhacRiskPage = new BHACRiskPage(getDriver());
    }

    /**
     * Risk &gt; Vulnerabilities: search for the vulnerability, expand the row, Action → Map (opens panel only).
     *
     * @param vulnerabilityName vulnerability name as shown in the Vulnerability Name column
     * @return true if search, expand, and Map complete successfully
     */
    public boolean MapVulnerablityToIA(String vulnerabilityName) {
        log.info("Mapping vulnerability to IA via Risk list: " + vulnerabilityName);
        try {
            return bhacRiskPage.searchExpandAndMapVulnerability(vulnerabilityName);
        } catch (Exception e) {
            log.info("MapVulnerablityToIA failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Full map flow: Action → Map, then + Map Remediation Content, search/select remediation, All Targets, Add, Save.
     *
     * @param vulnerabilityName    vulnerability name in the grid
     * @param remediationContentName remediation row to select (e.g. {@code install_pkg})
     */
    public boolean MapVulnerablityToIA(String vulnerabilityName, String remediationContentName) {
        log.info("Mapping vulnerability to IA with remediation: vuln=" + vulnerabilityName + ", content=" + remediationContentName);
        try {
            return bhacRiskPage.mapVulnerabilityToIaWithRemediation(vulnerabilityName, remediationContentName);
        } catch (Exception e) {
            log.info("MapVulnerablityToIA failed: " + e.getMessage());
            return false;
        }
    }

    private static final int RISK_UNIQUE_VULN_WAIT_SECONDS = 20;

    private WebDriverWait riskUniqueVulnerabilitiesWait() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(RISK_UNIQUE_VULN_WAIT_SECONDS));
    }

    /**
     * Risk &gt; Unique Vulnerabilities list: type in the search box and click Search.
     */
    public void searchUniqueVulnerabilitiesList(String searchText) {
        bhacRiskPage.searchUniqueVulnerabilitiesList(searchText, riskUniqueVulnerabilitiesWait());
    }

    /**
     * Risk &gt; Unique Vulnerabilities: returns the data row whose Vulnerability Name matches {@code vulnerabilityName}.
     */
    public WebElement findUniqueVulnerabilityDataRowByName(String vulnerabilityName) {
        return bhacRiskPage.findUniqueVulnerabilityDataRowByName(vulnerabilityName, riskUniqueVulnerabilitiesWait());
    }

    /**
     * Risk &gt; Unique Vulnerabilities: expands the given vulnerability row and waits for the impacted-assets panel.
     */
    public void expandUniqueVulnerabilityDataRow(WebElement vulnerabilityDataRow) {
        bhacRiskPage.expandUniqueVulnerabilityDataRow(vulnerabilityDataRow, riskUniqueVulnerabilitiesWait());
    }

    /**
     * After a vulnerability row is expanded: true if {@code assetName} appears in the impacted-assets Asset Name column.
     */
    public boolean isAssetPresentInExpandedImpactedAssets(WebElement vulnerabilityDataRow, String assetName) {
        try {
            return bhacRiskPage.isAssetPresentInExpandedImpactedAssets(
                    vulnerabilityDataRow, assetName, riskUniqueVulnerabilitiesWait());
        } catch (Exception e) {
            log.info("isAssetPresentInExpandedImpactedAssets failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Risk &gt; Vulnerabilities: search list, expand matching row, and verify the asset exists under impacted assets.
     *
     * @param vulnerabilityName text in the Vulnerability Name column
     * @param assetName         hostname or substring to match in Asset Name
     */
    public boolean searchExpandVulnerabilityAndVerifyAssetPresent(String vulnerabilityName, String assetName) {
        log.info("Risk: verify asset under vulnerability — vuln=" + vulnerabilityName + ", asset=" + assetName);
        try {
            return bhacRiskPage.searchExpandVulnerabilityAndVerifyAssetPresent(vulnerabilityName, assetName);
        } catch (Exception e) {
            log.info("searchExpandVulnerabilityAndVerifyAssetPresent failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Navigate to a main BHAC tab using the BHACTab enum.
     * @param tab the BHACTab enum value (e.g. BHACLandingPage.BHACTab.DASHBOARD)
     */
    public BHACFeatureMethods navigateToTab(BHACLandingPage.BHACTab tab) {
        log.info("Navigating to BHAC tab: " + tab.getTabName());
        bhacLandingPage.navigateToTab(tab);
        return this;
    }

    /**
     * Click the Import button on the Manage > Import page.
     */
    public BHACFeatureMethods clickImportButton() {
        log.info("Clicking Import button.");
        bhacLandingPage.clickImportButton();
        return this;
    }

    /**
     * Click the Create Operation button on the Operations list page.
     */
    public BHACFeatureMethods clickCreateOperationButton() {
        log.info("Clicking Create Operation button.");
        bhacLandingPage.clickCreateOperationButton();
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
        return bhacLandingPage.isCreateOperationWindowOpen();
    }

    /**
     * Select a vendor from the Import Scan modal dropdown.
     * @param vendorName the vendor name (e.g. "Qualys", "Nessus", "Rapid7")
     */
    public BHACFeatureMethods selectVendor(String vendorName) {
        log.info("Selecting vendor: " + vendorName);
        bhacLandingPage.selectVendor(vendorName);
        return this;
    }

    /**
     * Check if a file is already imported in the BHAC import scan list.
     * @param fileName the file name to search for (e.g. "Qualys_RHEL_CVEs.xml")
     * @return true if the file is found in the import list, false otherwise
     */
    public boolean isFileImportedInBHAC(String fileName) {
        log.info("Checking if file is already imported: " + fileName);
        return bhacLandingPage.isFileImportedInBHAC(fileName);
    }

    /**
     * Delete an imported file from the BHAC import scan list by clicking Action > Remove.
     * @param fileName the file name to delete (e.g. "Qualys_RHEL_CVEs.xml")
     */
    public BHACFeatureMethods deleteImportedFile(String fileName) {
        log.info("Deleting imported file: " + fileName);
        bhacLandingPage.deleteImportedFile(fileName);
        return this;
    }

    /**
     * Verify that a file does not appear in the BHAC import scan table.
     * If the file is still present, it will refresh and retry up to 10 times with 2-second intervals.
     * @param fileName the file name that should not be present in the table
     * @return true if the file is not found after retries, false if it's still present
     */
    public boolean verifyFileNotInTableWithRetry(String fileName) {
        log.info("Verifying that file should not appear in table: " + fileName);
        return bhacLandingPage.verifyFileNotInTableWithRetry(fileName);
    }

    /**
     * Verify that a file does not appear in the BHAC import scan table with custom retry parameters.
     * If the file is still present, it will refresh and retry based on provided parameters.
     * @param fileName the file name that should not be present in the table
     * @param maxRetries maximum number of retry attempts
     * @param waitTimeSeconds wait time between retries in seconds
     * @return true if the file is not found after retries, false if it's still present
     */
    public boolean verifyFileNotInTableWithRetry(String fileName, int maxRetries, int waitTimeSeconds) {
        log.info("Verifying that file should not appear in table: " + fileName + 
                " (Max retries: " + maxRetries + ", Wait time: " + waitTimeSeconds + "s)");
        return bhacLandingPage.verifyFileNotInTableWithRetry(fileName, maxRetries, waitTimeSeconds);
    }

    /**
     * Click the refresh button to refresh data in the current view
     * @return BHACFeatureMethods instance for method chaining
     */
    public BHACFeatureMethods clickRefreshButton() {
        log.info("Clicking refresh button");
        bhacLandingPage.clickRefreshButton();
        return this;
    }

    /**
     * Select operating systems from the OS filter dropdown in the Import Scan modal.
     * @param osNames the OS names to select (e.g. "Windows", "Linux", "Others")
     */
    public BHACFeatureMethods selectOperatingSystem(String... osNames) {
        log.info("Selecting operating systems: " + String.join(", ", osNames));
        bhacLandingPage.selectOperatingSystem(osNames);
        return this;
    }

    /**
     * Select all severity levels from the Severity filter dropdown.
     */
    public BHACFeatureMethods selectAllSeverity() {
        log.info("Selecting all severity levels.");
        bhacLandingPage.selectAllSeverity();
        return this;
    }

    /**
     * Click the Attach file button on the Import Scan modal.
     */
    public BHACFeatureMethods clickAttachFileButton() {
        log.info("Clicking Attach file button.");
        bhacLandingPage.clickAttachFileButton();
        return this;
    }

    /**
     * Click the Import button on the Import Scan modal footer.
     */
    public BHACFeatureMethods clickModalImportButton() {
        log.info("Clicking Import button on modal.");
        bhacLandingPage.clickModalImportButton();
        return this;
    }

    public boolean isOperationsListed() {
        log.info("Checking if operations are listed.");
        return bhacLandingPage.isOperationsListed();
    }

    /**
     * Selects IA as the Remediation Tool if it is not already selected.
     */
    public BHACFeatureMethods selectIAAsRemediationTool() {
        log.info("Selecting IA as remediation tool.");
        bhacLandingPage.selectIAAsRemediationTool();
        return this;
    }

    public BHACFeatureMethods selectExecuteNow() {
        log.info("Selecting Execute Now radio button.");
        bhacLandingPage.selectExecuteNow();
        return this;
    }

    /**
     * Clears and types the given name into the Operation Name field.
     * @param name the operation name to enter
     */
    public BHACFeatureMethods provideOperationName(String name) {
        log.info("Providing operation name: " + name);
        bhacLandingPage.provideOperationName(name);
        return this;
    }

    /**
     * Clears and types the given text into the Operation Description field.
     * @param description the operation description to enter
     */
    public BHACFeatureMethods provideOperationDescription(String description) {
        log.info("Providing operation description: " + description);
        bhacLandingPage.provideOperationDescription(description);
        return this;
    }

    /**
     * Clicks the Next button on the Create Operation wizard.
     * Waits up to 10 seconds for it to become enabled; fails the test if still disabled.
     */
    public BHACFeatureMethods clickNextButton() {
        log.info("Clicking Next button.");
        bhacLandingPage.clickNextButton();
        return this;
    }

    public BHACFeatureMethods clickSaveButton() {
        log.info("Clicking Save button.");
        bhacLandingPage.clickSaveButton();
        return this;
    }

    /**
     * Types the asset name into the vulnerability search box, clicks Search,
     * waits for results, then clicks the matching asset row to select it.
     *
     * @param assetName the asset name to search for and select
     */
    public BHACFeatureMethods searchAndSelectVulnerability(String assetName) {
        log.info("Searching and selecting vulnerability for asset: " + assetName);
        bhacLandingPage.searchAndSelectVulnerability(assetName);
        return this;
    }

    /**
     * Vulnerability selector: opens the Advanced filter docked panel.
     */
    public BHACFeatureMethods openVulnerabilityAdvancedFilterPanel() {
        log.info("Opening vulnerability selector Advanced filter panel.");
        bhacLandingPage.openVulnerabilityAdvancedFilterPanel();
        return this;
    }

    /**
     * In Advanced filter, clicks a criterion row by its label (e.g. Asset, CVE ID, Severity).
     *
     * @param criterionLabel visible text on the row
     */
    public BHACFeatureMethods selectAdvancedFilterCriterion(String criterionLabel) {
        log.info("Selecting Advanced filter criterion: " + criterionLabel);
        bhacLandingPage.selectAdvancedFilterCriterion(criterionLabel);
        return this;
    }

    /**
     * Generic Advanced filter: criterion row, child search, checkbox by label, Done, Apply.
     *
     * @param criterionLabel e.g. {@code "Asset"}, {@code "Vulnerability Name"}
     * @param optionLabelText exact label on the checkbox (e.g. asset host or {@code Java-Important/Sec})
     */
    public BHACFeatureMethods applyAdvancedFilterSelectOption(String criterionLabel, String optionLabelText) {
        log.info("Applying Advanced filter: criterion=" + criterionLabel + ", option=" + optionLabelText);
        bhacLandingPage.applyAdvancedFilterSelectOption(criterionLabel, optionLabelText);
        return this;
    }

    /**
     * Advanced filter with criterion {@code "Asset"}.
     *
     * @param assetName exact name shown on the asset checkbox (e.g. from test data)
     */
    public BHACFeatureMethods applyAdvancedFilterSelectAsset(String assetName) {
        log.info("Applying Advanced filter for asset: " + assetName);
        bhacLandingPage.applyAdvancedFilterSelectAsset(assetName);
        return this;
    }

    /**
     * Advanced filter with criterion {@code "Vulnerability Name"}.
     *
     * @param vulnerabilityName exact name on the checkbox (e.g. {@code Java-Important/Sec})
     */
    public BHACFeatureMethods applyAdvancedFilterSelectVulnerabilityName(String vulnerabilityName) {
        log.info("Applying Advanced filter for vulnerability name: " + vulnerabilityName);
        bhacLandingPage.applyAdvancedFilterSelectVulnerabilityName(vulnerabilityName);
        return this;
    }

    /**
     * Clicks the Configure Script Parameters button and waits for the modal to open.
     */
    public BHACFeatureMethods openConfigureScriptParameters() {
        log.info("Opening Configure Script Parameters modal.");
        bhacLandingPage.openConfigureScriptParameters();
        return this;
    }

    /**
     * Finds the row for paramName in the modal, clicks pencil, types value, clicks OK.
     *
     * @param paramName the script parameter name (e.g. "sf_action", "software_name")
     * @param value     the value to set
     */
    public BHACFeatureMethods provideScriptParameter(String paramName, String value) {
        log.info("Providing script parameter '" + paramName + "' = '" + value + "'");
        bhacLandingPage.provideScriptParameter(paramName, value);
        return this;
    }

    /**
     * Clicks Save on the Configuration Script Parameters modal.
     * Fails the test if Save is not enabled within 10 seconds.
     */
    public BHACFeatureMethods saveScriptParameters() {
        log.info("Saving script parameters.");
        bhacLandingPage.saveScriptParameters();
        return this;
    }

    public boolean searchOperationAndVerifyStatus(String operationName, String expectedStatus) {
        log.info("Searching for operation: " + operationName + " and verifying status: " + expectedStatus);
        return bhacLandingPage.searchOperationAndVerifyStatus(operationName, expectedStatus);
    }

    public boolean isRemediationButtonEnabled() {
        log.info("Checking if Remediation button is enabled.");
        return bhacRiskPage.isRemediationButtonEnabled();
    }

    /** Risk &gt; Vulnerabilities: clicks the Remediate button above the unique vulnerabilities list. */
    public void clickRemediationButton() {
        log.info("Clicking Remediation button on Risk / Vulnerabilities.");
        bhacRiskPage.clickRemediationButton();
    }
}


