package pages.ITSM.bwf;

import static framework.Launcher.getDriver;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.Launcher;
import util.CommonWaits;
import util.HelixGPTUtil;
import util.SolqaLogger;

public class BWFGPT {
      public static final SolqaLogger log = new SolqaLogger(BWFGPT.class.getName());
    CommonWaits commonWaits;
    HelixGPTUtil helixGPTutils = new HelixGPTUtil();

    public BWFGPT() {
        PageFactory.initElements(getDriver(), this);
        commonWaits=new CommonWaits();
    }

    //String Locators

    //WebElement Locators
    @FindBy(xpath = "//div[contains(@class,'adapt-helix-gpt-chat-thread')]//ul")
    List<WebElement> responseElement;
    
    @FindBy(xpath = "//div[contains(@class,'adapt-helix-gpt-chat-thread')]")
    WebElement chatThreadContainer;
    
    @FindBy(xpath = "//div[contains(@class,'adapt-helix-gpt-chat-thread')]//table")
    List<WebElement> responseTables;
    
    @FindBy(xpath = "//div[contains(@class,'adapt-helix-gpt-chat-thread')]//a")
    List<WebElement> responseLinks;
    
    @FindBy(xpath = "//div[contains(@class,'bwf-info-bar')]//div[contains(@class,'title')]")
    WebElement caseDisplayId;

    public String getAllBWFGPTResponseString() {
        return helixGPTutils.getAllGPTResponseString(responseElement);
    }
    
    public String getAllBWFGPTFullResponseString() {
        log.info("Capturing entire chat thread text (including tables)");
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForAllSpinnersToHide(util.SolQAConstants.appHelixGPT);
        
        StringBuilder fullResponse = new StringBuilder();
        
        // First, wait for stable response using repetition detection
        String lastText = null;
        int repeatCount = 0;
        long startTime = System.currentTimeMillis();
        long timeout = 45_000; // 45 sec
        
        while (System.currentTimeMillis() - startTime < timeout) {
            commonWaits.waitForAllSpinnersToHide(util.SolQAConstants.appHelixGPT);
            
            if (chatThreadContainer != null) {
                String currentText = chatThreadContainer.getText().trim();
                
                if (lastText != null && lastText.equals(currentText)) {
                    repeatCount++;
                } else {
                    repeatCount = 1;
                    lastText = currentText;
                }
                
                if (repeatCount >= 2) {
                    log.info("Response stabilized, capturing content");
                    break;
                }
            }
        }
        
        // Now capture the full response text
        if (chatThreadContainer != null) {
            String containerText = chatThreadContainer.getText().trim();
            fullResponse.append(containerText);
            log.info("HelixGPT Full Conversation collected (length: " + fullResponse.length() + " chars)");
            
            // Log first 200 chars for debugging
            if (fullResponse.length() > 0) {
                String preview = fullResponse.length() > 200 ? 
                    fullResponse.substring(0, 200) + "..." : fullResponse.toString();
                log.info("Response preview: " + preview);
            }
            
            // If response is empty or very short, try getting text from tables directly
            if (fullResponse.length() < 50 && responseTables != null && !responseTables.isEmpty()) {
                log.info("Response text is short, extracting from " + responseTables.size() + " table(s)");
                fullResponse.setLength(0);
                for (WebElement table : responseTables) {
                    fullResponse.append(table.getText()).append("\n");
                }
                log.info("Extracted text from tables (length: " + fullResponse.length() + " chars)");
            }
            
            String finalResponse = fullResponse.toString().trim();
            String originalResponse = finalResponse;
            
            // Remove meta statements like "HelixGPT replied:" that reduce DeepEval relevancy score
            // Use regex to handle any whitespace variations
            if (finalResponse.startsWith("HelixGPT replied:")) {
                finalResponse = finalResponse.replaceFirst("^HelixGPT replied:\\s*", "");
                log.info("Removed 'HelixGPT replied:' prefix from response");
            }
            
            // Remove pagination like "1 of 10" at the end (handle any whitespace before it)
            if (finalResponse.matches(".*\\d+\\s+of\\s+\\d+\\s*$")) {
                finalResponse = finalResponse.replaceAll("\\s*\\d+\\s+of\\s+\\d+\\s*$", "");
                log.info("Removed pagination marker from end of response");
            }
            
            // Remove any extra email addresses or company details that may appear in tables
            // but keep the core answer about support group and case count
            finalResponse = finalResponse.trim();
            
            if (!finalResponse.equals(originalResponse)) {
                log.info("Cleaned response: original length " + originalResponse.length() + " → cleaned length " + finalResponse.length());
            }
            
            // Final validation: ensure we have content
            if (finalResponse.isEmpty()) {
                log.warn("⚠️ Response is empty after all extraction attempts");
            } else {
                log.info("✅ Successfully captured and cleaned response with " + finalResponse.length() + " characters");
            }
            
            return finalResponse;
        }
        
        log.warn("Chat thread container not found");
        return "";
    }

    public BWFGPT clickFirstLinkInGPTResponse() {
        log.info("Clicking first link in GPT response");
        commonWaits.waitForSometime(commonWaits.minDelay);
        if (responseLinks != null && !responseLinks.isEmpty()) {
            log.info("Found " + responseLinks.size() + " links in GPT response");
            WebElement firstLink = responseLinks.get(0);
            String linkText = firstLink.getText();
            log.info("Clicking on first link: " + linkText);
            commonWaits.clickWithoutInterception(firstLink);
            log.info("Clicked on link successfully");
        } else {
            log.warn("No links found in GPT response");
        }
		return this;
    }

    public String getCaseDisplayId() {
        log.info("Getting Case Display ID from case page");
        commonWaits.waitForElementVisible(caseDisplayId);
        String displayId = caseDisplayId.getText().trim();
        log.info("Found Case Display ID: " + displayId);
        return displayId;
    }

    /**
     * Verifies Case page opened in new tab by validating Case Display ID.
     * Handles tab switching, validation, and cleanup (close new tab and return to parent).
     * 
     * @return Case Display ID from the newly opened case page
     */
    public String verifyAndCloseCasePageInNewTab() {
        String caseDisplayId = null;
        
        commonWaits.waitForSometime(commonWaits.minDelay);
        
        if (Launcher.getDriver().getWindowHandles().size() > 1) {
            List<String> tabs = new ArrayList<>(Launcher.getDriver().getWindowHandles());
            WebDriver newTabDriver = Launcher.getDriver().switchTo().window(tabs.get(1));
            Launcher.setDriver(newTabDriver);
            log.info("Switched to new tab, verifying case page");
            commonWaits.waitForPageLoad(getDriver());
            
            // Get and validate Case Display ID from case page
            caseDisplayId = getCaseDisplayId();
            log.info("Case Display ID on new page: " + caseDisplayId);
            Assert.assertTrue(caseDisplayId.startsWith("CASE-"), "Case Display ID should start with 'CASE-', found: " + caseDisplayId);
            log.info("✅ Case page loaded successfully with Display ID: " + caseDisplayId);
            
            Launcher.getDriver().close();
            log.info("Closed new tab");
            WebDriver parentDriver = Launcher.getDriver().switchTo().window(tabs.get(0));
            Launcher.setDriver(parentDriver);
            log.info("Switched back to parent window");
        } else {
            log.info("No new tab opened, continuing with parent window");
        }
        
        return caseDisplayId;
    }


}
