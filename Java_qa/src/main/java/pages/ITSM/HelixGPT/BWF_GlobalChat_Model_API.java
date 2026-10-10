package pages.ITSM.HelixGPT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import framework.Launcher;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import util.SolQAConstants;

public class BWF_GlobalChat_Model_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(xMatter_API.class.getName());
    //private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String modelId, skillId;

    public BWF_GlobalChat_Model_API()
    {
        config = configProp;
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

    public void getGlobalChatSkillId()
    {
        String getGlobalChatSkillIdUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/skill/datapage";

        RequestSpecification getGlobalChatSkillIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("X-Requested-By", "XMLHttpRequest")
        .queryParam("pageSize", -1)
        .queryParam("startIndex", 0)
        .queryParam("propertySelection", "name,id,displayId,seedData")
        .queryParam("sortBy", "name")
        .queryParam("applicationName", SolQAConstants.appBWF)
        .contentType(ContentType.URLENC)
        .cookies(cookies);

        Response getGlobalChatSkillIdResponse = getGlobalChatSkillIdRequest.get(getGlobalChatSkillIdUrl);
        String jsonResponse = getGlobalChatSkillIdResponse.getBody().asString();

        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataList = jsonPath.getList("data");
            
            for (Map<String, Object> skill : dataList) {
                if (skill.get("name") != null && 
                    skill.get("name").toString().equalsIgnoreCase("BWF Global Chat Skill")) {
                    skillId = skill.get("id").toString();
                    logger.info("Found BWF Global Chat Skill ID: " + skillId);
                    
                }
            }
            logger.warning("BWF Global Chat Skill not found in JSON response");  
        } catch (Exception e) {
            logger.severe("Error parsing JSON response for BWF Global Chat Skill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateModelForChatSkill()
    {
        String updateModelForChatSkillUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/skill/" + skillId;
        String bodyJson = "{\"modelId\":\"" + modelId + "\"}";

        RequestSpecification updateModelForChatSkillRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response updateModelForChatSkillResponse = updateModelForChatSkillRequest.put(updateModelForChatSkillUrl);
        assertEquals(updateModelForChatSkillResponse.getStatusCode(), 200, "The Update Model for Chat Skill is not successful");
        logger.info("Update Model for Chat Skill Successful and response received");
    }   
}
