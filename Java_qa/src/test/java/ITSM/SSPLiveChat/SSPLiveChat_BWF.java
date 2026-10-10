package ITSM.SSPLiveChat;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.SSPLiveChat.SspLiveChatFeatureMethods;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.CommonMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.WebDriverManager;
import util.Listener;

public class SSPLiveChat_BWF extends Launcher {

	static SolqaLogger log = new SolqaLogger(SSPLiveChat_BWF.class.getName());
	SspLiveChatFeatureMethods sspLiveChatFeatureMethods;
    BWFFeatureMethods bwfFeatureMethod;
    WebDriver sspLiveChatDriver, bwfLiveChatDriver;
    ReadJson readJson = new ReadJson();
    Map<Object, String> bwfUserData, liveChatTestData, liveChatUserData, enduser;

    @BeforeMethod(alwaysRun=true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        // Note: This test uses multiple WebDrivers (SSP and BWF), so we'll handle them in the test method
    }

    //previous owner: shudeshp
    @Test(description = "LiveChat SSP conversation with BWF Live agent", groups = {"MAT", "BWF","DWP" })
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11808", Products = { Product.Helix_SSPLIVECHAT,Product.Helix_BWF })
    public void DRSQA_11808_SSP_BWFLiveAgentConversion(ITestContext context) throws IOException {
        log.info("*********** DRSQA_11808: SSP_BWFLiveAgentConversion ****** START *************");
      
        log.info("---------Step1: Login to SSP through end user------------");
        sspLiveChatDriver = launchWeb(SolQAConstants.appSSPLiveChat,SolQAConstants.appSSPLiveChat);
        liveChatTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSSPLiveChat, "SSPLiveChatTestData",
                "DRSQA_11808_Data");
        liveChatUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSSPLiveChat, "SSPLiveChatUsers", "bmcopsenduser");
        login(liveChatUserData.get("UserName"), liveChatUserData.get("Password"), SolQAConstants.appSSPLiveChat);
           context.setAttribute("DRSQA_11808_Driver", getDriver());
      		context.setAttribute("DRSQA_11808_Product", Product.Helix_SmartIT);

        log.info("---------Step: end user select topic------------");
        sspLiveChatFeatureMethods = new SspLiveChatFeatureMethods();
        sspLiveChatFeatureMethods.setTopicFromChatTopics(liveChatTestData.get("topic"));

        log.info("----DWP initialization done. User: " + liveChatUserData.get("UserName"));
        String randomStr = RandomStringUtils.randomAlphanumeric(5);
        String initialMessage = "Hello" + randomStr;
        sspLiveChatFeatureMethods.typeMessageInTextArea(initialMessage);
        log.info("---------DWP request for live chat is submitted with text= " + initialMessage);

        log.info("---------Step2: Login to BWF Live chat agent------------");
        bwfLiveChatDriver= launchWeb(SolQAConstants.bundleName,SolQAConstants.appBWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        context.setAttribute("DRSQA_11808_Driver", getDriver());
      	context.setAttribute("DRSQA_11808_Product", Product.Helix_BWF);

        bwfFeatureMethod = new BWFFeatureMethods();
        bwfFeatureMethod.verifyLoginToBWF();

        log.info("---------Step3: Accept Live Chat------------");
        log.info("Opening BWF Live Chat console");
        bwfFeatureMethod.openLiveChat();
        bwfFeatureMethod.verifyLiveChatOpened();
        String agentName = bwfFeatureMethod.getLiveAgentName();
        bwfFeatureMethod.clickQueueLink();
        String customerName = "(" + liveChatUserData.get("UserName") + ")" + " " + liveChatUserData.get("FirstName");
        bwfFeatureMethod.selectCustomerFromQueue(initialMessage, customerName);
        log.info("Accepting Chat from BWF Live Chat console");
        bwfFeatureMethod.clickAcceptChat();
        bwfFeatureMethod.captureScreenshot("DRSQA_11808_requestAccepted", SolQAConstants.appBWF);

        log.info("---------Step4: Conversation------------");
        log.info("Switch to SSP Live Chat console");
        sspLiveChatFeatureMethods.switchToSSPLiveChat(sspLiveChatDriver);
        sspLiveChatFeatureMethods.verifySystemMessageOnSSPConsole(String.format(liveChatTestData.get("systemMessageForJoiningTheSession"), agentName));
        sspLiveChatFeatureMethods.captureScreenshot("DRSQA_11808_Verified_system_massage", SolQAConstants.appDWP);
        String attachmentName1 = "DRSQA_11808.txt";
        File fileName = CommonMethods.createTextFile(attachmentName1);
        sspLiveChatFeatureMethods.attachFileInSspConsole(fileName);
        sspLiveChatFeatureMethods.captureScreenshot("DRSQA_11808_Request_submitted_for_live_chat", SolQAConstants.appDWP);

        log.info("Switch to BWF live chat");
        bwfFeatureMethod.switchToBWFLiveChat(bwfLiveChatDriver);
        bwfFeatureMethod.verifyAttachmentIsShared(attachmentName1);
        bwfFeatureMethod.captureScreenshot("DRSQA_11808_verified_attachment_upload_massage", SolQAConstants.appBWF);
        bwfFeatureMethod.verifyMessageOnLiveChat(initialMessage);
        String customerFullName=liveChatUserData.get("FullName");
        bwfFeatureMethod.verifyMessageOnLiveChat(String.format(liveChatTestData.get("SSPattachmentUploadMessage"), customerFullName));
        String filepath = System.getProperty("user.dir") + File.separator + attachmentName1;
        filepath = URLDecoder.decode(filepath, StandardCharsets.UTF_8);
        File file = new File(filepath);
        log.info("----DRSQA_11808_File2 Absolute path----- " + file.getAbsolutePath());
        bwfFeatureMethod.uploadAttachment(file);

        String SupportAgentFullName= bwfUserData.get("FullName");
        sspLiveChatFeatureMethods.switchToSSPLiveChat(sspLiveChatDriver);
        sspLiveChatFeatureMethods.verifyMessageOnSSPConsole(String.format(liveChatTestData.get("attachmentUploadMessage"), SupportAgentFullName));

        log.info("---------Step5: Send private note-----------");
        log.info("Switch to BWF live chat");
        bwfFeatureMethod.switchToBWFLiveChat(bwfLiveChatDriver);
        bwfFeatureMethod.sendPrivateNoteInLiveChat(liveChatTestData.get("PrivateNote"));

        log.info("Switch to SSP live chat");
        sspLiveChatFeatureMethods.switchToSSPLiveChat(sspLiveChatDriver);
        sspLiveChatFeatureMethods.verifyPrivateChatMessageNotThere();

        log.info("---------Step6: Search article------------");
        log.info("Switch to BWF live chat");
        bwfFeatureMethod.switchToBWFLiveChat(bwfLiveChatDriver);
        log.info("--------In BWF live chat agent request is accepted with message ");
        bwfFeatureMethod.captureScreenshot("DRSQA_11808_Agent_request_Accepted", SolQAConstants.appBWF);
        bwfFeatureMethod.selectResourceTabFromRightPanelAndShareTheKnowledge(liveChatTestData.get("article"));
        bwfFeatureMethod.captureScreenshot("DRSQA_11808_Knowledge_Article_Shared", SolQAConstants.appBWF);
        bwfFeatureMethod.openSharedKnowledgeArticleAndSwitchBack(liveChatTestData.get("article"));
        bwfFeatureMethod.captureScreenshot("DRSQA_11808_Knowledge_Article_opened_and_come_back_to_live_chat_console", SolQAConstants.appBWF);

        log.info("---------Step7: Create case------------");
        bwfFeatureMethod.createCaseTicketForLiveChatRequest();
        List<String> attachmentNames = new ArrayList<>();
        attachmentNames.add(attachmentName1);

        log.info("---------Step8: Open case and review activity------------");
        bwfFeatureMethod.previewAndVerifyCaseTicketDetailsOnLiveChatPage(customerFullName);
        bwfFeatureMethod.viewCaseAndVerifyAttachmentFilesSharedAndDetailsOnActivityPage(attachmentNames,initialMessage);
        bwfFeatureMethod.captureScreenshot("DRSQA_11808_case_created", SolQAConstants.appBWF);
        log.info("--------Selecting article for request: ");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (sspLiveChatFeatureMethods != null) {
            sspLiveChatFeatureMethods.closeBrowser();
        }
        if (bwfFeatureMethod != null) {
            bwfFeatureMethod.closeBrowser();
        }
        WebDriverManager.cleanupDriver();
        Listener.cleanupThreadLocal();
    }
}
