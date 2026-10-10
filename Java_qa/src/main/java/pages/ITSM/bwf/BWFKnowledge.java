package pages.ITSM.bwf;

import framework.BasePage;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.Map;

public class BWFKnowledge extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(BWFKnowledge.class.getName());
    CommonWaits commonWaits;

    public BWFKnowledge() {
        PageFactory.initElements(getDriver(), this);
        commonWaits=new CommonWaits();
    }

    //String locators
    int DEFAULT_WAIT_TIME = 2000;
    String bwfLOBSelection = "//bwf-lob-selection//button";
    String selectLob_fromDrpDown = "//*[@aria-labelledby= 'dropdownMenu']/button[normalize-space()='%s']";
    String knowledgeTemplate = "//div[@role='button' and contains(@aria-label, '%s')]";
    public String dropDownKnowledgeMetadata="//button[@aria-label='%s']";
    public String dropdownValue_xpath = "//button[contains(@class, 'rx-select')]//div[contains(.,'%s')]//parent::button";
    String dropdownOption = "//button[contains(@class,'rx-select__option') and .//adapt-highlight[normalize-space()='%s']]|//button[contains(@class,'rx-select__option') and contains(normalize-space(.),'%s')]";
    String activityCommentExact = "//div[contains(@class,'activities')]//div[contains(@class,'contents') and normalize-space()='%s']";
    String activityCommentContains = "//div[contains(@class,'activities')]//*[contains(normalize-space(.), '%s')]";
    String userRow = "//*[contains(@class,'name') and contains(normalize-space(.),'%s')]";
    String DROP_DOWN_CASE_DATA = "//label[contains(. ,'%s')]/ancestor::adapt-rx-select//button";
    String DROPDOWN_VALUE_XPATH = "//button[contains(@class, 'rx-select')]//div[contains(.,'%s')]//parent::button";
    String statusDropdownOption = "//button[@role='option' and contains(normalize-space(.), '%s')]";

    //WebElement locators
    @FindBy(xpath = "//button[contains(text(),'Use selected Template')]")
    WebElement btn_UseSelectedTemplate;
    @FindBy(xpath = "//*[@rx-view-component-id='5b361975-9765-4f12-8933-28575b3a97ee']//input")
    WebElement txtBox_knowledgeTitle;
    @FindBy(xpath = "//bwf-assign-to-me//button")
    WebElement link_AssignToMe;
    @FindBy(xpath = "//*[@rx-view-component-id='acff2d86-e59e-4d40-9426-b9dd8fa17cb2']//button[contains(. ,'Save')]")
    WebElement btn_saveKnowledge;
    @FindBy(xpath = "//rx-runtime-view-modal[contains(normalize-space(.), 'Update status')]//button[contains(. ,'Save')]")
    WebElement btn_saveStatusTransition;
    @FindBy(xpath = "//*[@rx-view-component-id='710d3f2b-6578-45c1-92d6-0949afb79563']//span")
    WebElement txt_knowledgeArticleID;
    @FindBy(xpath = "//*[@rx-view-component-id='5c11d82c-8269-41fc-a93f-374252adc4c2']//button[contains(. ,'Go to Article')]")
    WebElement btn_ViewArticle;
    @FindBy(xpath = "//button[contains(@class, 'status-transition')]//*[contains(@class, 'btn-content')]//span")
	public WebElement txt_articleStatus;
    @FindBy(xpath = "//button[contains(@class,'nav-link') and contains(normalize-space(.),'Activity')]")
    WebElement activityTab;
    @FindBy(css = ".status-transition")
    WebElement statusTransition;
    @FindBy(xpath = "//button[contains(.,'Save') and not(@disabled)]")
    WebElement saveButton;
    @FindBy(xpath = "//button[contains(.,'Edit') and not(@disabled)]")
    WebElement editArticleButton;
    @FindBy(xpath = "//adapt-button[normalize-space()='Major Edit']")
    WebElement majorEditButton;
    @FindBy(xpath = "//adapt-rx-textfield[contains(. ,'Knowledge Title')]//input")
    WebElement majorEditTitleInput;
    @FindBy(xpath = "//*[@rx-view-component-id='d5280e99-d102-4f5a-a331-b889bbe1407b']//button[contains(normalize-space(.), 'Edit')]")
    WebElement editMetadataButton;
    @FindBy(xpath = "//button[contains(.,'Assign to me') or contains(@aria-label,'Assign')]")
    WebElement assignToMeButton;
    @FindBy(xpath = "//button[normalize-space()='Assign']")
    WebElement assignButton;
    @FindBy(xpath = "//button[@aria-label='Status' or contains(@aria-label,'Status')]")
    WebElement statusDropdown;
    @FindBy(xpath = "//button[contains(@class,'rx-select__toggle')]")
    WebElement statusDropdownFallback;
    @FindBy(xpath = "//rx-runtime-view-modal[contains(normalize-space(.), 'Update status')]")
    WebElement statusTransitionModal;
    @FindBy(xpath = "//*[@rx-view-component-id='6f8e4177-cad6-4d59-9467-074b688aa06e']//button[@aria-label='Status']")
    WebElement updateKnowledgeStatusDropdown;

    public void setLineOfBusiness(String lobName) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(bwfLOBSelection+ "/span"), 20), "LOB selection dropdown is not visible");
        WebElement element = commonWaits.waitForByElementToBeVisible(By.xpath(bwfLOBSelection + "/span"));
        String selectedLOBValue = element.getText().trim();
        log.info("---- Already selected LOB value is - "+ selectedLOBValue);
        if(selectedLOBValue.equalsIgnoreCase(lobName)){
            log.info("-- LOB value is already selected to expected LOB -"+ lobName);
        } else {
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(bwfLOBSelection), 20), "LOB selection dropdown is not visible");
            commonWaits.clickWithoutInterception(By.xpath(bwfLOBSelection));
            Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(selectLob_fromDrpDown, lobName)), 20), "Desired LOB option '" + lobName + "' is not visible in dropdown");
            commonWaits.clickWithoutInterception(By.xpath(String.format(selectLob_fromDrpDown, lobName)));
        }
    }

    public String createKnowledgeArticle(Map<Object, String> knowledgeData){
        selectKnowledgeTemplate(knowledgeData.get("ArticleTemplate"));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        setArticleTitle(knowledgeData.get("ArticleTitle"));
        selectFieldsInKnowledgeMetadata("Knowledge Set",knowledgeData.get("KnowledgeSet"));
        clickOnAssignToMeButton();
        selectFieldsInKnowledgeMetadata("Category Tier 1",knowledgeData.get("TIER1"));
        selectFieldsInKnowledgeMetadata("Category Tier 2",knowledgeData.get("TIER2"));
        selectFieldsInKnowledgeMetadata("Category Tier 3",knowledgeData.get("TIER3"));
        selectFieldsInKnowledgeMetadata("Region",knowledgeData.get("REGION"));
        selectFieldsInKnowledgeMetadata("Site Group",knowledgeData.get("SITE_GROUP"));
        selectFieldsInKnowledgeMetadata("Site",knowledgeData.get("SITE"));
        save_Knowledge();
        String knowledgeArticleID = getKAIDFieldValueOnPreview();
        clickonViewFullArticle();
        return knowledgeArticleID;
    }

    public BWFKnowledge selectKnowledgeTemplate(String templateName){
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(By.xpath(String.format(knowledgeTemplate, templateName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.clickWithoutInterception(btn_UseSelectedTemplate);
        return this;
    }

    public void setArticleTitle(String titleName) {
        commonWaits.waitForElementClickable(txtBox_knowledgeTitle).sendKeys(titleName + " "+ RandomStringUtils.randomAlphanumeric(4));
    }

    public void selectFieldsInKnowledgeMetadata(String labelName, String value) {
        commonWaits.waitForSometime(commonWaits.moreDelay);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropDownKnowledgeMetadata, labelName)), 20), "Dropdown for '" + labelName + "' is not visible");
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropDownKnowledgeMetadata, labelName)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownValue_xpath, value)), 20), "Desired option '" + value + "' is not visible in dropdown for '" + labelName + "'");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownValue_xpath, value))).click();
    }

    public void clickOnAssignToMeButton() {
        commonWaits.clickWithoutInterception(link_AssignToMe);
    }

    public void save_Knowledge() {
        commonWaits.clickWithoutInterception(btn_saveKnowledge);
    }

    public String getKAIDFieldValueOnPreview() {
        return commonWaits.waitForElementVisible(txt_knowledgeArticleID).getText().trim();
    }

    public void clickonViewFullArticle(){
        commonWaits.clickWithoutInterception(btn_ViewArticle);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }

    public String getArticleTransitionStatus() {
        commonWaits.waitForPageReadyState();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), txt_articleStatus, 20), "Article status is not visible");
        String statusValue = commonWaits.waitForElementVisible(txt_articleStatus).getText().trim();
        log.info("******The status is : " + statusValue.trim());
        return statusValue;
    }

    public void changeArticleStatusTo(String status) {
        commonWaits.clickWithoutInterception(txt_articleStatus);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropDownKnowledgeMetadata, "Status")), 10), "Status dropdown is not visible");
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownKnowledgeMetadata, "Status"))).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(dropdownValue_xpath, status)), 10), "Desired status option '" + status + "' is not visible in dropdown");
        commonWaits.clickWithoutInterception(By.xpath(String.format(dropdownValue_xpath, status)));
        log.info("---Selected '" + status + "' for Status Dropdown");
        commonWaits.waitForElementVisible(saveButton).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForSometime(commonWaits.moreDelay);
    }

    public BWFKnowledge verifyStatus(String expectedStatus) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusTransition, 20), "Status transition button is not visible");
        String actual = commonWaits.waitForElementVisible(statusTransition).getText().trim();
        Assert.assertEquals(actual, expectedStatus, "Knowledge article status mismatch");
        return this;
    }

    public BWFKnowledge setExternalMetadata(String externalValue) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        log.info("Setting External metadata to: " + externalValue);
        if (commonWaits.isElementPresent(editMetadataButton)) {
            commonWaits.clickWithoutInterception(editMetadataButton);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        setDropDownValues("External", externalValue);

        // Save (metadata blade save)
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(saveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFKnowledge setDropDownValues(String labelName, String value) {
        try {
            commonWaits.waitForSometime(DEFAULT_WAIT_TIME);
            commonWaits.clickWithoutInterception(By.xpath(String.format(DROP_DOWN_CASE_DATA, labelName)));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DROPDOWN_VALUE_XPATH, value))).click();
            log.info("Successfully set dropdown " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown value for " + labelName + ": " + e.getMessage());
            throw new RuntimeException("Failed to set dropdown value for " + labelName, e);
        }
        return this;
    }

    public BWFKnowledge majorEditCreateVersion(String newTitle, boolean publishApproval, String assignToUserDisplayText) {
        log.info("Creating major edit version with title: " + newTitle);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Open edit mode and choose Major Edit
        commonWaits.clickWithoutInterception(editArticleButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.clickWithoutInterception(majorEditButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        // Set title
        commonWaits.waitForElementVisible(majorEditTitleInput).clear();
        majorEditTitleInput.sendKeys(newTitle);

        // Save major edit
        commonWaits.clickWithoutInterception(saveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);

        if (publishApproval) {
            setStatus(SolQAConstants.PUBLISH_APPROVAL);
            if (assignToUserDisplayText != null && !assignToUserDisplayText.isBlank()) {
                assignToMe(assignToUserDisplayText);
            }
            saveStatus();
        }
        return this;
    }

    public BWFKnowledge setStatus(String status) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusTransition, 20), "Status transition button did not appear");
        commonWaits.clickWithoutInterception(statusTransition);
        commonWaits.waitForSometime(DEFAULT_WAIT_TIME);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), statusTransitionModal, 20), "Status transition modal did not appear");
        commonWaits.waitForElementVisible(updateKnowledgeStatusDropdown, 20).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(statusDropdownOption, status), 20), "Status transition modal did not appear");
        commonWaits.clickWithoutInterception(By.xpath(String.format(statusDropdownOption, status)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForPageReadyState();
        return this;
    }

    public BWFKnowledge assignToMe(String userDisplayText) {
        try {
            commonWaits.clickWithoutInterception(assignToMeButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
            commonWaits.clickWithoutInterception(By.xpath(String.format(userRow, userDisplayText)));
            commonWaits.clickWithoutInterception(assignButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        } catch (Exception e) {
            log.info("Assign-to-me selection skipped/failed: " + e.getMessage());
        }
        return this;
    }

    public BWFKnowledge saveStatus() {
        commonWaits.clickWithoutInterception(saveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForPageReadyState();
        return this;
    }

    public BWFKnowledge openActivityTab() {
        commonWaits.clickWithoutInterception(activityTab);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public BWFKnowledge verifyCommentPresent(String expectedText) {
        openActivityTab();
        Assert.assertTrue(commonWaits.isElementPresent(By.xpath(String.format(activityCommentExact, expectedText)))
                        || commonWaits.isElementPresent(By.xpath(String.format(activityCommentContains, expectedText))),
                "Expected comment not found in knowledge article activity: " + expectedText);
        return this;
    }

    public BWFKnowledge verifyCommentNotPresent(String text) {
        openActivityTab();
        Assert.assertFalse(commonWaits.isElementPresent(By.xpath(String.format(activityCommentExact, text)))
                        || commonWaits.isElementPresent(By.xpath(String.format(activityCommentContains, text))),
                "Unexpected comment is present in knowledge article activity: " + text);
        return this;
    }

    public BWFKnowledge refresh() {
        getDriver().navigate().refresh();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForPageReadyState();
        return this;
    }

    public BWFKnowledge setArticleTitleExact(String title) {
        commonWaits.waitForElementClickable(txtBox_knowledgeTitle);
        txtBox_knowledgeTitle.clear();
        txtBox_knowledgeTitle.sendKeys(title);
        return this;
    }

    /**
     * Creates a knowledge article using an exact title (no random suffix).
     * Returns the created Article ID.
     */
    public String createKnowledgeArticleWithExactTitle(Map<Object, String> knowledgeData, String exactTitle) {
        selectKnowledgeTemplate(knowledgeData.get("ArticleTemplate"));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        setArticleTitleExact(exactTitle);
        selectFieldsInKnowledgeMetadata("Knowledge Set", knowledgeData.get("KnowledgeSet"));
        clickOnAssignToMeButton();
        selectFieldsInKnowledgeMetadata("Category Tier 1", knowledgeData.get("TIER1"));
        selectFieldsInKnowledgeMetadata("Category Tier 2", knowledgeData.get("TIER2"));
        selectFieldsInKnowledgeMetadata("Category Tier 3", knowledgeData.get("TIER3"));
        selectFieldsInKnowledgeMetadata("Region", knowledgeData.get("REGION"));
        selectFieldsInKnowledgeMetadata("Site Group", knowledgeData.get("SITE_GROUP"));
        selectFieldsInKnowledgeMetadata("Site", knowledgeData.get("SITE"));
        save_Knowledge();
        String knowledgeArticleID = getKAIDFieldValueOnPreview();
        clickonViewFullArticle();
        return knowledgeArticleID;
    }

}