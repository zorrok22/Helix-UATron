package pages.ITSM.SmartIT;

import java.io.File;
import java.util.List;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class WorkOrderPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    String iFrame = "//iframe[@id='pwa-frame']";
    String feedIFrame = "//*[@id='304423041']//iframe";
    String attachedFileOnTimeline = "//*[@id='attachment-block']//*[contains(@id, 'attachement-name') and text()='%s']";
    public String activityNotebox = "//*[contains(@placeholder,'add a note')]";
    public String btnActivityNotesPost = "//button[contains(@id,'304268430')]";
    public String uploadFileInputOnActivity = "//*[@automationid='307597955']//input[@type='file']";

    //WebElements Locators
    @FindBy(xpath = "//*[@ux-id='system-alert']//*[@ux-id='system-alert-text']")
    WebElement sysAlertMsgLocator;

    @FindBy(xpath = "//input[contains(@class,'checkbox')]//..//div//span[text()='Public']")
    public WebElement chkBoxPublicComment;

    public WorkOrderPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(WorkOrderPage.class.getName());
        commonWaits = new CommonWaits();
    }

    @FindBy(xpath = "//*[@id='304427321']//adapt-advanced-filter")
    public WebElement filterIcon;
    public boolean isAttachedFileAppearedUnderActivityTab(String expectedFileName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
        switchToFrame(findElement(By.xpath(iFrame)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterIcon,10), "Filter icon is not visible on work order details page, might be issue in loading the page.");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(feedIFrame),10), "Activity feed iframe is not visible on work order details page, might be issue in loading the page.");
        boolean flag = false;
        List<WebElement> elements = findElements(By.xpath(feedIFrame));
        commonWaits.waitForPageFullyLoaded(50, 1);
        for (WebElement element : elements) {
            getDriver().switchTo().frame(element);
            commonWaits.waitForElementLoad(By.xpath(String.format(attachedFileOnTimeline, expectedFileName)));
            if (isElementPresent(By.xpath(String.format(attachedFileOnTimeline, expectedFileName)))) {
                flag = true;
                break;
            }
            getDriver().switchTo().parentFrame();
        }
        switchtoDefaultFrame();
        switchtoDefaultFrame();
        return flag;
    }

    public void addComment_InActivityFeed(String value) {
        addComment_InActivityFeed(value, true);
    }

    public void addComment_InActivityFeed(String value, boolean isPublic) {
        switchtoDefaultFrame();
        switchToFrame(findElement(By.xpath(iFrame)));
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(findElement(By.xpath(activityNotebox)));
        commonWaits.waitForAngularRequestsToFinish();
        WebElement activityNoteBoxElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(activityNotebox)));
        commonWaits.clickWithoutInterception(activityNoteBoxElement);
        keyClearAndType(activityNotebox, value);

        // If left unchecked, it remains private. For public notes, toggle the Public option.
        if (isPublic) {
            commonWaits.waitForElementVisible(chkBoxPublicComment).click();
        }

        commonWaits.waitForElementClickable(findElement(By.xpath(btnActivityNotesPost)));
        commonWaits.waitForAngularRequestsToFinish();
        if (Objects.equals(findElement(By.xpath(btnActivityNotesPost)).getAttribute("disabled"), "true")) {
            Assert.fail("-----Post button of Activity Timeline is disabled");
        } else {
            findElement(By.xpath(btnActivityNotesPost)).click();
        }
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForSometime(commonWaits.minDelay);
        switchtoDefaultFrame();
    }

    public void addAttachmentNote_InActivityFeed(String noteText, File fileToUpload, boolean isPublic) {
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToFrame(findElement(By.xpath(iFrame)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), filterIcon,10), "Filter icon is not visible on work order details page, might be issue in loading the page.");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(feedIFrame),10), "Activity feed iframe is not visible on work order details page, might be issue in loading the page.");
        commonWaits.waitForAngularRequestsToFinish();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(activityNotebox), 15), "Activity note box is not visible in timeline.");
        commonWaits.clickWithoutInterception(By.xpath(activityNotebox));
        keyClearAndType(activityNotebox, noteText);

        if (isPublic) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), chkBoxPublicComment, 15), "Public comment checkbox is not visible in timeline.");
            commonWaits.clickWithoutInterception(chkBoxPublicComment);
        }

        WebElement uploadInput = commonWaits.waitForElementPresenceLocated(By.xpath(uploadFileInputOnActivity));
        new CommonMethods().attachFile(fileToUpload, uploadInput);

        commonWaits.waitForElementClickable(findElement(By.xpath(btnActivityNotesPost)));
        if (Objects.equals(findElement(By.xpath(btnActivityNotesPost)).getAttribute("disabled"), "true")) {
            Assert.fail("-----Post button of Activity Timeline is disabled");
        } else {
            WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(btnActivityNotesPost));
            commonWaits.clickWithoutInterception(we);
        }

        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForSometime(commonWaits.minDelay);
        switchtoDefaultFrame();
    }

}