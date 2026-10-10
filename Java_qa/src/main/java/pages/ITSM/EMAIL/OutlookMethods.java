package pages.ITSM.EMAIL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolqaLogger;

public class OutlookMethods extends BasePage{

    public static final SolqaLogger log = new SolqaLogger(OutlookMethods.class.getName());
    CommonWaits commonWaits;

    public OutlookMethods() {
        PageFactory.initElements(getDriver(), this);
        commonWaits=new CommonWaits();
    }

    //String Locators
    String MailResponseXpath = "(//div[contains(@aria-label,'%s')])[1]";

    //WebElement Locators
    @FindBy(xpath = "//button[normalize-space()='New']")
    WebElement newButton;

    @FindBy(xpath = "//input[@aria-label='To']")
    WebElement toField;

    @FindBy(xpath = "//input[contains(@aria-label,'Subject')]")
    WebElement subjectField;

    @FindBy(xpath = "//button[contains(@autoid,'mcp') and @aria-label='Send']")
    WebElement sendButton;

    @FindBy(xpath = "//a[contains(.,'Resolve')]")
    WebElement resolvedButton;

    @FindBy(xpath = "//a[contains(.,'Reopen')]")
    WebElement reopenButton;


    public OutlookMethods clickOnNewButton() {
        log.info("Clicking on New button");
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        commonWaits.waitForElementClickable(newButton);
        clickWithoutInterception(newButton);
        return this;
    }

    public OutlookMethods setToField(String toFieldValue) {
        log.info("Setting To field to " + toFieldValue);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        commonWaits.waitForElementClickable(toField);
        toField.sendKeys(toFieldValue);
        return this;
    }

    public OutlookMethods setSubjectField(String subjectFieldValue) {
        log.info("Setting Subject field to " + subjectFieldValue);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        commonWaits.waitForElementClickable(subjectField);
        subjectField.sendKeys(subjectFieldValue);
        return this;
    }

    public OutlookMethods clickOnSendButton() {
        log.info("Clicking on Send button");
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        commonWaits.waitForElementClickable(sendButton);
        clickWithoutInterception(sendButton);
        return this;
    }

    public OutlookMethods composeNewEmail(String toFieldValue, String subjectFieldValue) {
        clickOnNewButton();
        setToField(toFieldValue);
        setSubjectField(subjectFieldValue);
        clickOnSendButton();
        return this;
    }

    public OutlookMethods verifyRequiredMailResponse(String mailResponse) {
        log.info("Verifying required mail response");
        
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 15);
        commonWaits.waitForSometime(60000); //This hardcoded wait is because email response is not visible immediately.
        getDriver().navigate().refresh();
        try{
        WebElement mailResponseElement =commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MailResponseXpath, mailResponse)));
        Assert.assertTrue(mailResponseElement.isDisplayed(), "Required mail response is not found");
        }
        catch(Exception e){
            log.error("Required mail response is not found");
            commonWaits.waitForSometime(60000); //This hardcoded wait is because email response is not visible immediately.
            getDriver().navigate().refresh();
            WebElement mailResponseElement =commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MailResponseXpath, mailResponse)));
            Assert.assertTrue(mailResponseElement.isDisplayed(), "Required mail response is not found");
        }
        return this;
    }

    public String getINCIDFromMailMessage(String mailResponse) {
    WebElement mailResponseElement =commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MailResponseXpath, mailResponse)));
    String ariaLabel = mailResponseElement.getAttribute("aria-label");
    String incId = ariaLabel.replaceAll(".*(INC\\d+).*", "$1");
    return incId;
    }

    public OutlookMethods switchToOutlookPage(WebDriver webDriver) {
		log.info("----------- Switching to Outlook Page ----------------------");
		String windowHandle = webDriver.getWindowHandle();
		webDriver.switchTo().window(windowHandle);
		setDriver(webDriver);
		log.info("Switched to Outlook window successfully.");
		return this;
	}

    public OutlookMethods clickOnResolvedButton(String mailResponse) {
        log.info("Opening request from mail response");
        WebElement mailResponseElement =commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MailResponseXpath, mailResponse)));
        commonWaits.clickWithoutInterception(mailResponseElement);
        log.info("Opened request from mail response");
        log.info("Clicking on Resolved button");
        commonWaits.clickWithoutInterception(resolvedButton);
        log.info("Clicked on Resolved button");
        return this;
    }

    public OutlookMethods clickOnReopenButton(String mailResponse) {
        log.info("Opening request from mail response");
        WebElement mailResponseElement =commonWaits.waitForByElementToBeVisible(By.xpath(String.format(MailResponseXpath, mailResponse)));
        commonWaits.clickWithoutInterception(mailResponseElement);
        log.info("Opened request from mail response");
        log.info("Clicking on Reopen button");
        commonWaits.clickWithoutInterception(reopenButton);
        log.info("Clicked on Reopen button");
        return this;
    }

}
