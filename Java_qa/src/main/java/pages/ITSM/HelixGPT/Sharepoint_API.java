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

public class Sharepoint_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(Sharepoint_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String sharepointId;
    private String sharePointDataSourceId;

    public Sharepoint_API()
    {
        config = configProp;
    }

    public void getSharepointId()
    {
        cookies = login.loginAction();
        String getSharepointIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Connection_SharePoint");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getSharepointIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response sharepointIdResposne = getSharepointIdRequest.post(getSharepointIdUrl);
        assertEquals(sharepointIdResposne.getStatusCode(), 200, "The get Sharepoint Id request is not successful");

        JsonPath SharepointIdPath = new JsonPath(sharepointIdResposne.getBody().asString());
        sharepointId = SharepointIdPath.getString("data[0].379");

        assertNotNull(sharepointId, "The Sharepoint Id is not available");
    }

    public void updateSharepointStatus(String status)
    {
        String updateSharepointStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_SharePoint/" + sharepointId;
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
            bodyJson = bodyJson.replace("${0}", sharepointId).replace("${1}", "SharePoint").replace("${2}", formatted).replace("\"${3}\"", statusCode);

        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification updateSharepointSettingsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updateSharepointStatusResposne = updateSharepointSettingsRequest.put(updateSharepointStatusUrl);
        assertEquals(updateSharepointStatusResposne.getStatusCode(), 204, "The Update Sharepoint Status process is not successful");
    }

    public void verifySharepointStatusDetails(String status)
    {
        String getSharepointDetailsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_SharePoint/" + sharepointId;

        RequestSpecification getSharepointDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response sharepointDetailsResposne = getSharepointDetailsRequest.get(getSharepointDetailsUrl);
        assertEquals(sharepointDetailsResposne.getStatusCode(), 200, "The get Sharepoint Details request is not successful");

        JsonPath sharepointDetailsPath = new JsonPath(sharepointDetailsResposne.getBody().asString());
        String id = sharepointDetailsPath.getString("id");

        assertEquals(sharepointId, id, "The Id in response does not match");

        String statusValue = sharepointDetailsPath.getString("fieldInstances.7.value");
        int statusCode;

        if(status.equalsIgnoreCase("enable"))
        {
            statusCode = 0;
        }
        else
        {
            statusCode = 10;
        }

        assertEquals(Integer.parseInt(statusValue), statusCode, "The Status of Sharepoint does not match");
    }

    public void getSharepointDataSourceId()
    {
        cookies = login.loginAction();
        String sharePointDataSourceIdurl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_DataSource_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
        }
        catch (Exception e) {
            logger.severe("Exception in Sharepoint Data Source ID: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getSharepointDataSourceIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .body(bodyJson)
        .cookies(cookies);

        Response getSharepointDataSourceIdResponse = getSharepointDataSourceIdRequest.post(sharePointDataSourceIdurl);
        assertEquals(getSharepointDataSourceIdResponse.getStatusCode(), 200, "The Data Source details have not been received successfully");

        sharePointDataSourceId = Utils.getDataSourceIdByDataSourceName(getSharepointDataSourceIdResponse.getBody().asString(), "Microsoft SharePoint Online", logger);
        logger.info("Data Source ID for Microsoft SharePoint Online: " + sharePointDataSourceId);
    }

    public void createSharepointConnection(String connectionName)
    {
        String createSharePointConnectionUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/connection/SPT";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Create_SharePoint_Source_Connection.json";
        String bodyJson="";
        String name = connectionName;
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", name).replace("${1}", sharePointDataSourceId);

        }
        catch (Exception e) {
            logger.severe("Exception in Sharepoint Source Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification createSharePointSourceRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response createShrePointSourceResponse = createSharePointSourceRequest.post(createSharePointConnectionUrl);
        assertEquals(createShrePointSourceResponse.getStatusCode(), 200, "The Create Sharepoint Source connection request is not successful");

        JsonPath sharePointSourcePath = new JsonPath(createShrePointSourceResponse.getBody().asString());
        name = sharePointSourcePath.getString("name");
        String dataSourceId = sharePointSourcePath.getString("dataSourceId");
        assertEquals(name, connectionName, "The Name of connection in response does not match");
        assertEquals(dataSourceId, sharePointDataSourceId, "The Data Source Id in response does not match");
    }
    
    
    
}
