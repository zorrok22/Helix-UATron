package pages.ITOM.Intelligent_Automation;

import framework.BasePage;

import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;

public class PoliciesPage extends BasePage {

    private static Logger logger = Logger.getLogger(PoliciesPage.class);
    CommonWaits commonWaits=new CommonWaits();

    public static String url = "iaurl"; //"/intelligent-automation/#/sandbox"

    public static String finalpolicyName = "";
    public static String runningState = "Running";
    public String successText = "SUCCESS";
    public String failedText = "FAILED";
    private String successExectionText = "Success";
    public String draftText = "DRAFT";
    public String disableText = "DISABLED";
    public String publishedText = "PUBLISHED";
    public String manualText = "Manual";
    public String AutomaticText = "Automatic";
    public String countLabel = "1";
    public String countsLabel="2";
    public int rowCount=1;
    public int statusRowCount=2;
    public String AWSEventText= "//*[text()='$OPTION']";
    public String ansibleConnectorName= "ansible_tower connector";
    public String SyncMessage= "Action synced successfully.";
    public String SyncProgressMessage= "Sync already in progress.";
    public String action_Sync_Message="";
    public String statusText = "COMPLETED";
    public String pollStatus = "ACTIVE";
    public String currentHandle = getDriver().getWindowHandle();
    public String description="Intelligent Automation";
    public String eventField="Intelligent Automation Incident";
    public String IAAutomation="Intelligent Automation Incident";
    public int tabindex0=0;
    public int tabindex1=1;
    public int tabindex2=2;
    public static String delete_troubleTicket_field = "//b[contains(.,'${ticketField}')]/../..//span[contains(@class,'d-icon-trash')]";
    public String infoFieldName="msg";
    public String itsmIncidentTemplatePolicy = "Create or Update Incident Ticket";
    public String BlackoutTemplatePolicy ="Create or Update Blackout Policy";
    public String incidentTicketDropdown="//*[text()='$FieldName']";
    public String incidentNoteField = "Notes";
    public String expectedEventDetails="undefined";
    public String ansibleNewWorkflowTemplate= "Launch [IT-Systems_Monitoring]";
    public String anisbleWorkflowText="You can use alphanumeric characters, spaces, and all special characters except square [ ] and angle < > brackets.";
    public String isEventJsonNew="{\"innovationstudio\": \"Events\",\"msg\": \"test edit option\",\"test\": \"innovation\"}";
    public WebElement actualEventdetailsElementAfterEditParameter=null;
    public WebElement actualEventdetailsElementBeforEditParameter=null;
    public String expectedEventdetailsElementBeforEditParameter="Events";
    public String k8OnpremEventName = "attribute";
    public String ErrorMsgText="Minimum three characters required.";
    public String K8JsonForTriggerCondition="{\"attribute\": \"Sample test//test1//test2\",\"attribute1\": \"Sample\",\"pqr\": null,\"empty\": \"\"}";
    public String test="S";
    public String k8OnpremEventName1 = "empty";
    public String k8OnpremEventName2 = "attribute1";
    public String ErrorMsgForEmptySpaceText="Minimum three characters required. The value cannot be left empty. Please provide a valid input and try again.";
    public String LastUpdatedTextBeforeEditingPolicy;
    public  String	testMessageText = "The automation policy is saved before testing the policy. Do you want to continue?";
    public String dateTime_Range ="//div[normalize-space()='$OPTION']";
    public String ErrorMsgduplicateAddVeriableText="Variable is already present.";
    public String PathVariable = "Path";
    public String HostName = "HostName";

    @FindBy(xpath = "//a[@href='#/policy/policies']")
    WebElement policyTab;

    @FindBy(xpath = "//span[text()='Automations Policies']")
    WebElement automationHeading;

    @FindBy(xpath = "//span/span[text()='Create Automation Policy']")
    WebElement createAutomationButton;

    @FindBy(xpath = "(//span[text()='Policy Name'])")
    WebElement policyNameColumn;

    @FindBy(xpath = "//span[text()='Execution Mode']")
    WebElement executionModeColumn;

    @FindBy(xpath = "//span[text()='Status']")
    WebElement statusColumn;

    @FindBy(xpath = "//span[text()='Last Updated']")
    WebElement updatedByColumn;

    @FindBy(xpath = "//span[text()='Actions']")
    WebElement actionColumn;

    @FindBy(xpath = "//adapt-table-toolbar//input")
    WebElement policyNameSearchBox;

    @FindBy(xpath = "//input[@name='name']")
    WebElement policyInputText;

    @FindBy(xpath = "//input[contains(@name,'textarea')]")
    WebElement descriptionInput;

    @FindBy(xpath = "//span[text()='Manual']")
    WebElement manualRadioButton;

    @FindBy(xpath = "//span[text()='Automatic']")
    WebElement automaticRadioButton;

    @FindBy(xpath = "//*[@id=\"event-criteria-block\"]//button/span")
    WebElement eventTypeBrowseButton;

    @FindBy(xpath = "//span[text()='Schedule']")
    WebElement scheduleRadioButton;

    @FindBy(xpath = "//button[@data-testid=\"start_date_calendar-control\"]")
    WebElement scheduleStartDate;

    @FindBy(xpath = "//button[@data-testid=\"end_date_calendar-control\"]")
    WebElement scheduleEndDate;

    @FindBy(xpath = "//button[@data-testid=\"start_date_apply\"]")
    WebElement startDateApply;

    @FindBy(xpath = "//button[@data-testid=\"end_date_apply\"]")
    WebElement endDateApply;

    @FindBy(xpath = "//span[text()='Define Event']")
    WebElement defineEventText;

    @FindBy(xpath = "//span[contains(.,'Paste Event JSON')]")
    WebElement eventJSONOption;

    @FindBy(xpath = "//button[@type='button' and @aria-label='Close']")
    WebElement defineEventCloseButton;

    @FindBy(xpath = "//div[@class='ace_content']")
    WebElement JSONFormatTextInput;

    @FindBy(xpath = "//app-event-modal//button")
    WebElement eventdoneButton;

    @FindBy(xpath = "//*[@adapttooltip=\"Remove\"]")
    WebElement removeIcon;

    @FindBy(xpath = "//input[@id='criteria-rows-typeahead-input']")
    WebElement triggerConditionTextbox;

    @FindBy(xpath = "//*[text()='No Bracket']")
    WebElement noParenthesisOption;

    @FindBy(xpath = "//button[.//span[contains(text(), 'Select Action')]]")
    WebElement addActionButton;

    @FindBy(xpath = "//*[@class=\"ace_paren ace_rparen\"]")
    WebElement rightParenthesis;

    @FindBy(xpath = "//*[@class=\"ace_paren ace_rparen\"]")
    WebElement leftParenthesis;

    @FindBy(xpath = "//input[@placeholder='Filter items']")
    WebElement userDefinedSearchbox;

    //	@FindBy(xpath = "//*[@role=\"searchbox\"]")
    //	WebElement userDefinedSearchbox;

    @FindBy(xpath = "//adapt-highlight")
    WebElement adaptHighlight;

    @FindBy(xpath = "(//*[text()='Equals'])")
    WebElement equalSign;

    @FindBy(xpath = "(//*[text()='Events'])")
    WebElement event;

    @FindBy(xpath = "//*[text()=\"Select\"]")
    WebElement selectButton;

    @FindBy(name = "state")
    WebElement state;

    @FindBy(name = "region")
    WebElement region;

    @FindBy(name = "instance_ids")
    WebElement instanceId;

    @FindBy(id = "input-done")
    WebElement inputDoneButton;

    @FindBy(name = "timeInput")
    WebElement timeInputText;

