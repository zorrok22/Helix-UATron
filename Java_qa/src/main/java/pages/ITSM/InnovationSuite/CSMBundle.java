package pages.ITSM.InnovationSuite;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CSMBundle extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    String btnLocator = "//button[contains(@data-testid,'%s')]";
    String filterHeader_selectFilterValue = "//div[text()='%s']//ancestor::button";
    String fvalue = "//parent::div//following-sibling::div[contains(@class,'tab')]//*[contains(text(),'%s')]";
    String selectTextFilter_loc = "//parent::div//following-sibling::div[contains(@class,'tab')]//input";
    String iFrame = "//iframe[@id='pwa-frame']";
    String fieldValueFromViewPage = "//*[@id='ar%s_data']";
    String buttonClassLocator = "//button[contains(@class,'%s')]";
    String variableRows = "//div[contains(@class,'ui-table')]//*[@data-testid='adapt-table_column-%s_row-0']";
    public String columnNameUnhecked = "//label[contains(.,'%s')]//input[@aria-checked='false']//..//div";
    String studioFrame = "//iframe[@id='studio-frame']";
    
    //Locators
    String menuFilterButton = "adapt-af-2_menu";
    String clearFilterButton = "adapt-af-2_Clear";
    String applyFilterButton = "adapt-af-2_footer_apply";
    String removeFilterButton = "remove-btn";
    String dropDownButton = "dropdown-kabob-btn dropdown";

    @FindBy(xpath = "//div//p[@id='error-message']")
	WebElement loginFailedText;
    @FindBy(xpath = "//span[contains(normalize-space(),'Customer Service Management')]")
    WebElement applicationNameLabel;
    @FindBy(xpath = "//div[contains(@class,'ui-table')]//th[contains(@data-testid,'field-id')]")
    WebElement headerValues;
   
    public CSMBundle() {
        PageFactory.initElements(getDriver(), this);                
        log = new SolqaLogger(CSMBundle.class.getName());
        commonWaits = new CommonWaits();
    }

    public boolean verifyCSMLoginFailed() {
        log.info("Verifying CSM login failed.");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(loginFailedText).isDisplayed();
    }

    public CSMBundle verifyCSMLoginSuccess() {
        log.info("Verifying CSM login success.");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        verifyTitle(SolQAConstants.CSMPAGE_TITLE);
        return this;
    }

}
    