package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HealedLocatorManager manages and persists information about healed locators.
 * It tracks which locators were healed, when they were healed, and what strategy was used.
 * This information is saved to a JSON file for reporting and analysis.
 * 
 * @author SolutionsQA Team
 * @version 1.0
 * @since 2025-01-07
 */
public class HealedLocatorManager {
    
    private static final SolqaLogger log = new SolqaLogger(HealedLocatorManager.class.getName());
    private static HealedLocatorManager instance;
    private static final String HEALED_LOCATORS_DIR = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "HealedLocators";
    private static final String HEALED_LOCATORS_FILE = HEALED_LOCATORS_DIR + File.separator + "healed_locators.json";
    private static final String HEALED_LOCATORS_REPORT = HEALED_LOCATORS_DIR + File.separator + "healed_locators_report.html";
    
    // Thread-safe storage for healed locators
    private final Map<String, List<HealedLocatorInfo>> healedLocators = new ConcurrentHashMap<>();
    
    // Private constructor for singleton pattern
    private HealedLocatorManager() {
        ensureDirectoryExists();
        loadExistingHealedLocators();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized HealedLocatorManager getInstance() {
        if (instance == null) {
            instance = new HealedLocatorManager();
        }
        return instance;
    }
    
    /**
     * Record a healed locator
     * @param testName Name of the test where healing occurred
     * @param originalLocator Original locator that failed
     * @param healedLocator New locator that worked
     * @param strategy Strategy used for healing
     * @param timestamp When the healing occurred
     */
    public void recordHealing(String testName, String originalLocator, String healedLocator, 
                             String strategy, Date timestamp) {
        HealedLocatorInfo info = new HealedLocatorInfo(testName, originalLocator, healedLocator, 
                                                       strategy, timestamp);
        
        healedLocators.computeIfAbsent(testName, k -> new ArrayList<>()).add(info);
        
        log.info("Recorded healed locator for test: " + testName);
        log.info("Original: " + originalLocator);
        log.info("Healed: " + healedLocator);
        log.info("Strategy: " + strategy);
        
        // Save to file immediately
        saveToFile();
    }
    
    /**
     * Get all healed locators for a specific test
     */
    public List<HealedLocatorInfo> getHealedLocatorsForTest(String testName) {
        return healedLocators.getOrDefault(testName, new ArrayList<>());
    }
    
    /**
     * Get all healed locators across all tests
     */
    public Map<String, List<HealedLocatorInfo>> getAllHealedLocators() {
        return new HashMap<>(healedLocators);
    }
    
    /**
     * Check if any locators were healed
     */
    public boolean hasHealedLocators() {
        return !healedLocators.isEmpty();
    }
    
    /**
     * Get count of healed locators for a specific test
     */
    public int getHealedCountForTest(String testName) {
        return healedLocators.getOrDefault(testName, new ArrayList<>()).size();
    }
    
    /**
     * Get total count of healed locators across all tests
     */
    public int getTotalHealedCount() {
        return healedLocators.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    /**
     * Clear all healed locator records (useful for new test runs)
     */
    public void clear() {
        healedLocators.clear();
        log.info("Cleared all healed locator records");
    }
    
    /**
     * Save healed locators to JSON file
     */
    private void saveToFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            
            // Convert to a list format for better JSON structure
            List<HealedLocatorInfo> allHealings = new ArrayList<>();
            for (List<HealedLocatorInfo> healings : healedLocators.values()) {
                allHealings.addAll(healings);
            }
            
            mapper.writeValue(new File(HEALED_LOCATORS_FILE), allHealings);
            log.info("Saved healed locators to: " + HEALED_LOCATORS_FILE);
            
            // Also generate HTML report
            generateHtmlReport();
            
        } catch (IOException e) {
            log.error("Failed to save healed locators to file", e);
        }
    }
    
    /**
     * Load existing healed locators from file (if any)
     */
    private void loadExistingHealedLocators() {
        try {
            File file = new File(HEALED_LOCATORS_FILE);
            if (file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                
                HealedLocatorInfo[] healings = mapper.readValue(file, HealedLocatorInfo[].class);
                
                for (HealedLocatorInfo healing : healings) {
                    healedLocators.computeIfAbsent(healing.getTestName(), k -> new ArrayList<>()).add(healing);
                }
                
                log.info("Loaded " + healings.length + " existing healed locator records");
            }
        } catch (IOException e) {
            log.debug("No existing healed locators file found or failed to load: " + e.getMessage());
        }
    }
    
