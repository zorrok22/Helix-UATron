package pages.ITOM.Api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.*;

/** *********************************************************************
 Purpose:		This class contains common functions for Helix Monitor Events using API's
 @author			almahaja
  ********************************************************************** */

public class HMEvents {
    private static final SolqaLogger log = new SolqaLogger(HMEvents.class.getName());
    public static String eventsCreate = "/events-service/api/v1.0/events";
    public static String eventOperation = "events-service/api/v1.0/events/operations/<operation>";
    public static String eventQuery = "tsws/monitoring/api/v1.0/events/search?withCount=true";
    public static String eventDetails = "/tsws/monitoring/api/v1.0/events/<event_identifier>/details";
    public static String bhomservertime = "/tsws/monitoring/api/v1.0/time/servertime";
    public static String bhommsearch = "/events-service/api/v1.0/events/msearch";
    public static final String incidentSubscriptionEndpoint = "/tsws/api/v1.0/integration/incident";
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static String hmUrl = config.get("ADE_PLATFORM_URL");
    private static final String bhomPSRPolicySubcription = "/tsws/api/v1.0/integration/incident";
    private static final String bhomApiKeyEndPoint = "/tsws/monitoring/api/v1.0/tenant/apikey";
    private static final String ALARM_POLICY_SEARCH = "/alarm-service/api/v1.0/alarm_policies/search";
    private static final String ALARM_POLICY = "/alarm-service/api/v1.0/alarm_policies";
    private static final String EVENT_POLICY_SEARCH = "/events-service/api/v1.0/event_policies/search?excludeConfig=true";
    private static final String EVENT_POLICY_DELETE = "/events-service/api/v1.0/event_policies/delete";
    private static final String UserInfoEndPoint = "/tsws/monitoring/api/v1.0/userInfo";
    private static final String alarmPolicy = "/tsws/monitoring/api/v1.0/alarm/policies";
    private static final String GroupPolicy = "/tsws/monitoring/api/v1.0/tag/details/<tagId>";
    private static final String GroupPolicyJsonFile = "createGroupPolicy.json";
    private static final String GroupPolicyCreate = "/tsws/monitoring/api/v1.0/tag/create";
    private static final String GroupSlotValue = "/tsws/monitoring/api/v1.0/slotorder/getallslotorders";
    private static final String GroupDelete = "/tsws/monitoring/api/v1.0/tag/delete";
    private static final String GroupPolicySearch = "/tsws/monitoring/api/v1.0/tag/search";
    private static final String GroupPolicySearchJsonFile = "getGroupPolicyList.json";
    private static final String BHOMDeviceList = "/tsws/monitoring/api/v1.0/device/search";
    private static final String MSearchSituationQueryAllStatusJsonFile = "msearchsituationqueryallstatus.json";

