package pages.ITOM.Aiops;

import framework.BasePage;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.List;
import java.util.Map;

public class AiopsServiceBlueprintPage extends BasePage {
    CommonWaits commonWaits;

    private static SolqaLogger log = new SolqaLogger(AIOpsFeatureMethods.class.getName());
    String blueprintsShowHideBtn = "//input[@id='blueprint-list-toggle-hideshow']";

    Map<String, String> TABLES = Map.of(
            "blueprint_list_table", "blueprint-list-table",
            "blueprint_services_table", "blueprint-instances-table"
    );
    String blueprintsTableColumnSearch = "//adapt-table-header-cell//span[text()='%s']//ancestor::adapt-table-header-cell/adapt-table-filters//input";
    String blueprintsName = "//adapt-table[@id='%s']//td[@data-testid='adapt-table_column-0_row-0']//div/div";
    String blueprintTableActionMenuDropdown = "//adapt-table[@id='%s']//button[@id='%s_dropdownMenu']";
    String blueprintTableActionMenuOption = "//adapt-dropdown-window2//button[contains(@class,'dropdown-item') and contains(normalize-space(),'%s')]";

    
    
    @FindBy(xpath = "//button[@id='blueprint-list-create']")
	public WebElement blueprintCreateNewBtn;

	@FindBy(xpath = "//input[@id='blueprint-attr-name']")
	public WebElement bluePrintName;

	@FindBy(xpath = "//input[@id='blueprint-attr-description']")
	public WebElement blueprintDescription;
	@FindBy(xpath = "//button[@id='footer-nextsstep']")
	public WebElement blueprintCreateNextBtn;
	@FindBy(xpath = "//div[@id='select-class-CandidateSoftwareInstance']")
	WebElement candidateSoftwareInstance;
	@FindBy(xpath = "//div[@id='select-class-CloudManagementGroup']")
	WebElement cloudManagementGroup;

	@FindBy(xpath = "//button[@id='footer-save']")
	WebElement saveButton;
	@FindBy(xpath = "//button[@id='footer-close']")
	WebElement closeButton;

	@FindBy(xpath = "//button[@id='footer-clear']")
	WebElement clearButton;

	String checkboxLocator = "//input[@type='checkbox']";

	@FindBy(xpath = "//button[@id='confirmation-yes-btn']")
	WebElement confirmSaveButton;

	@FindBy(xpath = "//span[@class='a-toast__details-text']")
	public WebElement toastMsg;

	@FindBy(xpath = "//th[@data-testid='adapt-table-2-th-name']//input[contains(@id,'rx-search')]")
	public WebElement serchFilterName;

	@FindBy(xpath = "//td//button[contains(. ,'Action')]")
	public WebElement actionButton;

	@FindBy(xpath = "//button[contains(. ,'Edit')]")
	public WebElement editButton;
    
    
    public AiopsServiceBlueprintPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    private List<WebElement> getWebElements(String xpath) {
        return findElements(By.xpath(xpath));
    }

    public String aiopsConfigBlueprintAction(String blueprintName, String action) {
        // Default values
        String blueprintVersion = "1.0.0";
        String mnuText = "";

        mnuText = aiopsConfigClickBlueprintAction(action);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

        return mnuText;
    }

    public void aiopsConfigClickShowHideBlueprint() {
        WebElement we = findElement(By.xpath(blueprintsShowHideBtn));
        commonWaits.waitForElementClickable(blueprintsShowHideBtn, commonWaits.defaultTimeout);
        if (we.getAttribute(SolQAConstants.areaChecked).equals("false")) {
            clickByAction(blueprintsShowHideBtn);
        }
    }

