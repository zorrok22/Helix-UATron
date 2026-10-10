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
import org.testng.annotations.BeforeMethod;
import util.CommonMethods;
import java.util.*;
import java.util.stream.Collectors;
import util.ExcelReaderForMLSituation;
import static io.restassured.RestAssured.given;
import util.CommonWaits;

/** *********************************************************************
 Purpose:		This class contains common functions for AiopsPrediction using API's
 @author			
  ********************************************************************** */

public class AiopsPrediction {
    private static final SolqaLogger log = new SolqaLogger(AiopsPrediction.class.getName());
    
  
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    CommonWaits commonWait = new CommonWaits();
    CommonMethods commonmethod = new CommonMethods();
    private String jwtToken = null;
    List<String> HostsList = new ArrayList<>();
    List<String> NodeIDList = new ArrayList<>();
    private List<String[]> SupportedEntityTypesPolicyMapping = deriveSupportedEntityTypes(loadPatrolPolicies());
    private List<String[]> patrolPolicies = loadPatrolPolicies();
    private static ExcelReaderForMLSituation excelReaderForML = new ExcelReaderForMLSituation();

    public String getAddmId(String ciname) {
      JSONObject query = new JSONObject();
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
      query.put("query", "SEARCH Host WHERE name HAS SUBSTRING '"+ciname+"' ORDER BY name SHOW #id as 'id', name, #kind, type");
      query.put("limit", 0);
      JSONArray results = aiopsApiCore.getDiscoveryNodeId(String.valueOf(query), jwtToken);

      int count = results.getJSONObject(0).getInt("count");
      if(count > 0) {
          return results.getJSONObject(0).getJSONArray("results").getJSONArray(0).getString(0);
      }
      return null;

  }

  public boolean fetchLinuxHosts() {
      // Equivalent to Robot get_linux_device (simplified)
      jwtToken = commonmethod.refreshJwtToken(jwtToken);
      JSONObject query = new JSONObject()
              .put("query", "search MonitoredObject where entity_type='NUK_CPU' show *")
              .put("limit", 50);
      JSONArray results = aiopsApiCore.getDiscoveryNodeId(String.valueOf(query), jwtToken);
      if(results == null){
          log.info("Unable to fetch Linux Machine List");
          return false;
      }
      int count = results.getJSONObject(0).getInt("count");


      for (int i = 0; i < count; i++) {
          JSONArray row = results.getJSONObject(0).getJSONArray("results").getJSONArray(i);
          if (row.length() > 3) {
              String hostname = row.getString(3);
              HostsList.add(hostname);
          }
      }
      return true;
  }


  public String prepareAssociatedElements(int maxlimit) {

      String associateString = "{\\\"selector_type\\\":\\\"metric_entity_ade\\\",\\\"value\\\":{\\\"entityId\\\":\\\"<EntityId>\\\",\\\"entityTypeId\\\":\\\"<Entity Type>\\\",\\\"node_kind\\\":\\\"Host\\\",\\\"source\\\":\\\"\\\",\\\"metric\\\":\\\"<Metric>\\\"}}";
      List<String> nodeIds = new ArrayList<>();
      if (SupportedEntityTypesPolicyMapping == null || SupportedEntityTypesPolicyMapping.isEmpty()) {
          Assert.fail("SupportedEntityTypesPolicyMapping is empty; cannot build associated elements");
      }
      int i = 0;
      boolean breakLoop = false;
      for(String addmId:NodeIDList){
          for (String[] mapping : SupportedEntityTypesPolicyMapping) {
                if(i>= maxlimit){
                  breakLoop = true;
                  break;
                }
              String entityType = mapping[0];
              String metricId = mapping[1];
              jwtToken = commonmethod.refreshJwtToken(jwtToken);
              String entityId = aiopsApiCore.getEntityTypeId(addmId, entityType, jwtToken);
              if (entityId != null) {
                  String associatedString = associateString.replace("<EntityId>", entityId)
                          .replace("<Entity Type>", entityType)
                          .replace("<Metric>", metricId);
                  nodeIds.add(associatedString);
                  i++;
              }
          }
          if(breakLoop){
            break;
          }
      }
      String nodeIdsString = String.join(", ", nodeIds);
      return nodeIdsString;
  }



