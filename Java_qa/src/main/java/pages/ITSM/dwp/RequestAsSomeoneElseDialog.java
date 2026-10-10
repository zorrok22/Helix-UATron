package pages.ITSM.dwp;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.util.List;

public class RequestAsSomeoneElseDialog extends BasePage {

    static SolqaLogger log = new SolqaLogger(RequestAsSomeoneElseDialog.class.getName());
    CommonWaits commonWaits;

    // -------------------- XPaths (dynamic templates only) --------------------
    private static final String SELECTED_USER_PILL_XPATH =
            "//div[contains(@class,'modal') or contains(@class,'dialog')]//*[contains(@class,'pill')"
                    + " or contains(@class,'selected')][contains(.,'%s')]";
    // Relative locator used within the selected-user pill element.
    private static final String SELECTED_USER_REMOVE_BUTTON_REL_XPATH =
            ".//button[contains(@aria-label,'Remove') or contains(@class,'close') or contains(@class,'remove')]";

    private static final String RESULT_ITEM_BY_TEXT_XPATH =
            "//div[contains(@class,'modal') or contains(@class,'dialog')]//*[self::button or self::li or self::div]"
                    + "[contains(normalize-space(),'%s')]";

    private static final String USER_BADGE_BY_NAME_XPATH =
            "//span[contains(.,'%s') and (contains(@class,'badge') or contains(@class,'pill') or contains(@class,'chip'))]";
    private static final String REQUESTING_FOR_BY_NAME_XPATH =
            "//*[contains(text(),'Requesting for') or contains(text(),'Request for')]//*[contains(.,'%s')]";
    private static final String HEADER_CONTAINS_NAME_XPATH =
            "//header//*[contains(.,'%s')]";
    private static final String BANNER_CONTAINS_NAME_XPATH =
            "//*[contains(@class,'banner') or contains(@class,'notification') or contains(@class,'alert')]//*[contains(.,'%s')]";
    private static final String USER_MENU_AREA_CONTAINS_NAME_XPATH =
            "//*[contains(@class,'user-menu') or contains(@class,'profile')]//*[contains(.,'%s')]";
    private static final String GENERIC_SELECTED_USER_CONTAINS_NAME_XPATH =
            "//*[contains(@class,'requesting-for') or contains(@class,'request-for') or contains(@class,'selected-user')]//*[contains(.,'%s')]";

    // -------------------- WebElements Locators --------------------
    @FindAll({
            @FindBy(xpath = "//button[contains(@data-ta,'userMenu') or contains(@data-ta,'userMenuButton')"
                    + " or contains(@aria-label,'User menu') or contains(@class,'user-menu')]"),
            @FindBy(xpath = "//button[contains(@aria-label,'User') or contains(@data-ta,'user') or contains(@class,'profile')]"),
            @FindBy(xpath = "//div[contains(@class,'profile') and contains(@class,'dropdown')]//button"),
            @FindBy(xpath = "//button[@aria-label='Profile menu']")
    })
    List<WebElement> userMenuButtonCandidates;

    @FindAll({
            @FindBy(xpath = "//*[self::button or self::a][contains(normalize-space(),'Request as someone else')]"),
            @FindBy(xpath = "//button[contains(text(),'Request as someone else')]"),
            @FindBy(xpath = "//a[contains(text(),'Request as someone else')]"),
            @FindBy(xpath = "//*[contains(@class,'menu-item') or contains(@class,'dropdown-item')][contains(.,'Request as someone else')]"),
            @FindBy(xpath = "//*[@role='menuitem'][contains(.,'Request as someone else')]")
    })
    List<WebElement> requestAsMenuItemCandidates;

    @FindBy(xpath = "//*[contains(@class,'modal') or contains(@class,'dialog')]//*[contains(.,'Request as someone else')]")
    WebElement dialogTitle;

    @FindBy(xpath = "//input[contains(@placeholder,'Search for people')]")
    WebElement searchInputForPeople;

    @FindBy(xpath = "//div[contains(@class,'modal')]")
    WebElement modalAny;

    @FindBy(xpath = "//div[@role='dialog']")
    WebElement dialogRoleAny;

