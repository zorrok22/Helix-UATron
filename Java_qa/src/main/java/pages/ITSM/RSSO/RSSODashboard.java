package pages.ITSM.RSSO;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.List;
public class RSSODashboard extends BasePage {
    static SolqaLogger log = new SolqaLogger(RSSODashboard.class.getName());
    CommonWaits commonWaits=new CommonWaits();
    CommonMethods commonMethods=new CommonMethods();
    SolQAConstants solqaConstants = new SolQAConstants();

    public RSSODashboard() {
        PageFactory.initElements(getDriver(), this);
      }

    //  String Locators
    String rssoTabsLink = "//a[@role='link' and normalize-space()='%s']";
    String timeOutFieldXpath = "//*[normalize-space()='%s']/ancestor::div[contains(@class,'adapt')]//input";
    String dropdownOptionXpathForTimeUnit = "//div[normalize-space()='%s']/parent::button";
    String dropdownOptionButtonXpath = "//adapt-highlight[normalize-space()='%s']/ancestor::button";

    //  WebElements

    @FindBy(xpath = "//button[normalize-space()='Single Sign-On']")
	public WebElement RSSOPageTitle;

    @FindBy(xpath = "//*[normalize-space()='Search']/parent::div//input")
    WebElement TenantNameSearchBox;

    @FindBy(xpath = "//td[@data-testid='adapt-table_column-0_row-0']//div[contains(@class,'ellipsis')]")
    WebElement TenantName;

    @FindBy(xpath = "//button[@title='Edit Tenant']")
    WebElement editTenantButton;

    @FindBy(xpath = "//span[normalize-space()='UI idle timeout']/parent::div[@class='checkbox__item']")
    WebElement enableUIIdleTimeoutCheckbox;

    @FindBy(xpath = "//button[@id='footerSaveBtn']")
    WebElement saveButton;

    @FindBy(xpath = "//button[@id='select-tenant']")
    WebElement pinButton;

    @FindBy(xpath = "//button[normalize-space()='Update']")
    WebElement updateButton;
    
    @FindBy(xpath = "//div[normalize-space()='Session timeout']")
    WebElement sessionIdleWarningMessage;

    @FindBy(xpath = "//button[normalize-space()='Ok']")
    WebElement okButton;

    @FindBy(xpath = "//button[@aria-label='Close dialog']")
    WebElement crossButton;

    @FindBy(xpath = "//h4[normalize-space()='Idle Timeout Settings']/parent::div//div[normalize-space()='Hours' or normalize-space()='Seconds']/parent::button")
	List<WebElement> timeUnitButtons;

