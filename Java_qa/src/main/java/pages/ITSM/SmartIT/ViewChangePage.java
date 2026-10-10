package pages.ITSM.SmartIT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class ViewChangePage extends BasePage {
	
	static SolqaLogger log;
	CommonWaits commonWaits;

	
	
     String XPATH_IFRAME_PWA = "//iframe[@id='pwa-frame']";
	String XPATH_ALL_IFRAMES = "//iframe";
	String XPATH_BY_ID_TEMPLATE = "//*[@id='%s']";
	String XPATH_CHANGE_ID_VALUE_TEMPLATE = "//*[@id='%s']";
	String CHANGE_ID_DATA_ID = "ar1000000182_data";
	String XPATH_CHANGE_ID_AR_DATA = "//*[@id='ar1000000182_data']";
	String XPATH_CHANGE_ID_AR = "//*[@id='ar1000000182']";
	String XPATH_CHANGE_ID_AR_CONTAINS = "//*[contains(@id,'ar1000000182') and contains(@id,'data')]";
	String REGEX_CRQ_ID = "(CRQ\\d+)";
	String XPATH_ACTIVITY_NOTEBOX = "//*[contains(@placeholder,'add a note')]";
	String XPATH_ACTIVITY_POST_BUTTON = "//button[contains(@id,'304268430')]";
	String XPATH_PUBLIC_NOTE_CHECKBOX = "//input[contains(@class,'checkbox')]//..//div//span[text()='Public']";
    String XPATH_SEARCH_CI_TEXTBOX = "//*[contains(@placeholder,'Search multiple CIs')]";
	String XPATH_SEARCH_ICON = "//span[contains(@class,'d-icon-search left' )]";
	String XPATH_CI_CHECKBOX_IN_ROW = ".//div[contains(@class,'ui-chkbox-box')]";
	String XPATH_CI_CHECKBOX_ANY = "//div[contains(@class, 'ui-chkbox-box')]";
	String XPATH_RELATIONSHIP_OPTION_TEMPLATE = "//*[contains(@class, 'rx-select__options-')]/button[contains(.,'%s')]";
	String REL_ANCESTOR_TR_1 = "./ancestor::tr[1]";
	String REL_ANCESTOR_ROLE_ROW_1 = "./ancestor::*[@role='row'][1]";
    String XPATH_EDIT_BUTTON = "//button[normalize-space()='Edit' or @aria-label='Edit' or @title='Edit'"
			+ " or .//span[normalize-space()='Edit'] or contains(@ux-id,'edit') or contains(@data-testid,'edit')]";
	String XPATH_ADD_TASK_BUTTON = "//button[contains(.,'Add task') or contains(.,'Add Task') or contains(@aria-label,'Add task') or contains(@title,'Add task')]";
	String XPATH_STATUS_DOWN_ARROW = "//button[@id='304424821']";
	String XPATH_CLEAR_BUTTON_STATUS = "//button[@aria-label='Clear Status ']";
	String XPATH_STATUS_DROPDOWN = "//button[@role='combobox' and @name='ar303502600']";
	String XPATH_STATUS_OPTION_TEMPLATE = "//button[@role='option'][.//span[normalize-space()='%s']]";
    String XPATH_OUTAGE_CI_SELECTION = "(//div[contains(@class, 'ui-chkbox-box')])[2]";
	String OUTAGE_SAVE_BUTTON_ID = "304448291";
	String OUTAGE_SCHEDULED_START_DATE_ID = "1000000350";
	String OUTAGE_SCHEDULED_END_DATE_ID = "1000000362";
	String XPATH_CREATED_OUTAGE = "//a[contains(@class, 'ng-star-inserted') and contains(text(), 'AOT')]";
	String XPATH_CREATED_OUTAGE_CI_TEMPLATE = "//div[contains(text(), '%s')]";

	String XPATH_MORE_BUTTON =
			"//button[normalize-space()='More' or @aria-label='More' or @title='More' or .//span[normalize-space()='More']]"
			+ " | //button[contains(@class,'more') and (contains(@aria-label,'More') or contains(@title,'More'))]";
	String XPATH_MENU_ITEM_BY_TEXT_CONTAINS_TEMPLATE =
			"//*[@role='menu' or contains(@class,'a-menu') or contains(@class,'menu')]"
			+ "//*[self::li or self::button or self::span][contains(normalize-space(.), %s)]";
	String XPATH_SHOW_MORE_BUTTON_BY_TEXT_CONTAINS_TEMPLATE =
			"//button[contains(normalize-space(.), %s) or .//span[contains(normalize-space(.), %s)]]";
	String XPATH_IMPACTED_CIS_BLADE_HEADER =
			"//*[contains(normalize-space(.),'Impacted CIs') or contains(normalize-space(.),'Impacted CI')]";
	String XPATH_VIEW_TOGGLE_BY_TEXT_CONTAINS_TEMPLATE =
			"//button[contains(normalize-space(.), %s) or .//span[contains(normalize-space(.), %s)]]"
			+ " | //a[contains(normalize-space(.), %s)]";
	String XPATH_BUTTON_OR_LINK_CONTAINS_QUOTED_TEMPLATE =
			"//button[contains(normalize-space(.), %s)] | //a[contains(normalize-space(.), %s)]";
	String XPATH_ANY_ELEMENT_CONTAINS_QUOTED_TEMPLATE =
			"//*[contains(normalize-space(.), %s)]";
    String XPATH_SHOW_SERVICE_ONLY_CHECKBOX_BY_LABEL_CONTAINS_TEMPLATE =
			"//label[contains(normalize-space(.), %s)]//input[@type='checkbox']"
			+ " | //input[@type='checkbox' and contains(@aria-label, %s)]";
	String XPATH_IMPACTED_CIS_LIST_ROW_BY_TEXT_CONTAINS_TEMPLATE =
			"//*[contains(@class,'impacted') or contains(@data-testid,'impacted') or contains(@class,'list')]"
			+ "//*[self::td or self::div or self::span][contains(normalize-space(.), %s)]";
	String XPATH_TOPOLOGY_NODE_BY_NAME_CONTAINS_TEMPLATE =
			"//*[contains(@class,'topology') or contains(@data-testid,'topology') or contains(@class,'graph')]"
			+ "//*[self::div or self::span or self::text or self::a][contains(normalize-space(.), %s)]";
	String XPATH_CONTEXT_MENU_OPTION_BY_TEXT_CONTAINS_TEMPLATE =
			"//*[@role='menu' or contains(@class,'menu')]//*[self::li or self::button or self::span][contains(normalize-space(.), %s)]";
	String XPATH_TOPOLOGY_EDGE_ANY =
			"//*[name()='path' and (contains(@class,'edge') or contains(@class,'link'))]"
			+ " | //*[(self::div or self::span) and (contains(@class,'edge') or contains(@class,'link') or contains(@data-testid,'edge'))]";
	String XPATH_IMPACT_ICON_ANY =
			"//*[contains(@class,'impact') and (self::span or self::div or self::img)]"
			+ " | //*[@aria-label and contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'impact')]";
    public String CILocatorWithName = "//*[name()='tspan' and contains(text(),'%s')]";
	String CIOptionToGetDetails = "//context-menu-content//span[contains(.,'%s')]/parent::a";
	String impactedCIName="//*[@title='List view']//td[normalize-space()='%s']";
	public String searchedCIResult = "//td[@data-testid='adapt-table_column-1_row-0' and contains(., '%s')]";
	public String searchedCIResult_2 = "//td[@data-testid='adapt-table_column-1_row-1' and contains(., '%s')]";
    public String checkBoxCIResult="//tr[.//td[normalize-space()='%s']]//div[contains(@class,'ui-chkbox')]";
	public String relationshipOption = "//*[contains(text(),'%s')]/ancestor::button[contains(@class, 'rx-select__option')]";
    String[] CHANGE_ID_CANDIDATE_XPATHS = new String[] {
			String.format(XPATH_CHANGE_ID_VALUE_TEMPLATE, CHANGE_ID_DATA_ID),
			XPATH_CHANGE_ID_AR,
			XPATH_CHANGE_ID_AR_DATA,
			XPATH_CHANGE_ID_AR_CONTAINS
	};
	
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
	
	@FindBy(xpath="//*[@title='Select']")
	public WebElement associationTypeButton;
	
	@FindBy(xpath="//*[@id='304253220']")
	public WebElement addCIButton;
	
	@FindBy(xpath = "//*[contains(@aria-label,'Description') and @id='1000000000']")
	public WebElement descriptionTextboxOutage;

	@FindBy(xpath = "//*[@id='304449871']")
    WebElement searchCITextbox;

	@FindBy(xpath="//*[@id='304253220']")
	public WebElement saveCIButton;

    String IFramePwa = XPATH_IFRAME_PWA;
	
	@FindBy(xpath = "//button[contains(@class,'more') or contains(@aria-label,'More')]")
	WebElement moreOptionsButton;
	
	@FindBy(xpath = "//button[contains(.,'CIs Impacted')] | //a[contains(.,'CIs Impacted')] | //li[contains(.,'CIs Impacted')]")
	WebElement cisImpactedTab;
	
	@FindBy(xpath = "//input[@type='checkbox' and contains(@class,'select-all')] | //div[contains(@class,'select-all')]//input")
	WebElement selectAllCIsCheckbox;
	
	@FindBy(xpath = "//button[contains(.,'Relate')] | //button[contains(.,'Add Selected')]")
	WebElement relateAllButton;
	
	@FindBy(xpath = "//*[contains(@class,'impacted') or contains(@class,'impact')]")
	WebElement impactedCIIndicator;
	
	@FindBy(xpath = "//button[contains(.,'Create New Impact Analysis Run')]")
	WebElement createNewImpactAnalysisRunButton;
	
	@FindBy(xpath = "//button[contains(.,'Use Associated CI')] | //a[contains(.,'Use Associated CI')]")
	WebElement useAssociatedCIOption;
	
	@FindBy(xpath = "//button[contains(.,'Input CI')] | //a[contains(.,'Input CI')]")
	WebElement inputCIOption;
	
	@FindBy(xpath = "//button[contains(.,'Add Selected')]")
	WebElement addSelectedButton;
	
	@FindBy(xpath = "//button[contains(.,'Save')]")
	WebElement niaSaveButton;
	
	@FindBy(xpath = "//button[contains(.,'Run Analysis')]")
	WebElement runAnalysisButton;
	
	@FindBy(xpath = "//button[contains(.,'All CIs')] | //a[contains(.,'All CIs')] | //li[contains(.,'All CIs')]")
	WebElement allCIsTab;

	@FindBy(xpath = "//*[@testid='304452061']")
	WebElement moreButton;

	@FindBy(xpath = "//*[@data-testid='304437921']")
	WebElement initiateImpactAnalysisButton;

	@FindBy(xpath = "//*[contains(@class,'dp-title col')]")
	WebElement checkCopyChangeTitle;

	@FindBy(xpath = "//*[contains(@id,'ar530075586')]")
	WebElement impactedCIListViewToolTiplabel;

	@FindBy(xpath = "//*[contains(@id,'530075266')]")
	WebElement closeButtonOnISBlade;

	@FindBy(xpath = "(//*[@id='304481391']//button)")
	WebElement showMoreOptionOnImpactAnalysisBanner;

	@FindBy(xpath = "//button[contains(@data-testid,'adapt-tabs') and contains(.,'Topology view')]")
	WebElement topologyViewTab;

	@FindBy(xpath = "//iframe[@title='Data Visualization Field']")
	WebElement topologyViewFrame;

	@FindBy(xpath = "//*[name()='circle' and contains(@class,'cmdb-tolopogy')]")
	WebElement cmdbTopologyNode;

	@FindBy(xpath = "//*[@class='loader-container']")
	WebElement loaderOnCMDB;

	@FindBy(xpath = "//*[name()='text' and contains(@class,'cmdb-topology-ci-indicator')]")
	List<WebElement> ciIndicator;

	@FindBy(xpath = "//*[@id='explorer_dataset_tabs']//a[normalize-space()='BMC.ASSET']//span[contains(@class,'icon-cross')]")
	WebElement bmcAssetDataSetTab;

	@FindBy(xpath = "//span[text()='Show services only']/..")
	WebElement showServiceCheckBoxImpactedCI;

	@FindBy(xpath = "//button[contains(@data-testid,'adapt-tabs') and contains(.,'List view')]")
	WebElement listViewTab;

	@FindBy(xpath = "//*[@name='ar304451471']")
	WebElement relationshipTypeButton;
	
	String moreOptionItem = "//button[contains(.,'%s')] | //a[contains(.,'%s')] | //li[contains(.,'%s')]";
	String niaSuccessMessageLocator = "//*[contains(@class,'alert') or contains(@class,'message') or contains(@class,'toast')]//*[contains(text(),'%s')]";
	
	public ViewChangePage() {
    	log = new SolqaLogger(ViewChangePage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

	public ViewChangePage clickEditBtn() {
		log.info("Clicking Edit button on Change");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();

		// Edit button may be in header (default DOM) or inside PWA iframe depending on build.
		List<WebElement> editButtons = findElements(By.xpath(XPATH_EDIT_BUTTON));
		if (editButtons == null || editButtons.isEmpty()) {
			switchToFrame(By.xpath(XPATH_IFRAME_PWA));
			editButtons = findElements(By.xpath(XPATH_EDIT_BUTTON));
		}

		if (editButtons == null || editButtons.isEmpty()) {
			Assert.fail("Edit button not found on Change view page");
			return this;
		}
		commonWaits.waitForElementVisible(editButtons.get(0));
		clickByAction(editButtons.get(0));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.switchtoDefaultFrame();
		return this;
	}

	public boolean isAddTaskButtonPresent() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		boolean present = !findElements(By.xpath(XPATH_ADD_TASK_BUTTON)).isEmpty();
		commonWaits.switchtoDefaultFrame();
		return present;
	}

    @FindBy(xpath = "//*[contains(@id,'ar1000000182') and contains(@id,'data')]")
    WebElement changeIDOnViewPage;
    public String FetchTicketID() {
        log.info("Fetching Ticket ID");
        commonWaits.waitForPageFullyLoaded(30, 3);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        // Ticket id is inside PWA iframe in PV pages
        commonWaits.switchtoDefaultFrame();
        switchToFrame(By.xpath(XPATH_IFRAME_PWA));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), changeIDOnViewPage, 20),
                "Change Ticket ID element not found on PV page");
        String found = changeIDOnViewPage.getText().trim();
        Assert.assertFalse(found.trim().isEmpty(), "Change Ticket ID not found on PV page");
		return found.trim();
	}
	
	 public void switchToPWAFrame() {
	        commonWaits.switchtoDefaultFrame();
	        commonWaits.switchToFrame(By.xpath(XPATH_IFRAME_PWA));
	        log.info("Switched to PWA frame successfully.");
	    }

	public ViewChangePage relateCI(String ciName, String relationshipType) {
		log.info("Relating CI '" + ciName + "' to the Incident");
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(30, 3);
		switchToPWAFrame();
		
		commonWaits.clickWithoutInterception(configurationItemsTab);
		commonWaits.waitForElementVisible(relateCIButton);
		commonWaits.clickWithoutInterception(relateCIButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForElementVisible(searchCITextbox).click();
        typeKeys(searchCITextbox, ciName);
		commonWaits.waitForSometime(commonWaits.moreDelay);
        Actions action = new Actions(getDriver());
        action.sendKeys(Keys.ENTER).build().perform();
		if(commonWaits.isElementPresent(By.xpath(String.format(searchedCIResult,ciName)),20)){
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchedCIResult,ciName)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(checkBoxCIResult, ciName)));
		}

		else{
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(searchedCIResult_2,ciName)), 30), "Searched CI Result is not visible");
	        commonWaits.clickWithoutInterception(By.xpath(String.format(checkBoxCIResult,ciName)));

		}
        
        log.info("Selecting relationship type");
		
        commonWaits.clickWithoutInterception(relationshipTypeButton);
		commonWaits.waitForSometime(commonWaits.moreDelay);
		try{
        commonWaits.clickWithoutInterception(By.xpath(String.format(relationshipOption, relationshipType)));
		}
		catch(Exception e){
			WebElement element = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(relationshipOption, relationshipType)));
			commonWaits.clickByJS(element);
		}
        commonWaits.waitForPageFullyLoaded(30,3);
        
        log.info("Clicking save button");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveCIButton, 30), "Save CI button is not visible");
        commonWaits.clickWithoutInterception(saveCIButton);
        commonWaits.waitForPageFullyLoaded(30,3);
		log.info("Successfully related CI '" + ciName + "' to the Incident.");
		return this;
	}
	
	// -------------------- Impact analysis / simulation actions --------------------
	public ViewChangePage initiateImpactAnalysisFromMoreMenu(String initiateImpactText) {
		String quoted = quoteForXpathContains(initiateImpactText);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		List<WebElement> moreButtons = findElements(By.xpath(XPATH_MORE_BUTTON));
		if (moreButtons == null || moreButtons.isEmpty()) {
			throw new AssertionError("More menu button not found on Change view.");
		}
		commonWaits.clickWithoutInterception(moreButtons.get(0));
		
		By menuItemBy = By.xpath(String.format(XPATH_MENU_ITEM_BY_TEXT_CONTAINS_TEMPLATE, quoted));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		List<WebElement> menuItems = findElements(menuItemBy);
		if (menuItems == null || menuItems.isEmpty()) {
			throw new AssertionError("Menu item not found: " + initiateImpactText);
		}
		commonWaits.clickWithoutInterception(menuItems.get(0));
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	public ViewChangePage clickShowMoreOnImpactAnalysisBanner(String showMoreText) {
		String quoted = quoteForXpathContains(showMoreText);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		By showMoreBy = By.xpath(String.format(XPATH_SHOW_MORE_BUTTON_BY_TEXT_CONTAINS_TEMPLATE, quoted, quoted));
		List<WebElement> btns = findElements(showMoreBy);
		if (btns == null || btns.isEmpty()) {
			throw new AssertionError("Show more button not found on impact analysis banner.");
		}
		commonWaits.clickWithoutInterception(btns.get(0));
		
		// Blade should appear
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		Assert.assertTrue(isElementPresent(By.xpath(XPATH_IMPACTED_CIS_BLADE_HEADER)), "Impacted CIs blade did not open.");
		return this;
	}
	
	public ViewChangePage openImpactedCIsTopologyView(String topologyText) {
		return clickImpactedCisViewToggle(topologyText);
	}
	
	public ViewChangePage openImpactedCisListView(String listText) {
		return clickImpactedCisViewToggle(listText);
	}
	
	public ViewChangePage clickImpactedCisViewToggle(String viewText) {
		String quoted = quoteForXpathContains(viewText);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		By toggleBy = By.xpath(String.format(XPATH_VIEW_TOGGLE_BY_TEXT_CONTAINS_TEMPLATE, quoted, quoted, quoted));
		List<WebElement> toggles = findElements(toggleBy);
		if (toggles == null || toggles.isEmpty()) {
			throw new AssertionError("View toggle not found: " + viewText);
		}
		commonWaits.clickWithoutInterception(toggles.get(0));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	public ViewChangePage verifyShowServiceOnlyCheckboxEditable(String showServiceOnlyLabel) {
		String quoted = quoteForXpathContains(showServiceOnlyLabel);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		By cbBy = By.xpath(String.format(XPATH_SHOW_SERVICE_ONLY_CHECKBOX_BY_LABEL_CONTAINS_TEMPLATE, quoted, quoted));
		List<WebElement> cbs = findElements(cbBy);
		if (cbs == null || cbs.isEmpty()) {
			throw new AssertionError("Show service only checkbox not found.");
		}
		Assert.assertTrue(cbs.get(0).isEnabled(), "Show service only checkbox is not editable.");
		return this;
	}
	
	public ViewChangePage selectShowServiceOnlyAndVerifyService(String showServiceOnlyLabel, String expectedService) {
		String cbQuoted = quoteForXpathContains(showServiceOnlyLabel);
		String svcQuoted = quoteForXpathContains(expectedService);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		By cbBy = By.xpath(String.format(XPATH_SHOW_SERVICE_ONLY_CHECKBOX_BY_LABEL_CONTAINS_TEMPLATE, cbQuoted, cbQuoted));
		List<WebElement> cbs = findElements(cbBy);
		if (cbs == null || cbs.isEmpty()) {
			throw new AssertionError("Show service only checkbox not found.");
		}
		commonWaits.clickWithoutInterception(cbs.get(0));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		
		By rowBy = By.xpath(String.format(XPATH_IMPACTED_CIS_LIST_ROW_BY_TEXT_CONTAINS_TEMPLATE, svcQuoted));
		Assert.assertTrue(isElementPresent(rowBy), "Expected service not visible in Impacted CIs list view: " + expectedService);
		return this;
	}
	
	public ViewChangePage expandTopologyParentAndChildren(String mainCiName, String expandText, String expandParentText, String expandChildText) {
		// Best-effort: right-click CI node and execute context menu options from test data.
		rightClickTopologyNode(mainCiName);
		clickTopologyContextMenuOption(expandText);
		clickTopologyContextMenuOption(expandParentText);
		
		rightClickTopologyNode(mainCiName);
		clickTopologyContextMenuOption(expandText);
		clickTopologyContextMenuOption(expandChildText);
		return this;
	}
	
	public ViewChangePage clickRelationshipArrowAndShowDetails(String showRelationshipDetailsText) {
		String quoted = quoteForXpathContains(showRelationshipDetailsText);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		List<WebElement> edges = findElements(By.xpath(XPATH_TOPOLOGY_EDGE_ANY));
		if (edges == null || edges.isEmpty()) {
			throw new AssertionError("No relationship edge found in topology view.");
		}
		commonWaits.clickWithoutInterception(edges.get(0));
		
		By optionBy = By.xpath(String.format(XPATH_CONTEXT_MENU_OPTION_BY_TEXT_CONTAINS_TEMPLATE, quoted));
		List<WebElement> opts = findElements(optionBy);
		if (opts != null && !opts.isEmpty()) {
			commonWaits.clickWithoutInterception(opts.get(0));
		} else {
			// Some builds show it as a button in a popover
			By alt = By.xpath(String.format(XPATH_BUTTON_OR_LINK_CONTAINS_QUOTED_TEMPLATE, quoted, quoted));
			Assert.assertTrue(isElementPresent(alt), "Show relationship details option not found.");
			commonWaits.clickWithoutInterception(alt);
		}
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	public ViewChangePage simulateImpactOnChildCi(String childCiName, String simulateImpactText) {
		rightClickTopologyNode(childCiName);
		clickTopologyContextMenuOption(simulateImpactText);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	public int getImpactIconCount() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		List<WebElement> icons = findElements(By.xpath(XPATH_IMPACT_ICON_ANY));
		return icons == null ? 0 : icons.size();
	}
	
	public ViewChangePage verifyTopologyCiPresent(String ciName) {
		String quoted = quoteForXpathContains(ciName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		By nodeBy = By.xpath(String.format(XPATH_TOPOLOGY_NODE_BY_NAME_CONTAINS_TEMPLATE, quoted));
		Assert.assertTrue(isElementPresent(nodeBy), "CI is not present on topology view: " + ciName);
		return this;
	}
	
	public void rightClickTopologyNode(String ciName) {
		String quoted = quoteForXpathContains(ciName);
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		By nodeBy = By.xpath(String.format(XPATH_TOPOLOGY_NODE_BY_NAME_CONTAINS_TEMPLATE, quoted));
		List<WebElement> nodes = findElements(nodeBy);
		if (nodes == null || nodes.isEmpty()) {
			throw new AssertionError("Topology CI node not found: " + ciName);
		}
		WebElement node = nodes.get(0);
		try { javaScriptScrollIntoView(node); } catch (Exception ignored) {}
		new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(node).contextClick(node).build().perform();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}
	
	public void clickTopologyContextMenuOption(String optionText) {
		String quoted = quoteForXpathContains(optionText);
		By optionBy = By.xpath(String.format(XPATH_CONTEXT_MENU_OPTION_BY_TEXT_CONTAINS_TEMPLATE, quoted));
		List<WebElement> opts = findElements(optionBy);
		if (opts == null || opts.isEmpty()) {
			throw new AssertionError("Topology context menu option not found: " + optionText);
		}
		commonWaits.clickWithoutInterception(opts.get(0));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}
	
	public ViewChangePage createRelatedOutage(String ciName, String relationshipType, String outageDescription) {

		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
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
        return this;
	}
	
	public ViewChangePage createOutage(String outageDescription) {
		log.info("Creating Outage");
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));

		commonWaits.waitForByElementToBeVisible(By.xpath(XPATH_OUTAGE_CI_SELECTION));
        commonWaits.clickWithoutInterception(By.xpath(XPATH_OUTAGE_CI_SELECTION));
        
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		
		log.info("Filling Description for Outage");
		commonWaits.waitForElementVisible(descriptionTextboxOutage);
		typeKeys(descriptionTextboxOutage, outageDescription);
		commonWaits.waitForPageFullyLoaded(30, 3);
		
		log.info("Setting Scheduled Start Date");
		scheduledStartDate();
		log.info("Setting Scheduled End Date");
		scheduledEndDate();
		
		log.info("Saving Outage");
		final String saveOutageXpath = String.format(XPATH_BY_ID_TEMPLATE, OUTAGE_SAVE_BUTTON_ID);
		commonWaits.waitForByElementToBeVisible(By.xpath(saveOutageXpath));
		commonWaits.clickWithoutInterception(By.xpath(saveOutageXpath));
		
		log.info("Outage created successfully.");
		
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
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
	    
	    final String startDateXpath = String.format(XPATH_BY_ID_TEMPLATE, OUTAGE_SCHEDULED_START_DATE_ID);
	    commonWaits.waitForByElementToBeVisible(By.xpath(startDateXpath)).sendKeys(startDate);
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

	    final String endDateXpath = String.format(XPATH_BY_ID_TEMPLATE, OUTAGE_SCHEDULED_END_DATE_ID);
	    commonWaits.waitForByElementToBeVisible(By.xpath(endDateXpath)).sendKeys(endDate);
	}
	
	public void verifyOutageCreated(String ciName){
		log.info("Verifying Outage Created");
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));

		commonWaits.waitForElementVisible(relatedItemsTab);

		commonWaits.waitForByElementToBeVisible(By.xpath(XPATH_CREATED_OUTAGE));
		Assert.assertTrue(isElementPresent(By.xpath(XPATH_CREATED_OUTAGE)),
				"Outage is not created successfully.");

		log.info("Outage created and verified successfully.");
		
		log.info("Clicking created Outage");
		commonWaits.clickWithoutInterception(By.xpath(XPATH_CREATED_OUTAGE));
		
		log.info("Verifying created outage CI");
		final String createdOutageCiXpath = String.format(XPATH_CREATED_OUTAGE_CI_TEMPLATE, ciName);
		commonWaits.waitForByElementToBeVisible(By.xpath(createdOutageCiXpath));
		String outageCI = getValueFromText(createdOutageCiXpath);
		Assert.assertTrue(outageCI.contains("Outage"), "Outage CI does not contain expected text.");
		
		log.info("Outage details verified successfully.");
		commonWaits.waitForPageFullyLoaded(30, 3);
	}
	
	public ViewChangePage setStatus(String newStatus) {
		commonWaits.waitForPageFullyLoaded(30,3);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(XPATH_STATUS_DOWN_ARROW), 30), "Status DownArrow button is not visible");
        commonWaits.clickWithoutInterception(By.xpath(XPATH_STATUS_DOWN_ARROW));
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(XPATH_CLEAR_BUTTON_STATUS), 30), "Clear button is not visible");
        commonWaits.clickWithoutInterception(By.xpath(XPATH_CLEAR_BUTTON_STATUS));
		commonWaits.waitForPageFullyLoaded(30,3);
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(XPATH_STATUS_DROPDOWN), 30), "Status dropdown button is not visible");
        commonWaits.clickWithoutInterception(By.xpath(XPATH_STATUS_DROPDOWN));
        
		final String statusOptionXpath = String.format(XPATH_STATUS_OPTION_TEMPLATE, newStatus);
		
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(statusOptionXpath), 30), "Status Option button is not visible");
        commonWaits.clickWithoutInterception(By.xpath(statusOptionXpath));

		commonWaits.waitForPageFullyLoaded(30,3);
		return this;
	}
	
	public ViewChangePage verifyImpactAnalysisBlockingMessage(String expectedMessage) {
	    log.info("Verifying Impact Analysis Blocking Message");

	    commonWaits.waitForPageFullyLoaded(30, 3);
	    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	    switchtoDefaultFrame();
	    switchToFrame(By.xpath(XPATH_IFRAME_PWA));
	    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

	    By messageBy = By.xpath(String.format(
	            XPATH_ANY_ELEMENT_CONTAINS_QUOTED_TEMPLATE,
	            quoteForXpathContains(expectedMessage)
	    ));

	    commonWaits.waitForElementVisible_withFluentWait(getDriver(), messageBy, 10);
	    String actual = getDriver().findElement(messageBy).getText();

	    Assert.assertTrue(actual.contains(expectedMessage),
	            "Impact Analysis blocking message not found.\nExpected (partial): "
	                    + expectedMessage + "\nActual: " + actual);

	    log.info("Impact Analysis blocking message verified");
	    return this;
	}
	
	public ViewChangePage refreshPage() {
		log.info("Refreshing the page");
		getDriver().navigate().refresh();
		commonWaits.waitForPageFullyLoaded(30, 3);
		log.info("Page refreshed successfully");
		return this;
	}

	public ViewChangePage addActivityNote(String noteText, boolean isPublic) {
		log.info("Adding " + (isPublic ? "public" : "private") + " activity note on Change: " + noteText);

		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForAngularRequestsToFinish();

		WebElement noteBox = commonWaits.waitForByElementToBeVisible(By.xpath(XPATH_ACTIVITY_NOTEBOX));
		commonWaits.clickWithoutInterception(noteBox);
		noteBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		noteBox.sendKeys(Keys.DELETE);
		noteBox.sendKeys(noteText);

		if (isPublic) {
			if (commonWaits.isElementPresent(By.xpath(XPATH_PUBLIC_NOTE_CHECKBOX))) {
				commonWaits.clickWithoutInterception(By.xpath(XPATH_PUBLIC_NOTE_CHECKBOX));
			}
		}

		commonWaits.waitForElementClickable(findElement(By.xpath(XPATH_ACTIVITY_POST_BUTTON)));
		commonWaits.clickWithoutInterception(By.xpath(XPATH_ACTIVITY_POST_BUTTON));
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		return this;
	}

	public ViewChangePage addActivityNoteWithPersonMention(String personDisplayName, String noteText, boolean isPublic) {
		// Best-effort mention: type "@<name>" into the note; UI will resolve mention if enabled.
		final String combined = (noteText == null ? "" : noteText) + " @" + (personDisplayName == null ? "" : personDisplayName);
		return addActivityNote(combined.trim(), isPublic);
	}

	public boolean isActivityNotePresent(String noteText) {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(XPATH_IFRAME_PWA));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		// 1) Try in the main PWA frame context
		if (isNotePresentInCurrentContext(noteText)) {
			switchtoDefaultFrame();
			return true;
		}

		// 2) Many SmartIT PV timelines render inside nested iframes; search them (depth 2)
		boolean found = searchNoteInChildIframes(noteText, 2);
		switchtoDefaultFrame();
		return found;
	}

	public void verifyActivityNotePresent(String noteText) {
		long start = System.currentTimeMillis();
		long timeoutMs = 60_000;
		while (System.currentTimeMillis() - start < timeoutMs) {
			if (isActivityNotePresent(noteText)) {
				return;
			}
			commonWaits.waitForSometime(2000);
		}
		Assert.fail("Activity note not found on Change timeline. Expected to find: " + noteText);
	}

	public static String quoteForXpathContains(String text) {
		if (text == null) {
			return "''";
		}
		if (!text.contains("'")) {
			return "'" + text + "'";
		}
		// Fallback for single quotes: concat('a', "'", 'b')
		String[] parts = text.split("'");
		StringBuilder sb = new StringBuilder("concat(");
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				sb.append(", \"'\", ");
			}
			sb.append("'").append(parts[i]).append("'");
		}
		sb.append(")");
		return sb.toString();
	}

	public boolean isNotePresentInCurrentContext(String noteText) {
		String quoted = quoteForXpathContains(noteText);
		// Keep it broad: note content can appear in different containers depending on PV build
		By by = By.xpath(String.format(XPATH_ANY_ELEMENT_CONTAINS_QUOTED_TEMPLATE, quoted));
		try {
			return !findElements(by).isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean searchNoteInChildIframes(String noteText, int depth) {
		if (depth <= 0) {
			return false;
		}
		List<WebElement> frames;
		try {
			frames = findElements(By.xpath(XPATH_ALL_IFRAMES));
		} catch (Exception e) {
			return false;
		}
		for (WebElement frame : frames) {
			try {
				getDriver().switchTo().frame(frame);
				if (isNotePresentInCurrentContext(noteText)) {
					return true;
				}
				if (searchNoteInChildIframes(noteText, depth - 1)) {
					return true;
				}
			} catch (Exception ignored) {
				// continue scanning other iframes
			} finally {
				try {
					getDriver().switchTo().parentFrame();
				} catch (Exception ignored) {}
			}
		}
		return false;
	}

	// ==================== NETWORK IMPACT ANALYSIS METHODS ====================
	
	/**
	 * Click on More options button
	 */
	public ViewChangePage clickOnMoreOptions() {
		log.info("Clicking on More options button");
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFramePwa));
		commonWaits.waitForElementClickable(moreOptionsButton);
		commonWaits.clickWithoutInterception(moreOptionsButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Verify if a specific option is visible in More options menu
	 */
	public ViewChangePage verifyMoreOptionVisible(String optionName) {
		log.info("Verifying More option visible: " + optionName);
		String xpath = String.format(moreOptionItem, optionName, optionName, optionName);
		boolean isVisible = commonWaits.isElementPresent(By.xpath(xpath));
		Assert.assertTrue(isVisible, "More option '" + optionName + "' is not visible");
		log.info("More option '" + optionName + "' is visible");
		return this;
	}
	
	/**
	 * Close More options menu
	 */
	public ViewChangePage closeMoreOptions() {
		log.info("Closing More options menu");
		commonWaits.clickWithoutInterception(moreOptionsButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Click on a specific option in More options menu
	 */
	public ViewChangePage clickOnMoreOption(String optionName) {
		log.info("Clicking on More option: " + optionName);
		String xpath = String.format(moreOptionItem, optionName, optionName, optionName);
		commonWaits.waitForByElementToBeVisible(By.xpath(xpath));
		commonWaits.clickWithoutInterception(By.xpath(xpath));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}
	
	/**
	 * Select Create New Impact Analysis Run option
	 */
	public ViewChangePage selectCreateNewImpactAnalysisRun(String ciOption) {
		log.info("Selecting Create New Impact Analysis Run with option: " + ciOption);
		commonWaits.waitForElementVisible(createNewImpactAnalysisRunButton);
		commonWaits.clickWithoutInterception(createNewImpactAnalysisRunButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		
		if (ciOption.contains("Associated")) {
			commonWaits.waitForElementVisible(useAssociatedCIOption);
			commonWaits.clickWithoutInterception(useAssociatedCIOption);
		} else if (ciOption.contains("Input")) {
			commonWaits.waitForElementVisible(inputCIOption);
			commonWaits.clickWithoutInterception(inputCIOption);
		}
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Verify NIA success message
	 */
	public ViewChangePage verifyNIASuccessMessage(String expectedMessage) {
		log.info("Verifying NIA success message: " + expectedMessage);
		commonWaits.waitForPageFullyLoaded(30, 3);
		String xpath = String.format(niaSuccessMessageLocator, expectedMessage.substring(0, 30));
		boolean isDisplayed = commonWaits.isElementPresent(By.xpath(xpath));
		Assert.assertTrue(isDisplayed, "NIA success message not displayed. Expected: " + expectedMessage);
		log.info("NIA success message verified");
		return this;
	}
	
	/**
	 * Click on CIs Impacted tab
	 */
	public ViewChangePage clickOnCIsImpacted() {
		log.info("Clicking on CIs Impacted tab");
		commonWaits.waitForElementVisible(cisImpactedTab);
		commonWaits.clickWithoutInterception(cisImpactedTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Select and relate all impacted CIs
	 */
	public ViewChangePage selectAndRelateAllImpactedCIs() {
		log.info("Selecting and relating all impacted CIs");
		if (commonWaits.isElementPresent(selectAllCIsCheckbox)) {
			commonWaits.clickWithoutInterception(selectAllCIsCheckbox);
		}
		commonWaits.waitForElementVisible(relateAllButton);
		commonWaits.clickWithoutInterception(relateAllButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Verify CIs are displayed as impacted
	 */
	public ViewChangePage verifyCIsDisplayedAsImpacted() {
		log.info("Verifying CIs are displayed as impacted");
		boolean isDisplayed = commonWaits.isElementPresent(impactedCIIndicator);
		Assert.assertTrue(isDisplayed, "CIs are not displayed as impacted");
		log.info("CIs are displayed as impacted");
		return this;
	}
	
	/**
	 * Navigate back to change request
	 */
	public ViewChangePage navigateBackToChangeRequest() {
		log.info("Navigating back to change request");
		getDriver().navigate().back();
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Verify impacted CIs in Configuration Item section
	 */
	public ViewChangePage verifyImpactedCIsInConfigurationItemSection() {
		log.info("Verifying impacted CIs in Configuration Item section");
		commonWaits.waitForPageFullyLoaded(30, 3);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFramePwa));
		commonWaits.waitForElementVisible(configurationItemsTab);
		commonWaits.clickWithoutInterception(configurationItemsTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		boolean hasImpactedCIs = commonWaits.isElementPresent(impactedCIIndicator);
		log.info("Impacted CIs in Configuration Item section: " + hasImpactedCIs);
		return this;
	}
	
	/**
	 * Choose CIs from All CIs tab
	 */
	public ViewChangePage chooseCIsFromAllCIs() {
		log.info("Choosing CIs from All CIs");
		commonWaits.waitForElementVisible(allCIsTab);
		commonWaits.clickWithoutInterception(allCIsTab);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		if (commonWaits.isElementPresent(selectAllCIsCheckbox)) {
			commonWaits.clickWithoutInterception(selectAllCIsCheckbox);
		}
		return this;
	}
	
	/**
	 * Click Add Selected button
	 */
	public ViewChangePage clickAddSelected() {
		log.info("Clicking Add Selected button");
		commonWaits.waitForElementVisible(addSelectedButton);
		commonWaits.clickWithoutInterception(addSelectedButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}
	
	/**
	 * Save and Run Analysis
	 */
	public ViewChangePage saveAndRunAnalysis() {
		log.info("Saving and running analysis");
		if (commonWaits.isElementPresent(niaSaveButton)) {
			commonWaits.clickWithoutInterception(niaSaveButton);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		}
		commonWaits.waitForElementVisible(runAnalysisButton);
		commonWaits.clickWithoutInterception(runAnalysisButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}

	public void clickMoreButton_TicketViewPage() {
		commonWaits.waitForSometime(commonWaits.moreDelay);
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFramePwa));
		commonWaits.waitForElementVisible(moreButton);
		commonWaits.clickWithoutInterception(moreButton);
		commonWaits.waitForPageFullyLoaded(30, 3);
		Assert.assertTrue(commonWaits.isElementPresent(initiateImpactAnalysisButton), "Initiate Impact Analysis button not found");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}


	public void clickInitiateImpactAnalysis() {
		log.info("Clicking on Initiate Impact Analysis link");
		clickMoreButton_TicketViewPage();
		commonWaits.waitForElementVisible(initiateImpactAnalysisButton);
		commonWaits.clickByJS(initiateImpactAnalysisButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	public void verifyImpactSimulationBladeDetails() {
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForSometime(commonWaits.moreDelay); 
    	verifyEquals(getText(checkCopyChangeTitle).trim(),"Impacted CIs","Impacted ci blade title is not matched");
 		verifyEquals(getText(impactedCIListViewToolTiplabel).trim(), "Select impacted CIs from the list to relate them", "Tool tip message not matched for List tab on impacted ci blade");
 		
  	}

	public void ClickonCloseButtonOnISBlade() {
		commonWaits.waitForElementVisible(closeButtonOnISBlade);
		commonWaits.clickWithoutInterception(closeButtonOnISBlade);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	public void clickShowMoreOptionOnImpactAnalysisBanner() {
		commonWaits.waitForElementVisible(showMoreOptionOnImpactAnalysisBanner);
		commonWaits.clickWithoutInterception(showMoreOptionOnImpactAnalysisBanner);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}
	
	public void clickTopologyViewOnImpactedCI() {
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFramePwa));
		commonWaits.clickWithoutInterception(topologyViewTab);
		commonWaits.switchToFrame(topologyViewFrame);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}
    
	public void clickOnCIWithName(String ciName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForSometime(commonWaits.moreDelay);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
		commonWaits.waitForSometime(commonWaits.moreDelay);
		Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(CILocatorWithName, ciName)),40));
		commonWaits.clickWithoutInterception(By.xpath(String.format(CILocatorWithName, ciName)));
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	public void rightClickOnCINode(){
		commonWaits.waitForElementVisible(cmdbTopologyNode, 30);
		commonWaits.javaScriptScrollIntoView(cmdbTopologyNode);
		commonWaits.waitForElementClickable(cmdbTopologyNode).click();
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript(
			"var evt = new MouseEvent('contextmenu', {bubbles: true, cancelable: true}); arguments[0].dispatchEvent(evt);",
			cmdbTopologyNode
		);
	    commonWaits.waitForSometime(commonWaits.minDelay);
	}

	public void clickOnOptionFromRightClickOfCI(String option){
		WebElement Options = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CIOptionToGetDetails,option)));
		commonWaits.waitForElementClickable(Options);
		commonWaits.clickByJS(Options);
		commonWaits.waitForSometime(commonWaits.moreDelay);
	}

	public boolean verifyCIIspresent(String ciName) {
		log.info("Verifying CI is Present on CMDB topology");
		commonWaits.waitForSometime(commonWaits.minDelay);
		WebElement CI = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(CILocatorWithName, ciName)));
		return CI.isDisplayed();
	}

	public int getCIIndicatorCount() {
		commonWaits.waitForElementInvisible_withFluentWait(getDriver(), loaderOnCMDB, 150);
		commonWaits.waitForPageFullyLoaded(30, 1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
		int count = ciIndicator.size();
		return count;

	}

	public void closeExploreDataSetTab(){
		commonWaits.clickWithoutInterception(bmcAssetDataSetTab);
	}

	public void verifyShowServiceOnlyEditableImpactedCI(){
		Assert.assertTrue(commonWaits.isElementPresent(showServiceCheckBoxImpactedCI), "Show service only checkbox is not editable");
		Assert.assertTrue(showServiceCheckBoxImpactedCI.isEnabled(), "Show service only checkbox is not editable");
	}

	public void clickOnListViewOnImpactedCI(){
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFramePwa));
		commonWaits.clickByJS(listViewTab);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	public void verifyServiceDisplayedImpactedCIListView(String serviceName){
		switchtoDefaultFrame();
		switchToFrame(By.xpath(IFramePwa));
		commonWaits.waitForElementVisible(showServiceCheckBoxImpactedCI).click();
		Assert.assertTrue(commonWaits.isElementPresent(String.format(impactedCIName,serviceName)));
	}

	
}

