package pages.ITSM.ITSM;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Problem extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(Problem.class.getName());
    CommonWaits commonWaits;
    public Problem() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    // String locators
    String TYPE_HEAD_INPUT = "(//label[contains(. ,'%s')]/following-sibling::*)[1]";
    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String TICKET_ID = "(//label[contains(. ,'%s')]/following-sibling::textarea)[2]";

    // WebElement locators
    @FindBy(xpath = "//label[contains(text(), 'Problem ID')]/following-sibling::textarea")
    WebElement PROBLEM_ID;
    @FindBy(xpath = "//label[contains(text(), 'Status')]/parent::*//input")
    WebElement STATUS_FIELD;
    @FindBy(xpath = "//*[@class='AutoCompleteNonSelection']")
    WebElement TYPE_HEAD_OPTION;

    public String fillProblemData(Map<Object, String> problemData) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        // Set Description
        if (problemData.get("Notes") != null) {
            setTextAreaInput("Notes", problemData.get("Notes"));
        }

        // Set Summary
        if (problemData.get("Summary") != null) {
            setTextAreaInput("Summary", problemData.get("Summary"));
        }

        if (problemData.get("Impact") != null) {
            setDropDownField("Impact", problemData.get("Impact"));
        }

        if (problemData.get("Urgency") != null) {
            setDropDownField("Urgency", problemData.get("Urgency"));
        }

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Add 1 day to get tomorrow, keep current time
        LocalDateTime tomorrowWithCurrentTime = now.plusDays(1);

        // Format: M/d/yyyy hh:mm:ss a
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy hh:mm:ss a");

        String formattedDateTime = tomorrowWithCurrentTime.format(formatter);

        // Set Target Date
        setTextAreaInput("Target Date", formattedDateTime);

        String problemID = getProblemID();

        //Click Save btn
        commonWaits.clickByText("Save");

        log.info("WorkOrder ID: " + problemID);
        return problemID;
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
    
    public void setTypeHeadInput(String labelName, String value){
        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
            webElement.click();
            webElement.sendKeys(value);
            commonWaits.waitForElementVisible(TYPE_HEAD_OPTION, 2).click();
            log.info("Set typehead input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set typehead input for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public String getProblemID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(PROBLEM_ID).getAttribute("value").trim();
    }

    String searchResult ="//td//span[text()='%s']";
    public void searchProblem(String problemID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TICKET_ID, "Problem ID")));
        webElement.click();
        webElement.sendKeys(problemID);
        commonWaits.clickByText("Search");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchResult, problemID)));
        Actions action = new Actions(getDriver());
        action.doubleClick(webElement).perform();
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void updateProblemStatus(String status, String statusReason) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField("Status", status);
        if (status.equalsIgnoreCase("Completed")) {
            setDropDownField("Status Reason", statusReason);
        }
        commonWaits.clickByText("Save");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        log.info("Problem status updated to: " + status);
    }

    public String getStatus() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(STATUS_FIELD).getAttribute("value");
    }

}