package pages.ITOM.Multiservice;

import framework.BasePage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;

public class ServiceMonitoringPage extends BasePage {

    public static SolqaLogger log = new SolqaLogger(ServiceMonitoringPage.class.getName());
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    CommonWaits commonWaits;

    public String jwtToken;
    public final String BASE_URL = config.get("ADE_PLATFORM_URL");
    public final String ACCESS_KEY = SolQAConstants.API_ACCESS_KEY; // Or use SolQAConstants.API_ACCESS_KEY
    public final String ACCESS_SECRET_KEY = SolQAConstants.API_SECRET_KEY; // Or use SolQAConstants.API_SECRET_KEY
    public static final String currentDir = System.getProperty("user.dir");
    private static final long SITUATION_SEARCH_TIMEOUT_MS = 180_000; // 3 minutes
    private static final long SITUATION_POLL_INTERVAL_MS = 5000; // 5 seconds
    private static final int SUCCESS_TOAST_TIMEOUT_SECONDS = 5;

    String baseJSONFilePath = currentDir + File.separator + "resources" + File.separator + "ITOM" + File.separator + "IA";
    String EVENTS_ENDPOINT = "/events-service/api/v1.0/events";
    String EVENTS_POLICY_ENDPOINT = "/events-service/api/v1.0/event_policies";
    String DISABLE_EVENTS_POLICY_ENDPOINT = "/events-service/api/v1.0/event_policies/disable";
    String REMOVE_EVENTS_POLICY_ENDPOINT = "/events-service/api/v1.0/event_policies/delete";
    String GROUP_CREATE_ENDPOINT = "/tsws/monitoring/api/v1.0/tag/create";
    String GROUP_UPDATE_ENDPOINT = "/tsws/monitoring/api/v1.0/tag/update";
    String GROUP_DELETE_ENDPOINT = "/tsws/monitoring/api/v1.0/tag/delete";
    String DELETE_ALARM_API = "/alarm-service/api/v1.0/alarm_policies";
    String CREATE_ALARM_POLICY = "/tsws/monitoring/api/v1.0/alarm/policies";
    String GET_SOLUTIONS = "/tsws/monitoring/api/v1.0/package/solutions";
    String SEARCH_IA_POLICY_ENDPOINT = "/intelligent-automation/api/v1/workflows/search";
    String UPDATE_IA_POLICY_STATUS_ENDPOINT = "/intelligent-automation/api/v1/workflows/update_status";
    String CREATE_IA_POLICY_ENDPOINT = "/intelligent-automation/api/v1/workflows";
    String CONFIGURE_IS_CONNECTOR_ENDPOINT = "/intelligent-automation/api/v1/connectors";
    String REMOTE_PLUGIN_KEY = "/intelligent-automation/api/v1/remote_plugins";

    String GET_ALL_SWII_CONNECTORS_ENDPOINT = "/swpui/api/mediator/v3/origins/retrieve/all";
    String DELETE_SWII_CONNECTOR_ENDPOINT = "/swpui/api/mediator/v3/origins/delete";
    String DELETE_IA_POLICY_STATUS_ENDPOINT = "/intelligent-automation/api/v1/workflows";
    String UPDATE_AIOPS_FEATURE_FLAGS_ENDPOINT = "/aiops-config/api/v1.0/features";
    String defaultSearchjsonString = "{\"pageSize\":25,\"pageNumber\":0,\"filter\":\"type = 'user_defined'\",\"sortedOn\":\"updated_time DESC\"}";
    String finalJson = "";

    String situationLink = "(//a[contains(@class, 'situation-msg') and normalize-space(.)='%s'])[1]";
    String situationActionBtn = "//tr[contains(. ,'%s')]//button[@id='actionDropdownMenu']";
    String situationStatus = "//aiops-card[contains(. ,'%s')]//*[contains(@id, 'situation-status')]";
    String filterDropdownBtn = "//button[contains(. ,'%s')]";
    String filterOptionCheckbox = "//button[@role='option' and contains(. ,'%s')]";

