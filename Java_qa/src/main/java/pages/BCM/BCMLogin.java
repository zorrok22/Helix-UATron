package pages.BCM;

import framework.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolqaLogger;

/**
 * Page Object for BMC Client Management Login page
 * Handles all login-related interactions
 */
public class BCMLogin extends BasePage {
    
    static SolqaLogger log = new SolqaLogger(BCMLogin.class.getName());
    CommonWaits commonWaits = new CommonWaits();
    
    public BCMLogin() {
        PageFactory.initElements(getDriver(), this);
        log.info("Initialized BCMLogin page object");
    }
    
    // Login page elements
    @FindBy(xpath = "//input[@id='login-input']")
    private WebElement usernameField;
    
    @FindBy(xpath = "//input[@id='password-input']")
    private WebElement passwordField;
    
    @FindBy(xpath = "//button[@id='login-button']")
    private WebElement loginButton;
    
    // Error message element
    @FindBy(xpath = "//div[contains(@class,'text-danger')]")
    private WebElement errorMessage;
    
    // Loading panel
    @FindBy(xpath = "//div[@class='general-loading-panel' and contains(@style,'display: inline')]")
    private WebElement loadingPanel;
    
    // Login form container
    @FindBy(xpath = "//form[contains(@class,'form')]")
    private WebElement loginForm;
    
    // Re-authentication form elements (appears after session timeout)
    @FindBy(xpath = "//input[@id='login-input' and @readonly]")
    private WebElement readonlyUsernameField;
    
    @FindBy(xpath = "//button[@id='cancel-button']")
    private WebElement cancelButton;
    
    @FindBy(xpath = "//button[@id='login-button' and @type='submit']")
    private WebElement reAuthSignInButton;
    
    /**
     * Perform login with username and password
     * @param username Username to login
     * @param password Password to login
     * @return BCMLogin instance for method chaining
     */
    public BCMLogin login(String username, String password) {
        log.info("Performing login with username: " + username);
        
        // Wait for login form to be visible
        commonWaits.waitForElementVisible(loginForm);
        
        // Enter username
        commonWaits.waitForElementClickable(usernameField);
        usernameField.clear();
        usernameField.sendKeys(username);
        log.info("Entered username: " + username);
        
        // Enter password
        commonWaits.waitForElementClickable(passwordField);
        passwordField.clear();
        passwordField.sendKeys(password);
        log.info("Entered password");
        
        // Click login button
        commonWaits.waitForElementClickable(loginButton);
        loginButton.click();
        log.info("Clicked login button");
        
        // Wait for loading to complete
        commonWaits.waitForPageFullyLoaded(30, 10);
        return this;
    }
    
    /**
     * Check if login page is displayed
     * @return true if login page is visible
     */
    public boolean isLoginPageDisplayed() {
        boolean isDisplayed = commonWaits.isElementPresent(loginForm);
        log.info("Login page displayed: " + isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Get error message text if present
     * @return Error message text or empty string
     */
    public String getErrorMessage() {
        try {
            if (commonWaits.isElementPresent(errorMessage)) {
                String message = errorMessage.getText();
                log.info("Error message found: " + message);
                return message;
            }
        } catch (Exception e) {
            log.info("No error message present");
        }
        return "";
    }
    
    /**
     * Verify if error message is displayed (for invalid login)
     * @return true if error message is visible
     */
    public boolean isErrorMessageDisplayed() {
        boolean isDisplayed = commonWaits.isElementPresent(errorMessage);
        log.info("Error message displayed: " + isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Wait for login page to load completely
     * @return BCMLogin instance for method chaining
     */
    public BCMLogin waitForLoginPageLoad() {
        log.info("Waiting for login page to load");
        commonWaits.waitForElementVisible(loginForm);
        commonWaits.waitForPageFullyLoaded(30, 10);
        log.info("Login page loaded successfully");
        return this;
    }
    
    /**
     * Check if re-authentication form is displayed (after session timeout)
     * Re-auth form has readonly username field and cancel button
     * @return true if re-authentication form is visible
     */
    public boolean isReAuthenticationFormDisplayed() {
        try {
            boolean isReadonlyUsernamePresent = commonWaits.isElementPresent(readonlyUsernameField);
            boolean isCancelButtonPresent = commonWaits.isElementPresent(cancelButton);
            boolean isSignInButtonPresent = commonWaits.isElementPresent(reAuthSignInButton);
            boolean isFormDisplayed = isReadonlyUsernamePresent && isCancelButtonPresent && isSignInButtonPresent;
            log.info("Re-authentication form displayed: " + isFormDisplayed);
            return isFormDisplayed;
        } catch (Exception e) {
            log.info("Re-authentication form not displayed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get the readonly username value from re-auth form
     * @return Username displayed in readonly field
     */
    public String getReAuthUsername() {
        if (commonWaits.isElementPresent(readonlyUsernameField)) {
            String username = readonlyUsernameField.getAttribute("value");
            log.info("Re-auth username: " + username);
            return username;
        }
        return "";
    }
    
    /**
     * Perform re-authentication after timeout
     * Username is pre-filled and readonly, only password needs to be entered
     * @param password Password to re-authenticate
     * @return BCMLogin instance for method chaining
     */
    public BCMLogin performReAuthentication(String password) {
        log.info("Performing re-authentication");
        
        // Wait for re-auth form
        commonWaits.waitForElementVisible(passwordField);
        
        // Enter password
        passwordField.clear();
        passwordField.sendKeys(password);
        log.info("Entered password for re-authentication");
        
        // Click sign in
        commonWaits.waitForElementClickable(reAuthSignInButton);
        reAuthSignInButton.click();
        log.info("Clicked Sign In button for re-authentication");
        
        // Wait for page load
        commonWaits.waitForPageFullyLoaded(30, 10);
        return this;
    }
    
    /**
     * Click on Cancel button (on re-authentication form)
     * @return BCMLogin instance for method chaining
     */
    public BCMLogin clickCancelButton() {
        log.info("Clicking Cancel button");
        commonWaits.waitForElementClickable(cancelButton);
        cancelButton.click();
        log.info("Cancel button clicked");
        commonWaits.waitForPageFullyLoaded(15, 5);
        return this;
    }

}
