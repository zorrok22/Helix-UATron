package pages.ITSM.dwp;

import framework.BasePage;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPBroadcastsAdminPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    String ADMIN_LEFT_NAV_LINK = "//*[self::a or self::button][normalize-space()='%s' or contains(normalize-space(.),'%s')]";
    String ROW_CONTAINS_TEXT = "//*[self::tr or self::div][contains(normalize-space(.), %s)]";
    String broadcastRow = "//*[self::tr or self::div][contains(normalize-space(.), '%s')][contains(normalize-space(.), '%s')]";
    String ACTIONS_BUTTON_IN_ROW = "//*[self::tr or self::div][contains(normalize-space(.), %s)]//*[self::button or self::a][contains(@aria-label,'Action')]";

    @FindBy(xpath = "//*[self::span or self::div][normalize-space()='Remove' or normalize-space()='Delete']/ancestor::*[self::button or self::a][1]")
    WebElement REMOVE_ACTION;
    @FindBy(xpath = "//input[contains(@placeholder,'Search') or contains(@aria-label,'Search') or contains(@name,'search')][1]")
    WebElement searchInput;
    @FindBy(xpath = "//button[normalize-space()='Remove' or normalize-space()='Delete' or normalize-space()='Yes' or normalize-space()='OK']")
    WebElement CONFIRM_REMOVE;

    public DWPBroadcastsAdminPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(DWPBroadcastsAdminPage.class.getName());
        commonWaits = new CommonWaits();
    }

    public DWPBroadcastsAdminPage openBroadcastsPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            By link = By.xpath(String.format(ADMIN_LEFT_NAV_LINK, "Broadcasts", "Broadcast"));
            if (commonWaits.isElementPresent(link, 3)) {
                commonWaits.clickWithoutInterception(link);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                return this;
            }
        } catch (Exception ignored) {
        }

        String current = getDriver().getCurrentUrl();
        int idx = current.indexOf("/admin");
        Assert.assertTrue(idx > 0, "Not on DWP admin URL. Current: " + current);
        String adminBase = current.substring(0, idx + "/admin".length());

        List<String> candidates = Arrays.asList(
                adminBase + "/broadcasts",
                adminBase + "/broadcasts/",
                adminBase + "/#/broadcasts",
                adminBase + "/#/broadcasts/"
        );

        for (String url : candidates) {
            try {
                getDriver().navigate().to(url);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                if (getDriver().getPageSource().toLowerCase().contains("broadcast")) {
                    return this;
                }
            } catch (Exception ignored) {
            }
        }

        Assert.fail("Unable to open Broadcasts page from DWP admin. Current: " + current);
        return this;
    }

    public DWPBroadcastsAdminPage searchBroadcast(String subject) {
        Assert.assertNotNull(subject, "Broadcast subject is null");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(searchInput).click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInput.sendKeys(subject);
        searchInput.sendKeys(Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public boolean isBroadcastPresent(String textContains) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By row = By.xpath(String.format(ROW_CONTAINS_TEXT, textContains));
        return commonWaits.isElementPresent(row, 2);
    }

    public DWPBroadcastsAdminPage verifyBroadcastRowContains(String titleContains, String statusContains) {
        Assert.assertNotNull(titleContains, "Expected title is null");
        Assert.assertNotNull(statusContains, "Expected status is null");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(broadcastRow, titleContains, statusContains)), 10),
                "Broadcast row not found with title/status. Title contains: '" + titleContains + "', Status contains: '" + statusContains + "'");
        return this;
    }

    public DWPBroadcastsAdminPage removeBroadcastBySubject(String subject) {
        Assert.assertNotNull(subject, "Broadcast subject is null");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        By actions = By.xpath(String.format(ACTIONS_BUTTON_IN_ROW, subject));
        Assert.assertTrue(commonWaits.isElementPresent(actions, 10), "Actions button not found for broadcast row: " + subject);
        commonWaits.clickWithoutInterception(actions);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        commonWaits.clickWithoutInterception(REMOVE_ACTION);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(CONFIRM_REMOVE);

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

}