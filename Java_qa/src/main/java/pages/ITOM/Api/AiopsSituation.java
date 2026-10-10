package pages.ITOM.Api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.CommonMethods;
import util.CommonWaits;
import util.ExcelReaderForMLSituation;
import util.ReadPropertiesFile;
import util.SolqaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
 Purpose:		This class contains common functions for AiopsSituation using API's
 @author			almahaja
  ********************************************************************** */

public class AiopsSituation {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituation.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static String hmHostName = config.get("ADE_PLATFORM_URL");
    private HMEvents hm_events = new HMEvents();
    private Aiops aiopsApiCore = new Aiops();
    CommonMethods commonmethod = new CommonMethods();
    LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
    private static final long situationTimeDiff = 300000;
    SoftAssert softAssert = null;
    private String jwtToken = null;
    CommonWaits commonWait = new CommonWaits();
    private String saveServiceEndPoint = "/service-management/api/v1.0/service/save";
    private String getServiceEndPoint = "/service-management/api/v1.0/service/get";
    private String getaiopsServiceModel = "getAiopsServiceModel.json";
    private String updateEventEndPoint = "/events-service/api/v1.0/events/";
    private static String eventClass = "NAGIOS_EV1";
    private static String location = "Pune";
    public static boolean incidentValidation = false;
    public static final String event_tag = "";
    private String updateServiceEndpoint = "/smart-graph-api/api/v1.11/models/multi";
    private String SituationConfigurationEndPoint = "/aiops-config/api/v1.1/situation_configurations/search";
    private String SituationConfigurationUpdateEndPoint = "/aiops-config/api/v1.1/situation_configurations/";
    private static final String aiopssituationconfigUrl = "/aiops-config/api/v1.0/situation_configurations/";
    private static final String SERVICE_SAVE_MULTI = "/smart-graph-api/api/v1.11/models/multi";
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();
    private int expectedMinimumCICount = 0;

    /** Whether IdleTime was staged for PATCH in {@link #adjustIdleTimeMetricOnServiceModel(String, String)}. */
    public enum IdleTimeMetricChange {
        /** IdleTime selector was appended (cloned from Utilization or first selector). */
        ADDED,
        /** IdleTime selector was removed from metrics_kpi. */
        REMOVED,
        /** No metric change was staged (error before PATCH or no metric_group/metrics_kpi block). */
        NONE
    }

    /** Result of {@link #adjustIdleTimeMetricOnServiceModel(String, String)}: success, HTTP status, and add/remove. */
    public static final class IdleTimeAdjustResult {
        public final boolean success;
        /** HTTP status of the last relevant call, or -1 when not applicable. */
        public final int statusCode;
        public final IdleTimeMetricChange change;

        public IdleTimeAdjustResult(boolean success, int statusCode, IdleTimeMetricChange change) {
            this.success = success;
            this.statusCode = statusCode;
            this.change = change;
        }
    }

