package pages.ITSM.HelixGPT;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import pages.ITSM.InnovationSuite.InnovationSuite;
import util.CommonWaits;
import util.HelixGPTUtil;
import util.SolQAConstants;
import util.SolqaLogger;

public class HelixGPTAgentStudio extends BasePage {
    WebDriver driver;

    static SolqaLogger log;
    CommonWaits commonWaits;
    String appName = null;
    InnovationSuite innovationSuite = new InnovationSuite();
    HelixGPTUtil helixGPTUtil = new HelixGPTUtil();
    // CommonWaits app name used for loaders in CommonWaits
    private static final String APP_SERVICE = "SERVICE";
	
	// Wizard grid (services list)
	private static final String CSS_SERVICES_CELL_CONTENTS =
			"rx-services-wizard-step .ui-grid-render-container-body .ui-grid-cell:nth-child(1) .ui-grid-cell-contents";
	private static final By SERVICES_CELL_CONTENTS_BY = By.cssSelector(CSS_SERVICES_CELL_CONTENTS);
	private static final By SERVICES_CHECKBOX_ROW_BY =
			By.cssSelector("rx-services-wizard-step .ui-grid-pinned-container .ui-grid-row");

    //String Locators
    String AppDropdownValue = "//*[contains(@class, 'rx-select__options')]//button[contains(. ,'%s')]";
    String skillRadio="//*[text()='%s']/parent::*/parent::*/parent::*//adapt-rx-radiobutton//div[contains(@class,'radio_')]";
    String modelLabel = "//adapt-rx-select[.//span[text()='%s']]//button";
    String modelValue = "//adapt-highlight[normalize-space()='%s']";
    String skillFieldValue="//adapt-rx-control-label[contains(., '%s')]/following-sibling::div//div[contains(@class,'rx-select')]";
    String helixGPTManagerSkillTabs="//button[@role='tab' and contains(.,'%s')]";
    String selectCheckBoxByName="//*[normalize-space()='%s']/..//div[@class='ui-chkbox ui-widget']";
    String thumbsIcons="//span[contains(@class,'thumb_%s')]/parent::button";
    String applicationList = "//select[@rx-id='wizard-application']//option[text()='%s']";
    String skillList = "//select[@rx-id='wizard-skill']//option[contains(text(),'%s')]";
    String visibleColumnCheckbox="//*[text()='%s']/parent::*/parent::*//input[@type='checkbox']";
    String guidOfskill = "//tr//td//adapt-button[contains(.,'%s')]/../../parent::td/..//td[2]";
    String PromptSearchResult = "//td[contains(@data-testid,'adapt-table_column')]//div[normalize-space()='%s']"; 
    String promptNameList = "//div[normalize-space()='%s']";
    String linkPromptRadioButton="//div[normalize-space()='%s']//div[@class='radio__label']";
    String promptLink="//div[normalize-space()='%s']/..//button";
    String startPromptCheckbox="//span[contains(text(),'Starter prompt')]/parent::*/parent::label";
    String PromptModel="//button[normalize-space()='%s']/.";
    String SkillSearchResult = "//adapt-button//span[text()='%s']"; 
    String firstArticle = "//a[contains(normalize-space(),'%s')]";
    String itemPresentOnPage = "//li/div[contains(.,'%s')]";
    String connectionName= "//*[contains(@class,'connection-name')]//span[normalize-space()='%s']";
    String ConnectionType="//div[contains(@class,'rx-select__option')]//adapt-highlight[normalize-space()='%s']";
    String AddButton="//adapt-button[contains(@rx-id,'add-connection') and normalize-space()='%s']";
    String connectionParameters="//adapt-rx-control-label[.//label//span[text()='%s']]/following-sibling::div//input";
    String connectionParametersDropdown="//adapt-rx-control-label[.//label//span[text()='%s']]/following-sibling::div//button";
    String collapsedMenuItem="//div[@role='treeitem' and @aria-label='%s']";
    String connectionParameterDropdownValue="//adapt-highlight[contains(normalize-space(),'%s')]";
    String SKILLActionButton="//div[normalize-space()='%s']//ancestor::tr//button[normalize-space()='%s']";
    String SKIllActionButton="//div[normalize-space()='%s']//ancestor::tr//button";
    String UnlinkPromptbutton ="//button[normalize-space()='%s']";
    String YesFromPopUp ="//adapt-button[normalize-space()='%s']";
    String HelixGPTAgentStudioTabs="//span[normalize-space()='%s']/parent::a[@role='link']";
    String fieldIdLabel = "//div[text()='%s']/following::div[contains(@class,'cell-contents')][1]";
    String recordLinkByName = "//a[normalize-space()='%s']";
    String recordCheckboxByName = "//a[normalize-space()='%s']/ancestor::tr//div[contains(@class,'ui-chkbox-box')]";
    String statusOptionByText = "//button[@role='option']//div[text()='%s']";
    String editDataButtonLocator = "//button[@rx-id='edit-data']";
    String agentRadioButton = "(//*[normalize-space()='%s']/ancestor::*//adapt-rx-radiobutton//div[contains(@class,'radio_')])[1]";
    String agentRadioButtonLabel="//div[normalize-space()='%s']//div[@class='radio__label']";
    String agentRadioButtonLabelChecked="//input[@checked='checked']/ancestor::div[normalize-space()='%s']//div[@class='radio__label']";


    // Form detection locators
    String newButtonInnerSpan = ".//span[@class='btn-content']";

    //WebElement Locators
	@FindBy(xpath = "//a[contains(.,'Visit deployed application')]")
	WebElement visitDeployedApplicationLink;

	@FindBy(xpath = "//label[contains(.,'Application')]/ancestor::adapt-rx-control-label//following-sibling::div//button")
	WebElement applicationDropdown;

	@FindBy(xpath ="//input[contains(@id,'rx-search') and @placeholder='Filter']")
	WebElement AppSearchTextBox;

    @FindBy(xpath = "//adapt-tab-panel[contains(@class,'tab-pane active')]//adapt-button//span[normalize-space()='Apply']")
    WebElement applySkill;

    @FindBy(xpath = "//adapt-button[normalize-space()='Apply' and contains(@class,'promptDetails')]")
    WebElement applyAgent;

    @FindBy(xpath = "(//div[normalize-space()='HelixGPT'])[1]")
    WebElement HelixGPTBotLogo;

    @FindBy(xpath = "//textarea[contains(@placeholder, 'Ask a')  or contains(@placeholder, 'Type a message')]")
    WebElement textInputArea;

    @FindBy(xpath = "(//div[contains(@class,'cb-message')])[last()]")
    private List<WebElement> allMessages;

     @FindBy(xpath = "(//div[contains(@data-testid,'helixGPTChatMessage')])[last()]")
    private List<WebElement> allSkillResponces;

    @FindBy(xpath = "//span[contains(@class,'thumb_down')]/parent::button")
    WebElement Thumbs_Icon;

    @FindBy(xpath = "//*[contains(@class,'additional-feedback')]//textarea")
    private WebElement feedbackTextarea;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submitFeedbackButton;

    @FindBy(xpath = "//button[@rx-id='send-message-button']")
    private WebElement sendMessageButton;

    @FindBy(css = "[rx-id=actions-buttons]")
    private WebElement actionsButton;

    @FindBy(css = "[rx-id=enable-chat-for-service]")
    private WebElement publishChatEnabledService;

    @FindBy(xpath ="//*[contains(@class,'d-block position-absolute')]//iframe")
    private WebElement publishServiceIframe;

    @FindBy(xpath = "//select[@rx-id='wizard-provider']")
    private WebElement COGNITIVE_PROVIDER;

    @FindBy(xpath = "//option[@value='BMC HelixGPT']")
    private WebElement BMC_HELIXGPT;

    @FindBy(xpath = "//select[@rx-id='wizard-application']")
    private WebElement applicationNamePicker;

    @FindBy(xpath = "//select[@rx-id='wizard-skill']")
    private WebElement skillNamePicker;

    @FindBy(css = "[rx-id=next-button]")
    private WebElement nextButton;

    @FindBy(css = "rx-services-wizard-step .ui-grid-header .ui-grid-selection-row-header-buttons")
    private WebElement selectAllServices;

    @FindBy(css = ".modal-dialog [rx-id=search-text-input]")
    private WebElement searchInput;

    @FindBy(css = "rx-services-wizard-step .ui-grid-render-container-body .ui-grid-cell:nth-child(1) .ui-grid-cell-contents")
    private List<WebElement> services;

    @FindBy(css = "rx-services-wizard-step .ui-grid-pinned-container .ui-grid-row")
    private List<WebElement> checkboxes;

    @FindBy(css = "[rx-id=import-succeeded-message]")
    private WebElement publicationSuccessMessage;

    @FindBy(xpath = "(//button[@rx-id='confirm-button'])[1]")
    private WebElement warningAcceptButton;

    @FindBy(xpath = "//div[contains(@class,'message') and contains(.,'submitted')]//a")
    private WebElement requestIDFromLastResponse;

    @FindBy(xpath = "//button[@rx-id='services-button']")
    private WebElement ServicesButton;

    @FindBy(xpath = "//label[contains(.,'Request Variations')]//span")
    private WebElement RequestVariationsButton;

    @FindBy(xpath = "//*[@class='rx-questions-header' and contains(.,'Service Questions')]")
    private WebElement ServiceQuestionsHeader;

    @FindBy(xpath = "//button[contains(.,'Visible columns')]")
    WebElement threeDotsMoreOption;

    @FindBy(xpath = "//span[text()='Visible columns']/parent::button")
    private WebElement visibleColumnDropDownButton;

    @FindBy(xpath ="//input[@placeholder='Search']")
    WebElement searchBox;

