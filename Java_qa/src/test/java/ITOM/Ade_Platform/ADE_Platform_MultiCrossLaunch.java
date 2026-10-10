package ITOM.Ade_Platform;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITOM.Developer_Tool.DeveloperToolFeatureMethods;
import pages.ITOM.Discovery.DiscoveryFeatureMethods;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import pages.ITOM.Intelligent_Integrations.IIFeatureMethods;
import pages.ITOM.Log_Analytics.LogAnalyticsFeatureMethods;
import pages.ITOM.Multiservice.ServiceMonitoringFeatureMethods;
import pages.ITOM.Optimize.OptimizeFeatureMethods;
import pages.ITOM.Platform.PlatformFeatureMethods;
import pages.ITSM.CMDB.CMDBFeatureMethods;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.*;
import java.io.File;
import java.util.Map;

public class ADE_Platform_MultiCrossLaunch extends Launcher {

    static SolqaLogger log = new SolqaLogger(ADE_Platform_MultiCrossLaunch.class.getName());
    WebDriver adeDriver;
    PlatformFeatureMethods platformFeatureMethods;
    BWFFeatureMethods bwfFeatureMethods;
    CMDBFeatureMethods cmdbFeatureMethods;
    OptimizeFeatureMethods optimizeFeatureMethods;
    DashboadFeatureMethods dashboadFeatureMethods;
    DeveloperToolFeatureMethods developerToolFeatureMethods;
    DWPFeatureMethods dwpFeatureMethods;
    DWPCFeatureMethods dwpcFeatureMethods;
    DiscoveryFeatureMethods discoveryFeatureMethods;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    IAFeatureMethod iaFeatureMethod;
    IIFeatureMethods iiFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    LogAnalyticsFeatureMethods logAnalyticsFeatureMethods;
    BhomFeatureMethods bhomFeatureMethods;
    ServiceMonitoringFeatureMethods serviceMonitoringFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;

