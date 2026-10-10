package util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.testng.Assert;

import framework.Launcher;

/**
 * Generates Model Comparison Report for Java tests
 * Equivalent to Python model_comparison_report.py
 */
public class ModelComparisonReportGenerator extends Launcher {
    
    static SolqaLogger log = new SolqaLogger(ModelComparisonReportGenerator.class.getName());
    
    // Dynamically determined providers from test execution
    private java.util.Set<String> providers = new java.util.LinkedHashSet<>();
    
    private List<TestResult> testResults = new ArrayList<>();
    
    /**
     * Test result data structure
     */
    public static class TestResult {
        public String jiraLink;
        public String testSummary;
        public String context;
        public String input;
        public String expectedOutput;
        public Map<String, ProviderResult> providerResults = new HashMap<>();
        
        public TestResult(String jiraLink, String testSummary, String context, 
                         String input, String expectedOutput) {
            this.jiraLink = jiraLink;
            this.testSummary = testSummary;
            this.context = context;
            this.input = input;
            this.expectedOutput = expectedOutput;
        }
    }
    
    /**
     * Provider result data structure
     */
    public static class ProviderResult {
        public String score;
        public String passFail;
        public String actualOutput;
        public Double hallucinationScore;
        public Double relevancyScore;
        public String refusalPattern;
        
        public ProviderResult(String score, String passFail, String actualOutput,
                            Double hallucinationScore, Double relevancyScore, String refusalPattern) {
            this.score = score;
            this.passFail = passFail;
            this.actualOutput = actualOutput;
            this.hallucinationScore = hallucinationScore;
            this.relevancyScore = relevancyScore;
            this.refusalPattern = refusalPattern;
        }
    }
    
    /**
     * Add a test result to the report
     */
    public void addTestResult(TestResult testResult) {
        testResults.add(testResult);
    }
    
    /**
     * Extract provider name from config key
     * Maps the provider and model from config to display name in report
     */
    private String extractProviderFromConfig(String configKey) {
        // Handle parametrized test name format: "provider,model" or just "provider"
        String[] parts = configKey.split(",");
        String provider = parts[0].trim();
        String model = parts.length > 1 ? parts[1].trim() : "";
        
        log.info("Extracting provider from: '" + configKey + "' -> provider='" + provider + "', model='" + model + "'");
        
        // Map to display names matching Python report format
        if (provider.contains("Azure OpenAI")) {
            if (model.contains("4.1") || model.contains("GPT-4.1") || model.contains("helixgpt")) {
                return "Azure OpenAI - GPT-4.1-helixgpt";
            } else if (model.contains("4-turbo") || model.contains("GPT-4-turbo")) {
                return "Azure OpenAI - GPT-4-turbo";
            }
            // Default Azure OpenAI mapping
            return "Azure OpenAI - " + model;
        } else if (provider.contains("Google Vertex AI")) {
            if (model.contains("gemini-2.0-flash")) {
                return "GCP Vertex - gemini-2.0-flash-001";
            }
            return "Google Vertex AI";
        } else if (provider.contains("GCP") || provider.contains("Vertex")) {
            return "GCP Vertex - gemini-2.0-flash-001";
        } else if (provider.contains("Oracle")) {
            return "Oracle Cloud - llama-3.1-70b-instruct";
        }
        
        // Fallback: return provider + model as-is
        String result = provider + (model.isEmpty() ? "" : " - " + model);
        log.info("Provider mapped to: '" + result + "'");
        return result;
    }
    
