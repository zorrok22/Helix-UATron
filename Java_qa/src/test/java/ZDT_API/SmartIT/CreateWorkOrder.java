package ZDT_API.SmartIT;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import org.json.JSONObject;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.json.JSONException;

public class CreateWorkOrder{

    private LoginHelper loginHelper;
    Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    private String csrfToken;
    private static final Logger logger = Logger.getLogger(CreateWorkOrder.class.getName());

    public CreateWorkOrder(LoginHelper loginHelper) {
        this.loginHelper = loginHelper;
    }

    public void run(){
        try{
            logger.info("Starting Create WorkOrder Flow");
            boolean loginSuccess = loginHelper.performLogin();
            if (!loginSuccess) {
                logger.severe("Login failed");
                return;
            }
            config = loginHelper.getConfig();
            headers = loginHelper.getHeaders();
            cookies = loginHelper.getCookies();
            csrfToken = loginHelper.getCsrfToken();

             // Step 1: Draft Work Order
            String draftUrl = config.get("SmartIT_url") + "/smartit/rest/v2/workorder/draft";
            headers.put("Referer", config.get("SmartIT_url") + "/smartit/app/");
            headers.put("X-XSRF-TOKEN", csrfToken);

            String draftPayload = "{\"type\":\"workorder\"}";
            Response draftResponse = RestAssured.given().baseUri(draftUrl).headers(headers).cookies(cookies).body(draftPayload).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(draftResponse.getStatusCode(), 200, "The Create Workorder draft process has failed for user: " + loginHelper.getUserName());
            logger.info("Create WorkOrder draft validation successful for user: " + loginHelper.getUserName());

            // Extract id and displayId (handle both array and object response, like CreateIncident)
            String workorderId = null;
            String displayId = null;
            String draftResp = draftResponse.getBody().asString();
            try {
                if (draftResp.trim().startsWith("[")) {
                    org.json.JSONArray draftArr = new org.json.JSONArray(draftResp);
                    if (draftArr.length() > 0) {
                        JSONObject draftJson = draftArr.getJSONObject(0);
                        org.json.JSONArray itemsArr = draftJson.optJSONArray("items");
                        if (itemsArr != null && itemsArr.length() > 0) {
                            JSONObject item = itemsArr.getJSONObject(0);
                            displayId = item.optString("displayId", null);
                            workorderId = item.optString("id", null);
                            logger.info("Extracted displayId: " + displayId + ", id: " + workorderId);
                        } else {
                            logger.warning("'items' array is missing or empty in draft response.");
                            return;
                        }
                    } else {
                        logger.warning("Draft array is empty.");
                        return;
                    }
                } else {
                    JSONObject draftJson = new JSONObject(draftResp);
                    org.json.JSONArray itemsArr = draftJson.optJSONArray("items");
                    if (itemsArr != null && itemsArr.length() > 0) {
                        JSONObject item = itemsArr.getJSONObject(0);
                        displayId = item.optString("displayId", null);
                        workorderId = item.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + workorderId + " for user: " + loginHelper.getUserName());
                    } else {
                        displayId = draftJson.optString("displayId", null);
                        workorderId = draftJson.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + workorderId + " for user: " + loginHelper.getUserName());
                    }
                }
            } catch (JSONException e) {
                logger.severe("Failed to parse draft response: " + e.getMessage());
                return;
            }
            if (workorderId == null || displayId == null) {
                logger.warning("No id/displayId in draft response");
                return;
            }

            // Step 2: Read payload from JSON file and replace placeholders
            String payloadPath = "resources" + File.separator + "SMARTIT" + File.separator + "payloads" + File.separator + "SmartIT_create_workorder_payload.json";
            String createPayload = new String(Files.readAllBytes(Paths.get(payloadPath)));
            createPayload = createPayload.replace("${id}", workorderId).replace("${DID}", displayId);

            String createUrl = config.get("SmartIT_url") + "/smartit/rest/workorder";
            Response createResponse = RestAssured.given().baseUri(createUrl).headers(headers).cookies(cookies).body(createPayload.toString()).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(createResponse.getStatusCode(), 200, "The Create Workorder Process has failed for user: " + loginHelper.getUserName());
            logger.info("Create Workorder validation successful for user: " + loginHelper.getUserName());
        }
        catch (Exception e) {
            logger.severe("Exception in Create WorkOrder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