    // Function to read JSON file and return it's content
    public String readJSon(String jsonFileName){
        try {
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "Monitor"+File.separator+jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String runEventQueryAPI(Map<String, String> params, String helixJwtToken) {
        String jsonFile = "runEventQueryApi.json";
        RestAssured.baseURI = hmUrl;
        try {
            String jsonContent = readJSon(jsonFile);
            log.info(jsonContent);
            String allStatus = "OPEN,ACK,ASSIGNED,BLACKOUT,CLOSED";
            JSONObject payload = new JSONObject();
            JSONObject advFilter = new JSONObject();
            JSONArray filterConditions = new JSONArray();

            if (params.isEmpty()) {
                payload.put("directFilterStatusCriteria",
                        Map.of("value", allStatus, "identifier", "status", "operator", "WITHIN"));
            } else {
                int i = 0;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey().toLowerCase();
                    String value = entry.getValue();

                    switch (key) {
                        case "status":
                            advFilter.put("status", value.toUpperCase());
                            break;
                        case "class":
                            advFilter.put("eventClass", value);
                            break;
                        case "severity":
                            advFilter.put("severity", value.toUpperCase());
                            break;
                        case "eventclass":
                            key = "CLASS";
                            break;
                        case "totalresultcount":
                            payload.put("totalResultCount", Integer.parseInt(value));
                            break;
                        default:
                            JSONObject condition = new JSONObject();
                            condition.put("condition_order", i);
                            condition.put("slot_name", key);
                            condition.put("condition_bracket", "");
                            condition.put("condition_operator", i == 0 ? "" : "AND");
                            condition.put("slot_operator", "contains");
                            condition.put("slot_value", value);
                            condition.put("end_bracket", "");

                            filterConditions.put(condition);
                            i++;
                            break;
                    }

                }
                advFilter.put("filterConditions", filterConditions);
            }

            JSONObject jsonObject = new JSONObject(jsonContent);
            jsonObject.put("advanceFilter", advFilter);

            log.info("Run Event Query Payload: "+jsonObject.toString(3));

            Response response = given()
                    .header("Authorization", "Bearer " + helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toString())
                    .post(eventQuery);
            String rawResponse = response.getBody().asString();
            String rawPreview = rawResponse.length() <= 300 ? rawResponse : rawResponse.substring(0, 300) + "...";
            log.info("Run Event Query API raw response: " + rawPreview);
            if (response.getStatusCode() == 200) {
                log.info("Response: " + response.getBody().asString());
                return response.getBody().asString(); // Adjust based on actual response format
            } else {
                System.err.println("Failed to retrieve events. Response Code: " + response.getStatusCode());
                return "";
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getEventIdentifierApi(Map<String, String> params, String helixJwtToken) throws Exception {
        try {
            String events = runEventQueryAPI(params, helixJwtToken);
            List<String> identifiers = extractIdentifiers(events);
            log.info("Identifiers: " + identifiers);
            return identifiers;
        }
        catch (Exception e){
            Assert.fail("Failed to extract Event Identifier. "+e.getMessage());
            return Collections.singletonList("");
        }
    }


    private static List<String> extractIdentifiers(String events) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(events);
        JsonNode eventListNode = jsonNode.get("eventList");

        List<String> identifiers = new ArrayList<>();
        if (eventListNode != null && eventListNode.isArray()) {
            for (JsonNode node : eventListNode) {
                JsonNode identifierNode = node.get("_identifier");
                if (identifierNode != null) {
                    identifiers.add(identifierNode.asText());
                }
            }
        }
        return identifiers;
    }


    public String streamRestApiEvents(String evClass, int eventCount, String helixJwtToken,
                                      String sourceHostname, String msg, String severity, String location, String tag) {
        String jsonFile = "streamRestApiEvents.json";
        RestAssured.baseURI = hmUrl;
        StringBuilder responseString = new StringBuilder();
        try {
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<SEVERITY>", severity)
                    .replace("<MESSAGE>", msg)
                    .replace("<LOCATION>", location).replace("<CLASS>", evClass)
                    .replace("<SOURCE_HOSTNAME>", sourceHostname)
                    .replace("<tag>", tag);
            log.info("Sent Event Payload is:\n" + jsonContent);
            for (int i = 0; i < eventCount; i++) {
                log.info("Send Event-" + i);
                Response response = given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + helixJwtToken)
                        .contentType("application/json")
                        .body(jsonContent)
                        .post(eventsCreate);

                if (response.getStatusCode() == 200) {
                    log.info("Event sent successfully: " + response.getBody().asString());
                    responseString.append("{\"").append(i).append("\"").append(":").append(response.getBody().asString()).append("}");
                } else {
                    System.err.println("Failed to send event. Response Code: " + response.getStatusCode());
                    responseString.append("{\"").append(i).append("\"").append(":").append(response.getBody().asString()).append("}");
                    Assert.fail("Failed to send event.");
                }
            }
            return responseString.toString();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String closeEventApi(String eventIdentifier, String notes, String helixJwtToken) throws JsonProcessingException {

        String jsonFile = "closeEvents.json";
        RestAssured.baseURI = hmUrl;
        try {
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("EVENT_ID_LIST", eventIdentifier).replace("<NOTES>", notes);

            log.info("Close Event Payload is:\n" + jsonContent);

            // Execute REST API call
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + helixJwtToken)
                    .body(jsonContent)
                    .post(eventOperation.replace("<operation>", "close"));

            int statusCode = response.getStatusCode();

            if (statusCode != 200) {
                if (statusCode == 202) {
                    log.info("Close Event accepted by Server.");
                } else {
                    log.info("Close Event Operation failed with Status Code " + statusCode + " for: " + eventIdentifier);
                    Assert.fail("Close Event Operation failed with Status Code " + statusCode + " for: " + eventIdentifier);
                }
            }
            return response.getBody().asString();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String eventDetails(String eventIdentifier, String helixJwtToken) throws JsonProcessingException {

        RestAssured.baseURI = hmUrl;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + helixJwtToken)
                .get(eventDetails.replace("<event_identifier>", eventIdentifier));

        int statusCode = response.getStatusCode();

        if (statusCode != 200) {
            if (statusCode == 202) {
                log.info("Get Event Details Successful.");
            }
            else{
                log.info("Get Event Details failed with Status Code "+statusCode+ " for "+ eventIdentifier);
                Assert.fail("Get Event Details failed with Status Code "+statusCode+" for: "+" eventIdentifier");
            }
        }
        return response.getBody().asString();
    }

    public void closeAllEventByClass(String class_name,String jwt_token) throws JsonProcessingException {
        //Close all open Event
        Map<String, String> param = new HashMap<>();
        param.put("class", class_name);
        param.put("status","OPEN");
        String jsonString = runEventQueryAPI(param,jwt_token);
        JSONObject json_body = new JSONObject(jsonString);
        JSONArray jsonarr = json_body.getJSONArray("eventList");
        for (Object rec : jsonarr){
            JSONObject jsonObject = (JSONObject) rec;
            String identifier = jsonObject.getString("_identifier");
            closeEventApi(identifier,"closing Event by api", jwt_token);
        }
    }

    public Long getBhomServerTime(String helixJwtToken)
    {
        RestAssured.baseURI = hmUrl;

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(bhomservertime);

        if (response.getStatusCode() == 200) {
            JSONObject tenant_features_response = new JSONObject(response.getBody().asString());
            log.info("BHOM server time response: " + response.getBody().asString());
            Long servertime = tenant_features_response.getLong("timeInMS");
            return servertime;
        }
        return 0L;
    }
    private static String CompressJson(String jsonContent) {
        // Remove newline and tab characters
        String compressed = jsonContent.replace("\n", "").replace("\t", "");
        // Remove extra spaces (multiple spaces or leading/trailing spaces)
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(compressed);
        return matcher.replaceAll(" ");
    }

    public JSONObject runMSearchQueryGetSituation(String service_key, String helixJwtToken, String jsonFile, long timeDiff) {
        RestAssured.baseURI = hmUrl;
        String jsonContent = readJSon(jsonFile);
        jsonContent = CompressJson(jsonContent);
        long endtime = getBhomServerTime(helixJwtToken);
        long start_time =  endtime - timeDiff;
        jsonContent = jsonContent.replace("<SERVICE_KEY>", service_key);
        jsonContent = jsonContent.replace("<END_TIME>", Long.toString(endtime));
        jsonContent = jsonContent.replace("<START_TIME>", Long.toString(start_time));
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(bhommsearch);
        if (response.getStatusCode() == 200) {
            JSONObject tenant_features_response = new JSONObject(response.getBody().asString());
            if(response.getBody().asString().length() < 500){
                log.info("MSearch situation response (truncated): " + response.getBody().asString());
            }else{
                log.info("MSearch situation response: " + response.getBody().asString().substring(0,500));
            }
            return tenant_features_response;
        }else {
            log.info("Failed to retrieve change ids associated with CI. Response code is: "+response.getStatusCode());
            log.info("MSearch situation failure response: " + response.getBody().asString());
            return null;
        }
    }
    public boolean getIncidentPolicyEnable(String helixJwtToken){
        RestAssured.baseURI = hmUrl;

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(bhomPSRPolicySubcription);
        if (response.getStatusCode() == 200) {
            int no_data = response.getBody().asString().length();
            if (no_data < 10){
                return false;
            }
            JSONObject subscription_response = new JSONObject(response.getBody().asString());
            String name = subscription_response.getJSONArray("response").getJSONObject(0).getString("name");
            String incidentCreationApp = SolQAConstants.IA.replace(" ", "_");
            return name.contains(incidentCreationApp);
        }
        return false;
    }

    /**
     * Checks whether incident creation is enabled.
     */
    public boolean checkIncidentCreationEnabled(String helixJwtToken) throws IOException, InterruptedException {
        RestAssured.baseURI = hmUrl;
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(incidentSubscriptionEndpoint);
        if (response.getStatusCode() == 204) {
            return false;
        }
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String name = jsonObject.getJSONArray("response").getJSONObject(0).getString("name");
        return name.contains("Automation");
    }

    /**
     * Get BHOM API KEY
     */
    public String getBhomApiKey(String helixJwtToken)
    {
        RestAssured.baseURI = hmUrl;

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(bhomApiKeyEndPoint);
        if (response.getStatusCode() == 200) {
            log.info(response.getBody().asString());
            JSONObject subscription_response = new JSONObject(response.getBody().asString());
            return subscription_response.getString("apiKey");
        }
        return null;

    }

    public boolean SearchanddeleteAlarmPolicies(String searchString, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;
        JSONObject searchBody = new JSONObject()
                .put("searchString", searchString)
                .put("sortCriteria", new JSONObject().put("attribute", "modifiedTime").put("order", "DESC"));

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ helixJwtToken)
                .header("Content-Type", "application/json")
                .body(searchBody.toString())
                .post(ALARM_POLICY_SEARCH);

        if (resp.getStatusCode() != 200) {
            log.info("Alarm policy search failed: " + resp.getStatusCode());
            return false;
        }

        JSONObject json = new JSONObject(resp.getBody().asString());
        JSONArray policies = json.optJSONArray("policies");
        if (policies == null){
            log.info("No data fetch nothing to delete");
            return true;
        }

        for (int i = 0; i < policies.length(); i++) {
            JSONObject policy = policies.getJSONObject(i);
            String name = policy.optString("policyName", "");
            String entityType = policy.optJSONArray("entityThresholds")
                    .optJSONObject(0)
                    .optJSONObject("metricDetails")
                    .optString("entityTypeId", "");
            boolean isPredictPolicy = name.toLowerCase().contains(searchString) || entityType.toLowerCase().contains("nuk_cpu");
            boolean isBmcPolicy = name.toUpperCase().contains("BMC");
            if (isPredictPolicy && !isBmcPolicy) {
                String id = policy.optString("id", "");
                if (!id.isEmpty()) {
                    RestAssured.given()
                            .header("Authorization", "Bearer "+ helixJwtToken)
                            .header("Content-Type", "application/json")
                            .delete(ALARM_POLICY + "/" + id);
                    if (resp.getStatusCode() < 299) {
                        log.info("Deleted alarm policy: " + name);}else{
                        log.info("Fail to deleted alarm policy: " + name);
                    }
                }
            }
        }
        return true;
    }

    public boolean createPatrolAgentAlarmPolicy(String PoliceName, String entityTypeId, String metricId, String label, String systemType, String unit, boolean baseLine,String helixJwtToken) {
        //Get Solution
        RestAssured.baseURI = hmUrl;
        
        String path = getAlarmPolicyPath(systemType, entityTypeId, metricId, helixJwtToken);
        if(path == null){
            log.info("Failed to get path for " + systemType + " " + entityTypeId + " " + metricId);
            return false;
        }
        // Simplified payload modeled after Robot create-alarm
        String policyName = PoliceName + "-" + randomSuffix();

        JSONObject metricDetails = new JSONObject()
                .put("entityTypeId", entityTypeId)
                .put("metricId", metricId)
                .put("label", label)
                .put("unit", unit)
                .put("path", path);

        JSONObject alarmConfig = new JSONObject()
                .put("condition", new JSONObject()
                        .put("comparison", "above")
                        .put("threshold", 1)
                        .put("baseline", baseLine)
                        .put("numPolls", -1)
                        .put("duration", 1))
                .put("action", new JSONObject()
                        .put("severity", "CRITICAL")
                        .put("autoClose", true)
                        .put("closeDuration", 0))
                .put("configOrder", 0);
        if(baseLine){
            alarmConfig.getJSONObject("condition").put("baselineType",0);
        }
        JSONObject threshold = new JSONObject()
                .put("metricDetails", metricDetails)
                .put("type", "ALL")
                .put("evaluationCriteria", "")
                .put("order", 0)
                .put("alarmConfigurations", new JSONArray().put(alarmConfig));

        JSONObject policy = new JSONObject()
                .put("policyName", policyName)
                .put("enabled", true)
                .put("executionOrder", 9999)
                .put("entityThresholds", new JSONArray().put(threshold));

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(policy.toString())
                .post(ALARM_POLICY);
        if(resp.getStatusCode() > 299) {
            log.info("Failed to create alarm policy for " + path);
            log.info(resp.getBody().asString());
            return false;
        }
        log.info("Created prediction alarm policy " + policyName);
        return true;
    }

    private String resolveLatestPAVersion(String solutionJson, String systType) {
        try {
            JSONObject sol = new JSONObject(solutionJson);
            JSONArray data = sol.optJSONArray("solutionData");
            if (data == null) {
                return null;
            }
            String latestKMVersion = "0";
            for (int i = 0; i < data.length(); i++) {
                JSONObject entry = data.getJSONObject(i);
                String solName = entry.optString("solutionName", "");
                if (!solName.toLowerCase().contains(systType)) {
                    continue;
                }
                String version = entry.optString("version", "0");
                if (compareVersion(version, latestKMVersion) > 0) {
                    latestKMVersion = version;
                }
            }
            if(latestKMVersion.equalsIgnoreCase("0"))
            {
                return null;
            }else{
                return latestKMVersion;
            }
        } catch (Exception e) {
            log.info("Failed to parse solution version, defaulting to latest: " + e.getMessage());
            return null;
        }
    }

    private int compareVersion(String a, String b) {
        String[] pa = a.split("\\.");
        String[] pb = b.split("\\.");
        int len = Math.max(pa.length, pb.length);
        for (int i = 0; i < len; i++) {
            int va = i < pa.length ? Integer.parseInt(pa[i]) : 0;
            int vb = i < pb.length ? Integer.parseInt(pb[i]) : 0;
            if (va != vb) return Integer.compare(va, vb);
        }
        return 0;
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public void SearchanddeleteEventPolicies(String searchSring, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;
        String SearchEventPolicy = "searchEventPolicy.json";
        String jsonContent = readJSon(SearchEventPolicy);
        jsonContent = jsonContent.replace("<POLICY_NAME>",searchSring);

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(EVENT_POLICY_SEARCH);

        if (resp.getStatusCode() != 200) {
            log.info("Event policy search failed: " + resp.getStatusCode());
            return;
        }

        JSONObject json = new JSONObject(resp.getBody().asString());
        int totalRecord = json.getInt("totalRecords");
        if (totalRecord < 1){
            log.info("No Event record fetch");
            return;
        }
        JSONArray policies = json.optJSONArray("policies");
        if (policies == null) return;

        for (int i = 0; i < policies.length(); i++) {
            JSONObject policy = policies.getJSONObject(i);
            String name = policy.optString("name", "");
            String uuid = policy.optString("id", "");
            // 1. Create a JSONArray and add the UUID to it
            JSONArray idsArray = new JSONArray();
            idsArray.put(uuid);

            // 2. Create the main JSONObject and add the "ids" array
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ids", idsArray);

            RestAssured.given()
                    .header("Authorization", "Bearer "+ helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toString())
                    .post(EVENT_POLICY_DELETE);
            log.info("Deleted Event policy: " + name);

        }
    }

    public String getTanantId(String helixJwtToken){
        RestAssured.baseURI = hmUrl;

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(UserInfoEndPoint);
        if (response.getStatusCode() < 299) {

            JSONObject user_response = new JSONObject(response.getBody().asString());
            return user_response.getString("tenantName");
        }
        return null;
    }

    /**
     * Retrieves events for a given status using msearch. Tracks pagination via endTime.
     * Returns true when events are found and fills eventListId; otherwise clears list and returns false.
     */
    public boolean CloseAllOpenEvent(String helixJwtToken) throws JsonProcessingException {
        String status = "OPEN";
        int partitionSize = 980;

        List<String> eventListId = new ArrayList<>();
        JSONObject response = runMSearchQueryGetSituation(status,helixJwtToken,MSearchSituationQueryAllStatusJsonFile,2);

        JSONArray responses = response.getJSONArray("responses");
        JSONObject hits = responses.getJSONObject(0).getJSONObject("hits");
        int value = hits.getJSONObject("total").getInt("value");
        if (value == 0) {
            return false;
        }

        JSONArray recordList = hits.getJSONArray("hits");
        for (Object rec : recordList) {
            JSONObject src = ((JSONObject) rec).getJSONObject("_source");
            String eventId = src.optString("_identifier", "");
            if (!eventId.isEmpty()) {
                eventListId.add(eventId);
            }
        }
        List<String> Results = IntStream.iterate(0, i -> i < eventListId.size(), i -> i + partitionSize)
                .mapToObj(i -> eventListId.subList(i, Math.min(i + partitionSize, eventListId.size())))
                .map(subList -> subList.stream().collect(Collectors.joining("\",\"", "\"", "\"")))
                .collect(Collectors.toList());

        for(String eventStr: Results){
            if (eventStr.startsWith("\"") && eventStr.endsWith("\"")) {
                // 3. If so, return the substring between the first and last character
                eventStr =  eventStr.substring(1, eventStr.length() - 1);
                closeEventApi(eventStr,"Close all Event", helixJwtToken);
            }
        }

        return true;
    }

    public JSONArray getBHOMDeviceList(String deviceSearchString, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;

        JSONObject body = new JSONObject()
                .put("searchString", deviceSearchString)
                .put("currentView", JSONObject.NULL)
                .put("sortBy", "severity_name")
                .put("sortDirection", "ascending")
                .put("devicesStartIndex", 0)
                .put("devicesEndIndex", 200);
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .when()
                .post(BHOMDeviceList);
        log.info("BHOM device list response: " + response.getBody().asString());
        if (response.getStatusCode() != 200) {
            log.info("Failed to get BHOM device list: " + response.getStatusCode());
            return null;
        }
        JSONObject json = new JSONObject(response.getBody().asString());
        JSONArray devices = json.getJSONObject("_object").getJSONArray("deviceSummary");
        log.info("BHOM device list: " + devices.toString());
        return devices;
    }

    public JSONArray getGroupPolicy(String groupName, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;

        String jsonContent = readJSon(GroupPolicySearchJsonFile);
        jsonContent = jsonContent.replace("<GROUP_NAME>",groupName);
        log.info("Group policy search payload: " + jsonContent);
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(GroupPolicySearch);
        log.info("Group policy search response: " + response.getBody().asString());
        if (response.getStatusCode() != 200) {
            log.info("Failed to get group policy: " + response.getStatusCode());
            return null;
        }
        JSONObject json = new JSONObject(response.getBody().asString());
        JSONArray groups = json.getJSONArray("taglist");
        if (groups.length() == 0) {
            return null;
        }
        return groups;
    }

    public boolean searchAndDeleteGroup(String groupName, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;

        JSONArray groups = getGroupPolicy(groupName, helixJwtToken);
        if (groups == null || groups.length() == 0) {
            return false;
        }
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            String name = group.optString("tagName", "");
            String uuid = group.optString("tagId", "");
            if(uuid.isEmpty()) {
                continue;
            }
            JSONArray idsArray = new JSONArray();
            idsArray.put(uuid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tagIds", idsArray);
            Response responseDelete = given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toString())
                    .when()
                    .post(GroupDelete);
            log.info("Group delete response: " + responseDelete.getBody().asString());
            if (responseDelete.getStatusCode() != 200) {
                log.info("Failed to delete group: " + responseDelete.getStatusCode());
                return false;
            }
            log.info("Deleted group: " + name);
        }
        return true;
    }

    public String[] getgroupSlotValue(String EventPolicyName, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(GroupSlotValue);
        log.info("Group slot value response: " + response.getBody().asString());
        if (response.getStatusCode() != 200) {
            log.info("Failed to get group slot value: " + response.getStatusCode());
            return null;
        }
        JSONObject json = new JSONObject(response.getBody().asString());
        JSONArray slotOrders = json.getJSONArray("response");
        String slotValue = "";
        String slotName = "";
        for(Object slotOrder : slotOrders) {
            JSONObject slotOrderObj = (JSONObject) slotOrder;
            slotName = slotOrderObj.optString("name", "");
            slotValue = slotOrderObj.optString("slotOrderID", "");
            if(slotName.contains(EventPolicyName)) {
                log.info("Group slot value found for event policy: " + EventPolicyName);
                return new String[]{slotName, slotValue};
            }
        }
        log.info("Group slot value not found for event policy: " + EventPolicyName);
        return new String[]{slotName, slotValue};
    }

    public boolean createGroupPolicy(String groupName, String deviceName, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;

        String jsonContent = readJSon(GroupPolicyJsonFile);
        String[] slotValue = getgroupSlotValue(groupName, helixJwtToken);
        jsonContent = jsonContent.replace("<POLICY_NAME>", groupName).replace("<device>", deviceName).replace("<SLOT_VALUE>", slotValue[1]);
        jsonContent = jsonContent.replace("<SLOT_NAME>", slotValue[0]);
        log.info("Create Group Policy Payload: " + jsonContent);
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(GroupPolicyCreate);
        log.info("Group policy response: " + response.getBody().asString());
        if (response.getStatusCode() != 200) {
            log.info("Failed to create group policy: " + response.getStatusCode());
            return false;
        }
        log.info("Group policy created successfully");
        return true;
    }

    public JSONObject getGroupPolicyDetails(String groupName, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;
        JSONArray groups = getGroupPolicy(groupName, helixJwtToken);
        if (groups == null || groups.length() == 0) {
            return null;
        }
        String tagId = groups.getJSONObject(0).optString("tagId", "");
        if (tagId.isEmpty()) {
            return null;
        }

        log.info("Getting group policy details for tagId: " + tagId);
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(GroupPolicy.replace("<tagId>", tagId));
        log.info("Group policy response: " + response.getBody().asString());
        if (response.getStatusCode() != 200) {
            log.info("Failed to get group policy: " + response.getStatusCode());
            return null;
        }
        return new JSONObject(response.getBody().asString());
    }

    public boolean Create3rdPartyAlarmPolicy(String PolicyBody, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;
        log.info("Creating 3rd party alarm policy: " + PolicyBody);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(PolicyBody)
                .post(alarmPolicy);
        log.info(resp.getBody().asString());
        if(resp.getStatusCode() > 299) {
            log.info("Failed to create alarm policy for ");
            return false;
        }
        log.info("Created prediction alarm policy ");
        return true;
    }

    public boolean createBaselineAlarmPolicy(String policyName, String entityTypeId, String metricId, String label, String systemType, String unit,  String helixJwtToken) {
        RestAssured.baseURI = hmUrl;
        String path = getAlarmPolicyPath(systemType, entityTypeId, metricId, helixJwtToken);
        if(path == null){
            log.info("Failed to get path for " + systemType + " " + entityTypeId + " " + metricId);
            return false;
        }
        policyName = policyName + "-" + randomSuffix();
        String jsonContent = readJSon("createAlarmPolicyBaseline.json");
        jsonContent = jsonContent.replace("<POLICY_NAME>", policyName);
        jsonContent = jsonContent.replace("<entityTypeId>", entityTypeId);
        jsonContent = jsonContent.replace("<metricId>", metricId);
        jsonContent = jsonContent.replace("<label>", label);
        jsonContent = jsonContent.replace("<unit>", unit);
        jsonContent = jsonContent.replace("<path>", path);
        log.info("Create Baseline Alarm Policy Payload: " + jsonContent);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(alarmPolicy);
        log.info(resp.getBody().asString());
        if(resp.getStatusCode() > 299) {
            log.info("Failed to create alarm policy for ");
            return false;
        }
        log.info("Created baseline alarm policy ");
        return true;
    }

    public String getAlarmPolicyPath(String systemType, String entityTypeId, String metricId, String helixJwtToken) {
        RestAssured.baseURI = hmUrl;

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .get("/tsws/monitoring/api/v1.0/package/solutions?licensed=true");

        if (resp.getStatusCode() != 200) {
            log.info("Solution search failed: " + resp.getStatusCode());
            return null;
        }
        String solutionBody = resp.getBody().asString();
        log.info("Solution body: " + solutionBody);

        String path = "";
        if(systemType.equals("Linux")) {
            String systType = "nuk";
            String nukVersion = resolveLatestPAVersion(solutionBody, systType);
            path = systType + "/" + nukVersion + "/" + entityTypeId + "/" + metricId;
        } else if(systemType.equals("Windows")) {
            String systType = "psx_epd";
            String psxVersion = resolveLatestPAVersion(solutionBody, systType);
            path = systType + "/" + psxVersion + "/" + entityTypeId + "/" + metricId;
        } else{
            path = systemType ;
        }
        if(path.isEmpty()) {
            log.info("Failed to resolve path for " + systemType);
            return null;
        }

        return path;
    }

    public String streamRestApiEvents(String evClass, int eventCount, String helixJwtToken, String sourceHostname, String msg, String severity, String location, String tag, String priority) {
        String jsonFile = "streamRestApiEvents.json";
        RestAssured.baseURI = hmUrl;
        StringBuilder responseString = new StringBuilder();
        try {
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<SEVERITY>", severity)
        .replace("<MESSAGE>", msg)
        .replace("<LOCATION>", location).replace("<CLASS>", evClass)
        .replace("<SOURCE_HOSTNAME>", sourceHostname)
        .replace("<tag>", tag)
        .replace("PRIORITY_3", priority);

        log.info("Sent Event Payload is:\n" + jsonContent);
        for (int i = 0; i < eventCount; i++) {
        log.info("Send Event-" + i);
        Response response = given()
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + helixJwtToken)
        .contentType("application/json")
        .body(jsonContent)
        .post(eventsCreate);

        if (response.getStatusCode() == 200) {
        log.info("Event sent successfully: " + response.getBody().asString());
        responseString.append("{\"").append(i).append("\"").append(":").append(response.getBody().asString()).append("}");
        } else {
        System.err.println("Failed to send event. Response Code: " + response.getStatusCode());
        responseString.append("{\"").append(i).append("\"").append(":").append(response.getBody().asString()).append("}");
        Assert.fail("Failed to send event.");
        }
        }
        return responseString.toString();
        }catch (Exception e) {
        throw new RuntimeException(e);
        }
    }
    public JSONObject getBhomPatrolAgent(String helixJwtToken) {
        RestAssured.baseURI = hmUrl;
        String jsonContent = readJSon("getbhomPatrolAgent.json");
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post("/tsws/monitoring/api/v1.0/unifiedadmin/agent/details");
        log.info("Connected agent response: " + response.getBody().asString());
        if(response.getStatusCode() != 200){
            log.info("Failed to get connected agent: " + response.getStatusCode());
            return null;
        }
        JSONObject json = new JSONObject(response.getBody().asString());
        return json;
    }
}
