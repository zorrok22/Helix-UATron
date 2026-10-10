package pages.ITOM.Ade_Portal_Landing;

import framework.BasePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Automation_Console.AutomationConsolePage;
import pages.ITOM.Dashboard.DashboardPage;
import pages.ITOM.Developer_Tool.DeveloperToolPage;
import pages.ITOM.Intelligent_Integrations.SWPIntelligentIntegration;
import pages.ITOM.Log_Analytics.LogAnalytics;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.Discovery.DiscoveryPage;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITSM.InnovationSuite.InnovationSuite;
import pages.ITSM.SmartIT.SmartITDashboardPage;
import pages.ITSM.bwf.BWFDashboard;
import pages.ITSM.dwp.DWPDashboard;
import util.CommonWaits;
import util.SolQAConstants;

import java.util.ArrayList;
import java.util.List;

public class AdePortalLandingPage extends BasePage {
    static Logger log = Logger.getLogger(AdePortalLandingPage.class.getName());
    @FindBy(xpath = "//adapt-dialog//app-switch-org-modal//button[contains(.,'Switch Organization')]")
    public WebElement switchOrgButton;

    @FindBy(xpath = "(//div[@class='app-title'])")
    List<WebElement> tiles;

    @FindBy(xpath = "//*[@class='dashboard-title' and normalize-space()='Agent Outcomes']")
    public WebElement agentOutcomesHeading;

    @FindBy(xpath = "//button[@aria-label='Application launcher']")
    public WebElement applicationLauncherButton;

    @FindBy(xpath = "//div[contains(@class,'text-container')]//p[contains(normalize-space(),'This application has no home page')]")
    public WebElement noHomePageToastMessage;

    public String switchOrgPopUpDialog = "//adapt-dialog//app-switch-org-modal";
    public String verifyLogin = "//header//a[@href='#/landing' and contains(normalize-space(.),'Home')]";
    public String getProduct = "//div[contains(text(),'%s')]//parent::div/parent::div[contains(@class,'top')]";
    public String getAppNameLocator = "//cloud-adapt-navigation//adapt-menu[@id='adapt-menu-0']//span[1]";
    public String capacityOptimizationNameLocator = "//shell-root//span//span[contains(@class,'product__name')]";
    public String itsmPage = "//a[@href='#/create/smart-recorder']";
    public String ticketConsoleItsmPage = "//h3[text()='Ticket Console']";
    public String cmdbConsole = "//ul/li/a[@href='#/dashboard']";
    String BHOMTileName = "BMC Helix Operations Management";
    String AIOPSTileName = "BMC Helix Service Monitoring";
    String logAnalyticTileName = "BMC Helix Log Analytics";
    String intelligentIntegrationTileName = "BMC Helix Intelligent Integrations";
    String intelligentAutomationTileName = "BMC Helix Intelligent Automation";
    String discoveryTileName = "BMC Helix Discovery";
    String developerToolTileName = "BMC Helix Developer Tools";
    String dashboardTileName = "BMC Helix Dashboards";
    String TelemetryTileName = "ADE Helix Telemetry";
    String automationConsoleTileName = "BMC Helix Automation Console";
    String dwpTile = "BMC Helix Digital Workplace";
    String dwpcTile = "BMC Helix Digital Workplace Catalog";
    String bwfTile = "BMC Helix Business Workflows";
    String isTile = "BMC Helix Innovation Suite";
    String smartITTile = "BMC Helix ITSM";
    String bmcHelixPageInnovationStudio = "//header//span/span[text()='Innovation Studio']";
    String LaunchApplicationLocator = "//button[contains(@data-testid,'navigation-action-subitem') and normalize-space()='%s']";

    WebDriver driver;
    CommonWaits commonWaits;
    DWPDashboard dwpDashboard;

