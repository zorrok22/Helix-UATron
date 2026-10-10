package pages.ITOM.Aiops;

import com.fasterxml.jackson.databind.JsonNode;
import framework.BasePage;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.Discovery;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import pages.ITOM.Bhom.BhomPage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class AIOpsStandaloneMethods extends BasePage {
    public static SolqaLogger log = new SolqaLogger(AIOpsStandaloneMethods.class.getName());
    private static final CommonWaits commonwait = new CommonWaits();
    WebDriver driver;
    String aiopsServiceEndPoint = "/aiops/#/entities/service/";
    private String TargetServiceUrl = "";
    @FindBy(xpath = "//*[@id='navmenu']")
    public WebElement loganalytics_navmenu;

    @FindBy(xpath = "//div[@id='BMC Helix Dashboards']")
    public WebElement dashboards_app_xpath;
    
    @FindBy(xpath = "//span[contains(.,'Root Cause Analysis')]/../../../../..//button")
    public WebElement AnalyzeRootCausexpath;

    @FindBy(xpath = "//span[contains(.,'Standalone Events')]/../../../button[@aria-selected='false']")
    public WebElement RootCauseStandAlonexpath;

    public  AIOpsStandaloneMethods() {
        this.driver = getDriver();
        PageFactory.initElements(this.driver,this);
    }

    public boolean waitforPlatformPageLoad() {
        return commonwait.waitForElementVisible_withFluentWait(driver,dashboards_app_xpath, 40);
    }

    public void setservice_url(String serviceKey) {
        this.TargetServiceUrl = configProp.get("ADE_PLATFORM_URL") + aiopsServiceEndPoint + serviceKey + "?type=key";
    }

    public boolean navigateToServicesTab() {
        log.info("Navigate to AIOps page");
        driver.get(TargetServiceUrl);
        commonwait.waitForElementVisible_withFluentWait(driver,AnalyzeRootCausexpath, 40);
        commonwait.pause();
        commonwait.pureSafeClick(AnalyzeRootCausexpath);
        commonwait.pause();
        try{
            commonwait.waitForElementVisible(RootCauseStandAlonexpath, 15);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    

    
}
