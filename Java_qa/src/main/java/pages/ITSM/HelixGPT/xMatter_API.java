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

public class xMatter_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(xMatter_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String xMatterId;

    public xMatter_API()
    {
        config = configProp;
    }

    public void getXmatterID()
    {
        cookies = login.loginAction();
        String getXMatterIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Connection_xMatters");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getXMatterIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response xMatterIdResposne = getXMatterIdRequest.post(getXMatterIdUrl);
        assertEquals(xMatterIdResposne.getStatusCode(), 200, "The get XMatter Id request is not successful");

        JsonPath xMatterIdPath = new JsonPath(xMatterIdResposne.getBody().asString());
        xMatterId = xMatterIdPath.getString("data[0].379");

        assertNotNull(xMatterId, "The XMatter Id is not available");

    }

    public void updateXMatterStatus(String status)
    {
        String updateXMAtterStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_xMatters/" + xMatterId;
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
            bodyJson = bodyJson.replace("${0}", xMatterId).replace("${1}", "xMatters").replace("${2}", formatted).replace("\"${3}\"", statusCode);

        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification updateXMatterSettingsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updateXMatterStatusResposne = updateXMatterSettingsRequest.put(updateXMAtterStatusUrl);
        assertEquals(updateXMatterStatusResposne.getStatusCode(), 204, "The Update XMatter Status process is not successful");
    }

    public void verifyXMatterStatusDetails(String status)
    {
        String getXMatterDetailsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_xMatters/" + xMatterId;

        RequestSpecification getXMatterDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response xMatterDetailsResposne = getXMatterDetailsRequest.get(getXMatterDetailsUrl);
        assertEquals(xMatterDetailsResposne.getStatusCode(), 200, "The get XMatter Details request is not successful");

        JsonPath xMatterDetailsPath = new JsonPath(xMatterDetailsResposne.getBody().asString());
        String id = xMatterDetailsPath.getString("id");

        assertEquals(xMatterId, id, "The Id in response does not match");

        String statusValue = xMatterDetailsPath.getString("fieldInstances.7.value");
        int statusCode;

        if(status.equalsIgnoreCase("enable"))
        {
            statusCode = 0;
        }
        else
        {
            statusCode = 10;
        }

        assertEquals(Integer.parseInt(statusValue), statusCode, "The Status of XMatter does not match");
    }    
}
