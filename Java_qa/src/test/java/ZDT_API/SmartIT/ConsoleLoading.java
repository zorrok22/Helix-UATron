package ZDT_API.SmartIT;

import java.util.Map;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.logging.Logger;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class ConsoleLoading {

    private LoginHelper loginHelper;
    Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    private String csrfToken;
    private static final String USERNAME = "1234";
    private static final Logger logger = Logger.getLogger(CreateWorkOrder.class.getName());

    public ConsoleLoading(LoginHelper loginHelper) {
        this.loginHelper = loginHelper;
    }

    public void run(){
        try{
            logger.info("Starting Console Loading Flow");
            boolean loginSuccess = loginHelper.performLogin();
            if (!loginSuccess) {
                logger.severe("Login failed");
                return;
            }
            config = loginHelper.getConfig();
            headers = loginHelper.getHeaders();
            cookies = loginHelper.getCookies();
            csrfToken = loginHelper.getCsrfToken();

            headers.put("Referer", config.get("SmartIT_url") + "/smartit/app/");
            headers.put("X-XSRF-TOKEN", csrfToken);

            String baseUrl = config.get("SmartIT_url");
            // 1. /smartit/rest/v2/preference/details/search?clientType=UC&preferenceGroup=TicketConsoleColumns&user=${username}
            Response response1 = RestAssured.given().baseUri(baseUrl + "/smartit/rest/v2/preference/details/search?clientType=UC&preferenceGroup=TicketConsoleColumns&user=" + USERNAME).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(response1.getStatusCode(), 200, "The First Response has failed for user: " + loginHelper.getUserName());
            logger.info("Response 1 validation successful");
            // 2. /smartit/rest/v2/foundation/consolecolumn/available/search
            Response response2 = RestAssured.given().baseUri(baseUrl + "/smartit/rest/v2/foundation/consolecolumn/available/search").headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(response2.getStatusCode(), 200, "The Second Response has failed for user: " + loginHelper.getUserName());
            logger.info("Response 2 validation successful");
            // 3. /smartit/rest/v2/metadata?type=task&type=change&type=problem&type=knownerror&type=release
            Response response3 = RestAssured.given().baseUri(baseUrl + "/smartit/rest/v2/metadata?type=task&type=change&type=problem&type=knownerror&type=release").headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(response3.getStatusCode(), 200, "The Third Response has failed for user: " + loginHelper.getUserName());
            logger.info("Response 3 validation successful");
            // 4. /smartit/rest/v2/preference/details/search?clientType=UC&preferenceGroup=TicketConsoleFilter&user=${username}
            Response response4 = RestAssured.given().baseUri(baseUrl + "/smartit/rest/v2/preference/details/search?clientType=UC&preferenceGroup=TicketConsoleFilter&user=" + USERNAME).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(response4.getStatusCode(), 200, "The Fourth Response has failed for user: " + loginHelper.getUserName());
            logger.info("Response 4 validation successful");

             // 5. /smartit/rest/v2/foundation/mtcstats/get (POST)
            long nowMillis = System.currentTimeMillis();
            long nowSeconds = nowMillis / 1000L;
            long startTimeSeconds = nowSeconds - 2592000L;

            org.json.JSONObject filterCriteria = new org.json.JSONObject();
            org.json.JSONArray assignees = new org.json.JSONArray();
            assignees.put(new org.json.JSONObject().put("loginId", USERNAME));
            filterCriteria.put("assignees", assignees);
            filterCriteria.put("statusMappings", new org.json.JSONArray().put("open"));
            org.json.JSONArray createDateRanges = new org.json.JSONArray();
            createDateRanges.put(new org.json.JSONObject().put("start", startTimeSeconds).put("end", nowMillis));
            filterCriteria.put("createDateRanges", createDateRanges);

            org.json.JSONArray stats = new org.json.JSONArray();
            stats.put(new org.json.JSONObject().put("name", "mytickets"));
            stats.put(new org.json.JSONObject().put("name", "Critical"));
            stats.put(new org.json.JSONObject().put("name", "new"));
            stats.put(new org.json.JSONObject().put("name", "open"));
            stats.put(new org.json.JSONObject().put("name", "total"));
            stats.put(new org.json.JSONObject().put("name", "securitytickets"));

            org.json.JSONObject postPayload = new org.json.JSONObject();
            postPayload.put("filterCriteria", filterCriteria);
            postPayload.put("stats", stats);

            Response response5 = RestAssured.given().baseUri(baseUrl + "/smartit/rest/v2/foundation/mtcstats/get").headers(headers).cookies(cookies).contentType(ContentType.JSON).body(postPayload.toString()).when().post().then().extract().response();
            assertEquals(response5.getStatusCode(), 200, "The Fifth Response has failed for user: " + loginHelper.getUserName());
            logger.info("Response 5 validation successful");
            // 6. /smartit/rest/v2/person/workitems/get (POST)
            org.json.JSONObject chunkInfo = new org.json.JSONObject();
            chunkInfo.put("startIndex", 0);
            chunkInfo.put("chunkSize", 75);

            org.json.JSONObject sortInfo = new org.json.JSONObject(); // empty object

            org.json.JSONArray attributeNames = new org.json.JSONArray();
            attributeNames.put("slaStatus");
            attributeNames.put("targetDate");
            attributeNames.put("priority");
            attributeNames.put("customerName");
            attributeNames.put("id");
            attributeNames.put("summary");
            attributeNames.put("lastModifiedDate");
            attributeNames.put("assignee");
            attributeNames.put("status");

            org.json.JSONArray customAttributeNames = new org.json.JSONArray(); // empty array

            org.json.JSONObject postPayload6 = new org.json.JSONObject();
            postPayload6.put("filterCriteria", filterCriteria); // reuse from above
            postPayload6.put("chunkInfo", chunkInfo);
            postPayload6.put("sortInfo", sortInfo);
            postPayload6.put("attributeNames", attributeNames);
            postPayload6.put("customAttributeNames", customAttributeNames);

            Response response6 = RestAssured.given().baseUri(baseUrl + "/smartit/rest/v2/person/workitems/get").headers(headers).cookies(cookies).body(postPayload6.toString()).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(response6.getStatusCode(), 200, "The Final Response has failed for user: " + loginHelper.getUserName());
            logger.info("Response 6 validation successful");

        }
        catch (Exception e) {
            logger.severe("Exception in Create WorkOrder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
