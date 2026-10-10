package ITOM;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Discovery.DiscoveryFeatureMethods;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import pages.ITOM.Itsm_Insights.ITSMInsightsFeatureMethods;
import util.*;

public class Sanity_Prerequisites extends Launcher {

    static SolqaLogger log = new SolqaLogger(Sanity_Prerequisites.class.getName());

    WebDriver ITOMDriver;
    AIOpsFeatureMethods aiopsFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;
    BhomFeatureMethods bhomFeatureMethods;
    IAFeatureMethod iaFeatureMethod;
    ITSMInsightsFeatureMethods itsmInsightsFeatureMethods;
    DiscoveryFeatureMethods discoveryFeatureMethods;
    CommonWaits commonWaits;

    public static String randomString = "_" + RandomStringUtils.randomAlphanumeric(5);
    public static String DRSQA_12355_ServiceName = "DRSQA_12355_Service" + randomString;
    public static String DRSQA_12426_ServiceName = "DRSQA_12426_Service"+ randomString;
    public static String DRSQA_12454_ServiceName = "DRSQA_12454_Service"+ randomString;
    public static String DRSQA_12355_k8sServiceName = "DRSQA_12355_k8sService"+ randomString;
    public static String AIOpsService_DRSQA_12374 = "DRSQA_12374_AIOpsService"+ randomString;
 

