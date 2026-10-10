package pages.ITSM.ITSM;

import framework.BasePage;
import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ITSMCommonOperations extends BasePage {

    CommonWaits commonWaits;
    SolqaLogger log;

    public ITSMCommonOperations() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ITSMCommonOperations.class.getName());
        commonWaits = new CommonWaits();
    }

    @FindBy(xpath = "//a[(text()='Incident Management')]")
    WebElement incidentManagementLink;
    @FindBy(xpath = "(//a[text()='Advanced Options' and contains(@class,'treetitle')])[4]")
    WebElement advancedOptionsLink;
    @FindBy(xpath = "//a[text()='Rules']")
    WebElement rulesLink;
    @FindBy(xpath = "//*[text()='Sandbox Enabled']/following-sibling::div/input")
    WebElement sandboxEnableInput;
    @FindBy(xpath = "//a[@arid='303060100' and contains(normalize-space(.), 'Next')]")
    WebElement nextButton;
    @FindBy(xpath = "//a[contains(., 'Service') and contains(. ,'Targets')]")
    WebElement serviceTargetsTab;

    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String sandboxEnableValueXpath = "//*[@class='MenuTable']//td[text()='%s']";
    String ElementByARID = "//*[@arid='%s']";
    String CHANGE_DATA_TABS_XPATH = "//*[@class='OuterTab']//a[contains(. ,'%s')]";
    String INPUT_FIELD_XPATH = "(//label[contains(. ,'%s')]/following-sibling::input)[1]";
    String DATA_SOURCE_VALUE_CELL_XPATH = "//table[.//*[self::th or self::td][normalize-space()='Data Source']]//tr/td[1][normalize-space()='%s']";
    String SERVICE_TARGET_ID_CELL_XPATH = "//table[.//*[self::th or self::td][normalize-space()='ID']]//tr[td]/td[2][normalize-space()='%s']";
    String SERVICE_TARGET_BUILD_STATUS_BY_ID_XPATH = "//table[.//*[self::th or self::td][normalize-space()='ID'] and .//*[self::th or self::td][contains(normalize-space(),'Build Status')]]//tr[td[2][normalize-space()='%s']]/td[last()]";
    String RULE_BUILD_STATUS_CELLS_XPATH = "//table[.//*[self::th or self::td][contains(normalize-space(),'Rule Build')]]//tr[td]/td[last()]";
    String AST_AppSetting_Show_Current_Settings = "260000008";
    String tab = "//a[normalize-space()='%s']";

    public ITSMCommonOperations clickOnCustomConfigurationAndNavigateToRulesPageOfIncident() {
        commonWaits.clickByText("Custom Configuration");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        scrollToElement(incidentManagementLink);
        commonWaits.doubleClickOnElementUsingJS(incidentManagementLink);

        scrollToElement(advancedOptionsLink);
        commonWaits.doubleClickOnElementUsingJS(advancedOptionsLink);

        scrollToElement(rulesLink);
        commonWaits.clickWithoutInterception(rulesLink);
        commonWaits.clickByText(SolQAConstants.OPEN);
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations setServiceRequestOnSubmitToValue(String Company, String value, String settingName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField("Company", Company);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField(settingName, value);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickByText(SolQAConstants.REFRESH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations setDropDownField(String labelName, String value) {
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownField, labelName))).click();
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, value))).click();
            log.info("Set dropdown field " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
        return this;
    }

    public ITSMCommonOperations modifyAssetSandboxEnabledSettings(String company, String sandboxEnableValue) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementByARID, AST_AppSetting_Show_Current_Settings))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(sandboxEnableInput).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(sandboxEnableValueXpath, sandboxEnableValue))).click();
        setDropDownField(SolQAConstants.Default_Company_Label, company);
        commonWaits.clickByText(SolQAConstants.MODIFY);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations fillScheduleDatesSection(Map<Object, String> itsmTestData) {
        if (itsmTestData.get("Schedule Dates") == null) {
            log.info("Schedule Dates section not required, skipping");
            return this;
        }

        log.info("Navigating to Date/System tab to set Schedule Dates");
        try {
            WebElement dateSystemTab = commonWaits.waitForElementPresenceLocated(
                    By.xpath(String.format(CHANGE_DATA_TABS_XPATH, SolQAConstants.SYSTEM_DATE))
            );
            commonWaits.javaScriptScrollIntoView(dateSystemTab);
            commonWaits.clickWithoutInterception(By.xpath(String.format(CHANGE_DATA_TABS_XPATH, SolQAConstants.SYSTEM_DATE)));
            commonWaits.waitForAngularRequestsToFinish();

            // Set Scheduled Start Date
            setScheduleDate(SolQAConstants.Scheduled_Start_Date, SolQAConstants.Scheduled_Start_Date, itsmTestData);

            // Set Scheduled End Date
            setScheduleDate(SolQAConstants.Scheduled_End_Date, SolQAConstants.Scheduled_End_Date, itsmTestData);

            log.info("Successfully set all Schedule Dates");
            commonWaits.clickByText(SolQAConstants.SAVE);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        } catch (Exception e) {
            log.error("Failed to set Schedule Dates: " + e.getMessage());
            throw new RuntimeException("Error setting Schedule Dates", e);
        }
        return this;
    }

    public ITSMCommonOperations setScheduleDate(String dataKey, String fieldLabel, Map<Object, String> itsmTestData) {
        String daysValue = itsmTestData.get(dataKey);
        if (daysValue != null && !daysValue.trim().isEmpty()) {
            try {
                int days = Integer.parseInt(daysValue);
                String DATE_TIME_FORMAT_PATTERN = "MM/dd/yyyy hh:mm:ss a";
                String dateValue = getFutureDate(days, DATE_TIME_FORMAT_PATTERN);
                log.info("Setting " + fieldLabel + ": " + dateValue + " (+" + days + " days from today)");
                setInputField(fieldLabel, dateValue);
            } catch (NumberFormatException e) {
                log.error("Invalid number format for " + dataKey + ": '" + daysValue + "'. Must be an integer.");
                throw new IllegalArgumentException("Invalid days value for " + dataKey + ": " + daysValue, e);
            }
        } else {
            log.info(fieldLabel + " not found in test data, skipping");
        }
        return this;
    }

    public ITSMCommonOperations setInputField(String labelName, String value) {
        log.info("Setting input field: '" + labelName + "' with value: '" + value + "'");
        try {
            WebElement webElement = this.commonWaits.waitForByElementToBeVisible(By.xpath(String.format(INPUT_FIELD_XPATH, labelName)));
            webElement.sendKeys(value);
            log.info("Successfully set input field '" + labelName + "' to '" + value + "'");
        } catch (Exception e) {
            log.error("Failed to set input field for '" + labelName + "': " + e.getMessage());
            throw e;
        }
        return this;
    }

    public ITSMCommonOperations clickOnNextButton() {
        commonWaits.waitForElementVisible(nextButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations clickDataSourceByValue(String dataSourceValue) {
        if (dataSourceValue == null || dataSourceValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Data Source value cannot be null or empty");
        }

        String valueToClick = dataSourceValue.trim();
        try {
            By dataSourceCellBy = By.xpath(String.format(DATA_SOURCE_VALUE_CELL_XPATH, valueToClick));
            WebElement dataSourceCell = commonWaits.waitForByElementToBeVisible(dataSourceCellBy);
            commonWaits.javaScriptScrollIntoView(dataSourceCell);
            commonWaits.clickWithoutInterception(dataSourceCellBy);
            log.info("Clicked Data Source row with value: " + valueToClick);
        } catch (Exception e) {
            log.error("Unable to click Data Source value '" + valueToClick + "': " + e.getMessage());
            throw e;
        }
        return this;
    }

    public ITSMCommonOperations clickServiceTargetRowById(String serviceTargetId) {
        if (serviceTargetId == null || serviceTargetId.trim().isEmpty()) {
            throw new IllegalArgumentException("Service Target ID cannot be null or empty");
        }

        String idToClick = serviceTargetId.trim();
        try {
            By serviceTargetIdCellBy = By.xpath(String.format(SERVICE_TARGET_ID_CELL_XPATH, idToClick));
            WebElement serviceTargetIdCell = commonWaits.waitForByElementToBeVisible(serviceTargetIdCellBy);
            commonWaits.javaScriptScrollIntoView(serviceTargetIdCell);
            commonWaits.clickWithoutInterception(serviceTargetIdCellBy);
            log.info("Clicked Service Target row with ID: " + idToClick);
        } catch (Exception e) {
            log.error("Unable to click Service Target ID '" + idToClick + "': " + e.getMessage());
            throw e;
        }
        return this;
    }

    public String getBuildStatusForServiceTargetId(String serviceTargetId) {
        if (serviceTargetId == null || serviceTargetId.trim().isEmpty()) {
            throw new IllegalArgumentException("Service Target ID cannot be null or empty");
        }

        String idToCheck = serviceTargetId.trim();
        try {
            By buildStatusBy = By.xpath(String.format(SERVICE_TARGET_BUILD_STATUS_BY_ID_XPATH, idToCheck));
            WebElement buildStatusCell = commonWaits.waitForByElementToBeVisible(buildStatusBy);
            commonWaits.javaScriptScrollIntoView(buildStatusCell);
            String actualStatus = buildStatusCell.getText().trim();
            log.info("Fetched Build Status for ID '" + idToCheck + "' as '" + actualStatus + "'");
            return actualStatus;
        } catch (Exception e) {
            log.error("Unable to fetch Build Status for ID '" + idToCheck + "': " + e.getMessage());
            throw e;
        }
    }

    public ITSMCommonOperations verifyBuildStatusForServiceTargetId(String serviceTargetId, String expectedBuildStatus) {
        if (serviceTargetId == null || serviceTargetId.trim().isEmpty()) {
            throw new IllegalArgumentException("Service Target ID cannot be null or empty");
        }
        if (expectedBuildStatus == null || expectedBuildStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected Build Status cannot be null or empty");
        }

        String idToCheck = serviceTargetId.trim();
        String expectedStatus = expectedBuildStatus.trim();
        try {
            String actualStatus = getBuildStatusForServiceTargetId(idToCheck);

            if (!expectedStatus.equalsIgnoreCase(actualStatus)) {
                String error = "Build Status mismatch for ID '" + idToCheck + "'. Expected: '" + expectedStatus + "', Actual: '" + actualStatus + "'";
                log.error(error);
                throw new AssertionError(error);
            }

            log.info("Verified Build Status for ID '" + idToCheck + "' is '" + actualStatus + "'");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            log.error("Unable to verify Build Status for ID '" + idToCheck + "': " + e.getMessage());
            throw e;
        }
        return this;
    }

    public ITSMCommonOperations clickOnTab(String tabName) {
        WebElement tabWebelement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(tab, tabName)));
        tabWebelement.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations verifyAllRuleBuildStatusesAreBuiltSuccessfully() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        By ruleBuildStatusCellsBy = By.xpath(RULE_BUILD_STATUS_CELLS_XPATH);
        commonWaits.waitForByElementToBeVisible(ruleBuildStatusCellsBy);

        List<WebElement> statusCells = getDriver().findElements(ruleBuildStatusCellsBy);
        if (statusCells.isEmpty()) {
            throw new AssertionError("No Rule Build Status rows found in Administration table");
        }

        List<String> failedRows = new ArrayList<>();
        for (int i = 0; i < statusCells.size(); i++) {
            String status = statusCells.get(i).getText().trim();
            if (!"Built Successfully".equalsIgnoreCase(status)) {
                failedRows.add("row " + (i + 1) + " = '" + status + "'");
            }
        }

        if (!failedRows.isEmpty()) {
            String error = "Rule Build Status validation failed: " + String.join(", ", failedRows);
            log.error(error);
            throw new AssertionError(error);
        }

        log.info("All Rule Build Status rows are 'Built Successfully'. Total rows checked: " + statusCells.size());
        return this;
    }

    public ITSMCommonOperations clickOnServiceTargetsTab() {
        commonWaits.waitForElementVisible(serviceTargetsTab).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations clickOnBuildBtn() {
        commonWaits.clickByText(SolQAConstants.BUILD);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public ITSMCommonOperations confirmOperationInPopup() {
        commonWaits.switchToTabByTitle("Confirm Operation");
        commonWaits.clickByText(SolQAConstants.YES);
        commonWaits.switchToTabByTitle("SLM:Console");
        return this;
    }

    public ITSMCommonOperations clickViewButton() {
        commonWaits.clickByText(SolQAConstants.VIEW);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    @FindBy(xpath = "//label[text()='Hours*']/following-sibling::*//input")
    WebElement hoursInput;
    @FindBy(xpath = "//label[text()='Minutes*']/following-sibling::*//input")
    WebElement minutesInput;
    public ITSMCommonOperations setSingleGoalAndCostValues(String Hours, String Minutes) {
        commonWaits.waitForElementVisible(hoursInput).clear();
        hoursInput.sendKeys(Hours);
        commonWaits.waitForElementVisible(minutesInput).clear();
        minutesInput.sendKeys(Minutes);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

}