    public RSSODashboard verifyLogin() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        WebElement generalLink = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(rssoTabsLink, SolQAConstants.GENERAL)));
        Assert.assertTrue(generalLink.isDisplayed(), "RSSO page is not launched successfully");
        return this;
    }

    public RSSODashboard clickOnRSSOTab(String tabName) {
        WebElement tenantLink = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(rssoTabsLink, tabName)));
        commonWaits.clickWithoutInterception(tenantLink);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public RSSODashboard verifyTenantsIsPresent(String tenantName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementClickable(TenantNameSearchBox);
        TenantNameSearchBox.sendKeys(tenantName);
        commonWaits.waitForExecutionComplete();
        commonMethods.pressEnterKey();
        try {
            Assert.assertTrue(commonWaits.isElementPresent(TenantName), "Tenant is not present in the list");
            Assert.assertEquals(TenantName.getText().trim(), tenantName, "Tenant name is not matching for "+tenantName);
        }
        catch (AssertionError e) {
            commonWaits.waitForSometime(commonWaits.minDelay);
        }
        Assert.assertTrue(commonWaits.isElementPresent(TenantName), "Tenant is not present in the list");
        Assert.assertEquals(TenantName.getText().trim(), tenantName, "Tenant name is not matching for "+tenantName);
        return this;
    }

    public RSSODashboard clickOnEnableUIIdleTimeout(String tenantName) {
        commonWaits.clickWithoutInterception(editTenantButton);
        commonWaits.waitForElementVisible(enableUIIdleTimeoutCheckbox);
        if(!enableUIIdleTimeoutCheckbox.isSelected()) {
            log.info("UI idle timeout is not enabled for "+tenantName);
            commonWaits.clickWithoutInterception(enableUIIdleTimeoutCheckbox);
            commonWaits.clickWithoutInterception(saveButton);
            return this;
        }   
        else{
            log.info("UI idle timeout is already enabled for "+tenantName);
            commonWaits.clickWithoutInterception(saveButton);
        }
        return this;
    }

    public RSSODashboard clickOnDisableUIIdleTimeout(String tenantName) {
        commonWaits.clickWithoutInterception(editTenantButton);
        commonWaits.waitForElementVisible(enableUIIdleTimeoutCheckbox);
        if(enableUIIdleTimeoutCheckbox.isSelected()) {
            log.info("UI idle timeout is enabled for "+tenantName);
            commonWaits.clickWithoutInterception(enableUIIdleTimeoutCheckbox);
            commonWaits.clickWithoutInterception(saveButton);
            return this;
        }   
        else{
            log.info("UI idle timeout is already disabled for "+tenantName);
            commonWaits.clickWithoutInterception(saveButton);
            return this;
        }
    }

    public RSSODashboard clickOnPinButton() {
        commonWaits.clickWithoutInterception(pinButton);
        return this;
    }

    public RSSODashboard setRequiredTimeOut(String TimeOutField, String TimeOutValue) {
      log.info("Setting the required time out for "+TimeOutField+" to "+TimeOutValue);
      commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
      WebElement timeOutField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(timeOutFieldXpath, TimeOutField)));
      timeOutField.click();
      timeOutField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
      timeOutField.sendKeys(Keys.DELETE);
      timeOutField.sendKeys(TimeOutValue);
      return this;
    }

    public RSSODashboard selectTimeUnit(String TimeUnit) {
      for (WebElement element : timeUnitButtons) {
        if (element.isDisplayed()) {
             clickWithoutInterception(element);
             clickWithoutInterception(By.xpath(String.format(dropdownOptionButtonXpath, TimeUnit)));
            commonWaits.waitForExecutionComplete();
        }
}
      return this;
    }
    public RSSODashboard clickOnUpdateButton() {
        if(!updateButton.isEnabled()) {
            log.info("Update button is not enabled");
            return this;
        }
        else{
            log.info("Update button is enabled");
            commonWaits.clickWithoutInterception(updateButton);
            return this;
        }
    }

    public RSSODashboard verifySessionIdleWarningMessage() {
        log.info("Verifying session idle warning message");
        boolean isSessionIdleWarningMessagePresent = commonWaits.isElementPresent(sessionIdleWarningMessage,60);
        Assert.assertTrue(isSessionIdleWarningMessagePresent, "Session idle warning message is not present");
        log.info("Session idle warning message is present");
        return this;
    }

    public RSSODashboard verifySessionIdleWarningMessageIsNotPresent() {
        log.info("Verifying session idle warning message is not present");
        boolean isSessionIdleWarningMessagePresent = commonWaits.isElementPresent(sessionIdleWarningMessage);
        Assert.assertFalse(isSessionIdleWarningMessagePresent, "Session idle warning message is present");
        log.info("Session idle warning message is not present");
        return this;
    }

    public RSSODashboard clickOnOkButton() {
        if(commonWaits.isElementPresent(okButton)){
            log.info("Clicking on Ok button");
            commonWaits.clickWithoutInterception(okButton);
            log.info("Ok button is clicked");
            return this;
        }
        else{
            log.info("Ok button is not present");
            return this;
        }
    }

    public RSSODashboard verifySessionLogout() {
        log.info("Verifying session logout");
        boolean isSessionLogout = commonWaits.isElementPresent(userName);
        Assert.assertTrue(isSessionLogout, "Session is not logged out");
        log.info("Session is logged out");
        return this;
    }

    public RSSODashboard verifySessionLogoutIsNotPresent() {
        log.info("Verifying session logout is not present");
        boolean isSessionLogoutPresent = commonWaits.isElementPresent(userName);
        Assert.assertFalse(isSessionLogoutPresent, "Session is logged out");
        log.info("Session is not logged out");
        return this;
    }

    public RSSODashboard openNewTab() {
        commonMethods.openNewTab();
        return this;
    }

    public RSSODashboard clickOnCrossButton() {
        commonWaits.clickWithoutInterception(crossButton);
        return this;
    }
}
