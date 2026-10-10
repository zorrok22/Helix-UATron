package ITOM.Aiops;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AiopsSituationMultiService {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationMultiService.class.getName());
    private static final int event_cnt = 1;
    
    public static final String event_tag = "";
    private static String eventClass = "NAGIOS_EV1";
    private static String location = "Pune";
    private static String eventSeverity = "CRITICAL";
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    CommonWaits commonWait = new CommonWaits();
    SoftAssert softAssert = null;
    CommonMethods commonmethod = new CommonMethods();

    @Test(description = "[Situation across service boundary]: verify multi-service situation is created with interconnected nodes with one node part of different service and other node outside of both service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12482", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12482_verify_multi_service_situation_is_created_with_interconnected_nodes_with_one_node_part_of_different_service_and_other_node_outside_of_both_service(ITestContext context)
            throws IOException {
        String jwtToken = commonmethod.refreshJwtToken(null);
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        String[] NodeList = new String[]{"Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB4 on houperfms692", "houperfms692", "Oracle Net Services (TNS) Listener 11g Release 2 identified as LISTENER on houperfms692", "Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB2 on houperfms692"};
        String[] ServiceList = new String[]{"ML-ERIC-SERVICE-1", "ML-ERIC-SERVICE-2"};
        hm_events.CloseAllOpenEvent(jwtToken);
        try {
            if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
                Assert.fail("Failed to enable ml-situations feature.");
            }
        } catch (InterruptedException e) {
            log.error("Failed to enable ml-situations feature: " + e.getMessage());
            e.printStackTrace();
        }

        log.info("Validate ml-situation-across-service-boundary enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled This TC cannot run");
            return;
        }

        for(String serviceName: ServiceList){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            log.info("Resetting health score for service " + serviceName);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"),jwtToken);
        }

        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            log.info("Sending event to " + hostname + " with message: " + msg);
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(ServiceList[0], jwtToken);
        log.info("Waiting for situation creation for service " + ServiceList[0]);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Get Service ID");
        JSONObject situationObject = aiopsApiCore.getSituationsForService(ServiceList[0],jwtToken);
        String msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService1 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(!msg.contains("Multi-Service")){
            softAssert.fail("No Multi service created");
        }

        situationObject = aiopsApiCore.getSituationsForService(ServiceList[1],jwtToken);
        msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService2 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(!msg.contains("Multi-Service")){
            softAssert.fail("No Multi service created");
        }
        if(!situationIdentifierService1.equals(situationIdentifierService2)){
            softAssert.fail("No Multi service created");
        }

        hm_events.CloseAllOpenEvent(jwtToken);
        softAssert.fail();
    }

    @Test(description = "[Situation across service boundary]: verify multi-service situation is created with interconnected nodes and one node being part of different service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12481", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12481_verify_multi_service_situation_is_created_with_interconnected_nodes_and_one_node_being_part_of_different_service(ITestContext context)
            throws IOException, InterruptedException {
        String jwtToken = commonmethod.refreshJwtToken(null);
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        String[] NodeList = new String[]{"Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB4 on houperfms692", "Oracle Database ORA112DB2", "Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB2 on houperfms692"};
        String[] ServiceList = new String[]{"ML-ERIC-SERVICE-2", "ML-ERIC-SERVICE-3"};
        hm_events.CloseAllOpenEvent(jwtToken);
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("situation_incident_consolidation is not enabled; skipping test as passed.");
            return;
        }

        log.info("Validate ml-situation-across-service-boundary enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("ml-situation-across-service-boundary is not enabled This TC cannot run");
            return;
        }
        
        for(String serviceName: ServiceList){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            log.info("Resetting health score for service " + serviceName);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"),jwtToken);
        }

        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            log.info("Sending event to " + hostname + " with message: " + msg);
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(ServiceList[0], jwtToken);
        log.info("Waiting for situation creation for service " + ServiceList[0]);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Get Service ID");
        JSONObject situationObject = aiopsApiCore.getSituationsForService(ServiceList[0],jwtToken);
        String msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService1 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(!msg.contains("Multi-Service")){
            softAssert.fail("No Multi service created");
        }

        situationObject = aiopsApiCore.getSituationsForService(ServiceList[1],jwtToken);
        msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService2 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(!msg.contains("Multi-Service")){
            softAssert.fail("No Multi service created");
        }
        if(!situationIdentifierService1.equals(situationIdentifierService2)){
            softAssert.fail("No Multi service created");
        }

        hm_events.CloseAllOpenEvent(jwtToken);
        softAssert.fail();
    }

    @Test(description = "[Situation across service boundary]: Verify situation is created with interconnected nodes and one node not part of the service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12480", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12480_verify_multi_service_situation_is_created_with_interconnected_nodes_and_one_node_not_part_of_the_service(ITestContext context)
            throws IOException, InterruptedException {
        String jwtToken = commonmethod.refreshJwtToken(null);
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        String[] NodeList = new String[]{"Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB4 on houperfms692", "houperfms692"};
        String[] ServiceList = new String[]{"ML-ERIC-SERVICE-1"};
        hm_events.CloseAllOpenEvent(jwtToken);
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("situation_incident_consolidation is not enabled; skipping test as passed.");
            return;
        }
        for(String serviceName: ServiceList){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            log.info("Resetting health score for service " + serviceName);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"),jwtToken);
        }

        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            log.info("Sending event to " + hostname + " with message: " + msg);
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(ServiceList[0], jwtToken);
        log.info("Waiting for situation creation for service " + ServiceList[0]);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Get Service ID");
        JSONObject situationObject = aiopsApiCore.getSituationsForService(ServiceList[0],jwtToken);
        String msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService1 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(msg.contains("Multi-Service")){
            softAssert.fail("Multi service sit created");
        }

        hm_events.CloseAllOpenEvent(jwtToken);
        softAssert.fail();
    }

    @Test(description = "[Situation across service boundary]: Provide way to exclude specific events from situation irrespective of event severity and service enrichment", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "13106", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13106_Provide_way_to_exclude_specific_events_from_situation_irrespective_of_event_severity_and_service_enrichment(ITestContext context)
            throws IOException, InterruptedException {
        String jwtToken = commonmethod.refreshJwtToken(null);
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        String[] NodeList = new String[]{"Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB4 on houperfms692", "houperfms692", "Oracle Net Services (TNS) Listener 11g Release 2 identified as LISTENER on houperfms692", "Oracle Database Server Enterprise 11g Release 2 identified as ORA112DB2 on houperfms692"};
        String[] ServiceList = new String[]{"ML-ERIC-SERVICE-1", "ML-ERIC-SERVICE-2"};
        hm_events.CloseAllOpenEvent(jwtToken);
        if (!aiopsApiCore.getTenantFeatureFlagStatus("ml-situation-across-service-boundary",jwtToken)){
            log.info("situation_incident_consolidation is not enabled; skipping test as passed.");
            return;
        }
        for(String serviceName: ServiceList){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            log.info("Resetting health score for service " + serviceName);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"),jwtToken);
        }

        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            log.info("Sending event to " + hostname + " with message: " + msg);
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
        }

        log.info("Sendf a Event with TAG");
        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            log.info("Sending event to " + hostname + " with message: " + msg);
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, "ExcludeFromSituation");
        }

        Map<String, String> healthScore = aiopsApiCore.getHealthScore(ServiceList[0], jwtToken);
        log.info("Waiting for situation creation for service " + ServiceList[0]);
        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 185000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("Get Service ID");
        JSONObject situationObject = aiopsApiCore.getSituationsForService(ServiceList[0],jwtToken);
        String msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService1 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(!msg.contains("Multi-Service")){
            softAssert.fail("No Multi service created");
        }

        situationObject = aiopsApiCore.getSituationsForService(ServiceList[1],jwtToken);
        msg = situationObject.getJSONArray("eventList").getJSONObject(0).getString("msg");
        String situationIdentifierService2 = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        if(!msg.contains("Multi-Service")){
            softAssert.fail("No Multi service created");
        }
        if(!situationIdentifierService1.equals(situationIdentifierService2)){
            softAssert.fail("No Multi service created");
        }

        List<String> eventIds = aiopsApiCore.getEventIdsAssociatedWithSituation(situationIdentifierService1, jwtToken);
        log.info("Event in Situation " + eventIds.size() + " Expected Event 3");
        if(eventIds.size() > 3){
            softAssert.fail("Situation has excluded event");
        }


        hm_events.CloseAllOpenEvent(jwtToken);
        softAssert.fail();
    }
}
