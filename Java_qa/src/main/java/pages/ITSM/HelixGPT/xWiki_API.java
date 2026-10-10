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

public class xWiki_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(xWiki_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String xWikiId;
    private String xWikiDataSourceId;

    public xWiki_API()
    {
        config = configProp;
    }

    public void getxWikiId()
    {
        cookies = login.loginAction();
        String getxWikiIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Connection_XWiki");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getxWikiIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response xWikiIdResposne = getxWikiIdRequest.post(getxWikiIdUrl);
        assertEquals(xWikiIdResposne.getStatusCode(), 200, "The get xWiki Id request is not successful");

        JsonPath xWikiIdPath = new JsonPath(xWikiIdResposne.getBody().asString());
        xWikiId = xWikiIdPath.getString("data[0].379");

        assertNotNull(xWikiId, "The xWiki Id is not available");
    }

    public void updatexWikiStatus(String status)
    {
        String updatexWikiStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_XWiki/" + xWikiId;
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
            bodyJson = bodyJson.replace("${0}", xWikiId).replace("${1}", "XWiki").replace("${2}", formatted).replace("\"${3}\"", statusCode);

        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification updatexWikiSettingsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updatexWikiStatusResposne = updatexWikiSettingsRequest.put(updatexWikiStatusUrl);
        assertEquals(updatexWikiStatusResposne.getStatusCode(), 204, "The Update xWiki Status process is not successful");
    }

    public void verifyxWikiStatusDetails(String status)
    {
        String getxWikiDetailsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_XWiki/" + xWikiId;

        RequestSpecification getxWikiDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response xWikiDetailsResposne = getxWikiDetailsRequest.get(getxWikiDetailsUrl);
        assertEquals(xWikiDetailsResposne.getStatusCode(), 200, "The get xWiki Details request is not successful");

        JsonPath xWikiDetailsPath = new JsonPath(xWikiDetailsResposne.getBody().asString());
        String id = xWikiDetailsPath.getString("id");

        assertEquals(xWikiId, id, "The Id in response does not match");

        String statusValue = xWikiDetailsPath.getString("fieldInstances.7.value");
        int statusCode;

        if(status.equalsIgnoreCase("enable"))
        {
            statusCode = 0;
        }
        else
        {
            statusCode = 10;
        }

        assertEquals(Integer.parseInt(statusValue), statusCode, "The Status of xWiki does not match");
    }

    public void getxWikiDataSourceId()
    {
        cookies = login.loginAction();
        String xWikiDataSourceIdurl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_DataSource_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
        }
        catch (Exception e) {
            logger.severe("Exception in Sharepoint Data Source ID: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getxWikiDataSourceIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .body(bodyJson)
        .cookies(cookies);

        Response getxWikiDataSourceIdResponse = getxWikiDataSourceIdRequest.post(xWikiDataSourceIdurl);
        assertEquals(getxWikiDataSourceIdResponse.getStatusCode(), 200, "The Data Source details have not been received successfully");

        xWikiDataSourceId = Utils.getDataSourceIdByDataSourceName(getxWikiDataSourceIdResponse.getBody().asString(), "X-Wiki", logger);
        logger.info("Data Source ID for XWiki: " + xWikiDataSourceId);
    }

    public void createxWikiConnection(String connectionName)
    {
        String createSalesforceConnectionUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/connection/XWIKI";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Create_xWiki_Source_Connection.json";
        String bodyJson="";
        String name = connectionName;
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", name).replace("${1}", xWikiDataSourceId);

        }
        catch (Exception e) {
            logger.severe("Exception in Sharepoint Source Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification createSalesforceSourceRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response createSalesforceSourceResponse = createSalesforceSourceRequest.post(createSalesforceConnectionUrl);
        assertEquals(createSalesforceSourceResponse.getStatusCode(), 200, "The Create xWiki Source connection request is not successful");

        JsonPath sharePointSourcePath = new JsonPath(createSalesforceSourceResponse.getBody().asString());
        name = sharePointSourcePath.getString("name");
        String dataSourceId = sharePointSourcePath.getString("dataSourceId");
        assertEquals(name, connectionName, "The Name of connection in response does not match");
        assertEquals(dataSourceId, xWikiDataSourceId, "The Data Source Id in response does not match");
    }  
}


