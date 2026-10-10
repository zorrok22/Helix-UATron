package pages.ITSM.dwp;

import framework.BasePage;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ServiceRequestPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    public ServiceRequestPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ServiceRequestPage.class.getName());
        commonWaits = new CommonWaits();
    }

    //WebElements
    @FindBy(xpath = "//*[contains(@data-ta, 'fulfillmentDisplayId')]")
    public WebElement fulfillmentDisplayId;
    @FindBy(xpath = "//*[normalize-space()='Status']/ancestor::dwp-headline-item//*[contains(@class, 'headline-item__description')] | //*[@data-ta='approvalsGroupTitle']")
    WebElement serviceRequestStatus;
    @FindBy(xpath = "//*[contains(@data-ta, 'fulfillmentDisplayId')]")
    List<WebElement> fulfillmentDisplayIds;
    @FindBy(xpath = "//*[@data-ta='groupedHeadlineItem_0' or @data-ta='approvalsGroupTitle']")
    WebElement approvalStatus;
    @FindBy(xpath = "//div[contains(@class,'survey-banner')]")
    WebElement surveyBanner;
    @FindBy(xpath = "//span[@class='btn-content' and contains(.,'Submit')]/parent::button")
    WebElement surveySubmitButton;
    @FindBy(xpath = "//strong[contains(text(),'Thanks for your feedback!')]")
    WebElement feedbackThankYouMessage;
    @FindBy(xpath = "//button[@type='button' and @data-dismiss='modal' and @aria-label='Close']")
    WebElement closeDialogButton;
    @FindBy(xpath = "(//button[contains(normalize-space(.),'Cancel')])[1]")
    WebElement cancelRequestButton;
    @FindBy(xpath = "//*[contains(@class,'modal-footer')]//button[contains(normalize-space(.),'Cancel Request')]")
    WebElement confirmCancelButton;
    @FindBy(xpath = "//button[contains(@class, 'collaborator-actions__dropdown')]")
    WebElement collaboratorDropdownButtonXpath;
    @FindBy(xpath = "//button[contains(text(),'Add/remove')]")
    WebElement collaboratorAddRemoveButtonXpath;
    @FindBy(css = "input[placeholder='Search for people or groups']")
    WebElement collaboratorSearchInputCss;
    @FindBy(css = "button.close")
    WebElement closeCollaboratorWindowCss;
    @FindBy(xpath = "//dwp-collaborators-actions[@data-ta='eventDetailsDelegateCollaboratorsAction']//button")
    WebElement collaboratorDelegationDropdownXpath;
    @FindBy(css = "button[data-ta='collaboratorsActionsOpenPickerButton']")
    WebElement delegationAddButtonXpath;
    @FindBy(xpath = "//button[contains(.,'Mark as complete') or contains(.,'Mark task as complete') or contains(.,'Complete')]")
    WebElement markTaskCompleteButton;
    @FindBy(xpath = "//button[contains(@class,'today')]")
    WebElement todaysDayLocator;
    @FindBy(xpath = "//button[normalize-space()='OK']")
    WebElement OK;
    @FindBy(css = "button.btn.btn-primary")
    WebElement btnSubmitLocator;
    @FindBy(xpath = "//button[contains(@class,'a-rating__item is-excellent ng-star-inserted')]")
    WebElement EXCELLENT_RATING;
    @FindBy(xpath = "//p[contains(text(), 'Excellent')]")
    WebElement excellentRatingOnFeedbackModal;
    @FindBy(xpath = "//p[contains(text(), 'Okay')]")
    WebElement okayRatingOnFeedbackModal;
    @FindBy(xpath = "//button[contains(@class,'a-rating__item is-ok ng-star-inserted')]")
    WebElement OKAY_RATING;
    @FindBy(xpath = "//adapt-rx-textarea//textarea")
    WebElement FEEDBACK_INPUT;
    @FindBy(xpath = "//div[contains(@class, 'footer')]//button[contains(.,'Submit')]")
    WebElement SUBMIT_FEEDBACK;
    @FindBy(xpath = "//dwp-survey-completed")
    WebElement surveyCompletedView;

    @FindBy(xpath = "//dwp-headline-user/*[contains(text(),' Request for')]/..//*[contains(@class,'text-default ')]")
    WebElement requestedForValue;

    @FindBy(xpath = "//button[normalize-space()='Yes']")
    WebElement YES_BUTTON;

    @FindBy(xpath = "//dwp-service-view//button[normalize-space()= 'Change']")
    WebElement changeButton;

    @FindBy(xpath = "//dwp-service-view//button[contains(text(),'New request')]")
    WebElement New_Request;

    //String locators
    String collaboratorPillTextXpath = "//dwp-collaborators-actions//dwp-filter-pills//span/span[contains(@data-ta, 'filterPillsPillTextVisible')]";
    String collaboratorSearchResult = "button.picker-item.btn_clear-style";
    String npsRatingInputXpath = "//input[@type='radio' and contains(@id,'rating-selection')]//following-sibling::label[text()=' %s ']";
    String headlineItemValueXpath = "//dt[normalize-space()='%s']/following-sibling::dd//span";
    String requestAnswerValueXpath = "//dwp-request-answers-view//dt[normalize-space()='%s']/following-sibling::dd";
    String selectedDelegationCollaboratorXpath = "//dwp-collaborators-actions[@data-ta='eventDetailsDelegateCollaboratorsAction']//*[contains(@data-ta, 'filterPillsPillText') and contains(.,'%s')]";
    String detailByLabelDt = "//dt[normalize-space()='%s']/following-sibling::dd[1]";
    String detailByLabel = "//label[normalize-space()='%s']/following-sibling::*[1]";
    String detailByLabelFallback = "//*[normalize-space()='%s']/following-sibling::*[1]";
    String DETAIL_ACTION_BY_LABEL_AND_TEXT =
            "//dt[normalize-space()='%s']/following-sibling::dd[1]//*[self::a or self::button][normalize-space()='%s' or contains(normalize-space(.),'%s')]"
                    + " | //label[normalize-space()='%s']/following-sibling::*[1]//*[self::a or self::button][normalize-space()='%s' or contains(normalize-space(.),'%s')]";
    String DETAIL_LABEL_PRESENT = "//dt[normalize-space()='%s'] | //label[normalize-space()='%s'] | //*[normalize-space()='%s']";
    String surveyModalField = "//dwp-survey-completed//dl/dt[contains(text(),'%s')]/following-sibling::*";
    String TEXT_FIELD_ON_SURVEY_BY_LABEL = "//label[contains(.,'%s')]/following::div//input";
    String complexSurveyDateTime = "//label[contains(text(),'%s')]/ancestor::div//input[contains(@class,'datetime--input-control')]";
    String TEXT_AREA_ON_SURVEY_BY_LABEL = "//label[contains(.,'%s')]/following::div//textarea";
    String complexSurveyRadioButton = "//div[contains(text(),'%s')]/parent::*/parent::*//input[@type='radio']/following-sibling::label/span[contains(text(),'%s')]";
    String DROPDOWN_ON_SURVEY_BY_LABEL = "//label[contains(text(),'%s')]/../..//following-sibling::dwp-select//button";
    String DROPDOWN_VALUES_LOCATOR_Updated = "//*[contains(@class, 'a-dropdown-window')]//button";
    String surveyCheckboxLabel = "//dwp-survey-questionnaire//div[contains(.,'%s')]";
    String surveyCheckboxValues = "//dwp-survey-questionnaire//div[contains(.,'%s')]//div[contains(@class,'checkbox__item')]//span[starts-with(@class,'ng-star')]";
    String requestIDFromServiceItemsXpath = "//dwp-event-container-card[contains(normalize-space(.), '%s')]//span[contains(normalize-space(.), 'Request ID')]/following-sibling::span";

    // Fulfillment details (PWA request details — overflow menu, modal, summary)
    By ANY_FULFILLMENT_ID = By.xpath("//*[contains(@data-ta,'fulfillmentDisplayId')]");
    By ANY_DIALOG =
            By.xpath("//*[contains(@class,'modal') and (contains(@class,'show') or contains(@style,'display'))]"
                    + " | //*[@role='dialog']");
    private static final String DIALOG_TEXT_CONTAINS =
            "//*[contains(@class,'modal') and (contains(@class,'show') or contains(@style,'display'))]"
                    + "//*[contains(normalize-space(.), %s)]"
                    + " | //*[@role='dialog']//*[contains(normalize-space(.), %s)]";
    private static final String ANY_TEXT_CONTAINS = "//*[contains(normalize-space(.), %s)]";
    String fulfillmentIDs = "//*[contains(@data-ta,'fulfillmentDisplayId') and normalize-space()='%s']";
    String FULFILLMENT_OVERFLOW_IN_ROW =
            "//*[contains(@data-ta,'fulfillmentDisplayId') and normalize-space()='%s']"
                    + "/ancestor::*[.//*[contains(@data-ta,'fulfillmentOverflowActions')]][1]"
                    + "//*[contains(@data-ta,'fulfillmentOverflowActions')]//button";
    String FULFILLMENT_OVERFLOW_BY_DATA_TA_CONTAINS =
            "//*[contains(@data-ta,'fulfillmentOverflowActions') and contains(@data-ta,'%s')]//button";
    String FULFILLMENT_OVERFLOW_SCOPED_FROM_ID_EL =
            "ancestor::*[.//*[contains(@data-ta,'fulfillmentOverflowActions')]][1]"
                    + "//*[contains(@data-ta,'fulfillmentOverflowActions')]//button";
    String FULFILLMENT_OVERFLOW_MORE_ARIA_FROM_ID_EL =
            "ancestor::*[.//button[contains(@aria-label,'More') or contains(@aria-label,'more')]][1]"
                    + "//button[contains(@aria-label,'More') or contains(@aria-label,'more') or contains(@class,'overflow')]";
    String FULFILLMENT_DETAILS_MENU_OPTION_XPATH =
            "//*[@role='menu' or @role='listbox']//*[(self::button or self::a or self::span or @role='menuitem')"
                    + " and normalize-space()='Fulfillment Details']"
                    + " | //button[normalize-space()='Fulfillment Details']"
                    + " | //a[normalize-space()='Fulfillment Details']";
    String FULFILLMENT_DETAILS_SUMMARY_DD_CONTAINS =
            "//*[@role='dialog']//dt[normalize-space()='%s']/following-sibling::dd[1][contains(normalize-space(.), %s)]"
                    + " | //*[contains(@class,'modal')]//dt[normalize-space()='%s']/following-sibling::dd[1]"
                    + "[contains(normalize-space(.), %s)]";
    String FULFILLMENT_DETAILS_SUMMARY_LOOSE_CONTAINS =
            "//*[@role='dialog']//*[contains(normalize-space(.), %s)]"
                    + " | //*[contains(@class,'modal') and (contains(@class,'show') or contains(@style,'display'))]"
                    + "//*[contains(normalize-space(.), %s)]";
    String FULFILLMENT_DETAILS_SUMMARY_DD_NOT_BLANK =
            "//*[@role='dialog']//dt[normalize-space()='%s']/following-sibling::dd[1][normalize-space()!='']"
                    + " | //*[contains(@class,'modal')]//dt[normalize-space()='%s']/following-sibling::dd[1][normalize-space()!='']";

    public String getFulfillmentID() {
        final int maxRefreshAttempts = 10;
        final long waitBetweenAttempts = 15000;
        for (int attempt = 1; attempt <= maxRefreshAttempts; attempt++) {
            log.info("Refreshing request details page to fetch fulfillment id. Attempt " + attempt + " of " + maxRefreshAttempts);
            refreshBrowser();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            commonWaits.waitForPageReadyState();
            try {
                if (commonWaits.isElementPresent(fulfillmentDisplayId, 10)) {
                    WebElement fulfillmentElement = commonWaits.waitForElementVisible(fulfillmentDisplayId);
                    commonWaits.scrollToSpecificElement(fulfillmentElement);
                    String fulfillmentId = fulfillmentElement.getText();
                    if (fulfillmentId != null && !fulfillmentId.trim().isEmpty()) {
                        log.info("Fulfillment id generated successfully on attempt " + attempt + ": " + fulfillmentId.trim());
                        return fulfillmentId.trim();
                    }
                    log.info("Fulfillment element is visible but text is empty on attempt " + attempt);
                } else {
                    log.info("Fulfillment element is not visible on attempt " + attempt);
                }
            } catch (Exception e) {
                log.info("Unable to fetch fulfillment id on attempt " + attempt + ". Error: " + e.getMessage());
            }
            if (attempt < maxRefreshAttempts) {
                log.info("Waiting " + (waitBetweenAttempts / 1000) + "s before next attempt...");
                try { Thread.sleep(waitBetweenAttempts); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        Assert.fail("Fulfillment is not generated after " + maxRefreshAttempts + " attempts");
        return null;
    }

    public List<String> getAllFulfillmentIDs() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> els = fulfillmentDisplayIds;
        Set<String> unique = new LinkedHashSet<>();
        if (els != null) {
            for (WebElement el : els) {
                try {
                    if (el == null) continue;
                    String raw = el.getText();
                    if (raw == null) continue;
                    raw = raw.trim();
                    if (raw.isEmpty()) continue;

                    // If multiple IDs are rendered in one element, split by comma.
                    String[] parts = raw.split(",");
                    for (String p : parts) {
                        if (p == null) continue;
                        String t = p.trim();
                        if (!t.isEmpty()) {
                            unique.add(t);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return new ArrayList<>(unique);
    }

    public String getServiceRequestStatus() {
        refreshBrowser();
        return commonWaits.waitForElementVisible(approvalStatus).getText().trim();
    }

    public String getHeadlineItemValue(String label) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return commonWaits.waitForByElementToBeVisible(By.xpath(String.format(headlineItemValueXpath, label)))
                .getText().trim();
    }

    public void verifyHeadlineItemValueWithRetry(String label, String expectedValue, int timeoutSeconds) {
        long end = System.currentTimeMillis() + Duration.ofSeconds(timeoutSeconds).toMillis();
        String lastSeen = null;
        while (System.currentTimeMillis() < end) {
            try {
                refreshBrowser();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                lastSeen = getHeadlineItemValue(label);
                if (lastSeen.equalsIgnoreCase(expectedValue)) {
                    return;
                }
            } catch (Exception ignored) {
                // ignore and retry until timeout
            }
            commonWaits.waitForSometime(commonWaits.moreDelay);
        }
        Assert.fail("Headline item '" + label + "' did not match expected value '" + expectedValue + "' within "
                + timeoutSeconds + "s. Last seen value: '" + lastSeen + "'");
    }

    public void verifyServiceStatus(String expectedStatus) {
        boolean statusFound = false;
        String currentStatus = "";
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        long startTime = System.currentTimeMillis();
        long maxDuration = TimeUnit.MINUTES.toMillis(2);  // 2 minutes
        
        while (System.currentTimeMillis() - startTime < maxDuration) {
            commonWaits.waitForElementVisible(serviceRequestStatus);
             currentStatus = serviceRequestStatus.getText();
            if (currentStatus.equals(expectedStatus)) {
                statusFound = true;
                break;
            } else {
                refreshBrowser();
            }
        }
        Assert.assertTrue(statusFound, "Service status not found in " + maxDuration + " seconds. Expected: " + expectedStatus + ", Actual: " + currentStatus);
    }

    public ServiceRequestPage cancelServiceRequest() {
        log.info("Cancelling the service request");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), cancelRequestButton, 10)) {
            Assert.fail("Cancel request button is not present on Service Request details page.");
        }
        commonWaits.clickByJS(cancelRequestButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), confirmCancelButton, 10)) {
            commonWaits.clickByJS(confirmCancelButton);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForExecutionComplete();
        
        log.info("Cancel request action completed");
        return this;
    }

    public ServiceRequestPage selectNPSRating(String ratingValue) {
        log.info("Selecting NPS rating: " + ratingValue);
        commonWaits.waitForElementVisible(surveyBanner);
        WebElement ratingLabel = findElement(By.xpath(String.format(npsRatingInputXpath, ratingValue)));
        commonWaits.clickWithoutInterception(ratingLabel);
        log.info("NPS rating " + ratingValue + " selected successfully");

        log.info("Submitting NPS rating");
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(surveySubmitButton);
        commonWaits.clickWithoutInterception(surveySubmitButton);
        log.info("NPS rating submitted successfully");

        log.info("Validating feedback thank you message");
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(feedbackThankYouMessage);
        String thankYouText = feedbackThankYouMessage.getText();
        Assert.assertTrue(thankYouText.contains("Thanks for your feedback!"),
                "Thank you message not displayed. Actual message: " + thankYouText);
        log.info("Feedback thank you message validated: " + thankYouText);

        log.info("Closing feedback dialog");
        commonWaits.waitForElementVisible(closeDialogButton);
        commonWaits.clickWithoutInterception(closeDialogButton);
        log.info("Feedback dialog closed successfully");
        return this;
    }

    public String getDetailValueByLabel(String label) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By[] candidates = new By[]{
                By.xpath(String.format(detailByLabelDt, label)),
                By.xpath(String.format(detailByLabel, label)),
                By.xpath(String.format(detailByLabelFallback, label))
        };
        for (By candidate : candidates) {
            if (commonWaits.isElementPresent(candidate)) {
                String value = findElement(candidate).getText().trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }
        return "";
    }

    public boolean isDetailLabelPresent(String label) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return commonWaits.isElementPresent(By.xpath(String.format(DETAIL_LABEL_PRESENT, label, label, label)));
    }

    public void verifyDetailValueByLabelContains(String label, String expectedValue) {
        String actual = getDetailValueByLabel(label);
        Assert.assertTrue(actual != null && actual.contains(expectedValue),
                "Detail value mismatch for label '" + label + "'. Expected to contain: '" + expectedValue + "', Actual: '" + actual + "'");
    }

    public void clickDetailActionByLabel(String label, String actionText) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        By by = By.xpath(String.format(DETAIL_ACTION_BY_LABEL_AND_TEXT, label, actionText, actionText, label, actionText, actionText));
        Assert.assertTrue(commonWaits.isElementPresent(by),
                "Action '" + actionText + "' not found for label '" + label + "' on request details page.");
        commonWaits.clickWithoutInterception(by);
        commonWaits.waitForExecutionComplete();
    }

    public void verifyRequestedForUserName(String expectedUser) {
        log.info("Verifying Requested for user name: " + expectedUser);
        commonWaits.waitForElementVisible(requestedForValue);
        String actual = requestedForValue.getText().trim();
        Assert.assertTrue(actual != null && actual.contains(expectedUser),
                "Requested for user not as expected. Expected: " + expectedUser + ", Actual: '" + actual + "'");
    }

    public boolean isCollaboratorOptionAvailable() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return commonWaits.isElementPresent(collaboratorDropdownButtonXpath);
    }

    public ServiceRequestPage addRemoveCollaborator(String collaboratorName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(collaboratorDropdownButtonXpath).click();
        commonWaits.waitForElementVisible(collaboratorAddRemoveButtonXpath).click();
        commonWaits.waitForElementVisible(collaboratorSearchInputCss).sendKeys(collaboratorName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.cssSelector(collaboratorSearchResult), 10), "Collaborator search result not found for: " + collaboratorName);
        List<WebElement> collaboratorsSearchResult = commonWaits.getElements(By.cssSelector(collaboratorSearchResult));
        for (WebElement collaborator : collaboratorsSearchResult) {
            collaborator.click();
            break;
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(closeCollaboratorWindowCss).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage verifySelectedCollaboratorInDetailsPage(String collaboratorName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> pills = findElements(By.xpath(collaboratorPillTextXpath));
        boolean found = pills.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase(collaboratorName));
        Assert.assertTrue(found, "Collaborator '" + collaboratorName + "' is not present in the collaborators list");
        return this;
    }

    public ServiceRequestPage clickDelegationCollaboratorsOption() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(collaboratorDelegationDropdownXpath).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage addDelegationCollaborator(String collaboratorName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(delegationAddButtonXpath).click();
        commonWaits.waitForElementVisible(collaboratorSearchInputCss).sendKeys(collaboratorName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.cssSelector(collaboratorSearchResult), 10), "Collaborator search result not found for: " + collaboratorName);
        List<WebElement> collaboratorsSearchResult = commonWaits.getElements(By.cssSelector(collaboratorSearchResult));
        for (WebElement collaborator : collaboratorsSearchResult) {
            collaborator.click();
            break;
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(closeCollaboratorWindowCss).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage verifySelectedDelegationCollaborator(String userName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(selectedDelegationCollaboratorXpath, userName))),
                "User " + userName + " is absent in the Delegation collaborators list");
        return this;
    }

    public ServiceRequestPage verifySubmittedRequestEventDetails(String questionLabel, String expectedValue) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(requestAnswerValueXpath, questionLabel)), 20),
                "Answer for question '" + questionLabel + "' is not visible in request details.");
        WebElement answer = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(requestAnswerValueXpath, questionLabel)));
        String actual = answer.getText().trim();
        Assert.assertTrue(actual.contains(expectedValue),
                "Answer mismatch for question '" + questionLabel + "'. Expected to contain: '" + expectedValue + "' but found: '" + actual + "'");
        return this;
    }

    public ServiceRequestPage verifySubmittedRequestEventDetailNotPresent(String questionLabel) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertFalse(commonWaits.isElementPresent(By.xpath(String.format(requestAnswerValueXpath, questionLabel))),
                "Field '" + questionLabel + "' is present in request details but expected to be absent.");
        return this;
    }

    public boolean isSurveyOptionAvailable() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return commonWaits.isElementPresent(surveyBanner, 3) || commonWaits.isElementPresent(surveySubmitButton, 3);
    }

    public void markTaskAsComplete() {
        log.info("Marking task as complete");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementVisible(markTaskCompleteButton);
        commonWaits.clickWithoutInterception(markTaskCompleteButton);
        if(commonWaits.isElementPresent(YES_BUTTON)) {
            commonWaits.clickWithoutInterception(YES_BUTTON);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    public ServiceRequestPage selectCheckBoxValuesOnSurveyModalByLabel(String label, List<String> values) {
        List<WebElement> checkboxValues = findElements(By.xpath(String.format(surveyCheckboxValues, label)));
        boolean clicked = false;
        for (String value : values) {
            for (WebElement checkboxValue : checkboxValues) {
                if (checkboxValue.getText().contains(value)) {
                    clickByJS(checkboxValue);
                    commonWaits.waitForSometime(commonWaits.minDelay);
                    clicked = true;
                }
            }
            if (!clicked) {
                throw new AssertionError(String.format("There is no checkbox with name, " + value));
            }
        }
        return this;
    }

    public ServiceRequestPage setValueInTextFieldOfSurveyModalByLabel(String label, String value) {
        commonWaits.waitForByElementToBeVisible((By.xpath(String.format(TEXT_FIELD_ON_SURVEY_BY_LABEL, label))));
        commonWaits.waitForElementClickable(String.format(TEXT_FIELD_ON_SURVEY_BY_LABEL, label), 5);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TEXT_FIELD_ON_SURVEY_BY_LABEL, label))).clear();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TEXT_FIELD_ON_SURVEY_BY_LABEL, label))).sendKeys(value);
        return this;
    }

    public ServiceRequestPage selectCurrentDateTimeOnSurveyModal(String calendarLabel) {
        complexSurveyDateTime = String.format(complexSurveyDateTime, calendarLabel);
        try {
            Robot robot = new Robot();
            for (int i = 0; i < 10; i++) {
                robot.keyPress(KeyEvent.VK_PAGE_DOWN);
            }
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(complexSurveyDateTime));
        clickByJS(we);
        commonWaits.waitForElementVisible(todaysDayLocator).click();
        commonWaits.waitForElementVisible(OK).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(complexSurveyDateTime));
        return this;
    }

    public ServiceRequestPage selectValueInDropDownOnSurveyModalByLabel(String dropdownName, String value) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_ON_SURVEY_BY_LABEL, dropdownName))).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(DROPDOWN_VALUES_LOCATOR_Updated));
        findElements(By.xpath(DROPDOWN_VALUES_LOCATOR_Updated))
                .stream()
                .filter(e -> e.getText().contains(value)).findFirst()
                .orElseThrow(
                        () -> new AssertionError(String.format("There is no option with '%s' name", value)))
                .click();
        commonWaits.waitForSometime(1000);
        return this;
    }

    public ServiceRequestPage clickOnRadioButtonOnSurveyModalWithLabel(String sLabel, String value) {
        String radioButton = String.format(complexSurveyRadioButton, sLabel, value);
        commonWaits.waitForByElementToBeVisible(By.xpath(radioButton)).click();
        return this;
    }

    public ServiceRequestPage setValueInTextAreaOfSurveyModalByLabel(String label, String value) {
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TEXT_AREA_ON_SURVEY_BY_LABEL, label)));
        we.clear();
        we.sendKeys(value);
        return this;
    }

    public ServiceRequestPage clickSubmitButtonOnSurveyModal() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();
        commonWaits.waitForElementClickable(btnSubmitLocator).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage rateExcellentAndAddFeedback(String feedbackText) {
        clickExcellent();
        commonWaits.waitForElementVisible(excellentRatingOnFeedbackModal);
        enterFeedbackAndSubmit(feedbackText);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage clickExcellent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(EXCELLENT_RATING);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage rateOkayAndAddFeedback(String feedbackText) {
        clickOkay();
        commonWaits.waitForElementVisible(okayRatingOnFeedbackModal);
        enterFeedbackAndSubmit(feedbackText);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage clickOkay() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(OKAY_RATING);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public String getSurveyAnswerValueFor(String questionLabel) {
        commonWaits.waitForElementVisible(surveyCompletedView);
        commonWaits.waitForExecutionComplete();
        try {
            if (findElement(By.xpath(String.format(surveyModalField, questionLabel))).isDisplayed()) {
                return findElement(By.xpath(String.format(surveyModalField, questionLabel))).getText().trim();
            } else {
                log.info(String.format("Survey answer for field {%s} is not displayed", questionLabel));
                return null;
            }
        } catch (NoSuchElementException e) {
            log.info(String.format("Survey modal - Exception {%s} working with field {%s}", e.getMessage(), questionLabel));
            return null;
        }
    }

    public ServiceRequestPage enterFeedbackAndSubmit(String feedbackText) {
        commonWaits.waitForElementVisible(FEEDBACK_INPUT).sendKeys(feedbackText);
        commonWaits.waitForExecutionComplete();
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementVisible(SUBMIT_FEEDBACK).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage waitForFulfillmentSectionToDisplay() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(ANY_FULFILLMENT_ID, 60),
                "Fulfillment section/IDs not visible on request details.");
        return this;
    }

    public ServiceRequestPage openFulfillmentDetailsByPrefix(String prefix) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        waitForFulfillmentSectionToDisplay();

        String p = prefix == null ? "" : prefix.trim();
        Assert.assertFalse(p.isEmpty(), "Fulfillment prefix cannot be null/empty");

        List<String> ids = getAllFulfillmentIDs();
        String match = null;
        for (String id : ids) {
            if (id != null && id.trim().toUpperCase().startsWith(p.toUpperCase())) {
                match = id.trim();
                break;
            }
        }
        Assert.assertNotNull(match, "No fulfillment id found with prefix '" + prefix + "'. Seen: " + ids);

        if (!openFulfillmentDetailsViaOverflowMenu(match)) {
            log.info("Overflow menu path not used or failed; opening details by clicking fulfillment id: " + match);
            By idBy = By.xpath(String.format(fulfillmentIDs, match));
            Assert.assertTrue(commonWaits.isElementPresent(idBy, 30), "Fulfillment id element not found for: " + match);
            commonWaits.clickWithoutInterception(idBy);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }

        Assert.assertTrue(commonWaits.isElementPresent(ANY_DIALOG, 30),
                "Fulfillment details dialog did not open for fulfillment: " + match);
        return this;
    }

    /**
     * PWA request details: open row kebab (overflow), choose "Fulfillment Details". Falls back to legacy click on id
     * when overflow locators are absent.
     */
    public boolean openFulfillmentDetailsViaOverflowMenu(String fulfillmentId) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (fulfillmentId == null || fulfillmentId.trim().isEmpty()) {
            return false;
        }
        String id = fulfillmentId.trim();
        By idBy = By.xpath(String.format(fulfillmentIDs, id));
        if (!commonWaits.isElementPresent(idBy, 30)) {
            log.info("Fulfillment id not on page for overflow flow: " + id);
            return false;
        }
        WebElement idEl = commonWaits.waitForByElementToBeVisible(idBy);
        try {
            scrollToElement(idEl);
        } catch (Exception ignored) {
        }

        WebElement overflow = findFulfillmentOverflowButton(id, idEl);
        if (overflow == null) {
            return false;
        }
        commonWaits.clickWithoutInterception(overflow);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        if (!commonWaits.isElementPresent(By.xpath(FULFILLMENT_DETAILS_MENU_OPTION_XPATH), 12)) {
            log.info("'Fulfillment Details' menu option not visible after opening overflow for: " + id);
            return false;
        }
        commonWaits.clickWithoutInterception(By.xpath(FULFILLMENT_DETAILS_MENU_OPTION_XPATH));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return true;
    }

    private WebElement findFulfillmentOverflowButton(String fulfillmentId, WebElement idEl) {
        String idForContains = fulfillmentId.replace("'", ""); // display ids are typically alphanumeric
        By byRowScoped = By.xpath(String.format(FULFILLMENT_OVERFLOW_IN_ROW, fulfillmentId));
        if (commonWaits.isElementPresent(byRowScoped, 6)) {
            return findElement(byRowScoped);
        }
        By byDataTa = By.xpath(String.format(FULFILLMENT_OVERFLOW_BY_DATA_TA_CONTAINS, idForContains));
        if (commonWaits.isElementPresent(byDataTa, 4)) {
            return findElement(byDataTa);
        }
        try {
            List<WebElement> scoped = idEl.findElements(By.xpath(FULFILLMENT_OVERFLOW_SCOPED_FROM_ID_EL));
            if (!scoped.isEmpty()) {
                return scoped.get(0);
            }
        } catch (Exception ignored) {
        }
        try {
            List<WebElement> more = idEl.findElements(By.xpath(FULFILLMENT_OVERFLOW_MORE_ARIA_FROM_ID_EL));
            if (!more.isEmpty()) {
                return more.get(more.size() - 1);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public ServiceRequestPage verifyFulfillmentDetailsFields(String expectedFieldsCsv) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(ANY_DIALOG, 15), "Fulfillment details dialog is not open.");

        if (expectedFieldsCsv == null || expectedFieldsCsv.trim().isEmpty()) {
            return this;
        }

        String[] fields = expectedFieldsCsv.split(",");
        for (String raw : fields) {
            if (raw == null) continue;
            String f = raw.trim();
            if (f.isEmpty()) continue;

            String lit = quoteForXpathContains(f);
            By inDialog = By.xpath(String.format(DIALOG_TEXT_CONTAINS, lit, lit));
            By anywhere = By.xpath(String.format(ANY_TEXT_CONTAINS, lit));

            Assert.assertTrue(commonWaits.isElementPresent(inDialog, 10) || commonWaits.isElementPresent(anywhere, 10),
                    "Expected fulfillment details field label not visible: " + f);
        }
        return this;
    }

    /**
     * Asserts the Details modal shows the expected summary (prefers dt/dd "Summary" row; otherwise any dialog text match).
     */
    public ServiceRequestPage verifyFulfillmentDetailsSummaryContains(String expectedText) {
        if (expectedText == null || expectedText.trim().isEmpty()) {
            return this;
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(ANY_DIALOG, 15), "Fulfillment details dialog is not open.");

        String snippet = quoteForXpathContains(expectedText.trim());
        By bySummaryDd = By.xpath(String.format(
                FULFILLMENT_DETAILS_SUMMARY_DD_CONTAINS,
                SolQAConstants.SUMMARY_LABEL,
                snippet,
                SolQAConstants.SUMMARY_LABEL,
                snippet));
        By byLoose = By.xpath(String.format(FULFILLMENT_DETAILS_SUMMARY_LOOSE_CONTAINS, snippet, snippet));

        Assert.assertTrue(commonWaits.isElementPresent(bySummaryDd, 15) || commonWaits.isElementPresent(byLoose, 15),
                "Expected summary text not visible in fulfillment details dialog: " + expectedText);
        return this;
    }

    /** Verifies the Details dialog has a non-empty Summary value (ITSM summary may differ from DWP request summary text). */
    public ServiceRequestPage verifyFulfillmentDetailsSummaryNotBlank() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(ANY_DIALOG, 15), "Fulfillment details dialog is not open.");
        By byDd = By.xpath(String.format(
                FULFILLMENT_DETAILS_SUMMARY_DD_NOT_BLANK, SolQAConstants.SUMMARY_LABEL, SolQAConstants.SUMMARY_LABEL));
        Assert.assertTrue(commonWaits.isElementPresent(byDd, 15),
                "Summary field is missing or empty in fulfillment details dialog.");
        return this;
    }

    public ServiceRequestPage closeFulfillmentDetailsModalIfPresent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            if (commonWaits.isElementPresent(closeDialogButton, 3)) {
                commonWaits.clickWithoutInterception(closeDialogButton);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            } else if (commonWaits.isElementPresent(By.cssSelector("button.close"), 3)) {
                commonWaits.clickWithoutInterception(By.cssSelector("button.close"));
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            }
        } catch (Exception ignored) {
        }
        return this;
    }

    private static String quoteForXpathContains(String text) {
        if (text == null) {
            return "''";
        }
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }
        StringBuilder sb = new StringBuilder("concat(");
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i > 0) sb.append(",");
            if (c == '\'') {
                sb.append("\"'\"");
            } else if (c == '"') {
                sb.append("'\"'");
            } else {
                sb.append("'").append(c).append("'");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    @FindBy(xpath = "//*[text()='Related Items']/following::button[normalize-space()='Show More']")
    WebElement XPATH_SHOW_MORE_BUTTON;
    @FindBy(xpath = "//button[contains(.,'Request now') or contains(.,'Request Now')]")
    WebElement REQUEST_NOW_BUTTON;
    String RELATED_ITEMS_HEADER_TITLE = "//dwp-knowledge-article-side-item[contains(. ,'Related Items')]//a[contains(@aria-label, '%s')]";
    
    public ServiceRequestPage verifyCollaboratorNotPresentInDetailsPage(String collaboratorName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertFalse(isCollaboratorPresentInDetailsPage(collaboratorName),
                "Collaborator '" + collaboratorName + "' is present in the collaborators list but expected to be absent");
        return this;
    }

    public boolean isCollaboratorPresentInDetailsPage(String collaboratorName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> pills = findElements(By.xpath(collaboratorPillTextXpath));
        return pills.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase(collaboratorName));
    }

    public ServiceRequestPage clickOnRelatedService(String serviceName) {
        refreshBrowser();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        scrollToBottomOfPage();
        if (commonWaits.isElementPresent(XPATH_SHOW_MORE_BUTTON)) {
            commonWaits.waitForElementClickable(XPATH_SHOW_MORE_BUTTON).click();
        }
        String relatedServiceName = String.format(RELATED_ITEMS_HEADER_TITLE, serviceName);
        commonWaits.clickWithoutInterception(By.xpath(relatedServiceName));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage clickRequestNowIfPresentOnDwpServicePage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        Assert.assertTrue(commonWaits.isElementPresent(REQUEST_NOW_BUTTON), "Service is in available state");
        commonWaits.waitForElementVisible(REQUEST_NOW_BUTTON).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage openFulfillmentByID(String fulfillmentId) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (!openFulfillmentDetailsViaOverflowMenu(fulfillmentId)) {
            commonWaits.clickWithoutInterception(By.xpath(String.format(fulfillmentIDs, fulfillmentId)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
        return this;
    }

    @FindBy(xpath = "//button[normalize-space()='Add response']")
    WebElement addResponseButton;
    public ServiceRequestPage addResponseDWPCRepeatQuestionService(String response1, String response2) {
        commonWaits.waitForElementVisible(addResponseButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        fillTextFieldByLabel("Repeat1_Question", response1);
        commonWaits.waitForElementVisible(addResponseButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        fillTextFieldByLabel("Repeat1_Question", response2);
        return this;
    }

    String SET_TEXTFIELD_BYLABEL = "(//label[contains(.,'%s')]/../following-sibling::div//input)[last()]";
    public ServiceRequestPage fillTextFieldByLabel(String label, String text) {
        commonWaits.waitForExecutionComplete();
        log.info("Filling SRD field labeled " + label + " with text '" + text + "'");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(SET_TEXTFIELD_BYLABEL, label)));
        log.debug("Clearing field and Typing text");
        commonWaits.waitForElement(By.xpath(String.format(SET_TEXTFIELD_BYLABEL, label))).clear();
        getDriver().findElement(By.xpath(String.format(SET_TEXTFIELD_BYLABEL, label))).sendKeys(text);
        commonWaits.waitForSometime(commonWaits.minDelay);
        return this;
    }

    public String getRequestIDFromServiceItems(String serviceName) {
        WebElement element = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(requestIDFromServiceItemsXpath, serviceName)));
        return element.getText().trim();
    }

    public void ClickOnNewRequestInDependencySection(){
        log.info("ClickOnNewRequestInDependencySection ");
        if(commonWaits.waitForElementVisible_withFluentWait(getDriver(), changeButton, 20)){
            click(changeButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        }
        commonWaits.waitForElementVisible(New_Request);
        click(New_Request);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
    }

    @FindBy(xpath = "//dwp-event-details//button[contains(., 'Approve')]")
    WebElement approveButton;
    @FindBy(xpath = "//dwp-event-details//button[contains(., 'Reject')]")
    WebElement rejectButton;
    @FindBy(xpath = "//dwp-event-details//button[contains(., 'On Hold')]")
    WebElement onHoldButton;
    public ServiceRequestPage clickApproveRequestInEventDetails() {
        log.info("Approving request from event details");
        commonWaits.waitForElementVisible(approveButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public String rejectionReasonTextareaLocator = "//textarea";
    @FindBy(xpath = "//*[contains(@class, 'modal-footer')]//button[2]")
    WebElement confirmRejectButtonLocator;
    @FindBy(xpath = "//*[contains(@class, 'modal-footer')]//button[1]")
    WebElement cancelRejectionButtonLocator;
    public ServiceRequestPage clickRejectRequestInEventDetails(String reason, boolean ifConfirm) {
        log.info("Rejecting request from event details");
        commonWaits.waitForElementVisible(rejectButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForElementPresent(rejectionReasonTextareaLocator);
        keyClearAndType(rejectionReasonTextareaLocator, reason);
        if (ifConfirm) {
            commonWaits.waitForElementClickable(confirmRejectButtonLocator).click();
        } else {
            commonWaits.waitForElementClickable(cancelRejectionButtonLocator).click();
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public ServiceRequestPage clickOnHoldRequestInEventDetails() {
        log.info("Putting request on hold from event details");
        commonWaits.waitForElementVisible(onHoldButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

}