    @Test(priority = 2, groups = {"sanity", "sanity_monitor", "sanity_aiops"},
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            description = "Data creation for DRSQA-12355: cluster AIOps service + Discovery k8s service + BHOM events")
    public void dataCreationForDRSQA_12355(ITestContext context) throws Exception {
        log.info("==== Data creation DRSQA-12355 started ====");
        doPreconditions(context);
        
        String ciName = SolQAConstants.CLUSTER_CI;
        log.info("Creating service: " + DRSQA_12355_ServiceName + " for CI: " + ciName);
        aiopsFeatureMethods.createServiceUsingUI(DRSQA_12355_ServiceName, ciName);

        String k8s_cluster = CommonMethods.readConfig("k8s_cluster");
        log.info("Creating k8s service: " + DRSQA_12355_k8sServiceName + " for k8s cluster: " + k8s_cluster);
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.DISCOVERY_TILE);
        discoveryFeatureMethods.verifyLogin();
        discoveryFeatureMethods.CreateServiceInDiscovery(DRSQA_12355_k8sServiceName, k8s_cluster);
        bhomFeatureMethods.send_events("event 1", k8s_cluster);
        bhomFeatureMethods.send_events("event 2", k8s_cluster);
        bhomFeatureMethods.send_events("event 3", k8s_cluster);
        bhomFeatureMethods.send_events("event 4", k8s_cluster);
        bhomFeatureMethods.send_events("event 5", k8s_cluster);
        bhomFeatureMethods.send_events("event 6", k8s_cluster);
        bhomFeatureMethods.send_events("event 7", k8s_cluster);
        log.info("==== Data creation DRSQA-12355 completed ====");
    }

    @Test(priority = 1, groups = {"sanity","sanity_aiops","sanity_monitor"},
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            description = "Data creation for DRSQA-12426: AIOps service on cluster CI")
    public void dataCreationForDRSQA_12426(ITestContext context) throws Exception {
        log.info("==== Data creation DRSQA-12426 started ====");
        doPreconditions(context);
        String ciName = SolQAConstants.CLUSTER_CI;
        log.info("Creating service: " + DRSQA_12426_ServiceName + " for CI: " + ciName);
        aiopsFeatureMethods.createServiceUsingUI(DRSQA_12426_ServiceName, ciName);
        log.info("==== Data creation DRSQA-12426 completed ====");
    }

    @Test(priority = 1, groups = {"sanity","sanity_monitor","sanity_aiops"},
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            description = "Data creation for DRSQA-12454: AIOps service on health-indicator CI")
    public void dataCreationForDRSQA_12454(ITestContext context) throws Exception {
        log.info("==== Data creation DRSQA-12454 started ====");
        doPreconditions(context);
        String healthIndicatorCIName = SolQAConstants.CLUSTER_CI;
        log.info("Creating service: " + DRSQA_12454_ServiceName + " for Health Indicator CI: " + healthIndicatorCIName);
        aiopsFeatureMethods.createServiceUsingUI(DRSQA_12454_ServiceName, healthIndicatorCIName);
        log.info("==== Data creation DRSQA-12454 completed ====");
    }

    @Test(priority = 1, groups = {"sanity","sanity_monitor","sanity_aiops"},
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            description = "Data creation for DRSQA-12374: AIOps service + BHOM events on cluster CI")
    public void dataCreationForDRSQA_12374(ITestContext context) throws Exception {
        log.info("==== Data creation DRSQA-12374 started ====");
        doPreconditions(context);
        String ciName = SolQAConstants.CLUSTER_CI;
        log.info("Creating AIOps service: " + AIOpsService_DRSQA_12374 + " for CI: " + ciName);
        aiopsFeatureMethods.createServiceUsingUI(AIOpsService_DRSQA_12374, ciName);
        log.info("==== Data creation DRSQA-12374 completed ====");
    }

    /**
     * Sends three BHOM events for the cluster CI (same as sanityPrerequisites lines 89–92).
     * Runs after all granular data-creation tests finish; {@code alwaysRun = true} keeps this
     * executing even when one of those tests fails.
     */
    @Test(priority = 3, groups = {"sanity","sanity_monitor", "sanity_aiops"}, alwaysRun = true,
            dependsOnMethods = {
                    "dataCreationForDRSQA_12355",
                    "dataCreationForDRSQA_12426",
                    "dataCreationForDRSQA_12454",
                    "dataCreationForDRSQA_12374"
            },
            description = "Send 3 BHOM events for cluster CI after data-creation tests (runs even if prior tests failed)")
    public void sendBhomEventsForClusterAfterDataCreationTests(ITestContext context) throws Exception {
        log.info("==== Send BHOM events for cluster CI (after data-creation tests) started ====");
        doPreconditions(context);
        String ciName = SolQAConstants.CLUSTER_CI;
        log.info("Sending 3 events for CI: " + ciName);
        bhomFeatureMethods.send_events("event 1", ciName);
        bhomFeatureMethods.send_events("event 2", ciName);
        bhomFeatureMethods.send_events("event 3", ciName);
        log.info("==== Send BHOM events for cluster CI completed ====");
    }

    @Test(priority = 4, groups = {"sanity","sanity_intelligentautomation"},
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            description = "IA data setup: cross-launch Intelligent Automation and enable PSR (sanityPrerequisites lines 61-64)")
    public void datasetupforIA(ITestContext context) throws Exception {
        log.info("==== datasetupforIA started ====");
        launchLoginVerifyOnITOMPortal(context);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.IA);
        log.info("Cross-launched to IA application and enabling PSR.");
        portalFeatureMethods.enablePSR(CommonMethods.readConfig("CMDBURL"),
                CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"));
        log.info("==== datasetupforIA completed ====");
    }

    /** Launch ITOM, login, init page objects, verify portal login (no product cross-launch). */
    private void launchLoginVerifyOnITOMPortal(ITestContext context) throws Exception {
        log.info("==== Sanity Prerequisites Test Started ====");
        ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(ITOMDriver);
        context.setAttribute("SanityPrerequisites_Driver", ITOMDriver);
        context.setAttribute("SanityPrerequisites_Product", Product.ADE_Platform);
        log.info("Launched ITOM application and set driver.");
        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        log.info("Logged in to ITOM with provided credentials.");
        itsmInsightsFeatureMethods = new ITSMInsightsFeatureMethods();
        discoveryFeatureMethods = new DiscoveryFeatureMethods();
        portalFeatureMethods = new Portal_FeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
        bhomFeatureMethods = new BhomFeatureMethods();
        iaFeatureMethod = new IAFeatureMethod();
        commonWaits = new CommonWaits();

        portalFeatureMethods.verifyLogin();
        log.info("Verified login to ITOM portal.");
    }

    public void doPreconditions(ITestContext context) throws Exception {
        launchLoginVerifyOnITOMPortal(context);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
        log.info("Cross-launched to AIOps application.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (ITOMDriver != null) {
            try {
                ITOMDriver.quit();
                log.info("Driver closed successfully");
            } catch (Exception e) {
                log.warn("Error closing driver: " + e.getMessage());
            }
        }
    }
}