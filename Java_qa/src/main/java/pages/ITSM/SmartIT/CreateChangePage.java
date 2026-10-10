package pages.ITSM.SmartIT;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CreateChangePage extends SmartITCommonOperations {

	static SolqaLogger log = new SolqaLogger(CreateIncidentPage.class.getName());
    CommonWaits commonWaits=new CommonWaits();

	public CreateChangePage() {
		PageFactory.initElements(getDriver(), this);
	}

    // -------------------- Locators / constants --------------------
	String XPATH_BY_ID_TEMPLATE = "//*[@id='%s']";
	String XPATH_TYPEAHEAD_OPTION_BY_ID_AND_CLASS_TEMPLATE = "//*[contains(@id, '%s') and contains(@class, '%s')]";
	String XPATH_IFRAME_PWA = "//iframe[@id='pwa-frame']";
	String ID="(//*[contains(@id,'%s')])";
	String IDForDate="xpath=(//*[contains(@id,'%s')])";
	String AdaptTextBox="xpath=(//*[@id='%s'])";
	String checkedItem = "(//*[@id='ar301260800']//*[text()[contains(.,'%s')]]/..//*[contains(@class,'ui-chkbox-box') or contains(@id,'adapt-radiobutton')])";
	String dropdownOption = "(//adapt-dropdown-window2//*[text()='%s'])[last()]//ancestor::button"; 
	String CancelButton="//button[@name='ar301614900']";

	// AR IDs
	String ARID_SAVE_CHANGE = "304440891";
	String ARID_ASSIGN_TO_ME = "304421561";
	String ScheduledStartdates="1000000350";
	String ScheduledEnddates="1000000362";
	String ActualStartdates="1000000348";
	String ActualEnddates="1000000364";
	String RelatedConfigurationItemBtn="304424121";
	String CISearchTextbox="304449871";
	String RelateConfiguritonItemCITable="304449901";
	String AddConfigurationItemBlade="ar301260800";
	String AssociationType="304419791";
	String RelatedItemBladeSave="304253220";

	// Save / Assign
	String XPATH_SAVE_BUTTON_BY_ID = "//*[@id='" + ARID_SAVE_CHANGE + "']";
	String XPATH_ASSIGN_TO_ME_LEGACY = "//*[@title='Assign to me' and @id='" + ARID_ASSIGN_TO_ME + "']";
	String XPATH_ASSIGN_TO_ME_TITLE_OR_ARIA_OR_TEXT = "//*[@title='Assign to me' or @aria-label='Assign to me' or normalize-space(.)='Assign to me']";
	String XPATH_ASSIGN_TO_ME_BUTTON_TITLE_OR_ARIA_OR_TEXT = "//button[@title='Assign to me' or @aria-label='Assign to me' or .//*[normalize-space(.)='Assign to me']]";
	String XPATH_ASSIGN_TO_ME_CONTAINS_TITLE_OR_ARIA_CASE_INSENSITIVE =
			"//*[contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'assign to me')"
					+ " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'assign to me')]";
	String XPATH_SAVE_BUTTON_BY_ID_OR_ARIA_OR_TITLE_OR_TEXT_TEMPLATE = "//button[@id='%s' or @aria-label='Save' or @title='Save' or normalize-space(.)='Save' or .//span[normalize-space(.)='Save']]";
	String XPATH_SAVE_BUTTON_CLASS_OR_TESTID = "//button[contains(@class,'save') or contains(@data-testid,'save')][not(@disabled)]";
	String XPATH_SAVE_BUTTON_GENERIC = "//*[self::button or self::a][normalize-space(.)='Save' or @title='Save' or @aria-label='Save']";

	/** Same XPath string as {@link #saveButtonAdaptPrimary}; use with {@link By#xpath(String)} for candidate lists. */
	private static final String XPATH_SAVE_BUTTON_ADAPT_PRIMARY =
			"//button[@adapt-button][contains(@class,'btn-primary')][@role='button'][.//span[normalize-space(.)='Save']]";
	/**
	 * BWF {@code rx-info-bar} display id (e.g. {@code aria-label="Display Id , CASE-0000013055"}).
	 * Same string as {@link #rxInfoBarDisplayId}.
	 */
	private static final String XPATH_RX_INFO_BAR_DISPLAY_ID =
			"//rx-info-bar//div[contains(@class,'title')][contains(@aria-label,'Display Id')]";
	@FindBy(xpath = XPATH_SAVE_BUTTON_ADAPT_PRIMARY)
	WebElement saveButtonAdaptPrimary;
	@FindBy(xpath = XPATH_RX_INFO_BAR_DISPLAY_ID)
	WebElement rxInfoBarDisplayId;

	@FindBy(xpath = "//span[normalize-space()='Save']/parent::button[@title='Save ticket']")
	WebElement saveChangeButton;
	/** Open {@code adapt-rx-select} panel: multiple filter inputs — use {@link #findElements(By)}. */
	private static final String XPATH_RX_SELECT_OPEN_PANEL_FILTER_INPUTS =
			"//*[contains(@class,'rx-select__options') or contains(@class,'a-dropdown-window')]"
					+ "//input[not(@type='hidden') and not(@type='checkbox') and not(@type='radio')]";
	final By byRxSelectOpenPanelFilterInputs = By.xpath(XPATH_RX_SELECT_OPEN_PANEL_FILTER_INPUTS);

	// BWF adapt-rx-select / {@link #getInputValueByLabel} — single {@code %s} = {@link #quoteForXpathContains} result
	String XPATH_ADAPT_RX_SELECT_CONTAINER_BY_LABEL =
			"//adapt-rx-select[.//*[self::label or self::span][contains(normalize-space(.),%s)]]";
	String XPATH_ADAPT_RX_SELECT_DISPLAY_BY_LABEL =
			XPATH_ADAPT_RX_SELECT_CONTAINER_BY_LABEL + "//div[contains(@class,'rx-select__search-button-title')]";
	String XPATH_ADAPT_RX_SELECT_COMBOBOX_BY_LABEL =
			XPATH_ADAPT_RX_SELECT_CONTAINER_BY_LABEL + "//button[@role='combobox']";
	String XPATH_INPUT_FOLLOWING_LABEL =
			"//*[self::label or self::span or self::div][contains(normalize-space(.),%s)]/following::input[1]";
	String XPATH_CONTENTEDITABLE_AFTER_LABEL =
			"//*[self::label][contains(normalize-space(.),%s)]/following::div[@contenteditable='true'][1]";
	String XPATH_ADAPT_RX_TEXTAREA_CONTAINS_LABEL = "//adapt-rx-textarea[contains(normalize-space(.), %s)]//textarea";

	// Dropdown helpers
	String XPATH_CLEAR_BUTTON_BY_LABEL_TEMPLATE = "//*[text()='%s']/ancestor::div[@class='select_container ng-star-inserted']/div";
	String XPATH_DROPDOWN_BY_LABEL_TEMPLATE = "//*[text()='%s']/ancestor::adapt-rx-select";
	String XPATH_DROPDOWN_VALUE_TEMPLATE = "//*[contains(@class, 'rx-select__options-scroll')]/button[contains(.,'%s')]";

	// Build-agnostic typeahead (label â†’ input)
	String XPATH_TYPEAHEAD_INPUT_BY_LABEL_TEMPLATE =
			"//*[self::label or self::span or self::div][normalize-space(.)=%s]/following::input[1]"
					+ " | //*[self::label or self::span or self::div][normalize-space(.)=%s]/following::textarea[1]"
					+ " | //label[normalize-space(.)=%s]/following::input[1]"
					+ " | //label[normalize-space(.)=%s]/following::textarea[1]";
	String XPATH_TYPEAHEAD_INPUT_BY_PLACEHOLDER_CONTAINS_TEMPLATE = "//input[contains(@placeholder,%s)] | //textarea[contains(@placeholder,%s)]";
	String XPATH_TYPEAHEAD_OPTION_BY_VALUE_CONTAINS_TEMPLATE =
			"//*[@role='listbox']//*[self::div or self::button or self::li or self::span][contains(normalize-space(.), %s)]"
					+ " | //*[contains(@class,'dropdown') or contains(@class,'typeahead') or contains(@class,'autocomplete')]"
					+ "//*[self::div or self::button or self::li or self::span][contains(normalize-space(.), %s)]";
	String XPATH_TYPEAHEAD_ANY_OPTION =
			"//*[@role='listbox']//*[self::div or self::button or self::li][contains(@class,'dropdown-item') or @role='option']"
					+ " | //*[contains(@class,'dropdown') or contains(@class,'typeahead') or contains(@class,'autocomplete')]"
					+ "//*[self::div or self::button or self::li][contains(@class,'dropdown-item') or @role='option']";

	String ARID_CHANGE_COORDINATOR_INPUT = "ar1000003230";
	String ARID_SUMMARY_INPUT = "1000000000";
	String ARID_REQUESTED_FOR_INPUT = "ar304438111";
	String ARID_OPERATIONAL_CATEGORY_INPUT = "ar304420021";
	String ARID_PRODUCT_CATEGORY_INPUT = "ar304420031";
	String ARID_RISK_LEVEL_DROPDOWN = "1000000180";
	String CLASS_TYPEAHEAD_DROPDOWN_ITEM = "dropdown-item";
	String ARID_UPDATE_LOCATION_BUTTON = "302180500";
	String CLASS_UPDATE_LOCATION_BUTTON = "smt-btn undefined";

	// Risk level
	String XPATH_SELECT_RISK_LEVEL_MANUALLY = "//*[contains(text(),'Select risk level manually')]/parent::label[@class='radio__item']";
	String XPATH_RISK_LEVEL_OPTION_TEMPLATE = "//*[contains(@class, 'rx-select__options')]/button[contains(.,'%s')]";

	// Change location / dates / CI relate (used by DRSQA-11379)
	String LABEL_REGION = "Region";
	String LABEL_SITE_GROUP = "Site group";
	String LABEL_SITE = "Site";
	String LABEL_DATES = "Dates";
	String LABEL_CIS = "CIs";
	String LABEL_CHANGE_LOCATION = "Change location";

	String XPATH_LEFT_NAV_ITEM_BY_TEXT_CONTAINS_TEMPLATE = "//*[contains(@class,'left') or contains(@class,'side') or self::nav]"
			+ "//*[self::a or self::button or self::li][contains(normalize-space(.), %s)]";

	String XPATH_DATE_INPUT_BY_LABEL_CONTAINS_TEMPLATE = "//label[contains(normalize-space(.), %s)]/following::input[1]"
			+ " | //*[@aria-label and contains(normalize-space(@aria-label), %s)]";

	String LABEL_SCHEDULED_START = "Scheduled Start";
	String LABEL_SCHEDULED_END = "Scheduled End";
	DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("M/d/yyyy");

	String XPATH_RELATE_CI_BUTTON = "//button[contains(.,'Relate configuration item') or contains(.,'Relate configuration items')]";
	String XPATH_SEARCH_CI_TEXTBOX = "//*[contains(@placeholder,'Search') and (contains(@placeholder,'CI') or contains(@placeholder,'CIs'))]";
	String XPATH_CI_CHECKBOX_ANY = "//div[contains(@class, 'ui-chkbox-box')]";
	String XPATH_ASSOCIATION_TYPE_BUTTON = "//*[@title='Select' or @aria-label='Select']";
	String XPATH_RELATED_ITEM_BLADE_SAVE = "//*[@id='304253220' or normalize-space(.)='Save']";
	String XPATH_RELATIONSHIP_OPTION_TEMPLATE = "//*[contains(@class, 'rx-select__options-')]/button[contains(.,'%s')]";
	String XPATH_CONFLICT_MESSAGE_BY_TEXT_CONTAINS_TEMPLATE =
			"//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), %s)"
			+ " and (self::div or self::span or self::p)]";

	// Public string constants (kept for backward compatibility in this class)
	public String SaveButton = XPATH_SAVE_BUTTON_BY_ID;
	public String IFrame = XPATH_IFRAME_PWA;
	public String assignToMe = XPATH_ASSIGN_TO_ME_LEGACY;
	public String ElementXpathByID = XPATH_BY_ID_TEMPLATE;
	public String typeheadListElementID = XPATH_TYPEAHEAD_OPTION_BY_ID_AND_CLASS_TEMPLATE;
	public String clearButton = XPATH_CLEAR_BUTTON_BY_LABEL_TEMPLATE;
	public String ElementDropdownXpathByText = XPATH_DROPDOWN_BY_LABEL_TEMPLATE;
	public String DropdownValue = XPATH_DROPDOWN_VALUE_TEMPLATE;
	public String riskLevelButton = XPATH_SELECT_RISK_LEVEL_MANUALLY;
	public String RiskLevelOption = XPATH_RISK_LEVEL_OPTION_TEMPLATE;
	public String changeCoordinatorTextArea = ARID_CHANGE_COORDINATOR_INPUT;
	public String summaryTextArea = ARID_SUMMARY_INPUT;
	public String requestedForTextArea = ARID_REQUESTED_FOR_INPUT;
	public String operationalCategoryTextArea = ARID_OPERATIONAL_CATEGORY_INPUT;
	public String productCategoryTextArea = ARID_PRODUCT_CATEGORY_INPUT;
	public String riskLevelDropdown = ARID_RISK_LEVEL_DROPDOWN;
	public String typeheadListRequestedForTextArea = CLASS_TYPEAHEAD_DROPDOWN_ITEM;
	public String typeheadListOperationalCategoryTextArea = CLASS_TYPEAHEAD_DROPDOWN_ITEM;
	public String typeheadListProductCategoryTextArea = CLASS_TYPEAHEAD_DROPDOWN_ITEM;
	public String typeheadListchangeCoordinatorTextArea = CLASS_TYPEAHEAD_DROPDOWN_ITEM;
	public String updateLocationButton = ARID_UPDATE_LOCATION_BUTTON;
	public String typeheadListUpdateLocationButton = CLASS_UPDATE_LOCATION_BUTTON;

	// NOTE: kept but not used in current flow
	public String AssignToMeLink = "//*[contains(text(),'%s')]";
	public String Answers = "//*[@id='304450801']//*[@question-number=%s]//input/following-sibling::label";
    public String textAreaByLabel = "//adapt-rx-textarea[contains(normalize-space(.), '%s')]//textarea";

	public void switchToCreateChangeContext() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		try {
			switchtoDefaultFrame();
			if (isElementPresent(By.xpath(XPATH_IFRAME_PWA))) {
				switchToFrame(By.xpath(XPATH_IFRAME_PWA));
			}
		} catch (Exception ignored) {}
	}

	public By byTypeaheadInputForLabel(String labelText) {
		String quoted = quoteForXpathContains(labelText);
		return By.xpath(String.format(
				XPATH_TYPEAHEAD_INPUT_BY_LABEL_TEMPLATE,
				quoted, quoted, quoted, quoted
		));
	}

	public By byTypeaheadOption(String value) {
		String quoted = quoteForXpathContains(value);
		return By.xpath(String.format(XPATH_TYPEAHEAD_OPTION_BY_VALUE_CONTAINS_TEMPLATE, quoted, quoted));
	}

	public By byAnyTypeaheadOption() {
		return By.xpath(XPATH_TYPEAHEAD_ANY_OPTION);
	}

	public CreateChangePage setTypeaheadByLabel(String labelText, String value) {
		if (value == null || value.trim().isEmpty()) {
			return this;
		}
		switchToCreateChangeContext();
		// BWF Create Change: many fields (Customer, assignment groups, …) use adapt-rx-select
		// (combobox + rx-select__options), not a plain typeahead input after the label.
		String quotedForAdapt = quoteForXpathContains(labelText.trim());
		if (isElementPresent(By.xpath(String.format(XPATH_ADAPT_RX_SELECT_CONTAINER_BY_LABEL, quotedForAdapt)))) {
			return setAdaptRxSelectByLabel(labelText, value);
		}

		By inputBy = byTypeaheadInputForLabel(labelText);

		List<WebElement> inputs = findElements(inputBy);
		if (inputs == null || inputs.isEmpty()) {
			// Fallback: try by placeholder containing the label words
			String quoted = quoteForXpathContains(labelText);
			inputBy = By.xpath(String.format(XPATH_TYPEAHEAD_INPUT_BY_PLACEHOLDER_CONTAINS_TEMPLATE, quoted, quoted));
			inputs = findElements(inputBy);
		}

		if (inputs == null || inputs.isEmpty()) {
			log.info("Typeahead input not found for label: " + labelText);
			return this;
		}

		WebElement input = inputs.get(0);
		try { javaScriptScrollIntoView(input); } catch (Exception ignored) {}
		commonWaits.clickWithoutInterception(input);
		input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		input.sendKeys(Keys.DELETE);
		commonWaits.waitForSometime(commonWaits.minDelay); // This wait is required to click on the input field
		Actions actions = new Actions(getDriver());
        actions.moveByOffset(10, 10).click().build().perform();
		keyClearAndType(input, value);
		commonWaits.waitForSometime(commonWaits.moreDelay); // This wait is required to click on the input field
		commonWaits.waitForExecutionComplete();

		// Wait for suggestions and select best match (or first option)
		long start = System.currentTimeMillis();
		long timeoutMs = 20_000;
		while (System.currentTimeMillis() - start < timeoutMs) {
			List<WebElement> exact = findElements(byTypeaheadOption(value));
			if (exact != null && !exact.isEmpty()) {
				commonWaits.clickWithoutInterception(exact.get(0));
				return this;
			}
			List<WebElement> any = findElements(byAnyTypeaheadOption());
			if (any != null && !any.isEmpty()) {
				commonWaits.clickWithoutInterception(any.get(0));
				return this;
			}
			commonWaits.waitForSometime(500);
		}
		log.info("No typeahead option found for value '" + value + "' under label '" + labelText + "'");
		return this;
	}

	public CreateChangePage selectChangeTemplate(String templateName) {
		if (templateName == null || templateName.trim().isEmpty()) {
			return this;
		}
		log.info("Selecting Change Template: " + templateName);
		setTypeaheadByLabel("Select Template", templateName);
		// Some builds label it as "Template"
		setTypeaheadByLabel("Template", templateName);
		return this;
	}

	/**
	 * Quote text for safe XPath contains()/normalize-space() usage.
	 */
	public static String quoteForXpathContains(String text) {
		if (text == null) {
			return "''";
		}
		if (!text.contains("'")) {
			return "'" + text + "'";
		}
		if (!text.contains("\"")) {
			return "\"" + text + "\"";
		}
		// Both quotes present â†’ concat('a', "\"", 'b', "'", 'c')
		StringBuilder sb = new StringBuilder("concat(");
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (i > 0) sb.append(",");
			if (c == '\'') {
				sb.append("\"'\"");
			} else if (c == '"') {
				sb.append("'\"'");
			} else {
				sb.append("'").append(c).append("'");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public CreateChangePage FillChangeTemplate(Map<Object, String> smartITTestData) {
		log.info("Filling Change Details");
		switchToCreateChangeContext();
		// Wait for Create Change page to render (best-effort)
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		if (smartITTestData.get("RequestedFor") != null) {
		log.info("Entering Customer");
		selectRequestedForPersonForChangeTemplate(smartITTestData.get("RequestedFor"));
		}

		// Template is often required to enable Save in some environments
		if (smartITTestData.get("Template") != null) {
			selectChangeTemplate(smartITTestData.get("Template"));
		}

		if (smartITTestData.get("Change Title") != null) {
		log.info("Entering Summary");
		setSummary(smartITTestData.get("Change Title"));
		}

        // Optional: Fill Description/Notes (used by DWP approval details: "Notes")
        if (smartITTestData.get("Change Description") != null) {
            String descriptionLabel = smartITTestData.get("Change Description Label") != null
                    ? smartITTestData.get("Change Description Label")
                    : "Description";
            log.info("Entering Change Description");
            setTextAreaByLabel(descriptionLabel, smartITTestData.get("Change Description"));
        }

		if (smartITTestData.get("ChangeClass") != null) {
			log.info("Setting Change Class");
			setChangeClass(smartITTestData.get("ChangeClass"));
		}

		if (smartITTestData.get("Impact") != null) {
			log.info("Entering Impact");
			setImpact(smartITTestData.get("Impact"));
		}

		if (smartITTestData.get("Urgency") != null) {
			log.info("Entering Urgency");
			setUrgency(smartITTestData.get("Urgency"));
		}

		if (smartITTestData.get(SolQAConstants.PRIORITY) != null) {
			log.info("Setting Priority");
			setPriority(smartITTestData.get(SolQAConstants.PRIORITY));
		}

		if (smartITTestData.get("OperationalCategory") != null) {
			log.info("Entering Operational Category");
			setTypeaheadField("Operational category", smartITTestData.get("OperationalCategory"));
		}
		if (smartITTestData.get("ProductCategory") != null) {
            setTypeaheadField("Product category", smartITTestData.get("ProductCategory"));
		}
		if (smartITTestData.get("RiskLevel") != null) {
			setRiskLevel(smartITTestData.get("RiskLevel"));
		}
		if (smartITTestData.get("ChangeCoordinator") != null) {
			log.info("Setting Change Coordinator");
			setChangeCoordinator(smartITTestData.get("ChangeCoordinator"));
		}

		// Assignment/Service fields (label-based, build-agnostic)
		setTypeaheadByLabel("Change manager group", smartITTestData.get("ChangeManagerGroup"));
		setTypeaheadByLabel("Change manager", smartITTestData.get("ChangeManager"));
		setTypeaheadByLabel("Change coordinator group", smartITTestData.get("ChangeCoordinatorGroup"));
		// Some UIs label person field as "Change coordinator"
		setTypeaheadByLabel("Change coordinator", smartITTestData.get("ChangeCoordinator"));
		setTypeaheadByLabel("Service", smartITTestData.get("Service"));
		setTypeaheadByLabel("Company", smartITTestData.get("Company"));
		return this;
	}

    public void setTextAreaByLabel(String label, String value) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textAreaByLabel, label)));
        WebElement area = getDriver().findElement(By.xpath(String.format(textAreaByLabel, label)));
        commonWaits.scrollToElement(area);
        area.clear();
        area.sendKeys(value);
    }

	public CreateChangePage selectRequestedForPersonForChangeTemplate(String RequestedFor) {
		switchToCreateChangeContext();

		// Old locator for suggestion item is not stable across builds; use label-based typeahead selection.
		setTypeaheadByLabel("Requested for", RequestedFor);

		// Best-effort: if location update button is shown, click it
		try {
			By updateLocationBy = By.xpath(String.format(typeheadListElementID, updateLocationButton, typeheadListUpdateLocationButton));
			if (isElementPresent(updateLocationBy)) {
				commonWaits.clickWithoutInterception(updateLocationBy);
			}
		} catch (Exception ignored) {}
		return this;
	}

	public CreateChangePage setSummary(String summary) {
		Actions actions = new Actions(getDriver());
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, summaryTextArea)));
        actions.moveByOffset(10, 10).click().build().perform();
		keyClearAndType(String.format(ElementXpathByID, summaryTextArea), summary);
		return this;
	}

	public CreateChangePage setImpact(String impact) {
  	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(clearButton, SolQAConstants.IMPACT)));
		log.info("Clearing the impact value");
		commonWaits.clickWithoutInterception(By.xpath(String.format(clearButton, SolQAConstants.IMPACT)));
		log.info("Updating the Impact value");
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementDropdownXpathByText, SolQAConstants.IMPACT)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownValue, impact)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(DropdownValue, impact)));
		return this;
	}

	public CreateChangePage setUrgency(String urgency) {
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(clearButton, SolQAConstants.URGENCY)));
		log.info("Clearing the urgency value");
		commonWaits.clickWithoutInterception(By.xpath(String.format(clearButton, SolQAConstants.URGENCY)));
		log.info("Updating the urgency value");
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementDropdownXpathByText, SolQAConstants.URGENCY)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownValue, urgency)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(DropdownValue, urgency)));
		return this;
	}

	public CreateChangePage setOperationalCategory(String operationalCategory) {
		keyClearAndType(String.format(ElementXpathByID, operationalCategoryTextArea), operationalCategory);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementID, operationalCategoryTextArea, typeheadListOperationalCategoryTextArea)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementID, operationalCategoryTextArea, typeheadListOperationalCategoryTextArea)));
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}

	public CreateChangePage setProductCategory(String productCategory) {
		keyClearAndType(String.format(ElementXpathByID, productCategoryTextArea), productCategory);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListElementID, productCategoryTextArea, typeheadListProductCategoryTextArea)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementID, productCategoryTextArea, typeheadListProductCategoryTextArea)));
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}

	public CreateChangePage clickAssignedToMe_ChangeManager() {
		log.info("Clicking on 'Assign To Me' link");

		// This control is build-dependent:
		// - It can be in PWA iframe or in default DOM
		// - It may not appear if the ticket is already assigned
		// So: try best-effort click; do NOT hard-fail if not present.
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		By[] candidates = new By[] {
				By.xpath(XPATH_ASSIGN_TO_ME_LEGACY), // legacy exact locator
				By.xpath(XPATH_ASSIGN_TO_ME_TITLE_OR_ARIA_OR_TEXT),
				By.xpath(XPATH_ASSIGN_TO_ME_BUTTON_TITLE_OR_ARIA_OR_TEXT),
				By.xpath(XPATH_ASSIGN_TO_ME_CONTAINS_TITLE_OR_ARIA_CASE_INSENSITIVE)
		};

		boolean clicked = tryClickAssignToMe(candidates, true) || tryClickAssignToMe(candidates, false);

		if (!clicked) {
			log.info("'Assign to me' control not found/visible. Continuing (change may already be assigned).");
			switchtoDefaultFrame();
			return this;
		}

		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		return this;
	}

	public boolean tryClickAssignToMe(By[] candidates, boolean insidePwaFrame) {
		try {
			switchtoDefaultFrame();
			if (insidePwaFrame) {
				switchToFrame(By.xpath(XPATH_IFRAME_PWA));
			}

			long start = System.currentTimeMillis();
			long timeoutMs = 20_000;
			while (System.currentTimeMillis() - start < timeoutMs) {
				for (By by : candidates) {
					try {
						List<WebElement> els = findElements(by);
						if (els == null || els.isEmpty()) {
							continue;
						}
						WebElement el = els.get(0);
						if (el == null || !el.isDisplayed()) {
							continue;
						}
						try {
							javaScriptScrollIntoView(el);
						} catch (Exception ignored) {}

						try { commonWaits.clickWithoutInterception(el); }
						catch (Exception e) { clickByJS(el); }
						return true;
					} catch (Exception ignored) {}
				}
				commonWaits.waitForSometime(500);
			}
			return false;
		} catch (Exception ignored) {
			return false;
		}
	}

	public CreateChangePage setRiskLevel(String riskLevel) {

		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));

		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(riskLevelButton)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(riskLevelButton)));

		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, riskLevelDropdown)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID, riskLevelDropdown)));

		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(RiskLevelOption, riskLevel)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(RiskLevelOption, riskLevel)));
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}


	/**
	 * Clicks the BWF/Adapt primary Save control: {@code @adapt-button}, {@code btn-primary},
	 * {@code role="button"}, nested span {@code Save} — {@link #saveButtonAdaptPrimary}.
	 * Tries PWA iframe first, then top-level DOM (same as {@link #tryClickSave(By[], boolean)}).
	 */
	public CreateChangePage clickSaveButtonAdaptPrimary() {
		log.info("Clicking primary Save (adapt-button / btn-primary)");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		for (boolean usePwaFrame : new boolean[] { true, false }) {
			try {
				switchtoDefaultFrame();
				if (usePwaFrame) {
					if (!isElementPresent(By.xpath(XPATH_IFRAME_PWA))) {
						continue;
					}
					switchToFrame(By.xpath(XPATH_IFRAME_PWA));
				}
				WebElement btn = commonWaits.waitForElementClickable(saveButtonAdaptPrimary, 15);
				try {
					javaScriptScrollIntoView(btn);
				} catch (Exception ignored) {
				}
				commonWaits.clickWithoutInterception(btn);
				return this;
			} catch (Exception ignored) {
			}
		}
		throw new AssertionError(
				"Primary Save button not found or not clickable (adapt-button / btn-primary / span Save).");
	}

	public CreateChangePage saveChange() {
		log.info("Clicking on Save Button on Change");
        switchToPWAFrame();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveChangeButton, 20),
                "Save button not visible after waiting");
		commonWaits.clickWithoutInterception(saveChangeButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		return this;
	}

	public boolean tryClickSave(By[] candidates, boolean insidePwaFrame) {
		try {
			switchtoDefaultFrame();
			if (insidePwaFrame) {
				switchToFrame(By.xpath(XPATH_IFRAME_PWA));
			}

			long start = System.currentTimeMillis();
			long timeoutMs = 25_000;
			while (System.currentTimeMillis() - start < timeoutMs) {
				for (By by : candidates) {
					try {
						List<WebElement> els = findElements(by);
						if (els == null || els.isEmpty()) {
							continue;
						}
						WebElement el = els.get(0);
						if (el == null || !el.isDisplayed()) {
							continue;
						}
						try { javaScriptScrollIntoView(el); } catch (Exception ignored) {}

						try { commonWaits.clickWithoutInterception(el); }
						catch (Exception e) { clickByJS(el); }
						return true;
					} catch (Exception ignored) {}
				}
				commonWaits.waitForSometime(500);
			}
			return false;
		} catch (Exception ignored) {
			return false;
		}
	}


	public CreateChangePage setChangeClass(String changeClass) {
		log.info("Setting Change Class: " + changeClass);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(clearButton, SolQAConstants.CHANGE_CLASS)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(clearButton, SolQAConstants.CHANGE_CLASS)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementDropdownXpathByText, SolQAConstants.CHANGE_CLASS)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownValue, changeClass)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(DropdownValue, changeClass)));
		return this;
	}

	public CreateChangePage setPriority(String priority) {
		log.info("Setting Priority: " + priority);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(clearButton, SolQAConstants.PRIORITY)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(clearButton, SolQAConstants.PRIORITY)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementDropdownXpathByText, SolQAConstants.PRIORITY)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownValue, priority)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(DropdownValue, priority)));
		return this;
	}

	public CreateChangePage setChangeCoordinator(String changeCoordinator) {
		log.info("Setting Change Coordinator: " + changeCoordinator);
		keyClearAndType(String.format(ElementXpathByID, changeCoordinatorTextArea), changeCoordinator);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		By dropdownLocator = By.xpath(String.format(typeheadListElementID, changeCoordinatorTextArea, typeheadListchangeCoordinatorTextArea));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.clickWithoutInterception(dropdownLocator);
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}

	// -------------------- Helpers for DRSQA-11379 --------------------
	public CreateChangePage setChangeLocation(String commaSeparatedLocation) {
		// Expected format: "Region,SiteGroup,Site" (as in Umbrella test)
		if (commaSeparatedLocation == null || commaSeparatedLocation.trim().isEmpty()) {
			return this;
		}
		String[] parts = commaSeparatedLocation.split(",");
		String region = parts.length > 0 ? parts[0].trim() : "";
		String siteGroup = parts.length > 1 ? parts[1].trim() : "";
		String site = parts.length > 2 ? parts[2].trim() : "";

		clickLeftNavSection(LABEL_CHANGE_LOCATION);
		setTypeaheadByLabel(LABEL_REGION, region);
		setTypeaheadByLabel(LABEL_SITE_GROUP, siteGroup);
		setTypeaheadByLabel(LABEL_SITE, site);
		return this;
	}

	public String getDate_pv(int days, String amPm) {
		Calendar cal = Calendar.getInstance();
		
		// Reset time to base
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	
		// Add days
		cal.add(Calendar.DAY_OF_MONTH, days);
	
		// Set AM/PM
		if (amPm.equalsIgnoreCase("PM")) {
			cal.set(Calendar.AM_PM, Calendar.PM);
			cal.set(Calendar.HOUR, 12); // Noon
		} else {
			cal.set(Calendar.AM_PM, Calendar.AM);
			cal.set(Calendar.HOUR, 12); // Midnight (12 AM)
		}
	
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		return formatter.format(cal.getTime());
	}

	public WebElement typeKeys(String locator, String value) {
    
		WebElement we = getElement(locator);
		try {
		  we = typeKeys(we, value);
		  commonWaits.waitForExecutionComplete();
		} catch (Exception e) {
		  log.debug("ERROR" + e);
		  Assert.fail("Unable to type in " + locator);
		}
		return we;
	  }

	public CreateChangePage fillScheduleDates(int startOffsetDays, int endOffsetDays) {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		typeKeys(String.format(IDForDate, ScheduledStartdates), getDate_pv(startOffsetDays, "AM"));
		typeKeys(String.format(IDForDate, ScheduledEnddates), getDate_pv(endOffsetDays, "AM"));
		Actions actions = new Actions(getDriver());
        actions.moveByOffset(10, 10).click().perform();
		return this;
	}

	public CreateChangePage fillActualDates(int startOffsetDays, int endOffsetDays) {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		typeKeys(String.format(IDForDate, ActualStartdates), getDate_pv(startOffsetDays, "AM"));
		typeKeys(String.format(IDForDate, ActualEnddates), getDate_pv(endOffsetDays, "AM"));
		return this;
	}

	public CreateChangePage relateCIs(String relationshipType, String[] CIList) {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		for (int i = 1; i <= CIList.length; i++) {
			commonWaits.clickWithoutInterception(By.xpath(String.format(ID, RelatedConfigurationItemBtn)));
			typeKeys(String.format(AdaptTextBox, CISearchTextbox), CIList[i - 1]);
			commonWaits.waitForSometime(commonWaits.moreDelay);
		    Actions actions = new Actions(getDriver());
			actions.sendKeys(Keys.ENTER).build().perform();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			click(String.format(checkedItem, CIList[i - 1]));
			click(String.format(ID, AssociationType));
			click(String.format(dropdownOption, relationshipType));
			click(String.format(ID,RelatedItemBladeSave));
		}
		return this;
	}

	public CreateChangePage verifyChangeFreezeConflictMessagePresent(String expectedContains) {
		// Umbrella validated presence of conflict message element; use a generic contains check.
		String needle = expectedContains == null || expectedContains.trim().isEmpty() ? "conflict" : expectedContains.trim();
		String quoted = quoteForXpathContains(needle.toLowerCase());
		switchToCreateChangeContext();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		By msgBy = By.xpath(String.format(XPATH_CONFLICT_MESSAGE_BY_TEXT_CONTAINS_TEMPLATE, quoted));
		Assert.assertTrue(isElementPresent(msgBy), "Conflict message not displayed for change during freeze period.");
		return this;
	}

	public CreateChangePage clickLeftNavSection(String sectionText) {
		String quoted = quoteForXpathContains(sectionText);
		switchToCreateChangeContext();
		By navBy = By.xpath(String.format(XPATH_LEFT_NAV_ITEM_BY_TEXT_CONTAINS_TEMPLATE, quoted));
		List<WebElement> items = findElements(navBy);
		if (items != null && !items.isEmpty()) {
			commonWaits.clickWithoutInterception(items.get(0));
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		}
		return this;
	}

	public CreateChangePage setDateInputByLabelContains(String labelContains, String dateValue) {
		String quoted = quoteForXpathContains(labelContains);
		switchToCreateChangeContext();
		By inputBy = By.xpath(String.format(XPATH_DATE_INPUT_BY_LABEL_CONTAINS_TEMPLATE, quoted, quoted));
		List<WebElement> inputs = findElements(inputBy);
		if (inputs == null || inputs.isEmpty()) {
			// Some builds render date inputs only after nav selection; keep it best-effort but fail for required fields.
			throw new AssertionError("Date input not found for label: " + labelContains);
		}
		WebElement input = inputs.get(0);
		commonWaits.clickWithoutInterception(input);
		input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		input.sendKeys(Keys.DELETE);
		input.clear();
		input.sendKeys(dateValue);
		input.sendKeys(Keys.TAB);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}

	/**
	 * Select a value in an {@code adapt-rx-select} (BWF/Helix combobox with optional pagination search).
	 */
	private CreateChangePage setAdaptRxSelectByLabel(String labelText, String value) {
		String quotedLabel = quoteForXpathContains(labelText.trim());
		String quotedValue = quoteForXpathContains(value.trim());
		WebElement combo = commonWaits.waitForByElementToBeVisible(
				By.xpath(String.format(XPATH_ADAPT_RX_SELECT_COMBOBOX_BY_LABEL, quotedLabel)));
		try {
			javaScriptScrollIntoView(combo);
		} catch (Exception ignored) {
		}
		commonWaits.clickWithoutInterception(combo);
		commonWaits.waitForSometime(300);

		long start = System.currentTimeMillis();
		long timeoutMs = 20_000;
		while (System.currentTimeMillis() - start < timeoutMs) {
			List<WebElement> opts = findElements(By.xpath(
					"//*[contains(@class,'rx-select__options')]//*[self::button][contains(normalize-space(.),"
							+ quotedValue + ")]"));
			if (opts != null && !opts.isEmpty()) {
				commonWaits.clickWithoutInterception(opts.get(0));
				commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
				return this;
			}
			opts = findElements(By.xpath("//*[@role='listbox']//*[contains(normalize-space(.)," + quotedValue + ")]"
					+ "[self::button or self::div or self::span or self::li]"));
			if (opts != null && !opts.isEmpty()) {
				commonWaits.clickWithoutInterception(opts.get(0));
				commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
				return this;
			}
			// Paginated lists: filter/search inside the open panel
			List<WebElement> searchInputs = findElements(byRxSelectOpenPanelFilterInputs);
			if (searchInputs != null && !searchInputs.isEmpty()) {
				WebElement si = searchInputs.get(0);
				try {
					commonWaits.clickWithoutInterception(si);
					si.sendKeys(Keys.chord(Keys.CONTROL, "a"));
					si.sendKeys(Keys.DELETE);
					si.sendKeys(value);
				} catch (Exception ignored) {
				}
				commonWaits.waitForSometime(500);
			}
			commonWaits.waitForSometime(400);
		}
		log.info("No adapt-rx-select option found for value '" + value + "' under label '" + labelText + "'");
		return this;
	}

    public String getInputValueByLabel(String labelText) {
        switchToCreateChangeContext();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (labelText == null || labelText.trim().isEmpty()) {
            throw new IllegalArgumentException("Label text cannot be null/empty");
        }
        String quoted = quoteForXpathContains(labelText.trim());

        try {
            // 1) Dropdowns (Customer, Company, Class, Impact, …)
            By selectBy = By.xpath(String.format(XPATH_ADAPT_RX_SELECT_DISPLAY_BY_LABEL, quoted));
            if (isElementPresent(selectBy)) {
                WebElement title = commonWaits.waitForByElementToBeVisible(selectBy);
                return title.getText().trim();
            }
            // 2) Standard inputs (e.g. Summary on adapt-rx-textfield)
            By inputBy = By.xpath(String.format(XPATH_INPUT_FOLLOWING_LABEL, quoted));
            if (isElementPresent(inputBy)) {
                WebElement input = commonWaits.waitForByElementToBeVisible(inputBy);
                String v = input.getAttribute("value");
                return v != null ? v : "";
            }
            // 3) Rich text Description (Froala)
            By froalaBy = By.xpath(String.format(XPATH_CONTENTEDITABLE_AFTER_LABEL, quoted));
            if (isElementPresent(froalaBy)) {
                WebElement ed = commonWaits.waitForByElementToBeVisible(froalaBy);
                return ed.getText().trim();
            }
            // 4) adapt-rx-textarea
            By taBy = By.xpath(String.format(XPATH_ADAPT_RX_TEXTAREA_CONTAINS_LABEL, quoted));
            if (isElementPresent(taBy)) {
                WebElement ta = commonWaits.waitForByElementToBeVisible(taBy);
                String v = ta.getAttribute("value");
                return v != null ? v : ta.getText();
            }
        } finally {
            switchtoDefaultFrame();
        }

        throw new AssertionError("Unable to read input value for label: " + labelText);
    }

    public String getChangeIdFromUrlOrPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Pattern chgPattern = Pattern.compile("(CHG\\d+)");
        Pattern casePattern = Pattern.compile("(CASE-\\d+)");
        Pattern displayIdFromAria = Pattern.compile("Display Id\\s*,\\s*(.+)");

        try {
            String url = getDriver().getCurrentUrl();
            if (url != null) {
                Matcher m = chgPattern.matcher(url);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (Exception ignored) {}

        try {
            switchToCreateChangeContext();
            WebElement displayIdBar = commonWaits.waitForElementVisible(rxInfoBarDisplayId, 30);
            String ariaLabel = displayIdBar.getAttribute("aria-label");
            if (ariaLabel != null) {
                Matcher ariaM = displayIdFromAria.matcher(ariaLabel);
                if (ariaM.find()) {
                    String raw = ariaM.group(1).trim();
                    Matcher chgM = chgPattern.matcher(raw);
                    if (chgM.find()) {
                        return chgM.group(1);
                    }
                    Matcher caseM = casePattern.matcher(raw);
                    if (caseM.find()) {
                        return caseM.group(1);
                    }
                }
            }
            String visible = displayIdBar.getText();
            if (visible != null) {
                String t = visible.trim();
                Matcher mChg = chgPattern.matcher(t);
                if (mChg.find()) {
                    return mChg.group(1);
                }
                Matcher mCase = casePattern.matcher(t);
                if (mCase.find()) {
                    return mCase.group(1);
                }
            }
        } catch (Exception ignored) {
        } finally {
            switchtoDefaultFrame();
        }

        throw new AssertionError("Unable to determine created Change ID from URL/page.");
    }


	public CreateChangePage clickOnCancelButtonIfPresent() {
		try {
			if(isElementPresent(By.xpath(String.format(CancelButton)))){
				commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CancelButton))));
			}
			return this;
		} catch (Exception ignored) {
			return this;
		}
	}

	public CreateChangePage waitFor10SecondsToChangeFreeze() {
		commonWaits.waitForSometime(10000);
		return this;
	}

}
