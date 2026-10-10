package pages.ITSM.ITSM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CTMPeople extends ITSMDashboard {

    CommonWaits commonWaits;
    SolqaLogger log;
    CommonMethods commonMethods;

    public CTMPeople() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(CTMPeople.class.getName());
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    @FindBy(xpath = "//*[contains(@class,'btn') and contains(.,'Login/Access')]")
    WebElement loginAccessTab;
    @FindBy(xpath = "//*[contains(@class,'btn') and contains(.,'IS') and contains(.,'Application') and contains(.,'Licenses')]")
    WebElement IS_Application_Licenses_Tab;
    @FindBy(xpath = "//*[contains(@class,'btn') and contains(.,'IS') and contains(.,'Personas')]")
    WebElement IS_Personas_Tab;
    @FindBy(xpath = "//td[contains(. ,'1 row selected')]")
    WebElement oneRowSelectedText;
    String IS_Personas_Table_Row = "//table[@id='T304436181']//tr[contains(normalize-space(.), '%s')]";
    @FindBy(xpath = "//a[contains(normalize-space(.), 'Update Personas')]")
    WebElement updatePersonasButton;
    @FindBy(xpath = "//a[contains(normalize-space(.), 'Add/Modify')]")
    WebElement addModifyPersonasButton;
    @FindBy(xpath = "//a[contains(normalize-space(.), 'Close')]")
    WebElement closePersonasButton;
    @FindBy(xpath = "//*[@arid='304436181']//a[text()='Refresh']")
    WebElement refreshPersonasButton;

    String textAreaField = "//label[normalize-space(text())='%s']/following-sibling::textarea";
    String IS_Application_Licenses_Table_Row = "//table[@id='T304449631']//span[normalize-space()='%s']/ancestor::td";
    String DropDownField = "(//label[normalize-space(.)='%s']/parent::div//a)[last()]";
    String DropDownFieldOption = "//td[contains(@class, 'MenuEntryName') and normalize-space(.)='%s']";

    public CTMPeople verifyPageLoaded() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                        By.xpath(String.format(ElementByARID, ElementARID.CTM_PEOPLE_Title)), 10),
                "Add/Remove Licenses Console page did not load successfully.");
        return this;
    }

    public CTMPeople searchPeopleWithUserName(String userName) {
        commonWaits.clickByText(SolQAConstants.New_search);
        commonWaits.waitForElementVisible(loginAccessTab).click();
        WebElement loginID_Input = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textAreaField, SolQAConstants.Login_ID)));
        loginID_Input.clear();
        loginID_Input.sendKeys(userName);
        commonWaits.clickByText(SolQAConstants.SEARCH);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                        oneRowSelectedText, 10),
                "Search results did not return the expected user with Login ID: " + userName);
        return this;
    }

    public CTMPeople updateISApplicationLicenses(String licenseValue) {
        commonWaits.waitForElementVisible(IS_Application_Licenses_Tab).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        if (!commonWaits.isElementPresent(By.xpath(String.format(IS_Application_Licenses_Table_Row, licenseValue)))) {
            commonWaits.clickByText(SolQAConstants.Update_IS_Application_Licence);
            switchToLastWindow();
            setDropDownFieldValue(SolQAConstants.IS_Application_License, licenseValue);
            commonWaits.clickByText(SolQAConstants.ADD);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickByText(SolQAConstants.CLOSE);
            switchToParentWindow();
        }
        return this;
    }

    public void setDropDownFieldValue(String labelName, String value) {
        try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(this.DropDownField, labelName)), 10), "Dropdown field with label '" + labelName + "' is not visible.");
            this.commonWaits.waitForByElementToBeVisible(By.xpath(String.format(this.DropDownField, labelName))).click();
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(this.DropDownFieldOption, value)), 10), "Dropdown option with value '" + value + "' is not visible for field '" + labelName + "'.");
            this.commonWaits.waitForByElementToBeVisible(By.xpath(String.format(this.DropDownFieldOption, value))).click();
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public CTMPeople updateISPersonas(String applicationName, String personaValue) {
        commonWaits.waitForElementClickable(IS_Personas_Tab, 10).click();
        if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(IS_Personas_Table_Row, personaValue)), 5)) {
            commonWaits.waitForElementClickable(updatePersonasButton, 10).click();
            switchToLastWindow();
            setDropDownFieldValue("Application Name*", applicationName);
            setDropDownFieldValue("IS Persona", personaValue);
            commonWaits.waitForElementClickable(addModifyPersonasButton, 10).click();
            commonWaits.waitForElementClickable(closePersonasButton, 10).click();
            switchToParentWindow();
            commonWaits.waitForElementClickable(refreshPersonasButton, 10).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(IS_Personas_Table_Row, personaValue)), 10), "Persona '" + personaValue + "' was not added successfully for application '" + applicationName + "'.");
        }
        else {
            log.debug("Persona '" + personaValue + "' already exists for application '" + applicationName + "'. No update needed.");
        }
        return this;
    }
}
