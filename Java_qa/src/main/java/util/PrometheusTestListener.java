package util;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * *********************************************************************
 * Purpose: Enhanced TestNG listener with Prometheus metrics integration
 * Suite Name: Framework File
 * Module Name: Metrics
 * @author sbonta
 * @version 1.0
 * @since 2025-09-03
 * Comments: Captures test timing and results for Prometheus metrics
 * *********************************************************************
 */
public class PrometheusTestListener implements ITestListener, ISuiteListener {

    private static final Logger log = Logger.getLogger(PrometheusTestListener.class.getName());
    
    // Track test start times for duration calculation
    private static final Map<String, Long> testStartTimes = new ConcurrentHashMap<>();
    private static String currentSuite = "unknown";
    private static String currentEnvironment = "unknown";
    private static String currentProduct = "unknown";
    private static String currentDeploymentJobId = "unknown";
    private static int runningTestCount = 0;
    
    // Track products and users seen in tests for better suite-level reporting
    private static final Map<String, String> testProductMap = new ConcurrentHashMap<>();
    private static final Map<String, String> testUserMap = new ConcurrentHashMap<>();
    
    // Track skipped tests to avoid duplicate recording
    private static final Set<String> recordedSkippedTests = new HashSet<>();
    
    // Configuration properties for user information
    private static Map<String, String> configProperties;
    private static final String USERDIR = System.getProperty("user.dir");

    // API-specific patterns for extraction
    private static final Pattern HTTP_STATUS_PATTERN = Pattern.compile("\\b([1-5]\\d{2})\\b");
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile("/[a-zA-Z0-9/_.-]+");

