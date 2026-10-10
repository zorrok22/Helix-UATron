package pages.ITSM.CSM;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITSM.InnovationSuite.InnovationSuite;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CSMDashboardPage extends InnovationSuite {
	
	static SolqaLogger log;
	CommonWaits commonWaits;

	public CSMDashboardPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(CSMDashboardPage.class.getName());
		commonWaits=new CommonWaits();
	}

    //String Locators
    public String buttonContainsText = "//button[contains(.,'%s')]";
    public String buttonContainsID = "//button[contains(@id,'%s')]";
    public String inputContainsText = "//input[contains(@id,'%s')]";
    public String getDropdownOptionByText = "//adapt-rx-select[contains(.,'%s')]//div[contains(@class,'select__search')]";
    public String linkByContainsText = "//a[@role='link' and normalize-space()='%s']";
    public String configuredCompanyTableRow = "//*[@rx-view-component-id='31e66dc0-ce9d-4cb4-bbb5-11045605a55c']//td[contains(normalize-space(.), '%s')]";
    public String dropdownField = "//adapt-rx-select[contains(normalize-space(.), '%s')]//button";
    public String dropdownOption = "//button[@role='option' and contains(normalize-space(.), '%s')]";
    public String supportGroupCompanyRadioBtn = "//td[contains(normalize-space(.), '%s')]//input[@type='radio']";
    public String editClassificationCSMParametersTableRow = "//*[@rx-view-component-id='81dc4569-94a0-470c-aad5-00ad0e7cffd6']//td[contains(normalize-space(.), '%s')]";
    public String setClassificationCSMParametersTableRow = "//*[@rx-view-component-id='58389435-1aa9-4077-93d9-0ec2d1927955']//td[contains(normalize-space(.), '%s')]//adapt-row-selection-checkbox";
    public String listItemTabByLabel = "//button[@role='listitem' and contains(normalize-space(.), '%s')]";
    public String caseSummaryValueInTable = "//*[@rx-view-component-id='87eb5577-b197-4022-9f98-bda65bb823fc']//td[contains(normalize-space(.), '%s')]";
    public String caseTemplateTableRow = "//*[@rx-view-component-id='b7e5999b-fb05-425e-93ef-45c9de7571bc']//td[contains(normalize-space(.), '%s')]";
    public String companyDefaultValuesTableRow = "//*[@rx-view-component-id='4a71694a-102b-48f9-b8c0-183d176eaa0b']//td[contains(normalize-space(.), '%s')]";
    public String customerProfileTableRow = "//*[@rx-view-component-id='d17b779c-2b60-4c35-a223-58ed0e8f265b']//td[contains(normalize-space(.), '%s')]";
    public String dropdownFieldByLabel = "//label[text()='%s']/parent::*//adapt-rx-select//button";
    public String LinkAvailableInHamburgerMenu = "//a[contains(@class, 'a-hamburger__menu-link') and normalize-space()='%s']";

    //WebElement Locators
    @FindBy(xpath = "//button[contains(@class, 'a-hamburger')]")
    public WebElement hamburgerMenuButton;
    @FindBy(xpath = "//*[contains(@class, 'a-toast--success')]//span[contains(. ,'Case with the  Reference ID  CS')]")
    public WebElement caseCreationSuccessToastMessage;
    @FindBy(xpath = "//*[@rx-view-component-id='b73af9d9-f485-436f-9b18-04e5d3f8c737']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement customerProfileSubmitButton;
    @FindBy(xpath = "//*[@rx-view-component-id='926b8e84-d8ac-4d4a-bb4c-5396c7ea57c3']//button[contains(normalize-space(.), 'Close')]")
    public WebElement customerProfileCloseButton;
    @FindBy(xpath = "//*[contains(@class, 'a-toast--success')]")
    public WebElement successToastMessage;
    @FindBy(xpath = "//*[@rx-view-component-id='ebe55057-a324-4ff5-b372-902fbc80af26']//button[@aria-label='Select options']")
    public WebElement activityNameSelectOption;
    @FindBy(xpath = "//adapt-rx-checkbox[contains(normalize-space(.), 'Select all loaded')]")
    public WebElement selectAllLoadedActivitiesCheckbox;
    @FindBy(xpath = "//*[@rx-view-component-id='7c0dfa91-acf4-4c95-a916-579a0ab4cebf']//button[contains(normalize-space(.), 'Add Activity')]")
    public WebElement addActivityButton;
    @FindBy(xpath = "//*[@rx-view-component-id='1634b08d-c7d0-410c-b39a-5664f5597b92']//adapt-empty-state")
    public WebElement emptyActivitiesGroupTable;
    @FindBy(xpath = "//*[@rx-view-component-id='9b7c442b-9d44-4ee4-9b0b-1d545baee093']//button[contains(normalize-space(.), 'New')]")
    public WebElement newCustomerProfileButton;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Edit')]")
    public WebElement editButton;
    @FindBy(xpath = "//*[@rx-view-component-id='16831a3a-30d3-437a-98a1-0e710b8014f1']//button[contains(normalize-space(.), 'Assign CSM Role')]")
    public WebElement assignCSMRoleButton;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Add Main Group')]")
    public WebElement addMainGroupButton;
    @FindBy(xpath = "//*[@rx-view-component-id='eb67d81b-4e3a-4ca9-8cdc-e631ee136207']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement addMainGroupSubmitButton;
    @FindBy(xpath = "//*[@rx-view-component-id='b4bebd07-59e3-4ab8-8056-2c4e0d835619']//button[contains(normalize-space(.), 'Close')]")
    public WebElement addMainGroupCloseButton;
    @FindBy(xpath = "//*[@rx-view-component-id='c57b09f9-e2bf-4082-aadf-976981d66402']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement addCSMRoleSubmitButton;
    @FindBy(xpath = "//*[@rx-view-component-id='273c4cbc-b854-4273-964c-cf29be8377f4']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement setCSMParametersSubmitButton;
    @FindBy(xpath = "//*[@rx-view-component-id='f393240e-2c26-4ed3-8066-daf31a2d7602']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement summariesAndQuestionsSubmitButton;
    @FindBy(xpath = "//adapt-rx-search[contains(normalize-space(.), 'Filter')]//input")
    public WebElement filterInput;
    @FindBy(xpath = "//*[@rx-view-component-id='8dd6d391-c464-4dd3-8c8d-1a87ff95013e']//button[contains(normalize-space(.), 'Add Case Template')]")
    public WebElement addCaseTemplateButton;
    @FindBy(xpath = "//*[@rx-view-component-id='b69b8b5a-c514-4843-94b7-28bed6ec2185']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement caseTemplateSubmitButton;
    @FindBy(xpath = "//*[@rx-view-component-id='7b882a8a-798a-4643-8595-913f1dd6537f']//button[contains(normalize-space(.), 'Close')]")
    public WebElement caseTemplateCloseButton;
    @FindBy(xpath = "//*[@rx-view-component-id='5fbfd18e-ff8e-4844-af9f-a63ec8fc2fae']//button[contains(normalize-space(.), 'Submit')]")
    public WebElement companyDefaultValuesSubmitButton;
    @FindBy(xpath = "//header[contains(.,'Customer Service Management')]")
    public WebElement csmHeader;
    @FindBy(xpath = "//*[contains(@class, 'a-hamburger__content-header') and contains(. ,'Customer Service Management')]")
    public WebElement csmHeaderInHamburgerMenu;
    @FindBy(xpath = "//button[contains(.,'Start Session')]")
    public WebElement startSessionButton;
    @FindBy(xpath = "//a[contains(.,'Open Console')]")
    public WebElement openConsoleLink;
    @FindBy(xpath = "//strong[contains(.,'Frontliner Workspace')]")
    public WebElement frontlinerWorkspaceHeader;
    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    public WebElement csmLogoutMenu;
    @FindBy(xpath = "//button[contains(text(),'Sign out')]")
    public WebElement csmSignOut;
    @FindBy(xpath = "//button[contains(.,'New Case')]")
    public WebElement csmNewCaseButton;
    @FindBy(xpath = "//adapt-rx-textfield[contains(.,'Customer ID')]//input")
    public WebElement caseCustomerIDInput;
    @FindBy(xpath = "//button[contains(.,'Fetch Customer Info')]")
    public WebElement fetchCustomerInfoButton;
    @FindBy(xpath = "//button[contains(.,'Next')]")
    public WebElement caseNextButton;
    @FindBy(xpath = "//button[contains(.,'Knowledge Base') and contains(@role,'tab')]")
    public WebElement caseKnowledgeBaseTab;
    @FindBy(xpath = "//p[contains(.,'No results found')]")
    public WebElement caseKnowledgeBaseNoResultsFound;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Case Nature')]//button")
    public WebElement caseNatureDropdown;
    @FindBy(xpath = "//adapt-rx-select[contains(.,'Case Template')]//button")
    public WebElement caseTemplateDropdown;
    @FindBy(xpath = "//button[contains(.,'Confirm')]")
    public WebElement confirmButton;
    @FindBy(xpath = "//*[@rx-view-component-id='15012462-6356-4767-a1ed-a4fdf567701b']//button[contains(.,'Submit')]")
    public WebElement caseSubmitButton;
    @FindBy(xpath = "//*[@rx-view-component-id='739330f0-420f-4b00-a44f-6f63d778f1fe']//button[contains(.,'Submit')]")
    public WebElement domainAssignSubmitBtn;
    @FindBy(xpath = "//adapt-close//button[@aria-label='Close']")
    public WebElement hamburgerCloseButton;
    @FindBy(xpath = "//*[@rx-view-component-id='03027250-9101-42b4-a46a-e5d03b6a876d']//button[contains(normalize-space(.), 'Fetch Customer Info')]")
    public WebElement caseFetchCustomerInfoButton;
    @FindBy(xpath = "//*[@rx-view-definition-guid='5b1ccb9e-c59d-4c9f-95fe-ca3c3698dadf']//*[contains(normalize-space(.), 'Case Template')]")
    WebElement caseTemplateTab;
    @FindBy(xpath = "//*[@rx-view-component-id='414566d1-89d9-4b11-90a5-2d8674805a44' and contains(. ,'Add CSM Roles')]")
    WebElement addCSMRoleHeader;
    @FindBy(xpath = "(//rx-runtime-view-modal[contains(normalize-space(.), 'Add Main Group')])[1]")
    WebElement addMainGroupHeader;
    
    //String variables
    public String roamingEnabledID = "radiobutton-7";
    public String invoiceNoId = "radiobutton-8";
    public String dateOfInvoiceId = "radiobutton-9";
    public String amountBilledId = "radiobutton-10";
    public String roamingEnabledDropdownID = "select-93";
    public String invoiceNoInputID = "textfield-95";
    public String dateOfInvoiceInputID = "datetime-96";
    public String amountBilledInputID = "textfield-97";
    private static final Pattern CASE_REF_ID_PATTERN = Pattern.compile("\\bCS\\d+\\b");
	
	public CSMDashboardPage verifyCSMPageLoaded() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), hamburgerMenuButton, 40)) {
            clickWithoutInterception(hamburgerMenuButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), csmHeaderInHamburgerMenu, 20), "CSM Header is not displayed");
            clickWithoutInterception(hamburgerCloseButton);
        } else {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), csmHeader, 20), "CSM Header is not displayed");
        }
        return this;
    }
	
	public CSMDashboardPage clickStartSessionButton() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		if(commonWaits.waitForElementVisible_withFluentWait(getDriver(), startSessionButton, 20)) {
			log.debug("Clicking on Start Session button");
            commonWaits.waitForElementClickable(startSessionButton).click();
		} else {
			log.debug("Clicking on Start Session link");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), openConsoleLink, 20), "Open Console link is not visible in CSM after clicking on Start Session button");
            commonWaits.waitForElementClickable(openConsoleLink).click();
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		return this;
	}
	
	public CSMDashboardPage verifyFrontlinerWorkspacePageLoaded() {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), frontlinerWorkspaceHeader, 20), "Frontliner Workspace Header is not displayed");
    	Assert.assertTrue(commonWaits.isElementPresent(frontlinerWorkspaceHeader), "Frontliner Workspace Header is not displayed");
    	return this;
    }
	
	public void csmLogout() {
    	clickWithoutInterception(csmLogoutMenu);
		clickWithoutInterception(csmSignOut);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }
	
	public CSMDashboardPage clickOnNewCaseButton() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), csmNewCaseButton, 20), "New Case button is not visible in CSM");
		clickWithoutInterception(csmNewCaseButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		commonWaits.switchToLastWindow();
		return this;
	}

    public CSMDashboardPage fillCustomerInformationData(String customerID) {
        //Add Customer ID and Fetch Details
        log.debug("Filling Customer Information with Customer ID: " + customerID);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseCustomerIDInput, 20), "Customer ID input field is not visible in Case Creation Console");
        commonWaits.clickWithoutInterception(caseCustomerIDInput);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        keyClearAndType(caseCustomerIDInput, customerID);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), fetchCustomerInfoButton, 20), "Fetch Customer Info button is not visible in Case Creation Console after entering Customer ID");
        commonWaits.clickByJS(fetchCustomerInfoButton);
        return this;
    }
	
	public CSMDashboardPage clickOnNextButton() {
		log.debug("Clicking Next button in Case Creation Console");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseNextButton, 20), "Next button is not visible in Case Creation Console");
		clickWithoutInterception(caseNextButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		return this;
	}
	
	public CSMDashboardPage clickOnSubmitButton() {
		log.debug("Clicking Next button in Case Creation Console");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseSubmitButton, 20), "Submit button is not visible in Case Creation Console");
		clickWithoutInterception(caseSubmitButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		return this;
	}
	
	public CSMDashboardPage verifyKnowledgeBaseNoResultsFound() {
		log.debug("Verifying No Results Found message in Knowledge Base tab");
		clickWithoutInterception(caseKnowledgeBaseTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		Assert.assertTrue(commonWaits.isElementPresent(caseKnowledgeBaseNoResultsFound), "No Results Found message is not displayed in Knowledge Base tab");
		return this;
	}
	
	public CSMDashboardPage fillCaseClassificationData(Map<Object, String> caseData) {
		//Add Case Nature and Case Template
		log.debug("Filling Case Classification with Case Nature: " + caseData.get(SolQAConstants.CASE_NATURE) + " and Case Template: " + caseData.get(SolQAConstants.CASE_TEMPLATE));
		clickWithoutInterception(caseNatureDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, caseData.get(SolQAConstants.CASE_NATURE))));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		clickWithoutInterception(caseTemplateDropdown);
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, caseData.get(SolQAConstants.CASE_TEMPLATE))));
		
		log.debug("Filling additional information");
		log.debug("Setting Roaming Enabled to Yes");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		clickWithoutInterception(By.xpath(String.format(inputContainsText, roamingEnabledID)));
		clickWithoutInterception(By.xpath(String.format(buttonContainsID,roamingEnabledDropdownID)));
		clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.YES)));
		clickWithoutInterception(confirmButton);
		
		log.debug("Setting Invoice Number");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		clickWithoutInterception(By.xpath(String.format(inputContainsText, invoiceNoId)));
		keyClearAndType(String.format(inputContainsText, invoiceNoInputID), caseData.get("invoiceNo"));
		clickWithoutInterception(confirmButton);
		
		log.debug("Setting Date of Invoice");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		clickWithoutInterception(By.xpath(String.format(inputContainsText, dateOfInvoiceId)));
		keyClearAndType(String.format(inputContainsText, dateOfInvoiceInputID), getCurrentDateTime("MM/dd/yyyy"));
		clickWithoutInterception(confirmButton);
		
		log.debug("Setting Amount Billed");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		clickWithoutInterception(By.xpath(String.format(inputContainsText, amountBilledId)));
		keyClearAndType(String.format(inputContainsText, amountBilledInputID), caseData.get("amountBilled"));
		clickWithoutInterception(confirmButton);
		
		return this;
	}
	
	public CSMDashboardPage verifyKnowledgeBaseDataPresent() {
		log.debug("Verifying Knowledge Base data is present");
		clickWithoutInterception(caseKnowledgeBaseTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
		Assert.assertFalse(commonWaits.isElementPresent(caseKnowledgeBaseNoResultsFound), "No Results Found message is not displayed in Knowledge Base tab");
		return this;
	}
	
	public CSMDashboardPage verifyAssignedMainGroupIsSelected() {
		log.debug("Verifying Assigned Main Group is selected");
		Assert.assertFalse(findElement(By.xpath(String.format(getDropdownOptionByText, "Assigned Main Group"))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).equalsIgnoreCase("Select"), "Assigned Main Group is not selected");
		return this;
	}
	
	public CSMDashboardPage verifyChannelIsSelected() {
		log.debug("Verifying Channel is selected");
		Assert.assertFalse(findElement(By.xpath(String.format(getDropdownOptionByText, "Channel"))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).equalsIgnoreCase("Select"), "Assigned Main Group is not selected");
		return this;
	}

    public CSMDashboardPage navigateToTab(String tabName) {
        log.debug("Navigating to tab: " + tabName);
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), hamburgerMenuButton, 10)) {
            clickWithoutInterception(hamburgerMenuButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(LinkAvailableInHamburgerMenu, tabName)), 20), "Tab: " + tabName + " is not visible in Hamburger menu");
            clickWithoutInterception(By.xpath(String.format(LinkAvailableInHamburgerMenu, tabName)));
        }
        else {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(linkByContainsText, tabName)), 20), "Tab: " + tabName + " is not visible in CSM");
            clickWithoutInterception(By.xpath(String.format(linkByContainsText, tabName)));
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        return this;
    }

    public CSMDashboardPage OpenTabFromCSMControlPanel(String tabName) {
        log.debug("Opening tab: " + tabName + " from CSM Control Panel");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, tabName)), 20), "Tab: " + tabName + " is not visible in CSM Control Panel");
        clickWithoutInterception(By.xpath(String.format(buttonContainsText, tabName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        return this;
    }

    public CSMDashboardPage setCoreConfiguration(Map<Object, String> coreConfigData) {

        if (coreConfigData.get("Company") != null) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Company_Label)), 20), "Company dropdown is not visible in CSM Control Panel");
            clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Company_Label)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Assign_CSM_Domain)), 20), "Assign CSM Domain button is not visible in CSM after clicking on Company dropdown");
            clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Assign_CSM_Domain)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownField, SolQAConstants.Company_Label)), 20), "Company dropdown field is not visible in CSM after clicking on Assign CSM Domain button");
            clickWithoutInterception(By.xpath(String.format(dropdownField, SolQAConstants.Company_Label)));
            commonWaits.waitForSometime(commonWaits.moreDelay);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterInput, 20), "Filter input field is not visible in Company dropdown in CSM");
            clickWithoutInterception(filterInput);
            filterInput.sendKeys(coreConfigData.get("Company"));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownOption, coreConfigData.get("Company"))), 20), "Company: " + coreConfigData.get("Company") + " is not visible in dropdown options in CSM");
            clickWithoutInterception(By.xpath(String.format(dropdownOption, coreConfigData.get("Company"))));
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(configuredCompanyTableRow, coreConfigData.get("Company"))), 10)) {
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownField, SolQAConstants.Domain_Label)), 20), "Domain dropdown field is not visible in CSM after selecting Company: " + coreConfigData.get("Company"));
                clickWithoutInterception(By.xpath(String.format(dropdownField, SolQAConstants.Domain_Label)));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterInput, 20), "Filter input field is not visible in Domain dropdown in CSM");
                clickWithoutInterception(filterInput);
                filterInput.sendKeys(coreConfigData.get("Domain"));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownOption, coreConfigData.get("Domain"))), 20), "Domain: " + coreConfigData.get("Domain") + " is not visible in dropdown options in CSM");
                clickWithoutInterception(By.xpath(String.format(dropdownOption, coreConfigData.get("Domain"))));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), domainAssignSubmitBtn, 20), "Submit button is not visible after selecting Domain: " + coreConfigData.get("Domain") + " for Company: " + coreConfigData.get("Company"));
                clickWithoutInterception(domainAssignSubmitBtn);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(configuredCompanyTableRow, coreConfigData.get("Company"))), 10), "Company: " + coreConfigData.get("Company") + " is not configured successfully in CSM");
            } else {
                log.debug("Company: " + coreConfigData.get("Company") + " is already configured in CSM");
            }
        }

        if (coreConfigData.get("Support Group") != null) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Support_Group)), 20), "Support Group button is not visible in CSM Control Panel");
            clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Support_Group)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownField, SolQAConstants.Company_Label)), 20), "Assign CSM Role button is not visible in CSM after clicking on Support Group button");
            clickWithoutInterception(By.xpath(String.format(dropdownField, SolQAConstants.Company_Label)));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownOption, coreConfigData.get("Company"))), 20), "Filter input field is not visible in Company dropdown in CSM after clicking on Support Group button");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            clickWithoutInterception(By.xpath(String.format(dropdownOption, coreConfigData.get("Company"))));
            commonWaits.waitForSometime(commonWaits.moreDelay);
            clickWithoutInterception(By.xpath(String.format(supportGroupCompanyRadioBtn, coreConfigData.get("Company"))));
            if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), assignCSMRoleButton, 5)) {
                clickWithoutInterception(assignCSMRoleButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addCSMRoleHeader, 20), "Add CSM Role header is not displayed after clicking on Assign CSM Role button in CSM");
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addMainGroupButton, 20), "Add Main Group button is not displayed after clicking on Assign CSM Role button in CSM");
                clickWithoutInterception(addMainGroupButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addMainGroupHeader, 20), "Add Main Group header is not displayed after clicking on Add Main Group button in CSM");
                setTextField(SolQAConstants.Group_Name, coreConfigData.get("Main Group"));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addMainGroupSubmitButton, 20), "Submit button is not visible after entering Group Name in Add Main Group window in CSM");
                clickWithoutInterception(addMainGroupSubmitButton);
                if(!commonWaits.waitForElementVisible_withFluentWait(getDriver(), successToastMessage, 5)){
                    log.debug("Main Group is already added for Support Group: " + coreConfigData.get("Support Group") + " in CSM, hence closing the Add Main Group window");
                }
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addMainGroupCloseButton, 10), "Close button is not visible in Add Main Group window in CSM after submitting new Main Group");
                clickWithoutInterception(addMainGroupCloseButton);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.Main_Group, coreConfigData.get("Main Group"));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownField, SolQAConstants.Group_Role)), 20), "Group Role dropdown field is not visible in CSM after selecting Main Group: " + coreConfigData.get("Group Name"));
                clickWithoutInterception(By.xpath(String.format(dropdownField, SolQAConstants.Group_Role)));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterInput, 20), "Filter input field is not visible in Group Role dropdown in CSM after selecting Main Group: " + coreConfigData.get("Group Name"));
                clickWithoutInterception(filterInput);
                filterInput.sendKeys(coreConfigData.get("Group Role"));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownOption, coreConfigData.get("Group Role"))), 10), "Group Role: " + coreConfigData.get("Group Role") + " is not visible in dropdown options in CSM");
                clickWithoutInterception(By.xpath(String.format(dropdownOption, coreConfigData.get("Group Role"))));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addCSMRoleSubmitButton, 20), "Submit button is not visible after selecting Group Role: " + coreConfigData.get("Group Role") + " in CSM");
                clickWithoutInterception(addCSMRoleSubmitButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), successToastMessage, 5), "Success toast message is not displayed after submitting new CSM role in CSM");
            } else {
                log.debug("Support Group for Company: " + coreConfigData.get("Company") + " is already configured in CSM");
            }
        }

        if (coreConfigData.get("Classification") != null) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Classification)), 20), "Classification button is not visible in CSM Control Panel");
            clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Classification)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownFieldByLabel, SolQAConstants.Select_Company)), 20), "Company dropdown field is not visible in CSM after clicking on Classification button");
            commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownFieldByLabel, SolQAConstants.Select_Company)));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownOption, coreConfigData.get("Company"))), 20), "Company: " + coreConfigData.get("Company") + " is not visible in dropdown options in CSM");
            clickWithoutInterception(By.xpath(String.format(dropdownOption, coreConfigData.get("Company"))));
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(editClassificationCSMParametersTableRow, coreConfigData.get("Case Nature"))), 5)) {
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(setClassificationCSMParametersTableRow, coreConfigData.get("Case Nature"))), 20), "Set Classification CSM Parameters table row is not visible in CSM after clicking on Company: " + coreConfigData.get("Company") + " in Classification button");
                clickWithoutInterception(By.xpath(String.format(setClassificationCSMParametersTableRow, coreConfigData.get("Case Nature"))));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), setCSMParametersSubmitButton, 20), "Submit button is not visible after clicking on Set Classification CSM Parameters table row in CSM");
                clickWithoutInterception(setCSMParametersSubmitButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(editClassificationCSMParametersTableRow, coreConfigData.get("Case Nature"))), 10), "Classification: " + coreConfigData.get("Case Nature") + " is not set successfully for Company: " + coreConfigData.get("Company") + " in CSM");
            } else {
                log.debug("Classification: " + coreConfigData.get("Case Nature") + " is already configured for Company: " + coreConfigData.get("Company"));
            }
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(listItemTabByLabel, SolQAConstants.Summaries_Questions)), 20), "Summaries and Questions tab is not visible in CSM after clicking on Classification button");
            clickWithoutInterception(By.xpath(String.format(listItemTabByLabel, SolQAConstants.Summaries_Questions)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            setDropdownField(SolQAConstants.Company_Label, coreConfigData.get("Company"));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            setDropdownField(SolQAConstants.CASE_NATURE, coreConfigData.get("Case Nature"));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(caseSummaryValueInTable, coreConfigData.get("Summary"))), 5)) {
                setDropdownField(SolQAConstants.Category, coreConfigData.get("Category"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.TYPE, coreConfigData.get("Type"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.Item, coreConfigData.get("Item"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setTextField(SolQAConstants.SUMMARY_LABEL, coreConfigData.get("Summary"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), summariesAndQuestionsSubmitButton, 20), "Submit button is not visible after entering Classification values in CSM");
                clickWithoutInterception(summariesAndQuestionsSubmitButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(caseSummaryValueInTable, coreConfigData.get("Summary"))), 10), "Classification values for Case Nature: " + coreConfigData.get("Case Nature") + " are not set successfully for Company: " + coreConfigData.get("Company") + " in CSM");
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            } else {
                log.debug("Classification values for Case Nature: BMCOpsCSMOpCat1 are already configured for Company: " + coreConfigData.get("Company"));
            }
        }
        return this;
    }

    public CSMDashboardPage setStandardConfiguration(Map<Object, String> standardConfigData) {
        if (standardConfigData.get("Template Name") != null) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.CASE_TEMPLATE)), 20), "Case Template button is not visible in CSM Control Panel");
            clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.CASE_TEMPLATE)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            setDropdownField(SolQAConstants.Company_Label, standardConfigData.get("Company"));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(caseTemplateTableRow, standardConfigData.get("Template Name"))), 5)) {
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addCaseTemplateButton, 20), "Add Case Template button is not visible after clicking on Case Template button in CSM");
                clickWithoutInterception(addCaseTemplateButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseTemplateTab, 20), "Case Template tab is not visible after clicking on Add Case Template button in CSM");
                setDropdownField(SolQAConstants.CASE_NATURE, standardConfigData.get("Case Nature"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.Category, standardConfigData.get("Category"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.TYPE, standardConfigData.get("Type"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.Item, standardConfigData.get("Item"));
                commonWaits.waitForSometime(commonWaits.moreDelay);
                setDropdownField(SolQAConstants.SUMMARY_LABEL, standardConfigData.get("Summary"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setTextField("Template Name", standardConfigData.get("Template Name"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseTemplateSubmitButton, 20), "Submit button is not visible after entering Case Template values in CSM");
                clickWithoutInterception(caseTemplateSubmitButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), successToastMessage, 5), "Success toast message is not displayed after submitting new case template in CSM");
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseTemplateCloseButton, 20), "Close button is not visible after submitting new case template in CSM");
                clickWithoutInterception(caseTemplateCloseButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(caseTemplateTableRow, standardConfigData.get("Template Name"))), 10), "Case Template: " + standardConfigData.get("Template Name") + " is not set successfully for Company: " + standardConfigData.get("Company") + " in CSM");
            } else {
                log.debug("Case Template: " + standardConfigData.get("Template Name") + " is already configured for Company: " + standardConfigData.get("Company"));
            }
        }
        if (standardConfigData.get("Company Default Values") != null) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Company_Default_Values)), 20), "Company Default Values button is not visible in CSM Control Panel");
            clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Company_Default_Values)));
            commonWaits.waitForSometime(commonWaits.moreDelay);
            setDropdownField(SolQAConstants.Company_Label, standardConfigData.get("Company"));
            commonWaits.waitForSometime(commonWaits.moreDelay);
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(companyDefaultValuesTableRow, standardConfigData.get("Company"))), 5)) {
                setDropdownField(SolQAConstants.First_Assignment_Main_Group, standardConfigData.get("First Assignment Main Group"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.First_Assignment_Sub_Group, standardConfigData.get("First Assignment Sub Group"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.Priority_Matrix_Option, standardConfigData.get("Priority Matrix Option"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.SLA_Tag_Name, standardConfigData.get("SLA Tag Name"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                setDropdownField(SolQAConstants.KPI_Tag_Name, standardConfigData.get("KPI Tag Name"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), companyDefaultValuesSubmitButton, 20), "Submit button is not visible after entering Company Default Values in CSM");
                clickWithoutInterception(companyDefaultValuesSubmitButton);
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),By.xpath(String.format(companyDefaultValuesTableRow, standardConfigData.get("Company"))), 10),"Company Default Values for Company: " + standardConfigData.get("Company") + " are not set successfully in CSM");
            } else {
                log.debug("Company Default Values are already configured for Company: " + standardConfigData.get("Company"));
            }
        }
        return this;
    }

    public CSMDashboardPage setGroupSpecificConfiguration(Map<Object, String> groupSpecificConfigData) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Activities_for_Groups)), 20), "Activities for Groups button is not visible in CSM Control Panel");
        clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Activities_for_Groups)));
        commonWaits.waitForSometime(commonWaits.moreDelay);
        setDropdownField(SolQAConstants.Company_Label, groupSpecificConfigData.get("Company"));
        commonWaits.waitForSometime(commonWaits.moreDelay);
        setDropdownField(SolQAConstants.Parent_Group, groupSpecificConfigData.get("Parent Group"));
        commonWaits.waitForSometime(commonWaits.moreDelay);
        setDropdownField(SolQAConstants.Group_Name, groupSpecificConfigData.get("Group Name"));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), emptyActivitiesGroupTable, 5)) {
            Actions actions = new Actions(getDriver());
            actions.moveToElement(activityNameSelectOption).perform();
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), selectAllLoadedActivitiesCheckbox, 20), "Select All Loaded Activities checkbox is not visible after clicking on Activity Name select option in CSM");
            clickWithoutInterception(selectAllLoadedActivitiesCheckbox);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addActivityButton, 20), "Add Activity button is not visible after clicking on Select All Loaded Activities checkbox in CSM");
            clickWithoutInterception(addActivityButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), successToastMessage, 10)) {
                log.debug("Activities for Group: " + groupSpecificConfigData.get("Group Name") + " are already added in CSM, hence skipping adding activities and closing the Activities for Groups window");
            }
        } else {
            log.debug("Activities for Group: " + groupSpecificConfigData.get("Group Name") + " are already configured in CSM");
        }
        return this;
    }

    public CSMDashboardPage setCustomerDatabaseConfiguration(Map<Object, String> customerDatabaseConfigData) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(buttonContainsText, SolQAConstants.Customer_Profile)), 20), "Customer Profile button is not visible in CSM Control Panel");
        clickWithoutInterception(By.xpath(String.format(buttonContainsText, SolQAConstants.Customer_Profile)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        setDropdownField(SolQAConstants.Company_Label, customerDatabaseConfigData.get("Company"));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(customerProfileTableRow, customerDatabaseConfigData.get("Email"))), 20)) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), newCustomerProfileButton, 20), "New Customer Profile button is not visible after clicking on Customer Profile button in CSM");
            clickWithoutInterception(newCustomerProfileButton);
            setTextField(SolQAConstants.Customer_ID, customerDatabaseConfigData.get("Customer ID"));
            setTextField(SolQAConstants.FirstName, customerDatabaseConfigData.get("First Name"));
            setTextField("Last Name", customerDatabaseConfigData.get("Last Name"));
            setTextField(SolQAConstants.Email, customerDatabaseConfigData.get("Email"));
            setTextField(SolQAConstants.Primary_Contact_No, customerDatabaseConfigData.get("Primary Contact No"));
            setDropdownField(SolQAConstants.Customer_Segment, customerDatabaseConfigData.get("Customer Segment"));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), customerProfileSubmitButton, 20), "Submit button is not visible after entering Customer Profile values in CSM");
            clickWithoutInterception(customerProfileSubmitButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), successToastMessage, 20), "Success toast message is not displayed after submitting new customer profile in CSM");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), customerProfileCloseButton, 20), "Close button is not visible after submitting new customer profile in CSM");
            clickWithoutInterception(customerProfileCloseButton);
        } else {
            log.debug("Customer profile for Customer ID: " + "BMCOps EndUser" + " is already configured in CSM");
        }
        return this;
    }

    public CSMDashboardPage fillCaseData(Map<Object, String> caseData) {
        if (caseData.get("Customer ID") != null) {
            fillCustomerInformationData(caseData.get("Customer ID")).clickOnNextButton();
        }
        if (caseData.get("Case Nature") != null && caseData.get("Template Name") != null) {
            setDropdownField(SolQAConstants.CASE_NATURE, caseData.get("Case Nature"));
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            setDropdownField(SolQAConstants.CASE_TEMPLATE, caseData.get("Template Name"));
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            commonWaits.waitForSometime(10000);
            if (findElement(By.xpath(String.format(getDropdownOptionByText, SolQAConstants.Case_Priority))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).equalsIgnoreCase("Select")) {
                log.debug("Case Priority is not auto-populated after selecting Case Nature and Case Template");
                setDropdownField(SolQAConstants.CASE_TEMPLATE, "None");
                commonWaits.waitForSometime(commonWaits.moreDelay);
                setDropdownField(SolQAConstants.CASE_TEMPLATE, caseData.get("Template Name"));
                commonWaits.waitForSometime(commonWaits.moreDelay);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
            } else {
                log.debug("Case Priority is auto-populated after selecting Case Nature and Case Template");
            }
            commonWaits.waitForSometime(10000);
            Assert.assertFalse(findElement(By.xpath(String.format(getDropdownOptionByText, SolQAConstants.Case_Priority))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).equalsIgnoreCase("Select"), "Assigned Main Group is not selected");
            clickOnNextButton();
        }
        if (caseData.get("Assigned Main Group") != null && caseData.get("Assigned Sub Group") != null) {
            verifyAssignedMainGroupIsSelected().clickOnNextButton();
        }
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseSubmitButton, 20), "Submit button is not visible in Case Creation Console");
        clickWithoutInterception(caseSubmitButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        return this;
    }

    /**
     * Waits for case creation toast and returns only the Case Reference ID (e.g., CS0000000000006).
     */
    public String getCaseReferenceIdFromToast(int timeoutSeconds) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appCSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), caseCreationSuccessToastMessage, timeoutSeconds),"Case creation success toast message is not displayed in CSM");

        String toastText = caseCreationSuccessToastMessage.getText();

        // Fallback if getText() is empty in some UI states
        if (toastText == null || toastText.trim().isEmpty()) {
            toastText = caseCreationSuccessToastMessage.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
        }

        Matcher matcher = CASE_REF_ID_PATTERN.matcher(toastText == null ? "" : toastText);
        Assert.assertTrue(matcher.find(), "Could not find Case Reference ID in toast: " + toastText);

        return matcher.group(); // e.g., CS0000000000006
    }
    
}
