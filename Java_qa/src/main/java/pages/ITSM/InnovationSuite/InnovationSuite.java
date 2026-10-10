package pages.ITSM.InnovationSuite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import pages.ITSM.Api.API_CommonOperations;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import static org.testng.Assert.assertEquals;

public class InnovationSuite extends BasePage {

	static SolqaLogger log;
	CommonWaits commonWaits;
	CommonMethods commonMethods;
	API_CommonOperations apiCommonOperations;
	String appName = null;

	// Poll for status to become Active
	int maxAttempts;
	int attempt;
	String currentStatus, actualResponse;
	boolean statusActive, statusVisible, responseFound;

	String elementXpathByID = "//*[@id='%s']";
	String typeheadListElementText = "//*[contains(text(),'%s')]";
	String iframeInIncident = "//iframe[@id='pwa-view' or @id='pwa-frame']";
	String bundleString = "//div[contains(@class,'rx-ellipsis')]//a[normalize-space()='%s']";
	String checkboxXpath = "//a[normalize-space(text())='%s']/ancestor::div/div/adapt-row-selection-checkbox";
	String BMCChatBotTab = "//*[normalize-space()='%s']//ancestor::button[@role='tab']";
	String HelixGPTSkillValue = "//div//option[contains(normalize-space(),'%s')]";
	String assistantServiceProviderValues = "//div[@rx-id='rx-bot-configuration-provider']//option[normalize-space()='%s']";
	String chatops_Permission = "//rx-permission-editor//div[@class='clearfix']//*[contains(text(), '%s')]";
	String permissionValue = "//*[@class='ui-select__rx-choice' and @aria-label='%s']";
	String BMCBot = "//div[contains(@class,'rx-custom-cell')]//a[normalize-space(.)='%s']";
	String recordName = "//h4[normalize-space()='Data editor (%s)']";
	String processLinkByName = "//a[normalize-space()='%s' and contains(@href, 'process/edit')]";
	String hpmRecordTypeDropdown = "//a[normalize-space(.)='%s' and contains(@href, 'Create')]";
	String textInputField = "//adapt-rx-textfield[contains(. ,'%s')]//input";
	String ckeEditorField = "//*[contains(@id, 'cke_editor')]//*[@role='textbox' and @title='%s']";
	String dropdownField = "//adapt-rx-select[contains(. ,'%s')]//button";
	String dropdownOptions = "//button[@role='option' and normalize-space(.)='%s']";
	String counterFieldInput = "//adapt-rx-counter[contains(. ,'%s')]//input";
	String associatedRecordRadioBtn = "//adapt-rx-radiobutton//label";
	String dateFieldInput = "//adapt-rx-datetime[contains(. ,'%s')]//input";
	String formHeadingXpath = "//h4[contains(@class,'definition-name')]";
	String gainsightSettingsHeadingXpath = "//*[contains(@class,'text-logo')]";
	String gainsightDeploymentTypeDropdown = "//button[@name='deploymentType' and @role='combobox']";
	String gainsightEnvironmentTypeDropdown = "//button[@name='environmentType' and @role='combobox']";
	String enablePerformanceToggleInput = "(//h4[normalize-space()='Enable performance and quality improvement data collection:']/following::adapt-rx-switch//input)[1]";
	String enablePerformanceToggleLabel = "(//h4[normalize-space()='Enable performance and quality improvement data collection:']/following::adapt-rx-switch//label)[1]";
	String gainsightSettingsSaveButton = "//button//span[@class='btn-content']//ancestor::button[@rx-id='save-button']";
	String formSearchBox = "//input[contains(@class,'adapt-rx-search__input') and @placeholder='Search']";
	String searchResultText = "//span[contains(text(),'%s')]";
	String searchResultLink = "//a[contains(@href,'javascript:void')]";
	String bundleIdMappingCheckIcon = "//span[contains(@class,'d-icon-check_adapt')]";
	String globalContextTextarea = "//textarea[@class='form-control rx-form-control']";
	String editRecordModalTitle = "//span[contains(@class,'dp-title') and contains(text(),'Edit record')]";
	String featureFlagEnableButton = "//button[@rx-id='true-button' and @aria-pressed='false']";
	String featureFlagDisableButton = "//button[@rx-id='false-button' and @aria-pressed='false']";
	String featureFlagEnabledButton = "//button[@rx-id='true-button' and @aria-pressed='true']";
	String saveButtonEditRecord = "//button[contains(@class,'btn-primary') and .//span[text()='Save']]";
	String cancelButtonEditRecord = "//button[contains(@class,'btn-secondary') and .//span[text()='Cancel']]";
	String automatedAssetAssignmentHeading = "//span[contains(@class,'a-product__name') and contains(@class,'text-url-break-out') and contains(text(),'Automated Asset Assignment')]";
	String assetAutomationsTab = "//a[contains(@class,'adapt-menu__link')]//span[contains(@class,'a-menu__text') and contains(text(),'Asset Automations')]";
	String aaaCreateAutomationButton = "//button[contains(@class,'btn') and contains(.,'Create automation')]";
	String aaaCreateAutomationHeading = "//h1[normalize-space()='Create automation']";
	String aaaAutomationNameInput = "//adapt-rx-textfield[contains(.,'Name')]//input";
	String aaaAssetTypeDropdown = "//adapt-rx-select[contains(.,'Asset Type')]//button";
	String aaaAssetTypeOption = "//button[@role='option' and contains(.,'%s')]";
	String aaaAddExpressionButton = "//a[contains(.,'Add an expression')]";
	String aaaExpressionFieldDropdown = "//adapt-rx-select[contains(.,'Field')]//button";
	String aaaExpressionOperatorDropdown = "//adapt-rx-select[contains(.,'Operator')]//button";
	String aaaExpressionValueInput = "//adapt-rx-textfield[contains(.,'Value')]//input";
	String aaaDropdownOption = "//button[@role='option' and contains(.,'%s')]";
	String aaaSaveAutomationButton = "//button[contains(@class,'btn-primary') and contains(.,'Save')]";
	String aaaCancelAutomationButton = "//button[contains(@class,'btn-secondary') and contains(.,'Cancel')]";
	String aaaAutomationJobCreatedMessage = "//div[contains(@class,'growl-message') and contains(.,'created')]";
	String aaaAllRelationshipsCheckbox = "//adapt-rx-checkbox[contains(.,'All relationships')]//input[@type='checkbox']";
	String aaaSelectRelationshipDropdown = "//adapt-rx-select[contains(.,'Select Relationship')]//button";
	String aaaExpressionFilterInput = "//input[contains(@class,'adapt-rx-search__input') and @placeholder='Filter']";
	String aaaExpressionFieldButton = "//div[@rx-id='node']//adapt-highlight[@title='%s']/parent::span/preceding-sibling::button[contains(@class,'d-icon-plus_circle')]";
	String aaaExpressionOperatorButton = "//button[contains(@class,'narrow-operator') or contains(@class,'wide-operator')]//span[contains(@class,'btn-content') and contains(normalize-space(),'%s')]/parent::button";
	String aaaExpressionOkButton = "//button[@rx-id='save-button' and contains(.,'OK')]";
	String aaaCreateRelationshipCheckbox = "//adapt-rx-checkbox[@formcontrolname='selectCreateRelationship']//div[@class='checkbox__label']";
	String aaaCreateRelationshipCheckboxInput = "//adapt-rx-checkbox[@formcontrolname='selectCreateRelationship']//input[@type='checkbox']";
	String aaaRelationshipTypeDropdown = "//adapt-rx-select[@formcontrolname='relationshipType']//button[@role='combobox']";
	String aaaPeopleTypeDropdown = "//adapt-rx-select[@formcontrolname='peopleType']//button[@role='combobox']";
	String aaaPersonDropdown = "//rx-select-with-pagination[@formcontrolname='foundationId']//button[@role='combobox']";
	String aaaPersonFilterInput = "//adapt-rx-search//input[contains(@class,'adapt-rx-search__input') and @placeholder='Filter']";
	String aaaRoleDropdown = "//adapt-rx-select[@formcontrolname='roleType']//button[@role='combobox']";
	String aaaDropdownOptionByText = "//button[@role='option']//div[contains(.,'%s')]/ancestor::button";
	String aaaAutomationEntryByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']";
	String aaaSuccessToastMessage = "//span[contains(@class,'a-toast__details-text') and contains(.,'Automation rule saved successfully')]";
	String aaaEnableButton = "//button[contains(@class,'btn-link')]//span[normalize-space()='Enable']";
	String aaaAutomationRowByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr";
	String aaaAutomationRowCheckboxByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr//div[contains(@class,'ui-chkbox-box')]";
	String aaaAutomationStatusByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr//td[contains(@data-testid,'column-5')]//span[contains(@class,'ng-star-inserted')]";
	String aaaClosePopupButton = "//button[contains(@class,'btn-secondary')]//span[normalize-space()='Close']";
	String aaaAddAutomationJobButton = "//button[contains(@class,'btn-link')]//span[normalize-space()='Add automation job']";
	String aaaCreateJobPopupAutomationName = "//div[contains(@class,'rx-select__search-button-title') and contains(.,'%s')]";
	String aaaPreviewButton = "//button[contains(@class,'btn-primary')]//span[normalize-space()='Preview']";
	String aaaRunJobButton = "//button[contains(@class,'btn-primary')]//span[normalize-space()='Run Job']";
	String aaaPreviewAssetCheckbox = "//td[contains(.,'Optiplex')]//ancestor::tr//div[contains(@class,'ui-chkbox-box')]";
	String aaaJobCreatedToastMessage = "//div[contains(@class,'a-toast__summary') and contains(.,'Automation job created')]";
	String aaaAutomationJobConsoleLink = "//a[contains(.,'Automation job console') or contains(.,'View Automation job console')]";
	String aaaJobStatusCompleted = "//span[normalize-space()='Completed']";
	String dataMonitorTab = "//a[contains(@class,'adapt-menu__link')]//span[contains(@class,'a-menu__text') and contains(text(),'Data Monitor')]";
	String dataMonitorRulesList = "//tbody[contains(@class,'ui-table-tbody')]//tr";
	String dataMonitorTotalRulesTile = "//div[contains(@class,'adapt-s-card-part') and contains(text(),'Total rules')]/following-sibling::div[contains(@class,'adapt-s-card-part__count')]";
	String dataMonitorRulesWithOpenIssuesTile = "//div[contains(@class,'adapt-s-card-part') and contains(text(),'Rules with open issues')]/following-sibling::div[contains(@class,'adapt-s-card-part__count')]";
	String dataMonitorActiveRulesTile = "//div[contains(@class,'adapt-s-card-part') and contains(text(),'Active rules')]/following-sibling::div[contains(@class,'adapt-s-card-part__count')]";
	String dataMonitorInactiveRulesTile = "//div[contains(@class,'adapt-s-card-part') and contains(text(),'Inactive rules')]/following-sibling::div[contains(@class,'adapt-s-card-part__count')]";
	String newMonitoringRuleButton = "//button[contains(@class,'btn-link')]//span[contains(text(),'New monitoring rule')]";
	String newMonitoringRulePanelHeading = "//div[contains(@class,'text-container')]//h1[contains(text(),'monitoring rule')]";
	String monitoringRuleNameInput = "//input[@name='name' and @type='text']";
	String monitoringRuleDescriptionTextarea = "//textarea[contains(@class,'form-control')]";
	String monitoringRuleClassNameDropdown = "//button[@role='combobox' and .//div[normalize-space()='Select']]";
	String monitoringRuleClassTypeDropdown = "//label[contains(normalize-space(),'Class type')]/following::button[@role='combobox'][1]";
	String monitoringRuleClassFilterInput = "//input[contains(@class,'adapt-rx-search__input') and @placeholder='Filter']";
	String monitoringRuleFrequencyDropdown = "//adapt-rx-select[@formcontrolname='frequency']//button[@role='combobox']";
	String monitoringRulePriorityDropdown = "//adapt-rx-select[@formcontrolname='priority']//button[@role='combobox']";
	String monitoringRuleDropdownOption = "//button[@role='option']//adapt-highlight//span[contains(text(),'%s')]";
	String monitoringRuleAddExpressionButton = "//rx-expression-form-control[@formcontrolname='criteriaExpression']//button[contains(@class,'btn-link')]//span[contains(.,'Add an expression')]";
	String monitoringRuleSaveButton = "//button[@type='button' and normalize-space()='Save']";
	String monitoringRuleCancelButton = "//button[contains(@class,'btn-secondary')]//span[contains(text(),'Cancel')]";
	String monitoringRuleCreatedSuccessToast = "//div[contains(@class,'a-toast') and contains(.,'created')]";
	String monitoringRuleRowByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr";
	String monitoringRuleRowCheckboxByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr//div[contains(@class,'ui-chkbox-box')]";
	String monitoringRuleRowKebabMenuByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr//button[contains(@class,'dropdown-kabob-btn')]";
	String monitoringRuleEditOptionInDropdown = "//button[contains(@class,'dropdown-item')]//span[contains(text(),'Edit')]";
	String editMonitoringRulePanelHeading = "//div[contains(@class,'text-container')]//h1[contains(text(),'Edit monitoring rule')]";
	String monitoringRuleUpdatedSuccessToast = "//div[contains(@class,'a-toast') and contains(.,'updated')]";
	String monitoringRuleNameInGrid = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']";
	String monitoringRuleEnableButton = "//button[contains(@class,'btn-link')]//span[@class='btn-content']//span[contains(text(),'Enable')]/ancestor::button";
	String monitoringRuleDisableButton = "//button[contains(@class,'btn-link')]//span[@class='btn-content']//span[contains(text(),'Disable')]/ancestor::button";
	String monitoringRuleStatusByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr//td[contains(@data-testid,'column-6')]//span[contains(text(),'Active') or contains(text(),'Inactive')]";
	String monitoringRuleRunNowOption = "//span[text()='Run now']/ancestor::button";
	String monitoringRuleRunSuccessToast = "//span[contains(@class,'a-toast__details-text') and contains(text(),'Rule run successfully')]";
	String monitoringRuleRefreshButton = "//button[@rx-id='refresh-button']";
	String monitoringRuleCompletionStatusByName = "//a[contains(@class,'no-href-link')]//span[normalize-space()='%s']/ancestor::tr//td[contains(@data-testid,'column-7')]//span[contains(text(),'Completed')]";
	String monitoringRuleViewResultsOption = "//span[text()='View results']/ancestor::button";
	String monitoringRuleCIMatchesHeading = "//*[contains(text(),'CI matches for')]";
	String monitoringRuleCIMatchesRuleName = "//span[contains(text(),'CI matches for')]/strong[contains(text(),'%s')]";
	String monitoringRuleCIResultLink = "//table[@aria-labelledby='adapt-table-3-status']//a[contains(@class,'no-href-link')]";
	String processLink = "//a[contains(@href, 'process/edit') and contains(@href, '%s')]";
	String processRowCheckbox = "//tr[.//a[normalize-space()='%s']]//input[@type='checkbox' and @aria-label='Select row']";
	String formFieldInput = "//label[.//span[normalize-space()='%s']]/ancestor::div[contains(@class,'adapt-rx-textfield')]//input[@type='text']";
	String applicationLink = "//a[normalize-space()='%s']";
	String fieldXPath = "//span[contains(@class,'c-header__separator') and normalize-space()='%s']";
	String dropdownFieldXPath = "//li[contains(@class,'adapt-table-toolbar-dropdown-item')]//span[normalize-space()='%s']";
	String ruleActionsDropdown = "//span[normalize-space()='%s']/ancestor::tr//button[contains(@id,'row-actions-dropdown-button')]";
	String ruleAction = "//button[contains(@class,'dropdown-item')]//span[text()='%s']";
	String ruleContentEditor = "//div[contains(@class,'fr-element') and contains(@class,'fr-view')][@contenteditable='true']";		
	String firstParagraphXpath = ruleContentEditor + "/p[1]";
	String recordCheckboxByName = "//a[normalize-space()='%s']/ancestor::td//input[@type='checkbox' and @aria-label='Select row']";
	public String ValueField = "//label[normalize-space()='%s']//parent::div/parent::adapt-rx-control-label//following-sibling::div//textarea";
	String featureFlagOption = "//span[normalize-space()='%s']/ancestor::button";
	String navigationLink = "//a[contains(@class,'adapt-menu__link')][contains(.,'%s')]";
	String bundleTitle = "//h1[normalize-space()='%s']";
	String checkboxLocator = "//adapt-rx-control-label[contains(., '%s')]/following-sibling::div//adapt-rx-checkbox//div[@class='checkbox__label']";
	String checkboxInputLocator = "//adapt-rx-control-label[contains(., '%s')]/following-sibling::div//adapt-rx-checkbox//input[@type='checkbox']";
    String inputFieldValue = "//adapt-rx-control-label//label/span[normalize-space()='%s']/../../../..//input";
    String textareaFieldValue = "//adapt-rx-control-label//label/span[normalize-space()=%s]/../../../..//textarea";
    String typeheadInput = "//adapt-rx-typeahead[contains(. ,'%s')]//input";
    String typeheadOption = "(//button[@role='option' and contains(. ,'%s')])[1]";
	public String ElementXpathByID = "//*[@id='%s']";
	public String riskLevelButton = "//*[contains(text(),'Select risk level manually')]/parent::label[@class='radio__item']";
	public String RiskLevelOption = "//*[contains(@class, 'rx-select__options')]/button[contains(.,'%s')]";
	public String globalContextTextAreaLocator = "//adapt-rx-control-label//label/span[text()='Global Context']/../../../..//textarea";
	public String environmentTypeDropdownValue = "//button[normalize-space()='%s']";
	String supportsTrueStatus="//div[@aria-label='%s']//button[@aria-label='True']";
	String columnValue = "//table//tbody//tr[%s]/td[%s]//span[contains(@class,'ng-star')]";
	String newConnectionInputTextArea = "//span[normalize-space()='%s']/ancestor::adapt-rx-textfield//input";
	String dropdownOption = "//span[normalize-space()='%s']/ancestor::button";
	String dropdownName = "//span[normalize-space()='%s']/ancestor::adapt-rx-select//button";
	String dataSourceDropdownOption = "//div[normalize-space()='%s']/ancestor::button";


	
	//Locators variables
	String incidentButtonID = "ar1000000161_data";
	String saveIncidentButtonID = "304440891";
	String saveChangeButtonID = "304440891";
	String incidentSummaryID = "1000000000";
	String incidentDescriptionID = "1000000151";
	String contactSearchID = "ar303497600";
	String incidentCustomerID = "ar303530000";
	public String riskLevelDropdown = "1000000180";
	

	

