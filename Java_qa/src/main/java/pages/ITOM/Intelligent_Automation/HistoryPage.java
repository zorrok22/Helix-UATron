package pages.ITOM.Intelligent_Automation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.Duration;
import framework.BasePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import util.CommonWaits;

public class HistoryPage extends BasePage {


    CommonWaits commonWaits=new CommonWaits();
    public static int timeout = 15;

    WebDriverWait wait;

    private static Logger logger = Logger.getLogger(HistoryPage.class);

    public String executionModexpath="//table//tbody//tr[1]//td[4]//span";
    public String statusXpath="//table//tbody//tr[1]//td[5]//span";

    @FindBy(xpath = "//a[@href='#/policy/history']")
    WebElement historyTab;

    @FindBy(xpath = "//span[text()='Automation Policy Runs']")
    WebElement historyPageText;

    @FindBy(xpath = "//*[text()=\"Search\"]")
    WebElement policyNameSearchIcon;

    @FindBy(xpath = "//span[text()='Refresh']/..")
    WebElement refresh;

    @FindBy(xpath = "//span[text()='Automation Policy Runs']")
    WebElement HistoryTableHeading;

    @FindBy(xpath = "//span[text()='Start Date / Time']")
    WebElement tableHeadStartTime;

    @FindBy(xpath = "//span[text()='Automation Policy']")
    WebElement tableHeadPolicyName;

    @FindBy(xpath = "//span[text()='Instance ID']")
    WebElement tableHeadInstanceId;

    @FindBy(xpath = "//span[text()='Execution Mode']")
    WebElement tableHeadExecutionMode;

    @FindBy(xpath = "//span[text()='Status']")
    WebElement tableHeadStatus;

    @FindBy(xpath = "//span[text()='Duration']")
    WebElement tableHeadDuration;

    @FindBy(xpath = "//span[text()='Success']")
    WebElement labelSuccessful;

    @FindBy(xpath = "//span[text()='Running']")
    WebElement labelRunning;

    @FindBy(xpath = "//span[text()='Failed']")
    WebElement labelFailed;

    @FindBy(xpath = "//button[contains(.,'Advanced filter')]")
    WebElement buttonAdvancedFilter;

    @FindBy(xpath = "//table//tbody//tr[contains(@class,'at-row')]")
    List<WebElement> tableRowNum;

    @FindBy(xpath = "//button/span[contains(@class,'right')]/..")
    WebElement paginationNextButton;

    @FindBy(xpath = "//button/span[contains(@class,'left')]/..")
    WebElement paginationPreviousButton;

    @FindBy(xpath = "//p[text()='No items available']")
    WebElement noItemMessage;

    @FindBy(xpath = "//button[contains(.,'Clear')]")
    WebElement clearButton;

    @FindBy(xpath = "(//*[text()='Status'])[2]")
    WebElement filter_Status_ellipsis;

    @FindBy(xpath = "//div[text()='Failed']")
    WebElement failedStatusCheckbox;

    @FindBy(xpath = "(//*[text()='Execution Mode'])[2]")
    WebElement filter_ExecutionMode_ellipsis;

    @FindBy(xpath = "//*[text()='Manual']")
    public WebElement manual_ExectionMode_Checkbox;

    @FindBy(xpath = "//adapt-rx-select[contains(@class,'advanced-filter__rx-select')]//adapt-highlight[text()='Success']")
    public WebElement success_Status_Checkbox;

    @FindBy(xpath = "//adapt-rx-select[contains(@class,'advanced-filter__rx-select')]//adapt-highlight[text()='Failed']")
    public WebElement failed_Status_Checkbox;

    @FindBy(xpath = "//adapt-rx-select[contains(@class,'advanced-filter__rx-select')]//adapt-highlight[text()='Manual']")
    public WebElement manual_Mode_Checkbox;

    @FindBy(xpath = "//button[contains(.,'Apply filters')]")
    public WebElement apply_Filters_Button;

    @FindBy(xpath = "//button[contains(.,'Remove all')]")
    WebElement remove_all_Button;

