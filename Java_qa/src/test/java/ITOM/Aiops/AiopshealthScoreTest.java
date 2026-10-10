package ITOM.Aiops;

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
import util.SolqaDetails;
import util.SolqaLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AiopshealthScoreTest {
    private static final SolqaLogger log = new SolqaLogger(AiopshealthScoreTest.class.getName());
    CommonMethods commonmethod = new CommonMethods();
    SoftAssert softAssert = null;
    private String saveServiceEndPoint = "/service-management/api/v1.0/service/save";
    private String getServiceEndPoint = "/service-management/api/v1.0/service/get";
    private String getaiopsServiceModel = "getAiopsServiceModel.json";
    private String updateEventEndPoint = "/events-service/api/v1.0/events/";


    

    @Test(description = "Service Model Health Score Validation with Event Rules", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "15411", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_15411_Service_Model_Health_Score_Validation_with_Event_Rules(ITestContext context) throws Exception {
        this.softAssert = new SoftAssert();
        String hostName = "host-01-02";
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String serviceName = "ML-TSOM";
        HMEvents hm_events = new HMEvents();
        Aiops aiopsApiCore = new Aiops();
        List<String> eventIds = new ArrayList<>();
        CommonWaits commonWait = new CommonWaits();
        AiopsSituation aiopsSituation = new AiopsSituation();


        // 1. Send two events with Severity = UNKNOWN and different messages
        log.info("Sending two events with Severity=UNKNOWN on host " + hostName);
        String msg1 = "Test event message 1 " + System.currentTimeMillis();
        String msg2 = "Test event message 2 " + (System.currentTimeMillis() + 1);

        String eventClass = "NAGIOS_EV1";
        String location = "TestLocation";
        String event_tag = "TestTag";
        int no_of_event_per_node = 1;
        String jwtToken = commonmethod.refreshJwtToken(null);
        Map<String, String> healthMap = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        String serviceKey = healthMap.get("key");
        aiopsSituation.deleteEventProfile(serviceKey);
        log.info("Deleted existing event profile for service: " + serviceName);
        commonWait.waitForSometime(10000); //wait for 10 seconds to delete the event profile
        if (!aiopsSituation.updateEventProfile(serviceKey)) {
            Assert.fail("Failed to update event profile for service: " + serviceName);
        }
        commonWait.waitForSometime(10000); //wait for 10 seconds to update the event profile
        log.info("Updated event profile for service: " + serviceName);
        try {
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            aiopsSituation.CloseAllEventOnService(serviceName);
            log.info("Closed all events for class " + eventClass);
            // Use streamRestApiEvents to send events and get event IDs
            String eventId1 = aiopsSituation.extractEventId(hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, hostName, msg1, "UNKNOWN", location, event_tag));
            String eventId2 = aiopsSituation.extractEventId(hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, hostName, msg2, "UNKNOWN", location, event_tag));
            eventIds.add(eventId1);
            eventIds.add(eventId2);

            log.info("Event IDs: " + eventId1 + ", " + eventId2);

            // 2. Change severity of both events to CRITICAL using PATCH
            log.info("Changing severity of both events to CRITICAL");
            for (String eventId : eventIds) {
                String patchBody = "{\"severity\": \"CRITICAL\"}"; //Body is dynamic i.e only one filed is getting updated so no json file created
                logAnalyticsPage.Send_RestCall("PATCH", updateEventEndPoint + eventId, new String[]{"200", patchBody});
                log.info("Set severity CRITICAL for event " + eventId);
            }

            // 3. Change event Status to Acknowledged and Priority to P3
            log.info("Changing event status to Acknowledged and priority to PRIORITY_3");
            for (String eventId : eventIds) {
                String patchBody = "{\"status\": \"ACK\"}";
                logAnalyticsPage.Send_RestCall("PATCH", updateEventEndPoint + eventId, new String[]{"200", patchBody});
                log.info("Set status ACK for event " + eventId);
            }

            // 4. Validate Health Score <= 90 of ML-TSOM service
            log.info("Validating Health Score <= 90 for service: " + serviceName);
            int healthScore = 100;
            for (int i = 0; i < 10; i += 1) {
                healthScore = aiopsSituation.extractHealthScore(aiopsApiCore.getHealthScore(serviceName, jwtToken));
                log.info("Health Score: " + healthScore);
                if (healthScore <= 90) {
                    break;
                }
                commonWait.waitForSometime(15000);
            }
            softAssert.assertTrue(healthScore <= 90, "Health Score should be <= 90 after CRITICAL events");

            // 5. Send another event with Severity = UNKNOWN
            log.info("Sending another event with Severity=UNKNOWN");
            String msg3 = "Test event message 3 " + (System.currentTimeMillis() + 2);
            String eventId3 = aiopsSituation.extractEventId(hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, hostName, msg3, "UNKNOWN", location, event_tag));
            eventIds.add(eventId3);

            // 6. Change the severity of this new event to MAJOR, then to CRITICAL
            log.info("Changing severity of new event to MAJOR, then to CRITICAL");
            String patchMajor = "{\"severity\": \"MAJOR\"}";
            logAnalyticsPage.Send_RestCall("PATCH", updateEventEndPoint + eventId3, new String[]{"200", patchMajor});
            log.info("Set severity MAJOR for event " + eventId3);
            String patchCritical = "{\"severity\": \"CRITICAL\"}";
            logAnalyticsPage.Send_RestCall("PATCH", updateEventEndPoint + eventId3, new String[]{"200", patchCritical});
            log.info("Set severity CRITICAL for event " + eventId3);

            // 7. Change event Status to Acknowledged
            log.info("Changing status of new event to Acknowledged");
            String patchAck = "{\"status\": \"ACK\"}";
            logAnalyticsPage.Send_RestCall("PATCH", updateEventEndPoint + eventId3, new String[]{"200", patchAck});
            log.info("Set status ACK for event " + eventId3);

            // 8. Validate Health Score <= 90 of ML-TSOM service
            log.info("Validating Health Score <= 90 for service: " + serviceName + " after new CRITICAL event");
            healthScore = aiopsSituation.extractHealthScore(aiopsApiCore.getHealthScore(serviceName, jwtToken));
            log.info("Health Score: " + healthScore);
            softAssert.assertTrue(healthScore <= 90, "Health Score should be <= 90 after new CRITICAL event");

            // 9. Close the CRITICAL event by changing Status = Closed and Severity = UNKNOWN together
            log.info("Closing CRITICAL event by setting Status=Closed and Severity=UNKNOWN");
            String patchClose = "{\"status\": \"CLOSED\", \"severity\": \"UNKNOWN\"}";
            for (String eventId : eventIds) {
                logAnalyticsPage.Send_RestCall("PATCH", updateEventEndPoint + eventId, new String[]{"200", patchClose});
                log.info("Closed event " + eventId + " with severity UNKNOWN");
            }

            // 10. Validate Health Score = 100 of ML-TSOM service
            aiopsSituation.CloseAllEventOnService(serviceName);
            log.info("Validating Health Score = 100 for service: " + serviceName);

            for (int i = 0; i < 10; i += 1) {
                healthMap = aiopsApiCore.getHealthScore(serviceName, jwtToken);
                healthScore = aiopsSituation.extractHealthScore(healthMap);
                log.info("Health Score: " + healthScore);
                if (healthScore > 90) {
                    break;
                }
                commonWait.waitForSometime(15000);
            }
            softAssert.assertEquals(healthScore, 100, "Health Score should be 100 after closing CRITICAL event");

            // 11. Total Events = 1 (only MINOR remains), Impacted Events = 0
            log.info("Validating total and impacted events for service: " + serviceName);
            // Extract event summary from getHealthScore result


            int totalEvents = -1;
            if (healthMap.containsKey("totalEventCount")) {
                try {
                    totalEvents = Integer.parseInt(healthMap.get("totalEventCount"));
                } catch (Exception ignored) {
                }
            }
            log.info("Total Events: " + totalEvents);
            softAssert.assertEquals(totalEvents, 0, "Total Events should be 0 )");
        } catch (Exception e) {
            aiopsSituation.deleteEventProfile(serviceKey);
            log.info("Deleted event profile in exception handler");
        }finally {
            aiopsSituation.deleteEventProfile(serviceKey);
            log.info("Deleted event profile after test completion");
            softAssert.assertAll();
        }
    }
}