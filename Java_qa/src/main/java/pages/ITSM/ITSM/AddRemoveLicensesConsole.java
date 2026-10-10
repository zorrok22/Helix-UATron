package pages.ITSM.ITSM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class AddRemoveLicensesConsole extends ITSMDashboard {

    CommonWaits commonWaits;
    SolqaLogger log;
    CommonMethods commonMethods;

    public AddRemoveLicensesConsole() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(AddRemoveLicensesConsole.class.getName());
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    String licenseTypeColumnValue = "//table[@id='T15417']//span[normalize-space(.)='%s']/ancestor::td";
    String textAreaField = "//label[normalize-space(text())='%s']/following-sibling::textarea";
    String inputFieldByLabel = "(//label[contains(. ,'%s')]/following-sibling::*//input)[1]";

    public AddRemoveLicensesConsole verifyPageLoaded() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                        By.xpath(String.format(ElementByARID, ElementARID.Add_Remove_Licenses_Page_Title)), 10),
                "Add/Remove Licenses Console page did not load successfully.");
        return this;
    }

    public AddRemoveLicensesConsole selectLicenseType(String licenseType) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                        By.xpath(String.format(licenseTypeColumnValue, licenseType)), 5),
                "License type '" + licenseType + "' not found in the Add/Remove Licenses Console.");
        commonWaits.clickWithoutInterception(By.xpath(String.format(licenseTypeColumnValue, licenseType)));
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textAreaField, "License Type*")));
        Assert.assertTrue(commonWaits.waitForExpectedValue(we, licenseType, 5),
                "Selected license type '" + licenseType + "' did not populate in the License Type field.");
        return this;
    }

    public AddRemoveLicensesConsole setNumberOfLicensesInputValue(String numberOfLicensesValue) {
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(inputFieldByLabel, "Number of Licenses*")));
        we.clear();
        we.sendKeys(numberOfLicensesValue);
        return this;
    }

    public AddRemoveLicensesConsole clickSaveButton() {
        commonWaits.clickByText(SolQAConstants.SAVE);
        return this;
    }

}