package pages.ITSM.HelixGPT;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;
import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.testng.Assert.assertEquals;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Collabarator_AIOps_Chat_Agent_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(Collabarator_AIOps_Chat_Agent_API.class.getName());
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String collabaratorSkillId, aiopsChatAgentId, subAgents; 

    public Collabarator_AIOps_Chat_Agent_API() 
    {
        config = configProp;
    }

    public static void main(String[] args)
    {
        Collabarator_AIOps_Chat_Agent_API collabarator_AIOps_Chat_Agent_API = new Collabarator_AIOps_Chat_Agent_API();
        collabarator_AIOps_Chat_Agent_API.getCollabaratorSkillId();
        collabarator_AIOps_Chat_Agent_API.getAIOpsChatAgentId();
        collabarator_AIOps_Chat_Agent_API.getSubAgentsValue();
        collabarator_AIOps_Chat_Agent_API.updateAIOpsChatAgent();
    }

    public void getCollabaratorSkillId()
    {
        cookies = login.loginAction();
        String getCollabaratorSkillIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Agent");
        }
        catch(Exception e)
        {
            logger.severe("Exception in Get Collabarator Skill ID: " + e.getMessage());
        }

        RequestSpecification getCollabaratorSkillIdRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response getCollabaratorSkillIdResponse = getCollabaratorSkillIdRequest.post(getCollabaratorSkillIdUrl);
        assertEquals(getCollabaratorSkillIdResponse.getStatusCode(), 200, "The Get Collabarator Skill ID is not successful");
        String agentName = "Collaboration Supervisor";
        /**
        * Parse JSON and get value of field 379 when field 8 matches the given agent name
        */
        try {
            JsonPath jsonPath = new JsonPath(getCollabaratorSkillIdResponse.getBody().asString());
            
            // Extract data array
            List<Map<String, Object>> dataList = jsonPath.getList("data");
            
            // Search for agent with matching name in field 8
            for (Map<String, Object> agent : dataList) {
                String currentAgentName = (String) agent.get("8");
                
                if (agentName.equalsIgnoreCase(currentAgentName)) {
                    collabaratorSkillId = (String) agent.get("379");
                    logger.info("Found agent '" + agentName + "' with field 379 value: " + collabaratorSkillId);
                    
                }
            }
            logger.warning("Agent '" + agentName + "' not found in the data"); 
        } catch (Exception e) {
            logger.severe("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getAIOpsChatAgentId()
    {
        String getAIOpsChatAgentIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Agent");
        }
        catch(Exception e)
        {
            logger.severe("Exception in Get AIOps Chat Agent ID: " + e.getMessage());
        }

        RequestSpecification getAIOpsChatAgentIdRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response getAIOpsChatAgentIdResponse = getAIOpsChatAgentIdRequest.post(getAIOpsChatAgentIdUrl);
        assertEquals(getAIOpsChatAgentIdResponse.getStatusCode(), 200, "The Get AIOps Chat Agent ID is not successful");
        String agentName = "AIOps Chat Agent";
        /**
        * Parse JSON and get value of field 379 when field 8 matches the given agent name
        */
        try {
                JsonPath jsonPath = new JsonPath(getAIOpsChatAgentIdResponse.getBody().asString());
                
                // Extract data array
                List<Map<String, Object>> dataList = jsonPath.getList("data");
                
                // Search for agent with matching name in field 8
                for (Map<String, Object> agent : dataList) {
                    String currentAgentName = (String) agent.get("8");
                    
                    if (agentName.equalsIgnoreCase(currentAgentName)) {
                        aiopsChatAgentId = (String) agent.get("379");
                        logger.info("Found agent '" + agentName + "' with field 379 value: " + aiopsChatAgentId);
                        
                    }
                }
                logger.warning("Agent '" + agentName + "' not found in the data");
        }
        catch(Exception e)
        {
            logger.severe("Exception in Get AIOps Chat Agent ID: " + e.getMessage());
        }
    }

    public void getSubAgentsValue()
    {
        String getSubAgentsValueUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Agent/" + collabaratorSkillId;

        RequestSpecification getSubAgentsValueRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response getSubAgentsValueResponse = getSubAgentsValueRequest.get(getSubAgentsValueUrl);
        assertEquals(getSubAgentsValueResponse.getStatusCode(), 200, "The Get Sub Agents Value is not successful");

        try {
            JsonPath jsonPath = new JsonPath(getSubAgentsValueResponse.getBody().asString());
            
            // Navigate to fieldInstances.490000157.value
            subAgents = jsonPath.getString("fieldInstances.490000157.value");
            
            if (subAgents != null) {
                logger.info("Successfully extracted field 490000157 value: " + subAgents);
            } else {
                logger.warning("Field 490000157 not found or has null value");
            }
            
        } catch (Exception e) {
            logger.severe("Error extracting field 490000157 value: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void updateAIOpsChatAgent()
    {
        String updateAIOpsChatAgentUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Agent/" + collabaratorSkillId;

        Instant now = Instant.now();
        Instant truncated =  now.truncatedTo(ChronoUnit.SECONDS);
        String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneOffset.UTC)
        .format(truncated);

        JsonPath arrayPath = new JsonPath(subAgents);
        List<String> agentList = arrayPath.getList("$");
        
        // Add new value to the list
        if (!agentList.contains(aiopsChatAgentId)) {
            agentList.add(aiopsChatAgentId);
        }
        // Convert list to proper JSON array format with quotes around each element
        StringBuilder jsonArrayBuilder = new StringBuilder("[");
        for (int i = 0; i < agentList.size(); i++) {
            jsonArrayBuilder.append("\\\"").append(agentList.get(i)).append("\\\"");
            if (i < agentList.size() - 1) {
                jsonArrayBuilder.append(",");
            }
        }
        jsonArrayBuilder.append("]");
        subAgents = jsonArrayBuilder.toString();
        logger.info("Updated sub agents: " + subAgents);


        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Update_AIOps_Chat_Agent.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", subAgents).replace("${1}", formatted);
        }
        catch(Exception e)
        {
            logger.severe("Exception in Update AIOps Chat Agent: " + e.getMessage());
        }

        RequestSpecification updateAIOpsChatAgentRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updateAIOpsChatAgentResponse = updateAIOpsChatAgentRequest.put(updateAIOpsChatAgentUrl);
        System.out.println(updateAIOpsChatAgentResponse.getBody().asString());
         assertEquals(updateAIOpsChatAgentResponse.getStatusCode(), 204, "The Update AIOps Chat Agent is not successful");
         logger.info("AIOps Chat Agent updated successfully");
     }
}
