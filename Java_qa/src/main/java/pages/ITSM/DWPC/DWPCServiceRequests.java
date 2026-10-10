package pages.ITSM.DWPC;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DWPCServiceRequests extends BasePage {
	
	static SolqaLogger log;
	CommonWaits commonWaits;

	public DWPCServiceRequests() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(DWPCServiceRequests.class.getName());
		commonWaits=new CommonWaits();
	}
	
	public String serviceRequestRow = "//div[contains(@class,'ui-grid-cell-contents') and contains(.,'%s')]";
	
	@FindBy(xpath = "//sb-grid-search-box//input")
	public WebElement serviceRequestSearchBox;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Restart process')]")
    public WebElement restartProcessButton;
    @FindBy(xpath = "//*[contains(@class, 'modal-content')]//button[contains(normalize-space(.), 'Confirm')]")
    public WebElement confirmRestartButton;
    @FindBy(xpath = "//*[contains(@class, 'sb-alert-success') and contains(normalize-space(.), 'The process has been restarted')]")
    public WebElement restartSuccessMessage;
	
	public DWPCServiceRequests enterServiceRequestInSearchBox(String serviceRequestID) {
		log.info("Entering Service Request ID in search box: " + serviceRequestID);
		clickWithoutInterception(serviceRequestSearchBox);
		serviceRequestSearchBox.clear();
		serviceRequestSearchBox.sendKeys(serviceRequestID);
		return this;
	}
	
	public DWPCServiceRequests openDWPRequest(String serviceRequestID) {
		log.info("Open Service Request search result for ID: " + serviceRequestID);
		if(commonWaits.isElementPresent(serviceRequestSearchBox)) {
			enterServiceRequestInSearchBox(serviceRequestID);
		}
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
		commonWaits.doubleClick(getDriver(), findElement(By.xpath(String.format(serviceRequestRow, serviceRequestID))));
		return this;
	}

    public DWPCServiceRequests restartFailedServiceProcess(String serviceRequestID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        openDWPRequest(serviceRequestID);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), restartProcessButton, 20),
                "Restart Process button not visible for Service Request ID: " + serviceRequestID);
        commonWaits.clickWithoutInterception(restartProcessButton);
        commonWaits.clickWithoutInterception(confirmRestartButton);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), restartSuccessMessage, 20),
                "Restart success message not visible after restarting Service Request ID: " + serviceRequestID);
        return this;
    }

    public String selectCategory="//div[contains(@class,'sb-knowledge-article-association-blade')]//div[contains(@class,'sb-service')]/span[text()='%s']/following-sibling::*[text()='Select']";

    @FindBy(xpath = "//*[text()='Knowledge Article Association']/following-sibling::button[text()='Edit']")
    public WebElement knowledgeArticleAssociationEdit;
    @FindBy(css = "form[name='knowledgeAssociationForm'] .sb-blade-body label span")
    public List<WebElement> knowledgeTagCheckboxesList;
    @FindBy(css = "form[name='knowledgeAssociationForm'] .sb-blade-body label input")
    public List<WebElement> knowledgeTagCheckboxesInputList;
    @FindBy(css = "form[name='knowledgeAssociationForm'] button[type='submit']")
    public WebElement knowledgeTagConfirmButton;
    @FindBy(css = "form[name='knowledgeAssociationForm'] button[ng-click*=cancel]")
    public WebElement knowledgeTagCloseButton;
    @FindBy(css="[sb-configuration='knowledgeAssociationConfiguration'] button[ng-click*=cancel]")
    public WebElement KnowledgeArticleAssociationCloseButton;
    @FindBy(css="[sb-configuration='serviceOptionsConfiguration'] button[ng-click*=close]")
    public WebElement serviceOptionCloseButton;
    @FindBy(xpath = "//button[normalize-space()='Comments']")
    public WebElement commentsTab;
    @FindBy(xpath = "//span[contains(@class,'service-request-comment_message_rich_text')]//p")
    public WebElement commentField;
    @FindBy(xpath = "//a[contains(@href,'attachment')]")
    public List<WebElement> attachmentLinks;

    public DWPCServiceRequests editKnowledgeArticleAssociation(String knowledgeCategory,String value){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        commonWaits.waitForElementClickable(knowledgeArticleAssociationEdit).click();
        String tmpSelectCategory=String.format(selectCategory,knowledgeCategory);
        commonWaits.waitForByElementToBeVisible(By.xpath(tmpSelectCategory)).click();
        int knowledgeTagSize = this.knowledgeTagCheckboxesList.size();
        for(int i = 0; i < knowledgeTagSize; ++i) {
            WebElement knowledgeTag = this.knowledgeTagCheckboxesList.get(i);
            WebElement knowledgeTagInput = this.knowledgeTagCheckboxesInputList.get(i);
            if (!knowledgeTagInput.isSelected() && value.contains(knowledgeTag.getText())) {
                knowledgeTag.click();
            }
        }
        if(knowledgeTagConfirmButton.isEnabled()) {
            knowledgeTagConfirmButton.click();
        }
        else if(knowledgeTagCloseButton.isEnabled()){
            knowledgeTagCloseButton.click();
        }
        return this;
    }

    public DWPCServiceRequests closeKnowledgeArticleAssociation(){
        commonWaits.waitForElementClickable(KnowledgeArticleAssociationCloseButton).click();
        return this;
    }

    public DWPCServiceRequests closeServiceOption() {
        commonWaits.waitForElementClickable(serviceOptionCloseButton).click();
        return this;
    }

    public DWPCServiceRequests verifyCommentInRequest(String comment){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        commonWaits.clickWithoutInterception(commentsTab);
        commonWaits.waitForElementVisible(commentField);
        Assert.assertTrue(commentField.getText().contains(comment), "Comment not found in request: " + comment);
        return this;
    }

    public DWPCServiceRequests verifyAttachmentsInRequest(File[] attachments){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWPC);
        Set<String> expectedNames = Arrays.stream(attachments)
        .map(file -> file.getName().trim())
        .collect(Collectors.toSet());

Set<String> actualNames = new HashSet<>();

for (WebElement attachmentLink : attachmentLinks) {
    Assert.assertTrue(attachmentLink.isDisplayed(),
            "Attachment link not displayed");

    String title = attachmentLink.getAttribute("title");
    actualNames.add(title.trim());
}
Assert.assertTrue(actualNames.containsAll(expectedNames),
        "Expected attachments not matching.\nExpected: " + expectedNames + "\nActual: " + actualNames);
        return this;
    }
}