    @FindBy(xpath = "//button[contains(@aria-label, 'Remove Start Date')]")
    WebElement remove_filter;

    @FindBy(xpath = "//button[contains(.,'Cancel')]")
    WebElement cancel_Filters_Button;

    @FindBy(xpath = "//button[contains(.,'active')]")
    WebElement active_Filters_Text;

    @FindBy(xpath = "//span[text()='Clear']")
    WebElement clearFilterButton;

    @FindBy(xpath = "(//*[text()='Start Date'])")
    public WebElement startDateAndTime_ellipsis;

    @FindBy(xpath = "(//*[contains(@data-testid,'icon-angle_left')])[1]")
    public WebElement previousMonthAdaptIcon;

    @FindBy(xpath = "(//button[contains(.,' Start ')])[2]")
    public WebElement startDate;

    //span[contains(.,'Start Date:')]
    @FindBy(xpath = "//p[contains(.,'No filters are being applied')]")
    public WebElement noFilter;

    @FindBy(xpath = "//div[@class='ellipsis'][contains(.,'Policy Tags')]")
    public WebElement filter_PolicyTags_ellipsis;

    @FindBy(xpath = "//div[@role='alert'][contains(.,'This field is required')]")
    WebElement valueErrorMessage;

    @FindBy(xpath = "//input[@data-testid='tag-key+1_input']")
    WebElement keyFilter;

    @FindBy(xpath = "//input[contains(@id,'tag-value+1')]")
    WebElement valueFilter;

    @FindBy(xpath = "(//span[normalize-space()='OS'])[1]")
    WebElement keyPopup;

    @FindBy(xpath = "//button[1]//adapt-highlight[1]")
    WebElement valuePopup;

    @FindBy(xpath = "//span[normalize-space()='class']")
    WebElement classPopup;

    @FindBy(xpath = "//button[1]//adapt-highlight[1]//span[1]")
    WebElement alarmPopup;

    @FindBy(xpath = "//span[text()='Waiting']/following-sibling::span")
    public WebElement WaitingLabelCount;

    @FindBy(xpath = "//span[text()='Rejected']/following-sibling::span")
    public WebElement RejectedLabelCount;

    @FindBy(xpath="//td[text()='Scheduled For Approval']")
    public WebElement scheduledForApprove;

    @FindBy(xpath="//span[contains(.,'Change Creation Failed')]")
    public WebElement changeTicketIdFailed;

    @FindBy(xpath="//td[text()='Draft']")
    public WebElement statusDraft;

    @FindBy(xpath="//td[text()='Planning In Progress']")
    public WebElement planningInProgress;

    @FindBy(xpath="//td[text()='Scheduled For Review']")
    public WebElement reviewScheduled ;

    @FindBy(xpath="//td[text()='Implementation In Progress']")
    public WebElement implementationInProgress;

    @FindBy(xpath="//td[text()='Scheduled']")
    public WebElement scheduled;

    @FindBy(xpath="//td[text()='Scheduled For Approval']")
    public WebElement scheduledForApproval;

    @FindBy(xpath="//td[text()='Request For Change']")
    public WebElement requesrForChange;

    @FindBy(xpath="//td[text()='Request For Authorization']")
    public WebElement requesrForAuthorization;

    @FindBy(xpath="//td[text()='Closed']")
    public WebElement closedStatus;

    @FindBy(xpath="//div[contains(text(),'Waiting')]")
    public WebElement waitingStatus;

    @FindBy(xpath="//div[contains(text(),'Success')]")
    public WebElement successStatus;

    @FindBy(xpath="//td[contains(text(),'Cancelled')]")
    public WebElement statusCancelled;

    @FindBy(xpath = "//span[text()='Successful']/following-sibling::span")
    public WebElement successLabelCount;


    @FindBy(xpath = "//button[contains(@data-testid,'_end')]")
    public WebElement endDateDropdown;

    @FindBy(xpath = "//input[contains(@aria-valuenow,'Set time')]")
    public WebElement startTimeDropdown;

