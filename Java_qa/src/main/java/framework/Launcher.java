package framework;

/* *********************************************************************
 Purpose:            This is driver script which reads config file gets driver handle and pass it
 on to automation engine
 File Name:          Launcher.Java
 Suite Name:         Framework File
 Module Name: NA
 @author                    akarhadk
 @version            1.0
 @since              2025-04-19
 Comments:
 ********************************************************************** */

import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

import util.CommonMethods;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;

public class Launcher extends Driver {

    static Logger log = Logger.getLogger(Launcher.class.getName());
    public static CommonWaits commonWaits;
    static Wait<WebDriver> wait;
    // Replace static WebDriver with ThreadLocal for multi-threading support
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    Driver webDriverManager;

    public Launcher() {
        webDriverManager = new Driver();
        configProp = ReadPropertiesFile.readProperties(System.getProperty(USERDIR) + File.separator + "properties.config");
    }

    /**
     * Registers {@code se:name} for Selenium Grid before subclass {@code @BeforeMethod} creates the driver.
     * The {@link Method} argument is the upcoming {@code @Test} method (TestNG contract).
     */
    @BeforeMethod(alwaysRun = true)
    public void registerGridSessionTestName(Method upcomingTestMethod) {
        setSeSessionNameFromTestMethod(upcomingTestMethod);
    }

    public String BWFFeatureDescriptionURL = "/helix/index.html#/com.bmc.arsys.rx.innovationstudio/record/edit-data/Feature%20Descriptor";
    // Remove static from WebElements to make them thread-safe
    @FindBy(xpath = "//input[@id= 'user_login']")
    public WebElement userName;
    @FindBy(xpath = "//input[@id= 'login_user_password']")
    public WebElement password;
    @FindBy(xpath = "//button[@id= 'login-jsp-btn']")
    public WebElement btnLogin;
    @FindBy(xpath = "//input[@id= 'user_login']")
    public WebElement uNameBWF;
    @FindBy(xpath = "//input[@id= 'login_user_password']")
    public WebElement passwordBWF;
    @FindBy(xpath = "//button[@id= 'login-jsp-btn']")
    public WebElement btnLoginBWF;
    @FindBy(xpath = "//input[@id='user_login']")
    WebElement uNameSMartIT;
    @FindBy(xpath = "//input[@id='login_user_password']")
    WebElement pwdSmartIT;
    @FindBy(xpath = "//button[@type='submit']")
    WebElement loginSmartIT;
    @FindBy(xpath = "//input[@id='user_login']")
    WebElement userNameMonitor;
    @FindBy(xpath = "//input[@id='login_user_password']")
    WebElement pwdMonitor;
    @FindBy(xpath = "//button[@id='login-jsp-btn']")
    WebElement btnLoginMonitor;
    @FindBy(xpath = "//span[@class='fi navbar-menu-icon fi-navigation-horizontal']")
    WebElement menuBar;
    @FindBy(xpath = "//input[@placeholder='User Name']")
    public WebElement uNameINTS;
    @FindBy(xpath = "//input[@placeholder='Password']")
    public WebElement passwordINTS;
    @FindBy(xpath = "//button[@type='submit']")
    public WebElement btnLoginINTS;
    @FindBy(xpath = "//input[@id='okta-signin-username']")
    public WebElement uNameOkta;
    @FindBy(xpath = "//input[@id='okta-signin-password']")
    public WebElement passwordOkta;
    @FindBy(xpath = "//input[@id='okta-signin-submit']")
    public WebElement btnLoginOkta;
    @FindBy(xpath = "//input[@id='loginUserName']")
    public WebElement rssoUsername;
    @FindBy(xpath = "//input[@id='loginPass']")
    public WebElement rssoPwd;
    @FindBy(xpath = "//button[@id='login-btn']")
    public WebElement rssologinBtn;
    @FindBy(xpath = "//input[@id='login-input']")
    public WebElement BCMUsername;
    @FindBy(xpath = "//input[@id='password-input']")
    public WebElement BCMPwd;
    @FindBy(xpath = "//button[@id='login-button']")
    public WebElement BMCLoginBtn;
    @FindBy(xpath = "//form[contains(@class,'form')]")
    public WebElement BCMLoginForm;
    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    public WebElement logoutMenu;
    @FindBy(xpath = "//button[contains(text(),'Sign out')]")
    public WebElement signOut;
    @FindBy(xpath = "//input[@id='username']")
    public WebElement emailUserName;
    @FindBy(xpath = "//input[@id='password']")
    public WebElement emailPassword;
    @FindBy(xpath = "//div[@class='signinbutton']")
    public WebElement emailLoginBtn;

