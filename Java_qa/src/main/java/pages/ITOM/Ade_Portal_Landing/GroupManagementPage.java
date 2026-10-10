package pages.ITOM.Ade_Portal_Landing;

import framework.BasePage;
import util.CommonWaits;
import util.SolqaLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;

public class GroupManagementPage extends BasePage {

    public SolqaLogger log = new SolqaLogger(GroupManagementPage.class.getName());
    WebDriver driver= null;
    CommonWaits commonWaits;

    public GroupManagementPage(WebDriver driver) {

        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        commonWaits = new CommonWaits();

    }

    @FindBy(xpath="//div[contains(@class,'adapt-table-search')]//input")
    public WebElement search_txt_box;

    @FindBy(xpath = "(//div[contains(@class,'adapt-table-search')]//input)[2]")
    public WebElement user_search_txt_box;

    @FindBy(xpath ="(//table[@class='ui-table-scrollable-body-table'])[2]/tbody/tr[last()]")
    public WebElement userTable;

    public String groupActionLocator = "//div[contains(@class,'dropdown-menu')]/button[contains(.,'%s')]";

    @FindBy(xpath = "//table//tr/td[1]")
    private WebElement group_Entry;

    @FindBy(xpath = "//adapt-table-actions//button[contains(@id, 'dropdownMenu')]")
    private WebElement groupDropdownLocator;

    @FindBy(xpath = "//adapt-inline-editor//span[contains(@class,'editor-text')]")
    private WebElement GroupEditorHeaderLocator;

    @FindBy(xpath = "//div[@class='people-field']//span[contains(@rx-id,'Users-list-on-groupMappedCount')]")
    private WebElement UserAssignedCount;

    @FindBy(xpath = "//div[@rx-id='users-list-table']//table/tbody[@class='ui-table-tbody']//td[3]")
    private WebElement UserDirectoryTable;

    @FindBy(xpath = "//span[@rx-id='Users-list-on-groupMappedCount']")
    private WebElement assignedGroupLink;

    @FindBy(xpath = "//span[contains(text(),'User directory')]")
    private WebElement userDirectoryPage;

    public String MappedUsertable = "//adapt-docked-panel//tbody/tr/td[2]";

    @FindBy(xpath="//div[@class='ui-chkbox-box ui-widget ui-state-default']")
    public WebElement chkSelectUser;

    @FindBy(xpath="//button[@rx-id='confirm-map-user-role-btn']")
    public WebElement btnRemove;

    @FindBy(xpath="//button[@rx-id='delete-group-btn']")
    public WebElement btnDeleteOnModal;

    @FindBy(xpath="//div[@class='a-profile dropdown a-profile--dark a-profile--small ng-star-inserted']/button")
    public WebElement btnLoggedInUserProfile;

    @FindBy(xpath="//button[@id='adapt-profile-menu-item-2']")
    public WebElement btnSignOut;

    @FindBy(xpath="//i[@rx-id='group-close-btn']")
    public WebElement btnClose;

    @FindBy(xpath = "//button[.='Cancel']")
    public WebElement btnCancel;

    @FindBy(xpath="//button[contains(text(),'Delete')]")
    public WebElement btnDeleteGroup;

    public void closeThePanel(){
        driver.navigate().back();
        commonWaits.waitForSometime(commonWaits.moreDelay);
    }

    public void cancelThePanel(){
        commonWaits.waitForElementClickable(btnCancel).click();
        commonWaits.waitForPageReadyState(driver);
        }

    public void EnterGroupNameInSearchBoxAndVerifyEntry(String groupName){
        log.info("----- Enter value in search box and verify the group present in list ");
        commonWaits.retryingFindClick(search_txt_box);
        commonWaits.waitForElementVisible(group_Entry);
        commonWaits.retryingFindClick(search_txt_box);
        commonWaits.waitForElementClickable(search_txt_box).sendKeys(groupName+ Keys.ENTER);
        commonWaits.waitForPageReadyState(driver);
        commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementVisible(group_Entry);
        String searchedGroupName = (group_Entry).getText().trim();
        Assert.assertEquals(searchedGroupName,groupName, "Entered group name is not matched with the searched one");
    }