    @FindBy(xpath = "//div[contains(@class,'modal') and contains(@class,'show')]")
    WebElement modalShow;

    @FindBy(xpath = "//div[@role='dialog' and contains(@class,'show')]")
    WebElement dialogRoleShow;

    @FindBy(xpath = "//div[contains(@class,'modal') or contains(@class,'dialog')]//*[self::button or self::li or self::div][@role='option'"
            + " or contains(@class,'dropdown-item') or contains(@class,'typeahead') or contains(@class,'option') or contains(@class,'suggestion')]")
    List<WebElement> resultItems;

    @FindBy(xpath = "//div[contains(@class,'modal') or contains(@class,'dialog')]//button[contains(.,'Apply')"
            + " or contains(.,'OK') or contains(.,'Done')]")
    WebElement applyButton;

    @FindBy(xpath = "//header//button[contains(@class,'user') or contains(@class,'profile') or contains(@class,'account')]")
    WebElement anyUserMenuButtonInHeader;

    @FindBy(xpath = "//*[contains(@class,'user-menu') or contains(@class,'profile')]")
    WebElement userMenuArea;

    public RequestAsSomeoneElseDialog() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public RequestAsSomeoneElseDialog openDialogFromUserMenu() {
        log.info("Opening Request as someone else dialog from user menu");
        
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebElement menuButton = findUserMenuButton();
                commonWaits.clickWithoutInterception(menuButton);
                log.info("Clicked user menu button, waiting for menu to appear");
                commonWaits.waitForSometime(2000);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

                WebElement menuItem = null;
                if (requestAsMenuItemCandidates != null) {
                    for (WebElement candidate : requestAsMenuItemCandidates) {
                        if (menuItem == null && commonWaits.isElementPresent(candidate, 3)) {
                            menuItem = candidate;
                            log.info("Found 'Request as someone else' menu item");
                        }
                    }
                }
                
                if (menuItem == null) {
                    log.warn("'Request as someone else' menu item not found, attempt " + attempt + " of " + maxRetries);
                    try {
                        captureScreenshot("RequestAsSomeoneElse_MenuItemNotFound_Attempt" + attempt, SolQAConstants.appDWP);
                    } catch (Exception ignored) {}
                    
                    if (attempt == maxRetries) {
                        throw new AssertionError("'Request as someone else' menu item not found after " + maxRetries + " attempts. The feature may not be enabled or available for this user.");
                    }
                    
                    // Close the menu and retry
                    try {
                        menuButton.click(); // Click again to close
                        commonWaits.waitForSometime(1000);
                    } catch (Exception ignored) {}
                    commonWaits.waitForSometime(1000);
                    continue;
                }
                
                commonWaits.clickWithoutInterception(menuItem);
                log.info("Clicked 'Request as someone else' menu item");
                
                // Use existing waitForDialogToOpen method
                waitForDialogToOpen();
                return this; // Success - exit method
                
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                log.warn("StaleElementReferenceException while opening dialog, attempt " + attempt + " of " + maxRetries);
                if (attempt == maxRetries) {
                    throw new AssertionError("Failed to open Request as someone else dialog after " + maxRetries + " attempts due to stale element", e);
                }
                commonWaits.waitForSometime(1000);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            } catch (RuntimeException e) {
                log.warn("RuntimeException while opening dialog, attempt " + attempt + " of " + maxRetries + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    try {
                        captureScreenshot("RequestAsSomeoneElse_OpenDialogFailed", SolQAConstants.appDWP);
                    } catch (Exception ignored) {}
                    throw new AssertionError("Failed to open Request as someone else dialog after " + maxRetries + " attempts: " + e.getMessage(), e);
                }
                commonWaits.waitForSometime(1000);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            }
        }
        return this;
    }

    public RequestAsSomeoneElseDialog removeSelectedUser(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return this;
        }
        log.info("Removing selected user if present: " + userName);
        commonWaits.waitForSometime(2000);
        
        String pillXpath = String.format(SELECTED_USER_PILL_XPATH, userName);
        int maxRetries = 3;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                By pillBy = By.xpath(pillXpath);
                if (commonWaits.isElementPresent(pillBy)) {
                    WebElement pill = findElement(pillBy);
                    // Use predefined selectedUserRemoveButton locator
                    List<WebElement> buttons = pill.findElements(By.xpath(SELECTED_USER_REMOVE_BUTTON_REL_XPATH));
                    if (!buttons.isEmpty()) {
                        commonWaits.clickWithoutInterception(buttons.get(0));
                        commonWaits.waitForSometime(2000);
                        log.info("Removed user: " + userName);
                    }
                }
                return this; // Success - exit method
                
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                log.warn("StaleElementReferenceException while removing user, attempt " + attempt + " of " + maxRetries);
                if (attempt == maxRetries) {
                    log.warn("Could not remove user after " + maxRetries + " attempts, continuing anyway");
                    return this; // Don't fail the test if we can't remove - might not be critical
                }
                commonWaits.waitForSometime(1000);
            }
        }
        return this;
    }

    public RequestAsSomeoneElseDialog searchAndSelectUser(String userName) {
        log.info("Searching for user: " + userName);
        commonWaits.waitForSometime(2000);
        
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebElement input = commonWaits.waitForElementVisible(searchInputForPeople);
                
                // Clear and type
                input.click();
                input.clear();
                input.sendKeys(userName);
                log.info("Typed: " + userName + " (attempt " + attempt + ")");
                
                // Wait for results
                commonWaits.waitForSometime(4000);
                
                selectResult(userName);
                return this; // Success - exit method
                
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                log.warn("StaleElementReferenceException on attempt " + attempt + " of " + maxRetries);
                if (attempt == maxRetries) {
                    throw new AssertionError("Failed to interact with search input after " + maxRetries + " attempts due to stale element", e);
                }
                commonWaits.waitForSometime(1000);
            }
        }
        return this;
    }

    private WebElement findUserMenuButton() {
        commonWaits.waitForSometime(2000); // Give UI time to load
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        if (userMenuButtonCandidates != null) {
            for (WebElement candidate : userMenuButtonCandidates) {
                try {
                    if (commonWaits.isElementPresent(candidate, 3)) {
                        log.info("Found user menu button using fallback candidate");
                        return candidate;
                    }
                } catch (Exception ignored) {}
            }
        }

        // Final attempt: try any button in header area that might be the user menu
        try {
            if (commonWaits.isElementPresent(anyUserMenuButtonInHeader, 3)) {
                log.info("Found user menu button using fallback locator");
                return anyUserMenuButtonInHeader;
            }
        } catch (Exception ignored) {}
        
        throw new AssertionError("User menu button not found to open Request as someone else dialog. Tried multiple locators.");
    }

    private void waitForDialogToOpen() {
        log.info("Waiting for Request as someone else dialog to open...");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.waitForSometime(2000); // Give time for dialog to appear

        boolean modalOpened =
                commonWaits.isElementPresent(searchInputForPeople, 3)
                        || commonWaits.isElementPresent(dialogTitle, 3)
                        || commonWaits.isElementPresent(modalAny, 3)
                        || commonWaits.isElementPresent(dialogRoleAny, 3)
                        || commonWaits.isElementPresent(modalShow, 3)
                        || commonWaits.isElementPresent(dialogRoleShow, 3);
        
        if (!modalOpened) {
            try {
                captureScreenshot("RequestAsSomeoneElse_DialogFailedToOpen", SolQAConstants.appDWP);
            } catch (Exception ignored) {}
            throw new AssertionError("Request as someone else dialog failed to open - could not detect dialog or search input");
        }
        
        // Additional wait for dialog to stabilize
        commonWaits.waitForSometime(2000);
        log.info("Dialog is ready for interaction");
    }

    private void selectResult(String userName) {
        log.info("Selecting result for: " + userName);
        commonWaits.waitForSometime(2000);
        
        String specificUserXpath = String.format(RESULT_ITEM_BY_TEXT_XPATH, userName);
        By specificUserLocator = By.xpath(specificUserXpath);
        List<WebElement> results = null;
        
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // First try to find specific user by name
                results = findElements(specificUserLocator);
                if (!results.isEmpty()) {
                    log.info("Found specific user match: " + userName);
                } else {
                    // Fall back to general result items
                    results = resultItems;
                    if (!results.isEmpty()) {
                        log.info("Found " + results.size() + " general result(s)");
                    }
                }
                
                if (results.isEmpty()) {
                    // Try clicking ENTER as last resort
                    log.info("No results found, trying ENTER");
                    searchInputForPeople.sendKeys(Keys.ENTER);
                    commonWaits.waitForSometime(2000);
                    results = resultItems;
                }
                
                if (!results.isEmpty()) {
                    log.info("Found " + results.size() + " results, clicking first matching user: " + userName);
                    commonWaits.clickWithoutInterception(results.get(0));
                    log.info("Clicked on user name: " + userName);
                    
                    // Wait for dialog to close and selection to apply
                    commonWaits.waitForSometime(2000);
                    
                    // Verify dialog is closing/closed
                    for (int i = 0; i < 5; i++) {
                        if (!commonWaits.isElementPresent(modalAny, 1)) {
                            log.info("Dialog closed successfully");
                            break;
                        }
                        log.info("Waiting for dialog to close... (" + (i+1) + "/5)");
                        commonWaits.waitForSometime(1000);
                    }
                    
                    // Additional wait for the user selection to apply to page context
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                    commonWaits.waitForSometime(2000);
                    log.info("User selected: " + userName + ", dialog closed, selection should be active");
                    break; // Success - exit retry loop
                    
                } else if (attempt == maxRetries) {
                    String errorMsg = "No search results found for user: " + userName + ". User may not exist or is not searchable.";
                    log.error(errorMsg);
                    try {
                        captureScreenshot("UserNotFound_" + userName.replace(" ", "_"), SolQAConstants.appDWP);
                    } catch (Exception ignored) {}
                    throw new AssertionError(errorMsg);
                }
                
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                log.warn("StaleElementReferenceException while selecting result, attempt " + attempt + " of " + maxRetries);
                if (attempt == maxRetries) {
                    throw new AssertionError("Failed to select user result after " + maxRetries + " attempts due to stale element", e);
                }
                commonWaits.waitForSometime(1000);
            }
        }
        
        // Optional: Use predefined applyButton locator if dialog doesn't close
        log.info("Checking if Apply button exists (optional)...");
        try {
            if (commonWaits.isElementPresent(applyButton, 2)) {
                log.info("Found Apply button, clicking it");
                commonWaits.clickWithoutInterception(applyButton);
                log.info("Clicked Apply button");
                commonWaits.waitForSometime(2000);
            }
        } catch (Exception e) {
            log.info("No Apply button found or needed - user was selected directly by clicking name");
        }
        
        log.info("Done selecting user: " + userName);
    }
    
    /**
     * Check if "Request as someone else" feature is available
     * @return true if feature is available, false otherwise
     */
    public boolean isRequestAsSomeoneElseAvailable() {
        log.info("Checking if 'Request as someone else' feature is available");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        
        try {
            WebElement menuButton = findUserMenuButton();
            commonWaits.clickWithoutInterception(menuButton);
            commonWaits.waitForSometime(2000);

            boolean available = false;
            if (requestAsMenuItemCandidates != null) {
                for (WebElement candidate : requestAsMenuItemCandidates) {
                    if (!available && commonWaits.isElementPresent(candidate, 2)) {
                        available = true;
                    }
                }
            }
            
            // Close the menu
            menuButton.click();
            commonWaits.waitForSometime(1000);
            
            log.info("'Request as someone else' feature available: " + available);
            return available;
            
        } catch (Exception e) {
            log.warn("Error checking if 'Request as someone else' is available: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verify that the correct user is currently selected for "Request as someone else"
     * This should be called after the dialog has been closed and user returned to the main page
     * @param expectedUserName The expected user name that should be selected
     */
    public RequestAsSomeoneElseDialog verifySelectedUser(String expectedUserName) {
        log.info("Verifying selected user is: " + expectedUserName);
        commonWaits.waitForSometime(3000); // Wait longer for selection to apply
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        
        // Try to find indication that user is selected - could be in user menu, header, or banner
        String badgeXpath = String.format(USER_BADGE_BY_NAME_XPATH, expectedUserName);
        String requestingForXpath = String.format(REQUESTING_FOR_BY_NAME_XPATH, expectedUserName);
        String headerContainsXpath = String.format(HEADER_CONTAINS_NAME_XPATH, expectedUserName);
        String bannerContainsXpath = String.format(BANNER_CONTAINS_NAME_XPATH, expectedUserName);
        String userMenuAreaContainsXpath = String.format(USER_MENU_AREA_CONTAINS_NAME_XPATH, expectedUserName);
        String genericSelectedUserContainsXpath = String.format(GENERIC_SELECTED_USER_CONTAINS_NAME_XPATH, expectedUserName);
        By[] userIndicators = new By[] {
            // Check for badge/pill showing selected user
            By.xpath(badgeXpath),
            // Check for "Requesting for" or "Request for" label with user name
            By.xpath(requestingForXpath),
            // Check in header area
            By.xpath(headerContainsXpath),
            // Check for banner or notification
            By.xpath(bannerContainsXpath),
            // Check near user menu
            By.xpath(userMenuAreaContainsXpath),
            // Generic check
            By.xpath(genericSelectedUserContainsXpath)
        };
        
        boolean userVerified = false;
        for (By indicator : userIndicators) {
            if (commonWaits.isElementPresent(indicator, 3)) {
                log.info("✓ Verified user selection: " + expectedUserName + " is displayed on page");
                userVerified = true;
                break;
            }
        }
        
        if (!userVerified) {
            log.warn("Could not verify '" + expectedUserName + "' is displayed on page after selection");
            log.warn("The selection may still be active in the backend, checking user menu...");
            
            // Try opening user menu to check if selection is there
            try {
                WebElement menuButton = findUserMenuButton();
                commonWaits.clickWithoutInterception(menuButton);
                commonWaits.waitForSometime(1000);
                
                String menuText = menuButton.getText();
                if (menuText != null && menuText.contains(expectedUserName)) {
                    log.info("✓ Found user selection in user menu: " + expectedUserName);
                    userVerified = true;
                }
                
                // Close menu
                menuButton.click();
                commonWaits.waitForSometime(500);
            } catch (Exception e) {
                log.warn("Could not check user menu: " + e.getMessage());
            }
        }
        
        if (!userVerified) {
            log.warn("User '" + expectedUserName + "' is not visibly displayed on the page after selection");
            log.warn("This may be normal behavior - some implementations don't show the selected user until request submission");
            try {
                captureScreenshot("UserSelection_NotVisibleOnPage_" + expectedUserName.replace(" ", "_"), SolQAConstants.appDWP);
            } catch (Exception ignored) {}
            throw new AssertionError("User selection could not be verified on page. Expected user '" + expectedUserName + "' to be displayed after selection from 'Request as someone else' dialog. The selection may still be active in the backend.");
        }
        
        log.info("✓ User selection successfully verified on page: " + expectedUserName);
        return this;
    }
    
    /**
     * Log current page state for debugging user selection issues
     */
    public RequestAsSomeoneElseDialog logPageStateForDebugging() {
        log.info("=== Logging Page State for Debugging ===");
        try {
            String currentUrl = getDriver().getCurrentUrl();
            log.info("Current URL: " + currentUrl);
            
            String pageTitle = getDriver().getTitle();
            log.info("Page Title: " + pageTitle);
            
            // Check if any modal/dialog is open
            boolean modalPresent = commonWaits.isElementPresent(modalAny, 1);
            log.info("Modal present: " + modalPresent);
            
            // Check for any visible user names or selection indicators
            boolean userMenuPresent = commonWaits.isElementPresent(userMenuArea, 1);
            log.info("User menu present: " + userMenuPresent);
            
            log.info("=== End Page State Debug ===");
        } catch (Exception e) {
            log.warn("Error logging page state: " + e.getMessage());
        }
        return this;
    }
}
