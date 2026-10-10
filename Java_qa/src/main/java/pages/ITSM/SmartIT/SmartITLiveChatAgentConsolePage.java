package pages.ITSM.SmartIT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

public class SmartITLiveChatAgentConsolePage extends BasePage {

	private static final SolqaLogger log = new SolqaLogger(SmartITLiveChatAgentConsolePage.class.getName());
	private final CommonWaits commonWaits = new CommonWaits();

	public SmartITLiveChatAgentConsolePage() {
		PageFactory.initElements(getDriver(), this);
	}

	//WebElements
	@FindBy(xpath = "//button[@ux-id = 'createTicketBtn-dropdown' ]")
	public WebElement clickCreateIncidentButton;

	@FindBy(xpath = "//div[@ux-id = 'createIncidentBtn' ]")
	public WebElement createIncidentBtn;

	@FindBy(xpath = "//div[@ux-id = 'createWorkOrderBtn' ]")
	public WebElement createWorkOrderBtn;

	@FindBy(xpath = "//div[@class='system-alert__dismiss']/i[@class='icon-cross']")
	List<WebElement> systemAlertCloseBtn;

	@FindBy(xpath = "//a[text() = 'Resources']")
	public WebElement resourceTab;

	@FindBy(xpath = "//div[@ux-id='liveChatRightPane']//a[@ux-id='liveChatTicket_incident']")
	public WebElement resourcesIncidentId;

	@FindBy(xpath = "//a[@ux-id='liveChatTicket_workorder']")
	public WebElement resourcesWOId;

	@FindBy(xpath = "//button[@ux-id='popup-btn_0']")
	public WebElement BTN_CONFIRM_YES;

	@FindBy(xpath = "//*[@role='dialog' and .//*[contains(normalize-space(),'Warning')]]//button[normalize-space()='Yes']")
    public WebElement navWarningDialog;

	// -------------------- Locators (keep all at top) --------------------
	private static final String IFRAME_LIVE_CHAT = "//iframe[@id='liveChatFrame' or contains(@class,'ux-live-chat-iframe')]";

	// Tabs (SmartIT Live Chat UI varies; keep multiple safe fallbacks)
	private static final String[] TAB_CHATS_XPATHS = new String[]{
			"//a[.//span[contains(@class,'nav-link-title') and normalize-space()='Chats']]",
			"//button[.//span[contains(@class,'nav-link-title') and normalize-space()='Chats']]",
			"//*[@role='tablist']//*[normalize-space()='Chats']",
			"//a[.//*[normalize-space()='Chats'] or normalize-space()='Chats']",
			"//*[@role='tablist']//*[normalize-space()='Chat']",
			"//a[.//*[normalize-space()='Chat'] or normalize-space()='Chat']"
	};
	private static final String[] TAB_QUEUE_XPATHS = new String[]{
			"//a[.//span[contains(@class,'nav-link-title') and normalize-space()='Queue']]",
			"//button[.//span[contains(@class,'nav-link-title') and normalize-space()='Queue']]",
			"//*[@role='tablist']//*[normalize-space()='Queue']",
			"//a[.//*[normalize-space()='Queue'] or normalize-space()='Queue']"
	};

	private static final String[] TAB_Transferred_XPATHS = new String[]{
		"//*[@role='tablist']//*[normalize-space()='Transferred']"
};
	private static final String TOGGLE_AUTO_REFRESH = "//button[contains(@aria-label,'Auto refresh') or contains(@ux-id,'autoRefresh') or contains(@id,'autoRefresh')]";

	private static final String QUEUE_TIME_SORT_BTN = "//button[contains(@aria-label,'Queue time Sort')]";

	private static final String CUSTOMER_QUEUE_ROW_BY_MESSAGE =
			"(//div[contains(normalize-space(),'%s')]/ancestor::div[contains(@class,'cb-header')]/following-sibling::div//div[contains(normalize-space(),'%s')])[last()]";

	private static final String CHAT_TEXT_QUESTION = "//*[@ux-id='chatTextQuestion' and contains(normalize-space(),'%s')]";
	private static final String CHAT_TEXT_QUESTION_ALL = "//*[@ux-id='chatTextQuestion']";
	// Queue row locators vary by stack; keep a small set of resilient fallbacks
	private static final String[] QUEUE_CHAT_ROW_XPATHS = new String[] {
			CHAT_TEXT_QUESTION_ALL,
			"//*[contains(@ux-id,'chatTextQuestion')]",
			"//*[@ux-id='queueTextQuestion' or contains(@ux-id,'queueTextQuestion')]",
			"//*[contains(@class,'chatTextQuestion')]"
	};
	private static final String CHAT_ROW_ANCESTOR = "./ancestor::*[self::div or self::tr][1]";
	private static final String CHAT_ROW_ACTION_DROPDOWN =
			"(.//button[contains(@id,'chatActions') or contains(@ux-id,'chatActions') or contains(@aria-label,'Actions')])[1]";

	private static final String BTN_ACCEPT_CHAT = "//button[@id='acceptChatBtn' or @ux-id='acceptChatBtn']";
	private static final String BTN_END_CHAT = "//button[@ux-id='endChatBtn']";
	private static final String BTN_CHAT_ACTIONS = "//button[@ux-id='chatActionsDropDown' or @id='chatActionsDropDownMenu']";
	private static final String ACTION_IN_DROPDOWN = "//div[contains(@aria-labelledby,'chatActions') or @aria-labelledby='chatActionsDropDownMenu']//button[contains(normalize-space(),'%s')]";
	private static final String BTN_LEAVE_CHAT = "//button[contains(@aria-label,'Leave chat') or contains(@title,'Leave chat') or normalize-space()='Leave chat' or .//*[normalize-space()='Leave chat']]";

