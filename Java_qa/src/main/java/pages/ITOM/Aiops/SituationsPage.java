package pages.ITOM.Aiops;

import framework.BasePage;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import util.CommonWaits;
import util.SolqaLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import util.ReadJson;

public class SituationsPage extends BasePage {


    WebDriver driver;
    static SolqaLogger log = new SolqaLogger(SituationsPage.class.getName());
    CommonWaits commonWaits = new CommonWaits();
    ReadJson readJson;

    @FindBy(xpath = "//button[@data-testid='situationTypeGroupButton-0']")
    WebElement allSituationsButton;

    @FindBy(xpath = "//button[contains(@id,'actionDropdownMenu')]")
    WebElement actionDropdownMenu;

    @FindBy(xpath = "//div[@id='event-panel-box']//button[contains(@id,'actionDropdownMenu')]")
    WebElement eventActionDropdownMenu;

    String optionsLocators = "//span[contains(text(),'%s')]";

    @FindBy(xpath = "//button[@id='toggle-list-button']")
    WebElement listView;

    @FindBy(xpath = "//input[@placeholder='Search by situation and service names']")
    WebElement searchTextField;

    @FindBy(xpath = "//button[@data-testid='global-search_button']")
    WebElement searchButton;

    @FindBy(xpath = "(//*[contains(@id, 'msg-link')])[1]")
    WebElement firstSituation;

    @FindBy(xpath = "//*[@id='situation-detail-create-incident' or @id='situation-detail-no-incident' or @id='situation-detail-incident']")
    WebElement incidentLocator;

    @FindBy(xpath = "//button[@data-testid='situation-explanation-view-type-switcher-1']")
    WebElement eventTab;

    @FindBy(xpath = "//span[contains(@class , 'alert-link text-active')]/span")
    List<WebElement> eventMessages;

    @FindBy(xpath = "//adapt-button[@id='collapsible-section-toggle-button']/span/span")
    WebElement showRecommendationPlanner;

    @FindBy(xpath = "//*[@id='root-cause-summary']")
    WebElement rootCauseSummary;

    @FindBy(xpath = "//button[@data-id='step-next-button']")
    WebElement bapNextButton;

    @FindBy(xpath = "//*[@data-id='action-execute-button']")
    WebElement executeChangeToolButton;

    @FindBy(xpath = "//button[@data-testid='situationTypeGroupButton-1']")
    WebElement similarSituation;

    @FindBy(xpath = "//button[@data-testid='adapt_accordion_tab_id_4']")
    WebElement askHelixGPT;

    @FindBy(xpath = "//table//tbody/tr[td[count(//th[@data-testid='adapt-table-1-th-incident_id']/preceding-sibling::th)+1][normalize-space() != '-']][1]/td[1]")
    WebElement firstSituationWithIncHyperlink;

    @FindBy(xpath = "//button[contains(text(), 'Show more')]")
    WebElement showMoreButton;
    @FindBy(xpath = "//section[@data-testid='helixGPTChatMessage-0__contentSection']")
    WebElement hgptChatContentSection;
    @FindBy(xpath = "//adapt-speech-bubble[@data-testid='conversationSpeechBubble-2']")
    WebElement hgptChatResponse;

    @FindBy(xpath = "//button[contains(. ,'Events')]")
    WebElement eventsTab;
  
    @FindBy(xpath = "//div[@id='situation-detail-incident']//span")
    WebElement incidentID;
    
    String bapChangeTool = "//span[.='Change tool']";
    String bapChangeToolExecutionComplete = "//span[contains(text(),'Completed')]";
    String bapChangeToolExpectedText = "//span[contains(text(),'expectedChangeToolText')]";
    String situationGlobalSearchButton = "//button[@data-testid='global-search_button']";
    String situationDetailHyperlink = "//span[contains(text(), 'situationName')]";
    String situationDetailSeverity = "//span[@id='situation-detail-severity']";

    String bar = "//adapt-button[@id='collapsible-section-toggle-button']/span/span";
    String logInsights = "//button[@aria-controls='helixgpt-log-insights']";
    String run5Whys = "//button[contains(text(), 'Run 5Whys')]";
    String reoccurring = "//button[contains(text(), 'Reoccurring')]";
    String changeWindows = "//button[contains(text(), 'Change windows')]";

