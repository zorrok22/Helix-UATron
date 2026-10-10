package pages.ITOM.Dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import framework.BasePage;
import org.testng.asserts.SoftAssert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import util.CommonMethods;

public class DashboardPage extends BasePage {
    WebDriver driver;

    static SolqaLogger log = new SolqaLogger(DashboardPage.class.getName());
    CommonWaits commonWaits;
    CommonMethods commonMethods;
    Actions action;
    String appName = null;

    @FindBy(xpath = "//span[@title='OTel Service Overview']")
    public WebElement telemetryHomePage;
    public String dashboardHomePage = "//div[@class='grafana-app']";


    String menuItem="//a[contains(@data-testid,'menu item') and contains(.,'%s')]";
    String inputData ="//input[contains(@id,'%s')]";
    String folderLocator = "//a[text()='%s']";
    String checkFloderLocator="//a[text()='%s']//ancestor::div[@role='row']//input";
    String selectValue = "//label[contains(text(),'%s')]//parent::div//span[text()='%s']//parent::button";
    String variableRows= "//div[contains(@data-testid,'table body')]//div[@role='row']";
    String variableValue = variableRows + "[%s]//div[@role='cell'][%s]";
    String selectKey="//label[text()='%s']//parent::div//div[contains(@data-testid,'variable')]";
    String addNewDashboard= "//a[contains(@data-role,'menuitem') and contains(.,'%s')]";
    String addVisualization= "//*[contains(@data-testid,'%s')]";
    String enterQueryDetails="//label[text()='%s']//parent::div//input";
    String dashboardButton="//button[contains(.,'%s')]";
    String seletQueryDetails="//button[.='%s']";
    String selectFilterValue="//*[contains(@aria-label,'%s')]";
    String addFilterValue="//div[contains(@aria-label,'%s')]//span[contains(text(),'%s')]";
    String panelToggleLocator = "//button[contains(.,'%s')  and contains(@aria-label,'Expand row') and contains(@data-testid,'title')]";
    String panelToggleLocator2 = "//button[contains(.,'%s')  and contains(@aria-label,'Collapse row') and contains(@data-testid,'title')]";
    String dashboardPanelTitle = "//center[contains(.,'%s')]//parent::h3/parent::div";
    String panelTitle="//h2[text()='%s']";
    String headerValues="//div[contains(@role,'row')]//div[contains(@role,'columnheader')]";
    String checkETLFilterValue = "//label[contains(text(),'ETL')]//parent::div//*[contains(.,'Selected (0)')]//button[contains(@class,'css-1sbf6wh')]";
    String enterPanelTitle="//*[text()='Title']//ancestor::div[contains(@aria-label,'Panel')]//input";
    String enterDashboardTitle="//*[text()='Title']//ancestor::form//input[contains(@aria-label,'dashboard title')]";
    String enterDashboardDescription ="//*[text()='Title']//ancestor::form//textarea";
    String enterPanelDescription="//*[text()='Description']//ancestor::div[contains(@aria-label,'Panel')]//textarea";
    String saveDashboardChanges=" //span[contains(text(),'Save dashboard')]/parent::button";
    String changeSettingMessage="//textarea[contains(@aria-label,'message')]";
    String searchReport = "//input[contains(@label,'Search')]";
    String bccRecipientValues = "//label[contains(.,'BCC')]//..//..//*[contains(@class,'grafana-select-value-container-grafana-select-value-container-multi')]";
    String buttonLocator = "//button[contains(.,'%s')]";
    String noDataMessage = "//div[contains(@data-testid,'')]";
    String panelContentPresent = "//div[contains(@data-testid,'panel content')]";
    String toggleButtonLocator = "//button[contains(@title,'%s')]";
    String mediumAssignedCountLocator = "//tr[contains(.,'Assigned')]//td[4]//a";
    String firstIncidentLocator = "//div[contains(@role,'row')][1]//div[contains(@role,'cell')]//a";
    String incidentDetailsLabelLocator = "//h2[contains(.,'Incident details')]";
    public String selectFolderCheckbox ="//div[@role='row'][.//a[@title='%s']]//input[@type='checkbox']";
    String deleteSuccessMsg ="//span[text()='Dashboard %s deleted']";
    String tooltipModeOption = "//input[@name='tooltip.mode']/following-sibling::label[contains(normalize-space(),'%s')]";
    String timeRangeOption = "//label[contains(.,'%s')]";
    String metricsBrowserOption = "//button[@role='option' and @title='%s']";
    String metricsBrowserOptionContains = "//button[@role='option' and contains(@title,'%s')]";
    String axisPlacementOption = "//label[@for='option-%s-custom.axisPlacement']";
    String removeThresholdButton = "//button[@aria-label='Remove Threshold %s']";
    String saveDashboardDialogSave = "//*[text()='Title']//ancestor::form//button[contains(.,'Save')]";
    String forecastAlertTitle = "//div[contains(@class,'alert-title') and contains(text(),'%s')]";
    String dropDownFilter = "(//label[normalize-space()='%s']/following::button)[1]";
    String filterTextBox = "(//label[normalize-space()='%s']/parent::div//input)[1]";
    String timeRangeSearchInput = "//label[normalize-space()='%s']/parent::li";
    String nameInETLDetailsPanel = "//h2[text()='ETL details']//ancestor::section//span[contains(text(),'%s')]";

    @FindBy(xpath = "//label[contains(text(),'ETL')]//parent::div//button[contains(@class,'css-1sbf6wh')]")
    WebElement selectAllETLValue;

    @FindBy(xpath = "//input[contains(@placeholder,'Delete')]")
    WebElement confirmDelete;

    @FindBy(xpath = "//*[contains(@aria-label,'Open menu')]")
    WebElement menuIcon;

    @FindBy(xpath = "//button/span[contains(text(),'New')]")
    WebElement addNewButton;

    @FindBy(xpath = "//a[contains(.,'Open ETL list')]")
    WebElement openETLList;

    @FindBy(xpath = "//a//span[text()='New scheduled report']")
    WebElement newScheduledReport;

    @FindBy(xpath = "//div[@class='css-18vnhy4']")
    WebElement refershFilterIcon;
  
    @FindBy(xpath = "//input[@id='dashboard-serach-filter-input']")
    WebElement searchFolder;
  
    @FindBy(xpath = "//span[text()='Delete']")
    WebElement deleteButton;
    
    @FindBy(xpath = "//button[contains(@id,'var-OTelNamespace')]/span")
    WebElement nameSpaceFilterValue;
  
    @FindBy(xpath = "//button[@type='submit']")
    WebElement submitButton;
 
    @FindBy(xpath = "//button[@aria-label='Expand section HelixGPT Insight Finder']")
    public WebElement expandInsightFinder;
    
    @FindBy(xpath = "//button[@aria-label='Open menu']")
    public WebElement openMenuButton;
    
    @FindBy(xpath = "//div[@role='dialog']//p[text()='This action will delete the following content:']")
    public WebElement deleteDialogMsg;
  
    @FindBy(xpath = "//span[text()='Clear']")
    public WebElement clearButton;

    @FindBy(xpath = "//span[@title='ITSM - Service Collaborator Value Dashboard']")
    public WebElement helixGPTITSMServiceCollaboratorValueDashboard;

    @FindBy(xpath = "(//*[@title='Service Collaborator Skills usage']/parent::div/parent::div//following-sibling::div//span)[1]")
    public WebElement serviceCollaboratorSkillsUsage;

    @FindBy(xpath = "//*[@title='MTTR Trend (Hrs)']/parent::div/parent::div//following-sibling::div//div[@class='u-over']")
    public WebElement mttrTrend;

    @FindBy(xpath = "//*[@title='# of Total Incidents']/parent::span/parent::td//following-sibling::td")
    public WebElement numberOfTotalIncidents;

    @FindBy(xpath = "//*[@title='# of Unique Users interacted with Service Collaborator']/parent::div/parent::div//following-sibling::div//span")
    public WebElement numberOfUniqueUsersInteractedWithServiceCollaborator;

    @FindBy(xpath = "//*[@title='SLA Breached Trend (Hrs)']/parent::div/parent::div//following-sibling::div//div[@class='u-over']")
    public WebElement slaBreachedTrend;
    
    @FindBy(xpath = "//div[contains(@class,'u-over')]")
    WebElement chartOverlayArea;

    @FindBy(xpath = "//div[@id='grafana-portal-container']/div")
    WebElement tooltipPopupContent;

