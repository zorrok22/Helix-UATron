package ZDT_API;

import util.ReadJson;
import util.SolqaLogger;
import util.UserUpdater;
import java.io.File;
import java.util.Map;

/**
 * Utility class for ZDT API test suite
 * Provides common functionality for Excel data reading and user setup
 */
public class ZDTUtils {
    private static final SolqaLogger log = new SolqaLogger(ZDTUtils.class.getName());
    
    /**
     * Sets up ZDT user data by reading from Excel and updating JSON
     * This follows the ITSM/ITOM pattern - always uses "bmcopssupportagent" as the key,
     * but updates the credentials based on the runCounter value (which row in Excel to read)
     * 
     * @param config Configuration properties map
     * @return ZDTUserSetup object containing user details and ReadJson instance
     */
    public static ZDTUserSetup setupZDTUserData(Map<String, String> config) {
        int counter;
        // Always use "bmcopssupportagent" as the fixed key (ITSM pattern)
        String userKey = "bmcopssupportagent";
        
        // Parse runCounter with error handling for placeholder values
        String runCounterStr = config.get("runCounter");
        try {
            counter = Integer.parseInt(runCounterStr);
            if (counter < 0 || counter > 14) {
                log.warn("runCounter " + counter + " out of range (0-14). Defaulting to 0");
                counter = 0;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid runCounter value '" + runCounterStr + "'. Defaulting to 0 (no Excel update)");
            counter = 0;
        }
        
        if (counter > 0) {
            String filename = "ZDT_User_Data";
            UserUpdater zdtUserUpdater = new UserUpdater();
            String excelFilePath = System.getProperty("user.dir") + File.separator + 
                                   "resources" + File.separator + "ITSM" + File.separator + 
                                   "ZDT" + File.separator + filename + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + 
                                  "resources" + File.separator + "ITSM" + File.separator + 
                                  "ZDT" + File.separator + filename + ".json";
            
            log.info("Setting up ZDT users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);
            
            try {
                // Load and update JSON from Excel row
                // Excel structure: Row 0 = Header, Row 1+ = Data rows
                // runCounter=1 reads Row 0 (due to rowIndex-1), runCounter=2 reads Row 1, etc.
                zdtUserUpdater.loadJsonFile(jsonFilePath);
                zdtUserUpdater.updateFromExcelRow(excelFilePath, counter);
                
                log.info("Updated ZDT_User_Data.json 'bmcopssupportagent' entry from Excel row " + counter);
            } catch (Exception e) {
                log.error("Error updating from Excel: " + e.getMessage() + ". Using existing JSON.");
            }
        } else {
            log.info("runCounter is 0, using existing 'bmcopssupportagent' data from JSON (no Excel update)");
        }
        
        // Always load user data from the fixed "bmcopssupportagent" key
        ReadJson readJson = new ReadJson();
        Map<Object, String> zdtUserData = readJson.getInputValuesMapIncludingLabelField(
            "ITSM" + File.separator + "ZDT", "ZDT_User_Data", userKey);
        String username = zdtUserData.get("UserName");
        String password = zdtUserData.get("Password");
        log.info("Using ZDT user: " + username + " (UserKey: " + userKey + ", runCounter: " + counter + ")");
        
        return new ZDTUserSetup(username, password, readJson, zdtUserData, userKey, counter);
    }
    
    /**
     * Inner class to hold ZDT user setup data
     */
    public static class ZDTUserSetup {
        private final String username;
        private final String password;
        private final ReadJson readJson;
        private final Map<Object, String> zdtUserData;
        private final String userKey;
        private final int counter;
        
        public ZDTUserSetup(String username, String password, ReadJson readJson, 
                           Map<Object, String> zdtUserData, String userKey, int counter) {
            this.username = username;
            this.password = password;
            this.readJson = readJson;
            this.zdtUserData = zdtUserData;
            this.userKey = userKey;
            this.counter = counter;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public ReadJson getReadJson() {
            return readJson;
        }
        
        public Map<Object, String> getZdtUserData() {
            return zdtUserData;
        }
        
        public String getUserKey() {
            return userKey;
        }
        
        public int getCounter() {
            return counter;
        }
    }
}