  public boolean fetchWindowsHosts() {
      // Equivalent to Robot get_linux_device (simplified)
      jwtToken = commonmethod.refreshJwtToken(jwtToken);
      JSONObject query = new JSONObject()
              .put("query", "search MonitoredObject where entity_type='NT_CPU' show *")
              .put("limit", 50);

      JSONArray results = aiopsApiCore.getDiscoveryNodeId(String.valueOf(query), jwtToken);
      if(results == null){
          log.info("Unable to fetch Widows Machine List");
          return false;
      }
      int count = results.getJSONObject(0).getInt("count");


      for (int i = 0; i < count; i++) {
          JSONArray row = results.getJSONObject(0).getJSONArray("results").getJSONArray(i);
          if (row.length() > 3) {
              String hostname = row.getString(3);
              HostsList.add(hostname);
          }
      }
      return true;
  }



  public boolean waitForPrediction(String serviceName, int maxAttempts, int sleepSeconds) throws InterruptedException {
      // Poll prediction-api-service for the service name
      //generally prediction event take 20 min to generate
      for (int i = 0; i < maxAttempts; i++) {
          jwtToken = commonmethod.refreshJwtToken(jwtToken);
          JSONObject json = aiopsApiCore.getPredictionBasedOnService(serviceName,jwtToken);
          if(json != null){
              int total = json.optInt("total_record_count", 0);
              if (total > 0) {
                  // verify service name presence
                  JSONArray records = json.optJSONArray("records");
                  if (records != null) {
                      for (int r = 0; r < records.length(); r++) {
                          JSONObject rec = records.getJSONObject(r);
                          String srv = rec.optString("service_name", "");
                          if (serviceName.equalsIgnoreCase(srv)) {
                              return true;
                          }
                      }
                  }
              }
          }
          commonWait.waitForSometime(sleepSeconds * 1000L);
      }
      return false;
  }

    private List<String[]> loadPatrolPolicies() {
      try {
          List<Map<String, Object>> rows = excelReaderForML.getTestDataFromExcel(1, "patrolPolicies");
          if (rows == null || rows.isEmpty()) {
              log.warn("patrolPolicies sheet empty or missing; using defaults");
              return null;
          }
          List<String[]> policies = new ArrayList<>();
          for (Map<String, Object> row : rows) {
              String[] entry = new String[]{
                      toStr(row.get("entityTypeId")),
                      toStr(row.get("metricId")),
                      toStr(row.get("label")),
                      toStr(row.get("systemType")),
                      toStr(row.get("unit")),
                      toStr(row.get("Kpi"))
              };
              policies.add(entry);
          }
          return policies;
      } catch (Exception e) {
          log.warn("Failed to load patrol policies from Excel; using defaults. Error: " + e.getMessage());
          return null;
      }
  }

    private List<String[]> deriveSupportedEntityTypes(List<String[]> policies) {
        if(policies == null || policies.isEmpty()) {
            return null;
        }
        Set<String> seen = new HashSet<>();
        List<String[]> mappings = new ArrayList<>();
        for (String[] p : policies) {
            if (p.length < 2) continue;
            String key = p[0] + "|" + p[1];
            if (seen.add(key)) {
                mappings.add(new String[]{p[0], p[1]});
            }
        }
        return mappings;
    }

    private String toStr(Object val) {
    return val == null ? "" : val.toString();
    }

    public List<String> getHostsList() {
    return HostsList;
    }

    public List<String[]> getPatrolPolicies() {
    return patrolPolicies;
    }

    public List<String> getNodeIDList() {
    return NodeIDList;
    }

    public void setNodeIDList(String nodeID) {
    NodeIDList.add(nodeID);
    }

    public String prepareAssociatedElements(int maxlimit, String metricName) {

        String associateString = "{\\\"selector_type\\\":\\\"metric_entity_ade\\\",\\\"value\\\":{\\\"entityId\\\":\\\"<EntityId>\\\",\\\"entityTypeId\\\":\\\"<Entity Type>\\\",\\\"node_kind\\\":\\\"Host\\\",\\\"source\\\":\\\"\\\",\\\"metric\\\":\\\"<Metric>\\\"}}";
        List<String> nodeIds = new ArrayList<>();
        if (SupportedEntityTypesPolicyMapping == null || SupportedEntityTypesPolicyMapping.isEmpty()) {
            Assert.fail("SupportedEntityTypesPolicyMapping is empty; cannot build associated elements");
        }
        int i = 0;
        boolean breakLoop = false;
        for(String addmId:NodeIDList){
            for (String[] mapping : SupportedEntityTypesPolicyMapping) {
                if(i> maxlimit){
                    breakLoop = true;
                    break;
                }
                String entityType = mapping[0];
                String metricId = mapping[1];
                if(!entityType.equals(metricName)) {
                    continue;
                }
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                String entityId = aiopsApiCore.getEntityTypeId(addmId, entityType, jwtToken);
                if (entityId != null) {
                    String associatedString = associateString.replace("<EntityId>", entityId)
                            .replace("<Entity Type>", entityType)
                            .replace("<Metric>", metricId);
                    nodeIds.add(associatedString);
                    i++;
                }
            }
            if(breakLoop){
                break;
            }
        }
        String nodeIdsString = String.join(", ", nodeIds);
        return nodeIdsString;
    }

