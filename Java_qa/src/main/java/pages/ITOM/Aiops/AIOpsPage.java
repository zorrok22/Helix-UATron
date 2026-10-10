package pages.ITOM.Aiops;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.BasePage;
import org.openqa.selenium.*;
import org.testng.Assert;
import util.CommonWaits;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;
import util.DeepEval;
import util.SolQAConstants;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Bhom.BhomPage;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AIOpsPage extends BasePage {

    static SolqaLogger log = new SolqaLogger(AIOpsPage.class.getName());
    CommonWaits commonWaits = new CommonWaits();
    int healthScore100 = 100;
    List<String> eventOptionList = Arrays.asList("Acknowledge Event", "Assign Event...", "Close Event", "Decline Ownership", "Set Event Priority...", "Take Ownership", "Unacknowledge Event", "Add Notes");

    @FindBy(xpath = "//a[@href='#/entities']")
    WebElement entitlesTab;

    @FindBy(xpath = "//a[@href='#/risks']")
    WebElement riskTab;

    @FindBy(xpath = "//a[@href='#/situations']")
    WebElement situationsTab;

    // AIOps Dashboard Navigation Tabs - Dynamic XPath template
    private static final String AIOPS_TAB_XPATH_TEMPLATE = "//span[@class='a-menu__text' and text()='%s']/parent::a";
    
    // AIOps Risk Sub-tabs - Dynamic XPath template for Vulnerabilities and Change Risk Advisor tabs
    private static final String AIOPS_RISK_SUB_TAB_XPATH_TEMPLATE = "//span[contains(@class, 'nav-link-title') and text()='%s']/ancestor::button[@role='tab']";

    /** Configuration sub-tabs: link matched by URL fragment ({@code %s}) or anchor text ({@code %s}). */
    private static final String AIOPS_CONFIGURATION_SUB_TAB_XPATH_TEMPLATE = "//a[contains(@href, '%s') or text()='%s']";

    /** Product Features page: role=switch toggle for a feature row by title ({@code %s}). */
    private static final String AIOPS_PRODUCT_FEATURE_TOGGLE_XPATH_TEMPLATE =
            "//span[@class='h5 font-weight-bold' and text()='%s']/ancestor::div[contains(@class,'d-flex flex-row')]//input[@role='switch']";

    // Vulnerability search (Risk > Vulnerabilities grid)
    private static final String VULNERABILITY_SEARCH_INPUT_XPATH = "//input[@placeholder='Search Vulnerability Name or CVE ID']";
    private static final String VULNERABILITY_SEARCH_BUTTON_XPATH = "//button[contains(@class, 'adapt-rx-search__search-button')]";
    private static final String VULNERABILITY_RESULT_LINK_XPATH_TEMPLATE = "//a[contains(@class, 'vulnerability-name') and contains(text(), '%s')]";
    private static final String ANY_VULNERABILITY_RESULT_LINK_XPATH = "//a[contains(@class, 'vulnerability-name')]";

    // Asset selection and row actions (impacted assets grid)
    private static final String ASSET_ROW_BY_NAME_XPATH_TEMPLATE = "//td[.//div[contains(@class, 'w-100') and contains(@class, 'ellipsis') and normalize-space(.)='%s']]/ancestor::tr";
    private static final String ASSET_CHECKBOX_IN_ROW_XPATH = ".//div[contains(@class, 'ui-chkbox-box')]";
    private static final String ASSET_ACTION_MENU_IN_ROW_XPATH = ".//span[contains(@class, 'd-icon-ellipsis')]";
    /** Fallback listing of asset-name cells when row selection fails (debug logging). */
    private static final String ASSET_GRID_ELLIPSIS_DIVS_XPATH = "//div[contains(@class, 'ellipsis')]";
    private static final String PLAN_AUTOMATION_LINK_XPATH = "//a[contains(@class, 'action-links') and contains(text(), 'Plan Automation')]";
    private static final String UPDATE_STATUS_LINK_XPATH = "//a[contains(@class, 'action-links') and contains(text(), 'Update Status')]";

    @FindBy(xpath = "//div[@id='situation-detail-incident']//span")
    WebElement situationIncidentID;

    @FindBy(xpath = "//span[text()='Create Service']")
    WebElement createService;

    @FindBy(xpath = "//a[@id='toggle-tile-button']")
    WebElement toggleTilebutton;

    @FindBy(xpath = "//span[@id='severity-Critical-count']")
    WebElement criticalCount;

    @FindBy(xpath = "//span[@id='severity-Major-count']")
    WebElement majorCount;

    @FindBy(xpath = "//span[@id='severity-Minor-count']")
    WebElement minorCount;

    @FindBy(xpath = "//span[@id='severity-Ok-count']")
    WebElement okCount;

    @FindBy(xpath = "//*[@stroke-opacity='1']//*[@stroke-opacity='1']")
    List<WebElement> services;

    @FindBy(xpath = "//tbody[@class='ui-table-tbody']//td[1]")
    WebElement message;

    @FindBy(xpath = "//button[contains(@id,'actionDropdownMenu')]")
    WebElement actionDropdownMenu;

    @FindBy(xpath = "//button[@id='confirmation-yes-btn']")
    WebElement confirmYesButton;

    @FindBy(xpath = "//aiops-action-menu//button[contains(@id,'actionDropdownMenu')]")
    WebElement actionDropdownMenuOnEventDetailsPage;

    @FindBy(xpath = "//td[contains(@data-testid,'adapt-table_column')]//span[contains(@class,'alert-link')]")
    WebElement openFirstEventFromService;

    @FindBy(xpath = "//p[@id='event-detail-incident_id']")
    WebElement eventDetailIncidentID;

    @FindBy(xpath = "//a[contains(text(),'INC')]")
    WebElement incidentIDColumn;

    @FindBy(xpath = "//span[contains(.,'Create Automation')]")
    WebElement createAutomation;

    @FindBy(xpath = "//span[contains(.,'Create Incident')]")
    WebElement createIncident;

    @FindBy(xpath = "//span[contains(.,'Request Automation')]")
    WebElement requestAutomation;

    @FindBy(xpath = "//button[@id='estimate-field']")
    WebElement requestDropdown;

    @FindBy(xpath = "//button[@id='estimate-field_popup-0']")
    WebElement dropdownItemImmediately;

    @FindBy(xpath = "//button[contains(@id,'request-automation-button')]")
    WebElement submitButton;

    @FindBy(xpath = "(//*[@aria-label='Chart']//*[local-name()='tspan'])[2]")
    WebElement serviceNameOnChart;

    @FindBy(xpath = "//input[@id='search-text-box']")
    WebElement searchServiceText;

    @FindBy(xpath = "//input[@placeholder='Search by situation name']")
    WebElement searchSituation;

    @FindBy(xpath = "(//span[contains(@id,'automation-link')])[1]")
    WebElement automationLink;

    @FindBy(xpath = "(//button[contains(@id,'actionDropdownMenu')])[1]")
    WebElement actionDropdownMenuOnProbableCausePage;

    @FindBy(xpath = "//button/span[contains(text(),'Create Incident')]")
    WebElement createIncidentButton;

    @FindBy(xpath = "//button//span[contains(text(),'Yes')]")
    WebElement yesButtonCreatIncident;

    @FindBy(xpath = "//span[contains(.,'Yes')]")
    WebElement confirmationButton;

    @FindBy(xpath = "//button[@aria-label='Close']")
    WebElement closeButton;

    @FindBy(xpath = "//p[contains(text(),'No data available')]")
    WebElement noDataAvailable;

    @FindBy(xpath = "//*[@id='service-name']")
    WebElement serviceName;

    @FindBy(xpath = "//a[@href='#/configurations']")
    WebElement configurationTab;

    @FindBy(xpath = "//a[@href='#/configurations/manage-product-features']")
    WebElement manageProductFeatures;

    @FindBy(xpath = "//li[@id='manage.service.blueprints']")
    WebElement manageServiceBlueprints;

    @FindBy(xpath = "//*[@data-testid='intelligent-automations_input']")
    WebElement intelligentAutomationPermission;

    @FindBy(xpath = "//*[@data-testid='ml-based-situations_input']")
    WebElement mlBasedSituationPermission;

    @FindBy(xpath = "//*[@data-testid='root-cause-isolation_input']")
    WebElement rootCaseIsolationPermission;

    @FindBy(xpath = "//*[@id='dialogueCloseButton']")
    WebElement closeBtn;

    @FindBy(xpath = "//*[@title='Tile View']")
    WebElement tileView;

    @FindBy(xpath = "//span[@id='service-health-score']")
    WebElement healthScoreValue;

    @FindBy(xpath = "//span[contains(text(), 'Future Capacity Risk')]//..//span[contains(@class,'score-value')]")
    WebElement FutureCapacityRisk;
    
    @FindBy(xpath = "//*/span[contains(text(), 'Analyze Root Cause') or contains(text(), 'Analyze Probable Cause')]")
    WebElement AnalyzeProbablecauseTab;

    @FindBy(xpath = "//div[contains(normalize-space(),'Events')]/parent::span/parent::button")
    WebElement eventsButton;

    @FindBy(xpath = "//h2[@id='service-name']")
    WebElement serviceNameLocator;

    @FindBy(xpath = "(//header//nav//span/span)[2]")
    WebElement bmcAIOPSPage;

    @FindBy(xpath = "//div[@id='pca-causal-nodes-panel']")
    WebElement casualNode;

    @FindBy(xpath = "//td[@data-testid='adapt-table_column-0_row-0']")
    WebElement firstEvent;

    @FindBy(xpath = "//span[.='Event Details']")
    WebElement eventDetailsTitle;

    @FindBy(xpath = "//label[normalize-space()='CRITICAL']")
    WebElement status;

    @FindBy(xpath = "//label[@id='event-detail-priority']")
    WebElement eventDetailsPriority;

    @FindBy(xpath = "//h5[@id='event-detail-status']")
    WebElement eventStatus;

    @FindBy(xpath = "//span[.='More Details']")
    WebElement moreDetails;

    @FindBy(xpath = "//span[.='Assigned to']")
    WebElement assignedTo;

    @FindBy(xpath = "//div[.='Summary']")
    WebElement summary;

    @FindBy(xpath = "//p[@id='event-detail-class' and contains(text(),'nagios_ev1')]")
    WebElement className;

    @FindBy(xpath = "//p[.='Incident ID']")
    WebElement incidentId;

    @FindBy(xpath = "//p[.='Object Class']")
    WebElement objectClass;

    @FindBy(xpath = "//p[.='Object']")
    WebElement object;

    @FindBy(xpath = "//p[.='Host']/../..//p[.=' ${VC_Cluster}']")
    WebElement host;

    @FindBy(xpath = "//div[normalize-space()='Event Score ']")
    WebElement eventScore;

    @FindBy(xpath = "//*/span[contains(text(), 'Analyze Situations')]")
    WebElement situationSection;

    @FindBy(xpath = "//span[@id='event-detail-msg']")
    WebElement eventDetailsName;

    String policyNameWebElement = "//span[contains(.,'$policyName')]";
    String situationName = "//a/span[contains(text(), '%s')]";
    String runThisAutomation = "//span[contains(.,'%s')]/parent::div/parent::div/parent::div//button//span[contains(text(),'Run this Automation')]";
    String policyStatus = "//span[contains(.,'%s')]/parent::div/parent::div/following-sibling::div/div";
    String firstDisplayedService = "//aiops-tile-view//div[contains(@class,'entity-name text') and contains(.,'%s')]";
    String optionsLocators = "//span[contains(text(),'%s')]";
    @FindBy(xpath = "//a[@id='toggle-tile-button']")
    WebElement toggleTileButton;

    @FindBy(xpath = "//*[@id='service-detail-refresh']")
    WebElement serviceRefreshButton;

    @FindBy(xpath = "//*[name()='svg']//*[@serviceName='New Business Service']")
    WebElement newBusinessService;

    @FindBy(xpath = "//button[@id='service-node-content-edit-static']")
    WebElement serviceAddStaticContent;

    // String locators
    String loader = "//div[contains(@class,'ng-busy')]";
    String name = "//input[@id='service-node-props-name']";


    @FindBy(xpath = "//span[text()='Create Service']/parent::span/parent::button")
    WebElement createGenNextService;

    @FindBy(xpath = "//*[@serviceNodeKey='undefined']")
    WebElement serviceNode;

    @FindBy(xpath = "//input[@id='service-node-props-name']")
    WebElement serviceNameTxt;

    @FindBy(xpath = "//button[@id='service-node-content-edit-static']")
    WebElement StaticCI;

    @FindBy(xpath = "//button[@id='topology-add-cis']")
    WebElement addStaticCI;

    @FindBy(xpath = "(//adapt-table-header-cell//adapt-rx-search)[2]")
    WebElement ciName;

    @FindBy(xpath = "//button[@id='ci-search-add']")
    WebElement addCI;

    @FindBy(xpath = "//button[@id='ci-search-close']")
    WebElement closeCIView;

    @FindBy(xpath = "//fieldset[contains(normalize-space(.), 'Add Static content')]//button[@id='footer-saveclose']")
    WebElement staticContentSaveCloseBtn;
    
    @FindBy(xpath = "//button[@id='unsaved-changes-modal-save']")
    WebElement confirmationSave;

    @FindBy(xpath = "//button[@id='footer-saveclose']")
    WebElement closeFooter;

    @FindBy(xpath = "//button[@id='service-node-saveclose']")
    WebElement closeNode;

    @FindBy(xpath = "//button[@id='topology-decoration-layer-metric']")
    WebElement healthIndicator;

    @FindBy(xpath = "//button[@id='blueprint-node-add-metric-pattern']")
    WebElement addMetric;

    @FindBy(xpath = "(//adapt-rx-radiobutton)[1]")
    WebElement selectFirst;

    @FindBy(xpath = "(//button[contains(@class,'expansion-btn')])")
    WebElement expandButton;

    @FindBy(xpath = "//div[contains(.,'CPU')]/preceding-sibling::div[contains(@class,'at-expansion-cell')]")
    WebElement cpuButton;

    @FindBy(xpath = "//div[contains(.,'Utilization')]/preceding-sibling::div[contains(@class,'at-expansion-cell')]")
    WebElement utilizationButton;

    @FindBy(xpath = "//adapt-row-selection-checkbox")
    WebElement selectUtilizationRow;

    @FindBy(xpath = "//button[@id='selected-metric-add-kpi']")
    WebElement addSelectedKPI;

    @FindBy(xpath = "//button[@id='blueprint-properties-saveclose']")
    WebElement closeBlueprint;

    @FindBy(xpath = "//button[@id='service-node-saveclose']")
    WebElement svcNodeClose;

    @FindBy(xpath = "//div[contains(.,'Memory')]/preceding-sibling::div[contains(@class,'at-expansion-cell')]")
    WebElement memoryButton;

    @FindBy(xpath = "//div[contains(.,'Free memory')]/preceding-sibling::div[contains(@class,'at-expansion-cell')]/button")
    WebElement freeMemoryButton;

    @FindBy(xpath = "//div[contains(.,'Free memory')]/preceding-sibling::div[contains(@class,'at-expansion-cell')]/button/following::adapt-row-selection-checkbox")
    WebElement freeMemoryCheckBox;

    @FindBy(xpath = "//button[@id='dropdownMenu']")
    WebElement svcOptions;

    @FindBy(xpath = "//button[@id='service-edit']")
    WebElement svs_edit;

    @FindBy(xpath = "//button[@id='service-delete']")
    WebElement svs_delete;

    @FindBy(xpath = "//button[@id='confirmation-yes-btn']")
    WebElement confirmationYesBtn;

    @FindBy(xpath = "//button[contains(.,'Delete')]")
    WebElement deleteMetric;

    @FindBy(xpath = "//button[@btn-type='tertiary']/span")
    WebElement alarmPolicy;

    @FindBy(xpath = "//input[@id='txtPolicyName']")
    WebElement policyNameLocator;

    @FindBy(xpath = "//button[@id='instance-type-dropdown-attribute-for0']")
    WebElement multiInstanceDropdown;

    @FindBy(xpath = "//adapt-highlight[contains(text(), 'All Instances')]")
    WebElement allInstanceOption;

    @FindBy(xpath = "//div[@id='if-condition-box-ifThenRow0-if-condition-0']")
    WebElement ifConditionLocator;

    @FindBy(xpath = "//adapt-highlight[contains(text(), 'Above')]")
    WebElement aboveOption;

    @FindBy(xpath = "//input[@name='thresholdValue']")
    WebElement thresholdValue;

    @FindBy(xpath = "//adapt-highlight[contains(text(), 'time')]")
    WebElement timeOption;

    @FindBy(xpath = "//input[@name='durationValue']")
    WebElement durationValue;

    @FindBy(xpath = "//div[@id='then-action-box-ifThenRow0-then-action-0']")
    WebElement generate; // two elements

    @FindBy(xpath = "//adapt-highlight[contains(text(), 'Critical Alarm')]")
    WebElement criticalAlarm;

    @FindBy(xpath = "//adapt-highlight[contains(text(), 'Close alarm')]")
    WebElement closeAlarm;

    @FindBy(xpath = "//adapt-highlight[contains(text(), 'Immediately')]")
    WebElement immediately;

    @FindBy(xpath = "//button[@id='policySaveCloseBtn']")
    WebElement saveAlarm;

    @FindBy(xpath = "//topology-blueprint-ci-search//input[@aria-label='Name Filter']")
    WebElement ciSearchInput;

    @FindBy(xpath = "//topology-blueprint-ci-search/parent::div/following-sibling::div//button[contains(. ,'Add')]")
    WebElement addCIBtn;

    @FindBy(xpath = "//*[@id='service-impact-event-count']")
    WebElement impactingEvents;
    @FindBy(xpath = "//adapt-docked-panel[contains(. ,'Impacting Events')] ")
    WebElement impactingEventsPanel;
    @FindBy(xpath = "//adapt-docked-panel[contains(. ,'Event Details')]")
    WebElement eventDetailsPanel;
    @FindBy(xpath = "//td[@data-testid='adapt-table_column-0_row-0']//*[contains(@class, 'alert-link')]")
    WebElement firstEventMsgLink;
    @FindBy(xpath = "//button[contains(. ,'More Details')]")
    WebElement moreDetailsBtn;
    @FindBy(xpath = "//topology-visualizer[contains(. ,'Edit Service')]")
    WebElement editServiceWindow;
    @FindBy(xpath = "//adapt-rx-radiobutton[contains(. ,'Metrics Applicable for this Service Content')]")
    WebElement metricApplicableForServiceRadioBtn;
    @FindBy(xpath = "//input[@aria-label='Metric Name Filter']")
    WebElement healthIndicatorMetricNameFilter;
    @FindBy(xpath = "//input[@aria-label='Select options']/ancestor::adapt-rx-checkbox")
    WebElement allHostNameChkBox;
    @FindBy(xpath = "//button[contains(. ,'Add Metric')]")
    WebElement addMetricBtn;
    @FindBy(xpath = "//button[contains(. ,'Delete')]")
    WebElement deleteBtn;
    
    @FindBy(xpath = "//span[contains(text(),'Service has been saved successfully')]")
    WebElement serviceSuccessMsg;

    //String constants, below are dynamic element
    String popup_message = "//*[contains(., '%s')]";
    String grid_table_checkboxes = "//adapt-row-selection-checkbox/div";
    String grid_table_column_search_input = "//span[text() = 'CI Name']/ancestor::adapt-table-header-cell//div//input";
    String grid_table_expansion_btns = "//adapt-table[@id='%s']//button[contains(@class,'expansion-btn')]";
    String grid_table_expansion_btn_by_item = "//adapt-table[@id='%s']//div[contains(normalize-space(),'%s')]/ancestor::adapt-table//button[contains(@class,'expansion-btn')]";
    String searchedExpectedNode = "//div[contains(.,'%s')]/preceding-sibling::div/adapt-row-selection-checkbox";
    String metric = "//span[contains(.,'%s')]/ancestor::td/following-sibling::td/adapt-table-actions//button";
    String enablePolicy = "//input[@data-testid='policyStatus']";
    String service_node_rect = "//*[name()=\"g\" and contains(@serviceName,\"%s\")]";
    String add_health_indicator_btn = "//button[@id='topology-decoration-layer-metric']";
    String add_health_indicator_btn_text = "//button[@id='blueprint-node-add-metric-pattern']/span";
    String select_from_opt = "//span[text()='%s']/parent::label/parent::span/input[@type=\"radio\"]";
    String add_metric_btn = "//button[@id=\"selected-metric-add-kpi\"]";
    String ciCheckbox = "//td[contains(. ,'%s')]//*[contains(@class, 'ui-chkbox-box')]";
    String healthIndicatorMetricExpandBtn = "//div[contains(.,'%s')]/preceding-sibling::div[contains(@class,'at-expansion-cell')]//button";
    String healthIndicatorMetricActionsBtn = "//span[contains(.,'%s')]/ancestor::td/following-sibling::td/adapt-table-actions//button";

    String appName = null;
    CreateServicePage aiOpsCIService;

    public AIOpsPage() {
        PageFactory.initElements(getDriver(), this);
        aiOpsCIService = new CreateServicePage();
    }

    public void verifyAIOPSPageLaunchedSuccessfully() {
        appName = commonWaits.waitForElementVisible(bmcAIOPSPage).getText().trim();
        log.info("----- Opened application name - " + appName);
        Assert.assertEquals(appName, "Service Monitoring", "Service Monitoring application is not launched successfully");
    }

    public void verifyAIOPServiceModelingPageLaunchedSuccessfully() {
        appName = commonWaits.waitForElementVisible(bmcAIOPSPage).getText().trim();
        log.info("----- Opened application name - " + appName);
        Assert.assertEquals(appName, "Service Modeling", "Service Modeling application is not launched successfully");
    }

    public int getServiceHealthScore(String serviceName) {
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(serviceNameLocator);
        String service = serviceNameLocator.getText();
        Assert.assertTrue(service.contains(serviceName), "service is incorrect");
        commonWaits.waitForElementVisible(healthScoreValue);
        int healthScore = Integer.parseInt(commonWaits.waitForElementClickable(healthScoreValue).getText());
        return healthScore;
    }

    public void navigateToEntitles() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(entitlesTab);
        clickByAction(entitlesTab);
    }

	public void createServiceUsingUI(String serviceName, String ciName) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), entitlesTab, 10), "Entities tab is not visible, page might not have loaded properly");
        commonWaits.clickWithoutInterception(entitlesTab);
        commonWaits.waitForAllSpinnersToHide("AIOPS");

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createService, 10), "Create Service button is not visible, page might not have loaded properly");
        commonWaits.clickWithoutInterception(createService);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceNode, 10), "Service node is not visible, page might not have loaded properly");
        commonWaits.clickWithoutInterception(serviceNode);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceNameTxt, 10), "Service name text box is not visible, page might not have loaded properly");
        commonWaits.waitForElementClickable(serviceNameTxt).clear();
        serviceNameTxt.sendKeys(serviceName);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), StaticCI, 5), "Static CI button is not visible, page might not have loaded properly");
        commonWaits.clickWithoutInterception(StaticCI);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addStaticCI, 10), "Add Static CI button is not visible, page might not have loaded properly");
        commonWaits.clickWithoutInterception(addStaticCI);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ciSearchInput, 10), "CI search input box is not visible, page might not have loaded properly");
        commonWaits.waitForElementVisible(ciSearchInput).sendKeys(ciName);
        commonWaits.waitForAllSpinnersToHide("AIOPS");

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(ciCheckbox, ciName), 10), "CI checkbox for searched CI is not visible, page might not have loaded properly");
        commonWaits.clickWithoutInterception(By.xpath(String.format(ciCheckbox, ciName)));

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addCIBtn, 5), "Add CI button is not visible, page might not have loaded properly");
        commonWaits.waitForElementClickable(addCIBtn).click();
        commonWaits.waitForAllSpinnersToHide("AIOPS");

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), closeCIView, 5), "Close CI view button is not visible, page might not have loaded properly");
        commonWaits.waitForElementClickable(closeCIView).click();

        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), closeCIView, 5), "Close footer button is not visible, page might not have loaded properly");
        commonWaits.waitForElementClickable(staticContentSaveCloseBtn).click();

        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), staticContentSaveCloseBtn, 5), "Close node button is not visible, page might not have loaded properly");
        commonWaits.waitForElementClickable(closeNode).click();

        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), closeNode, 5), "Close footer button is not visible, page might not have loaded properly");
        commonWaits.waitForElementClickable(closeFooter).click();

        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), closeNode, 5), "Close footer button is not visible, page might not have loaded properly");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createService, 10), "Create Service button is not visible, page might not have loaded properly");
    }
    
	public AIOpsPage navigateToRisks() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(riskTab);
        clickByAction(riskTab);
        return this;
    }

    public void createGenNextSvcWithCI(String svcName, String ciNode) {
        log.info("Creating a Gen Next service by going to AiOps: " + svcName);
        commonWaits.waitForElementClickable(createGenNextService);
        commonWaits.clickWithoutInterception(createGenNextService);
        commonWaits.waitForElementClickable(serviceNode).click();
        commonWaits.waitForElementClickable(serviceNameTxt).clear();
        commonWaits.waitForElementClickable(serviceNameTxt).sendKeys(svcName);
        commonWaits.waitForElementClickable(StaticCI);
        commonWaits.clickWithoutInterception(StaticCI);
        commonWaits.waitForElementVisible(addStaticCI);
        commonWaits.clickWithoutInterception(addStaticCI);
        commonWaits.clickWithoutInterception(ciName);
        commonWaits.sendKeysByAction((ciName), ciNode);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(searchedExpectedNode, ciNode)))).click();
        commonWaits.waitForElementClickable(addCI).click();
        commonWaits.waitForElementClickable(closeCIView).click();
        commonWaits.waitForElementClickable(staticContentSaveCloseBtn);
        commonWaits.clickWithoutInterception(staticContentSaveCloseBtn);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(closeNode);
        commonWaits.clickWithoutInterception(closeNode);
        //** No option but to wait here
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementClickable(closeFooter);
        commonWaits.clickWithoutInterception(closeFooter);
        commonWaits.waitForPageReadyState(getDriver());

    }

    public void waitForEditOptionToLoad() {
        commonWaits.waitForElementClickable(svcOptions);
    }

    public void AddMemoryMetricToGenNextSvc(String svcName) {
        log.info("Adding a memory metric to a service : " + svcName);

        commonWaits.waitForElementClickable(svcOptions);
        commonWaits.clickWithoutInterception(svcOptions);

        commonWaits.waitForElementClickable(svs_edit);
        commonWaits.clickWithoutInterception(svs_edit);

        commonWaits.waitForElementClickable(healthIndicator);
        commonWaits.clickWithoutInterception(healthIndicator);

        commonWaits.waitForElementClickable(serviceNode);
        commonWaits.clickWithoutInterception(serviceNode);

        commonWaits.waitForElementClickable(addMetric);
        commonWaits.clickWithoutInterception(addMetric);

        commonWaits.waitForElementClickable(selectFirst);
        commonWaits.clickWithoutInterception(selectFirst);

        commonWaits.retryingFindClick(expandButton);

        try {
            commonWaits.waitForElementClickable(memoryButton);
            commonWaits.clickWithoutInterception(memoryButton);
        } catch (Exception e) {
            log.error("No Metric named Memory is found for service name" + svcName);
            Assert.fail("No Metric named Memory is found for service name" + svcName);
        }

        commonWaits.waitForElementClickable(freeMemoryButton);
        commonWaits.clickWithoutInterception(freeMemoryButton);

        commonWaits.waitForElementClickable(freeMemoryCheckBox);
        commonWaits.clickWithoutInterception(freeMemoryCheckBox);

        commonWaits.waitForElementClickable(addSelectedKPI);
        commonWaits.clickWithoutInterception(addSelectedKPI);

        commonWaits.waitForElementClickable(closeBlueprint);
        commonWaits.clickWithoutInterception(closeBlueprint);

        commonWaits.waitForElementClickable(svcNodeClose);
        commonWaits.clickWithoutInterception(svcNodeClose);

        commonWaits.waitForElementClickable(closeFooter);
        commonWaits.clickWithoutInterception(closeFooter);

        commonWaits.waitForPageReadyState(getDriver());

    }

    public void removeMetric(String sMetricName) {

        log.info("Removing a memory metric from a service : " + sMetricName);

        commonWaits.waitForElementClickable(svcOptions);
        commonWaits.clickWithoutInterception(svcOptions);

        commonWaits.waitForElementClickable(svs_edit);
        commonWaits.clickWithoutInterception(svs_edit);

        commonWaits.waitForElementClickable(healthIndicator);
        commonWaits.clickWithoutInterception(healthIndicator);

        commonWaits.waitForElementClickable(serviceNode);
        commonWaits.clickWithoutInterception(serviceNode);

        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(metric, sMetricName))));
        commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(metric, sMetricName))));

        commonWaits.waitForElementClickable(deleteMetric);
        commonWaits.clickWithoutInterception(deleteMetric);

        commonWaits.waitForElementClickable(closeNode);
        commonWaits.clickWithoutInterception(closeNode);

        commonWaits.waitForElementClickable(closeFooter);
        commonWaits.clickWithoutInterception(closeFooter);

        commonWaits.waitForPageReadyState(getDriver());
    }

    public void openService(String serviceName) {
        log.info("-----Opening the service :" + serviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createService, 5), "Create Service button is not visible, page might not have loaded properly");
        commonWaits.waitForElementVisible(toggleTilebutton).click();
        commonWaits.waitForElementVisible(searchServiceText).sendKeys(serviceName + Keys.ENTER);
        commonWaits.waitForElementVisible(searchServiceText).clear();
        commonWaits.waitForElementVisible(searchServiceText).sendKeys(serviceName+ Keys.ENTER);
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(firstDisplayedService, serviceName))).click();
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.clickWithoutInterception(AnalyzeProbablecauseTab);
    }

    public String requestAutomationAction() {
        String messageText = commonWaits.waitForElementVisible(message).getText();
        actionDropdownMenu.isDisplayed();
        actionDropdownMenu.click();
        requestAutomation.isDisplayed();
        requestAutomation.click();
        requestDropdown.click();
        dropdownItemImmediately.click();
        submitButton.click();
        return messageText;
    }

    public AIOpsPage selectMessageForRunAutomation(String policy_Name) {
        log.info("----selecting PCA message /Event from Probable Root Casuse section--------");
        boolean flag = false;

        try {
            commonWaits.waitForVisibleElement(casualNode);
            flag = true;
        } catch (Exception ignored) {
            log.info("Causal node is not visible");
        }
        if (!flag) {
            commonWaits.waitForElementClickable(AnalyzeProbablecauseTab).click();
        }
        commonWaits.waitForElementClickable(automationLink).click();
        WebElement policyName = findElement(By.xpath(this.policyNameWebElement.replace("$policyName", policy_Name)));
        commonWaits.scrollToSpecificElement(policyName);
        //policyName.isDisplayed();
        runThisAutomation = String.format(runThisAutomation, policy_Name);
        WebElement runThisAutomationButton = commonWaits.waitForElementPresenceLocated(By.xpath(runThisAutomation));
        runThisAutomationButton.click();
        commonWaits.waitForElementClickable(confirmationButton);
        commonWaits.clickToElement(confirmationButton);
        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).perform();
        commonWaits.waitForPageLoad(getDriver());
        return new AIOpsPage();
    }

    public boolean selectCreateIncidentActionForFirstEventInAiopsService() {
        log.info("----selecting PCA message /Event from Probable Root Casuse section--------");
        boolean flag = false;

        try {
            commonWaits.waitForVisibleElement(casualNode);
            flag = true;
        } catch (Exception ignored) {
            log.info("Causal node is not visible");
        }
        if (!flag) {
            commonWaits.waitForElementClickable(AnalyzeProbablecauseTab).click();
        }
        commonWaits.waitForElementVisible(actionDropdownMenuOnProbableCausePage);
        commonWaits.scrollToSpecificElement(actionDropdownMenuOnProbableCausePage);
        commonWaits.waitForElementClickable(actionDropdownMenuOnProbableCausePage).click();
        commonWaits.waitForElementClickable(actionDropdownMenuOnProbableCausePage).click();
        commonWaits.waitForSometime(1000); 
        // need above waits as the operation needs to sync for create incident button to be displayed
        commonWaits.waitForElementClickable(actionDropdownMenuOnProbableCausePage).click();
        commonWaits.waitForSometime(3000);
         // need above waits as the operation needs to sync for create incident button to be displayed
        commonWaits.waitForElementClickable(actionDropdownMenuOnProbableCausePage).click();
        commonWaits.waitForElementClickable(actionDropdownMenuOnProbableCausePage).click();
        try {
            commonWaits.waitForElementVisible(createIncidentButton).click();
        } catch (Exception e) {
            log.error("Create Incident button is not visible");
            return false;
        }
        commonWaits.waitForElementClickable(yesButtonCreatIncident).click();
        commonWaits.waitForPageLoad(getDriver());
        return true;
    }

    public AIOpsPage refreshBrowserOnEditServicePage() {
        getDriver().navigate().refresh();
        commonWaits.waitForElementVisible(AnalyzeProbablecauseTab, commonWaits.defaultTimeout);
        return new AIOpsPage();
    }

    public String getAutomationPolicyStatus(String policy_Name) {

        boolean flag = false;
        try {
            commonWaits.waitForVisibleElement(casualNode);
            flag = true;
        } catch (Exception ignored) {
            ;
        }


        if (!flag) {
            commonWaits.waitForElementClickable(AnalyzeProbablecauseTab).click();
        }
        //this.automationLink.isDisplayed();
        commonWaits.waitForElementClickable(automationLink).click();
        WebElement policyName = findElement(By.xpath(this.policyNameWebElement.replace("$policyName", policy_Name)));
        commonWaits.scrollToSpecificElement(policyName);
        //policyName.isDisplayed();
        policyStatus = String.format(policyStatus, policy_Name);
        WebElement policyStatusElement = commonWaits.waitForElementPresenceLocated(By.xpath(policyStatus));
        String statusText = policyStatusElement.getText().trim().toLowerCase();
        commonWaits.waitForElementClickable(closeButton).click();
        return statusText;
    }

    public boolean isAutomationPolicyVisible(String policy_Name) {
        boolean flag = false;

        try {
            commonWaits.waitForVisibleElement(casualNode);
            flag = true;
        } catch (Exception ignored) {
            ;
        }

        if (!flag) {
            commonWaits.waitForElementClickable(AnalyzeProbablecauseTab).click();
        }

        if (commonWaits.isElementPresent(automationLink)) {
            automationLink.click();
            try {
                WebElement policyName = findElement(By.xpath(this.policyNameWebElement.replace("$policyName", policy_Name)));
                commonWaits.scrollToSpecificElement(policyName);
                policyName.isDisplayed();
                flag = true;
            } catch (Exception e) {
                flag = false;
                closeButton.click();
            }
        } else {
            flag = false;
        }
        return flag;
    }

    public void navigateToConfigurations() {
        try {
            commonWaits.waitForElementClickable(configurationTab);
            configurationTab.click();
        } catch (Exception e) {
            commonWaits.clickToElement(configurationTab);
        }
    }

    public void navigateToConfigurationsmanageproductfeatures() {
        try {
            commonWaits.waitForElementClickable(manageProductFeatures);
            manageProductFeatures.click();
        } catch (Exception e) {
            commonWaits.clickToElement(manageProductFeatures);
        }
    }

    public AiopsServiceBlueprintPage navigateToConfigurationsManageServiceBlueprints() {
        try {
            commonWaits.waitForElementClickable(manageServiceBlueprints);
            manageServiceBlueprints.click();
        } catch (Exception e) {
            commonWaits.clickToElement(manageServiceBlueprints);
        }
        return new AiopsServiceBlueprintPage();
    }

    public void configureIAPermission() {
        this.navigateToConfigurations();
        this.navigateToConfigurationsmanageproductfeatures();
        try {
            String isPermissionEnabled = intelligentAutomationPermission.getAttribute("aria-checked");
            if (isPermissionEnabled.equalsIgnoreCase("false")) {
                intelligentAutomationPermission.click();
                commonWaits.clickToElement(closeBtn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHealthScore() {
        final long MAX_WAIT_MILLIS = 120000;      // 2 minutes
        final long POLL_INTERVAL_MILLIS = 10000;   // 10 seconds
        long startTime = System.currentTimeMillis();

        // Step 1: Get the initial score
        commonWaits.waitForElementVisible(healthScoreValue, commonWaits.defaultTimeout);
        String initialScoreStr = healthScoreValue.getText().trim();
        int initialScore = Integer.parseInt(initialScoreStr);
        log.info("Initial health score: " + initialScore);

        // Step 2: Poll for up to 2 minutes for a change
        while (System.currentTimeMillis() - startTime < MAX_WAIT_MILLIS) {
            commonWaits.waitForSometime(POLL_INTERVAL_MILLIS);

            // Click refresh and re-read the value
            commonWaits.waitForElementVisible(serviceRefreshButton).click();
            commonWaits.waitForElementVisible(healthScoreValue, commonWaits.defaultTimeout);
            String newScoreStr = healthScoreValue.getText().trim();
            int newScore = Integer.parseInt(newScoreStr);

            // If score has changed, return it
            if (newScore != initialScore) {
                log.info("Health score changed to: " + newScore);
                return newScore;
            }
        }

        // If the score didn't change within 2 minutes, return (or throw/return -1, as needed)
        log.info("Health score did not change within 2 minutes. Returning initial score: " + initialScore);
        return initialScore; // or return -1 if you want to indicate no change

    }

    public void verifyService(String expected) {
        boolean actual = aiopsServiceEntitiesSearchByText(expected);
        Assert.assertTrue(actual, "Service is not created.");
    }

    public boolean aiopsServiceEntitiesSearchByText(String serviceName) {
        boolean flag = false;
        log.info("------- search the entities by search_text-------");
        commonWaits.waitForElementVisible(createService);
        commonWaits.waitForElementClickable(toggleTilebutton).click();
        commonWaits.waitForElementVisible(searchServiceText);
        commonWaits.waitForElementClickable(searchServiceText).clear();
        commonWaits.waitForElementClickable(searchServiceText).sendKeys(serviceName + Keys.ENTER);
        firstDisplayedService = String.format(firstDisplayedService, serviceName);
        commonWaits.waitForByElementToBeVisible(By.xpath(firstDisplayedService));
        List<WebElement> services =commonWaits.findElements(By.xpath(firstDisplayedService));
        for (WebElement service : services) {
            if (commonWaits.isElementPresent(service) && service.getText().equals(serviceName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @FindBy(xpath = "//button//div[contains(.,'Similar Situations')]")
    WebElement similarSituations;

    @FindBy(xpath = "(//span/a[contains(@href,'#/situations/situation/')])[1]")
    WebElement firstSimilarSituation;

    @FindBy(xpath = "//span[contains(text(),'Ask HelixGPT')]/ancestor::button")
    WebElement askHelixGPT;

    @FindBy(xpath = "//span[contains(text(),'Best Action Recommendations')]")
    WebElement bestActionRecomended;

//check presence of //span[contains(text(),'Best Action Recommendations')]

    @FindBy(xpath = "//button[contains(text(),'Reoccurring ?')]")
    WebElement reoccurringButton;

    @FindBy(xpath = "//button[@id='timeRangeFilter']")
    WebElement timeRangeFilter;

    @FindBy(xpath = "//li[@id='Last 30 days']")
    WebElement last30Days;

    @FindBy(xpath = "(//section[contains(@data-testid,'helixGPTChatMessage')]//span/p/span)[2]")
    WebElement GPTChatMessage1;

    @FindBy(xpath = "(//section[contains(@data-testid,'helixGPTChatMessage')]//span/p/span)[3]")
    WebElement GPTChatMessage2;

    @FindBy(xpath = "(//section[contains(@data-testid,'helixGPTChatMessage')]//span/p/span)[4]")
    WebElement GPTChatMessage3;

    @FindBy(xpath = "(//div[contains(@class,'situation-summary')]//p)[1]")
    WebElement situationSummary;

    @FindBy(xpath = "//*[@id='service-total-event-count']")
    WebElement totalEventsCount;

    @FindBy(xpath = "//adapt-menu//a//span[contains(text(),'Configurations')]")
    WebElement configurationsTab;

    @FindBy(xpath = "//adapt-rx-switch//input[@data-testid='root-cause-isolation_input']")
    WebElement rootCauseIsolation;

    @FindBy(xpath = "//adapt-rx-switch//input[@data-testid='ml-based-situations_input']")
    WebElement MlBasedSituationFlag;

    @FindBy(xpath = "//adapt-dialog//div[@id='featureStatusMessage']")
    WebElement sConfirmDialog;

    @FindBy(xpath = "//button[contains(.,'Confirm') or contains(.,'Yes') or contains(.,'Ok')]")
    WebElement sConfirmButton;

    String sHelixGptOutput = "//p/span[contains(text(),'%s')]";


    public void showLast30DaysData() {
        commonWaits.clickWithoutInterception(timeRangeFilter);
        commonWaits.clickWithoutInterception(last30Days);
        commonWaits.waitForPageReadyState(getDriver());
        log.info("Last 30 days data is displayed");
    }

    public void openSimilarSituation() {
        commonWaits.waitForElementClickable(similarSituations).click();
        commonWaits.waitForElementClickable(firstSimilarSituation).click();
        commonWaits.waitForElementVisible(askHelixGPT);
    }

    public void verifyHelixGPTMessage(String context, String expected) {
        String situationSummaryTxt = commonWaits.waitForElementVisible(situationSummary).getText().trim();
        log.info("HelixGPT Situation Summary is: " + situationSummaryTxt);

        commonWaits.waitForElementClickable(askHelixGPT).click();
        commonWaits.waitForElementVisible(bestActionRecomended);
        commonWaits.waitForElementClickable(reoccurringButton).click();
        String output = "";
        WebElement element = null;

        boolean flag = false;

        long startTime = System.currentTimeMillis(); // Get the current time in milliseconds
        long twoMinutesInMillis = 3 * 60 * 1000;

        while (System.currentTimeMillis() - startTime < twoMinutesInMillis) {
            try {
                element = commonWaits.waitForElementVisible(GPTChatMessage1);//commonWaits.waitForElementVisible(findElement(By.xpath(String.format(sHelixGptOutput, message))));
                flag = true;
                break;
            } catch (Exception ignored) {
                ;
            }
        }

        if (!flag) {
            log.error("Even after waiting for 2 min, HelixGPT output not found");
            Assert.fail("Even after waiting for 2 min, HelixGPT output not found");
        } else {
            output = GPTChatMessage1.getText() + " " + GPTChatMessage2.getText() + " " + GPTChatMessage3.getText();
            log.info("HelixGPT output found: " + output);
            DeepEval deepEval = new DeepEval();
            deepEval.getDeepEvalScore(context, situationSummaryTxt, expected, output);
            String hallucinationScore = deepEval.getHallucinationScore();
            String relevanceScore = deepEval.getRelevancyScore();
            log.info("HelixGPT Hallucination Score: " + hallucinationScore);
            log.info("HelixGPT Relevance Score: " + relevanceScore);
            double hallucination = Double.parseDouble(hallucinationScore);
            double relevance = Double.parseDouble(relevanceScore);
            Assert.assertTrue(hallucination < 0.3, "HelixGPT Hallucination Score is above acceptable limit");
            Assert.assertTrue(relevance > 0.7, "HelixGPT Relevance Score is below acceptable limit");
        }

    }


    public SituationsPage navigateToSituationsPage() {
        try {
            commonWaits.waitForElementClickable((situationsTab));
            situationsTab.click();
        } catch (Exception e) {
            commonWaits.clickToElement(situationsTab);
        }
        return new SituationsPage();
    }


    public void clickOnEventFirstEvent() {
        log.info("clicking on first event in PCA section");
        commonWaits.waitForElementClickable(firstEvent);
        clickToElement(firstEvent);
    }

    public void openImpactedEvents() {
        commonWaits.waitForElementVisible(impactingEvents).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), impactingEventsPanel, 5), "Panel not opened");
    }

    public String openFirstEventFromEventsPanel() {
        commonWaits.waitForElementVisible(firstEventMsgLink).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), eventDetailsPanel, 5), "Panel not opened");
        return firstEventMsgLink.getText().trim();
    }

    public void crossLaunchEventFromEventDetailsPanel() {
        commonWaits.waitForElementVisible(moreDetailsBtn, 10);
        commonWaits.clickWithoutInterception(moreDetailsBtn);
        commonWaits.switchToLastWindow();
    }

    public SituationsPage clickOnSituation(String situationName) {
        log.info("clicking on Situation from Analyze Situation section");
        commonWaits.waitForElementVisible(serviceRefreshButton).click();
        commonWaits.waitForElementClickable(situationSection);
        clickToElement(situationSection);
        commonWaits.waitForElementClickable(String.format(situationName, serviceName), commonWaits.defaultTimeout);
        click(String.format(situationName, serviceName));
        return new SituationsPage();
    }


    public String verifyEventEditDetails() {
        log.info("verify all Event details options");
        Assert.assertTrue(commonWaits.isElementPresent(eventDetailsTitle), "event not opened in right panel");
        Assert.assertTrue(commonWaits.isElementPresent(status), "status label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(eventDetailsPriority), "event details priority label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(eventStatus), "event status label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(moreDetails), "more details label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(assignedTo), "assigned label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(summary), "summary label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(className), "class name label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(incidentId), "incident Id label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(object), "object label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(host), "host label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(objectClass), "object class label is incorrect");
        Assert.assertTrue(commonWaits.isElementPresent(eventScore), "event Score label is incorrect");
        commonWaits.waitForElementVisible(eventDetailsName);
        String eventName = eventDetailsName.getText();
        return eventName;
    }

    public BhomPage clickOnMoreDetailsLink() {
        log.info("clicking on more details link , which will open same event in BHOM page");
        commonWaits.waitForElementClickable(moreDetails).click();

        log.info("-----verify BHOM Event page opened and switch to page: ");
        List<String> browserTabs = new ArrayList<String>(getDriver().getWindowHandles());
        if (browserTabs.size() == 1) {
            Assert.fail("------- event details page not open in BHOM");
        }
        getDriver().switchTo().window(browserTabs.get(1));
        return new BhomPage();
    }

    public void verifyServiceImpactedAndHealthScoreUpdated(String serviceName) {
        commonWaits.waitForElementVisible(serviceNameLocator, 10);
        String service = serviceNameLocator.getText().trim();
        Assert.assertTrue(service.contains(serviceName), "Service name is incorrect");

        log.info("Wait for service to get impacted after sending events");

        // Maximum wait duration in milliseconds (2 minutes)
        final long maxWaitMillis = 120000;
        // Polling interval in milliseconds (customize as needed)
        final long pollIntervalMillis = 3000;

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < maxWaitMillis) {
            refreshBrowser();
            log.info("Checking if Service is impacted...");
            try {
                commonWaits.waitForElementVisible(healthScoreValue);
                int healthScore = Integer.parseInt(commonWaits.waitForElementClickable(healthScoreValue).getText());

                // Check if health score has changed from the initial value (assume 100)
                if (healthScore != SolQAConstants.Health_Score_OK) {
                    Assert.assertTrue(true, "Business Service is impacted as expected. Health score: " + healthScore);
                    return;
                }
            } catch (Exception e) {
                log.warn("Exception while checking service status, will retry: " + e.getMessage());
            }

            // Wait before the next poll using your custom waits method
            commonWaits.waitForSometime(pollIntervalMillis);
        }

        // At this point, 2 minutes have passed, health score did not change
        Assert.fail("Health score did not change from " + SolQAConstants.Health_Score_OK + " within 2 minutes");
    }

    public int getTotalEventsCount() {
        serviceRefreshButton.click();
        commonWaits.waitForAllSpinnersToHide("BHOM");
        String totalEvents = commonWaits.waitForElementVisible(totalEventsCount).getText().trim();
        return Integer.parseInt(totalEvents);
    }

    public void verifyEventOptions() {
        SoftAssert softAssert = new SoftAssert();

        boolean flag = false;
        try {
            commonWaits.waitForVisibleElement(casualNode);
            flag = true;
        } catch (Exception ignored) {
            ;
        }

        if (!flag) {
            commonWaits.waitForElementClickable(AnalyzeProbablecauseTab).click();
        }
        commonWaits.waitForElementClickable(eventsButton).click();
        commonWaits.waitForElementClickable(actionDropdownMenu).click();
        for (String item : eventOptionList) {
            softAssert.assertTrue(commonWaits.isElementPresent(String.format(optionsLocators, item)), "Option is not available: " + item);
        }
        softAssert.assertAll();
    }

    public List<String> aiopsDefineserviceAddStaticContentByInputfile(String cisFilename, String blockToProcess, boolean includeRelated, String randomStr) throws IOException {
        log.info("Click the add static content button");
        commonWaits.waitForElementClickable(serviceAddStaticContent);
        clickByAction(serviceAddStaticContent);
        aiOpsCIService.clickAddCIsButton();

        List<String> tooltipDataList = new ArrayList<>();

        if (cisFilename != null) {
            log.info("Add CIs from file");
            aiopsAddCisFromFile(cisFilename, blockToProcess, includeRelated, randomStr);

            log.info("Close the add CI button");
            aiOpsCIService.clickCloseButton();
        }

        log.info("Save and close");
        aiOpsCIService.aiopsBlueprintClickSavencloseBtn();
        aiOpsCIService.aiopsDefineserviceClickSavencloseService();

        return tooltipDataList;
    }

    public void aiopsAddCisFromFile(String inputFilename, String blockToProcess, boolean includeRelated, String randomStr) throws IOException {
        log.info("Get nodes and links from the input file");
        Map<String, Object>[] result = getNodesGraphInputsFromFile(inputFilename, blockToProcess, randomStr);
        Map<String, Object> nodes = (Map<String, Object>) result[0];
        Map<String, Object> links = (Map<String, Object>) result[1];

        log.info("Calculate total number of nodes");
        int allNodes = nodes.size() + 1;

        log.info("Process each node");
        for (int index = 1; index < allNodes; index++) {
            String nodeKey = "node" + index;
            Map<String, Object> node = (Map<String, Object>) nodes.get(nodeKey);

            if (node != null) {
                aiopsBlueprintAddCiInBlueprint(node, includeRelated);
            }
        }
    }

    private Map<String, Object>[] getNodesGraphInputsFromFile(String jsonFile, String blockToRead, String randomStr) throws IOException, JsonProcessingException {
        Map<String, Object> dataSource = new HashMap<>();

        log.info("Read JSON file");
        Map<String, Object> data = readJsonFile(jsonFile);

        if (data != null) {
            if (!randomStr.isEmpty()) {
                log.info("Replace placeholder with random string");
                String jsonStr = new ObjectMapper().writeValueAsString(data);
                jsonStr = jsonStr.replace("<RANDOM_STR>", randomStr);
                data = new ObjectMapper().readValue(jsonStr, Map.class);
            }

            if ("all".equals(blockToRead)) {
                dataSource = data;
            } else {
                dataSource = (Map<String, Object>) data.get(blockToRead);
            }

            if (dataSource == null) {
                System.out.println("No data to read");
                return new Map[]{new HashMap<>(), new HashMap<>()};
            }
        }

        Map<String, Object> nodes = (Map<String, Object>) dataSource.get("nodes");
        Map<String, Object> links = (Map<String, Object>) dataSource.get("links");

        return new Map[]{nodes, links};
    }

    /**
     * Helper method to read JSON file
     * @param filePath Path to the JSON file
     * @return Map containing the JSON data
     */
    private Map<String, Object> readJsonFile(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(filePath), Map.class);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a CI to the blueprint
     *
     * @param node           The node data containing CI information
     * @param includeRelated Whether to include related CIs
     */
    private void aiopsBlueprintAddCiInBlueprint(Map<String, Object> node, boolean includeRelated) {
        if (node != null) {
            String ciType = (String) node.getOrDefault(SolQAConstants.type, "");
            String ciKind = (String) node.getOrDefault(SolQAConstants.kind, "");
            String ciName = (String) node.getOrDefault(SolQAConstants.ciName, "");
            int ciNameIndex = Integer.valueOf((String) node.getOrDefault(SolQAConstants.ciNameIndex, "0"));
            String ciTypeStr = (String) node.getOrDefault(SolQAConstants.ciType, "");
            int ciTypeStrIndex = Integer.valueOf((String) node.getOrDefault(SolQAConstants.ciTypeIndex, "0"));
            List<Map<String, Object>> ciFilter = (List<Map<String, Object>>) node.get(SolQAConstants.filter);
            CreateServicePage aiOpsCIService = new CreateServicePage();
            log.info("Add CI to blueprint");
            aiOpsCIService.selectCIType(ciType);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
            aiOpsCIService.selectCIKind(ciKind);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

            String columnName = "";
            if (!ciName.isEmpty()) {
                columnName = SolQAConstants.name;
                aiOpsCIService.searchCIByColumn(columnName, ciName, ciNameIndex);
            }
            if (!ciTypeStr.isEmpty()) {
                columnName = SolQAConstants.type;
                aiOpsCIService.searchCIByColumn(columnName, ciTypeStr, ciTypeStrIndex);
            }

            log.info("Handle include related checkbox");
            if (includeRelated) {
                aiOpsCIService.clickAddIncludeRelatedButton(true);
            } else {
                aiOpsCIService.clickAddIncludeRelatedButton(false);
            }

            aiOpsCIService.clickAddButton();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
            aiOpsCIService.clearSearchText(columnName);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        }
    }

    public AIOpsPage clickOnNewService() {
        commonWaits.waitForAllSpinnersToHide("BHOM");
        Assert.assertTrue(commonWaits.waitForPageLoad(getDriver()), "Page did not load properly");
        commonWaits.clickWithoutInterception(createService);
        return new AIOpsPage();
    }

    public AIOpsPage clickOnNewBusinessService(String serviceName) {
        commonWaits.waitForElementClickable(newBusinessService).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(name))).clear();
        findElement(By.xpath(name)).sendKeys(serviceName);
        return new AIOpsPage();
    }

    public static Map<String, String> getArrowDirections() {
        Map<String, String> arrowDirections = new HashMap<>();
        arrowDirections.put("up", "d-icon-angle_up");
        arrowDirections.put("down", "d-icon-angle_down");
        return arrowDirections;
    }

    public void aiopsNewServiceClickServiceNodeByName(String nodeName) {
        String locator = String.format(service_node_rect, nodeName);
        WebElement element = findElement(By.xpath(locator));
        clickByAction(element);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(findElement(By.xpath(add_health_indicator_btn_text))).click();
    }

    public void addHealthIndicator() {
        log.info("Click on Health indicator");
        commonWaits.waitForElementClickable(findElement(By.xpath(add_health_indicator_btn))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    public void selectMetricScope(String metricScope) {
        String xpath = String.format(select_from_opt, metricScope);
        commonWaits.waitForElementClickable(xpath, commonWaits.defaultTimeout);
        commonWaits.click(xpath);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    public void aiopsDefineHealthIndicatorAddMetric(String metricType, String metricNameSearch, String ciName, String monitoredHostname) {
        Map<String, String> gridTablesName = new HashMap<>();
        gridTablesName.put("ci_name", "metric-inventory-cilist");
        gridTablesName.put("metric_type", "metric-inventory-entitytypeid");
        gridTablesName.put("metric_name", "metric-inventory-metricname");
        gridTablesName.put("monitored_host_n_entity_name", "metric-inventory-entityid");
        gridTablesName.put("blueprint_values", "blueprint-values-parent");

        aiopsSearchGridTableFieldValue("CI Name", ciName);
        aiopsExpandGridTableFieldValue(ciName, gridTablesName.get("ci_name"));

        aiopsSearchGridTableFieldValue("Metric Type", metricType);
        aiopsExpandGridTableFieldValue(metricType, gridTablesName.get("metric_type"));
        aiopsSearchGridTableFieldValue("Metric Name", metricNameSearch);
        aiopsExpandGridTableFieldValue(metricNameSearch, gridTablesName.get("metric_name"));

        aiopsSearchGridTableFieldValue("Monitored Hostname", monitoredHostname == null ? ciName : monitoredHostname);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(findElement(By.xpath(grid_table_checkboxes)));
        clickByAction(findElement(By.xpath(grid_table_checkboxes)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.click(add_metric_btn);
    }

    private void aiopsSearchGridTableFieldValue(String fieldName, String searchText) {
        String locator = String.format(grid_table_column_search_input, fieldName);
        commonWaits.sendKeysByAction(findElement(By.xpath(locator)), searchText);
    }

    private void aiopsExpandGridTableFieldValue(String field, String tableName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(grid_table_expansion_btns, tableName))));
        WebElement elem = findElement(By.xpath(String.format(grid_table_expansion_btn_by_item, tableName, field)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        log.info("log-----------------" + elem.getAttribute(SolQAConstants.areaExpanded));
        if ("false".equals(elem.getAttribute(SolQAConstants.areaExpanded))) {
            clickByAction(elem);
        }
    }

    public BhomPage addAlarmPolicy() {
        commonWaits.waitForElementClickable(alarmPolicy);
        clickByAction(alarmPolicy);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        List<String> browserTabs = new ArrayList<String>(getDriver().getWindowHandles());
        if (browserTabs.size() == 1) {
            Assert.fail("------- FAIL --> Crosslaunch did not happen for the product BHOM");
        }
        getDriver().switchTo().window(browserTabs.get(2));
        return new BhomPage();
    }

    public void fillPolicyName(String policyName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

        commonWaits.waitForElementClickable(policyNameLocator);
        typeKeys(policyNameLocator, policyName);
    }

    public void setAlarmGenerationConditions(String threshold, String duration) {
        commonWaits.waitForElementClickable(multiInstanceDropdown);
        clickByAction(multiInstanceDropdown);
        commonWaits.waitForElementClickable(allInstanceOption);
        clickByAction(allInstanceOption);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(ifConditionLocator);
        clickByAction(ifConditionLocator);
        commonWaits.waitForElementClickable(aboveOption);
        clickByAction(aboveOption);
        commonWaits.waitForElementClickable(thresholdValue);
        sendKeysByAction(thresholdValue, threshold);
        thresholdValue.sendKeys(Keys.TAB);

        commonWaits.waitForElementClickable(timeOption);
        clickByAction(timeOption);
        commonWaits.waitForElementClickable(durationValue);
        sendKeysByAction(durationValue, duration);
        durationValue.sendKeys(Keys.TAB);

        commonWaits.waitForElementClickable(generate);
        clickByAction(generate);
        commonWaits.waitForElementClickable(criticalAlarm);
        clickByAction(criticalAlarm);
        commonWaits.waitForElementClickable(closeAlarm);
        clickByAction(closeAlarm);
        commonWaits.waitForElementClickable(immediately);
        clickByAction(immediately);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

        selectAlarmPolicyStatus(By.xpath(enablePolicy), "true");
        commonWaits.waitForElementClickable(saveAlarm);
        clickByAction(saveAlarm);
        verifyPopupMessage();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        List<String> browserTabs = new ArrayList<String>(getDriver().getWindowHandles());
        if (browserTabs.size() == 1) {
            Assert.fail("------- FAIL --> Crosslaunch did not happened");
        }
        getDriver().switchTo().window(browserTabs.get(1));
    }

    public void verifyPopupMessage() {
        log.info("Wait until the success popup is visible");
        Assert.assertTrue(commonWaits.waitForVisibleElement(findElement(By.xpath(String.format(popup_message, SolQAConstants.successMessage)))), "Alarm Policy not saved successfully");
    }

    public String alarmPolicyCheckboxStatus(By checkboxLocator) {
        log.info("Get the 'aria-checked' attribute of the checkbox");
        String ariaChecked = findElement(checkboxLocator).getAttribute(SolQAConstants.areaChecked);
        if ("true".equals(ariaChecked)) {
            return "enabled";
        } else {
            return "disabled";
        }
    }

    public void selectAlarmPolicyStatus(By checkboxLocator, String pStatus) {
        String status = alarmPolicyCheckboxStatus(checkboxLocator);
        if ("enable".equalsIgnoreCase(pStatus)) {
            if ("disabled".equals(status)) {
                findElement(checkboxLocator).click();
            } else {
                System.out.println("policy is already enabled");
            }
        } else {
            if ("enabled".equals(status)) {
                findElement(checkboxLocator).click();
            } else {
                System.out.println("policy is already disabled");
            }
        }
    }

    public void enableEditServiceMode() {
        commonWaits.waitForElementVisible(svcOptions).click();
        commonWaits.waitForElementVisible(svs_edit).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), editServiceWindow, 5), "Edit service mode not enabled");
    }

    public void addHealthIndicatorToService(String metricType, String metricName) {
        commonWaits.waitForElementVisible(healthIndicator).click();
        commonWaits.waitForElementVisible(serviceNode).click();
        commonWaits.waitForElementVisible(addMetric).click();
        commonWaits.waitForElementVisible(metricApplicableForServiceRadioBtn).click();
        commonWaits.clickWithoutInterception(expandButton);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(healthIndicatorMetricExpandBtn, metricType))).click();
        commonWaits.waitForElementVisible(healthIndicatorMetricNameFilter).sendKeys(metricName);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(healthIndicatorMetricExpandBtn, metricName))).click();
        commonWaits.waitForElementVisible(allHostNameChkBox).click();
        commonWaits.waitForElementVisible(addMetricBtn).click();
        commonWaits.waitForElementVisible(closeBlueprint).click();
        commonWaits.waitForElementVisible(svcNodeClose).click();
        commonWaits.waitForElementVisible(closeFooter).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void deleteHealthIndicator(String metricName) {
        commonWaits.waitForElementVisible(healthIndicator).click();
        commonWaits.waitForElementVisible(serviceNode).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(healthIndicatorMetricActionsBtn, metricName))).click();
        commonWaits.waitForElementVisible(deleteBtn).click();
        commonWaits.waitForElementVisible(svcNodeClose).click();
        commonWaits.waitForElementVisible(closeFooter).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void deleteServiceUsingUI() {
        commonWaits.waitForElementVisible(svcOptions).click();
        commonWaits.waitForElementVisible(svs_delete).click();
        commonWaits.clickWithoutInterception(confirmationYesBtn);
        commonWaits.waitForPageLoad(getDriver());
    }

    public AIOpsPage disableMLBasedSituationsFlag() {
        log.info("Disabling ML Based Situations Flag");
        commonWaits.waitForElementClickable(configurationsTab).click();
        commonWaits.waitForElementVisible(manageProductFeatures);
        commonWaits.waitForElementClickable(manageProductFeatures).click();
        String isRCAFlagEnabled = rootCauseIsolation.getAttribute("aria-checked");
        if (isRCAFlagEnabled.equalsIgnoreCase("true")) {
            rootCauseIsolation.click();
            commonWaits.waitForElementVisible(sConfirmDialog);
            sConfirmButton.click();
        }
        String isFlagEnabled = MlBasedSituationFlag.getAttribute("aria-checked");
        if (isFlagEnabled.equalsIgnoreCase("true")) {
            MlBasedSituationFlag.click();
            commonWaits.waitForElementVisible(sConfirmDialog);
            sConfirmButton.click();
        }
        return new AIOpsPage();
    }

    public AIOpsPage enableMLBasedSituationsFlag() {
        log.info("Enabling ML Based Situations Flag");

        commonWaits.waitForElementClickable(configurationsTab).click();
        commonWaits.waitForElementVisible(manageProductFeatures);
        commonWaits.waitForElementClickable(manageProductFeatures).click();
        String isRCAFlagEnabled = rootCauseIsolation.getAttribute("aria-checked");
        if (isRCAFlagEnabled.equalsIgnoreCase("false")) {
            rootCauseIsolation.click();
            commonWaits.waitForElementVisible(sConfirmDialog);
            sConfirmButton.click();
        }
        String isFlagEnabled = MlBasedSituationFlag.getAttribute("aria-checked");
        if (isFlagEnabled.equalsIgnoreCase("false")) {
            MlBasedSituationFlag.click();
            commonWaits.waitForElementVisible(sConfirmDialog);
            sConfirmButton.click();
        }
        return new AIOpsPage();
    }

	public String getRiskCount() {
		// TODO Auto-generated method stub
		  log.info("Return Risk count for result"); 
		 commonWaits.waitForElementVisible(FutureCapacityRisk);
		return  commonWaits.waitForElementVisible(FutureCapacityRisk).getText();
		
	}

	public AIOpsPage createDuplicateService(String serviceName,String CIName) {
		// TODO Auto-generated method stub
		  log.info("Create sErvice if not Exist"); 
		  boolean actual = aiopsServiceEntitiesSearchByText(serviceName);
		  if(!actual)
		  {  navigateToEntitles();
			  createServiceUsingUI(serviceName,CIName);
			 log.info("New Duplicate Service Created");
			
			 
		  }
		
		return this;
	}

    /**
     * Navigate to AIOps dashboard tabs like Dashboard, Assets, Operations, etc.
     * @param tabName the tab name to navigate to (use constants from SolQAConstants)
     * @return AIOpsPage instance for method chaining
     */
    public AIOpsPage navigateToAIOpsTab(String tabName) {
        log.info("Navigating to AIOps tab: " + tabName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            WebElement tabElement;
            
            // Special case for Risks tab which uses existing element
            if (SolQAConstants.AIOPS_RISKS_TAB.equals(tabName)) {
                tabElement = riskTab;
            } else {
                // Use dynamic XPath for other tabs
                String dynamicXPath = String.format(AIOPS_TAB_XPATH_TEMPLATE, tabName);
                tabElement = getDriver().findElement(By.xpath(dynamicXPath));
            }
            
            commonWaits.waitForElementClickable(tabElement);
            tabElement.click();
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully navigated to tab: " + tabName);
        } catch (Exception e) {
            log.error("Failed to navigate to tab: " + tabName + ". Error: " + e.getMessage());
            throw new RuntimeException("Navigation to tab failed: " + tabName, e);
        }
        
        return this;
    }

    /**
     * Navigate to AIOps configuration sub-tabs like Connectors, SLA, Product Features
     * @param subTabName the sub-tab name to navigate to (use constants from SolQAConstants)
     * @return AIOpsPage instance for method chaining
     */
    public AIOpsPage navigateToConfigurationSubTab(String subTabName) {
        log.info("Navigating to AIOps Configuration sub-tab: " + subTabName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            String dynamicXPath = String.format(AIOPS_CONFIGURATION_SUB_TAB_XPATH_TEMPLATE,
                    subTabName.toLowerCase().replace(" ", "-"), subTabName);
            WebElement subTabElement = getDriver().findElement(By.xpath(dynamicXPath));
            
            commonWaits.waitForElementClickable(subTabElement);
            subTabElement.click();
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully navigated to sub-tab: " + subTabName);
        } catch (Exception e) {
            log.error("Failed to navigate to sub-tab: " + subTabName + ". Error: " + e.getMessage());
            throw new RuntimeException("Navigation to sub-tab failed: " + subTabName, e);
        }
        
        return this;
    }

    /**
     * Navigate to AIOps Risk sub-tabs like Vulnerabilities and Change Risk Advisor
     * @param riskSubTabName the risk sub-tab name to navigate to (use constants from SolQAConstants)
     * @return AIOpsPage instance for method chaining
     */
    public AIOpsPage navigateToRiskSubTab(String riskSubTabName) {
        log.info("Navigating to AIOps Risk sub-tab: " + riskSubTabName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            // Dynamic XPath for risk sub-tabs (Vulnerabilities and Change Risk Advisor)
            String dynamicXPath = String.format(AIOPS_RISK_SUB_TAB_XPATH_TEMPLATE, riskSubTabName);
            WebElement riskSubTabElement = getDriver().findElement(By.xpath(dynamicXPath));
            
            commonWaits.waitForElementClickable(riskSubTabElement);
            riskSubTabElement.click();
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully navigated to risk sub-tab: " + riskSubTabName);
        } catch (Exception e) {
            log.error("Failed to navigate to risk sub-tab: " + riskSubTabName + ". Error: " + e.getMessage());
            throw new RuntimeException("Navigation to risk sub-tab failed: " + riskSubTabName, e);
        }
        
        return this;
    }

    /**
     * Enable a product feature if it's not already enabled
     * @param featureName the name of the feature to enable (e.g., "Intelligent Automation")
     * @return AIOpsPage instance for method chaining
     */
    public AIOpsPage enableFeature(String featureName) {
        log.info("Enabling feature: " + featureName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            String featureXPath = String.format(AIOPS_PRODUCT_FEATURE_TOGGLE_XPATH_TEMPLATE, featureName);
            
            WebElement toggleElement = getDriver().findElement(By.xpath(featureXPath));
            
            // Check if the feature is already enabled
            String isChecked = toggleElement.getAttribute("aria-checked");
            
            if ("true".equals(isChecked)) {
                log.info("Feature '" + featureName + "' is already enabled");
            } else {
                log.info("Feature '" + featureName + "' is disabled. Enabling it now...");
                commonWaits.waitForElementClickable(toggleElement);
                toggleElement.click();
                commonWaits.waitForPageLoad(getDriver());
                
                // Verify the feature is now enabled
                String updatedStatus = toggleElement.getAttribute("aria-checked");
                if ("true".equals(updatedStatus)) {
                    log.info("Successfully enabled feature: " + featureName);
                } else {
                    log.error("Failed to enable feature: " + featureName);
                    throw new RuntimeException("Feature toggle did not change state: " + featureName);
                }
            }
        } catch (Exception e) {
            log.error("Failed to enable feature: " + featureName + ". Error: " + e.getMessage());
            throw new RuntimeException("Enable feature failed: " + featureName, e);
        }
        
        return this;
    }

    private static final String AIOPS_VIEW_ALL_BUTTON_XPATH_TEMPLATE = "//div[contains(@class, 'd-flex justify-content-end mt-2')]//a[contains(@href, '/view-all/%s')]";

    public AIOpsPage clickViewAllButton(String viewType) {
        log.info("Clicking on AIOps View All button for view type: " + viewType);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            String dynamicXPath = String.format(AIOPS_VIEW_ALL_BUTTON_XPATH_TEMPLATE, viewType);
            WebElement viewAllButton = getDriver().findElement(By.xpath(dynamicXPath));
            
            commonWaits.waitForElementClickable(viewAllButton);
            viewAllButton.click();
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully clicked View All button for view type: " + viewType);
        } catch (Exception e) {
            log.error("Failed to click View All button for view type: " + viewType + ". Error: " + e.getMessage());
            throw new RuntimeException("View All button click failed: " + viewType, e);
        }
        
        return this;
    }

    public AIOpsPage searchVulnerabilities(String searchTerm) {
        log.info("Searching for vulnerabilities with term: " + searchTerm);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            // Find and clear search input field
            WebElement searchInput = getDriver().findElement(By.xpath(VULNERABILITY_SEARCH_INPUT_XPATH));
            commonWaits.waitForElementVisible(searchInput);
            searchInput.clear();
            
            // Enter search term
            searchInput.sendKeys(searchTerm);
            log.info("Entered search term: " + searchTerm);
            
            // Find and click search button
            WebElement searchButton = getDriver().findElement(By.xpath(VULNERABILITY_SEARCH_BUTTON_XPATH));
            commonWaits.waitForElementClickable(searchButton);
            searchButton.click();
            
            // Wait for search results to load
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully searched for vulnerabilities with term: " + searchTerm);
            
        } catch (Exception e) {
            log.error("Failed to search vulnerabilities with term: " + searchTerm + ". Error: " + e.getMessage());
            throw new RuntimeException("Vulnerability search failed: " + searchTerm, e);
        }
        
        return this;
    }

    public AIOpsPage clickVulnerabilityByName(String vulnerabilityName) {
        log.info("Clicking on vulnerability: " + vulnerabilityName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            String dynamicXPath = String.format(VULNERABILITY_RESULT_LINK_XPATH_TEMPLATE, vulnerabilityName);
            WebElement vulnerabilityLink = getDriver().findElement(By.xpath(dynamicXPath));
            
            commonWaits.waitForElementClickable(vulnerabilityLink);
            vulnerabilityLink.click();
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully clicked on vulnerability: " + vulnerabilityName);
            
        } catch (Exception e) {
            log.error("Failed to click vulnerability: " + vulnerabilityName + ". Error: " + e.getMessage());
            throw new RuntimeException("Vulnerability click failed: " + vulnerabilityName, e);
        }
        
        return this;
    }

    public AIOpsPage clickFirstVulnerabilityFromResults() {
        log.info("Clicking on first vulnerability from search results");
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            WebElement firstVulnerabilityLink = getDriver().findElement(By.xpath(ANY_VULNERABILITY_RESULT_LINK_XPATH));
            
            // Get the vulnerability name for logging
            String vulnerabilityName = firstVulnerabilityLink.getText();
            log.info("Found first vulnerability: " + vulnerabilityName);
            
            commonWaits.waitForElementClickable(firstVulnerabilityLink);
            firstVulnerabilityLink.click();
            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully clicked on first vulnerability: " + vulnerabilityName);
            
        } catch (Exception e) {
            log.error("Failed to click first vulnerability from results. Error: " + e.getMessage());
            throw new RuntimeException("First vulnerability click failed", e);
        }
        
        return this;
    }

    public boolean isVulnerabilitySearchResultDisplayed(String vulnerabilityName) {
        log.info("Checking if vulnerability search result is displayed: " + vulnerabilityName);
        
        try {
            String dynamicXPath = String.format(VULNERABILITY_RESULT_LINK_XPATH_TEMPLATE, vulnerabilityName);
            WebElement vulnerabilityLink = getDriver().findElement(By.xpath(dynamicXPath));
            boolean isDisplayed = vulnerabilityLink.isDisplayed();
            log.info("Vulnerability " + vulnerabilityName + " displayed: " + isDisplayed);
            return isDisplayed;
            
        } catch (Exception e) {
            log.warn("Vulnerability not found in search results: " + vulnerabilityName);
            return false;
        }
    }

    public AIOpsPage selectAssetByName(String assetName) {
        log.info("Selecting asset by name: " + assetName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            String rowXPath = String.format(ASSET_ROW_BY_NAME_XPATH_TEMPLATE, assetName);
            log.info("Using XPath: " + rowXPath);
            WebElement assetRow = getDriver().findElement(By.xpath(rowXPath));
            
            WebElement checkbox = assetRow.findElement(By.xpath(ASSET_CHECKBOX_IN_ROW_XPATH));
            
            commonWaits.waitForElementClickable(checkbox);
            checkbox.click();
            
            log.info("Successfully selected checkbox for asset: " + assetName);
            
        } catch (Exception e) {
            log.error("Failed to select asset: " + assetName + ". Error: " + e.getMessage());
            try {
                List<WebElement> allAssets = getDriver().findElements(By.xpath(ASSET_GRID_ELLIPSIS_DIVS_XPATH));
                log.info("Available assets found:");
                for (WebElement asset : allAssets) {
                    String assetText = asset.getText().trim();
                    if (!assetText.isEmpty()) {
                        log.info("- " + assetText);
                    }
                }
            } catch (Exception ex) {
                log.warn("Could not list available assets for debugging");
            }
            throw new RuntimeException("Asset selection failed: " + assetName, e);
        }
        
        return this;
    }

    public AIOpsPage clickAssetActionMenu(String assetName) {
        log.info("Clicking action menu for asset: " + assetName);
        commonWaits.waitForPageLoad(getDriver());
        
        try {
            String rowXPath = String.format(ASSET_ROW_BY_NAME_XPATH_TEMPLATE, assetName);
            log.info("Using XPath: " + rowXPath);
            WebElement assetRow = getDriver().findElement(By.xpath(rowXPath));
            
            WebElement actionMenu = assetRow.findElement(By.xpath(ASSET_ACTION_MENU_IN_ROW_XPATH));
            
            commonWaits.waitForElementClickable(actionMenu);
            actionMenu.click();
            
            Thread.sleep(1000); // Small wait for popup animation
            log.info("Successfully clicked action menu for asset: " + assetName);
            
        } catch (Exception e) {
            log.error("Failed to click action menu for asset: " + assetName + ". Error: " + e.getMessage());
            throw new RuntimeException("Action menu click failed: " + assetName, e);
        }
        
        return this;
    }

    public AIOpsPage clickPlanAutomation() {
        log.info("Clicking on Plan Automation from dropdown menu");
        
        try {
            WebElement planAutomationLink = getDriver().findElement(By.xpath(PLAN_AUTOMATION_LINK_XPATH));
            
            commonWaits.waitForElementClickable(planAutomationLink);
            planAutomationLink.click();
            commonWaits.waitForPageLoad(getDriver());
            commonWaits.waitForSometime(5000);//Hardwait is neccessory to open new tab and start loading.No other way to wait for new tab to open.
            log.info("Successfully clicked Plan Automation");
            
        } catch (Exception e) {
            log.error("Failed to click Plan Automation. Error: " + e.getMessage());
            throw new RuntimeException("Plan Automation click failed", e);
        }
        
        return this;
    }

    public AIOpsPage clickUpdateStatus() {
        log.info("Clicking on Update Status from dropdown menu");
        
        try {
            WebElement updateStatusLink = getDriver().findElement(By.xpath(UPDATE_STATUS_LINK_XPATH));
            
            commonWaits.waitForElementClickable(updateStatusLink);
            updateStatusLink.click();
            commonWaits.waitForPageLoad(getDriver());
            
            log.info("Successfully clicked Update Status");
            
        } catch (Exception e) {
            log.error("Failed to click Update Status. Error: " + e.getMessage());
            throw new RuntimeException("Update Status click failed", e);
        }
        
        return this;
    }

    public AIOpsPage selectAssetAndPlanAutomation(String assetName) {
        log.info("Selecting asset and opening Plan Automation for: " + assetName);
        
        selectAssetByName(assetName);
        clickAssetActionMenu(assetName);
        clickPlanAutomation();
        
        return this;
    }

    public AIOpsPage selectAssetAndUpdateStatus(String assetName) {
        log.info("Selecting asset and opening Update Status for: " + assetName);
        
        selectAssetByName(assetName);
        clickAssetActionMenu(assetName);
        clickUpdateStatus();
        
        return this;
    }

    public boolean isAssetDisplayedInTable(String assetName) {
        log.info("Checking if asset is displayed in table: " + assetName);
        
        try {
            String rowXPath = String.format(ASSET_ROW_BY_NAME_XPATH_TEMPLATE, assetName);
            WebElement assetRow = getDriver().findElement(By.xpath(rowXPath));
            
            boolean isDisplayed = assetRow.isDisplayed();
            log.info("Asset " + assetName + " displayed: " + isDisplayed);
            return isDisplayed;
            
        } catch (Exception e) {
            log.warn("Asset not found in table: " + assetName);
            return false;
        }
    }

}