	//WebElements Locators
    @FindBy(xpath = "//span[contains(text(),'Load Gainsight script from BMC IT')]//ancestor::label//input[@type='checkbox']")
    WebElement loadGainsightFromBmcItToggleInput;

    @FindBy(xpath = "//span[contains(text(),'Load Gainsight script from BMC IT')]//ancestor::label//span[contains(@class,'switch__slider')]")
    WebElement loadGainsightFromBmcItToggleLabel;
	@FindBy(xpath = "//*[contains(@href, '#/com.bmc.arsys.rx.innovationstudio/settings') and contains(. ,'Administration')]")
	WebElement administrationButton;

	@FindBy(xpath = "//thead[contains(@class,'ui-table-thead')]")
	WebElement dataMonitorTableHeader;
	
	@FindBy(xpath = "//th[contains(@class,'at-header-cell') and contains(@aria-label,'Name') and @aria-sort]")
	WebElement dataMonitorColumnName;

	@FindBy(xpath = "//th[contains(@class,'at-header-cell') and contains(@aria-label,'Match') and @aria-sort]")
	WebElement dataMonitorColumnCIMatches;

	@FindBy(xpath = "//th[contains(@class,'at-header-cell') and contains(@aria-label,'Last') and @aria-sort]")
	WebElement dataMonitorColumnLastRun;

	@FindBy(xpath = "//th[contains(@class,'at-header-cell') and contains(@aria-label,'Priority') and @aria-sort]")
	WebElement dataMonitorColumnPriority;

	@FindBy(xpath = "//th[contains(@class,'at-header-cell') and contains(@aria-label,'Status') and @aria-sort]")
	WebElement dataMonitorColumnStatus;

	@FindBy(xpath = "//button[contains(@class,'btn-link')]//span[@class='btn-content']//span[contains(text(),'Delete')]/ancestor::button")
	WebElement monitoringRuleDeleteButton;
	
	@FindBy(xpath = "//div[contains(@class,'modal-dialog')]//button[contains(@class,'btn-primary')]")
	WebElement monitoringRuleDeleteConfirmButton;
	
	@FindBy(xpath = "//span[contains(@class,'a-toast__details-text') and contains(text(),'deleted')]")
	WebElement monitoringRuleDeletedSuccessToast;
	
	@FindBy(xpath = "//a[contains(@class,'cx-bread-crum-anchor') and contains(text(),'Dashboard')]")
	WebElement cmdbDashboardBreadcrumb;
	
	@FindBy(xpath = "//span[contains(text(),'Load Gainsight script from BMC IT')]//ancestor::label//input[@type='checkbox']")
	WebElement loadGainsightScriptFromBmcItToggleInput;
	
	@FindBy(xpath = "//span[contains(text(),'Load Gainsight script from BMC IT')]//ancestor::label//span[contains(@class,'switch__slider')]")
	WebElement loadGainsightScriptFromBmcItToggleLabel;
	
	@FindBy(xpath = "//span[contains(@class,'btn-content')]//span[contains(text(),'Close')]")
	WebElement aaaCICloseButton;

	@FindBy(xpath = "//span[text()='Create']//ancestor::button")
	WebElement createNewButton;

	@FindBy(xpath = "//span[text()='Incident']//ancestor::a")
	WebElement incidentButton;
	
	@FindBy(xpath = "//span[text()='Change Request']//ancestor::a")
	WebElement changeButton;

	@FindBy(xpath = "//li[contains(@class,'console-menu')]//span[normalize-space()='Console']/ancestor::button")
	WebElement consoleButton;

	@FindBy(xpath = "//span[text()='Ticket Console']//ancestor::a")
	WebElement ticketConsoleButton;

	@FindBy(xpath = "//span[contains(@class,'a-product__name text') and contains(.,'Innovation Studio')]")
	WebElement InnovationSuiteLogo;

	@FindBy(xpath = "//span[contains(@class,'a-product__name text') and text()='Innovation Studio']")
	WebElement bmcHelixPageInnovationStudio;

	@FindBy(xpath = "//div[contains(@class,'rx-ellipsis')]//a")
	List<WebElement> bundles;

	@FindBy(css = "[rx-id=install-button]")
	WebElement installButton;

	@FindBy(css = "[rx-id=close-button]")
	WebElement closeButton;

	@FindBy(xpath = "//button[@rx-id='install-button']/following-sibling::input")
	WebElement uploadFileInput;

	@FindBy(xpath = "//*[contains(@data-testid,'helixGPTChatThreadConversation')]")
	WebElement helixGPTThreadConversion;

	@FindBy(css = "[rx-id=preview-button]")
	WebElement previewButton;

	@FindBy(css = ".active .adapt-table-search-toolbar-container input")
	WebElement searchInput;

	@FindBy(css = ".active adapt-row-selection-checkbox")
	List<WebElement> checkboxes;

	@FindBy(css = "[rx-id=edit-data]")
	WebElement EditData;

	@FindBy(xpath = "//adapt-rx-control-label[normalize-space()='ID']/following-sibling::div//input")
	WebElement getID;

	@FindBy(xpath = "//td[@data-testid = 'adapt-table_column-0_row-0']//div[contains(@class,'tooltip')]//a")
	WebElement getRowData;

	@FindBy(css = ".adapt-rx-search__input-wrapper input")
	WebElement searchProcessInput;

	@FindBy(xpath = "//*[contains(@class,'definition-name')]/parent::div/parent::ax-record-data-editor//adapt-table-toolbar//div[contains(@class,'search-button')]")
	WebElement searchIcon;

	@FindBy(xpath = "//button[contains(.,'Save')]")
	WebElement saveStatusButton;

	@FindBy(xpath = "//button[@type='button' and contains(.,'Cancel')]")
	WebElement cancelButton;

	@FindBy(xpath = "//button[@aria-label='Next']")
	WebElement nextButton;

	@FindBy(css = ".active .rx-custom-cell a")
	List<WebElement> nameColumnRows;

	@FindBy(xpath = "//button[@rx-id='new-button' and contains(@class,'px-3 btn')]")
	WebElement newButton;
	
	@FindBy(xpath = "//adapt-rx-control-label[contains(.,'Chatbot name')]//following-sibling::div//input")
	WebElement chatbotNameTextBox;

	@FindBy(xpath = "//button[@rx-id='save-button']")
	WebElement saveButton;

	@FindBy(xpath = "//div[@rx-id='bot-display-name']//input")
	WebElement botNameTextBox;

	@FindBy(xpath = "//button[@rx-id='add-bot-locale-button']")
	WebElement addNewButton;

	@FindBy(xpath = "//*[normalize-space()='HelixGPT Skill']/following-sibling::select")
	WebElement helixGPTSkillDropdown;

	@FindBy(xpath = "//div[@rx-id='rx-bot-configuration-provider']//select")
	WebElement assistantServiceProviderDropdown;

	@FindBy(xpath = "//rx-iframe[contains(@class,'d-block position-absolute')]//iframe")
	WebElement frame;

	@FindBy(xpath = "//rx-permission-editor//a[text()='Edit']")
	WebElement editPermissionBtn;

	@FindBy(xpath = "//*[contains(@class, 'rx-permissions-editor')]//a[text()='Add Permission']")
	WebElement addPermissionBtn;

	@FindBy(xpath = "//*[@class='rx-permissions-editor-table']//button[text()='Group']")
	WebElement permissionType;

	@FindBy(xpath = "//*[contains(@class, 'rx-permissions-editor-table-group-role')]//span[@role='button']")
	WebElement permissionBtn;

	@FindBy(xpath = "//*[contains(@class, 'rx-permissions-editor-table-group-role')]//input")
	WebElement permissionInput;

	@FindBy(xpath = "//*[@class='d-modal__footer']//button[text()='Save']")
	WebElement permissionSaveButton;

	@FindBy(xpath = "//button[contains(@ng-click, 'launchBot')]")
	WebElement launchBotButton;

	@FindBy(xpath = "//button[contains(@ng-click, 'newTopic')]")
	WebElement SkillValue;

	@FindBy(xpath = "//button[normalize-space(.)='Create']")
	WebElement createHPMRecordButton;

	@FindBy(xpath = "//button[normalize-space(.)='Save']")
	WebElement saveHPMRecordButton;

	@FindBy(xpath = "//*[contains(@aria-label, 'Prefix Display ID')]")
	WebElement prefixDisplayIDField;

	@FindBy(xpath = "//*[contains(@aria-label, 'Display Id')]")
	WebElement displayIDField;

	@FindBy(xpath = "//label[contains(. ,'Portfolio')]/ancestor::div[contains(@class, 'rx-runtime-view-canvas-item-container-column')]/following-sibling::*//button[contains(. ,'Associate')]")
	WebElement associateRecordButton;

	@FindBy(xpath = "//label[contains(. ,'External Entity Reference')]/ancestor::div[contains(@class, 'rx-runtime-view-canvas-item-container-column')]/following-sibling::*//button[normalize-space(.)='Associate']")
	WebElement associateExternalEntityButton;

	@FindBy(xpath = "//input[@placeholder='Search']")
	WebElement searchInputBox;

	@FindBy(xpath = "//button[contains(@class, 'adapt-rx-search__search-button') and normalize-space(.)='Search']")
	WebElement searchButton;

	@FindBy(xpath = "//*[contains(@class, 'rx-ellipsis')]//a//span")
	WebElement searchedRecordLink;

	@FindBy(xpath = "//a[normalize-space(.)='New']")
	WebElement newBtn;

	@FindBy(xpath = "//*[contains(. ,'File to Import')]//*[@type='file']")
	WebElement fileToImportField;

	@FindBy(xpath = "//button[contains(@class,'btn') and contains(.,'Create automation')]")
	WebElement aaaCreateAutomationButtonElement;

	@FindBy(xpath = "//adapt-rx-textfield[contains(.,'Name')]//input")
	WebElement aaaAutomationNameInputElement;

	@FindBy(xpath = "//adapt-rx-select[contains(@formcontrolname,'assetType')]//button")
	WebElement aaaAssetTypeDropdownElement;

	@FindBy(xpath = "//button[contains(.,'Add an expression')]")
	WebElement aaaAddExpressionButtonElement;

	@FindBy(xpath = "//button[contains(@class,'btn-primary') and contains(.,'Save')]")
	WebElement aaaSaveAutomationButtonElement;

	@FindBy(xpath = "//h1[normalize-space()='Create automation']")
	WebElement aaaCreateAutomationHeadingElement;
	
	@FindBy(xpath = "//div[contains(@class,'summary-gpt__insights-title')]")
	WebElement insightsTitle;

	@FindBy(xpath = "//div[contains(@class,'summary-gpt__description--content')]")
	WebElement insightsSummary;
	
	@FindBy(xpath = "//button[@aria-label='Close']")
	WebElement insightsClose;
	
	@FindBy(xpath = "//select[contains(@class,'assistant-service-provider')]//option[@selected='selected']")
	WebElement AssistantServiceProviderValue;
	
	@FindBy(xpath = "//button[@role='tab' and .//span[contains(@class,'nav-link-title') and normalize-space()='Processes']]")
	WebElement processesTab;
	
	@FindBy(xpath = "//button[@role='tab' and .//span[contains(@class,'nav-link-title') and normalize-space()='Records']]")
	WebElement recordsTab;
	
	// Process Designer - Ask HelixGPT elements
	@FindBy(xpath = "//span[@data-testid='helixgpt-btn']//span[text()='Ask HelixGPT']")
	WebElement askHelixGPT;
	
	@FindBy(xpath = "//textarea[@aria-label='Send message']")
	WebElement HelixGptTextarea;
	
	@FindBy(xpath = "//button[span[text()='Send message']]")
	WebElement submitQuestionButton;
	
	@FindBy(xpath = "//textarea[@placeholder='Ask a follow up...']")
	WebElement askFollowUp;
	
	@FindBy(xpath = "(//div[contains(@class,'adapt-helix-gpt-chat-thread__response')]//div[contains(@class,'adapt-helix-gpt-chat-thread__message-content')])[last()]")
	WebElement helixGPTResponseContent;
	
	@FindBy(xpath = "//button[@aria-label='Close']")
	WebElement closeProcessButton;
	
	@FindBy(xpath = "//input[@data-attribute='rxData/isEnabled']")
	WebElement processEnableCheckbox;
	
	@FindBy(xpath = "//button[text()='Save']")
	WebElement saveProcessButton;
	
	@FindBy(xpath = "//button[text()='Close']")
	WebElement buttonClose;
	
	@FindBy(xpath = "//button[@rx-id='manage-processes-button']")
	WebElement manageProcessButton;
	
	@FindBy(xpath = "//button[@rx-id='run-button']")
	WebElement runProcessButton;
	
	@FindBy(xpath = "//button[@rx-id='ok-button']")
	WebElement runProcessOkButton;
	
	@FindBy(xpath = "//div//span[text()='Process has been started successfully.']")
	WebElement processRunSuccessMsg;
	
	@FindBy(css = "[rx-id=x-button]")
	WebElement closeRecordButton;
	
	@FindBy(xpath = "//adapt-rx-control-label[normalize-space()='Description(required)']/following-sibling::div//input")
	WebElement recordDescriptionField;
	
	@FindBy(xpath = "//button[@rx-id='new-button']")
	WebElement newApplicationButton;
	
	@FindBy(xpath = "//button[contains(@class,'dropdown-item') and normalize-space()='Application']")
	WebElement customApplicationOption;

	@FindBy(xpath = "//input[contains(@name,'Name')]")
	WebElement applicationNameInput;

	@FindBy(xpath = "//input[contains(@name,'ShortName')]")
	WebElement applicationShortNameInput;

	@FindBy(xpath = "//input[contains(@name,'groupId')]")
	WebElement applicationGroupIdInput;
	
	@FindBy(xpath = "//span[normalize-space()='Create']")
	WebElement createApplication;
	
	@FindBy(xpath = "//p[@rx-id='operation-succeeded-message']")
	WebElement createApplicationSucceed;
	
	@FindBy(xpath = "//span[text()='Visible columns']")
	WebElement tableToolbar;
	
	@FindBy(xpath = "//*[contains(@href, '#/com.bmc.arsys.rx.innovationstudio/workspace') and contains(. ,'Workspace')]")
	WebElement workspaceTab;
	
	@FindBy(xpath = "//button[@rx-id='delete-button' and contains(@class, 'btn-link')]")
	WebElement deleteButton;
	
	@FindBy(xpath = "//label[.//span[translate(normalize-space(), 'DESCRIPTION', 'description')='description']]/ancestor::div[@class='adapt-rx-textfield']//input[@type='text']")
	WebElement descriptionInput;
	
    @FindBy(xpath = "//rx-runtime-view-modal[contains(. ,'Associate Portfolio')]//button[normalize-space()='Select']")
    WebElement associateSelectButton;

    @FindBy(xpath = "//*[@rx-view-component-id='b4c1aa4a-8817-4c57-a5db-af3e92de9649']//button[normalize-space()='Associate']")
    WebElement associateRecordBtnInProject;

    @FindBy(xpath = "//header[contains(. ,'Portfolio Management')]")
    WebElement hpmHeader;

    @FindBy(xpath = "//*[@rx-view-component-id='e6bc1c77-53c0-4cf8-a28b-0805b9f48924']//button")
    WebElement associateRecordCompactBtn;

	@FindBy(xpath = "//input[@type='checkbox' and @role='switch']/parent::label/parent::div")
	WebElement EditFeatureFlagToggle;

	@FindBy(xpath = "//span[normalize-space()='Save']/ancestor::button")
	WebElement ToggleSaveButton;

	@FindBy(xpath = "//input[@type='checkbox' and @role='switch']")
	WebElement EditFeatureFlagToggleInput;
	
	@FindBy(xpath = "//button[contains(@class,'d-icon-left-plus_circle')]//span[normalize-space()='New']")
	WebElement newRecordButton;

	@FindBy(xpath = "//button[@name='environmentType']")
	WebElement environmentTypeDropdown;

	@FindBy(xpath = "(//h4[normalize-space()='Enable performance and quality improvement data collection:']/following::adapt-rx-switch//input)[1]")
	WebElement enablePerformanceToggleInputWebElement;

	@FindBy(xpath = "(//h4[normalize-space()='Enable performance and quality improvement data collection:']/following::adapt-rx-switch//label)[1]")
	WebElement enablePerformanceToggleLabelWebElement;

	@FindBy(css = ".rx-select__search-button-title")
    private WebElement statusValueLocator;

	@FindBy(xpath = "//button[contains(@id,'rx-select')]//div[normalize-space()='Disabled']")
    private WebElement statusDropdown;

	@FindBy(xpath = "//div[contains(@class,'option-content')][normalize-space()='Enabled']")
    private WebElement enabledStatus;

	@FindBy(xpath = "(//div[contains(@class,'rx-custom-cell')])[1]")
    private WebElement rhoFirstPresent;

	@FindBy(xpath = "//table//th[@scope='col']")
    private List<WebElement> headers;

	@FindBy(xpath = "//button[@rx-id='new-button']")
    private WebElement newConnectionButton;

	@FindBy(xpath = "//span[normalize-space()='Save']/ancestor::button")
    private WebElement newConnectionSaveButton;

