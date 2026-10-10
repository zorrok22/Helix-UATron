package pages.ITSM.SmartIT;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import framework.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ViewIncidentPage extends BasePage{

	static SolqaLogger log = new SolqaLogger(ViewIncidentPage.class.getName());
	CommonWaits commonWaits=new CommonWaits();
	
	String IFRAME_PWA_DEFAULT = "//iframe[@id='pwa-frame']";
	String ATTRIBUTE_ARIA_PRESSED = "aria-pressed";
	String ATTRIBUTE_ARIA_CHECKED = "aria-checked";
	String ATTRIBUTE_CLASS = "class";
	String NEEDS_ATTENTION_TEXT = "Needs attention";
	String CLASS_TOKEN_ACTIVE = "active";
	String CLASS_TOKEN_SELECTED = "selected";
	String CLASS_TOKEN_CHECKED = "checked";
	String CLASS_TOKEN_ON = "on";
	String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";
	String UPPER_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static final long FETCH_TICKET_ID_TIMEOUT_MS = 60_000L;
	public static final long FETCH_TICKET_ID_POLL_MS = 500L;
	
	@FindBy(id = "ar1000000161_data")
	public WebElement incidentIdData;
	@FindBy(id = "ar1000000161")
	public WebElement incidentIdContainer;
	@FindBy(xpath = "//*[@id='ar1000000161']//input")
	public WebElement incidentIdInput;
	@FindBy(xpath = "//*[@id='ar1000000161']//textarea")
	public WebElement incidentIdTextarea;
	@FindBy(xpath = "//*[contains(@id,'1000000161') and contains(@id,'_data')]")
	public List<WebElement> incidentIdDataContains;
	
	@FindBy(xpath = "//*[@ux-id='system-alert']//*[@ux-id='system-alert-text']")
	List<WebElement> systemAlertTexts;
	Pattern INCIDENT_ID_PATTERN = Pattern.compile("(INC\\d+)", Pattern.CASE_INSENSITIVE);

	String[] NEEDS_ATTENTION_TOGGLE_XPATHS = new String[] {
			"//button[contains(@aria-label,'" + NEEDS_ATTENTION_TEXT + "') or contains(@title,'" + NEEDS_ATTENTION_TEXT + "')]",
			"//button[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')," +
					"translate('" + NEEDS_ATTENTION_TEXT + "','" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'))]",
			"//button[.//*[contains(@class,'flag') or contains(@class,'icon-flag') or contains(@class,'a-icon--flag')]]",
			"//*[@ux-id='needs-attention-flag' or @ux-id='needs-attention' or @ux-id='attention-flag']"
	};
	
	@FindBy(xpath = "//div[@id='304419451']//a[normalize-space()='Apex-Watches.com (AWS)']")
	public WebElement serviceName;
	
	@FindBy(xpath = "//button[@role='tab' and  contains(normalize-space(.), 'Configuration items')]")
	WebElement configurationItemsTab;
	
	@FindBy(xpath = "//button[@role='tab' and  contains(normalize-space(.), 'Related items')]")
	WebElement relatedItemsTab;
	
	@FindBy(xpath = "//button[.//span[contains(@class, 'a-menu__text') and contains(text(), 'Create related')]]")
	WebElement createRelatedButton;
	
	@FindBy(xpath = "//ul[contains(@class, 'a-menu ng')]/li[contains(.,'Outage')]")
	WebElement createRelatedOutageButton;
	
	@FindBy(xpath = "//button[contains(.,'Relate configuration item')]")
	WebElement relateCIButton;
	
	@FindBy(xpath="//*[@id='302258625']")
	public WebElement searchCITextbox;
	
	@FindBy(xpath="//*[@id='302259032']")
	public WebElement searchIcon;
	
	@FindBy(xpath="//*[@name='ar301527000']")
	public WebElement relationshipTypeButton;
	
	@FindBy(xpath="//*[@id='304253220']")
	public WebElement saveCIButton;
	
	@FindBy(xpath="//*[contains(@aria-label,'Description') and @id='1000000000']")
	public WebElement descriptionTextboxOutage;

	@FindBy(xpath = "//*[@id='1000000164']//*[@role='article']")
	WebElement priority;

	@FindBy(xpath = "//button[@title='Refresh']")
	WebElement refreshTicketButton;

	@FindBy(xpath = "//iframe[@id='pwa-frame']")
	public WebElement IFrame;

	@FindBy(xpath = "(//*[@id='304423041']//iframe)[1]")
	public WebElement feedIFrame;

	@FindBy(xpath = "//*[@id='content_block' or @id='contentBlock']")
	public WebElement firstNoteActivityTimeline;

	@FindBy(xpath = "//button[contains(@class,'accept-all-button')]")
	public WebElement acceptAllButton;

	@FindBy(xpath = "//div[contains(@class,'article-assistant__header')]//button")
	public WebElement acrticleHeaderCloseButton;

	@FindBy(xpath = "//button[contains(@class,'dropdown') and contains(.,'Work In Progress')]")
	public WebElement workStatusDropdown;

	@FindBy(xpath = "//button[contains(@data-testid,'tabs') and contains(.,'Resources')]")
	public WebElement resourceTabLocator;

	@FindBy(xpath = "//*[contains(@id,'titleInput')]")
	public WebElement articleTitleLocator;
	
	@FindBy(xpath = "//*[@id='304457021']")
	public WebElement pcaCardButton;
	
	@FindBy(xpath = "//div[contains(@class,'text-danger') and normalize-space(text())='Probability']")
	public WebElement pcaCardServiceHealthProbability;
	
	@FindBy(xpath = "//div[contains(@class,'text-danger') and normalize-space(text())='Probability']//preceding-sibling::div")
	public WebElement pcaCardServiceHealthProbabilityPercent;
	
	@FindBy(xpath = "//div[normalize-space(text())='Impacted Server']//following-sibling::button")
	public WebElement impactedServiceName;

	@FindBy(xpath = "//span[contains(@class,'nav-link-title') and contains(text(),'Tasks')]")
	public WebElement tasksTab;
	
	@FindBy(xpath = "//a[contains(@class,'text-break') and contains(@href,'taskPV')]")
	public WebElement taskLink;

	@FindBy(xpath = "//button[@id='304420591' and @aria-label='Edit']")
	public WebElement editButton;

	@FindBy(xpath = "//div[@ux-id='system-alert-text']")
	public WebElement systemAlertMessage;

	@FindBy(xpath = "//input[@id='ar304457261']")
	public WebElement SupportGroupTextbox;

	@FindBy(xpath = "//input[@id='ar304457241']")
	public WebElement PersonNameTextbox;

	@FindBy(xpath = "//input[@id='ar304457301']")
	public WebElement SupportGroupTextboxCommunicationCoOrdinator;

	@FindBy(xpath = "//input[@id='ar304457281']")
	public WebElement PersonNameTextboxCommunicationCoOrdinator;

	@FindBy(xpath = "//span[normalize-space()='View report']/ancestor::button")
	public WebElement viewReportLink;

	@FindBy(xpath = "(//td[normalize-space()='Incident ID']/ancestor::tbody//a)[1]")
	public WebElement INCIDInDetails;

	@FindBy(xpath = "//div[contains(@class,'main')]//h2[contains(normalize-space(text()),'Major Incident Report')]")
	public WebElement majorIncidentReportEditor;

	@FindBy(xpath = "//button[@data-cmd='bold']")
	public WebElement textBoldButton;

	@FindBy(xpath = "//span[normalize-space()='Close']/ancestor::button")
	public WebElement closeButtonOnReport;

	@FindBy(xpath = "//button[@aria-label='Save' and @title='Add']")
	public WebElement SaveButtonOnReport;

	@FindBy(xpath = "//button[@aria-label='Edit Major incident']")
	public WebElement majorIncidentEditButton;
	
	@FindBy(xpath = "//adapt-rx-select[.//span[text()='Major incident']]//button[@role='combobox']")
	public WebElement majorIncidentDropdown;

	@FindBy(xpath = "//button[contains(@id,'print')]")
	public WebElement printOption;

	@FindBy(xpath = "//*[contains(.,'Executive Summary')]/parent::thead//following-sibling::tbody")
	public WebElement summary;

	@FindBy(xpath = "//div[contains(@class,'main')]//h2/strong[contains(normalize-space(text()),'Major Incident Report')]")
	public WebElement majorIncidentReportEditorBold;
	
	@FindBy(xpath = "//button[contains(@id,'304421') and .//span[normalize-space()='Assign to me']]")
	WebElement assignToMeButton;

	@FindBy(xpath = "//input[@id='ar1000000217']")
	public WebElement SupportGroupTextboxForAssignee;

	@FindBy(xpath = "//input[@id='ar1000000218']")
	public WebElement PersonNameTextboxForAssignee;

	@FindBy(xpath = "//button[@aria-label='Flag']")
	public WebElement unflagTicketButton;

	
	public String taskLinkXpath = "//a[contains(@class,'text-break') and contains(@href,'taskPV')]";
	
	// Task-specific status dropdown locators (Task page has different structure than Incident page)
	public String taskStatusDropdownXpath = "//span[text()='Status']//ancestor::adapt-rx-select//button[contains(@class,'rx-select')]";
	public String taskStatusDropdownAlt = "//adapt-rx-select[contains(@testid,'7')]//button";
	public String taskStatusDropdownAlt2 = "//button[@name='ar7' and contains(@class,'rx-select')]";
	
	public String searchedCIResult = "//td[@data-testid='adapt-table_column-1_row-0' and contains(., '%s')]";
    public String checkBoxCIResult="//span[normalize-space()='Relate items']/ancestor::div//div[contains(@class, 'ui-chkbox-box')]";
    public String outageCISelection = "(//div[contains(@class, 'ui-chkbox-box')])[2]";
    public String relationshipOption = "//*[contains(text(),'%s')]/ancestor::button[contains(@class, 'rx-select__option')]";
    public String createdOutage = "//a[contains(@class, 'ng-star-inserted') and contains(text(), 'AOT')]";
    public String createdOutageCI = "//div[contains(text(), '%s')]";
//    public String descriptionTextboxOutage = "//*[contains(@aria-label,'Description') and @id='1000000000']";
    public String closeOutageIcon="//*[@aria-label='close']";
    public String ElementXpathByID= "//*[@id='%s']";
	public String menuOptionsViewPage= "//div[@id='304433221']";
    public String IncidentIdValue = "//div[@id='ar1000000161_data']";
    public String IFrameViewIncident = "//iframe[@id='pwa-frame']";
    public String customerViewPage = "//a[@id='ar303530000_data']";
    public String incidentDescriptionViewPage = "//div[@id='ar1000000151_data']";
    public String incidentTitleViewPage = "//div[@id='304419361']";
    public String productCategoryViewPage = "//smt-text-view[@id='304420031'][2]";
    public String operationalCategoryViewPage = "//div[@id='ar304420021_data']";
    public String statusViewPage = "//div[@id='ar7_data']";
    public String StatusDownArrow= "//button[@id='304424821']";
    public String statusDropdown = "//button[@role='combobox' and @aria-label='Status']";
    public String statusReasonDropdown = "//span[text()='Status reason']//ancestor::adapt-rx-select[@testid]//button";
    public String resolutionNoteTextbox = "//textarea[@id='1000000156']";
    public String elementXpathById ="//*[@id='%s']"; 
    public String optionUpdate= "//*[contains(@class, 'rx-select__options-scroll')]/button[contains(.,'%s')]";
    public String statusOptionButton = "//*[contains(text(),'%s')]//ancestor::button[@role='option']";
	public String optionValueMajorIncident= "//*[contains(@class, 'rx-select__options-scroll')]/button[contains(.,'%s')]";
	public String elementXpathByName ="//*[@name='%s']";
	public String typeheadListIncidentManager= "//*[@id='ar304457241_popup_item-0']//adapt-highlight[contains(text(),'%s')]";
	public String relateditemTab ="(//*[contains(@class, 'nav-link')]//span//span[contains(.,'Related items')])[1]";
	public String actualDuplicateAssigned ="//td[@data-testid='adapt-table_column-0_row-0']//a[contains(@id,'ar304424351')]";
	public String viewDetails = "//*[@aria-label='Top probable causes nodes']/..//button[@aria-label='View Details']";
    public String majorIncidentCandidate = "//*[@id='ar304486201']";
    public String additionalElementByAriaLable= "//*[contains(@aria-label,'%s')]";
    public String companyValueElement= "//*[contains(@class, 'rx-typeahead-popup')]/button[contains(.,'%s')]";
    public String errorAlertText ="//*[@ux-id='cross-icon']";
	public String iFrameProbableCause= "//iframe[@id='VF304456951']";
	public String createNewArticleLocator="//button[contains(@data-testid,'304487091') and contains(.,'Create a new article')]";
	public String incidentMoreButton = "//*[@data-testid='menu-item-test-id-7' and contains(.,'More')]";
    public String workStatusOption = "//button[contains(.,'%s')]";
    public String openPinnedArticleLocator = "//*[@alt='Related']//ancestor::tr//a[contains(.,'%s')]";
    public String iFrameResourceIncident = "//iframe[contains(@id,'0304459671')]";
    public String dimissAlertButton = "//div[contains(@class,'system-alert__dismiss')]";
    public String ReputationalImpactPopUp = "//*[contains(@aria-label,'!Reputational impact')]";
    public String IFrameHealthTimeline = "//iframe[@id='VF304456951']";
	String activityNotesIFrame = "(//*[@id='304423041']//iframe)";
    String activityNotes = "(//*[@id='content_block' or @id='contentBlock'][contains(.,'added a note')])[1]";
    String elementByIDARData = "//*[@id='ar%s_data']";
	public String priorityBadgeText = "//div[@testid='ar1000000164_data' and text()='Low']";
	String pwaIframe = "//iframe[@id='pwa-frame']";
	public String requestIDViewPage = "//div[contains(.,'%s')and @ux-id='character-field-value']";
	
	// Request page activity feed locators - NOT inside any iframe (use normalize-space(.) so trailing text e.g. "with reason: """ still matches).
	public String requestActivityFeedItemXpath = "//div[contains(@ux-id,'activity-feed-item')]//*[contains(normalize-space(.),'%s')]";
	public String requestFeedItemTitleXpath = "//div[contains(@class,'feed-item__title')]//*[contains(normalize-space(.),'%s')]";
	public String requestFeedItemContentXpath = "//div[contains(@class,'feed-item')]//*[contains(normalize-space(.),'%s')]";

	String majorIncidentManager_ARID = "ar304457241";
	String saveButton = "Save...";
	String saveHereButton = "Save here";
	String viewFullArticle = "View full article";
	String optionDropdownMajorIncident= "ar304461051";
	String saveIncidentBox="304440891";
	String reputationalImpactBox= "Reputational impact";
	String DataLossesBox= "Data losses";
	String EconomicImpactBox= "Economic impact";
	String AffectedClientsBox= "Affected clients, finances, transactions";
	String ServiceDowntimeBox= "Service downtime (mins)";
	String serviceCIBox="ar303497300";
	String serviceCIBox1= "ar200000020";
	String impactedAreaBox="ar304439821";
	String impactedAreaBox1="304439891";
	String scheduledStartDateBox="1000000350";
	String scheduledEndDateBox="1000000362";
	String saveOutageButton="304448291";
	String statusARID = "7";
	
	public ViewIncidentPage() {
    	log = new SolqaLogger(ViewIncidentPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }
	
	public ViewIncidentPage switchToIncidentPwaFrame() {
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_PWA_DEFAULT));
		return this;
	}
	
	public WebElement findNeedsAttentionToggleOrNull() {
		switchToIncidentPwaFrame();
		for (String xpath : NEEDS_ATTENTION_TOGGLE_XPATHS) {
			try {
				By by = By.xpath(xpath);
				if (!findElements(by).isEmpty()) {
					return findElement(by);
				}
			} catch (Exception ignored) {
				// continue to next candidate
			}
		}
		return null;
	}
	
	public boolean isNeedsAttentionFlagIconVisible() {
		return findNeedsAttentionToggleOrNull() != null;
	}
	
	public boolean isNeedsAttentionFlagSet() {
		WebElement toggle = findNeedsAttentionToggleOrNull();
		if (toggle == null) return false;
		
		String ariaPressed = toggle.getAttribute(ATTRIBUTE_ARIA_PRESSED);
		if (ariaPressed != null && !ariaPressed.trim().isEmpty()) {
			return Boolean.parseBoolean(ariaPressed.trim());
		}
		
		String ariaChecked = toggle.getAttribute(ATTRIBUTE_ARIA_CHECKED);
		if (ariaChecked != null && !ariaChecked.trim().isEmpty()) {
			// Some builds return "true"/"false" or "mixed"
			return "true".equalsIgnoreCase(ariaChecked.trim());
		}
		
		String cls = toggle.getAttribute(ATTRIBUTE_CLASS);
		if (cls != null) {
			String c = cls.toLowerCase();
			return c.contains(CLASS_TOKEN_ACTIVE)
					|| c.contains(CLASS_TOKEN_SELECTED)
					|| c.contains(CLASS_TOKEN_CHECKED)
					|| c.contains(CLASS_TOKEN_ON);
		}
		return false;
	}
	
	public ViewIncidentPage setNeedsAttentionFlag(boolean shouldBeSet) {
		WebElement toggle = findNeedsAttentionToggleOrNull();
		Assert.assertNotNull(toggle, "Needs Attention flag toggle is not present on Incident view page.");
		
		boolean isSet = isNeedsAttentionFlagSet();
		if (isSet != shouldBeSet) {
			// Prefer BasePage clickByAction for tricky UI buttons
			clickByAction(toggle);
			commonWaits.waitForSometime(commonWaits.minDelay);
		}
		
		Assert.assertEquals(isNeedsAttentionFlagSet(), shouldBeSet,
				"Needs Attention flag state mismatch. Expected=" + shouldBeSet);
		return this;
	}
	
    public String FetchTicketID() {
    	log.info("Fetching Ticket ID");
    	
    	// Instead, wait for the Incident ID itself to appear.
    	final long endAt = System.currentTimeMillis() + FETCH_TICKET_ID_TIMEOUT_MS;
    	
    	while (System.currentTimeMillis() < endAt) {
    		try {
    			// 1) Best-effort: check toast/system alert in default content
    			switchtoDefaultFrame();
    			for (WebElement el : systemAlertTexts) {
    				try {
    					String t = el.getText();
    					if (t != null && !t.trim().isEmpty()) {
    						Matcher m = INCIDENT_ID_PATTERN.matcher(t);
    						if (m.find()) return m.group(1).trim();
    					}
    				} catch (Exception ignored) {}
    			}
    			
    			// 2) Best-effort: check URL for INCxxxx
    			try {
    				String url = getDriver().getCurrentUrl();
    				if (url != null && !url.trim().isEmpty()) {
    					Matcher m = INCIDENT_ID_PATTERN.matcher(url);
    					if (m.find()) return m.group(1).trim();
    				}
    			} catch (Exception ignored) {}

    			commonWaits.switchtoDefaultFrame();
    			if (!findElements(By.xpath(IFrameViewIncident)).isEmpty()) {
    				switchToFrame(By.xpath(IFrameViewIncident));
    			}
    			
    			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    			
    			String id = "";
    			id = readBestEffort(incidentIdData);
    			if (!id.isEmpty()) return id;
    			id = readBestEffort(incidentIdContainer);
    			if (!id.isEmpty()) return id;
    			id = readBestEffort(incidentIdInput);
    			if (!id.isEmpty()) return id;
    			id = readBestEffort(incidentIdTextarea);
    			if (!id.isEmpty()) return id;
    			if (incidentIdDataContains != null && !incidentIdDataContains.isEmpty()) {
    				for (WebElement el : incidentIdDataContains) {
    					id = readBestEffort(el);
    					if (!id.isEmpty()) return id;
    				}
    			}
    		} catch (Exception ignored) {
    			// best-effort polling; continue
    		}
    		
    		commonWaits.waitForSometime(FETCH_TICKET_ID_POLL_MS);
    	}
    	
    	Assert.fail("Unable to fetch Incident Ticket ID within " + FETCH_TICKET_ID_TIMEOUT_MS + "ms. " +
    			"Incident ID element not found/empty in PWA frame.");
    	return "";
	}

	public static String readBestEffort(WebElement el) {
		if (el == null) return "";
		try {
			String text = "";
			try { text = el.getText(); } catch (Exception ignored) {}
			if (text == null || text.trim().isEmpty()) {
				try { text = el.getAttribute("innerText"); } catch (Exception ignored) {}
			}
			if (text == null || text.trim().isEmpty()) {
				try { text = el.getAttribute("value"); } catch (Exception ignored) {}
			}
			return text == null ? "" : text.trim();
		} catch (Exception ignored) {
			return "";
		}
	}
	
	public void verifyIncidentData(Map<Object, String> smartITTestData) {
		commonWaits.waitForPageFullyLoaded(30,3);
		log.info("Verifying the Incident Data");
		
		if (smartITTestData.get("Customer Name") != null) {
			log.info("Verifying the Incident Customer Name");
			verifyEquals(getCustomerValue_viewPage(), smartITTestData.get("Customer Name").toString(),
					"Incident Customer Name Verification Failed.");
		}
		if (smartITTestData.get("Incident Description") != null && smartITTestData.get("Incident Description").toString() != "") {
			log.info("Verifying the Incident Description");
			verifyEquals(getDescription_viewPage(), smartITTestData.get("Incident Description").toString(),
					"Incident description verification failed");
		}
		if (smartITTestData.get("Incident Title") != null) {
			log.info("Verifying the Incident Title");
			verifyEquals(getTitle_viewPage(), smartITTestData.get("Incident Title").toString(),
					"Incident Title Verification Failed.");
		}
		if (smartITTestData.get("ProductCategory") != null) {
			log.info("Verifying the Incident Prod Category value");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			verifyEquals(getProductCategory_TicketViewPage(), smartITTestData.get("ProductCategory").toString(),
					"Incident Product Category Verification Failed.");
		}
		if (smartITTestData.get("OperationalCategory") != null
				&& smartITTestData.get("OperationalCategory").toString() != "") {
			log.info("Verifying the Incident Operation Category Value");
			verifyEquals(getOperationalCategory_TicketViewPage(), smartITTestData.get("OperationalCategory").toString(),
					"Incident Operational Category Verification Failed.");
		}
		if (smartITTestData.get(SolQAConstants.STATUS) != null) {
			verifyEquals(getStatus_viewPage(), smartITTestData.get(SolQAConstants.STATUS).toString(),
					"Status verification failed on incident view page");
		}
	}
	
	public String getCustomerValue_viewPage() {
		return getValueFromText(customerViewPage);
	}
	
	public String getDescription_viewPage() {
		return getValueFromText(incidentDescriptionViewPage);
	}
	
	public String getTitle_viewPage() {
		return getValueFromText(incidentTitleViewPage);
	}
	
	public String getProductCategory_TicketViewPage() {
		return getValueFromText(productCategoryViewPage);
	}
	
	public String getOperationalCategory_TicketViewPage() {
		return getValueFromText(operationalCategoryViewPage);
	}

    public String getStatus_viewPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
        return commonWaits.waitForByElementToBeVisible(By.xpath(statusViewPage)).getText();
    }
	
	public void setStatus(String newStatus, String statusReason, String resolutionNote) {
		setStatus(newStatus);
 
		String statusWithStatusReasonRequired[] = { "Pending", "Resolved", "Closed" };
 
		if (Arrays.asList(statusWithStatusReasonRequired).contains(newStatus)) {
			setStatusReason(statusReason);
		}
 
		String statusWithResolutionNoteRequired[] = { "Resolved", "Closed" };
		if (Arrays.asList(statusWithResolutionNoteRequired).contains(newStatus)) {
			setResolutionNote(resolutionNote);
		}
	}

	public void setStatus(String status) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(StatusDownArrow), 30),
                "Status dropdown is not visible");
		commonWaits.waitForByElementToBeVisible(By.xpath(StatusDownArrow));
		isElementPresent(By.xpath(StatusDownArrow));
		commonWaits.clickWithoutInterception(By.xpath(StatusDownArrow));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(statusDropdown), 20), "Status dropdown options are not visible");
		commonWaits.waitForByElementToBeVisible(By.xpath(statusDropdown));
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.clickWithoutInterception(By.xpath(statusDropdown));

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(statusOptionButton, status)), 20), "Status option '" + status + "' is not visible in dropdown");
		commonWaits.clickWithoutInterception(By.xpath(String.format(statusOptionButton, status)));
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void setStatusReason(String statusReason) {
		commonWaits.waitForPageFullyLoaded(30,3);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForPageFullyLoaded(30,3);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(statusReasonDropdown), 20), "Status reason dropdown is not visible");
		commonWaits.waitForByElementToBeVisible(By.xpath(statusReasonDropdown));
		isElementPresent(By.xpath(statusReasonDropdown));
		commonWaits.clickWithoutInterception(By.xpath(statusReasonDropdown));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(statusOptionButton, statusReason)), 20), "Status reason option '" + statusReason + "' is not visible in dropdown");
		commonWaits.clickWithoutInterception(By.xpath(String.format(statusOptionButton, statusReason)));
		commonWaits.waitForPageFullyLoaded(30,3);
	}
	
	public void setResolutionNote(String resolutionNote) {
		commonWaits.waitForPageFullyLoaded(30,3);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(resolutionNoteTextbox), 20), "Resolution note textbox is not visible");
		commonWaits.waitForByElementToBeVisible(By.xpath(resolutionNoteTextbox));
		isElementPresent(By.xpath(resolutionNoteTextbox));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		keyClearAndType(resolutionNoteTextbox, resolutionNote);
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void clickOnDropdownAndSelectOptionFromMajorIncident(String optionName){

		log.info("------ Clicking on Major Incident Reset demo and select option with name -"+ optionName);

		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForElementVisible(majorIncidentEditButton);
		commonWaits.clickWithoutInterception(majorIncidentEditButton);
		try {
		commonWaits.clickWithoutInterception(By.xpath(String.format(optionValueMajorIncident, optionName)));
		}
		catch (Exception e) {
			commonWaits.clickWithoutInterception(majorIncidentDropdown);
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(optionValueMajorIncident, optionName)) , 30 ), "Option Value is not visible");
			commonWaits.clickWithoutInterception(By.xpath(String.format(optionValueMajorIncident, optionName)));
		}

	}

	public void updateAssignment_MajorIncidentManager(String personName) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathById,majorIncidentManager_ARID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathById,majorIncidentManager_ARID)));
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadListIncidentManager, personName)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListIncidentManager, personName)));
		commonWaits.waitForPageFullyLoaded(30,3);
	}



	public void clickOnRelatedItemEditIncidentAndVerifyChildItem() {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchToTabByIndex(3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		refreshBrowser();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForByElementToBeVisible(By.xpath(relateditemTab));
		commonWaits.clickWithoutInterception(By.xpath(relateditemTab));

	}

	public String actualDuplicateAssigned() {
		commonWaits.waitForByElementToBeVisible(By.xpath(actualDuplicateAssigned));
		return getValueFromText(actualDuplicateAssigned).trim();

	}

	public void clickOnViewDetails() {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(iFrameProbableCause));
		Assert.assertTrue(commonWaits.isElementPresent(viewDetails),  "PCA card is not loaded properly");
		commonWaits.clickWithoutInterception(By.xpath(viewDetails));
		commonWaits.switchToTabByIndex(4);
	}

	public void verifyServiceAttachedToIncident(String expectedService) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.isElementPresent(serviceName);
		String actualValue = serviceName.getText().trim();
		Assert.assertEquals(actualValue, expectedService, "Apex related Incident is not opened");
		commonWaits.switchtoDefaultFrame();
	}

	public String getActivityFirstNoteText_ActivityTimeline() {
		log.info("Getting first activity feed text from activity timeline");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		switchToFrame(IFrame);
		switchToFrame(feedIFrame);
		String value = "";
		try {
			commonWaits.waitForElementVisible(firstNoteActivityTimeline);
			value = firstNoteActivityTimeline.getAttribute( "innerText");
			String[] str = value.split("\n");
			value = "";
			for (int i = 0; i < str.length; i++) {
				if (i == 0)
					value = value + str[i].trim();
				else
					value = value + "\n" + str[i];
			}
			switchtoDefaultFrame();
		} catch (NullPointerException e) {
			return value;
		}
		return value;
	}
	
	public void saveIncidentVieIncidentPage() {
		log.info("Clicking on Save Button on Incident");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathById, saveIncidentBox)));
		click(By.xpath(String.format(elementXpathById, saveIncidentBox)));
		commonWaits.waitForPageFullyLoaded(30, 3);	
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}
	
	public void verifyErrorMessage() {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		boolean isMajorIncidentFieldPresent = commonWaits.isElementPresent(majorIncidentCandidate);
		Assert.assertTrue(isMajorIncidentFieldPresent, "'Major Incident Candidate' field is not present");
		this.saveIncidentVieIncidentPage();
		
	}
	
	public void verifyAdditionalFields() {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		boolean isMajorIncidentFieldPresent = commonWaits.isElementPresent(majorIncidentCandidate);
		Assert.assertTrue(isMajorIncidentFieldPresent, "'Major Incident Candidate' field is not present");
		
		
		if (isMajorIncidentFieldPresent) {
			log.info("Verifying field value Reputational Impact");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,reputationalImpactBox)));
			String reputationalImpact= getValueFromText(String.format(additionalElementByAriaLable,reputationalImpactBox));
			log.info("Value for Reputational Impact is : " + reputationalImpact);
			
			log.info("Verifying field value Data losses");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,DataLossesBox)));
			String DataLosses= getValueFromText(String.format(additionalElementByAriaLable, DataLossesBox));
			log.info("Value for Data losses is : " + DataLosses);
			
			log.info("Verifying field value Economic impact");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,EconomicImpactBox)));
			String EconomicImpact= getValueFromText(String.format(additionalElementByAriaLable,EconomicImpactBox));
			log.info("Value for Economic impact is : " + EconomicImpact);
			
			log.info("Verifying field value Affected clients, finances, transactions");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,AffectedClientsBox)));
			String AffectedClients= getValueFromText(String.format(additionalElementByAriaLable,AffectedClientsBox));
			log.info("Value for Affected clients, finances, transactions : " + AffectedClients);
			
			log.info("Verifying field value Service downtime (mins)");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,ServiceDowntimeBox)));
			String ServiceDowntime= getValueFromText(String.format(additionalElementByAriaLable,ServiceDowntimeBox));
			log.info("Value for Service downtime (mins) : " + ServiceDowntime);
			
		}
	}
	
	public void FillAdditionalFields(String optionName, String downTimeValue){
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		boolean isMajorIncidentFieldPresent = commonWaits.waitForElementVisible_withFluentWait(getDriver(), majorIncidentCandidate , 30 );
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), majorIncidentCandidate , 30 ), "Major Incident Candidate' field is not present");
    	
		if (isMajorIncidentFieldPresent) {
			log.info("Updating Reputational Impact");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,reputationalImpactBox)));
			commonWaits.clickWithoutInterception(By.xpath(String.format(additionalElementByAriaLable, reputationalImpactBox)));
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(optionUpdate,optionName)));
			commonWaits.clickWithoutInterception(By.xpath(String.format(optionUpdate, optionName)));
			
			log.info("Updating Service downtime (mins)");
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(additionalElementByAriaLable,ServiceDowntimeBox)));
			keyClearAndType(String.format(additionalElementByAriaLable, ServiceDowntimeBox), downTimeValue);
			
			log.info("Click Assign To Me");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			commonWaits.waitForElementClickable(assignToMeButton);
			clickWithoutInterception(assignToMeButton);
			commonWaits.waitForPageFullyLoaded(30, 3);	
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			
		}
		
		this.saveIncidentVieIncidentPage();
	}
	
	public void FillServiceCI(String serviceValue) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathById,serviceCIBox)));
		keyClearAndType(String.format(elementXpathById, serviceCIBox), serviceValue);
		By serviceCiOptionBy = By.xpath(String.format(elementXpathById, serviceCIBox1));
		if (commonWaits.isElementPresent(serviceCiOptionBy, 5)) {
			commonWaits.clickWithoutInterception(serviceCiOptionBy);
		}
		commonWaits.waitForPageFullyLoaded(30, 3);	
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		
	}
	
	public void FillImpactedArea(String companyValue) {
		
		commonWaits.waitForPageFullyLoaded(30, 3);	
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathById,impactedAreaBox)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathById, impactedAreaBox)));
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(companyValueElement,companyValue)) , 30 ), "Company Value is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(String.format(companyValueElement,companyValue)));
    	
		commonWaits.waitForPageFullyLoaded(30, 3);	
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(elementXpathById,impactedAreaBox1)) , 30 ), "Impacted Area Box is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathById,impactedAreaBox1)));
		
		commonWaits.waitForPageFullyLoaded(30, 3);	
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}
	
	public void verifyReputaionalImpact(String reputationalInput) {
		
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(elementXpathById,"304431981")) , 30 ), "Reputational Impact is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathById,"304431981")));
    		
		log.info("Verify Toggle Opened");
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(elementXpathById,"304434351")) , 30 ), "Toggle is not visible");
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(elementXpathById,"1000000827")) , 30 ), "Toggle is not visible");
		keyClearAndType(String.format(elementXpathById, "1000000827"), reputationalInput);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ReputationalImpactPopUp,reputationalInput )) , 30 ), "Reputational Impact PopUp is not visible");
		commonWaits.clickWithoutInterception(By.xpath(String.format(ReputationalImpactPopUp, reputationalInput)));
		commonWaits.waitForPageFullyLoaded(30, 3);	
		
	}
	

	public String createNewArticle() {
		commonWaits.switchToFrame(IFrameViewIncident);
    	if(isElementNotPresent(createNewArticleLocator)) {
			clickWithoutInterception(By.xpath(incidentMoreButton));
		}
    	clickWithoutInterception(By.xpath(createNewArticleLocator));
    	commonWaits.switchtoDefaultFrame();
    	commonWaits.waitForAngularRequestsToFinish();
    	commonWaits.switchToLastWindow();
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    	Assert.assertTrue(getDriver().getCurrentUrl().contains(SolQAConstants.COMAROUND), "HKM is not cross launched successfully.");
		commonWaits.javaScriptScrollIntoView(commonWaits.waitForElementVisible(acceptAllButton));
    	clickWithoutInterception(acceptAllButton);
    	clickWithoutInterception(acrticleHeaderCloseButton);
    	String articleTitle = articleTitleLocator.getAttribute(SolQAConstants.ATTRIBUTE_VALUE); 
    	clickWithoutInterception(workStatusDropdown);
    	clickWithoutInterception(By.xpath(String.format(workStatusOption, SolQAConstants.PUBLISHED)));
    	clickWithoutInterception(By.xpath(String.format(workStatusOption, saveButton)));
    	clickWithoutInterception(By.xpath(String.format(workStatusOption, saveHereButton)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    	getDriver().close();
    	commonWaits.switchToTabByTitle(SolQAConstants.appITSM);
    	refreshBrowser();
    	return articleTitle;
    }
	
	public void navigateToResourceTab() {
		commonWaits.switchToFrame(IFrameViewIncident);
		clickWithoutInterception(resourceTabLocator);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
	}
	
	public void openArticle(String articleTitle) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchToFrameInsideFrame(IFrameViewIncident,iFrameResourceIncident);
		clickWithoutInterception(By.xpath(String.format(openPinnedArticleLocator, articleTitle)));
		commonWaits.switchtoDefaultFrame();
	}
	
	public boolean isKnowledgeArticlePinnedUnderResourcesTabByTitle(String articleTitle) {
		if (articleTitle == null || articleTitle.trim().isEmpty()) {
			return false;
		}
		String title = articleTitle.trim();
		String xp = String.format(openPinnedArticleLocator, title);
		
		// Best-effort: ensure Resources tab is selected (UI varies by stack)
		try { navigateToResourceTab(); } catch (Exception ignored) {}
		
		try {
			commonWaits.switchtoDefaultFrame();
			switchToFrame(By.xpath(IFrameViewIncident));
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			
			if (commonWaits.waitForElementPresent(By.xpath(iFrameResourceIncident))) {
				switchToFrame(By.xpath(iFrameResourceIncident));
			}
			
			boolean present = !findElements(By.xpath(xp)).isEmpty();
			commonWaits.switchtoDefaultFrame();
			return present;
		} catch (Exception ignored) {
			commonWaits.switchtoDefaultFrame();
			return false;
		}
	}
	
	public void clickOnViewFullArticle() {
		commonWaits.switchToFrame(IFrameViewIncident);
		if(isElementPresent(By.xpath(String.format(workStatusOption, viewFullArticle)))) {
			clickWithoutInterception(By.xpath(String.format(workStatusOption, viewFullArticle)));
		}
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchToLastWindow();
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 10);
	}
	
	public void relateCI(String ciName, String relationshipType) {
		log.info("Relating CI '" + ciName + "' to the Incident");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();
        
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), configurationItemsTab , 30 ), "configurationItemsTab  is not visible");
    	commonWaits.clickWithoutInterception(configurationItemsTab);
    	
		commonWaits.waitForElementVisible(relateCIButton);
		commonWaits.clickWithoutInterception(relateCIButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), searchCITextbox , 30 ), "searchCITextbox  is not visible");
		commonWaits.waitForElementClickable(searchCITextbox, 5);
        typeKeys(searchCITextbox, ciName);
        
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), searchIcon , 30 ), "searchIcon  is not visible");
        commonWaits.clickWithoutInterception(searchIcon);
		
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(searchedCIResult,ciName)) , 30 ), "searchedCIResult  is not visible");
        commonWaits.waitForElementClickable(checkBoxCIResult, 5);
        commonWaits.clickWithoutInterception(By.xpath(checkBoxCIResult));
        
        log.info("Selecting relationship type");
        commonWaits.waitForElementVisible(relationshipTypeButton);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), relationshipTypeButton , 30 ), "relationshipTypeButton  is not visible");
    	commonWaits.clickWithoutInterception(relationshipTypeButton);

    	Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(relationshipOption, relationshipType)) , 30 ), "relationshipTypeOption  is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(String.format(relationshipOption, relationshipType)));
        commonWaits.waitForPageFullyLoaded(30,3);
        
        log.info("Clicking save button");
        commonWaits.clickWithoutInterception(saveCIButton);
        commonWaits.waitForPageFullyLoaded(30,3);
		log.info("Successfully related CI '" + ciName + "' to the Incident.");
	}
	
	public void createRelatedOutage(String ciName, String relationshipType, String outageDescription) {

		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		log.info("Clicking Related Items Tab");
		commonWaits.waitForElementVisible(relatedItemsTab);
		commonWaits.clickWithoutInterception(relatedItemsTab);
		
		log.info("Clicking Create related button");
		commonWaits.waitForElementVisible(createRelatedButton);
		commonWaits.clickWithoutInterception(createRelatedButton);
		
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForElementVisible(createRelatedOutageButton);
        commonWaits.waitForElementVisible(createRelatedOutageButton).click();
        this.createOutage(outageDescription);
        
        commonWaits.waitForPageFullyLoaded(30, 3);
	}
	
	public void createOutage(String outageDescription) {
		log.info("Creating Outage");
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));

		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), outageCISelection , 30 ), "outageCISelection is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(outageCISelection));
        
        log.info("Filling Description for Outage");
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), descriptionTextboxOutage , 30 ), "descriptionTextboxOutage is not visible");
    	commonWaits.clickWithoutInterception(descriptionTextboxOutage);   	
        descriptionTextboxOutage.sendKeys(outageDescription);
		
		log.info("Setting Scheduled Start Date");
		scheduledStartDate();
		log.info("Setting Scheduled End Date");
		scheduledEndDate();
		
		log.info("Saving Outage");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, saveOutageButton)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID, saveOutageButton)));
		
		log.info("Outage created successfully.");
		
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		
	}
	
	
	public void scheduledStartDate() {
	    log.info("Setting Scheduled Start Date: today's date");

	    SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.AM_PM, Calendar.AM);

	    String startDate = formatter.format(calendar.getTime());
	    
	    commonWaits.waitForByElementToBeVisible(
	        By.xpath(String.format(ElementXpathByID, scheduledStartDateBox))
	    ).sendKeys(startDate);
	}
	
	public void scheduledEndDate() {
	    log.info("Setting Scheduled End Date: 2 days from today");

	    SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");

	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DATE, 2); // Add 2 days to current date
	    calendar.set(Calendar.HOUR, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.AM_PM, Calendar.AM);

	    String endDate = formatter.format(calendar.getTime());

	    commonWaits.waitForByElementToBeVisible(
	        By.xpath(String.format(ElementXpathByID, scheduledEndDateBox))
	    ).sendKeys(endDate);
	}
	
	public void verifyOutageCreated(String ciName){
		log.info("Verifying Outage Created");
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), relatedItemsTab , 30 ), "relatedItemsTab is not Visible");

		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createdOutage , 30 ), "Outage is not created successfully.");
		log.info("Outage created and verified successfully.");
		
		log.info("Clicking created Outage");
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createdOutage , 30 ), "createdOutage is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(createdOutage));    	
		
		log.info("Verifying created outage CI");
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(createdOutageCI, ciName)) , 30 ), "outageCISelection is not visible");
    	commonWaits.clickWithoutInterception(By.xpath(String.format(createdOutageCI, ciName)));
    	
		String outageCI = getValueFromText(String.format(createdOutageCI, ciName));
		Assert.assertTrue(outageCI.contains(ciName), "Outage CI does not contain expected text.");
		
		log.info("Outage details verified successfully.");
		
		log.info("Close created Outage");
		commonWaits.waitForByElementToBeVisible(By.xpath(closeOutageIcon));
		commonWaits.clickWithoutInterception(By.xpath(closeOutageIcon));
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForPageFullyLoaded(30, 3);
		this.clickOnDismissAlerts();
	}
	
	public ViewIncidentPage clickOnDismissAlerts() {
		 List<WebElement> dismissAlerts = findElements(By.xpath(dimissAlertButton));
		 if(dismissAlerts.size() > 0) {
			 for (WebElement alert : dismissAlerts) {
				 if (alert.isDisplayed()) {
					 commonWaits.clickWithoutInterception(alert);				 }
			 }
		 } else {
			 log.info("No alerts to dismiss");
		}
		 return this;
	}

    public String getPriority() {
        refreshTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), priority, 30),
                "Priority field is not visible on incident view page");
        return priority.getText();
    }

	public String getTicketSummaryPriorityBadgeText() {
		refreshTicket();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		String xpath = priorityBadgeText;	
		return commonWaits.waitForByElementToBeVisible(By.xpath(xpath)).getText().trim();
	}

    public void refreshTicket(){
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(pwaIframe));
        commonWaits.clickWithoutInterception(refreshTicketButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void switchToPWAFrame() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(pwaIframe));
        log.info("Switched to PWA frame successfully.");
    }

    public boolean verifyActivityNotes(String expectedActivityNotes) {
		boolean flag = false;
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		
		int activityNotesCount = getDriver().findElements(By.xpath(activityNotesIFrame)).size();
		
		for (int i = 1; i <= activityNotesCount; i++) {
			commonWaits.switchtoDefaultFrame();
			switchToPWAFrame();
			
			commonWaits.switchToFrame(By.xpath("(" + activityNotesIFrame + ")[" + i + "]"));
			
			java.util.List<WebElement> matchingCards = getDriver().findElements(By.xpath(activityNotes));
			
			if (!matchingCards.isEmpty()) {
				if (matchingCards.get(0).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).contains(expectedActivityNotes)) {
					return true;
				}
			}
		}
		return flag;
	}
    /**
     * Verify activity notes on Request page
     * Activity notes are NOT inside any iframe - they're in the main page content on the right side panel
     * @param expectedActivityNotes The expected activity text to verify
     * @return true if activity notes contain expected text
     */
    public boolean verifyRequestActivityNotes(String expectedActivityNotes) {
        log.info("Verifying activity notes on Request page for: " + expectedActivityNotes);
        // Activity notes are NOT inside any iframe - switch to default frame
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        try {
            // Try activity-feed-item locator first
            String xpath1 = String.format(requestActivityFeedItemXpath, expectedActivityNotes);
            if (commonWaits.isElementPresent(By.xpath(xpath1))) {
                log.info("Activity notes found with expected text: " + expectedActivityNotes);
                return true;
            }

            // Try feed-item__title locator
            String xpath2 = String.format(requestFeedItemTitleXpath, expectedActivityNotes);
            if (commonWaits.isElementPresent(By.xpath(xpath2))) {
                log.info("Activity notes found in feed-item__title: " + expectedActivityNotes);
                return true;
            }

            // Try generic feed-item locator
            String xpath3 = String.format(requestFeedItemContentXpath, expectedActivityNotes);
            if (commonWaits.isElementPresent(By.xpath(xpath3))) {
                log.info("Activity notes found in feed-item: " + expectedActivityNotes);
                return true;
            }

            log.warn("Activity notes not found with expected text: " + expectedActivityNotes);
            return false;
        } catch (Exception e) {
            log.error("Error verifying activity notes: " + e.getMessage());
            return false;
        }
    }

    public String getStatusOnViewPage() {
        refreshTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        log.info("-------------- Getting status on view page ---------");
        try {
            String status = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementByIDARData, statusARID))).getText();
            log.info("Retrieved status on view page: " + status);
            return status;
        } catch (Exception e) {
            log.error("Failed to get status on view page: " + e.getMessage());
            throw e;
        }
    }

    public ViewIncidentPage validatePCA_CardForAiopsServiceHealth() {
        log.info("Getting PCA Card Service Health Probability");
        
        // Switch to incident view frame
        commonWaits.switchtoDefaultFrame();
        switchToFrame(By.xpath(IFrameViewIncident));
        
        // Click on PCA Card button to expand the card
        log.info("Clicking on PCA Card button");
        commonWaits.waitForElementClickable(pcaCardButton);
        commonWaits.clickWithoutInterception(pcaCardButton);
        
        // Wait for probability element to be displayed
        switchToFrame(By.xpath(iFrameProbableCause));
        log.info("Waiting for PCA Card Service Health Probability to be displayed");
        scrollToElement(pcaCardServiceHealthProbability);
        commonWaits.waitForElementVisible(pcaCardServiceHealthProbability, 40);
        log.info("PCA Card AIOPS service health card is getting displayed");
        
        // Get the probability percentage text
        commonWaits.waitForElementVisible(pcaCardServiceHealthProbabilityPercent, 10);
        String probabilityText = pcaCardServiceHealthProbabilityPercent.getText().trim();
        log.info("PCA Card Probability text: " + probabilityText);
        
        String probabilityValue = probabilityText.replace("%", "").trim();
        int probability = Integer.parseInt(probabilityValue);
        String impactedService = impactedServiceName.getText().trim();
        log.info("PCA Card Probability value is: " + probability + " for Impacted Service: "+ impactedService);
        
        return this;
    }


	// ==================== TASK METHODS ====================
	
	/**
	 * Clicks on Tasks tab in the incident view page
	 */
	public ViewIncidentPage clickOnTasksTab() {
		log.info("Clicking on Tasks tab");
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForElementVisible(tasksTab);
		commonWaits.clickWithoutInterception(tasksTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Gets all task links from the Tasks tab
	 * @return List of task link WebElements
	 */
	public java.util.List<org.openqa.selenium.WebElement> getAllTaskLinks() {
		log.info("Getting all task links from Tasks tab");
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return getDriver().findElements(By.xpath(taskLinkXpath));
	}
	
	/**
	 * Gets count of tasks
	 */
	public int getTaskCount() {
		return getAllTaskLinks().size();
	}
	
	/**
	 * Clicks on a task link by index (0-based)
	 */
	public ViewIncidentPage clickOnTaskByIndex(int index) {
		log.info("Clicking on task at index: " + index);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		java.util.List<org.openqa.selenium.WebElement> taskLinks = getDriver().findElements(By.xpath(taskLinkXpath));
		if (index < taskLinks.size()) {
			commonWaits.clickWithoutInterception(taskLinks.get(index));
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		} else {
			throw new IndexOutOfBoundsException("Task index " + index + " is out of bounds. Total tasks: " + taskLinks.size());
		}
		return this;
	}
	
	/**
	 * Updates task status to the specified status
	 * Note: Task page structure - clicking StatusDownArrow opens the panel, then click on Status label to open dropdown
	 */
	public ViewIncidentPage updateTaskStatus(String newStatus) {
		log.info("Updating task status to: " + newStatus);
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFrameViewIncident));
		
		// Click on Status down arrow to expand the status section
		commonWaits.waitForByElementToBeVisible(By.xpath(StatusDownArrow));
		commonWaits.clickWithoutInterception(By.xpath(StatusDownArrow));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(30,3);
		
		 //Try to find and click the status dropdown - use multiple locator strategies
		By statusDropdownLocator = null;	
		if(commonWaits.isElementPresent(By.xpath(statusDropdown))) {
			statusDropdownLocator = By.xpath(statusDropdown);
		}

		if (statusDropdownLocator != null) {
			commonWaits.clickWithoutInterception(statusDropdownLocator);
		}
			
		// Select the status option
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(statusOptionButton, newStatus)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(statusOptionButton, newStatus)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(30,3);
		return this;
	}
	
	/**
	 * Saves the task (reuses same save button as incident)
	 */
	public ViewIncidentPage saveTask() {
		log.info("Saving task");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathById, saveIncidentBox)));
		click(By.xpath(String.format(elementXpathById, saveIncidentBox)));
		commonWaits.waitForPageFullyLoaded(30, 3);	
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Navigates back to the incident page from task page
	 */
	public ViewIncidentPage navigateBackToIncident() {
		log.info("Navigating back to incident page");
		getDriver().navigate().back();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	/**
	 * Closes all tasks one by one
	 * @param closedStatus The status to set for closing (e.g., "Closed")
	 */
	public ViewIncidentPage closeAllTasks(String closedStatus) {
		log.info("Closing all tasks with status: " + closedStatus);
		clickOnTasksTab();
		int taskCount = getTaskCount();
		log.info("Total tasks to close: " + taskCount);
		
		for (int i = 0; i < taskCount; i++) {
			log.info("Closing task " + (i + 1) + " of " + taskCount);
			clickOnTasksTab();  // Ensure we're on Tasks tab
			clickOnTaskByIndex(i);
			updateTaskStatus(closedStatus);
			saveTask();
			navigateBackToIncident();
		}
	log.info("All " + taskCount + " tasks closed successfully");
	return this;
}

/**
 * Verify category prediction activity notes with retry patterns
 * Tries the expected activity note first, then falls back to alternate patterns
 * @param expectedActivityNote Primary expected activity note text
 * @return true if activity note found with any pattern, false otherwise
 */
public boolean verifyCategoryPredictionActivityNotes(String expectedActivityNote) {
	log.info("Verifying category prediction activity notes");
	boolean activityNoteFound = false;
	
	try {
		// Try primary expected activity note
		if (verifyActivityNotes(expectedActivityNote)) {
			activityNoteFound = true;
			log.info("Successfully verified activity note contains: " + expectedActivityNote);
			return true;
		}
	} catch (AssertionError e) {
		log.info("Activity note with '" + expectedActivityNote + "' not found, checking alternate patterns");
	}
	
	// Try alternate patterns for category prediction activity notes
	String[] alternatePatterns = {
		"Category.*Helix Cognitive",
		"Operational Category.*cognitive",
		"Category.*AI Service",
		"Category.*Helix AI",
		"added by Helix Cognitive"
	};
	
	for (String pattern : alternatePatterns) {
		try {
			if (verifyActivityNotes(pattern)) {
				activityNoteFound = true;
				log.info("Successfully verified activity note matches pattern: " + pattern);
				return true;
			}
		} catch (AssertionError ae) {
			log.info("Pattern '" + pattern + "' not matched, trying next");
		}
	}
	
	if (!activityNoteFound) {
		log.error("No activity note found for category prediction with any pattern");
	}
	
	return activityNoteFound;
}

	public ViewIncidentPage clickOnEditButton() {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Click on Edit Button");
		commonWaits.clickWithoutInterception(editButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}

	public String getSystemAlertMessage() {
		commonWaits.switchtoDefaultFrame();
		log.info("Getting System Alert Message");
		commonWaits.waitForElementVisible(systemAlertMessage,5);
		return systemAlertMessage.getText().trim();
	}

	public ViewIncidentPage setSupportGroupAndPersonName(String supportGroupName, String personName) {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Setting Support Group and Person Name");
		commonWaits.waitForElementVisible(SupportGroupTextbox);
		SupportGroupTextbox.clear();
		SupportGroupTextbox.sendKeys(supportGroupName);
		commonWaits.waitForExecutionComplete();
		SupportGroupTextbox.sendKeys(Keys.ENTER);
		commonWaits.waitForElementVisible(PersonNameTextbox);
		PersonNameTextbox.clear();
		PersonNameTextbox.sendKeys(personName);
		commonWaits.waitForExecutionComplete();
		PersonNameTextbox.sendKeys(Keys.ENTER);
		Actions actions = new Actions(getDriver());
        actions.moveToElement(getDriver().findElement(By.tagName("body")), 0, 0)
       .moveByOffset(10, 10)
       .click()
       .perform();
		return this;
	}

	public ViewIncidentPage setSupportGroupAndPersonNameInCommunicationCoOrdinator(String supportGroupName, String personName) {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Setting Support Group and Person Name");
		commonWaits.waitForElementVisible(SupportGroupTextboxCommunicationCoOrdinator);
		SupportGroupTextboxCommunicationCoOrdinator.clear();
		SupportGroupTextboxCommunicationCoOrdinator.sendKeys(supportGroupName);
		commonWaits.waitForExecutionComplete();
		SupportGroupTextboxCommunicationCoOrdinator.sendKeys(Keys.ENTER);
		commonWaits.waitForElementVisible(PersonNameTextboxCommunicationCoOrdinator);
		PersonNameTextboxCommunicationCoOrdinator.clear();
		PersonNameTextboxCommunicationCoOrdinator.sendKeys(personName);
		commonWaits.waitForExecutionComplete();
		PersonNameTextboxCommunicationCoOrdinator.sendKeys(Keys.ENTER);
		return this;
	} 

	public String openMajorIncidentReportAndVerifyINCDetails() {	
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Clicking Open Major Incident Report");
		refreshTicket();
		commonWaits.waitForElementVisible(viewReportLink);
		commonWaits.clickWithoutInterception(viewReportLink);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForElementVisible(INCIDInDetails);
		return INCIDInDetails.getText();
	}

	public ViewIncidentPage MakeATextBoldInReport() {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Making text bold in Major Incident report");
		commonWaits.waitForElementVisible(majorIncidentReportEditor);
		commonWaits.doubleClick(getDriver(), majorIncidentReportEditor);
		majorIncidentReportEditor.sendKeys(Keys.chord(Keys.CONTROL, "a"));

		// Click on Bold toolbar button
		commonWaits.clickWithoutInterception(textBoldButton);
		commonWaits.clickWithoutInterception(SaveButtonOnReport);
		return this;
	}

	public boolean isReportTextBold() {

		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
	
		WebElement strongElement = commonWaits
				.waitForElementVisible(majorIncidentReportEditorBold);
	
		String fontWeight = strongElement.getCssValue("font-weight");
		log.info("Major Incident report text font weight: " + fontWeight);
	
		if (fontWeight == null) {
			return false;
		}
	
		fontWeight = fontWeight.trim();
	
		// Handle string values like "bold"
		if (fontWeight.equalsIgnoreCase("bold") ||
			fontWeight.equalsIgnoreCase("bolder")) {
			return true;
		}
	
		// Handle numeric values like 600, 700
		try {
			int numericWeight = Integer.parseInt(fontWeight);
			return numericWeight >= 600;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	

	public void verifyReportTextIsBold() {
		Assert.assertTrue(isReportTextBold(), "Major Incident report text is not bold as expected.");
	}

	public ViewIncidentPage closeButtonOnReport() {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Clicking Close Button on Report");
		commonWaits.waitForElementVisible(closeButtonOnReport);
		commonWaits.clickWithoutInterception(closeButtonOnReport);
		return this;
	}

	public void verifyPrintOption() {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Clicking Download Major Incident Report using Print Option");
	    Assert.assertTrue(commonWaits.isElementPresent(printOption), "Print Option is not present");
		Assert.assertTrue(commonWaits.isElementClickable(printOption), "Print Option is not clickable");
	}

	public String getAllSummary() {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Getting all GPT response string");
		commonWaits.waitForElementVisible(summary);
		return summary.getText().trim();
	}

  public By relatedTicketLinkById(String ticketId) {
        String safe = (ticketId == null) ? "" : ticketId.trim().replace("'", "");
        // "related-ticket-id" is used across SmartIT for related ticket links (incl. BWF case cross-launch)
        return By.xpath("//a[@ux-id='related-ticket-id' and contains(normalize-space(.),'" + safe + "')]");
    }

    public By relatedTicketRowById(String ticketId) {
        // Try common container patterns: table row, grid row, or any block that contains the link
        String safe = (ticketId == null) ? "" : ticketId.trim().replace("'", "");
        return By.xpath(
                "//*[self::tr or self::div][.//a[@ux-id='related-ticket-id' and contains(normalize-space(.),'" + safe + "')]][1]"
        );
    }

    public WebElement findRelatedTicketRowOrFail(String ticketId) {
        switchToIncidentPwaFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        // Navigate to Related items tab (best-effort fallback supported by existing locators)
        try {
            commonWaits.waitForElementVisible(relatedItemsTab);
            commonWaits.clickWithoutInterception(relatedItemsTab);
        } catch (Exception ignored) {
            commonWaits.waitForByElementToBeVisible(By.xpath(relateditemTab));
            commonWaits.clickWithoutInterception(By.xpath(relateditemTab));
        }

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForByElementToBeVisible(relatedTicketLinkById(ticketId));
        return commonWaits.waitForByElementToBeVisible(relatedTicketRowById(ticketId));
    }

    public ViewIncidentPage verifyRelatedTicketRelationshipType(String relatedTicketId, String expectedRelationshipType) {
        Assert.assertNotNull(relatedTicketId, "Related ticket id is null");
        Assert.assertNotNull(expectedRelationshipType, "Expected relationship type is null");

        WebElement row = findRelatedTicketRowOrFail(relatedTicketId);
        String rowText = "";
        try {
            rowText = row.getText();
        } catch (Exception ignored) {
        }

        Assert.assertTrue(
                rowText != null && rowText.contains(expectedRelationshipType),
                "Relationship type mismatch for related ticket '" + relatedTicketId + "'. Expected contains: '"
                        + expectedRelationshipType + "' but row text was: " + rowText
        );
        return this;
    }

    public ViewIncidentPage tryDeleteRelatedTicketRelationshipExpectError(String relatedTicketId, String expectedErrorMessage) {
        Assert.assertNotNull(relatedTicketId, "Related ticket id is null");
        Assert.assertNotNull(expectedErrorMessage, "Expected error message is null");

        WebElement row = findRelatedTicketRowOrFail(relatedTicketId);

        // Attempt to open row action menu (kebab/more). UI varies by tenant/version.
        By[] menuCandidates = new By[] {
                By.xpath(".//button[contains(@aria-label,'More') or contains(@title,'More')]"),
                By.xpath(".//button[contains(@class,'d-icon-more') or contains(@class,'more') or contains(@class,'menu')]"),
                By.xpath(".//*[@ux-id='more' or @ux-id='more-actions' or @ux-id='actions']"),
        };

        boolean menuOpened = false;
        for (By by : menuCandidates) {
            try {
                List<WebElement> els = row.findElements(by);
                if (!els.isEmpty() && els.get(0).isDisplayed()) {
                    clickByAction(els.get(0));
                    commonWaits.waitForExecutionComplete();
                    menuOpened = true;
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        Assert.assertTrue(menuOpened, "Could not open related ticket row actions menu for: " + relatedTicketId);

        // Attempt delete/remove relationship action (label varies)
        By[] deleteCandidates = new By[] {
                By.xpath("//*[self::button or self::a][normalize-space()='Remove relationship' or normalize-space()='Remove Relationship']"),
                By.xpath("//*[self::button or self::a][contains(normalize-space(.),'Remove') and contains(normalize-space(.),'relationship')]"),
                By.xpath("//*[self::button or self::a][normalize-space()='Delete relationship' or normalize-space()='Delete Relationship']"),
                By.xpath("//*[self::button or self::a][contains(normalize-space(.),'Delete') and contains(normalize-space(.),'relationship')]"),
                By.xpath("//*[self::button or self::a][normalize-space()='Remove' or normalize-space()='Delete']"),
        };

        boolean clickedDelete = false;
        for (By by : deleteCandidates) {
            try {
                if (commonWaits.isElementPresent(by, 2)) {
                    commonWaits.clickWithoutInterception(by);
                    commonWaits.waitForExecutionComplete();
                    clickedDelete = true;
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        Assert.assertTrue(clickedDelete, "Delete/Remove relationship action not found for related ticket: " + relatedTicketId);

        // Confirm if confirmation dialog appears (best-effort)
        try {
            By confirm = By.xpath("//button[normalize-space()='Yes' or normalize-space()='OK' or normalize-space()='Ok' or normalize-space()='Confirm']");
            if (commonWaits.isElementPresent(confirm, 2)) {
                commonWaits.clickWithoutInterception(confirm);
                commonWaits.waitForExecutionComplete();
            }
        } catch (Exception ignored) {
        }

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        // Validate error surfaced (system alert/toast text can appear under ux-id system-alert-text)
        boolean found = false;
        try {
            for (WebElement el : systemAlertTexts) {
                try {
                    String t = (el == null) ? "" : el.getText();
                    if (t != null && t.contains(expectedErrorMessage)) {
                        found = true;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        // Fallback: scan page for expected text
        if (!found) {
            found = commonWaits.isElementPresent(By.xpath("//*[contains(normalize-space(.),'" + expectedErrorMessage.replace("'", "") + "')]"), 5);
        }

        Assert.assertTrue(found, "Expected error message not found after delete attempt. Expected: " + expectedErrorMessage);
        return this;
    }

  public ViewIncidentPage setSupportGroupAndPersonNameForAssignee(String supportGroupName, String personName) {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Setting Support Group and Person Name");
		commonWaits.waitForElementVisible(SupportGroupTextboxForAssignee);
		SupportGroupTextboxForAssignee.clear();
		SupportGroupTextboxForAssignee.sendKeys(supportGroupName);
		commonWaits.waitForExecutionComplete();
		SupportGroupTextboxForAssignee.sendKeys(Keys.ENTER);
		commonWaits.waitForElementVisible(PersonNameTextboxForAssignee);
		PersonNameTextboxForAssignee.clear();
		PersonNameTextboxForAssignee.sendKeys(personName);
		commonWaits.waitForExecutionComplete();
		PersonNameTextboxForAssignee.sendKeys(Keys.ENTER);
		Actions actions = new Actions(getDriver());
        actions.moveToElement(getDriver().findElement(By.tagName("body")), 0, 0)
       .moveByOffset(10, 10)
       .click()
       .perform();
		return this;
	}

	public ViewIncidentPage unflagTicket() {
		commonWaits.switchtoDefaultFrame();
		switchToPWAFrame();
		log.info("Unflagging ticket");
		commonWaits.waitForElementVisible(unflagTicketButton);
		commonWaits.clickWithoutInterception(unflagTicketButton);
		return this;
	}
  
    public ViewIncidentPage openRelatedTicketInNewTab(String relatedTicketId) {
        Assert.assertNotNull(relatedTicketId, "Related ticket id is null");
        WebElement row = findRelatedTicketRowOrFail(relatedTicketId);
        WebElement link = row.findElement(By.xpath(".//a[@ux-id='related-ticket-id' and contains(normalize-space(.),'" + relatedTicketId.trim().replace("'", "") + "')]"));
        new CommonMethods().openLinkInNewTab(link);
        commonWaits.waitForExecutionComplete();
        commonWaits.switchToLastWindow();
        return this;
    }
}