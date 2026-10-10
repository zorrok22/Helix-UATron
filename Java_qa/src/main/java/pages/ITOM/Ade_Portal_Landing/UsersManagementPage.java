package pages.ITOM.Ade_Portal_Landing;

import org.openqa.selenium.*;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import framework.BasePage;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class UsersManagementPage extends BasePage {

    public SolqaLogger log = new SolqaLogger(UsersManagementPage.class.getName());
    WebDriver driver= null;
    CommonWaits commonWaits;

    public UsersManagementPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        commonWaits = new CommonWaits();
    }

    @FindBy(xpath="//div[contains(@class,'adapt-table-search')]//input")
    public WebElement search_txt_box;

    public String fullName_Entry = "//table//tr/td[1]";

    @FindBy(xpath="//adapt-table-actions//button[@id='dropdownMenu']")
    public WebElement userDropdownLocator;
  
    @FindBy(xpath="//button[normalize-space()='Actions']")
    public WebElement actionsButton;
  
    @FindBy(xpath="//button[normalize-space()='User options']")
    public WebElement userOptionsButton;
  
    @FindBy(xpath="//span[text()='Groups']//following-sibling::a//span[text()='Select']")
    public WebElement selectGroups;
  
    @FindBy(xpath="//span[text()='Roles']//following-sibling::a//span[text()='Select']")
    public WebElement selectRoles;
 
    @FindBy(xpath="(//input[@placeholder='Search' and contains(@class,'search__input')])[last()]")
    public WebElement searchUserGroupInput;
  
    @FindBy(xpath="(//input[@placeholder='Search' and contains(@class,'search__input')])[last()]")
    public WebElement searchRolesInput;
  
    @FindBy(xpath="//button[text()='Confirm']")
    public WebElement confirmButton;
    
    @FindBy(xpath="(//span[text()='User has been updated.'])[last()]")
    public WebElement userUpdatedMsg;
  
    @FindBy(xpath="//i[@rx-id='user-close-btn']")
    public WebElement closeButton;
  
    @FindBy(xpath="//button[text()='Cancel']")
    public WebElement cancelButton;
    
    //public String userDropdownLocator = "//adapt-table-actions//button[@id='dropdownMenu']";
    public String userActionLocator = "//div[@class='a-dropdown-window--menu']//button[contains(text(),'%s')]";
    public String user_Entry = "//table//tr/td[normalize-space()='%s']";
    public String matchingCheckbox = "//tbody[@class='ui-table-tbody']//tr[.//div[normalize-space()='%s']]//div[contains(@class,'ui-chkbox-box')]";


    
    /**
     * Function to search and verify the user in the 
     * @param userName
     * @return
     */
    
	public boolean enterUsernameInSearchBoxAndVerifyEntry(String userName) {
		log.info("----- Enter value in search box and verify the user present in list ");
		commonWaits.waitForElementClickable(search_txt_box).clear();

		// Explicitly added following command to clear the existing search list.
		search_txt_box.sendKeys(Keys.ENTER);
		search_txt_box.sendKeys(userName + Keys.ENTER);
        try{
        commonWaits.waitForElementVisible(findElement(By.xpath(String.format(user_Entry, userName))));}
        catch (NoSuchElementException e){
            log.info("Searched user is not present in the list");
        }
		if (commonWaits.isElementPresent(String.format(user_Entry, userName))) {
			return true;
		} else {
			return false;
		}

	}

    public void clickOnDropdownAndSelectAction(String actionName){

        log.info("--- Clicking on dropdown of searched group and selection action as - "+ actionName);
        commonWaits.waitForElementVisible(userDropdownLocator).click();

        userActionLocator = String.format(userActionLocator,actionName);
        commonWaits.waitForElementVisible(findElement(By.xpath(userActionLocator))).click();
        commonWaits.waitForPageReadyState(driver);
    }
    
    /**
     * Function to search and verify fullname of the user
     * @param fullName
     * @return
     */
    
    public boolean enterNameInSearchBoxAndVerifyEntry(String fullName){

        boolean isPresent = false;
        log.info("----- Enter value in search box and verify the user present in list ");

        commonWaits.waitForElementClickable(search_txt_box).clear();
        commonWaits.waitForElementVisible(findElement(By.xpath(fullName_Entry)));
        
        log.info("Full name of user is ---->" + fullName);

        search_txt_box.sendKeys(fullName+ Keys.ENTER);

        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementVisible(findElement(By.xpath(fullName_Entry)));
        List<WebElement> users = findElements(By.xpath(fullName_Entry));
        for(WebElement user: users){
            String assignedUser = user.getText().trim();
            log.info("--- fullname of user is -"+ assignedUser);
            if(assignedUser.equalsIgnoreCase(fullName)){
                isPresent = true;
                break;
            }
        }
        return isPresent;

    }
    
    public UsersManagementPage selectUserGroup(String groupName){
        log.info("--- Start: Selecting user group - " + groupName);
        
        // Step 1: Click on Actions button
        log.info("Step 1: Click on Actions button");
        commonWaits.waitForElementClickable(actionsButton).click();
        
        // Step 2: Click on User Options button
        log.info("Step 2: Click on User Options button");
        commonWaits.waitForElementClickable(userOptionsButton).click();
        
        // Step 3: Click on Select Groups
        log.info("Step 3: Click on Select Groups");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(selectGroups).click();
        
        // Step 4: Type group name in search input
        log.info("Step 4: Type group name in search input: " + groupName);
        typeKeys(searchUserGroupInput, groupName);
        
        // Step 5: Wait till loaded gets disappeared
        log.info("Step 5: Wait for loading to complete");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        
        // Step 6: Check if checkbox is already checked
        String checkboxXpath = String.format(matchingCheckbox, groupName);
        WebElement checkbox = getDriver().findElement(By.xpath(checkboxXpath));
        
        // Check if checkbox is already selected by checking for 'ui-state-active' class
        String checkboxClass = checkbox.getAttribute("class");
        if (checkboxClass != null && checkboxClass.contains("ui-state-active")) {
            log.info("User group '" + groupName + "' is already selected. Skipping selection.");
            
            // Click on Cancel button
            log.info("Click on Confirm button");
            commonWaits.waitForElementClickable(cancelButton).click();
        } else {
            // Step 7: Click on checkbox if not selected
            log.info("Step 7: Selecting user group checkbox for: " + groupName);
            commonWaits.clickWithoutInterception(checkbox);
            
            // Step 8: Click on Confirm button
            log.info("Step 8: Click on Confirm button");
            commonWaits.waitForElementClickable(confirmButton).click();
            
            // Step 9: Check for presence of user updated message
            log.info("Step 9: Verify user updated message appears");
            commonWaits.waitForElementVisible(userUpdatedMsg);
            log.info("User updated message displayed successfully");
            
            // Step 10: Wait for user updated message to disappear
            log.info("Step 10: Wait for user updated message to disappear");
            commonWaits.waitForElementInvisible(userUpdatedMsg);
        }
        
        // Step 11: Click on Close button
        log.info("Step 11: Click on Close button");
        commonWaits.clickWithoutInterception(closeButton);
        
        log.info("--- End: Successfully selected user group - " + groupName);
		return this;
    }
    
    public UsersManagementPage selectUserRole(String roleName){
        log.info("--- Start: Selecting user role - " + roleName);
        
        // Step 1: Click on Actions button
        log.info("Step 1: Click on Actions button");
        commonWaits.waitForElementClickable(actionsButton).click();
        
        // Step 2: Click on User Options button
        log.info("Step 2: Click on User Options button");
        commonWaits.waitForElementClickable(userOptionsButton).click();
        
        // Step 3: Click on Select Roles
        log.info("Step 3: Click on Select Roles");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(selectRoles).click();
        
        // Step 4: Type role name in search input
        log.info("Step 4: Type role name in search input: " + roleName);
        typeKeys(searchRolesInput, roleName);
        
        // Step 5: Wait till loaded gets disappeared
        log.info("Step 5: Wait for loading to complete");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        
        // Step 6: Check if checkbox is already checked
        String checkboxXpath = String.format(matchingCheckbox, roleName);
        WebElement checkbox = getDriver().findElement(By.xpath(checkboxXpath));
        
        // Check if checkbox is already selected by checking for 'ui-state-active' class
        String checkboxClass = checkbox.getAttribute("class");
        if (checkboxClass != null && checkboxClass.contains("ui-state-active")) {
            log.info("User role '" + roleName + "' is already selected. Skipping selection.");
            
            // Click on Cancel button
            log.info("Click on Confirm button");
            commonWaits.waitForElementClickable(cancelButton).click();
        } else {
            // Step 7: Click on checkbox if not selected
            log.info("Step 7: Selecting user role checkbox for: " + roleName);
            commonWaits.clickWithoutInterception(checkbox);
            
            // Step 8: Click on Confirm button
            log.info("Step 8: Click on Confirm button");
            commonWaits.waitForElementClickable(confirmButton).click();
            
            // Step 9: Check for presence of user updated message
            log.info("Step 9: Verify user updated message appears");
            commonWaits.waitForElementVisible(userUpdatedMsg);
            log.info("User updated message displayed successfully");
            
            // Step 10: Wait for user updated message to disappear
            log.info("Step 10: Wait for user updated message to disappear");
            commonWaits.waitForElementInvisible(userUpdatedMsg);
        }
        
        // Step 11: Click on Close button
        log.info("Step 11: Click on Close button");
        commonWaits.clickWithoutInterception(closeButton);
        
        log.info("--- End: Successfully selected user role - " + roleName);
		return this;
    }


}
