package pages.ITOM.Api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import util.ReadPropertiesFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

/** *********************************************************************
 Purpose:		This class contains common functions for Monitor Blackout Policies using API's
 @author			almahaja
  ********************************************************************** */

public class BlackoutPolicies {
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static String hmHostName = config.get("ADE_PLATFORM_URL");
    public static String hmSearchBlackoutPolicy = "events-service/api/v1.0/blackout_policies/search";
    public static String hmDeleteBlackoutPolicies = "events-service/api/v1.0/blackout_policies/delete";
    public static String hmCreateBlackoutPolicy = "events-service/api/v1.0/blackout_policies";
    // Function to read JSON file and return it's content
    private String readJSon(String jsonFileName){
        try {
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "Monitor"+File.separator+jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Search and return details for all blackout policies matching the name
    public JSONArray searchBlackoutPolicy(String policyName, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        JSONArray policiesDetails = null;
        String jsonFile = "searchBlackoutPolicy.json";
        try {
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<POLICY_NAME>", policyName);
            System.out.println("Search And Get Blackout Policies Details Payload is:\n" + jsonContent);

            Response response = given()
                    .header("Authorization", "Bearer " + helixJwtToken)
                    .contentType("application/json")
                    .body(jsonContent)
                    .post(hmSearchBlackoutPolicy);
            if (response.getStatusCode() == 200) {
                System.out.println(response.getBody().asString());
                policiesDetails = new JSONObject(response.getBody().asString()).getJSONArray("policies");
                System.out.println("Blackout Policies Details retrieved are: " + policiesDetails);
                return policiesDetails;
            } else {
                System.out.println("Blackout Policies Details not retrieved: " + response.getBody().asString());
                Assert.fail("Blackout Policies Details not retrieved. Status Code " + response.getStatusCode());
            }
            return policiesDetails;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    
    //delete Blackout Policies
    public boolean deleteBlackoutPolicies(List<String> policyIds, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        String jsonFile = "deleteBlackoutPolicies.json";
        try {
            String jsonContent = readJSon(jsonFile);
            String quotedList = policyIds.stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", "));
            jsonContent = jsonContent.replace("\"POLICY_ID_LIST\"", quotedList);
            System.out.println("Delete Blackout Policies Payload is:\n" + jsonContent);
            Response response = given()
                    .header("Authorization", "Bearer " + helixJwtToken)
                    .contentType("application/json")
                    .body(jsonContent)
                    .post(hmDeleteBlackoutPolicies);
            if (response.getStatusCode() == 200) {
                System.out.println(response.getBody().asString());
                System.out.println("Policies deleted successfully.");
                return true;
            } else {
                System.out.println("Policies not deleted. Response Code:"+response.getStatusCode()+" and response is:"+response.getBody().toString());
                Assert.fail("Blackout Policies Details not retrieved. Status Code " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    //Create Blackout Policies
    public String createBlackoutPolicy(Map<String, String> policyConfigs, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        String jsonFile = "createOneTimeRecurrenceBlackoutPolicy.json";
        try {
            String jsonContent = readJSon(jsonFile);
            for (Map.Entry<String, String> entry : policyConfigs.entrySet()) {
                jsonContent = jsonContent.replace(entry.getKey(), entry.getValue());
            }

            System.out.println("Create Blackout Policies Payload is:\n" + jsonContent);
            Response response = given()
                    .header("Authorization", "Bearer " + helixJwtToken)
                    .contentType("application/json")
                    .body(jsonContent)
                    .post(hmCreateBlackoutPolicy);
            if (response.getStatusCode() == 200) {
                System.out.println(response.getBody().asString());
                System.out.println("Policy Created Successfully.");
                return new JSONObject(response.getBody().asString()).getJSONArray("resourceId").get(0).toString();
            } else {
                System.out.println("Policy not created. Response Code:"+response.getStatusCode()+" and response is:"+response.getBody().asString());
                Assert.fail("Policy not created. Response Code: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

}
