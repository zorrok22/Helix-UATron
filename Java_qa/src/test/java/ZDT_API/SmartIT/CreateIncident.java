package ZDT_API.SmartIT;

import static org.testng.Assert.assertEquals;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;
import org.json.JSONObject;
import org.json.JSONException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CreateIncident {

    private String csrfToken;
    Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    LoginHelper loginHelper;
    private static final Logger logger = Logger.getLogger(CreateIncident.class.getName());

    public CreateIncident(LoginHelper loginHelper) {
        this.loginHelper = loginHelper;
    }

    public void run()
    {
        try{
            logger.info("Starting Create Incident Flow");
            
            boolean loginSuccess = loginHelper.performLogin();
            if (!loginSuccess) {
                logger.severe("Login failed");
                return;
            }
            config = loginHelper.getConfig();
            headers = loginHelper.getHeaders();
            cookies = loginHelper.getCookies();
            csrfToken = loginHelper.getCsrfToken();

            // Step 1: Create Incident Draft
            String draftUrl = config.get("SmartIT_URL") + "/smartit/rest/v3/incident/draft";
            headers.put("X-XSRF-TOKEN", csrfToken);
            Response draftResponse = RestAssured.given().baseUri(draftUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(draftResponse.getStatusCode(), 200, "The Create Incident Draft operation is not successful for user: " + loginHelper.getUserName());
            logger.info("Create Incident draft validation successful for user: " + loginHelper.getUserName());
            String draftResp = draftResponse.getBody().asString();
            loginHelper.updateCookies(draftResponse);
            // Extract displayId and id
            String displayId = null, incidentId = null;
            try {
                if (draftResp.trim().startsWith("[")) {
                    org.json.JSONArray draftArr = new org.json.JSONArray(draftResp);
                    if (draftArr.length() > 0) {
                        JSONObject draftJson = draftArr.getJSONObject(0);
                        org.json.JSONArray itemsArr = draftJson.optJSONArray("items");
                        if (itemsArr != null && itemsArr.length() > 0) {
                            JSONObject item = itemsArr.getJSONObject(0);
                            displayId = item.optString("displayId", null);
                            incidentId = item.optString("id", null);
                            logger.info("Extracted displayId: " + displayId + ", id: " + incidentId);
                        } else {
                            logger.warning("'items' array is missing or empty in draft response.");
                            return;
                        }
                    } else {
                        logger.warning("Incident draft array is empty.");
                        return;
                    }
                } else {
                    JSONObject draftJson = new JSONObject(draftResp);
                    org.json.JSONArray itemsArr = draftJson.optJSONArray("items");
                    if (itemsArr != null && itemsArr.length() > 0) {
                        JSONObject item = itemsArr.getJSONObject(0);
                        displayId = item.optString("displayId", null);
                        incidentId = item.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + incidentId + " for user: " + loginHelper.getUserName());
                    } else {
                        displayId = draftJson.optString("displayId", null);
                        incidentId = draftJson.optString("id", null);
                        logger.info("Extracted displayId: " + displayId + ", id: " + incidentId + " for user: " + loginHelper.getUserName());
                    }
                }
            } catch (JSONException e) {
                logger.severe("Failed to extract displayId/id: " + e.getMessage());
                return;
            }

            // Step 2: Create Incident
            String createIncidentUrl = config.get("SmartIT_URL") + "/smartit/rest/v2/incident";
            String payloadPath = "resources" + File.separator + "SMARTIT" + File.separator + "payloads" + File.separator + "SmartIT_create_incident_payload.json";
            StringBuilder payloadBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(payloadPath), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                payloadBuilder.append(line);
            }
            br.close();
            String payloadTemplate = payloadBuilder.toString();
            String payloadStr = payloadTemplate.replace("${id}", incidentId != null ? incidentId : "")
                                               .replace("${displayId}", displayId != null ? displayId : "");
            JSONObject incidentPayload = new JSONObject(payloadStr);
            incidentPayload.put("displayId", displayId);
            incidentPayload.put("id", incidentId);
            Response createIncidentResponse = RestAssured.given().baseUri(createIncidentUrl).headers(headers).body(incidentPayload.toString()).cookies(cookies).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(createIncidentResponse.getStatusCode(), 200, "The Create Incident Process Failed for user: " + loginHelper.getUserName());
            logger.info("Creation Incident validation successful for user: " + loginHelper.getUserName());
        }
        catch (Exception e) {
            logger.severe("Exception in CreateIncident: " + e.getMessage() + " for user: " + loginHelper.getUserName());
            e.printStackTrace();
        }
    }
}