    private Map<Object, String> itomUserData;
    private String userKey = "aananda"; // Default user key
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(config);
        userKey = "itomadminuser";
        log.info("ADE_Platform_MultiCrossLaunch will use user: " + itomUserData.get("UserName"));
    }

    @Test(description = "Cross Launch to Helix Business Work Flow", singleThreaded = true, groups = {"sanity_BWF", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14871", Products = {Product.ADE_Platform, Product.BWF})
    public void DRSQA_14871_CrossLaunchToHelixBusinessWorkFlow() {
        // User data already loaded in @BeforeTest - just use it directly
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14871 ====");
        log.info("Cross Launch to Helix Business Work Flow");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and BWF feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            bwfFeatureMethods = new BWFFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Business Workflows");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.BWF_TILE);
            log.info("Step 4: Cross launch to BWF completed");

            log.info("Step 5: Verifying BWF login");
            bwfFeatureMethods.verifyLoginToBWF();
            log.info("Step 5: BWF login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14871 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14871 ====");
            log.error("Error during BWF cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix CMDB", singleThreaded = true, groups = {"sanity_CMDB", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14872", Products = {Product.ADE_Platform, Product.CMDB})
    public void DRSQA_14872_CrossLaunchToBMCHelixCMDB() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14872 ====");
        log.info("Cross Launch to BMC Helix CMDB");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and CMDB feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            cmdbFeatureMethods = new CMDBFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix CMDB");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.CMDB_TILE);
            log.info("Step 4: Cross launch to CMDB completed");

            log.info("Step 5: Verifying CMDB login");
            cmdbFeatureMethods.verifyLoginTo_CMDB();
            log.info("Step 5: CMDB login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14872 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14872 ====");
            log.error("Error during CMDB cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Continuous Optimization", singleThreaded = true, groups = {"sanity_Optimize", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14874", Products = {Product.ADE_Platform, Product.Helix_Optimize})
    public void DRSQA_14874_CrossLaunchToBMCHelixContinuousOptimization() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14874 ====");
        log.info("Cross Launch to BMC Helix Continuous Optimization");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Optimize feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            optimizeFeatureMethods = new OptimizeFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Continuous Optimization");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.OPTIMIZE_TILE);
            log.info("Step 4: Cross launch to Continuous Optimization completed");

            log.info("Step 5: Verifying BHOM page launch");
            optimizeFeatureMethods.verifyBHCOPageLaunchedSuccessfully();
            log.info("Step 5: BHOM page launch verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14874 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14874 ====");
            log.error("Error during Continuous Optimization cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Dashboards", singleThreaded = true, groups = {"sanity_dashboards", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14876", Products = {Product.ADE_Platform, Product.Dashboards})
    public void DRSQA_14876_CrossLaunchToBMCHelixDashboards() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14876 ====");
        log.info("Cross Launch to BMC Helix Dashboards");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Dashboard feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            dashboadFeatureMethods = new DashboadFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Dashboards");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.DASHBOARDS_TILE);
            log.info("Step 4: Cross launch to Dashboards completed");

            log.info("Step 5: Verifying Dashboard page launch");
            dashboadFeatureMethods.verifyDashboardPageLaunchedSuccessfully();
            log.info("Step 5: Dashboard page launch verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14876 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14876 ====");
            log.error("Error during Dashboards cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Developer Tools", singleThreaded = true, groups = {"sanity_intelligentintegrations", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14878", Products = {Product.ADE_Platform, Product.Developer_Tools})
    public void DRSQA_14878_CrossLaunchToBMCHelixDeveloperTools() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14878 ====");
        log.info("Cross Launch to BMC Helix Developer Tools");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Developer Tools feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            developerToolFeatureMethods = new DeveloperToolFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Developer Tools");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.DEVELOPER_TOOLS_TILE);
            log.info("Step 4: Cross launch to Developer Tools completed");

            log.info("Step 5: Verifying Developer Tools login");
            developerToolFeatureMethods.verifyLogin();
            log.info("Step 5: Developer Tools login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14878 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14878 ====");
            log.error("Error during Developer Tools cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Digital Workplace", singleThreaded = true, groups = {"sanity_DWP", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14884", Products = {Product.ADE_Platform, Product.Helix_DWP})
    public void DRSQA_14884_CrossLaunchToBMCHelixDigitalWorkplace() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14878 ====");
        log.info("Cross Launch to BMC Helix Digital Workplace");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and DWP feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            dwpFeatureMethods = new DWPFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Digital Workplace");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.DWP_TILE);
            log.info("Step 4: Cross launch to Digital Workplace completed");

            log.info("Step 5: Verifying DWP home page");
            dwpFeatureMethods.verifyDWPHomePageOpened();
            log.info("Step 5: DWP home page verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14878 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14878 ====");
            log.error("Error during Digital Workplace cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Digital Workplace Catalog", singleThreaded = true, groups = {"sanity_DWPC", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14886", Products = {Product.ADE_Platform, Product.Helix_DWPC})
    public void DRSQA_14886_CrossLaunchToBMCHelixDigitalWorkplaceCatalog() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14886 ====");
        log.info("Cross Launch to BMC Helix Digital Workplace Catalog");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and DWPC feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            dwpcFeatureMethods = new DWPCFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Digital Workplace Catalog");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.DWPC_TILE);
            log.info("Step 4: Cross launch to Digital Workplace Catalog completed");

            log.info("Step 5: Verifying DWPC page launch");
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            log.info("Step 5: DWPC page launch verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14886 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14886 ====");
            log.error("Error during Digital Workplace Catalog cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Discovery", singleThreaded = true, groups = {"sanity_Discovery", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14887", Products = {Product.ADE_Platform, Product.Helix_Discovery})
    public void DRSQA_14887_CrossLaunchToBMCHelixDiscovery() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14887 ====");
        log.info("Cross Launch to BMC Helix Discovery");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Discovery feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            discoveryFeatureMethods = new DiscoveryFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Discovery");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.DISCOVERY_TILE);
            log.info("Step 4: Cross launch to Discovery completed");

            log.info("Step 5: Verifying Discovery login");
            discoveryFeatureMethods.verifyLogin();
            log.info("Step 5: Discovery login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14887 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14887 ====");
            log.error("Error during Discovery cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Innovation Suite", singleThreaded = true, groups = {"sanity_IS", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14889", Products = {Product.ADE_Platform, Product.Innovation_Suite})
    public void DRSQA_14889_CrossLaunchToBMCHelix_Innovation_Suite() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14889 ====");
        log.info("Cross Launch to BMC Helix Innovation Suite");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Innovation Suite feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Innovation Suite");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.IS_TILE);
            log.info("Step 4: Cross launch to Innovation Suite completed");

            log.info("Step 5: Verifying Innovation Suite login");
            innovationSuiteFeatureMethodsPage.verifyLogin();
            log.info("Step 5: Innovation Suite login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14889 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14889 ====");
            log.error("Error during Innovation Suite cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Intelligent Automation", singleThreaded = true, groups = {"sanity_intelligentautomation", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14892", Products = {Product.ADE_Platform, Product.ADE_IA})
    public void DRSQA_14892_CrossLaunchToBMCHelixIntelligentAutomation() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14892 ====");
        log.info("Cross Launch to BMC Helix Intelligent Automation");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and IA feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            iaFeatureMethod = new IAFeatureMethod();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Intelligent Automation");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.IA_TILE);
            log.info("Step 4: Cross launch to Intelligent Automation completed");

            log.info("Step 5: Verifying IA login");
            iaFeatureMethod.verifyLogin();
            log.info("Step 5: IA login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14892 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14892 ====");
            log.error("Error during Intelligent Automation cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Intelligent Integrations", singleThreaded = true, groups = {"sanity_intelligentintegrations", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14894", Products = {Product.ADE_Platform, Product.Intelligent_Integrations})
    public void DRSQA_14894_CrossLaunchToBMCHelixIntelligentIntegrations() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14894 ====");
        log.info("Cross Launch to BMC Helix Intelligent Integrations");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and II feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            iiFeatureMethods = new IIFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Intelligent Integrations");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.II_TILE);
            log.info("Step 4: Cross launch to Intelligent Integrations completed");

            log.info("Step 5: Verifying II login");
            iiFeatureMethods.verifyLogin();
            log.info("Step 5: II login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14894 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14894 ====");
            log.error("Error during Intelligent Integrations cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix ITSM", singleThreaded = true, groups = {"sanity_aif", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14895", Products = {Product.ADE_Platform, Product.Helix_ITSM})
    public void DRSQA_14895_CrossLaunchToBMCHelixITSM() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14895 ====");
        log.info("Cross Launch to BMC Helix ITSM");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and SmartIT feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            smartITFeatureMethods = new SmartITFeatureMethodsPage();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix ITSM");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.ITSM_TILE);
            log.info("Step 4: Cross launch to ITSM completed");

            log.info("Step 5: Verifying SmartIT page launch");
            smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
            log.info("Step 5: SmartIT page launch verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14895 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14895 ====");
            log.error("Error during ITSM cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Log Analytics", singleThreaded = true, groups = {"sanity_loganalytics", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14896", Products = {Product.ADE_Platform, Product.Log_Analytics})
    public void DRSQA_14896_CrossLaunchToBMCHelixLogAnalytics() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14896 ====");
        log.info("Cross Launch to BMC Helix Log Analytics");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Log Analytics feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            logAnalyticsFeatureMethods = new LogAnalyticsFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Log Analytics");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.LOG_ANALYTICS_TILE);
            log.info("Step 4: Cross launch to Log Analytics completed");

            log.info("Step 5: Verifying Log Analytics login");
            logAnalyticsFeatureMethods.verifyLogin();
            log.info("Step 5: Log Analytics login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14896 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14896 ====");
            log.error("Error during Log Analytics cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Operations Management", singleThreaded = true, groups = {"sanity_monitor", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14898", Products = {Product.ADE_Platform, Product.Operations_Management})
    public void DRSQA_14898_CrossLaunchToBMCHelixOperationsManagement() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14898 ====");
        log.info("Cross Launch to BMC Helix Operations Management");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and BHOM feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            bhomFeatureMethods = new BhomFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Operations Management");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM_TILE);
            log.info("Step 4: Cross launch to Operations Management completed");

            log.info("Step 5: Verifying BHOM login");
            bhomFeatureMethods.verifyLogin();
            log.info("Step 5: BHOM login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14898 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14898 ====");
            log.error("Error during Operations Management cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @Test(description = "Cross Launch to BMC Helix Service Monitoring", singleThreaded = true, groups = {"sanity_aiops", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14899", Products = {Product.ADE_Platform, Product.Service_Monitoring})
    public void DRSQA_14899_CrossLaunchToBMCHelixServiceMonitoring() {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14899 ====");
        log.info("Cross Launch to BMC Helix Service Monitoring");

        try {
            log.info("Step 1: Launching ADE Platform application");
            adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            log.info("Step 1: ADE Platform launched successfully");

            log.info("Step 2: Login to ADE Platform with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            log.info("Step 2: Login successful");

            log.info("Step 3: Initializing Platform and Service Monitoring feature methods");
            platformFeatureMethods = new PlatformFeatureMethods();
            serviceMonitoringFeatureMethods = new ServiceMonitoringFeatureMethods();
            portalFeatureMethods = new Portal_FeatureMethods();
            portalFeatureMethods.verifyLogin();
            log.info("Step 3: Feature methods initialized successfully");

            log.info("Step 4: Cross launching to BMC Helix Service Monitoring");
            platformFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);
            log.info("Step 4: Cross launch to Service Monitoring completed");

            log.info("Step 5: Verifying Service Monitoring login");
            serviceMonitoringFeatureMethods.verifyLogin();
            log.info("Step 5: Service Monitoring login verification successful");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14899 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14899 ====");
            log.error("Error during Service Monitoring cross launch test: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (bwfFeatureMethods != null) {
                bwfFeatureMethods.closeBrowser();
            }
            if (cmdbFeatureMethods != null) {
                cmdbFeatureMethods.closeBrowser();
            }
            if (optimizeFeatureMethods != null) {
                optimizeFeatureMethods.closeBrowser();
            }
            if (dashboadFeatureMethods != null) {
                dashboadFeatureMethods.closeBrowser();
            }
            if (developerToolFeatureMethods != null) {
                developerToolFeatureMethods.closeBrowser();
            }
            if (dwpFeatureMethods != null) {
                dwpFeatureMethods.closeBrowser();
            }
            if (dwpcFeatureMethods != null) {
                dwpcFeatureMethods.closeBrowser();
            }
            if (discoveryFeatureMethods != null) {
                discoveryFeatureMethods.closeBrowser();
            }
            if (innovationSuiteFeatureMethodsPage != null) {
                innovationSuiteFeatureMethodsPage.closeBrowser();
            }
            if (iaFeatureMethod != null) {
                iaFeatureMethod.closeBrowser();
            }
            if (iiFeatureMethods != null) {
                iiFeatureMethods.closeBrowser();
            }
            if (smartITFeatureMethods != null) {
                smartITFeatureMethods.closeBrowser();
            }
            if (logAnalyticsFeatureMethods != null) {
                logAnalyticsFeatureMethods.closeBrowser();
            }
            if (bhomFeatureMethods != null) {
                bhomFeatureMethods.closeBrowser();
            }
            if (serviceMonitoringFeatureMethods != null) {
                serviceMonitoringFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }

}