    @FindBy(name = "savingsinUSD")
    WebElement savingsinUSDText;

    @FindBy(xpath = "//input[@id='is-active-checkbox']")
    WebElement activeCheckboxInput;

    @FindBy(id = "save-policy-button")
    WebElement savePolicyButton;



    @FindBy(xpath = "//span[text()='Successful']/following-sibling::span")
    public WebElement successLabelCount;

    @FindBy(xpath="//span[text()='Running']/following-sibling::span")
    public WebElement runningLabelCount;

    @FindBy(xpath = "//span[text()='Failed']/following-sibling::span")
    public WebElement FailedLabelCount;

    @FindBy(id = "0_dropdownMenu")
    WebElement dropdownActionMenu;

    @FindBy(xpath = "//button[contains(.,'Move to draft')]")
    public WebElement unpublish;

    @FindBy(xpath = "//button[contains(.,'Edit')]")
    public WebElement edit;

    @FindBy(xpath = "//button[contains(.,'Copy')]")
    public WebElement copy;

    @FindBy(xpath = "//button[contains(.,'Publish')]")
    public WebElement publish;

    @FindBy(xpath = "//button[contains(.,'Delete')]")
    public WebElement delete;

    @FindBy(xpath = "//button[contains(.,'Move to draft')]")
    public WebElement moveToDraft;

    @FindBy(xpath = "//button[1][text()=' Yes ']")
    public WebElement confirmYesButton;

    @FindBy(xpath = "//table//tbody//tr[1]//td[2]")
    public WebElement policyNameLoc;

    @FindBy(xpath = "//table//tbody//tr[1]//td[4]")
    public WebElement exectutionMode;

    @FindBy(xpath = "//table//tbody//tr[1]//td[5]")
    public WebElement statusLoc;

    @FindBy(xpath = "//table//tbody//tr[1]//td[3]//span[@class='text-uppercase']")
    public WebElement policyStatusLoc;

    @FindBy(xpath = "//*[@adapttooltip=\"Configure\"]")
    public WebElement editActionConfiguration;

    @FindBy(id = "test-policy-button")
    WebElement testPolicyButton;

    @FindBy(xpath = "//button[text()='Confirm']")
    public WebElement confirmbutton;

    @FindBy(xpath = "//button[text()='Continue']")
    public WebElement continuebutton;

    @FindBy(xpath = "//button[text()='Execute']")
    public WebElement executebutton;

    @FindBy(xpath = "//span[text()='Running']")
    public WebElement policyExectionStatus;

    @FindBy(xpath = "//span[text()='Success']")
    public WebElement policyExectionSuccessStatus;

    @FindBy(xpath = "//*[contains(@class,'restart')]")
    public WebElement refreshExectionStatus;

    @FindBy(xpath = "//button[1][text()='Cancel']")
    public WebElement cancelButton;

    @FindBy(name = "policyName")
    WebElement policyNameInput;

    @FindBy(id = "copy-policy-button")
    WebElement copyPolicyButton;

    @FindBy(xpath = "//adapt-filter-controls[@data-testid='status']")
    WebElement filter_Status_ellipsis;

    @FindBy(xpath = "//div[text()='Published']")
    public WebElement published_Status_Checkbox;

    @FindBy(xpath = "//div[text()='Draft']")
    public WebElement draft_Status_Checkbox;

    @FindBy(xpath = "//adapt-filter-controls[@data-testid='ExecutionMode']")
    WebElement filter_ExecutionMode_ellipsis;

    @FindBy(xpath = "//div[text()='Manual']")
    public WebElement manual_ExectionMode_Checkbox;

    @FindBy(xpath = "//div[text()='Automatic']")
    WebElement Automatic_ExectionMode_Checkbox;

    @FindBy(xpath = "//button[contains(.,'Apply filters')]")
    public WebElement apply_Filters_Button;

    @FindBy(xpath = "//button[text()=' Remove all ']")
    WebElement  remove_all_Button;

    @FindBy(xpath = "//button[contains(.,'Cancel')]")
    WebElement cancel_Filters_Button;

    @FindBy(xpath = "//button[contains(text(),'active')]")
    WebElement active_Filters_Text;

    @FindBy(xpath = "//button[contains(.,'Clear')][1]")
    WebElement clearButton;

    @FindBy(xpath = "//a[(text()=' Sync Actions ')]")
    WebElement syncActionButton;

    @FindBy(xpath = "//span[contains(text(),'actions available')]")
    WebElement syncActionCount;

    @FindBy(xpath = "//span[(text()='Syncing Actions')]")
    WebElement syncText;

    @FindBy(xpath = "//div[contains(text(),'Action synced successfully.')]")
    WebElement actionSyncMessage;

    @FindBy(id = "cancel-policy-button")
    WebElement cancelPolicyButton;

    @FindBy(xpath = "(//adapt-rx-search//button)[2]")
    WebElement clearActionButton;

    @FindBy(xpath = "//input[@placeholder='Search actions']")
    WebElement searchActionButton;

    @FindBy(xpath = "(//input[@name='name'])[2]")
    WebElement k8DeploymentName;

    @FindBy(xpath = "//input[@name='namespace']")
    WebElement k8Namespace;

    @FindBy(xpath = "//span[contains(.,'The application associated with this action is not reachable. Check the connector status and ensure it is enabled and running.')]")
    WebElement actionErrorLoc;

    @FindBy(name = "nameInput")
    WebElement inputnameText;

    @FindBy(xpath = "//button[@name = 'restMethodFormField']")
    WebElement httpMethodDropdown;

    @FindBy(xpath = "//div[contains(adapt-highlight, 'POST')]")
    public WebElement actionPOST;

    @FindBy(xpath = "//adapt-highlight[normalize-space()='DELETE']")
    public WebElement actionDELETE;

    @FindBy(xpath = "//div[contains(adapt-highlight, 'PUT')]")
    public WebElement actionPUT;

    @FindBy(xpath = "//*[contains(text(),'GET')][1]")
    public WebElement actionGET;

    @FindBy(xpath = "//div[contains(adapt-highlight, 'PATCH')]")
    public WebElement actionPATCH;


    @FindBy(xpath = "//input[@name='apiPathFormField']")
    public WebElement apiInputPath;

    @FindBy(xpath = "//button[contains(@adapttooltip,'Remove')]")
    public WebElement actionTrashicon;

    @FindBy(xpath = "//*[@id=\"adapt_accordion_tab_id_2\"]/div/span[1]")
    public WebElement inputValueDropdown;

    @FindBy(xpath = "//input[@name='Name']")
    public WebElement processInputTextbox;

    @FindBy(xpath = "//adapt-highlight[contains(.,'_tenant_id')]")
    WebElement tenantId;

    @FindBy(xpath = "//adapt-highlight[contains(.,'1602985772')]")
    WebElement tenantValue;

    @FindBy(xpath = "//input[@id='tag-key+0']")
    public WebElement addKey;

    @FindBy(xpath = "//input[@id='tag-value+0']")
    public WebElement addValue;

    @FindBy(xpath = "//input[@id='tag-key+1']")
    public WebElement addKey1;

    @FindBy(xpath = "//input[@id='tag-value+1']")
    public WebElement addValue1;

    @FindBy(xpath = "//input[@id='tag-key+2']")
    public WebElement addKey2;

    @FindBy(xpath = "//input[@id='tag-value+2']")
    public WebElement addValue2;
    @FindBy(xpath = "//div[@role='alert'][contains(.,'This field is required')]")
    WebElement keyErrorMessage;

    @FindBy(xpath = "//div[@role='alert'][contains(.,'This field is required')]")
    WebElement valueErrorMessage;

    @FindBy(xpath = "//span[contains(.,'message')]")
    WebElement messageCheckBox;

