package pages.ITSM.bwf;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

/**
 * Minimal page-object for BWF case task / ad-hoc To Do flows used by DRSQA-11769.
 * Uses resilient text-based locators (no environment-specific rx-view-component-id).
 */
public class BWFToDoTasksPage extends BasePage {

    static SolqaLogger log = new SolqaLogger(BWFToDoTasksPage.class.getName());
    private final CommonWaits commonWaits;

    // ---- Locators (constants) ----
    String BUTTON_BY_TEXT = "//*[self::button or self::a][normalize-space()='%s' or contains(normalize-space(),'%s')]";
    String MENU_ITEM_BY_TEXT = "//*[self::button or self::a or self::li][contains(normalize-space(),'%s')]";
    String DROPDOWN_CONTROL_BUTTON_BY_LABEL = "//label[contains(.,'%s')]/following-sibling::*//button";
    String TASK_STATUS_CONTAINS = "//*[contains(@class,'status') and contains(.,'%s')]";
    String TaskArrow="(//div[normalize-space()='%s']//parent::*/parent::*/parent::*/parent::*//following-sibling::i)[1]";
    String AddaTaskButton="//button[@btn-type='tertiary' and normalize-space()='%s']";
    String AddToDoTaskTextFields="//span[normalize-space()='%s']//parent::label//parent::div//parent::adapt-rx-control-label//following-sibling::div//input";

    //WebElement locators
    @FindBy(xpath ="//div[@class='bwf-seperator-hover-line' and normalize-space()='Add a task']")
	WebElement AddATaskOption;

    @FindBy(xpath ="//button[contains(@class,'d-icon-cross')]")
	WebElement crossIcon;

    @FindBy(xpath ="//rx-action-button[@rx-view-component-id='d94b76e0-a0c4-4def-9fb9-d51bdfff6ae2']//button")
	WebElement toDoTaskRefreshButton;

    @FindBy(xpath = "//span[@class='popup-person']/ancestor::button")
	public WebElement PersonName;

    @FindBy(css = "[rx-view-component-id='641936df-ed72-4878-943c-0a7382602bac'] button")
	public WebElement lnk_saveCase;

    @FindBy(xpath ="(//div[contains(@class,'task-assignee')]//button)[1]")
	WebElement assignToMeButtonForTask;

    @FindBy(xpath ="(//button[normalize-space()='Mark complete'])[2]")
	WebElement markCompleteTask;

    private static final int DEFAULT_WAIT_MS = 2000;

    public BWFToDoTasksPage() {
        PageFactory.initElements(getDriver(), this);
        this.commonWaits = new CommonWaits();
    }

