package pages.ITSM.HelixDashboard;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.time.Duration;
import java.util.Map;

public class HelixDashboard extends BasePage {

    SolqaLogger log = new SolqaLogger(HelixDashboard.class.getName());
    CommonWaits commonWaits;
    Actions action;

    public HelixDashboard() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    //WebElement Xpath
    @FindBy(xpath = "//button[contains(@class,'input')][contains(text(),'Search')]")
    WebElement globalSearch;
    @FindBy(xpath = "//input[@aria-controls='kbar-listbox']")
    WebElement inputSearchBox;
    @FindBy(xpath = "(//a[@title='View Case Details'])[1]")
    WebElement AssignedCount;
    @FindBy(xpath = "//*[text()='Cases by assigned group']/ancestor::section//div[contains(@data-testid, 'panel content')]")
    WebElement casesByAssignedGroupPanel;
     @FindBy(xpath = "//button[contains(@aria-label,'Refresh')]")
    WebElement RefreshDashboardButton;
    @FindBy(xpath = "//button[contains(@aria-label, 'Time range')]")
    WebElement TimeRangeFilter;

    @FindBy(xpath = "(//label[normalize-space()='Priority']/following::button)[1]")
    WebElement removeAllButton;

    //String Xpath
    String dropDownFilter = "(//label[normalize-space()='%s']/following::button)[1]";
    String dropDownFilterValue = "//*[@role='option']//div[contains(. ,'%s')]";
    String searchedCaseID = "//a[translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='view case' and text()='%s']";
    String searchedDashboardName = "//a[@role='option']//span[text()='%s']";
    String ADERCommon_0 = "//i[@class='fa fa-spinner fa-spin fa-spin']";
    String PanelRefreshIcon = "//div[@class='panel-loading']/div[@class='css-1vzus6i-Icon']";
    String INCResult = "(//div[contains(@role,'row') and contains(.,'%s')])[1]";
    String filterTextBox = "(//label[normalize-space()='%s']/parent::div//input)[1]";