    @FindBy(xpath = "(//span[contains(text(),'status')])[1]")
    WebElement statusCheckbox;

    @FindBy(xpath = "//div[@class='system-tag light-gray'][contains(.,'class')]")
    WebElement classCheckbox;

    @FindBy(xpath = "//button[@class='btn btn-primary btn-sm']")
    WebElement eventDoneButton;

    @FindBy(xpath = "//input[@data-testid='tag-key+1_input']")
    public WebElement keyFilter;

    @FindBy(xpath = "//input[contains(@id,'tag-value+1')]")
    public WebElement valueFilter;

    @FindBy(xpath = "//div[@title='Displays Name status']")
    WebElement displayStatus;

    @FindBy(xpath = "//span[@class='segment-key'][contains(.,'responsecode')]")
    public WebElement responceCode;

    @FindBy(xpath = "//span[text()='\"COMPLETED\"']")
    WebElement statusCheck;

    @FindBy(xpath = "//span[text()='200']")
    WebElement responseValue;

    @FindBy(xpath = "//button[contains(@class,'close')]")
    WebElement policyDetailsClose;

    @FindBy(xpath = "//span[text()='Select']")
    WebElement processEventSelect;

    @FindBy(xpath = "//span[text()='Input Parameters']")
    WebElement inputParameter;

    @FindBy(xpath = "//div[@role='alert']")
    WebElement requiredMessage;

    @FindBy(xpath = "//input[@name='event data']")
    WebElement eventData;

    @FindBy(xpath = "//input[@name='action id']")
    WebElement actionId;

    @FindBy(xpath = "//*[(text()='\"COMPLETED\"')] | //*[(text()='\"ACTIVE\"')]")
    WebElement statusCheckComplete;

    @FindBy(xpath = "//adapt-highlight[text()='tags.platform']")
    WebElement tagPlatform;

    @FindBy(xpath = "//adapt-highlight[text()='kubernetes']")
    WebElement tagPlatformName;

    @FindBy(xpath = "//span[text()='Item 1']")
    WebElement itemElement;

    @FindBy(xpath = "(//input[@name='name'])[last()]")
    WebElement resourceInput;

    @FindBy(xpath = "//span[text()='Resources']")
    WebElement resoureceName;

    @FindBy(xpath = "//span[text()='Limits']")
    WebElement limitToSet;

    @FindBy(xpath = "(//input[@name='cpu'])[1]")
    WebElement cpuValue;

    @FindBy(xpath = "(//input[@name='memory'])[1]")
    WebElement memoryValueOne;

    @FindBy(xpath="(//span[contains(text(),'Requests')])[2]")
    WebElement elementRequest;

    @FindBy(xpath = "(//input[@name='cpu'])[last()]")
    WebElement cpuRequestValue;

    @FindBy(xpath = "(//input[@name='memory'])[last()]")
    WebElement memoryValueTwo;

    @FindBy(xpath = "//input[@id='ticket-switcher']")
    WebElement changeEnableButton;

    @FindBy(xpath="(//input[@type='checkbox'])[1]")
    WebElement changeEnableBtn;

    @FindBy(xpath="//span[text()='Modify']")
    WebElement modifyButton;

    @FindBy(name="impact")
    WebElement impactSelectButton;

    @FindBy(xpath="//*[text()='1-Extensive/Widespread']")
    public WebElement impactFirstDropDown;

    @FindBy(xpath="//div[contains(text(),'2-Significant/Large')]")
    public WebElement impactSecondDropDown;

    @FindBy(xpath="//div[contains(text(),'3-Moderate/Limited')]")
    public WebElement impactThirdDropDown;

    @FindBy(xpath="//div[contains(text(),'4-Minor/Localized')]")
    public WebElement impactFourthDropDown;

    @FindBy(name = "urgency")
    public WebElement urgencySelect;

    @FindBy(xpath="//button[contains(@class, 'rx-select__option') and contains(., '1-Critical')]")
    public WebElement urgencyCritical;

    @FindBy(xpath="//div[contains(text(),'2-High')]")
    public WebElement urgencyHigh;

    @FindBy(xpath="//div[contains(text(),'3-Medium')]")
    public WebElement urgencyMedium;

    @FindBy(xpath="//div[contains(text(),'4-Low')]")
    public WebElement urgencyLow;

    @FindBy(xpath="//input[contains(@id,'rx-typeahead')]")
    WebElement firstName;

    @FindBy(xpath="//button[@role='option'][contains(@id, 'BMCOps')]")
    List<WebElement> firstNameListbox;

    @FindBy(name="last_name")
    WebElement lastName;

    @FindBy(name="company")
    public WebElement companySelectButton;

    @FindBy(xpath="//div[contains(text(),'Calbro Services')]")
    public WebElement companyName;

    @FindBy(xpath="//textarea[@autocomplete='off']")
    WebElement changeDescription;

    @FindBy(xpath="(//button[contains(text(),'Save')])[2]")
    WebElement changeConfigSaveButton;

    @FindBy(xpath = "//span[text()='Waiting']/following-sibling::span")
    public WebElement WaitingLabelCount;

    @FindBy(xpath = "//span[text()='Rejected']/following-sibling::span")
    public WebElement RejectedLabelCount;

    @FindBy(xpath="//a[@closebtn='true']")
    WebElement ticketId;

    @FindBy(xpath="//*[text()='BMCOpsMonitoring']")
    public WebElement companyNameBMC;

    @FindBy(xpath="//pre[contains(text(),\"MessageId\")]")
    public WebElement messageId;

    @FindBy(xpath="//button[@aria-label='Profile menu']")
    WebElement profileDropDown;

    @FindBy(xpath="//button[contains(.,'My profile')]")
    WebElement myProfileButton;

    @FindBy(xpath="//*[contains(text(),'Tenant ID')]/following-sibling::div")
    WebElement tenantIdValue;

    @FindBy(xpath="//span[1]//dd[1]")
    WebElement impactDisplayValue;

    @FindBy(xpath="//span[2]//dd[1]")
    WebElement urgencyDisplayValue;

    @FindBy(xpath="//span[3]//dd[1]")
    WebElement companyDisplayValue;

    @FindBy(xpath="//span[4]//dd[1]")
    WebElement fnameDisplayValue;

    @FindBy(xpath="//span[5]//dd[1]")
    WebElement lnameDisplayValue;

    @FindBy(xpath="//span[6]//dd[1]")
    WebElement descriptionDisplayValue;

    @FindBy(xpath="//span[@class='menu-profile__username']")
    WebElement menuProfileUsername;

    @FindBy(xpath="(//div[@role='alert'][text()='This field is required'])[1]")
    WebElement fieldRequiredMessage;

    @FindBy(xpath = "//span[text()='Refresh']/..")
    WebElement refresh;

    @FindBy(xpath="//textarea[@name='processInputValues']")
    WebElement processInput;

    @FindBy(xpath="//input[@name='processDefinitionName']")
    WebElement processDefinition;

    @FindBy(xpath = "//span[text()='Rejected']/following-sibling::span")
    public WebElement rejectedLabelCount;

    @FindBy(xpath="//button[@class='btn btn-primary'][contains(.,'Save')]")
    WebElement saveConfiguration;

    @FindBy(xpath="//button[@aria-label='Close']")
    WebElement closeButton;

    @FindBy(xpath="//adapt-highlight[text()='sourceNodeID']")
    public WebElement triggerConditionNumber;

    @FindBy(xpath="//adapt-highlight[text()='271878215']")
    public WebElement triggerConditionNumberValue;

    @FindBy(xpath="//adapt-highlight[text()='null']")
    public WebElement triggerNullValue;

