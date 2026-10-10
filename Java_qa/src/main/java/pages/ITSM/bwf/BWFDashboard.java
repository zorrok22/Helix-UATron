package pages.ITSM.bwf;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.*;

public class BWFDashboard extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;
    BWFLiveChat liveChat;
    CommonMethods commonMethods;
    BWFCase bwfCase;
    HelixGPTUtil helixGPTUtil = new HelixGPTUtil();
   

    //String locators
    public String txtBox_searchGrid = "//input[starts-with(@id,'rx-search')]";
    public String compactNavigation = "//button[contains(@class,'a-hamburger ng-tns') and not(@disabled)]";
    public String menuGroupItemsParent_VerticalMenu = "//ul[contains(@class,'a-hamburger__menu')]//li//a";
    public String menuGroupItems_VerticalMenu = "//button[contains(@class,'a-hamburger__menu-link btn-link ng-tns')]";
    public String menuGroupItemsChild_VerticalMenu = "//button[contains(@class,'a-hamburger__menu-link btn-link')]";
    public String menuGroupItems = "//button[contains(@class,'a-menu__link btn btn-link')]";
    public String menuGroupItemsParent = "//button[contains(@class,'adapt-menu__link btn btn-link')]";
    public String menuGroupItemsChild = "//*[contains(@class,'adapt-submenu__link')]";
    public String bwfCases = "//p//span[text()='Cases']";
    public String selectLob_fromDrpDown = "//*[@aria-labelledby= 'dropdownMenu']/button[normalize-space()='%s']";
    public String gridCellValue = "//tr[%d]//td[contains(@class,'at-data-cell at-cell')][%d]";
    public String gridCellValueLink = "//tr[%d]//td[contains(@class,'at-data-cell at-cell')][%d]//a";
    public String activityTab = "//*[@class='activity__body']//*[contains(@class,'mb-2')]";
    public String bwfFrame = "//iFrame[contains(@srcdoc, '%s')]";
    public String ActualUserNameLocator = "//div[contains(@class,'activity__body')]//a[contains(text(),'%s')]";
    public String availableFilter = "//*[contains(@class,'tag-active')]";
    public String searchTextField = "//*[starts-with(@id,'rx-search')]";
    public String gridRow = "//tr[contains(@class,'cdk-drag at-row at-data-row')]";
    public String dataRow = "//tr[contains(@class,'data-row')]";
    public String columnHeaders = "//th[contains(@class, 'at-header-cell at-header-data-cell')]";
    public String columnName = "span.c-header__separator";
    public String applicationName = "//adapt-nav-action-subitem/button[normalize-space()='%s']";
    public String searchedSetting = "//div[contains(@role,'treeitem') and contains(.,'%s')]";
    public String linkTextLocator = "//a[contains(., '%s')]";
    public String SearchToggaleButton = "//*[normalize-space()='%s' and contains(@class,'active')]";
    public String searchDropdown = "//bwf-search-category//button[contains(@class, 'dropdown-toggle')]";
    public String optionFromSearchDropdown_GPT = "//button[contains(@class,'rx-select__option')]//*[text()='%s']";
    public String searchTextBox_GPTSearch = "//input[contains(@id,'rx-search')]";
    public String resultListElement = "((//bwf-search-result-item-card)[%s]/div//div/div)[%s]";
    public String previewTicketId = "//*[@title='%s']";
    public String previewTicketNameOnArticle = "//*[@rx-view-component-id='05394582-02a7-4211-823e-9230fe094aa9']//h3[text()='%s']";
    public String previewTicketNameOnCase = "//*[@rx-view-component-id='f66afc99-20a8-4563-a989-d474f5cbfe4e']//div[contains(.,'%s')]";
    public String ToggleButton = "//*[normalize-space()='%s']";
    public String upload_BWFService = "//*[starts-with(@class,'sb-grid-button')]//input[@type='file']";
    public String scriptToMakeFileElementVisible = "document.querySelector('input[type=file]').setAttribute('style', 'display:inline !important; visibility:visible !important');";
    public String activityAttachPin = "//button[@id='upload-attachmentBtn']";
    public String activityFileUploader = "//adapt-rx-uploader/input[@type='file']";
    public String activityUploadBtn = "//button[@id='upload-attachment-button']";
    public String activityAttachmentFileName = "//span[contains(@class,'bwf-attachment-container__file-name') and contains(.,'%s')]";
    public String anyTextareaInActivityFeed = "//div[contains(@class,'activity-feed')]//textarea";
    public String anyContentEditableElement = "//*[@contenteditable='true']";
    String XPATH_ACTIVITY_ATTACH_PIN_ARIA_LABEL = "//button[contains(@aria-label,'attachment') or contains(@aria-label,'Attach') or contains(@aria-label,'Upload')]";
    String XPATH_ACTIVITY_ATTACH_PIN_ID_VARIANT = "//button[contains(@id,'attachment') and (contains(@id,'upload') or contains(@id,'attach'))]";
    String XPATH_ACTIVITY_ATTACH_PIN_CLASS_VARIANT = "//button[contains(@class,'attachment') or contains(@class,'paperclip')]";
    String XPATH_ACTIVITY_FILE_INPUT_SHADOW_VARIANT = "//adapt-rx-uploader//input[@type='file']";
    String XPATH_ACTIVITY_FILE_INPUT_ANY = "//input[@type='file']";
    String XPATH_BODY = "//body";
    String attachmentInSocialFeed = "//span[contains(@class,'bwf-attachment-container__file-name') and contains(text(),'%s')]";


    public final By[] activityAttachPinCandidates = new By[]{
            By.xpath(activityAttachPin),
            By.xpath(XPATH_ACTIVITY_ATTACH_PIN_ARIA_LABEL),
            By.xpath(XPATH_ACTIVITY_ATTACH_PIN_ID_VARIANT),
            By.xpath(XPATH_ACTIVITY_ATTACH_PIN_CLASS_VARIANT)
    };
    public final By[] activityFileInputCandidates = new By[]{
            By.xpath(activityFileUploader),
            By.xpath(XPATH_ACTIVITY_FILE_INPUT_SHADOW_VARIANT),
            By.xpath(XPATH_ACTIVITY_FILE_INPUT_ANY),
            By.cssSelector("input[type='file']")
    };
    public final String scriptToMakeSpecificFileElementVisible = "arguments[0].setAttribute('style', 'display:inline !important; visibility:visible !important');";
    public String caseFieldValueByLabel = "(//label[contains(normalize-space(),'%s')])[last()]//following-sibling::div";
    public String expandMenuLocator = "//div[contains(@role,'treeitem') and contains(.,'%s')]//preceding-sibling::span[contains(@class,'tree__toggle')]";
    public String settingsNameLocator = "//div[contains(@role,'treeitem')]//*[text()='%s']";
    public String dataRowCellValue = "//tr[contains(@class,'at-data-row')][%d]//td[contains(@class,'at-data-cell')][%d]";
    public String dataRowCellLink = "//tr[contains(@class,'at-data-row')][%d]//td[contains(@class,'at-data-cell')][%d]//a";
    public String menuItemButton = "//button[contains(@class,'adapt-menu__link') and .//span[contains(@class,'a-menu__text') and normalize-space()='%s']]";
    public String submenuItemLink = "//a[@role='menuitem' and contains(@class,'adapt-submenu__link') and .//span[contains(@class,'a-menu__text') and normalize-space()='%s']]";
    String activityFileInput = "//*[@class = 'attachment-button']//input[@type='file']";

    //WebElement locators
    @FindBy(xpath = "//*[contains(@placeholder,'Add a note')]")
    public WebElement addNote;
    @FindBy(xpath = "//*[contains(@aria-label,'Rich text editor') and @contenteditable='true']")
    WebElement activityNotesTextArea;
    @FindBy(xpath = "//ul[@role='menu' and contains(@class,'a-dropdown-menu')]")
    WebElement submenuOverlay;

    @FindBy(xpath = "//bwf-lob-selection//button")
    WebElement changeLOBValueDropdown;

    @FindBy(xpath = "//bwf-lob-selection//button/span")
    WebElement bwfLOBSelection;

    @FindBy(xpath = "//*[@id= 'dropdownMenu']")
    WebElement changeLOBValueDropdownValues;

    @FindBy(xpath = "//rx-filter-tags//button")
    WebElement clearFilter;

    @FindBy(xpath = "//button[contains(@data-testid,'button')]")
    WebElement searchButton;

    @FindBy(xpath = "//*[@class='activity__body']")
    WebElement generalBodyNotes;

    @FindBy(xpath = "//*[contains(@placeholder,'Add a note')]")
    WebElement activityNotebox;

    @FindBy(xpath = "//div[contains(@class, 'activity-feed')]//input[contains(@class,'checkbox')]/../div/span[text()='Public']")
    WebElement chkBoxPublicComment;

    @FindBy(xpath = "//*[@class = 'activity-feed-note-buttons__right']//button[1]")
    WebElement btnActivityNotesPost;

    @FindBy(xpath = "//ul[contains(@class,'a-hamburger__menu')]//li/a[contains(.,'Reports')]")
    public WebElement reportsLeftNavTab;

    @FindBy(xpath = "//button[contains(. ,'Reports')]")
    public WebElement reportsTab;

    @FindBy(xpath = "//a[contains(@href, 'Manage Catalog')]")
    public WebElement manageCatalogLink;

    @FindBy(xpath = "//ul[contains(@class,'a-hamburger__menu')]//li/a[contains(.,'Manage Catalog')]")
    public WebElement manageCatalogLeftNavLink;

    @FindBy(xpath = "//button[normalize-space()='Services']")
    public WebElement menuServices;

    @FindBy(xpath = "//button[@aria-label='Application launcher']")
    public WebElement applicationLauncher;

    @FindBy(xpath = "//a[contains(@href, 'settings')]")
    public WebElement settingsLink;

    @FindBy(xpath = "//input[contains(@class,'rx-search__input')]")
    WebElement searchFilterOnSettings;

    @FindBy(xpath = "//button[@aria-label='Configuration Value']")
    WebElement knowledgeSourceConfigValueButton;

    @FindBy(xpath = "//input[contains(@class,'rx-search__input') and @placeholder='Search']")
    WebElement searchTextFieldOnSettings;

    @FindBy(xpath = "//button[contains(.,'Cancel')]")
    WebElement cancelButtonOnSettings;

    @FindBy(xpath = "//button[@aria-label='True']")
    WebElement knowledgeCuratorTrueButton;

    @FindBy(xpath = "//button[contains(.,'Save') and not(@disabled)]")
    WebElement saveButtonOnSettings;

    @FindBy(xpath = "//button[contains(.,'HelixGPT') and contains(@role,'tab')]")
    WebElement helixGPTTab;

    @FindBy(xpath = "//button[@aria-label='Next']")
    WebElement nextButton;

    @FindBy(xpath = "//a[contains(.,' HelixGPT for Global Search ')]")
    WebElement helixGPTGlobalSearchLink;

    @FindBy(xpath = "//a[contains(@class,'d-icon-gear') and contains(@href, '/settings')]")
    WebElement settingsMenu;

    @FindBy(xpath = "//adapt-nav-actions//span[text()='Search']/parent::button")
    public WebElement SearchIcon;

    @FindBy(xpath = "//button[.. and normalize-space()='Chat']")
    public WebElement chatOption;

    @FindBy(xpath = "//div[@class='input-group']//textarea")
    public WebElement TextArea;

    @FindBy(xpath = "//button[contains(@class,'send-message')]")
    public WebElement sendButtonIcon;

    @FindBy(xpath = "//button[contains(@id,'rx-select')]")
    WebElement searchDropdown_GPT;

    @FindBy(xpath = "//button[contains(@class, 'btn btn-secondary')]/span[@class='d-icon-search ng-star-inserted']")
    WebElement searchIcon_GPT;

    @FindBy(xpath = "//h1[contains(@class,'bwf-search-result')]//span")
    WebElement searchResultCount_GPT;

    @FindBy(xpath = "//*[@rx-view-component-id='22e123f0-1628-4709-89f4-e61caa46254a']//button")
    WebElement goToUserProfileOnGPT;

    @FindBy(xpath = "//iframe[contains(@srcdoc,'FMLA')]")
    WebElement FMLAFrameOnGPT;

    @FindBy(xpath = "//*[@rx-view-component-id='5befc323-d82d-4b05-88e3-22f6c07ffddb']//button")
    WebElement goToKnowledgeArticleOnGPT;

    @FindBy(xpath = "//*[@rx-view-component-id='2b90aea1-99c8-4873-b465-5927cc911c6e']//button")
    WebElement goToCaseOnGPT;

    @FindBy(xpath = "//div[contains(@data-testid,'helixGPTChatMessage')]")
    public List<WebElement> allMessages;

    @FindBy(xpath = "//adapt-dropdown-window2//button//adapt-highlight")
    List<WebElement> searchDropdownValues;

    @FindBy(xpath = "//div[@class = 'modal-content']/div")
    public WebElement importHandlingPopup;

    @FindBy(xpath = "//label[@class= 'radio']/span[contains(text() ,'Source file')]")
    public WebElement sourceFileStatus;

    @FindBy(xpath = "//div[text()='Workflows']/parent::div//label[@class= 'radio']/span[contains(text() ,'Overwrite')]")
    public WebElement overwriteWorkflows;

    @FindBy(xpath = "//div[text()='Document Definition']/parent::div//label[@class= 'radio']/span[contains(text() ,'Overwrite')]")
    public WebElement overwriteDocumentDefinition;

    @FindBy(xpath = "//button[contains(.,'Confirm')]")
    public WebElement confirmButton;

    @FindBy(xpath = "//div[@ng-switch= 'message.enableHtml']/div")
    public WebElement successMsg;

    @FindBy(xpath = "//button[@title='Refresh']")
    public WebElement refreshButton;

    @FindBy(xpath = "//span[@class='btn-content']//i[@class='d-icon-plus']")
    public WebElement attachmentIcon;

    public BWFDashboard() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(BWFDashboard.class.getName());
        commonWaits = new CommonWaits();
        liveChat = new BWFLiveChat();
        commonMethods = new CommonMethods();
    }

    public WebElement findFileInputDeep() {
        try {
            Object res = executeJS(
                    "return (function(){"
                            + "function find(root){"
                            + "  if(!root) return null;"
                            + "  const el = root.querySelector(\"input[type='file']\");"
                            + "  if(el) return el;"
                            + "  const nodes = root.querySelectorAll('*');"
                            + "  for(const n of nodes){"
                            + "    if(n && n.shadowRoot){"
                            + "      const found = find(n.shadowRoot);"
                            + "      if(found) return found;"
                            + "    }"
                            + "  }"
                            + "  return null;"
                            + "}"
                            + "return find(document);"
                            + "})();"
            );
            return (res instanceof WebElement) ? (WebElement) res : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public WebElement findFileInputInContext() {
        for (By input : activityFileInputCandidates) {
            try {
                if (commonWaits.isElementPresent(input)) {
                    return findElement(input);
                }
            } catch (Exception ignored) {
            }
        }
        return findFileInputDeep();
    }

    public void closeFilePickerIfOpen() {
        try {
            log.info("Attempting to close any open file picker dialog");
            Actions actions = new Actions(getDriver());
            actions.sendKeys(Keys.ESCAPE).perform();
            commonWaits.waitForSometime(500);
        } catch (Exception e) {
            log.debug("No file picker to close or error closing: " + e.getMessage());
        }
    }

    public void verifyLoginToBWF() {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWFA);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(txtBox_searchGrid), 30), "Login is not Successful or BWF dashboard did not load within expected time");
    }

    public void openLiveChat() {
        log.info("opening live chat");
        navigateToLivechat();
    }

    public void navigateToLivechat() {
        clickSubMenuItem(SolQAConstants.LIVE_CHAT, SolQAConstants.AGENT_CONSOLE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public void clickSubMenuItem(String menuGroupName, String menuItemName) {
        WebElement groupElement = null;
        if (!getDriver().findElements(By.xpath(compactNavigation)).isEmpty()) {
            log.info("---Vertical menu---");
            getDriver().findElement(By.xpath(compactNavigation)).click();
            groupElement = getDriver().findElements(By.xpath(menuGroupItems_VerticalMenu)).stream().filter((element) -> element.getText().equals(menuGroupName)).findFirst().orElse(null);

            if (groupElement == null) {
                log.info("---Looking for more menu items in Vertical Parent Menus");
                commonWaits.waitForByElementToBeVisible(By.xpath(menuGroupItemsParent_VerticalMenu));
                groupElement = getDriver().findElements(By.xpath(menuGroupItemsParent_VerticalMenu)).stream().filter((element) -> {
                    if (!(element.getText().trim().equals(menuGroupName))) {
                        commonWaits.waitForSometime(commonWaits.moreDelay);
                    }
                    return element.getText().trim().equals(menuGroupName);
                }).findFirst().orElseThrow(() -> new AssertionError(String.format("----There is no menu item with '%s' name", menuGroupName)));
            }
            (new Actions(getDriver())).moveToElement(groupElement).click().perform();

            getDriver().findElements(By.xpath(menuGroupItemsChild_VerticalMenu)).stream().filter((element) -> element.getText().trim().equals(menuItemName)).findFirst().orElseThrow(() -> new AssertionError(String.format("There is no menu item with '%s' name", menuItemName))).click();
        } else {
            log.info("--- Horizontal menu---");
            WebElement horizontalgroupElement = getDriver().findElements(By.xpath(menuGroupItems)).stream().filter((element) -> element.getText().trim().equals(menuGroupName)).findFirst().orElse(null);

            if (horizontalgroupElement == null) {
                log.info("---Looking for more menu items in Horizontal Parent Menus");
                horizontalgroupElement = getDriver().findElements(By.xpath(menuGroupItemsParent)).stream().filter((element) -> {
                    if (!(element.getText().trim().equals(menuGroupName))) {
                        commonWaits.waitForSometime(commonWaits.moreDelay);
                    }
                    return element.getText().trim().equals(menuGroupName);
                }).findFirst().orElseThrow(() -> new AssertionError(String.format("----There is no menu item with '%s' name", menuItemName)));

            }
            (new Actions(getDriver())).moveToElement(horizontalgroupElement).click().perform();
            horizontalgroupElement.findElements(By.xpath(menuGroupItemsChild)).stream().filter((element) -> {
                if (!(element.getText().trim().equals(menuGroupName))) {
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                }
                return element.getText().equals(menuItemName);
            }).findFirst().orElseThrow(() -> new AssertionError(String.format("There is no menu item with '%s' name", menuItemName))).click();
        }
    }

    public void verifyLiveChatOpened() {
        Assert.assertTrue(liveChat.verifyLiveChatFrameOpened(), "----Live Chat Frame did not open as expected");
    }

    public String getLiveAgentName() {
        return liveChat.getAgentName();
    }

    public void clickQueueLink() {
        log.info("opening queue link");
        liveChat.clickQueueLink();
    }

    public void selectCustomerFromQueue(String message, String customerName) {
        log.info("Select customer from the queue: " + customerName);
        liveChat.selectTheCustomerFromChatQueue(message, customerName);
    }

    public void clickAcceptChat() {
        log.info("Accept live chat");
        liveChat.clickOnAcceptChat();
    }

    public void verifyBWFPageLaunchedSuccessfully() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        String casesText = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(bwfCases))).getText().trim();
        Assert.assertEquals(casesText, "Cases", "Business Workflows application is not launched successfully");
    }

    public void setLineOfBusiness_CaseConsole(String lobValueToSet) {
        log.info("Changing LOB value on BWF case console..");
        boolean isPresent = false;
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), bwfLOBSelection, 30), "BWF LOB selection is not visible");
        commonWaits.waitForElementVisible(bwfLOBSelection);
        String selectedLOBValue = bwfLOBSelection.getText().trim();
        log.info("---- Already selected LOB value is - " + selectedLOBValue);

        if (selectedLOBValue.equalsIgnoreCase(lobValueToSet)) {
            log.info("-- LOB value is already selected to expected LOB -" + lobValueToSet);

        } else {
            try {
                commonWaits.waitForElementVisible_withFluentWait(getDriver(), changeLOBValueDropdown, commonWaits.moreDelay);
                commonWaits.waitForElementClickable(changeLOBValueDropdown, commonWaits.minDelay);
                isPresent = true;
            } catch (Exception e) {
                log.error("Exception: " + e.getMessage());
            }
            if (!isPresent) {
                log.info("LOB field is not editable");
            } else {
                String currentLob = changeLOBValueDropdown.getText().trim();
                log.info("Current lob is set to: " + currentLob);
                log.info("Changing LOB value to: " + lobValueToSet);
                try {
                    commonWaits.waitForElementVisible(changeLOBValueDropdown).click();
                } catch (StaleElementReferenceException | ElementClickInterceptedException ex) {
                    commonWaits.waitForElementVisible(changeLOBValueDropdown).click();
                }
                commonWaits.waitForElementVisible(changeLOBValueDropdownValues);
                WebElement element = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectLob_fromDrpDown, lobValueToSet)));
                commonWaits.clickByJS(element);
                log.info("Set " + lobValueToSet + " for Search LOB on Case Console");
            }
        }
        commonWaits.waitForExecutionComplete();
    }

    public void removeAllFiltersOnCaseWorkspaceIfPresent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), availableFilter, 10)) {
            log.info("Filter is already removed.");
        } else {
            log.info("Removing active filter from BWF dashboard");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), clearFilter, 10), "Clear filter button is not visible");
            clickWithoutInterception(clearFilter);
        }
    }

    public void setSearchTextField(String value) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForElementClickable(findElement(By.xpath(searchTextField)));
        keyClearAndType(searchTextField, value);
        log.info("Set- " + value + " for Search TextField on Case Console");
    }

    public void setSearchTextField_CaseConsole(String searchValue) {
        log.info("Searching the ticket from BWF case console: " + searchValue);
        commonWaits.waitForLongTimeForElementVisible(findElement(By.xpath(searchTextField)));
        commonWaits.waitForElementClickable(findElement(By.xpath(searchTextField)));
        setSearchTextField(searchValue);
    }

    public void clickSearchBtn_CaseConsole() {
        clickWithoutInterception(searchButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        boolean res = commonWaits.waitForElementVisible_withFluentWait(getDriver(), gridRow, 30);
        Assert.assertTrue(res, "Case or request is not found on BWF dashboard after 30 seconds");
    }

    public BWFDashboard clickCaseIDLink_CaseConsole(String value, String columnName) {
        log.info("Opening the ticket from BWF case console: " + value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        clickCaseIDInCaseConsoleGrid(columnName, value, "Case ID");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForSometime(5000);
        return this;
    }

    public void clickCaseIDInCaseConsoleGrid(String columnNameToSearchValue, String valueToSearch, String columnNameValueToClock) {
        openRowWithoutScroll(columnNameToSearchValue, valueToSearch, columnNameValueToClock);
    }

    public void openRowWithoutScroll(String columnName, String cellValue, String linkColumnName) {
        List<WebElement> numberOfRows = findElements(By.xpath(dataRow));
        if (numberOfRows.isEmpty()) {
            throw new RuntimeException("Table is empty");
        } else {
            List<String> columnNames = getColumnNames();
            int columnNumber = columnNames.indexOf(columnName);
            if (columnNumber == -1) {
                throw new RuntimeException("Table does not have '" + columnName + "' column");
            } else {
                int clickableIndex = getColumnNames().indexOf(linkColumnName);
                if (clickableIndex == -1) {
                    throw new RuntimeException("Table does not have '" + linkColumnName + "' column");
                } else {
                    columnNumber++;
                    clickableIndex++;
                    boolean found = false;
                    int size = numberOfRows.size();

                    for (int i = 1; i <= size; i++) {
                        try {
                            WebElement cellElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dataRowCellValue, i, columnNumber)));
                            String expectedCellValue = cellElement.getText().trim();
                            log.info("Row " + i + " - Comparing '" + cellValue.trim() + "' with '" + expectedCellValue + "'");

                            if (cellValue.trim().equals(expectedCellValue)) {
                                found = true;
                                log.info("Found matching row at index: " + i);
                                commonWaits.clickWithoutInterception(By.xpath(String.format(dataRowCellLink, i, clickableIndex)));
                                break;
                            }
                        } catch (Exception e) {
                            log.info("Error reading row " + i + ": " + e.getMessage());
                        }
                    }
                    if (!found) {
                        throw new RuntimeException("Failed to find '" + cellValue + "' value in the '" + columnName + "' column");
                    }
                }
            }
        }
    }

    public List<String> getColumnNames() {
        return findElements(By.xpath(columnHeaders)).stream().map((el) -> el.findElement(By.cssSelector(columnName)).getAttribute("innerText").trim()).collect(Collectors.toList());
    }

    public void verify_Activity_Tab(String value) {
        int maxRetries = 5;
        boolean isPresent = false;
    
        for (int i = 0; i < maxRetries; i++) {
            try {
                bwfCase = new BWFCase();
                log.info("Attempt " + (i + 1) + " of " + maxRetries + " - Checking Activity tab for: " + value);
                log.info("Recent Activity: " + bwfCase.Recent_Activity.getText().trim());
                if (verifyStatusUpdate_In_SocialFeed(value)) {
                    isPresent = true;
                    break;
                }
                throw new Exception("Activity not found");
            } catch (Exception e) {
                log.info("Activity not found on attempt " + (i + 1) + ", refreshing and waiting before retry...");
                click(refreshButton);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            }
        }
    
        Assert.assertTrue(isPresent, "Expected Status '" + value + "' did not appear in Activity tab after " + maxRetries + " refresh attempts");
    }

    public boolean verifyStatusUpdate_In_SocialFeed(String value) {
        log.info("verifyStatusUpdate_In_SocialFeed: " + value);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(generalBodyNotes);
        boolean bStatusFound = false;
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(String.format(activityTab + bwfFrame, value)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(XPATH_BODY), 20), "Activity feed body is not visible");
        String str = commonWaits.waitForByElementToBeVisible(By.xpath(XPATH_BODY)).getText();
        log.info(" --- Actual value is : " + str);
        if (str.contains(value)) {
            bStatusFound = true;
        }
        commonWaits.switchtoDefaultFrame();
        return bStatusFound;
    }

    public void verifyUserName_In_SocialFeed(String UserName) {
        commonWaits.waitForAngularRequestsToFinish();
        try {
            commonWaits.waitForElementVisible(generalBodyNotes);
        } catch (Exception e) {
            Assert.fail("---- Notes section not seen. Exception = " + e.getMessage());
        }
        Assert.assertTrue(isElementPresent(By.xpath(String.format(ActualUserNameLocator, UserName))), "-----UserName was not found/ as expected in Activity Timeline");

    }

    public void addActivityNotes(String value) {
        commonWaits.waitForAngularRequestsToFinish();
        addComment_InSocialFeed(value);
    }

    public void addComment_InSocialFeed(String value) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(activityNotebox);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementClickable(activityNotebox);
        activityNotebox.click();
        commonWaits.clickWithoutInterception(activityNotesTextArea);
        keyClearAndType(activityNotesTextArea, value);
        commonWaits.waitForElementVisible(chkBoxPublicComment).click();
        commonWaits.waitForElementClickable(btnActivityNotesPost);
        commonWaits.waitForAngularRequestsToFinish();
        if (Objects.equals(btnActivityNotesPost.getAttribute("disabled"), "true")) {
            Assert.fail("-----Post button of Activity Timeline is disabled");
        } else {
            btnActivityNotesPost.click();
        }
        commonWaits.waitForAngularRequestsToFinish();
    }

    public void addComment_InSocialFeed_WithAttachment(String comment, File file) {
        this.enterNote(comment);
        chkBoxPublicComment.click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        WebElement inpAttachFile = getDriver().findElement(By.xpath(activityFileInput));
        inpAttachFile.sendKeys(file.getAbsolutePath());
        commonWaits.waitForExecutionComplete();
        if (!btnActivityNotesPost.isEnabled()) {
            Assert.fail("-----Post button of Activity Timeline is disabled");
        } else
            btnActivityNotesPost.click();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementClickable(addNote);
    }

    public void enterNote(String note){
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementVisible(activityNotebox);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForElementClickable(activityNotebox);
        commonWaits.clickWithoutInterception(activityNotebox);
        activityNotesTextArea.sendKeys(note);
        commonWaits.waitForElementVisible(chkBoxPublicComment);
    }

    public void verifyAttachment_In_Activity_Tab(String fileName) {
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(activityAttachmentFileName, fileName))),
                "Attachment not found in BWF Activity tab. Expected file: " + fileName);
    }

    public void navigateToPage(String menuGroupName, String menuItemName) {
        WebElement groupElement;
        if (!getDriver().findElements(By.xpath(compactNavigation)).isEmpty()) {
            log.info("---Vertical menu---");
            commonWaits.waitForByElementToBeVisible(By.xpath(compactNavigation)).click();
            groupElement = getDriver().findElements(By.xpath(menuGroupItems_VerticalMenu)).stream().filter((element) -> element.getText().equals(menuGroupName)).findFirst().orElse(null);

            if (groupElement == null) {
                log.info("---Looking for more menu items in Vertical Parent Menus");
                groupElement = getDriver().findElements(By.xpath(menuGroupItemsParent_VerticalMenu)).stream().filter((element) -> element.getText().trim().equals(menuGroupName)).findFirst().orElseThrow(() -> new AssertionError(String.format("----There is no menu item with '%s' name", menuGroupName)));
            }

            (new Actions(getDriver())).moveToElement(groupElement).click().perform();
            getDriver().findElements(By.xpath(menuGroupItemsChild_VerticalMenu)).stream().filter((element) -> element.getText().trim().equals(menuItemName)).findFirst().orElseThrow(() -> new AssertionError(String.format("There is no menu item with '%s' name", menuItemName))).click();
        } else {
            log.info("--- Horizontal menu---");
            WebElement horizontalgroupElement = getDriver().findElements(By.xpath(menuGroupItems)).stream().filter((element) -> element.getText().trim().equals(menuGroupName)).findFirst().orElse(null);

            if (horizontalgroupElement == null) {
                log.info("---Looking for more menu items in Horizontal Parent Menus");
                horizontalgroupElement = getDriver().findElements(By.xpath(menuGroupItemsParent)).stream().filter((element) -> element.getText().trim().equals(menuGroupName)).findFirst().orElseThrow(() -> new AssertionError(String.format("----There is no menu item with '%s' name", menuItemName)));

            }

            (new Actions(getDriver())).moveToElement(horizontalgroupElement).click().perform();
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(menuGroupItemsChild)));
            horizontalgroupElement.findElements(By.xpath(menuGroupItemsChild)).stream().filter((element) -> element.getText().equals(menuItemName)).findFirst().orElseThrow(() -> new AssertionError(String.format("There is no menu item with '%s' name", menuItemName))).click();
        }

    }

    /**
     * Navigate through menu item and submenu (Substitute method for new DOM structure)
     * This method handles the adapt-menu structure with button-based menus and overlay submenus
     *
     * @param menuItemName    - Name of the main menu item (e.g., "Create", "Workspace", "Live Chat")
     * @param submenuItemName - Name of the submenu item (e.g., "Case", "Knowledge"). Pass null if no submenu needed.
     *                        <p>
     *                        Usage examples:
     *                        navigateToMenuItem("Create", "Case") - Clicks Create menu, then Case submenu
     *                        navigateToMenuItem("Quick Case", null) - Clicks Quick Case direct link
     */
    public void navigateToMenuItem(String menuItemName, String submenuItemName) {
        log.info(String.format("Navigating to Menu: '%s', Submenu: '%s'", menuItemName, (submenuItemName != null ? submenuItemName : "None")));

        try {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

            // Step 1: Click on the main menu item button
            By menuItemLocator = By.xpath(String.format(menuItemButton, menuItemName));
            WebElement menuButton = commonWaits.waitForByElementToBeVisible(menuItemLocator);

            log.info("Clicking on menu item: " + menuItemName);
            commonWaits.clickWithoutInterception(menuButton);

            // Step 2: If submenu item is specified, wait for overlay and click submenu
            if (submenuItemName != null && !submenuItemName.trim().isEmpty()) {
                // Wait for submenu overlay to appear
                commonWaits.waitForElementVisible(submenuOverlay);

                // Find and click the submenu anchor link
                By submenuItemLocator = By.xpath(String.format(submenuItemLink, submenuItemName));
                WebElement submenuLink = commonWaits.waitForByElementToBeVisible(submenuItemLocator);

                log.info("Clicking on submenu item: " + submenuItemName);
                commonWaits.clickWithoutInterception(submenuLink);
            }

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            log.info(String.format("Successfully navigated to '%s' -> '%s'", menuItemName, (submenuItemName != null ? submenuItemName : "Direct Link")));

        } catch (Exception e) {
            String errorMsg = String.format("Failed to navigate to Menu: '%s', Submenu: '%s'. Error: %s",
                    menuItemName, (submenuItemName != null ? submenuItemName : "None"), e.getMessage());
            log.error(errorMsg);
            throw new AssertionError(errorMsg, e);
        }
    }

    public void crossLaunchReports() {
        if ((!getDriver().findElements(By.xpath(compactNavigation)).isEmpty())) {
            log.info("-----------Vertical menu bar-----");
            commonWaits.waitForElementClickable(getDriver().findElement(By.xpath(compactNavigation))).click();
            commonWaits.clickWithoutInterception(reportsLeftNavTab);
        } else {
            log.info("------------- Horizontal menu bar ----------------");
            commonWaits.clickWithoutInterception(reportsTab);
        }
    }

    public void searchCase(String caseID) {
        removeAllFiltersOnCaseWorkspaceIfPresent();
        commonWaits.waitForElement(By.xpath(searchTextField)).clear();
        commonWaits.waitForElement(By.xpath(searchTextField)).sendKeys(caseID);
        clickWithoutInterception(searchButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        boolean res = commonWaits.waitForElementVisible_withFluentWait(getDriver(), gridRow, 10);
        Assert.assertTrue(res, "Case or request is not found on BWF dashboard after 30 seconds");
    }

    public void openCase(String value) {
        log.info("Opening the ticket from BWF case console: " + value);
        WebElement caseRow = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(gridRow + "//*[contains(. ,'%s')]//a", value)));
        commonWaits.clickWithoutInterception(caseRow);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForSometime(2000);
    }

    public void searchKnowledge(String KAIDOrName) {
        removeAllFiltersOnCaseWorkspaceIfPresent();
        commonWaits.waitForElement(By.xpath(searchTextField)).clear();
        commonWaits.waitForElement(By.xpath(searchTextField)).sendKeys(KAIDOrName);
        clickWithoutInterception(searchButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        boolean res = commonWaits.waitForElementVisible_withFluentWait(getDriver(), gridRow, 10);
        Assert.assertTrue(res, "Case or request is not found on BWF dashboard after 30 seconds");
    }

    public void openKnowledge(String value) {
        WebElement caseRow = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(gridRow + "[contains(. ,'%s')]//a", value)));
        commonWaits.clickWithoutInterception(caseRow);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    String rowLinkByText = "//tr[contains(normalize-space(.), '%s')]//a";
    public BWFDashboard openGridRecordByLinkText(String linkText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        By linkLocator = By.xpath(String.format(rowLinkByText, linkText));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), linkLocator, 10), "Link with text '" + linkText + "' is not visible in the grid.");
        WebElement link = commonWaits.waitForByElementToBeVisible(linkLocator);
        commonWaits.clickWithoutInterception(link);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public void crosslaunchManageCatalog() {
        log.info("******* clicking on manage catalog ******************");
        if ((commonWaits.isElementPresent(compactNavigation))) {
            log.info("-----------Vertical menu bar-----");
            commonWaits.clickWithoutInterception(getDriver().findElement(By.xpath(compactNavigation)));
            commonWaits.clickWithoutInterception(manageCatalogLeftNavLink);
        } else {
            log.info("------------- Horizontal menu bar ----------------");
            commonWaits.clickWithoutInterception(manageCatalogLink);
        }
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs.get(1));
        commonWaits.waitForAngularRequestsToFinish();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), menuServices, 10), "Manage catalog crosslaunch is failed.");
    }

    public void crossLaunchApplication(String appName) {
        commonWaits.clickWithoutInterception(applicationLauncher);
        commonWaits.clickWithoutInterception(By.xpath(String.format(applicationName, appName)));
        commonWaits.switchToTabByIndex(1);
        commonWaits.waitForAngularRequestsToFinish();
    }

    public void openBWFSettings() {
        log.info("Opening BWF Settings");
        commonWaits.clickWithoutInterception(settingsLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public void searchAndOpenSettings(String settingName) {
        openBWFSettings();
        log.info("Searching for setting: " + settingName);
        commonWaits.waitForElementVisible(searchFilterOnSettings).click();
        searchFilterOnSettings.clear();
        searchFilterOnSettings.sendKeys(settingName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        clickWithoutInterception(By.xpath(String.format(searchedSetting, settingName)));
    }

    public void selectHelixGPTTab() {
        log.info("Selecting HelixGPT tab in BWF settings");
        if (!commonWaits.isElementPresent(helixGPTTab)) {
            commonWaits.refreshBrowser();
            searchAndOpenSettings(SolQAConstants.manageLineOfBusiness);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            if (!commonWaits.isElementPresent(helixGPTTab)) {
                commonWaits.waitForSometime(commonWaits.moreDelay);
            }
        }
        clickWithoutInterception(helixGPTTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public void enableHelixGPTGlobalSearch() {
        log.info("Selecting HelixGPT Global Search link in BWF settings");
        if (commonWaits.isElementPresent(helixGPTGlobalSearchLink)) {
            clickWithoutInterception(helixGPTGlobalSearchLink);
            log.info("Enabling toggle for configuration value HelixGPT Global Search");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public void checkKnowledgeSourceConfigValue() {
        searchAndOpenSettings(SolQAConstants.CommonConfig);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        searchTextFieldOnSettings.clear();
        searchTextFieldOnSettings.sendKeys(SolQAConstants.KNOWLEDGE_SOURCE);
        searchTextFieldOnSettings.sendKeys(Keys.ENTER);
        clickWithoutInterception(By.xpath(String.format(linkTextLocator, SolQAConstants.KNOWLEDGE_SOURCE)));
        String configValue = commonWaits.waitForElementVisible(knowledgeSourceConfigValueButton).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT);
        Assert.assertTrue(configValue.equalsIgnoreCase("HKM"), "Configuration value of Knowledge Source is not set to HKM.");
        clickWithoutInterception(cancelButtonOnSettings);
    }

    public void updateKnowledgeCuratorValue(String value) {
        commonWaits.refreshBrowser();
        searchAndOpenSettings(SolQAConstants.manageLineOfBusiness);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (commonWaits.isElementPresent(nextButton)) {
            clickWithoutInterception(nextButton);
        }
        if (!commonWaits.isElementPresent(helixGPTTab)) {
            commonWaits.refreshBrowser();
            searchAndOpenSettings(SolQAConstants.manageLineOfBusiness);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        }
        clickWithoutInterception(helixGPTTab);
        clickWithoutInterception(By.xpath(String.format(linkTextLocator, SolQAConstants.KNOWLEDGE_CURATOR)));
        clickWithoutInterception(knowledgeCuratorTrueButton);
        clickWithoutInterception(saveButtonOnSettings);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public BWFDashboard navigateToSettings() {
        log.info("Navigating to settings");
        commonWaits.waitForElementVisible(settingsMenu);
        commonWaits.waitForElementClickable(settingsMenu);
        settingsMenu.click();
        commonWaits.waitForPageLoadingComplete(SolQAConstants.appBWF, 40);
        commonWaits.waitForExecutionComplete();
        log.info("Clicked on settings menu");
        return this;
    }

    public void verifyGlobalSearchButtonAfterHelixGPTSearchEnabled() {
        log.info("Verifying global search button after Helix GPT search enabled");
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(SearchIcon.isDisplayed(), "Search icon after enabling Helix GPT is not shown");
        commonWaits.waitForExecutionComplete();
        log.info("Search icon after enabling Helix GPT is shown");
    }

    public void clickGlobalSearchButtonAfterHelixGPTSearchEnabled() {
        log.info("Clicking global search button after Helix GPT search enabled");
        commonWaits.waitForElementClickable(SearchIcon);
        commonWaits.clickWithoutInterception(SearchIcon);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForSometime(commonWaits.minDelay);
        log.info("Clicked global search button after Helix GPT search enabled");
    }

    public void navigateToChatOption() {
        log.info("Navigating to chat option");
        commonWaits.clickWithoutInterception(chatOption);
        commonWaits.waitForExecutionComplete();
        log.info("Clicked on chat option");
    }

    public void sendMessageInGPTChat(String message) {
        log.info("Sending message in GPT chat");
        commonWaits.waitForElementVisible(TextArea);
        TextArea.sendKeys(message);
        commonWaits.waitForElementClickable(sendButtonIcon).click();
        commonWaits.waitForExecutionComplete();
        log.info("Message sent in GPT chat");
    }

    public void defaultSearchToggleButton(String button) {
        if (commonWaits.isElementPresent(By.xpath(String.format(SearchToggaleButton, button)))) {
            log.info("Default search toggle button is already set to: " + button);
        } else {
            log.info("Default search toggle button is not set to: " + button + ". Setting it now.");
            By ButtonLocator = By.xpath(String.format(ToggleButton, button));
            WebElement ele = commonWaits.waitForByElementToBeVisible(ButtonLocator);
            commonWaits.clickWithoutInterception(ele);
        }
    }

    public void verifyFilterOptionsOnSearchDropdownMenus() {
        commonWaits.waitForByElementToBeVisible(By.xpath(searchDropdown)).click();
        List<String> values = Arrays.asList(SolQAConstants.FILTER_ALL,
                SolQAConstants.FILTER_KNOWLEDGE_ARTICLES,
                SolQAConstants.FILTER_PEOPLE,
                SolQAConstants.FILTER_DOCUMENT, // Default selected
                SolQAConstants.FILTER_ASSET,
                SolQAConstants.FILTER_CASES,
                SolQAConstants.FILTER_Tasks,
                SolQAConstants.FILTER_CASE_TEMPLATES,
                SolQAConstants.FILTER_TASK_TEMPLATES);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        for (String value : values) {
            searchDropdownValues.stream().filter(e -> !e.getText().trim().equals(value)).findFirst()
                    .orElseThrow(() -> new AssertionError(" found the value, " + value + " in search dropdown"));
        }
        commonWaits.waitForByElementToBeVisible(By.xpath(searchDropdown)).click();
        commonWaits.waitForSometime(commonWaits.minDelay);
    }

    public void selectSearchCategory_OnGPTSearch(String category) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementVisible(searchDropdown_GPT).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(optionFromSearchDropdown_GPT, category))).click();
        commonWaits.waitForSometime(commonWaits.minDelay);
    }

    public void selectFilterOptionAndSearchOnHelixGPTSearchbox(String TicketType, String textToSearch) {
        log.info("Select filter option from search dropdown - " + TicketType + " and search text - " + textToSearch);
        selectSearchCategory_OnGPTSearch(TicketType);
        commonWaits.waitForByElementToBeVisible(By.xpath(searchTextBox_GPTSearch)).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(searchTextBox_GPTSearch)).clear();
        commonWaits.waitForByElementToBeVisible(By.xpath(searchTextBox_GPTSearch)).sendKeys(textToSearch);
        commonWaits.waitForElementClickable(searchIcon_GPT).click();
        commonWaits.waitForSometime(commonWaits.moreDelay);
    }

    public int getResultCount() {
        String text = commonWaits.waitForElementVisible(searchResultCount_GPT, 20).getText().trim();
        return Integer.parseInt(text.subSequence(1, text.length() - 1).toString());
    }

    public enum ItemType {
        PEOPLE("People"),
        KNOWLEDGE_ARTICLES("Knowledge articles"),
        CASES("Cases");

        public final String value;

        ItemType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ItemType fromString(String text) {
            for (ItemType type : ItemType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown itemType: " + text);
        }
    }

    public void verifyCountOfItemsReturnedAndOpenFullView(String itemType, String itemTypeValue, int itemlevel, int itemline) {
        commonWaits.switchToTabByIndex(0);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        int count = getResultCount();
        log.info("Count is - " + count);

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(resultListElement, itemlevel, itemline)));
        commonWaits.waitForElementClickable(ele);
        commonWaits.clickWithoutInterception(ele);

        String ticketId = getDriver().findElement(By.xpath(String.format(resultListElement, itemlevel, itemline))).getText().trim();
        log.info("Ticket id is - " + ticketId);
        Assert.assertTrue(ticketId.startsWith(itemTypeValue),
                "Item is not returned by the search result when filter is applied for " + itemTypeValue);

        if (count > 0) {
            ItemType type = ItemType.fromString(itemType);

            switch (type) {
                case PEOPLE:
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    commonWaits.switchtoDefaultFrame();
                    Assert.assertTrue(commonWaits.isElementPresent(String.format(previewTicketId, itemTypeValue)),
                            "Not correct ticket is loaded in preview for people.");
                    commonWaits.waitForElementClickable(goToUserProfileOnGPT).click();
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    break;

                case KNOWLEDGE_ARTICLES:
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    commonWaits.switchtoDefaultFrame();
                    //  getDriver().switchTo().frame(FMLAFrameOnGPT);
                    Assert.assertTrue(commonWaits.isElementPresent(String.format(previewTicketNameOnArticle, ticketId)),
                            "Not correct ticket is loaded in preview for knowledge article.");
                    commonWaits.waitForElementClickable(goToKnowledgeArticleOnGPT).click();
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    break;

                case CASES:
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    Assert.assertTrue(commonWaits.isElementPresent(String.format(previewTicketNameOnCase, ticketId)),
                            "Not correct ticket is loaded in preview for cases.");
                    commonWaits.waitForElementClickable(goToCaseOnGPT).click();
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    commonWaits.switchtoDefaultFrame();
                    break;

                default:
                    break;
            }
        }
        commonWaits.waitForSometime(commonWaits.minDelay);
    }

    public String getAllGPTResponseString() {
        return helixGPTUtil.getAllGPTResponseString(allMessages);
    }

    public void importService(String fileLocation) {
        log.info("Importing BWF Service");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        try {
            uploadBWFService(fileLocation);
            commonWaits.waitForElementVisible(importHandlingPopup);
            clickWithoutInterception(sourceFileStatus);
            clickWithoutInterception(overwriteWorkflows);
            if (commonWaits.isElementPresent(overwriteDocumentDefinition)) {
                clickWithoutInterception(overwriteDocumentDefinition);
            }
            clickWithoutInterception(confirmButton);
            commonWaits.waitForElementVisible(successMsg);
            log.info("SuccessMsg Message " + successMsg.getText());
            Assert.assertTrue(successMsg.getText().contains("Services have been successfully imported.")
                    || successMsg.getText().contains("Your import request is in progress"), "Service are not imported");
        } catch (Exception e) {
            log.error("Service upload failed.");
            Assert.fail("Not able to upload Service", e);
        }
    }

    public void uploadBWFService(String fileLocation) {
        log.info("Uploading BWF Service");
        try {
            log.info("File Location: " + fileLocation);
            var file1 = new File(fileLocation);
            WebElement inpAttachFile = findElement(By.xpath(upload_BWFService));
            executeJS(scriptToMakeFileElementVisible);
            inpAttachFile.sendKeys(file1.getAbsolutePath());
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info("Error while attaching file while importing BWF Service: " + e);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public BWFDashboard searchExpandAndOpenSettings(String menuName, String settingName) {
        openBWFSettings();
        log.info("Expanding for setting: " + menuName);
        searchFilterOnSettings.clear();
        searchFilterOnSettings.sendKeys(menuName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        clickWithoutInterception(By.xpath(String.format(expandMenuLocator, menuName)));
        clickWithoutInterception(By.xpath(String.format(settingsNameLocator, settingName)));
        return this;
    }

    public BWFDashboard clickSearchBtn_CaseConsoleAndVerifyNoResults(int timeoutSeconds) {
        commonWaits.waitForElementClickable(searchButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        boolean res = commonWaits.waitForElementVisible_withFluentWait(getDriver(), gridRow, timeoutSeconds);
        Assert.assertFalse(res, "Case or request is unexpectedly found on BWF dashboard");
        return this;
    }

    /**
     * Validate a case field value on case details page
     *
     * @param fieldLabel    - The label of the field to validate (e.g., "Case Template", "Category Tier 1")
     * @param expectedValue - The expected value of the field
     */
    public BWFDashboard validateCaseField(String fieldLabel, String expectedValue) {
        log.info("Validating Case Field: '" + fieldLabel + "' with expected value: '" + expectedValue + "'");

        try {
            // Wait for page to load
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.waitForExecutionComplete();

            commonWaits.scrollToSpecificElement(getDriver().findElement(By.xpath(String.format(caseFieldValueByLabel, fieldLabel))));

            // Get the field value
            String actualValue = commonWaits.getTextSafely(By.xpath(String.format(caseFieldValueByLabel, fieldLabel)));

            log.info("Field '" + fieldLabel + "' - Actual value: '" + actualValue + "', Expected value: '" + expectedValue + "'");

            // Assert the field value matches expected
            Assert.assertEquals(actualValue, expectedValue,
                    "Case field '" + fieldLabel + "' validation failed. Expected: '" + expectedValue + "', Actual: '" + actualValue + "'");

            log.info("Successfully validated Case field '" + fieldLabel + "' = '" + expectedValue + "'");

        } catch (Exception e) {
            log.error("Failed to validate case field '" + fieldLabel + "': " + e.getMessage());
            throw new RuntimeException("Unable to validate case field '" + fieldLabel + "'", e);
        }
        return this;
    }

    public int numberOfTimesRequestIDIsPresentInCaseConsoleGrid(String requestId) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        if (requestId == null || requestId.trim().isEmpty()) {
            return 0;
        }
        List<WebElement> rows = findElements(By.xpath(gridRow));
        int count = 0;
        for (WebElement row : rows) {
            try {
                if (row.getText() != null && row.getText().contains(requestId)) {
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        return count;
    }

    public List<String> getVisibleValuesFromCaseConsoleColumn(String columnName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertNotNull(columnName, "Column name is null");
        List<WebElement> numberOfRows = findElements(By.xpath(dataRow));
        Assert.assertFalse(numberOfRows.isEmpty(), "No rows found in case console grid");

        List<String> columnNames = getColumnNames();
        int columnNumber = columnNames.indexOf(columnName);
        Assert.assertTrue(columnNumber >= 0, "Table does not have '" + columnName + "' column");
        columnNumber++; // 1-based for xpath index

        List<String> values = new ArrayList<>();
        int size = numberOfRows.size();
        for (int i = 1; i <= size; i++) {
            try {
                WebElement cellElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dataRowCellValue, i, columnNumber)));
                String txt = (cellElement == null) ? "" : cellElement.getText();
                if (txt != null) {
                    txt = txt.trim();
                    if (!txt.isEmpty()) {
                        values.add(txt);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return values;
    }

    public boolean verifyAttachmentUpdate_In_SocialFeed(String attachmentName) {
		log.info("verifyAttachmentUpdate_In_SocialFeed" + attachmentName);
		commonWaits.waitForAngularRequestsToFinish();
		try {
            log.info("Attachment found in Social Feed: " + attachmentName);
            if (commonWaits.isElementPresent(attachmentIcon)) {
                attachmentIcon.click();
            }
			commonWaits.waitForByElementToBeVisible(By.xpath(String.format(attachmentInSocialFeed, attachmentName)));
            return true;
		}
		catch (Exception e) {
			log.info("----No attachment thumbnail seen in Timeline even after 50 seconds." +
					e.getMessage());
            return false;
		}
	}

}