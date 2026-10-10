package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.*;

/** *********************************************************************
 Purpose:       This class will act as listener to generate extent reports
 File Name:     Listener.Java
 Suite Name:    Framework File
 Module Name:   NA
 @author        akarhadk,akisore
 @version       1.0
 @since         2025-04-18

 ********************************************************************** */

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.google.common.base.Strings;
import com.vimalselvam.testng.EmailReporter;

public class Listener implements ITestListener, ISuiteListener {
    public static final Logger log = Logger.getLogger(Listener.class.getName());
    WebDriver driver = null;
    public static ExtentTest test = null;
    public static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();

    GetScreenshot scnShot;
    ReadJson readJson;
    static Listener instance;
    private static final String REPORTER_ATTR = "extentTestNgReporter";
    static ExtentReports reporter;

    private ExtentHtmlReporter htmlReporter;

    public static final String USERDIR = System.getProperty("user.dir");
    final String reportPathStr = System.getProperty("user.dir") + File.separator+"Reports"+File.separator;
    protected FileWriter writer;

    // Default single-run output (1st run): always report_parent.html
    final File reportFile = new File(reportPathStr, "report_parent.html");
    
    // Suite-level variables for report_parent.html generation
    private static File parentReportFile;
    private static boolean reportParentCreated = false;  // Flag: report_parent.html created and must be protected
    private static boolean isParentSuite = false;
    private static int childSuiteCount = 0;
    private static boolean isRetrySuite = false;
    private static File generatedRetryXmlFile = null;
    /** Master suite XML file (base name) used to reliably classify child suite-files. */
    private static String parentSuiteXmlFileName = null;
    
    // Consolidated retry tracking for nested suites
    private static List<ITestContext> failedTestContexts = new ArrayList<>();
    private static String parentSuiteName = null;
    
    /** Failed contexts when one suite XML defines multiple &lt;test&gt; tags (e.g. Regression_FullRun.xml). */
    private static final List<ITestContext> multiTestSuiteFailedContexts = new ArrayList<>();
    
    // Static shared reporter for nested suite-files (persists across ISuite instances)
    private static ExtentReports staticSharedReporter = null;
    private static int childSuiteCounter = 0;
    private static int childSuiteCompletionCounter = 0;  // Track how many children have finished
    private static final Object reporterLock = new Object();

    // MasterSuite.xml emulation (when the master suite does NOT declare this listener)
    private static boolean emulatedMasterSuite = false;
    private static String emulatedMasterSuiteName = null;
    private static int emulatedMasterSuiteFileCount = 0;
    
    // Flag to track if report_child.html has been created during retry (prevent overwrites)
    private static boolean retryReportCreated = false;
    // ThreadLocal variables for thread-safe access
    private static final ThreadLocal<String> tlJIRAID = new ThreadLocal<>();
    private static final ThreadLocal<String> tlAUTHOR = new ThreadLocal<>();
    private static final ThreadLocal<Product[]> tlProducts = new ThreadLocal<>();
    private static final ThreadLocal<String> tlCurrentProduct = new ThreadLocal<>();
    private static final ThreadLocal<String> tlProductVersion = new ThreadLocal<>();
    private static final ThreadLocal<String> tlSetupName = new ThreadLocal<>();
    private static final ThreadLocal<String> tlServiceName = new ThreadLocal<>();
    private static final ThreadLocal<String> tlJenkinJobId = new ThreadLocal<>();
    private static final ThreadLocal<String> tlJenkinJobName = new ThreadLocal<>();
    private static final ThreadLocal<String> tlDeploymentBuildNo = new ThreadLocal<>();
    private static final ThreadLocal<String> tlDeploymentJobId = new ThreadLocal<>();
    private static final ThreadLocal<String> tlZdtsuiteExecution = new ThreadLocal<>();

    private static String baseNameOrNull(String path) {
        if (path == null) return null;
        try {
            return new File(path).getName();
        } catch (Exception ignored) {
            return path;
        }
    }

    private static boolean sameSuiteFile(String a, String b) {
        String aa = baseNameOrNull(a);
        String bb = baseNameOrNull(b);
        if (aa == null || bb == null) return false;
        return aa.equalsIgnoreCase(bb);
    }

