package ITOM.Aiops;

import ITOM.Intelligent_Automation.IntelligentAutomationTest;
import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Api.Discovery;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.Discovery.DiscoveryPage;
import pages.ITOM.Intelligent_Automation.HistoryPage;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Intelligent_Automation.PoliciesPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import util.*;
import java.io.File;
import java.time.Duration;
import java.util.Map;

public class AIOpsWithIAPolicyTests extends Launcher {
    public static SolqaLogger log = new SolqaLogger(AIOpsWithIAPolicyTests.class.getName());
    WebDriver driver;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    String app = "ITOM";
    String serviceName;
    String cluster_CI = config.get("CLUSTER_CI");
    String k8s_cluster = config.get("k8s_cluster");
    String accessKey = SolQAConstants.API_ACCESS_KEY;
    String secreteKey = SolQAConstants.API_SECRET_KEY;
    ReadJson readJson = new ReadJson();
    Map<Object, String> aiopsTestData;
    CommonWaits commonWaits;
    IAFeatureMethod iaFeatureMethod;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    public static String AIOpsService;
    public String IS_PolicyName="UsedForAutomation";
    IntelligentAutomationPage iaPage;
    PoliciesPage policiesPage;
    HistoryPage historyPage;
    AIOpsPage aiOpsPage;
    DiscoveryPage discoveryPage;
    ServiceMonitoringPage smPage;
    BhomPage monitoringPage;
    BhomFeatureMethods bhomFeatureMethods;
    public String portal="BMC Helix Portal";

    public void doPreconditions() {
        driver = this.launchWeb(app, app);
        setDriver(driver);
        commonWaits=new CommonWaits();
        iaFeatureMethod = new IAFeatureMethod();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        iaPage = new IntelligentAutomationPage();
        policiesPage = new PoliciesPage();
        discoveryPage = new DiscoveryPage();
        monitoringPage = new BhomPage();
        aiOpsPage = new AIOpsPage();
        historyPage = new HistoryPage();
        smPage = new ServiceMonitoringPage(getDriver());
        bhomFeatureMethods = new BhomFeatureMethods();
        readJson =new ReadJson();
        AIOpsService="SolqaAutomationIAService";
    }

    @Test(description = "Prereq for IA policy creation", priority = 1,enabled=false)
    @SolqaDetails(authorName = "dmascare",  TTID="", JiraID = "", Products = {Product.intelligent_automation})
    public void preconditionIAPOlicy() throws Exception {
        doPreconditions();
        log.info("***** Create IS Policy --> " + IS_PolicyName + " <-- if not available *******");
        iaFeatureMethod.configure_IS_connector("ISCloud");
        iaFeatureMethod.Configure_IA_policy_with_Innovation_Studio_API(IS_PolicyName);
    }