    @FindBy(xpath = "//div[contains(@data-testid,'VizLegend')]")
    WebElement vizLegendItems;

    @FindBy(xpath = "//div[contains(@aria-label,'Legend Visibility field property editor')]")
    WebElement legendVisibilityContainer;

    @FindBy(xpath = "//div[contains(@aria-label,'Algorithm type field property editor')]")
    WebElement algorithmTypeContainer;

    @FindBy(xpath = "//div[contains(@aria-label,'Future period field property editor')]")
    WebElement futurePeriodContainer;

    @FindBy(xpath = "//div[contains(@aria-label,'Future period field property editor')]//div[contains(@class,'singleValue')]")
    WebElement futurePeriodSelectedValue;

    @FindBy(xpath = "//div[contains(@class,'u-cursor-x')]")
    WebElement tooltipCursorElement;

    @FindBy(xpath = "//div[contains(@aria-label,'Legend Visibility field property editor')]//input[@role='switch']")
    WebElement legendVisibilityInput;

    @FindBy(xpath = "//div[contains(@aria-label,'Algorithm type field property editor')]//input[@role='combobox']")
    WebElement algorithmTypeInput;

    @FindBy(xpath = "//div[contains(@aria-label,'Future period field property editor')]//input[@role='combobox']")
    WebElement futurePeriodInput;

    @FindBy(xpath = "//body")
    WebElement bodyElement;

    @FindBy(xpath = "//div[@data-testid='data-testid Options group Axis']")
    WebElement axisOptionsGroup;

    @FindBy(xpath = "//div[@data-testid='data-testid Options group Thresholds']")
    WebElement thresholdsOptionsGroup;

    @FindBy(xpath = "//div[@data-testid='uplot-main-div']")
    WebElement timeSeriesChart;

    @FindBy(xpath = "//div[@data-testid='uplot-main-div']//div[contains(@class,'u-axis')][1]")
    WebElement yAxisElement;

    @FindBy(xpath = "//div[@aria-label='Axis Soft min field property editor']//input[@type='number']")
    WebElement softMinInput;

    @FindBy(xpath = "//div[@aria-label='Axis Soft max field property editor']//input[@type='number']")
    WebElement softMaxInput;

    @FindBy(xpath = "//span[text()='Add threshold']/parent::button")
    WebElement addThresholdButton;

    @FindBy(xpath = "//button[@data-testid='data-testid Apply changes and go back to dashboard']")
    WebElement applyChangesButton;
    
    @FindBy(xpath = "//span[contains(@data-testid,'Edit panel breadcrumb')]")
    WebElement editPanelBreadcrumb;

    @FindBy(xpath = "//button[@data-testid='data-testid TimePicker Open Button']")
    WebElement timePickerOpenButton;

    @FindBy(xpath = "//div[@data-testid='uplot-main-div']")
    WebElement forecastGraphChart;

    @FindBy(xpath = "//div[contains(@data-testid,'VizLegend series Prediction')]")
    WebElement predictionLegendItem;

    @FindBy(xpath = "//div[contains(@data-testid,'VizLegend series Prediction')]//button[contains(@class,'LegendLabel')]")
    WebElement predictionLegendLabel;

    @FindBy(xpath = "//button[contains(.,'Metrics browser')]")
    WebElement metricsBrowserButton;

    @FindBy(xpath = "//input[@aria-label='Filter expression for metric']")
    WebElement metricFilterInput;

    @FindBy(xpath = "//input[@aria-label='Filter expression for label']")
    WebElement labelFilterInput;

    @FindBy(xpath = "//input[@aria-label='Filter expression for label values']")
    WebElement labelValuesFilterInput;

    @FindBy(xpath = "//button[@aria-label='Use selector for query button']")
    WebElement useQueryButton;

    @FindBy(xpath = "//div[@aria-label='selector']")
    WebElement resultingSelector;

    @FindBy(xpath = "//div[text()='Source']/following-sibling::div//input[@role='combobox']")
    WebElement sourceCombobox;

    @FindBy(xpath = "//button[@role='option'][1]")
    WebElement firstMatchingOption;

    @FindBy(xpath = "//button[normalize-space()='Cancel']")
    WebElement cancelDashboardChanges;

    @FindBy(xpath = "//button[normalize-space()='Back to dashboard']")
    WebElement backToDashboard;

    @FindBy(xpath = "//a[@title='Dashboards']")
    WebElement dashboardsLink;

    @FindBy(xpath = "//button[contains(@aria-label,'Refresh')]")
    WebElement RefreshDashboardButton;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement saveDashboardButton;

    //Locator variables
    String enabletoggleButtonString = "Enable kiosk mode";
    String toggleButtonString = "Toggle top search bar";

    String sPanelTitleXpath = "//section[.//h2[normalize-space(text())='%s']]";
    String sCssSelectorForTableBody = "table.css-smg4bo tbody tr";

    @FindBy(xpath = "//label[contains(text(),'Business Service')]/following-sibling::div/button")
    WebElement businessServiceDropdown;

    @FindBy(xpath = "//button/span[contains(@data-testid,'Value')]")
    List<WebElement> businessServiceValues;

    String sBusinessServiceValue="//button/span[contains(@data-testid,'Value') and contains(text(),'%s')]";
    String selectOptions = "//div[contains(@role,'option') or contains(@class,'select-option')]";
    @FindBy(xpath = "//div[text()='Source']/following-sibling::div//*[contains(@class,'css-10w7wsd-input-suffix')]")
    WebElement sourceDropdown;
    @FindBy( xpath = "//div[@role='listbox' and @aria-label='Select options menu']//div[@role='option'][.//span[normalize-space()='Optimize']]")
    WebElement sourceDropdownoptimize;
    @FindBy(xpath = "//div[text()='Source']/following-sibling::div")
    WebElement dataSourceSelect;
    @FindBy(xpath = "//label[contains(text(),'Data source')]//parent::div//input")
    WebElement dataSourceInput;
    @FindBy(xpath = "//label[contains(text(),'Query type')]/..//select")
    WebElement queryTypeSelect;
    @FindBy(xpath = "//label[contains(text(),'Query type')]//parent::div//input")
    WebElement queryTypeInput;
    @FindBy(xpath = "//div[contains(text(),'Source')]/..//select | //label[contains(text(),'Source')]/..//select")
    WebElement sourceSelect;
    @FindBy(xpath = "//label[contains(text(),'Source')]//parent::div//input")
    WebElement sourceInput;
    @FindBy(xpath = "//button[contains(@aria-label,'Visualization') or contains(@title,'Visualization')]")
    WebElement visualizationPickerButton;
    @FindBy(xpath = "//input[contains(@aria-label,'Search options') or contains(@placeholder,'Search or filter visualizations') or contains(@placeholder,'Search for...')]")
    WebElement  visualizationSearchInput;
    String visualizationOption = "//div[contains(@role,'option') or contains(@data-testid,'Plugin visualization item Helix Cross-tab') or contains(@title,'%s')]";
    String visualizationSelectedBadge = "//button[contains(@aria-label,'Visualization')]/span[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'%s')]";
    String rowsInputLocator = "//label[contains(.,'Rows')]/following::input[1]";
    String columnsInputLocator = "//label[contains(.,'Columns')]/following::input[1]";
    String crossTabHeaderLocator = "//div[contains(@role,'columnheader') or self::th][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'%s')]";
    @FindBy(xpath = "//div[text()='X-axis']//parent::button")
    WebElement combinationChartXaxisButton;
    @FindBy(xpath = "//div[text()='X-axis']//parent::button/following-sibling::div//label[text()=\"Select a metric\"]/following-sibling::div[1]//input")
    WebElement combinationChartXaxisMetricInput;
    @FindBy(xpath = "//div[text()='X-axis']//parent::button/following-sibling::div//label[text()=\"Specify a label\"]/following-sibling::div[1]//input")
    WebElement  combinationChartXaxisLabelInput;
    @FindBy(xpath = "//div[text()='Left y-axis']//parent::button/following-sibling::div//label[text()=\"Select metrics\"]/following-sibling::div[1]//input")
    WebElement combinationChartLeftYaxisButton;
    @FindBy(xpath = "//div[text()='Left y-axis']//parent::button/following-sibling::div//label[text()=\"Select metrics\"]/following-sibling::div[1]//input")
    WebElement combinationChartLeftYaxisMetricInput;
    @FindBy(xpath = "//div[text()='Left y-axis']//parent::button/following-sibling::div//label[text()=\"Select the chart type\"]/following-sibling::div[1]//input")
    WebElement combinationChartLeftYaxisChartTypeInput;
    @FindBy(xpath = "//div[text()='Left y-axis']//parent::button/following-sibling::div//label[text()=\"Specify a label\"]/following-sibling::div[1]//input")
    WebElement combinationChartLeftYaxisLabelInput;
    @FindBy(xpath = "//*[name()='g' and @class='apexcharts-xaxis-title']")
    WebElement combinationChartXAxisTitle;
    @FindBy(xpath = "//*[name()='g' and @class='apexcharts-yaxis-title']")
    WebElement combinationChartYAxisTitle;
    @FindBy(xpath = "//*[name()='g' and @class='apexcharts-xaxis-texts-g']/*[name()='text']/*[name()='tspan']")
    WebElement combinationChartXAxisValues;
    @FindBy(xpath = "//*[name()='g' and @class='apexcharts-yaxis-texts-g']/*[name()='text']/*[name()='tspan']")
    WebElement combinationChartYAxisValues;

