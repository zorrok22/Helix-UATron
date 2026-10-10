package pages.ITOM.Bhom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.BasePage;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import util.CommonWaits;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.SolQAConstants;
import util.SolqaLogger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class BhomPage extends BasePage {
    WebDriver driver;
    WebDriverWait wait;
    String appName = null;
    static SolqaLogger log = new SolqaLogger(BhomPage.class.getName());
    CommonWaits commonWaits=new CommonWaits();
    List<String> eventOptionList= Arrays.asList("eventDetail.tab.summary", "eventDetail.tab.internals", "eventDetail.tab.object", "eventDetail.tab.source", "eventDetail.tab.logsAndNotes", "eventDetail.tab.Others");
    //WebElements
    @FindBy(xpath = "//a[@title='Refresh']")
    public WebElement refreshEventDetailsPageBtn;

    @FindBy(xpath = "//a[@id='eventRefreshButton']")
    public WebElement refreshButton;
    
    @FindBy(xpath = "//div[@class='bmc-notification-item']")
	public WebElement incidentCreatedmsg;

    @FindBy(xpath = "//input[@id='txtSearchInp']")
    public WebElement searchEventInput;

    @FindBy(xpath = "//*[@role='gridcell']//*[contains(@title, 'INC')]")
    public WebElement ticketID;

    @FindBy(xpath = "//a[@id='event_action_modifyEventSlotValue']")
    public WebElement modifyEventButton;

    @FindBy(xpath = "//*[contains(@id, 'eventMenu')]")
    public WebElement eventMenuButton;

    @FindBy(xpath = "//a[@id='event_action_im_operations.Close']")
    public WebElement closeEventButton;

    @FindBy(xpath = "//select[@id='slotDropdown']")
    public WebElement ticketEventTypeDropdown;

    @FindBy(xpath = "//option[text()='Priority']")
    public WebElement ticketEventTypePriorityOption;

    @FindBy(xpath = "//button[contains(. ,'OK')]")
    public WebElement okButton;

    @FindBy(xpath = "//*[text()='Priority']/ancestor::label-desc/following-sibling::div//button[@id='add-slot-priority-input_button']")
    public WebElement priorityDropdownButton;

    @FindBy(xpath = "//span[text()='Monitoring']//parent::button")
    public WebElement monitoringDropdown;

    @FindBy(xpath = "//*[@id='row1eventGrid']/div[6]")
    public WebElement OccuredTime;

    @FindBy(xpath = "//*[@id='row1eventGrid']/div[6]")
    public WebElement OccuredTimeincidentId;

    @FindBy(xpath = "//*[@id='304440891']")
    public WebElement save_button;

    @FindBy(xpath = "//*[@id='eventDetail.tab.Others']")
    public WebElement Other_event;

    @FindBy(xpath = "//*[@id='incident_idValue']")
    public WebElement incident_id;

    @FindBy(xpath = "//span[text()='Configuration']//parent::button")
    public WebElement ConfigurationDropdown;

    @FindBy(xpath = "//span[text()='Alarm Policies']//parent::button")
    public WebElement alarmPoliciesMenu;

    @FindBy(xpath = "//span[text()='Events']//parent::button")
    public WebElement eventsMenu;

    @FindBy(xpath = "//span[text()='Blackout Policies']//parent::button")
    public WebElement blackoutPoliciesMenu;

    @FindBy(xpath = "//input[@id='txtSearchInp']")
    public WebElement searchMessageText;

    @FindBy(xpath = "//a[text()='View Event Details']")
    public WebElement viewEventDetailsButton;

    @FindBy(xpath = "//a[@id='eventDetail.tab.Others']")
    public WebElement othersTabOnEvent;

    @FindBy(xpath = "//a[@id='eventDetail.tab.internals']")
    public WebElement internalsTabOnEvent;

    @FindBy(xpath = "//*[@id='_identifierValue']")
    public WebElement internalIDValue;

    @FindBy(xpath = "//*[@id='incident_assigneeValue']")
    public WebElement incidentAssigneeValue;

    @FindBy(xpath = "//div[@id='eventMenu']")
    public WebElement menuOptionOnEventDetailsPage;

    @FindBy(xpath = "//button[.='OK']")
    public WebElement okButtonAfterPerformingActions;

    @FindBy(xpath = "//div[@id='evDetailHeaderEvMsg']")
    public WebElement messageHeaderValue;

    @FindBy(xpath = "//span[text()='Groups']//parent::button")
    public WebElement GroupsMenu;

    @FindBy(xpath = "//span[text()='Create']//parent::button")
    public WebElement CreateGroups;

    @FindBy(xpath = "(//input[@id='criteria-rows-typeahead-input'])[1]")
    public WebElement SelectionQuery;

    @FindBy(xpath = "(//input[@id='criteria-rows-typeahead-input'])[2]")
    public WebElement EventSelectionQuery;

    @FindBy(xpath="//*[@id='tagConfigSaveBtn']")
    public WebElement  SaveButton;

    @FindBy(xpath = "//button[@id='entitySelectionCriteriaPreview']")
    public WebElement previewBtn;

    @FindBy(xpath = "//button[contains(@class,'entity-count-link')]")
    public WebElement entityCountLink;

    @FindBy(xpath = "//*[@id='tags.impacted.devices']")
    public WebElement previewDeviceCount;

    @FindBy(xpath = "//*[@id='tags.impacted.events']")
    public WebElement previewEventCount;

    @FindBy(xpath = "//button[@id='createTagConfigButton']")
    public WebElement createGroupBtn;

    @FindBy(xpath = "//h1[@id='tagName']")
    WebElement groupDetailName;

    @FindBy(xpath = "//h1[@id='tagTotalEventsCount']")
    WebElement groupDetailTotalEventsCount;

    @FindBy(xpath = "//span[@id='tagDevicesCount']/preceding-sibling::span")
    WebElement groupDetailDevicesCount;

    @FindBy(xpath = "//span[@id='tagSelectionCriteria']")
    WebElement groupDetailEntityCriteria;

    @FindBy(xpath = "//span[@id='eventCriteriaQuery']")
    WebElement groupDetailEventCriteria;

    @FindBy(xpath = "//li[@heading='Events']/a")
    WebElement groupDetailEventsTab;

    @FindBy(xpath = "//li[@heading='Devices']/a")
    WebElement groupDetailDevicesTab;

    @FindBy(xpath = "//span[@id='eventsCount']")
    WebElement eventsTabTotalCount;

    @FindBy(xpath = "//span[@id='allDevices']")
    WebElement devicesTabAllDevicesCount;

    @FindBy(xpath = "//device-tile-view//div[contains(@class,'tile device-tile')]")
    List<WebElement> deviceTileElements;

    @FindBy(xpath = "//div[@id='eventGrid']//a[contains(@href,'#/monitoring/events/')]")
    List<WebElement> eventGridRowLinks;

    @FindBy(xpath = "//span[text()='Agents']//parent::button")
    WebElement agentsMenu;

    @FindBy(xpath = "//h1[@id='Agents_id']")
    WebElement agentsPageTitle;

    @FindBy(xpath = "//span[@id='totalAgentsCount']")
    WebElement totalAgentsCount;

    @FindBy(xpath = "//span[starts-with(@id,'name-')]//span[contains(@class,'px-1')]")
    List<WebElement> agentNameCells;

    @FindBy(xpath = "(//span[@class='ml-2 ng-star-inserted' or @id='cdk-drop-list-1'])[1]")
    public WebElement Publish_Status;

    @FindBy(xpath ="//*[@id='rx-select-312_popup-3']")
    public WebElement fetch_pendingStatus;

    @FindBy(xpath = "//*[@id='incident_statusValue']")
    public WebElement Incident_status;

    public String group_publish_dropdown =("//*[@id='0_dropdownMenu']");

    @FindBy(xpath="//*[@id='dropdown__action-0']/span")
    public WebElement Edit;

    @FindBy(xpath="//input[@id='publishedAsService']/following-sibling::div[contains(. ,'Publish as Service')]")
    public WebElement  PublishAsService;

    @FindBy(xpath = "//*[@class='btn btn-secondary adapt-rx-search__search-button ng-star-inserted']")
    public WebElement click_search;

    @FindBy(xpath = "//*[text()='Devices']//parent::button")
    public WebElement devicesMenu;

    @FindBy(xpath = "//*[@id='Device_search_inputbox']")
    public WebElement deviceSearchInputBox;

    @FindBy(xpath = "//input[@id='Monitor_search_inputbox']")
    public WebElement monitorSearchInputBox;

    @FindBy(xpath = "//am4-category-value-chart")
    public WebElement monitoringDataGraph;

    @FindBy(xpath = "//*[@title='Blackout' and contains(@class, 'fi-event-blackout')]")
    public WebElement blackoutEventStatus;

    @FindBy(xpath = "//input[@placeholder='Search by policy name']")
    public WebElement searchBlackoutPolicyInput;

    @FindBy(xpath = "//*[@id='enable-disable-status-cell']")
    public WebElement blackoutPolicyStatusCell;

    @FindBy(xpath = "//button[@data-testid='events-advanced-filter-id_menu']")
    public WebElement advancedFilterButton;

    @FindBy(xpath = "//button[@data-testid='events-advanced-filter-id_Clear']")
    public WebElement clearFilterButton;

    @FindBy(xpath = "//button[contains(. ,'Selection Criteria')]")
    public WebElement selectionCriteriaButton;

    @FindBy(xpath = "//input[@id='criteria-rows-typeahead-input']")
    public WebElement selectionCriteriaInput;

    @FindBy(xpath = "//button[@data-testid='events-advanced-filter-id_footer_apply']")
    public WebElement applyFilterButton;

    @FindBy(xpath = "//adapt-tag//span[@data-testid='tag_active']")
    public WebElement activeFilterTag;

    @FindBy(xpath = "//input[@type='checkbox']/ancestor::adapt-row-selection-checkbox")
    public WebElement blackoutPolicyCheckbox;

    @FindBy(xpath = "//adapt-button[@id='blackoutPolicy.list.disable']")
    public WebElement disableBlackoutPolicyButton;

    @FindBy(xpath = "//button[normalize-space()='Yes']")
    public WebElement confirmPopupYesButton;

    @FindBy(xpath = "//adapt-rx-search")
    WebElement searchGroup;

    @FindBy(xpath = "//span[text()='Administration']//parent::button")
    public WebElement administrationDropdown;

    @FindBy(xpath = "//span[text()='Authorization Profiles']//parent::button")
    public WebElement authorizationProfilesMenu;

    @FindBy(xpath = "//input[@id='profileName']")
    public WebElement authorizationProfileNameInput;

    @FindBy(xpath = "//span[text()='Create']//parent::button")
    public WebElement createButton;

    @FindBy(xpath = "//a[@id='USERGROUP-add']")
    public WebElement addUserGroupButton;

    @FindBy(xpath = "//input[@placeholder='Enter a user group...']")
    public WebElement userGroupSearchInput;

    public String Close_event = "//*[@id='event_action_im_operations.Close']";

    @FindBy(xpath = "//*[@id='row0eventGrid']//*[contains(@class, 'fi-event')]")
    public WebElement eventStatusIcon;

    @FindBy(xpath = "//*[@id='evDetailHeaderEvMsg']")
    WebElement eventDetailsTitle;

    @FindBy(xpath = "//div[@id='evDetailHeaderEvMsg']")
    WebElement eventNameHeader;
    @FindBy(xpath = "//a[@id='eventSearchActionButton']/parent::button")
    WebElement searchBtn;
    @FindBy(xpath = "//a[@role='tab' and contains(. ,'Others')]")
    WebElement othersTab;
    @FindBy(xpath = "(//*[text()='Incident ID']/following-sibling::*)[1]")
    WebElement incidentID;
    
  
    @FindBy(xpath = "//div[@id='col_incident_id']")
    WebElement incidentIDCol;
    
    @FindBy(xpath = "(//*[text()='Incident ID']//..//a[contains(@class,'triangle_down')]")
    WebElement incidentIDColASC;
  
    @FindBy(xpath = "(//*[text()='Priority']/following-sibling::*)[1]")
    WebElement priorityFieldValue;
    @FindBy(xpath = "//*[contains(@id, 'severity') and not(contains(@title, 'Information'))]/ancestor::*[@role='row']//a[contains(@href,'#/monitoring/events/')]")
    WebElement firstEventLink;
    
    @FindBy(xpath = "//div[@id='severityValue' and contains(@class,'ellipsis')]")
    WebElement severityField;
  
    @FindBy(xpath = "//div[@id='statusValue' and @title='Open']")
    WebElement statusField;
  
    @FindBy(xpath = "//div[@id='classValue']")
    WebElement classField;
  
    @FindBy(xpath = "//div[@id='msgValue']")
    WebElement messageField;
  
    @FindBy(xpath = "//div[@id='_modified_timeValue']")
    WebElement occurredField;
    
    @FindBy(xpath = "//label[@id='CRITICAL_labelId']")
	public WebElement critical;    
    @FindBy(xpath = "//label[@id='INFO_labelId']")
		    public WebElement information;    
    @FindBy(xpath = "//label[@id='ACK_labelId']")
    public WebElement ACK_labelId;    
    @FindBy(xpath = " //*[contains(@id, 'row0eventGrid')]//div[@columnindex='1']")
    public WebElement rowActionBtn;  
    @FindBy(xpath = "  //a[text()='Create Incident']")
    public WebElement createIncident;
    @FindBy(xpath = "//span[@id='INFO']")
    		  public WebElement incidentCount;
    
    @FindBy(xpath = "((//tbody/tr/td)[3]//span)[2]")
    WebElement publishedStatus;
    
    @FindBy(xpath = "//app-tag-tile-view//div[contains(@class,'card-custom-wrapper')]")
    WebElement groupTile;
    @FindBy(xpath = "//a[@id='monitorViewTypeSelector']")
    WebElement monitorViewTypeSelector;
    @FindBy(xpath = "//button[contains(text(),'Tile View')]")
    WebElement tileViewOption;
  
    @FindBy(xpath = "//button[@id='createAuthProfileButton']")
    WebElement createAuthProfileButton;
  
    @FindBy(xpath = "//input[@name='profileName' and @placeholder='Enter a unique name.']")
    WebElement authProfileNameInputBox;
  
    @FindBy(xpath = "//a[@id='USERGROUP-add']")
    WebElement addUserGroup;
  
    @FindBy(xpath = "//a[text()='Objects']")
    WebElement objectsTab;
  
    @FindBy(xpath = "//a[@id='DSM_SERVICE-add']")
    WebElement addSolutionButton;
  
    @FindBy(xpath = "//button/span[text()='Save']")
    WebElement saveAuthProfileButton;
  
    @FindBy(xpath = "//div[text()='Authorization Profile is created successfully.']")
    WebElement authProfileSuccessMsg;
  
    @FindBy(xpath = "//a[@id='edit_object_action_menu']")
    WebElement editObjectActionMenu;
  
    @FindBy(xpath = "//a[@id='edit_object_authprofile']//span[text()='Edit']")
    WebElement editObjectAction;
    
    @FindBy(xpath = "//button[text()='Save']")
    WebElement saveEditAuthProfileButton;
 
    @FindBy(xpath = " //button[@id='delete-role-yes']")
    WebElement editConfirmationButton;
    
    @FindBy(xpath = "//div[text()='Authorization Profile is updated successfully.']")
    WebElement editAuthProfileSuccessMsg;
    

    @FindBy(xpath = "//span[text()='Event Policies']//parent::button")
    WebElement eventPoliciesMenu;

    @FindBy(xpath = "//button[@id='createEventPolicyButton']")
    WebElement createEventPolicyBtn;

    @FindBy(xpath = "//h1[text()='Create Event Policy']")
    WebElement createEventPolicyPageTitle;

    @FindBy(xpath = "//input[@id='txtPolicyName']")
    WebElement eventPolicyNameInput;

    @FindBy(xpath = "//criteria-multi-rows[@id='eventsCriteriaSelection']//input[@id='criteria-rows-typeahead-input']")
    WebElement eventPolicyCriteriaInput;

    public String eventPolicyTypeaheadOption = "//button[@role='option' and contains(.,'%s')]";

    @FindBy(xpath = "//button[@id='policyConfigurationsDropdown']")
    WebElement policyConfigDropdown;

    @FindBy(xpath = "//input[contains(@class,'checkbox__input') and @id='policyStatus']")
    WebElement enablePolicyCheckbox;

    @FindBy(xpath = "//adapt-rx-checkbox[@id='policyStatus']")
    WebElement enablePolicyWrapper;

    @FindBy(xpath = "//div[@class='cdk-overlay-container']//button[contains(.,'Save')]")
    WebElement suppressionSettingsSaveBtn;

    @FindBy(xpath = "//adapt-accordion-tab//span[text()='Suppression']")
    WebElement suppressionAccordionTab;

    @FindBy(xpath = "//div[contains(text(),'Policy is created successfully')]")
    WebElement successToastNotification;

    @FindBy(xpath = "//input[@id='notificationSendTo_search']")
    WebElement notificationSendToInput;

    @FindBy(xpath = "//textarea[@id='notificationSubject-input']")
    WebElement notificationSubjectInput;

    @FindBy(xpath = "//textarea[@id='notificationText-input']")
    WebElement notificationMessageInput;

    @FindBy(xpath = "//input[contains(@class,'checkbox__input') and @id='chkSeverity']")
    WebElement eventSeverityCheckbox;

    @FindBy(xpath = "//adapt-rx-checkbox[@id='chkSeverity']")
    WebElement eventSeverityCheckboxWrapper;

    @FindBy(xpath = "//button[@id='select-chkOperator']")
    WebElement severityOperatorDropdown;

    @FindBy(xpath = "//button[@id='select-chkSeverity']")
    WebElement severityValueDropdown;

    @FindBy(xpath = "//button[@id='saveConfigurationBtn']")
    WebElement previewAndSaveBtn;

    @FindBy(xpath = "//adapt-accordion-tab//span[text()='Notification']")
    WebElement notificationAccordionTab;

    @FindBy(xpath = "//div[@class='cdk-overlay-container']//button[normalize-space(.)='Save']")
    WebElement notificationPreviewSaveBtn;

    @FindBy(xpath = "//a[@title='Acknowledge Event']")
    WebElement acknowledgeEvent;

    @FindBy(xpath = "//a[@title='Assign Event...']")
    WebElement assignEvent;

    @FindBy(xpath = "//input[@id='assign-user-list']")
    WebElement assignUserList;

    @FindBy(xpath = "//button[contains(@class,'btn-primary') and normalize-space()='OK']")
    WebElement approveOkButton;
   
    @FindBy(xpath = "//a[@title='Close Event']")
    WebElement closeEvent;

    @FindBy(xpath = "//a[contains(@href, 'events')][1]")
    WebElement firstEventDetails;

    @FindBy(xpath = "//div[@id='contenttablemonitorHierarchyGrid']//td")
    WebElement errorMessage;
    // String locators
    public String firstDisplayedEvent ="//div[contains(.,'%s')]";
    public String originalEvent = "//div[(@title='%s')]";
    public String actionsOnEvent ="//a[contains(text(),'%s')]";
    public String Status_reason ="//*[@id='rx-select-400']/div[1]";
    public String priorityDropdownOption = "//*[text()='%s']/ancestor::button[@role='option']";
    public String Status = "//*[@id='304424821']/div/span[1]";
    public String fetch_status = "//*[@id='rx-select-723']/div/div";
    public String change_Status ="//*[@id='rx-select-312']";
    public String incident_status="//*[@id='rx-select-312']";
    public String Related_items_btn = "//*[contains(text(),'Related items')]";
    public String viewFullIncident =("//*[@id='304428981_data']");
    public String Publish_as_Service = ("//*[@id='dropdown__action-3' or contains (text(),'Publish as Service')]");
    public String group_publish_btn="//*[@id='group-publish-btn']";
    public String Group_publish_done_btn =("//*[@id='group-publish-done-btn']");
    public String GroupDescription = "//*[@class='form-control rx-form-control form-control-sm']";
    public String search_unpublished_group = ("//adapt-rx-search//input[@id='deviceFilterInputBox']") ;
    public String operationsManagementButton = ("//button[contains(.,'BMC Helix Operations Management')]");
    public String GroupName="//adapt-rx-control-label[contains(. ,'Group Name')]/following-sibling::div//input";
    public String Services = ("//span[text()='Services']/parent::a");//("//*[@id='adapt-menu-6']/div/div/a/span");
    public String Search_Services = ("//input[@id='search-text-box']");
    public String EventName = ("//*[@style='fill:#11845b']/following-sibling::*");
    public String Fetch_severity = "//*[@id='CRITICAL']";
    public String Search_event = "//input[@id='txtSearchInp']";
    public String backButton = "//a[@id='detailsBackButton']";
    public String incidentMenu = "//*[@id='eventMenuButton0']";
    public String incidentMenuDuplicate = "//*[@id='eventMenuButton1']";
    public String Close_Event = "//a[contains(text(),'Close Event')]";
    public String OK_button = "//*[@id='getNoteDialog']/div/div[3]/button[1]/span";
    public String linkByName = "//a[normalize-space()='%s' or contains(text(),'%s')]";
    public String contact_name = "//*[@id='304419231']";
    public String Deleter_event = "//div[@id='eventMenuButton2']";
    public String Close_event_ok_button = "//*[@id='getNoteDialog']/div/div[3]/button[1]/span";//*[@id='NotPossibleOperationOnEvent']/div/div[3]/button[1]/span";
    public String clearSearch = "//*[@id='eventSerchTextId']";
    public String firstEventMenu = "//div[.='%s']/..//div[@columnindex='1']";
    public String triggerAutomation = "//div/a[@id='event_action_show_trigger_automation_anchor']";
    public String runThisAutomation = "//span[.='Run this Automation']";
    public String successfullyExecution = "//div[.='Automation policy execution request is submitted successfully.']";
    public String successStatus="//span[contains(.,'Successful')]";
    public String TotalEventCount="//div/span[@id='eventsCount']";
    public String openFirstEvent = "(//a[contains(@href,'#/monitoring/events/')])";
    public String occurredDateLink = "//*[@id='row1eventGrid']//div[@columnindex='7']";
    public String eventDetails = "//h1[text()='Event Details']";
    public String relatedEventsTab = "//li/a[@id='eventDetail.tab.relatedEvents']";
    public String operationsNameLocator = "(//adapt-navigation//nav//span[contains(@class,'product__name')])[2]";
    public String EventSearchResultByMessage = "//*[contains(@class, 'text-info') and @title='%s']";
    public String deviceName = "//device-tile-view//a[contains(text(),'%s')]";
    public String tabLink = "//a[normalize-space()='%s']/parent::li";
    public String monitorName = "//div[@ng-show='showTilesView']//a[@title='%s']";
    public String policyColumnName = "//th//span[contains(@class, 'c-header__separator')]";
    public String policyColumnValue = "//td[@data-testid='adapt-table_column-%s_row-0']";
    public String openEventStatus = "//*[@title='Open' and contains(@class, 'event-open')]";
    public String blackoutStatusEvents = "//*[@title='Blackout' and contains(@class, 'event-blackout')]";
    public String tableRowPath ="//*[contains(@id, 'row')]";
    String tabName = "//a[@id='%s']";
    String metricOption="//button[@role='option' and contains(. ,'%s') and contains(. ,'%s') and contains(. ,'Health')]";
    public String userGroupCheckbox ="//label[@id='%s']//input";
    public String authProfileMenu ="//span[@id='menu%s']";
    public String authProfileEdit = "//a[contains(@id,'edit%s')]//span[text()='Edit']";
    public String authProfileDelete = "//a[contains(@id,'delete%s')]//span[text()='Delete']";
    public String tabNameByText = "//a[text()='%s']";
    public String checkboxesXpath = "//input[@type='checkbox' and @name='objectChk']";
    public String groupDetailNameByTitle = "//h1[@id='tagName' and contains(@title,'%s')]";
    public String enablePolicyCheck = ".//input[@type='checkbox']";
    String checkboxStatusXpath = "(//*[contains(normalize-space(),'%s')]/span)[1]";
    String checkboxXpath = "//*[contains(normalize-space(),'%s')]/span[contains(@class,'fi fi-checked')]";
    String countXpath = "//*[contains(normalize-space(),'%s')]/following-sibling::span";
    String device_Name = "//a[@title='%s']";
    // String variables
    public String Opening_Bracket = "(";
    public String AgentHostName = "Agent Host Name";
    public String Contains = "Contains";
    public String Severity = "Severity";
    public String Equals = "Equals";
    public String Major = "Major";
    public String Class_slot = "Class";
    public String Closing_bracket = (")");
    String entityCriteriaRowLabel = "//criteria-rows[@id='entitySelectionCriteriaControl']//label[contains(text(),'%s')]";
    String eventCriteriaRowLabel = "//criteria-rows[@id='eventsCriteriaSelection']//label[contains(text(),'%s')]";
    public String Message = "Message";
    public String policyConfigOption = "//button[contains(.,'%s') and @role='option']";
    public String eventPolicyCriteriaRowLabel = "//criteria-rows[contains(@id,'eventsCriteriaSelection')]//label[contains(text(),'%s')]";
    public String notificationSendToTagText = "//adapt-metatag[@id='notificationSendTo']//span[contains(text(),'%s')]";
    public String userGroupCheckboxXpath = "//label[@id='%s']";

    @FindBy(xpath = "//criteria-rows[@id='entitySelectionCriteriaControl']//input[@aria-expanded='true']")
    WebElement entityTypeaheadExpanded;

    @FindBy(xpath = "//criteria-rows[@id='eventsCriteriaSelection']//input[@aria-expanded='true']")
    WebElement eventTypeaheadExpanded;

    @FindBy(xpath = "//div[contains(@class,'preview-summary-container')]")
    WebElement previewSummaryContainer;

    @FindBy(xpath = "//button[@id='okButton']")
    WebElement OkButton;

    @FindBy(xpath = "//li[@heading='Objects']//a")
    WebElement objectLink;

    @FindBy(xpath = "//li[@heading='Devices']//a")
    WebElement devicesLink;

    @FindBy(xpath = "//a[@id='DEVICE-add']")
    WebElement addDeviceButton;

    @FindBy(xpath = "//button[@id='searchObjects']")
    WebElement searchObjectsButton;

    @FindBy(xpath = "//input[@ng-checked='object.selected']/parent::label")
    List<WebElement> selectAllCheckbox;

    @FindBy(xpath = "(//input[@ng-checked='object.selected']/parent::label)[1]")
    WebElement selectAllCheckbox1;

    @FindBy(xpath = "//span[contains(@ng-if,'object.name')]")
    List<WebElement> devicesNames;

    @FindBy(xpath = "//button[@id='saveButtonAuthProfile']")
    WebElement saveConfigurationBtn;

    @FindBy(xpath = "//div[contains(@class,'bmc-notification-title') and normalize-space()='Success']")
    WebElement successNotification;

    @FindBy(xpath = "//span[@id='idDeviceName']")
    WebElement openedDeviceName;

    @FindBy(xpath = "//a[@class='text-truncate d-block']")
    List<WebElement> restrictedDeviceNames;
    public String random_number;
    public String cluster_name;
    private String previewDevices;
    private String previewEvents;

  

    String sEventCreateIncidentAction = "Create Incident";

    public BhomPage() {
        this.driver=getDriver();
        PageFactory.initElements(this.driver,this);
        wait=new WebDriverWait(this.driver, Duration.ofSeconds(60));
    }

    public void verifyBHOMLaunchedSuccessfully(String applicationName){
        commonWaits.waitForElementVisible_withFluentWait(driver,operationsNameLocator,50);
        appName = driver.findElement(By.xpath(operationsNameLocator)).getText().trim();
        log.info("----- Opened application name - "+ appName);
        Assert.assertTrue(applicationName.contains(appName), "Operations Management application is not launched successfully");
    }

    public String searchForGroupAndRetrieveItsPublishedStatus(String groupName){
        String status = "";
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(search_unpublished_group))).click();
        findElement(By.xpath(search_unpublished_group)).sendKeys(groupName);
        findElement(By.xpath(search_unpublished_group)).sendKeys(Keys.ENTER);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementVisible(publishedStatus);
        status = (publishedStatus).getText();
        return status;
    }

    public BhomPage navigateToGroupTab() {
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementVisible(ConfigurationDropdown);
        commonWaits.waitForElementClickable(ConfigurationDropdown);
        ConfigurationDropdown.click();
        commonWaits.waitForElementVisible(GroupsMenu);
        commonWaits.waitForElementClickable(GroupsMenu);
        GroupsMenu.click();
        commonWaits.waitForPageReadyState(getDriver());
        return this;
    }

    public String navigateToAgentsAndGetCommonHostnameSubstring() {
        log.info("Navigating to Configuration -> Agents page");
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementVisible(ConfigurationDropdown).click();
        commonWaits.waitForElementVisible(agentsMenu).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementVisible(agentsPageTitle);
        commonWaits.waitForElementVisible(totalAgentsCount);

        List<String> hostnames = new ArrayList<>();
        for (WebElement el : agentNameCells) {
            String name = el.getAttribute("title").trim();
            if (name.contains(":")) {
                name = name.substring(0, name.lastIndexOf(":"));
            }
            if (name.contains(".")) {
                name = name.substring(0, name.indexOf("."));
            }
            hostnames.add(name);
        }
        log.info("Found " + hostnames.size() + " agent hostnames on Agents page");

        String bestSubstring = "";
        int bestCount = 0;

        for (String hostname : hostnames) {
            String[] parts = hostname.split("-");
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) prefix.append("-");
                prefix.append(parts[i]);
                String currentPrefix = prefix.toString();
                if (currentPrefix.length() < 3) continue;
                int count = 0;
                for (String h : hostnames) {
                    if (h.contains(currentPrefix)) count++;
                }
                if (count > 1 && (count > bestCount || (count == bestCount && currentPrefix.length() > bestSubstring.length()))) {
                    bestCount = count;
                    bestSubstring = currentPrefix;
                }
            }
        }

        log.info("Common hostname substring: '" + bestSubstring + "' found in " + bestCount + " agent names");
        Assert.assertTrue(bestCount > 1, "Could not find a common substring present in more than 1 agent name");
        return bestSubstring;
    }

    public BhomPage navigateToMonitoringGroup() {
        commonWaits.waitForPageReadyState(getDriver());
        commonWaits.waitForElementVisible(monitoringDropdown);
        commonWaits.clickWithoutInterception(monitoringDropdown);
        commonWaits.waitForElementVisible(GroupsMenu);
        commonWaits.clickWithoutInterception(GroupsMenu);
        commonWaits.waitForPageReadyState(getDriver());
        return this;
    }

    public Boolean checkIfGroupIsListed(String groupName){
        navigateToMonitoringGroup();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForElementVisible(searchGroup);
        commonWaits.clickWithoutInterception(searchGroup);
        Actions actions = new Actions(driver);
        actions.sendKeys(groupName+Keys.ENTER).perform();
        commonWaits.waitForPageReadyState(getDriver());
        try {
            commonWaits.waitForElementVisible(groupTile);
        }catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    public BhomPage openGroupAndVerifyDetails(String groupName, String expectedDeviceCount, String expectedEventCount, String entityHostName, String eventClassName) {
        log.info("Clicking on group tile to open Group Details page");
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.clickWithoutInterception(groupTile);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForPageReadyState(getDriver());
        WebElement groupNameElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(groupDetailNameByTitle, groupName)));
        String actualGroupName = groupNameElement.getAttribute("title").trim();
        log.info("Group Details page loaded for: " + actualGroupName);
        Assert.assertTrue(actualGroupName.contains(groupName),
                "Expected group name containing '" + groupName + "' but got: " + actualGroupName);

        String actualDeviceCount = commonWaits.waitForElementVisible(groupDetailDevicesCount).getText().trim();
        log.info("Group Details Devices: " + actualDeviceCount + ", Preview Devices: " + expectedDeviceCount);
        Assert.assertEquals(actualDeviceCount, expectedDeviceCount,
                "Device count mismatch between Preview and Group Details");

        String actualEventCount = commonWaits.waitForElementVisible(groupDetailTotalEventsCount).getText().trim();
        log.info("Group Details Events: " + actualEventCount + ", Preview Events: " + expectedEventCount);
        Assert.assertEquals(actualEventCount, expectedEventCount,
                "Event count mismatch between Preview and Group Details");

        String entityCriteriaText = commonWaits.waitForElementVisible(groupDetailEntityCriteria).getText().trim();
        log.info("Entity Selection Criteria: " + entityCriteriaText);
        Assert.assertTrue(entityCriteriaText.contains(entityHostName),
                "Entity criteria does not contain '" + entityHostName + "', actual: " + entityCriteriaText);

        String eventCriteriaText = commonWaits.waitForElementVisible(groupDetailEventCriteria).getText().trim();
        log.info("Event Selection Criteria: " + eventCriteriaText);
        Assert.assertTrue(eventCriteriaText.contains(eventClassName),
                "Event criteria does not contain '" + eventClassName + "', actual: " + eventCriteriaText);

        log.info("Clicking Events tab to verify associated events");
        commonWaits.clickWithoutInterception(groupDetailEventsTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForPageReadyState(getDriver());
        String eventsGridTotal = commonWaits.waitForElementVisible(eventsTabTotalCount).getText().trim();
        log.info("Events tab Total count: " + eventsGridTotal + ", Preview Events: " + expectedEventCount);
        Assert.assertEquals(eventsGridTotal, expectedEventCount,
                "Events tab Total count does not match Preview event count");

        if (Integer.parseInt(expectedEventCount) > 0) {
            log.info("Verifying event rows are displayed in the Events grid");
            Assert.assertTrue(eventGridRowLinks.size() > 0,
                    "Expected event rows to be displayed but none found in the Events grid");
            log.info("Events grid displays " + eventGridRowLinks.size() + " event row(s)");

            log.info("Clicking first event to verify navigation to Event Details page");
            commonWaits.clickWithoutInterception(eventGridRowLinks.get(0));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForByElementToBeVisible(By.xpath(eventDetails));
            log.info("Event Details page loaded successfully");

            log.info("Navigating back to Group Details page");
            commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(backButton))).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(groupDetailNameByTitle, groupName)));
            log.info("Navigated back to Group Details page successfully");
        }

        log.info("Clicking Devices tab to verify associated devices");
        commonWaits.clickWithoutInterception(groupDetailDevicesTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForPageReadyState(getDriver());
        int allDevicesTotal = deviceTileElements.size();
        log.info("Devices tab All Devices count: " + allDevicesTotal);
        Assert.assertEquals(String.valueOf(allDevicesTotal), expectedDeviceCount,
                "Devices tab All Devices count does not match Preview device count");
		return this;
    }

    public void Publish_as_service(){
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(SaveButton);
        commonWaits.clickWithoutInterception(By.xpath(search_unpublished_group));
        commonWaits.waitForByElementToBeVisible(By.xpath(search_unpublished_group)).sendKeys(random_number);
        commonWaits.waitForElementVisible_withFluentWait(driver,group_publish_dropdown,50);
        commonWaits.waitForByElementToBeVisible(By.xpath(group_publish_dropdown)).click();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(Edit);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.javaScriptScrollIntoView(PublishAsService);
        commonWaits.clickWithoutInterception(PublishAsService);
        commonWaits.clickWithoutInterception(SaveButton);
    }

    public void CreateGroupWithEntity(String groupName, String hostName) {
        random_number = generateRandomString();
        cluster_name =  random_number;
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.elementToBeClickable(CreateGroups));
        CreateGroups.click();
        commonWaits.waitForElementVisible_withFluentWait(driver, GroupName, 100);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(GroupName)));
        commonWaits.clickWithoutInterception(ele);
        ele.sendKeys(groupName);
        commonWaits.waitForByElementToBeVisible(By.xpath(GroupDescription)).sendKeys(cluster_name);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Opening_Bracket);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(AgentHostName);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Contains);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(hostName);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Closing_bracket);
        wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).sendKeys(Keys.ENTER);
        String selectionqueryfetch =  wait.until(ExpectedConditions.elementToBeClickable(SelectionQuery)).getText();
        //log.info("selectionqueryfetch"+selectionqueryfetch);
    }

    public void CreateGroupWithEvent() {
        random_number = generateRandomString();
        cluster_name =  random_number;
        commonWaits.waitForPageLoad(driver);
        wait.until(ExpectedConditions.elementToBeClickable(CreateGroups));
        CreateGroups.click();
        commonWaits.waitForElementVisible_withFluentWait(driver, GroupName, 100);
        WebElement elem = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(GroupName)));
        commonWaits.clickWithoutInterception(elem);
        elem.sendKeys(random_number);
        commonWaits.waitForByElementToBeVisible(By.xpath(GroupDescription)).sendKeys(cluster_name);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Opening_Bracket);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        //log.info("*********  Entity Selection Criteria  *******");
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Severity);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Equals);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Major);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Closing_bracket);
        wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).sendKeys(Keys.ENTER);
        String selectionqueryfetch =  wait.until(ExpectedConditions.elementToBeClickable(EventSelectionQuery)).getText();
        //log.info(selectionqueryfetch+"selectionqueryfetch");
    }

    public String createGroupWithEntityAndEvent(String groupNamePrefix, String entityFieldName, String entityOperator, String entityHostName, String eventFieldName, String eventOperator, String eventClassName) {
        int randomNum = new Random().nextInt(90000) + 10000;
        String groupName = groupNamePrefix + randomNum;
        log.info("Creating group: " + groupName + " with entity hostname: " + entityHostName + " and event class: " + eventClassName);

        commonWaits.waitForElementClickable(createGroupBtn).click();
        commonWaits.waitForElementVisible_withFluentWait(driver, GroupName, 100);
        WebElement groupNameInput = commonWaits.waitForByElementToBeVisible(By.xpath(GroupName));
        commonWaits.clickWithoutInterception(groupNameInput);
        groupNameInput.sendKeys(groupName);

        log.info("Filling Entity Selection Criteria");
        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Opening_Bracket);
        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(entityCriteriaRowLabel, Opening_Bracket)));

        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(entityFieldName);
        commonWaits.waitForElementVisible(entityTypeaheadExpanded);
        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(entityCriteriaRowLabel, entityFieldName)));

        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(entityOperator);
        commonWaits.waitForElementVisible(entityTypeaheadExpanded);
        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(entityCriteriaRowLabel, entityOperator)));

        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(entityHostName);
        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(entityCriteriaRowLabel, entityHostName)));

        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Closing_bracket);
        commonWaits.waitForElementClickable(SelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(entityCriteriaRowLabel, Closing_bracket)));

        log.info("Filling Event Selection Criteria");
        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Opening_Bracket);
        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventCriteriaRowLabel, Opening_Bracket)));

        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(eventFieldName);
        commonWaits.waitForElementVisible(eventTypeaheadExpanded);
        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventCriteriaRowLabel, eventFieldName)));

        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(eventOperator);
        commonWaits.waitForElementVisible(eventTypeaheadExpanded);
        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventCriteriaRowLabel, eventOperator)));

        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(eventClassName);
        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventCriteriaRowLabel, eventClassName)));

        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Closing_bracket);
        commonWaits.waitForElementClickable(EventSelectionQuery).sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventCriteriaRowLabel, Closing_bracket)));

        log.info("Clicking Preview to verify matching entity and event counts");
        commonWaits.scrollToElement(previewBtn);
        commonWaits.clickWithoutInterception(previewBtn);
        commonWaits.waitForElementVisible(previewSummaryContainer);
        previewDevices = commonWaits.waitForElementVisible(previewDeviceCount).getText().trim();
        previewEvents = commonWaits.waitForElementVisible(previewEventCount).getText().trim();
        log.info("Preview shows Devices: " + previewDevices + ", Events: " + previewEvents);

        log.info("Saving group");
        commonWaits.clickWithoutInterception(SaveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        log.info("Group save action completed for: " + groupName);
        return groupName;
    }

    public String getNotificationMessage() {
        commonWaits.waitForElementVisible(incidentCreatedmsg);
        String message = incidentCreatedmsg.getText().trim();
        log.info("Notification message: " + message);
        return message;
    }

    public boolean isNotificationPresent() {
        return commonWaits.isElementPresent(incidentCreatedmsg);
    }

    public String getPreviewDevices() {
        return previewDevices;
    }

    public String getPreviewEvents() {
        return previewEvents;
    }

    public void navigateToEventsTab() {
        log.info("Navigating to Events tab");
        commonWaits.clickWithoutInterception(monitoringDropdown);
        commonWaits.waitForElementVisible(eventsMenu).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public boolean isCreateIncidentOptionPresent(){
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForElementVisible(menuOptionOnEventDetailsPage).click();
        return commonWaits.isElementPresent(getDriver().findElement(By.xpath(String.format(actionsOnEvent,sEventCreateIncidentAction))));
    }

    public BhomPage clickCreateIncident(){
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(String.format(actionsOnEvent,sEventCreateIncidentAction)))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        return this;
    }

    public BhomPage searchEvent(String message){
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForElementVisible(searchMessageText).clear();
        commonWaits.waitForElementVisible(searchMessageText).sendKeys(message + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        firstDisplayedEvent = String.format(firstDisplayedEvent,message);
        return this;
    }

    public BhomPage searchEventwithDetails(String message){
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForElementVisible(searchMessageText).clear();
        commonWaits.waitForElementVisible(searchMessageText).sendKeys(message + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementVisible(firstEventDetails).click();
        return this;
    }

    public BhomPage acknowledgeEvent(String message){
        commonWaits.waitForPageLoad(getDriver());
        originalEvent = String.format(originalEvent,message);
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(originalEvent))).click();
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForElementClickable(acknowledgeEvent).click();
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForElementClickable(approveOkButton).click();
        return this;
    }

    public BhomPage assignEvent(String message){
        commonWaits.waitForPageLoad(getDriver());
        originalEvent = String.format(originalEvent,message);
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(originalEvent))).click();
        commonWaits.waitForElementClickable(assignEvent).click();
        commonWaits.waitForElementVisible(assignUserList).sendKeys("vinay");
        commonWaits.waitForElementClickable(approveOkButton).click();
        return this;
    }

    public BhomPage closeEvent(String message){
        commonWaits.waitForPageLoad(getDriver());
        originalEvent = String.format(originalEvent,message);
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(originalEvent))).click();
        commonWaits.waitForElementClickable(closeEvent).click();
        commonWaits.waitForElementClickable(approveOkButton).click();
        return this;
    }

    public int getEventCount(){
        String eventCount = commonWaits.waitForElementVisible(getDriver().findElement(By.xpath(TotalEventCount))).getText();
        return Integer.parseInt(eventCount);
    }

    public void viewRelatedEventDetails(){
        commonWaits.waitForElementClickable(firstEventLink).click();
        //Check if event details page is loaded
        commonWaits.waitForPageLoad(getDriver());
        commonWaits.waitForElementVisible(getDriver().findElement(By.xpath(eventDetails)));
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(relatedEventsTab))).click();
        commonWaits.waitForPageLoad(getDriver());
    }

    public void GoBackFromEventDetails(){
        String eventDetailsGoBack="//a[@id='detailsBackButton']";
        commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(eventDetailsGoBack))).click();
        commonWaits.waitForPageLoad(getDriver());
    }

    public void verifyTriggerAutomation(String eventMessage){
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(String.format(firstEventMenu,eventMessage)))).click();
        commonWaits.waitForElementVisible_withFluentWait(driver, triggerAutomation, 10);
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(triggerAutomation))).click();
        log.info("Trigger Automation button clicked");
        try{
            commonWaits.waitForElementVisible_withFluentWait(driver, runThisAutomation, 10);
            commonWaits.waitForElementClickable(driver.findElement(By.xpath(runThisAutomation))).click();
            log.info("Run This Automation button clicked");
            commonWaits.waitForElementVisible_withFluentWait(driver, successfullyExecution, 10);
            Assert.assertTrue(driver.findElement(By.xpath(successfullyExecution)).isDisplayed(),"IA Automation execution was not seen successfully.");
        }
        catch(Exception e){
            Assert.fail("IA Automation execution was not seen successfully.");
        }  
    }

    public void selectMessageForCreateAutomation() {
        try {
            commonWaits.waitForPageLoad(driver);
            commonWaits.loader(driver);
            Actions modifyAction = new Actions(driver);
            modifyAction.moveToElement(driver.findElement(By.xpath("(//button[@id='actionDropdownMenu'])[last()]"))).click().perform();
            this.driver.findElement(By.xpath("//span[contains(.,'Create Automation')]")).isDisplayed();
            this.driver.findElement(By.xpath("//span[contains(.,'Create Automation')]")).click();
            commonWaits.loader(driver);
            commonWaits.waitForPageLoad(driver);
        } catch (Exception var2) {
            //logger.info(var2);
        }
    }

    public void refreshEvents() {
        commonWaits.clickWithoutInterception(refreshButton);
        log.info("Events refreshed successfully.");
    }

    public String getIncidentID(){
        refreshBrowser();
        commonWaits.waitForElementVisible(othersTab).click();
        commonWaits.waitForElementVisible(refreshEventDetailsPageBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForPageReadyState();
        String incID = incidentID.getText();
        Assert.assertTrue(incID.contains("INC"), "Incident ticket is not generated");
        return incID;
    }

    public void modifyEventForPriority(String priority) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.clickWithoutInterception(eventMenuButton);
        commonWaits.clickWithoutInterception(modifyEventButton);
        commonWaits.clickWithoutInterception(ticketEventTypeDropdown);
        commonWaits.scrollToElement(ticketEventTypePriorityOption);
        commonWaits.clickWithoutInterception(ticketEventTypePriorityOption);
        commonWaits.clickWithoutInterception(priorityDropdownButton);
        commonWaits.clickWithoutInterception(By.xpath(String.format(priorityDropdownOption, priority)));
        commonWaits.waitForElementVisible(okButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        refreshBrowser();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertEquals(commonWaits.waitForElementVisible(priorityFieldValue).getText(), priority, "Priority is not changed");
    }

    public void closeEvent(){
        commonWaits.clickWithoutInterception(eventMenuButton);
        commonWaits.clickWithoutInterception(closeEventButton);
        commonWaits.waitForElementVisible(okButton).click();
        refreshBrowser();
    }

    public void navigateToDevices() {
        log.info("Navigating to Devices tab");
        commonWaits.clickWithoutInterception(monitoringDropdown);
        commonWaits.clickWithoutInterception(devicesMenu);
    }

    public int searchAndCountSearchedDevice(String device){
        commonWaits.waitForElementVisible(deviceSearchInputBox).sendKeys(device + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

        boolean found = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(deviceName, device))).isDisplayed();
        if (found) {
            log.info("Device found: " + device);
            return commonWaits.getElements(By.xpath(String.format(deviceName, device))).size();
        } else {
            log.info("Device not found: " + device);
            return 0;
        }

    }
    public void searchAndOpenDevice(String device){
        commonWaits.waitForElementVisible(deviceSearchInputBox).sendKeys(device + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(deviceName, device))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void navigateToMonitorsTab() {
        log.info("Navigating to Monitors tab");
        // Implementation to navigate to Monitors tab
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(tabLink, "Monitors"))).click();
    }

    public void selectMonitor(String monitor) {

        log.info("Selecting Tile view option " );
        commonWaits.waitForElementClickable(monitorViewTypeSelector).click();
        commonWaits.waitForElementClickable(tileViewOption).click();

        log.info("Selecting monitor: " + monitor);
        // Implementation to select monitor
        commonWaits.waitForElementVisible(monitorSearchInputBox).sendKeys(monitor + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(monitorName, monitor))).click();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public boolean isDataGraphAvailable(){
        return commonWaits.isElementPresent(monitoringDataGraph);
    }

    public int getOpenEventCount(){
        int openEventCount = 0;
        if (commonWaits.isElementPresent(By.xpath(openEventStatus))){
            log.info("Open event status is present.");
            openEventCount = commonWaits.getElements(By.xpath(openEventStatus)).size();
        } else {
            log.info("Open event status is not present.");
        }
        return openEventCount;
    }

    public int getBlackoutEventCount(){
        int eventsCount = 0;
        if (commonWaits.isElementPresent(By.xpath(blackoutStatusEvents))){
            log.info("blackout events are present.");
            eventsCount = commonWaits.getElements(By.xpath(blackoutStatusEvents)).size();
        } else {
            log.info("blackout events are not present.");
        }
        return eventsCount;
    }

    public void verifyOpenEventsCreatedForHost(String hostName){
        log.info("Verifying events are created for host: " + hostName);
        this.searchEvent(hostName);
        Assert.assertTrue(getOpenEventCount() > 0, "No open events found for host: " + hostName);
    }

    public void closeAllOpenEventsForHost(String hostName) {
        log.info("Closing all events for host: " + hostName);
        this.searchEvent(hostName);
        int eventCount = getOpenEventCount();
        if (eventCount == 0) {
            log.info("No open events found for host: " + hostName);
        }
        else {
            for (int i = 1; i <= eventCount; i++) {
                this.closeEvent();
            }
        }
    }

    public void applyAdvanceFilter(String filterCriteria) {
        commonWaits.waitForElementVisible(advancedFilterButton).click();
        if(commonWaits.isElementPresent(clearFilterButton)){
            log.info("Clearing existing filters if any.");
            clearFilterButton.click();
        }
        commonWaits.waitForElementVisible(selectionCriteriaButton).click();
        commonWaits.scrollToElement(selectionCriteriaInput);
        commonWaits.waitForElementVisible(selectionCriteriaInput).click();
        commonWaits.typeAndSelectEachPart(selectionCriteriaInput, filterCriteria);
        commonWaits.waitForElementVisible(applyFilterButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        log.info("Applied advanced filter: " + filterCriteria);
        commonWaits.waitForElementInvisible(selectionCriteriaInput);
       Assert.assertTrue(commonWaits.isElementPresent(activeFilterTag), "Active filter tag is not present after applying filter.");
    }

    public String getHeaderMessageValue(){
        commonWaits.waitForPageReadyState(getDriver());
        String message = commonWaits.waitForElementVisible(messageHeaderValue).getText().trim();
        log.info("Actual message is - "+ message);
        return  message;
    }

    public String getSNOWIncidentValue(){
        commonWaits.waitForElementVisible(othersTabOnEvent).click();
        commonWaits.waitForPageReadyState(getDriver());
        String ID = commonWaits.waitForElementVisible(incident_id).getText().trim();
        log.info("ID is - "+ ID);
        return  ID;
    }

    public String getIncidentValue(){
        commonWaits.waitForElementVisible(othersTabOnEvent).click();
        commonWaits.waitForPageReadyState(getDriver());
        String ID = commonWaits.waitForElementVisible(incident_id).getText().trim();
        log.info("ID is - "+ ID);
        return  ID;
    }

    public void searchAndOpenEventsByMessage(String message) {
        searchEvent(message);
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), occurredDateLink, 30),
                "Occurred date link is not visible for event message: " + message);
        driver.findElement(By.xpath(occurredDateLink)).click();
        commonWaits.waitForPageReadyState(getDriver());
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), eventDetails, 30),
                "Event Details page is not visible for event message: " + message);
    }

    public void searchAndOpenNonInformationEventByMessage(String message) {
        searchEvent(message);
        commonWaits.waitForElementClickable(firstEventLink).click();
        commonWaits.waitForPageReadyState(getDriver());
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), eventDetails, 30),
                "Event Details page is not visible for event message: " + message);
    }

    public void goToOtherTabOfEvent(){
        commonWaits.waitForElementVisible(othersTabOnEvent).click();
    }

    public void closeAllOpenEventsForService(String serviceName) {
        log.info("Closing all events for Service: " + serviceName);
        String filterCriteria = String.format("( _service_name equals '%s' )", serviceName);
        this.applyAdvanceFilter(filterCriteria);
        int eventCount = getOpenEventCount();
        if (eventCount == 0) {
            log.info("No open events found for Service: " + serviceName);
        }
        else {
            for (int i = 1; i <= eventCount; i++) {
                closeEvent();
            }
        }
    }

    public void closeAllBlackoutEvents() {
        String filterCriteria = "( status equals BLACKOUT )";
        this.applyAdvanceFilter(filterCriteria);
        int eventCount = getBlackoutEventCount();
        if (eventCount == 0) {
            log.info("No open events in blackout status");
        }
        else {
            for (int i = 1; i <= eventCount; i++) {
                closeEvent();
            }
        }
    }

    public void navigateToBlackoutPolicies() {
        log.info("Navigating to Blackout Policies");
        commonWaits.waitForElementVisible(ConfigurationDropdown).click();
        commonWaits.waitForElementVisible(blackoutPoliciesMenu).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void searchBlackoutPolicy(String policyName) {
        log.info("Searching for blackout policy: " + policyName);
        commonWaits.waitForElementVisible(searchBlackoutPolicyInput).sendKeys(policyName + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void verifyBlackoutPolicyCreatedAndEnabled() {
        Assert.assertTrue(commonWaits.waitForElementVisible(blackoutPolicyStatusCell).getText().contains("Enabled"));
    }

    public void verifyBlackoutPolicyNotExpired() {
        int ColumnSize = commonWaits.getElements(By.xpath(policyColumnName)).size();
        int columnIndex = 0;
        for(int i = 0; i<ColumnSize; i++) {
            String columnName = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyColumnName, "["+ i +"]"))).getText();
            if (columnName.equalsIgnoreCase("Blackout State")) {
                columnIndex = i;
                break;
            }
        }
        if(columnIndex > 0){
            String policyState = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyColumnValue, columnIndex))).getText();
            Assert.assertEquals(policyState,"In Blackout","Blackout policy is not in Blackout state.");
        }
        else {
            log.error("Blackout State column not found in the blackout policies table.");
            Assert.fail("Blackout State column not found in the blackout policies table.");
        }
    }

    public boolean verifyEventsInBlackoutState(String hostName) {
        log.info("Verifying events in blackout state for host: " + hostName);
        this.searchEvent(hostName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        return commonWaits.isElementPresent(blackoutEventStatus);
    }

    public String getEventStatus(String eventName) {
        log.info("get event status");
        this.searchEvent(eventName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        Assert.assertTrue(commonWaits.isElementPresent(eventStatusIcon), "Event status icon is not present for event: " + eventName);
        return eventStatusIcon.getAttribute("title").trim();
    }

    public void disableBlackoutPolicy(String policyName) {
        log.info("Disabling blackout policy for host: " + policyName);
        this.navigateToBlackoutPolicies();
        this.searchBlackoutPolicy(policyName);
        commonWaits.clickWithoutInterception(blackoutPolicyCheckbox);
        commonWaits.waitForElementVisible(disableBlackoutPolicyButton).click();
        commonWaits.waitForElementVisible(confirmPopupYesButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void verifyEventEditDetails(String eventName){
        commonWaits.waitForElementVisible(eventDetailsTitle);
        String actual = commonWaits.waitForElementVisible(eventNameHeader).getText().trim();
        Assert.assertEquals(actual, eventName, "Incorrect event opened in BHOM application while cross launch");
    }

    public void verifyEventTabNames() {
        SoftAssert softAssert = new SoftAssert();
        commonWaits.waitForElementVisible(eventDetailsTitle);
        for(String item: eventOptionList){
            softAssert.assertTrue(commonWaits.isElementPresent(String.format(tabName, item)), "Event Tab is not available: "+ item);
        }
        softAssert.assertAll();
    }




    public String getServiceNameFromPayload(String jsonPayload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonPayload);

            // Navigate to: response → allslots → _service_name
            JsonNode node = root.path("response")
                    .path("allslots")
                    .path("_service_name");

            if (node.isMissingNode() || node.isNull()) {
                return null;
            }

            return node.asText();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void navigateToAlarmPolicies() {
        commonWaits.waitForElementVisible(ConfigurationDropdown).click();
        commonWaits.waitForElementVisible(alarmPoliciesMenu).click();
    }

    public boolean searchAlarmPolicy(String policyName) {
        commonWaits.switchToTabByTitle(SolQAConstants.BHOM);
        commonWaits.waitForElementVisible(policySearchInput).sendKeys(policyName + Keys.ENTER);
        return commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(policyRow, policyName), 5);
    }

    public void deletePolicy(String policyName) {
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyRow+"//*[contains(@class ,'ui-chkbox-box')]", policyName)));
        we.click();
        commonWaits.waitForElementVisible(deleteBtn).click();
        commonWaits.waitForElementVisible(yesModalBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void createAlarmPolicy(Map<Object, String> testData) {
        commonWaits.waitForElementVisible(createPolicyBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForElementVisible(policyNameInput).sendKeys(testData.get("policyName"));
        commonWaits.waitForElementVisible(searchMetricInput1).sendKeys(testData.get("metricName1"));
        if(Objects.equals(testData.get("metricTypeNode"), "Windows")) {
            commonWaits.clickWithoutInterception(By.xpath(String.format(metricOption, testData.get("metricName1"), "Windows")));
        }
        commonWaits.clickWithoutInterception(selectInstanceDropdown);
        commonWaits.waitForElementVisible(allInstancesOption).click();
        scrollToElement(ifCondition1);
        commonWaits.waitForElementVisible(ifCondition1).click();
        commonWaits.clickWithoutInterception(aboveOption);
        commonWaits.waitForElementVisible(thresholdInput).sendKeys(testData.get("thresholdValue")+Keys.TAB);
        commonWaits.waitForElementVisible(timeOption).click();
        commonWaits.waitForElementVisible(durationInput).sendKeys(testData.get("durationValue")+Keys.TAB);
        commonWaits.waitForElementVisible(thenCondition1).click();
        commonWaits.waitForElementVisible(criticalAlarmOption).click();
        commonWaits.waitForElementVisible(closeAlarmOption).click();
        commonWaits.waitForElementVisible(immediatelyOption).click();

        commonWaits.waitForElementVisible(addNewMetricBtn).click();
        commonWaits.waitForElementVisible(searchMetricInput2).sendKeys(testData.get("metricName2"));
        if(Objects.equals(testData.get("metricTypeNode"), "Windows")) {
            commonWaits.clickWithoutInterception(By.xpath(String.format(metricOption, testData.get("metricName2"), "Windows")));
        }
        scrollToElement(selectInstanceDropdown);
        commonWaits.clickWithoutInterception(selectInstanceDropdown);
        commonWaits.waitForElementVisible(allInstancesOption).click();
        commonWaits.scrollToBottomOfPage();
        commonWaits.waitForElementVisible(ifCondition2).click();
        commonWaits.waitForElementVisible(aboveOption).click();
        commonWaits.waitForElementVisible(thresholdInput).sendKeys(testData.get("thresholdValue")+Keys.TAB);
        commonWaits.waitForElementVisible(timeOption).click();
        commonWaits.waitForElementVisible(durationInput).sendKeys(testData.get("durationValue")+Keys.TAB);
        commonWaits.waitForElementVisible(thenCondition2).click();
        commonWaits.waitForElementVisible(majorAlarmOption).click();
        commonWaits.waitForElementVisible(closeAlarmOption).click();
        commonWaits.waitForElementVisible(immediatelyOption).click();
        commonWaits.scrollToElement(enablePolicyBtn);
        enablePolicyBtn.click();
        commonWaits.waitForElementVisible(savePolicyBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
    }

    @FindBy(xpath = "//input/ancestor::*[contains(@class, 'checkbox__label') and contains(. ,'Enable Policy')]")
    WebElement enablePolicyBtn;
    @FindBy(xpath = "//input[@placeholder='Search by policy name']")
    WebElement policySearchInput;
    String policyRow="//td[contains(. ,'%s')]/parent::tr";
    @FindBy(xpath = "//adapt-button[@title='Delete']")
    WebElement deleteBtn;
    @FindBy(xpath = "//button[contains(. ,'Yes')]")
    WebElement yesModalBtn;
    @FindBy(xpath = "//button[@id='createAlarmGenerationPolicyButton']")
    WebElement createPolicyBtn;
    @FindBy(xpath = "//adapt-rx-textfield[contains(. ,'Policy Name')]//input")
    WebElement policyNameInput;
    @FindBy(xpath = "(//input[@placeholder='Search Metric'])[1]")
    WebElement searchMetricInput1;
    @FindBy(xpath = "(//input[@placeholder='Search Metric'])[2]")
    WebElement searchMetricInput2;
    @FindBy(xpath = "//button[contains(. ,'Select Instance')]")
    WebElement selectInstanceDropdown;
    @FindBy(xpath = "//if-condition//div[@id='if-condition-box-ifThenRow0-if-condition-0']")
    WebElement ifCondition1;
    @FindBy(xpath = "//if-condition//div[@id='if-condition-box-ifThenRow1-if-condition-0']")
    WebElement ifCondition2;
    @FindBy(xpath = "//then-action//div[@id='then-action-box-ifThenRow0-then-action-0']")
    WebElement thenCondition1;
    @FindBy(xpath = "//then-action//div[@id='then-action-box-ifThenRow1-then-action-0']")
    WebElement thenCondition2;
    @FindBy(xpath = "//button[contains(. ,'Above')]")
    WebElement aboveOption;
    @FindBy(xpath = "//button[contains(. ,'time')]")
    WebElement timeOption;
    @FindBy(xpath = "//input[@name='thresholdValue']")
    WebElement thresholdInput;
    @FindBy(xpath = "//input[@name='durationValue']")
    WebElement durationInput;
    @FindBy(xpath = "//button[contains(. ,'Critical Alarm')]")
    WebElement criticalAlarmOption;
    @FindBy(xpath = "//button[contains(. ,'Major Alarm')]")
    WebElement majorAlarmOption;
    @FindBy(xpath = "//button[contains(. ,'Close alarm')]")
    WebElement closeAlarmOption;
    @FindBy(xpath = "//button[contains(. ,'Immediately')]")
    WebElement immediatelyOption;
    @FindBy(xpath = "//button[contains(. ,'Add New Metric')]")
    WebElement addNewMetricBtn;
    @FindBy(xpath = "//button[contains(. ,'All Instances') and @role='option']")
    WebElement allInstancesOption;
    @FindBy(xpath = "//button[@id='policySaveCloseBtn']")
    WebElement savePolicyBtn;
    @FindBy(xpath = "//span[contains(@ng-if,'params.translateKey') and normalize-space()='All Devices']/following-sibling::span")
    WebElement allDevicesCount;
    @FindBy(xpath = "//div[@class='tile device-tile']//a[contains(@class,'text')]")
    List<WebElement> serverName;
    public BhomPage verifySaturationForecastAlertEventForHost(String message){
    	log.info("Select first filtered event");
    	commonWaits.waitForElementClickable(firstEventLink).click();
        commonWaits.waitForElementVisible(getDriver().findElement(By.xpath(eventDetails)));	
        commonWaits.waitForElementVisible(eventDetailsTitle);
        
        log.info("Validating event severity");
        String eventSeverity = commonWaits.waitForElementVisible(severityField).getText().trim();
        Assert.assertEquals(eventSeverity, SolQAConstants.CRITICAL_SEVERITY, "Incorrect event severity in BHOM application");
        
        log.info("Validating event status");
        String eventStatus = commonWaits.waitForElementVisible(statusField).getText().trim();
        Assert.assertEquals(eventStatus, SolQAConstants.OPEN, "Incorrect event status in BHOM application");
        
        log.info("Validating event class");
        String eventClass = commonWaits.waitForElementVisible(classField).getText().trim();
        Assert.assertEquals(eventClass, "OptimizeEvent", "Incorrect event class in BHOM application");
        
        log.info("Validating event message");
        String eventMessage = commonWaits.waitForElementVisible(messageField).getText().trim();
        Assert.assertTrue(eventMessage.contains(message), "Incorrect event message in BHOM application");
        
        log.info("Validating event occurrences is within 10 mins rage");
        String eventOccurred = commonWaits.waitForElementVisible(occurredField).getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime occurredDateTime;
        try {
            occurredDateTime = LocalDateTime.parse(eventOccurred, formatter);
        } catch (DateTimeParseException e) {
            Assert.fail("Event occurred time '" + eventOccurred + "' is not in expected format dd/MM/yyyy HH:mm");
            return this;
        }

        LocalDate today = LocalDate.now();
        Assert.assertEquals(occurredDateTime.toLocalDate(), today,
                "Event occurred date is not today. Actual: " + occurredDateTime.toLocalDate() + ", Expected: " + today);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(15);
        Assert.assertTrue(!occurredDateTime.isBefore(tenMinutesAgo) && !occurredDateTime.isAfter(now),
                "Event occurred time is not within the last 15 minutes, Occurred: " + occurredDateTime + ", Now: " + now);
        
        return this;
        
    }
    
    public void closeEventMatchingCriteria(String filterCriteria){
    	commonWaits.waitForPageLoad(getDriver());
        applyAdvanceFilter(filterCriteria);
        closeEvent();
    }
    
    public BhomPage applyAdvanceFilterForEvent(String filterCriteria) {
        commonWaits.waitForElementVisible(advancedFilterButton).click();
        if(commonWaits.isElementPresent(clearFilterButton)){
            log.info("Clearing existing filters if any.");
            clearFilterButton.click();
        }
        commonWaits.waitForElementVisible(selectionCriteriaButton).click();
        commonWaits.scrollToElement(selectionCriteriaInput);
        commonWaits.waitForElementVisible(selectionCriteriaInput).click();
        commonWaits.typeAndSelectEachPart(selectionCriteriaInput, filterCriteria);
        commonWaits.waitForElementVisible(applyFilterButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        log.info("Applied advanced filter: " + filterCriteria);
        return this;
    }

    /**
     * Get the internal ID of an event
     * @return The internal ID as a string
     */
    public String getEventInternalID() {
        try {
            commonWaits.waitForPageReadyState(driver);
            commonWaits.waitForElementClickable(internalsTabOnEvent).click();
            commonWaits.waitForPageReadyState(driver);
            String ID = commonWaits.waitForElementVisible(internalIDValue).getText().trim();
            log.info("Event Internal ID is - " + ID);
            return ID;
        } catch (Exception e) {
            log.error("Failed to get event internal ID: " + e.getMessage());
            throw new RuntimeException("Failed to get event internal ID: " + e.getMessage());
        }
    }



	public Integer selectMultipleEvent() {
		// TODO Auto-generated method stub
		  log.info("Remove Critical & Imformation from List ");

		  commonWaits.waitForElementClickable(critical);
		  click(critical);
		  commonWaits.waitForElementClickable(information);		  
		  click(information);
          commonWaits.waitForExecutionComplete();   
          commonWaits.waitForPageReadyState(getDriver());
		  log.info("Set IncidentID column in ASC ");
		  setIncidentId_ASC();	
		  log.info("Set grid Order as IncidentID ASC");
		  commonWaits.waitForExecutionComplete();          
          log.info("Select more than one Event ");
		
		 List<WebElement> rows = getDriver().findElements( By.xpath(tableRowPath));
		  Actions actions = new Actions(getDriver());
		 
	        if (!rows.isEmpty()) {
	            // Define the start and end rows for the selection range
	            WebElement firstRow = rows.get(1); // Select the second row (index 1)
	            WebElement lastRow = rows.get(2);  // Select the fifth row (index 4)

	          
	            // 2. Click the first row
	            actions.click(firstRow)
	                   // 3. Press and hold the SHIFT key
	                   .keyDown(Keys.SHIFT)
	                   // 4. Click the last row while SHIFT is held
	                   .click(lastRow)
	                   // 5. Release the SHIFT key
	                   .keyUp(Keys.SHIFT)
	                   // Build and perform the sequence of actions
	                   .build()	                   
	                   
	                   .perform();
	            

	        }
	        
	        log.info("Click on Row Action menu ");
	        
	        commonWaits.waitForElementClickable(rowActionBtn);
	        actions.keyDown(Keys.SHIFT).click(rowActionBtn).keyUp(Keys.SHIFT).perform();
	        
	        log.info("Create Incident ");
	         commonWaits.clickWithoutInterception(createIncident);
	       //  createIncident.click();
	         
	         log.info("Check Success message");
	         commonWaits.waitForElementClickable(incidentCreatedmsg);
	         String msg=incidentCreatedmsg.getText();
	         log.info("Success message"+msg);
	        
	         Assert.assertTrue(msg.equalsIgnoreCase(SolQAConstants.incidentCreatedmsg), "Incident Not Created Success fully");
	         
	         log.info("Reload List Two times");
	        
	        refreshEvents(); 
	        commonWaits.waitForExecutionComplete();
	        commonWaits.waitForPageLoad(getCurrentDriver());	        
	       
	        refreshEvents();
	        commonWaits.waitForExecutionComplete();
	        commonWaits.waitForPageLoad(getCurrentDriver());
	       
	        refreshEvents();	
	        commonWaits.waitForExecutionComplete();
	        commonWaits.waitForPageLoad(getCurrentDriver());
	      
	        log.info("Verify expected Result");
            Integer incidentcountAfterEvent=Integer.parseInt(incidentCount.getText());
            return incidentcountAfterEvent;
	        
	    }

	
	public Integer getIncidentCount() {
		// TODO Auto-generated method stub
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForPageLoad(getCurrentDriver());
		commonWaits.waitForElementVisible(incidentCount);
		int incidentcount=Integer.parseInt(incidentCount.getText());
		  log.info("Incident Count :::"+incidentcount);
		return incidentcount;
	}

	private void setIncidentId_ASC() {
        log.info("Incident Column order ASC");
      //   clickonIncidentIdColumn();
         if(isIncidentColumnASC())
		  {
		  clickonIncidentIdColumn();
		  }
	}

	private boolean isIncidentColumnASC() {
		// TODO Auto-generated method stub
		try {
			boolean status = commonWaits.isElementPresent(incidentIDColASC);
	        return status;
	    } catch (NoSuchElementException e) {
	        return true;
	    }
	}

	private void clickonIncidentIdColumn() {
		commonWaits.waitForElementClickable(incidentIDCol);		 
		  incidentIDCol.click();
		  commonWaits.waitForExecutionComplete();
	}
	
	public BhomPage validateAuthProfilePageisOpened() {
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("authProfile"), 
                "Authorization Profile page is not displayed. Current URL: " + currentUrl);
        log.info("Auth Profile Page is displayed successfully");
		return this;
	}
	
	/**
	 * Method to click on a link based on the link name
	 * @param linkName - Name of the link to click
	 * @return BhomPage
	 */
	public BhomPage clickOnAuthProfileLink(String linkName) {
		log.info("Clicking on link: " + linkName);
		String linkXpath = String.format(linkByName, linkName, linkName);
		WebElement link = commonWaits.waitForByElementToBeVisible(By.xpath(linkXpath));
		commonWaits.scrollToElement(link);
		commonWaits.clickWithoutInterception(link);
		commonWaits.waitForPageLoad(getDriver());
		log.info("Successfully clicked on link: " + linkName);
		return this;
	}
	
	/**
	 * Method to create Authorization Profile
	 * @param authProfileBaseName - Base name for the auth profile
	 * @param userGroupName - User group name to be selected
	 * @return String - Full auth profile name created
	 */
	public String CreateAuthProfile(String authProfileBaseName, String userGroupName) {
		log.info("Creating Authorization Profile with base name: " + authProfileBaseName);
		
		// Generate 5 digit random string
		String randomString = String.format("%05d", randomNumber());
		String authProfileName = authProfileBaseName + randomString;
		log.info("Auth Profile name with random string: " + authProfileName);
		
		// Click on Create Auth Profile button
		log.info("Clicking on Create Auth Profile button");
		commonWaits.waitForElementClickable(createAuthProfileButton).click();
		commonWaits.waitForPageLoad(getDriver());
		
		// Input auth profile name
		log.info("Entering auth profile name: " + authProfileName);
		commonWaits.waitForElementVisible(authProfileNameInputBox).clear();
		authProfileNameInputBox.sendKeys(authProfileName);
		
		// Click on Add User Group
		log.info("Clicking on Add User Group");
		commonWaits.waitForElementClickable(addUserGroup).click();
		
		// Select checkbox for user group
		log.info("Selecting user group checkbox: " + userGroupName);
		String userGroupCheckboxXpath = String.format(userGroupCheckbox, userGroupName);
		commonWaits.clickWithoutInterception(By.xpath(userGroupCheckboxXpath));
		
		// Click OK button
		log.info("Clicking OK button after selecting user group");
		commonWaits.waitForElementClickable(okButton).click();
		
		// Click on Objects tab
		log.info("Clicking on Objects tab");
		commonWaits.waitForElementClickable(objectsTab).click();
		commonWaits.waitForPageLoad(getDriver());
		
		// Click on Services tab
		log.info("Clicking on Services tab");
		WebElement servicesTabElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(tabNameByText, "Services")));
		commonWaits.clickWithoutInterception(servicesTabElement);
		
		// Click on Add Solution button
		log.info("Clicking on Add Solution button");
		commonWaits.clickWithoutInterception(addSolutionButton);
		
		// Select "selectAll" checkbox
		log.info("Selecting 'selectAll' checkbox");
		String selectAllCheckboxXpath = String.format(userGroupCheckbox, "selectAll");
		commonWaits.clickWithoutInterception(By.xpath(selectAllCheckboxXpath));
		
		// Click OK button
		log.info("Clicking OK button after selecting solutions");
		commonWaits.waitForElementClickable(okButton).click();
		
		// Click Save Auth Profile button
		log.info("Clicking Save Auth Profile button");
		commonWaits.waitForElementClickable(saveAuthProfileButton).click();
		
		// Wait for success message to be visible
		log.info("Waiting for success message to be visible");
		commonWaits.waitForElementVisible(authProfileSuccessMsg);
		log.info("Success message displayed: Authorization Profile is created successfully");
		
		// Wait for success message to be invisible
		log.info("Waiting for success message to disappear");
		commonWaits.waitForElementInvisible(authProfileSuccessMsg);
		log.info("Authorization Profile created successfully: " + authProfileName);
		
		return authProfileName;
	}
	
	/**
	 * Method to edit Authorization Profile
	 * @param authProfileName - Name of the authorization profile to edit
	 * @return BhomPage
	 */
	public BhomPage editAuthProfile(String authProfileName) {
		log.info("Editing Authorization Profile: " + authProfileName);
		
		// Click on auth profile menu
		log.info("Clicking on auth profile menu for: " + authProfileName);
		String authProfileMenuXpath = String.format(authProfileMenu, authProfileName);
		WebElement authProfileMenuElement = commonWaits.waitForByElementToBeVisible(By.xpath(authProfileMenuXpath));
		commonWaits.clickWithoutInterception(authProfileMenuElement);
		
		// Click on auth profile edit
		log.info("Clicking on Edit for auth profile: " + authProfileName);
		String authProfileEditXpath = String.format(authProfileEdit, authProfileName);
		WebElement authProfileEditElement = commonWaits.waitForByElementToBeVisible(By.xpath(authProfileEditXpath));
		commonWaits.clickWithoutInterception(authProfileEditElement);
		commonWaits.waitForPageLoad(getDriver());
		
		// Click on Objects tab
		log.info("Clicking on Objects tab");
		commonWaits.waitForElementClickable(objectsTab).click();
		commonWaits.waitForPageLoad(getDriver());
		
		// Click on Services tab
		log.info("Clicking on Services tab");
		WebElement servicesTabElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(tabNameByText, "Services")));
		commonWaits.clickWithoutInterception(servicesTabElement);
		
		// Click on edit object action menu
		log.info("Clicking on Edit Object Action Menu");
		commonWaits.waitForElementClickable(editObjectActionMenu).click();
		
		// Click on edit object action
		log.info("Clicking on Edit Object Action");
		commonWaits.clickWithoutInterception(editObjectAction);
		commonWaits.waitForPageLoad(getDriver());
		
		// Deselect a random checkbox and validate
		log.info("Finding checkboxes to deselect");
		
		// Collect all checkbox IDs (not WebElements) to avoid stale references
		List<WebElement> checkboxElements = getDriver().findElements(By.xpath(checkboxesXpath));
		List<String> checkboxIds = new java.util.ArrayList<>();
		for (WebElement cb : checkboxElements) {
			try {
				String id = cb.getAttribute("id");
				if (id != null && !id.isEmpty()) {
					checkboxIds.add(id);
				}
			} catch (StaleElementReferenceException e) {
				log.warn("Stale element while collecting checkbox IDs, skipping");
			}
		}
		
		log.info("Total checkboxes found: " + checkboxIds.size());
		
		// Find a selected checkbox and deselect it
		int maxAttempts = Math.min(checkboxIds.size(), 10); 
		int randomStartIndex = checkboxIds.size() > 0 ? new Random().nextInt(checkboxIds.size()) : 0;
		
		for (int i = 0; i < maxAttempts; i++) {
			int currentIndex = (randomStartIndex + i) % checkboxIds.size();
			String checkboxId = checkboxIds.get(currentIndex);
			
			try {
				// Locate fresh element by ID
				WebElement freshCheckbox = getDriver().findElement(By.id(checkboxId));
				
				if (freshCheckbox.isSelected()) {
					log.info("Deselecting checkbox at index: " + currentIndex + ", ID: " + checkboxId);
					
					// Scroll to the checkbox and click it to deselect
					commonWaits.scrollToSpecificElement(freshCheckbox);
					commonWaits.clickToElement(servicesTabElement);
					
					// Wait for Angular/DOM to update
					commonWaits.waitForAngularRequestsToFinish(); 
					
					// Re-locate and validate checkbox is deselected
					WebElement revalidatedCheckbox = getDriver().findElement(By.id(checkboxId));
					if (!revalidatedCheckbox.isSelected()) {
						log.info("Successfully deselected and validated checkbox at index: " + currentIndex);
						break;
					} else {
						log.warn("Checkbox at index " + currentIndex + " is still selected after click, trying next");
					}
				}
			} catch (StaleElementReferenceException e) {
				log.warn("Stale element at index " + currentIndex + ", trying next checkbox");
				continue;
			} 
		}
		
		// Click on Save Edit Auth Profile button
		log.info("Clicking on Save button");
		commonWaits.waitForElementClickable(saveEditAuthProfileButton).click();
		
		// Click Yes button on edit confirmation dialog box
		log.info("Clicking Yes button button on edit confirmation dialog box");
		commonWaits.waitForElementClickable(yesModalBtn).click();
		
		// Wait for success message to be visible
		log.info("Waiting for success message to be visible");
		commonWaits.waitForElementVisible(editAuthProfileSuccessMsg);
		log.info("Success message displayed: Authorization Profile is updated successfully");
				
		// Wait for success message to be invisible
		log.info("Waiting for success message to disappear");
		commonWaits.waitForElementInvisible(editAuthProfileSuccessMsg);
		log.info("Authorization Profile updated successfully: " + authProfileName);
		
		return this;
	}
	
	
	/**
	 * Method to delete Authorization Profile
	 * @param authProfileName - Name of the authorization profile to delete
	 * @return BhomPage
	 */
	public BhomPage deleteAuthProfile(String authProfileName) {
		log.info("Deleting Authorization Profile: " + authProfileName);
		
		// Click on auth profile menu
		log.info("Clicking on auth profile menu for: " + authProfileName);
		String authProfileMenuXpath = String.format(authProfileMenu, authProfileName);
		WebElement authProfileMenuElement = commonWaits.waitForByElementToBeVisible(By.xpath(authProfileMenuXpath));
		commonWaits.clickWithoutInterception(authProfileMenuElement);
		
		// Click on auth profile delete
		log.info("Clicking on Edit for auth profile: " + authProfileName);
		String authProfileDeleteXpath = String.format(authProfileDelete, authProfileName);
		WebElement authProfileEditElement = commonWaits.waitForByElementToBeVisible(By.xpath(authProfileDeleteXpath));
		commonWaits.clickWithoutInterception(authProfileEditElement);
		commonWaits.waitForPageLoad(getDriver());
		
		// Click Yes button on delete confirmation dialog box
		log.info("Clicking Yes button button on delete confirmation dialog box");
		commonWaits.waitForElementClickable(yesModalBtn).click();
		
		return this;
		
	}	
	
	public BhomPage navigateToEventPolicies() {
		log.info("Navigating to Configuration -> Event Policies");
		commonWaits.waitForPageReadyState(getDriver());
		commonWaits.waitForElementVisible(ConfigurationDropdown).click();
		commonWaits.waitForElementVisible(eventPoliciesMenu).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForPageReadyState(getDriver());
		return this;
	}

	public String createSuppressionEventPolicy(String policyNamePrefix, String eventFieldName, String eventOperator,
			String eventValue, String policyConfiguration) {
		String policyName = policyNamePrefix + generateRandomString().substring(0, 5);
		log.info("Creating suppression event policy: " + policyName);

		commonWaits.waitForElementVisible(createEventPolicyBtn).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForPageReadyState(getDriver());
		commonWaits.waitForElementVisible(createEventPolicyPageTitle);

		commonWaits.waitForElementVisible(eventPolicyNameInput).clear();
		commonWaits.waitForElementVisible(eventPolicyNameInput).sendKeys(policyName);

		log.info("Filling Event Selection Criteria: ( " + eventFieldName + " " + eventOperator + " " + eventValue
				+ " )");

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Opening_Bracket);
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, Opening_Bracket)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(eventFieldName);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyTypeaheadOption, eventFieldName)));
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, eventFieldName)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(eventOperator);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyTypeaheadOption, eventOperator)));
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, eventOperator)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(eventValue);
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, eventValue)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Closing_bracket);
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, Closing_bracket)));
		log.info("Event Selection Criteria filled successfully");

		log.info("Selecting Policy Configuration: " + policyConfiguration);
		scrollToElement(policyConfigDropdown);
		commonWaits.waitForElementVisible(policyConfigDropdown).click();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyConfigOption, policyConfiguration)))
				.click();

		log.info("Saving Suppression Settings on side panel");
		commonWaits.waitForElementClickable(suppressionSettingsSaveBtn);
		commonWaits.clickWithoutInterception(suppressionSettingsSaveBtn);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForElementVisible(suppressionAccordionTab);
		log.info("Suppression configuration saved successfully");

		log.info("Enabling Policy");
		scrollToElement(enablePolicyWrapper);
		executeJS("arguments[0].click();", enablePolicyCheckbox);

		log.info("Clicking Save button");
		commonWaits.waitForElementVisible(savePolicyBtn).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForPageReadyState(getDriver());

		return policyName;
	}

	public boolean isEventPolicySuccessToastDisplayed() {
		try {
			commonWaits.waitForElementVisible(successToastNotification);
			return true;
		} catch (Exception e) {
			log.error("Success toast notification not displayed: " + e.getMessage());
			return false;
		}
	}

	public boolean waitForEventOnEventsPage(String eventMessage, int maxAttempts) {
		log.info("Waiting for event to appear on events page: " + eventMessage);
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			log.info("Attempt " + attempt + "/" + maxAttempts);
			commonWaits.clickWithoutInterception(refreshButton);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
			commonWaits.waitForElementVisible(searchMessageText).clear();
			commonWaits.waitForElementVisible(searchMessageText).sendKeys(eventMessage + Keys.ENTER);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
			int count = getEventCount();
			if (count > 0) {
				log.info("Event found on events page: " + eventMessage + " (count: " + count + ")");
				return true;
			}
			log.info("Event not found yet, retrying...");
		}
		log.info("Event not found after " + maxAttempts + " attempts: " + eventMessage);
		return false;
	}

	public boolean isEventAbsentOnEventsPage(String eventMessage) {
		log.info("Verifying event is absent on events page: " + eventMessage);
		commonWaits.clickWithoutInterception(refreshButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForElementVisible(searchMessageText).clear();
		commonWaits.waitForElementVisible(searchMessageText).sendKeys(eventMessage + Keys.ENTER);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		int count = getEventCount();
		log.info("Event count for '" + eventMessage + "': " + count);
		return count == 0;
	}

	public String createNotificationEventPolicy(String policyNamePrefix, String eventFieldName, String eventOperator,
			String eventValue, String policyConfiguration, String notificationSendTo, String notificationSubject,
			String notificationMessage, String severityOperator, String severityValue) {

		String policyName = policyNamePrefix + generateRandomString().substring(0, 5);

		commonWaits.waitForElementVisible(createEventPolicyBtn).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForPageReadyState(getDriver());
		commonWaits.waitForElementVisible(createEventPolicyPageTitle);

		commonWaits.waitForElementVisible(eventPolicyNameInput).clear();
		commonWaits.waitForElementVisible(eventPolicyNameInput).sendKeys(policyName);

		log.info("Filling Event Selection Criteria: ( " + eventFieldName + " " + eventOperator + " " + eventValue
				+ " )");

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Opening_Bracket);
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, Opening_Bracket)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(eventFieldName);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyTypeaheadOption, eventFieldName)));
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, eventFieldName)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(eventOperator);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyTypeaheadOption, eventOperator)));
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, eventOperator)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(eventValue);
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, eventValue)));

		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Closing_bracket);
		commonWaits.waitForElementClickable(eventPolicyCriteriaInput).sendKeys(Keys.ENTER);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(eventPolicyCriteriaRowLabel, Closing_bracket)));
		log.info("Event Selection Criteria filled successfully");

		log.info("Selecting Policy Configuration: " + policyConfiguration);
		scrollToElement(policyConfigDropdown);
		commonWaits.waitForElementVisible(policyConfigDropdown).click();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyConfigOption, policyConfiguration)))
				.click();

		log.info("Adding notification recipient: " + notificationSendTo);
		commonWaits.waitForElementVisible(notificationSendToInput).sendKeys(notificationSendTo);
		commonWaits.waitForElementVisible(notificationSendToInput).sendKeys(Keys.ENTER);

		log.info("Setting notification subject");
		commonWaits.waitForElementVisible(notificationSubjectInput).clear();
		commonWaits.waitForElementVisible(notificationSubjectInput).sendKeys(notificationSubject);

		log.info("Setting notification message");
		commonWaits.waitForElementVisible(notificationMessageInput).clear();
		commonWaits.waitForElementVisible(notificationMessageInput).sendKeys(notificationMessage);

		log.info("Selecting Event Severity criteria");
		scrollToElement(eventSeverityCheckboxWrapper);
		executeJS("arguments[0].click();", eventSeverityCheckbox);

		log.info("Selecting severity operator: " + severityOperator);
		commonWaits.waitForElementVisible(severityOperatorDropdown).click();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyConfigOption, severityOperator))).click();

		log.info("Selecting severity value: " + severityValue);
		commonWaits.waitForElementVisible(severityValueDropdown).click();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(policyConfigOption, severityValue))).click();

		log.info("Clicking Preview & Save button");
		commonWaits.waitForElementClickable(previewAndSaveBtn);
		commonWaits.clickWithoutInterception(previewAndSaveBtn);

		log.info("Clicking Save in Preview dialog");
		commonWaits.waitForElementClickable(notificationPreviewSaveBtn);
		commonWaits.clickWithoutInterception(notificationPreviewSaveBtn);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForElementVisible(notificationAccordionTab);
		log.info("Notification configuration saved successfully");

		log.info("Enabling Policy");
		scrollToElement(enablePolicyWrapper);
		executeJS("arguments[0].click();", enablePolicyCheckbox);

		log.info("Clicking Save button");
		commonWaits.waitForElementVisible(savePolicyBtn).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
		commonWaits.waitForPageReadyState(getDriver());

		return policyName;
	}

    public String getAllDevicesCount() {
       String allDevicesCountText = commonWaits.waitForElementVisible(allDevicesCount).getText().trim();
       log.info("All Devices count: " + allDevicesCountText);
       return allDevicesCountText;
    }

    public void selectEventFiltersBasedOnCount(String devicesStatus) {
        log.info("Selecting event filters based on count: " + devicesStatus);
            // Split values by comma
            String[] statuses = devicesStatus.split(",");
            for (String status : statuses) {
                status = status.trim();   // remove extra spaces
                    WebElement countElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(countXpath, status)));
                    int count = Integer.parseInt(countElement.getText().trim());
                    log.info("Count for " + status + ": " + count);
                    // get checkbox
                    boolean isChecked = false;
                    if(commonWaits.isElementPresent(By.xpath(String.format(checkboxStatusXpath, status)))) {
                    WebElement checkbox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(checkboxStatusXpath, status)));
                    isChecked = checkbox.getAttribute("class").equals("fi fi-unchecked");
                    log.info("Checkbox for " + status + ": " + isChecked);
                    }
                    else{
                        isChecked = false;
                    }
        
                    try{
                        WebElement checkboxElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(checkboxXpath, status)));
                        commonWaits.clickWithoutInterception(checkboxElement);
                        log.info(status + " -> Checked");
                    }
                    catch(AssertionError e){
                       log.error("Failed to check checkbox for " + status + " | " + e.getMessage());
                    }
                    } 
            
        }

        public void openDevice(String deviceName) {
            log.info("Opening device: " + deviceName);
            WebElement deviceElement=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(device_Name, deviceName)));
            commonWaits.clickWithoutInterception(deviceElement);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
            Assert.assertTrue(commonWaits.isElementPresent(openedDeviceName), "Device not opened");
            commonWaits.waitForSometime(commonWaits.moreDelay);
            Assert.assertEquals(openedDeviceName.getAttribute("title"), deviceName, "Device name is not matching");
            log.info("Device opened successfully: " + deviceName);
        }
    
        public String copyDeviceUrl(String Device1, String Device2, String Device3, String Device4) {
            String[] devices = {Device1, Device2, Device3, Device4};
            for (String deviceName : devices) {
                if(!serverName.get(0).getText().equals(deviceName)) {
                    log.info("Device is not restricted " + deviceName);
                    commonWaits.clickWithoutInterception(serverName.get(0));
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
                    Assert.assertTrue(commonWaits.isElementPresent(openedDeviceName), "Device not opened");
                    log.info("Device opened successfully: " + deviceName);
                    String deviceUrl = getDriver().getCurrentUrl();
                    log.info("Device URL: " + deviceUrl);
                    return deviceUrl;
                }
            }
            return null;
}

    public void pasteDeviceUrlAndVerifyUserHaveNoAccess(String deviceUrl) {
        log.info("Pasting device URL: " + deviceUrl);
        getDriver().get(deviceUrl);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        Assert.assertTrue(commonWaits.isElementPresent(errorMessage), "Error message is not displayed");
        Assert.assertEquals(errorMessage.getText(), "No data to display", "Error message is not matching");
        log.info("User have no access to the device: " + deviceUrl);
    }

    public void createAuthProfile(String authProfileName) {
        log.info("Creating Authorization Profile: " + authProfileName);
        commonWaits.waitForElementVisible(createButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForElementVisible(authorizationProfileNameInput).clear();
        commonWaits.keyClearAndType(authorizationProfileNameInput, authProfileName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
    }

    public void addAssociatedUserGroup(String userGroupName) {
        log.info("Adding associated user group: " + userGroupName);
        commonWaits.waitForElementVisible(addUserGroupButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.waitForElementVisible(userGroupSearchInput,20);
        commonWaits.keyClearAndType(userGroupSearchInput, userGroupName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.clickWithoutInterception(By.xpath(String.format(userGroupCheckboxXpath, userGroupName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.clickWithoutInterception(okButton);
        commonWaits.waitForExecutionComplete();
        log.info("Associated user group added successfully: " + userGroupName);
    }

    public void switchToObjectsTab() {
        log.info("Switching to Objects tab");
        commonWaits.clickWithoutInterception(objectLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        log.info("Objects tab switched successfully");
    }

    public void switchToDevicesTab() {
        log.info("Switching to Devices tab");
        commonWaits.clickWithoutInterception(devicesLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        log.info("Devices tab switched successfully");
    }

    public void addFirstFourDevices() {
        log.info("Adding first four devices");
        commonWaits.clickWithoutInterception(addDeviceButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBHOM);
        commonWaits.clickWithoutInterception(searchObjectsButton);
        commonWaits.waitForSometime(commonWaits.moreDelay); //This hardwait is required here to avoid flakiness
        commonWaits.waitForElementVisible(selectAllCheckbox1,40);
        commonWaits.clickWithoutInterception(selectAllCheckbox.get(0));
        commonWaits.clickWithoutInterception(selectAllCheckbox.get(1));
        commonWaits.clickWithoutInterception(selectAllCheckbox.get(2));
        commonWaits.clickWithoutInterception(selectAllCheckbox.get(3));
        commonWaits.clickWithoutInterception(okButton);
        commonWaits.waitForExecutionComplete();
        log.info("First four devices added successfully");
       
    }

    public List<String> getNameOfAddedDevices() {
        log.info("Getting name of added devices");
        List<String> deviceNames = new ArrayList<>();
        for (WebElement deviceName : devicesNames) {
            deviceNames.add(deviceName.getText());
        }
        log.info("Name of added devices: " + deviceNames);
        return deviceNames;
    }

    public List<String> getNameOfAddedDevicesInRestrictedUser() {
        log.info("Getting name of added devices");
        List<String> deviceNames = new ArrayList<>();
        for (WebElement deviceName : restrictedDeviceNames) {
            deviceNames.add(deviceName.getText());
        }
        log.info("Name of added devices: " + deviceNames);
        return deviceNames;
    }

    public void saveAuthProfile() {
        log.info("Saving Authorization Profile");
        commonWaits.clickWithoutInterception(saveConfigurationBtn);
        commonWaits.waitForElementVisible(authProfileSuccessMsg,10);
        log.info("Authorization Profile saved successfully");
    }

}