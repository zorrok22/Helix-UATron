package pages.ITSM.ITSM;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Knowledge extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(Knowledge.class.getName());
    CommonWaits commonWaits;

    public Knowledge() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";
    String TYPE_HEAD_INPUT = "(//label[contains(. ,'%s')]/following-sibling::textarea)[1]";
    String QUESTION_IFRAME = "//iframe[contains(@title, 'Editor')]";
    /** Search result grid cells; grid is often inside table row iframes (see {@link #switchToKnowledgeSearchResultsFrameIfNeeded()}). */
    String ARTICLE_SEARCH_COLUMN = "//fieldset[@id='pagerow']//*[@artype='Column']";
    String ARTICLE_SEARCH_STATUS_LINKS = "//table[@class='status']//a";
    String articleLink = "//a[contains(normalize-space(.), '%s')]";

    @FindBy(xpath = "//*[contains(@aria-label, 'Editor') and @role='textbox']")
    WebElement QUESTION_TEXTBOX;
    @FindBy(xpath = "//label[contains(text(), 'Article ID')]/following-sibling::textarea")
    WebElement ARTICLE_ID;
    @FindBy(xpath = "//label[contains(text(), 'Status')]/following-sibling::textarea")
    WebElement STATUS_FIELD;
    @FindBy(xpath = "//a[@title='Search']")
    WebElement SEARCH_BUTTON;
    @FindBy(xpath = "//a[@title='Refresh']")
    WebElement refreshReloadButton;
    @FindBy(xpath = "//table[@class='status']//a")
    WebElement searchResult;
    @FindBy(xpath = "(//*[@id='T302300031']//tr[not(contains(@class, 'hiddentablehdr'))])[1]")
    WebElement FIRST_FEEDBACK_ENTRY;
    @FindBy(xpath = "//a[@arid='302300038' and contains(. ,'View')]")
    WebElement VIEW_FEEDBACK_LINK;
    @FindBy(xpath = "//label[text()='Feedback Details']/following-sibling::textarea")
    WebElement FEEDBACK_DETAILS_TEXTAREA;
    @FindBy(xpath = "//a[text()='Details']")
    WebElement detailsTab;
    @FindBy(xpath = "//a[text()='Feedback']")
    WebElement feedbackTab;

    @FindBy(xpath = "//iframe[@title='Result1']")
    WebElement searchResultFrame;

    public String fillKnowledgeData(Map<Object, String> knowledgeData) {

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        //Set Template
        commonWaits.switchToTabByTitle(SolQAConstants.CREATE_KNOWLEDGE);
        setDropDownField(SolQAConstants.TEMPLATE, knowledgeData.get("Template"));
        commonWaits.clickByText(SolQAConstants.CREATE);
        commonWaits.switchToTabByTitle(SolQAConstants.BMC_HELIX);
        commonWaits.clickByText(SolQAConstants.DETAILS);

        //Set Company
        if (knowledgeData.get("Company") != null) {
            setDropDownField("Company", knowledgeData.get("Company"));
        }

        if (knowledgeData.get("Company*") != null) { // Using for Apex, as unable to create new method using existing method.
            setDropDownField("Company*", knowledgeData.get("Company*"));
        }

        if (knowledgeData.get("Title") != null) {
            setTextAreaInput("Title*", knowledgeData.get("Title"));
        }
        if (knowledgeData.get("Article Language") != null) {
            setDropDownField(SolQAConstants.ARTICLE_LANGUAGE, knowledgeData.get("Article Language"));
        }

        String KAID = getArticleID();

        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return KAID;
    }

    public void updateArticleStatus(String status) {
        commonWaits.clickByText(SolQAConstants.DETAILS);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setDropDownField(SolQAConstants.STATUS, status);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickWithoutInterception(refreshReloadButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }


    public String getArticleID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return commonWaits.waitForElementVisible(ARTICLE_ID).getAttribute(SolQAConstants.ATTRIBUTE_VALUE).trim();
    }

    public void setTextAreaInput(String labelName, String value) {
        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
            webElement.click();
            webElement.sendKeys(value);
            log.info("Set text area input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set text area input for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public void setDropDownField(String labelName, String value) {
        try {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(DropDownField, labelName)), 20), "Dropdown field with label '" + labelName + "' is not visible.");
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownField, labelName))).click();
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(DropDownFieldOption, value)), 20), "Dropdown option '" + value + "' for field '" + labelName + "' is not visible.");
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, value))).click();
            log.info("Set dropdown field " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public String getStatus() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickByText(SolQAConstants.DETAILS);
        return commonWaits.waitForElementVisible(STATUS_FIELD).getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
    }

    public void searchKnowledgeArticle(String articleID) {
        String id = articleID != null ? articleID.trim() : "";
        commonWaits.switchToLastWindow();
        switchtoDefaultFrame();
        refreshBrowser();
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        WebElement searchField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, SolQAConstants.SEARCH)));
        searchField.click();
        searchField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchField.sendKeys(Keys.DELETE);
        searchField.sendKeys(id);
        commonWaits.waitForElementVisible(SEARCH_BUTTON);
        commonWaits.clickWithoutInterception(SEARCH_BUTTON);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
    }

    /**
     * BMC Helix places the pagerow/results table inside numbered result iframes (same pattern as {@link #openArticle(String)}).
     * Searching from default content never finds {@link #ARTICLE_SEARCH_COLUMN}; switch into the iframe first.
     */
    String pageRow = "//fieldset[@id='pagerow']";
    private void switchToKnowledgeSearchResultsFrameIfNeeded() {
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        int waitSecondsGrid = 5;
        if (commonWaits.isElementPresent(By.xpath(ARTICLE_SEARCH_COLUMN), waitSecondsGrid)
                || commonWaits.isElementPresent(By.xpath(pageRow), waitSecondsGrid)
                || commonWaits.isElementPresent(By.xpath(ARTICLE_SEARCH_STATUS_LINKS), waitSecondsGrid)) {
            return;
        }

        for (int i = 0; i < 12; i++) {
            switchtoDefaultFrame();
            if (!trySwitchToKnowledgeResultFrame(i)) {
                continue;
            }
            if (commonWaits.isElementPresent(By.xpath(ARTICLE_SEARCH_COLUMN), 20)
                    || commonWaits.isElementPresent(By.xpath(pageRow), 20)
                    || commonWaits.isElementPresent(By.xpath(ARTICLE_SEARCH_STATUS_LINKS), 20)) {
                return;
            }
        }
        switchtoDefaultFrame();
    }

    private boolean trySwitchToKnowledgeResultFrame(int rowIndex) {
        String frameId = "302258813R" + rowIndex + "C1";
        try {
            FluentWait<WebDriver> w = new FluentWait<>(getDriver())
                    .withTimeout(Duration.ofSeconds(12))
                    .pollingEvery(Duration.ofMillis(250))
                    .ignoring(WebDriverException.class);
            w.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameId));
            log.debug("Switched to knowledge search result iframe: " + frameId);
            return true;
        } catch (Exception e) {
            log.debug("Knowledge search grid not in iframe " + frameId + ": " + e.getMessage());
            switchtoDefaultFrame();
            return false;
        }
    }

    /** @return number of column cells when present; otherwise rows via {@link #ARTICLE_SEARCH_STATUS_LINKS} */
    public int getSearchResultCount() {
        switchToKnowledgeSearchResultsFrameIfNeeded();

        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(ARTICLE_SEARCH_COLUMN), 25)) {
            return commonWaits.getElements(By.xpath(ARTICLE_SEARCH_COLUMN)).size();
        }
        try{
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(ARTICLE_SEARCH_STATUS_LINKS), 40),
                "Search results are not displayed.");
        }

        catch(AssertionError e){
            log.info("Search results are not displayed. Clicking on search button to refresh the results.");
            switchtoDefaultFrame();
            commonWaits.waitForElementVisible(SEARCH_BUTTON);
            commonWaits.clickWithoutInterception(SEARCH_BUTTON);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        }
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(searchResultFrame);
        List<WebElement> links = getDriver().findElements(By.xpath(ARTICLE_SEARCH_STATUS_LINKS));
        Assert.assertFalse(links.isEmpty(), "Search results table has no rows.");
        commonWaits.switchtoDefaultFrame();
        return links.size();
        
    }

    public String verifyTitle() {
        commonWaits.waitForElementVisible(searchResult);
        return searchResult.getText();
    }

    public Knowledge openArticle(String KAId) {
        int searchCount = getSearchResultCount();
        int i = 0;
        while (i < searchCount) {
            switchtoDefaultFrame();
            commonWaits.switchToFrameByNameOrID("302258813R" + i + "C1");
            if (verifyTitle().contains(KAId)) {
                break;
            }
            i++;
        }
        commonWaits.clickWithoutInterception(By.xpath(String.format(articleLink, KAId)));
        commonWaits.waitForExecutionComplete();
        switchtoDefaultFrame();
        commonWaits.switchToTabByTitle(SolQAConstants.Knowledge_Article_Manager);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public Knowledge verifyFeedBackIsAvailable(String expectedFeedbackText) {
        commonWaits.switchToLastWindow();
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(detailsTab).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(feedbackTab).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(FIRST_FEEDBACK_ENTRY).click();
        commonWaits.waitForElementVisible(VIEW_FEEDBACK_LINK).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.switchToTabByTitle("Feedback");
        String feedbackText = commonWaits.waitForElementVisible(FEEDBACK_DETAILS_TEXTAREA).getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
        Assert.assertEquals(feedbackText, expectedFeedbackText, "Feedback text does not match expected value.");
        log.info("Verified feedback text matches expected value: " + expectedFeedbackText);
        return this;
    }

}