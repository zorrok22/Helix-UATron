package pages.ITSM.dwp;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ActivityPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    //String locators
    public String requestSearchedDetailsBtn = "//dwp-global-search-list-item[contains(normalize-space(.), '%s')]//a[contains(. ,'Details')]";
    public String LATEST_ACTIVE_EVENT_RELEASE_REQUEST_ID = "//span[contains(.,'%s')]//ancestor::dwp-card-view//p[@data-ta='cardText_secondary.0']//span[@data-ta='cardTextPart_0']/following-sibling::span";
    public String LATEST_PAST_EVENT_RELEASE_REQUEST_ID = "//*[contains(@data-ta,'eventContainerCardSmallListInactive')]//span[contains(.,'%s')]//ancestor::dwp-list-view//p[@data-ta='cardText_secondary.0']//span[@data-ta='cardTextPart_0']/following-sibling::span";
    public String ACTIVE_EVENT_CARD_BY_ID = "//dwp-card[contains(@data-ta, 'eventContainerCardColored') and contains(., '%s')]";
    public String ATTACHMENTS_IN_NEW_COMMENT = "dwp-comments adapt-rx-uploader adapt-uploader-item";
    public String commentBlock = ".comments__comment-block";
    public String LATEST_COMMENT = "//dwp-comment-viewer//*[contains(text(),'%s')]";
    public String COMMENT_BLOCK_CONTAINING_TEXT = "//dwp-comment-viewer//*[contains(text(),'%s')]/ancestor::div[contains(@class,'comment')][1]";
    public String COMMENT_AUTHOR_XPATH = ".//span[contains(@class,'comments__user-name text')]";
    public String scriptToMakeFileElementVisible = "arguments[0].setAttribute('style', 'display:inline !important; visibility:visible !important');";
    public String approvalStatus = "//*[normalize-space()='%s']/ancestor::dwp-card-view//*[contains(@data-ta,'cardNotification')]";
    public String openRequest = "//*[normalize-space()='%s']/ancestor::dwp-card-view//div[contains(@class,'card-view__content')]";
    public String rejectionReasonTextareaLocator = "//textarea";
    public String ANY_SEARCH_RESULTS_TITLE = "//*[@data-ta='titleSearchItem' and contains(text(), '%s')]";
    public String SEARCH_RESULT_PAGINATION_BUTTONS = "[data-ta='globalSearchPaginationResult'] button";
    public String SEARCH_RESULT_PAGINATION_CURRENT_PAGE = "[data-ta='globalSearchPaginationResult'] .page-item.active>.page-link";
    public String RKM_ARTICLE_RESULT_LOCATOR = "//*[contains(@class,'aside-block font-weight-light pad-btm-4')]";
    public String RKM_ARTICLE_VOTE_LOCATOR = "//*[contains(text(),'%s')]";
    public String FEEDBACK_MESSAGE = "//*[@data-ta = 'feedbackRating']//span[contains(.,'Thanks for your feedback.')]";
    public String articleNameLocator = "//a[contains(.,'%s')]";
    public String attachmentBlock = "//div[contains(@data-ta,'commentsRichTextComment')] | //*[contains(@data-ta,'commentsPlainTextComment')]";
    public String attachmentBlockApex = "//p[contains(@data-ta,'Comment')]";
    public String searchItemLink = " //dwp-global-search-item-title//a[contains(. ,'%s')]";
    public String globalSearchFilterTab = "//button[contains(. ,'%s') and @role='tab']";
    public String SEARCH_TITLE_DETAILS = "xpath=//div[@data-ta='globalSearchRow'][contains(.,'%s')]//p";
    public String passwordInput = "input[type='password']";
    public String usernameInput = "//input[@id= 'user_login']";
    public String detailsButtonByRequestID = "//dwp-card[contains(., '%s')]//a[contains(.,'Details')]";
    public String pastEventDetailsButton = "//*[contains(@data-ta,'eventContainerCardSmallListInactive')]//div[contains(.,'%s')]//ancestor::dwp-list-view//a[contains(.,'Details')]";
    public String requestTitleByName = "//dwp-card-view[contains(.,'%s')]";
    public String requestTitleDetailsLinkByName = "//dwp-card-view[contains(.,'%s')]//a[contains(text(),'Details') or contains(text(),'details')]";
    public String requestDetailsSummaryText = "//*[contains(text(),'%s')]";
    public String FULFILLMENT_ID_BY_PREFIX = "//*[contains(@data-ta,'fulfillmentDisplayId') and starts-with(normalize-space(.), '%s')]";
    public String RequestSearchItemLink = "//p[contains(.,'Request ID')]//span[@data-ta='attributeValue' and normalize-space()='%s']";
    public String ACTIVE_EVENT_CARD_BY_TITLE = "//dwp-card-view//span[contains(.,'%s')]/ancestor::dwp-card-view";
    public String XPATH_CKEDITOR_BODY = "//body";
    public String CSS_COMMENT_BLOCK = ".comments__comment-block";
    public String ACTIVE_EVENT_CONTAINERS = "//*[contains(@data-ta,'eventContainerCard') or contains(@data-ta,'eventContainerCardColored') or contains(@data-ta,'eventContainerCardSmallListActive')]";
    public String PAST_EVENT_CONTAINERS = "//*[contains(@data-ta,'eventContainerCardSmallListInactive') or contains(@data-ta,'eventContainerCardSmallListPast') or contains(@data-ta,'eventContainerCardPast')]";
    public String ANY_EVENT_CONTAINERS = "(" + ACTIVE_EVENT_CONTAINERS + " | " + PAST_EVENT_CONTAINERS + ")";
    public String ACTIVE_EVENT_ANY_CARD = ACTIVE_EVENT_CONTAINERS + "//*[self::dwp-card-view or self::dwp-list-view or self::dwp-card][1]";
    public String ACTIVE_EVENT_CARD_BY_TEXT_ANYWHERE = ACTIVE_EVENT_CONTAINERS + "//*[contains(normalize-space(.), '%s')]" + "/ancestor::*[self::dwp-card-view or self::dwp-list-view or self::dwp-card][1]";
    public String ANY_EVENT_CARD_BY_TEXT_ANYWHERE = ANY_EVENT_CONTAINERS + "//*[contains(normalize-space(.), '%s')]" + "/ancestor::*[self::dwp-card-view or self::dwp-list-view or self::dwp-card][1]";
    public String PAST_EVENT_CONTAINER_INACTIVE_XPATH = "//*[contains(@data-ta,'eventContainerCardSmallListInactive')]";
    String fulfillmentIds = ".//*[contains(@data-ta,'fulfillmentDisplayId')]";
    String dropdownActionsOptions = "//button[contains(@role,'menuitem') and contains(normalize-space(.),'%s')]";
    String headers = ".//*[self::button or self::a or self::div][contains(normalize-space(.), '%s')]";
    String dependentServiceHeadline = "//*[contains(@class,'dependent') or contains(@data-ta,'dependent') or contains(@class,'accordion') or contains(@data-ta,'accordion')][.//*[contains(normalize-space(.), '%s')]]";
    String collapseDependant = "//*[contains(@class,'dependent') or contains(@data-ta,'dependent') or contains(@class,'accordion') or contains(@data-ta,'accordion')][.//*[contains(normalize-space(.), '%s')]]";
    String headlineValueOfDependantService = ".//dt[normalize-space()='%s']/following-sibling::dd[1]";
    String expandDependant = "//*[contains(@class,'dependent') or contains(@data-ta,'dependent') or contains(@class,'accordion') or contains(@data-ta,'accordion')][.//*[contains(normalize-space(.), '%s')]]";
    String actingAsUserXpath = "//*[contains(normalize-space(.), 'Acting') and contains(normalize-space(.), 'as') and contains(normalize-space(.), '%s')]";
    String statusAndApprover = "//*[contains(normalize-space(.), '%s') and contains(normalize-space(.), '%s')]";
    String rejectedReason = "//p[normalize-space(.)='%s']";
    String eventCard = "//*[contains(normalize-space(.),'%s')]/ancestor::*[self::dwp-card-view or self::dwp-list-view or self::dwp-card][1]";
    public String cardViewButton="//button[@data-ta='layoutActionsCardViewTriggerButton']";
    private String fulFillmentIdsXpathInCardView="//span[normalize-space()='%s']/../following-sibling:: p[@data-ta='cardText_secondary.1']//span[@class='text-secondary ng-star-inserted']";
	private String fullFillmentIdsInListView="//div[@data-ta='layoutViewWideScreen_activeEvents']//span[normalize-space()='%s']/../..//p[contains(@data-ta,'cardText_secondary.1')]//span[@data-ta='cardTextPart_1']";
    protected static final By SUBMIT_COMMENTS = By.cssSelector("dwp-comments dwp-comment-form button.btn-primary");
    /** Descendants with role=link under a scoped comment/attachment block (used with {@code WebElement#findElements}). */
    private static final By SCOPE_DESCENDANT_ROLE_LINK = By.xpath(".//*[@role='link']");
    /** Attachment file name span inside media icon under an {@code a} within a scoped block. */
    private static final By ATTACHMENT_NAME_MEDIA_ICON_SPAN = By.cssSelector("a dwp-icon-media .icon-media__content-wrapper p span");
    /**
     * Relative XPath under a block for {@code contains(normalize-space(.), &lt;literal&gt;)}.
     * Build with {@link #xpathStringLiteralForContains(String)} between prefix and suffix.
     */
    private static final String XPATH_SCOPE_DESC_CONTAINS_NORMALIZED_TEXT_PREFIX = ".//*[contains(normalize-space(.), ";
    private static final String XPATH_SCOPE_DESC_CONTAINS_NORMALIZED_TEXT_SUFFIX = ")]";
    private static final String More_Option = "//*[contains(@data-ta,'fulfillmentOverflowActions_%s')]//button ";


    @FindBy(css = "dwp-comments button.btn-primary")
    public WebElement submitComments;

    @FindBy(css = "[data-ta='addCommentButton']")
    public WebElement addCommentButton;

    @FindBy(css = "adapt-rx-uploader input")
    public WebElement addAttachmentInput;

    @FindBy(xpath = "//*[contains(@data-ta,'fulfillmentDisplayId')]")
    WebElement workOrderID;

    @FindBy(xpath = "//*[contains(@data-ta,'fulfillmentDisplayId')]")
    List<WebElement> requestIdValueByDataTa;

    @FindBy(xpath =
            "//*[normalize-space()='Request ID' or normalize-space()='Request Id' or normalize-space()='Request id']"
                    + "/following-sibling::*[1]//*[self::span or self::div or self::p][1]"
                    + " | "
                    + "//*[normalize-space()='Request ID' or normalize-space()='Request Id' or normalize-space()='Request id']"
                    + "/following-sibling::*[1][self::span or self::div or self::p]")
    List<WebElement> requestIdValueByLabelSibling;

    @FindBy(xpath =
            "//*[normalize-space()='Request ID' or normalize-space()='Request Id' or normalize-space()='Request id']"
                    + "/ancestor::*[self::div or self::section][1]"
                    + "//*[self::span or self::div or self::p][normalize-space() and not(contains(.,'Request ID'))][1]")
    List<WebElement> requestIdValueByRow;

    @FindBy(xpath = "(//dwp-icon-media//p)[1] | //p[@data-ta='coloredMediaHeaderParagraph_0']")
    WebElement requestStatus;

    @FindBy(xpath = "//dwp-actions//button[contains(., 'Approve')]")
    WebElement approveButton;

    @FindBy(xpath = "//dwp-actions//button[contains(., 'Reject')]")
    WebElement buttonReject;

    @FindBy(xpath = "//*[contains(@class, 'modal-footer')]//button[2]")
    WebElement confirmRejectButtonLocator;

    @FindBy(xpath = "//*[contains(@class, 'modal-footer')]//button[1]")
    WebElement cancelRejectionButtonLocator;

    @FindBy(xpath = "//dwp-icon-media//dt[contains(@data-ta,'approvalsGroupTitle')]")
    WebElement rejectIcon;

    @FindBy(xpath = "//*[contains(@data-ta,'globalSearchPaginationResult')]")
    WebElement searchResultPagination;

    @FindBy(css = "[data-ta='globalSearchPaginationResult'] .page-next")
    WebElement searchResultPaginationNextPage;

    @FindBy(css = "[data-ta='globalSearchPaginationResult'] .page-prev>.page-link")
    WebElement searchResultPaginationPrevPageButton;

    @FindBy(xpath = "//span[contains(.,'Articles')]//parent::button[contains(@role,'tab')]")
    WebElement articleTab;

    @FindBy(xpath = "//span[contains(.,'Leave your comment here')]//parent::div//p")
    public WebElement commentInputFieldWithRichTextSupport;

    @FindBy(xpath = "//textarea[contains(@placeholder, 'Leave your comment here')]")
    WebElement commentInputFieldWithoutRichTextSupport;

    @FindBy(xpath = "//div[contains(@id,'cke_1_contents')]//iframe")
    public WebElement commentIframe;

    @FindBy(xpath = "//div[contains(@id,'cke_') and contains(@id,'_contents')]//iframe | //iframe[contains(@title,'Rich Text') or contains(@class,'cke_wysiwyg_frame')]")
    List<WebElement> commentEditorIframes;

    @FindBy(css = "dwp-comments [contenteditable='true']")
    List<WebElement> commentEditorContentEditables;

    @FindAll({
            @FindBy(css = ".comments__comment-block"),
            @FindBy(css = "dwp-comment-viewer"),
            @FindBy(css = "dwp-comments dwp-comment-viewer"),
            @FindBy(css = "dwp-comments .fr-view"),
            @FindBy(css = "dwp-comments .comment"),
            @FindBy(css = "dwp-comments [data-ta*='comment']")
    })
    List<WebElement> commentRenderedCandidates;
    
    //WebElements Locators
    @FindBy(xpath = "//*[@data-ta='typeaheadSearchInputWrapper']")
    WebElement DWP_HOMEPAGE;

    @FindBy(xpath = "//div[contains(@class, 'd-icon-search')]/following-sibling::div//input")
    WebElement searchInputField;

    @FindBy(xpath = "//dwp-colored-media-header//*[@data-ta='coloredMediaHeaderTitle']")
    WebElement itemTitle;

    @FindBy(xpath = "//div[.. and normalize-space()='Search']")
    WebElement searchOption;

    @FindBy(xpath = "//button[contains(.,'Search')]")
    WebElement searchButtonHeader;

    @FindBy(xpath = "//button[normalize-space()='Confirm' or normalize-space()='Continue' or normalize-space()='OK' or normalize-space()='Submit']")
    WebElement confirmButton;

    @FindBy(xpath = "//dwp-actions//button[contains(., 'On Hold')]")
    WebElement onHoldButton;

    @FindBy(xpath = "//button[@data-ta='backButton']")
    WebElement backButton;

    @FindBy(xpath = "//dwp-actions//button[contains(., 'Approve')] | //button[contains(text(),'Approve')]")
    WebElement approvalFormButton;

    @FindBy(xpath = "//*[contains(text(),'BWF') or contains(text(),'Helix Business Workflows')]")
    WebElement bwfApprovalLabel;

    @FindBy(xpath = "//a[contains(@aria-label,'Request Again')]")
    WebElement requestAgainButton;

    @FindBy(xpath = "//button[contains(@aria-label,'Reopen')]")
    WebElement reopenButton;

    @FindBy(xpath = "//textarea[@placeholder='Enter a reason for reopening this request']")
    WebElement reopenReasonTextarea;

    @FindBy(xpath = "//button[@data-ta='confirmDialogYesButton']")
    WebElement reopenDialogConfirmButton;

    @FindBy(xpath = "//button[contains(.,'Submit request')]")
    WebElement submitRequestButton;

    @FindBy(xpath = "//button[contains(@aria-label,'Cancel')]")
    WebElement cancelButton;

    @FindBy(xpath = "//button[@data-ta='confirmDialogYesButton']")
    WebElement confirmCancelRequestButton;

    @FindBy(xpath = "//span[contains(text(),'Cancelled')]")
    WebElement cancelledStatusText;

    @FindBy(xpath = "(//*[@href='#/activity/events/details'])[1]")
    WebElement firstRequestTileDetailsLink;

    @FindBy(xpath = "//button[contains(@class,'a-rating-binary__like') and normalize-space()='Yes']")
    WebElement yesButton;

    @FindBy(xpath = "//p[@data-ta='articleSideContent' and (contains(., 'people found this useful') or contains(., 'people found this helpful'))]")
    WebElement helpfulCounter;

    @FindBy(xpath = "//*[@data-ta = 'feedbackRating']//span[contains(.,'Thanks for your feedback.')]")
    WebElement feedbackMessage;

    @FindBy(xpath = "//p[@data-ta='articleSideContent' and contains(text(),'people found this useful')]")
    WebElement helpfulCounterLocator;

    @FindBy(xpath = "//h3[normalize-space()='Status']/following-sibling::dl//dt")
    WebElement requestStatusText;

    @FindBy(xpath = "//dwp-actions//button[contains(., 'Reassign')]")
    WebElement reassignButton;

    @FindBy(css = "input[placeholder='Search for people']")
    WebElement peopleSearchInput;

    @FindBy(css = "button.picker-item.btn_clear-style")
    WebElement peopleSearchFirstResult;

    @FindBy(xpath = "//button[normalize-space()='Reassign' or normalize-space()='Save' or normalize-space()='Apply' or normalize-space()='Confirm']")
    WebElement confirmReassignBtn;

    @FindBy(xpath = "//dwp-typeahead-search//button[@aria-label='Submit']")
    WebElement sendButton;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Choose someone')]")
    WebElement chooseSomeoneBtn;

    @FindBy(xpath = "//button[contains(@aria-label,'More') or contains(@aria-label,'Actions') or contains(@title,'More') or contains(@title,'Actions')]")
    WebElement actionsButton;

    @FindBy(xpath = ".//*[self::button or self::a][@aria-expanded='false' or contains(@aria-label,'Expand') or contains(@title,'Expand')]")
    WebElement expandToggle;

    @FindBy(xpath = ".//*[self::button or self::a][@aria-expanded='true' or contains(@aria-label,'Collapse') or contains(@title,'Collapse')]")
    WebElement collapseToggle;

    @FindBy(xpath = "//*[@data-ta='approvalsGroupSubtitle' and normalize-space()='Details']")
    WebElement detailsBtn;

    @FindBy(xpath = "//*[contains(@class, 'headline-item__description') and contains(. ,'Rejected')]")
    WebElement rejectedIcon;

    @FindBy(css = "dwp-event-approver dt.display-name")
    WebElement APPROVER_NAME;

    @FindBy(css = "dwp-event-approver dd.display-status")
    WebElement STATUS_IN_APPROVER_SECTION;

    @FindBy(xpath = "//button[@data-ta='layoutActionsListViewTriggerButton']")
    WebElement LIST_VIEW_BUTTON;

    @FindBy(xpath = "//button[@data-ta='layoutActionsCardViewTriggerButton']")
    WebElement CARD_VIEW_BUTTON;

    @FindBy(xpath = "//div[@contenteditable='true']")
	public WebElement COMMENT_TEXT_AREA;

    @FindBy(xpath = "//*[contains(@data-ta,'actionsOverflowActionButton') and @aria-label='Comment']")
	private WebElement FulfillmentComment ;

    @FindBy(css = ".comments__comment-block")
    List<WebElement> initComments;

    @FindBy(css = "adapt-rx-uploader input")
	public WebElement ADD_ATTACHMENT_INPUT;
	

    public ActivityPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ActivityPage.class.getName());
        commonWaits = new CommonWaits();
    }

    public ActivityPage openActivityPage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        log.info("Opening Activity Page");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForPageReadyState();
        String activityPageURL = getDWPURL() + "activity";
        log.info("Activity Page URL: " + activityPageURL);
        getDriver().get(activityPageURL);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public String getSubmittedRequestIDFromActiveEvents(String serviceRequestTitle) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        refreshBrowser();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(LATEST_ACTIVE_EVENT_RELEASE_REQUEST_ID, serviceRequestTitle)),30);
        WebElement srdLatestRequestID = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(LATEST_ACTIVE_EVENT_RELEASE_REQUEST_ID, serviceRequestTitle)));
        String srdLatestRequestIDValue = srdLatestRequestID.getText();
        log.info("Last active request id is: " + srdLatestRequestIDValue);
        return srdLatestRequestIDValue;
    }

    public String getSubmittedRequestIDFromPastEvents(String serviceRequestTitle) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitAndVerifyForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(LATEST_PAST_EVENT_RELEASE_REQUEST_ID, serviceRequestTitle)),30);
        WebElement srdLatestRequestID = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(LATEST_PAST_EVENT_RELEASE_REQUEST_ID, serviceRequestTitle)));
        String srdLatestRequestIDValue = srdLatestRequestID.getText();
        log.info("Last past event request id is: " + srdLatestRequestIDValue);
        return srdLatestRequestIDValue;
    }

    public ActivityPage openSrmRequestByRequestID(String reqID) {
        log.info("Click Active last Request ID: " + reqID);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, reqID)), 30),
                "Active event card with request ID: '" + reqID + "' is not visible.");
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, reqID)))).click();
        log.info("Active last Request ID: " + reqID);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        
        if(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, reqID)), 30)){
            commonWaits.clickByJS(findElement(By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, reqID))));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            if(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, reqID)), 30)) {
                commonWaits.clickWithoutInterception(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, reqID))));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            }
        }
        return this;
    }

    public void openSubmittedSbRequestInActiveEventsByTitle(String title) {
        log.info("Opening active event by title: " + title);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By[] candidates = new By[]{
                By.xpath(String.format(ACTIVE_EVENT_CARD_BY_TITLE, title)),
                By.xpath(String.format(ACTIVE_EVENT_CARD_BY_TEXT_ANYWHERE, title))
        };

        WebElement card = null;
        for (By by : candidates) {
            card = findFirstDisplayed(by);
            if (card != null) {
                break;
            }
        }

        if (card == null) {
            int count = 0;
            try {
                count = findElements(By.xpath(ACTIVE_EVENT_ANY_CARD)).size();
            } catch (Exception ignored) {
            }
            throw new AssertionError("Active event card not found for title/text: '" + title + "'. Active card count (best-effort): " + count);
        }

        commonWaits.clickWithoutInterception(card);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    public WebElement findFirstDisplayed(By by) {
        try {
            List<WebElement> els = findElements(by);
            if (els == null || els.isEmpty()) {
                return null;
            }
            for (WebElement el : els) {
                try {
                    if (el != null && el.isDisplayed()) {
                        return el;
                    }
                } catch (Exception ignored) {
                }
            }
            return els.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public void addCommentWithAttachmentsInSubmittedRequest(String comment, File file) {
        log.info("Attaching a comment: [" + comment + "] with a file with name: " + file.getName());
        List<WebElement> initComments = findElements(By.cssSelector(commentBlock));
        addAttachment(comment, file, By.cssSelector(ATTACHMENTS_IN_NEW_COMMENT));
        
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), submitComments , 30 ), "Submit comments button is not visible");
    	commonWaits.clickWithoutInterception(submitComments);

        commonWaits.waitTillNewCommentAdded(By.cssSelector(commentBlock), initComments.size());
    }

    public ActivityPage addRichTextCommentWithLinkAndAttachment(String formattedText, String url, String linkText, File file) {
        log.info("Adding rich text comment with link and attachment");

        final List<WebElement> initComments = findElements(By.cssSelector(CSS_COMMENT_BLOCK));
        try {
            commonWaits.waitForElementClickable(addCommentButton).click();
        } catch (TimeoutException e) {
            log.info("Comment section displayed");
        }

        String safeText = formattedText == null ? "" : formattedText;
        String safeUrl = url == null ? "" : url;
        String safeLinkText = linkText == null ? "" : linkText;
        String html = "<p><strong>" + safeText + "</strong> <a href='" + safeUrl + "' target='_blank' rel='noopener noreferrer'>" + safeLinkText + "</a></p>";

        // Enter rich text: support CKEditor iframe OR contenteditable editor (Froala/Quill/etc.)
        boolean entered = false;
        switchtoDefaultFrame();
        try {
            // Prefer explicit CKEditor iframe if present
            List<WebElement> iframes = commentEditorIframes;
            if (iframes != null && !iframes.isEmpty()) {
                WebElement iframe = iframes.get(0);
                if (iframe != null && iframe.isDisplayed()) {
                    getDriver().switchTo().frame(iframe);
                    WebElement body = commonWaits.waitForByElementToBeVisible(By.xpath(XPATH_CKEDITOR_BODY));
                    ((JavascriptExecutor) getDriver()).executeScript("arguments[0].innerHTML = arguments[1];", body, html);
                    entered = true;
                }
            }
        } catch (Exception e) {
            log.info("CKEditor iframe editor not detected/usable. Will try contenteditable editor. " + e.getMessage());
        } finally {
            switchtoDefaultFrame();
        }

        if (!entered) {
            // Fallback: contenteditable editor in page DOM
            List<WebElement> editables = commentEditorContentEditables;
            if (editables != null) {
                for (WebElement el : editables) {
                    try {
                        if (el != null && el.isDisplayed()) {
                            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].focus();", el);
                            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].innerHTML = arguments[1];", el, html);
                            entered = true;
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (!entered) {
            // Last resort: use the existing plain-text field locator (no link formatting guaranteed)
            log.warn("Rich text editor not found; falling back to plain sendKeys(). Link may not be formatted as anchor.");
            commonWaits.waitForElementClickable(commentInputFieldWithRichTextSupport);
            commentInputFieldWithRichTextSupport.click();
            commentInputFieldWithRichTextSupport.sendKeys(safeText + " " + safeLinkText + " " + safeUrl);
        }

        // Attach file (reuse existing helper)
        sendFileNameToInput(file, addAttachmentInput);

        commonWaits.waitForElementClickable(submitComments).click();
        waitForCommentToRenderBestEffort(initComments == null ? 0 : initComments.size(), safeText, safeLinkText, safeUrl);
        return this;
    }

    public void waitForCommentToRenderBestEffort(int initialCount, String text, String linkText, String url) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        final long timeoutMs = 60_000;
        final long pollMs = 500;
        final long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                // 1) Prefer "count increased" if the standard selector exists in this build
                List<WebElement> blocks = findElements(By.cssSelector(CSS_COMMENT_BLOCK));
                if (blocks != null && blocks.size() >= initialCount + 1) {
                    return;
                }

                // 2) If comment blocks never exist, use alternative comment container selectors
                if (commentRenderedCandidates != null && !commentRenderedCandidates.isEmpty()) {
                    return;
                }

                // 3) If link got rendered, that's enough
                if (linkText != null && !linkText.trim().isEmpty()) {
                    String xp = "//a[normalize-space()='" + linkText.trim() + "']";
                    List<WebElement> links = findElements(By.xpath(xp));
                    if (links != null && !links.isEmpty()) {
                        return;
                    }
                }

                // 4) If plain text visible somewhere in comments area, that's enough
                if (text != null && !text.trim().isEmpty()) {
                    String xp = "//*[contains(@class,'comments') or self::dwp-comments]//*[contains(normalize-space(.),'" + text.trim() + "')]";
                    List<WebElement> txt = findElements(By.xpath(xp));
                    if (txt != null && !txt.isEmpty()) {
                        return;
                    }
                }
            } catch (Exception ignored) {
            }

            commonWaits.waitForSometime(pollMs);
        }

        Assert.fail("Timed out waiting for comment to render. linkText=" + linkText + " text=" + text);
    }


    public void addCommentWithMultipleAttachmentsInSubmittedRequest(String comment, File[] files) {
        log.info("Attaching a comment: [" + comment + "] with " + (files == null ? 0 : files.length) + " attachment(s).");
        List<WebElement> initComments = findElements(By.cssSelector(commentBlock));

        try {
            commonWaits.waitForElementClickable(addCommentButton).click();
        } catch (TimeoutException e) {
            log.info("Comment section displayed");
        }

        if (comment != null) {
            commonWaits.waitForElementClickable(commentInputFieldWithRichTextSupport);
            commentInputFieldWithRichTextSupport.sendKeys(comment);
        }

        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    sendFileNameToInput(file, addAttachmentInput);
                }
            }
        }

        commonWaits.waitForElementClickable(submitComments).click();
        commonWaits.waitTillNewCommentAdded(By.cssSelector(commentBlock), initComments.size());
    }

    public void addAttachment(String comment, File file, By attachmentsInComments) {
        List<WebElement> initUploadedAttachments = findElements(attachmentsInComments);
        commonWaits.waitForElementClickable(addCommentButton);
        commonWaits.clickWithoutInterception(addCommentButton);

        if (comment != null) {
            if (commonWaits.isElementPresent(commentInputFieldWithRichTextSupport)) {
                commonWaits.clickWithoutInterception(commentInputFieldWithRichTextSupport);
                commentInputFieldWithRichTextSupport.sendKeys(comment);
            } else {
                commonWaits.clickWithoutInterception(commentInputFieldWithoutRichTextSupport);
                commentInputFieldWithoutRichTextSupport.sendKeys(comment);
            }
        }
        sendFileNameToInput(file, addAttachmentInput);
        commonWaits.waitTillNewCommentAdded(attachmentsInComments, initUploadedAttachments.size());
        Optional<WebElement> attachedFile = findElements(attachmentsInComments).stream()
                .filter(we -> {
                    String trim = we.getText().trim().replaceAll("([\\r\\n\\t])", "");
                    return trim.equals(file.getName());
                })
                .findFirst();
        Assert.assertTrue(attachedFile.isPresent(), "File with the name: '" + file.getName() + "' is not attached.");
    }

    public void sendFileNameToInput(File file, WebElement attachmentInput) {
        if (file != null) {
            log.info("Attaching file: '" + file.getAbsolutePath() + "'");
            executeJS(scriptToMakeFileElementVisible, attachmentInput);
            attachmentInput.sendKeys(file.getAbsolutePath());
        } else {
            log.warn("Can not attach file=null");
        }
    }

    public void verifyAttachments(String comment, File file) {
        String attachmentName = file.getName();
        log.info("Verifying the comment: '" + comment + "' with the attachment is present: '" + attachmentName + "'. Path: " + file.getAbsolutePath());
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();

        Optional<WebElement> commentsBlockWithAttachments = Optional.empty();
        long deadline = System.currentTimeMillis() + 30_000L;
        while (System.currentTimeMillis() < deadline) {
            commentsBlockWithAttachments = findCommentBlockWithAttachment(comment, attachmentName);
            if (commentsBlockWithAttachments.isPresent()) {
                break;
            }
            commonWaits.waitForSometime(800);
            scrollToBottomOfPage();
        }

        Assert.assertTrue(commentsBlockWithAttachments.isPresent(),
                "Comment block with the attachment: '" + attachmentName + "' is not present.");
        WebElement block = commentsBlockWithAttachments.get();
        if (comment != null && !comment.trim().isEmpty()) {
            List<WebElement> commentText = block.findElements(By.xpath(attachmentBlock));
            boolean commentFound = commentText.stream().anyMatch(c -> {
                try {
                    String t = c.getText().trim().replaceAll("([\\r\\n\\t])", "");
                    return t.equals(comment) || t.contains(comment.trim());
                } catch (Exception e) {
                    return false;
                }
            });
            if (!commentFound) {
                try {
                    String blockText = block.getText().replaceAll("([\\r\\n\\t])", "");
                    commentFound = blockText.contains(comment.trim());
                } catch (Exception e) {
                    commentFound = false;
                }
            }
            Assert.assertTrue(commentFound, "Comment text: '" + comment + "' is not present.");
        }
        Optional<WebElement> uiAttachmentName = findAttachmentNameInBlock(block, attachmentName);
        Assert.assertTrue(uiAttachmentName.isPresent(), "Attachment with the name: '" + attachmentName + "' is not present.");
        Assert.assertTrue(isElementClickable(uiAttachmentName.get()),
                "Attachment with the name: '" + attachmentName + "' is not present.");
    }

    private Optional<WebElement> findCommentBlockWithAttachment(String comment, String attachmentName) {
        List<WebElement> blocks = findElements(By.cssSelector(commentBlock));
        List<WebElement> blocksWithAttachment = new ArrayList<>();
        for (WebElement we : blocks) {
            try {
                if (!we.isDisplayed()) {
                    continue;
                }
                boolean hasAttachment = findAttachmentCandidates(we).stream()
                        .anyMatch(el -> linkIndicatesAttachment(el, attachmentName));
                if (hasAttachment) {
                    blocksWithAttachment.add(we);
                }
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                continue;
            } catch (Exception ignored) {
                continue;
            }
        }
        if (blocksWithAttachment.isEmpty()) {
            return Optional.empty();
        }
        if (comment == null || comment.trim().isEmpty()) {
            return Optional.of(blocksWithAttachment.get(blocksWithAttachment.size() - 1));
        }
        String needle = comment.trim();
        Optional<WebElement> withComment = blocksWithAttachment.stream()
                .filter(we -> {
                    try {
                        return we.getText().contains(needle);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .reduce((a, b) -> b);
        return withComment.isPresent() ? withComment : Optional.of(blocksWithAttachment.get(blocksWithAttachment.size() - 1));
    }

    private static List<WebElement> findAttachmentCandidates(WebElement scope) {
        List<WebElement> out = new ArrayList<>(scope.findElements(By.tagName("a")));
        for (WebElement el : scope.findElements(SCOPE_DESCENDANT_ROLE_LINK)) {
        	
        	
            out.add(el);
        }
        return out;
    }

    private boolean linkIndicatesAttachment(WebElement link, String attachmentName) {
        try {
            if (!link.isDisplayed()) {
                return false;
            }
            String href = Optional.ofNullable(link.getAttribute("href")).orElse("");
            String download = Optional.ofNullable(link.getAttribute("download")).orElse("").trim();
            String text = Optional.ofNullable(link.getText()).orElse("").trim();
            String aria = Optional.ofNullable(link.getAttribute("aria-label")).orElse("");
            String title = Optional.ofNullable(link.getAttribute("title")).orElse("");

            if (!download.isEmpty()) {
                if (download.equals(attachmentName) || download.endsWith(attachmentName) || download.contains(attachmentName)) {
                    return true;
                }
            }
            if (text.contains(attachmentName) || aria.contains(attachmentName) || title.contains(attachmentName)) {
                return true;
            }
            return !href.isEmpty() && href.contains(attachmentName);
        } catch (Exception e) {
            return false;
        }
    }

    private Optional<WebElement> findAttachmentNameInBlock(WebElement block, String attachmentName) {
        Optional<WebElement> strict = block.findElements(ATTACHMENT_NAME_MEDIA_ICON_SPAN)
                .stream()
                .filter(we -> {
                    try {
                        String trim = we.getText().trim().replaceAll("([\\r\\n\\t])", "");
                        return we.isDisplayed() && trim.equals(attachmentName);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst();
        if (strict.isPresent()) {
            return strict;
        }
        Optional<WebElement> link = findAttachmentCandidates(block).stream()
                .filter(l -> linkIndicatesAttachment(l, attachmentName))
                .findFirst();
        if (link.isPresent()) {
            return link;
        }
        String literal = xpathStringLiteralForContains(attachmentName);
        List<WebElement> byText = block.findElements(By.xpath(
                XPATH_SCOPE_DESC_CONTAINS_NORMALIZED_TEXT_PREFIX + literal + XPATH_SCOPE_DESC_CONTAINS_NORMALIZED_TEXT_SUFFIX));
        return byText.stream().filter(we -> {
            try {
                return we.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }).findFirst();
    }

    /** XPath 1.0 string literal safe for use inside contains(..., 'arg'). */
    private static String xpathStringLiteralForContains(String s) {
        if (!s.contains("'")) {
            return "'" + s + "'";
        }
        String[] segs = s.split("'", -1);
        StringBuilder b = new StringBuilder("concat(");
        for (int i = 0; i < segs.length; i++) {
            if (i > 0) {
                b.append(", \"'\", ");
            }
            b.append("'").append(segs[i]).append("'");
        }
        b.append(")");
        return b.toString();
    }

    public void verifyComment(String text) {
        log.info("Checking if comment by index: was posted");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(LATEST_COMMENT, text)), 30), "Latest Comment is not visible ");
        String comment = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(LATEST_COMMENT, text))).getText();
        Assert.assertTrue(comment.contains(text), "Comment should be match");
    }

    public ActivityPage verifyCommentAuthor(String expectedAuthor, String commentText) {
        log.info("Verifying comment author");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();
        WebElement commentBlock = commonWaits.waitForByElementToBeVisible(
                By.xpath(String.format(COMMENT_BLOCK_CONTAINING_TEXT, commentText)));
        String author = commentBlock.findElement(By.xpath(COMMENT_AUTHOR_XPATH)).getText().trim();
        Assert.assertEquals(author, expectedAuthor, "Comment author does not match expected.");
        return this;
    }

    public ActivityPage verifyOpenedRequestTitle(String expectedTitle) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), itemTitle, 20), "Item title is not visible in details page");
        Assert.assertTrue(itemTitle.getText().trim().contains(expectedTitle), "Opened request title is not as expected. Expected to contain: " + expectedTitle);
        return this;
    }

    public void verifyApprovalStatus(String requestID, String expectedStatus) {
        String status = findElement(By.xpath(String.format(approvalStatus, requestID))).getText().trim();
        Assert.assertEquals(status, expectedStatus, "Approval Status does not match with expected");
    }

    public void verifyApprovalStatusFromDetails(String expectedStatus) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), requestStatus, 30), "Request Status element is not visible");
        String status = requestStatus.getText().trim();
        log.info("Approval Status: " + status);
        log.info("Expected Status: " + expectedStatus);
        Assert.assertEquals(status, expectedStatus, "Approval Status does not match with expected");
    }

    public void openApprovalProfile(String requestId) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(openRequest, requestId)), 20), "Approval profile link is not present for request ID: " + requestId);
        WebElement approvalProfile = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(openRequest, requestId)));
        log.info("1. Open approval profile card with clickWithoutInterception Method");
        commonWaits.clickWithoutInterception(approvalProfile);
        if (!getDriver().getCurrentUrl().contains("details")) {
            log.info("2. Open approval profile card with .click Method");
            commonWaits.waitForElementClickable(approvalProfile).click();
            if (!getDriver().getCurrentUrl().contains("details")) {
                log.info("3. Open approval profile card with findElement .click Method");
                commonWaits.click(By.xpath(String.format(openRequest, requestId)));
                if (!getDriver().getCurrentUrl().contains("details")) {
                    log.info("4. Open approval profile card with clickByJS Method");
                    commonWaits.clickByJS(approvalProfile);
                    if (!getDriver().getCurrentUrl().contains("details")) {
                        log.info("5. Open approval profile card with clickByAction Method");
                        commonWaits.clickByAction(approvalProfile);
                        if (!getDriver().getCurrentUrl().contains("details")) {
                            log.info("6. Open approval profile card with pureSafeClick Method");
                            commonWaits.pureSafeClick(approvalProfile);
                            if (!getDriver().getCurrentUrl().contains("details")) {
                                log.info("7. Open approval profile card with retryingFindClick Method");
                                commonWaits.retryingFindClick(approvalProfile);
                            }
                        }
                    }
                }
            }
        }
        Assert.assertTrue(getDriver().getCurrentUrl().contains("details"), "approval profile not opened after 7 attempts for request ID: " + requestId);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForPageReadyState();
    }

    public void openApprovalProfileWithoutRefresh(String requestId) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(openRequest, requestId)), 20), "Approval profile link is not present for request ID: " + requestId);
        WebElement approvalProfile = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(openRequest, requestId)));
        commonWaits.clickWithoutInterception(approvalProfile);
        if (!getDriver().getCurrentUrl().contains("details")) {
            commonWaits.waitForElementClickable(approvalProfile).click();
            if (!getDriver().getCurrentUrl().contains("details")) {
                commonWaits.click(By.xpath(String.format(openRequest, requestId)));
                if (!getDriver().getCurrentUrl().contains("details")) {
                    commonWaits.clickByJS(approvalProfile);
                    if (!getDriver().getCurrentUrl().contains("details")) {
                        commonWaits.clickByAction(approvalProfile);
                        if (!getDriver().getCurrentUrl().contains("details")) {
                            commonWaits.pureSafeClick(approvalProfile);
                            if (!getDriver().getCurrentUrl().contains("details")) {
                                commonWaits.retryingFindClick(approvalProfile);
                            }
                        }
                    }
                }
            }
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForPageReadyState();
    }


    public void doApproveRequest() {
        log.info("Approving the request");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), approveButton, 20), "Approve button is not visible");
        try {
        commonWaits.clickByJS(approveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(!commonWaits.waitForElementVisible_withFluentWait(getDriver(), approveButton, 20), "Approve button is still visible");
        } catch (Exception |AssertionError e) {
            commonWaits.clickWithoutInterception(approveButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            Assert.assertTrue(!commonWaits.waitForElementVisible_withFluentWait(getDriver(), approveButton, 20), "Approve button is still visible");
        }
    }

    public void doRejectRequest(String reason, boolean ifConfirm) {
        log.info("Starting the rejection process");
        buttonReject.click();
        commonWaits.waitForElementPresent(rejectionReasonTextareaLocator);
        keyClearAndType(rejectionReasonTextareaLocator, reason);
        if (ifConfirm) {
            commonWaits.waitForElementClickable(confirmRejectButtonLocator).click();
        } else {
            commonWaits.waitForElementClickable(cancelRejectionButtonLocator).click();
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    public void openSubmittedRequestInPastEvents(String requestId) {
        log.info("Click Past last Request ID " + requestId);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> pastEvents = findElements(By.xpath(PAST_EVENT_CONTAINER_INACTIVE_XPATH));
        WebElement we = pastEvents.stream().filter(element -> element.getText().contains(requestId)).findFirst().orElse(null);
        clickWithoutInterception(we);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        log.info(" Past last Request ID " + requestId);
    }

    public void verifyStatusDetailPage(String status) {
        log.info("Verify Status in the detail page");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        String actualStatus = rejectIcon.getText().trim();
        Assert.assertEquals(actualStatus, status, "Approval Status does not match with expected");
    }

    public void verifyItemFound(String searchedItemTitle, Boolean shouldBeFound) {
        log.info("Verifying that search item " + searchedItemTitle + " is found " + shouldBeFound);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        String finalLocator = buildXpathWithQuotes(searchedItemTitle);
        if (shouldBeFound) {
            boolean isPaginationPresent = commonWaits.isElementPresent(searchResultPagination);
            if (isPaginationPresent) {
                // Click on first page if pagination available
                findElements(By.cssSelector(SEARCH_RESULT_PAGINATION_BUTTONS)).stream()
                        .filter((ele) -> ele.getText().trim().equals("1"))
                        .findFirst();
            }
            while (isPaginationPresent && !isElementPresent(By.xpath(finalLocator))) {
                // break the loop if there is no further pages available
                if (searchResultPaginationNextPage.getAttribute("class").contains("disabled")) {
                    break;
                }
                clickAndVerifyNextResultPagePagination();
            }
            assertTrue(isElementPresent(By.xpath(finalLocator)), " Item was not found: " + searchedItemTitle +
                    " with locator: " + finalLocator);
        } else {
            assertFalse(isElementPresent(By.xpath(finalLocator)), " Item was found: " + searchedItemTitle);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    // Add this helper method in the same class
    private String buildXpathWithQuotes(String text) {
        if (!text.contains("'")) {
            return String.format("//*[@data-ta='titleSearchItem' and contains(text(), '%s')]", text);
        } else {
            String[] parts = text.split("'", -1);
            StringBuilder concat = new StringBuilder("concat(");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    concat.append(", \"'\", ");
                }
                concat.append("'").append(parts[i]).append("'");
            }
            concat.append(")");
            return String.format("//*[@data-ta='titleSearchItem' and contains(text(), %s)]", concat);
        }
    }

    public void clickAndVerifyNextResultPagePagination() {
        log.info("Verifying next button pagination on search result page.");
        int currentPage = Integer.parseInt(findElement(By.cssSelector(SEARCH_RESULT_PAGINATION_CURRENT_PAGE)).getText());
        clickWithoutInterception(searchResultPaginationPrevPageButton);
        commonWaits.waitForElementTextChange(By.cssSelector(SEARCH_RESULT_PAGINATION_CURRENT_PAGE), String.valueOf(currentPage + 1));
        Assert.assertEquals(findElement(By.cssSelector(SEARCH_RESULT_PAGINATION_CURRENT_PAGE)).getText(), String.valueOf(currentPage + 1), "Current page number is incorrect");
    }

    public void clickOnKnowledgeArticleUsefulLink() {

        String validationSentence = getRkmHelpfulCounterSentence();
        log.info("---Original usefulness  = " + validationSentence);
        //Extract useful vote number and total vote number
        validationSentence = validationSentence.replaceAll("[^-?0-9]+", " ");
        List<String> numbers = new ArrayList<>();
        numbers = Arrays.asList(validationSentence.trim().split(" "));
        int originalUseful = Integer.parseInt(numbers.get(0));
        int originalTotal = Integer.parseInt(numbers.get(1));
        Map<String, Integer> voteResults = new HashMap<>();
        voteResults.put("useCount", originalUseful);
        voteResults.put("total", originalTotal);
        isUseful(true);
        verifyFeedbackMessage_AfterVoting();
        Map<String, Integer> updatedVotesResults = updateVotes(voteResults, true);
        validationSentence = generateVoteResultsText(updatedVotesResults);
        log.info("---New usefulness  = " + validationSentence);
    }

    public String getRkmHelpfulCounterSentence() {
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForElementVisibility(By.xpath(RKM_ARTICLE_RESULT_LOCATOR));
        return findElement(By.xpath(RKM_ARTICLE_RESULT_LOCATOR)).getText();
    }

    public void isUseful(Boolean isHelpful) {
        commonWaits.waitForExecutionComplete();
        Assert.assertTrue(isElementPresent(By.xpath(String.format(RKM_ARTICLE_VOTE_LOCATOR, "Was this helpful?"))));
        if (isHelpful) {
            clickWithoutInterception(By.xpath(String.format(RKM_ARTICLE_VOTE_LOCATOR, "Yes")));
        } else {
            clickWithoutInterception(By.xpath(String.format(RKM_ARTICLE_VOTE_LOCATOR, "No")));
        }
    }

    public void verifyFeedbackMessage_AfterVoting() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            commonWaits.waitForElementVisible(feedbackMessage, 20);
            Assert.assertTrue(feedbackMessage.isDisplayed(), "Feedback message is not present");
        } catch (Exception e) {
            Assert.fail("Feedback message is not present: " + e.getMessage());
        }
    }

    public Map<String, Integer> updateVotes(Map<String, Integer> votesBeforeCountChanges, boolean wasUseful) {
        int useCountInt = votesBeforeCountChanges.get("useCount");
        int totalCountInt = votesBeforeCountChanges.get("total");
        Map<String, Integer> results = new HashMap<>();
        if (wasUseful) {
            useCountInt++;
        }
        totalCountInt++;
        results.put("useCount", useCountInt);
        results.put("total", totalCountInt);
        return results;
    }

    public String generateVoteResultsText(Map<String, Integer> votes) {
        String sentence = "%s out of %s people found this resource helpful";
        String useCount = votes.get("useCount").toString();
        String totalCount = votes.get("total").toString();
        return String.format(sentence, useCount, totalCount);
    }

    public void openArticle(String articleName) {
        clickWithoutInterception(articleTab);
        clickWithoutInterception(By.xpath(String.format(articleNameLocator, articleName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    public ActivityPage searchItemInGlobalSearch(String item, String tab) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), searchInputField, 20), "Global search input field is not visible");
        commonWaits.waitForElementVisible(searchInputField).click();
        searchInputField.sendKeys(item);
        commonWaits.waitForElementVisible(sendButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(searchButtonHeader)) {
            clickWithoutInterception(searchButtonHeader);
        }
        commonWaits.clickWithoutInterception(By.xpath(String.format(globalSearchFilterTab, tab)));
        int count = countOfArticlesVisibleAfterSearching(item);
        Assert.assertTrue(count > 0, "Searched item is not displayed");
        if (Objects.equals(tab, "Requests")) {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(
                            getDriver(), By.xpath(String.format(requestSearchedDetailsBtn, item)), 10),
                    "Searched item is not displayed in Requests tab");
        } else {
            WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchItemLink, item)));
            commonWaits.javaScriptScrollIntoView(we);
            Assert.assertTrue(we.isDisplayed(), "Searched item is not displayed");
        }
        return this;
    }

    public void verifyTitle(String Title) {
        openExactMatchingArticle(Title);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible(itemTitle, 20).getText().contains(Title), "Title is not as expected");
    }

    public void navigateToSearchOption() {
        commonWaits.clickWithoutInterception(searchOption);
        commonWaits.waitForExecutionComplete();
    }

    public ActivityPage openServiceRequestById(String requestID) {
        log.info("Open service by clicking on service name");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SEARCH_TITLE_DETAILS, requestID)));
        getElement(String.format(SEARCH_TITLE_DETAILS, requestID)).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ActivityPage verifyAddCommentButtonVisible() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible(addCommentButton).isDisplayed(),
                "Add Comment button is not visible; request details page may not be opened.");
        return this;
    }

    public ActivityPage searchInGlobalSearch(String searchText, String tab) {
        commonWaits.waitForElementVisible(searchInputField).click();
        searchInputField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInputField.sendKeys(searchText);
        sendButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(searchButtonHeader)) {
            clickWithoutInterception(searchButtonHeader);
        }
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(globalSearchFilterTab, tab)), 10)
                , "Global search filter tab '" + tab + "' is not visible");
        commonWaits.clickWithoutInterception(By.xpath(String.format(globalSearchFilterTab, tab)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ActivityPage verifyRequestDisplaysInActiveEventsWithStatus(String requestId, String expectedStatusText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementPresent(String.format(ACTIVE_EVENT_CARD_BY_ID, requestId));
        WebElement card = findElement(By.xpath(String.format(ACTIVE_EVENT_CARD_BY_ID, requestId)));
        String cardText = card.getText();
        Assert.assertTrue(cardText.contains(expectedStatusText),
                "Request '" + requestId + "' is not displayed with status text '" + expectedStatusText + "'. Card text: " + cardText);
        return this;
    }

    public ActivityPage doHoldRequest() {
        log.info("Putting the request On Hold");
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), onHoldButton, 20), "On Hold button is not visible");
        commonWaits.waitForElementClickable(onHoldButton);
        onHoldButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public void confirmActionWithPasswordIfPrompted(String username, String password) {
        try {
            List<WebElement> passwordInputs = findElements(By.cssSelector(passwordInput));
            if (passwordInputs.isEmpty()) {
                return;
            }

            WebElement passwordInput = passwordInputs.stream().filter(WebElement::isDisplayed).findFirst().orElse(passwordInputs.get(0));
            passwordInput.clear();
            passwordInput.sendKeys(password);

            // Best-effort username fill if prompt includes it (some RSSO dialogs only ask password)
            if (username != null && !username.trim().isEmpty()) {
                List<WebElement> userInputs = findElements(By.xpath(usernameInput));
                userInputs.stream().filter(WebElement::isDisplayed).findFirst().ifPresent(ui -> {
                    try {
                        ui.clear();
                        ui.sendKeys(username);
                    } catch (Exception ignored) {
                        // ignore best-effort
                    }
                });
            }

            // Confirm / Continue / OK
            if (commonWaits.isElementPresent(confirmButton)) {
                commonWaits.clickWithoutInterception(confirmButton);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            }
        } catch (Exception e) {
            log.info("Password confirmation prompt handling completed with exception: " + e.getMessage());
        }
    }

    public ActivityPage doReassignApprovalTo(String assigneeFullName) {
        log.info("Reassigning approval to: " + assigneeFullName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementClickable(reassignButton);
        reassignButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(chooseSomeoneBtn).click();
        commonWaits.waitForElementVisible(peopleSearchInput).clear();
        peopleSearchInput.sendKeys(assigneeFullName);
        commonWaits.waitForElementClickable(peopleSearchFirstResult);
        commonWaits.clickWithoutInterception(peopleSearchFirstResult);

        // Try primary action button in modal footer
        if (commonWaits.isElementPresent(confirmReassignBtn)) {
            commonWaits.clickWithoutInterception(confirmReassignBtn);
        }

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ActivityPage verifyStatusDetailContains(String expectedText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible(requestStatus).getText().contains(expectedText),
                "Expected status/details to contain: '" + expectedText + "'");
        return this;
    }

    /**
     * Validate presence of request tile by name, click Details link, verify summary
     * details and return Fulfillment ID This method: 1. Waits for activity page to
     * load 2. Validates presence of the specified request tile 3. Clicks on the
     * Details link for the first matching tile 4. Validates the summary details on
     * the details page 5. Fetches and returns the Fulfillment ID (e.g.,
     * CASE-0000001366)
     *
     * @param requestTileName - The name of the request tile (e.g., "Request Time
     *                        Off")
     * @param summaryDetails  - The summary details to validate on the details page
     * @return String - The Fulfillment ID from the details page
     */
    public String validateRequestTileAndClickDetails(String requestTileName, String summaryDetails) {
        log.info("Validating presence of '" + requestTileName + "' tile and clicking Details link");
        String fulfillmentId = null;

        // Wait for activity page to load
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        // Validate presence of the request tile
        log.info("Checking for presence of '" + requestTileName + "' tile");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(requestTitleByName, requestTileName)));

        WebElement requestTile = findElement(By.xpath(String.format(requestTitleByName, requestTileName)));
        Assert.assertTrue(requestTile.isDisplayed(), "Request tile '" + requestTileName + "' is not displayed");
        log.info("Successfully validated presence of '" + requestTileName + "' tile");

        // Click on Details link for the first tile
        log.info("Clicking on Details link for the first '" + requestTileName + "' tile");
        commonWaits.clickWithoutInterception(firstRequestTileDetailsLink);
        log.info("Successfully clicked on Details link");

        // Wait for details page to load
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForExecutionComplete();

        // Validate summary details on the details page
        log.info("Validating summary details: '" + summaryDetails + "' on the details page");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(requestDetailsSummaryText, summaryDetails)));

        WebElement summaryElement = findElement(By.xpath(String.format(requestDetailsSummaryText, summaryDetails)));
        Assert.assertTrue(summaryElement.isDisplayed(),
                "Summary details '" + summaryDetails + "' is not displayed on details page");
        log.info("Successfully validated summary details: '" + summaryDetails + "' on the details page");

        // Fetch Fulfillment ID
        log.info("Fetching Fulfillment ID from details page");
        commonWaits.waitForElementVisible(workOrderID, 30);
        fulfillmentId = workOrderID.getText().trim();
        log.info("Successfully fetched Fulfillment ID: " + fulfillmentId);

        return fulfillmentId;

    }

    /**
     * Verify approval details form is opened
     */
    public ActivityPage verifyApprovalDetailsFormOpened() {
        log.info("Verifying approval details form is opened");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            commonWaits.waitForElementVisible(approvalFormButton);
            Assert.assertTrue(approvalFormButton.isDisplayed(), "Approval details form is not opened");
            log.info("Approval details form is opened successfully");
        } catch (Exception e) {
            log.error("Failed to verify approval details form: " + e.getMessage());
            Assert.fail("Approval details form is not opened");
        }
        return this;
    }

    /**
     * Click on Details button for a specific request ID from Active Events
     */
    public ActivityPage clickDetailsForActiveRequest(String requestID) {
        log.info("Clicking on Details for active request: " + requestID);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(detailsButtonByRequestID, requestID)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(detailsButtonByRequestID, requestID)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Click on Details button for a specific request ID from Past Events
     */
    public ActivityPage clickDetailsForPastRequest(String requestID) {
        log.info("Clicking on Details for past request: " + requestID);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(pastEventDetailsButton, requestID)));
        commonWaits.clickWithoutInterception(By.xpath(String.format(pastEventDetailsButton, requestID)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Check if BWF approval is visible
     *
     * @return true if BWF approval label/text is visible
     */
    public boolean isBWFApprovalVisible() {
        log.info("Checking if BWF approval is visible");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            log.info("BWF approval is visible");
            return bwfApprovalLabel.isDisplayed();
        } catch (Exception e) {
            log.info("BWF approval is not visible");
            return false;
        }
    }

    public ActivityPage clickBackButton() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickByJS(backButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Add comment in submitted request
     *
     * @param comment - Comment text to add
     */
    public ActivityPage addCommentInSubmittedRequest(String comment) {
        log.info("Adding comment in submitted request: " + comment);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            // Click Add Comment button (composer must be openable from this state for this flow)
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addCommentButton, 30),
                    "Add Comment button is not visible within 30 seconds");
            commonWaits.clickByJS(addCommentButton);

            // Enter comment
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), commentInputFieldWithRichTextSupport, 30),
                    "Comment input field is not visible within 30 seconds");
            commentInputFieldWithRichTextSupport.clear();
            commentInputFieldWithRichTextSupport.sendKeys(comment);
            log.info("Entered comment text");

            // Switch back from iframe if we switched
            try {
                getDriver().switchTo().defaultContent();
            } catch (Exception e) {
                log.info("Already in default content");
            }

            // Click Submit button
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), submitComments, 30),
                    "Submit comments button is not visible within 30 seconds");
            commonWaits.clickByJS(submitComments);
            log.info("Clicked Submit button");

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Comment added successfully");

        } catch (Exception e) {
            // Switch back to default content in case of error
            try {
                getDriver().switchTo().defaultContent();
            } catch (Exception ex) {
                log.error("Failed to switch back to default content: " + ex.getMessage());
            }
            log.error("Failed to add comment: " + e.getMessage());
            Assert.fail("Unable to add comment in submitted request");
        }
        return this;
    }

    /**
     * Click on Request Again button on request details page
     */
    public ActivityPage clickRequestAgain() {
        log.info("Clicking on Request Again button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementClickable(requestAgainButton);
        commonWaits.clickWithoutInterception(requestAgainButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Click on Reopen button on request details page
     */
    public ActivityPage clickReopenButton() {
        log.info("Clicking on Reopen button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), reopenButton, 10), "Reopen button is not displayed");
        commonWaits.waitForElementClickable(reopenButton);
        commonWaits.clickWithoutInterception(reopenButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Enter reason for reopening request and confirm
     */
    public ActivityPage enterReopenReasonAndConfirm(String reason) {
        log.info("Entering reopen reason: " + reason);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), reopenReasonTextarea, 10), "Reopen reason textarea is not displayed");
        typeKeys(reopenReasonTextarea, reason);
        log.info("Clicking on Reopen button in dialog");
       if (commonWaits.isElementPresent(reopenDialogConfirmButton))
        	{
    	   commonWaits.waitForElementClickable(reopenDialogConfirmButton);
    	   commonWaits.clickWithoutInterception(reopenDialogConfirmButton);
        	}
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Click on Submit request button after Request Again
     */
    public ActivityPage clickSubmitRequestButton() {
        log.info("Clicking on Submit request button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementClickable(submitRequestButton);
        submitRequestButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Click on Cancel button on request details page
     */
    public ActivityPage clickCancelButton() {
        log.info("Clicking on Cancel button");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementClickable(cancelButton);
        cancelButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Confirm cancellation of request in popup dialog
     */
    public ActivityPage confirmCancelRequest() {
        log.info("Confirming cancel request in popup");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementClickable(confirmCancelRequestButton);
        confirmCancelRequestButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    /**
     * Verify request status is Cancelled
     */
    public ActivityPage verifyCancelledStatus() {
        log.info("Verifying request is cancelled");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(cancelledStatusText);
        Assert.assertTrue(cancelledStatusText.isDisplayed(), "Cancelled status is not displayed");
        log.info("Request status is Cancelled");
        return this;
    }

    /**
     * Refresh the page
     */
    public ActivityPage refreshPage() {
        log.info("Refreshing the page");
        getDriver().navigate().refresh();
        commonWaits.waitForPageFullyLoaded(30, 3);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        return this;
    }

    /**
     * Count articles visible after searching - exact match only
     *
     * @param item - Article title to search for (exact match)
     * @return count of exact matching articles
     */
    public int countOfArticlesVisibleAfterSearching(String item) {
        log.info("Checking if no duplicate articles are visible");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> articles = findElements(By.xpath(String.format(searchItemLink, item)));

        int count = 0;
        for (WebElement article : articles) {
            String articleText = article.getText().trim();
            if (articleText.equals(item.trim())) {
                count++;
                log.info("Found exact match article: " + articleText);
            } else {
                log.info("Skipping non-exact match: " + articleText);
            }
        }
        log.info("Total exact matching articles found: " + count);
        return count;
    }

    /**
     * Verify only one article visible after searching
     *
     * @param item - Article title to search for
     * @param tab  - Tab to click (e.g., "Knowledge")
     * @return true if exactly one article found
     */
    public boolean VerifyOnlyOneArticleVisibleAfterSearching(String item, String tab) {
        commonWaits.waitForElementVisible(searchInputField).click();
        searchInputField.sendKeys(item);
        searchInputField.sendKeys(Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(searchButtonHeader)) {
            clickWithoutInterception(searchButtonHeader);
        }
        int count = countOfArticlesVisibleAfterSearching(item);
        commonWaits.clickWithoutInterception(By.xpath(String.format(globalSearchFilterTab, tab)));
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(searchItemLink, item)));
        commonWaits.javaScriptScrollIntoView(we);
        Assert.assertTrue(we.isDisplayed(), "Searched item is not displayed");
        return count == 1;
    }

    /**
     * Open exact matching article from search results
     *
     * @param item - Article title to open (exact match only)
     */
    public void openExactMatchingArticle(String item) {
        log.info("Opening exact matching article for: " + item);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> articles = findElements(By.xpath(String.format(searchItemLink, item)));

        boolean foundAndClicked = false;
        for (WebElement article : articles) {
            String articleText = article.getText().trim();
            if (articleText.equals(item.trim())) {
                log.info("Found exact match article, clicking: " + articleText);
                commonWaits.javaScriptScrollIntoView(article);
                clickWithoutInterception(article);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                foundAndClicked = true;
                break;
            }
        }

        if (!foundAndClicked) {
            log.error("Could not find exact match article: " + item);
            Assert.fail("Article not found: " + item);
        }
    }

    /**
     * Get the helpful counter value
     *
     * @return current counter value
     */
    public int getHelpfulCounter() {
        log.info("Getting helpful counter value");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            int[] counts = parseHelpfulCounterValues();
            int count = counts.length > 0 ? counts[0] : 0;
            log.info("Current helpful counter: " + count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get helpful counter: " + e.getMessage());
            return 0;
        }
    }

    public int[] parseHelpfulCounterValues() {
        commonWaits.waitForElementVisible(helpfulCounter);
        String counterText = helpfulCounter.getText().trim();
        return parseHelpfulCounterValues(counterText);
    }

    public int[] parseHelpfulCounterValues(String counterText) {
        Pattern numberPattern = Pattern.compile("(\\d+)");
        Matcher matcher = numberPattern.matcher(counterText);
        java.util.List<Integer> values = new java.util.ArrayList<>();
        while (matcher.find()) {
            values.add(Integer.parseInt(matcher.group(1)));
        }
        return values.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Click on Yes button for "Was this resource helpful?"
     */
    public ActivityPage clickOnYesWasThisResourceHelpful() {
        log.info("Clicking on Yes - Was this resource helpful");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            WebElement targetYesButton = yesButton;
            try {
                // Prefer the button next to the visible counter in the same article section
                targetYesButton = helpfulCounter.findElement(By.xpath("ancestor::*[contains(@class,'aside') or contains(@class,'article')][1]//button[contains(@class,'a-rating-binary__like')]"));
            } catch (Exception e) {
                log.info("Falling back to direct Yes locator");
            }
            commonWaits.clickWithoutInterception(targetYesButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Clicked Yes button successfully");
        } catch (Exception e) {
            log.error("Failed to click Yes button: " + e.getMessage());
            Assert.fail("Unable to click Yes button");
        }
        return this;
    }

    /**
     * Verify counter increased by 1 after clicking Yes
     *
     * @param counterBefore - Counter value before clicking Yes
     * @return true if counter increased by exactly 1
     */
    public boolean verifyCounterIncreased(int counterBefore) {
        log.info("Verifying counter increased from: " + counterBefore);
        int expected = counterBefore + 1;
        int maxAttempts = 8;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            int[] counters = parseHelpfulCounterValues();
            log.info("Counter values after attempt " + attempt + ": " + java.util.Arrays.toString(counters));

            for (int counter : counters) {
                if (counter == expected) {
                    log.info("Counter increased by 1 as expected");
                    return true;
                }
            }

            if (attempt < maxAttempts) {
                log.info("Counter not updated yet, refreshing and retrying");
                refreshBrowser();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                commonWaits.waitForExecutionComplete();
            }
        }

        int finalCounter = getHelpfulCounter();
        log.error("Counter did not increase correctly. Before: " + counterBefore + ", After: " + finalCounter);
        return false;
    }

    /**
     * Verify status is "Fault" with retry logic
     * Refreshes browser and retries up to 5 times with 5 second wait between retries
     *
     * @return boolean - true if Fault status found, false otherwise
     */
    public boolean verifyStatusOfService(String Status) {
        int retryLimit = 18; //It takes sometime around 2-3 minutes to get the status "Fault"
        int waitTimeSeconds = 10;

        log.info("Starting to verify status " + Status + " with retry limit: " + retryLimit);

        for (int i = 1; i <= retryLimit; i++) {
            try {
                log.info("Attempt " + i + " of " + retryLimit + " to verify status " + Status);

                // Wait for status element to be visible
                commonWaits.waitForElementVisible(requestStatusText);

                // Get status text
                String currentStatus = requestStatusText.getText().trim();
                log.info("Current status: " + currentStatus);
                if (currentStatus.equalsIgnoreCase(Status)) {
                    log.info("Status " + Status + " found on attempt " + i);
                    return true;
                }
                // If not the last attempt, refresh and wait
                if (i < retryLimit) {
                    log.info("Status is not " + Status + ", refreshing browser and waiting " + waitTimeSeconds + " seconds");
                    getDriver().navigate().refresh();
                    // Wait for page to load after refresh
                    commonWaits.waitForPageFullyLoaded(30, 2);
                }

            } catch (Exception e) {
                log.error("Error on attempt " + i + ": " + e.getMessage());

                // If not the last attempt, refresh and wait
                if (i < retryLimit) {
                    log.info("Refreshing browser and waiting " + waitTimeSeconds + " seconds before retry");
                    getDriver().navigate().refresh();
                    // Wait for page to load after refresh
                    commonWaits.waitForPageFullyLoaded(30, 2);
                }
            }
        }

        log.error("Status " + Status + " not found after " + retryLimit + " attempts");
        return false;
    }

    public ActivityPage selectFulfillmentByPrefix(String prefix) {
        Assert.assertNotNull(prefix, "prefix must not be null");
        String p = prefix.trim().toUpperCase();
        Assert.assertFalse(p.isEmpty(), "prefix must not be empty");

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By by = By.xpath(String.format(FULFILLMENT_ID_BY_PREFIX, p));
        Assert.assertTrue(commonWaits.isElementPresent(by, 30), "Fulfillment ID not present for prefix: " + p);

        WebElement el = commonWaits.waitForByElementToBeVisible(by);
        try {
            commonWaits.clickWithoutInterception(el);
        } catch (Exception e) {
            clickByAction(el);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ActivityPage searchRequestInGlobalSearch(String item, String tab) {
        commonWaits.waitForElementVisible(searchInputField).click();
        searchInputField.sendKeys(item);
        sendButton.click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(searchButtonHeader)) {
            clickWithoutInterception(searchButtonHeader);
        }
        commonWaits.clickWithoutInterception(By.xpath(String.format(globalSearchFilterTab, tab)));
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(RequestSearchItemLink, item)));
        commonWaits.javaScriptScrollIntoView(we);
        Assert.assertTrue(we.isDisplayed(), "Searched item is not displayed");
        return this;
    }

    public boolean isRequestAgainButtonPresent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return requestAgainButton.isDisplayed();
    }

    public ActivityPage verifyActingAsUserPresent(String expectedOriginalApproverFullName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(actingAsUserXpath, expectedOriginalApproverFullName))), "Acting as banner is not present for user: " + expectedOriginalApproverFullName);
        return this;
    }

    String approvalDisplayName = "//dwp-event-approver//*[contains(@class, 'display-name') and normalize-space()='%s']";
    String approvalDisplayStatus = "//dwp-event-approver//*[contains(@class, 'display-status') and normalize-space()='%s']";
    public ActivityPage verifyApprovalStatusWithDetails(String expectedStatus, String expectedApproverFullName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(detailsBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(approvalDisplayName, expectedApproverFullName))), "Approver name is not as expected: " + expectedApproverFullName);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(approvalDisplayStatus, expectedStatus))), "Approval status is not as expected: " + expectedStatus);
        return this;
    }

    public ActivityPage verifyRejectedInfoIconPresent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(rejectedIcon), "Rejected info icon is not present");
        return this;
    }

    public ActivityPage verifyRejectedReasonTextIsCorrect(String expectedReason) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        commonWaits.clickWithoutInterception(rejectedIcon);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(rejectedReason, expectedReason)), 10),
                "Rejected reason text is not as expected: " + expectedReason);
        return this;
    }

    public ActivityPage expandDependentService(String serviceName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(expandDependant, serviceName)));

        commonWaits.clickWithoutInterception(expandToggle);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        commonWaits.clickWithoutInterception(By.xpath(String.format(headers, serviceName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        return this;
    }

    public ActivityPage collapseDependentService(String serviceName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(collapseDependant, serviceName)));

        commonWaits.clickWithoutInterception(collapseToggle);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public String getHeadlineValueFromDependentService(String serviceName, String label) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dependentServiceHeadline, serviceName)));
        return findElement(By.xpath(String.format(headlineValueOfDependantService, label))).getText().trim();
    }

    public ActivityPage verifyApproverNameWithStatus(String approverName, String approvalStatus) {
        commonWaits.waitForElementVisible(APPROVER_NAME);
        Assert.assertEquals(APPROVER_NAME.getText().trim(), approverName, "Approver name is not correct");
        Assert.assertEquals(getText(STATUS_IN_APPROVER_SECTION), approvalStatus, "Status is not correct");
        return this;
    }

    public String getFulfillmentIdsTextFromActiveEvent(String requestIdOrTitle, boolean preferCardView) {
        WebElement eventCard = findActiveEventCardBestEffort(requestIdOrTitle, preferCardView);
        WebElement fulfillmentEl = commonWaits.findFirstDisplayedWithin(eventCard, By.xpath(fulfillmentIds));
        Assert.assertNotNull(fulfillmentEl, "Fulfillment IDs element not found on activity event card/list for: " + requestIdOrTitle);
        String text = fulfillmentEl.getText();
        text = (text == null) ? "" : text;
        assertFalse(text.trim().isEmpty(), "Fulfillment IDs text is empty for: " + requestIdOrTitle);
        return text.trim();
    }

    public String getFulfillmentIdsTooltipTextFromActiveEvent(String requestIdOrTitle, boolean preferCardView) {
        WebElement eventCard = findActiveEventCardBestEffort(requestIdOrTitle, preferCardView);
        WebElement fulfillmentEl = commonWaits.findFirstDisplayedWithin(eventCard, By.xpath(fulfillmentIds));
        Assert.assertNotNull(fulfillmentEl, "Fulfillment IDs element not found for tooltip extraction: " + requestIdOrTitle);
        return extractTooltipTextBestEffort(fulfillmentEl);
    }

    private WebElement findActiveEventCardBestEffort(String matchText, boolean preferCardView) {
        String cardXpath = ACTIVE_EVENT_CONTAINERS + String.format(eventCard, matchText);
        List<WebElement> candidates = findElements(By.xpath(cardXpath));
        if (candidates != null) {
            for (WebElement el : candidates) {
                try {
                    if (el != null && el.isDisplayed()) {
                        String tag = el.getTagName();
                        if (preferCardView && "dwp-card-view".equalsIgnoreCase(tag)) return el;
                        if (!preferCardView && "dwp-list-view".equalsIgnoreCase(tag)) return el;
                    }
                } catch (Exception ignored) {
                }
            }
            for (WebElement el : candidates) {
                try {
                    if (el != null && el.isDisplayed()) return el;
                } catch (Exception ignored) {
                }
            }
        }
        Assert.fail("Active event card/list not found for: " + matchText);
        return null;
    }

    private String extractTooltipTextBestEffort(WebElement el) {
        String title = el.getAttribute(SolQAConstants.TITLE_ATTRIBUTE);
        if (!title.isEmpty()) return title.trim();
        String ariaLabel = el.getAttribute(SolQAConstants.aria_label);
        if (!ariaLabel.isEmpty()) return ariaLabel.trim();

        try {
            new Actions(getDriver()).moveToElement(el).perform();
        } catch (Exception ignored) {
        }

        By[] tooltipCandidates = new By[]{
                By.cssSelector(".tooltip-inner"),
                By.cssSelector("[role='tooltip']"),
                By.xpath("//*[contains(@class,'tooltip-inner')]"),
                By.xpath("//*[contains(@class,'tooltip') and normalize-space()]")
        };
        for (By by : tooltipCandidates) {
            try {
                List<WebElement> tips = getDriver().findElements(by);
                if (tips == null) continue;
                for (WebElement t : tips) {
                    if (t != null && t.isDisplayed()) {
                        String txt = t.getText();
                        if (txt != null && !txt.trim().isEmpty()) return txt.trim();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        Assert.fail("Tooltip text not found for fulfillment IDs element");
        return "";
    }

    public ActivityPage verifyActiveEventRequestDisplaysWithStatus(String requestId, String expectedStatus) {
        Assert.assertNotNull(requestId, "Request Id is null");
        Assert.assertNotNull(expectedStatus, "Expected status is null");

        int attempts = 6;
        for (int i = 1; i <= attempts; i++) {
            try {
                openSrmRequestByRequestID(requestId);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                String actual = "";
                try {
                    actual = commonWaits.waitForElementVisible(requestStatusText).getText().trim();
                } catch (Exception ignored) {
                }
                if (actual.contains(expectedStatus)) {
                    return this;
                }
            } catch (Exception ignored) {
            }
            try {
                refreshBrowser();
            } catch (Exception ignored) {
            }
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }

        throw new AssertionError("Request '" + requestId + "' was not found with expected status '" + expectedStatus + "' in Active Events.");
    }

    public ActivityPage clickActionMenuOptionOnOpenedRequest(String optionText) {
        Assert.assertNotNull(optionText, "Option text is null");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(actionsButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownActionsOptions, optionText)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    @FindBy(xpath = "//div[@class='ticket__basic-info']")
    WebElement requestBasicInfoSection;

    public void switchingToSmartITAfterCrossLaunchingItThroughDWP(String expectedURLPart) {
        log.info("Verifying whether SmartIT is cross launched successfully through DWP");
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForNumberWindowsToBe(2);
        List<String> browserTabs = new ArrayList<>(getDriver().getWindowHandles());
        if (browserTabs.size() == 1) {
            Assert.fail("------- FAIL --> SmartIT application did not load ");
        }
        getDriver().switchTo().window(browserTabs.get(1));
        commonWaits.waitForElementVisible(requestBasicInfoSection, 10);
        String currentWindowURL = getDriver().getCurrentUrl();
        log.info("----- Current window URL - " + currentWindowURL);
        Assert.assertTrue(commonWaits.waitForURLContains(expectedURLPart), "Failed to verify URL");
    }

    public void clickOnCardViewButton(){
		commonWaits.waitForElementClickable(cardViewButton,60);
		commonWaits.clickWithoutInterception(By.xpath(cardViewButton));
	}

    public Map<String, String> getFulFillmentIdsFromActiveEventsInCardView(String requestID){
		Map<String,String> mapOfFulFillmentIds=new HashMap<>();
		this.clickOnCardViewButton();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        fulFillmentIdsXpathInCardView=String.format(fulFillmentIdsXpathInCardView,requestID);
		int counter=0;
		while (counter<3){
			if(commonWaits.isElementPresent(By.xpath(fulFillmentIdsXpathInCardView))){
				break;
			}
			else{
                   commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                   getDriver().navigate().refresh();
                   commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
			}
			counter++;
		}
		String fulFillmentIds=commonWaits.waitForByElementToBeVisible(By.xpath(fulFillmentIdsXpathInCardView)).getText().trim();
		String[] arryOfFulFillmentIds=fulFillmentIds.split(",");
		for(String fullFillmentID:arryOfFulFillmentIds){
			if(fullFillmentID.trim().startsWith("CRQ")){
				mapOfFulFillmentIds.put("ChangeID",fullFillmentID.trim());
			}
			if(fullFillmentID.trim().startsWith("INC")){
				mapOfFulFillmentIds.put("IncidentID",fullFillmentID.trim());
			}
			if(fullFillmentID.trim().startsWith("WO")){
				mapOfFulFillmentIds.put("WorkOrderID",fullFillmentID.trim());
			}
		}
		return  mapOfFulFillmentIds;
	}

    public ActivityPage clickListViewButton() {
        commonWaits.waitForElementClickable(LIST_VIEW_BUTTON,60);
		commonWaits.clickWithoutInterception(LIST_VIEW_BUTTON);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
		return this;
    }

    public void clickCardViewButton() {
		commonWaits.waitForElementClickable(CARD_VIEW_BUTTON,60);
		commonWaits.clickWithoutInterception(CARD_VIEW_BUTTON);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
	}

    public Map<String, String> getFulFillmentIdsFromActiveEventsInListView(String requestID){
		Map<String,String> mapOfFulFillmentIds=new HashMap<>();
		clickListViewButton();
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        fullFillmentIdsInListView=String.format(fullFillmentIdsInListView,requestID);
		String fulFillmentIds=commonWaits.waitForByElementToBeVisible(By.xpath(fullFillmentIdsInListView)).getText().trim();
		String[] arryOfFulFillmentIds=fulFillmentIds.split(",");
		for(String fullFillmentID:arryOfFulFillmentIds){
			if(fullFillmentID.trim().startsWith("CRQ")){
				mapOfFulFillmentIds.put("ChangeID",fullFillmentID.trim());
			}
			if(fullFillmentID.trim().startsWith("INC")){
				mapOfFulFillmentIds.put("IncidentID",fullFillmentID.trim());
			}
			if(fullFillmentID.trim().startsWith("WO")){
				mapOfFulFillmentIds.put("WorkOrderID",fullFillmentID.trim());
			}
		}
		this.clickCardViewButton();
		return  mapOfFulFillmentIds;
	}

    public void addCommentWithMultipleAttachmentInFulfillmentDetails(String comment, String[] fileNames) {
		log.info("Attaching a comment: [" + comment + "] with attachments in Fulfillment section ");
		WebElement ele=commonWaits.waitForByElementToBeVisible(By.xpath(String.format(More_Option,"INC")));
		clickWithoutInterception(ele);
		commonWaits.waitForElementVisible(FulfillmentComment);
		clickWithoutInterception(FulfillmentComment);
		switchtoDefaultFrame();
		commonWaits.waitForElementClickable(COMMENT_TEXT_AREA).sendKeys(comment);
		this.switchtoDefaultFrame();
		for(int i=0;i<fileNames.length;i++) {
			File file = CommonMethods.createTextFile(fileNames[i]);
			this.sendFileNameToInput(file, ADD_ATTACHMENT_INPUT);
		}
		clickWithoutInterception(SUBMIT_COMMENTS);
		commonWaits.waitForAngularRequestsToFinish();
	}

}


