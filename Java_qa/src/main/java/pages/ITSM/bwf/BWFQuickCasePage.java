package pages.ITSM.bwf;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class BWFQuickCasePage extends BasePage {

	static SolqaLogger log;
	CommonWaits commonWaits;

	// String locators
	String userInDropdown = "//p[contains(text(),'%s')]";
	String templateOption = "//*[contains(text(),'%s')]";
	
	// WebElements Locators
	@FindBy(xpath = "//a//span[contains(text(),'Quick Case')]")
	WebElement quickCaseTab;

	@FindBy(xpath = "//div[contains(@class,'sr-input')]//div[@contenteditable='true']")
	WebElement quickCaseSearchField;

	@FindBy(xpath = "//button//span[contains(text(),'Create Case')]")
	WebElement createCaseButton;

	@FindBy(xpath = "//div[contains(@class,'toast-message') or contains(@class,'notification')]//span[contains(@id,'case') or contains(@id,'Case')]")
	WebElement createdCaseIDElement;

	@FindBy(xpath = "//div[@class='bwf-info-bar']//div[contains(text(),'CASE')]")
	WebElement caseIDField;

    @FindBy(xpath = "//button[@aria-label='Close']")
    WebElement closeCaseButton;
    
	public BWFQuickCasePage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(BWFQuickCasePage.class.getName());
		commonWaits = new CommonWaits();
	}

	/**
	 * Click on Quick Case tab
	 */
	public BWFQuickCasePage clickQuickCaseTab() {
		log.info("Clicking on Quick Case tab");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		
		try {
			commonWaits.waitForElementVisible_withFluentWait(getDriver(), quickCaseTab, 30);
			commonWaits.clickByJS(quickCaseTab);
			log.info("Clicked on Quick Case tab");
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
			commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
			commonWaits.waitForPageReadyState();
		} catch (Exception e) {
			log.error("Failed to click Quick Case tab: " + e.getMessage());
			Assert.fail("Unable to click Quick Case tab");
		}
		return this;
	}

	/**
	 * Type customer name and select from dropdown in Quick Case
	 * 
	 * @param customerName - Customer name to search (e.g., @swaugh)
	 */
    String SuggestionOption = "//bwf-selectable-list-item[contains(normalize-space(.), '%s')]";
	public BWFQuickCasePage typeAndSelectCustomerInQuickCase(String customerName) {
		log.info("Typing and selecting customer: " + customerName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		
		try {
			// Type customer name
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), quickCaseSearchField, 30), "Quick Case search field is not visible");
			quickCaseSearchField.clear();
			quickCaseSearchField.sendKeys("@"+customerName);
			log.info("Typed customer: " + customerName);
			
			// Select from dropdown
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                            By.xpath(String.format(SuggestionOption, customerName)), 5),
                    "Customer suggestion option did not appear in time");
            commonWaits.clickWithoutInterception(By.xpath(String.format(SuggestionOption, customerName)));
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		} catch (Exception e) {
			log.error("Failed to select customer: " + e.getMessage());
			Assert.fail("Unable to select customer in Quick Case");
		}
		return this;
	}

	/**
	 * Type text in Quick Case search field
	 * 
	 * @param searchText - Text to search (e.g., !move)
	 */
	public BWFQuickCasePage typeInQuickCaseSearch(String searchText) {
		log.info("Typing in Quick Case search: " + searchText);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		
		try {
			commonWaits.waitForElementVisible(quickCaseSearchField);
			quickCaseSearchField.sendKeys(searchText);
			log.info("Typed search text: " + searchText);
			
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		} catch (Exception e) {
			log.error("Failed to type in Quick Case search: " + e.getMessage());
			Assert.fail("Unable to type in Quick Case search");
		}
		return this;
	}

	/**
	 * Select template from Quick Case dropdown
	 * 
	 * @param templateName - Template name to select
	 */
	public BWFQuickCasePage selectTemplateFromQuickCase(String templateName) {
		log.info("Selecting template: " + templateName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                            By.xpath(String.format(SuggestionOption, templateName)), 5),
                    "Template option did not appear in time");
			WebElement template = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SuggestionOption, templateName)));
			commonWaits.clickWithoutInterception(template);
			log.info("Selected template: " + templateName);
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		} catch (Exception e) {
			log.error("Failed to select template: " + e.getMessage());
			Assert.fail("Unable to select template: " + templateName);
		}
		return this;
	}

	/**
	 * Verify template search is successful
	 * 
	 * @param templateName - Template name to verify
	 * @return true if template is visible
	 */
	public boolean isTemplateSearchSuccessful(String templateName) {
		log.info("Verifying template search successful for: " + templateName);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		
		try {
			findElement(By.xpath(String.format(templateOption, templateName)));
			log.info("Template found: " + templateName);
			return true;
		} catch (Exception e) {
			log.error("Template not found: " + templateName);
			return false;
		}
	}

	/**
	 * Click Create Case button
	 */
	public BWFQuickCasePage clickCreateCaseButton() {
		log.info("Clicking Create Case button");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		
		try {
			commonWaits.waitForElementVisible(createCaseButton);
			commonWaits.clickByJS(createCaseButton);
			log.info("Clicked Create Case button");
			
			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
			
		} catch (Exception e) {
			log.error("Failed to click Create Case button: " + e.getMessage());
			Assert.fail("Unable to click Create Case button");
		}
		return this;
	}

	/**
	 * Get the created case ID
	 * 
	 * @return Case ID as String
	 */
	public String getCreatedCaseID() {
		log.info("Getting created case ID");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
		
		try {
			// Try to get from toast notification first
			try {
				commonWaits.waitForElementVisible(caseIDField, 5);
				String caseID = caseIDField.getText().trim();
				// Extract case ID from message like "Case CAS000000000001 created"
				if (caseID.contains("CAS")) {
					log.info("Case ID is: " + caseID);
					return caseID;
				}
			} catch (Exception e) {
				log.info("Case ID field not found");
			}
			
			log.error("Unable to retrieve case ID");
			return "";
			
		} catch (Exception e) {
			log.error("Failed to get case ID: " + e.getMessage());
			return "";
		}
	}

    public BWFQuickCasePage clickCloseCaseButton() {
        log.info("Clicking Close Case button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        
        try {
            commonWaits.waitForElementVisible(closeCaseButton);
            commonWaits.clickByJS(closeCaseButton);
            log.info("Clicked Close Case button");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        catch (Exception e) {
            log.error("Failed to click Close Case button: " + e.getMessage());
            Assert.fail("Unable to click Close Case button");
        }
        return this;
    }
}

