package pages.ITOM.Itsm_Insights;


import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITSM.SmartIT.SmartITDashboardPage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ITSMInsightsPage extends BasePage {

    public static SolqaLogger log = new SolqaLogger(ITSMInsightsPage.class.getName());

    @FindBy(xpath = "//button[contains(translate(., 'j', 'J'), 'Manage Jobs')]")
    public WebElement manageJobsLink;

    @FindBy(xpath = "//span[text()='Create one-time job']/ancestor::button/following-sibling::table/tbody/tr")
    public WebElement oneTimeJobRows;

    @FindBy(xpath = "//*[text()='Create one-time job']/ancestor::button/following-sibling::table//adapt-rx-select//button")
    public WebElement oneTimeJobActionBtn;

    @FindBy(xpath = "//*[@role='listbox']//button//strong[text()='Delete']")
    public WebElement deleteJobButton;

    @FindBy(xpath = "//*[contains(@class,'modal-footer')]//button[text()='Delete']")
    public WebElement modelDialobDeleteButton;

    @FindBy(xpath = "//input[@name='jobName']")
    public WebElement jobNameInput;

    @FindBy(xpath = "//input[contains(@id,'rx-datetime-range')]")
    public WebElement dateRangeInput;

    @FindBy(xpath = "//h3[normalize-space()='Data range']")
    public WebElement calendarIcon;

    @FindBy(xpath = "//adapt-highlight[text()='Status']/ancestor::button")
    public WebElement statusCheckbox;

    @FindBy(xpath = "//*[@id='job-config-save-button' and contains(. ,'Run now')]")
    public WebElement runNowBtn;

    @FindBy(xpath = "//button[@aria-label='Refresh']")
    public WebElement jobRefreshBtn;

    @FindBy(xpath = "//span[@class='a-toast__details-text']")
    public WebElement toastError;

    @FindBy(xpath = "//span[text()='Workspaces']/parent::button")
    public WebElement workspace;

    @FindBy(xpath = "//span[text()='Proactive problem management']/parent::button")
    public WebElement ppmlink;

    @FindBy(xpath = "//button[text()[contains(., 'View cluster details')]]")
    public WebElement viewCluster;

    @FindBy(xpath = "(//*[@class='adapt-chart-group']//*[@class='treemap__actions-menu'])[1]")
    public WebElement cluster;

    @FindBy(xpath = "//button[normalize-space()='Create problem']")
    public WebElement createProblem;

    @FindBy(xpath = "//button[text()[contains(., ' Continue')]]")
    public WebElement popUpOptional;

    @FindBy(xpath = "//div[@id='304420531']//div[@id='ar1000000232_data']")
    public WebElement problemID;

    @FindBy(xpath = "//button[@id='304440891']")
    public WebElement saveProblem;

    @FindBy(xpath = "//span[contains(.,'Related items')]/parent::span/descendant::adapt-badge//span[contains(@class,'badge-primary')]")
    public WebElement incidentCount;

    @FindBy(xpath = "//smt-text-view/h2[contains(text(),'Created from cluster')]")
    public WebElement createdFromCluster;

    @FindBy(xpath = "//*[@ux-id='navigation-item_workspaces']")
    public WebElement workspace_xpath;

    @FindBy(xpath = "//*[@ux-id='navitem-element']//span[contains(.,'Proactive Problem')]")
    public WebElement ppm_xpath;

    @FindBy(xpath = "//*[contains(@id,'manage-job-create') and contains(. ,'Create one-time job')]")
    public WebElement createOneTimeJoblink;

    @FindBy(xpath = "//*[@rx-view-component-id='1d8a2263-eb9c-4829-a8eb-f6755cd6474c']//tbody/tr[1]/td[7]")
    public WebElement problemIDValue;

    String jobStatusSuccessful="//*[text()='%s']/following-sibling::td/p[contains(text(),'Successful')]";
    String jobErrorMessage="//*[text()='%s']/following-sibling::td[2]";
    String ITSMInsightIframe="//iframe[@id='pwa-frame']";

    CommonWaits commonWaits;
    IntelligentAutomationPage iaPage;
    String pattern="MMM dd, yyyy";

    SmartITDashboardPage smartITDashboardPage;
    public ITSMInsightsPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        iaPage = new IntelligentAutomationPage();
        smartITDashboardPage = new SmartITDashboardPage();
    }

    public ITSMInsightsPage navigateToManageJobs(){
        log.info("Navigate to Manage Jobs.");
        commonWaits.waitForElementClickable((manageJobsLink)).click();
        commonWaits.waitForPageReadyState(getDriver());
        return this;
    }

    public ITSMInsightsPage navigateTo_ProactiveProbManagement() {
        log.info("Navigate to PPM page.");
        commonWaits.waitForPageReadyState(getDriver());
        smartITDashboardPage.navigateToWorkSpace("Proactive Problem Management");
        commonWaits.switchToLastWindow();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForPageLoad(getDriver());
        iaPage.closeWindow(SolQAConstants.HELIX_ITSM);
        commonWaits.switchToTabByTitle(SolQAConstants.ITOM_INCIDENT_INSIGHTS);
        commonWaits.waitForPageReadyState(getDriver());
        return this;
    }

    public void createOneTimeJob(String jobName){
        log.info("Creating one time job.");
        waitForJobLinkEnabled();
        commonWaits.waitForPageReadyState(getDriver());
        if(commonWaits.isElementPresent((oneTimeJobRows))){
            log.info("Deleting existing one time job.");
            deleteOneTimeJob();
        }
        waitForJobLinkEnabled();
        commonWaits.waitForElementClickable((createOneTimeJoblink)).click();
        commonWaits.waitForPageReadyState(getDriver());
        if(commonWaits.isElementPresent(toastError)){
            String errorMsg=commonWaits.waitForElementVisible((toastError)).getText().trim();
            Assert.fail("There is an application error while clicking on Create One time job link.Error message is : "+ errorMsg);
        }
        log.info("Entering required details to create job.");
        commonWaits.waitForElementVisible(jobNameInput).clear();
        commonWaits.waitForElementVisible(jobNameInput).sendKeys(jobName);
        commonWaits.javaScriptScrollIntoView(commonWaits.waitForElementVisible((dateRangeInput)));
        commonWaits.waitForElementVisible(dateRangeInput).clear();
        commonWaits.waitForElementVisible(dateRangeInput).click();
        String startDate=getPastDateTime(14,pattern);
        String endDate = getCurrentDateTime(pattern);
        commonWaits.waitForElementVisible(dateRangeInput).sendKeys(startDate);
        commonWaits.waitForElementVisible(dateRangeInput).sendKeys(" - ");
        commonWaits.waitForElementVisible(dateRangeInput).sendKeys(endDate);
        commonWaits.clickWithoutInterception(calendarIcon);
        commonWaits.waitForElementClickable(statusCheckbox).click();
        commonWaits.waitForElementVisible(runNowBtn).click();
    }

    public void waitForJobLinkEnabled(){
        int i=0;
       while(!commonWaits.waitForElementVisible(createOneTimeJoblink).isEnabled() && i<10){
            ++i;
            //**** This Minimum wait is required for the joblink. Even fluent wait doesn't work here
            commonWaits.waitForSometime(commonWaits.minDelay);
        }
    }
    public void deleteOneTimeJob(){
        log.info("Delete one-time Job if present.");
        commonWaits.waitForElementVisible(oneTimeJobActionBtn).click();
        commonWaits.waitForElementVisible(deleteJobButton).click();
        commonWaits.waitForElementVisible(modelDialobDeleteButton).click();
        commonWaits.waitForPageReadyState(getDriver());
    }


    public String selectClusterAndRequestForProblemID(){
        log.info("Select the cluster and request for its problem id to be created.");
        String problemIDValue="";
        commonWaits.waitForElementClickable(cluster).click();
        commonWaits.waitForElementClickable(createProblem).click();
        try {
            //Click this popup, only if present.
            commonWaits.waitForElementClickable(popUpOptional).click();
        }catch (Exception ignored){}
        commonWaits.waitForTabToLoadFully(SolQAConstants.HELIX_ITSM);
        commonWaits.switchToTabByTitle(SolQAConstants.HELIX_ITSM);
        switchToFrame(ITSMInsightIframe);
        commonWaits.waitForPageReadyState(getDriver());
        try {
            problemIDValue = commonWaits.waitForElementVisible(problemID).getText();
            log.info("Problem ID generated is: " + problemIDValue);
        }catch(Exception e){
            log.error("Problem id itself is not seen generated in expected time on smartIT Problem management page.");
            Assert.fail("Problem id itself is not seen generated in expected time on smartIT Problem management page.");
        }
        commonWaits.waitForElementClickable(saveProblem).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(ITSMInsightIframe);
        if (commonWaits.isElementPresent(createdFromCluster)){
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageReadyState(getDriver());
            String incidentCountInCluster = commonWaits.waitForElementVisible(incidentCount).getText().trim();
            log.info("Incidents associated with the problem: "+incidentCountInCluster);
            Assert.assertTrue(Integer.parseInt(incidentCountInCluster)>0,"No Incidents are seen under problem id.");
        }else{
            Assert.fail("Incident tab itself is not visible in Problem page.");
        }
        commonWaits.switchToTabByTitle("ITSM Insights");
        return problemIDValue;
    }

    public void checkIfClusterIsHavingProblemCreated(){
        log.info("OPen the associated clusters. & check if problem id is associated.");
        commonWaits.waitForElementClickable(cluster).click();
        commonWaits.waitForElementClickable(viewCluster).click();
        commonWaits.waitForPageReadyState(getDriver());
        Assert.assertTrue(commonWaits.isElementPresent(problemIDValue),"Problem is not seen associated with the cluster.");
    }

    public void verifyJobStatus(String jobName){
        log.info("Verify Job execution status.");
        int counter=0;
        waitForJobLinkEnabled();
        commonWaits.waitForElementClickable(jobRefreshBtn).click();
        while(!commonWaits.isElementPresent(By.xpath(String.format(jobStatusSuccessful,jobName))) && counter < 5){
            log.info("Wait for few seconds before retrying. If Job is created.");
            ++counter;
            commonWaits.waitForElementClickable(jobRefreshBtn).click();
        }
        commonWaits.waitForElementClickable(jobRefreshBtn).click();
        if(!commonWaits.isElementPresent(By.xpath(String.format(jobStatusSuccessful,jobName)))){
            String errorMsg=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(jobErrorMessage,jobName))).getText().trim();
            Assert.fail("Job failed with Error message : " + errorMsg);
        }
        commonWaits.waitForElementClickable(workspace).click();
        commonWaits.waitForElementClickable(ppmlink).click();
        commonWaits.waitForPageReadyState(getDriver());
    }

}
