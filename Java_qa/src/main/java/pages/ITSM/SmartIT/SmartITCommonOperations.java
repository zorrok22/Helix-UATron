package pages.ITSM.SmartIT;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.util.List;

public class SmartITCommonOperations extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(SmartITCommonOperations.class.getName());
    CommonWaits commonWaits;

    public SmartITCommonOperations() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    //String Locators
    public String ElementXpathByID = "//*[@id='%s']";
    public String elementByIDARData = "//*[@id='ar%s_data']";
    public String pwaIframe = "//iframe[@id='pwa-frame']";
    public static final String SMART_IT_PAGE_SPINNER = "//*[@class='loader-container']";
    public String recommendedKnowledgeUnpinArticle = "(//*[@id='ka-id' and contains(. , '%s')]/ancestor::tr//button[@aria-label='Unpin the item'])[1]";
    String KA_Search_Result_iframe = "//*[@id='304267930']//iframe";
    public String recommendedKnowledgePinArticle = "(//*[@id='ka-id' and contains(. , '%s')]/ancestor::tr//button[@aria-label='Pin the item'])[1]";
    String typeHeadInput = "(//adapt-rx-typeahead[contains(normalize-space(.), '%s')]//input)";
    String typeHeadValueOption = "//button[contains(@class, 'rx-typeahead__dropdown-item') and contains(normalize-space(.), '%s')]";
    String textInputField = "//adapt-rx-textfield[contains(normalize-space(.), '%s')]//input";
    String textAreaField = "//adapt-rx-textarea[contains(normalize-space(.), '%s')]//textarea";
    String dropdownField = "//adapt-rx-select[contains(normalize-space(.), '%s')]//button";
    String dropdownValueOption = "//button[contains(@class, 'rx-select__option') and contains(normalize-space(.), '%s')]";
    String dateField = "//adapt-rx-datetime[contains(normalize-space(.), '%s')]//input";
    String counterField = "//adapt-rx-counter[contains(normalize-space(.), '%s')]//input";
    String radioField = "//*[contains(normalize-space(.), '%s')]//adapt-rx-radiobutton//*[contains(@class, 'radio__item') and contains(normalize-space(.), '%s')]";
    String tabButtonXpath = "//button[@role='tab' and contains(normalize-space(.), '%s')]";
    public String activityNoteContent = "//*[@id='content_block' or @id='contentBlock']";
    String feedIFrame = "//*[@id='304423041']//iframe";

    //WebElement Locators
    @FindBy(xpath = "(//span[contains(@class,'d-icon-pencil')][1]")
    WebElement editButton;
    @FindBy(xpath = "//button[contains(. ,'Wait')]")
    public WebElement waitAlertButton;
    @FindBy(xpath = "//button[contains(., 'Resource') or contains(@aria-label, 'Resource')]")
    WebElement resourceTabLocator;
    @FindBy(xpath = "//input[@placeholder='Search for another knowledge resource']")
    WebElement knowledgeSearchBox;
    @FindBy(xpath = "//button[@aria-label='Search for knowledge resources']")
    WebElement knowledgeSearchBtn;
    @FindBy(xpath = "//textarea[@aria-label='New note']")
    WebElement chatTextBox;
    @FindBy(xpath = "//button[contains(@aria-label,'Email')]")
    WebElement sendEmailButton;
    @FindBy(xpath = "//button[contains(@aria-label,'Print') or contains(@title,'Print')][not(@disabled)]")
    WebElement printerIcon;
    @FindBy(xpath = "//button[@id='304440891']")
    WebElement saveButton;
    @FindBy(xpath = "//button[normalize-space(.)='Cancel' or @aria-label='Cancel' or @title='Cancel']")
    WebElement cancelButton;
    @FindBy(xpath = "//*[@role='tablist']//button[@role='tab' and contains(normalize-space(.), 'Outages')]")
    WebElement outagesTab;
    @FindBy(xpath = "//*[@ux-id='system-alert']//*[@ux-id='system-alert-text']")
    WebElement alertText;
    @FindBy(xpath = "//*[@id='304419361']")
    WebElement ticketTitleOnViewPage;
    @FindBy(xpath = "(//*[text()='Description']/ancestor::smt-char-field//div[@role='article'])[last()]")
    WebElement ticketDescriptionOnViewPage;
    @FindBy(xpath = "//*[text()='Related service request']/ancestor::*[@id='304429581']//a")
    WebElement relatedServiceRequestID;
    @FindBy(xpath = "//*[text()='Related DWP request']/ancestor::*[@id='304433131']//a")
    WebElement relatedDWPRequestID;
    @FindBy(xpath = "//li[@id='304421601']//button")
    WebElement moreButtonInTabs;
    @FindBy(xpath = "//button[@role='option']//div[contains(normalize-space(),'Related parties')]/..")
    WebElement relatedPartiesTabUnderMore;
    @FindBy(xpath = "//button[@role='option']//div[contains(normalize-space(),'Source information')]/..")
    WebElement sourceInformationTabUnderMore;
    @FindBy(xpath = "//button[@title='Add related party']")
    WebElement createRelatedPartyButtonXpath;
    @FindBy(css = ".cancel-button")
    WebElement cancelPrintButton;
    @FindBy(xpath = "//button[@title='Refresh']")
    WebElement refreshButton;

    //Strings
    String ciField = "303497400";
    String serviceField = "303497300";
    String descriptionField = "1000000151";
    String incidentTypeField = "1000000099";
    String majorIncidentField = "304461051";
    String relatedPartiesTabName = "Related parties";
    String sourceInformationTabName = "Source information";

    public void switchToPWAFrame() {
        log.info("-------------- Switching to PWA frame ---------");
        try {
            commonWaits.switchtoDefaultFrame();
            commonWaits.switchToFrame(By.xpath(pwaIframe));
            log.info("Switched to PWA frame successfully.");
        } catch (Exception e) {
            log.error("Failed to switch to PWA frame: " + e.getMessage());
            throw e;
        }
        log.info("-------------- PWA frame switch completed ---------");
    }

    public String getFieldValue(String fieldName) {
        switchToPWAFrame();
        String fieldValue = null;
        switch (fieldName) {
            case "CI":
                fieldValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementByIDARData, ciField))).getText().trim();
                break;
            case "Service":
                fieldValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementByIDARData, serviceField))).getText().trim();
                break;

            case "Description":
                fieldValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementByIDARData, descriptionField))).getText().trim();
                break;
            case "Incident Type":
                fieldValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementByIDARData, incidentTypeField))).getText().trim();
                break;
            case "Major Incident":
                fieldValue = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(elementByIDARData, majorIncidentField))).getText().trim();
                break;
            default:
                log.error("Incorrect Field");
        }
        return fieldValue;
    }

    public void enableEditMode() {
        log.info("-------------- Enabling edit mode ---------");
        try {
            log.info("Enabling Edit Mode");
            commonWaits.waitForElementVisible(editButton).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForPageFullyLoaded(30, 3);
            log.info("Edit mode enabled successfully");
        } catch (Exception e) {
            log.error("Failed to enable edit mode: " + e.getMessage());
            throw e;
        }
        log.info("-------------- Edit mode enabling completed ---------");
    }

    public SmartITCommonOperations handlePageLoadWaitAlertIfPresent() {
        try {
            if (waitAlertButton.isDisplayed()) {
                log.info("Wait alert is displayed, clicking the Wait button to dismiss");
                commonWaits.clickWithoutInterception(waitAlertButton);
                commonWaits.waitForPageReadyState(getDriver());
                Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), SMART_IT_PAGE_SPINNER, 10), "Page spinner did not disappear after dismissing wait alert");
                log.info("Wait alert dismissed successfully by clicking the Wait button");
            }
        } catch (Exception e) {
            log.info("No wait alert present or not visible, continuing without action");
        }
        return this;
    }

    public SmartITCommonOperations clickResourceTab() {
        log.info("-------------- Clicking Resource Tab (PV) ---------");
        try {
            switchtoDefaultFrame();
            commonWaits.switchToFrame(By.xpath(pwaIframe));
            commonWaits.clickWithoutInterception(resourceTabLocator);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            log.info("Resource tab clicked successfully");
        } catch (Exception e) {
            log.error("Failed to click resource tab: " + e.getMessage());
            throw e;
        }
        return this;
    }

    public SmartITCommonOperations searchKnowledgeInRecommendedKnowledgeSection(String knowledgeArticleId) {
        log.info("-------------- Searching knowledge article in recommended knowledge section: " + knowledgeArticleId + " ---------");
        try {
            commonWaits.waitForElementVisible(knowledgeSearchBtn, 5);
            commonWaits.scrollToElement(knowledgeSearchBtn).click();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.scrollToElement(knowledgeSearchBox);
            knowledgeSearchBox.clear();
            knowledgeSearchBox.sendKeys(knowledgeArticleId + Keys.ENTER);
            commonWaits.waitForPageFullyLoaded(30, 1);
            log.info("Knowledge article searched successfully");
        } catch (Exception e) {
            log.error("Failed to search knowledge article: " + e.getMessage());
            throw e;
        }
        return this;
    }

    String recommendedKA_Iframe = "(//*[@id='304267930']//iframe)[1]";
    public boolean isKnowledgeArticlePinnedUnderResourcesTab(String knowledgeArticleId) {
        log.info("-------------- Checking if knowledge article is pinned: " + knowledgeArticleId + " ---------");
        try {
            switchToPWAFrame();
            switchToFrame(By.xpath(recommendedKA_Iframe));
            boolean isPinned = commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                    By.xpath(String.format(recommendedKnowledgeUnpinArticle, knowledgeArticleId)), 10);
            log.info("Knowledge article pinned status: " + isPinned);
            return isPinned;
        } catch (Exception e) {
            log.error("Failed to check if knowledge article is pinned: " + e.getMessage());
            return false;
        }
    }

    public SmartITCommonOperations selectRecommendedKnowledge(String knowledgeID) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
        switchToPWAFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchToFrame(By.xpath(KA_Search_Result_iframe));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(recommendedKnowledgePinArticle, knowledgeID)), 20), "Recommended Knowledge Article pin option is not available");
        commonWaits.clickWithoutInterception(By.xpath(String.format(recommendedKnowledgePinArticle, knowledgeID)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForSometime(commonWaits.minDelay);
        log.info("Recommended Knowledge Article selected successfully");
        return this;
    }

    public boolean isChatTextBoxPresentOnChatWindow() {
        log.info("-------------- Checking if chat textbox is present (PV) ---------");
        try {
            commonWaits.switchtoDefaultFrame();
            switchToFrame(By.xpath(pwaIframe));
            commonWaits.waitForPageReadyState(getDriver());
            boolean isPresent = commonWaits.isElementPresent(chatTextBox);
            log.info("Chat textbox present status: " + isPresent);
            return isPresent;
        } catch (Exception e) {
            log.error("Failed to check chat textbox presence: " + e.getMessage());
            return false;
        }
    }

    public boolean isSendEmailButtonPresent() {
        log.info("-------------- Checking if send email button is present (PV) ---------");
        try {
            commonWaits.switchtoDefaultFrame();
            switchToFrame(By.xpath(pwaIframe));
            commonWaits.waitForPageReadyState(getDriver());
            boolean isPresent = commonWaits.isElementPresent(sendEmailButton);
            log.info("Send email button present status: " + isPresent);
            return isPresent;
        } catch (Exception e) {
            log.error("Failed to check send email button presence: " + e.getMessage());
            return false;
        }
    }

    public SmartITCommonOperations clickPrinterIcon() {
        log.info("-------------- Clicking printer icon (PV) ---------");
        try {
            commonWaits.switchtoDefaultFrame();
            switchToFrame(By.xpath(pwaIframe));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.clickWithoutInterception(printerIcon);
            log.info("Printer icon clicked successfully");
        } catch (Exception e) {
            log.error("Failed to click printer icon: " + e.getMessage());
            throw e;
        }
        return this;
    }

    public SmartITCommonOperations clickCancelButtonFromPrintBlade_PV() {
        log.info("-------------- Clicking cancel button from print blade (PV) ---------");
        try {
            // Switch to print window if it opened in new window
            String originalWindow = getDriver().getWindowHandle();
            int windowCount = getDriver().getWindowHandles().size();

            if (windowCount > 1) {
                // If print opened in new window, switch to it and close
                for (String windowHandle : getDriver().getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        getDriver().switchTo().window(windowHandle);
                        log.info("Switched to print window");
                        break;
                    }
                }
                getDriver().switchTo().window(originalWindow);
                log.info("Switched back to original window");
            } else {
                // If print blade opened in same window, click cancel
                commonWaits.switchtoDefaultFrame();
                commonWaits.waitForPageFullyLoaded(30, 1);
                if (commonWaits.isElementPresent(cancelPrintButton)) {
                    commonWaits.waitForElementVisible(cancelPrintButton);
                    commonWaits.clickWithoutInterception(cancelPrintButton);
                    log.info("Cancel button clicked successfully");
                } else {
                    log.info("Cancel button not found, print dialog may have closed automatically");
                }
            }
        } catch (Exception e) {
            log.error("Failed to click cancel button from print blade: " + e.getMessage());
            // Don't throw exception, print dialog handling can be flaky
        }
        return this;
    }

    public SmartITCommonOperations setTypeaheadField(String fieldLabel, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(typeHeadInput, fieldLabel)), 10),
                "Typeahead input field is not visible for field: " + fieldLabel);
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeHeadInput, fieldLabel)));
        commonWaits.scrollToElement(Field);
        Field.clear();
        Field.click();
        Field.sendKeys(value);
        // Wait for suggestions to load
        commonWaits.waitForSometime(commonWaits.moreDelay);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(typeHeadValueOption, value), 10),
                "Typeahead suggestions did not load for field: " + fieldLabel);
        commonWaits.clickByJS(findElement(By.xpath(String.format(typeHeadValueOption, value))));
        return this;
    }

    public SmartITCommonOperations setTextField(String fieldLabel, String value) {
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textInputField, fieldLabel)));
        commonWaits.scrollToElement(Field);
        Field.clear();
        Field.sendKeys(value);
        return this;
    }

    public SmartITCommonOperations setTextAreaField(String fieldLabel, String value) {
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textAreaField, fieldLabel)));
        commonWaits.scrollToElement(Field);
        Field.clear();
        Field.sendKeys(value);
        return this;
    }

    public SmartITCommonOperations setDropdownField(String fieldLabel, String value) {
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownField, fieldLabel)));
        commonWaits.scrollToElement(Field);
        Field.click();
        // Wait for options to load
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(dropdownValueOption, value), 5),
                "Dropdown options did not load for field: " + fieldLabel);
        commonWaits.click(By.xpath(String.format(dropdownValueOption, value)));
        return this;
    }

    public SmartITCommonOperations setDateField(String fieldLabel, String value) {
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateField, fieldLabel)));
        commonWaits.scrollToElement(Field);
        Field.clear();
        Field.sendKeys(value);
        Field.sendKeys(Keys.TAB); // To trigger any onBlur events
        return this;
    }

    public SmartITCommonOperations setCounterField(String fieldLabel, String value) {
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(counterField, fieldLabel)));
        commonWaits.scrollToElement(Field);
        Field.clear();
        Field.sendKeys(value);
        return this;
    }

    public SmartITCommonOperations clickSave() {
        if (commonWaits.isElementPresent(By.xpath(pwaIframe))) {
            switchToPWAFrame();
        }
        commonWaits.scrollToElement(saveButton);
        commonWaits.clickWithoutInterception(saveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public SmartITCommonOperations clickCancel() {
        commonWaits.scrollToElement(cancelButton);
        commonWaits.clickWithoutInterception(cancelButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    public SmartITCommonOperations setRadioField(String fieldLabel, String value) {
        WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(radioField, fieldLabel, value)));
        commonWaits.scrollToElement(Field);
        Field.click();
        return this;
    }

    public void waitForCreateAndViewPageLoaded() {
        switchToPWAFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        handlePageLoadWaitAlertIfPresent();
    }

    public SmartITCommonOperations openOutagesTab() {
        commonWaits.clickWithoutInterception(outagesTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    public String getSystemAlertText() {
        try {
            switchtoDefaultFrame();
            if (commonWaits.isElementPresent(alertText)) {
                return alertText.getText();
            }
        } catch (Exception ignored) {
            log.info("System alert not found in default frame");
        }
        try {
            switchToPWAFrame();
            if (commonWaits.isElementPresent(alertText)) {
                return alertText.getText();
            }
        } catch (Exception ignored) {
            log.info("System alert not found in PWA frame");
        }
        return "";
    }

    public String getTitle_viewPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ticketTitleOnViewPage, 20), "Ticket title is not displayed on view page");
        return ticketTitleOnViewPage.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim();
    }

    public String getDescription_viewPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return commonWaits.waitForElementVisible(ticketDescriptionOnViewPage).getText();
    }

    public String getDWPRequestID_FromSmartIT() {
        log.info("---- Get DWP Request from SmartIT ------");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();
        if(commonWaits.waitForElementVisible_withFluentWait(getDriver(), relatedServiceRequestID, 5)){
            return relatedServiceRequestID.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim().replaceFirst(":+\\s*$", "").trim();
        }
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), relatedDWPRequestID, 5)){
            return relatedDWPRequestID.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).trim().replaceFirst(":+\\s*$", "").trim();
        }
        Assert.fail("Neither related service request ID nor related DWP request ID is displayed on the SmartIT view page.");
        return "";
    }

    public boolean isActivityNoteAppearedUnderActivityTab_simpleText(String expectedText) {
        commonWaits.switchToActiveWindow(0);
        getDriver().navigate().refresh();
        commonWaits.waitForPageFullyLoaded(50, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();

        List<WebElement> elements = findElements(By.xpath(feedIFrame));
        commonWaits.waitForPageFullyLoaded(50, 1);
        for (WebElement element : elements) {
            getDriver().switchTo().frame(element);
            try {
                if (!findElements(By.xpath(activityNoteContent)).isEmpty()) {
                    String content = findElement(By.xpath(activityNoteContent)).getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).replaceAll("\\s+", " ").trim();
                    log.info("Activity note content: " + content);
                    log.info("Expected text: " + expectedText);
                    if (content != null && content.contains(expectedText.replaceAll("\\s+", " ").trim())) {
                        switchtoDefaultFrame();
                        return true;
                    }
                }
            } catch (Exception ignored) {
                log.info("Activity note content not found in this iframe, continuing to next iframes");
            }
            getDriver().switchTo().parentFrame();
        }
        switchtoDefaultFrame();
        return false;
    }

    String activityNoteContent_DWPRequest = "//*[contains(@class, 'timeline-feed')]//*[contains(. ,'%s')]";
    public boolean isActivityNoteAppearedUnderActivityTab_simpleText_ForDWPRequest(String expectedText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
        return commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(activityNoteContent_DWPRequest, expectedText)), 10);
    }

    public boolean isAttachedFileAppearedUnderActivityTab_ForDWPRequest(String expectedFileName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
        return commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(activityNoteContent_DWPRequest, expectedFileName)), 10);
    }

    public boolean verifyRelatedPartiesAndSourceInformationTabs() {
        boolean tabFound = false;
        switchToPWAFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        if (commonWaits.isElementPresent(moreButtonInTabs)) {
            commonWaits.waitForElementVisible(moreButtonInTabs);
            commonWaits.clickWithoutInterception(moreButtonInTabs);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

            if (commonWaits.isElementPresent(relatedPartiesTabUnderMore) && commonWaits.isElementPresent(sourceInformationTabUnderMore)) {
                tabFound = true;
            }

            try {
                commonWaits.clickWithoutInterception(moreButtonInTabs);
            } catch (Exception ignored) {
            }
        } else {
            tabFound = isElementPresent(By.xpath(String.format(tabButtonXpath, sourceInformationTabName)))
                    && isElementPresent(By.xpath(String.format(tabButtonXpath, relatedPartiesTabName)));
        }
        return tabFound;
    }

    public SmartITCommonOperations openRelatedPartiesTab() {
        switchToPWAFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        if (commonWaits.isElementPresent(moreButtonInTabs)) {
            commonWaits.clickWithoutInterception(moreButtonInTabs);
            commonWaits.waitForElementVisible(relatedPartiesTabUnderMore).click();
        } else {
            commonWaits.clickWithoutInterception(By.xpath(String.format(tabButtonXpath, relatedPartiesTabName)));
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    @FindBy(xpath = "//*[@rx-view-component-id='ad5a5c93-8262-4c0e-aa86-b01b60a14260']//button[contains(normalize-space(.), 'Save')]")
    WebElement saveRelatedParty;
    String createRelatedPartyIframe = "//iframe[@id='VF304437901']";
    public SmartITCommonOperations createRelatedParty(String relatedPartyType, String relatedPartyName, String relatedPartyRole) {
        // Assumption (Umbrella mapping): Related parties blade uses PV Adapt fields with labels "Type" and "Name".
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(createRelatedPartyButtonXpath).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchToFrame(By.xpath(createRelatedPartyIframe));

        if (relatedPartyType != null && !relatedPartyType.trim().isEmpty()) {
            setDropdownField("Type(required)", relatedPartyType);
        }
        if (relatedPartyName != null && !relatedPartyName.trim().isEmpty()) {
            WebElement Field = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeHeadInput+"[last()]", "Name")));
            commonWaits.scrollToElement(Field);
            Field.clear();
            Field.sendKeys(relatedPartyName);
            // Wait for suggestions to load
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), "//button[contains(@class, 'rx-typeahead__dropdown-item')]", 7),
                    "Typeahead suggestions did not load for field: " + "Name");
            commonWaits.click(By.xpath("//button[contains(@class, 'rx-typeahead__dropdown-item')]"));
        }
        if (relatedPartyRole != null && !relatedPartyRole.trim().isEmpty()) {
            setDropdownField("Role(required)", relatedPartyRole);
        }

        commonWaits.waitForElementVisible(saveRelatedParty).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    @FindBy(xpath = "//button[@id='304423751' and contains(. , 'Back')]")
    WebElement backToResourceTabButton;
    public SmartITCommonOperations clickOnBackToResourceTab() {
        commonWaits.clickWithoutInterception(refreshButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(backToResourceTabButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    String studioIframe = "//iframe[@id='studio-frame']";
    public boolean isStudioFrameVisible() {
        return commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(studioIframe), 10);
    }

    @FindBy(xpath = "//*[@id='304452051']//button[contains(normalize-space(.), 'More')]")
    WebElement moreOptionsBtnOnViewChange;
    @FindBy(xpath = "//button[@role='menuitem' and contains(normalize-space(.), 'Approval list')]")
    WebElement approvalListOption;
    @FindBy(xpath = "//fieldset[contains(. ,'Approval list')]")
    WebElement approvalListSection;
    @FindBy(xpath = "//button[@id='304452201' and contains(normalize-space(.) , 'Add approver')]")
    WebElement addApproverBtn;
    @FindBy(xpath = "//adapt-rx-radiobutton//label[contains(normalize-space(.), 'Individual')]")
    WebElement individualApproverRadioBtn;
    @FindBy(xpath = "//input[@id='ar304452561']")
    WebElement approverTypeaheadInput;
    @FindBy(xpath = "//button[@id='304452591' and @title='Add']")
    WebElement selectApproverBtn;
    @FindBy(xpath = "//button[@id='301614900' and @aria-label='Close']")
    WebElement closeApprovalListBtn;
    String approverTypeheadOption = "//button[contains(@class, 'rx-typeahead__dropdown-item') and contains(normalize-space(.), '%s')]";

    public SmartITCommonOperations addApprover(String approverName) {
        switchToPWAFrame();
        commonWaits.waitForElementVisible(moreOptionsBtnOnViewChange).click();
        commonWaits.waitForElementVisible(approvalListOption).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), approvalListSection, 10), "Approval list section did not appear after clicking approval list option");
        commonWaits.waitForElementClickable(addApproverBtn).click();
        commonWaits.waitForPageFullyLoaded(10,2);
        commonWaits.waitForElementVisible(individualApproverRadioBtn).click();
        commonWaits.waitForElementVisible(approverTypeaheadInput).click();
        approverTypeaheadInput.sendKeys(approverName);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(approverTypeheadOption, approverName), 10), "Approver typeahead suggestions did not load for approver name: " + approverName);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(approverTypeheadOption, approverName))).click();
        commonWaits.clickWithoutInterception(selectApproverBtn);
        commonWaits.waitForPageReadyState();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementClickable(closeApprovalListBtn, 5).click();
        return this;
    }

    @FindBy(xpath = "//input[@title='Add a note']")
    WebElement addNoteInputBox;
    @FindBy(xpath = "//*[@ux-id='add-note-textbox' and @role='textbox'] | //*[@aria-label='Add a note' and @role='textbox']")
    WebElement addNoteTextBox;
    @FindBy(xpath = "//input[@type='file' and @id='uploadAttachment']")
    WebElement fileUploadInput;
    @FindBy(xpath = "//*[@ux-id='timeline-note-active' or contains(@class, 'timeline-note')]//*[contains(@class, 'attachment__thumbnail')]")
    WebElement attachedFileThumbnail;
    @FindBy(xpath = "//button[@ux-id='timeline-note-post' or text()='Post']")
    WebElement postNoteButton;
    public SmartITCommonOperations addAttachmentNote_InActivityFeed(String noteText, File file) {
        switchtoDefaultFrame();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.clickWithoutInterception(addNoteInputBox);
        commonWaits.clickWithoutInterception(addNoteTextBox);
        addNoteTextBox.sendKeys(noteText);
        if (file != null && file.exists()) {
            fileUploadInput.sendKeys(file.getAbsolutePath());
            commonWaits.waitForPageFullyLoaded(10, 2);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), attachedFileThumbnail, 10), "Attached file thumbnail did not appear in activity feed");
        }
        commonWaits.waitForElementClickable(postNoteButton).click();
        commonWaits.waitForPageFullyLoaded(10, 2);
        return this;
    }

    @FindBy(xpath = "(//*[contains(@id,'304431941')])")
    WebElement FlagOnView;
    @FindBy(xpath = "//*[@id='304431941']//button[not(@hidden)]")
    WebElement flagButtonOnView;
    public boolean isAttentionFlagSet() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();
        if (!commonWaits.isElementPresent(FlagOnView) || !commonWaits.isElementPresent(flagButtonOnView)) {
            return false;
        }
        String title = commonWaits.waitForElementVisible(flagButtonOnView).getAttribute("title");
        return title != null && title.trim().equalsIgnoreCase("Unflag");
    }
    
}