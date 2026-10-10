package pages.ITSM.SmartIT;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartITCalendarPage extends BasePage {

	private static final SolqaLogger log = new SolqaLogger(SmartITCalendarPage.class.getName());
	private CommonWaits commonWaits;

	public SmartITCalendarPage() {
		log.info("Initializing SmartITCalendarPage");
		PageFactory.initElements(getDriver(), this);
		commonWaits = new CommonWaits();
		log.info("SmartITCalendarPage initialized successfully");
	}

	// -------------------- Locators / constants --------------------
	String pwaIframe = "//iframe[@id='pwa-frame']";
	String ChangeFreezeDrpDwn ="//*[normalize-space()='%s']/ancestor::adapt-rx-control-label/following-sibling::div//button";
	String drpDown_locator = "//button[contains(@class,'dropdown-item')]//div[contains(. ,'%s')]";
	String AdaptTextBox="//*[@rx-view-component-id='%s']//input";
	String checkedItem="//*[contains(.,'%s')]//adapt-row-selection-checkbox";
	String CISearchTextbox="e24931c7-d5ca-4eea-85d7-d218e3ec58af";
	String ID="(//*[contains(@id,'%s')])";
	String RelatedConfigurationItemBtn="304424121";
	String Adapt_TextBox="(//*[@id='%s'])";
	String CISearchTextbox_Adapt_TextBox="304449871";
	String WarningIconInCalender = "(//p[normalize-space()='%s']/parent::*//adapt-icon[contains(@class,'warningIcon')])[1]";


	
  //WebElements
	@FindBy(xpath = "//iframe[@id='studio-frame']")
	public WebElement studioIframe;

	@FindBy(xpath = "//button[@id='createNew']")
	public WebElement CreateNewButtonFromCalendar;
	
	@FindBy(xpath = "//button[@data-testid='createChangeFreeze']")
	public WebElement ChangeFreezeButtonFromCalendar;
	
	@FindBy(xpath = "//*[@rx-view-component-id='abcdefa6-95e4-49db-99f6-ef1c2ef995de']//button")
	public WebElement SaveChangeFreeze;
	
	@FindBy(xpath = "//div[contains(.,'change freeze period')]//div[@ux-id='system-alert-text']")
	public WebElement ChangeFreezeErrorMessage;

	@FindBy(xpath = "//*[@ux-id='navigation-item_calendar']//a")
	public WebElement calendar;

	@FindBy(xpath = "//label[contains(.,'Start Date')]/parent::div/parent::*//following-sibling::div//input")
	public WebElement SchedualStartDate;

	@FindBy(xpath = "//label[contains(.,'End Date')]/parent::div/parent::*//following-sibling::div//input")
	public WebElement SchedualEndDate;

	@FindBy(xpath = "//label[contains(.,'Start Time')]/parent::div/parent::*//following-sibling::div//input")
	public WebElement SchedualStartTime;

	@FindBy(xpath = "//label[contains(.,'End Time')]/parent::div/parent::*//following-sibling::div//input")
	public WebElement SchedualEndTime;

	@FindBy(xpath = "//*[contains(.,'Summary')]/parent::adapt-rx-control-label/following-sibling::div//input")
	public WebElement ChangeFreezeTextbox;

	@FindBy(xpath = "//div[@ux-id='dates-tab']/div[@class='create-change-wizard__tab']//span")
	public WebElement DatesTab;

	@FindBy(xpath = "//div[@ux-id='field_scheduledDates']//input[@ux-id='start-date-picker']")
	public WebElement StartDatePicker;

	@FindBy(xpath = "//div[@ux-id='field_scheduledDates']//input[@ux-id='end-date-picker']")
	public WebElement EndDatePicker;

	@FindBy(xpath = "//button[contains(.,'Add configuration')]")
	public WebElement AddCI;

	@FindBy(xpath = "//*[@rx-view-component-id='7e1b1a41-f7bc-470c-b524-5f7522c62ae9']//button")
	public WebElement CISearchbtn;

	@FindBy(xpath = "//*[@rx-view-component-id='d2cf9a1e-a71f-4cde-aa71-2de496849746']")
	public WebElement AddBtn;

	@FindBy(xpath = "//button[normalize-space()='Today']")
	public WebElement todayButton;
	

	public SmartITCalendarPage navigateToCalendar() {
		commonWaits.switchtoDefaultFrame();
		log.info("Clicking on Calendar Menu");
		commonWaits.clickWithoutInterception(calendar);
		commonWaits.waitForPageFullyLoaded(defaultTimeout, 4);
		return this;
	}

	public String getDate(int days, String forMat) {
		SimpleDateFormat formatter = new SimpleDateFormat(forMat);
		Date dt = new Date();
		String date = formatter.format(dt.getTime() + ((days * 1000 * 60 * 60 * 24)));
		return date;
	}

	public enum dateFormat {
		MMMDYYYY("MMM d, yyyy"), DDMMMMYYYY("dd MMMM yyyy"),MMMMDYYYY("MMMM d, YYYY");
		
		public final String text;
		
		private dateFormat(final String text) {
			this.text = text;
		}
		@Override
		public String toString() {
			return text;
		}
	}

	public String getDate(int days) {
		return getDate(days, dateFormat.MMMDYYYY.toString());
	}

	public SmartITCalendarPage fillScheduleDatesInChangeFreeze(int startDateDays, int endDateDays) {
		log.info("Filling the Change schedule Dates");
		String scheduleStartDate = this.getDate(startDateDays);
		String scheduleEndDate = this.getDate(endDateDays);
		commonWaits.waitForElementVisible(SchedualStartDate, 10);
		typeKeys(SchedualStartDate, scheduleStartDate);
		commonWaits.waitForElementVisible(SchedualEndDate);
		typeKeys(SchedualEndDate, scheduleEndDate);
		return this;
	}

	

	public void navigateToChangeFreeze() {
		log.info("Navigating to Change Freeze");
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageFullyLoaded(defaultTimeout, 1);
		commonWaits.switchtoDefaultFrame();
		commonWaits.waitForElementVisible(studioIframe, 60);
		commonWaits.switchToFrame(studioIframe);
		commonWaits.waitForElementVisible(CreateNewButtonFromCalendar, 60);
		commonWaits.clickWithoutInterception(CreateNewButtonFromCalendar);
		commonWaits.clickWithoutInterception(ChangeFreezeButtonFromCalendar);
		commonWaits.waitForExecutionComplete();
	}

	public void fillScheduleStartTimeAndEndTime(String startTime, String endTime) {
		commonWaits.waitForElementVisible(SchedualStartTime, 10);
		this.typeKeys(SchedualStartTime,startTime);
		this.typeKeys(SchedualEndTime, endTime);
	}

	public void setDropDownValues(String labelName, String value){
		log.info("Set Values of Fields while Creating case");
		String sLocator= null;
		switch (labelName) {

		case "Company":
			sLocator= ChangeFreezeDrpDwn;
			break;
			
		case "Type":
			sLocator= ChangeFreezeDrpDwn;
			break;
			
		case "Based on":
			sLocator= ChangeFreezeDrpDwn;
			break;
			
		case "Region":
			sLocator= ChangeFreezeDrpDwn;
			break;
			
		case "Site group":
			sLocator= ChangeFreezeDrpDwn;
			break;
			
		case "Site":
			sLocator= ChangeFreezeDrpDwn;
			break;
		
		default:
			break;
		}

		WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(sLocator,labelName)));
		commonWaits.javaScriptScrollIntoView(ele);
		commonWaits.waitForSometime(commonWaits.minDelay);
		commonWaits.waitForElementClickable(ele);
		Actions builder = new Actions(getDriver());
		builder.moveToElement(ele).click().perform();
		commonWaits.waitForSometime(commonWaits.minDelay);
		String DropdownLocator = String.format(drpDown_locator, value);
		try{
			commonWaits.waitForByElementToBeVisible(By.xpath(DropdownLocator));
			WebElement element = getDriver().findElement(By.xpath(DropdownLocator));
			builder.moveToElement(element).click().perform();
		}catch(Exception e){
			Assert.fail(value +" for dropdown "+ labelName + " is not found on create case page");
		}
		log.info(value+" for DropDown "+labelName+" in Create case");
		commonWaits.waitForExecutionComplete();
	}

	public void setSummary(String summary) {
		commonWaits.waitForPageFullyLoaded(defaultTimeout, 1);
		commonWaits.waitForElementVisible(ChangeFreezeTextbox, 10);
		ChangeFreezeTextbox.sendKeys(summary);
		commonWaits.waitForExecutionComplete();
	}
 
    public void saveChangeFreeze() {
	 commonWaits.waitForElementClickable(SaveChangeFreeze);
	 commonWaits.clickWithoutInterception(SaveChangeFreeze);
	 commonWaits.waitForExecutionComplete();
	 commonWaits.switchtoDefaultFrame();
	 commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
 }


    public SmartITCalendarPage addCIForChangeFreeze(String searchText) {
	    commonWaits.waitForElementClickable(AddCI);
	    commonWaits.clickWithoutInterception(AddCI);
	    commonWaits.waitForExecutionComplete();
	    commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AdaptTextBox, CISearchTextbox)));
	    commonWaits.typeKeys(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(AdaptTextBox, CISearchTextbox))), searchText);
	    commonWaits.clickWithoutInterception(CISearchbtn);
	    click(CISearchbtn);
	    click(String.format(checkedItem,searchText));
	    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	    commonWaits.clickWithoutInterception(AddBtn);
	    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
	    return this;
}

public void clickAddConfigurationItem() {
	log.info("Clicking on Add Configuration Item of Create Change page");
	commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ID, RelatedConfigurationItemBtn)));
	commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ID, RelatedConfigurationItemBtn))));
	commonWaits.waitForByElementToBeVisible(By.xpath(String.format(Adapt_TextBox, CISearchTextbox_Adapt_TextBox)));
}
 
  public void verifyRedDotIconPresentInCalender(String ChangeID,boolean isPresent) {
	   commonWaits.waitForPageFullyLoaded(defaultTimeout, 1);
	   commonWaits.switchtoDefaultFrame();
	   commonWaits.switchToFrame(studioIframe);
	   commonWaits.clickWithoutInterception(todayButton);
	   WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(WarningIconInCalender,ChangeID,10)));
	   boolean flag = commonWaits.isElementPresent(ele);
	   Assert.assertEquals(flag, isPresent,"Red dot icon is not present in calender");
   }
}


