package pages.ITSM.SmartIT;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ITSMInsightsPage extends BasePage {

	SolqaLogger log;
	CommonWaits commonWaits;
	CommonMethods commonMethods;

	public ITSMInsightsPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(ITSMInsightsPage.class.getName());
		commonWaits=new CommonWaits();
		commonMethods=new CommonMethods();
	}
   
    //String locators
    String oneTimeJobRows="//*[text()='Create one-time job']/ancestor::button/following-sibling::table//tbody/tr";
    String oneTimeJobActionBtn="//*[text()='Create one-time job']/ancestor::button/following-sibling::table//adapt-rx-select//button";
    String deleteJobButton="//*[@role='listbox']//button//strong[text()='Delete']";
    String modelDialobDeleteButton="//*[contains(@class,'modal-footer')]//button[text()='Delete']";
    String jobNameInput="//input[@name='jobName']";
    String dateRangeInput="//*[contains(@id,'rx-datetime-range')]";
    String calendarIcon="//h3[normalize-space()='Data range']";
    String runNowBtn="//*[@id='job-config-save-button' and contains(. ,'Run now')]";
    String jobStatusSuccessful="//*[text()='%s']/following-sibling::td/p[contains(text(),'Successful')]";
    String jobRefreshBtn="//*[@aria-label='Refresh']";
    String jobErrorMessage="//*[text()='%s']/following-sibling::td[2]";
    String toastError="//*[@class='a-toast__details-text']";

	public String searchTextBox= "//input[@class='form-control adapt-rx-search__input']";
	public String searchTextBoxButton = "//div[contains(@class, 'rx-search__search-button')]//button";
	public String clusterLink = "//span[@class='tile-label' and contains(text(), '%s')]";
	public String selectAllIncidents= "//adapt-table-toolbar//button[contains(., 'Select all')]";
	public String manageOriginalDuplicate = "//button[contains(text(), 'Manage Original/Duplicate')]";
	public String parentModalTitle = "//div[@class='modal-header']//h5[contains(text(), 'Relate selected incidents to Original')]";
	public String parentIncident = "//div[contains(@class, 'relate-to-incident')]//button[@title='Recommended Original']/preceding-sibling::button";
	public String cancelBtn = "//button[text()='Cancel']";
	public String closeBtn = "//button[@aria-label='Close']";
	public String duplicateIncidentValue= "//table[@class='ui-table-scrollable-body-table']/tbody/tr[2]/td[1]//div/button";
	public String checkboxLocator= "//table[@class='ui-table-scrollable-body-table']/tbody/tr[2]/td[1]//div/div[2]/span";
	public String parentIncidentRadioBtn = "//*[@type='radio']/following-sibling::label";
	public String continueBtn = "(//button[text() = 'Continue'])[1]";
	public String markAsDuplicateBtn = "//*[contains(@id, 'markAsDuplicateBtn')]";
	public String showDuplicates = "//*[@id='aif-btn-RECOMMENDEDOF' and normalize-space()='Show duplicates']";
	public String firstDuplicateIncident= "//div[contains(@class, 'duplicates-item-container')]//*[@class='icon-indeterminate']/following-sibling::*)[1]";
	public String JobName= "//*[contains(text(), '%s')]";
	public String buttonOption = "//button[normalize-space()='%s']";

	@FindBy(xpath = "//button[contains(.,'Manage Jobs')]")
	WebElement manageJobsLink;

	@FindBy(xpath = "//button[contains(.,'Workspaces')]")
	WebElement workspacesLink;

	@FindBy(xpath = "//button[contains(.,'Proactive problem management')]")
	WebElement proactiveProblemManagementLink;

	@FindBy(xpath = "//label[contains(.,'Jobs')]//ancestor::adapt-rx-control-label//following-sibling::div//button")
	WebElement jobDropdownIcon;

	@FindBy(xpath = "//button[contains(.,'View advanced analytics')]")
	WebElement ViewAdvancedAnalytics;
	
	@FindBy(xpath = "//button[contains(.,'Chart based visualization')]")
	WebElement chartBasedVisualization;

	@FindBy(xpath = "//*[contains(@id,'manage-job-create') and contains(. ,'Create one-time job')]")
	WebElement createOneTimeJoblink;

	@FindBy(xpath = "(//*[contains(@class,'treemap__actions-menu') and @role='button'])[1]")
	WebElement threeDotsDrilldown;

	@FindBy(xpath = "//input[contains(@placeholder,'search for existing problem by Problem')]")
	WebElement relateToExistingProblemSearchBox;

	@FindBy(xpath = "((//div[contains(@class,'ui-chkbox')])[1]/parent::adapt-row-selection-checkbox/parent::div | (//div[@aria-labelledby='adapt-toolbar-2-counter']//div[contains(@class,'ui-chkbox')]/parent::adapt-row-selection-checkbox)[1])[2]")
	WebElement relateToExistingProblemResultCheckbox;

	@FindBy(xpath = "(//button[normalize-space()='Continue'])[2]")
	WebElement continueButtonForRelateToExistingProblem;

	@FindBy(xpath = "//div[contains(@class,'a-toast__summary') and normalize-space()='Success']")
	WebElement successToastForRelateToExistingProblem;

	@FindBy(xpath = "//button[@id='304440891' and normalize-space()='Save']")
	WebElement saveProblem;

	@FindBy(xpath = "//div[contains(@title,'PBI')]")
	WebElement problemID;

	@FindBy(xpath = "(//*[contains(@class,'treemap-node') and @role='button'])[1]")
	WebElement firstCluster;

	@FindBy(xpath = "(//button[contains(@id,'INC')])[1]")
	WebElement firstIncident;

	@FindBy(xpath = "//div[contains(@title,'INC')]")
	WebElement incidentIDValue;

	@FindBy(xpath = "//button[@aria-label='Explore probable causes']")
	WebElement exploreProbableCausesButton;

	@FindBy(xpath = "//span[contains(@class,'product__name text') and normalize-space()='Service Monitoring']")
	WebElement clusterLinkOnCrossLaunchPage;

	@FindBy(xpath = "//span[normalize-space()='Search for existing problem']/ancestor::button[@role='tab']")
	WebElement searchForExistingProblemButton;

	@FindBy(xpath = "//iframe[@id='pwa-frame']")
    WebElement pwaFrame;

	@FindBy(xpath=("//span[contains(text(),'Resources')]//ancestor::button[@role='tab']"))
	WebElement resourcesTab;

	public void navigateToManageJobs(){
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.clickWithoutInterception(manageJobsLink);
        commonWaits.waitForAngularRequestsToFinish();
    }
	
	 public void createOneTimeJob(String jobName){
	        waitForJobLinkEnabled();
	        if(commonWaits.isElementPresent(By.xpath(oneTimeJobRows)) && commonWaits.getElements(By.xpath(oneTimeJobRows)).size()==3){
	            deleteOneTimeJob();
	        }
	        waitForJobLinkEnabled();
	        commonWaits.clickWithoutInterception(createOneTimeJoblink);
	        if(commonWaits.waitForElementPresent(toastError,15)){
	            String errorMsg=commonWaits.waitForByElementToBeVisible(By.xpath(toastError)).getText().trim();
	            Assert.fail("There is an application error while clicking on Create One time job link.Error message is : "+ errorMsg);
	        }

	        keyClearAndType(jobNameInput, jobName);
    		commonWaits.waitForPageFullyLoaded(30, 3);
	        commonWaits.javaScriptScrollIntoView(commonWaits.waitForByElementToBeVisible(By.xpath(dateRangeInput)));
	        keyClearAndClick(dateRangeInput);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	        LocalDate today = LocalDate.now();
	        LocalDate todayMinusDate = today.minusDays(14);
	        String startDate = todayMinusDate.format(formatter);
	        String endDate=today.format(formatter);
	        commonWaits.waitForByElementToBeVisible(By.xpath(dateRangeInput)).sendKeys(startDate);
	        commonWaits.waitForByElementToBeVisible(By.xpath(dateRangeInput)).sendKeys(" - ");
	        commonWaits.waitForByElementToBeVisible(By.xpath(dateRangeInput)).sendKeys(endDate);
	        commonWaits.clickWithoutInterception(By.xpath(calendarIcon));
	        commonWaits.waitForPageFullyLoaded(30, 3);
	        commonWaits.waitForByElementToBeVisible(By.xpath(runNowBtn)).click();
	    }
	 
	 public void waitForJobLinkEnabled(){
	        int i=0;
	        while(!commonWaits.waitForElementVisible(createOneTimeJoblink).isEnabled() && i<10){
	            ++i;
	            commonWaits.waitForSometime(commonWaits.minDelay);
	        }
	    }
	 
	 public void deleteOneTimeJob(){
	        commonWaits.waitForByElementToBeVisible(By.xpath(oneTimeJobActionBtn)).click();
	        commonWaits.waitForByElementToBeVisible(By.xpath(deleteJobButton)).click();
	        commonWaits.waitForByElementToBeVisible(By.xpath(modelDialobDeleteButton)).click();
	        commonWaits.waitForAngularRequestsToFinish();
	    }
	 
	 public void verifyJobStatus(String jobName){
	        this.waitForJobLinkEnabled();
	        int counter=0;
	        while(!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(jobStatusSuccessful,jobName)), 10) && counter < 10){
	            ++counter;
	            commonWaits.waitForByElementToBeVisible(By.xpath(jobRefreshBtn)).click();
	        }
	        commonWaits.waitForByElementToBeVisible(By.xpath(jobRefreshBtn)).click();
	        if(!commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(jobStatusSuccessful,jobName)), 20)){
	            String errorMsg=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(jobErrorMessage,jobName))).getText().trim();
	            Assert.fail("Job failed with Error message : " + errorMsg);
	        }
	    }

	public void setClusterNameAndClickCluster(String clusterName) throws Exception {
		commonWaits.waitForByElementToBeVisible(By.xpath(searchTextBox));
		Assert.assertTrue(commonWaits.isElementPresent(searchTextBox), "Search Box is missing on PPM console of ITSM Insights");
		keyClearAndType(searchTextBox,clusterName );
		commonWaits.waitForByElementToBeVisible(By.xpath(searchTextBoxButton));
		click(By.xpath(searchTextBoxButton));
		commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(clusterLink, clusterName), commonWaits.moreDelay);
		click(By.xpath(String.format(clusterLink, clusterName)));
		commonWaits.waitForExecutionComplete();
	}

	public void clickOnManageOriginalDuplicateLink() {
		commonWaits.waitForByElementToBeVisible(By.xpath(selectAllIncidents));
		click(selectAllIncidents);
		commonWaits.waitForPageFullyLoaded(30, 3);
		commonWaits.clickWithoutInterception(By.xpath(manageOriginalDuplicate));
		Assert.assertTrue(commonWaits.waitForElementPresent(parentModalTitle), "element not visible");
	}

	public void clickOnParentTicket() {

		log.info("Clicking on parent incident if present");
		if (isElementPresent(By.xpath(parentIncident))) {
			commonWaits.pureSafeClick(parentIncident);
		} else {
			log.warn("Parent incident link not found.");
			return;
		}

		int currentWindowCount = getDriver().getWindowHandles().size();
		int expectedWindowCount = currentWindowCount + 1;

		log.info("Waiting for window count to be: " + expectedWindowCount);
		commonWaits.waitForNumberWindowsToBe(expectedWindowCount);

		log.info("Switching to last window after window opens");
		commonWaits.switchToLastWindow();

		log.info("Now on window with title: " + getDriver().getTitle());
		log.info("Current URL: " + getDriver().getCurrentUrl());
	}

	public void closeParentTicket() throws Exception {

		commonWaits.switchToTabByIndex(2);

		if (isElementPresent(By.xpath(cancelBtn))) {
			commonWaits.pureSafeClick(cancelBtn);
		}
		if (isElementPresent(By.xpath(closeBtn))) {
			commonWaits.pureSafeClick(closeBtn);
		}
		commonWaits.waitForExecutionComplete();
	}

	public String clickOnDuplicateIncident() {
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForElementClickable(duplicateIncidentValue, 30);
		String id = getValueFromText(duplicateIncidentValue).trim();
		return id;
	}

	public void markAsDuplicate() {
		commonWaits.switchToTabByIndex(2);
		commonWaits.clickWithoutInterception(By.xpath(parentIncidentRadioBtn));
		commonWaits.clickWithoutInterception(By.xpath(continueBtn));
		commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForElementClickable(markAsDuplicateBtn, 30);
		commonWaits.clickWithoutInterception(By.xpath(markAsDuplicateBtn));
		commonWaits.waitForElementClickable(showDuplicates, 30);
		commonWaits.clickWithoutInterception(By.xpath(showDuplicates));

	}

	public String getFirstDuplicateID() {
		commonWaits.waitForByElementToBeVisible(By.xpath(firstDuplicateIncident));
		return getValueFromText(firstDuplicateIncident);
	}

	public void navigateBackToProactiveProblemManagement(){
        commonWaits.clickWithoutInterception(workspacesLink);
		commonWaits.waitForElementVisible(proactiveProblemManagementLink);
		commonWaits.clickWithoutInterception(proactiveProblemManagementLink);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForAngularRequestsToFinish();
    }

	public void selectJob(String jobName) {
        commonWaits.waitForElementVisible(jobDropdownIcon);
		commonWaits.clickWithoutInterception(jobDropdownIcon);	
		WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(JobName, jobName)));
		if(ele != null) {
			commonWaits.clickWithoutInterception(By.xpath(String.format(JobName, jobName)));
		} else {
			log.error("Job with name " + jobName + " not found in the dropdown.");
			Assert.fail("Job with name " + jobName + " not found in the dropdown.");
		}	

}