    /**
     * Ensure the directory for healed locators exists
     */
    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(HEALED_LOCATORS_DIR));
        } catch (IOException e) {
            log.error("Failed to create healed locators directory", e);
        }
    }
    
    /**
     * Generate an HTML report of healed locators
     */
    public void generateHtmlReport() {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n<head>\n");
            html.append("<title>Healed Locators Report</title>\n");
            html.append("<meta charset='UTF-8'>\n");
            html.append("<style>\n");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
            html.append("h1 { color: #333; border-bottom: 3px solid #4CAF50; padding-bottom: 10px; }\n");
            html.append("h2 { color: #555; margin-top: 30px; }\n");
            html.append(".summary { background-color: #fff; padding: 20px; border-radius: 5px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
            html.append(".summary-item { display: inline-block; margin-right: 30px; }\n");
            html.append(".summary-label { font-weight: bold; color: #666; }\n");
            html.append(".summary-value { font-size: 24px; color: #4CAF50; font-weight: bold; }\n");
            html.append("table { border-collapse: collapse; width: 100%; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
            html.append("th { background-color: #4CAF50; color: white; padding: 12px; text-align: left; }\n");
            html.append("td { padding: 10px; border-bottom: 1px solid #ddd; }\n");
            html.append("tr:hover { background-color: #f5f5f5; }\n");
            html.append(".test-name { font-weight: bold; color: #2196F3; }\n");
            html.append(".original { color: #f44336; font-family: monospace; font-size: 12px; word-break: break-all; }\n");
            html.append(".healed { color: #4CAF50; font-family: monospace; font-size: 12px; word-break: break-all; }\n");
            html.append(".strategy { background-color: #FFF9C4; padding: 4px 8px; border-radius: 3px; font-size: 12px; }\n");
            html.append(".timestamp { color: #999; font-size: 12px; }\n");
            html.append(".no-data { text-align: center; padding: 40px; color: #999; font-style: italic; }\n");
            html.append("</style>\n");
            html.append("</head>\n<body>\n");
            
            html.append("<h1>🔧 Locator Healer Report</h1>\n");
            
            // Summary section
            html.append("<div class='summary'>\n");
            html.append("<div class='summary-item'>\n");
            html.append("<div class='summary-label'>Total Tests with Healed Locators:</div>\n");
            html.append("<div class='summary-value'>").append(healedLocators.size()).append("</div>\n");
            html.append("</div>\n");
            html.append("<div class='summary-item'>\n");
            html.append("<div class='summary-label'>Total Healed Locators:</div>\n");
            html.append("<div class='summary-value'>").append(getTotalHealedCount()).append("</div>\n");
            html.append("</div>\n");
            html.append("<div class='summary-item'>\n");
            html.append("<div class='summary-label'>Generated:</div>\n");
            html.append("<div class='summary-value' style='font-size: 14px; color: #666;'>").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("</div>\n");
            html.append("</div>\n");
            html.append("</div>\n");
            
            if (healedLocators.isEmpty()) {
                html.append("<div class='no-data'>No locator healing occurred during this test run.</div>\n");
            } else {
                // Group by test name
                for (Map.Entry<String, List<HealedLocatorInfo>> entry : healedLocators.entrySet()) {
                    html.append("<h2 class='test-name'>Test: ").append(entry.getKey()).append("</h2>\n");
                    html.append("<table>\n");
                    html.append("<tr>\n");
                    html.append("<th style='width: 25%'>Original Locator</th>\n");
                    html.append("<th style='width: 25%'>Healed Locator</th>\n");
                    html.append("<th style='width: 20%'>Strategy Used</th>\n");
                    html.append("<th style='width: 15%'>Timestamp</th>\n");
                    html.append("</tr>\n");
                    
                    for (HealedLocatorInfo info : entry.getValue()) {
                        html.append("<tr>\n");
                        html.append("<td class='original'>").append(escapeHtml(info.getOriginalLocator())).append("</td>\n");
                        html.append("<td class='healed'>").append(escapeHtml(info.getHealedLocator())).append("</td>\n");
                        html.append("<td><span class='strategy'>").append(info.getStrategy()).append("</span></td>\n");
                        html.append("<td class='timestamp'>").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getTimestamp())).append("</td>\n");
                        html.append("</tr>\n");
                    }
                    
                    html.append("</table>\n");
                }
            }
            
            html.append("</body>\n</html>");
            
            // Write to file
            try (FileWriter writer = new FileWriter(HEALED_LOCATORS_REPORT)) {
                writer.write(html.toString());
            }
            
            log.info("Generated HTML report: " + HEALED_LOCATORS_REPORT);
            
        } catch (IOException e) {
            log.error("Failed to generate HTML report", e);
        }
    }
    
    /**
     * Escape HTML special characters
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
     * Inner class to hold healed locator information
     */
    public static class HealedLocatorInfo {
        private String testName;
        private String originalLocator;
        private String healedLocator;
        private String strategy;
        private Date timestamp;
        
        // Default constructor for Jackson
        public HealedLocatorInfo() {
        }
        
        public HealedLocatorInfo(String testName, String originalLocator, String healedLocator, 
                                String strategy, Date timestamp) {
            this.testName = testName;
            this.originalLocator = originalLocator;
            this.healedLocator = healedLocator;
            this.strategy = strategy;
            this.timestamp = timestamp;
        }
        
        // Getters and setters
        public String getTestName() {
            return testName;
        }
        
        public void setTestName(String testName) {
            this.testName = testName;
        }
        
        public String getOriginalLocator() {
            return originalLocator;
        }
        
        public void setOriginalLocator(String originalLocator) {
            this.originalLocator = originalLocator;
        }
        
        public String getHealedLocator() {
            return healedLocator;
        }
        
        public void setHealedLocator(String healedLocator) {
            this.healedLocator = healedLocator;
        }
        
        public String getStrategy() {
            return strategy;
        }
        
        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return "HealedLocatorInfo{" +
                    "testName='" + testName + '\'' +
                    ", originalLocator='" + originalLocator + '\'' +
                    ", healedLocator='" + healedLocator + '\'' +
                    ", strategy='" + strategy + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}

