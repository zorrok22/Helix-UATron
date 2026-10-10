package pages.ITSM.SmartIT;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.support.PageFactory;
import framework.BasePage;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ViewWorkorderPage extends BasePage {

    static SolqaLogger log = new SolqaLogger(ViewWorkorderPage.class.getName());
    CommonWaits commonWaits;

    public ViewWorkorderPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ViewWorkorderPage.class.getName());
        commonWaits = new CommonWaits();
    }

	@FindBy(xpath=("//span[contains(text(),'Resources')]//ancestor::button[@aria-posinset='2' ]//ancestor::li[contains(@class,'c866074846-1234')]"))
	WebElement resourcesTab;
    @FindBy(xpath = "//*[@id='304421551' or @id='304421561' or (@title='Assign to me') or (normalize-space(.)='Assign to me')]")
    WebElement assignToMeLink;
    @FindBy(xpath = "//*[@id='ar7_data']")
    WebElement statusDropdown;
	@FindBy(xpath = "//a[@id='ar301395300_data']")
	public WebElement workorderCustomerName;
	@FindBy(xpath = "//div[@id='304419361']")
	public WebElement workorderTitle;
	@FindBy(xpath = "//*[@ux-id='system-alert']//*[@ux-id='system-alert-text']")
    public WebElement sysAlertMsgLocator;
	@FindBy(xpath = "//*[contains(@id,'ar304456391_data')]")
	public WebElement relatedDWPRequestID;

	// -------------------- Needs Attention flag (Workorder view) --------------------
	private static final String ATTRIBUTE_ARIA_PRESSED = "aria-pressed";
	private static final String ATTRIBUTE_ARIA_CHECKED = "aria-checked";
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String NEEDS_ATTENTION_TEXT = "Needs attention";
	private static final String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPER_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final String XPATH_REFRESH_BUTTON = "//button[@title='Refresh']";
	private static final String[] NEEDS_ATTENTION_TOGGLE_XPATHS = new String[] {
			"//button[contains(@aria-label,'" + NEEDS_ATTENTION_TEXT + "') or contains(@title,'" + NEEDS_ATTENTION_TEXT + "')]",
			"//button[contains(translate(normalize-space(.),'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "')," +
					"translate('" + NEEDS_ATTENTION_TEXT + "','" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'))]",
			"//button[.//*[contains(@class,'flag') or contains(@class,'icon-flag') or contains(@class,'a-icon--flag')]]",
			"//*[@ux-id='needs-attention-flag' or @ux-id='needs-attention' or @ux-id='attention-flag']"
	};

	private static final String[] ASSIGN_TO_ME_XPATHS = new String[] {
			"//*[@title='Assign to me' or @aria-label='Assign to me' or normalize-space(.)='Assign to me']",
			"//button[@title='Assign to me' or @aria-label='Assign to me' or .//*[normalize-space(.)='Assign to me']]",
			"//*[contains(translate(@title,'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'assign to me')"
					+ " or contains(translate(@aria-label,'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'assign to me')]"
	};

	//String locators
	public String viewPageElement= "//*[@id='%s']";
    public String IFrameViewWorkorder = "//iframe[@id='pwa-frame']";
	public String StatusDownArrow= "//*[@id='304424821']";
	public String statusOptionButton = "//*[contains(text(),'%s')]//parent::adapt-highlight//ancestor::button";

    private static final String IFRAME_VIEW_WORKORDER = "//iframe[@id='pwa-frame']";
    private static final String REL_IFRAME_XPATH = ".//iframe";
    private static final String STATUS_DOWN_ARROW= "//*[@id='304424821']";
    private static final String STATUS_DROPDOWN = "//*[@id= '7']//button[contains(@id, 'rx-select') and @name='ar7']";
    private static final String STATUS_OPTION_BUTTON = "//*[contains(text(),'%s')]//parent::adapt-highlight//ancestor::button";

    // Resources / Knowledge search + pinning (best-effort; UI varies by stack)
    private static final String[] RESOURCE_SEARCH_INPUT_XPATHS = new String[] {
            "//input[contains(@placeholder,'Search') or contains(@aria-label,'Search') or contains(@name,'search') or contains(@id,'search')]",
            "//adapt-rx-search//input",
            "//adapt-search//input"
    };
    private static final String ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String KNOWLEDGE_TITLE_CONTAINS =
            "//*[self::a or self::span or self::div]" +
                    "[contains(@class,'rx-ellipsis') or self::a or self::span]" +
                    "[contains(normalize-space(.),'%s')]";
    private static final String PIN_TOGGLE_FROM_TITLE =
            "(" +
                    "%s/ancestor::*[self::div or self::tr or self::li][1]" +
                    "//*[contains(@ux-id,'toggle-selection') or contains(@aria-label,'Pin') or contains(@title,'Pin') " +
                    "or contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'pin')]" +
                    "/ancestor::*[self::button or self::a][1]" +
                    ")[1]";
    private static final String PINNED_ARTICLE_LINK =
            "//*[@alt='Related']//ancestor::tr//a[contains(normalize-space(.),'%s')]"
                    + " | //a[contains(normalize-space(.),'%s')]";

    // Fetch Workorder Ticket I'd wait constants (align with incident behavior)
    private static final long FETCH_TICKET_ID_TIMEOUT_MS = 60_000L;
    private static final long FETCH_TICKET_ID_POLL_MS = 500L;

    // Fallback: toast / URL parsing for Workorder ID
    private static final String XPATH_SYSTEM_ALERT_TEXT = "//*[@ux-id='system-alert']//*[@ux-id='system-alert-text']";
    private static final Pattern WORKORDER_ID_PATTERN = Pattern.compile("(WO\\d+)", Pattern.CASE_INSENSITIVE);

    // Candidate Workorder ID locations (varies across SmartIT builds)
    private static final String ARID_WORKORDER_ID = "1000000182";
    private static final String XPATH_WORKORDER_ID_DATA = "//*[@id='ar" + ARID_WORKORDER_ID + "_data']";
    private static final String XPATH_WORKORDER_ID_CONTAINER = "//*[@id='ar" + ARID_WORKORDER_ID + "']";
    private static final String XPATH_WORKORDER_ID_INPUT = "//*[@id='ar" + ARID_WORKORDER_ID + "']//input";
    private static final String XPATH_WORKORDER_ID_TEXTAREA = "//*[@id='ar" + ARID_WORKORDER_ID + "']//textarea";
    private static final String XPATH_WORKORDER_ID_CONTAINS = "//*[contains(@id,'" + ARID_WORKORDER_ID + "') and contains(@id,'_data')]";
    private static final String[] XPATHS_WORKORDER_ID_CANDIDATES = new String[] {
            XPATH_WORKORDER_ID_DATA,
            XPATH_WORKORDER_ID_CONTAINER,
            XPATH_WORKORDER_ID_INPUT,
            XPATH_WORKORDER_ID_TEXTAREA,
            XPATH_WORKORDER_ID_CONTAINS
    };

    public String FetchTicketID() {
        log.info("Fetching Workorder Ticket ID");

        final long endAt = System.currentTimeMillis() + FETCH_TICKET_ID_TIMEOUT_MS;
        while (System.currentTimeMillis() < endAt) {
            try {
                // 1) Best-effort: check toast/system alert in default content
                switchtoDefaultFrame();
                for (WebElement el : findElements(By.xpath(XPATH_SYSTEM_ALERT_TEXT))) {
                    try {
                        String t = el.getText();
                        if (t != null && !t.trim().isEmpty()) {
                            Matcher m = WORKORDER_ID_PATTERN.matcher(t);
                            if (m.find()) return m.group(1).trim();
                        }
                    } catch (Exception ignored) {}
                }

                // 2) Best-effort: check URL for WOxxxx
                try {
                    String url = getDriver().getCurrentUrl();
                    if (url != null && !url.trim().isEmpty()) {
                        Matcher m = WORKORDER_ID_PATTERN.matcher(url);
                        if (m.find()) return m.group(1).trim();
                    }
                } catch (Exception ignored) {}

                switchtoDefaultFrame();
                if (!findElements(By.xpath(IFRAME_VIEW_WORKORDER)).isEmpty()) {
                    switchToFrame(By.xpath(IFRAME_VIEW_WORKORDER));
                }

                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

                for (String xp : XPATHS_WORKORDER_ID_CANDIDATES) {
                    By by = By.xpath(xp);
                    java.util.List<WebElement> els = findElements(by);
                    if (els == null || els.isEmpty()) continue;

                    WebElement el = els.get(0);
                    String text = "";
                    try { text = el.getText(); } catch (Exception ignored) {}
                    if (text == null || text.trim().isEmpty()) {
                        try { text = el.getAttribute("innerText"); } catch (Exception ignored) {}
                    }
                    if (text == null || text.trim().isEmpty()) {
                        try { text = el.getAttribute("value"); } catch (Exception ignored) {}
                    }

                    if (text != null && !text.trim().isEmpty()) {
                        switchtoDefaultFrame();
                        return text.trim();
                    }
                }
            } catch (Exception ignored) {
                // best-effort polling; continue
            }

            commonWaits.waitForSometime(FETCH_TICKET_ID_POLL_MS);
        }

        org.testng.Assert.fail("Unable to fetch Workorder Ticket ID within " + FETCH_TICKET_ID_TIMEOUT_MS + "ms. " +
                "Workorder ID element not found/empty in PWA frame.");
        return "";
    }

	private void switchToWorkorderPwaFrame() {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_VIEW_WORKORDER));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	private WebElement findNeedsAttentionToggleOrNull() {
		switchToWorkorderPwaFrame();
		for (String xpath : NEEDS_ATTENTION_TOGGLE_XPATHS) {
			try {
				By by = By.xpath(xpath);
				if (!findElements(by).isEmpty()) {
					return findElement(by);
				}
			} catch (Exception ignored) {}
		}
		return null;
	}

	private WebElement findFirstSearchInputOrNull() {
		for (String xp : RESOURCE_SEARCH_INPUT_XPATHS) {
			try {
				java.util.List<WebElement> els = findElements(By.xpath(xp));
				if (els == null || els.isEmpty()) {
					continue;
				}
				for (WebElement el : els) {
					try {
						if (el != null && el.isDisplayed() && el.isEnabled()) {
							return el;
						}
					} catch (Exception ignored) {}
				}
				return els.get(0);
			} catch (Exception ignored) {}
		}
		return null;
	}

    public ViewWorkorderPage refreshWorkOrder() {
    	switchToWorkorderPwaFrame();
    	By by = By.xpath(XPATH_REFRESH_BUTTON);
    	if (!findElements(by).isEmpty()) {
    		commonWaits.clickWithoutInterception(by);
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		commonWaits.waitForPageFullyLoaded(30, 3);
    	}
    	switchtoDefaultFrame();
    	return this;
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
    		return "true".equalsIgnoreCase(ariaChecked.trim());
    	}

    	String cls = toggle.getAttribute(ATTRIBUTE_CLASS);
    	if (cls != null) {
    		String c = cls.toLowerCase();
    		return c.contains("active") || c.contains("selected") || c.contains("checked") || c.contains("on");
    	}
    	return false;
    }

    public ViewWorkorderPage setNeedsAttentionFlag(boolean shouldBeSet) {
    	WebElement toggle = findNeedsAttentionToggleOrNull();
    	org.testng.Assert.assertNotNull(toggle, "Needs Attention flag toggle is not present on Workorder view page.");

    	boolean isSet = isNeedsAttentionFlagSet();
    	if (isSet != shouldBeSet) {
            clickByAction(toggle);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageFullyLoaded(30, 3);
    	}

    	org.testng.Assert.assertEquals(isNeedsAttentionFlagSet(), shouldBeSet,
    			"Needs Attention flag state mismatch. Expected=" + shouldBeSet);
    	return this;
    }

    public boolean setNeedsAttentionFlagBestEffort(boolean shouldBeSet) {
        WebElement toggle = findNeedsAttentionToggleOrNull();
        if (toggle == null) return false;

        boolean isSet = isNeedsAttentionFlagSet();
        if (isSet != shouldBeSet) {
            clickByAction(toggle);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageFullyLoaded(30, 3);
        }
        return isNeedsAttentionFlagSet();
    }

    public ViewWorkorderPage clickAssignToMeOnViewPage(String fieldNameOrLabel) {
    	// fieldNameOrLabel is optional; prefer provided string when possible, but keep fallback generic.
    	switchToWorkorderPwaFrame();

    	// Try scoped click if label provided
    	if (fieldNameOrLabel != null && !fieldNameOrLabel.trim().isEmpty()) {
    		String lbl = fieldNameOrLabel.trim();
    		String scoped =
    				"(//*[normalize-space(.)='" + lbl + "']/ancestor::*[self::div or self::section or self::li][1]" +
    						"//*[contains(translate(@title,'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'assign to me')" +
    						" or contains(translate(@aria-label,'" + UPPER_ALPHA + "','" + LOWER_ALPHA + "'),'assign to me')]" +
    						"/ancestor::*[self::button or self::a][1])[1]";
    		By scopedBy = By.xpath(scoped);
    		if (!findElements(scopedBy).isEmpty()) {
    			commonWaits.clickWithoutInterception(scopedBy);
    			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    			switchtoDefaultFrame();
    			return this;
    		}
    	}

    	// Generic fallbacks
    	for (String xp : ASSIGN_TO_ME_XPATHS) {
    		By by = By.xpath(xp);
    		if (!findElements(by).isEmpty()) {
    			commonWaits.clickWithoutInterception(by);
    			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    			break;
    		}
    	}

    	switchtoDefaultFrame();
    	return this;
    }

    public ViewWorkorderPage setStatus(String status) {
        commonWaits.waitForPageFullyLoaded(30,3);
        switchToWorkorderPwaFrame();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), STATUS_DOWN_ARROW, 20), "Status dropdown is not visible on Workorder view page.");
        commonWaits.clickWithoutInterception(By.xpath(STATUS_DOWN_ARROW));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(STATUS_DROPDOWN), 20), "Status dropdown options are not visible on Workorder view page.");
        commonWaits.waitForPageFullyLoaded(30,3);
        commonWaits.clickWithoutInterception(By.xpath(STATUS_DROPDOWN));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(STATUS_OPTION_BUTTON, status)), 20), "Status option '" + status + "' is not visible in dropdown on Workorder view page.");
        commonWaits.clickWithoutInterception(By.xpath(String.format(STATUS_OPTION_BUTTON, status)));
        commonWaits.waitForPageFullyLoaded(30,3);
        return this;
    }

	public void clickOnResourcesTab() {
		switchToWorkorderPwaFrame();
		commonWaits.waitForElementVisible(resourcesTab);
		commonWaits.clickWithoutInterception(resourcesTab);
		commonWaits.waitForPageFullyLoaded(30, 3);
	}

    public ViewWorkorderPage clickAssignToMeIfPresent() {
        switchtoDefaultFrame();
        switchToFrame(By.xpath(IFrameViewWorkorder));
        if (commonWaits.isElementPresent(assignToMeLink)) {
            commonWaits.clickWithoutInterception(assignToMeLink);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }
        return this;
    }

    public String getStatus() {
        switchtoDefaultFrame();
        switchToFrame(By.xpath(IFrameViewWorkorder));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusDropdown, 20), "Status dropdown is not visible on Workorder view page.");
        return commonWaits.waitForElementVisible(statusDropdown).getText().trim();
    }

    public ViewWorkorderPage verifyStatus(String expectedStatus) {
        String actual = getStatus();
        Assert.assertTrue(actual.equalsIgnoreCase(expectedStatus) || actual.contains(expectedStatus),
                "Work Order status mismatch. Expected: '" + expectedStatus + "', Actual: '" + actual + "'");
        return this;
    }

	public void searchAndPinKnowledgeArticleInResources(String searchKeyword, String articleTitle) {
		if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
			throw new AssertionError("searchKeyword must not be null/empty");
		}
		if (articleTitle == null || articleTitle.trim().isEmpty()) {
			throw new AssertionError("articleTitle must not be null/empty");
		}

		clickOnResourcesTab();
		switchToWorkorderPwaFrame();

		WebElement searchInput = findFirstSearchInputOrNull();
		if (searchInput != null) {
			try { searchInput.clear(); } catch (Exception ignored) {}
			searchInput.sendKeys(searchKeyword.trim());
			searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
			commonWaits.waitForPageFullyLoaded(30, 3);
		}

		String titleXpath = String.format(KNOWLEDGE_TITLE_CONTAINS, articleTitle.trim());
		commonWaits.waitForElementPresent(By.xpath(titleXpath));

		String pinXpath = String.format(PIN_TOGGLE_FROM_TITLE, titleXpath);
		if (commonWaits.waitForElementPresent(By.xpath(pinXpath))) {
			commonWaits.clickWithoutInterception(By.xpath(pinXpath));
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			commonWaits.waitForPageFullyLoaded(30, 3);
		} else {
			log.warn("Pin/selection control not found for knowledge article row: " + articleTitle);
		}

		switchtoDefaultFrame();
	}

	public boolean isKnowledgeArticlePinnedUnderResourcesTabByTitle(String articleTitle) {
		if (articleTitle == null || articleTitle.trim().isEmpty()) {
			return false;
		}
		String title = articleTitle.trim();
		String xp = String.format(PINNED_ARTICLE_LINK, title, title);

		// Check in PWA frame
		switchToWorkorderPwaFrame();
		if (!findElements(By.xpath(xp)).isEmpty()) {
			switchtoDefaultFrame();
			return true;
		}

		// Fallback: check in nested iframes under PWA (some stacks render resources in inner frame)
		java.util.List<WebElement> iframes = findElements(By.xpath(REL_IFRAME_XPATH));
		if (iframes != null && !iframes.isEmpty()) {
			for (WebElement frame : iframes) {
				try {
					getDriver().switchTo().frame(frame);
					if (!findElements(By.xpath(xp)).isEmpty()) {
						switchtoDefaultFrame();
						return true;
					}
				} catch (Exception ignored) {
				} finally {
					try { getDriver().switchTo().parentFrame(); } catch (Exception ignored) {}
				}
			}
		}

		switchtoDefaultFrame();
		return false;
	}

	public ViewWorkorderPage verifyWorkorderData(Map<Object, String> smartITTestData) {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFRAME_VIEW_WORKORDER));
		if(smartITTestData.get(SolQAConstants.CUSTOMER_FULL_NAME) != null) {
			log.info("Verifying Customer");
			verifyEquals(workorderCustomerName.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT), smartITTestData.get(SolQAConstants.CUSTOMER_FULL_NAME), "Customer Name does not match");
		}

		if(smartITTestData.get(SolQAConstants.WORKORDER_TITLE) != null) {
			log.info("Verifying Workorder Title");
			verifyEquals(workorderTitle.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT), smartITTestData.get(SolQAConstants.WORKORDER_TITLE), "Workorder Title does not match");
		}
		switchtoDefaultFrame();
		return this;
  	}

	public ViewWorkorderPage verifyAlertMessage(String expectedMessage) {
		switchtoDefaultFrame();
		commonWaits.waitForElementVisible(sysAlertMsgLocator);
		verifyEquals(sysAlertMsgLocator.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT), expectedMessage, "Alert message does not match");
		return this;
	}

	public String getRelatedDWPRequestID() {
		return relatedDWPRequestID.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
	}

    @FindBy(xpath = "//button[@id='304430951']")
    WebElement unFlagBtn;
    @FindBy(xpath = "//button[@id='304430961']")
    WebElement flagBtn;
    @FindBy(xpath = "//textarea[@aria-label='New note' and @id='304247080']")
    WebElement noteTextArea;
    @FindBy(xpath = "//button[@id='304268430' and contains(normalize-space() ,'Post')]")
    WebElement postBtn;
    public ViewWorkorderPage SetFlagAndUnFlag(String flagName, String FlagMessage) {
        switchToWorkorderPwaFrame();
        if (Objects.equals(flagName, "Unflag")) {
            commonWaits.waitForElementClickable(unFlagBtn).click();
        }
        if (Objects.equals(flagName, "Flag")) {
            commonWaits.waitForElementClickable(flagBtn).click();
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), noteTextArea, 20), "Note text area is not visible after clicking " + flagName);
        commonWaits.clickWithoutInterception(noteTextArea);
        noteTextArea.sendKeys(FlagMessage);
        commonWaits.clickWithoutInterception(postBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

}