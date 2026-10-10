package pages.ITSM.InnovationSuite;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class KnowledgeManagementPage extends BasePage {

	static SolqaLogger log;
	CommonWaits commonWaits;

	String knowledgeConfiguration ="Knowledge configuration";
	String knowledgeAccessMapping ="Knowledge access mapping";

	//String locators
	String mainURL=  "//*[@rx-id='main-url']//input";
	String frameURL = "//*[@rx-id='frame-url']//input";
	String searchBox ="//input[@placeholder='Search']";
	String comAroundKnowldgeUrl = "//*[contains(@rx-id,'server-url')]//input";
	String comAroundSubscriptionId = "//*[contains(@rx-id,'subscription-id')]//input";
	String comAroundUserName = "//*[contains(@rx-id,'user-name')]//input";
	String comAroundPassword = "//*[contains(@rx-id,'password')]//input";
	String searchConfiguation = "//input[contains(@class,'search__input')]";
	String openSearchResult = "//span[contains(@class,'tree') and contains(.,'%s')]";
	String searchResultExpand = "//*[contains(@class,'a-tree__toggle') and contains(@class,'is-flip')]";
	String warningModalBtn = "//rx-modal//div[contains(., 'Warning')]/following-sibling::div//button[normalize-space()='%s']";    
	String companiesDropDownList= "[class*='dropdown-item']";
	String companiesCheckBox = "//tbody/tr/td//adapt-row-selection-checkbox/div";
	String MappedStatus = "//div[normalize-space()='%s']/ancestor::tr//adapt-badge[normalize-space()='Mapped']";
	String DigitalWorkplaceConfigFields = "//*[normalize-space()='%s']/ancestor::adapt-rx-control-label//following-sibling::div//input";
	private static final String MAIN_URL_XPATH=  "//*[@rx-id='main-url']//input";
	private static final String FRAME_URL_XPATH = "//*[@rx-id='frame-url']//input";
	private static final String COMAROUND_KNOWLEDGE_URL_XPATH = "//*[contains(@rx-id,'server-url')]//input";
	private static final String COMAROUND_SUBSCRIPTION_ID_XPATH = "//*[contains(@rx-id,'subscription-id')]//input";
	private static final String COMAROUND_USER_NAME_XPATH = "//*[contains(@rx-id,'user-name')]//input";
	private static final String COMAROUND_PASSWORD_XPATH = "//*[contains(@rx-id,'password')]//input";
	private static final String SEARCH_CONFIGURATION_XPATH = "//input[contains(@class,'search__input')]";
	private static final String OPEN_SEARCH_RESULT_XPATH = "//span[contains(@class,'tree') and contains(.,'%s')]";
	private static final String SEARCH_RESULT_EXPAND_XPATH = "//*[contains(@class,'a-tree__toggle') and contains(@class,'is-flip')]";
	private static final String WARNING_MODAL_BTN_XPATH = "//rx-modal//div[contains(., 'Warning')]/following-sibling::div//button[normalize-space()='%s']";    
	private static final String COMPANIES_DROPDOWN_LIST_CSS= "[class*='dropdown-item']";
	private static final String COMPANIES_CHECKBOX_XPATH = "//tbody/tr/td//adapt-row-selection-checkbox/div";
	private static final String MAPPED_STATUS_XPATH = "//div[normalize-space()='%s']/ancestor::tr//adapt-badge[normalize-space()='Mapped']";

	//WebElements Locators
	@FindBy(xpath = "//button[contains(. ,'Apply filters')]//span")
	WebElement applyFilters;
	@FindBy(xpath = "//*[contains(@data-testid,'1_node0_toggle')]")
	WebElement searchResultExpandButton;

	@FindBy(xpath = "//*[contains(@rx-id,'save')]")
	WebElement saveButton;
	
	@FindBy(xpath = "//*[contains(@rx-id,'save') and @disabled]")
	WebElement saveButtonDisabled;

	@FindBy(xpath = "//button[contains(@rx-id,'test')]")
	WebElement testButton;

	@FindBy(xpath = "//*[contains(@class,'alert-content')]")
	WebElement testConnectionMsg;

	@FindBy(xpath = "//*[normalize-space()='Select options']/parent::div[@class='checkbox']")
	WebElement selectAll;

	@FindBy(xpath = "//button[contains(. ,'Unmap access')]")
	WebElement unmapAccess;

	@FindBy(xpath = "//*[contains(@class, 'a-tag__close-btn') or contains(@aria-label, 'Remove')]")
	WebElement removeFilterBtn;

	@FindBy(xpath = "//span[contains(. , 'Knowledge management user group')]/ancestor::adapt-rx-select//button")
	WebElement userGroupDropdown;

	@FindBy(xpath = "//adapt-custom-actions//button[contains(. ,'Select all')]")
	WebElement selectAllUserGroupValue;

	@FindBy(xpath = "//adapt-rx-typeahead//input")
	WebElement filterExpressionInput;

	@FindBy(xpath = "//adapt-advanced-filter//button")
	WebElement filterBtn;

	@FindBy(xpath = "//span[contains(@class, 'filter-tags__tag-text')]/ancestor::adapt-tag")
	WebElement gridFilter;

	@FindBy(css = "adapt-rx-select button")
	WebElement companyMappingButton;

	@FindBy(css = ".d-icon-left-cubes_restart")
	WebElement mapAccessButton;
	
	@FindBy(xpath = "//button[contains(.,'Connecting')]")
	WebElement connectingButton;
	
	@FindBy(xpath = "//adapt-highlight[contains(normalize-space(), '(All Portals)')]/ancestor::button//div[contains(@class,'checkbox_')]")
	List<WebElement> allPortalsOptions;
	
	@FindBy(xpath = "//tbody[@class='ui-table-tbody']/tr//span[contains(@class, 'badge-pulse') and contains(. ,'Mapped')]")
	List<WebElement> tableRows;

	public KnowledgeManagementPage() {
		log = new SolqaLogger(KnowledgeManagementPage.class.getName());
		PageFactory.initElements(getDriver(), this);
		commonWaits = new CommonWaits();
	}


	public void navigateToParentConfiguration(String configName) {
		keyClearAndType(SEARCH_CONFIGURATION_XPATH, configName);
		findElement(By.xpath(SEARCH_CONFIGURATION_XPATH));
		commonWaits.sendEnterKey();
		if(!commonWaits.isElementPresent(SEARCH_RESULT_EXPAND_XPATH)) {
			clickWithoutInterception(searchResultExpandButton);
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
	}

	public void navigateToChildConfiguration(String configName) {
		String resultXpath = String.format(OPEN_SEARCH_RESULT_XPATH, configName);
		commonWaits.waitForElementClickable(findElement(By.xpath(resultXpath)));
		clickWithoutInterception(findElement(By.xpath(resultXpath)));
	}

    public void configurationComaroundIntegrations(Map<Object, String> isTestData) {
        navigateToChildConfiguration(knowledgeConfiguration);
        log.info("-------------- Setting up Comaround Integration Configuration values ---------");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        if (Objects.requireNonNull(commonWaits.findElement(By.xpath(MAIN_URL_XPATH)).getAttribute("value")).isEmpty()) {
            keyClearAndType(MAIN_URL_XPATH, isTestData.get("HKMMainURL"));
        }
        if (Objects.requireNonNull(commonWaits.findElement(By.xpath(FRAME_URL_XPATH)).getAttribute("value")).isEmpty()) {
            keyClearAndType(FRAME_URL_XPATH, isTestData.get("HKMFrameURL"));
        }
        if (Objects.requireNonNull(commonWaits.findElement(By.xpath(COMAROUND_KNOWLEDGE_URL_XPATH)).getAttribute("value")).isEmpty()) {
            keyClearAndType(COMAROUND_KNOWLEDGE_URL_XPATH, isTestData.get("EndPoint"));
        }
        if (Objects.requireNonNull(commonWaits.findElement(By.xpath(COMAROUND_SUBSCRIPTION_ID_XPATH)).getAttribute("value")).isEmpty()) {
            keyClearAndType(COMAROUND_SUBSCRIPTION_ID_XPATH, isTestData.get("SubscriptionID"));
        }
        if (Objects.requireNonNull(commonWaits.findElement(By.xpath(COMAROUND_USER_NAME_XPATH)).getAttribute("value")).isEmpty()) {
            keyClearAndType(COMAROUND_USER_NAME_XPATH, isTestData.get("UserName"));
        }
        if (Objects.requireNonNull(commonWaits.findElement(By.xpath(COMAROUND_PASSWORD_XPATH)).getAttribute("value")).isEmpty()) {
            keyClearAndType(COMAROUND_PASSWORD_XPATH, isTestData.get("Password"));
        }
        if (commonWaits.waitForElementVisible(testButton).isEnabled()) {
            clickWithoutInterception(testButton);
            Assert.assertTrue(isHKMTestConnectionSuccessful(), "Test Connection to HKM failed");
            clickWithoutInterception(saveButton);
            commonWaits.waitForElementVisible(saveButtonDisabled);
        }
        log.info("-------------- Configuration of Comaround Integration END ---------");
    }

	public boolean isHKMTestConnectionSuccessful(){
		try{
			commonWaits.waitForElementInvisible(connectingButton);
			commonWaits.waitForElementVisible(testConnectionMsg);
			if(testConnectionMsg.getText().trim().toLowerCase().contains("connection succeeded")) {
				return true;
			}
			else
				return false;
		}catch (Exception e){
			return false;
		}
	}

	public void setAccessMappingComAround(String CompanyName) {
		commonWaits.waitForExecutionComplete();
		navigateToChildConfiguration(knowledgeAccessMapping);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		if(setMappedFilterExpression() != 0){
			log.info("Select All companies and unmap all");
			commonWaits.waitForElementVisible(selectAll);
			commonWaits.waitForElementClickable(selectAll);
			commonWaits.clickByAction(selectAll);
			
			try {
			commonWaits.waitForElementVisible(unmapAccess);
			commonWaits.clickByAction(unmapAccess);
			}
			catch(Exception e) {
				commonWaits.clickByJS(selectAll);
				commonWaits.waitForElementVisible(unmapAccess);
				commonWaits.clickByAction(unmapAccess);
				log.info("Unmap access button is not visible, might be already unmapped");
			}
			String yesXpath = String.format(WARNING_MODAL_BTN_XPATH, "Yes");
			commonWaits.waitForByElementToBeVisible(By.xpath(yesXpath)).click();
			log.info("Remove select filter status=Mapped");
			clickWithoutInterception(removeFilterBtn);
		}
		if (commonWaits.isElementPresent(removeFilterBtn)){
			commonWaits.clickWithoutInterception(removeFilterBtn);
		}

		log.info("Search " +CompanyName+ "as company and click on map access");
		log.info("if not select company checkbox and click on map access and select all user groups");
		selectCompany(CompanyName);
		clickMapAccessButton();
		log.info("Open Knowledge management user group drop down and select all and save mapping");
		clickWithoutInterception(userGroupDropdown);
		for (WebElement checkbox : allPortalsOptions) {
	           if (!checkbox.isSelected()) {
	               checkbox.click();
	           }
	       }
		sendEscKey();
		saveMapping();
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		By mappedStatusBy = By.xpath(String.format(MAPPED_STATUS_XPATH, CompanyName));
		Assert.assertTrue(
				commonWaits.waitForElementVisible_withFluentWait(getDriver(), mappedStatusBy, 30),
				CompanyName + " did not appear as Mapped within timeout after save");
	}


	public int setMappedFilterExpression() {
		log.info("Click on Filter button and set expression Status: Mapped and apply filters");
		commonWaits.clickWithoutInterception(filterBtn);
		commonWaits.clickWithoutInterception(filterExpressionInput);
		filterExpressionInput.sendKeys("Status: Mapped" + Keys.ENTER);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForElementClickable(applyFilters);
		commonWaits.click(applyFilters);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForElementVisible(gridFilter,20);
		return tableRows.size();
	}

	public void selectCompany(String company){
		log.info("Selected company to update access mapping :  " + company);
		clickWithoutInterception(companyMappingButton);
		List<WebElement> options = commonWaits.getElements(By.cssSelector(COMPANIES_DROPDOWN_LIST_CSS));
		options.stream()
				.filter(w -> w.getText().equals(company))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Option '" + company + "' is not present"))
				.click();
	}

	public void clickMapAccessButton(){
		log.info("click map access for user group :  ");
		List<WebElement> checkboxes = commonWaits.getElements(By.xpath(COMPANIES_CHECKBOX_XPATH));
		if (checkboxes == null || checkboxes.isEmpty()) {
			Assert.fail("Company checkbox not found for mapping");
			return;
		}
		WebElement firstCheckbox = checkboxes.get(0);
		commonWaits.clickWithoutInterception(firstCheckbox);
		if(getMapAccessDisabledValue())
		{
			clickWithoutInterception(mapAccessButton);
		}
	}

	public boolean getMapAccessDisabledValue() {
		return mapAccessButton.getAttribute("disabled") == null;
	}

	public void saveMapping(){
		if(getMapAccessDisabledValue()) {
			clickWithoutInterception(saveButton);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
		}
	}

	public KnowledgeManagementPage configurationDWPIntegrations(Map<Object, String> isTestData) {
		navigateToChildConfiguration(isTestData.get("DigitalWorkplace"));
		log.info("-------------- Setting up Digital Workplace Integration Configuration values ---------");
		WebElement DWPURLInput= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DigitalWorkplaceConfigFields, isTestData.get("DigitalWorkplaceURL"))));
		keyClearAndType(DWPURLInput, configProp.get("DWP_URL"));
		WebElement DWPUsernameInput= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DigitalWorkplaceConfigFields, isTestData.get("DigitalWorkplaceUsername"))));
		keyClearAndType(DWPUsernameInput, configProp.get("adminUserName"));
		WebElement DWPPasswordInput= commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DigitalWorkplaceConfigFields, isTestData.get("DigitalWorkplacePassword"))));
		keyClearAndType(DWPPasswordInput, configProp.get("adminPassword"));
		clickWithoutInterception(saveButton);
		commonWaits.waitForElementVisible(saveButtonDisabled);
		log.info("-------------- Configuration of Digital Workplace Integration END ---------");
		return this;
	}

}