    @Override
    public void onStart(ISuite suite) {
        System.out.println("PrometheusTestListener.onStart() called for suite: " + suite.getName());
        log.info("PrometheusTestListener.onStart() called for suite: " + suite.getName());
        
        currentSuite = suite.getName();
        
        // Load configuration properties for user information
        loadConfigProperties();
        
        // Extract environment info from config properties with COMPREHENSIVE fallbacks
        if (configProperties != null) {
            currentEnvironment = configProperties.get("setupName");
            
            // Check if setupName is a placeholder (contains __)
            if (currentEnvironment != null && currentEnvironment.contains("__")) {
                log.warn("setupName contains placeholder: " + currentEnvironment + " - will try other sources");
                currentEnvironment = null;
            }
            
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                // Try multiple sources in priority order
                currentEnvironment = System.getProperty("setupName");
                log.info("Attempt 1 - System.getProperty('setupName'): " + currentEnvironment);
                
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    currentEnvironment = System.getenv("SETUP_NAME");
                    log.info("Attempt 2 - System.getenv('SETUP_NAME'): " + currentEnvironment);
                }
                
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    currentEnvironment = System.getenv("ENVIRONMENT");
                    log.info("Attempt 3 - System.getenv('ENVIRONMENT'): " + currentEnvironment);
                }
                
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    currentEnvironment = System.getenv("ENV");
                    log.info("Attempt 4 - System.getenv('ENV'): " + currentEnvironment);
                }
                
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    currentEnvironment = suite.getParameter("environment");
                    log.info("Attempt 5 - suite.getParameter('environment'): " + currentEnvironment);
                }
                
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    currentEnvironment = suite.getParameter("setupName");
                    log.info("Attempt 6 - suite.getParameter('setupName'): " + currentEnvironment);
                }
                
                // Try to extract from stackName if available
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    String stackName = configProperties.get("stackName");
                    if (stackName == null || stackName.trim().isEmpty() || stackName.contains("__")) {
                        stackName = System.getenv("STACK_NAME");
                    }
                    if (stackName != null && !stackName.trim().isEmpty() && !stackName.contains("__")) {
                        currentEnvironment = stackName;
                        log.info("Attempt 7 - Extracted from stackName: " + currentEnvironment);
                    }
                }
                
                // Try to extract from adminUserName domain
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    String adminUser = configProperties.get("adminUserName");
                    if (adminUser != null && !adminUser.contains("__")) {
                        // Extract environment from username patterns like "hannah_admin" -> likely from helixdemoXX
                        // This is a fallback - will use "discovered" as indicator
                        currentEnvironment = "auto-detected";
                        log.info("Attempt 8 - Auto-detected (from config presence): " + currentEnvironment);
                    }
                }
                
                if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                    currentEnvironment = "unknown";
                    log.warn("ALL ATTEMPTS FAILED - defaulting to 'unknown'");
                }
            }
            
            log.info("✓ Final currentEnvironment determined: " + currentEnvironment);
            
            // Extract product from suite name or config, defaulting to ITSM if not found
            currentProduct = extractProductFromSuite(currentSuite, configProperties);
            if (currentProduct == null || "unknown".equals(currentProduct)) {
                currentProduct = System.getProperty("productName", 
                    suite.getParameter("product") != null ? suite.getParameter("product") : "ITSM");
            }
        } else {
            // Fallback if config properties couldn't be loaded
            log.warn("Config properties are NULL - using system properties and environment variables");
            
            currentEnvironment = System.getProperty("setupName");
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                currentEnvironment = System.getenv("SETUP_NAME");
            }
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                currentEnvironment = System.getenv("ENVIRONMENT");
            }
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                currentEnvironment = System.getenv("ENV");
            }
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                currentEnvironment = suite.getParameter("environment");
            }
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                currentEnvironment = suite.getParameter("setupName");
            }
            if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
                currentEnvironment = "unknown";
            }
            
            log.info("✓ Final currentEnvironment (no config): " + currentEnvironment);
            
            currentProduct = System.getProperty("productName", 
                suite.getParameter("product") != null ? suite.getParameter("product") : "ITSM");
        }
        
        // Determine deployment job id using the same fallback logic used for test-level recording
        currentDeploymentJobId = getCurrentDeploymentJobId();
        
        // Debug logging for all extracted values
        System.out.println("=== PrometheusTestListener Configuration ===");
        System.out.println("Suite: " + currentSuite);
        System.out.println("Environment: " + currentEnvironment);
        System.out.println("Product: " + currentProduct);
        System.out.println("Deployment Job ID: " + currentDeploymentJobId);
        System.out.println("===========================================");
        
        // Debug logging for deployment job ID
        log.info("=== Deployment Job ID Debug ===");
        log.info("System property 'deployment_job_id': " + System.getProperty("deployment_job_id"));
        log.info("Suite parameter 'deploymentJobId': " + suite.getParameter("deploymentJobId"));
        log.info("Environment variable 'DEPLOYMENT_JOB_ID': " + System.getenv("DEPLOYMENT_JOB_ID"));
        log.info("Final currentDeploymentJobId: " + currentDeploymentJobId);
        log.info("===============================");
        
        log.info("Suite started: " + currentSuite + ", Environment: " + currentEnvironment + 
                ", Product: " + currentProduct + ", DeploymentJobId: " + currentDeploymentJobId);
        
        // Detect automation type and initialize appropriate metrics servers
        boolean isApiAutomation = isApiAutomationSuite(currentSuite);
        System.out.println("Detected automation type: " + (isApiAutomation ? "API" : "UI"));
        log.info("Detected automation type: " + (isApiAutomation ? "API" : "UI"));
        
        // Initialize UI metrics server (always initialized)
        System.out.println("Checking if Prometheus UI metrics server is running...");
        if (!PrometheusMetricsExporter.isRunning()) {
            System.out.println("Initializing Prometheus UI metrics server...");
            PrometheusMetricsExporter.initialize();
        } else {
            System.out.println("Prometheus UI metrics server is already running");
        }
        
        // Initialize API metrics server if this is API automation
        if (isApiAutomation) {
            System.out.println("Checking if Prometheus API metrics server is running...");
            if (!PrometheusMetricsExporter.isApiRunning()) {
                System.out.println("Initializing Prometheus API metrics server...");
                PrometheusMetricsExporter.initializeApiServer();
            } else {
                System.out.println("Prometheus API metrics server is already running");
            }
        }
        
        // Start the status transition timer
        System.out.println("Starting status transition timer...");
        PrometheusMetricsExporter.startStatusTransitionTimer();
        
        // Log current status
        System.out.println("Current metrics status: " + PrometheusMetricsExporter.getCurrentStatus());
        log.info("Current metrics status: " + PrometheusMetricsExporter.getCurrentStatus());
        
        // Suite initialization completed
        System.out.println("Suite initialization completed - " + (isApiAutomation ? "API" : "UI") + 
                          " metrics will be recorded as tests execute");
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("=== SUITE FINISH CALLED ===");
        log.info("Suite finishing: " + currentSuite);
        
        // Determine overall suite result based on test results
        String suiteResult = determineSuiteResult(suite);
        
        // Determine primary product and user for suite-level metrics
        String suitePrimaryProduct = getSuitePrimaryProduct();
        String suitePrimaryUser = getSuitePrimaryUser();
        
        log.info("Suite result determined: " + suiteResult + " for suite: " + currentSuite + 
                ", Primary Product: " + suitePrimaryProduct + ", Primary User: " + suitePrimaryUser);
        System.out.println("Suite result determined: " + suiteResult + " for suite: " + currentSuite + 
                          ", Primary Product: " + suitePrimaryProduct + ", Primary User: " + suitePrimaryUser);
        
        // Detect automation type for suite completion
        boolean isApiAutomation = isApiAutomationSuite(currentSuite);
        
        // Record suite completion based on automation type
        System.out.println("Recording " + (isApiAutomation ? "API" : "UI") + " suite execution completion...");
        
        if (isApiAutomation) {
            PrometheusMetricsExporter.recordApiSuiteExecution(currentSuite, currentEnvironment, 
                                                             suitePrimaryProduct, getCurrentDeploymentJobId(), suiteResult, suitePrimaryUser);
            log.info("API Suite finished: " + currentSuite + ", Result: " + suiteResult + 
                    " - API Suite metrics recorded");
            
            // Update running API tests to 0
            PrometheusMetricsExporter.updateApiTestsRunning(currentSuite, currentEnvironment, suitePrimaryProduct, 
                                                          getCurrentDeploymentJobId(), suitePrimaryUser, 0);
        } else {
            PrometheusMetricsExporter.recordSuiteExecution(currentSuite, currentEnvironment, 
                                                          suitePrimaryProduct, getCurrentDeploymentJobId(), suiteResult, suitePrimaryUser);
            log.info("UI Suite finished: " + currentSuite + ", Result: " + suiteResult + 
                    " - UI Suite metrics recorded");
            
            // Update running UI tests to 0
            PrometheusMetricsExporter.updateTestsRunning(currentSuite, currentEnvironment, suitePrimaryProduct, 
                                                        getCurrentDeploymentJobId(), suitePrimaryUser, 0);
        }
        
        // Fix #2: Capture all skipped tests from XML
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("📊 CAPTURING SKIPPED TESTS FROM TESTNG XML");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        String outputDir = suite.getOutputDirectory();
        String xmlPath = outputDir + "/testng-results.xml";
        
        // Wait a moment for TestNG to finish writing the XML file
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        captureSkippedTestsFromXML(xmlPath);
        
        System.out.println("═══════════════════════════════════════════════════════════\n");
        // End Fix #2
        
        // Schedule server shutdown after suite completion to keep metrics available for configured delay
        System.out.println("Scheduling metrics server shutdown after suite completion...");
        PrometheusMetricsExporter.scheduleServerShutdownAfterSuite(currentSuite, isApiAutomation);
        
        System.out.println("Suite execution completed: " + currentSuite + " (Result: " + suiteResult + 
                          ") - Test-level metrics were streaming during execution, suite-level completion metrics now available");
        System.out.println("Metrics servers will remain active for " + PrometheusMetricsExporter.getServerShutdownDelaySeconds() + " seconds for Prometheus scraping");
        
        // Keep suite execution active for the same duration (this will hold the suite execution)
        System.out.println("Keeping test automation suite active for configured delay period...");
        PrometheusMetricsExporter.keepSuiteExecutionActive(currentSuite, isApiAutomation);
        
        System.out.println("=== SUITE FINISH COMPLETED ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testKey = getTestKey(result);
        String testClass = result.getTestClass().getName();
        String testName = result.getMethod().getMethodName();
        
        // Extract user and product information from the test being executed
        String testUser = extractUserFromTest(result);
        String testProduct = extractProductFromTest(result);
        
        // DEBUG: Show key extraction info
        System.out.println("� TEST: " + testName + " -> Product: " + testProduct + ", User: " + testUser);
        
        // Update current context with test-specific information
        if (testUser != null && !testUser.equals("unknown")) {
            testUserMap.put(testKey, testUser);
            log.info("Extracted user from test: " + testUser + " for test: " + testName);
        }
        
        if (testProduct != null && !testProduct.equals("unknown")) {
            testProductMap.put(testKey, testProduct);
            currentProduct = testProduct; // Update current product for this test
            log.info("Extracted product from test: " + testProduct + " for test: " + testName);
        }
        
        testStartTimes.put(testKey, System.currentTimeMillis());
        
        // Determine if this is an API test for running count tracking
        boolean isApiTest = isApiTest(result, currentSuite);
        
        synchronized (this) {
            runningTestCount++;
            if (isApiTest) {
                PrometheusMetricsExporter.updateApiTestsRunning(currentSuite, currentEnvironment, 
                                                               currentProduct, getCurrentDeploymentJobId(), 
                                                               testUser != null ? testUser : getCurrentUser(), runningTestCount);
                System.out.println("🔗 API TEST STARTED: " + testName + " (Running count: " + runningTestCount + ")");
            } else {
                PrometheusMetricsExporter.updateTestsRunning(currentSuite, currentEnvironment, 
                                                            currentProduct, getCurrentDeploymentJobId(), 
                                                            testUser != null ? testUser : getCurrentUser(), runningTestCount);
                System.out.println("UI TEST STARTED: " + testName + " (Running count: " + runningTestCount + ")");
            }
        }
        
        log.debug("Test started: " + testKey + " with user: " + testUser + " and product: " + testProduct);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        recordTestCompletion(result, "PASS", null);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String errorType = "assertion_failure";
        if (result.getThrowable() != null) {
            errorType = result.getThrowable().getClass().getSimpleName().toLowerCase();
        }
        recordTestCompletion(result, "FAIL", errorType);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testKey = getTestKey(result);
        recordedSkippedTests.add(testKey);
        recordTestCompletion(result, "SKIP", null);
    }

    private void recordTestCompletion(ITestResult result, String testResult, String errorType) {
        String testKey = getTestKey(result);
        String testClass = result.getTestClass().getName();
        String testName = result.getMethod().getMethodName();
        
        // Extract test-specific user and product information
        String testUser = extractUserFromTest(result);
        String testProduct = extractProductFromTest(result);
        
        // Use test-specific product if available, otherwise use current product
        String effectiveProduct = (testProduct != null && !testProduct.equals("unknown")) ? testProduct : currentProduct;
        String effectiveUser = (testUser != null && !testUser.equals("unknown")) ? testUser : getCurrentUser();
        
        // Calculate test duration
        Long startTime = testStartTimes.remove(testKey);
        double duration = 0.0;
        if (startTime != null) {
            duration = (System.currentTimeMillis() - startTime) / 1000.0;
        }
        
        // Determine if this is an API test
        boolean isApiTest = isApiTest(result, currentSuite);
        
        synchronized (this) {
            // Fix #1: Only decrement if test actually started
            if (startTime != null) {
                runningTestCount--;
                if (isApiTest) {
                    PrometheusMetricsExporter.updateApiTestsRunning(currentSuite, currentEnvironment, 
                                                                   effectiveProduct, getCurrentDeploymentJobId(), effectiveUser, runningTestCount);
                } else {
                    PrometheusMetricsExporter.updateTestsRunning(currentSuite, currentEnvironment, 
                                                                effectiveProduct, getCurrentDeploymentJobId(), effectiveUser, runningTestCount);
                }
            } else {
                log.debug("Test " + testName + " skipped before starting - not decrementing running count");
                System.out.println("⏭️  SKIPPED (never started): " + testName + " - Running count: " + runningTestCount);
            }
        }
        
        // Record metrics with test-specific information
        String deploymentJobId = getCurrentDeploymentJobId();
        String jiraTestCaseId = getJiraTestCaseId(result);
        
        if (isApiTest) {
            // Extract API-specific information
            String apiEndpoint = extractApiEndpoint(result);
            String httpMethod = extractHttpMethod(result);
            String httpStatusCode = extractHttpStatusCode(result);
            
            log.info("Recording API test metrics - DeploymentJobId: " + deploymentJobId + ", User: " + effectiveUser + 
                    ", Product: " + effectiveProduct + ", Test: " + testClass + "." + testName + ", Result: " + testResult + 
                    ", JiraID: " + jiraTestCaseId + ", Endpoint: " + apiEndpoint + ", Method: " + httpMethod + 
                    ", StatusCode: " + httpStatusCode);
            
            PrometheusMetricsExporter.recordApiTestExecution(
                currentSuite, currentEnvironment, effectiveProduct,
                testClass, testName, testResult, duration, errorType,
                deploymentJobId, effectiveUser, jiraTestCaseId,
                apiEndpoint, httpMethod, httpStatusCode
            );
            
            log.info("🔗 API Test completed: " + testName + " -> Product: " + effectiveProduct + ", User: " + effectiveUser + 
                    ", Endpoint: " + apiEndpoint + ", Method: " + httpMethod + ", Result: " + testResult + 
                    ", Duration: " + duration + "s" + ", StatusCode: " + httpStatusCode + ", JiraID: " + jiraTestCaseId);
        } else {
            log.info("Recording UI test metrics - DeploymentJobId: " + deploymentJobId + ", User: " + effectiveUser + 
                    ", Product: " + effectiveProduct + ", Test: " + testClass + "." + testName + ", Result: " + testResult + 
                    ", JiraID: " + jiraTestCaseId + ", Status: " + PrometheusMetricsExporter.getCurrentStatus());
            
            PrometheusMetricsExporter.recordTestExecution(
                currentSuite, currentEnvironment, effectiveProduct,
                testClass, testName, testResult, duration, errorType,
                deploymentJobId, effectiveUser, jiraTestCaseId
            );
            
            log.info("Test completed: " + testName + ", Result: " + testResult + 
                    ", Duration: " + duration + "s" + 
                    (errorType != null ? ", Error: " + errorType : "") +
                    ", User: " + effectiveUser + ", Product: " + effectiveProduct + ", JiraID: " + jiraTestCaseId);
        }
    }

    private String getTestKey(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    /**
     * Parse TestNG XML to capture all skipped tests (Fix #2)
     */
    private void captureSkippedTestsFromXML(String xmlPath) {
        try {
            File xmlFile = new File(xmlPath);
            if (!xmlFile.exists()) {
                log.warn("TestNG results XML not found at: " + xmlPath);
                return;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            Element root = doc.getDocumentElement();
            String totalSkipped = root.getAttribute("skipped");
            
            log.info("TestNG XML reports " + totalSkipped + " skipped tests - checking for uncaptured skips");
            System.out.println("📊 TestNG XML: " + totalSkipped + " skipped tests - checking for uncaptured skips");
            
            NodeList testMethods = doc.getElementsByTagName("test-method");
            int capturedSkips = 0;
            int alreadyRecorded = 0;
            
            for (int i = 0; i < testMethods.getLength(); i++) {
                Element testMethod = (Element) testMethods.item(i);
                String status = testMethod.getAttribute("status");
                String isConfig = testMethod.getAttribute("is-config");
                
                if ("SKIP".equals(status) && "false".equals(isConfig)) {
                    String testName = testMethod.getAttribute("name");
                    String signature = testMethod.getAttribute("signature");
                    
                    String testClass = signature;
                    if (signature.contains("(")) {
                        testClass = signature.substring(0, signature.indexOf("("));
                        if (testClass.contains(".")) {
                            testClass = testClass.substring(0, testClass.lastIndexOf("."));
                        }
                    }
                    
                    String testKey = testClass + "." + testName;
                    if (!recordedSkippedTests.contains(testKey)) {
                        log.info("Capturing skipped test from XML: " + testKey);
                        System.out.println("   ⏭️  Capturing: " + testKey);
                        
                        String testProduct = extractProductFromTestName(testName);
                        String effectiveProduct = (testProduct != null && !testProduct.equals("unknown")) ? testProduct : currentProduct;
                        
                        boolean isApiSuite = isApiAutomationSuite(currentSuite);
                        if (isApiSuite) {
                            PrometheusMetricsExporter.recordApiTestExecution(
                                currentSuite, currentEnvironment, effectiveProduct,
                                testClass, testName, "SKIP", 0.0, "test_disabled",
                                getCurrentDeploymentJobId(), getCurrentUser(), 
                                PrometheusMetricsExporter.extractJiraTestCaseId(testName, testClass),
                                "unknown", "unknown", "unknown"
                            );
                        } else {
                            PrometheusMetricsExporter.recordTestExecution(
                                currentSuite, currentEnvironment, effectiveProduct,
                                testClass, testName, "SKIP", 0.0, "test_disabled",
                                getCurrentDeploymentJobId(), getCurrentUser(),
                                PrometheusMetricsExporter.extractJiraTestCaseId(testName, testClass)
                            );
                        }
                        
                        recordedSkippedTests.add(testKey);
                        capturedSkips++;
                    } else {
                        alreadyRecorded++;
                    }
                }
            }
            
            log.info("Captured " + capturedSkips + " additional skipped tests from XML (" + alreadyRecorded + " were already recorded)");
            System.out.println("📊 Result: " + capturedSkips + " new skipped tests captured from XML");
            System.out.println("   (" + alreadyRecorded + " were already captured during test execution)");
            
        } catch (Exception e) {
            log.error("Error parsing TestNG XML for skipped tests: " + e.getMessage(), e);
            System.err.println("⚠️  Error parsing TestNG XML: " + e.getMessage());
        }
    }

    private String extractProductFromTestName(String testName) {
        if (testName == null) return "unknown";
        String upper = testName.toUpperCase();
        if (upper.contains("ITSM")) return "ITSM";
        if (upper.contains("SMARTIT")) return "SMARTIT";
        if (upper.contains("DWP")) return "DWP";
        if (upper.contains("BWF")) return "BWF";
        return "unknown";
    }

    private String getErrorType(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        String errorMessage = throwable.getMessage();
        String exceptionType = throwable.getClass().getSimpleName();
        
        if (errorMessage != null) {
            String lowerError = errorMessage.toLowerCase();
            
            if (lowerError.contains("timeout") || exceptionType.contains("Timeout")) {
                return "timeout";
            } else if (lowerError.contains("element not found") || lowerError.contains("no such element")) {
                return "element_not_found";
            } else if (lowerError.contains("assertion") || exceptionType.contains("Assert")) {
                return "assertion_failure";
            } else if (lowerError.contains("connection") || lowerError.contains("network")) {
                return "network_error";
            } else if (exceptionType.contains("NullPointer")) {
                return "null_pointer";
            } else if (exceptionType.contains("WebDriver")) {
                return "webdriver_error";
            }
        }
        
        return "other";
    }

    /**
     * Extract Jira test case ID from SolqaDetails annotation
     */
    private String getJiraTestCaseId(ITestResult result) {
        try {
            Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
            if (testMethod.isAnnotationPresent(SolqaDetails.class)) {
                SolqaDetails annotation = testMethod.getAnnotation(SolqaDetails.class);
                String jiraId = annotation.JiraID();
                if (jiraId != null && !jiraId.trim().isEmpty() && !"JIRA-ID".equals(jiraId)) {
                    log.debug("Found Jira test case ID from annotation: " + jiraId + " for test: " + result.getMethod().getMethodName());
                    return jiraId.trim();
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract Jira test case ID from annotation: " + e.getMessage());
        }
        
        // Fallback to utility method in PrometheusMetricsExporter
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String jiraId = PrometheusMetricsExporter.extractJiraTestCaseId(methodName, className);
        
        log.debug("Final Jira test case ID for test " + methodName + ": " + jiraId);
        return jiraId;
    }

    /**
     * Get current deployment job ID with multiple fallback options
     */
    private static String getCurrentDeploymentJobId() {
        // Try system property first (from Maven -D parameter)
        String deploymentId = System.getProperty("deployment_job_id");
        log.debug("System.getProperty('deployment_job_id'): " + deploymentId);
        if (deploymentId != null && !deploymentId.trim().isEmpty() && !"unknown".equals(deploymentId)) {
            log.debug("Using deployment_job_id from system property: " + deploymentId);
            return deploymentId.trim();
        }
        
        // Try environment variable (from container)
        deploymentId = System.getenv("DEPLOYMENT_JOB_ID");
        log.debug("System.getenv('DEPLOYMENT_JOB_ID'): " + deploymentId);
        if (deploymentId != null && !deploymentId.trim().isEmpty() && !"unknown".equals(deploymentId)) {
            log.debug("Using deployment_job_id from environment variable: " + deploymentId);
            return deploymentId.trim();
        }
        
        // Try config properties
        if (configProperties == null) {
            loadConfigProperties();
        }
        if (configProperties != null) {
            deploymentId = configProperties.get("deployment_job_id");
            log.debug("configProperties.get('deployment_job_id'): " + deploymentId);
            if (deploymentId != null && !deploymentId.trim().isEmpty() && !"unknown".equals(deploymentId)) {
                log.debug("Using deployment_job_id from config properties: " + deploymentId);
                return deploymentId.trim();
            }
        }
        
        // Fallback to static variable
        log.debug("currentDeploymentJobId static variable: " + currentDeploymentJobId);
        if (currentDeploymentJobId != null && !"unknown".equals(currentDeploymentJobId)) {
            log.debug("Using deployment_job_id from static variable: " + currentDeploymentJobId);
            return currentDeploymentJobId;
        }
        
        log.warn("No deployment_job_id found, using 'unknown'");
        return "unknown";
    }

    private static String getCurrentUser() {
        try {
            // Ensure config properties are loaded
            if (configProperties == null) {
                loadConfigProperties();
            }
            
            // First, try to get user from config properties based on the current product
            if (configProperties != null) {
                String user = getUserFromProductConfig(currentProduct);
                if (user != null && !user.trim().isEmpty() && !user.contains("__")) {
                    log.debug("Found product-specific user from config: " + user + " for product: " + currentProduct);
                    return user;
                }
            }
            
            // Second, try to read user from JSON file for the current product only
            if (currentProduct != null && !currentProduct.equals("unknown")) {
                String user = getCurrentProductUser(currentProduct);
                if (user != null && !user.trim().isEmpty()) {
                    log.debug("Found application user: " + user + " from " + currentProduct + " JSON file");
                    return user;
                }
            }
            
            // Third, fallback to generic config properties
            if (configProperties != null) {
                // Try to get the logged-in application user from config properties
                // Priority order: AdminUser -> ISAdminUserName -> username -> ARAdminUserName -> DWPAdminUserName
                String user = configProperties.get("AdminUser");
                if (user != null && !user.trim().isEmpty() && !user.contains("__")) {
                    log.debug("Found generic AdminUser from config: " + user);
                    return user;
                }
                
                user = configProperties.get("ISAdminUserName");
                if (user != null && !user.trim().isEmpty() && !user.contains("__")) {
                    log.debug("Found ISAdminUserName from config: " + user);
                    return user;
                }
                
                user = configProperties.get("username");
                if (user != null && !user.trim().isEmpty() && !user.contains("__")) {
                    log.debug("Found username from config: " + user);
                    return user;
                }
                
                user = configProperties.get("ARAdminUserName");
                if (user != null && !user.trim().isEmpty() && !user.contains("__")) {
                    log.debug("Found ARAdminUserName from config: " + user);
                    return user;
                }
                
                user = configProperties.get("DWPAdminUserName");
                if (user != null && !user.trim().isEmpty() && !user.contains("__")) {
                    log.debug("Found DWPAdminUserName from config: " + user);
                    return user;
                }
            }
        } catch (Exception e) {
            log.warn("Could not load application user: " + e.getMessage());
        }
        
        // Final fallback to system user if no application user found
        String systemUser = System.getenv().getOrDefault("USER", System.getProperty("user.name", "unknown"));
        log.debug("Using system user as final fallback: " + systemUser);
        return systemUser;
    }

    /**
     * Get user from config properties based on the current product
     */
    private static String getUserFromProductConfig(String product) {
        if (configProperties == null || product == null) {
            return null;
        }
        
        // Try product-specific user properties first
        switch (product.toUpperCase()) {
            case "SMARTIT":
                String smartItUser = configProperties.get("ISAdminUserName");
                if (smartItUser != null && !smartItUser.trim().isEmpty() && !smartItUser.contains("__")) {
                    return smartItUser;
                }
                break;
            case "DWP":
                String dwpUser = configProperties.get("DWPAdminUserName");
                if (dwpUser != null && !dwpUser.trim().isEmpty() && !dwpUser.contains("__")) {
                    return dwpUser;
                }
                break;
            case "BWF":
                String bwfUser = configProperties.get("ARAdminUserName");
                if (bwfUser != null && !bwfUser.trim().isEmpty() && !bwfUser.contains("__")) {
                    return bwfUser;
                }
                break;
            case "ITSM":
            default:
                String itsmUser = configProperties.get("AdminUser");
                if (itsmUser != null && !itsmUser.trim().isEmpty() && !itsmUser.contains("__")) {
                    return itsmUser;
                }
                // Try adminUserName as fallback for ITSM
                String adminUser = configProperties.get("adminUserName");
                if (adminUser != null && !adminUser.trim().isEmpty() && !adminUser.contains("__")) {
                    return adminUser;
                }
                break;
        }
        
        return null;
    }
    
    /**
     * Get the actual user configured for the current product from JSON files
     */
    private static String getCurrentProductUser(String product) {
        if (product == null) {
            return null;
        }
        
        try {
            // Map product names to their JSON file names
            String appType = getAppTypeFromProduct(product);
            if (appType == null) {
                return null;
            }
            
            // Get the configured user for this product from JSON
            // This should ideally come from the test context or configuration
            String configuredUser = getConfiguredUserForProduct(appType);
            
            if (configuredUser != null) {
                String user = readUserFromJsonFile(appType, configuredUser);
                if (user != null && !user.trim().isEmpty()) {
                    return user;
                }
            }
            
            // If no specific user configured, try common admin users for this product
            String[] commonUsers = getCommonUsersForProduct(appType);
            for (String userType : commonUsers) {
                String user = readUserFromJsonFile(appType, userType);
                if (user != null && !user.trim().isEmpty()) {
                    log.debug("Using common user " + userType + " for product " + product);
                    return user;
                }
            }
            
        } catch (Exception e) {
            log.debug("Could not get user for product " + product + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Map product names to their corresponding app types for JSON files
     */
    private static String getAppTypeFromProduct(String product) {
        switch (product.toUpperCase()) {
            case "SMARTIT":
                return "SMARTIT";
            case "BWF":
                return "BWF";
            case "DWP":
                return "DWP";
            case "INNOVATIONSTUDIO":
                return "InnovationStudio";
            case "SSPLIVECHAT":
                return "SSPLIVECHAT";
            case "ITSM":
            default:
                return "ITSM";
        }
    }
    
    /**
     * Get configured user for a product (can be enhanced to read from test context)
     */
    private static String getConfiguredUserForProduct(String appType) {
        // This could be enhanced to read from:
        // 1. System properties: -DuserType=bmcopssupportagent
        // 2. Suite parameters
        // 3. Test context
        
        String userType = System.getProperty("userType");
        if (userType != null && !userType.trim().isEmpty()) {
            log.debug("Using userType from system property: " + userType);
            return userType;
        }
        
        // Try to get from config properties
        if (configProperties == null) {
            loadConfigProperties();
        }
        if (configProperties != null) {
            String configUser = configProperties.get("testUserType");
            if (configUser != null && !configUser.trim().isEmpty() && !configUser.contains("__")) {
                log.debug("Using testUserType from config: " + configUser);
                return configUser;
            }
        }
        
        return null; // No specific user configured
    }
    
    /**
     * Get common admin users for each product type
     */
    private static String[] getCommonUsersForProduct(String appType) {
        switch (appType.toUpperCase()) {
            case "BWF":
                return new String[]{"Elizabeth", "opsSupportAgent"};
            case "SMARTIT":
                return new String[]{"bmcopssupportagent", "bmcopsenduser"};
            case "DWP":
                return new String[]{"bmcopssupportagent", "pbunyon"};
            case "ITSM":
            default:
                return new String[]{"bmcopssupportagent", "bmcOpsManagerUser"};
        }
    }

    /**
     * Read user from JSON file in resources folder
     */
    private static String readUserFromJsonFile(String appType, String userKey) {
        try {
            String jsonFileName = appType + "Users";
            ReadJson readJson = new ReadJson();
            
            // Try to read user data from JSON file
            Map<Object, String> userData = readJson.getInputValuesMapIncludingLabelField(appType, jsonFileName, userKey);
            
            if (userData != null && !userData.isEmpty()) {
                String userName = userData.get("UserName");
                if (userName != null && !userName.trim().isEmpty()) {
                    return userName.trim();
                }
            }
        } catch (Exception e) {
            // Silently continue to next option - this is expected when user doesn't exist in this JSON file
            log.debug("Could not read user " + userKey + " from " + appType + " JSON file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Load configuration properties to get application user information
     */
    private static void loadConfigProperties() {
        try {
            // Support both execution modes:
            // - Running from repo root  -> <user.dir>/Java_qa/properties.config
            // - Running from Java_qa    -> <user.dir>/properties.config
            File direct = new File(USERDIR + File.separator + "properties.config");
            File nested = new File(USERDIR + File.separator + "Java_qa" + File.separator + "properties.config");

            File configFile = direct.exists() ? direct : nested;
            if (!configFile.exists()) {
                log.warn("Could not locate properties.config. Tried: " + direct.getAbsolutePath() + " and " + nested.getAbsolutePath());
                configProperties = null;
                return;
            }

            configProperties = ReadPropertiesFile.readProperties(configFile.getAbsolutePath());
            log.info("Loaded configuration properties for user detection from: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            log.warn("Could not load configuration properties: " + e.getMessage());
            configProperties = null;
        }
    }

    private String determineSuiteResult(ISuite suite) {
        // Check if there were any failures
        return suite.getResults().values().stream()
                .anyMatch(result -> result.getTestContext().getFailedTests().size() > 0) ? 
                "FAIL" : "PASS";
    }

    /**
     * Extract product name from suite name and configuration
     */
    private static String extractProductFromSuite(String suiteName, Map<String, String> configProperties) {
        if (suiteName == null) {
            return "ITSM";
        }
        
        String lowerSuiteName = suiteName.toLowerCase();
        
        // Check suite name for product indicators
        if (lowerSuiteName.contains("smartit")) {
            return "SMARTIT";
        } else if (lowerSuiteName.contains("dwp")) {
            return "DWP";
        } else if (lowerSuiteName.contains("bwf")) {
            return "BWF";
        } else if (lowerSuiteName.contains("cmdb")) {
            return "CMDB";
        } else if (lowerSuiteName.contains("ssplivechat") || lowerSuiteName.contains("livechat")) {
            return "SSPLIVECHAT";
        } else if (lowerSuiteName.contains("innovation")) {
            return "InnovationStudio";
        } else if (lowerSuiteName.contains("itsm")) {
            return "ITSM";
        }
        
        // Check if config has product-specific URLs to infer the product
        if (configProperties != null) {
            if (configProperties.containsKey("SmartIT_URL") && !configProperties.get("SmartIT_URL").contains("__")) {
                return "SMARTIT";
            } else if (configProperties.containsKey("DWP_URL") && !configProperties.get("DWP_URL").contains("__")) {
                return "DWP";
            } else if (configProperties.containsKey("CMDB_URL") && !configProperties.get("CMDB_URL").contains("__")) {
                return "CMDB";
            } else if (configProperties.containsKey("SSP_LIVECHAT_URL") && !configProperties.get("SSP_LIVECHAT_URL").contains("__")) {
                return "SSPLIVECHAT";
            } else if (configProperties.containsKey("ITSM_URL") && !configProperties.get("ITSM_URL").contains("__")) {
                return "ITSM";
            }
        }
        
        // Default to ITSM if we can't determine the product
        return "ITSM";
    }
    
    /**
     * Get current test method name for debugging
     */

    
    /**
     * Extract product from test package structure
     */
    private String extractProductFromTestPackage(String testClassName) {
        if (testClassName == null) return "unknown";
        
        String lowerClassName = testClassName.toLowerCase();
        if (lowerClassName.contains(".bwf.")) {
            return "BWF";
        } else if (lowerClassName.contains(".smartit.")) {
            return "SMARTIT";
        } else if (lowerClassName.contains(".dwp.")) {
            return "DWP";
        } else if (lowerClassName.contains(".cmdb.")) {
            return "CMDB";
        } else if (lowerClassName.contains(".itsm.")) {
            return "ITSM";
        } else if (lowerClassName.contains(".ssplivechat.")) {
            return "SSPLIVECHAT";
        }
        return "unknown";
    }
    
    /**
     * Extract user information from the test being executed
     * Priority: 1) Test parameters (data-driven CSV), 2) Test context attributes, 3) JSON files
     */
    private String extractUserFromTest(ITestResult result) {
        try {
            // PRIORITY 1: Extract from test parameters (data-driven tests with CSV)
            Object[] parameters = result.getParameters();
            if (parameters != null && parameters.length > 0) {
                log.debug("Test has " + parameters.length + " parameters, checking for username...");
                
                // Check all parameters for username-like values
                for (int i = 0; i < Math.min(parameters.length, 5); i++) {  // Check first 5 params
                    Object param = parameters[i];
                    
                    // Case 1: Parameter is an Object array (common CSV pattern)
                    if (param instanceof Object[]) {
                        Object[] paramArray = (Object[]) param;
                        if (paramArray.length > 0 && paramArray[0] instanceof String) {
                            String username = ((String) paramArray[0]).trim();
                            if (isValidUsername(username)) {
                                log.info("✅ Extracted username from test parameter array[" + i + "][0]: " + username);
                                return username;
                            }
                        }
                    }
                    // Case 2: Parameter is a direct String (could be username)
                    else if (param instanceof String) {
                        String username = ((String) param).trim();
                        if (isValidUsername(username)) {
                            log.info("✅ Extracted username from test parameter[" + i + "]: " + username);
                            return username;
                        }
                    }
                }
                
                log.debug("No valid username found in test parameters");
            }
            
            // PRIORITY 2: Try to get from test context attributes
            try {
                Object contextUser = result.getAttribute("username");
                if (contextUser instanceof String) {
                    String username = ((String) contextUser).trim();
                    if (isValidUsername(username)) {
                        log.info("✅ Extracted username from test context: " + username);
                        return username;
                    }
                }
            } catch (Exception e) {
                log.debug("No username in test context: " + e.getMessage());
            }
            
            // PRIORITY 3: Fall back to JSON file extraction
            String testClassName = result.getTestClass().getName();
            String appType = extractAppTypeFromTestClass(testClassName);
            if (appType != null) {
                log.debug("Falling back to JSON extraction for appType: " + appType);
                String[] commonUsers = getCommonUsersForTestApp(appType);
                if (commonUsers != null && commonUsers.length > 0) {
                    for (String userType : commonUsers) {
                        String user = readUserFromJsonFileForTest(appType, userType);
                        if (user != null && !user.trim().isEmpty()) {
                            log.debug("Extracted username from JSON (" + appType + "/" + userType + "): " + user);
                            return user.trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting user from test: " + e.getMessage(), e);
        }
        return "unknown";
    }
    
    /**
     * Check if a string is a valid username (not empty, not "unknown", looks like a username)
     */
    private boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = username.trim().toLowerCase();
        
        // Exclude common non-username values
        if (trimmed.equals("unknown") || 
            trimmed.equals("null") || 
            trimmed.equals("") ||
            trimmed.length() < 3 ||  // Username should be at least 3 chars
            trimmed.matches("^\\d+$") ||  // Not just numbers
            trimmed.matches("^[^a-z0-9]+$")) {  // Not just special chars
            return false;
        }
        
        // Valid username pattern (letters, numbers, underscore, hyphen, dot)
        return username.matches("^[a-zA-Z][a-zA-Z0-9._-]*$");
    }
    
    /**
     * Extract product information from the test being executed
     */
    private String extractProductFromTest(ITestResult result) {
        try {
            // First, try to get product from SolqaDetails annotation
            Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
            if (testMethod.isAnnotationPresent(SolqaDetails.class)) {
                SolqaDetails annotation = testMethod.getAnnotation(SolqaDetails.class);
                Product[] products = annotation.Products();
                if (products != null && products.length > 0) {
                    Product primaryProduct = products[0];
                    String productName = mapProductEnumToString(primaryProduct);
                    if (productName != null && !productName.equals("unknown")) {
                        return productName;
                    }
                }
            }
            
            // Fallback to package-based extraction
            String testClassName = result.getTestClass().getName();
            return extractProductFromTestPackage(testClassName);
            
        } catch (Exception e) {
            log.debug("Could not extract product from test: " + e.getMessage());
        }
        return "unknown";
    }
    
    /**
     * Helper methods for extraction
     */
    private String extractAppTypeFromTestClass(String testClassName) {
        if (testClassName == null) return null;
        String lowerClassName = testClassName.toLowerCase();
        if (lowerClassName.contains(".bwf.")) return "BWF";
        else if (lowerClassName.contains(".smartit.")) return "SMARTIT";
        else if (lowerClassName.contains(".dwp.")) return "DWP";
        else if (lowerClassName.contains(".cmdb.")) return "ITSM";
        else if (lowerClassName.contains(".itsm.")) return "ITSM";
        else if (lowerClassName.contains(".ssplivechat.")) return "SSPLIVECHAT";
        return null;
    }
    
    private String[] getCommonUsersForTestApp(String appType) {
        if (appType == null) return null;
        switch (appType.toUpperCase()) {
            case "BWF": return new String[]{"opsSupportAgent", "Elizabeth"};
            case "SMARTIT": return new String[]{"bmcopssupportagent", "bmcopsenduser"};
            case "DWP": return new String[]{"bmcopsenduser", "bmcopssupportagent"};
            case "ITSM": return new String[]{"bmcopssupportagent", "bmcOpsManagerUser"};
            case "SSPLIVECHAT": return new String[]{"bmcopsenduser"};
            default: return new String[]{"bmcopssupportagent"};
        }
    }
    
    /**
     * Get the correct JSON filename for the given app type
     */
    private String getJsonFileName(String appType) {
        switch (appType.toUpperCase()) {
            case "SMARTIT":
                return "SmartITUsers";
            case "INNOVATIONSTUDIO":
            case "IS":
                return "ISUsers";
            case "SSPLIVECHAT":
                return "SSPLiveChatUsers";
            case "BWF":
            case "DWP":
            case "ITSM":
            default:
                return appType + "Users";
        }
    }
    
    private String readUserFromJsonFileForTest(String appType, String userKey) {
        try {
            ReadJson readJson = new ReadJson();
            String jsonFileName = getJsonFileName(appType);
            Map<Object, String> userData = readJson.getInputValuesMapIncludingLabelField(appType, jsonFileName, userKey);
            if (userData != null && !userData.isEmpty()) {
                String userName = userData.get("UserName");
                if (userName != null && !userName.trim().isEmpty()) {
                    return userName.trim();
                }
            }
        } catch (Exception e) {
            log.debug("Could not read user " + userKey + " from " + appType + " JSON file: " + e.getMessage());
        }
        return null;
    }
    
    private String mapProductEnumToString(Product product) {
        if (product == null) return "unknown";
        switch (product) {
            case Helix_ITSM: return "ITSM";
            case Helix_BWF: return "BWF";
            case Helix_DWP: return "DWP";
            case SmartIT: return "SMARTIT";
            case Helix_SmartIT: return "SMARTIT";
            case CMDB: return "CMDB";
            case Helix_SSPLIVECHAT: return "SSPLIVECHAT";
            default: return product.toString();
        }
    }
    
    /**
     * Get the primary product for suite-level metrics based on executed tests
     */
    private String getSuitePrimaryProduct() {
        if (testProductMap.isEmpty()) {
            return currentProduct;
        }
        
        // Count occurrences of each product to find the most common one
        Map<String, Integer> productCounts = new HashMap<>();
        for (String product : testProductMap.values()) {
            productCounts.put(product, productCounts.getOrDefault(product, 0) + 1);
        }
        
        // Find the most common product
        String primaryProduct = currentProduct;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                primaryProduct = entry.getKey();
            }
        }
        
        log.info("Suite primary product determined as: " + primaryProduct + " based on " + testProductMap.size() + " tests");
        return primaryProduct;
    }
    
    /**
     * Get the primary user for suite-level metrics based on executed tests
     */
    private String getSuitePrimaryUser() {
        if (testUserMap.isEmpty()) {
            return getCurrentUser();
        }
        
        // Count occurrences of each user to find the most common one
        Map<String, Integer> userCounts = new HashMap<>();
        for (String user : testUserMap.values()) {
            userCounts.put(user, userCounts.getOrDefault(user, 0) + 1);
        }
        
        // Find the most common user
        String primaryUser = getCurrentUser();
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : userCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                primaryUser = entry.getKey();
            }
        }
        
        log.info("Suite primary user determined as: " + primaryUser + " based on " + testUserMap.size() + " tests");
        return primaryUser;
    }

    // ===========================
    // API AUTOMATION DETECTION AND EXTRACTION METHODS
    // ===========================

    /**
     * Detect if the suite is API automation based on suite name
     */
    private static boolean isApiAutomationSuite(String suiteName) {
        if (suiteName == null) return false;
        String lowerSuiteName = suiteName.toLowerCase();
        return lowerSuiteName.contains("api") || 
               lowerSuiteName.contains("zdt_api") || 
               lowerSuiteName.contains("rest") || 
               lowerSuiteName.contains("service");
    }

    /**
     * Detect if an individual test is an API test
     * STRICT detection - only mark as API if clearly API-related
     */
    private static boolean isApiTest(ITestResult result, String suiteName) {
        String testClassName = result.getTestClass().getName().toLowerCase();
        String testMethodName = result.getMethod().getMethodName().toLowerCase();
        
        // PRIORITY 1: Check test class package for EXPLICIT API indicators
        if (testClassName.contains("zdt_api.") ||  // ZDT_API package
            testClassName.contains(".api.") ||      // .api. package
            testClassName.contains(".rest.") ||     // .rest. package  
            testClassName.contains("apitest") ||    // APITest class name
            testClassName.contains("resttest")) {   // RESTTest class name
            log.debug("Test class " + testClassName + " identified as API test (package/name match)");
            return true;
        }
        
        // PRIORITY 2: Check if suite is explicitly API-based
        if (isApiAutomationSuite(suiteName)) {
            // Even if suite is API, exclude UI test classes
            if (testClassName.contains(".ui.") || 
                testClassName.contains(".selenium.") ||
                testClassName.contains(".dwp.") && !testClassName.contains("zdt_api") ||
                testClassName.contains(".smartit.") && !testClassName.contains("zdt_api") ||
                testClassName.contains(".bwf.") && !testClassName.contains("zdt_api") ||
                testClassName.contains(".itsm.") && !testClassName.contains("zdt_api") && !testClassName.contains(".api.")) {
                log.debug("Test class " + testClassName + " is UI despite API suite - marking as UI");
                return false;  // This is a UI test
            }
            
            log.debug("Test class " + testClassName + " identified as API test (API suite)");
            return true;  // Suite is API and class is not UI
        }
        
        // PRIORITY 3: Check method names for explicit API indicators
        if (testMethodName.startsWith("testapi") ||
            testMethodName.startsWith("testrest")) {
            log.debug("Test method " + testMethodName + " identified as API test (method name)");
            return true;
        }
        
        // Default to UI
        log.debug("Test class " + testClassName + " identified as UI test (default)");
        return false;
    }

    /**
     * Extract API endpoint from test result
     */
    private String extractApiEndpoint(ITestResult result) {
        try {
            // Try to get endpoint from exception messages or test data
            if (result.getThrowable() != null) {
                String message = result.getThrowable().getMessage();
                if (message != null) {
                    Matcher matcher = ENDPOINT_PATTERN.matcher(message);
                    if (matcher.find()) {
                        String endpoint = matcher.group();
                        log.debug("Extracted API endpoint from exception: " + endpoint);
                        return endpoint;
                    }
                }
            }
            
            // Try to extract from test class package or name
            String testClassName = result.getTestClass().getName();
            String testMethodName = result.getMethod().getMethodName();
            
            // Map common test patterns to API endpoints
            if (testClassName.toLowerCase().contains("incident")) {
                if (testClassName.toLowerCase().contains("smartit")) {
                    return "/smartit/rest/v3/incident";
                } else {
                    return "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/incident";
                }
            } else if (testClassName.toLowerCase().contains("change")) {
                if (testClassName.toLowerCase().contains("smartit")) {
                    return "/smartit/rest/v3/change";
                } else {
                    return "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/change";
                }
            } else if (testClassName.toLowerCase().contains("knowledge")) {
                return "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/knowledge";
            } else if (testClassName.toLowerCase().contains("workorder")) {
                return "/smartit/rest/v2/workorder";
            } else if (testMethodName.toLowerCase().contains("create")) {
                return "/api/rest/create";
            } else if (testMethodName.toLowerCase().contains("get")) {
                return "/api/rest/get";
            } else if (testMethodName.toLowerCase().contains("update")) {
                return "/api/rest/update";
            } else if (testMethodName.toLowerCase().contains("delete")) {
                return "/api/rest/delete";
            }
            
        } catch (Exception e) {
            log.debug("Could not extract API endpoint from test: " + e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extract HTTP method from test result
     */
    private String extractHttpMethod(ITestResult result) {
        try {
            String testMethodName = result.getMethod().getMethodName();
            
            // Map test patterns to HTTP methods
            if (testMethodName.toLowerCase().contains("create") || 
                testMethodName.toLowerCase().contains("post")) {
                return "POST";
            } else if (testMethodName.toLowerCase().contains("get") || 
                       testMethodName.toLowerCase().contains("read") ||
                       testMethodName.toLowerCase().contains("fetch")) {
                return "GET";
            } else if (testMethodName.toLowerCase().contains("update") || 
                       testMethodName.toLowerCase().contains("put") ||
                       testMethodName.toLowerCase().contains("modify")) {
                return "PUT";
            } else if (testMethodName.toLowerCase().contains("delete") || 
                       testMethodName.toLowerCase().contains("remove")) {
                return "DELETE";
            } else if (testMethodName.toLowerCase().contains("patch")) {
                return "PATCH";
            }
            
            // Try to extract from exception messages
            if (result.getThrowable() != null) {
                String message = result.getThrowable().getMessage();
                if (message != null) {
                    String upperMessage = message.toUpperCase();
                    if (upperMessage.contains("GET")) return "GET";
                    if (upperMessage.contains("POST")) return "POST";
                    if (upperMessage.contains("PUT")) return "PUT";
                    if (upperMessage.contains("DELETE")) return "DELETE";
                    if (upperMessage.contains("PATCH")) return "PATCH";
                }
            }
            
        } catch (Exception e) {
            log.debug("Could not extract HTTP method from test: " + e.getMessage());
        }
        return "GET"; // Default to GET for API calls
    }

    /**
     * Extract HTTP status code from test result
     */
    private String extractHttpStatusCode(ITestResult result) {
        try {
            // Try to extract from exception messages
            if (result.getThrowable() != null) {
                String message = result.getThrowable().getMessage();
                if (message != null) {
                    Matcher matcher = HTTP_STATUS_PATTERN.matcher(message);
                    if (matcher.find()) {
                        String statusCode = matcher.group();
                        log.debug("Extracted HTTP status code from exception: " + statusCode);
                        return statusCode;
                    }
                }
            }
            
            // Infer status code based on test result
            if ("PASS".equals(result.getStatus()) || result.getStatus() == ITestResult.SUCCESS) {
                // For successful tests, infer based on HTTP method
                String method = extractHttpMethod(result);
                if ("POST".equals(method)) {
                    return "201"; // Created
                } else {
                    return "200"; // OK
                }
            } else if ("FAIL".equals(result.getStatus()) || result.getStatus() == ITestResult.FAILURE) {
                // For failed tests, common error codes
                if (result.getThrowable() != null) {
                    String message = result.getThrowable().getMessage();
                    if (message != null) {
                        String lowerMessage = message.toLowerCase();
                        if (lowerMessage.contains("not found") || lowerMessage.contains("404")) {
                            return "404";
                        } else if (lowerMessage.contains("unauthorized") || lowerMessage.contains("401")) {
                            return "401";
                        } else if (lowerMessage.contains("forbidden") || lowerMessage.contains("403")) {
                            return "403";
                        } else if (lowerMessage.contains("bad request") || lowerMessage.contains("400")) {
                            return "400";
                        } else if (lowerMessage.contains("internal server error") || lowerMessage.contains("500")) {
                            return "500";
                        }
                    }
                }
                return "500"; // Default server error for failed tests
            }
            
        } catch (Exception e) {
            log.debug("Could not extract HTTP status code from test: " + e.getMessage());
        }
        return "200"; // Default success code
    }
}
