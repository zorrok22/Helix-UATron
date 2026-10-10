package ITOM.Ade_Platform;

import framework.Launcher;
import org.testng.annotations.BeforeMethod;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import util.*;

import java.io.File;
import java.time.Duration;
import java.util.Map;

public class Ade_Platform extends Launcher {

    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") +
            File.separator + "properties.config");
    static SolqaLogger log = new SolqaLogger(Ade_Platform.class.getName());
    WebDriver driver = null;
    AdePortalLandingPage adePortalLandingPagePage;
    Wait<WebDriver> wait;
    String app = "ITOM";
    CommonWaits commonWaits;
    ReadJson readJson;
    Map<Object, String> portalTestData;

    @BeforeMethod
    public void setup() {
        DOMConfigurator.configure("log4j_Config.xml");
        log.info("*********** beforeClass of ADE_Platform ********************");
        driver = getWebDriver();
        driver.manage().deleteAllCookies();
        driver = this.launchWeb(app, app);
        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);
        adePortalLandingPagePage = new AdePortalLandingPage(driver);
        commonWaits = new CommonWaits();
        readJson = new ReadJson();
    }

    //Previous owner: vshelke
    //mArking this test as disabled as this already covered in Sanity tests
    @Test(description = "[TMS-IMS] Launch each applications from Helix Portal landing console with ADE-RSSO Local user",enabled = false, groups = {"Regression", "PMDemo", "PreProd", "Prod"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12342", Products = {Product.ADE_Platform})
    public void DRSQA_12342_CrossLaunchEachApplicationsFromADEPortalForUpgrade(ITestContext context) {

        log.info("------- START: 12342 Launch each applications from Helix Portal landing console with ADE-RSSO Local user ------------");
      
        log.info("-------Step1:- Login to the Helix Portal using Admin credentials---------");
        context.setAttribute("12342_WebDriver", driver);
        context.setAttribute("12342_Product", Product.Cloud_Security);

        portalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+File.separator+"Portal", "TestData", "DRSQA_12342_Data");

        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        adePortalLandingPagePage.verifyLoginToADEPlatform();

        log.info("-------Step1:- Try cross Launching Capacity Optimize Application from ADE Helix Portal---------");
        adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName2"));

        log.info("-------Step3:- Try cross Launching Helix Dashboard Application from ADE Helix Portal---------");
        adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName4"));

        log.info("-------Step4:- Try cross Launching Log Analytics Application from ADE Helix Portal---------");
        adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName5"));

        log.info("-------Step5:- Try cross Launching Operation Management Application from ADE Helix Portal---------");
        adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName6"));

        // log.info("-------Step8 and Step9:- Try cross Launching Vulnerability Management Application from ADE Helix Portal---------");
        // adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName7"));

        log.info("-------Step6:- Try cross Launching ITSM Application from ADE Helix Portal if enabled---------");
        adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName8"));

        log.info("-------Step7:- Try cross Launching BWF Application from ADE Helix Portal if enabled---------");
        adePortalLandingPagePage.navigationAndVerifyApplicationLaunchedSuccessfully(portalTestData.get("ApplicationName9"));
            
        log.info("------- END: 12342 Launch each applications from Helix Portal landing console with ADE-RSSO Local user ------------");
        
    }

    @AfterMethod(alwaysRun = true)
    public void doPostConditions() {
        if (driver != null) {
            driver.quit();
        }

    }

}
