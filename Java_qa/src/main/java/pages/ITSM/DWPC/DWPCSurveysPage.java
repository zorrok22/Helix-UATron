package pages.ITSM.DWPC;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolqaLogger;

public class DWPCSurveysPage extends BasePage {
	
	static SolqaLogger log;
	CommonWaits commonWaits;

	//WebElements Locators
	@FindBy (xpath ="//*[text()='Surveys' and @class='sb-page__title']")
	WebElement surveysPageTitle;
	
	@FindBy (xpath ="//button[@ng-bind='action.label' and text()='New Survey']")
	WebElement newSurveyButton;
	
	@FindBy (xpath ="//h3[@class='modal-title' and contains(text(),'Survey Designer')]")
	WebElement surveyDesignerTitle;
	
	@FindBy (xpath ="//button[@class='sb-icon-button d-icon-ellipsis dropdown-toggle' and @uib-dropdown-toggle]")
	WebElement threeDotsIcon;
	
	@FindBy (xpath ="//button[@ng-click='QuestionDesigner.edit()']")
	WebElement editButton;
	
	@FindBy (xpath ="//div[@class='sb-box_section-header' and contains(text(),'Label customizations')]")
	WebElement labelCustomizationHeader;
	
	@FindBy (xpath ="//div[contains(@class,'rating-column_labels-row')]//p[contains(text(),'Not at all likely')]")
	WebElement npsNotAtAllLikelyText;
	
	@FindBy (xpath ="//div[contains(@class,'rating-column_labels-row')]//p[contains(text(),'Extremely likely')]")
	WebElement npsExtremelyLikelyText;
	
	@FindBy (xpath ="//span[contains(@class,'sb-formfield__label') and contains(text(),'0 - Strongly negative')]/following-sibling::span//input")
	WebElement stronglyNegativeInput;
	
	@FindBy (xpath ="//span[contains(@class,'sb-formfield__label') and contains(text(),'10 - Strongly positive')]/following-sibling::span//input")
	WebElement stronglyPositiveInput;
	
	@FindBy (xpath ="//div[contains(@class,'sb-edit-question-rating-option_label') and contains(text(),'Terrible')]")
	WebElement facesTerribleLabel;
	
	@FindBy (xpath ="//div[contains(@class,'sb-edit-question-rating-option_label') and contains(text(),'Excellent')]")
	WebElement facesExcellentLabel;
	
	@FindBy (xpath ="//button[@ng-click='submit()' and contains(@class,'d-button_primary')]")
	WebElement saveButton;
	
	@FindBy (xpath ="//button[@type='button' and @ng-click='save()' and contains(@class,'d-button_primary')]")
	WebElement saveCreatedSurveyButton;
	
	@FindBy (xpath ="//input[@name='name' and @ng-model='questionnaire.name']")
	WebElement surveyNameTextbox;
	
	@FindBy (xpath ="//button[@type='submit' and contains(@class,'d-button_primary')]")
	WebElement finalSaveButton;
	
	@FindBy (xpath ="//a[@ui-sref='sb.dashboard()' and contains(@class,'d-n-menu__link')]")
	WebElement dashboardTab;
	

