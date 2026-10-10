package ITOM.Ade_Platform;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Ade_Portal_Landing.UsersManagementPage;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Platform.PlatformLandingPage;
import util.*;
import java.io.File;
import java.util.*;

public class Ade_UserAccess extends Launcher {

	static SolqaLogger log = new SolqaLogger(Ade_UserAccess.class.getName());
	Map<Object, String> IATestData;
	ReadJson readJson = new ReadJson();
	Portal_FeatureMethods portalFeatureMethods;
	BhomFeatureMethods bhomFeatureMethods;
	Map<String, String> configProp = ReadPropertiesFile
			.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

	@Test(description = "[RBAC] Auth profile to support access control for business and technical services", groups = {
			"ITOM" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17021", Products = { Product.RBAC })
	public void DRSQA_17021_RBAC_Auth_Profile_Access_Control(ITestContext context) throws Exception {
		log.info("*************** Start: DRSQA_17021_RBAC_Auth_Profile_Access_Control ****************");

		boolean userAlreadyExists = false;
		CommonWaits commonWaits = new CommonWaits();

		log.info("Step 1: Load test data");
		Map<Object, String> itomTestData = readJson.getInputValuesMapIncludingLabelField("ITOM", "ITOMTestData",
				"DRSQA_17021_Data");

		String loginId = itomTestData.get("LoginID");

		log.info("Step 2: Launch browser and login to Helix Portal with admin user");
		WebDriver adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
		setDriver(adeDriver);
		login(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"), SolQAConstants.appITOM);
		PlatformLandingPage platformLandingPage = new PlatformLandingPage(getDriver());
		platformLandingPage.verifyPlatformLandingPageOpened();
		context.setAttribute("DRSQA_17021_Driver", getDriver());
		context.setAttribute("DRSQA_17021_Product", Product.RBAC);

		log.info("Step 3: Verify user " + loginId + " is present in portal");
		platformLandingPage.navigateToPage("User access", "Users and keys");
		UsersManagementPage userManagementPage = new UsersManagementPage(getDriver());
		userAlreadyExists = userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(loginId);

		if (userAlreadyExists) {
			log.info("User " + loginId + " exists in the system");
		} else {
			log.info("Pre-requisite User (cchapman) does not exist");
		}

		log.info("Step 4: Update User Groups" + loginId);
		log.info("Search for user and click Edit");
		userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(loginId);

		String userGroup = itomTestData.get("UserGroup");
		userManagementPage.selectUserGroup(userGroup);
		log.info("User group '" + userGroup + "' selected successfully for user: " + loginId);
		
		log.info("Step 5: User Roles for" + loginId);
		String role = itomTestData.get("Role");
		userManagementPage.selectUserRole(role);
		log.info("Role '" + userGroup + "' selected successfully for user: " + loginId);

		log.info("User " + loginId + " is ready for User Groups and User Roles updates");

		log.info("Step 6: Navigate back to ADE Portal home page");
		platformLandingPage.navigateToHomePage();
		log.info("Successfully navigated back to ADE Portal home page");

		log.info("Step 7: Navigate to BHOM (BMC Helix Operations Management)");
		platformLandingPage.crossLaunchProduct(SolQAConstants.BHOM);
		commonWaits.waitForSometime(commonWaits.moreDelay);
		log.info("Successfully navigated to BHOM");

		log.info("Step 8: Navigate to Administration -> Authorization Profiles");
		bhomFeatureMethods = new BhomFeatureMethods();
		bhomFeatureMethods.navigateToAuthProfile();

		log.info("Step 9: Create Authorization Profile");
		String authProfileBaseName = itomTestData.get("AuthProfileBaseName");
		String createdAuthProfileName = bhomFeatureMethods.createAuthProfile(authProfileBaseName, userGroup);
		log.info("Authorization Profile created successfully: " + createdAuthProfileName);
		context.setAttribute("DRSQA_17021_AuthProfileName", createdAuthProfileName);

		log.info("Step 10: Edit Authorization Profile");
		bhomFeatureMethods.editAuthProfile(createdAuthProfileName);
		log.info("Authorization Profile edited successfully: " + createdAuthProfileName);

		log.info("Step 11: Delete Authorization Profile");
		bhomFeatureMethods.deleteAuthProfile(createdAuthProfileName);
		log.info("Authorization Profile deleted successfully: " + createdAuthProfileName);

		log.info("*************** End: DRSQA_17021_RBAC_Auth_Profile_Access_Control ****************");

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
		} catch (Exception e) {
			log.error("Error during cleanup: " + e.getMessage());
		}
	}
}
