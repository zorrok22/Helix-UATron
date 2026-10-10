package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import framework.BasePage;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class TicketConsolePage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    // String locators
    String xpath_filter_header = "//div[contains(@class, 'tc__filter-toggle')]//div[contains(@class, 'dropdown-item') and translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = translate('%s', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')]";
    String xpath_filter_value = "/..//div[contains(@class, 'dropdown-item') and contains(text(), '%s')]";
    String xpath_text_filter_input = "/..//input";
    String xpath_needs_attention_option = "//div[contains(@class, 'tc__filter-toggle')]//div[contains(@class, 'dropdown-item__header') and text()='%s']" +
            "/..//*[self::label or self::span][normalize-space(.)='%s']";
    String xpath_view_page_field = "//*[@id='ar%s_data']";

    @FindBy(xpath = "//*[@ux-id='console-filter-btn']")
    WebElement filterIconButton;

    @FindBy(xpath = "//*[@ux-id='clear-filters-btn']")
    WebElement clearFiltersButton;

    @FindBy(xpath = "//*[@ux-id='console-apply-filter-btn']")
    WebElement applyFiltersButton;

    @FindBy(xpath = "//div[@aria-label='Approval List']")
    WebElement approvalBanner;
    
    @FindBy(xpath = "//div[contains(@class,'request-approvers')]//span")
    WebElement openApprovalBannerText;

    @FindBy(xpath = "//*[@ux-id='related-ticket-id']")
    WebElement fulfillmentIDLink;

    @FindBy(xpath = "//div[@ux-id='fullname']")
    WebElement ApprovalName;

    @FindBy(xpath = "//a[contains(@href,'workorder') and @ux-id='related-ticket-id']")
    WebElement WorkOrderIDLink;

    @FindBy(xpath = "//a[@ux-id='related-ticket-id']")
    WebElement relatedTicketIDLink;

    @FindBy(xpath = "//div[contains(@title,'Install projector in conference room')]//following-sibling::div")
    WebElement WorkOrderIDStatus;

    @FindBy(xpath = "//div[contains(@title,'ExternalActivity')]//following-sibling::div")
    WebElement ApprovalRecordStatus;

    @FindBy(xpath = "//*[starts-with(@title,'WO')]")
    WebElement WorkOrderIDFromViewPage;

    @FindBy(xpath = "//iframe[@id='studio-frame']")
    WebElement studioFrame;

    @FindBy(xpath = "//*[@role='link' and normalize-space()='Show approvers']")
    WebElement showApprovalsLink;

    @FindBy(xpath = "//iframe[@id='pwa-frame']")
    WebElement pwaFrame;

    @FindBy(xpath = "//div[contains(@class,'ngViewport')]/div/div[1]")
    WebElement firstTicket;

    @FindBy(xpath = "(//div[contains(@class,'ngViewport')]/div/div)[last()]")
    WebElement lastTicket;

    @FindBy(xpath = "//span[@aria-label='Work Order ID']")
    WebElement WorkOrderIDFromViewPageWhenPVDisabled;

    String customerName_ARID = "301395300";
    String workorderSummary_ARID = "1000000000";
    String workorderPriority_ARID = "1000000164";
    String workorderStatus_ARID = "7";

    public TicketConsolePage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(TicketConsolePage.class.getName());
        commonWaits = new CommonWaits();
    }

    public TicketConsolePage clearFilter() {
        log.info("Clicking on clear filter button.");
        if (isElementClickable(clearFiltersButton)) {
            commonWaits.clickWithoutInterception(clearFiltersButton);
        }
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public TicketConsolePage clickFilter() {
        log.info("Clicking on filter icon");
        clickByAction(filterIconButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        return this;
    }

    public TicketConsolePage applyFilter() {
        log.info("Clicking on Apply Filters");
        commonWaits.clickWithoutInterception(applyFiltersButton);
        commonWaits.waitForElementClickable(clearFiltersButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public TicketConsolePage applyFilterAndOpenTicket(String ticketType, String ticketID) {
        clearFilter();
        clickFilter();
        selectFilterValue(SolQAConstants.TICKET_TYPE_FILTER, ticketType);
        selectTextFilter(SolQAConstants.KEYWORDS_FILTER, ticketID);
        applyFilter();
        openFirstTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    public TicketConsolePage applyKeywordsFilterAndOpenFirstTicket(String ticketID) {
        log.info("Ticket Console: filter icon, Keywords search for id: " + ticketID);
        clearFilter();
        clickFilter();
        selectTextFilter(SolQAConstants.KEYWORDS_FILTER, ticketID);
        applyFilter();
        openFirstTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    public TicketConsolePage applyFilterAndOpenTicketWithNeedsAttentionAndId(String ticketType, String needsAttentionValue, String ticketID) {
        clearFilter();
        clickFilter();
        selectFilterValue(SolQAConstants.TICKET_TYPE_FILTER, ticketType);
        expandFilter(SolQAConstants.NEEDS_ATTENTION_FILTER);
        String needsAttentionCheckbox = String.format(xpath_needs_attention_option,
                SolQAConstants.NEEDS_ATTENTION_FILTER, needsAttentionValue);
        clickByAction(needsAttentionCheckbox);
        selectTextFilter(SolQAConstants.KEYWORDS_FILTER, ticketID);
        applyFilter();
        openFirstTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }

    public TicketConsolePage applyFilterAndOpenTicketWithNeedsAttention(String ticketType, String needsAttentionValue) {
        clearFilter();
        clickFilter();
        selectFilterValue(SolQAConstants.TICKET_TYPE_FILTER, ticketType);
        expandFilter(SolQAConstants.NEEDS_ATTENTION_FILTER);
        String needsAttentionCheckbox = String.format(xpath_needs_attention_option,
                SolQAConstants.NEEDS_ATTENTION_FILTER, needsAttentionValue);
        clickByAction(needsAttentionCheckbox);
        applyFilter();
        openLastTicket();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public TicketConsolePage selectFilterValue(String filterType, String value) {
        String filterValue = String.format(xpath_filter_header, filterType)
                + String.format(xpath_filter_value, value);
        if ("Published".equals(value)) {
            filterValue = String.format(xpath_filter_header, filterType)
                    + String.format(xpath_filter_value, value);
        }

        if ("Closed".equals(value)) {
            filterValue = String.format(xpath_filter_header, filterType)
                    + String.format(xpath_filter_value, value);
        }
        expandFilter(filterType);
        clickByAction(filterValue);
        return this;
    }

    public TicketConsolePage selectTextFilter(String filterType, String text) {
        String filterTextbox = String.format(xpath_filter_header, filterType) + xpath_text_filter_input;
        expandFilter(filterType);
        keyClearAndType(filterTextbox, text);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
        getElement("xpath=" + filterTextbox).sendKeys(Keys.ENTER);
        return this;
    }

    public TicketConsolePage expandFilter(String filterType) {
        log.info("Expanding filter: " + filterType);
        String filterHeader = String.format(xpath_filter_header, filterType);
        scrollToElement("xpath=" + filterHeader);
        if (getElement("xpath=" + filterHeader).getAttribute("class")
                .contains("collapsed")) {
            clickByAction(filterHeader);
        }
        return this;
    }


    public TicketConsolePage openFirstTicket() {
        log.info("Opening first ticket from ticket console");
        commonWaits.clickWithoutInterception(firstTicket);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public TicketConsolePage openLastTicket() {
        log.info("Opening last ticket from ticket console");
        commonWaits.clickWithoutInterception(lastTicket);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        return this;
    }

    public String getWOCustomerName() {
        commonWaits.waitForAngularRequestsToFinish();
        switchtoDefaultFrame();
        switchToFrame(pwaFrame);
        return getElement("xpath=" + String.format(xpath_view_page_field, customerName_ARID))
                .getAttribute("innerText");
    }

    public String getWOFieldValueByArid(String arid) {
        try {
            if (arid == null || arid.trim().isEmpty()) return "";
            commonWaits.waitForAngularRequestsToFinish();
            switchtoDefaultFrame();
            switchToFrame(pwaFrame);
            String fieldXpath = String.format(xpath_view_page_field, arid.trim());
            if (commonWaits.isElementPresent(fieldXpath)) {
                return getElement("xpath=" + fieldXpath).getAttribute("innerText");
            }
        } catch (Exception ignored) {}
        return "";
    }

    public String getWOSummary() {
        return getWOFieldValueByArid(workorderSummary_ARID);
    }

    public String getWOPriority() {
        return getWOFieldValueByArid(workorderPriority_ARID);
    }

    public String getWOStatus() {
        return getWOFieldValueByArid(workorderStatus_ARID);
    }

    public boolean verifyYellowApprovalBannerPresent() {
        log.info("Verifying yellow approval banner is present");
        switchtoDefaultFrame();
        commonWaits.waitForElementVisible(approvalBanner);
        return commonWaits.isElementPresent(approvalBanner);
    }

    public boolean verifyYellowApprovalBannerApprovalText(String Text) {
        log.info("Verifying yellow approval banner text");
        commonWaits.waitForElementVisible(openApprovalBannerText);
        return openApprovalBannerText.getText().equals(Text);
    }

    public void clickOnFulfillmentIDHyperLink() {
        log.info("Clicking on Fulfillment ID hyperlink: " );
        commonWaits.clickWithoutInterception(fulfillmentIDLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public boolean verifyApprovalIsAvailable(String approvalName) {
        log.info("Verifying approval is available");
       commonWaits.waitForElementVisible(ApprovalName);
       return ApprovalName.getText().equals(approvalName);
    }

    public String getWorkOrderIDFromFulfillmentSection() {
        log.info("Getting Work Order ID from Fulfillment section");
        commonWaits.switchtoDefaultFrame();
        refreshBrowser();
        if (!commonWaits.waitForElementVisible_withFluentWait(getDriver(), WorkOrderIDLink, 10)) {
            commonWaits.clickWithoutInterception(relatedTicketIDLink);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), WorkOrderIDLink, 20), "Work Order ID link is not visible after clicking related ticket ID link");
        }
        return WorkOrderIDLink.getText().trim();
    }

    public String getWorkOrderIDStatusFromFulfillmentSection() {
        log.info("Getting Work Order ID Status from Fulfillment section");
        commonWaits.waitForElementVisible(WorkOrderIDStatus);
        return WorkOrderIDStatus.getText();
    }

    public String getApprovalRecordStatusFromFulfillmentSection() {
        log.info("Getting Approval Record Status from Fulfillment section");
        commonWaits.waitForElementVisible(ApprovalRecordStatus);
        return ApprovalRecordStatus.getText();
    }

    public void clickOnWorkOrderIDHyperLink() {
        log.info("Clicking on Work Order ID hyperlink: " );
        commonWaits.clickWithoutInterception(WorkOrderIDLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public boolean verifyWorkorderIDFromSmartITViewPage(String workorderID) {
        log.info("Verifying Workorder ID from SmartIT View Page");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
        switchToFrame(pwaFrame);
        commonWaits.waitForElementVisible(WorkOrderIDFromViewPage);
        return WorkOrderIDFromViewPage.getAttribute(SolQAConstants.ATTRIBUTE_INNERTEXT).contains(workorderID);
    }

    public void clickOnBannerShowApprovers() {
        log.info("Clicking on Show Approvers from yellow banner");
        commonWaits.clickWithoutInterception(showApprovalsLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        log.info("Clicked on Show Approvers from yellow banner");
    }

    public String getFulfillmentID() {
        final int maxAttempts = 5;
        String fulfillmentID = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Attempting to fetch Fulfillment ID - Attempt " + attempt + " of " + maxAttempts);
                refreshBrowser();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
                commonWaits.waitForAllSpinnersToHide("DOTS_LOADER");
                if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), fulfillmentIDLink, 10)) {
                    commonWaits.waitForElementVisible(fulfillmentIDLink);
                    fulfillmentID = fulfillmentIDLink.getText();
                    log.info("Successfully fetched Fulfillment ID: " + fulfillmentID);
                    return fulfillmentID;
                }
            } catch (Exception e) {
                log.warn("Attempt " + attempt + " failed to fetch Fulfillment ID: " + e.getMessage());
                if (attempt < maxAttempts) {
                    log.info("Retrying...");
                    commonWaits.waitForSometime(2000); // Wait 2 seconds before retry
                }
            }
        }
        Assert.fail("Fulfillment ID link is not visible after " + maxAttempts + " attempts");
        return fulfillmentID;
    }

    public String getWorkOrderID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(WorkOrderIDLink);
        return WorkOrderIDLink.getText();
    }

    public boolean verifyWorkorderIDFromSmartITViewPageWhenPVDisabled(String workorderID) {
        log.info("Verifying Workorder ID from SmartIT View Page");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchtoDefaultFrame();
        commonWaits.waitForElementVisible(WorkOrderIDFromViewPageWhenPVDisabled);
        return WorkOrderIDFromViewPageWhenPVDisabled.getText().contains(workorderID);
    }

}
