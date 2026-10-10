package pages.ITOM.Ade_Portal_Landing;

import framework.Launcher;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.GrafanaDashboard.ReportingPage;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Intelligent_Integrations.SWPIntelligentIntegration;
import pages.ITOM.Platform.PlatformLandingPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.CommonWaits;
import util.Product;
import util.SolQAConstants;
import util.SolqaLogger;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Portal_FeatureMethods extends Launcher {

    static SolqaLogger log = new SolqaLogger(Portal_FeatureMethods.class.getName());
    CommonWaits commonWaits;
    WebDriver driver;
    PlatformLandingPage platformLandingPage;
    IntelligentAutomationPage intelligentAutomation;
    SWPIntelligentIntegration swpIntelligentIntegration;
    Events_API eventsAPI;
    BhomPage bhom;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    UsersManagementPage usersManagementPage;
    AdePortalLandingPage adeportallandingpage;

    public Portal_FeatureMethods(ITestContext context, String jiraID) {
        DOMConfigurator.configure("log4j_Config.xml");
        driver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(driver);
        platformLandingPage = new PlatformLandingPage(driver);
        adeportallandingpage = new AdePortalLandingPage(driver);
        commonWaits = new CommonWaits();
        eventsAPI = new Events_API();
        intelligentAutomation = new IntelligentAutomationPage();
        bhom = new BhomPage();
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        swpIntelligentIntegration = new SWPIntelligentIntegration(driver);
        context.setAttribute(jiraID + "_WebDriver", driver);
        context.setAttribute(jiraID + "_Product", Product.ADE_Platform);
    }

    public Portal_FeatureMethods() {
        platformLandingPage = new PlatformLandingPage(getDriver());
        commonWaits = new CommonWaits();
        eventsAPI = new Events_API();
        intelligentAutomation = new IntelligentAutomationPage();
        bhom = new BhomPage();
        smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        swpIntelligentIntegration = new SWPIntelligentIntegration(getDriver());
    }

    public Portal_FeatureMethods loginToADEPlatform(String Username, String Password) {
        login(Username, Password, SolQAConstants.appITOM);
        platformLandingPage.verifyPlatformLandingPageOpened();
        return this;
    }

    public void verifyLogin() {
        platformLandingPage.verifyPlatformLandingPageOpened();
    }

    public void crossLaunchProduct(String productName) {
        platformLandingPage.crossLaunchProduct(productName);
        log.info("Cross launched product: " + productName);
    }

    public void enablePSR(String itsmEndpointURL, String username, String password) {
        intelligentAutomation.enablePSR(itsmEndpointURL, username, password);
        log.info("Proactive Service Resolution (PSR) enabled successfully.");
    }

    public void enablePSRWithINRFlag(String itsmEndpointURL, String username, String password, boolean isINRFlagON) {
        intelligentAutomation.enablePSRWithINRFlag(itsmEndpointURL, username, password,isINRFlagON);
        log.info("PSR mapping configured successfully.");
    }

    public void removePSRMapping() {
        intelligentAutomation.removePSRMapping();
    }

    public Portal_FeatureMethods createNewKeyWithRoles(String keyName) {
        platformLandingPage.createNewKey(keyName);
        platformLandingPage.closeKeyDetails();
        platformLandingPage.navigateToHomePage();
        return this;
    }

    public void injectEventTicketForBHOM(String jsonFilePath, Map<Object, String> TestData, String msg) throws IOException {
        eventsAPI.injectEventTicketForBHOM(jsonFilePath, TestData, msg);
        log.info("Event ticket injected for BHOM using the provided JSON file.");
    }

    public String injectEventTicketUsingJasonContent(String content) throws IOException {
        String eventId = eventsAPI.injectEventTicketUsingJsonContent(content);
        log.info("Event ticket injected for BHOM using the provided JSON Content.");
        return eventId;
    }

    public void navigateToPortalTab() {
        commonWaits.switchToTabByTitle(SolQAConstants.BMC_HELIX_PORTAL);
        log.info("Switched to Portal tab successfully.");
    }

    public void globalSearchAndOpenTicket(String ticketID) {
        smartITFeatureMethodsPage.navigateToTicketConsole();
        smartITFeatureMethodsPage.globalSearchAndOpenTicket(ticketID);
    }

    public void modifyEventForPriority(String priority) {
        commonWaits.switchToTabByTitle(SolQAConstants.BHOM);
        bhom.modifyEventForPriority(priority);
    }

    public void closeEvent() {
        commonWaits.switchToTabByTitle(SolQAConstants.BHOM);
        bhom.closeEvent();
        log.info("Event closed successfully.");
    }

    public void closeDriver() {
        if (driver != null) {
            driver.quit();
            log.info("WebDriver closed successfully.");
        }
    }

    public void verifyIntelligentIntegrationPageLaunchedSuccessfully() {
        swpIntelligentIntegration.verifyIntelligentIntegrationPageLaunchedSuccessfully();
    }

    public void addAndConfigureConnector(Map<Object, String> connectorData) {
        swpIntelligentIntegration.addAndConfigureConnector(connectorData);
    }

    public void searchForStream(String streamName) {
        swpIntelligentIntegration.navigateToStreamsPage();
        swpIntelligentIntegration.searchStream(streamName);
    }

    public void startStream() {
        swpIntelligentIntegration.startStream();
    }

    public void navigateToDevices() {
        bhom.navigateToDevices();
    }

    public void searchDevice(String deviceName) {
        bhom.searchAndOpenDevice(deviceName);
    }

    public void removeConnector(String connectorName) {
        commonWaits.switchToTabByTitle(SolQAConstants.II);
        swpIntelligentIntegration.navigateToConnectorsPage();
        swpIntelligentIntegration.removeConnector(connectorName);
    }

    public void navigateToMonitorsTab() {
        bhom.navigateToMonitorsTab();
    }

    public void selectMonitor(String monitorName) {
        bhom.selectMonitor(monitorName);
        boolean status = bhom.isDataGraphAvailable();
    }

    public void verifyDataGraphIsAvailable() {
        Assert.assertTrue(bhom.isDataGraphAvailable(), "Data graph is not available.");
        log.info("Data graph is available.");
    }

    public void navigateToTab(String tabTitle) {
        commonWaits.switchToTabByTitle(tabTitle);
        log.info("Switched to " + tabTitle + " tab successfully.");
    }

    public void logoutFromDashboardAndSwitchBackToTab(String tabName){
        ReportingPage reportingPage = new ReportingPage();
        reportingPage.logoutFromDashboard();
        navigateToTab(tabName);
        log.info("Helix Portal's AuthProxy has a 3-minute cache, it does not validate token on every request. Wait for 3 minute for logout to work in parent tab. Logged out from Dashboard and switched back to " + tabName + " tab.");
        // Helix Portal's AuthProxy has a 3-minute cache, it does not validate token on every request.
        commonWaits.waitForSometime(60000*3); // Wait for 3 minutes to ensure logout is processed
        getDriver().navigate().refresh();
        Assert.assertTrue(commonWaits.isElementPresent(userName), "Application is not logout automatically");
    }

    public Portal_FeatureMethods captureScreenshot(String ssName, String app) {
        try {
            commonWaits.captureScreenshot(ssName, app);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    
    /**
     * Method to select user group for a user
     * @param groupName - Name of the user group to select (e.g., "Operators")
     */
    public Portal_FeatureMethods selectUserGroup(String groupName) {
        log.info("--- UserManagementFeatureMethods: Selecting user group - " + groupName);
        usersManagementPage.selectUserGroup(groupName);
        log.info("--- UserManagementFeatureMethods: User group selection completed - " + groupName);
		return this;
    }
    
    /**
     * Method to select user role for a user
     * @param roleName - Name of the user role to select
     */
    public Portal_FeatureMethods selectUserRole(String roleName) {
        log.info("--- UserManagementFeatureMethods: Selecting user role - " + roleName);
        usersManagementPage.selectUserRole(roleName);
        log.info("--- UserManagementFeatureMethods: User role selection completed - " + roleName);
		return this;
    }

    public Portal_FeatureMethods clickOnApplicationSwitcher() {
        platformLandingPage.clickOnApplicationSwitcher();
        return this;
    }

    public Portal_FeatureMethods verifyApplicationSwitcherDropdownIsDisplayed(Map<Object, String> expectedOptions) {
        platformLandingPage.verifyApplicationSwitcherDropdownIsDisplayed(expectedOptions);
        return this;
    }

    public Portal_FeatureMethods clickOnApplicationSwitcherOption(String optionName) {
        platformLandingPage.clickOnApplicationSwitcherOption(optionName);
        return this;
    }

    public Portal_FeatureMethods navigationAndVerifyApplicationLaunchedSuccessfully(String applicationName) {
        platformLandingPage.navigationAndVerifyApplicationLaunchedSuccessfully(applicationName);
        return this;
    }

    public Portal_FeatureMethods navigateToPage(String menu, String subMenu) {
        platformLandingPage.navigateToPage(menu, subMenu);
        return this;
    }

    public Portal_FeatureMethods createNewKey(String keyName){
        platformLandingPage.createNewKey(keyName);
        return this;
    }

    public Portal_FeatureMethods selectAllGroupsAndRoles() {
        platformLandingPage.selectAllGroups();
        commonWaits.waitForExecutionComplete();
        platformLandingPage.selectAllRoles();
        commonWaits.waitForExecutionComplete();
        platformLandingPage.closeKeyDetails();
        platformLandingPage.navigateToHomePage();
        return this;
    }

    public Portal_FeatureMethods navigateToProfile() {
        platformLandingPage.navigateToProfile();
        return this;
    }

    public Portal_FeatureMethods verifyCopyKeyDetails() throws UnsupportedFlavorException, IOException {
        platformLandingPage.editMyAccessKeys();
        platformLandingPage.verifyCopyKeyDetails();
        return this;
    }

    public Portal_FeatureMethods verifyGenerateNewSecretKey() {
        platformLandingPage.verifyGenerateNewSecretKey();
        return this;
    }

    public Portal_FeatureMethods verifyLoginToADEPlatform() {
        adeportallandingpage.verifyLoginToADEPlatform();
        return this;
    }

    public Portal_FeatureMethods verifyAgentOutcomesTileIsVisible() {
        adeportallandingpage.verifyAgentOutcomesTileIsVisible();
        return this;
    }

    public Portal_FeatureMethods clickOnAgentOutcomesTile() {
        adeportallandingpage.clickOnAgentOutcomesTile();
        return this;
    }

    public Portal_FeatureMethods validateLaunchedURLUsesISBasedServiceURL(String expectedURL) {
        adeportallandingpage.validateLaunchedURLUsesISBasedServiceURL(expectedURL);
        return this;
    }

    public Portal_FeatureMethods confirmAgentOutcomesLandingPageLoadsSuccessfully() {
        adeportallandingpage.confirmAgentOutcomesLandingPageLoadsSuccessfully();
        return this;
    }

    public Portal_FeatureMethods confirmAgentOutcomesLandingPageNotLoadsSuccessfully() {
        adeportallandingpage.confirmAgentOutcomesLandingPageNotLoadsSuccessfully();
        return this;
    }

    public Portal_FeatureMethods clickOnBWFTile() {
        adeportallandingpage.clickOnBWFTile();
        return this;
    }

    public Portal_FeatureMethods clickOnApplicationLauncherButton() {
        adeportallandingpage.clickOnApplicationLauncherButton();
        return this;
    }

    public Portal_FeatureMethods launchApplicationFromISAppLauncher(String applicationName) {
        adeportallandingpage.launchApplicationFromISAppLauncher(applicationName);
        return this;
    }

}