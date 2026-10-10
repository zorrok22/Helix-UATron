package pages.ITSM.dwp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ServiceHealthPage extends BasePage {

    static SolqaLogger log = new SolqaLogger(ServiceHealthPage.class.getName());
    CommonWaits commonWaits;

    String TAB_BY_NAME = "//button[@role='tab' and contains(normalize-space(.), '%s')]";
    String CARD_CONTAINING_TEXT = "//*[contains(normalize-space(.), '%s')]/ancestor::*[self::dwp-card or self::dwp-card-view or self::dwp-list-view or self::div[contains(@class,'card')]][1]";
    String MENU_ITEM_BY_TEXT = "//*[self::button or self::a][normalize-space()='%s' or contains(normalize-space(.), '%s')]";

    String KEBAB_BUTTON_IN_CARD=".//button[contains(@aria-label,'More') or contains(@aria-label,'Actions') or contains(@class,'kebab') or contains(@data-ta,'more') or contains(@data-ta,'kebab')]";
    @FindBy(xpath = "//input[(contains(@placeholder,'Search') or contains(@aria-label,'Search'))]")
    WebElement SEARCH_INPUT_GENERIC;

    public ServiceHealthPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public ServiceHealthPage openTab(String tabName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By tab = By.xpath(String.format(TAB_BY_NAME, tabName));
        if (commonWaits.isElementPresent(tab)) {
            commonWaits.clickWithoutInterception(tab);
        } else {
            Assert.fail("Service Health tab not found: " + tabName);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceHealthPage searchHealthItem(String healthItemName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(SEARCH_INPUT_GENERIC), "Service Health search input not found");
        commonWaits.waitForElementVisible(SEARCH_INPUT_GENERIC);
        SEARCH_INPUT_GENERIC.clear();
        SEARCH_INPUT_GENERIC.sendKeys(healthItemName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceHealthPage openActionFromKebabMenu(String healthItemName, String actionName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By card = By.xpath(String.format(CARD_CONTAINING_TEXT, healthItemName));
        Assert.assertTrue(commonWaits.isElementPresent(card), "Service Health item card not found: " + healthItemName);
        WebElement cardEl = commonWaits.waitForByElementToBeVisible(card);

        By kebab = By.xpath(KEBAB_BUTTON_IN_CARD);
        Assert.assertFalse(cardEl.findElements(kebab).isEmpty(), "Kebab/actions button not found for health item: " + healthItemName);
        cardEl.findElements(kebab).get(0).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        By action = By.xpath(String.format(MENU_ITEM_BY_TEXT, actionName, actionName));
        Assert.assertTrue(commonWaits.isElementPresent(action), "Action '" + actionName + "' not found in kebab menu for: " + healthItemName);
        commonWaits.clickWithoutInterception(action);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        log.info("Opened action '" + actionName + "' from Service Health for item: " + healthItemName);
        return this;
    }
}

