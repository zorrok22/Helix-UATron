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

public class Salesforce_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(Salesforce_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String salesforceId;
    private String salesforceDataSourceId;

    public Salesforce_API()
    {
        config = configProp;
    }

    public void getSalesforceId()
    {
        cookies = login.loginAction();
        String getSalesforceIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Connection_Salesforce");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getSalesforceIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response salesforceIdResposne = getSalesforceIdRequest.post(getSalesforceIdUrl);
        assertEquals(salesforceIdResposne.getStatusCode(), 200, "The get Salesforce Id request is not successful");

        JsonPath SalesforceIdPath = new JsonPath(salesforceIdResposne.getBody().asString());
        salesforceId = SalesforceIdPath.getString("data[0].379");

        assertNotNull(salesforceId, "The Salesforce Id is not available");
    }

    public void updateSalesforceStatus(String status)
    {
        String updateSalesforceStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_Salesforce/" + salesforceId;
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
            bodyJson = bodyJson.replace("${0}", salesforceId).replace("${1}", "Salesforce").replace("${2}", formatted).replace("\"${3}\"", statusCode);

        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification updateSalesforceSettingsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updateSalesforceStatusResposne = updateSalesforceSettingsRequest.put(updateSalesforceStatusUrl);
        assertEquals(updateSalesforceStatusResposne.getStatusCode(), 204, "The Update Salesforce Status process is not successful");
    }

    public void verifySalesforceStatusDetails(String status)
    {
        String getSalesforceDetailsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_Salesforce/" + salesforceId;

        RequestSpecification getSalesforceDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response salesforceDetailsResposne = getSalesforceDetailsRequest.get(getSalesforceDetailsUrl);
        assertEquals(salesforceDetailsResposne.getStatusCode(), 200, "The get Salesforce Details request is not successful");

        JsonPath salesforceDetailsPath = new JsonPath(salesforceDetailsResposne.getBody().asString());
        String id = salesforceDetailsPath.getString("id");

        assertEquals(salesforceId, id, "The Id in response does not match");

        String statusValue = salesforceDetailsPath.getString("fieldInstances.7.value");
        int statusCode;

        if(status.equalsIgnoreCase("enable"))
        {
            statusCode = 0;
        }
        else
        {
            statusCode = 10;
        }

        assertEquals(Integer.parseInt(statusValue), statusCode, "The Status of Salesforce does not match");
    }

    public void getSalesforceDataSourceId()
    {
        cookies = login.loginAction();
        String salesforceDataSourceIdurl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_DataSource_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
        }
        catch (Exception e) {
            logger.severe("Exception in Sharepoint Data Source ID: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getSalesforceDataSourceIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .body(bodyJson)
        .cookies(cookies);

        Response getSalesforceDataSourceIdResponse = getSalesforceDataSourceIdRequest.post(salesforceDataSourceIdurl);
        assertEquals(getSalesforceDataSourceIdResponse.getStatusCode(), 200, "The Data Source details have not been received successfully");

        salesforceDataSourceId = Utils.getDataSourceIdByDataSourceName(getSalesforceDataSourceIdResponse.getBody().asString(), "Salesforce Knowledge", logger);
        logger.info("Data Source ID for SALESFORCE_KNOWLEDGE: " + salesforceDataSourceId);
    }

    public void createSalesforceConnection(String connectionName)
    {
        String createSalesforceConnectionUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/connection/SALESFORCE_KNOWLEDGE";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Create_Salesforce_Source_Connection.json";
        String bodyJson="";
        String name = connectionName;
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", name).replace("${1}", salesforceDataSourceId);

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
        assertEquals(createSalesforceSourceResponse.getStatusCode(), 200, "The Create Sharepoint Source connection request is not successful");

        JsonPath sharePointSourcePath = new JsonPath(createSalesforceSourceResponse.getBody().asString());
        name = sharePointSourcePath.getString("name");
        String dataSourceId = sharePointSourcePath.getString("dataSourceId");
        assertEquals(name, connectionName, "The Name of connection in response does not match");
        assertEquals(dataSourceId, salesforceDataSourceId, "The Data Source Id in response does not match");
    }   
}