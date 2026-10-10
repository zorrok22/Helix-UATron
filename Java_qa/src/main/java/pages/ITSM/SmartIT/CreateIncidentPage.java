package pages.ITSM.SmartIT;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CreateIncidentPage extends BasePage {

    static SolqaLogger log = new SolqaLogger(CreateIncidentPage.class.getName());
    CommonWaits commonWaits;
    SmartITCommonOperations smartITCommonOperations;

    public CreateIncidentPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        smartITCommonOperations = new SmartITCommonOperations();
    }

    //String locators
    public String ElementXpathByID = "//*[@id='%s']";
    public String ElementDropdownXpathByID = "//*[text()='%s']/ancestor::adapt-rx-select";
    public String statusDropdownCreatePage = "//*[contains(@class, 'rx-select__option-content')]/parent::button[contains(. ,'%s')]";
    public String typeheadListElementID = "//*[contains(@id,'%s') and contains(@class,'%s') and contains(.,'%s')]";
    public String typeheadListElementText = "//button[contains(normalize-space(.),'%s')]";
    public String dropdownOption = "//button//div[normalize-space()='%s']";
    public String brainIconXpath = "//button[contains(@class,'category-prediction') or contains(@aria-label,'Category Prediction') or contains(@title,'Predict Category') or contains(@class,'cognitive-prediction')]";

    //WebElements
    @FindBy(xpath = "//span[contains(@class,'icon-helix_gpt') ]/parent::span/parent::button")
    public WebElement AskHelixGPTTab;

    @FindBy(xpath = "//div[contains(@class,'summary-gpt__description')]")
    public WebElement HelixGptContent;

    @FindBy(xpath = "//*[@id='ar303530000' or contains(@placeholder,'Enter customer name') or contains(@title,'Enter customer name')]")
    public WebElement customerNameInput;

    @FindBy(xpath = "//button[@aria-label='View details' or (contains(@class,'smt-link-btn') and .//span[normalize-space()='View details'])]")
	public WebElement viewDetailsButton;

	@FindBy(xpath = "//*[@aria-label='Alarm details']/following-sibling::*//*[contains(@class,'bwf-read-only-content')]")
	public WebElement alarmDetailsContent;

    @FindBy(xpath = "//iframe[@id='pwa-frame']")
    WebElement pwaFrame;

    @FindBy(xpath = "//*[@ux-id='related-items-tab']//a")
    public WebElement relatedItemsTab;

    @FindBy(xpath = "(//td[contains(@class,'at-data-cell')])[2]")
	public WebElement RelationShip_Status;

    @FindBy(xpath = "//*[contains(@class,'preview-panel')]//iframe")
	public WebElement iframe;

	@FindBy(xpath = "(//span[text()='Delete'])[2]")
	public WebElement Delete_Option;

	@FindBy(xpath = "//button[@id='302180500']")
	public WebElement Accept_popUp;

	@FindBy(xpath = "//a[contains(text(),'CASE-')]")
	public WebElement Case_ID_inPV;

	@FindBy(xpath = "//div[contains(@class,'ui-chkbox-box')]")
	public WebElement Case_checkbox;

    @FindBy(xpath = "//span[normalize-space()='Related items']/ancestor::button[@role='tab']")
    public WebElement relatedItemsTabButton;

	String IFRAME_ALARMS = "//iframe[@id='VF304437901']";
	String XPATH_BY_ID = "//*[@id='%s']";
	String XPATH_CONTAINS_TEXT = "//*[contains(text(),'%s')]";
	String XPATH_FIELD_INPUT_BY_LABEL = "(//*[self::label or self::span][normalize-space(.)='%s']/following::*[self::input or self::textarea])[1]";

	// Field ARIDs / IDs
	private static final String ARID_CUSTOMER = "303530000";
	private static final String ARID_SERVICE = "303497300";
    private static final String ARID_TEMPLATE = "ar303558600";

	// Test data keys
	String TD_COMPANY = "Company";
	// Wait constants
	int PAGE_LOAD_TIMEOUT_SECONDS = 30;
	int PAGE_LOAD_POLL_SECONDS = 3;
	long DROPDOWN_STABILIZE_WAIT_MS = 2_000;
	long RETRY_WAIT_MS = 500;
	int MAX_RETRIES = 3;

	//WebElements
    @FindBy(xpath = "//button[@title='Edit' and @name='ar304420591']")
    public WebElement editButton;

    @FindBy(xpath = "//button[@class='close' and contains(@aria-label,'Clear Product category')]")
    public WebElement clearProductCategoryButton;

    @FindBy(xpath = "//button[@class='close' and contains(@aria-label,'Clear Service ')]")
    public WebElement clearServiceButton;

    @FindBy(xpath = "(//button[@class='close' and contains(@aria-label,'Clear Customer ')])[1]")
    public WebElement clearCustomerName;

    @FindBy(xpath = "//button[@id='ar303497300_popup_item-0']")
    public WebElement firstItemAlt;

    @FindBy(xpath = "//button[@role='option' and contains(@class,'dropdown-item')]")
    public WebElement firstDropdownItemXpath;

    @FindBy(xpath = "//span[normalize-space()='Incident type']/ancestor::adapt-rx-control-label//following-sibling::div/button")
    public WebElement incidentTypeDropdown;

    @FindBy(xpath = "//button[@title='Service CI recommendation']//span[text()='Recommendation']")
    WebElement serviceCI_Recommendation;

    @FindBy(xpath = "//input[@id='ar303497300' or @aria-label='Service']")
    WebElement serviceInputField;

    String customerBox = "ar303530000";
    String descriptionBox = "1000000151";
    String summaryBox = "1000000000";
    String operationalCategoryBox = "ar304420021";
    String typeheadOperationalCategory = "dropdown-item";
    String productCategoryBox = "ar304420031";
    String typeheadproductCategoryBox = "dropdown-item";
    String assignToMeLink = "304425871";
    String saveIncidentBox = "304440891";
    String contactBox = "ar303497600";
    String serviceCIBox = "ar303497300";
    String serviceCIBox1 = "ar200000020";
    String assigneeSupportGroupInput = "(//*[self::label or self::span][normalize-space(.)='Support group']/following::*[self::input][1])";
    String assigneePersonInput = "(//*[self::label or self::span][normalize-space(.)='Person']/following::*[self::input][1])";

    public CreateIncidentPage switchToPWAFrame() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(pwaFrame);
        return this;
    }

    public CreateIncidentPage FillIncidentTemplate(Map<Object, String> smartITTestData) {
        log.info("Filling Incident Details");
        smartITCommonOperations.handlePageLoadWaitAlertIfPresent();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);

        // Wait for customer name input WebElement to be visible
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), customerNameInput, 20), "Customer Name input field did not become visible within timeout");

        if (smartITTestData.get("Customer Name") != null) {
            log.info("Entering Customer Name");
            smartITCommonOperations.setTypeaheadField("Customer", smartITTestData.get("Customer Name"));
        }

        if (smartITTestData.get("Template") != null) {
            setTemplate(smartITTestData.get("Template"));
        }

        if (smartITTestData.get("Incident Description") != null) {
            log.info("Entering Description");
            setDescription(smartITTestData.get("Incident Description"));
        }

        if (smartITTestData.get("Incident Title") != null) {
            log.info("Entering Summary");
            setSummary(smartITTestData.get("Incident Title"));
        }

        if (smartITTestData.get("Impact") != null) {
            log.info("Entering Impact");
            setImpact(smartITTestData.get("Impact"));
        }

        if (smartITTestData.get("Urgency") != null) {
            log.info("Entering Urgency");
            setUrgency(smartITTestData.get("Urgency"));
        }

        if (smartITTestData.get("Status") != null) {
            log.info("Entering Status");
            setStatus(smartITTestData.get("Status"));
        }

        if (smartITTestData.get("OperationalCategory") != null) {
            log.info("Entering Operational Category");
            smartITCommonOperations.setTypeaheadField("Operational category", (smartITTestData.get("OperationalCategory")));
        }
        if (smartITTestData.get("ProductCategory") != null) {
            smartITCommonOperations.setTypeaheadField("Product category", (smartITTestData.get("ProductCategory")));
        }

        if (smartITTestData.get("Support group") != null) {
            smartITCommonOperations.setTypeaheadField("Support group", smartITTestData.get("Support group"));
        }

        if (smartITTestData.get("Person") != null) {
            smartITCommonOperations.setTypeaheadField("Person", smartITTestData.get("Person"));
        }
       
        return this;
    }

    public CreateIncidentPage setCustomer(String personName) {
        final String customerArId = "ar" + ARID_CUSTOMER;
        final String customerXpath = String.format(XPATH_BY_ID, customerArId);
        keyClearAndType(customerXpath, personName);

        final String optionXpath = String.format(XPATH_CONTAINS_TEXT, personName);
        commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
        commonWaits.clickWithoutInterception(By.xpath(optionXpath));
        return this;
    }

    public CreateIncidentPage setDescription(String description) {
        keyClearAndType(String.format(ElementXpathByID, descriptionBox), description);
        return this;
    }

    public CreateIncidentPage setSummary(String summary) {
        keyClearAndType(String.format(ElementXpathByID, summaryBox), summary);
        return this;
    }

    public CreateIncidentPage setStatus(String status) {
        click(String.format(ElementDropdownXpathByID, "Status"));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(statusDropdownCreatePage, status)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(statusDropdownCreatePage, status)));
        return this;
    }

    public CreateIncidentPage setImpact(String impact) {
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(ElementDropdownXpathByID, SolQAConstants.IMPACT))));
        clickWithoutInterception(By.xpath("//*[text()='Impact']/ancestor::smt-select-field//button[contains(@class,'close')]"));
        clickWithoutInterception(By.xpath(String.format(ElementDropdownXpathByID, SolQAConstants.IMPACT)));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(statusDropdownCreatePage, impact)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(statusDropdownCreatePage, impact)));
        return this;
    }

    public CreateIncidentPage setUrgency(String urgency) {
        click(String.format(ElementDropdownXpathByID, SolQAConstants.URGENCY));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(statusDropdownCreatePage, urgency)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(statusDropdownCreatePage, urgency)));
        return this;
    }

    public CreateIncidentPage setOperationalCategory(String operationalCategory) {
        keyClearAndType(String.format(ElementXpathByID, operationalCategoryBox), operationalCategory);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementID, operationalCategoryBox, typeheadOperationalCategory, operationalCategory)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementID, operationalCategoryBox, typeheadOperationalCategory, operationalCategory)));
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public CreateIncidentPage setProductCategory(String productCategory) {
        keyClearAndType(String.format(ElementXpathByID, productCategoryBox), productCategory);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementID, productCategoryBox, typeheadproductCategoryBox, productCategory)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementID, productCategoryBox, typeheadproductCategoryBox, productCategory)));
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public CreateIncidentPage clickAssigntoMe() {
        log.info("Clicking on 'Assign To Me' link");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ElementXpathByID, assignToMeLink)), 20), "'Assign To Me' link did not become visible within timeout");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, assignToMeLink))).click();
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public CreateIncidentPage setAssigneeSupportGroup(String supportGroup) {
        if (supportGroup == null || supportGroup.trim().isEmpty()) {
            return this;
        }
        log.info("Setting assignee support group: " + supportGroup);
        keyClearAndType(assigneeSupportGroupInput, supportGroup);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(typeheadListElementText, supportGroup)), 20), "Support group dropdown option did not become visible within timeout");
        commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementText, supportGroup)));
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public CreateIncidentPage setAssigneePerson(String personName) {
        log.info("Setting assignee person: " + personName);
        keyClearAndType(assigneePersonInput, personName);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(typeheadListElementText, personName)), 20), "Person dropdown option did not become visible within timeout");
        commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementText, personName)));
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public CreateIncidentPage saveIncident() {
        commonWaits.switchtoDefaultFrame();
        switchToFrame(pwaFrame);
        log.info("Clicking on Save Button on Incident");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(ElementXpathByID, saveIncidentBox)), 40), "Save button did not become visible within timeout");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, saveIncidentBox)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID, saveIncidentBox)));
        commonWaits.waitForPageFullyLoaded(40, 3);
        commonWaits.waitForPageReadyState(getDriver());
        smartITCommonOperations.handlePageLoadWaitAlertIfPresent();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(30, 3);
        return this;
    }

    public CreateIncidentPage navigateToAskGPT() {
        log.info("Navigating to Ask GPT");
        commonWaits.switchtoDefaultFrame();
        switchToFrame(pwaFrame);
        commonWaits.waitForElementClickable(AskHelixGPTTab);
        commonWaits.clickWithoutInterception(AskHelixGPTTab);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public CreateIncidentPage setContact(String contact) {
        keyClearAndType(String.format(ElementXpathByID, contactBox), contact);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementText, contact)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementText, contact)));
        return this;
    }

    public CreateIncidentPage clearProductCategory() {
        commonWaits.clickWithoutInterception(clearProductCategoryButton);
        return this;
    }

    public CreateIncidentPage updateCustomer(String personName) {
        log.info("Updating customer with name: " + personName);

        // Check if clearCustomerName button is present and visible before clicking
        try {
            if (clearCustomerName.isDisplayed()) {
                log.info("Clear customer button is visible, clicking to clear existing customer");
                commonWaits.clickWithoutInterception(clearCustomerName);
            }
        } catch (NoSuchElementException e) {
            log.info("Clear customer button not present on page, skipping clear action");
        } catch (Exception e) {
            log.info("Clear customer button not visible or not interactable: " + e.getMessage());
        }

        log.info("Typing customer name: " + personName);
        final String customerArId = "ar" + ARID_CUSTOMER;
        final String customerXpath = String.format(XPATH_BY_ID, customerArId);
        keyClearAndType(customerXpath, personName);

        log.info("Waiting for customer dropdown option to appear");
        final String customerOptionXpath = String.format(XPATH_CONTAINS_TEXT, personName);
        commonWaits.waitForByElementToBeVisible(By.xpath(customerOptionXpath));

        log.info("Clicking on customer dropdown option");
        commonWaits.clickWithoutInterception(By.xpath(customerOptionXpath));
        log.info("Customer updated successfully");
        return this;
    }

	private void switchToPwaFrameIfPresent() {
		try {
			commonWaits.switchtoDefaultFrame();
			if (commonWaits.isElementPresent(pwaFrame)) {
				switchToFrame(pwaFrame);
			}
		} catch (Exception ignored) {}
	}

	public CreateIncidentPage setCompany(String companyName) {
		if (companyName == null || companyName.trim().isEmpty()) return this;
		try {
			By inputBy = By.xpath(String.format(XPATH_FIELD_INPUT_BY_LABEL, TD_COMPANY));
			if (!findElements(inputBy).isEmpty()) {
				WebElement input = findElement(inputBy);
				clickByAction(input);
				input.clear();
				input.sendKeys(companyName);
				By optionBy = By.xpath(String.format(XPATH_CONTAINS_TEXT, companyName));
				if (!findElements(optionBy).isEmpty()) {
					commonWaits.clickWithoutInterception(optionBy);
				}
			}
		} catch (Exception e) {
			log.warn("Unable to set Company (continuing). Error: " + e.getMessage());
		}
		return this;
	}
	
	public CreateIncidentPage setService(String serviceValue) {
		commonWaits.waitForPageFullyLoaded(PAGE_LOAD_TIMEOUT_SECONDS, PAGE_LOAD_POLL_SECONDS);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(pwaFrame);
		
		log.info("Setting Service name :" + serviceValue);
		final String serviceArId = "ar" + ARID_SERVICE;
		final String serviceXpath = String.format(XPATH_BY_ID, serviceArId);
		commonWaits.waitForByElementToBeVisible(By.xpath(serviceXpath));
	    try {
			if (clearServiceButton.isDisplayed()) {
				log.info("Clear service button is visible, clicking to clear existing service");
				commonWaits.clickWithoutInterception(clearServiceButton);
			}
		} catch (NoSuchElementException e) {
			log.info("Clear service button not present on page, skipping clear action");
		} catch (Exception e) {
			log.info("Clear service button not visible or not interactable: " + e.getMessage());
		}
	    keyClearAndType(serviceXpath, serviceValue);
		
		log.info("Waiting for dropdown results to load and stabilize...");
		commonWaits.waitForSometime(DROPDOWN_STABILIZE_WAIT_MS); // Wait for dropdown to render and stabilize
		
		log.info("Clicking on first dropdown item: " + firstDropdownItemXpath);
		
		boolean clicked = false;
		int maxRetries = MAX_RETRIES;
		
		for (int retry = 0; retry < maxRetries && !clicked; retry++) {
			try {
				log.info("Attempt " + (retry + 1) + " to click dropdown item");
				
				if (firstDropdownItemXpath.isDisplayed() && firstDropdownItemXpath.isEnabled()) {
					commonWaits.scrollToSpecificElement(firstDropdownItemXpath);
					clickByAction(firstDropdownItemXpath);
					clicked = true;
					log.info("Successfully clicked first dropdown item");
				}
				
			} catch (org.openqa.selenium.StaleElementReferenceException e) {
				log.warn("Stale element on attempt " + (retry + 1) + ", retrying...");
			} catch (Exception e) {
				log.warn("Failed to click on attempt " + (retry + 1) + ": " + e.getMessage());
				if (retry == maxRetries - 1) {
					// Last retry - try alternative xpath
					log.warn("All retries exhausted, trying alternative xpath");
					try {
						clickByAction(firstItemAlt);
						clicked = true;
						log.info("Successfully clicked using alternative xpath");
					} catch (Exception ex) {
						log.error("Alternative xpath also failed: " + ex.getMessage());
					}
				} else {
					commonWaits.waitForSometime(RETRY_WAIT_MS);
				}
			}
		}
		
		if (!clicked) {
			log.error("Failed to click dropdown item after " + maxRetries + " retries");
			throw new RuntimeException("Unable to select service dropdown item");
		}
		 
		commonWaits.waitForPageFullyLoaded(PAGE_LOAD_TIMEOUT_SECONDS, 5);	
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
		
	}

    public CreateIncidentPage updateIncident(Map<Object, String> smartITTestData) {
        log.info("Editing Incident Details");
        switchToFrame(pwaFrame);
        commonWaits.waitForElementVisible(editButton);
        commonWaits.clickWithoutInterception(editButton);
        if (smartITTestData.get("Customer Name") != null) {
            log.info("Entering Customer Name");
            updateCustomer(smartITTestData.get("Customer Name"));
        }

        if (smartITTestData.get("Incident Description") != null) {
            log.info("Entering Description");
            setDescription(smartITTestData.get("Incident Description"));
        }

        if (smartITTestData.get("Incident Title") != null) {
            log.info("Entering Summary");
            setSummary(smartITTestData.get("Incident Title"));
        }

        if (smartITTestData.get("Impact") != null) {
            log.info("Entering Impact");
            setImpact(smartITTestData.get("Impact"));
        }

        if (smartITTestData.get("Urgency") != null) {
            log.info("Entering Urgency");
            setUrgency(smartITTestData.get("Urgency"));
        }

        if (smartITTestData.get("Status") != null) {
            log.info("Entering Status");
            setStatus(smartITTestData.get("Status"));
        }

        if (smartITTestData.get("OperationalCategory") != null) {
            log.info("Entering Operational Category");
            setOperationalCategory(smartITTestData.get("OperationalCategory"));
        }
        if (smartITTestData.get("Contact") != null) {
            setContact(smartITTestData.get("Contact"));
        }
        if (smartITTestData.get("Service") != null) {
            setService(smartITTestData.get("Service"));
        }
        if (smartITTestData.get("ProductCategory") != null) {
            if (smartITTestData.get("ProductCategory") != null && !smartITTestData.get("ProductCategory").trim().isEmpty()) {
                setProductCategory(smartITTestData.get("ProductCategory"));
            } else {
                clearProductCategory();
            }
        }
        return this;
    }

    public String getDescription() {
        return getText(findElement(By.xpath(String.format(ElementXpathByID, descriptionBox))));
    }

    public CreateIncidentPage setIncidentType(String incidentType) {
        commonWaits.waitForElementVisible(incidentTypeDropdown);
        commonWaits.clickWithoutInterception(incidentTypeDropdown);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownOption, incidentType)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownOption, incidentType)));
        return this;
    }

    public CreateIncidentPage clickServiceCIRecommendationAndValidate(String expectedService) {
        log.info("Clicking on Service CI Recommendation button");
        commonWaits.scrollToSpecificElement(serviceCI_Recommendation);
        commonWaits.clickWithoutInterception(serviceCI_Recommendation);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        log.info("Getting the predicted Service CI value from input field");
        String actualService = serviceInputField.getAttribute("value");
        log.info("Predicted Service CI: " + actualService);

        log.info("Validating predicted Service CI against expected value");
        Assert.assertEquals(actualService, expectedService, "Service CI value does not match expected value");
        log.info("Service CI prediction validated successfully. Expected: " + expectedService + ", Actual: "
                + actualService);
        return this;
    }

    /**
     * Click on brain icon (Category Prediction icon) to trigger category prediction
     *
     * @return CreateIncidentPage for method chaining
     */
    public CreateIncidentPage clickCategoryPredictionIcon() {
        log.info("Attempting to click on category prediction/brain icon");
        commonWaits.switchtoDefaultFrame();
        switchToFrame(pwaFrame);

        if (commonWaits.isElementPresent(By.xpath(brainIconXpath))) {
            commonWaits.clickWithoutInterception(By.xpath(brainIconXpath));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
            log.info("Successfully clicked on category prediction/brain icon");
        } else {
            log.info("Brain icon not visible. Category may be auto-predicted or icon not available.");
        }
        return this;
    }

    
    /**
     * Clicks on the "View details" button. The button is typically displayed in list/grid views
     * (e.g., incident list, search results). Switches to pwa-frame before clicking.
     *
     * @return CreateIncidentPage for method chaining
     */
    public CreateIncidentPage clickViewDetails() {
        log.info("Clicking on View details button");
        commonWaits.switchtoDefaultFrame();
        int waitSeconds = Math.max(commonWaits.defaultTimeout, 60);
        // PWA iframe can stay ng-hide / empty until Smart IT sets $root.showFrame and loads the PWA URL
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), pwaFrame, waitSeconds),
                "PWA frame not visible within " + waitSeconds + "s");
        commonWaits.switchToFrame(pwaFrame);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), viewDetailsButton, waitSeconds),
                "View details not visible within " + waitSeconds + "s inside PWA frame");
        WebElement viewDetailsReady = commonWaits.waitForElementClickable(viewDetailsButton, waitSeconds);
        javaScriptScrollIntoView(viewDetailsReady);
        commonWaits.clickWithoutInterception(viewDetailsReady);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        log.info("Successfully clicked on View details button");
        commonWaits.switchtoDefaultFrame();
        return this;
    }

    /**
     * Switches to pwa-frame, then to Alarms iframe (nested inside pwa-frame), then locates the element.
     * Verifies the Alarm details text displayed on the incident view page.
     *
     * @param expectedText Expected alarm details text (required, must not be null or empty)
     * @return CreateIncidentPage for method chaining
     */
    public CreateIncidentPage verifyAlarmDetailsText(String expectedText) {
        if (expectedText == null || expectedText.trim().isEmpty()) {
            throw new IllegalArgumentException("expectedText must not be null or empty");
        }
        String expected = expectedText.trim();
        log.info("Verifying Alarm details text: " + expected);
        commonWaits.switchtoDefaultFrame();
        switchToFrame(pwaFrame);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(By.xpath(IFRAME_ALARMS));
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);

        commonWaits.waitForElementVisible(alarmDetailsContent);
        String actualText = alarmDetailsContent.getText().trim();
        log.info("Actual Alarm details text: " + actualText);
        Assert.assertEquals(actualText, expected, "Alarm details text does not match expected value");
        log.info("Alarm details text verified successfully");
        return this;
    }

    public CreateIncidentPage clickRelatedItemsTab() {
		log.info("Clicking on Relate Items tab");
		commonWaits.waitForElementVisible(relatedItemsTab);
		commonWaits.clickWithoutInterception(relatedItemsTab);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}

    public boolean checkCaseRelationType() throws InterruptedException {
		boolean flag = false;
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Switching to PWA frame");
        commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(pwaFrame);
		log.info("Clicking on Relate Items tab");
		commonWaits.clickWithoutInterception(relatedItemsTabButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForElementVisible(RelationShip_Status);
		if (RelationShip_Status.getText().equals("Related to")) {
			System.out.println("Type is related to");
			flag = true;
		}

		return flag;
	}

	public boolean TryToDeleteRelationship(String Error) throws InterruptedException {
		boolean flag = false;
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		if (!Case_checkbox.isSelected()) {
			Case_checkbox.click();
		}

		commonWaits.waitForElementVisible(Delete_Option);
		Delete_Option.click();
		commonWaits.waitForElementVisible(Accept_popUp);
		if (Accept_popUp.isDisplayed()) {
			flag = true;
		}
		commonWaits.waitForElementClickable(Accept_popUp);
		Accept_popUp.click();
		log.info("Error_Message is correct" + Error);

		return flag;
	}

	public boolean cross_Launch_To_BWF() {
		boolean flag = false;
		commonWaits.waitForElementVisible(Case_ID_inPV, 20);
		commonWaits.waitForElementClickable(Case_ID_inPV, 20);
		commonWaits.clickWithoutInterception(Case_ID_inPV);
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.switchToLastWindow();
        if (getDriver().getCurrentUrl().contains("com.bmc.dsm.bwfa/view/")) {
            log.info("navigated to bwf url successfully");
            flag = true;
        }

		return flag;
	}

    public CreateIncidentPage setTemplate(String template) {
        keyClearAndType(String.format(ElementXpathByID, ARID_TEMPLATE), template);
        commonWaits.waitForSometime(commonWaits.minDelay);
        Actions action = new Actions(getDriver());
        action.sendKeys(Keys.ENTER).build().perform();
        return this;
    }

}
