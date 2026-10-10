package ITOM.BHOM_AIOps;

import framework.Launcher;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsConfigurationMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import util.*;
import org.testng.annotations.AfterMethod;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AIOPS_Configuration extends Launcher {

	static SolqaLogger log = new SolqaLogger(AIOPS_Configuration.class.getName());
	Portal_FeatureMethods portalFeatureMethods;
	BhomFeatureMethods bhomFeatureMethods;
	AIOpsFeatureMethods aiopsFeatureMethods;
	AIOpsConfigurationMethods aiopsConfigurationMethods;
	Map<Object, String> aiopsTestData;
	ReadJson readJson = new ReadJson();
	Map<String, String> configProp = ReadPropertiesFile
			.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

	private int counter;
	private Map<Object, String> itomUserData;
	private UserUpdater itomUserUpdater;
	private String userKey = "aananda"; // Default user key

	@BeforeTest(alwaysRun = true)
	public void setupITOMUsers() {
		// Update ITOM user data from Excel based on runCounter
		// Always use "itomadminuser" key (ITSM pattern)
		userKey = "itomadminuser";
		log.info("BHOM_AIOps will use userKey: " + userKey);
	}

	@Test(description = "Manage Service Blueprints - Allow enabling and disabling blueprints while creation and editing", groups = {
			"" })
	@SolqaDetails(authorName = "dpatare", JiraID = "DRSQA-15658", Products = {})
	public void DRSQA_15658_Manage_Service_Blueprints(ITestContext context) throws IOException {
		log.info("Start Test case DRSQA-15658_Manage_Service_Blueprints");
		// Step 1: Login to ADE portal
		log.info("---- Login to Portal with correct username and password----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15658");
		bhomFeatureMethods = new BhomFeatureMethods();
		aiopsConfigurationMethods = new AIOpsConfigurationMethods();
		aiopsFeatureMethods = new AIOpsFeatureMethods();
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		// Step 1: Cross launch BHOM
		log.info("Cross launching AIOPS");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
		// Step 2: Navigate to Configuration Tab
		log.info("Navigate to Configuration Tab");

		aiopsFeatureMethods.navigateToConfigurations();
		// Step 3: Navigate to Manage Service Blueprints
		log.info("Navigate to Manage Service Blueprints");
		aiopsConfigurationMethods.navigateToConfigurationsManageServiceBlueprints();
		// Step 4: Create a new blueprint
		log.info("Create a new blueprint");
		String bluePrintName = aiopsConfigurationMethods.createNewBlueprint();

		log.info("SearchBlueprint By name");

		aiopsConfigurationMethods.searchBlueprintByName(bluePrintName);
		// Step 11: Edit the blueprint
		log.info("Edit the blueprint");
		aiopsConfigurationMethods.editBlueprint(bluePrintName);

		log.info("End Test case DRSQA-15658_Manage_Service_Blueprints");

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
