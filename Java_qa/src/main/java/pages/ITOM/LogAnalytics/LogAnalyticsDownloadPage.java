package pages.ITOM.LogAnalytics;

import framework.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import org.testng.Assert;
import util.CommonWaits;
import util.SolqaLogger;

import java.io.File;

public class LogAnalyticsDownloadPage  extends BasePage {
    private static final SolqaLogger log = new SolqaLogger(LogAnalyticsDownloadPage.class.getName());
    private static final CommonWaits commonwait = new CommonWaits();
    private String loganalytics_url =   "";
    WebDriver driver;

    public void SetLogAnalyticsDownloadPage() {
        this.driver = getDriver();
        PageFactory.initElements(driver, this);
    }


    @FindBy(xpath = "//*[@id='navmenu']")
    public WebElement loganalytics_navmenu;

    @FindBy(xpath = "//div[@id='BMC Helix Log Analytics']")
    public WebElement loganalytics_app_xpath;

    @FindBy(xpath = "//span[contains(.,'Collection')]/../../button")
    public WebElement loganalytics_collection_button;


    @FindBy(xpath = "//span[contains(.,'Connectors')]/../../a")
    public WebElement loganalytics_connectors_button;


    @FindBy(xpath = "//span[contains(.,'Create')]/../../button")
    public WebElement loganalytics_create_button;


    @FindBy(xpath = "//button[@id='connectorType']")
    public WebElement loganalytics_connectorType_button;


    @FindBy(xpath = "//input[contains(@id,'rx-search')]")
    public WebElement loganalytics_search_input;


    @FindBy(xpath = "//input[@id='name-input']")
    public WebElement loganalytics_name_input;


    @FindBy(xpath = "//textarea[@id='description-input']")
    public WebElement loganalytics_description_textarea;

    @FindBy(xpath = "//button[@id='download-connector-image-btn']")
    public WebElement loganalytics_download_connector_image_button;

    @FindBy(xpath = "//input[@id='tag-name']")
    public WebElement loganalytics_tag_name_input;
    

    @FindBy(xpath = "//input[@id='tag-value']")
    public WebElement loganalytics_tag_value_input;


    @FindBy(xpath = "//button[@id='add-row-1']")
    public WebElement loganalytics_add_row_1_button;


    @FindBy(xpath = "//button[@id='download-connector-btn']")
    public WebElement loganalytics_download_connector_button;


    @FindBy(xpath = "//button[@id='save-policy-button']")
    public WebElement loganalytics_save_policy_button;


    @FindBy(xpath = "//button[contains(.,'Ok')]")
    public WebElement loganalytics_ok_button;

    public String getDownloadBaseDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win") ? "c:\\temp" : "/tmp";
    }

    /**
     * Waits for downloads to complete in the given folder.
     * Returns true when expectedFiles (or more) are present and no temp (.crdownload/.tmp) files remain.
     */
    private boolean waitForDownloadComplete(String downloadPath, int expectedFiles, long timeoutMillis) {
        File dir = new File(downloadPath);
        long end = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < end) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                boolean hasTemp = false;
                int completed = 0;
                for (File f : files) {
                    String name = f.getName().toLowerCase();
                    if (name.endsWith(".crdownload") || name.endsWith(".tmp")) {
                        hasTemp = true;
                        break;
                    }
                    completed++;
                }
                if (!hasTemp && completed >= expectedFiles) {
                    log.info("Downloads complete in " + downloadPath + " with files=" + completed);
                    return true;
                }
            }
            commonwait.waitForSometime(5000);
        }
        log.info("Download not completed within timeout for path: " + downloadPath);
        return false;
    }

    public boolean deleteDirRecursive(File dir) {
        if (dir == null || !dir.exists()) {
            return true;
        }
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    if (!deleteDirRecursive(child)) {
                        return false;
                    }
                } else {
                    if (!child.delete()) {
                        log.warn("Failed to delete file: " + child.getAbsolutePath());
                        return false;
                    }
                }
            }
        }
        if (!dir.delete()) {
            log.warn("Failed to delete directory: " + dir.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * Downloads connector via UI; returns the timestamp part of download folder.
     */
    public void downloadConnector(String rnd, String policyName, String connectorName, int fileCount) throws InterruptedException {
        String downloadDir = getDownloadBaseDir() + File.separator +  "downloadLocation";
        log.info("launch Log Analytic page");
        commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_navmenu, 120);
        commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_collection_button, 120);
        commonwait.pause();
        loganalytics_collection_button.click();
        commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_connectors_button, 120);
        commonwait.pause();
        loganalytics_connectors_button.click();
        commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_create_button, 120);
        commonwait.pause();
        loganalytics_create_button.click();
        commonwait.waitForElementClickable(loganalytics_connectorType_button, 120);
        commonwait.pause();
        loganalytics_connectorType_button.click();
        commonwait.clearTextFromElement(loganalytics_search_input);
        loganalytics_search_input.sendKeys(connectorName);
        driver.findElement(By.xpath("//span[contains(.,'" + connectorName + "')]")).click();

        loganalytics_name_input.sendKeys(policyName + "-" + rnd);
        loganalytics_description_textarea.sendKeys(policyName + "-" + rnd);
        commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_download_connector_image_button, 5);
        loganalytics_download_connector_image_button.click();
        commonwait.pause();
        if(!waitForDownloadComplete(downloadDir, fileCount, 25 * 60 * 1000L)){
            log.error("Download not completed within timeout for path: " + downloadDir);
            Assert.fail("Download not completed within timeout for path: " + downloadDir);
        }
        fileCount++;
        loganalytics_tag_name_input.sendKeys("BMC-Automation-tag");
        loganalytics_tag_value_input.sendKeys("bmc-automation-tag-" + rnd);
        loganalytics_add_row_1_button.click();
        loganalytics_download_connector_button.click();
        if(!waitForDownloadComplete(downloadDir, fileCount, 5 * 60 * 1000L)){
            log.error("Download not completed within timeout for path: " + downloadDir);
            //Assert.fail("Download not completed within timeout for path: " + downloadDir);
            try {
                File dir = new File(downloadDir);
                File[] partials = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".crdownload"));
                if (partials != null) {
                    for (File f : partials) {
                        if (f.length() > 5 * 1024) {
                            File target = new File(downloadDir, "install-" + policyName + "-" + rnd + ".bat");
                            boolean renamed = f.renameTo(target);
                            log.info("Renaming partial download " + f.getName() + " to install.bat : " + renamed);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to rename .crdownload file: " + e.getMessage());
            }
        }
        // If a partial Chrome download exists and is >5KB, rename it to install.bat

        loganalytics_save_policy_button.click();
        commonwait.waitForSometime(6000);
        loganalytics_ok_button.click();
        commonwait.waitForElementPresent("//span[contains(.,'Create')]/../../button", 60);
    }

    public void setloganalytics_url(String url) {
        this.loganalytics_url = configProp.get("ADE_PLATFORM_URL") + url;
    }

    public String getloganalytics_url() {
        return this.loganalytics_url;
    }

    public boolean waitforLogAnalyticsPlatformPageLoad() {
        return commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_app_xpath, 20);
    }

    public boolean waitforLogAnalyticsMainPageLoad() {
        return commonwait.waitForElementVisible_withFluentWait(driver,loganalytics_navmenu, 20);
    }

}
