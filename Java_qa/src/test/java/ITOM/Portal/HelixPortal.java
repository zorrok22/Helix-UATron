package ITOM.Portal;

import framework.Launcher;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import util.SolqaLogger;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;

// No additional collections required; steps are explicit.

public class HelixPortal extends Launcher {
    static SolqaLogger log = new SolqaLogger(HelixPortal.class.getName());
    AdePortalLandingPage adeportallandingpage;
    Portal_FeatureMethods portalFeatureMethods;
    Map<Object, String> PortalTestData;
    ReadJson readJson = new ReadJson();


    @Test(description = "Portal: Verify Application Cross Launch from Application Switcher",groups={"Regression"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-17124",Products = {Product.Helix_Portal})
	public void DRSQA_17124_Verify_Application_Cross_Launch_from_Application_Switcher(ITestContext context) throws IOException{

        log.info("******* START: DRSQA-17124: Portal: Verify Application Cross Launch from Application Switcher *******");
        log.info("------- No Precondition required for this test *******");
       
        log.info("------- Step1:-  Log in to the portal using username pbunyon and password __USER_PASSWORD__*******");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17124");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        context.setAttribute("DRSQA_17124_Driver", getDriver());
        context.setAttribute("DRSQA_17124_Product", Product.Helix_Portal);
        PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"Portal", "TestData", "DRSQA_17124_Data");
        adeportallandingpage = new AdePortalLandingPage(getDriver());
        adeportallandingpage.verifyLoginToADEPlatform();

        log.info("------- Step2:- Click on the Application Switcher icon *******");
        portalFeatureMethods.clickOnApplicationSwitcher();

        log.info("------- Step3:- Verify the dropdown displays BMC Helix applications and Recently Used Applications *******");
        log.info("PortalTestData: " + PortalTestData.get("ApplicationTelemetry"));
        portalFeatureMethods.verifyApplicationSwitcherDropdownIsDisplayed(PortalTestData);

        log.info("------- Step4:- Click on ADE Helix Telemetry and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationTelemetry"));

        log.info("------- Step5:- Click on BMC Helix Automation Console and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationAutomationConsole"));