    public void aiopsConfigSearchBlueprintByColumnAndValue(String columnName, String searchValue) {
        String searchXpath = String.format(blueprintsTableColumnSearch, columnName);
        commonWaits.waitForElementClickable(findElement(By.xpath(searchXpath)));
        List<WebElement> elems = getWebElements(searchXpath);
        if (!elems.isEmpty()) {
            commonWaits.clearTextFromElement(elems.get(elems.size() - 1));
            commonWaits.typeKeys(elems.get(elems.size() - 1), searchValue);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    private String aiopsConfigClickBlueprintAction(String action) {
        String mnuText = "";
        String tableId = TABLES.get("blueprint_list_table");
        String dropdownXpath = String.format(blueprintTableActionMenuDropdown, tableId, 0);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        click(findElement(By.xpath(dropdownXpath)));

        String optionXpath = String.format(blueprintTableActionMenuOption, action);
        mnuText = findElement(By.xpath(optionXpath)).getText();
        click(findElement(By.xpath(optionXpath)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        String name = String.format(blueprintsName, tableId);
        mnuText = findElement(By.xpath(name)).getText();
        return mnuText;
    }
    public String createNewBlueprint() {
		log.info("In side Create the new blueprint Defination");
		commonWaits.waitForElementClickable(blueprintCreateNewBtn);
		click(blueprintCreateNewBtn);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
		String bluePrintNameValue = getBluePrintName();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
		setBluePrintDescription();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

		clickOnNextButton();
		addNewCIToBlueprint(candidateSoftwareInstance);
		log.info("CI Added in BluePrint");
		clickByAction(saveButton);
		setEnabledStatus(By.xpath(checkboxLocator), "enable");
		conformSaveButton();
		verifyAddTostSuccessMsg(toastMsg);
		clickByAction(closeButton);

		return bluePrintNameValue;
	}

	private AiopsServiceBlueprintPage setBluePrintDescription() {
		commonWaits.waitForElementClickable(blueprintDescription);
		String blueprintDescriptionValue = "TestDescription" + RandomStringUtils.randomAlphanumeric(10);
		commonWaits.waitForElementClickable(blueprintDescription).clear();
		commonWaits.waitForElementClickable(blueprintDescription).sendKeys(blueprintDescriptionValue);
		log.info("Set Description of new BluePrint");
		return this;
	}

	private String getBluePrintName() {
		commonWaits.waitForElementClickable(bluePrintName);
		String bluePrintNameValue = "TestBlueprint " + RandomStringUtils.randomAlphanumeric(5);
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForElementClickable(bluePrintName).clear();
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForElementClickable(bluePrintName).sendKeys(bluePrintNameValue);
		log.info("Set Name of new BluePrint");
		return bluePrintNameValue;
	}

	private AiopsServiceBlueprintPage verifyAddTostSuccessMsg(WebElement toastMsg) {
		// TODO Auto-generated method stub

		if (commonWaits.isElementPresent(toastMsg)) {
			String successMsg = commonWaits.waitForElementVisible((toastMsg)).getText().trim();
			log.info("Success Message:"+successMsg);
			Assert.assertTrue(successMsg.equalsIgnoreCase(SolQAConstants.successMsg),
					"BluePrint Not Saved Success fully");
		}
		return this;
	}
	

	private AiopsServiceBlueprintPage verifyEditTostSuccessMsg(WebElement toastMsg) {
		// TODO Auto-generated method stub

		if (commonWaits.isElementPresent(toastMsg)) {
			String successMsg = commonWaits.waitForElementVisible((toastMsg)).getText().trim();
			log.info("Success Message:"+successMsg);
			Assert.assertTrue(successMsg.equalsIgnoreCase(SolQAConstants.updateSuccessMsg),
					"BluePrint Not Saved Success fully");
		}
		return this;
	}

	private AiopsServiceBlueprintPage conformSaveButton() {
		// TODO Auto-generated method stub
		commonWaits.waitForElementClickable(confirmSaveButton);
		click(confirmSaveButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
		return this;
	}

	public String checkEnableStatus(By checkboxLocator) {
		log.info("Get the 'Service Blueprint Enabled' attribute of the checkbox");
		String ariaChecked = findElement(checkboxLocator).getAttribute(SolQAConstants.areaChecked);
		if ("true".equals(ariaChecked)) {
			return "enabled";
		} else {
			return "disabled";
		}
	}

	public AiopsServiceBlueprintPage setEnabledStatus(By checkboxLocator, String pStatus) {
		String status = checkEnableStatus(checkboxLocator);
		if ("enable".equalsIgnoreCase(pStatus)) {
			if ("disabled".equals(status)) {
				findElement(checkboxLocator).click();
			} else {
				log.info("Edit setting is already enabled");
			}
		} else {
			if ("enabled".equals(status)) {
				findElement(checkboxLocator).click();
			} else {
				log.info("Edit setting  is already disabled");
			}
		}
		return this;
	}

	public AiopsServiceBlueprintPage clickOnNextButton() {
		commonWaits.waitForElementClickable(blueprintCreateNextBtn);
		click(blueprintCreateNextBtn);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
		return this;
	}

	public AiopsServiceBlueprintPage addNewCIToBlueprint(WebElement ciComponent) {
		commonWaits.waitForElementClickable(ciComponent);
		click(ciComponent);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
		log.info("Add new CI of new BluePrint");
		return this;
	}

	public AiopsServiceBlueprintPage editBlueprint(String searchBluePrintName) {
		// TODO Auto-generated method stub
		commonWaits.waitForExecutionComplete();
		commonWaits.waitForElementVisible(actionButton,20).click();
		commonWaits.waitForElementVisible(editButton).click();
		commonWaits.waitForElementVisible(bluePrintName);
		log.info("--Search Key---"+searchBluePrintName.trim());
		commonWaits.waitForExecutionComplete();
		log.info("--On Edit Screen Name ---"+commonWaits.waitForElementVisible(bluePrintName).getAttribute("placeholder"));
		log.info("--Compare ---"+searchBluePrintName.trim().contains(bluePrintName.getAttribute("placeholder")));
		Assert.assertTrue(searchBluePrintName.contains(bluePrintName.getAttribute("placeholder")),
				"Service Blue print Not with same name");
		cleareContent();
		addNewCIToBlueprint(cloudManagementGroup);
		log.info(" other CI Added in BluePrint");
		clickOnNextButton();
		clickByAction(saveButton);
		commonWaits.waitForExecutionComplete();
		String enabledCheckButton = "//adapt-rx-switch//input[@type='checkbox']";
		log.info("Status****"+checkEnableStatus(By.xpath(enabledCheckButton)));
		Assert.assertTrue(checkEnableStatus(By.xpath(enabledCheckButton)).equalsIgnoreCase("enabled"),
				" CheckBox not set Enabled");
		conformSaveButton();
		verifyEditTostSuccessMsg(toastMsg);
		clickByAction(closeButton);
		return this;
	}

	private AiopsServiceBlueprintPage cleareContent() {
		// TODO Auto-generated method stub
		commonWaits.waitForElementClickable(clearButton);
		click(clearButton);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
		return this;
	}
}