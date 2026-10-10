package pages.ITSM.bwf;

import framework.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class BWFTask extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(BWFTask.class.getName());
    CommonWaits commonWaits;

    @FindBy(xpath = "//button[contains(@class,'status-transition')]//span[1]")
    WebElement TASK_STATUS;
    @FindBy(xpath = "(//*[@rx-view-component-id='2b3842ef-eb67-486a-a61e-2a274e0dc508']//div)[2]")
    WebElement TASK_ID_FALLBACK;
    @FindBy(xpath = "//*[@rx-view-component-id='f61d4798-be16-46e3-88a8-c9bd439965b6']//span")
    WebElement TASK_SUMMARY_FALLBACK;
    @FindBy(xpath = "//*[contains(@aria-label, 'Display Id')]")
    WebElement DISPLAY_ID;
    @FindBy(xpath = "//*[@rx-view-component-id='6759ba60-df0d-4d5e-8eb9-5101490fd4d4']//button")
    WebElement saveTaskButton;
    @FindBy(xpath = "//*[contains(@class,'task') and contains(@class,'summary')]//span")
    WebElement taskSummary;
    @FindBy(xpath = "(//bwf-task-card[contains(@class,'task')]//adapt-tag[contains(@class,'bwf-task-status-tag')]//span[contains(@class,'a-tag__content')])[1]")
	WebElement ToDoTaskStatus1;
    @FindBy(xpath = "(//bwf-task-card[contains(@class,'task')]//adapt-tag[contains(@class,'bwf-task-status-tag')]//span[contains(@class,'a-tag__content')])[2]")
   	WebElement ToDoTaskStatus2;
    @FindBy(xpath = "(//bwf-task-card[contains(@class,'task')]//adapt-tag[contains(@class,'bwf-task-status-tag')]//span[contains(@class,'a-tag__content')])[3]")
   	WebElement ToDoTaskStatus3;
    @FindBy(xpath ="//button[@title='Refresh']")
	WebElement toDoTaskRefreshButton;

    @FindBy(xpath = "(//rx-action-button[@rx-view-component-id='d94b76e0-a0c4-4def-9fb9-d51bdfff6ae2']//button)[2]")
    WebElement toDoTaskRefreshButton2;

    public BWFTask() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public String getTaskId() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (commonWaits.isElementPresent(DISPLAY_ID)) {
            return commonWaits.waitForElementVisible(DISPLAY_ID).getText().trim();
        }
        return commonWaits.waitForElementVisible(TASK_ID_FALLBACK).getText().trim();
    }

    public boolean verifyTaskStatus(String value) {
			commonWaits.waitForSometime(commonWaits.moreDelay);
            if(commonWaits.isElementPresent(toDoTaskRefreshButton2)){
                try{
                commonWaits.clickByAction(toDoTaskRefreshButton2);
                }
                catch(Exception e){
                    log.info("No refresh button found for task: " + e.getMessage());
                }
            }else if(commonWaits.isElementPresent(toDoTaskRefreshButton)){
                commonWaits.clickByAction(toDoTaskRefreshButton);
            }
		    commonWaits.waitForSometime(commonWaits.minDelay);
            String statusText1 =  commonWaits.waitForElementVisible(ToDoTaskStatus1).getText().trim();  // Get text of each status
            String statusText2 =  commonWaits.waitForElementVisible(ToDoTaskStatus2).getText().trim();  // Get text of each status
            if (!statusText1.equals(value) && !statusText2.equals(value) ) {
                return false;  // If any status is not "Completed", return false
            }
            return true; 
    }

    @FindBy(xpath = "//*[@rx-view-component-id='b6ce11f9-1a48-4753-9cd4-023af695a44c']//button")
    WebElement taskStatusField;
    public String getTaskStatus() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), taskStatusField, 5), "");
        return taskStatusField.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
    }

    public BWFTask verifyTaskSummary(String expectedSummary) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        String actual;
        if (commonWaits.isElementPresent(TASK_SUMMARY_FALLBACK)) {
            actual = commonWaits.waitForElementVisible(TASK_SUMMARY_FALLBACK).getText().trim();
        } else {
            // Generic fallback: look for summary-like visible text on task header
            actual = commonWaits.waitForElementVisible(taskSummary).getText().trim();
        }
        Assert.assertEquals(actual, expectedSummary, "Task summary mismatch");
        return this;
    }
}