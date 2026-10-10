package pages.ITSM.SmartIT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CreateContractPage extends BasePage {
	
	static SolqaLogger log;
    CommonWaits commonWaits;
    
    //WebElements Locators
    
    @FindBy (xpath ="//iframe[@id='pwa-frame']")
	WebElement IFrame;
    
    @FindBy (xpath = "//*[contains(@class, 'rx-typeahead-')]/button[@data-testid='ar260100006_item-0']")
    WebElement firstOptionCostCenter;

    //String locators
    /** Term (General): open the adapt-rx-select combobox. */
    public String termRxSelectCombo =
            "//*[normalize-space(.)='Term' or normalize-space(.)='Term required']/ancestor::adapt-rx-select[1]//button[@role='combobox']";
    /** Term option after open; {@code String.format(..., value.toLowerCase(Locale.ROOT))}. */
    public String termRxSelectOptionByText = "//button[@role='option' and .//span[text()='%s']]";
    public String ElementXpathByID= "//*[@id='%s']";
    public String typeheadListElementText =
            "//*[@role='listbox']//*[contains(normalize-space(.),'%s')]";
    public String ElementXpathByAriaLabel= "//*[@aria-label= '%s']";
    public String ElementDropdownXpathByID = "//*[text()='%s']/ancestor::adapt-rx-select";
    public String dropDropdownCreatePage= "//*[contains(@class, 'rx-select__option-content')]/parent::button[contains(. ,'%s')]";
    
	String contractType= "Contract type";
	String summaryButton= "8";
	String contractIDTextBox= "260000000";
	String companyButton= "ar1000000001";
	String supplierNameBox= "ar1000000396";
	String costValueBox="ar260100006";
	String supportGroupBox= "ar260142102";
	String saveContractBox= "304440891";
	String notificationBox="240001015";
	String statusBox= "Status";
	
	
    public CreateContractPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(CreateContractPage.class.getName());
		commonWaits=new CommonWaits();
	}
    
	public void FillContractTemplate(Map<Object, String> smartITTestData) {
		log.info("Filling Contract Details");
		switchtoDefaultFrame();
		switchToFrame(IFrame);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ElementXpathByID,"an_z2PL_SV_ContractType")), 30),
                "Contract Type is not visible.");
		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID,"an_z2PL_SV_ContractType")));
		
		if (smartITTestData.get("Contract Type") != null) {
			log.info("Entering Contract Type");
			setContractType(smartITTestData.get("Contract Type").toString());
		}
		
		if (smartITTestData.get("Contract Title") != null) {
			log.info("Entering Summary");
			setSummary(smartITTestData.get("Contract Title").toString());
		}

		if (smartITTestData.get(SolQAConstants.STATUS) != null) {
			log.info("Entering Status");
			setStatus(smartITTestData.get(SolQAConstants.STATUS).toString());
		}

		if (smartITTestData.get("Company") != null) {
			log.info("Entering Company");
			setCompany(smartITTestData.get("Company").toString());
		}
		
		if (smartITTestData.get("Supplier Name") != null) {
			log.info("Entering Supplier Name");
			setSupplierName(smartITTestData.get("Supplier Name").toString());
		}

		if (smartITTestData.get("Term") != null) {
			log.info("Entering Term");
			setTerm(smartITTestData.get("Term").toString());
		}
		
		if (smartITTestData.get("Support Group") != null) {
			log.info("Entering Support Group");
			setSupportGroup(smartITTestData.get("Support Group").toString());
		}
		
		this.setContractID();
		
	}
    
    	public void setContractType(String contractTypeValue) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(ElementXpathByAriaLabel, contractType), 30),
                    "Contract Type is not visible.");

    		keyClearAndType(String.format(ElementXpathByAriaLabel, contractType), contractTypeValue);
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(typeheadListElementText, contractTypeValue)), 30),
                    "contractTypeValue is not visible.");
    		commonWaits.waitForSometime(4000);
    		commonWaits.clickWithoutInterception(By.xpath(String.format(typeheadListElementText, contractTypeValue)));
    		commonWaits.waitForPageFullyLoaded(30, 1);
    	}
    	
    	
    	public void setSummary(String summary) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, summaryButton)));
    		keyClearAndType(String.format(ElementXpathByID, summaryButton), summary);
    		commonWaits.waitForPageFullyLoaded(30, 3);	
    	}
    	
    	
    	public void setStatus(String status) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		click(String.format(ElementDropdownXpathByID, statusBox));
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDropdownCreatePage, status)));
    		commonWaits.clickWithoutInterception(By.xpath(String.format(dropDropdownCreatePage, status)));
    		commonWaits.waitForPageFullyLoaded(30, 3);	
    	}
    	
    	public String setContractID() {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    	    String contractID = RandomStringUtils.randomNumeric(6);

    	    switchtoDefaultFrame();
    	    switchToFrame(IFrame);

    	    By contractIdLocator = By.xpath(String.format(ElementXpathByID, contractIDTextBox));

    	    commonWaits.waitForByElementToBeVisible(contractIdLocator);
    	    keyClearAndType(String.format(ElementXpathByID, contractIDTextBox), contractID);
    	    findElement(contractIdLocator).sendKeys(Keys.TAB);
    	    commonWaits.waitForPageFullyLoaded(30, 3);

    	    String enteredValue = findElement(contractIdLocator).getAttribute("value");

    	    if (!contractID.equals(enteredValue)) {
    	        log.error("Mismatch: Expected Contract ID: " + contractID + ", but found: " + enteredValue);
    	        throw new AssertionError("Contract ID value was not set correctly in the field.");
    	    }
    	    
    	    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

    	    log.info("Generated and verified Contract ID: " + contractID);
    	    return contractID;
    	}


    	
    	public void setCompany(String company) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		keyClearAndType(String.format(ElementXpathByID, companyButton), company);
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ElementXpathByAriaLabel, company)) , 30 ), "Option Value is not visible");
			commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByAriaLabel, company)));
    		commonWaits.waitForPageFullyLoaded(30, 3);	
    		
    	}
   
    	public void setSupplierName(String supplierName) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, supplierNameBox)));
    		keyClearAndType(String.format(ElementXpathByID, supplierNameBox), supplierName);
        By optionBy = By.xpath(String.format(typeheadListElementText, supplierName));
        commonWaits.waitForByElementToBeVisible(optionBy);
        commonWaits.clickWithoutInterception(optionBy);
    		commonWaits.waitForPageFullyLoaded(30, 3);	
    	}
    	
    	public void setTerm(String value) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), termRxSelectCombo, 30),
    				"Term option '" + value + "' is not visible in the dropdown list");
    		commonWaits.clickWithoutInterception(By.xpath(termRxSelectCombo));
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    		By option = By.xpath(String.format(termRxSelectOptionByText, value.toLowerCase(Locale.ROOT)));
    		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), option, 30),
    				"Term option '" + value + "' is not visible in the dropdown list");
    		commonWaits.clickWithoutInterception(option);
    		commonWaits.waitForPageFullyLoaded(30, 3);
    	}

    	public void setCostCenter() {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, costValueBox)));
    		commonWaits.clickWithoutInterception(By.xpath(String.format(ElementXpathByID, costValueBox)));
    		commonWaits.waitForElementVisible(firstOptionCostCenter);
    		commonWaits.clickWithoutInterception(firstOptionCostCenter);
    	}
    	
    	public void setSupportGroup(String value) {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, supportGroupBox)));
    		keyClearAndType(String.format(ElementXpathByID, supportGroupBox), value);
	        By optionBy = By.xpath(String.format(typeheadListElementText, value));
	        commonWaits.waitForByElementToBeVisible(optionBy);
	        commonWaits.clickWithoutInterception(optionBy);
    	}
    	
    	public void saveContract() {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		log.info("Clicking on Save Button on Contract"); 
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, saveContractBox)));
    		click(By.xpath(String.format(ElementXpathByID, saveContractBox)));
    		commonWaits.waitForPageFullyLoaded(30, 3);	
    		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    	
	}
    	
    	public void updateNotificationDate() {
    		switchtoDefaultFrame();
    		switchToFrame(IFrame);
    		
    		log.info("Updating Notification Date: today's date"); 
    		commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ElementXpathByID, notificationBox)));
    		
    	    // Format: M/d/yyyy hh:mm:ss a → e.g., 7/9/2025 12:00:00 AM
    	    SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");

    	    Calendar calendar = Calendar.getInstance();
    	    calendar.set(Calendar.HOUR, 0);
    	    calendar.set(Calendar.MINUTE, 0);
    	    calendar.set(Calendar.SECOND, 0);
    	    calendar.set(Calendar.AM_PM, Calendar.AM);

    	    String todayDate = formatter.format(calendar.getTime());

    	    // Assuming the field label for notification date is "Notification Date"
    	    commonWaits.waitForByElementToBeVisible(
    	        By.xpath(String.format(ElementXpathByID, notificationBox))
    	    ).sendKeys(todayDate);
    	}


}