	// Attachments (shared UI across stacks)
	private static final String BTN_ATTACH_PIN = "//button[@id='upload-attachmentBtn' or contains(@id,'upload-attachment')]";
	private static final String INPUT_FILE_UPLOADER = "//adapt-rx-uploader/input[@type='file'] | //adapt-rx-uploader//input[@type='file']";
	private static final String TXT_FILES_ADDED_TITLE = "//span[contains(@class,'adapt-uploader-file-name')]";
	private static final String BTN_UPLOAD_ATTACHMENT = "//button[@id='upload-attachment-button' or contains(@id,'upload-attachment')]";
	private static final String ATTACHMENT_DOWNLOAD_LINK = "//a[@download='%s']";

	// Navigation warning dialog (blocks clicks on tabs like Queue)
	private static final String[] NAV_WARNING_DIALOG_XPATHS = new String[]{
			"//*[@role='dialog' and .//*[contains(normalize-space(),'Warning')]]",
			"//*[contains(@class,'modal') and .//*[contains(normalize-space(),'Warning')]]",
			"//*[contains(@class,'dialog') and .//*[contains(normalize-space(),'Warning')]]"
	};
	private static final String NAV_WARNING_YES_BUTTON = ".//button[normalize-space()='Yes' or .//*[normalize-space()='Yes']]";
	private static final String NAV_WARNING_NO_BUTTON = ".//button[normalize-space()='No' or .//*[normalize-space()='No']]";

	// Transfer chat dialog (UI varies across stacks; keep resilient fallbacks)
	private static final String[] TRANSFER_DIALOG_XPATHS = new String[]{
			"//*[@role='dialog' and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'transfer')]]",
			"//*[contains(@class,'modal') and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'transfer')]]",
			"//*[contains(@class,'dialog') and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'transfer')]]"
	};
	private static final String TRANSFER_AGENT_BUTTON_CONTAINS_TEXT = ".//button[contains(normalize-space(.),'%s')]";
	private static final String TRANSFER_AGENT_TEXT_TO_BUTTON =
			"(.//*[self::span or self::div][contains(normalize-space(.),'%s')]/ancestor::button[1])[1]";
	private static final String[] TRANSFER_CONFIRM_BUTTON_XPATHS = new String[]{
			".//button[normalize-space()='Transfer' or .//*[normalize-space()='Transfer']]",
			".//button[normalize-space()='Confirm' or .//*[normalize-space()='Confirm']]",
			".//button[normalize-space()='OK' or .//*[normalize-space()='OK']]"
	};

	private static final String TXT_AGENT_NAME = "//span[@ux-id='agentScreenName']";
	private static final String TXTAREA_AGENT_MESSAGE = "//textarea[@id='agent-input-textarea' and contains(@class,'form-control')]";

	private static final String CHAT_MESSAGE_TEXT = "//span[contains(normalize-space(),'%s')]";
	// Some stacks vary in casing/prefix ("System Message:"), so match case-insensitively.
	private static final String ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String SYSTEM_MESSAGE_TEXT =
			"//*[contains(@class,'defaultsysmsg') and " +
					"contains(translate(normalize-space(.),'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'%s')]";
	private static final String INVITEE_MONITOR_ICON = "//*[contains(@class,'monitor') or contains(@aria-label,'Monitor') or contains(@title,'Monitor')]";
	private static final String INVITEE_PARTICIPANT_ICON = "//*[contains(@class,'participant') or contains(@aria-label,'Participate') or contains(@title,'Participate')]";

	// Knowledge / Resources tab (for HKM article sharing)
	private static final String[] TAB_RESOURCES_XPATHS = new String[]{
			// Tab labels can vary by stack: Resources / Knowledge / Articles
			"//*[@role='tablist']//*[self::span or self::div][normalize-space()='Resources' or normalize-space()='Resource' or normalize-space()='Knowledge' or normalize-space()='Articles']"
					+ "/parent::*[self::button or self::a]",
			"//*[@role='tablist']//*[self::button or self::a][contains(translate(normalize-space(.),'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'resource')"
					+ " or contains(translate(normalize-space(.),'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'knowledge')"
					+ " or contains(translate(normalize-space(.),'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'article')]",
			"//a[.//*[normalize-space()='Resources' or normalize-space()='Knowledge' or normalize-space()='Articles'] or normalize-space()='Resources' or normalize-space()='Knowledge' or normalize-space()='Articles']",
			"//button[.//*[normalize-space()='Resources' or normalize-space()='Knowledge' or normalize-space()='Articles'] or normalize-space()='Resources' or normalize-space()='Knowledge' or normalize-space()='Articles']",
			"//*[@ux-id='resourcesTab' or @id='resourcesTab' or contains(@ux-id,'resource') or contains(@id,'resource') or contains(@ux-id,'knowledge') or contains(@id,'knowledge')]",
			"//*[self::button or self::a][contains(@aria-label,'Resource') or contains(@aria-label,'Resources') or contains(@aria-label,'Knowledge') or contains(@title,'Resource') or contains(@title,'Knowledge')]"
	};
	private static final String[] RESOURCE_SEARCH_INPUT_XPATHS = new String[]{
			// Search box can be global within Resources panel
			"//input[contains(@placeholder,'Search') or contains(@aria-label,'Search') or contains(@name,'search') or contains(@id,'search')]",
			"//adapt-rx-search//input",
			"//adapt-search//input",
			"//input[contains(@placeholder,'article') or contains(@aria-label,'article')]"
	};
	// Resource list items render differently across stacks; match by visible title text (e.g. "Windows Blue Screen of Death").
	private static final String RESOURCE_TITLE_CONTAINS_TEXT =
			"//*[self::a or self::span or self::div]" +
					"[contains(@class,'rx-ellipsis') or self::a or self::span]" +
					"[contains(normalize-space(.),'%s')]";

