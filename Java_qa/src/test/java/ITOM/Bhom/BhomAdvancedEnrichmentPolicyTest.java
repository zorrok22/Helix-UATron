package ITOM.Bhom;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.SolqaDetails;
import util.SolqaLogger;
import util.ReadPropertiesFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.put;

/**
 * Java translation of Robot: "AIOPS: Advanced Enrichment Event Policies"
 */
public class BhomAdvancedEnrichmentPolicyTest {
    private static final SolqaLogger log = new SolqaLogger(BhomAdvancedEnrichmentPolicyTest.class.getName());
    CommonMethods commonmethod = new CommonMethods();
    private static final String EVENT_POLICY_ENDPOINT = "/events-service/api/v1.0/event_policies";
    private static final String EVENT_MSEARCH = "/events-service/api/v1.0/events/msearch";
    private static final String EVENT_CLASS = "NAGIOS_EV1";
    private static final String LOCATION = "Pune";
    private static final String EVENT_TAG = "";
    private final HMEvents hmEvents = new HMEvents();
    private final CommonWaits commonWaits = new CommonWaits();
    private final SoftAssert softAssert = new SoftAssert();


    private void createPolicy(String filename, String policyName) throws IOException, InterruptedException {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String jsonContent = hmEvents.readJSon(filename);
        jsonContent = jsonContent.replace("<POLICY_NAME>", policyName);
        logAnalyticsPage.Send_RestCall("POST", EVENT_POLICY_ENDPOINT, new String[]{"200", jsonContent});
        if(logAnalyticsPage.response_code > 299){
            Assert.fail("Fail to create Event Policy " + filename);
        }

    }



    private int countOpenEventsWithMessage(String jwtToken, String message) {
        int eventCnt = 0;
        String msearchEventsBody = "msearchqueryallevent.json";
        long timediff = 1800000; // 24 hours in milliseconds
        // 2. Get total events matching policy (last 24h, excluding class=situation)
        JSONObject situationObject = hmEvents.runMSearchQueryGetSituation("dummy", jwtToken, msearchEventsBody, timediff);
        log.info("Situation object received from msearch query");
        JSONObject hitsobject = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        JSONArray hitsArray = hitsobject.getJSONArray("hits");
        for (Object hits : hitsArray) {
            JSONObject hit = ((JSONObject) hits).getJSONObject("_source");
            String msg = hit.optString("msg", "");
            String status = hit.optString("status", "");
            if(!"OPEN".equalsIgnoreCase(status)){
                continue;
            }
            if (msg.contains(message)){
                eventCnt += 1;
            }
        }
        return eventCnt;
    }

    @Test(description = "AIOPS: Advanced Enrichment Event Policies", groups = {"regression"})
    @SolqaDetails(authorName = "Automation", JiraID = "AUTO-ADV-ENRICH", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void validateAdvancedEnrichmentPolicies(ITestContext context) throws Exception {
        String jwtToken = commonmethod.refreshJwtToken(null);
        String PolicyNamePrefix = "Advanced_event_enrichment_policy";
        log.info("JWT token refreshed");
        log.info("CLose all open event on Nagios class");
        hmEvents.closeAllEventByClass(EVENT_CLASS, jwtToken);

        String policy1 = "AdvanceEnrichPolicyNewEvent.json";
        String policy2 = "AdvanceEnrichPolicyRemoveDuplicate.json";
        String policy3 = "AdvanceEnrichPolicyLookup.json";

        log.info("Creating advanced enrichment policies");
        createPolicy(policy1, PolicyNamePrefix);
        createPolicy(policy2, PolicyNamePrefix);
        createPolicy(policy3, PolicyNamePrefix);


        log.info("Sending 10 unique CRITICAL events");
        List<String> messages = new ArrayList<>();
        String duplicateMessage = null;
        String msg = "Advance_Enrichment_Policy_Test_" + UUID.randomUUID().toString().substring(0, 6);
        for (int i = 0; i < 10; i++) {
            messages.add(msg);
            if (i == 0) duplicateMessage = msg;
            hmEvents.streamRestApiEvents(EVENT_CLASS, 1, jwtToken, "abc.com", msg, "CRITICAL", LOCATION, EVENT_TAG);
            commonWaits.waitForSometime(500);
        }

        log.info("Sending 10 duplicate CRITICAL events for message: " + duplicateMessage);
        for (int i = 0; i < 10; i++) {
            hmEvents.streamRestApiEvents(EVENT_CLASS, 1, jwtToken, "abc.com", duplicateMessage, "CRITICAL", LOCATION, EVENT_TAG);
            commonWaits.waitForSometime(500);
        }

        log.info("Waiting for enrichment to process");
        commonWaits.waitForSometime(20000);

        int openCount = countOpenEventsWithMessage(jwtToken, duplicateMessage);
        log.info("Open event count for message " + duplicateMessage + " = " + openCount);
        softAssert.assertEquals(openCount, 1, "Expected only one open event after de-dup enrichment");

        // cleanup created policies
        log.info("Cleaning up created policies");
        hmEvents.SearchanddeleteEventPolicies(PolicyNamePrefix,jwtToken);
        log.info("CLose all open event on Nagios class");
        hmEvents.closeAllEventByClass(EVENT_CLASS, jwtToken);


        softAssert.assertAll();
    }
}