    public void searchAndOpenDashboard(String dashboardName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF, Duration.ofSeconds(40));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), globalSearch, 20), "Global search button not found on dashboard");
        commonWaits.waitForElementClickable(globalSearch).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), inputSearchBox, 20), "Input search box not found after clicking global search button");
        commonWaits.waitForElementVisible(inputSearchBox).sendKeys(dashboardName);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(searchedDashboardName, dashboardName)), 20), "Searched dashboard name not found in the list: " + dashboardName);
        commonWaits.waitForElementClickable(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchedDashboardName, dashboardName))), 10).click();
    }

    public void setFilters(String filterName, String filterValue) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownFilter, filterName)));
        commonWaits.clickWithoutInterception(ele);
        commonWaits.waitForExecutionComplete();
        action = new Actions(getDriver());
        action.sendKeys(Keys.ENTER).perform();
        commonWaits.clickWithoutInterception(RefreshDashboardButton);
    }

    public void setFiltersAndSelectValue(String filterName, String filterValue) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(filterTextBox, filterName)), 20), "Filter not found: " + filterName);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(filterTextBox, filterName)));
        commonWaits.clickWithoutInterception(ele);
        commonWaits.waitForExecutionComplete();
        ele.sendKeys(filterValue);
        action = new Actions(getDriver());
        action.sendKeys(Keys.ENTER).perform();
        commonWaits.waitForExecutionComplete();
        action.moveByOffset(10, 10).click().perform();
        commonWaits.clickWithoutInterception(RefreshDashboardButton);
    }

    public void applyFilters(Map<Object, String> helixDashboardData) {
        if (helixDashboardData.get("LOB") != null) {
            setFiltersAndSelectValue("Line of business", helixDashboardData.get("LOB"));
        }
        if (helixDashboardData.get("Company") != null) {
            setFiltersAndSelectValue("Company", helixDashboardData.get("Company"));
        }
        if (helixDashboardData.get("Assigned Group") != null) {
            setFiltersAndSelectValue("Assigned group", helixDashboardData.get("Assigned Group"));
        }
        if (helixDashboardData.get(SolQAConstants.PRIORITY) != null) {
            setFiltersAndSelectValue(SolQAConstants.PRIORITY, helixDashboardData.get(SolQAConstants.PRIORITY));
        }
        commonWaits.waitForElementVisible(casesByAssignedGroupPanel, 10);
    }

    public void openCaseFromDashboard(String CaseID) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), AssignedCount, 20), "Assigned count element not found on dashboard");
        commonWaits.waitForElementVisible(AssignedCount).click();
        commonWaits.switchToTabByIndex(2);
        commonWaits.waitForAngularRequestsToFinish();
        waitTillCompleteDashboardLoad();
        setLast30MinutesTimeRangeFilter();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(searchedCaseID, CaseID)), 20), "Searched case ID not found in the list: " + CaseID);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchedCaseID, CaseID)));
        commonWaits.scrollToElement(ele);
        commonWaits.clickWithoutInterception(By.xpath(String.format(searchedCaseID, CaseID)));
        commonWaits.switchToTabByIndex(3);
    }

    @FindBy(xpath = "//button[contains(@aria-label, 'Time range selected')]")
    WebElement TimeRangeSelected;
    @FindBy(xpath = "//label[text()='Last 30 minutes']")
    WebElement Last30MinutesOption;
    public HelixDashboard setLast30MinutesTimeRangeFilter() {
        commonWaits.waitForElementClickable(TimeRangeFilter, 5).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), Last30MinutesOption, 10), "Last 30 minutes option not found in time range filter");
        commonWaits.clickWithoutInterception(Last30MinutesOption);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    /**
     * Waits for the dashboard to completely load by checking for loading indicators
     */
    public void waitTillCompleteDashboardLoad() {
        // Wait for spinner to disappear
        try {
            if (this.commonWaits.waitForElementPresent(ADERCommon_0, 3)) {
                if (this.commonWaits.waitForInvisibilityOfElementLocated(By.xpath(ADERCommon_0))) {
                    log.info("Page loading spinner disappeared successfully");
                } else {
                    log.warn("Page loading spinner remained visible after timeout");
                }
            } else {
                log.info("Page loading spinner not found - page may already be loaded");
            }

            // Wait for panel refresh icons to disappear
            log.info("Waiting for all panel data to be loaded...");
            if (this.commonWaits.waitForElementPresent(PanelRefreshIcon, 3)) {
                if (this.commonWaits.waitForInvisibilityOfElementLocated(By.xpath(PanelRefreshIcon))) {
                    log.info("All panel data loaded successfully");
                } else {
                    log.error("Panel data failed to load");
                }
            } else {
                log.info("Panel refresh icons not found - panels may already be loaded");
            }
        } catch (Exception e) {
            log.error("Exception while waiting for dashboard to load: " + e.getMessage());
        }
    }

    public boolean verifyIncidentDataAsPerFilter(String FilterTypeValue) {
        try {
            commonWaits.scrollToBottomOfPage();
            commonWaits.waitForSometime(commonWaits.moreDelay); //This wait is required here because no other dynamic wait is working
            Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(INCResult, FilterTypeValue))), "Value not found for filter: " + FilterTypeValue);

        } catch (AssertionError e) {
            log.error("Exception while verifying incident data as per filter: refreshing the dashboard and try again" + e.getMessage());
            commonWaits.clickWithoutInterception(RefreshDashboardButton);
            commonWaits.waitForExecutionComplete();
            commonWaits.scrollToBottomOfPage();
            commonWaits.waitForSometime(commonWaits.moreDelay); //This wait is required here because no other dynamic wait is working
        }
        return commonWaits.isElementPresent(By.xpath(String.format(INCResult, FilterTypeValue)));
    }

    public void removeAllFilter() {
            int maxAttempts = 5;
            for (int i = 0; i < maxAttempts; i++) {
                    if (removeAllButton != null && removeAllButton.isDisplayed()) {
                        removeAllButton.click();
                       log.info("Clicked removeAllButton - Attempt: " + (i + 1));
                    }
                    else{
                        log.info("Remove all button not found");
                        break;
                    }
    }

}

}
