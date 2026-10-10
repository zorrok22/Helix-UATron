package pages.ITSM.HelixGPT;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

import framework.Launcher;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class WebSource_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(WebSource_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String webSourceId;
    private String webDataSourceId;

    public WebSource_API()
    {
        config = configProp;
    }

    public void getWebsourceId()
    {
        cookies = login.loginAction();
        String getWebsourceIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Connection_Web");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getWebsourceIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response websourceIdResposne = getWebsourceIdRequest.post(getWebsourceIdUrl);
        assertEquals(websourceIdResposne.getStatusCode(), 200, "The get WebSource Id request is not successful");

        JsonPath WebsourceIdPath = new JsonPath(websourceIdResposne.getBody().asString());
        webSourceId = WebsourceIdPath.getString("data[0].379");

        assertNotNull(webSourceId, "The WebSource Id is not available");
    }

    public void updateWebsourceStatus(String status)
    {
        String updateWebsourceStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_Web/" + webSourceId;
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Update_Connection_Status.json";
        String bodyJson = "", statusCode;
        if(status.equalsIgnoreCase("enable"))
        {
            statusCode =Integer.toString(0);
        }
        else
        {
            statusCode=Integer.toString(10);
        }
        Instant now = Instant.now();
        Instant truncated =  now.truncatedTo(ChronoUnit.SECONDS);
        String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneOffset.UTC)
        .format(truncated);
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", webSourceId).replace("${1}", "Web").replace("${2}", formatted).replace("\"${3}\"", statusCode);

        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification updateWebsourceSettingsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updateWebsourceStatusResposne = updateWebsourceSettingsRequest.put(updateWebsourceStatusUrl);
        assertEquals(updateWebsourceStatusResposne.getStatusCode(), 204, "The Update WebSource Status process is not successful");
    }

    public void verifyWebsourceStatusDetails(String status)
    {
        String getWebsourceDetailsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_Web/" + webSourceId;

        RequestSpecification getWebsourceDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response websourceDetailsResposne = getWebsourceDetailsRequest.get(getWebsourceDetailsUrl);
        assertEquals(websourceDetailsResposne.getStatusCode(), 200, "The get WebSource Details request is not successful");

        JsonPath websourceDetailsPath = new JsonPath(websourceDetailsResposne.getBody().asString());
        String id = websourceDetailsPath.getString("id");

        assertEquals(webSourceId, id, "The Id in response does not match");

        String statusValue = websourceDetailsPath.getString("fieldInstances.7.value");
        int statusCode;

        if(status.equalsIgnoreCase("enable"))
        {
            statusCode = 0;
        }
        else
        {
            statusCode = 10;
        }
        assertEquals(Integer.parseInt(statusValue), statusCode, "The Status of WebSource does not match");
    }

    public void getWebsourceDataSourceId()
    {
        cookies = login.loginAction();
        String getWebsourceDataSourceIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_DataSource_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
        }
        catch (Exception e) {
            logger.severe("Exception in getting WebSource DataSource Id: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getWebsourceDataSourceIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response websourceDataSourceIdResponse = getWebsourceDataSourceIdRequest.post(getWebsourceDataSourceIdUrl);
        assertEquals(websourceDataSourceIdResponse.getStatusCode(), 200, "The get WebSource DataSource Id request is not successful");

        webDataSourceId = Utils.getDataSourceIdByDataSourceName(websourceDataSourceIdResponse.getBody().asString(), "Web", logger);
        logger.info("Data Source ID for Web: " + webDataSourceId);

        assertNotNull(webDataSourceId, "The WebSource DataSource Id is not available");
    }

    public void createWebsourceConnection(String connectionName)
    {
        String createWebsourceConnectionUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/connection/WEB";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Create_Web_Source_Connection.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", connectionName).replace("${1}", webDataSourceId);
        }
        catch (Exception e) {
        logger.severe("Exception in creating WebSource Connection: " + e.getMessage());
        e.printStackTrace();
        }

        RequestSpecification createWebsourceConnectionRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response createWebsourceConnectionResponse = createWebsourceConnectionRequest.post(createWebsourceConnectionUrl);
        assertEquals(createWebsourceConnectionResponse.getStatusCode(), 200, "The create WebSource Connection request is not successful");

        JsonPath createWebsourceConnectionPath = new JsonPath(createWebsourceConnectionResponse.getBody().asString());
        String createdConnectionId = createWebsourceConnectionPath.getString("name");

        assertNotNull(createdConnectionId, "The created WebSource Connection Id is not available");
        logger.info("WebSource Connection created successfully with ID: " + createdConnectionId);
    }
}