    public BWFToDoTasksPage openTaskUsingArrow(String taskName) {
        log.info("Opening task using arrow: " + taskName);
        WebElement ele= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TaskArrow, taskName)));
		commonWaits.clickWithoutInterception(ele);
		commonWaits.waitForExecutionComplete();
        return this;
    }

    public boolean verifyAddATaskOption(String option1, String option2) {
        log.info("Verifying Add a task option: " + option1 + " and " + option2);
        commonWaits.waitForElementVisible(AddATaskOption);
        Actions actions = new Actions(getDriver());
        actions.moveToElement(AddATaskOption).build().perform();
        WebElement FirstOption= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, option1)));
        WebElement SecondOption= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, option2)));
        return FirstOption.isDisplayed() && SecondOption.isDisplayed();
    }

    public boolean verifyAddATaskSubOption(String subOption, String Option1, String Option2) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, subOption))).click();
	    WebElement FirstOption= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, Option1)));
	    WebElement SecondOption= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, Option2)));
        return FirstOption.isDisplayed() && SecondOption.isDisplayed();
    }

    public BWFToDoTasksPage goToAddParallelAdhocToDoTask(String addTaskLabel, String addParallelLabel, String addAdhocLabel, String addToDoLabel) {
        if(crossIcon.isDisplayed()) {
			crossIcon.click();
		}
	    commonWaits.waitForElementVisible(toDoTaskRefreshButton).click();
		commonWaits.waitForElementVisible(AddATaskOption);
		Actions actions = new Actions(getDriver());
	    actions.moveToElement(AddATaskOption).build().perform();
	    WebElement Option1= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, SolQAConstants.ADD_A_PARALLEL_TASK)));
	    commonWaits.clickWithoutInterception(Option1);
	    WebElement Option2= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, SolQAConstants.ADD_AN_ADHOC_TASK)));
	    commonWaits.clickWithoutInterception(Option2);
	    WebElement Option3= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, SolQAConstants.TO_DO)));
	    commonWaits.clickWithoutInterception(Option3);
        return this;
    }

    public BWFToDoTasksPage goToAddSequentialAdhocToDoTask(String addTaskLabel, String addSequentialLabel, String addAdhocLabel, String addToDoLabel) {
        if(commonWaits.isElementPresent(crossIcon)) {
			crossIcon.click();
		}
	    commonWaits.waitForElementVisible(toDoTaskRefreshButton).click();
		commonWaits.waitForElementVisible(AddATaskOption);
		Actions actions = new Actions(getDriver());
	    actions.moveToElement(AddATaskOption).build().perform();
	    WebElement Option1= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, SolQAConstants.ADD_A_PARALLEL_TASK)));
	    commonWaits.clickWithoutInterception(Option1);
	    WebElement Option2= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, SolQAConstants.ADD_AN_ADHOC_TASK)));
	    commonWaits.clickWithoutInterception(Option2);
	    WebElement Option3= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddaTaskButton, SolQAConstants.TO_DO)));
	    commonWaits.clickWithoutInterception(Option3);
        return this;
    }

    public void setAddAToDoTaskTextFields(String FieldName, String FieldValue)  {
        switch(FieldName) {
            case SolQAConstants.TASK_TITLE: commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddToDoTaskTextFields, SolQAConstants.TASK_TITLE))).sendKeys(FieldValue);
             break;
             
            case SolQAConstants.TEMPLATE_NAME: commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddToDoTaskTextFields, SolQAConstants.TEMPLATE_NAME))).sendKeys(FieldValue);
            break;
             
            case SolQAConstants.TASK_SUMMARY: commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddToDoTaskTextFields, SolQAConstants.TASK_SUMMARY))).sendKeys(FieldValue);
            break;
            
            case SolQAConstants.TARGET_DAYS_TO_COMPLETE: commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddToDoTaskTextFields, SolQAConstants.TARGET_DAYS_TO_COMPLETE))).sendKeys(FieldValue);
            break;
    
            case SolQAConstants.ASSIGNEE: WebElement assigneeField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AddToDoTaskTextFields, SolQAConstants.ASSIGNEE)));
            assigneeField.sendKeys(FieldValue);
            commonWaits.clickWithoutInterception(PersonName);
            break;
        }
        }

    public BWFToDoTasksPage setAddAToDoTaskDropDownFields(String fieldLabel, String option) {
        if (option == null) {
            return this;
        }
        // Try to click dropdown control by label and pick option
        By control = By.xpath(String.format(DROPDOWN_CONTROL_BUTTON_BY_LABEL, fieldLabel));
        if (isElementPresent(control)) {
            commonWaits.clickWithoutInterception(control);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            By opt = By.xpath(String.format(MENU_ITEM_BY_TEXT, option));
            commonWaits.clickWithoutInterception(opt);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        return this;
    }

    public BWFToDoTasksPage save_ToDoTask() {
        commonWaits.waitForElementClickable(lnk_saveCase);
			lnk_saveCase.click();
			commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFToDoTasksPage close_ToDoTask() {
        tryClickIfPresent(By.xpath(String.format(BUTTON_BY_TEXT, "Close", "Close")));
        tryClickIfPresent(By.xpath(String.format(BUTTON_BY_TEXT, "Cancel", "Cancel")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFToDoTasksPage assignTaskToMe() {
            getDriver().navigate().refresh();
            commonWaits.waitForElementVisible(assignToMeButtonForTask);
            commonWaits.clickWithoutInterception(assignToMeButtonForTask);
            commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFToDoTasksPage markCompleteTask(String taskName) {
        openTaskUsingArrow(taskName);
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.waitForElementVisible(toDoTaskRefreshButton).click();
		commonWaits.waitForElementVisible(markCompleteTask);
		commonWaits.waitForElementClickable(markCompleteTask);
		commonWaits.clickWithoutInterception(markCompleteTask);
		commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFToDoTasksPage verifyTaskStatus(String expectedStatus) {
        By statusBy = By.xpath(String.format(TASK_STATUS_CONTAINS, expectedStatus));
        Assert.assertTrue(isElementPresent(statusBy), "Expected task status not found: " + expectedStatus);
        return this;
    }

    private void tryClickIfPresent(By by) {
        try {
            if (isElementPresent(by)) {
                commonWaits.clickWithoutInterception(by);
                commonWaits.waitForSometime(DEFAULT_WAIT_MS);
            }
        } catch (Exception ignored) {}
    }

}

