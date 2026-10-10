package util;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * *********************************************************************
 * Purpose: Prometheus metrics exporter for test automation results
 * Suite Name: Framework File
 * Module Name: Metrics
 * @author sbonta
 * @version 1.0
 * @since 2025-09-03
 * Comments: Exposes test metrics for Prometheus scraping
 * *********************************************************************
 */
public class PrometheusMetricsExporter {

    private static final Logger log = Logger.getLogger(PrometheusMetricsExporter.class.getName());
    private static HTTPServer server;
    private static HTTPServer apiServer; // API metrics server on port 8081
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicBoolean apiInitialized = new AtomicBoolean(false);
    
    // Product version and run counter from properties.config
    private static String productVersion = "unknown";
    private static String runCounter = "0";
    
    // Status management
    private static String currentStatus = "active";
    private static int scrapDelaySeconds = 300; // Default 5 minutes (300 seconds)
    private static ScheduledExecutorService statusScheduler;
    private static final AtomicBoolean statusTimerStarted = new AtomicBoolean(false);
    
    // Server shutdown management
    private static int serverShutdownDelaySeconds = 300; // Default 5 minutes (300 seconds)
    private static ScheduledExecutorService serverShutdownScheduler;
    private static final AtomicBoolean serverShutdownScheduled = new AtomicBoolean(false);
    private static ScheduledFuture<?> serverShutdownTask;
    
    // Suite execution delay management
    private static boolean suiteExecutionDelayEnabled = true; // Enable by default
    private static int suiteExecutionDelaySeconds = 300; // Default same as server shutdown delay
    
    // TTL-based metric removal management
    private static boolean metricTTLEnabled = false; // Default DISABLED for KPI persistence
    private static int metricTTLSeconds = 30; // Default 30 seconds (only used if enabled)
    private static final ScheduledExecutorService metricCleanupScheduler = 
        Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r, "MetricCleanup-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
    private static final Map<String, ScheduledFuture<?>> metricCleanupTasks = 
        new ConcurrentHashMap<>();
    
    // Metrics ports - can be configured via environment variables
    private static final int METRICS_PORT = Integer.parseInt(
        System.getenv().getOrDefault("METRICS_PORT", "8080")
    );
    private static final int API_METRICS_PORT = Integer.parseInt(
        System.getenv().getOrDefault("API_METRICS_PORT", "8081")
    );
    
    // ===========================
    // REGISTRIES FOR METRICS
    // ===========================
    // Separate registries for UI (port 8080) and API (port 8081) metrics
    // This prevents duplicate metrics - each port sees only its relevant metrics
    private static final CollectorRegistry uiRegistry = new CollectorRegistry();
    private static final CollectorRegistry apiRegistry = new CollectorRegistry();

    // ===========================
    // UNIFIED METRICS FOR BOTH UI AND API
    // ===========================
    // These metrics are registered on BOTH registries so they appear on both ports
    // Differentiated by test_type label (UI or API)
    // Status codes: 0=PASS, 2=SKIP, 99=FAIL
    
    // Gauge for test status (registered on BOTH registries)
    public static final Gauge UI_TESTCASE_EXECUTION_STATUS = Gauge.build()
            .name("uatron_testcase_execution_status_total")
            .help("Test case execution status for UI automation (0=passed, 2=skipped, 99=failed)")
            .labelNames("suite", "environment", "product", "test_type", "test_class", "test_name", "result", 
                       "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", 
                       "jira_test_case_id", "product_version", "run_counter")
            .register(uiRegistry);
    
    public static final Gauge API_TESTCASE_EXECUTION_STATUS_UNIFIED = Gauge.build()
            .name("uatron_testcase_execution_status_total")
            .help("Test case execution status for API automation (0=passed, 2=skipped, 99=failed)")
            .labelNames("suite", "environment", "product", "test_type", "test_class", "test_name", "result", 
                       "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", 
                       "jira_test_case_id", "product_version", "run_counter")
            .register(apiRegistry);
    
    // Counter for total count (registered on BOTH registries)
    public static final Counter UI_TESTCASE_EXECUTIONS_COUNTER = Counter.build()
            .name("uatron_testcase_executions_total")
            .help("Total number of test case executions for UI automation")
            .labelNames("suite", "environment", "product", "test_type", "test_class", "test_name", "result", 
                       "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", 
                       "jira_test_case_id", "product_version", "run_counter")
            .register(uiRegistry);
    
    public static final Counter API_TESTCASE_EXECUTIONS_COUNTER_UNIFIED = Counter.build()
            .name("uatron_testcase_executions_total")
            .help("Total number of test case executions for API automation")
            .labelNames("suite", "environment", "product", "test_type", "test_class", "test_name", "result", 
                       "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", 
                       "jira_test_case_id", "product_version", "run_counter")
            .register(apiRegistry);

    // ===========================
    // UNIFIED METRICS (UI - Port 8080)
    // ===========================
    // These metrics include test_type label to differentiate UI vs API tests
    
    public static final Counter TESTS_TOTAL = Counter.build()
            .name("uatron_tests_total")
            .help("Total number of tests executed (UI and API)")
            .labelNames("suite", "environment", "product", "test_type", "result", "deployment_job_id", "user", "product_version", "run_counter")
            .register(uiRegistry);

    public static final Gauge TESTS_RUNNING = Gauge.build()
            .name("uatron_tests_running")
            .help("Number of tests currently running")
            .labelNames("suite", "environment", "product", "test_type", "deployment_job_id", "user", "product_version", "run_counter")
            .register(uiRegistry);

    public static final Histogram TEST_DURATION = Histogram.build()
            .name("uatron_test_duration_seconds")
            .help("Test execution duration in seconds")
            .labelNames("suite", "environment", "product", "test_type", "test_class", "test_name", "deployment_job_id", "user", "jira_test_case_id", "product_version", "run_counter")
            .buckets(0.1, 0.5, 1, 5, 10, 30, 60, 120, 300, 600, 1200, 3600)
            .register(uiRegistry);

    public static final Summary TEST_EXECUTION_TIME = Summary.build()
            .name("uatron_test_execution_time_seconds")
            .help("Test execution time summary")
            .labelNames("suite", "environment", "product", "test_type", "result", "deployment_job_id", "user", "product_version", "run_counter")
            .quantile(0.5, 0.05)
            .quantile(0.9, 0.01)
            .quantile(0.99, 0.001)
            .register(uiRegistry);

    public static final Gauge TESTCASE_EXECUTION_STATUS = Gauge.build()
            .name("uatron_testcase_execution_status")
            .help("Individual test case execution status (0=passed, 2=skipped, 99=failed)")
            .labelNames("suite", "environment", "product", "test_type", "test_class", "test_name", "deployment_job_id", "user", "jira_test_case_id", "product_version", "run_counter")
            .register(uiRegistry);

    // Suite-level metrics
    public static final Gauge SUITE_STATUS = Gauge.build()
            .name("uatron_suite_status")
            .help("Current suite execution status (0=passed, 2=skipped, 99=failed)")
            .labelNames("suite", "environment", "product", "test_type", "deployment_job_id", "user", "product_version", "run_counter")
            .register(uiRegistry);

    public static final Counter SUITE_EXECUTIONS = Counter.build()
            .name("uatron_suite_executions_total")
            .help("Total number of suite executions")
            .labelNames("suite", "environment", "product", "test_type", "result", "deployment_job_id", "user", "product_version", "run_counter")
            .register(uiRegistry);

    // Error tracking metrics
    public static final Counter TEST_ERRORS = Counter.build()
            .name("uatron_test_errors_total")
            .help("Total number of test errors")
            .labelNames("suite", "environment", "product", "test_type", "error_type", "deployment_job_id", "user", "product_version", "run_counter")
            .register(uiRegistry);

    // Environment info metrics
    public static final Gauge BUILD_INFO = Gauge.build()
            .name("uatron_build_info")
            .help("Build information")
            .labelNames("version", "build_time", "git_commit", "environment", "test_type", "deployment_job_id", "user", "product_version", "run_counter")
            .register(uiRegistry);

    // ===========================
    // API AUTOMATION METRICS (Port 8081)
    // ===========================

    // API test execution metrics - CONSOLIDATED
    public static final Gauge API_TESTS_RUNNING = Gauge.build()
            .name("uatron_api_tests_running")
            .help("Number of API tests currently running")
            .labelNames("suite", "environment", "product", "deployment_job_id", "user", "product_version", "run_counter")
            .register(apiRegistry);

