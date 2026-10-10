package pages.ITSM.SmartReporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartReportingLandingPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    String ViewFullIncident = "View full incident";

    //String locator
    String contentDashboardLink = "//div[@title='%s' and contains(@class, 'content')]";
    String folderLink = "//div[contains(@class,'subFolders')]//div[contains(@title, 'replaceMe')]";
    String subFolderLink = "//div[contains(@class,'subFolders')]//div[contains(@title, 'replaceMe')]";
    String longRunningReportDialog = "//div[contains(text(),'Long Running Report')]";
    String chatbotData = "//tr[contains(@class, 'CrosstabData')][%s]//td";
    String availableIncidents ="//td[contains(@title,'INC')]//parent::tr//a[contains(., 'INC')]";
    String View_Full_Incident = "//a[normalize-space()='%s']";
    String iframe = "//*[contains(@class,'preview-panel')]//iframe";
    String ticketIframe = "//iframe[@id='pwa-frame']";
    String reportItemContainer = "//*[@class='browseItemHolder']//*[@title='%s']";
    String requestIDAsPerCompanyName = "(//*[@title='%s']/preceding-sibling::td//a)[1]";

    //WebElements Locators
    @FindBy(xpath = "//*[@id='ar1000000161_data']")
    WebElement ticketID;

    @FindBy(xpath = "//*[@arid = '1000000161']")
    WebElement incidentOpenedInNewTab;

    @FindBy(xpath = "//div[contains(@class,'searchBar')]//input")
    WebElement SearchTextBox;

    @FindBy(xpath = "//div[@class='searchImgContainer']")
    WebElement SearchButton;

    @FindBy(xpath = "//td[contains(@class, 'reportOutputTitle')]")
    WebElement reportOutputLink;

    @FindBy(xpath = "(//div[@class = 'dateSelector'])[last()]//*[@class = 'broadcastInput inputOne']")
    WebElement btn_fromDate;

    @FindBy(xpath = "(//div[@class = 'dateSelector'])[last()]//*[@class = 'broadcastInput inputTwo']")
    WebElement btn_toDate;

    @FindBy(xpath = "(//div[@class = 'dateSelector'])[last()]//*[@class = 'applyButton']")
    WebElement btn_dateApply;

    @FindBy(xpath = "(//*[@class = 'broadcastInput rangeDisplay'])[1]")
    WebElement calender_icon;

    @FindBy(xpath = "//*[(@class= 'submitMidHighlightText') and contains(text(),'Go')]")
    WebElement btn_go;

    @FindBy(xpath = "//div[@class='filterOutputBreadcrumbItem ']")
    WebElement reportUp;

    @FindBy(xpath = "//*[@class = 'datepickerContainer']")
    WebElement datePicker;

    @FindBy(xpath = "(//div[@id= 'submit']//div)[last()]")
    WebElement SubmitButton_OnLongRunningReport;

    @FindBy(xpath = "//p[@class='logoTitle' and normalize-space()='Smart Reporting']")
    WebElement homePageTitle;

    @FindBy(xpath = "//div[contains(@class ,'searchBar')]//input")
    WebElement txtBox_searchBar;

    @FindBy(xpath ="//*[@class='filterList']/following-sibling::div//*[text()='Go']")
    WebElement filterGoButton;

    @FindBy(xpath = "//*[@title='Search Content']")
    WebElement searchBtn;

    public SmartReportingLandingPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(SmartReportingLandingPage.class.getName());
        commonWaits=new CommonWaits();
    }

    public void verifyNavigationToSmartReporting() {
        List<String> getTabs = new ArrayList<>(getDriver().getWindowHandles());
        if (getTabs.size() == 1) {
            Assert.fail("-------FAIL--Reports tab did not open. Possibly Reporting is not configured");
        }
        WebDriver currentDriver = getDriver().switchTo().window(getTabs.get(1));
        setDriver(currentDriver);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
    }

    public void verifySmartReportingHomePage(){
        waitForSmartReportingSpinnersToDisappear();
        Assert.assertTrue(commonWaits.waitForVisibleElement(homePageTitle), "Smart reporting home page is visible");
    }

    public void waitForSmartReportingSpinnersToDisappear () {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
    }

    public void searchFolder(String folderName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
        commonWaits.waitForElementClickable(SearchTextBox).sendKeys(folderName);
        commonWaits.waitForElementClickable(SearchButton).click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementClickable(SearchTextBox).clear();
        commonWaits.waitForElementClickable(SearchButton).click();
        commonWaits.waitForExecutionComplete();
    }

    public void clickOnFolder(String folderName) {
        waitForSmartReportingSpinnersToDisappear();
        log.info("Clicking on content dashboard link on Smart reporting tab ");
        folderLink = folderLink.replace("replaceMe",folderName);
        try {
            WebElement weContentLink = getDriver().findElement(By.xpath(folderLink));
            commonWaits.waitForElementClickable(weContentLink);
            weContentLink.click();
        }
        catch(Exception e) {
            Assert.fail("Folder with name " + folderName + "not found on smart reporting.");
        }
    }

    public void clickOnSubFolder(String folderName) {
        waitForSmartReportingSpinnersToDisappear();
        log.info("Clicking on content dashboard link on Smart reporting tab ");
        subFolderLink = subFolderLink.replace("replaceMe",folderName);

        try {
            WebElement weContentLink = getDriver().findElement(By.xpath(subFolderLink));
            commonWaits.waitForElementClickable(weContentLink);
            commonWaits.clickWithoutInterception(weContentLink);
        }
        catch(Exception e) {

            Assert.fail("Folder with name " + folderName + "not found on smart reporting.");
        }

    }

    public void clickContentDashBoardLinkForVerifyingDetails(String contentName) {
        waitForSmartReportingSpinnersToDisappear();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        getDriver().navigate().refresh();
        log.info("Clicking on content dashboard link on Smart reporting tab ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
        clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(contentDashboardLink,contentName))));
        Actions action = new Actions(getDriver());
        action.doubleClick(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(contentDashboardLink,contentName)))).perform();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementVisible(reportOutputLink);
    }

    public void enterDateAndclickOnGo() {
        waitForSmartReportingSpinnersToDisappear();

        String fromDateToEnter, toDateToEnter = "";
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -4);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fromDateToEnter = dateFormat.format(c.getTime());

        log.info("fromDateToEnter: "+fromDateToEnter);

        c.add(Calendar.MONTH, 5);
        toDateToEnter = dateFormat.format(c.getTime());

        log.info("toDateToEnter: "+toDateToEnter);
        commonWaits.waitForElementClickable(calender_icon).click();
        commonWaits.waitForElementClickable(datePicker);

        commonWaits.waitForElementClickable(btn_fromDate).click();
        btn_fromDate.sendKeys(Keys.HOME);
        btn_fromDate.sendKeys(fromDateToEnter + " 00:00:00");
        btn_fromDate.sendKeys(Keys.TAB);

        commonWaits.waitForElementClickable(btn_toDate).click();
        btn_toDate.sendKeys(Keys.HOME);
        btn_toDate.sendKeys(toDateToEnter + " 00:00:00");
        btn_toDate.sendKeys(Keys.TAB);

        commonWaits.waitForElementClickable(btn_dateApply).click();

        commonWaits.waitForElementClickable(btn_go).click();
        checkIfLongReportingDialogPresentIfYesCloseEdit();
        JavascriptExecutor js = (JavascriptExecutor) getDriver();

        commonWaits.waitForElementVisible(reportUp);
        js.executeScript("arguments[0].scrollIntoView(true);", reportUp);
    }

    public void checkIfLongReportingDialogPresentIfYesCloseEdit(){
        log.info("Checking if Long running report dialog present");
        List<WebElement> elementList = getDriver().findElements(By.xpath(longRunningReportDialog));
        if(!elementList.isEmpty()){
            log.info("-- Long Running report dialog is present");
            clickWithoutInterception(SubmitButton_OnLongRunningReport);
        }
        else{
            log.info("Long Running Report dialog is not present");
        }
    }

    public void openFirstIncidentAndVerifyDetails() {
        checkIfLongReportingDialogPresentIfYesCloseEdit();
        String expectedIncidentID = clickOnFirstCaseID_OnIncidentDetailsReportTab();
        log.info("expected Case id: " + expectedIncidentID);
        log.info("******* Checking if incident is opened or not.");
        clickOnViewFullIncident();
        List<String> browserTabs = new ArrayList<>(getDriver().getWindowHandles());
        WebDriver currentDriver = getDriver().switchTo().window(browserTabs.get(2));
        setDriver(currentDriver);
        String actualIncidentID = fetchTicketID();
        log.info("actual Case id: " + actualIncidentID);
        Assert.assertEquals(actualIncidentID, expectedIncidentID,
                "Incident ID from details tab is not matched with incident details page.");
    }

    public String fetchTicketID() {
        switchToFrame(By.xpath(ticketIframe));
        commonWaits.waitForElementVisible(ticketID);
        return ticketID.getText();
    }

    public String clickOnFirstCaseID_OnIncidentDetailsReportTab(){
        commonWaits.waitForElementPresent(availableIncidents);
        List<WebElement> CaseIDList = getDriver().findElements(By.xpath(availableIncidents));
        String caseID = null;
        if(!CaseIDList.isEmpty()) {
            for (WebElement webElement : CaseIDList) {
                if (webElement.getText() != null) {
                    log.info("CaseID list value: " + webElement.getText());
                    caseID = webElement.getText().trim();
                    webElement.click();
                    List<String> browserTabs = new ArrayList<>(getDriver().getWindowHandles());
                    log.info("----------------browser tabs size" + browserTabs.size());
                    if (browserTabs.size() == 1) {
                        Assert.fail("FAIL--Case Details tab did not open");
                    }
                    WebDriver currentDriver = getDriver().switchTo().window(browserTabs.get(2));
                    setDriver(currentDriver);
                    break;
                }
            }
        }else{
            log.error("CaseID list is empty in Smart reporting");
            throw new AssertionError("CaseID list is empty in Smart reporting");
        }
        return caseID;
    }

    public void clickOnViewFullIncident() {
        WebElement viewFullIncident = null;
        try {
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
            switchToFrame(findElement(By.xpath(iframe)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
            String ViewFull_Incident = String.format(View_Full_Incident, ViewFullIncident);
            viewFullIncident = commonWaits.waitForByElementToBeVisible((By.xpath(ViewFull_Incident)));
            isElementPresent(By.xpath(ViewFull_Incident));
            commonWaits.waitForElementClickable(viewFullIncident, 10);
            viewFullIncident.click();
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
        } catch (Exception e) {
            commonWaits.clickByJS(viewFullIncident);
        }
    }

    public void verifyCaseDetailsInNewTab(){
        checkIfLongReportingDialogPresentIfYesCloseEdit();
        try {
            String expectedCaseID = clickOnFirstCaseID_OnIncidentDetailsReportTab();
            log.info("expected Case id: " + expectedCaseID);
            commonWaits.waitForPageFullyLoaded(60, 10);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
            log.info("******* Checking if incident is opened or not.");
            boolean isIncidentPresent = commonWaits.waitForElementVisible_withFluentWait(getDriver(), incidentOpenedInNewTab, 120);
            Assert.assertTrue(isIncidentPresent, "Incident is not opened on new tab.");
        }
        catch (Exception e) {
            Assert.fail("Reporting not configured properly. Check the reporting configuration." +  e.getMessage());
        }
    }

    public void verifyCrossLaunch() {
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs.get(0));
        getDriver().switchTo().window(tabs.get(1));
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
    }

    public void openReport(String reportName) {
        commonWaits.waitForElementVisible(txtBox_searchBar).sendKeys(reportName);
        commonWaits.waitForElementVisible(searchBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
        WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(reportItemContainer, reportName)));
        commonWaits.doubleClickOnElementUsingJS(webElement);

        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
        commonWaits.waitForElementVisible(reportOutputLink, 10);
    }

    public void openFirstRequest(String companyName) {
        commonWaits.javaScriptScrollIntoView(filterGoButton);
        commonWaits.clickWithoutInterception(filterGoButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.SMART_REPORTING);
        commonWaits.javaScriptScrollIntoView(reportOutputLink);

        WebElement element = commonWaits.waitForElementPresenceLocated(By.xpath(String.format(requestIDAsPerCompanyName, companyName)));
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), element, 10);
        commonWaits.switchToTabByTitle(SolQAConstants.BUSINESS_WORKFLOWS);
        commonWaits.waitForAngularRequestsToFinish();
    }

}