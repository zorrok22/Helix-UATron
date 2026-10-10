package ZDT_API.DWP;

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

public class IncidentManagement {

    private DWPLogin login;
    private static final Logger logger = Logger.getLogger(IncidentManagement.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String incidentId;

    public IncidentManagement(DWPLogin login) {
        this.login = login;
        config = login.getConfig();
    }


    public String searchIncidentDetails(String parameter)
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        String searchIncidentByStatusPayloadPath="";
        String searchIncidentByStatusPayload = "";
        String searchIncidentUrl = config.get("DWP_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/incident/search";
        if(parameter.equalsIgnoreCase("status"))
        {
            searchIncidentByStatusPayloadPath = "resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Search_Incident_By_Status.json";
        }
        else
        {
            searchIncidentByStatusPayloadPath = "resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Search_Incident_By_User.json";
        }
        try{
            searchIncidentByStatusPayload = new String(Files.readAllBytes(Paths.get(searchIncidentByStatusPayloadPath)));
        }
        catch(Exception e){
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response searchIncidentResponse = RestAssured.given().baseUri(searchIncidentUrl).queryParam("filter", "My Group Tickets").headers(headers).cookies(cookies).body(searchIncidentByStatusPayload).contentType(ContentType.JSON).when().post().then().extract().response();
        logger.info("Search Incident By Status Response Code: " + searchIncidentResponse.getStatusCode());
        assertEquals(searchIncidentResponse.getStatusCode(), 200, "The Login is not successful for user: " + login.getUserName());

        // Parse JSON response to extract incident ID
        JsonPath jsonPath = new JsonPath(searchIncidentResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        incidentId = jsonPath.getString("details[0].id");
        assertNotNull(incidentId, "The Incident Id is not found for user: " + login.getUserName());
        logger.info("Extracted Incident ID: " + incidentId + " for user: " + login.getUserName());
        return incidentId;
        
    }

    public String searchIncidentById()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        if (incidentId == null || incidentId.isEmpty()) {
            logger.warning("The incident Id is not available for user: " + login.getUserName());
            return "";
        }
        String searchIncidentByIdUrl = config.get("DWP_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/incident/" + incidentId;
        Response searchIncidentResponse = RestAssured.given().baseUri(searchIncidentByIdUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
        assertEquals(searchIncidentResponse.getStatusCode(), 200, "The Incident respone is not available");

        JsonPath jsonPath = new JsonPath(searchIncidentResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        String id = jsonPath.getString("details.id");
        assertEquals(incidentId, id,"The Incident Id is not found for user: " + login.getUserName());
        logger.info("The Incident Id is " + incidentId + " for user: " + login.getUserName());
        return incidentId;

    }

    public void modifyIncidentDetails()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        if (incidentId == null || incidentId.isEmpty()) {
            logger.warning("The incident Id is not available for user: " + login.getUserName());
            return;
        }
        String modifyIncidentUrl = config.get("DWP_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/incident/" + incidentId;
        String modifyIncidentPayloadPath="resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Modify_Incident.json";
        String modifyIncidentPayload="";
        try{
            modifyIncidentPayload = new String(Files.readAllBytes(Paths.get(modifyIncidentPayloadPath)));
        }
        catch(Exception e)
        {
            logger.warning("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response modifyIncidentResponse = RestAssured.given().baseUri(modifyIncidentUrl).headers(headers).cookies(cookies).body(modifyIncidentPayload).contentType(ContentType.JSON).when().patch().then().extract().response();

        assertEquals(modifyIncidentResponse.getStatusCode(), 200, "The Modify incident is not successful");

        JsonPath jsonPath = new JsonPath(modifyIncidentResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        String id = jsonPath.getString("details.id");
        assertEquals(incidentId, id,"The Incident Id does not match for user: " + login.getUserName());
        logger.info("The Incident Id is " + incidentId + " for user: " + login.getUserName());
    }

    public void createIncident()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        String createIncidentUrl = config.get("DWP_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/incident";
        String createIncidentPayloadPath = "resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Create_Incident.json";
        String createIncidentPayload = "";
        try{
            createIncidentPayload = new String(Files.readAllBytes(Paths.get(createIncidentPayloadPath)));
        }
        catch(Exception e)
        {
            logger.warning("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response createIncidentResponse = RestAssured.given().baseUri(createIncidentUrl).headers(headers).cookies(cookies).body(createIncidentPayload).contentType(ContentType.JSON).when().post().then().extract().response();
        assertEquals(createIncidentResponse.getStatusCode(), 201, "The Create incident is not successful for user: " + login.getUserName());

        JsonPath jsonPath = new JsonPath(createIncidentResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
         incidentId = jsonPath.getString("details.id");
         assertNotNull(incidentId, "The Incident creation failed for user: " + login.getUserName());
         logger.info("Created Incident ID: " + incidentId + " for user: " + login.getUserName());
        
    }
}