public void navigateToViewAdvancedAnalytics(){
	if(commonWaits.isElementClickable(ViewAdvancedAnalytics)) {
		commonWaits.clickWithoutInterception(ViewAdvancedAnalytics);
        commonWaits.waitForAngularRequestsToFinish();
    }
	else {
		log.error("View Advanced Analytics button is not clickable.");	
		Assert.fail("View Advanced Analytics button is not clickable.");	
}
}	

public void verifyAdvancedAnalyticsDataPage() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.switchToLastWindow();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		Assert.assertTrue(commonWaits.isElementPresent(chartBasedVisualization, 30), "Chart based visualization button is not present on Advanced Analytics Data page.");
		log.info("Advanced Analytics Data page is displayed successfully.");		
		commonWaits.waitForPageFullyLoaded(30, 3);
	}

	public void clickOn3DotsDrilldownOnTopCornerOfClusterMap() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.clickWithoutInterception(threeDotsDrilldown);
		commonWaits.waitForAngularRequestsToFinish();
	}

	public ITSMInsightsPage selectClusterOption(String clusterOptionName) {
		WebElement clusterOptionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(buttonOption, clusterOptionName)));
		commonWaits.clickWithoutInterception(clusterOptionElement);
		commonWaits.waitForAngularRequestsToFinish();
		WebElement continueButton = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(buttonOption, "Continue")));
		commonWaits.clickWithoutInterception(continueButton);
		commonWaits.waitForAngularRequestsToFinish();
		return this;
	}

	public ITSMInsightsPage relateToExistingProblem() {
		if(!commonWaits.isElementPresent(relateToExistingProblemSearchBox)) {
			Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), searchForExistingProblemButton, 30), "Search for existing problem button is not visible.");
			commonWaits.clickWithoutInterception(searchForExistingProblemButton);
		}
		commonWaits.waitForElementVisible(relateToExistingProblemSearchBox);
		keyClearAndType(relateToExistingProblemSearchBox, "%");
		commonMethods.pressEnterKey();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
		commonWaits.clickWithoutInterception(relateToExistingProblemResultCheckbox);
		commonWaits.clickWithoutInterception(continueButtonForRelateToExistingProblem);
		commonWaits.waitForAngularRequestsToFinish();
		WebElement continueButton = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(buttonOption, "Save")));
		commonWaits.clickWithoutInterception(continueButton);
		commonWaits.waitForAngularRequestsToFinish();
		Assert.assertTrue(commonWaits.isElementPresent(successToastForRelateToExistingProblem, 60), "Success toast is not present after relating to existing problem.");
		log.info("Successfully related to existing problem.");
		return this;
	}

	public String createProblemAndGetProblemID() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForNumberWindowsToBe(4);
		commonWaits.switchToLastWindow();
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(pwaFrame);
		commonWaits.clickWithoutInterception(saveProblem);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
		commonWaits.waitForElementVisible(problemID,50);
		String problemIDValue = problemID.getAttribute("title");
		commonWaits.switchtoDefaultFrame();
		log.info("Problem ID is: " + problemIDValue);
		log.info("Successfully created problem.");
		return problemIDValue;
	}

	 public ITSMInsightsPage openTheFirstCluster() {
		commonWaits.clickWithoutInterception(firstCluster);
		commonWaits.waitForAngularRequestsToFinish();
		return this;
	}

	public ITSMInsightsPage openIncidentFromClusterDetailsPageAndVerifyIncidentID() {
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchToLastWindow();
		commonWaits.waitForElementVisible(firstIncident,40);
		String incidentID = firstIncident.getText().trim();
		log.info("Incident ID is: " + incidentID);
		commonWaits.clickWithoutInterception(firstIncident);
		commonWaits.waitForPageFullyLoaded(30, 3);		
		commonWaits.switchToLastWindow();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.DOTS_LOADER);
		commonWaits.switchtoDefaultFrame();
		commonWaits.switchToFrame(pwaFrame);
		commonWaits.waitForElementVisible(incidentIDValue,60);
		String actualIncidentID = incidentIDValue.getAttribute("title");
		log.info("actual Incident ID: " + actualIncidentID);
		Assert.assertEquals(actualIncidentID, incidentID,
				"Incident ID from details tab is not matched with incident details page.");
		return this;
	}

	public ITSMInsightsPage clickOnResourcesTab() {
			commonWaits.waitForElementVisible(resourcesTab);
			commonWaits.clickWithoutInterception(resourcesTab);
			commonWaits.waitForPageFullyLoaded(30, 3);
		return this;
	}

	public ITSMInsightsPage checkForExploreProbableCausesButton() {
		clickOnResourcesTab();
		commonWaits.waitForElementVisible(exploreProbableCausesButton,10);
		Assert.assertTrue(commonWaits.isElementPresent(exploreProbableCausesButton), "Explore Probable Causes button is not present on the incident details page.");
		commonWaits.waitForAngularRequestsToFinish();
		return this;
	}

	public ITSMInsightsPage clickOnExploreProbableCausesButtonAndVerifyCrossLaunch() {
		commonWaits.clickWithoutInterception(exploreProbableCausesButton);
		commonWaits.waitForAngularRequestsToFinish();
		commonWaits.switchToLastWindow();
		Assert.assertTrue(commonWaits.isElementPresent(clusterLinkOnCrossLaunchPage, 40), "Cluster link is not present on the cross launch page.");
		commonWaits.waitForExecutionComplete();
		return this;
	}

}
