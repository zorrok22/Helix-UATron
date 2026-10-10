package pages.ITOM.Aiops;

import framework.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.io.IOException;
import java.util.List;

public class CreateServicePage extends BasePage {

    CommonWaits commonWaits;

    static SolqaLogger log = new SolqaLogger(CreateServicePage.class.getName());

    public CreateServicePage() {
        PageFactory.initElements(getDriver(),this);
        commonWaits = new CommonWaits();
    }

    @FindBy(xpath = "//button[@id='footer-saveclose']")
    List<WebElement> saveCloseElements;

    @FindBy(xpath = "//button[@id='ci-search-add']")
    WebElement add_ci_btn;

    @FindBy(xpath = "//button[@id='ci-search-close']")
    WebElement addci_close_btn;

    @FindBy(xpath = "//button[@id='service-node-saveclose']")
    WebElement service_node_saveclose;

    @FindBy(xpath = "//button[@id='blueprint-properties-saveclose']")
    WebElement blueprint_saveclose;

    @FindBy(xpath = "//button[@id='element-search-select-dropdown-1']")
    WebElement addci_type_dropdown;

    @FindBy(xpath = "//*[@testid='element-search-text']")
    WebElement searchBox;

    @FindBy(xpath = "//button[@id='element-search-select-dropdown-2']")
    WebElement addci_kind_dropdown;

    @FindBy(xpath = "//input[@id='include-related']")
    WebElement addci_add_incl_related_chk;

    @FindBy(xpath = "//input[@id='include-related']/following-sibling::div")
    WebElement addci_add_incl_related_chk_click;

    String blueprint_close_btn = "//button[@id='footer-close']";
    String blueprint_save_btn ="//button[@id='footer-save']";
    String aiopsSpinner = "//div[contains(@class,'ng-busy')]";
    String table_checkboxes =  "//adapt-row-selection-checkbox/div";
    String addci_type_option = "//div[@id='element-search-select-dropdown-1_popup']//*[text()='%s']";
    String addci_kind_option = "//div[@id='element-search-select-dropdown-2_popup']//*[text()='%s']";
    String table_column_search_input = "//adapt-table[@id='add-cis-table']//th[@aria-label='%s']//input[contains(@id,'search')]";
    String table_column_clear_search_inputs = "//adapt-table[@id='add-cis-table']//th[@aria-label='Name']//button[contains(@class, 'clear') or contains(@data-testid, 'clear')]";
    String selectcis_add_ci_btn = "//button[@id='topology-add-cis']";

