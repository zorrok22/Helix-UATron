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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AiopsSituationIncidentTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationIncidentTest.class.getName());
    public static final String configuration_end_point = "/aiops-config/api/v1.0/situation_configurations/search";
    CommonMethods commonmethod = new CommonMethods();
    private static final int event_cnt = 1;
    private static final long situationTimeDiff = 300000;
    public static final String aiopssituationconfigUrl = "/aiops-config/api/v1.0/situation_configurations/";
    public static final String event_tag = "";
    private static String eventClass = "NAGIOS_EV1";
    private static String location = "Pune";
    HMEvents hm_events = new HMEvents();
    CommonWaits commonWait = new CommonWaits();
    Aiops aiopsApiCore = new Aiops();
    SoftAssert softAssert = null;
    private ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    /**
     * Retrieves the first situation ID from the situation configurations.
     */
    private String getSituationId() throws IOException, InterruptedException {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String[] arry = {"200", "{}"};
        logAnalyticsPage.Send_RestCall("POST", configuration_end_point, arry);
        JSONObject record = logAnalyticsPage.jsonObject;
        return record.getJSONArray("situations").getJSONObject(0).getString("id");
    }

    @Test(description = "[Situation Incident Consolidation] Verify Incident Creation for Situation and Incident ID update for new Events", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12470", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12470_Verify_Incident_Creation_for_Situation_and_Incident_ID_update_for_new_Events(ITestContext context)
            throws IOException, InterruptedException {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        String eventSeverity = "CRITICAL";
        String mlSheetName = "mlcheck";
        int testcase_number = 5;
        String situationJsonFile = "msearchsituationquery.json";
        String jwtToken = commonmethod.refreshJwtToken(null);
        log.info("JWT token refreshed");
        if (!aiopsApiCore.enableMLSituationsFeature(jwtToken)) {
            Assert.fail("Failed to enable ml-situations feature.");
        }
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        log.info("Closed all events for class " + eventClass);
        commonWait.waitForSometime(15000);
        log.info("Initial wait after event cleanup");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("situation_incident_consolidation",jwtToken)){
            log.info("situation_incident_consolidation is not enabled; skipping test as passed.");
            return;
        }

        if(!hm_events.getIncidentPolicyEnable(jwtToken)){
            Assert.fail("IA PSR is not enabled cannot run this test case");
        }

        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        log.info("Check Service Model Exist");
        String[] bussness_service = row_data.get(0).get("Business Name").toString().split((","));
        aiopsApiCore.searchAndGetServiceDetails(bussness_service[0],jwtToken);
        log.info("Service model verified for " + bussness_service[0]);

        log.info("Get init service health score");
        List<Map<String, String>> businessHealthList = new ArrayList<>();
        for(String service_name: bussness_service){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service" + service_name + "Health Score " + healthScore.get("actualHealth"));
            businessHealthList.add(healthScore);
        }

        String[] NodeList = row_data.get(0).get("Impact Element").toString().split((","));
        String[] ci_priority = row_data.get(0).get("Impact Priority").toString().split((","));
        String[] ci_severity = row_data.get(0).get("Impact Severity").toString().split((","));

        aiopsApiCore.resetServiceHealthScore(businessHealthList.get(0).get("key"),jwtToken);
        log.info("Reset health score for service key " + businessHealthList.get(0).get("key"));


        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, eventSeverity, location, event_tag);
            log.info("Sent event to " + hostname + " with msg: " + msg);
        }

        if (!aiopsApiCore.waitForSituationCreationMsearch(businessHealthList.get(0).get("key"), jwtToken, 185000)) {
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            Assert.fail("Situation did not get created within 3 minutes.");
        }
        log.info("Situation created for service key " + businessHealthList.get(0).get("key"));

        String incident_id = "";
        String situationIdentifier = "";
        for (int j=0;j<5;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Polling situation for incident id, attempt " + (j+1));
            JSONObject situationObject = hm_events.runMSearchQueryGetSituation(businessHealthList.get(0).get("key"), jwtToken, situationJsonFile, situationTimeDiff);
            JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
            situationIdentifier = hitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
            incident_id = hitsobject.getJSONArray("hits").getJSONObject(0).getJSONObject("_source").optString("incident_id","");
            if(incident_id.length() > 5){
                log.info("Incident id found: " + incident_id);
                break;
            }
            commonWait.waitForSometime(5000);
            log.info("Waiting before next incident id poll");
        }
        if(incident_id.length() < 5){
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            Assert.fail("No incident got created at situation level.");
        }
        log.info("Situation identifier: " + situationIdentifier + " with incident id: " + incident_id);

        List<String> situationEventList = aiopsApiCore.getEventIdsAssociatedWithSituation(situationIdentifier, jwtToken);
        int eventCnt = situationEventList.toArray().length;
        log.info("Initial event count in situation: " + eventCnt);
        for (String eventId : situationEventList) {
            commonWait.waitForSometime(1000);
            JSONObject jsonObject = new JSONObject(hm_events.eventDetails(eventId, jwtToken));
            String eventIncidentId = jsonObject.getJSONObject("response").getJSONObject("allslots").getString("incident_id");
            if(eventIncidentId.length() > 5){
                if(!eventIncidentId.equalsIgnoreCase(incident_id)) {
                    softAssert.fail("incident id does not match with situation. Event is Critical");
                }
            }
        }

        //Send New Event

        for (String hostname : NodeList) {
            String randomStr = RandomStringUtils.randomAlphanumeric(7);
            String msg = "Memory utilization of " + hostname + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, event_cnt, jwtToken, hostname, msg, "MAJOR", location, event_tag);
            log.info("Sent additional MAJOR event to " + hostname + " with msg: " + msg);
        }

        //Wait for new event to get added to Situation
        int CurreventCnt = 0;
        for (int j=0;j<13;j++) {
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Polling for new events in situation, attempt " + (j+1));
            situationEventList = aiopsApiCore.getEventIdsAssociatedWithSituation(situationIdentifier, jwtToken);
            CurreventCnt = situationEventList.toArray().length;
            log.info("Current Event Count <" + CurreventCnt + "> previous Event Count <" + eventCnt + ">");
            if(CurreventCnt > eventCnt){
                break;
            }
            commonWait.waitForSometime(15000);
            log.info("Waiting before next event count poll");
        }

        if(CurreventCnt == eventCnt){
            hm_events.closeAllEventByClass(eventClass, jwtToken);
            Assert.fail("No new event got added");
        }
        log.info("New events added to situation. Current count: " + CurreventCnt);

        //validate
        for (String eventId : situationEventList) {
            commonWait.waitForSometime(1000);
            JSONObject jsonObject = new JSONObject(hm_events.eventDetails(eventId, jwtToken));
            String eventIncidentId = jsonObject.getJSONObject("response").getJSONObject("allslots").getString("incident_id");
            if(eventIncidentId.length() > 5){
                if(!eventIncidentId.equalsIgnoreCase(incident_id)) {
                    softAssert.fail("incident id does not match with situation");
                }
            }
        }
        log.info("Closing all events for cleanup");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        softAssert.assertAll();

    }
}
