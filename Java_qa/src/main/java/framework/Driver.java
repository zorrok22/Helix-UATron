package framework;

/* *********************************************************************
 Purpose:            This is main Driver class where driver initialization is done and driver
 * handle is returned. Supports Chrome, Firefox, Edge, Safari, and IE browsers.
 * Uses WebDriverManager for automatic driver management (Selenium 4).
 * Supports remote execution, headless mode, and multi-threading via ThreadLocal.
 File Name:          Driver.Java
 Suite Name:         Framework File
 Module Name: NA
 @author             akarhadk
 @version            1.0
 @since              2019-12-20
 
  ********************************************************************** */

import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import util.ReadPropertiesFile;

public class Driver {

    protected static Map<String, String> configProp;
    // Replace instance WebDriver with ThreadLocal for multi-threading support
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    /** Selenium Grid 4 session display name; set per test (e.g. from {@code Launcher} @BeforeMethod). */
    private static final ThreadLocal<String> seSessionName = new ThreadLocal<>();

    /**
     * Sets the {@code se:name} capability for the next remote session on this thread
     * (Selenium Grid session name / observability).
     */
    public static void setSeSessionName(String name) {
        if (name == null || name.isBlank()) {
            seSessionName.remove();
        } else {
            seSessionName.set(name.trim());
        }
    }

    static String getSeSessionName() {
        return seSessionName.get();
    }

    /**
     * Derives a stable session label from the upcoming TestNG test method.
     */
    public static void setSeSessionNameFromTestMethod(Method testMethod) {
        if (testMethod == null) {
            return;
        }
        setSeSessionName(testMethod.getDeclaringClass().getSimpleName() + "." + testMethod.getName());
    }

    public static String USERDIR = "user.dir";
    private static boolean disablePopupGC = false;
    private static String ChromeDownloadFilepath = "";

    public Driver() {
        configProp = ReadPropertiesFile.readProperties(System.getProperty(USERDIR) + File.separator + "properties.config");
    }

    public WebDriver getWebDriver() {
        WebDriver driver = getWebDriver(getBrowser());

        tlDriver.set(driver); // Store driver in ThreadLocal
        driver.manage().deleteAllCookies();
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        return driver;
    }

    // Get the current WebDriver from ThreadLocal
    public static WebDriver getCurrentDriver() {
        return tlDriver.get();
    }

    // Clean up ThreadLocal to prevent memory leaks
    public static void removeDriver() {
        if (tlDriver.get() != null) {
            tlDriver.get().quit();
            tlDriver.remove();
        }
        seSessionName.remove();
    }

    private WebDriver getWebDriver(Browser browser) {
        switch (browser) {
            case IE:
                return WebDriverManager.iedriver().create();

            case FF:
                return WebDriverManager.firefoxdriver().create();

            case GC:
                ChromeOptions chromeOptions = new ChromeOptions();

                // Add headless option only if headless flag is set to true in properties.config
                if (isHeadlessEnabled()) {
                    chromeOptions.addArguments("--headless=new");
                }

                if (disablePopupGC) {
                    Map<String, Object> chromePrefs = new HashMap<>();
                    chromePrefs.put("profile.default_content_settings.popups", 0); // Disable pop-ups
                    // Disable Password Manager and Breach Alerts
                    chromePrefs.put("credentials_enable_service", false);
                    chromePrefs.put("profile.password_manager_enabled", false);
                    chromePrefs.put("profile.password_manager_leak_detection", false);

                    if (!ChromeDownloadFilepath.isEmpty()) {
                        chromePrefs.put("download.default_directory", ChromeDownloadFilepath); // Set the download path
                    }
                    chromePrefs.put("download.prompt_for_download", "false"); // Ensure auto-download
                    chromeOptions.setExperimentalOption("prefs", chromePrefs);

                    // Disable the specific "Safe Browsing" password check feature
                    chromeOptions.addArguments("--disable-features=SafeBrowsingPasswordCheck");
                }

                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--disable-save-password-bubble");
                chromeOptions.addArguments("--disable-features=PasswordLeakDetection,PasswordCheck,SafeBrowsingPasswordCheck");
                // Performance optimizations to match manual browser speed
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-background-networking");
                chromeOptions.addArguments("--disable-background-timer-throttling");
                chromeOptions.addArguments("--disable-backgrounding-occluded-windows");
                chromeOptions.addArguments("--disable-hang-monitor");
                chromeOptions.addArguments("--disable-client-side-phishing-detection");
                chromeOptions.addArguments("--no-first-run");
                chromeOptions.addArguments("--safebrowsing-disable-auto-update");

                if (isRemoteExecution()) {
                    Map<String, Object> seOptions = new HashMap<>();
                    seOptions.put("deployment_job_id", configProp.get("deployment_job_id"));
                    chromeOptions.setCapability("se:options", seOptions);
                    final String gridSessionName = getSeSessionName();
                    if (gridSessionName != null && !gridSessionName.isEmpty()) {
                        chromeOptions.setCapability("se:name", gridSessionName);
                    }
                }

                WebDriver driver = WebDriverManager.chromedriver().capabilities(chromeOptions).create();
                if (isRemoteExecution()) {
                    ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
                }
                return driver;

            case EDGE:
                return WebDriverManager.edgedriver().create();

            case SAFARI:
                return WebDriverManager.safaridriver().create();

            default:
                throw new UnsupportedOperationException("Attempt to start invalid browser " + browser);
        }
    }

    private boolean isRemoteExecution() {
        return System.getProperty("wdm.remoteAddress") != null;
    }

    public String getDownloadDirectory() {
        return getBrowser() == Browser.IE
                ? System.getenv("USERPROFILE") + File.separator + "Downloads"
                : System.getProperty(USERDIR) + File.separator + "target";
    }

    public String getDefaultDownloadPath() {
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return home + File.separator + "Downloads";
        } else {
            // macOS or Linux usually use ~/Downloads
            return home + File.separator + "Downloads";
        }
    }


    private Browser getBrowser() {
        return Browser.valueOf(configProp.getOrDefault("browser", "").toUpperCase());
    }

    private boolean isHeadlessEnabled() {
        String headlessValue = configProp.get("headless");
        return headlessValue != null && headlessValue.equalsIgnoreCase("true");
    }

    public void setDisablePopupGC(boolean disablePopupGC) {
        Driver.disablePopupGC = disablePopupGC;
    }
    public void setChromeDownloadFilepath(String downloadFilepath) {
        Driver.ChromeDownloadFilepath = downloadFilepath;
    }
}
