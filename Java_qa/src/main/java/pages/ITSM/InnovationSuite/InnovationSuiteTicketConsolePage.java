package pages.ITSM.InnovationSuite;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class InnovationSuiteTicketConsolePage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    String btnLocator = "//button[contains(@data-testid,'%s')]";
    String filterHeader_selectFilterValue = "//div[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = translate('%s', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')]//ancestor::button";
    String fvalue = "//parent::div//following-sibling::div[contains(@class,'tab')]//*[contains(text(),'%s')]";
    String selectTextFilter_loc = "//parent::div//following-sibling::div[contains(@class,'tab')]//input";
    String iFrame = "//iframe[@id='pwa-frame']";
    String fieldValueFromViewPage = "//*[@id='ar%s_data']";
    String buttonClassLocator = "//button[contains(@class,'%s')]";
    String headerValues = "//div[contains(@class,'ui-table')]//th[contains(@data-testid,'field-id')]";
    String variableRows = "//div[contains(@class,'ui-table')]//*[@data-testid='adapt-table_column-%s_row-0']";
    public String columnNameUnhecked = "//label[contains(.,'%s')]//input[@aria-checked='false']//..//div";
    String studioFrame = "//iframe[@id='studio-frame']";
    
    //Locators
    String menuFilterButton = "adapt-af-2_menu";
    String clearFilterButton = "adapt-af-2_Clear";
    String applyFilterButton = "adapt-af-2_footer_apply";
    String removeFilterButton = "remove-btn";
    String dropDownButton = "dropdown-kabob-btn dropdown";

    @FindBy(xpath = "//button[contains(@class,'table-toolbar__btn-link') and contains(.,'Visible columns')]")
	public WebElement visibleColumnsButton;
    @FindBy(xpath = "//button[contains(. ,'Apply filters')]")
    WebElement applyFiltersBtn;
    @FindBy(xpath = "//a[contains(.,'Edit')]")
    WebElement editButton;
    @FindBy(xpath = "//button[contains(@class, 'advanced-filter__dropdown-anchor')]")
    WebElement filterBtn;
    @FindBy(xpath = "//button[contains(. ,'Clear')]")
    WebElement filterClearBtn;
    @FindBy(xpath = "//button[contains(. ,'Remove all')]")
    WebElement removeAllFiltersBtn;

    @FindBy(xpath = "//input[contains(@placeholder,'Filter')]")
    WebElement addVisibleColumnFilter;
    String  firstTicket = "(//*[contains(@class, 'rx-ellipsis')]//span[normalize-space()='%s'])[last()]";
    @FindBy(xpath = "//input[contains(@placeholder,'Search columns')]")
    public WebElement columnSearchBox;
    @FindBy(xpath = "//input[contains(@class,'adapt-rx-search__input')]")
    WebElement ticketSearchInput;
    @FindBy(xpath = "//button[contains(@class,'adapt-rx-search__search-button')]")
    WebElement ticketSearchButton;

    public InnovationSuiteTicketConsolePage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(InnovationSuiteTicketConsolePage.class.getName());
        commonWaits = new CommonWaits();
    }

    public InnovationSuiteTicketConsolePage clearFilter() {
        if (commonWaits.isElementPresent(removeAllFiltersBtn, 3)) {
            clickWithoutInterception(removeAllFiltersBtn);
        } else {
            log.info("Remove all filters button not present. Skipping clear filter step.");
        }
        return this;
    }

    public InnovationSuiteTicketConsolePage clickFilter() {
        log.info("Clicking on filter icon");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(filterBtn).click();
        return this;
    }

    public InnovationSuiteTicketConsolePage applyFilter() {
        log.info("Clicking on Apply Filters");
        WebElement applyFiltersElement = commonWaits.waitForElementVisible(applyFiltersBtn);
        if (applyFiltersElement.isEnabled()) {
            applyFiltersElement.click();
        } else {
            log.info("Apply Filters button is disabled. Skipping click.");
        }
        return this;
    }

    public InnovationSuiteTicketConsolePage applyFilterAndSearchTicket(String ticketType, String ticketID) {
        switchToStudioFrame();
        clickFilter();
        clearFilter();
        selectFilterValue(SolQAConstants.TICKET_TYPE_FILTER, ticketType);
        selectTextFilter(SolQAConstants.KEYWORDS_FILTER, ticketID);
        applyFilter();
        return this;
    }

    public InnovationSuiteTicketConsolePage searchByTicketId(String ticketId) {
        if (!commonWaits.isElementPresent(ticketSearchInput, 5)) {
            log.info("Ticket search input not present. Skipping search action.");
            return this;
        }

        log.info("Typing ticket id into search box: " + ticketId);
        keyClearAndType(ticketSearchInput, ticketId);

        if (!commonWaits.isElementPresent(ticketSearchButton, 5)) {
            log.info("Ticket search button not present.");
            return this;
        }
        clickWithoutInterception(ticketSearchButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public InnovationSuiteTicketConsolePage ClearFiler() {
        commonWaits.switchtoDefaultFrame();
        //switchToStudioFrame();
        clickFilter();
        clearFilter();
        applyFilter();
        return this;
    }

    public InnovationSuiteTicketConsolePage openFirstTicket(String ticketID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(firstTicket, ticketID))).click();
        return this;
    }

    public InnovationSuiteTicketConsolePage selectFilterValue(String filterType, String value) {
        String filterValue = String.format(filterHeader_selectFilterValue, filterType)
                + String.format(fvalue, value);
        if ("Published".equals(value)) {
            filterValue = String.format(filterHeader_selectFilterValue, filterType)
                    + String.format(fvalue, value);
        }

        if ("Closed".equals(value)) {
            filterValue = String.format(filterHeader_selectFilterValue, filterType)
                    + String.format(fvalue, value);
        }
        expandFilter(filterType);
        findElement(By.xpath(filterValue)).click();
        return this;
    }

    public InnovationSuiteTicketConsolePage selectTextFilter(String filterType, String text) {
        String filterTextbox = String.format(filterHeader_selectFilterValue, filterType) + selectTextFilter_loc;
        expandFilter(filterType);
        keyClearAndType(filterTextbox, text);
        findElement(By.xpath(filterTextbox)).sendKeys(Keys.ENTER);
        return this;
    }

    public InnovationSuiteTicketConsolePage expandFilter(String filterType) {
        log.info("Expanding filter: " + filterType);
        scrollToElement(findElement(By.xpath(String.format(filterHeader_selectFilterValue, filterType))));
        if (findElement(By.xpath((String.format(filterHeader_selectFilterValue, filterType)))).getAttribute(SolQAConstants.ATTRIBUTE_CLASS)
                .contains("collapsed")) {
            findElement(By.xpath((String.format(filterHeader_selectFilterValue, filterType)))).click();
        }
        return this;
    }

    public InnovationSuiteTicketConsolePage addVisibleColumn(String columnName) {
        log.info("Adding visible column: " + columnName);
        commonWaits.waitForElementVisible(visibleColumnsButton);
        clickWithoutInterception(visibleColumnsButton);
        if(commonWaits.isElementPresent(addVisibleColumnFilter)) {
            addVisibleColumnFilter.clear();
            addVisibleColumnFilter.sendKeys(columnName);
		} else {
			keyClearAndType(columnSearchBox, columnName);
		}

        if(isElementPresent(By.xpath(String.format(columnNameUnhecked, columnName)))) {
            log.info("Column " + columnName + " is not checked, checking it now.");
            clickWithoutInterception(By.xpath(String.format(columnNameUnhecked, columnName)));
        }
        return this;
    }

    public String getColumnValue(String columnName) {
        List<WebElement> values = findElements(By.xpath(headerValues));
        int index = 0;
        for( int i=0 ; i < values.size(); i++) {
            String headerText = values.get(i).getAttribute(SolQAConstants.ATTRIBUTE_ARIALABEL);
            if(headerText.equalsIgnoreCase(columnName)) {
                index = i;
                break;
            }
        }
       String variableText = findElement(By.xpath(String.format(variableRows, index))).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
       log.info("Value of column " + columnName + " is: " + variableText);
       return variableText;
    }

    public InnovationSuiteTicketConsolePage clickOnEditButton() {
        log.info("Clicking on Edit button");
        clickWithoutInterception(findElement(By.xpath(String.format(buttonClassLocator, dropDownButton))));
        clickWithoutInterception(editButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
		return this;
    }

    public InnovationSuiteTicketConsolePage switchToStudioFrame() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(studioFrame));
        return this;
    }


    
}
