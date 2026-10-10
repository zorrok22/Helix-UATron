package pages.ITSM.bwf;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import framework.BasePage;
import framework.Launcher;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class BWFLiveChat extends BasePage {

    static SolqaLogger log = new SolqaLogger(BWFLiveChat.class.getName());
    CommonWaits commonWaits=new CommonWaits();

    //String locators
    public String iFrameLiveChat = "//iframe[contains(@class,'ux-live-chat-iframe')]";
    public String spinnerLocator = "//bwf-loader//div[@class='loader-app']";
    public String myChatslink = "//*[@role='tablist']//*[contains(. ,'My Chats')]/parent::button";
    public String agentName = "//span[@ux-id='agentScreenName']";
    public String queueLink = "//span[contains(@class,'nav-link-title') and contains(.,'Queue')]";
    public String selectCustomerFromQueue = "(//*[contains(normalize-space(.),'%s')]/ancestor::*[contains(@class,'cb-header')][1]/following-sibling::*[1]//*[contains(normalize-space(.),'%s')])[last()]";
    public String selectCustomerFromQueueFallback_NoHeaderClass = "(//*[contains(normalize-space(.),'%s')]/ancestor::div[1]/following-sibling::div//*[contains(normalize-space(.),'%s')])[last()]";
    public String selectCustomerFromQueueByMessageOnly = "(//*[contains(normalize-space(.),'%s')])[last()]";
    public String acceptChatButton = "//button[@id='acceptChatBtn']";
    public String attachmetFileLocator = "//a[@download='%s']";
    public String messageInChatWindow = "//span[contains(text(), '%s')]";
    public String filesAddedTitle = "//span[contains(@class,'adapt-uploader-file-name')]";
    public String fileUploader = "//adapt-rx-uploader/input[@type='file']";
    public String attachPin = "//button[@id='upload-attachmentBtn']";
    public String uploadBtn = "//button[@id='upload-attachment-button']";
    public String privateNote = "//button[@id='notesBtn']";
    public String privateModalTitle = "//h5[@class='modal-title']";
    public String privateNoteArea = "//textarea[@id='notesTextarea']";
    public String saveButton = "//button[@id='saveNotesBtn']";
    public String noteAddedSymbol="//span[@ux-id='d-icon-note_pencil']";
    public String resourceTab = "//button[contains(@class,'nav-link')]//span[contains(@class,'nav-link')][contains(text(),'Resources')]";
    public String knowledgeArticlesList ="//*[contains(text(),'Recommended Knowledge')]/parent::div/following-sibling::div//bwf-search-result-fields/div[contains(@class,'bwf-search-fields ng-star-inserted')]";
    public String shareIconLocator = "//div[contains(@class, 'list__item__share-link d-icon-share')]";
    public String searchBox = "//bwf-global-search-bar//input[@placeholder='Type search text and press enter.']";
    public String articleLink = "//span[contains(@class, 'la-attachment-details')]/a[contains(@aria-label,'%s')]";
    public String sharedArticleLinks = "//span[contains(@class, 'la-attachment-details')]/a";
    public String kArticletitle = "//div[@data-ta='coloredMediaHeaderTitleWrapper']/h2";
    public String endChatBtn = "//button[@ux-id='endChatBtn']";
    public String confirmYesBtn = "//button[normalize-space()='Yes']";
    public String agentMessageBox = "//textarea[@id='agent-input-textarea' and contains(@class,'form-control')]";
    public String systemMessageInChatWindow = "//*[contains(@class,'defaultsysmsg') and contains(normalize-space(.),'%s')]";
    public String chatActionsDropDown = "//button[@id='chatActionsDropDownMenu' or @id='chatActionsDropDown' or @ux-id='chatActionsDropDownMenu']";
    public String chatActionsMenu = "//div[@aria-labelledby='chatActionsDropDownMenu' or @aria-labelledby='chatActionsDropDown']";
    public String chatActionItem = "//button[contains(normalize-space(.),'%s')]";
    public String okButton = "//div[contains(@class,'d-flex') and contains(@class,'ng-star-inserted')]//button[contains(@class,'btn-primary')]";
    public String ticketLocator = "//span[contains(text(), 'Create Case')]";
    public String ticketLocatorForCasePreview = "//span[contains(text(), 'Case Preview')]";
    public String modalHeader = "//span[@class='dp-title']";
    public String requester = "//span[text()='Requester']/../..//p/a";
    public String source = "//label[normalize-space()='Source']/..//div";
    public String caseIdLocator =  "//div[contains(@ux-id,'chatText')]/span[contains(text(), 'has been created')]";
    public String viewCase = ".//span[contains(text(),'Go to Case')]/..";
    public String viewDetail = "//bwf-activity-chat-log//button";
    public String modalTitle = "//span[contains(@class,'dp-title')]";
    public String attachmentList = "//span[@class='text-default bwf-attachment-container__file-name']";
    public String attachmnetFileName = "//span[contains(@class,'text-default bwf-attachment-container__file-name')]";
    public String fldSummaryUIValue ="//*[@rx-view-component-id='5ba63106-4f56-4729-a69d-36e0722e73c3']//span";
    public String fldFlowsetUIValue = "(//*[@rx-view-component-id='cd41e867-4676-450a-8536-bfd01460d742']//div)[2]";
    public String fldPriorityUIValue ="//*[@rx-view-component-id='4c010281-507c-4544-ab93-0cc033a0b65e']//div[2]";
    public String fldRequesterUIValue ="//*[@rx-view-component-id='efb0481e-7604-4c76-9202-4fb42a9ee0af']//p[contains(@class,'person-name ng-star-inserted')]/a";
    public String fldContactUIValue ="(//*[@rx-view-component-id='d60d5915-fb9a-4d1f-8294-746a735bfca6']//div//button)[1]";
    public String fldSupportGroupUIValue = "//*[@rx-view-component-id='95c038e1-d2fd-466b-b6d8-f0c5de4daa07']//div[@class='read-only-hierachy']//div/div";
    public String fldAssigneeUIValue ="//*[@rx-view-component-id='95c038e1-d2fd-466b-b6d8-f0c5de4daa07']//p";
    public String fldCatTier1UIValue ="(//*[@rx-view-component-id='eb869842-3d5c-40a1-aa31-08258faf2122']//div)[2]";
    public String fldCatTier1UIValueWithFlowset ="(//*[@rx-view-component-id='eb869842-3d5c-40a1-aa31-08258faf2122']//div)[2]";//"(//*[@rx-view-component-id='73fb70b0-2992-4dc5-b7ed-3d3d13cc4d6b']//div)[2]";
    public String fldCatTier2UIValue ="(//*[@rx-view-component-id='59383c00-f568-453e-9389-cbd83c8fea6e']//div)[2]";
    public String fldCatTier3UIValue ="(//*[@rx-view-component-id='107a0a82-a12f-4e9b-8bc8-2f8fbac013e4']//div)[2]";
    public String fldCatTier4UIValue ="(//*[@rx-view-component-id='fefba1ee-1862-48bc-b3e9-37f1e52528be']//div)[2]";
    public String FLD_CASE_TEMPLATE = "//*[@rx-view-component-id='76f79080-500d-4ae1-96ba-54dbd3e457a7']//div/div";
    public String fldLineOfBusiness = "(//*[@rx-view-component-id='9de4b215-e6c2-4150-b11f-4b4c834f9a76']//div)[2]";
    public String fldCaseSource ="//*[@rx-view-component-id='9aea7c54-3d2b-4548-88cf-01492e9dda45']//div/div";
    public String statusUIValue = "//*[@rx-view-component-id='df9a7672-5f84-495b-910f-ffbb77e2992f']//button";
    public String activityTab ="((//*[@role='tablist'])[2]//button)[1]";
    public String fldCompany = "//*[@rx-view-component-id='95c038e1-d2fd-466b-b6d8-f0c5de4daa07']//div/div[@class='read-only-hierachy']//div/div";
    public String targetDate = "(//*[@rx-view-component-id='2f6ca840-59ea-49e8-b347-3ba390176405']//div)[2]";
    String XPATH_RESOURCE_ROW_CANDIDATE_1_TEMPLATE = "//*[contains(@class,'bwf-search-fields') and contains(normalize-space(.),'%s')]";
	String XPATH_RESOURCE_ROW_CANDIDATE_2_TEMPLATE = "//*[contains(@class,'search') or contains(@class,'result') or contains(@class,'list')]//*[contains(normalize-space(.),'%s')]";
	String REL_SHARE_ICON_IN_ROW = ".//div[contains(@class,'d-icon-share') or contains(@class,'share-link') or contains(@class,'share')]";

    public boolean verifyLiveChatFrameOpened() {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver()).withTimeout(Duration.ofSeconds(30*5))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(WebDriverException.class);
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(spinnerLocator)));
        }catch(Exception e){
            Assert.fail("-------Spinner did not stop after 200 seconds", e);
        }
        switchToFrame(iFrameLiveChat);
        wait = new FluentWait<WebDriver>(getDriver()).withTimeout(Duration.ofSeconds(30*5))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(WebDriverException.class);
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(spinnerLocator)));
        } catch (Exception var5) {
            Assert.fail("-------Spinner did not stop after 200 seconds", var5);
        }
        try{
            switchToFrame(iFrameLiveChat);
            commonWaits.waitForElementVisible_withFluentWait(getDriver(),myChatslink, 120);
            return getDriver().findElement(By.xpath(myChatslink)).isDisplayed();
        }catch (Exception e) {
            log.info("----Caught Exception in verifyLiveChatFrameOpened() : " + e.getMessage());
            return false;
        }
    }

    public void switchToFrame(String locator) {
        switchtoDefaultFrame();
        switchToFrame((By.xpath(locator)));
    }

    public String getAgentName(){
        switchtoDefaultFrame();
        this.switchToFrame(this.iFrameLiveChat);
        String nameOfAgent= commonWaits.waitForByElementToBeVisible((By.xpath(agentName))).getText();
        switchtoDefaultFrame();
        return nameOfAgent;
    }

    public void clickQueueLink() {
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForByElementToBeVisible(By.xpath(queueLink)).click();
        if ( commonWaits.waitForByElementToBeVisible(By.xpath(queueLink)).getAttribute("aria-selected") == "false" ) {
            Assert.fail("---Queue Link could not be selected." ); }
        else {
            log.info("---Queue Link selected");
        }

    }

    public void selectTheCustomerFromChatQueue(String message, String customerName) {
        Assert.assertNotNull(message, "message must not be null");
        Assert.assertNotNull(customerName, "customerName must not be null");
        String msg = message.trim();
        String cust = customerName.trim();
        Assert.assertFalse(msg.isEmpty(), "message must not be empty");
        Assert.assertFalse(cust.isEmpty(), "customerName must not be empty");

        log.info("----Selecting the customer from chat Queue. Customer=" + cust + ", message=" + msg);

        long timeoutMs = 90_000;
        long pollMs = 2_000;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                switchToFrame(iFrameLiveChat);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                commonWaits.waitForExecutionComplete();

                String primaryXpath = String.format(selectCustomerFromQueue, cust, msg);
                String fallbackXpath = String.format(selectCustomerFromQueueFallback_NoHeaderClass, cust, msg);
                String msgOnlyXpath = String.format(selectCustomerFromQueueByMessageOnly, msg);

                WebElement target = null;
                if (!findElements(By.xpath(primaryXpath)).isEmpty()) {
                    target = commonWaits.waitForByElementToBeVisible(By.xpath(primaryXpath));
                } else if (!findElements(By.xpath(fallbackXpath)).isEmpty()) {
                    target = commonWaits.waitForByElementToBeVisible(By.xpath(fallbackXpath));
                } else if (!findElements(By.xpath(msgOnlyXpath)).isEmpty()) {
                    // As a last resort, click the message element itself (row selection often triggers from any cell).
                    target = commonWaits.waitForByElementToBeVisible(By.xpath(msgOnlyXpath));
                }

                if (target != null) {
                    javaScriptScrollIntoView(target);
                    commonWaits.clickWithoutInterception(target);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    switchtoDefaultFrame();
                    return;
                }
            } catch (Exception ignored) {
                // keep polling
            } finally {
                switchtoDefaultFrame();
            }

            commonWaits.waitForSometime(pollMs);
            try { getDriver().navigate().refresh(); } catch (Exception ignored) {}
        }

        Assert.fail("---- Customer not present/visible in Queue. Customer name - " + cust
                + " and message - " + msg + ".");
    }

    public void clickOnAcceptChat() {
        switchtoDefaultFrame();
        switchToFrame(iFrameLiveChat);
        log.info("click to Accept Chat");
        commonWaits.waitForByElementToBeVisible(By.xpath(acceptChatButton)).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForSometime(commonWaits.minDelay);
        switchtoDefaultFrame();
    }

    public void verifyAttachmentIsShared(String attachmentFileName) {
        switchtoDefaultFrame();
        this.switchToFrame(this.iFrameLiveChat);
        log.info("Verifying attachment is in chat window");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(attachmetFileLocator, attachmentFileName)));
        } catch (Exception var5) {
            Assert.fail("-----attachment is not available in chat window. attachment name = " + attachmentFileName);
        }
    }

    public void verifyMessageOnLiveChat(String expectedMessage) {
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(messageInChatWindow,expectedMessage)));
        }catch (Exception e) {
            Assert.fail("---Message not seen in Live Chat Window. Message=" + expectedMessage +". Exception Caught - " + e.getMessage());
        }
        switchtoDefaultFrame();
    }

    public void uploadAttachment(File file) {
        commonWaits.waitForAngularRequestsToFinish();
        switchToFrame(iFrameLiveChat);

        clickWithoutInterception(By.xpath(attachPin));
        try {
            switchToFrame(iFrameLiveChat);
            commonWaits.waitForSometime(commonWaits.minDelay);
            if (file != null) {

                WebElement elem = getDriver().findElement(By.xpath(fileUploader));
                String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";

                ((JavascriptExecutor) getDriver()).executeScript(js, elem);
                elem.sendKeys(file.getAbsolutePath());

            }
        } catch (Exception e) {
            Assert.fail("--caught exception " + e.getMessage());
        }

        try {
            commonWaits.waitForExecutionComplete();
        } catch (Exception e) {
            log.info(e.toString());
        }
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(filesAddedTitle));
        clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(uploadBtn)));
        try {
            commonWaits.waitForExecutionComplete();
        } catch (Exception e) {
            log.info(e.toString());
        }
        commonWaits.waitForSometime(commonWaits.moreDelay);
        if (file != null) {
            verifyAttachmentIsShared(file.getName());
        }
        switchtoDefaultFrame();
    }

    public void sendPrivateNoteInLiveChat(String message) {
        switchToFrame(iFrameLiveChat);
        if (message != null) {
            String privateNoteXpath = privateNote;
            String privateModalTitleXpath = privateModalTitle;
            String privateNoteAreaXpath = privateNoteArea;
            String saveButtonXpath = saveButton;

            WebElement privateNoteBtn = commonWaits.waitForByElementToBeVisible(By.xpath(privateNoteXpath));
            commonWaits.clickWithoutInterception(privateNoteBtn);
            commonWaits.waitForByElementToBeVisible(By.xpath(privateModalTitleXpath));

            WebElement noteArea = commonWaits.waitForByElementToBeVisible(By.xpath(privateNoteAreaXpath));
            noteArea.sendKeys(message);

            WebElement saveBtn = commonWaits.waitForByElementToBeVisible(By.xpath(saveButtonXpath));
            commonWaits.clickWithoutInterception(saveBtn);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        switchtoDefaultFrame();
    }

    public boolean verifyPrivateChatMessageNotThere() {
        log.info("Verify Private Chat message");
        commonWaits.waitForSometime(commonWaits.minDelay);
        boolean res = commonWaits.isElementPresent(By.xpath(noteAddedSymbol));
        switchtoDefaultFrame();
        return res;
    }

    public void selectResourceTabFromRightPanelAndShareTheKnowledge(String searchKnowledge){
        switchtoDefaultFrame();
        commonWaits.waitForByElementToBeVisible((By.xpath(resourceTab))).click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(searchBox)).click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(searchBox)).sendKeys(searchKnowledge);
        sendEnterKey();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        List<WebElement> articleLists = commonWaits.getElements(By.xpath(knowledgeArticlesList));
        commonWaits.waitForElementVisible(articleLists.get(0));
        try {
            commonWaits.waitForExecutionComplete();
        } catch (Exception e) {
            log.info(e.toString());
        }
        commonWaits.waitForSometime(commonWaits.minDelay);
        List<WebElement> sharedIconLists = commonWaits.getElements(By.xpath(shareIconLocator));
        sharedIconLists.get(0).click();
        try {
            commonWaits.waitForExecutionComplete();
        } catch (Exception e) {
            log.info(e.toString());
        }
        commonWaits.waitForSometime(commonWaits.minDelay);
    }

    /**
     * Shares a Resource template from the right panel "Resources" tab (not a Knowledge article).
     * Used by VC flows where sharing the template triggers a question on the transcript.
     *
     * Engineering assumption: the Resources tab uses the same global-search bar and share icon pattern as Knowledge,
     * but the result section header may differ (so do NOT rely on "Recommended Knowledge" heading).
     */
    public void selectResourceTabFromRightPanelAndSelectTemplate(String templateName) {
        Assert.assertNotNull(templateName, "templateName must not be null");
        String name = templateName.trim();
        Assert.assertFalse(name.isEmpty(), "templateName must not be empty");

        switchtoDefaultFrame();
        commonWaits.waitForByElementToBeVisible(By.xpath(resourceTab)).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        WebElement sb = commonWaits.waitForByElementToBeVisible(By.xpath(searchBox));
        sb.click();
        try { sb.clear(); } catch (Exception ignored) {}
        sb.sendKeys(name);
        sendEnterKey();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        String safe = name.replace("'", "");
        By[] rowCandidates = new By[] {
                // Most common: search result row contains the template text
                By.xpath(String.format(XPATH_RESOURCE_ROW_CANDIDATE_1_TEMPLATE, safe)),
                // Fallback: any result container containing the text
                By.xpath(String.format(XPATH_RESOURCE_ROW_CANDIDATE_2_TEMPLATE, safe))
        };

        long start = System.currentTimeMillis();
        WebElement row = null;
        while (System.currentTimeMillis() - start < 60_000 && row == null) {
            for (By by : rowCandidates) {
                try {
                    List<WebElement> els = findElements(by);
                    if (els != null && !els.isEmpty()) {
                        row = els.get(0);
                        break;
                    }
                } catch (Exception ignored) {}
            }
            if (row == null) {
                commonWaits.waitForSometime(1_000);
            }
        }

        // Click share icon scoped to row if possible; otherwise fall back to first share icon on page.
        try {
            if (row != null) {
                try { javaScriptScrollIntoView(row); } catch (Exception ignored) {}
                List<WebElement> shareInRow = row.findElements(By.xpath(REL_SHARE_ICON_IN_ROW));
                if (shareInRow != null && !shareInRow.isEmpty()) {
                    commonWaits.clickWithoutInterception(shareInRow.get(0));
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    return;
                }
            }
        } catch (Exception ignored) {}

        // Fallback: click first share icon visible in results area
        long start2 = System.currentTimeMillis();
        while (System.currentTimeMillis() - start2 < 60_000) {
            try {
                List<WebElement> shares = findElements(By.xpath(shareIconLocator));
                if (shares != null && !shares.isEmpty()) {
                    commonWaits.clickWithoutInterception(shares.get(0));
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    return;
                }
            } catch (Exception ignored) {}
            commonWaits.waitForSometime(1_000);
        }

        Assert.fail("Unable to share Resource template from Resources tab. templateName=" + name);
    }

    /**
     * Verifies that a knowledge article was shared in the live chat transcript and returns its name (aria-label or text).
     * Generic helper to support knowledge-sharing test flows.
     */
    public String verifyAndGetSharedKnowledgeArticle() {
        switchtoDefaultFrame();
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForAngularRequestsToFinish();

        List<WebElement> links = findElements(By.xpath(sharedArticleLinks));
        Assert.assertFalse(links.isEmpty(), "No shared knowledge article link found in BWF live chat transcript.");

        WebElement first = links.get(0);
        String name = "";
        try {
            name = first.getAttribute("aria-label");
        } catch (Exception ignored) {}
        if (name == null || name.trim().isEmpty()) {
            try { name = first.getText(); } catch (Exception ignored) {}
        }
        name = name == null ? "" : name.trim();
        Assert.assertFalse(name.isEmpty(), "Shared knowledge article link name is empty in BWF live chat transcript.");
        switchtoDefaultFrame();
        return name;
    }

    /**
     * Sends a message as the live agent in BWF live chat (inside live chat iframe).
     */
    public void sendMessageInLiveChat(String message) {
        Assert.assertNotNull(message, "Agent message must not be null");
        String msg = message.trim();
        Assert.assertFalse(msg.isEmpty(), "Agent message must not be empty");

        switchtoDefaultFrame();
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        WebElement input = commonWaits.waitForByElementToBeVisible(By.xpath(agentMessageBox));
        input.click();
        input.sendKeys(msg);
        input.sendKeys(Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        switchtoDefaultFrame();
    }

    public void verifySystemGeneratedMessageInLiveChatWindow(String expectedMessage) {
        Assert.assertNotNull(expectedMessage, "expectedMessage must not be null");
        String exp = expectedMessage.trim();
        Assert.assertFalse(exp.isEmpty(), "expectedMessage must not be empty");

        switchtoDefaultFrame();
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        String[] candidates = new String[] {
                String.format(systemMessageInChatWindow, exp),
                "//*[contains(normalize-space(.),'" + exp.replace("'", "") + "')]"
        };

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 60_000) {
            for (String xp : candidates) {
                try {
                    if (!findElements(By.xpath(xp)).isEmpty()) {
                        switchtoDefaultFrame();
                        return;
                    }
                } catch (Exception ignored) {}
            }
            commonWaits.waitForSometime(1_000);
        }

        Assert.fail("System message not found in BWF live chat window: " + exp);
    }

    public void selectChatAction(String action) {
        Assert.assertNotNull(action, "action must not be null");
        String act = action.trim();
        Assert.assertFalse(act.isEmpty(), "action must not be empty");

        switchtoDefaultFrame();
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(chatActionsDropDown)));
        commonWaits.waitForByElementToBeVisible(By.xpath(chatActionsMenu));
        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(chatActionItem, act))));
        switchtoDefaultFrame();
    }

    public void clickOkButton() {
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(okButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public void customerIsRemovedFromLiveAgentQueue(String message, String customerName) {
        verifyChatRemovedFromQueueAfterEnd(message, customerName);
    }

    /**
     * Ends the live chat session as the agent and confirms "Yes" if the confirmation dialog appears.
     */
    public void endChatAndConfirmYes() {
        switchtoDefaultFrame();
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(endChatBtn)));
        commonWaits.waitForSometime(commonWaits.minDelay);

        // Confirmation dialog varies across builds; handle if present.
        try {
            if (!findElements(By.xpath(confirmYesBtn)).isEmpty()) {
                commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(confirmYesBtn)));
            }
        } catch (Exception ignored) {}

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        switchtoDefaultFrame();
    }

    /**
     * Verifies the chat is removed from the Queue after ending chat.
     * Uses a short polling loop to allow UI refresh.
     */
    public void verifyChatRemovedFromQueueAfterEnd(String message, String customerName) {
        Assert.assertNotNull(message, "message must not be null");
        Assert.assertNotNull(customerName, "customerName must not be null");

        String msg = message.trim();
        String cust = customerName.trim();
        Assert.assertFalse(msg.isEmpty(), "message must not be empty");
        Assert.assertFalse(cust.isEmpty(), "customerName must not be empty");

        long timeoutMs = 60_000;
        long pollMs = 2_000;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                clickQueueLink();
                switchToFrame(iFrameLiveChat);
                String rowXpath = String.format(selectCustomerFromQueue, cust, msg);
                if (findElements(By.xpath(rowXpath)).isEmpty()) {
                    switchtoDefaultFrame();
                    return;
                }
            } catch (Exception ignored) {}
            commonWaits.waitForSometime(pollMs);
            try { getDriver().navigate().refresh(); } catch (Exception ignored) {}
        }

        Assert.fail("Chat was not removed from BWF live agent queue after end. Customer=" + cust + ", message=" + msg);
    }

    public void createCaseTicketForLiveChatRequest(){
        switchtoDefaultFrame();
        commonWaits.waitForElementVisible(commonWaits.waitForByElementToBeVisible(By.xpath(ticketLocator)));
        commonWaits.waitForByElementToBeVisible(By.xpath(ticketLocator)).click();
        //get case id
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public String getCaseCreatedId(){
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        WebElement caseId = commonWaits.waitForByElementToBeVisible(By.xpath(caseIdLocator));
        commonWaits.waitForElementVisible(caseId);
        return caseId.getText();
    }

    public void previewAndVerifyCaseTicketDetailsOnLiveChatPage(String expectedUser){
        switchtoDefaultFrame();
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), ticketLocatorForCasePreview,20);
        WebElement casePreview=commonWaits.waitForByElementToBeVisible(By.xpath(ticketLocatorForCasePreview));
        commonWaits.waitForElementClickable(casePreview).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), modalHeader, 20);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), requester, 20);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), source, 20);
        Assert.assertEquals(commonWaits.waitForByElementToBeVisible(By.xpath(requester)).getText(),expectedUser, "Requester is not matching with expected user.");
        Assert.assertEquals(commonWaits.waitForByElementToBeVisible(By.xpath(source)).getText(),"Live Chat", "Source is not set to Live Chat.");
    }

    public void viewCaseAndVerifyAttachmentFilesSharedAndDetailsOnActivityPage(List<String> attachments, String initialMessage){
        switchtoDefaultFrame();
        clickWithoutInterception(By.xpath(viewCase));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        List<String> tabs = new ArrayList<String>(getDriver().getWindowHandles());
        WebDriver driverTabs = getDriver().switchTo().window(tabs.get(1));
        Launcher.setDriver(driverTabs);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        verify_CreatedCase_Summary(initialMessage);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), viewDetail, commonWaits.minDelay);
        commonWaits.waitForByElementToBeVisible(By.xpath(viewDetail)).click();
        getDriver().switchTo().activeElement();
        String windowTitle = commonWaits.waitForByElementToBeVisible(By.xpath(modalTitle)).getText();
        Assert.assertEquals("Chat Transcript", windowTitle, "Chat Transcript view details page is not opened.");
        List<String> actualItems = new ArrayList<>();
        List<WebElement> attachmentItems = commonWaits.getElements(By.xpath(attachmnetFileName));
        for(int i=0;i<attachmentItems.size();i++){
            actualItems.add(attachmentItems.get(i).getText().trim());
        }
        for(int i=0;i<attachments.size();i++){
            Assert.assertTrue(actualItems.contains(attachments.get(i)+".txt" )|| actualItems.contains(attachments.get(i)), "Attachment"+i+" = "+ attachments.get(i) +" not found");
        }
        driverTabs = getDriver().switchTo().window(tabs.get(0));
        Launcher.setDriver(driverTabs);
        switchtoDefaultFrame();
        commonWaits.waitForSometime(commonWaits.minDelay);
    }

    public void verify_CreatedCase_Summary(String value) {
        commonWaits.waitForSometime(commonWaits.defaultTimeout);
        try {
            Assert.assertEquals(getUITextsOfEditCase("SUMMARY"), value, "HR Case Summary does not match");
        } catch (AssertionError e) {
            log.error("Assertion failed at Summary: " + e);
        }
    }

    public String getUITextsOfEditCase(String labelName) {
        log.info("Get UI Values of Fields after Creating case. " + labelName);
        String sLocator=null;
        switch (labelName) {

        case "SUMMARY":
            sLocator= fldSummaryUIValue;
            break;
        case "PRIORITY":
            sLocator= fldPriorityUIValue;
            break;
        case "REQUESTER":
            sLocator= fldRequesterUIValue;
            break;
        case "CONTACT":
            sLocator= fldContactUIValue;
            break;
        case "SUPPORTGROUP":
            sLocator= fldSupportGroupUIValue;
            break;
        case "ASSIGNEE":
            sLocator= fldAssigneeUIValue;
            break;
        case "TIER1":
            sLocator= fldCatTier1UIValue;
            break;
        case "TIER1WITHFLOWSET":
            sLocator= fldCatTier1UIValueWithFlowset;
            break;
        case "TIER2":
            sLocator= fldCatTier2UIValue;
            break;
        case "TIER3":
            sLocator= fldCatTier3UIValue;
            break;
        case "TIER4":
            sLocator= fldCatTier4UIValue;
            break;
        case "STATUSUI":
            sLocator= statusUIValue;
            break;
        case "ACTIVITY":
            sLocator= activityTab;
            break;
        case "CASETEMPLATE":
            sLocator=FLD_CASE_TEMPLATE;
            break;
        case "FLOWSET":
            sLocator=fldFlowsetUIValue;
            break;
        case "COMPANY":
            sLocator=fldCompany;
            break;
        case "SOURCE":
            sLocator=fldCaseSource;
            break;
        case "DATE":
            sLocator=targetDate;
            break;
        case "LINE_OF_BUSINESS":
            sLocator=fldLineOfBusiness;
            break;
        default:
            break;
        }
        try {
            commonWaits.waitForSometime(commonWaits.minDelay);
            commonWaits.waitForByElementToBeVisible(By.xpath(sLocator));
        } catch (Exception e){
            Assert.fail("--- Field '" + labelName +"' locator not found. " + e.getMessage());
        }
        String value = getDriver().findElement(By.xpath(sLocator)).getText().trim();
        log.info(" Value of TextField "+ labelName + " = " + value);
        return value;
    }

    public void openSharedKnowledgeArticleAndSwitchBack(String sharedArticle){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        switchToFrame(iFrameLiveChat);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(articleLink,sharedArticle))).click();
        List<String> tabs = new ArrayList<String>(getDriver().getWindowHandles());
        new WebDriverWait(getDriver(), Duration.ofSeconds(20)).until(ExpectedConditions.numberOfWindowsToBe(2));
        WebDriver driver1 = getDriver().switchTo().window(tabs.get(1));
        Launcher.setDriver(driver1);
        commonWaits=new CommonWaits();
        driver1.navigate().refresh();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementVisible( commonWaits.waitForByElementToBeVisible(By.xpath(kArticletitle)));
        Assert.assertTrue(sharedArticle.contains( commonWaits.waitForByElementToBeVisible(By.xpath(kArticletitle)).getText()), "Article Shared and Article opened is not same");
        getDriver().close();
        driver1=getDriver().switchTo().window(tabs.get(0));
        Launcher.setDriver(driver1);
    }

    private String safeGetUrl() {
        try { return String.valueOf(getDriver().getCurrentUrl()); } catch (Exception e) { return ""; }
    }

}