package pages.ITSM.InnovationSuite;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class InnovationSuiteOpsSwarmerTest extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(InnovationSuiteOpsSwarmerTest.class.getName());
    
    CommonWaits commonwaits;
    String smartIT = "SmartIT";
    String HelixGPTAgentStudioApplication = "Skill";


    public InnovationSuiteOpsSwarmerTest() {
        PageFactory.initElements(getDriver(), this);
        log.info("PageFactory initialized InnovationSuiteOpsSwarmerTest elements");
        commonwaits = new CommonWaits();
        log.info("CommonWaits instance created");
    }
    //WebElements locators
    @FindBy(xpath = "//span[contains(.,'Visit deployed application')]")
    WebElement visitDeployedApplicationButton;
    @FindBy(xpath = "//div[@class='application-section']//button/div")
    WebElement applicationlistButton;
    @FindBy(xpath = "//label[contains(.,'Filter')]/..")
    WebElement filterButton;
    @FindBy(xpath = "//input[contains(@id,'search') and @placeholder='Filter']")
    WebElement searchInput;
    @FindBy(xpath = "//button[@role='option']")
    WebElement opsSwarmersappselection;
    @FindBy(xpath = "//span[text()='Ops Swarmer']/../../../..//input[@type='radio']")
    WebElement opsSwarmersradioButton;
    @FindBy(xpath = "//span[text()='Ops Swarmer']/../../../../..//button")
    WebElement opsSwarmersactionButton;
    @FindBy(xpath = "//button[contains(.,'Test')]")
    WebElement opsSwarmerstestButton;
    @FindBy(xpath = "//textarea[@aria-label='Send message']")
    WebElement opsSwarmerstestmessageInput;
    @FindBy(xpath = "(//section[contains(@data-testid,'helixGPTChatMessage')])[last()]")
    WebElement latestMessageGPTResponseElement;
    @FindBy(xpath = "//div[@id='BMC Helix Innovation Suite']")
    WebElement innovationSuiteLink;

    @FindBy(xpath = "//input[@placeholder='Search']")
    WebElement searchskillInput;
    @FindBy(xpath = "//button[contains(@class,'search-button')]")
    WebElement searchskillButton;
    @FindBy(xpath = "//a[text()=' Skill ']/../../../..//div[contains(@class,'ui-chkbox-box')]")
    WebElement skillCheckbox;
    @FindBy(xpath = "//button[@rx-id='edit-data']")
    WebElement editDataButton;
    @FindBy(xpath = "//h4[contains(.,'Data editor (Skill)')]")
    WebElement dataEditorSkillTitle;
    @FindBy(xpath = "//tr[1]/td[9]//span/span")
    WebElement skillIDElement;
    @FindBy(xpath = "//span[contains(.,'New topic')]/..")
    WebElement clearChatButton;

    public boolean waitForInnovationSuiteLinkToBeVisible() {
        try {
            log.info("Waiting for innovation suite link to be visible");
            CommonWaits.fluentWait(innovationSuiteLink);
            log.info("Fluent wait completed for innovation suite link");
            commonWaits.waitForElementVisible(innovationSuiteLink);
            log.info("Innovation suite link is visible");
            return true;
        } catch (Exception e) {
            log.error("Failed to wait for innovation suite link to be visible: " + e.getMessage());
            return false;
        }
    }

    public String searchSkill(String skillName) {
        try {
            commonwaits.waitForElementInvisible_withFluentWait(getDriver(), searchskillInput.toString(), 60);
            log.info("Search skill input is ready");
            searchskillInput.sendKeys(HelixGPTAgentStudioApplication);
            commonwaits.pause();
            log.info("Entered HelixGPT Agent Studio application filter text");
            CommonWaits.fluentWait(searchskillButton);
            log.info("Search skill button is ready");
            searchskillButton.click();
            commonwaits.pause();
            log.info("Clicked search skill button");
            CommonWaits.fluentWait(skillCheckbox);
            log.info("Skill checkbox is ready");
            skillCheckbox.click();
            commonwaits.pause();
            log.info("Clicked skill checkbox");
            CommonWaits.fluentWait(editDataButton);
            log.info("Edit data button is ready");
            commonwaits.waitForElementClickable(editDataButton);
            log.info("Edit data button is clickable");
            editDataButton.click();
            commonwaits.pause();
            log.info("Clicked edit data button");
            commonwaits.pause();
            log.info("Paused after opening data editor");
            CommonWaits.fluentWait(dataEditorSkillTitle);
            log.info("Data editor (Skill) title is visible");
            commonwaits.waitForElementInvisible_withFluentWait(getDriver(), searchskillInput.toString(), 60);
            commonwaits.pause();
            log.info("Search skill input ready for skill name");
            searchskillInput.clear();
            commonwaits.pause();
            log.info("Cleared search skill input");
            searchskillInput.sendKeys(skillName);
            commonwaits.pause();
            log.info("Entered skill name: " + skillName);
            CommonWaits.fluentWait(searchskillButton);
            log.info("Search skill button ready for skill name search");
            searchskillButton.click();
            commonwaits.waitForSometime(5000);
            log.info("Clicked search for skill name");
            commonwaits.pause();
            log.info("Paused after skill name search");
            CommonWaits.fluentWait(skillIDElement);
            log.info("Skill ID element is visible");
            String skillID = skillIDElement.getText();
            log.info("Skill ID is: " + skillID);
            return skillID;
        } catch (Exception e) {
            log.error("Failed to search skill: " + e.getMessage());
            return null;
        }
    }

    public boolean navigateToHelixGPTAgentStudio(String innovationSuiteURL) {
        try {
            getDriver().get(innovationSuiteURL);
            log.info("Navigated to Innovation Suite URL");
            commonwaits.waitForSometime(10000);
            CommonWaits.fluentWait(visitDeployedApplicationButton);
            log.info("Fluent wait completed for Visit deployed application button");
            commonwaits.waitForSometime(10000);
            commonWaits.waitForElementVisible(visitDeployedApplicationButton);
            log.info("Visit deployed application button is visible");
            commonWaits.pause();
            return true;
        } catch (Exception e) {
            log.error("Failed to navigate to HelixGPT Agent Studio (visit deployed application): " + e.getMessage());
            return false;
        }
    }

    public boolean loadInnovationSuiteapplication(String innovationSuiteURL) {
        try {
            getDriver().get(innovationSuiteURL);
            log.info("Navigated to Innovation Suite application URL");
            CommonWaits.fluentWait(applicationlistButton);
            log.info("Fluent wait completed for application list button");
            commonWaits.waitForElementVisible(applicationlistButton, 60);
            log.info("Application list button is visible");
        } catch (Exception e) {
            log.error("Failed to load innovation suite application: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean searchOpsSwarmers(String opsSwarmers) {
        try {
            CommonWaits.fluentWait(applicationlistButton);
            log.info("Fluent wait completed for application list button (Ops Swarmers flow)");
            commonWaits.waitForElementVisible(applicationlistButton);
            log.info("Application list button is visible (Ops Swarmers flow)");
            commonWaits.pureSafeClick(applicationlistButton);
            log.info("Clicked application list button");
            CommonWaits.fluentWait(filterButton);
            log.info("Filter button is ready");
            commonWaits.pureSafeClick(filterButton);
            log.info("Clicked filter button");
            CommonWaits.fluentWait(searchInput);
            log.info("Search input is ready");
            searchInput.sendKeys(opsSwarmers);
            log.info("Entered Ops Swarmers filter text: " + opsSwarmers);
            searchInput.sendKeys(Keys.ENTER);
            log.info("Pressed Enter on search input");

            CommonWaits.fluentWait(opsSwarmersappselection);
            log.info("Ops Swarmers app option is ready");
            opsSwarmersappselection.click();
            log.info("Selected Ops Swarmers app");
            CommonWaits.fluentWait(opsSwarmersradioButton);
            log.info("Ops Swarmers radio button is ready");
            opsSwarmersradioButton.click();
            log.info("Selected Ops Swarmers radio");
            CommonWaits.fluentWait(opsSwarmersactionButton);
            log.info("Ops Swarmers action button is ready");
            opsSwarmersactionButton.click();
            log.info("Clicked Ops Swarmers action button");
            CommonWaits.fluentWait(opsSwarmerstestButton);
            log.info("Ops Swarmers Test button is ready");
            opsSwarmerstestButton.click();
            log.info("Clicked Ops Swarmers Test button");

        } catch (Exception e) {
            log.error("Failed to search change: " + e.getMessage());
            return false;
        }
        return true;
    }


    public boolean navigatetoopsSwarmerstestprompt(String url) {
        try {
            getDriver().get(url);
            log.info("Navigated to Ops Swarmer test URL");
            CommonWaits.fluentWait(opsSwarmerstestmessageInput);
            log.info("Send message textarea is ready");
            return true;
        } catch (Exception e) {
            log.error("Failed to navigate to Ops Swarmer test prompt: " + e.getMessage());
            return false;
        }
    }


    public String getopsSwarmersresult(String message) {
        try {
            CommonWaits.fluentWait(clearChatButton);
            log.info("Clear chat button is ready");
            clearChatButton.click();
            commonWaits.waitForSometime(5000);
            log.info("Clicked clear chat button");
            CommonWaits.fluentWait(opsSwarmerstestmessageInput);
            log.info("Send message textarea is ready");
            opsSwarmerstestmessageInput.sendKeys(message);
            commonWaits.waitForSometime(2000);
            log.info("Entered Ops Swarmer message");
            opsSwarmerstestmessageInput.sendKeys(Keys.ENTER);
            log.info("Sent Ops Swarmer message (Enter)");
            commonWaits.waitForSometime(10000);
            log.info("Waited 10s after sending message");
            CommonWaits.fluentWait(latestMessageGPTResponseElement);
            log.info("Latest GPT response section is present");

            final int maxAttempts = 60;
            final String[] loadingLastTexts = {
                    "Executing the query",
                    "Formulating the query",
                    "Analyzing"
            };

            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                log.info("Polling GPT response attempt " + (attempt + 1) + " of " + maxAttempts);
                commonWaits.waitForElementVisible(latestMessageGPTResponseElement);
                log.info("Latest GPT response element is visible");
                String lastText = "";
                List<WebElement> descendants = latestMessageGPTResponseElement.findElements(By.xpath(".//*"));
                for (WebElement el : descendants) {
                    String t = el.getText();
                    if (t != null && !t.trim().isEmpty()) {
                        lastText = t.trim();
                    }
                }
                log.info("Resolved last descendant text for GPT response (may be empty)");
                if (lastText.isEmpty()) {
                    String self = latestMessageGPTResponseElement.getText();
                    if (self != null && !self.trim().isEmpty()) {
                        lastText = self.trim();
                        log.info("Used root GPT response text as last text fallback");
                    }
                }

                boolean stillLoading = false;
                for (String phrase : loadingLastTexts) {
                    if (phrase.equals(lastText)) {
                        stillLoading = true;
                        break;
                    }
                }
                if (stillLoading) {
                    log.info("GPT response still in progress (last text: \"" + lastText + "\"); waiting 15s before retry");
                    commonWaits.waitForSometime(15000);
                    log.info("Finished 15s wait; retrying GPT response poll");
                    continue;
                }
                String full = latestMessageGPTResponseElement.getText();
                log.info("GPT response finished loading; returning full response text");
                return full == null ? "" : full.trim();
            }
            log.warn("Exceeded max attempts waiting for GPT response to leave loading state");
            String full = latestMessageGPTResponseElement.getText();
            log.info("Returning GPT response text after max attempts");
            return full == null ? "" : full.trim();
        } catch (Exception e) {
            log.error("Failed to get ops swarmer result: " + e.getMessage());
            return null;
        }
    }



}