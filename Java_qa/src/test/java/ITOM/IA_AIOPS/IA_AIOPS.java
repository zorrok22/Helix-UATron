package ITOM.IA_AIOPS;

import ITOM.Sanity_Prerequisites;
import ITOM.Intelligent_Automation.IntelligentAutomationTest;
import framework.Launcher;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Aiops.AIOpsPage;
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
import java.util.Map;

public class IA_AIOPS extends Launcher {

    public static SolqaLogger log = new SolqaLogger(IA_AIOPS.class.getName());

    WebDriver driver;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    ReadJson readJson = new ReadJson();
    CommonWaits commonWaits;
    IAFeatureMethod iaFeatureMethod;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    public static String AIOpsService;
    public String IS_PolicyName = "UsedForAutomation";
    IntelligentAutomationPage iaPage;
    PoliciesPage policiesPage;
    HistoryPage historyPage;
    AIOpsPage aiOpsPage;
    DiscoveryPage discoveryPage;
    ServiceMonitoringPage smPage;
    BhomPage monitoringPage;
    BhomFeatureMethods bhomFeatureMethods;
    public String portal = "BMC Helix Portal";

    private Map<Object, String> itomUserData;
    private String userKey = "aananda"; // Default user key

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(config);
        userKey = "itomadminuser";
        log.info("IA_AIOPS will use user: " + itomUserData.get("UserName"));
    }

    public void doPreconditions() {
        driver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(driver);
        commonWaits = new CommonWaits();
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
        readJson = new ReadJson();
        AIOpsService = "SolqaAutomationIAService";
    }

    @AfterClass
    public void teardown() throws Exception {
        doPreconditions();
        log.info("Finally, Delete the created Policy");
        iaFeatureMethod.update_IA_policy_status_via_api(IS_PolicyName, "DRAFT");
        iaFeatureMethod.delete_IA_policy_via_api(IS_PolicyName);
        aiOpsFeatureMethods.closeBrowser();
    }

    @Test(description = "[Intelligent Automation] Verify Create Automation on Intelligent Automation from AIOps", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.dataCreationForDRSQA_12374"},
            groups = {"intelligentautomation", "aiops", "sanity", "sanity_aiops"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12374", Products = {Product.intelligent_automation, Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_12374_Verify_Create_Automation_On_Intelligent_Automation_From_AIOps(ITestContext context) {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("------- START: DRSQA-12374 [Intelligent Automation] Verify Create Automation on Intelligent Automation from AIOps ------------");
        doPreconditions();
        context.setAttribute("DRSQA_12374_Driver", driver);
        context.setAttribute("DRSQA_12374_Product", Product.ADE_Platform);
        login(username, password, SolQAConstants.appITOM);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();
        aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

        log.info("Enable IA permission");
        aiOpsFeatureMethods.configureIAPermission();
        log.info("Navigate to Entitles tab");
        aiOpsFeatureMethods.navigateToEntitles();
        aiOpsFeatureMethods.openService(Sanity_Prerequisites.AIOpsService_DRSQA_12374);

        if (aiOpsFeatureMethods.getTotalEventsCount() < 3) {
            log.info("Total events count is less than 3, sending events to ensure at least 3 events are present for the service.");
            String ciName = SolQAConstants.PA_AGENT;
            aiOpsFeatureMethods.createServiceUsingUI(Sanity_Prerequisites.AIOpsService_DRSQA_12374, ciName);
            bhomFeatureMethods.send_events("event 1", ciName);
            bhomFeatureMethods.send_events("event 2", ciName);
            bhomFeatureMethods.send_events("event 3", ciName);
        }

        log.info("Create Automation started");
        bhomFeatureMethods.selectMessageForCreateAutomation();

        log.info("Run Automation Policy in AIOps");
        iaFeatureMethod.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
        iaFeatureMethod.refreshBrowser();
        log.info("Wait for IA Policy to get reflected.");
        aiOpsFeatureMethods.selectMessageForRunAutomation(IS_PolicyName);

        log.info("Verify that Policy is executed successfully on History Page in IA");
        iaFeatureMethod.switchToWindow(IntelligentAutomationTest.IATile);

        iaFeatureMethod.navigateToHistoryPage();
        iaFeatureMethod.searchPolicy(IS_PolicyName);
        iaFeatureMethod.verifyFirstFilterResult(policiesPage.manualText, policiesPage.successText);

        log.info("Verify that Policy status is successful in AIOps");
        iaFeatureMethod.switchToWindow(IntelligentAutomationTest.AIOpsTitle);
        iaFeatureMethod.refreshBrowser();
        Assert.assertEquals(aiOpsFeatureMethods.getAutomationPolicyStatus(IS_PolicyName), "successful");
        switchToTabByTitle(SolQAConstants.AIOPS);
        aiOpsFeatureMethods.refreshBrowser();
        aiOpsFeatureMethods.navigateToServicesTab();
        aiOpsFeatureMethods.deleteServiceUsingUI(Sanity_Prerequisites.AIOpsService_DRSQA_12374);
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