package util;

import framework.Driver;
import org.openqa.selenium.WebDriver;

/**
 * WebDriverManager utility class for ThreadLocal WebDriver management
 * Provides centralized methods for multi-threading support
 */
public class WebDriverManager {
    
    /**
     * Get the current WebDriver instance for the current thread
     * @return WebDriver instance or null if not initialized
     */
    public static WebDriver getCurrentDriver() {
        return Driver.getCurrentDriver();
    }
    
        
    /**
     * Initialize a new WebDriver for the current thread
     * @return WebDriver instance
     */
    public static WebDriver initializeDriver() {
        Driver driverManager = new Driver();
        return driverManager.getWebDriver();
    }
    
    /**
     * Clean up the WebDriver for the current thread
     * This method should be called in @AfterMethod or @AfterClass
     */
    public static void cleanupDriver() {
        Driver.removeDriver();
    }
    
    /**
     * Check if WebDriver is initialized for current thread
     * @return true if WebDriver exists, false otherwise
     */
    public static boolean isDriverInitialized() {
        return getCurrentDriver() != null;
    }
} 