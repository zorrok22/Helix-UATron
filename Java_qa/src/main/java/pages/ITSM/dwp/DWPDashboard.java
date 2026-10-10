package pages.ITSM.dwp;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.containsString;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPDashboard extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    Map<String, String> propertiesConfig = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    //String locators (dynamic templates only)
    public String ENHANCED_BUCKET_NAME = "//*[@data-ta='mediaSectionTitle'][contains(text(), '%s')]";
    public String ElementXpath = "//*[@data-ta='%s']";
    public String dwpPage = "//div[contains(@class, 'navigation-wrapper')]//span[contains(@class, 'a-product__name')]";
    public String dwpcPage = "//div[contains(text(), 'Service')]";
    String compactNavigationTab = "//a[contains(@class, 'a-hamburger__menu-link') and normalize-space()='%s']";
    String dwpNavigationTabs = "//nav//ul/li[contains(@class,'a-menu__item')]//a/span[normalize-space()='%s']/parent::a";
    public String assetGroupBucket = "//h2[contains(., '%s') and contains(@data-ta,'mediaSectionTitle')]";
    public String serviceRequestInMyStuff = "//dwp-card[contains(., '%s')]";
    public String moreOptionServiceRequestInMyStuff = "//dwp-card[contains(., '%s')]//button";
    public String serviceRequestOptionInCarousel = "//a[contains(@role,'option') and contains(.,'%s')]";
    public String SET_TEXTFIELD_BYLABEL = "//h2[contains(normalize-space(), '%s')]";
    public String XPATH_SEARCH_TOGGLE_BUTTON_ACTIVE = "//div[normalize-space()='%s' ]/ancestor::button[@aria-checked='true']";
    public String XPATH_SEARCH_TOGGLE_BUTTON = "//div[normalize-space()='%s' ]/ancestor::button";
    String notificationPanelXpath = "//p[contains(@class, 'approval-delegation-notification__title') and contains(normalize-space(.), '%s')]";
    String broadcastBanner = "//*[contains(@class,'banner') or contains(@class,'alert') or contains(@class,'notification') or contains(@class,'broadcast')]//*[contains(normalize-space(.), '%s')]";
    String bwfIFrame = "//iframe[@title='BWF']";
    public String availableFilter = "//*[contains(@class,'tag-active')]";

    //WebElements Locators
    @FindBy(xpath = "//*[@data-ta='typeaheadSearchInputWrapper']")
    WebElement DWP_HOMEPAGE;

    @FindBy(xpath = "//*[@data-ta= 'cardFullViewAction']")
    List<WebElement> catalogItems;

    @FindBy(xpath = "//a//*[contains(@class, 'a-product__logo')]")
    WebElement dwpLogo;

    @FindBy(xpath = "//dwp-category-dropdown//*[@data-ta='browserCategories']")
    WebElement dwpBrowserCategories;

    @FindBy(xpath = "//*[contains(@class,'erver-error-message-container')]//p")
    WebElement loginErrorMessage;

    @FindBy(css = ".a-hamburger__close>button.close")
    WebElement burgerMenuCloseButton;

    @FindBy(xpath = "//*[@data-ta='typeaheadSearchInputWrapper']")
    WebElement dwpHomePageMarker;

    @FindBy(xpath = "//div[contains(@class, 'navigation-wrapper')]//span[contains(@class, 'a-product__name')]")
    WebElement dwpPageMarker;

    @FindBy(xpath = "//div[contains(text(), 'Service')]")
    WebElement dwpcPageMarker;

    @FindBy(xpath = "//div[contains(@class,'profile dropdown')]//button")
    WebElement profileIcon;

    @FindBy(xpath = "//button[contains(text(),'Administration')]")
    WebElement administrationButton;

    @FindBy(xpath = "//*[@data-ta='typeaheadSearchInput']//input")
    WebElement GLOBAL_SEARCH_INPUT;

    @FindBy(css = ".adapt-rx-search .adapt-rx-search__form-control-block_active")
    WebElement GLOBAL_SEARCH_ACTIVATED;

    @FindBy(xpath = "//dwp-typeahead-search|//dwp-global-search-field")
    WebElement GLOBAL_SEARCH_BAR;

    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    public WebElement dwpLogoutMenu;

    @FindBy(xpath = "//button[contains(text(),'Sign out')]")
    public WebElement dwpSignOut;

    @FindBy(xpath = "//button[contains(.,'Yes')]")
    public WebElement yesButton;

    @FindBy(xpath = "//button[contains(@class, 'a-hamburger')]")
    WebElement hamburgerMenu;

    @FindBy(xpath = "//a[contains(@href, 'mystuff')]")
    WebElement myStuffPageLink;

    @FindBy(xpath = "//a[contains(@class,'menu') and contains(., 'Digital Workplace Admin')]")
    WebElement dwpAdminPageMarker;
    
    @FindBy(xpath = "//button[contains(@data-testid,'remove-btn')]")
    WebElement clearFilter;

    public DWPDashboard() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(DWPDashboard.class.getName());
        commonWaits = new CommonWaits();
    }

    public DWPDashboard verifyLogin() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), dwpLogo, 15), "Login to DWP failed");
        return this;
    }

    public DWPDashboard navigateToPage(String pageName) {
        getDriver().get(propertiesConfig.get("appServer") + pageName);
        Assert.assertTrue(commonWaits.waitForVisibleElement(dwpBrowserCategories), "Navigation to " + pageName + " failed");
        return this;
    }

    public boolean verifyCatalogSection() {
        boolean flag = false;
        if (catalogItems != null && !catalogItems.isEmpty()) {
            try {
                commonWaits.waitForElementVisible(catalogItems.get(0), 10);
            } catch (Exception ignored) {
            }
        }
        List<WebElement> items = catalogItems;
        assert items != null;
        if (!items.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    public DWPDashboard verifyErrorMessage(String errorMessage) {
        log.info("Verifying error message when Wrong username or password entered");
        Assert.assertTrue((loginErrorMessage.getText()).equals(errorMessage) || (loginErrorMessage.getText()).equals("Incorrect username or password"), "Error message not matched");
        return this;
    }

    public DWPDashboard closeBurgerMenuIfPresentDWP() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(burgerMenuCloseButton)) {
            closeBurgerMenu();
        }
        return this;
    }

    private DWPDashboard closeBurgerMenu() {
        log.info("Closing Side Menu");
        commonWaits.waitForElementVisible(burgerMenuCloseButton).click();
        return this;
    }

    public DWPDashboard closeDWP() {
        if (getDriver() != null) {
            getDriver().quit();
        }
        return this;
    }

    public boolean isShowAllItemsLinkVisible() {
        return isElementPresent(By.xpath(String.format(ENHANCED_BUCKET_NAME, "All items and services")));
    }

    public DWPDashboard verifyDWPHomePageOpened() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), DWP_HOMEPAGE, 30), "DWP HomePage is not opened URL is: " + getDriver().getCurrentUrl());
        log.info("verify Login successful and DWP HomePage is opened");
        return this;
    }

    public DWPDashboard verifyDWPPageLaunchedSuccessfully() {
        commonWaits.waitForElementVisible(dwpPageMarker,40);
        String appName = dwpPageMarker.getText().trim();
        Assert.assertEquals(appName, "Digital Workplace", "Digital Workplace application is not launched successfully");
        return this;
    }

    public DWPDashboard verifyDWPCPageLaunchedSuccessfully() {
        commonWaits.waitForElementVisible(dwpcPageMarker);
        boolean isdwpcPresent = commonWaits.isElementPresent(dwpcPageMarker);
        Assert.assertTrue(isdwpcPresent, " Digital Workplace Catalog application is not launched successfully");
        return this;
    }

    public DWPDashboard openSrmRequestByRequestID(String reqID) {
        commonWaits.waitForByElementToBeVisible((By.xpath(String.format(ElementXpath, "typeaheadSearchInput"))));
        log.info("Entering requestID in the search box");
        keyClickAndType(String.format(ElementXpath, "typeaheadSearchInput"), reqID);
        if (commonWaits.isElementPresent((By.xpath(String.format(ElementXpath, "typeaheadSearchDropdownOption_0"))))) {
            click(By.xpath(String.format(ElementXpath, "typeaheadSearchDropdownOption_0")));
        }
        click(By.xpath(String.format(ElementXpath, "typeaheadSearchTriggerButton")));
        log.info("Opening the requestID displayed");
        commonWaits.waitForByElementToBeVisible((By.xpath(String.format(ElementXpath, "anySearchResultsItemTitle"))));
        click(By.xpath(String.format(ElementXpath, "anySearchResultsItemTitle")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard verifySRDTitle(String titleName) {
        commonWaits.waitForByElementToBeVisible((By.xpath(String.format(ElementXpath, "coloredMediaHeaderTitle"))));
        getValueFromText(String.format(ElementXpath, "coloredMediaHeaderTitle"));
        return this;
    }

    public DWPDashboard refreshDWPPage() {
        getDriver().navigate().refresh();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard navigateToAdminPage() {
        String baseUrl = getDWPURL();
        String adminUrl = baseUrl.replace("/app", "/admin");
        int idx = adminUrl.indexOf("/admin") + "/admin".length();
        adminUrl = adminUrl.substring(0, idx);
        log.info("Admin Page URL: " + adminUrl);
        getDriver().get(adminUrl);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard navigateToAdmin() {
        String currentUrl = getDWPURL();
        int appIndex = currentUrl.indexOf("/app");
        Assert.assertTrue(appIndex > -1, "Current URL does not contain '/app' segment to trim: " + currentUrl);
        String adminUrl = currentUrl.substring(0, appIndex) + "/admin";
        log.info("Admin Page URL (trimmed from current URL): " + adminUrl);
        getDriver().get(adminUrl);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard dwpLogout() {
        clickWithoutInterception(dwpLogoutMenu);
        clickWithoutInterception(dwpSignOut);
        clickWithoutInterception(yesButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public DWPDashboard activateGlobalSearch(String placeholderText) {
        log.info("Activating Global Search");
        scrollToPageTop();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (!commonWaits.isElementPresent(GLOBAL_SEARCH_ACTIVATED)) {
            commonWaits.waitForElementVisible(GLOBAL_SEARCH_BAR);
            clickByAction(GLOBAL_SEARCH_BAR);
            commonWaits.waitForElementVisible(GLOBAL_SEARCH_INPUT);
            String defaultTxt = GLOBAL_SEARCH_INPUT.getAttribute("placeholder");
            MatcherAssert.assertThat("Global search is not activated.",
                    defaultTxt, containsString(placeholderText));
            Assert.assertTrue(commonWaits.isElementPresent(GLOBAL_SEARCH_ACTIVATED), "Global search is not active");
            log.info("Global Search is activated!");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard performSearchWOWaitingResults(final String searchCriteria) {
        log.info("Performing Global Search using keywords: '" + searchCriteria + "'");
        if (commonWaits.isElementPresent(GLOBAL_SEARCH_INPUT)) {
            GLOBAL_SEARCH_INPUT.sendKeys(searchCriteria);
            GLOBAL_SEARCH_INPUT.sendKeys(Keys.ENTER);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        } else {
            activateGlobalSearch("Hello, how can I help you?");
            typeKeysWithEnter(GLOBAL_SEARCH_INPUT, searchCriteria);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
        log.info("Asking question: " + searchCriteria + " and waiting for response");
        return this;
    }

    public boolean isNavigationMenuItemVisible(String tabText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(hamburgerMenu)) {
            commonWaits.waitForElementClickable(hamburgerMenu).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            boolean present = commonWaits.isElementPresent(By.xpath(String.format(compactNavigationTab, tabText)));
            closeBurgerMenuIfPresentDWP();
            return present;
        }
        return commonWaits.isElementPresent(By.xpath(String.format(dwpNavigationTabs, tabText)));
    }

    public DWPDashboard openNavigationMenuItem(String tabText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(hamburgerMenu)) {
            commonWaits.waitForElementClickable(hamburgerMenu).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            WebElement tab = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(compactNavigationTab, tabText)));
            commonWaits.clickWithoutInterception(tab);
        } else {
            WebElement tab = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dwpNavigationTabs, tabText)));
            commonWaits.clickWithoutInterception(tab);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.switchtoDefaultFrame();
        if (commonWaits.isElementPresent(By.xpath(bwfIFrame))) {
            commonWaits.switchToFrame(By.xpath(bwfIFrame));
        }
        return this;
    }

    public DWPDashboard navigateToMyStuffPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        String myStuffUrl = getDWPURL() + "mystuff";
        log.info("My Stuff Page URL: " + myStuffUrl);
        getDriver().get(myStuffUrl);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard verifyServiceRequestInMyStuff(String requestID, String assetGroupName) {
        log.info("Verifying service request " + requestID + " in My Stuff page under " + assetGroupName);
        commonWaits.waitForAngularRequestsToFinish();
        Assert.assertTrue(findElement(By.xpath(String.format(assetGroupBucket, assetGroupName))).isDisplayed(), "Asset Group " + assetGroupName + " should be visible");
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(serviceRequestInMyStuff, requestID))), "Service request should appear in My Stuff page under " + assetGroupName);
        return this;
    }

    public DWPDashboard clickOnServiceCarouselInMyStuff(String serviceName, String serviceCarousel) {
        log.info("Clicking on service carousel for request: " + serviceName);
        commonWaits.waitForAngularRequestsToFinish();
        clickWithoutInterception(By.xpath(String.format(moreOptionServiceRequestInMyStuff, serviceName)));

        log.info("Verify Day2 actions list is displayed in button format");
        Assert.assertTrue(verifyDay2ActionsDisplayed(serviceCarousel),
                "Day2 actions list must be displayed in button format");
        clickWithoutInterception(By.xpath(String.format(serviceRequestOptionInCarousel, serviceCarousel)));

        log.info("Verify questions related to service Action are displayed");
        Assert.assertTrue(verifyDay2ActionQuestionsDisplayed(serviceCarousel),
                "Questions related to service Action must be displayed");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        log.info("Clicked on service carousel successfully");
        return this;
    }

    public void removeAllFiltersOnServiceHealthIfPresent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (isElementNotPresent(availableFilter)) {
            log.info("Filter is already removed.");
        } else {
            log.info("Removing active filter from BWF dashboard");
            clickWithoutInterception(clearFilter);
        }
    }

    
    public boolean verifyDay2ActionsDisplayed(String serviceName) {
        log.info("Verifying Day2 actions are displayed");
        commonWaits.waitForAngularRequestsToFinish();
        return commonWaits.isElementPresent(By.xpath(String.format(serviceRequestOptionInCarousel, serviceName)));
    }

    public boolean verifyDay2ActionQuestionsDisplayed(String serviceCarousel) {
        log.info("Verifying Day2 action questions are displayed");
        commonWaits.waitForAngularRequestsToFinish();
        return commonWaits.isElementPresent(By.xpath(String.format(SET_TEXTFIELD_BYLABEL, serviceCarousel)));
    }

    public DWPDashboard defaultSearchToggleButton(String button) {
		log.info("Checking default search toggle button: " + button);
			if( commonWaits.isElementPresent(By.xpath(String.format(XPATH_SEARCH_TOGGLE_BUTTON_ACTIVE, button)))) {
				log.info("Default search toggle button is already set to: " + button);
			} else {
				log.info("Default search toggle button is not set to: " + button + ". Setting it now.");
				By buttonLocator = By.xpath(String.format(XPATH_SEARCH_TOGGLE_BUTTON, button));
				WebElement ele = commonWaits.waitForByElementToBeVisible(buttonLocator);
				commonWaits.clickWithoutInterception(ele);
                commonWaits.waitForExecutionComplete();
			}
        return this;
	}

    @FindBy(xpath = "//button[contains(@aria-label, 'notifications')]")
    WebElement notificationBellButtonXpath;
    public DWPDashboard openNotification() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(notificationBellButtonXpath);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public boolean isNotificationPresent(String expectedTextContains) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (expectedTextContains == null || expectedTextContains.trim().isEmpty()) {
            return false;
        }
        By notification = By.xpath(String.format(notificationPanelXpath, expectedTextContains));
        return commonWaits.isElementPresent(notification, 2);
    }

    public boolean isBroadcastBannerDisplayed(String expectedTextContains) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (expectedTextContains == null || expectedTextContains.trim().isEmpty()) {
            return false;
        }
        return commonWaits.isElementPresent(By.xpath(String.format(broadcastBanner, expectedTextContains)), 2);
    }

    public DWPDashboard verifyAlternateApproverMessageInNotification(String originalApproverFullName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        long start = System.currentTimeMillis();
        long timeoutMs = 60_000;
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (!findElements(By.xpath(String.format(notificationPanelXpath, originalApproverFullName))).isEmpty()) {
                return this;
            }
        }
        Assert.fail("Alternate approver notification not found for original approver: " + originalApproverFullName);
        return this;
    }

    public DWPDashboard navigateToMyActivityPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        String myActivityUrl = getDWPURL() + "/activity";
        log.info("My Activity Page URL: " + myActivityUrl);
        getDriver().get(myActivityUrl);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPDashboard verifyDWPAdminPageLaunchedSuccessfully() {
        commonWaits.waitForElementVisible(dwpAdminPageMarker);
        Assert.assertTrue(commonWaits.isElementPresent(dwpAdminPageMarker), "Digital Workplace Administration application is not launched successfully");
        return this;
    }

}
