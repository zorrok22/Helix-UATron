package pages.BCM;

import framework.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolqaLogger;

/**
 * Page Object for BMC Client Management Dashboard/Home page
 * Handles main page interactions and navigation
 */
public class BCMDashboard extends BasePage {
    
    static SolqaLogger log = new SolqaLogger(BCMDashboard.class.getName());
    CommonWaits commonWaits = new CommonWaits();
    
    public BCMDashboard() {
        PageFactory.initElements(getDriver(), this);
        log.info("Initialized BCMDashboard page object");
    }
    
    // Page header and navigation elements
    @FindBy(xpath = "//span[@class='a-product__name' and contains(text(),'Client Management')]")
    private WebElement productName;
    
    @FindBy(xpath = "//h1[contains(text(),'Welcome to BMC Client Management')]")
    private WebElement welcomeMessage;
    
    @FindBy(xpath = "//p[contains(@class,'intro') and contains(text(),'Start by typing the device name')]")
    private WebElement introText;
    
    // Search input field
    @FindBy(xpath = "//input[@id='search-input']")
    private WebElement searchInput;
    
    // Profile menu dropdown toggle
    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    private WebElement profileMenuButton;
    
    // Profile dropdown menu container
    @FindBy(xpath = "//ul[@role='menu' and contains(@class,'a-profile-dropdown-menu')]")
    private WebElement profileDropdownMenu;
    
    // Profile dropdown items
    @FindBy(xpath = "//button[@data-testid='adapt-profile-1_menu-item-0' and contains(text(),'Preferences')]")
    private WebElement preferencesMenuItem;
    
    @FindBy(xpath = "//button[@data-testid='adapt-profile-1_menu-item-1' and contains(text(),'Java Console Installation')]")
    private WebElement javaConsoleMenuItem;
    
    @FindBy(xpath = "//button[@data-testid='adapt-profile-1_menu-item-2' and contains(text(),'SSL Certificate')]")
    private WebElement sslCertificateMenuItem;
    
    @FindBy(xpath = "//button[@data-testid='adapt-profile-1_sign-out' and contains(text(),'Sign out')]")
    private WebElement signOutButton;
    
    // Settings gear icon
    @FindBy(xpath = "//button[@aria-label='Settings']")
    private WebElement settingsButton;
    
    // Home menu item
    @FindBy(xpath = "//a[@data-testid='menu-item-test-id-1']//span[text()='Home']")
    private WebElement homeMenuItem;
    
    // Username display in profile
    @FindBy(xpath = "//span[@class='menu-profile__username']")
    private WebElement usernameDisplay;
    
    // Administrator connected message
    @FindBy(xpath = "//button[contains(text(),'There is 1 administrator connected')]")
    private WebElement adminConnectedMessage;
    
    // Need help link
    @FindBy(xpath = "//div[contains(@class,'help') and contains(text(),'Need help?')]")
    private WebElement needHelpLink;
    
    /**
     * Verify if dashboard is displayed after successful login
     * @return true if dashboard elements are visible
     */
    public boolean isDashboardDisplayed() {
        log.info("Verifying dashboard is displayed");
        boolean isWelcomeVisible = commonWaits.isElementPresent(welcomeMessage);
        boolean isSearchVisible = commonWaits.isElementPresent(searchInput);
        boolean isDashboardDisplayed = isWelcomeVisible && isSearchVisible;
        log.info("Dashboard displayed: " + isDashboardDisplayed);
        return isDashboardDisplayed;
    }
    
    /**
     * Get the displayed username from profile menu
     * @return Username text
     */
    public String getDisplayedUsername() {
        commonWaits.waitForElementVisible(usernameDisplay);
        String username = usernameDisplay.getText().trim();
        log.info("Displayed username: " + username);
        return username;
    }
    
    /**
     * Verify welcome message is displayed
     * @return true if welcome message is visible
     */
    public boolean isWelcomeMessageDisplayed() {
        boolean isDisplayed = commonWaits.isElementPresent(welcomeMessage);
        log.info("Welcome message displayed: " + isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Click on profile menu to open dropdown
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard openProfileMenu() {
        log.info("Opening profile menu");
        commonWaits.waitForElementClickable(profileMenuButton);
        profileMenuButton.click();
        
        // Wait for dropdown to appear
        commonWaits.waitForElementVisible(profileDropdownMenu);
        log.info("Profile menu opened successfully");
        return this;
    }
    
    /**
     * Click on Preferences menu item
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard clickPreferences() {
        log.info("Clicking Preferences menu item");
        openProfileMenu();
        commonWaits.waitForElementClickable(preferencesMenuItem);
        preferencesMenuItem.click();
        log.info("Clicked Preferences");
        return this;
    }
    
    /**
     * Click on Java Console Installation menu item
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard clickJavaConsoleInstallation() {
        log.info("Clicking Java Console Installation menu item");
        openProfileMenu();
        commonWaits.waitForElementClickable(javaConsoleMenuItem);
        javaConsoleMenuItem.click();
        log.info("Clicked Java Console Installation");
        return this;
    }
    
    /**
     * Click on SSL Certificate menu item
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard clickSSLCertificate() {
        log.info("Clicking SSL Certificate menu item");
        openProfileMenu();
        commonWaits.waitForElementClickable(sslCertificateMenuItem);
        sslCertificateMenuItem.click();
        log.info("Clicked SSL Certificate");
        return this;
    }
    
    /**
     * Perform logout by clicking sign out button
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard performLogout() {
        log.info("Performing logout");
        openProfileMenu();
        commonWaits.waitForElementClickable(signOutButton);
        signOutButton.click();
        
        // Wait for login page to appear
        commonWaits.waitForPageFullyLoaded(30, 10);
        log.info("Logout completed successfully");
        return this;
    }
    
    /**
     * Enter search text in search field
     * @param searchText Text to search
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard enterSearchText(String searchText) {
        log.info("Entering search text: " + searchText);
        commonWaits.waitForElementClickable(searchInput);
        searchInput.clear();
        searchInput.sendKeys(searchText);
        log.info("Search text entered");
        return this;
    }
    
    /**
     * Click settings button
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard clickSettings() {
        log.info("Clicking Settings button");
        commonWaits.waitForElementClickable(settingsButton);
        settingsButton.click();
        log.info("Settings button clicked");
        return this;
    }
    
    /**
     * Click Home menu item
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard clickHome() {
        log.info("Clicking Home menu item");
        commonWaits.waitForElementClickable(homeMenuItem);
        homeMenuItem.click();
        log.info("Home menu item clicked");
        return this;
    }
    
    /**
     * Wait for dashboard to load completely
     * @return BCMDashboard instance for method chaining
     */
    public BCMDashboard waitForDashboardLoad() {
        log.info("Waiting for dashboard to load");
        commonWaits.waitForElementVisible(searchInput);
        commonWaits.waitForPageFullyLoaded(30, 10);
        log.info("Dashboard loaded successfully");
        return this;
    }
    
    /**
     * Verify product name is displayed in header
     * @return true if product name is visible
     */
    public boolean isProductNameDisplayed() {
        boolean isDisplayed = commonWaits.isElementPresent(productName);
        log.info("Product name displayed: " + isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Check if user is still logged in (session active)
     * @return true if dashboard is visible
     */
    public boolean isUserLoggedIn() {
        boolean isLoggedIn = isDashboardDisplayed();
        log.info("User logged in status: " + isLoggedIn);
        return isLoggedIn;
    }
}
