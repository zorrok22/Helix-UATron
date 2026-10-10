package ITOM.BHOM_AIOps;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import ITOM.Sanity_Prerequisites;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Discovery.DiscoveryFeatureMethods;
import pages.ITOM.Itsm_Insights.ITSMInsightsFeatureMethods;
import util.*;

public class BHOM_AIOps extends Launcher {

    static SolqaLogger log = new SolqaLogger(BHOM_AIOps.class.getName());
    Portal_FeatureMethods portalFeatureMethods;
    BhomFeatureMethods bhomFeatureMethods;
    AIOpsFeatureMethods aiopsFeatureMethods;
    DiscoveryFeatureMethods discoveryFeatureMethods;
    ITSMInsightsFeatureMethods itsmInsightsFeatureMethods;
    AdePortalLandingPage adeportallandingpage;
    Map<Object, String> aiopsTestData, PortalTestData;
    ReadJson readJson = new ReadJson();
    Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private Map<Object, String> itomUserData;
    private String userKey = "aananda"; // Default user key
    WebDriver ITOMDriver;

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(configProp);
        userKey = "itomadminuser";
        log.info("BHOM_AIOps will use user: " + itomUserData.get("UserName"));
    }

    @Test(description = "Blackout Only New Events and restore last status of event on blackout Expiry", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12815", Products = {Product.Helix_DWP})
    public void DRSQA_12815_Blackout_Only_New_Events_and_restore_last_status_of_event_on_blackout_Expiry(ITestContext context) throws IOException {
        String hostName = "host-18";
        String serviceName = "Citrix";

        // Step 1: Login to ADE portal
        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12815");
        bhomFeatureMethods = new BhomFeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        
        // Use credentials from @BeforeTest setup
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Login to ADE Platform with " + username);
        portalFeatureMethods.loginToADEPlatform(username, password);

        // Step 2: Cross launch BHOM
        log.info("Cross launching BHOM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        // Step 3: Close existing blackout events if any
        bhomFeatureMethods.closeAllBlackoutEvents();

        // Step 4: Close any existing events for the host
        log.info("Closing any existing events for service: " + serviceName);
        bhomFeatureMethods.closeAllEventsForService(serviceName);

        // Step 5: Create blackout policy
        log.info("Creating blackout policy for host: " + hostName);
        String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateBlackoutPolicy.json";
        String policyName = "DRSQA_12815_Policy_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.createBlackoutPolicy(JsonContent, hostName, policyName, "false", "true");

        // Step 6: Create blackout policy events using API
        bhomFeatureMethods.waitForPolicyToActivate();
        log.info("Creating blackout policy events for host: " + hostName);
        JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateOpenEvents.json";
        String msgForEvent = "DRSQA_12815_Event_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.injectEventForBlackoutPolicy(JsonContent, hostName, msgForEvent);

        // Step 7: Verify events are in blackout state
        log.info("Verifying events are in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent, "Blackout");

        // Step 8: Check health score in AIOps
        log.info("Checking health score in AIOps");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
        aiopsFeatureMethods.openService(serviceName);
        Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 100, "Health score does not match expected value");

        // Step 9: Disable blackout policy
        log.info("Disabling blackout policy");
        bhomFeatureMethods.disableBlackoutPolicy(policyName);

        // Step 10: Verify events still in blackout state
        log.info("Verifying events still in blackout state after policy disable");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent, "Open");

        // Step 11: Check health score in AIOps again
        log.info("Checking health score in AIOps after policy disable");
        portalFeatureMethods.navigateToTab(SolQAConstants.AIOPS);
        Assert.assertTrue(aiopsFeatureMethods.getHealthScore() < 100, "Health score should be less than 100 after restoring last status of events");

        // Step 12: Close the browser
        log.info("Closing the browser");
        portalFeatureMethods.closeBrowser();
    }

    @Test(description = "Blackout Existing and New Events and restore last status of event on blackout expiry", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12816", Products = {Product.Helix_DWP})
    public void DRSQA_12816_Blackout_Existing_and_New_Events_and_restore_last_status_of_event_on_blackout_expiry(ITestContext context) throws IOException {
        String hostName = "host-18";
        String serviceName = "Citrix";

        // Step 1: Login to ADE portal
        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12816");
        bhomFeatureMethods = new BhomFeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        
        // Use credentials from @BeforeTest setup
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Login to ADE Platform with " + username);
        portalFeatureMethods.loginToADEPlatform(username, password);

        // Step 2: Cross launch BHOM
        log.info("Cross launching BHOM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        // Step 3: Close existing blackout events if any
        bhomFeatureMethods.closeAllBlackoutEvents();

        // Step 4: Close any existing events for the host
        log.info("Closing any existing events for service: " + serviceName);
        bhomFeatureMethods.closeAllEventsForService(serviceName);

        // Step 5: Create blackout policy events using API
        log.info("Creating blackout policy events for host: " + hostName);
        String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateOpenEvents.json";
        String msgForEvent1 = "DRSQA_12816_Event1_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.injectEventForBlackoutPolicy(JsonContent, hostName, msgForEvent1);

        // Step 6: Verify events are created
        log.info("Verifying events are created for host: " + hostName);
        bhomFeatureMethods.verifyOpenEventsCreatedForHost(msgForEvent1);

        // Step 7: Create blackout policy
        log.info("Creating blackout policy for host: " + hostName);
        JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateBlackoutPolicy.json";
        String policyName = "DRSQA_12816_Policy_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.createBlackoutPolicy(JsonContent, hostName, policyName, "true", "true");

        // Step 8: Verify events are in blackout state
        log.info("Verifying events are in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent1, "Blackout");

        //step 9: create another events while blackout policy is active
        log.info("Creating additional events while blackout policy is active");
        JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateOpenEvents.json";
        String msgForEvent2 = "DRSQA_12816_Event2_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.injectEventForBlackoutPolicy(JsonContent, hostName, msgForEvent2);

        // Step 10: Verify additional events are also in blackout state
        log.info("Verifying additional events are also in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent2, "Blackout");

        // Step 11: Check health score in AIOps
        log.info("Checking health score in AIOps");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
        aiopsFeatureMethods.openService(serviceName);
        Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 100, "Health score does not match expected value");

        // Step 12: Disable blackout policy
        log.info("Disabling blackout policy");
        bhomFeatureMethods.disableBlackoutPolicy(policyName);

        // Step 13: Verify events in open state
        log.info("Verifying events still in blackout state after policy disable");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent1, "Open");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent2, "Open");

        // Step 14: Check health score in AIOps again
        log.info("Checking health score in AIOps after policy disable");
        portalFeatureMethods.navigateToTab(SolQAConstants.AIOPS);
        Assert.assertTrue(aiopsFeatureMethods.getHealthScore() < 100, "Health score should be less than 100 after restoring last status of events");

        // Step 15: Close the browser
        log.info("Closing the browser");
        portalFeatureMethods.closeBrowser();
    }

    @Test(description = "Blackout Existing and New Events and Keep them in blackout status on blackout expiry", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12819", Products = {Product.Helix_DWP})
    public void DRSQA_12819_Blackout_Existing_and_New_Events_and_Keep_them_in_blackout_status_on_blackout_expiry(ITestContext context) throws IOException {
        String hostName = "host-18";
        String serviceName = "Citrix";

        // Step 1: Login to ADE portal
        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12819");
        bhomFeatureMethods = new BhomFeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        
        // Use credentials from @BeforeTest setup
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Login to ADE Platform with " + username);
        portalFeatureMethods.loginToADEPlatform(username, password);

        // Step 2: Cross launch BHOM
        log.info("Cross launching BHOM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        // Step 3: Close existing blackout events if any
        bhomFeatureMethods.closeAllBlackoutEvents();

        // Step 4: Close any existing events for the host
        log.info("Closing any existing events for service: " + serviceName);
        bhomFeatureMethods.closeAllEventsForService(serviceName);

        // Step 5: Create blackout policy events using API
        log.info("Creating blackout policy events for host: " + hostName);
        String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateOpenEvents.json";
        String msgForEvent1 = "DRSQA_12819_Event1_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.injectEventForBlackoutPolicy(JsonContent, hostName, msgForEvent1);

        // Step 6: Verify events are created
        log.info("Verifying events are created for host: " + hostName);
        bhomFeatureMethods.verifyOpenEventsCreatedForHost(msgForEvent1);

        // Step 7: Create blackout policy
        log.info("Creating blackout policy for host: " + hostName);
        JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateBlackoutPolicy.json";
        String policyName = "DRSQA_12819_Policy_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.createBlackoutPolicy(JsonContent, hostName, policyName, "true", "false");

        // Step 8: Verify events are in blackout state
        log.info("Verifying events are in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent1, "Blackout");

        //step 9: create another events while blackout policy is active
        log.info("Creating additional events while blackout policy is active");
        JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateOpenEvents.json";
        String msgForEvent2 = "DRSQA_12819_Event2_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.injectEventForBlackoutPolicy(JsonContent, hostName, msgForEvent2);

        // Step 10: Verify additional events are also in blackout state
        log.info("Verifying additional events are also in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent2, "Blackout");

        // Step 11: Check health score in AIOps
        log.info("Checking health score in AIOps");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
        aiopsFeatureMethods.openService(serviceName);
        Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 100, "Health score does not match expected value");

        // Step 12: Disable blackout policy
        log.info("Disabling blackout policy");
        bhomFeatureMethods.disableBlackoutPolicy(policyName);

        // Step 13: Verify events still in blackout state
        log.info("Verifying events still in blackout state after policy disable");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent1, "Blackout");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent2, "Blackout");

        // Step 14: Check health score in AIOps again
        log.info("Checking health score in AIOps after policy disable");
        portalFeatureMethods.navigateToTab(SolQAConstants.AIOPS);
        Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 100, "Health score does not match expected value");

        // Step 15: Close the browser
        log.info("Closing the browser");
        portalFeatureMethods.closeBrowser();
    }

    @Test(description = "Blackout Only New Events and Keep them in blackout status on blackout expiry", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12820", Products = {Product.Helix_DWP})
    public void DRSQA_12820_Blackout_Only_new_Events_and_Keep_them_in_blackout_status_on_blackout_expiry(ITestContext context) throws IOException {
        String hostName = "host-18";
        String serviceName = "Citrix";

        // Step 1: Login to ADE portal
        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12820");
        bhomFeatureMethods = new BhomFeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        
        // Use credentials from @BeforeTest setup
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Login to ADE Platform with " + username);
        portalFeatureMethods.loginToADEPlatform(username, password);

        // Step 2: Cross launch BHOM
        log.info("Cross launching BHOM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        // Step 3: Close existing blackout events if any
        bhomFeatureMethods.closeAllBlackoutEvents();

        // Step 4: Close any existing events for the host
        log.info("Closing any existing events for service: " + serviceName);
        bhomFeatureMethods.closeAllEventsForService(serviceName);

        // Step 5: Create blackout policy
        log.info("Creating blackout policy for host: " + hostName);
        String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateBlackoutPolicy.json";
        String policyName = "DRSQA_12820_Policy_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.createBlackoutPolicy(JsonContent, hostName, policyName, "true", "false");

        // Step 6: Create blackout policy events using API
        log.info("Creating blackout policy events for host: " + hostName);
        JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "CreateOpenEvents.json";
        String msgForEvent = "DRSQA_12820_Event_" + RandomStringUtils.randomAlphanumeric(10);
        bhomFeatureMethods.injectEventForBlackoutPolicy(JsonContent, hostName, msgForEvent);

        // Step 7: Verify events are in blackout state
        log.info("Verifying events are in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent, "Blackout");

        // Step 8: Check health score in AIOps
        log.info("Checking health score in AIOps");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
        aiopsFeatureMethods.openService(serviceName);
        Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 100, "Health score does not match expected value");

        // Step 9: Disable blackout policy
        log.info("Disabling blackout policy");
        bhomFeatureMethods.disableBlackoutPolicy(policyName);

        // Step 10: Verify events are in blackout state
        log.info("Verifying events are in blackout state");
        bhomFeatureMethods.verifyEventsStatus(msgForEvent, "Blackout");

        // Step 11: Check health score in AIOps again
        log.info("Checking health score in AIOps after policy disable");
        portalFeatureMethods.navigateToTab(SolQAConstants.AIOPS);
        Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 100, "Health score does not match expected value");

        // Step 12: Close the browser
        log.info("Closing the browser");
        portalFeatureMethods.closeBrowser();
    }

    @Test(description = "AIOps : Verify AIOps functionality with data integration from multiple source as input", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.dataCreationForDRSQA_12355"},
            groups = {"sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12355", Products = {Product.AiOPS, Product.Operations_Management})
    public void DRSQA_12355_Verify_AIOps_Functionality_With_Data_Integration_From_Multiple_Source_As_Input(ITestContext context) {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("---- Login to Portal with correct username and password----------");
        ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(ITOMDriver);
        context.setAttribute("DRSQA_12355_Driver", ITOMDriver);
        context.setAttribute("DRSQA_12355_Product", Product.ADE_Platform);
        portalFeatureMethods = new Portal_FeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        bhomFeatureMethods = new BhomFeatureMethods();
        discoveryFeatureMethods = new DiscoveryFeatureMethods();

        log.info("Login to ADE Platform with " + username);
        login(username, password, SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Cross launch to AIOps application");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);

        log.info("Navigate to Services tab");
        aiopsFeatureMethods.navigateToServicesTab();


        aiopsFeatureMethods.openService(Sanity_Prerequisites.DRSQA_12355_ServiceName);
        Assert.assertEquals(aiopsFeatureMethods.getTotalEventsCount(), 3, "Events not came");
        log.info("Verify service is impacted and health score is updated");
        aiopsFeatureMethods.verifyServiceImpactedAndHealthScoreUpdated(Sanity_Prerequisites.DRSQA_12355_ServiceName);

        log.info("Create K8s service from IA and verify the service is created in AIOps");
        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.openService(Sanity_Prerequisites.DRSQA_12355_k8sServiceName);

        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.deleteServiceUsingUI(Sanity_Prerequisites.DRSQA_12355_ServiceName);

        aiopsFeatureMethods.navigateToServicesTab();
        discoveryFeatureMethods.navigateToDiscoveryFromApplicationSwitcher();
        discoveryFeatureMethods.delete_service_model(Sanity_Prerequisites.DRSQA_12355_k8sServiceName);
    }

    @Test(description = "AIOps: Validate the available Event actions for an Event", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.sanityPrerequisites"}, groups = {"monitor", "aiops"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12425", Products = {Product.Service_Monitoring, Product.ADE_OperationsManagement})
    public void DRSQA_12425_Validate_the_available_Event_actions_for_an_Event(ITestContext context) {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("------- START: DRSQA-12425 AIOps: Validate the available Event actions for an Event ------------");
        ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(ITOMDriver);
        context.setAttribute("DRSQA_12425_Driver", ITOMDriver);
        context.setAttribute("DRSQA_12425_Product", Product.ADE_Platform);
        portalFeatureMethods = new Portal_FeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        bhomFeatureMethods = new BhomFeatureMethods();

        log.info("Login to ADE Platform with " + username);
        login(username, password, SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Cross launch to AIOps application");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);

        String ciName = SolQAConstants.CLUSTER_CI;
        String DRSQA_12425_ServiceName = "DRSQA_12425_Service"+ "_" + RandomStringUtils.randomAlphanumeric(5);
        log.info("Creating service: " + DRSQA_12425_ServiceName + " for CI: " + ciName);
        aiopsFeatureMethods.createServiceUsingUI(DRSQA_12425_ServiceName, ciName);

        log.info("Sending 3 events for for CI: " + ciName);
        bhomFeatureMethods.send_events("event 1", ciName);
        bhomFeatureMethods.send_events("event 2", ciName);
        bhomFeatureMethods.send_events("event 3", ciName);

        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
        log.info("Navigate to Services tab");
        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.openService(DRSQA_12425_ServiceName);
        Assert.assertEquals(aiopsFeatureMethods.getTotalEventsCount(), 3, "Events not came");

        log.info("Navigate back to AIOps application and open the service to check if it is impacted");
        aiopsFeatureMethods.verifyServiceImpactedAndHealthScoreUpdated(DRSQA_12425_ServiceName);
        ReadJson readJson = new ReadJson();
        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsService_TestData", "ServiceData");
        String situation = aiopsTestData.get("situationName");

        log.info("------------- verify all event Options");
        aiopsFeatureMethods.verifyEventOptionsForFirstEvent();
        aiopsFeatureMethods.captureScreenshot("12425_EventDetails", SolQAConstants.appITOM);

        log.info("------------- Navigate to Situation page explicitly and verify situation options ---->");
        aiopsFeatureMethods.navigateToSituationPageAndVerifySituationOption(situation);
        aiopsFeatureMethods.captureScreenshot("12425_situationDetails", SolQAConstants.appITOM);

        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.deleteServiceUsingUI(DRSQA_12425_ServiceName);

        log.info("------- END: DRSQA-12425 AIOps: Validate the available Event actions for an Event ------------");
    }

    @Test(description = "[Aiops]Validate The Event Details Cross launch to BHOM", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.dataCreationForDRSQA_12426"},
            groups = {"sanity_monitor", "sanity_aiops", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12426", Products = {Product.Service_Monitoring, Product.ADE_OperationsManagement})
    public void DRSQA_12426_Validate_Event_Details_Cross_launch_to_BHOM(ITestContext context) {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("------- START: DRSQA_12426 AIOps: Validate The Event Details Crosslaunch to BHOM ------------");
        ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(ITOMDriver);
        context.setAttribute("DRSQA_12426_Driver", ITOMDriver);
        context.setAttribute("DRSQA_12426_Product", Product.ADE_Platform);
        portalFeatureMethods = new Portal_FeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        bhomFeatureMethods = new BhomFeatureMethods();

        log.info("Login to ADE Platform with " + username);
        login(username, password, SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Cross launch to AIOps application");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);

        log.info("Navigate to Services tab");
        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.openService(Sanity_Prerequisites.DRSQA_12426_ServiceName);
        Assert.assertEquals(aiopsFeatureMethods.getTotalEventsCount(), 3, "Events not came");

        aiopsFeatureMethods.captureScreenshot("12426_checkServiceImpactedOrNot", SolQAConstants.appITOM);
        log.info("Verify service is impacted and health score is updated");
        aiopsFeatureMethods.verifyServiceImpactedAndHealthScoreUpdated(Sanity_Prerequisites.DRSQA_12426_ServiceName);

        log.info("------------- Open Event and check all required data is present and cross launch event in BHOM application and check event details --------> ");
        String eventName = aiopsFeatureMethods.crossLaunchImpactingEvent();
        bhomFeatureMethods.verifyEventEditDetails(eventName);
        aiopsFeatureMethods.captureScreenshot("12426_BHOMPageEventDetails", SolQAConstants.appITOM);

        switchToTabByTitle(SolQAConstants.AIOPS);
        aiopsFeatureMethods.refreshBrowser();
        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.deleteServiceUsingUI(Sanity_Prerequisites.DRSQA_12426_ServiceName);
        log.info("------- END: DRSQA-12426 AIOps: Validate The Event Details Cross launch to BHOM ------------");
    }

    @Test(description = "[NextGen]: Validate the impact on Service when health indicators are updated for a service.", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.dataCreationForDRSQA_12454"},
            groups = {"sanity_monitor", "sanity_aiops", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12454", Products = {Product.Service_Monitoring, Product.ADE_OperationsManagement})
    public void DRSQA_12454_Validate_impact_on_service_when_health_Indicators_are_Updated(ITestContext context) {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(ITOMDriver);
        context.setAttribute("DRSQA_12454_Driver", ITOMDriver);
        context.setAttribute("DRSQA_12454_Product", Product.ADE_Platform);

        log.info("------- START: DRSQA_12454 [NextGen]: Validate the impact on Service when health indicators are updated for a service. ------------");
        portalFeatureMethods = new Portal_FeatureMethods();
        bhomFeatureMethods = new BhomFeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();

        log.info("Login to ADE Platform with " + username);
        login(username, password, SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITOM + File.separator + "AIOPS", "AiopsTestData", "DRSQA_12454_TestData");

        log.info("Cross launch to BHOM application");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);

        log.info("Navigate to Alarm Policies page");
        bhomFeatureMethods.navigateToAlarmPolicies();
        String policyName = aiopsTestData.get("policyName");

        bhomFeatureMethods.createAlarmPolicy(aiopsTestData);
        log.info("Alarm policy created is: " + policyName);

        try {
            log.info("Navigate to AIOps application");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);

            log.info("Navigate to Services tab");
            aiopsFeatureMethods.navigateToServicesTab();

            aiopsFeatureMethods.openService(Sanity_Prerequisites.DRSQA_12454_ServiceName);
            log.info("Verify service is impacted and health score is updated");
            Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 82, "Expected health score is  incorrect");

            log.info("------------- Add health indicators and verify the health score is updated --------> ");
            aiopsFeatureMethods.addHealthIndicatorToService(aiopsTestData.get("metricName1"), aiopsTestData.get("metricType"));
            Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 72, "Expected health score is  incorrect");

            aiopsFeatureMethods.addHealthIndicatorToService(aiopsTestData.get("metricName2"), aiopsTestData.get("metricType"));
            Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 64, "Expected health score is  incorrect");

            log.info("------------- Delete health indicators and verify the health score is updated --------> ");
            aiopsFeatureMethods.deleteHealthIndicatorToService("ProcessUtilization");
            aiopsFeatureMethods.deleteHealthIndicatorToService("MemoryUsage");

            log.info("Verify health score is back to original score");
            Assert.assertEquals(aiopsFeatureMethods.getHealthScore(), 82, "Expected health score is  incorrect");
        } finally {
            log.info("Alarm policy is left as is");
        }
        aiopsFeatureMethods.navigateToServicesTab();
        aiopsFeatureMethods.deleteServiceUsingUI(Sanity_Prerequisites.DRSQA_12454_ServiceName);
        log.info("------- END: DRSQA_12454 [NextGen]: Validate the impact on Service when health indicators are updated for a service. ------------");
    }

    @Test(description = "DRSQA-13433 Validate Impact of Event related to SMTP mailbox on Customer Service Model.  https://jira.example.com/browse/DRSQA-13433",groups={"Regression"})
    @SolqaDetails(authorName = "dpatare", JiraID = "DRSQA-13433", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_13433_Validate_Impact_of_Event_related_to_SMTP_mailbox_on_Customer_Service_Model(ITestContext context) throws Exception {
    	
    	 log.info("-------Start : DRSQA-13433 Validate Impact of Event related to SMTP mailbox on Customer Service Model ");
    	  Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-13433");
          aiopsFeatureMethods = new AIOpsFeatureMethods();
        
          portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
          
          log.info("Navigate to AIOPS");
          portalFeatureMethods.navigateToPortalTab();
          portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
          Map<Object, String> drsqa__13433_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"AIOPS", "AiopsTestData", "DRSQA_13433_serviceName");
          log.info("---- Verify " + drsqa__13433_data.get("serviceName") + " Test data ----");
     
          String serviceName = drsqa__13433_data.get("serviceName");
          aiopsFeatureMethods.navigateToServicesTab();
          log.info("Navigate to Service Tab");
          aiopsFeatureMethods.openService(serviceName);
          log.info("Check Service Health score");
         int helthScore = aiopsFeatureMethods.getHealthScore();      
                  
          String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"AIOPS"+ File.separator+ "DRSQA_13433_Data.json";
                
          log.info("Create Event JSON file path: " + JsonContent);
          String eventJson = readJson.readJsonInputArray(JsonContent, "DRSQA_13433_Event");
          String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);
          log.info("Event injection completed successfully"+event_ID);
          
          log.info("Verify Health scrore before is greater than After Event Inject");          
          
          Assert.assertTrue(aiopsFeatureMethods.getHealthScore()<helthScore, "Health score value not get reduced as per expected");

          log.info("-------END : DRSQA-13433 Validate Impact of Event related to SMTP mailbox on Customer Service Model ");
      	
    }

    @Test(description = "DRSQA-13267 Validate Impact of Event related to Elastic Search service on Customer Service Model. https://jira.example.com/browse/DRSQA-13267",groups={"Regression"})
    @SolqaDetails(authorName = "dpatare", JiraID = "DRSQA-13267", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_13267_Validate_Impact_of_Event_related_to_Elastic_Search_service_on_Customer_Service_Model(ITestContext context) throws Exception {
    		 log.info("-------Start : DRSQA-13267 Validate Impact of Event related to Elastic Search service on Customer Service Model ");
   	  Portal_FeatureMethods portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-13267");
         aiopsFeatureMethods = new AIOpsFeatureMethods();
       
         portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));         
         
         log.info("Checking health score in AIOps");
         portalFeatureMethods.navigateToPortalTab();
         portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
         Map<Object, String> drsqa__13267_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"AIOPS", "AiopsTestData", "DRSQA_13267_serviceName");
         log.info("---- Verify " + drsqa__13267_data.get("serviceName") + " Test data ----");
    
         String serviceName = drsqa__13267_data.get("serviceName");
         aiopsFeatureMethods.navigateToServicesTab();
         log.info("Navigate to Service Tab");
         aiopsFeatureMethods.openService(serviceName);
         log.info("Check Service Health score");
        int helthScore = aiopsFeatureMethods.getHealthScore();      
                 
         String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator +"AIOPS"+ File.separator+ "DRSQA_13267_Data.json";
               
         log.info("Create Event JSON file path: " + JsonContent);
         String eventJson = readJson.readJsonInputArray(JsonContent, "DRSQA_13267_Event");
         String event_ID = portalFeatureMethods.injectEventTicketUsingJasonContent(eventJson);
         log.info("Event injection completed successfully"+event_ID);
         
         log.info("Verify Health scrore before is greater than After Event Inject");          
         
         Assert.assertTrue(((aiopsFeatureMethods.getHealthScore()<helthScore)||aiopsFeatureMethods.getHealthScore()==0), "Health score value not get reduced as per expected");

         log.info("-------END : DRSQA-13267  Validate Impact of Event related to Elastic Search service on Customer Service Model");
    }

    @Test(description = "RBAC - Enforce authorization profiles when viewing Devices in BHOM UI when RBAC is enabled", groups={"Regression","BHOM"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-18979", Products = {Product.ADE_OperationsManagement,Product.AiOPS})
    public void DRSQA_18979_RBAC_Enforce_authorization_profiles_when_viewing_Devices_in_BHOM_UI_when_RBAC_is_enabled(ITestContext context) throws IOException {
       log.info("-------START : RBAC - Enforce authorization profiles when viewing Devices in BHOM UI when RBAC is enabled");
       log.info("--Precondition:-AIOps tenant is available; service_access_control_feature_enabled - feature flag should be enabled:- Assuming it is enabled");
       log.info("--Precondition2-Multiple devices are available in BHOM- Assuming there are at least 4 devices");
       log.info("--Precondition3-- In Helix Portal > Create a Group");
       portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18979");
       PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"AIOPS", "AiopsTestData", "DRSQA_18979_TestData");
       portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
       context.setAttribute("DRSQA_18979_Driver", getDriver());
       context.setAttribute("DRSQA_18979_Product", Product.Helix_Portal);
       adeportallandingpage = new AdePortalLandingPage(getDriver());
       adeportallandingpage.verifyLoginToADEPlatform();
       String groupName = PortalTestData.get("GroupName") +RandomStringUtils.randomAlphabetic(3);
       log.info("Creating new user group: " + groupName);
       bhomFeatureMethods=new BhomFeatureMethods();
       bhomFeatureMethods.createNewUserGroup(groupName, PortalTestData.get("UserName"));
       getDriver().navigate().refresh();
       commonWaits.waitForExecutionComplete();
       String roleName = PortalTestData.get("RoleName") + RandomStringUtils.randomAlphabetic(3);
       log.info("Adding new role: " + roleName);
       bhomFeatureMethods.addNewRole(roleName);
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName1"), PortalTestData.get("SubExpandName1"), PortalTestData.get("SubCollapseName1"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName2"), PortalTestData.get("SubExpandName2"), PortalTestData.get("SubCollapseName2"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName3"), PortalTestData.get("SubExpandName3"), PortalTestData.get("SubCollapseName3"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName4"), PortalTestData.get("SubExpandName4"), PortalTestData.get("SubCollapseName4"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName5"), PortalTestData.get("SubExpandName5"), PortalTestData.get("SubCollapseName5"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName6"), PortalTestData.get("SubExpandName6"), PortalTestData.get("SubCollapseName6"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName7"), PortalTestData.get("SubExpandName7"), PortalTestData.get("SubCollapseName7"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName8"), PortalTestData.get("SubExpandName8"), PortalTestData.get("SubCollapseName8"));
       bhomFeatureMethods.addPermissionToRole(PortalTestData.get("PermissionName9"), PortalTestData.get("SubExpandName9"), PortalTestData.get("SubCollapseName9"));
       bhomFeatureMethods.clickOnSelectButton(PortalTestData.get("Groups"));
       bhomFeatureMethods.addGroupToRole(groupName);
       getDriver().navigate().refresh();
       bhomFeatureMethods.removeAllGroupsFromUser(PortalTestData.get("UserName"));
       bhomFeatureMethods.clickOnSelectButton(PortalTestData.get("Groups"));
       bhomFeatureMethods.addGroupToRole(groupName);
       getDriver().navigate().refresh();

       log.info("Navigating to BHOM application");
       bhomFeatureMethods.navigateToHomePage();
       portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
       bhomFeatureMethods = new BhomFeatureMethods();
       bhomFeatureMethods.verifyLogin();
       String authProfileName = PortalTestData.get("AuthProfileName")+RandomStringUtils.randomAlphabetic(3);
       log.info("Creating Authorization Profile: " + authProfileName);
       bhomFeatureMethods.navigateToAuthProfile();
       bhomFeatureMethods.createAuthProfile(authProfileName);
       bhomFeatureMethods.addAssociatedUserGroup(groupName);
       bhomFeatureMethods.switchToObjectsTab();
       bhomFeatureMethods.switchToDevicesTab();
       bhomFeatureMethods.addFirstFourDevices();
       List<String> addedDevices = bhomFeatureMethods.getNameOfAddedDevices();
       String device1 = addedDevices.get(0);
       String device2 = addedDevices.get(1);
       String device3 = addedDevices.get(2);
       String device4 = addedDevices.get(3);
       bhomFeatureMethods.saveAuthProfile();
       log.info("Precondition is completed successfully");

       log.info("Step1:- Login to BHOM with restricted user i.e. apavlik");
       portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18979");
       PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"AIOPS", "AiopsTestData", "DRSQA_18979_TestData");
       portalFeatureMethods.loginToADEPlatform(PortalTestData.get("RestrictedUser"), PortalTestData.get("Password"));
       context.setAttribute("DRSQA_18979_Driver", getDriver());
       context.setAttribute("DRSQA_18979_Product", Product.Helix_Portal);
       adeportallandingpage = new AdePortalLandingPage(getDriver());
       adeportallandingpage.verifyLoginToADEPlatform();
       portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
       bhomFeatureMethods = new BhomFeatureMethods();
       bhomFeatureMethods.verifyLogin();

       log.info("Step2:- Apply all filters if any and check available events");
       log.info("Step3:- Navigate to Devices overview");
       bhomFeatureMethods.navigateToDevices();

       log.info("Step4:- Verify the devices are displayed with the group name");
       String allDevicesCount = bhomFeatureMethods.getAllDevicesCount();
       Assert.assertEquals(allDevicesCount, "4", "All Devices count is not 4");
      
       log.info("Step5:- Open any device");
       bhomFeatureMethods.selectEventFiltersBasedOnCount(PortalTestData.get("DevicesStatus"));
       List<String> restrictedDevices =bhomFeatureMethods.getNameOfAddedDevicesInRestrictedUser();
       Assert.assertTrue(restrictedDevices.containsAll(addedDevices)&& addedDevices.containsAll(restrictedDevices),"Device names are not matching. Expected: "+addedDevices+", Actual: "+restrictedDevices);
       bhomFeatureMethods.openDevice(device1);
       bhomFeatureMethods.closeBrowser();

       log.info("Step6:- Now Login to BHOM with Admin user");
       portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18979");
       PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"AIOPS", "AiopsTestData", "DRSQA_18979_TestData");
       portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
       setDriver(getDriver());
       context.setAttribute("DRSQA_18979_Driver", getDriver());
       context.setAttribute("DRSQA_18979_Product", Product.Helix_Portal);
       adeportallandingpage = new AdePortalLandingPage(getDriver());
       adeportallandingpage.verifyLoginToADEPlatform();
       portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
       bhomFeatureMethods = new BhomFeatureMethods();
       bhomFeatureMethods.verifyLogin();

       log.info("Step7:- Apply all filters and check available events");
       log.info("Step8:- Navigate to Devices overview");
       bhomFeatureMethods.navigateToDevices();

       log.info("Step9:- Apply all filters and check available Devices");
       String adminAllDevicesCount = bhomFeatureMethods.getAllDevicesCount();
      
       log.info("Step10 and 11:- Open Any Device which restricted user does not have access eg. Device 1");
       log.info("Step11:- Copy Device 1 Url");
       bhomFeatureMethods.selectEventFiltersBasedOnCount(PortalTestData.get("DevicesStatus"));
       String deviceUrl = bhomFeatureMethods.copyDeviceUrl(device1, device2, device3, device4);
       log.info("Step12:- Paste the Device 1 Url in a new tab and verify the device is not accessible");
       portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18979");
       PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"AIOPS", "AiopsTestData", "DRSQA_18979_TestData");
       portalFeatureMethods.loginToADEPlatform(PortalTestData.get("RestrictedUser"), PortalTestData.get("Password"));
       context.setAttribute("DRSQA_18979_Driver", getDriver());
       context.setAttribute("DRSQA_18979_Product", Product.Helix_Portal);
       adeportallandingpage = new AdePortalLandingPage(getDriver());
       adeportallandingpage.verifyLoginToADEPlatform();
       portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
       bhomFeatureMethods = new BhomFeatureMethods();
       bhomFeatureMethods.verifyLogin();
       bhomFeatureMethods.pasteDeviceUrlAndVerifyUserHaveNoAccess(deviceUrl);

       log.info("-------END : RBAC - Enforce authorization profiles when viewing Devices in BHOM UI when RBAC is enabled");
    }
    
    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (portalFeatureMethods != null) {
                portalFeatureMethods.closeBrowser();
            }
            if (bhomFeatureMethods != null) {
                bhomFeatureMethods.closeBrowser();
            }
            if (aiopsFeatureMethods != null) {
                aiopsFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }

}