    public static final Histogram API_TEST_DURATION = Histogram.build()
            .name("uatron_api_test_duration_seconds")
            .help("API test execution duration in seconds")
            .labelNames("suite", "environment", "product", "test_class", "test_name", "api_endpoint", "http_method", "deployment_job_id", "user", "jira_test_case_id", "product_version", "run_counter")
            .buckets(0.1, 0.5, 1, 2, 5, 10, 30, 60, 120, 300)
            .register(apiRegistry);

    public static final Summary API_TEST_EXECUTION_TIME = Summary.build()
            .name("uatron_api_test_execution_time_seconds")
            .help("API test execution time summary")
            .labelNames("suite", "environment", "product", "test_class", "test_name", "result", "api_endpoint", "deployment_job_id", "user", "jira_test_case_id", "product_version", "run_counter")
            .quantile(0.5, 0.05)
            .quantile(0.9, 0.01)
            .quantile(0.99, 0.001)
            .register(apiRegistry);

    // PRIMARY METRIC - All API test executions with full details and status codes
    public static final Gauge API_TESTCASE_EXECUTION_STATUS = Gauge.build()
            .name("uatron_api_testcase_execution_status_total")
            .help("API test case execution status by endpoint, method, and status code (0=passed, 2=skipped, 99=failed)")
            .labelNames("suite", "environment", "product", "test_class", "test_name", "result", "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", "jira_test_case_id", "product_version", "run_counter")
            .register(apiRegistry);

    // API response time metrics
    public static final Histogram API_RESPONSE_TIME = Histogram.build()
            .name("uatron_api_response_time_seconds")
            .help("API response time in seconds")
            .labelNames("suite", "environment", "product", "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", "product_version", "run_counter")
            .buckets(0.01, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10, 30)
            .register(apiRegistry);

    // API Suite-level metrics
    public static final Gauge API_SUITE_STATUS = Gauge.build()
            .name("uatron_api_suite_status")
            .help("Current API suite execution status (0=passed, 2=skipped, 99=failed)")
            .labelNames("suite", "environment", "product", "deployment_job_id", "user", "product_version", "run_counter")
            .register(apiRegistry);

    public static final Counter API_SUITE_EXECUTIONS = Counter.build()
            .name("uatron_api_suite_executions_total")
            .help("Total number of API suite executions")
            .labelNames("suite", "environment", "product", "result", "deployment_job_id", "user", "product_version", "run_counter")
            .register(apiRegistry);

    // API error tracking metrics
    public static final Counter API_TEST_ERRORS = Counter.build()
            .name("uatron_api_test_errors_total")
            .help("Total number of API test errors")
            .labelNames("suite", "environment", "product", "error_type", "deployment_job_id", "user", "product_version", "run_counter")
            .register(apiRegistry);

    // HTTP status code metrics
    public static final Counter API_HTTP_STATUS_CODES = Counter.build()
            .name("uatron_api_http_status_codes_total")
            .help("Total count of HTTP status codes returned by API")
            .labelNames("suite", "environment", "product", "api_endpoint", "http_method", "http_status_code", "deployment_job_id", "user", "product_version", "run_counter")
            .register(apiRegistry);

    // API Environment info metrics
    public static final Gauge API_BUILD_INFO = Gauge.build()
            .name("uatron_api_build_info")
            .help("API build information")
            .labelNames("version", "build_time", "git_commit", "environment", "deployment_job_id", "user", "product_version", "run_counter")
            .register(apiRegistry);

    /**
     * Initialize Prometheus metrics server
     */
    public static void initialize() {
        System.out.println("PrometheusMetricsExporter.initialize() called");
        log.info("PrometheusMetricsExporter.initialize() called");
        
        if (initialized.compareAndSet(false, true)) {
            try {
                // Load product version and run counter from properties.config
                loadProductVersionAndRunCounter();
                
                // Load scrap delay, TTL, server shutdown, and suite execution delay configuration
                loadScrapDelayConfiguration();
                loadMetricTTLConfiguration();
                loadServerShutdownDelayConfiguration();
                loadSuiteExecutionDelayConfiguration();
                
                System.out.println("Starting Prometheus HTTP servers...");
                log.info("Starting Prometheus HTTP servers...");
                
                // Start UI metrics HTTP server on port 8080 with UI registry ONLY
                System.out.println("Starting UI metrics server on port " + METRICS_PORT + " (UI Registry ONLY)");
                server = new HTTPServer(new InetSocketAddress(METRICS_PORT), uiRegistry, true);
                
                // Set build info for UI metrics
                setBuildInfo();
                
                System.out.println("UI metrics server started successfully on port " + METRICS_PORT);
                System.out.println("UI Metrics endpoint: http://localhost:" + METRICS_PORT + "/metrics");
                log.info("UI metrics server started on port " + METRICS_PORT);
                
            } catch (IOException e) {
                System.err.println("Failed to start Prometheus metrics server: " + e.getMessage());
                e.printStackTrace();
                log.error("Failed to start Prometheus metrics server", e);
                initialized.set(false);
            }
        } else {
            System.out.println("Prometheus metrics server already initialized");
            log.info("Prometheus metrics server already initialized");
        }
    }

    /**
     * Initialize API metrics server on port 8081
     */
    public static void initializeApiServer() {
        System.out.println("PrometheusMetricsExporter.initializeApiServer() called");
        log.info("PrometheusMetricsExporter.initializeApiServer() called");
        
        if (apiInitialized.compareAndSet(false, true)) {
            try {
                // Load product version and run counter if not already loaded
                if (productVersion.equals("unknown")) {
                    loadProductVersionAndRunCounter();
                }
                
                // Start API metrics HTTP server on port 8081 with API registry ONLY
                System.out.println("Starting API metrics server on port " + API_METRICS_PORT + " (API Registry ONLY)");
                log.info("Starting API metrics server on port " + API_METRICS_PORT + " (API Registry ONLY)");
                
                apiServer = new HTTPServer(new InetSocketAddress(API_METRICS_PORT), apiRegistry, true);
                
                // Set API build info
                setApiBuildInfo();
                
                System.out.println("API metrics server started successfully on port " + API_METRICS_PORT);
                System.out.println("API Metrics endpoint: http://localhost:" + API_METRICS_PORT + "/metrics");
                log.info("API metrics server started on port " + API_METRICS_PORT);
                log.info("API Metrics endpoint: http://localhost:" + API_METRICS_PORT + "/metrics");
                
            } catch (IOException e) {
                System.err.println("Failed to start API metrics server: " + e.getMessage());
                e.printStackTrace();
                log.error("Failed to start API metrics server", e);
                apiInitialized.set(false);
            }
        } else {
            System.out.println("API metrics server already initialized");
            log.info("API metrics server already initialized");
        }
    }

    /**
     * Record test execution metrics
     */
    public static void recordTestExecution(String suite, String environment, String product, 
                                         String testClass, String testName, String result, 
                                         double durationSeconds, String errorType, String deploymentJobId, String user, String jiraTestCaseId) {
        
        String testType = "UI"; // Test type for UI automation
        
        // Calculate status code (0=PASS, 2=SKIP, 99=FAIL)
        int testStatus = getStatusCode(result);
        
        // Record to UI Gauge metric with test_type=UI and empty API fields
        UI_TESTCASE_EXECUTION_STATUS.labels(
            suite, environment, product, testType, testClass, testName, result,
            "", "", "",  // Empty api_endpoint, http_method, http_status_code for UI tests
            deploymentJobId, user, jiraTestCaseId, productVersion, runCounter
        ).set(testStatus);
        
        // Increment UI Counter metric for total count
        UI_TESTCASE_EXECUTIONS_COUNTER.labels(
            suite, environment, product, testType, testClass, testName, result,
            "", "", "",  // Empty api_endpoint, http_method, http_status_code for UI tests
            deploymentJobId, user, jiraTestCaseId, productVersion, runCounter
        ).inc();
        
        // Increment total tests counter
        TESTS_TOTAL.labels(suite, environment, product, testType, result, deploymentJobId, user, productVersion, runCounter).inc();
        
        // Record test duration
        TEST_DURATION.labels(suite, environment, product, testType, testClass, testName, deploymentJobId, user, jiraTestCaseId, productVersion, runCounter)
                     .observe(durationSeconds);
        
        // Record execution time summary
        TEST_EXECUTION_TIME.labels(suite, environment, product, testType, result, deploymentJobId, user, productVersion, runCounter)
                           .observe(durationSeconds);
        
        // Record individual test case execution status to old UI-specific metric (for backward compatibility)
        TESTCASE_EXECUTION_STATUS.labels(suite, environment, product, testType, testClass, testName, deploymentJobId, user, jiraTestCaseId, productVersion, runCounter)
                                 .set(testStatus);
        
        // Schedule TTL-based metric removal (only if enabled)
        if (metricTTLEnabled) {
            scheduleMetricRemoval(suite, environment, product, testType, testClass, testName, deploymentJobId, user, jiraTestCaseId);
        }
        
        // Record errors if any (only for failed tests)
        if (errorType != null && !errorType.isEmpty() && 
            ("FAIL".equalsIgnoreCase(result) || "FAILED".equalsIgnoreCase(result))) {
            TEST_ERRORS.labels(suite, environment, product, testType, errorType, deploymentJobId, user, productVersion, runCounter).inc();
        }
        
        log.info("Recorded UI test metrics - Suite: " + suite + ", Test: " + testName + 
                ", Result: " + result + ", Status: " + testStatus + ", Duration: " + durationSeconds + "s, User: " + user + ", JiraID: " + jiraTestCaseId);
    }

