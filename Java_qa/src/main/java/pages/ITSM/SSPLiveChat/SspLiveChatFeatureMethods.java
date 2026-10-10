package pages.ITSM.SSPLiveChat;

import java.io.File;
import org.openqa.selenium.WebDriver;
import framework.BasePage;
import framework.Launcher;
import util.SolqaLogger;

public class SspLiveChatFeatureMethods extends BasePage {

    static SolqaLogger log = new SolqaLogger(SspLiveChatFeatureMethods.class.getName());
    SspLiveChat sspLiveChat = new SspLiveChat();

    public void openQuickLinksTab() {
        sspLiveChat.openQuickLinksTab();
    }

    public SspLiveChatFeatureMethods openQuickLinkAndVerifyUrlContainsAndClose(String linkText, String expectedUrlContains) {
        sspLiveChat.openQuickLinkAndVerifyUrlContainsAndClose(linkText, expectedUrlContains);
        return this;
    }

    public SspLiveChatFeatureMethods setTopicFromChatTopics(String topic){
        sspLiveChat.setTopicFromChatTopics(topic);
        return this;
    }

    public SspLiveChatFeatureMethods openLiveChatPage() {
        sspLiveChat.openLiveChatPage();
        return this;
    }

    public SspLiveChatFeatureMethods typeMessageInTextArea(String message) {
        sspLiveChat.typeMessageInTextArea(message);
        return this;
    }

    public SspLiveChatFeatureMethods verifySystemMessageOnSSPConsole(String expectedMessage) {
        sspLiveChat.verifySystemMessageOnSSPConsole(expectedMessage);
        return this;
    }

    public SspLiveChatFeatureMethods clickChatNowOption() {
        sspLiveChat.clickChatNowOption();
        return this;
    }

    public SspLiveChatFeatureMethods attachFileInSspConsole(File fileName) {
        sspLiveChat.attachFileInSspConsole(fileName);
        return this;
    }

    public SspLiveChatFeatureMethods switchToSSPLiveChat(WebDriver webDriver) {
        log.info("----------- Switching to SSP Live Chat window ----------------------");
        String windowHandle = webDriver.getWindowHandle();
        webDriver.switchTo().window(windowHandle);
        Launcher.setDriver(webDriver);
        return this;
    }

    public SspLiveChatFeatureMethods verifyMessageOnSSPConsole(String expectedMessage) {
        sspLiveChat.verifyMessageOnSSPConsole(expectedMessage);
        return this;
    }

    public SspLiveChatFeatureMethods verifyAttachmentIsShared(String attachmentFileName) {
        sspLiveChat.verifyAttachmentIsShared(attachmentFileName);
        return this;
    }

    public SspLiveChatFeatureMethods verifyPrivateChatMessageNotThere() {
        sspLiveChat.verifyPrivateChatMessageNotThere();
        return this;
    }

    public SspLiveChatFeatureMethods closeLiveChatWindowUsingBrowserClose() {
        sspLiveChat.closeLiveChatWindowUsingBrowserClose();
        return this;
    }

    public SspLiveChatFeatureMethods openHKMSharedKnowledgeArticleAndSwitchBack(String articleName) {
        sspLiveChat.openHKMSharedKnowledgeArticleAndSwitchBack(articleName);
        return this;
    }

    public SspLiveChatFeatureMethods selectRequestFromOpenItemOnSSPConsole(String initialChatMessage, String openItemsSectionName) {
        sspLiveChat.selectRequestFromOpenItemOnSSPConsole(initialChatMessage, openItemsSectionName);
        return this;
    }

    /**
     * Attempts to cancel/end the current SSP live chat session from the end-user side.
     * If the explicit cancel/end control isn't available in a given build, falls back to closing the chat window.
     */
    public SspLiveChatFeatureMethods cancelChatWithAgent() {
        sspLiveChat.cancelChatWithAgent();
        return this;
    }

}
