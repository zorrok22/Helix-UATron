package pages.ITOM.Intelligent_Automation;
import java.awt.datatransfer.*;
import java.io.File;

import framework.BasePage;
import org.openqa.selenium.Keys;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import pages.ITOM.Api.HMEvents;
import util.ReadPropertiesFile;
import org.json.JSONObject;
import java.time.*;
import java.time.format.DateTimeFormatter;
import framework.Launcher;
import util.CommonWaits;
import util.SolQAConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IAChangeBlackoutConfiguration extends BasePage {
    WebDriver driver;
    CommonWaits commonWaits=new CommonWaits();
    private static final String base_url =  configProp.get("ADE_PLATFORM_URL");
    private static String psrIntegrationPage = base_url + "/intelligent-automation/#/integrations/psr-integrations";

    @FindBy(xpath = "//p[contains(.,'Blackout Policy')]")
    WebElement CHANGE_Enable_Caption;

    @FindBy(xpath = "//p[contains(.,'Blackout Policy')]/../../../../..//input[@type='checkbox']")
    WebElement CHANGE_Enable_BUTTON;

    @FindBy(xpath = "//p[contains(.,'Blackout Policy')]/../../../../..//button[@btn-type='warning']")
    WebElement CHANGE_Delete_BUTTON;

    @FindBy(xpath = "//button[contains(.,'Yes')]")
    WebElement CONFIRM_YES_BUTTON;

    @FindBy(xpath = "//p[contains(.,'Blackout Policy')]/../../../../..//button[@btn-type='primary']")
    WebElement CHANGE_Modify_BUTTON;

    @FindBy(xpath = "//span[contains(.,'Configure')]/../..//button[@id='connection-button']")
    WebElement CHANGE_Config_BUTTON;

    @FindBy(xpath = "//b[contains(.,'configurations')]/../../../../button")
    WebElement CHANGE_Blackout_Policy_Config_BUTTON;

    @FindBy(xpath = "//div[contains(text(),'blackoutExistingEvents')]/../../..//button[contains(@aria-label,'Remove')]")
    WebElement CHANGE_remove_blackoutExist_BUTTON;

    @FindBy(xpath = "//div[contains(text(),'blackoutExistingEvents')]/../../..//input")
    WebElement CHANGE_input_blackoutExist_BUTTON;

    @FindBy(xpath = "//div[contains(text(),'resetStatusOnBlackoutEnd')]/../../..//button[contains(@aria-label,'Remove')]")
    WebElement CHANGE_remove_resetblackout_BUTTON;

    @FindBy(xpath = "//div[contains(text(),'resetStatusOnBlackoutEnd')]/../../..//input")
    WebElement CHANGE_input_resetblackout_BUTTON;

    @FindBy(xpath = "//button[@id='save-connector-button']")
    WebElement CHANGE_save_config_BUTTON;

    @FindBy(xpath = "//button[@id='save-integration-button']")
    WebElement CHANGE_enable_blackout_BUTTON;

    @FindBy(xpath = "//fieldset//button[contains(.,'Cancel')]")
    WebElement AddCustomCancelButton;

    public IAChangeBlackoutConfiguration(){
        PageFactory.initElements(getDriver(), this);
        driver = getDriver();
        log("Initialized IA Change Blackout page elements");
        log("Configured explicit wait");
    }
    public boolean getChangeStatus(){
        log("Opening PSR integration page to fetch status");
        driver.get(psrIntegrationPage);
        commonWaits.waitForPageLoad(driver);
        log("Page load completed");
        commonWaits.waitForElementVisible_withFluentWait(driver, CHANGE_Enable_BUTTON, 60);
        log("Enable button detected");
        commonWaits.waitForElementVisible(CHANGE_Enable_Caption,20);
        log("Enable caption visible");
        boolean statusEnabled = !CHANGE_Enable_BUTTON.getAttribute("aria-checked").equals("false");
        log("Current change status: " + statusEnabled);
        return statusEnabled;
    }

    public void deleteChangeConfig(){
        log("Initiating change configuration delete");
        driver.get(psrIntegrationPage);
        commonWaits.waitForPageLoad(driver);
        log("Page load completed");
        commonWaits.waitForElementVisible(CHANGE_Enable_Caption,20);
        log("Enable caption visible");
        commonWaits.click(CHANGE_Delete_BUTTON);
        log("Clicked delete button");
        commonWaits.waitForElementVisible_withFluentWait(driver, CONFIRM_YES_BUTTON, 60);
        commonWaits.click(CONFIRM_YES_BUTTON);
        log("Confirmed deletion");
        commonWaits.waitForSometime(3000);
        log("Delete wait period elapsed");
    }

    public void configureChangeBlackout(){
        log("Starting change blackout configuration");
        driver.get(psrIntegrationPage);
        log("PSR integration page opened");
        commonWaits.waitForPageLoad(driver);
        log("Page load completed");
        driver.navigate().refresh();
        commonWaits.pause();
        commonWaits.waitForPageLoad(driver);
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_Modify_BUTTON, 15);
        commonWaits.click(CHANGE_Modify_BUTTON);

        log("Clicked modify button");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_Config_BUTTON, 15);
        commonWaits.pureSafeClick(CHANGE_Config_BUTTON);

        log("Clicked config button");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_Blackout_Policy_Config_BUTTON, 10);
        commonWaits.pause();
        commonWaits.pureSafeClick(CHANGE_Blackout_Policy_Config_BUTTON);

        log("Opened blackout policy configuration");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_remove_blackoutExist_BUTTON, 5);
        commonWaits.pause();
        commonWaits.pureSafeClick(CHANGE_remove_blackoutExist_BUTTON);

        log("Removed blackoutExistingEvents pill");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_input_blackoutExist_BUTTON, 5);
        commonWaits.pause();
        commonWaits.typeKeys(CHANGE_input_blackoutExist_BUTTON,"false");
        commonWaits.pause();
        log("Entered blackoutExistingEvents value");
        CHANGE_input_blackoutExist_BUTTON.sendKeys(Keys.ENTER);
        log("Confirmed blackoutExistingEvents value");
        clickCancelButton();
        commonWaits.pureSafeClick(CHANGE_remove_resetblackout_BUTTON);

        log("Removed resetStatusOnBlackoutEnd pill");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_input_resetblackout_BUTTON, 5);
        commonWaits.pause();
        commonWaits.typeKeys(CHANGE_input_resetblackout_BUTTON,"true");
        log("Entered resetStatusOnBlackoutEnd value");
        CHANGE_input_resetblackout_BUTTON.sendKeys(Keys.ENTER);
        log("Confirmed resetStatusOnBlackoutEnd value");
        clickCancelButton();
        commonWaits.scrollToSpecificElement(CHANGE_save_config_BUTTON);
        log("Scrolled to save config button");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_save_config_BUTTON, 5);
        commonWaits.pause();
        commonWaits.pureSafeClick(CHANGE_save_config_BUTTON);

        log("Clicked save config button");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_enable_blackout_BUTTON, 5);
        commonWaits.pause();
        commonWaits.pureSafeClick(CHANGE_enable_blackout_BUTTON);

        log("Initial save wait completed");
        commonWaits.pause();
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_Enable_Caption, 20);
        log("Enable button visible after configuration save");
    }

    public void disablechangeConfig(){
        log("Disabling change configuration");
        driver.get(psrIntegrationPage);
        log("PSR integration page opened");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_Enable_Caption, 15);
        log("Page load completed");
        CHANGE_Enable_BUTTON.click();
        log("Clicked enable/disable toggle");
        commonWaits.waitForSometime(10000);
        log("Disable wait complete");
        commonWaits.waitForElementVisible_withFluentWait(driver,CHANGE_Enable_Caption, 15);
    }

    private void log(String message){
        System.out.println("[IAChangeBlackoutConfiguration] " + message);
    }

    private void clickCancelButton(){
        try{
            commonWaits.pause();
            commonWaits.waitForElementVisible_withFluentWait(driver,AddCustomCancelButton, 1);
            commonWaits.pureSafeClick(AddCustomCancelButton);
            log("Clicked cancel button");
        } catch (Exception e) {
            log("Failed to click cancel button: " + e.getMessage());
        }
    }

}