	// Share action is typically an "arrow" icon button to the left of an article row.
	private static final String RESOURCE_SHARE_ARROW_FROM_TITLE =
			"(" +
					"%s/ancestor::*[self::div or self::li][1]" +
					"//button[" +
					"contains(@aria-label,'Share') or contains(@title,'Share')" +
					" or contains(@aria-label,'Send') or contains(@title,'Send')" +
					" or contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'share')" +
					" or contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'arrow')" +
					" or contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'forward')" +
					"]" +
					" | " +
					"%s/ancestor::*[self::div or self::li][1]" +
					"//*[self::adapt-icon or self::i or self::span]" +
					"[contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'arrow')" +
					" or contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'share')]" +
					"/ancestor::*[self::button or self::a][1]" +
					")[1]";
	private static final String SHARED_ARTICLE_IN_TRANSCRIPT =
			"//a[contains(normalize-space(.),'%s')] | //span[contains(normalize-space(.),'%s')]";

	// Live Chat -> Create Incident / Resources (Ticket link)
	@FindBy(xpath = "//*[@ux-id='createIncident']")
	public WebElement liveChatCreateIncidentButton;
	private static final String XPATH_RESOURCE_TICKET_LINK_BY_PREFIX =
			"//a" +
			"[contains(normalize-space(.),'%s')]";

	
	String gptSummary = "//app-transfer-to-agent//adapt-rx-textarea[contains(. ,'HelixGPT Chat Summary')]//textarea";
	public String switchToFrame = "//iframe[@id='liveChatFrame']";
		

	private enum UiContext { LIVE_CHAT_FRAME, DEFAULT_CONTENT }

	// -------------------- Constants (no hard-coded magic numbers) --------------------
	private static final long DEFAULT_POLL_TIMEOUT_MS = 60_000;
	private static final long DEFAULT_POLL_INTERVAL_MS = 2_000;
	private static final String MSG_INITIAL_MESSAGE_REQUIRED = "initialMessage must not be null";
	private static final String MSG_CUSTOMER_NAME_REQUIRED = "customerName must not be null";
	private static final String MSG_EXPECTED_MESSAGE_REQUIRED = "expectedMessage must not be null";
	private static final String MSG_ACTION_LABEL_REQUIRED = "actionLabel must not be null";
	private static final String MSG_MESSAGE_REQUIRED = "message must not be null";
	private static final String MSG_MONITOR_ICON_MISSING = "Monitor icon not present for invitee";
	private static final String MSG_PARTICIPANT_ICON_MISSING = "Participant icon not present for invitee";
	private static final String MSG_CHAT_ACTION_DROPDOWN_NOT_FOUND = "Chat action dropdown not found for chat: ";
	private static final String MSG_CHATS_TAB_MISSING = "Chats tab not present; continuing without tab switch";
	private static final String MSG_QUEUE_TAB_MISSING = "Queue tab not present; continuing without tab switch";
	private static final String MSG_AGENT_NAME_REQUIRED = "agentName must not be null";
	private static final String MSG_TRANSFER_DIALOG_NOT_FOUND = "Transfer dialog not found/open.";
	private static final long DIALOG_POLL_INTERVAL_MS = 1_000;
	private static final long DIALOG_POLL_TIMEOUT_MS = 30_000;
	private static final long QUEUE_CHATS_POLL_TIMEOUT_MS = 60_000;
	private static final long QUEUE_CHATS_POLL_INTERVAL_MS = 2_000;
	String transferChatBtn="//button[@ux-id='transferChatBtn']";
    String transferAgentRadioBtn="//input[@value='%s'] [contains(@id,'radiobutton')]/../..";

	// Colors are read from CSS background-color / color, depending on UI.
	private static final String CSS_BACKGROUND_COLOR = "background-color";
	private static final String LOG_PREFIX = "[SmartIT LiveChat] ";
	private static final String XPATH_NEAREST_VISIBLE_TAB_ANCESTOR = "./ancestor::a[1] | ./ancestor::button[1]";
	private static final String REGEX_SYSTEM_MESSAGE_PREFIX = "(?i)^system\\s*message\\s*:\\s*";
	private static final String SYSTEM_MESSAGE_PREFIX = "System Message: ";

	private boolean isLiveChatFramePresent() {
		try {
			return !findElements(By.xpath(IFRAME_LIVE_CHAT)).isEmpty();
		} catch (Exception ignored) {}
		return false;
	}

	private boolean clickFirstPresentInLiveChatFrame(String[] xpaths) {
		for (String xp : xpaths) {
			List<WebElement> els = findElements(By.xpath(xp));
			if (els != null && !els.isEmpty()) {
				// Prefer displayed targets; in collapsed nav the title span exists but is not visible.
				for (WebElement candidate : els) {
					WebElement target = candidate;
					try {
						if (!candidate.isDisplayed()) {
							// Click nearest visible ancestor (icon-only navigation)
							List<WebElement> ancestorTargets = candidate.findElements(
									By.xpath(XPATH_NEAREST_VISIBLE_TAB_ANCESTOR)
							);
							if (ancestorTargets != null && !ancestorTargets.isEmpty() && ancestorTargets.get(0).isDisplayed()) {
								target = ancestorTargets.get(0);
							} else {
								continue;
							}
						}
						commonWaits.clickWithoutInterception(target);
						return true;
					} catch (Exception ignored) {
						// Try next candidate/xpath
					}
				}
			}
		}
		return false;
	}

