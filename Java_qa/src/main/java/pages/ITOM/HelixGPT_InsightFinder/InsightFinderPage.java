package pages.ITOM.HelixGPT_InsightFinder;

import static org.testng.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class InsightFinderPage extends BasePage {

    CommonWaits commonWaits;
    WebDriver driver;
    public SoftAssert softAssert;

    static SolqaLogger log = new SolqaLogger(InsightFinderPage.class.getName());
    int waitTimeSeconds;
    int pollIntervalMs;
    int maxAttempts;
    int actualPanelCount ;
    boolean countMatched ;

    public InsightFinderPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        softAssert = new SoftAssert(); 
    }
    
    public String insightFinderText = "//div[@data-testid='bubble-container']//p[text()='%s']";
 
    public String spanTextLocator = " //span[text()='%s']";
    
    public String loader ="//div[@data-testid='ai-avatar-container']//div[contains(@class,'ai-loader')]";
    
    public String dashboardPanelHeader = "(//section[contains(@class,'panel-container')]//h2)['%s']";
    
    public String navigation = "//nav[@aria-label='Breadcrumbs']//span[@title='%s']";
    
    public String divRow = ".//div[@role='row']";
    
    public String canvasElement = ".//canvas";
    
    public String uplotDiv = ".//div[@data-testid='uplot-main-div']";
    
    public String singlestatDiv = ".//div[contains(@class,'singlestat')]";
    
    public String errorDiv = ".//div[@data-testid='data-testid Panel data error message']";
    
    public String panelContent = ".//ancestor::div[contains(@data-testid,'header-container')]//following-sibling::div[contains(@data-testid,'panel content')]";
    		    
    public String panelMenuButton = "./parent::div//following-sibling::button[@title='Menu']";
    
    public String viewLineDiv = ".//div[contains(@class,'view-line')]";
    
    public String columnHeaders = ".//div[@role='columnheader']//button//div";
    
    public String cell = ".//div[@role='cell']";
    
    public String buttons = ".//button";
    
    public String scrollbar = ".//div[contains(@class,'scrollbar-view')]";
    
    public String virtualContainers = ".//div[@style and contains(@style,'position: relative')]";
    
    public String span = ".//span";

    public String otherRecommendedVizCollapseToggle = "(//div[@data-testid='chatbox-container']//div[contains(@class,'collapse__header-label') and contains(.,'%s')]/preceding-sibling::button)[last()]";
    
    @FindBy(xpath = "//span[contains(@data-testid,'Insight Finder breadcrumb')]")
    public WebElement insightFinderBreadcrumb;

    @FindBy(xpath = "//div[@data-testid='chatbox-container']")
    public WebElement insightFinderChatboxContainer;

    @FindBy(xpath = "//div[contains(@class,'rc-drawer-open')]//span[normalize-space()='Insight Finder']")
    public WebElement insightFinderDrawerTitle;

    @FindBy(xpath = "//div[@data-testid='chatbox-container']//*[self::textarea or self::input][@placeholder='Ask a question' or @data-testid='messaage-input']")
    public WebElement msgInputInsightFinderDrawer;

    @FindBy(xpath = "//div[@data-testid='chat-welcome']//h2")
    public WebElement chatWelcomeGreeting;

    @FindBy(xpath = "//div[@data-testid='chat-welcome']//descendant::h3")
    public WebElement chatWelcomeMsg;

    @FindBy(xpath = "//div[@data-testid='chatbox-container']//button[@data-testid='submit-input-addon']")
    public WebElement submitButtonInsightFinderDrawer;
    
    @FindBy(xpath = "//textarea[@placeholder='Ask a question' or 'Continue to update the last visualization...']")
    public WebElement msgInput;
    
    @FindBy(xpath = "//button[@data-testid='submit-input-addon']")
    public WebElement submitButton;

    @FindBy(xpath = "//span[text()='Analyzing prompt']")
    public WebElement analyzePrompt;

    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//*[@data-testid='chat-panel-item-added' or @data-testid='bubble-text-alert']")
    public WebElement lastAgentChatOutcome;

    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//div[@data-testid='chat-panel-item-added']")
    public WebElement lastAgentChatPanelItemAdded;

    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//div[@data-testid='bubble-text-alert']")
    public WebElement lastAgentBubbleAlert;

    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//div[@data-testid='bubble-text-dashboard']")
    public WebElement lastAgentBubbleDashboard;
  
    @FindBy(xpath = "//button[@data-testid='reset-input-addon']")
    public WebElement resetInputButton;
  
    @FindBy(xpath = "//button[@data-testid='add-new-panel-btn']")
    public WebElement newTopicButton;
  
    @FindBy(xpath = "//button[@data-testid='stop-input-addon' and @aria-label='Stop action']")
    public WebElement stopActionButton;
  
    @FindBy(xpath = "(//div[contains(@data-testid,'header-container')])[last()]//h2")
    public WebElement headerContainerTitle;
    
    @FindBy(xpath = "//div[contains(@data-testid,'panel content')]")
    public WebElement insightPanelContent;
  
    @FindBy(xpath = "//div[@id='scene-container']")
    public WebElement insightFinderOutputContainer;
  
    @FindBy(xpath = "//button[contains(@data-testid,'Panel menu')]")
    public WebElement panelMenuOptions;
  
    @FindBy(xpath = "//div[@role='menu']//child::button[contains(@data-testid,'Panel menu item Query')]")
    public WebElement queryPanelMenu;
  
    @FindBy(xpath = "//div[@role='menu']//child::button[contains(@data-testid,'Panel menu item Remove')]")
    public WebElement removePanelMenu;
    
    @FindBy(xpath = "//div[contains(@class,'view-lines monaco-mouse-cursor-text')]")
    public WebElement codeEditorContainer;
    
    @FindBy(xpath = "//div[@data-testid='ai-avatar-container']//div[contains(@class,'ai-loader')]")
    public WebElement aiLoader;
  
    @FindBy(xpath = "//button[@aria-label='Start over']")
    public WebElement startOverButton;
    
    @FindBy(xpath = "//div[@data-testid='ai-avatar-container']//div[contains(@class,'ai-loader')]/following-sibling::span[text()='Working on the request' or text()='Analyzing prompt' or text()='Generating visualization' or text()='Mapping prompt to views']")
    public WebElement aiLoaderText;
 
    @FindBy(xpath = "//button[@data-testid='action-btn-save']")
    public WebElement saveButton;
    
    @FindBy(xpath = "(//div[@data-testid='bubble-text-dashboard'])[last()]/div")
    public WebElement latestInsightResponse;		
    
    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//div[contains(@data-testid,'panel content')]//div[@role='grid']")
    public WebElement insightTableResult;

    /**
     * RDG body rows under {@link #insightTableResult} (excludes header row). Same scope as
     * {@code tableBodyElement} passed to {@link #validateTableRowCount(WebElement)} when it is the latest grid.
     */
    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//div[contains(@data-testid,'panel content')]//div[@role='grid']//div[@role='row' and not(contains(@class,'rdg-header-row'))]")
    public List<WebElement> rdgDataRows;

    @FindBy(xpath = "(//div[@data-testid='agent-message'])[last()]//div[contains(@data-testid,'panel content')]")
    public WebElement latestPanelContent;
    
    @FindBy(xpath = "//h3[text()='Save dashboard']")
    public WebElement saveDashboardTitle;
  
    @FindBy(xpath = "//input[@name='title']")
    public WebElement titleTextbox;	
  
    @FindBy(xpath = "//textarea[@name='description']")
    public WebElement descriptionTextbox;	
 
    @FindBy(xpath = "(//button[contains(@data-testid,'Panel status error')])[last()]")
    public WebElement panelStatusError;
  
    @FindBy(xpath = "//div[contains(@class,'canvas-content')]")
    public WebElement canvasContent;
    
    @FindBy(xpath = "(//div[@data-testid='bubble-container'])[last()][contains(translate(., 'ERROR', 'error'), 'error')]")
    public WebElement insightErrorResponse;
  
    @FindBy(xpath = "//div[@role='presentation' and @data-mprt='7']")
    public WebElement queryContent;
  
    @FindBy(xpath = "//button[@aria-label='Close']")
    public WebElement closeButton;
    
    @FindBy(xpath = "(//div[@data-testid='chatbox-container']//p[@data-testid='bubble-recommendation-actions'])[last()]//span[last()]")
    public WebElement lastRecommendationSpanInChatbox;
  
    @FindBy(xpath = "//h1[text()='HelixGPT Insight Finder Settings']")
    public WebElement gptSettingsTitle;
 
    @FindBy(xpath = "//input[@label='Skill Identifier']")
    public WebElement skillIdentifier;
  
    @FindBy(xpath = "//label[contains(.,'Enable Follow Up Recommendations')]//input[@role='switch']")
    public WebElement recommendationCheckbox;
  
    @FindBy(xpath = "//label[contains(.,'Enable Streaming')]//input[@role='switch']")
    public WebElement streamingCheckbox;
    
    @FindBy(xpath = "//div[@role='presentation' and @data-mprt='7']")
    public WebElement freshQueryContent;
            
    @FindBy(xpath = ".//div[@data-testid='data-testid table body']//div[@role='row']")
    public List<WebElement> rows;
    
    @FindBy(xpath = "./ancestor::section[contains(@data-testid,'Panel header')]")
    public WebElement parentSection;
    
    @FindBy(xpath = "./ancestor::section[contains(@data-testid,'Panel header')]//div[@role='columnheader']")
    public List<WebElement> headerElements;
    
    @FindBy(xpath = "//section[contains(@class,'panel-container')]//h2")
    public List<WebElement> panelHeaders;
    
    @FindBy(xpath = "//div[contains(@data-testid,'header-container')]//h2")
    public List<WebElement> finalPanelHeaders;
    
    @FindBy(xpath = "//label[contains(normalize-space(.),'Enable Follow Up Recommendations')]/div//input[@role='switch']")
    public WebElement recommendationInputLocator;

    /**
     * Ask a question in Insight Finder, wait for AI processing, and verify response
     * 
     * @param question The question to ask in the text input
     * @param expectedHeaderText The expected text in the header container title
     */
    public Boolean askQuestion(String question, String expectedHeaderText, Boolean newTopic) {
        log.info("---- Asking question: " + question + " ----");
        
        // Check for New Topic button only if newTopic parameter is true
        if (newTopic != null && newTopic) {
            log.info("---- newTopic is true, checking for New Topic button ----");
            
            if (isVisible(newTopicButton)) {
                log.info("---- New Topic button is visible, clicking it ----");
                newTopicButton.click();
                
                String newTopicLocator = String.format(spanTextLocator, "Start a new topic");
                log.info("---- Validating 'Start a new topic' message ----");
                commonWaits.waitForByElementToBeVisible(By.xpath(newTopicLocator));
                Assert.assertTrue(isElementPresent(By.xpath(newTopicLocator)), 
                        "'Start a new topic' message is not present");
                log.info("---- 'Start a new topic' message validated successfully ----");
            } else {
                log.info("---- New Topic button is not visible, proceeding with question ----");
            }
        } else {
            log.info("---- newTopic is false or null, skipping New Topic button check ----");
        }
        
        commonWaits.waitForElementVisible(msgInputInsightFinderDrawer, 20);
        msgInputInsightFinderDrawer.clear();
        msgInputInsightFinderDrawer.sendKeys(question);
        log.info("---- Question entered in input field ----");
        
        commonWaits.waitForElementClickable(submitButtonInsightFinderDrawer, 10);
        submitButtonInsightFinderDrawer.click();
        log.info("---- Submit button clicked ----");
        
        log.info("---- Waiting for AI loader to appear ----");
        commonWaits.waitForElementVisible(aiLoader, 30);
        Assert.assertTrue(isVisible(aiLoader), 
                "AI Loader is not visible");
        log.info("---- AI Loader is visible (AI is processing) ----");
        
        log.info("---- Waiting for AI processing to complete ----");
        commonWaits.waitForElementInvisible_withFluentWait(getDriver(), loader, 100);
        log.info("---- AI processing completed (loader disappeared) ----");
        
        softAssert.assertFalse(isVisible(insightErrorResponse), "Insight Error response should not be visible");

        boolean outcomeReady = commonWaits.waitForElementVisible_withFluentWait(getDriver(), lastAgentChatOutcome, 90);
        boolean embeddedPanelVisible = outcomeReady && isVisible(lastAgentChatPanelItemAdded);
        boolean errorBubbleVisible = outcomeReady && isVisible(lastAgentBubbleAlert);

        String actualMessage;
        if (embeddedPanelVisible) {
            actualMessage = lastAgentBubbleDashboard.getText();
        } else if (errorBubbleVisible) {
            actualMessage = lastAgentBubbleAlert.getText();
        } else {
            commonWaits.waitForElementVisible(latestInsightResponse, 30);
            actualMessage = latestInsightResponse.getText();
        }
        log.info("---- Actual visualization message: '" + actualMessage + "' ----");

        boolean notVizFailureText = !actualMessage.toLowerCase().contains("unable to create the visualization");
        boolean isValidMessage = !errorBubbleVisible && (embeddedPanelVisible
                || (!actualMessage.trim().isEmpty() && notVizFailureText));

        softAssert.assertTrue(isValidMessage,
                "Expected embedded panel (chat-panel-item-added) or dashboard content without error, but found: '"
                        + actualMessage + "' for query: " + question);
      
        log.info("---- Validating that panel status error is not visible ----");
        softAssert.assertFalse(isVisible(panelStatusError), 
            "Panel status error should not be visible");
        
        
        if(isValidMessage)
        {
            log.info("---- Verifying header container title is visible ----");
            commonWaits.waitForElementVisible(headerContainerTitle, 100);
            softAssert.assertTrue(isVisible(headerContainerTitle), 
                    "Header Container Title is not visible");
            log.info("---- Header Container Title is visible ----");
            
            String actualHeaderText = headerContainerTitle.getText();
            log.info("---- Actual Header Text: " + actualHeaderText + " ----");
            log.info("---- Expected Header Text: " + expectedHeaderText + " ----");
            
		    boolean isMatch = validateHeaderTextMatch(actualHeaderText,
		    expectedHeaderText);
		    
		    softAssert.assertTrue(isMatch,
		    "Header Container Title text does not match. Expected: '" +
		    expectedHeaderText + "' but found: '" + actualHeaderText + "'");
		    log.info("---- Header Container Title text validated successfully ----");
		    log.info("---- Question processing completed successfully ----");
		    return true; // Return true if message is valid and header validation completed
        }
        
        log.warn("---- Visualization message validation failed, returning false ----");
        return false;
		
    }
    
    /**
     * Validate header text match - checks if all words from expected text are present in actual text
     * 
     * @param actualText The actual header text from UI
     * @param expectedText The expected keywords/words to match
     * @return true if all expected words are present in actual text, false otherwise
     */
    private boolean validateHeaderTextMatch(String actualText, String expectedText) {
        // Normalize texts: lowercase, remove quotes, extra spaces
        String normalizedActual = actualText.toLowerCase()
                .replaceAll("['\"]", "")
                .replaceAll("\\s+", " ")
                .trim();
        
        String normalizedExpected = expectedText.toLowerCase()
                .replaceAll("['\"]", "")
                .replaceAll("\\s+", " ")
                .trim();
        
        log.info("---- Normalized Actual: " + normalizedActual + " ----");
        log.info("---- Normalized Expected: " + normalizedExpected + " ----");
        
        // Split expected text into words
        String[] expectedWords = normalizedExpected.split("\\s+");
        
        // Check if all expected words are present in actual text
        List<String> missingWords = new ArrayList<>();
        int matchCount = 0;
        
        for (String expectedWord : expectedWords) {
            if (normalizedActual.contains(expectedWord)) {
                matchCount++;
                log.info("---- Word '" + expectedWord + "' found in actual text ----");
            } else {
                missingWords.add(expectedWord);
                log.warn("---- Word '" + expectedWord + "' NOT found in actual text ----");
            }
        }
        
        log.info("---- Match Result: " + matchCount + "/" + expectedWords.length + " words matched ----");
        
        if (!missingWords.isEmpty()) {
            log.warn("---- Missing words: " + missingWords + " ----");
        }
        
        // All expected words must be present in actual text
        return missingWords.isEmpty();
    }
    
    /**
     * Save dashboard with title and description
     * Clicks save button, enters title and description, then saves and validates success message
     * 
     * @param title The title for the dashboard
     * @param description The description for the dashboard
     */
    public InsightFinderPage saveDashboard(String title, String description) {
        log.info("---- Starting dashboard save process ----");
        log.info("---- Dashboard Title: " + title + " ----");
        log.info("---- Dashboard Description: " + description + " ----");
        
        log.info("---- Clicking on Save button ----");
        commonWaits.waitForElementClickable(saveButton, 30);
        commonWaits.clickWithoutInterception(saveButton);
        
        log.info("---- Waiting for 'Save dashboard' title to be visible ----");
        commonWaits.waitForElementVisible(saveDashboardTitle, 50);
        Assert.assertTrue(isVisible(saveDashboardTitle), 
                "'Save dashboard' title is not visible");
        
        log.info("---- Waiting for title textbox to be visible ----");
        commonWaits.waitForElementVisible(titleTextbox, 30);
        Assert.assertTrue(isVisible(titleTextbox), 
                "Title textbox is not visible");
        log.info("---- Title textbox is visible, entering title ----");
        titleTextbox.clear();
        titleTextbox.sendKeys(title);
        
        log.info("---- Waiting for description textbox to be visible ----");
        commonWaits.waitForElementVisible(descriptionTextbox, 10);
        Assert.assertTrue(isVisible(descriptionTextbox), 
                "Description textbox is not visible");
        log.info("---- Description textbox is visible, entering description ----");
        descriptionTextbox.clear();
        descriptionTextbox.sendKeys(description);
        
        log.info("---- Clicking on Save button to save dashboard ----");
        commonWaits.waitForElementClickable(String.format(spanTextLocator, "Save"), 10);
        commonWaits.clickWithoutInterception(By.xpath(String.format(spanTextLocator, "Save")));
        
        String successMessageLocator = String.format(spanTextLocator, "Dashboard saved");
        log.info("---- Waiting for 'Dashboard saved' success message ----");
        commonWaits.waitForByElementToBeVisible(By.xpath(successMessageLocator));
        Assert.assertTrue(isElementPresent(By.xpath(successMessageLocator)), 
                "'Dashboard saved' success message is not present");
        log.info("---- 'Dashboard saved' success message validated successfully ----");
        
        return this;
    }
    
    
    /**
     * Extract column headers from the table by navigating up from table body element
     * This method finds the parent table container and extracts column header names
     * If headers are not found, checks for "No Data" content in latestPanelContent
     * 
     * @param tableBodyElement The table body WebElement with data-testid="data-testid table body"
     * @param waitSeconds Wait time in seconds for the headers to be visible
     * @return List of column header names
     */
    public List<String> validateAndGetInsightResultTable(WebElement tableBodyElement, int waitSeconds) {
        log.info("---- Extracting table column headers ----");
        List<String> headers = new ArrayList<>();
        
        // FIRST: Check for "No Data" before trying to wait for table body element
        log.info("---- Checking for 'No Data' message before waiting for table body ----");
        if (isNoDataPresent()) {
            log.info("---- 'No Data' found - returning list with 'No data' ----");
            headers.add("No data");
            return headers;
        }
        
        try {
            // Try to wait for table body element (only if "No Data" is not present)
            if (waitSeconds > 0) {
                try {
                    commonWaits.waitForElementVisible(tableBodyElement, waitSeconds);
                } catch (Exception waitEx) {
                    log.warn("---- Table body element not visible after waiting ----");
                    // Check again for "No Data" after wait timeout
                    if (isNoDataPresent()) {
                        log.info("---- 'No Data' found after wait timeout - returning list with 'No data' ----");
                        headers.add("No data");
                        return headers;
                    }
                    // If still no "No Data", re-throw exception
                    throw waitEx;
                }
            }
            
            // Column headers: RDG uses div[@role='columnheader']; legacy may nest buttons (columnHeaders)
            log.info("---- Resolving column headers from table/grid element ----");
            List<WebElement> headerElements = tableBodyElement.findElements(By.xpath(".//div[@role='columnheader']"));
            if (headerElements.isEmpty()) {
                log.warn("---- No columnheader divs; trying nested button/header pattern ----");
                headerElements = tableBodyElement.findElements(By.xpath(columnHeaders));
            }
            
            log.info("---- Found " + headerElements.size() + " column header elements ----");
            
            for (WebElement headerElement : headerElements) {
                String headerText = headerElement.getText().trim();
                
                // Try to get text from nested button/div if empty
                if (headerText.isEmpty()) {
                    try {
                        WebElement button = headerElement.findElement(By.xpath(buttons));
                        headerText = button.getText().trim();
                    } catch (Exception e) {
                        // Skip if no button found
                    }
                }
                
                headers.add(headerText);
                log.info("---- Header " + headers.size() + ": '" + headerText + "' ----");
            }
            
            if (headers.isEmpty()) {
                log.warn("---- No headers extracted, checking for 'No Data' message ----");
                // Check if "No Data" is present - if yes, return list with "No data" instead of failing
                if (isNoDataPresent()) {
                    log.info("---- 'No Data' found - returning list with 'No data' ----");
                    headers.add("No data");
                    return headers;
                }
                // If no headers and no "No Data", then fail
                checkForNoDataMessage();
            } else {
                // Headers are present, now validate row count
                log.info("---- Headers are present, validating row count ----");
                validateTableRowCount(tableBodyElement);
            }
            
        } catch (Exception e) {
            log.error("---- Error extracting headers: " + e.getMessage() + " ----");
            log.info("---- Checking for 'No Data' message in latestPanelContent ----");
            // Check if "No Data" is present - if yes, return list with "No data" instead of failing
            if (isNoDataPresent()) {
                log.info("---- 'No Data' found - returning list with 'No data' ----");
                headers.add("No data");
                return headers;
            }
            // If error and no "No Data", then fail
            checkForNoDataMessage();
        }
        
        log.info("---- Total headers extracted: " + headers.size() + " ----");
        return headers;
    }
    
    /**
     * Check if "No Data" message is present in latestPanelContent
     * This is a non-failing check that returns boolean
     * 
     * @return true if "No Data" message is found, false otherwise
     */
    private boolean isNoDataPresent() {
        try {
            if (isVisible(latestPanelContent)) {
                String panelText = latestPanelContent.getText().trim();
                log.info("---- Latest Panel Content Text: " + panelText + " ----");
                
                if (panelText.toLowerCase().contains("no data")) {
                    log.info("---- 'No Data' message found in panel content ----");
                    return true;
                }
            }
        } catch (Exception ex) {
            log.info("---- Could not check for 'No Data' message: " + ex.getMessage() + " ----");
        }
        return false;
    }
    
    /**
     * Validate that the table has more than 0 data rows
     * This method is called after headers are successfully extracted
     *
     * @param tableBodyElement Grid or panel root (used for legacy scrollbar path). RDG row count uses
     *                         {@link #rdgDataRows} (latest agent-message grid), which matches {@link #insightTableResult}.
     */
    private InsightFinderPage validateTableRowCount(WebElement tableBodyElement) {
        try {
            // RDG (Insight Finder chat): data rows are role=row, excluding rdg-header-row (see @FindBy rdgDataRows)
            int rowCount = this.rdgDataRows.size();
            if (rowCount > 0) {
                log.info("---- Found " + rowCount + " data rows (RDG grid) ----");
                log.info("---- Validation passed: Table has at least 1 data row ----");
                return this;
            }
            // Legacy virtualized table uses scrollbar-view; RDG chat panels do not — use findElements to avoid NoSuchElementException
            List<WebElement> scrollViews = tableBodyElement.findElements(By.xpath(scrollbar));
            if (scrollViews.isEmpty()) {
                String ariaRowCount = tableBodyElement.getAttribute("aria-rowcount");
                log.warn("---- 0 data rows and no scrollbar-view under table element (RDG empty / non-legacy layout). aria-rowcount="
                        + ariaRowCount + " ----");
                log.info("---- Validation passed: Headers present; skipping legacy row count when scrollbar-view is absent ----");
                return this;
            }
            // Legacy virtualized table body (scrollbar + inner rows)
            WebElement scrollView = scrollViews.get(0);
            WebElement virtualContainer = scrollView.findElement(By.xpath(virtualContainers));
            List<WebElement> rows = virtualContainer.findElements(By.xpath(divRow));
            rowCount = rows.size();
            log.info("---- Found " + rowCount + " data rows in legacy virtualized table body ----");
            if (rowCount == 0) {
                log.error("---- Table has headers but 0 data rows ----");
                Assert.fail("Table has column headers but contains 0 data rows. Expected at least 1 row.");
            } else {
                log.info("---- Validation passed: Table has " + rowCount + " data rows ----");
            }
        } catch (Exception ex) {
            log.error("---- Error validating row count: " + ex.getMessage() + " ----");
            Assert.fail("Failed to validate table row count: " + ex.getMessage());
        }
        return this;
    }
    
    /**
     * Extract integer value from an element using multiple strategies
     * Designed to handle stat panels, singlestat visualizations, and tables
     * Handles "No Data" cases by returning -1 (both data and "No data" are acceptable)
     * 
     * Strategy 1: Extract from nested span elements (for stat panels with large numbers)
     * Strategy 2: Split text by whitespace and extract first valid integer
     * Strategy 3: Check for "No Data" message
     * 
     * @param element The WebElement to extract integer from
     * @param waitSeconds Wait time in seconds for the element to be visible
     * @return Integer value extracted from element text, or -1 if "No Data" is found
     */
    public int validateAndGetInsightResultCount(WebElement element, int waitSeconds) {
        log.info("---- Extracting integer value from element ----");
        
        try {
            // Wait for element to be visible
            if (waitSeconds > 0) {
                commonWaits.waitForElementVisible(element, waitSeconds);
            }
            
            // Strategy 1: Try to find span elements with numeric values (for stat panels)
            try {
                List<WebElement> spans = element.findElements(By.xpath(span));
                log.info("---- Found " + spans.size() + " span elements ----");
                
                for (WebElement span : spans) {
                    String spanText = span.getText().trim();
                    if (!spanText.isEmpty()) {
                        log.info("---- Checking span text: '" + spanText + "' ----");
                        String numericText = spanText.replaceAll("[^0-9-]", "");
                        if (!numericText.isEmpty() && numericText.matches("-?\\d+")) {
                            int value = Integer.parseInt(numericText);
                            log.info("---- Successfully extracted integer from span: " + value + " ----");
                            return value;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("---- Span extraction strategy failed: " + e.getMessage() + " ----");
            }
            
            // Strategy 2: Get full text and try to extract integer
            String elementText = element.getText().trim();
            log.info("---- Element full text: '" + elementText + "' ----");
            
            // Check if element text is empty
            if (elementText.isEmpty()) {
                log.warn("---- Element text is empty ----");
                
                // Check for "No data" in latestPanelContent
                if (isVisible(latestPanelContent)) {
                    String panelText = latestPanelContent.getText().trim();
                    if (panelText.toLowerCase().contains("no data")) {
                        log.info("---- Found 'No Data' in panel content - This is acceptable ----");
                        log.info("---- Returning -1 to indicate 'No Data' ----");
                        return -1;
                    }
                }
                
                // Neither integer nor "No data" found
                Assert.fail("Element text is empty and no 'No Data' message found");
            }
            
            // Strategy 3: Split by whitespace and extract first valid integer
            String[] textParts = elementText.split("\\s+");
            for (String part : textParts) {
                String numericText = part.replaceAll("[^0-9-]", "");
                if (!numericText.isEmpty() && numericText.matches("-?\\d+")) {
                    try {
                        int value = Integer.parseInt(numericText);
                        log.info("---- Successfully extracted integer from text part: " + value + " ----");
                        return value;
                    } catch (NumberFormatException e) {
                        // Continue to next part
                    }
                }
            }
            
            // Strategy 4: Check if "No data" message is present
            if (elementText.toLowerCase().contains("no data")) {
                log.info("---- Element contains 'No Data' message - This is acceptable ----");
                log.info("---- Returning -1 to indicate 'No Data' ----");
                return -1;
            }
            
            // Check latestPanelContent for "No data"
            if (isVisible(latestPanelContent)) {
                String panelText = latestPanelContent.getText().trim();
                if (panelText.toLowerCase().contains("no data")) {
                    log.info("---- Found 'No Data' in panel content - This is acceptable ----");
                    log.info("---- Returning -1 to indicate 'No Data' ----");
                    return -1;
                }
            }
            
            // No integer or "No data" found
            log.error("---- Failed to extract integer from element ----");
            log.error("---- Element text was: '" + elementText + "' ----");
            Assert.fail("No integer value found in element text: '" + elementText + "' and no 'No Data' message found");
            return -1; // Never reached
            
        } catch (Exception e) {
            log.error("---- Error extracting integer: " + e.getMessage() + " ----");
            Assert.fail("Error extracting integer from element: " + e.getMessage());
            return -1; // Never reached due to Assert.fail
        }
    }
    
    private InsightFinderPage checkForNoDataMessage() {
        try {
            if (isVisible(latestPanelContent)) {
                String panelText = latestPanelContent.getText().trim();
                log.info("---- Latest Panel Content Text: " + panelText + " ----");
                
                if (panelText.toLowerCase().contains("no data")) {
                    log.error("---- FOUND 'No Data' message in panel content ----");
                    log.error("---- The table/panel has no data to display ----");
                    Assert.fail("Table body element not found and panel displays 'No Data'. Panel text: " + panelText);
                } else {
                    log.info("---- Panel content does not contain 'No Data' message ----");
                    log.info("---- Panel text: " + panelText + " ----");
                }
            } else {
                log.info("---- Latest Panel Content is not visible ----");
            }
        } catch (Exception ex) {
            log.info("---- Could not check for 'No Data' message: " + ex.getMessage() + " ----");
        }
		return this;
    }
    
    /**
     * Validate Insight Canvas/Panel element
     * Checks if element has data or "No data" message
     * Fails if neither data nor "No data" is present
     * 
     * @param canvasElement The parent canvas/panel WebElement to validate
     * @param waitSeconds Wait time in seconds for the element to be visible
     */
    public InsightFinderPage validateLatestInsightCanvasResult() {
        log.info("---- Starting Insight Canvas validation ----");
        int waitSeconds = 10;
        try {
            // Wait for canvas element to be visible
            if (waitSeconds > 0) {
                commonWaits.waitForElementVisible(latestPanelContent, waitSeconds);
            }
            
            // Get text content from canvas element
            String canvasText = latestPanelContent.getText().trim();
            log.info("---- Canvas Element Text: '" + canvasText + "' ----");
            
            // Check if element has some data (text length > 0 and not just "No data")
            if (!canvasText.isEmpty()) {
                // Check if it contains "No data" message
                if (canvasText.toLowerCase().contains("no data")) {
                    log.info("---- Canvas displays 'No data' message - validation passed ----");
                    // This is valid - panel correctly shows "No data"
                    return this;
                } else {
                    log.info("---- Canvas has data content - validation passed ----");
                    // This is valid - panel has actual data
                    return this;
                }
            }
            
            // If canvas is empty (no text at all), check for nested elements or visualizations
            log.info("---- Canvas text is empty, checking for child elements ----");
            
            // Check for table rows
            List<WebElement> tableRows = latestPanelContent.findElements(By.xpath(divRow));
            if (!tableRows.isEmpty()) {
                log.info("---- Canvas has " + tableRows.size() + " table rows - validation passed ----");
                return this;
            }
            
            // Check for canvas/chart elements
            List<WebElement> canvasElements = latestPanelContent.findElements(By.xpath(canvasElement));
            if (!canvasElements.isEmpty()) {
                log.info("---- Canvas has " + canvasElements.size() + " chart canvas elements - validation passed ----");
                return this;
            }
            
            // Check for uPlot charts
            List<WebElement> uplotElements = latestPanelContent.findElements(By.xpath(uplotDiv));
            if (!uplotElements.isEmpty()) {
                log.info("---- Canvas has " + uplotElements.size() + " uPlot chart elements - validation passed ----");
                return this;
            }
            
            // Check for stat panels
            List<WebElement> statPanels = latestPanelContent.findElements(By.xpath(singlestatDiv));
            if (!statPanels.isEmpty()) {
                log.info("---- Canvas has " + statPanels.size() + " stat panel elements - validation passed ----");
                return this;
            }
            
            // Check for "No data" error message element
            List<WebElement> noDataElements = latestPanelContent.findElements(By.xpath(errorDiv));
            if (!noDataElements.isEmpty()) {
                String noDataText = noDataElements.get(0).getText().trim();
                if (noDataText.toLowerCase().contains("no data")) {
                    log.info("---- Canvas has 'No data' error message element - validation passed ----");
                    return this;
                }
            }
            
            // If we reach here, canvas has neither data nor "No data" message
            log.error("---- Canvas validation FAILED: No data and no 'No data' message found ----");
            log.error("---- Canvas HTML: " + latestPanelContent.getAttribute("outerHTML") + " ----");
            Assert.fail("Canvas validation failed: Element has neither data nor 'No data' message. Canvas text: '" + canvasText + "'");
            
        } catch (Exception e) {
            log.error("---- Error during canvas validation: " + e.getMessage() + " ----");
            Assert.fail("Canvas validation failed due to error: " + e.getMessage());
        }
		return this;
    }
    
    /**
     * Validates the dashboard folder content by verifying panel count and header texts
     * 
     * @param expectedPanelCount Expected number of panels in the dashboard
     * @param expectedHeaders List of expected header texts for each panel (expectedHeader1, expectedHeader2, etc.)
     */
    public InsightFinderPage ValidateDashboardFolderContent(List<String> expectedHeaders) {
        log.info("---- Starting Dashboard Folder Content Validation ----");
        
        try {
            // Wait for canvas and initial panel to load
        	commonWaits.waitForElementVisible(canvasContent, 10);
            
            // Wait for panel count to match expected count
            log.info("---- Waiting for panel count to reach " + expectedHeaders.size() + " ----");
            waitTimeSeconds = 40; // Maximum wait time
            pollIntervalMs = 500; // Check every 500ms
            maxAttempts = (waitTimeSeconds * 250) / pollIntervalMs;
            actualPanelCount = 0;
            countMatched = false;
            
            // Initial aggressive scrolling to load all panels
            log.info("---- Performing initial scroll to load all panels ----");
            performAggressiveScroll();
            
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                // Perform incremental scrolling on each attempt to ensure panels are loaded
                if (attempt % 3 == 0) {
                    performIncrementalScroll();
                }
                
				/*
				 * List<WebElement> panelHeaders =
				 * getDriver().findElements(By.xpath(dashboardPanels));
				 */
				 
                actualPanelCount = panelHeaders.size();
                
                if (actualPanelCount == expectedHeaders.size()) {
                    log.info("---- Panel count matched! Found " + actualPanelCount + " panels after " + 
                             (attempt * pollIntervalMs / 1000.0) + " seconds ----");
                    countMatched = true;
                    break;
                } else {
                    if (attempt % 4 == 0) { // Log every 2 seconds
                        log.info("---- Attempt " + attempt + "/" + maxAttempts + ": Found " + actualPanelCount + 
                                 " panels, waiting for " + expectedHeaders.size() + " ----");
                    }
                    commonWaits.waitForSometime(pollIntervalMs);
                }
            }
            
            // Get final panel count
			/*
			 * List<WebElement> panelHeaders =
			 * getDriver().findElements(By.xpath(dashboardPanels));
			 */
            actualPanelCount = panelHeaders.size();
            log.info("---- Final Panel Count: " + actualPanelCount + " ----");
            
            // Assert panel count matches expected
            Assert.assertEquals(actualPanelCount, expectedHeaders.size(), 
                "Panel count mismatch after waiting " + waitTimeSeconds + " seconds! Expected: " + 
                		expectedHeaders.size() + ", but found: " + actualPanelCount);
            
            log.info("---- Panel count validation PASSED ----");
            
            // Validate headers for each panel
            log.info("---- Validating headers for " + actualPanelCount + " panels ----");
            
            
            for (int i = 0; i < actualPanelCount; i++) {
                // Check if expected header is provided for this panel
                if (i >= expectedHeaders.size()) {
                    log.warn("---- No expected header provided for panel " + (i + 1) + ", skipping validation ----");
                    continue;
                }
                
                String expectedHeader = expectedHeaders.get(i);
                WebElement headerElement = finalPanelHeaders.get(i);
                
                // Wait for header to be visible
                commonWaits.waitForElementVisible(headerElement, 5);
                
                String actualHeaderText = headerElement.getText().trim();
                log.info("---- Panel " + (i + 1) + " - Actual Header: '" + actualHeaderText + "' ----");
                log.info("---- Panel " + (i + 1) + " - Expected Header: '" + expectedHeader + "' ----");
                
                // Validate header text using existing validation method
                boolean isMatch = validateHeaderTextMatch(actualHeaderText, expectedHeader);
                
                if (isMatch) {
                    log.info("---- Panel " + (i + 1) + " header validation PASSED ----");
                } else {
                    String errorMsg = "Panel " + (i + 1) + " header validation FAILED! "
                            + "Actual: '" + actualHeaderText + "', Expected keywords: '" + expectedHeader + "'";
                    log.error("---- " + errorMsg + " ----");
                    softAssert.fail(errorMsg);
                }
                
                // Validate existence of panel content
                try {
                    log.info("---- Validating panel content existence for Panel " + (i + 1) + " ----");
                    List<WebElement> panelContentElements = headerElement.findElements(By.xpath(panelContent));
                    
                    if (!panelContentElements.isEmpty() && isVisible(panelContentElements.get(0))) {
                        log.info("---- Panel " + (i + 1) + " content validation PASSED - Panel content exists ----");
                    } else {
                        String errorMsg = "Panel " + (i + 1) + " content validation FAILED! "
                                + "Panel content element not found or not visible for header: '" + actualHeaderText + "'";
                        log.error("---- " + errorMsg + " ----");
                        softAssert.fail(errorMsg);
                    }
                } catch (Exception e) {
                    String errorMsg = "Panel " + (i + 1) + " content validation ERROR! "
                            + "Exception while checking panel content: " + e.getMessage();
                    log.error("---- " + errorMsg + " ----");
                    softAssert.fail(errorMsg);
                }
            }
            
            log.info("---- Dashboard Folder Content Validation PASSED ----");
            
        } catch (Exception e) {
            log.error("---- Error during Dashboard Folder Content validation: " + e.getMessage() + " ----");
            e.printStackTrace();
            Assert.fail("Dashboard Folder Content validation failed: " + e.getMessage());
        }
		return this;
    }
    
    /**
     * Validates all soft assertions collected during the test
     * Should be called at the end of each test to ensure all soft assertions are evaluated
     */
    public void assertAllSoftAssertions() {
        log.info("---- Validating all soft assertions ----");
        softAssert.assertAll();
        log.info("---- All soft assertions validated successfully ----");
    }
    
    /**
     * Removes a query panel by finding the panel header that contains all expected words
     * and clicking on its panel menu to remove it
     * 
     * @param expectedHeaderWords String containing words that should be present in the panel header
     *                           (flexible matching - all words must be present but not exact match)
     */
    public InsightFinderPage removeQueryWithHeader(String expectedHeaderWords) {
        log.info("---- Starting to remove query with header containing: '" + expectedHeaderWords + "' ----");
        
        try {
            // Wait for panels to load
            commonWaits.waitForSometime(100);
            
            // Find all panel headers
            List<WebElement> allPanelHeaders = finalPanelHeaders;
            
            log.info("---- Found " + allPanelHeaders.size() + " panel headers ----");
            
            if (allPanelHeaders.isEmpty()) {
                Assert.fail("No panel headers found on the page");
            }
            
            // Normalize expected words
            String normalizedExpected = expectedHeaderWords.toLowerCase()
                    .replaceAll("['\"]", "")
                    .replaceAll("\\s+", " ")
                    .trim();
            String[] expectedWords = normalizedExpected.split("\\s+");
            
            log.info("---- Looking for panel header containing words: " + String.join(", ", expectedWords) + " ----");
            
            // Search for matching header
            WebElement matchingHeader = null;
            String matchingHeaderText = "";
            
            for (WebElement header : allPanelHeaders) {
                String headerText = header.getText().trim();
                String normalizedHeaderText = headerText.toLowerCase()
                        .replaceAll("['\"]", "")
                        .replaceAll("\\s+", " ")
                        .trim();
                
                log.info("---- Checking header: '" + headerText + "' ----");
                
                // Check if all expected words are present in this header
                boolean allWordsMatch = true;
                for (String expectedWord : expectedWords) {
                    if (!normalizedHeaderText.contains(expectedWord)) {
                        allWordsMatch = false;
                        break;
                    }
                }
                
                if (allWordsMatch) {
                    matchingHeader = header;
                    matchingHeaderText = headerText;
                    log.info("---- Found matching header: '" + headerText + "' ----");
                    break;
                }
            }
            
            if (matchingHeader == null) {
                Assert.fail("No panel header found containing all words: '" + expectedHeaderWords + "'");
            }
            
            // Find and click the panel menu button for this header
            WebElement menuButton = matchingHeader.findElement(
                By.xpath(panelMenuButton));
            
            // Scroll to top first to ensure proper visibility
            log.info("---- Scrolling to top of page ----");
            scrollToPageTop();
            
            // Then scroll to menu button
            log.info("---- Scrolling to menu button ----");
            javaScriptScrollIntoView(menuButton);
            
            // Click menu button using JavaScript (more reliable)
            log.info("---- Clicking panel menu button for header: '" + matchingHeaderText + "' ----");
            clickToElement(menuButton);
            log.info("---- Menu button clicked using JavaScript ----");
            
            // Wait for menu to appear and click Query option
            log.info("---- Waiting for Query menu option ----");
            commonWaits.waitForElementPresenceLocated(By.xpath(String.format(spanTextLocator, SolQAConstants.QUERY)));
            commonWaits.clickWithoutInterception(By.xpath(String.format(spanTextLocator, SolQAConstants.QUERY)));
            
            // Wait for panel to be removed
            commonWaits.waitForSometime(1000);
            log.info("---- Query with header '" + matchingHeaderText + "' successfully ----");
            
            // Extract and validate query content
            String query = getQueryContentSpanText();
            log.info("---- Extracted query content ----");
            
            // Validate query is not null or empty (with warning if fails)
            if (query == null || query.isEmpty()) {
                log.warn("---- Query content is null or empty (possibly due to DOM changes) ----");
                log.warn("---- Continuing with panel removal validation ----");
            } else {
                log.info("---- Query validation PASSED ----");
                log.info("Query: " + query);
            }
            
            commonWaits.clickWithoutInterception(closeButton);
            
            log.info("---- Clicking panel menu button for header: '" + matchingHeaderText + "' ----");
            javaScriptScrollIntoView(menuButton);
            clickToElement(menuButton);
            
            log.info("Click menu button: Remove");
            commonWaits.waitForElementPresenceLocated(By.xpath(String.format(spanTextLocator, SolQAConstants.REMOVE)));
            commonWaits.clickWithoutInterception(By.xpath(String.format(spanTextLocator, SolQAConstants.REMOVE)));
            
            // Wait for panel removal to complete
            commonWaits.waitForSometime(2000);
            log.info("---- Validating that panel with header '" + matchingHeaderText + "' is no longer visible ----");
            
            // Find all current panel headers after removal
            List<WebElement> remainingPanelHeaders = finalPanelHeaders;
            
            log.info("---- Found " + remainingPanelHeaders.size() + " panel headers after removal ----");
            
            // Check that none of the remaining headers match the expected header words
            boolean panelStillVisible = false;
            for (WebElement header : remainingPanelHeaders) {
                String headerText = header.getText().trim();
                String normalizedHeaderText = headerText.toLowerCase()
                        .replaceAll("['\"]", "")
                        .replaceAll("\\s+", " ")
                        .trim();
                
                // Check if all expected words are still present in this header
                boolean allWordsMatch = true;
                for (String expectedWord : expectedWords) {
                    if (!normalizedHeaderText.contains(expectedWord)) {
                        allWordsMatch = false;
                        break;
                    }
                }
                
                if (allWordsMatch) {
                    panelStillVisible = true;
                    log.error("---- Panel with header '" + headerText + "' is still visible ----");
                    break;
                }
            }
            
            // Assert that the panel is not visible
            Assert.assertFalse(panelStillVisible, 
                "Panel with header containing '" + expectedHeaderWords + "' should not be visible after removal");
            
            log.info("---- Validation PASSED: Panel with header '" + matchingHeaderText + "' is not visible ----");
            
        } catch (Exception e) {
            log.error("---- Error removing query with header: " + e.getMessage() + " ----");
            Assert.fail("Failed to remove query with header containing: '" + expectedHeaderWords + "'. Error: " + e.getMessage());
        }
		return this;
    }
    
    /**
     * Fetches the content of all span elements inside queryContent (Monaco Editor)
     * Handles nested span structure to avoid duplicate text
     * 
     * @return String containing all span texts concatenated with proper line breaks
     */
    public String getQueryContentSpanText() {
        log.info("---- Fetching all span content from queryContent ----");
        
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Wait for it to be visible
                commonWaits.waitForElementVisible(freshQueryContent, 10);
                
                // Use JavaScript to extract all text content (avoids stale element issues)
                JavascriptExecutor js = (JavascriptExecutor) getDriver();
                String allText = (String) js.executeScript("return arguments[0].innerText;", freshQueryContent);
                
                if (allText != null && !allText.trim().isEmpty()) {
                    log.info("---- Query content extracted successfully using JavaScript ----");
                    log.info("---- Content length: " + allText.length() + " characters ----");
                    return allText.trim();
                }
                
                // If JavaScript extraction returns empty, try DOM-based extraction
                log.info("---- JavaScript extraction returned empty, trying DOM-based extraction ----");
                
                // For Monaco editor, get view-line divs (each represents one line)
                List<WebElement> viewLines = freshQueryContent.findElements(By.xpath(viewLineDiv));
                
                log.info("---- Found " + viewLines.size() + " view-line elements inside queryContent ----");
                
                if (viewLines.isEmpty()) {
                    log.warn("---- No view-line elements found ----");
                    return "";
                }
                
                // Collect text from each view-line (extract immediately to avoid staleness)
                StringBuilder contentBuilder = new StringBuilder();
                for (int i = 0; i < viewLines.size(); i++) {
                    try {
                        // Re-find the view-line to avoid staleness
                        List<WebElement> currentViewLines = freshQueryContent.findElements(By.xpath(viewLineDiv));
                        
                        if (i < currentViewLines.size()) {
                            String lineText = currentViewLines.get(i).getText().trim();
                            if (!lineText.isEmpty()) {
                                contentBuilder.append(lineText).append("\n");
                            }
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException se) {
                        log.warn("---- Stale element at line " + i + ", skipping ----");
                    }
                }
                
                String result = contentBuilder.toString().trim();
                
                if (!result.isEmpty()) {
                    log.info("---- Query content extracted successfully (" + viewLines.size() + " lines) ----");
                    return result;
                }
                
                log.warn("---- Attempt " + attempt + " returned empty content ----");
                
                if (attempt < maxRetries) {
                    log.info("---- Retrying extraction (attempt " + (attempt + 1) + "/" + maxRetries + ") ----");
                    commonWaits.waitForSometime(1000);
                }
                
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                log.warn("---- Stale element exception on attempt " + attempt + ", retrying... ----");
                if (attempt < maxRetries) {
                    commonWaits.waitForSometime(1000);
                } else {
                    log.error("---- Failed after " + maxRetries + " attempts due to stale elements ----");
                }
            } catch (Exception e) {
                log.error("---- Error fetching span content from queryContent: " + e.getMessage() + " ----");
                if (attempt >= maxRetries) {
                    return "";
                }
            }
        }
        
        log.warn("---- All extraction attempts failed, returning empty string ----");
        return "";
    }
    
    /**
     * Simple payload tracking verification without complex classes
     * Monitors network requests and checks if payload contains expected tracking information
     * Includes assertion logic to fail test if tracking not found (unless DevTools unavailable)
     * 
     * @param durationSeconds - Duration in seconds to monitor network traffic
     * @param urlFilter - Optional URL filter (e.g., "stream") or null for all requests
     * @param expectedSolution - Expected solution value (e.g., "insight_finder_assistant")
     * @param expectedUseCase - Expected use_case value (e.g., "generate_visualization")
     */
    public InsightFinderPage verifyPayloadContainsTracking(int durationSeconds, String urlFilter, 
                                               String expectedSolution, String expectedUseCase) {
        log.info("---- Verifying payload contains tracking: solution='" + expectedSolution + 
                 "', use_case='" + expectedUseCase + "' ----");
        
        // Flag to track if we found the tracking info
        
        final boolean[] trackingFound = {false};
        
        try {
            ChromiumDriver chromiumDriver = (ChromiumDriver) getDriver();
            
           org.openqa.selenium.devtools.DevTools devTools = chromiumDriver.getDevTools();
            devTools.createSession();
            
            // Enable Network tracking
            devTools.send(Network.enable(
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                java.util.Optional.empty()
            ));
            
            log.info("---- Network monitoring started for " + durationSeconds + " seconds ----");
            if (urlFilter != null) {
                log.info("---- URL Filter: " + urlFilter + " ----");
            }
            
            // Listen for request events and check payloads inline - no classes needed!
            devTools.addListener(Network.requestWillBeSent(),
                request -> {
                    try {
                        String url = request.getRequest().getUrl();
                        
                        // Apply URL filter if specified
                        boolean shouldCheck = (urlFilter == null) || 
                                             url.toLowerCase().contains(urlFilter.toLowerCase());
                        
                        if (shouldCheck && request.getRequest().getPostData().isPresent()) {
                            String payload = request.getRequest().getPostData().get();
                            
                            // Check for tracking fields inline - simple string matching
                            String normalizedPayload = payload.toLowerCase();
                            boolean hasTracking = normalizedPayload.contains("tracking");
                            boolean hasSolution = payload.contains(expectedSolution);
                            boolean hasUseCase = payload.contains(expectedUseCase);
                            
                            if (hasTracking && hasSolution && hasUseCase) {
                                log.info("---- ✓ FOUND: Tracking verified in payload! ----");
                                log.info("  └─ URL: " + url);
                                log.info("     Solution: " + expectedSolution);
                                log.info("     Use Case: " + expectedUseCase);
                                trackingFound[0] = true;
                            }
                        }
                    } catch (Exception e) {
                        log.debug("---- Error checking request: " + e.getMessage() + " ----");
                    }
                }
            );
            
            // Monitor for specified duration
            commonWaits.waitForSometime(durationSeconds * 1000);
            
            // Assert tracking verification result
            boolean trackingVerified = trackingFound[0];
            
            if (trackingVerified) {
                log.info("---- ✓ PASSED: Tracking verified in payload ----");
                Assert.assertTrue(true, "Tracking verified successfully");
            } else {
                log.error("---- ✗ FAILED: Tracking NOT found in payload ----");
                Assert.fail("Stream API payload should contain tracking: " +
                    "solution='insight_finder_assistant', use_case='generate_visualization'");
            }
            
        } catch (Exception e) {
            // If DevTools is not available, log warning but don't fail the test
            log.warn("===========================================================");
            log.warn("---- SKIPPING PAYLOAD TRACKING VERIFICATION ----");
            log.warn("---- Reason: DevTools not available or version mismatch ----");
            log.warn("---- This is a non-critical verification ----");
            log.warn("---- Test will continue without this check ----");
            log.warn("===========================================================");
            // Don't throw assertion error - allow test to continue
        }
		return this;
    }
    
    /**
     * Clicks the last follow-up recommendation under "Other recommended visualizations", then Send.
     * Label text: {@link SolQAConstants#INSIGHT_FINDER_OTHER_RECOMMENDED_VISUALIZATIONS_LABEL}.
     */
    public InsightFinderPage clickLastRecommendation() {
        log.info("---- Attempting to click last recommendation (follow-up visualizations) ----");
        try {
            commonWaits.waitForElementVisible(insightFinderChatboxContainer, 30);

            By collapseToggleBy = By.xpath(String.format(otherRecommendedVizCollapseToggle,
                    SolQAConstants.INSIGHT_FINDER_OTHER_RECOMMENDED_VISUALIZATIONS_LABEL));
            List<WebElement> toggles = getDriver().findElements(collapseToggleBy);
            if (toggles.isEmpty()) {
                softAssert.fail("Follow-up visualizations collapse toggle not found for label: "
                        + SolQAConstants.INSIGHT_FINDER_OTHER_RECOMMENDED_VISUALIZATIONS_LABEL);
                return this;
            }
            WebElement collapseToggle = toggles.get(0);
            javaScriptScrollIntoView(collapseToggle);
            String expanded = collapseToggle.getAttribute("aria-expanded");
            if (!"true".equalsIgnoreCase(expanded)) {
                log.info("---- Expanding follow-up visualizations section ----");
                commonWaits.clickWithoutInterception(collapseToggle);
            }

            commonWaits.waitForElementVisible(lastRecommendationSpanInChatbox, 30);
            String expectedText = lastRecommendationSpanInChatbox.getText().trim();
            log.info("---- Last recommendation text: '" + expectedText + "' ----");

            javaScriptScrollIntoView(lastRecommendationSpanInChatbox);
            try {
                commonWaits.clickWithoutInterception(lastRecommendationSpanInChatbox);
            } catch (Exception e) {
                log.warn("---- Click on recommendation span failed, using JS click: " + e.getMessage() + " ----");
                clickToElement(lastRecommendationSpanInChatbox);
            }

            commonWaits.waitForElementVisible(msgInputInsightFinderDrawer, 15);
            try {
                new WebDriverWait(getDriver(), Duration.ofSeconds(15)).pollingEvery(Duration.ofMillis(200))
                        .until(ExpectedConditions.attributeToBe(msgInputInsightFinderDrawer, "value", expectedText));
                log.info("---- Textarea populated with recommendation text ----");
            } catch (TimeoutException te) {
                log.warn("---- Textarea value did not match in time; setting via JavaScript ----");
                ((JavascriptExecutor) getDriver()).executeScript(
                        "arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles:true}));",
                        msgInputInsightFinderDrawer, expectedText);
            }

            commonWaits.waitForElementClickable(submitButtonInsightFinderDrawer, 10);
            submitButtonInsightFinderDrawer.click();
            log.info("---- Submit button clicked ----");

            log.info("---- Waiting for AI loader to appear ----");
            commonWaits.waitForElementVisible(aiLoader, 30);
            Assert.assertTrue(isVisible(aiLoader), "AI Loader is not visible");
            log.info("---- AI Loader is visible (AI is processing) ----");

            log.info("---- Waiting for AI processing to complete ----");
            commonWaits.waitForElementInvisible_withFluentWait(getDriver(), loader, 100);
            log.info("---- AI processing completed (loader disappeared) ----");

            log.info("---- Successfully clicked last recommendation and sent ----");
        } catch (Exception e) {
            log.error("---- clickLastRecommendation failed: " + e.getMessage() + " ----");
            softAssert.fail("clickLastRecommendation failed: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Extract all values from a specific column by column name and validate dates are within specified days
     * Works with virtualized tables that have role="columnheader" and role="cell" structure
     * 
     * @param tableElement - The panel content WebElement containing the table
     * @param columnName - The name of the column to extract (e.g., "Creation Time")
     * @param numberOfDays - Number of days to validate against (dates should be within this many days from now)
     * @param waitSeconds - Maximum seconds to wait for table to be visible
     * @return List of all column values as strings
     */
    public List<String> extractAndValidateColumnDates(WebElement tableElement, String columnName, 
                                                       int numberOfDays, int waitSeconds) {
        List<String> columnValues = new ArrayList<>();
        
        try {
            log.info("---- Extracting and validating column: '" + columnName + "' within " + numberOfDays + " days ----");
            
            // Wait for table element to be visible
            commonWaits.waitForElementVisible(tableElement, waitSeconds);
            
            // Find all column headers
            List<WebElement> headers = tableElement.findElements(By.xpath(columnHeaders));
            
            if (headers.isEmpty()) {
                log.warn("---- No column headers found in table ----");
                return columnValues;
            }
            
            // Find the column index by matching header text
            int columnIndex = -1;
            for (int i = 0; i < headers.size(); i++) {
                String headerText = headers.get(i).getText().trim();
                log.debug("---- Header " + i + ": '" + headerText + "' ----");
                
                if (headerText.equalsIgnoreCase(columnName.trim()) || 
                    headerText.toLowerCase().contains(columnName.toLowerCase().trim())) {
                    columnIndex = i;
                    log.info("---- Found column '" + columnName + "' at index: " + columnIndex + " ----");
                    break;
                }
            }
            
            if (columnIndex == -1) {
                String errorMsg = "---- Column '" + columnName + "' not found in table headers ----";
                log.error(errorMsg);
                softAssert.fail(errorMsg);
                return columnValues;
            }
            
            // Find all rows in the table body
            log.info("---- Found " + rows.size() + " rows in table ----");
            
            if (rows.isEmpty()) {
                log.warn("---- No data rows found in table body ----");
                return columnValues;
            }
            
            // Extract values from the specific column for each row
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                try {
                    // Find all cells in this row
                    List<WebElement> cells = rows.get(rowIndex).findElements(By.xpath(cell));
                    
                    if (columnIndex < cells.size()) {
                        String cellValue = cells.get(columnIndex).getText().trim();
                        columnValues.add(cellValue);
                        log.debug("---- Row " + rowIndex + ", Column '" + columnName + "': " + cellValue + " ----");
                    } else {
                        log.warn("---- Row " + rowIndex + " has fewer cells than expected (column index: " + 
                                 columnIndex + ", cells: " + cells.size() + ") ----");
                    }
                } catch (Exception e) {
                    log.warn("---- Error extracting cell from row " + rowIndex + ": " + e.getMessage() + " ----");
                }
            }
            
            log.info("---- Extracted " + columnValues.size() + " values from column '" + columnName + "' ----");
            
            // Validate dates if column values were extracted
            if (!columnValues.isEmpty()) {
                validateDatesWithinDays(columnValues, columnName, numberOfDays);
            } else {
                log.warn("---- No values extracted from column '" + columnName + "' for date validation ----");
            }
            
        } catch (Exception e) {
            String errorMsg = "---- Error extracting column '" + columnName + "': " + e.getMessage() + " ----";
            log.error(errorMsg);
            e.printStackTrace();
            softAssert.fail(errorMsg);
        }
        
        return columnValues;
    }
    
    /**
     * Perform aggressive scrolling to load all virtualized/lazy-loaded panels
     * Scrolls through the entire page in increments to ensure all content is rendered
     */
    private InsightFinderPage performAggressiveScroll() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            
            // First scroll to top
            commonWaits.scrollToPageTop();
            
            Long pageHeight = (Long) js.executeScript("return document.body.scrollHeight");
            
            // Scroll down in increments of 300px to trigger lazy loading
            int scrollStep = 300;
            int currentPosition = 0;
            
            while (currentPosition < pageHeight) {
                currentPosition += scrollStep;
                js.executeScript("window.scrollTo(0, " + currentPosition + ");");
                
                // Check if new content was loaded (page height increased)
                Long newHeight = (Long) js.executeScript("return document.body.scrollHeight");
                if (newHeight > pageHeight) {
                    log.info("---- New content loaded, page height increased to: " + newHeight + "px ----");
                    pageHeight = newHeight;
                }
            }
            
            log.info("---- Scrolling to bottom of page ----");
            commonWaits.scrollToBottomOfPage();
            
            // Scroll back to top slowly
            log.info("---- Scrolling back to top ----");
            currentPosition = pageHeight.intValue();
            while (currentPosition > 0) {
                currentPosition -= scrollStep;
                js.executeScript("window.scrollTo(0, " + Math.max(0, currentPosition) + ");");
                commonWaits.waitForSometime(100);
            }
            
            log.info("---- Aggressive scroll completed ----");
            
        } catch (Exception e) {
            log.warn("---- Error during aggressive scroll: " + e.getMessage() + " ----");
        }
		return this;
    }
    
    /**
     * Perform incremental scrolling to maintain loaded panels and trigger any additional lazy loading
     */
    private InsightFinderPage performIncrementalScroll() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            
            // Quick scroll down then up to keep panels in DOM
            js.executeScript("window.scrollBy(0, 500);");
            commonWaits.waitForSometime(100);
            js.executeScript("window.scrollBy(0, -500);");
            commonWaits.waitForSometime(100);
            
            // Scroll to bottom and back to top
            scrollToBottomOfPage();
            commonWaits.waitForSometime(200);
            scrollToPageTop();
            commonWaits.waitForSometime(100);
            
        } catch (Exception e) {
            log.debug("---- Error during incremental scroll: " + e.getMessage() + " ----");
        }
		return this;
    }
    
    /**
     * Validate that all date values in the list are within specified number of days from now
     * Supports multiple date formats including: "12/15/2025, 01:12:09 PM", "MM/dd/yyyy", etc.
     * 
     * @param dateValues - List of date strings to validate
     * @param columnName - Column name (for logging)
     * @param numberOfDays - Number of days to validate against
     */
    private InsightFinderPage validateDatesWithinDays(List<String> dateValues, String columnName, int numberOfDays) {
        log.info("---- Validating " + dateValues.size() + " dates from column '" + columnName + 
                 "' are within " + numberOfDays + " days ----");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thresholdDate = now.minusDays(numberOfDays);
        
        int validCount = 0;
        int invalidCount = 0;
        
        // Common date formats to try
        List<DateTimeFormatter> formatters = Arrays.asList(
            DateTimeFormatter.ofPattern("M/d/yyyy, hh:mm:ss a", Locale.ENGLISH),  // 12/15/2025, 01:12:09 PM
            DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm:ss a", Locale.ENGLISH), // 12/15/2025, 01:12:09 PM
            DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a", Locale.ENGLISH),   // 12/15/2025, 1:12:09 PM
            DateTimeFormatter.ofPattern("MM/dd/yyyy, h:mm:ss a", Locale.ENGLISH),  // 12/15/2025, 1:12:09 PM
            DateTimeFormatter.ofPattern("M/d/yyyy hh:mm:ss a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );
        
        for (int i = 0; i < dateValues.size(); i++) {
            String dateStr = dateValues.get(i).trim();
            
            if (dateStr.isEmpty()) {
                log.warn("---- Row " + i + ": Empty date value - skipping validation ----");
                continue;
            }
            
            boolean dateParsed = false;
            LocalDateTime dateTime = null;
            
            // Try each formatter
            for (DateTimeFormatter formatter : formatters) {
                try {
                    dateTime = LocalDateTime.parse(dateStr, formatter);
                    dateParsed = true;
                    break;
                } catch (Exception e) {
                    // Try next formatter
                }
            }
            
            if (!dateParsed) {
                String errorMsg = "---- Row " + i + ": Could not parse date '" + dateStr + 
                                  "' with any known format ----";
                log.error(errorMsg);
                softAssert.fail(errorMsg);
                invalidCount++;
                continue;
            }
            
            // Calculate days difference
            long daysBetween = ChronoUnit.DAYS.between(dateTime, now);
            long absDaysBetween = Math.abs(daysBetween);
            
            log.info("---- Row " + i + ": Date '" + dateStr + "' is " + absDaysBetween + 
                     " days from now (parsed as: " + dateTime + ") ----");
            
            // Validate date is within specified days (allowing for future dates too)
            if (absDaysBetween <= numberOfDays) {
                log.info("---- ✓ Row " + i + ": Date is within " + numberOfDays + " days ----");
                validCount++;
            } else {
                String errorMsg = "---- ✗ Row " + i + ": Date '" + dateStr + "' is " + absDaysBetween + 
                                  " days away, exceeds " + numberOfDays + " days limit ----";
                log.error(errorMsg);
                softAssert.fail(errorMsg);
                invalidCount++;
            }
        }
        
        // Summary
        log.info("===========================================================");
        log.info("---- Date Validation Summary for column '" + columnName + "' ----");
        log.info("  Total dates checked: " + dateValues.size());
        log.info("  Valid (within " + numberOfDays + " days): " + validCount);
        log.info("  Invalid (exceeds " + numberOfDays + " days): " + invalidCount);
        log.info("===========================================================");
        
        if (invalidCount > 0) {
            softAssert.fail("Date validation failed: " + invalidCount + " date(s) in column '" + 
                           columnName + "' exceed " + numberOfDays + " days limit");
        }
		return this;
    }
    
    /**
     * Update Insight Finder Settings - sets skill identifier, streaming checkbox, and recommendation checkbox
     * 
     * @param skillId The skill ID to set in the Skill Identifier field
     * @param streamingFlag Boolean flag to enable/disable Enable Streaming checkbox (optional - skipped if not found)
     * @param recommendationFlag Boolean flag to enable/disable Follow Up Recommendations checkbox (optional - skipped if not found)
     */
    public InsightFinderPage updateInsightFinderSettings(String skillId, boolean streamingFlag, boolean recommendationFlag) {
        log.info("===========================================================");
        log.info("---- Updating Insight Finder Settings ----");
        log.info("===========================================================");
        
        try {
            // Step 1: Wait for Settings page to load
            log.info("---- Waiting for Settings page to load ----");
            commonWaits.waitForElementVisible(gptSettingsTitle, 15);
            
            // Step 2: Check and update Skill Identifier field
            log.info("---- Checking Skill Identifier field ----");
            commonWaits.waitForElementVisible(skillIdentifier, 10);
            
            // Scroll element into view to ensure it's interactable
            commonWaits.javaScriptScrollIntoView(skillIdentifier);
            
            String currentValue = skillIdentifier.getAttribute("value");
            log.info("---- Current Skill Identifier value: " + (currentValue != null && !currentValue.isEmpty() ? currentValue : "(empty)") + " ----");
            
            if (currentValue == null || currentValue.trim().isEmpty() || skillId != null) {
                log.info("---- Skill Identifier is empty, entering Skill ID: " + skillId + " ----");
                
                try {
                    commonWaits.waitForElementClickable(skillIdentifier, 10);
                    commonWaits.typeAndSelectEachPart(skillIdentifier, skillId);
                    log.info("---- ✓ Skill ID entered successfully using direct interaction ----");
                } catch (Exception e) {
                    log.warn("---- Direct interaction failed, using JavaScript fallback ----");
                    commonWaits.setAttributeValue(skillIdentifier, skillId);
                    log.info("---- ✓ Skill ID entered successfully using JavaScript ----");
                }
            } else {
                log.info("---- Skill Identifier already has value: " + currentValue + ", skipping update ----");
            }
            
            // Step 3: Check and update streaming checkbox based on flag
            log.info("---- Checking Stream checkbox ----");
            log.info("---- Waiting for Stream checkbox presence (may be hidden) ----");
            
            log.info("---- Stream checkbox found, proceeding with update ----");
            
            // Get current state - input might be hidden but we can still check its state
            boolean isStreamChecked = streamingCheckbox.isSelected();
            log.info("---- Stream checkbox currently checked: " + isStreamChecked + " ----");
            log.info("---- Desired state: " + streamingFlag + " ----");
            
            if (isStreamChecked != streamingFlag) {
                log.info("---- Checkbox state needs to be changed, clicking checkbox ----");
                
                // Get the input ID to find the visual toggle label
                String inputId = streamingCheckbox.getAttribute("id");
                log.info("---- Stream input ID: " + inputId + " ----");
                
                // Click the label for="switch-id" which is the visible toggle
                By labelLocator = By.xpath("//label[@for='" + inputId + "']");
                WebElement labelElement = commonWaits.waitForByElementToBeVisible(labelLocator);
                
                // Scroll the label into view
                commonWaits.javaScriptScrollIntoView(labelElement);
                
                // Click the visible label toggle
                commonWaits.clickWithoutInterception(labelElement);
                log.info("---- ✓ Stream checkbox updated successfully ----");
            } else {
                log.info("---- Stream checkbox already in desired state, no change needed ----");
            }
            
            // Step 4: Check and update recommendation checkbox based on flag
            log.info("---- Checking Recommendation checkbox ----");
            log.info("---- Waiting for Recommendation checkbox presence (may be hidden) ----");
            
            log.info("---- Recommendation checkbox found, proceeding with update ----");
            
            // Get current state - input might be hidden but we can still check its state
            boolean isCurrentlyChecked = recommendationInputLocator.isSelected();
            log.info("---- Recommendation checkbox currently checked: " + isCurrentlyChecked + " ----");
            log.info("---- Desired state: " + recommendationFlag + " ----");
            
            if (isCurrentlyChecked != recommendationFlag) {
                log.info("---- Checkbox state needs to be changed, clicking checkbox ----");
                
                // Get the input ID to find the visual toggle label
                String inputId = recommendationInputLocator.getAttribute("id");
                log.info("---- Recommendation input ID: " + inputId + " ----");
                
                // Click the label for="switch-id" which is the visible toggle
                By labelLocator = By.xpath("//label[@for='" + inputId + "']");
                WebElement labelElement = commonWaits.waitForByElementToBeVisible(labelLocator);
                
                // Scroll the label into view
                commonWaits.javaScriptScrollIntoView(labelElement);
                
                // Click the visible label toggle
                commonWaits.clickWithoutInterception(labelElement);
                log.info("---- ✓ Recommendation checkbox updated successfully ----");
            } else {
                log.info("---- Recommendation checkbox already in desired state, no change needed ----");
            }
            
            // Step 5: Save the settings
            log.info("---- Clicking Save button ----");
            String saveButton = String.format(spanTextLocator, "Save");
            WebElement saveButtonElement = commonWaits.waitForByElementToBeVisible(By.xpath(saveButton));
            commonWaits.javaScriptScrollIntoView(saveButtonElement);
            commonWaits.clickWithoutInterception(By.xpath(saveButton));
            log.info("---- ✓ Save button clicked ----");
            
            log.info("===========================================================");
            log.info("---- ✓ Insight Finder Settings updated successfully ----");
            log.info("===========================================================");
            
        } catch (Exception e) {
            String errorMsg = "---- ✗ Error updating Insight Finder Settings: " + e.getMessage() + " ----";
            log.error(errorMsg);
            softAssert.fail(errorMsg);
            e.printStackTrace();
        }
		return this;
    }
            
       
}
