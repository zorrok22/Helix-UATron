package ITOM.Aiops;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.ReadPropertiesFile;
import util.SolqaDetails;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

/**
 * Java translation for the impact-balance-profile Robot test case.
 */
public class AiopsImpactBalanceProfileTest {

    private static final SolqaLogger log = new SolqaLogger(AiopsImpactBalanceProfileTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String BASE_URL = config.get("ADE_PLATFORM_URL");
    private static final String EVENT_CLASS = "NAGIOS_EV1";
    private static final String DEFAULT_CLUSTER_NAME = "Aus_Rancher_PSR_VSAN_Cluster_01";
    private static final String BALANCE_SERVICE_PREFIX = "ML-BALANCE-CLUST-H";
    private static final String BLUEPRINT_NAME = "Default Blueprint for VCenter Cluster Name";
    private static final String LOCATION = "Pune";
    private static final String eventSeverity = "CRITICAL";
    private static final Path BLUEPRINT_PAYLOAD_PATH = Paths.get(System.getProperty("user.dir"),
            "resources", "ITOM", "AIOPS", "vcenterClusterBlueprintImport.json");

    private final Aiops aiopsApi = new Aiops();
    private final HMEvents hmEvents = new HMEvents();
    private final CommonWaits commonWaits = new CommonWaits();
    private SoftAssert softAssert = null; // Initialize softAssert to null
    CommonMethods commonmethod = new CommonMethods();

    

    @Test(description = "[NextGen] Validate health score of service with balancing profile for K8s/VMware", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12456", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12456_validate_health_score_of_service_with_balancing_profile_for_K8s_VMware(ITestContext context) throws Exception {
        context.setAttribute("ServiceLevel_Product", Product.Service_Monitoring);
        log.info("Starting impact balance profile scenario");
        executeImpactBalanceProfileScenario();
    }

    private void executeImpactBalanceProfileScenario() throws Exception {
        this.softAssert = new SoftAssert();
        String jwtToken = commonmethod.refreshJwtToken(null);
        log.info("JWT token refreshed for scenario");

        hmEvents.closeAllEventByClass(EVENT_CLASS, jwtToken);
        //aiopsApi.deleteServiceModel(BALANCE_SERVICE_PREFIX, jwtToken);
        log.info("Closed all events for class " + EVENT_CLASS);

        log.info("Wait for cleanup before rebuilding service model");
        commonWaits.waitForSometime(12000);


        JSONObject serexit = aiopsApi.searchAndGetServiceDetails(BALANCE_SERVICE_PREFIX + "-0", jwtToken);
        log.info(String.valueOf(serexit));
        if (serexit.toString().length() < 5){
            BlueprintInfo blueprintInfo = ensureVcenterBlueprintReady(jwtToken);
            ClusterInfo clusterInfo = fetchClusterInfo(DEFAULT_CLUSTER_NAME, jwtToken);
            createClusterModel(clusterInfo, blueprintInfo, jwtToken);
            log.info("Created cluster model for " + DEFAULT_CLUSTER_NAME);
        }
        String parentName = BALANCE_SERVICE_PREFIX + "-0";
        String childName = BALANCE_SERVICE_PREFIX + "-1";
        ClusterServiceModel serviceModel = new ClusterServiceModel(parentName, childName);
        Map<String, String> serviceKeys = resolveServiceKeys(serviceModel.serviceNames(), jwtToken);
        for (String key : serviceKeys.values()) {
            aiopsApi.resetServiceHealthScore(key, jwtToken);
            log.info("Reset health score for service key: " + key);
        }

        commonWaits.waitForSometime(60000);
        log.info("Waited for health score reset to take effect");

        String childServiceName = serviceModel.childServiceName();
        List<String> hostNodes = fetchHostNodes(serviceKeys.get(childServiceName), jwtToken);
        Assert.assertFalse(hostNodes.isEmpty(), "No host nodes discovered for cluster service");
        log.info("Fetched host nodes for child service: " + hostNodes);

        sendEventsToHosts(hostNodes, jwtToken);
        log.info("Sent events to host nodes");

        log.info("Wait for health computation after sending events");
        commonWaits.waitForSometime(30000);

        validateParentHealth(serviceModel.parentServiceName(), jwtToken);
        log.info("Validated parent health for " + serviceModel.parentServiceName());
        validateChildHealth(childServiceName, hostNodes.size(), jwtToken);
        log.info("Validated child health for " + childServiceName);

        hmEvents.closeAllEventByClass(EVENT_CLASS, jwtToken);
        log.info("Wait for service recovery after closing events");
        commonWaits.waitForSometime(30000);

        for (String serviceName : serviceModel.serviceNames()) {
            Map<String, String> health = aiopsApi.getHealthScore(serviceName, jwtToken);
            softAssert.assertEquals(health.getOrDefault("health", "0"), "100",
                    "Service health did not restore to 100 for " + serviceName);
            log.info("Validated restored health=100 for " + serviceName);
        }

        softAssert.assertAll();
        log.info("Soft assertions completed");
    }

    private BlueprintInfo ensureVcenterBlueprintReady(String jwtToken) throws IOException {
        Optional<BlueprintInfo> blueprintOpt = fetchBlueprint();
        if (blueprintOpt.isEmpty()) {
            log.info("Blueprint not found. Importing Default Blueprint for VCenter Cluster Name");
            importVcenterBlueprint(jwtToken);
            commonWaits.waitForSometime(5000);
            blueprintOpt = fetchBlueprint();
        }
        if (blueprintOpt.isEmpty()) {
            throw new IllegalStateException("Unable to import VCenter Cluster blueprint");
        }
        BlueprintInfo info = blueprintOpt.get();
        if (!"ENABLED".equalsIgnoreCase(info.state())) {
            log.info("Blueprint found in state " + info.state() + ". Enabling it.");
            setBlueprintState(info, "ENABLED", jwtToken);
            commonWaits.waitForSometime(2000);
            info = fetchBlueprint().orElseThrow(() -> new IllegalStateException("Blueprint missing after enable call"));
            log.info("Blueprint enabled and re-fetched");
        }
        return info;
    }

    private Optional<BlueprintInfo> fetchBlueprint() {
        RestAssured.baseURI = BASE_URL;
        JSONObject payload = new JSONObject();
        payload.put("outputAttributes", new JSONArray(Arrays.asList("key", "name", "namespace", "description",
                "defaultBpId", "read_only", "state", "instances_count")));
        payload.put("limit", 10);
        payload.put("filters", new JSONArray().put(new JSONObject()
                .put("attribute", "name")
                .put("value", BLUEPRINT_NAME)));
        payload.put("multiSortMeta", new JSONArray().put(new JSONObject().put("attribute", "name").put("order", 1)));
        payload.put("globalStats", true);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + commonmethod.refreshJwtToken(null))
                .contentType(ContentType.JSON)
                .body(payload.toString())
                .post("/service-management/api/v1.0/blueprint/list");
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to fetch blueprint list");
        log.info("Fetched blueprint list successfully");

        JSONObject body = new JSONObject(response.getBody().asString());
        int count = body.optInt("count", 0);
        if (count == 0) {
            return Optional.empty();
        }
        JSONArray blueprints = body.getJSONArray("blueprints");
        JSONObject blueprint = blueprints.getJSONObject(0);
        JSONObject blueprintId = blueprint.getJSONObject("blueprintId");
        log.info("Found blueprint id=" + blueprintId.getString("id") + " version=" + blueprintId.getString("version"));
        return Optional.of(new BlueprintInfo(
                blueprintId.getString("id"),
                blueprintId.getString("version"),
                blueprint.optString("state", "DISABLED")));
    }

