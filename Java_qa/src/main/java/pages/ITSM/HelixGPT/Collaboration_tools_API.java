package pages.ITSM.HelixGPT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Collaboration_tools_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(Collaboration_tools_API.class.getName());
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private String collaborationToolsId;

    public Collaboration_tools_API()
    {
        config = configProp;
    }

    public void getCollaborationToolsId()
    {
        cookies = login.loginAction();
        String getCollaborationToolsIdUrl = config.get("IS_URL") + "/api/rx/application/datapage";
        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Get_Connection_Details.json";
        String bodyJson = "";
        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${1}", "Toolkit");
        }
        catch (Exception e) {
            logger.severe("Exception in Collaboration Tools: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification getCollaborationToolsIdRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response collaborationToolsIdResponse = getCollaborationToolsIdRequest.post(getCollaborationToolsIdUrl);
        String jsonResponse = collaborationToolsIdResponse.getBody().asString();
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataList = jsonPath.getList("data");
            
            for (Map<String, Object> record : dataList) {
                if (record.get("8") != null && 
                    record.get("8").toString().equalsIgnoreCase("Collaboration Tools")) {
                    collaborationToolsId = record.get("379").toString();
                    logger.info("Found field 379 for type '" + "COLLABORATION_TOOLS" + "': " + collaborationToolsId);
                }
            }
            
            logger.warning("No record found with 490000150 = " + "COLLABORATION_TOOLS");
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response for type '" + "COLLABORATION_TOOLS" + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void toggleEnableStatus(String state)
    {
        String toggleEnableStatusUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance/com.bmc.dsom.hgm:Toolkit/" + collaborationToolsId;

        String payloadPath = "Java_qa" + File.separator + "resources" + File.separator + "ITSM" + File.separator + "HelixGPT" + File.separator + "Toggle_XMatter_PagerDuty_Settings.json";
        String bodyJson = "";
        boolean xmatterStatus = false, pagerdutyStatus = false;

        if(state.equalsIgnoreCase("xmatter"))
        {
            xmatterStatus = true;
        }
        else{
            pagerdutyStatus = true;
        }

        try{
            bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson = bodyJson.replace("${0}", collaborationToolsId);
            bodyJson = bodyJson.replace("${1}", "" +xmatterStatus);
            bodyJson = bodyJson.replace("${2}", "" + pagerdutyStatus);
        }
        catch (Exception e) {
            logger.severe("Exception in Collaboration Tools: " + e.getMessage());
            e.printStackTrace();
        }

        RequestSpecification toggleStatusRequest = RestAssured.given()
        .relaxedHTTPSValidation() // to skip SSL verification like curl -k
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .cookies(cookies)
        .body(bodyJson);

        Response toggleStatusResponse = toggleStatusRequest.put(toggleEnableStatusUrl);
        assertEquals(toggleStatusResponse.getStatusCode(), 204, "The Toggle Collaboration Tools Status process is not successful");
    }
    
}
