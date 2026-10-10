package pages.ITSM.dwp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class PreferencePage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    // String locators
    String userInDropdown = "//span[contains(text(),'%s')]";
    String approverInList = "//*[contains(text(),'%s')]";
    String removeApproverButton = "//*[contains(text(),'%s')]/ancestor::*[contains(@class,'list-item') or contains(@class,'row')]//button[contains(@class,'delete') or contains(@aria-label,'Remove') or contains(text(),'Remove')]";

    // WebElements Locators
    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    WebElement profileMenuButton;

    @FindBy(xpath = "//button[contains(text(),'Preferences')]")
    WebElement preferencesMenuItem;

    @FindBy(xpath = "//button[contains(text(),' Manage approvers ')]")
    WebElement manageApproversButton;

    @FindBy(xpath = "//button[contains(text(),' Add approver ' )]")
    WebElement addApproverButton;

    @FindBy(xpath = "//input[@placeholder='Search for people']")
    WebElement alternateApproverUsernameField;

    @FindBy(xpath = "//input[@name='endDate']")
    WebElement endDateField;

    @FindBy(xpath = "//button[contains(text(),'Save')]")
    WebElement saveApproverButton;

    @FindBy(xpath = "//button[contains(text(),'Close')]")
    WebElement closeDialogButton;

    @FindBy(xpath = "//button[contains(text(),'Confirm') or contains(text(),'Yes') or contains(text(),'Delete')]")
    WebElement confirmButton;

    @FindBy(xpath = "//button[@data-ta='collaboratorSettingsDeleteButton'] | //section[contains(normalize-space(.), 'Collaborators')]//button[@aria-label='Delete']")
    public WebElement deleteCollaboratorButton;

    public PreferencePage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(PreferencePage.class.getName());
        commonWaits = new CommonWaits();
    }

    /**
     * Navigate to user preferences from profile menu
     */
    public PreferencePage navigateToPreferences() {
        log.info("Navigating to User Preferences");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            // Click profile icon/menu
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), profileMenuButton, 30), "Profile menu button is not visible");
            commonWaits.clickByJS(profileMenuButton);
            log.info("Clicked on profile menu");

            // Wait and click Preferences
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), preferencesMenuItem, 30), "Preferences menu item is not visible");
            commonWaits.clickByJS(preferencesMenuItem);
            log.info("Clicked on Preferences menu item");

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        } catch (Exception e) {
            log.error("Failed to navigate to preferences: " + e.getMessage());
            Assert.fail("Unable to navigate to user preferences");
        }
        return this;
    }

    /**
     * Click on Manage Approvers button/link
     */
    public PreferencePage manageAlternateApprovers() {
        log.info("Opening Manage Alternate Approvers");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            // Click Manage Approvers button
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), manageApproversButton, 30), "Manage Approvers button is not visible");
            commonWaits.clickByJS(manageApproversButton);
            log.info("Clicked on Manage Approvers button");

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        } catch (Exception e) {
            log.error("Failed to open manage approvers: " + e.getMessage());
            Assert.fail("Unable to open manage alternate approvers");
        }
        return this;
    }

    /**
     * Add an alternate approver
     *
     * @param userName - Username of the alternate approver
     * @param endDate  - End date for the alternate approver assignment (format: MM/dd/yyyy)
     */
    public PreferencePage addAlternateApprover(String userName, String endDate) {
        log.info("Adding alternate approver: " + userName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            //Check if the user is already added
            if (verifyAlternateApproverAdded(userName)) {
                log.info("Alternate approver already added: " + userName);
                return this;
            }

            // Click Add Approver button if present
            try {
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addApproverButton, 30), "Add Approver button is not visible");
                commonWaits.clickByJS(addApproverButton);
                log.info("Clicked on Add Approver button");
            } catch (Exception e) {
                log.info("Add Approver button not found or not needed, continuing...");
            }

            // Enter username
            commonWaits.waitForElementVisible(alternateApproverUsernameField);
            alternateApproverUsernameField.clear();
            alternateApproverUsernameField.sendKeys(userName);
            log.info("Entered username: " + userName);

            // Try to select from dropdown if present
            try {
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(userInDropdown, userName)), 10),
                        "User dropdown did not appear for username: " + userName);
                WebElement userOption = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(userInDropdown, userName)));
                commonWaits.clickByJS(userOption);
                log.info("Selected user from dropdown: " + userName);
            } catch (Exception e) {
                log.info("No dropdown selection needed for username");
            }
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

            // Enter end date
            try {
                commonWaits.waitForElementVisible(endDateField);
                endDateField.clear();
                endDateField.sendKeys(endDate);
                log.info("Entered end date: " + endDate);
            } catch (Exception e) {
                log.info("End date field not present or not required");
            }

            // Click Save/Submit button
            commonWaits.waitForElementVisible(saveApproverButton);
            commonWaits.clickByJS(saveApproverButton);
            log.info("Clicked on Save button");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

            // Close dialog if present
            try {
                commonWaits.clickByJS(closeDialogButton);
                log.info("Closed the dialog");
            } catch (Exception e) {
                log.info("No close button found or dialog auto-closed");
            }

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Successfully added alternate approver: " + userName);
            return this;
        } catch (Exception e) {
            log.error("Failed to add alternate approver: " + e.getMessage());
            try {
                captureScreenshot("AddAlternateApproverError", SolQAConstants.appDWP);
            } catch (Exception ex) {
                log.error("Failed to capture screenshot: " + ex.getMessage());
            }
            Assert.fail("Unable to add alternate approver: " + userName);
            return this;
        }
    }

    /**
     * Verify alternate approver is added successfully
     *
     * @param userName - Username to verify
     * @return true if approver is found in the list
     */
    public boolean verifyAlternateApproverAdded(String userName) {
        log.info("Verifying alternate approver is added: " + userName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        boolean isPresent = commonWaits.isElementPresent(By.xpath(String.format(approverInList, userName)));

        if (isPresent) {
            log.info("Alternate approver verified in list: " + userName);
        } else {
            log.error("Alternate approver not found in list: " + userName);
        }

        return isPresent;
    }

    String manageAlternateApproversMoreOption = "//dwp-approval-item[contains(normalize-space(.), '%s')]//button[@id='kabobMenuOptionsDropdown']";
    @FindBy(xpath = "//button[normalize-space()='Cancel alternate approver']")
    WebElement cancelAlternateApproverButton;
    @FindBy(xpath = "//dwp-confirmation-dialog//button[contains(normalize-space(.), 'Yes')]")
    WebElement confirmCancelAlternateApproverButton;
    public PreferencePage removeAlternateApproverIfPresent(String userName) {
        log.info("Checking if alternate approver is present for removal: " + userName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (verifyAlternateApproverAdded(userName)) {
            if (commonWaits.isElementPresent(By.xpath(String.format(manageAlternateApproversMoreOption, userName)))) {
                log.info("Alternate approver found, attempting to remove: " + userName);
                commonWaits.clickWithoutInterception(By.xpath(String.format(manageAlternateApproversMoreOption, userName)));
                Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), cancelAlternateApproverButton, 5),
                        "Cancel alternate approver button did not appear for user: " + userName);
                commonWaits.clickByJS(cancelAlternateApproverButton);
                log.info("Clicked cancel alternate approver button for: " + userName);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                if (commonWaits.isElementPresent(confirmCancelAlternateApproverButton)) {
                    commonWaits.clickWithoutInterception(confirmCancelAlternateApproverButton);
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
                }
                log.info("Successfully removed alternate approver: " + userName);
            } else {
                log.info("Remove option not available for alternate approver: " + userName);
            }
        }
        return this;
    }

    /**
     * Remove alternate approver
     *
     * @param userName - Username to remove
     */
    public PreferencePage removeAlternateApprover(String userName) {
        log.info("Removing alternate approver: " + userName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        try {
            // Find and click remove/delete button for the user
            WebElement removeButton = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(removeApproverButton, userName)));
            commonWaits.clickByJS(removeButton);
            log.info("Clicked remove button for: " + userName);

            // Confirm if confirmation dialog appears
            try {
                commonWaits.waitForElementVisible(confirmButton);
                commonWaits.clickByJS(confirmButton);
                log.info("Confirmed removal");
            } catch (Exception e) {
                log.info("No confirmation needed");
            }

            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Successfully removed alternate approver: " + userName);
            return this;
        } catch (Exception e) {
            log.error("Failed to remove alternate approver: " + e.getMessage());
            Assert.fail("Unable to remove alternate approver: " + userName);
            return this;
        }
    }

    public PreferencePage deleteDefaultCollaboratorsIfPresentInPreference() {
        log.info("Deleting default collaborators if present in Preferences");
        

        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), deleteCollaboratorButton , 30 )) {
            commonWaits.clickWithoutInterception(deleteCollaboratorButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            log.info("Default collaborator deleted successfully");
            
        } else {
            log.info("No default collaborators are available to delete");
        }
        return this;
    }

}