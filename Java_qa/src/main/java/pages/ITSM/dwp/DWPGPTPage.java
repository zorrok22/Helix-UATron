package pages.ITSM.dwp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import framework.Launcher;
import util.CommonWaits;
import util.HelixGPTUtil;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPGPTPage extends BasePage {
    SolqaLogger log;
    CommonWaits commonWaits;
    DWPDashboard dwpDashboard ;
    HelixGPTUtil helixGPTutil;

     public DWPGPTPage() {
        log = new SolqaLogger(DWPGPTPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        dwpDashboard = new DWPDashboard();
        helixGPTutil = new HelixGPTUtil();
    }


    //String Locators
    private static final String HEADER_DETAILS_CSS = "span.busy-loader-app-title";

    @FindBy (xpath ="(//adapt-helix-gpt-chat-assistant-avatar/following-sibling::div)[last()]")
	List<WebElement> allMessages;

    @FindBy(css = "[data-testid*='conversationSpeechBubble']:last-of-type dwp-action[data-ta*='helixGptChatMessageFooterReferences']")
    List<WebElement> sourcesReferenceLinks;

    @FindBy(css = HEADER_DETAILS_CSS)
    WebElement headerDetailsBusyLoader;

    @FindBy (css ="button.adapt-chat-text-entry__action_send-message")
    WebElement SEND_MESSAGE_BUTTON;

    @FindBy (css ="section.adapt-helix-gpt-chat-thread__footer-section")
	List<WebElement> SOURCE_FOOTER;

    @FindBy (xpath ="//*[@data-ta='coloredMediaHeaderTitle']")
    WebElement ARTICLE_BODY;

    @FindBy (xpath ="//button[@data-ta='backButton']")
    WebElement BACK_BUTTON;

    @FindBy(xpath = "//button[contains(@data-testid,'helixGPTChatResetButton')]")
	WebElement clearhelixGPTSearchButton;

    @FindBy(xpath = "//div[contains(@class,'adapt-rx-search')]//input")
	WebElement gptSearchOncustomViewPage;

    @FindBy(xpath = "//div[.. and normalize-space()='Chat']")
	WebElement chatOption ;

    @FindBy(xpath = "//button[contains(@class,'helixgptchat-icon')]")
	WebElement helixGPTSearchIcon;
   
    @FindBy (css = "adapt-chat-text-entry[data-testid='helixGPTChatTextEntryInput'] textarea")
    WebElement SEARCH_TEXTAREA;

    @FindBy (xpath = "//button[contains(.,'Attach')]")
    WebElement ATTACHMENT_ICON_ON_HELIXGPT;

    @FindBy (xpath = "//adapt-helix-gpt-chat-text-entry//input[@type='file']")
    WebElement inpAttachFile;

    @FindBy(xpath="//div[contains(@class,'uploads')]//span[contains(@class,'text')]")
	WebElement errorMessageOnGPT;

    @FindBy(xpath = "//div[@class='input-group']//textarea")
	public WebElement TextArea;

    @FindBy(xpath = "//button[contains(@class,'send-message')]")
	public WebElement sendButtonIcon;

    @FindBy(xpath = "(//div[contains(@class,'helix-gpt')]//a)[1]")
	public WebElement firstRecord;

     @FindBy(xpath = "//textarea[contains(@placeholder, 'Ask a question')  or contains(@placeholder, 'Type a message')or contains(@placeholder, 'Ask a follow')]")
    WebElement textInputArea;

    @FindBy(xpath = ".//tr[position()>1]")
	public List<WebElement> rows;

    @FindBy(xpath = "//a[normalize-space()='All Activities'] ")
	public WebElement allActivities;

     @FindBy(xpath = "(//div[contains(@class,'helix-gpt-chat-thread__message-content')]//table//thead//th)[1]")
	public WebElement gptTableResponse;
    
    @FindBy(xpath = "//div[contains(@class,'helix-gpt-chat-thread__message-content')]//table//thead//th")
	public List<WebElement> tableResponse;

    @FindBy(xpath = "//div[contains(@class,'helix-gpt-chat-thread__message-content')]//h5")
	public List<WebElement> chatResponse;
    
    @FindBy(xpath = "//textarea[@placeholder='Ask a question']")
	public WebElement chatInput;
    @FindBy(xpath = "//div[contains(@class,'adapt-chat-text-entry__footer--e')]//button")
   	public WebElement sendButton;
    
    @FindBy(xpath = "(//span[contains(. ,'HelixGPT replied:')]/following-sibling::section//p)[last()]")
   	public WebElement TextOfResponse;
    @FindBy(xpath = " //div[contains(@class,'d-flex adapt')]")
   	public WebElement ResponseElement;

    @FindBy(xpath = "(//td/span[contains(normalize-space(),'CASE')])[1]")
	public WebElement firstRequestID;
 
   public String getAllGPTResponseString() {
   return helixGPTutil.getAllGPTResponseString(allMessages);
}



public void typeKeysWithEnterForSendButton(WebElement element, String value) {
    element.clear();
    element.sendKeys(value + Keys.ENTER);
}

  public void sendMessage(String message) {
        log.info("Sending message to HelixGPT Bot: " + message);
        commonWaits.switchToLastWindow();
        commonWaits.waitForElementVisible(textInputArea, 10);
        commonWaits.waitForElementClickable(textInputArea, 10);
        typeKeysWithEnterForSendButton(textInputArea, message);
        log.info("Message sent: " + message);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForExecutionComplete();
        log.info("Message processing complete.");
    }

   public String serviceRequestQuestion(String question, Map<Object, String> TestData) {
	    // Store asked question
	    Set<String> askedQuestions = new HashSet<>();
	    askedQuestions.add(question);

	    // Loop through test data and match Label using contains
	    for (Map.Entry<Object, String> entry : TestData.entrySet()) {
	        String label = entry.getKey().toString();   
	        String value = entry.getValue();            

	        if (question.toLowerCase().contains(label.toLowerCase())) {
	        	sendMessage(value);
	            return value;
	        }
	    }
        return "Question is different:- "+question;
}

     private WebElement getLatestSourceElement(){
        WebElement lastWebElement = SOURCE_FOOTER.stream()
                .max(Comparator.comparingInt(message ->
                        Integer.parseInt(message.getAttribute("data-testid").split("-")[1].split("__")[0])
                ))
                .orElse(null);
        return lastWebElement;
    }
     public List<String> getSourceLinksOnSearchedResult() {
        log.info("Getting article links which are visible on searched result.");
        getLatestSourceElement(); // preserve original intent (latest bubble context)
        List<WebElement> elements = sourcesReferenceLinks;
        List<String> articleDetails = new ArrayList<>();
        elements.stream().forEach(item -> articleDetails.add(item.getText()));
        return articleDetails;
    }

	public void allureStep(String description, boolean actual, Object matcher) {
    Assert.assertTrue(actual, description + " | Expected: " + matcher + " | Actual: " + actual);
}


     public void clickOnArticleLink(String articleName) {
        log.info(String.format("Clicking on Article %s link.", articleName));
        WebElement articleElement = sourcesReferenceLinks.stream()
                .filter(webElement -> webElement.getText().equalsIgnoreCase(articleName))
                .findFirst()
                .get();
        articleElement.click();
    }

     public void waitForArticleToLoad(){
       commonWaits.waitForElementInvisible(headerDetailsBusyLoader);
    }

    public String gettingOpenedArticleText() {
        log.info("Getting text of the opened article.");
        commonWaits.waitForVisibleElement(ARTICLE_BODY);
        return ARTICLE_BODY.getText();
    }

    public void clickOnBackButton() {
        log.info("Clicking on Back button.");
        commonWaits.waitForElementClickable(BACK_BUTTON);
        BACK_BUTTON.click();
        commonWaits.waitForExecutionComplete();
    }

    public void clearHelixGPTSearch() {
		commonWaits.waitForElementClickable(clearhelixGPTSearchButton);
		commonWaits.clickWithoutInterception(clearhelixGPTSearchButton);
	}

    public void verifyKeywardRelatedSearch_OnGPT(String keyward){
		log.info("Verifying keyward related search");
        switchtoDefaultFrame();
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForElementVisible(gptSearchOncustomViewPage);
		String searchString = gptSearchOncustomViewPage.getAttribute("value").trim();
		log.info("Search string is - "+ searchString);
		Assert.assertTrue(keyward.equalsIgnoreCase(searchString),"Keyward related search on GPT is not as expected");

	}

    public void navigateToChatOption() {
		commonWaits.clickWithoutInterception(chatOption);
		commonWaits.waitForExecutionComplete();
	}

     public void clickOnHelixGPTSearchIcon() {
       String currentUrl = getDriver().getCurrentUrl();
       if(!currentUrl.contains("/activity")){
           log.info("Current URL does not contain '/activity'. Navigating to activity page.");
          commonWaits.clickWithoutInterception(allActivities);
          commonWaits.switchToLastWindow();
          commonWaits.waitForAngularRequestsToFinish();    
  }
		commonWaits.waitForElementClickable(helixGPTSearchIcon);
		commonWaits.clickWithoutInterception(helixGPTSearchIcon);
	}

    public void isAttachmentIconPresentOnHelixGPT(){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHelixGPT);
        commonWaits.waitForElementVisible(ATTACHMENT_ICON_ON_HELIXGPT);
        Assert.assertTrue(commonWaits.isElementPresent(ATTACHMENT_ICON_ON_HELIXGPT), "Attachment icon on helixGPT is not present ");

	}

    public void attachFileInHelixGPT(String fileName) throws IOException {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHelixGPT);
        commonWaits.waitForElementVisible(ATTACHMENT_ICON_ON_HELIXGPT);

		String currentDirectory = new java.io.File(".").getCanonicalPath();
		String fileLocation = currentDirectory + File.separator + "resources" + File.separator + "DWP" + File.separator + fileName;
		log.info("File Location: " + fileLocation);

		File file1 = new File(fileLocation);
		commonWaits.waitForSometime(commonWaits.minDelay);
		inpAttachFile.sendKeys(file1.getAbsolutePath());
		commonWaits.waitForSometime(commonWaits.moreDelay);

	}

    public void verifyErrorMessageForNotSupportedFilesOnGPT(String errormsg){
		log.info("verifying error messages for not supported files");
		commonWaits.waitForElementVisible(errorMessageOnGPT, 100);
		String actualErrorMessage = errorMessageOnGPT.getText().trim();
		Assert.assertTrue(actualErrorMessage.contains(errormsg),"Error message for not supporting files is not correct");

	}

    public void sendMessageInGPTChat(String message) {
        commonWaits.waitForElementVisible(TextArea);
        TextArea.sendKeys(message);
		commonWaits.waitForElementClickable(sendButtonIcon);
		commonWaits.clickWithoutInterception(sendButtonIcon);
		commonWaits.waitForExecutionComplete();
	}

    public String getFirstRecordTextFromGPTResponse() {
        log.info("Getting text of the first record from GPT response.");
        commonWaits.waitForElementVisible(firstRecord);
        return firstRecord.getText();
    }

    public void openFirstRecordFromTable() {
		commonWaits.waitForElementClickable(firstRecord);
		commonWaits.clickWithoutInterception(firstRecord);
  }

   public String getGPTRequestIdBySrNo(int srNo) {
		    // Check for valid Sr.No
		    if (srNo < 1 || srNo > rows.size()) {
		        throw new IllegalArgumentException("Invalid Sr.No: " + srNo);
		    }
		    WebElement row = rows.get(srNo - 1); 
		    // Assuming Request ID is in the 2nd column (index 1)
		    WebElement requestIdCell = row.findElements(By.tagName("td")).get(0);

		    return requestIdCell.getText();
		}

  public String getTheLatestChatTableResponse() {
		return helixGPTutil.getAllGPTResponseString(tableResponse);
	}

    public List<String> getTheLatestChatResponse() {
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForSometime(commonWaits.moreDelay);
		commonWaits.waitForElementVisible(gptTableResponse, 60);
		List<String> responses = new ArrayList<>();
		for (WebElement response : chatResponse) {
			String text = (String) ((JavascriptExecutor) getDriver()).executeScript("return arguments[0].textContent;", response);
			responses.add(text.trim());
			log.info("---- text is - "+ text);
			responses.add(text.trim());
		}
		return responses;
	}

	public DWPGPTPage validateTheResponse(String input) {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHelixGPT);
		chatInput.sendKeys(input);
		
		log.info("The click button is displayed"+sendButton.isDisplayed());	
		commonWaits.clickWithoutInterception(sendButton);
		commonWaits.waitForElementVisible(TextOfResponse);
		String ResponseOfRequest = TextOfResponse.getText();
		log.info(ResponseOfRequest);
		Assert.assertTrue(ResponseElement.isDisplayed());
		return this;
	}

    public DWPGPTPage openGPTPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        String gptPageURL = getDWPURL() + SolQAConstants.GPT_PAGE_URL;
        getDriver().get(gptPageURL);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }
    
    public DWPGPTPage navigateToDWPURLInNewOpenedTab() {
		getDriver().get(Launcher.getDWPURL());
		return this;
	}

    public String getFirstRequestID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(firstRequestID,70);
        return firstRequestID.getText();
    }

}
