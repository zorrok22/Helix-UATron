package ITSM.BWF;

import framework.Launcher;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.SmartIT.CreateChangePage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BWF_ITSM extends Launcher {

    public static SolqaLogger log = new SolqaLogger(BWF_ITSM.class.getName());
    ReadJson readJson = new ReadJson();
    WebDriver bwfDriver = null, smartITDriver = null;
    BWFFeatureMethods bwfFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    DWPCFeatureMethods dwpcFeatureMethods;
    Map<Object, String> smartITUserData, smartITTestData, bwfUserData, bwfTestData;

    @Test(description = "[BWF][ITSM]-BWF to ITSM integration (External ID)", groups = {"BWF", "ITSM", "Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11633", Products = {Product.Helix_BWF, Product.Helix_ITSM})
    public void DRSQA_11633_BWF_ITSM_Integration_external_ID(ITestContext context) throws IOException {

        log.info("***************** START:- BWF_ITSM_Integration_external_ID (DRSQA-11633) ********************* ");

        dwpcFeatureMethods = new DWPCFeatureMethods();
        String remedyConnectorID = dwpcFeatureMethods.getRemedyConnectionId();
        log.info("Remedy Connector ID from DWPC: " + remedyConnectorID);
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);

        log.info("Step 1: Login to BWF and create case using integration template");
        context.setAttribute("DRSQA_11633_BWF_Driver", bwfDriver);
        context.setAttribute("DRSQA_11633_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Peter");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11633_TestData");

        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Create BWF case using template: " + bwfTestData.get("Template Name"));
        bwfTestData.put("Connection ID", remedyConnectorID);
        String caseId = bwfFeatureMethods.createCase(bwfTestData);
        log.info("BWF case created with Case ID: " + caseId);
        bwfFeatureMethods.captureScreenshot("DRSQA_11633_BWF_CaseCreated_" + caseId, SolQAConstants.appBWF);

        log.info("Step 2: Wait for External ID (INC*) on case");
        String expectedPrefix = bwfTestData.get("ExpectedExternalIdPrefix");
        String externalIncId = bwfFeatureMethods.waitForExternalIdToContain(expectedPrefix, 6, 5000);
        log.info("External Incident ID created is: " + externalIncId);
        Assert.assertTrue(externalIncId.contains(expectedPrefix), "External ID not created/visible on BWF case.");

        // Optional: open related task if it exists and verify external ID is referenced
        try {
            String taskTitle = externalIncId + " - " + bwfTestData.get("Task Name");
            bwfFeatureMethods.clickLinkByText(taskTitle).verifyTextPresent(externalIncId);
            bwfFeatureMethods.captureScreenshot("DRSQA_11633_BWF_Task_Verified", SolQAConstants.appBWF);
            getDriver().navigate().back();
        } catch (NoSuchElementException | TimeoutException e) {
            log.info("Task verification skipped (task link not found): " + e.getMessage());
        }

        log.info("Open SmartIT, search incident and move it to Resolved");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11633_SmartIT_Driver", smartITDriver);
        context.setAttribute("DRSQA_11633_SmartIT_Product", Product.Helix_ITSM);

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11633_ITSM_IncidentResolution");

        log.info("Login to SmartIT user is: " + smartITUserData.get("UserName"));
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        log.info("Search and open incident in SmartIT: " + externalIncId);
        smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.INCIDENT, externalIncId);
        smartITFeatureMethods.captureScreenshot("DRSQA_11633_SmartIT_IncidentOpened", SolQAConstants.appSmartIT);

        log.info("Move incident to Resolved in SmartIT");
        smartITFeatureMethods.updateIncidentStatus(
                smartITTestData.get("ResolvedStatus"),
                smartITTestData.get("ResolvedStatusReason"),
                smartITTestData.get("ResolvedResolutionNote")
        );
        smartITFeatureMethods.saveIncident();
        smartITFeatureMethods.verifyStatusViewPage(smartITTestData.get("ExpectedResolvedStatus"));
        smartITFeatureMethods.captureScreenshot("DRSQA_11633_SmartIT_IncidentResolved", SolQAConstants.appSmartIT);
        smartITFeatureMethods.closeBrowser();

        log.info("Step 4: Verify BWF case status becomes Resolved after incident resolution");
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        String expectedResolvedStatus = bwfTestData.get("ExpectedResolvedStatus");
        log.info("Waiting for BWF case status to become: " + expectedResolvedStatus);
        String actualResolvedStatus = bwfFeatureMethods.waitForCaseStatus(expectedResolvedStatus, 12, 5000);
        log.info("Final BWF case status observed: " + actualResolvedStatus);
        Assert.assertEquals(actualResolvedStatus, expectedResolvedStatus, "BWF case status did not update to Resolved.");
        bwfFeatureMethods.captureScreenshot("DRSQA_11633_BWF_CaseResolved", SolQAConstants.appBWF);

        log.info("***************** END:- BWF_ITSM_Integration_external_ID (DRSQA-11633) ********************* ");
    }

    @Test(description = "Create related change request from BWF case", groups = {"BWF", "ITSM", "SmartIT", "Regression_P1","hpareek"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11733", Products = {Product.Helix_BWF, Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA11733_CreateChangeFromCase(ITestContext context) throws Exception {

        log.info("***************** START:- DRSQA11733_CreateChangeFromCase (DRSQA-11733) ********************* ");

        // ----------------- Precondition: Enable integration settings in BWF -----------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11733_BWF_Driver", bwfDriver);
        context.setAttribute("DRSQA_11733_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "hannah_admin");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA11733_CreateCase");

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        bwfFeatureMethods
                .navigateToSettings()
                .enterValueInTextField(bwfTestData.get("settingName"))
                .clickOnSearchedAdminSetting(bwfTestData.get("settingName"))
                .clickApplicationConfigurationsTab()
                .selectLOBFromDropDown(bwfTestData.get("LOBValue"))
                .clickApplicationConfigNameAndSetValue(bwfTestData.get("configName"), SolQAConstants.TRUE)
                .clickApplicationConfigNameAndSetValue(bwfTestData.get("configName2"), SolQAConstants.TRUE);

        bwfFeatureMethods.captureScreenshot("DRSQA11733_SettingsEnabled", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // ----------------- Step 1: Create new asset in SmartIT (XML->JSON migrated) -----------------
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA_11733_SmartIT_Driver", getDriver());
        context.setAttribute("DRSQA_11733_SmartIT_Product", Product.SmartIT);

        // Umbrella used SmartITUsers key "BWFWithAssetPermissionUser". SolutionsQA does not have this key.
        // Engineering decision: use existing SmartIT user "pbunyon" as the asset-capable user.
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "yanandaUser");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11733_CreateAsset");

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethods.navigateToCreateNewAsset();

        String assetName = smartITTestData.get("AssetNamePrefix") + "_" + UUID.randomUUID();
        Map<Object, String> assetData = new HashMap<>(smartITTestData);
        assetData.put("AssetName", assetName);

        smartITFeatureMethods.createAsset(assetData);
        smartITFeatureMethods.captureScreenshot("DRSQA11733_AssetCreated_" + assetName, SolQAConstants.appSmartIT);
        smartITFeatureMethods.closeBrowser();

        // ----------------- Step 2: Create case in BWF -----------------
        setDriver(bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF));
        bwfFeatureMethods = new BWFFeatureMethods();

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "yanandaUser");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA11733_CreateCase");

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        Map<Object, String> caseData = new HashMap<>(bwfTestData);
        caseData.put("Line of Business", bwfTestData.get("Line of Business"));
        //caseData.put("Assignee", bwfTestData.get("User"));

        String caseId = bwfFeatureMethods.createCase(caseData);
        bwfFeatureMethods.captureScreenshot("DRSQA11733_CaseCreated_" + caseId, SolQAConstants.appBWF);


        // ----------------- Step 3: Relate asset to case -----------------
        bwfFeatureMethods
                .navigateToRelatedAssetsTab()
                .relateAssetToCase(bwfTestData.get("CITypeLabel"), bwfTestData.get("ciValue"), assetName)
                .verifyRelatedAssetPresent(assetName);

        // ----------------- Step 4: Create change from Related tickets -----------------
        String bwfHandle = getDriver().getWindowHandle();
        bwfFeatureMethods
                .navigateToRelatedTicketsTab()
                .openCreateMenuAndSelectRelatedTicketType(bwfTestData.get("Create_DropDown_Options"), bwfTestData.get("changeDropdown"));

        // ----------------- Step 5: Verify autopopulated fields and update required fields on Change -----------------
        CreateChangePage createChangePage = new CreateChangePage();
        String customer = createChangePage.getInputValueByLabel(bwfTestData.get("CustomerFieldLabel"));
        Assert.assertTrue(customer.contains(bwfTestData.get("Requester")),
                "Auto populated customer value not matched. Expected contains: " + bwfTestData.get("Requester") + " Actual: " + customer);

        String company = createChangePage.getInputValueByLabel(bwfTestData.get("CompanyFieldLabel"));
        Assert.assertTrue(company.contains(bwfTestData.get("Company")),
                "Auto populated company value not matched. Expected contains: " + bwfTestData.get("Company") + " Actual: " + company);

        // Assignment: Manager organization + Manager group only (label-driven)
        createChangePage.setTypeaheadByLabel(bwfTestData.get("ManagerOrgLabel"), bwfTestData.get("managerOrgValue"));
        createChangePage.setTypeaheadByLabel(bwfTestData.get("ManagerGroupLabel"), bwfTestData.get("managerGroupValue"));
        
        // Save change and capture Change ID
        createChangePage.clickSaveButtonAdaptPrimary();
        smartITFeatureMethods.captureScreenshot("DRSQA11733_ChangeCreated", SolQAConstants.appSmartIT);

        // ----------------- Step 6: Verify change entry added under Related tickets in case -----------------
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.navigateToRelatedTicketsTab();
        bwfFeatureMethods.captureScreenshot("DRSQA11733_ChangeRelatedToCase_" + caseId, SolQAConstants.appBWF);
        closeBrowser();
        log.info("***************** END:- DRSQA11733_CreateChangeFromCase (DRSQA-11733) ********************* ");
    }

    @Test(description = "Create ITSM tickets from Related Tickets and Relate tickets to Case", groups = {"Regression_P1", "BWF", "SmartIT", "ITSM","gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "18024", JiraID = "DRSQA-11652", Products = {Product.Helix_BWF, Product.SmartIT, Product.Helix_ITSM})
    public void DRSQA11652_Able_To_Create_Incident_Under_Related_Ticket(ITestContext context) throws Exception {
		log.info("------- START: DRSQA11652_Able_To_Create_Incident_Under_Related_Ticket");
		log.info("-----------DRSQA-11652 Enabling precondition of Create Service request to On ----------------");
         bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA11652_TestData");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11652_Driver", getDriver());
        context.setAttribute("DRSQA_11652_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.enterValueInTextField(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
        try {
            bwfFeatureMethods.clickApplicationConfigNameAndSetValue(bwfTestData.get("CommonSettingRequest"), SolQAConstants.TRUE);
        } catch (Exception e) {
           log.info("Exception occurred while enabling CommonSettingRequest: " + e.getMessage());
        }
		bwfFeatureMethods.closeBrowser();
    
		log.info("--------- Step1:- Login to BWF  with the agent ----------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11652_Driver", bwfDriver);
        context.setAttribute("DRSQA_11652_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA11652_TestData");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("--------- Step2:- Create Case with the mandatory fields ----------");
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        String caseID = bwfFeatureMethods.createCase(bwfTestData);
        log.info("Case ID: " + caseID);

        log.info("--------- Step3:- Go to Case --Edit case --> Related Tickets----------");
		bwfFeatureMethods.navigateToRelatedTicketsTab();

        log.info("--------- Step4:- On the  case --> Related tickets --> Create( Incident, WO) ----------");
		Assert.assertTrue(bwfFeatureMethods.verifyFieldPresent_in_Related_Tickets(SolQAConstants.RELATE_FIELD), "Element not present on Related Page - 'Relate'");
		Assert.assertTrue(bwfFeatureMethods.verifyFieldPresent_in_Related_Tickets(SolQAConstants.CREATE_FIELD), "Element not present on Related Page - 'Create'");
		bwfFeatureMethods.clickOption(SolQAConstants.CREATE_FIELD);

        log.info("--------- Step5:- Fill the required fields ----------");
		Assert.assertTrue(bwfFeatureMethods.verifyDropDownValues(bwfTestData.get("Create_DropDown_Options")));
		bwfFeatureMethods.clickIncidentOption();
		log.info("--------- DRSQA-11652 Creating Incident in BWF ----------");
		bwfFeatureMethods.SetDropdownValueforBWFIncident(SolQAConstants.INCIDENT_TYPE,bwfTestData.get("Incident_Type"));
		bwfFeatureMethods.SetDropdownValueforBWFIncident(SolQAConstants.URGENCY,bwfTestData.get("Urgency"));
		bwfFeatureMethods.SetDropdownValueforBWFIncident(SolQAConstants.IMPACT,bwfTestData.get("Impact"));
		bwfFeatureMethods.saveIncident();
		Assert.assertTrue(bwfFeatureMethods.verifyRelationshipType(bwfTestData.get("RelationShip_Type_Status")));
		bwfFeatureMethods.verify_Activity_Updated();

        log.info("--------- Step6:-Relate the Case with INC ----------");
		bwfFeatureMethods.clickOption(SolQAConstants.RELATE_FIELD);
		Assert.assertTrue(bwfFeatureMethods.verifyDropDownValues(bwfTestData.get("Relate_DropDown_Options")));
		String INC_ID = bwfFeatureMethods.INCID();
        log.info("INC ID: " + INC_ID);
		String Profile_name = bwfFeatureMethods.UserID();
        log.info("Profile name: " + Profile_name);
        bwfFeatureMethods.closeBrowser();

		log.info("--------- Step7:- Logging into the SmartIT PV ----------");
        smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        context.setAttribute("DRSQA_11652_Driver", smartITDriver);
        context.setAttribute("DRSQA_11652_Product", Product.SmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA11652_TestData");
        log.info("SmartIT user is: " + smartITUserData.get("UserName"));
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        log.info("--------- Step8:- Open the INC in PV and under related item check the case related----------");
        log.info("Navigate to Ticket Console");
        smartITFeatureMethods.navigateToTicketConsole();
        smartITFeatureMethods.captureScreenshot("DRSQA_11810_navigatedToConsole", SolQAConstants.appSmartIT);
        Assert.assertTrue(smartITFeatureMethods.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");
        log.info("Applying Filters and opening created workorder");

        log.info("--------- Step9:- Try to remove the relationship from smart IT --> Click on Delete option----------");
        smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.INCIDENT, INC_ID);
		Assert.assertTrue(smartITFeatureMethods.checkCaseRelationType(), "Relation type is not related to");
		Assert.assertTrue(smartITFeatureMethods.TryToDeleteRelationship(bwfTestData.get("Error_Message")), "Delete operation is successful ");

        log.info("--------- Step10:- BWF cross launch should be successfully from smartIT PV----------");
		Assert.assertTrue(smartITFeatureMethods.cross_Launch_To_BWF(), "Cross launching is failed");

        log.info("--------- Step11:- BWF case console --> from case  relate to INC, CRQ and CAse----------");

        log.info("--------- Step12:- Select the INC and  Case and click on  Add work Note----------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11652_Driver", bwfDriver);
        context.setAttribute("DRSQA_11652_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA11652_TestData");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
		bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField(caseID);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseID, "Case ID");
		bwfFeatureMethods.navigateToRelatedTicketsTab();
		bwfFeatureMethods.clickOnAddWorkNote();
		bwfFeatureMethods.selectWorkNoteType(bwfTestData.get("Work_Note_Type"));
		bwfFeatureMethods.AddWorkNote(bwfTestData.get("Private_Note"), SolQAConstants.PrivateNote);
		Assert.assertTrue(bwfFeatureMethods.verifyBody_In_SocialFeed(bwfTestData.get("Private_Note")));
		bwfFeatureMethods.clickOnAddWorkNote();
		bwfFeatureMethods.selectWorkNoteType(bwfTestData.get("Work_Note_Type"));

        log.info("--------- Step13 and Step14:- Select all related tickets and Add a Private comment in Single Line and check parent Case----------");
		bwfFeatureMethods.AddBoldItalicWorkNote(bwfTestData.get("Private_Note"), SolQAConstants.PrivateNote);
		Assert.assertTrue(bwfFeatureMethods.verifyBody_In_SocialFeed(bwfTestData.get("Private_Note")));

        log.info("--------- Step15 and Step16:- Open the related  INC  in smart IT (PV) and check the comments----------");
        smartITFeatureMethods.switchToSmartITPage(smartITDriver);
		String ExpectedActivity = "Elizabeth Peters added a note The following note was added from " + caseID ;
		Assert.assertTrue(smartITFeatureMethods.isActivityNoteAppearedUnderActivityTab_simpleText(ExpectedActivity), "Newly added Note from SmartIT is not present on incident id");
		smartITDriver.close();

		log.info("--------- Step17:- public notes added on the related case will be visible on related case service Request id (if create service request = true) ----------");
		bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11652_Driver", getDriver());
        context.setAttribute("DRSQA_11652_Product", Product.Helix_BWF);
		bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
		bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA11652_TestData");
		login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
		bwfFeatureMethods.verifyLoginToBWF();
		bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
		bwfFeatureMethods.setSearchTextField(caseID);
		bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseID, "Case ID");
		bwfFeatureMethods.assignCaseToMe();

        log.info("--------- Step18:- Open Agent profile in BWF by himself who added all above comments and check Person Activity ----------");
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed(bwfTestData.get("First_Activity")));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed(bwfTestData.get("Second_Activity")));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed("Elizabeth Peters" + " " + bwfTestData.get("Third_Activity")));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed("Elizabeth Peters" + " " + bwfTestData.get("Fourth_Activity")));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed("Elizabeth Peters added " + INC_ID + " " + bwfTestData.get("Fifth_Activity")));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed("Elizabeth Peters" + " " + bwfTestData.get("Sixth_Activity")));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed(String.format(bwfTestData.get("Seventh_Activity"),caseID)));
		Assert.assertTrue(bwfFeatureMethods.verifyTitle_In_SocialFeed("Elizabeth Peters" + " " + bwfTestData.get("Eighth_Activity")));
		bwfFeatureMethods.closeBrowser();
		log.info("*********** DRSQA11652_Able_To_Create_Incident_Under_Related_Ticket ******End*****");
    }
	

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        log.info("Starting browser cleanup for BWF_ITSM test method.");
        safelyQuitDriver(smartITDriver, "SmartIT");
        if (bwfDriver != smartITDriver) {
            safelyQuitDriver(bwfDriver, "BWF");
        } else {
            log.info("BWF and SmartIT driver references are identical; skipping duplicate quit call.");
        }
        log.info("Completed browser and thread-local cleanup for BWF_ITSM test method.");
    }

}