    public AdePortalLandingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        commonWaits = new CommonWaits();
    }

    public void verifyLoginToADEPlatform() {
        boolean login_value = commonWaits.waitForElementClickable(verifyLogin, 100);
        Assert.assertTrue(login_value, "Login to ADE platform is not successful");

    }

    public int getTotalLaunchTiles() {
        log.info("-----Get total launch tiles from portal page------");
        return tiles.size();
    }

    public String getApplicationTileTextFromIndex(int i) {
        log.info("-----Get application tiles name from portal page------");
        String appText = "(//div[@class='app-title'])[%s]";
        appText = String.format(appText, i);
        WebElement element = findElement(By.xpath(appText));
        return element.getText().trim();
    }

    public void crossLaunchProduct(String productName) {
        log.info("-----cross launch product: " + productName);
        commonWaits.waitForElementClickable(String.format(getProduct, productName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, productName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(productName);

    }

    public BhomPage navigateToBHOM() {
        log.info("-----cross launch product: " + BHOMTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, BHOMTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, BHOMTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(BHOMTileName);
        return new BhomPage();
    }

    public AIOpsPage navigateToAIOPS() {
        log.info("-----cross launch product: " + AIOPSTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, AIOPSTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, AIOPSTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        switchToProductPage(AIOPSTileName);
        return new AIOpsPage();
    }

    public AIOpsPage switchToSmartIt() {
        log.info("-----Navigate to SmartIT");
        switchToProductPage(smartITTile);
        return new AIOpsPage();
    }

    public LogAnalytics navigateToLogAnalytics() {
        log.info("-----cross launch product: " + logAnalyticTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, logAnalyticTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, logAnalyticTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(logAnalyticTileName);
        return new LogAnalytics(driver);
    }

    public SWPIntelligentIntegration navigateToIntelligentIntegration() {
        log.info("-----cross launch product: " + intelligentIntegrationTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, intelligentIntegrationTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, intelligentIntegrationTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(intelligentIntegrationTileName);
        return new SWPIntelligentIntegration(driver);
    }

    public IntelligentAutomationPage navigateToIntelligentAutomation() {
        log.info("-----cross launch product: " + intelligentAutomationTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, intelligentAutomationTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, intelligentAutomationTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(intelligentAutomationTileName);
        return new IntelligentAutomationPage();
    }

    public DiscoveryPage navigateToDiscoveryPage() {
        log.info("-----cross launch product: " + discoveryTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, discoveryTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, discoveryTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(discoveryTileName);
        return new DiscoveryPage();
    }

    public DeveloperToolPage navigateToDeveloperTool() {
        log.info("-----cross launch product: " + developerToolTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, developerToolTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, developerToolTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(developerToolTileName);
        return new DeveloperToolPage(driver);
    }

    public DashboardPage navigateToDashboardPage() {
        log.info("-----cross launch product: " + dashboardTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, dashboardTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, dashboardTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(dashboardTileName);
        return new DashboardPage(driver);
    }

    public DashboardPage navigateToTelemetryPage() {
        log.info("-----cross launch product: " + TelemetryTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, TelemetryTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, TelemetryTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(TelemetryTileName);
        return new DashboardPage(driver);
    }

    public AutomationConsolePage navigateToAutomationConsolePage() {
        log.info("-----cross launch product: " + automationConsoleTileName);
        commonWaits.waitForElementClickable(String.format(getProduct, automationConsoleTileName), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, automationConsoleTileName)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(automationConsoleTileName);
        return new AutomationConsolePage(driver);
    }

    public DWPDashboard navigateToDWPPage() {
        log.info("-----cross launch product: " + dwpTile);
        commonWaits.waitForElementClickable(String.format(getProduct, dwpTile), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, dwpTile)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(dwpTile);
        return new DWPDashboard();
    }

    public DWPDashboard navigateToDWPCatalogPage() {
        log.info("-----cross launch product: " + dwpcTile);
        commonWaits.waitForElementClickable(String.format(getProduct, dwpcTile), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, dwpcTile)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(dwpcTile);
        return new DWPDashboard();
    }

    public DWPDashboard navigateToBWFPage() {
        log.info("-----cross launch product: " + bwfTile);
        commonWaits.waitForElementClickable(String.format(getProduct, bwfTile), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, bwfTile)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(bwfTile);
        return new DWPDashboard();
    }

    public InnovationSuite navigateToInnovationSuite() {
        log.info("-----cross launch product: " + isTile);
        commonWaits.waitForElementClickable(String.format(getProduct, isTile), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, isTile)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(isTile);
        return new InnovationSuite();
    }

    public SmartITDashboardPage navigateToSmartIT() {
        log.info("-----cross launch product: " + smartITTile);
        commonWaits.waitForElementClickable(String.format(getProduct, smartITTile), 50);
        WebElement ele = findElement(By.xpath(String.format(getProduct, smartITTile)));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        switchToProductPage(smartITTile);
        return new SmartITDashboardPage(driver);
    }

    public void switchToProductPage(String productName) {
        log.info("-----verify cross launch for product: " + productName);
        List<String> browserTabs = new ArrayList<String>(getDriver().getWindowHandles());
        log.info(browserTabs.size()+"---------------------");
        if (browserTabs.size() == 1) {
            Assert.fail("------- FAIL --> Crosslaunch did not happen for the product: " + productName);
        }
        getDriver().switchTo().window(browserTabs.get(1));
        clickOnSwitchOrganizationPopUp();
    }

    public void clickOnSwitchOrganizationPopUp() {
        try {
            log.info("Checking for switch organization pop up message");
            commonWaits.waitForElementClickable(switchOrgPopUpDialog, 20);
            commonWaits.waitForElementClickable(switchOrgButton);
            switchOrgButton.click();
            log.info("Clicked switch organization button present on the pop up message");

        } catch (Exception e) {
            log.info("--Switch organization button is NOT present");
        }
    }

    public void goBackToHomePage() {
        driver.close();
        List<String> browserTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(0));
    }

    public void navigationAndVerifyApplicationLaunchedSuccessfully(String applicationName) {
        String appName = null;
        switch (applicationName) {

            case "BMC Helix Automation Console":
                AutomationConsolePage automationConsolePage = new AutomationConsolePage(driver);
                navigateToAutomationConsolePage();
                automationConsolePage.verifyAutomationConsolePageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Business Workflows":
                BWFDashboard bwfDashboard = new BWFDashboard();
                navigateToBWFPage();
                bwfDashboard.verifyBWFPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "ADE Helix Telemetry":
                DashboardPage telemetryPage = new DashboardPage(driver);
                navigateToTelemetryPage();
                telemetryPage.verifyTelemetryPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Cloud Cost":
                crossLaunchProduct(applicationName);
                commonWaits.waitForElementClickable(getAppNameLocator, 50);
                appName = findElement(By.xpath(getAppNameLocator)).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertTrue(applicationName.contains(appName), "Cloud cost application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix Capacity Optimization":
                crossLaunchProduct(applicationName);
                commonWaits.waitForElementClickable(capacityOptimizationNameLocator, 50);
                appName = commonWaits.waitForByElementToBeVisible(By.xpath(capacityOptimizationNameLocator)).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertTrue(applicationName.contains(appName), "Capacity Optimization application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix Cloud Security":
                crossLaunchProduct(applicationName);
                commonWaits.waitForElementClickable(getAppNameLocator, 50);
                appName = findElement(By.xpath(getAppNameLocator)).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertTrue(applicationName.contains(appName), "Cloud Security application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix Continuous Optimization":
                crossLaunchProduct(applicationName);
                appName = commonWaits.waitForByElementToBeVisible(By.xpath(capacityOptimizationNameLocator)).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertEquals(appName, "Continuous Optimization", "Continuous Optimization application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix Dashboards":
                DashboardPage dashboardPage = new DashboardPage(driver);
                navigateToDashboardPage();
                dashboardPage.verifyDashboardPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Developer Tools":
                DeveloperToolPage developerToolPage = new DeveloperToolPage(driver);
                navigateToDeveloperTool();
                developerToolPage.verifyDeveloperToolPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Digital Workplace":
                dwpDashboard = new DWPDashboard();
                navigateToDWPPage();
                dwpDashboard.verifyDWPPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Digital Workplace Catalog":
                dwpDashboard = new DWPDashboard();
                navigateToDWPCatalogPage();
                dwpDashboard.verifyDWPCPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Discovery":
                DiscoveryPage discoveryPage = new DiscoveryPage();
                navigateToDiscoveryPage();
                discoveryPage.verifyDiscoveryPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Innovation Suite":
                InnovationSuite innovationSuite = new InnovationSuite();
                navigateToInnovationSuite();
                innovationSuite.verifyInnovationSuitePageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Intelligent Automation":
                IntelligentAutomationPage intelligentAutomation = new IntelligentAutomationPage();
                navigateToIntelligentAutomation();
                intelligentAutomation.verifyIntelligentAutomationPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Intelligent Integrations":
                SWPIntelligentIntegration swpIntelligentIntegration = new SWPIntelligentIntegration(driver);
                navigateToIntelligentIntegration();
                swpIntelligentIntegration.verifyIntelligentIntegrationPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix ITSM":
                SmartITDashboardPage smartITDashboardPage = new SmartITDashboardPage(driver);
                navigateToSmartIT();
                smartITDashboardPage.verifySmartITPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Log Analytics":
                LogAnalytics logAnalytics = new LogAnalytics(driver);
                navigateToLogAnalytics();
                logAnalytics.verifyLogAnalyticsPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Operations Management":
                BhomPage bhomPage = new BhomPage();
                navigateToBHOM();
                bhomPage.verifyBHOMLaunchedSuccessfully(applicationName);
                goBackToHomePage();
                break;

            case "BMC Helix Service Monitoring":
                AIOpsPage aiOpsPage = new AIOpsPage();
                navigateToAIOPS();
                aiOpsPage.verifyAIOPSPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Vulnerability Management":
                crossLaunchProduct(applicationName);
                commonWaits.waitForElementClickable(getAppNameLocator, 50);
                appName = findElement(By.xpath(getAppNameLocator)).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertEquals(appName, "Automation Console", "Vulnerability Management application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix for CSP":
                crossLaunchProduct(applicationName);
                appName = findElement(By.xpath(bmcHelixPageInnovationStudio)).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertEquals(appName, "Innovation Studio", "CSP application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix CMDB":
                crossLaunchProduct(applicationName);
                boolean iscmdbPresent = commonWaits.waitForElementClickable(cmdbConsole, 80);
                Assert.assertTrue(iscmdbPresent, "CMDB application is not launched successfully");
                goBackToHomePage();
                break;

            default:
                break;
        }
    }

    public String adaptSubMenuItems = "//div[contains(@class,'adapt-submenu-item')]//span[contains(text(),'%s')]/parent::button";
    @FindBy(xpath="//adapt-menu//span[contains(text(),'User access')]/parent::button")
    public WebElement dashboard_dropdown;


    public String clickOnDropdownAndSelectOption(String OptionName){
        log.info("------ Clicking on Common Services dropdown and select option with name -"+ OptionName);
        commonWaits.waitForElementClickable(dashboard_dropdown).click();
        String submenu = String.format(adaptSubMenuItems, OptionName);
        commonWaits.waitForElementVisible(findElement(By.xpath(submenu))).click();
        commonWaits.waitForPageReadyState(driver);
        String URL = driver.getCurrentUrl();
        return URL;
    }

    public void verifyAgentOutcomesTileIsVisible() {
        log.info("----- Verify Agent Outcomes tile is visible in Helix Portal");
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(getProduct, "Agent Outcomes")));
        commonWaits.waitForElementClickable(ele);
        Assert.assertTrue(ele.isDisplayed(), "Agent Outcomes tile is not visible in Helix Portal");    
        log.info("Agent Outcomes tile is visible in Helix Portal");
    }

    public void clickOnAgentOutcomesTile() {
        log.info("----- Click on Agent Outcomes tile in Helix Portal");
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(getProduct, "Agent Outcomes")));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        log.info("Agent Outcomes tile is clicked in Helix Portal");
    }

    public void validateLaunchedURLUsesISBasedServiceURL(String expectedURL) {
        log.info("----- Validate the launched URL uses the IS-based service URL (correct routing)");
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL.contains(expectedURL), true, "Launched URL does not use the IS-based service URL (correct routing)");
        log.info("Launched URL uses the IS-based service URL (correct routing)");
    }

    public void confirmAgentOutcomesLandingPageLoadsSuccessfully() {
        log.info("----- Confirm Agent Outcomes landing page loads successfully");
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertTrue(commonWaits.isElementPresent(agentOutcomesHeading,100), "Agent Outcomes landing page does not load successfully");
        log.info("Agent Outcomes landing page loads successfully");
    }

    public void confirmAgentOutcomesLandingPageNotLoadsSuccessfully() {
        log.info("----- Confirm Agent Outcomes landing page does not load successfully");
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertTrue(commonWaits.isElementPresent(noHomePageToastMessage,100), "Agent Outcomes landing page loads successfully");
        log.info("Agent Outcomes landing page does not load successfully");
    }

    public void clickOnBWFTile() {
        log.info("----- Click on BMC Helix Business Workflows tile in Helix Portal");
        commonWaits.waitForElementClickable(String.format(getProduct, "BMC Helix Business Workflows"), 50);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(getProduct, "BMC Helix Business Workflows")));
        commonWaits.waitForElementClickable(ele);
        ele.click();
        log.info("BMC Helix Business Workflows tile is clicked in Helix Portal");
    }

    public void clickOnApplicationLauncherButton() {
        log.info("----- Open IS App Launcher");
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(applicationLauncherButton, 100);
        applicationLauncherButton.click();
        log.info("Application Launcher button is clicked in Helix Portal");
    }

    public void launchApplicationFromISAppLauncher(String applicationName) {
        log.info("----- Launch " + applicationName + " from IS App Launcher");
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(LaunchApplicationLocator, applicationName)));
        commonWaits.waitForElementClickable(ele);
        commonWaits.clickWithoutInterception(ele);
        commonWaits.waitForExecutionComplete();
        log.info(applicationName + " is launched from IS App Launcher");
    }


}