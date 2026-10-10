package ITOM.Bhom;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import util.*;

import java.io.File;
import java.util.Map;

public class BHOMTest extends Launcher{

    private static SolqaLogger log = new SolqaLogger(BHOMTest.class.getName());
    public static String app=SolQAConstants.appITOM;
    CommonWaits commonWaits;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    ReadJson readJson = new ReadJson();
    Map<Object, String> enrichmentTestData;
    BhomFeatureMethods bhomFeatureMethods;

    String PA_AGENT=config.get("PA_AGENT");

    String cluster_CI=config.get("CLUSTER_CI");

    public void doPreconditions() {
        launchWeb(app, app);
        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
        commonWaits=new CommonWaits();
        bhomFeatureMethods = new BhomFeatureMethods();
        }

   @Test(description = "https://jira.example.com/browse/DRSQA-12314 EventCorrelation : Verify event correlation from multiple source (BHOM, TSOM) in Monitor",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare",  TTID="39927", JiraID = "DRSQA-12314", Products = {Product.ADE_OperationsManagement})
    public void DRSQA_12314_verify_event_correlation(ITestContext context) throws Exception{
        doPreconditions();
        String uniqueEventMessage=commonWaits.generateRandomString();
        String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + "Event_Correlation_Policy_DRSQA_12314.json";

        log.info("Create Event Correlation Policy. ");
        //create_correlation_event_policy
        String policyID = bhomFeatureMethods.create_correlation_event_policy(JsonPath,uniqueEventMessage, cluster_CI);

        try {
            log.info("Send 4 events with message: " + uniqueEventMessage);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

            log.info("Go to BHOM -> Events Page and search for the event generated.");
            bhomFeatureMethods.searchForCorrelatedEvent(uniqueEventMessage);

            log.info("Deactivate the event correlation Policy. & try resending the events.");
            //deactivate_event_policy
            bhomFeatureMethods.deactivate_event_policy(policyID);

            log.info("Send 4 events with message: " + uniqueEventMessage);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

            log.info("Go to BHOM -> Events Page and search for the event generated.");
            bhomFeatureMethods.searchForCorrelatedEventPostDeactivation(uniqueEventMessage);

        }
        finally
        {
            bhomFeatureMethods.delete_event_policy(policyID);
        }
    }

    //Disabling this test as This feature will be deprecated in 26.1.00 (refer attached jira DR0F3-32442 )
    @Test(description = "Validate Group publish as a service to AIOPS for PA/third Party Data.  https://jira.example.com/browse/DRSQA-12419",enabled=false)
    @SolqaDetails(authorName = "dmascare",  TTID="39927", JiraID = "DRSQA-12419", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_12419_Validate_Group_Publish_As_Service_To_Aiops(ITestContext context) throws Exception{
        doPreconditions();
        String EventGroupName=commonWaits.generateRandomString();
        String EventGroupJsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + "Event_Group_Creation_DRSQA_12419.json";
        String EntityGroupName=commonWaits.generateRandomString();
        String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + "Group_Creation_DRSQA_12419.json";

        log.info("Create Event Group in a published mode: "+EventGroupJsonPath);
        String eventGroupID = bhomFeatureMethods.create_group(EventGroupJsonPath,EventGroupName,PA_AGENT,"","true");

        log.info("Go to BHOM -> Monitoring -> Groups & check if created group is listed here");
        bhomFeatureMethods.navigateToOperationsManagementPage();
        Assert.assertTrue(bhomFeatureMethods.checkIfEventGroupIsListed(EventGroupName), "Event group created is not present.");

        log.info("Create Entity Group in an unpublished mode: "+EntityGroupName);
        String entityGroupID = bhomFeatureMethods.create_group(JsonPath,EntityGroupName,PA_AGENT,"","false");

        try {
            log.info("Go to BHOM -> Configuration -> Groups & check if created group is in expected state.");
            String unPublishedStatus = bhomFeatureMethods.get_group_published_status(EntityGroupName);
            Assert.assertEquals(unPublishedStatus,"Unpublished","Status is not as expected: " +unPublishedStatus);

            log.info("Publish the same group. ");
            bhomFeatureMethods.update_group(JsonPath, EntityGroupName,PA_AGENT, entityGroupID, "true");
            bhomFeatureMethods.checkForGroupStatusUpdate();

            log.info("Now, Go to BHOM -> Configuration -> Groups & check if created group is in expected state.");
            String publishedStatus = bhomFeatureMethods.get_group_published_status(EntityGroupName);
            Assert.assertEquals(publishedStatus,"Published","Status is not as expected: " +publishedStatus);

            log.info("Send three events to this new entity group.");
            bhomFeatureMethods.send_events(EntityGroupName, PA_AGENT);
            bhomFeatureMethods.send_events(EntityGroupName, PA_AGENT);
            bhomFeatureMethods.send_events(EntityGroupName, PA_AGENT);


            log.info("Go to AIops. Check if service is listed & check if the health-score is less than 100. ");
            int score = bhomFeatureMethods.gotoServicesPageAndRetrieveHealthScore(EntityGroupName);
            Assert.assertTrue(score < 100, "Service is not seen impacted. Health Score is: " + String.valueOf(score));

        }finally {
           bhomFeatureMethods.delete_group(eventGroupID);
           bhomFeatureMethods.delete_group(entityGroupID);
        }
    }

    @Test(description = "Test Basic Enrichment Feature  https://jira.example.com/browse/DRSQA-12824", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12824", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_12824_Test_Basic_Enrichment_Feature(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_12824 Test Basic Enrichment Feature ");

        String jsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"Monitor"+ File.separator+ "EventJsonData.json";
        String eventJson = readJson.readJsonInputArray(jsonContent, "DRSQA_12824_Event");
        enrichmentTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + "Monitor", "Monitor_TestData", "EnrichmentServiceData");
        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12824");

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        log.info("---- send Events with specific event json ----------");
        String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);