        log.info("------- Step6:- Click on BMC Helix Business Workflows and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationBusinessWorkflows"));

        log.info("------- Step7:- Click on BMC Helix Continuous Optimization and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationContinuousOptimization"));

        log.info("------- Step8:- Click on BMC Helix Dashboards and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationDashboards"));

        log.info("------- Step9:- Click on BMC Helix Developer Tools and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationDeveloperTools"));

        log.info("------- Step10:- Click on BMC Helix Digital Workplace and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationDigitalWorkplace"));

        log.info("------- Step11:- Click on BMC Helix Digital Workplace Catalog and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationDigitalWorkplaceCatalog"));

        log.info("------- Step12:- Click on BMC Helix Discovery and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationDiscovery"));

        log.info("------- Step13:- Click on BMC Helix Innovation Suite and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationInnovationSuite"));

        log.info("------- Step14:- Click on BMC Helix Intelligent Automation and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationIntelligentAutomation"));

        log.info("------- Step15:- Click on BMC Helix Intelligent Integrations and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationIntelligentIntegrations"));

        log.info("------- Step16:- Click on BMC Helix ITSM and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationITSM"));

        log.info("------- Step17:- Click on BMC Helix Log Analytics and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationLogAnalytics"));

        log.info("------- Step18:- Click on BMC Helix Operations Management and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationOperationsManagement"));

        log.info("------- Step19:- Click on BMC Helix CMDB and verify the page is launched successfully ********");
        portalFeatureMethods.navigationAndVerifyApplicationLaunchedSuccessfully(PortalTestData.get("ApplicationCMDB"));

        log.info("******* END: DRSQA-17124: Portal: Verify Application Cross Launch from Application Switcher *******");
    }

    @Test(description = "Portal: Create Access Key and API Key from User Access and Profile",groups={"Regression"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-17126",Products = {Product.Helix_Portal})
	public void DRSQA_17126_Create_Access_Key_and_API_Key_from_User_Access_and_Profile(ITestContext context) throws IOException, UnsupportedFlavorException{

        log.info("******* START: DRSQA-17126: Portal: Create Access Key and API Key from User Access and Profile *******");
        log.info("------- No Precondition required for this test *******");
       
        log.info("------- Step1:-  Log in to the portal using username pbunyon and password __USER_PASSWORD__*******");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17126");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        context.setAttribute("DRSQA_17126_Driver", getDriver());
        context.setAttribute("DRSQA_17126_Product", Product.Helix_Portal);
        PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"Portal", "TestData", "DRSQA_17126_Data");
        adeportallandingpage = new AdePortalLandingPage(getDriver());
        adeportallandingpage.verifyLoginToADEPlatform();

        log.info("------- Step2:- Navigate to User Access and click on Users and Keys *******");
        log.info("------- Step3:- Click on Access Keys and select Add Key *******");
        log.info("--------Step4:- Enter required details: Key Name, Key Type (Access Key or API Key), and Key Expiry, then proceed");
        log.info("------- Create a new key with the name " + PortalTestData.get("KeyName") + " *******");
        log.info("------- Step5:- Generate the key and copy the key details (shown only once) *******");
        portalFeatureMethods.createNewKey(PortalTestData.get("KeyName")+RandomStringUtils.randomAlphabetic(5));

        log.info("------- Step6:- Select appropriate Groups and Roles as per assignment and save *******");
        portalFeatureMethods.selectAllGroupsAndRoles();

        log.info("------- Step7:- Navigate back to the portal and click on Profile *******");
        portalFeatureMethods.navigateToProfile();

        log.info("------- Step8:- Click Add Key beside My Access Keys *******");
        log.info("**** Generate new secrete key functionality is already verified in Presuite *******");
        portalFeatureMethods.verifyCopyKeyDetails();
        log.info("******* END: DRSQA-17124: Portal: Verify Application Cross Launch from Application Switcher *******");
    }

    @Test(description = "[Agent Outcomes] - Validate cross-launch of **Agent Outcomes** from Helix Portal and IS App Launcher",groups={"Regression"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-19027",Products = {Product.Helix_Portal})
	public void DRSQA_19027_Validate_cross_launch_of_Agent_Outcomes_from_Helix_Portal_and_IS_App_Launcher(ITestContext context) {
        log.info("******* START: DRSQA-19027: [Agent Outcomes] - Validate cross-launch of **Agent Outcomes** from Helix Portal and IS App Launcher *******");
        log.info("------- Precondition:- Agent outcome tile should be enabled in the portal --------");

        log.info("------- Step1:-  Log in to **Helix Portal** as an entitled user*******");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-19027");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        context.setAttribute("DRSQA_19027_Driver", getDriver());
        context.setAttribute("DRSQA_19027_Product", Product.Helix_Portal);
        PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"Portal", "TestData", "DRSQA_19027_Data");
        portalFeatureMethods.verifyLoginToADEPlatform();

        log.info("------- Step2:- Verify the **Agent Outcomes** tile is visible in Helix Portal *******");
        portalFeatureMethods.verifyAgentOutcomesTileIsVisible();

        log.info("------- Step3:- Click the **Agent Outcomes** tile in Helix Portal *******");
        portalFeatureMethods.clickOnAgentOutcomesTile();
   
        log.info("------- Step4:- Validate the launched URL uses the **IS-based service URL** (correct routing) *******");
        portalFeatureMethods.validateLaunchedURLUsesISBasedServiceURL(PortalTestData.get("ExpectedURLContains"));
       
        log.info("--------Step5:- Confirm **Agent Outcomes** landing page loads successfully *******");
        portalFeatureMethods.confirmAgentOutcomesLandingPageLoadsSuccessfully();
   
        log.info("--------Step6:- Log out (or end session) from the launched app/portal *******");
        portalFeatureMethods.closeBrowser();
   
        log.info("--------Step7:- Log in to **Helix Portal** as a non-entitled user *******");
        PortalTestData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +"Portal", "TestData", "DRSQA_19027_Data");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-19027");
		portalFeatureMethods.loginToADEPlatform(PortalTestData.get("NonAccessingUser"), PortalTestData.get("NonAccessingUserPassword"));
        context.setAttribute("DRSQA_19027_Driver", getDriver());
        context.setAttribute("DRSQA_19027_Product", Product.Helix_Portal);
        portalFeatureMethods.verifyLoginToADEPlatform();

        log.info("--------Step8:- Verify the **Agent Outcomes** tile is available to the non-entitled user *******");
        portalFeatureMethods.verifyAgentOutcomesTileIsVisible();

        log.info("--------Step9:- Click the **Agent Outcomes** tile in Helix Portal *******");
        portalFeatureMethods.clickOnAgentOutcomesTile();
        portalFeatureMethods.confirmAgentOutcomesLandingPageNotLoadsSuccessfully();
        portalFeatureMethods.closeBrowser();

        log.info("--------Step10:- Log in to any **Innovation Studio (IS)** based app eg. BWF as an entitled user *******");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-19027");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        context.setAttribute("DRSQA_19027_Driver", getDriver());
        context.setAttribute("DRSQA_19027_Product", Product.Helix_Portal);
        portalFeatureMethods.verifyLoginToADEPlatform();
        portalFeatureMethods.clickOnBWFTile();

        log.info("--------Step11:- Open **IS App Launcher** *******");
        portalFeatureMethods.clickOnApplicationLauncherButton();

        log.info("--------Step12:- Launch **Agent Outcomes** from IS App Launcher *******");
        portalFeatureMethods.launchApplicationFromISAppLauncher(PortalTestData.get("ApplicationName"));
        portalFeatureMethods.confirmAgentOutcomesLandingPageLoadsSuccessfully();
   
        log.info("--------Step13:- Log out (or end session) from IS *******");
        portalFeatureMethods.closeBrowser();
   
        log.info("--------Step14:- Open **IS App Launcher** *******");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-19027");
		portalFeatureMethods.loginToADEPlatform(PortalTestData.get("NonAccessingUser"), PortalTestData.get("NonAccessingUserPassword"));
        context.setAttribute("DRSQA_19027_Driver", getDriver());
        context.setAttribute("DRSQA_19027_Product", Product.Helix_Portal);
        portalFeatureMethods.verifyLoginToADEPlatform();
        portalFeatureMethods.clickOnBWFTile();
        portalFeatureMethods.clickOnApplicationLauncherButton();
        portalFeatureMethods.launchApplicationFromISAppLauncher(PortalTestData.get("ApplicationName"));

        log.info("--------Step15:- Launch **Agent Outcomes** from IS App Launcher *******");
        portalFeatureMethods.confirmAgentOutcomesLandingPageNotLoadsSuccessfully();
        portalFeatureMethods.closeBrowser();
    }


    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        try {
            if (getDriver() != null) {
                getDriver().quit();
                log.info("WebDriver closed successfully.");
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }
}
