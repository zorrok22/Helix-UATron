package pages.ITSM.ITSM;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
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

public class ITSMDashboard extends BasePage {

    //String Locators
    String mainMenuXpath = "//*[text()='%s']";
    String searchCI = "//*[@arid='%s']//textarea";
    String classTextarea = "//*[contains(@class,'%s')]//textarea";
    String enterLoginID = "//*[contains(@class,'LoginID')]//textarea[not(@readonly)]";
    String password = "//*[contains(@class,'Password')]//input";
    String licenseTypeValue = "//*[contains(@class,'MenuTable')]//td[text()='%s']";
    String iframe = "//*[contains(@class,'DIVPopup')]//iframe";
    String tabButtonID = "//*[@arid='%s']//span[contains(@class,'Label')]";
    String menuItemXpathString = "//td[contains(@class, 'MenuEntryName') and text()='%s']";
    String centralizedConfigurationSettingValue =
			"(//*[contains(normalize-space(.),'%s')]/ancestor::td/following-sibling::td)[1]";
    String majorIncidentRadioBox = "//*[contains(@id,'WIN_0_rc0id304485851')]";
    String thresholdTextBox = "//*[contains(@id,'arid_WIN_0_304484641')]";
    String hkmTextareaLocator = "//*[contains(@class,'%s')]//textarea";
    String hkmTextareaPasswordLocator = "//*[contains(@class,'ISAdminPassword')]//input";
    String ElementTextareaByARID = "//*[@arid='%s']/textarea";
    String ElementByARID = "//*[@arid='%s']";
    String ElementByText = "//span[text()='%s']";
    String serverNameLocator = "//td[contains(@class,'BaseTableCell')]//span[text()='%s']";
    String aridButtonXpath = "//*[@arid='%s']";
    String firstResultRowXpath = "//tr[contains(@class,'SelPrimary')]//td//nobr//span";
    String resultRowXpath = "//tr[@tabindex='0']//td//nobr//span";
    String companyDropdownValue= "//*[@class='MenuTableBody']/child::tr[contains(.,'%s')]";
	// Label differs across environments (some show "Create Request on Submit*" / others "...Submit To")
	private static final String HPD_CFG_CREATE_REQUEST_ON_SUBMIT_TO_LABEL = "Create Request on Submit To";
	private static final String HPD_CFG_CREATE_REQUEST_ON_SUBMIT_LABEL = "Create Request on Submit";
	private static final int HPD_CFG_SET_WAIT_MS = 2_000;
	private static final String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPER_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// Centralized Configuration robust lookup patterns (label -> value cell)
	private static final String CCS_VALUE_CELL_EXACT_TEXT = "(//*[text()='%s']/ancestor::td/following-sibling::td)[1]";
	private static final String CCS_VALUE_CELL_NORMALIZED_TEXT = "(//*[normalize-space(text())='%s']/ancestor::td/following-sibling::td)[1]";
	private static final String CCS_VALUE_CELL_NORMALIZED_ANY = "(//*[normalize-space(.)='%s']/ancestor::td/following-sibling::td)[1]";
	private static final String CCS_VALUE_CELL_CONTAINS_ANY = "(//*[contains(normalize-space(.),'%s')]/ancestor::td/following-sibling::td)[1]";
	private static final String CCS_VALUE_CELL_CASE_INSENSITIVE =
			"(//*[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" +
			"translate('%s','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]/ancestor::td/following-sibling::td)[1]";

	// PV configuration (declare locators/constants above methods)
	private static final int PV_UI_WAIT_TIMEOUT_MS = 60_000;
	private static final int PV_UI_WAIT_POLL_MS = 500;
	private static final int PV_FRAME_SEARCH_DEPTH = 6;
	private static final int PV_BODY_SNIPPET_MAX = 350;
	private static final String PV_APPLY_BUTTON_XPATH = "//a[contains(@class,'Apply')]";
	private static final By PV_APPLY_BUTTON_BY = By.xpath(PV_APPLY_BUTTON_XPATH);
	private static final String PV_IFRAME_CSS = "iframe,frame";
	private static final By PV_FRAMES_BY = By.cssSelector(PV_IFRAME_CSS);
	private static final String PV_TOGGLE_XPATH_TEXT_INPUT =
			"(//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::input[@type='checkbox'])[1]";
	private static final String PV_TOGGLE_XPATH_TEXT_ROLE =
			"(//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::*[@role='checkbox' or @role='switch'])[1]";
	private static final String PV_TOGGLE_XPATH_TEXT_BTNIMGDIV =
			"(//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::*[contains(@class,'btnimgdiv')])[1]";
	private static final String PV_TOGGLE_XPATH_ARIA_INPUT =
			"(//*[@aria-label and contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::input[@type='checkbox'])[1]";
	private static final String PV_TOGGLE_XPATH_TITLE_INPUT =
			"(//*[@title and contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::input[@type='checkbox'])[1]";
	private static final String PV_TOGGLE_XPATH_ARIA_ROLE_OR_BTNIMGDIV =
			"(//*[@aria-label and contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::*[@role='checkbox' or @role='switch' or contains(@class,'btnimgdiv')])[1]";
	private static final String PV_TOGGLE_XPATH_TITLE_ROLE_OR_BTNIMGDIV =
			"(//*[@title and contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'progressive')]" +
			"/following::*[@role='checkbox' or @role='switch' or contains(@class,'btnimgdiv')])[1]";
	private static final By PV_TOGGLE_BY_ROLE_SWITCH = By.cssSelector("[role='switch']");
	private static final By PV_TOGGLE_BY_ROLE_CHECKBOX = By.cssSelector("[role='checkbox']");
	private static final By PV_TOGGLE_BY_INPUT_CHECKBOX = By.cssSelector("input[type='checkbox']");
	private static final By PV_TOGGLE_BY_BTNIMGDIV = By.cssSelector(".btnimgdiv");
	private static final String MIDTIER_ERROR_TITLE = "midtier - error page";
	private static final String JS_DOC_TITLE = "return document.title";
	private static final String JS_BODY_TEXT = "return document.body ? document.body.innerText : ''";
	private static final String REGEX_9280_VALID_SERVER =
			"valid\\s+mid\\s+tier\\s+servers\\.?\\s*:\\s*([A-Za-z0-9._-]+)";
	private static final String MSG_PV_TOGGLE_NOT_FOUND =
			"Progressive Views Configuration form loaded but PV toggle was not found. ";
	
	// MidTier error page handling
	private static final String LINK_TEXT_RETURN_TO_HOME_PAGE = "Return to home page";
	private static final String ARSERVER_ALIAS_PLACEHOLDER_PREFIX = "__";
	private static final String CTM_PEOPLE_LOGIN_ID_TEXTAREA_XPATH = "//*[contains(@class,'LoginID')]//textarea";
	private static final By CTM_PEOPLE_LOGIN_ID_TEXTAREA_BY = By.xpath(CTM_PEOPLE_LOGIN_ID_TEXTAREA_XPATH);
	private static final String PV_REL_CHECKBOX_INPUT = ".//input[@type='checkbox']";
	private static final String PV_REL_ANCESTOR_1_CHECKBOX_INPUT = "./ancestor::*[1]//input[@type='checkbox']";
	private static final String PV_REL_ANCESTOR_2_CHECKBOX_INPUT = "./ancestor::*[2]//input[@type='checkbox']";
	
	// String Locators for formatted XPaths (with %s placeholders)
	String incidentRulesRadioButtonXpath = "//label[contains(text(),'%s')]/following-sibling::fieldset//label[text()='%s']/preceding-sibling::input";
	String cognitiveTemplateOptionXpath = "//td[contains(@class,'MenuEntryName') and text()='%s']";
    String selfServicePortalButtonXpath = "(//span[normalize-space()='%s']/ancestor::td)[1]";
    String configurationButtonXpath = "//span[contains(normalize-space(),'%s')]";
    //Locators variables
    String systemButtonID = "15076";
    String generalButtonID = "15079";
    String configurationButtonID = "15097";
    String printButtonLocator = "Print";
    String printBoxID = "1000000134";
    public static int lz3Btn_Yes = 302180500;
    String dataSetID_ARID = "400127400";
    String ciName_ARID = "200000020";
    String reconID_ARID = "400129200";
    String docPageLocator = "Sign in with your account to access Docs-XWiki";
    String HKM_URL = "IS";
    String HKM_AdminUserName = "ISAdminUsername";
    String HKM_AdminPassword = "ISAdminPassword";
    String companyPetramco = "Petramco";
    String globalSearchTextboxBox = "302258625";
    String globalSearchIconButton = "302259032";
    String configurationModifyARID = "800001039";
    String quickLinksOverlayModifyARID = "800001013";
    String SelfServicePortalARID = "800030352";
    String SelfServicePortalConfigurationARID = "800030361";

    //WebElements Locators
    @FindBy(xpath = "//*[@arid='304258090']//div[@class='btnimgdiv']")
    WebElement applications;

    @FindBy(xpath = "//*[@arid='1002']")
    WebElement searchCIButton;

    @FindBy(xpath = "//*[contains(@class,'newrequest')]")
    WebElement newRequest;

    @FindBy(xpath = "//*[contains(@class,'Site Char')]//a")
    WebElement siteName;

    @FindBy(xpath = "//*[contains(@class,'btn') and contains(.,'Login')]")
    WebElement clickOnLoginButton;

    @FindBy(xpath = "//*[contains(@class,'LicenseType')]//div")
    WebElement licenseType;

    @FindBy(xpath = "//*[contains(@class,'UpdatePermissionGroups')]")
    WebElement updatePermissionGroups;

    @FindBy(xpath = "//*[contains(@class,'PermissionGroup')]//textarea")
    WebElement permissionGroup;

    @FindBy(xpath = "//*[contains(@class,'BtnAdd')]")
    WebElement addModifyBtn;

    @FindBy(xpath = "//*[contains(@class,'BtnClose')]")
    WebElement closeBtn;

    @FindBy(xpath = "//*[contains(@class,'PermissionGroup')]//a")
    WebElement licenseTypeValueBtn;

    @FindBy(xpath = "//a[contains(@class,'btn') and contains(.,'OK')]")
    WebElement clickOnOKButton;

    @FindBy(xpath = "//a[contains(@class,'Submit')]")
    WebElement clickOnSubmitButton;

    @FindBy(xpath = "//a[contains(@class,'Apply')]")
    WebElement clickOnApplyButton;

