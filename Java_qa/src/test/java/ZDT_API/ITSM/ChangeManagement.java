package ZDT_API.ITSM;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.*;
import java.util.logging.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;

public class ChangeManagement {

    private ITSMLogin login;
    private static final Logger logger = Logger.getLogger(ChangeManagement.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String changeId;

    public ChangeManagement(ITSMLogin login)
    {
        this.login = login;
        config = login.getConfig();
    }

    public void createChange()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        String createChangeUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/change";
        String createChangePayloadPath = "resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Create_Change.json";
        String createChangePayload = "";
        try{
            createChangePayload = new String(Files.readAllBytes(Paths.get(createChangePayloadPath)));
        }
        catch(Exception e)
        {
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response createChangeResponse = RestAssured.given().baseUri(createChangeUrl).headers(headers).cookies(cookies).body(createChangePayload).contentType(ContentType.JSON).when().post().then().extract().response();
        assertEquals(createChangeResponse.getStatusCode(), 201, "The Create Change is not successful");

        JsonPath jsonPath = new JsonPath(createChangeResponse.getBody().asString());

        changeId = jsonPath.getString("details.id");
        assertNotNull(changeId, "The change creation failed for user: " + login.getUserName());
        logger.info("The Change Request Id is " + changeId + " for user: " + login.getUserName());
    }

    public void searchChangeDetails()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        String searchChangeUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/change/search";
        String searchChangeDetailsPayloadPath="resources"+ File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Search_Change.json";
        String searchChangeDetailsPayload="";
        try{
            searchChangeDetailsPayload = new String(Files.readAllBytes(Paths.get(searchChangeDetailsPayloadPath)));
        }
        catch(Exception e){
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response searchChangeResponse = RestAssured.given().baseUri(searchChangeUrl).queryParam("filter", "My Group Tickets").headers(headers).cookies(cookies).body(searchChangeDetailsPayload).contentType(ContentType.JSON).when().post().then().extract().response();
        assertEquals(searchChangeResponse.getStatusCode(), 200, "The Search change is not successful");
        JsonPath jsonPath = new JsonPath(searchChangeResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        changeId = jsonPath.getString("details[0].id");
        assertNotNull(changeId, "The Change Id is not found for user: " + login.getUserName());
        logger.info("Extracted Incident ID: " + changeId + " for user: " + login.getUserName());
    }

    public void searchChangeDetailsById()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        if (changeId == null || changeId.isEmpty()) {
            logger.warning("The Change Id is not available for user: " + login.getUserName());
            return;
        }
        String searchChangeByIdUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/change/" + changeId;
        Response searchChangeResponse = RestAssured.given().baseUri(searchChangeByIdUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
        assertEquals(searchChangeResponse.getStatusCode(), 200, "The Change respone is not available");

        JsonPath jsonPath = new JsonPath(searchChangeResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        String id = jsonPath.getString("details.id");
        assertEquals(changeId, id,"The Change Id is not found for user: " + login.getUserName());
        logger.info("The Change Request Id is " + changeId + " for user: " + login.getUserName());
    }

    public void modifyChange()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        if (changeId == null || changeId.isEmpty()) {
            logger.warning("The incident Id is not available");
            return;
        }
        String modifyChangeUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/change/" + changeId;
        String modifyChangePayloadPath="resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Modify_Change.json";
        String modifyChangePayload="";
        try{
            modifyChangePayload = new String(Files.readAllBytes(Paths.get(modifyChangePayloadPath)));
        }
        catch(Exception e)
        {
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response modifyChangeResponse = RestAssured.given().baseUri(modifyChangeUrl).headers(headers).cookies(cookies).body(modifyChangePayload).contentType(ContentType.JSON).when().patch().then().extract().response();

        assertEquals(modifyChangeResponse.getStatusCode(), 200, "The Modify incident is not successful");

        JsonPath jsonPath = new JsonPath(modifyChangeResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        String id = jsonPath.getString("details.id");
        assertEquals(changeId, id,"The Incident Id does not match for user: " + login.getUserName());
        logger.info("The Change Request Id is " + changeId + " for user: " + login.getUserName());
    }
    
}
