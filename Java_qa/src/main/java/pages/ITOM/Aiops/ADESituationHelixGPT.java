package pages.ITOM.Aiops;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.ReadPropertiesFile;
import framework.Launcher;
import util.CommonWaits;
import util.SolQAConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ADESituationHelixGPT {
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String base_url = config.get("ADE_PLATFORM_URL");
    private static final String ade_username = config.get("ADE_USERNAME");
    private static final String ade_password = config.get("ADE_PASSWORD");
    public static Launcher  launch_driver = new Launcher();
    private static final CommonWaits commonWait = new CommonWaits();
    public static WebDriver driver = null;
    private static final String askHelixGPTbutton = "//span[contains(.,'HelixGPT')]/../../../button";
    private static final String logInsightLink = "//button[@aria-controls='helixgpt-log-insights']";
    private static final String logInsightSummary = "//*[contains(@id,'log-insights-item-summary')]";
    private static final String gtpBAPlink = "//span[contains(.,'Show recommendation planner')]/../../adapt-button";
    private static final String gtpBAPStepLink = "//div[contains(@id,'adapt-stepper')]/div/div/div[contains(.,'Step 1')]";
    private static final String gtpBAP5whys = "//button[contains(@data-testid,'helixGPTChatMessage') and contains(.,'Run 5Whys')]";
    private static final String why5response = "//div[contains(@data-testid,'helixGPTChatMessage-2_')]/span";
    private static final int webTimeout = 50;
    private static final String situationLink = "/aiops/#/situations/situation/";
    private static final String loganalytics_app_xpath = "//div[@id=\"BMC Helix Log Analytics\"]";

    public static void openADEPage(){
        try {
            // Create launcher instance and launch browser
            driver = launch_driver.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            Launcher.setDriver(driver);
            // Initialize wait and commonWaits

            // Login to the system
            Launcher.login(ade_username, ade_password, SolQAConstants.appITOM);

            // Navigate to Log Analytics (AiOps page)
            commonWait.waitForElementPresent(loganalytics_app_xpath, webTimeout);
        }catch(Exception ignored){
        }
    }
    public static String openSituationHelixGPT(String identifier) throws InterruptedException {
        String situationUrl = base_url + situationLink + identifier;


        try {
            openADEPage();

            // Open the situation page
            driver.get(situationUrl);
            commonWait.waitForPageLoad(driver);
            // Click HelixGPT button
            commonWait.waitForElementPresent(askHelixGPTbutton,webTimeout);
            WebElement helixGptBtn = commonWait.waitForByElementToBeVisible(By.xpath(askHelixGPTbutton));
            helixGptBtn.click();

            // Wait and click log insights button
            commonWait.waitForElementPresent(logInsightLink,webTimeout);
            WebElement logInsightsBtn = commonWait.waitForByElementToBeVisible(By.xpath(logInsightLink));
            logInsightsBtn.click();


            // Wait for the summary element to be visible and get its content
            commonWait.waitForElementPresent(logInsightSummary,webTimeout);
            WebElement summaryElement = commonWait.waitForByElementToBeVisible(By.xpath(logInsightSummary));
            String summaryContent = summaryElement.getText();

            // Print and return the summary content
            System.out.println("Summary Content: " + summaryContent);
            return summaryContent;

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public static void getSituationHelixGPTBAP(String identifier) throws InterruptedException {
        String situationUrl = base_url + situationLink + identifier;

        try{
            openADEPage();
            // Open the situation page
            driver.get(situationUrl);
            commonWait.waitForPageLoad(driver);
            // Click HelixGPT button
            commonWait.waitForElementPresent(gtpBAPlink,webTimeout);
            WebElement helixGptBtn = commonWait.waitForByElementToBeVisible(By.xpath(gtpBAPlink));
            helixGptBtn.click();

            commonWait.waitForElementPresent(gtpBAPStepLink,webTimeout);
            commonWait.waitForByElementToBeVisible(By.xpath(gtpBAPStepLink));

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public static String getSituationHelixGPTwhy5(String identifier) throws InterruptedException {
        String situationUrl = base_url + situationLink + identifier;

        try{
            openADEPage();
            // Open the situation page
            driver.get(situationUrl);
            commonWait.waitForPageLoad(driver);
            // Click HelixGPT button
            commonWait.waitForElementPresent(askHelixGPTbutton,webTimeout);
            WebElement helixGptBtn = commonWait.waitForByElementToBeVisible(By.xpath(askHelixGPTbutton));
            helixGptBtn.click();

            commonWait.waitForElementPresent(gtpBAP5whys,webTimeout);
            WebElement helixGpt5whyBtn = commonWait.waitForByElementToBeVisible(By.xpath(gtpBAP5whys));
            helixGpt5whyBtn.click();


            commonWait.waitForElementPresent(why5response,webTimeout);

            StringBuilder response = new StringBuilder();
            // Get all elements under the root xpath
            List<WebElement> allElements = driver.findElements(By.xpath(why5response + "//*"));

            // Extract text from all elements
            for (WebElement element : allElements) {
                String text = element.getText();
                if (!text.isEmpty()) {
                    response.append(text).append("\n");
                    System.out.println(text);
                }
            }
            return response.toString();

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}