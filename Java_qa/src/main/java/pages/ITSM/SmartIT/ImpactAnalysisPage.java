package pages.ITSM.SmartIT;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ImpactAnalysisPage extends BasePage {

    public ImpactAnalysisPage(WebDriver driver) {
        log = new SolqaLogger(ImpactAnalysisPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public ImpactAnalysisPage() {
        log = new SolqaLogger(ImpactAnalysisPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    public String navigateToConsoleOption="//*[@role='menuitem'][normalize-space()='%s']";
    public String consoleOptionButton="//a[contains(@data-testid,'navigation-menu-item') and normalize-space()='%s']";
    public String filterHeader_selectFilterOption="//div[@class='advanced-filter__label' and normalize-space()='%s']";
    public String filterHeader_selectFilterValue="//div[@class='advanced-filter__label' and normalize-space()='%s']/ancestor::div[contains(@class,'card')]//div[contains(@class,'input-block')]//input";
    public String threeDotsMenuOption="//button[normalize-space()='%s']";
    public String dropdownOption="(//button//div[normalize-space()='%s'])[1]";
    public String triggerEventOption="//span[normalize-space()='%s']/parent::div";
    public String qualificationOption="//div[contains(@class,'bwf-field-selector_field') and normalize-space()='%s']";
    public String ruleNameOption="//a[normalize-space()='%s']";
    public String navigationOption="//a[contains(@class,'a-hamburger__menu-link') and normalize-space()='%s']";
    public String moreMenuOption="//button[normalize-space()='%s']";
    public String impactAnalysisRunOption="//button[normalize-space()='%s']";
    public String relationshipOption = "//*[contains(text(),'%s')]/ancestor::button[contains(@class, 'rx-select__option')]";
    public String createdOutage = "//a[contains(@class, 'ng-star-inserted') and contains(text(), 'AOT')]";
    public String createdOutageCI = "//div[contains(text(), '%s')]";
    public String searchedCIResult = "//td[@data-testid='adapt-table_column-1_row-0' and contains(., '%s')]";
    public String checkBoxCIResult="//span[normalize-space()='Relate items']/ancestor::div//div[contains(@class, 'ui-chkbox-box')]";
    public String navigationFirstOption="//button[normalize-space()='%s']";
    public String firstConsoleOptionButton="//a[contains(@data-testid,'submenu-item') and normalize-space()='%s']";
    //WebElements Locators
    @FindBy(xpath = "//button[contains(@class,'a-hamburger')]//span[contains(.,'Main menu')]/..")
    WebElement hamburgerMenu;

    @FindBy(xpath = "//button[normalize-space()='Impact analysis run']")
    WebElement impactAnalysisRunButton;
   
    @FindBy(xpath = "//button[normalize-space()='Console']")
    WebElement navigateToConsole;

    @FindBy(xpath = "(//label[normalize-space()='Description']/ancestor::div//div[@class='input-group']//input)[1]")
    WebElement descriptionInput;

    @FindBy(xpath = "//rx-runtime-view-modal//button[normalize-space()='Filter']")
    WebElement FiltersBtn;

    @FindBy(xpath = "//rx-runtime-view-modal//button[contains(@aria-label,'Remove')]")
    WebElement removeAllFiltersBtn;

    @FindBy(xpath = "//button[normalize-space()='Apply filters']")
    WebElement applyFiltersBtn;

    @FindBy(xpath = "((//button[@aria-label='Select options']//div[@class='checkbox'])/ancestor::button)[2]")
    WebElement selectAllCheckbox;

    @FindBy(xpath = "//button[normalize-space()='Add selected']")
    WebElement addSelectedButton;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    WebElement saveButton;

    @FindBy(xpath = "//button[normalize-space()='Run analysis']")
    WebElement runAnalysisButton;

    @FindBy(xpath = "//td[@data-testid='adapt-table_column-1_row-0']//a")
    WebElement impactAnalysisRunStatus;

    @FindBy(xpath = "(//button[contains(@class,'dropdown-kabob-btn dropdown-toggle')])[1]")
    WebElement threeDotsMenu;

    @FindBy(xpath = "//iframe[@name='ccIframeComponent']")
    WebElement IFrame;
    
    @FindBy(xpath = "//iframe[@class='noborder-noscroll' and @tabindex='-1']")
    WebElement helixGPTSummaryIFrame;

    @FindBy(xpath = "//a[contains(normalize-space(),'REQ')]")
    WebElement requestID;
  
    @FindBy(xpath = "//p[strong[normalize-space()='Summary:']]/following-sibling::ul[1]//li")
    WebElement summarizeElement;

    @FindBy(xpath = "//button[normalize-space()='Rule']")
    WebElement addRuleButton;

    @FindBy(xpath = "//span[normalize-space()='Name']/ancestor::adapt-rx-control-label//following-sibling::div[@class='input-group']//input")
    WebElement nameInput;

    @FindBy(xpath = "//span[normalize-space()='Company']/ancestor::adapt-rx-control-label//following-sibling::div//button")
    WebElement companyDropdown;

    @FindBy(xpath = "(//button[normalize-space()='Build expression'])[1]")
    WebElement buildExpressionButton;

    @FindBy(xpath = "//span[normalize-space()='Incident type']/ancestor::adapt-rx-control-label//following-sibling::div//button")
    WebElement incidentTypeDropdown;

    @FindBy(xpath = "//button[normalize-space()='Add']")
    WebElement addButton;

    @FindBy(xpath = "//button[normalize-space()='Apply']")
    WebElement applyButton;

    @FindBy(xpath = "//span[normalize-space()='=']/parent::button")
    WebElement equalButton;

    @FindBy(xpath = "//th//span[contains(@class,'c-header__separator')]")
    List<WebElement> headerValues;

    @FindBy(xpath = "//button[@aria-label='Save']")
    WebElement saveIncidentButton;

    @FindBy(xpath = "//*[@id='content_block' or @id='contentBlock']")
    WebElement firstNoteActivityTimeline;

    @FindBy(xpath = "//iframe[@id='pwa-view']")
    WebElement createNewIncidentIFrame;

    @FindBy(xpath = "(//iframe[@title='View Field' and @scrolling='no'])[1]")
    WebElement feedIFrame;

    @FindBy(xpath = "//button[@title='Refresh']")
    WebElement refreshIcon;
    
    @FindBy(xpath = "(//div[contains(@class,'rx-custom-cell') and contains(normalize-space(),'INC')])[1]//a")
    WebElement firstIncidentLink;

    @FindBy(xpath = "//button[normalize-space()='More']")
    WebElement moreMenuButton;

    @FindBy(xpath = "(//button[@aria-label='Select options']//div[@class='checkbox'])[1]//ancestor::button")
    WebElement selectAllCheckboxInSelectInputCIs;

    @FindBy(xpath = "//div[@id='304433221']")
    WebElement menuOptionsViewPage;

    @FindBy(xpath = "//div[@id='ar1000000161_data']")
    WebElement IncidentIdValue;

    @FindBy(xpath = "//button[normalize-space()='Yes']")
    WebElement yesButton;

    @FindBy(xpath = "//button[@role='tab' and  contains(normalize-space(.), 'Configuration items')]")
	WebElement configurationItemsTab;
	
	@FindBy(xpath = "//button[@role='tab' and  contains(normalize-space(.), 'Related items')]")
	WebElement relatedItemsTab;
	
	@FindBy(xpath = "//button[.//span[contains(@class, 'a-menu__text') and contains(text(), 'Create related')]]")
	WebElement createRelatedButton;
	
	@FindBy(xpath = "//ul[contains(@class, 'a-menu ng')]/li[contains(.,'Outage')]")
	WebElement createRelatedOutageButton;
	
	@FindBy(xpath = "//button[contains(.,'Relate configuration item')]")
	WebElement relateCIButton;
	
	@FindBy(xpath="//*[@id='302258625']")
	public WebElement searchCITextbox;
	
	@FindBy(xpath="//*[@id='302259032']")
	public WebElement searchIcon;
	
	@FindBy(xpath="//*[@name='ar301527000']")
	public WebElement relationshipTypeButton;
	
	@FindBy(xpath="//*[@id='304253220']")
	public WebElement saveCIButton;
	

    public ImpactAnalysisPage navigateToOptionInAdvancedResponsiveNavigation(String navigation_Option, String Option) {
        switchtoDefaultFrame();
        log.info("Navigating to " + Option );
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        if(commonWaits.isElementPresent(By.xpath(String.format(navigationFirstOption, navigation_Option)))) {
            clickWithoutInterception(By.xpath(String.format(navigationFirstOption, navigation_Option)));
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
            WebElement consoleOptionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(firstConsoleOptionButton, Option)));
            clickWithoutInterception(consoleOptionElement);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        }
        else {
            log.info("clicking on hamburger menu and navigating to " + Option);
            clickWithoutInterception(hamburgerMenu);
            clickWithoutInterception(By.xpath(String.format(navigationOption, navigation_Option)));
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
            WebElement consoleOptionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(consoleOptionButton, Option)));
            clickWithoutInterception(consoleOptionElement);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        }
        return this;
    }

    public ImpactAnalysisPage clickOnImactAnalysisRun(){
        log.info("Clicking on Impact Analysis Run");
        clickWithoutInterception(impactAnalysisRunButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public ImpactAnalysisPage clickOnYesButtonifVisible() {
        log.info("Checking if Yes Button is visible");
        if(commonWaits.isElementPresent(yesButton)) {
            log.info("Clicking on Yes Button");
            commonWaits.waitForElementVisible(yesButton);
            commonWaits.clickWithoutInterception(yesButton);
        }
        return this;
    }

    public ImpactAnalysisPage addDescription(String description){
        log.info("Adding description: " + description);
        clickOnYesButtonifVisible();
        commonWaits.waitForElementVisible(descriptionInput).sendKeys(description);
        return this;
    }

    public  ImpactAnalysisPage clearFilter() {
        log.info("Clearing all filters");
        if(commonWaits.isElementPresent(removeAllFiltersBtn)) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), removeAllFiltersBtn, 5), "Remove All Filters Button not found");
            removeAllFiltersBtn.click();
        }
        return this;
    }

    public ImpactAnalysisPage clickFilter() {
        log.info("Clicking on filter icon");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(FiltersBtn).click();
        return this;
    }

    public ImpactAnalysisPage applyFilter() {
        log.info("Clicking on Apply Filters");
        commonWaits.waitForElementVisible(applyFiltersBtn).click();
        return this;
    }

    public void selectFilterValue(String filterType, String value) {
        log.info("Selecting filter value: " + value + " for filter type: " + filterType);
        WebElement filterValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(filterHeader_selectFilterOption, filterType)));
        clickWithoutInterception(filterValue);
        WebElement filterValueElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(filterHeader_selectFilterValue, filterType)));
        filterValueElement.sendKeys(value);
        filterValueElement.sendKeys(Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public ImpactAnalysisPage applyFilter(String ticketType, String Value) {
        log.info("Applying filter: " + ticketType + " for value: " + Value);
        commonWaits.switchtoDefaultFrame();
        clearFilter();
        clickFilter();
        selectFilterValue(ticketType,Value);
        applyFilter();
        return this;
    }

    public ImpactAnalysisPage clickOnSelectAllCheckbox() {
        log.info("Clicking on Select All Checkbox");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForElementVisible(selectAllCheckbox);
        if(!selectAllCheckbox.isSelected()) {
           commonWaits.clickByAction(selectAllCheckbox);
        } else {
            log.info("Select All Checkbox is already selected");
        }
        return this;
    }

    public ImpactAnalysisPage clickOnAddSelectedButton() {
        log.info("Clicking on Add Selected Button");
        commonWaits.waitForElementVisible(addSelectedButton).click();
        return this;
    }

    public ImpactAnalysisPage clickOnSaveButton() {
        log.info("Clicking on Save Button");
        commonWaits.waitForElementVisible(saveButton);
        commonWaits.clickWithoutInterception(saveButton);
        return this;
    }

    public ImpactAnalysisPage clickOnRunAnalysisButton() {  
        log.info("Clicking on Run Analysis Button");
        commonWaits.waitForElementVisible(runAnalysisButton);
        commonWaits.clickWithoutInterception(runAnalysisButton);
        return this;
    }

    public String getImpactAnalysisRunStatus() {
        log.info("Getting Impact Analysis Run Status");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return commonWaits.waitForElementVisible(impactAnalysisRunStatus).getText().trim();
    }

    public ImpactAnalysisPage navigateToRequiredOptionThrough3DotsMenu(String option) {
        log.info("Navigating to required option through 3 dots menu: " + option);
        clickWithoutInterception(threeDotsMenu);
        clickOnYesButtonifVisible();
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(threeDotsMenuOption, option)));
        commonWaits.moveToElement(getCurrentDriver(), optionElement);
        clickWithoutInterception(optionElement);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    public ImpactAnalysisPage switchToImpactAnalysisIframe() {
        log.info("Switching to Impact Analysis Iframe");
        commonWaits.switchtoDefaultFrame();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), IFrame, 30), "Impact Analysis Iframe not found");
        commonWaits.switchToFrame(IFrame);
        return this;
    }

    public String getActivityFirstNoteText_ActivityIARID(String activityFirstNoteText) {
        log.info("Getting Activity First Note Text and IAR ID");
        Pattern pattern = Pattern.compile("IAR\\d+");
        Matcher matcher = pattern.matcher(activityFirstNoteText);

        String iarId = null;
        if (matcher.find()) {
            iarId = matcher.group();
        }
        Assert.assertNotNull(iarId, "IAR ID was not found in activity message");
        return iarId;
    }

    public String getIncidentRequestID() {
        log.info("Getting Incident Request ID");
        switchtoDefaultFrame();
		switchToFrame(createNewIncidentIFrame);
        return commonWaits.waitForElementVisible(requestID).getText().trim();
    }

    public ImpactAnalysisPage clickOnRuleLink() {
        log.info("Clicking on Rule Link");
        commonWaits.waitForElementVisible(addRuleButton);
        commonWaits.clickWithoutInterception(addRuleButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public ImpactAnalysisPage setRuleName(String ruleName) {
        log.info("Setting Rule Name: " + ruleName);
        commonWaits.waitForElementVisible(nameInput).sendKeys(ruleName);
        return this;
    }

    public ImpactAnalysisPage selectCompany(String companyName) {
        log.info("Selecting Company: " + companyName);
        commonWaits.clickWithoutInterception(companyDropdown);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownOption,companyName)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownOption,companyName)));
        return this;
    }

    public ImpactAnalysisPage selectTriggerEvent(String triggerEvent) {
        log.info("Selecting Trigger Event: " + triggerEvent);
        WebElement triggerEventElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(triggerEventOption,triggerEvent)));
        commonWaits.clickWithoutInterception(triggerEventElement);
        return this;
    }

    public ImpactAnalysisPage addQualification(String qualification, String incidentType) {
        log.info("Adding Qualification: " + qualification);
        log.info("Clicking on Build expression button");
        commonWaits.clickWithoutInterception(buildExpressionButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 10);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        WebElement qualificationElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(qualificationOption,qualification)));
        commonWaits.clickWithoutInterception(qualificationElement);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("selecting the Incident type " + incidentType);
        commonWaits.clickWithoutInterception(incidentTypeDropdown);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownOption,incidentType)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownOption,incidentType)));
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("click on '=' button");
        commonWaits.clickWithoutInterception(equalButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Clicking on Add button");
        commonWaits.clickWithoutInterception(addButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Clicking on Apply button");
        commonWaits.clickWithoutInterception(applyButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        log.info("Clicking on Save button");
        commonWaits.clickWithoutInterception(saveButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public ImpactAnalysisPage addNewRuleForIncident(String ruleName, String companyName, String triggerEvent1, String triggerEvent2, String qualification, String incidentType) {
        if(!commonWaits.isElementPresent(By.xpath(String.format(ruleNameOption,ruleName)))) {
            log.info("Adding new rule for incident: " + ruleName);
            clickOnRuleLink();
            setRuleName(ruleName);
            selectCompany(companyName);
            selectTriggerEvent(triggerEvent1);
            selectTriggerEvent(triggerEvent2);
            addQualification(qualification, incidentType);
       }
        else {
            log.info("Rule already exists: " + ruleName);
        }
        return this;
    }

    public String getImpactAnalysisDescription() {
        log.info("Getting Impact Analysis Description Status");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        String description = "";
        for(int i = 1; i < headerValues.size(); i++) {
            if(headerValues.get(i).getText().trim().equalsIgnoreCase(SolQAConstants.DESCRIPTION_LABEL)) {
                String descriptionCellXpath ="//td[@data-testid='adapt-table_column-" + i + "_row-0']//div";
                description = commonWaits.waitForByElementToBeVisible(By.xpath(descriptionCellXpath)).getText().trim();
            }
        }
        return description;
     
    }

    public ImpactAnalysisPage clickOnSaveIncidentButton() {
        log.info("Clicking on Save Incident Button");
        commonWaits.waitForElementVisible(saveIncidentButton);
        commonWaits.clickWithoutInterception(saveIncidentButton);
        return this;
    }

    public String getActivityFirstNoteText_ActivityTimeline() {
		log.info("Getting first activity feed text from activity timeline");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		switchtoDefaultFrame();
		switchToFrame(createNewIncidentIFrame);
        switchToFrame(feedIFrame);
		String value = "";
		try {
			commonWaits.waitForElementVisible(firstNoteActivityTimeline);
            value = getattributeValue(firstNoteActivityTimeline, "innerText");
			String[] str = value.split("\n");
			value = "";
			for (int i = 0; i < str.length; i++) {
				if (i == 0)
					value = value + str[i].trim();
				else
					value = value + "\n" + str[i];
			}
			switchtoDefaultFrame();
		} catch (NullPointerException e) {
			return value;
		}
		return value;
	}

    public ImpactAnalysisPage switchToCreateNewIncidentIframe() {
        log.info("Switching to Create New Incident Iframe");
        commonWaits.switchtoDefaultFrame();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createNewIncidentIFrame, 30), "Create New Incident Iframe not found");
        commonWaits.switchToFrame(createNewIncidentIFrame);
        return this;
    }

    public String FetchTicketID() {
    	switchtoDefaultFrame();
		switchToFrame(createNewIncidentIFrame);
		log.info("Fetching Ticket ID");
        commonWaits.waitForPageFullyLoaded(30,3);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(menuOptionsViewPage);
        return commonWaits.waitForElementVisible(IncidentIdValue).getText().trim();
	}

    public ImpactAnalysisPage verifyActivityNotesinImpactAnalysisPage(String expectedActivityNotes) {
        log.info("Verifying Activity Notes with 3-minute retry logic");
        commonWaits.waitForPageFullyLoaded(30,3);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        long startTime = System.currentTimeMillis();
        long timeout = 3 * 60 * 1000; // 3 minutes in milliseconds
        boolean isVerified = false;
        String activityNotes = "";
        while ((System.currentTimeMillis() - startTime) < timeout) {
            activityNotes = getActivityFirstNoteText_ActivityTimeline();
            if (activityNotes.contains(expectedActivityNotes)) {
                isVerified = true;
                break;
            }
            log.info("Note not found yet. Clicking refresh and retrying...");
            commonWaits.switchtoDefaultFrame();
            commonWaits.switchToFrame(createNewIncidentIFrame);
            commonWaits.clickWithoutInterception(refreshIcon);
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }
        Assert.assertTrue(isVerified, "Activity Notes did not appear after 3 minutes. Got: " + activityNotes + " but Expected: " + expectedActivityNotes);
        
        return this;
    }

    public ImpactAnalysisPage openFirstIncident() {
        log.info("Opening first incident");
        commonWaits.waitForElementVisible(firstIncidentLink);
        commonWaits.clickWithoutInterception(firstIncidentLink);
        return this;
    }

    public ImpactAnalysisPage navigateToRequiredOptionFromMoreMenu(String option) {
        log.info("Navigating to required option from more menu: " + option);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        clickWithoutInterception(moreMenuButton);
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(moreMenuOption, option)));
        clickWithoutInterception(optionElement);
        return this;
    }

    public ImpactAnalysisPage navigateToRequiredImpactAnalysisRunOption(String option) {
        log.info("Navigating to required impact analysis run option: " + option);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        clickWithoutInterception(impactAnalysisRunButton);
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(impactAnalysisRunOption, option)));
        commonWaits.clickByAction(optionElement);
        return this;
    }

    public ImpactAnalysisPage clickOnSelectAllCheckboxInSelectInputCIs() {
        log.info("Clicking on Select All Checkbox");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForElementVisible(selectAllCheckboxInSelectInputCIs);
        if(!selectAllCheckboxInSelectInputCIs.isSelected()) {
           commonWaits.clickByAction(selectAllCheckboxInSelectInputCIs);
        } else {
            log.info("Select All Checkbox is already selected");
        }
        return this;
    }
    /**
     * Check if 'Summarize using Helix GPT' option is available in 3 dots menu
     * @return true if option is available, false otherwise
     */
    public boolean isSummarizeUsingHelixGPTOptionAvailable() {
        log.info("Checking if 'Summarize using Helix GPT' option is available");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        try {
            clickWithoutInterception(threeDotsMenu);
            
            String optionXpath = String.format(threeDotsMenuOption, SolQAConstants.Summarize_Using_Helix_GPT);
            int elementCount = getDriver().findElements(By.xpath(optionXpath)).size();
            boolean isAvailable = elementCount > 0;
            
            log.info("'Summarize using Helix GPT' option availability: " + isAvailable);
            
            // Close the menu
            commonWaits.switchtoDefaultFrame();
            clickWithoutInterception(threeDotsMenu);
            return isAvailable;
        } catch (Exception e) {
            log.info("'Summarize using Helix GPT' option is not available: " + e.getMessage());
            commonWaits.switchtoDefaultFrame();
            clickWithoutInterception(threeDotsMenu);
            return false;
        }
    }

    /**
     * Verify Impact CI summarize is visible after HelixGPT summarization
     * @return true if summary is visible, false otherwise
     */
    public boolean verifyImpactCISummarizeVisible() {
        log.info("Verifying Impact CI summarize is visible");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        try {
            // Switch to the HelixGPT summary iframe
            commonWaits.switchtoDefaultFrame();
            commonWaits.waitForElementVisible(helixGPTSummaryIFrame);
            commonWaits.switchToFrame(helixGPTSummaryIFrame);
            log.info("Switched to HelixGPT summary iframe");
            
            // Check if summary element is visible
            boolean isVisible = summarizeElement.isDisplayed();
            log.info("Impact CI summarize visibility: " + isVisible);
            
            // Switch back to default frame
            commonWaits.switchtoDefaultFrame();
            return isVisible;
        } catch (Exception e) {
            log.error("Failed to verify Impact CI summarize: " + e.getMessage());
            commonWaits.switchtoDefaultFrame();
            return false;
        }
    }

    public void relateCI(String ciName, String relationshipType) {
		log.info("Relating CI '" + ciName + "' to the Incident");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.clickWithoutInterception(configurationItemsTab);
		
		commonWaits.waitForElementVisible(relateCIButton);
		commonWaits.clickWithoutInterception(relateCIButton);

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForElementVisible(searchCITextbox).click();
        typeKeys(searchCITextbox, ciName);
        commonWaits.waitForElementVisible(searchIcon).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchedCIResult,ciName)));
        commonWaits.clickWithoutInterception(By.xpath(checkBoxCIResult));
        
        log.info("Selecting relationship type");
        commonWaits.waitForElementVisible(relationshipTypeButton).click();
        commonWaits.clickWithoutInterception(By.xpath(String.format(relationshipOption, relationshipType)));
        commonWaits.waitForPageFullyLoaded(30,3);
        
        log.info("Clicking save button");
        commonWaits.clickWithoutInterception(saveCIButton);
        commonWaits.waitForPageFullyLoaded(30,3);
		log.info("Successfully related CI '" + ciName + "' to the Incident.");
	}
	
}
