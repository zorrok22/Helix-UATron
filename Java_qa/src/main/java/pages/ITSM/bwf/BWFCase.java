package pages.ITSM.bwf;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BWFCase extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(BWFCase.class.getName());
    CommonWaits commonWaits;
    CommonMethods commonMethods;

    public BWFCase() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    // Constants for better maintainability
    int DEFAULT_WAIT_TIME = 2000;
    int GO_TO_CASE_WAIT_TIME = 6;
    String AUTO_RESOLVED_STATUS = "Auto Resolved";
    String saveButton = "Save...";
    String saveHereButton = "Save here";
    String REQUESTER_RESPONSE_Q1 = "What is your Business Unit?";
    String REQUESTER_RESPONSE_Q2 = "Why do you need access to Udemy?";
    String REQUESTER_RESPONSE_Q3 = "Which Online training course do you want to take?";
    String REQUESTER_RESPONSE_Q4 = "By when do you need this?";
    String lastCreatedCaseId;
    Pattern CASE_ID_PATTERN = Pattern.compile("([A-Z]{2,}\\d{3,}|\\b\\d{6,}\\b)");
    DateTimeFormatter REQUESTERS_RESPONSE_DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final String CREATE_OPTION="Create";
    public static final String RELATE_OPTION="Relate";
    public static final String ADD_WORK_NOTE="Add work note";

    // String locators as constants for better maintainability
    String DROP_DOWN_CASE_DATA = "//label[contains(. ,'%s')]/ancestor::adapt-rx-select//button";
    String DROPDOWN_VALUE_XPATH = "//button[contains(@class, 'rx-select')]//div[contains(.,'%s')]//parent::button";
    String TYPE_HEAD_INPUT = "//label[contains(. ,'%s')]/ancestor::adapt-rx-typeahead//input";
    String TEMPLATE_NAME_SELECTOR = "//button[contains(. ,'Select Case Template')]";
    String TEMPLATE_REFRESH_BUTTON = "//rx-runtime-view-modal//button[@aria-label='Refresh' or .//*[contains(@class,'d-icon-refresh')] or contains(@class,'d-icon-refresh')]";
    String GO_TO_CASE_BUTTON_XPATH = "//button[contains(. ,'Go to Case')]";
    String CASE_ID_XPATH_FALLBACK = "//*[contains(@aria-label, 'Display Id') or contains(@aria-label,'Display ID') or contains(@aria-label,'DisplayId')] | //*[(self::span or self::div) and (contains(@class,'display-id') or contains(@class,'bwf-ticket'))]";
    String TOAST_DETAILS_XPATH = "//*[contains(@class,'a-toast__details')]";
    String TOAST_GO_TO_CASE_BUTTON = "//*[contains(@class,'a-toast') or contains(@class,'toast')]//*[normalize-space()='Go to Case']/ancestor::button[1]";
    String REQUESTERS_RESPONSE_TITLE = "//*[contains(normalize-space(.),\"Requester's Response\") or contains(normalize-space(.),\"Requester\u2019s Response\")]";
    String REQUESTERS_RESPONSE_OK_BUTTON = "(//*[contains(normalize-space(.),\"Requester's Response\") or contains(normalize-space(.),\"Requester\u2019s Response\")]/following::button[normalize-space()='OK'])[1]";
    String REQUESTERS_RESPONSE_PANEL_ANCESTOR = "ancestor::*[self::div or self::section][contains(@class,'side') or contains(@class,'drawer') or contains(@class,'panel')][1]";
    String REQUESTERS_RESPONSE_ANY_INPUT_NEAR_LABEL = "//*[self::label or self::div or self::span][normalize-space()='%s']/following::input[1]";
    String REQUESTERS_RESPONSE_ANY_TEXTAREA_NEAR_LABEL = "//*[self::label or self::div or self::span][normalize-space()='%s']/following::textarea[1]";
    String REQUESTERS_RESPONSE_PANEL_INPUT_BY_LABEL_FOLLOWING = ".//*[self::label or self::div or self::span][normalize-space()='%s']/following::input[1]";
    String REQUESTERS_RESPONSE_PANEL_INPUT_BY_CONTAINER = ".//*[normalize-space()='%s']/ancestor::div[1]//input[1]";
    String REQUESTERS_RESPONSE_PANEL_TEXTAREA_BY_LABEL_FOLLOWING = ".//*[self::label or self::div or self::span][normalize-space()='%s']/following::textarea[1]";
    String REQUESTERS_RESPONSE_PANEL_TEXTAREA_BY_CONTAINER = ".//*[normalize-space()='%s']/ancestor::div[1]//textarea[1]";
    String REQUESTERS_RESPONSE_GLOBAL_INPUT_BY_LABEL_FOLLOWING = "//*[normalize-space()='%s']/following::input[1]";
    String CREATE_KNOWLEDGE_BUTTON = "//button[contains(text(),'Create knowledge')]";
    String WORK_STATUS_OPTION_BUTTON = "//button[contains(.,'%s')]";
    String PINNED_KNOWLEDGE_ID = "//*[@class='search-item__pin-icon']//ancestor::a[contains(.,'%s')]//div[contains(@class,'end-auto')]";
    String ACTIVITY_TITLE_CONTAINS = "//div[contains(@class,'activity-title') and contains(.,'%s')]";
    String STATUS_REASON_OPTION = "//div[@role='listbox']//button[@role='option']//div[contains(. ,'%s')]";
    String CATEGORY_TIER_VALUE_XPATH = "//label[contains(.,'%s')]/ancestor::adapt-rx-select//div[contains(@class,'rx-select__search-button-title')]";
    String createKnowledgeLocator = CREATE_KNOWLEDGE_BUTTON;
    String workStatusOption = WORK_STATUS_OPTION_BUTTON;
    String getPinnedKnowledgeIDLocator = PINNED_KNOWLEDGE_ID;
    String activityTitleLocator = ACTIVITY_TITLE_CONTAINS;
    String selectStatusReasonValue = STATUS_REASON_OPTION;
    String upcomingTaskXpath = "//bwf-task-list//a[contains(.,'%s')]";
    String viewCaseLocator = "//div[contains(.,'%s')]//button[contains(.,'View Case')]";
    String requesterResponseDateLocator = "//label[contains(. ,'%s')]/ancestor::adapt-rx-datetime//input";
    String requesterResponseTextLocator = "//label[contains(. ,'%s')]/ancestor::adapt-rx-textfield//input";
    String TASK_LINK_BY_TITLE = "//a[contains(@class,'task-summary__name') and contains(.,'%s')]";
    String TASK_LINK_BY_ID = "//div[contains(text(),'%s')]/ancestor::div[@class='task-summary']//a[contains(@class,'task-summary__name')]";
    String dropDownCaseStatus = "//button[@aria-label='%s']";
    String dropdownValue_xpath = "//button[contains(@class, 'rx-select')]//div[contains(.,'%s')]//parent::button";
    String userHintXpath = "//*[contains(@class, 'person-email') and contains(text(), '%s')]/parent::div";
    String taskLinkXpath = "//div[contains(@class,'task-list__task-card')]//a[contains(normalize-space(.),'%s')]";
    String TASK_CELL_BY_SUMMARY = "//bwf-task-card//a[contains(.,'%s')]";
    String EXTERNAL_ID_LABEL_VALUE = "//label[contains(translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'EXTERNAL') and contains(translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'ID')]/following-sibling::*[1]";
    String LINK_BY_TEXT = "//a[normalize-space()='%s' or contains(normalize-space(.), '%s')]";
    String ANY_TEXT_CONTAINS = "//*[contains(normalize-space(.), '%s')]";
    String textFieldValue = "//adapt-rx-textfield[contains(normalize-space(.), '%s')]//input";
    String attachmentInActivityTab = "//span[contains(@class,'bwf-attachment-container__file-name') and contains(text(),'%s')]";
    String APPROVERS_SECTION_PERSON = "//tbody[contains(@class,'ui-table-tbody')]//a[normalize-space()='%s']";
    String NOTIFICATION_LIST_ITEMS = "//*[contains(@class, 'active-messages')]//p";
    String TASK_ID_LINK_NOTIFICATION = "//rx-user-messages//p//a[normalize-space()='%s']";
    String adHocApprover = "//tr[contains(@class,'at-data-row') and contains(., '%s')]//adapt-row-selection-checkbox";
    String approversList = "//tbody[@class='ui-table-tbody']//tr[contains(@class,'at-data-row')]";
    String fieldValueInActivity = "//li[contains(@class,'fields')][.//div[contains(@class,'field') and normalize-space()='%s']]//div[contains(@class,'value') and not(contains(@class,'html'))]";
    String fieldValueInActivityHtml = "//li[contains(@class,'fields')][.//div[contains(@class,'field') and normalize-space()='%s']]//div[contains(@class,'value html')]";
    String sQuestion = "//label[contains(.,'%s')]/..//div[contains(@class, 'field-value') or contains(@class,'read-only')]";
    String row = "//*[self::tr or self::div][contains(normalize-space(.),'%s')]";
    /** Asset picker grid: PrimeNG + CDK virtual scroll; scope to data rows so header/spacer rows are excluded. */
    String rowCheckbox = "//tr[contains(@class,'at-data-row') and contains(normalize-space(.),'%s')]//adapt-row-selection-checkbox";
    String asset = "//*[contains(normalize-space(.),'%s')]";
    /** Related Assets tab record grid — scope avoids matching the same substring elsewhere on the case view. */
    String relatedAssetTextInGrid = "//rx-container[contains(@class,'asset-grid')]//*[contains(normalize-space(.),'%s')]";
    /** Related Tickets → Create: items render under CDK overlay in adapt-dropdown-window2 (dropdown-item buttons). */
    String createMenuOption = "//adapt-dropdown-window2//*[self::button or self::a][contains(normalize-space(.),'%s')]";
    String tab = "//button[@role='tab' and contains(normalize-space(.), '%s')]";
    String searchedTemplateRowRadioBtn = "//rx-runtime-view-modal//a[contains(normalize-space(.) ,'%s')]/ancestor::tr//input/ancestor::adapt-rx-radiobutton";
    String dynamicFieldLocator = "//*[@rx-view-component-id='795026f0-f441-46b7-81c4-736511c800e0']//adapt-rx-control-label[contains(.,'%s')]/..//input";
    String relatedTickets_Fields="//div[contains(@class,'dropdown dr')]//span[text()='%s']";
    String DropdownOptions="//button[@id='dropdownMenu' and contains(.,'%s')]//span";
    String IncidentDropdownOption="(//span[normalize-space()='%s']//following::button)[1]";
    String IncidentDropdownValue="//*[contains(@class, 'rx-select__options')]//button[contains(. ,'%s')]";
    String text_common_xpath="//*[text()='%s']";
    String CaseEditBodyLocator = "//div[contains(@class,'activities px-4')]";
    String workNoteValue = "//button[contains(@class, 'rx-select__option') and contains(. ,'%s')]";
    String collaboratorPill = "//label[normalize-space()='Collaborators' or normalize-space()='Collaborator']/following-sibling::*[1]//*[contains(normalize-space(.), '%s')]";
    String descriptionFrame="//iframe[@title='Description']";

    //WebElement locators
    /**
     * Related Tickets tab: first data row in Adapt/PrimeNG grid (CDK virtual scroll).
     * {@code tr.at-data-row}; excludes {@code ui-table-tbody-spacer}.
     */
    private static final String XPATH_RELATED_TICKETS_FIRST_DATA_ROW =
            "(//div[contains(@class,'ui-table-scrollable-wrapper')]"
                    + "//tbody[contains(@class,'ui-table-tbody')][not(contains(@class,'spacer'))]"
                    + "//tr[contains(@class,'at-data-row')])[1]";
    @FindBy(xpath = XPATH_RELATED_TICKETS_FIRST_DATA_ROW)
    WebElement relatedTicketsGridFirstDataRow;

    @FindBy(xpath = "//button[@role='tab' and contains(. ,'All Templates')]")
    WebElement ALL_TEMPLATES_TAB;
    @FindBy(xpath = "//input[@placeholder='Search']")
    WebElement SEARCH_TEMPLATE_INPUT;
    @FindBy(xpath = "//button[contains(. ,'Save')]")
    WebElement SAVE_BUTTON;
    @FindBy(xpath = "//button[contains(. ,'Go to Case')]")
    WebElement GO_TO_CASE_BUTTON;
    @FindBy(xpath = "//*[contains(@aria-label, 'Display Id')]")
    WebElement CASE_ID;
    @FindBy(xpath = "//button[@role='option']")
    WebElement TYPE_HEAD_OPTION;
    @FindBy(xpath = "//adapt-rx-textfield[contains(. ,'Summary')]//input")
    WebElement SUMMARY_INPUT;
    @FindBy(xpath = " //*[contains(@aria-label,'Rich text editor') and @contenteditable='true']")
    WebElement DESCRIPTION_INPUT;
    @FindBy(xpath = "//button[contains(. ,'Assign To Me')]")
    WebElement ASSIGN_TO_ME_BUTTON;
    @FindBy(xpath = "//button[contains(@class, 'status-transition')]//*[contains(@class, 'btn-content')]//span")
    WebElement txt_CaseStatus;
    @FindBy(xpath = "//*[contains(@class, 'bwf-ticket-summary')]//span")
    WebElement caseSummary;
    @FindBy(xpath = "//button[contains(@class,'accept-all-button')]")
    WebElement acceptAllButton;
    @FindBy(xpath = "//div[contains(@class,'article-assistant__header')]//button")
    WebElement articleHeaderCloseButton;
    @FindBy(xpath = "//button[contains(@class,'dropdown') and contains(.,'Work In Progress')]")
    WebElement workStatusDropdown;
    @FindBy(xpath = "//button[contains(@data-testid,'tabs') and contains(.,'Activity')]")
    WebElement activityTabLocator;
    @FindBy(xpath = "//button[contains(@data-testid,'tabs') and contains(.,'Resources')]")
    WebElement resourceTabLocator;
    @FindBy(xpath = "//*[contains(@id,'titleInput')]")
    WebElement articleTitleLocator;
    @FindBy(xpath = "//div[.='Resolution Description']//ancestor::div[contains(@class,'textarea')]//textarea")
    WebElement resolutionDescriptionInput;
    @FindBy(xpath = "//button[contains(@id,'57061a45')]")
    WebElement toggleButtonLocator;
    @FindBy(css = "[rx-view-component-id='e43fa7f2-66fc-4ac5-bf75-6b94da5e5318'] button")
    WebElement statusReasonDrpDown;
    @FindBy(xpath = "//rx-action-button[@rx-view-component-id='9b92d770-be16-4781-a3c6-a2be8d80bc68']//button")
    WebElement GenerateCaseResolution;
    @FindBy(xpath = "//*[@rx-view-component-id='6b74e29c-c4b0-4854-bbe9-65f0852c380b']//div[contains(@class,'bwf-read')]")
    WebElement ResolutionDescription;
    @FindBy(xpath = "//*[contains(@class, 'a-toast__details')]//span")
    WebElement successMsg;
    @FindBy(xpath = "//*[@rx-view-component-id = 'ee5dd503-a10e-4d22-9ac5-99c400892bb7']//button")
    WebElement btn_saveCase;
    @FindBy(xpath = "//bwf-activity-knowledge-flag")
    WebElement flaggedIconInActivityTab;
    @FindBy(xpath = "//*[contains(@placeholder,'Add a note')]")
    WebElement activityNoteBox;
    @FindBy(xpath = "//*[contains(@aria-label,'Rich text editor') and @contenteditable='true']")
    WebElement activityNotesTextArea;
    @FindBy(xpath = "//div[contains(@class, 'activity-feed')]//input[contains(@class,'checkbox')]/../div/span[text()='Public']")
    WebElement chkBoxPublicComment;
    @FindBy(xpath = "//*[@class = 'activity-feed-note-buttons__right']//button[1]")
    WebElement btnActivityNotesPost;
    @FindBy(xpath = "//bwf-activity-add-note//input[@type='file']")
    WebElement uploadFileInput;
    @FindBy(xpath = "//bwf-activity-ticket-view-count//bwf-activity-title/div[contains(@class,'activity-title')]")
    WebElement viewCountOnCase;
    @FindBy(xpath = "//button[contains(@data-testid,'tabs') and contains(.,'Tasks')]")
    WebElement tasksTabLocator;
    @FindBy(xpath = "//button[normalize-space()='OK' or normalize-space()='Ok']")
    WebElement OK_BUTTON;
    @FindBy(xpath = "//*[self::a or self::button][normalize-space(.)='Reopen' or contains(normalize-space(.),'Reopen')]")
    WebElement REOPEN_LINK_OR_BUTTON;
    @FindBy(xpath = "//span[normalize-space()='Show Approvers']/ancestor::button[1]")
    WebElement ShowApproversButton;
    @FindBy(xpath = "//button[contains(@class, 'd-icon-bell') and contains(@id, 'adapt-nav-actions')]")
    WebElement NOTIFICATION_BELL_ICON;
    @FindBy(xpath = "//span[normalize-space()='Back']/ancestor::button[1]")
    WebElement BACK_FROM_APPROVER_LIST;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Request Approval')]")
    WebElement requestApproval;
    @FindBy(xpath = "//*[@rx-view-component-id='aa178767-b3e5-498d-ba46-186d68f1a685']//input[@placeholder='Search']")
    WebElement approverSearchBox;
    @FindBy(xpath = "//*[@rx-view-component-id='aa178767-b3e5-498d-ba46-186d68f1a685']//*[contains(@class, 'd-icon-search')]/parent::button")
    WebElement approverSearchBtn;
    @FindBy(xpath = "//*[@rx-view-component-id='d4d11f28-2c11-4ef3-96a7-c4c88c02d8e8']//button")
    WebElement addApproverBtn;
    @FindBy(xpath = "(//button[contains(normalize-space(), 'Edit')])[1]")
    WebElement editBtn;
    @FindBy(xpath = "//input[contains(@class, 'adapt-rx-search__input') and @placeholder='Filter']")
    WebElement typeHeadInputField;
    @FindBy(xpath = "//*[@rx-view-component-id='5ba63106-4f56-4729-a69d-36e0722e73c3']//span")
    WebElement fieldSummaryValue;
    @FindBy(xpath = "//label[@aria-label='Summary']/following::div[contains(@class,'bwf-dynamic-field-value')]")
    WebElement fieldTitleValue;
    @FindBy(xpath = "//label[@aria-label='Counter']/following::div[contains(@class,'bwf-dynamic-field-value')]")
    WebElement fieldCounterValue;
    @FindBy(xpath = "//*[@rx-view-component-id='4c010281-507c-4544-ab93-0cc033a0b65e']//div[2]")
    WebElement fieldPriorityValue;
    @FindBy(xpath = "//*[@rx-view-component-id='efb0481e-7604-4c76-9202-4fb42a9ee0af']//p[contains(@class,'person-name')]/a")
    WebElement fieldRequesterValue;
    @FindBy(xpath = "(//*[@rx-view-component-id='d60d5915-fb9a-4d1f-8294-746a735bfca6']//div//button)[1]")
    WebElement fieldContactValue;
    @FindBy(xpath = "//*[@rx-view-component-id='95c038e1-d2fd-466b-b6d8-f0c5de4daa07']//p")
    WebElement fieldAssigneeValue;
    @FindBy(xpath = "(//*[@rx-view-component-id='eb869842-3d5c-40a1-aa31-08258faf2122']//label[text()='Category Tier 1']/following-sibling::*)")
    WebElement fieldCatTier1Value;
    @FindBy(xpath = "//bwf-read-only-field[.//label[normalize-space()='Category Tier 2']]//div[contains(@class,'read-only-content')]")
    WebElement fieldCatTier2Value;
    @FindBy(xpath = "//bwf-read-only-field[.//label[normalize-space()='Category Tier 3']]//div[contains(@class,'read-only-content')]")
    WebElement fieldCatTier3Value;
    @FindBy(xpath = "//bwf-read-only-field[.//label[normalize-space()='Category Tier 4']]//div[contains(@class,'read-only-content')]")
    WebElement fieldCatTier4Value;
    @FindBy(xpath = "(//*[@rx-view-component-id='cd41e867-4676-450a-8536-bfd01460d742']//div)[2]")
    WebElement fieldFlowsetValue;
    @FindBy(xpath = "//*[@rx-view-component-id='df9a7672-5f84-495b-910f-ffbb77e2992f']//button")
    WebElement statusValue;
    @FindBy(xpath = "//*[@rx-view-component-id='76f79080-500d-4ae1-96ba-54dbd3e457a7']//label[text()='Case Template']/following-sibling::*")
    WebElement fieldCaseTemplateValue;
    @FindBy(xpath = "//*[@rx-view-component-id='9aea7c54-3d2b-4548-88cf-01492e9dda45']//label[text()='Source']/following-sibling::*")
    WebElement fieldCaseSourceValue;
    @FindBy(xpath = "//*[@rx-view-component-id='9de4b215-e6c2-4150-b11f-4b4c834f9a76']//label[text()='Line of Business']/following-sibling::*")
    WebElement fieldLineOfBusinessValue;
    @FindBy(xpath = "//*[@rx-view-component-id='95c038e1-d2fd-466b-b6d8-f0c5de4daa07']//div[@class='read-only-hierachy']//div/div")
    WebElement fieldSupportGroupValue;
    @FindBy(xpath = "//*[contains(@class,'read-only-hierachy')]//label[contains(normalize-space(.),'Assigned Group')]//following-sibling::div[contains(@class,'read-only-content')]")
    WebElement fieldAssignedGroupValue;
    @FindBy(xpath = "//*[@rx-view-component-id='9377b9fb-687e-42a9-aebf-3208c70d97d0']//label[text()='External Ticket ID']/following-sibling::*")
    WebElement externalTicketIDValue;
    @FindBy(xpath = "//*[@rx-view-component-id='c44e9302-9f07-4beb-b075-2bb8ddd819f1']//label[text()='Case Site']/following-sibling::*")
    WebElement caseSiteValue;
    @FindBy(xpath = "//label[text()='Request ID']/following-sibling::*[contains(@class, 'read-only-content')]")
    WebElement requestIDValue;
    @FindBy(xpath = "//button[@aria-label='Show more' and .//span[normalize-space()='Show more']]")
    WebElement showMoreButtonInActivity;
    @FindBy(xpath = "//div[contains(@role,'dialog')]//span[contains(.,'Requester')]")
    WebElement requesterDialogTitle;
    @FindBy(xpath = "//button[contains(text(),'Auto-Categorize') or contains(.,'Auto-Categorize')]")
    WebElement autoCategorizeButton;
    @FindBy(xpath = "//button[contains(@class,'btn-primary') and contains(@class,'btn-lg')]//span[normalize-space()='OK']/ancestor::button")
    WebElement requesterResponseOkButton;
    @FindBy(xpath = "//label[contains(.,'What is your Business Unit?')]/ancestor::*[contains(@class,'form-group')]//input[@type='text']")
    WebElement businessUnitInput;
    @FindBy(xpath = "//label[contains(.,'Why do you need access to Udemy?')]/ancestor::*[contains(@class,'form-group')]//input[@type='text']")
    WebElement udemyAccessReasonInput;
    @FindBy(xpath = "//label[contains(.,'Which Online training course do you want to take?')]/ancestor::*[contains(@class,'form-group')]//input[@type='text']")
    WebElement onlineCourseInput;
    @FindBy(xpath = "//label[contains(.,'By when do you need this?')]/ancestor::*[contains(@class,'form-group')]//input[@type='text']")
    WebElement requiredByDateInput;
    @FindBy(xpath = "//button[contains(@class,'btn')]//span[@class='btn-content']//span[normalize-space()='Reopen']")
    WebElement reopenButton;
    @FindBy(xpath = "//div[@class='task-assignee__field']//bwf-assign-to-me//button")
    WebElement assignToMeButtonOnTask;
    @FindBy(xpath = "//bwf-mark-complete-action//button")
    WebElement markAsCompleteButtonOnTask;
    @FindBy(xpath = "//button[contains(@data-testid,'tabs') and (contains(.,'Access') or contains(.,'Case Access'))]")
    WebElement caseAccessTab;
    @FindBy(xpath = "//button[contains(@class,'btn-title') and contains(.,'Agent Access')]")
    WebElement agentAccessTab;
    @FindBy(xpath = "//label[normalize-space()='Agent']/ancestor::div[contains(@class,'agent-wrapper')]//button[normalize-space()='Add']")
    WebElement addAgentAccessBtn;
    @FindBy(xpath = "//div[contains(.,'Agent Access')]/ancestor::div[contains(@class,'access-group')]//button[contains(@class,'btn-title')]")
    WebElement addBtn;
    @FindBy(xpath = "//input[@role='combobox' and @aria-label='Agent' and contains(@placeholder,\"Agent's name\")]")
    WebElement agentSearchInput;
    @FindBy(xpath = "(//button[@role='combobox' and @aria-label='Support Group' and contains(@placeholder,\"Select\")])[2]")
    WebElement confidSearchInput;
    @FindBy(xpath = "//button[contains(@class,'picker-item') or @role='option'][1]")
    WebElement firstResult;
    @FindBy(xpath = "//*[contains(normalize-space(.),'Can edit') or contains(normalize-space(.),'Write') or contains(normalize-space(.),'Edit')]//input[@type='checkbox' or @type='radio']")
    WebElement editToggle;
    @FindBy(xpath = "//div[contains(text(),' Confidential Support Groups')]//parent::div[contains(@class,'access-group')]//button[contains(@class,'btn-title') and contains(.,' Support Group Access ')]")
    WebElement addConfidentialSupportGroupButton;
    @FindBy(xpath = "//*[contains(normalize-space(.),'Confidential') and contains(normalize-space(.),'Support')]/ancestor::*[1]//button[contains(normalize-space(.),'Add') or contains(normalize-space(.),'Add/remove')] | //button[contains(normalize-space(.),'Add') and contains(normalize-space(..),'Confidential')]")
    WebElement addConfidentialSupportGroupBtn;
    @FindBy(xpath = "//button[contains(normalize-space(.) , 'Go to Case')]")
    WebElement goToCaseButton;
    @FindBy(xpath = "//*[self::button or self::a][contains(normalize-space(.),'View Survey') or contains(normalize-space(.),'View survey') or contains(normalize-space(.),'View survey response')]")
    WebElement VIEW_SURVEY_LINK_OR_BUTTON;
    /** Relate/add control on Related Assets: newer builds use label "Assets" (plus); older builds used "Relate … Asset". */
    @FindBy(xpath = "//button[(contains(normalize-space(.),'Relate') and contains(normalize-space(.),'Asset'))] | //rx-action-button[.//span[normalize-space()='Assets']]//button")
    WebElement relateAssetsButton;
    @FindBy(xpath = "//button[contains(@class,'btn-primary') and .//span[normalize-space()='Search']]")
    WebElement relateAssetsGridSearchBtn;
    @FindBy(xpath = "(//div[contains(@class, 'input-wrapper')]//input)[2]")
    WebElement relateAssetsGridSearchInput;
    @FindBy(xpath = "//button[normalize-space()='Search' or contains(normalize-space(.),'Search')]")
    WebElement searchBtn;
    @FindBy(xpath = "//button[normalize-space()='Relate' or normalize-space()='Add' or normalize-space()='Apply' or normalize-space()='OK']")
    WebElement relateAddCIBtn;
    @FindBy(xpath = "//button[normalize-space()='Create' or .//span[normalize-space()='Create']]")
    WebElement createBtn;
    @FindBy(xpath = "//button[contains(@class, 'dropdown-toggle')]//span[text()='Create']")
    WebElement CreateRelatedticketButton;
    @FindBy(xpath = "//*[@rx-view-component-id='f348e681-ac02-452c-b37f-009ac4434053']//button")
    WebElement applyButtonInTemplateModal;
    @FindBy(xpath = "//*[@role='heading' and text()=\"Requester's Response\"]")
    WebElement requestersResponseTitle;
    @FindBy(css = "[rx-view-component-id='d459dabe-1ccb-4776-b121-874ed4ded902'] button")
    public WebElement lnk_okCase;
    @FindBy(xpath = "//rx-action-button[.//span[normalize-space()='Assets']]//button")
    WebElement assetsButton;

    @FindBy(xpath = "//button[contains(@class,'dropdown-item')]")
    public List<WebElement> Create_DropDown_options;

    @FindBy(xpath = "//div[contains(@class,'dropdown-menu')]//button[normalize-space()='Incident']")
    public WebElement IncidentOption;

    @FindBy(xpath ="//input[contains(@id,'rx-search') and @placeholder='Filter']")
    WebElement IncidentTypeTextBox;

    @FindBy(xpath = "(//div[contains(@class,'rx-custom-cell')])[3]")
    public WebElement RelationShip_Type_Status;

    @FindBy(xpath ="//tbody[@class='ui-table-tbody']//a")
    public WebElement INC_Number;

    @FindBy(xpath ="//span[contains(@class,'username')]")
    public WebElement Profile_Username;

    @FindBy(xpath ="//button[@aria-label='Profile menu']")
    public WebElement Profile_Icon;

    @FindBy(xpath ="//*[contains(@class,'ui-chkbox-box')]")
    public WebElement Case_checkbox;

    @FindBy(xpath ="(//*[contains(@aria-label,'Rich text editor') and @contenteditable='true'])[2]")
    public WebElement Add_work_note_TextBox;

    @FindBy(css ="bwf-activity-ticket-view-count > div > div.activity__body > bwf-activity-title > div > span")
    public WebElement Recent_Activity;

    @FindBy(xpath ="//button[contains(@class,'bwf-mr-sm')]")
    public WebElement Post_Button;

    @FindBy(xpath ="//body[contains(text(),'added to the')]")
    public WebElement CaseBodyLocator;

    @FindBy(xpath ="//rx-rich-text[@rx-view-component-id='72010a6d-c0d7-433a-ab9d-3203fb373518']//span")
    public WebElement CreatedToDoTask;

    @FindBy(xpath = "//*[contains(@placeholder,'Add a note')]")
    public WebElement addNote;

    @FindBy(xpath ="//*[@rx-view-component-id='6d761b3d-90ad-4d00-b66f-5dda8572bc56']//*[contains(@title,'Bold')]")
    WebElement boldInAddWorkNote;

    @FindBy(xpath ="//a[contains(@class,'italic')]")
    WebElement italic;

    @FindBy(xpath ="(//button[contains(@id,'italic')])[2]")
    WebElement italicInAddWorkNote;

    @FindBy(xpath ="//a[contains(@class,'underline')]")
    WebElement underline;

    @FindBy(xpath ="(//button[contains(@id,'underline')])[2]")
    WebElement underlineInAddWorkNote;

    @FindBy(xpath ="//a[contains(@class,'textcolor')]")
    WebElement colorPicker;
    @FindBy(xpath ="//a[@class='cke_colorbox' and contains(@title,'Dark Violet')]")
    WebElement violet;

    @FindBy(xpath ="//span[@class='cke_toolgroup']//a[contains(@class,'link')]")
    WebElement link;

    @FindBy(xpath = "//label[text()='Display Text']/parent::div//input[contains(@class,'input_text')]")
    WebElement displayText;

    @FindBy(xpath = "//label[text()='URL']/parent::div//input[contains(@class,'input_text')]")
    WebElement URL_locator;

    @FindBy(xpath="//a[@title='OK']")
    WebElement okButtonLocator;

    @FindBy(xpath ="//*[@rx-view-component-id='72105367-23ba-4ad0-9234-707832ff91a5']//div[@class='activities px-4']//div[@class='content pt-2 html']//iframe")
    WebElement iframe;

    @FindBy(xpath = "//button[@aria-label='Note type']")
    WebElement workNoteTypeDropdown;

    @FindBy(xpath = "//label[normalize-space()='Multi-select Question']/following-sibling::*//*[contains(@class, 'bwf-dynamic-field-value')]")
    WebElement multiSelectQuestionValue;

    @FindBy(xpath = "//label[normalize-space()='Radio Button Question']/following-sibling::*[contains(@class, 'read-only-content')]")
    WebElement radioButtonQuestionValue;

    @FindBy(xpath = "(//body[@class='fr-view'])[1]")
    WebElement descriptionValue;

    @FindBy(xpath = "//*[@rx-view-component-id='72105367-23ba-4ad0-9234-707832ff91a5']//div[@class='activities px-4']//bwf-activity-title/div")
    List<WebElement> activityTabTitle;


    /**
     * Sets a value in a typehead input field
     *
     * @param labelName The label name of the input field (must not be null or empty)
     * @param value     The value to set (must not be null or empty)
     * @throws IllegalArgumentException if labelName or value is null or empty
     */
    public BWFCase setTypeHeadInput(String labelName, String value) {
        if (labelName == null || labelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }

        try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(TYPE_HEAD_INPUT, labelName)), 20),
                    "Typehead input field with label '" + labelName + "' is not visible.");
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            webElement.sendKeys(value);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), TYPE_HEAD_OPTION, 20),
                    "Typehead option with value '" + value + "' is not visible.");
            commonWaits.waitForElementVisible(TYPE_HEAD_OPTION).click();
            log.info("Successfully set typehead input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set typehead input for " + labelName + ": " + e.getMessage());
            throw new RuntimeException("Failed to set typehead input for " + labelName, e);
        }
        return this;
    }

    /**
     * Sets the summary field
     *
     * @param summary The summary text (must not be null)
     * @throws IllegalArgumentException if summary is null
     */
    public BWFCase setSummary(String summary) {
        if (summary == null) {
            throw new IllegalArgumentException("Summary cannot be null");
        }
        commonWaits.waitForElementVisible(SUMMARY_INPUT).sendKeys(summary);
        log.info("Set summary to: " + summary);
        return this;
    }

    /**
     * Sets the description field
     *
     * @param description The description text (must not be null)
     * @throws IllegalArgumentException if description is null
     */
    public BWFCase setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        commonWaits.waitForElementVisible(DESCRIPTION_INPUT).sendKeys(description);
        log.info("Set description to: " + description);
        return this;
    }

    /**
     * Sets the Business Unit field
     *
     * @param businessUnit The business unit value (must not be null)
     * @throws IllegalArgumentException if businessUnit is null
     */
    public BWFCase setBusinessUnit(String businessUnit) {
        if (businessUnit == null) {
            throw new IllegalArgumentException("Business Unit cannot be null");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(businessUnitInput);
        businessUnitInput.clear();
        businessUnitInput.sendKeys(businessUnit);
        log.info("Set Business Unit to: " + businessUnit);
        return this;
    }

    /**
     * Sets the Udemy Access Reason field
     *
     * @param reason The reason for needing Udemy access (must not be null)
     * @throws IllegalArgumentException if reason is null
     */
    public BWFCase setUdemyAccessReason(String reason) {
        if (reason == null) {
            throw new IllegalArgumentException("Udemy Access Reason cannot be null");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(udemyAccessReasonInput);
        udemyAccessReasonInput.clear();
        udemyAccessReasonInput.sendKeys(reason);
        log.info("Set Udemy Access Reason to: " + reason);
        return this;
    }

    /**
     * Sets the Online Course field
     *
     * @param courseName The online training course name (must not be null)
     * @throws IllegalArgumentException if courseName is null
     */
    public BWFCase setOnlineCourse(String courseName) {
        if (courseName == null) {
            throw new IllegalArgumentException("Online Course name cannot be null");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(onlineCourseInput);
        onlineCourseInput.clear();
        onlineCourseInput.sendKeys(courseName);
        log.info("Set Online Course to: " + courseName);
        return this;
    }

    /**
     * Sets the Required By Date field
     *
     * @param date The date value (must not be null, format: MM/DD/YYYY)
     * @throws IllegalArgumentException if date is null
     */
    public BWFCase setRequiredByDate(String date) {
        if (date == null) {
            throw new IllegalArgumentException("Required By Date cannot be null");
        }
        String futureDate = getFutureDate(Integer.parseInt(date), "MM/dd/yyyy");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(requiredByDateInput);
        requiredByDateInput.clear();
        requiredByDateInput.sendKeys(futureDate);
        log.info("Set Required By Date to: " + futureDate);
        return this;
    }

    /**
     * Sets a value in a dropdown field
     *
     * @param labelName The label name of the dropdown field (must not be null or empty)
     * @param value     The value to select from dropdown (must not be null or empty)
     * @throws IllegalArgumentException if labelName or value is null or empty
     */
    public BWFCase setDropDownValues(String labelName, String value) {
        validateInputParameters(labelName, value);

        try {
            commonWaits.waitForSometime(DEFAULT_WAIT_TIME);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(DROP_DOWN_CASE_DATA, labelName)), 20),
                    "Dropdown with label '" + labelName + "' is not visible.");
            commonWaits.clickWithoutInterception(By.xpath(String.format(DROP_DOWN_CASE_DATA, labelName)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(DROPDOWN_VALUE_XPATH, value)), 20),
                    "Dropdown value '" + value + "' for label '" + labelName + "' is not visible.");
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_VALUE_XPATH, value))).click();
            log.info("Successfully set dropdown " + labelName + " to " + value);
        } catch (Exception e) {            log.error("Failed to set dropdown value for " + labelName + ": " + e.getMessage());
            throw new RuntimeException("Failed to set dropdown value for " + labelName, e);
        }
        return this;
    }

    public BWFCase validateInputParameters(String labelName, String value) {
        if (labelName == null || labelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        return this;
    }

    /**
     * Sets the template name in the case
     *
     * @param templateName The name of the template to select (must not be null or empty)
     * @throws IllegalArgumentException if templateName is null or empty
     */
    public BWFCase setTemplateName(String templateName) {
        if (templateName == null || templateName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }

        commonWaits.waitForByElementToBeVisible(By.xpath(TEMPLATE_NAME_SELECTOR)).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ALL_TEMPLATES_TAB, 10), "All Templates tab is not visible in template selection modal");
        commonWaits.clickByJS(ALL_TEMPLATES_TAB);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        WebElement searchInput = commonWaits.waitForElementVisible(SEARCH_TEMPLATE_INPUT);
        if (isElementPresent(By.xpath(TEMPLATE_REFRESH_BUTTON))) {
            commonWaits.clickWithoutInterception(By.xpath(TEMPLATE_REFRESH_BUTTON));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        searchInput.clear();
        searchInput.sendKeys(templateName + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        By templateRadioBy = By.xpath(String.format(searchedTemplateRowRadioBtn, templateName));
        Assert.assertTrue(
                commonWaits.isElementPresent(templateRadioBy, 20),
                "Template radio button is not visible for template name: " + templateName
        );
        commonWaits.clickWithoutInterception(templateRadioBy);
        commonWaits.waitForElementClickable(applyButtonInTemplateModal).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Successfully set template name to " + templateName);
        return this;
    }

    /**
     * Clicks the Assign To Me button
     */
    public BWFCase clickOnAssignToMeButton() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ASSIGN_TO_ME_BUTTON, 20), "Assign To Me button is not visible");
        commonWaits.waitForElementVisible(ASSIGN_TO_ME_BUTTON);
        commonWaits.scrollToSpecificElement(ASSIGN_TO_ME_BUTTON);
        commonWaits.clickWithoutInterception(ASSIGN_TO_ME_BUTTON);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        try {
            BWFCase bwfCase = new BWFCase();
            Assert.assertTrue(bwfCase.getCaseTransitionStatus().equals(SolQAConstants.ASSIGNED));
        } catch (AssertionError e) {
            ASSIGN_TO_ME_BUTTON.click();
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            BWFCase bwfCase = new BWFCase();
            Assert.assertTrue(bwfCase.getCaseTransitionStatus().equals(SolQAConstants.ASSIGNED));
        }
        log.info("Clicked on Assign To Me button");
        return this;
    }

    /**
     * Fills case data from a map of field names to values
     *
     * @param caseData Map containing case field data (must not be null)
     * @throws IllegalArgumentException if caseData is null
     */
    public BWFCase fillCaseData(Map<Object, String> caseData) {
        if (caseData == null) {
            throw new IllegalArgumentException("Case data map cannot be null");
        }

        fillDropDownIfPresent(caseData, SolQAConstants.CREATE_CASE_LINE_OF_BUSINESS);
        // Fill typehead inputs
        commonWaits.switchToLastWindow();
        fillTypeHeadInputIfPresent(caseData, SolQAConstants.REQUESTER_LABEL);
        fillTypeHeadInputIfPresent(caseData, SolQAConstants.CONTACT_LABEL);
        // Fill dropdown values
        fillDropDownIfPresent(caseData, SolQAConstants.PRIORITY);
        fillDropDownIfPresent(caseData, SolQAConstants.Source);
        fillDropDownIfPresent(caseData, SolQAConstants.Label);
        fillDropDownIfPresent(caseData, SolQAConstants.Case_Site);
        fillDropDownIfPresent(caseData, SolQAConstants.CATEGORY_TIER_1);
        fillDropDownIfPresent(caseData, SolQAConstants.CATEGORY_TIER_2);
        fillDropDownIfPresent(caseData, SolQAConstants.CATEGORY_TIER_3);
        fillDropDownIfPresent(caseData, SolQAConstants.CATEGORY_TIER_4);
        fillDropDownIfPresent(caseData, SolQAConstants.Assigned_Group);
        fillDropDownIfPresent(caseData, SolQAConstants.Assignee_Label);

        // Set template
        if (caseData.get("Template Name") != null) {
            setTemplateName(caseData.get("Template Name"));
        }

        // Some templates require requester responses before Save will create/open the case
//        fillRequestersResponseIfPresent(caseData);

        // Set text fields
        if (caseData.get("Summary") != null) {
            setSummary(caseData.get("Summary"));
        }
        if (caseData.get("Description") != null) {
            setDescription(caseData.get("Description"));
        }
        // Handle assign to me
        commonWaits.waitForSometime(commonWaits.moreDelay); //This wait is required assign to me button is not visible immediately
        if (caseData.get("Assign To Me") != null) {
            clickOnAssignToMeButton();
        }
        // Save and navigate to case
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(SAVE_BUTTON);

        boolean isRequesterResponseSet = false;
        // Set requester response fields (dynamic fields)
        if (caseData.get("BusinessUnit") != null) {
            setBusinessUnit(caseData.get("BusinessUnit"));
            isRequesterResponseSet = true;

        }
        if (caseData.get("UdemyAccessReason") != null) {
            setUdemyAccessReason(caseData.get("UdemyAccessReason"));
            isRequesterResponseSet = true;
        }
        if (caseData.get("OnlineCourse") != null) {
            setOnlineCourse(caseData.get("OnlineCourse"));
            isRequesterResponseSet = true;
        }
        if (caseData.get("DaysToAdd") != null) {
            setRequiredByDate(caseData.get("DaysToAdd"));
            isRequesterResponseSet = true;
        }

        // Click OK button on requester response dialog if any fields were filled
        if (isRequesterResponseSet) {
            log.info("Clicking OK button on requester response dialog");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForElementVisible(requesterResponseOkButton);
            commonWaits.clickWithoutInterception(requesterResponseOkButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Clicked OK button successfully");
        }

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();

        if (commonWaits.isElementPresent(requestersResponseTitle)) {
            handleIntegrationPromptIfPresent(caseData);
        }
        waitForCaseToOpenAfterSave();
        log.info("Successfully filled case data and navigated to case");
        return this;
    }

    public void fillRequestersResponseIfPresent(Map<Object, String> caseData) {
        try {
            List<WebElement> titleEls = findElements(By.xpath(REQUESTERS_RESPONSE_TITLE));
            if (titleEls.isEmpty()) {
                return;
            }

            WebElement panelRoot = null;
            try {
                for (WebElement t : titleEls) {
                    try {
                        if (t != null && t.isDisplayed()) {
                            panelRoot = t.findElement(By.xpath(REQUESTERS_RESPONSE_PANEL_ANCESTOR));
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
            if (panelRoot == null) {
                // Fallback: operate globally if we can't find a good root
                panelRoot = titleEls.get(0);
            }

            // Fill any question values provided in test data map (keys are the question text)
            for (Map.Entry<Object, String> e : caseData.entrySet()) {
                if (e == null || e.getKey() == null) continue;
                String question = String.valueOf(e.getKey()).trim();
                String answer = e.getValue();
                if (answer == null) continue;

                // Only try obvious question-like keys to avoid accidental matches
                if (!question.endsWith("?")) continue;

                try {
                    WebElement input = findRequesterResponseInput(panelRoot, question);
                    if (input != null) {
                        commonWaits.scrollToSpecificElement(input);
                        try {
                            input.clear();
                        } catch (Exception ignored) {
                        }

                        String valueToType = answer;
                        if ("By when do you need this?".equalsIgnoreCase(question) && answer.toLowerCase().contains("next")) {
                            valueToType = LocalDate.now().plusDays(1).format(REQUESTERS_RESPONSE_DATE_FMT);
                        }

                        setInputValueStrong(input, valueToType);
                    }
                } catch (Exception ignored) {
                }
            }

            // Click OK to close panel and apply values
            if (!findElements(By.xpath(REQUESTERS_RESPONSE_OK_BUTTON)).isEmpty()) {
                WebElement ok = findElements(By.xpath(REQUESTERS_RESPONSE_OK_BUTTON)).get(0);
                commonWaits.scrollToSpecificElement(ok);
                try {
                    clickByJS(ok);
                } catch (Exception ignored) {
                    commonWaits.clickWithoutInterception(ok);
                }
            }

            // Wait for panel to close
            long end = System.currentTimeMillis() + 15000L;
            while (System.currentTimeMillis() < end) {
                if (findElements(By.xpath(REQUESTERS_RESPONSE_TITLE)).isEmpty()) {
                    break;
                }
                commonWaits.waitForSometime(500);
            }

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
        } catch (Exception ignored) {
        }
    }

    public WebElement findRequesterResponseInput(WebElement panelRoot, String question) {
        String q = question.replace("'", "\\'");
        By[] candidates = new By[]{
                // Label → next input
                By.xpath(String.format(REQUESTERS_RESPONSE_PANEL_INPUT_BY_LABEL_FOLLOWING, q)),
                // Container → input
                By.xpath(String.format(REQUESTERS_RESPONSE_PANEL_INPUT_BY_CONTAINER, q)),
                // Label → textarea (just in case)
                By.xpath(String.format(REQUESTERS_RESPONSE_PANEL_TEXTAREA_BY_LABEL_FOLLOWING, q)),
                By.xpath(String.format(REQUESTERS_RESPONSE_PANEL_TEXTAREA_BY_CONTAINER, q))
        };

        for (By by : candidates) {
            try {
                List<WebElement> els = panelRoot.findElements(by);
                for (WebElement el : els) {
                    try {
                        if (el != null && el.isDisplayed()) return el;
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Global fallback if panelRoot search fails
        try {
            List<WebElement> els = findElements(By.xpath(String.format(REQUESTERS_RESPONSE_GLOBAL_INPUT_BY_LABEL_FOLLOWING, q)));
            for (WebElement el : els) {
                try {
                    if (el != null && el.isDisplayed()) return el;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        // Another global fallback: label-like elements can be div/span; try those explicitly
        try {
            List<WebElement> els = findElements(By.xpath(String.format(REQUESTERS_RESPONSE_ANY_INPUT_NEAR_LABEL, question)));
            for (WebElement el : els) {
                try {
                    if (el != null && el.isDisplayed()) return el;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        try {
            List<WebElement> els = findElements(By.xpath(String.format(REQUESTERS_RESPONSE_ANY_TEXTAREA_NEAR_LABEL, question)));
            for (WebElement el : els) {
                try {
                    if (el != null && el.isDisplayed()) return el;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public boolean isRequestersResponsePanelOpen() {
        List<WebElement> titles = findElements(By.xpath(REQUESTERS_RESPONSE_TITLE));
        for (WebElement t : titles) {
            if (t != null && t.isDisplayed()) return true;
        }
        return false;
    }

    public void setInputValueStrong(WebElement input, String value) {
        input.click();
        input.clear();
        input.sendKeys(value);

        // For controlled inputs, set value via JS and dispatch events
        // BasePage.executeJS only supports passing a single WebElement arg; use literal string for value
        String escaped = value.replace("\\", "\\\\").replace("'", "\\'");
        executeJS(
                "const el=arguments[0];" +
                        "el.focus();" +
                        "el.value='" + escaped + "';" +
                        "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                        "el.dispatchEvent(new Event('change',{bubbles:true}));" +
                        "el.dispatchEvent(new Event('blur',{bubbles:true}));",
                input
        );
    }

    public void waitForCaseToOpenAfterSave() {
        long end = System.currentTimeMillis() + (GO_TO_CASE_WAIT_TIME * 1000L);
        while (System.currentTimeMillis() < end) {
            try {
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            } catch (Exception ignored) {
            }

            // 1) If case id is already visible, we are on the case page.
            if (isCaseIdVisibleNow()) {
                return;
            }

            // 2) If "Go to Case" is present, click it (prefer JS click to avoid overlays).
            if (clickGoToCaseIfPresent()) {
                try {
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    commonWaits.waitForExecutionComplete();
                } catch (Exception ignored) {
                }
                if (isCaseIdVisibleNow()) {
                    return;
                }
            }

            commonWaits.waitForSometime(1000);
        }

        // Final short attempt: try clicking again if present
        clickGoToCaseIfPresent();

        // If case still didn't open, capture Case ID from toast (case is created) and let caller open it via Case Console.
        if (!isCaseIdVisibleNow()) {
            String toastText = readToastDetailsText();
            String parsed = extractCaseId(toastText);
            if (parsed != null) {
                lastCreatedCaseId = parsed;
                log.info("Case appears created but not opened yet. Captured Case ID from toast: " + parsed);
                return;
            }
            log.warn("Case did not open after Save and Case ID could not be read yet. Toast: " + toastText);
        }
    }

    public boolean isCaseIdVisibleNow() {
        try {
            List<WebElement> ids = findElements(By.xpath(CASE_ID_XPATH_FALLBACK));
            for (WebElement el : ids) {
                try {
                    if (el != null && el.isDisplayed() && el.getText() != null && !el.getText().trim().isEmpty()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean clickGoToCaseIfPresent() {
        try {
            // Prefer toast "Go to Case" button if present (often the only reliable one)
            try {
                List<WebElement> toastBtn = findElements(By.xpath(TOAST_GO_TO_CASE_BUTTON));
                for (WebElement b : toastBtn) {
                    try {
                        if (b != null && b.isDisplayed()) {
                            commonWaits.scrollToSpecificElement(b);
                            clickByJS(b);
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }

            List<WebElement> buttons = findElements(By.xpath(GO_TO_CASE_BUTTON_XPATH));
            if (buttons.isEmpty()) return false;

            // Prefer the last displayed match (active footer/overlay often renders last).
            for (int i = buttons.size() - 1; i >= 0; i--) {
                try {
                    WebElement b = buttons.get(i);
                    if (b != null && b.isDisplayed()) {
                        commonWaits.scrollToSpecificElement(b);
                        try {
                            clickByJS(b);
                        } catch (Exception ignored) {
                            commonWaits.clickWithoutInterception(b);
                        }
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public String readToastDetailsText() {
        try {
            List<WebElement> details = findElements(By.xpath(TOAST_DETAILS_XPATH + "//span"));
            for (WebElement el : details) {
                try {
                    if (el != null && el.isDisplayed()) {
                        String t = el.getText();
                        if (t != null && !t.trim().isEmpty()) return t.trim();
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String extractCaseId(String text) {
        if (text == null) return null;
        try {
            Matcher m = CASE_ID_PATTERN.matcher(text);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Helper method to fill typehead input if value is present
     *
     * @param caseData  The case data map
     * @param fieldName The field name to check and fill
     */
    public BWFCase fillTypeHeadInputIfPresent(Map<Object, String> caseData, String fieldName) {
        String value = caseData.get(fieldName);
        if (value != null) {
            setTypeHeadInput(fieldName, value);
        }
        return this;
    }

    /**
     * Helper method to fill dropdown if value is present
     *
     * @param caseData  The case data map
     * @param fieldName The field name to check and fill
     */
    public BWFCase fillDropDownIfPresent(Map<Object, String> caseData, String fieldName) {
        String value = caseData.get(fieldName);
        if (value != null) {
            setDropDownValues(fieldName, value);
        }
        return this;
    }

    /**
     * Gets the case ID from the case details
     *
     * @return The case ID
     */
    public String getCaseID() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), CASE_ID, 30), "Case ID is not visible");
        return CASE_ID.getText().trim();
    }

    /**
     * Click Save button
     */
    public BWFCase clickOnSaveButton() {
        log.info("Clicking Save button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(SAVE_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    /**
     * Click Go to Case button
     */
    public BWFCase clickOnGoToCaseButton() {
        log.info("Clicking Go to Case button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementClickable(GO_TO_CASE_BUTTON, GO_TO_CASE_WAIT_TIME).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    /**
     * Gets the case status
     *
     * @return The case status
     */
    public String getCaseTransitionStatus() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        String statusValue = commonWaits.waitForElementVisible(txt_CaseStatus, 10).getText().trim();
        log.info("******The status is : " + statusValue.trim());
        return statusValue;
    }

    /**
     * Changes the case status to the specified value
     *
     * @param status The status to set (must not be null or empty)
     * @throws IllegalArgumentException if status is null or empty
     */
    public BWFCase changeCaseStatusTo(String status) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), txt_CaseStatus, 20), "Status is not visible");
            commonWaits.clickWithoutInterception(txt_CaseStatus);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Label)), 20), "Status is not visible");
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Label))).click();
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownValue_xpath, status)), 10), "Status dropdown value '" + status + "' is not visible");
            commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownValue_xpath, status)));
            log.info("Selected " + status + " for Status Dropdown");

            // Handle special case for Resolved status
            if (SolQAConstants.RESOLVED.equalsIgnoreCase(status)) {
                handleResolvedStatus();
            }
            if (SAVE_BUTTON.isEnabled()) {
                commonWaits.clickWithoutInterception(SAVE_BUTTON);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),txt_CaseStatus, 20), "Case status element is not visible after saving");
            }
            log.info("Successfully changed case status to " + status);
        } catch (Exception e) {
            log.error("Failed to change case status to " + status + ": " + e.getMessage());
            throw new RuntimeException("Failed to change case status to: " + status, e);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase changeCaseStatusToResolvedWithNotes(Map<Object, String> caseData) {
        String status = SolQAConstants.RESOLVED;
        try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),txt_CaseStatus, 20), "Case status element is not visible");
            log.info("Changing case status to Resolved with notes");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForAngularRequestsToFinish();
            commonWaits.clickWithoutInterception(txt_CaseStatus);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Label)), 20), "Status dropdown is not visible");
            commonWaits.clickWithoutInterception(By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Label)));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),By.xpath(String.format(dropdownValue_xpath, status)), 10), "Status dropdown value '" + status + "' is not visible");
            commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownValue_xpath, status)));
            log.info("Selected " + status + " for Status Dropdown");

            // Handle special case for Resolved status
            handleResolvedStatus();
            String value = caseData.get("Resolution Description");
            if (value != null) {
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), resolutionDescriptionInput, 10), "Resolution Description input is not visible");
                resolutionDescriptionInput.clear();
                resolutionDescriptionInput.sendKeys(value);
            }
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
            Assert.assertTrue(commonWaits.isElementClickable(SAVE_BUTTON), "Save button is not clickable");
            commonWaits.clickWithoutInterception(SAVE_BUTTON);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Successfully changed case status to " + status);
            Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), String.format(dropDownCaseStatus, SolQAConstants.Status_Label), 20), "Status dropdown is still visible");
        } catch (Exception e) {
            log.error("Failed to change case status to " + status + ": " + e.getMessage());
            throw new RuntimeException("Failed to change case status to: " + status, e);
        }
        return this;
    }

    /**
     * Handles the special case when status is set to Resolved
     */
    public BWFCase handleResolvedStatus() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Reason)), 10), "Status Reason dropdown is not visible after selecting Resolved status");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Reason))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownValue_xpath, AUTO_RESOLVED_STATUS)), 10), "Auto Resolved option is not visible in Status Reason dropdown");
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownValue_xpath, AUTO_RESOLVED_STATUS)));
        return this;
    }

    /**
     * Verifies case data against expected values
     *
     * @param caseData Map containing expected case data (must not be null)
     * @throws IllegalArgumentException if caseData is null
     */
    public BWFCase verifyCaseData(Map<Object, String> caseData) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (caseData == null) {
            throw new IllegalArgumentException("Case data map cannot be null");
        }

        log.info("Starting case data verification");
        verifyFieldIfPresent(caseData, SolQAConstants.SUMMARY_LABEL, this::getCaseSummary);
        verifyFieldIfPresent(caseData, SolQAConstants.Status_Label, this::getCaseTransitionStatus);
        verifyFieldIfPresent(caseData, SolQAConstants.Source, () -> getCaseDetail("Source"));
        verifyFieldIfPresent(caseData, SolQAConstants.CATEGORY_TIER_1, () -> getCaseDetail("Category Tier 1"));
        verifyFieldIfPresent(caseData, SolQAConstants.CATEGORY_TIER_2, () -> getCaseDetail("Category Tier 2"));
        verifyFieldIfPresent(caseData, SolQAConstants.CATEGORY_TIER_3, () -> getCaseDetail("Category Tier 3"));
        verifyFieldIfPresent(caseData, SolQAConstants.CASE_TEMPLATE, () -> getCaseDetail("Case Template"));
        verifyFieldIfPresent(caseData, SolQAConstants.Flowset, () -> getCaseDetail("Flowset"));
        verifyFieldIfPresent(caseData, SolQAConstants.Support_Group, () -> getCaseDetail("Support Group"));
        verifyFieldIfPresent(caseData, SolQAConstants.Requester_Full_Name, () -> getCaseDetail("Requester"));
        verifyFieldIfPresent(caseData, SolQAConstants.Requester_Email, () -> getCaseDetail("Requester Email"));
        verifyFieldIfPresent(caseData, SolQAConstants.CONTACT_LABEL, () -> getCaseDetail("Contact"));
        verifyFieldIfPresent(caseData, SolQAConstants.Assignee_Label, () -> getCaseDetail("Assignee"));
        verifyFieldIfPresent(caseData, SolQAConstants.LINE_OF_BUSINESS, () -> getCaseDetail("Line of Business"));

        log.info("Case data verification completed successfully");
        return this;

    }

    /**
     * Verifies a field if it's present in the case data map
     *
     * @param caseData      The case data map
     * @param fieldName     The field name to verify
     * @param valueSupplier Supplier function to get the actual value
     */
    public BWFCase verifyFieldIfPresent(Map<Object, String> caseData, String fieldName, Supplier<String> valueSupplier) {
        String expectedValue = caseData.get(fieldName);
        if (expectedValue != null) {
            String actualValue = valueSupplier.get();
            Assert.assertEquals(actualValue, expectedValue, fieldName + " is not matching");
            log.info("Verified " + fieldName + ": expected=" + expectedValue + ", actual=" + actualValue);
        }
        return this;
    }

    public String getCaseSummary() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return commonWaits.waitForElementVisible(caseSummary).getText().trim();
    }

    public String createNewKnowledge() {
        if (isElementNotPresent(createKnowledgeLocator)) {
            clickWithoutInterception(toggleButtonLocator);
        }
        clickWithoutInterception(By.xpath(createKnowledgeLocator));
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.switchToLastWindow();
        Assert.assertTrue(getDriver().getCurrentUrl().contains(SolQAConstants.COMAROUND), "HKM is not cross launched successfully.");
        commonWaits.javaScriptScrollIntoView(commonWaits.waitForElementVisible(acceptAllButton));
        clickWithoutInterception(acceptAllButton);
        clickWithoutInterception(articleHeaderCloseButton);
        String articleTitle = articleTitleLocator.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
        clickWithoutInterception(workStatusDropdown);
        clickWithoutInterception(By.xpath(String.format(workStatusOption, SolQAConstants.PUBLISHED)));
        clickWithoutInterception(By.xpath(String.format(workStatusOption, saveButton)));
        clickWithoutInterception(By.xpath(String.format(workStatusOption, saveHereButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        getDriver().close();
        commonWaits.switchToTabByTitle(SolQAConstants.BUSINESS_WORKFLOWS);
        refreshBrowser();
        return articleTitle;
    }

    public BWFCase navigateToResourceTab() {
        clickWithoutInterception(resourceTabLocator);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public String getKnowledgeID(String articleTitle) {
        return commonWaits.waitForByElementToBeVisible(By.xpath(String.format(getPinnedKnowledgeIDLocator, articleTitle))).getText().trim();
    }

    public BWFCase verifyArticleCreatedForCase(Map<Object, String> caseData, String articleID) {
        String expectedText = caseData.get("Requester") + " has created article " + articleID + " from this case.";
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(activityTitleLocator, expectedText))),
                "Article creation verification failed. Expected text not found: " + expectedText);
        return this;
    }

    public BWFCase setStatusReason(String statusValue) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusReasonDrpDown, 20), "Status Reason dropdown is not visible");
        commonWaits.waitForElementClickable(statusReasonDrpDown);
        statusReasonDrpDown.click();
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectStatusReasonValue, statusValue)));
        ele.click();
        log.info("---Selected status reason-> " + statusValue + " for Status reason Dropdown");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase navigateToActivityTab() {
        clickWithoutInterception(activityTabLocator);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase clickOnGenerateCaseResolution() {
        commonWaits.clickWithoutInterception(GenerateCaseResolution);
        commonWaits.waitForElementVisible(ResolutionDescription, 60);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public String getResolutionDescription() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(ResolutionDescription, 60);
        return ResolutionDescription.getText();
    }

    public BWFCase saveStatusTransition() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(btn_saveCase);
        try {
            String successMessage = commonWaits.waitForElementVisible(successMsg, 20).getText().trim();
            Assert.assertTrue(successMessage.contains("successfully"), "Status is not saved.");
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        } catch (AssertionError e) {
            log.error("Failed to save status transition: " + e.getMessage());
        }
        return this;
    }

    public BWFCase changeCaseStatusToWithoutSaving(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        try {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.clickWithoutInterception(txt_CaseStatus);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropDownCaseStatus, SolQAConstants.Status_Label)), 20), "Status dropdown is not visible");
            commonWaits.clickWithoutInterception(By.xpath(String.format(dropDownCaseStatus, "Status")));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownValue_xpath, status)), 40), "Status option '" + status + "' is not visible in dropdown");
            commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownValue_xpath, status)));
            log.info("Selected " + status + " for Status Dropdown");

            // Handle special case for Resolved status
            if ("Resolved".equalsIgnoreCase(status)) {
                handleResolvedStatus();
            }
        } catch (Exception e) {
            log.error("Failed to change case status to " + status + ": " + e.getMessage());
            throw new RuntimeException("Failed to change case status to: " + status, e);
        }
        return this;
    }

    public BWFCase verifyCaseIsSetToFlagged() {
        switchtoDefaultFrame();
        Assert.assertTrue(commonWaits.isElementPresent(flaggedIconInActivityTab), "Case is not flagged as expected.");
        log.info("Case is flagged as expected.");
        return this;
    }

    public BWFCase addComment_In_SocialFeed_WithPeopleMention(String comment, String firstName) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(activityNoteBox);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.clickWithoutInterception(activityNoteBox);
        commonWaits.waitForElementVisible(activityNotesTextArea).sendKeys("@" + firstName);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(userHintXpath, firstName))).click();
        commonWaits.waitForExecutionComplete();
        activityNotesTextArea.sendKeys(" " + comment);
        commonWaits.clickWithoutInterception(chkBoxPublicComment);
        commonWaits.waitForElementClickable(btnActivityNotesPost);
        commonWaits.waitForExecutionComplete();
        if (Objects.equals(btnActivityNotesPost.getAttribute("disabled"), "true")) {
            Assert.fail("-----Post button of Activity Timeline is disabled");
        } else {
            commonWaits.clickWithoutInterception(btnActivityNotesPost);
        }
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public BWFCase addActivityNoteWithAttachment(String Comment, File file) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(activityNoteBox);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.clickWithoutInterception(activityNoteBox);
        commonWaits.waitForExecutionComplete();
        activityNotesTextArea.sendKeys(Comment);
        commonWaits.clickWithoutInterception(chkBoxPublicComment);
        commonMethods.attachFile(file, uploadFileInput);
        commonWaits.waitForElementClickable(btnActivityNotesPost);
        commonWaits.waitForExecutionComplete();
        if (Objects.equals(btnActivityNotesPost.getAttribute("disabled"), "true")) {
            Assert.fail("-----Post button of Activity Timeline is disabled");
        } else {
            commonWaits.clickWithoutInterception(btnActivityNotesPost);
        }
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public boolean isViewedCaseShown() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return commonWaits.isElementPresent(viewCountOnCase);
    }

    public BWFCase navigateToTasksTab() {
        clickWithoutInterception(tasksTabLocator);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase verifyCaseDetailEquals(String fieldLabel, String expectedValue) {
        String actual = getCaseDetail(fieldLabel);
        Assert.assertEquals(actual, expectedValue, "Case field '" + fieldLabel + "' value mismatch.");
        return this;
    }

    @FindBy(xpath = "//button[contains(@class, 'status-transition')]")
    WebElement taskStatusBtn;
    public BWFCase resolveTaskByName(String taskName) {
        navigateToTasksTab();
        commonWaits.waitForAngularRequestsToFinish();
        WebElement taskLink = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(taskLinkXpath, taskName)));
        commonWaits.clickWithoutInterception(taskLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), taskStatusBtn, 10), "Task Status button is not visible");
        commonWaits.clickWithoutInterception(taskStatusBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropDownCaseStatus, SolQAConstants.STATUS)), 10), "Task Status dropdown is not visible");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownCaseStatus, SolQAConstants.STATUS))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(By.xpath(String.format(selectStatusReasonValue, SolQAConstants.STATUS_CLOSED)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Save (try view-specific button first, fallback to any enabled Save)
        commonWaits.clickWithoutInterception(btn_saveCase);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Best-effort verification: status dropdown button should now show Closed
        String statusText = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownCaseStatus, "Status"))).getText();
        Assert.assertTrue(statusText.contains(SolQAConstants.STATUS_CLOSED), "Task status was not set to Closed. Actual: " + statusText);

        getDriver().navigate().back();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public void handleIntegrationPromptIfPresent(Map<Object, String> caseData) {
        String connectionId = caseData.get("Connection ID");
        String templateToUse = caseData.get("Template To Use");

        // If no data provided, do nothing (keeps other tests unaffected)
        if ((connectionId == null || connectionId.isBlank()) && (templateToUse == null || templateToUse.isBlank())) {
            return;
        }

        try {
            if (connectionId != null && !connectionId.isBlank()) {
                setTextFieldValue("RemedyConnectionID", connectionId);
            }

            if (templateToUse != null && !templateToUse.isBlank()) {
                setTextFieldValue("IncidentTemplateName", templateToUse);
            }

            if (commonWaits.isElementPresent(OK_BUTTON)) {
                commonWaits.clickWithoutInterception(OK_BUTTON);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            }
        } catch (Exception e) {
            log.info("Integration prompt not handled (skipped/failed): " + e.getMessage());
        }
    }

    /**
     * Returns External ID value shown on case (if present).
     */
    public String getExternalIdValue() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Try to locate by label first
        if (commonWaits.isElementPresent(By.xpath(EXTERNAL_ID_LABEL_VALUE))) {
            String value = commonWaits.waitForByElementToBeVisible(By.xpath(EXTERNAL_ID_LABEL_VALUE)).getText().trim();
            if (!value.isBlank()) {
                return value;
            }
        }

        // Fallback: any visible text containing "INC" within External ID section
        if (commonWaits.isElementPresent(By.xpath(String.format(ANY_TEXT_CONTAINS, "INC")))) {
            return commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ANY_TEXT_CONTAINS, "INC"))).getText().trim();
        }

        return "";
    }

    /**
     * Refreshes and waits for External ID to contain expected text.
     */
    public String waitForExternalIdToContain(String expectedContains, int maxAttempts, int waitMs) {
        for (int i = 0; i < maxAttempts; i++) {
            String value = getExternalIdValue();
            if (value != null && !value.isBlank() && value.contains(expectedContains)) {
                return value;
            }
            refreshBrowser();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForSometime(waitMs);
        }
        return getExternalIdValue();
    }

    /**
     * Clicks a link by visible text (used for tasks/related items).
     */
    public BWFCase clickLinkByText(String linkText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(By.xpath(String.format(LINK_BY_TEXT, linkText, linkText)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    /**
     * Complete all task actions in task flow
     */
    @FindBy(xpath = "//button[contains(@class, 'tab') and contains(., 'Task')]")
    WebElement taskTab;
    @FindBy(xpath = "//button[contains(., 'Complete') or contains(., 'Done') or contains(., 'Finish')]")
    WebElement completeButton;
    String Tasks = "//button[contains(@class, 'task') or contains(@class, 'action')][not(contains(@class, 'completed')) and not(contains(@class, 'disabled'))]";
    public BWFCase completeAllTaskActions() {
        log.info("Completing all task actions in task flow");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Navigate to Task tab if needed
        try {
            commonWaits.waitForElementVisible(taskTab, 3);
            commonWaits.clickWithoutInterception(taskTab);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        } catch (Exception e) {
            log.info("Task tab not found or already on task view");
        }

        // Find all incomplete tasks
        List<WebElement> tasks = findElements(By.xpath(Tasks));

        log.info("Found " + tasks.size() + " tasks to complete");

        for (WebElement task : tasks) {
            try {
                String taskName = task.getText().trim();
                log.info("Completing task: " + taskName);
                commonWaits.clickWithoutInterception(task);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

                // Click complete/done button if present
                try {
                    commonWaits.waitForElementVisible(completeButton, 3);
                    commonWaits.clickWithoutInterception(completeButton);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                } catch (Exception e) {
                    log.info("No complete button found for task");
                }
            } catch (Exception e) {
                log.warn("Could not complete task: " + e.getMessage());
            }
        }

        log.info("Completed all task actions");
        return this;
    }

    /**
     * Verifies that the Reopen button is not present on the page
     * This is used to confirm that a case cannot be reopened (e.g., when in certain statuses)
     */
    public BWFCase verifyReopenButtonIsNotPresent() {
        log.info("Verifying Reopen button is not present");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        boolean isReopenButtonPresent = commonWaits.isElementPresent(reopenButton);

        Assert.assertFalse(isReopenButtonPresent, "Reopen button should not be present but was found on the page");
        log.info("Verified: Reopen button is not present as expected");
        return this;
    }

    /**
     * Verifies any text is present on current view.
     */
    public BWFCase verifyTextPresent(String text) {
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(ANY_TEXT_CONTAINS, text))),
                "Expected text not found on page: " + text);
        return this;
    }

    public BWFCase verifyActivityContainsText(String expectedText) {
        navigateToActivityTab();
        return verifyTextPresent(expectedText);
    }

    public BWFCase setTextFieldValue(String fieldName, String value) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textFieldValue, fieldName))).sendKeys(value);
        log.info("Set " + fieldName + " to: " + value);
        return this;
    }

    public boolean isAttachmentPresentInActivityTab(String fileName) {
        log.info("Verifying attachment is present in activity tab: " + fileName);
        getDriver().navigate().refresh();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return commonWaits.isElementPresent(By.xpath(String.format(attachmentInActivityTab, fileName)),20);
    }

    public BWFCase changeCaseStatusAndSave(String status, String statusReason) {
        changeCaseStatusToWithoutSaving(status);
        if (statusReason != null && !statusReason.isBlank()) {
            setStatusReason(statusReason);
        }
        saveStatusTransition();
        return this;
    }

    public BWFCase verifyCaseStatus(String expectedStatus) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        String actual = commonWaits.waitForElementVisible(txt_CaseStatus).getText().trim();
        Assert.assertEquals(actual, expectedStatus, "Case status mismatch actual: " + actual + " expected: " + expectedStatus);
        return this;
    }

    public BWFCase verifyActivityTitlePresent(String expectedTitleContains) {
        navigateToActivityTab();
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(activityTitleLocator, expectedTitleContains))),
                "Expected activity title not found: " + expectedTitleContains);
        return this;
    }

    @FindBy(xpath = "//*[@rx-view-component-id='ec8e2edf-0ec1-49de-a8b4-9df9e97121b3']/ancestor::adapt-subnav-item/following-sibling::adapt-subnav-dropdown//button")
    WebElement moreOptionBtn;
    @FindBy(xpath = "//li//*[@rx-view-component-id='ec8e2edf-0ec1-49de-a8b4-9df9e97121b3']//button[contains(normalize-space(.), 'Reopen')]")
    WebElement reopenBtnInMoreOption;
    public BWFCase reopenCaseFromCaseView() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (REOPEN_LINK_OR_BUTTON.isDisplayed()){
            commonWaits.clickWithoutInterception(REOPEN_LINK_OR_BUTTON);
        }
        else{
            commonWaits.clickWithoutInterception(moreOptionBtn);
            commonWaits.clickWithoutInterception(reopenBtnInMoreOption);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase verifyApproverPresentInApproversList(String approverFullName) {
        log.info("Verifying approver is present in approvers list: " + approverFullName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        refreshBrowser();//to refresh the page to get the latest data
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForAngularRequestsToFinish();
        log.info("Clicking show approvers button");
        commonWaits.clickWithoutInterception(ShowApproversButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Checking if approver is present in approvers list: " + approverFullName);
        Assert.assertTrue(
                commonWaits.isElementPresent(By.xpath(String.format(APPROVERS_SECTION_PERSON, approverFullName))),
                "Expected approver is not present in approvers list: " + approverFullName
        );
        log.info("Verified: Approver is present in approvers list");
        commonWaits.clickWithoutInterception(BACK_FROM_APPROVER_LIST);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    @FindBy(xpath = "//*[@rx-view-component-id='87a8561e-a502-478e-ba23-ae82f8ee58e3']//button[contains(. ,'Show Approvers')]")
    WebElement showApproversListButton;
    public BWFCase clickShowApproversList() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), showApproversListButton, 10), "Show Approvers button is not visible.");
        commonWaits.clickWithoutInterception(showApproversListButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }
    public BWFCase clickOnNotificationBell() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), NOTIFICATION_BELL_ICON, 20), "Notification bell icon is not visible");
        commonWaits.waitForElementClickable(NOTIFICATION_BELL_ICON, 5).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase verifyNotificationContains(String expectedAlertContains) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        List<WebElement> alerts = getDriver().findElements(By.xpath(NOTIFICATION_LIST_ITEMS));
        ArrayList<String> allAlerts = new ArrayList<>();
        boolean found = false;
        for (WebElement el : alerts) {
            String txt = el.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
            allAlerts.add(txt);
            if (txt.contains(expectedAlertContains)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "Expected notification not found: " + expectedAlertContains + ". Actual: " + allAlerts);
        return this;
    }

    public boolean isActivityTitlePresent(String expectedTitleContains) {
        navigateToActivityTab();
        return commonWaits.isElementPresent(By.xpath(String.format(activityTitleLocator, expectedTitleContains)));
    }

    public BWFCase clickTaskSummaryLinkOnCase(String taskSummary) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(TASK_CELL_BY_SUMMARY, taskSummary)), 10);
        String currentHandle = getDriver().getWindowHandle();
        int handleCount = getDriver().getWindowHandles().size();
        commonWaits.clickWithoutInterception(By.xpath(String.format(TASK_CELL_BY_SUMMARY, taskSummary)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (getDriver().getWindowHandles().size() > handleCount) {
            commonWaits.switchToLastWindow();
        } else {
            // stay on same window (task opened in-place)
            getDriver().switchTo().window(currentHandle);
        }
        return this;
    }

    public BWFCase clickTaskIdFromNotification(String taskId) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TASK_ID_LINK_NOTIFICATION, taskId)));
        int handleCount = getDriver().getWindowHandles().size();
        commonWaits.clickWithoutInterception(By.xpath(String.format(TASK_ID_LINK_NOTIFICATION, taskId)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (getDriver().getWindowHandles().size() > handleCount) {
            commonWaits.switchToLastWindow();
        }
        return this;
    }

    public BWFCase addAdhocApproval(String ApproverName) {
        log.info("Starting addAdhocApproval for approver: " + ApproverName);
        String approver = ApproverName == null ? "" : ApproverName.trim();
        log.info("Normalized approver name: " + approver);
        Assert.assertTrue(commonWaits.isElementPresent(requestApproval), "CASE ALLOW ADHOC Approval is ");
        log.info("Request Approval control is visible");
        commonWaits.waitForElementClickable(requestApproval).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(approversList), "Approver List is Empty");
        List<WebElement> numberOfRows = findElements(By.xpath(approversList));
        log.info("Approver rows found before search: " + numberOfRows.size());
        if (numberOfRows.isEmpty()) {
            throw new RuntimeException("Table is empty");
        } else {
            commonWaits.waitForElementVisible(approverSearchBox).clear();
            log.info("Approver search input cleared");
            commonWaits.waitForElementVisible(approverSearchBox).sendKeys(approver);
            log.info("Entered approver: " + approver);
            commonWaits.waitForElementClickable(approverSearchBtn).click();
            log.info("Clicked approver search button");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            numberOfRows = findElements(By.xpath(approversList));
            log.info("Approver rows found after search: " + numberOfRows.size());
            if (numberOfRows.isEmpty()) {
                throw new RuntimeException("Approver is not available");
            } else {
                boolean approverFound = false;
                for (WebElement row : numberOfRows) {
                    String rowText = row.getText() == null ? "" : row.getText().trim().toLowerCase();
                    if (rowText.contains(approver.toLowerCase()) && rowText.length() > 0) {
                        log.info("Matched approver row text: " + rowText);
                        List<WebElement> rowCheckers = row.findElements(By.cssSelector("adapt-row-selection-checkbox .ui-chkbox-box"));
                        if (rowCheckers.isEmpty()) {
                            rowCheckers = row.findElements(By.cssSelector("adapt-row-selection-checkbox"));
                        }
                        if (rowCheckers.isEmpty()) {
                            rowCheckers = row.findElements(By.xpath(".//input[@type='checkbox' and @aria-label='Select row']"));
                        }
                        if (!rowCheckers.isEmpty()) {
                            commonWaits.clickWithoutInterception(rowCheckers.get(0));
                            approverFound = true;
                            break;
                        } else {
                            log.warn("Approver row found but checkbox locator not found in row");
                        }
                    }
                }
                if (!approverFound) {
                    log.info("Approver not matched from scanned rows, using fallback locator: " + approver);
                    commonWaits.clickWithoutInterception(By.xpath(String.format(adHocApprover, approver)));
                }
                Assert.assertTrue(addApproverBtn.isEnabled(), "Approver is not selected");
                addApproverBtn.click();
                log.info("Add approver button clicked");
            }
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Completed addAdhocApproval for approver: " + approver);
        return this;
    }


    public BWFCase expandAuditActivityToRevealAllFields() {
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), showMoreButtonInActivity, 10)) {
            log.info("Expanding audit activity to reveal all fields");
            commonWaits.clickWithoutInterception(showMoreButtonInActivity);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        return this;
    }
    public BWFCase verify_Value_Added_In_Audit_To_Field(String fieldLabel, String expectedValue) {
        log.info("Verifying audit field: '" + fieldLabel + "' = '" + expectedValue + "'");
        commonWaits.switchtoDefaultFrame();

        By locator = By.xpath(String.format(fieldValueInActivity, fieldLabel));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), locator, 20),
                "Field '" + fieldLabel + "' not found in audit details");
        WebElement valueElement = commonWaits.waitForByElementToBeVisible(locator);
        String actualValue = valueElement.getAttribute("title");
        if (actualValue == null || actualValue.trim().isEmpty()) {
            actualValue = valueElement.getText();
        }
        actualValue = actualValue.trim();
        log.info("Audit field '" + fieldLabel + "': expected='" + expectedValue + "', actual='" + actualValue + "'");
        Assert.assertTrue(actualValue.equalsIgnoreCase(expectedValue),
                "Audit field mismatch for '" + fieldLabel + "': expected='" + expectedValue + "', actual='" + actualValue + "'");
        return this;
    }

    public String getValueFromAuditTabForField(String fieldLabel) {
        if (commonWaits.isElementPresent(showMoreButtonInActivity)) {
            commonWaits.clickWithoutInterception(showMoreButtonInActivity);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        By locator = By.xpath(String.format(fieldValueInActivity, fieldLabel));
        if (!commonWaits.isElementPresent(locator)) {
            return null;
        }
        WebElement el = commonWaits.waitForByElementToBeVisible(locator);
        String val = el.getAttribute("title");
        if (val == null || val.trim().isEmpty()) {
            val = el.getText();
        }
        return val == null ? null : val.trim();
    }

    public BWFCase clickEditCase() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(editBtn), "Edit Case button/link not found");
        commonWaits.clickWithoutInterception(editBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase updateDynamicListValue(String fieldLabel, String value) {
        commonWaits.clickWithoutInterception(By.xpath(String.format(DROP_DOWN_CASE_DATA, fieldLabel)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), typeHeadInputField, 5),
                "Typehead input field not displayed for dynamic list: " + fieldLabel);
        typeHeadInputField.click();
        typeHeadInputField.sendKeys(value);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(DROPDOWN_VALUE_XPATH, value), 5),
                "Dropdown value not displayed for dynamic list: " + fieldLabel + " with value: " + value);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_VALUE_XPATH, value))).click();
        return this;
    }

    public BWFCase save_Edited_Case() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(SAVE_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public String getCaseDetail(String labelName) {
        log.info("Get UI Values of Fields after Creating case. " + labelName);
        WebElement sLocator = null;

        switch (labelName) {
            case "Summary":
                sLocator = fieldSummaryValue;
                break;
            case "Priority":
                sLocator = fieldPriorityValue;
                break;
            case "Requester":
                sLocator = fieldRequesterValue;
                break;
            case "Contact":
                sLocator = fieldContactValue;
                break;
            case "Assignee":
                sLocator = fieldAssigneeValue;
                break;
            case "Category Tier 1":
                sLocator = fieldCatTier1Value;
                break;
            case "Category Tier 2":
                sLocator = fieldCatTier2Value;
                break;
            case "Category Tier 3":
                sLocator = fieldCatTier3Value;
                break;
            case "Category Tier 4":
                sLocator = fieldCatTier4Value;
                break;
            case "Flowset":
                sLocator = fieldFlowsetValue;
                break;
            case "Status":
                sLocator = statusValue;
                break;
            case "Case Template":
                sLocator = fieldCaseTemplateValue;
                break;
            case "Source":
                sLocator = fieldCaseSourceValue;
                break;
            case "Line of Business":
                sLocator = fieldLineOfBusinessValue;
                break;
            case "Support Group":
                sLocator = fieldSupportGroupValue;
                break;
            case "Assigned Group":
                sLocator = fieldAssignedGroupValue;
                break;
            case "External Ticket ID":
                sLocator = externalTicketIDValue;
                break;
            case "Case Site":
                sLocator = caseSiteValue;
                break;
            case "Request ID":
                sLocator = requestIDValue;
                break;
            case "Title":
                sLocator = fieldTitleValue;
                break;
            case "Counter":
                sLocator = fieldCounterValue;
                break;
            case "Multi-select Question":
                sLocator = multiSelectQuestionValue;
                break;
            case "Radio Button Question":
                sLocator = radioButtonQuestionValue;
                break;

            default:
                throw new IllegalArgumentException("Field label '" + labelName + "' is not recognized for case details retrieval.");
        }
        if (sLocator == null) {
            throw new AssertionError("--- Field '" + labelName + "' locator not found.");
        }
        try {
            commonWaits.switchtoDefaultFrame();
            commonWaits.waitForSometime(commonWaits.moreDelay);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), sLocator, 15), "--- Field '" + labelName + "' is not visible.");
        } catch (Exception e) {
            throw new AssertionError("--- Field '" + labelName + "' locator not found. " + e.getMessage(), e);
        }
        String value = sLocator.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
        log.info(" Value of TextField " + labelName + " = " + value);
        return value;
    }

    public void verifyResponseForTheQuestion(String sResponseQuestion, String expectedResponse) {
        String sActualResponse = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(sQuestion, sResponseQuestion))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
        Assert.assertTrue(sActualResponse.contains(expectedResponse), "Response for Question " + sResponseQuestion + " is not as expected. "
                + "Expected: " + expectedResponse + " Actual is: " + sActualResponse);
    }

    /* Opens a task by clicking on the task link
     * @param taskIdentifier - Can be either task title or task ID (e.g., "TASK-0000000315")
     * @return BWFCase instance for method chaining
     */
    public BWFCase openTask(String taskIdentifier) {
        log.info("Opening task: " + taskIdentifier);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        try {
            // Try to find by task title first
            if (commonWaits.isElementPresent(By.xpath(String.format(TASK_LINK_BY_TITLE, taskIdentifier)))) {
                WebElement taskLink = findElement(By.xpath(String.format(TASK_LINK_BY_TITLE, taskIdentifier)));
                commonWaits.clickByJS(taskLink);
                log.info("Clicked on task by title: " + taskIdentifier);
            } else {
                // Try to find by task ID (Display ID)
                WebElement taskLink = commonWaits.waitForElementVisible(findElement(By.xpath(String.format(TASK_LINK_BY_ID, taskIdentifier))));
                commonWaits.clickByJS(taskLink);
                log.info("Clicked on task by ID: " + taskIdentifier);
            }

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Task opened successfully: " + taskIdentifier);

        } catch (Exception e) {
            log.error("Failed to open task: " + taskIdentifier);
            Assert.fail("Unable to open task: " + taskIdentifier + ". Error: " + e.getMessage());
        }

        return this;
    }

    /**
     * Clicks on "Assign to me" button for a task
     *
     * @return BWFCase instance for method chaining
     */
    public BWFCase clickAssignToMeOnTaskInBWFCase() {
        log.info("Clicking on 'Assign to me' button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        try {
            commonWaits.waitForElementVisible(assignToMeButtonOnTask);
            commonWaits.clickByJS(assignToMeButtonOnTask);

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Successfully clicked 'Assign to me' button");

        } catch (Exception e) {
            log.error("Failed to click 'Assign to me' button");
            Assert.fail("Unable to click 'Assign to me' button. Error: " + e.getMessage());
        }
        return this;
    }

    public BWFCase validateAutoCategorizeOptioncWhileCaseCreate(Map<Object, String> caseData) {
        if (caseData == null) {
            throw new IllegalArgumentException("Case data map cannot be null");
        }

        fillDropDownIfPresent(caseData, SolQAConstants.CREATE_CASE_LINE_OF_BUSINESS);
        // Fill typehead inputs
        commonWaits.switchToLastWindow();
        fillTypeHeadInputIfPresent(caseData, "Requester");

        // Set text fields
        if (caseData.get("Summary") != null) {
            setSummary(caseData.get("Summary"));
        }

        // Click Auto-Categorize button
        log.info("Clicking on Auto-Categorize button");
        commonWaits.waitForElementClickable(autoCategorizeButton);
        commonWaits.clickWithoutInterception(autoCategorizeButton);
        log.info("Clicked on Auto-Categorize button");

        // Wait for page to load
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();
        log.info("Page loaded after Auto-Categorize");

        // Validate Category Tier dropdowns have values
        log.info("Validating Category Tier dropdowns have values");
        validateCategoryTierHasValue(SolQAConstants.CATEGORY_TIER_1);
        validateCategoryTierHasValue(SolQAConstants.CATEGORY_TIER_2);
        validateCategoryTierHasValue(SolQAConstants.CATEGORY_TIER_3);
        log.info("Successfully validated all Category Tier dropdowns have values");

        log.info("Successfully validated Auto Categorize option while case create");
        return this;
    }

    /**
     * Validates that a Category Tier dropdown has a value selected
     *
     * @param categoryTierLabel The label of the category tier field (e.g., "Category Tier 1")
     */
    public BWFCase validateCategoryTierHasValue(String categoryTierLabel) {
        try {
            // Get the dropdown value element
            WebElement dropdownValueElement = commonWaits.waitForByElementToBeVisible(
                    By.xpath(String.format(CATEGORY_TIER_VALUE_XPATH, categoryTierLabel)));
            // Get the text from the dropdown value element
            String selectedValue = dropdownValueElement.getText().trim();

            // Check if the element has the placeholder class
            String elementClass = dropdownValueElement.getAttribute(SolQAConstants.EVENT_CLASS);
            boolean hasPlaceholder = elementClass != null && elementClass.contains("placeholder");

            // Validate that a value is present (not empty and not the placeholder)
            Assert.assertFalse(selectedValue.isEmpty(),
                    categoryTierLabel + " dropdown is empty - no value selected");
            Assert.assertFalse(hasPlaceholder,
                    categoryTierLabel + " dropdown still shows placeholder - no value auto-categorized");
            Assert.assertFalse(selectedValue.equalsIgnoreCase("Select") || selectedValue.equalsIgnoreCase("Choose"),
                    categoryTierLabel + " dropdown still shows placeholder text: " + selectedValue);

            log.info(categoryTierLabel + " has value: " + selectedValue);
        } catch (Exception e) {
            log.error("Failed to validate " + categoryTierLabel + ": " + e.getMessage());
            throw new RuntimeException("Unable to validate " + categoryTierLabel, e);
        }
        return this;
    }

    public BWFCase openUpcomingTask(String taskName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        WebElement upcomingTask = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(upcomingTaskXpath, taskName)));
        commonWaits.javaScriptScrollIntoView(upcomingTask);
        commonWaits.clickWithoutInterception(upcomingTask);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase clickOnViewCase(String caseID) {
        clickWithoutInterception(By.xpath(String.format(viewCaseLocator, caseID)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase fillCaseDataForUdemy(Map<Object, String> caseData) {
        if (caseData == null) {
            throw new IllegalArgumentException("Case data map cannot be null");
        }

        fillDropDownIfPresent(caseData, SolQAConstants.CREATE_CASE_LINE_OF_BUSINESS);
        // Fill typehead inputs
        commonWaits.switchToLastWindow();
        fillTypeHeadInputIfPresent(caseData, SolQAConstants.REQUESTER_LABEL);
        fillTypeHeadInputIfPresent(caseData, SolQAConstants.CONTACT_LABEL);

        // Set template
        if (caseData.get(SolQAConstants.TEMPLATE_NAME) != null) {
            setTemplateName(caseData.get(SolQAConstants.TEMPLATE_NAME));
        }

        // Set text fields
        if (caseData.get(SolQAConstants.SUMMARY_LABEL) != null) {
            setSummary(caseData.get(SolQAConstants.SUMMARY_LABEL));
        }
        if (caseData.get(SolQAConstants.DESCRIPTION_LABEL) != null) {
            setDescription(caseData.get(SolQAConstants.DESCRIPTION_LABEL));
        }

        // Handle assign to me
        if (caseData.get(SolQAConstants.ASSIGN_TO_ME_LABEL) != null && caseData.get(SolQAConstants.ASSIGN_TO_ME_LABEL).equalsIgnoreCase(SolQAConstants.TRUE)) {
            clickOnAssignToMeButton();
        }

        // Save and navigate to case
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(SAVE_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        if (commonWaits.isElementPresent(requesterDialogTitle)) {
            log.info("Requester is mandatory field, filling requester");
            requesterResponseTextInputs(REQUESTER_RESPONSE_Q1, caseData.get("BusinessUnit"));
            requesterResponseTextInputs(REQUESTER_RESPONSE_Q2, caseData.get("AccessReason"));
            requesterResponseTextInputs(REQUESTER_RESPONSE_Q3, caseData.get("CourseName"));
            requesterResponseDateInputs(REQUESTER_RESPONSE_Q4, getFutureDate(Integer.parseInt(caseData.get("DaysToAdd")), "MMM dd, yyyy"));
            clickWithoutInterception(By.xpath(String.format(workStatusOption, SolQAConstants.OK)));
        }

        commonWaits.waitForElementClickable(GO_TO_CASE_BUTTON, GO_TO_CASE_WAIT_TIME).click();
        log.info("Successfully filled case data and navigated to case");
        return this;
    }

    public BWFCase requesterResponseTextInputs(String labelName, String value) {
        if (labelName == null || labelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }

        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(requesterResponseTextLocator, labelName)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            webElement.sendKeys(value);
            log.info("Successfully set typehead input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set typehead input for " + labelName + ": " + e.getMessage());
            throw new RuntimeException("Failed to set typehead input for " + labelName, e);
        }
        return this;
    }

    public BWFCase requesterResponseDateInputs(String labelName, String value) {
        if (labelName == null || labelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(requesterResponseDateLocator, labelName)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            webElement.sendKeys(value);
            log.info("Successfully set typehead input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set typehead input for " + labelName + ": " + e.getMessage());
            throw new RuntimeException("Failed to set typehead input for " + labelName, e);
        }
        return this;
    }

    public BWFCase clickMarkAsCompleteOnTaskInBWFCase() {
        log.info("Clicking on 'Mark as complete' button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), markAsCompleteButtonOnTask, 20), "'Mark as complete' button is not visible on task");
            commonWaits.waitForElementVisible(markAsCompleteButtonOnTask);
            commonWaits.clickByJS(markAsCompleteButtonOnTask);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info("Successfully clicked 'Mark as complete' button");
        } catch (Exception e) {
            log.error("Failed to click 'Mark as complete' button");
            Assert.fail("Unable to click 'Mark as complete' button. Error: " + e.getMessage());
        }
        commonWaits.waitForSometime(commonWaits.moreDelay);
        return this;
    }

    public BWFCase clickOnGoToCaseIfPresent() {
        log.info("Checking for presence of 'Go to Case' button");
        if (commonWaits.isElementPresent(GO_TO_CASE_BUTTON)) {
            log.info("'Go to Case' button is present, clicking it");
            commonWaits.clickWithoutInterception(GO_TO_CASE_BUTTON);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForPageReadyState();
            log.info("Clicked on 'Go to Case' button and navigated to case");
        } else {
            log.info("'Go to Case' button is not present, skipping click");
        }
        return this;
    }

    public BWFCase openCaseAccessTab() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(caseAccessTab), "Case Access tab is not present on case view");
        commonWaits.clickWithoutInterception(caseAccessTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase addAgentAccessForCase(String userLoginOrFullName, boolean canEdit) {
        openCaseAccessTab();
        Assert.assertTrue(commonWaits.isElementPresent(agentAccessTab), "Add Agent access button not found");
        commonWaits.clickWithoutInterception(agentAccessTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(agentSearchInput), "Search input not found in Agent access picker");
        agentSearchInput.clear();
        agentSearchInput.sendKeys(userLoginOrFullName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(firstResult), "No agent search result found for: " + userLoginOrFullName);
        commonWaits.clickWithoutInterception(firstResult);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(addAgentAccessBtn), "Add Agent access button not found");
        commonWaits.clickWithoutInterception(addAgentAccessBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(agentAccessTab), "Add Agent access button not found");
        commonWaits.clickWithoutInterception(agentAccessTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Attempt to set edit permission if a toggle exists
        if (commonWaits.isElementPresent(editToggle)) {
            boolean selected = editToggle.isSelected();
            if (canEdit != selected) {
                commonWaits.clickWithoutInterception(editToggle);
            }
        }

        return this;
    }

    public BWFCase addConfidentialSupportGroupForCase(String supportGroupNameOrId, boolean canEdit) {
        openCaseAccessTab();
        Assert.assertTrue(commonWaits.isElementPresent(addConfidentialSupportGroupButton), "Add Confidential Support Group button not found");
        commonWaits.clickWithoutInterception(addConfidentialSupportGroupButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(confidSearchInput), "Search input not found in Confidential Support Group picker");
        confidSearchInput.clear();
        confidSearchInput.sendKeys(supportGroupNameOrId);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(firstResult), "No support group search result found for: " + supportGroupNameOrId);
        commonWaits.clickWithoutInterception(firstResult);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        Assert.assertTrue(commonWaits.isElementPresent(addAgentAccessBtn), "Add Agent access button not found");
        commonWaits.clickWithoutInterception(addAgentAccessBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        if (commonWaits.isElementPresent(editToggle)) {
            boolean selected = editToggle.isSelected();
            if (canEdit != selected) {
                commonWaits.clickWithoutInterception(editToggle);
            }
        }

        Assert.assertTrue(commonWaits.isElementPresent(addConfidentialSupportGroupButton), "Add Confidential Support Group button not found");
        commonWaits.clickWithoutInterception(addConfidentialSupportGroupButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        return this;
    }

    /**
     * Opens the survey response view in the case (if available).
     */
    public BWFCase clickViewSurvey() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(VIEW_SURVEY_LINK_OR_BUTTON), "View Survey link/button is not present on the case.");
        commonWaits.clickWithoutInterception(VIEW_SURVEY_LINK_OR_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    /**
     * Verifies the survey rating and comment fields in the case view (uses generic label/value resolution).
     */
    public BWFCase verifySurveyRatingAndComment(String ratingFieldLabel, String expectedRating, String commentFieldLabel, String expectedComment) {
        Assert.assertNotNull(ratingFieldLabel, "Rating field label is null");
        Assert.assertNotNull(commentFieldLabel, "Comment field label is null");
        verifyResponseForTheQuestion(ratingFieldLabel, expectedRating);
        verifyResponseForTheQuestion(commentFieldLabel, expectedComment);
        return this;
    }

    public BWFCase verifySurveyRatingDisplayed(String ratingFieldLabel, String expectedRating) {
        Assert.assertNotNull(ratingFieldLabel, "Rating field label is null");
        verifyResponseForTheQuestion(ratingFieldLabel, expectedRating);
        return this;
    }

    /**
     * Lightweight presence check for a survey field value by its label.
     */
    public boolean isSurveyFieldDisplayed(String fieldLabel) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (fieldLabel == null) {
            return false;
        }
        return commonWaits.isElementPresent(By.xpath(String.format(sQuestion, fieldLabel)), 3);
    }

    public BWFCase ClickOnGoToCaseIfPresent() {
        if (commonWaits.isElementPresent(goToCaseButton)) {
            commonWaits.clickWithoutInterception(goToCaseButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();
        } else {
            log.info("Go to Case button not present, cannot click");
        }
        return this;
    }

    public BWFCase navigateToTabByName(String tabName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (tabName == null || tabName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tab name cannot be null/empty");
        }

        WebElement el = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(tab, tabName)));
        commonWaits.scrollToSpecificElement(el);
        commonWaits.clickWithoutInterception(el);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFCase relateAssetToCase(String ciTypeLabel, String ciTypeValue, String assetName) {
        if (assetName == null || assetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name cannot be null/empty");
        }
        navigateToTabByName("Related Assets");
        commonWaits.scrollToSpecificElement(assetsButton);
        commonWaits.clickWithoutInterception(assetsButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Search qualification dropdown (CI type)
        if (ciTypeLabel != null && ciTypeValue != null) {
            try {
                setDropDownValues(ciTypeLabel, ciTypeValue);
            } catch (Exception e) {
                // Best-effort fallback: common label across builds
                setDropDownValues("CI type", ciTypeValue);
            }
        }

        if (commonWaits.isElementPresent(relateAssetsGridSearchBtn, 5)) {
            commonWaits.clickWithoutInterception(relateAssetsGridSearchBtn);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        } else if (commonWaits.isElementPresent(searchBtn, 5)) {
            commonWaits.clickWithoutInterception(searchBtn);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }

        // Filter by asset name (grid search on asset picker — not the Agent Access combobox searchInput)
        if (commonWaits.isElementPresent(relateAssetsGridSearchInput, 5)) {
            relateAssetsGridSearchInput.clear();
            relateAssetsGridSearchInput.sendKeys(assetName);
            relateAssetsGridSearchInput.sendKeys(Keys.ENTER);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        } else if (commonWaits.isElementPresent(agentSearchInput, 5)) {
        	agentSearchInput.clear();
        	agentSearchInput.sendKeys(assetName);
        	agentSearchInput.sendKeys(Keys.ENTER);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }

        // Select row containing asset name
        commonWaits.clickWithoutInterception(By.xpath(String.format(rowCheckbox, assetName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        commonWaits.clickWithoutInterception(relateAddCIBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();
        return this;
    }



    public BWFCase verifyRelatedAssetPresent(String assetName) {
        navigateToTabByName("Related Assets");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(relatedAssetTextInGrid, assetName)), 15), "Related asset not present: " + assetName);
        return this;
    }

    public BWFCase openCreateMenuAndSelectRelatedTicketType(String expectedOptionsCsv, String optionToSelect) {
        navigateToTabByName("Related Tickets");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        commonWaits.clickWithoutInterception(CreateRelatedticketButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        if (expectedOptionsCsv != null && !expectedOptionsCsv.trim().isEmpty()) {
            String[] opts = expectedOptionsCsv.split(",");
            for (String o : opts) {
                String opt = o.trim();
                if (opt.isEmpty()) continue;
                Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(createMenuOption, opt)), 10), "Create menu option not present: " + opt);
            }
        }

        commonWaits.clickWithoutInterception(By.xpath(String.format(createMenuOption, optionToSelect)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.switchToLastWindow();
        return this;
    }

    public BWFCase verifyRelatedTicketPresent(String ticketId) {
        navigateToTabByName("Related Tickets");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(asset, ticketId)), 20), "Related ticket not present: " + ticketId);
        return this;
    }

    /**
     * Asserts the Related Tickets grid (virtual-scrolled Adapt table) has at least one data row.
     */
    public BWFCase verifyRelatedTicketsTableHasAtLeastOneRecord() {
        navigateToTabByName("Related Tickets");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), relatedTicketsGridFirstDataRow, 20),
                "Related Tickets grid: expected at least one data row (tr.at-data-row).");
        return this;
    }

    public BWFCase verifyRelatedTicketRelationshipType(String ticketId, String expectedRelationshipType) {
        Assert.assertNotNull(ticketId, "Ticket id is null");
        Assert.assertNotNull(expectedRelationshipType, "Expected relationship type is null");

        navigateToTabByName("Related Tickets");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        String txt = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(row, ticketId))).getText();

        Assert.assertTrue(
                txt != null && txt.contains(expectedRelationshipType),
                "Relationship type not matched for related ticket '" + ticketId + "'. Expected contains: '"
                        + expectedRelationshipType + "' but was: " + txt
        );
        return this;
    }

    public BWFCase addActivityNote(String noteText, boolean isPublic) {
        Assert.assertNotNull(noteText, "Note text is null");
        navigateToActivityTab();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        commonWaits.waitForElementVisible(activityNoteBox);
        commonWaits.clickWithoutInterception(activityNoteBox);
        commonWaits.waitForElementVisible(activityNotesTextArea);
        activityNotesTextArea.sendKeys(noteText);

        if (isPublic) {
            try {
                commonWaits.clickWithoutInterception(chkBoxPublicComment);
            } catch (Exception ignored) {
            }
        }

        commonWaits.waitForElementClickable(btnActivityNotesPost);
        if (Objects.equals(btnActivityNotesPost.getAttribute("disabled"), SolQAConstants.trueValue)) {
            Assert.fail("Post button of Activity Timeline is disabled");
        }
        commonWaits.clickWithoutInterception(btnActivityNotesPost);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFCase addBoldItalicActivityNote(String noteText, boolean isPublic) {
        Assert.assertNotNull(noteText, "Note text is null");
        navigateToActivityTab();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        commonWaits.waitForElementVisible(activityNoteBox);
        commonWaits.clickWithoutInterception(activityNoteBox);
        commonWaits.waitForElementVisible(activityNotesTextArea);

        // Best-effort formatting using editor keyboard shortcuts; content remains readable even if formatting is ignored.
        Actions actions = new Actions(getDriver());
        actions.click(activityNotesTextArea)
                .keyDown(Keys.CONTROL).sendKeys("b").sendKeys("i").keyUp(Keys.CONTROL)
                .sendKeys(noteText)
                .perform();

        if (isPublic) {
            try {
                commonWaits.clickWithoutInterception(chkBoxPublicComment);
            } catch (Exception ignored) {
            }
        }

        commonWaits.waitForElementClickable(btnActivityNotesPost);
        if (Objects.equals(btnActivityNotesPost.getAttribute("disabled"), SolQAConstants.trueValue)) {
            Assert.fail("Post button of Activity Timeline is disabled");
        }
        commonWaits.clickWithoutInterception(btnActivityNotesPost);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public boolean isCollaboratorPresent(String collaboratorName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return commonWaits.isElementPresent(By.xpath(String.format(collaboratorPill, collaboratorName)));
    }

    public BWFCase verifyCollaboratorPresent(String collaboratorName) {
        Assert.assertTrue(isCollaboratorPresent(collaboratorName), "Collaborator is not present on BWF case: " + collaboratorName);
        return this;
    }

    public BWFCase verifyCollaboratorNotPresent(String collaboratorName) {
        Assert.assertFalse(isCollaboratorPresent(collaboratorName), "Collaborator is present on BWF case but expected to be absent: " + collaboratorName);
        return this;
    }

    public boolean isTaskPresentOnCase(String taskSummary) {
        return commonWaits.isElementPresent(By.xpath(String.format(TASK_CELL_BY_SUMMARY, taskSummary)))
                || commonWaits.isElementPresent(By.xpath(String.format(taskLinkXpath, taskSummary)));
    }

    public BWFCase setDynamicFieldValues(String que, String value){
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(String.format(dynamicFieldLocator,que))), 100);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dynamicFieldLocator,que))).clear();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dynamicFieldLocator,que))).sendKeys(value);
		return this;
	}

	public BWFCase ok_Case() {
		commonWaits.waitForElementClickable(lnk_okCase);
		lnk_okCase.click();
		commonWaits.waitForSometime(commonWaits.minDelay);
		return this;
	}

    public boolean verifyFieldPresent_in_Related_Tickets(String fieldLabel) {
    	boolean present = false;
	    WebElement relateTicketsfield=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(relatedTickets_Fields, fieldLabel)));
    	
    	try {
			commonWaits.waitForElementVisible(relateTicketsfield);
			if (fieldLabel.equals(relateTicketsfield.getText())) {
				present = true;
			}
    	}
		catch (Exception e) {
    		log.info(e.getMessage());
    	}
    	return present;
    }    	

    public BWFCase clickOption(String Option){
        WebElement Click_option = null;
        if (Option.equals(CREATE_OPTION)){
            Click_option=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownOptions, CREATE_OPTION)));
        }
        else if (Option.equals(RELATE_OPTION)){
            Click_option=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownOptions, RELATE_OPTION)));
        }		 
           commonWaits.waitForElementClickable(Click_option);
           commonWaits.clickWithoutInterception(Click_option);
           commonWaits.waitForExecutionComplete();
           return this;
       }
    
    public boolean verifyDropDownValues(String jsonData) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        boolean flag = Create_DropDown_options.stream()
                   .map(WebElement::getText)
                   .collect(Collectors.joining(","))
                   .contains(jsonData);

        if (flag) {
            log.info("The List of WebElements matches the JSON data.");
        }

        return flag;
    }

    public BWFCase clickIncidentOption(){
        commonWaits.waitForElementClickable(IncidentOption);
        IncidentOption.click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFCase Dropdown_Values(List<WebElement> sLocator,String value) {
        for (WebElement SingleOption:sLocator) {
            if(SingleOption.getText().equals(value)) {
                commonWaits.waitForElementClickable(SingleOption, 10).click();
                SingleOption.click();
            }
        }
        return this;
    }

    public BWFCase SetDropdownValueforBWFIncident(String Dropdown,String value) {
        WebElement Dropdown_Option=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(IncidentDropdownOption, Dropdown)));
        commonWaits.waitForElementVisible(Dropdown_Option, 10);
        commonWaits.waitForElementClickable(Dropdown_Option, 10).click();
        commonWaits.waitForExecutionComplete();

        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.javaScriptScrollIntoView(IncidentTypeTextBox);
        commonWaits.waitForElementVisible(IncidentTypeTextBox,10);
        commonWaits.waitForElementClickable(IncidentTypeTextBox,10).sendKeys(value);
        WebElement DropdownValue=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(IncidentDropdownValue, value)));
        commonWaits.waitForElementVisible(DropdownValue, 10);
        commonWaits.waitForElementClickable(DropdownValue, 10).click();
        commonWaits.waitForExecutionComplete();

        return this;
    }

    public BWFCase saveIncident() {
        WebElement save_Incident = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(text_common_xpath,SolQAConstants.SAVE)));
        commonWaits.waitForElementClickable(save_Incident);
        save_Incident.click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public boolean verifyRelationshipType(String jsonData) {
        commonWaits.waitForElementVisible(RelationShip_Type_Status, 10);
        log.info("Verifying Relationship Type:-"+RelationShip_Type_Status.getText().trim());
           if (RelationShip_Type_Status.getText().trim().equals(jsonData)) {
               commonWaits.waitForExecutionComplete();
               return true;
           } else {
               commonWaits.waitForExecutionComplete();
               return false;
           }
           
    }

    public void verify_Activity_Updated() {
        String ExpectedActivity="Elizabeth Peters added "+INC_Number.getText()+" as a Related to incident.";
        Assert.assertTrue(verifyTitle_In_SocialFeed(ExpectedActivity), "Expected activity not found in social feed: " + ExpectedActivity);

    }

    public String INCID() {
        return INC_Number.getText();
    }

    public String UserID() {
        commonWaits.waitForElementVisible(Profile_Icon);
        commonWaits.clickByJS(Profile_Icon);
        return Profile_Username.getText();
    }

    public BWFCase clickOnAddWorkNote() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible(Case_checkbox);
        commonWaits.waitForElementClickable(Case_checkbox);
        if (!Case_checkbox.isSelected()) {
            Case_checkbox.click();
        }
        WebElement add_work_note= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(text_common_xpath,ADD_WORK_NOTE)));
        commonWaits.waitForElementClickable(add_work_note,20);
        add_work_note.click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFCase AddWorkNote(String Note_Text, String Note) {
        commonWaits.waitForElementVisible(Add_work_note_TextBox,20).click();
        commonWaits.sendKeysByAction(Add_work_note_TextBox, Note_Text);
        commonWaits.waitForElementClickable(Post_Button,20);
        Post_Button.click();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public BWFCase AddBoldItalicWorkNote(String Note_Text, String Note) {
        commonWaits.waitForElementClickable(boldInAddWorkNote).click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementClickable(italicInAddWorkNote).click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementClickable(underlineInAddWorkNote).click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementVisible(Add_work_note_TextBox, 20).click();
        commonWaits.sendKeysByAction(Add_work_note_TextBox, Note_Text);
        commonWaits.waitForElementClickable(Post_Button, 20);
        Post_Button.click();
        commonWaits.waitForExecutionComplete();
        return this;
    }


    public boolean verifyBody_In_SocialFeed(String body){
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(CaseEditBodyLocator));
        commonWaits.switchToFrame(iframe);
        boolean bStatusFound = false;
        String str = CaseBodyLocator.getText().trim();
        log.info("Social feed body: " + str);
        log.info("Expected body: " + body);
        if (str.contains(body))
        {
            bStatusFound = true;
        }
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForExecutionComplete();
        return bStatusFound;

    }

    public BWFCase selectWorkNoteType(String value){
        commonWaits.waitForElementVisible(Add_work_note_TextBox);
        commonWaits.waitForElementClickable(workNoteTypeDropdown).click();
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(workNoteValue, value)));
        new Actions(getDriver()).moveToElement(we).click().perform();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public boolean verifyTitle_In_SocialFeed(String title){
        commonWaits.waitForByElementToBeVisible(By.xpath(CaseEditBodyLocator));
        boolean bStatusFound = false;
        for (WebElement tmpWE : activityTabTitle) {
            String str = tmpWE.getText().trim();
            log.info("Social feed title: " + str);
            log.info("Expected title: " + title);
            if (str.contains(title))
            {
                bStatusFound = true;
                break;
            }
        }
        return bStatusFound;
    }


    public boolean verify_Activity_Tab_Title(String title){
        commonWaits.waitForExecutionComplete();
        boolean valuePresent = verifyTitle_In_SocialFeed(title);
        return valuePresent;
    }

    public String getDescriptionValue() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(descriptionFrame));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), descriptionValue, 15), "--- Field '" + "Description" + "' is not visible.");
        String value = descriptionValue.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
        switchtoDefaultFrame();
        return value;
    }

}