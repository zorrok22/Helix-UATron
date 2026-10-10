package ZDT_API.SmartIT;

import java.util.Map;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;

import org.json.JSONException;
import org.json.JSONObject;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CreateChange {

    private LoginHelper loginHelper;
    Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    private String csrfToken;
    private static final Logger logger = Logger.getLogger(CreateWorkOrder.class.getName());

    public CreateChange(LoginHelper loginHelper) {
        this.loginHelper = loginHelper;
    }
    
    public void run()
    {
        try{
            logger.info("Starting Create Change Flow");
            boolean loginSuccess = loginHelper.performLogin();
            if (!loginSuccess) {
                logger.severe("Login failed");
                return;
            }
            config = loginHelper.getConfig();
            headers = loginHelper.getHeaders();
            cookies = loginHelper.getCookies();
            csrfToken = loginHelper.getCsrfToken();

            String draftUrl = config.get("SmartIT_url") + "/smartit/rest/change/draft";

            headers.put("Referer", config.get("SmartIT_url") + "/smartit/app/");
            headers.put("Accept-Language", "en-US,en;q=0.9");
            headers.put("X-XSRF-TOKEN", csrfToken);
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.108 Safari/537.36");
            headers.put("Accept", "application/json, text/plain, */*");

            headers.put("X-XSRF-TOKEN", csrfToken);

            Response draftResponse = RestAssured.given().baseUri(draftUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(draftResponse.getStatusCode(), 200, "The Create Change Draft Process has failed for user: " + loginHelper.getUserName());
            logger.info("Create Change draft validation successful for user: " + loginHelper.getUserName());
            String draftResp = draftResponse.getBody().asString();
            String crqId = null;
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
                            crqId = item.optString("id", null);
                            logger.info("Extracted displayId: " + displayId + ", id: " + crqId);
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
                        crqId = item.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + crqId);
                    } else {
                        displayId = draftJson.optString("displayId", null);
                        crqId = draftJson.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + crqId + " for user: " + loginHelper.getUserName());
                    }
                }
            } catch (JSONException e) {
                logger.severe("Failed to parse draft response: " + e.getMessage());
                return;
            }
            if (crqId == null || displayId == null) {
                logger.warning("No id/displayId in draft response");
                return;
            }

             // Step 2: Read payload from JSON file and replace placeholders
            String payloadPath = "resources" + File.separator + "SMARTIT" + File.separator + "payloads" + File.separator + "SmartIT_create_crq_payload.json";
            String createPayload = new String(Files.readAllBytes(Paths.get(payloadPath)));
            createPayload = createPayload.replace("${id}", crqId).replace("${displayId}", displayId);

            String createUrl = config.get("SmartIT_url") + "/smartit/rest/change";
            Response createChangeResponse = RestAssured.given().baseUri(createUrl).headers(headers).cookies(cookies).body(createPayload.toString()).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(createChangeResponse.getStatusCode(), 200, "The Create Change Request Process has failed for user: " + loginHelper.getUserName());
            logger.info("Create Change validaton successful for user: " + loginHelper.getUserName());
        }
        catch (Exception e) {
            logger.severe("Exception in CreateCRQ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
}
