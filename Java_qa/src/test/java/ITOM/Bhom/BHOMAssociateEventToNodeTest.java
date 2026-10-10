package ITOM.Bhom;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsPrediction;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Bhom.BhomCompositeAlarm;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BHOMAssociateEventToNodeTest {
    private static final SolqaLogger log = new SolqaLogger(BHOMAssociateEventToNodeTest.class.getName());
    HMEvents hm_events = null;
    BhomCompositeAlarm bhomCompositeAlarm = null;
    CommonWaits commonWait = null;
    CommonMethods commonmethod = null;
    SoftAssert softAssert = null;
    private String jwtToken = null;
    String compositeAlarmNamePrefix = "Composite Alarm Test";
    String compositeAlarmSheet = "CompositeAlarm";
    String className = "CompositeAlarm";
    String msearchQueryFile = "msearchqueryonOpenClass.json";
    private String EventClassName = "NAGIOS_EV1";
    private String SourceHostName = "host-01-03";
    private String Location = "localhost";
    Aiops aiopsApiCore = null;
    private String policyInit = "Automation_AssociateEventToNodePolicy";
    
    @BeforeMethod(alwaysRun = true) 
    public void beforeMethod() {
        log.info("Setting up the test...");
        commonmethod = new CommonMethods();
        jwtToken = commonmethod.refreshJwtToken(null);
        commonWait = new CommonWaits();
        hm_events = new HMEvents();
        bhomCompositeAlarm = new BhomCompositeAlarm();
        softAssert = new SoftAssert();
        aiopsApiCore = new Aiops();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        log.info("Delete associate event to node policy");
        hm_events.SearchanddeleteEventPolicies(policyInit, jwtToken);
    }

    @Test(description = "Verify the node kind association with an event as part of the AssociateEventToNode evaluation", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "19791", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_19791_verify_node_kind_association_with_event(ITestContext context) throws IOException {
        
        jwtToken = commonmethod.refreshJwtToken(null);
        hm_events.SearchanddeleteEventPolicies(policyInit, jwtToken);

        log.info("Close All Event on classname: " + EventClassName);
        hm_events.closeAllEventByClass(EventClassName, jwtToken);

        log.info("Create associate event to node policy");
        String policyname = policyInit + "_" + RandomStringUtils.randomAlphanumeric(10);
        boolean success = bhomCompositeAlarm.createAssociateEventToNodePolicy(policyname);
        if(!success){
            log.error("Failed to create associate event to node policy");
            Assert.fail( "Failed to create associate event to node policy");
        }
        log.info("Send a Event");
        String eventMessage = "Automation_Event_AssociateEventToNodePolicy_" + RandomStringUtils.randomAlphanumeric(10);
        hm_events.streamRestApiEvents(EventClassName, 1, jwtToken, SourceHostName, eventMessage, "WARNING", Location, "", "PRIORITY_3");
        commonWait.waitForSometime(30000);

        String searchEventFile = "msearchqueryonOpenClass.json";
        JSONObject EventResponse = hm_events.runMSearchQueryGetSituation(EventClassName, jwtToken,searchEventFile,60000);
        
        log.info("For loop all the record received on response");
        JSONObject hitsobject = EventResponse.getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
        boolean found = false;
        for( Object hit : hitsobject.getJSONArray("hits")) {
            JSONObject hitObject = (JSONObject) hit;
            String EventClass = hitObject.getJSONObject("_source").getString("class");
            if(EventClass.equals(EventClassName)){
                String message = hitObject.getJSONObject("_source").getString("msg");
                if(message.equals(eventMessage)){
                    String detail = hitObject.getJSONObject("_source").getString("details");
                    if(detail.length() < 2){
                        softAssert.fail("Event details is empty");
                    }
                    found = true;
                    break;
                }
            }
        }
        if(!found){
            softAssert.fail("Event not found");
        }
        softAssert.assertAll();

    }
}
