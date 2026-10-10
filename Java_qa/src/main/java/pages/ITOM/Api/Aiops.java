package pages.ITOM.Api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import pages.ITOM.Api.HMEvents;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import util.CommonWaits;

/** *********************************************************************
 Purpose:		This class contains common functions for AIOps using API's
 @author			almahaja
  ********************************************************************** */

public class Aiops {
    private static final SolqaLogger log = new SolqaLogger(Aiops.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static String hmHostName = config.get("ADE_PLATFORM_URL");
    public static String aiopsServiceSave = "/service-management/api/v1.0/service/save";
    public static String aiopsQueryTopology = "/service-management/api/v1.0/topology/query";
    public static String aiopsImpactSummary = "/impact-service/api/v1.0/entities/<SERVICE_KEY>?startTime=<START_TIME>&endTime=<END_TIME>&type=key&entityType=service";
    public static String aiopsGetSituationDetails = "/service-management/api/v1.0/service/situation/get";
    public static String aiopsEntities = "impact-service/api/v1.0/entities";
    public static String eventQuery = "tsws/monitoring/api/v1.0/events/search?withCount=true";
    public static String aiopsResetHealthScore = "health-service/api/v1.0/internal/reset-health-score";
    public static final String aiopsGetSituation = "service-management/api/v1.0/service/situation/get";
    public static final String aiopsPcaGetChangeAssociatedWithCi = "pca-api-service/api/v1.0/pca/node/changes";
    public static final String aiopstenantfeatures = "/featureflag/api/v1/tenantfeatures";
    private static String serviceMgmtCreateBlueprintApi = "/service-management/api/v1.0/blueprint/save";
    private static String aiopsBlueprintDelete = "/service-management/api/v1.0/blueprint/delete";
    private static String getAiopsBlueprint = "/service-management/api/v1.0/blueprint/list";
    private static String stateAiopsBlueprint = "/service-management/api/v1.0/blueprint/state";

    private static final String aiopsHealthTimeLine = "/impact-service/api/v1.0/entities/";
    public static final String aiopsCITopology ="/service-management/api/v1.0/service/data/content";
    public static final String aiopsNodeDetail = "/pca-api-service/api/v1.0/pca/node/";
    private static String aiopsRunRanker = "/situation-best-action-recommender/api/v1.0/generate_ranker";
    private static final String aiopsServiceDelete = "/service-management/api/v1.0/service/delete";
    private static final String aiopsENTITIES_ENDPOINT = "/impact-service/api/v1.0/entities";
    private static final String TOPOLOGY_SEARCH = "/service-management/api/v1.0/topology/search";
    private static final String HEALTH_RESET = "/health-service/api/v1.0/internal/reset-health-score";
    private static final String SERVICE_SAVE_MULTI = "/smart-graph-api/api/v1.17/models/multi";
    private static final String PREDICTION_SEARCH = "/prediction-api-service/api/v1.0/predictions";
    private static final String GET_SERVICE_ENDPOINT = "/service-management/api/v1.0/service/get";
    private static final String CONFIGURATION_ENDPOINT = "/aiops-config/api/v1.0/situation_configurations/search";
    private static final String SITUATION_CONFIGURATION_ENDPOINT = "/service-management/api/v1.0/service/configuration/default";
    private static final String GET_SERVICEMODEL_ENDPOINT = "/smart-graph-api/api/v1.17/models/";

    private JSONObject tenant_features_response = null;

    private HMEvents hm_events = new HMEvents();
    // Function to read JSON file and return it's content
    public String readJSon(String jsonFileName){
        try {
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "AIOPS"+File.separator+jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Create blank service
    public JsonNode createBlankService(String serviceName, String helixJwtToken) {
        RestAssured.baseURI = hmHostName;
        
        try {
            String jsonFile = "createBlankService.json";
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<SERVICE_NAME>", serviceName);
            log.info("Create Blank Business Service Payload is:\n"+jsonContent);

            Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(aiopsServiceSave);
            
            JsonNode responseJson = objectMapper.readTree(response.getBody().asString());
            log.info("Created Service " + serviceName + " and the response is: " + responseJson);
            
            if (response.getStatusCode() == 200) {
                return responseJson;
            } else {
                log.error("Failed to create blank service, response returned is:\n " + responseJson);
                throw new RuntimeException("Failed to create service");
            }
            
        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + ", while creating " + serviceName);
            throw new RuntimeException(e);
        }
    }

    public JsonNode createServiceFromGivenBlueprint(String serviceName, String bluePrintName, String value, String helixJwtToken) {
        RestAssured.baseURI = hmHostName;

        try {
            String jsonFile = "createBlankServiceFromGivenBlueprint.json";
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<SERVICE_NAME>", serviceName);

            String[] result = getBlueprintID(bluePrintName, helixJwtToken);


            jsonContent = jsonContent.replace("<BLUEPRINT_NAME>", bluePrintName);
            jsonContent = jsonContent.replace("<VALUE>", value);
            jsonContent = jsonContent.replace("<BLUEPRINT_ID>", result[0]);
            jsonContent = jsonContent.replace("<BLUEPRINT_VERSION>", result[1]);

            log.info("Create Blank Business Service with given Blueprint Payload is:\n"+jsonContent);

            Response response = given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonContent)
                    .when()
                    .post(aiopsServiceSave);

            JsonNode responseJson = objectMapper.readTree(response.getBody().asString());
            log.info("Created Service " + serviceName + " and the response is: " + responseJson);

            if (response.getStatusCode() == 200) {
                return responseJson;
            } else {
                log.error("Failed to create blank service, response returned is:\n " + responseJson);
                throw new RuntimeException("Failed to create service");
            }

        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + ", while creating " + serviceName);
            throw new RuntimeException(e);
        }
    }


