package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class AssetConsolePage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators (XPaths with %s parameters or dynamic path construction)
    public String btnLocator = "//*[@ux-id='%s']";
    public String filterHeader_selectFilterValue = "//div[contains(@class, 'tc__filter-toggle')]//div[contains(@class, 'dropdown-item__header') and text()='%s']";
    public String fieldValue = "/..//div[contains(@class, 'dropdown-item') and contains(text(), '%s')]";
    public String selectTextFilter_loc = "/..//input";
    public String assetMoreMenuItem = "//button[@role='menuitem']//span[normalize-space()='%s']";
    public String bcmActionMenuItem = "//button[@role='menuitem']//span[normalize-space()='%s']";
    public String activityFeedItem = "//div[contains(@class,'feed-item') or contains(@ux-id,'activity')]//span[contains(text(),'%s')]";

    //WebElements Locators
    @FindBy(xpath = "//div[contains(@class,'ngViewport')]/div/div[1]")
    WebElement firstAsset;

    @FindBy(xpath = "//input[@ux-id='console-search-bar']")
    WebElement searchBarInput;

    @FindBy(xpath = "//button[@aria-label='More options']")
    WebElement moreOptionsButton;

    @FindBy(xpath = "//iframe[contains(@src,'bcm') or contains(@title,'Asset Details')]")
    WebElement bcmIframe;

    @FindBy(xpath = "//input[@id='username' or @name='username']")
    WebElement bcmUsername;

    @FindBy(xpath = "//input[@id='password' or @name='password' or @type='password']")
    WebElement bcmPassword;

    @FindBy(xpath = "//button[contains(text(),'Login') or @type='submit']")
    WebElement bcmLoginBtn;

    public AssetConsolePage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(AssetConsolePage.class.getName());
        commonWaits = new CommonWaits();
    }

    public void clearFilter() {
        log.info("Clicking on clear filter button.");
        if(isElementClickable(findElement(By.xpath(String.format(btnLocator, "clear-filters-btn"))))) {
            findElement(By.xpath(String.format(btnLocator, "clear-filters-btn"))).click();
        }
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void clickFilter() {
        log.info("Clicking on filter icon");
        findElement(By.xpath(String.format(btnLocator, "console-filter-btn"))).click();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void applyFilter() {
        log.info("Clicking on Apply Filters");
        findElement(By.xpath(String.format(btnLocator, "console-apply-filter-btn"))).click();
        commonWaits.waitForElementEnabled(String.format(btnLocator, "clear-filters-btn"), commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForExecutionComplete();
    }

    public void selectFilterValue(String filterType, String value) {
        String filterValue = String.format(filterHeader_selectFilterValue, filterType)
                + String.format(fieldValue, value);
        expandFilter(filterType);
        findElement(By.xpath(filterValue)).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void selectTextFilter(String filterType, String text) {
        String filterTextbox = String.format(filterHeader_selectFilterValue, filterType) + selectTextFilter_loc;
        expandFilter(filterType);
        keyClearAndType(filterTextbox, text);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        findElement(By.xpath(filterTextbox)).sendKeys(Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void expandFilter(String filterType) {
        log.info("Expanding filter: " + filterType);
        scrollToElement(findElement(By.xpath(String.format(filterHeader_selectFilterValue, filterType))));
        if (findElement(By.xpath((String.format(filterHeader_selectFilterValue, filterType)))).getAttribute("class")
                .contains("collapsed")) {
            findElement(By.xpath((String.format(filterHeader_selectFilterValue, filterType)))).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }
    }

    public void searchInSearchBar(String searchText) {
        log.info("Searching in search bar: " + searchText);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(searchBarInput);
        keyClearAndType(searchBarInput, searchText);
        searchBarInput.sendKeys(Keys.ENTER);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void applyFiltersAndSearch(String assetType, String status, String searchText) {
        clearFilter();
        clickFilter();
        selectFilterValue("Asset Type", assetType);
        selectFilterValue("Status", status);
        applyFilter();
        searchInSearchBar(searchText);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void openFirstAsset() {
        log.info("Opening first asset from asset console");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.clickWithoutInterception(firstAsset);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    /**
     * Click on More dropdown button on asset page
     */
    public AssetConsolePage clickMoreDropdown() {
        log.info("Clicking on More dropdown");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(moreOptionsButton);
        clickWithoutInterception(moreOptionsButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    /**
     * Select an option from More dropdown menu
     * @param menuItem Menu item text (e.g., "Asset Details")
     */
    public AssetConsolePage selectMoreMenuItem(String menuItem) {
        log.info("Selecting menu item from More dropdown: " + menuItem);
        WebElement menuOption = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(assetMoreMenuItem, menuItem)));
        clickWithoutInterception(menuOption);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    /**
     * Login to BCM with credentials
     * @param username BCM username
     * @param password BCM password
     */
    public AssetConsolePage loginToBCM(String username, String password) {
        log.info("Logging in to BCM with username: " + username);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        
        // Check if BCM iframe is present and switch to it
        if (commonWaits.isElementPresent(bcmIframe)) {
            switchToFrame(bcmIframe);
        }
        
        commonWaits.waitForElementVisible(bcmUsername);
        keyClearAndType(bcmUsername, username);
        keyClearAndType(bcmPassword, password);
        clickWithoutInterception(bcmLoginBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        log.info("BCM login completed");
        return this;
    }

    /**
     * Select BCM action from More menu
     * @param action Action name (e.g., "Audit Now", "Reboot", "Shut down", "Wake up", "Check Connection")
     */
    public AssetConsolePage selectBCMAction(String action) {
        log.info("Selecting BCM action: " + action);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        
        // Switch to default frame first
        switchtoDefaultFrame();
        
        // Check if we're in BCM iframe
        if (commonWaits.isElementPresent(bcmIframe)) {
            switchToFrame(bcmIframe);
        }
        
        // Click on More dropdown if not already open
        if (commonWaits.isElementPresent(moreOptionsButton)) {
            clickWithoutInterception(moreOptionsButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }
        
        // Select the action
        WebElement actionItem = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(bcmActionMenuItem, action)));
        clickWithoutInterception(actionItem);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        log.info("BCM action selected: " + action);
        return this;
    }

    /**
     * Verify if activity/notification is present in activities tab
     * @param expectedText Expected text in activity feed
     * @return true if activity is found, false otherwise
     */
    public boolean verifyActivityInFeed(String expectedText) {
        log.info("Verifying activity in feed: " + expectedText);
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        boolean isPresent = commonWaits.isElementPresent(By.xpath(String.format(activityFeedItem, expectedText)));
        log.info("Activity found: " + isPresent);
        return isPresent;
    }

    /**
     * Open full asset information page
     */
    public AssetConsolePage openFullAssetInformation() {
        log.info("Opening full asset information");
        openFirstAsset();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

}

