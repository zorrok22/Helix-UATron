package pages.ITSM.ITSM;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Change extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(Change.class.getName());
    CommonWaits commonWaits;
    public Change() {
        PageFactory.initElements(getDriver(),this);
        commonWaits = new CommonWaits();
    }

    //String locators
    String TYPE_HEAD_INPUT = "(//label[contains(. ,'%s')]/following-sibling::textarea)[1]";
    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String TICKET_ID = "(//label[contains(. ,'%s')]/following-sibling::textarea)[last()]"; //[2]
    String searchResult ="//table[@id='T1020']//td//span[text()='%s']";
    String changeDetailsTab = "//*[@class='OuterTab']//a[text()='%s']";
    String dateInformationFields = "//label[contains(text(), '%s')]/following-sibling::input";
    String textareaByARID = "//*[@arid='%s']//textarea";
    String changeIDOnApprovalCentral = "14832";
    String changeCategorization = "//div[@id='WIN_3_1000000001']//a";
    String planAndScheduleActions = "//div[@id='WIN_1_301542100']//a";
    String planAndScheduleRelateCi = "//td[.='Relate CI']";
    String planAndScheduleRelateCiName = "//*[@id='arid_WIN_0_301737200']";
    String planAndScheduleRelateCiSearchButton = "//*[@id='WIN_0_301304500']";
    String getPlanAndScheduleActionsRelateCISelectCiButton = "//*[@arid='301867500']";
    String selectCiToRelateIFrame = "//*[@class='DIVPopupBodyNoBorder DIVPopupBodyBorder']/iframe";
    String selectCIToRelateAcceptPopup = "//*[@id='PopupMsgFooter']/a[1]";
    String changeManagerSelect = "//div[@id='WIN_1_1000000403']//a";
    String selectService = "//td[.='Service']";
    String selectCategory = "//td[.='category']";
    String searchChangeResult = "//div[contains(@id,'ResultsList')]//span[text()='%s']";

    //WebElement locators
    @FindBy(xpath = "//a[@title='Quick Search']")
    WebElement quickSearchBtn;
    String changeRow = "//input[@class='colcheck']/ancestor::tr";
    @FindBy(xpath = "//*[text()='Approve Selected']/ancestor::a[@title='Approve']")
    WebElement approveSelectedBtn;
    @FindBy(xpath = "//a[@title='Refresh']")
    WebElement refreshButton;
    @FindBy(xpath = "(//label[contains(text(), 'Status')]/following::textarea)[1]")
    WebElement statusField;
    @FindBy(xpath = "//label[contains(text(), 'Change ID')]/following-sibling::textarea")
    WebElement CHANGE_ID;

    String ApproverTypeLabel = "Individual/Group";
    String First_Name_Label = "First Name";
    String Last_Name_Label = "Last Name";

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

    public String fillChangeData(Map<Object, String> changeData) {
        commonWaits.waitForAllSpinnersToHide("ITSM");
        // Set Summary
        if (changeData.get("Summary") != null) {
            setTextAreaInput("Summary", changeData.get("Summary"));
        }

        if (changeData.get("Notes") != null) {
            setTextAreaInput("Notes", changeData.get("Notes"));
        }

        if (changeData.get("Impact") != null) {
            setDropDownField("Impact", changeData.get("Impact"));
        }

        if (changeData.get("Urgency") != null) {
            setDropDownField("Urgency", changeData.get("Urgency"));
        }

        if (changeData.get(SolQAConstants.PRIORITY) != null) {
            setDropDownField(SolQAConstants.PRIORITY, changeData.get(SolQAConstants.PRIORITY));
        }

        if(changeData.get("Service") != null){
            setTextAreaInput("Service", changeData.get("Service"));
            commonWaits.waitForByElementToBeVisible(By.xpath(selectService.replace("Service",changeData.get("Service")))).click();
        }

        if(changeData.get("Categorization") != null){
            String category =  changeData.get("Categorization");
            navigateToChangeDetailsTab("Categorization");
            commonWaits.waitForByElementToBeVisible(By.xpath(changeCategorization)).click();
            commonWaits.waitForByElementToBeVisible(By.xpath(selectCategory.replace("category", category))).click();
        }

        String changeID = getChangeID();

        //Click Save btn
        commonWaits.clickByText(SolQAConstants.SAVE);
        log.info("change ID: " + changeID);
        return changeID;
    }

    public String getChangeID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(CHANGE_ID).getAttribute(SolQAConstants.ATTRIBUTE_VALUE).trim();
    }

    public void searchChange(String changeID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(TICKET_ID, "Change ID")), 10), "Change ID search field is not visible");
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TICKET_ID, "Change ID")));
        webElement.click();
        webElement.sendKeys(changeID);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchResult, changeID)));
        Actions action = new Actions(getDriver());
        action.doubleClick(webElement).perform();
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void updateChangeStatus(String status) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField("Status", status);
        if (status.equalsIgnoreCase("Scheduled For Review")) {
            updateScheduleAndActualDates();
        }
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    public void navigateToChangeDetailsTab(String tabName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(changeDetailsTab, tabName))).click();
    }

    public void updateScheduleAndActualDates(){
        navigateToChangeDetailsTab(SolQAConstants.SYSTEM_DATE);
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 3; i++) {
            // Set time to 12:00:00 AM
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);

            // Format and add date to list
            dateList.add(formatter.format(calendar.getTime()));

            // Move to next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateInformationFields, "Scheduled Start Date"))).sendKeys(dateList.get(0));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateInformationFields, "Scheduled End Date"))).sendKeys(dateList.get(1));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateInformationFields, "Actual Start Date"))).sendKeys(dateList.get(1));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateInformationFields, "Actual End Date"))).sendKeys(dateList.get(2));
    }

    public void approveChange(String changeID){
        commonWaits.switchToTabByIndex(1);
        setDropDownField(SolQAConstants.SHOW, SolQAConstants.PENDING);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickByText(SolQAConstants.REFRESH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textareaByARID, changeIDOnApprovalCentral)));
        webElement.click();
        webElement.clear();
        webElement.sendKeys(changeID);
        quickSearchBtn.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        if (!commonWaits.waitForByElementToBeVisible(By.xpath(changeRow)).getAttribute("class").contains("SelPrimary")){
            commonWaits.waitForByElementToBeVisible(By.xpath(changeRow+"//input")).click();
        }
        approveSelectedBtn.click();
        commonWaits.waitForInvisibilityOfElementLocated(By.xpath(changeRow));
    }


    public String getStatus(){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(statusField).getAttribute("value");
    }

    public void relateCIToChange(String ciName, WebDriver driver){
        String parentWindowHandler = driver.getWindowHandle();
        commonWaits.waitForByElementToBeVisible(By.xpath(planAndScheduleActions)).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(planAndScheduleRelateCi)).click();
        String subWindowHandler = null;

        Set<String> handles = driver.getWindowHandles(); // get all window handles
        for (String handle : handles) {
            subWindowHandler = handle;
        }
        driver.switchTo().window(subWindowHandler); // switch to popup window

        commonWaits.waitForByElementToBeVisible(By.xpath(planAndScheduleRelateCiName)).sendKeys(ciName);
        commonWaits.waitForByElementToBeVisible(By.xpath(planAndScheduleRelateCiSearchButton)).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(getPlanAndScheduleActionsRelateCISelectCiButton)).click();
        commonWaits.switchToFrame(selectCiToRelateIFrame);
        commonWaits.waitForByElementToBeVisible(By.xpath(selectCIToRelateAcceptPopup)).click();

        driver.switchTo().window(parentWindowHandler);
    }

    public void assignManager(Map<Object, String> changeData){
        if(changeData.get("ChangeManager") != null){
            String changeManager =  changeData.get("ChangeManager");
            commonWaits.waitForByElementToBeVisible(By.xpath(changeManagerSelect)).click();
            commonWaits.waitForByElementToBeVisible(By.xpath("//td[.='"+changeManager+"']")).click();
            commonWaits.clickByText(SolQAConstants.SAVE);
        }
    }

    public void searchChangeRequest(String changeID) {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    	WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, "Change ID")));
    	webElement.click();
    	webElement.sendKeys(changeID);
    	commonWaits.clickByText(SolQAConstants.SEARCH);
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    	webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchChangeResult, changeID)));
    	Actions action = new Actions(getDriver());
    	action.doubleClick(webElement).perform();
    	commonWaits.switchToTabByIndex(1);
    	commonWaits.waitForAngularRequestsToFinish();
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    @FindBy(xpath = "//a[@arid='301326200' and contains(. , 'Save')]")
    WebElement addApproverSaveButton;
    public Change addApproverToChange(String typeOfApprover, String firstName, String lastName) {
        commonWaits.clickWithoutInterception(By.xpath(String.format(ElementPath.linkWithARID, ElementARID.Add_Approver_Btn)));
        commonWaits.switchToTabByTitle("Add Approver");
        setDropDownField(ApproverTypeLabel, typeOfApprover);
        setTextAreaInput(First_Name_Label, firstName);
        setTextAreaInput(Last_Name_Label, lastName);
        commonWaits.sendEnterKey();
        commonWaits.waitForElementVisible(addApproverSaveButton).click();
        commonWaits.switchToTabByTitle("BMC Helix");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

}