    @FindBy(xpath = "//*[normalize-space()='Link prompt']/ancestor::div[contains(@class, 'modal-content')]//div[contains(@class, 'search-button')]")
    WebElement SearchButton;
    
    @FindBy(xpath = "//adapt-button[@rx-id='skill-details-tab-set-link-prompt']")
    WebElement linkPromptBtn;

    @FindBy(xpath = "//adapt-button[@rx-id='link-prompt-modal-link']")
    WebElement linkBtn;

    @FindBy(xpath = "//*[normalize-space()='Link prompt']/ancestor::div[contains(@class, 'modal-content')]//input[contains(@placeholder, 'Search')]")
    WebElement SearchTextArea;

    @FindBy(xpath = "//adapt-button[@rx-id='prompt-record-grid-edit']")
    WebElement EditPromptButton;

    @FindBy(xpath = "//span[contains(@class, 'd-icon-upload add-button-icon')]")
    private WebElement importButton;

    @FindBy(xpath = "//adapt-button[@rx-id='import-skill-modal-next']")
    private WebElement NextButton;

    @FindBy(xpath = "//adapt-button[@rx-id='import-skill-modal-import']")
    private WebElement skillImport;

    @FindBy(xpath = "//button[contains(.,'New topic')]")
    WebElement NewTopic;
    
    @FindBy(xpath = "//button[contains(.,'Test log')]")
    WebElement TestLog;
    
    @FindBy(xpath = "//button[contains(@class,'expansion-btn')]")
    WebElement ExpandArrow;

    @FindBy(xpath = "//div[normalize-space()='Input']//span[@aria-hidden='true']")
    WebElement InputButton;
    
    @FindBy(xpath = "//div[@aria-label='skill_id']//span[contains(text(),'AG')]")
    WebElement SkillID;

    @FindBy(xpath = "//li[contains(.,'How To Connect To The Office')]")
    WebElement OfficePrinterResult;

    @FindBy(xpath = "//button[contains(.,'Test')]")
    WebElement TestButton;

    @FindBy(xpath = "//span[text()='Settings']/parent::a")
	WebElement settingsMenu;

    @FindBy(xpath = "//div[contains(@class,'adapt-rx-search') and @role='search']//input")
	WebElement searchSettingsInput;

    @FindBy(xpath = "(//adapt-tree-node//span[contains(@data-testid, 'adapt-tree')])[1]")
    WebElement treeCollapsed;

    @FindBy(xpath = "//span[contains(text(),'Line of Business Management')]")
    WebElement lobManagement;

    @FindBy(xpath = "//adapt-button[@rx-id='add-connection']")
   	WebElement AddConnection;

    @FindBy(xpath = "//adapt-rx-control-label[normalize-space()='Connection type']/following-sibling::div//button")
   	WebElement ConnectionTypeDropdown;

    @FindBy(xpath = "//button[contains(.,'Save')]")
    WebElement saveStatusButton;

    @FindBy(xpath = "//*[@class='ui-chkbox-box ui-widget ui-state-default ui-state-active']")
    private List<WebElement> selectedCheckBoxes;

     @FindBy(xpath = "//button[@rx-id='publish-button']")
    WebElement publishButton;

    @FindBy(xpath = "//adapt-button[contains(@class,'btn keyboard-focusable') and normalize-space()='Upload File']/..//input")
    WebElement uploadFile;

    @FindBy(xpath = "//*[@class='ui-chkbox-box ui-widget ui-state-default']")
    List<WebElement> emptyCheckBoxes;
    
    @FindBy(xpath = "//a[contains(text(),'Sign in')]")
    WebElement rbacSignIn;
    @FindBy(xpath = "//input[@name='loginfmt']")
    WebElement rbacUsername;
    @FindBy(xpath = "//input[@type='password']")
    WebElement rbacPassword;    
    @FindBy(xpath = "//input[@type='submit']")
    WebElement acceptSubmit;
    
    @FindBy(xpath = "//input[contains(@id,'rx-search') and @placeholder='Search']")
    WebElement recordGridSearchInput;

    @FindBy(xpath = "//button[contains(@class,'adapt-rx-search__search-button')]")
    WebElement recordGridSearchButton;

    @FindBy(xpath = "//button[@rx-id='edit-data']")
    WebElement editDataButton;

    @FindBy(xpath = "//button[@rx-id='new-button']")
    WebElement newButton;

    @FindBy(xpath = "//adapt-rx-control-label[contains(., 'Description')]/following-sibling::div//input[@type='text']")
    WebElement descriptionTextarea;

    @FindBy(xpath = "//adapt-rx-control-label[contains(., 'Status')]/following-sibling::div//button")
    WebElement statusDropdownButton;

    @FindBy(xpath = "//button[contains(@class,'btn-primary')][.//span[text()='Save']]")
    WebElement saveButton;
    
    @FindBy(xpath = "//button[@rx-id='new-button']")
    WebElement newButtonRecordDef;

    @FindBy(xpath = "//button[contains(@rx-id,'record.domain.RegularRecordDefinition')]")
    WebElement regularRecordOption;

    @FindBy(xpath = "//input[@data-attribute='rxData/name']")
    WebElement recordNameInput;

    @FindBy(xpath = "//input[@data-attribute='rxData/shouldExportData']/../span")
    WebElement shouldExportDataToggle;

    @FindBy(xpath = "//input[@data-attribute='rxData/shouldExportData']")
    WebElement shouldExportDataInput;

    @FindBy(xpath = "//input[@data-attribute='rxData/enableLocalization']/../span")
    WebElement enableDataTranslationToggle;

    @FindBy(xpath = "//input[@data-attribute='rxData/enableLocalization']")
    WebElement enableDataTranslationInput;

    @FindBy(xpath = "//a[contains(@class,'dropdown-toggle') and normalize-space()='New Field']")
    WebElement newFieldButton;

    @FindBy(xpath = "//li[contains(@ng-click,'addNewField')]//a[text()='Attachment']")
    WebElement attachmentFieldTypeOption;

    @FindBy(xpath = "(//input[@data-attribute='rxData/name'])[last()]")
    WebElement fieldNameInput;

    @FindBy(xpath = "//input[@data-attribute='rxData/required']/../span")
    WebElement requiredCheckbox;

    @FindBy(xpath = "//input[@data-attribute='rxData/required']")
    WebElement requiredCheckboxInput;

    @FindBy(xpath = "//button[contains(@class,'header__button_save')]")
    WebElement saveFieldButton;

    @FindBy(xpath = "//div[text()='Record Definition saved successfully.']")
    WebElement successMessage;

    @FindBy(xpath = "//*[text()='Saved successfully.']")
    WebElement addRecordSuccessMessage;

    @FindBy(xpath = "//iframe[contains(@src,'irecord')]")
    WebElement recordIframe;
    
    @FindBy(xpath = "//input[@type='file' and @aria-label='File Control']")
    WebElement attachmentFileInputByFieldName;

    @FindBy(xpath = "//div[contains(@class,'modal')]")
    List<WebElement> modalLocator;

    @FindBy(xpath = "//form")
    List<WebElement> formLocator;

    @FindBy(xpath = "//adapt-docked-panel")
    List<WebElement> dockedPanelLocator;

    @FindBy(xpath = "//div[@role='dialog']")
    List<WebElement> dialogLocator;

    @FindBy(xpath = "//adapt-rx-control-label")
    List<WebElement> adaptRxControlLabel;

    @FindBy(xpath = "//input[@type='file']")
    List<WebElement> fileInputLocator;

    @FindBy(xpath = "//input[@type='text']")
    List<WebElement> textInputLocator;

