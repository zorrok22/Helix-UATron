package pages.ITOM.GrafanaDashboard;


import framework.BasePage;
import framework.Driver;
import org.apache.log4j.Logger;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import util.CommonWaits;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import util.SolQAConstants;


import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class ReportingPage extends BasePage {

    static Logger log = Logger.getLogger(ReportingPage.class.getName());

    @FindBy(xpath = "//div[contains(text(),'BMC Helix Operations Management')]//parent::div/parent::div[contains(@class,'top')]")
    public WebElement launchOpManagement;
    @FindBy(xpath = "//div[contains(text(),'BMC Helix Dashboards')]//parent::div/parent::div[contains(@class,'top')]")
    public WebElement launchDashboard;
    @FindBy(id = "idDeviceName")
    public WebElement devicenameId;
    @FindBy(xpath = "//button[@id='header-menu-toggle']")
    public WebElement toggle;
    @FindBy(xpath = "//a[contains(@class,'dropdown-item') and contains(text(),'Launch Dashboard')]")
    public WebElement launchDashboardFromEvents;
    public String lnk_monotoringEvents = "monitoring-events";
    @FindBy(xpath = "//adapt-submenu//button/span[.='Events']")
    public WebElement Events;
    @FindBy(xpath = "//adapt-submenu//button/span[.='Devices']")
    public WebElement Devices;
    @FindBy(xpath = "//*[span/text()='Metrics Data']")
    public WebElement openMetricsDashboard;
    @FindBy(xpath = "//*[span/text()='Event Data']")
    public WebElement openEventDashboard;
    @FindBy(xpath = "//a[contains(@id,'launchDashboard')]")
    public WebElement launchDashboardfromdevice;
    @FindBy(xpath = "//div[@class='grafana-app']")
    public WebElement dashboardHomePage;
    @FindBy(xpath = "xpath=//div[text()='Description']")
    public WebElement description;
    @FindBy(xpath = "xpath=//div[text()='Source']/..//select")
    public WebElement source;
    @FindBy(xpath = "(//device-tile-view//div/a[contains(@href,'#/devices/')])[1]")
    public WebElement firstDeviceName;
    @FindBy(xpath = "//span[.='Monitoring']/parent::button[contains(@class,'adapt-menu__link')]")
    public WebElement Toggle;
    @FindBy(xpath = "(//div[@columnindex='6']/div)[1]")
    public WebElement firstEventName;
    @FindBy(xpath = "(//div[contains(@id,'eventMenuButton')])[1]")
    public WebElement firstOption;
    @FindBy(xpath = "//button[text()='Search...']")
    public WebElement searchDashboardFromTop;
    @FindBy(xpath = "//input[@placeholder='Search or jump to...']")
    public WebElement search;
    public String DashboardInMainSearch = "//div[@role='dialog']//a[@href]//span[text()='%s']";
    @FindBy(xpath = "//div/*[@data-testid='data-testid Select option']")
    public WebElement filterDropdown;
    public String button = "//label[text()='%s']/following-sibling::div//*[contains(@aria-label, 'angle-down')]";
    public String filterValue = "//div[contains(@class,'grafana-select-menu')]";
    public String filterValueButton = "//div[contains(@class,'grafana-select-menu')]//div[@role='option']";
    public String filterValueSelect = "//div[contains(@class,'grafana-select-menu')]//div[@role='option']";
    @FindBy(xpath = "//span[@aria-current='page']")
    public WebElement currentPage;
    public String dashboardAllFilters = "//div[contains(@class, 'css-9il6na')]/label[contains(@class, 'css-1l3ep5k')]";
    @FindBy(xpath = "//section[@data-testid='data-testid Panel header Events without Node Id']")
    public WebElement eventsWithoutNodeIdWidget;
    @FindBy(xpath = "//section[@data-testid='data-testid Panel header Events with Node Id but without service']")
    public WebElement eventsWithNodeWithoutServiceWidget;
    @FindBy(xpath = "//section[@data-testid='data-testid Panel header All class events without Node Id']")
    public WebElement allClassEventsWithoutNodeIdWidget;
    @FindBy(xpath = "//section[@data-testid='data-testid Panel header All class events with Node Id but without service']")
    public WebElement allClassEventsWithNodeWithoutServiceWidget;
    public String svgTextElements = ".//*[local-name()='svg']//*[local-name()='g']//*[local-name()='text' and @pointer-events='none']";
    public String tspanChildren = ".//*[local-name()='tspan']";
    public String legendButtonsInSection = ".//ul//button";
    public String legendButtonsInParentDiv = "ancestor::div[1]//ul//button";
    public String legendButtonsInGrandparentDiv = "ancestor::div[2]//ul//button";
    public String legendLabelsInAncestor = "ancestor::div[3]//*[contains(@class, 'LegendLabel')]";
    public String widgetLegendLabels = ".//*[contains(@class, 'LegendLabel')]";
    public String widgetLegendButtonsFallback = ".//button[contains(@class, 'Legend')]";

    @FindBy(xpath = "//button[@aria-label='Profile']")
    WebElement profile;

    @FindBy(xpath = "//a[@href='/dashboards/logout']")
    WebElement logout;

    String userName =  "//input[@name='user-name']";
    public String Panel = "//div[@data-griditem-key]";

    @FindBy(xpath = "//button[contains(@aria-label,'Share') or contains(@title,'Share') or contains(@data-testid,'share')] | //a[contains(@aria-label,'Share')]")
    WebElement shareButtonLocator;
    @FindBy(xpath = "//button[contains(@aria-label,'Edit time range') or contains(@aria-label,'Time range')]")
    WebElement timePickerLocator;
    @FindBy(xpath = "//label[contains(.,'Last 90 days') or contains(.,'90 days')]")
    WebElement last90DaysLocator;
    @FindBy(xpath = "//label[contains(.,'Last 5 years') or contains(.,'5 years')]")
    WebElement last5yearsLocator;
    @FindBy(xpath = "//div[contains(@class,'css-f8iitu')] | //div[contains(@class,'error')]")
    WebElement errorMessageLocator;
    @FindBy(xpath = "//div[contains(@class,'share-modal')] | //div[contains(@aria-label,'Share')]")
    WebElement shareModalLocator;
    @FindBy(xpath = "//button[contains(.,'Export') or contains(@aria-label,'Export')] | //a[contains(.,'Export')]")
    WebElement exportTabLocator;
    @FindBy(xpath = "//button[contains(.,'Download') or contains(@aria-label,'Download')] | //a[contains(.,'Download')]")
    WebElement downloadTabLocator;
    @FindBy(xpath = "//button[contains(@type,'button')]/span[contains(text(),'Add')]")
    WebElement addPanelControlLocator;
    @FindBy(xpath = "//button[contains(@aria-label,'Save dashboard') or contains(@title,'Save dashboard') or contains(@aria-label,'Save changes')] | //a[contains(@aria-label,'Save dashboard')]")
    WebElement saveDashboardControlLocator;
    @FindBy(xpath = "//button[contains(@aria-label,'Dashboard settings') or contains(@title,'Dashboard settings')] | //a[contains(@aria-label,'Dashboard settings')]")
    WebElement dashboardSettingsControlLocator;
    @FindBy(xpath = "//h1/a[contains(span/text(),'Case Details')]")
    WebElement bwfCaseDetailsHeaderLocator;
    @FindBy(xpath = "//div[@class='panel-title']//h2[text()='Cases by assigned group']//ancestor::div[@data-panelid]//td//a")
    List<WebElement> bwfCasesByAssignedGroupDataPointsLocator;
    @FindBy( xpath="//span[text()='Successfully generated report']")
    WebElement downloadedSuccessMsg;
  
    @FindBy( xpath="//div[@role='columnheader']//div[contains(text(),'Category')]")
    WebElement categoryColumn;
  
    @FindBy( xpath="//div[@role='columnheader']//div[contains(text(),'Usage')]")
    WebElement usageColumn;
  
    @FindBy( xpath="//div[@data-testid='data-testid table body']//div[@role='row']")
    List<WebElement> usageTableRows;
    
    @FindBy( xpath="//div[@role='columnheader']//div[contains(text(),'Device Name')]")
    WebElement deviceNameColumn;
    
    @FindBy( xpath="//div[@role='columnheader']//div[contains(text(),'License Type')]")
    WebElement licenseTypeColumn;
    
    @FindBy( xpath="//div[contains(text(),'No data')]")
    WebElement noDataMessage;
    
    WebDriver driver;
    String homeURL = null;
    CommonWaits commonWaits;

    public ReportingPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public String getDeviceName() {
        return commonWaits.waitForElementVisible(devicenameId).getText();
    }
    public void launch_OpManagement() {
        log.info("-----cross launch Operation Management");
        commonWaits.waitForElementVisible(launchOpManagement, 50);
        commonWaits.waitForElementClickable(launchOpManagement).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void launch_Dashboard() {
        log.info("-----cross launch Dashboard");
        commonWaits.waitForElementVisible(launchDashboard, 50);
        commonWaits.waitForElementClickable(launchDashboard).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void switchToWindow(String windowName) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(windowName)) {
                break;
            }
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }
    public void toggle() {
        commonWaits.waitForElementClickable(Toggle).click();
    }

    public void monitoringDevices() {
        commonWaits.waitForElementClickable(Devices).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void monitoringEvents() {
        commonWaits.waitForElementClickable(Events).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void openOneDevice() {
        String deviceName = "";
        String dashboard = "dashboards";
        log.info("Open one monitor device");
        commonWaits.waitForElementVisible(firstDeviceName);
        deviceName = firstDeviceName.getAttribute("title");

        if (deviceName == "") {
            throw new AssertionError("No Devices are seen to be configured.");
        }
        log.info("Device seen configured is: " + deviceName);
        commonWaits.waitForElementClickable(findElement(By.linkText(deviceName))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

        String actualDevicename = getDeviceName();
        Assert.assertEquals(actualDevicename, deviceName, "Device name is not matching");
        commonWaits.waitForElementClickable(findElement(By.id("idIADDDevActionIcon"))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        log.info("Launch grafana reporting from this device");
        launchDashboardFromDevice();
        log.info("Switch to grafana reporting tab");
        commonWaits.switchToTabByTitle(SolQAConstants.DashboardTitle);
        String reportingURL = getDriver().getCurrentUrl();
        String[] parts = deviceName.split("[/ ]");
        boolean result = reportingURL.contains(parts[0]);
        log.info("Verify device name in to grafana report");
        Assert.assertTrue(reportingURL.contains(dashboard), "Reporting URL is invalid");
        Assert.assertTrue(result,
                "Fail to open report for device " + deviceName);
    }

    public void launchDashboardFromDevice() {
        commonWaits.waitForElementClickable(launchDashboardfromdevice).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void launchDashboardFromEvent() {
        log.info("Launch grafana reporting from this event");
        commonWaits.waitForElementClickable(launchDashboardFromEvents).click();

    }

    public void openOneEvent() {
        String deviceName = "";
        commonWaits.waitForElementVisible(firstEventName);
        deviceName = firstEventName.getAttribute("title");

        if (deviceName == "") {
            throw new AssertionError("No Devices are seen to be configured.");
        }
        log.info("Device seen configured is: " + deviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(firstOption).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        log.info("Launch grafana reporting from this Event");
        launchDashboardFromEvent();
        log.info("Switch to grafana reporting tab");
        commonWaits.switchToTabByTitle(SolQAConstants.HELIX_DASHBOARD);
        String reportingURL = getDriver().getCurrentUrl();
        String[] parts = deviceName.split("[/ ]");
        Boolean result = reportingURL.contains(parts[0]);
    }
    public void verifyMetricsDashboardLoaded(){
        log.info("Validate default metrics panels loaded.");
        commonWaits.waitForElementVisible(openMetricsDashboard);
        Assert.assertTrue(commonWaits.isElementPresent(openMetricsDashboard),
                "Failed to Load Metrics Dashboard");
    }
    public void verifyEventDashboardLoaded(){
        log.info("Validate default events panels loaded.");
        commonWaits.waitForElementVisible(openEventDashboard);
        Assert.assertTrue(commonWaits.isElementPresent(openEventDashboard),
                "Failed to Load Event Data Dashboard");
    }
    public void verifyDashboardPageLaunchedSuccessfully(){
        boolean isPresent = commonWaits.waitForVisibleElement(dashboardHomePage);
        Assert.assertTrue(isPresent, "Dashboards application is not launched successfully");
    }

    public void SearchDashboard(String dashboardName) {
        commonWaits.waitForElementClickable(searchDashboardFromTop).click();
        commonWaits.typeKeys(search, dashboardName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void openDashboard(String dashboardName, String folderName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        SearchDashboard(dashboardName);
        WebElement xpathForDashboardInMainSearch = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DashboardInMainSearch,dashboardName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        commonWaits.scrollToSpecificElement(xpathForDashboardInMainSearch);
        commonWaits.waitForElementClickable(xpathForDashboardInMainSearch).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
    }
    public boolean verifyDashboardLoaded(String dashboardName) {
        log.info("Verifying dashboard loaded: " + dashboardName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        commonWaits.waitForElementVisible(dashboardHomePage);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(dashboardHomePage.isDisplayed(), "Dashboard not loaded successfully: " + dashboardName);
        softAssert.assertAll();
        log.info("Dashboard verified successfully: " + dashboardName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        return true;
    }

    public int getNoOfValuesInFilter(String filterName) {
        log.info("Getting number of values in filter with filtername -->" + filterName);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(button,filterName)))).click();
        Assert.assertTrue(commonWaits.isElementPresent(filterDropdown), "none value appearing for filter -->" + filterName + " , no data available for this filter");
        String type = getDriver().findElement(By.xpath(filterValue)).getAttribute("class");

        int offsetValue = 0;
        if (type.contains("multi")) {
            offsetValue = 1;
        }
        commonWaits.waitForElementPresent(By.xpath(filterValueButton));
        List<WebElement> webElements = commonWaits.getElements(By.xpath(filterValueSelect));
        commonWaits.sendEscKey();
        log.info("Total size of elements is -->" + webElements.size());
        return webElements.size() == 1 ? webElements.size() : webElements.size() - offsetValue;
    }


    public void verifyFilterForDashboard (String filterName){
        log.info("Verifying filter functionality for filter '" + filterName + "'");
        int noOfFilterValues = getNoOfValuesInFilter(filterName);
        Assert.assertTrue(noOfFilterValues >= 1, "noOfFilterValues is not greater than 0 or 1, test failed");
        log.info("Number of values found for '" + filterName + "' are " + noOfFilterValues);
        Random rand = new Random();
        int randomNum = rand.nextInt(noOfFilterValues);
        if (randomNum == 0 || randomNum == 1) {
            randomNum = 1;
        }

        log.info("Selecting random filter value.. for noOfFilterValues -->" + noOfFilterValues);
        if (noOfFilterValues == 1) {
            if (filterName.equals("Line of Business")) {
                selectValueForFilter(filterName, 4);
            } else {
                selectValueForFilter(filterName, randomNum);
            }
        } else if (!filterName.equals("Line of Business")) {
            selectValueForFilter(filterName, randomNum);
        } else {
            selectValueForFilter(filterName, 4);
        }

    }

    public void selectValueForFilter (String filterName,int number){
        log.info("Checking if filter named '" + filterName + "' is found in dashboard.");
        log.info("Selecting Filter value from Filter named '" + filterName + "' index for value is -->" + number);
        Assert.assertTrue(commonWaits.waitForVisibleElement(findElement(By.xpath(String.format(button,filterName)))), "Filter with Name '" + filterName + "' is not found..");
        log.info("Filter with name '" + filterName + "' is found in dashboard.");
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(button,filterName)))).click();
        String type = getDriver().findElement(By.xpath(filterValue)).getAttribute("class");
        int offsetValue = 0;
        if (type.contains("multi")) {
            offsetValue = 1;
        }

        commonWaits.waitForElementPresent(By.xpath(filterValueSelect));
        if (number > 1) {
            List<WebElement> webElements = commonWaits.getElements(By.xpath(filterValueSelect));
            Assert.assertTrue(webElements.size() > offsetValue, "Filter '" + filterName + "' doesn't contain any data to be selected.");
            String filterCheck = getDriver().findElement(By.xpath(filterValueSelect)).getAttribute("class");
            if (!filterCheck.contains("selected")) {
                commonWaits.waitForElementClickable(findElement(By.xpath(filterValueSelect))).click();
            }
        }
        commonWaits.sendEscKey();
        commonWaits.waitForElementClickable(currentPage).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

    }

    public void verifyAllDashboardFilters() {
        log.info("Verifying all dashboard filters....");
        List<WebElement> webElemFilterList = commonWaits.getElements(By.xpath(dashboardAllFilters));
        if (webElemFilterList.size() >= 1) {
            Iterator var2 = webElemFilterList.iterator();

            while(var2.hasNext()) {
                WebElement ele = (WebElement)var2.next();
                String filterName = ele.getText();
                this.verifyFilterForDashboard(filterName);
            }
        }

    }

    public void logoutFromDashboard(){
        commonWaits.waitForElementClickable(profile);
        clickByAction(profile);
        commonWaits.waitForElementClickable(logout);
        clickByAction(logout);
        log.info("this will log out and Login page will appear in some time automatically");
        Assert.assertTrue(isElementPresent(By.xpath(userName)), "Application is not logout successfully");
    }
    public boolean verifyDashboardHasNoErrors() {
        log.info("Verifying dashboard has no errors");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(Panel));
            List<WebElement> panels = commonWaits.getElements(By.xpath(Panel));
            for (int i = 1; i <= panels.size(); i++) {
                By errorIconLocator = By.xpath("//div[@data-panelid][" + i + "]//div[contains(@class,'panel-error')]");
                if (commonWaits.isElementPresent(errorIconLocator)) {
                    WebElement errorIcon = commonWaits.waitForByElementToBeVisible(errorIconLocator);
                    errorIcon.click();
                    if (commonWaits.isElementPresent(errorMessageLocator)) {
                        String panelError = errorMessageLocator.getText();
                        Assert.fail("Panel number - '" + i + "' contains following error : " + panelError);
                    }
                }
            }
            log.info("Dashboard has no errors - verification passed");
            return true;
        } catch (Exception e) {
            log.error("Error while verifying dashboard: " + e.getMessage());
            return false;

        }

    }

    public boolean verifyDashboardHasErrors() {
        log.info("Verifying dashboard panels show errors");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(Panel));
            List<WebElement> panels = commonWaits.getElements(By.xpath(Panel));
            boolean errorFound = false;
            for (int i = 1; i <= panels.size(); i++) {
                By errorIconLocator = By.xpath("//div[@data-panelid][" + i + "]//div[contains(@class,'panel-error')]");
                if (commonWaits.isElementPresent(errorIconLocator)) {
                    errorFound = true;
                }
            }
            if (!errorFound) {
                log.warn("No panel errors detected on dashboard");
            }
            return errorFound;
        } catch (Exception e) {
            log.error("Error while verifying dashboard errors: " + e.getMessage());
            return false;
        }
    }

    public boolean isShareDashboard() {
        log.info("Verifying Share Dashboard functionality");
        try {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
            // Try to find Share Dashboard icon/button
            if (commonWaits.isElementPresent(shareButtonLocator)) {
                commonWaits.waitForElementClickable(shareButtonLocator).click();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
                log.info("Share Dashboard icon found and clicked");
                return true;
            } else {
                log.warn("Share Dashboard icon not found");
                return false;
            }
        } catch (Exception e) {
            log.error("Error while sharing dashboard: " + e.getMessage());
            return false;
        }
    }
    public boolean isExportDashboardAsFile(String fileType) {
        log.info("Exporting Dashboard as " + fileType);
        try {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
            // First ensure share dialog is open

            if (!commonWaits.isElementPresent(shareModalLocator)) {
                Assert.assertTrue(isShareDashboard());
            }
            // Click on Export tab/button

            if (commonWaits.isElementPresent(exportTabLocator)) {
                commonWaits.waitForElementClickable(exportTabLocator).click();
            }
            //Click on Download button

            if (commonWaits.isElementPresent(downloadTabLocator)) {
                commonWaits.waitForElementClickable(downloadTabLocator).click();
            }
            // Select file type and click Download
            By fileTypeButtonLocator = By.xpath("//button[contains(.,'" + fileType + "')] | //a[contains(.,'" + fileType + "')]");
            if (commonWaits.isElementPresent(fileTypeButtonLocator)) {
                WebElement fileTypeButton = commonWaits.waitForByElementToBeVisible(fileTypeButtonLocator);
                commonWaits.waitForElementClickable(fileTypeButton).click();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
                commonWaits.waitForElementVisible(downloadedSuccessMsg);
                log.info("Dashboard export as " + fileType + " initiated");
                return true;
            } else {
                log.warn("Export " + fileType + " option not found");
                return false;
            }
        } catch (Exception e) {
            log.error("Error while exporting dashboard: " + e.getMessage());
            return false;
        }
    }

    public boolean selectLast90Days() {
        log.info("Selecting Last 90 Days time range");
        try {
            // Try to find EditTimepicker (panel time range) first

            if (commonWaits.isElementPresent(timePickerLocator)) {
                commonWaits.waitForElementClickable(timePickerLocator).click();
            }
            // Click on Last 90 days option
            if (commonWaits.isElementPresent(last90DaysLocator)) {
                commonWaits.waitForElementClickable(last90DaysLocator).click();
                return true;
            }
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
            log.info("Successfully selected Last 90 Days time range");
        } catch (Exception e) {
            log.error("Failed to select Last 90 Days: " + e.getMessage());
            throw new AssertionError("Failed to select Last 90 Days time range: " + e);
        }
        return false;
    }
    public boolean selectLast5years() {
        log.info("Selecting Last 5 years time range");
        try {
            // Try to find EditTimepicker (panel time range) first

            if (commonWaits.isElementPresent(timePickerLocator)) {
                commonWaits.waitForElementClickable(timePickerLocator).click();
            }
            // Click on Last years option
            if (commonWaits.isElementPresent(last5yearsLocator)) {
                commonWaits.waitForElementClickable(last5yearsLocator).click();
                return true;
            }
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
            log.info("Successfully selected Last 5 years time range");
        } catch (Exception e) {
            log.error("Failed to select Last 5 years: " + e.getMessage());
            throw new AssertionError("Failed to select Last 5 years time range: " + e);
        }
        return false;
    }

    public boolean hasEditControls() {
        log.info("Checking dashboard edit controls visibility for current user");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        boolean addPanelVisible = commonWaits.isElementPresent(addPanelControlLocator);
        boolean saveDashboardVisible = commonWaits.isElementPresent(saveDashboardControlLocator);
        boolean settingsVisible = commonWaits.isElementPresent(dashboardSettingsControlLocator);
        log.info(String.format("Edit controls presence -> addPanel: %s, saveDashboard: %s, settings: %s",
                addPanelVisible, saveDashboardVisible, settingsVisible));
        return addPanelVisible || saveDashboardVisible || settingsVisible;
    }

    public boolean drillThroughCasesByAssignedGroup() {
        log.info("Launching drill-through from 'Cases by assigned group' panel");
        try {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
            if (bwfCasesByAssignedGroupDataPointsLocator == null || bwfCasesByAssignedGroupDataPointsLocator.isEmpty()) {
                log.warn("No data points found in 'Cases by assigned group' panel to drill through.");
                return true;
            }

            WebElement targetElement = bwfCasesByAssignedGroupDataPointsLocator.get(0);
            int maxValue = 0;
            for (WebElement element : bwfCasesByAssignedGroupDataPointsLocator) {
                String text = element.getText().trim();
                if (text.matches("^[1-9][0-9]*$")) {
                    int value = Integer.parseInt(text);
                    if (value > maxValue) {
                        maxValue = value;
                        targetElement = element;
                    }
                }
            }
            log.info("Clicking data point with value: " + targetElement.getText());
            commonWaits.waitForElementClickable(targetElement).click();
            commonWaits.switchToLastWindow();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            return bwfCaseDetailsHeaderLocator.isDisplayed();
        } catch (Exception e) {
            log.error("Drill-through validation failed: " + e.getMessage());
            return false;
        }
    }
    
    public ReportingPage validateCategoryAndUsageColumnsWithData() {
        log.info("Validating Category and Usage columns are present with data rows");
        
        try {
            log.info("Checking for Category column header");
            Assert.assertTrue(commonWaits.isElementPresent(categoryColumn), "Category column header is not present");
            log.info("✓ Category column header is present");
            
            log.info("Checking for Usage column header");
            Assert.assertTrue(commonWaits.isElementPresent(usageColumn), "Usage column header is not present");
            log.info("✓ Usage column header is present");
            
            log.info("Counting data rows");
            int rowCount = usageTableRows.size();
            log.info("Number of data rows found: " + rowCount);
            
            Assert.assertTrue(rowCount > 0, "Table should have at least one row with data, but found: " + rowCount);
            log.info("✓ Table contains " + rowCount + " data row(s)");
            
            log.info("========== Validation Summary ==========");
            log.info("Category column: PRESENT");
            log.info("Usage column: PRESENT");
            log.info("Data rows count: " + rowCount + " (> 0)");
            log.info("Result: VALIDATION PASSED");
            log.info("==========================================");
            
        } catch (Exception e) {
            log.error("Validation failed: " + e.getMessage());
            Assert.fail("Failed to validate Category and Usage columns with data: " + e.getMessage());
        }
		return this;
    }
    
    public ReportingPage validateLicenseUsageDataInDashboard() {
        log.info("Validating Device Name and License Type columns with data or No data message");
        
        try {
            log.info("Checking for Device Name column header");
            Assert.assertTrue(commonWaits.isElementPresent(deviceNameColumn), "Device Name column header is not present");
            log.info("✓ Device Name column header is present");
            
            log.info("Checking for License Type column header");
            Assert.assertTrue(commonWaits.isElementPresent(licenseTypeColumn), "License Type column header is not present");
            log.info("✓ License Type column header is present");
            
            log.info("Checking for data rows or No data message");
            boolean hasNoDataMessage = commonWaits.isElementPresent(noDataMessage);
            int rowCount = usageTableRows.size();
            
            if (hasNoDataMessage) {
                log.info("✓ 'No data' message is displayed in the table");
            } else if (rowCount > 0) {
                log.info("✓ Table contains " + rowCount + " data row(s)");
            } else {
                Assert.fail("Table should have either data rows (> 0) or 'No data' message, but found neither");
            }
            
            log.info("========== Validation Summary ==========");
            log.info("Device Name column: PRESENT");
            log.info("License Type column: PRESENT");
            log.info("Data status: " + (hasNoDataMessage ? "No data message displayed" : rowCount + " row(s) found"));
            log.info("Result: VALIDATION PASSED");
            log.info("==========================================");
            
        } catch (Exception e) {
            log.error("Validation failed: " + e.getMessage());
            Assert.fail("Failed to validate License Usage data in dashboard: " + e.getMessage());
        }
        return this;
    }

    public WebElement scrollWidgetIntoView(WebDriver localDriver, WebElement widgetElement) {
        log.info("Scrolling widget into view");

        boolean found = false;
        int maxScrollAttempts = 25;
        for (int attempt = 1; attempt <= maxScrollAttempts; attempt++) {
            if (commonWaits.isElementPresent(widgetElement, 2)) {
                log.info("Widget found in DOM on attempt " + attempt);
                found = true;
                break;
            }
            log.info("Widget not in DOM, scrolling page down (attempt " + attempt + "/" + maxScrollAttempts + ")");
            scroll(0, attempt * 500);
        }

        if (!found) {
            log.error("Widget not found in DOM after " + maxScrollAttempts + " scroll attempts");
            return null;
        }

        commonWaits.scrollToSpecificElement(widgetElement);
        commonWaits.moveToElement(localDriver, widgetElement);
        commonWaits.waitForElementVisible(widgetElement, 10);

        log.info("Scroll complete — widget is now in viewport");
        return widgetElement;
    }

    public Map<String, Double> getPieChartDataFromWidget(WebDriver localDriver, WebElement widgetElement) {
        Map<String, Double> classPercentageMap = new HashMap<>();
        try {
            log.info("Extracting pie chart data from widget");

            WebElement widgetSection = scrollWidgetIntoView(localDriver, widgetElement);
            if (widgetSection == null) {
                log.error("Cannot extract pie chart data — widget not found");
                return classPercentageMap;
            }

            commonWaits.waitForElementVisible(widgetElement, 15);
            List<WebElement> legendTextElements = widgetElement.findElements(By.xpath(svgTextElements));
            log.info("Found " + legendTextElements.size() + " SVG text elements with pointer-events='none'");

            for (WebElement textElement : legendTextElements) {
                try {
                    List<WebElement> tspans = textElement.findElements(By.xpath(tspanChildren));

                    if (tspans.size() >= 2) {
                        String className = tspans.get(0).getText().trim();
                        String percentText = tspans.get(1).getText().trim();

                        log.info("Found tspans - Class: '" + className + "', Percentage: '" + percentText + "'");

                        String percentStr = percentText.replace("%", "").trim();

                        if (percentStr.matches("^\\d+(\\.\\d+)?$")) {
                            try {
                                double percentage = Double.parseDouble(percentStr);
                                classPercentageMap.put(className, percentage);
                                log.info("Extracted: " + className + " = " + percentage + "%");
                            } catch (NumberFormatException e) {
                                log.warn("Could not parse percentage for class '" + className + "': " + percentStr);
                            }
                        } else {
                            log.warn("Second tspan doesn't contain a valid percentage: " + percentStr);
                        }
                    } else {
                        log.info("Text element has " + tspans.size() + " tspan(s), expected at least 2");
                    }
                } catch (Exception e) {
                    log.warn("Error processing text element: " + e.getMessage());
                }
            }

            if (classPercentageMap.isEmpty()) {
                log.warn("No percentages extracted from SVG text elements. Widget may not be showing percentage data.");
            }
        } catch (Exception e) {
            log.error("Error extracting pie chart data: " + e.getMessage(), e);
        }
        return classPercentageMap;
    }

    public List<String> getWidgetLegendClasses(WebDriver localDriver, WebElement widgetElement) {
        List<String> legendClasses = new ArrayList<>();
        try {
            log.info("Extracting legend class names from widget");

            List<WebElement> legendButtons = new ArrayList<>();

            legendButtons = widgetElement.findElements(By.xpath(legendButtonsInSection));
            log.info("Strategy 1 (inside section): found " + legendButtons.size() + " legend items");

            if (legendButtons.isEmpty()) {
                legendButtons = widgetElement.findElements(By.xpath(legendButtonsInParentDiv));
                log.info("Strategy 2 (parent div): found " + legendButtons.size() + " legend items");
            }

            if (legendButtons.isEmpty()) {
                legendButtons = widgetElement.findElements(By.xpath(legendButtonsInGrandparentDiv));
                log.info("Strategy 3 (grandparent div): found " + legendButtons.size() + " legend items");
            }

            if (legendButtons.isEmpty()) {
                legendButtons = widgetElement.findElements(By.xpath(legendLabelsInAncestor));
                log.info("Strategy 4 (LegendLabel class): found " + legendButtons.size() + " legend items");
            }

            for (WebElement btn : legendButtons) {
                try {
                    String className = btn.getText().trim();
                    if (!className.isEmpty()) {
                        legendClasses.add(className);
                        log.info("Legend class: '" + className + "'");
                    }
                } catch (Exception e) {
                    log.warn("Error reading legend button text: " + e.getMessage());
                }
            }

            if (legendClasses.isEmpty()) {
                log.warn("No legend classes extracted from widget after trying all strategies");
            }
        } catch (Exception e) {
            log.error("Error extracting legend classes: " + e.getMessage(), e);
        }
        return legendClasses;
    }

    public List<Double> getPieChartPercentagesOnly(WebDriver localDriver, WebElement widgetElement) {
        List<Double> percentages = new ArrayList<>();
        try {
            log.info("Extracting percentage-only SVG labels from widget");

            WebElement widgetSection = scrollWidgetIntoView(localDriver, widgetElement);
            if (widgetSection == null) {
                log.error("Cannot extract SVG percentages — widget not found");
                return percentages;
            }

            List<WebElement> textElements = widgetElement.findElements(By.xpath(svgTextElements));
            log.info("Found " + textElements.size() + " SVG text elements");

            for (WebElement textElement : textElements) {
                try {
                    List<WebElement> tspans = textElement.findElements(By.xpath(tspanChildren));
                    for (WebElement tspan : tspans) {
                        String text = tspan.getText().trim();
                        String cleaned = text.replace("%", "").trim();
                        if (cleaned.matches("^\\d+(\\.\\d+)?$")) {
                            percentages.add(Double.parseDouble(cleaned));
                            log.info("Extracted SVG percentage: " + text);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error processing text element: " + e.getMessage());
                }
            }

            Collections.sort(percentages, Collections.reverseOrder());
            log.info("SVG percentages (sorted desc): " + percentages);
        } catch (Exception e) {
            log.error("Error extracting pie chart percentages: " + e.getMessage(), e);
        }
        return percentages;
    }

    public List<String> getWidgetLegendItems(WebDriver localDriver, WebElement widgetElement) {
        List<String> legendItems = new ArrayList<>();
        try {
            log.info("Extracting legend items from widget");

            WebElement widgetSection = scrollWidgetIntoView(localDriver, widgetElement);
            if (widgetSection == null) {
                log.error("Cannot extract legend items — widget not found");
                return legendItems;
            }

            List<WebElement> legendElements = widgetElement.findElements(By.xpath(widgetLegendLabels));
            log.info("Found " + legendElements.size() + " legend elements via LegendLabel class");

            if (legendElements.isEmpty()) {
                legendElements = widgetElement.findElements(By.xpath(widgetLegendButtonsFallback));
                log.info("Fallback: found " + legendElements.size() + " legend elements");
            }

            for (WebElement element : legendElements) {
                try {
                    String name = element.getAttribute("title");
                    if (name == null || name.trim().isEmpty()) {
                        name = element.getText();
                    }
                    if (name != null && !name.trim().isEmpty()) {
                        legendItems.add(name.trim());
                        log.info("Legend item: '" + name.trim() + "'");
                    }
                } catch (Exception e) {
                    log.warn("Error reading legend element: " + e.getMessage());
                }
            }

            if (legendItems.isEmpty()) {
                log.warn("No legend items extracted from widget");
            }
        } catch (Exception e) {
            log.error("Error extracting legend items: " + e.getMessage(), e);
        }
        return legendItems;
    }

    public Map<String, Double> getEventsWithoutNodeIdPieChartData() {
        return getPieChartDataFromWidget(getDriver(), eventsWithoutNodeIdWidget);
    }

    public Map<String, Double> getEventsWithNodeWithoutServicePieChartData() {
        return getPieChartDataFromWidget(getDriver(), eventsWithNodeWithoutServiceWidget);
    }

    public List<String> getAllClassEventsWithoutNodeIdLegendItems() {
        return getWidgetLegendItems(getDriver(), allClassEventsWithoutNodeIdWidget);
    }

    public List<Double> getAllClassEventsWithoutNodeIdPieChartPercentages() {
        return getPieChartPercentagesOnly(getDriver(), allClassEventsWithoutNodeIdWidget);
    }

    public List<String> getAllClassEventsWithNodeWithoutServiceLegendItems() {
        return getWidgetLegendItems(getDriver(), allClassEventsWithNodeWithoutServiceWidget);
    }

    public List<Double> getAllClassEventsWithNodeWithoutServicePieChartPercentages() {
        return getPieChartPercentagesOnly(getDriver(), allClassEventsWithNodeWithoutServiceWidget);
    }

}