    @FindBy(xpath="//adapt-highlight[text()='string']")
    public WebElement triggerString;

    @FindBy(xpath="//adapt-highlight[contains(.,'abc@123#456**7&8')]")
    public WebElement stringWithSpecialChar;

    @FindBy(xpath="//adapt-highlight[text()='empty']")
    public WebElement triggerConditionEmpty;

    @FindBy(xpath="//adapt-highlight[text()='\"\"']")
    public WebElement emptyValue;

    @FindBy(xpath="//adapt-highlight[text()='boolean']")
    public WebElement booleanKey;

    @FindBy(xpath="//*[text()='true']")
    public WebElement trueValue;

    @FindBy(xpath="//a[contains(@class,'d-icon-sm d-icon-cross pl-2')]")
    WebElement triggerConditionClose;

    @FindBy(xpath="//adapt-highlight[text()='floatingID']")
    public WebElement floatTriggerCondition;

    @FindBy(xpath="//adapt-highlight[text()='1.5']")
    public WebElement floatValue;

    @FindBy(xpath="//adapt-highlight[text()='abc']")
    public WebElement abcString;

    @FindBy(xpath="//adapt-highlight[text()='false']")
    public WebElement falseValue;

    @FindBy(xpath="//adapt-highlight[text()='OR']")
    public WebElement orCondition;

    @FindBy(xpath="//adapt-highlight[text()='source_address']")
    public WebElement sourceAddress;

    @FindBy(xpath="//adapt-highlight[text()='110.201.199.241']")
    public WebElement sourceAddressValue;

    @FindBy(xpath="//adapt-highlight[text()='AND']")
    public WebElement andCondition;

    @FindBy(xpath="//adapt-highlight[text()='No Bracket']")
    WebElement noBracket;

    @FindBy(xpath="//adapt-highlight[text()='Equals']")
    WebElement triggerConditionEquals;

    @FindBy(xpath="//button[@aria-label='Close']")
    WebElement eventCloseButton;

    @FindBy(xpath = "(//input[@name='name'])")
    public List<WebElement> tssaInputTextbox;

    @FindBy(xpath = "(//span[contains(@class,'tab-caret')])")
    public List<WebElement> tssaDropdown;

    @FindBy(xpath = "//span[contains(text(),'Group')]/../../span")
    public WebElement tssaGroupDropdown;

    @FindBy(xpath = "//span[contains(text(),'Targets')]/../../span")
    public WebElement tssaTargetsDropdown;

    @FindBy(xpath = "//span[contains(text(),'Servers')]/../../span")
    public WebElement tssaServerDropdown;

    @FindBy(xpath = "(//span[contains(text(),'Item 1')]/../../span)")
    public List<WebElement> itemDropdown;

    @FindBy(xpath = "(//span[contains(text(),'Item 2')]/../../span)")
    public List<WebElement> item2Dropdown;

    @FindBy(xpath = "//span[contains(text(),'Overridden Script Params')]/../../span")
    public WebElement tssaParamsDropdown;

    @FindBy(xpath = "//input[contains(@name,'path')]")
    public WebElement tssaPathInputTextbox;

    @FindBy(xpath = "//input[@name='description']")
    public WebElement tssaInputDescriptionTextbox;

    @FindBy(xpath = "//button[normalize-space()='Add Item']")
    public WebElement tssaAddItemLink;

    @FindBy(xpath = "//input[@name='value']")
    public List<WebElement> tssaNameInputTextbox;

    @FindBy(xpath="//span[contains(text(),'Workflow executed successfully!')]")
    WebElement workFlowMessage;

    @FindBy(xpath="//adapt-filter-controls[@data-testid='updated_by']")
    public WebElement filterUpdatedBy;

    @FindBy(xpath="(//input[@role='checkbox'])[1]")
    public WebElement updatedByUser;

    @FindBy(xpath="(//button[@id='0_dropdownMenu'])[1]")
    public WebElement dropdownAction;

    @FindBy(xpath = "(//span[normalize-space()='Action Name'])")
    WebElement actionNameField;

    @FindBy(xpath = "//input[@id='action-name-input']")
    WebElement actionName;

    @FindBy(xpath = "//p[contains(text(),'Maps and transforms the event fields to trouble ticket fields based on which a trouble ticket is created.')]")
    WebElement descriptionFieldText;

    @FindBy(xpath = "//b[contains(text(),'description')]")
    WebElement itsmFieldDescription;

    @FindBy(xpath = "//b[contains(text(),'severity')]")
    WebElement itsmFieldSeverity;

    @FindBy(xpath = "//b[contains(text(),'ticketType')]")
    WebElement itsmFieldTicketType;

    @FindBy(xpath = "//b[contains(text(),'status')]")
    WebElement itsmFieldStatus;

    @FindBy(xpath = "//b[contains(text(),'company')]")
    WebElement itsmFieldCompany;

    @FindBy(xpath = "//b[contains(text(),'attachment')]")
    WebElement itsmFieldAttachment;

    @FindBy(xpath = "//b[contains(text(),'statusChange')]")
    WebElement itsmFieldStatusChange;

    @FindBy(xpath = "//b[contains(text(),'statusChangeReason')]")
    WebElement itsmFieldStatusChangeReason;

    @FindBy(xpath = "//b[contains(text(),'statusChangeDate')]")
    WebElement itsmFieldStatusChangeDate;

    @FindBy(xpath = "//b[contains(text(),'channel')]")
    WebElement itsmFieldChannel;

    @FindBy(xpath = "//b[contains(text(),'CreationDate')]")
    WebElement itsmFieldCreationDate;

    @FindBy(xpath = "//b[contains(text(),'expectedResolutionDate')]")
    WebElement itsmFieldExpectedResolutionDate;

    @FindBy(xpath = "//b[contains(text(),'externaId')]")
    WebElement itsmFieldExternalID;

    @FindBy(xpath = "//b[contains(text(),'lastUpdate')]")
    WebElement itsmFieldLastUpdate;

    @FindBy(xpath = "//b[contains(text(),'name')]")
    WebElement itsmFieldName;

    @FindBy(xpath = "//b[contains(text(),'note')]")
    WebElement itsmFieldNote;

    @FindBy(xpath = "//b[contains(text(),'priority')]")
    WebElement itsmFieldPriority;

    @FindBy(xpath = "//b[contains(text(),'relatedEntity')]")
    WebElement itsmFieldRelatedEntity;

    @FindBy(xpath = "//b[contains(text(),'relatedParty')]")
    WebElement itsmFieldRelatedParty;

    @FindBy(xpath = "//b[contains(text(),'requestedResolutionDate')]")
    WebElement itsmFieldRequestedResolutionDate;

    @FindBy(xpath = "//span[@class='segment-value ng-star-inserted']")
    WebElement policyExecutionError;

    @FindBy(className = "close")
    WebElement closeExecutionWindow;

    @FindBy(xpath = "//p[contains(text(),'Create Trouble Ticket')]")
    WebElement tmfDescriptionFieldText;

    @FindBy(xpath = "//span[normalize-space()='Request Body']")
    WebElement requestBodyField;

    @FindBy(xpath = "//img[@alt='arrow down']")
    List<WebElement> dropDownField;

    @FindBy(xpath = "//div[text()='Start typing']")
    WebElement textboxField;

    @FindBy(xpath = "//b[contains(text(),'incidentId')]")
    WebElement itsmIncidentld;

    @FindBy(xpath = "(//*[text()='INCIDENT_ORIGIN'])")
    WebElement incident_origin;

    @FindBy(xpath = "//button[contains(.,'Add Trouble Ticket Field')]")
    WebElement addTroubleTicketField;

    @FindBy(xpath = "//input[@placeholder='Filter']")
    WebElement searchFilterOptions;

