package pages.ITSM.Apex;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import pages.ITSM.SmartIT.CreateChangePage;
import pages.ITSM.SmartIT.TicketConsolePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexSmartITPage extends BasePage{

    static SolqaLogger log;
    CommonWaits commonWaits;
    CreateChangePage createChangePage;

    //String locators
    public String filterHeader = "//*[text()='%s']//ancestor::button";
    public String fieldValue = "//ancestor::div[contains(@class,'card')]//*[text()='%s']//ancestor::label//input";
    public String selectTextFilter= "//ancestor::div[contains(@class,'card')]//input";
    public String openTicketID="//a[contains(.,'%s')]";
    public String ElementDropdownXpathByID = "//*[text()='%s']/ancestor::adapt-rx-select//button";
    public String statusDropdownCreatePage= "//*[contains(@class, 'rx-select__option-content')]/parent::button[contains(. ,'%s')]";
    String buttonContainsTextLocator = "//button[contains(.,'%s')]";
    public String tableHeadersLocatorFromApprovalList = "//div[contains(@role,'dialog')]//th";
    public String tableDataLocatorFromApprovalList = "//td[contains(@class,'at-data-cell')][%s]";

    @FindBy(xpath = "//iframe[@id='studio-frame']")
    WebElement studioFrame;
    @FindBy(xpath = "//iframe[@id='pwa-frame']")
    WebElement pwaFrame;
    @FindBy(xpath = "//*[contains(@class,'header') and contains(.,'Tickets')]")
    WebElement ticketConsoleHeader;
    @FindBy(xpath = "//button[contains(@class,'filter__dropdown')]")
    WebElement filterButton;
    @FindBy(xpath = "//button[contains(@data-testid,'Clear')]")
    WebElement clearFilterButton;
    @FindBy(xpath = "//button[contains(@data-testid,'apply')]")
    WebElement applyFilterButton;
    @FindBy(xpath = "//button[contains(@id,'304420591')]")
    WebElement editChangeButton;
    @FindBy(xpath = "//div[contains(@id,'303502600')]")
    WebElement statusDataLocator;
    @FindBy(xpath = "//button[contains(@id,'304420591')]")
    WebElement changeEditButtonElement;
    @FindBy(xpath = "//*[text()='Status']/ancestor::div[contains(@class,'menu')]//button[contains(@class,'close')]")
    WebElement clearStatusButton;
    @FindBy(xpath = "//*[text()='Status']/ancestor::div[contains(@class,'menu')]//button")
    WebElement statusDropdown;
    @FindBy(xpath = "//adapt-docked-panel//button[contains(@id,'304453531')]")
    WebElement approveRequestButton;
    @FindBy(xpath = "//div[contains(@role,'dialog')]//button[contains(@id,'304420681')]")
    WebElement refreshApprovalListButton;
    @FindBy(xpath = "//button[contains(@id,'304420681')]")
    WebElement refreshButton;

    public ApexSmartITPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(TicketConsolePage.class.getName());
        commonWaits = new CommonWaits();
        createChangePage = new CreateChangePage();
    }

    public boolean isUserNavigateToTicketConsoleApex() {
        boolean flag = false;
        switchtoDefaultFrame();
        switchToFrame(studioFrame);
        flag = commonWaits.isElementPresent(ticketConsoleHeader);
        switchtoDefaultFrame();
        return flag;
    }

    public void applyFilterAndSearchTicketApex(String ticketType, String ticketId) {
        switchToFrame(studioFrame);
        clickFilterApex();
        clearFilterApex();
        selectFilterValueApex(SolQAConstants.TICKET_TYPE_FILTER, ticketType);
        selectTextFilterApex(SolQAConstants.KEYWORDS_FILTER, ticketId);
        applyFilterApex();
        openFirstTicketApex(ticketId);
        switchtoDefaultFrame();
    }

    public void clickFilterApex() {
        log.info("Clicking on filter icon");
        clickWithoutInterception(filterButton);
    }

    public void clearFilterApex() {
        log.info("Clicking on clear filter button.");
        if(commonWaits.isElementPresent(clearFilterButton)) {
            clickWithoutInterception(clearFilterButton);
        }
    }

    public void selectFilterValueApex(String filterType, String value) {
        String filterValue = String.format(filterHeader, filterType)
                + String.format(fieldValue, value);
        if ("Published".equals(value)) {
            filterValue = String.format(filterHeader, filterType)
                    + String.format(fieldValue, value);
        }

        if ("Closed".equals(value)) {
            filterValue = String.format(filterHeader, filterType)
                    + String.format(fieldValue, value);
        }
        expandFilterApex(filterType);
        clickWithoutInterception(By.xpath(filterValue));
    }

    public void expandFilterApex(String filterType) {
        log.info("Expanding filter: " + filterType);
        scrollToElement(findElement(By.xpath(String.format(filterHeader, filterType))));
        if (findElement(By.xpath((String.format(filterHeader, filterType)))).getAttribute("class")
                .contains("collapsed")) {
            findElement(By.xpath((String.format(filterHeader, filterType)))).click();
        }
    }

    public void selectTextFilterApex(String filterType, String text) {
        String filterTextbox = String.format(filterHeader, filterType) + selectTextFilter;
        expandFilterApex(filterType);
        keyClearAndType(filterTextbox, text);
        findElement(By.xpath(filterTextbox)).sendKeys(Keys.ENTER);
    }

    public void applyFilterApex() {
        log.info("Clicking on Apply Filters");
        clickWithoutInterception(applyFilterButton);
        commonWaits.waitForExecutionComplete();
    }

    public void openFirstTicketApex(String ticketId) {
        log.info("Opening first ticket from ticket console");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.clickWithoutInterception(By.xpath(String.format(openTicketID, ticketId)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void setStatus(String status) {
        switchToFrame(createChangePage.IFrame);
        log.info("Updating the Status value");
        commonWaits.clickWithoutInterception(clearStatusButton);
        commonWaits.clickWithoutInterception(statusDropdown);
        commonWaits.clickWithoutInterception(By.xpath(String.format(buttonContainsTextLocator, status)));
        switchtoDefaultFrame();
    }

    public void approveRequestFromList() {
        switchToFrame(createChangePage.IFrame);
        log.info("Approving the request");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        clickWithoutInterception(findElement(By.xpath(String.format(buttonContainsTextLocator, SolQAConstants.SHOW_APPROVERS))));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        clickWithoutInterception(refreshApprovalListButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(approveRequestButton.isDisplayed(), "Approve button is not displayed");
        clickWithoutInterception(approveRequestButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.clickWithoutInterception(By.xpath(String.format(buttonContainsTextLocator, SolQAConstants.CLOSE)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
    }

    public void verifyChangeStatus(String status) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(createChangePage.IFrame);
        clickWithoutInterception(refreshButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState();
        log.info("Verifying the Status value");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusDataLocator, 10), "Status element is not visible");
        Assert.assertEquals(statusDataLocator.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT), status, "Status is not as expected " + status);
        switchtoDefaultFrame();
    }

    public void addScheduledStartAndEndDate(int startDaysToAdd, int endDaysToAdd) {
        log.info("Adding Start and End Date");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(createChangePage.IFrame);
        addDate(SolQAConstants.SCHEDULED_START_DATE, startDaysToAdd);
        addDate(SolQAConstants.SCHEDULED_END_DATE, endDaysToAdd);
        switchtoDefaultFrame();
    }

    public void addActualStartAndEndDate(int startDaysToAdd, int endDaysToAdd) {
        log.info("Adding Start and End Date");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(createChangePage.IFrame);
        addDate(SolQAConstants.ACTUAL_START_DATE, startDaysToAdd);
        addDate(SolQAConstants.ACTUAL_END_DATE, endDaysToAdd);
        switchtoDefaultFrame();
    }

    public void addDate(String dateType, int daysToAdd) {
        String dateTypeField = "";
        switch(dateType) {
            case SolQAConstants.SCHEDULED_START_DATE:
                dateTypeField = "1000000350";
                break;

            case SolQAConstants.SCHEDULED_END_DATE:
                dateTypeField = "1000000362";
                break;

            case SolQAConstants.ACTUAL_START_DATE:
                dateTypeField = "1000000348";
                break;

            case SolQAConstants.ACTUAL_END_DATE:
                dateTypeField = "1000000364";
                break;
            default:
                log.info("Invalid date type provided: " + dateType);
                Assert.fail("Invalid date type provided: " + dateType);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy hh:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysToAdd); // Add 2 days to current date
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        String startDate = formatter.format(calendar.getTime());

        commonWaits.waitForByElementToBeVisible(
            By.xpath(String.format(createChangePage.ElementXpathByID, dateTypeField))).sendKeys(startDate);
    }

    public void clickOnChangeEditButton() {
        log.info("Clicking on Change Edit Button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(createChangePage.IFrame);
        clickWithoutInterception(changeEditButtonElement);
        switchtoDefaultFrame();
    }

    public void clickOnSaveChangeButton() {
        log.info("Clicking on Save Change Button");
        switchtoDefaultFrame();
        switchToFrame(createChangePage.IFrame);
        createChangePage.saveChange();
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void verifyApproverFromList(String approverName) {
        switchToFrame(createChangePage.IFrame);
        log.info("Verifying the approver from list: " + approverName);
        clickWithoutInterception(findElement(By.xpath(String.format(buttonContainsTextLocator, SolQAConstants.SHOW_APPROVERS))));
        List<WebElement> headers = findElements(By.xpath(tableHeadersLocatorFromApprovalList));
        int index = 0;
        for(int i=0; i<headers.size(); i++) {
            if(headers.get(i).getText().trim().equals("Approvers")) {
                index = i + 1;
                break;
            }
        }
        String actualApproverName = findElement(By.xpath(String.format(tableDataLocatorFromApprovalList, index))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
        Assert.assertEquals(actualApproverName, approverName,"Approver name is not as expected.");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
    }

}