    @FindBy(xpath = "//button[contains(@aria-label,'Time range selected')]")
    WebElement timeRangeButton;

    @FindBy(xpath = "//input[@placeholder='Search quick ranges']")
    WebElement timerangeSearchInput;

    @FindBy(xpath = "(//*[normalize-space()='Total Incidents']/parent::div//following-sibling::div//span)[1]")
    WebElement totalIncidentsCount;

    @FindBy(xpath = "(//*[normalize-space()='MTTR with Service Collaborator (Hrs)']/parent::div//following-sibling::div//span)[1]")
    WebElement mttrValue;

    @FindBy(xpath = "//*[@title='Average Response Time without Service Collaborator (Hrs)']/parent::div/parent::div//following-sibling::div//span")
    WebElement averageResponseTimeWithoutServiceCollaborator;

    @FindBy(xpath = "//*[@title='Average Response Time with Service Collaborator (Hrs)']/parent::div/parent::div//following-sibling::div//span")
    WebElement averageResponseTimeWithServiceCollaborator;
    
    @FindBy(xpath = "//button[@data-testid='insight-finder-top-bar-button']")
    public WebElement insightFinderButton;

    @FindBy(xpath = "(//label[normalize-space()='Actor']/following::button)")
    WebElement removeAllButton;

    @FindBy(xpath = "(//label[normalize-space()='ETL']/following::button)[1]")
    WebElement removeETLFilterButton;

    public DashboardPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    public DashboardPage verifyDashboardPageLaunchedSuccessfully(){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        boolean isPresent = commonWaits.waitForElementVisible_withFluentWait(driver,dashboardHomePage,80);
        Assert.assertTrue(isPresent, "Dashboards application is not launched successfully");
        return this;
    }

    public DashboardPage verifyTelemetryPageLaunchedSuccessfully(){
        commonWaits.waitForElementVisible(telemetryHomePage,50);
        appName = telemetryHomePage.getText().trim();
        log.info("----- Opened application name: Helix OpenTelemetry dashboard - "+ appName);
        Assert.assertEquals(appName,"OTel Service Overview", "Helix OpenTelemetry dashboard is not launched successfully");
        return this;
    }