    @FindBy(xpath = "//button[@role='option']/div/div")
    WebElement searchField;

    @FindBy(xpath = "//button[contains(@class,'action-trash d-icon-pencil')]")
    public List<WebElement> editaction;

    @FindBy(xpath = "//div[contains(@class,'adapt-mt-input-container')]")
    public List<WebElement> inputContainer;

    @FindBy(xpath = "(//input[@role='combobox'])")
    public List<WebElement> inputTextField;

    @FindBy(xpath = "//button[contains(.,'Add Event Field')]")
    WebElement addInfoEventField;

    @FindBy(xpath="//td[text()='Completed']")
    WebElement completedStatus;

    @FindBy(xpath = "//button[contains(.,'Add Incident Field')]")
    WebElement addIncidentField;

    @FindBy(xpath = "//span[contains(text(),'Available Fields')]")
    WebElement availableFields;

    @FindBy(xpath = "//span[contains(text(),'Custom Field')]")
    WebElement customFields;

    @FindBy(xpath = "//div[@aria-label=\"Knowledge Center Bot, also known as KC Bot is an onboarding assistant that allows you to see the list of onboarding items in one place for quick and easy reference. This improves your in-app experience.\"]")
    public WebElement helpDrag;

    @FindBy(xpath = "//h2[text()='Map the event fields to the ITSM fields to create an Incident.']")
    public WebElement helpDrop;

    @FindBy(xpath = "//button[@aria-controls='adapt-tabs-dropdown-event-details']")
    public WebElement eventDetailsTab;

    @FindBy(xpath = "//div[text()='You can use alphanumeric characters, spaces, and all special characters except square [ ] and angle < > brackets.']")
    public WebElement ansibleNewWorkflowTemplate1;

    @FindBy(xpath = "//button[@id='input-cancel']")
    public WebElement cancleButton;

    @FindBy(xpath = "//adapt-highlight[text()='msg']")
    WebElement msgInput;

    @FindBy(xpath = "//*[text()='test edit option']")
    WebElement msgText;

    @FindBy(xpath = "(//*[text()='Equals'])[2]")
    WebElement equalSign1;

    @FindBy(xpath = "//div[@class='adapt-rx-typeahead__input-block']")
    WebElement activeTextboxInput;

    @FindBy(xpath = "//div[text()='Test Inputs']")
    WebElement testInputBox;

    @FindBy(xpath = "(//div[@aria-label='innovationstudio'])[2]")
    WebElement parameterToEdit;

    @FindBy(xpath = "(//input[@type='text'])[8]")
    WebElement textBoxToEnter;

    @FindBy(xpath="//*[text()='true']")
    public WebElement triggerBooleanValue;

    @FindBy(xpath = "//div[text()='Minimum three characters required. ']")
    public WebElement ErrorMsg;

    @FindBy(xpath="//adapt-highlight[text()='attribute']")
    public WebElement attributeKey;

    @FindBy(xpath="//adapt-highlight[text()='Begins with']")
    public WebElement beginsWithOperator;


    @FindBy(xpath="//*[text()='S']")
    public WebElement attributeValueBeginsWith;

    @FindBy(xpath="//adapt-highlight[text()='empty']")
    public WebElement emptyKey;

    @FindBy(xpath = "//div[text()='Minimum three characters required. The value cannot be left empty. Please provide a valid input and try again. ']")
    public WebElement ErrorMsgForEmptySpace;

    @FindBy(xpath="//adapt-highlight[text()='attribute1']")
    public WebElement attribute1Key;

    @FindBy(xpath="(//label[@class='px-1 ng-star-inserted'])[3]")
    public WebElement elementToEdit;

    @FindBy(xpath="(//input[@id='is-active-checkbox1'])[1]")
    public WebElement executeOnEventCheckbox;

    @FindBy(xpath = "//button[@class='btn btn-link adapt-table-toolbar__btn-link']")
    WebElement coloumnSelectorIcon;

    @FindBy(xpath = "(//span[text()='Policy Name'])[2]")
    WebElement policyNameCheckbox;

    @FindBy(xpath = "(//span[text()='Execution Mode'])[2]")
    WebElement executionModeCheckbox;

    @FindBy(xpath = "(//span[text()='Status'])[2]")
    WebElement statusCheckboxInColoumnSelector;

    @FindBy(xpath = "(//span[text()='Last Updated'])[2]")
    WebElement lastUpdatedCheckbox;

    @FindBy(xpath = "(//span[text()='Actions'])[2]")
    WebElement actionsCheckbox;

    @FindBy(xpath = "//button[@class='btn btn-link adapt-table-toolbar__btn-link']")
    WebElement coloumnSelectorClosedIcon;

    @FindBy(xpath = "//button[@id='test-policy-button']")
    WebElement disabledtestPolicyButton;

    @FindBy(xpath = "//button[text()='Discard & Exit']")
    WebElement discardAndExitButton;

    @FindBy(xpath = "//button[text()='Confirm']")
    WebElement confirmButton;

    @FindBy(xpath = "(//span[contains(text(),'category')])[1]")
    WebElement categoryCheckbox;

    @FindBy(xpath = "(//span[contains(text(),'identifier')])[1]")
    WebElement identifierCheckbox;

    @FindBy(xpath = "//input[@name='enablePolicyCheckbox']")
    WebElement publishPolicyCheckbox;

    @FindBy(xpath = "//span[text()='The automation policy is saved before testing the policy. Do you want to continue?']")
    public WebElement policyTestMessage;

    @FindBy(xpath = "//button[@name='On this day']")
    WebElement onThisDay;

    @FindBy(xpath = "//*[.='No end date']")
    WebElement noEndDay;

    @FindBy(xpath = "//span[text()='Add Linked Action']")
    WebElement addLinkedAction;

    @FindBy(xpath = "//button[@name='restMethodFormField']")
    WebElement restMethodFormField;

    @FindBy(xpath = "//*[.='POST']")
    WebElement selectPOST;

    @FindBy(xpath = "//input[@name='apiPathFormField']")
    WebElement apiPath;

    @FindBy(xpath = "//h2[@class=\"work-flow-details-title\"]")
    public WebElement policyDetailsName;

    @FindBy(xpath = "//div[@class='nav-link active']")
    public WebElement policyDetailsTime;

    @FindBy(xpath = "//adapt-tabset[contains(@class, 'workflow-tab-section')]")
    public WebElement policyDetails;

    @FindBy(xpath = "//span[@rx-id='edit-timezone-link']")
    WebElement editTimePreference;

    @FindBy(xpath = "//adapt-rx-select[@rx-id='select-timezone']/div")
    WebElement timeZoneDropdown;

    @FindBy(xpath = "//div[contains(@class, 'rx-select__option-content')]//div[contains(., 'Hawaii')]")
    public WebElement hawaiiTimeZone;

    @FindBy(xpath = "//div[contains(@class, 'rx-select__option-content')]//div[contains(., 'Kolkata')]")
    public WebElement Ind_Kolkata_TimeZone;

    @FindBy(xpath = "//span[@rx-id='save-timezone']")
    WebElement saveTimeZone;

    @FindBy(xpath = "//td[contains(@class, 'at-data-cell') and contains(@data-testid, 'adapt-table_column-3_row-0')]//span")
    public WebElement Policy_TimeZone;

    @FindBy(xpath = "//input[@placeholder='Get Events']")
    WebElement bhomConnectorActionName;

    @FindBy(xpath = "//input[@placeholder='Generic REST Action']")
    public WebElement linkedActionName;

    @FindBy(xpath = "//span[contains(text(), 'bhom connector')]")
    public WebElement bhomConnectorExpand;

