package pages.ITSM.dwp;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DWPCommonOperations extends BasePage {

    public final SolqaLogger log = new SolqaLogger(DWPCommonOperations.class.getName());
    public final CommonWaits commonWaits;

    public DWPCommonOperations() {
        PageFactory.initElements(getDriver(), this);
        this.commonWaits = new CommonWaits();
    }

    // String Locators
    String TEXT_AREA_FIELD_LOCATOR = "//dwp-question-control[contains(. ,'%s')]//textarea";
    String DROPDOWN_FIELD_LOCATOR = "//dwp-question-control[contains(. ,'%s')]//button";
    String DROPDOWN_OPTION_LOCATOR = "//button[normalize-space(.)='%s' and @role='option']";
    String ATTACHMENT_FIELD_LOCATOR = "//dwp-question-control[contains(. ,'%s')]//input[@type='file']";
    String ATTACHMENTS_IN_NEW_COMMENT = "dwp-comments adapt-rx-uploader adapt-uploader-item";
    String ATTACHMENT_IN_QUESTION_LOCATOR = "//*[normalize-space()='%s']/following-sibling::*//dwp-attachment-answer//a";
    String ATTACHMENT_IN_COMMENT_LOCATOR = "//*[contains(@class, 'comments__attachment')]//a[@download='%s']";
    String TEXT_COLOR_LOCATOR = "//*[contains(@style, 'background:%s;') and @data-cmd='applytextColor']";
    String BACKGROUND_COLOR_LOCATOR = "//*[contains(@style, 'background:%s;') and @data-cmd='applybackgroundColor']";
    String counterFieldLocator = "//*[contains(. ,'%s')]/following-sibling::adapt-rx-counter//input";
    String toggleOptionLocator = "//*[contains(normalize-space(.), '%s')]//adapt-rx-switch//input";
    String feedbackResponseRadioBtn = "//adapt-rx-radiobutton[contains(. ,'%s')]//label";
    String LATEST_COMMENT = "//dwp-comment-viewer//*[contains(text(),'%s')]";
    String similarArticleLink = "//dwp-knowledge-article-side-item[contains(. ,'Similar Articles')]//a[normalize-space()='%s']";
    String commentBlock = ".comments__comment-block";
    String radioBtnOptionLocator = "//dwp-question-control[contains(normalize-space(.), '%s')]//adapt-rx-radiobutton[contains(normalize-space(.), '%s')]//input";
    String checkboxOptionLocator = "//dwp-question-control[contains(normalize-space(.), '%s')]//adapt-rx-checkbox[contains(normalize-space(.), '%s')]//input";
    String dwpSpinnerLocator = "//*[contains(@class, 'loader-container')]";
    String inputFieldByLabel = "//label[normalize-space(.)='%s']/ancestor::dwp-question-control//input";
    String radioFieldOptionFromTable = "//dwp-table-question-presenter[contains(normalize-space(.), '%s')]//input[@type='radio' and @value='%s']/parent::*";
    String checkboxFieldOptionFromTable = "//dwp-table-question-presenter[contains(normalize-space(.), '%s')]//input[@type='checkbox' and contains(@aria-label, '%s')]/ancestor::adapt-row-selection-checkbox";

    // Scripts and Constants
    public static final String SCRIPT_MAKE_ELEMENT_VISIBLE = "arguments[0].setAttribute('style', 'display:inline !important; visibility:visible !important');";
    public static final String ATTACHMENT_IN_QUESTION = "Question";
    public static final String ATTACHMENT_IN_COMMENT = "Comment";

    @FindBy(xpath = "//button[@data-ta='addCommentButton']")
    public WebElement addCommentButton;
    @FindBy(css = "adapt-froala-editor .fr-wrapper .fr-view p")
    public WebElement rtfCommentBox;
    @FindBy(xpath = "//adapt-froala-editor[contains(@class, 'adapt-rich-editor')]")
    public WebElement richTextBoxEditor;
    @FindBy(css = "adapt-rx-uploader input")
    public WebElement addAttachmentInput;
    @FindBy(css = "button[data-cmd='bold']")
    public WebElement boldButton;
    @FindBy(css = "button[data-cmd='italic']")
    public WebElement italicButton;
    @FindBy(css = "button[data-cmd='underline']")
    public WebElement underlineButton;
    @FindBy(css = "button[data-cmd='textColor']")
    public WebElement textColorButton;
    @FindBy(css = "button[data-cmd='backgroundColor']")
    public WebElement backgroundColorButton;
    @FindBy(css = "button.fr-command[data-cmd='insertLink']")
    public WebElement insertLinkButton;
    @FindBy(xpath = "//*[text()='URL']/preceding-sibling::input")
    public WebElement linkUrlInput;
    @FindBy(css = "button.fr-command[data-cmd='linkInsert']")
    public WebElement insertLinkOkButton;
    @FindBy(xpath = "//button[normalize-space()='Insert Image']")
    public WebElement insertImageButton;
    @FindBy(xpath = "//input[contains(@aria-labelledby, 'image-upload-layer') and @type='file']")
    public WebElement insertImageFileInput;
    @FindBy(xpath = "//button[normalize-space(.)='Request now']")
    public WebElement requestNowButton;
    @FindBy(xpath = "//button[@data-ta='commentFormSubmitButton']")
    public WebElement submitCommentButton;
    @FindBy(xpath = "//button[normalize-space(.)='Next']")
    public WebElement nextButton;
    @FindBy(xpath = "//dwp-knowledge-article-side-item[contains(. ,'Similar Article')]")
    public WebElement similarArticlesSection;
    @FindBy(xpath = "//dwp-feedback-button[contains(. ,'Feedback')]//*[contains(@class, 'btn-link')] ")
    public WebElement feedbackButton;
    @FindBy(xpath = "//textarea[@data-ta='feedbackTextArea'] | //span[contains(. ,'Tell us more')]/parent::div/following-sibling::textarea")
    public WebElement feedbackTextArea;
    @FindBy(xpath = "//button[@data-ta='submitFeedbackButton'] | button[normalize-space()='Submit']")
    public WebElement submitFeedbackButton;
    @FindBy(xpath = "//*[contains(@class, 'a-toast--success') and contains(. ,'Feedback submitted')]")
    public WebElement feedbackSuccessToast;
    @FindBy(xpath = "//adapt-rx-search[contains(normalize-space(.), 'Filter')]//input")
    public WebElement typeheadSearchInputInDropdown;
    @FindBy(xpath = "//input[contains(@class, 'adapt-rx-search__input') and @placeholder='Filter']")
    WebElement comboBoxSearchInput;
    @FindBy(xpath = "//*[@data-ta='typeaheadSearchInputWrapper']")
    WebElement DWP_HOMEPAGE;

    /**
     * Fills a text area field with the specified value
     *
     * @param label Field label to identify the text area
     * @param value Text value to enter
     * @return Current instance for method chaining
     */
    public DWPCommonOperations fillTextAreaField(String label, String value) {
        try {
            log.info("Starting to fill text area field with label: '" + label + "'");
            WebElement textArea = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TEXT_AREA_FIELD_LOCATOR, label)));
            textArea.clear();
            textArea.sendKeys(value);
            log.info("Successfully filled text area field with label: '" + label + "' with value: '" + value + "'");
        } catch (Exception e) {
            log.error("Failed to fill text area field with label: '" + label + "'. Error: " + e.getMessage());
            throw new RuntimeException("Error filling text area field: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Selects a value from a dropdown field
     *
     * @param label Field label to identify the dropdown
     * @param value Option value to select
     * @return Current instance for method chaining
     */
    public DWPCommonOperations fillDropdownField(String label, String value) {
        try {
            log.info("Starting to select dropdown option - Label: '" + label + "', Value: '" + value + "'");
            WebElement dropDown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_FIELD_LOCATOR, label)));
            dropDown.click();
            log.info("Clicked on dropdown with label: '" + label + "'");

            WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_OPTION_LOCATOR, value)));
            option.click();
            log.info("Successfully selected dropdown option: '" + value + "' for field: '" + label + "'");
        } catch (Exception e) {
            log.error("Failed to select dropdown option - Label: '" + label + "', Value: '" + value + "'. Error: " + e.getMessage());
            throw new RuntimeException("Error selecting dropdown option: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Uploads a file to an attachment field
     *
     * @param label    Field label to identify the attachment input
     * @param filePath Absolute path to the file
     * @return Current instance for method chaining
     */
    public DWPCommonOperations fillAttachmentField(String label, String filePath) {
        try {
            log.info("Starting to upload file to attachment field with label: '" + label + "'");
            WebElement attachmentField = commonWaits.waitForElementPresenceLocated(By.xpath(String.format(ATTACHMENT_FIELD_LOCATOR, label)));
            attachmentField.sendKeys(filePath);
            log.info("Successfully uploaded file: '" + filePath + "' to attachment field: '" + label + "'");
        } catch (Exception e) {
            log.error("Failed to upload file to attachment field with label: '" + label + "'. File path: '" + filePath + "'. Error: " + e.getMessage());
            throw new RuntimeException("Error uploading file to attachment field: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Verifies that the DWP home page is loaded and visible
     *
     * @return Current instance for method chaining
     */
    public DWPCommonOperations verifyDWPHomePageOpened() {
        try {
            log.info("Starting verification: DWP home page should be opened");
            waitForAllDWPSpinnersToHide();
            log.info("All spinners have been hidden, checking for DWP home page elements");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), DWP_HOMEPAGE, 20), "DWP HomePage element is not visible");
            log.info("Successfully verified: DWP home page is displayed and accessible");
        } catch (Exception e) {
            log.error("Failed to verify DWP home page. Error: " + e.getMessage());
            throw new RuntimeException("DWP home page verification failed: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Clicks on the 'Request now' button to submit a request
     *
     * @return Current instance for method chaining
     */
    public DWPCommonOperations clickOnRequestNow() {
        try {
            log.info("Clicking on 'Request now' button");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            commonWaits.waitForPageFullyLoaded(7, 1);
            commonWaits.clickWithoutInterception(requestNowButton);
            log.info("Successfully clicked on 'Request now' button");
        } catch (Exception e) {
            log.error("Failed to click on 'Request now' button. Error: " + e.getMessage());
            throw new RuntimeException("Error clicking on 'Request now' button: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Waits for all spinner elements to hide on the DWP page
     *
     * @return Current instance for method chaining
     */
    public DWPCommonOperations waitForDWPSpinnersToHide() {
        try {
            log.info("Waiting for all spinners to hide on DWP application");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("All spinners have been hidden successfully");
            commonWaits.waitForExecutionComplete();
            commonWaits.waitForPageReadyState(getDriver());
        } catch (Exception e) {
            log.error("Failed while waiting for spinners to hide. Error: " + e.getMessage());
            throw new RuntimeException("Error waiting for spinners to hide: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Creates an image file with the specified name
     *
     * @param fileName Name/path of the file to create
     * @return File object representing the created file
     * @throws IOException If file creation fails
     */
    public File createImageFile(String fileName) throws IOException {
        File imageFile = new File(fileName);
        if (imageFile.createNewFile()) {
            log.info("Image file created successfully: " + fileName);
        } else {
            log.warn("Image file was not created (it may already exist): " + fileName);
        }
        return imageFile;
    }

    /**
     * Adds a rich text format comment with attachment to a submitted request
     *
     * @param Comment    Comment text to add
     * @param file       File to attach to the comment
     * @param rtfOptions Map containing RTF formatting options (Bold, Italic, Underline, TextColor, BackgroundColor, Link, ImagePath)
     * @return Current instance for method chaining
     */
    public DWPCommonOperations addRTFCommentWithAttachmentInSubmittedRequest(String Comment, File file, Map<Object, String> rtfOptions) {
        clickOnAddComment();
        waitForDWPSpinnersToHide();
        verifyRichTextBox();
        addRichTextComment(Comment, rtfOptions);
        attachFileInComment(file);
        commonWaits.waitForElementVisible(submitCommentButton);
        commonWaits.waitForElementClickable(submitCommentButton).click();
        return this;
    }

    /**
     * Clicks on the 'Add Comment' button
     *
     * @return Current instance for method chaining
     */
    public DWPCommonOperations clickOnAddComment() {
        waitForDWPSpinnersToHide();
        commonWaits.waitForElementVisible(addCommentButton).click();
        return this;
    }

    /**
     * Verifies that the rich text box is visible and ready
     *
     * @return Current instance for method chaining
     */
    public DWPCommonOperations verifyRichTextBox() {
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForPageReadyState(getDriver());
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), richTextBoxEditor, 20), "Rich Text Box is not visible");
        log.info("Rich Text Box is visible");
        return this;
    }

    /**
     * Adds a rich text comment with formatting options
     *
     * @param Comment Comment text to add
     * @param options Map containing formatting options (Bold, Italic, Underline, TextColor, BackgroundColor, Link, ImagePath)
     * @return Current instance for method chaining
     */
    public DWPCommonOperations addRichTextComment(String Comment, Map<Object, String> options) {
        try {
            log.info("Adding rich text comment with options: " + options);

            rtfCommentBox.sendKeys(Comment);
            // Select all entered text
            rtfCommentBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));

            // Apply formatting as per options
            if (options.containsKey("Bold")) {
                commonWaits.waitForElementVisible(boldButton).click();
            }
            if (options.containsKey("Italic")) {
                commonWaits.waitForElementVisible(italicButton).click();
            }
            if (options.containsKey("Underline")) {
                commonWaits.waitForElementVisible(underlineButton).click();
            }
            if (options.containsKey("TextColor")) {
                commonWaits.waitForElementVisible(textColorButton).click();
                commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TEXT_COLOR_LOCATOR, options.get("TextColor")))).click();
            }
            if (options.containsKey("BackgroundColor")) {
                commonWaits.waitForElementVisible(backgroundColorButton).click();
                commonWaits.waitForByElementToBeVisible(By.xpath(String.format(BACKGROUND_COLOR_LOCATOR, options.get("BackgroundColor")))).click();
            }
            if (options.containsKey("Link")) {
                commonWaits.waitForElementVisible(insertLinkButton).click();
                commonWaits.waitForElementVisible(linkUrlInput).sendKeys(options.get("Link"));
                commonWaits.waitForElementVisible(insertLinkOkButton).click();
            }
            if (options.containsKey("ImagePath")) {
                rtfCommentBox.sendKeys(Keys.END); // Move cursor to end
                rtfCommentBox.sendKeys(Keys.ENTER);

                commonWaits.waitForElementVisible(insertImageButton).click();
                commonWaits.waitForElementVisible(insertImageFileInput).sendKeys(options.get("ImagePath")); // Absolute path to image file
            }

            log.info("Rich text comment added successfully.");
        } catch (Exception e) {
            log.error("Failed to add rich text comment. Error: " + e.getMessage());
            throw new RuntimeException("Error adding rich text comment: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Attaches a file to a comment
     *
     * @param file File object to attach
     * @return Current instance for method chaining
     */
    public DWPCommonOperations attachFileInComment(File file) {

        // Attach file
        if (file != null) {
            log.info("Attaching file: '" + file.getAbsolutePath() + "'");
            this.executeJS(SCRIPT_MAKE_ELEMENT_VISIBLE, this.addAttachmentInput);
            this.addAttachmentInput.sendKeys(file.getAbsolutePath());
        } else {
            log.warn("Cannot attach file=null");
            return this;
        }
        return this;
    }

    /**
     * Verifies attachment is present and downloads it to specified directory
     *
     * @param expectedFileName      Name of the file to download
     * @param attachmentIn          Location of attachment ("Question" or "Comment")
     * @param labelOrAttachmentName Label text for question or attachment name for comment
     * @param downloadDir           Directory path where file will be downloaded
     * @return Current instance for method chaining
     */
    public DWPCommonOperations verifyAndDownloadAttachment(String expectedFileName, String attachmentIn, String labelOrAttachmentName, String downloadDir) {
        try {
            log.info("Starting attachment verification and download - File: '" + expectedFileName + "', Location: '" + attachmentIn + "'");

            // 1. Build locator based on attachment location
            String attachmentLinkLocator = buildAttachmentLocator(attachmentIn, labelOrAttachmentName);

            // 2. Verify attachment is present
            WebElement attachmentLink = commonWaits.waitForByElementToBeVisible(By.xpath(attachmentLinkLocator));
            String actualFileName = attachmentLink.getText();
            Assert.assertTrue(actualFileName.contains(expectedFileName),
                    "Attachment with name '" + expectedFileName + "' not found. Actual: '" + actualFileName + "'");
            log.info("Verified attachment is present: " + expectedFileName);

            File downloadedFile = new File(downloadDir, expectedFileName);

            // Delete file if it already exists to ensure we're checking a fresh download
            if (downloadedFile.exists()) {
                log.info("Deleting existing file before download: " + downloadedFile.getAbsolutePath());
                if (!downloadedFile.delete()) {
                    log.warn("Failed to delete existing file: " + downloadedFile.getAbsolutePath());
                }
            }

            // 3. Click to download
            commonWaits.javaScriptScrollIntoView(attachmentLink);
            commonWaits.clickWithoutInterception(attachmentLink);
            log.info("Clicked on attachment link to initiate download");
            commonWaits.waitForSometime(commonWaits.moreDelay);

            // 4. Verify file is downloaded
            Assert.assertTrue(downloadedFile.exists(), "Downloaded file not found: " + downloadedFile.getAbsolutePath());
            log.info("Successfully verified and downloaded attachment: " + expectedFileName);
        } catch (Exception e) {
            log.error("Failed to verify/download attachment: '" + expectedFileName + "'. Error: " + e.getMessage());
            throw new RuntimeException("Error verifying/downloading attachment: " + e.getMessage(), e);
        }
        return this;
    }

    public String buildAttachmentLocator(String attachmentIn, String labelOrAttachmentName) {
        switch (attachmentIn) {
            case ATTACHMENT_IN_QUESTION:
                return String.format(ATTACHMENT_IN_QUESTION_LOCATOR, labelOrAttachmentName);
            case ATTACHMENT_IN_COMMENT:
                return String.format(ATTACHMENT_IN_COMMENT_LOCATOR, labelOrAttachmentName);
            default:
                throw new IllegalArgumentException("Invalid attachmentIn value: '" + attachmentIn + "'. Expected 'Question' or 'Comment'");
        }
    }

    public void waitForAllDWPSpinnersToHide() {

        try {
            By spinnerLocator = By.xpath(dwpSpinnerLocator);

            // ⚡ PERFORMANCE FIX: Use direct driver call to avoid healing overhead
            // Loaders are transient elements - if not present, page is already loaded!
            List<WebElement> spinners = getDriver().findElements(spinnerLocator);

            if (spinners.isEmpty()) {
                log.info("✅ No " + "DWP" + " loaders found - page already loaded");
                return;
            }

            log.info("⏳ Found " + spinners.size() + " " + "DWP" + " loader(s), waiting for invisibility...");

            // Wait for all found spinners to disappear with shorter timeout
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

            for (WebElement spinner : spinners) {
                try {
                    wait.until(ExpectedConditions.invisibilityOf(spinner));
                } catch (TimeoutException e) {
                    log.warn("⚠️ Spinner did not disappear within 10s, continuing anyway");
                }
            }

            log.info("✅ All " + "DWP" + " loaders disappeared");

        } catch (Exception e) {
            log.info("Spinner wait completed with exception: " + e.getMessage());
        }
    }

    public DWPCommonOperations clickOnNextButton() {
        try {
            log.info("Clicking on 'Next' button");
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), nextButton, 10), "'Next' button is not visible");
            commonWaits.clickWithoutInterception(nextButton);
            log.info("Successfully clicked on 'Next' button");
        } catch (Exception e) {
            log.error("Failed to click on 'Next' button. Error: " + e.getMessage());
            throw new RuntimeException("Error clicking on 'Next' button: " + e.getMessage(), e);
        }
        return this;
    }

    public DWPCommonOperations fillCounterField(String label, String value) {
        try {
            log.info("Starting to fill counter field with label: '" + label + "'");
            WebElement counterField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(counterFieldLocator, label)));
            counterField.clear();
            counterField.sendKeys(value);
            log.info("Successfully filled counter field with label: '" + label + "' with value: '" + value + "'");
        } catch (Exception e) {
            log.error("Failed to fill counter field with label: '" + label + "'. Error: " + e.getMessage());
            throw new RuntimeException("Error filling counter field: " + e.getMessage(), e);
        }
        return this;
    }

    public DWPCommonOperations setToggleOption(String questionLabel, boolean toggleValue) {
        String isChecked = commonWaits.findElement(By.xpath(String.format(toggleOptionLocator, questionLabel))).getAttribute("aria-checked");
        if (isChecked == null) {
            throw new RuntimeException("Could not determine the state of the toggle for question: '" + questionLabel + "'");
        }
        if ((toggleValue && isChecked.equals("false")) || (!toggleValue && isChecked.equals("true"))) {
            log.info("Setting toggle for question '" + questionLabel + "' to " + (toggleValue ? "enabled" : "disabled"));
            commonWaits.clickWithoutInterception(By.xpath(String.format(toggleOptionLocator, questionLabel)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        } else {
            log.info("Toggle for question '" + questionLabel + "' is already set to " + (toggleValue ? "enabled" : "disabled"));
        }
        return this;
    }

    public DWPCommonOperations selectRadioOption(String questionLabel, String optionText) {
        try {
            log.info("Selecting radio option '" + optionText + "' for question '" + questionLabel + "'");
            commonWaits.clickWithoutInterception(By.xpath(String.format(radioBtnOptionLocator, questionLabel, optionText)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to select radio option '" + optionText + "' for question '" + questionLabel + "': " + e.getMessage(), e);
        }
    }

    public DWPCommonOperations selectCheckboxOption(String questionLabel, String optionText) {
        String[] valueArray = optionText.split(",");
        for (String value : valueArray) {
            String trimmedValue = value.trim();
            try {
                commonWaits.clickWithoutInterception(By.xpath(String.format(checkboxOptionLocator, questionLabel, trimmedValue)));
            } catch (Exception e) {
                log.error("Dropdown option '" + trimmedValue + "' not found for question '" + questionLabel + "': " + e.getMessage());
            }
        }
        return this;
    }

    public DWPCommonOperations verifyLatestComment(String expectedText) {
        log.info("Verifying the latest (first) comment.");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> comments = findElements(By.cssSelector(commentBlock));
        Assert.assertFalse(comments.isEmpty(), "No comments found.");
        String latestCommentText = comments.get(0).getText().trim();
        Assert.assertTrue(latestCommentText.contains(expectedText), "Latest comment does not match expected text.");
        return this;
    }

    public DWPCommonOperations verifySimilarArticleIsAvailable(String similarArticleTitle) {
        log.info("Verifying Similar Articles section is displayed.");
        Assert.assertTrue(commonWaits.waitForVisibleElement(similarArticlesSection), "Similar Articles section is not displayed.");
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(similarArticleLink, similarArticleTitle))), "Similar Article with title '" + similarArticleTitle + "' is not displayed.");
        return this;
    }

    public DWPCommonOperations giveFeedbackOnArticle(String feedbackType, String feedbackText) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), feedbackButton, 10), "Feedback button is not visible.");
        commonWaits.clickWithoutInterception(feedbackButton);
        commonWaits.clickWithoutInterception(By.xpath(String.format(feedbackResponseRadioBtn, feedbackType)));
        if (feedbackText != null && !feedbackText.isEmpty()) {
            commonWaits.clickWithoutInterception(feedbackTextArea);
            feedbackTextArea.sendKeys(feedbackText);
        }
        commonWaits.clickWithoutInterception(submitFeedbackButton);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), feedbackSuccessToast, 10), "Feedback success toast is not displayed.");
        return this;
    }

    public DWPCommonOperations verifyCommentNotPresent(String text) {
        log.info("Verifying comment is NOT present: " + text);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();
        Assert.assertTrue(findElements(By.xpath(String.format(LATEST_COMMENT, text))).isEmpty(),
                "Comment should NOT be present, but was found: " + text);
        return this;
    }

    public DWPCommonOperations verifyAttachmentNotPresent(File file) {
        String attachmentName = file.getName();
        log.info("Verifying attachment is NOT present: " + attachmentName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();

        boolean attachmentFound = findElements(By.cssSelector(commentBlock))
                .stream().anyMatch(we -> {
                    List<WebElement> linksInComment = we.findElements(By.tagName("a"));
                    return linksInComment.stream().anyMatch(l -> l.getAttribute("download") != null
                            && l.getAttribute("download").equals(attachmentName));
                });
        Assert.assertFalse(attachmentFound, "Attachment should NOT be present, but was found: " + attachmentName);
        return this;
    }

    public DWPCommonOperations fillTypeAheadDropdownField(String label, String value) {
        try {
            log.info("Selecting typeahead dropdown option - Label: '" + label + "', Value: '" + value + "'");
            commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_FIELD_LOCATOR, label)));
            commonWaits.clickWithoutInterception(typeheadSearchInputInDropdown);
            typeheadSearchInputInDropdown.sendKeys(value);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(DROPDOWN_OPTION_LOCATOR, value), 7),
                    "Typeahead dropdown option '" + value + "' is not visible");
            commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_OPTION_LOCATOR, value)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Error selecting typeahead dropdown option. Label: '" + label + "', Value: '" + value + "'. " + e.getMessage(), e);
        }
    }

    public DWPCommonOperations selectMultipleValuesInDropdownField(String questionLabel, String values) {
        try {
            WebElement dropDown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_FIELD_LOCATOR, questionLabel)));
            dropDown.click();
            String[] valueArray = values.split(",");
            for (String value : valueArray) {
                String trimmedValue = value.trim();
                try {
                    WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_OPTION_LOCATOR, trimmedValue)));
                    option.click();
                } catch (Exception e) {
                    log.error("Dropdown option '" + trimmedValue + "' not found for question '" + questionLabel + "': " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to select multiple values in dropdown for question '" + questionLabel + "': " + e.getMessage(), e);
        }
        return this;
    }

    public DWPCommonOperations fillTimeFieldByLabel(String label, String value) {
        try {
            WebElement input = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(inputFieldByLabel, label)));
            input.clear();
            input.sendKeys(value);
        } catch (Exception e) {
            log.error("Failed to fill time field for label '" + label + "': " + e.getMessage(), e);
        }
        return this;
    }

    public DWPCommonOperations selectRadioOptionFromTable(String questionLabel, String rowText) {
        try {
            WebElement radio = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(radioFieldOptionFromTable, questionLabel, rowText)));
            commonWaits.clickWithoutInterception(radio);
        } catch (Exception e) {
            log.error("Failed to select radio option from table for question '" + questionLabel + "', row '" + rowText + "': " + e.getMessage(), e);
        }
        return this;
    }

    public DWPCommonOperations selectCheckboxOptionFromTable(String questionLabel, String rowText) {
        try {
            WebElement we = commonWaits.waitForElementPresenceLocated(By.xpath(String.format(checkboxFieldOptionFromTable, questionLabel, rowText)));
            // Only click if not already selected
            if (!we.isSelected()) {
                clickByAction(we);
            } else {
                log.info("Checkbox already selected for question '" + questionLabel + "', row '" + rowText + "'");
            }
        } catch (Exception e) {
            log.error("Failed to select checkbox option from table for question '" + questionLabel + "', row '" + rowText + "': " + e.getMessage(), e);
        }
        return this;
    }

    public DWPCommonOperations fillComboBoxField(String label, String value) {
        try {
            log.info("Starting to select dropdown option - Label: '" + label + "', Value: '" + value + "'");
            WebElement dropDown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_FIELD_LOCATOR, label)));
            dropDown.click();
            log.info("Clicked on dropdown with label: '" + label + "'");

            commonWaits.clickWithoutInterception(comboBoxSearchInput);
            comboBoxSearchInput.sendKeys(value);

            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(DROPDOWN_OPTION_LOCATOR, value), 20),
                    "ComboBox dropdown option '" + value + "' is not visible");

            commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_OPTION_LOCATOR, value)));

            log.info("Successfully selected dropdown option: '" + value + "' for field: '" + label + "'");
        } catch (Exception e) {
            log.error("Failed to select dropdown option - Label: '" + label + "', Value: '" + value + "'. Error: " + e.getMessage());
            throw new RuntimeException("Error selecting dropdown option: " + e.getMessage(), e);
        }
        return this;
    }

    String typeHeadInput = "//dwp-question-control[contains(. ,'%s')]//input";
    public DWPCommonOperations fillTypeheadField(String label, String value) {
        try {
            log.info("Filling typehead field - Label: '" + label + "', Value: '" + value + "'");
            WebElement input = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeHeadInput, label)));
            input.sendKeys(value);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(DROPDOWN_OPTION_LOCATOR, value), 7),
                    "Typeahead dropdown option '" + value + "' is not visible after entering value in typehead field with label '" + label + "'");
            commonWaits.clickWithoutInterception(By.xpath(String.format(DROPDOWN_OPTION_LOCATOR, value)));
            log.info("Successfully filled typehead field: '" + label + "' with value: '" + value + "'");
        } catch (Exception e) {
            log.error("Failed to fill typehead field - Label: '" + label + "', Value: '" + value + "'. Error: " + e.getMessage(), e);
            throw new RuntimeException("Error filling typehead field: " + e.getMessage(), e);
        }
        return this;
    }

    String checkboxOptionByText = "//input[@type='checkbox']/ancestor::label[1][.//*[normalize-space()='%s']]//input[@type='checkbox']";
    String radioOptionByText = "//input[@type='radio']/ancestor::label[1][.//*[normalize-space()='%s']]//input[@type='radio']";

    public DWPCommonOperations selectCheckboxOptionByText(String optionText) {
        WebElement checkbox = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(checkboxOptionByText, optionText)));
        String clazz = checkbox.getAttribute(SolQAConstants.ATTRIBUTE_CLASS) == null ? "" : checkbox.getAttribute(SolQAConstants.ATTRIBUTE_CLASS);
        boolean isChecked = clazz.contains(SolQAConstants.NOT_NG_EMPTY_CLASS) || checkbox.isSelected();
        if (!isChecked) {
            commonWaits.clickWithoutInterception(checkbox);
            commonWaits.waitForExecutionComplete();
        } else {
            log.info("Checkbox with option text '" + optionText + "' is already selected.");
        }
        return this;
    }

    public DWPCommonOperations selectRadioOptionByText(String optionText) {
        WebElement radio = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(radioOptionByText, optionText)));
        String clazz = radio.getAttribute(SolQAConstants.ATTRIBUTE_CLASS) == null ? "" : radio.getAttribute(SolQAConstants.ATTRIBUTE_CLASS);
        boolean isSelected = clazz.contains(SolQAConstants.NOT_NG_EMPTY_CLASS) || radio.isSelected();
        if (!isSelected) {
            commonWaits.clickWithoutInterception(radio);
            commonWaits.waitForExecutionComplete();
        } else {
            log.info("Radio option with text '" + optionText + "' is already selected.");
        }
        return this;
    }
}