    String spanContainsText = "//span[normalize-space(.)='TEXT']";
    String divContainsText = "//div[normalize-space(.)='TEXT']";

    String incidentLink = "//tr[.//span[contains(text(), 'SITUATION')]]//button[text()='INCIDENT']";

    @FindBy(xpath = "//th[contains(@data-testid, 'th-deep_rca')]")
    WebElement deepRCAColumnHeader;

    String deepRCACompletedIcon = "//span[@id='%s-deep-rca-completed']";
    String deepRCAPendingIcon = "//span[@id='%s-deep-rca-pending']";
    String deepRCAInProgressIcon = "//span[@id='%s-deep-rca-running']";
    String deepRCANAIcon = "//span[@id='%s-deep-rca-na']";

    String deepRCAStatusIconsExcludingSituation = "//span[contains(@id, '-deep-rca-') and not(contains(@id, '%s'))]";

    @FindBy(xpath = "//div[contains(@class, 'deep-rca-sidebar')]")
    WebElement deepRCASidebar;

    @FindBy(xpath = "//span[contains(@class, 'title-text') and contains(text(), 'Deep RCA')]")
    WebElement deepRCASidebarTitle;

    String deepRCAProgressStatusText = "//div[contains(@class, 'deep-rca-sidebar')]//*[contains(text(), '%s')]";

    public SituationsPage() {
        PageFactory.initElements(getDriver(), this);
    }

    public void verifySituationsPageOpened() {
        commonWaits.waitForElementClickable(allSituationsButton);
        Assert.assertTrue(commonWaits.isElementPresent(allSituationsButton), "Situation page not opened");
    }

    public void verifySituationOptions() {
        SoftAssert softAssert = new SoftAssert();
        List<String> situationOptionList = Arrays.asList("Acknowledge Situation", "Assign Situation...", "Close Situation", "Decline Ownership", "Set Situation Priority...", "Take Ownership", "Unacknowledge Situation", "Add Notes");
        commonWaits.waitForElementVisible(listView);
        commonWaits.waitForElementClickable(listView).click();
        commonWaits.waitForElementClickable(actionDropdownMenu).click();
        for (String item : situationOptionList) {
            softAssert.assertTrue(commonWaits.isElementPresent(String.format(optionsLocators, item)), "Option is not available: " + item);
        }
        softAssert.assertAll();
    }

    @FindBy(xpath = "//button[@id='timeRangeFilter']")
    WebElement timeRangeFilter;
    @FindBy(xpath = "//*[contains(@class, 'time-range-item') and @id='Last 60 minutes']")
    WebElement last60MinOption;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Advanced filter')]")
    WebElement advancedFilterButton;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Clear')]")
    WebElement clearFilterButton;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Apply filters')]")
    WebElement applyFilterButton;

