package pages.ITOM.Developer_Tool;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITSM.dwp.DWPDashboard;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class DeveloperToolPage extends BasePage {
    WebDriver driver;

    static SolqaLogger log = new SolqaLogger(DeveloperToolPage.class.getName());
    CommonWaits commonWaits;
    String appName = null;

    public String bmcHelixPage="(//header//nav//span/span)[2]";

    public DeveloperToolPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public DeveloperToolPage(){
        PageFactory.initElements(getDriver(), this);
        commonWaits=new CommonWaits();
    }

    public void verifyDeveloperToolPageLaunchedSuccessfully(){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        appName = commonWaits.waitForByElementToBeVisible(By.xpath(bmcHelixPage)).getText().trim();
        log.info("----- Opened application name - "+ appName);
        Assert.assertEquals(appName, "Developer Tools", "Developer Tools application is not launched successfully");
    }
}
