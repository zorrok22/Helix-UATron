package pages.ITSM.ITSM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolqaLogger;

public class Report extends BasePage{

    CommonWaits commonWaits;
    SolqaLogger log;
    CommonMethods commonMethods;

    //String Locators
    String reportLabel = "//label[.='report-label']";
    String newReportLabel = "//td[contains(.,'New')]";
    String menuItemLocator = "//td[contains(@class,'MenuEntry') and contains(.,'%s')]";
    String formInoutLocator = "//textarea[contains(@armenu,'FormsUsedInReportsMenu')]";
    String formSearchedOption = "//tr[contains(@class,'AutoCompleteNonSelection')]//td[contains(.,'%s')]";
    String disabledOKButtonLocator = "//a[contains(@class,'btnd') and contains(.,'OK')]";

    //WebElements Locators
    @FindBy(xpath = "//*[contains(@title,'New')]//ancestor::a")
    WebElement newReportButton;

    @FindBy(xpath = "//*[contains(@alt,'Menu for Type')]//ancestor::a")
    WebElement typeMenuButton;
    
    @FindBy(xpath = "//*[contains(@alt,'Menu for Type')]//ancestor::a")
    WebElement formMenuButton;

    @FindBy(xpath = "//a[contains(.,'Helix Dashboard')]")
    WebElement helixDashboardLink;
    
    public Report() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(Report.class.getName());
        commonWaits=new CommonWaits();
        commonMethods = new CommonMethods();
    }

    public void clickOnNewReportButton() {
		log.info("Clicking on New Report button");
		commonWaits.waitForElementPresent(reportLabel);
		clickWithoutInterception(newReportButton);
	}

    public void selectTypeMenu(String menuItem) {
		log.info("Selecting Type Menu");
		commonWaits.waitForElementPresent(newReportLabel);
		commonWaits.waitForElementVisible(typeMenuButton);
		clickWithoutInterception(typeMenuButton);
		By menuItemBy = By.xpath(String.format(menuItemLocator, menuItem));
		commonWaits.waitForByElementToBeVisible(menuItemBy);
		clickWithoutInterception(menuItemBy);
	}
    
    public void selectFormMenu(String menuItem) {
		log.info("Selecting Form Menu");
		keyClearAndType(formInoutLocator, menuItem);
		clickWithoutInterception(By.xpath(String.format(formSearchedOption, menuItem)));
	}
    
    public void verifyDisabledOKButton() {
    	Assert.assertTrue(isElementPresent(By.xpath(disabledOKButtonLocator)), "OK button is not disabled");
    }
    
    public void clickOnHelixDashboardLink() {
		log.info("Clicking on Helix Dashboard link");
		clickWithoutInterception(helixDashboardLink);
		commonWaits.switchToLastWindow();
    }

}
