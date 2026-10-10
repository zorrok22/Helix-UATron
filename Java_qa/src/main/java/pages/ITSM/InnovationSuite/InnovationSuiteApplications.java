package pages.ITSM.InnovationSuite;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class InnovationSuiteApplications extends BasePage{

    CommonWaits commonWaits;
    SolqaLogger log;
    CommonMethods commonMethods;


    //String locators
    String configurationLocator = "//div[contains(.,'%s') and contains(@aria-expanded,'false')]";
    String subMenuConfigurationLocator = "//div[contains(@aria-label,'%s')]";
    String enabledString = "//span[contains(.,'Enabled')]";
    String processDefXpath = "//a[normalize-space()='%s']";
    String processNameXpath = "//div[contains(@data-name,'properties')]//label[contains(.,'%s')]//following-sibling::div//input";
  	String recordDefNameXpath = "//div[contains(.,'%s') and contains(@data-field,'rxData/name')]//input";
  	String linkContainsTextLocator = "//a[contains(.,'%s')]";
  	String adaptTextFieldXpath = "//adapt-rx-textfield[contains(.,'%s')]//input";
  	String recordTypeXpath = "//button[contains(.,'%s')]";

    //WebElements Locators
    @FindBy(xpath = "//span[contains(.,'Settings')]//ancestor::a")
    WebElement settingsButton;

    @FindBy(xpath = "//span[contains(.,'Enable')]//ancestor::button[not(@disabled)]")
    WebElement enableButton;

    @FindBy(xpath = "//span[contains(.,'Enable')]//ancestor::button[(@disabled)]")
    WebElement enableButtonDisabled;
    
    @FindBy(xpath = "//span[contains(.,'Disable')]//ancestor::button")
    WebElement disableButton;
    
    @FindBy(xpath = "//rx-text-form-control[@rx-id='discovery-url']//input[@type='text']")
    WebElement discoveryUrlTextbox;
    
    @FindBy(xpath = "//button[contains(@class,'a-hamburger')]//span[contains(.,'Main menu')]/..")
    WebElement hamburgerMenu;
    
    @FindBy(xpath = "//a[contains(@class,'a-hamburger__menu-link') and contains(@class,'a-dropdown__submenu-handler') and contains(.,'Console')]")
    WebElement hamburgerConsoleMenu;
    
    @FindBy(xpath = "//a[contains(@class,'a-hamburger__menu-link') and contains(@href,'Asset%20Console%20SM')]")
    WebElement hamburgerAssetConsoleLink;
    
    @FindBy(xpath = "//span[text()='Console']/ancestor::button")
    WebElement navigateToConsole;
    
    @FindBy(xpath = "//a[contains(@href,'Asset%20Console%20SM') and .//span[normalize-space()='Asset Console']]")
    WebElement navigateToAssetConsole;
    
    @FindBy(xpath = "//a[.//span[normalize-space()='Asset Console']]")
    WebElement AssetConsole;
    
    @FindBy(xpath = "//button[contains(@class, 'advanced-filter__dropdown-anchor')]")
    WebElement filterBtn;
    
    @FindBy(xpath = "//button[contains(. ,'Remove all')]")
    WebElement removeAllFiltersBtn;
    
    @FindBy(xpath = "//button[contains(. ,'Apply filters')]")
    WebElement applyFiltersBtn;
    
    @FindBy(xpath = "//input[@placeholder='Search']")
    WebElement searchBar;    
  
    @FindBy(xpath = "//span[text()='IsEnabled']//ancestor::div[@class='ng-star-inserted']//button[@aria-label='True']")
    WebElement featureEnableButton;
    
    @FindBy(xpath = "//button[normalize-space(.)='General']")
    WebElement generalDropdown;

    @FindBy(xpath = "//button[@disabled='disabled' and normalize-space()='Enable']")
    WebElement DisableAdvanceResponsiveNavigationButton;
    @FindBy(xpath = "//button[normalize-space(.)='Save']")
	WebElement saveHPMRecordButton;
    @FindBy(xpath = "//button[normalize-space(.)='Create']")
	WebElement createHPMRecordButton;
    @FindBy(xpath = "//a[contains(@class,'back')]")
  	WebElement backButton;
  	@FindBy(xpath = "//button[contains(.,'OK')]")
  	WebElement okButton;
  	@FindBy(xpath = "//div[contains(@class,'joint-paper joint-theme-rx')]")
  	WebElement processCanvas;
  	@FindBy(xpath = "//div[@id='v-315']")
  	WebElement createRecordActivityXpath;
  	@FindBy(xpath = "//iframe")
  	WebElement processDefinitionFrame;
  	@FindBy(xpath = "//a[contains(@class,'process-property')]")
  	public WebElement processNameField;
  	@FindBy(xpath = "//a[contains(@class,'dropdown__trigger')]")
  	public WebElement processDefNameXpath;
  	@FindBy(xpath = "//a[contains(@class,'element-property')]")
  	public WebElement elementPropertyField;
  	@FindBy(xpath = "//button[contains(.,'Processes') and @role]")
  	public WebElement processDefinitionTab;
  	@FindBy(xpath = "//adapt-tab-panel[contains(@class,'active')]//button[@rx-id='new-button' and contains(@class,'btn')]")
  	public WebElement createProcessDefButtonXpath;
  	@FindBy(xpath = "//a[contains(@class,'arrow_left')]")
  	public WebElement arrowLeftButton;
  	@FindBy(xpath = "//button[contains(.,'Records') and @role]")
  	WebElement recordDefinitionTab;
  	@FindBy(xpath = "//button[@rx-id='new-button' and contains(@class,'btn')]")
  	WebElement createRecordDefButtonXpath;
  	@FindBy(xpath = "//button[contains(.,'Close')]")
  	WebElement closeRecordDefDialogButton;
  	@FindBy(xpath = "//p[contains(.,'Application created successfully')]")
  	WebElement applicationCreatedSuccessMessage;
  	@FindBy(xpath = "//button[contains(.,'Application') and contains(@class,'dropdown-item')]")
  	WebElement createApplicationXpath;
    
    @FindBy(xpath = "//span[text()='IsEnabled']//ancestor::div[@class='ng-star-inserted']//button[@aria-label='False']")
    WebElement featureDisabledButton;
    
    String filterHeader_selectFilterValue = "//div[contains(.,'%s') and contains(@class,'advanced-filter__label')]//parent::div";
    String fvalue = "//span[contains(@class,'rx-select__option-label') and .='%s']";
    String selectTextFilter_loc = "//input[@type='text']";

    public InnovationSuiteApplications() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(InnovationSuiteApplications.class.getName());
        commonWaits=new CommonWaits();
        commonMethods = new CommonMethods();
    }

    public InnovationSuiteApplications clickOnSettings() {
        log.info("Clicking on Settings button");
		commonWaits.switchToLastWindow();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        clickWithoutInterception(settingsButton);
        return this;
    }

    public InnovationSuiteApplications clickOnConfiguration(String configurationName) {
        log.info("Expanding Configuration: " + configurationName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        if(isElementPresent(By.xpath(String.format(configurationLocator, configurationName)))) {
            clickWithoutInterception(By.xpath(String.format(configurationLocator, configurationName)));
        }
        return this;
    }

    public InnovationSuiteApplications selectConfiguration(String configurationName) {
        log.info("Expanding Configuration: " + configurationName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        clickWithoutInterception(By.xpath(String.format(subMenuConfigurationLocator, configurationName)));
        return this;
    }

    public InnovationSuiteApplications clickOnEnableButton() {
        log.info("Checking if configuration is already enabled");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        

        if(commonWaits.isElementPresent(disableButton) && disableButton.isEnabled()) {
            log.info("Configuration is already enabled (Disable button is visible). Skipping enable action.");
            return this;
        }
        
        if(commonWaits.isElementPresent(enableButton)) {
            log.info("Enable button is visible and not disabled. Clicking on Enable button.");
            clickWithoutInterception(enableButton);
            commonWaits.waitForElementPresent(By.xpath(enabledString));
            log.info("Configuration enabled successfully");
        } else {
            log.info("Enable button is not available or is disabled. No action taken.");
        }
        return this;
    }
    
    public InnovationSuiteApplications setDiscoveryUrl(String discoveryUrl) {
        log.info("Setting Discovery URL: " + discoveryUrl);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);

        // Check if Discovery URL field is already visible
        if (commonWaits.isElementPresent(discoveryUrlTextbox)) {
            keyClearAndType(discoveryUrlTextbox, discoveryUrl);
            log.info("Discovery URL set successfully (already visible)");

        } else {
            log.info("Discovery URL not visible, trying to expand General section");

            // Expand General only if it's collapsed
            try {
                String expanded = generalDropdown.getAttribute("aria-expanded");

                if (expanded == null || expanded.equalsIgnoreCase("false")) {
                    clickWithoutInterception(generalDropdown);
                    log.info("Clicked on General dropdown");
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
                } else {
                    log.info("General section already expanded");
                }

            } catch (Exception e) {
            	String message = "Discovery URL textbox still not found after expanding General";
                log.error(message);
                Assert.fail(message);
            }

            // Try again after expanding
            if (commonWaits.isElementPresent(discoveryUrlTextbox)) {
                keyClearAndType(discoveryUrlTextbox, discoveryUrl);
                log.info("Discovery URL set successfully after expanding General");
            } else {
                log.warn("Discovery URL textbox still not found after expanding General");
            }
        }

        return this;
    }
    
    public InnovationSuiteApplications navigateToAssetConsole() {
        switchtoDefaultFrame();
        log.info("Navigating to Asset Console from Innovation Suite");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
 
        if(commonWaits.isElementPresent(hamburgerMenu)) {
            log.info("Hamburger menu is present, using hamburger navigation");
            clickWithoutInterception(hamburgerMenu);
            commonWaits.waitForSometime(commonWaits.minDelay);
            
            if(commonWaits.isElementPresent(hamburgerConsoleMenu)) {
                log.info("Clicking on Console in hamburger menu");
                clickWithoutInterception(hamburgerConsoleMenu);
                commonWaits.waitForSometime(commonWaits.minDelay);
            }
            
            if(commonWaits.isElementPresent(hamburgerAssetConsoleLink)) {
                log.info("Clicking on Asset Console link from hamburger menu");
                clickWithoutInterception(hamburgerAssetConsoleLink);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
                commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
                log.info("Successfully navigated to Asset Console via hamburger menu");
            } else {
                log.warn("Asset Console link not found in hamburger menu");
            }
        } 
            
            else{
            	
            	log.info("Hamburger menu not present, using standard navigation");
            	
            if(commonWaits.isElementPresent(navigateToConsole)) {
                log.info("Clicking on Console button");
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
                clickWithoutInterception(navigateToConsole);
                commonWaits.waitForSometime(commonWaits.minDelay);
                log.info("Clicking on Asset Console link");
                clickWithoutInterception(AssetConsole);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
                commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
                log.info("Successfully navigated to Asset Console");
                
            }
            
            if(commonWaits.isElementPresent(navigateToAssetConsole)) {
                log.info("Clicking on Asset Console link");
                clickWithoutInterception(navigateToAssetConsole);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
                commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
                log.info("Successfully navigated to Asset Console");
            } else {
                log.warn("Asset Console link not found");
            }
        }
    
        return this;
    }

    public InnovationSuiteApplications clickOnDisableButton() {
        log.info("Checking if configuration is already enabled");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        

        if(commonWaits.isElementPresent(DisableAdvanceResponsiveNavigationButton)) {
            log.info("Advanced Responsive Navigation is already enabled. Disabling it now.");
            clickWithoutInterception(disableButton);
            log.info("Advanced Responsive Navigation disabled successfully");
        } else {
            log.info("Advanced Responsive Navigation is already disabled. No action taken.");
        }
        return this;
    }
    
 // Methods for DRSQA-13334: Create Application, Record Definition, and Process Definition
	
  	public InnovationSuiteApplications navigateToCreateNewApplication() {
  		log.info("Navigating to create new application");
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(createRecordDefButtonXpath);
  		clickWithoutInterception(createApplicationXpath);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return this;
  	}
  	
  	public InnovationSuiteApplications fillApplicationDetails(String displayName, String shortName, String groupId) {
  		log.info("Filling application details - Display Name: " + displayName + ", Short Name: " + shortName + ", Group ID: " + groupId);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		
  		// Fill Application Display Name
  		keyClearAndType(String.format(adaptTextFieldXpath, SolQAConstants.APPLICATION_DISPLAY_NAME), displayName);
  		
  		// Fill Application Short Name
  		keyClearAndType(String.format(adaptTextFieldXpath, SolQAConstants.APPLICATION_SHORT_NAME), shortName);
  		
  		// Fill Group ID
  		keyClearAndType(String.format(adaptTextFieldXpath, SolQAConstants.GROUP_ID), groupId);
  		
  		commonWaits.waitForExecutionComplete();
  		log.info("Clicking Create Application button");
  		clickWithoutInterception(createHPMRecordButton);
  		return this;
  	}
  	
  	public InnovationSuiteApplications verifyApplicationCreated(String applicationDisplayName) {
  		log.info("Verifying application created: " + applicationDisplayName);
  		Assert.assertTrue(commonWaits.isElementPresent(applicationCreatedSuccessMessage), "Application creation success message not displayed");
  		return this;
  	}
  	
  	public InnovationSuiteApplications closeNewApplicationDialog() {
  		log.info("Closing new application dialog");
  		clickWithoutInterception(closeRecordDefDialogButton);
  		return this;
  	}
  	
  	public InnovationSuiteApplications navigateToCreateNewRecordDefinition() {
  		log.info("Navigating to create new record definition");
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(recordDefinitionTab);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(createRecordDefButtonXpath);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return this;
  	}
  	
  	public InnovationSuiteApplications selectRecordDefinitionType(String recordType) {
  		log.info("Selecting record definition type: " + recordType);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(By.xpath(String.format(recordTypeXpath, recordType)));
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return this;
  	}
  	
  	public InnovationSuiteApplications fillRecordDefinitionName(String recordDefinitionName) {
  		commonWaits.switchToFrame(processDefinitionFrame);
  		log.info("Filling record definition name: " + recordDefinitionName);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		keyClearAndType(String.format(recordDefNameXpath, SolQAConstants.name), recordDefinitionName);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(saveHPMRecordButton);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		if(saveHPMRecordButton.isEnabled()) {
  			clickWithoutInterception(saveHPMRecordButton);
  			commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		}
  		clickWithoutInterception(arrowLeftButton);
  		commonWaits.switchtoDefaultFrame();
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return this;
  	}
  	
  	public boolean verifyRecordDefinitionCreated(String recordDefinitionName) {
  		log.info("Verifying record definition created: " + recordDefinitionName);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return commonWaits.isElementPresent(By.xpath(String.format(processDefXpath, recordDefinitionName)));
  	}
  	
  	public InnovationSuiteApplications navigateToCreateNewProcessDefinition() {
  		log.info("Navigating to create new process definition");
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(processDefinitionTab);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(createProcessDefButtonXpath);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return this;
  	}
  	
  	public InnovationSuiteApplications selectInputMap(String processDefinitionName) {
  		log.info("Filling process definition name: " + processDefinitionName);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(elementPropertyField);
  		clickWithoutInterception(processDefNameXpath);
  		clickWithoutInterception(By.xpath(String.format(linkContainsTextLocator, processDefinitionName)));
  		return this;
  	}
  	
  	public InnovationSuiteApplications enterProcessName(String name) {
  		log.info("Entering process definition name: " + name);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(processNameField);
  		keyClearAndType(String.format(processNameXpath, SolQAConstants.name), name);
  		return this;
  	}
  	
  	public InnovationSuiteApplications dragAndDropCreateRecordActivity() {
  		log.info("Dragging and dropping Create Record activity to process canvas");
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		Actions actions = new Actions(getDriver());
  		try {
  			switchtoDefaultFrame();
  			switchToFrame(processDefinitionFrame);
  			actions.clickAndHold(createRecordActivityXpath).pause(Duration.ofMillis(1000)).moveToElement(processCanvas)
              .pause(Duration.ofMillis(1000))
              .release().click()
              .perform();

  	        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  	        log.info("Create Record activity dragged and dropped to center successfully");

  		} catch (Exception e) {
  			log.error("Failed to drag and drop Create Record activity: " + e.getMessage());
  		}
  		return this;
  	}
  	
  	public InnovationSuiteApplications saveProcessDefinition() {
  		log.info("Saving process definition");
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(saveHPMRecordButton);
  		clickWithoutInterception(okButton);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		clickWithoutInterception(backButton);
  		commonWaits.waitForExecutionComplete();
  		switchtoDefaultFrame();
  		return this;
  	}

  	public boolean verifyProcessDefinitionCreated(String processDefinitionName) {
  		log.info("Verifying process definition created: " + processDefinitionName);
  		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
  		return commonWaits.isElementPresent(By.xpath(String.format(processDefXpath, processDefinitionName)));
  	}

	public InnovationSuiteApplications enabledOrDisabledFeatureDescription(String input) {
		// TODO Auto-generated method stub
		Boolean enabled=false;
		Boolean disabled=false;
		 commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
	        if(input.equalsIgnoreCase(SolQAConstants.ENABLED))
			{
				String attributeValue = commonWaits.waitForElementVisible(featureEnableButton).getAttribute("class");

				if (attributeValue.contains("btn-primary")) {
					log.info("Feature Description is already enabled (Disable button is visible). Skipping enable action.");
					enabled=true;

				} else {
					log.info("Enable button is visible and not disabled. Clicking on Enable button.");
					clickWithoutInterception(featureEnableButton);
					log.info("Feature Description enabled successfully");
					enabled=true;
				}
				Assert.assertTrue(enabled,"FeatureDescription Not Set True ");
			}
	        else {
	        	  if(input.equalsIgnoreCase(SolQAConstants.DISABLED))
	  			{
	  				String attributeValue = commonWaits.waitForElementVisible(featureDisabledButton).getAttribute("class");

	  				if (attributeValue.contains("btn-primary")) {
	  					log.info(
	  							"Feature Description is already Disable  (enabled button is visible). Skipping Disable action.");
	  					disabled=true;

	  				} else {
	  					log.info("enabled Disable is visible and not disabled. Clicking on Disable button.");
	  					clickWithoutInterception(featureDisabledButton);
	  					log.info("Feature Description Disable successfully");
	  					disabled=true;
	  				}
	  				
	  				Assert.assertTrue(disabled,"Feature Description Not Set False ");
	  			}
	        	  
	        }
	        
		return this;
	}

	

}