    /**
     * Generate HTML report
     */
    public String generateHtmlReport() {
        String reportDir = "Reports";
        new File(reportDir).mkdirs();
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String outputPath = reportDir + File.separator + "model_comparison_report_" + timestamp + ".html";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath, java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(generateHtmlContent());
            log.info("Model Comparison Report generated: " + outputPath);
            return outputPath;
        } catch (IOException e) {
            log.error("Error generating model comparison report: " + e.getMessage());
            Assert.fail("Failed to generate model comparison report: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate HTML content
     */
    private String generateHtmlContent() {
        StringBuilder html = new StringBuilder();
        
        // HTML header
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Model Comparison Report</title>\n");
        html.append("    <style>\n");
        html.append(getCssStyles());
        html.append("    </style>\n");
        html.append("</head>\n");
        // Get environment/setup name from config
        String environment = configProp != null ? configProp.getOrDefault("setupName", "Java Tests") : "Java Tests";
        
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <header>\n");
        html.append("            <h1>Model Comparison Report</h1>\n");
        html.append("            <p class=\"timestamp\">Generated on: " + 
                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + 
                   ", Environment : " + environment + "</p>\n");
        html.append("        </header>\n");
        html.append("        <div class=\"table-container\">\n");
        html.append("            <table class=\"comparison-table\">\n");
        html.append("                <thead>\n");
        html.append("                    <tr>\n");
        html.append("                        <th>JIRA Link</th>\n");
        html.append("                        <th>Test Summary</th>\n");
        html.append("                        <th>Context</th>\n");
        html.append("                        <th>Input</th>\n");
        html.append("                        <th>Expected Output</th>\n");
        
        // Add provider columns dynamically based on what providers are in test results
        for (String provider : providers) {
            html.append("                        <th class=\"provider-column\">").append(provider).append("</th>\n");
        }
        
        html.append("                    </tr>\n");
        html.append("                </thead>\n");
        html.append("                <tbody>");
        
        // Add test result rows
        for (TestResult testResult : testResults) {
            html.append("<tr>\n");
            html.append("                        <td class=\"jira-link\"><a href=\"").append(testResult.jiraLink).append("\" target=\"_blank\">").append(testResult.jiraLink).append("</a></td>\n");
            html.append("                        <td class=\"test-summary\">").append(escapeHtml(testResult.testSummary)).append("</td>\n");
            html.append("                        <td class=\"context\">").append(escapeHtml(testResult.context)).append("</td>\n");
            html.append("                        <td class=\"input\">").append(escapeHtml(testResult.input)).append("</td>\n");
            html.append("                        <td class=\"expected-output\">").append(escapeHtml(testResult.expectedOutput)).append("</td>\n");
            
            // Add provider result columns dynamically
            for (String provider : providers) {
                ProviderResult result = testResult.providerResults.get(provider);
                if (result != null) {
                    String cellClass = "pass".equals(result.passFail) ? "pass" : 
                                     "fail".equals(result.passFail) ? "fail" : "na";
                    html.append("                        <td class=\"provider-result ").append(cellClass).append("\">\n");
                    
                    // Test result
                    html.append("                            <div class=\"test-result\">\n");
                    html.append("                                <div class=\"status\">").append(result.passFail).append("</div>\n");
                    html.append("                            </div>\n");
                    
                    // Metrics
                    html.append("                            <div class=\"metrics\">\n");
                    if (result.relevancyScore != null) {
                        String relevancyStatus = result.relevancyScore > 0.7 ? "PASS" : "FAIL";
                        String relevancyClass = result.relevancyScore > 0.7 ? "metric-pass" : "metric-fail";
                        html.append("                                <div class=\"metric-item ").append(relevancyClass).append("\">\n");
                        html.append("                                    <strong>Answer Relevancy:</strong> ").append(String.format("%.3f", result.relevancyScore)).append(" (").append(relevancyStatus).append(")\n");
                        html.append("                                </div>\n");
                    }
                    
                    if (result.hallucinationScore != null) {
                        String hallucinationStatus = result.hallucinationScore <= 0.3 ? "PASS" : "FAIL";
                        String hallucinationClass = result.hallucinationScore <= 0.3 ? "metric-pass" : "metric-fail";
                        html.append("                                <div class=\"metric-item ").append(hallucinationClass).append("\">\n");
                        html.append("                                    <strong>Hallucination:</strong> ").append(String.format("%.3f", result.hallucinationScore)).append(" (").append(hallucinationStatus).append(")\n");
                        html.append("                                </div>\n");
                    }
                    
                    // Add refusal pattern indicator if detected
                    if (result.refusalPattern != null && !result.refusalPattern.isEmpty() && !result.refusalPattern.equals("none")) {
                        html.append("                                <div class=\"metric-item metric-refusal\">\n");
                        html.append("                                    <strong>⚠️ Bot Refusal:</strong> ").append(escapeHtml(result.refusalPattern)).append("\n");
                        html.append("                                </div>\n");
                    }
                    html.append("                            </div>\n");
                    
                    // Actual output
                    html.append("                            <div class=\"actual-output\">\n");
                    html.append("                                <strong>Actual Output:</strong>\n");
                    html.append("                                <div class=\"output-content\" title=\"Scroll to view full output\">").append(escapeHtml(result.actualOutput)).append("</div>\n");
                    html.append("                            </div>\n");
                    html.append("                        </td>\n");
                } else {
                    html.append("                        <td class=\"provider-result na\">\n");
                    html.append("                            <div class=\"test-result\">\n");
                    html.append("                                <div class=\"status\">N/A</div>\n");
                    html.append("                            </div>\n");
                    html.append("                        </td>\n");
                }
            }
            
            html.append("                    </tr>\n");
        }
        
        html.append("                </tbody>\n");
        html.append("            </table>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * Get CSS styles (same as Python version)
     */
    private String getCssStyles() {
        return "* {\n" +
               "    margin: 0;\n" +
               "    padding: 0;\n" +
               "    box-sizing: border-box;\n" +
               "}\n" +
               "\n" +
               "body {\n" +
               "    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
               "    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
               "    min-height: 100vh;\n" +
               "    padding: 20px;\n" +
               "}\n" +
               "\n" +
               ".container {\n" +
               "    max-width: 100%;\n" +
               "    margin: 0 auto;\n" +
               "    background: white;\n" +
               "    border-radius: 10px;\n" +
               "    box-shadow: 0 10px 30px rgba(0,0,0,0.3);\n" +
               "    overflow: hidden;\n" +
               "}\n" +
               "\n" +
               "header {\n" +
               "    background: linear-gradient(45deg, #2c3e50, #3498db);\n" +
               "    color: white;\n" +
               "    padding: 30px;\n" +
               "    text-align: center;\n" +
               "}\n" +
               "\n" +
               "header h1 {\n" +
               "    font-size: 2.5em;\n" +
               "    margin-bottom: 10px;\n" +
               "    text-shadow: 2px 2px 4px rgba(0,0,0,0.3);\n" +
               "}\n" +
               "\n" +
               ".timestamp {\n" +
               "    font-size: 1.1em;\n" +
               "    opacity: 0.9;\n" +
               "}\n" +
               "\n" +
               ".table-container {\n" +
               "    overflow-x: auto;\n" +
               "    padding: 20px;\n" +
               "}\n" +
               "\n" +
               ".comparison-table {\n" +
               "    width: 100%;\n" +
               "    border-collapse: collapse;\n" +
               "    background: white;\n" +
               "    border-radius: 8px;\n" +
               "    overflow: hidden;\n" +
               "    box-shadow: 0 4px 6px rgba(0,0,0,0.1);\n" +
               "}\n" +
               "\n" +
               ".comparison-table th {\n" +
               "    background: linear-gradient(45deg, #34495e, #2c3e50);\n" +
               "    color: white;\n" +
               "    padding: 15px 10px;\n" +
               "    text-align: left;\n" +
               "    font-weight: 600;\n" +
               "    font-size: 0.9em;\n" +
               "    position: sticky;\n" +
               "    top: 0;\n" +
               "    z-index: 10;\n" +
               "}\n" +
               "\n" +
               ".provider-column {\n" +
               "    min-width: 200px;\n" +
               "    background: linear-gradient(45deg, #27ae60, #2ecc71) !important;\n" +
               "}\n" +
               "\n" +
               ".comparison-table td {\n" +
               "    padding: 12px 10px;\n" +
               "    border-bottom: 1px solid #ecf0f1;\n" +
               "    vertical-align: top;\n" +
               "    font-size: 0.85em;\n" +
               "    line-height: 1.4;\n" +
               "}\n" +
               "\n" +
               ".comparison-table tr:hover {\n" +
               "    background-color: #f8f9fa;\n" +
               "}\n" +
               "\n" +
               ".jira-link a {\n" +
               "    color: #3498db;\n" +
               "    text-decoration: none;\n" +
               "    font-weight: 500;\n" +
               "}\n" +
               "\n" +
               ".jira-link a:hover {\n" +
               "    text-decoration: underline;\n" +
               "}\n" +
               "\n" +
               ".test-summary {\n" +
               "    max-width: 200px;\n" +
               "    word-wrap: break-word;\n" +
               "}\n" +
               "\n" +
               ".context, .input, .expected-output {\n" +
               "    max-width: 250px;\n" +
               "    word-wrap: break-word;\n" +
               "    font-size: 0.8em;\n" +
               "}\n" +
               "\n" +
               ".provider-result {\n" +
               "    min-width: 280px;\n" +
               "    max-width: 320px;\n" +
               "    padding: 12px;\n" +
               "    vertical-align: top;\n" +
               "}\n" +
               "\n" +
               ".test-result {\n" +
               "    text-align: center;\n" +
               "    margin-bottom: 10px;\n" +
               "}\n" +
               "\n" +
               ".provider-result .status {\n" +
               "    padding: 6px 12px;\n" +
               "    border-radius: 4px;\n" +
               "    font-weight: bold;\n" +
               "    font-size: 0.9em;\n" +
               "    display: inline-block;\n" +
               "    margin-bottom: 8px;\n" +
               "}\n" +
               "\n" +
               ".provider-result.pass .status {\n" +
               "    background-color: #d4edda;\n" +
               "    color: #155724;\n" +
               "}\n" +
               "\n" +
               ".provider-result.fail .status {\n" +
               "    background-color: #f8d7da;\n" +
               "    color: #721c24;\n" +
               "}\n" +
               "\n" +
               ".provider-result.na .status {\n" +
               "    background-color: #e2e3e5;\n" +
               "    color: #6c757d;\n" +
               "}\n" +
               "\n" +
               ".metrics {\n" +
               "    border: 1px solid #e9ecef;\n" +
               "    border-radius: 6px;\n" +
               "    padding: 8px;\n" +
               "    margin-bottom: 10px;\n" +
               "    background-color: #f8f9fa;\n" +
               "}\n" +
               "\n" +
               ".metric-item {\n" +
               "    padding: 4px 6px;\n" +
               "    margin: 3px 0;\n" +
               "    border-radius: 3px;\n" +
               "    font-size: 0.75em;\n" +
               "    line-height: 1.4;\n" +
               "}\n" +
               "\n" +
               ".metric-item strong {\n" +
               "    display: inline-block;\n" +
               "    min-width: 80px;\n" +
               "    font-size: 0.7em;\n" +
               "    text-transform: uppercase;\n" +
               "    letter-spacing: 0.5px;\n" +
               "}\n" +
               "\n" +
               ".metric-pass {\n" +
               "    background-color: #d1ecf1;\n" +
               "    color: #0c5460;\n" +
               "    border-left: 3px solid #17a2b8;\n" +
               "}\n" +
               "\n" +
               ".metric-fail {\n" +
               "    background-color: #f8d7da;\n" +
               "    color: #721c24;\n" +
               "    border-left: 3px solid #dc3545;\n" +
               "}\n" +
               "\n" +
               ".metric-na {\n" +
               "    background-color: #e2e3e5;\n" +
               "    color: #6c757d;\n" +
               "    border-left: 3px solid #6c757d;\n" +
               "}\n" +
               "\n" +
               ".metric-refusal {\n" +
               "    background-color: #fff3cd;\n" +
               "    color: #856404;\n" +
               "    border-left: 3px solid #ffc107;\n" +
               "    font-weight: 600;\n" +
               "}\n" +
               "\n" +
               ".actual-output {\n" +
               "    border-top: 1px solid #e9ecef;\n" +
               "    padding-top: 8px;\n" +
               "    position: relative;\n" +
               "}\n" +
               "\n" +
               ".actual-output::before {\n" +
               "    content: '📜';\n" +
               "    position: absolute;\n" +
               "    top: 8px;\n" +
               "    right: 5px;\n" +
               "    font-size: 0.8em;\n" +
               "    opacity: 0.6;\n" +
               "    z-index: 1;\n" +
               "}\n" +
               "\n" +
               ".actual-output strong {\n" +
               "    display: block;\n" +
               "    font-size: 0.75em;\n" +
               "    color: #495057;\n" +
               "    margin-bottom: 5px;\n" +
               "    text-transform: uppercase;\n" +
               "    letter-spacing: 0.5px;\n" +
               "}\n" +
               "\n" +
               ".output-content {\n" +
               "    font-size: 0.7em;\n" +
               "    line-height: 1.4;\n" +
               "    color: #6c757d;\n" +
               "    word-wrap: break-word;\n" +
               "    overflow-wrap: break-word;\n" +
               "    max-height: 200px;\n" +
               "    overflow-y: auto;\n" +
               "    overflow-x: hidden;\n" +
               "    padding: 8px;\n" +
               "    background-color: #fff3cd;\n" +
               "    border: 1px solid #ffeaa7;\n" +
               "    border-radius: 4px;\n" +
               "    position: relative;\n" +
               "}\n" +
               "\n" +
               "/* Enhanced scrollbar styling */\n" +
               ".output-content::-webkit-scrollbar {\n" +
               "    width: 8px;\n" +
               "}\n" +
               "\n" +
               ".output-content::-webkit-scrollbar-track {\n" +
               "    background: #f1f1f1;\n" +
               "    border-radius: 4px;\n" +
               "}\n" +
               "\n" +
               ".output-content::-webkit-scrollbar-thumb {\n" +
               "    background: #888;\n" +
               "    border-radius: 4px;\n" +
               "}\n" +
               "\n" +
               ".output-content::-webkit-scrollbar-thumb:hover {\n" +
               "    background: #555;\n" +
               "}\n" +
               "\n" +
               "/* Firefox scrollbar styling */\n" +
               ".output-content {\n" +
               "    scrollbar-width: thin;\n" +
               "    scrollbar-color: #888 #f1f1f1;\n" +
               "}\n" +
               "\n" +
               "/* Responsive design */\n" +
               "@media (max-width: 768px) {\n" +
               "    body {\n" +
               "        padding: 10px;\n" +
               "    }\n" +
               "    \n" +
               "    header h1 {\n" +
               "        font-size: 2em;\n" +
               "    }\n" +
               "    \n" +
               "    .comparison-table th,\n" +
               "    .comparison-table td {\n" +
               "        padding: 8px 5px;\n" +
               "        font-size: 0.75em;\n" +
               "    }\n" +
               "    \n" +
               "    .provider-result {\n" +
               "        min-width: 250px;\n" +
               "        max-width: 270px;\n" +
               "    }\n" +
               "    \n" +
               "    .output-content {\n" +
               "        font-size: 0.65em;\n" +
               "        max-height: 150px;\n" +
               "    }\n" +
               "    \n" +
               "    .metric-item {\n" +
               "        font-size: 0.7em;\n" +
               "    }\n" +
               "}\n" +
               "\n" +
               "@media (max-width: 480px) {\n" +
               "    .provider-result {\n" +
               "        min-width: 200px;\n" +
               "        max-width: 220px;\n" +
               "    }\n" +
               "    \n" +
               "    .output-content {\n" +
               "        font-size: 0.6em;\n" +
               "        max-height: 120px;\n" +
               "    }\n" +
               "    \n" +
               "    .metric-item {\n" +
               "        font-size: 0.65em;\n" +
               "        padding: 3px 4px;\n" +
               "    }\n" +
               "}";
    }
    
    /**
     * Escape HTML characters
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
     * Process test results and generate report
     */
    public void processTestResults(List<Map<String, Object>> testResultsData) {
        Map<String, TestResult> groupedResults = new HashMap<>();
        
        for (Map<String, Object> result : testResultsData) {
            String testName = (String) result.get("test_name");
            String providerConfig = extractProviderFromTestName(testName);
            String baseName = extractBaseTestName(testName);
            
            log.info("Processing test result: " + testName);
            log.info("  Base name: " + baseName);
            log.info("  Provider config: " + providerConfig);
            
            if (!groupedResults.containsKey(baseName)) {
                groupedResults.put(baseName, new TestResult(
                    "JIRA-" + baseName.replace("_", "-"),
                    baseName.replace("_", " "),
                    (String) result.getOrDefault("context", "N/A"),
                    (String) result.getOrDefault("test_input", "N/A"),
                    (String) result.getOrDefault("expected_output", "N/A")
                ));
            }
            
            String providerName = extractProviderFromConfig(providerConfig);
            providers.add(providerName); // Add to dynamic provider list
            log.info("  Mapped to provider: " + providerName);
            
            Double hallucinationScore = (Double) result.get("hallucination_score");
            Double relevancyScore = (Double) result.get("relevancy_score");
            String refusalPattern = (String) result.getOrDefault("refusal_pattern", "none");
            
            // Determine pass/fail status
            String passFail = "FAIL";
            if (hallucinationScore != null && relevancyScore != null) {
                if (hallucinationScore <= 0.3 && relevancyScore > 0.7) {
                    passFail = "PASS";
                }
            }
            
            String scoreDisplay = "N/A";
            if (hallucinationScore != null && relevancyScore != null) {
                scoreDisplay = String.format("H: %.2f, R: %.2f", hallucinationScore, relevancyScore);
            }
            
            ProviderResult providerResult = new ProviderResult(
                scoreDisplay,
                passFail,
                (String) result.getOrDefault("actual_output", "N/A"),
                hallucinationScore,
                relevancyScore,
                refusalPattern
            );
            
            groupedResults.get(baseName).providerResults.put(providerName, providerResult);
        }
        
        testResults.addAll(groupedResults.values());
        log.info("Total providers found: " + providers.size() + " -> " + providers);
    }
    
    /**
     * Extract base test name from parametrized test name
     */
    private String extractBaseTestName(String testName) {
        if (testName.contains("[")) {
            return testName.substring(0, testName.indexOf("["));
        }
        return testName;
    }
    
    /**
     * Extract provider config from test name
     */
    private String extractProviderFromTestName(String testName) {
        if (testName.contains("[") && testName.contains("]")) {
            return testName.substring(testName.indexOf("[") + 1, testName.indexOf("]"));
        }
        return "default";
    }
}
