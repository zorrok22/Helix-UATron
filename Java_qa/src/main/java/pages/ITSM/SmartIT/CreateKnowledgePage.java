package pages.ITSM.SmartIT;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CreateKnowledgePage extends BasePage{
	
	static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    
    //WebElements Locators
    @FindBy(xpath = " //*[contains(@class, 'header-content')]//span[contains(text(), 'Knowledge Management')]")
    WebElement KnowledgeHeading;
    
    @FindBy(xpath = "//*[@id='titleInput']")
    WebElement KnowledgeTitleInput;
    
    @FindBy(xpath = "//*[contains(@class,'end flex-nowrap')]//*[contains(text(),'Save')]")
    WebElement KnowledgeSaveButton;
    
    @FindBy(xpath = "//button[text()='Save here']")
    WebElement SaveHereButton;
    

  	public CreateKnowledgePage() {
  
    	log = new SolqaLogger(CreateKnowledgePage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

  	
	public void verifyCreateKnowledgePageLoaded(String articleLink) {
		commonWaits.waitForPageFullyLoaded(30,1);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForURLContains(articleLink);
		log.info("Create Knowledge Page loaded successfully.");
	}

	public void enterKnowledgeTitle(String knowledgeTitle) {
		log.info("Entering Knowledge Title: " + knowledgeTitle);
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForElementVisible(KnowledgeTitleInput);
		typeKeys(KnowledgeTitleInput, knowledgeTitle);
		log.info("Entered Knowledge Title: " + knowledgeTitle);
		commonWaits.waitForPageFullyLoaded(30,1);
	}
	
	public void saveCreatedKnowledge() {
		log.info("Saving the created Knowledge.");
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForElementVisible(KnowledgeSaveButton);
		commonWaits.clickWithoutInterception(KnowledgeSaveButton); 
		commonWaits.waitForPageFullyLoaded(30, 1);
		
		commonWaits.waitForElementVisible(SaveHereButton);
		commonWaits.clickWithoutInterception(SaveHereButton);
		
		log.info("Knowledge created and saved successfully.");
	}
}
