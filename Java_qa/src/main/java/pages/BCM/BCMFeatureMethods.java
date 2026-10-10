package pages.BCM;

import framework.BasePage;
import framework.Launcher;
import org.testng.Assert;
import util.CommonWaits;
import util.SolqaLogger;
import util.SolQAConstants;

/**
 * Feature methods class for BMC Client Management
 * Contains high-level test actions and business logic
 */
public class BCMFeatureMethods extends BasePage {
    
    static SolqaLogger log = new SolqaLogger(BCMFeatureMethods.class.getName());
    BCMLogin bcmLogin = new BCMLogin();
    BCMDashboard bcmDashboard = new BCMDashboard();
    CommonWaits commonWaits = new CommonWaits();
    
    /**
     * Constructor
     */
    public BCMFeatureMethods() {
        log.info("Initialized BCMFeatureMethods");
    }
    
    /**
     * Verify login page is displayed
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyLoginPageDisplayed() {
        log.info("Verifying login page is displayed");
        boolean isDisplayed = bcmLogin.isLoginPageDisplayed();
        Assert.assertTrue(isDisplayed, "Login page should be displayed");
        log.info("Login page verification: PASS");
        return this;
    }
    
    /**
     * Perform login with username and password
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods performLogin(String username, String password) {
        log.info("Performing login with user: " + username);
        bcmLogin.waitForLoginPageLoad();
        Launcher.login(username, password, SolQAConstants.appBCM);
        log.info("Login action completed");
        return this;
    }
    
    /**
     * Verify dashboard is displayed after successful login
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyDashboardDisplayed() {
        log.info("Verifying dashboard is displayed");
        bcmDashboard.waitForDashboardLoad();
        boolean isDisplayed = bcmDashboard.isDashboardDisplayed();
        Assert.assertTrue(isDisplayed, "Dashboard should be displayed after successful login");
        log.info("Dashboard verification: PASS");
        return this;
    }
    
    /**
     * Verify welcome message is displayed on dashboard
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyWelcomeMessage() {
        log.info("Verifying welcome message is displayed");
        commonWaits.waitForPageFullyLoaded(15, 5);
        bcmDashboard.waitForDashboardLoad();
        boolean isDisplayed = bcmDashboard.isWelcomeMessageDisplayed();
        Assert.assertTrue(isDisplayed, "Welcome message should be displayed on dashboard");
        log.info("Welcome message verification: PASS");
        return this;
    }
    
    /**
     * Verify product name is displayed in header
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyProductName() {
        log.info("Verifying product name is displayed");
        boolean isDisplayed = bcmDashboard.isProductNameDisplayed();
        Assert.assertTrue(isDisplayed, "Product name 'Client Management' should be displayed");
        log.info("Product name verification: PASS");
        return this;
    }
    
    /**
     * Verify logged-in username matches expected
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyLoggedInUsername(String expectedUsername) {
        log.info("Verifying logged-in username: " + expectedUsername);
        String actualUsername = bcmDashboard.getDisplayedUsername();
        Assert.assertTrue(actualUsername.contains(expectedUsername), 
            "Expected username '" + expectedUsername + "' but found '" + actualUsername + "'");
        log.info("Username verification: PASS");
        return this;
    }
    
    /**
     * Click Home menu item
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods clickHome() {
        log.info("Clicking Home menu");
        bcmDashboard.clickHome();
        commonWaits.waitForPageFullyLoaded(10, 5);
        log.info("Home menu clicked");
        return this;
    }
    
    /**
     * Enter search text in search field
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods enterSearchText(String searchText) {
        log.info("Entering search text: " + searchText);
        bcmDashboard.enterSearchText(searchText);
        log.info("Search text entered");
        return this;
    }
    
    /**
     * Click Settings button
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods clickSettings() {
        log.info("Clicking Settings button");
        bcmDashboard.clickSettings();
        commonWaits.waitForPageFullyLoaded(10, 5);
        log.info("Settings button clicked");
        return this;
    }
    
    /**
     * Click Preferences from profile menu
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods clickPreferences() {
        log.info("Clicking Preferences from profile menu");
        bcmDashboard.clickPreferences();
        commonWaits.waitForPageFullyLoaded(15, 5);
        log.info("Preferences clicked");
        return this;
    }
    
    /**
     * Click Java Console Installation menu item
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods clickJavaConsoleInstallation() {
        log.info("Clicking Java Console Installation");
        bcmDashboard.clickJavaConsoleInstallation();
        commonWaits.waitForPageFullyLoaded(15, 5);
        log.info("Java Console Installation clicked");
        return this;
    }
    
    /**
     * Click SSL Certificate menu item
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods clickSSLCertificate() {
        log.info("Clicking SSL Certificate");
        bcmDashboard.clickSSLCertificate();
        commonWaits.waitForPageFullyLoaded(15, 5);
        log.info("SSL Certificate clicked");
        return this;
    }
    
    /**
     * Perform logout
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods performLogout() {
        log.info("Performing logout");
        bcmDashboard.performLogout();
        bcmLogin.waitForLoginPageLoad();
        log.info("Logout completed successfully");
        return this;
    }
    
    /**
     * Verify logout was successful
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyLogoutSuccessful() {
        log.info("Verifying logout was successful");
        boolean isLoginPageDisplayed = bcmLogin.isLoginPageDisplayed();
        Assert.assertTrue(isLoginPageDisplayed, "Login page should be displayed after logout");
        log.info("Logout verification: PASS");
        return this;
    }
    
    /**
     * Verify error message is displayed for invalid login
     */
    public BCMFeatureMethods verifyLoginErrorDisplayed() {
        log.info("Verifying login error message is displayed");
        commonWaits.waitForPageFullyLoaded(10, 5);
        // Error message and login page should still be visible
        boolean isLoginPageDisplayed = bcmLogin.isLoginPageDisplayed();
        Assert.assertTrue(isLoginPageDisplayed, "Login page should still be displayed after invalid credentials");
        String errorMessage = bcmLogin.getErrorMessage();
        Assert.assertTrue(errorMessage.contains(SolQAConstants.INVALID_CREDENTIALS), "Error message should be displayed after invalid credentials");
        log.info("Login error verification: PASS");
        return this;
    }
    
