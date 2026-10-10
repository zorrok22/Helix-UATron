package pages.ITSM.InnovationSuite;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class InnovationSuiteAssetConsolePage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    String filterHeader_selectFilterValue = "//div[text()='%s']//ancestor::button";
    String fvalue = "//parent::div//following-sibling::div[contains(@class,'tab')]//*[contains(text(),'%s')]";
    String selectTextFilter_loc = "//parent::div//following-sibling::div[contains(@class,'tab')]//input";
    String smartItFrame = "//iframe[@id='smart-it' and not(contains(@class, 'isHidden'))]";
    String relatedCIsModalXpath = "//span[contains(@class,'dp-title') and contains(text(),'Related CIs')]";
    
    @FindBy(xpath = "//button[contains(@class, 'advanced-filter__dropdown-anchor')]")
    WebElement filterBtn;
    
    @FindBy(xpath = "//button[contains(. ,'Remove all')]")
    WebElement removeAllFiltersBtn;
    
    @FindBy(xpath = "//span[text()='Clear']/parent::button")
    WebElement clearButton;
    
    @FindBy(xpath = "//button[contains(. ,'Apply filters')]")
    WebElement applyFiltersBtn;
    
    @FindBy(xpath = "//input[@type='text' and @placeholder='Search']")
    WebElement searchBox;
    
    @FindBy(xpath = "//button[contains(@class,'adapt-rx-search__search-button')]")
    WebElement searchButton;
    
    @FindBy(xpath = "//button[contains(@id ,'row-actions-dropdown-button') and contains(@class,'dropdown-kabob-btn')]")
    WebElement threeDotsButton;
    
    @FindBy(xpath = "//span[contains(text(),'View related CIs')]")
    WebElement viewRelatedCIOption;
    
    @FindBy(xpath = "//button[contains(@class,'btn-link')]//span[contains(text(),'Dynamic Service Model')]")
    WebElement dynamicServiceModelButton;
    
    @FindBy(xpath = "//button[contains(@class,'btn-link')]//span[contains(text(),'DSM Node Topology')]")
    WebElement dsmNodeTopologyButton;
    
    @FindBy(xpath = "//button[contains(@class,'btn-link')]//span[contains(text(),'CI Explorer')]")
    WebElement ciExplorerButton;

    public InnovationSuiteAssetConsolePage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(InnovationSuiteAssetConsolePage.class.getName());
        commonWaits = new CommonWaits();
    }

    public void clearFilter() {
        log.info("Clearing Asset Console filters");
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        if (commonWaits.isElementPresent(clearButton, 5)) {
            clickWithoutInterception(clearButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        }
    }

    public void clickFilter() {
        log.info("Clicking on Asset Console filter icon");
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForElementVisible(filterBtn);
        clickWithoutInterception(filterBtn);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        log.info("Waiting for filter panel to open");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
    }

    public void applyFilter() {
        log.info("Clicking on Apply Filters in Asset Console");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForElementVisible(applyFiltersBtn);
        clickWithoutInterception(applyFiltersBtn);
    }

    public void selectFilterValue(String filterType, String value) {
        log.info("Selecting filter: " + filterType + " = " + value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        String filterValue = String.format(filterHeader_selectFilterValue, filterType) + String.format(fvalue, value);
        expandFilter(filterType);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterValue, 10);
        WebElement filterValueElement = findElement(By.xpath(filterValue));
        commonWaits.waitForElementVisible(filterValueElement);
        clickWithoutInterception(filterValueElement);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
    }

    public void selectTextFilter(String filterType, String text) {
        log.info("Selecting text filter: " + filterType + " = " + text);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        String filterTextbox = String.format(filterHeader_selectFilterValue, filterType) + selectTextFilter_loc;
        expandFilter(filterType);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterTextbox, 10);
        WebElement filterTextboxElement = findElement(By.xpath(filterTextbox));
        commonWaits.waitForElementVisible(filterTextboxElement);
        filterTextboxElement.sendKeys(text);
    }

    public void expandFilter(String filterType) {
        log.info("Expanding filter: " + filterType);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        String filterHeader = String.format(filterHeader_selectFilterValue, filterType);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterHeader, 20);
        WebElement filterHeaderElement = findElement(By.xpath(filterHeader));
        commonWaits.waitForElementVisible(filterHeaderElement);
        clickWithoutInterception(filterHeaderElement);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
    }

    public void applyFilterAndSearchAsset(String assetType, String status) {
        log.info("Applying filter and searching in Asset Console");
        clickFilter();
        clearFilter();
        selectFilterValue("Asset type", assetType);
        selectFilterValue("Status", status);
        applyFilter();
    }
    
    public void searchCIInAssetConsole(String ciName) {
        log.info("Searching for CI: " + ciName + " in Asset Console search box");
        commonWaits.waitForElementVisible(searchBox);
        searchBox.clear();
        searchBox.sendKeys(ciName);
        commonWaits.waitForSometime(commonWaits.minDelay);
        log.info("Clicking on search button");
        commonWaits.waitForElementVisible(searchButton);
        clickWithoutInterception(searchButton);
        log.info("Clicked search button to search for CI");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForSometime(commonWaits.minDelay);
    }
    
    public void validateCIInSearchResults(String ciName) {
        log.info("Validating CI: " + ciName + " in search results");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        String ciResultXpath = "//td[contains(@class,'at-data-cell')]//span[contains(text(),'" + ciName + "')]";
        WebElement ciResult = findElement(By.xpath(ciResultXpath));
        if(commonWaits.isElementPresent(ciResult)) {
            log.info("CI found in search results: " + ciResult.getText());
            Assert.assertTrue(ciResult.getText().contains(ciName), "CI name validation failed");
            log.info("Successfully validated CI: " + ciName + " in search results");
        } else {
            log.error("CI not found in search results: " + ciName);
            Assert.fail("CI not found in search results: " + ciName);
        }
    }
    
    public void clickThreeDotsAndViewRelatedCI() {
        log.info("Clicking on three dots button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForElementVisible(threeDotsButton);
        clickWithoutInterception(threeDotsButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        
        log.info("Clicking on 'View related CI' option");
        commonWaits.waitForElementVisible(viewRelatedCIOption);
        clickWithoutInterception(viewRelatedCIOption);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Successfully clicked 'View related CI'");
    }
    
    public void validateRelatedCIsModalDisplayed() {
        log.info("Validating 'Related CIs' modal is displayed");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);

        WebElement relatedCIsModal = findElement(By.xpath(relatedCIsModalXpath));
        
        if(commonWaits.isElementPresent(relatedCIsModal)) {
            log.info("'Related CIs' modal is displayed");
            Assert.assertTrue(relatedCIsModal.isDisplayed(), "'Related CIs' modal is not visible");
            log.info("Successfully validated 'Related CIs' modal");
        } else {
            log.error("'Related CIs' modal not found");
            Assert.fail("'Related CIs' modal not displayed");
        }
    }
    
    public void validateThreeButtonsVisible() {
        log.info("Validating three DSM cross-launch buttons are visible");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        
        commonWaits.waitForElementVisible(dynamicServiceModelButton);
        Assert.assertTrue(dynamicServiceModelButton.isDisplayed(), "Dynamic Service Model button is not visible");
        log.info("Dynamic Service Model button is visible");
        
        commonWaits.waitForElementVisible(dsmNodeTopologyButton);
        Assert.assertTrue(dsmNodeTopologyButton.isDisplayed(), "DSM Node Topology button is not visible");
        log.info("DSM Node Topology button is visible");
        
        commonWaits.waitForElementVisible(ciExplorerButton);
        Assert.assertTrue(ciExplorerButton.isDisplayed(), "CI Explorer button is not visible");
        log.info("CI Explorer button is visible");
        
        log.info("Successfully validated all three DSM cross-launch buttons are visible");
    }
    
    public void clickDynamicServiceModelButton() {
        log.info("Clicking on Dynamic Service Model button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForElementVisible(dynamicServiceModelButton);
        clickWithoutInterception(dynamicServiceModelButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Successfully clicked Dynamic Service Model button");
    }
    
    public void clickDSMNodeTopologyButton() {
        log.info("Clicking on DSM Node Topology button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForElementVisible(dsmNodeTopologyButton);
        clickWithoutInterception(dsmNodeTopologyButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Successfully clicked DSM Node Topology button");
    }
    
    public void clickCIExplorerButton() {
        log.info("Clicking on CI Explorer button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForElementVisible(ciExplorerButton);
        clickWithoutInterception(ciExplorerButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Successfully clicked CI Explorer button");
    }
    
    public void validateDiscoveryPageLaunched(String discoveryUrl) {
        log.info("Validating Discovery page launched without errors");
        log.info("Expected Discovery URL contains: " + discoveryUrl);
        
        int existingWindowCount = getDriver().getWindowHandles().size();
        log.info("Existing window count: " + existingWindowCount);
        
        commonWaits.switchToLastWindow();
        
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        
        String currentUrl = getDriver().getCurrentUrl();
        log.info("Current URL after cross-launch: " + currentUrl);
        
        if (currentUrl.contains(discoveryUrl)) {
            log.info("Successfully validated Discovery page launched - URL contains: " + discoveryUrl);
        } else {
            log.error("Discovery URL validation failed. Expected URL to contain: " + discoveryUrl + ", but got: " + currentUrl);
            Assert.fail("Discovery page validation failed - URL does not contain expected keyword: " + discoveryUrl);
        }
        
        commonWaits.switchToTabByIndex(0);
    }
    
    public void validateCMDBExplorerWidgetOpened(String urlKeyword) {
        log.info("Validating CMDB Explorer Widget is opened");
        log.info("Expected URL contains keyword: " + urlKeyword);
        
        int existingWindowCount = getDriver().getWindowHandles().size();
        log.info("Existing window count: " + existingWindowCount);
        
        commonWaits.switchToLastWindow();

        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        
        String currentUrl = getDriver().getCurrentUrl();
        log.info("Current URL: " + currentUrl);
        
        if (currentUrl.contains(urlKeyword)) {
            log.info("Successfully validated CMDB Explorer Widget opened - URL contains keyword: " + urlKeyword);
        } else {
            log.error("CMDB Explorer validation failed. Expected URL to contain: " + urlKeyword + ", but got: " + currentUrl);
            Assert.fail("CMDB Explorer validation failed - URL does not contain expected keyword: " + urlKeyword);
        }
        
        commonWaits.switchToTabByIndex(0);
    }
}

