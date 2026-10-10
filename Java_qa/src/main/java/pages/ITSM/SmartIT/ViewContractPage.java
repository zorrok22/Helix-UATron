package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ViewContractPage extends BasePage {
	
	static SolqaLogger log;
    CommonWaits commonWaits;
	
	@FindBy(xpath = "//div[@id='304419451']//a[normalize-space()='Apex-Watches.com (AWS)']")
	private WebElement serviceName;
	
	@FindBy(xpath="//*[contains(@class,'cdk-drop-list a-actual-tab')]/li[contains(.,'Assets')]")
	public WebElement assetsTab;
	
	@FindBy(xpath="//*[@id='304442286' ]")
	public WebElement relateExistingAssetButton;
	
	@FindBy(xpath="//*[@id='302258625' ]")
	public WebElement searchCITextbox;
	
	@FindBy(xpath="//*[@id='302259032' ]")
	public WebElement searchIcon;
	
	@FindBy(xpath="//*[@name='ar301527000']")
	public WebElement relationshipTypeButton;
	
	@FindBy(xpath="//*[@id='304253220']")
	public WebElement saveCIButton;
	

	//String locators
		public String menuOptionsViewPage= "//div[@id='304433221']";
	    public String ContractIDValue = "//*[@id='ar260000000_data']";
	    public String IFramePwa = "//iframe[@id='pwa-frame']";
	    public String iFrameStudio= "//iframe[@id='studio-frame']";
	    public String StatusDownArrow= "//button[@id='304424821']";
	    public String statusDropdown = "//*[@id= '7']//button[contains(@id, 'rx-select') and @name='ar7']";
	    public String statusOptionButton = "//*[contains(text(),'%s')]//ancestor::button[contains(@class, 'rx-select')]";
	    public String searchedCIResult = "//*[contains(@class, 'ui-table-tbody ng-tns-c2330473529-3693')]/tr[1]/td[contains(.,'%s')]";
	    public String checkBoxCIResult="//div[@class='ui-chkbox-box ui-widget ui-state-default']";
	    public String relationshipOption = "//*[contains(text(),'%s')]/ancestor::button[contains(@class, 'rx-select__option')]";
	    
	    
	    public ViewContractPage() {
	    	PageFactory.initElements(getDriver(), this);
			log = new SolqaLogger(ViewContractPage.class.getName());
			commonWaits=new CommonWaits();
	    }
	    
	    public String FetchContractID() {
			log.info("Fetching Contract ID");
	        commonWaits.waitForPageFullyLoaded(30,3);
	        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(menuOptionsViewPage), 30),
	                "Status Reason is not visible.");

//	        commonWaits.waitForByElementToBeVisible(By.xpath(menuOptionsViewPage));
	        return getValueFromText(ContractIDValue);
		}
	    
	    public void setStatus(String newStatus) {
	    	commonWaits.waitForPageFullyLoaded(30,3);
	    	switchtoDefaultFrame();
	    	switchToFrame(By.xpath(IFramePwa));
	        commonWaits.waitForByElementToBeVisible(By.xpath(StatusDownArrow));
	    	isElementPresent(By.xpath(StatusDownArrow));
	    	commonWaits.clickWithoutInterception(By.xpath(StatusDownArrow));
	    	commonWaits.waitForByElementToBeVisible(By.xpath(statusDropdown));
	    	commonWaits.waitForPageFullyLoaded(30,3);
	    	commonWaits.clickWithoutInterception(By.xpath(statusDropdown));
	    	commonWaits.clickWithoutInterception(By.xpath(String.format(statusOptionButton, newStatus)));
	    	commonWaits.waitForPageFullyLoaded(30,3);
	    	
	    }
	    
	    public void relateCI(String ciName, String relationshipType) {

			log.info("Relating CI '" + ciName + "' to the Incident");

			commonWaits.waitForPageFullyLoaded(30, 3);
			commonWaits.switchtoDefaultFrame();
			switchToFrame(By.xpath(IFramePwa));
			
			log.info("Clicking Assets Tab");
			commonWaits.waitForElementVisible(assetsTab);
			commonWaits.clickWithoutInterception(assetsTab);
			
			log.info("Clicking relateExistingAsset Button");
			commonWaits.waitForElementVisible(relateExistingAssetButton);
			commonWaits.clickWithoutInterception(relateExistingAssetButton);

			commonWaits.waitForElementVisible(searchCITextbox);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	        typeKeys(searchCITextbox, ciName);
	        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	        commonWaits.waitForElementVisible(searchIcon).click();
			
	        commonWaits.waitForByElementToBeVisible(By.xpath(checkBoxCIResult));
	        commonWaits.clickWithoutInterception(By.xpath(checkBoxCIResult));
	        
	        log.info("Selecting relationship type");
	        commonWaits.waitForElementVisible(relationshipTypeButton).click();
	        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(relationshipOption, relationshipType)));
	        commonWaits.clickWithoutInterception(By.xpath(String.format(relationshipOption, relationshipType)));
	        
	        commonWaits.waitForPageFullyLoaded(30,3);
	        
	        log.info("Clicking save button");
	        commonWaits.waitForElementVisible(saveCIButton).click();
	        
	        commonWaits.waitForPageFullyLoaded(30,3);
			
			log.info("Successfully related CI '" + ciName + "' to the Incident.");
			
		}

}
