package pages.ITSM.HelixGPT;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.testng.Assert.assertEquals;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HVA_Skill_Import_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(HVA_Skill_Import_API.class.getName());
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();

    public HVA_Skill_Import_API()
    {
        config = configProp;
    }

    public static void main(String[] args)
    {
        HVA_Skill_Import_API hvaSkillImportAPI = new HVA_Skill_Import_API();
        hvaSkillImportAPI.importHVASkills();
    }


    public void importHVASkills()
    {
        cookies = login.loginAction();
        String importHVASkillsUrl = config.get("IS_URL") + "/api/com.bmc.dsom.hgm/skill/import";

        String fileName = "Solqa_HVA_Skill_With_gpt_4_1.json";
        // Try module-relative path first (when working dir is repo root)
        java.nio.file.Path payloadPath = Paths.get("Java_qa", "resources", "HelixGPT", fileName);
        if (!Files.exists(payloadPath)) {
            // Fallback for when working dir is Java_qa
            payloadPath = Paths.get("resources", "HelixGPT", fileName);
        }
        if (!Files.exists(payloadPath)) {
            throw new RuntimeException("Import HVA Skills payload file not found at: " + payloadPath.toAbsolutePath());
        }

        String bodyJson;
        try{
            bodyJson = new String(Files.readAllBytes(payloadPath));
        }
        catch(Exception e)
        {
            throw new RuntimeException("Exception in Import HVA Skills while reading payload: " +
                    payloadPath.toAbsolutePath(), e);
        }

        RequestSpecification importHVASkillsRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .header("default-bundle-scope", "com.bmc.dsom.hgm")
        .cookies(cookies)
        .queryParam("overwriteSkill", true)
        .queryParam("overwriteModel", true)
        .queryParam("overwritePrompts", true)
        .queryParam("overwriteAgent", true)
        .queryParam("overwriteTools", true)
        .queryParam("overwriteConnections", true)
        .body(bodyJson);

        Response importHVASkillsResponse = importHVASkillsRequest.post(importHVASkillsUrl);
        int statusCode = importHVASkillsResponse.getStatusCode();

        if (statusCode == 204) {
            logger.info("Successfully imported HVA Skills");
        } else {
            logger.severe("Import HVA Skills failed. Status: " + statusCode +
                    " | Body: " + importHVASkillsResponse.getBody().asString());
            assertEquals(statusCode, 204, "The Import HVA Skills is not successful");
        }
    }
}
