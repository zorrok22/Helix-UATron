package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class GlobalSearchStudioPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits; 
    
    //String locators
    public String itsmPage = "//a[@href='#/create/smart-recorder']";
    
    //WebElements Locators
    
    @FindBy(xpath="//textarea[@placeholder='Ask a question']")
    WebElement questionTextArea;
    
    @FindBy(xpath = "//textarea[@placeholder='Ask a follow up...']")
    WebElement followUpQuestionTextArea;
    
    @FindBy(xpath = "//*[contains(@class,'d-icon-send')]")
    WebElement sendButton;
    
    @FindBy(xpath = "//*[@aria-haspopup='listbox']")
    WebElement ticketAndKnowledgeDropdown;
    
    @FindBy(xpath = "//*[@placeholder='Type search text and press enter.']")
    WebElement searchTextArea;
    
    @FindBy(xpath = "//*[@type='button' and contains(@class,'adapt-rx-search__search-button')]")
    WebElement searchIcon;
    
    @FindBy(xpath = "//*[contains(@data-testid,'clear')]")
    WebElement clearIcon;
    
    @FindBy(xpath = "//button[contains(@class,'btn') and normalize-space()='Search']")
    WebElement searchButton;
    
    @FindBy(xpath = "//button[contains(@class,'btn') and normalize-space()='Chat']")
    WebElement chatButton;
    
    public String DropdownOption = "//button[@role='option' and normalize-space()='%s']";
    public String chatSearchButton= "//*[contains(@class,'d-inline-flex' )]/button[contains(.,'%s')]";
    public String searchedResult = "//div[contains(text(),'%s')]";
    public String headerTitleKnowledgeArticle = "//h1[contains(@class,'translation-title') and contains(text(),'%s')]";
    public String viewStyle ="//adapt-button[@title='%s']";
    public String IFramePWA= "//iframe[@id='pwa-frame']";
    public String IFrameStudio= "//iframe[@id='studio-frame' and not(contains(@class, 'ng-hide'))]";
    public String IframeComponentList="//iframe[contains(@name,'ccIframeComponent')]";
    public String IframeComponentListRquest="//iframe[contains(@src,'Preview')]";
    public String IFrameComponentCard="//*[@class='search-smartit-preview-view']//child::iframe[contains(@name,'ccIframeComponent')]";
    public String IFramePWAView  = "//iframe[@id='pwa-view']";
    public String IFrameSmartIT = "//iframe[@id='smart-it']";
    public String viewFullRequestXpath = "//a[@aria-label='View Full Request' and normalize-space()='View Full Request']";
	public String relatedTicketXpath = "//a[@ux-id='related-ticket-id']";
	public String viewFullArticleXpath = "//a[.//span[normalize-space()='View full article']]";
	public String viewFullArticleCardViewXpath = "(//rx-action-button[contains(@class,'primary')]//a[contains(.,'View full article')])[2]";

    
    
    public GlobalSearchStudioPage() {
    	log = new SolqaLogger(GlobalSearchStudioPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }
    
    public GlobalSearchStudioPage verifyGlobalSearchStudioPageLaunchedSuccessfully() {
    	commonWaits.switchtoDefaultFrame();
    	commonWaits.waitForByElementToBeVisible(By.xpath(IFrameStudio));
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
    	log.info("Verifying GLobal Search Studio Page is launched successfully");
    	boolean isChatButtonPresent = commonWaits.waitForElementVisible_withFluentWait(getDriver(), chatButton , commonWaits.defaultTimeout);
        Assert.assertTrue(isChatButtonPresent, "Global Search Studio Page is not launched successfully");
        return this;
    }
    
    public GlobalSearchStudioPage clickOnChatSearchButton(String buttonType) {
    	commonWaits.switchtoDefaultFrame();
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
    	log.info("Click on Button:" + buttonType);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(By.xpath(String.format(chatSearchButton, buttonType)))) {
        	commonWaits.clickWithoutInterception(By.xpath(String.format(chatSearchButton, buttonType)));
        }
        
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }
    
    public GlobalSearchStudioPage askQuestionInChat(String question) {
    	commonWaits.switchtoDefaultFrame();
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
    	commonWaits.waitForPageFullyLoaded(10, 1);
    	log.info("Ask Question in Chat");
    	commonWaits.waitForElementVisible(questionTextArea);
    	typeKeys(questionTextArea, question); 
    	commonWaits.waitForElementClickable(sendButton);
    	sendButton.click();
    	commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
    	return this;
    }
    
	public GlobalSearchStudioPage askFollowUpQuestionInChat(String followUpQuestion) {
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(By.xpath(IFrameStudio));
		log.info("Ask Follow Up Question in Chat");
		commonWaits.waitForElementVisible(followUpQuestionTextArea);
		typeKeys(followUpQuestionTextArea, followUpQuestion);
		commonWaits.waitForElementClickable(sendButton);
		sendButton.click();
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
    
	public GlobalSearchStudioPage selectTicketAndKnowledgeDropdown(String searchDropdownOption) {
		commonWaits.switchtoDefaultFrame();
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
		log.info("Select Ticket and Knowledge Dropdown");
		commonWaits.waitForElementVisible(ticketAndKnowledgeDropdown);
		ticketAndKnowledgeDropdown.click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropdownOption, searchDropdownOption)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(DropdownOption, searchDropdownOption)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	public GlobalSearchStudioPage searchRequestedFilter(String inputText) {
		commonWaits.switchtoDefaultFrame();
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
		log.info("Search Requested Filter : Ticket/Knowledge/request");
		commonWaits.waitForElementVisible(searchTextArea);
		typeKeys(searchTextArea, inputText);
		commonWaits.waitForElementVisible(searchIcon);
		searchIcon.click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	public GlobalSearchStudioPage selectViewStyle(String viewStyle) {
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(By.xpath(IFrameStudio));
		log.info("Select View Style");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(this.viewStyle, viewStyle)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(this.viewStyle, viewStyle)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	public GlobalSearchStudioPage verifyAndClickSearchedResult(String result) {
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(By.xpath(IFrameStudio));
		log.info("Verify Searched Knowledge/Ticket/request Result");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchedResult, result)));
		String searchedResultValue= getValueFromText(String.format(searchedResult, result));
		Assert.assertTrue(searchedResultValue.contains(result),
				"Searched Knowledge/Ticket/request Result is not displayed as expected");
		log.info("Click on Searched Knowledge/Ticket/request Result");
		commonWaits.clickWithoutInterception(By.xpath(String.format(searchedResult, result)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	public GlobalSearchStudioPage verifyResultPreviewListView(String articleName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
    	commonWaits.switchToFrame(By.xpath(IframeComponentList));
		log.info("Verify Knowledge Article Preview");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(headerTitleKnowledgeArticle, articleName)));
		String headerTitle = getValueFromText(String.format(headerTitleKnowledgeArticle, articleName));
		Assert.assertTrue(headerTitle.contains(articleName), "Knowledge Article Preview is not displayed as expected");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	public GlobalSearchStudioPage verifyResultPreviewCardView(String input) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
    	commonWaits.switchToFrame(By.xpath(IFrameStudio));
    	commonWaits.switchToFrame(By.xpath(IFrameComponentCard));
		log.info("Verify Knowledge Article Preview");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(headerTitleKnowledgeArticle, input)));
		String headerTitle = getValueFromText(String.format(headerTitleKnowledgeArticle, input));
		Assert.assertTrue(headerTitle.contains(input), "Knowledge Article Preview is not displayed as expected");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("Verified Knowledge Article Preview");
		return this;
	}
	
	public GlobalSearchStudioPage clickOnViewFullResultListView(String input) {
		log.info("Click on View Full Result - List View");
		clickOnViewFullArticle(input);
		return this;
	}
	
	public GlobalSearchStudioPage clickOnViewFullResultCardView(String input) {
		log.info("Click on View Full Result - Card View");
		clickOnViewFullArticleCardView(input);
		return this;
	}
	
	public GlobalSearchStudioPage clearSearchedResult() {
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(By.xpath(IFrameStudio));
		log.info("Clear Searched Result");
		commonWaits.waitForElementVisible(clearIcon);
		commonWaits.clickWithoutInterception(clearIcon);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.switchtoDefaultFrame();
		return this;
	}
	
	private void switchToCorrectIframe(String elementXpath) {
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(elementXpath), 5)) {
			log.info("Element found in main document");
			return;
		}

		try {
			commonWaits.switchToFrame(By.xpath(IFrameStudio));
			if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(elementXpath), 10)) {
				log.info("Element found in studio frame");
				return;
			}

			try {
				commonWaits.waitForSometime(4000); // Wait for potential iframe loading
				commonWaits.switchToFrame(By.xpath(IframeComponentList));
				if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(elementXpath), 10)) {
					log.info("Element found in studio -> component list frame");
					return;
				}
			} catch (Exception e) {
				log.warn("Component list frame not accessible or element not found: " + e.getMessage());
			}
		} catch (Exception e) {
	        log.warn("Studio frame not accessible: " + e.getMessage());
	    } finally {
	        commonWaits.switchtoDefaultFrame();
	    }
		throw new RuntimeException("Element not found in any accessible iframe: " + elementXpath);
	}
	
	public GlobalSearchStudioPage clickOnViewFullArticleCardView(String articleLink) {
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		log.info("Click on View Full Article (Card View) with link: " + articleLink);
		
		// Switch to correct iframe
		commonWaits.switchToFrame(IFrameStudio);

		int existingWindowCount = getDriver().getWindowHandles().size();
		log.info("Existing window count: " + existingWindowCount);

		commonWaits.waitForByElementToBeVisible(By.xpath(viewFullArticleCardViewXpath));
		commonWaits.clickWithoutInterception(By.xpath(viewFullArticleCardViewXpath));

		commonWaits.switchToLastWindow();

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForURLContains(articleLink);

		commonWaits.switchToTabByIndex(0);
		return this;
	}

	// Generic clickOnViewFullArticle method that works with any view
	public GlobalSearchStudioPage clickOnViewFullArticle(String articleLink) {
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		log.info("Click on View Full Article with link: " + articleLink);
		
		// Switch to correct iframe
		switchToFrame(IFrameStudio);
		
		int existingWindowCount = getDriver().getWindowHandles().size();
		log.info("Existing window count: " + existingWindowCount);
		
		commonWaits.waitForByElementToBeVisible(By.xpath(viewFullArticleXpath));
		commonWaits.clickWithoutInterception(By.xpath(viewFullArticleXpath));
		
		commonWaits.switchToLastWindow();
		
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForURLContains(articleLink);
		
		commonWaits.switchToTabByIndex(0);
		return this;
	}
	
	public GlobalSearchStudioPage clickOnViewFullRequest(String requestLink) {
		log.info("Click on View Full Request with link: " + requestLink);
		ViewFullRequest(requestLink);
		return this;
    }
	
	public GlobalSearchStudioPage ViewFullRequest(String requestLink) {
		log.info("Click on View Full Request with link: " + requestLink);

		commonWaits.switchtoDefaultFrame();
		switchToFrameInsideFrame(IFrameStudio, IframeComponentListRquest);
		
		commonWaits.waitForByElementToBeVisible(By.xpath(viewFullRequestXpath));
		commonWaits.clickWithoutInterception(By.xpath(viewFullRequestXpath));

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		// URL contains GUID instead of REQ ID, so check for "request" in URL
		commonWaits.waitForURLContains("request");
		return this;
	}
	
	/**
	 * Opens the related incident link in a new tab from SBE Request page
	 * Gets the incident ID from UI and opens it in new tab
	 * @return The incident ID that was opened
	 */
	public String openRelatedIncidentInNewTab() {
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForByElementToBeVisible(By.xpath(relatedTicketXpath));
		String incidentID = getValueFromText(relatedTicketXpath);
		log.info("Opening related incident " + incidentID + " in new tab");
		new util.CommonMethods().openLinkInNewTab(By.xpath(relatedTicketXpath));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForURLContains("incident");
		return incidentID;
	}

}