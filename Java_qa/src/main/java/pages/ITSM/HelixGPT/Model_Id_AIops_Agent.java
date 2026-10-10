package pages.ITSM.HelixGPT;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import com.mongodb.util.JSON;

import framework.Launcher;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import io.restassured.http.ContentType;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Model_Id_AIops_Agent extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(Model_Id_AIops_Agent.class.getName());
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String modelId;
    private List<Object> aiopsAgentsList = new ArrayList<>();

    public Model_Id_AIops_Agent()
    {
        config = configProp;
    }

    public static void main(String[] args)
    {
        Model_Id_AIops_Agent model_Id_AIops_Agent = new Model_Id_AIops_Agent();
        model_Id_AIops_Agent.getModelId(ModelDetails.ModelName.GPT_4_1_AZURE);
        model_Id_AIops_Agent.getAIOpsAgents();
        model_Id_AIops_Agent.updateAIOpsAgents();
    }

    public void getModelId(ModelDetails.ModelName modelName)
    {
        cookies = login.loginAction();
        String getModelIdUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/model/datapage";
       
        RequestSpecification getModelIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("X-Requested-By", "XMLHttpRequest")
        .queryParam("pageSize", -1)
        .queryParam("startIndex", 0)
        .queryParam("propertySelection", "name,id,vendor,seedData,modifiedDate")
        .queryParam("sortBy", "name")
        .contentType(ContentType.URLENC)
        .cookies(cookies);

        Response getModelIdResponse = getModelIdRequest.get(getModelIdUrl);
        assertEquals(getModelIdResponse.getStatusCode(), 200, "The Get Model Id is not successful");
        modelId = ModelDetails.ModelName.getModelIdByName(getModelIdResponse.getBody().asString(), modelName, logger);
        assertNotNull(modelId, "The Model Id is not available");
        logger.info("Model Id for " + modelName.getModelName() + " is: " + modelId);
    }

    public void getAIOpsAgents()
    {
        String getAIOpsAgentsUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Agent");
            
            // Modify propertySelection using string replacement
            bodyJson = bodyJson.replace(
                "\"propertySelection\": \"1,2,3,4,5,6,7,8,16,379,399,490000200,490000201,490000202,490000205,490000206,490000375,490000376,490000377,490000378\"",
                "\"propertySelection\": \"1,2,3,8,379\""
            );
            
            // Add queryExpression field before the closing brace of values object
            bodyJson = bodyJson.replace(
                "\"shouldIncludeTotalSize\": \"false\"",
                "\"shouldIncludeTotalSize\": \"false\",\n      \"queryExpression\": \"(('2' LIKE \\\"%aiops%\\\" OR '8' LIKE \\\"%aiops%\\\" OR '379' LIKE \\\"%aiops%\\\"))\""
            );
            
        }
        catch (Exception e) {
            logger.severe("Exception in Get AIOps Agents: " + e.getMessage());
            e.printStackTrace();
        }
        logger.fine("Modified JSON payload: " + bodyJson); 

        RequestSpecification getAIOpsAgentsRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response getAIOpsAgentsResponse = getAIOpsAgentsRequest.post(getAIOpsAgentsUrl);
        assertEquals(getAIOpsAgentsResponse.getStatusCode(), 200, "The Get AIOps Agents is not successful");

        JsonPath jsonPath = new JsonPath(getAIOpsAgentsResponse.getBody().asString());
        
        // Extract data array as List of Objects (complete JSON objects)
        aiopsAgentsList = jsonPath.getList("data");
        logger.info("Total AIOps Agents found: " + aiopsAgentsList.size());

    }


    public void updateAIOpsAgents()
    {
        Instant now = Instant.now();
        Instant truncated =  now.truncatedTo(ChronoUnit.SECONDS);
        String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneOffset.UTC)
        .format(truncated);
        for(Object agent : aiopsAgentsList)
        {
            // Cast Object to Map to access JSON fields
            Map<String, Object> agentMap = (Map<String, Object>) agent;
            
            String displayId = (String) agentMap.get("1");           // Record ID
            String agentName = (String) agentMap.get("8");         // Agent Name
            String agentTypeId = (String) agentMap.get("379");     // Agent Type ID

            String updateAIOpsAgentsUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Agent/" + agentTypeId;
            String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Update_AIOps_Agents.json";
            String bodyJson = "";
            try{
                bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
                bodyJson = bodyJson.replace("${0}", agentTypeId).replace("${1}", displayId).replace("${2}", formatted).replace("${3}", modelId);
            }
            catch (Exception e) {
                logger.severe("Exception in Update AIOps Agents: " + e.getMessage());
                e.printStackTrace();
            }

            RequestSpecification updateAIOpsAgentsRequest = RestAssured.given()
            .relaxedHTTPSValidation() // to skip SSL verification like curl -k
            .header("Content-Type", "application/json")
            .header("X-Requested-By", "XMLHttpRequest")
            .cookies(cookies)
            .body(bodyJson);

            Response updateAIOpsAgentsResponse = updateAIOpsAgentsRequest.put(updateAIOpsAgentsUrl);
            assertEquals(updateAIOpsAgentsResponse.getStatusCode(), 204, "The Update AIOps Agents is not successful");

            logger.info("Model updated for AIOps Agent: " + agentName);
        }
    }
    
   
}
