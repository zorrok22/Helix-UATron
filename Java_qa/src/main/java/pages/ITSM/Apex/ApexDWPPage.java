package pages.ITSM.Apex;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import pages.ITSM.SmartIT.WorkOrderPage;
import pages.ITSM.dwp.ActivityPage;
import pages.ITSM.dwp.CatalogPage;
import pages.ITSM.dwp.ServiceRequestPage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexDWPPage extends BasePage {

	static SolqaLogger log;
	CommonWaits commonWaits;
	ActivityPage activityPage;
	CatalogPage catalogPage;
	ServiceRequestPage serviceRequestPage;
	WorkOrderPage workOrderPage;

	public ApexDWPPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(ApexDWPPage.class.getName());
		commonWaits=new CommonWaits();
		activityPage = new ActivityPage();
		catalogPage = new CatalogPage();
		serviceRequestPage = new ServiceRequestPage();
	}

	@FindBy(xpath = "//span[contains(.,'Leave your comment here')]//parent::div//p")
	public WebElement commentInputField;

	//String Variable
	public String templateResaonTextareaLabel = "Please enter the reason for the request";

	public void addCommentWithAttachmentsInSubmittedRequestApex(String comment, File file) {
		log.info("Attaching a comment: [" + comment + "] with a file with name: " + file.getName());
		List<WebElement> initComments = findElements(By.cssSelector(activityPage.commentBlock));
		addAttachmentApex(comment, file, By.cssSelector(activityPage.ATTACHMENTS_IN_NEW_COMMENT));
		commonWaits.waitForElementClickable(activityPage.submitComments).click();
		commonWaits.waitTillNewCommentAdded(By.cssSelector(activityPage.commentBlock), initComments.size());
	}

	protected void addAttachmentApex(String comment, File file, By attachmentsInComments) {
		List<WebElement> initUploadedAttachments = findElements(attachmentsInComments);
		try {
			commonWaits.waitForElementClickable(activityPage.addCommentButton).click();
		} catch (TimeoutException e) {
			log.info("Comment section displayed");
		}
		if (comment != null) {
			//			commonWaits.switchToFrame(commentIframe);
			commonWaits.waitForElementClickable(commentInputField);
			commentInputField.sendKeys(comment);
			//			switchtoDefaultFrame();
		}
		activityPage.sendFileNameToInput(file, activityPage.addAttachmentInput);
		commonWaits.waitTillNewCommentAdded(attachmentsInComments, initUploadedAttachments.size());
		Optional<WebElement> attachedFile = findElements(attachmentsInComments).stream()
				.filter(we -> {
					String trim = we.getText().trim().replaceAll("([\\r\\n\\t])", "");
					return trim.equals(file.getName());
				})
				.findFirst();
		Assert.assertTrue(attachedFile.isPresent(), "File with the name: '" + file.getName() + "' is not attached.");
	}

	public void verifyAttachmentsApex(String comment, File file) {
		String attachmentName = file.getName();
		log.info("Verifying the comment: '" + comment + "' with the attachment is present: '" + attachmentName + "'. Path: " + file.getAbsolutePath());
		Optional<WebElement> commentsBlockWithAttachments = findElements(By.cssSelector(activityPage.commentBlock))
				.stream().filter(we -> {
					List<WebElement> linksInComment = we.findElements(By.tagName("a"));
					return linksInComment.stream().anyMatch(l -> l.getAttribute("href") != null && !l.getAttribute("href").isEmpty()
							&& l.getAttribute("download") != null && l.getAttribute("download").equals(attachmentName));
				})
				.findFirst();
		Assert.assertTrue(commentsBlockWithAttachments.isPresent(),"Comment block with the attachment: '" + attachmentName + "' is not present.");
		if (comment != null) {
			List<WebElement> commentText = commentsBlockWithAttachments.get().findElements(By.xpath(activityPage.attachmentBlock));
			Optional<WebElement> commentTextElement = commentText.stream().filter(c -> c.getText().trim().equals(comment)).findFirst();
			Assert.assertTrue(commentTextElement.isPresent(),"Comment text: '" + comment + "' is not present.");
		}
		Optional<WebElement> uiAttachmentName = commentsBlockWithAttachments.get()
				.findElements(By.cssSelector("a dwp-icon-media .icon-media__content-wrapper p span"))
				.stream().filter(we -> {
					String trim = we.getText().trim().replaceAll("([\\r\\n\\t])", "");
					return we.isDisplayed() && trim.equals(attachmentName);
				})
				.findFirst();
		Assert.assertTrue(uiAttachmentName.isPresent(), "Attachment with the name: '" + attachmentName + "' is not present.");
		Assert.assertTrue(isElementClickable(uiAttachmentName.get()),"Attachment with the name: '" + attachmentName + "' is not present.");
	}

	public void addVPNAccessRequestDetails(Map<Object, String> templateData) {
		catalogPage.fillTextFieldByLabel("Reason for Access", templateData.get("Reason for Access"));
	}

	public void addMSOfficeInstallationTemplateDetails(Map<Object, String> templateData) {	
		catalogPage.fillTextFieldByLabel(templateResaonTextareaLabel, templateData.get(SolQAConstants.REASON));
		catalogPage.setUrgency(templateData.get(SolQAConstants.URGENCY));
	}

	public void verifyNoFulfillmentCreatedForRejectedService() {	
		log.info("Verifying Fulfillment Details are not present in rejected Service Request");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
		Assert.assertFalse(commonWaits.isElementPresent(serviceRequestPage.fulfillmentDisplayId), "Fulfillment ID is present in Service Request");
	}

}