    @AfterMethod
    public void teardown() throws Exception{
        doPreconditions();
        log.info("Finally, Delete the created Policy");
        iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName, "DRAFT");
        iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        bhomFeatureMethods.closeEventsWithMsgAndCI("", cluster_CI);
    }


    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "[Intelligent Automation] Verify Create Automation on Intelligent Automation from AIOps  https://jira.example.com/browse/DRSQA-12374 ", dependsOnMethods = {"preconditionIAPOlicy"},priority = 1,enabled=false, groups = {"sanity_aiops", "sanity_intelligentautomation", "sanity"})
    @SolqaDetails(authorName = "dmascare",  TTID="39987", JiraID = "DRSQA-12374", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12374_Verify_Create_Automation_On_Intelligent_Automation_From_AIOps(ITestContext context ) throws Exception {

        log.info("------- START: DRSQA-12374 [Intelligent Automation] Verify Create Automation on Intelligent Automation from AIOps ------------");
        String AIOpsService_DRSQA_12374 = "";
        doPreconditions();
        AIOpsService_DRSQA_12374 = AIOpsService + RandomStringUtils.randomAlphanumeric(5);

        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), SolQAConstants.appITOM);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();
        aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

        log.info("--------- Create Service using backend--> " + AIOpsService_DRSQA_12374);

        aiOpsFeatureMethods.createServiceFromAPI(AIOpsService_DRSQA_12374, cluster_CI,accessKey, secreteKey);
        bhomFeatureMethods.send_events("event 1", cluster_CI);
        bhomFeatureMethods.send_events("event 2", cluster_CI);
        bhomFeatureMethods.send_events("event 3", cluster_CI);

        try
        {

            log.info("Navigate to AIOps page");
            iaPage.navigateToMonitoringPage();
            iaPage.switchToWindow(IntelligentAutomationTest.AIOpsTitle);

            log.info("Enable IA permission");
            aiOpsPage.configureIAPermission();
            log.info("Navigate to Entitles tab");
            aiOpsPage.navigateToEntitles();
            aiOpsPage.openService(AIOpsService_DRSQA_12374);
            log.info("Create Automation started");
            monitoringPage.selectMessageForCreateAutomation();

            log.info("Run Automation Policy in AIOps");
            iaPage.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
            iaPage.refreshBrowser();
            log.info("Wait for IA Policy to get reflected.");
            aiOpsPage.selectMessageForRunAutomation(IS_PolicyName);

            log.info("Verify that Policy is executed successfully on History Page in IA");
            iaPage.switchToWindow(IntelligentAutomationTest.IATile);

            iaPage.navigateToHistoryPage();
            historyPage.searchPolicy(IS_PolicyName);
            historyPage.verifyFirstFilterResult(policiesPage.manualText, policiesPage.successText);

            log.info("Verify that Policy status is successful in AIOps");
            this.iaPage.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
            this.iaPage.refreshBrowser();
            Assert.assertEquals(aiOpsPage.getAutomationPolicyStatus(IS_PolicyName), "successful");

        }
        finally
        {
            log.info("Deleting the service from Discovery.");
            aiOpsFeatureMethods.deleteService(AIOpsService_DRSQA_12374);
        }
    }

    @Test(description = "[Intelligent Automation] Verify Request Automation on Intelligent Automation from AIOps  https://jira.example.com/browse/DRSQA-12378 ", priority = 0,groups={"Regression"})
    @SolqaDetails(authorName = "dmascare",  TTID="39991", JiraID = "DRSQA-12378", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12378_Verify_Request_Automation_On_Intelligent_Automation_From_AIOps() throws Exception {

        log.info("------- START: DRSQA-12378 [Intelligent Automation] Verify Request Automation on Intelligent Automation from AIOps ------------");
        String AIOpsService_DRSQA_12378 = "";

        doPreconditions();
        AIOpsService_DRSQA_12378 = RandomStringUtils.randomAlphanumeric(8);
        String IS_PolicyName_DRSQA_12378 = RandomStringUtils.randomAlphanumeric(5);//IS_PolicyName+ RandomStringUtils.randomAlphanumeric(5);

        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);

        log.info("--------- Create Service using backend--> " + AIOpsService_DRSQA_12378);
        aiOpsFeatureMethods.createServiceFromAPI(AIOpsService_DRSQA_12378, cluster_CI, accessKey, secreteKey);
        bhomFeatureMethods.send_events("event 1", cluster_CI);
        bhomFeatureMethods.send_events("event 2", cluster_CI);
        bhomFeatureMethods.send_events("event 3", cluster_CI);

        try {

            log.info("---- Wait until 3 min for the service to get impacted.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(AIOpsService_DRSQA_12378, 100, Duration.ofMinutes(3))) {
                log.info("Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. ");
            }

            log.info("*** Do Request Automation from AIOps");
            iaPage.navigateToMonitoringPage();
            log.info("Enable IA permission");
            aiOpsPage.configureIAPermission();
            aiOpsPage.navigateToEntitles();
            aiOpsPage.openService(AIOpsService_DRSQA_12378);
            String requestName = this.aiOpsPage.requestAutomationAction();
            log.info("Create Automation Policy in IA");
            iaPage.switchToWindow(portal);
            iaPage.navigateToAutomationPage().verifyTitle();
            iaPage.switchToWindow(IntelligentAutomationTest.IATile);
            iaPage.navigateToRequestPage();
            iaPage.navigateToPolicyPageFromRequestPage(requestName);
            policiesPage.setPolicyNameAndDescription(IS_PolicyName_DRSQA_12378);
            iaPage.deleteExistingTriggerCondition();
            iaPage.setTriggerCondition();
            iaPage.selectConnector(IntelligentAutomationTest.ISConnectorName);
            iaPage.syncAction();
            iaPage.selectAction(IntelligentAutomationTest.ISSearchAction);
            iaPage.configureChangeManagementSlider(false);
            iaPage.publishAndSavePolicy();
            log.info("Policy is created in IA with name " + IS_PolicyName_DRSQA_12378);

            log.info("Run Automation Policy in AIOps");
            iaPage.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
            iaPage.refreshBrowser();
            aiOpsPage.selectMessageForRunAutomation(IS_PolicyName_DRSQA_12378);
            log.info("Verify that Policy is executed successfully on History Page in IA");
            iaPage.switchToWindow(IntelligentAutomationTest.IATile);
            iaPage.navigateToHistoryPage();
            historyPage.searchPolicy(IS_PolicyName_DRSQA_12378);
            historyPage.verifyFirstFilterResult(policiesPage.manualText, policiesPage.successText);

            log.info("Verify that Policy status is Successful in AIOps");
            iaPage.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
            iaPage.refreshBrowser();
            Assert.assertEquals(aiOpsPage.getAutomationPolicyStatus(IS_PolicyName_DRSQA_12378), "successful");

            log.info("Move Policy to Draft status");
            iaPage.switchToWindow(IntelligentAutomationTest.IATile);
            iaPage.navigateToPoliciesPage();
            policiesPage.searchPolicy(IS_PolicyName_DRSQA_12378, policiesPage.rowCount);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);

            log.info("Delete Policy in IA");
            iaPage.switchToWindow(IntelligentAutomationTest.IATile);
            iaPage.navigateToPoliciesPage();
            policiesPage.searchPolicy(IS_PolicyName_DRSQA_12378, policiesPage.rowCount);
            iaPage.deletePolicy(IS_PolicyName_DRSQA_12378);

            log.info("Verify that policy is not visible in AIOps service");
            iaPage.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
            iaPage.refreshBrowser();
            Assert.assertFalse(this.aiOpsPage.isAutomationPolicyVisible(IS_PolicyName_DRSQA_12378), "Policy is still visible in Aiops");

        }
        finally {
            iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName_DRSQA_12378, "DRAFT");
            iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName_DRSQA_12378);
            log.info("Deleting the service from Discovery.");
            aiOpsFeatureMethods.deleteService(AIOpsService_DRSQA_12378);
        }
        log.info("------- END: DRSQA-12378 [Intelligent Automation] Verify Request Automation on Intelligent Automation from AIOps ------------");
    }

    @Test(description = "Events : IA Support in BHOM to trigger Automation https://jira.example.com/browse/DRSQA-12320 ",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare",  TTID="39933", JiraID = "DRSQA-12320", Products = {Product.intelligent_automation,Product.ADE_OperationsManagement})
    public void DRSQA_12320_IA_Support_In_BHOM_To_Trigger_Automation() throws Exception {
        doPreconditions();
        iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
        iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        log.info("***** Create IS Policy --> " + IS_PolicyName + " <-- if not available *******");
        iaFeatureMethod.configure_IS_connector("ISCloud");
        iaFeatureMethod.Configure_IA_policy_with_Innovation_Studio_API(IS_PolicyName);

        try {
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);

            String uniqueEventMessage = commonWaits.generateRandomString();
            log.info("Send event with message: " + uniqueEventMessage);
            bhomFeatureMethods.send_events(uniqueEventMessage, cluster_CI);

            log.info("Go to BHOM -> Events Page and search for the event generated.");
            bhomFeatureMethods.searchForEventGenerated(uniqueEventMessage);

            log.info("Check if we can Run the automation using the IA Policy created on the selected event. Trigger the run.");
            bhomFeatureMethods.verifyTriggerAutomation(uniqueEventMessage);
        }
        finally {
            aiOpsFeatureMethods.closeBrowser();
            log.info("Finally, Delete the created Policy");
            iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
            iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        }
    }

    @Test(description = "AIOps: Validate the Request a new Automation flow from PCA",groups={"Regression"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12379", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12379_Validate_the_Request_a_new_Automation_flow_from_PCA(ITestContext context) throws Exception {
        log.info("------- START: DRSQA_12379 AIOps: Validate the Request a new Automation flow from PCA ------------");
        context.setAttribute("12379_Product", Product.Service_Monitoring);
        context.setAttribute("12379_WebDriver", driver);

        doPreconditions();
        iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
        iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        log.info("***** Create IS Policy --> " + IS_PolicyName + " <-- if not available *******");
        iaFeatureMethod.configure_IS_connector("ISCloud");
        iaFeatureMethod.Configure_IA_policy_with_Innovation_Studio_API(IS_PolicyName);

        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsService_TestData", "ServiceData");
        serviceName = RandomStringUtils.randomAlphanumeric(8); //aiopsTestData.get("serviceName") +

        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();

        log.info("--------- Create Service using backend--> " + serviceName);
        aiOpsFeatureMethods.createServiceFromAPI(serviceName, cluster_CI, accessKey, secreteKey);
        bhomFeatureMethods.send_events("event 1", cluster_CI);
        bhomFeatureMethods.send_events("event 2", cluster_CI);
        bhomFeatureMethods.send_events("event 3", cluster_CI);

        try {

            log.info("---- Wait until 3 min for the service to get impacted.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(3))) {
                log.info("Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. ");
            }

            log.info("------------- Navigate to AIOPS Application and go to Services page------>");
            aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();
            log.info("--------------- Open Service --> " + serviceName);
            aiOpsFeatureMethods.openService(serviceName);
            aiOpsFeatureMethods.captureScreenshot("12379_checkServiceImpactedOrNot", SolQAConstants.appITOM);
            log.info("--------------- Run automation and check automation run status--> ");
            aiOpsFeatureMethods.runAutomationPolicyAndCheckStatus(IS_PolicyName);
            log.info("------- END: 12379 AIOps: Validate the Request a new Automation flow from PCA ------------");

        } catch (Exception t) {
            log.error("Test Failed: " + t.getMessage());
            throw new AssertionError(t.getMessage());
        } finally {
            log.info("Finally, Delete the created Policy");
            iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
            iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
            log.info("Deleting the service from Discovery.");
            aiOpsFeatureMethods.deleteService(serviceName);

        }
    }

    @Test(description = "AIOps : Verify AIOps functionality with data integration from multiple source as input ",groups={"Regression"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12355", Products = {Product.Service_Monitoring})
    public void DRSQA_12355_Verify_Verify_AIOps_functionality(ITestContext context) throws Exception {
        log.info("------- START: DRSQA_12355 AIOps : Verify AIOps functionality with data integration from multiple source as input ------------");
        context.setAttribute("12355_Product", Product.Service_Monitoring);
        context.setAttribute("12355_WebDriver", driver);

        doPreconditions();

        iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
        iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        log.info("***** Create IS Policy --> " + IS_PolicyName + " <-- if not available *******");
        iaFeatureMethod.configure_IS_connector("ISCloud");
        iaFeatureMethod.Configure_IA_policy_with_Innovation_Studio_API(IS_PolicyName);

        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();

        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsService_TestData", "ServiceData");
        serviceName = RandomStringUtils.randomAlphanumeric(5);//aiopsTestData.get("serviceName") + RandomStringUtils.randomAlphanumeric(5);
        String k8sServiceName = RandomStringUtils.randomAlphanumeric(5); //aiopsTestData.get("k8sService") + RandomStringUtils.randomAlphanumeric(5);

        log.info("--------- Create Service using backend--> " + serviceName);
        aiOpsFeatureMethods.createServiceFromAPI(serviceName, cluster_CI, accessKey, secreteKey);

        try {

            log.info("--------------- Create Service for k8s kubernetes cluster through discovery --> " + k8sServiceName);
            aiOpsFeatureMethods.createServiceFromAPI(k8sServiceName, "kubernetes cluster", "K8S",accessKey, secreteKey);
            //iaFeatureMethod.CreateServiceInAIOps(k8sServiceName, k8s_cluster);

            log.info("------------- Navigate to AIOPS Application and go to Services page------>");
            aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

            bhomFeatureMethods.send_events("event 1", cluster_CI);
            bhomFeatureMethods.send_events("event 2", cluster_CI);
            bhomFeatureMethods.send_events("event 3", cluster_CI);

            log.info("---- Wait until 3 min for the service to get impacted.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(3))) {
                log.info("Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. ");
            }

            log.info("------------- Navigate to Services tab");
            aiOpsFeatureMethods.navigateToServicesTab();
            aiOpsFeatureMethods.openService(serviceName);
            aiOpsFeatureMethods.captureScreenshot("12355_checkServiceImpactedOrNot", SolQAConstants.appITOM);
            aiOpsFeatureMethods.verifyServiceImpactedAndHealthScoreUpdated(serviceName);

            log.info("--------------- Run automation and check automation run status--> ");
            aiOpsFeatureMethods.runAutomationPolicyAndCheckStatus(IS_PolicyName);

            log.info("------- END: DRSQA_12355 AIOps : Verify AIOps functionality with data integration from multiple source as input ------------");

        } catch (Exception t) {
            log.error("Test Failed: " + t.getMessage());
            throw new AssertionError(t.getMessage());
        } finally {
            log.info("Finally, Delete the created Policy");
            iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
            iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
            log.info("Deleting the service from Discovery.");
            aiOpsFeatureMethods.deleteService(serviceName);
            iaFeatureMethod.DeleteServiceInAIOps(k8sServiceName);
            aiOpsFeatureMethods.closeBrowser();
        }
    }


    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "AIOps: Validate the avaialble Event actions for an Event", dependsOnMethods = {"preconditionIAPOlicy"}, priority = 5,enabled=false)
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12425", Products = {Product.Service_Monitoring})
    public void DRSQA_12425_Validate_the_available_Event_actions_for_an_Event(ITestContext context) {
        log.info("------- START: DRSQA-12425 AIOps: Validate the available Event actions for an Event ------------");
        context.setAttribute("12425_Product", Product.Service_Monitoring);
        context.setAttribute("12425_WebDriver", driver);

        doPreconditions();

        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();

        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsService_TestData", "ServiceData");
        serviceName = aiopsTestData.get("serviceName") + RandomStringUtils.randomAlphanumeric(5);
        String situation = aiopsTestData.get("situationName");

        log.info("--------- Create Service using backend--> " + serviceName);
        aiOpsFeatureMethods.createServiceFromAPI(serviceName, cluster_CI, accessKey, secreteKey);
        bhomFeatureMethods.send_events("event 1", cluster_CI);
        bhomFeatureMethods.send_events("event 2", cluster_CI);
        bhomFeatureMethods.send_events("event 3", cluster_CI);


        try {
            log.info("------------- Navigate to AIOPS Application and go to Services Tab------>");
            aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

            aiOpsFeatureMethods.openService(serviceName);
            aiOpsFeatureMethods.captureScreenshot("12425_checkServiceImpactedOrNot", SolQAConstants.appITOM);
            aiOpsFeatureMethods.verifyServiceImpactedAndHealthScoreUpdated(serviceName);

            log.info("------------- on PCA section - verify all event Options");
            aiOpsFeatureMethods.verifyEventOptionsForFirstEvent();
            aiOpsFeatureMethods.captureScreenshot("12425_EventDetails", SolQAConstants.appITOM);

            log.info("------------- Navigate to Situation page explicitly and verify situation options ---->");
            aiOpsFeatureMethods.navigateToSituationPageAndVerifySituationOption(situation);
            aiOpsFeatureMethods.captureScreenshot("12425_situationDetails", SolQAConstants.appITOM);

            log.info("------- END: DRSQA-12425 AIOps: Validate the available Event actions for an Event ------------");

        } catch (Exception t) {
            log.error("Test Failed: " + t.getMessage());
            throw new AssertionError(t.getMessage());
        } finally {
            log.info("Deleting the service from Discovery.");
            aiOpsFeatureMethods.deleteService(serviceName);
        }
    }


    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "[Aiops]Validate The Event Details Crosslaunch to BHOM", dependsOnMethods = {"preconditionIAPOlicy"}, priority = 6,enabled=false)
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12426", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12426_Validate_Event_Details_Cross_launch_to_BHOM(ITestContext context) {
        log.info("------- START: DRSQA_12426 AIOps: Validate The Event Details Crosslaunch to BHOM ------------");
        context.setAttribute("12426_Product", Product.Service_Monitoring);
        context.setAttribute("12426_WebDriver", driver);

        doPreconditions();

        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsService_TestData", "ServiceData");
        serviceName = aiopsTestData.get("serviceName") + RandomStringUtils.randomAlphanumeric(5);

        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), app);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();

        log.info("--------- Create Service using backend--> " + serviceName);
        aiOpsFeatureMethods.createServiceFromAPI(serviceName, cluster_CI,accessKey, secreteKey);
        bhomFeatureMethods.send_events("event 1", cluster_CI);
        bhomFeatureMethods.send_events("event 2", cluster_CI);
        bhomFeatureMethods.send_events("event 3", cluster_CI);

        try {
            log.info("------------- Navigate to AIOPS Application and go to Services Tab------>");
            aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

            aiOpsFeatureMethods.openService(serviceName);
            aiOpsFeatureMethods.captureScreenshot("12426_checkServiceImpactedOrNot", SolQAConstants.appITOM);
            aiOpsFeatureMethods.verifyServiceImpactedAndHealthScoreUpdated(serviceName);

            log.info("------------- Open Event and check all required data is present and cross launch event in BHOM application and check event details --------> ");
            aiOpsFeatureMethods.openEventDetailsAndCrossLaunchEventInBHOMApplication();
            aiOpsFeatureMethods.captureScreenshot("12426_BHOMPageEventdetails", SolQAConstants.appITOM);

            log.info("------- END: DRSQA-12426 AIOps: Validate The Event Details Cross launch to BHOM ------------");

        } catch (Exception t) {
            log.error("Test Failed: " + t.getMessage());
            throw new AssertionError(t.getMessage());
        } finally {
            log.info("Deleting the service from Discovery.");
            aiOpsFeatureMethods.deleteService(serviceName);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (iaFeatureMethod != null) {
                iaFeatureMethod.closeBrowser();
            }
            if (aiOpsFeatureMethods != null) {
                aiOpsFeatureMethods.closeBrowser();
            }
            if (iaPage != null) {
                iaPage.closeBrowser();
            }
            if (policiesPage != null) {
                policiesPage.closeBrowser();
            }
            if (discoveryPage != null) {
                discoveryPage.closeBrowser();
            }
            if (monitoringPage != null) {
                monitoringPage.closeBrowser();
            }
            if (aiOpsPage != null) {
                aiOpsPage.closeBrowser();
            }
            if (historyPage != null) {
                historyPage.closeBrowser();
            }
            if (smPage != null) {
                smPage.closeBrowser();
            }
            if (bhomFeatureMethods != null) {
                bhomFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }

}