package ITOM.LogAnalytics;

import framework.Launcher;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITOM.Log_Analytics.LogAnalyticsFeatureMethods;
import util.*;
import org.testng.annotations.Test;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class LogPush extends Launcher {
    public static final SolqaLogger log = new SolqaLogger(LogPush.class.getName());
    Portal_FeatureMethods portalFeatureMethods;
    LogAnalyticsFeatureMethods logAnalyticsFeatureMethods;
    
    private int counter;
    private Map<Object, String> itomUserData;
    private UserUpdater itomUserUpdater;
    private String userKey = "aananda"; // Default user key
    private ReadJson readJson;

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(configProp);
        userKey = "itomadminuser";
        log.info("LogPush will use user: " + itomUserData.get("UserName"));
    }

    @Test(description = "LogAnalytics - Verify log ingestion using logstash", singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12346", Products = {Product.ADE_Platform, Product.ADE_LogAnalytics})
    public void DRSQA_12346_LogAnalytics_Verify_log_ingestion_using_logstash(ITestContext context) throws UnknownHostException {
        // User data already loaded in @BeforeTest - just use it directly
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12346");
        logAnalyticsFeatureMethods = new LogAnalyticsFeatureMethods();
        log.info("Login to ADE Platform with " + username);
        portalFeatureMethods.loginToADEPlatform(username, password);

        log.info("Navigate to Log Analytics tile");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.LOG_ANALYTICS_TILE);
        String currentHostname = InetAddress.getLocalHost().getHostName();

        // Build command parts separately to avoid parsing issues
        String scriptPath = System.getProperty(USERDIR) + File.separator + "resources" + File.separator +"ITOM" + File.separator + "LA" + File.separator + "send_logs.py";
        String portal = CommonMethods.readConfig("ADE_PLATFORM_URL");
        String logFile = System.getProperty(USERDIR)+ File.separator + "applog.txt";
        logAnalyticsPage.generateJWTToken();
        String jwtToken = logAnalyticsPage.auth_token;
        HMEvents hm_events = new HMEvents();
        String apiKey = hm_events.getBhomApiKey(jwtToken);
        // Debug: Print individual components
        log.info("Script path: " + scriptPath);
        log.info("Portal: " + portal);
        log.info("API Key: " + apiKey);
        log.info("Log file: " + logFile);
        log.info("Host: " + currentHostname);

        // Construct command WITHOUT quotes
        String pythonCmd = String.format("%s --portal %s --api-key %s --file %s --host %s",
                scriptPath, portal, apiKey, logFile, currentHostname);

        log.info("Final python command: " + pythonCmd);
        log.info("run python script to push logs to Log Analytics");
        logAnalyticsFeatureMethods.runPythonScript(pythonCmd);

        log.info("Navigate to Log Analytics tile");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.LOG_ANALYTICS_TILE);
        logAnalyticsFeatureMethods.verifyLogin();

        log.info("Verify logs are ingested and visible in Log Analytics");
        String filterName = "log_source_host";
        logAnalyticsFeatureMethods.verifyLogHostName(currentHostname, filterName);
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (portalFeatureMethods != null) {
                portalFeatureMethods.closeBrowser();
            }
            if (logAnalyticsFeatureMethods != null) {
                logAnalyticsFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }
}