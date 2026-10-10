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

public class Release extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(Problem.class.getName());
    CommonWaits commonWaits;
    public Release() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    // String locators
    String TYPE_HEAD_INPUT = "(//label[contains(. ,'%s')]/following-sibling::textarea)[1]";
    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String TICKET_ID = "(//label[contains(. ,'%s')]/following-sibling::textarea)[2]";
    String searchResult ="//td//span[text()='%s']";
    String dropDownFieldValue = "(//label[contains(. ,'%s')]/parent::div//textarea)[1]";

    // WebElement locators
    @FindBy(xpath = "//label[contains(text(), 'Release ID')]/following-sibling::textarea")
    WebElement RELEASE_ID;
    @FindBy(xpath = "(//label[contains(text(), 'Status')]/following::textarea)[1]")
    WebElement statusField;
    @FindBy(xpath = "//a[@ardbn='Submit' and contains(. ,'Save')]")
    WebElement saveButton;

    public String fillReleaseData(Map<Object, String> releaseData){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        // Set Summary
        if (releaseData.get("Summary") != null) {
            setTextAreaInput("Summary", releaseData.get("Summary"));
        }

        if (releaseData.get("Notes") != null) {
            setTextAreaInput("Notes", releaseData.get("Notes"));
        }

        if (releaseData.get("Business Justification") != null) {
            setDropDownField("Business Justification", releaseData.get("Business Justification"));
        }

        if (releaseData.get("Impact") != null) {
            setDropDownField("Impact", releaseData.get("Impact"));
        }

        if (releaseData.get("Urgency") != null) {
            setTextAreaInput("Urgency", releaseData.get("Urgency"));
        }

        if (releaseData.get("Priority") != null) {
            setTextAreaInput("Priority", releaseData.get("Priority"));
        }

        String releaseID = getReleaseID();

        //Click Save btn
        commonWaits.waitForElementVisible(saveButton).click();

        log.info("change ID: " + releaseID);
        return releaseID;
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
            commonWaits.waitForSometime(1000); // Wait for dropdown options to load
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, value))).click();
            log.info("Set dropdown field " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public String getReleaseID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(RELEASE_ID).getAttribute("value").trim();
    }

    public void searchRelease(String changeID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TICKET_ID, "Release ID")));
        webElement.click();
        webElement.sendKeys(changeID);
        commonWaits.clickByText("Search");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchResult, changeID)));
        Actions action = new Actions(getDriver());
        action.doubleClick(webElement).perform();
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void updateReleaseStatus(String status) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField("Status", status);
        commonWaits.clickByText("Save");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void refreshTicket(){
        commonWaits.clickByText("Refresh");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void clickOnNextStage(){
        commonWaits.clickByText("Next Stage");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public String getStatus(){
    	refreshTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(statusField).getAttribute("value").trim();
    }

    public String getFieldValue(String fieldName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownFieldValue, fieldName))).getAttribute("value").trim();
    }
}