	public DWPCSurveysPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(DWPCSurveysPage.class.getName());
		commonWaits=new CommonWaits();
	}
	
	public DWPCSurveysPage verifySurveysPageLaunchedSuccessfully() {
		log.info("Verifying Surveys Page is launched successfully");
		commonWaits.waitForElementVisible(surveysPageTitle);
		boolean isSurveysPagePresent = commonWaits.isElementPresent(surveysPageTitle);
		Assert.assertTrue(isSurveysPagePresent, "Surveys Page is not launched successfully");
		log.info("Surveys Page launched successfully");
		return this;
	}
	
	public DWPCSurveysPage clickOnNewSurvey() {
		log.info("Clicking on New Survey button");
		commonWaits.waitForElementVisible(newSurveyButton);
		commonWaits.waitForElementClickable(newSurveyButton, 10);
		commonWaits.clickWithoutInterception(newSurveyButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		log.info("Clicked on New Survey button");
		return this;
	}
	
	public DWPCSurveysPage verifySurveyDesignerPopupDisplayed() {
		log.info("Verifying Survey Designer popup is displayed");
		commonWaits.waitForElementVisible(surveyDesignerTitle);
		boolean isSurveyDesignerPresent = commonWaits.isElementPresent(surveyDesignerTitle);
		Assert.assertTrue(isSurveyDesignerPresent, "Survey Designer popup is not displayed");
		log.info("Survey Designer popup displayed successfully");
		return this;
	}
	
	public DWPCSurveysPage editAndCheckSurveyFields() {
		log.info("Step 1: Clicking on three dots icon and Edit button");
		commonWaits.waitForElementVisible(threeDotsIcon);
		commonWaits.clickWithoutInterception(threeDotsIcon);
		commonWaits.waitForElementVisible(editButton);
		commonWaits.clickWithoutInterception(editButton);
		
		// Wait for Label customizations section to load
		commonWaits.waitForElementVisible(labelCustomizationHeader);
		
		// Check which scale is displayed by looking for scale-specific elements
		// NPS scale has "Not at all likely" / "Extremely likely" labels
		// Faces scale has "Terrible" / "Excellent" labels
		boolean isNpsScale = commonWaits.isElementPresent(npsNotAtAllLikelyText);
		boolean isFacesScale = commonWaits.isElementPresent(facesTerribleLabel);
		
		log.info("Scale detection - NPS (0-10): " + isNpsScale + ", Faces (1-5): " + isFacesScale);
		
		if (isNpsScale) {
			log.info("NPS scale (0-10) detected - validating Not at all likely/Extremely likely values");
			validateNpsScale();
		} else if (isFacesScale) {
			log.info("Faces scale (1-5) detected - validating Terrible/Excellent values");
			validateFacesScale();
		} else {
			log.info("Unknown scale detected, attempting NPS scale validation as default");
			validateNpsScale();
		}
		
		log.info("Step 4: Clicking on Save button");
		commonWaits.waitForElementVisible(saveButton);
		commonWaits.clickWithoutInterception(saveButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return this;
	}
	
	private DWPCSurveysPage validateNpsScale() {
		log.info("Step 2: Validating NPS scale text labels");
		commonWaits.waitForElementVisible(npsNotAtAllLikelyText);
		Assert.assertTrue(commonWaits.isElementPresent(npsNotAtAllLikelyText), "NPS 'Not at all likely' text is not displayed");
		log.info("NPS text 'Not at all likely' is displayed");
		
		commonWaits.waitForElementVisible(npsExtremelyLikelyText);
		Assert.assertTrue(commonWaits.isElementPresent(npsExtremelyLikelyText), "NPS 'Extremely likely' text is not displayed");
		log.info("NPS text 'Extremely likely' is displayed");
		
		log.info("Step 3: Validating Label customization input values for NPS scale");
		commonWaits.waitForElementVisible(labelCustomizationHeader);
		Assert.assertTrue(commonWaits.isElementPresent(labelCustomizationHeader), "Label customizations section is not displayed");
		
		commonWaits.waitForElementVisible(stronglyNegativeInput);
		String stronglyNegativeValue = stronglyNegativeInput.getAttribute("value");
		log.info("Strongly negative (0) input value: " + stronglyNegativeValue);
		Assert.assertEquals(stronglyNegativeValue, "Not at all likely", "Strongly negative input value mismatch");
		
		commonWaits.waitForElementVisible(stronglyPositiveInput);
		String stronglyPositiveValue = stronglyPositiveInput.getAttribute("value");
		log.info("Strongly positive (10) input value: " + stronglyPositiveValue);
		Assert.assertEquals(stronglyPositiveValue, "Extremely likely", "Strongly positive input value mismatch");
		return this;
	}
	
	private DWPCSurveysPage validateFacesScale() {
		log.info("Step 2: Validating Faces scale (1-5)");
		
		log.info("Step 3: Validating Label customization values for Faces scale");
		commonWaits.waitForElementVisible(labelCustomizationHeader);
		Assert.assertTrue(commonWaits.isElementPresent(labelCustomizationHeader), "Label customizations section is not displayed");
		
		commonWaits.waitForElementVisible(facesTerribleLabel);
		String terribleValue = facesTerribleLabel.getText().trim();
		log.info("Strongly negative (1) label value: " + terribleValue);
		Assert.assertEquals(terribleValue, "Terrible", "Strongly negative label value mismatch");
		
		commonWaits.waitForElementVisible(facesExcellentLabel);
		String excellentValue = facesExcellentLabel.getText().trim();
		log.info("Strongly positive (5) label value: " + excellentValue);
		Assert.assertEquals(excellentValue, "Excellent", "Strongly positive label value mismatch");
		return this;
	}
	
	public String saveCreatedSurvey(String surveyNamePrefix) {
		String surveyName = surveyNamePrefix + "_" + RandomStringUtils.randomAlphabetic(10);
		
		log.info("Step 1: Clicking on Save button to save created survey");
		commonWaits.waitForElementVisible(saveCreatedSurveyButton);
		commonWaits.waitForElementClickable(saveCreatedSurveyButton, 10);
		commonWaits.clickWithoutInterception(saveCreatedSurveyButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		
		log.info("Step 2: Entering survey name in Name textbox");
		commonWaits.waitForElementVisible(surveyNameTextbox);
		surveyNameTextbox.clear();
		surveyNameTextbox.sendKeys(surveyName);
		log.info("Survey name entered: " + surveyName);
		
		log.info("Step 3: Clicking final Save button to save survey");
		commonWaits.waitForElementVisible(finalSaveButton);
		commonWaits.waitForElementClickable(finalSaveButton, 10);
		commonWaits.clickWithoutInterception(finalSaveButton);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		
		return surveyName;
	}
	
	public DWPCDashboardPage navigateBackToDashboard() {
		log.info("Click on Dashboard tab");
		commonWaits.waitForElementVisible(dashboardTab);
		commonWaits.clickWithoutInterception(dashboardTab);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
		return new DWPCDashboardPage();
	}

	

}

