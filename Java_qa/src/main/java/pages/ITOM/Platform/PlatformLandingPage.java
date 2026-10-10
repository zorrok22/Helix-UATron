package pages.ITOM.Platform;

import framework.BasePage;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Automation_Console.AutomationConsolePage;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.Dashboard.DashboardPage;
import pages.ITOM.Developer_Tool.DeveloperToolPage;
import pages.ITOM.Discovery.DiscoveryPage;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Intelligent_Integrations.SWPIntelligentIntegration;
import pages.ITOM.Log_Analytics.LogAnalytics;
import pages.ITSM.InnovationSuite.InnovationSuite;
import pages.ITSM.SmartIT.SmartITDashboardPage;
import pages.ITSM.bwf.BWFDashboard;
import pages.ITSM.dwp.DWPDashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlatformLandingPage extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(PlatformLandingPage.class.getName());
    WebDriver driver;
    CommonWaits commonWaits;
    DWPDashboard dwpDashboard;

    public PlatformLandingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    //String locators
    String verifyLogin = "//header//a[@href='#/landing' and contains(normalize-space(.),'Home')]";
    String navigationTab = "//button[contains(@class, 'adapt-menu__link')]//span[text()='%s']";
    String menuItem = "//span[text()='%s']/ancestor::button";
    String item = "//span[text()='%s']";
    String keyInput = "//adapt-rx-control-label[contains(. ,'%s')]/following-sibling::div";
    String keyExpiry = "//*[text()='%s']/ancestor::button";
    String productTile = "//div[contains(text(),'%s')]/ancestor::div[contains(@class, 'card-test')]";
    String applicationSwitcherDropdownOption = "//button[contains(@data-testid,'navigation-action-subitem') and normalize-space(.)='%s']";
    String userGroup = "//div[contains(@class,'w-100 at-tooltip') and normalize-space()='%s']";
    String selectUserCheckBox = "//div[normalize-space()='%s']//adapt-row-selection-checkbox";
    String roleType = "//span[normalize-space()='%s']/parent::adapt-highlight";
    String selectButton="//span[normalize-space()='%s']/parent::div//a/span";
    String subExpandButton="//span[normalize-space()='%s']/ancestor::div[contains(@class,'a-tree__content')]/span";
    String subCollapseCheckbox="//span[normalize-space()='%s']/ancestor::label[contains(@class,'checkbox')]";
    String actionsButtonOption = "//button[@role='menuitem' and normalize-space()='%s']";

    //WebElements locators
    @FindBy(xpath = "//button[normalize-space()='Confirm']")
    WebElement confirmBtn;
    @FindBy(xpath = "//*[text()='Roles']/following-sibling::a/span")
    WebElement rolesBtn;
    @FindBy(xpath = "//i[@class='d-icon-cross_adapt']")
    WebElement closeButton;
    @FindBy(xpath = "//a[@href='#/landing']")
    WebElement homePageButton;

    @FindBy(xpath = "//button[@aria-label='Application switcher']")
    WebElement applicationSwitcher;

    @FindBy(xpath = "//button[contains(@data-testid,'navigation-action-subitem')]")
    List<WebElement> applicationSwitcherDropdownOptions;

    @FindBy(xpath = "(//button[contains(@data-testid,'navigation-action-subitem')])[1]")
    WebElement applicationSwitcherDropdownOptionFirst;
    
    @FindBy(xpath = "//span[contains(@class,'a-product__name text-url')]")
    WebElement ProductHeader;

    @FindBy(xpath = "(//span[text()='Access keys']/ancestor::button)[1]")
    WebElement accessKeysBtn;

    @FindBy(xpath = "(//span[text()='Add key']/ancestor::button)[1]")
    WebElement addKeyBtn;

    @FindBy(xpath = "//*[text()='Groups']/following-sibling::a/span")
    WebElement groupsBtn;
    
    @FindBy(xpath = "//th[@aria-label='Role name']//adapt-rx-checkbox")
    WebElement allRolesBtn;

    @FindBy(xpath = "//th[@aria-label='Group name']//button[contains(@class,'selection')]")
    WebElement allGroupsBtn;

    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    WebElement profileBtn;

    @FindBy(xpath = "//button[contains(@id,'adapt-profile-menu-item-0')]")
    WebElement myProfileBtn;

    @FindBy(xpath = "(//div[@class='my-keys']//button[normalize-space()='Actions'])[1]")
    WebElement actionsBtn;

    @FindBy(xpath = "//button[normalize-space()='Edit']")
    WebElement deleteBtn;

    @FindBy(xpath = "//button[normalize-space()='Yes']")
    WebElement yesBtn;

    @FindBy(xpath = "(//span[normalize-space()='Secret key has been generated.'])[1]")
    WebElement secretKeyGeneratedText;

    @FindBy(xpath = "(//button[normalize-space()='Delete key'])[1]")
    WebElement selectionDeleteKeyBtn;

    @FindBy(xpath = "(//div[contains(@class,'success')]//span[normalize-space()='Tenant ID and Key has been copied'])[1]")
    WebElement tenantIDAndKeyCopiedText;

    @FindBy(xpath = "//input[@placeholder='Search']")
    WebElement userGroupSearchInput;

    @FindBy(xpath = "//button[@rx-id='add-group-btn']")
    WebElement addGroupBtn;

    @FindBy(xpath = "//input[@name='groupName']")
    WebElement groupNameInput;

    @FindBy(xpath = "//button[@rx-id='add-group-confirm-btn']")
    WebElement confirmAddGroupBtn;

    @FindBy(xpath = "//span[@rx-id='user-select-btn']")
    WebElement selectUserButton;

    @FindBy(xpath = "(//input[@placeholder='Search'])[2]")
    WebElement selectUserSearchInput;

    @FindBy(xpath = "//button[contains(@class,'panel-confirm')]")
    WebElement confirmSelectUserBtn;

    @FindBy(xpath = "//span[normalize-space()='1 assigned']")
    WebElement oneAssignedText;

    @FindBy(xpath = "//button[@rx-id='add-role-table']")
    WebElement addRoleBtn;

    @FindBy(xpath = "//adapt-rx-textfield[@rx-id='role-name-textfield']//input")
    WebElement roleNameInput;

    @FindBy(xpath = "//button[@name=\"'type'\"]")
    WebElement roleTypeDropdown;

    @FindBy(xpath = "//button[@rx-id='role-confirm-btn']")
    WebElement confirmRoleBtn;

    @FindBy(xpath = "//button[contains(@class,'card-title border-0')]")
    WebElement expandPermissionBtn;

    @FindBy(xpath = "//button[@rx-id='permission-confirm-btn']")
    WebElement confirmPermissionBtn;

    @FindBy(xpath = "//span[normalize-space()='Groups']/parent::div//span[contains(@rx-id,'groups-count-btn')]")
    WebElement groupsCount;

    @FindBy(xpath = "(//div[contains(@class,'form-control-feedback')]/ancestor::div[@class='checkbox'])[last()]")
    WebElement selectAllCheckboxBtn;

    @FindBy(xpath = "//button[@rx-id='remove-btn']")
    WebElement removeRoleBtn;

    @FindBy(xpath = "//button[contains(@class,'action-dropdown-toggle')]")
    WebElement ActionsButton;
    @FindBy(xpath = "(//div[contains(@class,'success')]//span[normalize-space()='Access Key has been added.'])[1]")
    WebElement accessKeyAddedSuccessfullyText;


    public void verifyPlatformLandingPageOpened() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        boolean login_value = commonWaits.waitForElementVisible_withFluentWait(getDriver(), verifyLogin, 30);
        Assert.assertTrue(login_value, "Login to ADE platform is not successful");
        log.info("Platform Landing Page is opened");
    }

    public void crossLaunchProduct(String productName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        WebElement productTileElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(productTile, productName)));
        commonWaits.scrollToElement(productTileElement);
        commonWaits.clickWithoutInterception(productTileElement);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForPageReadyState();
        commonWaits.switchToTabByTitle(productName);
    }

    public void navigateToPage(String navigationTabName, String MenuItemName){
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(navigationTab, navigationTabName))).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuItem, MenuItemName))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickByText(String text){
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(item, text))).click();
    }

    public void createNewKey(String keyName) {
        navigateToPage("User access", "Users and keys");
        commonWaits.clickWithoutInterception(accessKeysBtn);
        commonWaits.clickWithoutInterception(addKeyBtn);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(keyInput+"//input", "Key name")));
        we.click();
        we.sendKeys(keyName);
        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(keyInput, "Key expiry")));
        we.click();
        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(keyExpiry, "Never expires (not recommended)")));
        if (we.getAttribute("aria-selected").equals("false")){
            we.click();
        }
        commonWaits.clickWithoutInterception(confirmBtn);
        Assert.assertTrue(commonWaits.isElementPresent(accessKeyAddedSuccessfullyText), "Access key has not been added");
        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuItem, "Copy key details")));
        commonWaits.clickWithoutInterception(we);
        Assert.assertTrue(commonWaits.isElementPresent(tenantIDAndKeyCopiedText), "Tenant ID and Key has not been copied");
    }

    public void closeKeyDetails() {
        commonWaits.clickWithoutInterception(closeButton);
    }

    public void navigateToHomePage() {
        commonWaits.clickWithoutInterception(homePageButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickOnApplicationSwitcher() {
        log.info("Clicking on the Application Switcher icon");
        commonWaits.clickByJS(applicationSwitcher);
        commonWaits.waitForExecutionComplete();
    }

    public void verifyApplicationSwitcherDropdownIsDisplayed(Map<Object, String> expectedOptions) {
        commonWaits.waitForElementVisible(applicationSwitcherDropdownOptionFirst, 10);
    
        Set<String> actualSet = applicationSwitcherDropdownOptions.stream()
                .map(e -> e.getText().trim().toLowerCase())
                .collect(Collectors.toSet());
    
        Set<String> expectedSet = expectedOptions.values().stream()
                .map(e -> e.trim().toLowerCase())
                .collect(Collectors.toSet());
    
        // Find differences
        Set<String> missing = new HashSet<>(expectedSet);
        missing.removeAll(actualSet);
    
        Set<String> extra = new HashSet<>(actualSet);
        extra.removeAll(expectedSet);
    
        // Build detailed error message
        if (!missing.isEmpty() || !extra.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder(" Application Switcher Validation Failed:");
    
            if (!missing.isEmpty()) {
                errorMessage.append(" Missing in UI:");
                missing.forEach(item -> errorMessage.append(" - ").append(item).append("\n"));
                Assert.fail("Missing in UI: " + missing.toString());
            }
    
            errorMessage.append(" Expected:").append(expectedSet);
            errorMessage.append(" Actual:").append(actualSet);
    
           
        }
    
        commonWaits.waitForExecutionComplete();
    }

    public void clickOnApplicationSwitcherOption(String optionName) {
        clickOnApplicationSwitcher();
        commonWaits.clickWithoutInterception(By.xpath(String.format(applicationSwitcherDropdownOption, optionName)));
        commonWaits.waitForExecutionComplete();
        commonWaits.switchToLastWindow();
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
                clickOnApplicationSwitcherOption(applicationName);
                automationConsolePage.verifyAutomationConsolePageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Business Workflows":
                BWFDashboard bwfDashboard = new BWFDashboard();
                clickOnApplicationSwitcherOption(applicationName);
                bwfDashboard.verifyBWFPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "ADE Helix Telemetry":
                DashboardPage telemetryPage = new DashboardPage(driver);
                clickOnApplicationSwitcherOption(applicationName);
                telemetryPage.verifyTelemetryPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Continuous Optimization":
                clickOnApplicationSwitcherOption(applicationName);
                appName = commonWaits.waitForElementVisible(ProductHeader, 40).getText().trim();
                log.info("----- Opened application name - " + appName);
                Assert.assertEquals(appName, "Continuous Optimization", "Continuous Optimization application is not launched successfully");
                goBackToHomePage();
                break;

            case "BMC Helix Dashboards":
                DashboardPage dashboardPage = new DashboardPage(driver);
                clickOnApplicationSwitcherOption(applicationName);
                dashboardPage.verifyDashboardPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Developer Tools":
                DeveloperToolPage developerToolPage = new DeveloperToolPage(driver);
                clickOnApplicationSwitcherOption(applicationName);
                developerToolPage.verifyDeveloperToolPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Digital Workplace":
                dwpDashboard = new DWPDashboard();
                clickOnApplicationSwitcherOption(applicationName);
                dwpDashboard.verifyDWPHomePageOpened();
                goBackToHomePage();
                break;

            case "BMC Helix Digital Workplace Catalog":
                dwpDashboard = new DWPDashboard();
                clickOnApplicationSwitcherOption(applicationName);
                dwpDashboard.verifyDWPCPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Discovery":
                DiscoveryPage discoveryPage = new DiscoveryPage();
                clickOnApplicationSwitcherOption(applicationName);
                discoveryPage.verifyDiscoveryPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Innovation Suite":
                InnovationSuite innovationSuite = new InnovationSuite();
                clickOnApplicationSwitcherOption(applicationName);
                innovationSuite.verifyInnovationSuitePageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Intelligent Automation":
                IntelligentAutomationPage intelligentAutomation = new IntelligentAutomationPage();
                clickOnApplicationSwitcherOption(applicationName);
                intelligentAutomation.verifyIntelligentAutomationPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Intelligent Integrations":
                SWPIntelligentIntegration swpIntelligentIntegration = new SWPIntelligentIntegration(driver);
                clickOnApplicationSwitcherOption(applicationName);
                swpIntelligentIntegration.verifyIntelligentIntegrationPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix ITSM":
                SmartITDashboardPage smartITDashboardPage = new SmartITDashboardPage(driver);
                clickOnApplicationSwitcherOption(applicationName);
                smartITDashboardPage.verifySmartITPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Log Analytics":
                LogAnalytics logAnalytics = new LogAnalytics(driver);
                clickOnApplicationSwitcherOption(applicationName);
                logAnalytics.verifyLogAnalyticsPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

            case "BMC Helix Operations Management":
                BhomPage bhomPage = new BhomPage();
                clickOnApplicationSwitcherOption(applicationName);
                bhomPage.verifyBHOMLaunchedSuccessfully(applicationName);
                goBackToHomePage();
                break;

            case "BMC Helix Service Modeling":
                AIOpsPage aiOpsPage = new AIOpsPage();
                    clickOnApplicationSwitcherOption(applicationName);
                    aiOpsPage.verifyAIOPServiceModelingPageLaunchedSuccessfully();
                    goBackToHomePage();
                    break;

            case "BMC Helix Service Monitoring":
                aiOpsPage = new AIOpsPage();
                clickOnApplicationSwitcherOption(applicationName);
                aiOpsPage.verifyAIOPSPageLaunchedSuccessfully();
                goBackToHomePage();
                break;

                case "BMC Helix CMDB":
                    clickOnApplicationSwitcherOption(applicationName);
                    appName = commonWaits.waitForElementVisible(ProductHeader, 50).getText().trim();
                    log.info("----- Opened application name - " + appName);
                    Assert.assertEquals(appName, "CMDB", "CMDB application is not launched successfully");
                    goBackToHomePage();
                    break;

            default:
                log.info("----- Application name - " + applicationName + " is not supported");
                Assert.fail("----- Application name - " + applicationName + " is not supported");
                break;
        }
    }

    public void selectAllGroups() {
        commonWaits.clickWithoutInterception(groupsBtn);
        commonWaits.waitForElementVisible(allGroupsBtn, 10);
        commonWaits.pureSafeClick(allGroupsBtn);
        if(!confirmBtn.isEnabled()){
            commonWaits.clickByAction(allGroupsBtn);
        }
        commonWaits.clickWithoutInterception(confirmBtn);
        commonWaits.waitForExecutionComplete();
    }

    public void selectAllRoles() {
        commonWaits.clickWithoutInterception(rolesBtn);
        commonWaits.waitForElementVisible(allRolesBtn, 10);
        commonWaits.pureSafeClick(allRolesBtn);
        if(!confirmBtn.isEnabled()){
            commonWaits.clickByAction(allRolesBtn);
        }
        commonWaits.clickWithoutInterception(confirmBtn);
        commonWaits.waitForExecutionComplete();
    }

    public void navigateToProfile() {
        commonWaits.clickWithoutInterception(profileBtn);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(myProfileBtn);
        commonWaits.waitForExecutionComplete();
    }

    public void verifyCopyKeyDetails() throws UnsupportedFlavorException, IOException {
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuItem, "Copy key details")));
        we.click();
        Assert.assertTrue(tenantIDAndKeyCopiedText.isDisplayed(), "Tenant ID and Key has not been copied");
    }

    public void verifyGenerateNewSecretKey() {
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuItem, "Generate new secret key")));
        commonWaits.clickWithoutInterception(we);
        commonWaits.clickWithoutInterception(yesBtn);
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(commonWaits.isElementPresent(secretKeyGeneratedText), "Generate new secret key button is not displayed");
    }

    public void editMyAccessKeys() {
            commonWaits.clickWithoutInterception(actionsBtn);
            commonWaits.clickWithoutInterception(deleteBtn);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        
    }

    public boolean verifyIsUserGroupPresent(String groupName) {
        commonWaits.waitForElementVisible(userGroupSearchInput,10);
        commonWaits.keyClearAndType(userGroupSearchInput, groupName);
        commonWaits.waitForExecutionComplete();
        commonWaits.sendEnterKey();
        commonWaits.waitForExecutionComplete();
        return commonWaits.isElementPresent(By.xpath(String.format(userGroup, groupName)));
    }

    public void createNewUserGroup(String groupName, String userName) {
        navigateToPage("User access", "User groups");
        if(!verifyIsUserGroupPresent(groupName)) {
        log.info("Creating new user group " + groupName);
        commonWaits.waitForElementVisible(addGroupBtn, 10);
        commonWaits.clickWithoutInterception(addGroupBtn);
        commonWaits.waitForExecutionComplete();
        commonWaits.keyClearAndType(groupNameInput, groupName);
        commonWaits.clickWithoutInterception(confirmBtn);
        commonWaits.waitForExecutionComplete();
        log.info("User group " + groupName + " created successfully");
        commonWaits.clickWithoutInterception(selectUserButton);
        commonWaits.keyClearAndType(selectUserSearchInput, userName);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(By.xpath(String.format(selectUserCheckBox, userName)));
        commonWaits.clickWithoutInterception(confirmSelectUserBtn);
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(commonWaits.isElementPresent(oneAssignedText), "User is not assigned to the group");
        log.info("User " + userName + " assigned to the group " + groupName + " successfully");
        } 
        else {
            log.info("User group " + groupName + " already exists");
        }
    }

    public boolean verifyIsRolePresent(String roleName) {
        commonWaits.waitForElementVisible(userGroupSearchInput,10);
        commonWaits.keyClearAndType(userGroupSearchInput, roleName);
        commonWaits.waitForExecutionComplete();
        commonWaits.sendEnterKey();
        commonWaits.waitForExecutionComplete();
        return commonWaits.isElementPresent(By.xpath(String.format(userGroup, roleName)));
    }

    public void createNewRole(String roleName) {
        log.info("Creating new role " + roleName);
        commonWaits.clickWithoutInterception(addRoleBtn);
        commonWaits.waitForExecutionComplete();
        commonWaits.keyClearAndType(roleNameInput, roleName);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(roleTypeDropdown);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(By.xpath(String.format(roleType, "Role")));
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(confirmRoleBtn);
        commonWaits.waitForExecutionComplete();
        log.info("Role " + roleName + " created successfully");
    }

    public void clickOnSelectButton(String buttonName) {
        log.info("Clicking on select button " + buttonName);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectButton, buttonName)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(selectButton, buttonName)));
        commonWaits.waitForExecutionComplete();
    }

    public void addPermissionToRole(String permissionName, String SubExpandName, String SubCollapseName) {
        log.info("Adding permission " + permissionName );
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementVisible(selectUserSearchInput, 10);
        commonWaits.keyClearAndType(selectUserSearchInput, permissionName);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.clickWithoutInterception(expandPermissionBtn);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.clickWithoutInterception(By.xpath(String.format(subExpandButton, SubExpandName)));
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(By.xpath(String.format(subCollapseCheckbox, SubCollapseName)));
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(confirmPermissionBtn);
        log.info("Permission " + permissionName + " added successfully");
    }

    public void addGroupToRole(String groupName) {
        log.info("Adding group " + groupName + " to the role");
        commonWaits.keyClearAndType(selectUserSearchInput, groupName);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(By.xpath(String.format(selectUserCheckBox, groupName)));
        commonWaits.clickWithoutInterception(confirmSelectUserBtn);
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(commonWaits.isElementPresent(oneAssignedText), "Group is not assigned to the role");
        log.info("Group " + groupName + " added successfully");
    }

    public void removeAllGroupsFromUser(String userName) {
        log.info("Removing all user groups");
        navigateToPage("User access", "Users and keys");
        commonWaits.waitForElementVisible(userGroupSearchInput,10);
        commonWaits.keyClearAndType(userGroupSearchInput, userName);
        commonWaits.waitForExecutionComplete();
        commonWaits.sendEnterKey();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementClickable(ActionsButton);
        commonWaits.clickByAction(ActionsButton);
        if(!commonWaits.isElementPresent(By.xpath(String.format(actionsButtonOption, "User options")))){
            commonWaits.clickByJS(ActionsButton);
        }
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(actionsButtonOption, "User options")));
        commonWaits.clickWithoutInterception(By.xpath(String.format(actionsButtonOption, "User options")));
        if(commonWaits.isElementPresent(groupsCount)){
        commonWaits.waitForElementVisible(groupsCount, 10);
        commonWaits.clickWithoutInterception(groupsCount);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(selectAllCheckboxBtn);
        commonWaits.clickWithoutInterception(selectAllCheckboxBtn);
        commonWaits.clickWithoutInterception(removeRoleBtn);
        commonWaits.waitForExecutionComplete();
        }
        log.info("All user groups removed successfully");
    }

    public void addUsersToRole(String userName) {
        log.info("Adding group " + userName + " to the role");
        commonWaits.keyClearAndType(selectUserSearchInput, userName);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(By.xpath(String.format(selectUserCheckBox, userName)));
        commonWaits.clickWithoutInterception(confirmSelectUserBtn);
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(commonWaits.isElementPresent(oneAssignedText), "Group is not assigned to the role");
        getDriver().navigate().refresh();
        commonWaits.waitForExecutionComplete();
        log.info("User " + userName + " added successfully");
    }


    public void addNewRole(String roleName) {
        navigateToPage("User access", "Roles and permissions");
        createNewRole(roleName);
    }
    
}