    @FindBy(xpath = "//div[contains(@class, 'a3t-datetime--controls-wrapper')]")
    public WebElement policy_schedule_start_date;

    @FindBy(xpath = "//input[@name='everyCounter-undefined']")
    public WebElement policy_recurring_time;

    @FindBy(xpath = "//button[@id='add-variable-button']")
    public WebElement addVariable;

    @FindBy(xpath = "//input[@placeholder='Enter variable name']")
    public WebElement addVariable_name;

    @FindBy(xpath = "//button[contains(@class,'btn-add-variable')]")
    public WebElement addVariable_btn;

    @FindBy(xpath = "//div[b[text()='Test Variable']]")
    public WebElement expand_Variable;

    @FindBy(xpath = "//input[@id='adapt-mt-0_search']")
    public WebElement add_Variable_value;

    @FindBy(xpath = "//span[@class='btn-content' and normalize-space()='Edit Variables']")
    public WebElement edit_Variable;

    @FindBy(xpath = "//span[contains(@class, 'a-tag__content')]/span")
    public WebElement existing_variable_value;

    @FindBy(xpath = "//b[text()='Test Variable']/ancestor::button//span[@class='d-icon-trash']")
    public WebElement delete_Variable;

    @FindBy(xpath="(//div[@role='listbox']//button)")
    List<WebElement> Chain_Mgm_UserList;

    @FindBy(xpath = "//button[@id='add-variable-button' and .//span[contains(text(), 'Add Variables')]]")
    public WebElement addVariableButton;

    @FindBy(xpath = "//input[@type='radio' and @value='Events' and @checked='checked' ]")
    public WebElement events_RadioButton;

    @FindBy(xpath = "//span[text()='Add Variable']/ancestor::button")
    public WebElement addVeriable_button;

    @FindBy(xpath = "//input[@placeholder='Enter variable name' and @type='text']")
    public WebElement addVerable_textBox;

    @FindBy(xpath = "//span[text()='Variable is already present.']")
    public WebElement errorMsg_ForDuplicateVariable;

    @FindBy(xpath = "(//input[@type='text' and contains(@class, 'rx-form-control') and @role='combobox'])[2]")
    public WebElement defaultValue_TextBox;

    @FindBy(xpath = "//a[contains(.,'Configure Text')]")
    public WebElement configureTextLinkTypeProperty;

    @FindBy(xpath = "//a[@class='add-translation-text']")
    public WebElement AddTransformationlink;

    @FindBy(xpath = "//button[contains(@name,'transTypeMdl')]")
    public WebElement transTypeDropdown;

    @FindBy(xpath = "(//div[@role='listbox'])//button[3]")
    public WebElement stringDropdownItem;

    @FindBy(xpath = "//button[contains(@name,'functionTag')]")
    public WebElement functionTagDropdown;

    @FindBy(xpath = "//div[contains(@class, 'rx-select__option-content--default') and contains(., 'toLowerCase')]")
    public WebElement toLowerCase;

    @FindBy(xpath = "(//button[text()='Save'])[1]")
    public WebElement btn_save;

    @FindBy(xpath = "//button[contains(.,'Done')]")
    public WebElement doneButton;

    @FindBy(xpath = "(//button[@type='button' and normalize-space(text())='Add Condition'])[1]")
    public WebElement addConditionButton;

    @FindBy(xpath = "(//input[@id='criteria-rows-typeahead-input'])[2]")
    public WebElement conditionInputTextbox;

    @FindBy(xpath = "//*[text()='contains']")
    public WebElement containssign;

    @FindBy(xpath = "//input[@id='custom-criteria-rows-typeahead-input']")
    public WebElement VariableName;

    @FindBy(xpath = "//button[starts-with(@name, 'inOptionFormField')]//div[contains(@class, 'rx-select__search-button-title')]")
    public WebElement TypeQueryParam;

    @FindBy(xpath = "//button[.//adapt-highlight[contains(text(), 'PATH')]]")
    public WebElement pathTyapeQueryParam;

    @FindBy(xpath = "//input[starts-with(@name, 'keyFormField') and contains(@class, 'rx-form-control')]")
    public WebElement keyField;

    @FindBy(xpath = "//input[starts-with(@name, 'valueFormField') and contains(@class, 'rx-form-control')]")
    public WebElement ValueField;

    @FindBy(xpath = "//textarea[@name='requestBodyOptionFormField']")
    public WebElement RequestBodyField;

    @FindBy(xpath = "//div[@id='retentions']//adapt-rx-select//button[@id='rx-select-box1']")
    public WebElement PolicyRetentionOption;

    @FindBy(xpath = "//button[.//adapt-highlight[text()='15 days']]")
    public WebElement FifteenDaysOption;

    @FindBy(xpath = "//button[.//adapt-highlight[text()='30 days']]")
    public WebElement ThiryDaysOption;

    @FindBy(xpath = "//button[.//adapt-highlight[text()='90 days']]")
    public WebElement NinetyDaysOption;

    @FindBy(xpath = "//button[normalize-space() = 'Execute']")
    WebElement executeButton;

    @FindBy(xpath = "//button[text()='Continue']")
    WebElement continueButton;

    String testPolicyWindow = "//h2[normalize-space()='Test Automation Policy']";
    String responseCode = "//span[normalize-space()= 'responsecode']//ancestor::div[@class='json-view']//span[normalize-space()= '200']";

    @FindBy(xpath = "//div[@class='modal-body']//div[@aria-label='details']")
    WebElement detailsRightElement;

    @FindBy(xpath = "//div[@title='Generic REST Action']")
    WebElement testPolicyAction;

    String testPolicySuccessMessage = "//span[@class='a-toast__details-text']";

    @FindBy(xpath = "//app-test-policy//button[text()='Cancel']")
    WebElement testPolicyCancel;

    String tagWarning= "//span[@data-testid='tag_warning'])";
    String refreshPolicyTest = "//span[@title='Refresh']";
    String apiCallWarning = "//span[contains(text(), 'Api Call In-Progress')]";

    private final WebDriver driver;
    WebDriverWait wait;

    public static int timeout = 15;

    public PoliciesPage() {
        this.driver = getDriver();
        PageFactory.initElements(this.driver, this);
        wait = new WebDriverWait(driver,Duration.ofSeconds(10));

    }
    HistoryPage histroyPage = new HistoryPage();


    public PoliciesPage verifySyncAction(String connectorName) {
        syncAction(connectorName);
        commonWaits.implicitWait(10);
        cancelPolicyButton.isDisplayed();
        wait.until(ExpectedConditions.elementToBeClickable(cancelPolicyButton));
        cancelPolicyButton.click();
        return this;
    }

