package util;

import java.util.*;

import framework.Launcher;

/**
 * Utility class to manage test data for model comparison reports
 */
public class TestDataManager extends Launcher {
    
    static SolqaLogger log = new SolqaLogger(TestDataManager.class.getName());
    
    // Thread-safe storage for test data
    private static final ThreadLocal<Map<String, Object>> currentTestData = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };
    
    /**
     * Set context for current test
     */
    public static void setContext(String context) {
        currentTestData.get().put("context", context);
        log.info("Context set: " + context);
    }
    
    /**
     * Set test input for current test
     */
    public static void setTestInput(String testInput) {
        currentTestData.get().put("test_input", testInput);
        log.info("Test input set: " + testInput);
    }
    
    /**
     * Set expected output for current test
     */
    public static void setExpectedOutput(String expectedOutput) {
        currentTestData.get().put("expected_output", expectedOutput);
        log.info("Expected output set: " + expectedOutput);
    }
    
    /**
     * Set actual output for current test
     */
    public static void setActualOutput(String actualOutput) {
        currentTestData.get().put("actual_output", actualOutput);
        log.info("Actual output set: " + actualOutput);
    }
    
    /**
     * Set hallucination score for current test
     */
    public static void setHallucinationScore(Double hallucinationScore) {
        currentTestData.get().put("hallucination_score", hallucinationScore);
        log.info("Hallucination score set: " + hallucinationScore);
    }
    
    /**
     * Set relevancy score for current test
     */
    public static void setRelevancyScore(Double relevancyScore) {
        currentTestData.get().put("relevancy_score", relevancyScore);
        log.info("Relevancy score set: " + relevancyScore);
    }
    
    /**
     * Get all current test data
     */
    public static Map<String, Object> getCurrentTestData() {
        return new HashMap<>(currentTestData.get());
    }
    
    /**
     * Clear current test data
     */
    public static void clearCurrentTestData() {
        currentTestData.get().clear();
        log.info("Current test data cleared");
    }
    
    /**
     * Capture all test data for model comparison report
     */
    public static void captureAllTestData() {
        Map<String, Object> data = getCurrentTestData();
        
        String context = (String) data.get("context");
        String testInput = (String) data.get("test_input");
        String expectedOutput = (String) data.get("expected_output");
        String actualOutput = (String) data.get("actual_output");
        Double hallucinationScore = (Double) data.get("hallucination_score");
        Double relevancyScore = (Double) data.get("relevancy_score");
        
        ModelComparisonReportListener.captureTestData(
            context, testInput, expectedOutput, actualOutput, 
            hallucinationScore, relevancyScore
        );
        
        log.info("All test data captured for model comparison report");
    }
    
    /**
     * Helper method to create test data map
     */
    public static Map<String, Object> createTestDataMap(String context, String testInput, 
                                                       String expectedOutput, String actualOutput,
                                                       Double hallucinationScore, Double relevancyScore) {
        Map<String, Object> testData = new HashMap<>();
        testData.put("context", context);
        testData.put("test_input", testInput);
        testData.put("expected_output", expectedOutput);
        testData.put("actual_output", actualOutput);
        testData.put("hallucination_score", hallucinationScore);
        testData.put("relevancy_score", relevancyScore);
        return testData;
    }
}
