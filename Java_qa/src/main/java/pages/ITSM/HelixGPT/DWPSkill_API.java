package pages.ITSM.HelixGPT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DWPSkill_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(DWPSkill_API.class.getName());
    private Map<String, String> cookies = new HashMap<>();
    private String skillId;

    public DWPSkill_API()
    {
        config = configProp;
        this.updateUrl();
    }

    public void updateUrl()
    {
        config.put("DWP_URL", config.get("DWP_URL").replace("/dwp/app/#/", ""));
    }

    public void logintoDWPAdmin()
    {
        String dwpLoginUrl = config.get("DWP_URL") + "/dwp/restapi/users/sessions/login";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "DWP_Login.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", config.get("ADE_USERNAME"));
            bodyJson = bodyJson.replace("${1}", config.get("ADE_PASSWORD"));
        }
        catch(Exception e)
        {
            logger.severe("Exception in DWP Login: " + e.getMessage());
        }

        RequestSpecification dwpLoginRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .body(bodyJson);

        Response dwpLoginResponse = dwpLoginRequest.post(dwpLoginUrl);
        assertEquals(dwpLoginResponse.getStatusCode(), 200, "The DWP Login is not successful");
        cookies.putAll(dwpLoginResponse.getCookies());
        logger.info("DWP Login Successful and cookies received");
    }

    public void getSkillId(String skillName)
    {
        String getSkillIdUrl = config.get("DWP_URL") + "/dwp/api/v1.0/skills";
        RequestSpecification getSkillIdRequest = RestAssured.given()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response getSkillIdResponse = getSkillIdRequest.get(getSkillIdUrl);
        assertEquals(getSkillIdResponse.getStatusCode(), 200, "The Get Skill Id is not successful");
        logger.info("Get Skill Id Successful and response received");

        skillId = getSkillIdByName(getSkillIdResponse.getBody().asString(), skillName, logger);
        assertNotNull(skillId, "The Skill Id is not available");
        logger.info("Skill Id for " + skillName + " is: " + skillId);
    }

    public void UpdateSkill()
    {
        String updateSkillUrl = config.get("DWP_URL") + "/dwp/rest/settings/single/helixGptPanelSkillId/" + skillId;

        RequestSpecification updateSkillRequest = RestAssured.given()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies);

        Response updateSkillResponse = updateSkillRequest.post(updateSkillUrl);
        assertEquals(updateSkillResponse.getStatusCode(), 204, "The Update Skill is not successful");
        logger.info("Update Skill Successful and response received");
    }

    private static String getSkillIdByName(String jsonResponse, String skillName, Logger logger) {
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> contentList = jsonPath.getList("content");
            
            for (Map<String, Object> skill : contentList) {
                if (skill.get("name") != null && 
                    skill.get("name").toString().equalsIgnoreCase(skillName)) {
                    String skillId = skill.get("id").toString();
                    logger.info("Found skill ID for name '" + skillName + "': " + skillId);
                    return skillId;
                }
            }
            
            logger.warning("No skill found with name: " + skillName);
            return null;
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response for skill name '" + skillName + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
}
