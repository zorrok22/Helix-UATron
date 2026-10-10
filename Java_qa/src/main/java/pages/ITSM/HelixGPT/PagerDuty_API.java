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

public class PagerDuty_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(PagerDuty_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String pagerDutyId;

    public PagerDuty_API()
    {
        config = configProp;
    }

    public void getPagerDutyId()
    {
        cookies = login.loginAction();
        String getPagerDutyIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Connection_PagerDuty");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getPagerDutyIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response pagerDutyIdResposne = getPagerDutyIdRequest.post(getPagerDutyIdUrl);
        assertEquals(pagerDutyIdResposne.getStatusCode(), 200, "The get PagerDuty Id request is not successful");

        JsonPath PagerDutyIdPath = new JsonPath(pagerDutyIdResposne.getBody().asString());
        pagerDutyId = PagerDutyIdPath.getString("data[0].379");

        assertNotNull(pagerDutyId, "The PagerDuty Id is not available");
    }

    public void updatePagerDutyStatus(String status)
    {
        String updatePagerDutyStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_PagerDuty/" + pagerDutyId;
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
            bodyJson = bodyJson.replace("${0}", pagerDutyId).replace("${1}", "PagerDuty").replace("${2}", formatted).replace("\"${3}\"", statusCode);

        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification updatePagerDutySettingsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updatePagerDutyStatusResposne = updatePagerDutySettingsRequest.put(updatePagerDutyStatusUrl);
        assertEquals(updatePagerDutyStatusResposne.getStatusCode(), 204, "The Update PagerDuty Status process is not successful");
    }

    public void verifyPagerDutyStatusDetails(String status)
    {
        String getPagerDutyDetailsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Connection_PagerDuty/" + pagerDutyId;

        RequestSpecification getPagerDutyDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response pagerDutyDetailsResposne = getPagerDutyDetailsRequest.get(getPagerDutyDetailsUrl);
        assertEquals(pagerDutyDetailsResposne.getStatusCode(), 200, "The get PagerDuty Details request is not successful");

        JsonPath pagerDutyDetailsPath = new JsonPath(pagerDutyDetailsResposne.getBody().asString());
        String id = pagerDutyDetailsPath.getString("id");

        assertEquals(pagerDutyId, id, "The Id in response does not match");

        String statusValue = pagerDutyDetailsPath.getString("fieldInstances.7.value");
        int statusCode;

        if(status.equalsIgnoreCase("enable"))
        {
            statusCode = 0;
        }
        else
        {
            statusCode = 10;
        }
        assertEquals(Integer.parseInt(statusValue), statusCode, "The Status of PagerDuty does not match");
    }
    
}
