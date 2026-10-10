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

public class CreateProblem {

    private LoginHelper loginHelper;
    Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    private String csrfToken;
    private static final Logger logger = Logger.getLogger(CreateWorkOrder.class.getName());

    public CreateProblem(LoginHelper loginHelper)
    {
        this.loginHelper = loginHelper;
    }

    public void run()
    {
        try{
            logger.info("Starting Create WorkOrder Flow");
            boolean loginSuccess = loginHelper.performLogin();
            if (!loginSuccess) {
                logger.severe("Login failed for user: " + loginHelper.getUserName());
                return;
            }
            config = loginHelper.getConfig();
            headers = loginHelper.getHeaders();
            cookies = loginHelper.getCookies();
            csrfToken = loginHelper.getCsrfToken();

             // Step 1: Draft Problem 
            String draftUrl = config.get("SmartIT_url") + "/smartit/rest/problem/draft";
            Map<String, String> headers = new HashMap<>();
            headers.put("Referer", config.get("SmartIT_url") + "/smartit/app/");
            headers.put("X-XSRF-TOKEN", csrfToken);

            Response draftResponse = RestAssured.given().baseUri(draftUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(draftResponse.getStatusCode(), 200, "The Create Problem draft process has failed for user: " + loginHelper.getUserName());
            String draftResp = draftResponse.getBody().asString();

            String problemId = null;
            String displayId = null;
            try {
                if (draftResp.trim().startsWith("[")) {
                    org.json.JSONArray draftArr = new org.json.JSONArray(draftResp);
                    if (draftArr.length() > 0) {
                        JSONObject draftJson = draftArr.getJSONObject(0);
                        org.json.JSONArray itemsArr = draftJson.optJSONArray("items");
                        if (itemsArr != null && itemsArr.length() > 0) {
                            JSONObject item = itemsArr.getJSONObject(0);
                            displayId = item.optString("displayId", null);
                            problemId = item.optString("id", null);
                            logger.info("Extracted displayId: " + displayId + ", id: " + problemId);
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
                        problemId = item.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + problemId + " for user: " + loginHelper.getUserName());
                    } else {
                        displayId = draftJson.optString("displayId", null);
                        problemId = draftJson.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + problemId + " for user: " + loginHelper.getUserName());
                    }
                }
            } catch (JSONException e) {
                logger.severe("Failed to parse draft response: " + e.getMessage());
                return;
            }
            if (problemId == null || displayId == null) {
                logger.warning("No id/displayId in draft response");
                return;
            }

            // Step 2: Read payload from JSON file and replace placeholders
            String payloadPath = "resources" + File.separator + "SMARTIT" + File.separator + "payloads" + File.separator + "SmartIT_create_problem_payload.json";
            String createPayload = new String(Files.readAllBytes(Paths.get(payloadPath)));
            createPayload = createPayload.replace("${id}", problemId).replace("${displayId}", displayId);

            String createUrl = config.get("SmartIT_url") + "/smartit/rest/problem";
            Response createProblemResponse = RestAssured.given().baseUri(createUrl).body(createPayload.toString()).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(createProblemResponse.getStatusCode(), 200, "The Create Problem process has failed for user: " + loginHelper.getUserName());
        }
        catch (Exception e) {
            logger.severe("Exception in Create WorkOrder: " + e.getMessage() + " for user: " + loginHelper.getUserName());
            e.printStackTrace();
        }
    }

    
}
