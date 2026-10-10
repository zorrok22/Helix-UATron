package util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import util.ReadJson;

/**
 * Thread-safe user manager for multi-threaded test execution
 * This class ensures each thread has its own user data instances
 */
public class ThreadSafeUserManager {
    
    // ThreadLocal storage for user data
    private static final ThreadLocal<Map<String, Object>> threadUserData = new ThreadLocal<>();
    private static final ReadJson readJson = new ReadJson();
    
    /**
     * Get user data for the current thread
     * @param appName Application name
     * @param fileName File name
     * @param inputArray Input array name
     * @return Map containing user data
     */
    public static Map<Object, String> getUserData(String appName, String fileName, String inputArray) {
        Map<String, Object> threadData = threadUserData.get();
        if (threadData == null) {
            threadData = new ConcurrentHashMap<>();
            threadUserData.set(threadData);
        }
        
        String key = appName + "_" + fileName + "_" + inputArray;
        if (!threadData.containsKey(key)) {
            Map<Object, String> userData = readJson.getInputValuesMapIncludingLabelField(appName, fileName, inputArray);
            threadData.put(key, userData);
        }
        
        @SuppressWarnings("unchecked")
        Map<Object, String> result = (Map<Object, String>) threadData.get(key);
        return result;
    }
    
    /**
     * Clear thread-specific user data
     * This should be called in @AfterMethod or @AfterClass
     */
    public static void clearThreadData() {
        threadUserData.remove();
    }
    
    /**
     * Get current thread ID for logging
     * @return Thread ID as string
     */
    public static String getCurrentThreadId() {
        return String.valueOf(Thread.currentThread().getId());
    }
} 