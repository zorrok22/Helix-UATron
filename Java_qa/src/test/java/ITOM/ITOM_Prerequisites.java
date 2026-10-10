package ITOM;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Api.Events_API;
import util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ITOM_Prerequisites extends Launcher {

    public static final SolqaLogger log = new SolqaLogger(ITOM_Prerequisites.class.getName());

    public WebDriver adeDriver;
    public Portal_FeatureMethods portalFeatureMethods;
    public CommonWaits commonWaits;
    public Events_API events_api;

    public static final String SOLQA_CONSTANTS_FILE_PATH = "src/main/java/util/SolQAConstants.java";
    public static final String PROPERTIES_FILE_PATH = "properties.config";
    public static final String TOPOLOGY_SEARCH_ENDPOINT = "/service-management/api/v1.0/topology/search";
    public static final String AUTOMATION_KEY_NAME = "Automation Key";
    public static final int MAX_ALLOWED_KEYS = 2;

    public static final String XPATH_ACTIONS_BTN = "//*[contains(@class, 'my-keys')]//button[contains(. ,'Actions')]";
    public static final String XPATH_AUTOMATION_KEY = "//*[contains(@class, 'my-keys')]//span[text()='Automation Key']";

    // Excel data source support
    public int counter;
    public Map<Object, String> itomUserData;
    public String userKey = "aananda"; // Default user key

    @FindBy(xpath = "//button[@aria-label='Profile menu']")
    public WebElement profileMenu;

    @FindBy(xpath = "//button[contains(. ,'My profile')]")
    public WebElement myProfileBtn;

    @FindBy(xpath = "//button[contains(. ,'Delete')]")
    public WebElement deleteBtn;

    @FindBy(xpath = "//button[contains(. ,'Delete key')]")
    public WebElement deleteKeyBtn;

    @FindBy(xpath = "//button[contains(. ,'Add key')]")
    public WebElement addKeyBtn;

    @FindBy(xpath = "//adapt-rx-textfield[contains(. ,'API key name')]//input")
    public WebElement keyNameInput;

    @FindBy(xpath = "//adapt-rx-select[contains(. ,'Key expiry')]//button")
    public WebElement keyExpiryDropdown;

    @FindBy(xpath = "//button[@role='option' and contains(. ,'Never expires (not recommended)')]")
    public WebElement neverExpireOption;

    @FindBy(xpath = "//button[contains(. ,'Confirm')]")
    public WebElement confirmButton;

    @FindBy(xpath = "//button[contains(. ,'Copy all key details')]")
    public WebElement copyKeyDetailsBtn;

    @FindBy(xpath = "//adapt-rx-textarea[contains(. ,'Description')]//textarea")
    public WebElement descriptionInput;

    @FindBy(xpath = XPATH_AUTOMATION_KEY + "/ancestor::div[contains(@class, 'row')]//button[contains(. ,'Actions')]")
    public WebElement automationKeyActionsBtn;

    @FindBy(xpath = "//button[contains(. ,'Edit')]")
    public WebElement editBtn;

    @FindBy(xpath = "//*[@class='d-icon-cross_adapt']")
    public WebElement closeModalBtn;

    /**
     * Static utility method to set up ITOM user data based on run counter
     * This method delegates to UserUpdater.updateITOMUserDataFromExcel()
     * This method handles BOTH scenarios:
     * - When runCounter=0: Returns credentials from properties.config (ADE_USERNAME/ADE_PASSWORD)
     * - When runCounter>0: Updates JSON from Excel and returns credentials from JSON
     * This is the method other test classes should call to get user data.
     *
     * @param config Configuration map containing runCounter and admin credentials
     * @return Map containing UserName and Password
     */
    public static Map<Object, String> updateITOMUserDataFromExcel(Map<String, String> config) {
        // Delegate to UserUpdater's static method (logger parameter ignored, UserUpdater uses its own log4j logger)
        return UserUpdater.updateITOMUserDataFromExcel(config);
    }

    /**
     * Setup method to load user data based on runCounter
     * This runs before any test methods
     * Pattern follows ITSM approach - always uses "itomadminuser" key
     * - When runCounter=0: Reads credentials from properties.config (adminUserName/adminPassword)
     * - When runCounter>0: Reads credentials from Excel file (ITOM_User_Data.xlsx)
     */
    public void setupITOMUsers() {
        // Call the static method from UserUpdater to get user data (handles both runCounter=0 and >0)
        itomUserData = UserUpdater.updateITOMUserDataFromExcel(configProp);

        // Parse counter for internal use
        try {
            counter = Integer.parseInt(configProp.get("runCounter"));
        } catch (NumberFormatException e) {
            counter = 0;
        }

        // Always use "itomadminuser" as the key
        userKey = "itomadminuser";

        String username = itomUserData.get("UserName");
        log.info("Prerequisites will run with user: " + username + " (UserKey: " + userKey + ", runCounter: " + counter + ")");
    }

    /**
     * Step 1: Retrieve and store API keys in properties file
     * This must run FIRST to get ACCESS_KEY and SECRET_KEY
     */
    @Test(priority = -1, alwaysRun = true, description = "Update AccessKey, SecretKey, and TenantID in SolQAConstants get CI Nodes",
            groups = {"ITOM_BMCIT", "ITSMInsights", "Optimize", "Regression", "aiops", "bap", "intelligentautomation", "monitor", "otel", "sanity", "sanity_aif", "sanity_aiops", "sanity_intelligentautomation", "sanity_monitor", "snow"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "Pre-requisite1", Products = {Product.ADE_Platform})
    public void UpdateApiKeysInSolQAConstantsAndGetCINodes() {

        setupITOMUsers();
        String keyDetails = retrieveAndStoreApiKeys();
        Assert.assertNotNull(keyDetails, "Key details should not be null");

        // Extract and update only API keys and Tenant ID
        String accessKey = extractAccessKey(keyDetails);
        String secretKey = extractSecretKey(keyDetails);
        String tenantId = extractTenantId(keyDetails);

        SolQAConstants.API_ACCESS_KEY = accessKey;
        SolQAConstants.API_SECRET_KEY = secretKey;
        SolQAConstants.API_TENANT_ID = tenantId;

        log.info("API keys successfully stored and verified in SolQAConstants file");

        reloadConfiguration();
        try {
            // Verify constants were loaded correctly (API keys from SolQAConstants)
            Assert.assertNotNull(SolQAConstants.API_ACCESS_KEY, "API_ACCESS_KEY not found");
            Assert.assertNotNull(SolQAConstants.API_SECRET_KEY, "API_SECRET_KEY not found");
            Assert.assertFalse(SolQAConstants.API_ACCESS_KEY.contains("__accessKey__"), "API_ACCESS_KEY not properly updated");
            Assert.assertFalse(SolQAConstants.API_SECRET_KEY.contains("__secretKey__"), "API_SECRET_KEY not properly updated");

            log.info("Constants loaded successfully. ACCESS_KEY: " + SolQAConstants.API_ACCESS_KEY.substring(0, 10) + "...");

            List<String> ciNodes = getCINodes();
            Assert.assertFalse(ciNodes.isEmpty(), "CI nodes list should not be empty");

             // Extract CI node values
             String clusterCi = ciNodes.size() > 0 ? ciNodes.get(0) : null;
             String paAgent = ciNodes.size() > 1 ? ciNodes.get(1) : null;
 
             // Update the in-memory constants
             SolQAConstants.CLUSTER_CI = clusterCi;
             SolQAConstants.PA_AGENT = paAgent;

               // Also update CI node values in properties file (keeping existing logic)
            updatePropertiesFileWithCINodes(clusterCi, paAgent);

            log.info("CI nodes successfully stored in properties file and SolQAConstants");
        } catch (Exception e) {
            log.error("Failed to update CI nodes: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }


    public String retrieveAndStoreApiKeys() {
        try {
            initializeDriver();
            loginToPortal();
            navigateToProfileAndKeys();

            int keyCount = getKeyCount();
            log.info("Current number of keys: " + keyCount);

            boolean automationKeyExists = isAutomationKeyPresent();
            log.info("Automation Key exists: " + automationKeyExists);

            handleKeyManagement(keyCount, automationKeyExists);

            return getKeyDetailsFromUI();
        } catch (Exception e) {
            log.error("Error retrieving API keys: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve API keys", e);
        }
    }

    public List<String> getCINodes() {
        try {
            events_api = new pages.ITOM.Api.Events_API();

            org.json.JSONObject requestBody = new org.json.JSONObject();
            requestBody.put("query", "SEARCH Host WHERE (type HAS SUBSTRING 'UNIX Server') ORDER BY name DESC SHOW #id as 'id', name");
            requestBody.put("limit", 2);

            Response response = RestAssured.given()
                    .baseUri(configProp.get("ADE_PLATFORM_URL"))
                    .header("Authorization", "Bearer " + events_api.getJwtToken(
                            SolQAConstants.API_ACCESS_KEY,
                            SolQAConstants.API_SECRET_KEY
                    ))
                    .contentType(ContentType.JSON)
                    .body(requestBody.toString())
                    .post(TOPOLOGY_SEARCH_ENDPOINT);

            Assert.assertEquals(response.getStatusCode(), 200, "Failed to search CI nodes. Response: " + response.getBody().asString());

            return extractHostNamesFromResponse(response);
        } catch (Exception e) {
            log.error("Error retrieving CI nodes: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve CI nodes", e);
        }
    }

    public void initializeDriver() {
        log.info("Launching ITOM application...");
        adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        PageFactory.initElements(adeDriver, this);
        commonWaits = new CommonWaits();
        portalFeatureMethods = new Portal_FeatureMethods();
    }

    public void loginToPortal() {
        log.info("Logging in to the portal...");

        // Use credentials from Excel/JSON data source
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");

        log.info("Logging in with user: " + username);
        login(username, password, SolQAConstants.appITOM);

        log.info("Verifying login...");
        portalFeatureMethods.verifyLogin();
    }

    public void navigateToProfileAndKeys() {
        log.info("Navigating to profile menu...");
        commonWaits.clickWithoutInterception(profileMenu);
        commonWaits.clickWithoutInterception(myProfileBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public int getKeyCount() {
        return getDriver().findElements(By.xpath(XPATH_ACTIONS_BTN)).size();
    }

    public boolean isAutomationKeyPresent() {
        try {
            return !getDriver().findElements(By.xpath(XPATH_AUTOMATION_KEY)).isEmpty();
        } catch (Exception e) {
            log.warn("Error checking for Automation Key: " + e.getMessage());
            return false;
        }
    }

    public void handleKeyManagement(int keyCount, boolean automationKeyExists) {
        if (keyCount >= MAX_ALLOWED_KEYS && !automationKeyExists) {
            log.info("Deleting a key to create Automation Key...");
            deleteAKey();
            createAutomationKey();
        } else if (automationKeyExists) {
            log.info("Opening Automation Key for edit...");
            openAutomationKeyForEdit();
        } else {
            log.info("Creating Automation Key...");
            createAutomationKey();
        }
    }

    public void openAutomationKeyForEdit() {
        commonWaits.clickWithoutInterception(automationKeyActionsBtn);
        commonWaits.clickWithoutInterception(editBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForSometime(1000);
    }

    public void deleteAKey() {
        commonWaits.clickWithoutInterception(By.xpath(XPATH_ACTIONS_BTN));
        commonWaits.clickWithoutInterception(deleteBtn);
        commonWaits.clickWithoutInterception(deleteKeyBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

        int remainingKeys = getKeyCount();
        Assert.assertTrue(remainingKeys < MAX_ALLOWED_KEYS, "Key deletion failed");
        log.info("Key deleted successfully. Remaining keys: " + remainingKeys);
    }

    public void createAutomationKey() {
        commonWaits.clickWithoutInterception(addKeyBtn);
        commonWaits.waitForElementVisible(keyNameInput).sendKeys(AUTOMATION_KEY_NAME);
        commonWaits.clickWithoutInterception(keyExpiryDropdown);
        commonWaits.clickWithoutInterception(neverExpireOption);
        commonWaits.clickWithoutInterception(confirmButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        log.info("Automation Key created successfully");
    }

    public String getKeyDetailsFromUI() {
        try {
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.waitForElementVisible(descriptionInput);
            String existingDescription = descriptionInput.getAttribute("value");
            if (existingDescription != null && !existingDescription.trim().isEmpty()) {
                log.info("Using existing data from Description field");
                return existingDescription;
            } else {
                log.info("Storing key details in Description field");
                commonWaits.clickWithoutInterception(copyKeyDetailsBtn);
                descriptionInput.sendKeys(Keys.CONTROL, "v");
                String keyDetails = descriptionInput.getAttribute("value");
                log.info("Key details retrieved: " + keyDetails);
                commonWaits.waitForSometime(commonWaits.moreDelay);
                commonWaits.clickWithoutInterception(closeModalBtn);
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
                return keyDetails;
            }
        } catch (Exception e) {
            log.error("Error retrieving key details from UI: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get key details", e);
        }
    }

    public List<String> extractHostNamesFromResponse(Response response) {
        List<String> hostNames = new ArrayList<>();
        try {
            List<?> rawRows = response.jsonPath().getList("[0].results");

            if (rawRows != null) {
                for (Object rowObj : rawRows) {
                    if (rowObj instanceof List<?>) {
                        List<?> row = (List<?>) rowObj;
                        if (row.size() >= 2 && row.get(1) != null) {
                            hostNames.add(row.get(1).toString());
                        }
                    }
                }
            }
            log.info("Hosts retrieved: " + hostNames);
        } catch (Exception e) {
            log.error("Error extracting host names: " + e.getMessage(), e);
        }
        return hostNames;
    }

    public String extractAccessKey(String keyDetails) {
        try {
            int start = keyDetails.indexOf("Key details:") + "Key details:".length();
            int end = keyDetails.indexOf("::");
            return keyDetails.substring(start, end).trim();
        } catch (Exception e) {
            log.error("Error extracting access key: " + e.getMessage());
            return "";
        }
    }

    public String extractSecretKey(String keyDetails) {
        try {
            int start = keyDetails.indexOf("::") + "::".length();
            int end = keyDetails.indexOf(",");
            return keyDetails.substring(start, end).trim();
        } catch (Exception e) {
            log.error("Error extracting secret key: " + e.getMessage());
            return "";
        }
    }

    public String extractTenantId(String keyDetails) {
        try {
            String tenantId = keyDetails.substring(keyDetails.indexOf(",") + 1)
                    .trim()
                    .replaceAll("\\s+", "");

            if (tenantId.startsWith("TenantID:")) {
                tenantId = tenantId.substring("TenantID:".length()).trim();
            }
            return tenantId;
        } catch (Exception e) {
            log.error("Error extracting tenant ID: " + e.getMessage());
            return "";
        }
    }

    /**
     * Update properties file with CI nodes only
     */
    public void updatePropertiesFileWithCINodes(String clusterCi, String paAgent) {
        try {
            File propertiesFile = new File(PROPERTIES_FILE_PATH);
            if (!propertiesFile.exists()) {
                log.error("Properties file not found: " + PROPERTIES_FILE_PATH);
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(PROPERTIES_FILE_PATH)));

            if (clusterCi != null) {
                content = content.replace("__CLUSTER_CI__", clusterCi);
            }
            if (paAgent != null) {
                content = content.replace("__PA_AGENT__", paAgent);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile))) {
                writer.write(content);
            }

            log.info("Successfully updated CI nodes in properties file:");
            if (clusterCi != null) log.info("  CLUSTER_CI: " + clusterCi);
            if (paAgent != null) log.info("  PA_AGENT: " + paAgent);

        } catch (IOException e) {
            log.error("Error updating properties file with CI nodes: " + e.getMessage(), e);
        }
    }

    /**
     * Reload configuration from properties file after updates
     */
    public void reloadConfiguration() {
        try {
            log.info("Reloading configuration from properties file: " + PROPERTIES_FILE_PATH);

            // Read the properties file
            java.util.Properties props = new java.util.Properties();
            try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE_PATH)) {
                props.load(fis);
            }

            // Clear existing configProp map
            if (configProp != null) {
                configProp.clear();

                // Copy all properties to configProp map
                for (String key : props.stringPropertyNames()) {
                    configProp.put(key, props.getProperty(key));
                }
            }

            Assert.assertNotNull(configProp);
            log.info("Configuration reloaded successfully. Total properties: " + configProp.size());

            // Wait for stability
            Thread.sleep(1000);

        } catch (IOException e) {
            log.error("Error reloading configuration: " + e.getMessage(), e);
            throw new RuntimeException("Failed to reload configuration", e);
        } catch (InterruptedException e) {
            log.error("Thread interrupted during reload: " + e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Configuration reload interrupted", e);
        }
    }

    /**
     * Helper method to update or add a constant in SolQAConstants file
     */
    public String updateOrAddConstant(String content, String constantName, String value) {
        String searchPattern = "public static String " + constantName + " = \"[^\"]*\";";
        String replacement = "public static String " + constantName + " = \"" + value + "\";";

        if (content.contains("public static String " + constantName + " =")) {
            // Update existing constant
            return content.replaceFirst(searchPattern, replacement);
        } else {
            // Constant doesn't exist, it should have been added already in the file
            // Just update the default value
            return content.replaceFirst(
                    "public static String " + constantName + " = \"[^\"]*\";",
                    replacement
            );
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (adeDriver != null) {
            try {
                adeDriver.quit();
                log.info("Driver closed successfully");
            } catch (Exception e) {
                log.warn("Error closing driver: " + e.getMessage());
            }
        }
    }

}