    // WebElement Locators
    @FindBy(xpath = "//aiops-badge[contains(. ,'CRITICAL')]/ancestor::aiops-situation-analyzer-detail//*[@id='situation-detail-incident']//span")
    WebElement situationID;
    @FindBy(xpath = "//a[@href='#/situations' and @role='link']")
    WebElement linkSituations;
    @FindBy(xpath = "//header[contains(. ,'Service Monitoring')]")
    WebElement serviceMonitoringHeader;
    @FindBy(xpath = "//button[@data-testid='events-advanced-filter-id_menu']")
    WebElement advancedFilter;
    @FindBy(xpath = "//div[contains(text(), 'Selection Criteria')]/ancestor::button")
    WebElement selectionCriteriaFilter;
    @FindBy(xpath = "//input[@id = 'criteria-rows-typeahead-input']")
    WebElement criteriaFilterInput;
    @FindBy(xpath = "//button[@data-testid='events-advanced-filter-id_footer_apply']")
    WebElement applyFilter;
    @FindBy(xpath = "//div[@id='eventMenuButton0']")
    WebElement threeDotFirstEventEdit;
    @FindBy(xpath = "//a[@id='event_action_show_export_anchor']")
    WebElement exportOption;
    @FindBy(xpath = "//*[@id='radio_slot_scope_allSlots']")
    WebElement allSlots;
    @FindBy(xpath = "//button[@ng-click='ok()']/span[text()='Export']")
    WebElement exportBtn;
    @FindBy(xpath = "//input[@id='criteria-rows-typeahead-input']")
    WebElement triggerConditionTextBox;
    @FindBy(xpath = "//*[text()='No Bracket']")
    WebElement noParenthesisOption;
    @FindBy(xpath = "//adapt-highlight[normalize-space()='ID']")
    WebElement idCondition;
    @FindBy(xpath = "(//*[text()='Equals'])")
    WebElement equalSign;
    @FindBy(xpath = "//button[contains(@class, 'advanced-filter__dropdown')]")
    WebElement advancedFilterButton;
    @FindBy(xpath = "//*[contains(@class, 'a-tag active')]")
    WebElement activeFilterTag;
    @FindBy(xpath = "//button[normalize-space(.)='Clear']")
    WebElement clearFilterButton;
    @FindBy(xpath = "//button[normalize-space(.)='Apply filters']")
    WebElement applyFilterButton;
    @FindBy(xpath = "//input[@placeholder='Search by situation and service names']")
    WebElement searchSituationInput;
    @FindBy(xpath = "//button[normalize-space(.)='Search']")
    WebElement searchButton;
    @FindBy(xpath = "//button[@id='toggle-refresh']")
    WebElement refreshSituationsBtn;
    @FindBy(xpath = "//button[@id='toggle-tile-button']")
    WebElement tileViewBtn;
    @FindBy(xpath = "//button[@id='toggle-hierarchy-button']")
    WebElement hierarchyViewBtn;
    @FindBy(xpath = "//button[@id='toggle-list-button']")
    WebElement listViewBtn;
    @FindBy(xpath = "//*[contains(@class, 'advanced-filter__rx-select')]")
    WebElement filterDropdownOptions;
    @FindBy(xpath = "//button[contains(. ,'Apply filters')]")
    WebElement filterApplyButton;
    @FindBy(xpath = "//button[normalize-space(.)='Close Situation']")
    WebElement closeSituationBtn;
    @FindBy(xpath = "//button[normalize-space(.)='Yes']")
    WebElement yesBtn;
    @FindBy(xpath = "//*[contains(@class, 'a-toast--success')]")
    WebElement successToastMsg;
    @FindBy(xpath = "//span[contains(text(),'Multi-Service')]")
    WebElement linkMultiService;
    @FindBy(xpath = "//button[contains(. ,'Events')]")
    WebElement linkEvents;
    @FindBy(xpath = "//a[contains(@class, 'situation-msg')]//span")
    WebElement situationName;


    public ServiceMonitoringPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public ServiceMonitoringPage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public ServiceMonitoringPage verifyLogin() {
        commonWaits.waitForElementVisible_withFluentWait(getDriver(), serviceMonitoringHeader, 10);
        Assert.assertTrue(serviceMonitoringHeader.isDisplayed(), "Monitoring page is not displayed");
        return this;
    }

    public ServiceMonitoringPage navigateToSituations() {
        commonWaits.waitForElementVisible(linkSituations).click();
        return this;
    }

    public ServiceMonitoringPage generateJWTToken() {
        // Create JSON payload
        JSONObject requestBody = new JSONObject();
        requestBody.put("access_key", ACCESS_KEY);
        requestBody.put("access_secret_key", ACCESS_SECRET_KEY);

        try {
            // Make POST request
            Response response = RestAssured.given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(requestBody.toString())
                    .post("/ims/api/v1/access_keys/login");

            // Verify response status code
            Assert.assertEquals(response.getStatusCode(), 200, "Failed to generate JWT token");

            // Extract JWT token from response
            jwtToken = response.jsonPath().getString("json_web_token");
            Assert.assertNotNull(jwtToken, "JWT token is null");

            // Print token for verification (remove in production)
            System.out.println("Generated JWT Token: " + jwtToken);

        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            throw e;
        }
        return this;
    }

    public ServiceMonitoringPage send_events_with_class(String classType, String uniqueMessage, String hostName) {
        String token = getJwtToken();
        String eventJson = Generate_json_for_event(classType, hostName, uniqueMessage);
        injectEventWithRuntimeJson(token, eventJson);
        return this;
    }