    @FindBy(xpath = "//input[contains(@aria-label,'hours')]")
    public WebElement selectHour;

    @FindBy(xpath = "//input[contains(@aria-label,'minutes')]")
    public WebElement selectMinutes;

    @FindBy(xpath = "//span[text()='AM']")
    WebElement selectAMTime;

    @FindBy(xpath = "//span[text()='PM']")
    WebElement endTime;

    @FindBy(xpath = "//adapt-tabset[@class='advanced-filter__tab-container ng-tns-c130-6 ng-tns-c121-11 adapt-tabset ng-star-inserted']")
    WebElement scrollBar;

    @FindBy(xpath = "//input[contains(@aria-valuenow,'Select time')]")
    WebElement EndTimeDropDown;


    @FindBy(xpath = "//adapt-table-toolbar//input")
    WebElement eventIDSearch;


    @FindBy(xpath = "//button[@class='ui-paginator-page ui-paginator-element ui-state-default ui-corner-all ng-star-inserted'][1]")
    WebElement navigateToNextPage;

    @FindBy(xpath = "/html/body/app-root/home-page/div/app-automation/div/automation-history/div/div/div/div[2]/div/div")
    WebElement getTable;

    @FindBy(xpath = "//adapt-rx-select[contains(@class,'advanced-filter__rx-select')]//adapt-highlight[text()='Automatic']")
    public WebElement automatic_Mode_Checkbox;

    @FindBy(xpath = "//div[text()=' Create or update an Incident ']")
    public WebElement createupdatetab;

    @FindBy(xpath = "//span[text()='result']")
    public WebElement resultString;

    @FindBy(xpath = "//button[@class='close']")
    public WebElement closeButton;

    @FindBy(xpath = "//button[@aria-controls='adapt-tabs-dropdown-event-details']")
    public WebElement eventDetailsTab;

    @FindBy(xpath = "//span[text()='Event ID is copied!']")
    WebElement eventIdCopyMsg;

    @FindBy(xpath = "//div[text()='Start value cannot be after the end value']")
    WebElement ErrorMsgForTime;

    @FindBy(xpath = "//button[@class='btn btn-link adapt-table-toolbar__btn-link']")
    WebElement coloumnSelectorIcon;

    @FindBy(xpath = "(//span[text()='Start Date / Time'])[2]")
    WebElement startDateTimeCheckbox;

    @FindBy(xpath = "(//span[text()='Automation Policy'])[2]")
    WebElement automationPolicyCheckbox;

    @FindBy(xpath = "(//span[text()='Event ID'])[2]")
    WebElement eventIDCheckbox;

    @FindBy(xpath = "(//span[text()='Execution Mode'])[2]")
    WebElement executionModeCheckbox;

    @FindBy(xpath = "(//span[text()='Status'])[2]")
    WebElement statusCheckbox;

    @FindBy(xpath = "(//span[text()='Duration'])[2]")
    WebElement durationCheckbox;

    @FindBy(xpath = "(//span[text()='Ticket ID'])[2]")
    WebElement ticketIdCheckbox;

    @FindBy(xpath = "(//span[text()='Instance ID'])[2]")
    WebElement instanceIdCheckbox;

    @FindBy(xpath = "//button[@class='btn btn-link adapt-table-toolbar__btn-link']")
    WebElement coloumnSelectorClosedIcon;

    @FindBy(xpath = "//button[contains(text(),'Bulk Actions')]")
    WebElement bulkActionDDL;

    @FindBy(xpath = "//*[contains(text(),'Delete Automation Runs')]")
    WebElement deleteAutomationRuns;

    @FindBy(xpath = "//button[contains(text(),'Confirm')]")
    WebElement confirm_delete;

    @FindBy(xpath = "//button[@aria-label='Remove Status: Success']")
    public WebElement statusFilter;

    @FindBy(xpath = "//a[@aria-label='Remove Execution Mode: Automatic']")
    public WebElement execution_Mode_filter;

    @FindBy(xpath = "//a[@aria-label='Remove Execution Mode: Manual']")
    public WebElement manual_Mode_filter;

