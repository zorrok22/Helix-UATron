package pages.ITSM.SSPLiveChat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import framework.BasePage;
import framework.Launcher;
import org.openqa.selenium.WebDriver;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import util.CommonMethods;

public class SspLiveChat extends BasePage {

    static SolqaLogger log = new SolqaLogger(SspLiveChat.class.getName());
    CommonWaits commonWaits = new CommonWaits();
    CommonMethods commonMethods = new CommonMethods();

    //String locators
    public String loadingSpinner = "//div[contains(@class,'loader-container')]";
    public String topicName = "//*[contains(@class, 'rx-select__option-content') and contains(. ,'%s')]";
    public String topicDropDownIcon = "//button[@id='topicSelect']";
    public String fldMessage = "//textarea[@id='inputQuery' and @placeholder='Start typing your question']";
    public String openItemTab = "//*[@role='tablist']//*[contains(. ,'Open Items')]/parent::button";
    public String messageInInboxForNormalMessage = "//p[contains(@class, 'chatMessage') and contains(text() ,'%s')]";
    public String errorIfLimitExceed = "//span[contains(@class ,'error-title') and contains(text(), 'Max sessions limit reached')]";
    public String closeButtonForErrorIfRequestExceed = "//errors-list//button[@aria-label='Close errors']";
    public String myOpenChatsWithCount = "//adapt-accordion//*[normalize-space()='%s']";
    public String cancelChatRequest = "//button[contains(@aria-label,'Cancel chat')]";
    public String chatTab = "//*[@role='tablist']//*[contains(. ,'Chat')]/parent::button";
    public String messageInInbox = "//span[contains(@class, 'defaultsysmsg') and contains(text() ,'%s')]";
    public String sendAttachment = "//button[@class='send attachment']";
    public String uploadFileLocator = "//input[@type='file']";
    public String sendBtn = "//button[@id='btnSend']";
    public String noteAddedSymbol = "//span[@ux-id='d-icon-note_pencil']";
    public String loadingSpinnerLocator = "//div[contains(@class,'loader-container')]";
    public String liveChat = "//button[contains(@class, 'a-action btn btn-link d-icon-comments_o ng-star-inserted')]";
    public String quickLinksTab = "//*[@role='tablist']//*[contains(normalize-space(),'" + SolQAConstants.QUICK_LINKS + "')]/parent::button";
    public String quickLinkAnchorByText = "//a[contains(normalize-space(.),'%s')]";
    // -------------------- Locators (declare before methods) --------------------
    private static final String TOPIC_NAME_XPATH = "//*[contains(@class, 'rx-select__option-content') and contains(. ,'%s')]";
    private static final String TOPIC_DROPDOWN_ICON_XPATH = "//button[@id='topicSelect']";
    private static final String FLD_MESSAGE_XPATH = "//textarea[@id='inputQuery' and @placeholder='Start typing your question']";
    private static final String OPEN_ITEM_TAB_XPATH = "//*[@role='tablist']//*[contains(. ,'Open Items')]/parent::button";
    private static final String MESSAGE_IN_INBOX_NORMAL_XPATH = "//p[contains(@class, 'chatMessage') and contains(text() ,'%s')]";
    private static final String ERROR_MAX_SESSIONS_XPATH = "//span[contains(@class ,'error-title') and contains(text(), 'Max sessions limit reached')]";
    private static final String CLOSE_ERRORS_BTN_XPATH = "//errors-list//button[@aria-label='Close errors']";
    private static final String MY_OPEN_CHATS_WITH_COUNT_XPATH = "//adapt-accordion//*[normalize-space()='%s']";
    private static final String MY_OPEN_CHATS_WITH_COUNT_CONTAINS_XPATH = "//adapt-accordion//*[contains(normalize-space(.),'%s')]";
    private static final String MY_OPEN_CHATS_SECTION_CASE_INSENSITIVE_XPATH =
            "//*[@role='tablist' or self::adapt-accordion or ancestor::adapt-accordion]" +
                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'%s')]";
    private static final String ANY_OPEN_CHATS_SECTION_XPATH = "//adapt-accordion//*[contains(normalize-space(.),'Open Chats')]";
    private static final String OPEN_ITEMS_ACCORDION_XPATH = "//adapt-accordion";
    private static final String CANCEL_CHAT_REQUEST_XPATH = "//button[contains(@aria-label,'Cancel chat')]";
    private static final String CHAT_TAB_XPATH = "//*[@role='tablist']//*[contains(. ,'Chat')]/parent::button";
    private static final String SYSTEM_MESSAGE_IN_INBOX_XPATH = "//span[contains(@class, 'defaultsysmsg') and contains(text() ,'%s')]";
    private static final String OPEN_ITEM_ENTRY_BY_TEXT_XPATH =
            "//*[contains(@class,'open') or contains(@class,'item') or contains(@class,'chat')]" +
                    "//*[contains(normalize-space(.),'%s')]";
    // Umbrella reference locator (stable in SSP builds)
    private static final String OPEN_ITEM_REQUEST_EXACT_XPATH = "//*[contains(@class,'openItemText')][normalize-space()='%s']";
    private static final String OPEN_ITEM_REQUEST_CONTAINS_XPATH = "//*[contains(@class,'openItemText')][contains(normalize-space(.),'%s')]";
    private static final String OPEN_ITEM_REQUEST_ANY_XPATH = "//*[contains(@class,'openItemText')]";
    private static final String BTN_SEND_ATTACHMENT_XPATH = "//button[@class='send attachment']";
    private static final String INPUT_UPLOAD_FILE_XPATH = "//input[@type='file']";
    private static final String BTN_SEND_XPATH = "//button[@id='btnSend']";
    private static final String ATTACHMENT_DOWNLOAD_LINK_XPATH = "//a[@download='%s']";
    private static final String NOTE_ADDED_SYMBOL_XPATH = "//span[@ux-id='d-icon-note_pencil']";
    private static final String BTN_LIVE_CHAT_XPATH = "//button[contains(@class, 'a-action btn btn-link d-icon-comments_o ng-star-inserted')]";
    private static final String CHAT_NOW_XPATH = "//div[contains(@class,'rla') and normalize-space()='Chat now']";
    private static final String SHARED_KNOWLEDGE_ARTICLE_LINK_XPATH =
            "//*[self::a or self::button][contains(normalize-space(.),'%s')]";
    private static final String BTN_END_CHAT_XPATH = "//button[contains(@aria-label,'End chat') or contains(@aria-label,'End Chat') or normalize-space()='End chat' or normalize-space()='End Chat']";
    private static final String BTN_CONFIRM_YES_XPATH = "//button[normalize-space()='Yes' or normalize-space()='YES']";

    // -------------------- Constants (no magic numbers) --------------------
    private static final long ATTACHMENT_VERIFY_TIMEOUT_MS = 60_000;
    private static final long ATTACHMENT_VERIFY_POLL_INTERVAL_MS = 1_000;

    public void setTopicFromChatTopics(String topic) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(TOPIC_DROPDOWN_ICON_XPATH));
        commonWaits.clickWithoutInterception(we);
        we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TOPIC_NAME_XPATH, topic)));
        clickByAction(we);
    }

    public void openLiveChatPage() {
        WebElement liveChatBtn = commonWaits.waitForByElementToBeVisible(By.xpath(BTN_LIVE_CHAT_XPATH));
        clickByAction(liveChatBtn);
        commonWaits.waitForNumberWindowsToBe(2);
        ArrayList<String> secondWindow = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(secondWindow.get(1));
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), TOPIC_DROPDOWN_ICON_XPATH, 60);
        commonWaits.waitForElementPresent(FLD_MESSAGE_XPATH, 30);
    }

    public boolean isChatNowButtonPresent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        return commonWaits.waitForElementPresent(By.xpath(CHAT_NOW_XPATH));
    }

    public void clickChatNowOption() {
        commonWaits.waitForSometime(commonWaits.minDelay);
        if (isChatNowButtonPresent()) {
            WebElement chatNow = commonWaits.waitForByElementToBeVisible(By.xpath(CHAT_NOW_XPATH));
            clickByAction(chatNow);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
            Assert.assertFalse(isChatNowButtonPresent(), "'Chat now' button was not clicked.");
        }
    }

    public void openQuickLinksTab() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        commonWaits.waitForElementPresenceLocated(By.xpath(quickLinksTab)).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
    }

    public SspLiveChat openQuickLinkAndVerifyUrlContainsAndClose(String linkText, String expectedUrlContains) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);

        String originalHandle = getDriver().getWindowHandle();
        java.util.Set<String> beforeHandles = getDriver().getWindowHandles();

        WebElement link = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(quickLinkAnchorByText, linkText)));
        clickWithoutInterception(link);

        commonWaits.waitForNumberOfWindows(getDriver(), beforeHandles.size() + 1, 20);
        java.util.Set<String> afterHandles = getDriver().getWindowHandles();
        String newHandle = null;
        for (String h : afterHandles) {
            if (!beforeHandles.contains(h)) {
                newHandle = h;
                break;
            }
        }
        Assert.assertNotNull(newHandle, "Quick Link did not open a new window/tab as expected.");

        WebDriver newTab = getDriver().switchTo().window(newHandle);
        Launcher.setDriver(newTab);
        commonWaits.waitForSometime(commonWaits.minDelay);

        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.toLowerCase().contains(expectedUrlContains.toLowerCase()),
                "Quick Link cross-launch URL mismatch. Expected url to contain: " + expectedUrlContains + " but was: " + currentUrl);

        WebDriver back = getDriver().switchTo().window(originalHandle);
        Launcher.setDriver(back);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        return this;
    }

    public void typeMessageInTextArea(String message) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        if (message != null) {
            commonWaits.waitForElementPresent(FLD_MESSAGE_XPATH);
            commonWaits.waitForElementPresenceLocated(By.xpath(FLD_MESSAGE_XPATH)).sendKeys(message);
        }
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        ifMaxSessionLimitReachedThenRemovePreviousChatAgent("My Open Chats", message);
        verifyMessageOnSSPConsole(message);
    }

    public void closeLiveChatWindowUsingBrowserClose() {
        // Closes SSP Live Chat popup window and switches back to the parent DWP window.
        ArrayList<String> handles = new ArrayList<>(getDriver().getWindowHandles());
        if (handles.size() < 2) {
            // Nothing to close; already on parent window
            return;
        }
        String current = getDriver().getWindowHandle();
        getDriver().close();
        String parent = null;
        for (String h : handles) {
            if (!h.equals(current) && parent == null) {
                parent = h;
            }
        }
        if (parent != null) {
            getDriver().switchTo().window(parent);
        }
    }

    public void ifMaxSessionLimitReachedThenRemovePreviousChatAgent(String OpenItemTabName, String initialMessage) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        boolean flag = commonWaits.waitForElementPresent(By.xpath(ERROR_MAX_SESSIONS_XPATH));
        if (flag) {
            clickOpenItemTab();
            commonWaits.waitForByElementToBeVisible(By.xpath(CLOSE_ERRORS_BTN_XPATH)).click();
            log.info("Open the Item from OpenItem tab");
            commonWaits.waitForElementPresenceLocated(By.xpath(String.format(MY_OPEN_CHATS_WITH_COUNT_XPATH, OpenItemTabName)));
            commonWaits.clickWithoutInterception(commonWaits.waitForElementPresenceLocated(By.xpath(String.format(MY_OPEN_CHATS_WITH_COUNT_XPATH, OpenItemTabName))));
            commonWaits.waitForSometime(commonWaits.minDelay);
            commonWaits.waitForElementPresenceLocated(By.xpath(CANCEL_CHAT_REQUEST_XPATH));
            List<WebElement> requests = findElements(By.xpath(CANCEL_CHAT_REQUEST_XPATH));
            for (WebElement request : requests) {
                clickByAction(request);
                commonWaits.waitForSometime(commonWaits.minDelay);
            }
            clickChatTab();
            typeMessageInTextArea(initialMessage);
        }
    }

    public void clickChatTab() {
        log.info("click chat tab");
        WebElement chatTab = commonWaits.waitForElementPresenceLocated(By.xpath(CHAT_TAB_XPATH));
        commonWaits.clickWithoutInterception(chatTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
    }

    public void verifyMessageOnSSPConsole(String expectedMessage) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        String messageInbox = String.format(MESSAGE_IN_INBOX_NORMAL_XPATH, expectedMessage);
        commonWaits.waitForElementPresent(messageInbox);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(messageInbox)), "Message not verified."
                + "Message expected = " + expectedMessage + "And Message actual  = " + getDriver().findElement(By.xpath(messageInbox)).getText());
    }

    public void clickOpenItemTab() {
        log.info("click open item tab");
        WebElement openItems = commonWaits.waitForElementPresenceLocated(By.xpath(OPEN_ITEM_TAB_XPATH));
        commonWaits.clickWithoutInterception(openItems);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
    }

    /**
     * Selects an existing chat request from SSP "Open Items" using the chat's initial message text.
     * Engineering assumption: the initial message text is visible within the Open Items list entry.
     */
    public SspLiveChat selectRequestFromOpenItemOnSSPConsole(String initialChatMessage, String openItemsSectionName) {
        Assert.assertNotNull(initialChatMessage, "initialChatMessage must not be null");
        Assert.assertNotNull(openItemsSectionName, "openItemsSectionName must not be null");
        String msg = initialChatMessage.trim();
        String section = openItemsSectionName.trim();
        Assert.assertFalse(msg.isEmpty(), "initialChatMessage must not be empty");
        Assert.assertFalse(section.isEmpty(), "openItemsSectionName must not be empty");

        // Prefer the same locator used in Umbrella (openItemText). Fallback to partial match if UI truncates.
        String msgShort = msg.length() > 20 ? msg.substring(0, 20) : msg;
        String msgSafe = msg.replace("'", "");
        String msgShortSafe = msgShort.replace("'", "");

        String[] candidates = new String[]{
                String.format(OPEN_ITEM_REQUEST_EXACT_XPATH, msg),
                String.format(OPEN_ITEM_REQUEST_CONTAINS_XPATH, msg),
                String.format(OPEN_ITEM_REQUEST_CONTAINS_XPATH, msgShort),
                String.format(OPEN_ITEM_ENTRY_BY_TEXT_XPATH, msg),
                "//*[contains(normalize-space(.),'" + msgSafe + "')]",
                "//*[contains(normalize-space(.),'" + msgShortSafe + "')]"
        };

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 90_000) {
            try {
                clickOpenItemTab();
                commonWaits.waitForElementPresent(By.xpath(OPEN_ITEMS_ACCORDION_XPATH));

                // Close any max-session errors if they appear (keeps UI interactable).
                try {
                    if (!findElements(By.xpath(CLOSE_ERRORS_BTN_XPATH)).isEmpty()) {
                        commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(CLOSE_ERRORS_BTN_XPATH)));
                    }
                } catch (Exception ignored) {
                }

                // Expand/Select the Open Items accordion section (e.g., "My Open Chats" or "My Open Chats (1)")
                WebElement openItemsSection = null;
                if (!findElements(By.xpath(String.format(MY_OPEN_CHATS_WITH_COUNT_XPATH, section))).isEmpty()) {
                    openItemsSection = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MY_OPEN_CHATS_WITH_COUNT_XPATH, section)));
                } else if (!findElements(By.xpath(String.format(MY_OPEN_CHATS_WITH_COUNT_CONTAINS_XPATH, section))).isEmpty()) {
                    openItemsSection = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MY_OPEN_CHATS_WITH_COUNT_CONTAINS_XPATH, section)));
                } else if (!findElements(By.xpath(String.format(MY_OPEN_CHATS_SECTION_CASE_INSENSITIVE_XPATH, section.toLowerCase()))).isEmpty()) {
                    openItemsSection = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MY_OPEN_CHATS_SECTION_CASE_INSENSITIVE_XPATH, section.toLowerCase())));
                } else if (!findElements(By.xpath(ANY_OPEN_CHATS_SECTION_XPATH)).isEmpty()) {
                    // fallback: pick any "Open Chats" section if name differs across builds
                    openItemsSection = commonWaits.waitForByElementToBeVisible(By.xpath(ANY_OPEN_CHATS_SECTION_XPATH));
                }
                if (openItemsSection != null) {
                    commonWaits.clickWithoutInterception(openItemsSection);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
                }

                // First try targeted candidates
                for (String xp : candidates) {
                    List<WebElement> els = findElements(By.xpath(xp));
                    if (els == null || els.isEmpty()) {
                        continue;
                    }
                    for (WebElement el : els) {
                        try {
                            if (el != null && el.isDisplayed()) {
                                try {
                                    javaScriptScrollIntoView(el);
                                } catch (Exception ignored) {
                                }
                                clickByAction(el);
                                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
                                try {
                                    clickChatTab();
                                } catch (Exception ignored) {
                                }
                                return this;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                // Last resort: if only one Open Item exists in the list, click it.
                // Engineering assumption: this test has a single open chat under the chosen section at this point.
                List<WebElement> openItems = findElements(By.xpath(OPEN_ITEM_REQUEST_ANY_XPATH));
                List<WebElement> visibleOpenItems = new ArrayList<>();
                if (openItems != null) {
                    for (WebElement el : openItems) {
                        try {
                            if (el != null && el.isDisplayed()) {
                                visibleOpenItems.add(el);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (visibleOpenItems.size() == 1) {
                    WebElement only = visibleOpenItems.get(0);
                    try {
                        javaScriptScrollIntoView(only);
                    } catch (Exception ignored) {
                    }
                    clickByAction(only);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
                    try {
                        clickChatTab();
                    } catch (Exception ignored) {
                    }
                    return this;
                }
            } catch (Exception ignored) {
            }

            commonWaits.waitForSometime(1_000);
        }

        Assert.fail("Unable to select request from SSP Open Items. Section=" + section + " | initialChatMessage=" + msg);
        return this;
    }

    public void verifySystemMessageOnSSPConsole(String expectedMessage) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        String messageInbox = String.format(SYSTEM_MESSAGE_IN_INBOX_XPATH, expectedMessage);
        commonWaits.waitForElementPresent(messageInbox);
        Assert.assertTrue(isElementPresent(By.xpath(messageInbox)), "Message not verified."
                + "Message expected = " + expectedMessage + "And Message actual  = " + getDriver().findElement(By.xpath(messageInbox)).getText());
    }

    public void attachFileInSspConsole(File fileName) {
        WebElement sendAttachment = commonWaits.waitForElementClickable(findElement(By.xpath(BTN_SEND_ATTACHMENT_XPATH)));
        commonWaits.clickWithoutInterception(sendAttachment);
        commonMethods.attachFile(fileName, findElement(By.xpath(INPUT_UPLOAD_FILE_XPATH)));
        WebElement sendBtn = commonWaits.waitForElementClickable(findElement(By.xpath(BTN_SEND_XPATH)));
        commonWaits.clickWithoutInterception(sendBtn);
    }

    public void verifyAttachmentIsShared(String attachmentFileName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        Assert.assertNotNull(attachmentFileName, "attachmentFileName must not be null");
        String name = attachmentFileName.trim();
        Assert.assertFalse(name.isEmpty(), "attachmentFileName must not be empty");

        // SSP renders attachments differently across versions (sometimes no @download attr).
        // Engineering assumption: attachment file name appears either as a download link or as visible filename text in the chat transcript.
        String[] candidates = new String[]{
                String.format(ATTACHMENT_DOWNLOAD_LINK_XPATH, name),
                "//a[contains(@download,'" + name + "')]",
                "//a[contains(@href,'" + name + "') or contains(normalize-space(.),'" + name + "')]",
                "//span[contains(@class,'adapt-uploader-file-name') and contains(normalize-space(.),'" + name + "')]",
                "//span[contains(@class,'file-name') and contains(normalize-space(.),'" + name + "')]",
                "//*[contains(@class,'chatMessage') and contains(normalize-space(.),'" + name + "')]",
                "//div[contains(@class,'chat') or contains(@class,'conversation')]//*[contains(normalize-space(.),'" + name + "')]"
        };

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < ATTACHMENT_VERIFY_TIMEOUT_MS) {
            for (String xp : candidates) {
                try {
                    if (!findElements(By.xpath(xp)).isEmpty()) {
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
            commonWaits.waitForSometime(ATTACHMENT_VERIFY_POLL_INTERVAL_MS);
        }

        Assert.fail("Attachment not found in SSP chat: " + name);
    }

    public boolean verifyPrivateChatMessageNotThere() {
        log.info("Verify Private Chat message");
        commonWaits.waitForSometime(commonWaits.minDelay);
        boolean res = isElementPresent(By.xpath(NOTE_ADDED_SYMBOL_XPATH));
        switchtoDefaultFrame();
        return res;
    }

    public void openHKMSharedKnowledgeArticleAndSwitchBack(String articleName) {
        Assert.assertNotNull(articleName, "articleName must not be null");
        String name = articleName.trim();
        Assert.assertFalse(name.isEmpty(), "articleName must not be empty");

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
        String linkXpath = String.format(SHARED_KNOWLEDGE_ARTICLE_LINK_XPATH, name);

        int beforeCount = getDriver().getWindowHandles().size();
        String originalHandle = getDriver().getWindowHandle();
        String originalUrl = "";
        try {
            originalUrl = getDriver().getCurrentUrl();
        } catch (Exception ignored) {
        }

        WebElement link = commonWaits.waitForByElementToBeVisible(By.xpath(linkXpath));
        commonWaits.clickWithoutInterception(link);
        commonWaits.waitForSometime(commonWaits.minDelay);

        // Prefer new window/tab if it opens (no breaks in loops).
        long start = System.currentTimeMillis();
        boolean newWindowOpened = false;
        boolean navigatedSameTab = false;
        while (System.currentTimeMillis() - start < 15_000 && !newWindowOpened && !navigatedSameTab) {
            newWindowOpened = getDriver().getWindowHandles().size() > beforeCount;
            if (!newWindowOpened) {
                try {
                    String nowUrl = getDriver().getCurrentUrl();
                    navigatedSameTab = (originalUrl != null && !originalUrl.isEmpty() && nowUrl != null && !nowUrl.equals(originalUrl));
                } catch (Exception ignored) {
                }
            }
            if (!newWindowOpened && !navigatedSameTab) {
                commonWaits.waitForSometime(500);
            }
        }

        if (newWindowOpened) {
            commonWaits.switchToLastWindow();
            commonWaits.waitForPageLoad(getDriver());
            try {
                getDriver().close();
            } catch (Exception ignored) {
            }
            try {
                getDriver().switchTo().window(originalHandle);
            } catch (Exception ignored) {
            }
        } else {
            // Same-window navigation: return back to chat window.
            try {
                getDriver().navigate().back();
            } catch (Exception ignored) {
            }
        }

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
    }

    /**
     * Cancels/ends the current chat session from the end-user perspective.
     * Engineering assumption: depending on tenant/build, SSP may expose either a "Cancel chat" or "End chat" control.
     * If neither is available, fall back to closing the chat window (best-effort cleanup).
     */
    public void cancelChatWithAgent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);

        By[] candidates = new By[]{
                By.xpath(CANCEL_CHAT_REQUEST_XPATH),
                By.xpath(BTN_END_CHAT_XPATH)
        };

        for (By by : candidates) {
            try {
                if (!findElements(by).isEmpty()) {
                    WebElement btn = commonWaits.waitForElementClickable(findElement(by));
                    clickByAction(btn);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);

                    // Optional confirmation dialog
                    try {
                        if (!findElements(By.xpath(BTN_CONFIRM_YES_XPATH)).isEmpty()) {
                            WebElement yes = commonWaits.waitForElementClickable(findElement(By.xpath(BTN_CONFIRM_YES_XPATH)));
                            commonWaits.clickWithoutInterception(yes);
                        }
                    } catch (Exception ignored) {
                    }

                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSSPLiveChat);
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback for environments without an explicit cancel/end action.
        try {
            closeLiveChatWindowUsingBrowserClose();
        } catch (Exception ignored) {
        }
    }

}