package pages.ITOM.HelixGPT_InsightFinder;

import framework.BasePage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Dashboard.DashboardPage;
import pages.ITOM.GrafanaDashboard.Dashboard_FeatureMethods;
import pages.ITOM.GrafanaDashboard.ReportingPage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;


public class InsightFinderFeatureMethods extends BasePage {
    
    static SolqaLogger log = new SolqaLogger(InsightFinderFeatureMethods.class.getName());
    CommonWaits commonWaits;
    WebDriver driver;
    AdePortalLandingPage adePage;
    DashboardPage dashboardPage;
    Dashboard_FeatureMethods dashboardFeatureMethods;
    ReportingPage reportingPage;
    InsightFinderPage insightFinderPage;

    public InsightFinderFeatureMethods() {
    	PageFactory.initElements(getDriver(),this);
        commonWaits = new CommonWaits();
        adePage = new AdePortalLandingPage(getDriver());
        dashboardPage = new DashboardPage(getDriver());
        reportingPage = new ReportingPage();
        dashboardFeatureMethods = new Dashboard_FeatureMethods();
        insightFinderPage = new InsightFinderPage();
    }
   
    public void openInsightFinderPage() {
        log.info("---- Opening Insight Finder ----");
        
        // Check if toggle element is visible before clicking
        commonWaits.clickToElement(dashboardPage.insightFinderButton);
        
        log.info("---- Insight Finder opened successfully ----");
        
        log.info("---- Waiting for Insight Finder drawer panel to load ----");
        commonWaits.waitForElementVisible(insightFinderPage.insightFinderChatboxContainer, 30);
        
        log.info("---- Verifying Insight Finder drawer elements ----");
        Assert.assertTrue(isVisible(insightFinderPage.insightFinderChatboxContainer),
                "Insight Finder chatbox container is not visible");
        Assert.assertTrue(isVisible(insightFinderPage.insightFinderDrawerTitle),
                "Insight Finder drawer title is not visible");
        log.info("---- Insight Finder drawer and title are visible ----");
        
        commonWaits.waitForElementVisible(insightFinderPage.chatWelcomeGreeting, 20);
        Assert.assertTrue(insightFinderPage.chatWelcomeGreeting.getText().trim().contains("Hello"),
                "Greeting line should contain Hello. Actual: " + insightFinderPage.chatWelcomeGreeting.getText());
        log.info("---- Greeting line visible: " + insightFinderPage.chatWelcomeGreeting.getText().trim() + " ----");
        
        commonWaits.waitForElementVisible(insightFinderPage.chatWelcomeMsg, 20);
        String actualWelcomeMsg = insightFinderPage.chatWelcomeMsg.getText().trim();
        Assert.assertEquals(actualWelcomeMsg, "How can I help you today?",
                "Chat Welcome message is not as expected. Actual: " + actualWelcomeMsg);
        log.info("---- Chat Welcome message verified: " + actualWelcomeMsg + " ----");
        
        commonWaits.waitForElementVisible(insightFinderPage.msgInputInsightFinderDrawer, 20);
        Assert.assertTrue(isVisible(insightFinderPage.msgInputInsightFinderDrawer),
                "Message Input field is not visible");
        log.info("---- Message Input field is visible ----");
        
        Assert.assertTrue(isVisible(insightFinderPage.submitButtonInsightFinderDrawer),
                "Send (submit) button is not visible in Insight Finder panel");
        log.info("---- Send button is visible ----");
        
        log.info("---- All Insight Finder page elements verified successfully ----");
    }
    
    public Boolean askQuestion(String question, String expectedHeaderText, Boolean newTopic) {
        return insightFinderPage.askQuestion(question, expectedHeaderText, newTopic);
    }
    
    public List<String> validateAndGetInsightResultTable() {
        log.info("---- Extracting column names from insightTableResult ----");
        return insightFinderPage.validateAndGetInsightResultTable(insightFinderPage.insightTableResult, 30);
    }
    
    public int validateAndGetInsightResultCount() {
        log.info("---- Validate Insight Finder Result Count ----");
        return insightFinderPage.validateAndGetInsightResultCount(insightFinderPage.latestPanelContent, 30);
    }
    
    public void saveDashboard(String title, String description) {
    	insightFinderPage.saveDashboard(title, description);
    }
    
    public void openSavedDashboardFolder(String dashboardName) {
    	// Check if toggle element is visible before clicking
    	commonWaits.clickIfElementPresent(dashboardPage.openMenuButton);
        
    	log.info("---- Opening menu: " + SolQAConstants.DashboardTitle + " ----");
    	dashboardPage.openMenu(SolQAConstants.DashboardTitle);
    	
    	log.info("----- Opening Printable Case Summary Details dashboard ----");
        dashboardFeatureMethods.openDashboard(dashboardName, dashboardName);
    	
    }
    
