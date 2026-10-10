from selenium.webdriver.common.by import By

class AIOPSLocators:
    """Locators for ITOM Portal pages"""
    # Login Page
    USERNAME_FIELD = (By.ID, "user_login")
    PASSWORD_FIELD = (By.ID, "login_user_password")
    LOGIN_BUTTON = (By.ID, "login-jsp-btn")
    # PROFILE_AVATAR = (By.CSS_SELECTOR, "span.a-profile__avatar.a-profile_delault-icon.a-profile__avatar--big")
    PROFILE_AVATAR = (By.XPATH, "//span[contains(@class, 'a-profile__avatar a-profile_delault-icon')]")
    # AIOps Tabs
    AIOPS_TAB = (By.XPATH, "//a[contains(@class, 'adapt-menu__link')]//span[text()='{}']")
    
    # Situations Page
    FILTER_REMOVE_BUTTON = (By.XPATH, "//button[contains(@class, 'a-tag__close-btn') and @aria-label='{}']")
    ADVANCED_FILTER_DROPDOWN = (By.CSS_SELECTOR, "span.advanced-filter__dropdown-anchor-label-wrapper")
    TYPE_FILTER = (By.XPATH, "//div[contains(@class, 'advanced-filter__label')]//div[text()='Type']")
    ML_BASED_SITUATION_OPTION = (By.XPATH, "//adapt-highlight[text()='ML-based Situation']")
    APPLY_FILTERS_BUTTON = (By.XPATH, "//button[@type='button' and @adapt-button='' and @btn-type='primary' and @size='small' and contains(@class, 'btn-primary') and @data-testid='adapt-af-1_footer_apply']")
    FIRST_SITUATION_MESSAGE = (By.XPATH, "(//a[@class='situation-msg alert-link text-active large font-weight-bold'])[1]")
    ASK_HELIXGPT_BUTTON = (By.XPATH, "//button[@id='adapt_accordion_tab_id_4']")
    HELIXGPT_TAB = (By.XPATH, "//button[@aria-controls='{}']")
    LOG_INSIGHTS_SUMMARY = (By.XPATH, "//p[@id='log-insights-item-summary-0']")
    EVENTS_DISPLAY_IN_SITUATION_EXPLANATION = (By.XPATH, "//button[@data-testid='situation-explanation-view-type-switcher-1']")
    EVENT_MESSAGES = (By.XPATH, "//tbody[@class='ui-table-tbody']//tr//td[1]//span[contains(@class, 'alert-link')]")
    SITUATIONS_TABLE="//table[@class='ui-table-scrollable-body-table']"
    SITUATIONS_TABLE_ROWS="//table[@class='ui-table-scrollable-body-table']/tbody/tr"
    ACTION_BUTTON="//button[@id='actionDropdownMenu']"
    CLOSE_BUTTON= "//button[@id='close']"
    CONFIRMATIONPOPUP_YES_BUTTON= "//button[@id='confirmation-yes-btn']"
    REFRESH_BUTTON="//button[@id='toggle-refresh']"
    IMPACT_BUTTON="//button[contains(normalize-space(), 'Impact ?')]"
    SHOW_MORE_BUTTON="//button[contains(normalize-space(), 'Show more')]"
    # Situation Summary Locators
    ROOT_CAUSE_SUMMARY = (By.XPATH, "//p[@id='root-cause-summary']")
    SITUATION_SUMMARY_DESCRIPTION = (By.XPATH, "//p[@id='situation-summary-description']")
    #
    QUESTION_ID = {
        "Impact": (By.XPATH, "//button[@data-testid='helixGPTChatMessage-0__helixGPTChatSuggestedQuestion-2']"),
    }

    ANSWER_ID = {
        "Impact": (By.XPATH, "//div[@data-testid='helixGPTChatMessage-2__messageContent']//span//p"),
    }

    SEARCH_SITUATION_BASED_ON_TEXT_BUTTON = (By.XPATH, "//button[@data-testid='global-search_button']")

    ROOT_CAUSE_SITUATION = (By.XPATH, "//span[@id='e0bd4585-ade4-457b-a791-a028d34af456-rca-icon']/following-sibling::span")

    # Table header locator for Situations page
    SITUATIONS_TABLE_HEADERS = (By.XPATH, "//table//thead//th")

    # Locator for the service row (ML-K8S-DEP) in the Situations table
    SERVICE_ROW = (By.XPATH, "//table//tbody//tr[.//td[contains(text(), 'ML-K8S-DEP')]]")

    # Locator for the Show Recommendation Planner hyperlink in the situation details panel
    RECOMMENDATION_PLANNER_HYPERLINK = (By.XPATH, "//div[contains(@class, 'situation-details')]//a[contains(text(), 'Show Recommendation Planner')]")

    BAP_TAB = (By.XPATH, "//button[.//span[contains(text(), 'Best Action Recommendations')]]")

    # Locators for Situation Explanation and CI Topology sections
    SITUATION_EXPLANATION_SECTION = (By.XPATH, "//div[contains(@class, 'situation-explanation')]//h3[contains(text(), 'Situation Explanation')]")
    CI_TOPOLOGY_SECTION = (By.XPATH, "//div[contains(@class, 'ci-topology')]//h3[contains(text(), 'CI Topology and Analysis')]")

    # Situation Explanation Page Locators
    EVENTS_TAB = (By.XPATH, "//div[contains(@class, 'situation-explanation')]//button[contains(@class, 'view-type-switcher') and contains(text(), 'Events')]")
    EVENTS_TABLE = (By.XPATH, "//div[contains(@class, 'situation-explanation')]//table[contains(@class, 'ui-table')]")
    EVENT_ROWS = (By.XPATH, "//div[contains(@class, 'situation-explanation')]//table[contains(@class, 'ui-table')]//tbody//tr")
    EVENT_MESSAGE = (By.XPATH, ".//td[1]//span[contains(@class, 'alert-link')]")
    EVENT_SEVERITY = (By.XPATH, ".//td[2]//span[contains(@class, 'severity')]")
    EVENT_TIME = (By.XPATH, ".//td[3]//span[contains(@class, 'time')]")
    EVENT_SOURCE = (By.XPATH, ".//td[4]//span[contains(@class, 'source')]")
    EVENT_TYPE = (By.XPATH, ".//td[5]//span[contains(@class, 'type')]")

    TIME_RANGE_DROPDOWN = (By.XPATH, "//button[@id='timeRangeFilter']")
    TIME_RANGE_LAST_30_DAYS = (By.XPATH, "//li[@id='Last 30 days']")



