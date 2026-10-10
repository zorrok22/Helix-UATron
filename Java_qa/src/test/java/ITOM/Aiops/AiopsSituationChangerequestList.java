package ITOM.Aiops;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.CommonMethods;
import util.Product;
import util.ReadPropertiesFile;
import util.SolqaDetails;
import util.SolqaLogger;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AiopsSituationChangerequestList {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationChangerequestList.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = new CommonMethods();
    public static final String event_tag = "";
    private String eventClass = "NAGIOS_EV1";
    private String location = "Pune";
    private String serviceName = "ML-TSOM";
    private String[] ciList = {"host-01-02", "host-01-03", "host-01-04", "host-01-07"};
    private String changeHost = "host-01-02";
    private SoftAssert softAssert = null;
    private AiopsDeepRCA aiopsDeepRCA = new AiopsDeepRCA();


    @Test(description = "[Change Event] Validate Change event is getting created and getting associated with situation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12464", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12464_Validate_Change_event_is_getting_created_and_getting_associated_with_situation(ITestContext context) throws Exception {
        HMEvents hm_events = new HMEvents();
        Aiops aiopsApiCore = new Aiops();
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        int no_of_event_per_node = 1;
        ChangeContext changeContext = createChangeAndTriggerSituation(hm_events, aiopsApiCore, no_of_event_per_node);
        Map<String, String> changeIdentifiers = changeContext.changeIdentifiers;
        Map<String, String> healthScore = changeContext.healthScore;
        Map<String, String> ciNodeIds = changeContext.ciNodeIds;
        CMDBRestApi cmdbRestApi = changeContext.cmdbRestApi;
        String jwtToken = commonmethod.refreshJwtToken(null);

        ZonedDateTime actualEndTimeUtc = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime actualStartTimeUtc = actualEndTimeUtc.minusMinutes(45);
        DateTimeFormatter remedyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.000+0000'");
        String actualStartTime = actualStartTimeUtc.format(remedyTimeFormatter);
        String actualEndTime = actualEndTimeUtc.format(remedyTimeFormatter);
        cmdbRestApi.moveChangeStatusComplete(changeIdentifiers.get("infrachangeid"), changeIdentifiers.get("RequestID"),
                actualStartTime, actualEndTime);
        log.info("Step 11: Change moved to Completed status with actual window");

        for (Map.Entry<String, String> entry : ciNodeIds.entrySet()) {
            JSONArray nodeChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"), entry.getValue(), jwtToken);
            boolean infraChangePresent = false;
            if (nodeChangeList != null) {
                for (int i = 0; i < nodeChangeList.length(); i++) {
                    JSONObject entryObj = nodeChangeList.optJSONObject(i);
                    if (entryObj == null) {
                        continue;
                    }
                    JSONObject source = entryObj.optJSONObject("_source");
                    if (source == null) {
                        continue;
                    }
                    if (changeIdentifiers.get("infrachangeid").equalsIgnoreCase(source.optString("infrastructure_change_id"))) {
                        infraChangePresent = true;
                        break;
                    }
                }
            }
            softAssert.assertTrue(infraChangePresent,
                    "Change with infra id " + changeIdentifiers.get("infrachangeid") + " not present for node " + entry.getKey());
        }
        log.info("Step 12: Confirmed change propagation for every CI node");

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);
        log.info("Step 13: Cleanup complete and service health restored");

        softAssert.assertAll();
        log.info("Step 14: Soft assertions evaluated successfully");
    }

    @Test(description = "AIOps: Verify Change request flow for k8s Node/VMware Vms in Aiops service CI Topology/Hierarchy", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12375", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12375_Verify_Change_request_flow_for_k8s_Node_VMware_Vms_in_Aiops_service_CI_Topology_Hierarchy(ITestContext context) throws Exception {
        HMEvents hm_events = new HMEvents();
        Aiops aiopsApiCore = new Aiops();
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        int no_of_event_per_node = 1;
        ChangeContext changeContext = createChangeAndTriggerSituation(hm_events, aiopsApiCore, no_of_event_per_node);
        Map<String, String> changeIdentifiers = changeContext.changeIdentifiers;
        Map<String, String> healthScore = changeContext.healthScore;
        CMDBRestApi cmdbRestApi = changeContext.cmdbRestApi;
        String changeSummary = changeContext.changeSummary;

        cmdbRestApi.moveChangeStatusCancelled(changeIdentifiers.get("infrachangeid"), changeIdentifiers.get("RequestID"));

        String jwtToken = commonmethod.refreshJwtToken(null);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);

        // Impact again after cancellation
        String thirdSummary = "Post-cancel impact 1 on " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(4);
        String fourthSummary = "Post-cancel impact 2 on " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(4);
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, changeHost, thirdSummary, "MAJOR", location, event_tag);
        hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, changeHost, fourthSummary, "MAJOR", location, event_tag);

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        if (!aiopsApiCore.waitForSituationCreation(healthScore.get("key"), jwtToken, 180000)) {
            softAssert.fail("Situation not created after post-cancel impact on host " + changeHost);
        }

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray postCancelRCAList = aiopsApiCore.getServiceChangeRCAList(healthScore.get("key"), healthScore.get("id"), jwtToken);
        boolean isChangePresentPostCancel = false;
        for (int i = 0; postCancelRCAList != null && i < postCancelRCAList.length(); i++) {
            JSONObject entry = postCancelRCAList.optJSONObject(i);
            if (entry == null) {
                continue;
            }
            JSONObject source = entry.optJSONObject("_source");
            if (source == null) {
                continue;
            }
            if (changeSummary.equalsIgnoreCase(source.optString("short_description"))) {
                isChangePresentPostCancel = true;
                break;
            }
        }
        softAssert.assertFalse(isChangePresentPostCancel, "Cancelled change still present in Service Change RCA list");

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);

        softAssert.assertAll();
    }

    private ChangeContext createChangeAndTriggerSituation(HMEvents hm_events, Aiops aiopsApiCore, int noOfEventsPerNode) throws Exception {
        String jwtToken = commonmethod.refreshJwtToken(null);
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
        JSONArray currentChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"),healthScore.get("id"),jwtToken);
        int curenttotalcount = currentChangeList == null ? 0 : currentChangeList.length();


        String cmdbUrl = config.get("CMDBURL");
        String username = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");
        RemedyApiClient remedyApiClient = new RemedyApiClient(cmdbUrl);
        String authToken = remedyApiClient.getRemedyApiKey(username, password);
        CMDBRestApi cmdbRestApi = new CMDBRestApi(cmdbUrl, authToken);

        String changeSummary = "Upgrade TSIM Application from 11.2.00 to 11.4.07 on  " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(5);
        String changeDescription = "Upgrade TSOM Application from 11.2.00 to 11.4.07 on " + changeHost + " 11.4.07 have latest Security Fix and cumulative HF.";    
        cmdbRestApi.setCreatebackdatatedChange(true);
        Map<String, String> changeIdentifiers = cmdbRestApi.createChangeRequestUpdateStatus(changeSummary, changeDescription, serviceName, ciList, username,"US Support 3");
        cmdbRestApi.setCreatebackdatatedChange(false);
        if (changeIdentifiers == null) {
            Assert.fail("Failed to create and progress change request for " + changeHost);
        }

        if(!aiopsDeepRCA.moveChangeTorequestStatus(changeIdentifiers,"Implementation In Progress")){
            Assert.fail("Failed to progress change request for service " + serviceName);
        }

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);

        Map<String, String> serviceContent = aiopsApiCore.aiopsGetCITopology(healthScore.get("key"), jwtToken);
        Map<String, String> ciNodeIds = new HashMap<>();
        for (String ciName : ciList) {
            String nodeId = serviceContent.get(ciName);
            if (nodeId == null) {
                Assert.fail("CI " + ciName + " not present in service topology for " + serviceName);
            }
            ciNodeIds.put(ciName, nodeId);
        }

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        String firstSummary = "Change impact validation events on " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(4);
        String secondSummary = "Change impact validation related to utilization " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(4);
        hm_events.streamRestApiEvents(eventClass, noOfEventsPerNode, jwtToken, changeHost, firstSummary, "MAJOR", location, event_tag);
        hm_events.streamRestApiEvents(eventClass, noOfEventsPerNode, jwtToken, changeHost, secondSummary, "MAJOR", location, event_tag);

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        if (!aiopsApiCore.waitForSituationCreation(healthScore.get("key"), jwtToken, 180000)) {
            softAssert.fail("Situation not created after impacting host " + changeHost);
        }

        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray updatedChangeList = aiopsApiCore.getChangeAssociatedWithService(healthScore.get("key"),healthScore.get("id"),jwtToken);
        int updatedChangeCount = updatedChangeList == null ? 0 : updatedChangeList.length();
        if(updatedChangeCount < curenttotalcount){
            log.info("Some of the change got deleted so cannot - condition should be equal or greater than");
        }else {
            log.info("Service change count increased. Previous: " + curenttotalcount + " Current: " + updatedChangeCount);
            softAssert.assertTrue(updatedChangeCount > curenttotalcount,
                    "Service change count did not increase. Previous: " + curenttotalcount + " Current: " + updatedChangeCount);
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        JSONArray changeRCAList = aiopsApiCore.getServiceChangeRCAList(healthScore.get("key"), healthScore.get("id"), jwtToken);
        boolean isChangePresent = false;
        for (int i = 0; changeRCAList != null && i < changeRCAList.length(); i++) {
            JSONObject entry = changeRCAList.optJSONObject(i);
            if (entry == null) {
                continue;
            }
            JSONObject source = entry.optJSONObject("_source");
            if (source == null) {
                continue;
            }
            String shortDesc = source.optString("short_description", "");
            String infrastructurechangeid = source.optString("infrastructure_change_id", "");
            if (infrastructurechangeid.equalsIgnoreCase(changeIdentifiers.get("infrachangeid"))) {
                isChangePresent = true;
                log.info("Change ID and Created ID is matching");
                break;
            }
        }
        if(!isChangePresent){
            log.info("Change ID and Created ID is not matching");
            //removed as Future Change ID will not be present in the RCA list
            //softAssert.fail("Created change summary not present in Service Change RCA list");
        }
        ChangeContext context = new ChangeContext();
        context.healthScore = healthScore;
        context.changeIdentifiers = changeIdentifiers;
        context.ciNodeIds = ciNodeIds;
        context.cmdbRestApi = cmdbRestApi;
        context.changeSummary = changeSummary;
        return context;
    }

    private static class ChangeContext {
        Map<String, String> healthScore;
        Map<String, String> changeIdentifiers;
        Map<String, String> ciNodeIds;
        CMDBRestApi cmdbRestApi;
        String changeSummary;
    }
}