    public ServiceMonitoringPage send_events(String uniqueMessage, String hostName) {
        String token = getJwtToken();
        String eventJson = Generate_json_for_event("EVENT", hostName, uniqueMessage);
        injectEventWithRuntimeJson(token, eventJson);
        return this;
    }

    public ServiceMonitoringPage send_IA_policy_event(String hostName, String uniqueMessage) throws IOException {
        String token = getJwtToken();
        String jsonFilePath = currentDir + File.separator + "resources" + File.separator + "ITOM" + File.separator + "Monitor" + File.separator + "IAPolicyEventPayload.json";
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        String finalJson = content
                .replace("__hostname__", hostName)
                .replace("__msg__", uniqueMessage);
        injectEventWithRuntimeJson(token, finalJson);
        log.info("Sent IA policy event with message: " + uniqueMessage);
        return this;
    }


    public ServiceMonitoringPage send_events_with_object(String uniqueMessage, String hostName) {
        String token = getJwtToken();
        String eventJson = Generate_json_with_object_for_event("EVENT", hostName, uniqueMessage);
        injectEventWithRuntimeJson(token, eventJson);
        return this;
    }

    public ServiceMonitoringPage send_events_with_details(String uniqueMessage, String hostName, String details) {
        String token = getJwtToken();
        String eventJson = Generate_json_for_event("EVENT", hostName, uniqueMessage);
        String modifiedJson = eventJson.replace(
                "\"category\": \"APPLICATION\",",
                "\"category\": \"APPLICATION\",\n" + details
        );
        injectEventWithRuntimeJson(token, modifiedJson);
        return this;
    }

    public ServiceMonitoringPage send_events_with_json(String jsonFilePath) throws IOException {
        String token = getJwtToken();
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        injectEventWithRuntimeJson(token, content);
        return this;
    }