    @FindBy(xpath = "//span[@class='d-icon-ellipsis ng-star-inserted']")
    public WebElement advanced_filter_eclipse;

    @FindBy(xpath = "//td[@data-testid=\"adapt-table_column-0_row-0\"]//span")
    public WebElement history_TimeZone;

    @FindBy(xpath = "//button[@id='searchDropdown']")
    public WebElement History_by_Event_ID;

    @FindBy(xpath = "//button[contains(text(), 'Automation Policy')]")
    public WebElement History_by_Automation_Policy;

    @FindBy(xpath = "//button[contains(text(), 'Event ID')]")
    public WebElement Search_by_Event_ID;

    @FindBy(xpath = "//table//tbody//tr[1]//td[2]//a")
    public WebElement firstPolocyHistoryResult;

    @FindBy(xpath = "//workflow-details//button[@role='tab']//span[contains(text(),'Output')]")
    public WebElement policyHistoryOutput;

    @FindBy(xpath = "//adapt-rx-search")
    public WebElement searchPolicy;

    @FindBy(xpath = "//input[contains(@placeholder,'Search by using policy name')]")
    public WebElement searchByPolicyName;

    @FindBy(xpath = "//table//tbody//tr[1]//td[2]//div")
    public WebElement policyTextLocator;

    @FindBy(xpath = "//table//tbody//tr[1]//td[4]//span")
    public WebElement executionModeLocator;

    @FindBy(xpath = "//table//tbody//tr[1]//td[5]//div")
    public WebElement statusValueLocator;

    @FindBy(xpath = "//button[@id='searchDropdown']")
    WebElement searchDropdownMenu;

    @FindBy(xpath = "//input[contains(@placeholder,'Search by using policy name')]")
    WebElement historyPageSearchBox;

    @FindBy(xpath = "//input[@placeholder='Search by Event ID']")
    WebElement historyPageSearchBoxByEventID;

    @FindBy(xpath = "(//table//td)[7]//a")
    WebElement ticketIDChangeRequest;

    String selectOptionFromSearchDropdown = "//button[contains(.,'%s')]";

    String jsonFieldxpath = "//nxt-json-view-item//span[contains(text(),'%s')]";

    private final WebDriver driver;

    public HistoryPage() {
        // TODO Auto-generated constructor stub
        this.driver = getDriver();
        PageFactory.initElements(this.driver, this);
        wait=new WebDriverWait(this.driver, Duration.ofSeconds(60));

    }

    // Verify first row status and mode after filter History Page
    public HistoryPage verifyFirstFilterResult(String executionModeText, String statusText) {
        commonWaits.waitForElementVisible(refresh);
        refresh.click();
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.loader(getDriver());
        commonWaits.waitForSometime(commonWaits.minDelay);
        String executionMode = findElement(By.xpath(executionModexpath)).getText().trim();
        Assert.assertEquals(executionMode, executionModeText, "Execution mode is different");
        commonWaits.waitForSometime(commonWaits.minDelay);
        String status = findElement(By.xpath(statusXpath)).getText().trim();
        Assert.assertEquals(status, statusText, "Status is different");
        return this;
    }

    public String fetchChangeTicketIDFromFirstRow() {
        commonWaits.waitForElementClickable(ticketIDChangeRequest);
        String ticketID = ticketIDChangeRequest.getText().trim();
        return ticketID;
    }

    public void openFirstResultAndCheckJsonField(String jsonField){
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(firstPolocyHistoryResult).click();
        commonWaits.waitForElementVisible(policyHistoryOutput);
        String xpath = String.format(jsonFieldxpath, jsonField);
        try {
            boolean displayed = findElement(By.xpath(xpath)).isDisplayed();
            Assert.assertTrue(displayed, "Expected JSON value '" + jsonField + "' is not found in the IA Policy History.");
            commonWaits.waitForElementClickable(closeButton).click();
        }catch(NoSuchElementException e){
            throw new AssertionError("Expected JSON value '" + jsonField + "' is not found in the IA Policy History.");
        }

    }


