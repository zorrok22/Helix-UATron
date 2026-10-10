package util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.log4j.Logger;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Purpose : This class is created to modify userjson file runtime with an external xls / csv / json
 * @author akarhadk
 * Date: 2nd Sept 2025
 *
 */
public class UserUpdater {
    private static final Logger logger = Logger.getLogger(UserUpdater.class);
    static String JSON_FILE_PATH = "";
    private final ObjectMapper objectMapper;
    private JsonNode rootNode;

    public UserUpdater() {
        this.objectMapper = new ObjectMapper();
       // loadJsonFile();
    }

    public void loadJsonFile(String PATH) {
        try {
        	JSON_FILE_PATH=PATH;
            String jsonContent = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            rootNode = objectMapper.readTree(jsonContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ITSMUsers.json file", e);
        }
    }

    public void updateFromExcel(String excelFilePath) {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                processUserRow(row);
            }

            // Save changes back to JSON file
            saveJsonFile();

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file", e);
        }
    }

    public void updateFromExcelRow(String excelFilePath, int rowIndex) {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // rowIndex is the run counter (1-based data row)
            // Excel row 0 is header, so runCounter=1 should read Excel row 1 (first data row)
            int excelRowIndex = rowIndex; // No need to subtract 1, as row 0 is the header
            Row row = sheet.getRow(excelRowIndex);
            if (row != null) {
                processUserRow(row);
                // Save changes back to JSON file
                saveJsonFile();
                System.out.println("Successfully processed runCounter " + rowIndex + " (Excel row " + excelRowIndex + ")");
            } else {
                System.out.println("Row " + rowIndex + " (Excel row " + excelRowIndex + ") not found in Excel file. Available rows: 0 to " + sheet.getLastRowNum());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file row " + rowIndex, e);
        }
    }
    
    /**
     * Updates multiple user keys from a single Excel row
     * This method is designed for SmartIT scenarios where one row contains data for multiple users
     * @param excelFilePath Path to the Excel file
     * @param rowIndex The row index to process (1-based)
     * @param userKeys Array of user keys to update (e.g., ["bmcopssupportagent", "bmcopsenduser"])
     */
    public void updateMultipleUsersFromExcelRow(String excelFilePath, int rowIndex, String[] userKeys) {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // rowIndex is the run counter (1-based data row)
            // Excel row 0 is header, so runCounter=1 should read Excel row 1 (first data row)
            int excelRowIndex = rowIndex; // No need to subtract 1, as row 0 is the header
            Row row = sheet.getRow(excelRowIndex);
            if (row != null) {
                System.out.println("Processing multiple users from runCounter " + rowIndex + " (Excel row " + excelRowIndex + ")");
                System.out.println("User keys to update: " + String.join(", ", userKeys));
                
                // Process each user key
                for (String userKey : userKeys) {
                    processMultipleUserRow(row, userKey);
                }
                
                // Save changes back to JSON file
                saveJsonFile();
                System.out.println("Successfully processed multiple users from runCounter " + rowIndex);
            } else {
                System.out.println("Row " + rowIndex + " (Excel row " + excelRowIndex + ") not found in Excel file. Available rows: 0 to " + sheet.getLastRowNum());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file row " + rowIndex + " for multiple users", e);
        }
    }
    
    /**
     * Convenience method specifically for SmartIT scenarios with bmcopssupportagent and bmcopsenduser
     * @param excelFilePath Path to the Excel file
     * @param rowIndex The row index to process (1-based)
     */
    public void updateSmartITUsersFromExcelRow(String excelFilePath, int rowIndex) {
        String[] smartITUserKeys = {"bmcopssupportagent", "bmcopsenduser"};
        updateMultipleUsersFromExcelRow(excelFilePath, rowIndex, smartITUserKeys);
    }
    
    /**
     * Updates SmartIT users with multiple usernames per user key
     * This method handles the specific case where each user key has multiple usernames
     * @param excelFilePath Path to the Excel file
     * @param rowIndex The row index to process (1-based

     */
    public void updateSmartITMultipleUsersFromExcelRow(String excelFilePath, int rowIndex) {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // rowIndex is the run counter (1-based data row)
            // Excel row 0 is header, so runCounter=1 should read Excel row 1 (first data row)
            int excelRowIndex = rowIndex; // No need to subtract 1, as row 0 is the header
            Row row = sheet.getRow(excelRowIndex);
            if (row != null) {
                System.out.println("Processing SmartIT multiple users from runCounter " + rowIndex + " (Excel row " + excelRowIndex + ")");
                
                // Get the user key from column B (index 1)
                String userKey = getCellValueAsString(row.getCell(1));
                if (userKey != null && !userKey.trim().isEmpty()) {
                    processSmartITMultipleUserRow(row, userKey.trim());
                } else {
                    System.out.println("UserKey is null or empty in row " + rowIndex);
                }
                
                // Save changes back to JSON file
                saveJsonFile();
                System.out.println("Successfully processed SmartIT multiple users from row " + rowIndex);
            } else {
                System.out.println("Row " + rowIndex + " (Excel row " + excelRowIndex + ") not found in Excel file. Available rows: 0 to " + sheet.getLastRowNum());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file row " + rowIndex + " for SmartIT multiple users", e);
        }
    }
    
    /**
     * Updates SmartIT users with runtime structure (UserKey1/UserKey2 with Label1/Value1 and Label2/Value2)
     * This method handles the flexible structure where each row contains two user keys with their respective label/value pairs
     * @param excelFilePath Path to the Excel file
     * @param rowIndex The row index to process (1-based)
     */
    public void updateMultiUserRuntimeUsersFromExcelRow(String excelFilePath, int rowIndex) {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // rowIndex is the run counter (1-based data row)
            // Excel row 0 is header, so runCounter=1 should read Excel row 1 (first data row)
            int excelRowIndex = rowIndex; // No need to subtract 1, as row 0 is the header
            Row row = sheet.getRow(excelRowIndex);
            if (row != null) {
                System.out.println("Processing SmartIT runtime users from runCounter " + rowIndex + " (Excel row " + excelRowIndex + ")");
                
                // Process UserKey1 with Value1 and Value2
                String userKey1 = getCellValueAsString(row.getCell(1)); // Column B
                System.out.println("UserKey1 from Column B: '" + userKey1 + "'");
                if (userKey1 != null && !userKey1.trim().isEmpty()) {
                    processSmartITRuntimeUserRowCorrected(row, userKey1.trim(), 2, 3, 4, 5); // Columns C, D, E, F
                } else {
                    System.out.println("UserKey1 is null or empty in row " + rowIndex);
                }
                
                // Process UserKey2 with Value1 and Value2
                String userKey2 = getCellValueAsString(row.getCell(6)); // Column G
                System.out.println("UserKey2 from Column G: '" + userKey2 + "'");
                if (userKey2 != null && !userKey2.trim().isEmpty()) {
                    processSmartITRuntimeUserRowCorrected(row, userKey2.trim(), 7, 8, 9, 10); // Columns H, I, J, K
                } else {
                    System.out.println("UserKey2 is null or empty in row " + rowIndex);
                }
                
                // Save changes back to JSON file
                saveJsonFile();
                System.out.println("Successfully processed SmartIT runtime users from row " + rowIndex);
            } else {
                System.out.println("Row " + rowIndex + " (Excel row " + excelRowIndex + ") not found in Excel file. Available rows: 0 to " + sheet.getLastRowNum());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file row " + rowIndex + " for SmartIT runtime users", e);
        }
    }

    public void updateFromCSV(String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip header row
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                processUserData(values);
            }

            // Save changes back to JSON file
            saveJsonFile();

        } catch (IOException e) {
            throw new RuntimeException("Failed to process CSV file", e);
        }
    }

    private void processUserRow(Row row) {
        String userKey = getCellValueAsString(row.getCell(0));
        if (userKey == null || userKey.trim().isEmpty()) {
            System.out.println("UserKey is null or empty in row " + row.getRowNum());
            return;
        }

        System.out.println("Processing user: " + userKey);
        ArrayNode userArray = ((ObjectNode) rootNode).putArray(userKey);
        
        // Process each column pair (Label, Value)
        for (int i = 1; i < row.getLastCellNum(); i += 2) {
            String label = getCellValueAsString(row.getCell(i));
            String value = getCellValueAsString(row.getCell(i + 1));
            
            if (label != null && value != null && !label.trim().isEmpty() && !value.trim().isEmpty()) {
                ObjectNode fieldNode = userArray.addObject();
                // Normalize field names to prevent inconsistencies
                String normalizedLabel = normalizeFieldName(label.trim());
                fieldNode.put("Label", normalizedLabel);
                fieldNode.put("Value", value.trim());
                System.out.println("  Added: " + normalizedLabel + " = " + value.trim());
            } else {
                System.out.println("  Skipped empty or null field: Label=" + label + ", Value=" + value);
            }
        }
    }
    
    /**
     * Processes a row for a specific user key (for multiple user scenarios)
     * @param row The Excel row to process
     * @param userKey The specific user key to update
     */
    private void processMultipleUserRow(Row row, String userKey) {
        if (userKey == null || userKey.trim().isEmpty()) {
            System.out.println("UserKey is null or empty for user: " + userKey);
            return;
        }

        System.out.println("Processing user: " + userKey);
        ArrayNode userArray = ((ObjectNode) rootNode).putArray(userKey);
        
        // Process each column pair (Label, Value) starting from column 1
        // Skip the first column as it might contain a different identifier
        for (int i = 1; i < row.getLastCellNum(); i += 2) {
            String label = getCellValueAsString(row.getCell(i));
            String value = getCellValueAsString(row.getCell(i + 1));
            
            if (label != null && value != null && !label.trim().isEmpty() && !value.trim().isEmpty()) {
                ObjectNode fieldNode = userArray.addObject();
                // Normalize field names to prevent inconsistencies
                String normalizedLabel = normalizeFieldName(label.trim());
                fieldNode.put("Label", normalizedLabel);
                fieldNode.put("Value", value.trim());
                System.out.println("  Added for " + userKey + ": " + normalizedLabel + " = " + value.trim());
            } else {
                System.out.println("  Skipped empty or null field for " + userKey + ": Label=" + label + ", Value=" + value);
            }
        }
    }
    
    /**
     * Processes a row for SmartIT scenarios with multiple usernames per user key
     * This method handles the consistent structure where each row contains one user key
     * with multiple username/password pairs
     * @param row The Excel row to process
     * @param userKey The specific user key to update
     */
    private void processSmartITMultipleUserRow(Row row, String userKey) {
        if (userKey == null || userKey.trim().isEmpty()) {
            System.out.println("UserKey is null or empty for user: " + userKey);
            return;
        }

        System.out.println("Processing SmartIT user: " + userKey);
        ArrayNode userArray = ((ObjectNode) rootNode).putArray(userKey);
        
        // Process Label-Value pairs starting from column C (index 2)
        // Column A (index 0) = Row ID, Column B (index 1) = UserKey
        for (int i = 2; i < row.getLastCellNum(); i += 2) {
            String label = getCellValueAsString(row.getCell(i));
            String value = getCellValueAsString(row.getCell(i + 1));
            
            if (label != null && value != null && !label.trim().isEmpty() && !value.trim().isEmpty()) {
                ObjectNode fieldNode = userArray.addObject();
                // Normalize field names to prevent inconsistencies
                String normalizedLabel = normalizeFieldName(label.trim());
                fieldNode.put("Label", normalizedLabel);
                fieldNode.put("Value", value.trim());
                System.out.println("  Added for " + userKey + ": " + normalizedLabel + " = " + value.trim());
            } else {
                System.out.println("  Skipped empty or null field for " + userKey + ": Label=" + label + ", Value=" + value);
            }
        }
    }
    
    /**
     * Processes a row for SmartIT runtime scenarios with specific label/value pairs
     * This method handles the runtime structure where each user key has specific label/value pairs
     * @param row The Excel row to process
     * @param userKey The specific user key to update
     * @param label1Col Column index for first label
     * @param value1Col Column index for first value
     * @param label2Col Column index for second label
     * @param value2Col Column index for second value
     */
    private void processSmartITRuntimeUserRow(Row row, String userKey, int label1Col, int value1Col, int label2Col, int value2Col) {
        if (userKey == null || userKey.trim().isEmpty()) {
            System.out.println("UserKey is null or empty for user: " + userKey);
            return;
        }

        System.out.println("Processing SmartIT runtime user: " + userKey);
        ArrayNode userArray = ((ObjectNode) rootNode).putArray(userKey);
        
        // Process Label1/Value1 pair
        String label1 = getCellValueAsString(row.getCell(label1Col));
        String value1 = getCellValueAsString(row.getCell(value1Col));
        
        if (label1 != null && value1 != null && !label1.trim().isEmpty() && !value1.trim().isEmpty()) {
            ObjectNode fieldNode1 = userArray.addObject();
            String normalizedLabel1 = normalizeFieldName(label1.trim());
            fieldNode1.put("Label", normalizedLabel1);
            fieldNode1.put("Value", value1.trim());
            System.out.println("  Added for " + userKey + ": " + normalizedLabel1 + " = " + value1.trim());
        } else {
            System.out.println("  Skipped empty or null field for " + userKey + ": Label1=" + label1 + ", Value1=" + value1);
        }
        
        // Process Label2/Value2 pair
        String label2 = getCellValueAsString(row.getCell(label2Col));
        String value2 = getCellValueAsString(row.getCell(value2Col));
        
        if (label2 != null && value2 != null && !label2.trim().isEmpty() && !value2.trim().isEmpty()) {
            ObjectNode fieldNode2 = userArray.addObject();
            String normalizedLabel2 = normalizeFieldName(label2.trim());
            fieldNode2.put("Label", normalizedLabel2);
            fieldNode2.put("Value", value2.trim());
            System.out.println("  Added for " + userKey + ": " + normalizedLabel2 + " = " + value2.trim());
        } else {
            System.out.println("  Skipped empty or null field for " + userKey + ": Label2=" + label2 + ", Value2=" + value2);
        }
    }
    
    /**
     * Processes a row for SmartIT runtime scenarios with corrected UserKey1/Value1 and UserKey2/Value2 mapping
     * This method correctly maps UserKey1 to Value1 and UserKey2 to Value2
     * @param row The Excel row to process
     * @param userKey The specific user key to update
     * @param label1Col Column index for first label
     * @param value1Col Column index for first value
     * @param label2Col Column index for second label
     * @param value2Col Column index for second value
     */
    private void processSmartITRuntimeUserRowCorrected(Row row, String userKey, int label1Col, int value1Col, int label2Col, int value2Col) {
        if (userKey == null || userKey.trim().isEmpty()) {
            System.out.println("UserKey is null or empty for user: " + userKey);
            return;
        }

        System.out.println("Processing SmartIT runtime user (corrected): " + userKey);
        
        // Remove existing array if it exists and create a new one
        if (rootNode.has(userKey)) {
            ((ObjectNode) rootNode).remove(userKey);
            System.out.println("  Removed existing array for userKey: " + userKey);
        }
        
        ArrayNode userArray = ((ObjectNode) rootNode).putArray(userKey);
        
        // Get the label from Label1 column (should be "UserName")
        String label1 = getCellValueAsString(row.getCell(label1Col));
        System.out.println("  Label1 from column " + label1Col + ": '" + label1 + "'");
        if (label1 == null || label1.trim().isEmpty()) {
            label1 = "UserName"; // Default label if not found
            System.out.println("  Using default Label1: '" + label1 + "'");
        }
        
        // Get the value from Value1 column (this is the actual value for UserKey1)
        String value1 = getCellValueAsString(row.getCell(value1Col));
        System.out.println("  Value1 from column " + value1Col + ": '" + value1 + "'");
        
        if (value1 != null && !value1.trim().isEmpty()) {
            ObjectNode fieldNode1 = userArray.addObject();
            String normalizedLabel1 = normalizeFieldName(label1.trim());
            fieldNode1.put("Label", normalizedLabel1);
            fieldNode1.put("Value", value1.trim());
            System.out.println("  Added for " + userKey + ": " + normalizedLabel1 + " = " + value1.trim());
        } else {
            System.out.println("  Skipped empty or null value for " + userKey + ": Value1=" + value1);
        }
        
        // Get the label from Label2 column (should be "Password")
        String label2 = getCellValueAsString(row.getCell(label2Col));
        System.out.println("  Label2 from column " + label2Col + ": '" + label2 + "'");
        if (label2 == null || label2.trim().isEmpty()) {
            label2 = "Password"; // Default label if not found
            System.out.println("  Using default Label2: '" + label2 + "'");
        }
        
        // Get the value from Value2 column (this is the actual value for UserKey1)
        String value2 = getCellValueAsString(row.getCell(value2Col));
        System.out.println("  Value2 from column " + value2Col + ": '" + value2 + "'");
        
        if (value2 != null && !value2.trim().isEmpty()) {
            ObjectNode fieldNode2 = userArray.addObject();
            String normalizedLabel2 = normalizeFieldName(label2.trim());
            fieldNode2.put("Label", normalizedLabel2);
            fieldNode2.put("Value", value2.trim());
            System.out.println("  Added for " + userKey + ": " + normalizedLabel2 + " = " + value2.trim());
        } else {
            System.out.println("  Skipped empty or null value for " + userKey + ": Value2=" + value2);
        }
    }
    


    private void processUserData(String[] values) {
        if (values.length < 3) { // At least userKey and one Label-Value pair
            return;
        }

        String userKey = values[0].trim();
        if (userKey.isEmpty()) {
            return;
        }

        ArrayNode userArray = ((ObjectNode) rootNode).putArray(userKey);
        
        // Process each pair of values (Label, Value)
        for (int i = 1; i < values.length; i += 2) {
            if (i + 1 < values.length) {
                String label = values[i].trim();
                String value = values[i + 1].trim();
                
                if (!label.isEmpty() && !value.isEmpty()) {
                    ObjectNode fieldNode = userArray.addObject();
                    fieldNode.put("Label", label);
                    fieldNode.put("Value", value);
                }
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    /**
     * Normalizes field names to ensure consistency across the application
     * @param fieldName The original field name
     * @return The normalized field name
     */
    private String normalizeFieldName(String fieldName) {
        if (fieldName == null) return null;
        
        String normalized = fieldName.trim();
        
        // Handle common variations of username field
        if (normalized.equalsIgnoreCase("username") || 
            normalized.equalsIgnoreCase("user name") ||
            normalized.equalsIgnoreCase("user_name")) {
            return "UserName"; // Standardize to UserName
        }
        
        // Handle common variations of password field
        if (normalized.equalsIgnoreCase("password") || 
            normalized.equalsIgnoreCase("pwd") ||
            normalized.equalsIgnoreCase("pass")) {
            return "Password"; // Standardize to Password
        }
        
        // For other fields, capitalize first letter of each word
        return normalized;
    }

    private void saveJsonFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(JSON_FILE_PATH), rootNode);
            System.out.println("Successfully saved JSON file to: " + JSON_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ITSMUsers.json file", e);
        }
    }

    /**
     * Static utility method to setup ITOM user data based on run counter
     * This method handles BOTH scenarios:
     * - When runCounter=0: Returns credentials from properties.config (ADE_USERNAME/ADE_PASSWORD)
     * - When runCounter>0: Updates JSON from Excel and returns credentials from JSON
     * 
     * This is the ONLY method other test classes need to call to get user data.
     * No @BeforeTest needed in other classes for user data setup.
     * 
     * @param config Configuration map containing runCounter and admin credentials
     * @return Map containing UserName and Password
     */
    public static Map<Object, String> updateITOMUserDataFromExcel(Map<String, String> config) {
        int counter = 0;
        String userKey = "itomadminuser"; // Always use this key (ITSM pattern)
        
        // Parse runCounter with error handling
        String runCounterStr = config.get("runCounter");
        try {
            counter = Integer.parseInt(runCounterStr);
            logger.info("Parsed runCounter: " + counter);
        } catch (NumberFormatException e) {
            logger.warn("Invalid runCounter value '" + runCounterStr + "'. Defaulting to 0");
            counter = 0;
        }
        
        Map<Object, String> userData;
        
        if (counter == 0) {
            // When runCounter=0, read directly from properties.config
            logger.info("runCounter is 0, attempting to read credentials from properties.config");
            
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            
            // Validate credentials exist and are not placeholder values
            boolean validCredentials = false;
            if (username != null && password != null) {
                username = username.trim();
                password = password.trim();
                
                if (!username.isEmpty() && !password.isEmpty() && 
                    !username.contains("__") && !password.contains("__")) {
                    validCredentials = true;
                }
            }
            
            if (validCredentials) {
                // Create a map with credentials from properties.config
                userData = new java.util.HashMap<>();
                userData.put("UserName", username);
                userData.put("Password", password);
                logger.info("Successfully loaded credentials from properties.config: " + username);
            } else {
                // Fallback logic disabled when runCounter=0
                logger.error("properties.config has missing or placeholder values for ADE_USERNAME/ADE_PASSWORD");
                logger.error("When runCounter=0, valid credentials MUST be provided in properties.config");
                logger.error("Found ADE_USERNAME: " + (username != null ? username : "null"));
                logger.error("Found ADE_PASSWORD: " + (password != null ? "***" : "null"));
                throw new RuntimeException("No valid credentials found in properties.config. Please set ADE_USERNAME and ADE_PASSWORD with non-placeholder values.");
            }
            
        } else {
            // When runCounter>0, update JSON from Excel and then read it
            logger.info("runCounter is " + counter + ", reading credentials from Excel");
            
            try {
                // Update JSON from Excel
                String filename = "ITOM_User_Data";
                UserUpdater itomUserUpdater = new UserUpdater();
                String excelFilePath = System.getProperty("user.dir") + File.separator + 
                                       "resources" + File.separator + "ITOM" + File.separator + 
                                       filename + ".xlsx";
                String jsonFilePath = System.getProperty("user.dir") + File.separator + 
                                      "resources" + File.separator + "ITOM" + File.separator + 
                                      filename + ".json";
                
                logger.info("Excel file path: " + excelFilePath);
                logger.info("JSON file path: " + jsonFilePath);
                
                // Load and update JSON from Excel row
                itomUserUpdater.loadJsonFile(jsonFilePath);
                itomUserUpdater.updateFromExcelRow(excelFilePath, counter);
                logger.info("Updated ITOM_User_Data.json 'itomadminuser' entry from Excel row " + counter);
                
                // Load user data from JSON (which was just updated from Excel)
                ReadJson readJson = new ReadJson();
                userData = readJson.getInputValuesMapIncludingLabelField("ITOM", "ITOM_User_Data", userKey);
            } catch (Exception e) {
                logger.error("Failed to load credentials from Excel: " + e.getMessage(), e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Failed to setup ITOM users from Excel for runCounter: " + counter, e);
            }
        }
        
        String username = userData.get("UserName");
        logger.info("ITOM user setup complete: " + username + " (UserKey: " + userKey + ", runCounter: " + counter + ")");
        
        return userData;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java UserUpdater <file_path> <file_type> [row_index] [user_keys]");
            System.out.println("file_type should be either 'excel', 'csv', 'excel-row', 'excel-multiple-users', 'smartit', 'smartit-multiple', or 'smartit-runtime'");
            System.out.println("For 'excel-row': provide row_index (1-based)");
            System.out.println("For 'excel-multiple-users': provide row_index and user_keys (comma-separated)");
            System.out.println("For 'smartit': provide row_index (1-based) - single username per user key");
            System.out.println("For 'smartit-multiple': provide row_index (1-based) - multiple usernames per user key");
            System.out.println("For 'smartit-runtime': provide row_index (1-based) - UserKey1/UserKey2 with Label1/Value1 and Label2/Value2");
            System.out.println("Examples:");
            System.out.println("  java UserUpdater BWFUsers.xlsx excel");
            System.out.println("  java UserUpdater BWFUsers.xlsx excel-row 3");
            System.out.println("  java UserUpdater SmartITUsers.xlsx excel-multiple-users 2 bmcopssupportagent,bmcopsenduser");
            System.out.println("  java UserUpdater SmartITUsers.xlsx smartit 2");
            System.out.println("  java UserUpdater SmartITUsers.xlsx smartit-multiple 2");
            System.out.println("  java UserUpdater SmartITUsers.xlsx smartit-runtime 2");
            return;
        }

        String filePath = args[0];
        String fileType = args[1].toLowerCase();

        UserUpdater updater = new UserUpdater();

        try {
            switch (fileType) {
                case "excel":
                    updater.updateFromExcel(filePath);
                    break;
                case "csv":
                    updater.updateFromCSV(filePath);
                    break;
                case "excel-row":
                    if (args.length < 3) {
                        System.out.println("For 'excel-row', please provide row_index");
                        return;
                    }
                    int rowIndex = Integer.parseInt(args[2]);
                    updater.updateFromExcelRow(filePath, rowIndex);
                    break;
                case "excel-multiple-users":
                    if (args.length < 4) {
                        System.out.println("For 'excel-multiple-users', please provide row_index and user_keys");
                        return;
                    }
                    int multiRowIndex = Integer.parseInt(args[2]);
                    String[] userKeys = args[3].split(",");
                    updater.updateMultipleUsersFromExcelRow(filePath, multiRowIndex, userKeys);
                    break;
                case "smartit":
                    if (args.length < 3) {
                        System.out.println("For 'smartit', please provide row_index");
                        return;
                    }
                    int smartITRowIndex = Integer.parseInt(args[2]);
                    updater.updateSmartITUsersFromExcelRow(filePath, smartITRowIndex);
                    break;
                case "smartit-multiple":
                    if (args.length < 3) {
                        System.out.println("For 'smartit-multiple', please provide row_index");
                        return;
                    }
                    int smartITMultipleRowIndex = Integer.parseInt(args[2]);
                    updater.updateSmartITMultipleUsersFromExcelRow(filePath, smartITMultipleRowIndex);
                    break;
                case "smartit-runtime":
                    if (args.length < 3) {
                        System.out.println("For 'smartit-runtime', please provide row_index");
                        return;
                    }
                    int smartITRuntimeRowIndex = Integer.parseInt(args[2]);
                    updater.updateMultiUserRuntimeUsersFromExcelRow(filePath, smartITRuntimeRowIndex);
                    break;
                default:
                    System.out.println("Invalid file type. Use 'excel', 'csv', 'excel-row', 'excel-multiple-users', 'smartit', 'smartit-multiple', or 'smartit-runtime'");
                    return;
            }
            System.out.println("Successfully updated users JSON file");
        } catch (Exception e) {
            System.err.println("Error updating users: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 