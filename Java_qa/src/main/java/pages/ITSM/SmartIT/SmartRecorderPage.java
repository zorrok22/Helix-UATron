package pages.ITSM.SmartIT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import framework.BasePage;
import org.testng.Assert;
import pages.ITSM.dwp.CatalogPage;
import pages.ITSM.dwp.DWPCommonOperations;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartRecorderPage extends BasePage{

	static SolqaLogger log;
	CommonWaits commonWaits;
	CommonMethods commonMethods;
    CatalogPage catalogPage;
    DWPCommonOperations dwpCommonOperations;

    public SmartRecorderPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(SmartRecorderPage.class.getName());
        commonWaits = new CommonWaits();
        catalogPage = new CatalogPage();
		commonMethods = new CommonMethods();
        dwpCommonOperations = new DWPCommonOperations();
    }

	// Locators (use String only for dynamic templates)
	public String ElementXpathByID = "//*[@ux-id= '%s']";
	public String typeheadListElementID= "//*[contains(@class, 'smart-recorder__popup-item') and contains(normalize-space(.), '%s')]";
	public String recommendedDWPTempSelection= "//*[@ux-id='rs-dwp-catalog']//*[@ux-id='template-type']/..//*[@ux-id='template-name' and text()='%s']/../../..//i[@ux-id='toggle-selection']";
	private static final By DWP_CATALOG_SHOW_MORE_LINK_BY =
			By.xpath("//*[@ux-id='dwp-catalogs']//*[@ux-id='show-more-btn' and text()='Show more']");
	private static final By NO_TEMPLATE_CATALOGUE_BY =
			By.xpath("//div[contains(text(), 'No recommended catalogs found')]");
	public String recommendedKATitle= "//div[@ux-id='rs-recommended-ka']//div[@ux-id='rs-ka-title' and contains(text(), '%s')]";
	public String recommendedTempSelectionValue="//div[@ux-id='template-name' and text()='%s']/ancestor::div[contains(@class,'list-item')]//i[@ux-id='toggle-selection']";
	public String recommendedKnowledgeSelection= "//div[@title='%s']";
	public String headerTitleKnowledgeArticle = "//h1[contains(text(),'%s')]";
	private static final By IFRAME_HKM_BY = By.xpath("//iframe[@ux-id='hkm__frame']");
	public String templateNameLocator = "//div[@ux-id='smartrecorder-popup']//p[text()='Digital Workplace Catalog']/../p[text()='%s']";
	public String elementXpathByUiId= "//*[@ux-id='%s']";
	public String hkmTitleInputXpath = "//input[contains(@id,'%s')]";
	private static final String COMMENT_BODY_XPATH = "//body[contains(@class,'contents')]";
	String selectItemOption = "//button[contains(@role,'option') and contains(.,'%s')]";

    //WebElements
	@FindBy(xpath = "//div[@ux-id= 'smart-recorder-textarea']")
	public WebElement smartRecorderTextArea;

	@FindBy(xpath = "//dwp-form-control-label[normalize-space() = 'When do you need it by? (required)']/following-sibling::*//input")
	public WebElement selectDate;

	@FindBy(xpath = "//button[contains(@aria-label,'Who are you')]")
	public WebElement DropDownSelect;

	@FindBy(xpath = "//input[contains(@id,'rx-search')]")
	public WebElement userDropDown;

	@FindBy(xpath = "//button[@role='option']/div/div")
	public WebElement DropDownValueSelect;

	@FindBy(xpath = "//div[contains(@ux-id,'refresh')]/i[@class='icon-refresh']")
	public WebElement refreshIcon;

	@FindBy(xpath = "//iframe[@ux-id='dwp-window']")
	public WebElement dwpCatalogIframe;

	@FindBy(xpath = "//div[contains(@data-ta,'submitRequestBtn')]//button")
	public WebElement SubmitButton;
	
	@FindBy(xpath = "//button[contains(@aria-label,'Select item')]")
	public WebElement selectItemButton;
  
	@FindBy(xpath = "//iframe[contains(@class, 'cke_wysiwyg_frame cke_reset')]")
	public WebElement commentIframe;
  
	@FindBy(xpath = "//iframe[contains(@ux-id,'dwp-window')]")
	public WebElement requestIframe;

	@FindBy(xpath = "//*[@id='username']")
	private WebElement userName;

	@FindBy(xpath = "//*[@id='password']")
	private WebElement passWord;

	@FindBy(xpath = "//*[@id='loginBtn']")
	private WebElement loginButton;

	@FindBy(xpath = "//*[text()='Title']/../../../..//input[@placeholder='Add content']")
	private WebElement articleTitle;
	
	@FindBy(xpath = "//label[normalize-space()='Internal Notes']/parent::*/parent::*/parent::*//div[contains(@class,'fr-element')]")
	private WebElement articleInternalNotes;

	@FindBy(xpath = "//div/button[contains(@class,'dropdown')]/div[contains(.,'Work In Progress')]")
	private WebElement articleStatusdropDown;

	@FindBy(xpath = "//div[@role='listbox']//div[normalize-space()='Published']")
	private WebElement publishStatus;

	@FindBy(xpath = "//div[@role='listbox']//div[normalize-space()='Approved']")
	private WebElement approvedStatus;

	@FindBy(xpath = "//button[contains(@class,'star-inserted') and contains(normalize-space(),'Save')]")
	private WebElement saveButton;

	@FindBy(xpath = "//span[contains(text(),'New Content')]")
	private WebElement newContent;

	@FindBy(xpath = "//button[text()='Save here']")
	private WebElement savehereButton;

	@FindBy(xpath = "//adapt-toast//button[@aria-label='Close message']")
	private WebElement closePopup;

	@FindBy(xpath = "//adapt-toast//button[@aria-label='Close message']/../../div//div[contains(@class,'toast__detail')]")
	private WebElement articleSavePopup;

	@FindBy(xpath = "//*[contains(@class,'d-icon-eye')]/following-sibling::span")
	private WebElement viewCount;

	@FindBy(xpath = "//*[contains(@class,'d-icon-thumbs_up')]/following-sibling::span")
	private WebElement likes;

	@FindBy(xpath = "//*[@id='index-search-input']")
	private WebElement searchArticleInput;

	@FindBy(xpath = "//*[@id='search-button']")
	private WebElement searchBtn;

	@FindBy(xpath = "//button[contains(@class,'thumbs-up')]")
	private WebElement likeButton;

	@FindBy(xpath = "//div[@id='search-result']//li//span[contains(@class,'custom')]")
	private List<WebElement> articles;

	@FindBy(xpath = "//div[@data-uib-tooltip='Edit article']")
	private WebElement editArticle;

	@FindBy(xpath = "//button[@data-uib-tooltip='Delete article']")
	private WebElement deleteArticle;

	@FindBy(xpath = "//button[text()='Delete']")
	private WebElement confirmDelete;

	@FindBy(xpath = "//*[normalize-space()='Filtering tags']/parent::div//div[text()='Start typing']")
	private WebElement filterTag;

	@FindBy(xpath = "//*[normalize-space()='Filtering tags']/parent::div//div//input")
	private WebElement inputTag;

	private String hkmTitle = "//*[normalize-space()='%s']";
	@FindBy(xpath = "//div[@class='system-alert__dismiss' or @aria-label='Cancel']")
	WebElement systemAlert;
	
	@FindBy(xpath = "//button[normalize-space()='Ask HelixGPT']")
	WebElement AskGPTButton;
	
	@FindBy(xpath = "//button[normalize-space()='Yes please validate']")
	WebElement YesPleaseValidateButton;
	
	@FindBy(xpath = "//span[contains(@class,'border-top ng')]")
	WebElement gptLatestResponce;

	@FindBy(xpath = "//button[@ux-id='create-btn']")
	WebElement createTicketBtn;

	@FindBy(xpath = "//div[@ux-id='create-incident']")
	WebElement createIncFrmCreateTicket;

	@FindBy(xpath = "//div[@ux-id='create-workorder']")
	WebElement createWOFrmCreateTicket;

	@FindBy(xpath = "//*[@id='ar301395300']")
	WebElement setCustomerOnWOPV_xpath;

	@FindBy(xpath = "//button[normalize-space()='Search for knowledge resources']")
	WebElement searchKnowledgeBtn;

	@FindBy(xpath = "//button[normalize-space()='Create a new article']")
	WebElement createNewArticleBtn;

    //Strings
	public String viewFullArticle="hkm-view-full-article__button";
	public String saveToTicket="save-to-ticket-btn";
	public String deleteFromTicket="delete-btn";
	public String saveAndResolve="save-resolve-btn";
	public String createTicket="create-btn";
	public String createIncidentButton="create-incident";
	public String createWorkorderButton="create-workorder";
	private static final String VIEW_FULL_ARTICLE_UX_ID="hkm-view-full-article__button";
	private static final String SAVE_TO_TICKET_UX_ID="save-to-ticket-btn";
	private static final String DELETE_FROM_TICKET_UX_ID="delete-btn";
	private static final String SAVE_AND_RESOLVE_UX_ID="save-resolve-btn";
	private static final String CREATE_TICKET_UX_ID="create-btn";
	private static final String CREATE_INCIDENT_UX_ID="create-incident";
	private static final String CREATE_WORKORDER_UX_ID="create-workorder";
	private static final String COMPANY_TAG="Company=Petramco";

	// -------------------- HKM / ComAround (Smart Recorder knowledge search + create new) --------------------
	private static final String ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String PIN_TOGGLE_FROM_TITLE =
			"(" +
					"%s/ancestor::*[self::div or self::tr or self::li][1]" +
					"//*[contains(@ux-id,'toggle-selection') or contains(@aria-label,'Pin') or contains(@title,'Pin') " +
					"or contains(translate(@class,'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'pin')]" +
					"/ancestor::*[self::button or self::a][1]" +
					")[1]";
	private static final String RECOMMENDED_KNOWLEDGE_VIEWS_TEXT_FROM_TITLE =
			"(%s/ancestor::*[self::div or self::li][1]//*[contains(translate(normalize-space(.),'" + ALPHABET_UPPER + "','" + ALPHABET_LOWER + "'),'view')])[1]";
	private static final String RECOMMENDED_KNOWLEDGE_LIKES_TEXT_FROM_TITLE =
			"(%s/ancestor::*[self::div or self::li][1]//*[contains(normalize-space(.),'%')])[1]";
	
	
			public static String HKM_USERNAME;
			public static String HKM_PASSWORD;
			public static String SMARTIT_URL;


	public void clickSearchForKnowledgeResourcesButton() {
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.clickWithoutInterception(searchKnowledgeBtn);
	}

	public void clickCreateNewArticleButton() {
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.clickWithoutInterception(createNewArticleBtn);
	}

	public void loginToHKM(String uname, String pwd) {
		if (commonWaits.isElementPresent(userName)) {
			commonWaits.waitForElementClickable(userName).sendKeys(uname);
			commonWaits.waitForElementClickable(passWord).sendKeys(pwd);
			commonWaits.waitForElementClickable(loginButton).click();
			commonWaits.waitForSometime(10000);
		}

	}

	String smartITWindow;
	public void switchToHKM() {
		log.info("switching to HKM tab");
		commonWaits.waitForSometime(commonWaits.moreDelay); //This wait is required because opening new window sometime takes time to open
		Set<String> allWindows = getDriver().getWindowHandles();
		String hkmPortal = allWindows.stream().filter(window -> !window.equals(smartITWindow)).findFirst()
				.orElseThrow(() -> new RuntimeException("Looks like HKM/PV setting is not enable in ITSM"));
		getDriver().switchTo().window(hkmPortal);
		commonWaits.switchToLastWindow();
		commonWaits.waitForPageFullyLoaded(30,3);
		if (commonWaits.isElementPresent(userName)) {
			String HKM_USERNAME = CommonMethods.readConfig("HKM_USERNAME");
	        String HKM_PASSWORD = CommonMethods.readConfig("HKM_PASSWORD");
			log.info("logging in to HKM: " + HKM_USERNAME + "/" + HKM_PASSWORD);
			loginToHKM(HKM_USERNAME, HKM_PASSWORD);
		}

	}

	public boolean createHKMArticle(String hkmArticle) {
		switchToHKM();
		commonMethods.switchToWindowByTitle("Portal 1");
		commonWaits.waitForElementClickable(articleTitle);
		articleTitle.sendKeys(hkmArticle);
		commonWaits.waitForSometime(commonWaits.moreDelay);
		commonWaits.javaScriptScrollIntoView(filterTag);
		commonWaits.clickWithoutInterception(filterTag);
		commonWaits.waitForSometime(commonWaits.moreDelay);
		commonWaits.clickWithoutInterception(filterTag);
		inputTag.sendKeys(COMPANY_TAG+ Keys.ENTER);
		log.info("Company tag is selected");
		commonWaits.clickWithoutInterception(articleStatusdropDown);
		log.info("Article status dropdown is clicked");
		commonWaits.waitForSometime(commonWaits.moreDelay);
		if (!commonWaits.isElementPresent(publishStatus)){
			approvedStatus.click();
			log.info("Approved status is selected");
		}
		else{
			publishStatus.click();
			log.info("Published status is selected");
		}
	
		commonWaits.waitForElementClickable(saveButton).click();
		commonWaits.waitForElementClickable(newContent).click();
		commonWaits.waitForElementClickable(savehereButton).click();
		commonWaits.waitForElementClickable(saveButton);
		commonWaits.clickWithoutInterception(saveButton);
		log.info("Article is saved");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		return true;
	}

	public void selectCustomerOrAssetInSmartRecorder(String customerOrAssetName) {
		log.info("Selecting Customer/asset in smart Recorder");
		commonWaits.waitForPageFullyLoaded(30,3);
		enterStringWithAtSignInSmartRecorder(customerOrAssetName, false);
		commonWaits.waitForPageFullyLoaded(30,3);
		log.info("Selected Customer/asset: " + customerOrAssetName);
	}
	
	public void selectCustomerOrAssetInSmartRecorder(String searchText, String expectedDisplayName) {
		log.info("Selecting Customer/asset in smart Recorder. searchText=" + searchText + " expectedDisplayName=" + expectedDisplayName);
		commonWaits.waitForPageFullyLoaded(30,3);
		enterStringWithAtSignInSmartRecorder(searchText, false, expectedDisplayName);
		commonWaits.waitForPageFullyLoaded(30,3);
		log.info("Selected Customer/asset: " + searchText);
	}

	public void enterStringWithAtSignInSmartRecorder(String inputString, boolean clear) {
		enterStringWithAtSignInSmartRecorder(inputString, clear, null);
	}

	private void enterStringWithAtSignInSmartRecorder(String inputString, boolean clear, String expectedDisplayName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		isElementPresent(By.xpath(String.format(ElementXpathByID, "smart-recorder-textarea")));
		commonWaits.waitForPageFullyLoaded(30,3);
		keyClearAndType(String.format(ElementXpathByID, "smart-recorder-textarea"), "@" + inputString);
		isElementPresent(By.xpath(String.format(typeheadListElementID, inputString)));
		click(String.format(typeheadListElementID, inputString));
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void enterStringInSmartRecorder(String inputString, boolean clear) {
		isElementPresent(By.xpath(String.format(ElementXpathByID, "smart-recorder-textarea")));
		keyClickAndType(String.format(ElementXpathByID, "smart-recorder-textarea"), inputString);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	}

	public void selectRecommendedDWPTemplate(String templateName) {
		isElementPresent(By.xpath(String.format(recommendedDWPTempSelection, templateName)));
		click(String.format(recommendedDWPTempSelection, templateName));
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void clickCreateServiceRequestButton() {
		log.info("Click Create Service Request button");
		isElementPresent(By.xpath(String.format(ElementXpathByID, "create-srm-btn")));
		click(String.format(ElementXpathByID, "create-srm-btn"));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void clickSubmitRequestButton() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		log.info("Click Submit Service Request button");
		commonWaits.switchtoDefaultFrame();
		switchToFrame(By.xpath(String.format(ElementXpathByID, "dwp-window")));
		commonWaits.isElementPresent(SubmitButton);
		commonWaits.scrollToElement(SubmitButton);
		commonWaits.waitForElementClickable(SubmitButton, 10);
		SubmitButton.click();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForAllSpinnersToHide("DOTS_LOADER");
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void verifyCustomerDataOnSmartRecorderPage(String fullName, String company, String email, String telNo, String siteName) {
		commonWaits.isElementPresent(By.xpath(String.format(ElementXpathByID, "smart-recorder-textarea")));
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		String actualCustomerName = getValueFromText(String.format(ElementXpathByID,"full-name"));
		log.info("actual customer name: "+ actualCustomerName);
        Assert.assertEquals(actualCustomerName, fullName, "Customer is not saved with given first name & last name");

		String actualCompanyName = getValueFromText(String.format(ElementXpathByID, SolQAConstants.COMPANY));
		log.info("actual company name: "+ actualCompanyName);
        Assert.assertEquals(actualCompanyName, company, "Customer is not saved with given company name");

		String actualSite = getValueFromText(String.format(ElementXpathByID, SolQAConstants.SITE_NAME));
		log.info("actual customer name: "+ actualSite);
        Assert.assertEquals(actualSite, siteName, "Customer is not saved with given site");

		String actualEmail = getValueFromText(String.format(ElementXpathByID,"email"));
		log.info("actual customer name: "+ actualEmail);
        Assert.assertEquals(actualEmail, email, "Customer is not saved with given email");

		String actualDirectValue = getValueFromText(String.format(ElementXpathByID,"phone"));
		log.info("actual customer name: "+ actualDirectValue);
        Assert.assertEquals(actualDirectValue, telNo, "Customer is not saved with given direct field value");
	}

	public void sendQueryOnSmartRecorder(String que) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForElementClickable(String.format(ElementXpathByID, "smart-recorder-textarea"), 10);
		keyClickAndType(String.format(ElementXpathByID, "smart-recorder-textarea"), que);
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void verifyRecommendedDWPCTemplate(String templateDesc, String templateType) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		if(commonWaits.isElementPresent(DWP_CATALOG_SHOW_MORE_LINK_BY)){
			commonWaits.clickWithoutInterception(DWP_CATALOG_SHOW_MORE_LINK_BY);
		}
		if(!commonWaits.isElementPresent(NO_TEMPLATE_CATALOGUE_BY))
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(recommendedDWPTempSelection, templateDesc,templateType),30), "Recommended DWPC Template is not available");
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void verifyRecommendedKATemplate(String templateDesc, String templateType) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.isElementPresent(By.xpath(String.format(recommendedKATitle, "Media Service Restart", templateDesc, templateType)));
		commonWaits.scrollToSpecificElement(recommendedKATitle,"Media Service Restart", templateDesc, templateType);
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(recommendedKATitle, "Media Service Restart", templateDesc,templateType),30), "Recommended KA is not available");
		commonWaits.waitForPageFullyLoaded(30,3);
	}

	public void verifyRecommendedTemplate(String templateDesc, String templateType) {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.scrollToSpecificElement(recommendedTempSelectionValue,"Media Service", templateDesc, templateType);
		commonWaits.waitForPageFullyLoaded(30,3);
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(recommendedTempSelectionValue, "Media Service", templateDesc,templateType),30), "Recommended KA is not available");
		commonWaits.waitForPageFullyLoaded(30,3);
	}
	
	public void selectRecommendedKnowledge(String knowledgeName) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		isElementPresent(By.xpath(String.format(recommendedKnowledgeSelection, knowledgeName)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(recommendedKnowledgeSelection, knowledgeName)));
		commonWaits.waitForPageFullyLoaded(30,3);
	}
	
	public void verifyKnowledgeArticlePreview(String articleName) {

		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(IFRAME_HKM_BY);
		log.info("Verify Knowledge Article Preview");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(headerTitleKnowledgeArticle, articleName)));
		String headerTitle = getValueFromText(String.format(headerTitleKnowledgeArticle, articleName));
		Assert.assertTrue(headerTitle.contains(articleName), "Knowledge Article Preview is not displayed as expected");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
	}
	
	public void clickOnViewFullArticle(String articleLink) {
		
		commonWaits.switchtoDefaultFrame();	
		
		int existingWindowCount = getDriver().getWindowHandles().size();
        log.info("Existing window count: " + existingWindowCount);
        
	    log.info("Click on View Full Article");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, VIEW_FULL_ARTICLE_UX_ID)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, VIEW_FULL_ARTICLE_UX_ID)));
               
        int expectedWindowCount = existingWindowCount + 1;
        log.info("Waiting for window count to become: " + expectedWindowCount);
        commonWaits.waitForNumberWindowsToBe(expectedWindowCount);
        
        commonWaits.switchToLastWindow();
        
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForURLContains(articleLink);
        
        commonWaits.switchToTabByIndex(0);
             
	}
	
	public void clickOnSaveToTicket() {
		commonWaits.switchtoDefaultFrame();
		log.info("Click on Save To Ticket");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, SAVE_TO_TICKET_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, SAVE_TO_TICKET_UX_ID)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
	}
	
	public void clickOnDeleteFromTicket() {
		commonWaits.switchtoDefaultFrame();
		log.info("Click on Delete From Ticket");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, DELETE_FROM_TICKET_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, DELETE_FROM_TICKET_UX_ID)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
	}
	
	public void clickOnSaveAndResolve() {
		commonWaits.switchtoDefaultFrame();
		log.info("Click on Save And Resolve Button From Ticket");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, SAVE_AND_RESOLVE_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, SAVE_AND_RESOLVE_UX_ID)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForURLContains(SolQAConstants.IncidentAdditionalFields);
		
	}

	public void CreateTicketIncident() {
		commonWaits.switchtoDefaultFrame();
		log.info("Click on Create Ticket");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, CREATE_TICKET_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, CREATE_TICKET_UX_ID)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, CREATE_INCIDENT_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, CREATE_INCIDENT_UX_ID)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForURLContains(SolQAConstants.IncidentAdditionalFields);
	}
	
	public void CreateTicketWorkorder() {
		commonWaits.switchtoDefaultFrame();
		log.info("Click on Create Ticket");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, CREATE_TICKET_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, CREATE_TICKET_UX_ID)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, CREATE_WORKORDER_UX_ID)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, CREATE_WORKORDER_UX_ID)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForURLContains(SolQAConstants.Draft_WORKORDER_REQUEST_TYPE);
	}

	public void pinKnowledgeArticleFromRecommendedKnowledge(String articleTitle) {
		Assert.assertTrue(articleTitle != null && !articleTitle.trim().isEmpty(), "Article title is empty");
		String title = articleTitle.trim();
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		String titleXpath = String.format(recommendedKnowledgeSelection, title);
		Assert.assertTrue(commonWaits.waitForElementPresent(By.xpath(titleXpath)), "Recommended knowledge not found");

		String pinXpath = String.format(PIN_TOGGLE_FROM_TITLE, titleXpath);
		if (commonWaits.waitForElementPresent(By.xpath(pinXpath))) {
			commonWaits.clickWithoutInterception(By.xpath(pinXpath));
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
			commonWaits.waitForPageFullyLoaded(30, 3);
		} else {
			log.warn("Pin/selection control not found for Recommended Knowledge row: " + title);
		}
	}

	public String getViewCountTextFromRecommendedKnowledge(String articleTitle) {
		Assert.assertTrue(articleTitle != null && !articleTitle.trim().isEmpty(), "Article title is empty");
		String title = articleTitle.trim();
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		String titleXpath = String.format(recommendedKnowledgeSelection, title);
		Assert.assertTrue(commonWaits.waitForElementPresent(By.xpath(titleXpath)), "Recommended knowledge not found");

		String viewsXpath = String.format(RECOMMENDED_KNOWLEDGE_VIEWS_TEXT_FROM_TITLE, titleXpath);
		if (commonWaits.waitForElementPresent(By.xpath(viewsXpath))) {
			return findElement(By.xpath(viewsXpath)).getText();
		}
		return "";
	}

	public String getLikePercentTextFromRecommendedKnowledge(String articleTitle) {
		Assert.assertTrue(articleTitle != null && !articleTitle.trim().isEmpty(), "Article title is empty");
		String title = articleTitle.trim();
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

		String titleXpath = String.format(recommendedKnowledgeSelection, title);
		Assert.assertTrue(commonWaits.waitForElementPresent(By.xpath(titleXpath)), "Recommended knowledge not found");

		String likesXpath = String.format(RECOMMENDED_KNOWLEDGE_LIKES_TEXT_FROM_TITLE, titleXpath);
		if (commonWaits.waitForElementPresent(By.xpath(likesXpath))) {
			return findElement(By.xpath(likesXpath)).getText();
		}
		return "";
	}
	
	public void CreateTicketIncidentDraft() {
		commonWaits.switchtoDefaultFrame();
		log.info("Click on Create Ticket");
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, createTicket)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, createTicket)));
		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementXpathByUiId, createIncidentButton)));
		commonWaits.clickWithoutInterception(By.xpath(String.format(elementXpathByUiId, createIncidentButton)));
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		commonWaits.waitForURLContains("pwa-draft-incident");
	}

	public void searchAndSelectTemplateInSmartRecorder(String templateName, boolean clear) {
		log.info("Search and select template in Smart Recorder:- " + templateName);
		commonWaits.waitForElementVisible(smartRecorderTextArea);
		typeKeys(smartRecorderTextArea, "!" + templateName, clear, false);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(templateNameLocator,templateName), 10), "Template with name '" + templateName + "' not found in Smart Recorder");
		click(String.format(templateNameLocator,templateName));
		commonWaits.waitForPageFullyLoaded(defaultTimeout, 1);
		log.info("Selected template: " + templateName);
	}

    String dwpCatalogService = "//rs-dwp-catalog//*[@role='listitem']//*[@ux-id='template-name' and normalize-space()='%s']";
    @FindBy(xpath = "//button[normalize-space()='Create Service Request']")
    public WebElement createServiceRequestButton;
    public SmartRecorderPage searchAndSelectTemplateInSmartRecorderFromTemplateList(String templateName) {
        commonWaits.waitForElementVisible(smartRecorderTextArea).sendKeys(templateName+ Keys.ENTER);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(dwpCatalogService,templateName), 20), "Template with name '" + templateName + "' not found in Smart Recorder");
        commonWaits.click(By.xpath(String.format(dwpCatalogService,templateName)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createServiceRequestButton, 20), "Create Service Request button not visible after selecting template");
        return this;
    }

	public void Fill_ApprovalPersonSRD_smartRecorder(String userValue) {
		log.info("Filling dwpc Request Details");
        commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(dwpCatalogIframe);
		//select when do you need it by?
		String DateTime = "";
		Date dt1 = new Date();
		Calendar c1 = Calendar.getInstance();
		DateFormat dateFormat1 = new SimpleDateFormat("MMM d, yyyy h:mm a");
		c1.setTime(dt1);
		DateTime = dateFormat1.format(c1.getTime());
        commonWaits.waitForElementClickable(selectDate,5);
		typeKeys(selectDate, DateTime);

		//select who are you?
		click(DropDownSelect);
		click(userDropDown);
		typeKeys(userDropDown, userValue);
   		commonWaits.clickWithoutInterception(DropDownValueSelect);
		log.info("Filled dwpc Request Details");
	}

	public void clickOnRefreshButton() {
        log.info("Clicking on Refresh Button");
        commonWaits.switchtoDefaultFrame();
        clickWithoutInterception(refreshIcon);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(30, 3);
        log.info("Clicked on Refresh Button");
    }
  
	public void fillMailSeriveTemplateDetails(Map<Object, String> mailServiceData) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchToFrame(requestIframe);
		if (mailServiceData.get("MailServiceItem") != null) {
			clickWithoutInterception(selectItemButton);
			clickWithoutInterception(By.xpath(String.format(selectItemOption, mailServiceData.get("MailServiceItem"))));
		}
		if (mailServiceData.get("MailServiceDescription") != null) {
			switchToFrame(commentIframe);
			clickWithoutInterception(By.xpath(COMMENT_BODY_XPATH));
			keyClearAndType(COMMENT_BODY_XPATH, mailServiceData.get("MailServiceDescription"));
			commonWaits.switchtoDefaultFrame();
		}
		commonWaits.switchtoDefaultFrame();
	}

    public SmartRecorderPage switchToDWPWidgetFrame() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(dwpCatalogIframe);
        return this;
    }

    public SmartRecorderPage fillTextFieldByLabelInDWPWidget(String label, String value) {
        switchToDWPWidgetFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        catalogPage.fillTextFieldByLabel(label, value);
        return this;
    }

    public SmartRecorderPage fillDropdownFieldInDWPWidget(String label, String value) {
        switchToDWPWidgetFrame();
        dwpCommonOperations.fillDropdownField(label, value);
        return this;
    }

    public SmartRecorderPage clickNextInDWPWidget() {
        switchToDWPWidgetFrame();
        dwpCommonOperations.clickOnNextButton();
        return this;
    }

    public SmartRecorderPage fillCounterFieldInDWPWidget(String label, String value) {
        switchToDWPWidgetFrame();
        dwpCommonOperations.fillCounterField(label, value);
        return this;
    }

    public SmartRecorderPage fillAttachmentFieldInDWPWidget(String label, File file) {
        if (file == null) {
            throw new IllegalArgumentException("Attachment file cannot be null");
        }
        switchToDWPWidgetFrame();
        dwpCommonOperations.fillAttachmentField(label, file.getAbsolutePath());
        return this;
    }

    public SmartRecorderPage selectRadioOptionInDWPWidget(String questionLabel, String optionText) {
        switchToDWPWidgetFrame();
        dwpCommonOperations.selectRadioOption(questionLabel, optionText);
        return this;
    }

    public SmartRecorderPage selectCheckboxOptionInDWPWidget(String questionLabel, String optionText) {
        switchToDWPWidgetFrame();
        dwpCommonOperations.selectCheckboxOption(questionLabel, optionText);
        return this;
    }

    public SmartRecorderPage fillTypeAheadDropdownFieldInDWPWidget(String label, String value) {
        switchToDWPWidgetFrame();
        dwpCommonOperations.fillTypeAheadDropdownField(label, value);
        return this;
    }

    public boolean isSmartRecorderLoaded() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return commonWaits.isElementPresent(smartRecorderTextArea);
    }

	public void clickCreateIncidentOrWorkOrderFromCreateTicketBtn(String ticket) {
		log.info("Clicking on Create Ticket button");
		commonWaits.clickWithoutInterception(createTicketBtn);
		if(ticket.equals("Incident"))
			click(createIncFrmCreateTicket);
		else
			click(createWOFrmCreateTicket);
		commonWaits.waitForPageFullyLoaded(defaultTimeout, 1);
	}

	public void setCredentials(String url, String hkmUserName, String hkmPassword) {
		SMARTIT_URL = url;
		HKM_USERNAME = hkmUserName;
		HKM_PASSWORD = hkmPassword;

	}

}