        log.info("---- Set advance Filter with event ID ----------");
        bhomFeatureMethods.advancedFilter(event_ID);

        log.info("---- Export event and check expected fields ----------");
        bhomFeatureMethods.exportEventAndVerifyFields(SolQAConstants.PRIORITY.toLowerCase(), enrichmentTestData.get("priority"));

        log.info("---- cleanup: close event ----------");
        bhomFeatureMethods.closeEventsByIdApi(event_ID, enrichmentTestData.get("closureMessage"));

        log.info("-------END : DRSQA_12824 Test Basic Enrichment Feature ");
    }

    @Test(description = "Test Advance Enrichment Feature  https://jira.example.com/browse/DRSQA-12825", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12825", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_12825_Test_Advance_Enrichment_Feature(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_12825 Test Advance Enrichment Feature ");

        String jsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"Monitor"+ File.separator+ "EventJsonData.json";
        String eventJson = readJson.readJsonInputArray(jsonContent, "DRSQA_12825_Event");
        enrichmentTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + "Monitor", "Monitor_TestData", "EnrichmentServiceData");
        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12825");

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        log.info("---- send Events with specific event json ----------");
        String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);

        log.info("---- Set advance Filter with event ID ----------");
        bhomFeatureMethods.advancedFilter(event_ID);

        log.info("---- Export event and check expected fields ----------");
        bhomFeatureMethods.exportEventAndVerifyFields(SolQAConstants.LOCATION_KEY, enrichmentTestData.get("location"));

        log.info("---- cleanup: close event ----------");
        bhomFeatureMethods.closeEventsByIdApi(event_ID, enrichmentTestData.get("closureMessage"));

        log.info("-------END : DRSQA_12825 Test Advance Enrichment Feature ");
    }

    @Test(description = "Test Advance Enrichment Feature using Dynamic Data Enrichment", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12826", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_12826_Test_Advance_Enrichment_Feature_Dynamic_Data(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_12826 Test Advance Enrichment Feature using Dynamic Data Enrichment");

        String jsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"Monitor"+ File.separator+ "EventJsonData.json";
        String eventJson = readJson.readJsonInputArray(jsonContent, "DRSQA_12826_Event");
        enrichmentTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + "Monitor", "Monitor_TestData", "EnrichmentServiceData");
        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12826");

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        log.info("---- send Events with specific event json ----------");
        String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);

        log.info("---- Set advance Filter with event ID ----------");
        bhomFeatureMethods.advancedFilter(event_ID);

        log.info("---- Export event and check expected fields ----------");
        bhomFeatureMethods.exportEventAndVerifyFields(SolQAConstants.EVENT_MSG, enrichmentTestData.get("msg"));

        log.info("---- cleanup: close event ----------");
        bhomFeatureMethods.closeEventsByIdApi(event_ID, enrichmentTestData.get("closureMessage"));

        log.info("-------END : DRSQA_12826 Test Advance Enrichment Feature using Dynamic Data Enrichment");
    }

    @Test(description = "Test Refine Enrichment Feature", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-13031", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_13031_Test_Refine_Enrichment_Feature(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_13031 Test Refine Enrichment Feature ");

        String jsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"Monitor"+ File.separator+ "EventJsonData.json";
        String eventJson = readJson.readJsonInputArray(jsonContent, "DRSQA_13031_Event");
        enrichmentTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + "Monitor", "Monitor_TestData", "EnrichmentServiceData");
        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-13031");

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        log.info("---- send Events with specific event json ----------");
        String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);

        log.info("---- Set advance Filter with event ID ----------");
        bhomFeatureMethods.advancedFilter(event_ID);

        log.info("---- Export event and check expected fields ----------");
        bhomFeatureMethods.exportEventAndVerifyFields(SolQAConstants.PRIORITY.toLowerCase(), enrichmentTestData.get("priority"));

        log.info("---- cleanup: close event ----------");
        bhomFeatureMethods.closeEventsByIdApi(event_ID, enrichmentTestData.get("closureMessage"));

        log.info("-------End : DRSQA_13031 Test Refine Enrichment Feature ");
    }

    @Test(description = "Test Time Based Enrichment Feature", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-13032", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_13032_Test_Time_Based_Enrichment_Feature(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_13032 Test Time Based Enrichment Feature ");

        String jsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Monitor" + File.separator + "EventJsonData.json";
        String eventJson = readJson.readJsonInputArray(jsonContent, "DRSQA_13032_Event");
        enrichmentTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + "Monitor", "Monitor_TestData", "EnrichmentServiceData");
        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-13032");

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("---- send Events with specific event json ----------");
        String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);
        log.info("-----------------wait for 3 minutes to event should reflect in BHOM");
        new CommonWaits().waitForSometime(180000);

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        log.info("---- Set advance Filter with event ID ----------");
        bhomFeatureMethods.advancedFilter(event_ID);

        log.info("---- Export event and check expected fields ----------");
        bhomFeatureMethods.exportEventAndVerifyFields(SolQAConstants.DETAILS.toLowerCase(), enrichmentTestData.get("location"));

        log.info("---- cleanup: close event ----------");
        bhomFeatureMethods.closeEventsByIdApi(event_ID, enrichmentTestData.get("closureMessage"));

        log.info("-------END : DRSQA_13032 Test Time Based Enrichment Feature ");
    }
    
    
    @Test(description = "Verify_Create_Incident_event_operation_on_multiple_selected_events",groups={"Regression"})
    @SolqaDetails(authorName = "dpatare", JiraID = "DRSQA-15767", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_15767_Verify_Create_Incident_event_operation_on_multiple_selected_events(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_15767_Verify_Create_Incident_event_operation_on_multiple_selected_events ");

         Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15767");

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        bhomFeatureMethods.selectMultipleEvent();
        log.info("-------End : DRSQA_15767_Verify_Create_Incident_event_operation_on_multiple_selected_events ");

    }

    
    @Test(description = "Check if events are coming regularly without any delay.",groups={"Regression"})
    @SolqaDetails(authorName = "dpatare", JiraID = "DRSQA-14351", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_14351_Check_if_events_are_coming_regularly_without_any_delay(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_14351_Check if events are coming regularly without any delay.");
        
        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-14351");
         String jsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"Monitor"+ File.separator+ "EventJsonData.json";
          
         log.info("Create Event JSON file path: " + jsonContent);
         String eventJson =  readJson.readJsonInputArray(jsonContent, "DRSQA_14351_Event");
     enrichmentTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + "Monitor", "Monitor_TestData", "EnrichmentServiceData");
       
         String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);
         log.info("Event injection completed successfully"+event_ID);

        BhomFeatureMethods bhomFeatureMethods = new BhomFeatureMethods();
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
       
       
        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        bhomFeatureMethods.advancedFilter(event_ID);
        
        Assert.assertTrue(bhomFeatureMethods.getEventCount()==1,"Found more than 1 Event");
        log.info("-------End : DRSQA_14351_Verify_Create_Incident_event_operation_on_multiple_selected_eventse ");
             
        
       
        
    }

    @Test(description = "Verify groups creation in BHOM for monitoring devices. https://jira.example.com/browse/DRSQA-17685", groups = {"Regression"})
    @SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17685", Products = {Product.ADE_OperationsManagement})
    public void DRSQA_17685_Verify_groups_creation_in_BHOM(ITestContext context) throws Exception {
        log.info("-------Start : DRSQA_17685 Verify groups creation in BHOM for monitoring devices");

        Map<Object, String> testData = readJson.getInputValuesMapIncludingLabelField(
                SolQAConstants.appITOM + File.separator + "AIOPS", "AiopsTestData", "DRSQA_17685_TestData");

        String groupNamePrefix = testData.get("groupNamePrefix");
        String entityFieldName = testData.get("entityFieldName");
        String entityOperator = testData.get("entityOperator");
        String eventFieldName = testData.get("eventFieldName");
        String eventOperator = testData.get("eventOperator");
        String eventClassName = testData.get("eventClassName");

        Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17685");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("---- Navigate to BHOM ----------");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        bhomFeatureMethods = new BhomFeatureMethods();

        log.info("Navigating to Configuration -> Agents to find common hostname substring");
        String entityHostName = bhomFeatureMethods.navigateToAgentsAndGetCommonHostnameSubstring();
        log.info("Common hostname substring from Agents page: " + entityHostName);

        bhomFeatureMethods.navigateToGroups();

        log.info("Creating group with entity and event selection criteria");
        String groupName = bhomFeatureMethods.createGroupWithEntityAndEvent(groupNamePrefix, entityFieldName, entityOperator, entityHostName, eventFieldName, eventOperator, eventClassName);
        log.info("Group created: " + groupName);
        
        String deviceCount = bhomFeatureMethods.getPreviewDevices();
        String eventCount = bhomFeatureMethods.getPreviewEvents();
        log.info("Preview counts - Devices: " + deviceCount + ", Events: " + eventCount);
        
        Assert.assertTrue(bhomFeatureMethods.isNotificationPresent(),
                "Success notification not displayed after creating group");
        String successMsg = bhomFeatureMethods.getNotificationMessage();
        log.info("Group creation message: " + successMsg);
        Assert.assertTrue(successMsg.contains(SolQAConstants.groupSuccessMessage),
                "Expected success message containing '" + SolQAConstants.groupSuccessMessage + "' but got: " + successMsg);
        log.info("Group created and configured successfully: " + groupName);
   
        log.info("Navigating to Monitoring - Groups page to verify group is listed");
        Assert.assertTrue(bhomFeatureMethods.checkIfEventGroupIsListed(groupName),
                "Group '" + groupName + "' is not listed on Monitoring - Groups page");
        log.info("Group '" + groupName + "' verified on Monitoring - Groups page");

        log.info("Opening group to verify details, events and devices against preview counts");
        bhomFeatureMethods.openGroupAndVerifyDetails(groupName, deviceCount, eventCount, entityHostName, eventClassName);
        log.info("Group details verified - device count, event count, entity criteria, event criteria, Events tab and Devices tab validated successfully");

        log.info("Deleting the created group: " + groupName);
        bhomFeatureMethods.searchAndDeleteGroup(groupName);
        log.info("Group deleted successfully: " + groupName);

        log.info("-------End : DRSQA_17685 Verify groups creation in BHOM for monitoring devices");
    }
    
	@Test(description = "Verify creation of suppression event policy in BHOM", groups = { "Regression_P3" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17686", Products = { Product.ADE_OperationsManagement })
	public void DRSQA_17686_Verify_suppression_event_policy_creation(ITestContext context) throws Exception {
		log.info("-------Start : DRSQA_17686 Verify creation of suppression event policy in BHOM");

		Map<Object, String> testData = readJson.getInputValuesMapIncludingLabelField(
				SolQAConstants.appITOM + File.separator + "AIOPS", "AiopsTestData", "DRSQA_17686_TestData");

		String policyNamePrefix = testData.get("policyNamePrefix");
		String eventFieldName = testData.get("eventFieldName");
		String eventOperator = testData.get("eventOperator");
		String eventValue = testData.get("eventValue");
		String policyConfiguration = testData.get("policyConfiguration");
		String eventClass = testData.get("eventClass");
		String eventSeverity = testData.get("eventSeverity");
		String eventLocation = testData.get("eventLocation");
		String eventSourceHostname = testData.get("eventSourceHostname");
		String suppressedEventMsgPrefix = testData.get("suppressedEventMsgPrefix");
		String nonSuppressedEventMsgPrefix = testData.get("nonSuppressedEventMsgPrefix");
		String suppressedEventTag = testData.get("suppressedEventTag");
		String nonSuppressedEventTag = testData.get("nonSuppressedEventTag");

		Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17686");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("---- Navigate to BHOM ----------");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

		bhomFeatureMethods = new BhomFeatureMethods();

		String policyName = "";
		String suppressedEventMsg = "";
		String nonSuppressedEventMsg = "";
		String postDeleteEventMsg = "";
		String jwtToken = "";

		try {
			log.info("Step 1: Navigating to Configuration -> Event Policies");
			bhomFeatureMethods.navigateToEventPolicies();

			log.info("Step 2: Creating suppression event policy");
			policyName = bhomFeatureMethods.createSuppressionEventPolicy(policyNamePrefix, eventFieldName,
					eventOperator, eventValue, policyConfiguration);
			log.info("Event policy created: " + policyName);

			log.info("Step 3: Verifying success notification");
			Assert.assertTrue(bhomFeatureMethods.isEventPolicySuccessToastDisplayed(),
					"Success toast notification not displayed after creating event policy");
			log.info("Success notification verified");

			String randomSuffix1 = bhomFeatureMethods.generateRandomSuffix(5);
			suppressedEventMsg = suppressedEventMsgPrefix + randomSuffix1;
			log.info("Step 4: Sending matching event (should be suppressed): " + suppressedEventMsg);
			bhomFeatureMethods.sendEvent(eventClass, eventSourceHostname, suppressedEventMsg, eventSeverity,
					eventLocation, suppressedEventTag);

			String randomSuffix2 = bhomFeatureMethods.generateRandomSuffix(5);
			nonSuppressedEventMsg = nonSuppressedEventMsgPrefix + randomSuffix2;
			log.info("Step 5: Sending non-matching event (should not be suppressed): " + nonSuppressedEventMsg);
			bhomFeatureMethods.sendEvent(eventClass, eventSourceHostname, nonSuppressedEventMsg, eventSeverity,
					eventLocation, nonSuppressedEventTag);

			log.info("Step 6: Navigating to Events page");
			bhomFeatureMethods.navigateToEventsTab();

			log.info("Step 7: Validating non-suppressed event is visible on events page");
			Assert.assertTrue(bhomFeatureMethods.waitForEventOnEventsPage(nonSuppressedEventMsg),
					"Non-suppressed event should be displayed on events page: " + nonSuppressedEventMsg);

			log.info("Step 8: Validating suppressed event is NOT visible on events page");
			Assert.assertTrue(bhomFeatureMethods.isEventAbsentOnEventsPage(suppressedEventMsg),
					"Suppressed event should NOT be displayed on events page: " + suppressedEventMsg);

			log.info("Step 9: Deleting event policy via API: " + policyName);
			bhomFeatureMethods.searchAndDeleteEventPolicy(policyName);
			policyName = "";
			log.info("Event policy deleted. Waiting for deletion to take effect");
			bhomFeatureMethods.waitForPolicyToActivate();

			String randomSuffix3 = bhomFeatureMethods.generateRandomSuffix(5);
			postDeleteEventMsg = suppressedEventMsgPrefix + randomSuffix3;
			log.info("Step 10: Sending event after policy deletion (should NOT be suppressed): " + postDeleteEventMsg);
			bhomFeatureMethods.sendEvent(eventClass, eventSourceHostname, postDeleteEventMsg, eventSeverity,
					eventLocation, suppressedEventTag);

			log.info("Step 11: Validating event is visible after policy deletion");
			Assert.assertTrue(bhomFeatureMethods.waitForEventOnEventsPage(postDeleteEventMsg),
					"Event should be displayed after policy deletion: " + postDeleteEventMsg);

		} finally {
			if (!policyName.isEmpty()) {
				log.info("Cleaning up: Deleting event policy via API: " + policyName);
				bhomFeatureMethods.searchAndDeleteEventPolicy(policyName);
				log.info("Event policy deleted successfully: " + policyName);
			}
			if (!jwtToken.isEmpty()) {
				log.info("Cleaning up: Closing events via API");
				try {
					if (!nonSuppressedEventMsg.isEmpty()) {
						bhomFeatureMethods.closeEventsByMessageApi(nonSuppressedEventMsg, jwtToken);
					}
					if (!postDeleteEventMsg.isEmpty()) {
						bhomFeatureMethods.closeEventsByMessageApi(postDeleteEventMsg, jwtToken);
					}
				} catch (Exception e) {
					log.info("Error during event cleanup: " + e.getMessage());
				}
			}
		}

		log.info("-------End : DRSQA_17686 Verify creation of suppression event policy in BHOM");
	}

	@Test(description = "Verify creation of notification event policy for email in BHOM", groups = { "Regression_P3" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17689", Products = { Product.ADE_OperationsManagement })
	public void DRSQA_17689_Verify_notification_event_policy_creation(ITestContext context) throws Exception {
		log.info("-------Start : DRSQA_17689 Verify creation of notification event policy in BHOM");

		Map<Object, String> testData = readJson.getInputValuesMapIncludingLabelField(
				SolQAConstants.appITOM + File.separator + "AIOPS", "AiopsTestData", "DRSQA_17689_TestData");

		String policyNamePrefix = testData.get("policyNamePrefix");
		String eventFieldName = testData.get("eventFieldName");
		String eventOperator = testData.get("eventOperator");
		String eventValue = testData.get("eventValue");
		String policyConfiguration = testData.get("policyConfiguration");
		String notificationSendTo = testData.get("notificationSendTo");
		String notificationSubject = testData.get("notificationSubject");
		String notificationMessage = testData.get("notificationMessage");
		String severityOperator = testData.get("severityOperator");
		String severityValue = testData.get("severityValue");
		String eventClass = testData.get("eventClass");
		String eventSeverity = testData.get("eventSeverity");
		String eventLocation = testData.get("eventLocation");
		String eventSourceHostname = testData.get("eventSourceHostname");
		String eventMsgPrefix = testData.get("eventMsgPrefix");
		String eventTag = testData.get("eventTag");
		String mailsacEmail = testData.get("mailsacEmail");
		String mailsacApiKey = testData.get("mailsacApiKey");

		Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17689");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("---- Navigate to BHOM ----------");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

		bhomFeatureMethods = new BhomFeatureMethods();
		bhomFeatureMethods.initMailsacApi(mailsacApiKey);

		String policyName = "";
		String eventMsg = "";

		try {
			log.info("Step 1: Navigating to Configuration -> Event Policies");
			bhomFeatureMethods.navigateToEventPolicies();

			log.info("Step 2: Creating notification event policy");
			policyName = bhomFeatureMethods.createNotificationEventPolicy(policyNamePrefix, eventFieldName,
					eventOperator, eventValue, policyConfiguration, notificationSendTo, notificationSubject,
					notificationMessage, severityOperator, severityValue);
			log.info("Notification event policy created: " + policyName);

			log.info("Step 3: Verifying success notification");
			Assert.assertTrue(bhomFeatureMethods.isEventPolicySuccessToastDisplayed(),
					"Success toast notification not displayed after creating notification event policy");
			log.info("Success notification verified");

			log.info("Waiting for notification policy to activate");
			bhomFeatureMethods.waitForPolicyToActivate();

			String randomSuffix = bhomFeatureMethods.generateRandomSuffix(5);
			eventMsg = eventMsgPrefix + randomSuffix;
			log.info("Step 4: Sending event matching notification policy criteria: " + eventMsg);
			bhomFeatureMethods.sendEvent(eventClass, eventSourceHostname, eventMsg, eventSeverity, eventLocation,
					eventTag);

			log.info("Step 5: Navigating to Monitoring -> Events tab");
			bhomFeatureMethods.navigateToEventsTab();

			log.info("Step 6: Validating event is visible on events page: " + eventMsg);
			Assert.assertTrue(bhomFeatureMethods.waitForEventOnEventsPage(eventMsg),
					"Event should be displayed on events page: " + eventMsg);
			log.info("Event validated successfully on events page");

			log.info("Step 7: Validate notification email sent successfully");
			String expectedSubject = eventMsg;
			String expectedBody = notificationMessage.replace("%msg%", eventMsg);
			log.info("Validating notification email at: " + mailsacEmail);
			log.info("Expected email subject to contain: " + expectedSubject);
			log.info("Expected email body to contain: " + expectedBody);
			Assert.assertTrue(
					bhomFeatureMethods.validateNotificationEmail(mailsacEmail, expectedSubject, expectedBody, 5),
					"Notification email not received or content mismatch at " + mailsacEmail);
			log.info("Notification email validated successfully");
		} finally {
			if (!policyName.isEmpty()) {
				log.info("Cleaning up: Deleting event policy via API: " + policyName);
				try {
					bhomFeatureMethods.searchAndDeleteEventPolicy(policyName);
					log.info("Event policy deleted successfully: " + policyName);
				} catch (Exception e) {
					log.error("Failed to delete event policy: " + policyName + " - " + e.getMessage());
				}
			}
			if (!eventMsg.isEmpty()) {
				log.info("Cleaning up: Closing event via API: " + eventMsg);
				try {
					bhomFeatureMethods.closeEventsByMessageApi(eventMsg);
					log.info("Event closed successfully: " + eventMsg);
				} catch (Exception e) {
					log.error("Failed to close event: " + eventMsg + " - " + e.getMessage());
				}
			}
			log.info("Cleaning up: Deleting emails from mailsac: " + mailsacEmail);
			try {
				bhomFeatureMethods.deleteMailsacMessages(mailsacEmail);
				log.info("Mailsac emails deleted successfully");
			} catch (Exception e) {
				log.error("Failed to delete mailsac emails: " + e.getMessage());
			}
		}
		log.info("-------End : DRSQA_17689 Verify creation of notification event policy in BHOM");
	}

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (bhomFeatureMethods != null) {
            bhomFeatureMethods.closeBrowser();
        }
    }

}
