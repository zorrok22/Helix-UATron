package pages.ITSM.SmartIT;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.HelixGPTUtil;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartITHelixGPTPage extends BasePage {

	static SolqaLogger log = new SolqaLogger(ViewIncidentPage.class.getName());
	private final CommonWaits commonWaits = new CommonWaits();
	private final CommonMethods commonMethods = new CommonMethods();
	HelixGPTUtil helixGPTUtil=new HelixGPTUtil();
	
	public SmartITHelixGPTPage(){
       PageFactory.initElements(getDriver(), this);
    }

	// -------------------- Locators / constants (avoid inline strings) --------------------
	private static final String ITEM_TYPE_KNOWLEDGE = "Knowledge";
	private static final String ITEM_TYPE_TICKET = "Ticket";
	private static final String ITEM_TYPE_PEOPLE = "People";
	private static final String ITEM_TYPE_ASSET = "Asset";
	private static final String VIEW_FULL_ARTICLE = "View Full Article";

	private static final long GPT_RESPONSE_TIMEOUT_MS = 60_000; // 1 minute
	private static final int GPT_RESPONSE_STABLE_REPEAT_COUNT = 3;

	// String locators
	private static final String XPATH_ASK_GPT_OPTION = "//button[normalize-space()='%s']";
	private static final String XPATH_SEARCH_TOGGLE_BUTTON_ACTIVE = "//*[normalize-space()='%s' and contains(@class,'active')]";
	private static final String XPATH_TOGGLE_BUTTON = "//*[normalize-space()='%s']";
	private static final String XPATH_CATEGORY_ITEM = "//button[contains(@class,'rx-select__option')]//*[text()='%s']";
	private static final String XPATH_RESULT_LIST_ELEMENT = "((//bwf-search-result-item-card)[%s]/div//div/div)[%s]";
	private static final String XPATH_PREVIEW_TICKET_ID = "//*[@title='%s']";
	private static final String XPATH_CONTAINS_TEXT = "//*[contains(text(), '%s')]";
	private static final String XPATH_FIRST_ARTICLE = "//button[contains(normalize-space(),'%s')]";
	private static final String XPATH_SEARCH_RESULT_ITEM_CARD = "//bwf-search-result-item-card";
	private static final By SEARCH_RESULT_ITEM_CARD_BY = By.xpath(XPATH_SEARCH_RESULT_ITEM_CARD);


   //WebElements
	@FindBy(xpath = "//iframe[@id='pwa-frame']")
	private WebElement IFrame;

	@FindBy(xpath = "//span[contains(@class,'icon-helix_gpt') ]/parent::span/parent::button")
	public WebElement AskHelixGPTTab;

    @FindBy(xpath = "//a[@id='header-helixGPT_search_button']")
	public WebElement SearchIcon;

    @FindBy(xpath = "(//iframe[@id='studio-frame'])[1]")
	public WebElement gptIframe;

    @FindBy(xpath = "//bwf-search-category//button[contains(@class, 'dropdown-toggle')]")
	public WebElement searchDropdown;

    @FindBy(xpath = "//adapt-dropdown-window2//button//adapt-highlight")
	public List<WebElement> searchDropdwonValues;

    @FindBy(xpath = "//button[contains(@id,'rx-select')]")
	public WebElement categoryDropdown;

    @FindBy(xpath = "//input[contains(@id,'rx-search')]")
	public WebElement searchTextBox;

    @FindBy(xpath = "//button[contains(@class, 'btn btn-secondary')]/span[@class='d-icon-search ng-star-inserted']")
    public WebElement searchIcon;

    @FindBy(xpath = "//h1[contains(@class,'bwf-search-result')]//span")
	public WebElement resultCount;

    @FindBy(xpath = "//iframe[@name='ccIframeComponent' and contains(@class,'full-screen ng-star-inserted')]")
	public WebElement searchResultPreviewFrame;

    @FindBy(xpath = "//a[contains(@class, 'smt-link-btn')]")
	public WebElement viewFullTicketLink;

    @FindBy(xpath = "//*[contains(@class,'text-break smt-link-btn')]")
	public WebElement viewFullPersonOrAsset;

	@FindBy(xpath = "//div[@class='input-group']//textarea")
	public WebElement TextArea;

	@FindBy(xpath = "//button[contains(.,'Send')]")
	public WebElement sendButtonIcon;

	@FindBy(xpath = "//div[contains(@class,'helix-gpt-chat-thread') and contains(@data-testid,'helixGPTChatMessage')]")
	public List<WebElement> allMessages;

	@FindBy(xpath = "(//div[contains(@data-testid,'helixGPTChat')]//table)[last()]//th")
	public List<WebElement> allMessagesTable;

	@FindBy(xpath = "(//div[contains(@data-testid,'helixGPTChat')]//table)[last()]//td")
	public List<WebElement> allMessagesTable_2;

	@FindBy(xpath = "//div[contains(@class,'summary-gpt__description')]")
	public List<WebElement> AskGPTAllMessages;

	@FindBy(xpath = "//div[contains(@data-testid,'helixGPTChat')]//td")
	public WebElement allMessagesFromTable;

	@FindBy(xpath = "(//*[contains(@data-testid,'helixGPT')]//a)[1]")
	public WebElement firstRecord;

	@FindBy(xpath = "//button[contains(.,'New topic')]")
    WebElement NewTopic;

	@FindBy(xpath = "//button[@id='304484131' and @aria-label='Ask HelixGPT']")
	public WebElement resolutionNoteGPT;

	@FindBy(xpath = "(//*[@id='ar1000000156_data'])")
	public WebElement resolutionNoteViewPage;

	public void navigateToAskGPTOption() {
		log.info("Navigating to Ask Helix GPT option.");
		switchtoDefaultFrame();
		switchToFrame(IFrame);
		commonWaits.waitForElementClickable(AskHelixGPTTab).click();
		commonWaits.waitForExecutionComplete();
	}

	public void verifySummarizeActivityAndTicketOptions(String Option1, String Option2) {
		log.info("Verifying summarize activity and ticket options: " + Option1 + ", " + Option2);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		WebElement Opt1=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(XPATH_ASK_GPT_OPTION,Option1)));
		WebElement Opt2=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(XPATH_ASK_GPT_OPTION,Option2)));
		Assert.assertTrue(Opt1.isDisplayed(), "Option "+Option1+" is not displayed");
		Assert.assertTrue(Opt2.isDisplayed(), "Option "+Option2+" is not displayed");
		log.info("Both options are displayed: " + Option1 + " and " + Option2);
		commonWaits.waitForExecutionComplete();
	}

	public void clickSummarizeOptions(String Option) {
		log.info("Clicking summarize option: " + Option);
		WebElement Opt1=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(XPATH_ASK_GPT_OPTION,Option)));
		commonWaits.clickWithoutInterception(Opt1);
		commonWaits.waitForExecutionComplete();
	}

	public String getallGPTResponceString(WebElement responseElement) {
		log.info("Getting all GPT response string from element.");
		StringBuilder responseBuilder = new StringBuilder();
		String lastLine = null;
		int repeatCount = 0;
		long startTime = System.currentTimeMillis();
		boolean endOfResponseDetected = false;

		while (!endOfResponseDetected && (System.currentTimeMillis() - startTime < GPT_RESPONSE_TIMEOUT_MS)) {
			String fullText = responseElement.getText();
			String[] lines = fullText.split("\\r?\\n");

			if (lines.length > 0) {
				String currentLine = lines[lines.length - 1];
				responseBuilder.setLength(0);
				for (String line : lines) {
					responseBuilder.append(line).append("\n");
				}

				if (lastLine != null && lastLine.equals(currentLine)) {
					repeatCount++;
				} else {
					repeatCount = 1;
					lastLine = currentLine;
				}

				endOfResponseDetected = repeatCount >= GPT_RESPONSE_STABLE_REPEAT_COUNT;
			}

			if (!endOfResponseDetected) {
				try {
					commonWaits.waitForSometime(commonWaits.moreDelay);
				} catch (Exception e) {
					Thread.currentThread().interrupt();
					endOfResponseDetected = true;
				}
			}
		}

		log.info("Response collected: " + responseBuilder.toString().trim());
		return responseBuilder.toString().trim();
	}

	public void clickGlobalSearchButtonAfterHelixGPTSearchEnabled() {
		log.info("Clicking global search button after HelixGPT search enabled.");
		switchtoDefaultFrame();
		commonWaits.waitForElementClickable(SearchIcon).click();
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	public void verifyGlobalSearchButtonAfterHelixGPTSearchEnabled() {
		log.info("Verifying global search button after HelixGPT search enabled.");
		switchtoDefaultFrame();
		Assert.assertTrue(SearchIcon.isDisplayed(), "Search icon after enabling Helix GPT is not shown");
		commonWaits.waitForExecutionComplete();
	}

	public void defaultSearchToggleButton(String button) {
		log.info("Checking default search toggle button: " + button);
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
			if( commonWaits.isElementPresent(By.xpath(String.format(XPATH_SEARCH_TOGGLE_BUTTON_ACTIVE, button)))) {
				log.info("Default search toggle button is already set to: " + button);
			} else {
				log.info("Default search toggle button is not set to: " + button + ". Setting it now.");
				By buttonLocator = By.xpath(String.format(XPATH_TOGGLE_BUTTON, button));
				WebElement ele = commonWaits.waitForByElementToBeVisible(buttonLocator);
				commonWaits.clickWithoutInterception(ele);
			}
	}

	public void verifyFilterOptionsOnSearchDropdownMenus(){
		log.info("Verifying filter options on search dropdown menus.");
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
		commonWaits.waitForElementVisible(searchDropdown,10);
		commonWaits.waitForElementClickable(searchDropdown,10).click();
		List<String> values = Arrays.asList(SolQAConstants.FILTER_TICKET,
				SolQAConstants.FILTER_KNOWLEDGE,
				SolQAConstants.FILTER_PEOPLE,
				SolQAConstants.FILTER_TICKET_KNOWLEDGE, // Default selected
				SolQAConstants.FILTER_ASSET,
				SolQAConstants.FILTER_DWP_REQUEST);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		for (String value:values){
			searchDropdwonValues.stream().filter(e -> e.getText().trim() != value).findFirst()
					.orElseThrow(()-> new AssertionError(" found the value, " + value + " in search dropdown"));
		}
		searchDropdown.click();
		commonWaits.waitForExecutionComplete();
	}

	public void selectSearchCategory(String category) {

		log.info("Selecting search category: " + category);
		commonWaits.waitForPageFullyLoaded(50, 0);
		switchtoDefaultFrame();
		switchToFrame(gptIframe);
		click(categoryDropdown);
		click(String.format(XPATH_CATEGORY_ITEM, category));
	}


	public SmartITHelixGPTPage selectFilterOptionAndSearchOnHelixGPTSearchbox(String TicketType, String textToSearch){
		log.info("Select filter option from search dropdown - "+ TicketType +" and search text - "+ textToSearch);
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
		selectSearchCategory(TicketType);
		click(searchTextBox);
		typeKeys(searchTextBox, textToSearch);
		click(searchIcon);
		commonWaits.waitForPageFullyLoaded(50, 0);
		return this;
	}

	public void searchPeopleAndOpenProfile(String userName) {
		selectSearchCategory(ITEM_TYPE_PEOPLE);
		click(searchTextBox);
		searchTextBox.clear();
		searchTextBox.sendKeys(userName);
		searchTextBox.sendKeys(Keys.ENTER);
		commonWaits.waitForPageFullyLoaded(50, 0);

		Assert.assertTrue(commonWaits.waitForElementPresent(SEARCH_RESULT_ITEM_CARD_BY), "No People search result card found");
		List<WebElement> cards = findElements(SEARCH_RESULT_ITEM_CARD_BY);
		Assert.assertTrue(cards != null && !cards.isEmpty(), "No People search result card found");

		WebElement match = null;
		boolean found = false;
		for (WebElement card : cards) {
			try {
				String txt = card.getText();
				if (txt != null && txt.toLowerCase().contains(userName.toLowerCase())) {
					match = card;
					found = true;
				}
			} catch (Exception ignored) {}
		}
		Assert.assertTrue(found, "No People result found");

		int windowCount = getDriver().getWindowHandles().size();
		commonWaits.clickWithoutInterception(match);
		commonWaits.waitForPageFullyLoaded(50, 0);
		int newWindowCount = getDriver().getWindowHandles().size();
		Assert.assertEquals(newWindowCount, windowCount, "People profile opened in a new window unexpectedly.");
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
	}

	public void searchAssetAndOpenProfile(String assetName) {
		log.info("Searching Asset for: " + assetName);
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);

		selectSearchCategory(ITEM_TYPE_ASSET);
		click(searchTextBox);
		searchTextBox.clear();
		searchTextBox.sendKeys(assetName);
		searchTextBox.sendKeys(Keys.ENTER);
		commonWaits.waitForPageFullyLoaded(50, 0);

		Assert.assertTrue(commonWaits.waitForElementPresent(SEARCH_RESULT_ITEM_CARD_BY), "No Asset search result card found");
		List<WebElement> cards = findElements(SEARCH_RESULT_ITEM_CARD_BY);
		Assert.assertTrue(cards != null && !cards.isEmpty(), "No Asset search result card found");

		WebElement match = null;
		boolean found = false;
		for (WebElement card : cards) {
			try {
				String txt = card.getText();
				if (txt != null && txt.toLowerCase().contains(assetName.toLowerCase())) {
					match = card;
					found = true;
				}
			} catch (Exception ignored) {}
		}
		Assert.assertTrue(found, "No Asset result found");

		int windowCount = getDriver().getWindowHandles().size();
		commonWaits.clickWithoutInterception(match);
		commonWaits.waitForPageFullyLoaded(50, 0);
		int newWindowCount = getDriver().getWindowHandles().size();
		Assert.assertEquals(newWindowCount, windowCount, "Asset profile opened in a new window unexpectedly.");

		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
	}

	public int getResultCount() {
			log.info("Getting result count from search results.");
			String text = resultCount.getText();
			return Integer.parseInt(text.subSequence(1, text.length()-1).toString());
		}


  public void verifyCountOfItemsReturnedAndOpenFullView(String itemType, int itemlevel, int itemline){
		log.info("Verifying count of items returned and opening full view for itemType: " + itemType);
		commonWaits.waitForAngularRequestsToFinish();
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
		int count = getResultCount();
		log.info("Count is - "+ count);
		commonWaits.waitForPageFullyLoaded(50, 0);
		click(String.format(XPATH_RESULT_LIST_ELEMENT, itemlevel, itemline));
		commonWaits.waitForPageFullyLoaded(50, 0);
		String ticketId =commonWaits.waitForByElementToBeVisible(By.xpath(String.format(XPATH_RESULT_LIST_ELEMENT, itemlevel, itemline))).getText();
		log.info("Ticket ID is - "+ticketId);
		commonWaits.waitForAngularRequestsToFinish();
		verifyTrue(ticketId.contains(itemType), "Item is not returned by the search result when filter is applied for "+ itemType);
		if(count>0){
			commonWaits.waitForPageFullyLoaded(100, 0);
			try {
				switchtoDefaultFrame();
				switchToFrame(gptIframe);
				switchToFrame(searchResultPreviewFrame);
				commonWaits.waitForSometime(commonWaits.moreDelay);
				commonWaits.waitForElementVisible_withFluentWait(getDriver(),String.format(XPATH_PREVIEW_TICKET_ID, ticketId.trim()), 60);
				verifyTrue(commonWaits.isElementPresent(String.format(XPATH_PREVIEW_TICKET_ID, ticketId.trim())), "Not correct ticket is loaded in preview.");
			}
			catch(AssertionError e) {
				switchtoDefaultFrame();
				switchToFrame(gptIframe);
				switchToFrame(searchResultPreviewFrame);
				commonWaits.waitForSometime(commonWaits.moreDelay);
				commonWaits.waitForElementVisible_withFluentWait(getDriver(),String.format(XPATH_PREVIEW_TICKET_ID, ticketId), 60);
				verifyTrue(commonWaits.isElementPresent(String.format(XPATH_PREVIEW_TICKET_ID, ticketId)), "Not correct ticket is loaded in preview.");
			}
			if (ITEM_TYPE_KNOWLEDGE.equals(itemType)) {
				log.info("Clicking View Full Article for Knowledge item.");
				click(String.format(XPATH_CONTAINS_TEXT, VIEW_FULL_ARTICLE));
				switchtoDefaultFrame();
				commonWaits.waitForPageFullyLoaded(50, 0);
			} else if (ITEM_TYPE_TICKET.equals(itemType)) {
				log.info("Clicking View Full Ticket link for Ticket item.");
				click(viewFullTicketLink);
				switchtoDefaultFrame();
				commonWaits.waitForPageFullyLoaded(50, 0);
			} else if (ITEM_TYPE_PEOPLE.equals(itemType) || ITEM_TYPE_ASSET.equals(itemType)) {
				log.info("Clicking View Full Person/Asset for " + itemType + " item.");
				click(viewFullPersonOrAsset);
				switchtoDefaultFrame();
				commonWaits.waitForPageFullyLoaded(50, 0);
			}

		}
		commonWaits.waitForPageFullyLoaded(50, 0);
	}

	public void switchToSmartITPage(WebDriver webDriver) {
		log.info("----------- Switching to SmartIT Page ----------------------");
		String windowHandle = webDriver.getWindowHandle();
		webDriver.switchTo().window(windowHandle);
		setDriver(webDriver);
		log.info("Switched to SmartIT window successfully.");
	}

	public void sendMessageInHelixGPTChat(String message) {
		log.info("Sending message in HelixGPT chat: " + message);
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);
		commonWaits.waitForElementVisible(TextArea);
		TextArea.sendKeys(message);
		commonWaits.waitForElementClickable(sendButtonIcon).click();
		commonWaits.waitForExecutionComplete();
	}

	public String getAllGPTResponseString() {
		try{
		// Ensure we are inside the GPT iframe before reading messages
		switchtoDefaultFrame();
		commonWaits.switchToFrame(gptIframe);

		// Try collecting from table-based messages first, then from message elements.
		String tablePart = "";
		try {
			tablePart = helixGPTUtil.getAllGPTResponseString(allMessagesTable);
			tablePart = helixGPTUtil.getAllGPTResponseString(allMessagesTable_2);
		} catch (Exception ignored) {
			tablePart = helixGPTUtil.getAllGPTResponseString(allMessagesTable_2);
		}

		String messagesPart = helixGPTUtil.getAllGPTResponseString(allMessages);

		if (!tablePart.isEmpty()) {
			return tablePart + "\n" + messagesPart;
		}
		return messagesPart;
	}catch (Exception e){
		log.error("Error while collecting GPT response: " + e.getMessage());
		// Best-effort fallback
		return helixGPTUtil.getAllGPTResponseString(allMessages);
	}
}

	public String getAllAskGPTResponseString() {
		return helixGPTUtil.getAllGPTResponseString(AskGPTAllMessages);
	}

	public boolean verifyProviderNameInURL(String providerName) {
		log.info("Verifying provider name in URL: " + providerName);
		String originalHandle = getDriver().getWindowHandle();

		for (String handle : getDriver().getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				getDriver().switchTo().window(handle);
				try {
					if (getDriver().getCurrentUrl().contains(providerName)) {
						log.info("Provider name found in URL: " + getDriver().getCurrentUrl());
						return true; // Provider found, no need to continue
					}
				} finally {
					getDriver().switchTo().window(originalHandle);
				}
			}
		}
		 log.info("Provider name not found in any tab.");
		 return false; // Provider name not found in any tab

	}

	 public void openFirstRecordFromTable() {
			log.info("Opening first record from table.");
			commonWaits.waitForElementClickable(firstRecord);
			commonWaits.clickWithoutInterception(firstRecord);
	  }

	   public void clickOnNewTopic() {
		  log.info("Clicking on New Topic button.");
		  commonWaits.clickWithoutInterception(NewTopic);
		  commonWaits.waitForExecutionComplete();
	}

	public boolean isElementPresentAndDisplayed(WebElement  locator) {
		    try {
		        return locator.isDisplayed();
		    } catch (NoSuchElementException e) {
		        return false; // Element not found â†’ return false safely
		    }
		}

		public void clickOnResolutionNoteAskHelixGPT() {
			commonWaits.clickWithoutInterception(resolutionNoteGPT);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHelixGPT);
			commonWaits.waitForExecutionComplete();
		}

	public String getResolutionNote_viewPage() {
		switchtoDefaultFrame();
		switchToFrame(IFrame);
		return resolutionNoteViewPage.getAttribute("innerText");
	}

	public void openArticle(String Article){
		WebElement Articlelink=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(XPATH_FIRST_ARTICLE, Article)));
        commonWaits.waitForElementClickable(Articlelink).click();
        commonWaits.waitForExecutionComplete();
    }

	public SmartITHelixGPTPage clickGlobalSearchButtonAfterHelixGPTSearchEnabledAndOpenInNewTab() {
		log.info("Clicking global search button after HelixGPT search enabled.");
		switchtoDefaultFrame();
		commonMethods.openLinkInNewTab(SearchIcon);
		commonWaits.waitForExecutionComplete();
		commonWaits.switchToLastWindow();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return this;
	}


}
