package pages.ITSM.SmartIT;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CreateWorkorderPage extends SmartITCommonOperations {
	
	SolqaLogger log = new SolqaLogger(CreateWorkorderPage.class.getName());
	CommonWaits commonWaits;
    String lastCreatedCustomerFullName = "";
	String IFRAME_CREATE_WORKORDER = "//iframe[@id='pwa-frame']";
	String XPATH_BY_ID = "//*[@id='%s']";
	String XPATH_DROPDOWN_BTN_BY_ID = "//*[@id='%s']/div/button";
	String XPATH_DROPDOWN_VALUE_BTN_CONTAINS_TEXT = "//*[contains(normalize-space(.),'%s')]//parent::adapt-highlight//ancestor::button";
	String XPATH_TYPEAHEAD_ITEM = "//*[contains(@id,'%s') and contains(@class,'%s') and contains(.,'%s')]";
	String XPATH_CONTAINS_TEXT = "//*[contains(text(),'%s')]";
	String XPATH_FIELD_INPUT_BY_LABEL = "(//*[self::label or self::span][normalize-space(.)='%s']/following::*[self::input or self::textarea][1])";
	String Fields = "//label[contains(normalize-space(),'%s')]/ancestor::adapt-rx-textfield//input";
    String ARID_OPERATIONAL_CATEGORY = "304420021";
	String ARID_PRODUCT_CATEGORY = "304420031";
	String TYPEAHEAD_ITEM_CLASS = "dropdown-item";
    String ID_WORKORDER_DESCRIPTION = "1000000151";
	String ARID_CUSTOMER = "301395300";
	String ARID_CUSTOMER_ACTION = "1000000048"; // "+" / customer action button
	String ARID_SUMMARY = "1000000000";
	String ARID_PRIORITY = "1000000164";
	String ARID_STATUS = "7";
	String ARID_SAVE_WORKORDER = "304440891";
	String ARID_ASSIGN_TO_ME = "304421551";

	String XPATH_CREATE_NEW_CUSTOMER_CTA =
			"//button[.//span[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create') " +
			"and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'new')] " +
			"or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create new')]" +
			"|//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create new')]";
	String XPATH_SAVE_IN_DIALOG = "//button[normalize-space()='Save' or .//span[normalize-space()='Save'] or @ux-id='save-btn' or contains(@ux-id,'save')]";
	String XPATH_CLOSE_DIALOG = "//button[@class='close' or @aria-label='Close' or contains(@aria-label,'Close') or @ux-id='close-btn']";
	String LABEL_FIRST_NAME = "First Name";
	String LABEL_LAST_NAME = "Last Name";
	String LABEL_COMPANY = "Company";
	String LABEL_ORGANIZATION = "Organization";
	String LABEL_DEPARTMENT = "Department";
	String LABEL_SITE = "Site";
    String TD_FIRST_NAME = "FirstName";
	String TD_LAST_NAME = "LastName";
	String TD_COMPANY = "Company";
	String TD_ORGANIZATION = "Organization";
	String TD_DEPARTMENT = "Department";
	String TD_SITE = "Site";
    int PAGE_LOAD_TIMEOUT_SECONDS = 30;
	int PAGE_LOAD_POLL_SECONDS = 3;
    public String IFrameCreateWorkorder= "//iframe[@id='pwa-frame']";
    public String ElementXpathByID= "//*[@id='%s']";
    @FindBy(xpath = "//adapt-docked-panel[contains(.,'Create customer')]//*[@id='304419831']")
    public WebElement saveCustomerButton;
    @FindBy(xpath = "//button[@title='Create new customer']")
    public WebElement createNewCustomerButton;

	public CreateWorkorderPage() {
		PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
	}

    public void FillWorkOrderTemplate(Map<Object, String> smartITTestData) {
        log.info("Filling Workorder Details");
        switchtoDefaultFrame();
        switchToFrame(By.xpath(IFrameCreateWorkorder));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ElementXpathByID, "ar301395300")), 20),
                "Customer field is not visible in Create Workorder page, cannot proceed with filling the form");

        if (smartITTestData.get("Company") != null) {
            log.info("Entering Company");
            setCompany(String.valueOf(smartITTestData.get("Company")));
        }

        if (smartITTestData.get("Customer Name") != null) {
            log.info("Entering Customer Name");
            setTypeaheadField("Customer", smartITTestData.get("Customer Name"));
        }

        if (smartITTestData.get("Workorder Title") != null) {
            log.info("Entering Summary");
            setSummary(smartITTestData.get("Workorder Title"));
        }

        if (smartITTestData.get("WorkOrder Description") != null) {
            log.info("Entering WorkOrder Description");
            setDescription(smartITTestData.get("WorkOrder Description"));
        }

        if (smartITTestData.get("Priority") != null) {
            log.info("Entering Priority");
            setDropdownField("Priority", smartITTestData.get("Priority"));
        }

        if (smartITTestData.get("Status") != null) {
            log.info("Entering Status");
            setDropdownField("Status", smartITTestData.get("Status"));
        }

        if (smartITTestData.get("OperationalCategory") != null) {
            log.info("Entering Operational Category");
            setTypeaheadField("Operational category", smartITTestData.get("OperationalCategory"));
        }

        if (smartITTestData.get("ProductCategory") != null) {
            setTypeaheadField("Product category", smartITTestData.get("ProductCategory"));
        }

        if (smartITTestData.get("Manager support group") != null) {
            setTypeaheadField("Manager support group", smartITTestData.get("Manager support group"));
        }

        if (smartITTestData.get("Assignee support group") != null) {
            setTypeaheadField("Assignee support group", smartITTestData.get("Assignee support group"));
        }

        if (smartITTestData.get("Request assignee") != null) {
            setTypeaheadField("Request assignee", smartITTestData.get("Request assignee"));
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }
	
	public CreateWorkorderPage setCompany(String companyName) {
		if (companyName == null || companyName.trim().isEmpty()) return this;
		try {
			By inputBy = By.xpath(String.format(XPATH_FIELD_INPUT_BY_LABEL, TD_COMPANY));
			if (!findElements(inputBy).isEmpty()) {
				WebElement input = findElement(inputBy);
				clickByAction(input);
				try { input.clear(); } catch (Exception ignored) {}
				input.sendKeys(companyName);
				By optionBy = By.xpath(String.format(XPATH_CONTAINS_TEXT, companyName));
				if (!findElements(optionBy).isEmpty()) {
					commonWaits.clickWithoutInterception(optionBy);
				}
			}
		} catch (Exception e) {
			log.warn("Unable to set Company on workorder create page (continuing). Error: " + e.getMessage());
		}
		return this;
	}

    public CreateWorkorderPage setCustomer(String personName) {
        keyClearAndType((String.format(ElementXpathByID, "ar301395300")), personName);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ElementXpathByID, "ar1000000048")), 20);
        commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID, "ar1000000048")));
        return this;
    }

	public CreateWorkorderPage setSummary(String summary) {
		final String summaryXpath = String.format(XPATH_BY_ID, ARID_SUMMARY);
		keyClearAndType(summaryXpath, summary);
		return this;
	}

	public CreateWorkorderPage setDescription(String summary) {
		final String descriptionXpath = String.format(XPATH_BY_ID, ID_WORKORDER_DESCRIPTION);
		keyClearAndType(descriptionXpath, summary);
		return this;
	}

	public CreateWorkorderPage setPriority(String value) {
		final String dropdownBtnXpath = String.format(XPATH_DROPDOWN_BTN_BY_ID, ARID_PRIORITY);
		final String dropdownValueXpath = String.format(XPATH_DROPDOWN_VALUE_BTN_CONTAINS_TEXT, value);

		commonWaits.clickWithoutInterception(By.xpath(dropdownBtnXpath));
		commonWaits.waitForByElementToBeVisible(By.xpath(dropdownValueXpath));
		commonWaits.clickWithoutInterception(By.xpath(dropdownValueXpath));
		return this;
	}
	
	public CreateWorkorderPage setStatus(String status) {
		final String dropdownBtnXpath = String.format(XPATH_DROPDOWN_BTN_BY_ID, ARID_STATUS);
		final String dropdownValueXpath = String.format(XPATH_DROPDOWN_VALUE_BTN_CONTAINS_TEXT, status);

		commonWaits.clickWithoutInterception(By.xpath(dropdownBtnXpath));
		commonWaits.waitForByElementToBeVisible(By.xpath(dropdownValueXpath));
		commonWaits.clickWithoutInterception(By.xpath(dropdownValueXpath));
		return this;
	}
	
	public CreateWorkorderPage clickAssigntoMe() {
		log.info("Clicking on 'Assign To Me' link");
		final String assignToMeXpath = String.format(XPATH_BY_ID, ARID_ASSIGN_TO_ME);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(assignToMeXpath), 20),
                "'Assign To Me' link is not visible in Create Workorder page, cannot click on it");
		commonWaits.clickWithoutInterception(By.xpath(assignToMeXpath));
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	public CreateWorkorderPage saveWorkorder() { 
		log.info("Saving Workorder");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();
		log.info("Clicking on Save Button on Workorder");
		String saveXpath = String.format(XPATH_BY_ID, ARID_SAVE_WORKORDER);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(saveXpath), 20),
                "Save button is not visible in Create Workorder page, cannot proceed with saving the workorder");
		commonWaits.waitForByElementToBeVisible(By.xpath(saveXpath));
		commonWaits.clickWithoutInterception(By.xpath(saveXpath));
		commonWaits.waitForPageFullyLoaded(20, 2);
        commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForSometime(commonWaits.moreDelay);
		return this;
	}
	
	public CreateWorkorderPage setOperationalCategory(String operationalCategory) {
		final String operationalCategoryArId = "ar" + ARID_OPERATIONAL_CATEGORY;
		final String operationalCategoryXpath = String.format(XPATH_BY_ID, operationalCategoryArId);
		keyClearAndType(operationalCategoryXpath, operationalCategory);

		final String optionXpath = String.format(XPATH_TYPEAHEAD_ITEM, operationalCategoryArId, TYPEAHEAD_ITEM_CLASS, operationalCategory);
		commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		commonWaits.clickWithoutInterception(By.xpath(optionXpath));
		commonWaits.waitForPageFullyLoaded(PAGE_LOAD_TIMEOUT_SECONDS, PAGE_LOAD_POLL_SECONDS);
		return this;
	}

	public CreateWorkorderPage setProductCategory(String productCategory) {
		final String productCategoryArId = "ar" + ARID_PRODUCT_CATEGORY;
		final String productCategoryXpath = String.format(XPATH_BY_ID, productCategoryArId);
		keyClearAndType(productCategoryXpath, productCategory);

		final String optionXpath = String.format(XPATH_TYPEAHEAD_ITEM, productCategoryArId, TYPEAHEAD_ITEM_CLASS, productCategory);
		commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
		commonWaits.clickWithoutInterception(By.xpath(optionXpath));
		commonWaits.waitForPageFullyLoaded(PAGE_LOAD_TIMEOUT_SECONDS, PAGE_LOAD_POLL_SECONDS);
		return this;
	}

	public CreateWorkorderPage createNewCustomerWithoutLogin(Map<Object, String> customerData) {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_CREATE_WORKORDER));

		final String customerArId = "ar" + ARID_CUSTOMER;
		final String customerXpath = String.format(XPATH_BY_ID, customerArId);
		commonWaits.waitForByElementToBeVisible(By.xpath(customerXpath));

		// Step 2: click "+" next to customer
		final String customerActionArId = "ar" + ARID_CUSTOMER_ACTION;
		final String customerActionXpath = String.format(XPATH_BY_ID, customerActionArId);
		commonWaits.clickWithoutInterception(By.xpath(customerActionXpath));
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);

		// Step 3: best-effort open "Create New" customer dialog/page if present
		try {
			By createNewBy = By.xpath(XPATH_CREATE_NEW_CUSTOMER_CTA);
			if (!findElements(createNewBy).isEmpty()) {
				commonWaits.clickWithoutInterception(createNewBy);
			}
		} catch (Exception ignored) {}

		setCustomerDialogField(LABEL_FIRST_NAME, customerData.get(TD_FIRST_NAME));
		setCustomerDialogField(LABEL_LAST_NAME, customerData.get(TD_LAST_NAME));
		setCustomerDialogField(LABEL_COMPANY, customerData.get(TD_COMPANY));
		setCustomerDialogField(LABEL_ORGANIZATION, customerData.get(TD_ORGANIZATION));
		setCustomerDialogField(LABEL_DEPARTMENT, customerData.get(TD_DEPARTMENT));
		setCustomerDialogField(LABEL_SITE, customerData.get(TD_SITE));

		// Save customer (no login id entered by design)
		By saveBy = By.xpath(XPATH_SAVE_IN_DIALOG);
		if (!findElements(saveBy).isEmpty()) {
			commonWaits.clickWithoutInterception(saveBy);
		} else {
			// fallback close dialog if UI auto-saves
			By closeBy = By.xpath(XPATH_CLOSE_DIALOG);
			if (!findElements(closeBy).isEmpty()) {
				commonWaits.clickWithoutInterception(closeBy);
			}
		}

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		String firstName = customerData.get(TD_FIRST_NAME) == null ? "" : customerData.get(TD_FIRST_NAME).trim();
		String lastName = customerData.get(TD_LAST_NAME) == null ? "" : customerData.get(TD_LAST_NAME).trim();
		lastCreatedCustomerFullName = (firstName + " " + lastName).trim();
		return this;
	}
	
	public String getLastCreatedCustomerFullName() {
		return lastCreatedCustomerFullName == null ? "" : lastCreatedCustomerFullName.trim();
	}

	public void setCustomerDialogField(String label, Object valueObj) {
		WebElement field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(Fields, label)));
		clickByAction(field);
		field.clear();
		field.sendKeys(String.valueOf(valueObj));
		commonWaits.waitForExecutionComplete();
	}
	
	public CreateWorkorderPage clickOnCreateNewCustomerButton() {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_CREATE_WORKORDER));
		log.info("Clicking on 'Create New Customer' button");
		try {
			commonWaits.clickWithoutInterception(createNewCustomerButton);
		} finally {
			switchtoDefaultFrame();
		}
		return this;
	}
	
	public CreateWorkorderPage fillNewCustomerDetailsAndSave(Map<Object, String> smartITCustomerData) {
		// This method expects the create-customer dialog to be open already
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_CREATE_WORKORDER));
		if (smartITCustomerData.get("First name") != null){
			setCustomerDialogField("First name", smartITCustomerData.get("First name"));
		}
		if (smartITCustomerData.get("Last name") != null){
			setCustomerDialogField("Last name", smartITCustomerData.get("Last name"));
		}
		if (smartITCustomerData.get("Company") != null){
			setCustomerDialogField("Company", smartITCustomerData.get("Company"));
		}
		if (smartITCustomerData.get("Organization") != null){
			setCustomerDialogField("Organization", smartITCustomerData.get("Organization"));
		}
		if (smartITCustomerData.get("Department") != null){
			setCustomerDialogField("Department", smartITCustomerData.get("Department"));
		}
		if (smartITCustomerData.get("Site") != null){
			setCustomerDialogField("Site", smartITCustomerData.get("Site"));
		}
		clickOnSaveCustomerButton();
		return this;
	}
	
	public CreateWorkorderPage setTextInput(String label, String elementID, String value) {
		log.info("Setting value " + value + " in text input " + label);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_CREATE_WORKORDER));
		final String inputXpath = String.format(XPATH_BY_ID, elementID);
		keyClearAndType(inputXpath, value);
		switchtoDefaultFrame();
		return this;
	}
	
	public CreateWorkorderPage setTypeheadInput(String label, String locatorID, String value) {
		log.info("Setting value " + value + " in typehead input " + label);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_CREATE_WORKORDER));
		final String inputXpath = String.format(XPATH_BY_ID, locatorID);
		keyClearAndType(inputXpath, value);

		// Prefer simple contains-text option click
		By optionBy = By.xpath(String.format(XPATH_CONTAINS_TEXT, value));
		if (!findElements(optionBy).isEmpty()) {
			commonWaits.clickWithoutInterception(optionBy);
		} else {
			// Typeahead fallback: match by id/class/text
			final String optionXpath = String.format(XPATH_TYPEAHEAD_ITEM, locatorID, TYPEAHEAD_ITEM_CLASS, value);
			if (!findElements(By.xpath(optionXpath)).isEmpty()) {
				commonWaits.clickWithoutInterception(By.xpath(optionXpath));
			}
		}
		commonWaits.waitForPageFullyLoaded(30, 3);
		switchtoDefaultFrame();
		return this;
	}
	
	public CreateWorkorderPage clickOnSaveCustomerButton() {
		commonWaits.clickWithoutInterception(saveCustomerButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}

}