    public DashboardPage openMenu(String menu) {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        if(!commonWaits.isElementPresent(By.xpath(String.format(menuItem, menu)),40)) {
            clickWithoutInterception(menuIcon);
        }
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuItem, menu))).click();
        return this;
    }

    public DashboardPage openFoldersFromMenu(List<String> folderNames) {
        for(String folderName : folderNames) {
            log.info("Opening folder: " + folderName);
            keyClearAndType(String.format(inputData, "filter-input"), folderName);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(inputData, "filter-input"))).sendKeys(Keys.ENTER);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(folderLocator, folderName))).click();
            log.info("Folder opened successfully: " + folderName);
        }
        return this;
    }

    public DashboardPage openDashboard(String folderName) {
            log.info("Opening folder: " + folderName);
            keyClearAndType(String.format(inputData, "filter-input"), folderName);
            findElement(By.xpath(String.format(inputData, "filter-input"))).sendKeys(Keys.ENTER);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(folderLocator, folderName))).click();
            log.info("Folder opened successfully: " + folderName);
        return this;
    }

    public boolean verifyDashboardPresentWithTag(String folderName, String tag) {
        log.info("Checking folder: " + folderName);
        keyClearAndType(String.format(inputData, "filter-input"), folderName);
        if(isElementPresent(By.xpath(String.format(folderLocator, folderName)))) {
            if(isElementPresent(By.xpath(String.format(seletQueryDetails, tag)))) {
                return true;
            } else {
                log.info("Tag not found: " + folderName);
                return false;
            }
        } else {
            log.info("Folder not found: " + folderName);
            return false;
        }
    }

    public DashboardPage selectValues(String key, String value) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        WebElement ele= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(filterTextBox, key)));
        commonWaits.clickWithoutInterception(ele);
        commonWaits.waitForExecutionComplete();
        ele.sendKeys(value);
        commonWaits.sendEnterKey();
        commonWaits.waitForExecutionComplete();
        action = new Actions(getDriver());
        action.moveByOffset(10, 10).click().perform();
         commonWaits.clickWithoutInterception(RefreshDashboardButton);
        return this;
    }

    public DashboardPage refreshDashboard() {
        log.info("Refreshing dashboard");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dashboardButton, "Refresh")));
        clickWithoutInterception(By.xpath(String.format(dashboardButton, "Refresh")));
        commonWaits.waitForElementInvisible(refershFilterIcon);
        return this;
    }


    /**
     * Checks if a panel with the given title has any data displayed in its graph legend.
     *
     * @param panelTitleText The title of the panel, e.g. "Latency Trend"
     * @return true if data rows exist, false otherwise
     */
    public boolean isLatencyPanelDataPresent(String panelTitleText) {
        try {
            // 1. Locate the panel by its title <h2>
            WebElement panel = driver.findElement(By.xpath(String.format(sPanelTitleXpath, panelTitleText)));

            // 2. Inside the panel, find rows in the table body (legend section)
            List<WebElement> dataRows = panel.findElements(
                    By.cssSelector(sCssSelectorForTableBody)
            );

            // 3. If rows are found, the graph has data
            return !dataRows.isEmpty();

        } catch (Exception e) {
            log.error("Panel with title '" + panelTitleText + "' not found or table missing.");
            return false;
        }
    }


    public String getOtelNameSpaceValue(){
        commonWaits.waitForElementVisible(nameSpaceFilterValue,20);
        String nameSpaceValue = nameSpaceFilterValue.getText().trim();
        log.info("OTel Namespace filter value is : "+nameSpaceValue);
        return nameSpaceValue;
    }


    public List<String> getAllBusinessServices(){
        log.info("Getting all Business Services from Business Service filter");
        List<String> businessServices = new ArrayList<>();
        commonWaits.waitForElementClickable(businessServiceDropdown).click();
        for (WebElement businessServiceValue : businessServiceValues) {
            log.info("Business Service Value: " + businessServiceValue.getText().trim());
            businessServices.add(commonWaits.waitForElementVisible(businessServiceValue).getText().trim());
        }
        return businessServices;
    }

    public DashboardPage selectBusinessService(String businessService){
        if (businessService != null || !businessService.isEmpty() ) {
            log.info("Selecting Business Service : "+businessService);
            commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sBusinessServiceValue,businessService)))).click();
            commonWaits.waitForPageReadyState(driver);
        } else {
            log.info("Business Service is null or empty. Skipping selection.");
        }
        return this;
    }

    public DashboardPage verifyVariableValue(String key, String value) {
        log.info("Verifying value: " + value + " form : " + key);
        commonWaits.waitForElementVisible(findElement(By.xpath(headerValues)));

        if(!value.isEmpty() && ! value.equalsIgnoreCase("All")) {
            int index = 0;
            List<WebElement> values = findElements(By.xpath(headerValues));
            for( int i=0 ; i < values.size(); i++) {
                String headerText = values.get(i).getText();
                if(headerText.equalsIgnoreCase(key)) {
                    index = i+1;
                    break;
                }
            }

            List<String> actualSet = new ArrayList<>();
            commonWaits.waitForSometime(commonWaits.defaultTimeout);
            for(int i=1; i<=findElements(By.xpath(variableRows)).size(); i++) {
                String variableText = findElement(By.xpath(String.format(variableValue, String.valueOf(i), String.valueOf(index)))).getText();
                if(!variableText.trim().isEmpty()) {
                    actualSet.add(variableText);
                }
            }

            Set<String> sortedExpectedSet = null;
            if(value.contains(",")) {
                String[] actualValues = value.split(",");
                List<String> list = new ArrayList<>(Arrays.asList(actualValues));
                sortedExpectedSet = list.stream().map(String::trim).collect(Collectors.toSet());
            } else {
                sortedExpectedSet = new TreeSet<>(Arrays.asList(value));
            }

            Set<String> sortedActualSet = new TreeSet<>(actualSet);
            boolean result = sortedExpectedSet.containsAll(sortedActualSet);
            Assert.assertTrue(result, "Expected values are not matching with filter values.");
        }
            return this;
    }

    public DashboardPage clearAllFilters() {
        commonWaits.waitForSometime(commonWaits.moreDelay);
            int maxAttempts = 5;
            for (int i = 0; i < maxAttempts; i++) {
                    if (removeAllButton != null && removeAllButton.isDisplayed()) {
                        removeAllButton.click();
                       log.info("Clicked removeAllButton - Attempt: " + (i + 1));
                    }
                    else{
                        log.info("Remove all button not found");
                        break;
                    }
                    commonWaits.waitForSometime(commonWaits.minDelay);
                }
                return this;
            }

    public DashboardPage clearETLFilters() {
        log.info("Clearing ETL filters");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectKey, "ETL")));
        log.info("Removing ETL filter");
        commonWaits.waitForSometime(commonWaits.minDelay); //This is to wait for the ETL filter to be loaded
        if(commonWaits.isElementPresent(removeETLFilterButton)) {
            commonWaits.waitForElementClickable(removeETLFilterButton);
            commonWaits.clickWithoutInterception(removeETLFilterButton);
        } else {
            log.info("No ETL filters found to clear.");
        }
        return this;
    }

    public DashboardPage createNewVisualizationDashboard(String dashboardName) {
        log.info("Creating new dashboard: " + dashboardName);
        clickWithoutInterception(addNewButton);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(addNewDashboard, "New dashboard"))).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(addVisualization, "Create new panel"))).click();
        return this;
    }

    public boolean isSourceOptionPresent(String optionText) {
        log.info("Checking Source dropdown for option: " + optionText);
        clickWithoutInterception(sourceDropdown);
        if (commonWaits.isElementPresent(sourceDropdownoptimize)) {
            log.info("Source dropdown is present on the page.");
            return true;
        }
        log.info("Option '" + optionText + "' not found in Source dropdown.");
        return false;
    }

    public DashboardPage enterQueryDetails(String key, String value) {
        log.info("Entering query details for key: " + key + " with value: " + value);
        keyClearAndType(String.format(enterQueryDetails, key), value);
        findElement(By.xpath(String.format(enterQueryDetails, key))).sendKeys(Keys.ENTER);
        return this;
    }

    public DashboardPage selectQueryDetails(String key, String value) {
        log.info("Entering query details for key: " + key + " with value: " + value);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        clickWithoutInterception(By.xpath(String.format(seletQueryDetails, key)));
        keyClearAndType(String.format(selectFilterValue, "Filter expression for datamart"), value);
        clickWithoutInterception(By.xpath(String.format(addFilterValue, value,value)));
        return this;
    }

    public DashboardPage selectVisualizationPlugin(String pluginName) {
        log.info("Selecting visualization plugin: " + pluginName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        if (commonWaits.isElementPresent(visualizationPickerButton)) {
            clickWithoutInterception(visualizationPickerButton);
        }
        if (commonWaits.isElementPresent(visualizationSearchInput)) {
            keyClearAndType(visualizationSearchInput, pluginName);
        }
        By optionLocator = By.xpath(String.format(visualizationOption, pluginName));
        commonWaits.waitForByElementToBeVisible(optionLocator).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        return this;
    }

    public DashboardPage configureCrossTab(String rowField, String columnField) {
        log.info("Configuring cross tab with row '" + rowField + "' and column '" + columnField + "'");
        addFieldToCrossTab(rowsInputLocator, rowField);
        addFieldToCrossTab(columnsInputLocator, columnField);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        return this;
    }

    private DashboardPage addFieldToCrossTab(String inputXpath, String value) {
        By locator = By.xpath(inputXpath);
        if (!isElementPresent(locator)) {
            log.info("Cross tab input not found for locator: " + inputXpath);
        }
        WebElement input = commonWaits.waitForElementClickable(findElement(locator));
        input.click();
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.ENTER);
        return this;
    }

    public boolean verifyCrossTabHeaders(String rowHeader, String columnHeader) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        By rowHeaderLocator = By.xpath(String.format(crossTabHeaderLocator, rowHeader.toLowerCase()));
        By columnHeaderLocator = By.xpath(String.format(crossTabHeaderLocator, columnHeader.toLowerCase()));
        boolean rowPresent = isElementPresent(rowHeaderLocator);
        boolean columnPresent = isElementPresent(columnHeaderLocator);
        log.info(String.format("Cross tab header presence -> row: %s, column: %s", rowPresent, columnPresent));
        return rowPresent && columnPresent;
    }

    public DashboardPage configureCombinationBarChart(String xAxisMetric, String xAxisLabel, String leftYAxisMetric, String leftYAxisChartType, String leftYAxisLabel) {
        log.info("Configuring BMC Combination Chart with X-axis: " + xAxisMetric + " and Y-axis metric: " + leftYAxisMetric);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);

        if (commonWaits.isElementPresent(combinationChartXaxisButton)) {
            commonWaits.waitForElementClickable(combinationChartXaxisButton).click();
        }
        fillInputAndConfirm(combinationChartXaxisMetricInput, xAxisMetric);
        fillInputAndConfirm(combinationChartXaxisLabelInput, xAxisLabel);

        if (commonWaits.isElementPresent(combinationChartLeftYaxisButton)) {
            commonWaits.waitForElementClickable(combinationChartLeftYaxisButton).click();
        }
        fillInputAndConfirm(combinationChartLeftYaxisMetricInput, leftYAxisMetric);
        fillInputAndConfirm(combinationChartLeftYaxisChartTypeInput, leftYAxisChartType);
        fillInputAndConfirm(combinationChartLeftYaxisLabelInput, leftYAxisLabel);

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
        return this;
    }

    public boolean validateCombinationChartData(String expectedXAxisTitle, String expectedYAxisTitle) {
        log.info("Validating BMC Combination Chart axis labels and data points");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);

        String actualXAxisTitle = commonWaits.isElementPresent(combinationChartXAxisTitle)
                ? combinationChartXAxisTitle.getText().trim()
                : "";
        String actualYAxisTitle = commonWaits.isElementPresent(combinationChartYAxisTitle)
                ? combinationChartYAxisTitle.getText().trim()
                : "";

        boolean axisValuesPresent = commonWaits.isElementPresent(combinationChartXAxisValues) && commonWaits.isElementPresent(combinationChartYAxisValues);
        if (!axisValuesPresent) {
            log.warn("Axis values not found for combination chart");
        }
        log.info(String.format("Axis titles -> X: '%s', Y: '%s'", actualXAxisTitle, actualYAxisTitle));
        return axisValuesPresent
                && expectedXAxisTitle.equalsIgnoreCase(actualXAxisTitle)
                && expectedYAxisTitle.equalsIgnoreCase(actualYAxisTitle);
    }

    private void fillInputAndConfirm(WebElement inputLocator, String value) {
        if (!commonWaits.isElementPresent(inputLocator)) {
            log.warn("Combination chart input not present for locator: " + inputLocator);
            return;
        }
        commonWaits.waitForElementClickable(inputLocator);
        inputLocator.click();
        inputLocator.clear();
        inputLocator.sendKeys(value);
        inputLocator.sendKeys(Keys.ENTER);
    }

    public DashboardPage selectTableData() {
        if(isElementPresent(By.xpath(String.format(seletQueryDetails, "Switch to table")))) {
            clickWithoutInterception(By.xpath(String.format(seletQueryDetails, "Switch to table")));
        } else {
            log.info("Table data option not found.");
        }
        return this;
    }

    public DashboardPage enterPanelDetails(String title) {
        log.info("Entering Panel Title as "+ title);
        keyClearAndType(enterPanelTitle, title);
        return this;
    }

    public DashboardPage enterPanelDescription(String description) {
        log.info("Entering Panel Title as "+ description);
        keyClearAndType(enterPanelDescription, description);
        return this;
    }

    public DashboardPage enterDashboardDetails(String title) {
        log.info("Entering Panel Title as "+ title);
        keyClearAndType(enterDashboardTitle, title);
        return this;
    }

    public DashboardPage enterDashboardDescription(String description) {
        log.info("Entering Panel Title as "+ description);
        keyClearAndType(enterDashboardDescription, description);
        return this;
    }

    public DashboardPage saveChanges() {
        commonWaits.waitForByElementToBeVisible(By.xpath(saveDashboardChanges)).click();
        return this;
    }

    public DashboardPage saveDashboard() {
        log.info("Saving dashboard");
           commonWaits.clickWithoutInterception(saveDashboardButton);
           commonWaits.waitForExecutionComplete();
        return this;
    }

    public DashboardPage addChangeMessage(String message) {
        keyClearAndType(changeSettingMessage, message);
        return this;
    }

    public DashboardPage openDashboardSettings() {
        log.info("Opening dashboard settings");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dashboardButton, "Settings")));
        commonWaits.clickWithoutInterception(By.xpath(String.format(dashboardButton, "Settings")));
        return this;
    }

    public DashboardPage addTags(String tags) {
        if(tags.contains(",")) {
            String[] values = tags.split(",");
            for (String tag : values) {
                log.info("Adding tag: " + tag);
                keyClearAndType(String.format(inputData, "tags-input"), tag);
                findElement(By.xpath(String.format(inputData, "tags-input"))).sendKeys(Keys.ENTER);
                findElement(By.xpath(String.format(addVisualization, "General"))).click();

            }
        } else {
            log.info("Adding tag: " + tags);
            keyClearAndType(String.format(inputData, "tags-input"), tags);
            findElement(By.xpath(String.format(inputData, "tags-input"))).sendKeys(Keys.ENTER);
            findElement(By.xpath(String.format(addVisualization, "General"))).click();
        }
        return this;
    }

    public DashboardPage verifyDashboardTitle(String dashboardTitle) {
        log.info("Verifying dashboard title: " + dashboardTitle);
        expandETLPanel(dashboardTitle);
        log.info("Scrolling to bottom of the page");
        commonWaits.scrollToBottomOfPage();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0)");
        Assert.assertTrue(isElementPresent(By.xpath(String.format(dashboardPanelTitle, dashboardTitle))),
                "Dashboard with title " + dashboardTitle + " is not present.");
        collapseETLPanel(dashboardTitle);
        return this;
    }

    public DashboardPage expandETLPanel(String dashboardTitle) {
        if(isElementPresent(By.xpath(String.format(panelToggleLocator, dashboardTitle, "false")))) {
            clickWithoutInterception(By.xpath(String.format(panelToggleLocator, dashboardTitle, "false")));
        }
        return this;
    }

    public DashboardPage collapseETLPanel(String dashboardTitle) {
        if(isElementPresent(By.xpath(String.format(panelToggleLocator2, dashboardTitle)))) {
            clickWithoutInterception(By.xpath(String.format(panelToggleLocator2, dashboardTitle)));
        }
        return this;
    }

    public DashboardPage verifyPanels(String dashboardTitle) {
        log.info("Verifying Panels for : " + dashboardTitle);
        expandETLPanel(dashboardTitle);

        //Verify ETL Details Panel is present
        log.info("Verifying ETL Details Panel present for : " + dashboardTitle);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(panelTitle, "ETL details")));
        Assert.assertTrue(isElementPresent(By.xpath(String.format(panelTitle, "ETL details"))),
                "Dashboard with title " + dashboardTitle + " is not present.");

        //Verify Name in ETL Details Panel
        String tempDashboardTitle = null;
        if(dashboardTitle.contains("Amazon Web Services")) {
            tempDashboardTitle = "Amazon Web Services - AWS API Extractor";
        } else {
            tempDashboardTitle = dashboardTitle;
        }
        log.info("Verifying Name in ETL Details Panel for : " + tempDashboardTitle);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(nameInETLDetailsPanel, tempDashboardTitle)));
        Assert.assertTrue(isElementPresent(By.xpath(String.format(nameInETLDetailsPanel, tempDashboardTitle))),
                "Name in ETL details panel " + dashboardTitle + " is not same.");

        // Verify Loaded Samples Panel is present
        log.info("Verifying Loaded Samples Panel present for : " + dashboardTitle);
        Assert.assertTrue(isElementPresent(By.xpath(String.format(panelTitle, "Loaded samples"))),
                "Dashboard with title " + dashboardTitle + " is not present.");

        collapseETLPanel(dashboardTitle);
        return this;
    }

    public DashboardPage openETLList() {
        log.info("Opening ETL list");
        clickWithoutInterception(openETLList);
        commonWaits.waitForSometime(commonWaits.defaultTimeout);
        List<String> currentWindows = new ArrayList<>(getDriver().getWindowHandles());
        WebDriver currentWindow = getDriver().switchTo().window(currentWindows.get(2));
        setDriver(currentWindow);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        return this;
    }

    public DashboardPage clickOnNewScheduledReport() {
		log.info("Clicking on New Scheduled Report");
		commonWaits.waitForElementVisible(newScheduledReport, 50);
		newScheduledReport.click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        return this;
	}

    public DashboardPage addScheduledReportDetails(String schedulerName, String schedulerDescription, String dashboardName, String recepientsIds, String recepientsBCCIDs) {
		log.info("Adding Scheduled Report Details");
		keyClearAndType(String.format(inputData, "nameFieldId"), schedulerName);
		keyClearAndType(String.format(inputData, "descriptionFieldId"), schedulerDescription);
		keyClearAndType(String.format(inputData, "dashboardPickerFieldId"), dashboardName);
		commonWaits.waitForElementInvisible(findElement(By.xpath(String.format(addVisualization, "Spinner"))));
		findElement(By.xpath(String.format(inputData, "dashboardPickerFieldId"))).sendKeys(Keys.TAB);
		keyClearAndType(String.format(inputData, "recepientsFieldId"), recepientsIds);
		findElement(By.xpath(String.format(inputData, "recepientsFieldId"))).sendKeys(Keys.ENTER);
		keyClearAndType(String.format(inputData, "react-select"), recepientsBCCIDs);
		findElement(By.xpath(String.format(inputData, "react-select"))).sendKeys(Keys.ENTER);
		keyClearAndType(String.format(inputData, "weekDaysFieldId"), "Monday");
		findElement(By.xpath(String.format(inputData, "weekDaysFieldId"))).sendKeys(Keys.ENTER);
		keyClearAndType(String.format(inputData, "hoursFieldId"), "10");
		findElement(By.xpath(String.format(inputData, "hoursFieldId"))).sendKeys(Keys.ENTER);
		clickWithoutInterception(By.xpath(String.format(seletQueryDetails, "Save")));
        return this;
	}

    public DashboardPage openSchedulerReport(String reportName) {
        keyClearAndType(searchReport, reportName);
        clickWithoutInterception(By.xpath(String.format(buttonLocator, reportName)));
        return this;
    }

    public DashboardPage verifyBCCRecipient(String bccRecipient) {
		log.info("Verifying BCC Recipient: " + bccRecipient);
		String bccValues = findElement(By.xpath(bccRecipientValues)).getAttribute("textContent");
		Assert.assertTrue(bccValues.contains(bccRecipient), "BCC Recipient not found: " + bccRecipient);
		log.info("BCC Recipient verified successfully: " + bccRecipient);
        return this;
	}

    public DashboardPage verifyNoDataIsNotPresent() {
		log.info("Verifying no data is not present for dashboard." );
		Assert.assertFalse(isElementPresent(By.xpath(String.format(addVisualization, "Panel data error message"))),
				"No data in panel is present for dashboard.");
        return this;
    }

    public DashboardPage verifyPanelContentPresent() {
        log.info("Verifying panel content is present for dashboard");
        commonWaits.waitForByElementToBeVisible(By.xpath(panelContentPresent));
        Assert.assertTrue(isElementPresent(By.xpath(panelContentPresent)), "Panel content is not present for dashboard.");
        return this;
    }

    public boolean isPanelPresent(String title) {
        log.info("Checking if panel with title '" + title + "' is present");
        return isElementPresent(By.xpath(String.format(panelTitle, title)));
    }

    public DashboardPage disableSearchBarMenu() {
    	if(isElementNotPresent(String.format(toggleButtonLocator, enabletoggleButtonString))) {
    		clickWithoutInterception(By.xpath(String.format(toggleButtonLocator, toggleButtonString)));
    	}
        return this;
    }
    
    public DashboardPage clickOnMediumAssignedCount() {
    	disableSearchBarMenu();
		log.info("Clicking on Medium Assigned Count");
		clickWithoutInterception(By.xpath(mediumAssignedCountLocator));
		commonWaits.switchToLastWindow();
        return this;
    }
    
    public DashboardPage clickOnFirstIncident() {
    	commonWaits.waitForElementPresent(By.xpath(firstIncidentLocator));
    	clickWithoutInterception(By.xpath(firstIncidentLocator));
    	commonWaits.switchToLastWindow();
        return this;
    }
    
    /**
     * Delete dashboard by name
     * Searches for dashboard, selects checkbox, confirms deletion and validates success
     * 
     * @param dashboardName The name of the dashboard to delete
     */
    public DashboardPage deleteDashboard(String dashboardName) {
        log.info("---- Starting dashboard deletion process for: " + dashboardName + " ----");
        log.info("---- Waiting for search folder input ----");
        commonWaits.waitForElementVisible(searchFolder, 15);
        commonWaits.typeAndSelectEachPart(searchFolder, dashboardName);
        
        log.info("---- Wait for folder checkbox to be visible and click ----");
        String checkboxXPath = String.format(selectFolderCheckbox, dashboardName);
        commonWaits.waitForElementPresenceLocated(By.xpath(checkboxXPath));
        WebElement checkbox = getDriver().findElement(By.xpath(checkboxXPath));
        
        commonWaits.clickToElement(checkbox);
        
        log.info("---- Waiting for delete button to be visible ----");
        commonWaits.waitForElementVisible(deleteButton, 10);
        commonWaits.clickWithoutInterception(deleteButton);
        log.info("---- Delete button clicked ----");
        
        log.info("---- Waiting for delete dialog message to be visible ----");
        commonWaits.waitForElementVisible(deleteDialogMsg);
        log.info("---- Delete dialog message is visible ----");
        
        log.info("---- Entering Delete confirmation text ----");
        commonWaits.waitForElementVisible(confirmDelete, 10);
        commonWaits.typeAndSelectEachPart(confirmDelete, "Delete");
        
        log.info("---- Waiting for delete button to confirm deletion ----");
        commonWaits.waitForElementClickable(submitButton, 10);
        clickWithoutInterception(submitButton);
        log.info("---- Delete confirmation button clicked to confirm deletion ----");
        
        String successMsgXPath = String.format(deleteSuccessMsg, dashboardName);
        log.info("---- Waiting for delete success message to be visible ----");
        commonWaits.waitForByElementToBeVisible(By.xpath(successMsgXPath));
        log.info("---- Delete success message is visible ----");
        
        log.info("---- Waiting for delete success message to disappear ----");
        commonWaits.waitForElementInvisible_withFluentWait(getDriver(), successMsgXPath, 10);
        log.info("---- Dashboard deletion completed successfully for: " + dashboardName + " ----");
        
        return this;
    }
    
	/**
	 * Verifies if all dashboard folders from the provided list are present
	 * 
	 * @param folderNames List of folder names to verify
	 */
	public DashboardPage verifyDashboardsInFolder(List<String> folderNames) {
		log.info("Verifying presence of " + folderNames.size() + " dashboard folders");
		boolean allFoldersPresent = true;
		List<String> missingFolders = new ArrayList<>();
		List<String> foundFolders = new ArrayList<>();
		
		for (String folderName : folderNames) {
			log.info("Checking Dashboard: '" + folderName + "'");
			if (commonWaits.isElementPresent(clearButton)) {
				commonWaits.clickWithoutInterception(clearButton);
			}
			keyClearAndType(String.format(inputData, "filter-input"), folderName);
			
			if (isElementPresent(By.xpath(String.format(folderLocator, folderName)))) {
				log.info("✓ Dashboard FOUND: '" + folderName + "'");
				foundFolders.add(folderName);
			} else {
				log.warn("✗ Dashboard NOT FOUND: '" + folderName + "'");
				missingFolders.add(folderName);
				allFoldersPresent = false;
			}
		}
		
		// Summary log
		log.info("========== Verification Summary ==========");
		log.info("Total Dashboard checked: " + folderNames.size());
		log.info("Dashboards found: " + foundFolders.size() + " - " + foundFolders);
		log.info("Dashboards missing: " + missingFolders.size() + (missingFolders.isEmpty() ? "" : " - " + missingFolders));
		log.info("Result: " + (allFoldersPresent ? "ALL DASHBOARDS PRESENT" : "SOME DASHBOARDS MISSING"));
		log.info("==========================================");
	
		Assert.assertTrue(allFoldersPresent, "All expected Dashboard are Present inside Folder");
		return this;
	}
	
	public DashboardPage verifyEditPanelPageOpened() {
		log.info("Verifying Edit Panel page is opened");
		commonWaits.waitForElementVisible(editPanelBreadcrumb, 30);
		Assert.assertTrue(editPanelBreadcrumb.isDisplayed(), "Edit Panel page did not open - breadcrumb not found");
		log.info("Edit Panel page opened successfully");
		return this;
	}

	public DashboardPage verifyForecastAlertMessage(String expectedAlertText) {
		log.info("Verifying forecast alert message: " + expectedAlertText);
		String xpath = String.format(forecastAlertTitle, expectedAlertText);
		boolean isPresent = commonWaits.waitForElementPresent(xpath, 30);
		Assert.assertTrue(isPresent, "Expected alert message '" + expectedAlertText + "' is not displayed");
		log.info("Alert message validated successfully: " + expectedAlertText);
		return this;
	}

	public DashboardPage selectTimeRange(String timeRange) {
		log.info("Selecting time range: " + timeRange);
		commonWaits.waitForElementClickable(timePickerOpenButton).click();
		By timeRangeLocator = By.xpath(String.format(timeRangeOption, timeRange));
		commonWaits.waitForByElementToBeVisible(timeRangeLocator).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
		log.info("Time range selected successfully: " + timeRange);
		return this;
	}

	public DashboardPage verifyForecastGraphAndPredictionDisplayed() {
		log.info("Verifying forecast graph is displayed");
		commonWaits.waitForElementVisible(forecastGraphChart, 30);
		Assert.assertTrue(forecastGraphChart.isDisplayed(), "Forecast graph is not displayed");
		log.info("Forecast graph is displayed successfully");

		log.info("Verifying Prediction series is shown in the legend");
		commonWaits.waitForElementVisible(predictionLegendItem, 30);
		Assert.assertTrue(predictionLegendItem.isDisplayed(), "Prediction series is not displayed in the graph legend");

		String predictionText = predictionLegendLabel.getText().trim();
		log.info("Prediction data from legend: " + predictionText);

		log.info("Prediction series verified successfully in the legend");
		return this;
	}

	public DashboardPage selectQueryType(String queryType) {
		log.info("Selecting query type: " + queryType);
		commonWaits.waitForElementClickable(queryTypeInput).click();
		commonMethods.typeKeysWithEnter(queryTypeInput, queryType);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
		return this;
	}

	public DashboardPage selectSource(String source) {
		log.info("Selecting source: " + source);
		commonWaits.waitForElementClickable(sourceCombobox).click();
		sourceCombobox.sendKeys(source);
		sourceCombobox.sendKeys(Keys.ENTER);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
		return this;
	}

	public DashboardPage openMetricsBrowser() {
		log.info("Opening Metrics browser");
		commonWaits.waitForElementClickable(metricsBrowserButton).click();
		commonWaits.waitForElementVisible(metricFilterInput, 15);
		return this;
	}

	public DashboardPage selectMetric(String metricName) {
		log.info("Selecting metric: " + metricName);
		commonWaits.waitForElementClickable(metricFilterInput).click();
		commonMethods.typeKeysWithEnter(metricFilterInput, metricName);
		By metricLocator = By.xpath(String.format(metricsBrowserOption, metricName));
		commonWaits.waitForByElementToBeVisible(metricLocator).click();
		return this;
	}

	public DashboardPage selectLabel(String labelName) {
		log.info("Selecting label: " + labelName);
		commonWaits.waitForElementVisible(labelFilterInput, 15);
		commonMethods.typeKeysWithEnter(labelFilterInput, labelName);
		By labelLocator = By.xpath(String.format(metricsBrowserOptionContains, labelName));
		commonWaits.waitForByElementToBeVisible(labelLocator).click();
		return this;
	}

	public DashboardPage filterAndSelectFirstLabelValue(String filterText) {
		log.info("Filtering label values with: " + filterText + " and selecting first match");
		commonWaits.waitForElementVisible(labelValuesFilterInput, 15);
		commonMethods.typeKeysWithEnter(labelValuesFilterInput, filterText);
		commonWaits.waitForElementVisible(firstMatchingOption, 15);
		log.info("Selected first matching label value: " + firstMatchingOption.getAttribute("title"));
		firstMatchingOption.click();
		return this;
	}

	public DashboardPage clickUseQuery() {
		log.info("Clicking 'Use query' button");
		commonWaits.waitForElementClickable(useQueryButton).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.HELIX_DASHBOARD);
		return this;
	}

	public DashboardPage verifyResultingSelector(String expectedSelector) {
		log.info("Verifying resulting selector contains: " + expectedSelector);
		commonWaits.waitForElementVisible(resultingSelector, 15);
		String actualSelector = resultingSelector.getText().trim();
		log.info("Resulting selector: " + actualSelector);
		Assert.assertTrue(actualSelector.contains(expectedSelector),
				"Expected selector to contain '" + expectedSelector + "' but was '" + actualSelector + "'");
		return this;
	}

	public DashboardPage configureForecastQuery(String queryType, String source, String metric, String label,
			String labelValueFilter) {
		log.info(
				"Configuring forecast query - QueryType: " + queryType + ", Source: " + source + ", Metric: " + metric);
		selectQueryType(queryType);
		selectSource(source);
		openMetricsBrowser();
		selectMetric(metric);
		selectLabel(label);
		filterAndSelectFirstLabelValue(labelValueFilter);
		clickUseQuery();
		return this;
	}

	public DashboardPage verifyPanelTitleOnChart(String expectedTitle) {
		log.info("Verifying panel title on chart is: " + expectedTitle);
		String xpath = String.format(panelTitle, expectedTitle);
		boolean isPresent = commonWaits.waitForElementPresent(xpath, 30);
		Assert.assertTrue(isPresent, "Panel title on chart did not change to '" + expectedTitle + "'");
		log.info("Panel title on chart verified successfully: " + expectedTitle);
		return this;
	}

	public DashboardPage selectTooltipMode(String tooltipMode) {
		log.info("Selecting tooltip mode: " + tooltipMode);
		String xpath = String.format(tooltipModeOption, tooltipMode);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		WebElement label = driver.findElement(By.xpath(xpath));
		commonWaits.scrollToElement(label);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		label = driver.findElement(By.xpath(xpath));
		String inputId = label.getAttribute("for");
		WebElement radioInput = driver.findElement(By.id(inputId));
		clickToElement(radioInput);
		log.info("Tooltip mode selected: " + tooltipMode);
		return this;
	}

	public DashboardPage hoverOnPanelChart() {
		log.info("Hovering on panel chart overlay area");
		commonWaits.waitForElementVisible(chartOverlayArea, 40);
		Actions actions = new Actions(driver);
		actions.moveToElement(chartOverlayArea).perform();
		log.info("Hovered on panel chart successfully");
		return this;
	}

	public DashboardPage verifyTooltipDisplayed() {
		log.info("Verifying tooltip is displayed on chart");
		commonWaits.waitForElementVisible(tooltipPopupContent, 40);
		Assert.assertTrue(tooltipPopupContent.isDisplayed(), "Tooltip popup is hidden but expected to be visible");
		log.info("Tooltip is displayed on chart - verified successfully");
		return this;
	}

	public DashboardPage clickAwayFromChart() {
		log.info("Clicking away from chart to reset tooltip state");
		clickByAction(bodyElement);
		log.info("Clicked away from chart successfully");
		return this;
	}

	public DashboardPage verifyTooltipNotDisplayed() {
		log.info("Verifying tooltip is NOT displayed on chart");
		Assert.assertFalse(tooltipPopupContent.isDisplayed(), "Tooltip popup is visible but expected to be hidden");
		log.info("Tooltip is NOT displayed on chart - verified successfully");
		return this;
	}

	public DashboardPage toggleLegendVisibility() {
		log.info("Toggling Legend visibility switch");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(legendVisibilityContainer, 40);
		commonWaits.scrollToElement(legendVisibilityContainer);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		clickToElement(legendVisibilityInput);
		log.info("Legend visibility toggle clicked successfully");
		return this;
	}

	public DashboardPage verifyLegendNotVisible() {
		log.info("Verifying Legend is not visible in the chart panel");
		boolean isHidden = commonWaits.waitForElementInvisible(vizLegendItems);
		Assert.assertTrue(isHidden, "Legend items are still visible in the chart panel but expected to be hidden");
		log.info("Legend is not visible in the chart panel - verified successfully");
		return this;
	}

	public DashboardPage selectAlgorithmType(String algorithmType) {
		log.info("Selecting Algorithm Type: " + algorithmType);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(algorithmTypeContainer, 40);
		commonWaits.scrollToElement(algorithmTypeContainer);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		clickWithoutInterception(algorithmTypeInput);
		typeKeysWithEnter(algorithmTypeInput, algorithmType);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Algorithm Type selected successfully: " + algorithmType);
		return this;
	}

	public DashboardPage selectFuturePeriod(String futurePeriod) {
		log.info("Selecting Future Period: " + futurePeriod);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(futurePeriodContainer, 40);
		commonWaits.scrollToElement(futurePeriodContainer);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		clickWithoutInterception(futurePeriodInput);
		typeKeysWithEnter(futurePeriodInput, futurePeriod);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Future Period selected successfully: " + futurePeriod);
		return this;
	}

	public DashboardPage verifyFuturePeriodSelected(String expectedFuturePeriod) {
		log.info("Verifying Future Period selected value is: " + expectedFuturePeriod);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(futurePeriodSelectedValue, 40);
		String actualValue = futurePeriodSelectedValue.getText().trim();
		log.info("Future Period selected value: " + actualValue);
		Assert.assertEquals(actualValue, expectedFuturePeriod, "Future Period selected value mismatch. Expected: "
				+ expectedFuturePeriod + ", Actual: " + actualValue);
		log.info("Future Period verified successfully: " + actualValue);
		return this;
	}

	public DashboardPage selectDataSource(String dataSource) {
		log.info("Selecting Data source: " + dataSource);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementClickable(dataSourceInput).click();
		typeKeysWithEnter(dataSourceInput, dataSource);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Data source selected successfully: " + dataSource);
		return this;
	}

	public DashboardPage verifyAxisOptionVisible() {
		log.info("Verifying Axis option is visible in panel options");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(axisOptionsGroup, 40);
		commonWaits.scrollToElement(axisOptionsGroup);
		Assert.assertTrue(axisOptionsGroup.isDisplayed(), "Axis option group is not visible in panel options");
		log.info("Axis option is visible in panel options - verified successfully");
		return this;
	}

	public DashboardPage verifyThresholdsOptionVisible() {
		log.info("Verifying Thresholds option is visible in panel options");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(thresholdsOptionsGroup, 40);
		commonWaits.scrollToElement(thresholdsOptionsGroup);
		Assert.assertTrue(thresholdsOptionsGroup.isDisplayed(),
				"Thresholds option group is not visible in panel options");
		log.info("Thresholds option is visible in panel options - verified successfully");
		return this;
	}

	public DashboardPage verifyTimeSeriesChartDisplayed() {
		log.info("Verifying Time Series chart is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(timeSeriesChart, 40);
		Assert.assertTrue(timeSeriesChart.isDisplayed(), "Time Series chart is not displayed");
		log.info("Time Series chart is displayed - verified successfully");
		return this;
	}

	public DashboardPage selectAxisPlacement(String placement) {
		log.info("Selecting Axis Placement: " + placement);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(axisOptionsGroup, 40);
		commonWaits.scrollToElement(axisOptionsGroup);
		By placementLocator = By.xpath(String.format(axisPlacementOption, placement.toLowerCase()));
		clickWithoutInterception(placementLocator);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Axis Placement selected successfully: " + placement);
		return this;
	}

	public DashboardPage verifyAxisPlacementShiftedToRight() {
		log.info("Verifying axis placement has shifted to the right side of the chart");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(yAxisElement, 40);
		String style = yAxisElement.getAttribute("style");
		log.info("Y-axis element style: " + style);
		String leftValue = (String) executeJS("return arguments[0].style.left;", yAxisElement);
		int leftPixels = Integer.parseInt(leftValue.replace("px", "").trim());
		Assert.assertTrue(leftPixels > 0, "Axis placement did not shift to the right. Left value: " + leftValue);
		log.info("Axis placement shifted to the right - verified successfully (left: " + leftValue + ")");
		return this;
	}

	public DashboardPage setSoftMinMax(String softMin, String softMax) {
		log.info("Setting Soft min: " + softMin + " and Soft max: " + softMax);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(axisOptionsGroup, 40);
		commonWaits.scrollToElement(bodyElement);
		commonWaits.waitForElementVisible(softMinInput, 40);
		clickWithoutInterception(softMinInput);
		typeKeysWithEnter(softMinInput, softMin);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(softMaxInput, 40);
		clickWithoutInterception(softMaxInput);
		typeKeysWithEnter(softMaxInput, softMax);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Soft min and Soft max set successfully");
		return this;
	}

	public DashboardPage verifySoftMinMaxInForecastGraph(String expectedSoftMin, String expectedSoftMax) {
		log.info("Verifying Soft min: " + expectedSoftMin + " and Soft max: " + expectedSoftMax + " in forecast graph");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(softMinInput, 40);
		String actualSoftMin = softMinInput.getAttribute("value");
		Assert.assertEquals(actualSoftMin, expectedSoftMin,
				"Soft min value mismatch. Expected: " + expectedSoftMin + ", Actual: " + actualSoftMin);
		log.info("Soft min value verified: " + actualSoftMin);
		String actualSoftMax = softMaxInput.getAttribute("value");
		Assert.assertEquals(actualSoftMax, expectedSoftMax,
				"Soft max value mismatch. Expected: " + expectedSoftMax + ", Actual: " + actualSoftMax);
		log.info("Soft max value verified: " + actualSoftMax);
		commonWaits.waitForElementVisible(yAxisElement, 40);
		String yAxisWidth = (String) executeJS("return arguments[0].style.width;", yAxisElement);
		int widthPixels = Integer.parseInt(yAxisWidth.replace("px", "").trim());
		Assert.assertTrue(widthPixels > 22,
				"Y-axis width did not change after setting Soft min/max. Width: " + yAxisWidth);
		log.info("Y-axis width updated to " + yAxisWidth + " confirming Soft min/max applied in forecast graph");
		return this;
	}

	public DashboardPage clickAddThreshold() {
		log.info("Clicking on Add threshold button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementVisible(thresholdsOptionsGroup, 40);
		commonWaits.scrollToElement(thresholdsOptionsGroup);
		commonWaits.waitForElementClickable(addThresholdButton, 40);
		clickWithoutInterception(addThresholdButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Add threshold button clicked successfully");
		return this;
	}

	public DashboardPage verifyNewThresholdAddedWithDeleteOption() {
		log.info("Verifying new threshold is added with delete option");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		By removeNewThreshold = By.xpath(String.format(removeThresholdButton, "1"));
		commonWaits.waitForByElementToBeVisible(removeNewThreshold);
		int removeButtonCount = findElements(By.xpath("//button[starts-with(@aria-label,'Remove Threshold')]")).size();
		Assert.assertTrue(removeButtonCount > 1,
				"New threshold was not added. Remove buttons found: " + removeButtonCount);
		log.info("New threshold added with delete option - verified successfully. Total thresholds with delete: "
				+ removeButtonCount);
		return this;
	}

	public DashboardPage clickApplyChanges() {
		log.info("Clicking Apply changes button to go back to dashboard");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		commonWaits.waitForElementClickable(applyChangesButton, 40);
		clickWithoutInterception(applyChangesButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Apply changes button clicked - navigated back to dashboard");
		return this;
	}

	public DashboardPage saveDashboardWithName(String dashboardName) {
		log.info("Saving dashboard with name: " + dashboardName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		saveDashboard();
		commonWaits.waitForByElementToBeVisible(By.xpath(enterDashboardTitle));
		keyClearAndType(enterDashboardTitle, dashboardName);
		commonWaits.waitForByElementToBeVisible(By.xpath(saveDashboardDialogSave)).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Dashboard saved successfully with name: " + dashboardName);
		return this;
	}

	public DashboardPage reopenDashboard(String menuItem, String dashboardName) {
		log.info("Reopening dashboard: " + dashboardName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		openMenu(menuItem);
		openDashboard(dashboardName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
		log.info("Dashboard reopened successfully: " + dashboardName);
		return this;
	}
    
    public DashboardPage navigateTo_HelixGPT_ITSM_Service_Collaborator_Value_Dashboard() {
        log.info("Navigating to HelixGPT ITSM Service Collaborator Value Dashboard");
        commonWaits.switchToLastWindow();
        commonWaits.waitForAngularRequestsToFinish();
        String CurrentDashboardURL = getDriver().getCurrentUrl();
        String updatedUrl = CurrentDashboardURL.substring(0, CurrentDashboardURL.indexOf("?"))+SolQAConstants.HELIXGPT_ITSM_SERVICE_COLLABORATOR_VALUE_DASHBOARD;
        getDriver().navigate().to(updatedUrl);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertTrue(commonWaits.isElementPresent(helixGPTITSMServiceCollaboratorValueDashboard), "HelixGPT ITSM Service Collaborator Value Dashboard is not opened");
        log.info("HelixGPT ITSM Service Collaborator Value Dashboard opened successfully");
        return this;
    }

    public DashboardPage selectRequiredTimePeriod(String timePeriod) {
        log.info("Selecting required time period: " + timePeriod);
        commonWaits.clickWithoutInterception(timeRangeButton);
        keyClearAndType(timerangeSearchInput, timePeriod);
        commonWaits.clickWithoutInterception(By.xpath(String.format(timeRangeSearchInput, timePeriod)));
        log.info("Time period selected successfully: " + timePeriod);
        return this;

    }

    public String getTotalIncidentsCount() {
        commonWaits.waitForElementVisible(totalIncidentsCount, 10);
        return totalIncidentsCount.getText();
    }

    public String getMTTRValue() {
        commonWaits.waitForElementVisible(mttrValue, 10);
        return mttrValue.getText();
    }

    public String getAverageResponseTimeWithoutServiceCollaborator() {
        commonWaits.waitForElementVisible(averageResponseTimeWithoutServiceCollaborator, 10);
        return averageResponseTimeWithoutServiceCollaborator.getText();
    }

    public String getAverageResponseTimeWithServiceCollaborator() {
        commonWaits.waitForElementVisible(averageResponseTimeWithServiceCollaborator, 10);
        return averageResponseTimeWithServiceCollaborator.getText();
    }

    public String getServiceCollaboratorSkillsUsage() {
        commonWaits.waitForElementVisible(serviceCollaboratorSkillsUsage, 10);
        return serviceCollaboratorSkillsUsage.getText();
    }

    public boolean isMTTRTrendPresent() {
        return commonWaits.isElementPresent(mttrTrend);
    }

    public String getNumberOfTotalIncidents() {
        commonWaits.waitForElementVisible(numberOfTotalIncidents, 10);
        return numberOfTotalIncidents.getText();
    }

    public String getNumberOfUniqueUsersInteractedWithServiceCollaborator() {
        commonWaits.waitForElementVisible(numberOfUniqueUsersInteractedWithServiceCollaborator, 10);
        return numberOfUniqueUsersInteractedWithServiceCollaborator.getText();
    }

    public boolean isSLABreachedTrendPresent() {
        return commonWaits.isElementPresent(slaBreachedTrend);
    }

    public DashboardPage cancelChanges() {
        commonWaits.clickWithoutInterception(cancelDashboardChanges);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public DashboardPage backToDashboard() {
        log.info("Backing to dashboard");
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(backToDashboard);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public DashboardPage clickOnDashboardsLink() {
        log.info("Clicking on Dashboards link");
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(dashboardsLink);
        commonWaits.waitForExecutionComplete();
        return this;
    }


}