    public void clickOnDropdownAndSelectAction(String actionName){
        log.info("--- Clicking on dropdown of searched group and selection action as - "+ actionName);
        commonWaits.waitForElementVisible(groupDropdownLocator).click();
        groupActionLocator = String.format(groupActionLocator,actionName);
        commonWaits.waitForElementVisible(findElement(By.xpath(groupActionLocator))).click();
        commonWaits.waitForPageReadyState(driver);
    }

    //getEditorHeader
    public String getEditorHeader(){

        log.info("---- Verify group options editor is opened after clicking on Edit action of group --- ");
        String editorHeader = commonWaits.waitForElementVisible(GroupEditorHeaderLocator).getText().trim();
        return editorHeader;

    }

    public int getUserAssignedCountToGroup(){
        log.info("---- Verify User mapped count to group --- ");
        String userCount = commonWaits.waitForElementVisible(UserAssignedCount).getText().trim();
        String s= userCount.substring(0,userCount.indexOf(" "));
        int count = Integer.parseInt(s);
        log.info("-- User mapped to group are - "+ count);
        return count;

    }

    public void clickingOnAssignedUserCount(){

        log.info("---- Clicking on assigned users count mapped to group -----");
        commonWaits.waitForElementVisible(UserAssignedCount);
        commonWaits.clickWithoutInterception(UserAssignedCount);
        commonWaits.waitForPageReadyState(driver);
    }

    public boolean verifyUsernameFromAssignedList(String userName){

        boolean isPresent = false;
        log.info("-- Get user name from Group assigned list -----");

        commonWaits.waitForElementClickable(user_search_txt_box).clear();
        commonWaits.waitForSometime(commonWaits.minDelay);
        user_search_txt_box.sendKeys(userName+ Keys.ENTER);

        try{
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for(int i=0;i<10;i++){
                js.executeScript("arguments[0].scrollIntoView(true);",userTable);
                commonWaits.waitForSometime(commonWaits.minDelay);
            }
        }catch (Exception e){
            log.info("Scrolling is not working properly");
        }

        try {
            commonWaits.waitForElementVisible(findElement(By.xpath(MappedUsertable)));
        }catch(NoSuchElementException e){
            log.info("No user is assigned to group");
            return false;
        }
        List<WebElement> users = findElements(By.xpath(MappedUsertable));
        for(WebElement user: users){
            String assignedUser = user.getText().trim();
            log.info("--- Assigned username is -"+ assignedUser);
            if(assignedUser.equalsIgnoreCase(userName)){
                isPresent = true;
                break;
            }
        }
        return isPresent;

    }


    public void cleanUPLDAPUser(String User) {
        commonWaits.waitForElementVisible(assignedGroupLink).click();
        Assert.assertTrue(userDirectoryPage.isDisplayed(),"User Directory page is not displayed");
        Assert.assertTrue(UserDirectoryTable.getText().contains(User), "UserTable does not contain mentioned user");
        Assert.assertTrue(chkSelectUser.isDisplayed(),"Select User CheckBox is not displayed on page ");
        chkSelectUser.click();
        commonWaits.waitForElementVisible(btnRemove);
        Assert.assertTrue(btnRemove.isEnabled(),"Remove button is not enabled after selecting user");
        btnRemove.click();
        commonWaits.waitForPageReadyState(driver);
        Assert.assertTrue(assignedGroupLink.getText().contains("0 assigned"),"After removing user from group it is not reflected in Group Options page" );
        commonWaits.waitForPageReadyState(driver);
        btnClose.click();
    }

    public void deleteGroup() {

        commonWaits.waitForElementVisible(groupDropdownLocator).click();
        //   commonWaits.waitForSometime(commonWaits.minDelay);
        commonWaits.waitForElementVisible(btnDeleteGroup).click();
        commonWaits.waitForElementVisible(btnDeleteOnModal);
        Assert.assertTrue(btnDeleteOnModal.isDisplayed(),"Delete button is not displayed on Modal Window");
        btnDeleteOnModal.click();
        commonWaits.waitForPageReadyState(driver);
    }

    public void logOut() {
        btnLoggedInUserProfile.click();
        commonWaits.waitForElementVisible(btnSignOut).click();

    }
}
     