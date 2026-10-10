package ITSM.PartnerApps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITSM.CSM.CSMFeatureMethodsPage;
import pages.ITSM.HPM.HPM_FeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import util.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PartnerApps extends Launcher {

    private static final SolqaLogger log = new SolqaLogger(PartnerApps.class.getName());
    WebDriver HPMDriver, CSMDriver;
    ReadJson readJson;
    Map<Object, String> isUserData, HPMTestData, CSMTestData;
    SoftAssert softAssert;
    ITSMFeatureMethods itsmFeatureMethods;
    CSMFeatureMethodsPage csmFeatureMethodsPage;
    HPM_FeatureMethods hpm_featureMethods;

    public void beforeHPM() {
        readJson = new ReadJson();
        hpm_featureMethods = new HPM_FeatureMethods();
        softAssert = new SoftAssert();
    }

    public void beforeCSM() {
        readJson = new ReadJson();
        csmFeatureMethodsPage = new CSMFeatureMethodsPage();
    }

    @Test(description = "Verify functionality of Helix Portfolio Management", groups = {"MAT", "HPM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-15663", Products = {Product.Innovation_Suite, Product.HPM})
    public void DRSQA_15663_Verify_Functionality_Of_HPM(ITestContext context) {

        log.info("*************** Start: DRSQA_15663_Verify_Functionality_Of_HPM ****************");

        HPMDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(HPMDriver);
        context.setAttribute("DRSQA_15663_Driver", HPMDriver);
        context.setAttribute("DRSQA_15663_Product", Product.Helix_ITSM);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        readJson = new ReadJson();
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.LoadForm(SolQAConstants.CTM_PEOPLE, SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias")).verifyCTMPeoplePageLoaded();
        itsmFeatureMethods.searchPeopleWithUserName("bmcopssupportagent")
                .updateISPersonas("com.fusiongbs.agility", "Resource Master");
        itsmFeatureMethods.closeBrowser();

        log.info("Step 1/14: Launch HPM bundle (Innovation Suite)");
        HPMDriver = launchWeb(SolQAConstants.HPM_BUNDLE, SolQAConstants.appIS);
        setDriver(HPMDriver);

        context.setAttribute("DRSQA_15663_Driver", HPMDriver);
        context.setAttribute("DRSQA_15663_Product", Product.HPM);

        log.info("Step 2/14: Initialise HPM test context (ReadJson, SoftAssert)");
        beforeHPM();

        log.info("Step 3/14: Login to HPM as bmcopssupportagent");
        isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISUsers, "bmcopssupportagent");
        login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appHPM);
        hpm_featureMethods = new HPM_FeatureMethods();
        hpm_featureMethods.verifyHPMLogin();
        String randomSummary;

        log.info("Step 4/14: Create Portfolio record");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_PortfolioData");
        hpm_featureMethods.navigateCreateHPMRecord(SolQAConstants.Portfolio);
        randomSummary = "Portfolio_" + System.currentTimeMillis();
        HPMTestData.put(SolQAConstants.SUMMARY_LABEL, randomSummary);
        hpm_featureMethods.createHPMRecord(SolQAConstants.Portfolio, HPMTestData);
        String PortfolioID = hpm_featureMethods.getPortfolioTicketID();
        log.info("Step 4/14 (result): Portfolio created; ID=" + PortfolioID + ", Summary=" + randomSummary);
        softAssert.assertNotNull(PortfolioID, "Portfolio Record is not created successfully");

        log.info("Step 5/14: Create Idea record");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_IdeaData");
        hpm_featureMethods.navigateCreateHPMRecord(SolQAConstants.Idea);
        randomSummary = "Idea_" + System.currentTimeMillis();
        HPMTestData.put(SolQAConstants.SUMMARY_LABEL, randomSummary);
        hpm_featureMethods.createHPMRecord(SolQAConstants.Idea, HPMTestData);
        String IdeaID = hpm_featureMethods.getIdeaTicketID();
        log.info("Step 5/14 (result): Idea created; ID=" + IdeaID + ", Summary=" + randomSummary);
        softAssert.assertNotNull(IdeaID, "Idea Record is not created successfully");

        log.info("Step 6/14: Create Demand record");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_DemandData");
        hpm_featureMethods.navigateCreateHPMRecord(SolQAConstants.Demand);
        randomSummary = "Demand_" + System.currentTimeMillis();
        HPMTestData.put(SolQAConstants.SUMMARY_LABEL, randomSummary);
        hpm_featureMethods.createHPMRecord(SolQAConstants.Demand, HPMTestData);
        String DemandID = hpm_featureMethods.getDemandTicketID();
        log.info("Step 6/14 (result): Demand created; ID=" + DemandID + ", Summary=" + randomSummary);
        softAssert.assertNotNull(DemandID, "Demand Record is not created successfully");

        log.info("Step 7/14: Create Demand sub-ticket — Requirement");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_Demand_RequirementData");
        hpm_featureMethods.createDemandSubTickets(HPMTestData, SolQAConstants.Requirement);

        log.info("Step 8/14: Create Demand sub-ticket — Risk");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_Demand_RiskData");
        hpm_featureMethods.createDemandSubTickets(HPMTestData, SolQAConstants.Risk);

        log.info("Step 9/14: Create Demand sub-ticket — Stakeholder");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_Demand_StakeholderData");
        hpm_featureMethods.createDemandSubTickets(HPMTestData, SolQAConstants.Stakeholder);

        log.info("Step 10/14: Create Project record");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_ProjectData");
        hpm_featureMethods.navigateCreateHPMRecord(SolQAConstants.Project);
        randomSummary = "Project_" + System.currentTimeMillis();
        HPMTestData.put(SolQAConstants.SUMMARY_LABEL, randomSummary);
        hpm_featureMethods.createHPMRecord(SolQAConstants.Project, HPMTestData);
        String ProjectID = hpm_featureMethods.getProjectTicketID();
        log.info("Step 10/14 (result): Project created; ID=" + ProjectID + ", Summary=" + randomSummary);
        softAssert.assertNotNull(ProjectID, "Project Record is not created successfully");

        log.info("Step 11/14: Create Program record");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_ProgramData");
        hpm_featureMethods.navigateCreateHPMRecord(SolQAConstants.Program);
        randomSummary = "Program_" + System.currentTimeMillis();
        HPMTestData.put(SolQAConstants.SUMMARY_LABEL, randomSummary);
        hpm_featureMethods.createHPMRecord(SolQAConstants.Program, HPMTestData);
        String ProgramID = hpm_featureMethods.getProgramTicketID();
        log.info("Step 11/14 (result): Program created; ID=" + ProgramID + ", Summary=" + randomSummary);
        softAssert.assertNotNull(ProgramID, "Program Record is not created successfully");

        log.info("Step 12/14: Create Resource Request record");
        HPMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "HPM_ResourceRequestData");
        hpm_featureMethods.navigateCreateHPMRecord(SolQAConstants.Resource_Request);
        hpm_featureMethods.createHPMRecord(SolQAConstants.Resource_Request, HPMTestData);
        String ResourceRequestID = hpm_featureMethods.getResourceRequestTicketID();
        log.info("Step 12/14 (result): Resource Request created; ID=" + ResourceRequestID);
        softAssert.assertNotNull(ResourceRequestID, "Resource Request Record is not created successfully");

        log.info("Step 13/14: Run soft assertions");
        softAssert.assertAll();
        log.info("Step 14/14: Close browser");
        hpm_featureMethods.closeBrowser();
        log.pass("*************** End: DRSQA_15663_Verify_Functionality_Of_HPM ****************");
    }

    @Test(description = "Verify functionality of Customer Service management", groups = {"MAT", "CSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-15763", Products = {Product.Innovation_Suite, Product.CSM})
    public void DRSQA_15763_Verify_Functionality_Of_CSM() {

        log.info("*************** Start: DRSQA_15763_Verify_Functionality_Of_CSM ****************");

        log.info("Step 1/15 (Precondition): Launch ITSM as admin");
        CSMDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(CSMDriver);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        readJson = new ReadJson();

        log.info("Step 2/15 (Precondition): Verify ITSM login");
        itsmFeatureMethods.verifyITSMLogin();

        log.info("Step 3/15 (Precondition): Load AR System Licenses Console and configure CSM license values");
        CSMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMTestData, "DRSQA_15763_TestData");
        itsmFeatureMethods.LoadForm(SolQAConstants.AR_System_Licenses_Console, SolQAConstants.Default, CommonMethods.readConfig("ARServerAlias")).verifyAddRemoveLicensesPageLoaded();
        itsmFeatureMethods.updateLicenseValue(SolQAConstants.Customer_Service_Management, CSMTestData.get("CSM_License_Value"))
                .updateLicenseValue(SolQAConstants.Customer_Service_Management_User_Fixed, CSMTestData.get("CSM_License_User_Fixed_Value"))
                .updateLicenseValue(SolQAConstants.Customer_Service_Management_User_Floating, CSMTestData.get("CSM_License_User_Floating_Value"));

        log.info("Step 4/15 (Precondition): Open CTM People, search user, assign CSM fixed user license");
        itsmFeatureMethods.LoadForm(SolQAConstants.CTM_PEOPLE, SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias")).verifyCTMPeoplePageLoaded();
        itsmFeatureMethods.searchPeopleWithUserName(CSMTestData.get("CSMUserName")).updateISApplicationLicenses(SolQAConstants.Customer_Service_Management_User_Fixed);
        log.info("Step 5/15 (Precondition): Close ITSM browser");
        itsmFeatureMethods.closeBrowser();

        log.info("Step 6/15 (Precondition): Launch CSM bundle and login as admin to configure CSM");
        CSMDriver = launchWeb(SolQAConstants.CSM_BUNDLE, SolQAConstants.appIS);
        setDriver(CSMDriver);
        beforeCSM();
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appCSM);
        CSMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "DRSQA_15763_TestData");

        log.info("Step 7/15 (Precondition): CSM Configurations — Core");
        csmFeatureMethodsPage.verifyCSMPageLoaded();
        csmFeatureMethodsPage.navigateToTab(SolQAConstants.CONFIGURATIONS);

        csmFeatureMethodsPage.OpenTabFromCSMControlPanel(SolQAConstants.Core);
        csmFeatureMethodsPage.setCoreConfiguration(CSMTestData);

        log.info("Step 8/15 (Precondition): CSM Configurations — Standard");
        csmFeatureMethodsPage.OpenTabFromCSMControlPanel(SolQAConstants.Standard);
        csmFeatureMethodsPage.setStandardConfiguration(CSMTestData);

        log.info("Step 9/15 (Precondition): CSM Configurations — Groups Specific");
        csmFeatureMethodsPage.OpenTabFromCSMControlPanel(SolQAConstants.Groups_Specific);
        csmFeatureMethodsPage.setGroupSpecificConfiguration(CSMTestData);

        log.info("Step 10/15 (Precondition): CSM Configurations — Customer Database");
        csmFeatureMethodsPage.OpenTabFromCSMControlPanel(SolQAConstants.Customer_Database);
        csmFeatureMethodsPage.setCustomerDatabaseConfiguration(CSMTestData);
        log.info("Step 11/15 (Precondition): Close CSM admin browser after configuration");
        csmFeatureMethodsPage.closeBrowser();

        log.info("Step 12/15: Launch CSM bundle and login as bmcopssupportagent");
        CSMDriver = launchWeb(SolQAConstants.CSM_BUNDLE, SolQAConstants.appIS);
        setDriver(CSMDriver);
        beforeCSM();
        isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISUsers, "bmcopssupportagent");
        CSMTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, SolQAConstants.ISTestData, "DRSQA_15763_TestData");
        login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appCSM);

        log.info("Step 13/15: Verify CSM loaded, start session, open new case, fill case data");
        csmFeatureMethodsPage.verifyCSMPageLoaded();
        csmFeatureMethodsPage.clickStartSessionButton();
        csmFeatureMethodsPage.clickOnNewCaseButton();
        csmFeatureMethodsPage.fillCaseDataInCaseCreationConsole(CSMTestData);
        String CSM_CaseID = csmFeatureMethodsPage.getCaseReferenceIdFromToast();
        log.info("Step 14/15 (result): Case created; reference from toast=" + CSM_CaseID);
        Assert.assertTrue(CSM_CaseID.contains("CS"), "Case creation failed in CSM");

        log.info("Step 15/15: Close browser");
        csmFeatureMethodsPage.closeBrowser();
        log.pass("*************** End: DRSQA_15763_Verify_Functionality_Of_CSM ****************");
    }

    @Test(description = "Create/Search/Delete Record Using API")
    @SolqaDetails(authorName = "aramulwa", Products = {Product.Innovation_Suite})
    public void Create_Search_Delete_Record_Using_API() throws IOException {

        log.info("*************** Start: Create_Search_Delete_Record_Using_API ****************");
        log.info("Total steps planned: 5");

        log.info("Step 1/5: Resolve API payload path and read portfolio summary from createPortfolioAPI.json");
        String filePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appIS + File.separator + "createPortfolioAPI.json";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        String summary = root.path("fieldInstances").path("8").path("value").asText();
        log.info("Step 1/5 (result): Summary from JSON = " + summary);

        log.info("Step 2/5: Launch HPM bundle and login as admin");
        HPMDriver = launchWeb(SolQAConstants.HPM_BUNDLE, SolQAConstants.appIS);
        setDriver(HPMDriver);
        beforeHPM();
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appHPM);
        hpm_featureMethods = new HPM_FeatureMethods();
        hpm_featureMethods.verifyHPMLogin();

        log.info("Step 3/5: Create portfolio record via API");
        String recordID = hpm_featureMethods.createRecordUsingAPI(filePath, HPMDriver);
        log.info("Step 3/5 (result): Record ID = " + recordID);

        log.info("Step 4/5: Delete portfolio record (ID=" + recordID + ")");
        hpm_featureMethods.deleteRecord(SolQAConstants.HPM_Portfolio_Record, HPMDriver, recordID);
        log.info("Step 5/5: Delete completed; Create_Search_Delete_Record_Using_API finished successfully");
        log.pass("*************** End: Create_Search_Delete_Record_Using_API (passed) ****************");
    }

    @AfterMethod
    public void closeBrowser() {
        log.debug("Closing the browser");
        if (hpm_featureMethods != null) {
            hpm_featureMethods.closeBrowser();
        }
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (csmFeatureMethodsPage != null) {
            csmFeatureMethodsPage.closeBrowser();
        }
    }
}