    private static boolean boolAttr(ISuite suite, String key) {
        try {
            Object v = suite.getAttribute(key);
            return (v instanceof Boolean) && (Boolean) v;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Retry detection must be reliable even if callback order varies (ISuiteListener vs ITestListener).
     * This helper detects retry and stamps the suite attribute for later callbacks.
     */
    private boolean detectRetryAndStampSuiteAttr(ISuite suite) {
        if (suite == null) return false;
        if (boolAttr(suite, "IS_RETRY_SUITE")) return true;
        boolean detected = isRetrySuiteDetection(suite, suite.getName());
        if (detected) {
            try {
                suite.setAttribute("IS_RETRY_SUITE", true);
            } catch (Exception ignored) {
                // best-effort only
            }
        }
        return detected;
    }

    /**
     * When TestNG runs a suite that was included via a <suite-files> wrapper (e.g. MasterSuite.xml),
     * the current {@link org.testng.xml.XmlSuite} is often a "child" whose parent suite holds the suite-files list.
     * We must detect that wrapper to:
     * - create ONE shared report_parent.html across all suite-files
     * - delay retry execution until ALL suite-files complete
     */
    private org.testng.xml.XmlSuite findSuiteFilesParentSuite(org.testng.xml.XmlSuite xmlSuite) {
        try {
            org.testng.xml.XmlSuite cur = xmlSuite;
            while (cur != null) {
                List<String> suiteFiles = cur.getSuiteFiles();
                if (suiteFiles != null && !suiteFiles.isEmpty()) {
                    return cur;
                }
                cur = cur.getParentSuite();
            }
        } catch (Throwable ignored) {
            // best-effort only (TestNG API differences)
        }
        return null;
    }


    /**
     * Initialize System.getProperty values in ThreadLocal to ensure thread safety.
     * This method should be called at the start of each test execution.
     */
    private void initializeSystemProperties() {
        tlJenkinJobId.set(System.getProperty("jenkinJobId"));
        tlJenkinJobName.set(System.getProperty("jenkinJobName"));
        tlDeploymentBuildNo.set(System.getProperty("deployment_build_no"));
        tlZdtsuiteExecution.set(System.getProperty("zdtsuite_execution"));

        // deployment_job_id resolution (priority: -Ddeployment_job_id, env DEPLOYMENT_JOB_ID, properties.config)
        String deploymentJobId = System.getProperty("deployment_job_id");
        if (deploymentJobId == null || deploymentJobId.trim().isEmpty()) {
            deploymentJobId = System.getenv("DEPLOYMENT_JOB_ID");
        }
        if ((deploymentJobId == null || deploymentJobId.trim().isEmpty()) && configProp != null) {
            deploymentJobId = configProp.get("deployment_job_id");
        }
        tlDeploymentJobId.set(deploymentJobId);

        log.info("[Listener] Initialized system properties (constants) - jenkinJobId: " + tlJenkinJobId.get()
                + ", jenkinJobName: " + tlJenkinJobName.get()
                + ", deployment_job_id: " + tlDeploymentJobId.get());
    }
    
    
    private void initializeConfigProperties() {
        if (configProp == null) {
            // Attempt lazy-load in case this thread did not see onStart(ITestContext) first
            configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
        }

        if (configProp != null) {
            String setupName = configProp.get("setupName");
            String productVersion = configProp.get("productVersion");
            String serviceName = configProp.get("serviceName");
            String deploymentJobIdFromConfig = configProp.get("deployment_job_id");

            tlSetupName.set(setupName);
            tlProductVersion.set(productVersion);
            tlServiceName.set(serviceName);
            // Only set if not already set by system properties/environment
            String existingDeploymentJobId = tlDeploymentJobId.get();
            if ((existingDeploymentJobId == null || existingDeploymentJobId.trim().isEmpty()) &&
                    deploymentJobIdFromConfig != null && !deploymentJobIdFromConfig.trim().isEmpty()) {
                tlDeploymentJobId.set(deploymentJobIdFromConfig);
            }

            log.info("[Listener] Initialized config properties (constants) - setupName: " + setupName
                    + ", productVersion: " + productVersion
                    + ", serviceName: " + serviceName);
        } else {
            log.warn("[Listener] configProp is null! Config properties were not loaded properly in onStart()");
        }
    }

    //-DjenkinJobName=%JOB_NAME% Value will come from Jenkins.


    protected Map<String, String> configProp;
    protected final String JIRALink = "https://jira.example.com/browse/";
    /**
     * ExtentHtmlReporter, ExtentLoggerReporter The ExtentHtmlReporter and
     * ExtentLoggerReporter were deprecated in series 4.1.x and have been removed in
     * version 5. The replacement is ExtentSparkReporter, which is comprehensive,
     * ports all features along with a host of new ones.
     * <p>
     * ExtentSparkReporter spark = new ExtentSparkReporter("Spark.html");
     */

    public Listener() {
        // Constructor - ThreadLocal variables will be initialized per test in onTestStart
        log.info("Listener initialized. System properties will be loaded per-thread for thread safety.");
    }

    private static void setInstance(Listener formatter) {
        instance = formatter;
    }

    public void onTestStart(ITestResult result) {

        log.info("[Listener] Test execution started.. onTestStart() --> " + result.getMethod().getMethodName());
        System.out
                .println("[Listener] Test execution started.. onTestStart() --> " + result.getMethod().getMethodName());

        // Set current test name for locator healing tracking
        framework.BasePage.setCurrentTestName(result.getMethod().getMethodName());

        // Initialize config properties in ThreadLocal for thread safety (critical for skipped-before-start cases)
        initializeConfigProperties();

        // Initialize system properties in ThreadLocal for thread safety
        initializeSystemProperties();

        String JIRAID = "jiraid";
        String AUTHOR = "author";
        Product[] products = {Product.DSOM};
        boolean annotationFound = false;

        SolqaDetails ta = null;
        final Method[] methods = result.getTestClass().getRealClass().getMethods();
        String currentMethodName = result.getMethod().getMethodName();
        
        log.info("[Listener] Looking for @SolqaDetails annotation for method: " + currentMethodName);

        for (Method m : methods) {
            if (m.isAnnotationPresent(SolqaDetails.class)) {
                if (m.getName().equals(currentMethodName)) {
                    ta = m.getAnnotation(SolqaDetails.class);
                    try {
                    	JIRAID = ta.JiraID();
                    } catch (Exception e) {
                        log.warn("[Listener] Exception reading JiraID: " + e.getMessage());
                    }
                    AUTHOR = ta.authorName();
                    products = ta.Products();
                    annotationFound = true;
                    log.info("[Listener] Found @SolqaDetails for " + currentMethodName + 
                             " - Products: " + java.util.Arrays.toString(products) + 
                             ", Author: " + AUTHOR + ", JiraID: " + JIRAID);
                    break;
                }
            }
        }
        
        if (!annotationFound) {
            log.warn("[Listener] @SolqaDetails annotation NOT found for method: " + currentMethodName + 
                     ". Using default Product.DSOM. Please check if annotation is present on the test method.");
        }

        // Store in ThreadLocal for thread-safe access across methods
        tlJIRAID.set(JIRAID);
        tlAUTHOR.set(AUTHOR);
        tlProducts.set(products);
        
        log.info("[Listener] ThreadLocal values set for " + currentMethodName + 
                 " - JiraID: " + JIRAID + ", Author: " + AUTHOR + 
                 ", Products: " + java.util.Arrays.toString(products) + 
                 ", Thread: " + Thread.currentThread().getName());

        //test = reporter.createTest("<a href="+JIRALink+JIRAID+">"+JIRAID+"</a>" + " Author:" + AUTHOR);
        test = reporter.createTest("JIRAID - " + JIRAID + ": " + result.getMethod().getDescription());
        extentTest.set(test);
        final String mName = result.getMethod().getMethodName();
        final String defectID = readJson.getAssignedDefect(mName, "DEFECTS", "Defect_List", "ActiveDefectList");

        if (defectID != null) {
            extentTest.get().skip("Test Skipped due to defect : " + defectID);
            throw new SkipException("Test Skipped due to defect :" + defectID, null);
        }
        extentTest.get().info("<font style=font-weight:bold size=4>" + "Usecase Name: "
                + result.getMethod().getDescription() + "</font>");
        extentTest.get().info("<font color=blue style=font-weight:bold size=3>" + result.getMethod().getMethodName()
                + " test is started." + "</font>");
        extentTest.get().assignAuthor(AUTHOR);
        for (int i = 0; i < products.length; i++) {
            extentTest.get().assignCategory(products[i].toString());
        }

    }

    public void onTestSuccess(ITestResult result) {

        System.out.println(
                "[Listener] Going to take screenshot.. onTestSuccess() --> " + result.getMethod().getMethodName());
        log.info("[Listener] Screenshot captured for test case.. onTestSuccess() -->  "
                + result.getMethod().getMethodName());
        System.out.println("[Listener] see screenshot.. onTestSuccess() --> " + result.getMethod().getMethodName());
        extentTest.get().pass("<font color=green>" + result.getMethod().getMethodName() + " test is PASSED. " + "</font>");
        final long totalTime = (result.getEndMillis() - result.getStartMillis());
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
        extentTest.get().info("Total time taken for test execution in minutes: " + String.valueOf(minutes));
        
        // Log healed locators if any
        logHealedLocators(result);
        
        this.postTestMethodStatus(result, "PASS");
        flushResults(result);

        // Clean up ThreadLocal to prevent memory leaks
        cleanupThreadLocal();

    }

    public String getJIRAID(ITestResult result) {
        String tt = null;

        SolqaDetails ta = null;
        final Method[] methods = result.getTestClass().getRealClass().getMethods();

        for (Method m : methods) {
            if (m.isAnnotationPresent(SolqaDetails.class)) {
                if (m.getName().toString().equals(result.getMethod().getMethodName())) {
                    ta = m.getAnnotation(SolqaDetails.class);
                    try {
                        tt = ta.JiraID();
                        return tt;
                    } catch (Exception ignored) {
                        ;
                    }
                    break;
                }
            }
        }
        return tt;
    }

    public void onTestFailure(ITestResult result) {
        WebDriver driver = null;
        String currentProduct = "";

        System.out.println(
                "[Listener] Going to take screenshot.. onTestFailure() --> " + result.getMethod().getMethodName());
        log.info("[Listener] Screenshot captured for test case.. onTestFailure() --> "
                + result.getMethod().getMethodName());

        final ITestContext context = result.getTestContext();

        final String tt = getJIRAID(result);
        String attributeKey;
        
        // Handle cases where JIRA ID is not available (no @SolqaDetails annotation)
        if (tt != null && !tt.isEmpty()) {
            attributeKey = tt.replace("-", "_");
        } else {
            // Fallback to method name if no JIRA ID found
            attributeKey = result.getMethod().getMethodName();
            log.warn("No JIRA ID found for test: " + attributeKey + ". Using method name as fallback.");
        }
        
        // THREAD-SAFE FIX: Get driver from ThreadLocal instead of ITestContext
        driver = framework.Launcher.getDriver();
        if (driver == null) {
            // Fallback to context attribute if ThreadLocal is null (backward compatibility)
            // Try multiple naming patterns due to inconsistency in test classes
            driver = (WebDriver) context.getAttribute(attributeKey + "_Driver");
            if (driver == null) {
                driver = (WebDriver) context.getAttribute(attributeKey + "_WebDriver");
            }
            if (driver == null) {
                driver = (WebDriver) context.getAttribute(attributeKey + "_SmartIT_Driver");
            }
            if (driver != null) {
                log.warn("[Listener] Driver not found in ThreadLocal, retrieved from context attribute");
            } else {
                log.error("[Listener] Driver is NULL! Cannot capture screenshot. ThreadLocal and context attributes both empty.");
            }
        }
        
        try {
            currentProduct = ((Product) context.getAttribute(tt.replace("-", "_")  + "_Product")).toString();
        } catch (NullPointerException e) {
            currentProduct = "DSOM";
        }

        // Store in ThreadLocal for thread-safe access
        tlCurrentProduct.set(currentProduct);

        extentTest.get().fail("<font color=red>" + result.getMethod().getMethodName() + "test is FAILED in product "
                + currentProduct + "</font>");
        extentTest.get().fail("Exception Details: " + result.getThrowable().fillInStackTrace());

        try {

            if (driver != null) {
                framework.Launcher.setDriver(driver);
                final String image = GetScreenshot.capture(result.getMethod().getMethodName(), "FAIL");
                if (image != null && !image.isEmpty()) {
                    final MediaEntityModelProvider mediaModel = MediaEntityBuilder
                            .createScreenCaptureFromBase64String(image).build();
                    extentTest.get().fail("image:", mediaModel);
                    log.info("[Listener] Screenshot successfully captured and attached to report");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        extentTest.get().fail(Arrays.toString(result.getThrowable().getStackTrace()) + " Failed");
        final long totalTime = (result.getEndMillis() - result.getStartMillis());
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
        extentTest.get().info("Total time taken for test execution in minutes: " + String.valueOf(minutes));
        
        // Log healed locators if any
        logHealedLocators(result);
        
        this.postTestMethodStatus(result, "FAIL");
        flushResults(result);

        // Clean up ThreadLocal to prevent memory leaks
        cleanupThreadLocal();
    }



    public void onTestSkipped(ITestResult result) {

        System.out.println("[Listener] On test skipped.. onTestSkipped() --> " + result.getMethod().getMethodName());
        log.info("[Listener] On test skipped.. onTestSkipped() --> " + result.getMethod().getMethodName());

        extentTest.get()
        .skip("<font color=yellow>" + result.getMethod().getMethodName() + " test is SKIPPED." + "</font>");

        // Ensure ThreadLocal constants are present even if TestNG skipped without fully running onTestStart
        initializeConfigProperties();
        initializeSystemProperties();

        this.postTestMethodStatus(result, "SKIPPED");
        flushResults(result);

        // Clean up ThreadLocal to prevent memory leaks
        cleanupThreadLocal();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("[Listener] on test sucess within percentage");
        flushResults(result);
    }

    public void onFinish(ITestContext context) {

        log.info("[Listener] Test execution finish.. onFinish() --> " + context.getName());
        System.out.println("[Listener] Test execution finish.. onFinish() --> " + context.getName());
        
        // Check if we're using static shared reporter (indicates nested suite-files execution OR master suite)
        boolean isNestedExecution = false;
        boolean isMasterSuiteExecution = false;
        synchronized (reporterLock) {
            isNestedExecution = (staticSharedReporter != null);
            isMasterSuiteExecution = isParentSuite && (staticSharedReporter != null);
        }
        
        // For master suite with suite-files OR nested suite-files: collect all failures
        if (isNestedExecution && !isRetrySuite) {
            int failedCount = context.getFailedTests().size();
            
            if (isMasterSuiteExecution) {
                log.info("[Listener] Master suite execution - collecting failures from child: " + context.getName());
                System.out.println("[Listener] 📋 Master suite child finished: " + context.getName());
            } else {
                log.info("[Listener] Nested execution detected - checking for failures in: " + context.getName());
            }
            
            if (failedCount > 0) {
                // ALWAYS collect context for consolidated retry
                synchronized (reporterLock) {
                    if (!failedTestContexts.contains(context)) {
                        failedTestContexts.add(context);
                        log.info("[Listener] Added context to failedTestContexts list (total now: " + failedTestContexts.size() + ")");
                    }
                }
                
                log.info("[Listener] Child suite/test has " + failedCount + " failed test(s)");
                System.out.println("[Listener] 📋 Collected " + failedCount + " failed test(s) from: " + context.getName());
                
                // Debug: List the failed test names
                for (ITestResult result : context.getFailedTests().getAllResults()) {
                    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
                    log.info("[Listener]   - Failed: " + testName);
                    System.out.println("[Listener]   ❌ " + result.getMethod().getMethodName());
                }
            } else {
                log.info("[Listener] No failures in: " + context.getName());
                System.out.println("[Listener] ✅ No failures in: " + context.getName());
            }
        } else if (!isRetrySuite) {
            // For standalone suites (NOT part of nested suite-files): generate retry XML immediately
            // BUT: Do NOT generate if we're a child of a parent suite with nested suite-files
            boolean isChildOfNestedParent = false;
            synchronized (reporterLock) {
                // If parent suite variables are set, we're a child of nested execution
                isChildOfNestedParent = (parentReportFile != null && parentSuiteName != null);
            }
            
            if (isChildOfNestedParent) {
                log.info("[Listener] Child of nested parent - skipping individual retry XML generation");
                System.out.println("[Listener] 📋 Child suite - Will use consolidated retry later");
            } else {
                // One suite XML with multiple <test> blocks: each onFinish(ITestContext) used to call
                // generateFailedTestsXml and overwrite generatedRetryXmlFile — only the last <test> (e.g. P3) survived.
                int xmlTestCount = context.getSuite().getXmlSuite().getTests().size();
                boolean multiTestSameSuite = xmlTestCount > 1 && !isNestedExecution;
                if (multiTestSameSuite) {
                    int failedCount = context.getFailedTests().size();
                    if (failedCount > 0) {
                        synchronized (reporterLock) {
                            if (!multiTestSuiteFailedContexts.contains(context)) {
                                multiTestSuiteFailedContexts.add(context);
                            }
                            log.info("[Listener] Multi-<test> suite: queued " + failedCount + " failure(s) from '"
                                    + context.getName() + "' for consolidated retry (queue size="
                                    + multiTestSuiteFailedContexts.size() + ")");
                            System.out.println("[Listener] 📋 Queued failures from: " + context.getName()
                                    + " — consolidated retry after full suite finishes");
                        }
                    }
                } else {
                    // Single <test> or nested suite-file child: generate retry XML for this context (prior behavior)
                    log.info("[Listener] Standalone suite - generating retry XML immediately");
                    generateFailedTestsXml(context);
                }
            }
        } else {
            log.info("[Listener] Retry suite - skipping retry XML generation");
        }
        
        // Just flush - renaming is handled by onFinish(ISuite)
        reporter.flush();
        log.info("[Listener] Report flushed for test context: " + context.getName());

    }

    public void flushResults(ITestResult result) {

        log.info("[Listener] flushResults() after each test execution --> " + result.getMethod().getMethodName());
        System.out.println(
                "[Listener] flushResults() after each test execution --> " + result.getMethod().getMethodName());
        reporter.flush();

    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("[Listener] Override onStart() Test execution start --> " + context.getName());
        log.info("[Listener] Override onStart() Test execution start --> " + context.getName());
        setInstance(this);
        System.setProperty("org.freemarker.loggerLibrary", "none");

        final ISuite suite = context.getSuite();
        final boolean retryRun = detectRetryAndStampSuiteAttr(suite);

        // Check if we should use the static shared reporter (master suite with suite-files)
        synchronized (reporterLock) {
            // Retry must NEVER reuse the shared parent reporter.
            if (!retryRun && staticSharedReporter != null && isParentSuite) {
                // Use the static shared reporter created by the master suite
                reporter = staticSharedReporter;
                log.info("[Listener] Using static shared reporter for test context: " + context.getName());
                System.out.println("[Listener] 🔗 Using shared reporter (part of master suite execution)");
                
                // Initialize readJson if not already done
                if (readJson == null) {
                    readJson = new ReadJson();
                }
                
                // Initialize config properties
                if (configProp == null) {
                    configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
                }
                
                // Skip normal reporter initialization - we're using the shared one
                return;
            }
        }

        final File directory = new File(reportPathStr);
        if (!directory.exists()) {
            directory.mkdir();

        }
        configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
        final String isHostname = configProp.get("name");
        log.info("--------- IS_Server = " + isHostname);

        initializeConfigProperties();
        // Initialize ThreadLocal variables for thread safety
        // Note: These are initialized here and will be inherited per-thread in parallel execution
     

        // Initialize system properties in ThreadLocal
        initializeSystemProperties();

        // Check if this is a retry suite first (takes priority over everything)
        Boolean isRetrySuiteContext = retryRun;
        Boolean isParentSuiteContext = (Boolean) suite.getAttribute("IS_PARENT_SUITE");
        ExtentReports sharedReporter = (ExtentReports) suite.getAttribute("PARENT_REPORTER");
        
        // If this is a retry suite, check if report_child.html already created
        if (isRetrySuiteContext != null && isRetrySuiteContext) {
            synchronized (reporterLock) {
                if (retryReportCreated && reporter != null) {
                    log.info("[Listener] RETRY suite - Reusing existing report_child.html reporter");
                    System.out.println("[Listener] 🔄 RETRY suite - Reusing existing report_child.html reporter (preventing overwrite)");
                    
                    // Initialize readJson if not already done
                    if (readJson == null) {
                        readJson = new ReadJson();
                    }
                    return;  // Reuse existing reporter
                } else {
                    log.info("[Listener] RETRY suite detected - Will create dedicated report_child.html");
                    System.out.println("[Listener] 🔄 RETRY suite - Creating dedicated report_child.html");
                    retryReportCreated = true;
                    // Fall through to create individual report below
                }
            }
        } else {
            // PRIORITY 1: Check if static shared reporter exists (for nested suite-files)
            synchronized (reporterLock) {
                if (!retryRun && staticSharedReporter != null) {
                    reporter = staticSharedReporter;
                    childSuiteCounter++;
                    log.info("[Listener] Using STATIC shared reporter for child suite " + childSuiteCounter + "/" + childSuiteCount + ": " + context.getSuite().getName());
                    System.out.println("[Listener] 📊 Child suite " + childSuiteCounter + "/" + childSuiteCount + ": " + context.getName() + " - Using shared reporter");
                    
                    // Initialize readJson if not already done
                    if (readJson == null) {
                        readJson = new ReadJson();
                    }
                    return;  // Skip creating new reporter
                }
            }
        }
        
        // PRIORITY 2: Check if running in parent suite context (from ISuite listener - old approach for non-nested)
        
        // Retry suites should NEVER use shared reporter - they need their own fresh reporter
        if (isRetrySuiteContext != null && isRetrySuiteContext) {
            log.info("[Listener] Detected RETRY suite - Creating separate reporter for report_child.html");
            System.out.println("[Listener] 🔄 Detected RETRY suite - Creating separate report for retried tests only");
            // Fall through to create individual report
        } else if (isParentSuiteContext != null && isParentSuiteContext && sharedReporter != null) {
            // Use shared reporter for consolidated/individual report_parent.html
            reporter = sharedReporter;
            log.info("[Listener] Using shared reporter for report_parent.html");
            System.out.println("[Listener] Using shared reporter for report_parent.html: " + context.getSuite().getName());
        }
        
        if (reporter == null) {
            // Fallback: create individual report (for suites without ISuiteListener methods)
            log.info("[Listener] Creating individual report for suite: " + context.getName());
            System.out.println("[Listener] Creating individual report for suite: " + context.getName());
            
            // Determine which file to use for report
            File reportOutputFile;
            if (isRetrySuiteContext != null && isRetrySuiteContext) {
                // For retry suites, always use report_child.html directly (no timestamping needed)
                reportOutputFile = new File(reportPathStr, "report_child.html");
                // Override parentReportFile to null so onFinish doesn't try to rename
                parentReportFile = null;
                log.info("[Listener] Retry suite will generate: report_child.html");
                System.out.println("[Listener] 🔄 Creating NEW reporter for report_child.html");
                System.out.println("[Listener] 📝 Report location: " + reportOutputFile.getAbsolutePath());
                System.out.println("[Listener] 📝 Test context: " + context.getName());
            } else {
                reportOutputFile = (parentReportFile != null) ? parentReportFile : reportFile;
                System.out.println("[Listener] 📝 Creating report: " + reportOutputFile.getAbsolutePath());
            }
            
            //  File failedReportFile = new File(reportPathStr, "failedReport.html");
            final File emailReportFile = new File(reportPathStr, "emailable-report.html");
            readJson = new ReadJson();
            htmlReporter = new ExtentHtmlReporter(reportOutputFile);
            htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
            htmlReporter.config().setChartVisibilityOnOpen(true);
            htmlReporter.config().setTheme(Theme.DARK);
            
            // Set appropriate title based on suite type
            if (isRetrySuiteContext != null && isRetrySuiteContext) {
                htmlReporter.config().setDocumentTitle("Solutions QA Automation - Retry Report");
                htmlReporter.config().setReportName(context.getSuite().getName() + " - Failed Tests Retry");
            } else if (isParentSuite) {
                htmlReporter.config().setDocumentTitle("Consolidated Solutions QA Automation Report");
                htmlReporter.config().setReportName(context.getSuite().getName() + " - Consolidated Report");
            } else {
                htmlReporter.config().setDocumentTitle("Solutions QA Automation Report");
                htmlReporter.config().setReportName(context.getSuite().getName() + " - Test Report");
            }
            htmlReporter.config().setEncoding("utf-8");


            final EmailReporter emailReporter = new EmailReporter(emailReportFile);
            reporter = new ExtentReports();
            reporter.attachReporter(htmlReporter, emailReporter);
            reporter.setReportUsesManualConfiguration(true);
            final File csvFile = new File(reportPathStr, "report.csv");

            try {
                writer = new FileWriter(csvFile);

            } catch (IOException e) {
                log.info("IO Exxception occured in File operation" + e);
                System.out.println("IO Exxception occured in File operation" + e);
            }


            reporter.setSystemInfo("Setup Name ", configProp.get("setupName"));
            reporter.setSystemInfo("Product Version", configProp.get("productVersion"));
            
            // Add execution mode info
            if (isRetrySuiteContext != null && isRetrySuiteContext) {
                reporter.setSystemInfo("Execution Mode", "Retry (Failed Tests Only)");
                reporter.setSystemInfo("Report Type", "Retry Report - report_child.html");
            } else if (isParentSuite) {
                reporter.setSystemInfo("Execution Mode", "Consolidated (Nested Suites)");
            } else {
                reporter.setSystemInfo("Execution Mode", "Individual (Non-Nested Suite)");
            }

            // Write an initial report shell immediately so users can monitor live.
            try {
                reporter.flush();
            } catch (Exception e) {
                log.warn("[Listener] Initial reporter.flush() failed", e);
            }
        }
        
        // Initialize readJson if not already initialized (for parent suite context)
        if (readJson == null) {
            readJson = new ReadJson();
        }

    }

    private void postTestMethodStatus(ITestResult iTestResult, String status) {

        String errorMessage = "";
        if (status.equalsIgnoreCase("FAIL")) {
            errorMessage = iTestResult.getThrowable().fillInStackTrace().toString();
        }

        // Get ThreadLocal values with null checks and fallback to "orphan"
        log.info("[Listener] postTestMethodStatus called for: " + iTestResult.getMethod().getMethodName() + 
                 " on thread: " + Thread.currentThread().getName());
        
        String currentJenkinJobId = tlJenkinJobId.get();
        String currentJenkinJobName = tlJenkinJobName.get();
        String currentServiceName = tlServiceName.get();
        String currentDeploymentJobId = tlDeploymentJobId.get();
        String currentZdtsuiteExecution = tlZdtsuiteExecution.get();
        String currentProductVersion = tlProductVersion.get();
        String currentSetupName = tlSetupName.get();
        String currentAuthor = tlAUTHOR.get();
        Product[] currentProducts = tlProducts.get();
        
        log.info("[Listener] Retrieved from ThreadLocal - JiraID: " + tlJIRAID.get() + 
                 ", Author: " + currentAuthor + 
                 ", Products: " + (currentProducts != null ? java.util.Arrays.toString(currentProducts) : "NULL"));

        // Apply null checks and default values
        if (currentJenkinJobId == null) {
            currentJenkinJobId = "orphan";
        }

        if (currentJenkinJobName == null) {
            currentJenkinJobName = "orphan";
        }

        if (currentServiceName == null || currentServiceName.isEmpty()) {
            currentServiceName = "orphan";
        }

        if (currentDeploymentJobId == null) {
            currentDeploymentJobId = "orphan";
        }

        if (currentZdtsuiteExecution == null) {
            currentZdtsuiteExecution = "orphan";
        }

        if (currentProductVersion == null) {
            currentProductVersion = "unknown";
        }

        if (currentSetupName == null) {
            currentSetupName = "unknown";
        }

        if (currentAuthor == null) {
            currentAuthor = "unknown";
        }

        // CRITICAL FIX: If products is null, try to re-read from annotation
        if (currentProducts == null) {
            log.warn("[Listener] ThreadLocal tlProducts is NULL for test: " + iTestResult.getMethod().getMethodName() + 
                     ". Attempting to re-read annotation...");
            
            // Try to re-read the annotation directly
            try {
                Method testMethod = iTestResult.getMethod().getConstructorOrMethod().getMethod();
                if (testMethod.isAnnotationPresent(SolqaDetails.class)) {
                    SolqaDetails annotation = testMethod.getAnnotation(SolqaDetails.class);
                    currentProducts = annotation.Products();
                    log.info("[Listener] Successfully re-read Products from annotation: " + 
                             java.util.Arrays.toString(currentProducts));
                } else {
                    log.error("[Listener] No @SolqaDetails annotation found on method: " + 
                              iTestResult.getMethod().getMethodName());
                    currentProducts = new Product[]{Product.DSOM};
                }
            } catch (Exception e) {
                log.error("[Listener] Exception while re-reading annotation: " + e.getMessage());
                currentProducts = new Product[]{Product.DSOM};
            }
        } else {
            log.info("[Listener] Using ThreadLocal Products for test: " + 
                     iTestResult.getMethod().getMethodName() + " - " + 
                     java.util.Arrays.toString(currentProducts));
        }

        final Date date = new Date();
        final long time = date.getTime();

        // ADDING FOR LOOP here to fetch test result for each product it refers to
        for (int i = 0; i < currentProducts.length; i++) {
            final Map<String, String> tags = new LinkedHashMap<String, String>();
            tags.put("time", String.valueOf(time));
            tags.put("testclass", iTestResult.getTestClass().getName());
            tags.put("name", iTestResult.getName());
            tags.put("description", iTestResult.getMethod().getDescription());
            tags.put("result", status);
            tags.put("suite", iTestResult.getTestContext().getSuite().getName());
            tags.put("errorMessage", errorMessage);
            tags.put("productVersion", currentProductVersion);
            tags.put("productName", currentProducts[i].toString());
            tags.put("jenkinJobId", currentJenkinJobId);
            tags.put("authorName", currentAuthor);
            tags.put("jenkinJobName", currentJenkinJobName);
            tags.put("setupName", currentSetupName);
            tags.put("serviceName", currentServiceName);
            tags.put("deployment_job_id", currentDeploymentJobId);
            tags.put("zdtsuite_execution", currentZdtsuiteExecution);

            final Collection<String> values = tags.values();
            final String[] targetArray = values.toArray(new String[0]);

            InsertMultipleRecords.insertData(targetArray);
        }

    }
    
    /**
     * Clean up ThreadLocal variables to prevent memory leaks.
     * This method should be called when a thread is finished executing a test.
     * It's automatically called after onTestSuccess, onTestFailure, and onTestSkipped.
     */
    public static void cleanupThreadLocal() {
        tlJIRAID.remove();
        tlAUTHOR.remove();
        tlProducts.remove();
        tlCurrentProduct.remove();
    }
    
    /**
     * Log healed locators for the current test in the extent report
     * @param result Test result
     */
    private void logHealedLocators(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            HealedLocatorManager manager = HealedLocatorManager.getInstance();
            
            List<HealedLocatorManager.HealedLocatorInfo> healedLocators = manager.getHealedLocatorsForTest(testName);
            
            if (healedLocators != null && !healedLocators.isEmpty()) {
                extentTest.get().info("<font color=orange style=font-weight:bold>🔧 Locator Healing Report:</font>");
                extentTest.get().info("<font color=orange>" + healedLocators.size() + " locator(s) were healed during this test.</font>");
                
                for (HealedLocatorManager.HealedLocatorInfo info : healedLocators) {
                    StringBuilder healingInfo = new StringBuilder();
                    healingInfo.append("<div style='background-color: #FFF9C4; padding: 10px; margin: 5px 0; border-left: 4px solid #FFC107;'>");
                    healingInfo.append("<b>Strategy Used:</b> ").append(info.getStrategy()).append("<br>");
                    healingInfo.append("<b>Original Locator:</b> <code style='color: #f44336;'>").append(escapeHtml(info.getOriginalLocator())).append("</code><br>");
                    healingInfo.append("<b>Healed Locator:</b> <code style='color: #4CAF50;'>").append(escapeHtml(info.getHealedLocator())).append("</code><br>");
                    healingInfo.append("<b>Timestamp:</b> ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getTimestamp()));
                    healingInfo.append("</div>");
                    
                    extentTest.get().info(healingInfo.toString());
                }
                
                log.info("Logged " + healedLocators.size() + " healed locator(s) for test: " + testName);
            }
        } catch (Exception e) {
            log.error("Failed to log healed locators in extent report", e);
        }
    }
    
    /**
     * Escape HTML special characters for safe display in reports
     * @param text Text to escape
     * @return Escaped text
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * Generate failed tests XML file for retry execution
     * This method creates a TestNG XML file containing only the failed tests
     * @param context The test context containing test results
     */
    private void generateFailedTestsXml(ITestContext context) {
        try {
            // Check if there are any failed tests
            if (context.getFailedTests().size() == 0) {
                log.info("[Listener] No failed tests to generate retry XML");
                return;
            }
            
            // Skip generating retry XML if this is already a retry suite
            if (isRetrySuite) {
                log.info("[Listener] Skipping retry XML generation for retry suite");
                return;
            }
            
            int failedCount = context.getFailedTests().size();
            log.info("[Listener] Generating retry XML for " + failedCount + " failed test(s)");
            System.out.println("[Listener] 🔄 Generating retry XML for " + failedCount + " failed test(s)");
            
            // Get suite name for file naming
            String suiteName = context.getSuite().getName();
            String sanitizedSuiteName = suiteName.replaceAll("[^a-zA-Z0-9_-]", "_");
            
            // Create retry XML file
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String retryXmlFileName = "testng-failed_" + sanitizedSuiteName + "_" + timestamp + ".xml";
            File retryXmlFile = new File(reportPathStr + File.separator + ".." + File.separator + retryXmlFileName);
            
            // Build XML content - preserve settings from original suite
            String parallel = context.getSuite().getXmlSuite().getParallel() != null ? 
                             context.getSuite().getXmlSuite().getParallel().toString() : "false";
            int threadCount = context.getSuite().getXmlSuite().getThreadCount();
            String configFailurePolicy = context.getSuite().getXmlSuite().getConfigFailurePolicy() != null ?
                                        context.getSuite().getXmlSuite().getConfigFailurePolicy().toString() : "skip";
            
            StringBuilder xmlContent = new StringBuilder();
            xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlContent.append("<!DOCTYPE suite SYSTEM \"https://testng.org/testng-1.0.dtd\">\n");
            xmlContent.append("<suite name=\"").append(escapeXml(suiteName)).append(" - Failed Tests Retry\" ");
            xmlContent.append("verbose=\"2\" ");
            xmlContent.append("configfailurepolicy=\"").append(configFailurePolicy).append("\" ");
            xmlContent.append("parallel=\"").append(parallel).append("\" ");
            xmlContent.append("thread-count=\"").append(threadCount).append("\">\n");
            log.info("[Listener] Retry XML using parallel=" + parallel + ", thread-count=" + threadCount + 
                    ", configfailurepolicy=" + configFailurePolicy);
            System.out.println("[Listener] 🔧 Retry XML preserves original suite settings:");
            System.out.println("[Listener]    - parallel: " + parallel);
            System.out.println("[Listener]    - thread-count: " + threadCount);
            System.out.println("[Listener]    - configfailurepolicy: " + configFailurePolicy);
            xmlContent.append("  <listeners>\n");
            xmlContent.append("    <listener class-name=\"util.Listener\"/>\n");
            xmlContent.append("  </listeners>\n");
            xmlContent.append("  <test name=\"").append(escapeXml(context.getName())).append(" - Retry\">\n");
            
            // IMPORTANT: Do NOT copy <groups> from the original suite into retry XML.
            // If groups are present, TestNG can filter the explicitly included methods and mark them as SKIPPED,
            // which leads to report_child.html showing all skipped. Retry XML must be methods-only.
            
            xmlContent.append("    <classes>\n");
            
            // Add failed test classes and methods
            Set<String> processedClasses = new HashSet<>();
            for (ITestResult result : context.getFailedTests().getAllResults()) {
                String className = result.getTestClass().getName();
                String methodName = result.getMethod().getMethodName();
                
                if (!processedClasses.contains(className + "." + methodName)) {
                    xmlContent.append("      <class name=\"").append(escapeXml(className)).append("\">\n");
                    xmlContent.append("        <methods>\n");
                    xmlContent.append("          <include name=\"").append(escapeXml(methodName)).append("\"/>\n");
                    xmlContent.append("        </methods>\n");
                    xmlContent.append("      </class>\n");
                    processedClasses.add(className + "." + methodName);
                }
            }
            
            xmlContent.append("    </classes>\n");
            xmlContent.append("  </test>\n");
            xmlContent.append("</suite>\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(retryXmlFile)) {
                writer.write(xmlContent.toString());
                log.info("[Listener] Retry XML generated: " + retryXmlFile.getAbsolutePath());
                System.out.println("[Listener] ✅ Retry XML generated: " + retryXmlFileName);
                System.out.println("[Listener] 📝 Location: " + retryXmlFile.getAbsolutePath());
                
                // Store the generated retry XML file path for execution
                generatedRetryXmlFile = retryXmlFile;
            }
            
        } catch (Exception e) {
            log.error("[Listener] Failed to generate retry XML", e);
            System.out.println("[Listener] ❌ Failed to generate retry XML: " + e.getMessage());
        }
    }
    
    /**
     * Escape XML special characters
     * @param text Text to escape
     * @return Escaped text
     */
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
    
    /**
     * Generate nested retry XML that includes child retry XMLs as suite-files
     * @param contexts List of test contexts with failed tests from child suites
     * @param suiteName Name of the parent suite
     * @return File object of the generated nested retry XML
     */
    private File generateNestedRetryXml(List<ITestContext> contexts, String suiteName) {
        try {
            log.info("[Listener] generateNestedRetryXml called with " + contexts.size() + " context(s)");
            System.out.println("[Listener] 🔧 Generating NESTED retry XML...");
            
            String sanitizedSuiteName = suiteName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String retryXmlFileName = "testng-failed_" + sanitizedSuiteName + "_Nested_" + timestamp + ".xml";
            File retryXmlFile = new File(reportPathStr + File.separator + ".." + File.separator + retryXmlFileName);
            
            // Build nested XML content with suite-files
            StringBuilder xmlContent = new StringBuilder();
            xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlContent.append("<!DOCTYPE suite SYSTEM \"https://testng.org/testng-1.0.dtd\">\n");
            xmlContent.append("<suite name=\"").append(escapeXml(suiteName)).append(" - Failed Tests Retry (Nested)\" verbose=\"2\">\n");
            xmlContent.append("  <listeners>\n");
            xmlContent.append("    <listener class-name=\"util.Listener\"/>\n");
            xmlContent.append("  </listeners>\n");
            xmlContent.append("  <!-- NESTED RETRY: Includes child retry XMLs as suite-files -->\n");
            xmlContent.append("  <suite-files>\n");
            
            // For each child context, find its generated retry XML and add as suite-file
            int childRetryCount = 0;
            for (ITestContext ctx : contexts) {
                String childSuiteName = ctx.getSuite().getName();
                String sanitizedChildName = childSuiteName.replaceAll("[^a-zA-Z0-9_-]", "_");
                
                // Find the retry XML file for this child (it was generated in onFinish(ITestContext))
                File childRetryXml = findLatestRetryXmlForSuite(sanitizedChildName);
                
                if (childRetryXml != null && childRetryXml.exists()) {
                    // Use relative path from parent retry XML location
                    String relativePath = childRetryXml.getName();
                    xmlContent.append("    <suite-file path=\"").append(escapeXml(relativePath)).append("\"/>\n");
                    childRetryCount++;
                    log.info("[Listener]   Added child retry XML: " + relativePath);
                    System.out.println("[Listener]   ✅ Added: " + relativePath);
                } else {
                    log.warn("[Listener]   Could not find retry XML for child: " + sanitizedChildName);
                    System.out.println("[Listener]   ⚠️ Missing retry XML for: " + childSuiteName);
                }
            }
            
            xmlContent.append("  </suite-files>\n");
            xmlContent.append("</suite>\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(retryXmlFile)) {
                writer.write(xmlContent.toString());
                log.info("[Listener] Nested retry XML generated: " + retryXmlFile.getAbsolutePath());
                System.out.println("[Listener] ✅ Nested retry XML generated: " + retryXmlFileName);
                System.out.println("[Listener] 📝 Includes " + childRetryCount + " child retry XML(s)");
                System.out.println("[Listener] 📝 Location: " + retryXmlFile.getAbsolutePath());
            }
            
            return retryXmlFile;
            
        } catch (Exception e) {
            log.error("[Listener] Failed to generate nested retry XML", e);
            System.out.println("[Listener] ❌ Failed to generate nested retry XML: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find the latest retry XML file for a given suite name
     * @param sanitizedSuiteName Sanitized suite name to search for
     * @return File object of the latest retry XML, or null if not found
     */
    private File findLatestRetryXmlForSuite(String sanitizedSuiteName) {
        try {
            File testOutputDir = new File(reportPathStr + File.separator + "..");
            File[] files = testOutputDir.listFiles((dir, name) -> 
                name.startsWith("testng-failed_" + sanitizedSuiteName) && 
                name.endsWith(".xml") &&
                !name.contains("Nested") &&
                !name.contains("Consolidated")
            );
            
            if (files != null && files.length > 0) {
                // Return the most recently modified file
                File latest = files[0];
                for (File f : files) {
                    if (f.lastModified() > latest.lastModified()) {
                        latest = f;
                    }
                }
                return latest;
            }
        } catch (Exception e) {
            log.error("[Listener] Error finding retry XML for suite: " + sanitizedSuiteName, e);
        }
        return null;
    }
    
    /**
     * Generate consolidated retry XML from multiple test contexts (for nested suites)
     * @param contexts List of test contexts with failed tests from child suites
     * @param suiteName Name of the parent suite
     * @return File object of the generated consolidated retry XML
     */
    private File generateConsolidatedRetryXml(List<ITestContext> contexts, String suiteName) {
        try {
            log.info("[Listener] generateConsolidatedRetryXml called with " + contexts.size() + " context(s)");
            System.out.println("[Listener] 🔧 Generating consolidated retry XML...");
            
            // Count total failed tests across all contexts
            int totalFailedCount = 0;
            for (ITestContext ctx : contexts) {
                int failed = ctx.getFailedTests().size();
                totalFailedCount += failed;
                log.info("[Listener]   - Context: " + ctx.getName() + " has " + failed + " failed test(s)");
                System.out.println("[Listener]   - " + ctx.getName() + ": " + failed + " failed test(s)");
            }
            
            if (totalFailedCount == 0) {
                log.info("[Listener] No failed tests to generate consolidated retry XML");
                System.out.println("[Listener] ℹ️  No failed tests found");
                return null;
            }
            
            log.info("[Listener] Generating consolidated retry XML for " + totalFailedCount + " failed test(s) from " + contexts.size() + " child suite(s)");
            System.out.println("[Listener] 🔄 Generating consolidated retry XML for " + totalFailedCount + " failed test(s)");
            
            // Sanitize suite name for file naming
            String sanitizedSuiteName = suiteName.replaceAll("[^a-zA-Z0-9_-]", "_");
            
            // Create consolidated retry XML file with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String retryXmlFileName = "testng-failed_" + sanitizedSuiteName + "_Consolidated_" + timestamp + ".xml";
            File retryXmlFile = new File(reportPathStr + File.separator + ".." + File.separator + retryXmlFileName);
            
            // Build XML content - FLAT structure (no nested suite-files!)
            StringBuilder xmlContent = new StringBuilder();
            xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlContent.append("<!DOCTYPE suite SYSTEM \"https://testng.org/testng-1.0.dtd\">\n");
            xmlContent.append("<suite name=\"").append(escapeXml(suiteName)).append(" - Consolidated Failed Tests Retry\" verbose=\"2\">\n");
            xmlContent.append("  <listeners>\n");
            xmlContent.append("    <listener class-name=\"util.Listener\"/>\n");
            xmlContent.append("  </listeners>\n");
            xmlContent.append("  <!-- FLAT RETRY: Single test with all failed methods from all child suites -->\n");
            xmlContent.append("  <test name=\"Consolidated Retry - All Failed Tests\">\n");
            xmlContent.append("    <classes>\n");
            
            // Group failed tests by class name for cleaner XML
            Map<String, Set<String>> testsByClass = new HashMap<>();
            
            // Iterate through all contexts and collect failed tests
            for (ITestContext context : contexts) {
                log.info("[Listener] Processing failed tests from: " + context.getName());
                System.out.println("[Listener]   Processing: " + context.getName());
                
                for (ITestResult result : context.getFailedTests().getAllResults()) {
                    String className = result.getTestClass().getName();
                    String methodName = result.getMethod().getMethodName();
                    
                    log.info("[Listener]     - Failed test: " + className + "." + methodName);
                    System.out.println("[Listener]     - Adding: " + methodName + " (" + className + ")");
                    
                    // Group methods by class
                    testsByClass.computeIfAbsent(className, k -> new HashSet<>()).add(methodName);
                }
            }
            
            // Now write grouped tests to XML
            log.info("[Listener] Writing " + testsByClass.size() + " class(es) to retry XML");
            System.out.println("[Listener] 📝 Writing " + testsByClass.size() + " test class(es) to XML");
            
            for (Map.Entry<String, Set<String>> entry : testsByClass.entrySet()) {
                String className = entry.getKey();
                Set<String> methodNames = entry.getValue();
                
                xmlContent.append("      <class name=\"").append(escapeXml(className)).append("\">\n");
                xmlContent.append("        <methods>\n");
                for (String methodName : methodNames) {
                    xmlContent.append("          <include name=\"").append(escapeXml(methodName)).append("\"/>\n");
                }
                xmlContent.append("        </methods>\n");
                xmlContent.append("      </class>\n");
                
                log.info("[Listener]   Class: " + className + " with " + methodNames.size() + " method(s)");
                System.out.println("[Listener]   ✓ " + className + ": " + methodNames.size() + " method(s)");
            }
            
            xmlContent.append("    </classes>\n");
            xmlContent.append("  </test>\n");
            xmlContent.append("</suite>\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(retryXmlFile)) {
                writer.write(xmlContent.toString());
                log.info("[Listener] Consolidated retry XML generated: " + retryXmlFile.getAbsolutePath());
                System.out.println("[Listener] ✅ Consolidated retry XML generated: " + retryXmlFileName);
                System.out.println("[Listener] 📝 Contains " + totalFailedCount + " failed test(s) from " + contexts.size() + " child suite(s)");
                System.out.println("[Listener] 📝 Grouped into " + testsByClass.size() + " test class(es)");
                System.out.println("[Listener] 📝 Location: " + retryXmlFile.getAbsolutePath());
                
                return retryXmlFile;
            }
            
        } catch (Exception e) {
            log.error("[Listener] Failed to generate consolidated retry XML", e);
            System.out.println("[Listener] ❌ Failed to generate consolidated retry XML: " + e.getMessage());
            return null;
        }
    }
    
    // ==================== ISuiteListener Methods ====================
    
    /**
     * Detect if the suite is a retry suite based on suite name or child suite file names
     * @param suite The ISuite object
     * @param suiteName The name of the suite
     * @return true if this is a retry suite, false otherwise
     */
    private boolean isRetrySuiteDetection(ISuite suite, String suiteName) {
        // Check suite name for retry indicators
        if (suiteName != null && (
            suiteName.toLowerCase().contains("retry") ||
            suiteName.toLowerCase().contains("retrial") ||
            suiteName.toLowerCase().contains("failed") ||
            suiteName.toLowerCase().contains("rerun")
        )) {
            log.info("[Listener] Retry suite detected by name: " + suiteName);
            return true;
        }
        
        // Check child suite file names for testng-failed indicators
        if (suite.getXmlSuite().getChildSuites() != null && !suite.getXmlSuite().getChildSuites().isEmpty()) {
            for (org.testng.xml.XmlSuite childSuite : suite.getXmlSuite().getChildSuites()) {
                String childFileName = childSuite.getFileName();
                if (childFileName != null && (
                    childFileName.toLowerCase().contains("testng-failed") ||
                    childFileName.toLowerCase().contains("retry") ||
                    childFileName.toLowerCase().contains("failed")
                )) {
                    log.info("[Listener] Retry suite detected by child suite file: " + childFileName);
                    return true;
                }
            }
        }

        // Check suite-files (MasterSuite / nested retry XMLs use <suite-files>, not childSuites)
        try {
            List<String> suiteFiles = suite.getXmlSuite().getSuiteFiles();
            if (suiteFiles != null && !suiteFiles.isEmpty()) {
                for (String suiteFile : suiteFiles) {
                    if (suiteFile != null && (
                            suiteFile.toLowerCase().contains("testng-failed") ||
                            suiteFile.toLowerCase().contains("retry") ||
                            suiteFile.toLowerCase().contains("failed")
                    )) {
                        log.info("[Listener] Retry suite detected by suite-file: " + suiteFile);
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
            // best-effort detection only
        }
        
        // Check if the suite XML file name itself contains retry indicators
        String suiteFileName = suite.getXmlSuite().getFileName();
        if (suiteFileName != null && (
            suiteFileName.toLowerCase().contains("retry") ||
            suiteFileName.toLowerCase().contains("testng-failed") ||
            suiteFileName.toLowerCase().contains("failed")
        )) {
            log.info("[Listener] Retry suite detected by file name: " + suiteFileName);
            return true;
        }
        
        return false;
    }
    
    /**
     * Called when suite starts - handles report_parent.html generation for both nested and non-nested suites
     */
    /**
     * Check if this suite is a known child suite in nested execution (Petramco_P1/P2/P3)
     */
    private boolean isKnownNestedChild(String suiteFileName) {
        if (suiteFileName == null) return false;
        String fileName = new File(suiteFileName).getName();
        // Known child suites for Regression_FullRun.xml
        return fileName.matches("Petramco_P[123]\\.xml");
    }
    
    /**
     * Detect if this suite has child suite-files (nested execution)
     * @param suite The ISuite object
     * @return true if this suite has child suite-files
     */
    private boolean hasChildSuiteFiles(ISuite suite) {
        try {
            List<String> suiteFiles = suite.getXmlSuite().getSuiteFiles();
            return suiteFiles != null && !suiteFiles.isEmpty();
        } catch (Exception e) {
            log.warn("[Listener] Error checking for child suite files", e);
            return false;
        }
    }

    /**
     * If user ran a master suite via -Dsurefire.suiteXmlFiles=MasterSuite.xml but MasterSuite.xml
     * itself does not register this listener, then this listener won't see onStart(ISuite) for the master.
     *
     * In that case we "emulate" master suite behavior from the first child suite-file by parsing
     * the suiteXmlFiles property and counting its <suite-file> entries, so report_parent/report_child
     * don't get re-created per suite-file.
     */
    private EmulatedMasterSuite detectEmulatedMasterSuiteFromSurefireProperty() {
        try {
            String prop = System.getProperty("surefire.suiteXmlFiles");
            if (prop == null || prop.trim().isEmpty()) {
                // Some environments use an alternate property name; keep best-effort only.
                prop = System.getProperty("suiteXmlFiles");
            }
            if (prop == null || prop.trim().isEmpty()) return null;

            // surefire.suiteXmlFiles may be a comma-separated list.
            String[] parts = prop.split(",");
            for (String raw : parts) {
                if (raw == null) continue;
                String p = raw.trim();
                if (p.isEmpty()) continue;

                File suiteFile = new File(p);
                if (!suiteFile.isAbsolute()) {
                    suiteFile = new File(USERDIR, p);
                }
                if (!suiteFile.exists() || !suiteFile.isFile()) continue;

                // Using platform default charset is fine here (XML is ASCII/UTF-8 by declaration).
                String xml = new String(java.nio.file.Files.readAllBytes(suiteFile.toPath()));
                // Must be a suite-files master.
                if (!xml.contains("<suite-files")) continue;

                // Suite name (optional)
                String suiteName = null;
                java.util.regex.Matcher nameM = java.util.regex.Pattern
                        .compile("<suite\\s+[^>]*name\\s*=\\s*\"([^\"]+)\"", java.util.regex.Pattern.CASE_INSENSITIVE)
                        .matcher(xml);
                if (nameM.find()) {
                    suiteName = nameM.group(1);
                }

                // Count suite-file entries
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("<suite-file\\s+[^>]*path\\s*=\\s*\"([^\"]+)\"", java.util.regex.Pattern.CASE_INSENSITIVE)
                        .matcher(xml);

                int count = 0;
                while (m.find()) count++;

                if (count > 0) {
                    EmulatedMasterSuite emu = new EmulatedMasterSuite();
                    emu.suiteName = (suiteName == null || suiteName.trim().isEmpty()) ? suiteFile.getName() : suiteName.trim();
                    emu.suiteFileCount = count;
                    emu.suiteXmlPath = suiteFile.getAbsolutePath();
                    return emu;
                }
            }
        } catch (Exception e) {
            log.warn("[Listener] Failed to detect emulated master suite from surefire property", e);
        }
        return null;
    }

    private static final class EmulatedMasterSuite {
        String suiteName;
        int suiteFileCount;
        String suiteXmlPath;
    }
    
    @Override
    public void onStart(ISuite suite) {
        String suiteName = suite.getName();
        String suiteFileName = suite.getXmlSuite().getFileName();
        log.info("[Listener] Suite onStart called for: " + suiteName);
        System.out.println("=======================================================");
        System.out.println("[Listener] 🚀 Suite onStart: " + suiteName);
        System.out.println("[Listener] 📁 Suite file: " + suiteFileName);
        
        // Detect if this is a retry suite FIRST (takes priority).
        // Stamp suite attribute early so onStart(ITestContext) can reliably detect retry.
        isRetrySuite = isRetrySuiteDetection(suite, suiteName);
        suite.setAttribute("IS_RETRY_SUITE", isRetrySuite);
        
        if (!isRetrySuite) {
            synchronized (reporterLock) {
                multiTestSuiteFailedContexts.clear();
            }
        }
        
        // Check if this is a master suite with child suite-files (like MasterSuite.xml)
        boolean hasSuiteFiles = hasChildSuiteFiles(suite);
        
        if (hasSuiteFiles) {
            synchronized (reporterLock) {
                // This is a PARENT suite with suite-files (MasterSuite.xml approach)
                isParentSuite = true;
                List<String> suiteFiles = suite.getXmlSuite().getSuiteFiles();
                childSuiteCount = suiteFiles.size();
                parentSuiteName = suiteName;
                parentSuiteXmlFileName = baseNameOrNull(suiteFileName);
                childSuiteCounter = 0;
                childSuiteCompletionCounter = 0;
                
                // Clear any previous failed test contexts for fresh run
                if (!isRetrySuite) {
                    failedTestContexts.clear();
                }
                
                log.info("[Listener] Detected MASTER suite with suite-files: " + suiteName + 
                        " with " + childSuiteCount + " child suite file(s)");
                System.out.println("[Listener] 📊 MASTER SUITE detected: " + suiteName + 
                        " with " + childSuiteCount + " child suite-files:");
                
                // Log child suite files
                for (String suiteFile : suiteFiles) {
                    System.out.println("[Listener]    - " + suiteFile);
                }
                
                System.out.println("[Listener] 🔄 Will generate single report_parent.html for ALL child suites");
                System.out.println("[Listener] 🔄 Will collect failures from ALL child suites for consolidated retry");
                
                // Create report file
                String reportFileName = isRetrySuite ? "report_child.html" : "report_parent.html";
                parentReportFile = new File(reportPathStr, reportFileName);
                
                // Set protection flag for report_parent.html
                if (!isRetrySuite && reportFileName.equals("report_parent.html")) {
                    reportParentCreated = true;
                    log.info("[Listener] 🔒 report_parent.html protection flag SET");
                    System.out.println("[Listener] 🔒 report_parent.html PROTECTION ACTIVATED");
                }
                
                // Delete old report if exists (with protection)
                if (parentReportFile.exists()) {
                    String actualFileName = parentReportFile.getName();
                    if (actualFileName.equals("report_parent.html") && (reportParentCreated || isRetrySuite)) {
                        System.out.println("[Listener] 🔒 PROTECTED: " + actualFileName + " will not be deleted");
                    } else {
                        parentReportFile.delete();
                        System.out.println("[Listener] 🗑️  Deleted old " + actualFileName);
                    }
                }
                
                // Create static shared reporter for ALL child suites
                final File directory = new File(reportPathStr);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                
                ExtentHtmlReporter htmlRep = new ExtentHtmlReporter(parentReportFile);
                htmlRep.config().setTestViewChartLocation(ChartLocation.TOP);
                htmlRep.config().setChartVisibilityOnOpen(true);
                htmlRep.config().setTheme(Theme.DARK);
                
                if (isRetrySuite) {
                    htmlRep.config().setDocumentTitle("Solutions QA Automation - Retry Report");
                    htmlRep.config().setReportName(suiteName + " - Failed Tests Retry");
                } else {
                    htmlRep.config().setDocumentTitle("Consolidated Solutions QA Automation Report");
                    htmlRep.config().setReportName(suiteName + " - Consolidated Report");
                }
                htmlRep.config().setEncoding("utf-8");
                
                final File emailReportFile = new File(reportPathStr, "emailable-report.html");
                final EmailReporter emailRep = new EmailReporter(emailReportFile);
                
                staticSharedReporter = new ExtentReports();
                staticSharedReporter.attachReporter(htmlRep, emailRep);
                staticSharedReporter.setReportUsesManualConfiguration(true);
                
                // Set system info
                configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
                staticSharedReporter.setSystemInfo("Setup Name ", configProp.get("setupName"));
                staticSharedReporter.setSystemInfo("Product Version", configProp.get("productVersion"));
                staticSharedReporter.setSystemInfo("Execution Mode", isRetrySuite ? "Retry (Failed Tests Only)" : "Consolidated (Master Suite)");

                // Write an initial report shell immediately so users can monitor live.
                try {
                    staticSharedReporter.flush();
                } catch (Exception e) {
                    log.warn("[Listener] Initial staticSharedReporter.flush() failed", e);
                }
                
                log.info("[Listener] ✅ Static shared reporter created for master suite execution");
                System.out.println("[Listener] ✅ Static shared reporter created - ALL " + childSuiteCount + " child suites will use this");
                
                // Mark this as parent to skip normal initialization
                suite.setAttribute("IS_PARENT_SUITE", true);
                suite.setAttribute("PARENT_REPORTER", staticSharedReporter);
                suite.setAttribute(REPORTER_ATTR, staticSharedReporter);
                return;  // Skip normal suite setup
            }
        }

        // Emulated master suite-file handling (MasterSuite.xml without listener)
        synchronized (reporterLock) {
            if (!isRetrySuite && staticSharedReporter == null) {
                EmulatedMasterSuite emu = detectEmulatedMasterSuiteFromSurefireProperty();
                if (emu != null) {
                    emulatedMasterSuite = true;
                    emulatedMasterSuiteName = emu.suiteName;
                    emulatedMasterSuiteFileCount = emu.suiteFileCount;

                    isParentSuite = true;
                    childSuiteCount = emulatedMasterSuiteFileCount;
                    parentSuiteName = emulatedMasterSuiteName;
                    parentSuiteXmlFileName = baseNameOrNull(emu.suiteXmlPath);
                    childSuiteCounter = 0;
                    childSuiteCompletionCounter = 0;
                    failedTestContexts.clear();

                    System.out.println("[Listener] 📌 EMULATED MASTER SUITE: " + emulatedMasterSuiteName +
                            " (" + emulatedMasterSuiteFileCount + " suite-file(s)) from: " + emu.suiteXmlPath);
                    System.out.println("[Listener] 🔄 Will generate single report_parent.html for ALL suite-files");

                    // Create report_parent.html shared reporter (same as real master suite)
                    String reportFileName = "report_parent.html";
                    parentReportFile = new File(reportPathStr, reportFileName);

                    final File directory = new File(reportPathStr);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }

                    ExtentHtmlReporter htmlRep = new ExtentHtmlReporter(parentReportFile);
                    htmlRep.config().setTestViewChartLocation(ChartLocation.TOP);
                    htmlRep.config().setChartVisibilityOnOpen(true);
                    htmlRep.config().setTheme(Theme.DARK);
                    htmlRep.config().setDocumentTitle("Consolidated Solutions QA Automation Report");
                    htmlRep.config().setReportName(emulatedMasterSuiteName + " - Consolidated Report");
                    htmlRep.config().setEncoding("utf-8");

                    final File emailReportFile = new File(reportPathStr, "emailable-report.html");
                    final EmailReporter emailRep = new EmailReporter(emailReportFile);

                    staticSharedReporter = new ExtentReports();
                    staticSharedReporter.attachReporter(htmlRep, emailRep);
                    staticSharedReporter.setReportUsesManualConfiguration(true);

                    configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
                    staticSharedReporter.setSystemInfo("Setup Name ", configProp.get("setupName"));
                    staticSharedReporter.setSystemInfo("Product Version", configProp.get("productVersion"));
                    staticSharedReporter.setSystemInfo("Execution Mode", "Consolidated (Emulated Master Suite)");

                    // Write an initial report shell immediately so users can monitor live.
                    try {
                        staticSharedReporter.flush();
                    } catch (Exception e) {
                        log.warn("[Listener] Initial staticSharedReporter.flush() failed", e);
                    }

                    log.info("[Listener] ✅ Static shared reporter created for EMULATED master suite execution");

                    // Treat current suite as a CHILD suite-file of the emulated master
                    suite.setAttribute("IS_CHILD_OF_PARENT", true);
                    suite.setAttribute("PARENT_SUITE_NAME", parentSuiteName);
                    suite.setAttribute("PARENT_REPORTER", staticSharedReporter);
                    System.out.println("[Listener] 🔗 CHILD suite-file detected: " + suiteName + " (parent=" + parentSuiteName + ")");
                    return;
                }
            }
        }

        // MasterSuite.xml wrapper may not fire ISuiteListener for the wrapper itself in some runners.
        // If this suite has a parent XmlSuite with <suite-files>, initialize shared reporting ONCE here.
        synchronized (reporterLock) {
            if (!isRetrySuite && staticSharedReporter == null) {
                org.testng.xml.XmlSuite suiteFilesParent = findSuiteFilesParentSuite(suite.getXmlSuite());
                if (suiteFilesParent != null) {
                    List<String> suiteFiles = suiteFilesParent.getSuiteFiles();
                    if (suiteFiles != null && !suiteFiles.isEmpty()) {
                        isParentSuite = true;
                        childSuiteCount = suiteFiles.size();
                        parentSuiteName = (suiteFilesParent.getName() != null && !suiteFilesParent.getName().trim().isEmpty())
                                ? suiteFilesParent.getName().trim()
                                : "MasterSuite";
                        parentSuiteXmlFileName = baseNameOrNull(suiteFilesParent.getFileName());
                        childSuiteCounter = 0;
                        childSuiteCompletionCounter = 0;
                        failedTestContexts.clear();

                        System.out.println("[Listener] 📊 SUITE-FILES wrapper detected via parent XmlSuite: " + parentSuiteName
                                + " (" + childSuiteCount + " suite-file(s))");
                        System.out.println("[Listener] 🔄 Will generate single report_parent.html for ALL suite-files");

                        String reportFileName = "report_parent.html";
                        parentReportFile = new File(reportPathStr, reportFileName);

                        // Protect parent report from retry deletion/overwrite
                        reportParentCreated = true;

                        if (parentReportFile.exists()) {
                            System.out.println("[Listener] 🔒 PROTECTED: report_parent.html will not be deleted");
                        }

                        final File directory = new File(reportPathStr);
                        if (!directory.exists()) {
                            directory.mkdir();
                        }

                        ExtentHtmlReporter htmlRep = new ExtentHtmlReporter(parentReportFile);
                        htmlRep.config().setTestViewChartLocation(ChartLocation.TOP);
                        htmlRep.config().setChartVisibilityOnOpen(true);
                        htmlRep.config().setTheme(Theme.DARK);
                        htmlRep.config().setDocumentTitle("Consolidated Solutions QA Automation Report");
                        htmlRep.config().setReportName(parentSuiteName + " - Consolidated Report");
                        htmlRep.config().setEncoding("utf-8");

                        final File emailReportFile = new File(reportPathStr, "emailable-report.html");
                        final EmailReporter emailRep = new EmailReporter(emailReportFile);

                        staticSharedReporter = new ExtentReports();
                        staticSharedReporter.attachReporter(htmlRep, emailRep);
                        staticSharedReporter.setReportUsesManualConfiguration(true);

                        configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
                        staticSharedReporter.setSystemInfo("Setup Name ", configProp.get("setupName"));
                        staticSharedReporter.setSystemInfo("Product Version", configProp.get("productVersion"));
                        staticSharedReporter.setSystemInfo("Execution Mode", "Consolidated (Master Suite - suite-files)");

                        try {
                            staticSharedReporter.flush();
                        } catch (Exception e) {
                            log.warn("[Listener] Initial staticSharedReporter.flush() failed", e);
                        }

                        suite.setAttribute("IS_CHILD_OF_PARENT", true);
                        suite.setAttribute("PARENT_SUITE_NAME", parentSuiteName);
                        suite.setAttribute("PARENT_REPORTER", staticSharedReporter);

                        // Treat this suite as child so onFinish(ISuite) increments completion counter.
                        System.out.println("[Listener] 🔗 CHILD suite-file detected: " + suiteName + " (parent=" + parentSuiteName + ")");
                        return;
                    }
                }
            }
        }

        // If a master suite with <suite-files> has already initialized the shared reporter,
        // then every subsequent suite-file is a CHILD and must NOT trigger parent finalization.
        synchronized (reporterLock) {
            if (staticSharedReporter != null
                    && isParentSuite
                    && childSuiteCount > 0
                    && !hasSuiteFiles
                    && !isRetrySuite
                    // Use suite XML file identity (not suite name) because child XMLs often share the same <suite name="...">
                    && !sameSuiteFile(suiteFileName, parentSuiteXmlFileName)) {
                suite.setAttribute("IS_CHILD_OF_PARENT", true);
                suite.setAttribute("PARENT_SUITE_NAME", parentSuiteName);
                suite.setAttribute("PARENT_REPORTER", staticSharedReporter);
                System.out.println("[Listener] 🔗 CHILD suite-file detected: " + suiteName
                        + " (parent=" + parentSuiteName + ", file=" + baseNameOrNull(suiteFileName) + ")");
                return;
            }
        }
        
        // NEW: Check if this is a known nested child suite (Petramco_P1/P2/P3) - INCLUDES RETRY!
        boolean isNestedChild = isKnownNestedChild(suiteFileName);
        
        if (isNestedChild) {
            synchronized (reporterLock) {
                if (staticSharedReporter == null) {
                    // FIRST child suite - initialize parent context
                    if (isRetrySuite) {
                        System.out.println("[Listener] ✅ FIRST RETRY CHILD - Initializing nested retry execution");
                    } else {
                        System.out.println("[Listener] ✅ FIRST CHILD SUITE - Initializing nested execution");
                    }
                    isParentSuite = true;
                    childSuiteCount = 3; // P1, P2, P3
                    parentSuiteName = isRetrySuite ? "Regression_SolQA_Automation_Suite - Retry" : "Regression_SolQA_Automation_Suite";
                    childSuiteCounter = 0;
                    childSuiteCompletionCounter = 0;
                    
                    if (!isRetrySuite) {
                        failedTestContexts.clear();
                    }
                    
                    // Create appropriate report file
                    String reportFileName = isRetrySuite ? "report_child.html" : "report_parent.html";
                    parentReportFile = new File(reportPathStr, reportFileName);
                    
                    // CRITICAL: Set protection flag IMMEDIATELY for report_parent.html
                    if (!isRetrySuite && reportFileName.equals("report_parent.html")) {
                        reportParentCreated = true;
                        log.info("[Listener] 🔒 report_parent.html protection flag SET (early activation)");
                        System.out.println("[Listener] 🔒🔒🔒 report_parent.html PROTECTION ACTIVATED IMMEDIATELY");
                    }
                    
                    // CRITICAL SAFEGUARD: NEVER delete report_parent.html during retry execution!
                    // Only delete the specific file we're about to create:
                    // - For retry: ONLY delete report_child.html (if it exists from previous retry)
                    // - For parent: ONLY delete report_parent.html (if it exists from previous run)
                    if (parentReportFile.exists()) {
                        // Double-check the actual filename to protect report_parent.html
                        String actualFileName = parentReportFile.getName();
                        
                        if (actualFileName.equals("report_parent.html")) {
                            // ABSOLUTE PROTECTION: Check the protection flag first
                            if (reportParentCreated) {
                                // BLOCKED: report_parent.html is protected
                                log.error("[Listener] 🚫🚫🚫 BLOCKED: report_parent.html is PROTECTED by flag!");
                                System.out.println("[Listener] 🚫🚫🚫 BLOCKED: report_parent.html is PROTECTED!");
                                System.out.println("[Listener] 🔒 Protection flag is SET - file will NOT be deleted");
                            } else if (isRetrySuite) {
                                // BLOCKED: Never delete report_parent.html during retry
                                log.error("[Listener] 🚫 BLOCKED attempt to delete report_parent.html during retry!");
                                System.out.println("[Listener] 🚫 BLOCKED: Retry tried to delete report_parent.html!");
                                System.out.println("[Listener] 🔒 report_parent.html is PROTECTED and preserved");
                            } else {
                                // Allow parent run to delete old report_parent.html from previous run
                                log.info("[Listener] Deleting old report_parent.html from previous run");
                                boolean deleted = parentReportFile.delete();
                                System.out.println("[Listener] 🗑️  Deleted old report_parent.html: " + deleted);
                            }
                        } else if (actualFileName.equals("report_child.html")) {
                            // Safe to delete report_child.html during retry (from previous retry if any)
                            log.info("[Listener] Deleting old report_child.html to create fresh retry report");
                            boolean deleted = parentReportFile.delete();
                            System.out.println("[Listener] 🗑️  Deleted old report_child.html: " + deleted);
                        }
                    } else {
                        System.out.println("[Listener] 📝 Creating new " + reportFileName + " (no existing file to delete)");
                    }
                    
                    // Create static shared reporter for ALL child suites
                    final File directory = new File(reportPathStr);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    
                    try {
                        final ExtentHtmlReporter htmlRep = new ExtentHtmlReporter(parentReportFile);
                        htmlRep.config().setChartVisibilityOnOpen(true);
                        htmlRep.config().setTheme(Theme.DARK);
                        
                        if (isRetrySuite) {
                            htmlRep.config().setDocumentTitle("Solutions QA Automation - Retry Report");
                            htmlRep.config().setReportName(parentSuiteName + " - Failed Tests Retry");
                        } else {
                            htmlRep.config().setDocumentTitle("Consolidated Solutions QA Automation Report");
                            htmlRep.config().setReportName(parentSuiteName + " - Consolidated Report");
                        }
                        htmlRep.config().setEncoding("utf-8");
                        
                        final File emailReportFile = new File(reportPathStr, "emailable-report.html");
                        final EmailReporter emailRep = new EmailReporter(emailReportFile);
                        
                        staticSharedReporter = new ExtentReports();
                        staticSharedReporter.attachReporter(htmlRep, emailRep);
                        staticSharedReporter.setReportUsesManualConfiguration(true);
                        
                        configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
                        staticSharedReporter.setSystemInfo("Setup Name ", configProp.get("setupName"));
                        staticSharedReporter.setSystemInfo("Product Version", configProp.get("productVersion"));
                        
                        if (isRetrySuite) {
                            staticSharedReporter.setSystemInfo("Execution Mode", "Retry (Failed Tests Only)");
                            staticSharedReporter.setSystemInfo("Report Type", "Retry Report - report_child.html");
                        } else {
                            staticSharedReporter.setSystemInfo("Execution Mode", "Consolidated (Nested Suites)");
                            staticSharedReporter.setSystemInfo("Report Type", "Parent Report - report_parent.html");
                        }
                        
                        System.out.println("[Listener] ✅ Static shared reporter created for " + reportFileName);
                        System.out.println("[Listener] 📊 ALL 3 child suites will write to this report");
                    } catch (Exception e) {
                        log.error("[Listener] Failed to create static shared reporter", e);
                        System.out.println("[Listener] ❌ Error creating shared reporter: " + e.getMessage());
                    }
                } else {
                    // Subsequent child suite - reuse existing reporter
                    String suiteType = isRetrySuite ? "RETRY CHILD" : "CHILD SUITE";
                    System.out.println("[Listener] 🔗 " + suiteType + " #" + (childSuiteCounter + 1) + " - Using shared reporter");
                }
                
                // Mark this suite as a child
                suite.setAttribute("IS_CHILD_OF_PARENT", true);
                suite.setAttribute("PARENT_SUITE_NAME", parentSuiteName);
                
                return; // Skip normal suite setup
            }
        }
        
        // Check if this is a parent suite with child suite-files (OLD approach - won't trigger for suite-files)
        if (!isRetrySuite && suite.getXmlSuite().getChildSuites() != null && !suite.getXmlSuite().getChildSuites().isEmpty()) {
            synchronized (reporterLock) {
                isParentSuite = true;
                childSuiteCount = suite.getXmlSuite().getChildSuites().size();
                parentSuiteName = suiteName;
                childSuiteCounter = 0;
                childSuiteCompletionCounter = 0;  // Reset completion counter
                
                // Clear any previous failed test contexts for fresh run
                failedTestContexts.clear();
                
                log.info("[Listener] Detected NESTED suite: " + suiteName + 
                        " with " + childSuiteCount + " child suite(s) - Consolidated reporting enabled");
                System.out.println("[Listener] 📊 NESTED suite detected: " + suiteName + 
                        " with " + childSuiteCount + " child suite(s)");
                System.out.println("[Listener] 🔄 Will generate single report_parent.html for ALL child suites");
                
                // Create report file directly without timestamp
                if (isRetrySuite) {
                    parentReportFile = new File(reportPathStr, "report_child.html");
                    // Delete existing if present
                    if (parentReportFile.exists()) {
                        parentReportFile.delete();
                    }
                    System.out.println("[Listener] 🔄 RETRY suite detected - Will generate report_child.html");
                } else {
                    parentReportFile = new File(reportPathStr, "report_parent.html");
                    // Delete existing if present
                    if (parentReportFile.exists()) {
                        parentReportFile.delete();
                    }
                }
                
                // Create STATIC SHARED REPORTER for all child suites
                final File directory = new File(reportPathStr);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                
                ExtentHtmlReporter htmlRep = new ExtentHtmlReporter(parentReportFile);
                htmlRep.config().setTestViewChartLocation(ChartLocation.TOP);
                htmlRep.config().setChartVisibilityOnOpen(true);
                htmlRep.config().setTheme(Theme.DARK);
                
                if (isRetrySuite) {
                    htmlRep.config().setDocumentTitle("Solutions QA Automation - Retry Report");
                    htmlRep.config().setReportName(suiteName + " - Failed Tests Retry");
                } else {
                    htmlRep.config().setDocumentTitle("Consolidated Solutions QA Automation Report");
                    htmlRep.config().setReportName(suiteName + " - Consolidated Report");
                }
                htmlRep.config().setEncoding("utf-8");
                
                final File emailReportFile = new File(reportPathStr, "emailable-report.html");
                final EmailReporter emailRep = new EmailReporter(emailReportFile);
                
                staticSharedReporter = new ExtentReports();
                staticSharedReporter.attachReporter(htmlRep, emailRep);
                staticSharedReporter.setReportUsesManualConfiguration(true);
                
                // Set system info
                configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
                staticSharedReporter.setSystemInfo("Setup Name ", configProp.get("setupName"));
                staticSharedReporter.setSystemInfo("Product Version", configProp.get("productVersion"));
                staticSharedReporter.setSystemInfo("Execution Mode", isRetrySuite ? "Retry (Failed Tests Only)" : "Consolidated (Nested Suites)");
                
                log.info("[Listener] ✅ Static shared reporter created for nested suite execution");
                System.out.println("[Listener] ✅ Static shared reporter created - ALL child suites will use this");
            }
            
        } else {
            // Non-nested suite - still generate report_parent.html for consistent naming
            isParentSuite = false;
            staticSharedReporter = null;  // Ensure no shared reporter for non-nested
            
            log.info("[Listener] Detected NON-NESTED suite: " + suiteName + " - Individual reporting with report_parent.html");
            System.out.println("[Listener] 📄 NON-NESTED suite detected: " + suiteName);
            
            // Create report file directly without timestamp
                if (isRetrySuite) {
                    parentReportFile = new File(reportPathStr, "report_child.html");
                    // Delete ONLY report_child.html, NEVER report_parent.html
                    if (parentReportFile.exists() && parentReportFile.getName().equals("report_child.html")) {
                        parentReportFile.delete();
                        System.out.println("[Listener] 🗑️  Deleted old report_child.html");
                    }
                    System.out.println("[Listener] 🔄 RETRY suite detected - Will generate report_child.html");
                } else {
                    parentReportFile = new File(reportPathStr, "report_parent.html");
                    // Check protection flag before deleting
                    if (parentReportFile.exists() && parentReportFile.getName().equals("report_parent.html")) {
                        if (reportParentCreated) {
                            log.error("[Listener] 🚫 BLOCKED by protection flag - report_parent.html will NOT be deleted");
                            System.out.println("[Listener] 🚫 PROTECTED: report_parent.html will not be deleted");
                        } else {
                            parentReportFile.delete();
                            System.out.println("[Listener] 🗑️  Deleted old report_parent.html from previous run");
                        }
                    }
                    System.out.println("[Listener] Will generate report_parent.html");
                }
            
            // Store suite attributes so tests use the same reporter
            suite.setAttribute("PARENT_REPORTER", reporter);
            suite.setAttribute("IS_PARENT_SUITE", true);
            suite.setAttribute("IS_RETRY_SUITE", isRetrySuite);
        }
        
        // Configure report file name based on suite type
        final String configFile = suite.getParameter("report.config");
        if (!Strings.isNullOrEmpty(configFile) && htmlReporter != null) {
            htmlReporter.loadXMLConfig(configFile);
        }
        
        suite.setAttribute(REPORTER_ATTR, reporter);
    }
    
    /**
     * Called when suite finishes - renames report to report_parent.html or report_child.html based on suite type
     */
    @Override
    public void onFinish(ISuite suite) {
        String suiteName = suite.getName();
        log.info("[Listener] Suite onFinish called for: " + suiteName);
        System.out.println("[Listener] Suite onFinish called for: " + suiteName);
        
        // Check if this is a CHILD suite (part of nested execution)
        Boolean isChildOfParent = (Boolean) suite.getAttribute("IS_CHILD_OF_PARENT");
        // Extra safety: if we are inside a master <suite-files> run, ANY suite without suite-files is a child,
        // even if suite names are identical and the onStart(ISuite) child marker didn't get set.
        boolean hasSuiteFiles = hasChildSuiteFiles(suite);
        boolean shouldTreatAsChild =
                (isChildOfParent != null && isChildOfParent)
                        || (staticSharedReporter != null
                        && isParentSuite
                        && childSuiteCount > 0
                        && !hasSuiteFiles
                        && !isRetrySuite
                        && !sameSuiteFile(suite.getXmlSuite().getFileName(), parentSuiteXmlFileName));

        if (shouldTreatAsChild) {
            // This is a child suite - increment completion counter and check if all children done
            synchronized (reporterLock) {
                childSuiteCompletionCounter++;
                log.info("[Listener] 🔗 Child suite finishing: " + suiteName + " (" + childSuiteCompletionCounter + "/" + childSuiteCount + ")");
                System.out.println("[Listener] 🔗 Child suite finished: " + suiteName + " (" + childSuiteCompletionCounter + "/" + childSuiteCount + ")");
                
                // Check if ALL children have completed
                if (childSuiteCompletionCounter < childSuiteCount) {
                    System.out.println("[Listener] ⏳ Waiting for remaining " + (childSuiteCount - childSuiteCompletionCounter) + " child suite(s) to finish...");
                    return;  // Not all children done yet - wait for more
                }
                
                // ALL children have completed - now do the parent finalization!
                System.out.println("[Listener] ✅ All " + childSuiteCount + " child suites completed - Finalizing parent suite " + parentSuiteName + " now!");
            }
            // Fall through to parent finalization logic below
        }
        
        // For PARENT suite with nested suite-files OR master suite, only finalize after all children complete
        if (isParentSuite && staticSharedReporter != null) {
            synchronized (reporterLock) {
                log.info("[Listener] Parent/Master suite finishing - Processing consolidated results");
                System.out.println("[Listener] ✅ Master suite execution completed");
                
                // Flush the static shared reporter
                staticSharedReporter.flush();
                log.info("[Listener] Static shared reporter flushed");
                
                // Report file is already named correctly (no renaming needed)
                if (parentReportFile != null) {
                    String reportType = isRetrySuite ? "report_child.html" : "report_parent.html";
                    log.info("[Listener] Report saved as: " + parentReportFile.getAbsolutePath());
                    System.out.println("[Listener] ✅ Report saved as: " + reportType);
                    System.out.println("[Listener] 📊 Contains results from ALL child suites");
                    
                    if (!isRetrySuite) {
                        System.out.println("[Listener] 🔒 report_parent.html is PROTECTED (flag was set during creation)");
                    }
                }
                
                // Generate consolidated retry XML from collected failures
                log.info("[Listener] Checking for failed tests... isRetrySuite=" + isRetrySuite + ", failedTestContexts.size()=" + failedTestContexts.size());
                System.out.println("[Listener] 🔍 Checking for failures: " + failedTestContexts.size() + " test context(s) with failures");
                
                if (!isRetrySuite && !failedTestContexts.isEmpty()) {
                    // Count total failures across all child suites
                    int totalFailures = 0;
                    for (ITestContext ctx : failedTestContexts) {
                        int failed = ctx.getFailedTests().size();
                        totalFailures += failed;
                        System.out.println("[Listener]   - " + ctx.getName() + ": " + failed + " failed test(s)");
                    }
                    
                    log.info("[Listener] Generating CONSOLIDATED retry XML from " + failedTestContexts.size() + " child suite(s) with " + totalFailures + " total failures");
                    System.out.println("[Listener] 📋 Creating CONSOLIDATED retry XML:");
                    System.out.println("[Listener]    - " + failedTestContexts.size() + " child suite(s) with failures");
                    System.out.println("[Listener]    - " + totalFailures + " total failed test(s)");
                    
                    // Generate a single consolidated retry XML with all failures (FLAT structure)
                    generatedRetryXmlFile = generateConsolidatedRetryXml(failedTestContexts, parentSuiteName);
                    
                    if (generatedRetryXmlFile != null) {
                        System.out.println("[Listener] ✅ Consolidated testng-failed.xml created successfully");
                        System.out.println("[Listener] 📝 File: " + generatedRetryXmlFile.getName());
                        System.out.println("[Listener] 📝 Contains failures from ALL " + failedTestContexts.size() + " child suites");
                    }
                } else if (failedTestContexts.isEmpty()) {
                    log.info("[Listener] No failed tests - skipping retry generation");
                    System.out.println("[Listener] ✅ All tests passed - no retry needed!");
                }
                
                // CRITICAL: Clear parentReportFile NOW to prevent retry from deleting it!
                File savedParentReport = parentReportFile;  // Keep reference for backup copy
                parentReportFile = null;
                System.out.println("[Listener] 🔒 report_parent.html protected - parentReportFile cleared");
                
                // NOTE: Do not copy/rename the parent report. The first run must remain as Reports/report_parent.html.
                
                // Execute retry if retry XML was generated
                if (generatedRetryXmlFile != null && !isRetrySuite && generatedRetryXmlFile.exists()) {
                    System.out.println("[Listener] 🚀 Starting automatic retry execution...");
                    executeRetryTests(generatedRetryXmlFile);
                } else if (generatedRetryXmlFile == null && !failedTestContexts.isEmpty()) {
                    log.error("[Listener] Failed to generate retry XML file!");
                    System.out.println("[Listener] ❌ Retry XML generation failed!");
                }
                
                // Reset static variables for next run
                // CRITICAL: For parent run, do NOT reset staticSharedReporter yet!
                // It will be reused by retry child suites, then reset after retry completes
                if (isRetrySuite) {
                    // Retry run complete - Reset everything BUT keep protection flag active
                    System.out.println("[Listener] 🔄 Retry execution complete - Resetting all static variables");
                    isParentSuite = false;
                    childSuiteCount = 0;
                    childSuiteCompletionCounter = 0;
                    isRetrySuite = false;
                    parentReportFile = null;
                    generatedRetryXmlFile = null;
                    failedTestContexts.clear();
                    parentSuiteName = null;
                    staticSharedReporter = null;
                    childSuiteCounter = 0;
                    
                    // CRITICAL: Keep protection flag ACTIVE - NEVER delete report_parent.html
                    // reportParentCreated stays TRUE forever to protect report_parent.html
                    System.out.println("[Listener] 🔒 report_parent.html protection flag KEPT ACTIVE (never reset)");
                    System.out.println("[Listener] ✅ report_parent.html will NEVER be deleted");
                    log.info("[Listener] reportParentCreated kept as TRUE - report_parent.html permanently protected");
                } else {
                    // Parent run complete - keep staticSharedReporter for retry execution
                    System.out.println("[Listener] ✅ Parent run complete - Variables will be reset after retry");
                    // Reset most things but keep staticSharedReporter alive temporarily
                    isParentSuite = false;  // Allow retry to detect as new parent
                    childSuiteCount = 0;
                    childSuiteCompletionCounter = 0;
                    // Don't reset isRetrySuite, parentReportFile, staticSharedReporter yet!
                }
            }
            return;
        }
        
        // NON-NESTED suite execution (original behavior)
        // Flush the report
        if (reporter != null) {
            reporter.flush();
            
            // For retry suites that wrote directly to report_child.html (parentReportFile is null)
            if (parentReportFile == null && isRetrySuite) {
                synchronized (reporterLock) {
                    retryReportCreated = false;  // Reset for next run
                }
                log.info("[Listener] Retry report saved as: report_child.html (no renaming needed)");
                System.out.println("[Listener] ✅ Retry report saved as: report_child.html");
                System.out.println("[Listener] 🔄 Contains only retried test results");
            }
        }
        
        // Report is already named correctly (no renaming needed)
        if (reporter != null && parentReportFile != null) {
            String reportType = isRetrySuite ? "report_child.html" : "report_parent.html";
            log.info("[Listener] Report saved as: " + parentReportFile.getAbsolutePath());
            System.out.println("[Listener] ✅ Report saved as: " + reportType);
            if (isRetrySuite) {
                System.out.println("[Listener] 🔄 Retry report - Original report_parent.html preserved");
            }
        }
        
        // For non-nested suites, execute retry if needed
        // BUT: Do NOT execute if we're a child of a parent suite with nested suite-files!
        boolean isChildOfNestedParent = false;
        synchronized (reporterLock) {
            // If parent suite variables are set, we're a child of nested execution - DO NOT execute retry individually!
            isChildOfNestedParent = (parentReportFile != null && parentSuiteName != null);
        }
        
        // One XML with several <test> nodes (e.g. Regression_FullRun: P1, P2, P3): merge failures and retry once.
        // Per-test onFinish used to overwrite generatedRetryXmlFile so only the last section (often P3) was retried.
        if (!isParentSuite && !isChildOfNestedParent && !isRetrySuite) {
            synchronized (reporterLock) {
                if (!multiTestSuiteFailedContexts.isEmpty()) {
                    log.info("[Listener] Multi-test suite — consolidated retry for "
                            + multiTestSuiteFailedContexts.size() + " section(s) with failures");
                    System.out.println("[Listener] 🔄 Multi-test suite: consolidated retry ("
                            + multiTestSuiteFailedContexts.size() + " failed section(s))");
                    List<ITestContext> toRetry = new ArrayList<>(multiTestSuiteFailedContexts);
                    multiTestSuiteFailedContexts.clear();
                    File retryFile = generateConsolidatedRetryXml(toRetry, suiteName);
                    if (retryFile != null && retryFile.exists()) {
                        executeRetryTests(retryFile);
                    }
                }
            }
        }
        
        if (!isParentSuite && !isChildOfNestedParent && generatedRetryXmlFile != null && !isRetrySuite && generatedRetryXmlFile.exists()) {
            log.info("[Listener] Standalone suite - executing retry now");
            executeRetryTests(generatedRetryXmlFile);
        } else if (isChildOfNestedParent) {
            log.info("[Listener] Child of nested parent - Skipping individual retry execution");
            System.out.println("[Listener] 📋 Child suite finished - Retry will be handled by parent suite");
        }
        
        // Reset static variables for non-nested runs
        if (!isParentSuite) {
            isRetrySuite = false;
            parentReportFile = null;
            generatedRetryXmlFile = null;
        }
    }
    
    /**
     * Execute retry tests from the generated retry XML file
     * @param retryXmlFile The retry XML file to execute
     */
    private void executeRetryTests(File retryXmlFile) {
        try {
            log.info("[Listener] ⏳ Initiating automatic retry execution...");
            System.out.println("[Listener] ⏳ Initiating automatic retry execution...");
            System.out.println("[Listener] 📋 Retry XML: " + retryXmlFile.getAbsolutePath());
            
            // CRITICAL: Verify report_parent.html protection is active
            System.out.println("[Listener] 🔒 VERIFY: reportParentCreated flag = " + reportParentCreated);
            if (reportParentCreated) {
                System.out.println("[Listener] ✅ report_parent.html is PROTECTED during retry execution");
                log.info("[Listener] report_parent.html protection is ACTIVE during retry");
            } else {
                System.out.println("[Listener] ⚠️  WARNING: reportParentCreated flag is FALSE!");
            }
            
            // CRITICAL: Reset ALL static variables before retry execution
            // This allows retry child suites to create their own NEW staticSharedReporter for report_child.html
            // BUT keep reportParentCreated = true to protect report_parent.html!
            synchronized (reporterLock) {
                reporter = null;
                staticSharedReporter = null;  // Must reset so retry creates NEW reporter for report_child.html
                childSuiteCounter = 0;
                childSuiteCompletionCounter = 0;
                isParentSuite = false;  // Reset so retry child can set it
                parentReportFile = null;
                retryReportCreated = false;
                // DO NOT reset reportParentCreated - keep protection active!
                log.info("[Listener] Reset static reporters (kept reportParentCreated=" + reportParentCreated + ")");
                System.out.println("[Listener] 🔄 Reset: Variables cleared but reportParentCreated KEPT = " + reportParentCreated);
            }
            
            // Create TestNG instance for retry execution
            org.testng.TestNG testng = new org.testng.TestNG();
            
            // Set the retry XML file
            List<String> suiteFiles = new ArrayList<>();
            suiteFiles.add(retryXmlFile.getAbsolutePath());
            testng.setTestSuites(suiteFiles);
            
            // Set verbose mode for debugging
            testng.setVerbose(2);
            
            // Use current working directory
            testng.setOutputDirectory(USERDIR + File.separator + "test-output");
            
            log.info("[Listener] 🚀 Executing retry tests...");
            System.out.println("[Listener] 🚀 Executing retry tests from: " + retryXmlFile.getName());
            System.out.println("[Listener] ================================================");
            
            // Execute the retry tests
            testng.run();
            
            // Check results
            boolean hasFailures = testng.hasFailure();
            
            if (hasFailures) {
                log.warn("[Listener] ⚠️ Retry execution completed with failures");
                System.out.println("[Listener] ⚠️ Retry execution completed with failures");
            } else {
                log.info("[Listener] ✅ Retry execution completed successfully - All retried tests passed!");
                System.out.println("[Listener] ✅ Retry execution completed successfully - All retried tests passed!");
            }
            
            System.out.println("[Listener] ================================================");
            System.out.println("[Listener] 📝 Check Reports folder for report_child.html");
            
        } catch (Exception e) {
            log.error("[Listener] ❌ Failed to execute retry tests", e);
            System.out.println("[Listener] ❌ Failed to execute retry tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

}