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

public class ProblemManagement {

    private ITSMLogin login;
    private static final Logger logger = Logger.getLogger(IncidentManagement.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String problemId;

    public ProblemManagement(ITSMLogin login)
    {
        this.login = login;
        config = login.getConfig();
    }

    public void createProblem()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        String createProblemUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/problem";
        String createProblemPayloadPath = "resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Create_Problem.json";
        String createProblemPayload = "";
        try{
            createProblemPayload = new String(Files.readAllBytes(Paths.get(createProblemPayloadPath)));
        }
        catch(Exception e)
        {
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response createProblemResponse = RestAssured.given().baseUri(createProblemUrl).headers(headers).cookies(cookies).body(createProblemPayload).contentType(ContentType.JSON).when().post().then().extract().response();
        assertEquals(createProblemResponse.getStatusCode(), 201, "The Create Problem process is not successful");

        JsonPath jsonPath = new JsonPath(createProblemResponse.getBody().asString());

        problemId = jsonPath.getString("details.id");
        assertNotNull(problemId, "The change creation failed for user: " + login.getUserName());
        logger.info("The Problem Id is " + problemId + " for user: " + login.getUserName());
    }

    public String searchProblemDetails()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        String searchProblemUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/problem/search";

        String searchProblemDetailsPayloadPath="resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Search_Change.json";
        String searchProblemDetailsPayload="";
        try{
            searchProblemDetailsPayload = new String(Files.readAllBytes(Paths.get(searchProblemDetailsPayloadPath)));
        }
        catch(Exception e){
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response searchProblemResponse = RestAssured.given().baseUri(searchProblemUrl).queryParam("filter", "My Group Tickets").headers(headers).cookies(cookies).body(searchProblemDetailsPayload).contentType(ContentType.JSON).when().post().then().extract().response();
        assertEquals(searchProblemResponse.getStatusCode(), 200, "The Search Problem process is not successful");
        JsonPath jsonPath = new JsonPath(searchProblemResponse.getBody().asString());

        problemId = jsonPath.getString("details[0].id");
        assertNotNull(problemId, "The Problem Id is not found for user: " + login.getUserName());
        logger.info("Extracted Incident ID: " + problemId + " for user: " + login.getUserName());
        return problemId;

    }

    public String searchProblemById()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        if (problemId == null || problemId.isEmpty()) {
            logger.warning("The Problem Id is not available for user: " + login.getUserName());
            return "";
        }
        String searchProblemByIdUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/problem/" + problemId;
        Response searchProblemResponse = RestAssured.given().baseUri(searchProblemByIdUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
        assertEquals(searchProblemResponse.getStatusCode(), 200, "The Change respone is not available");

        JsonPath jsonPath = new JsonPath(searchProblemResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        String id = jsonPath.getString("details.id");
        assertEquals(problemId, id,"The Change Id is not found for user: " + login.getUserName());
        logger.info("The Problem Id is " + problemId + " for user: " + login.getUserName());
        return problemId;
    }

    public void modifyProblem()
    {
        cookies = login.performLogin();
        headers = login.getHeaders();
        if (problemId == null || problemId.isEmpty()) {
            logger.warning("The Problem Id is not available");
            return;
        }
        String modifyProblemUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/problem/" + problemId;
        String modifyProblemPayloadPath="resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "Modify_Change.json";
        String modifyProblemPayload="";
        try{
            modifyProblemPayload = new String(Files.readAllBytes(Paths.get(modifyProblemPayloadPath)));
        }
        catch(Exception e)
        {
            logger.severe("Exception in SearchIncidentDetailsByCriteria: " + e.getMessage());
            e.printStackTrace();
        }
        Response modifyProblemResponse = RestAssured.given().baseUri(modifyProblemUrl).headers(headers).cookies(cookies).body(modifyProblemPayload).contentType(ContentType.JSON).when().patch().then().extract().response();

        assertEquals(modifyProblemResponse.getStatusCode(), 200, "The Modify Problem process is not successful");

        JsonPath jsonPath = new JsonPath(modifyProblemResponse.getBody().asString());
        
        // Extract the first incident ID from the details array
        String id = jsonPath.getString("details.id");
        assertEquals(problemId, id,"The Problem Id does not match for user: " + login.getUserName());
        logger.info("The Problem Id is " + problemId + " for user: " + login.getUserName());
    }
    
}
