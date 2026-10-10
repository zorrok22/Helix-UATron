package ITOM.Ade_Platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Ade_Portal_Landing.GroupManagementPage;
import pages.ITOM.Ade_Portal_Landing.UsersManagementPage;
import pages.ITOM.Itsm_Insights.ITSMInsightsFeatureMethods;
import util.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WebhookTests extends Launcher {

    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") +
            File.separator + "properties.config");
    static SolqaLogger log = new SolqaLogger(WebhookTests.class.getName());
    String app = SolQAConstants.appITOM;
    CommonWaits commonWaits;
    ReadJson readJson;
    Map<Object, String> adeTestData;
    String adeApp = SolQAConstants.WEBHOOK;
    AdePortalLandingPage platformLaunchPage;
    GroupManagementPage groupManagementPage;
    UsersManagementPage userManagementPage;
    ITSMInsightsFeatureMethods itsmInsightsFeatureMethods;

    public void doPreconditions() {
        launchWeb(app, app);
        setDriver(getDriver());
        commonWaits=new CommonWaits();
        readJson = new ReadJson();
        platformLaunchPage = new AdePortalLandingPage(getDriver());
        groupManagementPage = new GroupManagementPage(getDriver());
        itsmInsightsFeatureMethods = new ITSMInsightsFeatureMethods();
        userManagementPage = new UsersManagementPage(getDriver());
    }

    public void navigateGroup(String groupName) {
        log.info("--- Opening Group Management section from Common services dropdown ------");
        String groupLaunchPageURL = platformLaunchPage.clickOnDropdownAndSelectOption("User groups");
        log.info("------ URL after clicking on group management- "+ groupLaunchPageURL);
        Assert.assertTrue(groupLaunchPageURL.contains("groups"),"User is not redirected to groups page");
        groupManagementPage.EnterGroupNameInSearchBoxAndVerifyEntry(groupName);
    }

    public void performActionOnGroup(String groupName, String action) {
        groupManagementPage.clickOnDropdownAndSelectAction(action);
        String editorName = groupManagementPage.getEditorHeader();
        Assert.assertTrue(editorName.equalsIgnoreCase(groupName),"Group option editor is not opened correctly");
    }

    @Test(description = "[IMS] [Sync-Webhook] A new user created in ITSM with Fixed, Floating or Bundled license with Incident / Problem permission gets synced to ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12358", Products = {Product.ADE_Platform})
    public void DRSQA_12358_New_user_with_Fixed_license_with_Incident_Problem_permissions(ITestContext context) throws Exception {

        doPreconditions();

        String userID1 = "", userID2 = "", userID3 = "", userID4 = "", userID5 = "";
        try {
            log.info("------- START: [IMS] [Sync-Webhook] A new user created in ITSM with Fixed, Floating or Bundled license with Incident / Problem permission gets synced to ADE-IMS ------------");
            log.info("Step 1 - Creating a fixed licensed user in ITSM with Problem related permissions");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12358_TestData_user_fixed");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);

            // Replacement values passed dynamically (from method args, DB, etc.)
            String remedyLoginId1 = adeTestData.get("LoginID");;
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String company1 = adeTestData.get("Company");
            String email1 = adeTestData.get("EmailAddress");
            String licenseType1 = SolQAConstants.FIXED_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);

            log.info("Step 2 - Creating a floating licensed user in ITSM with Incident related permissions");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12358_TestData_user_floating");
            String remedyLoginId2 = adeTestData.get("LoginID");;
            String firstName2 = adeTestData.get("FirstName");
            String lastName2 = adeTestData.get("LastName");
            String company2 = adeTestData.get("Company");
            String email2 = adeTestData.get("EmailAddress");
            String licenseType2 = SolQAConstants.FLOATING_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements2 = new LinkedHashMap<>();
            replacements2.put("_remedy_login_id_", remedyLoginId2);
            replacements2.put("__First_Name__", firstName2);
            replacements2.put("__Last_Name__", lastName2);
            replacements2.put("__Company__", company2);
            replacements2.put("_email_", email2);
            replacements2.put("__License_Type__", licenseType2);

            // Build and replace placeholders
            String finalJson2 = buildJsonWithReplacements(payloadFile, replacements2);
            userID2 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson2);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId2, userID2, SolQAConstants.INCIDENT_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId2, userID2, SolQAConstants.INCIDENT_MASTER);

            log.info("Step 3 - Creating a config licensed user in ITSM with Incident and infra related permissions");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12358_TestData_user_config");

            String remedyLoginId3 = adeTestData.get("LoginID");;
            String firstName3 = adeTestData.get("FirstName");
            String lastName3 = adeTestData.get("LastName");
            String company3 = adeTestData.get("Company");
            String email3 = adeTestData.get("EmailAddress");
            String licenseType3 = SolQAConstants.FIXED_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements3 = new LinkedHashMap<>();
            replacements3.put("_remedy_login_id_", remedyLoginId3);
            replacements3.put("__First_Name__", firstName3);
            replacements3.put("__Last_Name__", lastName3);
            replacements3.put("__Company__", company3);
            replacements3.put("_email_", email3);
            replacements3.put("__License_Type__", licenseType3);

            // Build and replace placeholders
            String finalJson3 = buildJsonWithReplacements(payloadFile, replacements3);
            userID3 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson3);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId3, userID3, SolQAConstants.INCIDENT_CONFIG);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId3, userID3, SolQAConstants.INFRA_CHANGE_CONFIG);

            log.info("Step 4 - Creating a read licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12358_TestData_user_read");

            String remedyLoginId4 = adeTestData.get("LoginID");;
            String firstName4 = adeTestData.get("FirstName");
            String lastName4 = adeTestData.get("LastName");
            String company4 = adeTestData.get("Company");
            String email4 = adeTestData.get("EmailAddress");
            String licenseType4 = SolQAConstants.READ_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements4 = new LinkedHashMap<>();
            replacements4.put("_remedy_login_id_", remedyLoginId4);
            replacements4.put("__First_Name__", firstName4);
            replacements4.put("__Last_Name__", lastName4);
            replacements4.put("__Company__", company4);
            replacements4.put("_email_", email4);
            replacements4.put("__License_Type__", licenseType4);

            // Build and replace placeholders
            String finalJson4 = buildJsonWithReplacements(payloadFile, replacements4);
            userID4 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson4);


            log.info("Step 5 - Creating a duplicate fixed licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12358_TestData_duplicate_user");

            String remedyLoginId5 = adeTestData.get("LoginID");;
            String firstName5 = adeTestData.get("FirstName");
            String lastName5 = adeTestData.get("LastName");
            String company5 = adeTestData.get("Company");
            String email5 = adeTestData.get("EmailAddress");
            String licenseType5 = SolQAConstants.FIXED_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements5 = new LinkedHashMap<>();
            replacements5.put("_remedy_login_id_", remedyLoginId5);
            replacements5.put("__First_Name__", firstName5);
            replacements5.put("__Last_Name__", lastName5);
            replacements5.put("__Company__", company5);
            replacements5.put("_email_", email5);
            replacements5.put("__License_Type__", licenseType5);

            // Build and replace placeholders
            String finalJson5 = buildJsonWithReplacements(payloadFile, replacements5);
            userID5 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson5);


            log.info("Step 5 - Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 3L);

            log.info("Step 6 - Login to Helix Portal with admin user for tenant where sync was performed.");
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("Step 7 - Go to licensed user. User created in Step1,2, 3 & 5 should be part of Licensed User Group. User created in Step4 should NOT be listed.");

            navigateGroup(SolQAConstants.LICENSE_USER);
            performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedlicenseuser User is not found in the Licensed group.");

            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId2), "floatinglicenseuser User is not found in the Licensed group.");

            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId3), "configlicenseuser User is not found in the Licensed group.");

            Assert.assertFalse(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId4), "readlicenseuser User is found in the Licensed group.");

            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId5), "duplicatelicenseuser User is not found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

            log.info("Step 8 - User created in Step1 should be part of Problem Identification User group..");
            navigateGroup(SolQAConstants.PROBLEM_IDENTIFICATION_USER);
            performActionOnGroup(SolQAConstants.PROBLEM_IDENTIFICATION_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();
            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedlicenseuser User is not found in the Problem Identification User group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

            log.info("Step 9 - User created in Step2 should be part of Incident Correlation User group.");
            navigateGroup(SolQAConstants.INCIDENT_CORRELATION_USER);
            performActionOnGroup(SolQAConstants.INCIDENT_CORRELATION_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();
            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId2), "floatinglicenseuser User is not found in the Incident Correlation User group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

            log.info("Step 10 - User created in Step3 should be part of ITSM Insight Administrators group.");
            navigateGroup(SolQAConstants.ITSM_INSIGHT_ADMINISTRATOR);
            performActionOnGroup(SolQAConstants.ITSM_INSIGHT_ADMINISTRATOR, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();
            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId3), "configlicenseuser User is not found in the ITSM Insight Administrators group.");
        }
        finally {
            try{
                log.info("Postcondition - Deleting all the users created in ITSM");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID2);
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID3);
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID4);
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID5);
                log.info("Deleted all the users created in ITSM");
            }
            catch (Exception e){
                log.error("Error while deleting ITSM users: " + Arrays.toString(e.getStackTrace()));
            }
        }


    }

    @Test(description = "[IMS] [Sync-Webhook] An existing license user with Problem permission is deleted in RemedyITSM is deleted from ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12299", Products = {Product.ADE_Platform})
    public void DRSQA_12299_Delete_Existing_user_with_Incident_Problem_permissions(ITestContext context) throws Exception {

        doPreconditions();
        String userID1 = "";
        log.info("------- START: [IMS] [Sync-Webhook] An existing license user with Incident/Problem permission is deleted in RemedyITSM is deleted from ADE-IMS ------------");
        log.info("Creating a Floating licensed user in ITSM with Problem related permissions");
        adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "TTWeb_8576_TestData_user_floating");

        // Input payload JSON file with placeholders
        String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
        File payloadFile = new File(JsonPath);
        String postfix = RandomStringUtils.randomNumeric(4);
        String remedyLoginId1 = adeTestData.get("LoginID").replace("8576", postfix);
        String firstName1 = adeTestData.get("FirstName").replace("8576", postfix);
        String lastName1 = adeTestData.get("LastName").replace("8576", postfix);
        String email1 = adeTestData.get("EmailAddress").replace("8576", postfix);
        String company1 = adeTestData.get("Company");
        String licenseType1 = SolQAConstants.FLOATING_LICENSE;

        try {

            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();


            log.info("----- Navigate to Users ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is not matched with the searched one");

            log.info("----- Navigate to User Groups ----- ");
            navigateGroup(SolQAConstants.LICENSE_USER);
            performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            log.info("Floating license username is -----> " + remedyLoginId1);
            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "floatinglicenseuser User is not found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();


        }
        finally {
            try{
                log.info("****** deleting created user");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                log.info("Deleted all the users created in ITSM");
            }
            catch (Exception e){
                log.error("Error while deleting ITSM users in DRSQA_12299: " + Arrays.toString(e.getStackTrace()));
            }
        }

        log.info("Wait for 10 seconds for users to get synced.");

        commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

        log.info("----- Navigate to Users ----- " );
        platformLaunchPage.clickOnDropdownAndSelectOption("Users");
        Assert.assertFalse(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is matched with the searched one");


        log.info("----- Navigate to User Groups ----- ");
        navigateGroup(SolQAConstants.LICENSE_USER);
        performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
        groupManagementPage.clickingOnAssignedUserCount();

        Assert.assertFalse(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "floatinglicenseuser User is found in the Licensed group.");

        groupManagementPage.cancelThePanel();
        groupManagementPage.closeThePanel();

    }

    @Test(description = "[IMS] [Sync-Webhook] An existing non-license (Read) user is updated by changing the License Type to Fixed with Incident/Problem permissions in ITSM is synced as new user to ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12300", Products = {Product.ADE_Platform})
    public void DRSQA_12300_An_Existing_readUser_updated_with_fixed_fixedLicense_with_Incident_Problem_permissions_sync_as_new_user(ITestContext context) throws Exception {
        doPreconditions();

        String userID1 = "";
        try {
            log.info("Creating a read licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12300_TestData_user_read");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);
            String postfix = RandomStringUtils.randomNumeric(4);
            String remedyLoginId1 = adeTestData.get("LoginID").replace("DRSQA_12300", postfix);
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String email1 = adeTestData.get("EmailAddress");
            String company1 = adeTestData.get("Company");
            String licenseType1 = SolQAConstants.READ_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("----- Navigate to Users and read only access ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertFalse(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is matched with the searched one");

            log.info("-------- update the user with fixed license");
            itsmInsightsFeatureMethods.updateLicenseForUserInCTMForm(userID1, SolQAConstants.FIXED_LICENSE);

            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);

            log.info("------- Wait for 10 seconds for users to get synced after update");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("-------- Navigate to Users and read only access updated with fixedLicense ----- " );

            log.info("------- Fixed license user is " + remedyLoginId1);

            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is matched with the searched one");

            log.info("----- Navigate to User Groups ----- ");
            navigateGroup(SolQAConstants.LICENSE_USER);
            performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "readOnlyUser User is found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

        } finally {
            try{
                log.info("****** deleting created user");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                log.info("Deleted all the users created in ITSM");
            }
            catch (Exception e){
                log.error("Error while deleting ITSM users in DRSQA_12300: " + Arrays.toString(e.getStackTrace()));
            }
        }

    }

    @Test(description = "[IMS] [Sync-Webhook] An existing licensed user is updated by changing the License Type to Read in RemedyITSM needs to be synced to ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12301", Products = {Product.ADE_Platform})
    public void DRSQA_12301_An_Existing_fixedUser_updated_with_read_readLicense_with_Incident_Problem_permissions(ITestContext context) throws Exception {

        doPreconditions();
        String userID1 = "";
        try {
            log.info("Creating a fixed licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12301_TestData_user_fixed");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);
            String postfix = RandomStringUtils.randomNumeric(4);
            String remedyLoginId1 = adeTestData.get("LoginID").replace("8578", postfix);
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String email1 = adeTestData.get("EmailAddress");
            String company1 = adeTestData.get("Company");
            String licenseType1 = SolQAConstants.FIXED_LICENSE;


            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);


            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);


            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");

            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("----- Navigate to Users and fixed license access ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is not matched with the searched one");


            log.info("-------- update the user with read license");
            itsmInsightsFeatureMethods.updateLicenseForUserInCTMForm(userID1, SolQAConstants.READ_LICENSE);

            log.info("------- Wait for 10 seconds for users to get synced after update");
            commonWaits.waitForSometime(commonWaits.moreDelay * 6L);

            log.info("-------- Navigate to Users and fixed license is updated with read ----- " );

            log.info("------- Fixed license user is " + remedyLoginId1);

            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is not matched with the searched one");

            log.info("----- Navigate to User Groups ----- ");
            navigateGroup(SolQAConstants.LICENSE_USER);
            performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            Assert.assertFalse(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedUser User is found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

        } finally {
            try{
                log.info("****** deleting created user");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                log.info("Deleted all the users created in ITSM");
            }
            catch (Exception e){
                log.error("Error while deleting ITSM users in DRSQA_12301: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }


    @Test(description = "[IMS] [Sync-Webhook] Login Name is changed in RemedyITSM using Data Wizard should be synced to ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12302", Products = {Product.ADE_Platform})
    public void DRSQA_12302_Login_name_Changed_using_DataWizard(ITestContext context) throws Exception {

        doPreconditions();
        String userID1 = "";
        try {
            log.info("Creating a fixed licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12302_TestData_user_fixed");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);
            String postfix = RandomStringUtils.randomNumeric(4);
            String remedyLoginId1 = adeTestData.get("LoginID").replace("8579", postfix);
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String email1 = adeTestData.get("EmailAddress");
            String company1 = adeTestData.get("Company");
            String licenseType1 = SolQAConstants.FIXED_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);


            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);
            log.info("old fixedlicenseuser -------> "+remedyLoginId1);

            String newLoginID = adeTestData.get("newLoginName") + RandomStringUtils.randomAlphanumeric(5);
            log.info(remedyLoginId1 + " updating login name to --> " + newLoginID);
            itsmInsightsFeatureMethods.updateLoginIDForUserInCTMForm(remedyLoginId1,newLoginID);

            log.info(remedyLoginId1 + " updated login ID to --> " + newLoginID);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 15L);
            log.info("old fixedlicenseuser -------> "+remedyLoginId1);

            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("----- Navigate to Users and search user with new login id ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(newLoginID), "Entered userName is not matched with the searched one");

        }
        finally {
            log.info("****** deleting created user");
            itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
            log.info("Deleted all the users created in ITSM");
        }
    }

    @Test(description = "[IMS] [Sync-Webhook] First or Last name of a user with a Login Name is changed should be synced into ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12303", Products = {Product.ADE_Platform})
    public void DRSQA_12303_Update_first_or_last_name_Changed(ITestContext context) throws Exception {

        doPreconditions();
        String userID1 = "";

        try {
            log.info("Creating a fixed licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12303_TestData_user_fixed");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);
            String postfix = RandomStringUtils.randomNumeric(4);
            String remedyLoginId1 = adeTestData.get("LoginID").replace("8580", postfix);
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String email1 = adeTestData.get("EmailAddress");
            String company1 = adeTestData.get("Company");
            String licenseType1 = SolQAConstants.FIXED_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);

            String newName = adeTestData.get("newName");
            log.info(firstName1 + " updating first name to --> " + newName);
            itsmInsightsFeatureMethods.updateLoginNameForUserInCTMForm(userID1, newName);
            log.info(firstName1 + " updated new name to --> " + newName);


            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);
            log.info("old fixedlicenseuser -------> "+firstName1);

            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");

            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("----- Navigate to Users and search user with new login id ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterNameInSearchBoxAndVerifyEntry(newName + " R " + lastName1),
                    "Entered userName is not matched with the searched one");

            log.info("----- Navigate to User Groups and search user with new login ids----- ");
            navigateGroup(SolQAConstants.LICENSE_USER);
            performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedUser User is not found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();
        }
        finally {
            try{
                log.info("****** deleting created user");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                log.info("Deleted all the users created in ITSM");
            }
            catch (Exception e){
                log.error("Error while deleting ITSM users in DRSQA_12303: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Test(description = "[IMS] [Sync-Webhook] Internet Email Address of a user with a Login Name is changed is synced into ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12304", Products = {Product.ADE_Platform})
    public void DRSQA_12304_Update_email_address_of_user(ITestContext context) throws Exception {

        doPreconditions();
        String userID1 = "";

        try {
            log.info("Creating a fixed licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12304_TestData_user_fixed");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);
            String postfix = RandomStringUtils.randomNumeric(4);
            String remedyLoginId1 = adeTestData.get("LoginID").replace("8581", postfix);
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String email1 = adeTestData.get("EmailAddress");
            String company1 = adeTestData.get("Company");
            String licenseType1 = SolQAConstants.FIXED_LICENSE;

            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);

            String newEmail = adeTestData.get("newEmail");
            itsmInsightsFeatureMethods.updateEmailForUserInCTMForm(userID1, newEmail);

            log.info(" updated email to --> " + newEmail);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("----- Navigate to Users and search user with new login id ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterNameInSearchBoxAndVerifyEntry(firstName1 + " R " + lastName1),
                    "Entered userName is not matched with the searched one");

            String actualEmail = itsmInsightsFeatureMethods.getEmailOfUserInCTMForm(userID1);

            Assert.assertTrue(actualEmail.equalsIgnoreCase(newEmail), newEmail);

        }
        finally {
            try{
                log.info("****** deleting created user");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                log.info("Deleted all the users created in ITSM");

            }
            catch (Exception e){
                log.error("Error while deleting ITSM users in DRSQA_12303: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Test(description = "[IMS] [Sync-Webhook] Remove Incident/Problem permission from an existing licensed user in RemedyITSM needs to be synced/removed from respective groups in ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12306", Products = {Product.ADE_Platform})
    public void DRSQA_12306_Permissions_of_Existing_user(ITestContext context) throws Exception {

        doPreconditions();
        String userID1 = "";

        try {
            log.info("Creating a fixed licensed user in ITSM");
            adeTestData = readJson.getInputValuesMapIncludingLabelField(adeApp, "adeTestData", "DRSQA_12306_TestData_user_fixed");

            // Input payload JSON file with placeholders
            String JsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "WEBHOOK" +File.separator + "createWebhookUser.json";
            File payloadFile = new File(JsonPath);
            String postfix = RandomStringUtils.randomNumeric(4);
            String remedyLoginId1 = adeTestData.get("LoginID").replace("8594", postfix);
            String firstName1 = adeTestData.get("FirstName");
            String lastName1 = adeTestData.get("LastName");
            String email1 = adeTestData.get("EmailAddress");
            String company1 = adeTestData.get("Company");
            String licenseType1 = SolQAConstants.FIXED_LICENSE;


            // Create map of placeholders → actual values
            Map<String, String> replacements = new LinkedHashMap<>();
            replacements.put("_remedy_login_id_", remedyLoginId1);
            replacements.put("__First_Name__", firstName1);
            replacements.put("__Last_Name__", lastName1);
            replacements.put("__Company__", company1);
            replacements.put("_email_", email1);
            replacements.put("__License_Type__", licenseType1);

            // Build and replace placeholders
            String finalJson = buildJsonWithReplacements(payloadFile, replacements);
            userID1 = itsmInsightsFeatureMethods.createUserInCTMForm(finalJson);

            // Try to add permission to this user
            String groupID1 = itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_USER);
            String groupID2 = itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.PROBLEM_MASTER);
            String groupID3 = itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.INCIDENT_USER);
            String groupID4 = itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.INCIDENT_MASTER);
            String groupID5 = itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.INCIDENT_CONFIG);
            String groupID6 = itsmInsightsFeatureMethods.addPermissionsToUserInCTMForm(remedyLoginId1, userID1, SolQAConstants.INFRA_CHANGE_CONFIG);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("Login to Helix Portal with admin user for tenant where sync was performed.");
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            platformLaunchPage.verifyLoginToADEPlatform();

            log.info("----- Navigate to Users ----- " );
            platformLaunchPage.clickOnDropdownAndSelectOption("Users");
            Assert.assertTrue(userManagementPage.enterUsernameInSearchBoxAndVerifyEntry(remedyLoginId1), "Entered userName is not matched with the searched one");

            log.info("----- Navigate to User Groups ----- ");
            navigateGroup(SolQAConstants.LICENSE_USER);
            performActionOnGroup(SolQAConstants.LICENSE_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            log.info("fixed license username is -----> " + remedyLoginId1);
            Assert.assertTrue(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedlicenseuser User is not found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

            log.info("Updating the user group permissions ---> remove Problem permission");
            itsmInsightsFeatureMethods.deletePermission(groupID1);
            itsmInsightsFeatureMethods.deletePermission(groupID2);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("----- Navigate to Problem Identification User ----- ");
            navigateGroup(SolQAConstants.PROBLEM_IDENTIFICATION_USER);
            performActionOnGroup(SolQAConstants.PROBLEM_IDENTIFICATION_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            log.info("fixed license username is -----> " + remedyLoginId1);
            Assert.assertFalse(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedlicenseuser User is not found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();

            log.info("Updating the user group permissions ---> remove Incident permission");
            itsmInsightsFeatureMethods.deletePermission(groupID3);
            itsmInsightsFeatureMethods.deletePermission(groupID4);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("----- Navigate to Incident Correlation User ----- ");
            navigateGroup(SolQAConstants.INCIDENT_CORRELATION_USER);
            performActionOnGroup(SolQAConstants.INCIDENT_CORRELATION_USER, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            log.info("fixed license username is -----> " + remedyLoginId1);
            Assert.assertFalse(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedlicenseuser User is not found in the Licensed group.");

            groupManagementPage.cancelThePanel();
            groupManagementPage.closeThePanel();


            log.info("Updating the user group permissions ---> remove Incident config permission");
            itsmInsightsFeatureMethods.deletePermission(groupID5);
            itsmInsightsFeatureMethods.deletePermission(groupID6);

            log.info("Wait for 10 seconds for users to get synced.");
            commonWaits.waitForSometime(commonWaits.moreDelay * 2L);

            log.info("----- Navigate to Incident Correlation User ----- ");
            navigateGroup(SolQAConstants.ITSM_INSIGHT_ADMINISTRATOR);
            performActionOnGroup(SolQAConstants.ITSM_INSIGHT_ADMINISTRATOR, "Edit");
            groupManagementPage.clickingOnAssignedUserCount();

            log.info("fixed license username is -----> " + remedyLoginId1);
            Assert.assertFalse(groupManagementPage.verifyUsernameFromAssignedList(remedyLoginId1), "fixedlicenseuser User is not found in the Licensed group.");


        }
        finally {
            try{
                log.info("****** deleting created user");
                itsmInsightsFeatureMethods.deleteUserInCTMForm(userID1);
                log.info("Deleted all the users created in ITSM");
            }
            catch (Exception e){
                log.error("Error while deleting ITSM users in DRSQA_12306: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Test(description = "[IMS] [Sync-Webhook] ITSM Insights related records on AR System Logical User Grouping form gets synced to ADE-IMS", groups={"Regression","webhook"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-12364", Products = {Product.ADE_Platform})
    public void DRSQA_12364_II_records_user_grouping_sync(ITestContext context) throws IOException {
        doPreconditions();

        log.info("Login to Helix Portal with admin user for tenant where sync was performed.");

        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
        platformLaunchPage.verifyLoginToADEPlatform();

        log.info("----- Navigate to ITSM Insight Administrator ----- ");
        navigateGroup(SolQAConstants.ITSM_INSIGHT_ADMINISTRATOR);
        performActionOnGroup(SolQAConstants.ITSM_INSIGHT_ADMINISTRATOR, "Edit");
        groupManagementPage.closeThePanel();

        log.info("----- Navigate to Problem Identification User ----- ");
        navigateGroup(SolQAConstants.PROBLEM_IDENTIFICATION_USER);
        performActionOnGroup(SolQAConstants.PROBLEM_IDENTIFICATION_USER, "Edit");
        groupManagementPage.closeThePanel();

        log.info("----- Navigate to Incident Correlation User ----- ");
        navigateGroup(SolQAConstants.INCIDENT_CORRELATION_USER);
        performActionOnGroup(SolQAConstants.INCIDENT_CORRELATION_USER, "Edit");
        groupManagementPage.closeThePanel();

    }

    public String buildJsonWithReplacements(File payloadFile, Map<String, String> replacements) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payloadFile);

        // Navigate to "values" node
        JsonNode valuesNode = root.get("values");
        if (valuesNode != null && valuesNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) valuesNode;

            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                String value = field.getValue().asText();

                // Replace placeholder if matches
                if (value.startsWith("_") || value.contains("__")) {
                    String newValue = replacements.getOrDefault(value, value);
                    objectNode.put(key, newValue);
                }
            }
        }

        // Return formatted JSON string
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    @AfterMethod()
    public void quitdriver(){
        if (getDriver() != null){
            getDriver().quit();}
    }

}