    public void searchAndOpenSituation(String situationName) {
        commonWaits.waitForElementVisible(searchTextField);
        commonWaits.sendEscKey();
        commonWaits.waitForElementClickable(listView).click();
        commonWaits.waitForElementVisible(timeRangeFilter).click();
        commonWaits.waitForElementVisible(last60MinOption).click();
        commonWaits.waitForElementVisible(advancedFilterButton).click();
        commonWaits.waitForElementVisible(clearFilterButton).click();
        commonWaits.waitForElementVisible(applyFilterButton).click();
        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(),applyFilterButton, 5), "Apply filter button is still visible after clicking, page might not be loaded");
        commonWaits.waitForElementClickable(searchTextField).sendKeys(situationName);
        WebElement button = getDriver().findElement(By.xpath(situationGlobalSearchButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", button);
        commonWaits.waitForPageLoad(getDriver());
        WebElement link = getDriver().findElement(By.xpath(situationDetailHyperlink.replace("situationName", situationName)));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", link);
        commonWaits.waitForElementPresent(situationDetailSeverity);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), findElement(By.xpath(situationDetailSeverity)), commonWaits.defaultTimeout), "Situation not opened");
    }

    public void searchSituationAndOpenIncidentAssociated(String situationName, String incidentId) {
        commonWaits.waitForElementVisible(searchTextField);
        commonWaits.waitForElementClickable(listView).click();
        commonWaits.waitForElementClickable(searchTextField).sendKeys(situationName);
        WebElement button = getDriver().findElement(By.xpath(situationGlobalSearchButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", button);
        commonWaits.waitForPageLoad(getDriver());
        WebElement link = getDriver().findElement(By.xpath(incidentLink.replace("SITUATION", situationName).replace("INCIDENT", incidentId)));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", link);
    }
    
    public AIOpsPage openIncidentAssociated() {
        commonWaits.waitForElementClickable(incidentID).click();
        return new AIOpsPage();
    }

    public void openFirstSimilarSituationWithIncident() {
        commonWaits.waitForElementClickable(similarSituation);
        commonWaits.clickToElement(similarSituation);
        commonWaits.waitForElementClickable(firstSituationWithIncHyperlink);
        commonWaits.click(firstSituationWithIncHyperlink);
    }

    public void verifyEventOptionsOnEditSituation() {
        SoftAssert softAssert = new SoftAssert();
        List<String> eventOptionList= Arrays.asList("Acknowledge Event", "Assign Event...", "Close Event", "Decline Ownership", "Set Event Priority...", "Take Ownership", "Unacknowledge Event", "Add Notes");
        commonWaits.waitForElementVisible(incidentLocator);
        commonWaits.waitForElementVisible(eventsTab).click();
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), eventActionDropdownMenu, 10);
        commonWaits.waitForElementClickable(eventActionDropdownMenu);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.clickWithoutInterception(eventActionDropdownMenu);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        for (String item : eventOptionList) {
            softAssert.assertTrue(commonWaits.isElementPresent(String.format(optionsLocators, item)), "Situation event option is not available: " + item);
        }
        softAssert.assertAll();
    }

    public void clickOnEventTabInSituation() {
        log.info("clicking on event tab in situation");
        commonWaits.waitForElementClickable(eventTab);
        clickToElement(eventTab);
    }

    public List<String> getEventMessagesFromSituation() {
        log.info("Get List of Events Name from situation");
        List<String> itemList = new ArrayList<>();
        commonWaits.waitForElementClickable(eventMessages.get(0));
        for (WebElement eventMessage : eventMessages) {
            itemList.add(eventMessage.getText());
        }
        return itemList;
    }

    public void verifyChangeBAP(String expectedRootCauseSubText, String expectedChangeToolText) {
        Assert.assertTrue(commonWaits.isElementPresent(showRecommendationPlanner), "Show recommendation planner is not present");
        commonWaits.click(showRecommendationPlanner);
        WebElement rootCauseMessage = commonWaits.waitForElementVisible(rootCauseSummary);
        log.info("RootCause Summary: " + rootCauseMessage.getText());
        Assert.assertFalse(rootCauseMessage.getText().contains(expectedRootCauseSubText), "Root Cause Summary is not displayed");
        boolean flag = true;
        int iterationCount = 5;
        int count = 0;
        do {
            try {
                count += 1;
                if (commonWaits.waitForElementPresent(By.xpath(bapChangeTool))) {
                    log.info("Change tool label is present.");
                    flag = false;
                } else {
                    log.info("Change tool label is not present, retrying...");
                    commonWaits.click(bapNextButton);
                }

            } catch (Exception e) {
                log.error("Exception occurred while clicking Next button: " + e.getMessage());
            }
        } while (flag && count < iterationCount);

        commonWaits.isElementPresent(executeChangeToolButton);
        commonWaits.click(executeChangeToolButton);
        ;
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), bapChangeToolExecutionComplete, commonWaits.defaultTimeout);
        commonWaits.waitForByElementToBeVisible(By.xpath(bapChangeToolExpectedText.replace("expectedChangeToolText", expectedChangeToolText)));
    }


    public void verifyHelixGptMenus() {
        readJson = new ReadJson();
        JSONObject chatMsgObject = readJson.readSimpleJson("resources/ITOM/AIOPS/aiopsBapGenericChatMsg.json");
        SoftAssert softAssert = new SoftAssert();
        commonWaits.waitForElementVisible(askHelixGPT);
        commonWaits.click(askHelixGPT);
        commonWaits.waitForElementVisible(hgptChatContentSection);
        commonWaits.getText(hgptChatContentSection, chatMsgObject.get("welcome_msg").toString());
        softAssert.assertTrue(commonWaits.isElementPresent(By.xpath(bar)), "Best Action Recommendations is not present");
        softAssert.assertTrue(commonWaits.isElementPresent(By.xpath(logInsights)), "Log Insights is not present");
        softAssert.assertTrue(commonWaits.isElementPresent(By.xpath(run5Whys)), "Run 5 Whys is not present");
        softAssert.assertTrue(commonWaits.isElementPresent(By.xpath(reoccurring)), "Reoccurring is not present");
        softAssert.assertTrue(commonWaits.isElementPresent(By.xpath(changeWindows)), "Change windows is not present");
        softAssert.assertTrue(commonWaits.isElementPresent(showMoreButton), "Show More Options is not present");
        softAssert.assertAll();
    }

    public void verify5Whys() {
        JSONObject chatMsgObject = readJson.readSimpleJson("resources/ITOM/AIOPS/aiopsBapGenericChatMsg.json");
        commonWaits.isElementPresent(By.xpath(run5Whys));
        commonWaits.click(run5Whys);
        commonWaits.waitForElementVisible(hgptChatResponse);
        commonWaits.waitForSometime(10000);
        boolean flag = true;
        int iterationCount = 6;
        int count = 0;

        do {
            try {
                count += 1;
                String message = commonWaits.getText(hgptChatResponse);
                if (message.contains(chatMsgObject.get("request_ack_msg").toString())) {
                    log.info("Current response is: " + message);
                    log.info("5Whys still running.");
                    commonWaits.waitForSometime(10000);
                } else {
                    log.info("Current response is: " + message);
                    log.info("5Whys finished execution.");
                    flag = false;
                }

            } catch (Exception e) {
                log.error("Exception occurred while clicking Next button: " + e.getMessage());
            }
        } while (flag && count < iterationCount);

        if (flag) {
            log.info("5 Whys analysis did not complete successfully after " + iterationCount + " attempts.");
            Assert.fail("5 Whys analysis did not complete successfully.");
        } else {
            String fiveWhys_output = commonWaits.getText(hgptChatResponse);
            if (fiveWhys_output.contains(chatMsgObject.get("info_not_found_msg").toString())) {
                log.info("Something wrong with 5 Whys.");
                Assert.fail("5 Whys analysis did not complete successfully.");
            } else {
                log.info("5 Whys analysis completed successfully.");
            }
        }
    }

    public void verifyMultiServiceSituation(List<String> serviceNames, List<String> eventMessages) {
        int serviceCount = serviceNames.size();
        int eventCount = eventMessages.size();
        SoftAssert softAssert = new SoftAssert();
        commonWaits.waitForElementClickable(findElement(By.xpath(spanContainsText.replace("TEXT", "+" + String.valueOf(serviceCount - 1)))));
        commonWaits.click(By.xpath(spanContainsText.replace("TEXT", "+" + String.valueOf(serviceCount - 1))));
        for (int i = 0; i < serviceCount; i++) {
            String serviceName = serviceNames.get(i);
            softAssert.assertTrue(commonWaits.waitForElementPresent(By.xpath(spanContainsText.replace("TEXT", serviceName))), "Service name not found: " + serviceName);
        }
        commonWaits.waitForElementPresent(By.xpath(divContainsText.replace("TEXT", "Events")));
        commonWaits.click(By.xpath(divContainsText.replace("TEXT", "Events")));
        for (int i = 0; i < eventCount; i++) {
            String eventMessage = eventMessages.get(i);
            softAssert.assertTrue(commonWaits.waitForElementPresent(By.xpath(spanContainsText.replace("TEXT", eventMessage))), " Event message not found: " + eventMessage);
        }
        softAssert.assertAll();
    }

    public SituationsPage switchToListViewAndSearchSituation(String situationName) {
        commonWaits.waitForElementVisible(searchTextField);
        commonWaits.sendEscKey();
        commonWaits.waitForElementClickable(listView).click();
        commonWaits.waitForElementVisible(timeRangeFilter).click();
        commonWaits.waitForElementVisible(last60MinOption).click();
        commonWaits.waitForElementVisible(advancedFilterButton).click();
        commonWaits.waitForElementVisible(clearFilterButton).click();
        commonWaits.waitForElementVisible(applyFilterButton).click();
        Assert.assertTrue(commonWaits.waitForElementInvisible_withFluentWait(getDriver(), applyFilterButton, 5), "Apply filter button is still visible after clicking, page might not be loaded");
        commonWaits.waitForElementClickable(searchTextField).sendKeys(situationName);
        clickWithoutInterception(By.xpath(situationGlobalSearchButton));
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public SituationsPage searchSituationOnPage(String situationName) {
        commonWaits.waitForElementClickable(searchTextField);
        keyClearAndType(searchTextField, situationName);
        clickWithoutInterception(By.xpath(situationGlobalSearchButton));
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public SituationsPage verifyDeepRCAColumnPresent() {
        commonWaits.waitForElementVisible(deepRCAColumnHeader);
        Assert.assertTrue(commonWaits.isElementPresent(deepRCAColumnHeader), "Deep RCA column header is not present on Situations page");
        return this;
    }

    public String getDeepRCAStatusForSituation(String situationId) {
        if (commonWaits.isElementPresent(By.xpath(String.format(deepRCACompletedIcon, situationId)))) {
            return "completed";
        } else if (commonWaits.isElementPresent(By.xpath(String.format(deepRCAInProgressIcon, situationId)))) {
            return "in-progress";
        } else if (commonWaits.isElementPresent(By.xpath(String.format(deepRCAPendingIcon, situationId)))) {
            return "pending";
        } else if (commonWaits.isElementPresent(By.xpath(String.format(deepRCANAIcon, situationId)))) {
            return "na";
        }
        return "unknown";
    }

    public SituationsPage waitForDeepRCAStatusForSituation(String situationId, String... expectedStatuses) {
        log.info("Waiting for Deep RCA status [" + String.join(", ", expectedStatuses) + "] for situation: " + situationId);
        StringBuilder xpathBuilder = new StringBuilder();
        for (int i = 0; i < expectedStatuses.length; i++) {
            String iconXpath;
            switch (expectedStatuses[i].toLowerCase()) {
                case "completed": iconXpath = deepRCACompletedIcon; break;
                case "in-progress": iconXpath = deepRCAInProgressIcon; break;
                case "pending": iconXpath = deepRCAPendingIcon; break;
                case "na": iconXpath = deepRCANAIcon; break;
                default: throw new IllegalArgumentException("Unknown Deep RCA status: " + expectedStatuses[i]);
            }
            if (i > 0) xpathBuilder.append(" | ");
            xpathBuilder.append(String.format(iconXpath, situationId));
        }
        By statusLocator = By.xpath(xpathBuilder.toString());
        long startTime = System.currentTimeMillis();
        long timeout = commonWaits.defaultTimeout * 2 * 1000L;
        boolean found = false;

        while (!found && (System.currentTimeMillis() - startTime) < timeout) {
            found = commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusLocator, 10);
            if (!found) {
                log.info("Status not found yet, refreshing page...");
                refreshBrowser();
            }
        }

        Assert.assertTrue(found,
            "Deep RCA status did not change to [" + String.join(", ", expectedStatuses) + "] within timeout for situation: " + situationId
        );
        log.info("Deep RCA status matched one of: [" + String.join(", ", expectedStatuses) + "]");
        return this;
    }

    public SituationsPage verifyDeepRCACompletedInPanel(String expectedStatusText) {
        log.info("Verifying Deep RCA completed status in panel: " + expectedStatusText);
        Assert.assertTrue(
            commonWaits.waitForElementVisible_withFluentWait(getDriver(), deepRCASidebar, commonWaits.defaultTimeout),
            "Deep RCA sidebar is not displayed on situation detail page"
        );
        By statusLocator = By.xpath(String.format(deepRCAProgressStatusText, expectedStatusText));
        Assert.assertTrue(
            commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusLocator, commonWaits.defaultTimeout),
            "Deep RCA status '" + expectedStatusText + "' is not shown in the Deep RCA panel"
        );
        return this;
    }

    public SituationsPage verifyDeepRCAStatusShownForOldSituations(String currentSituationId) {
        log.info("Verifying Deep RCA status is shown for old situations excluding: " + currentSituationId);
        By oldSituationStatusLocator = By.xpath(String.format(deepRCAStatusIconsExcludingSituation, currentSituationId));
        Assert.assertTrue(
            commonWaits.waitForElementVisible_withFluentWait(getDriver(), oldSituationStatusLocator, commonWaits.defaultTimeout),
            "Deep RCA status is not shown for old situations"
        );
        List<WebElement> oldSituationStatusIcons = getDriver().findElements(oldSituationStatusLocator);
        log.info("Found " + oldSituationStatusIcons.size() + " Deep RCA status icon(s) for old situations");
        return this;
    }

    public SituationsPage openSituationFromHierarchyView(String situationName) {
        clickWithoutInterception(By.xpath(situationDetailHyperlink.replace("situationName", situationName)));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(situationDetailSeverity), commonWaits.defaultTimeout), "Situation detail page not opened");
        return this;
    }

    public SituationsPage verifyDeepRCATriggeredAutomatically() {
        Assert.assertTrue(
            commonWaits.waitForElementVisible_withFluentWait(getDriver(), deepRCASidebar, commonWaits.defaultTimeout),
            "Deep RCA sidebar is not triggered automatically on situation detail page"
        );
        Assert.assertTrue(commonWaits.isElementPresent(deepRCASidebarTitle), "Deep RCA sidebar title 'Deep RCA' is not displayed");
        return this;
    }

    public SituationsPage verifyDeepRCAProgressStatusFlow(String[] expectedStatuses) {
        log.info("Verifying Deep RCA progress status flow: " + String.join(" -> ", expectedStatuses));
        int currentStatusIndex = 0;
        long startTime = System.currentTimeMillis();
        long timeout = commonWaits.defaultTimeout * 1000L;

        while (currentStatusIndex < expectedStatuses.length && (System.currentTimeMillis() - startTime) < timeout) {
            for (int i = currentStatusIndex; i < expectedStatuses.length; i++) {
                By statusLocator = By.xpath(String.format(deepRCAProgressStatusText, expectedStatuses[i]));
                if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusLocator, 2)) {
                    log.info("Deep RCA progress status detected: " + expectedStatuses[i]);
                    currentStatusIndex = i + 1;
                    break;
                }
            }
        }

        Assert.assertTrue(currentStatusIndex >= expectedStatuses.length,
            "Deep RCA progress did not reach final status '" + expectedStatuses[expectedStatuses.length - 1]
                + "'. Last detected index: " + (currentStatusIndex - 1));
        return this;
    }

    public void verifyBAPWithRecommendations(String expectedRecommendation) {
        commonWaits.waitForElementVisible(showRecommendationPlanner);
        commonWaits.click(showRecommendationPlanner);
        Assert.assertFalse(commonWaits.isElementPresent(By.xpath("//div[contains(text(),'No recommendations available')]")), "No recommendations message is not displayed");

        Boolean flag = true;
        int iterationCount = 5;
        int count = 0;
        do {
            try {
                count += 1;
                if (commonWaits.waitForElementPresent(By.xpath(spanContainsText.replace("TEXT", "Code tool")))) {
                    log.info("Code tool label is present.");
                    flag = false;
                } else {
                    log.info("Code tool label is not present, retrying...");
                    commonWaits.click(bapNextButton);
                }

            } catch (Exception e) {
                log.error("Exception occurred while clicking Next button: " + e.getMessage());
            }
        } while (flag && count < iterationCount);

        commonWaits.elementTextContains(findElement(By.xpath("//span[@class='action-section__title']//..//span[2]")), expectedRecommendation);
    }
}