    private void setBlueprintState(BlueprintInfo info, String state, String jwtToken) {
        RestAssured.baseURI = BASE_URL;
        JSONObject body = new JSONObject();
        body.put("id", info.id());
        body.put("version", info.version());
        body.put("state", state);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(body.toString())
                .post("/service-management/api/v1.0/blueprint/state");
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to update blueprint state");
        log.info("Updated blueprint state to " + state + " for id " + info.id());
    }

    private void importVcenterBlueprint(String jwtToken) throws IOException {
        RestAssured.baseURI = BASE_URL;
        String payload = Files.readString(BLUEPRINT_PAYLOAD_PATH);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/smart-graph-api/api/v1.11/models/multi");
        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 207,
                "Blueprint import failed: " + response.getBody().asString());
        log.info("Imported VCenter blueprint payload");
    }

    private ClusterInfo fetchClusterInfo(String clusterName, String jwtToken) {
        RestAssured.baseURI = BASE_URL;
        JSONObject request = new JSONObject();
        request.put("query", "SEARCH Cluster WHERE name HAS SUBSTRING '" + clusterName +
                "' ORDER BY name SHOW #id as 'id', name, #kind, type");
        request.put("limit", 5);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(request.toString())
                .post("/service-management/api/v1.0/topology/search");
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to fetch cluster topology");
        log.info("Fetched cluster topology for " + clusterName);

        JSONArray rootArray = new JSONArray(response.getBody().asString());
        if (rootArray.length() == 0) {
            throw new IllegalStateException("No cluster topology data returned");
        }
        JSONArray results = rootArray.getJSONObject(0).optJSONArray("results");
        if (results == null || results.length() == 0) {
            throw new IllegalStateException("Cluster not found in topology results");
        }

        JSONArray row = results.getJSONArray(0);
        String clusterId = Objects.toString(row.opt(0), null);
        String clusterDisplay = Objects.toString(row.opt(1), clusterName);
        Assert.assertNotNull(clusterId, "Cluster id not found in topology results");
        log.info("Cluster resolved id=" + clusterId + " display=" + clusterDisplay);
        return new ClusterInfo(clusterId, clusterDisplay);
    }

    private ClusterServiceModel createClusterModel(ClusterInfo clusterInfo,
                                                  BlueprintInfo blueprintInfo,
                                                  String jwtToken) {
        RestAssured.baseURI = BASE_URL;
        String parentName = BALANCE_SERVICE_PREFIX + "-0";
        String childName = BALANCE_SERVICE_PREFIX + "-1";

        JSONObject payload = buildClusterModelPayload(clusterInfo, blueprintInfo, parentName, childName);
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(payload.toString())
                .post("/service-management/api/v1.0/service/save");
        Assert.assertEquals(response.getStatusCode(), 200,
                "Failed to save cluster model: " + response.getBody().asString());
        log.info("Saved cluster model for parent=" + parentName + ", child=" + childName);

        return new ClusterServiceModel(parentName, childName);
    }

    private JSONObject buildClusterModelPayload(ClusterInfo clusterInfo,
                                                BlueprintInfo blueprintInfo,
                                                String parentName,
                                                String childName) {
        JSONObject parentNode = new JSONObject();
        parentNode.put("compatibleOnly", "false");
        parentNode.put("id", "$$$$0");
        parentNode.put("name", parentName);
        parentNode.put("criticality", 0);
        parentNode.put("labels", new JSONObject());
        parentNode.put("blueprintConfigurations", new JSONArray());
        parentNode.put("securityGroupConfigurations", new JSONArray());
        parentNode.put("staticNodeIds", new JSONArray());
        parentNode.put("associatedElements", new JSONArray());
        parentNode.put("kind", "BusinessService");

        JSONObject childNode = new JSONObject();
        childNode.put("compatibleOnly", "false");
        childNode.put("id", "$$$$1");
        childNode.put("name", childName);
        childNode.put("criticality", 0);
        childNode.put("labels", new JSONObject());
        childNode.put("securityGroupConfigurations", new JSONArray());
        childNode.put("staticNodeIds", new JSONArray());
        childNode.put("kind", "BusinessService");

        JSONObject blueprintId = new JSONObject();
        blueprintId.put("id", blueprintInfo.id());
        blueprintId.put("version", blueprintInfo.version());

        JSONObject blueprintConfiguration = new JSONObject();
        blueprintConfiguration.put("blueprintId", blueprintId);
        JSONArray configurationItems = new JSONArray();
        configurationItems.put(new JSONObject()
                .put("values", new JSONArray().put(clusterInfo.name()))
                .put("regex", false)
                .put("dsmParameterName", "uiSelector0")
                .put("path", ""));
        blueprintConfiguration.put("configurationItems", configurationItems);
        childNode.put("blueprintConfigurations", new JSONArray().put(blueprintConfiguration));

        JSONArray associatedElements = new JSONArray();
        JSONObject balancingRules = new JSONObject();
        balancingRules.put("type", "balancing_rules");
        JSONArray elements = new JSONArray();
        elements.put(new JSONObject()
                .put("element_type", "balancing_rule_topological")
                .put("name", "cluster")
                .put("selectors", new JSONArray())
                .put("threshold", 80)
                .put("quantifier", "percentage")
                .put("startCiId", clusterInfo.id())
                .put("linkKind", "HostContainment")
                .put("resourceKind", "Host")
                .put("category", "Hosts & Host Containers"));
        balancingRules.put("elements", elements);
        associatedElements.put(balancingRules);

        JSONObject healthProcessing = new JSONObject();
        healthProcessing.put("type", "health_processing");
        JSONArray hpElements = new JSONArray();
        hpElements.put(new JSONObject()
                .put("element_type", "health_processing")
                .put("statusMapping", new JSONObject())
                .put("selectors", new JSONArray())
                .put("scoreMapping", new JSONObject())
                .put("propagation", "enabled"));
        healthProcessing.put("elements", hpElements);
        associatedElements.put(healthProcessing);
        childNode.put("associatedElements", associatedElements);

        JSONArray nodes = new JSONArray();
        nodes.put(parentNode);
        nodes.put(childNode);

        JSONArray links = new JSONArray();
        links.put(new JSONObject()
                .put("sourceId", "$$$$0")
                .put("targetId", "$$$$1"));

        JSONObject payload = new JSONObject();
        payload.put("nodes", nodes);
        payload.put("links", links);
        log.info("Built cluster model payload");
        return payload;
    }

    private Map<String, String> resolveServiceKeys(List<String> serviceNames, String jwtToken) {
        Map<String, String> resolved = new LinkedHashMap<>();
        for (String serviceName : serviceNames) {
            JSONObject services = aiopsApi.searchAndGetServiceDetails(serviceName, jwtToken);
            Iterator<String> iterator = services.keys();
            while (iterator.hasNext()) {
                String id = iterator.next();
                JSONObject service = services.getJSONObject(id);
                if (serviceName.equalsIgnoreCase(service.optString("name"))) {
                    resolved.put(serviceName, service.optString("key"));
                    break;
                }
            }
            if (!resolved.containsKey(serviceName)) {
                throw new IllegalStateException("Unable to resolve key for " + serviceName);
            }
        }
        return resolved;
    }

    private List<String> fetchHostNodes(String serviceKey, String jwtToken) {
        RestAssured.baseURI = BASE_URL;
        long endTime = hmEvents.getBhomServerTime(jwtToken);
        long startTime = endTime - Duration.ofMinutes(5).toMillis();
        log.info("Fetching host nodes for serviceKey=" + serviceKey + " between " + startTime + " and " + endTime);

        JSONObject body = new JSONObject();
        body.put("startTime", startTime);
        body.put("endTime", endTime);
        body.put("timeSlicesNumber", 1);
        body.put("serviceKey", serviceKey);
        body.put("includeMaxSeverity", true);
        body.put("linkModelNodes", false);
        body.put("includeDownstream", true);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(body.toString())
                .post("/service-management/api/v1.0/service/data/content");
        Assert.assertEquals(response.getStatusCode(), 200,
                "Failed to fetch service data content: " + response.getBody().asString());
        log.info("Fetched service data content for serviceKey=" + serviceKey);

        JSONObject nodes = new JSONObject(response.getBody().asString()).optJSONObject("nodes");
        if (nodes == null) {
            return Collections.emptyList();
        }
        List<String> hostNames = new ArrayList<>();
        Iterator<String> iterator = nodes.keys();
        while (iterator.hasNext()) {
            String nodeId = iterator.next();
            JSONObject node = nodes.getJSONObject(nodeId);
            if ("Host".equalsIgnoreCase(node.optString("kind"))) {
                String name = node.optString("name");
                if (name != null && !name.isBlank()) {
                    hostNames.add(name);
                }
            }
        }
        log.info("Resolved host nodes: " + hostNames);
        return hostNames;
    }

    private void sendEventsToHosts(List<String> hostNodes, String jwtToken) {
        for (String host : hostNodes) {
            String message = "Impact balance profile critical event " + host + " "
                    + RandomStringUtils.insecure().nextNumeric(4);
            hmEvents.streamRestApiEvents(EVENT_CLASS, 1, jwtToken, host, message, eventSeverity, LOCATION, "");
            log.info("Sent event to host " + host + " message=" + message);
        }
    }

    private void validateParentHealth(String parentServiceName, String jwtToken) {
        Map<String, String> health = aiopsApi.getHealthScore(parentServiceName, jwtToken);
        int totalEvents = Integer.parseInt(health.getOrDefault("totalEventCount", "0"));
        int impactingSubs = Integer.parseInt(health.getOrDefault("impactingSubService", "0"));
        int score = Integer.parseInt(health.getOrDefault("health", "100"));

        softAssert.assertEquals(totalEvents, 0,
                "Parent service should not accumulate direct events");
        softAssert.assertEquals(impactingSubs, 1,
                "Parent service should report single impacted sub-service");
        softAssert.assertTrue(score <= 90,
                "Parent service health is expected to degrade");
        log.info("Parent health validated: totalEvents=" + totalEvents + ", impactingSubs=" + impactingSubs + ", score=" + score);
    }

    private void validateChildHealth(String childServiceName, int expectedEvents, String jwtToken) {
        Map<String, String> health = aiopsApi.getHealthScore(childServiceName, jwtToken);
        int totalEvents = Integer.parseInt(health.getOrDefault("totalEventCount", "0"));
        int score = Integer.parseInt(health.getOrDefault("health", "100"));

        softAssert.assertTrue(totalEvents >= expectedEvents,
                "Child service total events mismatch. Expected at least " + expectedEvents + " but found " + totalEvents);
        softAssert.assertTrue(score <= 90,
                "Child service health is expected to degrade");
        log.info("Child health validated: totalEvents=" + totalEvents + ", score=" + score);
    }

    private static class BlueprintInfo {
        private final String id;
        private final String version;
        private final String state;

        private BlueprintInfo(String id, String version, String state) {
            this.id = id;
            this.version = version;
            this.state = state;
        }

        String id() {
            return id;
        }

        String version() {
            return version;
        }

        String state() {
            return state;
        }
    }

    private static class ClusterInfo {
        private final String id;
        private final String name;

        private ClusterInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        String id() {
            return id;
        }

        String name() {
            return name;
        }
    }

    private static class ClusterServiceModel {
        private final String parentServiceName;
        private final String childServiceName;

        private ClusterServiceModel(String parentServiceName, String childServiceName) {
            this.parentServiceName = parentServiceName;
            this.childServiceName = childServiceName;
        }

        String parentServiceName() {
            return parentServiceName;
        }

        String childServiceName() {
            return childServiceName;
        }

        List<String> serviceNames() {
            return Arrays.asList(parentServiceName, childServiceName);
        }
    }
}