    public void deleteDashboard(String dashboardName) {
    	// Check if toggle element is visible before clicking
    	commonWaits.clickIfElementPresent(dashboardPage.openMenuButton);
        
    	log.info("---- Opening menu: " + SolQAConstants.DashboardTitle + " ----");
    	dashboardPage.openMenu(SolQAConstants.DashboardTitle);
    	
    	dashboardPage.deleteDashboard(dashboardName);
    }
    
    public void validateLatestInsightCanvasResult() {
        log.info("---- Validating Insight Canvas using latestPanelContent ----");
        insightFinderPage.validateLatestInsightCanvasResult();
    }
    
    public void ValidateDashboardFolderContent(List<String> expectedHeaders) {
        log.info("---- Validating Dashboard Folder Content ----");
        insightFinderPage.ValidateDashboardFolderContent(expectedHeaders);
    }
    
    public void assertAllSoftAssertions() {
        log.info("---- Asserting all soft assertions from InsightFinderPage ----");
        insightFinderPage.assertAllSoftAssertions();
    }
    
    public void removeQueryWithHeader(String queryToRemove) {
        insightFinderPage.removeQueryWithHeader(queryToRemove);
    }
    
    /**
     * Verify tracking information in stream API payload with generate_visualization use case
     * This method verifies that the payload contains:
     *   tracking: {
     *     solution: "insight_finder_assistant",
     *     use_case: "generate_visualization"
     *   }
     * 
     * NOTE: If DevTools is not available (version mismatch), this verification will be skipped
     *       with a warning, and the test will continue without failing.
     * 
     * @throws AssertionError if tracking information not found in payload (only if DevTools available)
     */
    public void verifyGenerateVisualizationTracking() {
        log.info("---- Verify tracking in stream API payload ----");
        log.info("---- Expected: solution='insight_finder_assistant', use_case='generate_visualization' ----");
        
        insightFinderPage.verifyPayloadContainsTracking(
            15,                              // Monitor for 15 seconds
            "stream",                        // Filter for stream API requests
            "insight_finder_assistant",      // Expected solution
            "generate_visualization"         // Expected use_case
        );
    }
    
    /**
     * Click the last follow-up recommendation under "Other recommended visualizations" and submit.
     * Uses {@link util.SolQAConstants#INSIGHT_FINDER_OTHER_RECOMMENDED_VISUALIZATIONS_LABEL}.
     */
    public void clickLastRecommendation() {
        log.info("---- Click on last recommendation ----");
        insightFinderPage.clickLastRecommendation();
    }
    
    /**
     * Extract all values from a specific column and validate dates are within specified days
     * Uses the latest panel content as the table source
     * 
     * @param columnName - The name of the column to extract (e.g., "Creation Time")
     * @param numberOfDays - Number of days to validate against (dates should be within this many days from now)
     * @param waitSeconds - Maximum seconds to wait for table to be visible
     * @return List of all column values as strings
     */
    public List<String> extractAndValidateColumnDates(String columnName, int numberOfDays) {
        log.info("---- Extract and validate column dates: '" + columnName + "' within " + numberOfDays + " days ----");
        return insightFinderPage.extractAndValidateColumnDates(
            insightFinderPage.latestPanelContent, columnName, numberOfDays, 30);
    }
    
    public void openSettingsPage() {
        log.info("---- Opening Settings menu: ");
        
        // Check if toggle element is visible before clicking
        commonWaits.clickIfElementPresent(dashboardPage.openMenuButton);
       
        commonWaits.clickIfElementPresent(dashboardPage.expandInsightFinder);
        
        dashboardPage.openMenu(SolQAConstants.SETTINGS);
        log.info("---- Settings menu opened successfully ----");
        
        // Wait for page to load and verify elements
        log.info("---- Waiting for Insight Finder page to load ----");
        commonWaits.waitForElementVisible(insightFinderPage.gptSettingsTitle, 30);
    }
    
    public void updateInsightFinderSettings(String skillId, Boolean streamingFlag, Boolean recommendationFlag) {
        log.info("---- Updating Insight Finder Settings ----");
        insightFinderPage.updateInsightFinderSettings(skillId, streamingFlag, recommendationFlag);
    }
    
    public Boolean askQuestionAndValidateCanvas(String question, String expectedHeaderText, Boolean newTopic) {
    	Boolean isSuccess = askQuestion(question, expectedHeaderText, true);
    	if (isSuccess) {
    		validateLatestInsightCanvasResult();
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public Boolean askQuestionAndValidateInsightResultCount(String question, String expectedHeaderText, Boolean newTopic) {
    	Boolean isSuccess = askQuestion(question, expectedHeaderText, true);
    	if (isSuccess) {
    		int result = validateAndGetInsightResultCount();
			if (result == -1) {
				log.info("********Question:"+ question + "Result: No data found (acceptable)");
			} else {
				log.info("********Question:"+ question + "Result:" + result);
			}
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public Boolean askQuestionAndValidateInsightResultTable(String question, String expectedHeaderText, Boolean newTopic) {
    	Boolean isSuccess = askQuestion(question, expectedHeaderText, true);
    	if (isSuccess) {
    		List<String> insightResult = validateAndGetInsightResultTable();
			log.info("********" + insightResult.toString());
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