    public static String getCurrentDay() {
        DateFormat sdf = new SimpleDateFormat("dd");
        Calendar currentDate = Calendar.getInstance();
        currentDate.get(Calendar.DATE);
        Date d = currentDate.getTime();
        String current_Day = sdf.format(d);
        return current_Day;
    }



    //Search create Policy
    public HistoryPage searchPolicy(String finalpolicyName) {
        commonWaits.waitForElementClickable(History_by_Event_ID).click();
        commonWaits.waitForElementVisible(History_by_Automation_Policy).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(searchPolicy).click();
        commonWaits.waitForElementClickable(searchByPolicyName).clear();
        commonWaits.waitForElementClickable(searchByPolicyName).sendKeys(finalpolicyName);
        commonWaits.waitForPageReadyState(getDriver());
        int rowNum = getSearchedCount();
        Assert.assertNotNull(rowNum);
        return this;
    }
    //get Searched Count
    public int getSearchedCount() {
        commonWaits.waitForPageLoad(driver);
        commonWaits.loader(driver);
        wait.until(ExpectedConditions.visibilityOf(tableRowNum.get(0)));
        tableRowNum.get(0).isDisplayed();
        int rowNum = tableRowNum.size();
        return rowNum;
    }

    public HistoryPage verifyfilterResultOnHistoryPage(String optionName, String PolicyName, String executionModeText, String statusText) {
        commonWaits.waitForElementClickable(searchDropdownMenu);
        searchDropdownMenu.click();
        selectOptionFromSearchDropdown = String.format(selectOptionFromSearchDropdown, optionName);
        WebElement dropDownOptionButtonElement = commonWaits.waitForByElementToBeVisible(By.xpath(selectOptionFromSearchDropdown));
        clickToElement(dropDownOptionButtonElement);
        commonWaits.waitForElementClickable(historyPageSearchBox);
        historyPageSearchBox.clear();
        historyPageSearchBox.sendKeys(PolicyName+ Keys.ENTER);
        String policyText = policyTextLocator.getText().trim();
        String executionMode = executionModeLocator.getText().trim();
        String statusValue = statusValueLocator.getText().trim();
        Assert.assertEquals(policyText, PolicyName);
        Assert.assertEquals(executionMode, executionModeText);
        Assert.assertEquals(statusValue, statusText);
        return new HistoryPage();
    }





    /**
     * Verify filter result by Event ID in History page
     * @param EventID The event ID to search for
     * @param executionModeText Expected execution mode (Manual/Automatic)
     * @param statusText Expected status (SUCCESS/FAILED)
     */
    public HistoryPage verifyfilterResultForID(String EventID, String executionModeText, String statusText) {
        try {
            logger.info("Verifying filter result for Event ID: " + EventID);
            // Click refresh button
            commonWaits.waitForElementVisible(refresh);
            refresh.click();
            commonWaits.waitForPageReadyState(driver);
            commonWaits.waitForElementVisible(historyPageSearchBoxByEventID).clear();
            commonWaits.waitForElementVisible(historyPageSearchBoxByEventID).sendKeys(EventID + Keys.ENTER);
            commonWaits.waitForSometime(commonWaits.minDelay);
            
            // Verify execution mode
            String executionMode = commonWaits.waitForElementVisible(getElement(executionModexpath)).getText().trim();
            Assert.assertEquals(executionMode, executionModeText, 
                "Execution mode doesn't match. Expected: " + executionModeText + ", Actual: " + executionMode);
            
            // Verify status
            String statusValue = commonWaits.waitForElementVisible(getElement(statusXpath)).getText().trim();
            Assert.assertEquals(statusValue, statusText, 
                "Status doesn't match. Expected: " + statusText + ", Actual: " + statusValue);
                
            logger.info("Successfully verified filter result for Event ID: " + EventID);
        } catch (Exception e) {
            logger.error("Failed to verify filter result for Event ID '" + EventID + "': " + e.getMessage());
            throw new AssertionError("Failed to verify filter result for Event ID '" + EventID + "': " + e.getMessage());
        }
        return this;
    }


}