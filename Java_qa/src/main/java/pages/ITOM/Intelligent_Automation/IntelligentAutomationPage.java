package pages.ITOM.Intelligent_Automation;

import framework.BasePage;
import org.apache.commons.io.FileDeleteStrategy;
import org.openqa.selenium.*;
import pages.ITOM.Discovery.DiscoveryPage;
import pages.ITOM.Itsm_Insights.ITSMInsightsPage;
import pages.ITOM.Optimize.OptimizePage;
import util.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntelligentAutomationPage extends BasePage {
    static SolqaLogger log = new SolqaLogger(IntelligentAutomationPage.class.getName());
    CommonWaits commonWaits=new CommonWaits();
    String appName = null;
    CommonMethods commonMethods;

    public String currentDir = System.getProperty("user.dir");
    public String ISConnector = "Innovation Studio";
    public String RestConnector = "Generic REST Connector";
    public static int timeout = 15;
    public static String runningState = "Running";
    Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    //WebElements locators
    @FindBy(xpath = "//*[contains(text(), 'IT Service Management')]/ancestor::app-integrations-tile[contains(. ,'Proactive Service Resolution')]//button[contains(. ,'Configure')]")
    WebElement PSR_Configure_BUTTON;

    @FindBy(xpath = "(//*[contains(text(), 'IT Service Management')]/ancestor::app-integrations-tile[contains(. ,'Proactive Service Resolution')]//label[contains(. ,'Enable')]//input)[1]")
    WebElement PSR_Enable_BUTTON;

    @FindBy(xpath = "//input[@value='HELIX_ITSM']")
    WebElement ITSM_ConnectionType;

    @FindBy(xpath = "//button[contains(. ,'Save')]")
    WebElement SaveButton;

    @FindBy(xpath = "//button[contains(. ,'Enable')]")
    WebElement EnableButton;

    @FindBy(xpath = "(//button[contains(@name, 'Contact_Company')]//*[contains(@class, 'rx-select__search-button-title')])[2]")
    WebElement ITSM_ContactCompanyTypeButton;

    @FindBy(xpath = "(//button[contains(@name, 'Contact_Company')]//*[contains(@class, 'rx-select__search-button-title')])[2]/ancestor::adapt-rx-select/../following-sibling::div//input")
    WebElement ITSM_ContactCompanySearchInput;

    @FindBy(xpath = "(//*[contains(text(), 'IT Service Management')]/ancestor::app-integrations-tile[contains(. ,'Proactive Service Resolution')]//button[contains(. ,'Delete')])[1]")
    WebElement deletePSRMappingButton;

    @FindBy(xpath = "//p[text()='Proactive Service Resolution']//parent::div/../../..//button[contains(.,'Modify')]")
    WebElement modifyButtonOnPSRSolution;
    @FindBy(xpath = "//div[contains(.,'Incident Noise Reduction')]//parent::div[@class='switch']//input")
    WebElement incidentNoiseReductionToogle;
    @FindBy(xpath = "//button[@id='save-integration-button']")
    WebElement savePSRSolution;
    @FindBy(xpath = "//adapt-docked-panel//button[contains(.,'Save')]")
    WebElement savebuttonOnCustomField;
    @FindBy(xpath = "//img[contains(@src,'connector/ts')]")
    List<WebElement> listOfConnectorImgs;
    @FindBy(xpath = "//input[@placeholder='Filter']")
    WebElement userDefinedSearchbox;
    @FindBy(xpath = "//span[text()='Manual']")
    public WebElement manualRadioButton;

    @FindBy(xpath = "//span[text()='Automatic']")
    public WebElement automaticRadioButton;

    @FindBy(xpath = "//*[@id=\"add-automation-button\"]/span")
    WebElement addActionButton;

    @FindBy(xpath = "//*[text()=\"Select\"]")
    WebElement selectButton;

    @FindBy(id = "input-done")
    WebElement inputDoneButton;

    @FindBy(xpath = "//button[contains(.,'Move to draft')]")
    public WebElement unpublish;

    @FindBy(xpath = "//button[contains(.,'Edit')]")
    public WebElement edit;

    @FindBy(xpath = "//button[contains(.,'Copy')]")
    public WebElement copy;

    @FindBy(xpath = "//button[contains(.,'Publish')]")
    public WebElement publish;

    @FindBy(xpath = "(//span[contains(.,'Delete')]/../parent::button[@btn-type='warning'])[1]")
    public WebElement delete;

    public String selectOptionFromSearchDropdown = "//button[contains(.,'%s')]";

    @FindBy(xpath = "//a[(text()=' Sync Actions ')]")
    WebElement syncActionButton;
    @FindBy(xpath = "//span[contains(@class,'btn-content') and normalize-space()='View actions']/parent::button")
    WebElement viewActionsButton;
    @FindBy(xpath = "//adapt-rx-switch//span[normalize-space()='Search Online']/ancestor::label")
    WebElement searchOnlineToggleLabel;
    
    @FindBy(xpath = "//adapt-rx-switch//span[normalize-space()='Search Online']/ancestor::label//input[@role='switch']")
    WebElement searchOnlineToggleInput;
    @FindBy(xpath = "//div[@id='select-action']//a[contains(@class,'sync-action-link')]")
    WebElement syncActionsLink;
    @FindBy(xpath = "//div[@id='mapping-blackout-policy']//span[.='Modify']/../parent::button[@id='connection-button']")
    WebElement modifyButtonOnConnection;
    @FindBy(xpath = "//button[contains(.,'Add Policy Field')]")
    public WebElement addPolicyFieldbutton;
    @FindBy(xpath = "//input[@name='isFieldRequired']")
    public WebElement requiredCheckbox;

    @FindBy(xpath = "//*[contains(text(),'Action synced successfully.')]")
    WebElement actionSyncMessage;

    @FindBy(xpath = "//*[contains(normalize-space(),'Action synced successfully')]")
    List<WebElement> actionSyncedSuccessfullyMessages;

    @FindBy(xpath = "//adapt-toast[contains(@class,'p-toast') and contains(@class,'a-toast-wrapper')]")
    List<WebElement> syncSuccessToastMessages;

    @FindBy(xpath = "//div[@id='select-action']//label[contains(@class,'action-name')]//span")
    List<WebElement> actionNameElements;

    @FindBy(xpath = "//input[@placeholder='Search actions']")
    WebElement searchActionButton;

    @FindBy(xpath = "//input[@id='tag-value+0']")
    public WebElement addValue;

    @FindBy(xpath = "//input[@id='ticket-switcher']")
    WebElement changeEnableButton;

    @FindBy(xpath = "//*[@id='rx-switch-1']")
    WebElement enablePSR;

    @FindBy(xpath = "//p[contains(.,'Proactive Service Resolution')]//parent::div/..//input[contains(@id,'rx-switch')]")
    WebElement psrSolutionButton;

    @FindBy(xpath = "//adapt-highlight[text()='sourceNodeID']")
    public WebElement triggerConditionNumber;

    @FindBy(xpath = "//adapt-highlight[text()='\"\"']")
    public WebElement emptyValue;

    @FindBy(xpath = "//adapt-highlight[text()='source_address']")
    public WebElement sourceAddress;

    @FindBy(xpath = "//adapt-highlight[text()='AND']")
    public WebElement andCondition;

    @FindBy(xpath = "(//button[@id='dropdownMenu'])[1]")
    public WebElement dropdownAction;

    @FindBy(xpath = "//table//tbody//tr[contains(@class,'at-row')]")
    List<WebElement> tableRowNum;

    @FindBy(xpath = "//span[contains(.,'Create Plugin Key')]")
    WebElement addPluginKeyButton;

    @FindBy(xpath = "//input[@id='pluginName']")
    WebElement nameTextInput;

    @FindBy(xpath = "//button[contains(.,'Download Plugin Key')]")
    WebElement downloadPluginKeyButton;

    @FindBy(xpath = "//button[contains(.,'Save')]")
    WebElement saveConnectorButton;

    @FindBy(xpath = "(//*[text()='Yes'])")
    WebElement SavePluginKeyConfirmationButton;

    @FindBy(xpath = "(//*[text()='Confirm'])")
    WebElement ConfirmButtonOnPolicyUpdate;

    @FindBy(xpath = "//div[contains(@class,'modal-body')]//span")
    WebElement ConfirmationDialogOnPolicyUpdate;

    @FindBy(xpath = "//tbody[contains(@class,'ui-table-tbody')]//a")
    List<WebElement> requests;

    @FindBy(xpath = "//button[contains(text(),'Create Automation Policy')]")
    WebElement createAutomationPolicyButton;

    @FindBy(xpath = "//input[@id='is-active-checkbox']")
    WebElement activeCheckboxInput;

    @FindBy(id = "save-policy-button")
    WebElement savePolicyButton;

    @FindBy(xpath = "//a[contains(@class,'d-icon-sm d-icon-cross pl-2')]")
    WebElement triggerConditionClose;

    @FindBy(xpath = "//a[contains(@class,'d-icon-sm d-icon-cross pl-2')]")
    List<WebElement> triggerConditionCloseList;
    @FindBy(xpath = "//input[@name='timeInput']")
    WebElement timeInputText;
    @FindBy(xpath = "//input[@name='savingsinUSD']")
    WebElement savingsinUSDText;
    @FindBy(xpath = "//table//tbody//tr[1]//td[1]")
    public WebElement policyNameOnPolicyPage;

    @FindBy(xpath = "//input[@name='name']")
    WebElement policyInputText;
    @FindBy(xpath = "//input[contains(@name,'textarea')]")
    WebElement descriptionInput;

    @FindBy(xpath = "//input[@id='criteria-rows-typeahead-input']")
    WebElement triggerConditionTextBox;

    @FindBy(xpath = "//*[text()='No Bracket']")
    WebElement noParenthesisOption;

    @FindBy(xpath = "//adapt-highlight[normalize-space()='$NEW.status']")
    WebElement statusCondition;

    @FindBy(xpath="//adapt-highlight[normalize-space()='$NEW.source_hostname']")
    WebElement sourceHostnameCondition;

    @FindBy(xpath="//div[@id='criteria-rows-typeahead-input_popup']//button[@data-testid='criteria-rows-typeahead-input_item-0']")
    WebElement sourceHostnameValue;

    @FindBy(xpath = "(//*[text()='Equals'])")
    WebElement equalSign;

    @FindBy(xpath = "(//*[text()='Contains'])")
    WebElement containsSign;

    @FindBy(xpath = "//input[contains(@id,'criteria')]")
    WebElement criteria;

    @FindBy(xpath = "//p[text()='Blackout Policy']//parent::div/../../..//button[contains(.,'Modify')]")
    WebElement modifyButtonOnBlackoutPolicy;

    @FindBy(xpath = "//adapt-highlight[normalize-space()='OPEN']")
    WebElement openCondition;

    @FindBy(xpath = "//input[@name='Name']")
    WebElement connectorNameTextInput;

    @FindBy(xpath = "//*[@aria-labelledby='dropdownMenu']/button[contains(text(),'Edit')]")
    WebElement editButton;

    @FindBy(xpath = "(//*[text()=' Delete '])")
    WebElement deleteButton;

    @FindBy(xpath = "(//*[text()='Yes'])")
    WebElement confirmationButton;

    @FindBy(xpath = "//div[contains(@class,'modal-content')]//button[@rx-id='delete-tenant-access-key-btn']")
    WebElement deleteAccesskeyBtn;

    @FindBy(xpath = "//div[@class='ace_content']")
    WebElement JSONFormatTextInput;

    @FindBy(xpath = "(//div[contains(text(),'Workflow executed successfully!')])[1]")
    WebElement workFlowMessage;

    @FindBy(xpath = "//pre[contains(text(),\"MessageId\")]")
    public WebElement messageId;

    @FindBy(xpath = "//input[@name='event data']")
    WebElement eventData;

    @FindBy(xpath = "//input[@name='action id']")
    WebElement actionId;

    @FindBy(xpath = "//span[text()='Input Parameters']")
    WebElement inputParameter;

    @FindBy(xpath = "//div[@role='alert']")
    WebElement requiredMessage;

    @FindBy(xpath = "//span[text()='Download Plugin']")
    WebElement downloadPluginButton;

    @FindBy(xpath = "(//a[text()=' Available Connectors '])")
    WebElement availableConnectorTab;

    @FindBy(xpath = "//span[text()='Refresh']/..")
    WebElement refreshHistoryTable;

    @FindBy(xpath = "//*[@id=\"event-criteria-block\"]//button/span")
    WebElement eventTypeBrowseButton;

    @FindBy(xpath = "//span[text()='Define Event']")
    WebElement defineEventText;

    @FindBy(xpath = "(//span[contains(.,'Paste Event JSON')]/parent::button)[1]")
    WebElement eventJSONOption;

    @FindBy(xpath = "//app-event-modal//button")
    WebElement eventDoneButton;

    @FindBy(name = "Endpoint URL")
    WebElement endPointURLInput;

    @FindBy(name = "Username")
    WebElement usernameInput;

    @FindBy(name = "Password")
    WebElement passwordInput;

    @FindBy(xpath = "//p[contains(.,'TMF-compliant')]//parent::div/..//input[contains(@id,'rx-switch')]")
    WebElement psrTMFSolutionButton;

    @FindBy(xpath = "//app-integrations-tile//p[contains(.,'TMF-compliant')]/..//parent::div/../..//button")
    WebElement configurebuttonForPSRTMF;
    @FindBy(xpath = "//span[.='Set Up']/../parent::button[@id='connection-button']")
    WebElement setUpButton;
    @FindBy(xpath = "//adapt-rx-radiobutton-group//input[contains(@value,'SNOW')]")
    WebElement radioButtonOfServiceNow;
    @FindBy(xpath = "//span[.='Configure']/../parent::button[@id='connection-button']")
    WebElement configureButtonOnConnection;
    @FindBy(xpath = "//b[.='ticketType']//parent::div/../..")
    WebElement ticketTypeButton_onActionConfig;
    @FindBy(xpath = "//b[.='eventSelectorProperty']//parent::div/../..")
    WebElement eventSelectorPropertyButton_onActionConfig;
    @FindBy(xpath = "//div[contains(@class,'adapt-rx-typeahead')]//input[@type='text' and @aria-expanded='false']")
    WebElement customFieldTextbox;
    @FindBy(xpath = "//button[contains(@aria-label,'Remove Incident')]")
    WebElement removeExisitingMapping;
    @FindBy(xpath = "//adapt-rx-typeahead//input[@type='text' and @aria-expanded='true']")
    WebElement mappingTextbox;
    String connectionFields = "//input[@name='%s']";
    @FindBy(xpath = "//p[contains(.,'Blackout Policy')]//parent::div/..//input[contains(@id,'rx-switch')]")
    WebElement blackoutSolutionButton;

    @FindBy(xpath = "//button[@aria-label='Application switcher']")
    WebElement adeNavBar;

    @FindBy(xpath = "//button[contains(text(),'BMC Helix Innovation Suite')]")
    WebElement isButton;

    @FindBy(xpath = "//h1[text()='Workspace']")
    WebElement bmcHelixPage;

    @FindBy(xpath = "//a[@href='#/policy/history']")
    WebElement historyTab;

    @FindBy(xpath = "//a[@href='#/connector/configured-connector']")
    WebElement connectorTab;

    @FindBy(xpath = "//a[@href='#/integrations/psr-integrations']")
    WebElement solutionsTab;

    @FindBy(xpath = "//a[@href='#/policy/policies']")
    WebElement policyTab;

    @FindBy(xpath = "//button[contains(.,'BMC Helix Service Monitoring')]")
    WebElement serviceMonitoringButton;

    @FindBy(xpath = "//button[contains(.,'BMC Helix Operations Management')]")
    WebElement operationsManagementButton;

    @FindBy(xpath = "//button[contains(.,'BMC Helix Discovery')]")
    WebElement discoveryButton;

    @FindBy(xpath = "//button[contains(.,'BMC Helix Continuous Optimization')]")
    WebElement optimizeButton;

    @FindBy(xpath = "//button[contains(.,'BMC Helix ITSM')]")
    WebElement    BMCHelixITSM;


    @FindBy(xpath = "(//a[text()=' Plugin Keys '])")
    WebElement pluginKeysTab;

    @FindBy(xpath = "//a[@href='#/automation/requests']")
    WebElement requestTab;

    @FindBy(xpath = "//span[text()='Automation Policy Runs']")
    WebElement HistoryTableHeading;

    @FindBy(xpath = "//span[text()='Success']")
    WebElement labelSuccessful;

    @FindBy(xpath = "//span[text()='Running']")
    WebElement labelRunning;

    @FindBy(xpath = "//span[text()='Failed']")
    WebElement labelFailed;

    @FindBy(xpath = "//button[contains(.,'Advanced filter')]")
    WebElement buttonAdvancedFilter;

    @FindBy(xpath = "//p[text()='No items available']")
    WebElement noItemMessage;

    @FindBy(xpath = "//span[text()='Refresh']/..")
    WebElement refresh;

    @FindBy(xpath = "//button[contains(.,'active')]")
    WebElement active_Filters_Text;

    @FindBy(xpath = "//a[@href='/dashboards/d/IntelligentAutomation']")
    WebElement reportTab;

    @FindBy(xpath = "//*[text()='Intelligent Automation Value Dashboard']")
    WebElement reportName;

    @FindBy(xpath = "//*[text()='Total Unique Automations']")
    WebElement totalAutomations;

    @FindBy(xpath = "//*[text()='Automation Runs']")
    WebElement automationRuns;

    @FindBy(xpath = "//button[contains(.,'BMC Helix Intelligent Automation')]")
    WebElement iaButton;

    @FindBy(xpath = "//button[contains(.,'Delete')]")
    WebElement deletePluginKeyButton;

    @FindBy(xpath = "//*/button[contains(text(),'Actions')]")
    WebElement dropdownActionMenu;
    @FindBy(id = "test-policy-button")
    public WebElement testPolicyButton;

    @FindBy(xpath = "//button[@id='searchDropdown']")
    WebElement searchdropdownMenu;
    @FindBy(xpath = "//button[contains(.,'Clear')]")
    WebElement clearButton;

    @FindBy(xpath = "//*[text()=\"Search\"]")
    WebElement policyNameSearchIcon;

    @FindBy(xpath = "//adapt-table-toolbar//input")
    WebElement policyNameSearchBox;

    @FindBy(xpath = "//i[@role='button']")
    List<WebElement> listOfInfoButtons;

    String infoButton = "(//i[@role='button'])[%s]";

    String dropDownConnectorButton = "(//i[@role='button'])[%s]/../../../div/div/button";

    @FindBy(xpath = "//table[@class='table']//span[text()='Plugin Key Name']")
    WebElement infoPluginKeyColumn;


    @FindBy(xpath = "//table//*[text()='Plugin Key Name']/../../td/div")
    WebElement infoPluginKeyValue;


    @FindBy(xpath = "(//a[text()=' Configured Connectors '])")
    WebElement configuredConnectorTab;

    @FindBy(id = "dropdownMenu")
    WebElement connectorDropdownMenu;

    @FindBy(xpath = "//button[contains(.,'Disable')]")
    WebElement disableButton;

    @FindBy(xpath = "//button[contains(.,'Enable')]")
    WebElement enableButton;

    @FindBy(xpath = "(//*[text()=' Delete '])")
    WebElement deletebutton;

    @FindBy(xpath = "//table[contains(@class,'ticket-status')]//tr/td[contains(text(),'Ticket Status')]/following-sibling::td")
    WebElement changeRequestTicketStatus;

    @FindBy(xpath = "//input[contains(@placeholder,'Search by using policy name')]")
    WebElement historyPageSearchBox;

    @FindBy(xpath = "//input[@placeholder='Search by Event ID']")
    WebElement historyPageSearchBoxByEventID;

    @FindBy(xpath = "//button[1][text()=' Yes ']")
    public WebElement confirmYesButton;
    //String locators
    String navTab = "//*[text()='%s']/parent::a";
    String ITSM_SetupConnectionBtn = "//*[@class='steps-title-text' and contains(. ,'%s')]/ancestor::div[@class='row']//button";
    String connectionField = "//fieldset[contains(. ,'Setup Connection')]//input[@name='%s']";
    String TicketField = "//*[text()='%s']/ancestor::button";
    String dropdownValue = "//button[@role='option' and contains(. ,'%s')]";

    public String configureConnectorByName = "//h5[contains(text(),'%s')]/following-sibling::button";
    String connectorNameXpath = "//div[@class='configured-connector-tile']//h5[contains(text(),'%s')]";
    String connectorStatusXpath = "(//div[@class='configured-connector-tile']//h5[contains(text(),'%s')]/following-sibling::span)[1]/span";
    String editConnector = "//p[text()='%s']/../preceding-sibling::div/div/button";
    String actionDropDown = "//tbody//tr//td//span[contains(text(),'%s')]/ancestor::tr//button[@id='dropdownMenu']";
    String connectorKebabButton = "//div[@class='configured-connector-tile']//h5[contains(text(),'%s')]/ancestor::div[contains(@class,'card')]//button[@id='dropdownMenu']";
    String connectorImg = "(//img[contains(@src,'connector/ts')])[%s]";
    String changeIDXpath = "//*[contains(text(),'%s')]/../../..//a[@closebtn='true']";
    String iaPageTitle = "(//header//nav//span/span)[2]";

    String awstext =  "//*[text()='%s']";
    private final WebDriver driver;
    private final WebDriverWait wait;
    Actions action;
    String Pluginkeyname;
    //ConnectorPage connectorPage;

    String TitlePageOfIA = "BMC Helix Intelligent Automation";


    public String currentHandle;

    @FindBy(xpath = "//input[@name='ServiceNow Endpoint URL']")
    WebElement snowEndpointUrl;


    @FindBy(xpath = "//input[@name='Username']")
    WebElement snowUserName;


    @FindBy(xpath = "//input[@name='Password']")
    WebElement snowPassword;


    @FindBy(xpath = "//input[@name='Grant Type']")
    WebElement snowGrantType;


    @FindBy(xpath = "//input[@name='Client Id']")
    WebElement snowClientId;


    @FindBy(xpath = "//input[@name='Client Secret']")
    WebElement snowClientSecret;

    @FindBy(xpath = "//input[@id='action-name-input']")
    WebElement sActionName;

    @FindBy(xpath = "//button[@name='restMethodFormField']")
    WebElement sHTTPMethod;


    @FindBy(xpath = "//input[@name='apiPathFormField']")
    WebElement sAPIPath;

    @FindBy(xpath = "//button[contains(@name,'inOptionFormField')]")
    WebElement sType;


    @FindBy(xpath = "(//input[contains(@id,'rx-typeahead')])[2]")
    WebElement sKey;

    @FindBy(xpath = "(//input[contains(@id,'rx-typeahead')])[3]")
    WebElement sValue;

    @FindBy(xpath = "//button[@name='contentTypeOptionFormField']")
    WebElement sContentType;


    @FindBy(xpath = "//textarea[@name='rawJsonTextarea']")
    WebElement sRawJson;

    @FindBy(xpath = "(//button[@role='tab'])[2]")
    WebElement sJsonEditorTab;

    @FindBy(xpath = "//span[contains(text(),'name')]/following-sibling::div/adapt-tag-field")
    WebElement sHoverName;

    @FindBy(xpath = "//span[contains(text(),'name')]/following-sibling::div//input[@type='text']")
    WebElement sNameInput;

    @FindBy(xpath = "//span[contains(text(),'job')]/following-sibling::div//input[@type='text']")
    WebElement sJobInput;

    @FindBy(xpath = "//span[contains(text(),'job')]/following-sibling::div/adapt-tag-field")
    WebElement sHoverJob;

    @FindBy(xpath="//div[contains(@class,'node')][.//span[contains(@class,'key-name') and normalize-space()='name']]   //div[contains(@class,'value-container-actions')]   //span[contains(@class,'d-icon-wrench') and contains(@class,'action-icon')]")
    WebElement addTransformationwrench;

    @FindBy(xpath = "//div[contains(@class,'node')][.//span[contains(@class,'key-name') and normalize-space()='job']]//div[contains(@class,'value-container-actions')]//span[contains(@class,'d-icon-wrench') and contains(@class,'action-icon')]")
    WebElement addJobTransformationwrench;

    @FindBy(xpath = "//span[contains(@class,'d-icon-pencil_adapt') and contains(@class,'action-icon') and contains(@class,'mb-3')]")
    WebElement editTransformationpencil;

    @FindBy(xpath = "((//adapt-tag-field)/following-sibling::span)[1]")
    WebElement sAddTransformations;
    // (//adapt-tag-field)[1]/following-sibling::span[@title='Add transformations']

    @FindBy(xpath = "((//adapt-tag-field)/following-sibling::span)[2]")
    WebElement sAddJobTransformations;

    @FindBy(xpath = "//span[contains(@class,'d-icon-pencil_adapt') or @title='Edit transformations']")
    WebElement sEditTransformations;

    @FindBy(xpath = "//a[@class='add-translation-text']")
    WebElement sAddTrans;

    @FindBy(xpath = "//app-bhom-string-functions//button[contains(text(),'Edit')]")
    WebElement sEditTrans;

    @FindBy(xpath = "//button[contains(text(),'Delete')]")
    WebElement sDeleteTrans;

    @FindBy(xpath = "//button[contains(@name,'transType')]")
    WebElement sTransType;


    @FindBy(xpath = "//button[contains(@name,'functionTag')]")
    WebElement sFunctions;


    @FindBy(xpath = "//button[text()='Save' and contains(@class,'ng-star-inserted')]")
    WebElement sSave;

    @FindBy(xpath="//button[normalize-space()='Save' and not(@id='save-policy-button')]")
    WebElement sSaveVariable;

    @FindBy(xpath = "//button[text()='Update']")
    WebElement sUpdate;

    @FindBy(xpath = "//button[text()='Done']")
    WebElement sDoneSecondary;

    @FindBy(xpath = "//button[contains(text(),'Done')]")
    WebElement sDonePrimary;

    @FindBy(xpath = "//button[contains(@id,'configure')]")
    public WebElement configureAction;

    @FindBy(xpath = "//button[@id='criteria-rows-typeahead-input_popup-$NEW.msg']")
    WebElement messageCondition;

    @FindBy(xpath = "//img[@alt='generic_rest']/ancestor::div[@class='card']//button[@id='dropdownMenu']")
    WebElement drowpdownEditRestConnector;

    @FindBy(xpath = "//img[@alt='generic_rest']/ancestor::div[@class='card']//h5")
    List<WebElement> genericRestCardTitle;

    @FindBy(xpath = "//img[@alt='generic_rest']/ancestor::div[@class='card']//i")
    WebElement informationHover;

    String crossIcon = "//button[contains(@class, 'card-title border')]/span[contains(@class, 'd-icon-cross_circle')]";

    @FindBy(xpath = "//input[@name='apiPathFormField']")
    WebElement apiPath;

    @FindBy(xpath = "//button[@name='inOptionFormField+0']")
    WebElement param;

    @FindBy(xpath = "//adapt-highlight/span[text()='QUERY']")
    WebElement queryParam;

    @FindBy(xpath = "(//input[contains(@id,'typeahead')])[3]")
    WebElement key;

    @FindBy(xpath = "//button[@name='inOptionFormField+1']")
    WebElement param1;

    @FindBy(xpath = "(//input[contains(@id,'typeahead')])[5]")
    WebElement key1;

    @FindBy(xpath = "(//input[contains(@id,'typeahead')])[4]")
    WebElement value1;

    @FindBy(xpath = "(//input[contains(@id,'typeahead')])[6]")
    WebElement value2;

    @FindBy(xpath = "//button[contains(@class, 'pl-0 btn keyboard-focusable')]")
    WebElement addKey;

    @FindBy(xpath = "//adapt-highlight/span[text()='HEADER']")
    WebElement headerParam;

    @FindBy(xpath = "//button[@id='input-done']")
    WebElement doneBtn;

    @FindBy(xpath = "//button[@id='add-variable-button']")
    WebElement addVariablePolicy;

    String addVariable = "//button[contains(@class, 'btn-add-variable')]";

    @FindBy(xpath = "//input[@id='description-input' and @placeholder='Enter variable name']")
    WebElement variableName;

    String variableArrow = "//b[text()='%s']/parent::div//img[@alt='arrow down']";
    
    String variableNameExpand = "//button//div/b[text()='%s']";

    @FindBy(xpath = "//adapt-tag-field//div[text()='Start typing']")
    WebElement variableValueField;

    @FindBy(xpath="//span[text()='$.event.msg']")
    WebElement eventMsgValue;

    @FindBy(xpath="//a[text()=' Configure Text ']")
    WebElement configureText;

    @FindBy(xpath = "//button[text()='Save']")
    WebElement saveBtn;

    @FindBy(xpath = "//button[normalize-space()='Save' and not(@id='save-policy-button')]")
    WebElement saveVariableBtn;

    @FindBy(xpath = "//button[@aria-label='Close']")
    WebElement crossBtn;

    @FindBy(xpath = "//input[contains(@placeholder,'ddMMyyyy')]")
    WebElement outputFormat;

    @FindBy(xpath = "//input[contains(@name,'addDaysToDate')]")
    WebElement DaysToAdd;

    @FindBy(xpath = "//input[contains(@name,'get')]")
    WebElement arrayIndex;

    @FindBy(xpath = "//button[contains(@id,'$NEW.msg')]")
    WebElement newMsg;

    @FindBy(xpath = "(//button[contains(@aria-label,'Remove')])[1]")
    WebElement removeNameValue;

    @FindBy(xpath = "(//button[contains(@aria-label,'Remove')])[2]")
    WebElement removeJobValue;

    @FindBy(xpath = "//a/span[contains(text(),'Launch History')]")
    WebElement launchHistoryTab;

    @FindBy(xpath = "//div[contains(@class,'modal') or contains(@class,'dialog')]//p | //div[contains(@class,'modal') or contains(@class,'dialog')]//span")
    WebElement sConfirmDialog;

    @FindBy(xpath = "//button[contains(.,'Confirm') or contains(.,'Yes') or contains(.,'OK')]")
    WebElement sConfirmButton;

    @FindBy(xpath = "//input[contains(@placeholder,'policy name') or @id='name-input']")
    WebElement sPolicyNameField;

    @FindBy(xpath = "//button[contains(.,'Save') or contains(.,'SAVE')]")
    WebElement sSavePolicyButton;

    @FindBy(xpath = "//button[contains(@data-testid,'selector_button')]")
    WebElement selectCredential;
    @FindBy(xpath ="(//*[text()='ApiKey'])")
    WebElement selectApiKey;
    @FindBy(xpath ="//input[@name='Key Name']")
    WebElement KeyName;
    @FindBy(xpath ="//input[@name='API Key']")
    WebElement APIKey;
    @FindBy(xpath ="//input[@name='Login URL']")
    WebElement LoginURL;
    @FindBy(xpath ="//input[@type='checkbox']")
    WebElement checkbox;
    @FindBy(xpath ="//input[@placeholder='Key']")
    WebElement TSOKey;
    @FindBy(xpath ="//input[@placeholder='Value']")
    WebElement TSOValue1;
    @FindBy(xpath ="//input[@placeholder='Value']")
    WebElement TSOValue2;
    @FindBy(xpath ="//*[contains(button,'+')]")
    WebElement clickplus;


    @FindBy(xpath="//adapt-rx-search//input")
    WebElement pluginSearch;

    @FindBy(xpath = "//*[contains(text(),'Configured Solutions')]/ancestor::button[@role='tab']")
    WebElement sComfiguredSolutionsTab;

    @FindBy(xpath = "//*[contains(text(),'Available Solutions')]/ancestor::button[@role='tab']")
    WebElement sAvailableSolutionsTab;


    @FindBy(xpath="//adapt-modal//button[contains(text(),'Yes')]")
    WebElement pluginKeyDeleteConfirmationButton;

    @FindBy(xpath="//input[@data-testid='temp-source-func-0']")
    WebElement sourceValueFirst;

    @FindBy(xpath="//input[@data-testid='temp-target-func-0']")
    WebElement targetValueFirst;

    @FindBy(xpath="//input[@data-testid='temp-source-func-1']")
    WebElement sourceValueSecond;

    @FindBy(xpath="//input[@data-testid='temp-target-func-1']")
    WebElement targetValueSecond;

    @FindBy(xpath="//button[contains(@class,'add-translation-text') and contains(normalize-space(),'Add new')]")
    WebElement addNewSource;

    String sDropDownValues = "//adapt-highlight/span[text()='%s']";
    String dropdownVariable = "//button[contains(@id, 'search_popup-%s')]";
    String configuretabLocator = "configuredConnectorTab";

    public static String DOWNLOAD_PATH = System.getProperty("user.dir") + File.separator + "target";
    public static String CREDS_PATH = DOWNLOAD_PATH + File.separator + "creds.json";
    public static String PULGIN_PATH = DOWNLOAD_PATH + File.separator + "remote-restapi-plugin.zip";
    public static String PULGIN_PATH_CRDOWNLOAD = PULGIN_PATH + ".crdownload";
    public static String DEST_ENCRYPTION_KEY_PATH = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "IA" + File.separator + "ia_remote_plugins" + File.separator + "encryption_key";
    public static String SOURCE_ENCRYPTION_KEY_PATH = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "IA" + File.separator +"database" +File.separator+"encryption_key";
    public static String SOURCE_SERVER_CERTIFICATE_PATH = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "IA" + File.separator+"servercerts"+File.separator+"tso.pem";
    public static String DEST_SERVER_CERTIFICATE_PATH = System.getProperty("user.dir") + File.separator + "remote-restapi-plugin" + File.separator + "server-certs" + File.separator + "tso.pem";

    String sUsersApiURL="/api/users";
    String xApiKey = "x-api-key";
    String xApiKeyValue = SolQAConstants.REQRES_KEY_VALUE;
    String transTypeValue;
    String functionValue;
    String HTTPMethod = "POST";
    String Header = "HEADER";
    String Json = "JSON";


    public IntelligentAutomationPage() {
        this.driver = getDriver();
        PageFactory.initElements(this.driver, this);
        wait = new WebDriverWait(this.driver, Duration.ofSeconds(60));
        action = new Actions(driver);
        commonMethods = new CommonMethods();
    }

    public void verifyIntelligentAutomationPageLaunchedSuccessfully(){
        commonWaits.waitForElementVisible_withFluentWait(driver, iaPageTitle, commonWaits.defaultTimeout);
        appName = driver.findElement(By.xpath(iaPageTitle)).getText().trim();
        log.info("----- Opened application name - "+ appName);
        Assert.assertEquals(appName, "Intelligent Automation", "Intelligent Automation application is not launched successfully");
    }

    public void setExecutionMode(boolean executionMode) {
        if (executionMode) {
            this.manualRadioButton.click();
        } else {
            this.automaticRadioButton.click();
        }
    }    
    public void configureRESTActionWithArrayTransformations(String rawJson, int iArrayIndex){
        transTypeValue = "Array";
        functionValue = "length";
        String eventDetails = "$.event.details";
        String arrayIndexNumber = String.valueOf(iArrayIndex);//"1";

        log.info("Configuring REST action with transformations");
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(sHTTPMethod).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,HTTPMethod)))).click();
        commonWaits.waitForElementClickable(sAPIPath).sendKeys(sUsersApiURL);
        commonWaits.clickWithoutInterception(sType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Header)))).click();
        commonWaits.waitForElementClickable(sKey).clear();
        commonWaits.waitForElementClickable(sKey).sendKeys(xApiKey);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sValue, 10));
        commonWaits.waitForElementClickable(sValue).click();
        commonWaits.waitForElementClickable(sValue).clear();
        commonWaits.waitForElementClickable(sValue).sendKeys(xApiKeyValue);
        commonWaits.clickWithoutInterception(sContentType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Json)))).click();
        commonWaits.clickWithoutInterception(sRawJson);
        commonWaits.waitForElementClickable(sRawJson).sendKeys(rawJson);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sJsonEditorTab).click();

        //Hover over the tag field to make the add transformations button visible
        commonWaits.scrollToSpecificElement(sHoverName);

        commonWaits.clickWithoutInterception(removeNameValue);

        commonWaits.waitForElementClickable(sNameInput).sendKeys(eventDetails + Keys.TAB);

        commonWaits.clickWithoutInterception(removeJobValue);
        commonWaits.waitForElementClickable(sJobInput).sendKeys(eventDetails + Keys.TAB);

        //HOver over these elements to make them visible
        Actions actions = new Actions(getDriver());
        actions.moveToElement(sHoverName).perform();

        log.info("Add transformations");
        commonWaits.waitForElementVisible(sAddTransformations).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sAddTrans).click();
        commonWaits.waitForElementClickable(sTransType).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,transTypeValue)))).click();
        commonWaits.waitForElementClickable(sFunctions).click();

        try{commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,functionValue)))).click();}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, retrying to click on function value");
            commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(sDropDownValues,functionValue))));
        }

        commonWaits.waitForElementClickable(sSave).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sDoneSecondary).click();
        commonWaits.waitForPageReadyState(getDriver());

        //Hover over the Job field to make the add transformations button visible
        commonWaits.scrollToSpecificElement(sHoverJob);

        Actions actions1 = new Actions(getDriver());

        //HOver over these Job field elements to make them visible
        actions1.moveToElement(sHoverJob).perform();

        log.info("Add transformations");
        commonWaits.waitForElementVisible(sAddJobTransformations).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sAddTrans).click();
        commonWaits.waitForElementClickable(sTransType).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,transTypeValue)))).click();
        commonWaits.waitForElementClickable(sFunctions).click();
        try{commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,"get")))).click();}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, retrying to click on function value");
            commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(sDropDownValues,"get"))));
        }
        commonWaits.waitForElementClickable(arrayIndex).sendKeys(arrayIndexNumber);
        commonWaits.waitForElementClickable(sSave).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sDoneSecondary).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary); //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void configureRESTActionWithDateTransformations(String rawJson, int iDays){
        transTypeValue = "Date & Time";
        functionValue = "addDaysToDate";

        log.info("Configuring REST action with transformations");
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(sHTTPMethod).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,HTTPMethod)))).click();
        commonWaits.waitForElementClickable(sAPIPath).sendKeys(sUsersApiURL);
        commonWaits.clickWithoutInterception(sType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Header)))).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sKey, 10));
        commonWaits.clickWithoutInterception(sKey);
        commonWaits.waitForElementClickable(sKey).clear();
        commonWaits.waitForElementClickable(sKey).sendKeys(xApiKey);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sValue, 10));
        commonWaits.clickWithoutInterception(sValue);
        commonWaits.waitForElementClickable(sValue).clear();
        commonWaits.waitForElementClickable(sValue).sendKeys(xApiKeyValue);
        commonWaits.clickWithoutInterception(sContentType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Json)))).click();
        commonWaits.clickWithoutInterception(sRawJson);
        commonWaits.waitForElementClickable(sRawJson).sendKeys(rawJson);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sJsonEditorTab).click();

        commonWaits.jsClickWithoutScroll(addTransformationwrench);

        log.info("Add transformations");

        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.clickWithoutInterception(sAddTrans);
        commonWaits.clickWithoutInterception(sTransType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,transTypeValue)))).click();
        commonWaits.waitForElementClickable(sFunctions).click();

        try{commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,functionValue)))).click();}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, retrying to click on function value");
            commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(sDropDownValues,functionValue))));
        }

        commonWaits.waitForElementClickable(outputFormat).sendKeys("yyyy-MM-dd hh:mm:ss");
        commonWaits.waitForElementClickable(DaysToAdd).sendKeys(String.valueOf(iDays));

        commonWaits.jsClickWithoutScroll(sSave);
        commonWaits.waitForSometime(defaultTimeout);
        commonWaits.retryingFindClick(sDoneSecondary);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void configureRESTActionWithTransformations(String rawJson){
        transTypeValue = "String";
        functionValue = "toUpperCase";

        log.info("Configuring REST action with transformations");
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(sHTTPMethod).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,HTTPMethod)))).click();
        commonWaits.waitForElementClickable(sAPIPath).sendKeys(sUsersApiURL);
        commonWaits.clickWithoutInterception(sType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Header)))).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sKey, 10));
        commonWaits.waitForElementClickable(sKey).clear();
        commonWaits.waitForElementClickable(sKey).sendKeys(xApiKey);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sValue, 10));
        commonWaits.clickWithoutInterception(sValue);
        commonWaits.waitForElementClickable(sValue).clear();
        commonWaits.waitForElementClickable(sValue).sendKeys(xApiKeyValue);
        commonWaits.clickWithoutInterception(sContentType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Json)))).click();
        commonWaits.clickWithoutInterception(sRawJson);
        commonWaits.waitForElementClickable(sRawJson).sendKeys(rawJson);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sJsonEditorTab).click();

        commonWaits.jsClickWithoutScroll(addTransformationwrench);
       
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.clickWithoutInterception(sAddTrans);
        commonWaits.clickWithoutInterception(sTransType);
       
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,transTypeValue)))).click();
        commonWaits.waitForElementClickable(sFunctions).click();

        try{commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,functionValue)))).click();}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, retrying to click on function value");
            commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(sDropDownValues,functionValue))));
        }
        commonWaits.jsClickWithoutScroll(sSave);
        commonWaits.waitForSometime(defaultTimeout);
        commonWaits.retryingFindClick(sDoneSecondary);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void configureRestActionsWithoutTransformation(String rawJson)
    {
        log.info("Configuring REST action with transformations");
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(sHTTPMethod).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,HTTPMethod)))).click();
        commonWaits.waitForElementClickable(sAPIPath).sendKeys(sUsersApiURL);
        commonWaits.clickWithoutInterception(sType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Header)))).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sKey, 10));
        commonWaits.waitForElementClickable(sKey).clear();
        commonWaits.waitForElementClickable(sKey).sendKeys(xApiKey);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sValue, 10));
        commonWaits.clickWithoutInterception(sValue);
        commonWaits.waitForElementClickable(sValue).clear();
        commonWaits.waitForElementClickable(sValue).sendKeys(xApiKeyValue);
        commonWaits.clickWithoutInterception(sContentType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Json)))).click();
        commonWaits.clickWithoutInterception(sRawJson);
        commonWaits.waitForElementClickable(sRawJson).sendKeys(rawJson);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sJsonEditorTab).click();

        commonWaits.retryingFindClick(sDonePrimary);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }


    public void configureRESTActionWithEditorEnumTransformations(String rawJson){

        transTypeValue = "Source value to target value";
       
        log.info("Configuring REST action with transformations");
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(sHTTPMethod).click();
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,HTTPMethod)))).click();
        commonWaits.waitForElementClickable(sAPIPath).sendKeys(sUsersApiURL);
        commonWaits.clickWithoutInterception(sType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Header)))).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sKey, 10));
        commonWaits.waitForElementClickable(sKey).clear();
        commonWaits.waitForElementClickable(sKey).sendKeys(xApiKey);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, sValue, 10));
        commonWaits.clickWithoutInterception(sValue);
        commonWaits.waitForElementClickable(sValue).clear();
        commonWaits.waitForElementClickable(sValue).sendKeys(xApiKeyValue);
        commonWaits.clickWithoutInterception(sContentType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,Json)))).click();
        commonWaits.clickWithoutInterception(sRawJson);
        commonWaits.waitForElementClickable(sRawJson).sendKeys(rawJson);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(sJsonEditorTab).click();

        commonWaits.jsClickWithoutScroll(addJobTransformationwrench);
        commonWaits.waitForPageReadyState(getDriver());

        commonWaits.clickWithoutInterception(sAddTrans);
        commonWaits.clickWithoutInterception(sTransType);

        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,transTypeValue)))).click();

        commonWaits.waitForElementClickable(sourceValueFirst).sendKeys("service is not running");
        commonWaits.waitForElementClickable(targetValueFirst).sendKeys("host is down");

        commonWaits.clickWithoutInterception(addNewSource);

        commonWaits.waitForElementClickable(sourceValueSecond).sendKeys("service is running");
        commonWaits.waitForElementClickable(targetValueSecond).sendKeys("host is up");

        commonWaits.retryingFindClick(sDoneSecondary);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void configureRESTActionWithVariableEnumTransformations(String variableName)
    {
        transTypeValue = "Source value to target value";

        this.clickAddVariable();
        this.fillVariable(variableName);
        this.clickVariableArrowBtn(variableName);
        this.expandVariable(variableName);
        this.FillEventMsgValue();
        commonWaits.clickWithoutInterception(configureText);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.clickWithoutInterception(sAddTrans);
        commonWaits.clickWithoutInterception(sTransType);
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,transTypeValue)))).click();
        commonWaits.waitForElementClickable(sourceValueFirst).sendKeys("service is not running");
        commonWaits.waitForElementClickable(targetValueFirst).sendKeys("host is down");
        commonWaits.clickWithoutInterception(addNewSource);
        commonWaits.waitForElementClickable(sourceValueSecond).sendKeys("service is running");
        commonWaits.waitForElementClickable(targetValueSecond).sendKeys("host is up");
        commonWaits.retryingFindClick(sDoneSecondary);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sSaveVariable);  //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void editRESTActionWithTransformations(String sFunctionToBeUpdated){
        log.info("Editing REST action with transformations");
        commonWaits.waitForElementClickable(configureAction).click();
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementVisible(sActionName);

        commonWaits.jsClickWithoutScroll(editTransformationpencil);
        

        log.info("Edit transformations for name field.");
        commonWaits.clickWithoutInterception(sEditTransformations);
       
        commonWaits.waitForPageReadyState(getDriver());
        
        try{commonWaits.clickWithoutInterception(sFunctions);}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, retrying to click on function dropdown");
            commonWaits.clickWithoutInterception(sFunctions);
        }
        try{commonWaits.waitForElementClickable(findElement(By.xpath(String.format(sDropDownValues,sFunctionToBeUpdated)))).click();}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, retrying to click on function value");
            commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(sDropDownValues,sFunctionToBeUpdated))));
        }
        commonWaits.jsClickWithoutScroll(sUpdate);
       
        commonWaits.waitForSometime(defaultTimeout);
        commonWaits.retryingFindClick(sDoneSecondary); //Let's monitor it in few runs, if it fails we will use method, retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary); //Let's monitor it in few runs, if it fails we will use method, retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void deleteTransformations(){
        log.info("Editing REST action with transformations");
        commonWaits.waitForElementClickable(configureAction).click();
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementVisible(sActionName);

        commonWaits.jsClickWithoutScroll(editTransformationpencil);

        log.info("Edit transformations for name field.");
        commonWaits.waitForElementClickable(sDeleteTrans).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDoneSecondary); //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.retryingFindClick(sDonePrimary); //retryingFindClick
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void configureChangeManagementSlider(boolean check) {
        try {
            commonWaits.waitForPageLoad(this.driver);
            changeEnableButton.isDisplayed();
            String status = changeEnableButton.getAttribute("aria-checked");
            if (status.equalsIgnoreCase("true") && !check) {
                changeEnableButton.click();
            }
            commonWaits.waitForPageLoad(this.driver);
        } catch (Exception ignored) {
        }
    }

    public void selectConnector(String connectorName) {
        commonWaits.waitForPageLoad(driver);
        //commonWaits.waitForSometime(10000);
        //this.wait.until(ExpectedConditions.visibilityOf(this.addActionButton));
        //this.addActionButton.isDisplayed();
        //this.addActionButton.click();
        commonWaits.waitForElementClickable(addActionButton).click();
        commonWaits.waitForPageLoad(driver);
        //commonWaits.waitForSometime(10000);
        WebElement automationTool = driver.findElement(By.xpath("//*[contains(text(),'" + connectorName + "')]"));
        commonWaits.waitForElementClickable(automationTool).click();
        //automationTool.isDisplayed();
        //commonWaits.clickToElement(automationTool);
        commonWaits.waitForPageLoad(driver);
    }

    public void syncAction() {
        syncActionButton.isDisplayed();
        syncActionButton.click();
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.elementToBeClickable(actionSyncMessage));
    }

    public void selectAction(String displayName) {
        try {
            this.searchAction(displayName);
            this.wait.until(ExpectedConditions.visibilityOf(this.selectButton));
            this.selectButton.isDisplayed();
            this.selectButton.click();
            commonWaits.waitForPageLoad(driver);
            commonWaits.explicitWaitVisibilityOfElement(this.inputDoneButton, timeout);
            this.wait.until(ExpectedConditions.visibilityOf(this.inputDoneButton));
            commonWaits.waitForElementClickable(inputDoneButton).click();
            //this.inputDoneButton.isDisplayed();
            //commonWaits.clickToElement(this.inputDoneButton);
            //commonWaits.waitForSometime(5000);
            commonWaits.waitForPageLoad(driver);
        } catch (Exception ignored) {
        }
    }



    public void searchAction(String actionName) {

        commonWaits.waitForPageLoad(driver);
        this.searchActionButton.isDisplayed();
        commonWaits.clickToElement(this.searchActionButton);
        this.searchActionButton.sendKeys(new CharSequence[]{actionName});
        this.searchActionButton.sendKeys(new CharSequence[]{Keys.ENTER});
        commonWaits.waitForPageLoad(driver);
        this.wait.until(ExpectedConditions.visibilityOf(this.searchActionButton));

        try {
            commonWaits.waitForPageLoad(driver);
            Thread.sleep(3000L);
            ((WebElement) this.tableRowNum.get(0)).isDisplayed();
        } catch (Exception ignored) {
        }


    }


    public void switchToWindow(String windowName) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(windowName)) {
                break;
            }
        }
        commonWaits.waitForPageReadyState(driver);
    }


    public void closeWindow(String windowName) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(windowName)) {
                driver.close();
                break;
            }
        }
    }

    public void refreshBrowser() {
        driver.navigate().refresh();
        commonWaits.waitForPageLoad(driver);
        //commonWaits.waitForSometime(5000);
    }

    public void navigateToPolicyPageFromRequestPage(String requestName) {
        commonWaits.waitForPageLoad(driver);
        for (WebElement element : requests) {
            if (element.getText().equalsIgnoreCase(requestName)) {
                action.moveToElement(element).click().perform();
                break;
            }
        }
        commonWaits.waitForPageLoad(driver);
        //commonWaits.waitForSometime(10000);
        wait.until(ExpectedConditions.elementToBeClickable(createAutomationPolicyButton)).click();
        commonWaits.waitForPageLoad(driver);
    }

    public void publishAndSavePolicy() {
        commonWaits.waitForPageLoad(driver);
        commonWaits.scrollToSpecificElement(activeCheckboxInput);
        commonWaits.waitForSometime(2000);
        activeCheckboxInput.isDisplayed();
        activeCheckboxInput.click();
        //commonWaits.waitForElementClickable(activeCheckboxInput).click();
        wait.until(ExpectedConditions.visibilityOf(savePolicyButton));
        commonWaits.explicitWaitVisibilityOfElement(savePolicyButton, timeout);
        savePolicyButton.click();
        commonWaits.waitForPageLoad(driver);
    }


    public void deleteExistingTriggerCondition() {
        int counter = triggerConditionCloseList.size();
        for (int i = 0; i < counter; i++) {
            triggerConditionClose.click();
        }
    }

    public void setEventDetailsTriggerCondition(String sCriteria) {
        commonWaits.waitForElementVisible(triggerConditionTextBox).click();
        commonWaits.waitForElementVisible(noParenthesisOption).click();
        commonWaits.waitForElementClickable(newMsg).click();
        commonWaits.waitForElementVisible(equalSign).click();
        commonWaits.waitForElementVisible(criteria).click();
        commonWaits.waitForElementVisible(criteria).sendKeys(sCriteria + Keys.TAB);
        //this.triggerConditionTextBox.sendKeys(new CharSequence[]{Keys.ESCAPE});
        commonWaits.waitForPageReadyState(driver);
    }



    public void setTriggerCondition() {

        commonWaits.clickWithoutInterception(triggerConditionTextBox);
        commonWaits.clickWithoutInterception(noParenthesisOption);
        commonWaits.waitForPageLoad(driver);
        commonWaits.scrollToSpecificElement(statusCondition);
        commonWaits.waitForElementClickable(statusCondition).click();
        //this.statusCondition.isDisplayed();
        //this.statusCondition.click();
        //commonWaits.waitForSometime(4000);
        commonWaits.waitForElementClickable(equalSign).click();
        //this.equalSign.isDisplayed();
        //this.equalSign.click();
        //commonWaits.waitForSometime(4000);
        commonWaits.waitForElementClickable(openCondition).click();
        //this.openCondition.isDisplayed();
        //openCondition.click();
        this.triggerConditionTextBox.sendKeys(new CharSequence[]{Keys.ESCAPE});
        commonWaits.waitForPageLoad(driver);
        //commonWaits.waitForSometime(4000);

    }

    public void setSourceHostnameTriggerCondition() {
        commonWaits.clickWithoutInterception(triggerConditionTextBox);
        commonWaits.clickWithoutInterception(noParenthesisOption);
        commonWaits.waitForPageLoad(driver);
        commonWaits.scrollToSpecificElement(sourceHostnameCondition);
        commonWaits.waitForElementVisible(sourceHostnameCondition).click();
        commonWaits.waitForElementVisible(equalSign).click();
        commonWaits.waitForElementVisible(sourceHostnameValue).click();
        commonWaits.waitForPageReadyState(driver);
    }


    public String getConnectorStatus(String connectorName) {
        commonWaits.waitForPageLoad(driver);
        connectorStatusXpath = String.format(connectorStatusXpath, connectorName);
        commonWaits.waitForPageLoad(driver);
        String statusText = "";
        String fetchingStatus = "//div[@class='configured-connector-tile']//h5[contains(text(),'%s')]/../div/span";
        fetchingStatus = String.format(fetchingStatus, connectorName);
        int counter = 0;
        while (counter < 25 && commonWaits.isElementPresent(fetchingStatus)) {
            commonWaits.waitForSometime(5000);
            counter++;
        }
        WebElement element = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectorStatusXpath, connectorName)));
        statusText = element.getText();
        return statusText;
    }

    public void deleteConnector(String connectorName) {
        String editButton = "//div[@class='configured-connector-tile']//h5[contains(text(),'%s')]/../preceding-sibling::div/div/button";
        editButton = String.format(editButton, connectorName);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(editButton)));
            element.click();
            commonWaits.waitForPageLoad(driver);
            deleteButton.isDisplayed();
            deleteButton.click();
            commonWaits.waitForPageLoad(driver);
            this.confirmationButton.isDisplayed();
            this.confirmationButton.click();
            commonWaits.waitForPageLoad(driver);
        } catch (Exception e) {
            log.info(" There is no connector to delete with name " + connectorName);
        }

    }

    public void setUserDefinedTagEventWithJsonString(String event) {
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(eventTypeBrowseButton).click();
        commonWaits.waitForElementVisible(defineEventText);
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementClickable(eventJSONOption).click();
        commonWaits.waitForElementClickable(JSONFormatTextInput).click();
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.ENTER});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.BACK_SPACE});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.BACK_SPACE});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.BACK_SPACE});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{event.replace("}]", "")});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.CONTROL, Keys.chord(new CharSequence[]{Keys.SHIFT, Keys.END})});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.chord(new CharSequence[]{Keys.DELETE})});
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementVisible(eventDoneButton).click();
        commonWaits.waitForPageReadyState(driver);
    }

    public void setUserDefinedTagEvent(String eventJson) {
        String event = null;
        commonWaits.waitForElementClickable(eventTypeBrowseButton);
        this.eventTypeBrowseButton.isDisplayed();
        this.eventTypeBrowseButton.click();
        commonWaits.waitForElementVisible(defineEventText);
        this.defineEventText.isDisplayed();
        commonWaits.clickWithoutInterception(eventJSONOption);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, JSONFormatTextInput, 10));
        commonWaits.waitForElementClickable(JSONFormatTextInput).click();
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.ENTER});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.BACK_SPACE});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.BACK_SPACE});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.BACK_SPACE});

        try {
            event = new String(Files.readAllBytes(Paths.get(eventJson)));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        driver.switchTo().activeElement().sendKeys(new CharSequence[]{event.replace("}]", "")});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.CONTROL, Keys.chord(new CharSequence[]{Keys.SHIFT, Keys.END})});
        driver.switchTo().activeElement().sendKeys(new CharSequence[]{Keys.chord(new CharSequence[]{Keys.DELETE})});
        commonWaits.waitForPageLoad(driver);
        this.eventDoneButton.isDisplayed();
        commonWaits.clickToElement(this.eventDoneButton);
    }


    public IntelligentAutomationPage navigateToAutomationPage() throws InterruptedException {
        String currentHandle = driver.getWindowHandle();
        commonWaits.explicitWaitVisibilityOfElement(this.adeNavBar, timeout);
        this.wait.until(ExpectedConditions.visibilityOf(this.adeNavBar));
        this.adeNavBar.isDisplayed();
        commonWaits.clickToElement(this.adeNavBar);
        commonWaits.waitForElementVisible_withFluentWait(driver, this.iaButton, commonWaits.defaultTimeout);
        //this.iaButton.isDisplayed();
        commonWaits.clickToElement(this.iaButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.switchToChildWindow(currentHandle);
        return this;
    }

    public void verifyTitle() throws InterruptedException {
        commonWaits.waitForPageLoad(driver);
        //commonWaits.waitForSometime(4000);
        commonWaits.verifyTitle(this.TitlePageOfIA);
    }




    public void enterInnovationStudioConnectorDetails(String ISConnectorName, String endPointURL, String username, String password) {

        availableConnectorTab.isDisplayed();
        availableConnectorTab.click();
        //ConnectorPage connectorPage = new ConnectorPage();
        configureConnectorByName = String.format(configureConnectorByName, ISConnector);
        WebElement configureElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(configureConnectorByName)));
        commonWaits.clickToElement(configureElement);
        commonWaits.waitForPageLoad(driver);
        this.connectorNameTextInput.isDisplayed();
        this.connectorNameTextInput.clear();
        this.connectorNameTextInput.sendKeys(new CharSequence[]{ISConnectorName});
        this.endPointURLInput.isDisplayed();
        this.endPointURLInput.clear();
        this.endPointURLInput.sendKeys(new CharSequence[]{endPointURL});
        this.usernameInput.isDisplayed();
        this.wait.until(ExpectedConditions.visibilityOf(this.usernameInput));
        //ConfigLoader config = new ConfigLoader();
        this.usernameInput.sendKeys(new CharSequence[]{username});
        this.passwordInput.isDisplayed();
        this.wait.until(ExpectedConditions.visibilityOf(this.passwordInput));
        this.passwordInput.sendKeys(new CharSequence[]{password});
        this.saveConnectorButton.isDisplayed();
        this.saveConnectorButton.click();

    }

    public void navigateToConnectorPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15L));
        wait.until(ExpectedConditions.elementToBeClickable(this.connectorTab));
        this.connectorTab.isDisplayed();
        commonWaits.clickToElement(this.connectorTab);
        commonWaits.waitForPageLoad(driver);
    }

    public void navigateToPoliciesPage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15L));
            commonWaits.waitForPageLoad(driver);
            wait.until(ExpectedConditions.elementToBeClickable(this.policyTab));
            this.policyTab.click();
        } catch (Exception var2) {
            commonWaits.clickToElement(this.policyTab);
        }

        commonWaits.waitForPageLoad(driver);
    }


    public void navigateToHistoryPage() {
        commonWaits.waitForPageLoad(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15L));
        wait.until(ExpectedConditions.elementToBeClickable(this.historyTab));
        commonWaits.clickToElement(this.historyTab);
        commonWaits.waitForPageLoad(driver);
    }


    public void navigateToRequestPage() {
        try {
            commonWaits.waitForPageLoad(driver);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15L));
            wait.until(ExpectedConditions.elementToBeClickable(this.requestTab));
            this.requestTab.click();
            commonWaits.waitForPageLoad(driver);
        } catch (Exception var2) {
            commonWaits.clickToElement(this.requestTab);
        }
    }


    public void navigateToMonitoringPage() {
        String currentHandle = driver.getWindowHandle();
        commonWaits.waitForPageLoad(driver);
        commonWaits.explicitWaitVisibilityOfElement(this.adeNavBar, timeout);
        this.wait.until(ExpectedConditions.visibilityOf(this.adeNavBar));
        this.adeNavBar.isDisplayed();
        commonWaits.clickToElement(this.adeNavBar);
        commonWaits.waitForPageLoad(driver);
        this.serviceMonitoringButton.isDisplayed();
        commonWaits.clickToElement(this.serviceMonitoringButton);
        commonWaits.waitForPageLoad(driver);
        commonWaits.switchToChildWindow(currentHandle);
        commonWaits.waitForPageLoad(driver);
        commonWaits.verifyTitle("BMC Helix Service Monitoring");
    }

    public OptimizePage navigateToOptimizePage() {
        currentHandle = driver.getWindowHandle();
        commonWaits.explicitWaitVisibilityOfElement(this.adeNavBar, timeout);
        this.adeNavBar.isDisplayed();
        commonWaits.clickToElement(this.adeNavBar);
        this.optimizeButton.isDisplayed();
        commonWaits.clickToElement(this.optimizeButton);
        commonWaits.switchToChildWindow(currentHandle);
        return new OptimizePage(getDriver());
    }

    public ITSMInsightsPage navigateToItsmPage() {
        currentHandle = driver.getWindowHandle();
        commonWaits.explicitWaitVisibilityOfElement(this.adeNavBar, timeout);
        this.adeNavBar.isDisplayed();
        commonWaits.clickToElement(this.adeNavBar);
        this.BMCHelixITSM.isDisplayed();
        commonWaits.clickToElement(this.BMCHelixITSM);
        commonWaits.switchToChildWindow(currentHandle);
        return new ITSMInsightsPage();
    }

    public DiscoveryPage navigateToDiscoveryPage() {
        //commonWaits.waitForPageLoad(driver);
        String currentHandle = driver.getWindowHandle();
        commonWaits.waitForPageLoad(driver);
        commonWaits.explicitWaitVisibilityOfElement(this.adeNavBar, timeout);
        this.wait.until(ExpectedConditions.visibilityOf(this.adeNavBar));
        this.adeNavBar.isDisplayed();
        commonWaits.clickToElement(this.adeNavBar);
        commonWaits.waitForPageLoad(driver);
        this.discoveryButton.isDisplayed();
        commonWaits.clickToElement(this.discoveryButton);
        commonWaits.waitForPageLoad(driver);
        commonWaits.switchToChildWindow(currentHandle);
        return new DiscoveryPage();
    }

    public void searchPolicy(String finalpolicyName) {
        try {
            boolean ispresent = clearButton.isDisplayed();
            if (ispresent) {
                clearButton.click();
            }
        } catch (Exception ignored) {
        }
        commonWaits.waitForPageLoad(driver);
        try {
            policyNameSearchIcon.isDisplayed();
            commonWaits.clickToElement(policyNameSearchIcon);
            commonWaits.waitForPageLoad(driver);
            this.policyNameSearchBox.sendKeys(new CharSequence[]{finalpolicyName});
            this.policyNameSearchBox.sendKeys(new CharSequence[]{Keys.ENTER});
            commonWaits.waitForPageLoad(driver);
            this.wait.until(ExpectedConditions.visibilityOf(clearButton));
        } catch (Exception var5) {
        }
        try {
            commonWaits.waitForPageLoad(driver);
            this.wait.until(ExpectedConditions.visibilityOf((WebElement) tableRowNum.get(0)));
            ((WebElement) tableRowNum.get(0)).isDisplayed();
            int rowNum = tableRowNum.size();
            Assert.assertTrue(rowNum>0);
        } catch (Exception ignored) {
        }
    }


    public void actionONSearchPolicy(WebElement actionElement) {
        commonWaits.waitForPageLoad(driver);
        if(commonWaits.isElementPresent(dropdownActionMenu)) {
            this.wait.until(ExpectedConditions.visibilityOf(this.dropdownActionMenu));
            this.dropdownActionMenu.isDisplayed();
            this.dropdownActionMenu.click();
            //commonWaits.waitForSometime(4000);
            this.wait.until(ExpectedConditions.visibilityOf(actionElement));
            this.wait.until(ExpectedConditions.visibilityOf(actionElement));
            commonWaits.explicitWaitElementToBeClickable(actionElement, 3);
            commonWaits.waitForPageLoad(driver);
            actionElement.isDisplayed();
            commonWaits.clickToElement(actionElement);

            try {
                commonWaits.waitForPageLoad(driver);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public void deletePolicy(String policy_name) {
        commonWaits.waitForPageLoad(driver);
        this.actionONSearchPolicy(deletePluginKeyButton);
        commonWaits.waitForElementClickable(confirmYesButton).click();
    }

    public void navigateToOperationsManagementPage() {
        commonWaits.waitForPageLoad(driver);
        String currentHandle = driver.getWindowHandle();
        commonWaits.waitForPageLoad(driver);
        commonWaits.explicitWaitVisibilityOfElement(this.adeNavBar, timeout);
        this.wait.until(ExpectedConditions.visibilityOf(this.adeNavBar));
        this.adeNavBar.isDisplayed();
        commonWaits.clickToElement(this.adeNavBar);
        commonWaits.waitForPageLoad(driver);
        this.operationsManagementButton.isDisplayed();
        commonWaits.clickToElement(this.operationsManagementButton);
        commonWaits.waitForPageLoad(driver);
        commonWaits.switchToChildWindow(currentHandle);
        commonWaits.waitForPageLoad(driver);
        //IAPage iaPage = new IAPage();
        //iaPage.recommendationCloseIcon();
        //commonWaits.waitForSometime(10000);
        commonWaits.verifyTitle("BMC Helix Operations Management");
    }

    public String getConfirmationDialogMessage(){
        wait.until(ExpectedConditions.visibilityOf(this.ConfirmationDialogOnPolicyUpdate));
        ConfirmationDialogOnPolicyUpdate.isDisplayed();
        String confirmationDialogMessage = ConfirmationDialogOnPolicyUpdate.getText().trim();
        //log.info("COnfirmation message is - "+ confirmationDialogMessage);
        return confirmationDialogMessage;
    }


    public String getDescription(){
        descriptionInput.isDisplayed();
        String description = descriptionInput.getAttribute("value");
        //log.info(" Description - "+ descriptionInput.getAttribute("value") );
        return description;
    }

    public void enablePSR(String itsmEndpointURL, String username, String password) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.clickWithoutInterception(By.xpath(String.format(navTab, SolQAConstants.SOLUTIONS_TAB)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.clickWithoutInterception(sAvailableSolutionsTab);
        commonWaits.waitForPageReadyState(getDriver());

        if (commonWaits.isElementPresent(PSR_Configure_BUTTON) && PSR_Configure_BUTTON.isEnabled()) {
            commonWaits.clickWithoutInterception(PSR_Configure_BUTTON);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(By.xpath(String.format(ITSM_SetupConnectionBtn, SolQAConstants.SETUP_CONNECTION_WITH_ITSM)));
            if (!ITSM_ConnectionType.getAttribute("checked").equals("true")) {
                commonWaits.clickWithoutInterception(ITSM_ConnectionType);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            }
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectionField, SolQAConstants.BMC_HELIX_ITSM_ENDPOINT_URL)));
            webElement.click();
            webElement.sendKeys(itsmEndpointURL);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectionField, "Username"))).sendKeys(username);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectionField, "Password"))).sendKeys(password);
            commonWaits.clickWithoutInterception(SaveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            commonWaits.clickWithoutInterception(By.xpath(String.format(ITSM_SetupConnectionBtn, "Configure mapping : Event")));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(By.xpath(String.format(TicketField, "Contact_Company")));
            if (commonWaits.waitForElementVisible(ITSM_ContactCompanyTypeButton).getText().contains("BHOM Field")) {
                ITSM_ContactCompanyTypeButton.click();
                commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownValue, "Custom"))).click();
            }
            commonWaits.waitForElementVisible(ITSM_ContactCompanySearchInput).sendKeys(SolQAConstants.CALBRO_SERVICES + Keys.ENTER);
            commonWaits.waitForElementVisible(ITSM_ContactCompanySearchInput).sendKeys(SolQAConstants.PETRAMCO_COMPANY + Keys.ENTER);
            commonWaits.clickWithoutInterception(SaveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            commonWaits.clickWithoutInterception(By.xpath(String.format(ITSM_SetupConnectionBtn, "Configure mapping : Incident")));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(SaveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(EnableButton);
        } else {
            commonWaits.clickWithoutInterception(sComfiguredSolutionsTab);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            if (PSR_Enable_BUTTON.getAttribute("aria-checked").equals("false")) {
                PSR_Enable_BUTTON.click();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            }
        }
        commonWaits.switchToTabByTitle(SolQAConstants.BMC_HELIX_PORTAL);
    }

    public void enablePSRWithINRFlag(String itsmEndpointURL, String username, String password, boolean enableINR) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.clickWithoutInterception(By.xpath(String.format(navTab, SolQAConstants.SOLUTIONS_TAB)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

        commonWaits.waitForElementVisible(sAvailableSolutionsTab).click();
        commonWaits.waitForPageReadyState(getDriver());


        if (commonWaits.isElementPresent(PSR_Configure_BUTTON)) {
            commonWaits.clickWithoutInterception(PSR_Configure_BUTTON);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            if (enableINR){
                log.info("Enabling incident noise reduction on PSR solution");
                enableIncidentNoiseReduction();
                commonWaits.waitForPageReadyState(getDriver());
            }
            commonWaits.clickWithoutInterception(By.xpath(String.format(ITSM_SetupConnectionBtn, SolQAConstants.SETUP_CONNECTION_WITH_ITSM)));
            if (!ITSM_ConnectionType.getAttribute("checked").equals("true")) {
                commonWaits.clickWithoutInterception(ITSM_ConnectionType);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            }
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectionField, SolQAConstants.BMC_HELIX_ITSM_ENDPOINT_URL)));
            webElement.click();
            webElement.sendKeys(itsmEndpointURL);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectionField, "Username"))).sendKeys(username);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectionField, "Password"))).sendKeys(password);
            commonWaits.clickWithoutInterception(SaveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            commonWaits.clickWithoutInterception(By.xpath(String.format(ITSM_SetupConnectionBtn, "Configure mapping : Event")));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(By.xpath(String.format(TicketField, "Contact_Company")));
            if (commonWaits.waitForElementVisible(ITSM_ContactCompanyTypeButton).getText().contains("BHOM Field")) {
                ITSM_ContactCompanyTypeButton.click();
                commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownValue, "Custom"))).click();
            }
            commonWaits.waitForElementVisible(ITSM_ContactCompanySearchInput).sendKeys(SolQAConstants.PETRAMCO_COMPANY + Keys.ENTER);
            commonWaits.clickWithoutInterception(SaveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            commonWaits.clickWithoutInterception(By.xpath(String.format(ITSM_SetupConnectionBtn, "Configure mapping : Incident")));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(SaveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.clickWithoutInterception(EnableButton);
        }
        else{
            if (PSR_Enable_BUTTON.getAttribute("aria-checked").equals("false")) {
                PSR_Enable_BUTTON.click();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            }
        }
        commonWaits.switchToTabByTitle(SolQAConstants.BMC_HELIX_PORTAL);
    }

    public void removePSRMapping(){
        commonWaits.switchToTabByTitle(SolQAConstants.IA);
        if (PSR_Enable_BUTTON.getAttribute("checked").equals("true")) {
            PSR_Enable_BUTTON.click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        }
        commonWaits.clickWithoutInterception(deletePSRMappingButton);
    }


    public void checkConnectorRadioButtonStatus(WebElement element) {
        try {
            commonWaits.waitForPageReadyState(getDriver());
            Assert.assertTrue(element.isDisplayed(),"Connector radio button is not displayed");
            String status = element.getDomAttribute("aria-checked");
            if (status.equalsIgnoreCase("false")) {
                log.info("User Can Configured one connector");
            }
            commonWaits.waitForPageReadyState(getDriver());
            ;
        } catch (Exception e) {
            log.error("Error while checking connector radio button status: " + e.getMessage());
        }

    }

    public void configureSnowInstanceOnPSRTMF(Boolean isTMF, String endpointURL, String username, String password,String grantTypeValue, String clientIDValue, String clientSecretValue, String isCase, Boolean enableINR){

        Actions action = new Actions(getDriver());

        commonWaits.waitForElementVisible(sAvailableSolutionsTab).click();
        commonWaits.waitForPageReadyState(getDriver());

        if (isTMF){
            log.info("configuring snow instance on PSR TMF solution");
            commonWaits.pureSafeClick(configurebuttonForPSRTMF);
        } else{
            log.info("configuring snow instance on PSR Non-TMF solution");
            commonWaits.waitForElementClickable(PSR_Configure_BUTTON).click();
        }

        if (enableINR){
            log.info("Enabling incident noise reduction on PSR solution");
            enableIncidentNoiseReduction();
            commonWaits.waitForPageReadyState(getDriver());
        }
        log.info("Setup connection details");
        try{commonWaits.waitForElementClickable(setUpButton).click();}
        catch(ElementClickInterceptedException e){
            commonWaits.clickWithoutInterception(setUpButton);
        }

        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForPageReadyState(getDriver());
        log.info("Select ServiceNow as connector type");
        try{commonWaits.waitForElementClickable(radioButtonOfServiceNow).click();}
        catch(ElementClickInterceptedException e){
            log.info("Element click intercepted, trying to click without interception");
            commonWaits.clickWithoutInterception(radioButtonOfServiceNow);
        }catch(TimeoutException e){
            log.info("Timeout while waiting for radio button, trying to move to element and click");
            action.moveToElement(radioButtonOfServiceNow).click().perform();
        }

        log.info("Enter value in text field ");
        commonWaits.waitForElementVisible(snowEndpointUrl).sendKeys(endpointURL);
        commonWaits.waitForElementVisible(snowUserName).sendKeys(username);
        commonWaits.waitForElementVisible(snowPassword).sendKeys(password);
        commonWaits.waitForElementVisible(snowGrantType).sendKeys(grantTypeValue);
        commonWaits.waitForElementVisible(snowClientId).sendKeys(clientIDValue);
        commonWaits.waitForElementVisible(snowClientSecret).sendKeys(clientSecretValue);
        log.info("Click on Save button");
        commonWaits.waitForElementClickable(saveConnectorButton).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForSometime(commonWaits.minDelay);

        commonWaits.waitForElementVisible(configureButtonOnConnection).click();
        commonWaits.waitForPageReadyState(getDriver());

        if(isCase.equalsIgnoreCase("Case")){
            log.info("Adding ticket type as Case");
            commonWaits.waitForElementVisible(ticketTypeButton_onActionConfig).click();
            commonWaits.waitForElementVisible(removeExisitingMapping).click();
            commonWaits.waitForElementVisible(mappingTextbox).clear();
            commonWaits.waitForElementVisible(mappingTextbox).sendKeys(isCase+Keys.ENTER);
        }
        commonWaits.waitForElementClickable(saveConnectorButton).click();

        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForSometime(commonWaits.minDelay);

        commonWaits.waitForElementClickable(configureButtonOnConnection).click();
        commonWaits.waitForElementClickable(saveConnectorButton).click();

        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(enableButton).click();

    }


    public void enableIncidentNoiseReduction() {
        try {
            commonWaits.waitForPageReadyState(getDriver());
            String status = incidentNoiseReductionToogle.getAttribute("aria-checked");
            if (status.equalsIgnoreCase("false")) {
                incidentNoiseReductionToogle.click();
            }else{
                log.info("INR is already enabled.");
            }
            commonWaits.waitForPageReadyState(getDriver());
        } catch (Exception e) {
            log.error("Error while enabling Incident Noise Reduction: " + e.getMessage());
        }
    }
    public void disablePSRTMFSolution() {
        try {
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForElementVisible(sComfiguredSolutionsTab).click();
            commonWaits.waitForPageReadyState(getDriver());
            String status = psrTMFSolutionButton.getAttribute("aria-checked");
            if (status.equalsIgnoreCase("true")) {
                psrTMFSolutionButton.click();
            }else{
                log.info("PSR TMF solution is already disabled");
            }
            commonWaits.waitForPageReadyState(getDriver());
        } catch (Exception e) {
            log.error("Error while disabling PSR TMF solution: " + e.getMessage());
        }
    }

    public Boolean isINREnabled() {
        log.info("Checking if INR is enabled on PSR solution");
        try {
            commonWaits.waitForElementClickable(PSR_Configure_BUTTON).click();
            refreshBrowser();
            commonWaits.waitForPageReadyState(getDriver());
            String status = incidentNoiseReductionToogle.getAttribute("aria-readonly");
            if (status.equalsIgnoreCase("true")) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            log.error("Error while enabling PSR solution: " + e.getMessage());
        }
        return false;
    }


    public void disablePSRSolution() {
        try {
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForElementVisible(sComfiguredSolutionsTab).click();
            commonWaits.waitForPageReadyState(getDriver());
            String status = psrSolutionButton.getAttribute("aria-checked");
            if (status.equalsIgnoreCase("true")) {
                psrSolutionButton.click();
            }else{
                log.error("PSR solution is already disabled");
            }
            commonWaits.waitForPageReadyState(getDriver());
        } catch (Exception e) {
            log.error("Error while disabling PSR solution: " + e.getMessage());
        }
    }


    public void enablePSRSolution() {
        try {
            commonWaits.waitForPageReadyState(getDriver());
            String status = psrSolutionButton.getAttribute("aria-checked");
            if (status.equalsIgnoreCase("false")) {
                psrSolutionButton.click();
            }else{
                log.info("PSR solution is already Enabled");
            }
            commonWaits.waitForPageReadyState(getDriver());
        } catch (Exception e) {
            log.error("Error while Enabling PSR solution: " + e.getMessage());
        }
    }

    public void deletePSRSolution() {
        try {
            refreshBrowser();
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForElementVisible(sComfiguredSolutionsTab).click();
            commonWaits.waitForPageReadyState(getDriver());
            boolean isPresent = commonWaits.isElementPresent(delete);
            if (isPresent) {
                log.info("Deleting PSR solution");
                commonWaits.pureSafeClick(delete);
                commonWaits.isElementPresent(deleteAccesskeyBtn);
                commonWaits.pureSafeClick(deleteAccesskeyBtn);
                commonWaits.waitForPageReadyState(getDriver());
                commonWaits.waitForElementVisible(configurebuttonForPSRTMF);
            }else{
                log.info("PSR solution is already deleted/ not configured");
            }
            commonWaits.waitForPageReadyState(getDriver());
        } catch (Exception e) {
            log.error("Error while deleting PSR solution: " + e.getMessage());
        }
    }


    public void navigateToSolutionsPage() {
        commonWaits.waitForElementVisible(solutionsTab).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void FillApiPath(String path) {
        commonWaits.waitForElementVisible(apiPath);
        apiPath.sendKeys(path);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void selectQueryParam(){
        commonWaits.waitForElementClickable(param);
        click(param);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(queryParam);
        click(queryParam);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void selectHeaderParam(){
        commonWaits.waitForElementClickable(param1);
        click(param1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(headerParam);
        click(headerParam);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickAddKey(){
        commonWaits.waitForElementClickable(addKey);
        click(addKey);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickAddVariable(){
        commonWaits.waitForElementClickable(addVariablePolicy);
        clickByJS(addVariablePolicy);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertTrue(commonWaits.isElementPresent(crossBtn));
    }

    public void clickDoneButton(){
        commonWaits.waitForElementClickable(doneBtn);
        click(doneBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickSaveButton(){
        commonWaits.waitForElementClickable(saveBtn);
        click(saveBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickSaveVariableButton(){
        commonWaits.waitForElementClickable(saveVariableBtn);
        click(saveVariableBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickVariableArrowBtn(String variableName){
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(variableArrow, variableName)));
        click(String.format(variableArrow, variableName));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void expandVariable(String variableName){
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(variableNameExpand, variableName)));
        click(String.format(variableNameExpand, variableName));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void fillVariableValue(String value) {
        commonWaits.waitForElementClickable(variableValueField);
        sendKeysByAction(variableValueField, value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void FillEventMsgValue() {
        commonWaits.clickWithoutInterception(variableValueField);
        commonWaits.scrollToSpecificElement(eventMsgValue);
        commonWaits.waitForElementClickable(eventMsgValue).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void fillVariableValueBySelectingDropdownValue(String value) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(variableValueField);
        sendKeysByAction(variableValueField, value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void selectQueryParam1(){
        commonWaits.waitForElementClickable(param1);
        click(param1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(queryParam);
        click(queryParam);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void fillKey1(String keyValue) {
        commonWaits.waitForElementVisible(key);
        key.sendKeys(keyValue);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void fillVariable(String value) {
        commonWaits.waitForElementVisible(variableName);
        variableName.sendKeys(value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(findElement(By.xpath(addVariable)));
        click(addVariable);
    }

    public void fillKey2(String keyValue) {
        commonWaits.waitForElementVisible(key1);
        key1.sendKeys(keyValue);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void fillValue1(String value) {
        commonWaits.waitForElementVisible(value1);
        value1.sendKeys(value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void fillValue2(String value) {
        commonWaits.waitForElementVisible(value2);
        value2.sendKeys(value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void setTriggerConditionForMessage(String message) {
        this.triggerConditionTextBox.isDisplayed();
        this.triggerConditionTextBox.click();
        this.noParenthesisOption.isDisplayed();
        this.noParenthesisOption.click();
        commonWaits.scrollToSpecificElement(messageCondition);
        commonWaits.waitForElementClickable(messageCondition).click();
        commonWaits.waitForElementClickable(equalSign).click();
        commonWaits.waitForElementClickable(triggerConditionTextBox);
        typeKeys(triggerConditionTextBox, message);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    }

    public boolean isConnectorPresent(String connectorName){
        boolean flag = false;
        for(int i=0; i< genericRestCardTitle.size(); i++) {
            if (commonWaits.isElementPresent(genericRestCardTitle.get(i))) {
                if (genericRestCardTitle.get(i).getText().equals(connectorName)) {
                    commonWaits.waitForElementClickable(informationHover);
                    click(informationHover);
                    if (commonWaits.isElementPresent(crossIcon)) {
                        deleteConnector(connectorName);
                        flag = false;
                    } else {
                        log.info("RestConnector is present");
                        flag = true;
                        break;
                    }
                } else {
                    log.info("RestConnector is present with different name: " + connectorName + " delete connector and create new");
                    flag = false;
                }
            }
        }
        return flag;
    }

    public String configureOnPremConnector(String connectorType,String ConnectorName) throws Exception
    {
        String pluginKeyName="";
        ConnectorPage connectorPage = new ConnectorPage();
        connectorPage.runCMD(IAConnectorConstant.UNINSTALLCMD);
        log.info("Download the Plugin zip file -- this may take some time to complete");
        downloadPlugin(connectorType);
        log.info("Unzip the above download the Plugin zip file and copy in project folder -- this may take some time to complete");
        unzipDownloadedFolder();
        log.info("Copy Credential file from project folder to required folder");
        createCredentials();
        log.info("Navigate to Plugin tab");
        navigateToPluginPage();
        log.info("Create new Plugin key");
        pluginKeyName=createPluginKey(IAConnectorConstant.pluginName);
        log.info("Plugin Key is created with name " + pluginKeyName);
        CopyConfigFile(IntelligentAutomationPage.CREDS_PATH, IAConnectorConstant.CONFIGJSON_PATH);
        connectorPage.runCMD(IAConnectorConstant.UNINSTALLCMD);
        Assert.assertTrue(isWindowsService(IAConnectorConstant.REMOTE_RESTAPI_PLUGIN,IAConnectorConstant.Failed_Status),"Service has not uninstalled "+ IAConnectorConstant.Service_Name  );
        log.info("Copy Server certificate file from project folder to required folder");
        copyServerCerificates();
        log.info("Execute Install bat file and start remote-restapi-plugin windows service");
        connectorPage.runCMD(IAConnectorConstant.INSTALLCOMMAND);
        connectorPage.runCMD(IAConnectorConstant.INSTALLSTARTCMD);
        Assert.assertTrue(isWindowsService(IAConnectorConstant.REMOTE_RESTAPI_PLUGIN,IAConnectorConstant.Running_Status),"Service is not in running state "+ IAConnectorConstant.Service_Name );
        log.info("Navigate to Connector page");
        navigateToConnectorPage();
        Assert.assertEquals(getConnectorStatus(ConnectorName),SolQAConstants.CONNECTED, "Added connector is not in CONNECTED status" );
        return pluginKeyName;
    }

    public void editRecentlyAddedConnectorName(String actualPluginKeyName, String updatedConnectorName, String connectorType) {
        commonWaits.waitForElementPresent(configuretabLocator,250);
        driver.navigate().refresh();
        commonWaits.waitForElementPresent(configuretabLocator,50);
        refreshBrowser();
        commonWaits.waitForElementPresent(configuretabLocator,20);
        int count = listOfConnectorImgs.size();
        String expPluginKeyName = "";
        for (int i = 1; i <= count; i++) {
            String connectorImgXpath = String.format(connectorImg, i);
            WebElement connectorImgElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(connectorImgXpath)));
            System.out.println(connectorImgElement.getAttribute("alt"));
            if (!connectorImgElement.getAttribute("alt").trim().equalsIgnoreCase(connectorType)) {
                continue;
            }
            String infoButtonXpath = connectorImgXpath+"/../..//i";
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(infoButtonXpath)));
            element.click();
            try {
                if (infoPluginKeyValue.isDisplayed()) {
                    expPluginKeyName = infoPluginKeyValue.getText().trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Actions actions = new Actions(driver);
            if (expPluginKeyName.equalsIgnoreCase(actualPluginKeyName)) {
                actions.sendKeys(Keys.ESCAPE).perform();
                dropDownConnectorButton = String.format(dropDownConnectorButton, i);
                WebElement dropDownConnectorButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dropDownConnectorButton)));
                dropDownConnectorButtonElement.click();
                break;
            } else {
                actions.sendKeys(Keys.ESCAPE).perform();
            }
        }
        commonWaits.waitForElementClickable(drowpdownEditRestConnector);
        clickByAction(drowpdownEditRestConnector);
        commonWaits.waitForElementClickable(editButton).click();
        commonWaits.waitForElementClickable(connectorNameTextInput).clear();
        commonWaits.waitForElementClickable(connectorNameTextInput).sendKeys(updatedConnectorName);
        commonWaits.waitForElementClickable(saveConnectorButton).click();
    }

    public void copyServerCerificates() {
        try {
            String certdestinationPATH = DEST_SERVER_CERTIFICATE_PATH;
            String certsourcePATH = SOURCE_SERVER_CERTIFICATE_PATH;
            log.info(" Copy server certificate from Source Folder " + certsourcePATH + " to " + certdestinationPATH + " is started");
            commonMethods.copyFileFromSourceToDestination(certsourcePATH, certdestinationPATH);
            log.info(" Copy server certificate from Source Folder " + certsourcePATH + " to " + certdestinationPATH + " is completed");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createCredentials() {
        try {
            String destinationPATH = IAConnectorConstant.CREDENTIALDBPATH;
            deleteFile(destinationPATH);
            deleteFile(DEST_ENCRYPTION_KEY_PATH);
            log.info("DB Folder deletion is Completed");
            String sourcePATH = currentDir + File.separator + IAConnectorConstant.SOURCEDBPATH;
            log.info(" Copy credentials from Source Folder " + sourcePATH + " to " + destinationPATH + " is started");
            copyFilesAndFolders(sourcePATH, destinationPATH);
            log.info(" Copy credentials from Source Folder " + sourcePATH + " to " + destinationPATH + " is completed");
            commonMethods.copyFileFromSourceToDestination(SOURCE_ENCRYPTION_KEY_PATH, destinationPATH+File.separator+"encryption_key");
            log.info("Encryption file is copied successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void navigateToPluginPage() {
        wait.until(ExpectedConditions.elementToBeClickable(this.connectorTab));
        Assert.assertTrue(connectorTab.isDisplayed());
        clickToElement(this.connectorTab);
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.elementToBeClickable(this.pluginKeysTab));
        Assert.assertTrue(pluginKeysTab.isDisplayed());
        clickToElement(this.pluginKeysTab);
        commonWaits.waitForPageLoad(driver);
    }

    public static void copyFilesAndFolders(String source, String destination) throws IOException {
        final Path sourcePath = Paths.get(source);
        final Path destinationPath = Paths.get(destination);
        if (Files.isDirectory(sourcePath, new LinkOption[0])) {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = sourcePath.relativize(file);
                    Path destinationFile = destinationPath.resolve(relativePath);
                    Files.createDirectories(destinationFile.getParent());
                    Files.copy(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = sourcePath.relativize(dir);
                    Path destinationDirectory = destinationPath.resolve(relativePath);
                    Files.createDirectories(destinationDirectory);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }

    }


    public boolean isWindowsService(String serviceName, String status)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "sc query " + serviceName);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        boolean success = false;
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");

                if (line.contains(status)) {
                    success = true;
                }
                if (line.contains(status)) {
                    success = true;
                }

            }
        }

        // ✅ Wait until command execution completes
        int exitCode = process.waitFor();

        log.info("Exit code: " + exitCode);
        log.info("Command output:\n" + output);

        return success;
    }

    public void downloadPlugin(String connectorName) throws Exception {
        availableConnectorTab.click();
        configureConnectorByName = String.format(configureConnectorByName, connectorName);
        WebElement configureElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(configureConnectorByName)));
        configureElement.click();
        try {
            deleteFile(IntelligentAutomationPage.PULGIN_PATH);
            deleteFile(IntelligentAutomationPage.PULGIN_PATH_CRDOWNLOAD);
        } catch (Exception e) {
            log.info("Unable to delete existing remote-restapi-plugin.zip file" + e.getMessage());
        }
        wait.until(ExpectedConditions.visibilityOf(downloadPluginButton));
        downloadPluginButton.click();


        int loopCounter = 0;
        try {
            long sizeInMB = 0;
            File remoteRestapiPluginZipFile = new File(IntelligentAutomationPage.PULGIN_PATH);
            boolean fileExists = remoteRestapiPluginZipFile.exists();
            do {
                loopCounter++;
                File remoteRestapiPluginFile = new File(IntelligentAutomationPage.PULGIN_PATH_CRDOWNLOAD);
                sizeInMB = remoteRestapiPluginFile.length() / (1024 * 1024);
                fileExists = remoteRestapiPluginZipFile.exists();
                log.info("File still downloading: "+ fileExists);
                commonWaits.waitForSometime(30000);
            } while (!fileExists && loopCounter <= 15);
        } catch (Exception e) {
            throw new Exception("Unable to download remote-restapi-plugin.zip file, this could be due to network problem. Reached maximum wait time. loopCounter: " + loopCounter);
        }
        if (loopCounter == 16) {
            throw new Exception("Unable to download remote-restapi-plugin.zip file, this could be due to network problem. Reached maximum wait time. loopCounter: " + loopCounter);
        } else {
            log.info("remote-restapi-plugin.zip file has been downloaded successfully");
        }

    }


    public static boolean deleteFile(String strFilename) {
        boolean success = false;

        try {
            log.info("Deleting file names " + strFilename);
            File f1 = new File(strFilename);
            log.info("File delete is started " + f1);
            success = f1.delete();
            log.info("File delete is completed " + f1);
            log.info("File Directory deletion is started ");
            if (f1.exists()) {
                File[] file;
                int fileLength = (file = f1.listFiles()).length;

                for(int i = 0; i < fileLength; ++i) {
                    File file1 = file[i];
                    FileDeleteStrategy.FORCE.delete(file1);
                    success = true;
                }
            }

            log.info("Deleting file names " + strFilename + success);
            log.info("File Directory deletion is Completed ");
        } catch (Exception e) {
            log.error("Unable to delete file:" + e.getMessage());
            e.printStackTrace();
        }

        return success;
    }

    public void unzipDownloadedFolder() {
        commonMethods.killProcess("Windows Command Processor");
        commonMethods.killProcess("node.exe");
        commonMethods.killProcess("npm.exe");
        deleteFile(currentDir + File.separator + IAConnectorConstant.REMOTE_RESTAPI_PLUGIN);
        commonWaits.waitForPageLoad(driver);
        commonMethods.createFolder(currentDir + File.separator + IAConnectorConstant.REMOTE_RESTAPI_PLUGIN);
        commonWaits.waitForPageLoad(driver);
        String sourceFolder = IntelligentAutomationPage.PULGIN_PATH;
        String destinationFolder = currentDir + File.separator + IAConnectorConstant.REMOTE_RESTAPI_PLUGIN;
        commonMethods.unzipplugin(sourceFolder, destinationFolder);
    }


    public IntelligentAutomationPage deletePluginKey(String pluginkeyname){
        navigateToPluginPage();
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForElementVisible(pluginSearch);
        pluginSearch.clear();
        pluginSearch.sendKeys(pluginkeyname+Keys.ENTER);
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementVisible(dropdownActionMenu).click();
        commonWaits.waitForElementVisible(deletePluginKeyButton).click();
        commonWaits.waitForElementVisible(pluginKeyDeleteConfirmationButton).click();
        return this;
    }


    public String createPluginKey(String pluginKeyName) {
        try {
            deleteFile(IntelligentAutomationPage.CREDS_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.Pluginkeyname = pluginKeyName;
        wait.until(ExpectedConditions.elementToBeClickable(this.addPluginKeyButton));
        this.addPluginKeyButton.isDisplayed();
        clickToElement(this.addPluginKeyButton);
        commonWaits.waitForVisibleElement(this.nameTextInput);
        wait.until(ExpectedConditions.visibilityOf(this.nameTextInput));
        this.nameTextInput.isDisplayed();
        this.nameTextInput.clear();
        Pluginkeyname = Pluginkeyname + "_" + commonWaits.randomNumber();
        this.nameTextInput.sendKeys(new CharSequence[]{Pluginkeyname});
        this.downloadPluginKeyButton.isDisplayed();
        this.downloadPluginKeyButton.click();
        commonWaits.waitForPageLoad(driver);
        this.saveConnectorButton.isDisplayed();
        clickToElement(this.saveConnectorButton);
        commonWaits.waitForVisibleElement(SavePluginKeyConfirmationButton);
        this.SavePluginKeyConfirmationButton.isDisplayed();
        clickToElement(this.SavePluginKeyConfirmationButton);
        commonWaits.waitForPageLoad(driver);
        return Pluginkeyname;
    }

    public void CopyConfigFile(String sourceFolder, String destinationFolder) {
        commonWaits.waitForPageLoad(getDriver());
        String confDestinationFolder = currentDir + destinationFolder;
        deleteFile(currentDir + destinationFolder);
        log.info(" Conf File deletion is Completed");
        log.info(" Copy Conf file from Source Folder " + sourceFolder + " to " + confDestinationFolder + " is started");
        commonMethods.copyFileFromSourceToDestination(sourceFolder, confDestinationFolder);
        log.info(" Copy Conf file from Source Folder " + sourceFolder + " to " + confDestinationFolder + " is completed");
    }

    /**
     * Navigate to Value Dashboard (Grafana dashboard for IA reporting)
     * Clicks on Report tab and switches to the Value Dashboard window
     */
    public IntelligentAutomationPage navigateToValueDashboard() {
        log.info("Navigating to Intelligent Automation Value Dashboard");
        try {
            commonWaits.waitForPageLoad(getDriver());
            commonWaits.waitForElementClickable(reportTab).click();
            log.info("Clicked on Report tab");
            
            // Switch to the Value Dashboard window
            switchToWindow("Intelligent Automation Value Dashboard");

            commonWaits.waitForPageLoad(getDriver());
            log.info("Successfully switched to Value Dashboard window");
            
        } catch (Exception e) {
            log.error("Failed to navigate to Value Dashboard: " + e.getMessage());
            throw new AssertionError("Failed to navigate to Value Dashboard: " + e.getMessage());
        }
        return this;
    }

    /**
     * Validate Value Dashboard displays correctly
     * Verifies dashboard name and key panels like "Total Unique Automations"
     */
    public IntelligentAutomationPage validateValueDashboard() {
        log.info("Validating Intelligent Automation Value Dashboard");
        try {
            commonWaits.waitForPageReadyState(driver);

            // Verify dashboard name
            commonWaits.waitForVisibleElement(reportName);
            String dashboardName = reportName.getText();
            log.info("Dashboard name: " + dashboardName);
            
            // Verify Total Unique Automations panel is present
            commonWaits.waitForVisibleElement(totalAutomations);
            String totalAutomationText = totalAutomations.getText();
            log.info("Total Automations panel text: " + totalAutomationText);
            
            // Validate expected values
            if (dashboardName.equals("Intelligent Automation Value Dashboard") && 
                totalAutomationText.equals("Total Unique Automations")) {
                log.info("Value Dashboard validated successfully");
                log.info("Dashboard name and Total Unique Automations panel are correct");
                
                // Close Value Dashboard window and return to parent
                getDriver().close();
                switchToParentWindow();
                commonWaits.waitForPageReadyState(getDriver());

            } else {
                log.error("Value Dashboard validation failed");
                log.error("Expected dashboard name: 'Intelligent Automation Value Dashboard', Found: " + dashboardName);
                log.error("Expected panel text: 'Total Unique Automations', Found: " + totalAutomationText);
                
                // Close window even on failure
                getDriver().close();
                switchToParentWindow();
                commonWaits.waitForPageReadyState(getDriver());
                throw new AssertionError("Value Dashboard validation failed - incorrect dashboard name or panel text");
            }
            
        } catch (Exception e) {
            log.error("Failed to validate Value Dashboard: " + e.getMessage());
            // Attempt to close window and return to parent
            try {
                getDriver().close();
                switchToParentWindow();
            } catch (Exception ex) {
                log.warn("Failed to close window: " + ex.getMessage());
            }
            throw new AssertionError("Failed to validate Value Dashboard: " + e.getMessage());

        }
        return this;
    }


    /**
     * Verify if a button is enabled or disabled on policies page
     * @param button The WebElement of the button to check
     * @return "true" if disabled, "false" if enabled
     */
    public String verifybuttonStatus_onPoliciespage(WebElement button) {
        try {
            commonWaits.waitForElementVisible(button);
            boolean isDisabled = button.getAttribute("disabled") != null || 
                               button.getAttribute("class").contains("disabled");
            log.info("Button status - Disabled: " + isDisabled);
            return isDisabled ? "true" : "false";
        } catch (Exception e) {
            log.error("Failed to verify button status: " + e.getMessage());
            return "false";
        }
    }


    /**
     * Save policy with confirmation dialog and verify message
     * @param expectedMessage The expected confirmation message
     */
    public IntelligentAutomationPage SavePolicyWithConfirmationDialog(String expectedMessage) {
        try {
            // Click save button
            commonWaits.waitForElementClickable(sSavePolicyButton).click();
            commonWaits.waitForPageReadyState(getDriver());
            
            // Wait for confirmation dialog
            String actualMessage = commonWaits.waitForElementVisible(sConfirmDialog).getText().trim();;

            log.info("Confirmation dialog message: " + actualMessage);
            
            // Click confirm button
            commonWaits.waitForElementClickable(sConfirmButton).click();
            commonWaits.waitForPageReadyState(getDriver());
            
            log.info("Saved policy with confirmation");
        } catch (Exception e) {
            log.error("Failed to save policy with confirmation: " + e.getMessage());
            throw new RuntimeException("Failed to save policy with confirmation: " + e.getMessage());
        }
        return this;
    }



    /**
     * Get the policy name from the policy edit page
     * @return The policy name
     */
    public String getPolicyName() {
        try {
            String policyName = commonWaits.waitForElementVisible(sPolicyNameField).getAttribute("value");
            log.info("Retrieved policy name: " + policyName);
            return policyName;
        } catch (Exception e) {
            log.error("Failed to get policy name: " + e.getMessage());
            return "";
        }
    }


    public void enterGenRestwithAPIKeysTSOCloudConnectorDetails(String connectorName, String TSOendPointURL, String TSOloginURL, String Key_Name, String API_Key, String Location, String usernamekey, String passwordkey, String username_value, String password_value) {
        
        commonWaits.waitForElementVisible(availableConnectorTab);
        availableConnectorTab.click();
        configureConnectorByName = String.format(configureConnectorByName, RestConnector);
        WebElement configureElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(configureConnectorByName)));
        commonWaits.clickToElement(configureElement);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(connectorNameTextInput).clear();
        this.connectorNameTextInput.sendKeys(new CharSequence[]{connectorName});
        commonWaits.waitForElementVisible(endPointURLInput).clear();
        this.endPointURLInput.sendKeys(new CharSequence[]{TSOendPointURL});
        commonWaits.clickToElement(selectCredential);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(selectApiKey);
        commonWaits.clickToElement(selectApiKey);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(KeyName).clear();
        this.KeyName.sendKeys(new CharSequence[]{Key_Name});
        commonWaits.waitForElementVisible(APIKey).clear();
        this.APIKey.sendKeys(new CharSequence[]{API_Key});
        commonWaits.waitForElementVisible(LoginURL).clear();
        this.LoginURL.sendKeys(new CharSequence[]{TSOloginURL});
        commonWaits.waitForElementVisible(checkbox);
        commonWaits.clickToElement(checkbox);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(TSOKey);
        this.TSOKey.sendKeys(new CharSequence[]{usernamekey});
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(TSOValue1);
        this.TSOValue1.sendKeys(new CharSequence[]{username_value});
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(clickplus);
        commonWaits.clickToElement(clickplus);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(TSOKey);
        this.TSOKey.sendKeys(new CharSequence[]{passwordkey});
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(TSOValue2);
        this.TSOValue2.sendKeys(new CharSequence[]{password_value});
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(saveConnectorButton);
        this.saveConnectorButton.click();
    }

    public boolean isConnectorPresentByNameContains(String connectorName) {
        log.info("Checking if connector containing name '" + connectorName + "' is present");
        String xpath = String.format(connectorNameXpath, connectorName);
        List<WebElement> matchingConnectors = driver.findElements(By.xpath(xpath));
        if (!matchingConnectors.isEmpty()) {
            log.info("Found " + matchingConnectors.size() + " connector(s) containing name: " + connectorName);
            return true;
        }
        log.info("No connector found containing name: " + connectorName);
        return false;
    }

    /**
     * Click on three dots (kebab menu) for a connector by name and select Edit from dropdown
     * @param connectorName Name of the connector (e.g., "ansible_tower1")
     */
    public IntelligentAutomationPage clickEditConnector(String connectorName) {
        String kebabButton = String.format(connectorKebabButton, connectorName);
        try {
            log.info("Clicking on three dots menu for connector: " + connectorName);
            WebElement kebabElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(kebabButton)));
            kebabElement.click();
            commonWaits.waitForPageLoad(driver);
            log.info("Clicking on Edit option from dropdown menu");
            commonWaits.waitForElementClickable(editButton).click();
            commonWaits.waitForPageLoad(driver);
            log.info("Edit connector page opened for: " + connectorName);
            return this;
        } catch (Exception e) {
            log.error("Failed to click Edit for connector: " + connectorName + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Click on 'View actions' button under Manage Actions section
     */
    public IntelligentAutomationPage clickViewActions() {
        log.info("Clicking on 'View actions' button");
        commonWaits.waitForElementClickable(viewActionsButton).click();
        commonWaits.waitForPageLoad(driver);
        log.info("'View actions' button clicked successfully");
        return this;
    }

    /**
     * Enable or disable the 'Search Online' toggle switch
     * @param enable true to enable, false to disable
     */
    public IntelligentAutomationPage setSearchOnlineToggle(boolean enable) {
        commonWaits.waitForElementVisible(searchOnlineToggleLabel);
        boolean isCurrentlyEnabled = Boolean.parseBoolean(searchOnlineToggleInput.getAttribute("aria-checked"));
        if (enable != isCurrentlyEnabled) {
            log.info((enable ? "Enabling" : "Disabling") + " 'Search Online' toggle");
            searchOnlineToggleLabel.click();
            commonWaits.waitForPageLoad(driver);
        } else {
            log.info("'Search Online' toggle is already " + (enable ? "enabled" : "disabled"));
        }
        return this;
    }

    /**
     * Click on 'Sync Actions' link in the Select Action docked panel
     */
    public IntelligentAutomationPage clickSyncActionsLink() {
        log.info("Clicking on 'Sync Actions' link");
        commonWaits.waitForElementClickable(syncActionsLink).click();
        commonWaits.waitForPageLoad(driver);
        log.info("'Sync Actions' clicked successfully");
        return this;
    }

    /**
     * Get all action names displayed in the 'Select an action' table.
     * @return List of action name strings (trimmed)
     */
    public List<String> getAllActionNames() {
        log.info("Fetching all action names from the actions table");
        List<String> actionNames = new java.util.ArrayList<>();
        for (WebElement el : actionNameElements) {
            String name = el.getText().trim();
            if (!name.isEmpty()) {
                actionNames.add(name);
            }
        }
        log.info("Found " + actionNames.size() + " actions: " + actionNames);
        return actionNames;
    }

    /**
     * Verify that all expected actions are present in the actions table.
     * @param expectedActionsCsv comma-separated string of expected action names
     * @return true if every expected action is found; false otherwise
     */
    public boolean verifyAllActionsPresent(String expectedActionsCsv) {
        log.info("Verifying all expected actions are present");
        List<String> actualActions = getAllActionNames();
        String[] expectedArray = expectedActionsCsv.split(",");
        boolean allPresent = true;
        List<String> missingActions = new java.util.ArrayList<>();

        for (String expected : expectedArray) {
            String trimmed = expected.trim();
            boolean found = false;
            for (String actual : actualActions) {
                if (actual.equalsIgnoreCase(trimmed)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingActions.add(trimmed);
                allPresent = false;
            }
        }

        if (allPresent) {
            log.info("All " + expectedArray.length + " expected actions are present");
        } else {
            log.error("Missing actions: " + missingActions);
        }
        return allPresent;
    }

    /**
     * Verify total action count matches the expected count.
     * @param expectedCount the expected number of actions
     * @return true if actual count matches expected count
     */
    public boolean verifyActionCount(int expectedCount) {
        List<String> actualActions = getAllActionNames();
        log.info("Expected action count: " + expectedCount + ", Actual: " + actualActions.size());
        return actualActions.size() == expectedCount;
    }

    /**
     * Search for an action by name in the 'Select an action' search box, press Enter,
     * and verify the action appears in the filtered results.
     * @param actionName the action name to search for
     * @return true if the action is found in the filtered results
     */
    public boolean searchAndVerifyAction(String actionName) {
        log.info("Searching for action: " + actionName);
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(searchActionButton));
        searchInput.clear();
        searchInput.sendKeys(actionName);
        searchInput.sendKeys(Keys.ENTER);
        commonWaits.waitForPageLoad(driver);

        List<String> filteredActions = getAllActionNames();
        boolean found = false;
        for (String action : filteredActions) {
            if (action.equalsIgnoreCase(actionName.trim())) {
                found = true;
                break;
            }
        }

        if (found) {
            log.info("Action '" + actionName + "' found in search results");
        } else {
            log.error("Action '" + actionName + "' NOT found in search results. Visible actions: " + filteredActions);
        }

        // Clear search to restore full list
        searchInput.clear();
        searchInput.sendKeys(Keys.ENTER);
        commonWaits.waitForPageLoad(driver);

        return found;
    }

    /** Polls every 500 ms (max 120 s) for {@code adapt-toast} success or legacy sync message. */
    public boolean isSyncSuccessfull() {
        log.info("Verifying if sync was successful (toast / message, poll 500 ms).");
        WebDriverWait toastWait = new WebDriverWait(driver, Duration.ofSeconds(120));
        toastWait.pollingEvery(Duration.ofMillis(500));
        toastWait.ignoring(StaleElementReferenceException.class);
        try {
            return toastWait.until(this::isSyncSuccessNotificationPresent);
        } catch (TimeoutException e) {
            log.error("Sync success notification not found within timeout: " + e.getMessage());
            return false;
        }
    }

    private boolean isSyncSuccessNotificationPresent(WebDriver d) {
        for (WebElement toast : syncSuccessToastMessages) {
            try {
                if (toast.isDisplayed() && textIndicatesSyncSuccess(toast.getText())) {
                    log.info("Sync success toast visible: " + toast.getText().trim());
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                // next poll
            }
        }
        for (WebElement el : actionSyncedSuccessfullyMessages) {
            try {
                if (el.isDisplayed()) {
                    log.info("Sync success message visible: " + el.getText().trim());
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                // next poll
            }
        }
        return false;
    }

    private static boolean textIndicatesSyncSuccess(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String t = text.trim();
        String lower = t.toLowerCase();
        return t.contains("Synced")
                || lower.contains("synced successfully")
                || lower.contains("action synced");
    }
}