    /**
     * Wait for idle timeout
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods waitForIdleTimeout(int minutes) {
        log.info("Waiting for " + minutes + " minutes for idle timeout");
        try {
            Thread.sleep(minutes * 60 * 1000L);
            log.info("Idle wait completed");
        } catch (InterruptedException e) {
            log.error("Wait interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return this;
    }
    
    /**
     * Check if session is still active
     */
    public boolean isSessionActive() {
        log.info("Checking session status");
        boolean isLoggedIn = bcmDashboard.isUserLoggedIn();
        log.info("Session active: " + isLoggedIn);
        return isLoggedIn;
    }
    
    /**
     * Check if session has expired
     */
    public boolean isSessionExpired() {
        log.info("Checking if session expired");
        boolean isLoginPageDisplayed = bcmLogin.isLoginPageDisplayed();
        log.info("Session expired (login page displayed): " + isLoginPageDisplayed);
        return isLoginPageDisplayed;
    }
    
    /**
     * Refresh page and verify re-authentication form is displayed after inactivity
     * This method should be called after waiting for idle timeout (5 minutes)
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods refreshPageAndVerifyReAuthForm() {
        log.info("Refreshing page to check for re-authentication form after inactivity");
        
        // Refresh the page
        getDriver().navigate().refresh();
        log.info("Page refreshed");
        
        // Wait for page to load
        commonWaits.waitForPageFullyLoaded(15, 5);
        
        // Verify re-authentication form is displayed
        boolean isReAuthFormDisplayed = bcmLogin.isReAuthenticationFormDisplayed();
        Assert.assertTrue(isReAuthFormDisplayed, "Re-authentication form should be displayed after 5 minutes of inactivity");
        
        log.info("Re-authentication form verification: PASS");
        log.info("Username field is readonly: " + bcmLogin.getReAuthUsername());
        return this;
    }
    
    /**
     * Perform re-authentication after session timeout
     * @param password Password to re-authenticate
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods performReAuthentication(String password) {
        log.info("Performing re-authentication after timeout");
        bcmLogin.performReAuthentication(password);
        log.info("Re-authentication completed");
        return this;
    }
    
    /**
     * Verify re-authentication form is displayed
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods verifyReAuthenticationFormDisplayed() {
        log.info("Verifying re-authentication form is displayed");
        boolean isDisplayed = bcmLogin.isReAuthenticationFormDisplayed();
        Assert.assertTrue(isDisplayed, "Re-authentication form should be displayed after session timeout");
        log.info("Re-authentication form verification: PASS");
        return this;
    }
    
    /**
     * Click on Cancel button (on re-authentication form)
     * @return BCMFeatureMethods instance for method chaining
     */
    public BCMFeatureMethods clickCancelButton() {
        log.info("Clicking Cancel button on re-authentication form");
        bcmLogin.clickCancelButton();
        log.info("Cancel button clicked successfully");
        return this;
    }

    
}
