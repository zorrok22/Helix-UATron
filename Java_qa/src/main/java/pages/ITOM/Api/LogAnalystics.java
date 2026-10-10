package pages.ITOM.Api;

import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import util.ReadPropertiesFile;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class LogAnalystics {
    private static final SolqaLogger log = new SolqaLogger(LogAnalystics.class.getName());
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private Cookies restAssuredCookies = null;
    private static String hmHostName = config.get("ADE_PLATFORM_URL");
    private String SearchConnectorFile = "LogAnalysticsConnectorSearch.json";
    private String SearchCollectionFile = "LogAnalysticsCollectionSearch.json";
    private String SearchParsingRuleFile = "LogAnalysticsParsingRuleSearch.json";
    private String CreateParsingRuleFile = "LogAnalysticsParsingRuleCreate.json";
    private String CreateCollectionRuleFile = "LogAnalysticsCollectionCreate.json";
    private String SearchConnectorEndPoint = "/loganalytics/tdc/api/v2.0/connectors/search";
    private String SearchCollectionEndPoint = "/loganalytics/logs-service/api/v1.0/logs/policies/search";
    private String SearchParsingRuleEndPoint = "/loganalytics/logs-service/api/v1.0/logs/collection/rules/search";
    private String DeleteCollectionEndPoint = "/logs-service/api/v1.0/logs/policies";
    private String DisableCollectionEndPoint = "/loganalytics/logs-service/api/v1.0/logs/policies/disable";
    private String DeleteParsingRuleEndPoint = "/logs-service/api/v1.0/logs/collection/rules";
    private String getParsingRuleTemplateEndPoint = "/loganalytics/tdc/api/v2.0/integrations/integration_templates";
    private String CreateParsingRuleEndPoint = "/loganalytics/logs-service/api/v1.0/logs/collection/rules";
    private String SearchUserGroupInfo = "/loganalytics/ims-api/groups?includeMappings=true";
    private String CreateCollectionRuleEndPoint =  "/loganalytics/logs-service/api/v1.0/logs/policies";
    private static String xsrfTokenValue = null;

    public String readJSon(String jsonFileName){
        try {
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "LA"+File.separator+jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            log.info("Loaded JSON file: " + jsonFile);
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setxsrftoken(Set<Cookie> seleniumCookies){
        for (Cookie cookie : seleniumCookies) {
            if (cookie.getName().equals("XSRF-TOKEN")) {
                xsrfTokenValue = cookie.getValue();
                // We found the token, no need to continue looping
                log.info("Captured XSRF token");
                break;
            }
        }

    }

    public void setrestassuredcookies(Set<Cookie> seleniumCookies){
        setxsrftoken(seleniumCookies);
        List<io.restassured.http.Cookie> list = new ArrayList<>();
        for (Cookie seleniumCookie : seleniumCookies) {
            io.restassured.http.Cookie build = new io.restassured.http.Cookie.Builder(
                    seleniumCookie.getName(),
                    seleniumCookie.getValue())
                    .build();
            list.add(build);
        }
        this.restAssuredCookies = new Cookies(
                list
        );
    }

    public Map<String, String> getUserGroupDetail(){
        RestAssured.baseURI = hmHostName;

        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .when()
                .get(SearchUserGroupInfo);

        log.info("response: " + response.getBody().asString());
        Map<String, String>usergroup = new HashMap<String, String>();
        if (response.getStatusCode() < 299) {
            JSONObject resp = new JSONObject(response.getBody().asString());
            JSONArray records = resp.getJSONArray("records");
            for(Object obj:records){
                JSONObject record = (JSONObject)obj;
                String name = record.getString("name");
                String groupdid = record.getString("group_id");
                usergroup.put(name,groupdid);
            }
            log.info("Fetched user groups count=" + usergroup.size());
            return usergroup;
        }
        log.info("Fail to get response: " + response.getStatusCode());
        return null;
    }

    public JSONObject getConnector(String SearchString){
        RestAssured.baseURI = hmHostName;
        String jsonContent = readJSon(SearchConnectorFile);
        jsonContent = jsonContent.replace("<SEARCH_NAME>", SearchString);
        log.info("Create Blank Business Service Payload is:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(SearchConnectorEndPoint);

        JSONObject responseJson = null;

        log.info("response: " + response.getBody().asString());

        if (response.getStatusCode() == 200) {

            log.info("Connector search success for: " + SearchString);
            return new JSONObject(response.getBody().asString());
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            throw new RuntimeException("Failed to get connector");
        }
    }

    public JSONObject getCollection(String SearchString){
        RestAssured.baseURI = hmHostName;
        String jsonContent = readJSon(SearchCollectionFile);
        jsonContent = jsonContent.replace("<SEARCH_NAME>", SearchString);
        log.info("Search collection Payload is:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(SearchCollectionEndPoint);

        JSONObject responseJson = null;

        log.info("response: " + response.getBody().asString());

        if (response.getStatusCode() == 200) {

            log.info("Collection search success for: " + SearchString);
            return new JSONObject(response.getBody().asString());
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            throw new RuntimeException("Failed to get collection ");
        }
    }

    public JSONObject getParsingRule(String SearchString){
        RestAssured.baseURI = hmHostName;
        String jsonContent = readJSon(SearchParsingRuleFile);
        jsonContent = jsonContent.replace("<SEARCH_NAME>", SearchString);
        log.info("Parsing Rule Body:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(SearchParsingRuleEndPoint);

        JSONObject responseJson = null;

        log.info("response: " + response.getBody().asString());

        if (response.getStatusCode() == 200) {

            log.info("Parsing rule search success for: " + SearchString);
            return new JSONObject(response.getBody().asString());
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            throw new RuntimeException("Failed to get Parsing Rule");
        }
    }

    public void deleteCollectionRule(String policyId, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        JSONObject jsonContent = new JSONObject().put("ids", new JSONArray().put(policyId));
        log.info("Json Content:\n " + jsonContent.toString());
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + helixJwtToken)
                .body(jsonContent.toString())
                .delete(DeleteCollectionEndPoint);
        log.info("response: " + response.getBody().asString());
        if (response.getStatusCode() > 299) {
            log.info("Failed to delete result, response returned is:\n " + response.getStatusCode());
        }
        log.info("Delete collection rule status=" + response.getStatusCode());
   }

    public void disableCollectionRule(String policyId){
        RestAssured.baseURI = hmHostName;
        JSONObject jsonContent = new JSONObject().put("ids", new JSONArray().put(policyId));
        log.info("Json Content:\n " + jsonContent.toString());
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent.toString())
                .post(DisableCollectionEndPoint);
        log.info("response: " + response.getBody().asString());
        if (response.getStatusCode() > 299) {
            log.info("Failed to disable result, response returned is:\n " + response.getStatusCode());
        }
        log.info("Disable collection rule status=" + response.getStatusCode());
    }

    public void deleteParsingRule(String policyId, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        JSONObject jsonContent = new JSONObject().put("ids", new JSONArray().put(policyId));
        log.info("Json Content:\n " + jsonContent.toString());
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + helixJwtToken)
                .body(jsonContent.toString())
                .delete(DeleteParsingRuleEndPoint);
        log.info("response: " + response.getBody().asString());
        if (response.getStatusCode() > 299) {
            log.info("Failed to delete result, response returned is: " + response.getStatusCode());
        }
        log.info("Delete parsing rule status=" + response.getStatusCode());
    }

    public String CreateParsingRule(String PolicyName,String templateId){
        RestAssured.baseURI = hmHostName;
        String jsonContent = readJSon(CreateParsingRuleFile);
        jsonContent = jsonContent.replace("<POLICY_NAME>", PolicyName);
        jsonContent = jsonContent.replace("<TEMPLATE_ID>", templateId);
        log.info("CreateParsingRule Payload is:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(CreateParsingRuleEndPoint);

        JSONObject responseJson = null;

        log.info("response: " + response.getBody().asString());

        if (response.getStatusCode() < 299) {
            return new JSONObject(response.getBody().asString()).optString("id", "");
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            throw new RuntimeException("Failed to create Parsing Rule");
        }
    }

    public String getParsingRuleTemplate(String templateName){
        RestAssured.baseURI = hmHostName;

        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .when()
                .get(getParsingRuleTemplateEndPoint);

        JSONObject responseJson = null;

        log.info("response: " + response.getBody().asString());

        if (response.getStatusCode() == 200) {
            JSONObject resp = new JSONObject(response.getBody().asString());
            JSONArray data = resp.getJSONArray("response");
            for(Object obj: data){
                JSONObject tempdata = (JSONObject)obj;
                String name = tempdata.getString("name");
                String id = tempdata.getString("id");
                if(name.equalsIgnoreCase(templateName)){
                    log.info("Found parsing rule template " + name + " id=" + id);
                    return id;
                }
            }
            return null;
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            throw new RuntimeException("Failed to get Template");
        }
    }

    public boolean CreateCollectionRule(String PolicyName,String templateId, String tenantId, String hostName, String groupid,
                                        String groupName, String parsingId, String parsingName, String folderPath, String tags){
        RestAssured.baseURI = hmHostName;
        String jsonContent = readJSon(CreateCollectionRuleFile);
        jsonContent = jsonContent.replace("<POLICY_NAME>", PolicyName);
        jsonContent = jsonContent.replace("<TENANT_ID>", tenantId);
        jsonContent = jsonContent.replace("<HOST_NAME>", hostName);
        jsonContent = jsonContent.replace("<TEMPLATE_ID>", templateId);
        jsonContent = jsonContent.replace("<ADMIN_ID>", groupid);
        jsonContent = jsonContent.replace("<ADMIN_NAME>", groupName);
        jsonContent = jsonContent.replace("<PARSING_RULE_ID>", parsingId);
        jsonContent = jsonContent.replace("<PARSING_RULE_NAME>", parsingName);
        jsonContent = jsonContent.replace("<FOLDER_PATH>", folderPath);
        jsonContent = jsonContent.replace("<TAGS>", tags);
        log.info("CreateCollectionRule Payload is:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(CreateCollectionRuleEndPoint);

        JSONObject responseJson = null;

        log.info("response: " + response.getBody().asString());

        if (response.getStatusCode() < 299) {
            String policyIdCreated = new JSONObject(response.getBody().asString()).optString("id", "");
            log.info("Created collection policy id: " + policyIdCreated);
            return true;
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            return false;
        }
    }

    public boolean CreateEventAlert(String PolicyName, String logstackHost)
    {
        RestAssured.baseURI = hmHostName;
        String CreateEventAlertFile = "LogAnalysticsEventAlertCreate.json";
        String CreateEventAlertEndPoint = "loganalytics/logs-service/api/v1.0/logs/policies";
        Pattern p = Pattern.compile("^[^#]+?(?:\\.\\d+)?(?=[. ;])");
        Matcher m = p.matcher(logstackHost);
        List<String> matches = new ArrayList<>();
        while (m.find()) {
            matches.add(m.group());
        }
        String logHostForPolicy = matches.isEmpty() ? logstackHost : matches.get(0);
        String jsonContent = readJSon(CreateEventAlertFile);
        jsonContent = jsonContent.replace("<POLICY_NAME>", PolicyName);
        jsonContent = jsonContent.replace("<LOGSTACK_HOST>", logHostForPolicy);
        log.info("CreateEventAlert Payload is:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(CreateEventAlertEndPoint);
        JSONObject responseJson = null;
        log.info("response: " + response.getBody().asString());
        if (response.getStatusCode() < 299) {
            log.info("Created event alert policy: " + PolicyName);
            return true;
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            return false;
        }
        
    }

    public JSONObject SearchEventAlert(String PolicyName){
        RestAssured.baseURI = hmHostName;
        String SearchEventAlertFile = "LogAnalysticsEventAlertSearch.json";
        String SearchEventAlertEndPoint = "loganalytics/logs-service/api/v1.0/logs/policies/search";
        String jsonContent = readJSon(SearchEventAlertFile);
        jsonContent = jsonContent.replace("<POLICY_NAME>", PolicyName);
        log.info("SearchEventAlert Payload is:\n"+jsonContent);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent)
                .when()
                .post(SearchEventAlertEndPoint);
        JSONObject responseJson = null;
        log.info("response: " + response.getBody().asString());
        if (response.getStatusCode() < 299) {
            log.info("Search event alert success for policy: " + PolicyName);
            return new JSONObject(response.getBody().asString());
        } else {
            log.info("Failed to get search result, response returned is:\n " + responseJson);
            return null;
        }
    }

    public boolean deleteEventAlert(String PolicyName){
        RestAssured.baseURI = hmHostName;
        String DeleteEventAlertEndPoint = "loganalytics/logs-service/api/v1.0/logs/policies";
        JSONObject responseJson = SearchEventAlert(PolicyName);
        if(responseJson == null){
            return false;
        }
        JSONObject metadata = responseJson.optJSONObject("metadata");
        int recordCount = metadata != null ? metadata.optInt("total_records", 0) : 0;
        if(recordCount == 0){
            return true;
        }
        JSONArray policyRecords = responseJson.optJSONArray("data");
        if (policyRecords == null) {
            log.info("No data array present in event alert search response; nothing to delete.");
            return true;
        }
        for(Object obj: policyRecords){
            JSONObject policy = (JSONObject)obj;
            String policyId = policy.getString("id");
            JSONObject jsonContent = new JSONObject().put("ids", new JSONArray().put(policyId));
            log.info("Json Content:\n " + jsonContent.toString());
            Response response = given()
                .header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsonContent.toString())
                .delete(DeleteEventAlertEndPoint);
            log.info("response: " + response.getBody().asString());
            if (response.getStatusCode() > 299) {
                log.info("Failed to delete event alert, response returned is:\n " + response.getBody().asString());
                return false;
            }
        }
        log.info("Deleted event alert policy: " + PolicyName);
        return true;
    }


}