	private WebElement findTransferDialogRootOrNull() {
		for (String xp : TRANSFER_DIALOG_XPATHS) {
			List<WebElement> dialogs = findElements(By.xpath(xp));
			if (dialogs != null && !dialogs.isEmpty()) {
				return dialogs.get(0);
			}
		}
		return null;
	}

	private boolean acceptNavigationAwayWarningIfPresent() {
		// Warning dialog is typically in default content (not inside live chat iframe),
		// but we check both contexts to be safe.
		for (int pass = 0; pass < 2; pass++) {
			if (pass == 0) {
				switchtoDefaultFrame();
			} else {
				switchToLiveChatFrame();
			}

			for (String dialogXp : NAV_WARNING_DIALOG_XPATHS) {
				List<WebElement> dialogs = findElements(By.xpath(dialogXp));
				if (dialogs == null || dialogs.isEmpty()) {
					continue;
				}
				WebElement dialog = dialogs.get(0);
				// For Live Chat flows we generally want to stay in Live Chat, so prefer "No".
				List<WebElement> noBtns = dialog.findElements(By.xpath(NAV_WARNING_NO_BUTTON));
				if (noBtns != null && !noBtns.isEmpty()) {
					log.info(LOG_PREFIX + "Navigation warning detected; clicking No to stay in Live Chat.");
					commonWaits.clickWithoutInterception(noBtns.get(0));
					switchtoDefaultFrame();
					return true;
				}

				// Fallback: some stacks only render a "Yes" action.
				List<WebElement> yesBtns = dialog.findElements(By.xpath(NAV_WARNING_YES_BUTTON));
				if (yesBtns != null && !yesBtns.isEmpty()) {
					log.info(LOG_PREFIX + "Navigation warning detected; clicking Yes to continue.");
					commonWaits.clickWithoutInterception(yesBtns.get(0));
					switchtoDefaultFrame();
					return true;
				}
			}
		}
		switchtoDefaultFrame();
		return false;
	}

	private boolean tryOpenResourcesPanel() {
		// Try in live chat iframe first
		switchToLiveChatFrame();
		if (clickFirstPresentInLiveChatFrame(TAB_RESOURCES_XPATHS)) {
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
			return true;
		}
		// Some stacks render resources outside iframe
		switchtoDefaultFrame();
		if (clickFirstPresentInLiveChatFrame(TAB_RESOURCES_XPATHS)) {
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
			return true;
		}
		return false;
	}

	private WebElement findFirstSearchInputInContext() {
		for (String xp : RESOURCE_SEARCH_INPUT_XPATHS) {
			List<WebElement> inputs = findElements(By.xpath(xp));
			if (inputs == null || inputs.isEmpty()) {
				continue;
			}
			for (WebElement el : inputs) {
				try {
					if (el != null && el.isDisplayed() && el.isEnabled()) {
						return el;
					}
				} catch (Exception ignored) {}
			}
			// If none are displayed/enabled, fall back to first match
			return inputs.get(0);
		}
		return null;
	}

	private UiContext findSearchInputAndSwitchContextOrFail() {
		// Prefer iframe context first
		switchToLiveChatFrame();
		WebElement input = findFirstSearchInputInContext();
		if (input != null) {
			return UiContext.LIVE_CHAT_FRAME;
		}
		switchtoDefaultFrame();
		input = findFirstSearchInputInContext();
		if (input != null) {
			return UiContext.DEFAULT_CONTENT;
		}
		throw new AssertionError("Resources/Knowledge search input not found (checked both live chat iframe and default content).");
	}

	public SmartITLiveChatAgentConsolePage selectArticleFromResourceTab(String articleSearchKeyword) {
		Assert.assertNotNull(articleSearchKeyword, "articleSearchKeyword must not be null");
		String keyword = articleSearchKeyword.trim();
		Assert.assertFalse(keyword.isEmpty(), "articleSearchKeyword must not be empty");

		// Try to open panel, but don't hard-fail on tab absence; some stacks show search input without a tab.
		tryOpenResourcesPanel();
		findSearchInputAndSwitchContextOrFail();
		WebElement searchInput = findFirstSearchInputInContext();
		if (searchInput == null) {
			switchtoDefaultFrame();
			throw new AssertionError("Resource search input not found after context selection.");
		}

		try {
			searchInput.clear();
		} catch (Exception ignored) {}
		searchInput.sendKeys(keyword);
		searchInput.sendKeys(Keys.ENTER);

		String resultXpath = String.format(RESOURCE_TITLE_CONTAINS_TEXT, keyword);
		Assert.assertTrue(commonWaits.waitForElementPresent(By.xpath(resultXpath)),
				"Knowledge article search result not found for keyword: " + keyword);
		// Always return to default content for callers
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage shareKnowledgeArticleUnderResourceTab(String articleName) {
		Assert.assertNotNull(articleName, "articleName must not be null");
		String name = articleName.trim();
		Assert.assertFalse(name.isEmpty(), "articleName must not be empty");

		tryOpenResourcesPanel();
		findSearchInputAndSwitchContextOrFail();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);

		// Ensure the article is visible in results (some stacks require a search before Share appears)
		String articleTitleXpath = String.format(RESOURCE_TITLE_CONTAINS_TEXT, name);
		if (!commonWaits.waitForElementPresent(By.xpath(articleTitleXpath))) {
			// Try search input using the article name
			WebElement searchInput = findFirstSearchInputInContext();
			if (searchInput == null) {
				switchtoDefaultFrame();
				throw new AssertionError("Resource search input not found while attempting to search article.");
			}
			try { searchInput.clear(); } catch (Exception ignored) {}
			searchInput.sendKeys(name);
			searchInput.sendKeys(Keys.ENTER);
			Assert.assertTrue(commonWaits.waitForElementPresent(By.xpath(articleTitleXpath)),
					"Knowledge article not found in Resources after searching: " + name);
		}

		String shareBtnXpath = String.format(RESOURCE_SHARE_ARROW_FROM_TITLE, articleTitleXpath, articleTitleXpath);
		commonWaits.clickWithoutInterception(By.xpath(shareBtnXpath));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifySharedArticleOnLiveChat(String articleName) {
		Assert.assertNotNull(articleName, "articleName must not be null");
		String name = articleName.trim();
		Assert.assertFalse(name.isEmpty(), "articleName must not be empty");

		switchToLiveChatFrame();
		String xp = String.format(SHARED_ARTICLE_IN_TRANSCRIPT, name, name);
		Assert.assertTrue(commonWaits.waitForElementPresent(By.xpath(xp)),
				"Shared knowledge article not present in Live Chat transcript: " + name);
		switchtoDefaultFrame();
		return this;
	}

	private List<WebElement> findQueueChatsInCurrentContext() {
		for (String xp : QUEUE_CHAT_ROW_XPATHS) {
			List<WebElement> chats = findElements(By.xpath(xp));
			if (chats != null && !chats.isEmpty()) {
				return chats;
			}
		}
		return java.util.Collections.emptyList();
	}

	private List<WebElement> findQueueChatsInAnyContext() {
		// Prefer live chat frame (common), fallback to default content (some stacks render queue list outside iframe)
		switchToLiveChatFrame();
		List<WebElement> chats = findQueueChatsInCurrentContext();
		if (!chats.isEmpty()) {
			return chats;
		}
		switchtoDefaultFrame();
		return findQueueChatsInCurrentContext();
	}

	public SmartITLiveChatAgentConsolePage transferChatToAgentFromOpenDialog(String agentNameOrUserName) {
		Assert.assertNotNull(agentNameOrUserName, MSG_AGENT_NAME_REQUIRED);
		String agent = agentNameOrUserName.trim();
		Assert.assertFalse(agent.isEmpty(), MSG_AGENT_NAME_REQUIRED);
        
			commonWaits.waitForExecutionComplete();
			switchToFrame(switchToFrame);
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(transferAgentRadioBtn,agentNameOrUserName))).click();
			this.click(transferChatBtn);
			commonWaits.switchtoDefaultFrame();
		
