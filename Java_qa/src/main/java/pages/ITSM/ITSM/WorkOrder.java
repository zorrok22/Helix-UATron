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
import java.util.Map;

public class WorkOrder extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(WorkOrder.class.getName());
    CommonWaits commonWaits;

    public WorkOrder() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    // WebElements
    @FindBy(xpath = "//*[@class='AutoCompleteNonSelection']")
    WebElement TYPE_HEAD_OPTION;
    @FindBy(xpath = "//*[contains(@class, 'SaveForm')]")
    WebElement SAVE_BUTTON;
    @FindBy(xpath = "//label[contains(text(), 'Work Order ID')]/following-sibling::textarea")
    WebElement WORKORDER_ID;
    @FindBy(xpath = "//label[contains(text(), 'Status')]/parent::*//input")
    WebElement STATUS_FIELD;

    // String locators
    String TICKET_ID = "(//label[contains(. ,'%s')]/following-sibling::textarea)[last()]";
    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String TYPE_HEAD_INPUT = "(//label[contains(. ,'%s')]/following-sibling::textarea)[1]";
    String searchResult ="//td//span[text()='%s']";

    public String fillWorkOrderData(Map<Object, String> workOrderData) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        //Set Company
        if (workOrderData.get("Company") != null) {
            setDropDownField(SolQAConstants.Company_Label, workOrderData.get("Company"));
        }

        // Set Customer
        if (workOrderData.get("Customer") != null) {
            setTypeHeadInput(SolQAConstants.Customer, workOrderData.get("Customer"));
        }

        // Set Description
        if (workOrderData.get("Notes") != null) {
            setTextAreaInput(SolQAConstants.Notes, workOrderData.get("Notes"));
        }

        // Set Summary
        if (workOrderData.get("Summary") != null) {
            setTextAreaInput(SolQAConstants.SUMMARY_LABEL, workOrderData.get("Summary"));
        }

        // Set Service
        if (workOrderData.get("Service") != null) {
            setDropDownField(SolQAConstants.Service, workOrderData.get("Service"));
        }

        // Set WorkOrder Type
        if (workOrderData.get("Work Order Type") != null) {
            setDropDownField(SolQAConstants.Work_Order_Type, workOrderData.get("Work Order Type"));
        }

        // Set manager support Group
        if (workOrderData.get("Manager Support Group Name") != null) {
            setDropDownField(SolQAConstants.Support_Group_Name+"*", workOrderData.get("Manager Support Group Name"));
        }

        // Set request Manager
        if (workOrderData.get("Request Manager") != null) {
            setDropDownField(SolQAConstants.Request_Manager, workOrderData.get("Request Manager"));
        }

        // Set Assignee support Group
        if (workOrderData.get("Assignee Support Group Name") != null) {
            setDropDownField(SolQAConstants.Support_Group_Name, workOrderData.get("Assignee Support Group Name"));
        }

        // Set request assignee
        if (workOrderData.get("Request Assignee") != null) {
            setDropDownField(SolQAConstants.Request_Assignee, workOrderData.get("Request Assignee"));
        }

        // Set Priority
        if (workOrderData.get(SolQAConstants.PRIORITY) != null) {
            setDropDownField(SolQAConstants.PRIORITY, workOrderData.get(SolQAConstants.PRIORITY));
        }

        String workOrderID = getWorkOrderID();

        //Click Save btn
        commonWaits.clickByText(SolQAConstants.SAVE);

        log.info("WorkOrder ID: " + workOrderID);
        return workOrderID;
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

    public String getWorkOrderID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(WORKORDER_ID).getAttribute(SolQAConstants.ATTRIBUTE_VALUE).trim();
    }

    public void searchWorkOrder(String ticketID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TICKET_ID, SolQAConstants.Work_Order_ID)));
        webElement.click();
        webElement.sendKeys(ticketID);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchResult, ticketID)));
        Actions action = new Actions(getDriver());
        action.doubleClick(webElement).perform();
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void updateStatus(String status) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField(SolQAConstants.STATUS, status);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void updatePriority(String priority) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField(SolQAConstants.PRIORITY, priority);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public String getStatus() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(STATUS_FIELD).getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
    }

}