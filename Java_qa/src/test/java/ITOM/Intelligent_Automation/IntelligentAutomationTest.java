package ITOM.Intelligent_Automation;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.Discovery.DiscoveryPage;
import pages.ITOM.Intelligent_Automation.*;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntelligentAutomationTest extends Launcher {

    private static SolqaLogger log = new SolqaLogger(IntelligentAutomationTest.class.getName());
    public static String ISConnectorName="IS";
    public static String app="ITOM";
    public static String ISSearchAction="Case - Get Case Details";
    public static String IATile="Intelligent Automation";
    public static String AIOpsTitle="Monitoring";
    public static String AIOpsService;
    public static String TSO_ConnectorName="Automation TSO Connector";
    public static String tsoConnector="TrueSight Orchestration";
    public String portal="BMC Helix Portal";
    IntelligentAutomationPage iaPage;
    PoliciesPage policiesPage;
    HistoryPage historyPage;
    AIOpsPage aiOpsPage;
    CommonWaits commonWaits;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    public String IS_PolicyName;
    String cluster_CI=SolQAConstants.CLUSTER_CI;
    String accessKey = SolQAConstants.API_ACCESS_KEY;
    String secreteKey = SolQAConstants.API_SECRET_KEY;

    DiscoveryPage discoveryPage;
    ServiceMonitoringPage smPage;
    BhomPage monitoringPage;
    WebDriver driver;
    IAFeatureMethod IAFeatureMethod;
    BhomFeatureMethods bhomFeatureMethods;
    String Transformation_Name_Field = "name";
    String Transformation_Job_Field = "job";
    String datePattern = "yyyy-MM-dd";
    String randomHHMMSS = " 01:02:30"; //HH:MM:SS format


    Portal_FeatureMethods portalFeatureMethods;
    AIOpsFeatureMethods aiOpsFeatureMethods;

    public void doPreconditions() {
        driver = this.launchWeb(app, app);
        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
        setDriver(driver);
        commonWaits=new CommonWaits();
        IAFeatureMethod = new IAFeatureMethod();
        iaPage = new IntelligentAutomationPage();
        policiesPage = new PoliciesPage();
        discoveryPage = new DiscoveryPage();
        monitoringPage = new BhomPage();
        aiOpsPage = new AIOpsPage();
        historyPage = new HistoryPage();
        smPage = new ServiceMonitoringPage(getDriver());
        IS_PolicyName="UsedForAutomation";
        AIOpsService="SolqaAutomationIAService" + RandomStringUtils.randomAlphanumeric(5);
        bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods = new Portal_FeatureMethods();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
     }

    @Test(description = "Verify BHOM-IA-ServiceNow-Non-TMF PSR for Multi-Service Situation https://jira.example.com/browse/DRSQA-12548",groups={"snow"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12548", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12548_Verify_BHOM_IA_ServiceNow_Non_TMF_PSR_usecase() throws Exception {
        log.info("------- START: DRSQA-12548 Verify BHOM-IA-ServiceNow-Non-TMF PSR for Multi-Service Situation ------------");

        doPreconditions();

        // CI configured with SNOW is "host-01-10" . To be run ona a specific tenant - ocitenant1.
        cluster_CI = "host-01-10";

        log.info("Wake up SNOW instance if its hibernating.");
        IAFeatureMethod.wakeUpSNOWFromHibernation();

        log.info("Check if PSR TMF solution is configured. If yes. Disable & delete all of it. & create fresh one.");
        IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();

        log.info("Configure NON TMF SMOW PSR Solution with Incident noise reduction enabled.");
        IAFeatureMethod.configureSnowInstanceOnPSRTMF(false,true);

        log.info("Wait for some time to offset lag in the system");
        commonWaits.waitForSometime(commonWaits.minDelay);

        String uniqueEventMessage= commonWaits.generateRandomString();
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
        String uniqueEventMessage_2= commonWaits.generateRandomString();
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage_2);
        bhomFeatureMethods.send_events(uniqueEventMessage_2, cluster_CI);

        log.info("Wait for Situation to get generated.");
        commonWaits.waitForSometime(commonWaits.moreDelay);

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(SolQAConstants.SNOW_IA_POLICY,"Automatic");

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        IAFeatureMethod.switchToBHOM();
        bhomFeatureMethods.searchAndOpenEventsByMessage(uniqueEventMessage);

        String exactMsg = bhomFeatureMethods.getEventMessage();
        log.info("Exact event Message: "+exactMsg);
        Assert.assertTrue(exactMsg.contains(uniqueEventMessage));

        String snowIncidentID= bhomFeatureMethods.getSNOWIncidentValue();
        log.info("snow Incident ID: "+snowIncidentID);

        log.info("Non TMF-L2: Login to SNOW & verify if incident got genearted in SNOW.");
        IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),exactMsg,"",cluster_CI,"");

        log.info("Go to BHOM. Close events.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessage,"");

        log.info("Wait for sometime for snow to reflect change in incident status.");
        commonWaits.waitForSometime(commonWaits.minDelay);

        log.info("Non TMF-L2: Login to SNOW again to check if incident status is resolved and CI is listed in the incident generated.");
        IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),exactMsg,"Resolved",cluster_CI,"");

    }

    @Test(description = "Verify BHOM-IA-ServiceNow-TMF PSR L3 usecase https://jira.example.com/browse/DRSQA-12508",groups={"snow"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12508", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12508_Verify_BHOM_IA_ServiceNow_TMF_PSR_L3_usecase() throws Exception {
        log.info("------- START: DRSQA-12508 Verify BHOM-IA-ServiceNow-TMF PSR L3 usecase ------------");

        doPreconditions();

        // CI configured with SNOW is "host-01-10" . To be run on a specific tenant only- OCItenant1.
        cluster_CI = "host-01-10";

        log.info("Wake up SNOW instance if its hibernating.");
        IAFeatureMethod.wakeUpSNOWFromHibernation();

        log.info("Check if PSR TMF solution is configured. If yes. Disable & delete all of it. & create fresh one.");
        IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();

        log.info("Configure TMF SMOW PSR Solution with Incident noise reduction enabled.");
        IAFeatureMethod.configureSnowInstanceOnPSRTMF(true,true);

        log.info("Wait for some time to offset lag in the system");
        commonWaits.waitForSometime(commonWaits.minDelay);

        String uniqueEventMessage= commonWaits.generateRandomString();
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
        String uniqueEventMessage_2= commonWaits.generateRandomString();
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage_2);
        bhomFeatureMethods.send_events(uniqueEventMessage_2, cluster_CI);

        log.info("Wait 1 min for Situation to get generated.");
        commonWaits.waitForSometime(commonWaits.moreDelay*12);

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(SolQAConstants.TMF_SNOW_IA_POLICY,"Automatic");

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        IAFeatureMethod.switchToBHOM();
        bhomFeatureMethods.searchAndOpenEventsByMessage(uniqueEventMessage);

        String exactMsg = bhomFeatureMethods.getEventMessage();
        log.info("Exact event Message: "+exactMsg);
        Assert.assertTrue(exactMsg.contains(uniqueEventMessage));

        String snowIncidentID= bhomFeatureMethods.getSNOWIncidentValue();
        log.info("snow Incident ID: "+snowIncidentID);

        log.info("TMF L3: Login to SNOW & verify if incident got genearted in SNOW.");
        IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),uniqueEventMessage,"",cluster_CI,"");

        log.info("Go to BHOM. Close events.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessage,"");

        log.info("Wait for sometime for snow to reflect change in incident status.");
        commonWaits.waitForSometime(commonWaits.minDelay);

        log.info("TMF L3: Login to SNOW again to check if incident status is resolved. CI and service name both are listed in the incident generated.");
        IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),uniqueEventMessage,"Resolved",cluster_CI,SolQAConstants.SNOW_Service);

    }

    @Test(description = "Verify BHOM-IA-ServiceNow-Non-TMF PSR for Situation (Incident Noise reduction support) https://jira.example.com/browse/DRSQA-12517",groups={"snow"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12517", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12517_Verify_BHOM_IA_ServiceNow_Non_TMF_PSR_for_Situation() throws Exception {
        log.info("------- START: DRSQA-12517 Verify BHOM-IA-ServiceNow-Non-TMF PSR for Situation (Incident Noise reduction support) ------------");

        // CI configured with SNOW is "host-01-10" . To be run ona a specific tenant.
        cluster_CI = "host-01-10";

        doPreconditions();
        log.info("Wake up SNOW instance if its hibernating.");
        IAFeatureMethod.wakeUpSNOWFromHibernation();

        log.info("Check if PSR TMF solution is configured. If yes. Disable & delete all of it. & create fresh one.");
        IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();

        log.info("Configure NON TMF SMOW PSR Solution with Incident noise reduction enabled.");
        IAFeatureMethod.configureSnowInstanceOnPSRTMF(false,true);

        log.info("Wait for some time to offset lag in the system");
        commonWaits.waitForSometime(commonWaits.minDelay);

        String uniqueEventMessage= commonWaits.generateRandomString();
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
        String uniqueEventMessage_2= commonWaits.generateRandomString();
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage_2);
        bhomFeatureMethods.send_events(uniqueEventMessage_2, cluster_CI);

        log.info("Wait for Situation to get generated.");
        commonWaits.waitForSometime(commonWaits.moreDelay*12);

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(SolQAConstants.SNOW_IA_POLICY,"Automatic");

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        IAFeatureMethod.switchToBHOM();
        bhomFeatureMethods.searchAndOpenEventsByMessage(uniqueEventMessage);

        String exactMsg = bhomFeatureMethods.getEventMessage();
        log.info("Exact event Message: "+exactMsg);
        Assert.assertTrue(exactMsg.contains(uniqueEventMessage));

        String snowIncidentID= bhomFeatureMethods.getSNOWIncidentValue();
        log.info("snow Incident ID: "+snowIncidentID);

        log.info("Non TMF L3: Login to SNOW & verify if incident got genearted in SNOW.");
        IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),uniqueEventMessage,"New",cluster_CI,SolQAConstants.SNOW_Service);

        log.info("Go to BHOM. Close events.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessage,"");

        log.info("Wait for sometime for snow to reflect change in incident status.");
        commonWaits.waitForSometime(commonWaits.moreDelay);

        log.info("Non TMF L3: Login to SNOW again to check if incident status is resolved and CI is listed in the incident generated.");
        IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),uniqueEventMessage,"Resolved",cluster_CI,SolQAConstants.SNOW_Service);

    }

    @Test(description = "Create Policy: JSON_Editor: Define action using JSON Editor , add transformation to the JSON fields. https://jira.example.com/browse/DRSQA-13178",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-13178", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_13178_Create_Policy_JSON_Editor_add_transformation_to_the_JSON_fields() throws Exception{

        doPreconditions();

        String REST_API_Connector_Name = SolQAConstants.REST_API_CONNECTOR_NAME;
        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String Transformation_Name = "morpheus";
        String Transformation_Job = "leader";

        IAFeatureMethod.connect_GenericRestAPI_Connector();

        log.info("Populate transformer values in lowercase.");
        JSONObject json = new JSONObject();
        json.put(Transformation_Name_Field, Transformation_Name);
        json.put(Transformation_Job_Field, Transformation_Job);

        log.info("Create a policy with REST API connector and add transformation to the JSON fields.");
        IAFeatureMethod.Configure_RESTAPI_policy_with_Transformations(REST_API_IA_Policy_Name,REST_API_Connector_Name,json.toString());

        String uniqueEventMessage=commonWaits.generateRandomString();
        log.info("Send event with message: " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Uppercase.");
        IAFeatureMethod.openFirstResultAndCheckJsonField(Transformation_Name.toUpperCase());

    }

    @Test(description = "Edit Policy: JSON_Editor: Define action using JSON Editor , update transformation to the JSON fields. https://jira.example.com/browse/DRSQA-13180",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-13180", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_13180_Edit_Policy_JSON_Editor_update_transformation_to_the_JSON_fields() throws Exception{

        doPreconditions();

        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String Transformation_Name = "morpheus";

        log.info("Update an existing policy with REST API connector and update transformation to the JSON fields.");
        IAFeatureMethod.edit_RESTAPI_policy_with_Transformation_functions(REST_API_IA_Policy_Name,"toLowerCase");

        String uniqueEventMessage=commonWaits.generateRandomString();
        log.info("Send event with message: " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Lowercase.");
        IAFeatureMethod.openFirstResultAndCheckJsonField(Transformation_Name.toLowerCase());

    }

    @Test(description = "Edit Policy: JSON_Editor: Define action using JSON Editor , delete transformation to the JSON fields. https://jira.example.com/browse/DRSQA-13181",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-13181", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_13181_Edit_Policy_JSON_Editor_delete_transformation_to_the_JSON_fields() throws Exception{

        doPreconditions();

        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String Transformation_Name = "morpheus";

        log.info("Update an existing policy with REST API connector and delete transformation to the JSON fields.");
        IAFeatureMethod.delete_transformations_from_RESTAPI_policy(REST_API_IA_Policy_Name);

        String uniqueEventMessage=commonWaits.generateRandomString();
        log.info("Send event with message: " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added JSON value is processed without any transformation.");
        IAFeatureMethod.openFirstResultAndCheckJsonField(Transformation_Name);

    }

    @Test(description = "Create Policy: JSON_Editor: Define action using JSON Editor , add Date transformation to the JSON fields. https://jira.example.com/browse/DRSQA-13606",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-13606", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_13606_Create_Policy_JSON_Editor_add_date_transformation_to_the_JSON_fields() throws Exception{
        log.info("------- START: DRSQA-13606 Create Policy: JSON_Editor: Define action using JSON Editor , add Date transformation to the JSON fields ------------");
        doPreconditions();

        int iDays = 5;
        String REST_API_Connector_Name = SolQAConstants.REST_API_CONNECTOR_NAME;
        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String expectedDate = commonWaits.getCurrentDateTime(datePattern)+randomHHMMSS;
        String pastDate = commonWaits.getPastDateTime(iDays,datePattern);
        String Transformation_Name = pastDate+randomHHMMSS;
        String Transformation_Job = pastDate+randomHHMMSS;

        log.info("Populate transformer date values.");
        JSONObject json = new JSONObject();
        json.put(Transformation_Name_Field, Transformation_Name);
        json.put(Transformation_Job_Field, Transformation_Job);

        try{
        log.info("Create a policy with REST API connector and add date transformation to the JSON fields.");
        IAFeatureMethod.Configure_RESTAPI_policy_with_Date_Transformations(REST_API_IA_Policy_Name,REST_API_Connector_Name,json.toString(),iDays);

        String uniqueEventMessage=commonWaits.generateRandomString();
        log.info("Send event with message: " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all expected date values.");
        IAFeatureMethod.openFirstResultAndCheckJsonField(expectedDate);}
        finally {
            log.info("Finally, Delete the created Policy");
            IAFeatureMethod.update_IA_policy_status_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME,"DRAFT");
            IAFeatureMethod.delete_IA_policy_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME);
        }

    }

    @Test(description = "Support of Add Variables in Create Automation Policy(Hardcoded value is set in variables)",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12518", Products = {Product.intelligent_automation,Product.Helix_OpManagement})
    public void DRSQA_12518_SupportAddVariableInCreateAutomationPolicyWithHardCordedValue(ITestContext context ) throws Exception {

        log.info("------- START: DRSQA-12518 Support of Add Variables in Create Automation Policy(Hardcoded value is set in variables) ------------");

        doPreconditions();
        log.info("-------  Create connector if not present and Create Rest generics Policy --> " + SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME + " <-- if not available -------");
        IAFeatureMethod.configure_IA_policy_with_GenericsRestConnector_UsingHardcodedValueVariable(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME, SolQAConstants.IA_VARIABLE_NAME);
        log.info("-----cleanup-----");
        IAFeatureMethod.update_IA_policy_status_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME);
        log.info("------- END: DRSQA-12518 Support of Add Variables in Create Automation Policy(Hardcoded value is set in variables) ------------");
    }

    @Test(description = "Create Policy: JSON_Editor: Define action using JSON Editor , add Array transformation to the JSON fields. https://jira.example.com/browse/DRSQA-13608",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-13608", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_13608_Create_Policy_JSON_Editor_add_array_transformation_to_the_JSON_fields() throws Exception {
        log.info("------- START: DRSQA-13608 Create Policy: JSON_Editor: Define action using JSON Editor , add Array transformation to the JSON fields ------------");
        doPreconditions();

        String REST_API_Connector_Name = SolQAConstants.REST_API_CONNECTOR_NAME;
        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String Transformation_Name = "morpheus";
        String Transformation_Job = "leader";
        String expectedValue = "BMW";
        String uniqueEventMessage = "alpha";
        String arrayDetails = "[\"Ford\",\"BMW\",\"Fiat\"]";
        String detailsLine = "    \"details\": \"[\\\"Ford\\\", \\\"BMW\\\", \\\"Fiat\\\"]\",\n";
        String jsonOutput = "{\n" + "  \"msg\": \"" + uniqueEventMessage + "\",\n" + "  \"details\": " + arrayDetails + "\n" + "}";

        log.info("Populate transformer values in lowercase.");
        JSONObject json = new JSONObject();
        json.put(Transformation_Name_Field, Transformation_Name);
        json.put(Transformation_Job_Field, Transformation_Job);

        try {
            log.info("Create a policy with REST API connector and add Array transformation to the JSON fields.");
            IAFeatureMethod.Configure_RESTAPI_policy_with_Array_Transformations(REST_API_IA_Policy_Name, REST_API_Connector_Name, jsonOutput, json.toString(), 1);

            log.info("Send event with json payload having alpha as a message.");
            bhomFeatureMethods.send_events_with_details(uniqueEventMessage, cluster_CI, detailsLine);

            log.info("Go to BHOM -> Events Page and search for the event generated.");
            bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

            log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
            bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

            log.info("Verify that Policy is executed successfully on History Page in IA");
            IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

            log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Uppercase.");
            IAFeatureMethod.openFirstResultAndCheckJsonField(expectedValue);
        } finally {
            log.info("Finally, Delete the created Policy");
            IAFeatureMethod.update_IA_policy_status_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME, "DRAFT");
            IAFeatureMethod.delete_IA_policy_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME);
        }
    }

    @Test(description = "Support of Add Variables in Create Automation Policy(BHOM event slot data is used as variable value)",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12549", Products = {Product.intelligent_automation,Product.Helix_OpManagement})
    public void DRSQA_12549_SupportAddVariableInCreateAutomationPolicyWithBHOMEventsSlot(ITestContext context ) throws Exception {

        log.info("------- START: DRSQA-12549 Support of Add Variables in Create Automation Policy(BHOM event slot data is used as variable value) ------------");

        doPreconditions();
        String uniqueEventMessage= SolQAConstants.eventName + commonWaits.generateRandomString();

        log.info("***** Create Rest generics Policy --> " + IS_PolicyName + " <-- if not available *******");
        String policyName = IAFeatureMethod.configure_IA_policy_with_GenericsRestConnector(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME, uniqueEventMessage, SolQAConstants.IA_VARIABLE_NAME);

        log.info("Send event with message: " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

        log.info("Verify that Policy is executed successfully");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME, policiesPage.successText);

        log.info("-----cleanup-----");
        IAFeatureMethod.update_IA_policy_status_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME);
    }

    @Test(description = "Verify Generic cloud connector Configure with APiKey(Feature flagged)-TSO connector",groups={"Genrestcloud"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-13500", Products = {Product.intelligent_automation,Product.Helix_OpManagement})
    public void DRSQA_13500_VerifyGenericcloudconnectorConfigurewithAPiKey(ITestContext context ) throws Exception {
        log.info("------- START: DRSQA-13500 Verify Generic cloud connector Configure with APiKey(Feature flagged)-TSO connector ------------");
        doPreconditions();

        String REST_API_Connector_Name = SolQAConstants.REST_API_Cloud_CONNECTOR_NAME_TSO;
        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_Cloud_IA_POLICY_NAME;

        log.info("Create a REST API cloud connector with end point TSO.");
        log.info("This is prerequisite as generic rest cloud connector is feature flaged");
        //IAFeatureMethod.Configure_GenRestwithAPIKeys_TSO_Cloud_Connector(REST_API_Connector_Name);

        log.info("Create a policy with REST API cloud connector with end point TSO.");
        IAFeatureMethod.Configure_GENREST_APIKey_Cloud_TSO_policy(REST_API_IA_Policy_Name, REST_API_Connector_Name);

        String uniqueEventMessage = commonWaits.generateRandomString();
        log.info("Send event with message: " + uniqueEventMessage);
        bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("-----cleanup-----");
        IAFeatureMethod.deletePolicy(REST_API_IA_Policy_Name);
    }

    @Test(description = "[Intelligent Automation] Verify reporting in Intelligent Automation by consuming grafana dashboard https://jira.example.com/browse/DRSQA-12370", groups = {"Regression"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12370", Products = {Product.intelligent_automation})
    public void DRSQA12370_Verify_reporting_in_Intelligent_Automation_by_consuming_grafana_dashboard(ITestContext context) throws Exception {
        
        log.info("======================================================================");
        log.info("TEST: DRSQA-12370 - Verify reporting in Intelligent Automation by consuming grafana dashboard");
        log.info("======================================================================");
        doPreconditions();

        IAFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        log.info("***** Create IS Policy --> " + IS_PolicyName + " <-- if not available *******");
        IAFeatureMethod.configure_IS_connector("ISCloud");
        IAFeatureMethod.Configure_IA_policy_with_Innovation_Studio_API(IS_PolicyName);

        try {

            String uniqueEventMessage = commonWaits.generateRandomString();
            log.info("Send event with message: " + uniqueEventMessage);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

            log.info("Go to BHOM -> Events Page and search for the event generated.");
            bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

            log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
            bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);

            log.info("Verify that Policy is executed successfully on History Page in IA");
            IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(IS_PolicyName, policiesPage.successText);

            log.info("Navigate to Value Dashboard and validate");
            IAFeatureMethod.navigateToValueDashboard();
            IAFeatureMethod.validateValueDashboard();

        }
        finally {
            bhomFeatureMethods.closeBrowser();
            //removed deletion of Policy
        }

    }

    @Test(description = "Verify option to manually create incident after PSR solution is configured https://jira.example.com/browse/DRSQA-12446", groups = {"Regression"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12446", Products = {Product.intelligent_automation, Product.AiOPS, Product.ADE_OperationsManagement})
    public void DRSQA12446_Verify_option_to_manually_create_incident_after_PSR_solution_is_configured(ITestContext context) throws Exception {
        
        log.info("TEST: DRSQA-12446 - Verify option to manually create incident after PSR solution is configured");
        doPreconditions();

        String serviceName = "PSR_Service_" + commonWaits.generateRandomString();
        bhomFeatureMethods.closeEventsWithMsgAndCI("", cluster_CI);
        try {
            log.info("Disable and delete earlier PSR solutions if any exist.");
            IAFeatureMethod.navigateToIA();
            IAFeatureMethod.disablePSRSolution();
            //IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();

            log.info("STEP 1: Configure PSR solution in IA");
            String uniqueEventMessage = commonWaits.generateRandomString();
            log.info("Sending event with message: " + uniqueEventMessage);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            // Wait for event to be processed
            commonWaits.waitForSometime(commonWaits.moreDelay);

            log.info("Step 2: Enable PSR with CMDB URL: " + configProp.get("CMDBURL"));
            IAFeatureMethod.enablePSRSolution();
            //portalFeatureMethods.enablePSRWithINRFlag(configProp.get("CMDBURL"), configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"),false);

            log.info("STEP 3: Navigate to BHOM Events page and search for the event");
            bhomFeatureMethods.searchForEventGeneratedWithDetails(uniqueEventMessage);

            log.info("STEP 4: Open event and Verify Create Incident option is present");
            log.info("Verifying Create Incident option is present for the event in BHOM.");
            boolean createIncidentOption = bhomFeatureMethods.isCreateIncidentOptionPresent();
            Assert.assertTrue(createIncidentOption, "Create Incident option is not present when IA-PSR Solution is enabled.");
            
            // Click on Create Incident
            if (createIncidentOption) {
                log.info("Clicking on Create Incident option");
                bhomFeatureMethods.clickCreateIncident();
                bhomFeatureMethods.searchAndOpenEventsByMessage(uniqueEventMessage);
            }

            log.info("STEP 5: Verify PSR policy is triggered in IA History page");
            IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(SolQAConstants.ITSM_PSR_IA_POLICY, "Automatic");

            bhomFeatureMethods.closeEventsWithMsgAndCI("", cluster_CI);
            log.info("STEP 6: Disable PSR solution from IA");
            IAFeatureMethod.disablePSRSolution();

            log.info("Wait for 1 minute time instead of sending event immediately. IA to BHOM updates take time.");
            commonWaits.waitForSometime(60000);

            log.info("STEP 7: Create Service in AIOps if not available");
            String uniqueEventMessageForServiceEvent = "PSR_EVENT"+commonWaits.generateRandomString();
            cluster_CI = "CLM-Cluster"; //ci-name of Vcenter service as required for test pre-condition
            aiOpsFeatureMethods.createServiceFromAPI(serviceName, cluster_CI, accessKey, secreteKey);
            commonWaits.waitForSometime(commonWaits.moreDelay);
            bhomFeatureMethods.send_events(uniqueEventMessageForServiceEvent, cluster_CI);

            log.info("STEP 8: Enable PSR solution from IA");
            IAFeatureMethod.enablePSRSolution();

            log.info("---- Wait until 3 min for the service to get impacted.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(3))) {
                log.info("Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. ");
            }

            log.info("STEP 9: Verify Create Incident option in AIOps Service");
            aiOpsFeatureMethods.verifyCreateIncidentOptionInAiopsService(serviceName);
            IAFeatureMethod.closeIATab();

        } finally {
            log.info("---------------------------------------------------------------");
            log.info("CLEANUP: Delete Service in AIOps. And delete PSR solutions ");
            log.info("---------------------------------------------------------------");
            log.info("Deleting the service from Discovery.");
            try{
            aiOpsFeatureMethods.deleteService(serviceName);}
            //IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();}
            catch (Exception e){
                log.info("Exception during cleanup: " + e.getMessage());
            }

        }
    }

    @Test(description = "Verify BHOM-IA-ServiceNow PSR L1 usecase(Incident) https://jira.example.com/browse/DRSQA-12435",groups={"snow"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12435", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12435_Verify_BHOM_IA_ServiceNow_TMF_PSR_L1_usecase() throws Exception {
        log.info("------- START: DRSQA-12508 Verify BHOM-IA-ServiceNow-TMF PSR L3 usecase ------------");

        doPreconditions();

        log.info("Wake up SNOW instance if its hibernating.");
        IAFeatureMethod.wakeUpSNOWFromHibernation();

        log.info("Check if PSR TMF solution is configured. If yes. Disable & delete all of it. & create fresh one.");
        IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();

        log.info("Configure TMF SMOW PSR Solution with Incident noise reduction enabled.");
        IAFeatureMethod.configureSnowInstanceOnPSRTMF(true,false);

        log.info("Wait for some time to offset lag in the system");
        commonWaits.waitForSometime(commonWaits.minDelay);

        try {
            String uniqueEventMessage = commonWaits.generateRandomString();
            log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            String uniqueEventMessage_2 = commonWaits.generateRandomString();
            log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage_2);
            bhomFeatureMethods.send_events(uniqueEventMessage_2, cluster_CI);

            log.info("Wait for Situation to get generated.");
            commonWaits.waitForSometime(commonWaits.moreDelay);

            log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
            IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(SolQAConstants.TMF_SNOW_IA_POLICY, "Automatic");

            log.info("Go to BHOM -> Events Page and search for the event generated.");
            IAFeatureMethod.switchToBHOM();
            bhomFeatureMethods.searchAndOpenEventsByMessage(uniqueEventMessage);

            String snowIncidentID = bhomFeatureMethods.getSNOWIncidentValue();
            log.info("snow Incident ID: " + snowIncidentID);

            log.info("TMF L1: Login to SNOW & verify if incident got genearted in SNOW.");
            IAFeatureMethod.openIncidentInSNOWAndVerify(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"),uniqueEventMessage, "", "", "");

            log.info("Go to BHOM. Close events.");
            bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessage, "");
        }finally {
            log.info("---------------------------------------------------------------");
            log.info("CLEANUP: Delete PSR TMF solutions ");
            log.info("---------------------------------------------------------------");
            IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();
        }
    }

    @Test(description = "Verify BHOM-IA-ServiceNow PSR L1 usecase (Case) https://jira.example.com/browse/DRSQA-12433", groups={"snow"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12433", Products = {Product.intelligent_automation, Product.AiOPS, Product.ADE_OperationsManagement, Product.SNOW})
    public void DRSQA12433_Verify_BHOM_IA_ServiceNOW_PSR_L1_use_case_for_create_Case(ITestContext context) throws Exception {

        log.info("------- START: Verify BHOM-IA-ServiceNow PSR L1 usecase(Case) ------------");
        String eventName = "L1Event_" + RandomStringUtils.randomAlphanumeric(5);
        String shortDescription = "Intelligent Automation";
        String caseListURL = "/now/nav/ui/classic/params/target/sn_customerservice_case_list.do";
        doPreconditions();

        try {

            log.info("********* Precondition: To check SNOW instance is hibernate, if yes wake up ****************");
            IAFeatureMethod.wakeUpSNOWFromHibernation();

            log.info("******** Step 1: Configure SNOW Solution for case ************");
            log.info("disable PSR solution");
            IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();

            log.info("configure snow instance in PSR TMF solution with Case support");
            IAFeatureMethod.configureSnowInstanceOnPSRTMFWithCase(true, false);

            log.info("******** Step 2: Send event without hostname (i.e L1) through API **********");
            commonWaits.waitForSometime(commonWaits.minDelay);
            bhomFeatureMethods.send_events(eventName, "");
            commonWaits.waitForSometime(commonWaits.moreDelay);

            log.info("****** Step 3: Go to BHOM -> Search event ***********");
            IAFeatureMethod.switchToBHOM();
            bhomFeatureMethods.searchAndOpenEventsByMessage(eventName);
            String exactMessage = bhomFeatureMethods.getEventMessage();
            log.info("Exact event message: " + exactMessage);

            String snowCaseID= bhomFeatureMethods.getSNOWIncidentValue();
            log.info("snow case ID: "+snowCaseID);

            String internalID = monitoringPage.getEventInternalID();
            log.info("Event Internal ID: " + internalID);

            log.info("******* Step 4: Verify policy is executed in IA ***********");
            IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(SolQAConstants.TMF_SNOW_IA_POLICY, "Automatic");

            log.info("******* Step 5: Login to Snow Instance and verify SNOW case generated for event ***********");
            IAFeatureMethod.openSNOWAndReturnDriver().getSNOWDriver().get(config.get("SNOW_URL") + caseListURL);

            IAFeatureMethod.openCaseInSNOWAndVerify(exactMessage, shortDescription);

            log.info("******** Step 6: Close event from BHOM ************");
            log.info("Go to BHOM. Close events.");
            bhomFeatureMethods.closeEventsWithMsgAndCI(exactMessage,"");

            log.info("------- END: Verify BHOM-IA-ServiceNow PSR L1 usecase(Case) ------------");
        }finally {
            log.info("******** Postcondition: Delete TMF PSR solution *********");
            IAFeatureMethod.disableAndDeleteAllEarlierPSRSolutions();
            IAFeatureMethod.getSNOWDriver().quit();
        }
    }

    @Test(description = "[Intelligent Automation] Verify user can edit and modify the published policy https://jira.example.com/browse/DRSQA-12489", groups = {"Regression"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12489", Products = {Product.intelligent_automation})
    public void DRSQA12489_Verify_user_can_edit_and_modify_the_published_policy(ITestContext context) throws Exception {

        log.info("------- START:  [Intelligent Automation] Verify user can edit and modify the published policy ------------");
        
        String updatedPolicyName = "";
        String confirmationMessage = "Changes made to a published policy cannot be rolled back. Do you want to continue?";
        String updatedPolicyNameWithPrefix="";

        try {
            log.info("******** Precondition: Create Policy in IA ***********");
            doPreconditions();

            IAFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
            IAFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
            log.info("***** Create IS Policy --> " + IS_PolicyName + " <-- if not available *******");
            IAFeatureMethod.configure_IS_connector("ISCloud");
            IAFeatureMethod.Configure_IA_policy_with_Innovation_Studio_API(IS_PolicyName);


            log.info("******** Step 1: Verify created policy is present on Policies page. Verify its details****************");
            iaPage.navigateToAutomationPage();
            iaPage.navigateToPoliciesPage();
            policiesPage.searchPolicy(IS_PolicyName, 2);
            Assert.assertTrue(iaPage.policyNameOnPolicyPage.getText().trim().contains(IS_PolicyName),
                    "Created policy is not present on policies page, expected -" + IS_PolicyName + " actual - " + iaPage.policyNameOnPolicyPage.getText().trim());
            Assert.assertTrue(policiesPage.policyNameLoc.getText().trim().equalsIgnoreCase(policiesPage.manualText),
                    "execution mode is not present on policies page, expected -" + policiesPage.manualText + " actual - " + policiesPage.policyNameLoc.getText().trim());
            Assert.assertTrue(policiesPage.policyStatusLoc.getText().trim().equalsIgnoreCase(policiesPage.publishedText),
                    "Published text is not present on policies page, expected -" + policiesPage.publishedText + " actual - " + policiesPage.policyStatusLoc.getText().trim());

            log.info("******** Step 2: Click on Actions dropdown -> Verify Edit option is enabled ************");
            iaPage.actionONSearchPolicy(iaPage.edit);

            log.info("******** Step 3: Edit Some fields -> Check 'Test policy' button -> click on Save button **************");
            updatedPolicyNameWithPrefix = IS_PolicyName + "_Updated";
            policiesPage.setPolicyNameAndDescription(updatedPolicyNameWithPrefix);
            String testPolicyButtonStatus = iaPage.verifybuttonStatus_onPoliciespage(iaPage.testPolicyButton);
            if (testPolicyButtonStatus.equalsIgnoreCase("true")) {
                log.info("Test policy button is disabled for published policy");
            } else {
                Assert.fail("Test policy button is enabled for published policy");
            }
            iaPage.SavePolicyWithConfirmationDialog(confirmationMessage);

            log.info("******** Step 4: Again Edit policy -> Verify updated details *****************");
            policiesPage.searchPolicy(updatedPolicyNameWithPrefix, policiesPage.rowCount);
            policiesPage.actionONSearchPolicy(iaPage.edit);
            updatedPolicyName = iaPage.getPolicyName();
            log.info("updated policy name is - " + updatedPolicyName);
            Assert.assertTrue(iaPage.getPolicyName().contains(updatedPolicyNameWithPrefix), "Policy name is not as per expectation");
            Assert.assertTrue(iaPage.getDescription().contains(updatedPolicyNameWithPrefix), "Description is not as per expectation");
            iaPage.SavePolicyWithConfirmationDialog(confirmationMessage);

            log.info("********* Step 5: Uncheck 'Publish policy' checkbox -> Click on Save button -> Verify confirmation dialog -> click on Confirm ****************");
            IAFeatureMethod.update_IA_policy_status_via_api(updatedPolicyNameWithPrefix,"DRAFT");

            log.info("********* Step 6: Verify policy status as Draft ***********");
            policiesPage.searchPolicy(updatedPolicyName, 1);
            Assert.assertTrue(policiesPage.getPolicyStatus().trim().equalsIgnoreCase(policiesPage.draftText),
                    "Policy is not in Draft status, expected -" + policiesPage.draftText + " actual - " + policiesPage.policyStatusLoc.getText().trim());

            log.info("********* Step 7: Publish the policy ***************");
            policiesPage.actionONSearchPolicy(iaPage.publish);

            log.info("------- END:  [Intelligent Automation] Verify user can edit and modify the published policy ------------");

        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw e;
        } finally {
            log.info("********* PostCondition: Delete policy ");
            try {
                if (updatedPolicyNameWithPrefix != null && !updatedPolicyNameWithPrefix.isEmpty()) {
                    IAFeatureMethod.update_IA_policy_status_via_api(updatedPolicyNameWithPrefix,"DRAFT");
                    IAFeatureMethod.delete_IA_policy_via_api(updatedPolicyNameWithPrefix);
                }else{
                    IAFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
                    IAFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
                }
            } catch (Exception e) {
                log.warn("Cleanup failed: " + e.getMessage());
            }
        }

    }

    @Test(description = "Verify BHOM-IA-ITSM PSR L1 use case for create incident",groups={"Regression"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12438", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12438_Verify_BHOM_IA_ITSM_PSR_L1_usecase() throws Exception {
        log.info("------- START: DRSQA_12438_Verify_BHOM_IA_ITSM_PSR_L1_usecase ------------");

        doPreconditions();
        log.info("PSR Solution is configured in IA as part of preconditions");
        IAFeatureMethod.navigateToIA();
       
        log.info("STEP 5: Create Service in AIOps if not available");
        String uniqueEventMessageForServiceEvent = "PSR_EVENT"+commonWaits.generateRandomString();
        log.info("Using MSg for sending event: "+uniqueEventMessageForServiceEvent);
        bhomFeatureMethods.send_events(uniqueEventMessageForServiceEvent, cluster_CI);
        commonWaits.waitForSometime(commonWaits.moreDelay);

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful("Create or Update Incident Ticket","Automatic");
        commonWaits.waitForSometime(120000); //waiting for 3 minutes for the Incident to be generated


        log.info("Go to BHOM -> Events Page and search for the event generated.");
        //IAFeatureMethod.switchToBHOM();
        bhomFeatureMethods.searchForEventGeneratedWithDetails(uniqueEventMessageForServiceEvent);

        String exactMsg = bhomFeatureMethods.getEventMessage();
        log.info("Exact event Message: "+exactMsg);
        Assert.assertTrue(exactMsg.contains(uniqueEventMessageForServiceEvent));

        String IncidentID= bhomFeatureMethods.getIncidentValue();
        log.info("Incident ID: "+IncidentID);
        Assert.assertTrue(IncidentID!=null && !IncidentID.isEmpty(), "Incident ID is not generated.");

        log.info("Go to BHOM. Close L2 event.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessageForServiceEvent,"");

        log.info("Wait for sometime for ITSM to reflect change in incident status.");
        commonWaits.waitForSometime(commonWaits.moreDelay);

        String status = IAFeatureMethod.getIncidentStatus(IncidentID);

        log.info("Incident Status is: " + status);

        Assert.assertTrue(status.equalsIgnoreCase("Resolved"), "Incident is not resolved as expected.");
    }

    @Test(description = "Verify BHOM-IA-ITSM PSR L2 use case for create incident",groups={"Regression"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12439", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12439_Verify_BHOM_IA_ITSM_PSR_L2_usecase() throws Exception {
        log.info("------- START: DRSQA_12439_Verify_BHOM_IA_ITSM_PSR_L2_usecase ------------");

        doPreconditions();
        List<String> cINames = IAFeatureMethod.getCIWhichIsSyncedInCMDB(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),"host-01");
        if (cINames.size() == 0) {
            log.info("No CI is synced in CMDB. L2 scenario will not work. " );
            throw new Exception("Precondition failed: No CI is synced in CMDB. L2 scenario will not work." );
        } else {
            log.info("CI synced in CMDB for the cluster CI: " + cluster_CI + " are: " + String.join(", ", cINames));
        }

        log.info("Using CI: " + cINames.get(0) + " for further test execution.");
        cluster_CI= cINames.get(0);

        log.info("Pre steps: Close all earlier open events. Perform ADDM Job reconcilation from CMDB side.");
        IAFeatureMethod.runADDMJob();

        log.info("PSR Solution is configured in IA as part of preconditions");
        IAFeatureMethod.navigateToIA();

        log.info("STEP 5: Create Service in AIOps if not available");
        String uniqueEventMessageForServiceEvent = "PSR_EVENT"+commonWaits.generateRandomString();
        log.info("Using MSg for sending event: "+uniqueEventMessageForServiceEvent);
        bhomFeatureMethods.send_events_with_object(uniqueEventMessageForServiceEvent, cluster_CI);
        commonWaits.waitForSometime(commonWaits.moreDelay);

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful("Create or Update Incident Ticket","Automatic");
        commonWaits.waitForSometime(120000); //waiting for 3 minutes for the Incident to be generated

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGeneratedWithDetails(uniqueEventMessageForServiceEvent);

        String exactMsg = bhomFeatureMethods.getEventMessage();
        log.info("Exact event Message: "+exactMsg);
        Assert.assertTrue(exactMsg.contains(uniqueEventMessageForServiceEvent));

        String IncidentID= bhomFeatureMethods.getIncidentValue();
        log.info("Incident ID: "+IncidentID);

        //log.info("TMF L3: Login to ITSM & verify if L2 incident got generated in ITSM with CI details.");
        //IAFeatureMethod.VerifyCINameInIncident(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),IncidentID);

        log.info("Go to BHOM. Close L2 event.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessageForServiceEvent,"");

        log.info("Wait for sometime for ITSM to reflect change in incident status.");
        commonWaits.waitForSometime(commonWaits.moreDelay);

        String status = IAFeatureMethod.getIncidentStatus(IncidentID);

        log.info("Incident Status is: " + status);

        Assert.assertTrue(status.equalsIgnoreCase("Resolved"), "Incident is not resolved as expected.");
    }

    @Test(description = "Verify BHOM-IA-ITSM PSR L3 use case for create incident",groups={"Regression"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-12443", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12443_Verify_BHOM_IA_ITSM_PSR_L3_usecase() throws Exception {
        log.info("------- START: DRSQA_12443_Verify_BHOM_IA_ITSM_PSR_L3_usecase ------------");

        doPreconditions();

        String finalCI="", finalService="";
        String CI_String=SolQAConstants.CLUSTER_CI;

        log.info("Fetching CI names synced in CMDB for the cluster CI String: " + CI_String);
        List<String> cINames = IAFeatureMethod.getCIWhichIsSyncedInCMDB(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),CI_String);
        System.out.println("CINAMES:"+cINames);
        if (cINames.size() == 0) {
            log.info("No CI synced in CMDB for the cluster CI String: " + CI_String);
            throw new Exception("Precondition failed: No CI synced in CMDB for the cluster CI String: " + CI_String);
        } else {
            log.info("CI synced in CMDB for the cluster CI: " + CI_String + " are: " + String.join(", ", cINames));
        }


        log.info("Searching for a CI and its associated Service which is synced in CMDB for L3 scenario.");
        boolean found=false;
        for (String ciName : cINames) {
            String result = ciName.replace(".example.com", "");
            List<String> listOfServices = aiOpsFeatureMethods.getServicesFromAPIWithRelatedCis(result, accessKey, secreteKey);
            if (listOfServices.size() > 0) {
                for (String svcName : listOfServices) {
                    if (IAFeatureMethod.IsThisServiceSyncedInCMDB(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),svcName)){
                        finalCI=result;
                        finalService=svcName;
                        found=true;
                        break;
                    };
                }

            }
        }

        if (!found){
        throw new Exception("Precondition failed: No Service found in AIOps synced with CMDB for the CI. L3 scenario can't be executed.");
        }
        log.info("Using CI: " + finalCI + " and Service: " + finalService + " for further test execution.");

        cluster_CI= finalCI;

        log.info("Pre steps: Close all earlier open events. Perform ADDM Job reconcilation from CMDB side.");
        IAFeatureMethod.runADDMJob();

        log.info("PSR Solution is configured in IA as part of preconditions");
        IAFeatureMethod.navigateToIA();

        log.info("Wait for some time to offset lag in the system");
        commonWaits.waitForSometime(commonWaits.minDelay);

        log.info("STEP 5: Create Service in AIOps if not available");
        String uniqueEventMessageForServiceEvent = "PSR_EVENT"+commonWaits.generateRandomString();
        bhomFeatureMethods.send_events(uniqueEventMessageForServiceEvent, cluster_CI);
        commonWaits.waitForSometime(commonWaits.moreDelay);

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful("Create or Update Incident Ticket","Automatic");
        commonWaits.waitForSometime(120000); //waiting for 3 minutes for the Incident to be generated

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGeneratedWithDetails(uniqueEventMessageForServiceEvent);

        String exactMsg = bhomFeatureMethods.getEventMessage();
        log.info("Exact event Message: "+exactMsg);
        Assert.assertTrue(exactMsg.contains(uniqueEventMessageForServiceEvent));

        String IncidentID= bhomFeatureMethods.getIncidentValue();
        log.info("Incident ID: "+IncidentID);

        //log.info("TMF L3: Login to ITSM & verify if L3 incident got generated in ITSM with CI details.");
        //IAFeatureMethod.VerifyCINameInIncident(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),IncidentID);

        log.info("Go to BHOM. Close L3 event.");
        bhomFeatureMethods.closeEventsWithMsgAndCI(uniqueEventMessageForServiceEvent,"");

    }

    @Test(description = "[Intelligent Automation] Verify IA Remediation when BHOM event is moved to Acknowledged. https://jira.example.com/browse/DRSQA-17460", groups = {"Regression"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-17460", Products = {Product.intelligent_automation})
    public void DRSQA_17460_Verify_IA_Remediation_when_BHOM_event_is_moved_to_Acknowledged() throws Exception {
        log.info("------- START: DRSQA_17460_Verify_IA_Remediation_when_BHOM_event_is_moved_to_Acknowledged ------------");

        doPreconditions();
        String ISpolicyName = "DRSQA_17460_IS_Policy";
        IAFeatureMethod.update_IA_policy_status_via_api(ISpolicyName,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(ISpolicyName);
        log.info("***** Create IS Policy --> " + ISpolicyName + " <-- if not available *******");
        IAFeatureMethod.configure_IS_connector("IScloud");
        IAFeatureMethod.Configure_IA_Automatic_policy_with_Innovation_Studio_API(ISpolicyName);
        log.info("Policy is successfully created and published.");

        long epochSec = System.currentTimeMillis() / 1000;

        String uniqueEventMessage = ISpolicyName + "_" + epochSec;
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
        bhomFeatureMethods.send_IA_policy_event(cluster_CI, uniqueEventMessage);

        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Move the event to Acknowledged state.");
        String fullMsg = ISpolicyName + "_" + epochSec + "NT_MEMORY Memory used > 100 MB for 0 min";
        bhomFeatureMethods.acknowledgeEvent(fullMsg);

        log.info("Switch to IA page.");
        IAFeatureMethod.switchtoIA();

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(ISpolicyName,"Automatic");
    }

    @Test(description = "[Intelligent Automation] Verify IA Remediation when BHOM event is moved to Assigned. https://jira.example.com/browse/DRSQA-17461", groups = {"Regression"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-17461", Products = {Product.intelligent_automation})
    public void DRSQA_17461_Verify_IA_Remediation_when_BHOM_event_is_moved_to_Assigned() throws Exception {
        log.info("------- START: DRSQA_17461_Verify_IA_Remediation_when_BHOM_event_is_moved_to_Assigned ------------");

        doPreconditions();
        String ISpolicyName = "DRSQA_17461_IS_Policy";
        IAFeatureMethod.update_IA_policy_status_via_api(ISpolicyName,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(ISpolicyName);
        log.info("***** Create IS Policy --> " + ISpolicyName + " <-- if not available *******");
        IAFeatureMethod.configure_IS_connector("IScloud");
        IAFeatureMethod.Configure_IA_Automatic_policy_with_Innovation_Studio_API(ISpolicyName);
        log.info("Policy is successfully created and published.");

        long epochSec = System.currentTimeMillis() / 1000;

        String uniqueEventMessage = ISpolicyName + "_" + epochSec;
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
        bhomFeatureMethods.send_IA_policy_event(cluster_CI, uniqueEventMessage);

        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Move the event to Acknowledged state.");
        String fullMsg = ISpolicyName + "_" + epochSec + "NT_MEMORY Memory used > 100 MB for 0 min";
        bhomFeatureMethods.assignEvent(fullMsg);

        log.info("Switch to IA page.");
        IAFeatureMethod.switchtoIA();

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(ISpolicyName,"Automatic");
        
    }


    @Test(description = "[Intelligent Automation] Verify IA Remediation when BHOM event is moved to closed. https://jira.example.com/browse/DRSQA-17462", groups = {"Regression"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-17462", Products = {Product.intelligent_automation})
    public void DRSQA_17462_Verify_IA_Remediation_when_BHOM_event_is_moved_to_Closed() throws Exception {
        log.info("------- START: DRSQA_17462_Verify_IA_Remediation_when_BHOM_event_is_moved_to_Closed ------------");

        doPreconditions();
        String ISpolicyName = "DRSQA_17462_IS_Policy";
        IAFeatureMethod.update_IA_policy_status_via_api(ISpolicyName,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(ISpolicyName);
        log.info("***** Create IS Policy --> " + ISpolicyName + " <-- if not available *******");
        IAFeatureMethod.configure_IS_connector("IScloud");
        IAFeatureMethod.Configure_IA_Automatic_policy_with_Innovation_Studio_API(ISpolicyName);
        log.info("Policy is successfully created and published.");

        long epochSec = System.currentTimeMillis() / 1000;

        String uniqueEventMessage = ISpolicyName + "_" + epochSec;
        log.info("Send one event to the CI used in the service with unique message as : " + uniqueEventMessage);
        bhomFeatureMethods.send_IA_policy_event(cluster_CI, uniqueEventMessage);

        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

        log.info("Move the event to Acknowledged state.");
        String fullMsg = ISpolicyName + "_" + epochSec + "NT_MEMORY Memory used > 100 MB for 0 min";
        bhomFeatureMethods.closeEvent(fullMsg);

        log.info("Switch to IA page.");
        IAFeatureMethod.switchtoIA();

        log.info("Go to IA History page & see if expected IA policies got triggered successfully.");
        IAFeatureMethod.checkIAPolicyInHistoryIsSuccessful(ISpolicyName,"Automatic");
        
    }

    @Test(description = "Create Policy: JSON_Editor: Define action using JSON Editor , add transformation to the JSON fields. https://jira.example.com/browse/DRSQA-19152",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-19152", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_19152_Create_Policy_Enum_Support_in_Json_Editor() throws Exception{

        log.info("------- START: DRSQA_19152_Create_Policy_Enum_Support_in_Json_Editor ------------");
        log.info("Step 1: Login to ADE Platform)");
        doPreconditions();

        String REST_API_Connector_Name = SolQAConstants.REST_API_CONNECTOR_NAME;
        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String Transformation_Name = "morpheus";
        String Transformation_Job = "$.event.msg";

        IAFeatureMethod.connect_GenericRestAPI_Connector();

        log.info("Populate transformer values in lowercase.");
        JSONObject json = new JSONObject();
        json.put(Transformation_Name_Field, Transformation_Name);
        json.put(Transformation_Job_Field, Transformation_Job);

        log.info("Step 2 to Step 8: Create a policy with REST API connector with JSON Editor Enum Support.");
        log.info("Create a policy with REST API connector and add transformation to the JSON fields.");
        IAFeatureMethod.Configure_RESTAPI_policy_with_Editor_Enum_Transformations(REST_API_IA_Policy_Name,REST_API_Connector_Name,json.toString());

        log.info("Step 9: Send Event and Check For Service is not running Mapping");
        log.info("Send event for Service is not running");
        String eventJsonForEnumSupportForServiceDown = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "Monitor" + File.separator + "eventForEnumSupportForHostDown.json";
        bhomFeatureMethods.send_events_with_json(eventJsonForEnumSupportForServiceDown);
        String uniqueEventMessage1 = "service is not running";

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage1);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage1);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Uppercase.");
        IAFeatureMethod.openFirstResultAndCheckJsonField("host is down");

        log.info("Step 10: Send Event and Check For Service is running Mapping");
        log.info("Send event for Service is running");
        String eventJsonForEnumSupportForServiceup = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "Monitor" + File.separator + "eventForEnumSupportForHostUp.json";
        bhomFeatureMethods.send_events_with_json(eventJsonForEnumSupportForServiceup);
        String uniqueEventMessage2 = "service is running";

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage2);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage2);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Uppercase.");
        IAFeatureMethod.openFirstResultAndCheckJsonField("host is up");
    }


    @Test(description = "Create Policy: JSON_Editor: Define action using JSON Editor , add transformation to the JSON fields. https://jira.example.com/browse/DRSQA-19151",groups={"IA_GenericConnector"})
    @SolqaDetails(authorName = "vbakhru",  JiraID = "DRSQA-19151", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_19151_Create_Policy_Enum_Support_in_Variable() throws Exception{

        log.info("------- START: DRSQA_19151_Create_Policy_Enum_Support_in_Variable ------------");
        log.info("Step 1: Login to ADE Platform)");
        doPreconditions();

        String REST_API_Connector_Name = SolQAConstants.REST_API_CONNECTOR_NAME;
        String REST_API_IA_Policy_Name = SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME;
        String Transformation_Name = "input_01";
        String Transformation_Job = "leader";

        IAFeatureMethod.connect_GenericRestAPI_Connector();

        log.info("Populate transformer values in lowercase.");
        JSONObject json = new JSONObject();
        json.put(Transformation_Name_Field, Transformation_Name);
        json.put(Transformation_Job_Field, Transformation_Job);

        log.info("Step 2 to Step 8: Create a policy with REST API connector with Variable Enum Support.");
        log.info("Create a policy with REST API connector and add transformation to the JSON fields.");
        IAFeatureMethod.Configure_RESTAPI_policy_with_Variable_Enum_Transformations(REST_API_IA_Policy_Name,REST_API_Connector_Name,json.toString(),"input_01");

        log.info("Step 9: Send Event and Check For Service is not running Mapping");
        log.info("Send event for Service is not running");
        String eventJsonForEnumSupportForServiceDown = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "Monitor" + File.separator + "eventForEnumSupportForHostDown.json";
        bhomFeatureMethods.send_events_with_json(eventJsonForEnumSupportForServiceDown);
        String uniqueEventMessage1 = "service is not running";

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage1);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage1);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Uppercase.");
        IAFeatureMethod.openFirstResultAndCheckJsonField("host is down");

        log.info("Step 10: Send Event and Check For Service is running Mapping");
        log.info("Send event for Service is running");
        String eventJsonForEnumSupportForServiceup = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "Monitor" + File.separator + "eventForEnumSupportForHostUp.json";
        bhomFeatureMethods.send_events_with_json(eventJsonForEnumSupportForServiceup);
        String uniqueEventMessage2 = "service is running";

        log.info("Go to BHOM -> Events Page and search for the event generated.");
        bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage2);

        log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
        bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage2);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        IAFeatureMethod.VerifyManualPolicyExecutionInHistoryPage(REST_API_IA_Policy_Name, policiesPage.successText);

        log.info("Open the Policy and check that added transformation is present in the JSON field with all letters in Uppercase.");
        IAFeatureMethod.openFirstResultAndCheckJsonField("host is up");
    }

    @AfterMethod(alwaysRun = true)
    public void quitdriver(){
        if (IAFeatureMethod != null){
            IAFeatureMethod.closeBrowser();
        }
        if (bhomFeatureMethods != null){
            bhomFeatureMethods.closeBrowser();
        }
    }

    @AfterClass
    public void teardown() throws Exception{
        doPreconditions();
        log.info("Finally, Delete the created Policy");
        IAFeatureMethod.update_IA_policy_status_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME,"DRAFT");
        IAFeatureMethod.delete_IA_policy_via_api(SolQAConstants.GENERIC_REST_API_IA_POLICY_NAME);

    }
}