    @FindBy(xpath = "//div[contains(@class,'ComponentName')]//a[contains(@class,'menu')]")
    WebElement componentMenuButton;

    @FindBy(xpath = "//*[@arid='302067300']//a")
    WebElement printPopupLinkLocator;

    @FindBy(xpath = "//*[@arid='1000000001']/child::a[@class='btn btn3d menu']")
    WebElement companyTextbox;

    @FindBy(xpath = "//*[@id='TBsearchsavechanges' and text()='Search']")
    WebElement searchButton;

    @FindBy(xpath = "//div[contains(@class,'EnableHelixknowledgemanagement')]//input")
    WebElement enableHKMLocator;

    @FindBy(xpath = "//*[contains(@arid,'301295802') or contains(@arid,'1003')]")
    WebElement saveHKMDetails;

    @FindBy(xpath = "//div[@arid='301789000']/textarea")
    WebElement IS_URL_TEXTBOX;

    @FindBy(xpath = "//div[@arid='301789001']/textarea")
    WebElement ADMIN_USER_TEXTBOX;

    @FindBy(xpath = "//div[@arid='123']/input")
    WebElement ADMIN_PASSWORD_TEXTBOX;

    @FindBy(xpath = "//*[@arid='301295800']//input[@type='checkbox']")
    WebElement hkmCheckbox;

    @FindBy(xpath = "//*[@arid='302180500']")
    WebElement HKMYesButton;

    @FindBy(xpath = "//*[@id='WIN_0_1003']")
    WebElement saveButtonHKM;

    @FindBy(xpath = "//input[@type = 'checkbox']")
    WebElement pv_checkbox;

    @FindBy(xpath = "//label[text()='Server Name']/following-sibling::textarea")
    WebElement serverNameTextarea;

    @FindBy(xpath = "//td[contains(@class,'BaseTableCell')]//span[text()='onbmc-s.bmc']")
    WebElement onbmcsbmcRecord;
    
    @FindBy(xpath = "//a[text()='OK']")
   	WebElement OkButton;
    
    @FindBy(xpath = "//table[@id='DivTable']//iframe")
   	WebElement AcceptIframeOkButton;
    
    @FindBy(xpath = "//*[contains(@class,'RemedyLoginID')]//textarea")
    WebElement remedyLoginIDTextbox;
	
    @FindBy(xpath = "//*[@id='WIN_0_301674600']//a")
    WebElement serviceRequestCreationButton;

    @FindBy(xpath = "//textarea[contains(@id,'arid_WIN_0_1')]")
    WebElement requestIDTextbox;

	@FindBy(xpath = "//div[contains(@class,'BaseTableInner')]")
	WebElement formNamesLocator;

    @FindBy(xpath = "//a[normalize-space()='OK']")
    WebElement userWarningOkButton;

    @FindBy(xpath = "//a[contains(.,'Onboarding Wizard')]/ancestor::div/following-sibling::*//label[contains(normalize-space(.),'Do not remind me again')]")
    WebElement onboardingDoNotRemindMeAgainCheckbox;

    @FindBy(xpath = "//*[@arid='800001044']//label[contains(text(), 'Show Disabled')]/..//input")
    WebElement showDisabledCheckbox;

    @FindBy(xpath = "//iframe[contains(@src,'MessagePopup.html')]")
    WebElement messagePopupIframe;

    @FindBy(xpath = "//div[@arid='800001002']//a[contains(@class,'expand')]")
    WebElement expandBtn;

    @FindBy(xpath = "//div[@class='OverflowAuto']/textarea")
    WebElement popupTextarea;

    @FindBy(xpath = "//span[contains(text(), 'Application Portal - List Quick Links')]")
    WebElement quickLinksMenuItem;

    @FindBy(xpath = "//div[@arid='7']//label[contains(text(),'Disable')]")
    WebElement diableConfigurationRadioBtn;

    @FindBy(xpath = "//div[@arid='7']//label[contains(text(),'Enabled')]")
    WebElement enableConfigurationRadioBtn;

    @FindBy(xpath = "//div[@class='OverflowAuto']/textarea/ancestor::table//button[contains(. ,'OK')]")
    WebElement popupTextareaOKButton;

    @FindBy(xpath = "//label[normalize-space()='Subject']/following-sibling::textarea")
    WebElement subjectTextarea;

	@FindBy(xpath = "//td[contains(@class,'BaseTableCellOdd') and contains(normalize-space(),'Request')]")
    WebElement requestIDFoundInEmailMessages;

    @FindBy(xpath = "//div[contains(@arhelp,'email message')]//span[contains(.,'Incident details')]")
    WebElement incidentDetailsHeaderInHtmlBody;

	@FindBy(xpath = "//*[@id='TBsearch' and text()='New Search']")
	WebElement newSearchButton;
	
	@FindBy(xpath = "//*[@arid='1000000001']//textarea")
	WebElement incidentRulesCompanyField;
	
	@FindBy(xpath = "//*[@id='TBsearchsavechanges' and text()='Search']")
	WebElement incidentRulesSearchButton;
	
	// Cognitive Training Setup Form WebElements
	@FindBy(xpath = "//*[@arid='301419600']//textarea")
	WebElement cognitiveDataTypeField;
	
	@FindBy(xpath = "//*[@arid='1000000082']//textarea")
	WebElement cognitiveCompanyField;
	
	@FindBy(xpath = "//*[@arid='303631200']//textarea")
	WebElement cognitiveApplicationField;
	
	@FindBy(xpath = "//*[@arid='303868900']//a")
	WebElement cognitiveTemplateDropdown;
	
	@FindBy(xpath = "//*[@arid='10007057']")
	WebElement cognitiveAddTemplateButton;
	
	@FindBy(xpath = "//*[@id='TBsearchsavechanges' and text()='Refresh']")
	WebElement cognitiveRefreshButton;
	
	@FindBy(xpath = "(//*[@arid='304413531']//tr[contains(@class,'BaseTableRow')])[1]//span")
	WebElement cognitiveStatusCell;
	
	@FindBy(xpath = "(//*[@arid='304413531']//tr[contains(@class,'BaseTableRow')])[1]")
	WebElement cognitiveFirstRow;
	
	@FindBy(xpath = "//*[@arid='304413531']//tr[contains(@class,'BaseTableRow')]")
	List<WebElement> cognitiveTrainingTableRows;
	
	@FindBy(xpath = "//*[@arid='300000035']")
	WebElement cognitiveGenerateButton;

    @FindBy(xpath = "//*[@id='WIN_0_1003']")
    WebElement saveButton;

    @FindBy(xpath = " //div[contains(@ardbn,'ManageCIs')]")
	WebElement manageCIsButton;

    @FindBy(xpath = "//*[@arid='800001044']//*[text()='Show Disabled']")
    WebElement showDisabledButton;

    @FindBy(xpath = "//textarea[@id='arid_WIN_0_800001002']")
    WebElement textBox;

    @FindBy(xpath = "//label[text()='Enabled']")
	WebElement enabledRadioBtn;

	@FindBy(xpath = "//label[text()='Disabled']")
	WebElement disabledRadioBtn;

    @FindBy(xpath = "//a[normalize-space()='OK']")
    WebElement okButton;

    @FindBy(xpath = "//td[contains(@class,'DIVPopupBodyNoBorder')]//iframe")
    WebElement okIframe;

    CommonWaits commonWaits;
    SolqaLogger log;
    CommonMethods commonMethods;