    public ServiceMonitoringPage update_group(String jsonFilePath, String sGroupName, String uniqueEventMessage, String hostName, String groupid, String publish) {
        log.info("Updating group: " + sGroupName);
        finalJson = "";
        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            finalJson = content
                    .replace("__device_name__", hostName)
                    .replace("__unique_event_msg__", uniqueEventMessage)
                    .replace("__tag_id__", groupid)
                    .replace("__groupname__", sGroupName)
                    .replace("__false_true__", publish);

        } catch (IOException e) {
            log.error("Error reading JSON file: " + e.getMessage());
        }
        post_group_updation(getJwtToken(), finalJson);
        return this;
    }

    public ServiceMonitoringPage delete_group(String groupid) {
        log.info("Deleting group with group id :" + groupid);
        String jsonString = "{\"tagIds\":[\"" + groupid + "\"]}";
        post_group_deletion(getJwtToken(), jsonString);
        return this;
    }

    public String Generate_json_for_correlation_event_policy(String json_file_path, String host_identifierA, String uniqueEventMessage) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(json_file_path)));

        String finalJson = content
                .replace("__ci__", host_identifierA)
                .replace("__uniquemsg__", uniqueEventMessage);

        return finalJson;
    }

    public String getVerionForKPI() {
        String SolutionVersion = "";

        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .get(GET_SOLUTIONS);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to get Version of a deployed KPI solution. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        JSONArray dataVerisons = objJsonObject.getJSONArray("solutionData");

        for (int i = 0, size = dataVerisons.length(); i < size; i++) {
            JSONObject objectInArray = dataVerisons.getJSONObject(i);
            if (objectInArray.get("solutionName").equals("nuk")) {
                SolutionVersion = objectInArray.get("version").toString();
            }
        }
        return SolutionVersion;
    }

    public String Generate_json_for_alarm_policy(String json_file_path, String host_identifierA, String uniqueEventMessage) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(json_file_path)));
        String sVersion = getVerionForKPI();
        String finalJson = content
                .replace("__pa_host__", host_identifierA)
                .replace("__alarm_policy_name__", uniqueEventMessage)
                .replace("__sol_version__", sVersion);
        return finalJson;
    }

    public ServiceMonitoringPage delete_alarm_policy(String policyID) {
        String token1 = getJwtToken();
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .delete(DELETE_ALARM_API + "/" + policyID);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to delete alarm policy. Response: " + response1.body().asString());

        return this;
    }

    public String get_IA_policy_id(String policyName) throws IOException {
        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .body(defaultSearchjsonString)
                .post(SEARCH_IA_POLICY_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to get IA policy data. Response: " + response1.body().asString());

        String output = response1.getBody().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(output);

        JsonNode dataArray = rootNode.path("data");
        String foundId = null;

        for (JsonNode node : dataArray) {
            String name = node.path("name").asText();
            if (name.equals(policyName)) {
                foundId = node.path("id").asText();
                break;
            }
        }

        if (foundId != null) {
            log.info("ID for name '" + policyName + "' is: " + foundId);
        } else {
            log.error("No matching name found for given policy Name ! " + policyName);
        }

        return foundId;
    }

    public String get_IA_policy_checksum(String policyName) throws IOException {

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .body(defaultSearchjsonString)
                .post(SEARCH_IA_POLICY_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to get IA policy data. Response: " + response1.body().asString());

        String output = response1.getBody().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(output);

        JsonNode dataArray = rootNode.path("data");
        String foundId = null;

        for (JsonNode node : dataArray) {
            String name = node.path("name").asText();
            if (name.equals(policyName)) {
                foundId = node.path("checksum").asText();
                break;
            }
        }

        if (foundId != null) {
            log.info("checksum for name '" + policyName + "' is: " + foundId);
        } else {
            log.error("No data found for given policy Name ! " + policyName);
        }

        return foundId;
    }

    public String get_IA_policy_status(String policyName) throws IOException {

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .body(defaultSearchjsonString)
                .post(SEARCH_IA_POLICY_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to get IA policy data. Response: " + response1.body().asString());

        String output = response1.getBody().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(output);

        JsonNode dataArray = rootNode.path("data");
        String foundStatus = null;

        for (JsonNode node : dataArray) {
            String name = node.path("name").asText();
            if (name.equals(policyName)) {
                foundStatus = node.path("status").asText();
                break;
            }
        }

        if (foundStatus != null) {
            log.info("Status for name '" + policyName + "' is: " + foundStatus);
        } else {
            log.error("No data found for given policy Name ! " + policyName);
        }

        return foundStatus;
    }

    public ServiceMonitoringPage delete_SWII_connector(String connectorName) throws IOException {

        String json = getAllSWIIConnectors();
        String id = getSWIIConnectorIdByInstanceName(json, connectorName);

        if (id != null) {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "Bearer " + getJwtToken())
                    .delete(DELETE_SWII_CONNECTOR_ENDPOINT + "?id=" + id + "&cascade=true");

            // Verify response
            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to delete II connector. Response: " + response1.body().asString());
        } else {
            log.info("II Connector with name '" + connectorName + "' not found.");
        }

        return this;
    }

    public String getAllSWIIConnectors() {

        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "helix_jwt_token=" + getJwtToken())
                .contentType(ContentType.JSON)
                .get(GET_ALL_SWII_CONNECTORS_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Error while fetching all SWII connectors.. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        return output;
    }

    public String getSWIIConnectorIdByInstanceName(String jsonResponse, String keyword) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode arrayNode = mapper.readTree(jsonResponse);

        for (JsonNode node : arrayNode) {
            String instanceName = node.get("instanceName").asText();
            if (instanceName != null && instanceName.toLowerCase().contains(keyword.toLowerCase())) {
                return node.get("id").asText();
            }
        }
        return null; // Return null if not found
    }


    public ServiceMonitoringPage create_IA_policy_using_IS_connector(String policyName) throws IOException {

        String IAPolicyJsonPath = baseJSONFilePath + File.separator + "IA_Policy_Using_IS_Connector.json";
        String rest_api = config.get("CMDBURL");

        //w/f id, this is a random id value.
        String wf_id = UUID.randomUUID().toString().toLowerCase();

        String content = new String(Files.readAllBytes(Paths.get(IAPolicyJsonPath)));
        String finalJson = content
                .replace("__ia_policy_name__", policyName)
                .replace("___is_restapi__", rest_api);

        log.info("Creating IA policy with name: " + policyName);
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .body(finalJson)
                .put(CREATE_IA_POLICY_ENDPOINT + "/" + wf_id);

        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to create IA policy. Response: " + response1.body().asString());
        return this;
    }


    public ServiceMonitoringPage create_IA_Automatic_policy_using_IS_connector(String policyName) throws IOException {

        String IAPolicyJsonPath = baseJSONFilePath + File.separator + "IA_Automatic_Event_Policy.json";
        String rest_api = config.get("CMDBURL");

        //w/f id, this is a random id value.
        String wf_id = UUID.randomUUID().toString().toLowerCase();

        String content = new String(Files.readAllBytes(Paths.get(IAPolicyJsonPath)));
        String finalJson = content
                .replace("__ia_policy_name__", policyName)
                .replace("___is_restapi__", rest_api);

        log.info("Creating IA policy with name: " + policyName);
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .body(finalJson)
                .put(CREATE_IA_POLICY_ENDPOINT + "/" + wf_id);

        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to create IA policy. Response: " + response1.body().asString());
        return this;
    }

    public ServiceMonitoringPage syncActionsApi(String connectorName) throws Exception {
        log.info("Checking for existing IS connector with name: " + connectorName);
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .get(CONFIGURE_IS_CONNECTOR_ENDPOINT);

        String output = response1.getBody().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootArray = mapper.readTree(output);

        if (rootArray.isArray()) {
            for (JsonNode node : rootArray) {
                JsonNode nameNode = node.get("name");
                if (nameNode != null && nameNode.asText().equalsIgnoreCase(connectorName)) {
                    JsonNode idNode = node.get("id");
                    if (idNode != null) {
                        String connectorId = idNode.asText();
                        log.info("Found existing IS connector with name: " + connectorName + ". Syncing it now.");
                        Response deleteResponse = RestAssured.given()
                                .baseUri(BASE_URL)
                                .header("Authorization", "Bearer " + getJwtToken())
                                .get(CONFIGURE_IS_CONNECTOR_ENDPOINT + "/" + connectorId + "/sync_actions");

                        Assert.assertEquals(deleteResponse.getStatusCode(), 201,
                                "Failed to Sync IS connector. Response: " + deleteResponse.body().asString());
                        log.info("Synced IS connector with name: " + connectorName);
                    }
                }
            }
        }
        return this;
    }


    public ServiceMonitoringPage deleteConnector(String sConnectorName) throws IOException {

        log.info("Checking for existing IS connector with name: " + sConnectorName);
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .get(CONFIGURE_IS_CONNECTOR_ENDPOINT);

        String output = response1.getBody().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootArray = mapper.readTree(output);

        if (rootArray.isArray()) {
            for (JsonNode node : rootArray) {
                JsonNode nameNode = node.get("name");
                if (nameNode != null && nameNode.asText().equalsIgnoreCase(sConnectorName)) {
                    JsonNode idNode = node.get("id");
                    if (idNode != null) {
                        String connectorId = idNode.asText();
                        log.info("Found existing IS connector with name: " + sConnectorName + ". Deleting it now.");
                        Response deleteResponse = RestAssured.given()
                                .baseUri(BASE_URL)
                                .header("Authorization", "Bearer " + getJwtToken())
                                .delete(CONFIGURE_IS_CONNECTOR_ENDPOINT + "/" + connectorId);

                        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                                "Failed to delete IS connector. Response: " + deleteResponse.body().asString());
                        log.info("Deleted IS connector with name: " + sConnectorName);
                    }

                }
            }
        }
        return this;
    }

    public ServiceMonitoringPage configure_IS_connector(String connectorName) throws Exception {

        log.info("Deleting existing IS connector with name: " + connectorName + " if exists.");
        deleteConnector(connectorName);

        log.info("Configuring IS connector with name: " + connectorName);
        String IAPolicyJsonPath = baseJSONFilePath + File.separator + "InnovationStudioConnector.json";
        String rest_api = config.get("CMDBURL");
        String userName = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");
        String content = new String(Files.readAllBytes(Paths.get(IAPolicyJsonPath)));
        String finalJson = content
                .replace("__connectorName__", connectorName)
                .replace("__restapiTargetURL__", rest_api)
                .replace("__username__", userName)
                .replace("__password__", password);

        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getJwtToken())
                .contentType(ContentType.JSON)
                .body(finalJson)
                .post(CONFIGURE_IS_CONNECTOR_ENDPOINT);

        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to configure IS connector. Response: " + response1.body().asString());

        log.info("Syncing IS connector with name: " + connectorName);
        syncActionsApi(connectorName);

        log.info("Waiting for 15 seconds for the sync to complete.");
        commonWaits.waitForSometime(15000);
        return this;
    }

    public ServiceMonitoringPage update_IA_policy_status(String policyName, String status) throws IOException {

        String id = get_IA_policy_id(policyName);
        String checksum = get_IA_policy_checksum(policyName);

        if (id != null) {
            String jsonString = String.format(
                    "{\"id\": \"%s\",\"checksum\": \"%s\",\"status\": \"%s\"}",
                    id, checksum, status
            );
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "Bearer " + getJwtToken())
                    .contentType(ContentType.JSON)
                    .body(jsonString)
                    .post(UPDATE_IA_POLICY_STATUS_ENDPOINT);

            // Verify response
            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to update IA policy status. Response: " + response1.body().asString());

        } else {
            log.info("Policy with name '" + policyName + "' not found.");
        }
        return this;
    }

    public ServiceMonitoringPage delete_IA_policy(String policyName) throws IOException {

        String id = get_IA_policy_id(policyName);
        if (id != null) {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "Bearer " + getJwtToken())
                    .delete(DELETE_IA_POLICY_STATUS_ENDPOINT + "/" + id);

            // Verify response
            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to delete IA policy. Response: " + response1.body().asString());
        } else {
            log.info("Policy with name '" + policyName + "' not found.");
        }

        return this;
    }

    public String post_event_correlation_policy(String token1, String jsonContent) {

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(jsonContent)
                .post(EVENTS_POLICY_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to create correlation event policy. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        //String message = objJsonObject.getString("message");
        String policyid = objJsonObject.getJSONArray("object").get(0).toString();
        return policyid;
    }

    public void switchToNewWindow(String originalWindow) {
        try {
            // Get all window handles
            Set<String> handles = getDriver().getWindowHandles();

            // Switch to the new window/tab
            for (String handle : handles) {
                if (!handle.equals(originalWindow)) {
                    getDriver().switchTo().window(handle);
                    System.out.println("Switched to new window with title: " + getDriver().getTitle());
                    return;
                }
            }
            throw new RuntimeException("New window/tab not found");
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch to new window: " + e.getMessage());
        }
    }

    public String getJwtToken() {
        if (jwtToken == null) {
            generateJWTToken();
        }
        return jwtToken;
    }

    public String create_group(String jsonFilePath, String sGroupName, String uniqueEventMessage, String hostName, String groupid, String publish) {
        log.info("Creating group: " + sGroupName);
        finalJson = "";
        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            finalJson = content
                    .replace("__device_name__", hostName)
                    .replace("__unique_event_msg__", uniqueEventMessage)
                    .replace("__tag_id__", groupid)
                    .replace("__groupname__", sGroupName)
                    .replace("__false_true__", publish);
        } catch (IOException e) {
            log.error("Error reading JSON file: " + e.getMessage());
        }
        groupid = post_group_creation(getJwtToken(), finalJson);
        log.info("Group id is: " + groupid);
        return groupid;
    }

    public String create_alarm_policy_for_pa(String jsonFilePath, String uniqueMessage, String hostName) {
        String token = getJwtToken();
        String policyid = "";
        String alarmPolicyJson = "";
        try {
            alarmPolicyJson = Generate_json_for_alarm_policy(jsonFilePath, hostName, uniqueMessage);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
        policyid = post_alarm_policy(token, alarmPolicyJson);
        return policyid;
    }


    public String create_alarm_policy_from_json(String jsonFilePath, String policyName) throws IOException {
        String token = getJwtToken();
        String policyid = "";
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        String finalJson = content
                .replace("__alarm_policy_name__", policyName);
        policyid = post_alarm_policy(token, finalJson);
        return policyid;
    }

    public String create_correlation_event_policy(String jsonFilePath, String uniqueMessage, String hostName) {
        String token = getJwtToken();
        String policyid = "";
        String eventJson = "";
        try {
            eventJson = Generate_json_for_correlation_event_policy(jsonFilePath, hostName, uniqueMessage);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
        policyid = post_event_correlation_policy(token, eventJson);
        return policyid;
    }

    public String Generate_json_for_event(String className, String host_identifier, String uniqueEventMessage) {
        String jsonBody = "[{"
                + "\"class\": \"" + className + "\","
                + "\"severity\": \"CRITICAL\","
                + "\"msg\": \"" + uniqueEventMessage + "\","
                + "\"source_identifier\": \"solqa\","

                + "\"category\": \"APPLICATION\","
                + "\"source_hostname\": \"" + host_identifier + "\","
                + "\"priority\": \"PRIORITY_1\","
                + "\"class_slots\":{ \"pn_severity\": \"MAJOR\" }"
                + "}]";

        return jsonBody;

    }

    public String Generate_json_with_object_for_event(String className, String host_identifier, String uniqueEventMessage) {
        String jsonBody = "[{"
                + "\"class\": \"" + className + "\","
                + "\"severity\": \"CRITICAL\","
                + "\"msg\": \"" + uniqueEventMessage + "\","
                + "\"source_identifier\": \"solqa\","

                + "\"category\": \"APPLICATION\","
                + "\"source_hostname\": \"" + host_identifier + "\","
                + "\"priority\": \"PRIORITY_1\","
                + "\"object\": \"" + "applications@Pagero-Prod@Company-BMC-SOFTWARE-INC-Documents" + "\","
                + "\"object_class\": \"" + "STM_SCRIPT_EP" + "\","
                + "\"object_id\": \"" + "12fe9b3a-747c-46a5-9629-b72c667b9a2f:STM_SCRIPT_EP:applications@Pagero-Prod@Company-BMC-SOFTWARE-INC-Documents" + "\","
                + "\"class_slots\":{ \"pn_severity\": \"MAJOR\" }"
                + "}]";

        return jsonBody;

    }


    public String deactivate_event_policy(String policyID) {

        String json = "{\"ids\": [\"" + policyID + "\"]}";
        String token1 = getJwtToken();
        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(json)
                .post(DISABLE_EVENTS_POLICY_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to deactivate event policy. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        String policyid = objJsonObject.getJSONArray("object").get(0).toString();
        return policyid;
    }

    public String delete_event_policy(String policyID) {

        String json = "{\"ids\": [\"" + policyID + "\"]}";
        String token1 = getJwtToken();
        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(json)
                .post(REMOVE_EVENTS_POLICY_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to delete event policy. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        String policyid = objJsonObject.getJSONArray("object").get(0).toString();
        return policyid;
    }

    public String post_alarm_policy(String token1, String jsonContent) {
        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(jsonContent)
                .post(CREATE_ALARM_POLICY);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to create alarm policy. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        String policyid = objJsonObject.getJSONArray("resourceId").get(0).toString();
        return policyid;
    }

    public String post_group_creation(String token1, String jsonContent) {
        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(jsonContent)
                .post(GROUP_CREATE_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to create Group. Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        String groupID = objJsonObject.getString("tagId").toString();
        return groupID;
    }

    public ServiceMonitoringPage post_group_updation(String token1, String jsonContent) {

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(jsonContent)
                .put(GROUP_UPDATE_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to update group. Response: " + response1.body().asString());

        return this;
    }

    public ServiceMonitoringPage post_group_deletion(String token1, String jsonContent) {

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(jsonContent)
                .post(GROUP_DELETE_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to delete group. Response: " + response1.body().asString());

        return this;
    }

    public ServiceMonitoringPage injectEventWithRuntimeJson(String token1, String jsonContent) {

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(jsonContent)
                .post(EVENTS_ENDPOINT);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to inject events. Response: " + response1.body().asString());

        // Log success
        System.out.println("Response: " + response1.body().asString());

        return this;
    }

    public ServiceMonitoringPage injectEvent(String token1, String jsonContent, String endpoint) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(jsonContent)));
        JSONArray eventsArray = new JSONArray(content);

        // Make the API call with the events
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token1)
                .contentType(ContentType.JSON)
                .body(eventsArray.toString())
                .post(endpoint);

        // Verify response
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to inject events. Response: " + response1.body().asString());

        // Log success
        System.out.println("Successfully injected " + eventsArray.length() + " events");
        System.out.println("Response: " + response1.body().asString());

        return this;
    }

    public ServiceMonitoringPage openSituation(String Name) {
        // Wait for situation to be visible in the list
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(situationLink, Name)));
        Assert.assertTrue(we.isDisplayed(), "Multiservice situation not found");
        commonWaits.clickWithoutInterception(we);

        return this;
    }

    public ServiceMonitoringPage removeExistingAdvancedFilters() {
        if (commonWaits.isElementPresent(activeFilterTag)) {
            commonWaits.clickWithoutInterception(advancedFilterButton);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickWithoutInterception(clearFilterButton);
            commonWaits.waitForExecutionComplete();
            commonWaits.clickWithoutInterception(applyFilterButton);
        }
        commonWaits.waitForAllSpinnersToHide("Aiops");
        commonWaits.waitForExecutionComplete();

        return this;
    }

    public ServiceMonitoringPage searchSituation(String situationName) {
        return searchSituation(situationName, SITUATION_SEARCH_TIMEOUT_MS);
    }

    public ServiceMonitoringPage searchSituation(String situationName, long timeoutMs) {
        log.info("Searching for situation: " + situationName + " (timeout: " + (timeoutMs / 1000) + "s)");

        // Refresh and search
        commonWaits.clickWithoutInterception(refreshSituationsBtn);
        commonWaits.waitForElementVisible(searchSituationInput);
        searchSituationInput.clear();
        searchSituationInput.sendKeys(situationName);
        commonWaits.waitForAllSpinnersToHide("Aiops");
        commonWaits.clickWithoutInterception(searchButton);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("Aiops");

        // Poll for situation to appear
        By situationXpath = By.xpath(String.format(situationLink, situationName));
        long startTime = System.currentTimeMillis();
        boolean found = false;
        int pollCount = 0;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            pollCount++;

            if (commonWaits.isElementPresent(situationXpath)) {
                found = true;
                log.info("Situation found after " + pollCount + " polls");
                break;
            }

            log.debug("Situation not found yet, polling...  (attempt " + pollCount + ")");
            commonWaits.waitForSometime(SITUATION_POLL_INTERVAL_MS);
            commonWaits.clickWithoutInterception(refreshSituationsBtn);
            commonWaits.waitForAngularRequestsToFinish();
            commonWaits.waitForAllSpinnersToHide("Aiops");
        }

        Assert.assertTrue(found, "Situation '" + situationName + "' not found after " + (timeoutMs / 1000) + " seconds (" + pollCount + " polls)");

        // Verify situation name matches
        String actualName = commonWaits.waitForByElementToBeVisible(situationXpath).getText().trim();
        Assert.assertEquals(actualName, situationName, "Situation name mismatch.  Expected: '" + situationName + "', Actual: '" + actualName + "'");

        log.info("Situation verified: " + situationName);
        return this;
    }

    public ServiceMonitoringPage verifySituationDetails() {
        Assert.assertTrue(commonWaits.waitForElementVisible(situationID).getText().contains("PD"), "Situation ID not found");
        return this;
    }

    public ServiceMonitoringPage advancedFilter(String criteria) {
        log.info("Applying advanced filter with criteria: " + criteria);

        try {
            // Wait for page to be ready
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            // Open advanced filter
            commonWaits.waitForElementClickable(advancedFilter);
            click(advancedFilter);

            // Open selection criteria
            commonWaits.waitForElementClickable(selectionCriteriaFilter);
            click(selectionCriteriaFilter);

            // Wait for filter input to be visible
            commonWaits.waitForElementVisible(criteriaFilterInput);
            Assert.assertTrue(triggerConditionTextBox.isDisplayed(),
                    "Trigger condition textbox not displayed");

            // Select "No Bracket" option
            triggerConditionTextBox.click();
            Assert.assertTrue(noParenthesisOption.isDisplayed(),
                    "No Parenthesis option not displayed");
            noParenthesisOption.click();

            // Select ID condition
            commonWaits.scrollToSpecificElement(idCondition);
            commonWaits.waitForElementClickable(idCondition).click();

            // Select equals operator
            commonWaits.waitForElementClickable(equalSign).click();

            // Enter criteria
            commonWaits.waitForElementClickable(triggerConditionTextBox);
            typeKeys(triggerConditionTextBox, criteria);

            // Wait for any auto-complete/suggestions to load
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            // Confirm entry
            new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

            // Apply filter
            commonWaits.waitForElementClickable(applyFilter);
            click(applyFilter);

            // Wait for filter to be applied
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);

            // Verify filter was applied (check for active filter tag)
            Assert.assertTrue(commonWaits.isElementPresent(activeFilterTag),
                    "Filter was not applied successfully - active filter tag not present");

            log.info("Advanced filter applied successfully:  " + criteria);
            return this;

        } catch (Exception e) {
            log.error("Failed to apply advanced filter: " + criteria, e);
            throw new AssertionError("Advanced filter application failed:  " + e.getMessage(), e);
        }
    }

    public ServiceMonitoringPage exportEvent() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        commonWaits.waitForElementClickable(threeDotFirstEventEdit);
        click(threeDotFirstEventEdit);
        commonWaits.waitForElementClickable(exportOption);
        click(exportOption);
        click(allSlots);
        commonWaits.waitForElementVisible(exportBtn);
        clickByJS(exportBtn);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        return this;
    }

    public ServiceMonitoringPage closeSituation(String situationName) {
        if (situationName == null || situationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Situation name cannot be null or empty");
        }

        log.info("Closing situation: " + situationName);

        try {
            // Open action menu
            WebElement actionBtn = commonWaits.waitForByElementToBeVisible(
                    By.xpath(String.format(situationActionBtn, situationName)));
            commonWaits.clickWithoutInterception(actionBtn);

            // Click close situation
            commonWaits.clickWithoutInterception(closeSituationBtn);

            // Confirm closure
            commonWaits.clickWithoutInterception(yesBtn);

            // Wait for success message
            boolean toastDisplayed = commonWaits.waitForElementVisible_withFluentWait(
                    getDriver(), successToastMsg, SUCCESS_TOAST_TIMEOUT_SECONDS);

            Assert.assertTrue(toastDisplayed,
                    "Success toast message not displayed after closing situation '" + situationName + "'");

            log.info("Situation closed successfully: " + situationName);
            return this;

        } catch (Exception e) {
            log.error("Failed to close situation: " + situationName, e);
            throw new AssertionError("Situation closure failed: " + e.getMessage(), e);
        }
    }

    public String getSituationStatus(String Name) {
        commonWaits.clickWithoutInterception(tileViewBtn);
        WebElement we = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(situationStatus, Name)));
        String status = we.getText().trim();
        commonWaits.clickWithoutInterception(hierarchyViewBtn);
        return status;
    }

    public ServiceMonitoringPage setFilters(String filterName, String filterValue) {
        commonWaits.clickWithoutInterception(advancedFilterButton);
        commonWaits.clickWithoutInterception(By.xpath(String.format(filterDropdownBtn, filterName)));
        commonWaits.waitForElementVisible(filterDropdownOptions);
        commonWaits.clickWithoutInterception(By.xpath(String.format(filterOptionCheckbox, filterValue)));
        commonWaits.clickWithoutInterception(filterApplyButton);
        return this;
    }

    public ServiceMonitoringPage navigateToListView() {
        commonWaits.clickWithoutInterception(listViewBtn);
        return this;
    }


}