    // Function to read JSON file and return it's content
    public boolean CloseAllEventOnService(String serviceName) throws JsonProcessingException {
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String situationJsonFile = "msearchgetAllEventOnService.json";
        long situationTimeDiff = 2;
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        JSONObject msquery = hm_events.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken,situationJsonFile,situationTimeDiff);
        if (msquery == null){
            return false;
        }
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord == 0){
            return true;
        }

        JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
        for (Object jsonobj: jsonarray) {
            JSONObject sit_obj =  (JSONObject)jsonobj;
            String eventId = sit_obj.getJSONObject("_source").getString("_identifier");
            hm_events.closeEventApi(eventId,"closing Event by api", jwtToken);
            
        }
        return true;
    }

    /**
     * Enables or disables a situation configuration via REST API.
     */
    public void updateSituationConfiguration(boolean status, String situationId) throws IOException, InterruptedException {
        String situationUrl = aiopssituationconfigUrl + situationId;
        String jsonContent = aiopsApiCore.readJSon("updatesituationconfigurationhighestseverity.json");
        log.info("Updating situation configuration status=" + status + " for id=" + situationId);
        if (status) {
            jsonContent = jsonContent.replace("<STATUS>", "true");
        }
        else{
            jsonContent = jsonContent.replace("<STATUS>", "false");
        }
        String[] arry = {"200", jsonContent};
        logAnalyticsPage.Send_RestCall("PUT", situationUrl, arry);
    }

    /**
     * Waits for a new event to be added into an existing situation.
     */
    public void waitForNewEventAddedToSituation(int expectedCount, String identifier, String jwtToken)
            throws JsonProcessingException {
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Waiting for new events to be added to the situation (max 3 minutes).");

        for (int cnt = 0; cnt < 16; cnt++) {
            List<String> eventIds = aiopsApiCore.getEventIdsAssociatedWithSituation(identifier, jwtToken);
            log.info("Event in Situation " + eventIds.size() + " Expected Event " + expectedCount);
            if (expectedCount <= eventIds.size()) {
                log.info("Expected event count reached for situation " + identifier);
                return;
            }
            commonWait.waitForSometime(15000);
        }

        Assert.fail("New event was not added to the situation within 3 minutes.");
    }


    /**
     * Retrieves the first situation ID from the situation configurations.
     */
    public String getSituationId() throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject record = aiopsApiCore.getSituationConfiguration(jwtToken);
        if (record == null) {
            Assert.fail("Failed to get situation configuration");
        }
        return record.getJSONArray("situations").getJSONObject(0).getString("id");
    }

    /**
     * Sets up the service whitelist for a given situation.
     */
    public void setSituationWhitelist(String[] serviceList, boolean updateWhiteListField, String situationId)
            throws IOException, InterruptedException {
        String situationUrl = aiopssituationconfigUrl + situationId;
        log.info("Setting situation whitelist updateWhiteListField=" + updateWhiteListField + " for id=" + situationId);
        ObjectMapper mapper = new ObjectMapper();
        String jsonFile = "mlsituationconfigwhitelist.json";
        String initialJson = aiopsApiCore.readJSon(jsonFile);

        ObjectNode situationBody = (ObjectNode) mapper.readTree(initialJson);
        ((ObjectNode)situationBody.get("configuration")).put("incident_service_key_list", "");
        String emptyBody = situationBody.toString();
        String[] arry = {"200", emptyBody};

        logAnalyticsPage.Send_RestCall("PUT", situationUrl, arry);

        if (!updateWhiteListField) {
            return;
        }

        List<String> bsKeyList = new ArrayList<>();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        for (String serviceName : serviceList) {
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            bsKeyList.add(healthScore.get("key"));
        }

        String bsKey = String.join(",", bsKeyList);
        ((ObjectNode)situationBody.get("configuration")).put("incident_service_key_list", bsKey);

        arry[1] = situationBody.toString();
        logAnalyticsPage.Send_RestCall("PUT", situationUrl, arry);
        log.info("Updated situation whitelist with keys: " + bsKey);
    }

    public List<String> validate_situation_creation(String[] serviceCheckList){
        String situationjsonfile = "msearchsituationquery.json";
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        List<String> situationList = new ArrayList<>();
        log.info("Validating situation creation for services: " + String.join(",", serviceCheckList));
        for (String serviceName : serviceCheckList) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            JSONObject situationObject = hm_events.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationjsonfile,situationTimeDiff);
            JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
            String situationIdentifier = hitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
            situationList.add(situationIdentifier);
            log.info("Captured situation identifier " + situationIdentifier + " for service " + serviceName);
        }
        return situationList;
    }

    public boolean close_service_situation(String[] service_list) throws JsonProcessingException {
        String situationjsonfile = "msearchsituationquery.json";

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Closing service situations for services: " + String.join(",", service_list));
        for (String serv_name: service_list) {
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serv_name, jwtToken);
            JSONObject msquery = hm_events.runMSearchQueryGetSituation(healthScore.get("key"), jwtToken, situationjsonfile, situationTimeDiff);
            int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord > 0){
                String sit_id = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
                hm_events.closeEventApi(sit_id,"closing Event by api", jwtToken);
                log.info("Closed situation " + sit_id + " for service " + serv_name);
            }
        }
        return true;
    }

    public boolean validate_event_under_situation(String situationid, boolean cond) throws JsonProcessingException {
        // Check non-whitelisted service
        String incidentId = "";
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        log.info("Wait for incident getting associated with Situation");
        for (int cnt = 0; cnt < 4; cnt++) {
            JSONObject jsonObject = new JSONObject(hm_events.eventDetails(situationid, jwtToken));
            incidentId = jsonObject.getJSONObject("response").getJSONObject("allslots").getString("incident_id");
            if (incidentId.length() < 3) {
                commonWait.waitForSometime(10000);
                continue;
            }
            break;
        }
        if (incidentId.length() < 3) {
            Assert.fail("Incident did not get created.");
        }
        log.info("Incident associated with situation: " + incidentId);

        List<String> situationEventList = aiopsApiCore.getEventIdsAssociatedWithSituation(situationid, jwtToken);
        int occurance_count = 0;
        for (String eventId : situationEventList) {
            commonWait.waitForSometime(1000);
            JSONObject jsonObject = new JSONObject(hm_events.eventDetails(eventId, jwtToken));
            String eventIncidentId = jsonObject.getJSONObject("response").getJSONObject("allslots").getString("incident_id");
            if (cond) {
                log.info("Validating equal to condition for incident");
                if (eventIncidentId.length() > 3 && eventIncidentId.equals(incidentId)) {
                    try {
                        Assert.fail("Event Incident not created situation: " + incidentId + " event: " + eventIncidentId);
                    }catch (AssertionError e) {
                        if (occurance_count > 0) {
                            softAssert.fail(e.getMessage());
                        }
                        occurance_count += 1;
                    }
                }
            }else{
                log.info("Validating not equal to condition for incident");
                if (eventIncidentId.length() > 3 && !eventIncidentId.equals(incidentId)) {
                    try {
                        Assert.fail("Event Incident got created situation: " + incidentId + " event: " + eventIncidentId);
                    }catch (AssertionError e) {
                        softAssert.fail(e.getMessage());
                    }
                }
            }
        }
        return true;
    }

    public String extractEventId(String responseString) {
        // Try to extract eventId from response JSON
        // Example response: {"0":{"eventId":"...","...":...}}
        try {
            JSONObject eventid = new JSONObject(responseString);
            String eventid_critical = eventid.getJSONObject("0").getJSONArray("resourceId").getString(0);
            return eventid_critical;
        } catch (Exception e) {
            log.warn("Failed to extract eventId from response: " + responseString);
        }
        return null;
    }

    // Helper to extract health score from getHealthScore map
    public int extractHealthScore(java.util.Map<String, String> healthMap) {
        if (healthMap == null) return -1;
        if (healthMap.containsKey("health")) {
            try { return Integer.parseInt(healthMap.get("health")); } catch (Exception e) { return -1; }
        }
        return -1;
    }


    public boolean updateEventProfile(String serviceId) throws IOException, InterruptedException {
        Aiops aiopsApiCore = new Aiops();
        String jwtToken = commonmethod.refreshJwtToken(null);
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        JSONObject jsonObject = aiopsApiCore.getServiceModel(serviceId, jwtToken);
        JSONArray nodes = jsonObject.optJSONArray("nodes");
        if (nodes == null || nodes.length() == 0) {
            Assert.fail("No nodes returned for serviceId " + serviceId + "; response=" + jsonObject.toString());
            return false;
        }
        JSONObject serviceModel = nodes.getJSONObject(0);
        String id = serviceModel.getString("id");
        String compatibleOnly = serviceModel.getString("compatibleOnly");
        JSONArray associatedElements = serviceModel.getJSONArray("associatedElements");

        // Build the increments JSON structure
        JSONObject increments = new JSONObject();
        JSONArray nodesArr = new JSONArray();
        JSONObject nodeObj = new JSONObject();
        nodeObj.put("id", id);
        nodeObj.put("compatibleOnly", compatibleOnly);

        JSONArray assocArr = new JSONArray();
        // health_processing
        for (int i = 0; i < associatedElements.length(); i++) {
            JSONObject assocElem = associatedElements.getJSONObject(i);
            String type = assocElem.getString("type");
            if (type.equals("health_processing")) {
                assocArr.put(assocElem);
            } else if (type.equals("metric_group")) {
                assocArr.put(assocElem);
            } else if (type.equals("event_configuration")) {
                // Replace elements with required event_rule
                JSONObject eventConfig = new JSONObject(assocElem.toString());
                JSONArray elements = new JSONArray();
                JSONObject eventRule = new JSONObject();
                eventRule.put("element_type", "event_rule");
                eventRule.put("name", "automation");
                eventRule.put("selectors", new JSONArray());
                JSONArray rulesArr = new JSONArray();
                JSONObject ruleObj = new JSONObject();
                ruleObj.put("slot", "severity");
                ruleObj.put("operator", "in");
                ruleObj.put("values", new JSONArray().put("critical"));
                rulesArr.put(ruleObj);
                eventRule.put("rules", rulesArr);
                elements.put(eventRule);
                eventConfig.put("elements", elements);
                assocArr.put(eventConfig);
            }
        }
        nodeObj.put("associatedElements", assocArr);
        nodesArr.put(nodeObj);
        increments.put("links", new JSONArray());
        increments.put("nodes", nodesArr);

        JSONObject result = new JSONObject();
        result.put("increments", increments);
        // Return the result variable
        // You can return or use 'result' as needed
        // Example: return result;
        // For now, just log it
        log.info("updateEventProfile increments JSON: " + result.toString());
        logAnalyticsPage.Send_RestCall("POST", saveServiceEndPoint, new String[]{"200",result.toString()});
        return true;

        // If you want to return it, change the method signature to return JSONObject
    }

    public boolean deleteEventProfile(String serviceId) throws IOException, InterruptedException {
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONObject jsonObject = aiopsApiCore.getServiceModel(serviceId, jwtToken);
        logAnalyticsPage.jsonObject = null;
        JSONArray nodes = jsonObject.optJSONArray("nodes");
        if (nodes == null || nodes.length() == 0) {
            Assert.fail("No nodes returned for serviceId " + serviceId + "; response=" + jsonObject.toString());
            return false;
        }
        JSONObject serviceModel = jsonObject.getJSONArray("nodes").getJSONObject(0);
        String id = serviceModel.getString("id");
        String compatibleOnly = serviceModel.getString("compatibleOnly");
        JSONArray associatedElements = serviceModel.getJSONArray("associatedElements");

        // Build the increments JSON structure for delete
        JSONObject increments = new JSONObject();
        JSONArray nodesArr = new JSONArray();
        JSONObject nodeObj = new JSONObject();
        nodeObj.put("id", id);
        nodeObj.put("compatibleOnly", compatibleOnly);

        JSONArray assocArr = new JSONArray();
        for (int i = 0; i < associatedElements.length(); i++) {
            JSONObject assocElem = associatedElements.getJSONObject(i);
            String type = assocElem.getString("type");
            if (type.equals("health_processing")) {
                assocArr.put(assocElem);
            } else if (type.equals("metric_group")) {
                // Do not modify metric_group, keep as-is
                assocArr.put(assocElem);
            } else if (type.equals("event_configuration")) {
                // Keep elements empty
                JSONObject eventConfig = new JSONObject(assocElem.toString());
                eventConfig.put("elements", new JSONArray());
                assocArr.put(eventConfig);
            }
        }
        nodeObj.put("associatedElements", assocArr);
        nodesArr.put(nodeObj);
        increments.put("links", new JSONArray());
        increments.put("nodes", nodesArr);

        JSONObject result = new JSONObject();
        result.put("increments", increments);

        log.info("deleteEventProfile increments JSON: " + result.toString());
        logAnalyticsPage.Send_RestCall("POST", saveServiceEndPoint, new String[]{"200",result.toString()});

        return true;
    }

    public boolean run_ml_scenario(int testcase_number, SoftAssert softAssert) throws IOException {
        
        boolean iapsrnable = false;
        String mlSheetName = "mlcheck";
        Aiops aiopsApiCore = new Aiops();
        HMEvents hm_events = new HMEvents();
        CommonWaits commonWait = new CommonWaits();
        List<Map> node_list = new ArrayList<>();
        String jwtToken = commonmethod.refreshJwtToken(null);
        String incidentjsonfile = "getServiceIncident.json";
        int currentincidentcount = 0;
        log.info("Testcase Number : " + testcase_number);
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        log.info("CLose all open event on Nagios class");
        hm_events.CloseAllOpenEvent(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        
        log.info("Check Service Model Exist");
        String[] bussness_service = row_data.get(0).get("Business Name").toString().split((","));
        iapsrnable = hm_events.getIncidentPolicyEnable(jwtToken);
        aiopsApiCore.searchAndGetServiceDetails(bussness_service[0],jwtToken);
        for(String service_name: bussness_service){
            CloseAllEventOnService(service_name);
        }
        log.info("Get init service health score");
        List<Map<String, String>> businessHealthList = new ArrayList<>();
        for(String service_name: bussness_service){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
            commonWait.waitForSometime(5000);
            healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
            businessHealthList.add(healthScore);
            if (Integer.parseInt(healthScore.get("actualHealth")) != 100){
                softAssert.fail("Health Score not correct for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
            }
        }

        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));
        String[] ci_priority = row_data.get(0).get("Impact Priority").toString().split((","));
        String[] ci_severity = row_data.get(0).get("Impact Severity").toString().split((","));
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        String msg;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        int no_of_event_per_node = (int)Double.parseDouble(row_data.get(0).get("No of Event per Node").toString());
        int total_rec = ci_list.length;
        for(int i =0;i< total_rec;i+= 1){
            msg = "Memory utilization of " + ci_list[i] + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[i], msg, ci_severity[i], location, event_tag);
        }
        log.info("start timer to check how long it to take situation creation");
        long sepochTime = Instant.now().getEpochSecond();
        log.info("Waiting for situation creation...");

        Map<String, String> healthScore = businessHealthList.get(0);
        if(incidentValidation){
            log.info("Get current open Incident count");
            JSONObject incident_Resp = hm_events.runMSearchQueryGetSituation(healthScore.get("key"),jwtToken,incidentjsonfile,2);
            currentincidentcount = incident_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        }

        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }
        long eepochTime = Instant.now().getEpochSecond();
        if ((eepochTime - sepochTime) < 60){
            commonWait.waitForSometime(15000);
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        for(String service_name: bussness_service) {
            Map<String, String> bshealthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service <" + service_name + "> Health Score " + bshealthScore.get("health"));
            if (Integer.parseInt(bshealthScore.get("health")) > 90) {
                softAssert.fail("Health Score not correct for business service <" + service_name + "> Health Score " + bshealthScore.get("health"));
            }

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Validate health Time Line for each Business Service");
            Object healthline = aiopsApiCore.aiopsGetHealthTimeline(bshealthScore.get("key"), jwtToken);
            if (healthline instanceof String) {
                softAssert.fail("Unable to get Health time Line");
            } else {
                JSONObject timeline = (JSONObject) healthline;
                int size = timeline.getJSONArray("timelines").getJSONObject(0).getJSONArray("values").length();
                int healthtimelinescore = timeline.getJSONArray("timelines").getJSONObject(0).getJSONArray("values").optJSONArray(size - 1).optInt(1);
                if (healthtimelinescore != Integer.parseInt(bshealthScore.get("health"))) {
                    softAssert.fail("Health Time line score " + healthtimelinescore + " Service health score " + bshealthScore.get("health"));
                }
            }
        }

        log.info("validate Node impacted");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        Map<String, String> node_detail = aiopsApiCore.aiopsGetCITopology(healthScore.get("key"), jwtToken);
        int mimcicount = 0;
        for(String nodename : ci_list){
            if (!node_detail.containsKey(nodename)) {
                log.info("Node not present in the ci topology " + nodename);
                continue;
            }
            String Node_id = node_detail.get(nodename);
            int node_health_score = aiopsApiCore.aiopsgetNodeHealthScore(Node_id,jwtToken);
            if ((node_health_score == 100) ||  (node_health_score == -1)){
                log.error("Node " + nodename + " healthscore not proper " + node_health_score);
            }else{
                mimcicount += 1;
            }
        }
        if (mimcicount <= expectedMinimumCICount) {
            softAssert.fail("less than " + expectedMinimumCICount + " nodes are impacted");
            log.error("less than " + expectedMinimumCICount + " nodes are impacted");
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("validate Incident creation on service level");
        if (iapsrnable && incidentValidation)
        {
            JSONObject incident_Resp = hm_events.runMSearchQueryGetSituation(healthScore.get("key"),jwtToken,incidentjsonfile,2);
            int totalrecord = incident_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
            if (totalrecord == currentincidentcount){
                softAssert.fail("Incident does not got created at service level " + totalrecord + " currentincidentcount " + currentincidentcount);
            }
        }

        log.info("Close all open event on Nagios class");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        for(String service_name: bussness_service){
            CloseAllEventOnService(service_name);
        }
        boolean healthScoreExpected = aiopsApiCore.waitForHealthScoreExpectedValue(91, bussness_service[0], jwtToken, 2);
        Map<String, String> bsHealthScore = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
        if (!healthScoreExpected){
            softAssert.fail("Health Score not correct for business service " + bussness_service[0] + " expected 91 actual Health Score " + bsHealthScore.get("health"));
        }
        
        for(String service_name: bussness_service){
            bsHealthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service " + service_name + " Health Score " + bsHealthScore.get("health"));
            if (Integer.parseInt(bsHealthScore.get("health")) < 91){
                softAssert.fail("Health Score not correct for business service " + service_name + " expected 100 actual Health Score " + bsHealthScore.get("health"));
            }
        }

        log.info("Wait for Node health to reset to 100");
        outerLoop: // Label for the outer loop
        for (int i = 0;i<10;i += 1){
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            for(String nodename : ci_list){
                if (!node_detail.containsKey(nodename)) {
                    continue;
                }
                String Node_id = node_detail.get(nodename);
                int node_health_score = aiopsApiCore.aiopsgetNodeHealthScore(Node_id,jwtToken);
                if (node_health_score != 100){
                    commonWait.waitForSometime(15000);
                }
                else{
                    log.info("one of ci is 100");
                    break outerLoop;
                }
            }
        }
       
        return true;

    }

    public boolean updateSituationConfiguration(String jsonfile, String serviceKey, String jwtToken) throws IOException, InterruptedException {
        String jsonContent = aiopsApiCore.readJSon(jsonfile);
        String updateconfigfilecontent = jsonContent.replace("<SERVICE_KEY>", serviceKey);
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(updateconfigfilecontent)
                .post(updateServiceEndpoint);

        if (resp.getStatusCode() > 299) {
            log.error("Update situation configuration failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    public boolean updateMultiServiceSituationConfiguration(boolean mlflag, String situationId, String jwtToken) throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;

        JSONArray capabilitiesUpdated = new JSONArray();
            capabilitiesUpdated.put(new JSONObject().put("name", "multi-service-fingerprint").put("isEnabled", true));
            capabilitiesUpdated.put(new JSONObject().put("name", "ml-situation-across-service-boundary").put("isEnabled", mlflag));

            JSONObject putPayload = new JSONObject();
            putPayload.put("name", "ml_situation_config");
            putPayload.put("description", "ml_situation_config_description");
            putPayload.put("enable", true);
            JSONObject cfg = new JSONObject();
            cfg.put("correlation_window", 5);
            cfg.put("merge_window", 5);
            cfg.put("fingerprint_idle_interval_for_expiry", 14400);
            cfg.put("offline_fingerprint_interval", 60);
            cfg.put("show_policy_based_situations", true);
            cfg.put("enable_multiservice_situations", true);
            putPayload.put("configuration", cfg);
            putPayload.put("capabilities", capabilitiesUpdated);

            String putEndpoint = SituationConfigurationUpdateEndPoint + situationId;
            log.info("Updating capability via endpoint " + putEndpoint + " desired=" + mlflag);
            

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(putPayload.toString())
                .put(putEndpoint);
        if (resp.getStatusCode() > 299) {
            log.error("Update multi-service situation configuration failed: " + resp.getStatusCode());
            return false;
        }
        return true;
        
    }

    public boolean updateSituationConfigurationsituationseverity(String severityValue, String situationId) throws IOException, InterruptedException {
        String situationUrl = aiopssituationconfigUrl + situationId;
        String jsonContent = aiopsApiCore.readJSon("updatesituationconfigurationsituationseverity.json");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        jsonContent = jsonContent.replace("<Severity>", severityValue);
        RestAssured.baseURI = hmHostName;
        log.info("Update situation configuration severity payload: " + jsonContent);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .put(situationUrl);
        if (resp.getStatusCode() > 299) {
            log.error("Update situation configuration severity failed: " + resp.getStatusCode());
            return false;
        }
        log.info("Update situation configuration severity successful" + resp.getBody().asString());
        return true;
    }

    public JSONObject getSituationConfiguration(String situationId, int correlationWindow, int mergeWindow, int fingerprintIdleIntervalForExpiry, int offlineFingerprintInterval, String jwtToken) throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;
        String endPoint = SituationConfigurationUpdateEndPoint + situationId;
        JSONObject payload = new JSONObject();
        payload.put("name", "ml_situation_config");
        payload.put("description", "ml_situation_config_description");
        payload.put("enable", true);
        JSONObject cfg = new JSONObject();
        cfg.put("correlation_window", correlationWindow);
        cfg.put("merge_window", mergeWindow);
        cfg.put("fingerprint_idle_interval_for_expiry", fingerprintIdleIntervalForExpiry);
        cfg.put("offline_fingerprint_interval", offlineFingerprintInterval);
        cfg.put("show_policy_based_situations", true);
        cfg.put("enable_multiservice_situations", true);
        payload.put("configuration", cfg);
        payload.put("capabilities", new JSONArray());
        String jsonContent = payload.toString();

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .put(endPoint);
        if (resp.getStatusCode() > 299) {
            log.error("Get situation configuration failed: " + resp.getStatusCode());
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public String getSituationConfigurationId() throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body("{}")
                .post(SituationConfigurationEndPoint);
    
        if (resp.getStatusCode() > 299) {
            log.error("Get situation configuration id failed: " + resp.getStatusCode());
            return null;
        }
        JSONObject jsonObject = new JSONObject(resp.getBody().asString());
        if (jsonObject.getJSONArray("situations").length() == 0) {
            log.error("No situation configuration found");
            return null;
        }
        return jsonObject.getJSONArray("situations").getJSONObject(0).getString("id");
    }

    public void setExpectedMinimumCICount(int expectedMinimumCICount) {
        this.expectedMinimumCICount = expectedMinimumCICount;
    }

    public boolean validateParentLevelHealthStatusPropagationOverride(String sourceKey, String destinationKey, String childlist, String propagationStatus, String jwtToken) throws IOException, InterruptedException {
        String endPoint = "/smart-graph-api/api/v1.11/models/multi";
        RestAssured.baseURI = hmHostName;
        String jsonContent = aiopsApiCore.readJSon("parentlevelpropagation.json");
        jsonContent = jsonContent.replace("<SOURCE_KEY>", sourceKey);
        jsonContent = jsonContent.replace("<DESTINATION_KEY>", destinationKey);
        jsonContent = jsonContent.replace("<PROPOGATION_STATUS>", propagationStatus);
        jsonContent = jsonContent.replace("<CHILD_LIST>", childlist);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(endPoint);
        if (resp.getStatusCode() > 299) {
            log.error("Validate parent level health status propagation override failed: " + resp.getStatusCode());
            return false;
        }
        log.info("Validate parent level health status propagation override successful" + resp.getBody().asString());
        return true;
    }

    /**
     * Loads the service model for {@code businessServiceName}, finds the first {@code metric_group} /
     * {@code metrics_kpi} block, then:
     * <ul>
     *   <li>If a selector with {@code metric} {@code IdleTime} exists, removes it and PATCHes.</li>
     *   <li>Otherwise clones the {@code Utilization} selector (or the first {@code metric_entity_ade} selector),
     *       sets {@code metric} to {@code IdleTime}, appends it, and PATCHes.</li>
     * </ul>
     * PATCH: {@code POST} /smart-graph-api/api/v1.11/models/multi with body
     * {@code [{"method":"PATCH","body":{"key":"&lt;modelKey&gt;","metadata":{"_associatedElements":"&lt;json&gt;"}}}]}
     *
     * @param businessServiceName service display name (e.g. {@code ML-TSOM})
     * @return success, HTTP status ({@code -1} if not applicable), and whether IdleTime was {@link IdleTimeMetricChange#ADDED} or {@link IdleTimeMetricChange#REMOVED}
     */
    public IdleTimeAdjustResult adjustIdleTimeMetricOnServiceModel(String businessServiceName, String jwtToken) {
        RestAssured.baseURI = hmHostName;
        Map<String, String> health = aiopsApiCore.getHealthScore(businessServiceName, jwtToken);
        String modelKey = health.get("key");
        if (modelKey == null || modelKey.isEmpty()) {
            log.error("No model key for service: " + businessServiceName);
            return new IdleTimeAdjustResult(false, -1, IdleTimeMetricChange.NONE);
        }
        JSONObject getBody = new JSONObject();
        getBody.put("modelKey", modelKey);
        getBody.put("includeDownstream", true);
        getBody.put("includeUpstream", true);

        Response getResp = given()
                .header("Authorization", "Bearer " + jwtToken)
                .header("Content-Type", "application/json")
                .body(getBody.toString())
                .post(SERVICE_SAVE_MULTI);
        if (getResp.getStatusCode() > 299) {
            log.error("service/get failed: " + getResp.getStatusCode() + " " + getResp.getBody().asString());
            return new IdleTimeAdjustResult(false, getResp.getStatusCode(), IdleTimeMetricChange.NONE);
        }
        JSONObject servicePayload = new JSONObject(getResp.getBody().asString());
        if (!servicePayload.has("nodes") || servicePayload.getJSONArray("nodes").length() == 0) {
            log.error("service/get returned no nodes for " + businessServiceName);
            return new IdleTimeAdjustResult(false, getResp.getStatusCode(), IdleTimeMetricChange.NONE);
        }
        JSONObject node = servicePayload.getJSONArray("nodes").getJSONObject(0);
        if (!node.has("associatedElements")) {
            log.error("No associatedElements on service node");
            return new IdleTimeAdjustResult(false, getResp.getStatusCode(), IdleTimeMetricChange.NONE);
        }
        JSONArray associatedElements = new JSONArray(node.getJSONArray("associatedElements").toString());

        boolean modified = false;
        IdleTimeMetricChange stagedChange = IdleTimeMetricChange.NONE;
        outer:
        for (int i = 0; i < associatedElements.length(); i++) {
            JSONObject assoc = associatedElements.getJSONObject(i);
            if (!"metric_group".equals(assoc.optString("type"))) {
                continue;
            }
            JSONArray elements = assoc.optJSONArray("elements");
            if (elements == null) {
                continue;
            }
            for (int j = 0; j < elements.length(); j++) {
                JSONObject el = elements.getJSONObject(j);
                if (!"metrics_kpi".equals(el.optString("element_type"))) {
                    continue;
                }
                JSONArray selectors = el.optJSONArray("selectors");
                if (selectors == null || selectors.length() == 0) {
                    log.error("metrics_kpi has no selectors to adjust");
                    return new IdleTimeAdjustResult(false, getResp.getStatusCode(), IdleTimeMetricChange.NONE);
                }
                JSONArray newSelectors = new JSONArray();
                boolean hadIdleTime = false;
                for (int k = 0; k < selectors.length(); k++) {
                    JSONObject sel = selectors.getJSONObject(k);
                    JSONObject val = sel.optJSONObject("value");
                    String metric = val != null ? val.optString("metric") : "";
                    if ("IdleTime".equals(metric)) {
                        hadIdleTime = true;
                        continue;
                    }
                    newSelectors.put(sel);
                }
                if (hadIdleTime) {
                    el.put("selectors", newSelectors);
                    log.info("Removed IdleTime metric selector from metric_group; remaining selectors: " + newSelectors.length());
                    modified = true;
                    stagedChange = IdleTimeMetricChange.REMOVED;
                } else {
                    JSONObject template = null;
                    for (int k = 0; k < selectors.length(); k++) {
                        JSONObject sel = selectors.getJSONObject(k);
                        JSONObject val = sel.optJSONObject("value");
                        if (val != null && "Utilization".equals(val.optString("metric"))) {
                            template = new JSONObject(sel.toString());
                            break;
                        }
                    }
                    if (template == null) {
                        template = new JSONObject(selectors.getJSONObject(0).toString());
                    }
                    template.getJSONObject("value").put("metric", "IdleTime");
                    selectors.put(template);
                    log.info("Added IdleTime metric selector (cloned from Utilization or first selector)");
                    modified = true;
                    stagedChange = IdleTimeMetricChange.ADDED;
                }
                break outer;
            }
        }
        if (!modified) {
            log.error("No metric_group/metrics_kpi block found to adjust for " + businessServiceName);
            return new IdleTimeAdjustResult(false, getResp.getStatusCode(), IdleTimeMetricChange.NONE);
        }

        JSONArray patchOps = new JSONArray();
        JSONObject op = new JSONObject();
        op.put("method", "PATCH");
        JSONObject inner = new JSONObject();
        inner.put("key", modelKey);
        JSONObject metadata = new JSONObject();
        metadata.put("_associatedElements", associatedElements.toString());
        inner.put("metadata", metadata);
        op.put("body", inner);
        patchOps.put(op);

        Response patchResp = given()
                .header("Authorization", "Bearer " + jwtToken)
                .header("Content-Type", "application/json")
                .body(patchOps.toString())
                .post(SERVICE_SAVE_MULTI);
        log.info("models/multi PATCH response: " + patchResp.getStatusCode() + " " + patchResp.getBody().asString());
        int code = patchResp.getStatusCode();
        if (code != 200 && code != 207) {
            log.error("models/multi PATCH failed: " + code + " (metric change: " + stagedChange + ")");
            return new IdleTimeAdjustResult(false, code, stagedChange);
        }
        return new IdleTimeAdjustResult(true, code, stagedChange);
    }


    public boolean updateSituationConfigurationKind(String jsonfile, String situationId, String status, String infrastructureNode, String networkNode, String applicationNode) throws IOException, InterruptedException {
        String situationUrl = aiopssituationconfigUrl + situationId;
        String jsonContent = aiopsApiCore.readJSon(jsonfile);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        jsonContent = jsonContent.replace("<STATUS>", status);
        JSONObject jsonObject = new JSONObject(jsonContent);
        if(infrastructureNode != null && !infrastructureNode.isEmpty()){
            jsonObject.getJSONObject("configuration").put("infrastructure_custom_nodes", infrastructureNode);
        }else{
            jsonObject.getJSONObject("configuration").remove("infrastructure_custom_nodes");
        }
        if(networkNode != null && !networkNode.isEmpty()){
            jsonObject.getJSONObject("configuration").put("network_custom_nodes", networkNode);
        }else{
            jsonObject.getJSONObject("configuration").remove("network_custom_nodes");
        }
        if(applicationNode != null && !applicationNode.isEmpty()){
            jsonObject.getJSONObject("configuration").put("application_custom_nodes", applicationNode);
        }else{
            jsonObject.getJSONObject("configuration").remove("application_custom_nodes");
        }
        jsonContent = jsonObject.toString();
        RestAssured.baseURI = hmHostName;
        log.info("Update situation configuration kind payload: " + jsonContent);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+jwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .put(situationUrl);
        if (resp.getStatusCode() > 299) {
            log.error("Update situation configuration severity failed: " + resp.getStatusCode());
            return false;
        }
        log.info("Update situation configuration severity successful" + resp.getBody().asString());
        return true;
    }

}