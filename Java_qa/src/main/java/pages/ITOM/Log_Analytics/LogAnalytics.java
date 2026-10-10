package pages.ITOM.Log_Analytics;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolqaLogger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogAnalytics extends BasePage {
    WebDriver driver;

    static SolqaLogger log = new SolqaLogger(LogAnalytics.class.getName());
    CommonWaits commonWaits;

    String kibanaIframe = "//iframe[@id='iframe_kibana']";
    String searchResult = "//dd[contains(. ,'%s')]";
    String selectedFilter = "//button[contains(@title, 'Filter: log_source_host: %s')]";

    @FindBy(xpath = "//button[contains(. ,'Add filter')]")
    WebElement addFilterBtn;
    @FindBy(xpath = "//*[text()='Select a field first']/following-sibling::*//input")
    WebElement searchFieldInput;
    @FindBy(xpath = "//label[text()='Operator']/parent::*/following-sibling::*//*[@role='combobox']")
    WebElement operatorDropdown;
    @FindBy(xpath = "//button[@role='option' and @title='log_source_host']")
    WebElement agentHostFilterOption;
    @FindBy(xpath = "//button[@role='option' and @title='is']")
    WebElement isFilterOption;
    @FindBy(xpath = "//input[@placeholder='Enter a value']")
    WebElement valueTextField;
    @FindBy(xpath = "(//button[contains(. ,'Save')])[2]")
    WebElement saveFilter;
    @FindBy(xpath = "//*[@id='GlobalFilterGroup']")
    WebElement globalFilterGroup;

    public LogAnalytics(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public LogAnalytics() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public void verifyLogAnalyticsPageLaunchedSuccessfully() {
        boolean isLoaded = commonWaits.waitForElementVisible_withFluentWait(getDriver(), kibanaIframe, 50);
        Assert.assertTrue(isLoaded, "kibana iframe not loaded");
        switchToKibanaIframe();
        Assert.assertTrue(commonWaits.isElementPresent(globalFilterGroup), "cross launch failed to log analytics");
    }

    public void runPythonScript(String pythonCmd) {
        try {
            // Split the command into separate arguments
            String[] parts = pythonCmd.split("\\s+");

            // Build command array: ["python", "script.py", "arg1", "arg2", ...]
            List<String> command = new ArrayList<>();
            command.add("python");

            // Add all parts (script path and arguments)
            command.addAll(Arrays.asList(parts));

            System.out.println("Executing command: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            System.out.println("Process started, reading output...");

            // Read output from the process with 5-minute timeout
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            long startTime = System.currentTimeMillis();
            long timeoutMs = 2 * 60 * 1000; // 2 minutes in milliseconds

            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                // Check if 2 minutes have passed
                if (System.currentTimeMillis() - startTime > timeoutMs) {
                    System.out.println("2 minutes timeout reached, stopping script...");
                    break;
                }
            }

            // Stop the process after 5 minutes
            System.out.println("Stopping Python script after 5 minutes...");
            process.destroyForcibly();

            // Wait a bit for the process to terminate
            boolean terminated = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
            if (terminated) {
                int exitCode = process.exitValue();
                System.out.println("Python script stopped with exit code: " + exitCode);
            } else {
                System.out.println("Python script force killed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyLogHostName(String hostName, String filterName) {
        switchToKibanaIframe();
        commonWaits.waitForElementVisible(addFilterBtn).click();
        commonWaits.waitForElementVisible(searchFieldInput).click();
        searchFieldInput.sendKeys(filterName);
        commonWaits.waitForElementVisible(agentHostFilterOption).click();
        commonWaits.waitForElementVisible(operatorDropdown).click();
        commonWaits.waitForElementVisible(isFilterOption).click();
        commonWaits.waitForElementVisible(valueTextField).sendKeys(hostName);
        commonWaits.clickWithoutInterception(saveFilter);
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), commonWaits.waitForByElementToBeVisible(By.xpath(String.format(selectedFilter, hostName))), 10);
        int result = commonWaits.findElements(By.xpath(String.format(searchResult, hostName))).size();
        Assert.assertTrue(result > 0, "search result not found");
    }

    public void switchToKibanaIframe() {
        commonWaits.switchtoDefaultFrame();
        commonWaits.switchToFrame(By.xpath(kibanaIframe));
    }
}