		return this;
	}

	public SmartITLiveChatAgentConsolePage switchToLiveChatFrame() {
		switchtoDefaultFrame();
		// Some stacks render Live Chat console directly (no iframe)
		if (isLiveChatFramePresent()) {
			commonWaits.switchToFrame(By.xpath(IFRAME_LIVE_CHAT));
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
		return this;
	}

	public SmartITLiveChatAgentConsolePage clickChatsTab() {
		acceptNavigationAwayWarningIfPresent();
		switchToLiveChatFrame();
		boolean clicked = clickFirstPresentInLiveChatFrame(TAB_CHATS_XPATHS);
		if (!clicked) {
			// Engineering assumption: some deployments show only one view (no explicit Chats tab).
			log.info(LOG_PREFIX + MSG_CHATS_TAB_MISSING);
		}
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage clickQueueTab() {
		acceptNavigationAwayWarningIfPresent();
		// Try in iframe context (common), then fallback to default content (some stacks)
		switchToLiveChatFrame();
		boolean clicked = clickFirstPresentInLiveChatFrame(TAB_QUEUE_XPATHS);
		if (!clicked) {
			switchtoDefaultFrame();
			clicked = clickFirstPresentInLiveChatFrame(TAB_QUEUE_XPATHS);
		}
		// If a warning pops after clicking (overlay), accept it and retry once.
		if (!clicked && acceptNavigationAwayWarningIfPresent()) {
			switchToLiveChatFrame();
			clicked = clickFirstPresentInLiveChatFrame(TAB_QUEUE_XPATHS);
			if (!clicked) {
				switchtoDefaultFrame();
				clicked = clickFirstPresentInLiveChatFrame(TAB_QUEUE_XPATHS);
			}
		}
		if (!clicked) {
			log.info(LOG_PREFIX + MSG_QUEUE_TAB_MISSING);
		}
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage clickQueueTimeSortToChangePrecedingOrder() {
		switchToLiveChatFrame();
		commonWaits.clickWithoutInterception(By.xpath(QUEUE_TIME_SORT_BTN));
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage toggleAutoRefresh() {
		clickChatsTab();
		switchToLiveChatFrame();
		if (!findElements(By.xpath(TOGGLE_AUTO_REFRESH)).isEmpty()) {
			commonWaits.clickWithoutInterception(By.xpath(TOGGLE_AUTO_REFRESH));
		}
		switchtoDefaultFrame();
		return this;
	}

	public String getLiveAgentName() {
		switchToLiveChatFrame();
		String agentName = commonWaits.waitForByElementToBeVisible(By.xpath(TXT_AGENT_NAME)).getText().trim();
		switchtoDefaultFrame();
		return agentName;
	}

	public SmartITLiveChatAgentConsolePage selectCustomerFromChatQueue(String initialMessage, String customerName) {
		Assert.assertNotNull(initialMessage, MSG_INITIAL_MESSAGE_REQUIRED);
		Assert.assertNotNull(customerName, MSG_CUSTOMER_NAME_REQUIRED);

		String rowXpath = String.format(CUSTOMER_QUEUE_ROW_BY_MESSAGE, customerName, initialMessage);
		switchToLiveChatFrame();
		try {
			WebElement row = commonWaits.waitForByElementToBeVisible(By.xpath(rowXpath));
			javaScriptScrollIntoView(row);
			clickByAction(row);
		} catch (Exception e) {
			// Fallback: select the latest (bottom-most) chat from Queue list.
			log.info(LOG_PREFIX + "Queue row not found by message/customer; selecting latest chat from queue. Reason: " + e.getMessage());
			selectLatestChatFromQueue();
		}
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage selectLatestChatFromQueue() {
		clickQueueTab();

		List<WebElement> chats = java.util.Collections.emptyList();
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < QUEUE_CHATS_POLL_TIMEOUT_MS) {
			acceptNavigationAwayWarningIfPresent();
			chats = findQueueChatsInAnyContext();
			if (!chats.isEmpty()) {
				break;
			}
			commonWaits.waitForSometime(QUEUE_CHATS_POLL_INTERVAL_MS);
		}

		Assert.assertFalse(chats.isEmpty(), "No chats found in Queue.");

		// Chat is expected to be at the end of the list (latest)
		WebElement last = chats.get(chats.size() - 1);
		try {
			javaScriptScrollIntoView(last);
		} catch (Exception ignored) {}
		clickByAction(last);

		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage clickAcceptChat() {
		acceptNavigationAwayWarningIfPresent();
		switchToLiveChatFrame();
		commonWaits.clickWithoutInterception(By.xpath(BTN_ACCEPT_CHAT));
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage typeReplyToCustomer(String message) {
		Assert.assertNotNull(message, MSG_MESSAGE_REQUIRED);
		switchToLiveChatFrame();
		WebElement input = commonWaits.waitForByElementToBeVisible(By.xpath(TXTAREA_AGENT_MESSAGE));
		input.sendKeys(message);
		input.sendKeys(Keys.ENTER);
		switchtoDefaultFrame();
		verifyMessageOnLiveChat(message);
		return this;
	}

	public SmartITLiveChatAgentConsolePage uploadAttachment(File file) {
		Assert.assertNotNull(file, "file must not be null");
		switchToLiveChatFrame();

		// Open upload UI if present
		if (!findElements(By.xpath(BTN_ATTACH_PIN)).isEmpty()) {
			commonWaits.clickWithoutInterception(By.xpath(BTN_ATTACH_PIN));
		}

		commonWaits.waitForSometime(commonWaits.minDelay);
		WebElement uploader = commonWaits.waitForByElementToBeVisible(By.xpath(INPUT_FILE_UPLOADER));
		try {
			// Some stacks hide the input; make it visible to sendKeys safely.
			executeJS("arguments[0].style.height='auto'; arguments[0].style.visibility='visible';", uploader);
		} catch (Exception ignored) {}
		uploader.sendKeys(file.getAbsolutePath());

		commonWaits.waitForByElementToBeVisible(By.xpath(TXT_FILES_ADDED_TITLE));
		commonWaits.clickWithoutInterception(By.xpath(BTN_UPLOAD_ATTACHMENT));
		commonWaits.waitForSometime(commonWaits.moreDelay);

		verifyAttachmentIsShared(file.getName());
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifyAttachmentIsShared(String attachmentFileName) {
		Assert.assertNotNull(attachmentFileName, "attachmentFileName must not be null");
		switchToLiveChatFrame();
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ATTACHMENT_DOWNLOAD_LINK, attachmentFileName)));
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifyMessageOnLiveChat(String expectedMessage) {
		Assert.assertNotNull(expectedMessage, MSG_EXPECTED_MESSAGE_REQUIRED);
		String msgXpath = String.format(CHAT_MESSAGE_TEXT, expectedMessage);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAngularRequestsToFinish();
		switchToLiveChatFrame();
		commonWaits.waitForByElementToBeVisible(By.xpath(msgXpath));
		Assert.assertFalse(findElements(By.xpath(msgXpath)).isEmpty(), "Message not present in Live Chat transcript: " + expectedMessage);
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifySystemMessageOnLiveChat(String expectedMessage) {
		Assert.assertNotNull(expectedMessage, MSG_EXPECTED_MESSAGE_REQUIRED);
		switchToLiveChatFrame();
		List<String> candidates = buildSystemMessageCandidates(expectedMessage);

		boolean found = false;
		for (String candidate : candidates) {
			String msgXpath = String.format(SYSTEM_MESSAGE_TEXT, candidate.toLowerCase(Locale.ROOT));
			if (commonWaits.waitForElementPresent(By.xpath(msgXpath))) {
				found = true;
				break;
			}
		}
		switchtoDefaultFrame();
		Assert.assertTrue(found,
				"System message not present in Live Chat transcript. Expected: " + expectedMessage +
						". Tried: " + candidates);
		return this;
	}

	private List<String> buildSystemMessageCandidates(String expectedMessage) {
		List<String> candidates = new ArrayList<>();
		if (expectedMessage == null) {
			return candidates;
		}
		String trimmed = expectedMessage.trim();
		if (!trimmed.isEmpty()) {
			candidates.add(trimmed);
		}

		// Allow tests to pass either with or without the "System Message:" prefix.
		String withoutPrefix = trimmed.replaceFirst(REGEX_SYSTEM_MESSAGE_PREFIX, "").trim();
		if (!withoutPrefix.isEmpty() && !withoutPrefix.equals(trimmed)) {
			candidates.add(withoutPrefix);
		}

		String withPrefix = (SYSTEM_MESSAGE_PREFIX + withoutPrefix).trim();
		if (!withPrefix.isEmpty() && !withPrefix.equals(trimmed) && !withPrefix.equals(withoutPrefix)) {
			candidates.add(withPrefix);
		}

		return candidates;
	}

	public boolean isMsgTextAreaDisplayed() {
		switchToLiveChatFrame();
		boolean displayed = !findElements(By.xpath(TXTAREA_AGENT_MESSAGE)).isEmpty();
		switchtoDefaultFrame();
		return displayed;
	}

	public SmartITLiveChatAgentConsolePage verifyMonitorIconForInvitee() {
		switchToLiveChatFrame();
		Assert.assertTrue(findElements(By.xpath(INVITEE_MONITOR_ICON)).size() > 0, MSG_MONITOR_ICON_MISSING);
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifyParticipantIconForInvitee() {
		switchToLiveChatFrame();
		Assert.assertTrue(findElements(By.xpath(INVITEE_PARTICIPANT_ICON)).size() > 0, MSG_PARTICIPANT_ICON_MISSING);
		switchtoDefaultFrame();
		return this;
	}

	public boolean verifyInviteeChatInAgentQueue(String initialChatMessage) {
		Assert.assertNotNull(initialChatMessage, MSG_INITIAL_MESSAGE_REQUIRED);
		String chatXpath = String.format(CHAT_TEXT_QUESTION, initialChatMessage);
		switchToLiveChatFrame();
		boolean present = !findElements(By.xpath(chatXpath)).isEmpty();
		switchtoDefaultFrame();
		return present;
	}

	public SmartITLiveChatAgentConsolePage clickEndChatAndConfirmYes() {
		switchToLiveChatFrame();
		commonWaits.clickWithoutInterception(By.xpath(BTN_END_CHAT));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
		switchtoDefaultFrame();
		commonWaits.clickWithoutInterception(BTN_CONFIRM_YES);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
		return this;
	}

	public SmartITLiveChatAgentConsolePage openChatActionsAndSelect(String actionLabel) {
		Assert.assertNotNull(actionLabel, MSG_ACTION_LABEL_REQUIRED);
		switchToLiveChatFrame();
		commonWaits.clickWithoutInterception(By.xpath(BTN_CHAT_ACTIONS));
		String actionXpath = String.format(ACTION_IN_DROPDOWN, actionLabel);
		commonWaits.clickWithoutInterception(By.xpath(actionXpath));
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage selectActionFromOpenedDropdown(String actionLabel) {
		Assert.assertNotNull(actionLabel, MSG_ACTION_LABEL_REQUIRED);
		switchToLiveChatFrame();
		String actionXpath = String.format(ACTION_IN_DROPDOWN, actionLabel);
		commonWaits.clickWithoutInterception(By.xpath(actionXpath));
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage switchToNewlyOpenedInviteeWindow(int expectedTotalWindows) {
		commonWaits.waitForNumberWindowsToBe(expectedTotalWindows);
		commonWaits.switchToLastWindow();
		return this;
	}

	public SmartITLiveChatAgentConsolePage clickLeaveChat() {
		switchToLiveChatFrame();
		commonWaits.clickWithoutInterception(By.xpath(BTN_LEAVE_CHAT));
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifyCustomerIsNotPresentInQueueAfterChatEnded(String initialMessage, String customerName) {
		Assert.assertNotNull(initialMessage, "initialMessage must not be null");
		Assert.assertNotNull(customerName, "customerName must not be null");

		String rowXpath = String.format(CUSTOMER_QUEUE_ROW_BY_MESSAGE, customerName, initialMessage);

		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < DEFAULT_POLL_TIMEOUT_MS) {
			clickQueueTab();
			switchToLiveChatFrame();
			boolean present = !findElements(By.xpath(rowXpath)).isEmpty();
			switchtoDefaultFrame();
			if (!present) {
				return this;
			}
			commonWaits.waitForSometime(DEFAULT_POLL_INTERVAL_MS);
			getDriver().navigate().refresh();
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
		}

		Assert.fail("Customer/chat still present in queue after timeout. customer=" + customerName + ", message=" + initialMessage);
		return this;
	}

	/**
	 * Best-effort SLA color verification for Queue time.
	 * Note: Live Chat UI sometimes paints SLA on the cell's background-color.
	 */
	public SmartITLiveChatAgentConsolePage verifyQueueTimeSLAColor(String expectedCssRgb) {
		// Deprecated: use verifyQueueTimeSLAColorForChat(...) for row-specific validation.
		switchToLiveChatFrame();
		WebElement sortBtn = commonWaits.waitForByElementToBeVisible(By.xpath(QUEUE_TIME_SORT_BTN));
		String actual = sortBtn.getCssValue(CSS_BACKGROUND_COLOR);
		switchtoDefaultFrame();
		Assert.assertTrue(actual != null && actual.contains(expectedCssRgb),
				"Queue time SLA color mismatch. expected contains=" + expectedCssRgb + ", actual=" + actual);
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifyQueueTimeSLAColorForChat(String initialChatMessage, String expectedCssRgb) {
		Assert.assertNotNull(initialChatMessage, MSG_INITIAL_MESSAGE_REQUIRED);
		if (expectedCssRgb == null || expectedCssRgb.trim().isEmpty()) {
			// No SLA color assertion requested (Umbrella passes empty for initial state).
			return this;
		}

		clickChatsTab();
		String chatXpath = String.format(CHAT_TEXT_QUESTION, initialChatMessage);
		switchToLiveChatFrame();
		WebElement chatText = commonWaits.waitForByElementToBeVisible(By.xpath(chatXpath));
		WebElement row = chatText.findElement(By.xpath(CHAT_ROW_ANCESTOR));
		String actual = row.getCssValue(CSS_BACKGROUND_COLOR);
		switchtoDefaultFrame();

		// Engineering assumption: SLA color is applied as row background-color.
		Assert.assertTrue(actual != null && actual.contains(expectedCssRgb),
				"Queue time SLA row color mismatch. expected contains=" + expectedCssRgb + ", actual=" + actual);
		return this;
	}

	public SmartITLiveChatAgentConsolePage clickChatActionDropdownForChat(String initialChatMessage) {
		Assert.assertNotNull(initialChatMessage, MSG_INITIAL_MESSAGE_REQUIRED);
		clickChatsTab();
		String chatXpath = String.format(CHAT_TEXT_QUESTION, initialChatMessage);
		switchToLiveChatFrame();
		WebElement chatText = commonWaits.waitForByElementToBeVisible(By.xpath(chatXpath));
		WebElement row = chatText.findElement(By.xpath(CHAT_ROW_ANCESTOR));
		WebElement actionDropdown = commonWaits.waitForByElementToBeVisible(By.xpath(CHAT_ROW_ACTION_DROPDOWN));
		clickByAction(actionDropdown);
		switchtoDefaultFrame();
		return this;
	}

	public SmartITLiveChatAgentConsolePage closeCurrentWindowIfPopupAndSwitchBackToFirst() {
		List<String> handles = new ArrayList<>(getDriver().getWindowHandles());
		if (handles.size() <= 1) {
			return this;
		}
		String current = getDriver().getWindowHandle();
		getDriver().close();
		for (String h : handles) {
			if (!h.equals(current)) {
				setDriver(getDriver().switchTo().window(h));
				break;
			}
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.ITSM_SMARTIT_COMPONENT);
		return this;
	}

  public SmartITLiveChatAgentConsolePage acceptNavigationAwayWarningByClickingYesIfPresent() {
	for (int pass = 0; pass < 2; pass++) {
		if (pass == 0) {
			switchtoDefaultFrame();
		} else {
			switchToLiveChatFrame();
		}
	}
        if(commonWaits.isElementPresent(navWarningDialog)) {
			log.info("Navigation warning detected; clicking Yes to continue.");
            commonWaits.clickWithoutInterception(navWarningDialog);
        }
        switchtoDefaultFrame();
        return this;
    }

	public SmartITLiveChatAgentConsolePage clickCreateIncidentButton() {
		log.info("Click On Create Incident button");
        click(clickCreateIncidentButton);
        click(createIncidentBtn);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

	public SmartITLiveChatAgentConsolePage closeNotificationAlerts() {
		try {
			for (WebElement btn : systemAlertCloseBtn) {
				click(btn);
			}
		} catch (Exception e) {
		}
		return this;
	}

	public SmartITLiveChatAgentConsolePage verifyTicketCreatedFromResourceTab() {
			log.info("Click On Resource Tab");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			commonWaits.clickWithoutInterception(resourceTab);
			if (commonWaits.isElementPresent(resourcesIncidentId)) {
				String incidentId = resourcesIncidentId.getAttribute("innerText");
				Assert.assertTrue(incidentId.contains("INC"),
						"Incident created by Live Chat is not available under resources tab");
			} else if (commonWaits.isElementPresent(resourcesWOId)) {
				String woId = resourcesWOId.getAttribute("innerText");
				Assert.assertTrue(woId.contains("WO"),
						"WO created by Live Chat is not available under resources tab");
			} else {
				Assert.fail("No ticket created by Live Chat is available under resources tab");
			}
		return this;
	}

	public String getTicketIdFromResourceTab() {
        log.info("GetTicketIdFromResourceTab");
        String ticketId = "";
        commonWaits.clickWithoutInterception(resourceTab);
        if (commonWaits.isElementPresent(resourcesIncidentId)) {
            ticketId = resourcesIncidentId.getAttribute("innerText");
        } else if (commonWaits.isElementPresent(resourcesWOId)) {
            ticketId = resourcesWOId.getAttribute("innerText");
        } else {
            Assert.fail("No ticket created by Live Chat is available under resources tab");
        }
        return ticketId.trim();
    }

	public SmartITLiveChatAgentConsolePage openTicketFromResources() {
		log.info("Open Ticket From Resources");
		switchtoDefaultFrame();
        if (commonWaits.isElementPresent(resourcesIncidentId)) {
            click(resourcesIncidentId);
        } else if (commonWaits.isElementPresent(resourcesWOId)) {
            click(resourcesWOId);
        }
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);

		return this;
	}

	public SmartITLiveChatAgentConsolePage clickCreateWorkOrderButton() {
		log.info("Click On Create Work Order button");
        click(clickCreateIncidentButton);
        click(createWorkOrderBtn);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

	public String verifySummarization(){
        commonWaits.waitForExecutionComplete();
        switchtoDefaultFrame();
        commonWaits.switchToFrame(switchToFrame);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(gptSummary));
        commonWaits.waitForSometime(commonWaits.minDelay);
        Assert.assertFalse(we.getAttribute("value").isEmpty(), "Summarization is not not working");
        switchtoDefaultFrame();
		return we.getAttribute("value");
    }

	public SmartITLiveChatAgentConsolePage clickTransferTab() {
		acceptNavigationAwayWarningIfPresent();
		// Try in iframe context (common), then fallback to default content (some stacks)
		switchToLiveChatFrame();
		boolean clicked = clickFirstPresentInLiveChatFrame(TAB_Transferred_XPATHS);
		if (!clicked) {
			switchtoDefaultFrame();
			clicked = clickFirstPresentInLiveChatFrame(TAB_Transferred_XPATHS);
		}
		// If a warning pops after clicking (overlay), accept it and retry once.
		if (!clicked && acceptNavigationAwayWarningIfPresent()) {
			switchToLiveChatFrame();
			clicked = clickFirstPresentInLiveChatFrame(TAB_Transferred_XPATHS);
			if (!clicked) {
				switchtoDefaultFrame();
				clicked = clickFirstPresentInLiveChatFrame(TAB_Transferred_XPATHS);
			}
		}
		if (!clicked) {
			log.info(LOG_PREFIX + MSG_QUEUE_TAB_MISSING);
		}
		switchtoDefaultFrame();
		return this;
	}


}