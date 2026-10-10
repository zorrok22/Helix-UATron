package pages.ITOM.Intelligent_Automation;

import framework.BasePage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.CommonWaits;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import util.ReadPropertiesFile;
import java.io.File;
import java.time.Duration;
import java.util.Map;

public class SNOWLaunchPage extends BasePage {

    WebDriver driver= null;

    static Logger log = Logger.getLogger(SNOWLaunchPage.class.getName());

    CommonWaits commonWaits;

    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    public SNOWLaunchPage() {
        this.driver = getWebDriver();
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public void setSNOWDriver(WebDriver driver){
        this.driver=driver;
    }

    public WebDriver getSNOWDriver(){
        return this.driver;
    }


    public void openSNOWPage(){
        driver.get(config.get("SNOW_URL"));
    }

    public void openListView(){
        String ListViewSuffixURL="/now/nav/ui/classic/params/target/incident_list.do";
        driver.get(config.get("SNOW_URL")+ListViewSuffixURL);
    }

    public void quitSNOWPage(){
        driver.quit();
    }


    @FindBy(xpath = "//form[@id='loginPage']//input[@id='user_name']")
    public WebElement txtBox_usename;

    @FindBy(xpath = "//input[@type='password'][contains(@id,'user_password')]")
    public WebElement txtBox_password;

    @FindBy(xpath = "//input[@id='username']")
    public WebElement txtBox_SNOWEmailID;

    @FindBy(xpath = "//button[@type='submit']")
    public WebElement username_submit_button;

    @FindBy(xpath = "//input[@id='password']")
    public WebElement txtBox_SNOWpassword;

    @FindBy(xpath = "//button[@type='submit']")
    public WebElement password_submit_button;

    @FindBy(xpath = "//button[contains(@id,'sysverb_login')]")
    public WebElement btn_login;

    @FindBy(xpath = "//input[@aria-label='Search']")
    public WebElement searchTextOnIncidentListPage;

    @FindBy(xpath="//span[@id='incident_hide_search']//select")
    WebElement selectDropdownOnIncidentListPage;

    @FindBy(xpath="//span[@id='sn_customerservice_case_hide_search']//select")
    WebElement selectDropdownOnCaseListPage;

    @FindBy(xpath = "//a[contains(@class,'linked')]")
    public WebElement linkedIncidentID;

    @FindBy(xpath = "//textarea[@id='incident.description']")
    public WebElement incidentDescription;

    @FindBy(xpath = "//input[@id='incident.short_description']")
    WebElement incidentShortDescription;

    @FindBy(xpath = "//textarea[@id='sn_customerservice_case.description']")
    public WebElement caseDescription;

    @FindBy(xpath = "//input[@id='sn_customerservice_case.short_description']")
    WebElement caseShortDescription;

    @FindBy(xpath = "//select[@id='incident.state']/option[@selected='SELECTED']")
    public WebElement incidentStatus;

    @FindBy(xpath = "//input[@id='sys_display.incident.business_service']")
    public WebElement incidentService;

    @FindBy(xpath = "//input[@id='sys_display.incident.cmdb_ci']")
    public WebElement incidentCI;

    @FindBy(xpath = "//input[@placeholder='Filter navigator']")
    public WebElement Filter_Locator;

    String wakeUpStatusJS="return document.querySelector(\"body > dps-app\").shadowRoot.querySelector(\"div > main > dps-home-auth-quebec\").shadowRoot.querySelector(\"div > section:nth-child(1) > div > dps-page-header > div:nth-child(2) > div > p > span\")";
    String signButtonJavaScript="return document.querySelector(\"body > dps-app\").shadowRoot.querySelector(\"div > header > dps-navigation-header\").shadowRoot.querySelector(\"header > div > div.dps-navigation-header-utility > ul > li:nth-child(2) > dps-login\").shadowRoot.querySelector(\"div > dps-button\").shadowRoot.querySelector(\"button\")";

    public void loginToSNOW(String username, String password) {
        log.info("*********** login To Service Now application ******************");
        commonWaits.waitForElementVisible(txtBox_usename).sendKeys(username);
        commonWaits.waitForElementVisible(txtBox_password).sendKeys(password);
        commonWaits.waitForElementClickable(btn_login).click();
        commonWaits.waitForPageReadyState(driver);
    }

    public void loginAndWakeUpSNOWInstance(String emailID, String password) {
        log.info("*********** login To Service Now application with SNOW Account ******************");
        commonWaits.waitForSometime(commonWaits.moreDelay);
        boolean flag=false;
        try{
            if(txtBox_usename.isDisplayed()){
                flag=true;
            }
        }
        catch (Exception ignored){
            ;
        }

        if(flag){
            log.info("Snow is not in hypernate mode. user can login to snow application");
        }else {
            log.info("Snow instance is in hybernate mode, we need wake up the instance");
            JavascriptExecutor jse = (JavascriptExecutor)driver;
            WebElement signInButton = (WebElement) jse.executeScript(signButtonJavaScript);
            log.info("clicking on Sign In Button");
            commonWaits.waitForElementVisible(signInButton).click();

            log.info("Enter SNOW Email ID");
            commonWaits.waitForElementVisible(txtBox_SNOWEmailID).sendKeys(emailID);
            commonWaits.waitForElementClickable(username_submit_button).click();

            log.info("Enter password");
            commonWaits.waitForElementVisible(txtBox_SNOWpassword).sendKeys(password);
            commonWaits.waitForElementClickable(password_submit_button).click();
            commonWaits.waitForPageReadyState(driver);
            commonWaits.waitForSometime(commonWaits.minDelay);
            log.info("Waking up SNOW instance after login");
            wakeUpSnowInstance();
        }
        log.info("*************** Login Successful **********************");
    }

    public void wakeUpSnowInstance(){
        commonWaits.waitForSometime(commonWaits.moreDelay);
        for(int i=0;i<7;i++) {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            WebElement ele = (WebElement) jse.executeScript(wakeUpStatusJS);
            try{
                String text = ele.getText().trim();
                if (text.contains("Waking")) {
                    log.info("Waiting for instance to wake up at "+ i + " minutes");
                    commonWaits.waitForSometime(60000);
                }else{
                    break;
                }
            }
            catch(Exception e)
            {
                log.error("**** Got this error: "+e.getMessage());
            }
            }
        }


    public void searchIncidentThroughMessage(String msg){
        commonWaits.waitForSometime(commonWaits.moreDelay);

        JavascriptExecutor jse = (JavascriptExecutor) driver;

        WebElement frame = (WebElement) jse.executeScript("return document.querySelector(\"body > macroponent-f51912f4c700201072b211d4d8c26010\").shadowRoot.querySelector(\"#gsft_main\")");
        driver.switchTo().frame(frame);

        Select object =new Select(selectDropdownOnIncidentListPage);
        object.selectByVisibleText("for text");

        commonWaits.waitForElementVisible(searchTextOnIncidentListPage);
        searchTextOnIncidentListPage.sendKeys(msg+Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);

    }

    public void openIncidentAndVerify(String Desc, String status, String CI, String serviceName){
        String resolved="Resolved";
        log.info("Open incident and verify");
        commonWaits.waitForElementVisible(linkedIncidentID);
        linkedIncidentID.click();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        if (Desc != ""){
            String Description = getIncidentDescription().replace("\n", "").replace("\r", "");
            log.info("SNOW Incident description: "+Description);
            Assert.assertTrue(Description.contains(Desc),"SNOW Incident description doesn't match with expected. Actual: "+Description);
        }

        if (status != ""){
            String snowStatus = getIncidentStatus();
            log.info("SNOW Incident status seen is : "+snowStatus);
            Assert.assertTrue(snowStatus.contains(status),"SNOW Incident Status doesn't match with expected. Actual status seen: "+snowStatus);
        }

        if (CI != ""){
            String snowCI = getIncidentCI();
            log.info("SNOW Incident status seen is : "+snowCI);
            Assert.assertTrue(snowCI.contains(CI),"SNOW Incident CI doesn't match with expected. Actual CI seen: "+snowCI);
        }

        if (serviceName != ""){
            String snowService = getIncidentService();
            log.info("SNOW Incident status seen is : "+snowService);
            Assert.assertTrue(snowService.contains(serviceName),"SNOW Incident service doesn't match with expected. Actual Service found: "+snowService);
        }



    }

    public String getIncidentDescription(){
        commonWaits.waitForElementVisible(incidentDescription);
        String description = incidentDescription.getText().trim();
        log.info("description is - "+ description);
        return description;
    }

    public String getIncidentStatus(){
        commonWaits.waitForElementVisible(incidentStatus);
        String staus = incidentStatus.getText().trim();
        log.info("Incident status is - "+ staus);
        return staus;
    }

    public String getIncidentService(){
        commonWaits.waitForElementVisible(incidentService);
        String service = incidentService.getAttribute("value").trim();
        log.info("Service is - "+ service);
        return service;
    }

    public String getIncidentCI(){
        commonWaits.waitForElementVisible(incidentCI);
        String ci = incidentCI.getAttribute("value").trim();
        log.info("CI is - "+ ci);
        return ci;
    }
    public String getIncidentShortDescription(){
        commonWaits.waitForElementVisible(incidentShortDescription);
        String description = incidentShortDescription.getAttribute("value").trim();
        log.info("Short description is - "+ description);
        return description;
    }

    /**
     * Search for a case in ServiceNow using message text
     * @param msg The message text to search for
     */
    public SNOWLaunchPage searchCaseThroughMessage(String msg){
        log.info("Searching for case through message: " + msg);
        commonWaits.waitForSometime(commonWaits.moreDelay);

        try {
            // Try shadow DOM approach first (for newer ServiceNow versions)
            JavascriptExecutor jse = (JavascriptExecutor) getSNOWDriver();
            WebElement frame = (WebElement) jse.executeScript(
                "return document.querySelector(\"body > macroponent-f51912f4c700201072b211d4d8c26010\").shadowRoot.querySelector(\"#gsft_main\")"
            );
            getSNOWDriver().switchTo().frame(frame);
            log.info("Switched to frame using shadow DOM approach");
        } catch (Exception e) {
            log.warn("Shadow DOM approach failed, trying direct frame switch: " + e.getMessage());
            try {
                // Fallback: Direct frame switch by ID
                WebDriverWait wait = new WebDriverWait(getSNOWDriver(), Duration.ofSeconds(10));
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("gsft_main"));
                log.info("Switched to frame using direct ID approach");
            } catch (Exception ex) {
                log.error("Failed to switch to gsft_main frame: " + ex.getMessage());
                throw new RuntimeException("Cannot switch to ServiceNow main frame", ex);
            }

        }

        //Select value from dropdown to search
        Select object = new Select(selectDropdownOnCaseListPage);
        object.selectByVisibleText("for text");

        commonWaits.waitForElementVisible(searchTextOnIncidentListPage);
        searchTextOnIncidentListPage.sendKeys(msg + Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        log.info("Successfully searched for case with message: " + msg);
        return this;
    }

    /**
     * Open a case and verify its details
     * @param message The expected message in the case description
     * @param shortDesc The expected short description
     */
    public SNOWLaunchPage openCaseAndVerify(String shortDesc){
        log.info("Open Case and verify");
        commonWaits.waitForElementVisible(linkedIncidentID);
        linkedIncidentID.click();
        commonWaits.waitForSometime(commonWaits.moreDelay);

        String shortDescription = getCaseShortDescription();
        Assert.assertTrue(shortDescription.contains(shortDesc), "Case short description doesn't match with expected. Expected: " + shortDesc + ", Actual: " + shortDescription);
        return this;
    }


    /**
     * Get the case short description from ServiceNow
     * @return The case short description text
     */
    public String getCaseShortDescription(){
        commonWaits.waitForElementVisible(caseShortDescription);
        String description = caseShortDescription.getAttribute("value").trim();
        log.info("Case short description is - " + description);
        return description;
    }

    /**
     * Verify login to ServiceNow was successful
     */
    public SNOWLaunchPage verifyLoginToSNOW(){
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForSometime(commonWaits.minDelay);
        log.info("Verified successful login to ServiceNow");
        return this;
    }

}