     public WebDriver launchWeb(String appName, String app) {

        WebDriver driver = webDriverManager.getWebDriver();
        PageFactory.initElements(driver, this);
        setDriver(driver);
        commonWaits = new CommonWaits();
        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500)) // Reduced from 5s to 500ms polling
                .ignoring(NoSuchElementException.class);
        String loginURL = getHomeURL(appName, app);
        if (loginURL == null || loginURL.isEmpty()) {
            throw new IllegalArgumentException("Invalid URL: URL is null or empty");
        }
        log.debug(loginURL);
        if (app.contains("MONITOR")) {
            driver.get(loginURL);
            driver.findElement(By.cssSelector("Body")).sendKeys(Keys.CONTROL + "\t");
        }

        else if (app.contains("SSPLIVECHAT")) {
            try {
            driver.get(loginURL);
            }
            catch (Exception e) {
            if (!commonWaits.isElementPresent(this.uNameBWF)) {
                String vchatURL = loginURL.replace("-vc", "-vchat");
                log.debug("Login page not found with -vc URL. Navigating to: " + vchatURL);
                driver.get(vchatURL);
            }
        }
        }

        else {
            driver.get(loginURL);
			driver.navigate().refresh();
        }
        // Removed implicit wait - mixing implicit + explicit waits causes unpredictable slowdowns in Selenium
        
        return driver;
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void setDriver(WebDriver webDriver) {
        tlDriver.set(webDriver);
    }

    // This method will generate base URL for respective products
    public static String getHomeURL(String applicationName, String app) {
        String hURL = "";
        switch (app) {
            case "BWF":
            case "HPM":
            case "InnovationStudio":
                hURL = getBaseURL(app) + "/helix/index.html#/" + applicationName;
                return hURL;

            case "SMARTIT":
                hURL = getSmartITURL();
                log.debug("Retrieved URL for SmartIT: " + hURL);
                return hURL;

            case "MONITOR":
                hURL = getBaseURL(app) + "#/monitoring/events";
                return hURL;

            case "INTS":
                hURL = getIntegrationServiceURL();
                return hURL;

            case "RSSO":
                hURL = getHelixPortalURL();
                return hURL;

            case "ADEOkta":
                hURL = getADEOktaIntegrationURL();
                return hURL;

            case "DWPC":
                hURL = getDWPCURL();
                return hURL;

            case "MCSM":
                hURL = getMCSMURL();
                return hURL;

            case "CMDB":
                hURL = getCMDBURL();
                return hURL;

            case "DWP":
                hURL = getDWPURL();
                return hURL;

            case "DISCOVERY":
                hURL = getBaseURL(app);
                return hURL;

            case "BCM":
                hURL = getBCMURL();
                return hURL;

            case "ITOM":
                hURL = getADEPortalURL();
                return hURL;

            case "SSPLIVECHAT":
                hURL = getSSPLiveChatURL();
                return hURL;

            case "ITSM":
                hURL = getITSMURL();
                return hURL;

            case "HelixGPT":
                hURL = getBaseURL(SolQAConstants.appIS) + "/com.bmc.dsm.chatbot/index.html#/chatbot/window/" + CommonMethods.getHelixGPTBotIDFromtxtFile();
                log.debug("HelixGPT URL: " + hURL);
                return hURL;

            case "EMAIL":
                hURL = "https://192.0.2.15/";
                return hURL;

            case "RSSOApp":
                hURL = getRSSOAppURL();
                return hURL;

			case "DWPAdmin":
				hURL = getDWPAdminURL();
				return hURL;

            default:
                Assert.fail("Home URL is not defined for app: " + app);
                return hURL;
        }
    }

    public static String getSSPLiveChatURL() {
        return configProp.get("SSP_LIVECHAT_URL");
    }

    public static String getITSMURL() {
        return configProp.get("ITSM_URL");
    }

    private static String getADEPortalURL() {
        return configProp.get("ADE_PLATFORM_URL");
    }

    public static String getSmartITURL() {
        return configProp.get("SmartIT_URL");
    }

    public static String getIntegrationServiceURL() {
        return configProp.get("IntegrationService_Console");
    }

    public static String getBCMURL() {
        return configProp.get("BCM_URL");
    }

    public static String getADEOktaIntegrationURL() {
        return configProp.get("ADE_PLATFORM_OKTA_INTEGRATION_URL");
    }

    public static String getHelixPortalURL() {
        return configProp.get("rsso_host_url");
    }

    public static String getDWPCURL() {
        return configProp.get("DWPCatalog_URL");
    }

    public static String getMCSMURL() {
        return configProp.get("mcsmURL");
    }

    public static String getCMDBURL() {
        return configProp.get("CMDB_URL");
    }

    public static String getDWPURL() {
        return configProp.get("DWP_URL");
    }

    public static String getRSSOAppURL() {
        return configProp.get("RSSO_URL");
    }

	public static String getDWPAdminURL() {
		return configProp.get("DWP_Admin_Console");
	}

	public void logout() {
		commonWaits.waitForElementVisible(logoutMenu);
		logoutMenu.click();
		commonWaits.waitForElementVisible(signOut);
		signOut.click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
	}
    
    public static String getBaseURL(String app) {
        String sURL = null;

        switch (app.toUpperCase()) {
            case "INNOVATIONSTUDIO":
            case "BWF":
            case "HPM":
                sURL = configProp.get("IS_URL");
                break;

            case "SMARTIT":
                sURL = configProp.get("SmartIT_URL");
                break;

            case "MONITOR":
                sURL = configProp.get("protocolMonitor") + "://" + configProp.get("nameMonitor");
                break;

            case "DISCOVERY":
                sURL = configProp.get("DISCOVERY_URL");
                break;

            default:
                Assert.fail("Base URL is not defined for app: " + app);
                break;
        }
        return sURL;
    }

    //This method will do log in for new addition on respective products, as below
    public static void login(String uName, String pwd, String app) {
        // Get the current thread's Launcher instance to access thread-safe WebElements
        Launcher launcher = new Launcher();
        WebDriver driver = getDriver();
        if (driver != null) {
            PageFactory.initElements(driver, launcher);
        }

        System.out.println("Thread " + Thread.currentThread().getId() + " - Logging in with user: " + uName + " for app: " + app + " - URL: " + getDriver().getCurrentUrl());

        switch (app) {
            case "BWF":
            case "HPM":
            case "CSM":
            case "DWP":
            case "DWPC":
            case "ITSM":
            case "DWPAdmin":
            case "HelixGPT":
            case "DISCOVERY":
            case "SSPLIVECHAT":
            case "InnovationStudio":
                commonWaits.waitForElementVisible(launcher.uNameBWF);
                commonWaits.keyClearAndType(launcher.uNameBWF, uName);
                commonWaits.waitForElementVisible(launcher.passwordBWF);
                commonWaits.keyClearAndType(launcher.passwordBWF, pwd);
                commonWaits.waitForElementVisible(launcher.btnLoginBWF).click();
                break;

            case "SMARTIT":
                commonWaits.keyClearAndType(launcher.uNameSMartIT, uName);
                commonWaits.keyClearAndType(launcher.pwdSmartIT, pwd);
                commonWaits.waitForElementVisible(launcher.loginSmartIT).click();
                break;

            case "MONITOR":
                launcher.userNameMonitor.sendKeys(uName);
                launcher.pwdMonitor.sendKeys(pwd);
                launcher.btnLoginMonitor.click();
                commonWaits.waitForElementVisible(launcher.menuBar);
                break;

            case "INTS":
                commonWaits.waitForElementClickable(launcher.uNameINTS);
                launcher.uNameINTS.sendKeys(uName);
                commonWaits.waitForElementClickable(launcher.passwordINTS);
                launcher.passwordINTS.sendKeys(pwd);
                commonWaits.waitForElementVisible(launcher.btnLoginINTS);
                launcher.btnLoginINTS.click();
                break;

            case "RSSO":
                commonWaits.waitForAngularRequestsToFinish();
                commonWaits.waitForElementVisible(launcher.rssoUsername);
                launcher.rssoUsername.sendKeys(uName);
                commonWaits.waitForAngularRequestsToFinish();
                commonWaits.waitForElementVisible(launcher.rssoPwd);
                launcher.rssoPwd.sendKeys(pwd);
                commonWaits.waitForAngularRequestsToFinish();
                commonWaits.waitForElementVisible(launcher.rssologinBtn);
                launcher.rssologinBtn.submit();
                commonWaits.waitForAngularRequestsToFinish();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                break;

            case "ADEOkta":
                commonWaits.waitForElementClickable(launcher.uNameOkta);
                launcher.uNameOkta.sendKeys(uName);
                commonWaits.waitForElementClickable(launcher.passwordOkta);
                launcher.passwordOkta.sendKeys(pwd);
                commonWaits.waitForElementVisible(launcher.btnLoginOkta);
                launcher.btnLoginOkta.click();
                break;

            case "BCM":
                log.debug("Performing BCM login with username: " + uName);

                // Wait for login form to be visible
                commonWaits.waitForElementVisible(launcher.BCMLoginForm);

                // Enter username
                commonWaits.waitForElementClickable(launcher.BCMUsername);
                launcher.BCMUsername.clear();
                launcher.BCMUsername.sendKeys(uName);
                log.debug("Entered BCM username: " + uName);

                // Enter password
                commonWaits.waitForElementClickable(launcher.BCMPwd);
                launcher.BCMPwd.clear();
                launcher.BCMPwd.sendKeys(pwd);
                log.debug("Entered BCM password");

                // Click login button
                commonWaits.waitForElementClickable(launcher.BMCLoginBtn);
                launcher.BMCLoginBtn.click();
                log.debug("Clicked BCM login button");
                break;

            case "ITOM":
                commonWaits.waitForElementClickable(launcher.userName);
                launcher.userName.clear();
                launcher.userName.sendKeys(uName);

                commonWaits.waitForElementClickable(launcher.password);
                launcher.password.clear();
                launcher.password.sendKeys(pwd);

                commonWaits.waitForElementVisible(launcher.btnLogin);
                launcher.btnLogin.click();
                break;

            case "EMAIL":
                commonWaits.waitForElementClickable(launcher.emailUserName);
                launcher.emailUserName.clear();
                launcher.emailUserName.sendKeys(uName);

                commonWaits.waitForElementClickable(launcher.emailPassword);
                launcher.emailPassword.clear();
                launcher.emailPassword.sendKeys(pwd);

                commonWaits.waitForElementVisible(launcher.emailLoginBtn);
                launcher.emailLoginBtn.click();
                break;

            default:
                Assert.fail("Login method does not exist for app: " + app);
                break;
        }
        // App-aware synchronization: readyState + spinner + angular
        commonWaits.waitForPageLoadingComplete(app, 30);
        System.out.println("Thread " + Thread.currentThread().getId() + " - Login completed for user: " + uName);
    }

    @AfterClass
    public void closeBrowser() {
        if (tlDriver.get() != null) {
            tlDriver.get().quit();
            tlDriver.remove(); // Clean up ThreadLocal to prevent memory leaks
        }
    }

    public String uniquify(String name) {
        String uniqueName = name + "_" + System.currentTimeMillis();
        log.debug("Unique Name: " + uniqueName);
        return uniqueName;
    }

    public void switchToTabByTitle(String title) {
        Set<String> allWindowHandles = getDriver().getWindowHandles();
        WebDriver currentDriver;
        for (String handle : allWindowHandles) {
            currentDriver = getDriver().switchTo().window(handle);
            if (getDriver().getTitle().contains(title)) {
                log.debug("Switched to tab with title: " + title);
                setDriver(currentDriver);
                return;
            }
        }
        log.warn("No tab found with title: " + title);
    }

    public void switchToLastWindow() {
        commonWaits.switchToLastWindow();
    }

    /**
     * Closes child window if multiple tabs/windows are open and switches back to main tab (index 0)
     */
    public void closeChildWindowAndSwitchToMain() {
        if (getDriver().getWindowHandles().size() > 1) {
            getDriver().close();
            commonWaits.switchToTabByIndex(0);
            log.debug("Closed child window and switched back to main tab");
        }
    }

    public void safelyQuitDriver(WebDriver driver, String name) {
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
        } catch (WebDriverException e) {
            log.error(name + " driver already closed or unavailable: " + e.getMessage());
        }
    }


}
