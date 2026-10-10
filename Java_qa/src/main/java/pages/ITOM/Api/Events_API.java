package pages.ITOM.Api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;

public class Events_API {

    static SolqaLogger log = new SolqaLogger(Events_API.class.getName());
    static final String EVENTS_ENDPOINT = "/events-service/api/v1.0/events";
    String jwtToken;
    Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    public void injectEventTicketForBHOM(String jsonFilePath, Map<Object, String> TestData, String msg) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        JSONArray eventsArray = new JSONArray(content);
        for (int i = 0; i < eventsArray.length(); i++) {
            JSONObject jsonData = eventsArray.getJSONObject(i);
            jsonData.put("severity", TestData.get("severity"));
            jsonData.put("msg", msg);
            jsonData.put("source_identifier", configProp.get("PA_AGENT"));
            jsonData.put("host", configProp.get("PA_AGENT"));
            jsonData.put("source_hostname", configProp.get("PA_AGENT"));
            jsonData.put("status", TestData.get("status"));
        }
        // Make the API call with the event data
        Response response = RestAssured.given()
                .baseUri(configProp.get("ADE_PLATFORM_URL"))
                .header("Authorization", "Bearer " + getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY))
                .contentType(ContentType.JSON)
                .body(eventsArray.toString())
                .post(EVENTS_ENDPOINT);
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to create BHOM policy events");
        if (response.getStatusCode() == 200) {
            JSONObject jsonObject = new JSONObject(response.getBody().asString());
            JSONArray resourceIdArray = jsonObject.getJSONArray("resourceId");
            List<String> value = new ArrayList<>();
            value.add(resourceIdArray.getString(0));
        }else{
            log.error("Response code is not 200" + response.getStatusCode());
        }
    }

    //this method will return event ID
    public String injectEventTicketUsingJsonContent(String jsonString) {
        JSONArray eventsArray = new JSONArray(jsonString);
        // Make the API call with the event data
        Response response = RestAssured.given()
                .baseUri(configProp.get("ADE_PLATFORM_URL"))
                .header("Authorization", "Bearer " + getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY))
                .contentType(ContentType.JSON)
                .body(eventsArray.toString())
                .post(EVENTS_ENDPOINT);
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to create BHOM policy events");
        if (response.getStatusCode() == 200) {
            JSONObject jsonObject = new JSONObject(response.getBody().asString());
            JSONArray resourceIdArray = jsonObject.getJSONArray("resourceId");
            List<String> value = new ArrayList<>();
            value.add(resourceIdArray.getString(0));
            return value.get(0);
        }else{
            log.error("Response code is not 200" + response.getStatusCode());
            return null;
        }
    }
    public void generateJWTToken(String ACCESS_KEY, String ACCESS_SECRET_KEY) {
        // Create JSON payload
        JSONObject requestBody = new JSONObject();
        requestBody.put("access_key", ACCESS_KEY);
        requestBody.put("access_secret_key", ACCESS_SECRET_KEY);

        try {
            // Make POST request
            Response response = RestAssured.given()
                    .baseUri(configProp.get("ADE_PLATFORM_URL"))
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
    }

    public String getJwtToken(String ACCESS_KEY, String ACCESS_SECRET_KEY) {
        if (jwtToken == null) {
            generateJWTToken(ACCESS_KEY, ACCESS_SECRET_KEY);
        }
        System.out.println("Using JWT Token: " + jwtToken);
        return jwtToken;
    }

    public void injectEventForBlackoutPolicy(String jsonFilePath, String hostName, String msg) throws IOException {
        log.info("Creating blackout policy events for host: "+hostName);
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        JSONArray eventsArray = new JSONArray(content);

        // Update host_name in each event object
        for (int i = 0; i < eventsArray.length(); i++) {
            JSONObject event = eventsArray.getJSONObject(i);
            event.put("source_hostname", hostName);
            event.put("external_id", hostName);
            event.put("instancename", hostName);
            event.put("msg", msg);

        }

        try {
            Response response = RestAssured.given()
                    .baseUri(configProp.get("ADE_PLATFORM_URL"))
                    .header("Authorization", "Bearer " + getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY))
                    .contentType(ContentType.JSON)
                    .body(eventsArray.toString())
                    .post(EVENTS_ENDPOINT);
            Assert.assertEquals(response.getStatusCode(), 200, "Failed to create blackout policy events");
        } catch (Exception e) {
            log.error("Error creating blackout policy events: "+e.getMessage());
            throw e;
        }
    }

    public void createBlackoutPolicy(String jsonFilePath, String hostName, String policyName, String blackoutExistingEvents, String resetStatusOnBlackoutEnd) throws IOException {
        // Read and parse JSON
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        JSONObject blackoutPolicy = new JSONObject(content);

        // Set name, description, and eventSelectorCriteria
        blackoutPolicy.put("name", policyName);
        blackoutPolicy.put("description", policyName);
        blackoutPolicy.put("eventSelectorCriteria", "( source_hostname matches '.*" + hostName + ".*' )");

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusMinutes(2);
        LocalDateTime end = start.plusMinutes(10);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String startDate = start.format(dateFormatter);
        String endDate = end.format(dateFormatter);
        String startTime = start.format(timeFormatter);
        String endTime = end.format(timeFormatter);

        // Update nested fields
        JSONObject timeframeDetails = blackoutPolicy
                .getJSONObject("configurations")
                .getJSONObject("definition")
                .getJSONObject("timeframeDetails");

        timeframeDetails.put("startDate", startDate);
        timeframeDetails.put("startTime", startTime);
        timeframeDetails.put("endTime", endTime);
        timeframeDetails.getJSONObject("endsOn").put("endDate", endDate);

        // Update processing_action values
        JSONObject processingAction = blackoutPolicy
                .getJSONObject("configurations")
                .getJSONObject("definition")
                .getJSONObject("processing_action");
        processingAction.put("blackoutExistingEvents", blackoutExistingEvents);
        processingAction.put("resetStatusOnBlackoutEnd", resetStatusOnBlackoutEnd);

        // Make API call
        Response response = RestAssured.given()
                .baseUri(configProp.get("ADE_PLATFORM_URL"))
                .header("Authorization", "Bearer " + getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY))
                .contentType(ContentType.JSON)
                .body(blackoutPolicy.toString())
                .post("events-service/api/v1.0/blackout_policies");
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to create blackout policy");
    }

    public void createSituation(String jsonContent) {

        JSONArray eventsArray = new JSONArray(jsonContent);
        Response response = RestAssured.given()
                .baseUri(configProp.get("ADE_PLATFORM_URL"))
                .header("Authorization", "Bearer " + getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY))
                .contentType(ContentType.JSON)
                .body(eventsArray.toString())
                .post(EVENTS_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to inject events. Response: " + response.body().asString());

        // Log success
        log.info("Successfully injected " + eventsArray.length() + " events");
    }

    public List<String> getAgentHostnames() throws IOException {
        String endpoint = "/tsws/monitoring/api/v1.0/unifiedadmin/agent/details";

        JSONObject directFilterCriteria = new JSONObject();
        directFilterCriteria.put("isConnected", "true");
        directFilterCriteria.put("agentConnected", "true");

        JSONObject requestJson = new JSONObject();
        requestJson.put("stringToSearch", "Windows");
        requestJson.put("directFilterCriteria", directFilterCriteria);

        String jsonInputString = requestJson.toString();

        Response response = RestAssured.given()
                .baseUri(configProp.get("ADE_PLATFORM_URL"))
                .header("Authorization", "Bearer " + getJwtToken(SolQAConstants.API_ACCESS_KEY, SolQAConstants.API_SECRET_KEY))
                .contentType(ContentType.JSON)
                .accept("*/*")
                .body(jsonInputString)
                .post(endpoint);

        Assert.assertEquals(response.getStatusCode(), 200, "Failed to get agent details");

        List<String> hostnames = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody().asString());
        JsonNode agentList = root.path("response").path("agentList");
        for (JsonNode agent : agentList) {
            String hostname = agent.path("hostname").asText();
            hostnames.add(hostname);
        }
        return hostnames;
    }


}