    // searchAndGetServiceDetails,
    public JSONObject searchAndGetServiceDetails(String serviceName, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        JSONObject servicesNode = new JSONObject();
        String jsonFile = "searchAndGetServiceDetails.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<SERVICE_NAME>", serviceName);
        log.info("Search And Get Service Details Payload is:\n"+jsonContent);

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .contentType("application/json")
                .body(jsonContent)
                .post(aiopsEntities);
        if (response.getStatusCode() == 200) {
            String bodyStr = response.getBody().asString();
            String bodyLog = truncateForLog(bodyStr, 200);
            log.info("Service search response: " + bodyLog);
            servicesNode = new JSONObject(bodyStr).getJSONObject("services");
            String servicesLog = truncateForLog(servicesNode.toString(), 200);
            log.info("Service details retrieved are: " + servicesLog);
            return servicesNode;
        }
        else {
            log.error("Service details not retrieved: " + response.getBody().asString());
            Assert.fail("Service details not retrieved. Status Code "+response.getStatusCode());
        }
        return servicesNode;
    }

    // Get related node ids to CI
    public List<String> getRelatedNodeIds(String ciId, String helixJwtToken, String connector) {

        RestAssured.baseURI = hmHostName;
        String jsonFile = "getRelatedNodeIds.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<NODE_ID>", ciId);
        log.info("Get Related Node Ids Payload is:\n"+jsonContent);

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .contentType("application/json")
                .body(jsonContent)
                .post(aiopsQueryTopology);

        List<String> relatedNodeList = new ArrayList<>();
        if (response.getStatusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.getBody().asString()).getJSONObject("nodes");
            jsonResponse.keys().forEachRemaining(node -> {
                if (jsonResponse.getJSONObject(node).getString("kind").equals("SoftwareCluster") && "dynatrace".equals(connector)) {
                    return;
                }
                relatedNodeList.add(node);
            });

            log.info("Static Node ID list retrieved: " + relatedNodeList);
        } else {
            log.error("Failed to generate related node list. Response Code: " + response.getStatusCode());
            Assert.fail("Failed to generate related node list.");
        }
        return relatedNodeList;
    }

    // Add static CIs to service model
    public String addStaticCisToServiceModel(String serviceModelKey, String ciId, String helixJwtToken, String connector) {

        RestAssured.baseURI = hmHostName;
        List<String> relatedNodeList = getRelatedNodeIds(ciId, helixJwtToken, connector);
        String quotedList = relatedNodeList.stream()
                .filter(s -> !s.startsWith("g_"))
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(", "));
        String jsonFile = "addStaticCisToServiceModel.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<SERVICE_MODEL_KEY>", serviceModelKey).replace("\"<NODE_IDS>\"",quotedList);
        log.info("Add Node Id to Service model Payload is:\n"+jsonContent);

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .contentType("application/json")
                .body(jsonContent)
                .post(aiopsServiceSave);

        if (response.getStatusCode() == 200 && response.getBody().asString().contains("\"resultMessage\":\"OK\"")) {
            log.info("Successfully saved service model.");
        } else {
            log.error("Error while saving service model. Response: " + response.getBody().asString());
            Assert.fail("Error while saving service model.");
        }
        return response.getBody().asString();
    }

    // Check events and situation created
    public boolean checkEventsAndSituationCreated(String modelKey, String startTime, String endTime, String helixJwtToken) {

        RestAssured.baseURI = hmHostName;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);
            
            long startTimestamp = startDateTime.toEpochSecond(ZoneOffset.UTC);
            long endTimestamp = endDateTime.toEpochSecond(ZoneOffset.UTC);
            
            String apiEndPoint = aiopsImpactSummary.replace("<SERVICE_KEY>", modelKey).replace("<START_TIME>", ""+startTimestamp).replace("<END_TIME>", ""+endTimestamp);
            log.info("API Endpoint: " + apiEndPoint);
            log.info("Get the total number of events from " + startTimestamp + " to " + endTimestamp);
            
            Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(apiEndPoint);
            int status = response.statusCode();
            if (status > 300){
                log.error("Response is:\n" + status);
                return false;
            }
            JsonNode responseJson = objectMapper.readTree(response.getBody().asString());
            log.info("Response is:\n" + responseJson);
            
            int totalEventCount = responseJson.get("totalEventCount").asInt();
            if (totalEventCount > 0) {
                log.info(totalEventCount + " Events Found in Total.");
                int situationCount = responseJson.get("situationCount").asInt();
                if (situationCount > 0) {
                    log.info("Service impacted and is having " + situationCount + 
                                     " situations created, with health score " + responseJson.get("health"));
                    return true;
                } else {
                    log.info("Service not impacted and no situations created.");
                    return false;
                }
            } else {
                log.info("No Events Found.");
                return false;
            }
            
        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + ", while checking events and situation for " + modelKey);
            throw new RuntimeException(e);
        }
    }

    // wait for situation is created in last waitTimeMillis.
    public boolean waitForSituationCreation(String modelKey, String helixJwtToken, int waitTimeMillis) {
        int situationCreationTime = 330; // Time in minutes for the situation to get generated after sending events/alarm generation. Right now API accepts values >=330
        int retryIntervalMillis = 10 * 1000; // Retry every 10 seconds
        long loopStartTime = System.currentTimeMillis();
        long loopEndTime = loopStartTime + waitTimeMillis;
        LocalDateTime now = LocalDateTime.now();

        // Get date-time from last 30 minutes
        LocalDateTime fewMinutesAgo = now.minusMinutes(situationCreationTime);

        // Formatter for the required format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        log.info("Current Date-Time: " + now.format(formatter));
        log.info("Date-Time "+situationCreationTime+" Minutes Ago: " + fewMinutesAgo.format(formatter));

        while (System.currentTimeMillis() < loopEndTime) {
            if (checkEventsAndSituationCreated(modelKey, fewMinutesAgo.format(formatter) , now.format(formatter), helixJwtToken)) {
                log.info("Service is impacted and situation is created returned true, exiting loop.");
                return true;
            }

            log.info("Service is not impacted, retrying every 10 seconds.");

            try {
                Thread.sleep(retryIntervalMillis);
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                return false;
            }
        }

        log.info("Loop finished after " + (waitTimeMillis / (60 * 1000)) + " minutes.");
        return false;
    }

    // Get Situations for Service
    public JSONObject getSituationsForService(String serviceName, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        JSONObject situations = null;
        String jsonFile = "getSituationsForService.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<SERVICE_NAME>", serviceName);
        log.info("Search Situation Payload is:\n"+jsonContent);
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(eventQuery);
        if (response.getStatusCode() == 200) {
            situations = new JSONObject(response.getBody().asString());
            log.info("Situations fetch response: " + response.getBody().asString());
        }
        else {
            Assert.fail("Can't retrieve situations details for Service. Status Code "+response.getStatusCode());
        }
        return situations;
    }

    // Verify situation details
    public boolean verifySituationDetails(String situationId, List<String> targetHostList, String helixJwtToken) {
        RestAssured.baseURI = hmHostName;
        try {
            String jsonFile = "verifySituationDetails.json";
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<SITUATION_ID>", situationId);
            log.info("Verify Situation Details Payload: " + jsonContent);
            Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(aiopsGetSituationDetails);
            
            JsonNode jsonResponse = objectMapper.readTree(response.getBody().asString());
            log.info("json response for situation details is:\n" + jsonResponse);
            
            JsonNode nodes = jsonResponse.get("nodes");
            log.info("Nodes found under the situation are: " + nodes);
            
            List<String> targetHostListCopy = new ArrayList<>(targetHostList);
            
            Iterator<String> fieldNames = nodes.fieldNames();
            while (fieldNames.hasNext()) {
                String node = fieldNames.next();
                JsonNode nodeData = nodes.get(node);
                String nodeName = nodeData.get("nodeName").asText();
                if (targetHostListCopy.contains(nodeName)) {
                    targetHostListCopy.remove(nodeName);
                }
            }
            
            if (targetHostListCopy.isEmpty()) {
                log.info("Situation verification successful.");
                return true;
            } else {
                log.error("Events for " + targetHostListCopy + " not found in situation.");
                return false;
            }
            
        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + " in verify situation details");
            throw new RuntimeException(e);
        }
    }


    // Verify if Situation is Saturated
    public boolean verifySituationSaturated(String situationId, String helixJwtToken) throws JsonProcessingException {
        String eventDetails = hm_events.eventDetails(situationId, helixJwtToken);
        log.info("Situation Details are"+ eventDetails);
        JSONObject jsonObject = new JSONObject(eventDetails);
        String status = jsonObject.getJSONObject("response").getJSONObject("allslots").get("state").toString();
        return status.equals("saturated");

    }

    // Reset Health Score of Service
    public int resetServiceHealthScore(String serviceKey, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        try {
            String jsonFile = "resetServiceHealthScore.json";
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<SERVICE_KEY>", serviceKey);
            log.info("Reset Service Health Score Payload: " + jsonContent);

            Response response = given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonContent)
                    .when()
                    .post(aiopsResetHealthScore);

            if (response.getStatusCode() == 200){
                log.info("Health Score for Service is Successfully reset.");
                return 0;
            } else if (response.getStatusCode() == 202){
                log.info("Request to reset Health Score for Service is accepted by server.");
                return 1;
            }else {
                log.error("Reset health score failed response: " + response.getBody().asString());
                return -1;
            }
        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + " in verify situation details");
            throw new RuntimeException(e);
        }
    }

    // Get Change ID's associated with Situation
    public List<String> getChangeIdsAssociatedWithSituation(String situationId, String helixJwtToken) throws JsonProcessingException {
        String eventDetails = hm_events.eventDetails(situationId, helixJwtToken);
        log.info("Situation Details are"+ eventDetails);
        JSONObject jsonObject = new JSONObject(eventDetails);
        String changeIds = jsonObject.getJSONObject("response").getJSONObject("allslots").get("change_id").toString();
        return Arrays.stream(changeIds.split(",")).map(String::trim).collect(Collectors.toList());
    }

    // Get INCIDENT ID's associated with Situation
    public String getIncidentIdsAssociatedWithSituation(String situationId, String helixJwtToken) throws JsonProcessingException {
        String eventDetails = hm_events.eventDetails(situationId, helixJwtToken);
        log.info("Situation Details are"+ eventDetails);
        JSONObject jsonObject = new JSONObject(eventDetails);
        log.info("Incident IDs associated with situation: " + jsonObject.getJSONObject("response").toString());
        String changeId = jsonObject.getJSONObject("response").getJSONObject("allslots").get("incident_id").toString();
        return changeId;
    }

    // Verify if Situation is Saturated
    public List<String> getEventIdsAssociatedWithSituation(String situationId, String helixJwtToken) throws JsonProcessingException {
        RestAssured.baseURI = hmHostName;
        List<String> eventIds = new ArrayList<>(List.of());
        try {
            String jsonFile = "getEventIdsAssociatedWithSituation.json";
            String jsonContent = readJSon(jsonFile);
            jsonContent = jsonContent.replace("<SITUATION_ID>", situationId);
            log.info("Get Event Ids Associated with Situation Payload: " + jsonContent);
            Response response = given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonContent)
                    .when()
                    .post(aiopsGetSituation);

            if (response.getStatusCode() == 200){
                JSONObject jsonObject = new JSONObject(response.getBody().asString());
                //log.info("Situation Details are: "+response.getBody().asString());
                JSONObject nodes = jsonObject.getJSONObject("nodes");
                // Iterate through node keys and extract eventIds
                for (String key : nodes.keySet()) {
                    JSONObject node = nodes.getJSONObject(key);
                    if (node.has("eventIds")) {
                        int situationsid = node.getInt("situationEventCount");
                        String eventsid = node.getString("eventIds");
                        if (situationsid < 2) {
                            eventIds.add(eventsid);
                        }
                        else{
                            String[] parts = eventsid.split(",");
                            eventIds.addAll(Arrays.asList(parts));
                        }
                    }
                }
                log.info("Event Ids associated with situation are: "+eventIds);
                return eventIds;
            }
            else {
                log.error("Failed to retrieve event ids associated with situation. Response code is: "+response.getStatusCode());
                log.error("Event IDs failure response: " + response.getBody().asString());
                return eventIds;
            }
        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + " while retrieving events ids associated with situation.");
            throw new RuntimeException(e);
        }
    }

    //Get Change Ids Associated with CI
    public ArrayList getChangeIdsAssociatedWithCI(String nodeId, String serviceKey, String startTime, String endTime, String transitionTime, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        ArrayList changeIds = new ArrayList<>(List.of());
        String jsonFile = "getChangeListForCI.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<NODE_ID>", nodeId).replace("<SERVICE_KEY>", serviceKey).
                replace("<END_TIME>", endTime).replace("<START_TIME>", startTime).replace("<TRANSITION_TIME>", transitionTime);
        log.info("Change Request: " + jsonContent);
        try {
            Response response = given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(jsonContent)
                    .when()
                    .post(aiopsPcaGetChangeAssociatedWithCi);

            if (response.getStatusCode() == 200){
                log.info("Change Details are: "+response.getBody().asString());
                JSONArray jsonArray = new JSONArray(response.getBody().asString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    changeIds.add(jsonArray.getString(i));
                }
                log.info("Change Ids associated with CI are: "+changeIds);
                return changeIds;
            }
            else {
                log.error("Failed to retrieve change ids associated with CI. Response code is: "+response.getStatusCode());
                log.error("Change IDs failure response: " + response.getBody().asString());
                return changeIds;
            }
        } catch (Exception e) {
            log.error("Caught exception " + e.getMessage() + " while retrieving change ids associated with CI.");
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getHealthScore(String servicename, String jwt_token){
        JSONObject record = searchAndGetServiceDetails(servicename, jwt_token);
        if (record == null || !record.keys().hasNext()) {
            throw new IllegalStateException("No service details returned for " + servicename);
        }

        String selectedId = null;
        JSONObject serviceObject = null;

        // Prefer non-SOLQA keys when multiple records are returned
        Iterator<String> it = record.keys();
        while (it.hasNext()) {
            String candidateId = it.next();
            JSONObject candidate = record.getJSONObject(candidateId);
            String keyVal = candidate.optString("key", "");
            if (record.length() > 1 && keyVal.toUpperCase().contains("SOLQA")) {
                continue;
            }
            selectedId = candidateId;
            serviceObject = candidate;
            break;
        }

        // Fallback to the first entry if all were filtered out
        if (serviceObject == null) {
            selectedId = record.keys().next();
            serviceObject = record.getJSONObject(selectedId);
        }

        String[] servicedata = new String[5];
        Map<String, String> service_Data = new HashMap<>();
        servicedata[0] = serviceObject.getString("key");
        service_Data.put("key",servicedata[0]);
        servicedata[1] = String.valueOf(serviceObject.getInt("actualHealth"));
        service_Data.put("actualHealth",servicedata[1]);
        servicedata[2] = serviceObject.getString("id");
        service_Data.put("id",servicedata[2]);
        servicedata[3] = String.valueOf(serviceObject.getInt("totalEventCount"));
        service_Data.put("totalEventCount",servicedata[3]);
        servicedata[3] = String.valueOf(serviceObject.getInt("health"));
        service_Data.put("health",servicedata[3]);
        service_Data.put("impactingSubService",String.valueOf(serviceObject.getInt("impactingSubService")));
        service_Data.put("impactedEntityCount",String.valueOf(serviceObject.getInt("impactedEntityCount")));
        return service_Data;
    }

    public void waitForHealthScoreExpectedValue(int expected_cnt,String servicename, String jwt_token){
        CommonWaits commonwait = new CommonWaits();
        int healthscore = 0;
        log.info("wait for for service healthscore set to expected value");
        for (int cnt = 0; cnt< 8; cnt++){
            JSONObject record = searchAndGetServiceDetails(servicename, jwt_token);
            Iterator<String> keys = record.keys();
            JSONObject serviceObject = null;
            while (keys.hasNext()) {
                String candidateId = keys.next();
                JSONObject candidate = record.getJSONObject(candidateId);
                String keyVal = candidate.optString("key", "");
                if (record.length() > 1 && keyVal.toUpperCase().contains("SOLQA")) {
                    continue;
                }
                serviceObject = candidate;
                break;
            }
            if (serviceObject == null && record.keys().hasNext()) {
                String fallbackId = record.keys().next();
                serviceObject = record.getJSONObject(fallbackId);
            }
            if (serviceObject != null) {
                healthscore = serviceObject.getInt("health");
                if (healthscore == expected_cnt) {
                    return;
                }
            }
            log.error("Healthscore not as per expected Actual: " + healthscore + " Expected: " + expected_cnt);
            commonwait.waitForSometime(15000);
        }
        Assert.fail("Health Score not as expected " + healthscore);
    }

    public boolean getTenantFeatureFlagStatus(String featureflag_name,String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        log.info("requested Feature Flag Name" +  aiopstenantfeatures);

        if (this.tenant_features_response == null) {
            Response response = given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .when()
                    .get(aiopstenantfeatures);
            if (response.getStatusCode() != 200) {    
                log.error("Failed to retrieve tenant features. Response code is: "+response.getStatusCode());
                log.error("Tenant features failure response: " + response.getBody().asString());
                return false;
            }
            this.tenant_features_response = new JSONObject(response.getBody().asString());
            log.info("Tenant feature flag response: " + response.getBody().asString());
        }
        JSONArray feature_list = this.tenant_features_response.getJSONArray("tenantfeatures");
        for (Object featureBodyObj: feature_list){
            JSONObject featureBody = (JSONObject)featureBodyObj;
            String name = featureBody.getString("Name");
            boolean status = featureBody.getBoolean("Status");
            if ((name.compareToIgnoreCase(featureflag_name) == 0) && status){
                return true;
            }
        }
        return false;
    }

    public boolean waitForSituationCreationMsearch(String modelKey, String helixJwtToken, int waitTimeMillis){
        CommonWaits commonWait = new CommonWaits();
        String situationjsonfile = "msearchsituationquery.json";
        int situationCreationTime = 330; // Time in minutes for the situation to get generated after sending events/alarm generation. Right now API accepts values >=330
        int retryIntervalMillis = 10 * 1000; // Retry every 10 seconds
        long loopStartTime = System.currentTimeMillis();
        long loopEndTime = loopStartTime + waitTimeMillis;
        long situationTimeDiff = 300000;
        LocalDateTime now = LocalDateTime.now();

        // Get date-time from last 30 minutes
        LocalDateTime fewMinutesAgo = now.minusMinutes(situationCreationTime);

        // Formatter for the required format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        log.info("Current Date-Time: " + now.format(formatter));
        log.info("Date-Time "+situationCreationTime+" Minutes Ago: " + fewMinutesAgo.format(formatter));

        while (System.currentTimeMillis() < loopEndTime) {
            JSONObject msquery = hm_events.runMSearchQueryGetSituation(modelKey, helixJwtToken, situationjsonfile, situationTimeDiff);
            if (msquery == null){
                return false;
            }
            int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord > 0) {
                JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
                for (Object jsonobj: jsonarray) {
                    JSONObject sit_obj =  (JSONObject)jsonobj;
                    String EventClass = sit_obj.getJSONObject("_source").getString("class");
                    if(!EventClass.equals("Situation")){
                        continue;
                    }
                    String status = sit_obj.getJSONObject("_source").getString("status");
                    if (!status.equals("CLOSED")){
                        log.info("Service is impacted and situation is created returned true, exiting loop.");
                        return true;
                    }
                }
            }
            log.info("Service is not impacted, retrying every 10 seconds.");
            commonWait.waitForSometime(15000);
        }

        log.info("Loop finished after " + (waitTimeMillis / (60 * 1000)) + " minutes.");
        return false;

    }

    public boolean updateServiceHealthProfile(String service_id, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        String jsonFile = "aiopsservicehealthprofile.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent =  jsonContent.replace("<Service_node_id>",service_id);
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(aiopsServiceSave);
        return response.getStatusCode() == 200;

    }

    public String aiopsCreateK8sBlueprintApi(String blueprintName, String blueprintDefFile, String helixJwtToken) {
        // 1. Read JSON file
        Map<String, Object> data = null;
        if (blueprintDefFile != null) {
            try {
                byte[] jsonData = Files.readAllBytes(Paths.get(blueprintDefFile));
                String jsonString = new String(jsonData);
                // Substitute blueprintName if needed (assuming %s in JSON)
                jsonString = String.format(jsonString, blueprintName);
                data = objectMapper.readValue(jsonString, Map.class);
            } catch (Exception e) {
                log.info("Error reading blueprint definition: " + e.getMessage());
            }
        }

        if (data == null) {
            String errMsg = "No blueprint definition provided, returning without creating blueprint";
            log.info("Error: " + errMsg);
        }

        try {
            // 2. Prepare request data
            String requestData = objectMapper.writeValueAsString(data);
            RestAssured.baseURI = hmHostName;
            // 3. Make POST request
            Response response = RestAssured.given()
                    .header("Authorization", "Bearer "+helixJwtToken)
                    .header("Content-Type", "application/json")
                    .body(requestData)
                    .post(serviceMgmtCreateBlueprintApi);

            int status = response.getStatusCode();

            if (status == 200) {
                // 5. Parse blueprintId from response
                String respBody = response.getBody().asString();
                Map<String, Object> respMap = objectMapper.readValue(respBody, Map.class);
                Map<String, Object> blueprintIdMap = parseJson(respMap, "blueprintId");
                String blueprintId = blueprintIdMap != null ? (String) blueprintIdMap.get("id") : null;
                return blueprintId;
            } else {
                log.error("Blueprint not created.");
            }
        } catch (Exception e) {
            log.error("Blueprint not created:"+ e.getMessage());
        }
        return null;
    }

    // Helper to parse nested JSON
    private Map<String, Object> parseJson(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }


    public String[] getBlueprintID(String blueprintName, String helixJwtToken) {
        String requestBody = "{\"outputAttributes\":[\"key\",\"name\",\"namespace\",\"description\",\"defaultBpId\",\"read_only\",\"state\",\"instances_count\"],\"limit\":50,\"filters\":[{\"attribute\":\"state\",\"value\":\"ENABLED\",\"comparator\":\"equals\"}],\"multiSortMeta\":[{\"attribute\":\"name\",\"order\":1}],\"globalStats\":true}";

        Response response = RestAssured
                .given()
                .baseUri(hmHostName)
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(getAiopsBlueprint);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String output = response.getBody().asString();
            try {
                JSONObject jsonResponse = new JSONObject(output);
                JSONArray blueprints = jsonResponse.getJSONArray("blueprints");
                for (int i = 0; i < blueprints.length(); i++) {
                    JSONObject blueprint = blueprints.getJSONObject(i);
                    String name = blueprint.optString("name", "");

                    if (name.equalsIgnoreCase(blueprintName)) {
                        JSONObject blueprintId = blueprint.getJSONObject("blueprintId");
                        String id = blueprintId.getString("id");
                        String version = blueprintId.getString("version");
                        return new String[]{id, version};
                    }
                }
                return null; // Not found
            } catch (JSONException e) {
                log.error("Error parsing JSON response: " + e.getMessage()); 
            } catch (Exception e){
                log.error("Error retrieving blueprint ID: " + e.getMessage());
            }
        } else {
            String output = response.getBody().asString();
            log.error("Failed to retrieve blueprints: " + output);

        }
        return null;
    }

    public void enableBlueprint(String blueprintName, String helixJwtToken) {
        RestAssured.baseURI = hmHostName;

        String[] result = getBlueprintID(blueprintName, helixJwtToken);
        if (result == null) {
            throw new RuntimeException("Blueprint with name " + blueprintName + " not found.");
        }

        String id = result[0];
        String version = result[1];
        String state = "ENABLED";

        String requestBody = String.format("{\"id\":\"%s\",\"version\":\"%s\",\"state\":\"%s\"}", id, version, state);

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(stateAiopsBlueprint);

        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
             throw new RuntimeException("Failed to enable blueprint: " + response.getBody().asString());
        }

    }

    public boolean aiopsDeleteBlueprintByIdApi(String blueprintId, String helixJwtToken) {
        // Prepare request data
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", blueprintId);
        requestData.put("version", "1.0.0");
        RestAssured.baseURI = hmHostName;

        // Make the API call
        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .contentType(ContentType.JSON)
                .body(requestData)
                .post(aiopsBlueprintDelete);

        int statusCode = response.getStatusCode();
        String output;
        if (statusCode == 200) {
            output = response.getBody().asString();// or as needed
            log.info("Topology search response: " + output);
            return true;
        } else {
            output = response.getBody().asString();
            log.error("Topology search failed response: " + output);
            return false;// or response.getBody().asString() for text
        }
    }

    public Object aiopsGetHealthTimeline(String ServiceKey, String helixJwtToken){
        HMEvents hm_events = new HMEvents();
        long timeDiff = 10800;
        long endtime = hm_events.getBhomServerTime(helixJwtToken) / 1000;
        long start_time =  endtime - timeDiff;
        RestAssured.baseURI = hmHostName;
        String end_point = aiopsHealthTimeLine  + ServiceKey + "/timeline?startTime=" + start_time + "&endTime=" + endtime + "&step=5m&timelineType=HEALTH&timelineType=EVENT&auto=true&type=key&entityType=service";

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(end_point);

        if (response.getStatusCode() != 200) {
            String output = response.getBody().asString();
            log.error(output);
            return false;
        }
        JSONObject healthTimelineResponse = new JSONObject(response.getBody().asString());
        log.info("Health timeline success response: " + response.getBody().asString());
        return healthTimelineResponse;

    }

    public Map<String,String> aiopsGetCITopology(String ServiceKey, String helixJwtToken) throws IOException {
        // Prepare request data
        RestAssured.baseURI = hmHostName;
        long timeDiff = 2;
        String jsonFile = "getCITopology.json";
        String jsonContent = readJSon(jsonFile);
        long end_time = (hm_events.getBhomServerTime(helixJwtToken));
        long start_time =  end_time - timeDiff;

        jsonContent =  jsonContent.replace("<SERVICE_KEY>",ServiceKey);
        jsonContent =  jsonContent.replace("<START_TIME>",Long.toString(start_time));
        jsonContent =  jsonContent.replace("<END_TIME>",Long.toString(end_time));
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(aiopsCITopology);

        int statusCode = response.getStatusCode();
        String output;
        if (statusCode == 200) {
            ObjectMapper mapper = new ObjectMapper();
            output = response.getBody().asString();// or as needed 
            log.info("Get CI Topology success response: " + (output.length() > 200 ? output.substring(0, 200) : output));
            JsonNode rootNode = mapper.readTree(output);
            JsonNode nodesNode = rootNode.get("nodes");
            Map<String, String> result = new HashMap<>();

            // Iterate over each field inside "nodes"
            Iterator<Map.Entry<String, JsonNode>> fields = nodesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();

                JsonNode node = entry.getValue();          // the inner object
                String id   = node.path("id").asText();    // e.g. "47263e640100c0115f..."
                String name = node.path("name").asText();  // e.g. "pw_j2ee_logging.sar …"
                String kind = node.path("kind").asText();  // e.g. "pw_j2ee_logging.sar …"

                // It is safe to add to the map even if id or name are empty – you can adjust
                // the logic if you want to skip such entries.
                result.put(name,id);
                result.put(name+"_kind",kind);
            }
            return result;



        } else {
            output = response.getBody().asString();
            log.error("Get CI Topology failed response: " + output);
            return new HashMap<>();// or response.getBody().asString() for text
        }
    }
    public int aiopsgetNodeHealthScore(String nodeid, String helixJwtToken){
        String end_point = aiopsNodeDetail + nodeid;
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(end_point);

        if (response.getStatusCode() == 200) {
            JSONObject healthResponse = new JSONObject(response.getBody().asString());
            log.info("Node health response: " + response.getBody().asString());
            return healthResponse.getInt("health");
        }
        return -1;
    }
    public boolean runRanker(String epochStart, String epochEnd, String helixJwtToken) {
        RestAssured.baseURI = hmHostName;
        String apiEndPoint = aiopsRunRanker;
        log.info("Run Ranker API Endpoint: " + apiEndPoint);
        String jsonFile = "runRanker.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent =  jsonContent.replace("EPOCH_START", epochStart).replace("EPOCH_END", epochEnd);
        log.info("Run Ranker from " + epochStart + " to " + epochEnd);

        Response response = given()
            .header("Authorization", "Bearer "+helixJwtToken)
            .header("Content-Type", "application/json")
            .body(jsonContent)
            .post(apiEndPoint);
        int status = response.statusCode();

        if (status != 200){
            log.error("Response is:\n" + status);
            return false;
        }

        return true;
    }


    public JSONArray getChangeAssociatedWithService(String ServiceKey, String ServiceID, String helixJwtToken){
        HMEvents hm_events = new HMEvents();
        String aiopsPCAGetChangeAssociatedWithService = "/pca-api-service/api/v1.0/pca/node/changes";
        long timeDiff = 10800000;
        long endtime = hm_events.getBhomServerTime(helixJwtToken) ;
        long starttime =  endtime - 10808070;
        endtime += 18000000;
        RestAssured.baseURI = hmHostName;
        String jsonFile = "getChangeAtServicelevel.json";
        String jsonContent = readJSon(jsonFile);
        long transittime =  endtime - timeDiff;

        jsonContent =  jsonContent.replace("<SERVICE_KEY>",ServiceKey);
        jsonContent =  jsonContent.replace("<SERVICE_ID>",ServiceID);
        jsonContent =  jsonContent.replace("<START_TIME>",Long.toString(starttime));
        jsonContent =  jsonContent.replace("<END_TIME>",Long.toString(endtime));
        jsonContent =  jsonContent.replace("<TRANSIT_TIME>",Long.toString(transittime));

        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .when()
                .post(aiopsPCAGetChangeAssociatedWithService);

        int statusCode = response.getStatusCode();
        log.info("Change list for service response code: " + statusCode);
        if (statusCode == 200) {
            log.info("Change list for service response: " + response.getBody().asString());
            return new JSONArray(response.getBody().asString());
        }
        return null;
    }

    public JSONArray getServiceChangeRCAList(String ServiceKey, String ServiceID, String helixJwtToken){
        RestAssured.baseURI = hmHostName;
        long timeDiff = 10800000 * 3;
        long endtime = hm_events.getBhomServerTime(helixJwtToken) + 3*60*60*1000;
        long starttime =  endtime - timeDiff;
        String aiopsChangeRCAEndPoint = "/pca-api-service/api/v1.0/pca/node/changes?node_id=" + ServiceID + "&start_time= " + Long.toString(starttime) + "&end_time=" + Long.toString(endtime) + "&service_key=" + ServiceKey;
        Response response = given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .when()
                .get(aiopsChangeRCAEndPoint);
        if (response.getStatusCode() == 200) {
            return new JSONArray(response.getBody().asString());
        }
        return new JSONArray();
    }

    public void deleteServiceModel(String servicePrefix, String jwtToken) {
        RestAssured.baseURI = hmHostName;

        JSONObject services = searchAndGetServiceDetails(servicePrefix,jwtToken);
        if (services == null) {
            return;
        }

        Iterator<String> iterator = services.keys();
        while (iterator.hasNext()) {
            String id = iterator.next();
            JSONObject serviceObject = services.getJSONObject(id);
            String name = serviceObject.optString("name");
            if (!name.startsWith(servicePrefix)) {
                continue;
            }
            JSONObject deleteBody = new JSONObject();
            deleteBody.put("serviceKey", serviceObject.optString("key"));
            Response deleteResponse = RestAssured.given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(ContentType.JSON)
                    .body(deleteBody.toString())
                    .post(aiopsServiceDelete);
            if (deleteResponse.getStatusCode() != 200) {
                log.error("Failed to delete service " + name + ": " + deleteResponse.getBody().asString());
            }
        }
    }

    public String getEntityTypeId(String nodeId, String EntityType, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        String endPoint = "/service-management/api/v1.0/metric/entities";
        String fileName = "getEntityTypeId.json";
        String jsonContent = readJSon(fileName);
        jsonContent = jsonContent.replace("<NODE_ID>", nodeId);
        jsonContent = jsonContent.replace("<ENTITY_TYPE>", EntityType);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(endPoint);
        if (resp.getStatusCode() > 299) {
            log.error("Entity type search failed: " + resp.getStatusCode());
            return null;
        }
        log.info("Health timeline response: " + resp.getBody().asString());
        JSONArray results = new JSONObject(resp.getBody().asString()).getJSONArray("entities");
        if(results.length() <  1){
            log.info("getEntityTypeId: No data available ");
            return null;
        }
        return results.getJSONObject(0).getString("entityId");
    }
    public JSONArray getDiscoveryNodeId(String query, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(query)
                .post(TOPOLOGY_SEARCH);
        if (resp.getStatusCode() > 299) {
            log.error("Topology search failed: " + resp.getStatusCode());
            return null;
        }
        log.info("Topology search response: " + resp.getBody().asString());
        JSONArray results = new JSONArray(resp.getBody().asString());
        return results;
    }

    public String createServiceWithStaticNodeusingMulti(String serviceName, String associateList,List<String> NodeIDList, boolean shipService, String parentServiceName, String jwtToken) {
        //get the ADDM ID
        RestAssured.baseURI = hmHostName;
        List<String> nodeIds = new ArrayList<>();
        String serviceModelFileName = "createServiceModelPrediction.json";
        for(String addmId:NodeIDList){
            nodeIds.add("\""+addmId+"\": {}");

        }
        String nodeIdsString = String.join(", ", nodeIds);
        String jsonContent = readJSon(serviceModelFileName);
        String child_uuid = UUID.randomUUID().toString();
        
        jsonContent = jsonContent.replace("<NODE_IDS>", nodeIdsString);
        jsonContent = jsonContent.replace("<SERVICE_NAME>", serviceName);
        jsonContent = jsonContent.replace("<TEMP_KEY>", child_uuid);
        jsonContent = jsonContent.replace("<Associated Elements>", associateList);
        // Simplified: create blank service and add a CI by name
        if(shipService){
            String parent_uuid = UUID.randomUUID().toString();
            JSONObject childServices = new JSONArray(jsonContent).getJSONObject(0);
            jsonContent = readJSon(serviceModelFileName);
            jsonContent = jsonContent.replace("<NODE_IDS>", "");
            jsonContent = jsonContent.replace("<SERVICE_NAME>", parentServiceName);
            jsonContent = jsonContent.replace("<TEMP_KEY>", parent_uuid);
            jsonContent = jsonContent.replace("<Associated Elements>", "");
            JSONArray parentArray = new JSONArray(jsonContent);
            JSONObject parentBody = parentArray.getJSONObject(0).getJSONObject("body");
            parentBody.put("metadata", new JSONObject().put("_associatedElements", "[]"));
            String dependedUponChildKey = "$$$$_" + child_uuid;
            parentBody.put("depended_upon", new JSONObject()
                    .put(dependedUponChildKey, new JSONObject().put("_associatedElements", "[]")));
            jsonContent = parentArray.toString();
            JSONObject parentServices = parentArray.getJSONObject(0);
            JSONArray parentServicesArray = new JSONArray();
            parentServicesArray.put(parentServices);
            parentServicesArray.put(childServices);

            jsonContent = parentServicesArray.toString();
        }
            



        Response saveResp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(SERVICE_SAVE_MULTI);

        if (saveResp.getStatusCode() > 299) {
            log.error("Fail to save service Model: " + saveResp.getStatusCode());
            return null;
        }
        log.info(saveResp.getBody().asString());
        String serviceId = new JSONArray(saveResp.getBody().asString()).getJSONObject(0).getJSONObject("body").optString("node_id", "");
        if(serviceId.isEmpty()){
            log.error("Service creation Failed");
            return null;
        }

        // TODO: add static CI to service model (requires CI id resolution)
        log.info("Service created: " + serviceName + " (" + serviceId + ")");
        return serviceId;
    }

    public JSONObject getPredictionBasedOnService(String serviceName, String jwtToken) throws InterruptedException {
        // Poll prediction-api-service for the service name
        RestAssured.baseURI = hmHostName;
        JSONObject body = new JSONObject()
                .put("start_time", Instant.now().getEpochSecond() - 10800)
                .put("end_time", Instant.now().getEpochSecond())
                .put("name", "")
                .put("tags", new JSONArray())
                .put("severity", new JSONArray())
                .put("page_number", 0)
                .put("page_size", 50)
                .put("sort_by", "impacted_in")
                .put("sort_order", "asc");


        Response resp = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .post(PREDICTION_SEARCH);

        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get prediction based on service failed: " + resp.getStatusCode());
            return null;
        }

        return new JSONObject(resp.getBody().asString());
    }

    public boolean waitForHealthScoreExpectedValue(int expected_cnt,String servicename, String jwt_token, int condition){
        CommonWaits commonwait = new CommonWaits();
        int healthscore = 0;
        log.info("wait for for service healthscore set to expected value");
        for (int cnt = 0; cnt< 9; cnt++){ // wait for 2 minutes
            Map<String, String> bsHealthScore = getHealthScore(servicename, jwt_token);
            healthscore = Integer.parseInt(bsHealthScore.get("health"));
            if (condition == 1) { // equal
                if (healthscore == expected_cnt) {
                    return true;
                }
            } else if (condition == 2) { // greater than
                if (healthscore > expected_cnt) {
                    return true;
                }
            } else if (condition == 3) { // less than
                if (healthscore < expected_cnt) {
                    return true;
                }
            }
            log.info("Healthscore not as per expected Actual: " + healthscore + " Expected: " + expected_cnt);
            commonwait.waitForSometime(15000);
        }
        log.info("Health Score not as expected " + healthscore);
        return false;
    }
    public JSONObject getServiceModel(String serviceName, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        String getaiopsServiceModel = "getAiopsServiceModel.json";
        String jsonContent = readJSon(getaiopsServiceModel);
        jsonContent = jsonContent.replace("<SERVICE_KEY>", serviceName);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(GET_SERVICE_ENDPOINT);
        
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get service model failed: " + resp.getStatusCode());
            return null;
        }
        
        return new JSONObject(resp.getBody().asString());
    }

    public JSONObject getSituationConfiguration(String jwtToken) {
        RestAssured.baseURI = hmHostName;
        String jsonContent = "{}";
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(CONFIGURATION_ENDPOINT);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get situation configuration failed: " + resp.getStatusCode());
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public boolean updateSituationConfiguration(String jsonContent, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(SITUATION_CONFIGURATION_ENDPOINT);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Update situation configuration failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    public boolean enablercafeature(String jwtToken) {
        RestAssured.baseURI = hmHostName;
        String ENABLE_RCA_FEATURE_ENDPOINT = "/aiops-config/api/v1.0/features/";
        JSONObject body = new JSONObject();
        body.put("features", new JSONObject().put("root-cause-isolation", true));
        String jsonContent = body.toString();
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(ENABLE_RCA_FEATURE_ENDPOINT);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Enable rca feature failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    public boolean enableMLSituationsFeature(String jwtToken) throws InterruptedException {
        CommonWaits commonWait = new CommonWaits();
        if(getTenantFeatureFlagStatus("ml-situations", jwtToken)) {
            log.info("ml-situations is already enabled.");
            return true;
        }
        RestAssured.baseURI = hmHostName;
        String ENABLE_MULTI_SITUATION_ENDPOINT = "/aiops-config/api/v1.0/features/";
        JSONObject body = new JSONObject();
        body.put("features", new JSONObject().put("ml-situations", true));
        String jsonContent = body.toString();
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(ENABLE_MULTI_SITUATION_ENDPOINT);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Enable ML Situations feature failed: " + resp.getStatusCode());
            return false;
        }
        enablercafeature(jwtToken);   
        if (!enablercafeature(jwtToken)) {
            log.error("Enable RCA feature failed: " + resp.getStatusCode());
            return false;
        }
        commonWait.waitForSometime(60000);
        return true;
    }

    public boolean enablePredictionFeature(String jwtToken) {
        if(getTenantFeatureFlagStatus("predictions", jwtToken)) {
            log.info("predictions is already enabled.");
            return true;
        }
        RestAssured.baseURI = hmHostName;
        String ENABLE_PREDICTION_FEATURE_ENDPOINT = "/aiops-config/api/v1.0/features/";
        JSONObject body = new JSONObject();
        body.put("features", new JSONObject().put("predictions", true));
        String jsonContent = body.toString();
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(ENABLE_PREDICTION_FEATURE_ENDPOINT);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Enable predictions feature failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    private String truncateForLog(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        int suffixLength = "...(truncated)".length();
        int endIndex = Math.max(0, maxLength - suffixLength);
        return value.substring(0, endIndex) + "...(truncated)";
    }

    public void settenantfeaturesresponse(JSONObject tenant_features_response) {
        this.tenant_features_response = tenant_features_response;
    }

    public JSONObject getSituationGlobalHealthConfiguration(String jwtToken) {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .get(SITUATION_CONFIGURATION_ENDPOINT);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get situation configuration failed: " + resp.getStatusCode());
            return null;  
        }
        return new JSONObject(resp.getBody().asString());
    }

    public boolean updateServiceHealthProfileConfiguration(String jsonContent, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(SERVICE_SAVE_MULTI);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Update service health profile configuration failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    public JSONArray getServiceModelDetail(String serviceKey, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .get(GET_SERVICEMODEL_ENDPOINT + serviceKey + "?expand_related=true&related_state=true");
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get service model failed: " + resp.getStatusCode());
            return null;
        }
        return new JSONArray(resp.getBody().asString());
    }

}