package pages.ITSM.HelixGPT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import org.json.JSONArray;
import org.json.JSONObject;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.testng.Assert.assertEquals;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IS_Curator_Url_Api extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(IS_Curator_Url_Api.class.getName());
    private Map<String, String> cookies = new HashMap<>();
    private LoginRequest_API login = new LoginRequest_API();
    private JSONArray globalComponentSettingsDetails;
    private String ownerId;

    public IS_Curator_Url_Api()
    {
        config = configProp;
    }

    public static void main(String[] args)
    {
        IS_Curator_Url_Api is_Curator_Url_Api = new IS_Curator_Url_Api();
        is_Curator_Url_Api.getGlobalComponentSettingsDetails();
        is_Curator_Url_Api.updateCuratorViewUrlValue();
    }

    public void getGlobalComponentSettingsDetails()
    {
        cookies = login.loginAction();
        String getGlobalComponentSettingsDetailsUrl = config.get("IS_URL") + "/api/rx/application/admin-settings/local/component-settings/Global";

        RequestSpecification getGlobalComponentSettingsDetailsRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .header("default-bundle-scope", "myit-sb")
        .cookies(cookies);

        Response getGlobalComponentSettingsDetailsResponse = getGlobalComponentSettingsDetailsRequest.get(getGlobalComponentSettingsDetailsUrl);
        assertEquals(getGlobalComponentSettingsDetailsResponse.getStatusCode(), 200, "The Get Global Component Settings Details is not successful");
        logger.info("Global Component Settings Details received successfully");
        JsonPath jsonPath = new JsonPath(getGlobalComponentSettingsDetailsResponse.getBody().asString());

        // Extract the values array as List and convert to JSONArray
        List<Map<String, Object>> valuesList = jsonPath.getList("values");
        globalComponentSettingsDetails = new JSONArray(valuesList);

        ownerId = globalComponentSettingsDetails.getJSONObject(0).getString("ownerKeyValue1");
        logger.info("Owner ID Found: " + ownerId);
    }

    public void updateCuratorViewUrlValue()
    {
        String urlValue = "https://is-server-1.example.com/helix/index.html#/myit-sb/view/myit-sb:Catalog%20Service%20Curator";
        for(int i = 0; i < globalComponentSettingsDetails.length(); i++)
        {
            JSONObject componentSettings = globalComponentSettingsDetails.getJSONObject(i);
            if(componentSettings.getString("settingName").equalsIgnoreCase("CATALOG_SERVICE_CURATOR_VIEW_URL"))
            {
                componentSettings.put("settingValue", urlValue); // This WILL update the original
                logger.info("Updated CATALOG_SERVICE_CURATOR_VIEW_URL to: " + urlValue);
                globalComponentSettingsDetails.put(i, componentSettings);
                break; // Exit loop once found
            }
        }
        String updateCuratorViewUrl = config.get("IS_URL") + "/api/rx/application/admin-settings/local/component-settings/Global/" + ownerId;

        RequestSpecification updateCuratorViewUrlRequest = RestAssured.given()
        .relaxedHTTPSValidation()
        .header("Content-Type", "application/json")
        .header("X-Requested-By", "XMLHttpRequest")
        .header("default-bundle-scope", "myit-sb")
        .cookies(cookies)
        .body(globalComponentSettingsDetails.toString());

        Response updateCuratorViewUrlResponse = updateCuratorViewUrlRequest.put(updateCuratorViewUrl);
        assertEquals(updateCuratorViewUrlResponse.getStatusCode(), 200, "The Update Curator View URL Value is not successful");
        logger.info("Curator View URL Value updated successfully");
    }
}
