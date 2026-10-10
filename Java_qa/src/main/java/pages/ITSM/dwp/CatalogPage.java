package pages.ITSM.dwp;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CatalogPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    // Constants
    String CATALOG_PATH = "/catalog";
    String TEMPLATE_REASON_TEXTAREA_LABEL = "Please enter the reason for the request";
    String REQUEST_NOW_BTN_XPATH = "//dwp-global-search-list-item[contains(.,'%s')]//span[contains(.,'Request now')]";
    String SET_TEXTFIELD_BYLABEL_XPATH = "//label[contains(.,'%s')]/../following-sibling::div//input";
    String catalogItems = "//*[@data-ta= 'cardFullViewAction']";
    String serviceCardByName = "//dwp-list-view[contains(@class,'card') and contains(.,'%s')]";
    String requestNowBtn = "//dwp-global-search-list-item[contains(.,'%s')]//span[contains(.,'Request now')]";
    String SET_DATE = "//label[contains(.,'%s')]/following::adapt-rx-datetime//input";
    String SET_TEXTFIELD_BYLABEL = "//label[contains(.,'%s')]/../following-sibling::div//input";
    String SET_TEXTAREA_BYLABEL = "//dwp-text-area-question-presenter[contains(.,'%s')]//..//textarea";
    String STATUS_DROPDOWN_CREATE_PAGE_XPATH = "//button[contains(.,'%s') and @role='option']";
    String ELEMENT_DROPDOWN_BY_LABEL_XPATH = "//label[contains(.,'%s')]//ancestor::dwp-question-control//button";
    String SET_DATE_XPATH = "//label[contains(.,'%s')]/following::adapt-rx-datetime//input";
    String SELECT_DROPDOWN_BY_LABEL_XPATH = "//button[@role='combobox' and contains(@aria-label,'%s')]";
    String SELECT_DROPDOWN_OPTION_BY_TEXT_XPATH = "//button[@role='option']//span[text()='%s']";
    private static final String SELECTED_USER_PILL_BY_LOGIN = "css=adapt-tag[data-ta='selectedItemTag%s']";
    private static final String CROSS_ICON_ON_SELECTED_USER_PILL = SELECTED_USER_PILL_BY_LOGIN + " button";
    //appears after click

    String REMOVE_ITEM_BUTTONS = "//*[contains(@class,'cart') or contains(@data-ta,'cart')]//button[contains(@aria-label,'Remove') or contains(normalize-space(.),'Remove')]";
    String BtnAfterGlobalSearch="//dwp-global-search-list-item[.//*[contains(normalize-space(.), '%s')]]//*[self::a or self::div][contains(normalize-space(.), '%s')]";
    String SERVICE_CARD_BY_NAME_XPATH = "//dwp-list-view[contains(@class,'card') and contains(.,'%s')]";

    @FindBy(xpath = "//*[contains(text(),'Request submitted') or contains(text(),'request submitted')]")
    WebElement requestSubmittedMessage;

    @FindBy(xpath = "//div[@role='search']//input")
    WebElement catalogSearch;

    @FindBy(xpath = "//div[@class='configurable-header__wrapper container']")
    WebElement checkoutHeading;

    @FindBy(xpath = "//span[contains(@data-ta,'catalogCardsSectionShowAllLink')]")
    WebElement showAllItemsLink;

    @FindBy(xpath = "//dwp-list-view[contains(@class,'card')]")
    WebElement serviceCard;

    @FindBy(xpath = "//span[contains(.,'Request now')]")
    WebElement requestService;

    @FindBy(xpath = "//button[contains(text(),'Submit')]")
    WebElement submitRequest;

    @FindBy(xpath = "//div[contains(@class,'colored-media-header')]//h2")
    WebElement serviceHeader;

    @FindBy(xpath = "//*[contains(text(),'All items and services')]/..//*[contains(text(),'Show all')]")
    WebElement showAllItemsFromAllItemsAndServices;

    @FindBy(xpath = "//button[contains(.,'Search')]")
    WebElement searchButtonHeader;

    @FindBy(xpath = "//button[contains(@class,'ModalButton')]")
    WebElement popUpOkButton;

    @FindBy(xpath = "//adapt-rx-search[@data-ta='searchToPick']//input")
    WebElement searchForPeopleInput;

    @FindBy(xpath = "//button[contains(@data-ta,'pickerButton')]")
    WebElement searchResults;

    @FindBy(xpath = "//label")
    List<WebElement> labelElements;

    @FindBy(xpath = "//div[contains(@class,'datepicker') or contains(@class,'calendar') or contains(@class,'modal')]//button[contains(@aria-label,'Close') or contains(.,'Close') or contains(@class,'close')]")
    WebElement closeDatePopupButton;

    @FindBy(xpath = "//label[contains(text(),'Summary')]")
    WebElement formIndicatorSummaryLabel;
    @FindBy(xpath = "//label")
    WebElement formIndicatorAnyLabel;
    @FindBy(xpath = "//dwp-question-control")
    WebElement formIndicatorQuestionControl;
    @FindBy(xpath = "//input[@type='text']")
    WebElement formIndicatorTextInput;
    @FindBy(xpath = "//textarea")
    WebElement formIndicatorTextarea;
    @FindBy(xpath = "//dwp-text-question-presenter")
    WebElement formIndicatorTextPresenter;
    @FindBy(xpath = "//dwp-text-area-question-presenter")
    WebElement formIndicatorTextareaPresenter;
    @FindAll({
            @FindBy(xpath = "//span[contains(text(),'Request for')]/following::a[contains(text(),'Edit')][1]"),
            @FindBy(xpath = "//*[contains(text(),'Request for')]/following::button[contains(.,'Edit')][1]"),
            @FindBy(xpath = "//a[contains(@class,'edit') and contains(.,'Edit')]"),
            @FindBy(xpath = "//*[contains(text(),'Request for')]//following::*[contains(@class,'edit') or @aria-label='Edit'][1]"),
            @FindBy(xpath = "//button[contains(.,'Edit') and contains(@aria-label,'Request for')]")
    })
    List<WebElement> requestForEditButtonCandidates;

    @FindAll({
            @FindBy(xpath = "//input[contains(@placeholder,'Search for people')]"),
            @FindBy(xpath = "//input[contains(@placeholder,'Search')]"),
            @FindBy(xpath = "//input[contains(@aria-label,'Search for people')]"),
            @FindBy(xpath = "//input[@type='search']")
    })
    List<WebElement> requestForSearchInputCandidates;

    @FindAll({
            @FindBy(xpath = "//*[@role='option']"),
            @FindBy(xpath = "//li[contains(@class,'option') or @role='option']"),
            @FindBy(xpath = "//*[contains(@class,'dropdown-item')]"),
            @FindBy(xpath = "//*[contains(@class,'option')]"),
            @FindBy(xpath = "//*[contains(@class,'result')]"),
            @FindBy(xpath = "//div[contains(@class,'menu')]//div[contains(@class,'item')]"),
            @FindBy(xpath = "//ul[@role='listbox']//li"),
            @FindBy(xpath = "//div[contains(@class,'search')]//following::div[contains(@class,'item')]")
    })
    List<WebElement> requestForResultCandidates;

    @FindBy(xpath = "//button[contains(@class,'search')]")
    WebElement searchButton;

    @FindBy(xpath = "//*[text()='Cart']//ancestor::button")
    WebElement cartButton;

    @FindBy(xpath = "//button[@data-ta='serviceProfileAddToCartButton']")
    WebElement ADD_TO_CART_BUTTON;

    @FindBy(css = "button[class*='toast__link']")
    WebElement VIEW_CART_BUTTON;

    @FindBy(xpath = "//button[contains(normalize-space(.),'Request') and (contains(normalize-space(.),'item') or contains(normalize-space(.),'Item'))]")
    WebElement REQUEST_ITEMS_BUTTON;

    @FindBy(xpath = "//button[@data-ta='requestsContainerActionsNextItemButton']")
    WebElement NEXT_ITEM_BUTTON;

    public CatalogPage() {
        log = new SolqaLogger(CatalogPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public CatalogPage openCatalogPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        String catalogPageURL = getDWPURL() + CATALOG_PATH;
        getDriver().get(catalogPageURL);
        return this;
    }

    public boolean verifyCatalogSection() {
        boolean flag = false;
        commonWaits.waitForByElementToBeVisible(By.xpath(catalogItems));
        List<WebElement> items = findElements(By.xpath(catalogItems));
        if (!items.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    public boolean isShowAllItemsLinkVisible() {
        return commonWaits.isElementPresent(showAllItemsFromAllItemsAndServices);
    }

    public CatalogPage openAllItemsPage() {
        commonWaits.waitForElementVisible(showAllItemsLink);
        commonWaits.clickWithoutInterception(showAllItemsLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public CatalogPage searchAndOpenService(String searchText) {
        log.info("Searching and opening details of " + searchText);
        commonWaits.waitForElementVisible(catalogSearch).sendKeys(searchText + Keys.ENTER);
         if (commonWaits.isElementPresent(searchButtonHeader)) {
             searchButtonHeader.click();
         }
       // handleTimeoutPopup();
        if (commonWaits.isElementPresent(By.xpath(String.format(serviceCardByName, searchText)))) {
            this.openService();
        } else if (isElementPresent(By.xpath(String.format(requestNowBtn, searchText)))) {
            log.info("Service with name " + searchText + " found in catalog. Requesting service now.");
        } else {
            log.info("Service with name " + searchText + " not found in catalog");
            Assert.fail("Service with name " + searchText + " not found in catalog");
        }
        return this;
    }

    public CatalogPage openService() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceCard, 20), "Service card is not visible");
        commonWaits.clickWithoutInterception(serviceCard);
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), requestService, 20)) {
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.clickWithoutInterception(requestService);
        }
        return this;
    }

    public CatalogPage clickOnRequestNow(String serviceName) {
        try {
            String requestNowXpath = String.format(REQUEST_NOW_BTN_XPATH, serviceName);
            commonWaits.waitForByElementToBeVisible(By.xpath(requestNowXpath)).click();
        } catch (Exception e) {
            String requestNowXpath = String.format(REQUEST_NOW_BTN_XPATH, serviceName);
            commonWaits.waitForElementClickable(findElement(By.xpath(requestNowXpath))).click();
        }
        return this;
    }

    public CatalogPage verifySrdTitle(String expectedTitle) {
        log.info("Checking if actual SRD title match expected");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), checkoutHeading, 30), "Checkout heading is not visible");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceHeader, 30), "Service Header is not visible");
        String serviceTitle = serviceHeader.getText();
        Assert.assertEquals(expectedTitle, serviceTitle, "Title is not matched as expected. Title is " + serviceTitle);
        return this;
    }

    public CatalogPage clickOnSubmitRequest() {
        try
    	{
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForSometime(5000);
        WebElement submitBtn = findClickableCheckoutSubmitButton();
        javaScriptScrollIntoView(submitBtn);
        commonWaits.clickWithoutInterception(submitBtn);
        commonWaits.waitForSometime(5000);
        }
        catch(Exception e)
        {
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        javaScriptScrollIntoView(submitRequest);
        commonWaits.clickWithoutInterception(submitRequest);
        commonWaits.waitForSometime(5000);
        }
        return this;
    }

    /**
     * Resolves the checkout Submit control. The old {@code //*[contains(text(),'Submit')]} matched the first node in
     * the tree (often not the real button or not interactable). Prefer explicit button text and footer placement.
     */
    private WebElement findClickableCheckoutSubmitButton() {
        String[] priorityXpaths = {
                "//button[contains(normalize-space(.),'Submit request')]",
                "//div[contains(@class,'footer')]//button[contains(normalize-space(.),'Submit')]",
                "//button[contains(@class,'btn-primary') and contains(.,'Submit')]",
                "//button[contains(text(),'Submit')]"
        };
        for (String xp : priorityXpaths) {
            for (WebElement el : getDriver().findElements(By.xpath(xp))) {
                try {
                    if (el.isDisplayed() && el.isEnabled()) {
                        return commonWaits.waitForElementClickable(el, 20L);
                    }
                } catch (StaleElementReferenceException e) {
                    // try next element
                }
            }
        }
        // Fallback: last matching Submit button is often the sticky checkout/footer action
        List<WebElement> anySubmit = getDriver().findElements(
                By.xpath("//button[contains(normalize-space(.),'Submit') and not(@disabled)]"));
        for (int i = anySubmit.size() - 1; i >= 0; i--) {
            try {
                WebElement el = anySubmit.get(i);
                if (el.isDisplayed() && el.isEnabled()) {
                    return commonWaits.waitForElementClickable(el, 20L);
                }
            } catch (StaleElementReferenceException e) {
                // try next index
            }
        }
        throw new RuntimeException("Checkout Submit button not found or not interactable (no displayed enabled button matched)");
    }

    private CatalogPage handleTimeoutPopup() {
        if (commonWaits.isElementPresent(popUpOkButton, 10)) {
            clickWithoutInterception(popUpOkButton);
        } else {
            log.info("No timeout popup displayed");
        }
        return this;
    }

    public CatalogPage addNameInService(String name) {
        fillInputFieldByLabel(SolQAConstants.NEW_NAME, name);
        return this;
    }

    public CatalogPage addMachineNameInService(String machineName) {
        fillInputFieldByLabel(SolQAConstants.MACHINE_NAME, machineName);
        return this;
    }

    public CatalogPage selectSoftwareRequired(String labelName, String softwareName) {
        log.info("Selecting " + labelName + ": " + softwareName);
        String dropdownXpath = String.format(SELECT_DROPDOWN_BY_LABEL_XPATH, labelName);
        String optionXpath = String.format(SELECT_DROPDOWN_OPTION_BY_TEXT_XPATH, softwareName);
        commonWaits.waitForByElementToBeVisible(By.xpath(dropdownXpath));
        commonWaits.clickWithoutInterception(By.xpath(dropdownXpath));
        commonWaits.waitForByElementToBeVisible(By.xpath(optionXpath));
        commonWaits.clickWithoutInterception(By.xpath(optionXpath));
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        log.info("Software selected: " + softwareName);
        return this;
    }

    private void fillInputFieldByLabel(String label, String text) {
        log.info("Filling SRD field labeled " + label + " with text '" + text + "'");
        keyClearAndType(String.format(SET_TEXTFIELD_BYLABEL_XPATH, label), text);
    }

    public CatalogPage setUrgency(String urgency) {
        click(String.format(ELEMENT_DROPDOWN_BY_LABEL_XPATH, SolQAConstants.URGENCY));
        String urgencyXpath = String.format(STATUS_DROPDOWN_CREATE_PAGE_XPATH, urgency);
        commonWaits.waitForByElementToBeVisible(By.xpath(urgencyXpath));
        commonWaits.clickWithoutInterception(By.xpath(urgencyXpath));
        return this;
    }

    public CatalogPage addMSOfficeInstallationTemplateDetails(Map<Object, String> templateData) {
        fillTextAreaByLabel(TEMPLATE_REASON_TEXTAREA_LABEL, templateData.get(SolQAConstants.REASON));
        setUrgency(templateData.get(SolQAConstants.URGENCY));
        return this;
    }

    public CatalogPage fillTextDateFieldByLabel(String label, String text) {
        commonWaits.waitForExecutionComplete();
        log.info("Filling SRD field labeled " + label + " with text '" + text + "'");
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(SET_DATE, label))));
        keyClearAndType(String.format(SET_DATE, label), text);
        return this;
    }

    public CatalogPage closeDateModalIfPresent() {
        try {
            if (commonWaits.isElementPresent(closeDatePopupButton, 3)) {
                commonWaits.clickWithoutInterception(closeDatePopupButton);
                return this;
            }
        } catch (Exception ignored) {
        }
        try {
            WebElement body = findElement(By.tagName("body"));
            body.sendKeys(Keys.ESCAPE);
        } catch (Exception ignored) {
        }
        return this;
    }

    public CatalogPage fillTextFieldByLabel(String label, String text) {
        commonWaits.waitForExecutionComplete();
        log.info("Filling SRD field labeled " + label + " with text '" + text + "'");
        commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(SET_TEXTFIELD_BYLABEL, label)), 20);
        log.debug("Clearing field and Typing text");
        commonWaits.waitForElement(By.xpath(String.format(SET_TEXTFIELD_BYLABEL, label))).clear();
        getDriver().findElement(By.xpath(String.format(SET_TEXTFIELD_BYLABEL, label))).sendKeys(text);
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public CatalogPage fillTextAreaByLabel(String label, String text) {
        commonWaits.waitForExecutionComplete();
        log.info("Filling SRD textarea labeled " + label + " with text '" + text + "'");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SET_TEXTAREA_BYLABEL, label)));
        log.debug("Clearing textarea and Typing text");
        commonWaits.waitForElement(By.xpath(String.format(SET_TEXTAREA_BYLABEL, label))).clear();
        getDriver().findElement(By.xpath(String.format(SET_TEXTAREA_BYLABEL, label))).sendKeys(text);
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public CatalogPage clickEditRequestForButton() {
        log.info("Clicking 'Edit' button to change 'Request for' user");
        commonWaits.waitForExecutionComplete();

        WebElement editButton = null;
        for (WebElement candidate : requestForEditButtonCandidates) {
            try {
                if (editButton == null && commonWaits.isElementPresent(candidate, 3)) {
                    editButton = candidate;
                    log.info("Found Edit button for 'Request for' using fallback candidate");
                }
            } catch (Exception ignored) {
            }
        }

        if (editButton == null) {
            String errorMsg = "Edit button for 'Request for' field not found";
            log.error(errorMsg);
            try {
                captureScreenshot("EditButtonNotFound_RequestFor", SolQAConstants.appDWP);
            } catch (Exception ignored) {
            }
            throw new AssertionError(errorMsg);
        }

        commonWaits.clickWithoutInterception(editButton);
        log.info("Clicked Edit button");
        commonWaits.waitForSometime(2000);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public CatalogPage searchAndSelectRequestForUser(String userName) {
            log.info("Searching in RequestAsSomeoneElseDialog: " + userName);
            commonWaits.waitForElementClickable(searchForPeopleInput).click();
            keyClearAndType(searchForPeopleInput, userName);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            commonWaits.waitForElementVisible(searchResults);
            Assert.assertTrue(commonWaits.isElementPresent(searchResults, 10), "Search results are not visible");
            commonWaits.clickWithoutInterception(searchResults);
            return this;
    }

    public CatalogPage waitForFormFieldsToLoad() {
        log.info("Waiting for form fields to load/refresh");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForSometime(5000); // Give form time to update after user change

        log.info("Current URL: " + getDriver().getCurrentUrl());
        log.info("Page title: " + getDriver().getTitle());

        // Check if at least one common form element is present (label, input, or textarea)
        boolean formReady = false;
        String foundIndicator = null;
        WebElement[] indicators = new WebElement[]{
                formIndicatorSummaryLabel,
                formIndicatorAnyLabel,
                formIndicatorQuestionControl,
                formIndicatorTextInput,
                formIndicatorTextarea,
                formIndicatorTextPresenter,
                formIndicatorTextareaPresenter
        };
        for (int i = 0; i < indicators.length; i++) {
            if (!formReady && commonWaits.isElementPresent(indicators[i], 5)) {
                log.info("Form indicator " + (i + 1) + " detected - form appears to be present");
                formReady = true;
                foundIndicator = "indicator " + (i + 1);
            }
        }

        if (!formReady) {
            log.error("⚠ Could not detect ANY form fields on page!");
            log.error("The page may have navigated away from the service request form");
            try {
                captureScreenshot("FormFieldsNotFound", SolQAConstants.appDWP);
            } catch (Exception ignored) {
            }
        } else {
            log.info("Form ready confirmed via " + foundIndicator);
        }

        // Log all labels found on page for debugging
        try {
            List<WebElement> allLabels = labelElements;
            log.info("Total labels found on page: " + allLabels.size());
            if (!allLabels.isEmpty()) {
                for (int i = 0; i < Math.min(allLabels.size(), 15); i++) {
                    try {
                        String labelText = allLabels.get(i).getText();
                        if (labelText != null && !labelText.trim().isEmpty()) {
                            log.info("  Available label: '" + labelText + "'");
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not list page labels: " + e.getMessage());
        }

        commonWaits.waitForExecutionComplete();
        log.info("Form fields load check completed");
        return this;
    }

    /**
     * Fill service request field by label name and submit the request
     * This method handles text input fields in service request forms,
     * clicks submit button and verifies the request was submitted successfully
     *
     * @param fieldLabel - The label text of the field (e.g., "Summary of your request")
     * @param value      - The value to fill in the field
     */
    public CatalogPage fillServiceRequest(String fieldLabel, String value) {
        commonWaits.waitForExecutionComplete();
        log.info("Filling service request field '" + fieldLabel + "' with value: " + value);

        try {
            // Wait for the field to be visible
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SET_TEXTFIELD_BYLABEL_XPATH, fieldLabel)));

            // Find and clear the field
            WebElement field = commonWaits.waitForElement(By.xpath(String.format(SET_TEXTFIELD_BYLABEL_XPATH, fieldLabel)));
            field.clear();

            // Enter the value
            field.sendKeys(value);
            log.info("Successfully filled field '" + fieldLabel + "' with value: " + value);

            // Click submit request button
            clickOnSubmitRequest();
            log.info("Clicked on Submit Request button");

            // Wait for and verify "Request submitted" message
            log.info("Waiting for 'Request submitted' message to appear...");
            commonWaits.waitForElementVisible(requestSubmittedMessage);

            Assert.assertTrue(requestSubmittedMessage.isDisplayed(), "Request submitted message is not displayed");
            log.info("Request submitted successfully - Success message verified");

        } catch (Exception e) {
            log.error("Failed to fill field '" + fieldLabel + "' or submit request: " + e.getMessage());
            throw new RuntimeException("Unable to fill service request field and submit: " + fieldLabel, e);
        }
        return this;
    }

    public CatalogPage searchAndOpenServiceProfile(String searchText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        log.info("Searching and opening service profile of " + searchText);

        if (!getDriver().getCurrentUrl().contains(CATALOG_PATH)) {
            openCatalogPage();
        }

        try {
            if (!catalogSearch.isDisplayed() && commonWaits.isElementPresent(searchButtonHeader)) {
                clickWithoutInterception(searchButtonHeader);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            }
        } catch (Exception ignored) {
        }

        commonWaits.waitForElementVisible(catalogSearch).clear();
        catalogSearch.sendKeys(searchText);
        catalogSearch.sendKeys(Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        handleTimeoutPopup();
        commonWaits.waitForSometime(5000);

        String serviceCardXpath = String.format(SERVICE_CARD_BY_NAME_XPATH, searchText);
        if (commonWaits.isElementPresent(By.xpath(serviceCardXpath), 5)) {
            commonWaits.clickWithoutInterception(By.xpath(serviceCardXpath));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            return this;
        }

        commonWaits.clickWithoutInterception(By.xpath(String.format(BtnAfterGlobalSearch, searchText, searchText)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public CatalogPage openCart() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(cartButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public CatalogPage removeAllItemsFromCart() {
        openCart();
        long start = System.currentTimeMillis();
        long timeoutMs = 60_000;
        while (System.currentTimeMillis() - start < timeoutMs) {
            List<WebElement> removeButtons = findElements(By.xpath(REMOVE_ITEM_BUTTONS));
            try {
                commonWaits.clickWithoutInterception(removeButtons.get(0));
            } catch (Exception e) {
                commonWaits.clickByJS(removeButtons.get(0));
            }
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
        return this;
    }

    public CatalogPage addToCart() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(ADD_TO_CART_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public CatalogPage viewCart() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(VIEW_CART_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return openCart();
    }

    public CatalogPage clickRequestItemsOnDialog() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(REQUEST_ITEMS_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public CatalogPage verifyCheckoutItemOpened(String expectedServiceName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            if (checkoutHeading != null && checkoutHeading.isDisplayed()) {
                Assert.assertTrue(checkoutHeading.getText().contains(expectedServiceName),
                        "Checkout item is not as expected. Expected to contain: " + expectedServiceName);
                return this;
            }
        } catch (Exception ignored) {
        }
        return verifySrdTitle(expectedServiceName);
    }

    public CatalogPage clickNextItemButton() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(NEXT_ITEM_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }
  
    public CatalogPage verifySrdTitleNotMatched(String expectedTitle) {
        log.info("Checking if actual SRD title match expected");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(checkoutHeading, 20);
        String serviceTitle = serviceHeader.getText();
        Assert.assertNotEquals(expectedTitle, serviceTitle, "Title is matched as expected. Title is " + serviceTitle);
        return this;
    }

    public CatalogPage clickCrossIconOnSelectedUserPill(String user) {
        log.info("Clicking cross icon on selected user pill");
        getElement(String.format(CROSS_ICON_ON_SELECTED_USER_PILL, user)).click();
        commonWaits.waitForExecutionComplete();
        return this;
    }
  
    public CatalogPage searchAndOpenServiceWithoutRequestNow(String searchText) {
        log.info("Searching and opening details of " + searchText);
        commonWaits.waitForElementVisible(catalogSearch).sendKeys(searchText + Keys.ENTER);
        if (commonWaits.isElementPresent(searchButtonHeader)) {
            searchButtonHeader.click();
        }
        if (commonWaits.isElementPresent(By.xpath(String.format(serviceCardByName, searchText)))) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceCard, 10), "Service card is not visible");
            commonWaits.clickWithoutInterception(serviceCard);
        } else {
            log.info("Service with name " + searchText + " not found in catalog");
            Assert.fail("Service with name " + searchText + " not found in catalog");
        }
        return this;
    }

}