    public boolean createService(String serviceName, String parentServiceName, String metricName ){
        log.info("Fetch Linux PA machine List");
        boolean linuxHosts = fetchLinuxHosts();
        Assert.assertTrue(linuxHosts, "Failed to fetch Linux hosts");
        log.info("Fetch Windows PA machine List");
        List<String> HostsList = getHostsList();
        List<String> NodeIDList = getNodeIDList();
        Assert.assertFalse(HostsList.isEmpty(), "No Linux or Windows hosts discovered to build service.");
        for(String ciname:HostsList) {
            String hostNoDomain = ciname.replaceAll("\\..*$", "");
            String addmId = getAddmId(hostNoDomain);
            if (addmId != null && !NodeIDList.contains(addmId)) {
                NodeIDList.add(addmId);
            }
        }
        // Create service model with the first discovered host
        String associateList = prepareAssociatedElements(8, metricName);
        associateList = associateList.replace("}}", ",\\\"serviceUnavailability\\\":true}}");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        boolean shipService = true;
        if(parentServiceName == null) {
            shipService = false;
        }
        String serviceId = aiopsApiCore.createServiceWithStaticNodeusingMulti(serviceName, associateList, NodeIDList, shipService, parentServiceName, jwtToken);
        if(serviceId == null) {
            return false;
        }
        return true;
    }

    public boolean updateServiceLevelConfiguration(String serviceName, int minEventCount) {
        // 0 = Global Configuration, 1 = Local Configuration
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        String serviceKey = healthMap.get("key");
        JSONObject serviceLevelConfiguration = aiopsApiCore.getServiceModel(serviceKey, jwtToken);
        if(serviceLevelConfiguration == null) {
            Assert.fail("Failed to get service level configuration");
        }
        JSONArray nodes = serviceLevelConfiguration.getJSONArray("nodes");
        JSONObject node = null;
        for(int i = 0; i < nodes.length(); i++) {
            JSONObject currentNode = nodes.getJSONObject(i);
            if(currentNode.getString("name").equals(serviceName)) {
                node = currentNode;
                break;
            }
        }

        if(node == null) {
            Assert.fail("Failed to get service level configuration");
        }

        JSONArray associatedElements = node.getJSONArray("associatedElements");
        boolean healthProcessEntry = false;
        for(int i = 0; i < associatedElements.length(); i++) {
            JSONObject associatedElement = associatedElements.getJSONObject(i);
            String type = associatedElement.getString("type");
            if(type.equals("health_processing")) {
                healthProcessEntry = true;
                int defaultminEventCount = associatedElements.getJSONObject(i).optInt("minEventCount", 0);
                if(minEventCount == 0) {
                    if(defaultminEventCount == 0){
                        break;
                    }
                    associatedElements.getJSONObject(i).remove("minEventCount");
                }else{
                    associatedElements.getJSONObject(i).put("minEventCount", minEventCount);
                }
            }
        }
        if(!healthProcessEntry) {
            JSONObject healthProcessing = new JSONObject();
            healthProcessing.put("type", "health_processing");
            JSONArray elements = new JSONArray();
            JSONObject element = new JSONObject();
            element.put("element_type", "health_processing");
            element.put("statusMapping", new JSONObject());
            element.put("selectors", new JSONArray());
            element.put("scoreMapping", new JSONObject());
            element.put("propagation", "enabled");
            if(minEventCount > 0) {
                element.put("minEventCount", minEventCount);
            }
            element.put("globalConfigurationBalancingRules", "enabled");
            elements.put(element);
            healthProcessing.put("elements", elements);
            associatedElements.put(healthProcessing);
            log.info(associatedElements.toString());
        }

        String associateElements = associatedElements.toString();
        JSONArray mainupdatebody = new JSONArray();
        JSONObject arrayBodyObj = new JSONObject();
        JSONObject updatebodyObj = new JSONObject();
        updatebodyObj.put("key", serviceKey);
        updatebodyObj.put("metadata", new JSONObject().put("_associatedElements", associateElements));
        arrayBodyObj.put("body", updatebodyObj);
        arrayBodyObj.put("method", "PATCH");
        mainupdatebody.put(arrayBodyObj);
        return aiopsApiCore.updateServiceHealthProfileConfiguration(mainupdatebody.toString(), jwtToken);

    }



}