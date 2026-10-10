package util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import framework.Launcher;

/**
 * TestNG listener to automatically capture test data and generate model comparison report
 */
public class ModelComparisonReportListener extends Launcher implements ITestListener {
    
    static SolqaLogger log = new SolqaLogger(ModelComparisonReportListener.class.getName());
    
    // Global storage for test data
    private static final Map<String, Map<String, Object>> testDataStorage = new ConcurrentHashMap<>();
    private static final Map<String, String> currentTestData = new ConcurrentHashMap<>();
    
    @Override
    public void onTestStart(ITestResult result) {
        // Clear current test data at the start of each test
        currentTestData.clear();
        log.info("📊 ModelComparisonReportListener: Test started: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        processTestResult(result, "PASS");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        processTestResult(result, "FAIL");
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        processTestResult(result, "SKIP");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        // Generate model comparison report at the end of test suite
        generateModelComparisonReport();
    }
    
    /**
     * Process test result and store data
     */
    private void processTestResult(ITestResult result, String status) {
        String testName = result.getMethod().getMethodName();
        
        // Get test parameters (provider and model from MultiProviderConfig)
        // Skip ITestContext parameter (typically the first parameter in HelixGPT tests)
        Object[] parameters = result.getParameters();
        String providerConfig = "default";
        
        if (parameters != null && parameters.length > 0) {
            // Debug: Log all parameters
            log.info("📊 Processing test parameters (count=" + parameters.length + "):");
            for (int i = 0; i < parameters.length; i++) {
                Object param = parameters[i];
                String paramType = param != null ? param.getClass().getName() : "null";
                String paramValue = param != null ? param.toString() : "null";
                log.info("📊   param[" + i + "]: type=" + paramType + ", value=" + paramValue);
            }
            
            // Find the actual provider and model parameters (skip ITestContext and TestRunner)
            String provider = null;
            String model = null;
            
            for (Object param : parameters) {
                if (param != null && 
                    !(param instanceof ITestContext) && 
                    !param.getClass().getName().contains("TestRunner")) {
                    
                    if (provider == null) {
                        provider = param.toString();
                    } else if (model == null) {
                        model = param.toString();
                        break; // We have both, stop looking
                    }
                }
            }
            
            if (provider != null && model != null) {
                providerConfig = provider + "," + model;
            } else if (provider != null) {
                providerConfig = provider;
            }
            
            log.info("📊   Final providerConfig: " + providerConfig);
        }
        
        // Create full test name with provider config
        String fullTestName = testName + "[" + providerConfig + "]";
        
        // Create test result data
        Map<String, Object> testResultData = new HashMap<>();
        testResultData.put("test_name", fullTestName);
        testResultData.put("status", status);
        testResultData.put("context", currentTestData.getOrDefault("context", "N/A"));
        testResultData.put("test_input", currentTestData.getOrDefault("test_input", "N/A"));
        testResultData.put("expected_output", currentTestData.getOrDefault("expected_output", "N/A"));
        testResultData.put("actual_output", currentTestData.getOrDefault("actual_output", "N/A"));
        testResultData.put("hallucination_score", parseDouble(currentTestData.get("hallucination_score")));
        testResultData.put("relevancy_score", parseDouble(currentTestData.get("relevancy_score")));
        testResultData.put("refusal_pattern", currentTestData.getOrDefault("refusal_pattern", "none"));
        testResultData.put("error_message", result.getThrowable() != null ? result.getThrowable().getMessage() : null);
        
        // Store test result data
        testDataStorage.put(fullTestName, testResultData);
        
        log.info("📊 Test result processed: " + fullTestName + " - " + status);
        log.info("📊   Provider config extracted: " + providerConfig);
        log.info("📊   Test data - H-Score: " + currentTestData.get("hallucination_score") + 
                 ", R-Score: " + currentTestData.get("relevancy_score") +
                 ", Refusal: " + currentTestData.getOrDefault("refusal_pattern", "none"));
        
        // Warn if test has no data (probably failed before DeepEval)
        if ("N/A".equals(currentTestData.get("test_input")) || currentTestData.get("test_input") == null) {
            log.warn("📊   ⚠️ WARNING: No test data captured for this test! Test probably failed before DeepEval evaluation.");
        }
        
        log.info("📊 Total tests in storage: " + testDataStorage.size());
    }
    
    /**
     * Generate model comparison report
     */
    private void generateModelComparisonReport() {
        log.info("========================================");
        log.info("MODEL COMPARISON REPORT GENERATION");
        log.info("========================================");
        log.info("Test data storage size: " + testDataStorage.size());
        
        if (testDataStorage.isEmpty()) {
            log.warn("⚠️ No test data available for model comparison report");
            log.warn("Possible reasons:");
            log.warn("1. Tests didn't call captureTestData()");
            log.warn("2. Tests didn't use DeepEval evaluation");
            log.warn("3. All tests were skipped");
            return;
        }
        
        log.info("Found " + testDataStorage.size() + " test result(s) to include in report");
        for (String testName : testDataStorage.keySet()) {
            log.info("  - " + testName);
        }
        
        try {
            ModelComparisonReportGenerator generator = new ModelComparisonReportGenerator();
            generator.processTestResults(new ArrayList<>(testDataStorage.values()));
            String reportPath = generator.generateHtmlReport();
            log.info("✅ Model comparison report generated successfully: " + reportPath);
            log.info("========================================");
        } catch (Exception e) {
            log.error("❌ Error generating model comparison report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Capture test data (called from tests)
     */
    public static void captureTestData(String context, String testInput, String expectedOutput, 
                                     String actualOutput, Double hallucinationScore, Double relevancyScore) {
        captureTestData(context, testInput, expectedOutput, actualOutput, hallucinationScore, relevancyScore, "none");
    }
    
    /**
     * Capture test data with refusal pattern (called from tests)
     */
    public static void captureTestData(String context, String testInput, String expectedOutput, 
                                     String actualOutput, Double hallucinationScore, Double relevancyScore, String refusalPattern) {
        if (context != null) currentTestData.put("context", context);
        if (testInput != null) currentTestData.put("test_input", testInput);
        if (expectedOutput != null) currentTestData.put("expected_output", expectedOutput);
        if (actualOutput != null) currentTestData.put("actual_output", actualOutput);
        if (hallucinationScore != null) currentTestData.put("hallucination_score", hallucinationScore.toString());
        if (relevancyScore != null) currentTestData.put("relevancy_score", relevancyScore.toString());
        if (refusalPattern != null) currentTestData.put("refusal_pattern", refusalPattern);
        
        log.info("📊 ModelComparisonReportListener.captureTestData() called");
        log.info("📊   - Hallucination: " + hallucinationScore);
        log.info("📊   - Relevancy: " + relevancyScore);
        log.info("📊   - Refusal: " + refusalPattern);
    }
    
    /**
     * Parse double from string
     */
    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