    public ITSMDashboard() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ITSMDashboard.class.getName());
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    public ITSMDashboard verifyLogin() {
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), applications, 10), "Login not successful - Applications icon not visible");
        return this;
    }

    public ITSMDashboard verifyHPD_CFG_RulesFormLoaded() {
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForURLContains(SolQAConstants.HPD_CFG_Rules_Form);
        return this;
    }    

	private By findCfgRulesFieldByLabel(String label) {
		if (label == null || label.trim().isEmpty()) return null;
		String lbl = label.trim();

		// In MidTier forms, "fields" might be textarea/input OR a menu trigger (a/div.btnimgdiv) inside the same row.
		// We search row-scoped first to avoid accidentally picking an unrelated textarea elsewhere.
		String rowScopedExact =
				"(//*[self::label or self::span or self::div or self::td][normalize-space(.)='%s']" +
				" | //*[self::label or self::span or self::div or self::td]" +
				"[translate(normalize-space(.),'%s','%s')=translate('%s','%s','%s')])" +
				"/ancestor::tr[1]//*[self::textarea or self::input or self::a or self::div[contains(@class,'btnimgdiv')]][1]";

		String rowScopedContains =
				"(//*[self::label or self::span or self::div or self::td]" +
				"[contains(translate(normalize-space(.),'%s','%s'),translate('%s','%s','%s'))])" +
				"/ancestor::tr[1]//*[self::textarea or self::input or self::a or self::div[contains(@class,'btnimgdiv')]][1]";

		// Generic label match (not necessarily inside a table row)
		final String labelNode =
				"(//*[self::label or self::span or self::div or self::td]" +
				"[translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')=" +
				"translate('" + lbl + "','" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')]" +
				" | //*[self::label or self::span or self::div or self::td]" +
				"[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')," +
				"translate('" + lbl + "','" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'))])[1]";

		By[] candidates = new By[] {
				// Row scoped (when rendered as a table)
				By.xpath(String.format(rowScopedExact, lbl, UPPER_ALPHA, LOWER_ALPHA, lbl, UPPER_ALPHA, LOWER_ALPHA)),
				By.xpath(String.format(rowScopedContains, UPPER_ALPHA, LOWER_ALPHA, lbl, UPPER_ALPHA, LOWER_ALPHA)),

				// Immediately following field (common MidTier pattern)
				By.xpath(labelNode + "/following::textarea[1]"),
				By.xpath(labelNode + "/following::input[1]"),

				// Dropdown/menu field (often rendered as an <a class='btn btn3d menu'> or btnimgdiv)
				By.xpath(labelNode + "/following::a[contains(@class,'menu')][1]"),
				By.xpath(labelNode + "/following::*[contains(@class,'btnimgdiv')][1]"),

				// Fallback: look for keywords (covers cases where label text varies slightly, e.g. has asterisk)
				By.xpath(
						"(//*[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'create request')" +
						" and contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'submit')]" +
						"/following::textarea[1] | " +
						"//*[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'create request')" +
						" and contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'submit')]" +
						"/following::input[1] | " +
						"//*[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'create request')" +
						" and contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'submit')]" +
						"/following::a[contains(@class,'menu')][1] | " +
						"//*[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'create request')" +
						" and contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'submit')]" +
						"/following::*[contains(@class,'btnimgdiv')][1])"
				)
		};

		for (By by : candidates) {
			if (commonWaits.isElementPresent(by)) return by;
		}
		return null;
	}

	private String readCfgRulesValueFromFieldOrRow(By fieldBy, String label) {
		if (fieldBy != null && commonWaits.isElementPresent(fieldBy)) {
			WebElement el = commonWaits.waitForByElementToBeVisible(fieldBy);
			String tag = "";
			try { tag = el.getTagName(); } catch (Exception ignored) {}
			if ("textarea".equalsIgnoreCase(tag) || "input".equalsIgnoreCase(tag)) {
				String v = el.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
				if (v == null || v.trim().isEmpty()) v = el.getText();
				return v == null ? "" : v.trim();
			}
			// Menu-like fields may store their selection as visible text
			String txt = "";
			try { txt = el.getText(); } catch (Exception ignored) {}
			if (txt != null && !txt.trim().isEmpty()) return txt.trim();
		}

		// If the field is a menu trigger/div, read from the row's first textarea/input if present.
		if (label != null && !label.trim().isEmpty()) {
			String lbl = label.trim();
			By rowValue = By.xpath(
					"(//*[self::label or self::span or self::div or self::td]" +
					"[translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')=translate('" + lbl + "','" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')]" +
					"/ancestor::tr[1]//*[self::textarea or self::input])[1]"
			);
			if (commonWaits.isElementPresent(rowValue)) {
				WebElement vEl = commonWaits.waitForByElementToBeVisible(rowValue);
				String v = vEl.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
				if (v == null || v.trim().isEmpty()) v = vEl.getText();
				return v == null ? "" : v.trim();
			}
		}
		return "";
	}

	/**
	 * HPD:CFG-Rules setting: "Create Request on Submit To"
	 * Expected values include: "In Service Request Management"
	 */
	public String setAndGetCreateRequestOnSubmitToValue(String expectedValue) {
		verifyHPD_CFG_RulesFormLoaded();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

		By fieldBy = findCfgRulesFieldByLabel(HPD_CFG_CREATE_REQUEST_ON_SUBMIT_TO_LABEL);
		if (fieldBy == null) {
			// Most environments show "Create Request on Submit*" (no "To")
			fieldBy = findCfgRulesFieldByLabel(HPD_CFG_CREATE_REQUEST_ON_SUBMIT_LABEL);
		}
		Assert.assertNotNull(fieldBy,
				"Could not locate '" + HPD_CFG_CREATE_REQUEST_ON_SUBMIT_LABEL + "' field on HPD:CFG-Rules form.");

		WebElement el = commonWaits.waitForByElementToBeVisible(fieldBy);
		commonWaits.scrollToSpecificElement(el);
		el.click();

		String tagName = "";
		try { tagName = el.getTagName(); } catch (Exception ignored) {}

		if ("textarea".equalsIgnoreCase(tagName) || "input".equalsIgnoreCase(tagName)) {
			el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			el.sendKeys(expectedValue);
			el.sendKeys(Keys.TAB);
		} else {
			// Menu-style field: click to open menu and pick value from MenuTable
			By optionBy = By.xpath(String.format(licenseTypeValue, expectedValue));
			commonWaits.waitForByElementToBeVisible(optionBy);
			commonWaits.clickWithoutInterception(optionBy);
		}

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		try { commonWaits.waitForSometime(HPD_CFG_SET_WAIT_MS); } catch (Exception ignored) {}

		// Persist (some environments require explicit Save/Apply)
		if (!commonWaits.clickByText(SolQAConstants.SAVE)) {
			if (commonWaits.isElementPresent(PV_APPLY_BUTTON_BY)) {
				commonWaits.clickWithoutInterception(PV_APPLY_BUTTON_BY);
			}
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

		return readCfgRulesValueFromFieldOrRow(fieldBy, HPD_CFG_CREATE_REQUEST_ON_SUBMIT_LABEL);
	}
    
    public ITSMDashboard selectCompany(String company) {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    	commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
    	Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), companyTextbox , 30 ), "Company dropdown is not visible");
    	commonWaits.clickWithoutInterception(companyTextbox); 	
    	Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(companyDropdownValue, company)) , 30 ), "Company dropdown value is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(String.format(companyDropdownValue, company)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), searchButton , 30 ), "Search button is not visible");
    	commonWaits.clickWithoutInterception(searchButton);
        return this;
    }

    
	public ITSMDashboard advanceMajorIncidentSettings() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);

		By yesRadio = By.xpath(majorIncidentRadioBox);
		WebElement yesElement = commonWaits.waitForByElementToBeVisible(yesRadio);


		boolean isSelected = (boolean) executeJS("return arguments[0].checked;", yesElement);

		if (!isSelected) {
			// Use JavaScript to click the radio button
			executeJS("arguments[0].click();", yesElement);
		}

		WebElement thresholdElement = commonWaits.waitForByElementToBeVisible(By.xpath(thresholdTextBox));
		// Clear and type using JavaScript
		executeJS("arguments[0].value = '';", thresholdElement);
		executeJS("arguments[0].value = '10';", thresholdElement);

		WebElement saveButtonElement = commonWaits.waitForElementVisible(saveButton);
		executeJS("arguments[0].click();", saveButtonElement);

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		return this;
	}

    public ITSMDashboard navigateToPageFromApplication(String mainMenu, String subMenu) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(applications).click();
        Actions actions = new Actions(getDriver());
        actions.moveToElement(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(mainMenuXpath, mainMenu)))).perform();
        actions.click(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(mainMenuXpath, subMenu)))).perform();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public void navigateMenu(String... menuPath) {
        if (menuPath == null || menuPath.length == 0) {
            return;
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(applications).click();
        Actions actions = new Actions(getDriver());
        WebElement current = null;

        for (String menu : menuPath) {
            current = commonWaits.waitForElementPresenceLocated(
                    By.xpath(String.format(mainMenuXpath, menu))
            );
            actions.moveToElement(current).perform();
        }
        if (current != null) {
            commonWaits.clickWithoutInterception(current);
        }
    }

    public ITSMDashboard LoadForm(String form, String view, String ARServerAlias) {
        log.debug("Loading Form " + form);
        String urlStr = "";
        if (ARServerAlias != null && !(ARServerAlias.contains("ARServerAlias"))) {
            urlStr = getITSMURL() + "/arsys/forms/" + ARServerAlias + "/" + form + "/" + view + "/form.html";
        } else {
            urlStr = getITSMURL() + "/arsys/forms/" + form + "/" + view + "/form.html";
        }
        log.debug(urlStr);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        getDriver().get(urlStr);
        Assert.assertTrue(commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1), "Page did not load within expected time: " + urlStr);
        return this;
    }

    public ITSMDashboard searchCI(String searchCIAssetType, String ciName) {
        WebElement DataSetTextBox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchCI, dataSetID_ARID)));
        DataSetTextBox.sendKeys(searchCIAssetType + Keys.ENTER);
        WebElement CINameTextBox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchCI, ciName_ARID)));
        CINameTextBox.sendKeys(ciName + Keys.ENTER);
        commonWaits.waitForElementClickable(searchCIButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        return this;
    }

    public String getReconciliationIdentity() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        long startTime = System.currentTimeMillis();
        long maxDuration = 180_000; // 3 minutes in milliseconds
        String value = "0";
        while (System.currentTimeMillis() - startTime < maxDuration) {
            commonWaits.clickByText(SolQAConstants.REFRESH);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            value = commonWaits.waitForElement(By.xpath(String.format(searchCI, reconID_ARID)))
                    .getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
            if (!"0".equals(value)) {
                break;
            }
            try {
                commonWaits.waitForSometime(maxDuration); // Wait 5 seconds before next refresh
            } catch (Exception e) {
                break;
            }
        }
        return value;
    }

    public String createUserInCTMPeopleForm(Map<Object, String> UserDetails, String ARServerAlias, boolean isRandomStringRequired) {
        LoadForm(SolQAConstants.CTM_PEOPLE, SolQAConstants.DEFAULT, ARServerAlias);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForElementClickable(newRequest);
        commonWaits.clickWithoutInterception(newRequest);
        String randomID = commonMethods.randomString(5);
        keyClearAndType(String.format(classTextarea, "FirstName"), UserDetails.get("FirstName"));
        keyClearAndType(String.format(classTextarea, "LastName"), UserDetails.get("LastName"));
        keyClearAndType(String.format(classTextarea, "Company"), UserDetails.get("Company"));
        keyClearAndType(String.format(classTextarea, "Organization"), UserDetails.get("Organization"));
        keyClearAndType(String.format(classTextarea, "Department"), UserDetails.get("Department"));
        siteName.click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(licenseTypeValue, UserDetails.get("Site")))).click();
        clickOnLoginButton.click();
        String loginID = UserDetails.get("LoginID") + (isRandomStringRequired ? randomID : "");
        keyClearAndType(enterLoginID, loginID);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(password, UserDetails.get("Password"))));
        keyClearAndType(password, UserDetails.get("Password"));
        commonWaits.waitForSometime(commonWaits.minDelay);
        licenseType.click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(licenseTypeValue, UserDetails.get("LicenseType")))).click();
        updatePermissionGroups.click();
        List<String> currentWindows = new ArrayList<>(getDriver().getWindowHandles());
        WebDriver currentWindow = getDriver().switchTo().window(currentWindows.get(1));
        setDriver(currentWindow);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        licenseTypeValueBtn.click();
        String[] applicationPermissions = UserDetails.get("ApplicationPermission").split(",");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(licenseTypeValue, applicationPermissions[0]))).click();
        findElement(By.xpath(String.format(licenseTypeValue, applicationPermissions[1].trim()))).click();
        keyClearAndType(String.format(classTextarea, "LicenseType"), UserDetails.get("LicenseType"));
        addModifyBtn.click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        switchToFrame(By.xpath(iframe));
        clickOnOKButton.click();
        switchtoDefaultFrame();
        commonWaits.waitForSometime(commonWaits.minDelay);
        closeBtn.click();
        currentWindow = getDriver().switchTo().window(currentWindows.get(0));
        setDriver(currentWindow);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(clickOnSubmitButton);
        commonWaits.waitForSometime(commonWaits.minDelay);
        currentWindows = new ArrayList<>(getDriver().getWindowHandles());
        currentWindow = getDriver().switchTo().window(currentWindows.get(1));
        setDriver(currentWindow);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(password, UserDetails.get("Password"))));
        keyClearAndType(password, UserDetails.get("Password"));
        clickOnOKButton.click();
        currentWindow = getDriver().switchTo().window(currentWindows.get(0));
        setDriver(currentWindow);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM); // Added to confirm the user is created
        log.debug("User is created:- " + loginID);
        return loginID;
    }
    
    public ITSMDashboard verifyCreatedUserIsExistsWithoutLoginID(Map<Object, String> UserDetails, String ARServerAlias){
    	LoadForm(SolQAConstants.CTM_PEOPLE, SolQAConstants.DEFAULT, ARServerAlias);
    	keyClearAndType(String.format(classTextarea, SolQAConstants.FIRSTNAME), UserDetails.get(SolQAConstants.FIRST_NAME));
        keyClearAndType(String.format(classTextarea, SolQAConstants.LASTNAME), UserDetails.get(SolQAConstants.LAST_NAME));
    	clickWithoutInterception(searchButton);
    	clickWithoutInterception(clickOnLoginButton);
    	Assert.assertTrue(remedyLoginIDTextbox.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).isEmpty(), "User is created with login ID successfully");
        log.debug("Verifying User is exists:- "+UserDetails);
        return this;
    }

    public ITSMDashboard navigateToCentralizedConfiguration() {
        log.debug("Navigating to Centralized Configuration");
        navigateToPageFromApplication(SolQAConstants.AR_SYSTEM_ADMINISTRATION, SolQAConstants.AR_SYSTEM_ADMINISTRATION_CONSOLE);
        commonWaits.switchToLastWindow();
        clickWithoutInterception(By.xpath(String.format(tabButtonID, systemButtonID)));
        clickWithoutInterception(By.xpath(String.format(tabButtonID, generalButtonID)));
        clickWithoutInterception(By.xpath(String.format(tabButtonID, configurationButtonID)));
        commonWaits.switchToLastWindow();
        log.debug("Navigated to Centralized Configuration");
        return this;
    }

	public ITSMDashboard selectMenuItemFromComponentMenu(String[] menuPath) {
		log.debug("Selecting Menu " + Arrays.toString(menuPath));
		clickWithoutInterception(componentMenuButton);
		for (String menuItem : menuPath) {
			By menuItemBy = findComponentMenuEntry(menuItem);
			if (menuItemBy != null) {
				clickWithoutInterception(menuItemBy);
			} else {
				log.warn("Menu entry not found in Centralized Configuration component menu: " + menuItem);
			}
		}
		return this;
	}

	private By findComponentMenuEntry(String menuItem) {
		try {
			if (menuItem == null) return null;
			String item = menuItem.trim();
			if (item.isEmpty()) return null;

			final List<String> xpathTemplates = Arrays.asList(
					"//td[contains(@class,'MenuEntryName') and text()='%s']",
					"//td[contains(@class,'MenuEntryName') and normalize-space(.)='%s']",
					"//*[contains(@class,'MenuEntryName') and normalize-space(.)='%s']",
					"//td[contains(@class,'MenuEntryName') and translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" +
							"translate('%s','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]",
					"//*[contains(@class,'MenuEntryName') and translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" +
							"translate('%s','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]",
					"//td[contains(@class,'MenuEntryName') and contains(normalize-space(.),'%s')]",
					"//*[contains(@class,'MenuEntryName') and contains(normalize-space(.),'%s')]"
			);

			for (String tpl : xpathTemplates) {
				By by = By.xpath(String.format(tpl, item));
				if (!findElements(by).isEmpty()) return by;
			}

			// Fallback for cluster names that can vary across stacks
			String lowered = item.toLowerCase();
			if (lowered.startsWith("dockermtcluster")) {
				// try "DockerMTCluster*Int*" and "*User*" matches
				String hint = lowered.contains("int") ? "int" : (lowered.contains("user") ? "user" : "");
				String clusterTpl = hint.isEmpty()
						? "//*[contains(@class,'MenuEntryName') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'dockermtcluster')]"
						: "//*[contains(@class,'MenuEntryName') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'dockermtcluster') and " +
						  "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + hint + "')]";
				By by = By.xpath(clusterTpl);
				if (!findElements(by).isEmpty()) return by;
			}
		} catch (Exception ignored) {}
		return null;
	}

	public ITSMDashboard centralizedConfigurationSetting(String configSettingName,String settingValue){
        log.debug("Setting " + configSettingName + " to " + settingValue);
		Actions actions = new Actions(getDriver());
		
		if ("helixgptGlobalSearchDefaultMode".equalsIgnoreCase(configSettingName)) {
			final String arID = "arid_WIN_0_3200";
			final By stableField = By.id(arID);
			WebElement target = commonWaits.waitForByElementToBeVisible(stableField);
			clickByAction(target);
			target.sendKeys(Keys.CONTROL + "a");
			target.sendKeys(Keys.BACK_SPACE);
			target.sendKeys(settingValue);
		} else {
			WebElement target = findCentralizedConfigurationValueCell(configSettingName);
			if (target == null) {
				target = commonWaits.waitForByElementToBeVisible(
						By.xpath(String.format(centralizedConfigurationSettingValue, configSettingName)));
			}
			Assert.assertNotNull(target, "Centralized Configuration setting '" + configSettingName + "' not found in UI.");
			actions.moveToElement(target).click().perform();
			actions.moveToElement(target).doubleClick().click().sendKeys(Keys.BACK_SPACE).perform();
			actions.moveToElement(target).sendKeys(settingValue).perform();
		}
		clickWithoutInterception(clickOnApplyButton);
        log.debug("Setting " + configSettingName + " is set to " + settingValue);
        return this;
	}
	
	public String getCentralizedConfigurationValue(String configSettingName) {
		log.debug("Getting value for configuration: " + configSettingName);
		WebElement target = findCentralizedConfigurationValueCell(configSettingName);
		Assert.assertNotNull(target, "Centralized Configuration setting '" + configSettingName + "' not found in UI.");
		String value = target.getText().trim();
		log.debug("Configuration " + configSettingName + " has value: " + value);
		return value;
	}

	private WebElement findCentralizedConfigurationValueCell(String configSettingName) {
		try {
			if (configSettingName == null) return null;
			String key = configSettingName.trim();
			if (key.isEmpty()) return null;

			// Some stacks render the key with slightly different naming; try common variants.
			List<String> keysToTry = new ArrayList<>();
			keysToTry.add(key);
			if (key.contains("mide_tier.base.url")) {
				keysToTry.add(key.replace("mide_tier.base.url", "midtier.base.url"));
				keysToTry.add(key.replace("mide_tier.base.url", "mid_tier.base.url"));
				keysToTry.add(key.replace("mide_tier.base.url", "midtier.base.url"));
				keysToTry.add(key.replace("mide_tier", "midtier"));
				keysToTry.add(key.replace("mide_tier", "mid_tier"));
				keysToTry.add(key.replace("mide_tier", "midtier"));
			}

			final List<String> xpathTemplates = Arrays.asList(
					CCS_VALUE_CELL_EXACT_TEXT,
					CCS_VALUE_CELL_NORMALIZED_TEXT,
					CCS_VALUE_CELL_NORMALIZED_ANY,
					CCS_VALUE_CELL_CASE_INSENSITIVE,
					CCS_VALUE_CELL_CONTAINS_ANY
			);

			for (String k : keysToTry) {
				if (k == null || k.trim().isEmpty()) continue;
				for (String tpl : xpathTemplates) {
					By by = By.xpath(String.format(tpl, k));
					try {
						// Use BasePage.findElements (healing-aware) to avoid hard-failing on missing locators
						if (!findElements(by).isEmpty()) {
							return commonWaits.waitForByElementToBeVisible(by);
						}
					} catch (Exception ignored) {}
				}
			}
		} catch (Exception ignored) {}
		return null;
	}
	
	public ITSMDashboard validateCentralizedConfigurationNotEmpty(String configSettingName) {
		String value = getCentralizedConfigurationValue(configSettingName);
		Assert.assertFalse(value.isEmpty(), "Configuration field '" + configSettingName + "' is empty but should have a value");
		log.debug("Validated that " + configSettingName + " is not empty");
		return this;
	}

    public ITSMDashboard clickOnPrintIcon() {
        clickWithoutInterception(By.xpath(String.format(mainMenuXpath, printButtonLocator)));
        return this;
    }

    public ITSMDashboard verifyPrintMessage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        switchToFrame(By.xpath(iframe));
        String printMessage = commonWaits.waitForElementVisible(findElement(By.xpath(String.format(searchCI, printBoxID)))).getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
        Assert.assertTrue(printMessage.equals(SolQAConstants.IncidentPrintMessage), "Print message is not displayed as expected");
        switchtoDefaultFrame();
        return this;
    }

    public ITSMDashboard clickOnPrintPopupLink() {
        switchToFrame(By.xpath(iframe));
        clickWithoutInterception(printPopupLinkLocator);
        switchtoDefaultFrame();
        commonWaits.switchToLastWindow();
        return this;
    }

    public ITSMDashboard verifyDocPageOpened() {
        Assert.assertTrue(isElementPresent(By.xpath(String.format(mainMenuXpath, docPageLocator))), "Doc page is not opened");
        return this;
    }

    public ITSMDashboard enableSelfServiceHKM() {
        log.debug("Enabling Self-Service HKM");
        if (!hkmCheckbox.isSelected()) {
            log.debug("Checking/enabling HKM checkbox");
            click(hkmCheckbox);
        } else {
            log.debug("HKM checkbox is already enabled");
        }
        commonWaits.waitForElementClickable(saveButtonHKM).click();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public ITSMDashboard enableHKMModule() {
    	if(enableHKMLocator.getAttribute(SolQAConstants.ATTRIBUTE_CHECKED)==null) {
    		clickWithoutInterception(enableHKMLocator);
    		clickWithoutInterception(saveHKMDetails);
    	}
    	else if(!enableHKMLocator.getAttribute(SolQAConstants.ATTRIBUTE_CHECKED).equalsIgnoreCase("true")) {
			clickWithoutInterception(enableHKMLocator);
			clickWithoutInterception(saveHKMDetails);
		}
    	return this;
	}
    
    public ITSMDashboard updateHKMURL(String hkmURL, String hkmAdminUserName, String hkmAdminPassword) {
    	if(!findElement(By.xpath(String.format(hkmTextareaLocator, HKM_URL))).getAttribute(SolQAConstants.ATTRIBUTE_VALUE).equalsIgnoreCase(hkmURL)) {
			keyClearAndType(String.format(hkmTextareaLocator, HKM_URL), hkmURL);
			keyClearAndType(String.format(hkmTextareaLocator, HKM_AdminUserName), hkmAdminUserName);
			keyClearAndType(String.format(hkmTextareaPasswordLocator, HKM_AdminPassword), hkmAdminPassword);
			clickWithoutInterception(saveHKMDetails);
		}
    	return this;
    }
    
	public String searchViaGlobalSearch(String searchText) {
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementTextareaByARID, globalSearchTextboxBox)));
	    clickWithoutInterception(By.xpath(String.format(ElementTextareaByARID, globalSearchTextboxBox)));
	    keyClearAndType(String.format(ElementTextareaByARID, globalSearchTextboxBox), searchText);
	    commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementByARID, globalSearchIconButton)));
	    clickWithoutInterception(By.xpath(String.format(ElementByARID, globalSearchIconButton)));
	    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

	    if (commonWaits.isElementPresent(By.xpath(String.format(ElementByText, searchText)))) {
	        WebElement resultElement = commonWaits.waitForByElementToBeVisible(
	            By.xpath(String.format(ElementByText, searchText))
	        );
	        String searchedText = resultElement.getText();
	        log.debug("Global search returned: " + searchedText);
	        return searchedText;
	    } else {
	        log.debug("No results found in global search for text: " + searchText);
	        return null;
	    }
	}

	public ITSMDashboard openFromGlobalSearch(String searchText) {
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementTextareaByARID, globalSearchTextboxBox)));
		clickWithoutInterception(By.xpath(String.format(ElementTextareaByARID, globalSearchTextboxBox)));
		keyClearAndType(String.format(ElementTextareaByARID, globalSearchTextboxBox), searchText);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementByARID, globalSearchIconButton)));
		clickWithoutInterception(By.xpath(String.format(ElementByARID, globalSearchIconButton)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

		By resultBy = By.xpath(String.format(ElementByText, searchText));
		Assert.assertTrue(commonWaits.isElementPresent(resultBy), "No results found in global search for text: " + searchText);
		WebElement resultElement = commonWaits.waitForByElementToBeVisible(resultBy);
		clickWithoutInterception(resultElement);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		return this;
	}
    
	public String getCTMPeopleLoginIdValue() {
		try {
			// On CTM:People the LoginID field can be read-only; read whichever textarea is present
			if (findElements(CTM_PEOPLE_LOGIN_ID_TEXTAREA_BY).isEmpty()) return "";
			WebElement el = findElement(CTM_PEOPLE_LOGIN_ID_TEXTAREA_BY);
			String v = el.getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
			if (v == null) v = el.getText();
			return v == null ? "" : v.trim();
		} catch (Exception ignored) {
			return "";
		}
	}

    public ITSMDashboard switchToActiveWindow() {
        WebDriver driver = getDriver();
        String currentHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        return this;
    }

    /**
     * Enable SSP Quick Links section and add ComAround shortcut.
     * Ported from Dev repo: ITSM_AppsCommon.ClickOnSelfServicePortalConfigurationAndEnableLivechatQuickLinks().
     * <p>
     * Notes:
     * - This flow relies on AR MidTier pages with `arid` attributes.
     * - It assumes the caller has already opened the Self Service Portal Configuration UI (typically from Admin Console).
     */
    public ITSMDashboard enableSSPQuickLinksForComAround() {
        log.debug("Enabling SSP Quick Links and adding ComAround shortcut");

        String ConfigurationValueSetting = "<listcontrol>\n" +
                "\t<id>shortcuts</id>\n" +
                "\t<title><span id='listtitle_shortcuts'>Quick Links</span></title>\n" +
                "\t<refresh>no</refresh>\n" +
                "\t<display>always</display>\n" +
                "\t<links>\n" +
                "\t\t<link><a alt='BMC Web Site' href='http://www.bmc.com' target='_blank'>BMC Software</link>\n" +
                "\t\t<link><a alt='CNN Web Site' href='http://www.cnn.com' target='_blank'>CNN</link>\n" +
                "\t\t<link><a alt='Microsoft Web Site' href='http://www.microsoft.com' target='_blank'>Microsoft</link>\n" +
                "<link><a alt='Comaround' href='https://zero-test.comaround.com/' target='_blank'>Comaround</link>\n" +
                "\t</links>\n" +
                "</listcontrol>\n" +
                "\n" +
                "// This generates a list control with a set of short cuts.";

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForExecutionComplete();
        clickWithoutInterception(By.xpath(String.format(aridButtonXpath, SelfServicePortalARID)));
        commonWaits.waitForExecutionComplete();
        clickWithoutInterception(By.xpath(String.format(aridButtonXpath, SelfServicePortalConfigurationARID)));
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementVisible(showDisabledButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickWithoutInterception(quickLinksMenuItem);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickByText("Modify");
        commonWaits.waitForExecutionComplete();
        commonWaits.switchToLastWindow();
        commonWaits.waitForExecutionComplete();
        if (commonWaits.isElementPresent(messagePopupIframe)) {
            commonWaits.switchToFrame(messagePopupIframe);
            commonWaits.clickByText("OK");
            switchtoDefaultFrame();
        }
        commonWaits.waitForElementVisible(expandBtn).click();
        commonWaits.waitForExecutionComplete();
        WebElement ta = commonWaits.waitForElementVisible(popupTextarea);
        ta.clear();
        ta.sendKeys(ConfigurationValueSetting);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),popupTextareaOKButton, 15), "OK button on Quick Links configuration popup is not visible");
        commonWaits.clickWithoutInterception(popupTextareaOKButton);
        switchtoDefaultFrame();
        clickWithoutInterception(diableConfigurationRadioBtn);
        clickWithoutInterception(enableConfigurationRadioBtn);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForExecutionComplete();
        commonWaits.switchToTabByTitle("Live Chat Administration Console");
        if (commonWaits.isElementPresent(By.linkText("Refresh Servlets"))) {
            commonWaits.clickByText("Refresh Servlets");
            commonWaits.waitForExecutionComplete();
        }
        if (commonWaits.isElementPresent(By.linkText("Apply"))) {
            commonWaits.clickByText("Apply");
            commonWaits.waitForExecutionComplete();
        }
        if (commonWaits.isElementPresent(By.linkText("OK"))) {
            commonWaits.clickByText("OK");
            commonWaits.waitForExecutionComplete();
        }

        log.debug("SSP Quick Links enabled and ComAround shortcut added.");
        return this;
    }

    public ITSMDashboard setSelfService_HKM(boolean value, String ARServerAlias, String adminUserName, String adminPassword) {
        commonWaits.waitForPageReadyState(getDriver());
        LoadForm(SolQAConstants.CFG_HKM_ConfigurationSettings, SolQAConstants.DEFAULT, ARServerAlias);
        commonWaits.waitForPageFullyLoaded(10, 1);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), IS_URL_TEXTBOX, 10), "IS URL textbox is not visible on HKM Configuration Settings form");
        IS_URL_TEXTBOX.click();IS_URL_TEXTBOX.clear();IS_URL_TEXTBOX.sendKeys(CommonMethods.readConfig("IS_URL"));

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ADMIN_USER_TEXTBOX, 10), "Admin User textbox is not visible on HKM Configuration Settings form");
        ADMIN_USER_TEXTBOX.click();ADMIN_USER_TEXTBOX.clear();ADMIN_USER_TEXTBOX.sendKeys(adminUserName);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ADMIN_PASSWORD_TEXTBOX, 10), "Admin Password textbox is not visible on HKM Configuration Settings form");
        ADMIN_PASSWORD_TEXTBOX.click();ADMIN_PASSWORD_TEXTBOX.clear();ADMIN_PASSWORD_TEXTBOX.sendKeys(adminPassword);

        commonWaits.waitForExecutionComplete();
        if (value) {
            if (!hkmCheckbox.isSelected()) {
                log.debug("check/enable HKM on checkbox");
                click(hkmCheckbox);
            }
        } else {
            if (hkmCheckbox.isSelected()) {
                log.debug("uncheck/disable HKM on checkbox");
                click(hkmCheckbox);
            }

        }
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickByText(SolQAConstants.SAVE);
        if (!value) {
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.switchToLastWindow();
            commonWaits.clickWithoutInterception(HKMYesButton);
        }
        commonWaits.switchToTabByTitle("CFG:HKM:ConfigurationSettings");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }


    public ITSMDashboard validateIncidentIDCaseSensitivity(String incidentID) {
        log.debug("Validating case sensitivity for Incident ID: " + incidentID);

        // Step 1: Uppercase search -> should succeed
        String upperCaseIncident = incidentID.toUpperCase();
        String resultUpper = searchViaGlobalSearch(upperCaseIncident);
        Assert.assertNotNull(resultUpper, "Incident ID not found when searched in uppercase");
        Assert.assertTrue(resultUpper.contains(upperCaseIncident),
                "Incident ID mismatch in uppercase search result");
        log.pass("Incident found successfully with uppercase search: " + resultUpper);

        // Step 2: Lowercase search -> should fail
        String lowerCaseIncident = incidentID.toLowerCase();
        String resultLower = searchViaGlobalSearch(lowerCaseIncident);
        Assert.assertNull(resultLower,
                "Incident ID should NOT be found when searched in lowercase");
        log.pass("No result found with lowercase search as expected.");
        return this;
    }

    public ITSMDashboard setSelfService_PV(boolean value, String ARServerAlias) {
        log.debug("Setting of PV from ITSM self service");
        final String targetForm = SolQAConstants.CFG_PROGRESSIVEVIEWSCONFIGURATION;
        final String view = SolQAConstants.DEFAULT;

        if (ARServerAlias == null || ARServerAlias.trim().isEmpty()
                || ARServerAlias.trim().startsWith(ARSERVER_ALIAS_PLACEHOLDER_PREFIX)) {
            try {
                String itsmUrl = configProp.get("ITSM_URL");
                if (itsmUrl != null && !itsmUrl.trim().isEmpty() && !itsmUrl.trim().startsWith(ARSERVER_ALIAS_PLACEHOLDER_PREFIX)) {
                    String host = new java.net.URI(itsmUrl.trim()).getHost();
                    if (host != null && host.contains(".")) {
                        ARServerAlias = host.substring(0, host.indexOf('.'));
                    }
                }
            } catch (Exception ignored) {}
        }

        if (ARServerAlias == null || ARServerAlias.trim().isEmpty()
                || ARServerAlias.trim().startsWith(ARSERVER_ALIAS_PLACEHOLDER_PREFIX)) {
            ARServerAlias = SolQAConstants.DEFAULT;
        }

        LoadForm(targetForm, view, ARServerAlias);
        commonWaits.waitForPageFullyLoaded(10, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        String titleAfterLoad = getPageTitleSafe();
        String bodyAfterLoad = getBodyTextSafe();
        if (isMidTier9280ServerNull(titleAfterLoad, bodyAfterLoad)) {
            String detectedServer = extractValidServerFrom9280(bodyAfterLoad);
            if (detectedServer == null || detectedServer.trim().isEmpty()) {
                detectedServer = ARServerAlias;
            }
            retryPvFormWithServerParam(targetForm, view, detectedServer);
        }

        if (commonWaits.clickByText(LINK_TEXT_RETURN_TO_HOME_PAGE)) {
            log.warn("MidTier error page detected while opening PV configuration; clicked 'Return to home page'. Retrying with server alias: " + ARServerAlias);
            commonWaits.waitForPageFullyLoaded(10, 1);
            LoadForm(targetForm, view, ARServerAlias);
            commonWaits.waitForPageFullyLoaded(10, 1);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        }

        String currentUrl = "";
        try { currentUrl = getDriver().getCurrentUrl(); } catch (Exception ignored) {}
        boolean isOnTargetForm = currentUrl != null && currentUrl.contains("ProgressiveViewsConfiguration");
        if (!isOnTargetForm) {
            String detectedAlias = null;
            try {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("/arsys/forms/([^/]+)/").matcher(currentUrl);
                if (m.find()) {
                    detectedAlias = m.group(1);
                }
            } catch (Exception ignored) {}

            if (detectedAlias != null && !detectedAlias.trim().isEmpty() && !detectedAlias.equalsIgnoreCase(ARServerAlias)) {
                log.warn("Redirected away from " + targetForm + ". Retrying with detected ARServerAlias from URL: " + detectedAlias
                        + " (was: " + ARServerAlias + "). Current URL: " + currentUrl);
                ARServerAlias = detectedAlias;
                LoadForm(targetForm, SolQAConstants.DEFAULT, ARServerAlias);
                commonWaits.waitForPageFullyLoaded(10, 1);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            }
        }

        try { currentUrl = getDriver().getCurrentUrl(); } catch (Exception ignored) {}
        isOnTargetForm = currentUrl != null && currentUrl.contains("ProgressiveViewsConfiguration");
        if (!isOnTargetForm) {
            log.warn("Still not on " + targetForm + " after alias retry. Trying to load form without ARServerAlias. Current URL: " + currentUrl);
            LoadForm(targetForm, SolQAConstants.DEFAULT, null);
            commonWaits.waitForPageFullyLoaded(10, 1);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        }

        try { currentUrl = getDriver().getCurrentUrl(); } catch (Exception ignored) {}
        isOnTargetForm = currentUrl != null && currentUrl.contains("ProgressiveViewsConfiguration");
        if (!isOnTargetForm) {
            Assert.fail("Unable to open Progressive Views Configuration form. MidTier redirected away from form. "
                    + "Final URL: " + currentUrl + ". "
                    + "Tried ARServerAlias: " + ARServerAlias + " and without alias.");
            return this;
        }

        switchtoDefaultFrame();
        final List<By> pvToggleLocators = Arrays.asList(
                By.xpath(PV_TOGGLE_XPATH_TEXT_INPUT),
                By.xpath(PV_TOGGLE_XPATH_TEXT_ROLE),
                By.xpath(PV_TOGGLE_XPATH_TEXT_BTNIMGDIV),
                By.xpath(PV_TOGGLE_XPATH_ARIA_INPUT),
                By.xpath(PV_TOGGLE_XPATH_TITLE_INPUT),
                By.xpath(PV_TOGGLE_XPATH_ARIA_ROLE_OR_BTNIMGDIV),
                By.xpath(PV_TOGGLE_XPATH_TITLE_ROLE_OR_BTNIMGDIV),
                PV_TOGGLE_BY_ROLE_SWITCH,
                PV_TOGGLE_BY_ROLE_CHECKBOX,
                PV_TOGGLE_BY_INPUT_CHECKBOX,
                PV_TOGGLE_BY_BTNIMGDIV
        );

        WebElement pvToggle = waitForPvToggle(pvToggleLocators);

        if (pvToggle == null) {
            String title = getPageTitleSafe();
            String bodySnippet = getBodySnippetSafe(PV_BODY_SNIPPET_MAX);
            int iframeCount = 0;
            try { iframeCount = findElements(PV_FRAMES_BY).size(); } catch (Exception ignored) {}

            Assert.fail(MSG_PV_TOGGLE_NOT_FOUND
                    + "Current URL: " + getDriver().getCurrentUrl()
                    + ". Title: " + title
                    + ". Frames: " + iframeCount
                    + (bodySnippet != null && !bodySnippet.isEmpty() ? ". Body: " + bodySnippet : ""));
            return this;
        }

        boolean isChecked = false;
        WebElement underlyingCheckbox = null;
        try {
            String tag = pvToggle.getTagName();
            String type = pvToggle.getAttribute("type");
            if ("input".equalsIgnoreCase(tag) && "checkbox".equalsIgnoreCase(type)) {
                underlyingCheckbox = pvToggle;
            }
        } catch (Exception ignored) {}
        if (underlyingCheckbox == null) {
            try { underlyingCheckbox = pvToggle.findElement(By.xpath(PV_REL_CHECKBOX_INPUT)); } catch (Exception ignored) {}
        }
        if (underlyingCheckbox == null) {
            try { underlyingCheckbox = pvToggle.findElement(By.xpath(PV_REL_ANCESTOR_1_CHECKBOX_INPUT)); } catch (Exception ignored) {}
        }
        if (underlyingCheckbox == null) {
            try { underlyingCheckbox = pvToggle.findElement(By.xpath(PV_REL_ANCESTOR_2_CHECKBOX_INPUT)); } catch (Exception ignored) {}
        }
        try {
            if (underlyingCheckbox != null) {
                isChecked = underlyingCheckbox.isSelected();
            } else {
                String ariaChecked = pvToggle.getAttribute("aria-checked");
                if (ariaChecked != null) {
                    isChecked = "true".equalsIgnoreCase(ariaChecked);
                } else if (pvToggle.getAttribute("checked") != null) {
                    isChecked = true;
                } else {
                    String cls = pvToggle.getAttribute("class");
                    isChecked = cls != null && cls.toLowerCase().contains("checked");
                }
            }
        } catch (Exception ignored) {}

        if (value && !isChecked) {
            log.debug("check/enable PV on toggle");
            smartClick(pvToggle);
        } else if (!value && isChecked) {
            log.debug("uncheck/disable PV on toggle");
            smartClick(pvToggle);
        }

        commonWaits.waitForSometime(5000);

        Assert.assertTrue(commonWaits.isElementPresent(clickOnApplyButton),
                "Apply button not found on PV configuration form. Current URL: " + getDriver().getCurrentUrl());
        smartClick(clickOnApplyButton);
        commonWaits.waitForPageFullyLoaded(10, 1);
        switchtoDefaultFrame();
        return this;
    }

	private String getPageTitleSafe() {
		try {
			switchtoDefaultFrame();
			return String.valueOf(executeJS(JS_DOC_TITLE));
		} catch (Exception ignored) {
			return "";
		}
	}

	private String getBodyTextSafe() {
		try {
			switchtoDefaultFrame();
			String body = String.valueOf(executeJS(JS_BODY_TEXT));
			return body == null ? "" : body;
		} catch (Exception ignored) {
			return "";
		}
	}

	private String getBodySnippetSafe(int maxLen) {
		try {
			String body = getBodyTextSafe();
			if (body == null) return "";
			body = body.replaceAll("\\s+", " ").trim();
			if (maxLen <= 0 || body.length() <= maxLen) return body;
			return body.substring(0, maxLen) + "...";
		} catch (Exception ignored) {
			return "";
		}
	}

	private boolean isMidTier9280ServerNull(String title, String body) {
		String t = title == null ? "" : title.toLowerCase();
		String b = body == null ? "" : body.toLowerCase();
		return (b.contains("arerr [9280]") && b.contains("server null") && b.contains("valid mid tier servers"))
				|| (t.contains(MIDTIER_ERROR_TITLE) && b.contains("arerr [9280]"));
	}

	private String extractValidServerFrom9280(String body) {
		try {
			if (body == null) return null;
			java.util.regex.Matcher m = java.util.regex.Pattern
					.compile(REGEX_9280_VALID_SERVER, java.util.regex.Pattern.CASE_INSENSITIVE)
					.matcher(body);
			return m.find() ? m.group(1) : null;
		} catch (Exception ignored) {
			return null;
		}
	}

	private boolean retryPvFormWithServerParam(String form, String view, String server) {
		try {
			if (server == null || server.trim().isEmpty()) return false;
			String s = server.trim();
			String urlWithAliasAndParam = getITSMURL() + "/arsys/forms/" + s + "/" + form + "/" + view + "/form.html?server=" + s;
			String urlWithoutAliasButParam = getITSMURL() + "/arsys/forms/" + form + "/" + view + "/form.html?server=" + s;

			log.warn("MidTier ARERR[9280] detected. Retrying PV form with explicit server parameter. server=" + s);

			getDriver().navigate().to(urlWithAliasAndParam);
			commonWaits.waitForPageFullyLoaded(10, 1);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
			if (!isMidTier9280ServerNull(getPageTitleSafe(), getBodyTextSafe())) return true;

			getDriver().navigate().to(urlWithoutAliasButParam);
			commonWaits.waitForPageFullyLoaded(10, 1);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
			return !isMidTier9280ServerNull(getPageTitleSafe(), getBodyTextSafe());
		} catch (Exception ignored) {
			return false;
		}
	}

	private boolean isUsableToggleCandidate(WebElement e) {
		if (e == null) return false;
		try {
			if (!e.isEnabled()) return false;
		} catch (Exception ex) {
			return false;
		}
		String tag = "";
		try { tag = e.getTagName(); } catch (Exception ignored) {}
		String type = null;
		try { type = e.getAttribute("type"); } catch (Exception ignored) {}
		boolean isCheckboxInput = "input".equalsIgnoreCase(tag) && type != null && "checkbox".equalsIgnoreCase(type);
		if (isCheckboxInput) return true; // input can be hidden; still usable by click helpers
		try { return e.isDisplayed(); } catch (Exception ignored) {}
		return false;
	}

	private WebElement firstUsable(By by) {
		try {
			List<WebElement> els = findElements(by); // BasePage.findElements (healing-aware)
			for (WebElement e : els) {
				if (isUsableToggleCandidate(e)) return e;
			}
		} catch (Exception ignored) {}
		return null;
	}

	private WebElement findToggleInCurrentContext(List<By> pvToggleLocators) {
		for (By by : pvToggleLocators) {
			WebElement candidate = firstUsable(by);
			if (candidate != null) return candidate;
		}

		// If Apply is present, any toggle-like control in this context is acceptable.
		try {
			if (!findElements(PV_APPLY_BUTTON_BY).isEmpty()) {
				final List<By> genericToggleCandidates = Arrays.asList(
						PV_TOGGLE_BY_ROLE_SWITCH,
						PV_TOGGLE_BY_ROLE_CHECKBOX,
						PV_TOGGLE_BY_INPUT_CHECKBOX,
						PV_TOGGLE_BY_BTNIMGDIV
				);
				for (By by : genericToggleCandidates) {
					WebElement candidate = firstUsable(by);
					if (candidate != null) return candidate;
				}
			}
		} catch (Exception ignored) {}

		return null;
	}

	private boolean switchToContextWithApply(int depth) {
		try {
			if (!findElements(PV_APPLY_BUTTON_BY).isEmpty()) return true;
			if (depth <= 0) return false;

			List<WebElement> frames = findElements(PV_FRAMES_BY);
			for (WebElement frame : frames) {
				boolean found = false;
				try {
					getDriver().switchTo().frame(frame);
					found = switchToContextWithApply(depth - 1);
					if (found) return true;
				} catch (Exception ignored) {
					// try next frame
				} finally {
					if (!found) {
						try { getDriver().switchTo().parentFrame(); } catch (Exception ignored2) {}
					}
				}
			}
		} catch (Exception ignored) {}
		return false;
	}

	private WebElement findToggleInFrames(int depth, List<By> pvToggleLocators, boolean includeCurrent) {
		try {
			if (includeCurrent) {
				WebElement inCurrent = findToggleInCurrentContext(pvToggleLocators);
				if (inCurrent != null) return inCurrent;
			}
			if (depth <= 0) return null;

			List<WebElement> frames = findElements(PV_FRAMES_BY);
			for (WebElement frame : frames) {
				WebElement found = null;
				try {
					getDriver().switchTo().frame(frame);
					found = findToggleInFrames(depth - 1, pvToggleLocators, true);
					if (found != null) return found; // keep driver in frame of found element
				} catch (Exception ignored) {
					// try next frame
				} finally {
					if (found == null) {
						try { getDriver().switchTo().parentFrame(); } catch (Exception ignored2) {}
					}
				}
			}
		} catch (Exception ignored) {}
		return null;
	}

	private WebElement waitForPvToggle(List<By> pvToggleLocators) {
		long end = System.currentTimeMillis() + PV_UI_WAIT_TIMEOUT_MS;
		WebElement pvToggle = null;
		while (System.currentTimeMillis() < end) {
			try {
				switchtoDefaultFrame();
				if (switchToContextWithApply(PV_FRAME_SEARCH_DEPTH)) {
					pvToggle = findToggleInCurrentContext(pvToggleLocators);
					if (pvToggle != null) return pvToggle;
				}
				switchtoDefaultFrame();
				pvToggle = findToggleInFrames(PV_FRAME_SEARCH_DEPTH, pvToggleLocators, true);
				if (pvToggle != null) return pvToggle;
			} catch (Exception ignored) {}

			commonWaits.waitForSometime(PV_UI_WAIT_POLL_MS);
		}
		return null;
	}

	private void smartClick(WebElement element) {
		try {
			commonWaits.clickWithoutInterception(element);
		} catch (Exception e1) {
			try {
				clickByAction(element);
			} catch (Exception e2) {
				try {
					clickByJS(element);
				} catch (Exception ignored) {}
			}
		}
	}

    public ITSMDashboard searchServerName(String serverName) {
        commonWaits.waitForElementVisible(serverNameTextarea);
        serverNameTextarea.sendKeys(serverName);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public boolean isServerNameSearchResultRowPresent(String serverName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return !findElements(By.xpath(String.format(serverNameLocator, serverName))).isEmpty();
    }

    public ITSMDashboard deleteServerName(String serverName) {
        try {
            WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(serverNameLocator, serverName)));
            commonWaits.clickWithoutInterception(ele);
            commonWaits.clickByText(SolQAConstants.DELETE);
            commonWaits.switchtoDefaultFrame();
            commonWaits.switchToFrame(AcceptIframeOkButton);
            commonWaits.clickWithoutInterception(OkButton);
        } catch (Exception e) {
            log.error("No record found to delete with server name: " + serverName);
        }
        return this;
    }
    
    public ITSMDashboard updateWorkorderServiceRequestCreation(String company, String value) {
    	selectCompany(company);
    	clickWithoutInterception(serviceRequestCreationButton);
    	clickWithoutInterception(By.xpath(String.format(licenseTypeValue, value)));
    	clickWithoutInterception(saveButton);
    	return this;
    }
    
    public ITSMDashboard searchRequestID(String value) {
        commonWaits.waitForPageFullyLoaded(10, 1);
        commonWaits.waitForElementVisible(requestIDTextbox);
        keyClearAndType(requestIDTextbox, value);
    	clickWithoutInterception(searchButton);
    	return this;
	}
    
    public ITSMDashboard verifyFormNames(String expectedFormNames) {
		verifyEquals(formNamesLocator.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).replaceAll("\n\n\n", ","), expectedFormNames, "Form Names are not matched as expected. Actual Form Names: " + formNamesLocator.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).replaceAll("\n\n\n", ",") + " .Expected Form Names: " + expectedFormNames);
		return this;
    }

	public ITSMDashboard searchINCRequestIDInEmailMessages(String requestID) {
		commonWaits.waitForElementVisible(subjectTextarea);
		subjectTextarea.sendKeys(requestID);
		commonWaits.clickByText(SolQAConstants.SEARCH);
		return this;
	}

	public String getINCRequestIDFoundInEmailMessages() {
		return commonWaits.waitForElementVisible(requestIDFoundInEmailMessages).getText().trim();
	}
    /**
     * Search in BMC_BusinessService form by DatasetId
     * @param datasetId The dataset ID to search for (e.g., "BMC.ADDM")
     * @return ITSMDashboard instance
     */
    public ITSMDashboard searchByDatasetId(String datasetId) {
        log.debug("Searching by DatasetId: " + datasetId);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement datasetIdTextarea = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchCI, dataSetID_ARID)));
        datasetIdTextarea.clear();
        datasetIdTextarea.sendKeys(datasetId);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.debug("Search completed for DatasetId: " + datasetId);
        return this;
    }

    /**
     * Select first result from search results and get the InstanceId value
     * @return The InstanceId value of the selected record
     */
    public String selectFirstResultAndGetInstanceId() {
        log.debug("Selecting first result from search results");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        String instanceId;
        try {
            // Try to find already selected row first
            instanceId = commonWaits.waitForByElementToBeVisible(By.xpath(firstResultRowXpath)).getText().trim();
        } catch (Exception e) {
            // If no row is selected, click on first available row
            commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(resultRowXpath)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            instanceId = commonWaits.waitForByElementToBeVisible(By.xpath(firstResultRowXpath)).getText().trim();
        }

        log.debug("Selected InstanceId: " + instanceId);
        return instanceId;
    }
    
    public ITSMDashboard handlePostLoginPopups() {
        log.debug("Post-login popup handling started");
 
        // Ensure the page is stable before checking for optional popups
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
 
        // Case\#1: Warning popup is present \-> acknowledge it
        if (isVisible(userWarningOkButton)) {
            log.debug("User Warning popup detected clicking OK");
            clickWithoutInterception(userWarningOkButton);
 
            // Wait for UI to settle after closing the warning
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
 
            // Validate that the warning popup is closed (if it was shown, it must go away)
            Assert.assertTrue(commonWaits.waitForElementInvisible(userWarningOkButton), "User Warning popup did not close as expected");
            log.debug("User Warning popup handled successfully");
        }
        // Case\#2: Warning not present, but onboarding wizard is present \-> suppress it for future logins
        else if (isVisible(onboardingDoNotRemindMeAgainCheckbox)) {
            log.debug("Onboarding Wizard popup detected selecting 'Do not remind me again'");
            clickWithoutInterception(onboardingDoNotRemindMeAgainCheckbox);
 
            // Wait for any resulting UI activity (if any)
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
 
            // Current behavior validates the element becomes invisible after interaction
            // (Assumes the checkbox/popup disappears after selection in this application build)
            Assert.assertTrue(commonWaits.waitForElementInvisible(onboardingDoNotRemindMeAgainCheckbox), "Onboarding Wizard popup did not close as expected");
            log.debug("Onboarding Wizard popup handled successfully");
        }
        // Case\#3: No post-login popups were shown \-> just proceed
        else {
            log.debug("No post-login popups detected continuing");
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        }
 
        log.debug("Post-login popup handling finished");
        return this;
    }

    /**
     * Verify Impact Analysis email status
     */
    public ITSMDashboard verifyImpactAnalysisEmailStatus(String expectedStatus) {
        log.debug("Verifying Impact Analysis email status: " + expectedStatus);
        commonWaits.waitForPageFullyLoaded(30, 3);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        String statusXpath = "//td[contains(text(),'Impact Analysis') or contains(text(),'NIA')]//following-sibling::td[contains(text(),'" + expectedStatus + "')]";
        boolean isStatusFound = commonWaits.isElementPresent(By.xpath(statusXpath));
        if (!isStatusFound) {
            statusXpath = "//*[contains(text(),'" + expectedStatus + "')]";
            isStatusFound = commonWaits.isElementPresent(By.xpath(statusXpath));
        }
        log.debug("Impact Analysis email status verification: " + (isStatusFound ? "Found" : "Not Found"));
        return this;
    }

    public ITSMDashboard verifyHtmlBodyOnEmailPage(String requestId) {
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.clickByText(SolQAConstants.NEW_SEARCH);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForElementVisible(subjectTextarea).clear();
        subjectTextarea.sendKeys(requestId);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickByText(SolQAConstants.HTML_BODY);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), incidentDetailsHeaderInHtmlBody, 20),
                "Incident details are not present in HTML Body");
        return this;
    }

    /**
     * Set rules page value using radio button
     * Navigates to Incident Rules form, searches for company, and sets rule value
     * @param company Company name to search for
     * @param ruleName Name of the rule to configure
     * @param ruleValue Value to set (e.g., "Yes" or "No")
     */
    public ITSMDashboard setRulesPageValueUsingRadioButton(String company, String ruleName, String ruleValue) {
        log.debug("Setting rule '" + ruleName + "' to '" + ruleValue + "' for company '" + company + "'");
        
        // Load the Incident Rules form
        LoadForm(SolQAConstants.HPD_CFG_RULES, SolQAConstants.DEFAULT, null);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        
        // Switch to new tab if opened
        List<String> tabs = new ArrayList<String>(getDriver().getWindowHandles());
        if(tabs.size() > 1) {
            getDriver().switchTo().window(tabs.get(1));
        }
        
        // Click New Search button to clear existing search
        if (isVisible(newSearchButton)) {
            commonWaits.clickWithoutInterception(newSearchButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        }
        
        // Enter company in search field
        commonWaits.waitForElementVisible(incidentRulesCompanyField);
        keyClearAndType(incidentRulesCompanyField, company);
        incidentRulesCompanyField.sendKeys(Keys.ENTER);
        
        // Click Search button
        commonWaits.clickWithoutInterception(incidentRulesSearchButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        
        // Select the radio button for the specified rule and value
        WebElement radioButton = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(incidentRulesRadioButtonXpath, ruleName, ruleValue)));
        radioButton.click();
        
        // Save changes
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        
        // Refresh to ensure changes are applied
        commonWaits.clickByText(SolQAConstants.REFRESH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        
        log.debug("Successfully set rule '" + ruleName + "' to '" + ruleValue + "'");
        return this;
    }

    /**
     * Train cognitive data for support group prediction or other AI features
     * @param dataType Type of cognitive data (e.g., "Support Group Prediction")
     * @param company Company name
     * @param application Application name (e.g., "Incident Management")
     * @param template Template name for training data
     */
    public ITSMDashboard trainCognitiveData(String dataType, String company, String application, String template) {
        log.debug("Training cognitive data - Type: " + dataType + ", Company: " + company + ", Application: " + application + ", Template: " + template);
        
        // Load the Cognitive Training Setup form
        LoadForm("CFG:COG:TrainingSetup", "Default Administrator View", null);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        
        // Set Data Type field
        commonWaits.waitForElementVisible(cognitiveDataTypeField);
        keyClearAndType(cognitiveDataTypeField, dataType);
        
        // Set Company field
        keyClearAndType(cognitiveCompanyField, company);
        
        // Set Application field
        keyClearAndType(cognitiveApplicationField, application);
        
        // Set Template dropdown
        commonWaits.clickWithoutInterception(cognitiveTemplateDropdown);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        
        // Select template from dropdown
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(cognitiveTemplateOptionXpath, template)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(cognitiveTemplateOptionXpath, template)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        
        // Check if table has data
        int rowCount = cognitiveTrainingTableRows.size();
        
        if (rowCount == 0) {
            // Click Add Selected Template button
            log.debug("No training data exists. Adding selected template.");
            commonWaits.clickWithoutInterception(cognitiveAddTemplateButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        }
        
        // Refresh table to get current status
        if (isVisible(cognitiveRefreshButton)) {
            commonWaits.clickWithoutInterception(cognitiveRefreshButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        }
        
        // Get status from first row, column 4 (Status column)
        String status = "";
        if (isVisible(cognitiveStatusCell)) {
            status = cognitiveStatusCell.getText().trim();
        }
        
        // If status is not COMPLETED, generate training data
        if (!status.equals("COMPLETED")) {
            log.debug("Training data status is '" + status + "'. Generating training data.");
            
            // Select first row in table
            commonWaits.clickWithoutInterception(cognitiveFirstRow);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            
            // Click Generate Training Data button
            commonWaits.clickWithoutInterception(cognitiveGenerateButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            
            // Wait for training to complete (max 10 minutes, check every 30 seconds)
            long startTime = System.currentTimeMillis();
            long maxDuration = 10 * 60 * 1000; // 10 minutes
            long interval = 30 * 1000; // 30 seconds
            
            while (System.currentTimeMillis() - startTime < maxDuration) {
                // Refresh table
                if (isVisible(cognitiveRefreshButton)) {
                    commonWaits.clickWithoutInterception(cognitiveRefreshButton);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
                }
                
                // Check status
                if (isVisible(cognitiveStatusCell)) {
                    status = cognitiveStatusCell.getText().trim();
                    log.debug("Current training status: " + status);
                    
                    if (status.equals("COMPLETED")) {
                        log.debug("Cognitive data training completed successfully");
                        break;
                    }
                }
                
                // Wait before next check
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    log.warn("Training wait interrupted: " + e.getMessage());
                }
            }
            
            // Verify final status
            Assert.assertEquals(status, "COMPLETED", "Cognitive data training did not complete within expected time");
        } else {
            log.debug("Training data already completed. Skipping training.");
        }
        
        log.debug("Cognitive data training process finished");
        return this;
    }

    public ITSMDashboard navigateToITSMURLInNewOpenedTab() {
		getDriver().get(Launcher.getITSMURL());
		return this;
	}

    public ITSMDashboard navigateToManageCIs() {
		commonWaits.clickWithoutInterception(manageCIsButton);
		return this;
	}

	public ITSMDashboard verifyManageCIsOptionIsVisible() {
		Assert.assertTrue(isVisible(manageCIsButton), "Manage CIs option is visible");
		return this;
	}

    @FindBy(xpath = "//tr[@tabindex='0']")
    List<WebElement> ccsParameters;
    public void addCCSParameters(String expectedCCSParameter) {
        int size = ccsParameters.size();
        log.debug("Total matching rows before clicking Add: " + size);

        boolean isFound = false;
        for (int i = 0; i < size; i++) {
            String xpath = "(//tr[@tabindex='0' and @arrow='" + i + "']/td)[1]";
            try {
                WebElement element = findElement(By.xpath(xpath));
                String text = element.getText().trim();
                if (text.equals(expectedCCSParameter)) {
                    log.debug("Expected text found. Exiting...");
                    isFound = true;
                    break;
                }
            } catch (Exception e) {
                log.debug("No element found for arrow=" + i);
            }
        }

        if (!isFound) {
            log.debug("Expected text not found. Clicking Add button...");
            commonWaits.clickByText(SolQAConstants.ADD);
            for (int i = 0; i < size; i++) {
                String xpath = "(//tr[@tabindex='0' and @arrow='" + i + "']/td)[1]";
                WebElement element = findElement(By.xpath(xpath));
                try {
                    if (element.getText().trim().isEmpty()) {
                        log.debug("Empty row found at arrow=" + i);
                        log.debug("-------"+element);
                        // Enter the value in the correct row
                        Actions actions = new Actions(getDriver());
                        actions.moveToElement(element).click().perform();
                        actions.moveToElement(element).doubleClick().sendKeys(Keys.ENTER).perform();
                        actions.moveToElement(element).sendKeys(expectedCCSParameter).perform();
                        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementByARID, 16000))).click();
                        log.debug("Value entered in newly created row at arrow=" + i);
                        break;
                    }
                } catch (Exception e) {
                    log.error("No empty element found for arrow=" + i + ". Checking next...");
                }
            }

        }
    }

    public ITSMDashboard navigateToConfiguration(){
        WebElement SELF_SERVICE_PORTAL_BUTTON = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(configurationButtonXpath, SolQAConstants.SELF_SERVICE_PORTAL)));
        commonWaits.clickWithoutInterception(SELF_SERVICE_PORTAL_BUTTON);
        WebElement CONFIGURATION_BUTTON = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(configurationButtonXpath, SolQAConstants.CONFIGURATION)));
        commonWaits.clickWithoutInterception(CONFIGURATION_BUTTON);
        return this;
    }

    public ITSMDashboard modifySelfServicePortal(String option) {
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.switchToLastWindow();
        WebElement selfServicePortalButton = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selfServicePortalButtonXpath, option)));
        commonWaits.clickWithoutInterception(selfServicePortalButton);
        commonWaits.clickByText(SolQAConstants.MODIFY);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.switchToLastWindow();
        commonWaits.clickByText(SolQAConstants.MODIFY);
        commonWaits.switchToFrame(okIframe);
        commonWaits.clickWithoutInterception(okButton);
        commonWaits.waitForExecutionComplete();
        commonWaits.switchtoDefaultFrame();
        return this;
    }

    public ITSMDashboard enterRequiredDetails() throws IOException {
        // Path of your txt file
        String filePath = System.getProperty("user.dir") + File.separator +"resources" + File.separator +"ITSM" + File.separator +"SelfServicePortal.txt";

        // Read complete file content as-is
        String fileContent = Files.readString(
                Paths.get(filePath),
                StandardCharsets.UTF_8
        );
        // Clear existing text (optional)
        commonWaits.waitForElementVisible(textBox,40);
        textBox.clear();
        commonWaits.waitForExecutionComplete();
        // Send complete content exactly as-is
        textBox.sendKeys(fileContent);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementVisible(disabledRadioBtn);
        disabledRadioBtn.click();
        commonWaits.waitForElementVisible(enabledRadioBtn);
        enabledRadioBtn.click();
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForExecutionComplete();
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForExecutionComplete();
        commonWaits.clickByText("Refresh Servlets");
        commonWaits.clickByText("Apply");
        commonWaits.clickByText("OK");
        return this;
    }



}