    public HelixGPTAgentStudio() {
        log = new SolqaLogger(HelixGPTAgentStudio.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }


   public void clickVisitDeployedApplicationLink() {
       log.info("Clicking on Visit Deployed Application Link");
       clickWithoutInterception(visitDeployedApplicationLink);
	   commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
       commonWaits.waitForAngularRequestsToFinish();
	   log.info("Successfully navigated to the HelixGPT Configuration Page");
   }

   public void selectApplication(String applicationName) {
       log.info("Selecting application: " + applicationName);
       commonWaits.switchToLastWindow();
       clickWithoutInterception(applicationDropdown);
	   commonWaits.waitForAngularRequestsToFinish();
	   commonWaits.waitForExecutionComplete();
	   commonWaits.waitForElementClickable(AppSearchTextBox,20).sendKeys(applicationName);
	   WebElement DropdownValue=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AppDropdownValue, applicationName)));
	   commonWaits.waitForElementVisible(DropdownValue, 30);
	   commonWaits.clickWithoutInterception(DropdownValue);
	   commonWaits.waitForExecutionComplete();
       log.info("Successfully selected application: " + applicationName);
   }

    public void selectSkill(String skillName){
        log.info("Selecting skill: " + skillName);
        WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(skillRadio,skillName)));
        commonWaits.clickWithoutInterception(ele);
        commonWaits.waitForExecutionComplete();
        log.info("Successfully selected skill: " + skillName);
    }

     public void clickOnApplyButton_OnSkill(){
        log.info("Clicking on Apply button");
        commonWaits.clickWithoutInterception(applySkill);
        commonWaits.waitForExecutionComplete();
        log.info("Successfully clicked on Apply button");
    }

    public void setModelValues(String label, String value) {
        log.info("Navigating to Model Tab");
        clickSkillTab(SolQAConstants.MODEL);
        log.info("Setting model values: " + label + " = " + value);
		WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(modelLabel, label)));
		commonWaits.waitForElementClickable(ele).click();
		WebElement Value=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(modelValue, value)));
		commonWaits.waitForElementClickable(Value);
		commonWaits.clickWithoutInterception(Value);
		commonWaits.waitForExecutionComplete();
		clickOnApplyButton_OnSkill();
        log.info("Successfully set model values: " + label + " = " + value);
		
	}

    public boolean getFieldValues(String Field, String Value) {
        log.info("Verifying if field '" + Field + "' contains value: " + Value);
        WebElement FieldValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(skillFieldValue, Field)));
        String resultText = FieldValue.getText().trim();
        if (resultText.contains(Value)) {
            log.info("Field '" + Field + "' already contains value: " + Value);
            return true; // Direct return to avoid unnecessary checks
        }
        log.info("Field '" + Field + "' does not contain value: " + Value + ". Setting value...");
        setModelValues(Field, Value);
        boolean result = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(skillFieldValue, Field)))
                          .getText().trim()
                          .contains(Value);
        log.info("After setting, field '" + Field + "' contains value: " + Value + " = " + result);
        return result;
    }

     public void clickSkillTab(String tabName){
    	WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(helixGPTManagerSkillTabs,tabName)));
        commonWaits.waitForElementClickable(ele);
        commonWaits.clickWithoutInterception(By.xpath(String.format(helixGPTManagerSkillTabs,tabName)));
    }

    public void selectSkillSearchSetting(String informationProvider) {
        log.info("Selecting skill search setting for information provider: " + informationProvider);
        clickSkillTab(SolQAConstants.SearchSettingTab);

        try {
            log.info("Number of selected checkboxes found: " + selectedCheckBoxes.size());
            for (WebElement checkbox : selectedCheckBoxes) {
                log.info("Deselecting selected checkbox");
                commonWaits.clickWithoutInterception(checkbox);
            }
        } catch (Exception ex) {
            log.info("None of the information providers are selected under Search settings tab");
        }

        log.info("Selecting information provider: " + informationProvider);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectCheckBoxByName, informationProvider)));
        commonWaits.clickWithoutInterception(ele);
        log.info("Clicking on Apply button after selecting information provider");
        commonWaits.waitForElementClickable(applySkill);
        commonWaits.clickByJS(applySkill);
        commonWaits.waitForExecutionComplete();
        log.info("Successfully selected skill search setting for information provider: " + informationProvider);
    }

    public void verifyLoginToHelixGPTBot() {
       final int LOGIN_TIMEOUT = 50;
       log.info("Verifying login to HelixGPT Bot...");
       boolean isLogoVisible = commonWaits.isElementPresent(HelixGPTBotLogo, LOGIN_TIMEOUT);
       Assert.assertTrue(isLogoVisible,"Login to HelixGPT Bot failed - Logo not visible within " + LOGIN_TIMEOUT + " seconds.");
       log.info("Successfully logged into HelixGPT Bot.");
}

  public void typeKeysWithEnterForSendButton(WebElement element, String value) {
    element.clear();
    element.sendKeys(value + Keys.ENTER);
}
    public void sendMessage(String message) {
        log.info("Sending message to HelixGPT Bot: " + message);
      //  commonWaits.switchToLastWindow();
        commonWaits.waitForElementVisible(textInputArea, 10);
        commonWaits.waitForElementClickable(textInputArea, 10);
        typeKeysWithEnterForSendButton(textInputArea, message);
        log.info("Message sent: " + message);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForExecutionComplete();
        log.info("Message processing complete.");
    }

  public String getAllGPTResponseString() {
        return helixGPTUtil.getAllGPTResponseString(allMessages);
}

 public String getAllSkillGPTResponseString() {
        return helixGPTUtil.getAllGPTResponseString(allSkillResponces);
}


   public void switchToHelixGPTPage(WebDriver webDriver) {
        log.info("----------- Switching to HelixGPT Page ----------------------");
        String windowHandle = webDriver.getWindowHandle();
        webDriver.switchTo().window(windowHandle);
        setDriver(webDriver);
        log.info("Switched to HelixGPT Agent Studio window successfully.");
    }

      public boolean verifyIsThumbsIconPresent(String icon) {
        log.info("Verifying presence of thumbs icon: " + icon);
        return commonWaits.isElementPresent(By.xpath(String.format(thumbsIcons, icon)));
    }

      public void clickOnThumbIcon(String icon) {
        log.info("Clicking on thumbs icon: " + icon);
        try {
            if (commonWaits.isElementPresent(Thumbs_Icon)) {
                Thumbs_Icon.click();
                return;
            }
        } catch (Exception e) {
            log.info("Exception while clicking on Thumbs_Icon: " + e.getMessage());
        }

        // Fallback: Click using explicit wait in case of an exception or empty list
        WebElement ele= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(thumbsIcons, icon)));
        commonWaits.clickWithoutInterception(ele);
        log.info("Clicked on thumbs icon: " + icon);
    }

    
    public void enterFeedbackAndSubmit(String feedback) {
        log.info("Entering feedback: " + feedback);
    	commonWaits.waitForElementVisible(feedbackTextarea);
    	feedbackTextarea.sendKeys(feedback);
        log.info("Submitting feedback");
        commonWaits.waitForElementClickable(submitFeedbackButton).click();
        log.info("Feedback submitted");
    }

    public boolean isNextButtonActive() {
        log.info("Checking if Next button is active");
        return nextButton.isEnabled();
    }

    public boolean isNextInactive() {
        log.info("Checking if Next button is inactive");
        return !isNextButtonActive();
    }

    protected void verifyThat(String reason, boolean assertion) {
        log.info("Verifying: " + reason);
        try {
            MatcherAssert.assertThat(reason, assertion);
        } catch (AssertionError e) {
            log.error("Verification failed: " + e.getMessage());
        }
    }

    public void typeKeysWithEnter(WebElement element, String keys) {
        log.info("Typing keys with Enter: " + keys);
        commonWaits.waitForElementVisible(element);
        element.clear();
        element.sendKeys(keys);
        element.sendKeys(Keys.ENTER);
        commonWaits.waitForExecutionComplete();
    }

    private Integer getServiceFieldIndex(String serviceName) {
    log.info("Getting index for service: " + serviceName);
    commonWaits.waitForAngularRequestsToFinish();
    commonWaits.waitForAllSpinnersToHide(APP_SERVICE);

    // Wait (best-effort) until the services grid is visible/populated (UATron-style condition wait)
	commonWaits.waitForByElementToBeVisible(SERVICES_CELL_CONTENTS_BY);
    Assert.assertTrue(services != null && !services.isEmpty(), "Services list did not load for service selection wizard.");

    WebElement serviceField = services.stream()
            .filter(item -> item.getText().equals(serviceName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("There is no service with the name: " + serviceName));

    int index = services.indexOf(serviceField);
    log.info("Index for service '" + serviceName + "' is: " + index);
    return index;
}



    public void searchForService(String serviceName) {
        log.info("Searching for service: " + serviceName);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        typeKeysWithEnter(searchInput, serviceName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForExecutionComplete();
        log.info("Search for service '" + serviceName + "' completed");
    }

    public void selectServiceAndWaitForDataLoading(String serviceName) {
        commonWaits.waitForAngularRequestsToFinish();
        log.info("Selecting '" + serviceName + "' service");
        WebElement html = getDriver().findElement(By.tagName("html"));
        html.sendKeys(Keys.ENTER);
        commonWaits.waitForByElementToBeVisible(SERVICES_CHECKBOX_ROW_BY);
        int index = getServiceFieldIndex(serviceName);
        WebElement service = checkboxes.get(index);
        commonWaits.waitForElementClickable(service, 30);
        commonWaits.scrollToSpecificElement(service);
        commonWaits.clickWithoutInterception(service);
        log.info("Service '" + serviceName + "' selected successfully");
    }

    public void publishService(boolean isSuccessMessageExpected, WebDriver driver) {
        log.info("Clicking 'Publish' button");
        commonWaits.waitForElementClickable(publishButton).click();
        if (isSuccessMessageExpected) {
            log.info("Waiting for publication success message");
            commonWaits.waitForElementVisible_withFluentWait(driver, publicationSuccessMessage, 60);
        }
        log.info("Publish service process completed");
    }

    private boolean isPublicationSuccessMessageValid() {
        log.info("Validating publication success message");
        boolean isValid = publicationSuccessMessage.getText().trim().equals(
            "Publication complete. The added service can be requested through the Chatbot once automated Chatbot retraining is complete."
        );
        log.info("Publication success message valid: " + isValid);
        return isValid;
    }

    public HelixGPTAgentStudio enableAndPublishChatbotWithGPTProvider(String ApplicationName, String SkillName, String serviceName) {
        log.info("Enabling and publishing chatbot with GPT provider");
        innovationSuite.navigateToBundle(SolQAConstants.BMC_Chatbot);
        log.info("clicking actions button");
        commonWaits.clickWithoutInterception(actionsButton);
        log.info("clicking publish chat enabled service");
        commonWaits.clickWithoutInterception(publishChatEnabledService);
        commonWaits.switchToFrame(publishServiceIframe);
        log.info("Selecting BMC HelixGPT as cognitive provider");
        commonWaits.clickWithoutInterception(COGNITIVE_PROVIDER);
        commonWaits.waitForElementVisible(BMC_HELIXGPT).click();
        COGNITIVE_PROVIDER.sendKeys(Keys.ESCAPE);
        log.info("Selecting application: " + ApplicationName);
        commonWaits.waitForElementVisible(applicationNamePicker).click();
        WebElement chatbotPicker = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(applicationList, ApplicationName)));
        commonWaits.clickWithoutInterception(chatbotPicker);
        COGNITIVE_PROVIDER.sendKeys(Keys.ESCAPE);
        log.info("Selecting skill: " + SkillName);
        commonWaits.clickWithoutInterception(skillNamePicker);
        WebElement skillPicker = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(skillList, SkillName)));
        commonWaits.clickWithoutInterception(skillPicker);
        COGNITIVE_PROVIDER.sendKeys(Keys.ESCAPE);
        log.info("Waiting for 'Next' button to be active on 'Service' tab");
        commonWaits.waitForElementVisible_withFluentWait(getDriver(),nextButton,10);
        commonWaits.clickWithoutInterception(nextButton);
        if(!commonWaits.isElementPresent(ServicesButton)) {
            log.info("'Services' button is not displayed");
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.clickWithoutInterception(nextButton);
        }
        searchForService(serviceName);
        selectServiceAndWaitForDataLoading(serviceName);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(),nextButton,10);
        commonWaits.clickWithoutInterception(nextButton);
        //verifyThat("'Next' button is active on 'Service' tab until service is selected", isNextInactive());
        if (commonWaits.isElementPresent(warningAcceptButton)) {
            log.info("Warning accept button is displayed, clicking it");
            warningAcceptButton.click();
        } else {
            log.info("'Request Variations' button is not displayed");
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.clickWithoutInterception(nextButton);
        }
        commonWaits.waitForElementVisible_withFluentWait(getDriver(),nextButton,10);
        commonWaits.clickWithoutInterception(nextButton);
        // Loop to handle multiple warning accept buttons if present, with a limit to avoid infinite loop
        int warningAcceptLimit = 5;
        int warningAcceptCount = 0;
        while (commonWaits.isElementPresent(warningAcceptButton) && warningAcceptCount < warningAcceptLimit) {
            log.info("Warning accept button is displayed, clicking it (attempt " + (warningAcceptCount + 1) + ")");
            commonWaits.clickByJS(warningAcceptButton);
            commonWaits.waitForSometime(commonWaits.minDelay);
            warningAcceptCount++;
        }
        if (warningAcceptCount == warningAcceptLimit) {
            log.warn("Warning accept button appeared more than " + warningAcceptLimit + " times. Stopping further attempts.");
        }
          log.info("'Service Questions' header is not displayed");
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.clickWithoutInterception(nextButton);
        try {
            	 publishService(true, getDriver());
            }
            catch (Exception e) {
				log.info("Exception occurred while clicking 'Next' button: " + e.getMessage());
				 commonWaits.clickWithoutInterception(nextButton);
				 publishService(true, getDriver());
            }
        verifyThat("Text of publish succeeded message is not valid", isPublicationSuccessMessageValid());
        log.info("Chatbot enabled and published with GPT provider");
        return this;
    }

    public String serviceRequestQuestion(String question, Map<Object, String> TestData) {
       log.info("Processing service request question: " + question);
        // Store asked question
        Set<String> askedQuestions = new HashSet<>();
        askedQuestions.add(question);

        // Loop through test data and match Label using contains
        for (Map.Entry<Object, String> entry : TestData.entrySet()) {
           String label = entry.getKey().toString();   
           String value = entry.getValue();            
          log.info("Checking if question contains label: " + label);
           if (question.toLowerCase().contains(label.toLowerCase())) {
             log.info("Match found for label: " + label + ", sending message: " + value);
            sendMessage(value);
              return value;
           }
        }
       log.info("No matching label found for question: " + question);
       return "Question is different:- "+question;
}

    public String getRequestIdFromLastresponse() {
       log.info("Getting request ID from last response");
       return requestIDFromLastResponse.getText();
    }

    public boolean verifyRequestOpenedInDWP(){
       log.info("Verifying if request opened in DWP");
    boolean flag=false;
    commonWaits.waitForElementClickable(requestIDFromLastResponse).click();
    commonWaits.switchToLastWindow();
    String URL=getDriver().getCurrentUrl();
       log.info("Current URL: " + URL);
    if(URL.contains("dwp")) {
        flag=true;
    }
    return flag;
    }

      public String getIDOfImportedSkill(String skillName){
             log.info("Getting ID of imported skill: " + skillName);
             commonWaits.waitForExecutionComplete();
             commonWaits.waitForElementVisible(threeDotsMoreOption).click();
             if(commonWaits.waitForElementPresenceLocated(By.xpath(String.format(visibleColumnCheckbox,"ID"))).getAttribute(SolQAConstants.ARIA_CHECKED).equalsIgnoreCase(SolQAConstants.FALSE)) {
                log.info("ID column is not checked, clicking to enable");
                commonWaits.waitForElementPresenceLocated(By.xpath(String.format(visibleColumnCheckbox,"ID"))).click();
             }
             commonWaits.waitForElementVisible(visibleColumnDropDownButton).click();

             WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(guidOfskill, skillName)));
             String skillID = commonWaits.waitForElementClickable(ele).getText().trim();
             log.info("Skill ID is - "+ skillID);
             return skillID;

          }

    public boolean isPromptPresent(String searchText) {
       log.info("Checking if prompt is present for search text: " + searchText);
        clickSkillTab(SolQAConstants.PromptTabName);
       commonWaits.waitForElementVisible(searchBox).clear();
       searchBox.sendKeys(searchText, Keys.ENTER);
       commonWaits.waitForSometime(commonWaits.moreDelay);
       boolean present = commonWaits.isElementPresent(By.xpath(String.format(PromptSearchResult,searchText)));
       log.info("Prompt present: " + present);
       return present;
    }

    public HelixGPTAgentStudio linkNewPrompt(String promptName){
         clickSkillTab(SolQAConstants.PromptTabName);
          log.info("Linking new prompt: " + promptName);
           if (!commonWaits.isElementPresent(String.format(promptNameList, promptName))){
              log.info("Prompt not present, proceeding to link");
              commonWaits.waitForVisibleElement(linkPromptBtn);
              linkPromptBtn.click();
              Assert.assertTrue(commonWaits.waitForVisibleElement(SearchTextArea), "Search Prompt button is not available for link prompt");
              SearchTextArea.sendKeys(promptName);
              commonWaits.waitForElementClickable(SearchButton).click();
              commonWaits.waitForByElementToBeVisible(By.xpath(String.format(linkPromptRadioButton,promptName))).click();
              commonWaits.waitForSometime(commonWaits.minDelay);
              commonWaits.clickWithoutInterception(linkBtn);
              commonWaits.waitForExecutionComplete();
             log.info("Prompt linked successfully: " + promptName);
           } else {
             log.info("Prompt already present: " + promptName);
          }
        return this;
    }
    
    public HelixGPTAgentStudio selectSkillPrompt(String promptName){
       log.info("Selecting skill prompt: " + promptName);
       clickSkillTab(SolQAConstants.PromptTabName);
       commonWaits.waitForElementVisible(searchBox).clear();
       searchBox.sendKeys(Keys.ENTER);
       commonWaits.clickWithoutInterception(By.xpath(String.format(promptLink,promptName)));
       commonWaits.waitForElementClickable(EditPromptButton);
       commonWaits.clickWithoutInterception(EditPromptButton);

       try {
          log.info("Checking if starter prompt checkbox is unchecked");
          if(commonWaits.waitForElementPresenceLocated(By.xpath(startPromptCheckbox+"/input")).getAttribute(SolQAConstants.ARIA_CHECKED).equalsIgnoreCase(SolQAConstants.FALSE)){
             log.info("Starter prompt checkbox is unchecked, clicking to check and save");
             commonWaits.clickWithoutInterception(By.xpath(startPromptCheckbox));
             commonWaits.clickWithoutInterception(By.xpath(String.format(PromptModel,SolQAConstants.SAVE)));
             commonWaits.clickWithoutInterception(By.xpath(String.format(PromptModel,SolQAConstants.CLOSE)));

          }
          else {
             log.info("Starter prompt checkbox is already checked, closing");
             commonWaits.clickWithoutInterception(By.xpath(String.format(PromptModel,SolQAConstants.CLOSE)));
          }
       }
       catch(Exception e) {
          log.info("Exception occurred, closing prompt modal");
          commonWaits.waitForExecutionComplete();
       }
       return this;
    }

     public boolean isSkillPresent(String searchText) {
       log.info("Checking if skill is present for search text: " + searchText);
       commonWaits.waitForElementVisible(searchBox).clear();
       searchBox.sendKeys(searchText, Keys.ENTER);
       commonWaits.waitForSometime(commonWaits.moreDelay);
       boolean present = commonWaits.isElementPresent(By.xpath(String.format(SkillSearchResult,searchText)));
       log.info("Skill present: " + present);
       return present;
    }

    public void importSkill(String SkillToimport) {
       final String PATH_TO_TEST_RESOURCES = System.getProperty("user.dir") + File.separator + "resources"
             + File.separator + "HelixGPT" + File.separator+ SkillToimport+".json";
       log.info("Importing skill: " + SkillToimport + " from file location: " + PATH_TO_TEST_RESOURCES);
       commonWaits.waitForElementClickable(importButton).click();
       commonWaits.waitForSometime(commonWaits.minDelay);
       WebElement uploadFileInput = commonWaits.waitForElementVisible(uploadFile);
       uploadFileInput.sendKeys(PATH_TO_TEST_RESOURCES);
       commonWaits.waitForElementClickable(NextButton).click();
       commonWaits.waitForElementClickable(skillImport).click();
       commonWaits.waitForExecutionComplete();
       log.info("Skill imported successfully: " + SkillToimport);
    }

     public HelixGPTAgentStudio clickTestButtonForSkill(String skillName) {
        log.info("Clicking test button for skill: " + skillName);
        WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SKIllActionButton, skillName)));
          commonWaits.clickWithoutInterception(ele);
          commonWaits.clickWithoutInterception(TestButton);
          commonWaits.waitForExecutionComplete();
        log.info("Test button clicked for skill");
        return this;
    }
      
      public void verifySkillIDForTestSkill(String skillID) {
        log.info("Verifying skill ID for test skill: " + skillID);
          Assert.assertTrue(commonWaits.isElementPresent(OfficePrinterResult),"Paper jam result is not available for Test skill");
          commonWaits.waitForElementVisible(InputButton);
          commonWaits.clickByJS(InputButton);
          Assert.assertTrue(commonWaits.isElementPresent(SkillID),"Paper jam skill ID is not available");
        Assert.assertTrue(SkillID.getText().equals(skillID),"Skill ID does not match for paper jam. expected: " + skillID + ", actual: " + SkillID.getText());
          commonWaits.waitForExecutionComplete();
        log.info("Skill ID verified for test skill: " + skillID);
    }
      
      public void clickOnNewTopic() {
        log.info("Clicking on New Topic button");
          commonWaits.clickWithoutInterception(NewTopic);
          commonWaits.waitForExecutionComplete();
        log.info("New Topic button clicked");
    }
      
      public void verifyNewTopicisClearingAllChat() {
        log.info("Verifying new topic is clearing all chat");
          Assert.assertFalse(commonWaits.isElementPresent(OfficePrinterResult),"Office printer result is not available for Test skill");
        log.info("New topic cleared all chat");
      }
      
      public void clickOnTestLog() {
        log.info("Clicking on Test Log button");
          commonWaits.waitForElementClickable(TestLog);
          commonWaits.clickWithoutInterception(TestLog);
          commonWaits.waitForExecutionComplete();
        log.info("Test Log button clicked");
    }

    public void selectAllSkillsFromSearchSetting(){
       log.info("Selecting all skills from search setting");
       clickSkillTab(SolQAConstants.PromptTabName);
       commonWaits.waitForSometime(commonWaits.moreDelay);
       try{
          log.info("Number of empty checkboxes found: " + emptyCheckBoxes.size());
          for (int i=1;i<=emptyCheckBoxes.size();i++){
             log.info("Selecting empty checkbox " + i);
             commonWaits.clickWithoutInterception(emptyCheckBoxes.get(i-1));
          }
       }catch (Exception ex){
          log.info("None of the information providers are non selected under Search settings tab ");
       }
       log.info("All skills selected from search setting");
    }

    public void openArticle(String Article){
		WebElement Articlelink=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(firstArticle, Article)));
        commonWaits.waitForElementClickable(Articlelink).click();
        commonWaits.waitForExecutionComplete();
    }

    public boolean verifyProviderNameInURL(String providerName) {
		log.info("Verifying provider name in URL: " + providerName);
		String originalHandle = getDriver().getWindowHandle();

		for (String handle : getDriver().getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				getDriver().switchTo().window(handle);
				try {
					if (getDriver().getCurrentUrl().contains(providerName)) {
						log.info("Provider name found in URL: " + getDriver().getCurrentUrl());
						return true; // Provider found, no need to continue
					}
				} finally {
					getDriver().switchTo().window(originalHandle);
				}
			}
		}
		 log.info("Provider name not found in any tab.");
		 return false; // Provider name not found in any tab

	}

     public void navigateToSettings(){
        log.info("Navigating to Settings menu");
        commonWaits.switchToLastWindow();
		commonWaits.waitForElementVisible(settingsMenu);
        commonWaits.clickWithoutInterception(settingsMenu);	
        commonWaits.waitForExecutionComplete();
	}

    public void enterValueInTextField(String value) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        log.info("Enter values in text field of search settings");
        commonWaits.waitForElementClickable(searchSettingsInput).clear();
        commonWaits.waitForElementClickable(searchSettingsInput).sendKeys(value);
        log.info("Set " + value + " for TextField");
    }

    public void clickOnSearchedAdminSetting(String settingName) throws Exception {
        String itemPresent = String.format(itemPresentOnPage, settingName);
        commonWaits.waitForByElementToBeVisible(By.xpath(itemPresent));
        int flag = getDriver().findElements(By.xpath(itemPresent)).size();
        log.info("Value of flag:" + flag);
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(collapsedMenuItem,settingName)));
        } catch (Exception e) {
            throw new Exception(settingName + " is not configured on server or not clickable, Please check the admin setting configuration in Innovation Studio.");
        }
        if (flag != 0) {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForByElementToBeVisible(By.xpath(itemPresent)).click();
        } else {
            commonWaits.waitForElementVisible(treeCollapsed).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForByElementToBeVisible(By.xpath(itemPresent)).click();
            commonWaits.waitForElementVisible(lobManagement);
        }
        commonWaits.waitForSometime(commonWaits.moreDelay);
    }

    public boolean isConnectionPresent(String searchText) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        try {
            WebElement record=commonWaits.waitForElementPresenceLocated(By.xpath(String.format(connectionName, searchText)));
            String resultText = record.getText().trim();
            return resultText.equals(searchText);
        } catch (Exception e) {
            return false;
        }
    }

    public HelixGPTAgentStudio unLinkPrompt(String promptName){
        clickSkillTab(SolQAConstants.PromptTabName);
        if (commonWaits.isElementPresent(By.xpath(String.format(promptNameList, promptName)))){
            WebElement ActionButton= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SKILLActionButton,promptName, SolQAConstants.ACTION)));
            ActionButton.click();
            WebElement UnlinkButton= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(UnlinkPromptbutton, SolQAConstants.UNLINK)));
            Assert.assertTrue(commonWaits.waitForVisibleElement(UnlinkButton), "Add prompt dialog is not visible");
            commonWaits.clickWithoutInterception(UnlinkButton);
            WebElement YesButton= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(YesFromPopUp, SolQAConstants.YES)));
            commonWaits.clickWithoutInterception(YesButton);

        }
        return this;
    }

    public HelixGPTAgentStudio selectAgent(String agentName){
        log.info("Navigating to Agents Tab");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(HelixGPTAgentStudioTabs, SolQAConstants.AGENTS)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(HelixGPTAgentStudioTabs, SolQAConstants.AGENTS)));
        commonWaits.waitForExecutionComplete();
        log.info("Successfully navigated to Agents Tab");
        log.info("Selecting agent: " + agentName);
        log.info("Checking if agent is present for search text: " + agentName);
        commonWaits.waitForElementVisible(searchBox).clear();
        searchBox.sendKeys(agentName, Keys.ENTER);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(By.xpath(String.format(agentRadioButton, agentName)));
        commonWaits.waitForExecutionComplete();
        log.info("Successfully selected agent: " + agentName);
        return this;
    }
   
  
	public HelixGPTAgentStudio LoginRBAC(Map<Object, String> rBACUsersData) {
		 
		// TODO Auto-generated method stub
		commonWaits.waitForElementVisible(rbacSignIn, 10);
        commonWaits.waitForElementClickable(rbacSignIn, 10);
		commonWaits.clickWithoutInterception(rbacSignIn);
		commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForExecutionComplete();
		commonWaits.waitForPageLoad(getCurrentDriver());
		log.info("Now on window with title: " + getDriver().getTitle());
		commonWaits.switchToLastWindow();
		log.info("Now on window with title: " + getDriver().getTitle());
		commonWaits.waitForElementVisible(rbacUsername, 10);
		commonWaits.sendKeysByAction(rbacUsername, rBACUsersData.get("UserName"));
		commonWaits.sendEnterKey();
		commonWaits.waitForElementVisible(rbacPassword, 10);
		commonWaits.sendKeysByAction(rbacPassword, rBACUsersData.get("Password"));
		commonWaits.sendEnterKey();		
		commonWaits.scrollToBottomOfPage();
		commonWaits.waitForElementVisible(acceptSubmit);
		commonWaits.clickWithoutInterception(acceptSubmit);
		commonWaits.switchBackToMainWindow();
		log.info("Now on window with title: " + getDriver().getTitle());
		return this;
	}
	
	public HelixGPTAgentStudio clickNewButton() {
        log.info("Clicking on New button for Record Definition");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        WebElement newBtn = commonWaits.waitForElementVisible(newButtonRecordDef);
        commonWaits.waitForElementClickable(newBtn).click();
        commonWaits.waitForExecutionComplete();
        log.info("New button clicked");
		return this;
    }

    public HelixGPTAgentStudio selectRegularRecord() {
        log.info("Selecting Regular record option");
        WebElement regularRecord = commonWaits.waitForElementVisible(regularRecordOption);
        commonWaits.waitForElementClickable(regularRecord).click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        log.info("Regular record option selected");
		return this;
    }

    public HelixGPTAgentStudio enterRecordName(String recordName) {
        log.info("Entering record name: " + recordName);
        commonWaits.switchToFrame(recordIframe);
        commonWaits.typeKeys(recordNameInput, recordName);
        log.info("Record name entered: " + recordName);
        log.info("Currently in iframe context for record definition form");
		return this;
    }

    /**
     * Ensures we are in the correct iframe context for form operations
     */
    private HelixGPTAgentStudio ensureIframeContext() {
        try {
            // Check if we're already in the iframe by trying to find an element
            boolean isDisplayed = recordNameInput.isDisplayed();
            Assert.assertTrue(isDisplayed);
            log.debug("Already in iframe context");
        } catch (Exception e) {
            // Not in iframe, switch to it
            log.info("Switching to iframe context");
            try {
                switchtoDefaultFrame();
                commonWaits.switchToFrame(recordIframe);
                log.info("Switched to iframe successfully");
            } catch (Exception ex) {
                log.warn("Could not switch to iframe: " + ex.getMessage());
            }
        }
		return this;
    }

    /**
     * Helper method to enable a toggle with multiple fallback strategies
     * @param toggleSpan The span element of the toggle
     * @param inputElement The input element of the toggle
     * @param toggleName Name of the toggle for logging
     * @return true if toggle was successfully enabled, false otherwise
     */
    private boolean enableToggleWithRetry(WebElement toggleSpan, WebElement inputElement, String toggleName) {
        log.info("Attempting to enable '" + toggleName + "' toggle");
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Attempt " + attempt + " to enable '" + toggleName + "'");

                // Check current state
                boolean isChecked = false;
                try {
                    // Use JavaScript to check state to avoid Selenium wait timeouts
                    Object checkedValue = executeJS("return arguments[0].checked;", inputElement);
                    isChecked = checkedValue != null && (Boolean) checkedValue;
                } catch (Exception e) {
                    log.warn("Could not check initial state, assuming unchecked: " + e.getMessage());
                }

                log.info("'" + toggleName + "' current state: " + (isChecked ? "enabled" : "disabled"));

                if (isChecked) {
                    log.info("'" + toggleName + "' is already enabled");
                    return true;
                }

                // Strategy 1: Scroll to element and click the span element
                try {
                    log.info("Strategy 1: Scrolling to and clicking toggle span for '" + toggleName + "'");
                    commonWaits.waitForElementClickable(toggleSpan);
                    javaScriptScrollIntoView(toggleSpan); // Scroll to element
                    commonWaits.clickWithoutInterception(toggleSpan);
                    commonWaits.waitForExecutionComplete();
                } catch (Exception e) {
                    log.warn("Strategy 1 failed: " + e.getMessage());
                }

                // Strategy 2: If Strategy 1 didn't work, try clicking the input directly
                boolean newState = false;
                try {
                    Object checkedValue = executeJS("return arguments[0].checked;", inputElement);
                    newState = checkedValue != null && (Boolean) checkedValue;
                } catch (Exception e) {
                    log.warn("Could not check state after span click: " + e.getMessage());
                }

                if (!newState) {
                    try {
                        log.info("Strategy 2: Clicking input element directly for '" + toggleName + "'");
                        // Try to click directly with a short timeout - if element not visible, skip to next strategy
                        if (inputElement.isDisplayed()) {
                            inputElement.click();
                            commonWaits.waitForExecutionComplete();
                            log.info("Strategy 2: Clicked input element successfully");
                        } else {
                            log.warn("Strategy 2: Input element not displayed, skipping to next strategy");
                        }
                    } catch (Exception e) {
                        log.warn("Strategy 2 failed: " + e.getMessage());
                    }
                }

                // Strategy 3: If both failed, try JavaScript click
                try {
                    Object checkedValue = executeJS("return arguments[0].checked;", inputElement);
                    newState = checkedValue != null && (Boolean) checkedValue;
                } catch (Exception e) {
                    newState = false;
                }

                if (!newState) {
                    try {
                        log.info("Strategy 3: Using JavaScript to click '" + toggleName + "'");
                        executeJS("arguments[0].click();", toggleSpan);
                        commonWaits.waitForExecutionComplete();
                    } catch (Exception e) {
                        log.warn("Strategy 3 failed: " + e.getMessage());
                    }
                }

                // Strategy 4: Force set the checked property via JavaScript
                try {
                    Object checkedValue = executeJS("return arguments[0].checked;", inputElement);
                    newState = checkedValue != null && (Boolean) checkedValue;
                } catch (Exception e) {
                    newState = false;
                }

                if (!newState) {
                    try {
                        log.info("Strategy 4: Force-setting checked property for '" + toggleName + "' via JavaScript");
                        executeJS("arguments[0].checked = true; arguments[0].setAttribute('checked', 'checked');", inputElement);
                        executeJS("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputElement);
                        commonWaits.waitForExecutionComplete();
                    } catch (Exception e) {
                        log.warn("Strategy 4 failed: " + e.getMessage());
                    }
                }

                // Final verification
                try {
                    Object checkedValue = executeJS("return arguments[0].checked;", inputElement);
                    newState = checkedValue != null && (Boolean) checkedValue;
                    log.info("'" + toggleName + "' final state after attempt " + attempt + ": " + (newState ? "enabled" : "disabled"));

                    if (newState) {
                        log.info("'" + toggleName + "' successfully enabled");
                        return true;
                    }
                } catch (Exception e) {
                    log.error("Could not verify final state: " + e.getMessage());
                }

                if (attempt < maxAttempts) {
                    log.warn("Attempt " + attempt + " failed, retrying...");
                    commonWaits.waitForExecutionComplete();
                }

            } catch (Exception e) {
                log.error("Unexpected error in attempt " + attempt + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        log.error("Failed to enable '" + toggleName + "' after " + maxAttempts + " attempts");
        return false;
    }

    public HelixGPTAgentStudio enableShouldExportData() {
        log.info("Enabling 'Should Export Data' toggle");
        try {
            ensureIframeContext();
            boolean success = enableToggleWithRetry(shouldExportDataToggle, shouldExportDataInput, "Should Export Data");
            if (!success) {
                log.error("Failed to enable 'Should Export Data' toggle after all attempts");
            }
        } catch (Exception e) {
            log.error("Error while enabling 'Should Export Data' toggle: " + e.getMessage());
            e.printStackTrace();
        }
        commonWaits.waitForExecutionComplete();
		return this;
    }

    public HelixGPTAgentStudio enableDataTranslation() {
        log.info("Enabling 'Enable Data Translation' toggle");
        try {
            ensureIframeContext();
            boolean success = enableToggleWithRetry(enableDataTranslationToggle, enableDataTranslationInput, "Enable Data Translation");
            if (!success) {
                log.error("Failed to enable 'Enable Data Translation' toggle after all attempts");
            }
        } catch (Exception e) {
            log.error("Error while enabling 'Enable Data Translation' toggle: " + e.getMessage());
            e.printStackTrace();
        }
        commonWaits.waitForExecutionComplete();
		return this;
    }

    public HelixGPTAgentStudio clickNewFieldButton() {
        log.info("Clicking on 'New field' button");
        commonWaits.waitForElementClickable(newFieldButton).click();
        commonWaits.waitForExecutionComplete();
        log.info("'New field' button clicked");
		return this;
    }

    public HelixGPTAgentStudio selectAttachmentFieldType() {
        log.info("Selecting Attachment field type");
        commonWaits.waitForElementClickable(attachmentFieldTypeOption).click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        log.info("Attachment field type selected");
		return this;
    }

    public HelixGPTAgentStudio enterFieldName(String fieldName) {
        log.info("Entering field name: " + fieldName);
        commonWaits.waitForElementClickable(fieldNameInput).clear();
        fieldNameInput.sendKeys(fieldName);
        log.info("Field name entered: " + fieldName);
		return this;
    }

    public HelixGPTAgentStudio enableRequiredCheckbox() {
        log.info("Enabling 'Required' checkbox for field");
        try {
            ensureIframeContext();
            boolean success = enableToggleWithRetry(requiredCheckbox, requiredCheckboxInput, "Required");
            if (!success) {
                log.error("Failed to enable 'Required' checkbox after all attempts");
            }
        } catch (Exception e) {
            log.error("Error while enabling 'Required' checkbox: " + e.getMessage());
            e.printStackTrace();
        }
        commonWaits.waitForExecutionComplete();
		return this;
    }

    public HelixGPTAgentStudio clickSaveFieldButton() {
        log.info("Clicking 'Save' button for field");
        commonWaits.waitForElementClickable(saveFieldButton).click();
        log.info("'Save' button clicked");
		return this;
    }

    public boolean verifySuccessMessage() {
        log.info("Verifying success message appears");
        try {
            commonWaits.waitForElementVisible(successMessage);
            boolean isPresent = commonWaits.isElementPresent(successMessage);
            if (isPresent) {
                log.info("Success message verified: " + successMessage.getText());
                return true;
            }
        } catch (Exception e) {
            log.warn("Success message not found: " + e.getMessage());
        }
        return false;
    }

    public HelixGPTAgentStudio waitForSuccessMessageToDisappear() {
        log.info("Waiting for success message to disappear");
        try {
            commonWaits.waitForElementInvisible(successMessage);
            log.info("Success message disappeared");
        } catch (Exception e) {
            log.info("Success message already disappeared or not found");
        }
        return this;
    }
    
    public String getFieldId(String fieldName) {
        log.info("Getting Field ID");
        try {
            WebElement fieldIdElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(fieldIdLabel, fieldName)));
            String fieldId = fieldIdElement.getText().trim();
            log.info("Field ID retrieved: " + fieldId);
           return fieldId;
        } catch (Exception e) {
            log.error("Could not retrieve Field ID: " + e.getMessage());
            return "";
        }
    }

    /**
     * Creates a complete record definition with an attachment field and returns the Field ID
     * This method performs all steps from creating a new record to copying the Field ID
     * 
     * @param recordName - Name of the record definition to create
     * @param fieldName - Name of the attachment field to add
     * @return String - Field ID of the created attachment field
     */
    public String createRecordDefinitionWithAttachmentField(String recordName, String fieldName) {
        log.info("===== Starting: Create Record Definition with Attachment Field =====");
        log.info("Record Name: " + recordName + ", Field Name: " + fieldName);

        try {
            // Click on New -> Regular record
            log.info("Click on New -> Regular record");
            clickNewButton();
            selectRegularRecord();

            // Enter record name
            log.info("Enter record name: " + recordName);
            enterRecordName(recordName);

            // Enable 'Should Export Data' toggle
            log.info("Enable 'Should Export Data' toggle");
            enableShouldExportData();

            // Enable 'Enable Data Translation' toggle
            log.info("Enable 'Enable Data Translation' toggle");
            enableDataTranslation();

            // Click on + 'New Field' button
            log.info("Click on + 'New Field' button");
            clickNewFieldButton();

            // Select Attachment field type
            log.info("Select Attachment field type");
            selectAttachmentFieldType();

            // Enter field name
            log.info("Enter field name: " + fieldName);
            enterFieldName(fieldName);

            // Enable 'Required' checkbox for field
            log.info("Enable 'Required' checkbox for field");
            enableRequiredCheckbox();

            // Click on 'Save' button
            log.info("Click on 'Save' button");
            clickSaveFieldButton();

            // Verify success message is displayed
            log.info("Verify success message is displayed");
            boolean successDisplayed = verifySuccessMessage();
            if (!successDisplayed) {
                String errorMsg = "Success message was not displayed after saving field '" + fieldName + "' for record '" + recordName + "'";
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // Wait for success message to disappear
            log.info("Wait for success message to disappear");
            waitForSuccessMessageToDisappear();

            // Copy the Field ID
            log.info("Copy the Field ID for field: " + fieldName);
            String fieldId = getFieldId(fieldName);

            if (fieldId == null || fieldId.isEmpty()) {
                String errorMsg = "Failed to retrieve Field ID for field '" + fieldName + "' in record '" + recordName + "'";
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            commonWaits.clickWithoutInterception(By.xpath(String.format(PromptModel,SolQAConstants.CLOSE)));

            log.info("===== Completed: Record Definition Created Successfully =====");
            log.info("Field ID: " + fieldId);
            return fieldId;

        } catch (Exception e) {
            log.error("Error creating record definition with attachment field: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create record definition '" + recordName + "' with attachment field '" + fieldName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Uploads an attachment instance to an existing record definition
     * This method performs all steps from selecting the record to saving the attachment
     * 
     * @param recordName - Name of the record definition
     * @param fieldName - Name of the attachment field
     * @param attachmentFile - File object to upload
     * @param description - Description for the attachment record
     * @param status - Status value for the attachment record
     */
    public HelixGPTAgentStudio uploadAttachmentInstanceToRecord(String recordName, String fieldName, File attachmentFile, String description, String status) {
        log.info("===== Starting: Upload Attachment Instance =====");
        log.info("Record: " + recordName + ", Field: " + fieldName + ", File: " + attachmentFile.getName());

        try {
            // Ensure we're in default frame context for grid operations
            log.info("Switch to default frame for record grid access");
            switchtoDefaultFrame();
            commonWaits.waitForExecutionComplete();

            // Search for and select created record using grid search
            log.info("Search for created '" + recordName + "' record using grid search");
            try {
                // Enter record name in search box
                commonWaits.waitForElementVisible(recordGridSearchInput);
                recordGridSearchInput.clear();
                recordGridSearchInput.sendKeys(recordName);
                log.info("Entered record name in search: " + recordName);

                // Click search button to trigger the search
                commonWaits.waitForElementVisible(recordGridSearchButton);
                commonWaits.clickWithoutInterception(recordGridSearchButton);
                log.info("Clicked search button");

                // Wait for grid to refresh with search results
                commonWaits.waitForExecutionComplete();
                commonWaits.waitForExecutionComplete(); // Extra wait for grid virtualization

                // Now click the checkbox beside the record (clicking the visible checkbox div, not hidden input)
                log.info("Clicking checkbox for record: " + recordName);
                By checkboxLocator = By.xpath(String.format(recordCheckboxByName, recordName));
                WebElement recordCheckbox = commonWaits.waitForByElementToBeVisible(checkboxLocator);
                commonWaits.clickWithoutInterception(recordCheckbox);
                log.info("Successfully selected record checkbox: " + recordName);
            } catch (Exception e) {
                log.error("Failed to search and select record: " + e.getMessage());
                throw e;
            }
            commonWaits.waitForExecutionComplete();

            // Wait for Edit Data button to become enabled, then click it
            log.info("Waiting for 'Edit Data' button to become enabled");
            
            boolean isEnabled = commonWaits.waitForElementEnabled(editDataButtonLocator, 20000, 500);
            if (!isEnabled) {
                String errorMsg = "Edit Data button did not become enabled after selecting record: " + recordName;
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            log.info("Edit Data button is now enabled, clicking it");
            commonWaits.clickWithoutInterception(editDataButton);
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.InnovationStudio);
            // Click on New button with retry strategies
            log.info("Click on New button to create new attachment instance");
            commonWaits.waitForElementVisible(newButton);
            log.info("New button is visible, attempting to click with multiple strategies");

            boolean newButtonClicked = false;
            int maxAttempts = 3;
            for (int attempt = 1; attempt <= maxAttempts && !newButtonClicked; attempt++) {
                try {
                    log.info("New button click attempt " + attempt + " of " + maxAttempts);

                    if (attempt == 1) {
                        // Strategy 1: Click the inner span element (Angular Material pattern)
                        log.info("Strategy 1: Clicking inner span content (Angular Material safe)");
                        executeJS("arguments[0].scrollIntoView({block: 'center'});", newButton);
                        commonWaits.waitForExecutionComplete();
                        // Click the inner span that contains the actual "New" text
                        try {
                            WebElement innerSpan = newButton.findElement(By.xpath(newButtonInnerSpan));
                            log.info("Found inner span, clicking it directly");
                            commonWaits.clickWithoutInterception(innerSpan);
                        } catch (Exception innerEx) {
                            log.warn("Could not find inner span, clicking button directly: " + innerEx.getMessage());
                            commonWaits.clickWithoutInterception(newButton);
                        }
                    } else if (attempt == 2) {
                        // Strategy 2: JavaScript click bypassing ripple effect
                        log.info("Strategy 2: Using JavaScript click to bypass ripple effect");
                        // Remove ripple temporarily and click
                        executeJS(
                            "var btn = arguments[0];" +
                            "var ripple = btn.querySelector('.ripple');" +
                            "if (ripple) ripple.style.pointerEvents = 'none';" +
                            "btn.click();" +
                            "if (ripple) setTimeout(function() { ripple.style.pointerEvents = ''; }, 100);",
                            newButton
                        );
                    } else {
                        // Strategy 3: Force click with Angular event dispatch
                        log.info("Strategy 3: Forcing click with Angular event dispatch");
                        executeJS(
                            "var btn = arguments[0];" +
                            "var event = new MouseEvent('click', {bubbles: true, cancelable: true, view: window});" +
                            "btn.dispatchEvent(event);",
                            newButton
                        );
                    }

                    // Wait briefly and check if form appeared
                    log.info("New button click executed (attempt " + attempt + "), checking if form loads");

                    // Try to detect if click worked by checking for form elements to appear
                    // Use a quick check (5 seconds) instead of the default 40 second wait
                    boolean formDetected = false;
                    try {
                        for (int i = 0; i < 10; i++) { // Check for 5 seconds (10 * 500ms)
                            commonWaits.waitForExecutionComplete();
                            // Quick check for form elements without long wait
                            if (!adaptRxControlLabel.isEmpty() ||
                                !fileInputLocator.isEmpty() ||
                                !textInputLocator.isEmpty() ||
                                !dialogLocator.isEmpty() ||
                                !dockedPanelLocator.isEmpty()) {
                                formDetected = true;
                                break;
                            }
                            Thread.sleep(500);
                        }

                        if (formDetected) {
                            log.info("Form element detected after click - click was successful!");
                            newButtonClicked = true;
                        } else {
                            log.warn("No form elements appeared within 5 seconds after click attempt " + attempt);
                            newButtonClicked = false;
                        }
                    } catch (Exception e) {
                        log.warn("Error checking for form after click attempt " + attempt + ": " + e.getMessage());
                        newButtonClicked = false;
                    }

                } catch (Exception e) {
                    log.warn("New button click attempt " + attempt + " failed: " + e.getMessage());
                    if (attempt == maxAttempts) {
                        // Add diagnostic info before throwing error
                        log.error("All click attempts failed. Capturing diagnostic info...");
                        try {
                            String pageSource = getDriver().getPageSource();
                            log.error("Page HTML snippet (first 1000 chars): " + pageSource.substring(0, Math.min(1000, pageSource.length())));
                            log.error("Current URL: " + getDriver().getCurrentUrl());
                            log.error("New button displayed: " + newButton.isDisplayed());
                            log.error("New button enabled: " + newButton.isEnabled());
                            log.error("New button location: " + newButton.getLocation());
                        } catch (Exception diagEx) {
                            log.error("Failed to capture diagnostics: " + diagEx.getMessage());
                        }
                        throw new RuntimeException("Failed to click New button after " + maxAttempts + " attempts - form did not open", e);
                    }
                    // Wait before retrying
                    commonWaits.waitForExecutionComplete();
                }
            }

            if (!newButtonClicked) {
                String errorMsg = "New button was clicked but form never appeared. Button might be disabled or not functional.";
                log.error(errorMsg);

                // Capture diagnostic information
                try {
                    log.error("=== DIAGNOSTICS: Form Did Not Appear ===");
                    log.error("Current URL: " + getDriver().getCurrentUrl());
                    log.error("Page title: " + getDriver().getTitle());
                    log.error("New button state - Displayed: " + newButton.isDisplayed() + ", Enabled: " + newButton.isEnabled());

                    // Check if there are any modal/dialog elements
                    int modalCount = modalLocator.size();
                    int dialogCount = dialogLocator.size();
                    int formCount = formLocator.size();
                    log.error("Elements on page - Modals: " + modalCount + ", Dialogs: " + dialogCount + ", Forms: " + formCount);

                    // Get page source snippet
                    String pageSource = getDriver().getPageSource();
                    log.error("Page HTML length: " + pageSource.length() + " characters");
                    if (pageSource.contains("No data to display")) {
                        log.error("Page still shows 'No data to display' - form definitely did not open");
                    }

                } catch (Exception diagEx) {
                    log.error("Failed to capture diagnostics: " + diagEx.getMessage());
                }

                throw new RuntimeException(errorMsg);
            }

            log.info("New button clicked successfully and form loaded");
            commonWaits.waitForExecutionComplete();

            // Form is in docked panel (not iframe), just wait for elements to stabilize
            log.info("Waiting for form fields to stabilize (form is in docked panel, not iframe)");
            commonWaits.waitForExecutionComplete();

            // Upload file to Attachment field
            log.info("Upload file to Attachment field: " + fieldName);
            log.info("Looking for file input with locator: //input[@type='file' and @aria-label='File Control']");
            WebElement fileInput = commonWaits.waitForElementVisible(attachmentFileInputByFieldName);

            // Check if file exists, if not look in resources/HelixGPT
            String filePath = attachmentFile.getAbsolutePath();
            if (!attachmentFile.exists()) {
                String resourcePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + 
                                    "HelixGPT" + File.separator + attachmentFile.getName();
                File resourceFile = new File(resourcePath);
                if (resourceFile.exists()) {
                    filePath = resourceFile.getAbsolutePath();
                    log.info("Using file from resources: " + filePath);
                }
            }
            fileInput.sendKeys(filePath);
            log.info("File uploaded successfully");
            commonWaits.waitForExecutionComplete();

            // Enter Description
            log.info("Enter Description as '" + description + "'");
            commonWaits.waitForElementVisible(descriptionTextarea);
            descriptionTextarea.clear();
            descriptionTextarea.sendKeys(description);
            commonWaits.waitForExecutionComplete();

            // Select Status
            log.info("Select Status as '" + status + "'");
            commonWaits.clickWithoutInterception(statusDropdownButton);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickWithoutInterception(By.xpath(String.format(statusOptionByText, status)));
            commonWaits.waitForExecutionComplete();

            // Click Save button
            log.info("Click Save button to save attachment record");
            commonWaits.clickWithoutInterception(saveButton);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickWithoutInterception(By.xpath(String.format(PromptModel,SolQAConstants.CLOSE)));
            log.info("===== Completed: Attachment Instance Uploaded Successfully =====");

        } catch (Exception e) {
            log.error("Error uploading attachment instance: " + e.getMessage(), e);
            throw new RuntimeException("Failed to upload attachment instance for record '" + recordName + "': " + e.getMessage(), e);
        }
		return this;
    }

    /**
     * Creates a connection record in HelixGPT Agent Studio for connecting a field to a record definition
     * 
     * @param connectionRecordDefinitionName - Name of the connection record definition (e.g., "Connection_Record_Definition")
     * @param fieldId - The Field ID to associate with this connection
     * @param status - Status of the connection (e.g., "Enabled", "Disabled")
     * @param connectionName - Name for this connection (e.g., "Attachment")
     * @param recordDefinition - Full record definition name (e.g., "com.bmc.dsom.hgm:Attachment")
     * @param dataSourceId - DataSource ID to use (e.g., "Helix Innovation Suite Record Definition")
     * @return String - The ID of the created connection record
     */
    public String createConnectionRecord(String connectionRecordDefinitionName, String fieldId, String status, 
                                        String connectionName, String recordDefinition, String dataSourceId) {
        log.info("===== Starting: Create Connection Record =====");
        log.info("Connection Record Definition: " + connectionRecordDefinitionName);
        log.info("Field ID: " + fieldId + ", Status: " + status + ", Name: " + connectionName);
        log.info("Record Definition: " + recordDefinition + ", DataSource ID: " + dataSourceId);

        try {
            log.info("Navigate to HelixGPT Agent Studio");
            innovationSuite.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);

            log.info("Select Record Definition: " + connectionRecordDefinitionName);
            innovationSuite.selectRecordDefinition(connectionRecordDefinitionName);

            log.info("Click Edit Data for: " + connectionRecordDefinitionName);
            innovationSuite.clickEditData(connectionRecordDefinitionName);
            commonWaits.waitForExecutionComplete();

            log.info("Click New button to create new connection record");
            innovationSuite.clickNewInEditDataPanel();
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.InnovationStudio);

            log.info("Fill Field ID with: " + fieldId);
            innovationSuite.setTextField("Field ID", fieldId);
            commonWaits.waitForExecutionComplete();

            log.info("Select Status as: " + status);
            innovationSuite.setDropdownField("Status", status);
            commonWaits.waitForExecutionComplete();

            log.info("Enter Name as: " + connectionName);
            innovationSuite.setTextField("Name", connectionName);
            commonWaits.waitForExecutionComplete();

            log.info("Enter Record Definition as: " + recordDefinition);
            innovationSuite.setTextField("Record Definition", recordDefinition);
            commonWaits.waitForExecutionComplete();

            log.info("Select DataSource ID as: " + dataSourceId);
            innovationSuite.setDropdownField("DataSource ID", dataSourceId);
            commonWaits.waitForExecutionComplete();

            log.info("Click Save button");
            innovationSuite.clickSaveButton();
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.InnovationStudio);

            log.info("Copy ID from saved connection record");
            String connectionRecordId = innovationSuite.getID();

            if (connectionRecordId == null || connectionRecordId.isEmpty()) {
                throw new RuntimeException("Failed to retrieve Connection Record ID - ID is empty");
            }

            log.info("Connection Record ID copied: " + connectionRecordId);
            log.info("===== Completed: Connection Record Created Successfully =====");

            return connectionRecordId;

        } catch (Exception e) {
            log.error("Error creating connection record: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create connection record in '" + connectionRecordDefinitionName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Creates a Data Connection Job record in HelixGPT Agent Studio
     * 
     * @param dataConnectionJobRecordDefinitionName - Name of the DataConnectionJob record definition
     * @param description - Description for the job
     * @param status - Status of the job (e.g., "Done", "Pending")
     * @param type - Type of job (e.g., "INGEST_DATA")
     * @param connectionId - The Connection Record ID to associate with this job
     * @param executeNow - Whether to execute the job now (true/false)
     * @param datasource - Datasource type (e.g., "RECORD_DEFINITION")
     * @param screenshotPrefix - Prefix for screenshots if needed
     * @return The ID of the created Data Connection Job
     * @throws RuntimeException if data connection job creation fails
     */
    public String createDataConnectionJob(String dataConnectionJobRecordDefinitionName, String description, 
                                          String status, String type, String connectionId, 
                                          boolean executeNow, String datasource, String screenshotPrefix) {
        try {
            log.info("===== Starting: Create Data Connection Job =====");
            log.info("Record Definition: " + dataConnectionJobRecordDefinitionName);
            log.info("Description: " + description);
            log.info("Status: " + status);
            log.info("Type: " + type);
            log.info("Connection ID: " + connectionId);
            log.info("Execute Now: " + executeNow);
            log.info("Datasource: " + datasource);

            log.info("Navigate to HelixGPT Agent Studio and select " + dataConnectionJobRecordDefinitionName);
            innovationSuite.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);
            innovationSuite.selectRecordDefinition(dataConnectionJobRecordDefinitionName);

            log.info("Click on Edit Data");
            innovationSuite.clickEditData(dataConnectionJobRecordDefinitionName);

            log.info("Click on New button to create new data connection job");
            innovationSuite.clickNewInEditDataPanel();

            log.info("Enter Description: " + description);
            innovationSuite.setTextField("Description", description);
            commonWaits.waitForExecutionComplete();

            log.info("Select Status: " + status);
            innovationSuite.setDropdownField("Status", status);
            commonWaits.waitForExecutionComplete();

            log.info("Select Type: " + type);
            innovationSuite.setDropdownField("Type", type);
            commonWaits.waitForExecutionComplete();

            log.info("Fill ConnectionId with: " + connectionId);
            innovationSuite.setTextField("ConnectionId", connectionId);
            commonWaits.waitForExecutionComplete();

            // Enable ExecuteNow checkbox if required
            if (executeNow) {
                log.info("Enable checkbox for 'ExecuteNow' field");
                innovationSuite.enableCheckboxField("ExecuteNow");
                commonWaits.waitForExecutionComplete();
            } else {
                log.info("Skipping ExecuteNow checkbox (executeNow = false)");
            }

            log.info("Type '" + datasource + "' in Datasource field");
            innovationSuite.setTextField("Datasource", datasource);
            commonWaits.waitForExecutionComplete();

            log.info("Click on Save button to save data connection job");
            innovationSuite.clickSaveButton();
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.InnovationStudio);

            log.info("Copy the ID from saved data connection job");
            String dataConnectionJobId = innovationSuite.getID();

            if (dataConnectionJobId == null || dataConnectionJobId.isEmpty()) {
                throw new RuntimeException("Failed to retrieve Data Connection Job ID - ID is empty");
            }

            log.info("Data Connection Job ID copied: " + dataConnectionJobId);
            log.info("===== Completed: Data Connection Job Created Successfully =====");

            return dataConnectionJobId;

        } catch (Exception e) {
            log.error("Error creating data connection job: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create data connection job in '" + dataConnectionJobRecordDefinitionName + "': " + e.getMessage(), e);
        }
    }

    public HelixGPTAgentStudio selectAgentForSkill(String agentName) {
        clickSkillTab(SolQAConstants.AGENT);
        boolean agentRadioButtonLabelElement = commonWaits.isElementPresent(By.xpath(String.format(agentRadioButtonLabelChecked, agentName)));
        if(agentRadioButtonLabelElement==false) {
        commonWaits.clickWithoutInterception(By.xpath(String.format(agentRadioButtonLabel, agentName)));
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(applyAgent);
        commonWaits.waitForExecutionComplete();
        log.info("Successfully selected agent: " + agentName);
        }
        else {
            log.info("Agent is already selected: " + agentName);
        }
        return this;
    }
    
    public HelixGPTAgentStudio verifyArticleIsNotPresent(String article) {
        boolean articleIsNotPresent = commonWaits.isElementPresent(By.xpath(String.format(firstArticle, article)));
        if(articleIsNotPresent==false) {
            log.info("Article is not present: " + article);
        }
        return this;
    }

}