    /**
     * @param connectorName
     */
    private void syncAction(String connectorName) {
        String syncSuccesfully="//span[normalize-space()='Action synced successfully.']";
        addActionButton.isDisplayed();
        addActionButton.click();
        String connectorPath="//div[contains(text(),\""+connectorName+"\")]";
        WebElement automationTool = driver.findElement(By.xpath(connectorPath));
        automationTool.isDisplayed();
        automationTool.click();
        syncActionButton.isDisplayed();
        try {
            syncActionButton.click();
            commonWaits.waitForPageLoad(driver);
            Wait<WebDriver> Fluentwait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(60))
                    .pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);
            WebElement elementText = Fluentwait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return driver.findElement(By.xpath(syncSuccesfully));							}
            });
            wait.until(ExpectedConditions.elementToBeClickable(elementText));
            action_Sync_Message = elementText.getText();
            Assert.assertEquals(action_Sync_Message.trim(), SyncMessage.trim());
        } catch (Exception e) {
            action_Sync_Message = actionSyncMessage.getText();
            Assert.assertTrue(action_Sync_Message.trim().contains(SyncProgressMessage.trim()));
        }
        try {
            commonWaits.implicitWait(10);
            Actions actions = new Actions(driver);
            wait.until(ExpectedConditions.visibilityOf(defineEventCloseButton));
            defineEventCloseButton.isDisplayed();
            wait.until(ExpectedConditions.elementToBeClickable(defineEventCloseButton));
            //defineEventCloseButton.click();
            actions.moveToElement(defineEventCloseButton).click().perform();
            driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
        } catch (Exception e) {
            //wait.until(ExpectedConditions.visibilityOf(defineEventCloseButton));
            //defineEventCloseButton.isDisplayed();
            //wait.until(ExpectedConditions.elementToBeClickable(defineEventCloseButton));
            //commonWaits.clickToElement(defineEventCloseButton);
            driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);

        }
    }

    public PoliciesPage setPolicyNameAndDescription(String policyName) {
        setPolicyName(policyName);
        descriptionInput.isDisplayed();
        descriptionInput.clear();
        descriptionInput.sendKeys(finalpolicyName + "_Description");
        logger.info("******* Set PolicyName And  Description Successfully   *******");
        return this;
    }

    /**
     * @param policyName
     */
    private void setPolicyName(String policyName) {
        commonWaits.waitForPageLoad(driver);
        commonWaits.loader(driver);
        finalpolicyName = policyName;
        commonWaits.waitForElementVisible(policyInputText);
        Assert.assertTrue(policyInputText.isDisplayed(), "policy name textbox not displayed");
        policyInputText.clear();
        policyInputText.sendKeys(finalpolicyName);
    }

    public PoliciesPage setUserDefinedEvent(String eventJson) {
        try {
            commonWaits.waitForPageLoad(driver);
            eventTypeBrowseButton.isDisplayed();
            eventTypeBrowseButton.click();
            commonWaits.waitForPageLoad(driver);
            defineEventText.isDisplayed();

            // Click on Paste Event JSON
            commonWaits.waitForPageLoad(driver);
            eventJSONOption.click();
            commonWaits.waitForPageLoad(driver);
            JSONFormatTextInput.isDisplayed();
            JSONFormatTextInput.click();
            commonWaits.waitForPageLoad(driver);

            driver.switchTo().activeElement().sendKeys(Keys.ENTER);
            driver.switchTo().activeElement().sendKeys(Keys.BACK_SPACE);
            driver.switchTo().activeElement().sendKeys(Keys.BACK_SPACE);
            driver.switchTo().activeElement().sendKeys(Keys.BACK_SPACE);
            driver.switchTo().activeElement().sendKeys(eventJson);

            commonWaits.waitForPageLoad(driver);
            eventdoneButton.isDisplayed();
            commonWaits.clickToElement(eventdoneButton);

            logger.info("******* Set User Defined Event Successfully *******");
        } catch (Exception e) {
            logger.error("Error in setting User Defined Event: " + e.getMessage(), e);
        }
        return this;
    }


    public PoliciesPage setScheduleTrigger() {
        commonWaits.waitForPageLoad(driver);
        scheduleRadioButton.isDisplayed();
        scheduleRadioButton.click();
        commonWaits.waitForPageLoad(driver);

        try {
            scheduleStartDate.isDisplayed();
            scheduleStartDate.click();
            commonWaits.waitForPageLoad(driver);
            // when start date not selected class does NOT contain 'a3t-datetime--controls-wrapper-active'
            policy_schedule_start_date.isDisplayed();
            String classAttr = policy_schedule_start_date.getAttribute("class");
            logger.info("Date not selected: " +classAttr);
            Assert.assertTrue(classAttr.contains("a3t-datetime--controls-wrapper a3t-datetime--controls-wrapper-active"));
        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }

        String currentDate = getCurrentDay();
        currentDate = currentDate.replaceFirst("^0+(?!$)", "");

        commonWaits.waitForPageLoad(driver);

        // Click on the date element for the current date
        List<WebElement> currentDateElement = driver.findElements(
                By.xpath("//button[not(@disabled)]//div[normalize-space()='" + currentDate + "']")
        );

        try {
            commonWaits.waitForPageLoad(driver);
            commonWaits.clickToElement(currentDateElement.get(0));
            commonWaits.waitForPageLoad(driver);
        } catch (Exception e) {
            commonWaits.waitForPageLoad(driver);
            commonWaits.clickToElement(currentDateElement.get(1));
        }

        startDateApply.isDisplayed();
        startDateApply.click();
        commonWaits.waitForPageLoad(driver);

        // End Date selection
//		onThisDay.isDisplayed();
//		onThisDay.click();
//		commonWaits.waitForPageLoad(driver);
//
//		noEndDay.isDisplayed();
//		noEndDay.click();
        commonWaits.waitForPageLoad(driver);
        logger.info("******* Start and End Date selection Successfully   *******");
        return this;
    }

    public static String getCurrentDay() {
        DateFormat sdf = new SimpleDateFormat("dd");
        Calendar currentDate = Calendar.getInstance();
        currentDate.get(Calendar.DATE);
        Date d = currentDate.getTime();
        String current_Day = sdf.format(d);
        return current_Day;
    }

    public PoliciesPage test_ScheduleRetention() {
        manualRadioButton.isDisplayed();
        manualRadioButton.click();
        commonWaits.waitForPageLoad(driver);

        automaticRadioButton.isDisplayed();
        automaticRadioButton.click();
        commonWaits.waitForPageLoad(driver);

        scheduleRadioButton.isDisplayed();
        scheduleRadioButton.click();
        commonWaits.waitForPageLoad(driver);
        ValidateSchedule();
        return this;
    }



    public PoliciesPage ValidateSchedule() {
        try {
            commonWaits.waitForPageLoad(driver);
            // when start date selected class does NOT contain 'a3t-datetime--controls-wrapper-active'
            policy_schedule_start_date.isDisplayed();
            String classAttr_datevisible = policy_schedule_start_date.getAttribute("class");
            logger.info("Date selected: " +classAttr_datevisible);
            Assert.assertTrue(classAttr_datevisible.contains("input-group a3t-datetime--controls-wrapper"));
            logger.info("Start date/time is retained");
            commonWaits.waitForPageLoad(driver);

            policy_recurring_time.isDisplayed();
            String actualValue = policy_recurring_time.getAttribute("value");
            Assert.assertEquals("2", actualValue);
            logger.info("Recurring time is retained");
            commonWaits.waitForPageLoad(driver);
        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
        return this;
    }


    public PoliciesPage configureChangeManagementSilder(WebElement element, boolean check) {
        try {
            commonWaits.waitForPageLoad(driver);
            element.isDisplayed();
            String status=element.getDomAttribute("aria-checked");
            if(status.equalsIgnoreCase("true")) {
                element.click();
            }
            commonWaits.waitForPageLoad(driver);
        } catch (Exception ignored) {
        }
        return this;
    }


    public PoliciesPage navigateToCreatePoliciesPage() {
        wait.until(ExpectedConditions.elementToBeClickable(createAutomationButton));
        try {
            createAutomationButton.isDisplayed();
            createAutomationButton.click();
            commonWaits.loader(driver);
        }catch(Exception e)
        {
            commonWaits.clickToElement(createAutomationButton);
        }
        return this;
    }




    // Search create Policy
    public PoliciesPage searchPolicy(String finalpolicyName ,int rowCount) {
        searchByPolicyName(finalpolicyName);
        try {
            int rowNum = getSearchedCount();
            Assert.assertEquals(rowNum,rowCount);
        } catch (Exception e) {
            logger.info("No row is present");
        }
        return this;
    }

    //get Searched Count
    public int getSearchedCount() {
        commonWaits.waitForPageLoad(driver);
        commonWaits.loader(driver);
        wait.until(ExpectedConditions.visibilityOf(histroyPage.tableRowNum.get(0)));
        histroyPage.tableRowNum.get(0).isDisplayed();
        int rowNum = histroyPage.tableRowNum.size();
        return rowNum;
    }

    //search By PolicyName
    public PoliciesPage searchByPolicyName(String finalpolicyName) {
        commonWaits.waitForPageLoad(driver);
        commonWaits.loader(driver);
        try {
            boolean ispresent = histroyPage.clearButton.isDisplayed();
            if (ispresent) {
                histroyPage.clearButton.click();
                driver.navigate().refresh();
                commonWaits.waitForPageLoad(driver);
                PageFactory.initElements(driver, this);
            }
        } catch (Exception e) {
            logger.info("Clear button not present");
        }
        commonWaits.waitForPageLoad(driver);
        try {
            histroyPage.policyNameSearchIcon.isDisplayed();
            commonWaits.clickToElement(histroyPage.policyNameSearchIcon);
            commonWaits.waitForPageLoad(driver);
            policyNameSearchBox.sendKeys(finalpolicyName);
            policyNameSearchBox.sendKeys(Keys.ENTER);
            commonWaits.waitForPageLoad(driver);
            commonWaits.loader(driver);
            wait.until(ExpectedConditions.visibilityOf(histroyPage.clearButton));
        }catch(Exception e)
        {
            logger.info("search button not visible ");
        }
        return this;
    }


    public PoliciesPage editIAPolicy(){
        commonWaits.waitForElementClickable(dropdownActionMenu).click();
        commonWaits.waitForElementVisible(edit).click();
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementVisible(policyInputText);
        return this;
    }


    // action DropDown Operations like publish/Copy/Unpublish policy
    public PoliciesPage actionDropDownOperation(WebElement actionElement, WebElement policyStatusLoc, String status) {

        dropDownOperation(actionElement,policyStatusLoc);
        commonWaits.waitForPageLoad(driver);
        commonWaits.getText(policyStatusLoc, status);
        return this;
    }

    // action DropDown Operations like publish/Copy/Unpublish policy
    public PoliciesPage dropDownOperation(WebElement actionElement, WebElement policyStatusLoc) {

        actionONSearchPolicy(actionElement);
        confirmYesButton.isDisplayed();
        confirmYesButton.click();
        try {
            policyStatusLoc.isDisplayed();
            commonWaits.waitForPageLoad(driver);
            //	Thread.sleep(5000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this;
    }
    public PoliciesPage actionONSearchPolicy(WebElement actionElement) {
        commonWaits.implicitWait(15);
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.visibilityOf(dropdownActionMenu));
        dropdownActionMenu.isDisplayed();
        dropdownActionMenu.click();
        //commonWaits.waitForSometime(4000);
        wait.until(ExpectedConditions.visibilityOf(actionElement));
        commonWaits.explicitWaitElementToBeClickable(actionElement, 3);
        commonWaits.waitForPageLoad(driver);
        commonWaits.implicitWait(10);
        actionElement.isDisplayed();
        commonWaits.clickToElement(actionElement);
        try {
            commonWaits.waitForPageLoad(driver);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this;
    }


   public String getPolicyStatus() {
       commonWaits.waitForPageLoad(driver);
       return commonWaits.waitForElementVisible(policyStatusLoc).getText();
   }

    public PoliciesPage searchAction(String actionName ,int rowCount) {
        try {
            boolean ispresent = clearActionButton.isDisplayed();
            if (ispresent) {
                clearActionButton.click();
                driver.navigate().refresh();
                commonWaits.waitForPageLoad(driver);
                PageFactory.initElements(driver, this);
            }
        } catch (Exception e) {
            logger.info("Clear button not present");
        }
        commonWaits.waitForPageLoad(driver);
        searchActionButton.isDisplayed();
        commonWaits.clickToElement(searchActionButton);

        searchActionButton.sendKeys(actionName);
        searchActionButton.sendKeys(Keys.ENTER);
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.visibilityOf(searchActionButton));
        try {
            commonWaits.waitForPageLoad(driver);
            commonWaits.loader(driver);
            histroyPage.tableRowNum.get(0).isDisplayed();

        } catch (Exception e) {
            logger.info("No row is present");
        }
        int rowNum = histroyPage.tableRowNum.size();
        Assert.assertEquals(rowNum,rowCount);
        return this;
    }


    public PoliciesPage clickOnSaveAndConfirm(){
        commonWaits.waitForElementClickable(savePolicyButton).click();
        commonWaits.waitForElementClickable(confirmbutton).click();
        commonWaits.waitForPageReadyState(getDriver());
        return this;
    }



    public PoliciesPage SavePolicy() {
        try {
            //Save Policy
            configureChangeManagementSilder(changeEnableButton, true);
            activeCheckboxInput.click();
            commonWaits.waitForPageLoad(driver);
            wait.until(ExpectedConditions.visibilityOf(savePolicyButton));
            savePolicyButton.click();
            commonWaits.waitForPageLoad(driver);
            logger.info("******* Select Action Successfully   *******");
        } catch (Exception ignored) {
        }
        return this;
    }

    public PoliciesPage publishAndSavePolicy() {
        commonWaits.waitForPageLoad(driver);
        commonWaits.jsClickWithoutScroll(publishPolicyCheckbox);
        wait.until(ExpectedConditions.visibilityOf(savePolicyButton));
        commonWaits.clickWithoutInterception(savePolicyButton);
        commonWaits.waitForPageLoad(driver);
        logger.info("******* Policy Saved and Published Successfully   *******");
        return this;
    }

    public PoliciesPage editAndSavePolicy() {
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.visibilityOf(savePolicyButton));
        commonWaits.clickWithoutInterception(savePolicyButton);
        commonWaits.waitForPageLoad(driver);
        commonWaits.clickWithoutInterception(confirmButton);
        commonWaits.waitForPageLoad(driver);
        logger.info("******* Policy Edited Successfully   *******");
        return this;
    }


    public PoliciesPage testPolicyAndVerifyDetails() {
        try {
            commonWaits.waitForElementClickable(testPolicyButton);
            testPolicyButton.click();
            commonWaits.waitForElementClickable(continueButton);
            continueButton.click();
            Assert.assertTrue(isElementPresent(By.xpath(testPolicyWindow)), "Test Automation Policy window");
            commonWaits.waitForElementClickable(detailsRightElement);
            detailsRightElement.click();
            commonWaits.waitForElementClickable(executebutton);
            executebutton.click();
            for(int i=0; i<9 ; i++) {
                if (isElementPresent(By.xpath(tagWarning))) {
                    commonWaits.waitForElementClickable(refreshPolicyTest, commonWaits.defaultTimeout);
                    click(refreshPolicyTest);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
                    commonWaits.waitForElementClickable(apiCallWarning , commonWaits.defaultTimeout);
                    click(apiCallWarning);
                }
            }
            Assert.assertEquals(commonWaits.waitForElementVisible(findElement(By.xpath(testPolicySuccessMessage))).getText(), SolQAConstants.testExecuteSuccessMessage, "Policynot tested successfully.");
            commonWaits.waitForElementClickable(testPolicyAction);
            testPolicyAction.click();
            commonWaits.scrollToSpecificElement(findElement(By.xpath(responseCode)));
            Assert.assertTrue(isElementPresent(By.xpath(responseCode)), "Rest query is not successful");
            commonWaits.waitForElementClickable(testPolicyCancel);
            testPolicyCancel.click();

            logger.info("******* Select Action Successfully   *******");
        } catch (Exception ignored) {
        }
        return this;
    }







}