    /**
     * Clicks the add existing CI's button on Select CI Page
     */
    public void clickAddCIsButton() {
        log.info("Clicks the add existing CI's button on Select CI Page");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        commonWaits.waitForElementClickable(findElement(By.xpath(selectcis_add_ci_btn)));
        clickByJS(findElement(By.xpath(selectcis_add_ci_btn)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    /**
     * Clicks the close button on Add CI Page
     */
    public void clickCloseButton() {
        log.info("Clicks the close button on Add CI Page");
        commonWaits.waitForElementClickable(addci_close_btn).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    /**
     * Clicks the Add button on Add CI Page
     */
    public void clickAddButton() {
        log.info("Clicks the Add button on Add CI Page");
        commonWaits.waitForElementClickable(add_ci_btn).click();
    }

    public void aiopsBlueprintClickSavencloseBtn() {
        commonWaits.waitForElementVisible(saveCloseElements.get(0));
        click(saveCloseElements.get(saveCloseElements.size() - 1));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    public void aiopsDefineserviceClickSavencloseService() {
        commonWaits.waitForElementClickable(service_node_saveclose);
        click(service_node_saveclose);
    }

    public void blueprintClickSavencloseService() {
        commonWaits.waitForElementClickable(blueprint_saveclose);
        click(blueprint_saveclose);
    }

    public void selectCIType(String ciType) {

        commonWaits.waitForElementVisible(searchBox);
        commonWaits.waitForElementClickable(addci_type_dropdown);
        clickByAction(addci_type_dropdown);

        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(addci_type_option, ciType))));
        clickByAction(findElement(By.xpath(String.format(addci_type_option, ciType))));
    }

    /**
     * This method is used to select CI by Kind in DSM
     * @param ciKind CI Kind like "Namespace" or "Deployment" etc
     */
    public void selectCIKind(String ciKind) {

        commonWaits.waitForElementClickable(addci_kind_dropdown);
        String currentlySelectedKind = addci_kind_dropdown.getText();

        if (!currentlySelectedKind.equals(ciKind)) {
            clickByAction(addci_kind_dropdown);
            commonWaits.waitForElementClickable(findElement(By.xpath(String.format(addci_kind_option, ciKind))));
            clickByAction(findElement(By.xpath(String.format(addci_kind_option, ciKind))));
        }
    }

    /**
     * This method is used to search CI by column name and text on Add CI Page
     * @param columnName Column name to use for CI search
     * @param searchValue Search text with respective column
     * @param ciIndexToSelect Index to select from searched list of CIs
     */
    public void searchCIByColumn(String columnName, String searchValue, int ciIndexToSelect) {
        String[] allSearchText = searchValue.split(",");
        for (String searchText : allSearchText) {
            List<WebElement> searchInputs = getDriver().findElements(By.xpath(String.format(table_column_search_input, columnName)));

            if (!searchInputs.isEmpty()) {
                WebElement lastInput = searchInputs.get(searchInputs.size() - 1);
                lastInput.sendKeys(searchText);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
                selectCheckboxInDropdownByIndex(table_checkboxes, ciIndexToSelect);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
            } else {
                try {
                    captureScreenshot("failed_screenshot", "ITOM");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * This method is used to click the "Add Incl related" button on Add CI Page
     */
    public void clickAddIncludeRelatedButton(boolean action) {
        try {
            commonWaits.waitForElementClickable(addci_add_incl_related_chk);
            String isChecked = addci_add_incl_related_chk.getAttribute( "aria-checked");
            if (!isChecked.equals(action)) {
                click(addci_add_incl_related_chk_click);
            }
        } catch (Exception e) {
            System.out.println("Include related checkbox not found: " + e.getMessage());
        }
    }

    /**
     * This method is used to clear a search text from search input of given field
     * @param fieldName Field / column name from table with search field like "Name", "Type" etc
     */
    public void clearSearchText(String fieldName) {
        List<WebElement> clearButtons = getDriver().findElements(By.xpath(String.format(table_column_clear_search_inputs, fieldName)));
        if (!clearButtons.isEmpty()) {
            WebElement lastClearButton = commonWaits.waitForElementClickable(clearButtons.get(clearButtons.size() - 1));
            commonWaits.waitForElementClickable(lastClearButton);
            clickByAction(lastClearButton);
        }
    }

    private void selectCheckboxInDropdownByIndex(String locator, int index) {
        List<WebElement> checkboxes = getDriver().findElements(By.xpath(locator));
        if (index < checkboxes.size()) {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
            commonWaits.waitForElementClickable(checkboxes.get(index)).click();
        }
    }

    public void aiopsBlueprintClickSaveBtn() {
        log.info("Wait for loading icon to disappear");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

        log.info("Get web elements using the blueprint save button locator");
        List<WebElement> elements = getDriver().findElements(By.xpath(blueprint_save_btn));

        log.info("Click the last element");
        click(elements.get(elements.size() - 1));

        log.info("Wait for loading icon to disappear again");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    public void aiopsBlueprintClickCloseBtn() {
        commonWaits.waitForElementClickable(getDriver().findElements(By.xpath(blueprint_close_btn)).get(0));
        List<WebElement> elems = getDriver().findElements(By.xpath(blueprint_close_btn));
        click(elems.get(elems.size() - 1));
    }
}

