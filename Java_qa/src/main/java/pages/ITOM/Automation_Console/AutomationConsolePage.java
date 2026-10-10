package pages.ITOM.Automation_Console;

import framework.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolqaLogger;

public class AutomationConsolePage extends BasePage {
    WebDriver driver;

    static SolqaLogger log = new SolqaLogger(AutomationConsolePage.class.getName());
    CommonWaits commonWaits;
    String appName = null;

    public String automationConsolePage="//span[@class='a-menu__text' and text()='Automation Console']";
    public String automationConsole = "//button[not(@disabled)]//span[contains(@class, 'a-product__name') and text()= 'Automation Console']";
    public String automationConsoleSelectionTSSAAndHelix = "//app-switch-hac-context//adapt-table[@id='change-hac-context']";

    public AutomationConsolePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public void verifyAutomationConsolePageLaunchedSuccessfully() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(driver, automationConsolePage, 30) ||
                commonWaits.waitForElementVisible_withFluentWait(driver, automationConsole, 30) ||
                commonWaits.waitForElementVisible_withFluentWait(driver, automationConsoleSelectionTSSAAndHelix, 30), "Automation Console application is not launched successfully");
    }

}
