package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartITDashboardPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    String create_requested_flow_xpath_template = "//*[contains(@href,'#/create/%s')]";
    String workspaces_dropdown_item_xpath_template = "//*[@ux-id='navitem-element' and contains(. ,'%s')]";
    String workspace_element_link_xpath_template = "//*[@ux-id='navitem-element-link_%s']";
    By LIVE_CHAT_MENU_BY = By.xpath("//*[@ux-id='navitem-more_liveChat']");
    By LIVE_CHAT_IFRAME_BY = By.xpath("//iframe[@id='liveChatFrame' or contains(@class,'ux-live-chat-iframe')]");
    String xpath_global_search_icon = "//*[@ux-id='navigation-item_console']//a[@ux-id='navitem-dropdown']";
    String xpath_more_dropdown = "//a[@ux-id='more-dropdown' or normalize-space()='More']";
    String xpath_header_more_menu = "//div[@ux-id='header-navigation']//a[contains(text(),'More')]";
    String xpath_more_button = "//a[@ux-id='more-dropdown']";
    public String itsmPage = "//a[contains(@href,'/create/smart-recorder')]";
    public String ticketConsoleItsmPage = "//h3[text()='Ticket Console']";
    public String createRequestedFlow = "//*[@href='#/create/%s']";
    public String workspacesDropdownItem = "//*[@ux-id='navitem-element' and contains(. ,'%s')]";
    public String smartRecorder = "//*[@href='#/create/smart-recorder' or text()='%s']";
    public String workSpaceElementLink = "//*[@ux-id='navitem-element-link_%s']";
    String pwaIframe = "//iframe[@id='pwa-frame']";
    String studioIframe = "//iframe[@id='studio-frame']";
    String globalSearchResult = "//*[contains(@class, 'rx-ellipsis') and text()='%s']";
    String globalSearchResultUsingMessage = "//div[contains(@class, 'search-result-card__header__text__title') and contains(., '%s')]";
    String viewFullTicketFrame = "//iframe[@id='cc-iframe-component']";
    String workspaceDropdownItem = "//*[@role='menuitem']//*[text()='%s']";
    String tableColumnHeader = "//div[contains(@class,'header__title')]//span[normalize-space()='%s']";

    String[] xpath_live_chat_menu = new String[]{
            "//*[@ux-id='navitem-more_liveChat']",
            "//*[@ux-id='navitem-more_live-chat']",
            "//*[contains(@ux-id,'navitem-more') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'live chat')]",
            "//*[contains(@ux-id,'navitem-more') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'livechat')]"
    };

    String[] xpath_live_chat_agent_console_link = new String[]{
            "//*[@ux-id='navitem-element-link_liveChatAgentConsole']",
            "//*[@ux-id='navitem-element_liveChatAgentConsole']//a[@ux-id='navitem-link']",
            "//*[self::a or self::button][contains(@href,'liveChat') or contains(@href,'livechat') or contains(@href,'live-chat')]",
            "//*[self::a or self::button][contains(@routerlink,'live') or contains(@ng-reflect-router-link,'live')]",
            "//li//span[contains(text(),'Live Chat') or (contains(text(),'Agent') and contains(text(),'Console'))][1]"
    };
    @FindBy(xpath = "//*[@id='header-search_button']")
    WebElement globalSearchButton;

    @FindBy(xpath = "//*[contains(@ux-id, 'workspaces') and contains(., 'Workspaces')]")
    WebElement WorkspaceButton;

    @FindBy(xpath = "//*[@href='#/create/broadcast']")
    WebElement broadcastMenu;

    @FindBy(xpath = "//*[@ux-id='navitem-more_reports']//a[@ux-id='navitem-link']")
    WebElement reportsMenu;

    @FindBy(xpath = "//div[@ux-id='header-navigation']//a[contains(text(),'More')]")
    WebElement moreMenu;

    @FindBy(xpath = "//*[@ux-id='navigation-item_console']//a[@ux-id='navitem-dropdown']")
    WebElement navigateToConsole;

    @FindBy(xpath = "//*[@ux-id='navitem-element-link_ticketConsole']")
    WebElement navigateToTicketConsole;

    @FindBy(xpath = "//*[@ux-id='navitem-element-link_assetConsole']")
    WebElement navigateToAssetConsole;

    @FindBy(xpath = "//button[contains(@class,'close-btn')]//span")
    WebElement consoleFilter;

    @FindBy(xpath = "//h3[text()='Ticket Console']")
    WebElement isUserNavigateToTicketConsole;

    @FindBy(xpath = "//a[@ux-id='more-dropdown']")
    WebElement MoreButton;

    @FindBy(xpath = "//*[@href='#/create/smart-recorder' and text()='Smart Recorder']")
    WebElement SmartRecorder;

    @FindBy(xpath = "//*[@ux-id='navitem-element-link_knowledge']")
    WebElement knowledgeButton;

    @FindBy(xpath = "//*[@id='header-helixGPT_search_button']")
    WebElement helixGPTSearchIcon;

    @FindBy(xpath = "//*[@aria-haspopup='listbox']")
    WebElement ticketAndKnowledgeDropdown;

    @FindBy(xpath = "//*[text()='Tickets']")
    WebElement ticketsItsmPage;

    String DropdownOption = "//button[.//adapt-highlight[text()='%s']]";

    @FindBy(xpath = "//a[contains(@class,'gear')]/parent::adapt-nav-action-item")
    WebElement impactAnalysisSettings;
    @FindBy(xpath = "//a[@aria-label='Search' and @href='#/globalSearchStudio']")
    WebElement gptGlobalSearch;
    @FindBy(xpath = "//input[@id='globalSearchBox'] | //*[@rx-view-component-id='10544561-e6d5-4016-a6cf-c24b3c89fa77']//input")
    WebElement globalSearchInput;
    @FindBy(xpath = "//a[contains(normalize-space(.), 'View full incident') or contains(normalize-space(.), 'View full ticket')]")
    WebElement viewFullTicketLink;
    @FindBy(xpath = "//*[@rx-view-component-id='c6a70440-945a-4694-91e8-f7301476e10d']//button[contains(. ,'Search')]")
    WebElement gptSearchButton;
    @FindBy(xpath = "//*[@rx-view-component-id='c6a70440-945a-4694-91e8-f7301476e10d']//button[contains(. ,'Chat')]")
    WebElement gptChatButton;
    @FindBy(xpath = "//a[@ux-id='global-search-icon']")
    WebElement globalSearchIcon;
    @FindBy(xpath = "//a[@ux-id='more-dropdown' or normalize-space()='More']")
    WebElement moreDropdown;
    @FindBy(xpath = "//*[contains(@ux-id, 'create') and contains(., 'Create New')]")
    WebElement createNewDropdown;
    @FindBy(xpath = "//*[contains(@ux-id, 'create') and contains(., 'Create New')]//i[contains(@class,'icon-angle_down')]")
    WebElement createNewDropdownAngleDown;

    @FindBy(xpath = "//a[contains(@href,'create/change') and contains(.,'Change Request')]")
    WebElement createNewChangeRequestLink;

    @FindBy(xpath = "//div[@ux-id='navigation-item_liveChat' or @ux-id='navigation-item-more']")
    WebElement livechatChooser;
    @FindBy(xpath = "//div[@ux-id='navitem-dropdown']//div[text()='Live Chat']")
    WebElement liveChatChooser2;
    @FindBy(xpath = "//li//span[contains(text(),'Live Chat') or (contains(text(),'Agent') and contains(text(),'Console'))][1]")
    WebElement liveChatLink;
    @FindBy(xpath = "//iframe[@id='liveChatFrame']")
    WebElement iFrameLiveChat;

    @FindBy(xpath = "//span[normalize-space()='List view']/ancestor::button[@role='tab']")
    WebElement listViewButton;
    
    @FindBy(xpath = "//td[@data-testid='adapt-table_column-0_row-0']/parent::tr")
    WebElement tableRow;

    @FindBy(xpath = "(//button[contains(@id,'PBI')])[1]")
    WebElement pbiButton;

    @FindBy(xpath = "//h3[normalize-space(.)='Ticket Console']")
    WebElement ticketConsoleHeading;

    //Locators variables
    String smartRecorderButton = "smart-Recorder";
    String contractButton = "contractPV";
    String realtimeIncidentCorrelation = "realtimeIncidentCorrelation";
    String changeButton = "changePV";
    String workorderButton = "workorderPV";
    String incidentButton = "incidentPV";
    String assetButton = "assetPV";
    String knownErrorButton = "knownerror";
    String broadcastButton = "broadcast";

    public SmartITDashboardPage(WebDriver driver) {
        log = new SolqaLogger(SmartITDashboardPage.class.getName());
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public SmartITDashboardPage() {
        log = new SolqaLogger(SmartITDashboardPage.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public boolean clickFirstPresent(String[] xpaths) {
        for (String xp : xpaths) {
            try {
                java.util.List<WebElement> els = findElements(By.xpath(xp));
                if (els != null && !els.isEmpty()) {
                    commonWaits.clickWithoutInterception(els.get(0));
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public boolean openHeaderMoreMenuIfPresent() {
        try {
            java.util.List<WebElement> els = findElements(By.xpath(xpath_header_more_menu));
            if (els != null && !els.isEmpty()) {
                commonWaits.clickWithoutInterception(els.get(0));
                commonWaits.waitForExecutionComplete();
                log.info("[NAV_LIVECHAT_AGENT_CONSOLE_V3] Opened header More menu");
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean openMoreDropdownIfPresent() {
        try {
            java.util.List<WebElement> els = findElements(By.xpath(xpath_more_dropdown));
            if (els != null && !els.isEmpty()) {
                commonWaits.clickWithoutInterception(els.get(0));
                commonWaits.waitForExecutionComplete();
                log.info("[NAV_LIVECHAT_AGENT_CONSOLE_V3] Opened ux-id more-dropdown");
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean openMoreButtonIfPresent() {
        try {
            java.util.List<WebElement> els = findElements(By.xpath(xpath_more_button));
            if (els != null && !els.isEmpty()) {
                commonWaits.clickWithoutInterception(els.get(0));
                commonWaits.waitForExecutionComplete();
                log.info("[NAV_LIVECHAT_AGENT_CONSOLE_V3] Opened MoreButton");
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean openAnyMoreMenu() {
        return openHeaderMoreMenuIfPresent() || openMoreDropdownIfPresent() || openMoreButtonIfPresent();
    }

    public boolean tryNavigateToLiveChatAgentConsoleByUrl() {
        try {
            String current = getDriver().getCurrentUrl();
            if (current == null || current.trim().isEmpty()) return false;
            String base = current.split("#")[0];
            String[] candidates = new String[]{
                    "#/live-chat/agent-console",
                    "#/livechat/agent-console",
                    "#/live-chat-agent-console",
                    "#/liveChatAgentConsole",
                    "#/livechat-agent-console",
                    "#/live-chat"
            };

            for (String hash : candidates) {
                try {
                    getDriver().navigate().to(base + hash);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
                    if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), LIVE_CHAT_IFRAME_BY, 8)) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @FindBy(xpath = "//rx-rich-text[normalize-space()='Tickets']")
    WebElement gptGlobalSearchOnSTCMode;
    public SmartITDashboardPage verifySmartITPageLaunchedSuccessfully() {
        log.info("Verifying SmartIT application is launched successfully");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForAllSpinnersToHide("HELIX_SMARTIT");
        if (commonWaits.isElementPresent(By.xpath(studioIframe), 40)) {
            commonWaits.switchToFrame(By.xpath(studioIframe));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), gptGlobalSearchOnSTCMode, 50), "SmartIT application login failed in STC mode");
            commonWaits.switchtoDefaultFrame();        }
        else {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), navigateToConsole, 20), "SmartIT application login failed in regular mode");
        }
        return this;
    }


    public void clickReportsMenu() {
        log.info("Click on reports ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(reportsMenu)) {
            reportsMenu.click();
        } else if (commonWaits.isElementPresent(moreMenu)) {
            moreMenu.click();
            commonWaits.waitForSometime(commonWaits.minDelay);
            reportsMenu.click();
        }
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void navigateToTicketConsole() {
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        log.info("Navigating to Ticket Console");
        commonWaits.waitForElementVisible(navigateToConsole);
        navigateToConsole.click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        clickWithoutInterception(navigateToTicketConsole);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
    }

    public SmartITDashboardPage navigateToLiveChatAgentConsole() {
        switchtoDefaultFrame();
        log.info("clicking on live chat link");
        click(livechatChooser);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        if (commonWaits.isElementPresent(liveChatChooser2))
            click(liveChatChooser2);
        if (commonWaits.isElementPresent(liveChatLink)) {
            click(liveChatLink);
            commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 5);
        }
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForElementVisible(iFrameLiveChat,20);
        commonWaits.switchToFrame(iFrameLiveChat);
        return this;
    }

    public void navigateToAssetConsole() {
        switchtoDefaultFrame();
        log.info("Navigating to Asset Console");
        navigateToConsole.click();
        commonWaits.waitForSometime(commonWaits.minDelay);
        clickWithoutInterception(navigateToAssetConsole);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
    }

    public boolean isUserNavigateToTicketConsole() {
        return getDriver().getCurrentUrl().contains("ticket-console");
    }

    public void verifyUserIsNavigatedToTicketConsole() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), ticketConsoleHeading, 20), "User is not navigated to Ticket Console");
    }

    public void navigateToCreateNewIncident() {
        log.info("Click on New Incident ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(moreMenu)) {
            commonWaits.clickWithoutInterception(moreMenu);
            commonWaits.waitForExecutionComplete();
        }
        commonWaits.clickWithoutInterception(createNewDropdown);
        commonWaits.clickWithoutInterception(By.xpath(String.format(create_requested_flow_xpath_template, incidentButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void navigateToCreateNewWorkorder() {
        switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForAllSpinnersToHide("HELIX_SMARTIT");
        log.info("Click on New Workorder");
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), moreMenu, 10)) {
            commonWaits.waitForElementClickable(moreMenu, 10).click();
        }
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createNewDropdown, 10), "Create New dropdown is not visible after clicking More menu");
        commonWaits.waitForElementClickable(createNewDropdown, 10).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(createRequestedFlow, workorderButton)), 10), "Create New dropdown options are not visible after clicking Create New dropdown");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(createRequestedFlow, workorderButton))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForAllSpinnersToHide("HELIX_SMARTIT");
        commonWaits.waitForAllSpinnersToHide("DOTS_LOADER");
    }

    public void navigateToCreateNewChange() {
        log.info("Click on New Change ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState(getDriver());
        if (commonWaits.isElementPresent(moreMenu, 20)) {
            commonWaits.waitForElementClickable(moreMenu, 10);
            commonWaits.clickWithoutInterception(moreMenu);
        }
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementVisible(createNewDropdown, 10);
        commonWaits.clickWithoutInterception(createNewDropdown);
        commonWaits.clickWithoutInterception(By.xpath(String.format(createRequestedFlow, changeButton)));
        commonWaits.waitForPageReadyState();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void navigateToCreateNewKnownError() {
        log.info("Click on New Known Error ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState(getDriver());
        if (commonWaits.isElementPresent(moreMenu)) {
            commonWaits.clickWithoutInterception(moreMenu);
        }
        commonWaits.clickWithoutInterception(createNewDropdown);
        commonWaits.clickWithoutInterception(By.xpath(String.format(createRequestedFlow, knownErrorButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void navigateToCreateNewBroadcast() {
        log.info("Click on New Broadcast ");
        commonWaits.waitForPageReadyState();
        if (commonWaits.isElementPresent(moreMenu)) {
            commonWaits.clickWithoutInterception(moreMenu);
        }
        commonWaits.clickWithoutInterception(createNewDropdown);
        commonWaits.clickWithoutInterception(broadcastMenu);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void navigateToProactiveProblemManagement() {

        int existingWindowCount = getDriver().getWindowHandles().size();
        log.info("Existing window count: " + existingWindowCount);

        log.info("Click on Proactive Problem Management ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(MoreButton)) {
            MoreButton.click();
            commonWaits.clickWithoutInterception(WorkspaceButton);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(workspaces_dropdown_item_xpath_template, SolQAConstants.Proactive_Problem_Management)));
            findElement(By.xpath(String.format(workspaces_dropdown_item_xpath_template, SolQAConstants.Proactive_Problem_Management))).click();

            int expectedWindowCount = existingWindowCount + 1;
            log.info("Waiting for window count to become: " + expectedWindowCount);
            commonWaits.waitForNumberWindowsToBe(expectedWindowCount);

            commonWaits.switchToLastWindow();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.waitForURLContains(SolQAConstants.ITSM_INSIGHTS);
        }
        else if (commonWaits.isElementPresent(WorkspaceButton)) {
            commonWaits.clickWithoutInterception(WorkspaceButton);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(workspaces_dropdown_item_xpath_template, SolQAConstants.Proactive_Problem_Management)));
            findElement(By.xpath(String.format(workspaces_dropdown_item_xpath_template, SolQAConstants.Proactive_Problem_Management))).click();
            int expectedWindowCount = existingWindowCount + 1;
            log.info("Waiting for window count to become: " + expectedWindowCount);
            commonWaits.waitForNumberWindowsToBe(expectedWindowCount);

            commonWaits.switchToLastWindow();
            commonWaits.waitForURLContains(SolQAConstants.ITSM_INSIGHTS);
        }
            
    }

    public void navigateToRealTimeIncidentCorrelation() throws Exception {
        log.info("Click on Real Time Incident Correlation ");

        int existingWindowCount = getDriver().getWindowHandles().size();
        log.info("Existing window count: " + existingWindowCount);

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(MoreButton)) {
            commonWaits.pureSafeClick(MoreButton);

            commonWaits.clickWithoutInterception(WorkspaceButton);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(workspace_element_link_xpath_template, realtimeIncidentCorrelation)));
            findElement(By.xpath(String.format(workspace_element_link_xpath_template, realtimeIncidentCorrelation))).click();
            commonWaits.waitForExecutionComplete();

            int expectedWindowCount = existingWindowCount + 1;
            log.info("Waiting for window count to become: " + expectedWindowCount);
            commonWaits.waitForNumberWindowsToBe(expectedWindowCount);

            commonWaits.switchToLastWindow();
            commonWaits.waitForURLContains(SolQAConstants.ITSM_INSIGHTS);
        }
    }

    public void navigateToSmartRecorder() {

        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        log.info("Click on Smart Recorder ");
        if (commonWaits.isElementPresent(SmartRecorder)) {
            commonWaits.clickWithoutInterception(SmartRecorder);
        } else if (commonWaits.isElementPresent(moreMenu)) {
            commonWaits.clickWithoutInterception(moreMenu);
            commonWaits.clickWithoutInterception(createNewDropdown);
            commonWaits.clickWithoutInterception(By.xpath(String.format(create_requested_flow_xpath_template, smartRecorderButton)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        } else if (commonWaits.isElementPresent(createNewDropdown)) {
            commonWaits.clickWithoutInterception(createNewDropdown);
            commonWaits.clickWithoutInterception(By.xpath(String.format(create_requested_flow_xpath_template, smartRecorderButton)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }

    }

    public void navigateToCreateNewContract() {

        log.info("Click on New Contract ");
        if (commonWaits.isElementPresent(moreMenu)) {
            commonWaits.clickWithoutInterception(moreMenu);
        }
        commonWaits.clickWithoutInterception(createNewDropdown);
        commonWaits.clickWithoutInterception(By.xpath(String.format(create_requested_flow_xpath_template, contractButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }


    public void navigateToHelixGPTGlobalSearch() {
        log.info("Click on Helix GPT Global Search Icon ");
        commonWaits.waitForElementVisible(helixGPTSearchIcon);
        commonWaits.clickWithoutInterception(helixGPTSearchIcon);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

    }

    public void globalSearchAndOpenTicket(String ticketID) {
        try{
        if (isGptGlobalSearchPresent()) {
            gptGlobalSearch(ticketID);
        } else {
            standardGlobalSearch(ticketID);
        }
    }
    catch (AssertionError e){
        standardGlobalSearch(ticketID);

    }
    }

    public void globalSearchAndOpenTicketUsingMessage(String message) {
        commonWaits.waitForExecutionComplete();
        gptGlobalSearchUsingMessage(message);
    }

    /**
     * Check if GPT Global Search is present on the page
     *
     * @return true if GPT Global Search is present, false otherwise
     */

	public boolean isGptGlobalSearchPresent() {
		log.info("Checking if GPT Global Search is present on the page");

		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
		commonWaits.waitForPageReadyState();

		boolean isPresent = commonWaits.waitForElementVisible_withFluentWait(getDriver(),gptGlobalSearch, 30);

		Assert.assertTrue(isPresent, "GPT Global Search button is not present on the page");

		return isPresent;
	}

    /**
     * Click on the standard global search icon
     */
    public void clickOnStandardGlobalSearch() {
        commonWaits.clickWithoutInterception(globalSearchIcon);
    }

    public SmartITDashboardPage gptGlobalSearch(String ticketID) {
        commonWaits.clickWithoutInterception(gptGlobalSearch);
        commonWaits.waitForPageReadyState();
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(studioIframe));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), gptSearchButton, 40), "GPT Search button is not visible");
        commonWaits.clickWithoutInterception(gptSearchButton);
        commonWaits.waitForElementVisible(globalSearchInput).sendKeys(ticketID + Keys.ENTER);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(globalSearchResult, ticketID))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(viewFullTicketFrame), 30),
                "Search result preview iframe is not visible after selecting ticket from existing results");
        commonWaits.switchToFrame(By.xpath(viewFullTicketFrame));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), viewFullTicketLink, 20),
                "View full incident / View full ticket link is not visible in preview");
        commonWaits.waitForPageReadyState();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), viewFullTicketLink, 30), "View Full Ticket link is not visible");
        commonWaits.clickWithoutInterception(viewFullTicketLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(pwaIframe));
        return this;
    }

    public SmartITDashboardPage gptGlobalSearchUsingMessage(String message) {
        navigateToGptSearch();
        activateGptSearchMode();
        performGlobalSearch(message);
        navigateToSearchResult(message);
        return this;
    }

    private void navigateToGptSearch() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), gptGlobalSearch, 20), "GPT Global Search button is not visible on the page");
        commonWaits.clickWithoutInterception(gptGlobalSearch);
        commonWaits.waitForPageReadyState();
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(studioIframe), 20), "Studio iframe is not visible after clicking GPT Global Search");
        commonWaits.switchToFrame(By.xpath(studioIframe));
    }

    private void activateGptSearchMode() {
        commonWaits.waitForElementVisible(gptSearchButton).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), gptSearchButton, 20),
                "GPT Search button is not visible");
        commonWaits.clickWithoutInterception(gptSearchButton);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForPageReadyState();
    }

    private void performGlobalSearch(String message) {
        // Wait for global search input with extended timeout and ensure it's interactable
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), globalSearchInput, 30),
                "Global search input is not visible after GPT search button click");
        
        // Additional wait to ensure element is fully loaded and interactable
        commonWaits.waitForElementClickable(globalSearchInput, 30);
        
        // Additional verification that input is ready for interaction
        Assert.assertTrue(globalSearchInput.isDisplayed() && globalSearchInput.isEnabled(), 
                "Global search input is not ready for interaction");
        
        globalSearchInput.clear();
        globalSearchInput.sendKeys(message);
        commonWaits.waitForExecutionComplete();
        globalSearchInput.sendKeys(Keys.ENTER);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 2);
    }

    private void navigateToSearchResult(String message) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),
                By.xpath(String.format(globalSearchResultUsingMessage, message)), 30),
                "Global search result with message: " + message + " is not visible");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(globalSearchResultUsingMessage, message))).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(viewFullTicketFrame), 30),
                "Search result preview iframe is not visible after selecting ticket from existing results for message: " + message);
        commonWaits.switchToFrame(By.xpath(viewFullTicketFrame));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), viewFullTicketLink, 30),
                "View full incident / View full ticket link is not visible after selecting result for message: " + message);
        commonWaits.clickWithoutInterception(viewFullTicketLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(pwaIframe));
    }

    public void standardGlobalSearch(String ticketID) {
        commonWaits.clickWithoutInterception(globalSearchIcon);
        commonWaits.waitForElementVisible(globalSearchInput).sendKeys(ticketID + Keys.ENTER);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), viewFullTicketLink, 10);
        commonWaits.clickWithoutInterception(viewFullTicketLink);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(pwaIframe));
    }

    public void resolveIncident(String resolutionNotes) {
        commonWaits.waitForElementPresent("//button[@name='ar304420591']");
        commonWaits.click("//button[@name='ar304420591']");
        keyClearAndType("//textarea[@id='1000000156']", resolutionNotes);
        // Set Status
        commonWaits.click("//button[@id='rx-select-328']");
        commonWaits.click("//*[.='Resolved']");
        // Set Status Reason
        commonWaits.click("//button[@id='rx-select-422']");
        commonWaits.click("//*[.='Temporary Corrective Action']");
        // Set Customer
        keyClearAndType("//input[@id='ar303530000']", "Abibo De Serio");
        // Save
        commonWaits.click("//button[@id='304440891']");
    }

    public void refreshSmartITPage() {
        getDriver().navigate().refresh();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState();
    }

    public void navigateToKnowledge() {

        log.info("Click on Knowledge ");

        int existingWindowCount = getDriver().getWindowHandles().size();
        log.info("Existing window count: " + existingWindowCount);

        commonWaits.waitForPageFullyLoaded(30, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(),createNewDropdown, 10)) {
            commonWaits.clickWithoutInterception(createNewDropdown);
            commonWaits.waitForElementVisible_withFluentWait(getDriver(),knowledgeButton, 10);
            commonWaits.clickWithoutInterception(knowledgeButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }

        commonWaits.switchToLastWindow();
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForURLContains(SolQAConstants.KNOWLEDGE);
    }

    public void navigateToWorkSpace(String workSpaceName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(moreMenu)) {
            commonWaits.clickWithoutInterception(moreMenu);
        }
        commonWaits.clickWithoutInterception(WorkspaceButton);
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.clickWithoutInterception(By.xpath(String.format(workspaceDropdownItem, workSpaceName)));
        commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);
    }

    public void navigateToNewIncident() {
        commonWaits.switchtoDefaultFrame();
        log.info("Click on New Incident ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        if (commonWaits.isElementPresent(moreMenu)) {
            log.info("Clicking More Menu");
            commonWaits.clickWithoutInterception(moreMenu);
            commonWaits.waitForExecutionComplete();
        }
        if (commonWaits.isElementPresent(createNewDropdownAngleDown)) {
            commonWaits.clickWithoutInterception(createNewDropdownAngleDown);
            commonWaits.waitForExecutionComplete();
        }
        commonWaits.clickWithoutInterception(By.xpath(String.format(create_requested_flow_xpath_template, incidentButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void navigateToCreateNewAsset() {
        log.info("Click on New Asset ");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageReadyState(getDriver());
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), moreMenu, 10)) {
            commonWaits.clickWithoutInterception(moreMenu);
        }
        commonWaits.clickWithoutInterception(createNewDropdown);
        commonWaits.clickWithoutInterception(By.xpath(String.format(createRequestedFlow, assetButton)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    public void verifyImpactAnalysisConsoleLaunchedSuccessfully() {
        int maxAttempts = 5;
        int attempt = 0;
        boolean isSuccess = false;
        while (attempt < maxAttempts) {
            try {
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
                boolean isItsmPresent = commonWaits.isElementPresent(impactAnalysisSettings);
                Assert.assertTrue(isItsmPresent, "SmartIT application is not launched successfully");
                isSuccess = true;
                break; // ✅ success → exit loop
    
            } catch (AssertionError | Exception e) {
                attempt++;
                if (attempt == maxAttempts) {
                    throw new AssertionError(
                        "SmartIT application is not launched successfully after " + maxAttempts + " attempts", e);
                }
                getDriver().navigate().refresh();
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            }
        }
    
        if (!isSuccess) {
            Assert.fail("SmartIT application validation failed after retries");
        }
    }

    public void navigateRequiredSmartITURL(String URL) {
		log.info("Navigating to required SmartIT URL: " + URL);
		String newURL = CommonMethods.readConfig("SmartIT_URL") + URL;
		getDriver().navigate().to(newURL);
		commonWaits.waitForExecutionComplete();
		log.info("Navigated to required SmartIT URL: " + newURL);
	}

    @FindBy(xpath = "//*[contains(@class, 'modal-content') and contains(normalize-space(.), 'Analytics')]")
    WebElement analyticsPopup;
    @FindBy(xpath = "//*[contains(@class, 'modal-content') and contains(normalize-space(.), 'Analytics')]//button[normalize-space()='Save']")
    WebElement analyticsPopupSaveButton;
    public SmartITDashboardPage handleAnalyticsPopup() {
        commonWaits.switchtoDefaultFrame();
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), analyticsPopup, 20)) {
            log.info("Analytics popup is present. Closing it.");
            commonWaits.waitForElementVisible(analyticsPopupSaveButton).click();
        } else {
            log.info("Analytics popup is not present.");
        }
        return this;
    }

    public SmartITDashboardPage switchToListViewInITSMInsights() {
        commonWaits.clickWithoutInterception(listViewButton);
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public SmartITDashboardPage verifyListViewContainsClusterandINCData() {
        log.info("Verifying list view contains Cluster and INC data");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(tableColumnHeader, "Incident cluster"))), "Cluster data is not present in the list view");
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(tableColumnHeader, "No. of Incidents"))), "Incident data is not present in the list view");
        log.info("List view contains Cluster and INC data");
        log.info("Verifying atleast one row is present in the list view");
        Assert.assertTrue(commonWaits.isElementPresent(tableRow), "Atleast one row is not present in the list view");
        log.info("Atleast one row is present in the list view");
        return this;
    }

    public SmartITDashboardPage verifyClusterDetailsPageContainsProblemID() {
        log.info("Verifying cluster details page contains Problem ID");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(tableColumnHeader, "Problem ID"))), "Problem ID is not present in the cluster details page");
        Assert.assertTrue(commonWaits.isElementPresent(pbiButton), "Problem ID is not present in the cluster details page");
        log.info("Problem ID is present in the cluster details page");
        return this;
    }

}