package pages.ITSM.ITSM;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.util.Map;

public class Incident extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(Incident.class.getName());
    CommonWaits commonWaits;
    CommonMethods commonMethods;
    public Incident() {
        PageFactory.initElements(getDriver(),this);
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    // WebElements
    @FindBy(xpath = "//*[@class='AutoCompleteNonSelection']")
    WebElement TYPE_HEAD_OPTION;
    @FindBy(xpath = "//*[contains(@class, 'SaveForm')]")
    WebElement SAVE_BUTTON;
    @FindBy(xpath = "(//label[contains(normalize-space(.),'Incident ID')]/following-sibling::textarea)[last()]")
    WebElement INCIDENT_ID;
    @FindBy(xpath = "//label[contains(text(), 'Status')]/parent::*//input")
    WebElement STATUS_FIELD;
    @FindBy(xpath = "//a[@ardbn='z3Btn_BreadCSearch']//img[@title='Search']")
    WebElement SEARCH_BUTTON;

    // String locators
    String searchResult ="//td//span[text()='%s']";
    String TYPE_HEAD_INPUT = "(//label[contains(normalize-space(.),'%s')]/following-sibling::textarea)[1]";
    String TICKET_ID = "(//label[contains(normalize-space(.),'%s')]/following-sibling::textarea)[2]";
    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String activityLogEntry = "//*[contains(@class,'ActivityFeed') or contains(@id,'ActivityFeed')]//*[contains(text(),'%s')]";
    String taskName = "(//td[contains(normalize-space(),'%s')])[1]";

  

    public String fillIncidentData(Map<Object, String> incidentData) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForPageReadyState(getDriver());
        //Set Company
        if (incidentData.get("Company") != null) {
            setDropDownField("Company", incidentData.get("Company"));
        }

        // Set Customer
        if (incidentData.get("Customer") != null) {
            setTypeHeadInput("Customer", incidentData.get("Customer"));
        }

        // Set Summary
        if (incidentData.get("Summary") != null) {
            setTextAreaInput("Summary", incidentData.get("Summary"));
        }

        // Set Description
        if (incidentData.get("Notes") != null) {
            setTextAreaInput("Notes", incidentData.get("Notes"));
        }

        //Set Impact
        if (incidentData.get("Impact") != null) {
            setDropDownField("Impact", incidentData.get("Impact"));
        }

        // Set Urgency
        if (incidentData.get("Urgency") != null) {
            setDropDownField("Urgency", incidentData.get("Urgency"));
        }

        // Set Priority
        if (incidentData.get(SolQAConstants.PRIORITY) != null) {
            setDropDownField(SolQAConstants.PRIORITY, incidentData.get(SolQAConstants.PRIORITY));
        }

        // Set Service
        if (incidentData.get("Service") != null) {
            setDropDownField("Service", incidentData.get("Service"));
        }

        // Set CI
        if (incidentData.get("CI") != null) {
            setDropDownField("CI", incidentData.get("CI"));
        }

        // Set Incident Type
        if (incidentData.get("Incident Type") != null) {
            scrollToBottomOfPage();
            setDropDownField("Incident Type", incidentData.get("Incident Type"));
        }

        // Set Reported Source
        if (incidentData.get("Reported Source") != null) {
            setDropDownField("Reported Source", incidentData.get("Reported Source"));
        }

        // Set Assigned Group
        if (incidentData.get("Assigned Group") != null) {
            setDropDownField("Assigned Group", incidentData.get("Assigned Group"));
        }

        //Set Assignee
        if (incidentData.get(SolQAConstants.ASSIGNEE) != null) {
            setDropDownField(SolQAConstants.ASSIGNEE, incidentData.get(SolQAConstants.ASSIGNEE));
        }

        // Set Vendor Group
        if (incidentData.get("Vendor Group") != null) {
            setDropDownField("Vendor Group", incidentData.get("Vendor Group"));
        }

        String incidentID = getIncidentID();

        //Click Save btn
        commonWaits.clickByText("Save");

        log.info("Incident ID: " + incidentID);
        return incidentID;
    }

    /**
     * Sets a value in a typehead input field
     * @param labelName The label name of the input field
     * @param value The value to set
     */
    public void setTypeHeadInput(String labelName, String value){
        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
            webElement.click();
            webElement.sendKeys(value);
            commonWaits.waitForElementVisible(TYPE_HEAD_OPTION, 5).click();
            log.info("Set typehead input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set typehead input for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public void setTextAreaInput(String labelName, String value){
        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
            webElement.click();
            webElement.sendKeys(value);
            log.info("Set text area input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set text area input for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public void setDropDownField(String labelName, String value) {
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownField, labelName))).click();
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, value))).click();
            log.info("Set dropdown field " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public String getIncidentID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(INCIDENT_ID).getAttribute("value").trim();
    }

    public String getFieldValue(String labelName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
        return webElement.getAttribute("value").trim();
    }


    public void searchIncident(String incidentID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        if (!commonWaits.isElementPresent(INCIDENT_ID)) {
            new ITSMDashboard().navigateToPageFromApplication(
                    SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.SEARCH_INCIDENT);
        }

        WebElement webElement = commonWaits.waitForElementVisible(INCIDENT_ID);
        webElement.click();
        webElement.sendKeys(incidentID);
        commonWaits.clickByText("Search");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchResult, incidentID)));
        Actions action = new Actions(getDriver());
        action.doubleClick(webElement).perform();
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void updateStatus(String status, String resolution, String statusReason) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField("Status", status);
        if (status.equalsIgnoreCase("In Progress")) {
            setTextAreaInput("Resolution", resolution);
        }
        if (status.equalsIgnoreCase("Resolved") || status.equalsIgnoreCase("Pending")) {
            setDropDownField("Status Reason", statusReason);
        }

        commonWaits.clickByText("Save");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public String getStatus() {
        commonWaits.clickByText("Refresh");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(STATUS_FIELD).getAttribute("value");
    }

    /**
     * Verify a task row exists for given task name and status on an Incident.
     * Old framework used table field id 10006140 (Tasks table).
     */
    public boolean isTaskWithStatusPresent(String TaskName, String expectedStatus) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForExecutionComplete();
        log.info("Task name: " + taskName);
        log.info("Expected status: " + expectedStatus);

        log.info("navigate tp Tasks tab");
        commonWaits.clickByText("Tasks");
        commonWaits.waitForExecutionComplete();
        log.info("verify task with name: " + TaskName + " and status: " + expectedStatus);
        WebElement taskElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(taskName, TaskName)));
        WebElement statusElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(taskName, expectedStatus)));
        if (commonWaits.isElementPresent(taskElement) && commonWaits.isElementPresent(statusElement)) {
            log.info("Task with name: " + TaskName + " and status: " + expectedStatus + " found");
            return true;
        }
        log.info("Task with name: " + taskName + " and status: " + expectedStatus + " not found");
        return false;
    }

    /**
     * Verifies that the activity log contains the expected text
     * @param expectedActivityText The expected text in the activity log
     * @return true if the activity log contains the expected text
     */
    public boolean verifyActivityLog(String expectedActivityText) {
        log.info("Verifying activity log contains: " + expectedActivityText);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        try {
            WebElement activityElement = commonWaits.waitForByElementToBeVisible(
                    By.xpath(String.format(activityLogEntry, expectedActivityText)));
            boolean isDisplayed = activityElement.isDisplayed();
            log.info("Activity log entry found: " + isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            log.error("Activity log entry not found: " + expectedActivityText);
            return false;
        }
    }

}