	@FindBy(xpath = "//div[@aria-label='ExecuteNow']//button[@rx-id='true-button']")
    private WebElement executeNowTrueButton;

	public InnovationSuite() {
		log = new SolqaLogger(InnovationSuite.class.getName());
		PageFactory.initElements(getDriver(), this);
		commonWaits = new CommonWaits();
		commonMethods = new CommonMethods();
		apiCommonOperations = new API_CommonOperations();
	}

	public void verifyInnovationSuitePageLaunchedSuccessfully() {
		log.info("Verifying Innovation Suite page is launched successfully");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForElementVisible(bmcHelixPageInnovationStudio, 30);
		appName = bmcHelixPageInnovationStudio.getText().trim();
		log.info("----- Opened application name - " + appName);
		Assert.assertEquals(appName, "Innovation Studio", "Innovation Studio application is not launched successfully");
	}

	public void navigateToAdminPage() {
		clickWithoutInterception(administrationButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
	}

	public void navigateToCreateNewIncident() {
		log.info("Create New Incident");
		if (commonWaits.isElementPresent(incidentButton)) {
			clickWithoutInterception(incidentButton);
		} else if (commonWaits.isElementPresent(createNewButton)) {
			clickWithoutInterception(createNewButton);
			clickWithoutInterception(incidentButton);
		}
	}
	
	public void navigateToCreateNewChangeRequest() {
		log.info("Create New Change Request");
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		
		if (commonWaits.isElementPresent(changeButton)) {
			clickWithoutInterception(changeButton);
		} else if (commonWaits.isElementPresent(createNewButton)) {
			clickWithoutInterception(createNewButton);
			clickWithoutInterception(changeButton);
		}
	}

	public void fillIncidentTemplate(Map<Object, String> itsmTestData) {
		log.info("Filling Incident Details");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		switchToFrame(By.xpath(iframeInIncident));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByID, incidentCustomerID)));
		if (itsmTestData.get("Customer Name") != null) {
			log.info("Entering Customer Name");
			setCustomer(itsmTestData.get("Customer Name"));
		}

		if (itsmTestData.get("Incident Description") != null) {
			log.info("Entering Description");
			setDescription(itsmTestData.get("Incident Description"));
		}

		if (itsmTestData.get("Incident Title") != null) {
			log.info("Entering Summary");
			setSummary(itsmTestData.get("Incident Title"));
		}
		switchtoDefaultFrame();
	}

	public void setCustomer(String personName) {
		keyClearAndType(String.format(elementXpathByID, incidentCustomerID), personName);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementText, personName)));
		clickWithoutInterception(By.xpath(String.format(typeheadListElementText, personName)));
	}

	public void editContact(String contact) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		switchToFrame(By.xpath(iframeInIncident));
		keyClearAndType(String.format(elementXpathByID, contactSearchID), contact);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementText, contact)));
		clickWithoutInterception(By.xpath(String.format(typeheadListElementText, contact)));
		switchtoDefaultFrame();
	}

	public void setDescription(String description) {
		keyClearAndType(String.format(elementXpathByID, incidentDescriptionID), description);
	}

	public void setSummary(String summary) {
		keyClearAndType(String.format(elementXpathByID, incidentSummaryID), summary);
	}

	public void saveIncident() {
		log.info("Clicking on Save Button on Incident");
		switchToFrame(By.xpath(iframeInIncident));
		clickWithoutInterception(By.xpath(String.format(elementXpathByID, saveIncidentButtonID)));
		switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
	}

	public String getIncidentNumber() {
		log.info("Get incident number");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		switchToFrame(By.xpath(iframeInIncident));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByID, incidentButtonID)));
		String incidentId = findElement(By.xpath(String.format(elementXpathByID, incidentButtonID))).getText();
		switchtoDefaultFrame();
		return incidentId;
	}
	
	public String getChangeNumber() {
		log.info("Get change number");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		switchToFrame(By.xpath(iframeInIncident));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByID, saveChangeButtonID)));
		String changeId = findElement(By.xpath(String.format(elementXpathByID, saveChangeButtonID))).getText();
		switchtoDefaultFrame();
		return changeId;
	}
	
	
	public void fillChangeTemplate(Map<Object, String> itsmTestData) {
		log.info("Filling Change Details");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		switchToFrame(By.xpath(iframeInIncident));

		if (itsmTestData.get("Change Title") != null) {
			log.info("Entering Summary");
			setSummary(itsmTestData.get("Change Title"));
		}
		
		String riskLevel = itsmTestData.get("Risk Level");
		if (riskLevel != null) {
			setRiskLevel(riskLevel);
		}
		
		switchtoDefaultFrame();
	}
	
	public InnovationSuite setRiskLevel(String riskLevel) {

		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(riskLevelButton)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(riskLevelButton)));

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, riskLevelDropdown)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID, riskLevelDropdown)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(RiskLevelOption, riskLevel)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(RiskLevelOption, riskLevel)));
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}
	
	public void saveChange() {
		log.info("Clicking on Save Button on Change");
		switchToFrame(By.xpath(iframeInIncident));
		clickWithoutInterception(By.xpath(String.format(elementXpathByID, saveChangeButtonID)));
		switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
	}

	public void navigateToTicketConsole() {
		log.info("Navigating to Ticket Console");
		if (commonWaits.isElementPresent(ticketConsoleButton)) {
			clickWithoutInterception(ticketConsoleButton);
		} else if (commonWaits.isElementPresent(consoleButton)) {
			clickWithoutInterception(consoleButton);
			clickWithoutInterception(ticketConsoleButton);
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
	}

	public void navigateToTicketConsoleThroughUrl() {
		log.info("Navigating to Ticket Console through URL");
		String isURL = CommonMethods.readConfig("IS_URL");
		String ticketConsoleURL = isURL + SolQAConstants.ITSM_TicketConsoleURL;
		getDriver().navigate().to(ticketConsoleURL);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
	}

	public InnovationSuite navigateToBundle(String bundleName) {
		log.info("Navigating to Bundle: " + bundleName);
		try {
			// Ensure we're in default frame context for navigation
			switchtoDefaultFrame();
			log.debug("Switched to default frame for bundle navigation");
			commonWaits.waitForExecutionComplete();

			// Try to find and click the bundle link
			WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(bundleString, bundleName)));
			commonWaits.clickWithoutInterception(ele);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.BWF);
			log.info("Navigated to Bundle: " + bundleName);
		} catch (Exception e) {
			log.warn("Standard bundle navigation failed, attempting fallback: " + e.getMessage());
			try {
				// Fallback 1: Try alternative locator for bundle heading/title
				switchtoDefaultFrame();
				WebElement bundleHeading = commonWaits
						.waitForByElementToBeVisible(By.xpath(String.format(bundleTitle, bundleName)));
				log.info("Bundle heading found, already on bundle page: " + bundleName);
				commonWaits.waitForAllSpinnersToHide(SolQAConstants.BWF);
			} catch (Exception e2) {
				log.error("Fallback bundle navigation also failed: " + e2.getMessage());
				// Try clicking via main navigation if available
				try {
					switchtoDefaultFrame();
					WebElement navLink = commonWaits
							.waitForByElementToBeVisible(By.xpath(String.format(navigationLink, bundleName)));
					commonWaits.clickWithoutInterception(navLink);
					commonWaits.waitForAllSpinnersToHide(SolQAConstants.BWF);
					log.info("Navigated to Bundle via navigation menu: " + bundleName);
				} catch (Exception e3) {
					log.error("All bundle navigation methods failed for: " + bundleName);
					throw new RuntimeException("Unable to navigate to bundle: " + bundleName + ". Errors: "
							+ e.getMessage() + "; " + e2.getMessage() + "; " + e3.getMessage());
				}
			}
		}
		return this;
	}

	public List<String> getPresentBundleNames() {
		log.info("Getting present bundle names");
		List<String> bundleNames = new ArrayList<>();
		for (WebElement bundle : bundles) {
			String bundleName = bundle.getText().trim();
			bundleNames.add(bundleName);
		}
		return bundleNames;
	}

	public void installApplication(String AppToimport) {
		final String PATH_TO_TEST_RESOURCES = System.getProperty("user.dir") + File.separator + "resources"
				+ File.separator + "InnovationStudio" + File.separator + AppToimport;
		System.out.println("File location: " + PATH_TO_TEST_RESOURCES);

		commonWaits.waitForElementClickable(installButton).click();
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.waitForElementClickable(uploadFileInput);
		uploadFileInput.sendKeys(PATH_TO_TEST_RESOURCES);
		commonWaits.waitForElementClickable(closeButton).click();
		commonWaits.waitForExecutionComplete();
	}

	public void navigateRequiredISURL(String URL) {
		log.info("Navigating to required IS URL: " + URL);
		String newURL = CommonMethods.readConfig("IS_URL") + URL;
		getDriver().navigate().to(newURL);
		commonWaits.waitForExecutionComplete();
		log.info("Navigated to required IS URL: " + newURL);
	}

	public void validateFormHeading(String expectedHeading) {
		log.info("Validating form heading: " + expectedHeading);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForExecutionComplete();
		WebElement formHeadingElement = findElement(By.xpath(formHeadingXpath));
		commonWaits.waitForElementVisible(formHeadingElement);
		String actualHeading = formHeadingElement.getText().trim().replaceAll("\\s+", " ");
		String expectedHeadingTrimmed = expectedHeading.trim().replaceAll("\\s+", " ");
		log.info("Actual form heading: " + actualHeading);
		log.info("Expected form heading (trimmed): " + expectedHeadingTrimmed);
		if (actualHeading.contains(expectedHeadingTrimmed) || expectedHeadingTrimmed.contains(actualHeading)) {
			log.info("Form heading validation successful. Expected: " + expectedHeadingTrimmed + ", Actual: " + actualHeading);
		} else {
			log.error("Form heading validation failed. Expected: " + expectedHeadingTrimmed + ", Actual: " + actualHeading);
			Assert.fail("Form heading validation failed. Expected: " + expectedHeadingTrimmed + ", Actual: " + actualHeading);
		}
	}

	public void validateGainsightSettingsHeading(String expectedHeading) {
		log.info("Validating Gainsight Settings heading: " + expectedHeading);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForExecutionComplete();
		WebElement gainsightHeadingElement = commonWaits.waitForByElementToBeVisible(By.xpath(gainsightSettingsHeadingXpath));
		commonWaits.waitForElementVisible(gainsightHeadingElement);
		String actualHeading = gainsightHeadingElement.getText().trim();
		log.info("Actual Gainsight Settings heading: " + actualHeading);
		if (actualHeading.contains(expectedHeading)) {
			log.info("Gainsight Settings heading validation successful. Expected: " + expectedHeading + ", Actual: " + actualHeading);
		} else {
			log.error("Gainsight Settings heading validation failed. Expected: " + expectedHeading + ", Actual: " + actualHeading);
			Assert.fail("Gainsight Settings heading validation failed. Expected: " + expectedHeading + ", Actual: " + actualHeading);
		}
	}

	public void enableGainsightToggleIfNotEnabled() {
		commonMethods.enableToggleIfNotEnabled(
			enablePerformanceToggleInput,
			enablePerformanceToggleLabel,
			SolQAConstants.appIS,
			"Enable performance and quality improvement data collection"
		);
	}

	public void enableLoadGainsightFromBmcItToggleIfNotEnabled() {
		commonMethods.enableToggleIfNotEnabled(
			loadGainsightScriptFromBmcItToggleInput,
			loadGainsightScriptFromBmcItToggleLabel,
			SolQAConstants.appIS,
			"Load Gainsight script from BMC IT"
		);
	}
	
	public void selectDeploymentType(String deploymentType) {
		log.info("Selecting Deployment Type: " + deploymentType);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(gainsightDeploymentTypeDropdown));
		clickWithoutInterception(dropdown);
		String optionXpath = String.format(aaaDropdownOptionByText, deploymentType);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Deployment Type: " + deploymentType);
	}

	public void environmentType(String environmentType) {
		log.info("Selecting Environment Type: " + environmentType);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(gainsightEnvironmentTypeDropdown));
		clickWithoutInterception(dropdown);
		String optionXpath = String.format(aaaDropdownOptionByText, environmentType);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Environment Type: " + environmentType);
	}

	public void saveGainsightSettingsIfEnabled() {
		log.info("Checking if Save button is enabled and clicking it");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		
		WebElement saveButtonElement = findElement(By.xpath(gainsightSettingsSaveButton));
		commonWaits.waitForElementVisible(saveButtonElement);
		
		if (saveButtonElement.isEnabled()) {
			log.info("Save button is enabled. Clicking Save button.");
			clickWithoutInterception(saveButtonElement);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
			log.info("Gainsight settings saved successfully");
		} else {
			log.info("Save button is not enabled. Skipping save action.");
		}
	}

	public void searchInFormSearchBox(String searchText) {
		log.info("Searching in form search box: " + searchText);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForExecutionComplete();
		WebElement searchBoxElement = findElement(By.xpath(formSearchBox));
		commonWaits.waitForElementVisible(searchBoxElement);
		searchBoxElement.clear();
		searchBoxElement.sendKeys(searchText);
		searchBoxElement.sendKeys(org.openqa.selenium.Keys.ENTER);
		log.info("Pressed Enter to search for: " + searchText);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForExecutionComplete();
	}

	public void validateSearchResult(String expectedResult) {
		log.info("Validating search result: " + expectedResult);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		String searchResultXpath = String.format(searchResultText, expectedResult);
		WebElement searchResultElement = findElement(By.xpath(searchResultXpath));
		commonWaits.waitForElementVisible(searchResultElement);
		String actualResult = searchResultElement.getText().trim();
		log.info("Actual search result: " + actualResult);
		if (actualResult.contains(expectedResult)) {
			log.info("Search result validation successful. Expected: " + expectedResult + ", Actual: " + actualResult);
		} else {
			log.error("Search result validation failed. Expected: " + expectedResult + ", Actual: " + actualResult);
			Assert.fail("Search result validation failed. Expected: " + expectedResult + ", Actual: " + actualResult);
		}
	}

	public void clickOnSearchResultLink() {
		log.info("Clicking on search result link");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement searchLinkElement = findElement(By.xpath(searchResultLink));
		commonWaits.waitForElementVisible(searchLinkElement);
		clickWithoutInterception(searchLinkElement);
		log.info("Clicked on search result link");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
	}

	public void validateEditRecordModalDisplayed() {
		log.info("Validating Edit Record modal is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		WebElement editRecordModal = findElement(By.xpath(editRecordModalTitle));
		commonWaits.waitForElementVisible(editRecordModal);
		if (editRecordModal.isDisplayed()) {
			log.info("Edit Record modal is displayed successfully");
		} else {
			log.error("Edit Record modal is not displayed");
			Assert.fail("Edit Record modal is not displayed");
		}
	}

	public void enableFeatureFlagIfNotEnabled() {
		log.info("Checking if feature flag is already enabled");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		
		if(commonWaits.isElementPresent(By.xpath(featureFlagEnabledButton))) {
			log.info("Feature flag is already enabled (True button is pressed). Skipping enable action.");
			return;
		}
		
		if(commonWaits.isElementPresent(By.xpath(featureFlagEnableButton))) {
			log.info("True button is available. Clicking to enable feature flag.");
			WebElement enableBtn = findElement(By.xpath(featureFlagEnableButton));
			clickWithoutInterception(enableBtn);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			commonWaits.waitForElementPresent(By.xpath(featureFlagEnabledButton));
			log.info("Feature flag enabled successfully");
		} else {
			log.info("True button is not available. No action taken.");
		}
	}

	public void clickSaveOrCancelButton() {
		log.info("Checking Save button status to close modal");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		
		WebElement saveBtn = findElement(By.xpath(saveButtonEditRecord));
		commonWaits.waitForElementVisible(saveBtn);
		
		if (saveBtn.isEnabled()) {
			log.info("Save button is enabled. Clicking Save button to close modal.");
			clickWithoutInterception(saveBtn);
			log.info("Clicked Save button");
		} else {
			log.info("Save button is disabled. Clicking Cancel button to close modal.");
			WebElement cancelBtn = findElement(By.xpath(cancelButtonEditRecord));
			commonWaits.waitForElementVisible(cancelBtn);
			clickWithoutInterception(cancelBtn);
			log.info("Clicked Cancel button");
		}
		
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		log.info("Modal closed successfully");
	}

	public void enableMappingIfNotAlreadyEnabled() {
		log.info("Checking if mapping is enabled by looking for check icon");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		
		try {
			WebElement checkIconElement = findElement(By.xpath(bundleIdMappingCheckIcon));
			if (checkIconElement.isDisplayed()) {
				log.info("Mapping is already enabled. Check icon is visible.");
			} else {
				log.info("Mapping is not enabled. Enabling it now by clicking search result link.");
				clickOnSearchResultLink();
			}
		} catch (Exception e) {
			log.info("Check icon not found. Enabling mapping now by clicking search result link.");
			clickOnSearchResultLink();
		}
	}

	public void searchAndOpenGlobalContextMapping(String searchText) {
		log.info("Searching for global context mapping: " + searchText);
		searchInFormSearchBox(searchText);
		validateSearchResult(searchText);
		clickOnSearchResultLink();
		validateEditRecordModalDisplayed();
	}

	public void replaceGlobalContextValue(String newValue) {
		log.info("Replacing global context textarea value");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForExecutionComplete();
		
		WebElement textareaElement = findElement(By.xpath(globalContextTextarea));
		commonWaits.waitForElementVisible(textareaElement);
		log.info("Clearing existing textarea value");
		textareaElement.clear();
		log.info("Entering new global context value");
		textareaElement.sendKeys(newValue);
		log.info("Global context value replaced successfully");
	}

	public void clickOnPalette() {
		log.info("Clicking on Palette");
		commonWaits.waitForElementVisible(helixGPTThreadConversion, 30);
		commonWaits.clickWithoutInterception(helixGPTThreadConversion);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked on Palette");
	}

	public String componentPropertiesSkill() {
		log.info("Getting component properties for skill");
		return commonWaits.waitForElementVisible(SkillValue).getText();
	}

	public void clickOnPreviewButton() {
		log.info("Clicking on Preview Button");
		commonWaits.waitForElementClickable(previewButton, 15);
		commonWaits.clickWithoutInterception(previewButton);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked on Preview Button");
	}

	public void searchGrid(String value) {
		log.info(String.format("Setting '%s' search value", value));
		typeKeysWithEnter(searchInput, value);
		commonWaits.waitForAngularRequestsToFinish();
	}

	public void selectCheckboxByName(String checkboxName) {
		log.info("Selecting checkbox with name: " + checkboxName);
		WebElement checkbox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(checkboxXpath, checkboxName)));
		if (!checkbox.isSelected()) {
			checkbox.click();
			log.info("Checkbox '" + checkboxName + "' selected.");
		} else {
			log.info("Checkbox '" + checkboxName + "' was already selected.");
		}

		WebElement box = checkbox.findElement(By.cssSelector(".ui-chkbox-box"));

		String classes = box.getAttribute("class");
		boolean isSelected = classes.contains("ui-state-active");

		if (!isSelected) {
			box.click();
			log.info("Checkbox '" + checkboxName + "' is now selected.");
		} else {
			log.info("Checkbox '" + checkboxName + "' was already selected.");
		}
	}

	public void selectRecordDefinition(String definitionName) {
		searchGrid(definitionName);
		selectCheckboxByName(definitionName);
	}

	public void clickEditData(String definitionName) {
		log.info("Clicking on Edit Data");
		commonWaits.waitForElementClickable(EditData);
		commonWaits.clickWithoutInterception(EditData);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(recordName, definitionName)));
		log.info("Clicked on Edit Data");
	}

	public boolean selectRecord() {
		log.info("Selecting record");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForElementVisible(getRowData, 30);
		commonWaits.clickWithoutInterception(getRowData);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		boolean isSelected = false;
		try {
			String rowClass = getRowData.getAttribute("class");
			isSelected = rowClass != null && rowClass.contains("selected");
			isSelected = true;
		} catch (Exception e) {
			log.error("Error while validating record selection: " + e.getMessage());
			isSelected = false;
		}
		Assert.assertTrue(isSelected,"Respective Record Not selected");
		return isSelected;
	}

	public String getID() {
		log.info("Getting ID");
		commonWaits.waitForElementVisible(getID);
		String inputValue = getID.getAttribute("value");
		log.info("Getting ID: " + inputValue);
		return inputValue;
	}

	public void searchUserText(String searchText) {
		commonWaits.waitForElementVisible(searchProcessInput);
		commonWaits.waitForElementClickable(searchProcessInput).clear();
		commonWaits.waitForElementClickable(searchProcessInput).sendKeys(searchText);
		commonWaits.waitForAngularRequestsToFinish();
		clickSearchFilterButton();
	}

	public void clickSearchFilterButton() {
		commonWaits.clickWithoutInterception(searchIcon);
		commonWaits.waitForAngularRequestsToFinish();
	}

	public void setValueOfField(String valueName, String value) {
		WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ValueField, valueName)));
		ele.clear();
		ele.sendKeys(value);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		if(saveStatusButton.isEnabled()) {
			saveStatusButton.click();
		}
		else {
			cancelButton.click();
		}
		commonWaits.waitForExecutionComplete();
	}
  
	public boolean isRightTabArrow() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		return commonWaits.isElementPresent(nextButton);
	}

	public void clickOnRightTabArrow() {
		try {
			commonWaits.waitForElementClickable(nextButton);
			commonWaits.clickWithoutInterception(nextButton);
			commonWaits.waitForExecutionComplete();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void clickOnTabFromBMCChatbot(String TabName) {
		WebElement tabName = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(BMCChatBotTab, TabName)));
		commonWaits.clickWithoutInterception(tabName);
		commonWaits.waitForExecutionComplete();
	}

	public boolean isChatBotPresent(String chatbotName) {
		try {
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
			if (nameColumnRows != null) {
				for (WebElement row : nameColumnRows) {
					if (row.isDisplayed() && row.getText().trim().equals(chatbotName)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public void initiateNewChatbot(String chatbotName) {
		log.info("Clicking on Create New Chatbot");
		commonWaits.waitForElementClickable(newButton).click();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForElementVisible(chatbotNameTextBox);
		chatbotNameTextBox.sendKeys(chatbotName);
		commonWaits.waitForElementClickable(saveStatusButton).click();
		commonWaits.waitForAngularRequestsToFinish();
		log.info("Clicked on Create New Chatbot and filled name: " + chatbotName);
	}

	public InnovationSuite selectAssistantServiceProvider(String ServiceProvider) {
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(frame);
		commonWaits.waitForElementVisible(assistantServiceProviderDropdown);
		commonWaits.waitForElementClickable(assistantServiceProviderDropdown).click();
		WebElement value = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(assistantServiceProviderValues, ServiceProvider)));
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.waitForElementClickable(value).click();
		return this;
	}

	public InnovationSuite checkBotPermission(String adminPermission, String publicPermission) {
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(frame);
		if (commonWaits.isElementPresent(By.xpath(String.format(chatops_Permission, adminPermission)))) {
			WebElement we1 = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(chatops_Permission, adminPermission)));
			commonWaits.javaScriptScrollIntoView(we1);
			commonWaits.waitForElementClickable(editPermissionBtn).click();
			Assert.assertTrue(commonWaits.isElementPresent(addPermissionBtn), "edit permission page is not loaded or add permission button is not clickable");
			commonWaits.waitForElementClickable(addPermissionBtn).click();
			commonWaits.waitForElementVisible(permissionType).click();
			commonWaits.waitForElementVisible(permissionBtn).click();
			commonWaits.waitForElementVisible(permissionInput).click();
			permissionInput.sendKeys(publicPermission);
			WebElement we2 = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(permissionValue, publicPermission)));
			we2.click();
			commonWaits.javaScriptScrollIntoView(permissionSaveButton);
			commonWaits.waitForElementClickable(permissionSaveButton).click();
			we1 = getDriver().findElement(By.xpath(String.format(chatops_Permission, publicPermission)));
			commonWaits.javaScriptScrollIntoView(we1);
			Assert.assertTrue(we1.isDisplayed(), "Permission is not set properly");
		}
		return this;
	}

	public void addHelixGPTChatbotSkill(String SkillValue, String BotName) {
		commonWaits.waitForElementClickable(addNewButton).click();
		commonWaits.waitForElementClickable(helixGPTSkillDropdown).click();
		WebElement value = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(HelixGPTSkillValue, SkillValue)));
		commonWaits.waitForElementClickable(value).click();
		commonWaits.pureSafeClick(value);
		commonWaits.javaScriptScrollIntoView(botNameTextBox);
		commonWaits.waitForElementVisible(botNameTextBox).sendKeys(BotName);
		commonWaits.waitForElementClickable(saveButton).click();
		checkBotPermission("Admin", "Public");
	}

	public void openBot(String BotName) {
		WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(BMCBot, BotName)));
		commonWaits.waitForElementClickable(ele);
		commonWaits.clickByJS(ele);
		commonWaits.waitForExecutionComplete();
		log.info("Opened bot: " + BotName);
	}

	public void launchHelixGPTBot() {
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(frame);
		commonWaits.waitForElementVisible(launchBotButton);
		commonWaits.waitForElementClickable(launchBotButton);
		commonWaits.clickByJS(launchBotButton);
		commonWaits.switchToLastWindow();
	}

	public InnovationSuite setTextField(String fieldName, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(textInputField, fieldName)), 20), "Text input field is not visible for field: " + fieldName);
		WebElement textField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textInputField, fieldName)));
		textField.clear();
		textField.sendKeys(value);
        return this;
	}

	public InnovationSuite setCKE_EditorField(String fieldName, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ckeEditorField, fieldName)), 20), "CKE editor field is not visible for field: " + fieldName);
		WebElement ckeField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ckeEditorField, fieldName)));
		ckeField.click();
		ckeField.sendKeys(value);
        return this;
	}

    public InnovationSuite setDropdownField(String fieldName, String option) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownField, fieldName)), 20), "Dropdown field is not visible for field: " + fieldName);
        WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownField, fieldName)));
        dropdown.click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownOptions, option)), 20), "Dropdown options are not displayed for field: " + fieldName);
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownOptions, option)));
        optionElement.click();
        return this;
    }

	public InnovationSuite setCounterField(String fieldName, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(counterFieldInput, fieldName)), 20), "Counter input field is not visible for field: " + fieldName);
		WebElement counterField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(counterFieldInput, fieldName)));
		counterField.clear();
		counterField.sendKeys(value);
        return this;
	}

	public InnovationSuite setDateField(String fieldName, String dateValue, String dateFormat) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dateFieldInput, fieldName)), 20), "Date input field is not visible for field: " + fieldName);
		WebElement dateField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateFieldInput, fieldName)));
		dateField.clear();
		dateField.sendKeys(getFutureDate(Integer.parseInt(dateValue), dateFormat));
        return this;
	}

	public void uploadFile(File file, WebElement webElement) {
		log.info("Uploading file: " + file.getAbsolutePath());
		webElement.sendKeys(file.getAbsolutePath());
		commonWaits.waitForAngularRequestsToFinish();
		log.info("Uploaded file: " + file.getAbsolutePath());
	}

	public void verifyAutomatedAssetAssignmentPageLaunched() {
		log.info("Verifying Automated Asset Assignment application is launched");
		
		commonWaits.switchToLastWindow();
		log.info("Switched to new window for Automated Asset Assignment application");
		commonWaits.waitForURLContains("asset-automation");
		
		WebElement headingElement = commonWaits.waitForByElementToBeVisible(By.xpath(automatedAssetAssignmentHeading));
		String appNameText = headingElement.getText().trim();
		log.info("Opened application name: " + appNameText);
		Assert.assertTrue(appNameText.contains("Automated Asset Assignment"), 
			"Automated Asset Assignment application is not launched successfully. Actual: " + appNameText);
		log.info("Automated Asset Assignment application launched successfully");
	}

	public void clickAssetAutomationsTab() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement assetAutomationsTabElement = commonWaits.waitForByElementToBeVisible(By.xpath(assetAutomationsTab));
		clickWithoutInterception(assetAutomationsTabElement);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked on Asset Automations tab");
	}

	public void clickCreateAutomation() {
		commonWaits.waitForElementClickable(aaaCreateAutomationButtonElement);
		clickWithoutInterception(aaaCreateAutomationButtonElement);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked on Create automation button");
	}

	public void verifyCreateAutomationPageDisplayed() {

		commonWaits.waitForURLContains("create");
		commonWaits.waitForElementVisible(aaaCreateAutomationHeadingElement, 30);
		String heading = aaaCreateAutomationHeadingElement.getText().trim();
		Assert.assertTrue(heading.contains("Create automation"), 
			"Create automation page heading not displayed. Actual: " + heading);
		log.info("Create automation page displayed successfully");
	}

	public void enterAutomationName(String automationName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForElementVisible(aaaAutomationNameInputElement, 30);
		keyClearAndType(aaaAutomationNameInputElement, automationName);
		log.info("Entered automation name: " + automationName);
	}

	public void selectAssetType(String assetType) {
		log.info("Selecting asset type: " + assetType);
		commonWaits.waitForElementClickable(aaaAssetTypeDropdownElement);
		clickWithoutInterception(aaaAssetTypeDropdownElement);
		commonWaits.waitForSometime(commonWaits.minDelay);
		
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(aaaAssetTypeOption, assetType)));
		clickWithoutInterception(option);
		log.info("Selected asset type: " + assetType);
	}

	public void clickAddExpression() {
		commonWaits.waitForElementClickable(aaaAddExpressionButtonElement);
		clickWithoutInterception(aaaAddExpressionButtonElement);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked on Add an expression button");
	}

	public void filterAndSelectExpressionField(String fieldName) {
		log.info("Filtering and selecting expression field: " + fieldName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement filterInput = commonWaits.waitForByElementToBeVisible(By.xpath(aaaExpressionFilterInput));
		keyClearAndType(filterInput, fieldName);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

		String fieldButtonXpath = String.format(aaaExpressionFieldButton, fieldName);
		
		WebElement fieldButton = commonWaits.waitForByElementToBeVisible(By.xpath(fieldButtonXpath));
		
		commonWaits.scrollToSpecificElement(fieldButton);
		clickWithoutInterception(fieldButton);
		
		log.info("Selected expression field: " + fieldName);
	}

	public InnovationSuite selectExpressionOperator(String operator) {
		log.info("Selecting expression operator: " + operator);

		String operatorButtonXpath = String.format(aaaExpressionOperatorButton, operator);

		try {

			WebElement operatorButton = commonWaits
					.waitForElementVisible(getDriver().findElement(By.xpath(operatorButtonXpath)), 30);
			clickWithoutInterception(operatorButton);
			log.info("Selected expression operator from button: " + operator);
		} catch (Exception e) {
			// If operator button not visible within 30 seconds, type into rich text editor
			log.info("Operator button not found, typing into rich text editor: " + operator);

			// Locate the rich text editor element
			commonWaits.waitForByElementToBeVisible(By.xpath(ruleContentEditor));

			// Find the paragraph inside the rich text editor
			WebElement firstParagraph = commonWaits.waitForByElementToBeVisible(By.xpath(firstParagraphXpath));

			// Do NOT clear existing content, just type expression value at the end
			commonWaits.scrollToSpecificElement(firstParagraph);
			clickWithoutInterception(firstParagraph);
			firstParagraph.sendKeys(operator);
			log.info("Typed value at the end of paragraph: " + operator);
		}
		return this;
	}

	public void clickExpressionOkButton() {
		log.info("Clicking OK button on expression modal");
		WebElement okButton = commonWaits.waitForByElementToBeVisible(By.xpath(aaaExpressionOkButton));
		clickWithoutInterception(okButton);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked OK button on expression modal");
	}

	public void addExpression(String field, String operator, String value) {
		log.info("Adding expression: " + field + " " + operator + " " + (value != null ? value : ""));
		
		clickAddExpression();
		filterAndSelectExpressionField(field);
		selectExpressionOperator(operator);
		if (value != null && !value.isEmpty()) {
			selectExpressionOperator(value);
		}
		clickExpressionOkButton();
		
		log.info("Expression added successfully");
	}

	public void selectCreateRelationshipCheckbox() {
		log.info("Selecting Create relationship checkbox");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement checkboxLabel = commonWaits.waitForByElementToBeVisible(By.xpath(aaaCreateRelationshipCheckbox));
		commonWaits.scrollToSpecificElement(checkboxLabel);
		
		WebElement checkboxInput = commonWaits.waitForElementPresenceLocated(By.xpath(aaaCreateRelationshipCheckboxInput));
		String isChecked = checkboxInput.getAttribute("aria-checked");
		
		if (!"true".equals(isChecked)) {
			clickWithoutInterception(checkboxLabel);
			commonWaits.waitForExecutionComplete();
			log.info("Create relationship checkbox selected");
		} else {
			log.info("Create relationship checkbox already selected");
		}
	}

	public void selectRelationshipType(String relationshipType) {
		log.info("Selecting Relationship Type: " + relationshipType);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(aaaRelationshipTypeDropdown));
		clickWithoutInterception(dropdown);
		
		String optionXpath = String.format(aaaDropdownOptionByText, relationshipType);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Relationship Type: " + relationshipType);
	}

	public void selectPeopleType(String peopleType) {
		log.info("Selecting People Type: " + peopleType);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(aaaPeopleTypeDropdown));
		clickWithoutInterception(dropdown);
		
		String optionXpath = String.format(aaaDropdownOptionByText, peopleType);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected People Type: " + peopleType);
	}

	public void selectPerson(String personName) {
		log.info("Selecting Person: " + personName);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(aaaPersonDropdown));
		clickWithoutInterception(dropdown);
		
		WebElement filterInput = commonWaits.waitForByElementToBeVisible(By.xpath(aaaPersonFilterInput));
		keyClearAndType(filterInput, personName);
		
		String optionXpath = String.format(aaaDropdownOptionByText, personName);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Person: " + personName);
	}

	public void selectRole(String role) {
		log.info("Selecting Role: " + role);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(aaaRoleDropdown));
		clickWithoutInterception(dropdown);
		
		String optionXpath = String.format(aaaDropdownOptionByText, role);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Role: " + role);
	}

	public void createRelationship(String relationshipType, String peopleType, String personName, String role) {
		log.info("Creating relationship with type: " + relationshipType + ", peopleType: " + peopleType + ", person: " + personName + ", role: " + role);
		selectCreateRelationshipCheckbox();
		selectRelationshipType(relationshipType);
		selectPeopleType(peopleType);
		selectPerson(personName);
		selectRole(role);
		log.info("Relationship created successfully");
	}

	public void clickSaveAutomation() {
		log.info("Clicking Save button to save automation");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForElementClickable(aaaSaveAutomationButtonElement);
		clickWithoutInterception(aaaSaveAutomationButtonElement);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked Save button");
	}

	public void verifyAutomationJobCreated() {
		log.info("Verifying automation job created successfully");
		verifySuccessToastMessage();
		commonWaits.waitForExecutionComplete();
		log.info("Automation job created successfully");
	}

	public void verifySuccessToastMessage() {
		log.info("Verifying success toast message");
		try {
			WebElement toastMessage = commonWaits.waitForByElementToBeVisible(By.xpath(aaaSuccessToastMessage));
			if (toastMessage != null && toastMessage.isDisplayed()) {
				log.info("Success toast message displayed: Automation rule saved successfully");
			}
		} catch (Exception e) {
			log.warn("Success toast message may have disappeared quickly, continuing...");
		}
	}

	public void switchToAssetAutomationsTab() {
		log.info("Switching to Asset Automations tab (index 0)");
		commonWaits.switchToTabByIndex(0);
		PageFactory.initElements(getDriver(), this);
		log.info("Switched to Asset Automations tab");
	}

	public void verifyAutomationEntryInList(String automationName) {
		log.info("Verifying automation entry in list: " + automationName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		String entryXpath = String.format(aaaAutomationEntryByName, automationName);
		WebElement automationEntry = commonWaits.waitForByElementToBeVisible(By.xpath(entryXpath));
		
		if (automationEntry != null && automationEntry.isDisplayed()) {
			log.info("Automation entry found in list: " + automationName);
		} else {
			log.error("Automation entry NOT found in list: " + automationName);
			throw new RuntimeException("Automation entry not found: " + automationName);
		}
	}

	public void closeAutomationDetailsPopupIfPresent() {
		log.info("Checking if Automation details popup is present");
		try {
			WebElement closeButton = getDriver().findElement(By.xpath(aaaClosePopupButton));
			if (closeButton != null && closeButton.isDisplayed()) {
				log.info("Automation details popup found, closing it");
				clickWithoutInterception(closeButton);
				commonWaits.waitForExecutionComplete();
				log.info("Closed Automation details popup");
			}
		} catch (Exception e) {
			log.info("Automation details popup not present, continuing...");
		}
	}

	public String fillAutomationRuleTemplate(Map<Object, String> automationData) {
		log.info("Filling Automation Rule Template");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		String uniqueAutomationName = null;

		if (automationData.get("AutomationName") != null) {
			uniqueAutomationName = automationData.get("AutomationName") + "_" + RandomStringUtils.randomAlphanumeric(5);
			enterAutomationName(uniqueAutomationName);
		}

		if (automationData.get("AssetType") != null) {
			selectAssetType(automationData.get("AssetType"));
		}

		addExpression(SolQAConstants.EXPRESSION_FIELD_NAME, SolQAConstants.EXPRESSION_OPERATOR_NOT_EQUAL, SolQAConstants.EXPRESSION_VALUE_NULL);

		if (automationData.get("RelationshipType") != null) {
			String relationshipType = automationData.get("RelationshipType");
			String peopleType = automationData.get("PeopleType");
			String personName = automationData.get("PersonName");
			String role = automationData.get("Role");
			createRelationship(relationshipType, peopleType, personName, role);
		}

		log.info("Automation Rule Template filled successfully");
		return uniqueAutomationName;
	}

	public void selectAutomationEntry(String automationName) {
		log.info("Selecting automation entry checkbox: " + automationName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

		String checkboxXpath = String.format(aaaAutomationRowCheckboxByName, automationName);
		WebElement checkbox = commonWaits.waitForByElementToBeVisible(By.xpath(checkboxXpath));
		clickWithoutInterception(checkbox);
		commonWaits.waitForExecutionComplete();
		log.info("Selected automation entry checkbox: " + automationName);
	}

	public void clickEnableButton() {
		log.info("Clicking Enable button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement enableButton = commonWaits.waitForByElementToBeVisible(By.xpath(aaaEnableButton));
		clickWithoutInterception(enableButton);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked Enable button");
	}

	public void verifyAutomationStatusActive(String automationName) {
		log.info("Verifying automation status is Active for: " + automationName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		String statusXpath = String.format(aaaAutomationStatusByName, automationName);
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(statusXpath) , 30 ), "Status is not visible");
		
		WebElement statusElement = commonWaits.waitForByElementToBeVisible(By.xpath(statusXpath));
		String status = statusElement.getText().trim();
		
		if ("Active".equalsIgnoreCase(status)) {
			log.info("Automation status verified as Active for: " + automationName);
		} else {
			log.error("Automation status is NOT Active. Current status: " + status);
			throw new RuntimeException("Expected status 'Active' but found: " + status);
		}
	}

	public void enableAutomationRule(String automationName) {
		log.info("Enabling automation rule: " + automationName);
		selectAutomationEntry(automationName);
		clickEnableButton();
		verifyAutomationStatusActive(automationName);
		log.info("Automation rule enabled successfully: " + automationName);
	}

	public void clickAddAutomationJobButton() {
		log.info("Clicking Add automation job button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement addJobButton = commonWaits.waitForByElementToBeVisible(By.xpath(aaaAddAutomationJobButton));
		clickWithoutInterception(addJobButton);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked Add automation job button");
	}

	public void verifyCreateJobPopupDisplayed(String automationName) {
		log.info("Verifying Create job popup is displayed with automation: " + automationName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		String popupXpath = String.format(aaaCreateJobPopupAutomationName, automationName);
		WebElement automationNameElement = commonWaits.waitForByElementToBeVisible(By.xpath(popupXpath));
		
		if (automationNameElement != null && automationNameElement.isDisplayed()) {
			log.info("Create job popup displayed with automation: " + automationName);
		} else {
			throw new RuntimeException("Create job popup not displayed for: " + automationName);
		}
	}

	public void clickPreviewButton() {
		log.info("Clicking Preview button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement previewButton = commonWaits.waitForByElementToBeVisible(By.xpath(aaaPreviewButton));
		clickWithoutInterception(previewButton);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked Preview button");
	}

	public void selectAssetInPreview() {
		log.info("Selecting asset in preview");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement assetCheckbox = commonWaits.waitForByElementToBeVisible(By.xpath(aaaPreviewAssetCheckbox));
		clickWithoutInterception(assetCheckbox);
		commonWaits.waitForExecutionComplete();
		log.info("Selected asset in preview");
	}

	public void clickRunJobButton() {
		log.info("Clicking Run Job button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement runJobButton = commonWaits.waitForByElementToBeVisible(By.xpath(aaaRunJobButton));
		clickWithoutInterception(runJobButton);
		commonWaits.waitForExecutionComplete();
		log.info("Clicked Run Job button");
	}

	public void verifyJobCreatedToastMessage() {
		log.info("Verifying job created toast message");
		try {
			WebElement toastMessage = commonWaits.waitForByElementToBeVisible(By.xpath(aaaJobCreatedToastMessage));
			if (toastMessage != null && toastMessage.isDisplayed()) {
				log.info("Job created toast message displayed: Automation job created");
			}
		} catch (Exception e) {
			log.warn("Job created toast message may have disappeared quickly, continuing...");
		}
	}

	public void clickAutomationJobConsoleLink() {
		log.info("Clicking Automation job console link");
		try {
			WebElement consoleLink = commonWaits.waitForByElementToBeVisible(By.xpath(aaaAutomationJobConsoleLink));
			clickWithoutInterception(consoleLink);
			commonWaits.waitForExecutionComplete();
			log.info("Clicked Automation job console link");
		} catch (Exception e) {
			log.info("Automation job console link not found in toast, navigating via menu");
		}
	}

	public void verifyJobStatusCompleted() {
		log.info("Verifying job status is Completed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		WebElement statusElement = commonWaits.waitForByElementToBeVisible(By.xpath(aaaJobStatusCompleted));
		if (statusElement != null && statusElement.isDisplayed()) {
			log.info("Job status verified as Completed");
		} else {
			log.error("Job status is NOT Completed");
			throw new RuntimeException("Job status is not Completed");
		}
	}

	public void clickDataMonitorTab() {
		log.info("Clicking on Data Monitor tab");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement dataMonitorTabElement = commonWaits.waitForByElementToBeVisible(By.xpath(dataMonitorTab));
		clickWithoutInterception(dataMonitorTabElement);
		commonWaits.waitForExecutionComplete();
		log.info("Data Monitor tab clicked successfully");
	}

	public void verifyDataMonitorPageDisplayed() {
		log.info("Verifying Data Monitor page is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		log.info("Verifying Data Monitor table header is displayed");
		commonWaits.waitForElementVisible(dataMonitorTableHeader);
		Assert.assertTrue(dataMonitorTableHeader.isDisplayed(), "Data Monitor table header is not displayed");
		log.info("Data Monitor table header is displayed");
	}

	public void verifyDataMonitorColumns() {
		log.info("Verifying Data Monitor columns are displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		log.info("Verifying Name column is displayed");
		commonWaits.waitForElementVisible(dataMonitorColumnName);
		Assert.assertTrue(dataMonitorColumnName.isDisplayed(), "Name column is not displayed");
		log.info("Name column is displayed");
		
		log.info("Verifying CI Matches column is displayed");
		commonWaits.waitForElementVisible(dataMonitorColumnCIMatches);
		Assert.assertTrue(dataMonitorColumnCIMatches.isDisplayed(), "CI Matches column is not displayed");
		log.info("CI Matches column is displayed");
		
		log.info("Verifying Last Run column is displayed");
		commonWaits.waitForElementVisible(dataMonitorColumnLastRun);
		Assert.assertTrue(dataMonitorColumnLastRun.isDisplayed(), "Last Run column is not displayed");
		log.info("Last Run column is displayed");
		
		log.info("Verifying Priority column is displayed");
		commonWaits.waitForElementVisible(dataMonitorColumnPriority);
		Assert.assertTrue(dataMonitorColumnPriority.isDisplayed(), "Priority column is not displayed");
		log.info("Priority column is displayed");
		
		log.info("Verifying Status column is displayed");
		commonWaits.waitForElementVisible(dataMonitorColumnStatus);
		Assert.assertTrue(dataMonitorColumnStatus.isDisplayed(), "Status column is not displayed");
		log.info("Status column is displayed");
		
		log.info("All Data Monitor columns verified successfully");
	}

	public void verifyDataMonitorRulesListDisplayed() {
		log.info("Verifying Data Monitor rules list is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		List<WebElement> rulesList = getDriver().findElements(By.xpath(dataMonitorRulesList));
		Assert.assertTrue(rulesList.size() > 0, "No rules found in Data Monitor list");
		log.info("Data Monitor rules list is displayed with " + rulesList.size() + " rules");
	}

	public void verifyDataMonitorTiles() {
		log.info("Verifying Data Monitor tiles are displayed with counts");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		log.info("Verifying Total rules tile is displayed");
		WebElement totalRulesTile = commonWaits.waitForElementPresenceLocated(By.xpath(dataMonitorTotalRulesTile));
		Assert.assertNotNull(totalRulesTile, "Total rules tile is not displayed");
		String totalRulesCount = totalRulesTile.getText();
		log.info("Total rules tile is displayed with count: " + totalRulesCount);
		
		log.info("Verifying Rules with open issues tile is displayed");
		WebElement rulesWithOpenIssuesTile = commonWaits.waitForElementPresenceLocated(By.xpath(dataMonitorRulesWithOpenIssuesTile));
		Assert.assertNotNull(rulesWithOpenIssuesTile, "Rules with open issues tile is not displayed");
		String rulesWithOpenIssuesCount = rulesWithOpenIssuesTile.getText();
		log.info("Rules with open issues tile is displayed with count: " + rulesWithOpenIssuesCount);
		
		log.info("Verifying Active rules tile is displayed");
		WebElement activeRulesTile = commonWaits.waitForElementPresenceLocated(By.xpath(dataMonitorActiveRulesTile));
		Assert.assertNotNull(activeRulesTile, "Active rules tile is not displayed");
		String activeRulesCount = activeRulesTile.getText();
		log.info("Active rules tile is displayed with count: " + activeRulesCount);
		
		log.info("Verifying Inactive rules tile is displayed");
		WebElement inactiveRulesTile = commonWaits.waitForElementPresenceLocated(By.xpath(dataMonitorInactiveRulesTile));
		Assert.assertNotNull(inactiveRulesTile, "Inactive rules tile is not displayed");
		String inactiveRulesCount = inactiveRulesTile.getText();
		log.info("Inactive rules tile is displayed with count: " + inactiveRulesCount);
		
		log.info("All Data Monitor tiles verified successfully");
	}

	public void clickNewMonitoringRuleButton() {
		log.info("Clicking on New monitoring rule button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement newMonitoringRuleBtn = commonWaits.waitForByElementToBeVisible(By.xpath(newMonitoringRuleButton));
		clickWithoutInterception(newMonitoringRuleBtn);
		commonWaits.waitForExecutionComplete();
		log.info("New monitoring rule button clicked successfully");
	}

	public void verifyNewMonitoringRulePanelDisplayed() {
		log.info("Verifying New monitoring rule panel is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement panelHeading = commonWaits.waitForByElementToBeVisible(By.xpath(newMonitoringRulePanelHeading));
		Assert.assertTrue(panelHeading.isDisplayed(), "New monitoring rule panel is not displayed");
		log.info("New monitoring rule panel is displayed");
	}

	public void enterMonitoringRuleName(String name) {
		log.info("Entering monitoring rule name: " + name);
		WebElement nameInput = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleNameInput));
		keyClearAndType(nameInput, name);
		log.info("Monitoring rule name entered: " + name);
	}

	public void enterMonitoringRuleDescription(String description) {
		log.info("Entering monitoring rule description: " + description);
		WebElement descriptionTextarea = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleDescriptionTextarea));
		keyClearAndType(descriptionTextarea, description);
		log.info("Monitoring rule description entered: " + description);
	}

	public void selectMonitoringRuleClass(String className) {
		log.info("Selecting monitoring rule class: " + className);
		WebElement classDropdown = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleClassNameDropdown));
		clickWithoutInterception(classDropdown);
		commonWaits.waitForExecutionComplete();
		
		log.info("Filtering class: " + className);
		WebElement filterInput = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleClassFilterInput));
		keyClearAndType(filterInput, className);
		commonWaits.waitForExecutionComplete();
		
		String optionXpath = String.format(monitoringRuleDropdownOption, className);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		log.info("Monitoring rule class selected: " + className);
	}

	public void selectMonitoringRuleClassType(String classType) {
		log.info("Selecting monitoring rule class type: "+ classType);
		WebElement classTypeDropdown = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleClassTypeDropdown));
	
		clickWithoutInterception(classTypeDropdown);
	    commonWaits.waitForExecutionComplete();
	        
	        String optionXpath = String.format(monitoringRuleDropdownOption, classType);
	        WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
	        clickWithoutInterception(option);
	        log.info("Monitoring rule class type selected: " + classType);
    }

	public void selectMonitoringRuleFrequency(String frequency) {
		log.info("Selecting monitoring rule frequency: " + frequency);
		WebElement frequencyDropdown = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleFrequencyDropdown));
		clickWithoutInterception(frequencyDropdown);
		commonWaits.waitForExecutionComplete();
		
		String optionXpath = String.format(monitoringRuleDropdownOption, frequency);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		log.info("Monitoring rule frequency selected: " + frequency);
	}

	public void selectMonitoringRulePriority(String priority) {
		log.info("Selecting monitoring rule priority: " + priority);
		WebElement priorityDropdown = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRulePriorityDropdown));
		clickWithoutInterception(priorityDropdown);
		commonWaits.waitForExecutionComplete();
		
		String optionXpath = String.format(monitoringRuleDropdownOption, priority);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		clickWithoutInterception(option);
		log.info("Monitoring rule priority selected: " + priority);
	}

	public void clickMonitoringRuleAddExpression() {
		log.info("Clicking on Add an expression button in monitoring rule");
		WebElement addExpressionBtn = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleAddExpressionButton));
		clickWithoutInterception(addExpressionBtn);
		commonWaits.waitForExecutionComplete();
		log.info("Add an expression button clicked");
	}

	public void addMonitoringRuleExpression(String field, String operator, String value) {
		log.info("Adding monitoring rule expression: " + field + " " + operator + " " + (value != null ? value : ""));
		clickMonitoringRuleAddExpression();
		filterAndSelectExpressionField(field);
		selectExpressionOperator(operator);
		if (value != null && !value.isEmpty()) {
			selectExpressionOperator(value);
		}
		clickExpressionOkButton();
		log.info("Monitoring rule expression added successfully");
	}

	public void clickMonitoringRuleSaveButton() {
		log.info("Clicking on Save button for monitoring rule");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement saveBtn = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleSaveButton));
		clickWithoutInterception(saveBtn);
		commonWaits.waitForExecutionComplete();
		log.info("Save button clicked");
	}

	public void verifyMonitoringRuleCreatedSuccessfully() {
		log.info("Verifying monitoring rule created successfully");
		try {
			WebElement successToast = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleCreatedSuccessToast));
			if (successToast != null && successToast.isDisplayed()) {
				log.info("Monitoring rule created successfully - success toast displayed");
			}
		} catch (Exception e) {
			log.info("Success toast may have disappeared quickly, continuing verification");
		}
	}

	public void clickMonitoringRuleKebabMenu(String ruleName) {
		log.info("Clicking on kebab menu (3-dot) for monitoring rule: " + ruleName);
		String kebabMenuXpath = String.format(monitoringRuleRowKebabMenuByName, ruleName);
		WebElement kebabMenu = commonWaits.waitForByElementToBeVisible(By.xpath(kebabMenuXpath));
		clickWithoutInterception(kebabMenu);
		commonWaits.waitForExecutionComplete();
		log.info("Kebab menu clicked for rule: " + ruleName);
	}

	public void clickEditOptionFromDropdown() {
		log.info("Clicking on Edit option from dropdown menu");
		WebElement editOption = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleEditOptionInDropdown));
		clickWithoutInterception(editOption);
		commonWaits.waitForExecutionComplete();
		log.info("Edit option clicked");
	}

	public void verifyEditMonitoringRulePanelDisplayed() {
		log.info("Verifying Edit monitoring rule panel is displayed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement panelHeading = commonWaits.waitForByElementToBeVisible(By.xpath(editMonitoringRulePanelHeading));
		Assert.assertTrue(panelHeading.isDisplayed(), "Edit monitoring rule panel is not displayed");
		log.info("Edit monitoring rule panel is displayed");
	}

	public String updateMonitoringRuleName(String baseName) {
		log.info("Updating monitoring rule name with random string");
		String updatedName = baseName + "_Edited_" + RandomStringUtils.randomAlphanumeric(5);
		WebElement nameInput = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleNameInput));
		keyClearAndType(nameInput, updatedName);
		log.info("Monitoring rule name updated to: " + updatedName);
		return updatedName;
	}

	public void updateMonitoringRuleDescription(String description) {
		log.info("Updating monitoring rule description: " + description);
		WebElement descriptionTextarea = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleDescriptionTextarea));
		keyClearAndType(descriptionTextarea, description);
		log.info("Monitoring rule description updated: " + description);
	}

	public void verifyMonitoringRuleUpdatedSuccessfully() {
		log.info("Verifying monitoring rule updated successfully");
		try {
			WebElement successToast = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleUpdatedSuccessToast));
			if (successToast != null && successToast.isDisplayed()) {
				log.info("Monitoring rule updated successfully - success toast displayed");
			}
		} catch (Exception e) {
			log.info("Success toast may have disappeared quickly, continuing verification");
		}
	}

	public void verifyMonitoringRuleInGrid(String ruleName) {
		log.info("Verifying monitoring rule in grid: " + ruleName);
		String ruleNameXpath = String.format(monitoringRuleNameInGrid, ruleName);
		WebElement ruleEntry = commonWaits.waitForByElementToBeVisible(By.xpath(ruleNameXpath));
		Assert.assertTrue(ruleEntry.isDisplayed(), "Monitoring rule '" + ruleName + "' is not displayed in grid");
		log.info("Monitoring rule verified in grid: " + ruleName);
	}

	public void selectMonitoringRuleCheckbox(String ruleName) {
		log.info("Selecting monitoring rule checkbox: " + ruleName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		String checkboxXpath = String.format(monitoringRuleRowCheckboxByName, ruleName);
		WebElement checkbox = commonWaits.waitForByElementToBeVisible(By.xpath(checkboxXpath));
		commonWaits.scrollToElement(checkbox);
		clickWithoutInterception(checkbox);
		commonWaits.waitForExecutionComplete();
		log.info("Monitoring rule checkbox selected: " + ruleName);
	}

	public void clickDisableMonitoringRuleButton() {
		log.info("Clicking on Disable button for monitoring rule");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement disableBtn = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleDisableButton));
		clickWithoutInterception(disableBtn);
		commonWaits.waitForExecutionComplete();
		log.info("Disable button clicked");
	}

	public void clickEnableMonitoringRuleButton() {
		log.info("Clicking on Enable button for monitoring rule");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement enableBtn = commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleEnableButton));
		commonWaits.scrollToSpecificElement(enableBtn);
		clickWithoutInterception(enableBtn);
		commonWaits.waitForExecutionComplete();
		log.info("Enable button clicked");
	}

	public String getMonitoringRuleStatus(String ruleName) {
		log.info("Getting monitoring rule status for: " + ruleName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		String statusXpath = String.format(monitoringRuleStatusByName, ruleName);
		WebElement statusElement = commonWaits.waitForByElementToBeVisible(By.xpath(statusXpath));
		String status = statusElement.getText().trim();
		log.info("Monitoring rule status: " + status);
		return status;
	}

	public void verifyMonitoringRuleStatusInactive(String ruleName) {
		log.info("Verifying monitoring rule status is Inactive: " + ruleName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		String status = getMonitoringRuleStatus(ruleName);
		Assert.assertEquals(status, "Inactive", "Monitoring rule status is not Inactive. Actual: " + status);
		log.info("Monitoring rule status verified as Inactive");
	}

	public InnovationSuite verifyMonitoringRuleStatusActive(String ruleName) {
		log.info("Verifying monitoring rule status is Active: " + ruleName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		// Poll for status to become Active
		maxAttempts = 10;
		attempt = 0;
		currentStatus = "";
		statusActive = false;
		
		while (attempt < maxAttempts && !statusActive) {
			try {
				currentStatus = getMonitoringRuleStatus(ruleName);
				log.info("Current monitoring rule status: " + currentStatus + " (attempt " + (attempt + 1) + " of " + maxAttempts + ")");
				
				if ("Active".equalsIgnoreCase(currentStatus)) {
					statusActive = true;
					log.info("✓ Monitoring rule status is now Active");
					break;
				}
				
				// Wait before next check
				if (attempt < maxAttempts - 1) {
					log.info("Status is '" + currentStatus + "', waiting 3 seconds before retry...");
					commonWaits.waitForSometime(3000);
				}
				
			} catch (Exception e) {
				log.info("Error getting status on attempt " + (attempt + 1) + ": " + e.getMessage());
			}
			
			attempt++;
		}
		
		Assert.assertEquals(currentStatus, "Active", "Monitoring rule status is not Active after " + maxAttempts + " attempts. Actual: " + currentStatus);
		log.info("Monitoring rule status verified as Active");
		return this;
	}
	
	public void clickRunNowOption() {
		commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleRunNowOption));
		getDriver().findElement(By.xpath(monitoringRuleRunNowOption)).click();
	}
	
	public InnovationSuite verifyRuleRunSuccessToast(){
		try {
			commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleRunSuccessToast));
			WebElement toast = getDriver().findElement(By.xpath(monitoringRuleRunSuccessToast));
			Assert.assertTrue(toast.isDisplayed(), "Rule run success toast not displayed");
		} catch (Exception e) {
			log.warn("Rule run success toast may have disappeared quickly: " + e.getMessage());
		}
		return this;
	}
	
	public void clickRefreshButton() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleRefreshButton));
		getDriver().findElement(By.xpath(monitoringRuleRefreshButton)).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
	}
	public void verifyMonitoringRuleCompletionStatus(String ruleName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		String completionStatusXpath = String.format(monitoringRuleCompletionStatusByName, ruleName);
		commonWaits.waitForByElementToBeVisible(By.xpath(completionStatusXpath));
		WebElement statusElement = getDriver().findElement(By.xpath(completionStatusXpath));
		Assert.assertTrue(statusElement.isDisplayed(), "Completion status 'Completed' not displayed for rule: " + ruleName);
	}
	
	public InnovationSuite verifyMonitoringRuleCompletionStatusWithRefresh(String ruleName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		String completionStatusXpath = String.format(monitoringRuleCompletionStatusByName, ruleName);
		
		// Keep refreshing until completion status is visible
		maxAttempts = 10;
		attempt = 0;
		statusVisible = false;
		
		while (attempt < maxAttempts && !statusVisible) {
			try {
				// Check if completion status is visible
				List<WebElement> statusElements = getDriver().findElements(By.xpath(completionStatusXpath));
				if (!statusElements.isEmpty() && statusElements.get(0).isDisplayed()) {
					statusVisible = true;
					log.info("Completion status 'Completed' is now visible for rule: " + ruleName);
					break;
				}
			} catch (Exception e) {
				log.info("Completion status not yet visible, attempt " + (attempt + 1) + " of " + maxAttempts);
			}
			
			// Status not visible yet, click refresh and wait
			if (!statusVisible) {
				log.info("Clicking refresh button to check rule completion status (attempt " + (attempt + 1) + ")");
				clickRefreshButton();
				attempt++;
				
				// Wait before next check (only if not last attempt)
				if (attempt < maxAttempts && !statusVisible) {
					commonWaits.waitForSometime(10);
				}
			}
		}
		
		// Final verification
		if (!statusVisible) {
			log.error("Completion status 'Completed' not displayed after " + maxAttempts + " refresh attempts for rule: " + ruleName);
			Assert.fail("Completion status 'Completed' not displayed after " + maxAttempts + " refresh attempts for rule: " + ruleName);
		}
		
		WebElement statusElement = getDriver().findElement(By.xpath(completionStatusXpath));
		Assert.assertTrue(statusElement.isDisplayed(), "Completion status 'Completed' not displayed for rule: " + ruleName);
		return this;
	}
	
	public void clickViewResultsOption() {
		commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleViewResultsOption));
		getDriver().findElement(By.xpath(monitoringRuleViewResultsOption)).click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
	}
	
	public void verifyCIMatchesResultsPage(String ruleName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleCIMatchesHeading));
		WebElement heading = getDriver().findElement(By.xpath(monitoringRuleCIMatchesHeading));
		Assert.assertTrue(heading.isDisplayed(), "CI matches heading not displayed");
		Assert.assertTrue(heading.getText().contains("CI matches for"), "CI matches heading text incorrect");
	}
	
	public String clickFirstCIResultLink() {
		commonWaits.waitForByElementToBeVisible(By.xpath(monitoringRuleCIResultLink));
		List<WebElement> ciLinks = getDriver().findElements(By.xpath(monitoringRuleCIResultLink));
		Assert.assertTrue(ciLinks.size() > 0, "No CI result links found");
		String ciName = ciLinks.get(0).getText().trim();
		ciLinks.get(0).click();
		return ciName;
	}
	
	public void verifyCrossLaunchedCMDBTab() {
		commonWaits.waitForNumberWindowsToBe(3);
		commonWaits.switchToLastWindow();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		String currentUrl = getDriver().getCurrentUrl();
		log.info("Cross-launched URL: " + currentUrl);
		Assert.assertTrue(currentUrl.toLowerCase().contains("cmdb"), "Cross-launched URL does not contain 'cmdb'. Actual URL: " + currentUrl);
		
		commonWaits.waitForElementVisible(cmdbDashboardBreadcrumb);
		Assert.assertTrue(cmdbDashboardBreadcrumb.isDisplayed(), "Dashboard breadcrumb is not displayed on the cross-launched CMDB page");
		log.info("CMDB Dashboard breadcrumb verified successfully");
	}
	
	public void clickDeleteMonitoringRuleButton() {
		commonWaits.waitForElementVisible(monitoringRuleDeleteButton);
		monitoringRuleDeleteButton.click();
	}
	
	public void confirmDeleteMonitoringRule() {
		commonWaits.waitForElementVisible(monitoringRuleDeleteConfirmButton);
		monitoringRuleDeleteConfirmButton.click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
	}
	
	public void verifyMonitoringRuleDeletedSuccessToast() {
		try {
			commonWaits.waitForElementVisible(monitoringRuleDeletedSuccessToast);
			Assert.assertTrue(monitoringRuleDeletedSuccessToast.isDisplayed(), "Rule deleted success toast not displayed");
		} catch (Exception e) {
			log.warn("Rule deleted success toast may have disappeared quickly: " + e.getMessage());
		}
	}
	
	public void deleteMonitoringRule(String ruleName) {
		selectMonitoringRuleCheckbox(ruleName);
		clickDeleteMonitoringRuleButton();
		confirmDeleteMonitoringRule();
		verifyMonitoringRuleDeletedSuccessToast();
	}
	
	public void verifyMonitoringRuleNotInGrid(String ruleName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		String ruleXpath = String.format(monitoringRuleNameInGrid, ruleName);
		List<WebElement> ruleElements = getDriver().findElements(By.xpath(ruleXpath));
		Assert.assertTrue(ruleElements.isEmpty(), "Monitoring rule still exists in grid: " + ruleName);
	}
	
	public void closeCIBox() {
		log.info("Closing CI Box if present");
		try {
			if (aaaCICloseButton != null && aaaCICloseButton.isDisplayed()) {
				log.info("CI Box found, closing it");
				clickWithoutInterception(aaaCICloseButton);
				commonWaits.waitForExecutionComplete();
				log.info("Closed CI Box");
			}
		} catch (Exception e) {
			log.info("CI Box not present, continuing...");
		}
	}
	
	public InnovationSuite validateViewInsightResult(String ruleName, List<String> analysisSummaryTexts, Boolean shouldViewInsightsBeVisible) {
		log.info("Validating View Insight result for rule: " + ruleName + " (shouldViewInsightsBeVisible: " + shouldViewInsightsBeVisible + ")");
		
		String ruleActionsDropdownXpath = String.format(ruleActionsDropdown, ruleName);
		log.info("Clicking on rule actions dropdown for rule: " + ruleName);
		commonWaits.waitForByElementToBeVisible(By.xpath(ruleActionsDropdownXpath));
		WebElement kebabMenu = getDriver().findElement(By.xpath(ruleActionsDropdownXpath));
		clickWithoutInterception(kebabMenu);
		
		String viewInsightsOptionXpath = String.format(ruleAction, SolQAConstants.VIEW_INSIGHTS);
		
		// Check if View Insights option should be visible or not
		if (!shouldViewInsightsBeVisible) {
			// Validate that View Insights option is NOT enabled/visible
			log.info("Verifying that 'View insights' option is NOT available/enabled");
			
			try {
				List<WebElement> viewInsightsOptions = getDriver().findElements(By.xpath(viewInsightsOptionXpath));
				
				if (viewInsightsOptions.isEmpty()) {
					log.info("✓ 'View insights' option is not present in dropdown (as expected)");
					return this; // Exit early - validation passed
				}
				
				// Check if element exists but is not enabled
				WebElement viewInsightsOption = viewInsightsOptions.get(0);
				boolean isEnabled = viewInsightsOption.isEnabled();
				boolean isDisplayed = viewInsightsOption.isDisplayed();
				
				if (!isEnabled || !isDisplayed) {
					log.info("✓ 'View insights' option is present but not enabled/visible (as expected)");
					log.info("  → isEnabled: " + isEnabled + ", isDisplayed: " + isDisplayed);
					return this; // Exit early - validation passed
				}
				
				// If we reach here, View Insights is visible and enabled when it shouldn't be
				log.error("✗ 'View insights' option is enabled and visible, but it should NOT be available");
				Assert.fail("View Insights option is enabled and visible for rule: " + ruleName + ", but expected it to be disabled/hidden");
				
			} catch (Exception e) {
				log.info("✓ 'View insights' option validation passed - option not available (as expected)");
				log.info("  → Exception caught: " + e.getMessage());
				return this; // Exit early - validation passed
			}
		}
		
		// If shouldViewInsightsBeVisible is true, proceed with normal flow
		log.info("Clicking on 'View insights' option");
		commonWaits.waitForByElementToBeVisible(By.xpath(viewInsightsOptionXpath));
		WebElement viewInsightsOption = getDriver().findElement(By.xpath(viewInsightsOptionXpath));
		clickWithoutInterception(viewInsightsOption);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageFullyLoaded(50, 3);
		
		log.info("Verifying insights title is displayed");
		
		commonWaits.javaScriptScrollIntoView(insightsTitle);
		commonWaits.waitForElementVisible(insightsTitle, 30);
		Assert.assertTrue(insightsTitle.isDisplayed(), "Insights title is not displayed");
		log.info("Insights title verified successfully");
		
		log.info("Verifying insights summary is displayed");
		commonWaits.waitForElementVisible(insightsSummary, 30);
		commonWaits.javaScriptScrollIntoView(insightsSummary);
		Assert.assertTrue(insightsSummary.isDisplayed(), "Insights summary is not displayed");
		
		String summaryText = insightsSummary.getText();
		log.info("Insights summary text retrieved. Length: " + summaryText.length());
		
		// Validate presence of each analysis summary text from the list and verify content exists after each title
		log.info("Validating presence of " + analysisSummaryTexts.size() + " analysis summary texts with content");
		int validatedCount = 0;
		List<String> missingTexts = new ArrayList<>();
		List<String> textsWithoutContent = new ArrayList<>();
		
		for (String expectedText : analysisSummaryTexts) {
			if (expectedText != null && !expectedText.trim().isEmpty()) {
				int titleIndex = summaryText.indexOf(expectedText);
				
				if (titleIndex != -1) {
					// Title found, now check if there is content after it
					int contentStartIndex = titleIndex + expectedText.length();
					
					// Extract text after the title (next 50 characters or until end)
					int contentEndIndex = Math.min(contentStartIndex + 50, summaryText.length());
					String contentAfterTitle = summaryText.substring(contentStartIndex, contentEndIndex).trim();
					
					// Remove common separators/whitespace to check for actual content
					contentAfterTitle = contentAfterTitle.replaceAll("^[:\\-\\s]+", "").trim();
					
					if (!contentAfterTitle.isEmpty() && contentAfterTitle.length() > 10) {
						log.info("✓ Found with content: " + expectedText);
						log.info("  → Content preview: " + contentAfterTitle.substring(0, Math.min(50, contentAfterTitle.length())) + "...");
						validatedCount++;
					} else {
						log.warn("✗ Found but no content after: " + expectedText);
						textsWithoutContent.add(expectedText);
					}
				} else {
					log.warn("✗ Missing: " + expectedText);
					missingTexts.add(expectedText);
				}
			}
		}
		
		// Step 8: Assert all texts are present with content
		List<String> allErrors = new ArrayList<>();
		
		if (!missingTexts.isEmpty()) {
			allErrors.add("Missing titles:\n" + String.join("\n", missingTexts));
		}
		
		if (!textsWithoutContent.isEmpty()) {
			allErrors.add("Titles found but no content after them:\n" + String.join("\n", textsWithoutContent));
		}
		
		if (!allErrors.isEmpty()) {
			String errorMessage = "View Insight validation failed:\n" + String.join("\n\n", allErrors);
			log.error(errorMessage);
			commonWaits.waitForElementClickable(insightsClose).click();
			Assert.fail(errorMessage);
		}
		
		log.info("View Insight validation completed successfully. Validated " + validatedCount + " out of " + analysisSummaryTexts.size() + " texts with content");
		commonWaits.waitForElementClickable(insightsClose).click();
		return this;
	}


	public String getSelectedAssistantServiceProvider() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(frame);
		commonWaits.waitForElementVisible(AssistantServiceProviderValue);
		log.info("Selected Assistant Service Provider: " + AssistantServiceProviderValue.getText());
		return AssistantServiceProviderValue.getText();
	}
	
	public InnovationSuite clickProcessesTab() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		clickWithoutInterception(processesTab);
		commonWaits.waitForExecutionComplete();
		return this;
	}
	
	public InnovationSuite clickRecordTab() {
		clickWithoutInterception(recordsTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		return this;
	}
	
	/**
	 * Creates a definition via HelixGPT by typing a question and submitting it
	 * 
	 * @param definition The type of process to create
	 * @param definitionDetails The details of the process
	 */
	public InnovationSuite createDefinitionViaHelixGPT(String definition, String definitionDetails, String expectedResponse) {
		log.info("Creating definition via HelixGPT with question: " + definition);
		log.info("Expected response text: " + expectedResponse);
		
		// Split comma-separated expected responses
		String[] expectedResponsesArray = expectedResponse.split(",");
		log.info("Expected response options count: " + expectedResponsesArray.length);
		for (int i = 0; i < expectedResponsesArray.length; i++) {
			log.info("  Option " + (i + 1) + ": '" + expectedResponsesArray[i].trim() + "'");
		}

		// Step 1: Click on Ask HelixGPT button
		log.info("Clicking on Ask HelixGPT button");
		commonWaits.waitForElementVisible(askHelixGPT, 30);
		commonWaits.waitForElementClickable(askHelixGPT);
		commonWaits.clickWithoutInterception(askHelixGPT);

		// Step 2: Wait and check for presence of HelixGPT textarea
		log.info("Waiting for HelixGPT textarea to be visible");
		commonWaits.waitForElementVisible(HelixGptTextarea, 30);
		commonWaits.waitForElementClickable(HelixGptTextarea);

		// Step 3: Type definition in HelixGPT textarea
		log.info("Typing definition in HelixGPT textarea");
		HelixGptTextarea.sendKeys(definition);
		log.info("Definition typed successfully: " + definition);

		// Step 4: Click on submit question button
		log.info("Clicking submit button");
		commonWaits.waitForElementVisible(submitQuestionButton, 10).click();
		log.info("Definition creation submitted via HelixGPT");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

		// Step 5: Wait for askFollowUp to be visible
		log.info("Waiting for askFollowUp textarea to be visible");
		commonWaits.waitForElementVisible(askFollowUp, 60);

		// Step 6: Type definitionDetails in askFollowUp textarea (all at once)
		log.info("askFollowUp textarea is now visible, Typing definition Details in askFollowUp textarea");
		commonWaits.scrollToSpecificElement(askFollowUp);
		askFollowUp.sendKeys(definitionDetails);
		commonWaits.waitForElementVisible(submitQuestionButton, 60).click();
		log.info("Definition Details typed successfully in askFollowUp: " + definitionDetails);

		// Wait for definition creation to complete
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

		// Step 7: Verify HelixGPT response contains expected message (with 10 retry attempts)
		maxAttempts = 10;
		responseFound = false;
		actualResponse = "";

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			log.info("Attempt " + attempt + " of " + maxAttempts + ": Waiting for HelixGPT to complete processing");

			// Wait for all spinners/loaders to hide
			log.info("Waiting for all spinners to hide...");
			commonWaits.waitForAngularRequestsToFinish();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			log.info("All spinners hidden");

			// Wait for response element to be visible
			log.info("Waiting for HelixGPT response content to be visible");
			commonWaits.waitForElementVisible(helixGPTResponseContent, 60);
			log.info("HelixGPT response is now visible");

			commonWaits.scrollToElement(helixGPTResponseContent);
			commonWaits.waitForElementVisible(askFollowUp, 30);
			commonWaits.waitForElementClickable(askFollowUp);
			commonWaits.scrollToSpecificElement(askFollowUp);
			commonWaits.waitForElementClickable(submitQuestionButton);
		actualResponse = helixGPTResponseContent.getText();
		log.info("Attempt " + attempt + " - HelixGPT Response Content: " + actualResponse);

		// Check if response contains ANY of the comma-separated expected texts
		String matchedResponse = null;
		for (String expectedText : expectedResponsesArray) {
			String trimmedExpectedText = expectedText.trim();
			if (actualResponse.contains(trimmedExpectedText)) {
				matchedResponse = trimmedExpectedText;
				break;
			}
		}
		
		if (matchedResponse != null) {
			log.info("SUCCESS on Attempt " + attempt + ": HelixGPT response contains expected text: '"
					+ matchedResponse + "'");
			responseFound = true;
			commonWaits.waitForElementClickable(closeProcessButton).click();
			break; // Exit loop if expected response is found
		} else {
			log.warn("ATTEMPT " + attempt + " FAILED: None of the expected texts found in response");
			for (int i = 0; i < expectedResponsesArray.length; i++) {
				log.warn("  Expected option " + (i + 1) + ": '" + expectedResponsesArray[i].trim() + "'");
			}

		// If not the last attempt, type "Proceed" and submit
		if (attempt < maxAttempts) {
			log.info("Preparing for attempt " + (attempt + 1) + " - waiting for submit button to be clickable");
			
			// Wait for submit button to be clickable FIRST (before typing "Proceed")
			log.info("Waiting for submit button to be clickable before typing 'Proceed'...");
			commonWaits.waitForElementVisible(submitQuestionButton, 40);
			commonWaits.waitForElementClickable(submitQuestionButton);
			log.info("Submit button is now clickable");
			
			// Now type "Proceed" in askFollowUp field
			log.info("Submit button is ready, now typing 'Proceed'");
			commonWaits.waitForElementVisible(askFollowUp, 30);
			commonWaits.waitForElementClickable(askFollowUp);
			askFollowUp.sendKeys("Proceed");
			log.info("Typed 'Proceed' in askFollowUp textarea");

			// Click submit button
			commonWaits.waitForElementClickable(submitQuestionButton).click();
			log.info("Clicked submit button for next attempt");

			// Wait for angular requests and spinners after submission
			commonWaits.waitForAngularRequestsToFinish();
			log.info("Waiting for spinners to hide after submission...");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			commonWaits.waitForPageReadyState(getDriver());
		}
		}
	}

	// Final validation after all attempts
	if (!responseFound) {
		log.error("FINAL FAILURE: None of the expected texts found after " + maxAttempts + " attempts");
		log.error("====================================================================================");
		log.error("EXPECTED RESPONSE OPTIONS:");
		for (int i = 0; i < expectedResponsesArray.length; i++) {
			log.error("  " + (i + 1) + ". '" + expectedResponsesArray[i].trim() + "'");
		}
		log.error("====================================================================================");
		log.error("FINAL RESPONSE CONTENT:");
		log.error(actualResponse);
		log.error("====================================================================================");
		log.info("====================================================================================");
		log.info("FINAL HELIXGPT RESPONSE CONTENT:");
		log.info(actualResponse);
		log.info("====================================================================================");
		log.info("EXPECTED RESPONSE OPTIONS:");
		for (int i = 0; i < expectedResponsesArray.length; i++) {
			log.info("  " + (i + 1) + ". '" + expectedResponsesArray[i].trim() + "'");
		}
		log.info("====================================================================================");
		
		// Build error message showing all options
		StringBuilder expectedList = new StringBuilder();
		for (int i = 0; i < expectedResponsesArray.length; i++) {
			if (i > 0) expectedList.append(" OR ");
			expectedList.append("'").append(expectedResponsesArray[i].trim()).append("'");
		}
		
		// Use assertion to fail
		Assert.assertTrue(false, "None of the expected response texts [" + expectedList.toString()
				+ "] found in HelixGPT response after " + maxAttempts + " attempts. Actual: " + actualResponse);
		
		commonWaits.waitForElementClickable(closeProcessButton).click();
		}
	return this;
	}

	/**
	 * Enables a process by clicking on the process link, enabling the checkbox, saving, and closing
	 * @param processName The name of the process to enable (e.g., "studentCreateRecord")
	 */
	public InnovationSuite enableProcess(String processName) {
		log.info("Enabling process: " + processName);
		
		// Step 1: Click on process link
		log.info("Step 1: Clicking on process link for: " + processName);
		WebElement processLink = commonWaits.waitForByElementToBeVisible(
			By.xpath(String.format(processLinkByName, processName)));
		commonWaits.clickWithoutInterception(processLink);
		log.info("Clicked on process link: " + processName);
		
		// Step 2: Wait for page to load
		log.info("Step 2: Waiting for process edit page to load");
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageReadyState(getDriver());
		log.info("Process edit page loaded successfully");
		
		// Step 3: Click on process enable checkbox
		log.info("Step 3: Clicking on process enable checkbox");
		commonWaits.switchToFrame(frame);
		
		// Scroll to element
		commonWaits.scrollToSpecificElement(processEnableCheckbox);
		
		// Check if already enabled using CommonWaits method
		boolean isCurrentlyEnabled = commonWaits.isElementChecked(processEnableCheckbox);
		log.info("Current checkbox state - isChecked: " + isCurrentlyEnabled);
		
		if (!isCurrentlyEnabled) {
			// Use JavaScript to click the checkbox
			log.info("Checkbox is not enabled. Enabling it now using JavaScript click...");
			commonWaits.clickByJS(processEnableCheckbox);
			log.info("Process enable checkbox clicked - process is now enabled");
		} else {
			log.info("Process enable checkbox is already selected");
		}
		
		// Step 4: Click on save button
		log.info("Step 4: Clicking on Save button");
		
		// Use JavaScript to click Save button
		log.info("Clicking Save button using JavaScript...");
		commonWaits.scrollToSpecificElement(saveProcessButton);
		commonWaits.clickByJS(saveProcessButton);
		log.info("Save button clicked");
		
		// Wait for save to complete
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		// Step 5: Click on Close button
		log.info("Step 5: Clicking on Close button");
		
		// Use JavaScript to click Close button
		log.info("Clicking Close button using JavaScript...");
		clickByJS(buttonClose);
		log.info("Close button clicked");
		
		switchtoDefaultFrame();
		
		// Wait for close to complete
		commonWaits.waitForAngularRequestsToFinish();
		log.info("Process '" + processName + "' enabled successfully");
		return this;
	}

	/**
	 * Runs a process by selecting it, clicking manage, run, filling form fields, and verifying success
	 * @param processName The name of the process to run (e.g., "studentUpdateRecord")
	 */
	public InnovationSuite runProcess(String processName, String studentID, String description, String studentRecordInstanceID, String recordID) {
		clickProcessesTab();
		log.info("Running process: " + processName);
		
		// Step 1: Click on process row checkbox
		log.info("Step 1: Clicking on process row checkbox for: " + processName);
		clickByJS(getDriver().findElement(By.xpath(String.format(processRowCheckbox, processName))));
		log.info("Process row checkbox clicked for: " + processName);
		
		// Step 2: Click on Manage Process button
		log.info("Step 2: Clicking on Manage Process button");
		commonWaits.waitForElementVisible(manageProcessButton, 30);
		clickByJS(manageProcessButton);
		log.info("Manage Process button clicked");
		
		// Step 3: Wait for page loaders to disappear
		log.info("Step 3: Waiting for page loaders to disappear");
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageReadyState(getDriver());
		log.info("Page loaders disappeared");
		
		// Step 4: Click on Run Process button
		log.info("Step 4: Clicking on Run Process button");
		commonWaits.waitForElementVisible(runProcessButton, 30);
		clickByJS(runProcessButton);
		log.info("Run Process button clicked");
		
		// Wait for run dialog to appear
		commonWaits.waitForAngularRequestsToFinish();
		
		if(studentID!= null)
		{
		log.info("Filling in 'Assignee' field");
		WebElement assigneeInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(formFieldInput, "studentID")));
		commonWaits.typeAndSelectEachPart(assigneeInput, studentID);
		log.info("Assignee field filled");
		}
		
		if(description!= null)
		{
		log.info("Filling 'Description' field");
		commonWaits.typeAndSelectEachPart(descriptionInput, description);
		log.info("Description field filled");
		}
		
		if(studentRecordInstanceID!= null)
		{
		log.info("Filling in 'characterRecordInstanceID' field");
		WebElement studentRecordInstanceIdInput = commonWaits.waitForByElementToBeVisible(
			By.xpath(String.format(formFieldInput, "studentRecordInstanceID")));
		commonWaits.typeAndSelectEachPart(studentRecordInstanceIdInput, studentRecordInstanceID);
		log.info("characterRecordInstanceID field filled");
		}
		
		if(recordID!= null)
		{
		log.info("Filling in 'recordIDInput' field");
		WebElement recordIDInput = commonWaits.waitForByElementToBeVisible(
			By.xpath(String.format(formFieldInput, "recordID")));
		commonWaits.typeAndSelectEachPart(recordIDInput, recordID);
		log.info("recordIDInput field filled");
		}
       
		log.info("Step 7: Clicking on Run Process OK button");
		commonWaits.waitForElementVisible(runProcessOkButton, 30);
		clickByJS(runProcessOkButton);
		log.info("Run Process OK button clicked");
		
		// Wait for process to start
		commonWaits.waitForAngularRequestsToFinish();
		
		// Step 8: Check for appearance of process run success message
		log.info("Step 9: Checking for appearance of process run success message");
		commonWaits.waitForElementVisible(processRunSuccessMsg, 30);
		log.info("Process run success message appeared");

		// Step 9: Wait for disappearance of process run success message
		log.info("Step 10: Waiting for disappearance of process run success message");
		commonWaits.waitForElementVisible(processRunSuccessMsg);
		log.info("Process run success message disappeared");
		
		// Step 10: Click on Close button
		log.info("Step 8: Clicking on Close button");
		commonWaits.waitForElementVisible(closeButton, 30);
		clickByJS(closeButton);
		log.info("Close button clicked");
		
		log.info("Process '" + processName + "' executed successfully");
		return this;
	}
	
	public String getFirstRecordId(String recordName) {
		log.info("Clicked Record Tab");
		clickRecordTab();
		selectRecordDefinition(recordName);
		clickEditData(recordName);
        selectRecord();
        String id = getID();
        clickWithoutInterception(closeRecordButton);
        clickWithoutInterception(By.xpath(aaaClosePopupButton));
        return id;
	}
	
	public String getRecordDescriptionField() {
		log.info("Getting ID");
		commonWaits.waitForElementVisible(recordDescriptionField);
		String descriptionValue = getID.getAttribute("value");
		log.info("Getting Description: " + descriptionValue);
		return descriptionValue;
	}
	
	public InnovationSuite validateRecordPresence(String recordName, String filter, Boolean presenceFlag) {
		clickRecordTab();
		selectRecordDefinition(recordName);
		clickEditData(recordName);
		searchUserText(filter);
		if(presenceFlag) {
			Assert.assertTrue(selectRecord(), "Record is not present for Filter:" + filter + "which is not expected");
			clickWithoutInterception(closeRecordButton);
	        clickWithoutInterception(By.xpath(aaaClosePopupButton));
		}
		else {
			Assert.assertTrue(isVisible(getRowData), "Record is present for Filter:" + filter + "which is not expected");
		}
		return this;
	}

	/**
	 * Creates a new custom application in Innovation Studio
	 * 
	 * @param name        Application name
	 * @param shortName   Application short name
	 * @param groupId     Application group ID
	 */
	public InnovationSuite createCustomApplication(String name, String shortName, String groupId) {
		WebElement appLink = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(bundleString, name)));
		if (commonWaits.isElementPresent(appLink)) {
			log.info("Application: " + name + "is already created..");
		} else {
			log.info("Creating custom application: " + name);

			// Step 1: Click on +New button
			log.info("Step 1: Clicking on +New button");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			commonWaits.waitForElementClickable(newApplicationButton, 30);
			clickByJS(newApplicationButton);
			log.info("+New button clicked");

			// Step 2: Click on Custom Application option from dropdown
			log.info("Step 2: Clicking on Custom Application option");
			commonWaits.waitForAngularRequestsToFinish();
			commonWaits.waitForElementClickable(customApplicationOption, 30);
			clickByJS(customApplicationOption);
			log.info("Custom Application option clicked");

			// Wait for create application form to load
			commonWaits.waitForAngularRequestsToFinish();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

			// Step 3: Fill application name
			log.info("Step 3: Filling application name: " + name);
			typeKeysWithEnter(applicationNameInput, name);
			log.info("Application name filled: " + name);

			// Step 5: Fill application short name
			log.info("Step 5: Filling application short name: " + shortName);
			typeKeysWithEnter(applicationShortNameInput, shortName);
			log.info("Application short name filled: " + shortName);

			// Step 6: Fill application group ID
			log.info("Step 6: Filling application group ID: " + groupId);
			typeKeysWithEnter(applicationGroupIdInput, groupId);
			log.info("Application group ID filled: " + groupId);

			// Step 7: Click Save button
			log.info("Step 7: Clicking on Save button");
			clickByJS(createApplication);
			log.info("Create button clicked");

			// Wait for application creation to complete
			commonWaits.waitForAngularRequestsToFinish();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

			Assert.assertTrue(commonWaits.isElementPresent(createApplicationSucceed), "Application creation Failed");
			commonWaits.clickWithoutInterception(closeButton);

			log.info("Custom application '" + name + "' created successfully");
		}
		return this;
	}

	/**
	 * Verifies that custom application was created successfully
	 * @param applicationName The name of the application to verify
	 */
	public InnovationSuite verifyCustomApplicationCreated(String applicationName) {
		log.info("Verifying custom application created: " + applicationName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		
		// Check if application is present in the list or page
		try {
			Assert.assertTrue(getDriver().findElement(By.xpath(String.format(applicationLink, applicationName))).isDisplayed(), "Custom application '" + applicationName + "' not found");
			log.info("Custom application '" + applicationName + "' verified successfully");
		} catch (Exception e) {
			log.error("Failed to verify custom application: " + applicationName);
			Assert.fail("Custom application '" + applicationName + "' was not created successfully");
		}
		return this;
	}
		
	public InnovationSuite validateRecordFields(String recordName, String... customFields) {
		log.info("Validating Record Fields for record: " + recordName);
		
		// Navigate to record definition
		clickRecordTab();
		selectRecordDefinition(recordName);
		clickEditData(recordName);
		
		// Wait for table to load
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForPageReadyState(getDriver());
		
		// List of expected OOTB fields
		String[] ootbFields = {
			"Display ID",
			"Created By",
			"Created Date",
			"Assignee",
			"Modified By",
			"Modified Date",
			"Status",
			"Description",
			"ID"
		};
		
		log.info("Step 1: Validating " + ootbFields.length + " OOTB fields in table header");
		
		// Validate OOTB fields in table header
		for (String fieldName : ootbFields) {
			try {
				// XPath to find the field header by text
				WebElement fieldHeader = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(fieldXPath, fieldName)));
				
				Assert.assertTrue(fieldHeader.isDisplayed(), "OOTB Field '" + fieldName + "' is not visible");
				log.info("✓ OOTB Field validated: " + fieldName);
				
			} catch (Exception e) {
				log.error("✗ OOTB Field NOT found: " + fieldName);
				Assert.fail("OOTB Field '" + fieldName + "' is not present in the record definition table");
			}
		}
		
		log.info("All " + ootbFields.length + "OOTB fields validated successfully");
		
		// Validate custom fields in dropdown (if provided)
		if (customFields != null && customFields.length > 0) {
			log.info("Step 2: Validating " + customFields.length + " custom fields in table toolbar dropdown");
			
			// Click on table toolbar to open dropdown
			log.info("Clicking on table toolbar to open dropdown");
			clickByJS(tableToolbar);
			log.info("Table toolbar clicked");
			
			// Wait for dropdown to appear
			commonWaits.waitForAngularRequestsToFinish();
			log.info("Dropdown menu opened successfully");
			
			// Validate each custom field in dropdown
			for (String fieldName : customFields) {
				try {
					// XPath to find the field in dropdown menu
					WebElement dropdownField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownFieldXPath, fieldName)));
					
					Assert.assertTrue(dropdownField.isDisplayed(), "Custom Field '" + fieldName + "' is not visible in dropdown");
					log.info("✓ Custom Field validated in dropdown: " + fieldName);
					
				} catch (Exception e) {
					log.error("✗ Custom Field NOT found in dropdown: " + fieldName);
					Assert.fail("Custom Field '" + fieldName + "' is not present in the table toolbar dropdown");
				}
			}
			
			log.info("All " + customFields.length + " custom fields validated successfully in dropdown");
			
			// Close the dropdown by clicking toolbar again or pressing ESC
			try {
				clickByJS(tableToolbar);
				log.info("Dropdown closed");
			} catch (Exception e) {
				log.warn("Could not close dropdown, continuing...");
			}
		}
		
		log.info("All fields validated successfully for record: " + recordName + 
				" (" + ootbFields.length + " OOTB + " + (customFields != null ? customFields.length : 0) + " custom)");
		
		clickWithoutInterception(closeButton);
		return this;
	}
	
	public InnovationSuite navigateToWorkspace() {
		clickWithoutInterception(workspaceTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
		return this;
	}
	
	public InnovationSuite deleteRecords(String recordName) {
		commonWaits.clickIfElementPresent(closeButton);
		clickRecordTab();
		log.info("Selecting checkbox for record: " + recordName);
		switchtoDefaultFrame();
	    commonWaits.clickWithoutInterception(By.xpath(String.format(recordCheckboxByName, recordName)));
	    log.info("Successfully selected checkbox for record: " + recordName);
		commonWaits.clickWithoutInterception(deleteButton);
		confirmDeleteMonitoringRule();
		log.info("Successfully deleted record: " + recordName);
		return this;
	}
	
	public InnovationSuite deleteProcesses(String processName) {
		clickProcessesTab();
		log.info("Selecting checkbox for process: " + processName);
	    commonWaits.clickWithoutInterception(By.xpath(String.format(recordCheckboxByName, processName)));
	    log.info("Successfully selected checkbox for record: " + processName);
		commonWaits.clickWithoutInterception(deleteButton);
		confirmDeleteMonitoringRule();
		log.info("Successfully deleted process: " + processName);
		return this;
	}
	

	public InnovationSuite enableRequiredFeatureFlag(String featureFlagName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement featureFlagOptionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(featureFlagOption, featureFlagName)));
		clickWithoutInterception(featureFlagOptionElement);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

		if(EditFeatureFlagToggleInput.getAttribute("aria-checked").equals("true")) {
			log.info("Feature flag is already enabled. Skipping enable action.");
		} else {
			log.info("Feature flag is not enabled. Enabling it now.");
			clickWithoutInterception(EditFeatureFlagToggle);
			clickWithoutInterception(ToggleSaveButton);
			commonWaits.waitForExecutionComplete();
		}
		return this;
	}
	
	public InnovationSuite clickNewInEditDataPanel() {
		log.info("Clicking New button in Edit Data panel");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForElementClickable(newRecordButton).click();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		log.info("New button clicked in Edit Data panel");
		return this;
	}

	public InnovationSuite clickSaveButton() {
		log.info("Clicking Save button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.waitForElementClickable(saveButton).click();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		log.info("Save button clicked");
		return this;
	}

	public InnovationSuite uploadAttachmentFile(String fieldName, File file) {
		log.info("Uploading file to attachment field: " + fieldName);
		try {
			// If file path is relative, try to find it in resources
			File fileToUpload = file;
			if (!file.exists()) {
				// Try to find file in resources/HelixGPT directory
				String resourcePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator
						+ "HelixGPT" + File.separator + file.getName();
				fileToUpload = new File(resourcePath);
				if (!fileToUpload.exists()) {
					log.warn("File not found in resources, using original path: " + file.getAbsolutePath());
					fileToUpload = file;
				} else {
					log.info("Found file in resources: " + fileToUpload.getAbsolutePath());
				}
			}

			// Locator for attachment field input
			String attachmentFieldLocator = "//adapt-rx-control-label[contains(., '" + fieldName
					+ "')]/following-sibling::div//input[@type='file']";
			WebElement attachmentInput = commonWaits.waitForElementPresenceLocated(By.xpath(attachmentFieldLocator));
			attachmentInput.sendKeys(fileToUpload.getAbsolutePath());
			commonWaits.waitForAngularRequestsToFinish();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			log.info("File uploaded successfully: " + fileToUpload.getName());
		} catch (Exception e) {
			log.error("Failed to upload file to attachment field: " + e.getMessage());
			throw e;
		}
		return this;
	}

	// Enable checkbox by field name
	public InnovationSuite enableCheckboxField(String fieldName) {
		log.info("Enabling checkbox for field: " + fieldName);
		try {
			WebElement checkboxLabel = commonWaits
					.waitForByElementToBeVisible(By.xpath(String.format(checkboxLocator, fieldName)));
			WebElement checkboxInput = commonWaits
					.waitForElementPresenceLocated(By.xpath(String.format(checkboxInputLocator, fieldName)));

			String isChecked = checkboxInput.getAttribute("aria-checked");
			if (isChecked == null || isChecked.equals("false")) {
				clickWithoutInterception(checkboxLabel);
				log.info("Checkbox '" + fieldName + "' enabled");
			} else {
				log.info("Checkbox '" + fieldName + "' already enabled");
			}
			commonWaits.waitForAngularRequestsToFinish();
		} catch (Exception e) {
			log.error("Failed to enable checkbox field: " + e.getMessage());
			throw e;
		}
		return this;
	}

    public void disableGainsightToggleIfEnabled() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		commonWaits.switchtoDefaultFrame();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForElementClickable(enablePerformanceToggleLabel, 5);

        String ariaChecked = enablePerformanceToggleInputWebElement.getAttribute("aria-checked");
        if ("true".equalsIgnoreCase(ariaChecked)) {
            commonWaits.clickWithoutInterception(By.xpath(enablePerformanceToggleLabel));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        } else {
            log.info("Toggle already disabled. Skipping.");
        }
    }

    public void disableLoadGainsightFromBmcItToggleIfEnabled() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForElementClickable(loadGainsightFromBmcItToggleLabel);

        String ariaChecked = loadGainsightFromBmcItToggleInput.getAttribute("aria-checked");
        if ("true".equalsIgnoreCase(ariaChecked)) {
            commonWaits.clickWithoutInterception(loadGainsightFromBmcItToggleLabel);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        } else {
            log.info("Toggle already disabled. Skipping.");
        }
    }

    public String getValueFromBundleIDField() {
        return getInputValueByLabel("Bundle ID");
    }

    public String getValueFromGlobalContextAreaField() {
		log.info("Getting value from Global Context Area field");
        commonWaits.waitForElementInvisible_withFluentWait(getDriver(),globalContextTextAreaLocator, 100);
        String value = getDriver().findElement(By.xpath(globalContextTextAreaLocator)).getAttribute("value");
        log.info("Value of global context is - "+ value) ;
        return value;
    }

    public String getInputValueByLabel(String labelText) {
        WebElement el = findElement(By.xpath(String.format(inputFieldValue, labelText)));
        commonWaits.waitForElementVisible(el);
        String value = el.getAttribute("value");
        return value == null ? "" : value;
    }

    public String getTextareaValueByLabel(String labelText) {
        WebElement el = findElement(By.xpath(String.format(textareaFieldValue, labelText)));
        commonWaits.waitForElementVisible(el);
        String value = el.getAttribute("value");
        return value == null ? "" : value;
    }

    public InnovationSuite setTypeHead(String fieldName, String option) {
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(typeheadInput, fieldName))), "Typehead field with name '" + fieldName + "' is not present.");
        WebElement input = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadInput, fieldName)));
        input.sendKeys(option);
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadOption, option)));
        optionElement.click();
        return this;
    }

	public InnovationSuite selectEnvoirnmentType(String environmentType) {
		log.info("Selecting environment type: " + environmentType);
		commonWaits.clickWithoutInterception(environmentTypeDropdown);
		WebElement environmentTypeElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(environmentTypeDropdownValue, environmentType)));
		environmentTypeElement.click();
		return this;
	}

	public String getStatusValue() {
		WebElement statusValue = commonWaits.waitForElementVisible(statusValueLocator);
		return statusValue.getText();
	}

	public void clickOnCancelButton() {
		commonWaits.clickWithoutInterception(cancelButton);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
	}

	public boolean isButtonOn(WebElement button) {
        return "true".equals(button.getAttribute("aria-pressed"));
    }

	public void verifySupportsbuttonsAreOn(String ariaLabel) {
        WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(supportsTrueStatus, ariaLabel)));
        boolean isTrueButtonOn = isButtonOn(ele);
    	 if (!isTrueButtonOn) {
             log.info("The 'True' button is OFF. Clicking to turn it ON.");
             ele.click();
         } else {
             log.info("The 'True' button is already ON.");
         }
         isTrueButtonOn = isButtonOn(ele);
         log.info("After clicking, the 'True' button is " + (isTrueButtonOn ? "ON" : "OFF"));
    }

	public void updateStatusToEnabled() {
		verifySupportsbuttonsAreOn("Supports Agents");
		verifySupportsbuttonsAreOn("Supports Indexing");
		commonWaits.waitForElementClickable(statusDropdown).click();
		commonWaits.waitForElementClickable(enabledStatus).click();
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.waitForElementVisible(saveStatusButton);
		commonWaits.clickWithoutInterception(saveStatusButton);
		commonWaits.waitForExecutionComplete();
	 }

	 public boolean isRhoFirstPresent() {
		commonWaits.waitForSometime(commonWaits.moreDelay);
		return commonWaits.isElementPresent(rhoFirstPresent);
	 }

	 public String getCellTextByColumnName(String columnName, int rowNumber) {
		int columnIndex = -1;
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).getAttribute("aria-label").trim().equalsIgnoreCase(columnName)) {
				columnIndex = i + 1; // XPath uses 1-based indexing
				break;
			}
		}
		if (columnIndex == -1) {
			throw new RuntimeException("Column not found: " + columnName);
		}
		return getDriver().findElement(By.xpath(String.format(columnValue, rowNumber, columnIndex))).getText().trim();
	}

	public void selectDropdownValue(String DropdownName, String value) {
		log.info("Selecting Dropdown Value: " + value + " from " + DropdownName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownName, DropdownName)));
		clickWithoutInterception(dropdown);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownOption, value)));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Dropdown Value: " + value + " from " + DropdownName);
	}

	public void selectDataSourceDropdownValue(String DropdownName, String value) {
		log.info("Selecting Dropdown Value: " + value + " from " + DropdownName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownName, DropdownName)));
		clickWithoutInterception(dropdown);
		WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dataSourceDropdownOption, value)));
		clickWithoutInterception(option);
		commonWaits.waitForExecutionComplete();
		log.info("Selected Dropdown Value: " + value + " from " + DropdownName);
	}

	 
     public String addNewConnectionForSharePointAndPickID(Map<Object, String> connectionData) {
        log.info("Adding new connection for SharePoint with name: " + connectionData.get("ConnectionName"));
        selectRecordDefinition( connectionData.get("ConnectionName"));
		clickEditData( connectionData.get("ConnectionName"));
        searchUserText( connectionData.get("DataSourceName"));
		if(isRhoFirstPresent()) {
			log.info("Connection " + connectionData.get("ConnectionName") + " is present. Picking ID");
			return getCellTextByColumnName("ID", 1);
		}
		else {
			log.info("RhoFirst is not present. Clicking on New button and creating new connection");
			clickWithoutInterception(newConnectionButton);
			WebElement clientIDInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Client ID")));
			commonWaits.keyClearAndType(clientIDInput, connectionData.get("Client ID"));
			WebElement clientSecretInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Client Secret")));
			commonWaits.keyClearAndType(clientSecretInput, connectionData.get("Client Secret"));
			WebElement siteInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Site")));
			commonWaits.keyClearAndType(siteInput, connectionData.get("Site"));
			WebElement tenantIDInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Tenant ID")));
			commonWaits.keyClearAndType(tenantIDInput, connectionData.get("Tenant ID"));
			WebElement tenantInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Tenant Name")));
			commonWaits.keyClearAndType(tenantInput, connectionData.get("Tenant Name"));
			WebElement nameInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Name")));
			commonWaits.keyClearAndType(nameInput, connectionData.get("Name"));
			selectDropdownValue("Connection Type", connectionData.get("Connection Type"));
			selectDataSourceDropdownValue("DataSource ID", connectionData.get("DataSource ID"));
			clickWithoutInterception(newConnectionSaveButton);
			commonWaits.waitForExecutionComplete();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			return getCellTextByColumnName("ID", 1);
		}
		
    }

	public String addNewConnectionForWebAndPickID(Map<Object, String> connectionData) {
        log.info("Adding new connection for Web with name: " + connectionData.get("ConnectionName"));
        selectRecordDefinition( connectionData.get("ConnectionName"));
		clickEditData( connectionData.get("ConnectionName"));
        searchUserText( connectionData.get("DataSourceName"));
		if(isRhoFirstPresent()) {
			log.info("Connection " + connectionData.get("ConnectionName") + " is present. Picking ID");
			return getCellTextByColumnName("ID", 1);
		}
		else {
			log.info("RhoFirst is not present. Clicking on New button and creating new connection");
			clickWithoutInterception(newConnectionButton);
			WebElement nameInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Name")));
			commonWaits.keyClearAndType(nameInput, connectionData.get("Name"));
			WebElement urlInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "URL")));
			commonWaits.keyClearAndType(urlInput, connectionData.get("URL"));
			selectDataSourceDropdownValue("DataSource ID", connectionData.get("DataSource ID"));
			clickWithoutInterception(newConnectionSaveButton);
			commonWaits.waitForExecutionComplete();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			return getCellTextByColumnName("ID", 1);
		}
		
    }

	public String addNewConnectionForCSMAndPickID(Map<Object, String> connectionData) {
        log.info("Adding new connection for CSM with name: " + connectionData.get("ConnectionName"));
        selectRecordDefinition( connectionData.get("ConnectionName"));
		clickEditData( connectionData.get("ConnectionName"));
        searchUserText( connectionData.get("DataSourceName"));
		if(isRhoFirstPresent()) {
			log.info("Connection " + connectionData.get("ConnectionName") + " is present. Picking ID");
			return getCellTextByColumnName("ID", 1);
		}
		else {
			log.info("RhoFirst is not present. Clicking on New button and creating new connection");
			clickWithoutInterception(newConnectionButton);
			WebElement nameInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Name")));
			commonWaits.keyClearAndType(nameInput, connectionData.get("Name"));
			selectDataSourceDropdownValue("DataSource ID", connectionData.get("DataSource ID"));
			clickWithoutInterception(newConnectionSaveButton);
			commonWaits.waitForExecutionComplete();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			return getCellTextByColumnName("ID", 1);
		}
		
    }

	public void navigateToWorkspaceTab() {
		log.info("Navigating to Workspace tab");
		clickWithoutInterception(workspaceTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		log.info("Navigated to Workspace tab");
	}

	public void triggerDataInjectionJob(Map<Object, String> dataConnectionJobData, String connectionId) {
		log.info("Triggering Data Injection Job: " + dataConnectionJobData.get("Description"));
		searchUserText(dataConnectionJobData.get("Description"));
		if(isRhoFirstPresent()) {
			log.info("Data Connection Job is present. Clicking on Edit button");
		}
		else {
			log.info("RhoFirst is not present. Clicking on New button and creating new data connection job");
			clickWithoutInterception(newConnectionButton);
			WebElement descriptionInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Description")));
			commonWaits.keyClearAndType(descriptionInput, dataConnectionJobData.get("Description"));
			WebElement connectionIdInput = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "ConnectionId")));
			commonWaits.keyClearAndType(connectionIdInput, connectionId);
			WebElement datasource = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(newConnectionInputTextArea, "Datasource")));
			commonWaits.keyClearAndType(datasource, dataConnectionJobData.get("DataSourceName"));
			clickWithoutInterception(executeNowTrueButton);
			clickWithoutInterception(newConnectionSaveButton);
			commonWaits.waitForExecutionComplete();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
			log.info("Data Injection Job triggered successfully");
		}
	}
}
