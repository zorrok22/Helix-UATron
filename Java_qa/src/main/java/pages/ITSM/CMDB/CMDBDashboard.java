package pages.ITSM.CMDB;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import framework.Launcher;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CMDBDashboard extends BasePage{

    CommonWaits commonWaits;
    SolqaLogger log;
    CommonMethods commonMethods;

    String datasetIDVariable = "datasetId";
    String permissionFilterSearchBoxVariable = "permission_filter_text";
    String datasourceVariable = "datasource";
    String datasourceFilterVariable = "datasource_filter";

    //String locators
    public String CMDB_Menu = "//adapt-navigation//button[contains(@class, 'adapt-menu__link') and contains(. ,'%s')]";
    public String CMDB_Menu_Item = "//adapt-submenu//*[contains(@class, 'adapt-submenu__link') and contains(. ,'%s')]";
    public String dataset_Item = "//a[contains(@id, 'dataset_picker_create_ci_%s_dropdown_item')]";
    public String class_Item = "//a[contains(@id, '%s_classkey')]";
    public String CMDB_SubMenu_Item = "//button[contains(. ,'%s')]/span[contains(@class, 'adapt-menu__link-toggle')]";
    public String reconciliationJobLink = "//div[contains(@class, 'jobName')]//a[normalize-space()='%s']";
    public String atriumIntegratorJobLink = "//div[contains(@class, 'jobName')]//a[normalize-space()='%s']";
    public String gainsightSelfHelpIconIframe = "//iframe[@id='px-widget-window']";
    public String gainsightSelfHelpIconButton = "//button[contains(@class,'px-widget-dragging-handle')]";
    public String selfHelpCloseButton = "//button[@aria-label='Click to close the Knowledge Center Bot window.' and contains(@class,'px-widget-dragging-handle')]";
    public String whatsNewTourButton = "//button[contains(@class,'entry-item')]//div[contains(text(),\"What's new\")]//ancestor::button";
    public String whatsNewTourPopup = "//div[contains(@class,'vex-content') and contains(@class,'apt-carousel-popup')]";
    public String whatsNewTourPopupTitle = "//div[contains(@class,'px-slider-title') and contains(text(),\"What's new\")]";
    public String whatsNewTourCloseButton = "//div[contains(@class,'vex-close') and contains(@class,'px-close-button')]";
    public String documentationAndVideosButton = "//button[contains(@class,'entry-item')]//div[contains(@title,'Documentation and videos')]//ancestor::button";
    public String documentationAndVideosHeader = "//div[contains(@class,'navigation-header-title') and normalize-space()='Documentation and videos']";
    public String cmdbDocumentationLink = "//button[contains(@class,'entry-item')]//div[contains(@title,'BMC Helix CMDB documentation')]//ancestor::button";
    public String profileMenuButton = "//button[@aria-label='Profile menu' and contains(@class,'a-profile__menu')]";
    public String analyticsMenuButton = "//button[contains(@class,'a-dropdown__link') and contains(text(),'Analytics')]";
    public String analyticsPopupTitle = "//h2[contains(@class,'modal-title') and contains(text(),'Analytics')]";
    public String analyticsCancelButton = "//button[@rx-id='cancel-button' and contains(text(),'Cancel')]";
    public String class_Name = "//a[normalize-space()='%s']";
    public String textField = "//label[normalize-space()='%s']//following-sibling::textarea";
    public String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    public String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    public String OperatorDropdownOptions = "//td[contains(@class,'MenuEntryName') and text()='%s']";
    public String NameInputField = "//label[normalize-space()='%s']//parent::div//input";
    public String DescriptionInputField = "//label[normalize-space()='%s']//parent::div//textarea";
    public String activityOptionDropdown = "//button[normalize-space()='%s']";
    public String datasetOptionDropdown = "//a[contains(@class,'dropdown-item') and normalize-space()='%s']";
    public String qualificationRulesetOption = "//span[normalize-space()='%s']";
    public String activityTypeOption = "//button[contains(@class,'dropdown-item ng-star-inserted') and normalize-space()='%s']";
    public String buttonTextLocator = "//button[contains(.,'%s')]";
    public String inputContainsIdLocator = "//input[contains(@id,'%s')]";
    public String selectPermissionLocator = "//tr[contains(.,'%s')]//input";
    public String datasetLocator = "//a[contains(.,'%s')]";
    public String datasetNameLocator = "//div[contains(@id,'cx_datatable_all-datasets_col_name')]//a[normalize-space()='%s']";
    public String datasetSourceNameLocator = "//div[contains(@id,'name')]//*[contains(normalize-space(),'%s')]";
    public String datasetIDLocator= "//div[contains(@id,'datasetId')]//a[normalize-space()='%s']";
    public String buttonIdContainsLocator = "//button[contains(@id,'%s')]";
    public String datasourceOptionLocator = "//a[contains(@id,'datasource') and text()='%s']";
	public String normaliaztionSelectDatasetLocator = "//a[contains(@id, 'dataset_picker_ne_create_edit_%s_dropdown_item')]";
	public String reconciliationSelectDatasetLocator = "//a[contains(@id,'dataset_picker_re_id_source_%s_dropdown_item')]";
    public String linkContainsText = "//a[contains(text(),'%s')]";
    
    // By locators for fluent wait methods
    private By jobNameInputBy = By.xpath("//div[contains(@id,'jobNameLbl')]//input");
    private By addActivityButtonBy = By.xpath("//button[contains(text(),'Add Activity')]");
    
    public String schduleTypeLocator = "//button[text()='%s'] | //span[text()='%s']";
    public String ciSearchResultButton = "//div[contains(@id,'cx_datatable_search')]//button[contains(text(),'%s')]";
    public String searchResultRow = "//button[@id='search_result_row_%s']";
    public String searchResultByName = "//button[@class='btn btn-link' and contains(text(),'%s')]";
    public String normalizeJobType = "//div[@aria-labelledby='start_job_run_btn_group']/button[normalize-space()='%s']";
    public String normalizationCompletedStatus = "//div[@class='normalization-form']//*[@id='statusLbl' and contains(normalize-space(.),'Completed')]";
    public String catalogDiscoveryInput = "//input[contains(@id, 'discovered_%s')]";
    public String catalogMappedInput = "//input[contains(@id, 'mapped_%s')]";
    public String dropdownOption = "//option[@value='%s']";
    String CI_Options = "//*[@role='menu' or contains(@class,'context') or contains(@class,'Context') or contains(@class,'menu')]" +
            "//*[self::button or self::a or self::li or self::span][contains(normalize-space(),'%s')]./ancestor::button[1] | ./ancestor::a[1]";
    String CIOption ="//ul[contains(@class,'dropdown-menu')]//span[contains(.,'%s')]/ancestor::a";
    String inputField = "//label[normalize-space()='%s']//following-sibling::input";
	String cmdbDropdown = "//label[text()='%s']/following-sibling::cx-select";
	String cmdbDropdownItem = "//button[@role='option' and contains(. ,'%s')]";
	public String permissionNameCheckbox = "//td[text()='%s']/preceding-sibling::td//label";
    String datasourceNameLocator = "//*[@id='cx_datatable_all_datasources_col_name']//a[normalize-space()='%s']";
    String datasetIDRowInDatasetConfigTable = "//*[@id='cx_datatable_ne_dataset_config_col_dataset_id' and contains(normalize-space(.), '%s')]";
    String checkbox_Allow_New_Product_Catalog_Entry = "//adapt-rx-checkbox[contains(. ,'Allow New Product Catalog Entry')]//input";
    String checkbox_Allow_Unapproved_CIs = "//adapt-rx-checkbox[contains(. ,'Allow Unapproved CIs')]//input";

    //WebElements Locators
    @FindBy(xpath = "//select[@id='quick_search']")
    WebElement quickSearchDropdown;
    
    @FindBy(xpath = "//input[@id='cmdb_widget_typeahead_datasets_input']")
    WebElement datasetInputField;
    
    @FindBy(xpath = "//input[@id='cmdb_widget_typeahead_classes_input']")
    WebElement classInputField;
    
    @FindBy(xpath = "//input[@id='cmdb_widget_typeahead_attributes_input']")
    WebElement attributeInputField;
    
    @FindBy(xpath = "//input[@id='attribute_value']")
    WebElement attributeValueField;
    
    @FindBy(xpath = "//button[@id='cmdb_widget_search_btn']")
    WebElement searchButton;
    
    @FindBy(xpath = "//div[@id='cx_datatable_search_widget_results_page_sel']//span")
    WebElement searchResultsCount;
    @FindBy(xpath = "//*[@id='cmdb_widget_typeahead_datasets_input']")
    WebElement CMDB_verifyLogin;

    @FindBy(xpath = "//create-cmdb-instance//button[contains(@class, 'dataset-picker-toggle')]")
    WebElement select_Dataset;

    @FindBy(xpath = "//cx-cmdb-class-picker//button[contains(@class, 'class-picker')]")
    WebElement select_Class;

    @FindBy(xpath = "//input[contains(@id, 'dataset_picker_create_ci')]")
    WebElement selectDatasetInput;

    @FindBy(xpath = "//input[contains(@id, 'class_picker')]")
    WebElement selectClassInput;

    @FindBy(xpath = "//button[text()='Save']")
    WebElement SaveCIBtn;

    @FindBy(xpath = "//cmdb-topology//input[@id='explorer_edit_mode_switch']/following-sibling::span//span[text()='Edit Mode']")
    WebElement explorerEditBtn;

    @FindBy(xpath = "//*[@id='explorer_node_label']/parent::*")
    WebElement ciName;

    @FindBy(xpath = "//*[contains(@id,'cmdb_widget_search_btn')]")
    WebElement searchCIButton;

    @FindBy(xpath = "//div[contains(@id, 'totalJobs_value_box')]/span")
    WebElement totalJobs;

    @FindBy(xpath = "//button[@id='startJobBtn']")
    WebElement startJobButton;

    @FindBy(xpath = "//div[@class='reconcilation-form']//*[@class='re-job-status-label']")
    WebElement reconciliationJobStatus;

    @FindBy(xpath = "//dynamic-adapt-form-control//input[@id='Name']")
    WebElement ci_Name_Input;

    @FindBy(xpath = "//dynamic-adapt-form-control//input[@id='ShortDescription']")
    WebElement ci_Desc_Input;

    @FindBy(xpath = "//cmdb-topology//div[@id='graph_outer_container_div']")
    WebElement topologyContainer;

    @FindBy(xpath = "//*[contains(@id,'cmdb_widget_typeahead_datasets_input')]")
    WebElement searchCIByAssetType;

    @FindBy(xpath = "//*[contains(@id,'cmdb_widget_typeahead_attributes_input')]")
    WebElement searchCIInputByType;

    @FindBy(xpath = "//*[contains(@id,'attribute_value')]")
    WebElement searchCIInputValue;

    @FindBy(xpath = "//input[contains(@placeholder,'Filter')]")
    WebElement jobFilterSearchBox;

    @FindBy(xpath = "//*[@id='statusLbl']//span[text()='Completed']")
    WebElement completedStatus;

    @FindBy(xpath = "//button[normalize-space()='Add Ruleset']")
    WebElement addRuleSetButton;

    @FindBy(xpath = "//input[@placeholder='Ruleset Name']")
    WebElement ruleSetNameInput;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement saveRuleSetButton;

    @FindBy(xpath = "//td[contains(@class,'qual-rule-grp-name')]//span")
    List<WebElement> AddedRuleSetName;

    @FindBy(xpath = "//button[normalize-space()='Add Qualification Rule']")
    WebElement addQualificationRuleButton;

    @FindBy(xpath = "//input[@placeholder='Rule Name']")
    WebElement ruleNameInput;

    @FindBy(xpath = "//textarea[@name='qualification']")
    WebElement qualificationTextBox;

    @FindBy(xpath = "//span[normalize-space()='Select Class']/parent::button[contains(@id,'dropdown_menu')]")
    WebElement classNameDropdown;

    @FindBy(xpath = "//input[contains(@id,'class_picker')]")
    WebElement classNameInput;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement saveQualificationRuleButton;

    @FindBy(xpath = "//button[normalize-space()='Add Dataset']")
    WebElement addDatasetButton;

    @FindBy(xpath = "//input[@name='name']")
    WebElement datasetNameInput;

    @FindBy(xpath = "//input[@name='datasetId']")
    WebElement datasetIDInput;

    @FindBy(xpath = "//input[@id='permission_filter_text']")
    WebElement permissionsInput;

    @FindBy(xpath = "(//div[contains(@class,'checkbox cx-permission-checkbox')])[1]")
    WebElement permissionCheckbox;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement saveDatasetButton;

    @FindBy(xpath = "//a[normalize-space()='New request']")
    WebElement newRequestButton;

    @FindBy(xpath = "//td[contains(@class,'Popup')]//iframe")
    WebElement OkPopUpIframe;

    @FindBy(xpath = "//input[contains(@id,'400009720')]/parent::div//a")
    WebElement OperatorDropdown;

    @FindBy(xpath = "//label[contains(@id,'431400500')]/parent::div//a")
    WebElement operatorOptionDropdown;

    @FindBy(xpath = "//textarea[contains(@id,'431400600')]")
    WebElement WhereValueInput;

    @FindBy(xpath = "//button[normalize-space()='Create Job']")
    WebElement createJobButton;

    @FindBy(xpath = "//button[contains(@class,'dataset-picker-toggle')]")
    WebElement selectDatasetButton;

    @FindBy(xpath = "//input[contains(@id,'dataset_picker')]")
    WebElement datasetInput;

    @FindBy(xpath = "//label[normalize-space()='Qualification']/parent::div//div[@class='checkbox__item']")
    WebElement qualificationCheckbox;

    @FindBy(xpath = "//a[normalize-space()='Select Qualification Rulesets']")
    WebElement selectQualificationRulesetsButton;

    @FindBy(xpath = "//*[normalize-space()='OK']")
    WebElement okButton;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement saveButton;

    @FindBy(xpath = "//div[@class='loadingContainer']")
    WebElement loadingContainer;

    @FindBy(xpath = "//label[normalize-space()='MarkAsDeleted']//parent::div//input")
    WebElement markAsDeletedInput;

    @FindBy(xpath = "//a[contains(.,'Select Destination')]")
    WebElement selectDestinationButton;

    @FindBy(xpath = "//a[contains(.,'General')]")
    WebElement generalButton;

    @FindBy(xpath = "//button[@id='dropdown_activity_type']")
    WebElement activityTypeDropdown;

    @FindBy(xpath = "//input[contains(@name,'smartGraphSyncScheduleStatus')]/parent::*/parent::div")
    WebElement enableScheduleToggleButton;

    @FindBy(xpath = "//input[contains(@name,'smartGraphSyncScheduleStatus')]/parent::*/parent::div//input[@aria-checked='false']/parent::*/parent::div")
    WebElement ScheduleToggleButton;

    @FindBy(xpath = "//span[contains(text(),'Current time at server is')]")
    WebElement serverTimeText;

    @FindBy(xpath = "//input[contains(@id,'config_start_time')]")
    WebElement configStartTimeInput;

    @FindBy(xpath = "//button[normalize-space()='OK']")
    WebElement configOKButton;
    
    @FindBy(xpath = "//*[contains(@class, 'd-icon-search')]")
    WebElement searchIcon;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement configSaveButton;
    @FindBy(xpath = "//a[contains(@id,'dst_all_link')]")
	WebElement navigationToAllDatasetsLink;
	@FindBy(xpath = "//button[contains(@class,'dataset-picker')]")
	WebElement datasetPickerButton;
	@FindBy(xpath = "//input[contains(@id,'dataset_picker')]")
	WebElement datasetSearchBox;
	@FindBy(xpath = "//textarea[contains(@id,'desc')]")
	WebElement descriptionInputBoxLocator;
    @FindBy(xpath = "//input[contains(@id, 'dataset_picker_re')]")
    public WebElement reDatasetInput;
    @FindBy(xpath = "//button[contains(text(),'Create') or contains(text(),'New')]")
    public WebElement createButton;
    @FindBy(xpath = "//div[contains(@id,'jobNameLbl')]//input")
    public WebElement jobNameInput;
    @FindBy(xpath = "//button[contains(text(),'Add Activity')]")
    public WebElement addActivityButton;
    @FindBy(xpath = "//button[contains(@id,'dataset_picker_re_id_source_dropdown_menu_btn')]")
    public WebElement reSourceDatasetDropdown;
    @FindBy(xpath = "//input[contains(@id, 'dataset_picker_ne_create')]")
    WebElement neDatasetInput;
    @FindBy(xpath = "//button[contains(@class,'dataset') or contains(text(),'Dataset')]")
    WebElement datasetDropdown;

    @FindBy(xpath = "//div[contains(@id, 'totalJobs_value_box')]")
    WebElement totalJobsBox;

    @FindBy(xpath = "(//div[@class='imports-form']//div[@id='statusLbl']//span)[2]")
    WebElement atriumIntegratorJobStatusElement;

    @FindBy(xpath = "//div[@class='imports-form']//span[contains(text(),'Records Processed')]/following-sibling::span")
    WebElement recordsProcessedElement;

    @FindBy(xpath = "//label[normalize-space()='Successful CIs']/parent::div//span[contains(@class,'ng')]")
    WebElement SuccessfulCICount;

    @FindBy(xpath = "//label[normalize-space()='Successful Relationships']/parent::div//span[contains(@class,'ng')]")
    WebElement SuccessfulRelationshipCount;

    @FindBy(xpath = "//button[normalize-space()='Start Job']")
    WebElement normalizationStartJobButton;

    @FindBy(xpath = "//catalog-mapping//div[contains(@id, 'catalog_mappings_create_btn')]/button")
    WebElement catalogMappingCreateBtn;

    @FindBy(xpath = "//select[contains(@id, 'mapped_manufacturer')]")
    WebElement manufacturerDropdownBox;

    @FindBy(xpath = "//*[@id='explorer_edit_mode_switch']")
    WebElement editSwitchInput;

    @FindBy(xpath = "//*[@arid='300000300']")
    WebElement saveCIBtn;

    @FindBy(xpath = "//a[contains(normalize-space(.), 'New request')]")
    WebElement newRequestLink;

    @FindBy(xpath = "//*[@id='ne_dataset_config_edit_sel_norm_type_name_label']")
    WebElement radioBtnNameAndCITLookup;

    @FindBy(xpath = "//*[@id='ne_dataset_config_edit_sel_norm_type_cti_only']")
    WebElement radioBtnCITLookup;

    @FindBy(xpath = "//input[@id='checkbox_enable_normalization']")
    WebElement checkboxEnableNormalization;

    @FindBy(xpath = "//input[@id='checkbox_inline_normalization']")
    WebElement inlineEnableNormalization;

    @FindBy(xpath = "//input[@id='cx_datatable_ne_dataset_config_filter']")
    WebElement datasetFilterInput;

    @FindBy(xpath = "//*[@id='cx_datatable_ne_dataset_config_edit_btn']//button")
    WebElement datasetConfigEditButton;

    public CMDBDashboard() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(CMDBDashboard.class.getName());
        commonWaits=new CommonWaits();
        commonMethods = new CommonMethods();
    }

    public CMDBDashboard verifyLoginTo_CMDB() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForSometime(3000);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible(CMDB_verifyLogin, 40);
        Assert.assertTrue(CMDB_verifyLogin.isDisplayed(), "Login to CMDB is not successful");
        return this;
    }

    public String createCI(Map<Object, String> cmdbTestData){
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(CMDB_Menu, cmdbTestData.get("Menu"))), 30), "Menu "+cmdbTestData.get("Menu")+" is not visible");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CMDB_Menu, cmdbTestData.get("Menu")))).click();

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(CMDB_Menu_Item, cmdbTestData.get("createCI"))), 30), "Menu Item "+cmdbTestData.get("createCI")+" is not visible");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CMDB_Menu_Item, cmdbTestData.get("createCI")))).click();

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                select_Dataset, 30), "Select Dataset dropdown is not visible");
        commonWaits.waitForElementVisible(select_Dataset).click();

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                selectDatasetInput, 30), "Select Dataset input field is not visible");
        commonWaits.waitForElementVisible(selectDatasetInput).click();
        selectDatasetInput.sendKeys(cmdbTestData.get("dataSetValue"));

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                 By.xpath(String.format(dataset_Item, cmdbTestData.get("dataSetValue"))), 30), "Dataset "+cmdbTestData.get("dataSetValue")+" is not visible in dropdown");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dataset_Item, cmdbTestData.get("dataSetValue")))).click();

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                select_Class, 30), "Select Class dropdown is not visible");
        commonWaits.waitForElementVisible(select_Class).click();

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                selectClassInput, 30), "Select Class input field is not visible");
        commonWaits.waitForElementVisible(selectClassInput).click();
        selectClassInput.sendKeys(cmdbTestData.get("dataClassValue"));

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(class_Item, cmdbTestData.get("dataClassValue"))), 30), "Class "+cmdbTestData.get("dataClassValue")+" is not visible in dropdown");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(class_Item, cmdbTestData.get("dataClassValue")))).click();
        String CIName = cmdbTestData.get("CIName") +"_"+ RandomStringUtils.randomAlphanumeric(3);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                ci_Name_Input, 30), "CI Name input field is not visible");
        commonWaits.waitForElementVisible(ci_Name_Input).clear();
        ci_Name_Input.sendKeys(CIName);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                ci_Desc_Input, 30), "CI Description input field is not visible");
        commonWaits.waitForElementVisible(ci_Desc_Input).clear();
        ci_Desc_Input.sendKeys(cmdbTestData.get("CIDesc") +"_"+ RandomStringUtils.randomAlphanumeric(3));

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                 SaveCIBtn, 30), "Save CI button is not visible");
        commonWaits.waitForElementVisible(SaveCIBtn).click();;

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                topologyContainer, 40), "Topology is not visible");
        return CIName;
    }

    public CMDBDashboard searchCI(String searchCIAssetType, String searchCIByType, String ciName) {
        searchCIByAssetType.sendKeys(searchCIAssetType);
        searchCIInputByType.sendKeys( searchCIByType);
        searchCIInputValue.sendKeys(ciName);
        clickWithoutInterception(searchCIButton);
        return this;
    }

    public CMDBDashboard navigateToMenu(String Menu, String MenuItem, String SubMenuItem) {
    	WebElement menuItem = null;
    	log.info("Navigating to Menu: " + Menu + ", MenuItem: " + MenuItem + ", SubMenuItem: " + SubMenuItem);
    	
    	// Wait for all spinners to hide before starting navigation
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
    	
    	// Click on main menu with wait
    	By menuBy = By.xpath(String.format(CMDB_Menu, Menu));
    	commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), menuBy, 15);
    	WebElement menuElement = commonWaits.waitForByElementToBeVisible(menuBy);
    	javaScriptScrollIntoView(menuElement);
    	menuElement.click();
    	
    	// Wait for menu to expand
    	commonWaits.waitForExecutionComplete();
    	commonWaits.waitForSometime(commonWaits.moreDelay);
    	
        if (!SubMenuItem.isEmpty()){
            log.info("Hovering over SubMenuItem: " + SubMenuItem);
            By subMenuBy = By.xpath(String.format(CMDB_SubMenu_Item, SubMenuItem));
            commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), subMenuBy, 15);
            WebElement subMenu = commonWaits.waitForByElementToBeVisible(subMenuBy);
            (new Actions(getDriver())).moveToElement(subMenu).perform();
            commonWaits.waitForSometime(commonWaits.moreDelay);
        }
        
        try {
        	By menuItemBy = By.xpath(String.format(CMDB_Menu_Item, MenuItem));
        	commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), menuItemBy, 15);
        	menuItem = commonWaits.waitForByElementToBeVisible(menuItemBy);
        }
        catch(Exception e){
            log.info("Sub Menu Item is not visible, moving mouse to Sub Menu Item again");
            By subMenuBy = By.xpath(String.format(CMDB_SubMenu_Item, SubMenuItem));
        	WebElement subMenu = commonWaits.waitForByElementToBeVisible(subMenuBy);
        	(new Actions(getDriver())).moveToElement(subMenu).perform();
        	commonWaits.waitForSometime(commonWaits.moreDelay);
		}
        
        By menuItemBy = By.xpath(String.format(CMDB_Menu_Item, MenuItem));
        WebElement finalMenuItem = commonWaits.waitForByElementToBeVisible(menuItemBy);
        javaScriptScrollIntoView(finalMenuItem);
        clickWithoutInterception(finalMenuItem);
        
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        
        log.info("Successfully navigated to: " + MenuItem);
        return this;
    }

    public CMDBDashboard runReconciliationJob(String reconciliationJob) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        Assert.assertTrue(commonWaits.waitForVisibleElement(jobFilterSearchBox), "Job Filters are not available");
        try{
        clickWithoutInterception(jobFilterSearchBox);
        }
        catch(Exception e){
            commonWaits.waitForElementVisible(jobFilterSearchBox);
            commonWaits.clickByAction(jobFilterSearchBox);
        }
        jobFilterSearchBox.clear();
        jobFilterSearchBox.sendKeys(reconciliationJob);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(reconciliationJobLink, reconciliationJob)));
        Assert.assertTrue(we.isDisplayed(), reconciliationJob+" Job is not available");
        clickWithoutInterception(we);
        commonWaits.waitForElementVisible(reconciliationJobStatus);
        commonWaits.waitForElementClickable(startJobButton).click();
        getDriver().navigate().refresh();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), completedStatus, 60);
        Assert.assertTrue(reconciliationJobStatus.getText().contains("Completed"),"Job status is not Completed");
        refreshBrowser();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard validateAndClickSelfHelpIcon() {
        log.info("Step 4: Validating Self Help Icon (Gainsight KC Bot) is visible");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForSometime(6000);
        commonWaits.switchtoDefaultFrame();
        
        log.info("Validating Self Help Icon button is visible and clicking it");
        WebElement selfHelpButton = commonWaits.waitForByElementToBeVisible(By.xpath(gainsightSelfHelpIconButton));
        Assert.assertTrue(selfHelpButton.isDisplayed(), "Gainsight Self Help Icon button is not visible");
        clickWithoutInterception(selfHelpButton);
        log.info("Self Help Icon clicked successfully");
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard clickWhatsNewTour() {
        log.info("Clicking on 'What's new' tour button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(gainsightSelfHelpIconIframe));
        
        WebElement whatsNewButton = commonWaits.waitForByElementToBeVisible(By.xpath(whatsNewTourButton));
        clickWithoutInterception(whatsNewButton);
        log.info("'What's new' tour button clicked successfully");

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard validateWhatsNewTourPopupAndClose() {
        log.info("Validating 'What's new' tour popup is displayed");
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        WebElement popupTitle = commonWaits.waitForByElementToBeVisible(By.xpath(whatsNewTourPopupTitle));
        Assert.assertTrue(popupTitle.getText().contains("What's new"), "'What's new' title is not displayed in popup");
        log.info("'What's new' tour popup title validated: " + popupTitle.getText());

        log.info("Closing 'What's new' tour popup");
        WebElement closeButton = commonWaits.waitForByElementToBeVisible(By.xpath(whatsNewTourCloseButton));
        clickWithoutInterception(closeButton);
        log.info("'What's new' tour popup closed successfully");

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard clickDocumentationAndVideosTab() {
        log.info("Clicking on 'Documentation and videos' tab");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(gainsightSelfHelpIconIframe));
        
        WebElement docAndVideosButton = commonWaits.waitForByElementToBeVisible(By.xpath(documentationAndVideosButton));
        clickWithoutInterception(docAndVideosButton);
        log.info("'Documentation and videos' tab clicked successfully");
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard validateDocumentationAndVideosHeader() {
        log.info("Validating 'Documentation and videos' header is displayed");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        
        WebElement docHeader = commonWaits.waitForByElementToBeVisible(By.xpath(documentationAndVideosHeader));
        Assert.assertTrue(docHeader.isDisplayed(), "'Documentation and videos' header is not displayed");
        log.info("'Documentation and videos' header validated: " + docHeader.getText());
        return this;
    }

    public CMDBDashboard clickCMDBDocumentationLink() {
        log.info("Clicking on 'BMC Helix CMDB documentation' link");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        
        WebElement cmdbDocLink = commonWaits.waitForByElementToBeVisible(By.xpath(cmdbDocumentationLink));
        clickWithoutInterception(cmdbDocLink);
        log.info("'BMC Helix CMDB documentation' link clicked successfully");
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard validateCMDBDocumentationPageOpened(String expectedUrl) {
        log.info("Validating CMDB documentation page opened in new tab");
        commonWaits.waitForNumberWindowsToBe(3);
        commonWaits.switchToLastWindow();
        commonWaits.waitForURLContains(expectedUrl);
        
        String currentUrl = getDriver().getCurrentUrl();
        log.info("Current URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains(expectedUrl), "CMDB documentation page URL does not contain expected URL. Expected: " + expectedUrl + ", Actual: " + currentUrl);
        log.info("CMDB documentation page validated successfully");
        return this;
    }

    public CMDBDashboard closeSelfHelpIfOpened() {
        log.info("Closing Self Help Icon if opened");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        
        try {
            WebElement closeButton = findElement(By.xpath(selfHelpCloseButton));
            if (closeButton.isDisplayed()) {
                clickWithoutInterception(closeButton);
                log.info("Self Help Icon closed successfully");
            }
        } catch (Exception e) {
            log.info("Self Help Icon is not opened or not found. Continuing...");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard clickProfileMenu() {
        log.info("Clicking on Profile menu button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        
        WebElement profileButton = commonWaits.waitForByElementToBeVisible(By.xpath(profileMenuButton));
        clickWithoutInterception(profileButton);
        log.info("Profile menu button clicked successfully");
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard clickAnalyticsButton() {
        log.info("Clicking on Analytics button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        
        WebElement analyticsButton = commonWaits.waitForByElementToBeVisible(By.xpath(analyticsMenuButton));
        clickWithoutInterception(analyticsButton);
        log.info("Analytics button clicked successfully");
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard validateAnalyticsPopupAndClose() {
        log.info("Validating Analytics popup is displayed");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        
        WebElement popupTitle = commonWaits.waitForByElementToBeVisible(By.xpath(analyticsPopupTitle));
        Assert.assertTrue(popupTitle.isDisplayed(), "Analytics popup title is not displayed");
        log.info("Analytics popup title validated: " + popupTitle.getText());
        
        log.info("Clicking Cancel button to close Analytics popup");
        WebElement cancelButton = commonWaits.waitForByElementToBeVisible(By.xpath(analyticsCancelButton));
        clickWithoutInterception(cancelButton);
        log.info("Analytics popup closed successfully");
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard addRuleSet(String ruleSetName) {
        log.info("Adding Qualification Rule");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        clickWithoutInterception(addRuleSetButton);
        commonWaits.waitForElementVisible(ruleSetNameInput);
        ruleSetNameInput.sendKeys(ruleSetName);
        clickWithoutInterception(saveRuleSetButton);
        return this;
    }   

    public boolean validateRuleSetAdded(String ruleSetName) {
        log.info("Validating Qualification Rule is added");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        for (WebElement RuleSetName : AddedRuleSetName) {
            if (RuleSetName.getText().equals(ruleSetName)) {
                return true;
            }
        }
        return false;
    }

    public CMDBDashboard addQualificationRule(String ruleName, String qualificationValue, String className) {
        log.info("Adding Qualification Rule");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        clickWithoutInterception(addQualificationRuleButton);
        commonWaits.waitForElementVisible(ruleNameInput);
        ruleNameInput.sendKeys(ruleName);
        commonWaits.waitForElementVisible(classNameDropdown);
        clickWithoutInterception(classNameDropdown);
        commonWaits.waitForElementVisible(classNameInput);
        classNameInput.sendKeys(className);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(class_Name, className))).click();
        commonWaits.waitForElementVisible(qualificationTextBox);
        qualificationTextBox.sendKeys(qualificationValue);
        clickWithoutInterception(saveQualificationRuleButton);
        return this;
    }

    public CMDBDashboard addQualificationRuleAndSave(String ruleSetName, String ruleName, String qualificationValue, String className) {
        if (validateRuleSetAdded(ruleSetName)==false) {
            addRuleSet(ruleSetName);
            addQualificationRule(ruleName, qualificationValue, className);
        } else {
            log.info("Rule Set "+ruleSetName+" is already added");
        }
        return this;
    }
    public CMDBDashboard enableSmartGraphSyncSchedule() {
        log.info("Enabling SmartGraph Sync Schedule");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        
        if (commonWaits.isElementPresent(ScheduleToggleButton)) {
            log.info("Schedule Toggle Button is disabled, enabling it");
            clickWithoutInterception(enableScheduleToggleButton);
        }
        return this;
    }

    public CMDBDashboard addDatasetAndSave(String datasetName, String datasetID, String permission1, String permission2) {
        log.info("Adding Dataset");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
        clickWithoutInterception(addDatasetButton);
        commonWaits.waitForElementVisible(datasetNameInput);
        datasetNameInput.sendKeys(datasetName);
        commonWaits.waitForElementVisible(datasetIDInput);
        datasetIDInput.sendKeys(datasetID);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), permissionsInput, 30 ), "Permissions input field is not present");
        typeKeys(permissionsInput, permission1);
 
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(permissionNameCheckbox, permission1)), 30),
                "Permission checkbox for "+permission1+" is not present");
        WebElement checkboxPermission1 = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(permissionNameCheckbox, permission1)));
        commonWaits.javaScriptScrollIntoView(checkboxPermission1);
        commonWaits.clickWithoutInterception(checkboxPermission1);
        if(permission2 != null && !permission2.isEmpty()){
            typeKeys(permissionsInput, permission2);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(permissionNameCheckbox, permission2)), 30),
                    "Permission checkbox for "+permission2+" is not present");
            WebElement checkboxPermission2 = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(permissionNameCheckbox, permission2)));
            commonWaits.javaScriptScrollIntoView(checkboxPermission2);
            commonWaits.clickWithoutInterception(checkboxPermission2);
        }
        commonWaits.waitForElementClickable(saveDatasetButton);
        clickWithoutInterception(saveDatasetButton);
        return this;
    }

    public CMDBDashboard clickOnNewRequest() {
        commonWaits.clickWithoutInterception(newRequestButton);
        return this;
    }

    public CMDBDashboard setTextField(String fieldName, String value) {
        WebElement textFieldElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textField, fieldName)));
        textFieldElement.clear();
        textFieldElement.sendKeys(value);
        return this;
    }

    public void setDropDownField(String labelName, String value) {
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownField, labelName))).click();
            commonWaits.waitForSometime(commonWaits.minDelay); // Wait for dropdown options to load
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, value))).click();
            log.info("Set dropdown field " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public CMDBDashboard createCIInComputerSystemForm(Map<Object, String> cmdbTestData) {
        refreshBrowser();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementVisible(newRequestLink).click();
        setTextField(SolQAConstants.NAME, cmdbTestData.get("CIName"));
        setTextField(SolQAConstants.DATASETID, cmdbTestData.get("DatasetID"));
        commonWaits.waitForSometime(commonWaits.moreDelay);
        if (cmdbTestData.get("CategoryLabel") != null && cmdbTestData.get("discoveryCategoryValue") != null) {
            setTextField(cmdbTestData.get("CategoryLabel"), cmdbTestData.get("discoveryCategoryValue"));
        }
        if (cmdbTestData.get("TypeLabel") != null && cmdbTestData.get("discoveryTypeValue") != null) {
            setTextField(cmdbTestData.get("TypeLabel"), cmdbTestData.get("discoveryTypeValue"));
        }
        if (cmdbTestData.get("ItemLabel") != null && cmdbTestData.get("discoveryItemValue") != null) {
            setTextField(cmdbTestData.get("ItemLabel"), cmdbTestData.get("discoveryItemValue"));
        }
        if (cmdbTestData.get("ModelLabel") != null) {
            setTextField(cmdbTestData.get("ModelLabel"), cmdbTestData.get("ProductNameValue"));
        }
        if (cmdbTestData.get("ManufacturerLabel") != null && cmdbTestData.get("discoveryManufacturerValue") != null) {
            setTextField(cmdbTestData.get("ManufacturerLabel"), cmdbTestData.get("discoveryManufacturerValue"));
        }
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementClickable(saveCIBtn, 10).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        return this;
    }

    public CMDBDashboard createMultipleCIs(Map<Object, String> cmdbTestData, int numberOfCIs){
        clickOnNewRequest();
        //This method starts CI with 1 so please enter numberOfCIs count as n+1
        for (int i = 1; i < numberOfCIs; i++) {
            Map<Object, String> ciData = new HashMap<>(cmdbTestData);
            String baseCIName = cmdbTestData.get("CIName");
            ciData.put("CIName", baseCIName + i);
            createCIInComputerSystemForm(ciData);
    
        }
        return this;
    }

    public CMDBDashboard createRelationshipInBaseRelationshipForm(Map<Object, String> cmdbTestData){
       
        commonWaits.switchToLastWindow();
        setDropDownField(SolQAConstants.NAMESPACE, cmdbTestData.get("Namespace"));
        setDropDownField(SolQAConstants.CLASS_NAME, cmdbTestData.get("RelationshipClassName"));
      try {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownField, SolQAConstants.WHERE))).click();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        Actions actions = new Actions(getDriver());
        actions.sendKeys("n").perform(); 
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, cmdbTestData.get("Where")))).click();
        log.info("Set dropdown field " + SolQAConstants.WHERE + " to " + cmdbTestData.get("Where"));
    } catch (Exception e) {
        log.error("Failed to set dropdown field for " + SolQAConstants.WHERE + ": " + e.getMessage());
        throw e;
    }
        try {
            commonWaits.clickWithoutInterception(OperatorDropdown);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(OperatorDropdownOptions, cmdbTestData.get("Operator")))).click();
                log.info("Set Operator dropdown field to " + cmdbTestData.get("Operator"));
        } catch (Exception e) {
            log.error("Failed to set Operator dropdown field to " + cmdbTestData.get("Operator") + ": " + e.getMessage());
            throw e;
        }

        try {
            commonWaits.clickWithoutInterception(operatorOptionDropdown);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(OperatorDropdownOptions, cmdbTestData.get("WhereOption")))).click();
                log.info("Set Operator dropdown field to " + cmdbTestData.get("WhereOption"));
        } catch (Exception e) {
            log.error("Failed to set Operator dropdown field to " + cmdbTestData.get("WhereOption") + ": " + e.getMessage());
            throw e;
        }
        commonWaits.waitForElementVisible(WhereValueInput);
        WhereValueInput.click();
        WhereValueInput.sendKeys(cmdbTestData.get("WhereValue"));
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.clickByText(SolQAConstants.OK);
        return this;
    }

    public CMDBDashboard createMultipleRelationships(Map<Object, String> cmdbTestData, int numberOfRelationships){
        clickOnNewRequest();
    
        int counter = 1; // starts from 1
    
        for (int i = 1; i <= numberOfRelationships; i++) {
    
            setTextField(SolQAConstants.NAME, cmdbTestData.get("RelationshipName") + "_" + i);
            setTextField(SolQAConstants.DATASETID, cmdbTestData.get("DatasetID"));
    
            // -------- SOURCE (1, 3, 5, ...)
            Map<Object, String> sourceData = new HashMap<>(cmdbTestData);
            sourceData.put("WhereValue", cmdbTestData.get("WhereValue") + counter);
    
            commonWaits.clickByText(SolQAConstants.SELECT_SOURCE);
            createRelationshipInBaseRelationshipForm(sourceData);
    
            counter++; // move to next number
    
            // -------- DESTINATION (2, 4, 6, ...)
            Map<Object, String> destinationData = new HashMap<>(cmdbTestData);
            destinationData.put("WhereValue", cmdbTestData.get("WhereValue") + counter);
    
            commonWaits.switchToLastWindow();
            commonWaits.waitForElementVisible(selectDestinationButton).click();
            createRelationshipInBaseRelationshipForm(destinationData);
    
            counter++; // prepare for next loop
            commonWaits.switchToLastWindow();
            commonWaits.waitForElementVisible(generalButton).click();
            setDropDownField(SolQAConstants.MARKASDELETED, cmdbTestData.get("MarkAsDeleted"));
            commonWaits.clickByText(SolQAConstants.RELATIONSHIP);
            commonWaits.clickByText(SolQAConstants.SAVE);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            commonWaits.switchtoDefaultFrame();
            commonWaits.switchToFrame(OkPopUpIframe);
            commonWaits.clickByText(SolQAConstants.OK);
            commonWaits.switchtoDefaultFrame();
            commonWaits.waitForSometime(commonWaits.moreDelay);
        }
        return this;
    }

    public CMDBDashboard clickOnCreateJob() {
        commonWaits.clickWithoutInterception(createJobButton);
        return this;
    }

    public CMDBDashboard setTextFieldInJobCreationForm(String fieldName, String value) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(NameInputField, fieldName))).sendKeys(value);
        return this;
    }

    public CMDBDashboard setDescriptionFieldInJobCreationForm(String fieldName, String value) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DescriptionInputField, fieldName))).sendKeys(value);
        return this;
    }

    public CMDBDashboard addActivityInJobCreationForm(String fieldName) {
        commonWaits.clickWithoutInterception(addActivityButton);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(activityOptionDropdown, fieldName))).click();
        return this;
    }

    public CMDBDashboard selectDatasetInActivityForm(String fieldName, String value) {
        commonWaits.clickWithoutInterception(selectDatasetButton);
        commonWaits.waitForElementVisible(datasetInput);
        datasetInput.clear();
        datasetInput.sendKeys(value);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(datasetOptionDropdown, value))).click();
        log.info("Selected dataset " + value + " in activity form");
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public CMDBDashboard deselectTheQualificationCheckboxInJobCreationForm() {
        log.info("Deselecting the qualification checkbox in activity form");
        commonWaits.waitForElementVisible(qualificationCheckbox);
        commonWaits.clickWithoutInterception(qualificationCheckbox);
        if(!commonWaits.isElementPresent(selectQualificationRulesetsButton)) {
            commonWaits.clickWithoutInterception(qualificationCheckbox);
        }
            log.info("Deselected the qualification checkbox in activity form");
        return this;
    }

    public CMDBDashboard clickOnSelectQualificationRulesetsButton(String value) {
        commonWaits.clickWithoutInterception(selectQualificationRulesetsButton);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(qualificationRulesetOption, value))).click();
        log.info("Selected qualification ruleset " + value + " in activity form");
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.clickWithoutInterception(okButton);
        log.info("Clicked on OK button in activity form");
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public CMDBDashboard clickOnSaveButtonInJobCreationForm() {
        commonWaits.clickWithoutInterception(saveButton);
        return this;
    }

    public CMDBDashboard selectActivityType(String value) {
        commonWaits.clickWithoutInterception(activityTypeDropdown);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(activityTypeOption, value))).click();
        log.info("Selected activity type " + value + " in activity form");
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public CMDBDashboard navigateToTotalJobs() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible(totalJobs);
        commonWaits.scrollToElement(totalJobs);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementClickable(totalJobs).click();
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public boolean checkIfJobIsCreated(String jobName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        Assert.assertTrue(commonWaits.waitForVisibleElement(jobFilterSearchBox), "Job Filters are not available");
        clickWithoutInterception(jobFilterSearchBox);
        jobFilterSearchBox.sendKeys(jobName);
        return commonWaits.isElementPresent(By.xpath(String.format(reconciliationJobLink, jobName)));
    }
   
    public CMDBDashboard setSmartGraphSyncSchedule() {
        log.info("Setting SmartGraph Sync Schedule");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.switchtoDefaultFrame();
       
        String fullText = serverTimeText.getText();
        log.info("Full Text: " + fullText);
        String time = fullText.replaceAll(".*?(\\d{1,2}:\\d{2}:\\d{2}\\s?(AM|PM)).*", "$1");
        log.info("Extracted Time: " + time);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");

        LocalTime updatedTime = LocalTime.parse(time, inputFormatter).plusMinutes(2);

         String finalTime = updatedTime.format(outputFormatter);
         log.info("Time after adding 2 minutes: " + finalTime);
    
        configStartTimeInput.clear();
        configStartTimeInput.sendKeys(finalTime);
        clickWithoutInterception(configSaveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }
    
    public String createNormalizationJob(Map<Object, String> cmdbTestData) {
		log.info("Creating Normalization Job");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

		// Click Create button
		clickWithoutInterception(createButton);
		commonWaits.waitForExecutionComplete();

		// Enter Job Name
		String jobName = uniquify(cmdbTestData.get("NormalizationJobName"));
		jobNameInput.clear();
		jobNameInput.sendKeys(jobName);

		// Select Dataset
		clickWithoutInterception(datasetDropdown);
		clickWithoutInterception(neDatasetInput);
		neDatasetInput.sendKeys(cmdbTestData.get("dataSetValue"));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(normaliaztionSelectDatasetLocator, cmdbTestData.get("dataSetValue")))).click();

		// Select Schedule type - Daily
		clickWithoutInterception(By.xpath(String.format(schduleTypeLocator, cmdbTestData.get("ScheduleType"), cmdbTestData.get("ScheduleType"))));

		// Save the job
		clickWithoutInterception(saveButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

		log.info("Normalization Job created successfully: " + jobName);
		return jobName;
	}

	public String createReconciliationJob(Map<Object, String> cmdbTestData) {
		log.info("Creating Reconciliation Job");

		// Click Create button
		javaScriptScrollIntoView(createButton);
		clickWithoutInterception(createButton);
		commonWaits.waitForExecutionComplete();

		// Enter Job Name
		String jobName = uniquify(cmdbTestData.get("ReconciliationJobName"));
		commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), jobNameInputBy, 15);
		javaScriptScrollIntoView(jobNameInput);
		jobNameInput.clear();
		jobNameInput.sendKeys(jobName);
		
		// Add Activity - Identity
		log.info("Waiting for Add Activity button to be visible");
		commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), addActivityButtonBy, 50);
		javaScriptScrollIntoView(addActivityButton);
		clickWithoutInterception(addActivityButton);
		commonWaits.waitForExecutionComplete();
		By activityOptionBy = By.xpath(String.format(buttonTextLocator, cmdbTestData.get("ReconciliationActivity")));
		WebElement activityOption = commonWaits.waitForByElementToBeVisible(activityOptionBy);
		javaScriptScrollIntoView(activityOption);
		clickWithoutInterception(activityOption);

        // Select Source Dataset
        javaScriptScrollIntoView(reSourceDatasetDropdown);
        clickWithoutInterception(reSourceDatasetDropdown);
        clickWithoutInterception(reDatasetInput);
        reDatasetInput.sendKeys(cmdbTestData.get("dataSetValue"));
        By datasetOptionBy = By.xpath(String.format(reconciliationSelectDatasetLocator, cmdbTestData.get("dataSetValue")));
        commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), datasetOptionBy, 15);
        WebElement datasetOption = commonWaits.waitForByElementToBeVisible(datasetOptionBy);
        javaScriptScrollIntoView(datasetOption);
        clickWithoutInterception(datasetOption);

        // Save the job
        javaScriptScrollIntoView(saveButton);
        clickWithoutInterception(saveButton);

	log.info("Reconciliation Job created successfully: " + jobName);
	return jobName;
	}

	public boolean verifyJobCreated(String jobName) {
		log.info("Verifying job created: " + jobName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		return commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(linkContainsText, jobName)), 20);
	}
	
	public CMDBDashboard navigateToAllDatasets() {
		clickWithoutInterception(navigationToAllDatasetsLink);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		return this;
	}
	
	public String createNewDataset(Map<Object, String> cmdbTestData) {
		String datasetName=uniquify(cmdbTestData.get("DatasetName"));
		clickWithoutInterception(By.xpath(String.format(buttonTextLocator, SolQAConstants.ADD_DATASET)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		fillDetails(findElement(By.xpath(String.format(inputContainsIdLocator, SolQAConstants.name.toLowerCase()))), datasetName);
		fillDetails(findElement(By.xpath(String.format(inputContainsIdLocator, datasetIDVariable))),datasetName);
		selectPermission(SolQAConstants.GROUPS, cmdbTestData.get("PermissionValues"));
		saveDatasetPermission();
		return datasetName;
	}
	
	public CMDBDashboard fillDetails(WebElement element, String value) {
		clickWithoutInterception(element);
		element.clear();
		element.sendKeys(value);
		return this;
	}
	
	public CMDBDashboard selectPermission(String permissionType, String values) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		clickWithoutInterception(By.xpath(String.format(buttonTextLocator, permissionType)));
		clickWithoutInterception(By.xpath(String.format(inputContainsIdLocator, permissionFilterSearchBoxVariable)));
		if(values.contains(",")) {
			String[] svalues = values.split(",");
			if(svalues.length>0) {
				for(String value: svalues) {
					fillDetails(findElement(By.xpath(String.format(inputContainsIdLocator, permissionFilterSearchBoxVariable))), value);
					commonWaits.waitForElementInvisible(findElement(By.xpath(String.format(selectPermissionLocator, value))));
					if(findElement(By.xpath(String.format(selectPermissionLocator, value))).getAttribute(SolQAConstants.ATTRIBUTE_CHECKED)==null || findElement(By.xpath(String.format(selectPermissionLocator, value))).getAttribute(SolQAConstants.ATTRIBUTE_CHECKED).equalsIgnoreCase(SolQAConstants.FALSE)) {
						findElement(By.xpath(String.format(selectPermissionLocator, value))).click();
					} else {
						log.info(value+" Permission is already selected");
					}
				}
			}
		} else {
			fillDetails(findElement(By.xpath(String.format(inputContainsIdLocator, permissionFilterSearchBoxVariable))), values);
			commonWaits.waitForElementInvisible(findElement(By.xpath(String.format(selectPermissionLocator, values))));
			if(findElement(By.xpath(String.format(selectPermissionLocator, values))).getAttribute(SolQAConstants.ATTRIBUTE_CHECKED)==null || findElement(By.xpath(String.format(selectPermissionLocator, values))).getAttribute(SolQAConstants.ATTRIBUTE_CHECKED).equalsIgnoreCase(SolQAConstants.FALSE)) {
				findElement(By.xpath(String.format(selectPermissionLocator, values))).click();;
			} else {
				log.info(values+" Permission is already selected");
			}
		}
		return this;
	}
	
	public CMDBDashboard saveDatasetPermission() {
		clickWithoutInterception(By.xpath(String.format(buttonTextLocator, SolQAConstants.SAVE)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		return this;
	}
	
	public CMDBDashboard searchDataset(String datasetName) {
		clickWithoutInterception(datasetPickerButton);
		datasetSearchBox.clear();
		datasetSearchBox.sendKeys(datasetName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		clickWithoutInterception(By.xpath(String.format(datasetLocator, datasetName)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		return this;
	}
	
	public CMDBDashboard verifyDataPresent(String datasetName) {
		Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(datasetNameLocator, datasetName))), datasetName+" Dataset is not available");
		return this;
	}

	public CMDBDashboard verifyDataSourcePresent(String datasetName) {
	    String xpath = String.format(datasetSourceNameLocator, datasetName);
	    if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(xpath), 30)) {
	        log.info("Datasource not found on first attempt, refreshing page and retrying...");
	        getDriver().navigate().refresh();
	        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
	        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(xpath), 30),
	                datasetName + " Dataset is not available");
	    }
	    return this;
	}
 
	
	public String createNewDatasource(Map<Object, String> cmdbTestData, String associatedDataset) {
		String datasourceName=uniquify(cmdbTestData.get("DatasourceName"));
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonTextLocator, SolQAConstants.ADD_DATASOURCE)) , 30 ), "Button Text is not visible");
		commonWaits.clickWithoutInterception(By.xpath(String.format(buttonTextLocator, SolQAConstants.ADD_DATASOURCE)));
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
		fillDetails(findElement(By.xpath(String.format(inputContainsIdLocator, SolQAConstants.name.toLowerCase()))), datasourceName);
		fillDetails(descriptionInputBoxLocator, cmdbTestData.get("DatasourceDescription"));
		WebElement providerTypeDropdown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(cmdbDropdown, cmdbTestData.get("DatasourceProviderType"))));
		providerTypeDropdown.click();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(cmdbDropdownItem, cmdbTestData.get("ProviderType")))).click();
		
		if(associatedDataset!=null && !associatedDataset.isEmpty()) {
			
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonIdContainsLocator, datasourceVariable)) , 30 ), "Datasource Value is not visible");
			commonWaits.clickWithoutInterception(By.xpath(String.format(buttonIdContainsLocator, datasourceVariable)));
			
			fillDetails(findElement(By.xpath(String.format(inputContainsIdLocator, datasourceFilterVariable))), associatedDataset);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
			
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(datasourceOptionLocator, associatedDataset)) , 30 ), "Datasource Value is not visible");
			commonWaits.clickWithoutInterception(By.xpath(String.format(datasourceOptionLocator, associatedDataset)));
		}
		
		saveDatasetPermission();
		return datasourceName;
	}

    /**
     * Verify if a specific Atrium Integrator job is present in the job list
     * @param jobName Name of the job to verify
     * @return true if job is present, false otherwise
     */
    public boolean isAtriumIntegratorJobPresent(String jobName) {
        log.info("Verifying if Atrium Integrator job is present: " + jobName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        Assert.assertTrue(commonWaits.waitForVisibleElement(jobFilterSearchBox), "Job Filters are not available");
        clickWithoutInterception(jobFilterSearchBox);
        jobFilterSearchBox.clear();
        jobFilterSearchBox.sendKeys(jobName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        boolean isPresent = commonWaits.isElementPresent(By.xpath(String.format(atriumIntegratorJobLink, jobName)));
        log.info("Job " + jobName + " present: " + isPresent);
        return isPresent;
    }

    /**
     * Execute Atrium Integrator job and wait for completion
     * @param jobName Name of the job to execute
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard runAtriumIntegratorJob(String jobName) {
        log.info("Running Atrium Integrator job: " + jobName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        Assert.assertTrue(commonWaits.waitForVisibleElement(jobFilterSearchBox), "Job Filters are not available");
        clickWithoutInterception(jobFilterSearchBox);
        jobFilterSearchBox.clear();
        jobFilterSearchBox.sendKeys(jobName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(atriumIntegratorJobLink, jobName)));
        Assert.assertTrue(we.isDisplayed(), jobName + " Job is not available");
        clickWithoutInterception(we);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementClickable(startJobButton).click();
        log.info("Job started: " + jobName);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), completedStatus, 120);
        commonWaits.waitForElementVisible(atriumIntegratorJobStatusElement);
        Assert.assertTrue(atriumIntegratorJobStatusElement.getText().contains("Completed"), "Job status is not Completed");
        log.info("Job completed successfully: " + jobName);
        refreshBrowser();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    /**
     * Get the number of records processed after job execution
     * @return Number of records processed as String
     */
    public String getRecordsProcessedCount() {
        log.info("Getting records processed count");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        String count = recordsProcessedElement.getText();
        log.info("Records processed: " + count);
        return count;
    }

    /**
     * Search for CI in CMDB using class name, attribute, and device name
     * @param className Class name to search (e.g., BMC.CORE:BMC_ComputerSystem)
     * @param attributeName Attribute name to search by (e.g., Name)
     * @param deviceName Device name to search for
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard searchCIByClassAndAttribute(String className, String attributeName, String deviceName) {
        log.info("Searching CI with class: " + className + ", attribute: " + attributeName + ", device: " + deviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        
        // Enter class name
        commonWaits.waitForElementVisible(searchCIByAssetType).clear();
        searchCIByAssetType.sendKeys(className);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        
        // Enter attribute name
        commonWaits.waitForElementVisible(searchCIInputByType).clear();
        searchCIInputByType.sendKeys(attributeName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        
        // Enter device name
        commonWaits.waitForElementVisible(searchCIInputValue).clear();
        searchCIInputValue.sendKeys(deviceName);
        
        // Click search button
        clickWithoutInterception(searchCIButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        log.info("CI search completed");
        return this;
    }
  
    /**
     * Verify if CI search results are displayed
     * @param deviceName Device name to verify in results
     * @return true if results are displayed, false otherwise
     */
    public boolean isCISearchResultDisplayed(String deviceName) {
        log.info("Verifying CI search results for device: " + deviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        boolean isDisplayed = commonWaits.isElementPresent(By.xpath(String.format(ciSearchResultButton, deviceName)));
        log.info("CI search result displayed: " + isDisplayed);
        return isDisplayed;
    }

    /**
     * Select search type from Quick Search dropdown
     * @param searchType Search type (e.g., "Quick Search", "Computers", "Services", "Custom")
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard selectSearchType(String searchType) {
        log.info("Selecting search type: " + searchType);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementVisible(quickSearchDropdown);
        quickSearchDropdown.click();
        WebElement searchOption = commonWaits.waitForByElementToBeVisible(By.xpath("//option[text()='" + searchType + "']"));
        clickWithoutInterception(searchOption);
        log.info("Search type selected: " + searchType);
        return this;
    }

    /**
     * Enter dataset value in the search panel
     * @param dataset Dataset name (e.g., "BMC.ASSET")
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard enterDataset(String dataset) {
        log.info("Entering dataset: " + dataset);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementVisible(datasetInputField).clear();
        datasetInputField.sendKeys(dataset);
        log.info("Dataset entered: " + dataset);
        return this;
    }

    /**
     * Enter class name in the search panel
     * @param className Class name (e.g., "BMC.CORE:BMC_ComputerSystem")
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard enterClassName(String className) {
        log.info("Entering class name: " + className);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementVisible(classInputField).clear();
        classInputField.sendKeys(className);
        log.info("Class name entered: " + className);
        return this;
    }

    /**
     * Enter attribute name in the search panel
     * @param attributeName Attribute name (e.g., "Name")
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard enterAttributeName(String attributeName) {
        log.info("Entering attribute name: " + attributeName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementVisible(attributeInputField).clear();
        attributeInputField.sendKeys(attributeName);
        log.info("Attribute name entered: " + attributeName);
        return this;
    }

    /**
     * Enter attribute value in the search panel
     * @param value Attribute value to search (e.g., "Optiplex")
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard enterAttributeValue(String value) {
        log.info("Entering attribute value: " + value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementVisible(attributeValueField).clear();
        attributeValueField.sendKeys(value);
        log.info("Attribute value entered: " + value);
        return this;
    }

    /**
     * Click the search button in the search panel
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard clickSearchButton() {
        log.info("Clicking search button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        commonWaits.waitForElementClickable(searchButton);
        clickWithoutInterception(searchButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        log.info("Search button clicked");
        return this;
    }

    /**
     * Perform complete CI search with all parameters
     * @param dataset Dataset name (e.g., "BMC.ASSET")
     * @param className Class name (e.g., "BMC.CORE:BMC_ComputerSystem")
     * @param attributeName Attribute name (e.g., "Name")
     * @param attributeValue Value to search for
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard performCISearch(String dataset, String className, String attributeName, String attributeValue) {
        log.info("Performing CI search - Dataset: " + dataset + ", Class: " + className + ", Attribute: " + attributeName + ", Value: " + attributeValue);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        
        enterDataset(dataset);
        enterClassName(className);
        enterAttributeName(attributeName);
        enterAttributeValue(attributeValue);
        clickSearchButton();
        
        log.info("CI search completed");
        return this;
    }

    /**
     * Check if asset is present in search results by name
     * @param assetName Name of the asset to search for
     * @return true if asset is found in results, false otherwise
     */
    public boolean isAssetPresentInResults(String assetName) {
        log.info("Checking if asset is present in search results: " + assetName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        boolean isPresent = commonWaits.isElementPresent(By.xpath(String.format(searchResultByName, assetName)));
        log.info("Asset " + assetName + " present in results: " + isPresent);
        return isPresent;
    }

    /**
     * Click on a search result row by asset name
     * @param assetName Name of the asset to click
     * @return CMDBDashboard instance for method chaining
     */
    public CMDBDashboard clickSearchResult(String assetName) {
        log.info("Clicking on search result: " + assetName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        WebElement resultElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchResultByName, assetName)));
        clickWithoutInterception(resultElement);
        log.info("Search result clicked: " + assetName);
        return this;
    }

    /**
     * Get the search results count text
     * @return Results count text (e.g., "0 Selected/ 7 Total")
     */
    public String getSearchResultsCount() {
        log.info("Getting search results count");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        String count = searchResultsCount.getText();
        log.info("Search results count: " + count);
        return count;
    }

    /**
     * Verify if search returned any results
     * @return true if results exist, false otherwise
     */
    public boolean hasSearchResults() {
        log.info("Checking if search has results");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCMDB);
        String resultsText = getSearchResultsCount();
        boolean hasResults = !resultsText.contains("0 Total");
        log.info("Search has results: " + hasResults);
        return hasResults;
    }

    public CMDBDashboard validateJobRunStatistics(Map<Object, String> cmdbTestData) {
        int maxRetries = 4;
        int attempt = 0;
        while (attempt < maxRetries) {
            getDriver().navigate().refresh();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
            commonWaits.waitForElementVisible(SuccessfulCICount);
            commonWaits.waitForSometime(commonWaits.minDelay); //this wait is required to avoid the stale element exception
            String ciCount = SuccessfulCICount.getText();
            if (!ciCount.contains("NA")) {
                break; // exit loop if valid value found
            }
            log.info("Attempt " + (attempt + 1) + ": Successful CI count is NA, refreshing page...");
            attempt++;
        }
        // Final validation after retries
        Assert.assertEquals(SuccessfulCICount.getText(),cmdbTestData.get("SuccessfulCICount"),"Successful CI count is not correct");
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(SuccessfulRelationshipCount);
    
        int actualCount = Integer.parseInt(SuccessfulRelationshipCount.getText().trim());
        int expectedMinCount = Integer.parseInt(cmdbTestData.get("SuccessfulRelationshipCount"));

        Assert.assertTrue(actualCount >= expectedMinCount,"Successful Relationship count is less than expected. Actual: "+ actualCount + ", Expected minimum: " + expectedMinCount);
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public CMDBDashboard navigateToCMDBURLInNewOpenedTab() {
		getDriver().get(Launcher.getCMDBURL());
		return this;
	}

    public CMDBDashboard enableEditMode() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible(topologyContainer);
        try {
            if (commonWaits.isElementPresent(editSwitchInput, 3)) {
                commonWaits.waitForElementVisible(editSwitchInput);
                String checked = editSwitchInput.getAttribute("aria-checked");
                if (checked == null) {
                    checked = editSwitchInput.getAttribute("checked");
                }
                if (checked != null && (checked.equalsIgnoreCase("true") || checked.equalsIgnoreCase("checked"))) {
                    return this;
                }
            }
        } catch (Exception ignored) {
        }
        commonWaits.clickWithoutInterception(explorerEditBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard rightClickOnCINode() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible(ciName);
        Actions actions = new Actions(getDriver());
        actions.contextClick(ciName).perform();
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public CMDBDashboard clickOnOptionFromRightClickOfCI(String optionText) {
        Assert.assertNotNull(optionText, "Option text is null");
        Assert.assertFalse(optionText.isEmpty(), "Option text is empty");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CIOption, optionText)));
        commonWaits.clickWithoutInterception(we);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard verifyMarkAsDelete() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible(ciName);
        String opacity = ciName.getCssValue("opacity");
        String klass = ciName.getAttribute("class");
        String textDecoration = ciName.getCssValue("text-decoration");
        String style = ciName.getAttribute("style");
        log.info("Mark As Delete check - opacity: " + opacity + ", class: " + klass + ", text-decoration: " + textDecoration + ", style: " + style);
        boolean blurred = false;
        try {
            if (opacity != null && !opacity.trim().isEmpty()) {
                blurred = Double.parseDouble(opacity.trim()) < 1.0;
            }
        } catch (Exception ignored) {
        }
        if (!blurred && klass != null) {
            String c = klass.toLowerCase();
            blurred = c.contains("deleted") || c.contains("blur") || c.contains("mark") || c.contains("line-through");
        }
        if (!blurred && textDecoration != null) {
            blurred = textDecoration.toLowerCase().contains("line-through");
        }
        if (!blurred && style != null) {
            blurred = style.toLowerCase().contains("opacity") || style.toLowerCase().contains("line-through");
        }
        Assert.assertTrue(blurred, "CI label is not blurred/marked as deleted on CMDB topology after Mark As Delete");
        return this;
    }

    public CMDBDashboard clickMidTierButtonByText(String buttonText) {
        Assert.assertNotNull(buttonText, "Button text is null");
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickByText(buttonText.trim());
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public CMDBDashboard setMidTierFieldValueById(String fieldId, String value) {
        Assert.assertNotNull(fieldId, "Field id is null");
        Assert.assertNotNull(value, "Value is null");
        String id = fieldId.trim();
        Assert.assertFalse(id.isEmpty(), "Field id is empty");

        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        List<By> candidates = Arrays.asList(
                By.id("arid_WIN_0_" + id),
                By.cssSelector("#WIN_0_" + id + " input"),
                By.cssSelector("#WIN_0_" + id + " textarea"),
                By.id("ar" + id),
                By.id(id),
                By.cssSelector("#ar" + id + " input"),
                By.cssSelector("#ar" + id + " textarea"),
                By.cssSelector("#" + id + " input"),
                By.cssSelector("#" + id + " textarea")
        );

        WebElement input = null;
        for (By by : candidates) {
            if (commonWaits.isElementPresent(by, 2)) {
                input = commonWaits.waitForByElementToBeVisible(by);
                break;
            }
        }
        if (input == null) {
            throw new AssertionError("Mid-tier field not found for id: " + id);
        }
        try {
            input.clear();
        } catch (Exception ignored) {
        }
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public String getMidTierFieldValueById(String fieldId) {
        if (fieldId == null || fieldId.trim().isEmpty()) {
            return "";
        }
        String id = fieldId.trim();
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        List<By> candidates = Arrays.asList(
                By.id("arid_WIN_0_" + id),
                By.cssSelector("#WIN_0_" + id + " input"),
                By.cssSelector("#WIN_0_" + id + " textarea"),
                By.id("ar" + id + "_data"),
                By.id("ar" + id),
                By.id(id),
                By.cssSelector("#ar" + id + " input"),
                By.cssSelector("#ar" + id + " textarea"),
                By.cssSelector("#" + id + " input"),
                By.cssSelector("#" + id + " textarea")
        );

        for (By by : candidates) {
            if (commonWaits.isElementPresent(by, 2)) {
                WebElement el = commonWaits.waitForByElementToBeVisible(by);
                String v = el.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
                if (v == null || v.trim().isEmpty()) {
                    v = el.getText();
                }
                return v == null ? "" : v.trim();
            }
        }
        return "";
    }

    public CMDBDashboard createCatalogMapping(Map<Object, String> cmdbTestData, String discoveredProductName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        commonWaits.waitForElementVisible(catalogMappingCreateBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        clickWithoutInterception(select_Class);
        commonWaits.waitForElementVisible(selectClassInput).click();
        selectClassInput.clear();
        selectClassInput.sendKeys(cmdbTestData.get("CI_ClassName"));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        clickWithoutInterception(By.xpath(String.format(class_Item, cmdbTestData.get("CI_ClassName"))));

        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogDiscoveryInput, SolQAConstants.catalogMappingCategory)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("discoveryCategoryValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogDiscoveryInput, SolQAConstants.catalogMappingProductType)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("discoveryTypeValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogDiscoveryInput, SolQAConstants.catalogMappingItem)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("discoveryItemValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogDiscoveryInput, SolQAConstants.catalogMappingProductName)));
        we.click();
        we.clear();
        we.sendKeys(discoveredProductName);

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogDiscoveryInput, SolQAConstants.catalogMappingManufacturer)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("discoveryManufacturerValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogMappedInput, SolQAConstants.catalogMappingCategory)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("mappedCategoryValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogMappedInput, SolQAConstants.catalogMappingProductType)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("mappedTypeValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogMappedInput, SolQAConstants.catalogMappingItem)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("mappedItemValue"));

        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(catalogMappedInput, SolQAConstants.catalogMappingProductName)));
        we.click();
        we.clear();
        we.sendKeys(cmdbTestData.get("mappedProductNameValue"));

        commonWaits.waitForElementVisible(manufacturerDropdownBox).click();
        clickWithoutInterception(By.xpath(String.format(dropdownOption, cmdbTestData.get("mappedManufacturerValue"))));

        commonWaits.waitForElementVisible(SaveCIBtn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

    public CMDBDashboard runNormalizationJob(String jobName, String normalizeType) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        clickWithoutInterception(totalJobs);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        Assert.assertTrue(commonWaits.waitForVisibleElement(jobFilterSearchBox), "Job Filters are not available");
        clickWithoutInterception(jobFilterSearchBox);
        jobFilterSearchBox.clear();
        jobFilterSearchBox.sendKeys(jobName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(reconciliationJobLink, jobName)));
        Assert.assertTrue(we.isDisplayed(), jobName + " Job is not available");
        clickWithoutInterception(we);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        if (commonWaits.isElementPresent(normalizationStartJobButton, 5)) {
            clickWithoutInterception(normalizationStartJobButton);
        } else {
            clickWithoutInterception(startJobButton);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        clickWithoutInterception(By.xpath(String.format(normalizeJobType, normalizeType)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);

        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(normalizationCompletedStatus), 60),
                "Normalization job did not complete successfully for job: " + jobName);
        return this;
    }

    public String getMidTierTextFieldValue(String fieldLabel) {
        if (fieldLabel == null || fieldLabel.trim().isEmpty()) {
            return "";
        }

        By textareaBy = By.xpath(String.format(textField, fieldLabel));
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), textareaBy, 20)) {
            WebElement el = commonWaits.waitForByElementToBeVisible(textareaBy);
            String v = el.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
            if (v == null || v.trim().isEmpty()) {
                v = el.getText();
            }
            return v == null ? "" : v.trim();
        }

        if (commonWaits.isElementPresent(By.xpath(String.format(inputField, fieldLabel)))) {
            WebElement el = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(inputField, fieldLabel)));
            String v = el.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
            if (v == null || v.trim().isEmpty()) {
                v = el.getText();
            }
            return v == null ? "" : v.trim();
        }

        return "";
    }

    public CMDBDashboard verifyDataSourceNamePresent(String datasourceName) {
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(datasourceNameLocator, datasourceName))), "Datasource is not available");
        return this;
    }

    public CMDBDashboard setDatasetConfiguration(String datasetID, Map<Object, String> configValues) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), datasetFilterInput, 20), "Dataset filter input is not visible");
        datasetFilterInput.clear();
        datasetFilterInput.sendKeys(datasetID + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(datasetIDRowInDatasetConfigTable, datasetID)), 20),
                "Dataset with ID " + datasetID + " is not visible in dataset configuration table");
        clickWithoutInterception(By.xpath(String.format(datasetIDRowInDatasetConfigTable, datasetID)));
        commonWaits.clickWithoutInterception(datasetConfigEditButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        if (configValues.get("Name and CIT Lookup") != null) {
            commonWaits.clickWithoutInterception(radioBtnNameAndCITLookup);
        }
        if (configValues.get("CIT Only") != null) {
            commonWaits.clickWithoutInterception(radioBtnCITLookup);
        }
        if (configValues.get("Enable Normalization") != null) {
            String desiredNormalization = String.valueOf(configValues.get("Enable Normalization"));
            String checkedAttr = checkboxEnableNormalization.getAttribute(SolQAConstants.ARIA_CHECKED);
            boolean isNormalizationEnabled = "true".equalsIgnoreCase(checkedAttr) || "checked".equalsIgnoreCase(checkedAttr);
            if (("true".equalsIgnoreCase(desiredNormalization) && !isNormalizationEnabled)
                    || ("false".equalsIgnoreCase(desiredNormalization) && isNormalizationEnabled)) {
                commonWaits.clickWithoutInterception(checkboxEnableNormalization);
            }
        }
        if (configValues.get("Inline Normalization") != null) {
            String desiredNormalization = String.valueOf(configValues.get("Inline Normalization"));
            String checkedAttr = inlineEnableNormalization.getAttribute(SolQAConstants.ARIA_CHECKED);
            boolean isInlineNormalizationEnabled = "true".equalsIgnoreCase(checkedAttr) || "checked".equalsIgnoreCase(checkedAttr);
            if (("true".equalsIgnoreCase(desiredNormalization) && !isInlineNormalizationEnabled)
                    || ("false".equalsIgnoreCase(desiredNormalization) && isInlineNormalizationEnabled)) {
                commonWaits.clickWithoutInterception(inlineEnableNormalization);
            }
        }
        if (configValues.get("Allow Unapproved CIs") != null) {
            String desiredNormalization = String.valueOf(configValues.get("Allow New Product Catalog Entry"));
            WebElement we = commonWaits.findElement(By.xpath(checkbox_Allow_Unapproved_CIs));
            String checkedAttr = we.getAttribute(SolQAConstants.ARIA_CHECKED);
            boolean isAllowUnapprovedCIsEnabled = "true".equalsIgnoreCase(checkedAttr) || "checked".equalsIgnoreCase(checkedAttr);
            if (("true".equalsIgnoreCase(desiredNormalization) && !isAllowUnapprovedCIsEnabled)
                    || ("false".equalsIgnoreCase(desiredNormalization) && isAllowUnapprovedCIsEnabled)) {
                commonWaits.clickWithoutInterception(By.xpath(checkbox_Allow_Unapproved_CIs+"/following-sibling::*"));
            }
        }
        if (configValues.get("Allow New Product Catalog Entry") != null) {
            String desiredNormalization = String.valueOf(configValues.get("Allow New Product Catalog Entry"));
            WebElement we = commonWaits.findElement(By.xpath(checkbox_Allow_New_Product_Catalog_Entry));
            String checkedAttr = we.getAttribute(SolQAConstants.ARIA_CHECKED);
            boolean isAllowNewProductCatalogEntryEnabled = "true".equalsIgnoreCase(checkedAttr) || "checked".equalsIgnoreCase(checkedAttr);
            if (("true".equalsIgnoreCase(desiredNormalization) && !isAllowNewProductCatalogEntryEnabled)
                    || ("false".equalsIgnoreCase(desiredNormalization) && isAllowNewProductCatalogEntryEnabled)) {
                commonWaits.clickWithoutInterception(By.xpath(checkbox_Allow_New_Product_Catalog_Entry+"/following-sibling::*"));
            }
        }
        if (saveButton.isEnabled()) {
            commonWaits.clickWithoutInterception(saveButton);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.CMDB);
        return this;
    }

}

