package ITOM;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.*;
import java.io.File;
import java.util.Map;

public class BHOM_IA_ITSM extends Launcher {

    static SolqaLogger log = new SolqaLogger(BHOM_IA_ITSM.class.getName());
    Map<Object, String> IATestData;
    ReadJson readJson = new ReadJson();
    Portal_FeatureMethods portalFeatureMethods;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    SmartITFeatureMethodsPage smartITFeatureMethodsPage;
    BhomFeatureMethods bhomFeatureMethods;
    WebDriver ITOMDriver;
    Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private Map<Object, String> itomUserData;
    private String userKey = "aananda"; // Default user key

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM_Prerequisites.updateITOMUserDataFromExcel(configProp);
        userKey = "itomadminuser";
        log.info("BHOM_IA_ITSM will use user: " + itomUserData.get("UserName"));
    }

    @Test(description = "Verify BHOM-IA-ITSM PSR L1 use case for create incident", singleThreaded = true,
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            groups = {"sanity_monitor", "sanity_intelligentautomation", "sanity_aif", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12437", Products = {Product.ADE_Platform, Product.Helix_SmartIT})
    public void DRSQA_12437_Verify_BHOM_IA_ITSM_PSR_L1_use_case_for_create_incident(ITestContext context) throws Exception {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-12437 ====");
        log.info("Verify BHOM-IA-ITSM PSR L1 use case for create incident");

        try {
            ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(ITOMDriver);
            context.setAttribute("DRSQA_12437_Driver", ITOMDriver);
            context.setAttribute("DRSQA_12437_Product", Product.ADE_Platform);

            log.info("Step 1: Initializing page objects");
            portalFeatureMethods = new Portal_FeatureMethods();
            bhomFeatureMethods = new BhomFeatureMethods();
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            log.info("Step 1: Page objects initialized successfully");

            log.info("Step 2: Login to Portal with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            commonWaits.switchtoDefaultFrame();
            portalFeatureMethods.verifyLogin();
            log.info("Step 2: Login successful");

           //Enable PSR in IA application
           portalFeatureMethods.crossLaunchProduct(SolQAConstants.IA);
           log.info("Cross-launched to IA application and enabling PSR.");
           portalFeatureMethods.enablePSR(CommonMethods.readConfig("CMDBURL"),
                   CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"));

            //Navigate to Portal tab
            portalFeatureMethods.navigateToPortalTab();

            //Navigate to BHOM application
            log.info("Step 5: Navigate to BHOM");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            log.info("Step 5: Successfully navigated to BHOM");

            log.info("Step 6: Create Incident using event - Loading test data");
            IATestData = readJson.getInputValuesMapIncludingLabelField("ITOM", "IATestData", "DRSQA_12437_TestData");
            String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "IA" + File.separator + "DRSQA_12437_Data.json";
            String msg = IATestData.get("msg") + "_" + RandomStringUtils.randomAlphanumeric(5);
            log.info("Step 6.1: Generated message for event: " + msg);
            log.info("Step 6.2: JSON file path: " + JsonContent);
            portalFeatureMethods.injectEventTicketForBHOM(JsonContent, IATestData, msg);
            log.info("Step 6: Event injection completed successfully");

            log.info("Step 7: Get Ticket ID from BHOM");
            log.info("Step 7.1: Searching for events with message: " + msg);
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            commonWaits.waitForPageLoad(ITOMDriver);
            bhomFeatureMethods.searchAndOpenNonInformationEventByMessage(msg);
            String incID = bhomFeatureMethods.getIncidentID();
            Assert.assertFalse(incID.isEmpty(), "Ticket is Empty");
            log.info("Step 7.2: Ticket ID retrieved successfully: " + incID);
            portalFeatureMethods.navigateToPortalTab();
            log.info("Step 7: Navigated back to Portal tab");

            log.info("Step 8: Navigate to ITSM");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
            log.info("Step 8: Successfully navigated to HELIX ITSM");

            log.info("Step 9: Global search the created incident");
            smartITFeatureMethodsPage.globalSearchAndOpenTicket(incID);
            log.info("Step 9: Incident ticket opened successfully");

            log.info("Step 10: Current ITSM priority before BHOM update: " + smartITFeatureMethodsPage.getPriority());

            log.info("Step 11: Modify event priority to 'Highest'");
            portalFeatureMethods.modifyEventForPriority("Highest");
            log.info("Step 11: Event priority modification completed");

           log.info("Step 12: Verify Critical badge next to incident ID in ITSM header");
           portalFeatureMethods.navigateToTab(SolQAConstants.HELIX_ITSM);
           commonWaits.refreshBrowser();
           commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
           commonWaits.switchtoDefaultFrame();
           smartITFeatureMethodsPage.switchToPWAFrame();
           smartITFeatureMethodsPage.verifyTicketHeaderPriorityBadge(SolQAConstants.CRITICAL_SEVERITY);
           log.info("Step 12: Header priority badge verification successful");

            log.info("Step 13: Close event");
            portalFeatureMethods.closeEvent();
            log.info("Step 13: Event closed successfully");

            log.info("Step 14: Verify incident auto-resolved in ITSM after BHOM event close");
            portalFeatureMethods.navigateToTab(SolQAConstants.HELIX_ITSM);
            commonWaits.refreshBrowser();
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            commonWaits.switchtoDefaultFrame();
            smartITFeatureMethodsPage.globalSearchAndOpenTicket(incID);
            smartITFeatureMethodsPage.verifyTicketStatus(SolQAConstants.RESOLVED);
            log.info("Step 14: Incident status verified as Resolved in ITSM");

            log.info("Step 15: Close incident in ITSM");
            commonWaits.switchtoDefaultFrame();
            smartITFeatureMethodsPage.switchToPWAFrame();
            smartITFeatureMethodsPage.selectCustomerOnIncidentViewAndSave(SolQAConstants.Paul_Bunyon);
            smartITFeatureMethodsPage.updateIncidentStatus(
                    SolQAConstants.STATUS_CLOSED,
                    SolQAConstants.FUTURE_ENHANCEMENT,
                    SolQAConstants.Paul_Bunyon);
            smartITFeatureMethodsPage.verifyTicketStatus(SolQAConstants.STATUS_CLOSED);
            log.info("Step 15: Incident closed in ITSM");

            log.info("Step 16: Verify event status in BHOM incident/event context after ITSM close");
            portalFeatureMethods.navigateToTab(SolQAConstants.BHOM);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
            bhomFeatureMethods.searchAndOpenNonInformationEventByMessage(msg);
            Assert.assertTrue(bhomFeatureMethods.getIncidentValue().contains(incID),
                    "Incident ID on BHOM event Others tab should still reference " + incID);
            bhomFeatureMethods.verifyEventsStatus(msg, "Closed");
            log.info("Step 16: BHOM event status and incident linkage verified");

            log.info("Step 17: Close driver and cleanup");
            portalFeatureMethods.closeDriver();
            log.info("Step 17: Driver closed successfully");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-12437 ====");

        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-12437 ====");
            log.error("Error occurred during test execution: " + e.getMessage());
            log.error("Stack trace: ", e);
            throw e;
        }
    }

    @Test(description = "PSR : PSR: L2 Infrastructure Incident using Patrol Agent Based on ADDMIntegrationId", singleThreaded = true,
            dependsOnMethods = {"ITOM.ITOM_Prerequisites.UpdateApiKeysInSolQAConstantsAndGetCINodes"},
            groups = {"sanity_monitor", "sanity_intelligentautomation", "sanity_aif", "sanity"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-14595", Products = {Product.ADE_Platform, Product.Helix_SmartIT})
    public void DRSQA_14595_Infrastructure_Incident_Using_Patrol_Agent_Based_On_ADDMIntegrationId(ITestContext context) throws Exception {
        // Use credentials from @BeforeTest setup (DO NOT reload from JSON)
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-14595 ====");
        log.info("PSR : L2 Infrastructure Incident using Patrol Agent Based in ComponentAlias");

        try {
            ITOMDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(ITOMDriver);
            context.setAttribute("DRSQA_14595_Driver", ITOMDriver);
            context.setAttribute("DRSQA_14595_Product", Product.ADE_Platform);
            log.info("Step 1: Initializing page objects");
            portalFeatureMethods = new Portal_FeatureMethods();
            smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
            bhomFeatureMethods = new BhomFeatureMethods();
            innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
            log.info("Step 1: Page objects initialized successfully");

            log.info("Step 2: Login to Portal with username: " + username);
            login(username, password, SolQAConstants.appITOM);
            portalFeatureMethods.verifyLogin();
            log.info("Step 2: Login successful");

             //Enable PSR in IA application
             portalFeatureMethods.crossLaunchProduct(SolQAConstants.IA);
             log.info("Cross-launched to IA application and enabling PSR.");
             portalFeatureMethods.enablePSR(CommonMethods.readConfig("CMDBURL"),
                     CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"));
 
             //Navigate to Portal tab
             portalFeatureMethods.navigateToPortalTab();

            log.info("Step 5: Navigate to BHOM");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            log.info("Step 5: Successfully navigated to BHOM");

            log.info("Step 6: Create Infrastructure Incident using event - Loading test data");
            IATestData = readJson.getInputValuesMapIncludingLabelField("ITOM", "IATestData", "DRSQA_14595_TestData");
            String JsonContent = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "IA" + File.separator + "DRSQA_14595_Data.json";
            String msg = IATestData.get("msg") + "_" + RandomStringUtils.randomAlphanumeric(5);
            log.info("Step 6.1: Generated message for event: " + msg);
            log.info("Step 6.2: JSON file path: " + JsonContent);
            portalFeatureMethods.injectEventTicketForBHOM(JsonContent, IATestData, msg);
            log.info("Step 6: Infrastructure event injection completed successfully");

            log.info("Step 7: Get Infrastructure Ticket ID from BHOM");
            log.info("Step 7.1: Searching for infrastructure events with message: " + msg);
            bhomFeatureMethods.searchAndOpenEventsByMessage(msg);
            String incID = bhomFeatureMethods.getIncidentID();
            Assert.assertFalse(incID.isEmpty(), "Infrastructure Ticket is Empty");
            log.info("Step 7.2: Infrastructure Ticket ID retrieved successfully: " + incID);
            portalFeatureMethods.navigateToPortalTab();
            log.info("Step 7: Navigated back to Portal tab");

            log.info("Step 8: Navigate to ITSM");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
            log.info("Step 8: Successfully navigated to HELIX ITSM");

            log.info("Step 9: Global search the created incident");
            smartITFeatureMethodsPage.globalSearchAndOpenTicket(incID);
            log.info("Step 9: Infrastructure incident ticket opened successfully");

            log.info("Step 10: Get current priority before updating event");
            String priorityBeforeUpdatingEvent = smartITFeatureMethodsPage.getPriority();

            log.info("Verify source hostname in activity and CI Field");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            smartITFeatureMethodsPage.verifyActivityNotes(IATestData.get("added by note"));
            smartITFeatureMethodsPage.verifyFieldValueOnTicketViewPage("CI", SolQAConstants.PA_AGENT);
            log.info("Step 10: Priority before updating event: " + priorityBeforeUpdatingEvent);

            log.info("Step 12: Modify infrastructure event priority to 'Highest'");
            portalFeatureMethods.modifyEventForPriority(SolQAConstants.HIGHEST);
            log.info("Step 12: Infrastructure event priority modification completed");

            log.info("Step 13: Verify Priority is updated in ITSM");
            portalFeatureMethods.navigateToTab(SolQAConstants.HELIX_ITSM);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

            String priorityAfterUpdatingEvent = smartITFeatureMethodsPage.getPriority();
            log.info("Step 13.1: Priority after updating event: " + priorityAfterUpdatingEvent);
            Assert.assertFalse(priorityBeforeUpdatingEvent.equalsIgnoreCase(priorityAfterUpdatingEvent), "Priority after update should not be same");
            log.info("Step 13: Priority update verification successful");

            log.info("Step 14: Close infrastructure event");
            portalFeatureMethods.closeEvent();
            log.info("Step 14: Infrastructure event closed successfully");

            log.info("Step 15: Move infrastructure ticket to RESOLVED in ITSM, then verify");
            portalFeatureMethods.navigateToTab(SolQAConstants.HELIX_ITSM);
            smartITFeatureMethodsPage.verifyTicketStatus(SolQAConstants.RESOLVED);
            log.info("Step 15: Infrastructure ticket status verification completed - Status: RESOLVED");

            log.info("Step 16: Close driver and cleanup");
            portalFeatureMethods.closeDriver();
            log.info("Step 16: Driver closed successfully");

            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-14595 ====");

        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-14595 ====");
            log.error("Error occurred during infrastructure test execution: " + e.getMessage());
            log.error("Stack trace: ", e);
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (portalFeatureMethods != null) {
                portalFeatureMethods.closeBrowser();
            }
            if (innovationSuiteFeatureMethodsPage != null) {
                innovationSuiteFeatureMethodsPage.closeBrowser();
            }
            if (smartITFeatureMethodsPage != null) {
                smartITFeatureMethodsPage.closeBrowser();
            }
            if (bhomFeatureMethods != null) {
                bhomFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }

}