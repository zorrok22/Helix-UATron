package ITOM.Aiops;

import ITOM.Sanity_Prerequisites;
import framework.Launcher;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import util.*;
import java.io.File;
import java.util.Map;

public class AiopsServiceModelTest extends Launcher {

    public static SolqaLogger log = new SolqaLogger(AiopsServiceModelTest.class.getName());
    WebDriver driver;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    Map<Object, String> aiopsTestData;
    String serviceName = null;
    ReadJson readJson;
    IAFeatureMethod iaFeatureMethod;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    Aiops aiopsApiCore;
    private Map<Object, String> itomUserData;
    private String userKey = "aananda"; // Default user key
    CommonMethods commonmethod = new CommonMethods();

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(config);
        userKey = "itomadminuser";
        log.info("AiopsServiceModelTest will use user: " + itomUserData.get("UserName"));
    }

    public void doPreconditions() {
        driver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(driver);
        iaFeatureMethod = new IAFeatureMethod();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        aiopsApiCore = new Aiops();
        readJson = new ReadJson();
    }

    @Test(description = "Create new Business Service and verify the topology view", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.sanityPrerequisites"}, groups = {"aiops"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12421", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12421_Create_New_Business_Service_Verify_TopologyView(ITestContext context) throws Exception {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("------- START: DRSQA_12421 Create new Business Service and verify the topology view");
        doPreconditions();
        context.setAttribute("12421_Product", Product.Service_Monitoring);
        context.setAttribute("12421_WebDriver", driver);

        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsTestData",
                "DRSQA_12421_testData");
        String kind_is_namespace = aiopsTestData.get("namespace");

        login(username, password, SolQAConstants.appITOM);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();
        aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

        String DRSQA_12421_ServiceName = "DRSQA_12421_TopologyService"+ "_" + RandomStringUtils.randomAlphanumeric(5);


        log.info("Creating a new service through AIOps: " + DRSQA_12421_ServiceName);
        String ciName = SolQAConstants.PA_AGENT;

        aiOpsFeatureMethods.openService(DRSQA_12421_ServiceName);

        log.info("------ Open service and check topology view and node details before events send ----------");
        ciName = ciName != null && ciName.endsWith(".example.com")
                ? ciName.substring(0, ciName.length() - ".example.com".length())
                : ciName;
        String node_name = aiOpsFeatureMethods.VerifyAiopsCiTopologyNodePresentByKindBeforeAnyEventSentToService(kind_is_namespace, ciName, aiopsTestData);

        log.info("--------send 3 Events with unique message-------");
        int noOfEvents = aiOpsFeatureMethods.sendEventForTopology(node_name);

        log.info("------ Open service and check topology view and node details after events send ----------");
        aiOpsFeatureMethods.navigateToServicesTab();
        aiOpsFeatureMethods.openService(DRSQA_12421_ServiceName);
        aiOpsFeatureMethods.VerifyAiopsCiTopologyNodePresentByKindAfterEventSentToService(kind_is_namespace, ciName, aiopsTestData.get("Topology"), noOfEvents, aiopsTestData);

        aiOpsFeatureMethods.refreshBrowser();
        aiOpsFeatureMethods.navigateToServicesTab();
        aiOpsFeatureMethods.deleteServiceUsingUI(DRSQA_12421_ServiceName);
    }

	@Test(description = "Validate CI's are grouped by kind and type in topology if topology-aggregate-by-kind-type feature flag is enabled", singleThreaded = true, groups = {
			"Regression" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17445", Products = { Product.Service_Monitoring,
			Product.Helix_Discovery })
	public void DRSQA_17445_Validate_CIs_Grouped_By_Kind_And_Type_In_Topology_With_FeatureFlag(ITestContext context)
			throws Exception {
		// Use credentials from @BeforeTest setup
		String username = itomUserData.get("UserName");
		String password = itomUserData.get("Password");
		log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

		log.info(
				"------- START: DRSQA_17445 Validate CI's are grouped by kind and type in topology if topology-aggregate-by-kind-type feature flag is enabled");
		context.setAttribute("17445_Product", Product.Service_Monitoring);
		context.setAttribute("17445_WebDriver", driver);
		doPreconditions();

		login(username, password, SolQAConstants.appITOM);
		aiOpsFeatureMethods.verifyLoginToADEPlatform();

		// Get JWT token for API calls
		String jwtToken = commonmethod.refreshJwtToken(null);
		log.info("Successfully obtained JWT token");

		aiopsTestData = readJson.getInputValuesMapIncludingLabelField(
				File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsTestData",
				"DRSQA_17445_testData");

		// Check if topology-aggregate-by-kind-type feature flag is enabled
		String featureFlagName = aiopsTestData.get("FeatureFlag");
		boolean isFeatureFlagEnabled = aiopsApiCore.getTenantFeatureFlagStatus(featureFlagName, jwtToken);

		if (!isFeatureFlagEnabled) {
			log.info("'" + featureFlagName + "' feature flag is not enabled; skipping test as passed.");
			log.pass("Test skipped as '" + featureFlagName + "' feature flag is not enabled");
			return;
		}

		log.info("'" + featureFlagName + "' feature flag is enabled. Proceeding with topology validation...");

		// Navigate to AIOps application and Services tab
		aiOpsFeatureMethods.navigateToAiopsApplicationAndClickOnServicesTab();

		// Open existing service
		String serviceName = aiopsTestData.get("ServiceName");
		log.info("Opening existing service: " + serviceName);
		aiOpsFeatureMethods.openService(serviceName);

		// Navigate to CI Topology tab
		log.info("Navigating to CI Topology tab");
		aiOpsFeatureMethods.selectTopologyTabOnserviceDetailsPage(aiopsTestData.get("TopologyTab"));

		// Verify topology aggregation is active (CIs grouped by kind and type)
		log.info("Verifying topology aggregation is active for grouping by kind and type");
		boolean isAggregationActive = aiOpsFeatureMethods
				.aiopsCiTopologyNodePresentByKindAndType(aiopsTestData.get("KindAndType"));

		if (isAggregationActive) {
			log.pass("Successfully validated that CI's are grouped by kind and type in topology with feature flag '"
					+ featureFlagName + "' enabled. Aggregation button is active.");
		} else {
			log.error("Topology aggregation is not active. CI's are NOT grouped by kind and type despite feature flag '"
					+ featureFlagName + "' being enabled.");
		}

		log.info("------- END: DRSQA_17445 Validate CI's are grouped by kind and type in topology");

	}

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (iaFeatureMethod != null) {
            iaFeatureMethod.closeBrowser();
        }
        if (aiOpsFeatureMethods != null) {
            aiOpsFeatureMethods.closeBrowser();
        }
    }

}