    /**
     * Extract Jira test case ID from various sources
     */
    public static String extractJiraTestCaseId(String testMethodName, String testClassName) {
        // Try to extract from test method name if it follows naming convention (e.g., DRSQA_12345_TestName)
        if (testMethodName != null && testMethodName.matches(".*[A-Z]+[_-]\\d+.*")) {
            String[] parts = testMethodName.split("[_-]");
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].matches("[A-Z]+") && parts[i + 1].matches("\\d+")) {
                    String jiraId = parts[i] + "-" + parts[i + 1];
                    log.debug("Extracted Jira ID from method name: " + jiraId);
                    return jiraId;
                }
            }
        }
        
        // Try to extract from test class name if it follows naming convention
        if (testClassName != null && testClassName.matches(".*[A-Z]+[_-]\\d+.*")) {
            String[] parts = testClassName.split("[_.-]");
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].matches("[A-Z]+") && parts[i + 1].matches("\\d+")) {
                    String jiraId = parts[i] + "-" + parts[i + 1];
                    log.debug("Extracted Jira ID from class name: " + jiraId);
                    return jiraId;
                }
            }
        }
        
        log.debug("No Jira test case ID found for test: " + testMethodName);
        return "unknown";
    }

    /**
     * Record test execution metrics (backward compatibility - auto-detects user, deployment job id, and attempts to extract Jira ID)
     */
    public static void recordTestExecution(String suite, String environment, String product, 
                                         String testClass, String testName, String result, 
                                         double durationSeconds, String errorType) {
        // Try system property first (from Maven -D parameter), then environment variable (from container)
        String deploymentJobId = System.getProperty("deployment_job_id");
        if (deploymentJobId == null || deploymentJobId.trim().isEmpty() || "unknown".equals(deploymentJobId)) {
            deploymentJobId = System.getenv().getOrDefault("DEPLOYMENT_JOB_ID", "unknown");
        }
        String user = getApplicationUser();
        String jiraTestCaseId = extractJiraTestCaseId(testName, testClass);
        recordTestExecution(suite, environment, product, testClass, testName, result, durationSeconds, errorType, deploymentJobId, user, jiraTestCaseId);
    }

    /**
     * Helper method to get numeric status code for test results
     */
    public static int getStatusCode(String result) {
        if ("PASS".equalsIgnoreCase(result) || "PASSED".equalsIgnoreCase(result)) {
            return 0; // PASS = 0
        } else if ("SKIP".equalsIgnoreCase(result) || "SKIPPED".equalsIgnoreCase(result)) {
            return 2; // SKIP = 2
        } else {
            return 99; // FAIL, FAILED, or any other result = 99
        }
    }

    // ===========================
    // API AUTOMATION METHODS
    // ===========================

    /**
     * Record API test execution metrics with endpoint and HTTP method information
     */
    public static void recordApiTestExecution(String suite, String environment, String product, 
                                            String testClass, String testName, String result, 
                                            double durationSeconds, String errorType, String deploymentJobId, 
                                            String user, String jiraTestCaseId, String apiEndpoint, 
                                            String httpMethod, String httpStatusCode) {
        
        String testType = "API"; // Test type for API automation
        
        // Get application user if not provided or is system user
        if (user == null || user.trim().isEmpty() || user.equals("unknown") || 
            user.equals(System.getProperty("user.name", "unknown"))) {
            user = getApplicationUser();
            log.debug("Resolved API test user to: " + user);
        }
        
        // Calculate status code (0=PASS, 2=SKIP, 99=FAIL)
        int testStatus = getStatusCode(result);
        
        // Normalize empty/unknown API fields
        if (apiEndpoint == null || apiEndpoint.trim().isEmpty() || apiEndpoint.equals("unknown")) {
            apiEndpoint = "";
        }
        if (httpMethod == null || httpMethod.trim().isEmpty() || httpMethod.equals("unknown")) {
            httpMethod = "";
        }
        if (httpStatusCode == null || httpStatusCode.trim().isEmpty() || httpStatusCode.equals("unknown")) {
            httpStatusCode = "";
        }
        
        // Record to API Gauge metric with test_type=API and full API details
        API_TESTCASE_EXECUTION_STATUS_UNIFIED.labels(
            suite, environment, product, testType, testClass, testName, result,
            apiEndpoint, httpMethod, httpStatusCode,
            deploymentJobId, user, jiraTestCaseId, productVersion, runCounter
        ).set(testStatus);
        
        // Increment API Counter metric for total count
        API_TESTCASE_EXECUTIONS_COUNTER_UNIFIED.labels(
            suite, environment, product, testType, testClass, testName, result,
            apiEndpoint, httpMethod, httpStatusCode,
            deploymentJobId, user, jiraTestCaseId, productVersion, runCounter
        ).inc();
        
        // Record to old API-specific metric (for backward compatibility)
        API_TESTCASE_EXECUTION_STATUS.labels(suite, environment, product, testClass, testName, result, apiEndpoint, 
                                            httpMethod, httpStatusCode, deploymentJobId, user, jiraTestCaseId, 
                                            productVersion, runCounter).set(testStatus);
        
        // Record API test duration histogram
        API_TEST_DURATION.labels(suite, environment, product, testClass, testName, apiEndpoint, httpMethod,
                                deploymentJobId, user, jiraTestCaseId, productVersion, runCounter)
                         .observe(durationSeconds);
        
        // Record execution time summary with enhanced labels
        API_TEST_EXECUTION_TIME.labels(suite, environment, product, testClass, testName, result, apiEndpoint,
                                      deploymentJobId, user, jiraTestCaseId, productVersion, runCounter)
                               .observe(durationSeconds);
        
        // Record HTTP status code counter
        API_HTTP_STATUS_CODES.labels(suite, environment, product, apiEndpoint, httpMethod, httpStatusCode,
                                    deploymentJobId, user, productVersion, runCounter).inc();
        
        // Record response time histogram
        API_RESPONSE_TIME.labels(suite, environment, product, apiEndpoint, httpMethod, httpStatusCode,
                                deploymentJobId, user, productVersion, runCounter).observe(durationSeconds);
        
        // Record errors if any (only for failed tests)
        if (errorType != null && !errorType.isEmpty() && 
            ("FAIL".equalsIgnoreCase(result) || "FAILED".equalsIgnoreCase(result))) {
            API_TEST_ERRORS.labels(suite, environment, product, errorType, deploymentJobId, user, productVersion, runCounter).inc();
        }
        
        log.info("Recorded API test metrics - Suite: " + suite + ", Test: " + testName + 
                ", Result: " + result + ", Status: " + testStatus + ", Duration: " + durationSeconds + "s" + 
                ", Endpoint: " + apiEndpoint + ", Method: " + httpMethod + ", StatusCode: " + httpStatusCode + 
                ", User: " + user + ", JiraID: " + jiraTestCaseId);
    }

    /**
     * Record API suite execution metrics
     */
    public static void recordApiSuiteExecution(String suite, String environment, String product, 
                                             String deploymentJobId, String result, String user) {
        
        API_SUITE_EXECUTIONS.labels(suite, environment, product, result, deploymentJobId, user, 
                                   productVersion, runCounter).inc();
        
        // Set suite status using the helper method
        int status = getStatusCode(result);
        API_SUITE_STATUS.labels(suite, environment, product, deploymentJobId, user, 
                              productVersion, runCounter).set(status);
        
        log.info("Recorded API suite metrics - Suite: " + suite + ", Result: " + result + ", Status: " + status + 
                ", User: " + user);
    }

    /**
     * Update API tests running gauge
     */
    public static void updateApiTestsRunning(String suite, String environment, String product, 
                                           String deploymentJobId, String user, double count) {
        // Always allow updating the running tests gauge for real-time monitoring
        API_TESTS_RUNNING.labels(suite, environment, product, deploymentJobId, user, 
                                productVersion, runCounter).set(count);
        
        log.debug("Updated API tests running count: " + count + " for suite: " + suite);
    }

    /**
     * Record suite execution metrics
     */
    public static void recordSuiteExecution(String suite, String environment, String product, 
                                           String deploymentJobId, String result, String user) {
        
        String testType = "UI"; // Default test type
        
        SUITE_EXECUTIONS.labels(suite, environment, product, testType, result, deploymentJobId, user, productVersion, runCounter).inc();
        
        // Set suite status using the helper method
        int status = getStatusCode(result);
        SUITE_STATUS.labels(suite, environment, product, testType, deploymentJobId, user, productVersion, runCounter).set(status);
        
        // Schedule TTL-based metric removal for suite status (only if enabled)
        if (metricTTLEnabled) {
            scheduleSuiteStatusRemoval(suite, environment, product, testType, deploymentJobId, user);
        }
        
        log.info("Recorded suite metrics - Suite: " + suite + ", Result: " + result + ", Status: " + status + 
                ", User: " + user);
    }

    /**
     * Record suite execution metrics (backward compatibility - auto-detects user)
     */
    public static void recordSuiteExecution(String suite, String environment, String product, 
                                           String deploymentJobId, String result) {
        String user = getApplicationUser();
        recordSuiteExecution(suite, environment, product, deploymentJobId, result, user);
    }

    /**
     * Update tests running gauge - only during active batch
     */
    public static void updateTestsRunning(String suite, String environment, String product, 
                                        String deploymentJobId, String user, double count) {
        String testType = "UI"; // Default test type
        
        // Always allow updating the running tests gauge for real-time monitoring
        TESTS_RUNNING.labels(suite, environment, product, testType, deploymentJobId, user, productVersion, runCounter).set(count);
        
        // Schedule TTL-based metric removal for running tests gauge (only if count > 0 and TTL enabled)
        if (count > 0 && metricTTLEnabled) {
            scheduleTestsRunningRemoval(suite, environment, product, testType, deploymentJobId, user);
        }
    }

    /**
     * Set build information
     */
    private static void setBuildInfo() {
        String version = System.getenv().getOrDefault("BUILD_VERSION", "unknown");
        String buildTime = System.getenv().getOrDefault("BUILD_TIME", "unknown");
        String gitCommit = System.getenv().getOrDefault("GIT_COMMIT", "unknown");
        String environment = getEnvironmentInfo();
        String testType = "UI"; // Default test type
        String deploymentJobId = System.getProperty("deployment_job_id");
        if (deploymentJobId == null || deploymentJobId.trim().isEmpty()) {
            deploymentJobId = System.getenv().getOrDefault("DEPLOYMENT_JOB_ID", "unknown");
        }
        String user = getApplicationUser();
        
        BUILD_INFO.labels(version, buildTime, gitCommit, environment, testType, deploymentJobId, user, productVersion, runCounter).set(1);
        
        // Schedule TTL-based metric removal for build info (only if enabled)
        if (metricTTLEnabled) {
            scheduleBuildInfoRemoval(version, buildTime, gitCommit, environment, testType, deploymentJobId, user);
        }
    }

    /**
     * Set API build information
     */
    private static void setApiBuildInfo() {
        String version = System.getenv().getOrDefault("BUILD_VERSION", "unknown");
        String buildTime = System.getenv().getOrDefault("BUILD_TIME", "unknown");
        String gitCommit = System.getenv().getOrDefault("GIT_COMMIT", "unknown");
        String environment = getEnvironmentInfo();
        String deploymentJobId = System.getProperty("deployment_job_id");
        if (deploymentJobId == null || deploymentJobId.trim().isEmpty()) {
            deploymentJobId = System.getenv().getOrDefault("DEPLOYMENT_JOB_ID", "unknown");
        }
        String user = getApplicationUser();
        
        API_BUILD_INFO.labels(version, buildTime, gitCommit, environment, deploymentJobId, user, productVersion, runCounter).set(1);
        
        log.info("Set API build info - Version: " + version + ", Environment: " + environment + 
                ", DeploymentJobId: " + deploymentJobId + ", User: " + user);
    }

    /**
     * Get environment information from multiple sources with COMPREHENSIVE fallbacks
     */
    private static String getEnvironmentInfo() {
        String environment = null;
        
        // Try system property first
        environment = System.getProperty("setupName");
        log.debug("Environment detection - System.getProperty('setupName'): " + environment);
        if (environment != null && !environment.trim().isEmpty() && !environment.contains("__")) {
            log.info("✓ Environment detected from System.getProperty('setupName'): " + environment);
            return environment;
        }
        
        // Try SETUP_NAME environment variable
        environment = System.getenv("SETUP_NAME");
        log.debug("Environment detection - System.getenv('SETUP_NAME'): " + environment);
        if (environment != null && !environment.trim().isEmpty() && !environment.contains("__")) {
            log.info("✓ Environment detected from System.getenv('SETUP_NAME'): " + environment);
            return environment;
        }
        
        // Try ENVIRONMENT environment variable
        environment = System.getenv("ENVIRONMENT");
        log.debug("Environment detection - System.getenv('ENVIRONMENT'): " + environment);
        if (environment != null && !environment.trim().isEmpty() && !environment.contains("__")) {
            log.info("✓ Environment detected from System.getenv('ENVIRONMENT'): " + environment);
            return environment;
        }
        
        // Try ENV environment variable
        environment = System.getenv("ENV");
        log.debug("Environment detection - System.getenv('ENV'): " + environment);
        if (environment != null && !environment.trim().isEmpty() && !environment.contains("__")) {
            log.info("✓ Environment detected from System.getenv('ENV'): " + environment);
            return environment;
        }
        
        // Try to read from properties.config
        try {
            String userDir = System.getProperty("user.dir");
            String configFile = userDir + File.separator + "properties.config";
            Map<String, String> configProperties = ReadPropertiesFile.readProperties(configFile);
            
            if (configProperties != null) {
                environment = configProperties.get("setupName");
                log.debug("Environment detection - config setupName: " + environment);
                
                // Check if it's a placeholder
                if (environment != null && environment.contains("__")) {
                    log.warn("setupName in config contains placeholder: " + environment);
                    environment = null;
                }
                
                if (environment != null && !environment.trim().isEmpty()) {
                    log.info("✓ Environment detected from properties.config: " + environment);
                    return environment;
                }
                
                // Try to get from stackName
                String stackName = configProperties.get("stackName");
                log.debug("Environment detection - config stackName: " + stackName);
                if (stackName != null && !stackName.trim().isEmpty() && !stackName.contains("__")) {
                    log.info("✓ Environment detected from stackName in properties.config: " + stackName);
                    return stackName;
                }
                
                // Try to get from STACK_NAME env var
                stackName = System.getenv("STACK_NAME");
                log.debug("Environment detection - System.getenv('STACK_NAME'): " + stackName);
                if (stackName != null && !stackName.trim().isEmpty() && !stackName.contains("__")) {
                    log.info("✓ Environment detected from STACK_NAME environment variable: " + stackName);
                    return stackName;
                }
                
                // Try to extract from adminUserName or other config fields
                String adminUser = configProperties.get("adminUserName");
                if (adminUser != null && !adminUser.contains("__") && !adminUser.trim().isEmpty()) {
                    // If we have valid config, at least return something meaningful
                    environment = "configured";
                    log.info("✓ Environment auto-detected (valid config present): " + environment);
                    return environment;
                }
            }
        } catch (Exception e) {
            log.debug("Could not read environment from properties.config: " + e.getMessage());
        }
        
        // Last resort - check if running in Kubernetes
        String kubernetesNamespace = System.getenv("KUBERNETES_NAMESPACE");
        if (kubernetesNamespace != null && !kubernetesNamespace.trim().isEmpty()) {
            log.info("✓ Environment detected from Kubernetes namespace: " + kubernetesNamespace);
            return kubernetesNamespace;
        }
        
        String podName = System.getenv("HOSTNAME");
        if (podName != null && podName.contains("-")) {
            // Extract environment hint from pod name pattern (e.g., "zdt-ui-xxx")
            log.info("✓ Environment detected from pod hostname pattern: k8s-pod");
            return "k8s-pod";
        }
        
        log.warn("✗ All environment detection methods failed - returning 'unknown'");
        log.warn("To fix: Set SETUP_NAME or ENVIRONMENT variable, or configure setupName in properties.config");
        return "unknown";
    }

    /**
     * Get the application user based on the current test context
     */
    private static String getApplicationUser() {
        try {
            String userDir = System.getProperty("user.dir");
            
            // First, determine the current product from environment or config
            String currentProduct = getCurrentProduct();
            
            // Load config properties
            String configFile = userDir + File.separator + "properties.config";
            Map<String, String> configProperties = ReadPropertiesFile.readProperties(configFile);
            
            // Try to get user from config properties based on the current product
            if (configProperties != null) {
                String user = getUserFromProductConfig(currentProduct, configProperties);
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
            log.warn("Could not load application user from configuration: " + e.getMessage());
        }
        
        // Final fallback to system user if no application user found
        String systemUser = System.getenv().getOrDefault("USER", System.getProperty("user.name", "unknown"));
        log.debug("Using system user as final fallback: " + systemUser);
        return systemUser;
    }

    /**
     * Get current product from environment or config
     */
    private static String getCurrentProduct() {
        // Try system property first
        String product = System.getProperty("productName");
        if (product != null && !product.trim().isEmpty()) {
            return product;
        }
        
        // Try environment variable
        product = System.getenv("PRODUCT_NAME");
        if (product != null && !product.trim().isEmpty()) {
            return product;
        }
        
        // Try to read from properties.config
        try {
            String userDir = System.getProperty("user.dir");
            String configFile = userDir + File.separator + "properties.config";
            Map<String, String> configProperties = ReadPropertiesFile.readProperties(configFile);
            
            if (configProperties != null) {
                // Try to determine from URLs in config
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
        } catch (Exception e) {
            log.debug("Could not determine product from properties.config: " + e.getMessage());
        }
        
        // Default to ITSM
        return "ITSM";
    }
    
    /**
     * Get user from config properties based on the current product
     */
    private static String getUserFromProductConfig(String product, Map<String, String> configProperties) {
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
            
            // Check if runCounter > 0, which means JSON files have been dynamically updated
            // In this case, we should prioritize reading from the updated JSON files
            boolean useRunCounterLogic = false;
            try {
                String runCounterStr = getRunCounter();
                if (runCounterStr != null && !runCounterStr.trim().isEmpty()) {
                    int runCounterValue = Integer.parseInt(runCounterStr.trim());
                    if (runCounterValue > 0) {
                        useRunCounterLogic = true;
                        log.debug("runCounter = " + runCounterValue + " detected for " + product + ", prioritizing JSON files");
                    }
                }
            } catch (NumberFormatException e) {
                log.debug("Could not parse runCounter value for " + product);
            }
            
            if (useRunCounterLogic) {
                // When runCounter > 0, try common users first as JSON files have been updated
                String[] commonUsers = getCommonUsersForProduct(appType);
                for (String userType : commonUsers) {
                    String user = readUserFromJsonFile(appType, userType);
                    if (user != null && !user.trim().isEmpty()) {
                        log.debug("Using runCounter-updated user " + userType + " for product " + product);
                        return user;
                    }
                }
            } else {
                // Original logic when runCounter = 0 or not set
                // Get the configured user for this product from JSON
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
                return "IS";
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
        // 4. runCounter-based dynamic user selection
        
        String userType = System.getProperty("userType");
        if (userType != null && !userType.trim().isEmpty()) {
            log.debug("Using userType from system property: " + userType);
            return userType;
        }
        
        // Try to get from config properties
        try {
            String userDir = System.getProperty("user.dir");
            String configFile = userDir + File.separator + "properties.config";
            Map<String, String> configProperties = ReadPropertiesFile.readProperties(configFile);
            
            if (configProperties != null) {
                String configUser = configProperties.get("testUserType");
                if (configUser != null && !configUser.trim().isEmpty() && !configUser.contains("__")) {
                    log.debug("Using testUserType from config: " + configUser);
                    return configUser;
                }
                
                // Check if runCounter logic applies for dynamic user selection
                // If runCounter > 0, the JSON files have been dynamically updated
                // and we should use the common user keys as they now contain the selected users
                String runCounterStr = configProperties.get("runCounter");
                if (runCounterStr != null && !runCounterStr.trim().isEmpty()) {
                    try {
                        int runCounterValue = Integer.parseInt(runCounterStr.trim());
                        if (runCounterValue > 0) {
                            log.debug("runCounter = " + runCounterValue + " detected, using common user keys for " + appType);
                            // Return null so the caller will fall back to common user keys
                            // which will read from the dynamically updated JSON files
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        log.debug("Could not parse runCounter value: " + runCounterStr);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not read testUserType from config: " + e.getMessage());
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
     * Get the correct JSON filename for the given app type
     */
    private static String getJsonFileName(String appType) {
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

    /**
     * Read user from JSON file in resources folder
     */
    private static String readUserFromJsonFile(String appType, String userKey) {
        try {
            String userDir = System.getProperty("user.dir");
            String jsonFileName = getJsonFileName(appType);
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
     * Load product version and run counter from properties.config file
     */
    private static void loadProductVersionAndRunCounter() {
        try {
            String userDir = System.getProperty("user.dir");
            File propertiesFile = new File(userDir, "properties.config");
            
            if (propertiesFile.exists()) {
                java.util.Properties props = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(propertiesFile)) {
                    props.load(fis);
                    
                    productVersion = props.getProperty("productVersion", "unknown");
                    runCounter = props.getProperty("runCounter", "0");
                    
                    log.info("Loaded from properties.config - productVersion: " + productVersion + ", runCounter: " + runCounter);
                    System.out.println("Loaded from properties.config - productVersion: " + productVersion + ", runCounter: " + runCounter);
                }
            } else {
                log.warn("properties.config file not found at: " + propertiesFile.getAbsolutePath());
                System.out.println("WARNING: properties.config file not found at: " + propertiesFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error loading productVersion and runCounter from properties.config: " + e.getMessage(), e);
            System.err.println("Error loading productVersion and runCounter: " + e.getMessage());
            // Keep default values
        }
    }

    /**
     * Get the current product version
     */
    public static String getProductVersion() {
        return productVersion;
    }

    /**
     * Get the current run counter
     */
    public static String getRunCounter() {
        return runCounter;
    }

    /**
     * Shutdown metrics server
     */
    public static void shutdown() {
        if (server != null) {
            server.stop();
            log.info("Prometheus UI metrics server stopped");
        }
        if (apiServer != null) {
            apiServer.stop();
            log.info("Prometheus API metrics server stopped");
        }
        shutdownStatusScheduler();
        shutdownMetricCleanupScheduler();
        cancelScheduledServerShutdown();
        initialized.set(false);
        apiInitialized.set(false);
    }

    /**
     * Get metrics server port
     */
    public static int getMetricsPort() {
        return METRICS_PORT;
    }

    /**
     * Check if metrics server is running
     */
    public static boolean isRunning() {
        return initialized.get() && server != null;
    }

    /**
     * Check if API metrics server is running
     */
    public static boolean isApiRunning() {
        return apiInitialized.get() && apiServer != null;
    }

    /**
     * Get API metrics server port
     */
    public static int getApiMetricsPort() {
        return API_METRICS_PORT;
    }

    /**
     * Load scrap delay configuration from properties.config
     */
    private static void loadScrapDelayConfiguration() {
        try {
            String userDir = System.getProperty("user.dir");
            File propertiesFile = new File(userDir, "properties.config");
            
            if (propertiesFile.exists()) {
                java.util.Properties props = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(propertiesFile)) {
                    props.load(fis);
                    
                    String scrapDelayStr = props.getProperty("scrapDelaySeconds", "300");
                    scrapDelaySeconds = Integer.parseInt(scrapDelayStr);
                    
                    log.info("Loaded scrap delay configuration: " + scrapDelaySeconds + " seconds");
                    System.out.println("Loaded scrap delay configuration: " + scrapDelaySeconds + " seconds");
                }
            } else {
                log.warn("properties.config file not found, using default scrap delay: 300 seconds (5 minutes)");
                System.out.println("WARNING: properties.config file not found, using default scrap delay: 300 seconds (5 minutes)");
            }
        } catch (Exception e) {
            log.error("Error loading scrap delay configuration: " + e.getMessage(), e);
            System.err.println("Error loading scrap delay configuration: " + e.getMessage());
            // Keep default value of 300 seconds (5 minutes)
        }
    }

    /**
     * Load metric TTL configuration from properties.config
     */
    private static void loadMetricTTLConfiguration() {
        try {
            String userDir = System.getProperty("user.dir");
            File propertiesFile = new File(userDir, "properties.config");
            
            if (propertiesFile.exists()) {
                java.util.Properties props = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(propertiesFile)) {
                    props.load(fis);
                    
                    // Load TTL enabled flag (default: false for KPI persistence)
                    String metricTTLEnabledStr = props.getProperty("metricTTLEnabled", "false");
                    metricTTLEnabled = Boolean.parseBoolean(metricTTLEnabledStr);
                    
                    // Load TTL duration (only used if enabled)
                    String metricTTLStr = props.getProperty("metricTTLSeconds", "30");
                    metricTTLSeconds = Integer.parseInt(metricTTLStr);
                    
                    log.info("Loaded metric TTL configuration: enabled=" + metricTTLEnabled + 
                            ", duration=" + metricTTLSeconds + " seconds");
                    System.out.println("Loaded metric TTL configuration: enabled=" + metricTTLEnabled + 
                                      ", duration=" + metricTTLSeconds + " seconds");
                    
                    if (!metricTTLEnabled) {
                        System.out.println("⚠️  TTL-based metric removal is DISABLED - All gauge metrics will persist for KPI analysis");
                        log.info("TTL-based metric removal is DISABLED - All gauge metrics will persist");
                    } else {
                        System.out.println("✅ TTL-based metric removal is ENABLED - Gauge metrics will be removed after " + 
                                         metricTTLSeconds + " seconds");
                        log.info("TTL-based metric removal is ENABLED with " + metricTTLSeconds + "s duration");
                    }
                }
            } else {
                log.warn("properties.config file not found, using defaults: TTL disabled, 30 seconds");
                System.out.println("WARNING: properties.config file not found, using defaults: TTL disabled, 30 seconds");
            }
        } catch (Exception e) {
            log.error("Error loading metric TTL configuration: " + e.getMessage(), e);
            System.err.println("Error loading metric TTL configuration: " + e.getMessage());
            // Keep default values: disabled, 30 seconds
        }
    }

    /**
     * Load server shutdown delay configuration from properties.config
     */
    private static void loadServerShutdownDelayConfiguration() {
        try {
            String userDir = System.getProperty("user.dir");
            File propertiesFile = new File(userDir, "properties.config");
            
            if (propertiesFile.exists()) {
                java.util.Properties props = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(propertiesFile)) {
                    props.load(fis);
                    
                    String shutdownDelayStr = props.getProperty("serverShutdownDelaySeconds", "300");
                    serverShutdownDelaySeconds = Integer.parseInt(shutdownDelayStr);
                    
                    log.info("Loaded server shutdown delay configuration: " + serverShutdownDelaySeconds + " seconds");
                    System.out.println("Loaded server shutdown delay configuration: " + serverShutdownDelaySeconds + " seconds");
                }
            } else {
                log.warn("properties.config file not found, using default server shutdown delay: 300 seconds (5 minutes)");
                System.out.println("WARNING: properties.config file not found, using default server shutdown delay: 300 seconds (5 minutes)");
            }
        } catch (Exception e) {
            log.error("Error loading server shutdown delay configuration: " + e.getMessage(), e);
            System.err.println("Error loading server shutdown delay configuration: " + e.getMessage());
            // Keep default value of 300 seconds (5 minutes)
        }
    }

    /**
     * Load suite execution delay configuration from properties.config
     */
    private static void loadSuiteExecutionDelayConfiguration() {
        try {
            String userDir = System.getProperty("user.dir");
            File propertiesFile = new File(userDir, "properties.config");
            
            if (propertiesFile.exists()) {
                java.util.Properties props = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(propertiesFile)) {
                    props.load(fis);
                    
                    // Check if suite execution delay is enabled
                    String enabledStr = props.getProperty("suiteExecutionDelayEnabled", "true");
                    suiteExecutionDelayEnabled = Boolean.parseBoolean(enabledStr);
                    
                    // Load delay duration (defaults to same as server shutdown delay)
                    String suiteDelayStr = props.getProperty("suiteExecutionDelaySeconds", 
                        props.getProperty("serverShutdownDelaySeconds", "300"));
                    suiteExecutionDelaySeconds = Integer.parseInt(suiteDelayStr);
                    
                    log.info("Loaded suite execution delay configuration: enabled=" + suiteExecutionDelayEnabled + 
                            ", duration=" + suiteExecutionDelaySeconds + " seconds");
                    System.out.println("Loaded suite execution delay configuration: enabled=" + suiteExecutionDelayEnabled + 
                                      ", duration=" + suiteExecutionDelaySeconds + " seconds");
                }
            } else {
                log.warn("properties.config file not found, using default suite execution delay: enabled=true, 300 seconds (5 minutes)");
                System.out.println("WARNING: properties.config file not found, using default suite execution delay: enabled=true, 300 seconds (5 minutes)");
            }
        } catch (Exception e) {
            log.error("Error loading suite execution delay configuration: " + e.getMessage(), e);
            System.err.println("Error loading suite execution delay configuration: " + e.getMessage());
            // Keep default values
        }
    }

    /**
     * Start the status transition timer
     * This will change status from "active" to "inactive" after the configured delay
     */
    public static void startStatusTransitionTimer() {
        if (statusTimerStarted.compareAndSet(false, true)) {
            statusScheduler = Executors.newSingleThreadScheduledExecutor();
            
            statusScheduler.schedule(() -> {
                currentStatus = "inactive";
                log.info("Status changed to 'inactive' after " + scrapDelaySeconds + " seconds");
                System.out.println("Status changed to 'inactive' after " + scrapDelaySeconds + " seconds");
            }, scrapDelaySeconds, TimeUnit.SECONDS);
            
            log.info("Started status transition timer - will change to 'inactive' after " + scrapDelaySeconds + " seconds");
            System.out.println("Started status transition timer - will change to 'inactive' after " + scrapDelaySeconds + " seconds");
        } else {
            log.info("Status transition timer already started");
            System.out.println("Status transition timer already started");
        }
    }

    /**
     * Get current status
     */
    public static String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Reset status to active (useful for testing or manual reset)
     */
    public static void resetStatusToActive() {
        currentStatus = "active";
        log.info("Status manually reset to 'active'");
        System.out.println("Status manually reset to 'active'");
    }

    /**
     * Shutdown status scheduler
     */
    public static void shutdownStatusScheduler() {
        if (statusScheduler != null && !statusScheduler.isShutdown()) {
            statusScheduler.shutdown();
            try {
                if (!statusScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    statusScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                statusScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Status scheduler shutdown completed");
        }
    }

    /**
     * Schedule server shutdown after suite completion
     * This ensures metrics servers remain active for the configured delay after automation ends
     */
    public static void scheduleServerShutdownAfterSuite(String suiteName, boolean isApiSuite) {
        if (serverShutdownScheduled.compareAndSet(false, true)) {
            serverShutdownScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "ServerShutdown-" + System.currentTimeMillis());
                t.setDaemon(true);
                return t;
            });
            
            serverShutdownTask = serverShutdownScheduler.schedule(() -> {
                try {
                    System.out.println("=== SCHEDULED SERVER SHUTDOWN INITIATED ===");
                    log.info("Shutting down metrics servers after " + serverShutdownDelaySeconds + " seconds delay");
                    
                    // Shutdown both UI and API servers
                    if (server != null) {
                        server.stop();
                        System.out.println("UI metrics server (port " + METRICS_PORT + ") stopped after suite completion");
                        log.info("UI metrics server (port " + METRICS_PORT + ") stopped after suite completion");
                    }
                    
                    if (apiServer != null) {
                        apiServer.stop();
                        System.out.println("API metrics server (port " + API_METRICS_PORT + ") stopped after suite completion");
                        log.info("API metrics server (port " + API_METRICS_PORT + ") stopped after suite completion");
                    }
                    
                    // Shutdown schedulers
                    shutdownStatusScheduler();
                    shutdownMetricCleanupScheduler();
                    
                    // Reset initialization flags
                    initialized.set(false);
                    apiInitialized.set(false);
                    
                    System.out.println("=== ALL METRICS SERVERS SHUTDOWN COMPLETED ===");
                    log.info("All metrics servers shutdown completed for suite: " + suiteName);
                    
                } catch (Exception e) {
                    log.error("Error during scheduled server shutdown: " + e.getMessage(), e);
                    System.err.println("Error during scheduled server shutdown: " + e.getMessage());
                }
            }, serverShutdownDelaySeconds, TimeUnit.SECONDS);
            
            System.out.println("=== SERVER SHUTDOWN SCHEDULED ===");
            System.out.println("Suite '" + suiteName + "' completed (" + (isApiSuite ? "API" : "UI") + " automation)");
            System.out.println("Metrics servers will remain active for " + serverShutdownDelaySeconds + " seconds");
            System.out.println("Automatic shutdown scheduled at: " + 
                new java.util.Date(System.currentTimeMillis() + (serverShutdownDelaySeconds * 1000L)));
            System.out.println("=======================================");
            
            log.info("Scheduled server shutdown in " + serverShutdownDelaySeconds + " seconds for suite: " + suiteName + 
                    " (automation type: " + (isApiSuite ? "API" : "UI") + ")");
        } else {
            System.out.println("Server shutdown already scheduled, extending for suite: " + suiteName);
            log.info("Server shutdown already scheduled, extending for suite: " + suiteName);
            
            // Cancel existing shutdown and reschedule with new delay
            if (serverShutdownTask != null && !serverShutdownTask.isDone()) {
                serverShutdownTask.cancel(false);
                System.out.println("Cancelled previous shutdown, rescheduling for " + serverShutdownDelaySeconds + " seconds");
                
                // Reschedule with new delay
                serverShutdownTask = serverShutdownScheduler.schedule(() -> {
                    try {
                        System.out.println("=== EXTENDED SCHEDULED SERVER SHUTDOWN INITIATED ===");
                        log.info("Shutting down metrics servers after extended " + serverShutdownDelaySeconds + " seconds delay");
                        
                        // Shutdown both UI and API servers
                        if (server != null) {
                            server.stop();
                            System.out.println("UI metrics server (port " + METRICS_PORT + ") stopped after suite completion");
                            log.info("UI metrics server (port " + METRICS_PORT + ") stopped after suite completion");
                        }
                        
                        if (apiServer != null) {
                            apiServer.stop();
                            System.out.println("API metrics server (port " + API_METRICS_PORT + ") stopped after suite completion");
                            log.info("API metrics server (port " + API_METRICS_PORT + ") stopped after suite completion");
                        }
                        
                        // Shutdown schedulers
                        shutdownStatusScheduler();
                        shutdownMetricCleanupScheduler();
                        
                        // Reset initialization flags
                        initialized.set(false);
                        apiInitialized.set(false);
                        
                        System.out.println("=== ALL METRICS SERVERS SHUTDOWN COMPLETED ===");
                        log.info("All metrics servers shutdown completed for suite: " + suiteName);
                        
                    } catch (Exception e) {
                        log.error("Error during extended scheduled server shutdown: " + e.getMessage(), e);
                        System.err.println("Error during extended scheduled server shutdown: " + e.getMessage());
                    }
                }, serverShutdownDelaySeconds, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Cancel scheduled server shutdown (useful for continuous testing)
     */
    public static void cancelScheduledServerShutdown() {
        if (serverShutdownTask != null && !serverShutdownTask.isDone()) {
            serverShutdownTask.cancel(false);
            System.out.println("Cancelled scheduled server shutdown - servers will remain active");
            log.info("Cancelled scheduled server shutdown - servers will remain active");
        }
        
        if (serverShutdownScheduler != null && !serverShutdownScheduler.isShutdown()) {
            serverShutdownScheduler.shutdown();
            try {
                if (!serverShutdownScheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    serverShutdownScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                serverShutdownScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        serverShutdownScheduled.set(false);
    }

    /**
     * Keep suite execution active for the configured delay period
     * This makes the entire test automation suite stay active (not just the metrics servers)
     */
    public static void keepSuiteExecutionActive(String suiteName, boolean isApiSuite) {
        if (!suiteExecutionDelayEnabled) {
            System.out.println("Suite execution delay is disabled - suite will complete immediately");
            log.info("Suite execution delay is disabled for suite: " + suiteName);
            return;
        }
        
        System.out.println("\n=== KEEPING SUITE EXECUTION ACTIVE ===");
        System.out.println("Suite '" + suiteName + "' (" + (isApiSuite ? "API" : "UI") + " automation) will remain active");
        System.out.println("Duration: " + suiteExecutionDelaySeconds + " seconds (" + (suiteExecutionDelaySeconds / 60.0) + " minutes)");
        System.out.println("Start time: " + new java.util.Date());
        System.out.println("End time: " + new java.util.Date(System.currentTimeMillis() + (suiteExecutionDelaySeconds * 1000L)));
        System.out.println("=====================================");
        
        log.info("Keeping suite execution active for " + suiteExecutionDelaySeconds + " seconds - Suite: " + suiteName + 
                " (automation type: " + (isApiSuite ? "API" : "UI") + ")");
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (suiteExecutionDelaySeconds * 1000L);
        int progressInterval = Math.max(30, suiteExecutionDelaySeconds / 10); // Progress update every 30 seconds or 10% of total
        long nextProgressTime = startTime + (progressInterval * 1000L);
        
        try {
            while (System.currentTimeMillis() < endTime) {
                long currentTime = System.currentTimeMillis();
                long remainingTime = (endTime - currentTime) / 1000L;
                
                // Show progress updates
                if (currentTime >= nextProgressTime) {
                    long elapsedTime = (currentTime - startTime) / 1000L;
                    double percentComplete = ((double) elapsedTime / suiteExecutionDelaySeconds) * 100;
                    
                    System.out.println("⏳ Suite execution active - Elapsed: " + elapsedTime + "s (" + 
                        String.format("%.1f", percentComplete) + "%), Remaining: " + remainingTime + "s");
                    System.out.println("   📊 Metrics endpoints accessible:");
                    System.out.println("      - UI metrics: http://localhost:" + METRICS_PORT + "/metrics");
                    if (isApiRunning()) {
                        System.out.println("      - API metrics: http://localhost:" + API_METRICS_PORT + "/metrics");
                    }
                    
                    log.info("Suite execution progress - " + percentComplete + "% complete, " + remainingTime + "s remaining");
                    nextProgressTime = currentTime + (progressInterval * 1000L);
                }
                
                // Sleep for 1 second intervals to allow interruption
                Thread.sleep(1000);
            }
            
            System.out.println("\n=== SUITE EXECUTION DELAY COMPLETED ===");
            System.out.println("Suite '" + suiteName + "' active period finished");
            System.out.println("Total duration: " + suiteExecutionDelaySeconds + " seconds");
            System.out.println("End time: " + new java.util.Date());
            System.out.println("Suite execution will now complete normally");
            System.out.println("=======================================");
            
            log.info("Suite execution delay completed for suite: " + suiteName + " after " + suiteExecutionDelaySeconds + " seconds");
            
        } catch (InterruptedException e) {
            System.out.println("\n⚠️  Suite execution delay interrupted!");
            log.warn("Suite execution delay interrupted for suite: " + suiteName + " - " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
            
            long actualDuration = (System.currentTimeMillis() - startTime) / 1000L;
            System.out.println("Actual duration: " + actualDuration + " seconds (interrupted after " + 
                String.format("%.1f", ((double) actualDuration / suiteExecutionDelaySeconds) * 100) + "%)");
        } catch (Exception e) {
            System.err.println("Error during suite execution delay: " + e.getMessage());
            log.error("Error during suite execution delay for suite: " + suiteName, e);
        }
    }

    /**
     * Get server shutdown delay configuration
     */
    public static int getServerShutdownDelaySeconds() {
        return serverShutdownDelaySeconds;
    }

    /**
     * Get suite execution delay configuration
     */
    public static int getSuiteExecutionDelaySeconds() {
        return suiteExecutionDelaySeconds;
    }

    /**
     * Check if suite execution delay is enabled
     */
    public static boolean isSuiteExecutionDelayEnabled() {
        return suiteExecutionDelayEnabled;
    }

    /**
     * Schedule metric removal after TTL expires
     */
    private static void scheduleMetricRemoval(String suite, String environment, String product, String testType,
                                            String testClass, String testName, String deploymentJobId,
                                            String user, String jiraTestCaseId) {
        
        if (!metricTTLEnabled) {
            log.debug("TTL-based removal is disabled - metric will persist: " + testName);
            return; // Exit early if TTL is disabled
        }
        
        String metricKey = generateMetricKey(suite, environment, product, testType, testClass, testName, 
                                           deploymentJobId, user, jiraTestCaseId);
        
        // Cancel any existing cleanup task for this metric
        ScheduledFuture<?> existingTask = metricCleanupTasks.get(metricKey);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        
        // Schedule new cleanup task
        ScheduledFuture<?> cleanupTask = metricCleanupScheduler.schedule(() -> {
            try {
                // Remove the metric by clearing its labels
                TESTCASE_EXECUTION_STATUS.remove(suite, environment, product, testType, testClass, testName,
                                                deploymentJobId, user, jiraTestCaseId, productVersion, runCounter);
                
                System.out.println("Removed metric for test: " + testName + " after TTL of " + metricTTLSeconds + " seconds");
                log.info("Removed metric for test: " + testName + " after TTL of " + metricTTLSeconds + " seconds");
                
                // Remove from tracking
                metricCleanupTasks.remove(metricKey);
                
            } catch (Exception e) {
                log.error("Error removing metric for test: " + testName, e);
            }
        }, metricTTLSeconds, TimeUnit.SECONDS);
        
        // Track the cleanup task
        metricCleanupTasks.put(metricKey, cleanupTask);
        
        log.info("Scheduled metric removal for test: " + testName + " in " + metricTTLSeconds + " seconds");
    }
    
    /**
     * Schedule suite status metric removal after TTL expires
     */
    private static void scheduleSuiteStatusRemoval(String suite, String environment, String product, String testType,
                                                  String deploymentJobId, String user) {
        
        if (!metricTTLEnabled) {
            log.debug("TTL-based removal is disabled - suite status will persist");
            return; // Exit early if TTL is disabled
        }
        
        String metricKey = "suite_status_" + String.join("_", suite, environment, product, testType, deploymentJobId, user, productVersion, runCounter);
        
        // Cancel any existing cleanup task for this metric
        ScheduledFuture<?> existingTask = metricCleanupTasks.get(metricKey);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        
        // Schedule new cleanup task
        ScheduledFuture<?> cleanupTask = metricCleanupScheduler.schedule(() -> {
            try {
                // Remove the suite status metric
                SUITE_STATUS.remove(suite, environment, product, testType, deploymentJobId, user, productVersion, runCounter);
                
                System.out.println("Removed suite status metric for suite: " + suite + " after TTL of " + metricTTLSeconds + " seconds");
                log.info("Removed suite status metric for suite: " + suite + " after TTL of " + metricTTLSeconds + " seconds");
                
                // Remove from tracking
                metricCleanupTasks.remove(metricKey);
                
            } catch (Exception e) {
                log.error("Error removing suite status metric for suite: " + suite, e);
            }
        }, metricTTLSeconds, TimeUnit.SECONDS);
        
        // Track the cleanup task
        metricCleanupTasks.put(metricKey, cleanupTask);
        
        log.info("Scheduled suite status metric removal for suite: " + suite + " in " + metricTTLSeconds + " seconds");
    }
    
    /**
     * Schedule tests running metric removal after TTL expires
     */
    private static void scheduleTestsRunningRemoval(String suite, String environment, String product, String testType,
                                                   String deploymentJobId, String user) {
        
        if (!metricTTLEnabled) {
            log.debug("TTL-based removal is disabled - tests running gauge will persist");
            return; // Exit early if TTL is disabled
        }
        
        String metricKey = "tests_running_" + String.join("_", suite, environment, product, testType, deploymentJobId, user, productVersion, runCounter);
        
        // Cancel any existing cleanup task for this metric
        ScheduledFuture<?> existingTask = metricCleanupTasks.get(metricKey);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        
        // Schedule new cleanup task
        ScheduledFuture<?> cleanupTask = metricCleanupScheduler.schedule(() -> {
            try {
                // Remove the tests running metric
                TESTS_RUNNING.remove(suite, environment, product, testType, deploymentJobId, user, productVersion, runCounter);
                
                System.out.println("Removed tests running metric for suite: " + suite + " after TTL of " + metricTTLSeconds + " seconds");
                log.info("Removed tests running metric for suite: " + suite + " after TTL of " + metricTTLSeconds + " seconds");
                
                // Remove from tracking
                metricCleanupTasks.remove(metricKey);
                
            } catch (Exception e) {
                log.error("Error removing tests running metric for suite: " + suite, e);
            }
        }, metricTTLSeconds, TimeUnit.SECONDS);
        
        // Track the cleanup task
        metricCleanupTasks.put(metricKey, cleanupTask);
        
        log.info("Scheduled tests running metric removal for suite: " + suite + " in " + metricTTLSeconds + " seconds");
    }
    
    /**
     * Schedule build info metric removal after TTL expires
     */
    private static void scheduleBuildInfoRemoval(String version, String buildTime, String gitCommit,
                                               String environment, String testType, String deploymentJobId, String user) {
        
        if (!metricTTLEnabled) {
            log.debug("TTL-based removal is disabled - build info will persist");
            return; // Exit early if TTL is disabled
        }
        
        String metricKey = "build_info_" + String.join("_", version, buildTime, gitCommit, environment, testType, deploymentJobId, user, productVersion, runCounter);
        
        // Cancel any existing cleanup task for this metric
        ScheduledFuture<?> existingTask = metricCleanupTasks.get(metricKey);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        
        // Schedule new cleanup task
        ScheduledFuture<?> cleanupTask = metricCleanupScheduler.schedule(() -> {
            try {
                // Remove the build info metric
                BUILD_INFO.remove(version, buildTime, gitCommit, environment, testType, deploymentJobId, user, productVersion, runCounter);
                
                System.out.println("Removed build info metric after TTL of " + metricTTLSeconds + " seconds");
                log.info("Removed build info metric after TTL of " + metricTTLSeconds + " seconds");
                
                // Remove from tracking
                metricCleanupTasks.remove(metricKey);
                
            } catch (Exception e) {
                log.error("Error removing build info metric", e);
            }
        }, metricTTLSeconds, TimeUnit.SECONDS);
        
        // Track the cleanup task
        metricCleanupTasks.put(metricKey, cleanupTask);
        
        log.info("Scheduled build info metric removal in " + metricTTLSeconds + " seconds");
    }
    
    /**
     * Generate unique metric key for tracking purposes
     */
    private static String generateMetricKey(String suite, String environment, String product, String testType,
                                          String testClass, String testName, String deploymentJobId,
                                          String user, String jiraTestCaseId) {
        return String.join("_", suite, environment, product, testType, testClass, testName, 
                          deploymentJobId, user, jiraTestCaseId, productVersion, runCounter);
    }

    /**
     * Shutdown metric cleanup scheduler
     */
    public static void shutdownMetricCleanupScheduler() {
        if (metricCleanupScheduler != null && !metricCleanupScheduler.isShutdown()) {
            // Cancel all pending cleanup tasks
            for (ScheduledFuture<?> task : metricCleanupTasks.values()) {
                task.cancel(false);
            }
            metricCleanupTasks.clear();
            
            metricCleanupScheduler.shutdown();
            try {
                if (!metricCleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    metricCleanupScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                metricCleanupScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Metric cleanup scheduler shutdown completed");
        }
    }

    /**
     * Get current metric TTL configuration
     */
    public static int getMetricTTLSeconds() {
        return metricTTLSeconds;
    }
    
    /**
     * Check if metric TTL is enabled
     */
    public static boolean isMetricTTLEnabled() {
        return metricTTLEnabled;
    }
}
