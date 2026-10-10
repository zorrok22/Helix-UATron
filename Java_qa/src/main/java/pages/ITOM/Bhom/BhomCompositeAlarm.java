package pages.ITOM.Bhom;
import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pages.ITOM.Aiops.ADESituationHelixGPT;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.json.JSONArray;
import org.json.JSONObject;
import pages.ITOM.Api.HMEvents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class BhomCompositeAlarm {
    private static final SolqaLogger log = new SolqaLogger(BhomCompositeAlarm.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
        System.getProperty("user.dir") + File.separator + "properties.config");
    private static String CompositeAlarmSearch = "/composite-alarm-service/api/v1.0/composite-policies/search";
    private static String CompositeAlarmCreate = "/composite-alarm-service/api/v1.0/composite-policies";
    private static String CompositeAlarmDelete = "/composite-alarm-service/api/v1.0/composite-policies/delete";
    private static String AssociateEventToNodePolicy = "/events-service/api/v1.0/event_policies";
    private static String AssociateEventToNodePolicySearch = "/events-service/api/v1.0/event_policies/search?excludeConfig=true";
    private static String AssociateEventToNodePolicyDelete = "/events-service/api/v1.0/event_policies/delete";
    private static String hmUrl = config.get("ADE_PLATFORM_URL");
    private String jwtToken = null;
    private HMEvents hmEvents = new HMEvents();
    private CommonMethods commonmethod = new CommonMethods();

    public JSONObject getCompositeAlarmList(String searchString){
        RestAssured.baseURI = hmUrl;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject searchBody = new JSONObject()
                .put("recordsPerPage", 25)
                .put("pageIndex", 1)
                .put("searchString", searchString)
                .put("sortCriteria", new JSONObject().put("attribute", "modifiedTime").put("order", "DESC"));
        
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ jwtToken)
                .header("Content-Type", "application/json")
                .body(searchBody.toString())
                .post(CompositeAlarmSearch);
        log.info("Composite alarm search response body: " + resp.getBody().asString());
        if (resp.getStatusCode() != 200) {
            log.info("Composite alarm search failed: " + resp.getStatusCode());
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public boolean createCompositeAlarm(String compositeAlarmName, String compositeExpression, String compositeeventMessage){
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String jsonFile = "createCompositeAlarmPolicy.json";
        String jsonContent = hmEvents.readJSon(jsonFile);
        jsonContent = jsonContent.replace("<COMPOSITE_ALARM_NAME>", compositeAlarmName);
        jsonContent = jsonContent.replace("<CompositeExpression>", compositeExpression);
        jsonContent = jsonContent.replace("<Event Message>", compositeeventMessage);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(CompositeAlarmCreate);
        log.info("Composite alarm create response body: " + resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.info("Composite alarm create failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }
    public boolean deleteCompositeAlarm(String compositeAlarmId){
        RestAssured.baseURI = hmUrl;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject deleteBody = new JSONObject()
                .put("policyIds", new JSONArray().put(compositeAlarmId));
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ jwtToken)
                .header("Content-Type", "application/json")
                .body(deleteBody.toString())
                .post(CompositeAlarmDelete);
        log.info("Composite alarm delete response body: " + resp.getBody().asString());
    
        if (resp.getStatusCode() != 200) {
            log.info("Composite alarm delete failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    public boolean deleteSearchCompositeAlarms(String compositeAlarmNamePrefix){
        if (compositeAlarmNamePrefix == null || compositeAlarmNamePrefix.isEmpty()){
            log.error("Composite alarm name prefix is required");
            return false;
        }
        JSONObject compositeAlarm = getCompositeAlarmList(compositeAlarmNamePrefix);
        if(compositeAlarm == null){
            log.error("Failed to get composite alarm list");
            return false;
        }else{
            log.info("Composite alarm list: " + compositeAlarm.toString());
            int recordlength = compositeAlarm.getInt("totalRecords");
            log.info("Total records: " + recordlength);
            JSONArray compositeAlarmList = compositeAlarm.getJSONArray("policies");
            log.info("Composite alarm list: " + compositeAlarmList.toString());
            for(Object compositeAlarmObject : compositeAlarmList){
                JSONObject compositeAlarmObjectJSON = (JSONObject) compositeAlarmObject;
                String compositeAlarmId = compositeAlarmObjectJSON.getString("id");
                boolean success = deleteCompositeAlarm(compositeAlarmId);
                if(!success){
                    log.error("Failed to delete composite alarm");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean createAssociateEventToNodePolicy(String policyName){
        RestAssured.baseURI = hmUrl;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String jsonContent = hmEvents.readJSon("AssociateEventToNodeEventPolicy.json");
        jsonContent = jsonContent.replace("<POLICY_NAME>", policyName);        
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(AssociateEventToNodePolicy);
        log.info("Associate event to node policy create response body: " + resp.getBody().asString());
        if (resp.getStatusCode() != 200) {
            log.info("Associate event to node policy create failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

}
