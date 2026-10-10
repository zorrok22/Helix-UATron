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
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.ReadPropertiesFile;
import util.SolqaDetails;
import util.SolqaLogger;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

/**
 * Java translation for Service-level.robot covering the service model creation, event generation and health validation flow.
 */
public class AiopsServiceLevelTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsServiceLevelTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String SERVICE_PREFIX = "BUSS_CH_SERV";
    private static final String EVENT_CLASS = "NAGIOS_EV1";
    private static final int MIN_IMPACTING_SUBSERVICE_COUNT = 20;
    private static final int MULTI_LEVEL_CHILD_COUNT = 2;
    SoftAssert softAssert = null;
    private CommonWaits commonwait = new CommonWaits();
    private final Aiops aiopsApi = new Aiops();
    private final HMEvents hmEvents = new HMEvents();
    private final ServiceLevelClient serviceClient = new ServiceLevelClient(config);
    CommonMethods commonmethod = new CommonMethods();
    private AiopsSituation aiopsSituation = new AiopsSituation();


    @Test(description = "[SHIP] Validate the health score propagation on impact of child services", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12445", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12445_Validate_the_health_score_propagation_on_impact_child_services(ITestContext context) throws Exception {
        context.setAttribute("ServiceLevel_Product", Product.Service_Monitoring);
        softAssert = null;
        runHealthScorePropagationScenario(
                serviceClient::buildServiceGraph,
                ImpactingSubServiceAssertion.AT_LEAST,
                MIN_IMPACTING_SUBSERVICE_COUNT,
                "Impacted sub-service count should be at least " + MIN_IMPACTING_SUBSERVICE_COUNT);
    }

    @Test(description = "[NextGen] Validate the health score propagation for a multi-level hierarchy", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12457", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12457_Validate_the_health_score_propagation_for_a_multi_level_hierarchy(ITestContext context) throws Exception {
        context.setAttribute("ServiceLevel_Product", Product.Service_Monitoring);
        softAssert = null;
        runHealthScorePropagationScenario(
                (servicePrefix, jwtToken) -> {
                    ServiceGraph graph = serviceClient.buildMultiLevelServiceGraph(servicePrefix, jwtToken, MULTI_LEVEL_CHILD_COUNT);
                    Assert.assertTrue(graph.getServiceNames().size() >= MULTI_LEVEL_CHILD_COUNT + 1,
                            "Unable to build multi-level service graph with the required child count");
                    return graph;
                },
                ImpactingSubServiceAssertion.EXACT,
                MULTI_LEVEL_CHILD_COUNT,
                "Impacted sub-service count mismatch for multi-level hierarchy");
    }

    private void runHealthScorePropagationScenario(ServiceGraphBuilder graphBuilder,
                                                   ImpactingSubServiceAssertion assertionMode,
                                                   int expectedImpactingSubServices,
                                                   String assertionMessage) throws Exception {
        this.softAssert = new SoftAssert();
        SoftAssert assertions = this.softAssert;
        String jwtToken = commonmethod.refreshJwtToken(null);
        hmEvents.CloseAllOpenEvent(jwtToken);
        commonwait.waitForSometime(10000);
        hmEvents.closeAllEventByClass(EVENT_CLASS, jwtToken);
        serviceClient.resetAllServiceHealth(jwtToken, aiopsApi);
        aiopsApi.deleteServiceModel(SERVICE_PREFIX, jwtToken);
        log.info("sleep for 90 sec");
        commonwait.waitForSometime(90000);
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        ServiceGraph serviceGraph = graphBuilder.build(SERVICE_PREFIX, jwtToken);
        Assert.assertFalse(serviceGraph.getServiceNames().isEmpty(), "No eligible topology nodes available to build service graph");

        try {
            serviceClient.saveServiceGraph(serviceGraph, jwtToken);
            log.info("sleep for 150 sec");
            commonwait.waitForSometime(150000); // wait for 2.5 minutes for service model to be created
            String finalJwtToken = jwtToken;
            Map<String, ServiceInfo> serviceInfos = serviceClient.resolveServiceInfos(serviceGraph.getServiceNames(), () -> commonmethod.refreshJwtToken(finalJwtToken), aiopsApi);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            for (ServiceInfo info : serviceInfos.values()) {
                aiopsApi.resetServiceHealthScore(info.key(), jwtToken);
            }

            for (String serviceName : serviceGraph.getServiceNames()) {
                aiopsSituation.CloseAllEventOnService(serviceName);
                boolean healthOk = false;
                Exception lastException = null;
                for(int i = 0; i < 4; i++){
                    try {
                        jwtToken = commonmethod.refreshJwtToken(jwtToken);
                        Map<String, String> healthScore = aiopsApi.getHealthScore(serviceName, jwtToken);
                        String healthValue = String.valueOf(healthScore.get("health"));
                        if ("100".equals(healthValue)) {
                            healthOk = true;
                            break;
                        }
                    } catch (Exception e) {
                        lastException = e;
                    }
                    commonwait.waitForSometime(15000); // retry every 15s
                    
                }

                if (!healthOk) {
                    aiopsApi.deleteServiceModel(SERVICE_PREFIX, jwtToken);
                    if (lastException != null) {
                        Assert.fail("Health score check failed for " + serviceName + ": " + lastException.getMessage());
                    } else {
                        Assert.fail("Service health is not 100 for " + serviceName + " within 60 seconds.");
                    }
                }
            }

            for (String serviceName : serviceGraph.getServiceNames()) {
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                ServiceInfo info = serviceInfos.get(serviceName);
                List<String> nodeNames = serviceClient.fetchServiceNodeNames(info.key(), jwtToken);
                serviceClient.sendEventsToNodes(nodeNames, SERVICE_PREFIX, jwtToken, hmEvents);
                commonwait.waitForSometime(1000);
            }
            log.info("Wait for healthscore to get updated after sending event");
            log.info("sleep for 30 sec");
            commonwait.waitForSometime(45000);
            int impacted_cnt = 0;
            for (int index = 0; index < serviceGraph.getServiceNames().size(); index++) {
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                String serviceName = serviceGraph.getServiceNames().get(index);
                int[] health = serviceClient.getServiceHealth(serviceName, jwtToken, aiopsApi);
                log.info("Total Event count " + health[1]);
                if (health[1] != 0) {
                    impacted_cnt++;
                }
                assertions.assertNotEquals(health[0], 100, "Service health did not degrade for " + serviceName);
            }
            if(impacted_cnt < 20){
                assertions.fail("Impacted sub-service count is less than expected: " + impacted_cnt + " < " + 20);
            }
            Map<String, String> healthScore = aiopsApi.getHealthScore(SERVICE_PREFIX + "_0", jwtToken);
            log.info("Healthscore " + SERVICE_PREFIX + "_0 " + healthScore);
            int impactingSubServices = Integer.parseInt(healthScore.getOrDefault("impactingSubService", "0"));
            if (assertionMode == ImpactingSubServiceAssertion.AT_LEAST) {
                if(impactingSubServices < expectedImpactingSubServices){
                    log.info("Impacting sub-service count is less than expected: " + impactingSubServices + " < " + expectedImpactingSubServices);
                    assertions.fail("Impacting sub-service count is less than expected: " + impactingSubServices + " < " + expectedImpactingSubServices);
                }
            } else {
                if(impactingSubServices < expectedImpactingSubServices){
                    assertions.fail("Else Condition " + impactingSubServices + " < " + expectedImpactingSubServices + " " + assertionMessage);
                }
            }
            log.info("close all Event");
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            hmEvents.closeAllEventByClass(EVENT_CLASS, jwtToken);
            log.info("sleep for 60 sec");
            commonwait.waitForSometime(50000);
            hmEvents.CloseAllOpenEvent(jwtToken);
            commonwait.waitForSometime(10000);
            for (String serviceName : serviceGraph.getServiceNames()) {
                aiopsSituation.CloseAllEventOnService(serviceName);
            }
            commonwait.waitForSometime(10000);
            log.info("Validate service health score is restored");
            for (String serviceName : serviceGraph.getServiceNames()) {
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                int[] health = serviceClient.getServiceHealth(serviceName, jwtToken, aiopsApi);
                assertions.assertEquals(health[1], 0, "Residual events found for " + serviceName);
                if (health[0]<91) {
                    assertions.fail("Service health not restored for " + serviceName + " Healthscore " + health[0]);
                }
            }


            assertions.assertAll();
        } finally {
            String finalToken = commonmethod.refreshJwtToken(jwtToken);
            aiopsApi.deleteServiceModel(SERVICE_PREFIX, finalToken);
        }
    }

    private enum ImpactingSubServiceAssertion {
        AT_LEAST,
        EXACT;
       
    }

    @FunctionalInterface
    private interface ServiceGraphBuilder {
        ServiceGraph build(String servicePrefix, String jwtToken);
    }

    private static class ServiceLevelClient {

        private static final SolqaLogger clientLog = new SolqaLogger(ServiceLevelClient.class.getName());
        private static final String TOPOLOGY_SEARCH = "/service-management/api/v1.0/topology/search";
        private static final String SERVICE_SAVE = "/service-management/api/v1.0/service/save";
        private static final String SERVICE_DATA_CONTENT = "/service-management/api/v1.0/service/data/content";
        private final String baseUrl;

        ServiceLevelClient(Map<String, String> config) {
            this.baseUrl = config.get("ADE_PLATFORM_URL");
        }

        ServiceGraph buildServiceGraph(String servicePrefix, String jwtToken) {
            RestAssured.baseURI = baseUrl;
            JSONObject request = new JSONObject();
            request.put("query", "SEARCH Host where type ORDER BY name SHOW #id as 'id', name, #kind, type");
            request.put("limit", 50);

            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(ContentType.JSON)
                    .body(request.toString())
                    .post(TOPOLOGY_SEARCH);
            log.info("Topology search response: " + response.getBody().asString());
            if (response.getStatusCode() != 200) {
                Assert.assertEquals(response.getStatusCode(), 200, "Failed to fetch topology data: " + response.getBody().asString());
            }

            JSONArray rootArray = new JSONArray(response.getBody().asString());
            if (rootArray.length() == 0) {
                return new ServiceGraph(new JSONArray(), new JSONArray(), Collections.emptyList());
            }

            JSONArray results = rootArray.getJSONObject(0).optJSONArray("results");
            if (results == null) {
                return new ServiceGraph(new JSONArray(), new JSONArray(), Collections.emptyList());
            }

            JSONArray nodes = new JSONArray();
            JSONArray links = new JSONArray();
            List<String> serviceNames = new ArrayList<>();
            int index = 0;

            for (int i = 0; i < results.length(); i++) {
                Object rowObj = results.get(i);
                if (!(rowObj instanceof JSONArray)) {
                    continue;
                }
                JSONArray row = (JSONArray) rowObj;
                String hostId = Objects.toString(row.opt(0), null);
                String hostName = Objects.toString(row.opt(1), "");
                String type = Objects.toString(row.opt(3), "");
                if (hostId == null || "null".equalsIgnoreCase(type)) {
                    continue;
                }
                if (hostName.toLowerCase().contains("aut-clm") || !isValidHostName(hostName)) {
                    continue;
                }
                if (type.equalsIgnoreCase("Appliance")) {
                    continue;
                }
                String serviceName = servicePrefix + "_" + index;
                serviceNames.add(serviceName);
                String pseudoId = "$$$$" + index;
                JSONObject node = new JSONObject();
                node.put("compatibleOnly", "false");
                node.put("id", pseudoId);
                node.put("name", serviceName);
                node.put("criticality", 0);
                node.put("labels", new JSONObject());
                node.put("blueprintConfigurations", new JSONArray());
                node.put("securityGroupConfigurations", new JSONArray());
                node.put("staticNodeIds", new JSONArray().put(hostId));
                node.put("associatedElements", new JSONArray());
                node.put("kind", "BusinessService");
                nodes.put(node);

                if (index > 0) {
                    JSONObject link = new JSONObject();
                    link.put("sourceId", "$$$$" + (index - 1));
                    link.put("targetId", pseudoId);
                    links.put(link);
                }
                index++;
            }

            clientLog.info("Prepared service graph with " + serviceNames.size() + " nodes");
            return new ServiceGraph(nodes, links, serviceNames);
        }

        ServiceGraph buildMultiLevelServiceGraph(String servicePrefix, String jwtToken, int childrenPerParent) {
            RestAssured.baseURI = baseUrl;
            JSONObject request = new JSONObject();
            request.put("query", "SEARCH Host where type ORDER BY name SHOW #id as 'id', name, #kind, type");
            request.put("limit", 50);

            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(ContentType.JSON)
                    .body(request.toString())
                    .post(TOPOLOGY_SEARCH);
            
            log.info("Topology search response: " + response.getBody().asString());
            if (response.getStatusCode() != 200) {
                Assert.assertEquals(response.getStatusCode(), 200, "Failed to fetch topology data: " + response.getBody().asString());
            }

            

            JSONArray rootArray = new JSONArray(response.getBody().asString());
            if (rootArray.length() == 0) {
                return new ServiceGraph(new JSONArray(), new JSONArray(), Collections.emptyList());
            }

            JSONArray results = rootArray.getJSONObject(0).optJSONArray("results");
            if (results == null) {
                return new ServiceGraph(new JSONArray(), new JSONArray(), Collections.emptyList());
            }

            JSONArray nodes = new JSONArray();
            JSONArray links = new JSONArray();
            List<String> serviceNames = new ArrayList<>();
            List<String> pseudoIds = new ArrayList<>();
            int normalizedChildren = Math.max(childrenPerParent, 1);
            int index = 0;

            for (int i = 0; i < results.length(); i++) {
                Object rowObj = results.get(i);
                if (!(rowObj instanceof JSONArray)) {
                    continue;
                }
                JSONArray row = (JSONArray) rowObj;
                String hostId = Objects.toString(row.opt(0), null);
                String hostName = Objects.toString(row.opt(1), "");
                String type = Objects.toString(row.opt(3), "");
                if (hostId == null || "null".equalsIgnoreCase(type)) {
                    continue;
                }
                if (hostName.toLowerCase().contains("aut-clm") || !isValidHostName(hostName)) {
                    continue;
                }
                if (type.equalsIgnoreCase("Appliance")) {
                    continue;
                }
                String serviceName = servicePrefix + "_" + index;
                serviceNames.add(serviceName);
                String pseudoId = "$$$$" + index;
                pseudoIds.add(pseudoId);
                JSONObject node = new JSONObject();
                node.put("compatibleOnly", "false");
                node.put("id", pseudoId);
                node.put("name", serviceName);
                node.put("criticality", 0);
                node.put("labels", new JSONObject());
                node.put("blueprintConfigurations", new JSONArray());
                node.put("securityGroupConfigurations", new JSONArray());
                node.put("staticNodeIds", new JSONArray().put(hostId));
                node.put("associatedElements", new JSONArray());
                node.put("kind", "BusinessService");
                nodes.put(node);

                if (index > 0) {
                    int parentIndex = (index - 1) / normalizedChildren;
                    JSONObject link = new JSONObject();
                    link.put("sourceId", pseudoIds.get(Math.min(parentIndex, pseudoIds.size() - 1)));
                    link.put("targetId", pseudoId);
                    links.put(link);
                }
                index++;
            }

            clientLog.info("Prepared multi-level service graph with " + serviceNames.size() + " nodes and " + normalizedChildren + " children per parent");
            return new ServiceGraph(nodes, links, serviceNames);
        }

        void saveServiceGraph(ServiceGraph graph, String jwtToken) {
            RestAssured.baseURI = baseUrl;
            JSONObject payload = new JSONObject();
            payload.put("nodes", graph.getNodes());
            payload.put("links", graph.getLinks());

            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(ContentType.JSON)
                    .body(payload.toString())
                    .post(SERVICE_SAVE);
            Assert.assertEquals(response.getStatusCode(), 200, "Failed to save service graph: " + response.getBody().asString());
        }



        void resetAllServiceHealth(String jwtToken, Aiops aiopsApi) {
            JSONObject services = aiopsApi.searchAndGetServiceDetails("",jwtToken);;
            if (services == null) {
                return;
            }
            Iterator<String> iterator = services.keys();
            while (iterator.hasNext()) {
                String serviceId = iterator.next();
                JSONObject serviceObject = services.getJSONObject(serviceId);
                int health = serviceObject.optInt("health", 100);
                if (health == 100) {
                    continue;
                }
                String serviceKey = serviceObject.optString("key");
                aiopsApi.resetServiceHealthScore(serviceKey, jwtToken);
            }
        }

        List<String> fetchServiceNodeNames(String serviceKey, String jwtToken) {
            RestAssured.baseURI = baseUrl;
            long endTime = System.currentTimeMillis();
            long startTime = endTime - Duration.ofMinutes(5).toMillis();
            JSONObject body = new JSONObject();
            body.put("startTime", startTime);
            body.put("endTime", endTime);
            body.put("timeSlicesNumber", 1);
            body.put("serviceKey", serviceKey);
            body.put("includeMaxSeverity", true);

            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .post(SERVICE_DATA_CONTENT);
            if (response.getStatusCode() != 200) {
                clientLog.warn("Unable to fetch service data for key " + serviceKey + ": " + response.getBody().asString());
                return Collections.emptyList();
            }

            JSONObject nodes = new JSONObject(response.getBody().asString()).optJSONObject("nodes");
            if (nodes == null) {
                return Collections.emptyList();
            }
            List<String> nodeNames = new ArrayList<>();
            Iterator<String> iterator = nodes.keys();
            while (iterator.hasNext()) {
                String nodeId = iterator.next();
                nodeNames.add(nodes.getJSONObject(nodeId).optString("name"));
            }
            return nodeNames;
        }

        int sendEventsToNodes(List<String> nodeNames, String servicePrefix, String jwtToken, HMEvents hmEvents) {
            int totalSent = 0;
            for (String nodeName : nodeNames) {
                if (nodeName == null || nodeName.isBlank()) {
                    continue;
                }
                if (nodeName.startsWith(servicePrefix)) {
                    continue;
                }
                String trimmed = nodeName.trim();
                String majorMsg = "Service level major event " + trimmed + " " + RandomStringUtils.insecure().nextNumeric(4);
                hmEvents.streamRestApiEvents(EVENT_CLASS, 1, jwtToken, trimmed, majorMsg, "MAJOR", "Pune", "");
                String minorMsg = "Service level minor event " + trimmed + " " + RandomStringUtils.insecure().nextNumeric(4);
                hmEvents.streamRestApiEvents(EVENT_CLASS, 1, jwtToken, trimmed, minorMsg, "MINOR", "Pune", "");
                totalSent += 2;
            }
            return totalSent;
        }


        int[] getServiceHealth(String serviceName, String jwtToken, Aiops aiopsApi) {
            Map<String, String> healthScore = aiopsApi.getHealthScore(serviceName, jwtToken);
            int totalevent = Integer.parseInt(healthScore.getOrDefault("totalEventCount", "0"));
            int actualEvent = Integer.parseInt(healthScore.getOrDefault("actualEventCount", "0"));
            int eventCount = 0;
            if (totalevent > 0 || actualEvent > 0) {
                eventCount = Math.max(totalevent, actualEvent);
            }
            return new int[]{Integer.parseInt(healthScore.getOrDefault("health", "100")), eventCount};
        }

        Map<String, ServiceInfo> resolveServiceInfos(List<String> serviceNames,
                                                     Supplier<String> tokenSupplier,
                                                     Aiops aiopsApi) throws InterruptedException {
            Map<String, ServiceInfo> resolved = new LinkedHashMap<>();
            List<String> pending = new ArrayList<>(serviceNames);

            for (int attempt = 0; attempt < 20 && !pending.isEmpty(); attempt++) {
                String token = tokenSupplier.get();
                Iterator<String> iterator = pending.iterator();
                while (iterator.hasNext()) {
                    String serviceName = iterator.next();
                    ServiceInfo info = getServiceInfo(serviceName, token, aiopsApi);
                    if (info != null) {
                        resolved.put(serviceName, info);
                        iterator.remove();
                    }
                }
                if (!pending.isEmpty()) {
                    Thread.sleep(Duration.ofSeconds(15).toMillis());
                }
            }

            if (!pending.isEmpty()) {
                throw new IllegalStateException("Unable to resolve service identifiers for " + pending);
            }
            return resolved;
        }

        ServiceInfo getServiceInfo(String serviceName, String jwtToken, Aiops aiopsApi) {
            JSONObject services = aiopsApi.searchAndGetServiceDetails(serviceName, jwtToken);
            Iterator<String> iterator = services.keys();
            while (iterator.hasNext()) {
                String id = iterator.next();
                JSONObject service = services.getJSONObject(id);
                if (serviceName.equalsIgnoreCase(service.optString("name"))) {
                    return new ServiceInfo(service.optString("key"));
                }
            }
            return null;
        }



        private boolean isValidHostName(String hostName) {
            return hostName.matches("[A-Za-z0-9._:-]+");
        }


    }

    private static class ServiceGraph {
        private final JSONArray nodes;
        private final JSONArray links;
        private final List<String> serviceNames;

        private ServiceGraph(JSONArray nodes, JSONArray links, List<String> serviceNames) {
            this.nodes = nodes == null ? new JSONArray() : nodes;
            this.links = links == null ? new JSONArray() : links;
            this.serviceNames = serviceNames == null ? new ArrayList<>() : new ArrayList<>(serviceNames);
        }

        JSONArray getNodes() {
            return nodes;
        }

        JSONArray getLinks() {
            return links;
        }

        List<String> getServiceNames() {
            return serviceNames;
        }
    }

    private static class ServiceInfo {
        private final String key;

        private ServiceInfo(String key) {
            this.key = key;
        }

        String key() {
            return key;
        }
    }
}

