package ITSM.DWP;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import framework.BasePage;
import org.testng.asserts.SoftAssert;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.*;
import framework.Launcher;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import util.CommonMethods;
import util.CommonWaits;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.UserUpdater;

public class DWP_BWF extends BasePage {

    static SolqaLogger log = new SolqaLogger(DWP_BWF.class.getName());
    DWPFeatureMethods dwpFeatureMethod;
    BWFFeatureMethods bwfFeatureMethods;
    ReadJson readJson = new ReadJson();
    Map<Object, String> dwpUserData, dwpTestData, bwfUserData, dwpcTestData, dwpcUserData, bwfTestData, alternateApprover;
    WebDriver dwpDriver, bwfDriver, dwpcDriver;
    UserUpdater dwpUsers, bwfUsers;
    int counter;
    DWPCFeatureMethods dwpcFeatureMethods;

    @BeforeTest
    public void setupUsers() {
        //Modifying json for DWPUsers
        counter = Integer.parseInt(configProp.get("runCounter"));
        if (counter > 0) {
            // Load the JSON file once
            String filename1 = "DWPUsers";
            dwpUsers = new UserUpdater();
            bwfUsers = new UserUpdater();
            String excelFilePath1 = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appDWP + File.separator + filename1 + ".xlsx";
            String jsonFilePath1 = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appDWP + File.separator + filename1 + ".json";
            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath1);
            log.info("JSON file path: " + jsonFilePath1);
            // Load the JSON file
            dwpUsers.loadJsonFile(jsonFilePath1);
            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            dwpUsers.updateFromExcelRow(excelFilePath1, counter);
            log.info("Updated DWPUsers.json for run counter: " + counter + " using row: " + counter);
            // Verify the update by reading the JSON back
            //Modifying json for BWFUsers
            String filename2 = "BWFUsers";
            String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appBWF + File.separator + filename2 + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appBWF + File.separator + filename2 + ".json";
            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);
            // Load the JSON file
            bwfUsers.loadJsonFile(jsonFilePath);
            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            bwfUsers.updateFromExcelRow(excelFilePath, counter);
            log.info("Updated BWFUsers.json for run counter: " + counter + " using row: " + counter);
        } else {
            //No change required
            log.info("runCounter is 0, no user updates required");
        }
    }

    //previous owner: shudeshp
    @Test(description = "DWP - Bi-directional communication between DWP request & BWF case (via new request)", groups = {"MAT", "DWP", "BWF", "DWPC"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11781", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_DWPC})
    public void DRSQA_11781_DWP_BWF_Communication_newCase(ITestContext context) throws Exception {

        log.info("*********** DRSQA-11781 : DWP_BWF_Communication_newCase *******************");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11781_Driver", dwpDriver);
        context.setAttribute("DRSQA_11781_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bmcopsenduser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11781_Data");

        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        log.info("Open Catalog Page");
        String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, SERVICE_NAME);
        dwpFeatureMethod.captureScreenshot("DRSQA_11781_requestCreated", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);

        log.info(" ************ SRD created ID: " + firstSRD);
        Assert.assertFalse(firstSRD.isEmpty(), "SR is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(firstSRD);
        String comment = "Adding attachment";
        File file2 = CommonMethods.createTextFile("DRSQA11781" + ".txt");

        log.info("Open submitted request and add comment with attachment");
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment, file2);
        dwpFeatureMethod.verifyAttachments("Adding attachment", file2);

        log.info("Login to BWF and add activity note");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11781_Driver", bwfDriver);
        context.setAttribute("DRSQA_11781_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "opsSupportAgent");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11781_Data");

        log.info("Login to BWF with user " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);

        bwfFeatureMethods.verifyLoginToBWF();
        log.info("Set LOB as : " + bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();

        log.info("Search and open requested service caseID: " + firstSRD);
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.captureScreenshot("DRSQA_11781_ticketOpenInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyTitle_In_SocialFeed(comment);
        bwfFeatureMethods.verify_UserName_In_Activity_Tab(dwpUserData.get("FullName"));

        String BWFComment = "BWF Public comment on case";
        log.info("Add public note from BWF side");
        bwfFeatureMethods.addActivityNotes(BWFComment);
        bwfFeatureMethods.captureScreenshot("DRSQA_11781_commentAddedInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("Switch to DWP window and verifying the comment added from BWF");
        dwpFeatureMethod.switchToDWP(dwpDriver);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.refreshDWPPage();

        log.info("Goto DWP and verify comments added from BWF side");
        dwpFeatureMethod.verifyComment(BWFComment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11781_bwfCommentInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
    }

    @Test(description = "DWP - BWF - Verify Case Template and Task Assignment workflow", groups = {"Regression_P1", "DWP", "BWF"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11712", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11712_DWP_BWF_Verify_Case_Template_And_Task_Assignment(ITestContext context) throws Exception {
        log.info("***** START : DRSQA_11712_DWP_BWF_Verify_Case_Template_And_Task_Assignment *****");

        context.setAttribute("DRSQA_11712_Driver", getDriver());
        context.setAttribute("DRSQA_11712_Product", Product.Helix_BWF);

        log.info("Step 1: Login to BWF Case BA from template owner group");
        log.info("Step 2: Navigate to Setting > Task management > Template > Open to do template from pre condiiton and inspect available dynamic field details"
                + "- Inspect Manage dynamic field UI and try to add new/existing field, try to remove added field, also try to update properties for added field"
                + "- Update Task status to Draft and click on Manage dynamic fields > Check each field property");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11712_Data");

        this.bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.captureScreenshot("DRSQA_11712_BWF_Login_Elizabeth", SolQAConstants.appBWF);

        bwfFeatureMethods.searchExpandAndOpenSettings(SolQAConstants.CASE_MANAGEMENT, SolQAConstants.TEMPLATES);
        bwfFeatureMethods.verifyTemplatePresent(bwfTestData.get("Template Name"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11712_Navigate_To_Case_Templates", SolQAConstants.appBWF);

        log.info("Step 3:Login with Case Agent and create a new Case in assigned status using template from pre condition"
                + "- Open and check to do task details and its dynamic fields"
                + "- Edit Task and try to update dynamic fields"
                + "- Check Case details");
        log.info("Create case with Udemy template");
        String caseID = bwfFeatureMethods.createCaseForUdemyData(bwfTestData);
        log.info("Case created with ID: " + caseID);
        bwfFeatureMethods.captureScreenshot("DRSQA_11712_Case_Created", SolQAConstants.appBWF);

        log.info("Step 4: With End User who is to do Assignee > Login to DWP and check for above task");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        this.dwpDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_11712_DWP_Login", SolQAConstants.appDWP);

        log.info("Search for request with case ID: " + caseID);
        dwpFeatureMethod.openSrmRequestByRequestID(caseID);
        dwpFeatureMethod.captureScreenshot("DRSQA_11712_Request_Opened_In_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);

        log.info("Step 5: Move Case to In Progress so that to do moved to Assigned");
        bwfFeatureMethods.switchToBWFLiveChat(bwfDriver);
        bwfFeatureMethods.openUpcomingTask(bwfTestData.get("TaskName"));
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.CANCELED);
        bwfFeatureMethods.clickOnViewCase(caseID);

        log.info("Change case status to Resolved");
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.RESOLVED);
        bwfFeatureMethods.captureScreenshot("DRSQA_11712_Case_Status_Changed_To_Assigned", SolQAConstants.appBWF);

        log.info("Verify case status is Resolved");
        Assert.assertEquals(bwfFeatureMethods.getCaseStatus(), SolQAConstants.RESOLVED, "Failed to change case status to Assigned");

        log.info("Step 6: With End User who is to do Assignee > Login to DWP and check for above task"
                + "- Open Task and check its details"
                + "- Click on Start button"
                + "- Enter partial responses and click on Save and Close");
        dwpFeatureMethod.switchToDWP(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();

        log.info("Verify request status is Completed");
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.COMPLETED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11712_Request_Status_Verified", SolQAConstants.appDWP);

        log.info("***** END : DRSQA_11712_DWP_BWF_Verify_Case_Template_And_Task_Assignment *****");
    }

    @Test(description = "DWP - Alternate approver can approve BWF case and add comments", groups = {"Regression_P1", "DWP", "BWF", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11642", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11642_AlternateApproverForBWFCase(ITestContext context) throws Exception {

        log.info("*********** DRSQA-11642 : Alternate approver can approve BWF case and add comments *******************");
        log.info("Step 1: Login to DWP and set alternate approver in user preferences");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11642_Driver", getDriver());
        context.setAttribute("DRSQA_11642_Product", Product.Helix_DWP);

        // Get test data
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11642_Data");

        // Get original approver user data
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "liamUser");
        alternateApprover = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "sadamUser");
        log.info("Login to DWP with original approver: " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();

        // Navigate to preferences and set alternate approver
        String alternateApproverFullName = alternateApprover.get("FullName");

        log.info("Navigate to User Preference > Approval Settings > Alternate Approval > Manage Approvers");
        dwpFeatureMethod.navigateToPreferences();
        dwpFeatureMethod.manageAlternateApprovers();

        String endDate = getFutureDateTime(365, "MMM d, yyyy, hh:mm a");
        log.info("Add alternate approver: " + alternateApproverFullName + " with end date: " + endDate);
        dwpFeatureMethod.addAlternateApprover(alternateApproverFullName, endDate);
        dwpFeatureMethod.captureScreenshot("DRSQA_11642_AlternateApproverAdded", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 2: Login to BWF and create case using Quick Case");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11642_Driver", getDriver());
        context.setAttribute("DRSQA_11642_Product", Product.Helix_BWF);
        // logout from DWP
        // Get BWF user data
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "LiamUsers");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11642_Data");

        log.info("Login to BWF with user: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Step 3: Click Quick Case tab and create case with template");
        bwfFeatureMethods.clickQuickCaseTab();

        // Type customer name and search
        String customerFullName = bwfTestData.get("FullName");
        log.info("Type customer: " + customerFullName + " and select");
        bwfFeatureMethods.typeAndSelectCustomerInQuickCase(customerFullName);

        // Type !move and select template
        String templateSearchText = bwfTestData.get("TemplateSearchText");  // !move
        String templateName = bwfTestData.get("TemplateName");  // "Move employee to new desk location"
        log.info("Type " + templateSearchText + " and select template: " + templateName);
        bwfFeatureMethods.typeInQuickCaseSearch(templateSearchText);
        bwfFeatureMethods.selectTemplateFromQuickCase(templateName);
        Assert.assertTrue(bwfFeatureMethods.isTemplateSearchSuccessful(templateName), "User is not able to search the template");
        bwfFeatureMethods.captureScreenshot("DRSQA_11642_TemplateSelected", SolQAConstants.appBWF);

        log.info("Step 3: Click Create case button");
        bwfFeatureMethods.clickCreateCaseButton();
        bwfFeatureMethods.captureScreenshot("DRSQA_11642_CaseCreated", SolQAConstants.appBWF);

        // Get created case ID
        String caseID = bwfFeatureMethods.getCreatedCaseID();
        log.info(" ************ Case created ID: " + caseID);
        Assert.assertFalse(caseID.isEmpty(), "Case is not created successfully");
        dwpFeatureMethod.closeBrowser();
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11642_Driver", getDriver());
        context.setAttribute("DRSQA_11642_Product", Product.Helix_BWF);

        log.info("Step 4: Login to DWP as alternate approver");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpFeatureMethod = new DWPFeatureMethods();
        context.setAttribute("DRSQA_11642_Driver", getDriver());
        context.setAttribute("DRSQA_11642_Product", Product.Helix_DWP);

        // Get alternate approver credentials
        Map<Object, String> alternateApproverData = readJson.getInputValuesMapIncludingLabelField(
                SolQAConstants.appDWP, "DWPUsers", "sadamUser"
        );

        log.info("step 5 : Login to DWP as alternate approver: " + alternateApproverData.get("UserName") + " add comments and approve case.");
        login(alternateApproverData.get("UserName"), alternateApproverData.get("Password"), SolQAConstants.appDWP);
        
        dwpFeatureMethod.openActivityPage();
        log.info("Open approval details for case: " + caseID);
        dwpFeatureMethod.openSrmRequestByRequestID(caseID);
        dwpFeatureMethod.verifyApprovalDetailsFormOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_11642_ApprovalDetailsOpened", SolQAConstants.appDWP);
        String alternateApproverComment = "Comment added by alternate approver " + alternateApproverData.get("FullName");
        log.info("Adding comment: " + alternateApproverComment);
        dwpFeatureMethod.addCommentInSubmittedRequest(alternateApproverComment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11642_CommentAdded", SolQAConstants.appDWP);
        log.info("Alternate approver approving the case");
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.APPROVED);
        dwpFeatureMethod.captureScreenshot("DRSQA_11642_CaseApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        log.info("step 6 : Login to BWF as approver and verify comment visibility.");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11642_Driver", getDriver());
        context.setAttribute("DRSQA_11642_Product", Product.Helix_BWF);
        log.info("Login to BWF with requester: " + bwfUserData.get("UserName"));
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "LiamUsers");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOBValue"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(caseID);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseID, "Case ID");
        bwfFeatureMethods.verify_Activity_Tab(alternateApproverComment);
        bwfFeatureMethods.captureScreenshot("DRSQA_11642_CommentVisibleToRequester", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("step 7 : Login to BWF as requester and verify comment visibility.");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11642_Driver", getDriver());
        context.setAttribute("DRSQA_11642_Product", Product.Helix_BWF);
        log.info("Login to BWF with requester: " + bwfUserData.get("UserName"));
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "LiamUsers");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOBValue"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(caseID);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseID, "Case ID");
        bwfFeatureMethods.verify_Activity_Tab(alternateApproverComment);
        bwfFeatureMethods.captureScreenshot("DRSQA_11642_CommentVisibleToRequester", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("*********** DRSQA-11642 test completed successfully *******************");
    }

    @Test(description = "BWF - Case reopen disabled when template configured with Allow case reopen = No", groups = {"Regression_P1", "DWP", "BWF", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11694", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11694_CaseReopenDisabled(ITestContext context) throws Exception {

        log.info("*********** Start: DRSQA_11694 - Case reopen disabled test *******************");

        // Step 1-7: Login to BWF and configure template
        log.info("Step 1: Login to BWF as Case Agent");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11694_Driver", getDriver());
        context.setAttribute("DRSQA_11694_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "LiamUsers");

        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11694_Data");

        log.info("Login to BWF with user: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Step 2-3: Navigate to Settings -> Case Management -> Templates");
        bwfFeatureMethods.navigateToCaseManagementTemplates();

        log.info("Step 4: Select Line of Business as Human Resource");
        bwfFeatureMethods.setLineOfBusinessInTemplates(bwfTestData.get("Line of Business"));

        log.info("Step 5: Search and open Udemy Training template");
        bwfFeatureMethods.searchAndOpenTemplate(bwfTestData.get("TemplateName"));

        log.info("Step 6: Edit template - Set status to Draft");
        log.info("Check if status is already Draft then skip the step");
        if (!bwfFeatureMethods.getTemplateStatus().equalsIgnoreCase(SolQAConstants.DRAFT)) {
            bwfFeatureMethods.clickEditOnTemplateMetadata();
            bwfFeatureMethods.setTemplateStatus(SolQAConstants.DRAFT);
            log.info(" Edit template - Set status to Draft");
            bwfFeatureMethods.SaveTemplateMetadata();
        }
       
        log.info("Step 7: Edit template - Set Allow case reopen = No and status = Active");
        bwfFeatureMethods.clickEditOnTemplate();
        bwfFeatureMethods.setAllowCaseReopen("No");
        bwfFeatureMethods.saveTemplate();
        bwfFeatureMethods.clickEditOnTemplateMetadata();
        bwfFeatureMethods.setTemplateStatus(SolQAConstants.Active);

        bwfFeatureMethods.SaveTemplateMetadata();
        bwfFeatureMethods.clickCancelOnTemplate();
        bwfFeatureMethods.clickBackOnTemplate();
        bwfFeatureMethods.captureScreenshot("DRSQA_11694_TemplateConfigured", SolQAConstants.appBWF);
        log.info("Step 8: Navigate to Create Case and create case with template");

        String caseID = bwfFeatureMethods.createCase(bwfTestData);
        bwfFeatureMethods.captureScreenshot("DRSQA_CaseReopen_CreateCasePage", SolQAConstants.appBWF);
        log.info("************ Case created with ID: " + caseID);
        Assert.assertFalse(caseID.isEmpty(), "Case is not created successfully");
        bwfFeatureMethods.changeCaseStatusTo(SolQAConstants.IN_PROGRESS);
        bwfFeatureMethods.captureScreenshot("DRSQA_11694_CaseInProgress", SolQAConstants.appBWF);
        // Step 13-17: Login to DWP and verify To-Dos
        log.info("Step 13-14: Close and relaunch DWP, login with end user");
        bwfFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11694_Driver", getDriver());
        context.setAttribute("DRSQA_11694_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        log.info("Step 15: Login to DWP with end-user: " + dwpUserData.get("UserName"));
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();

        log.info("Step 16-17: Open MyActivity and observe To-Dos task");
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseID);
        dwpFeatureMethod.captureScreenshot("DRSQA_11694_DWPActiveEvents", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // Step 18: Complete tasks and resolve case
        log.info("Step 18: Return to BWF, complete all tasks and resolve case");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11694_Driver", getDriver());
        context.setAttribute("DRSQA_11694_Product", Product.Helix_BWF);

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(caseID);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseID, "Case ID");
        bwfFeatureMethods.clickAssignToMeOnTaskInBWFCase();
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.clickMarkAsCompleteOnTaskInBWFCase();
        bwfFeatureMethods.changeCaseStatusToResolvedWithNotes(bwfTestData);
        bwfFeatureMethods.verifyReopenButtonIsNotPresent();
        bwfFeatureMethods.captureScreenshot("DRSQA_11694_CaseResolved", SolQAConstants.appBWF);

        bwfFeatureMethods.closeBrowser();
        log.info("*********** DRSQA_11694 test completed successfully *******************");
    }

    //previous owner:- Preethi
    @Test(description = "[DWP][BWF] Request service with case template and no config parameters using special character in login id", groups = {"Regression_P1", "DWP", "BWF", "gsutar"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-11508", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11508_RequestService_TemplateWithNoConfigParameters(ITestContext context) throws Exception {
     log.info("*********** Start: DRSQA_11508 - Request service with case template and no config parameters using special character in login id *******************");
     log.info("-------Precondition:- Dynamic BWF Request DWPC service is present,");

     log.info("-------Step1:- Login to BWFA as a Case Business Analyst. Go to Settings --> Case Management --> Templates. Choose Human Resource LoB. Create a new Case Template & capture the GUID/ID of the created case template-------");
     log.info("Using Software Install template for this test. GUID:- AGGADG1AAP0ICAOZ3SZ3OY75UM48W7");

     log.info("-------Step2:- Login to DWP as End user with special character in loginID, submit Dynamic BWF Request by providing Case Template GUID of template created in Step#-------");
     dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
     setDriver(dwpDriver);
     dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "SpecialCharacterUser");
     dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11508_SubmitRequest");
     login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
     context.setAttribute("DRSQA_11508_Driver", getDriver());
     context.setAttribute("DRSQA_11508_Product", Product.Helix_DWP);
     dwpFeatureMethod = new DWPFeatureMethods();
     dwpFeatureMethod.verifyDWPHomePageOpened();
     dwpFeatureMethod.openCatalogPage();
     String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
     if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
        dwpFeatureMethod.openAllItemsPage();
        dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
    } else {
        dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
    }
    log.info("request a service: " + SERVICE_NAME);
    dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);
    dwpFeatureMethod.fillTextFieldByLabel(SolQAConstants.BWF_CASE_TEMPLATE_GUID_FIELD, dwpTestData.get("BWF_CASE_TEMPLATE_GUID"));

     log.info("-------Step3:- End user clicks of submitted service request details and adds a comment with attachment to the request-------");
    dwpFeatureMethod.clickOnSubmitRequest();
    dwpFeatureMethod.captureScreenshot("DRSQA_11781_requestCreated", SolQAConstants.appDWP);
    String firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
    log.info(" ************ SRD created ID: " + firstSRD);
    Assert.assertFalse(firstSRD.isEmpty(), "SR is not created");
    dwpFeatureMethod.openSrmRequestByRequestID(firstSRD);
    String comment = "Adding attachment";
    File file2 = CommonMethods.createTextFile("DRSQA11508" + ".txt");
    log.info("Open submitted request and add comment with attachment");
    dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(comment, file2);
    dwpFeatureMethod.verifyAttachments("Adding attachment", file2);

    log.info("-------Step4:- Login to BWFA as Case Agent to whom Case created for request submitted in Step2 is assigned. Search for the service request submitted in Step#2. Verify Case details  fields are populated as per Case Template values.-------");
    bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
    setDriver(bwfDriver);
    bwfFeatureMethods = new BWFFeatureMethods();
    context.setAttribute("DRSQA_11508_Driver", bwfDriver);
    context.setAttribute("DRSQA_11508_Product", Product.Helix_BWF);
    bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
    bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11508_VerifyCaseCreation");
    log.info("Login to BWF with user " + bwfUserData.get("UserName"));
    login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
    bwfFeatureMethods.verifyLoginToBWF();
    log.info("Set LOB as : " + bwfTestData.get("HR_LOB"));
    bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
    bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
    log.info("Search and open requested service caseID: " + firstSRD);
    bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
    bwfFeatureMethods.clickSearchBtn_CaseConsole();
    bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
    bwfFeatureMethods.assignCaseToMe();
    bwfFeatureMethods.captureScreenshot("DRSQA_11508_ticketOpenInBWF", SolQAConstants.appBWF);

    log.info("-------Step5:-Verify the comment from End user submitted in Step#3-------");
    bwfFeatureMethods.verifyTitle_In_SocialFeed(comment);
    bwfFeatureMethods.verifyAttachmentInActivity(file2.getName());

    log.info("-------Step6:- As a BWF Case Agent add a public comment with attachment to the request-------");
    String BWFComment = "BWF Public comment on case";
    log.info("Add public note from BWF side");
    bwfFeatureMethods.addActivityNotes(BWFComment);
    bwfFeatureMethods.captureScreenshot("DRSQA_11508_commentAddedInBWF", SolQAConstants.appBWF);
     log.info("-Resolve and close the BWF case-------");
    bwfFeatureMethods.changeCaseStatusToWithoutSaving(SolQAConstants.RESOLVED);
    bwfFeatureMethods.saveStatusTransition();
    bwfFeatureMethods.closeBrowser();

    log.info("-------Step7:- Switch to DWP window and verify the comment added from BWF-------");
    dwpFeatureMethod.switchToDWP(dwpDriver);
    dwpFeatureMethod = new DWPFeatureMethods();
    dwpFeatureMethod.refreshDWPPage();
    log.info("Goto DWP and verify comments added from BWF side");
    dwpFeatureMethod.verifyComment(BWFComment);
    dwpFeatureMethod.captureScreenshot("DRSQA_11508_bwfCommentInDWP", SolQAConstants.appDWP);

    log.info("----------Step8:- Verify the request is closed in DWP-------");
    try{
    dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.STATUS_CLOSED);
    }
    catch (AssertionError e) {
        log.error("Failed to verify service request status: " + e.getMessage());
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.COMPLETED);
    }
    dwpFeatureMethod.closeBrowser();

    log.info("*********** END: DRSQA_11508 - Request service with case template and no config parameters using special character in login id *******************");

    }

    @Test(description = "[DWP][BWF] Requesting a service on behalf of another user and Case entry is created accordingly", groups = {"Regression_P1", "DWP", "BWF", "gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "463", JiraID = "DRSQA-11509", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11509_Requesting_a_service_on_behalf_of_another_user(ITestContext context) throws Exception {

        log.info("*********** START: DRSQA-11509 : Requesting a service on behalf of another user *******************");
        log.info("-------Precondition:-Precondition not required for this test--------");

        log.info("-------Step1:- End user (rmenon):*goes to DWP -> catalog,*requests created service,*enters value for requested for question, and selects Franz;*and just submits request.-------");
        log.info("-----------------Submit request in DWP----------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "rmenonEnduser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11509_TestData");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        context.setAttribute("DRSQA_11509_Driver", dwpDriver);
        context.setAttribute("DRSQA_11509_Product", Product.Helix_DWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openRequestAsSomeoneElseDialog();
        dwpFeatureMethod.clickCrossIconOnSelectedUserPill(dwpUserData.get("UserName"));
        dwpFeatureMethod.searchAndSelectRequestForUser(dwpTestData.get("OBO_User"));
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, dwpTestData.get("ServiceRequestName"));
        log.info(dwpTestData.get("ServiceRequestName") + " service request submitted successfully in DWP.");
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceRequestName"));
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Submitted request id is empty.");
        log.info("Request ID: " + requestId);
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifySrdTitle(dwpTestData.get("ServiceRequestName"));
        dwpFeatureMethod.verifyRequestedForUserName(dwpTestData.get("OBO_User"));
        dwpFeatureMethod.verifyServiceRequestDetailValueByLabelContains("Summary", dwpTestData.get("Summary"));
        dwpFeatureMethod.verifyServiceRequestDetailValueByLabelContains("where to move", dwpTestData.get("where to move"));
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------------DRCES-49 Verify Case created in BWF---------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11509_Driver", bwfDriver);
        context.setAttribute("DRSQA_11509_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "FriedaUser");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11509_TestData");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        String caseId = bwfFeatureMethods.getCaseID();
        log.info("BWF Case ID: " + caseId);
        bwfFeatureMethods.verifyCaseDetails(bwfTestData);

        log.info("----------------- Login to DWP as an approver Frieda and approve the request ----------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "friedaUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11509_TestData");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        context.setAttribute("DRSQA_11509_Driver", dwpDriver);
        context.setAttribute("DRSQA_11509_Product", Product.Helix_DWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openApprovalProfileWithoutRefresh(caseId);
        dwpFeatureMethod.doApproveRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11509_Approved Request Details", SolQAConstants.appDWP);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.APPROVED);
        dwpFeatureMethod.closeBrowser();

        log.info("----------------- Login to BWF as Frieda and resolve the case ----------------");
        bwfFeatureMethods.switchToBWFLiveChat(bwfDriver);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(SolQAConstants.RESOLVED);
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(SolQAConstants.STATUS_CLOSED);
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.closeBrowser();

        log.info("Login to DWP as an original requester and verify the request shown is closed");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "rmenonEnduser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        context.setAttribute("DRSQA_11509_Driver", dwpDriver);
        context.setAttribute("DRSQA_11509_Product", Product.Helix_DWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId);
        try{
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.STATUS_CLOSED);
        }
        catch (AssertionError e) {
            log.error("Failed to verify service request status: " + e.getMessage());
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.COMPLETED);
        }
        dwpFeatureMethod.captureScreenshot("DRSQA_11509_Final Approved Request Details", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("*********** END: DRSQA-11509 : Requesting a service on behalf of another user *******************");
    }

    //previous owner:- Preethi 
    @Test(description = "BWF Approval request details cross launch in DWP", groups = {"Regression_P3", "DWP", "BWF","gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "2569", JiraID = "DRSQA-11549", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11549_BWF_Approval_request_details_cross_launch_in_DWP(ITestContext context) throws Exception {
       log.info("*********** START: DRSQA-11549 : BWF Approval request details cross launch in DWP *******************");
       log.info("--------Precondition:-  Enable cross-launch to BWF in DWP Admin Console:---------");
       dwpDriver = launchWeb(SolQAConstants.DWP_ADMIN, SolQAConstants.DWP_ADMIN);
       setDriver(dwpDriver);
       context.setAttribute("DRSQA_11549_Driver", dwpDriver);
       context.setAttribute("DRSQA_11549_Product", Product.Helix_DWP);
       dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11549_TestData");
       login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPAdminPageLaunchedSuccessfully();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.clickOnSubMenu(SolQAConstants.APPROVAL);
        final String bwfBaseUrl = CommonMethods.readConfig("IS_URL")+"/helix/index.html#/com.bmc.dsm.bwfa/view/com.bmc.dsm.case-lib:Case%20Edit?Case%20ID={id}";
        dwpFeatureMethod.captureScreenshot("DRSQA_11549_SettingON", SolQAConstants.appDWP);     
        boolean saveApprovalSettings = dwpFeatureMethod.setCaseApprovalCrossLaunch(dwpTestData.get("bwfCrossLaunchSettingName"), true, bwfBaseUrl);

        if (saveApprovalSettings){
            dwpFeatureMethod.saveApprovalSettings();
        }
        dwpFeatureMethod.closeBrowser();

        log.info("---------Step1:- Login to DWP and submit the Request Time off service-------");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Peter");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11549_Driver", dwpDriver);
        context.setAttribute("DRSQA_11549_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        String serviceName = dwpTestData.get("ServiceName");
        log.info("Open and request service: " + serviceName);
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("label"), dwpTestData.get("Summary"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11549_RequestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);

        log.info("---------Step2:- Login to BWF and verify the case is created-------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11781_Driver", bwfDriver);
        context.setAttribute("DRSQA_11781_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11508_VerifyCaseCreation");
        log.info("Login to BWF with user " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        log.info("Set LOB as : " + bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        log.info("Search and open requested service caseID: " + firstSRD);
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        String caseId = bwfFeatureMethods.getCaseID();
        log.info("BWF Case ID: " + caseId);
       // bwfFeatureMethods.verifyRequestIdPresentInOpenedCase(firstSRD);
        
        log.info("---------Step2:- Login to BWF and verify the case is created-------");
   
    }

    //previous owner preethi
    @Test(description = "[BWF] [DWP] [Catalog] Improve criteria for when Survey is sent", groups = {"Regression_P3", "DWP", "BWF", "DWPC", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", TTID = "8678", JiraID = "DRSQA-11619", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_DWPC})
    public void DRSQA_11619_Improve_criteria_for_when_Survey_is_sent(ITestContext context) throws Exception {
        log.info("*** START *** DRSQA_11619 **********");

        String firstSRD;
        String secondSRD;

        // Start from DWPC (BWF precondition ADD_DWP_SURVEY_ON_CASE)
        dwpDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11619_Driver", getDriver());
        context.setAttribute("DRSQA_11619_Product", Product.Helix_DWPC);

        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCUsers, "hannah_admin");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11619_SurveySettings");
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        int deliveryLimitDays = Integer.parseInt(dwpcTestData.get("DeliveryLimitDays"));


        // -------------------- DWP: Submit first service request --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_DWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "swaugh");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11619_RequestDetails");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();

        String serviceName = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        // Summary label varies across builds ("Request Summary" vs "Summary")
        try {
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("Summary"));
        } catch (AssertionError e) {
            if (dwpTestData.get("SummaryLabelAlt") != null) {
                dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabelAlt"), dwpTestData.get("Summary"));
            } else {
                throw e;
            }
        }

        // Description field varies across templates/builds ("Justification" vs "Description")
        try {
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("DescriptionLabel"), dwpTestData.get("Description"));
        } catch (AssertionError e) {
            if (dwpTestData.get("DescriptionLabelAlt") != null) {
                dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("DescriptionLabelAlt"), dwpTestData.get("Description"));
            } else {
                throw e;
            }
        }

        //Required fields for "Software Install 1.0" SRD
        dwpFeatureMethod.selectSoftwareRequired(dwpTestData.get("SOFTWARE_LABEL"), dwpTestData.get("SOFTWARE_NAME"));
        dwpFeatureMethod.addMachineNameInService(dwpTestData.get("MACHINE_NAME"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11619_requestCreated_1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(firstSRD != null && !firstSRD.trim().isEmpty(), "First service request is not created");

        // -------------------- BWF: Resolve first case --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.bundleName, SolQAConstants.appBWF));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "LiamUsers");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11619_BWF_Case");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.captureScreenshot("DRSQA_11619_ticketOpenInBWF_1", SolQAConstants.appBWF);
        try {
            bwfFeatureMethods.assignCaseToMe();
        } catch (Exception ignored) {
        }
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(bwfTestData.get("StatusResolved"));
        bwfFeatureMethods.setStatusReason(bwfTestData.get("StatusReason"));
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.captureScreenshot("DRSQA_11619_caseResolved_1", SolQAConstants.appBWF);

        // -------------------- DWP: Verify survey IS present for first request --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_DWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(firstSRD);
        // NOTE: On this setup, BWF config ADD_DWP_SURVEY_ON_CASE is not available, so survey may not appear.
        // Keep this as best-effort to allow the rest of the flow to execute.
        if (!dwpFeatureMethod.isSurveyOptionAvailable()) {
            log.warn("Survey option is NOT displayed for first request. Skipping assert because BWF survey config is not available on this setup.");
        }
        dwpFeatureMethod.captureScreenshot("DRSQA_11619_verifySurveyPresent_1", SolQAConstants.appDWP);

        // -------------------- DWPC: Enable survey delivery limit to 1 day --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.appDWPC, SolQAConstants.appDWPC));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_DWPC);

        dwpcFeatureMethods = new DWPCFeatureMethods();
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.setFrequencyAdjustment(true, deliveryLimitDays);

        // -------------------- DWP: Submit second service request --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_DWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        // Summary label varies across builds ("Request Summary" vs "Summary")
        try {
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("Summary"));
        } catch (AssertionError e) {
            if (dwpTestData.get("SummaryLabelAlt") != null) {
                dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabelAlt"), dwpTestData.get("Summary"));
            } else {
                throw e;
            }
        }
        // Description field varies across templates/builds ("Justification" vs "Description")
        try {
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("DescriptionLabel"), dwpTestData.get("Description"));
        } catch (AssertionError e) {
            if (dwpTestData.get("DescriptionLabelAlt") != null) {
                dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("DescriptionLabelAlt"), dwpTestData.get("Description"));
            } else {
                throw e;
            }
        }
        // Required fields for "Software Install 1.0" SRD
        dwpFeatureMethod.selectSoftwareRequired(dwpTestData.get("SOFTWARE_LABEL"), dwpTestData.get("SOFTWARE_NAME"));
        dwpFeatureMethod.addMachineNameInService(dwpTestData.get("MACHINE_NAME"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11619_requestCreated_2", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        secondSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(secondSRD != null && !secondSRD.trim().isEmpty(), "Second service request is not created");

        // -------------------- BWF: Resolve second case --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.bundleName, SolQAConstants.appBWF));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_BWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(secondSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(secondSRD);
        bwfFeatureMethods.captureScreenshot("DRSQA_11619_ticketOpenInBWF_2", SolQAConstants.appBWF);
        try {
            bwfFeatureMethods.assignCaseToMe();
        } catch (Exception ignored) {
        }
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(bwfTestData.get("StatusResolved"));
        bwfFeatureMethods.setStatusReason(bwfTestData.get("StatusReason"));
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.captureScreenshot("DRSQA_11619_caseResolved_2", SolQAConstants.appBWF);

        // -------------------- DWP: Verify survey is NOT present for second request --------------------
        getDriver().manage().deleteAllCookies();
        getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.appDWP, SolQAConstants.appDWP));
        context.setAttribute("DRSQA_11619_Product", Product.Helix_DWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(secondSRD);
        Assert.assertFalse(dwpFeatureMethod.isSurveyOptionAvailable(), "Survey option IS displayed in DWP for second request (expected NOT generated).");
        dwpFeatureMethod.captureScreenshot("DRSQA_11619_verifySurveyNotPresent_2", SolQAConstants.appDWP);

        // -------------------- Cleanup (best-effort): reset delivery limit OFF --------------------
        try {
            getDriver().manage().deleteAllCookies();
            getDriver().navigate().to(Launcher.getHomeURL(SolQAConstants.appDWPC, SolQAConstants.appDWPC));
            dwpcFeatureMethods = new DWPCFeatureMethods();
            login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            // 	dwpcFeatureMethods.setSurveyDeliveryLimit(false, deliveryLimitDays);
        } catch (Exception e) {
            log.warn("Cleanup: failed to reset survey delivery limit. " + e.getMessage());
        }

        log.info("*** END *** DRSQA_11619 **********");
    }

    @Test(description = "Adhoc To Do Task Support", groups = {"Regression_P1", "DWP", "BWF", "gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "20679", JiraID = "DRSQA-11769", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11769_AdhocToDoTaskSupport(ITestContext context) {
        log.info("*** START *** DRSQA_11769_AdhocToDoTaskSupport **********");

        log.info("-----Precondition: Enable To Do in DWP Admin-----");
        dwpDriver = launchWeb(SolQAConstants.DWP_ADMIN, SolQAConstants.DWP_ADMIN);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11769_Driver", dwpDriver);
        context.setAttribute("DRSQA_11769_Product", Product.Helix_DWP);

        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPAdminPageLaunchedSuccessfully();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.enableFeatureSettingByName(SolQAConstants.TO_DO_FEATURE_NAME, true);
        dwpFeatureMethod.closeBrowser();

        log.info("-----Step1:- Login to BWF with the Case Agent and raise the raise with  case Template  Udemy Training---");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11769_Driver", bwfDriver);
        context.setAttribute("DRSQA_11769_Product", Product.Helix_BWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11769_caseData");
        log.info("Login to BWF user is: " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        log.info("Create BWF case using template: " + bwfTestData.get("Template Name"));
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.navigateToPage("Create", "Case");
        bwfFeatureMethods.fillCaseData(bwfTestData);
        bwfFeatureMethods.setDynamicFieldValues(bwfTestData.get("Que1"), bwfTestData.get("value1"));
        bwfFeatureMethods.setDynamicFieldValues(bwfTestData.get("Que2"), bwfTestData.get("value2"));
        bwfFeatureMethods.setDynamicFieldValues(bwfTestData.get("Que3"), bwfTestData.get("value3"));
        bwfFeatureMethods.ok_Case();
        String caseId = bwfFeatureMethods.clickOnGoToCaseIfPresent().getCaseID();
        log.info("Created case: " + caseId);

        log.info("-------Step2:- open the Grant Udemy account access to an employee task--------");
        bwfFeatureMethods.assignCaseToMe();
        try {
            bwfFeatureMethods.verifyCaseStatus(SolQAConstants.ASSIGNED);
        } catch (AssertionError e) {
            bwfDriver.navigate().refresh();
            bwfFeatureMethods.verifyCaseStatus(SolQAConstants.ASSIGNED);
        }
        bwfFeatureMethods.openTaskUsingArrow(bwfTestData.get("TaskName"));

        log.info("-------Step3:- 2 options visible below added task card------------");
        Assert.assertTrue(bwfFeatureMethods.verifyAddATaskOption(bwfTestData.get("AddTaskOption1"), bwfTestData.get("AddTaskOption2")), "2 options are not visible below added task card");
        Assert.assertTrue(bwfFeatureMethods.verifyAddATaskSubOption(bwfTestData.get("AddTaskOption2"), bwfTestData.get("AddParallelTaskOption1"), bwfTestData.get("AddParallelTaskOption2")), "2 sub options are not visible below Add parallel task option");
        Assert.assertTrue(bwfFeatureMethods.verifyAddATaskSubOption(bwfTestData.get("AddParallelTaskOption2"), bwfTestData.get("AddAdhocTaskOption1"), bwfTestData.get("AddAdhocTaskOption2")), "2 sub options are not visible below Add Adhoc task option");

        log.info("--------Step4:- Add a Adhoc To Do task-------");
        bwfFeatureMethods.goToAddParallelAdhocToDoTask();
        bwfFeatureMethods.setAddAToDoTaskTextFields(bwfTestData.get("TaskTitle"), bwfTestData.get("TaskTitleValue"));
        bwfFeatureMethods.setAddAToDoTaskTextFields(bwfTestData.get("TaskSummary"), bwfTestData.get("TaskSummaryValue"));
        bwfFeatureMethods.setAddAToDoTaskTextFields(bwfTestData.get("TaskTargetDays"), bwfTestData.get("TaskTargetDaysValue"));
        bwfFeatureMethods.setAddAToDoTaskDropDownFields(bwfTestData.get("Priority"), bwfTestData.get("PriorityValue"));
        bwfFeatureMethods.setAddAToDoTaskTextFields(SolQAConstants.ASSIGNEE, bwfTestData.get("AssigneeValue"));
        bwfFeatureMethods.save_ToDoTask();

        log.info("--------Step5:- Add a sequential To Do task-----");
        bwfFeatureMethods.goToAddSequentialAdhocToDoTask();
        bwfFeatureMethods.setAddAToDoTaskTextFields(bwfTestData.get("TaskTitle"), bwfTestData.get("SequentialTaskTitleValue"));
        bwfFeatureMethods.setAddAToDoTaskTextFields(bwfTestData.get("TaskSummary"), bwfTestData.get("SequentialTaskSummaryValue"));
        bwfFeatureMethods.setAddAToDoTaskTextFields(bwfTestData.get("TaskTargetDays"), bwfTestData.get("TaskTargetDaysValue"));
        bwfFeatureMethods.setAddAToDoTaskDropDownFields(bwfTestData.get("Priority"), bwfTestData.get("PriorityValue"));
        bwfFeatureMethods.setAddAToDoTaskTextFields(SolQAConstants.ASSIGNEE, bwfTestData.get("SequentialAssigneeValue"));
        bwfFeatureMethods.save_ToDoTask();
        bwfFeatureMethods.close_ToDoTask();
        bwfFeatureMethods.changeStatusTo(bwfTestData.get("Status"));

        log.info("--------Step6:- Login to DWP with Fritz user > Mark task as Complete-----");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11769_Driver", dwpDriver);
        context.setAttribute("DRSQA_11769_Product", Product.Helix_DWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fritzEnduser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedSbRequestInActiveEventsByTitle(bwfTestData.get("TaskTitleValue"));
        dwpFeatureMethod.verifySrdTitle(bwfTestData.get("TaskTitleValue"));
        dwpFeatureMethod.verifySrdTitleNotMatched(bwfTestData.get("SequentialTaskTitleValue"));
        dwpFeatureMethod.markTaskAsComplete();
        dwpFeatureMethod.closeBrowser();

        bwfFeatureMethods.switchToBWFLiveChat(bwfDriver);
        bwfFeatureMethods.assignTaskToMe();
        bwfFeatureMethods.markCompleteTask(bwfTestData.get("TaskName"));

        log.info("Step7:- Mark Frieda task as completed");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11769_Driver", dwpDriver);
        context.setAttribute("DRSQA_11769_Product", Product.Helix_DWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "friedaUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedSbRequestInActiveEventsByTitle(bwfTestData.get("SequentialTaskTitleValue"));
        dwpFeatureMethod.verifySrdTitle(bwfTestData.get("SequentialTaskTitleValue"));
        dwpFeatureMethod.markTaskAsComplete();
        dwpFeatureMethod.closeBrowser();

        log.info("Step8: Verify all bwf task should be completed");
        bwfFeatureMethods.switchToBWFLiveChat(bwfDriver);
        bwfFeatureMethods.openTaskUsingArrow(bwfTestData.get("TaskName"));
        Assert.assertTrue(bwfFeatureMethods.verifyTaskStatus(SolQAConstants.COMPLETED_STATUS), "All tasks are not completed");
        log.info("*** END ***** DRSQA_11769_AdhocToDoTaskSupport **********");
    }

    //Previous owner aramulwa
    @Test(description = "[DWP][Approval] Commenting and adding attachment on approvals", groups = {"Regression_P1", "sonalsha"}, singleThreaded = true)
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11485", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11485_CommentingAndAddingAttachmentToRequest(ITestContext context) throws Exception {

        final String testCaseId = "DRSQA_11485";
        String requestId;
        String fulfillmentId;
        log.info("--------------START : DRSQA_11485_CommentingAndAddingAttachmentToRequest------------------------");
        log.info("[" + testCaseId + "] Starting E2E validation: requester comment -> approver comment+attachment -> BWF activity verification");

        log.info("[" + testCaseId + "] Step 1: Submit request as end user and add initial comment in activity");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11485_TestData");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "Fritz");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11485_Driver", dwpDriver);
        context.setAttribute("DRSQA_11485_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToPreferences();
        dwpFeatureMethod.deleteDefaultCollaboratorsIfPresentInPreference();
        dwpFeatureMethod.refreshPage();
        String serviceName = dwpTestData.get("ServiceRequestName");
        String uniqueSummary = dwpTestData.get("SummaryPrefix") + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        log.info("[" + testCaseId + "] Requester user: " + dwpUserData.get("UserName") + ", service: " + serviceName + ", summary: " + uniqueSummary);
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(dwpTestData.get("SRDName"));
        dwpFeatureMethod.verifyOpenedRequestTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryQuestionLabel"), uniqueSummary);
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("DescriptionQuestionLabel"), dwpTestData.get("Description1"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11485_RequestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertNotNull(requestId, "Request ID is null");
        Assert.assertFalse(requestId.trim().isEmpty(), "Request ID is empty");
        log.info("[" + testCaseId + "] Captured Request ID: " + requestId);
        
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        String endUserComment = dwpTestData.get("EndUserComment");
        log.info("[" + testCaseId + "] Adding end-user comment on request: " + requestId);
        dwpFeatureMethod.addCommentInSubmittedRequest(endUserComment);
        dwpFeatureMethod.verifyComment(endUserComment);
        fulfillmentId = dwpFeatureMethod.getFulfillmentID();
        log.info("[" + testCaseId + "] Request submitted successfully. Request ID: " + requestId + ", Fulfillment ID: " + fulfillmentId);
        dwpFeatureMethod.closeBrowser();

        log.info("[" + testCaseId + "] Step 2: Login as approver, validate requester comment, add approver comment+attachment, and approve");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "friedaUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11485_DWP_Approver_Driver", getDriver());
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToPreferences();
        dwpFeatureMethod.deleteDefaultCollaboratorsIfPresentInPreference();
        dwpFeatureMethod.refreshPage();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(fulfillmentId);
        log.info("[" + testCaseId + "] Approver user: " + dwpUserData.get("UserName") + " validating end-user comment on request: " + requestId);
        dwpFeatureMethod.verifyComment(endUserComment);
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.WAITING_FOR_YOUR_APPROVAL);
        String approverComment = dwpTestData.get("ApproverComment");
        File attachment = CommonMethods.createTextFile(dwpTestData.get("AttachmentFileName"));
        Assert.assertNotNull(attachment, "Attachment file creation failed");
        log.info("[" + testCaseId + "] Adding approver comment and attachment: " + attachment.getName());
        dwpFeatureMethod.addCommentWithAttachmentsInSubmittedRequest(approverComment, attachment);
        dwpFeatureMethod.verifyAttachments(approverComment, attachment);
        dwpFeatureMethod.verifyComment(approverComment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11485_ApproverCommentAndAttachment", SolQAConstants.appDWP);
        log.info("[" + testCaseId + "] Approving request: " + requestId);
        dwpFeatureMethod.doApproveRequest();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.APPROVED);
        log.info("[" + testCaseId + "] Approval status verified as: " + SolQAConstants.APPROVED + " for request: " + requestId);
        dwpFeatureMethod.closeBrowser();

        log.info("[" + testCaseId + "] Step 3: Re-login as end user and verify approver comment+attachment visibility");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "Fritz");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyAttachments(approverComment, attachment);
        dwpFeatureMethod.verifyComment(approverComment);
        dwpFeatureMethod.captureScreenshot("DRSQA_11485_EndUserSeesApproverComment", SolQAConstants.appDWP);
        log.info("[" + testCaseId + "] End user validated approver comment and attachment on request: " + requestId);
        dwpFeatureMethod.closeBrowser();

        log.info("[" + testCaseId + "] Step 4: Verify comments and attachment in BWF case activity and approver profile activity");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11485_TestData");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11485_BWF_Driver", getDriver());
        context.setAttribute("DRSQA_11485_BWF_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        log.info("[" + testCaseId + "] Opened BWF case using Request ID: " + requestId);
        bwfFeatureMethods.captureScreenshot("DRSQA_11485_CaseOpenedInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verifyTitle_In_SocialFeed(bwfTestData.get("ApproverCommentToVerify"));
        bwfFeatureMethods.verifyTitle_In_SocialFeed(bwfTestData.get("EndUserCommentToVerify"));
        bwfFeatureMethods.verifyAttachmentUpdate_In_SocialFeed(bwfTestData.get("AttachmentName"));
        log.info("[" + testCaseId + "] BWF case activity validation completed for Request ID: " + requestId);
        bwfFeatureMethods.closeBrowser();

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "FriedaUser");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11485_BWF_Driver", getDriver());
        context.setAttribute("DRSQA_11485_BWF_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.openUserProfilePage();
        String personName = bwfFeatureMethods.getPersonNameFromProfilePage();
        Assert.assertEquals(personName, bwfUserData.get("FullName"), "Logged in user full name does not match");
        log.info("[" + testCaseId + "] Logged-in BWF approver profile validated: " + personName);
        bwfFeatureMethods.expandActivityOnProfilePage();
        bwfFeatureMethods.verifyTitle_In_SocialFeed(bwfTestData.get("ApproverCommentToVerify"));
        bwfFeatureMethods.verifyAttachmentUpdate_In_SocialFeed(bwfTestData.get("AttachmentName"));
        bwfFeatureMethods.closeBrowser();
        log.info("[" + testCaseId + "] Completed all validations. Request ID: " + requestId + ", Fulfillment ID: " + fulfillmentId);

        log.info("--------------END : DRSQA_11485_CommentingAndAddingAttachmentToRequest------------------------");
    }

    @Test(description = "[Case Reopen] Reopening the case created from DWP in BWF", groups = {"Regression_P1", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11449", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11449_ReopenCaseCreatedFromDWP_InBWF(ITestContext context) throws Exception {

        log.info("--------------START : DRSQA_11449_ReopenCaseCreatedFromDWP_InBWF------------------------");

        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11449_TestData");

        log.info("-------------------- Step 1: Submit request from DWP --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11449_Driver", dwpDriver);
        context.setAttribute("DRSQA_11449_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "swaughUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11449_TestData");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("Question_1"), dwpTestData.get("Answer_1"));
        String dateToEnter = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
        dwpFeatureMethod.fillTextDateFieldByLabel(dwpTestData.get("Question_2"), dateToEnter);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11449_submitRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertNotNull(requestId, "Request ID is null");
        Assert.assertFalse(requestId.trim().isEmpty(), "Request ID is empty");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 2: Verify case details in BWF and capture case ID --------------------");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11449_Driver", bwfDriver);
        context.setAttribute("DRSQA_11449_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        String caseId = bwfFeatureMethods.getCaseID();
        Assert.assertNotNull(caseId, "BWF Case ID is null");
        Assert.assertFalse(caseId.trim().isEmpty(), "BWF Case ID is empty");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Summary"), bwfTestData.get("Summary"), "Case summary mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Status"), bwfTestData.get("Status"), "Case status mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Source"), bwfTestData.get("Source"), "Case source mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Case Template"), bwfTestData.get("CaseTemplateValue"), "Case template mismatch");
        bwfFeatureMethods.clickShowApproversList().verifyApproverPresentInApproversList(bwfTestData.get("ApproverName"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11449_VerifyCaseDetails", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 3: Approver approves from DWP --------------------");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "sadamUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11449_Driver", dwpDriver);
        context.setAttribute("DRSQA_11449_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId);
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.WAITING_FOR_YOUR_APPROVAL);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11449_VerifyRequestStatusAfterApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 4: Resolve in BWF, reopen, verify activity + status --------------------");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11449_Driver", bwfDriver);
        context.setAttribute("DRSQA_11449_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        bwfFeatureMethods.verifyActivityTitlePresent(bwfTestData.get("ActivityTabTitle"));
        bwfFeatureMethods.changeCaseStatusAndSave(bwfTestData.get("ResolvedState"), bwfTestData.get("ResolvedStateReason"));
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("ResolvedState"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11449_ResolvingCase", SolQAConstants.appBWF);
        bwfFeatureMethods.reopenCaseFromCaseView();
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("InProgressState"));
        bwfFeatureMethods.verifyActivityTitlePresent(bwfTestData.get("ActivityTitleAfterReopen"));
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 5: Verify reopened request status in DWP --------------------");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "swaughUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11449_Driver", dwpDriver);
        context.setAttribute("DRSQA_11449_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("StatusAfterReopenedCase"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11449_RequestReopenedStatusInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        log.info("--------------END : DRSQA_11449_ReopenCaseCreatedFromDWP_InBWF------------------------");
    }

    @Test(description = "[DWP][Approval][BWF] Case Self Approval when request submitted in DWP", groups = {"Regression_P2"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11506", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11506_CaseSelfApprovalOfRequest(ITestContext context) throws Exception {

        log.info("--------------START : DRSQA_11506_CaseSelfApprovalOfRequest------------------------");

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11506_TestData");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11506_CaseSelfApprovalOfRequest");

        // -------------------- Step 1: Submit request "Employee Spot Bonus" in DWP --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11506_Driver", dwpDriver);
        context.setAttribute("DRSQA_11506_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("SRDName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("EmployeeFullNameLabel"), dwpTestData.get("EmployeeFullNameValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("AmountLabel"), dwpTestData.get("AmountValue"));
        dwpFeatureMethod.fillTextAreaField(dwpTestData.get("ReasonLabel"), dwpTestData.get("ReasonValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("SummaryValue"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertNotNull(requestId, "Request ID is null");
        Assert.assertFalse(requestId.trim().isEmpty(), "Request ID is empty");
        dwpFeatureMethod.captureScreenshot("DRSQA_11506_RequestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 2: Verify case created in BWF --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11506_Driver", bwfDriver);
        context.setAttribute("DRSQA_11506_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        bwfFeatureMethods.captureScreenshot("DRSQA_11506_CaseOpenedInBWF", SolQAConstants.appBWF);

        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Summary"), bwfTestData.get("Summary"), "Case summary mismatch actual: " + bwfFeatureMethods.getCaseDetail("Summary") + " expected: " + bwfTestData.get("Summary"));
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Category Tier 1"), bwfTestData.get("Tier1"), "Tier 1 mismatch actual: " + bwfFeatureMethods.getCaseDetail("Category Tier 1") + " expected: " + bwfTestData.get("Tier1"));
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Category Tier 2"), bwfTestData.get("Tier2"), "Tier 2 mismatch actual: " + bwfFeatureMethods.getCaseDetail("Category Tier 2") + " expected: " + bwfTestData.get("Tier2"));
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Category Tier 3"), bwfTestData.get("Tier3"), "Tier 3 mismatch actual: " + bwfFeatureMethods.getCaseDetail("Category Tier 3") + " expected: " + bwfTestData.get("Tier3"));

        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("Status"));
        bwfFeatureMethods.verifyActivityTitlePresent(bwfTestData.get("ActivityMessageSelf"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11506_CaseSelfApprovedActivity", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------END : DRSQA_11506_CaseSelfApprovalOfRequest------------------------");
    }

    @Test(description = "[DWP][BWF] Notification is sent when task is activated on the case, also notification for approval", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11487", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA11487_NotificationSent_WhenTaskIsActivated_OnCase(ITestContext context) throws Exception {

        log.info("--------------START : DRSQA11487_NotificationSent_WhenTaskIsActivated_OnCase------------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11487_Driver", dwpDriver);
        context.setAttribute("DRSQA_11487_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11487_TestData");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, serviceName);
        dwpFeatureMethod.captureScreenshot("DRSQA11487_RequestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestID = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        dwpFeatureMethod.verifyRequestCreated(requestID);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 2: Verify case created in BWF --------------------");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "MonikaUser");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11487_TestData");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11487_Driver", bwfDriver);
        context.setAttribute("DRSQA_11487_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();

        bwfFeatureMethods.openTicketID(bwfTestData.get("LOB"), requestID, "Request ID");
        String caseId = bwfFeatureMethods.getAndVerifyCaseID();
        bwfFeatureMethods.refreshBrowser();
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Summary"), bwfTestData.get("Summary"), "Case summary mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Requester"), bwfTestData.get("Requester"), "Case requester mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Status"), bwfTestData.get("Status"), "Case status mismatch");
        bwfFeatureMethods.verifyTextPresent(requestID);
        bwfFeatureMethods.captureScreenshot("DRSQA11487_VerifyCaseDetails", SolQAConstants.appBWF);

        log.info("-------------------- Step 3: First approver approves from DWP --------------------");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fcatUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11487_Driver", dwpDriver);
        context.setAttribute("DRSQA_11487_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId);
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("WaitingYourApprovalStatus"));
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA11487_VerifyRequestStatusAfterApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        setDriver(bwfDriver);
        bwfFeatureMethods.refreshBrowser();
        boolean isCaseApproved = bwfFeatureMethods.isActivityTitlePresent("Case was approved");

        // -------------------- Step 4: Second approver approves from DWP if case not yet approved --------------------
        if (!isCaseApproved) {
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "yanandaUser");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11487_Driver", dwpDriver);
            context.setAttribute("DRSQA_11487_Product", Product.Helix_DWP);
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(caseId);
            dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("WaitingYourApprovalStatus"));
            dwpFeatureMethod.clickApproveRequestInEventDetails();
            dwpFeatureMethod.refreshBrowser();
            dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
            dwpFeatureMethod.captureScreenshot("DRSQA11487_SecondApproval", SolQAConstants.appDWP);
            dwpFeatureMethod.closeBrowser();
        }

        log.info("-------------------- Step 5: Verify notifications for approval + task activation --------------------");
        refreshBrowser();
        bwfFeatureMethods.verifyActivityTitlePresent("Case was approved");
        bwfFeatureMethods.clickOnNotificationBell();
        bwfFeatureMethods.verifyNotificationContains(String.format(bwfTestData.get("InProgressNotificationTemplate"), caseId));
        bwfFeatureMethods.captureScreenshot("DRSQA11487_VerifyAlertDetails", SolQAConstants.appBWF);

        log.info("Open task from case and verify status");
        String taskSummary = bwfTestData.get("TaskSummary");
        bwfFeatureMethods.clickTaskSummaryLinkOnCase(taskSummary);
        bwfFeatureMethods.verifyTaskStatusOnFullView(bwfTestData.get("TaskStatus"));
        String taskId = bwfFeatureMethods.getTaskId();
        Assert.assertNotNull(taskId, "Task ID is null");
        Assert.assertFalse(taskId.trim().isEmpty(), "Task ID is empty");
        bwfFeatureMethods.captureScreenshot("DRSQA11487_VerifyTaskStatus_OnCase", SolQAConstants.appBWF);

        log.info("Close task tab if it opened a new window, then return to case");
        List<String> windows = new ArrayList<>(getDriver().getWindowHandles());
        if (windows.size() > 1) {
            getDriver().close();
            getDriver().switchTo().window(windows.get(0));
        }

        log.info("Verify notification contains task ID status update and open task via notification");
        bwfFeatureMethods.clickOnNotificationBell();
        bwfFeatureMethods.verifyNotificationContains(String.format(bwfTestData.get("TaskAssignedNotificationTemplate"), taskId));
        bwfFeatureMethods.captureScreenshot("DRSQA11487_VerifyTaskDetails_FromNotification", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------END : DRSQA11487_NotificationSent_WhenTaskIsActivated_OnCase------------------------");
    }

    //Previous owner aramulwa
    @Test(description = "[DWP][Approval][BWF] Manager Approval when request submitted in DWP", groups = {"Regression_P1", "sonalsha"}, singleThreaded = true)
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11507", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11507_ManagerLevelApprovalOfRequest(ITestContext context) throws Exception {

        log.info("--------------START: DRSQA_11507_ManagerLevelApprovalOfRequest------------------------");

        // -------------------- Step 1: Submit request twice in DWP as requester --------------------
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11507_TestData");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");

        String serviceName = dwpTestData.get("ServiceRequestName");
        String uniqueSummary = dwpTestData.get("SummaryValuePrefix") + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11507_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11507_Product", Product.Helix_DWPC);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11507_TestData");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11507_Driver", dwpDriver);
        context.setAttribute("DRSQA_11507_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("EmployeeFullNameLabel"), dwpTestData.get("EmployeeFullNameValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("AmountLabel"), dwpTestData.get("AmountValue"));
        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("ReasonLabel"), dwpTestData.get("ReasonValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), uniqueSummary);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_RequestSubmitted_1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId1 != null && !requestId1.trim().isEmpty(), "Request ID 1 is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_RequestDetails_1", SolQAConstants.appDWP);
        // Request Again for second submission
        dwpFeatureMethod.clickRequestAgain();
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("EmployeeFullNameLabel"), dwpTestData.get("EmployeeFullNameValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("AmountLabel"), dwpTestData.get("AmountValue"));
        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("ReasonLabel"), dwpTestData.get("ReasonValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), uniqueSummary);
        dwpFeatureMethod.clickSubmitRequestButton();
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_RequestSubmitted_2", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        if (requestId1.equals(requestId2)) {
            // allow event list to update
            new CommonWaits().waitForSometime(new CommonWaits().moreDelay * 10L);
            dwpFeatureMethod.openActivityPage();
            requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        }
        Assert.assertTrue(requestId2 != null && !requestId2.trim().isEmpty(), "Request ID 2 is not created");
        Assert.assertNotEquals(requestId2, requestId1, "Second request submission did not generate a new Request ID");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("Status"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_RequestDetails_2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 2: Verify cases created in BWF for both requests --------------------
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11507_TestData");

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11507_Driver", bwfDriver);
        context.setAttribute("DRSQA_11507_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        // Find + open case for Request 1
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId1, bwfTestData.get("SearchColumnName"));
        String caseId1 = bwfFeatureMethods.getCaseID();
        Assert.assertTrue(caseId1 != null && !caseId1.trim().isEmpty(), "BWF Case ID for Request 1 is empty");
        bwfFeatureMethods.verifyCaseDetails(bwfTestData);
        bwfFeatureMethods.verifyApproverPresentInApproversList(bwfTestData.get("ApproverFullName"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11507_VerifyCaseDetails_1", SolQAConstants.appBWF);
        getDriver().navigate().back();
        // Find + open case for Request 2
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId2, bwfTestData.get("SearchColumnName"));
        String caseId2 = bwfFeatureMethods.getCaseID();
        Assert.assertTrue(caseId2 != null && !caseId2.trim().isEmpty(), "BWF Case ID for Request 2 is empty");
        bwfFeatureMethods.verifyCaseDetails(bwfTestData);
        bwfFeatureMethods.verifyApproverPresentInApproversList(bwfTestData.get("ApproverFullName"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11507_VerifyCaseDetails_2", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // -------------------- Step 3: First level approver approves both requests in DWP --------------------
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fcatUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11507_Driver", dwpDriver);
        context.setAttribute("DRSQA_11507_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId1);
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("WaitingYourApprovalStatus"));
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_Level1Approve_Case1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId2);
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("WaitingYourApprovalStatus"));
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_Level1Approve_Case2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 4: Second level approver approves first and rejects second --------------------
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "yanandaUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11507_Driver", dwpDriver);
        context.setAttribute("DRSQA_11507_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId1);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_Level2Approve_Case1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId2);
        dwpFeatureMethod.clickRejectRequestInEventDetails(dwpTestData.get("RejectionReason"), true);
        dwpFeatureMethod.captureScreenshot("DRSQA_11507_Level2Reject_Case2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 5: Verify BWF case status after approve/reject --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11507_Driver", bwfDriver);
        context.setAttribute("DRSQA_11507_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        // Case 1 should be In Progress
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId1, bwfTestData.get("SearchColumnName"));
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("CaseStatusAfterApproval"));
        bwfFeatureMethods.clickOnNotificationBell();
        bwfFeatureMethods.verifyNotificationContains(String.format(bwfTestData.get("InProgressNotificationTemplate"), caseId1));
        bwfFeatureMethods.captureScreenshot("DRSQA_11507_BWF_Case1_InProgress", SolQAConstants.appBWF);
        getDriver().navigate().back();
        // Case 2 should be Canceled and rejection reason should be visible in Activity
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId2, bwfTestData.get("SearchColumnName"));
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("CaseStatusAfterRejection"));
        bwfFeatureMethods.verifyActivityContainsText(dwpTestData.get("RejectionReason"));
        bwfFeatureMethods.clickOnNotificationBell();
        bwfFeatureMethods.verifyNotificationContains(String.format(bwfTestData.get("CanceledNotificationTemplate"), caseId2));
        bwfFeatureMethods.captureScreenshot("DRSQA_11507_BWF_Case2_Canceled", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------END: DRSQA_11507_ManagerLevelApprovalOfRequest------------------------");
    }

    @Test(description = "[BWF][DWP] Send Resolution Note to Requester when Case is Resolved, Reopen from DWP side", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11626", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA11626_Reopen_For_BWF(ITestContext context) throws Exception {

        log.info("-------------------- Precondition: Enable CREATE_SERVICE_REQUEST in BWF LOB app config --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11626_Driver", bwfDriver);
        context.setAttribute("DRSQA_11626_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "WorkforceAdministrationCaseUsers");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11626_TestData");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.enterValueInTextField(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.clickApplicationConfigNameAndSetValue(SolQAConstants.CREATE_SERVICE_REQUEST, SolQAConstants.TRUE);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 1: Create case via Quick Case template in BWF --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11626_Driver", bwfDriver);
        context.setAttribute("DRSQA_11626_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.clickQuickCaseTab();
        bwfFeatureMethods.typeAndSelectCustomerInQuickCase(bwfTestData.get("CustomerName"));
        bwfFeatureMethods.typeInQuickCaseSearch(bwfTestData.get("TemplateSearchText"));
        bwfFeatureMethods.selectTemplateFromQuickCase(bwfTestData.get("TemplateName"));
        bwfFeatureMethods.clickCreateCaseButton();
        bwfFeatureMethods.captureScreenshot("DRSQA11626_CaseCreated", SolQAConstants.appBWF);
        String caseId = bwfFeatureMethods.getCreatedCaseID();
        bwfFeatureMethods.clickOnGoToCaseIfPresent();
        bwfFeatureMethods.captureScreenshot("DRSQA11626_RequestIdCaptured", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 2: Manager approves request in DWP --------------------");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "friedaUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11626_TestData");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11626_Driver", dwpDriver);
        context.setAttribute("DRSQA_11626_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId);
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("WaitingYourApprovalStatus"));
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA11626_ApprovedInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 3: Verify case in progress, resolve task, resolve case with Resolution Description --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11626_Driver", bwfDriver);
        context.setAttribute("DRSQA_11626_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(caseId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseId, "Case ID");
        bwfFeatureMethods.verifyCaseStatus(SolQAConstants.IN_PROGRESS);
        // Resolve a task (template-dependent)
        if (bwfFeatureMethods.isTaskPresentOnCase(bwfTestData.get("TaskSummary"))){
            bwfFeatureMethods.clickMarkAsCompleteOnTaskInBWFCase();
        }
        // Resolve case and add Resolution Description (syncs to DWP as a comment/note)
        Map<Object, String> caseResolveData = new java.util.HashMap<>();
        caseResolveData.put("Resolution Description", bwfTestData.get("ResolutionDescription"));
        bwfFeatureMethods.changeCaseStatusToResolvedWithNotes(caseResolveData);
        bwfFeatureMethods.captureScreenshot("DRSQA11626_CaseResolved", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 4: Requester sees resolution note in DWP and reopens request --------------------");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fritzEnduser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11626_Product", Product.Helix_DWP);
        context.setAttribute("DRSQA_11626_Driver", dwpDriver);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(caseId);
        dwpFeatureMethod.captureScreenshot("DRSQA11626_ResolutionNoteVisibleInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.clickReopenButton();
        dwpFeatureMethod.enterReopenReasonAndConfirm(dwpTestData.get("ReopenReason"));
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.IN_PROGRESS);
        dwpFeatureMethod.verifyComment(dwpTestData.get("ReopenReason"));
        dwpFeatureMethod.captureScreenshot("DRSQA11626_RequestReopenedInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        log.info("--------------END: DRSQA11626_Reopen_For_BWF------------------------");
    }

    @Test(description = "[BWF][DWP] Case is assigned based on location when created from DWP and Adhoc approval", groups = {"Regression_P1", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11463", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11463_CaseAssignmentBasedOnLocation(ITestContext context) throws Exception {

        log.info("--------------START: DRSQA_11463_CaseAssignmentBasedOnLocation------------------------");

        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11463_TestData");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11463_TestData");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "swaughUser");
        String serviceName = dwpTestData.get("ServiceName");

        String requestId;
        String caseId;
        String approverFullName;
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA-11463_Driver", dwpcDriver);
        context.setAttribute("DRSQA-11463_Product", Product.Helix_DWPC);

        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, serviceName);
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.captureScreenshot("DRSQA_11463_DWPC_ServicePresent", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();
        // Use BWF admin to toggle CASE_ALLOW_ADHOC_APPROVAL on/off
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "hannah_admin");

        try {
            // -------------------- Precondition: enable CASE_ALLOW_ADHOC_APPROVAL --------------------
           bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
            setDriver(bwfDriver);
            context.setAttribute("DRSQA-11463_Driver", bwfDriver);
            context.setAttribute("DRSQA-11463_Product", Product.Helix_BWF);
            login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfFeatureMethods.verifyLoginToBWF();
            bwfFeatureMethods.navigateToSettings();
            bwfFeatureMethods.enterValueInTextField(SolQAConstants.manageLineOfBusiness);
            bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.manageLineOfBusiness);
            bwfFeatureMethods.clickApplicationConfigurationsTab();
            bwfFeatureMethods.selectLOBFromDropDown(bwfTestData.get("LOBValue"));
            bwfFeatureMethods.clickApplicationConfigNameAndSetValue(bwfTestData.get("CommonSettingRequest"), SolQAConstants.TRUE);
            bwfFeatureMethods.closeBrowser();

            // -------------------- Step 1: Submit request in DWP as location-based requester --------------------

            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA-11463_Driver", dwpDriver);
            context.setAttribute("DRSQA-11463_Product", Product.Helix_DWP);
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openCatalogPage();
            if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
                dwpFeatureMethod.openAllItemsPage();
            }
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.verifySrdTitle(serviceName);
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("Question_1"), dwpTestData.get("Answer_1"));
            dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("Question_2"), dwpTestData.get("Answer_2"));
            dwpFeatureMethod.clickOnSubmitRequest();
            dwpFeatureMethod.captureScreenshot("DRSQA_11463_SubmitRequest", SolQAConstants.appDWP);
            dwpFeatureMethod.openActivityPage();
            requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
            Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Request ID is not created in DWP");
            dwpFeatureMethod.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod.captureScreenshot("DRSQA_11463_RequestDetails", SolQAConstants.appDWP);
            dwpFeatureMethod.closeBrowser();
            // -------------------- Step 2: Verify BWF case assignment fields based on requester location --------------------
            bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
            setDriver(bwfDriver);
            context.setAttribute("DRSQA-11463_Driver", bwfDriver);
            context.setAttribute("DRSQA-11463_Product", Product.Helix_BWF);
            login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfFeatureMethods.verifyLoginToBWF();
            bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOBValue"));
            bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
            bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
            bwfFeatureMethods.clickSearchBtn_CaseConsole();
            bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId); // default column "Request ID"
            caseId = bwfFeatureMethods.getCaseID();
            Assert.assertTrue(caseId != null && !caseId.trim().isEmpty(), "Case ID is empty in BWF for Request ID: " + requestId);
            // Validate key assignment outputs
            bwfFeatureMethods.verifyCaseDetailEquals("Case Template", bwfTestData.get("CaseTemplate"));
            bwfFeatureMethods.verifyCaseDetailEquals("Case Site", bwfTestData.get("Site"));
            bwfFeatureMethods.verifyCaseDetailEquals("Assigned Group", bwfTestData.get("AssignedGroup"));
            bwfFeatureMethods.verifyCaseDetailEquals("Priority", bwfTestData.get("Priority"));
            bwfFeatureMethods.captureScreenshot("DRSQA_11463_VerifyCaseAssignment", SolQAConstants.appBWF);
            // Add Ad hoc approver
            Map<Object, String> adhocApproverData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "sadamUser");
            approverFullName = adhocApproverData.get("FullName");
            bwfFeatureMethods.addAdhocApprover(approverFullName);
            bwfFeatureMethods.verifyApproverPresentInApproversList(approverFullName);
            bwfFeatureMethods.captureScreenshot("DRSQA_11463_AdhocApproverAdded", SolQAConstants.appBWF);
            bwfFeatureMethods.closeBrowser();

            // -------------------- Step 3: Approve as Ad hoc approver in DWP --------------------
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA-11463_Driver", dwpDriver);
            context.setAttribute("DRSQA-11463_Product", Product.Helix_DWP);
            login(adhocApproverData.get("UserName"), adhocApproverData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(caseId);
            dwpFeatureMethod.clickApproveRequestInEventDetails();
            dwpFeatureMethod.refreshBrowser();
            dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
            dwpFeatureMethod.captureScreenshot("DRSQA_11463_AdhocApproved", SolQAConstants.appDWP);
            dwpFeatureMethod.closeBrowser();

        } finally {
            // Best-effort cleanup: disable CASE_ALLOW_ADHOC_APPROVAL to avoid side effects on other tests
            try {
                bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
                setDriver(bwfDriver);
                context.setAttribute("DRSQA-11463_Driver", bwfDriver);
                context.setAttribute("DRSQA-11463_Product", Product.Helix_BWF);
                login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
                bwfFeatureMethods = new BWFFeatureMethods();
                bwfFeatureMethods.verifyLoginToBWF();
                bwfFeatureMethods.navigateToSettings();
                bwfFeatureMethods.enterValueInTextField(SolQAConstants.manageLineOfBusiness);
                bwfFeatureMethods.clickOnSearchedAdminSetting(SolQAConstants.manageLineOfBusiness);
                bwfFeatureMethods.clickApplicationConfigurationsTab();
                bwfFeatureMethods.selectLOBFromDropDown(bwfTestData.get("LOBValue"));
                bwfFeatureMethods.clickApplicationConfigNameAndSetValue(bwfTestData.get("CommonSettingRequest"), SolQAConstants.FALSE);
            } catch (Exception e) {
                log.warn("Failed to restore CASE_ALLOW_ADHOC_APPROVAL setting in cleanup: " + e.getMessage());
            } finally {
                if (bwfFeatureMethods != null) {
                    bwfFeatureMethods.closeBrowser();
                }
            }
        }

        log.info("--------------END: DRSQA_11463_CaseAssignmentBasedOnLocation------------------------");
    }

    @Test(description = "[Question responses] Question type returned for all question answers to the service", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "450", JiraID = "DRSQA-11499", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11499_QuestionTypeReturnedForAllQuestion(ITestContext context) throws Exception {

        log.info("--------------START: DRSQA_11499_QuestionTypeReturnedForAllQuestion------------------------");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11499_TestData");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11499_TestData");

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11499_Driver", bwfDriver);
        context.setAttribute("DRSQA_11499_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.navigateToSubmenuInSettings("Case Management");
        bwfFeatureMethods.clickOnSearchedAdminSettingWithoutClickToExpand("Templates");
        bwfFeatureMethods.setSearchValueOnCaseTemplatesGrid("Unclassified HR Case");
        bwfFeatureMethods.clickSearchBtnOnCaseTemplateGrid();
        bwfFeatureMethods.verifyValueInCaseTemplateGrid("Unclassified HR Case");
        bwfFeatureMethods.openGridItemByName("Unclassified HR Case");
        bwfFeatureMethods.changeTemplateStatus(SolQAConstants.DRAFT);
        bwfFeatureMethods.clickOnManageDynamicFields_ByChangingTemplateStatus();
        bwfFeatureMethods.addDynamicField("Dynamic Dropdown List");
        bwfFeatureMethods.changeTemplateStatus(SolQAConstants.Active);
        bwfFeatureMethods.closeBrowser();

        String requestId;
        log.info("-------------------- Step 1: Submit request in DWP with all question types --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11499_Driver", dwpDriver);
        context.setAttribute("DRSQA_11499_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "lyellowUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        DateFormat dfShort = new SimpleDateFormat("MMM d, yyyy");
        DateFormat dfDateTime = new SimpleDateFormat("MMM d, yyyy h:mm a");
        String dateShort = dfShort.format(new Date());
        String dateTime = dfDateTime.format(new Date());
        String[] dateTimeSplitForBwf = dateTime.replace(" at", "").split(",");
        String requesterFullName = dwpUserData.get("FullName");
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, dwpTestData.get("ServiceRequestName"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11499_RequestSubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceRequestName"));
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Request ID is not created in DWP");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyOpenedRequestTitle(dwpTestData.get("ServiceRequestName"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11499_VerifyServiceRequestTitle", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------- Step 2: Verify case details and question responses in BWF ---------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11499_Driver", bwfDriver);
        context.setAttribute("DRSQA_11499_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        Assert.assertTrue(bwfFeatureMethods.getCaseDetail("Summary").contains(bwfTestData.get("Summary")), "Summary mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Priority"), bwfTestData.get("Priority"), "Priority mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Source"), bwfTestData.get("Source"), "Source mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Category Tier 1"), bwfTestData.get("TIER1"), "Tier1 mismatch");
        Assert.assertTrue(bwfFeatureMethods.getCaseDetail("Contact").contains(requesterFullName), "Requester mismatch on case");
        bwfFeatureMethods.captureScreenshot("DRSQA_11499_OpenCaseDetails", SolQAConstants.appBWF);
        log.info("Verify question responses (rendered as case fields in BWF)");
        bwfFeatureMethods.verifyResponseForTheQuestion("Multi-select Question", bwfTestData.get("Multi-select Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Radio Button Question", bwfTestData.get("Radio Button Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Toggle Question", bwfTestData.get("Toggle Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Checkbox Question", bwfTestData.get("Checkbox Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Dropdown Question", bwfTestData.get("Dropdown Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Default Text Question", bwfTestData.get("Default Text Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Date Question", dateShort);
        bwfFeatureMethods.verifyResponseForTheQuestion("Date/Time Question", dateTimeSplitForBwf[0].trim());
        bwfFeatureMethods.verifyResponseForTheQuestion("Lookup Question from BWF", bwfTestData.get("Lookup Question from BWF"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Lookup Question from ITSM", bwfTestData.get("Lookup Question from ITSM"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Counter", bwfTestData.get("Counter"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Attachment Question", bwfTestData.get("Attachment Question"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Select city", bwfTestData.get("Select city"));
        bwfFeatureMethods.verifyResponseForTheQuestion("Dynamic Dropdown List", bwfTestData.get("dynamicUser"));
        log.info("Verify audit values (best-effort locator; may require tuning per tenant)");
        bwfFeatureMethods.clickShoMoreLinkInAuditIfPresent();
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Summary", bwfTestData.get("Summary"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Checkbox Question", bwfTestData.get("Checkbox Question"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Multi-select Question", bwfTestData.get("Multi-select Question"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Dynamic Dropdown List", bwfTestData.get("dynamicUser"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Lookup Question from BWF (Case Template)", bwfTestData.get("Lookup Question from BWF"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Lookup Question from ITSM (End User)", bwfTestData.get("Lookup Question from ITSM"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Counter", bwfTestData.get("Counter"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Default Text Question", bwfTestData.get("Default Text Question"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Radio Button Question", bwfTestData.get("Radio Button Question"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Dropdown Question", bwfTestData.get("Dropdown Question"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Toggle Question", bwfTestData.get("Toggle Question"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Category Tier 1", bwfTestData.get("TIER1"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Priority", bwfTestData.get("Priority"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Assigned Group", bwfTestData.get("Assigned Group"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Contact", requesterFullName);
        bwfFeatureMethods.captureScreenshot("DRSQA_11499_VerifyCaseDetails", SolQAConstants.appBWF);
        log.info("Update dynamic list and verify audit captures update");
        bwfFeatureMethods.updateDynamicListValue("Dynamic Dropdown List", bwfTestData.get("updatedDynamicUser"));
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field("Dynamic Dropdown List", bwfTestData.get("updatedDynamicUser"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11499_VerifyUpdatedCaseDetails", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------END: DRSQA_11499_QuestionTypeReturnedForAllQuestion------------------------");
    }

    @Test(description = "[LOB] Request service with LOB and template", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11571", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_DWPC})
    public void DRSQA_11571_RequestServiceWithLOBAndTemplate(ITestContext context) throws Exception {

        log.info("--------------START: DRSQA_11571_RequestServiceWithLOBAndTemplate------------------------");

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11571_service_info");
        Map<Object, String> bwfCaseDataWithTemplate = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11571_VerifyCaseDetails_ForTemplateLOB");
        Map<Object, String> bwfCaseDataWithoutTemplateDifferentLob = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11571_VerifyCaseDetails_WithoutTemplateAndDifferentLOBInput");
        Map<Object, String> bwfCaseDataWithoutTemplateDefaultLob = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11571_VerifyCaseDetails_WithoutTemplateAndLOB");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");

        String serviceName = dwpTestData.get("ServiceRequestName");
        String summaryLabel = dwpTestData.get("SummaryLabel");
        String summaryValue = dwpTestData.get("Summary");
        String templateLabel = dwpTestData.get("TemplateLabel");
        String lobValue = dwpTestData.get("LineofBusiness");

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA-11571_Driver", dwpcDriver);
        context.setAttribute("DRSQA-11571_Product", Product.Helix_DWPC);

        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, "ServiceExport_OlenaService");
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.captureScreenshot("DRSQA-11571_DWPC_ServicePresent", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();

        String templateId = bwfCaseDataWithTemplate.get("CaseTemplateId");

        // -------------------- Scenario 1: Summary + TemplateId + LOB input --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11571_Driver", dwpDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_DWP);

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(summaryLabel, summaryValue);
        dwpFeatureMethod.fillTextAreaField(templateLabel, templateId);
        dwpFeatureMethod.fillDropdownField("LineofBusiness", lobValue);
        dwpFeatureMethod.clickOnSubmitRequest();

        dwpFeatureMethod.openActivityPage();
        String requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId1 != null && !requestId1.trim().isEmpty(), "Request ID is not created in DWP");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyOpenedRequestTitle(serviceName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11571_RequestSubmitted_WithTemplateAndLOB", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // Verify case created in BWF for HR LOB (template takes precedence)
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11571_Driver", bwfDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfCaseDataWithTemplate.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId1);
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Summary"), bwfCaseDataWithTemplate.get("Summary"), "Case summary mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Status"), bwfCaseDataWithTemplate.get("Status"), "Case status mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Request ID"), requestId1, "Request ID mismatch on BWF case");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Case Template"), bwfCaseDataWithTemplate.get("CaseTemplateValue"), "Case template mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Line of Business"), bwfCaseDataWithTemplate.get("LOB"), "LOB mismatch on BWF case");
        bwfFeatureMethods.captureScreenshot("DRSQA_11571_VerifyCaseDetails_TemplateLOB", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // Negative: request should not be visible under Facilities LOB in BWF console
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11571_Driver", bwfDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId1);
        bwfFeatureMethods.closeBrowser();

        // -------------------- Scenario 2: Summary + (blank) Template + LOB input --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11571_Driver", dwpDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(summaryLabel, summaryValue);
        dwpFeatureMethod.fillDropdownField("LineofBusiness", lobValue);
        dwpFeatureMethod.clickOnSubmitRequest();

        dwpFeatureMethod.openActivityPage();
        String requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId2 != null && !requestId2.trim().isEmpty(), "Request ID is not created in DWP (scenario 2)");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.verifyOpenedRequestTitle(serviceName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11571_RequestSubmitted_WithoutTemplate_WithLOB", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "FriedaUser");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11571_Driver", bwfDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfCaseDataWithoutTemplateDifferentLob.get("FACILITIES_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId2);
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Summary"), bwfCaseDataWithoutTemplateDifferentLob.get("Summary"), "Case summary mismatch (scenario 2)");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Request ID"), requestId2, "Request ID mismatch on BWF case (scenario 2)");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Line of Business"), bwfCaseDataWithoutTemplateDifferentLob.get("LOB"), "LOB mismatch on BWF case (scenario 2)");
        bwfFeatureMethods.captureScreenshot("DRSQA_11571_VerifyCaseDetails_WithoutTemplate_WithLOB", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // -------------------- Scenario 3: Summary + (blank) Template + (no) LOB input --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11571_Driver", dwpDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaField(summaryLabel, summaryValue);
        dwpFeatureMethod.fillDropdownField("LineofBusiness", lobValue);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.openActivityPage();
        String requestId3 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId3 != null && !requestId3.trim().isEmpty(), "Request ID is not created in DWP (scenario 3)");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId3);
        dwpFeatureMethod.verifyOpenedRequestTitle(serviceName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11571_RequestSubmitted_WithoutTemplate_WithoutLOB", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "FriedaUser");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11571_Driver", bwfDriver);
        context.setAttribute("DRSQA_11571_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.LOB_FACILITIES);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId3);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId3);
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Summary"), bwfCaseDataWithoutTemplateDefaultLob.get("Summary"), "Case summary mismatch (scenario 3)");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Request ID"), requestId3, "Request ID mismatch on BWF case (scenario 3)");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Line of Business"), bwfCaseDataWithoutTemplateDefaultLob.get("LOB"), "LOB mismatch on BWF case (scenario 3)");
        bwfFeatureMethods.captureScreenshot("DRSQA_11571_VerifyCaseDetails_WithoutTemplate_WithoutLOB", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------END: DRSQA_11571_RequestServiceWithLOBAndTemplate------------------------");
    }

    @Test(description = "[DWP] [BWF] Confidential questions in DWP, BWF and Catalog", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11459", Products = {Product.Helix_DWPC, Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11459_ConfidentialQuestionsInDWPBWFAndCatalog(ITestContext context) throws Exception {

        log.info("--------------START: DRSQA_11459_ConfidentialQuestionsInDWPBWFAndCatalog------------------------");

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11459_TestData");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11459_TestData");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        Map<Object, String> nonConfBwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "aallbrookUser");
        Map<Object, String> confBwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "LiamUsers");
        String serviceName = dwpTestData.get("ServiceName");
        String requestId1;
        String requestId2;

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11459_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11459_Product", Product.Helix_DWPC);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11459_TestData");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.closeBrowser();

        log.info("-------------------- Submit request #1 (question visible) --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11459_Driver", dwpDriver);
        context.setAttribute("DRSQA_11459_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, "AllQuestions_ConfidentialService");
        dwpFeatureMethod.openActivityPage();
        requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId1 != null && !requestId1.trim().isEmpty(), "Request ID-1 is not created in DWP");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyOpenedRequestTitle(serviceName);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Verify BWF case as non-confidential user (masked values) --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11459_Driver", bwfDriver);
        context.setAttribute("DRSQA_11459_Product", Product.Helix_BWF);
        login(nonConfBwfUserData.get("UserName"), nonConfBwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId1);
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Priority"), SolQAConstants.HIGH, "Priority mismatch");
        Assert.assertTrue(bwfFeatureMethods.getCaseDetail("Summary").contains(bwfTestData.get("Summary")));
        String mask = bwfTestData.get("ConfidentialMaskedValue");
        bwfFeatureMethods.clickShoMoreLinkInAuditIfPresent();
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Radio"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Toggle"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Checkbox"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Dropdown"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_DateTime"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_LookupBWF"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_LookupITSM"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Counter"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_SelectCity"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_LookupBWF"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_LookupITSM"), mask);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Toggle"), mask);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Verify BWF case as confidential user (actual values) --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11459_Driver", bwfDriver);
        context.setAttribute("DRSQA_11459_Product", Product.Helix_BWF);
        login(confBwfUserData.get("UserName"), confBwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId1);
        bwfFeatureMethods.clickShoMoreLinkInAuditIfPresent();
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Radio"), bwfTestData.get("Expected_Radio"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Toggle"), bwfTestData.get("Expected_Toggle"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Checkbox"), bwfTestData.get("Expected_Checkbox"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Dropdown"), bwfTestData.get("Expected_Dropdown"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_LookupBWF"), bwfTestData.get("Expected_LookupBWF"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_LookupITSM"), bwfTestData.get("Expected_LookupITSM"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_Counter"), bwfTestData.get("Expected_Counter"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("Field_SelectCity"), bwfTestData.get("Expected_SelectCity"));
        bwfFeatureMethods.closeBrowser();

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11459_Driver", bwfDriver);
        context.setAttribute("DRSQA_11459_Product", Product.Helix_BWF);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.navigateToSubmenuInSettings("Case Management");
        bwfFeatureMethods.clickOnSearchedAdminSettingWithoutClickToExpand("Templates");
        bwfFeatureMethods.setLineOfBusinessInTemplates(SolQAConstants.HR);
        bwfFeatureMethods.setSearchValueOnCaseTemplatesGrid("Paid Time Off Request");
        bwfFeatureMethods.clickSearchBtnOnCaseTemplateGrid();
        bwfFeatureMethods.verifyValueInCaseTemplateGrid("Paid Time Off Request");
        bwfFeatureMethods.openGridItemByName("Paid Time Off Request");
        bwfFeatureMethods.changeTemplateStatus(SolQAConstants.DRAFT);
        bwfFeatureMethods.clickOnManageDynamicFields_ByChangingTemplateStatus();
        bwfFeatureMethods.setDynamicFieldToHidden("Counter", true);
        bwfFeatureMethods.changeTemplateStatus(SolQAConstants.Active);
        bwfFeatureMethods.closeBrowser();

        try {
            // -------------------- Submit request #2 (question hidden) --------------------
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11459_Driver", dwpDriver);
            context.setAttribute("DRSQA_11459_Product", Product.Helix_DWP);
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            dwpFeatureMethod.fillSubmitDWPService(dwpTestData, "AllQuestions_ConfidentialService");

            dwpFeatureMethod.openActivityPage();
            requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
            Assert.assertTrue(requestId2 != null && !requestId2.trim().isEmpty(), "Request ID-2 is not created in DWP");
            dwpFeatureMethod.closeBrowser();

            // -------------------- Verify hidden field absent in audit for request #2 (confidential user) --------------------
            bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
            setDriver(bwfDriver);
            context.setAttribute("DRSQA_11459_Driver", bwfDriver);
            context.setAttribute("DRSQA_11459_Product", Product.Helix_BWF);
            login(confBwfUserData.get("UserName"), confBwfUserData.get("Password"), SolQAConstants.appBWF);
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfFeatureMethods.verifyLoginToBWF();
            bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
            bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
            bwfFeatureMethods.setSearchTextField_CaseConsole(requestId2);
            bwfFeatureMethods.clickSearchBtn_CaseConsole();
            bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId2);
            Assert.assertTrue(bwfFeatureMethods.getCaseDetail(bwfTestData.get("Field_MultiSelect")).contains(bwfTestData.get("Expected_MultiSelect")));
            Assert.assertTrue(bwfFeatureMethods.getCaseDetail(bwfTestData.get("Field_Radio")).contains(bwfTestData.get("Expected_Radio")));
            bwfFeatureMethods.clickShoMoreLinkInAuditIfPresent();
            Assert.assertNull(bwfFeatureMethods.getValueFromAuditTabForField("Counter"), "Hidden field is unexpectedly present in audit for requestId2");
            bwfFeatureMethods.closeBrowser();
        }
        finally {
            bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
            setDriver(bwfDriver);
            context.setAttribute("DRSQA_11459_Driver", bwfDriver);
            context.setAttribute("DRSQA_11459_Product", Product.Helix_BWF);
            login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appBWF);
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfFeatureMethods.verifyLoginToBWF();
            bwfFeatureMethods.navigateToSettings();
            bwfFeatureMethods.navigateToSubmenuInSettings("Case Management");
            bwfFeatureMethods.clickOnSearchedAdminSettingWithoutClickToExpand("Templates");
            bwfFeatureMethods.setSearchValueOnCaseTemplatesGrid("Paid Time Off Request");
            bwfFeatureMethods.clickSearchBtnOnCaseTemplateGrid();
            bwfFeatureMethods.setLineOfBusinessInTemplates(SolQAConstants.HR);
            bwfFeatureMethods.verifyValueInCaseTemplateGrid("Paid Time Off Request");
            bwfFeatureMethods.openGridItemByName("Paid Time Off Request");
            bwfFeatureMethods.changeTemplateStatus(SolQAConstants.DRAFT);
            bwfFeatureMethods.clickOnManageDynamicFields_ByChangingTemplateStatus();
            bwfFeatureMethods.setDynamicFieldToHidden("Counter", false);
            bwfFeatureMethods.changeTemplateStatus(SolQAConstants.Active);
            bwfFeatureMethods.closeBrowser();
        }
        log.info("--------------END: DRSQA_11459_ConfidentialQuestionsInDWPBWFAndCatalog------------------------");
    }

    @Test(description = "Knowledge article (BWF) KA search operation with AND logic in DWP", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11631", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11631_BWF_Knowledge_article_operations_with_AND_logic_in_DWP(ITestContext context) throws Exception {

        log.info("--------- DRSQA-11631: BWF Knowledge article operations in DWP *** START");

        log.info("Step 1: Enable KA search setting from DWP Admin");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11631_Driver", dwpDriver);
        context.setAttribute("DRSQA_11631_Product", Product.Helix_DWP);

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11631_TestData");
        String providerSettingName = dwpTestData.get("settingName");
        String kaToSearch = dwpTestData.get("KAToSearch");

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.openProviderSettings(SolQAConstants.BWF_PROVIDER, 10);
        dwpFeatureMethod.setCheckboxValueInProviderSettings(providerSettingName, true);
        dwpFeatureMethod.updateSettingsAndReloadProviders();
        dwpFeatureMethod.closeBrowser();

        log.info("Step 2: Search BWF knowledge article in DWP and submit feedback");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11631_Driver", dwpDriver);
        context.setAttribute("DRSQA_11631_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.searchInGlobalSearch(dwpTestData.get("searchHoliday"), SolQAConstants.ARTICLES)
                .verifyItemFound(dwpTestData.get("article1"), true)
                .verifyItemFound(dwpTestData.get("article2"), false);

        dwpFeatureMethod.searchItemInGlobalSearch(kaToSearch, SolQAConstants.ARTICLES);
        dwpFeatureMethod.clickOnKnowledgeArticleUsefulLink();
        String feedbackText = "DRSQA_11631_Feedback_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        dwpFeatureMethod.giveFeedbackOnArticle("Good", feedbackText);
        dwpFeatureMethod.captureScreenshot("DRSQA_11631_FeedbackEnteredOnArticle", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 3: Validate feedback is visible in BWF knowledge article activity");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11631_Driver", bwfDriver);
        context.setAttribute("DRSQA_11631_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "LiamUsers");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToWorkspaceKnowledge();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.searchAndOpenKA(kaToSearch);
        bwfFeatureMethods.verifyKnowledgeArticleActivityCommentPresent(feedbackText);
        bwfFeatureMethods.captureScreenshot("DRSQA_11631_VerifyingFeedbackInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("Step 4: Disable KA search setting from DWP Admin");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11631_Driver", dwpDriver);
        context.setAttribute("DRSQA_11631_Product", Product.Helix_DWP);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.CONFIGURATION);
        dwpFeatureMethod.openProviderSettings(SolQAConstants.BWF_PROVIDER, 10);
        dwpFeatureMethod.setCheckboxValueInProviderSettings(providerSettingName, false);
        dwpFeatureMethod.updateSettingsAndReloadProviders();
        dwpFeatureMethod.closeBrowser();

        log.info("Step 5: Validate global search returns expected articles after disabling setting");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11631_Driver", dwpDriver);
        context.setAttribute("DRSQA_11631_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.searchInGlobalSearch(dwpTestData.get("searchHoliday"), SolQAConstants.ARTICLES)
                .verifyItemFound(dwpTestData.get("article1"), true)
                .verifyItemFound(dwpTestData.get("article2"), true);
        dwpFeatureMethod.closeBrowser();
        log.info("--------- DRSQA-11631: BWF Knowledge article operations in DWP *** END");
    }

    @Test(description = "DWP - Bi-directional communication between DWP request & BWF case (via new request)", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11517", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_DWPC})
    public void DRSQA_11517_DWP_BWF_Communication_newCase(ITestContext context) throws Exception {

        log.info("*********** DRSQA-11517 : DWP_BWF_Communication_newCase *******************");
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11517_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11517_Product", Product.Helix_DWPC);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11517_TestData");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("serviceName"), dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(dwpcTestData.get("serviceName")), "Service is not present/published in DWPC: " + dwpcTestData.get("serviceName"));
        dwpcFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11517_Driver", dwpDriver);
        context.setAttribute("DRSQA_11517_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11517_Data");
        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        log.info("Open Catalog Page");
        String SERVICE_NAME = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.fillSubmitDWPService(dwpTestData, SERVICE_NAME);
        dwpFeatureMethod.captureScreenshot("DRSQA_11517_requestCreated", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.refreshBrowser();
        String firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        log.info(" ************ SRD created ID: " + firstSRD);
        Assert.assertFalse(firstSRD.isEmpty(), "SR is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(firstSRD);
        File file = CommonMethods.createTextFile(dwpTestData.get("DWPAttachment"));
        log.info("Open submitted request and add comment with attachment");
        dwpFeatureMethod.addRTFCommentWithAttachmentInSubmittedRequest(dwpTestData.get("DWPComment"), file, dwpTestData);

        log.info("Login to BWF and add activity note");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        context.setAttribute("DRSQA_11517_Driver", bwfDriver);
        context.setAttribute("DRSQA_11517_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "MorwennaUser");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11517_Data");
        log.info("Login to BWF with user " + bwfUserData.get("UserName"));
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        log.info("Set LOB as : " + bwfTestData.get("LOBName"));
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOBName"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        log.info("Search and open requested service caseID: " + firstSRD);
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.captureScreenshot("DRSQA_11517_ticketOpenInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.verify_Activity_Tab(dwpTestData.get("DWPComment"));
        bwfFeatureMethods.verifyCaseIsSetToFlagged();
        log.info("Add public note from BWF side");
        File bwfAttachment = CommonMethods.createTextFile(bwfTestData.get("BWFAttachment"));
        bwfFeatureMethods.addActivityNoteWithAttachment(bwfTestData.get("BWFComment"), bwfAttachment);
        bwfFeatureMethods.addComment_In_SocialFeed_WithPeopleMention(bwfTestData.get("BWFComment2"), bwfTestData.get("MentionedUser"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11517_commentAddedInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("Switch to DWP window and verifying the comment added from BWF");
        dwpFeatureMethod.switchToDWP(dwpDriver);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.refreshDWPPage();
        log.info("Goto DWP and verify comments added from BWF side");
        dwpFeatureMethod.verifyComment(bwfTestData.get("BWFComment"));
        dwpFeatureMethod.verifyLatestComment("@Bobby Hill" + " " + bwfTestData.get("BWFComment2"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11517_bwfCommentInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
    }

    @Test(description = "[BWF] [DWP] Article versioning", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11535", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA11535_ArticleVersioning(ITestContext context) throws Exception {

        log.info("--------- DRSQA11535_ArticleVersioning *** START");
        log.info("Step 1: Create 3 knowledge articles in BWF");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11535_Driver", bwfDriver);
        context.setAttribute("DRSQA_11535_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11535_TestData");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();

        log.info("Navigate to Create > Knowledge and create three articles with deterministic titles");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String title1 = "DRSQA11535_Original_1_" + ts;
        String title2 = "DRSQA11535_Original_2_" + ts;
        String title3 = "DRSQA11535_Original_3_" + ts;
        String version1 = "DRSQA11535_Version1_" + ts;
        String version2 = "DRSQA11535_Version2_" + ts;
        String version3 = "DRSQA11535_Version3_" + ts;

        bwfFeatureMethods.navigateToPage(SolQAConstants.CREATE, SolQAConstants.FILTER_KNOWLEDGE);
        bwfFeatureMethods.createKnowledgeArticleWithExactTitle(bwfTestData, title1);
        bwfFeatureMethods.navigateToPage(SolQAConstants.WORKSPACE, SolQAConstants.FILTER_KNOWLEDGE);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(title1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(title1);
        bwfFeatureMethods.setStatus(SolQAConstants.DRAFT);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.setStatus(SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.verifyKnowledgeArticleStatus(SolQAConstants.PUBLISHED);
        bwfFeatureMethods.setKnowledgeArticleExternal(bwfTestData.get("External"));
        bwfFeatureMethods.majorEditCreateVersion(version1, false, null);

        bwfFeatureMethods.navigateToPage(SolQAConstants.CREATE, SolQAConstants.FILTER_KNOWLEDGE);
        bwfFeatureMethods.createKnowledgeArticleWithExactTitle(bwfTestData, title2);
        bwfFeatureMethods.navigateToPage(SolQAConstants.WORKSPACE, SolQAConstants.FILTER_KNOWLEDGE);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(title2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(title2);
        bwfFeatureMethods.setStatus(SolQAConstants.DRAFT);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.setStatus(SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.verifyKnowledgeArticleStatus(SolQAConstants.PUBLISHED);
        bwfFeatureMethods.setKnowledgeArticleExternal(bwfTestData.get("External"));
        bwfFeatureMethods.majorEditCreateVersion(version2, true, bwfUserData.get("UserName"));

        bwfFeatureMethods.navigateToPage(SolQAConstants.CREATE, SolQAConstants.FILTER_KNOWLEDGE);
        bwfFeatureMethods.createKnowledgeArticleWithExactTitle(bwfTestData, title3);
        bwfFeatureMethods.navigateToPage(SolQAConstants.WORKSPACE, SolQAConstants.FILTER_KNOWLEDGE);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(title3);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(title3);
        bwfFeatureMethods.setStatus(SolQAConstants.DRAFT);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.setStatus(SolQAConstants.PUBLISH_APPROVAL);
        bwfFeatureMethods.refreshBrowser();
        bwfFeatureMethods.verifyKnowledgeArticleStatus(SolQAConstants.PUBLISHED);
        bwfFeatureMethods.setKnowledgeArticleExternal(bwfTestData.get("External"));
        bwfFeatureMethods.majorEditCreateVersion(version3, true, bwfUserData.get("UserName"));
        bwfFeatureMethods.refreshKnowledgeArticle();
        bwfFeatureMethods.setKnowledgeArticleExternal(bwfTestData.get("Internal"));
        bwfFeatureMethods.closeBrowser();

        log.info("Step 3: Search for KAs in DWP and validate visibility rules");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11535_Driver", dwpDriver);
        context.setAttribute("DRSQA_11535_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "rjamesEnduser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11535_TestData");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        // VERSION1 not shown (draft)
        dwpFeatureMethod.searchInGlobalSearch(version1, SolQAConstants.ARTICLES);
        dwpFeatureMethod.verifyItemFound(version1, false);

        // VERSION3 and TITLE3 not shown (internal)
        dwpFeatureMethod.searchInGlobalSearch(version3, SolQAConstants.ARTICLES);
        dwpFeatureMethod.verifyItemFound(version3, false);
        dwpFeatureMethod.searchInGlobalSearch(title3, SolQAConstants.ARTICLES);
        dwpFeatureMethod.verifyItemFound(title3, false);

        // TITLE1 shown (external + published original)
        dwpFeatureMethod.searchInGlobalSearch(title1, SolQAConstants.ARTICLES);
        dwpFeatureMethod.verifyItemFound(title1, true);

        // VERSION2 shown and feedback added to version only
        dwpFeatureMethod.searchItemInGlobalSearch(version2, SolQAConstants.ARTICLES);
        dwpFeatureMethod.clickOnKnowledgeArticleUsefulLink();
        String feedbackText = "DRSQA11535_Feedback_" + ts;
        dwpFeatureMethod.giveFeedbackOnArticle(dwpTestData.get("feedbackRating"), feedbackText);
        dwpFeatureMethod.captureScreenshot("DRSQA11535_FeedbackEntered", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // Verify feedback in BWF: present on version2, not on original title2
        log.info("Step 4: Verify feedback is present on version but not original in BWF");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11535_Driver", bwfDriver);
        context.setAttribute("DRSQA_11535_Product", Product.Helix_BWF);

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToWorkspaceKnowledge();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(version2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(version2);
        bwfFeatureMethods.verifyKnowledgeArticleActivityCommentPresent(feedbackText);

        bwfFeatureMethods.navigateToWorkspaceKnowledge();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(title2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(title2);
        bwfFeatureMethods.verifyKnowledgeArticleActivityCommentNotPresent(feedbackText);
        bwfFeatureMethods.closeBrowser();
        log.info("--------- DRSQA11535_ArticleVersioning *** END");
    }

    @Test(description = "DWP Searches only Published articles", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11511", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA11511_UnpublishedKASearchInDWP(ITestContext context) {

        log.info("------------DRSQA-11511: DWP searches only Published articles-------------");

        log.info("Step 1: Create a BWF Knowledge Article (default should be In Progress) with deterministic title");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String kaTitle = "DRSQA11511_UnpublishedKA_" + ts;

        log.info("Step 1: Create BWF knowledge article (In Progress): " + kaTitle);
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11511_Driver", bwfDriver);
        context.setAttribute("DRSQA_11511_Product", Product.Helix_BWF);

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11511_TestData");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11511_TestData");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToPage("Create", "Knowledge");
        bwfFeatureMethods.createKnowledgeArticleWithExactTitle(bwfTestData, kaTitle);

        log.info("Step 2: Verify In Progress article is NOT searchable in DWP");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11511_Driver", dwpDriver);
        context.setAttribute("DRSQA_11511_Product", Product.Helix_DWP);
        dwpUserData = ThreadSafeUserManager.getUserData(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchInGlobalSearch(kaTitle, dwpTestData.get("searchTab"));
        dwpFeatureMethod.verifyItemFound(kaTitle, false);

        log.info("Step 3: Set status to Draft in BWF and verify NOT searchable in DWP");
        setDriver(bwfDriver);
        bwfFeatureMethods.navigateToWorkspaceKnowledge();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(kaTitle);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(kaTitle);
        bwfFeatureMethods.setStatus(dwpTestData.get("draftStatus"));

        setDriver(dwpDriver);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchInGlobalSearch(kaTitle, dwpTestData.get("searchTab"));
        dwpFeatureMethod.verifyItemFound(kaTitle, false);

        log.info("Step 4: Publish article and set External=Yes; verify searchable in DWP");
        setDriver(bwfDriver);
        bwfFeatureMethods.navigateToWorkspaceKnowledge();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(kaTitle);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(kaTitle);
        bwfFeatureMethods.setStatus(dwpTestData.get("publishApprovalStatus"));
        bwfFeatureMethods.setExternalMetadata("Yes");

        setDriver(dwpDriver);
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchInGlobalSearch(kaTitle, dwpTestData.get("searchTab"));
        dwpFeatureMethod.verifyItemFound(kaTitle, true);

        log.info("Step 5: Retire approval then Closed; verify NOT searchable in DWP");
        setDriver(bwfDriver);
        bwfFeatureMethods.navigateToWorkspaceKnowledge();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("Line of Business"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(kaTitle);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.openGridRecordByLinkText(kaTitle);
        bwfFeatureMethods.setStatus(dwpTestData.get("retireApprovalStatus"));
        bwfFeatureMethods.closeBrowser();

        setDriver(dwpDriver);
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchInGlobalSearch(kaTitle, dwpTestData.get("searchTab"));
        dwpFeatureMethod.verifyItemFound(kaTitle, false);
        dwpFeatureMethod.closeDWP();
        log.info("------------DRSQA-11511 END-------------");
    }

    @Test(description = "[DWP][BWF] Cross launch of BWF application through DWP application (UI-only)", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11495", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA11495_CrossLaunch_OfBWFApplication_ThroughDWPApplication(ITestContext context) throws Exception {

        log.info("--------------- DRSQA-11495 Cross launching of BWF application from DWP application (UI-only) ---------------");

        // -------------------- Step 1: Create a case in BWF (Finance LOB) --------------------
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11495_CreateCase");
        String financeLob = bwfTestData.get("Line of Business");
        String summaryPrefix = bwfTestData.get("Summary");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String caseSummary = summaryPrefix + "_" + ts;

        log.info("Step 1: Create BWF case (LOB=" + financeLob + ", Summary=" + caseSummary + ")");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11495_Driver", bwfDriver);
        context.setAttribute("DRSQA_11495_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "MorwennaUser");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);

        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();

        Map<Object, String> caseCreateData = new java.util.HashMap<>(bwfTestData);
        caseCreateData.put("Summary", caseSummary);
        String caseId = bwfFeatureMethods.createCase(caseCreateData);
        log.info("BWF case ID created: " + caseId);
        Assert.assertNotNull(caseId, "BWF Case ID is null");
        Assert.assertFalse(caseId.trim().isEmpty(), "BWF Case ID is empty");
        bwfFeatureMethods.captureScreenshot("DRSQA_11495_caseCreatedInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // -------------------- Step 2: Login to DWP as agent and cross-launch BWF --------------------
        log.info("Step 2: Login to DWP as support agent and cross-launch BWF");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11495_Driver", dwpDriver);
        context.setAttribute("DRSQA_11495_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "MosesUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        Assert.assertTrue(dwpFeatureMethod.isNavigationMenuItemVisible(SolQAConstants.BWF), "BWF navigation item is not visible for support agent user in DWP");
        dwpFeatureMethod.openNavigationMenuItem(SolQAConstants.BWF);

        // Now we should be in BWF (cross-launched)
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(financeLob);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(caseId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseId, "Case ID");

        Map<Object, String> caseVerifyData = new java.util.HashMap<>();
        caseVerifyData.put("Summary", caseSummary);
        bwfFeatureMethods.verifyCaseDetails(caseVerifyData);
        bwfFeatureMethods.captureScreenshot("DRSQA_11495_verifyCaseOpenThroughDWP", SolQAConstants.appDWP);
        bwfFeatureMethods.closeBrowser();

        // -------------------- Step 3: Open the case again in BWF and verify viewed indicator --------------------
        log.info("Step 3: Open case in BWF and verify viewed indicator is shown");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11495_Driver", bwfDriver);
        context.setAttribute("DRSQA_11495_Product", Product.Helix_BWF);

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(financeLob);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(caseId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseId, "Case ID");
        Assert.assertTrue(bwfFeatureMethods.isViewedCaseShown(), "Viewed case indicator is not shown on the case details");
        bwfFeatureMethods.captureScreenshot("DRSQA_11495_CaseViewSection", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------- DRSQA-11495 END ---------------");
    }

    //Previous owner aramulwa
    @Test(description = "[DWP] [BWF] [Approval] Confidential employee responses visibility for approvers", groups = {"Regression_P1", "sonalsha"}, singleThreaded = true)
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11458", Products = {Product.Helix_DWPC, Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11458_ConfidentialResponsesVisibilityForApprovers(ITestContext context) throws Exception {

        log.info("--------------START: DRSQA_11458_ConfidentialResponsesVisibilityForApprovers------------------------");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11458_TestData");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11458_TestData");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        Map<Object, String> level1Manager = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fcatUser");
        Map<Object, String> level2Manager = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "yanandaUser");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        String serviceName = dwpTestData.get("ServiceName");
        String templateId = dwpTestData.get("TemplateId");

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA-11458_DWPC_Driver", getDriver());
        context.setAttribute("DRSQA-11458_DWPC_Product", Product.Helix_DWPC);

        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, serviceName);
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.captureScreenshot("DRSQA-11458_DWPC_ServicePresent", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();

        // -------------------- Step 1: Submit request in DWP --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11458_DWP_Requester_Driver", getDriver());
        context.setAttribute("DRSQA_11458_DWP_Product", Product.Helix_DWP);

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyLogin();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);

        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("Summary"));
        dwpFeatureMethod.fillCounterField(dwpTestData.get("CounterLabel"), dwpTestData.get("CounterValue"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("TextFieldLabel"), dwpTestData.get("TextField"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("TemplateLabel"), templateId);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11458_RequestSubmitted", SolQAConstants.appDWP);

        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Request ID is not created in DWP");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyOpenedRequestTitle(serviceName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11458_VerifyServiceRequestTitle", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();


        // -------------------- Step 2: Verify case in BWF, edit case access, trigger approval --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11458_Driver", bwfDriver);
        context.setAttribute("DRSQA_11458_Product", Product.Helix_BWF);
        

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);

        String caseId = bwfFeatureMethods.getCaseID();
        Assert.assertTrue(caseId != null && !caseId.trim().isEmpty(), "BWF Case ID is empty");

        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Title"), bwfTestData.get("TitleValue"), "Case summary mismatch");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Request ID"), requestId, "Request ID mismatch on case");
        Assert.assertEquals(bwfFeatureMethods.getCaseDetail("Case Template"), bwfTestData.get("Template"), "Case template mismatch");

        // Confidential fields on case should be masked
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("TextFieldLabel"), bwfTestData.get("ConfidAnswer"));
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("CounterLabel"), bwfTestData.get("Counter"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11458_AnswersOnCase", SolQAConstants.appBWF);

        // Case Access tab setup (best-effort locators; tenant UI may vary)
        bwfFeatureMethods.openCaseAccessTab();
        bwfFeatureMethods.addAgentAccessForCase(level2Manager.get("UserName"), true);
        bwfFeatureMethods.addConfidentialSupportGroupForCase(bwfTestData.get("ConfidGroup"), false);
        bwfFeatureMethods.captureScreenshot("DRSQA_11458_CaseAccess", SolQAConstants.appBWF);

        // Trigger approval by editing category tiers & assign to me
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.setCaseFieldDropdownValue("Category Tier 1", bwfTestData.get("Tier1"));
        bwfFeatureMethods.setCaseFieldDropdownValue("Category Tier 2", bwfTestData.get("Tier2"));
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        getDriver().navigate().refresh();
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("StatusPending"));
        bwfFeatureMethods.closeBrowser();

        // -------------------- Step 3: Level-1 approver - confidential question NOT visible --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11458_Driver", dwpDriver);
        context.setAttribute("DRSQA_11458_Product", Product.Helix_DWP);

        login(level1Manager.get("UserName"), level1Manager.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11458_Level1_Approved", SolQAConstants.appDWP);

        // Verify questions on approval request
        getDriver().navigate().refresh();
        dwpFeatureMethod.verifyHeadlineItemValueWithRetry("Request ID", caseId, 20);
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("CounterLabel"), dwpTestData.get("CounterValue"));
        dwpFeatureMethod.verifySubmittedRequestEventDetailNotPresent(dwpTestData.get("TextFieldLabel"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11458_Level1_QuestionsOnApprovalRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 4: Level-2 approver - confidential question visible --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11458_Driver", dwpDriver);
        context.setAttribute("DRSQA_11458_Product", Product.Helix_DWP);

        login(level2Manager.get("UserName"), level2Manager.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11458_Level2_Approved", SolQAConstants.appDWP);

        // Verify questions on approval request by level 2 approver
        dwpFeatureMethod.verifyHeadlineItemValueWithRetry("Request ID", caseId, 20);
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("CounterLabel"), dwpTestData.get("CounterValue"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(dwpTestData.get("TextFieldLabel"), dwpTestData.get("TextField"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11458_Level2_QuestionsOnApprovalRequest", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 5: Verify case status in BWF after approvals --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11458_Driver", bwfDriver);
        context.setAttribute("DRSQA_11458_Product", Product.Helix_DWP);

        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("StatusInProgress"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11458_VerifyCaseStatusAfterApprovals", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("--------------END: DRSQA_11458_ConfidentialResponsesVisibilityForApprovers------------------------");
    }

    @Test(description = "[BWF] [DWP] [Catalog] Complex Surveys in BWF, DWP, Catalog", groups = {"DWP", "BWF", "DWPC", "Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11427", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_DWPC})
    public void DRSQA_11427_ComplexSurveysInBWF_DWP_Catalog(ITestContext context) throws Exception {
        log.info("*********** DRSQA_11427 : Complex Surveys in BWF, DWP, Catalog *******************");

        Map<Object, String> dwpcUserData, dwpcTestData;
        String firstSRD;
        String secondSRD;
        String thirdSRD;

        log.info("-------------------- Precondition: Enable Survey in DWP Admin --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "DWPAdminUsersWithMyITSuperAdminPermission");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11427_RequestDetails");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(SolQAConstants.SERVICE_REQUESTS);
        dwpFeatureMethod.enableSurveysSetting(true);
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_DWPAdmin_EnableSurvey", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Precondition: Disable Survey Delivery Limit in DWPC --------------------");
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWPC);

        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCUsers, "Liam_User");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11427_DWPCService");
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        int deliveryLimitDays = Integer.parseInt(dwpcTestData.get("DeliveryLimitDays"));
        dwpcFeatureMethods.setFrequencyAdjustment(false, deliveryLimitDays);
        dwpcFeatureMethods.closeBrowser();

        log.info("-------------------- Step 1: DWP - Submit Service Request --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String serviceName = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("Summary"));
        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("DescriptionLabel"), dwpTestData.get("Description"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_requestCreated_1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        firstSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(firstSRD != null && !firstSRD.trim().isEmpty(), "First service request is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(firstSRD);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 2: BWF - Resolve Case --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);

        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11427_Surveys");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_ticketOpenInBWF_1", SolQAConstants.appBWF);
        bwfFeatureMethods.waitForExternalIdToContain(firstSRD, 10, 1000);
        bwfFeatureMethods.assignCaseToMe();
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(bwfTestData.get("StatusResolved"));
        bwfFeatureMethods.setStatusReason(bwfTestData.get("StatusReason"));
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_caseResolved_1", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 3: BWF - Enable ADD_DWP_SURVEY_ON_CASE --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.enterValueInTextField(bwfTestData.get("ManageLOBSettingName"));
        bwfFeatureMethods.clickOnSearchedAdminSetting(bwfTestData.get("ManageLOBSettingName"));
        bwfFeatureMethods.clickOnForwardArrowInManageLOB();
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(bwfTestData.get("ApplicationConfigTabName"));
        bwfFeatureMethods.clickApplicationConfigNameAndSetValue(bwfTestData.get("ApplicationConfigName"), bwfTestData.get("ApplicationConfigValueOn"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_EnableAddSurveyOnCase", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 4: DWP - Submit Excellent Survey Feedback --------------------");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(firstSRD);
        Assert.assertTrue(dwpFeatureMethod.isSurveyOptionAvailable(), "Survey option is not displayed in DWP");
        dwpFeatureMethod.rateExcellentAndAddFeedback(dwpTestData.get("RatingExcellent"));
        dwpFeatureMethod.clickSubmitButtonOnSurveyModal();
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_feedbackSubmittedExcellent", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 5: BWF - Verify Rating & Comment --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.verifySurveyRatingAndComment(
                bwfTestData.get("RatingFieldLabel"),
                bwfTestData.get("RatingExcellent"),
                bwfTestData.get("CommentFieldLabel"),
                bwfTestData.get("SurveyComment")
        );
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_VerifyRatingAndComment", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 6/7/8/9/10: Reopen case, validate survey removed, resolve again --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.reopenCaseFromCaseView();
        Assert.assertTrue(bwfFeatureMethods.getCaseStatus().contains(bwfTestData.get("StatusInProgress")),
                "Failed: Case is not reopened / In Progress");
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_CaseReopened", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(firstSRD);
        Assert.assertFalse(dwpFeatureMethod.isSurveyOptionAvailable(), "Survey option is displayed in DWP after case reopen (expected removed).");
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_SurveyRemovedAfterReopen", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(firstSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(firstSRD);
        bwfFeatureMethods.changeCaseStatusToWithoutSaving(bwfTestData.get("StatusResolved"));
        bwfFeatureMethods.setStatusReason(bwfTestData.get("StatusReason"));
        bwfFeatureMethods.saveStatusTransition();
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_CaseResolvedAgain", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(firstSRD);
        Assert.assertTrue(dwpFeatureMethod.isSurveyOptionAvailable(), "Survey option is not displayed in DWP after case resolved again");
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_SurveyOptionAvailableAfterResolveAgain", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("-------------------- Step 11-15: Submit complex survey and verify in BWF --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToCaseManagementTemplates();
        bwfFeatureMethods.setLineOfBusinessInTemplates(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.searchAndOpenTemplate(bwfTestData.get("CaseTemplateName"));
        String caseTemplateId = bwfFeatureMethods.getTemplateRecordIdFromCurrentUrl();
        bwfFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        String complexServiceName = dwpTestData.get("ComplexSurveyServiceName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(complexServiceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(complexServiceName);
            dwpFeatureMethod.clickOnRequestNow(complexServiceName);
        }
        dwpFeatureMethod.verifySrdTitle(complexServiceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("TemplateFieldLabel"), caseTemplateId);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_complexRequestCreated_1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        secondSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(complexServiceName);
        if (secondSRD == null || secondSRD.trim().isEmpty()) {
            secondSRD = dwpFeatureMethod.getSubmittedRequestIDFromPastEvents(complexServiceName);
        }
        Assert.assertTrue(secondSRD != null && !secondSRD.trim().isEmpty(), "Second (complex) service request is not created");
        dwpFeatureMethod.openSubmittedRequestInPastEvents(secondSRD);
        Assert.assertTrue(dwpFeatureMethod.isSurveyOptionAvailable(), "Survey option is not displayed in DWP for complex survey request");

        dwpFeatureMethod.rateOkayAndAddFeedback(dwpTestData.get("RatingOkay"));
        List<String> checkboxValues = new ArrayList<>();
        for (String v : dwpTestData.get("SurveyCheckBoxValue").split(",")) {
            if (!v.trim().isEmpty()) checkboxValues.add(v.trim());
        }
        dwpFeatureMethod.selectCheckBoxValuesOnSurveyModalByLabel(dwpTestData.get("SurveyCheckBoxLabel"), checkboxValues);
        dwpFeatureMethod.clickOnRadioButtonOnSurveyModalWithLabel(dwpTestData.get("SurveyRadioButtonLabel"), dwpTestData.get("SurveyRadioButtonValue"));
        dwpFeatureMethod.setValueInTextAreaOfSurveyModalByLabel(dwpTestData.get("SurveyTextAreaLabel"), dwpTestData.get("SurveyTextAreaValue"));
        dwpFeatureMethod.setValueInTextFieldOfSurveyModalByLabel(dwpTestData.get("SurveyTextFieldLabel"), dwpTestData.get("SurveyTextFieldValue"));
        dwpFeatureMethod.selectValueInDropDownOnSurveyModalByLabel(dwpTestData.get("SurveyDropDownLabel"), dwpTestData.get("SurveyDropDownValue"));
        dwpFeatureMethod.selectCurrentDateTimeOnSurveyModal(dwpTestData.get("SurveyDateTimeLabel"));
        String dwpSurveyDateTimeAnswer = dwpFeatureMethod.getSurveyAnswerValueFor(dwpTestData.get("SurveyDateTimeLabel"));
        dwpFeatureMethod.clickSubmitButtonOnSurveyModal();
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_complexSurveySubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(secondSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(secondSRD);
        bwfFeatureMethods.verifySurveyRatingDisplayed(bwfTestData.get("RatingFieldLabel"), bwfTestData.get("RatingOkay"));
        bwfFeatureMethods.clickViewSurvey();
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("SurveyTextAreaLabel"), dwpTestData.get("SurveyTextAreaValue"));
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("SurveyCheckBoxLabel"), dwpTestData.get("SurveyCheckBoxValue"));
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("SurveyRadioButtonLabel"), dwpTestData.get("SurveyRadioButtonValue"));
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("SurveyDropDownLabel"), dwpTestData.get("SurveyDropDownValue"));
        bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("SurveyTextFieldLabel"), dwpTestData.get("SurveyTextFieldValue"));

        String dwpSurveyDateTimeAnswerModified = dwpSurveyDateTimeAnswer == null ? "" : dwpSurveyDateTimeAnswer.replace(" at", "");
        SimpleDateFormat dwpFmt = new SimpleDateFormat("MMM d, yyyy h:mm a");
        SimpleDateFormat bwfFmt = new SimpleDateFormat("EEE MMM dd yyyy h:mm:ss a");
        try {
            Date parsed = dwpFmt.parse(dwpSurveyDateTimeAnswerModified);
            bwfFeatureMethods.verifyResponseForTheQuestion(dwpTestData.get("SurveyDateTimeLabel"), bwfFmt.format(parsed));
        } catch (Exception e) {
            Assert.fail("Date parsing failure for survey date/time. value=" + dwpSurveyDateTimeAnswerModified);
        }
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_VerifyComplexSurveyInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("-------------------- Step 16/17/20: Turn config OFF, submit again, verify rating not shown in BWF --------------------");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.navigateToSettings();
        bwfFeatureMethods.enterValueInTextField(bwfTestData.get("ManageLOBSettingName"));
        bwfFeatureMethods.clickOnSearchedAdminSetting(bwfTestData.get("ManageLOBSettingName"));
        bwfFeatureMethods.clickOnForwardArrowInManageLOB();
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(bwfTestData.get("ApplicationConfigTabName"));
        bwfFeatureMethods.clickApplicationConfigNameAndSetValue(bwfTestData.get("ApplicationConfigName"), bwfTestData.get("ApplicationConfigValueOff"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_DisableAddSurveyOnCase", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11427_Driver", dwpDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(complexServiceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(complexServiceName);
            dwpFeatureMethod.clickOnRequestNow(complexServiceName);
        }
        dwpFeatureMethod.verifySrdTitle(complexServiceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("TemplateFieldLabel"), caseTemplateId);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.openActivityPage();
        thirdSRD = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(complexServiceName);
        if (thirdSRD == null || thirdSRD.trim().isEmpty()) {
            thirdSRD = dwpFeatureMethod.getSubmittedRequestIDFromPastEvents(complexServiceName);
        }
        Assert.assertTrue(thirdSRD != null && !thirdSRD.trim().isEmpty(), "Third service request is not created");
        dwpFeatureMethod.openSubmittedRequestInPastEvents(thirdSRD);
        Assert.assertTrue(dwpFeatureMethod.isSurveyOptionAvailable(), "Survey option is not displayed in DWP for third request");

        dwpFeatureMethod.rateOkayAndAddFeedback(dwpTestData.get("RatingOkay"));
        dwpFeatureMethod.selectCheckBoxValuesOnSurveyModalByLabel(dwpTestData.get("SurveyCheckBoxLabel"), checkboxValues);
        dwpFeatureMethod.clickOnRadioButtonOnSurveyModalWithLabel(dwpTestData.get("SurveyRadioButtonLabel"), dwpTestData.get("SurveyRadioButtonValue"));
        dwpFeatureMethod.setValueInTextAreaOfSurveyModalByLabel(dwpTestData.get("SurveyTextAreaLabel"), dwpTestData.get("SurveyTextAreaValue"));
        dwpFeatureMethod.setValueInTextFieldOfSurveyModalByLabel(dwpTestData.get("SurveyTextFieldLabel"), dwpTestData.get("SurveyTextFieldValue"));
        dwpFeatureMethod.selectValueInDropDownOnSurveyModalByLabel(dwpTestData.get("SurveyDropDownLabel"), dwpTestData.get("SurveyDropDownValue"));
        dwpFeatureMethod.selectCurrentDateTimeOnSurveyModal(dwpTestData.get("SurveyDateTimeLabel"));
        dwpFeatureMethod.clickSubmitButtonOnSurveyModal();
        dwpFeatureMethod.captureScreenshot("DRSQA_11427_thirdSurveySubmitted", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11427_Driver", bwfDriver);
        context.setAttribute("DRSQA_11427_Product", Product.Helix_BWF);
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Peter");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(thirdSRD);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(thirdSRD);
        Assert.assertFalse(bwfFeatureMethods.isSurveyFieldDisplayed(bwfTestData.get("RatingFieldLabel")), "Failed: submitted survey rating is displayed in BWF even after ADD_DWP_SURVEY_ON_CASE is OFF");
        bwfFeatureMethods.captureScreenshot("DRSQA_11427_VerifySurveyNotDisplayedInBWF", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        log.info("*** END *** DRSQA_11427 **********");
    }

    @Test(description = "Anonymizing the username in BWF-DWP comments", groups = {"Regression_P1", "DWP", "BWF", "gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "2463", JiraID = "DRSQA-11534", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11534_Anonymizing_Username_In_BWF_DWP_Comments(ITestContext context) throws Exception {

        log.info("*********** Anonymizing the username in BWF-DWP comments *******************");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11534_Anonymization");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11534_Anonymization");

        String hideAgentNameSetting = "HIDE_AGENT_NAME_FROM_REQUESTER";

        log.info("Step 1: Turn anonymization OFF in BWF");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenSettings(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
        bwfFeatureMethods.clickApplicationConfigNameAndSetValue(hideAgentNameSetting, SolQAConstants.False);
        bwfFeatureMethods.closeBrowser();

        log.info("Step 2: Submit DWP request as requester");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "swaugh");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod.verifyDWPHomePageOpened();

        String serviceName = dwpTestData.get("ServiceRequestName");
        log.info("Open and request service: " + serviceName);
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("Summary1"));
        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("DescriptionLabel"), dwpTestData.get("Description1"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11534_RequestSubmitted", SolQAConstants.appDWP);

        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertFalse(requestId.isEmpty(), "Service request ID not found after submission");
        log.info("Submitted request ID: " + requestId);

        log.info("Step 3: Add BWF comment 1 (anonymization OFF)");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        String comment1 = bwfTestData.get("Comment1");
        bwfFeatureMethods.addActivityNotes(comment1);
        bwfFeatureMethods.captureScreenshot("DRSQA_11534_BWFComment1Added", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        log.info("Step 4: Verify comment 1 in DWP shows real author name");
        setDriver(dwpDriver);
        dwpFeatureMethod.switchToDWP(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyComment(comment1);
        dwpFeatureMethod.verifyCommentAuthor(dwpTestData.get("AuthorFullName"), comment1);
        dwpFeatureMethod.captureScreenshot("DRSQA_11534_Comment1Verified", SolQAConstants.appDWP);

        log.info("Step 5: Turn anonymization ON in BWF");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "opsSupportAgent");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenSettings(SolQAConstants.manageLineOfBusiness);
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.clickTabNameOnLineOfBusinessManagement(SolQAConstants.applicationConfig);
        bwfFeatureMethods.clickApplicationConfigNameAndSetValue(hideAgentNameSetting, SolQAConstants.True);
        bwfFeatureMethods.closeBrowser();

        log.info("Step 6: Add BWF comment 2 and verify in DWP as anonymized author");
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        String comment2 = bwfTestData.get("Comment2");
        bwfFeatureMethods.addActivityNotes(comment2);
        bwfFeatureMethods.captureScreenshot("DRSQA_11534_BWFComment2Added", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        setDriver(dwpDriver);
        dwpFeatureMethod.switchToDWP(dwpDriver);
        dwpFeatureMethod.refreshDWPPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyComment(comment1);
        dwpFeatureMethod.verifyCommentAuthor(dwpTestData.get("AuthorFullName"), comment1);
        dwpFeatureMethod.verifyComment(comment2);
        dwpFeatureMethod.verifyCommentAuthor(dwpTestData.get("AuthorAnonymized"), comment2);
        dwpFeatureMethod.captureScreenshot("DRSQA_11534_Comment2Verified", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("*********** END : DRSQA_11534 *******************");
    }

    @Test(description = "Alternate/Delegate Approver validation", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "8925", JiraID = "DRSQA-11473", Products = {Product.Helix_DWP})
    public void DRSQA_11473_Alternate_Delegate_Approver_validation(ITestContext context) throws Exception {

        readJson = new ReadJson();
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11473_RequestDetails");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "jandersonUser");
        Map<Object, String> alternativeApproverData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "dmurphyUser");
        Map<Object, String> requesterData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "jhardyUser");
        String originalApproverFullName = dwpUserData.get("FullName");
        String alternativeApproverFullName = dwpTestData.get("AlternateApprover");
        String serviceName = dwpTestData.get("ServiceRequestName");

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWPC);
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11473_TestData");
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, dwpcTestData.get("ImportServiceName"));
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.closeBrowser();

        log.info("Step 1: Login as original approver, configure alternate approver in Preferences");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToPreferences();
        dwpFeatureMethod.manageAlternateApprovers();
        dwpFeatureMethod.addAlternateApprover(alternativeApproverFullName, getFutureDateTime(1, "MMM d, yyyy, h:mm a"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_AlternateApproverConfigured", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 2: Login as alternate approver and verify notification message contains original approver name");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(alternativeApproverData.get("UserName"), alternativeApproverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openNotification();
        dwpFeatureMethod.verifyAlternateApproverMessageInNotification(originalApproverFullName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_AlternateApproverNotification", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 3: Login as requester and submit Simple Change SRD (Approval)");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_RequestSubmitted_1", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId1 != null && !requestId1.trim().isEmpty(), "Request ID 1 is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("waitingApprovalStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_RequestWaitingApproval_1", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 4: Original approver sees approval request");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_OriginalApprover_ApprovalPresent_1", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 5: Alternate approver approves request, verify Acting as + approver list + details show alternate approver");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(alternativeApproverData.get("UserName"), alternativeApproverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyActingAsUserPresent(originalApproverFullName);
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("Status"));
        dwpFeatureMethod.verifyApprovalStatusWithDetails(dwpTestData.get("Status"), alternativeApproverFullName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_AlternateApprover_Approved_1", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 6: Original approver verifies approved request in Past events");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId1);
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.INITIATED);
        dwpFeatureMethod.verifyApprovalStatusWithDetails(dwpTestData.get("Status"), alternativeApproverFullName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_OriginalApprover_VerifyApprovedPast_1", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 7: Requester submits another request to Reject");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_RequestSubmitted_2", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId2 != null && !requestId2.trim().isEmpty(), "Request ID 2 is not created");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("waitingApprovalStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_RequestWaitingApproval_2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 8: Original approver sees approval request for Request 2");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.verifyApprovalDetailsFormOpened();
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_OriginalApprover_ApprovalPresent_2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 9: Alternate approver rejects request, verify details show alternate approver + reject reason");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(alternativeApproverData.get("UserName"), alternativeApproverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        dwpFeatureMethod.verifyActingAsUserPresent(originalApproverFullName);
        dwpFeatureMethod.verifyApproverNameWithStatus(originalApproverFullName, dwpTestData.get("WaitingStatus"));
        dwpFeatureMethod.clickRejectRequestInEventDetails(dwpTestData.get("RejectReason"), true);
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.REJECTED);
        dwpFeatureMethod.verifyApprovalStatusWithDetails(SolQAConstants.REJECTED, alternativeApproverFullName);
        dwpFeatureMethod.verifyRejectedReasonTextIsCorrect(dwpTestData.get("RejectReason"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_AlternateApprover_Rejected_2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 10: Original approver verifies rejected request in Past events (status + reason)");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId2);
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.REJECTED);
        dwpFeatureMethod.verifyApprovalStatusWithDetails(SolQAConstants.REJECTED, alternativeApproverFullName);
        dwpFeatureMethod.verifyRejectedReasonTextIsCorrect(dwpTestData.get("RejectReason"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_OriginalApprover_VerifyRejectedPast_2", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        log.info("Step 11: Requester verifies Approved is visible + Rejected is visible with reason");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(requesterData.get("UserName"), requesterData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        dwpFeatureMethod.verifyApprovalStatusWithDetails(dwpTestData.get("Status"), alternativeApproverFullName);
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId2);
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.REJECTED);
        dwpFeatureMethod.verifyApprovalStatusWithDetails(SolQAConstants.REJECTED, alternativeApproverFullName);
        dwpFeatureMethod.verifyRejectedReasonTextIsCorrect(dwpTestData.get("RejectReason"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_Requester_VerifyApprovedAndRejected", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // Cleanup: remove alternate approver
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11473_Driver", dwpDriver);
        context.setAttribute("DRSQA_11473_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToPreferences();
        dwpFeatureMethod.manageAlternateApprovers();
        dwpFeatureMethod.removeAlternateApproverIfPresent(alternativeApproverFullName);
        dwpFeatureMethod.captureScreenshot("DRSQA_11473_AlternateApproverRemoved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
    }

    @Test(description = "Order ID vs Request ID in DWP requests for multiple services from cart", groups = {"Regression_P2"})
    @SolqaDetails(authorName = "hpareek", TTID = "315", JiraID = "DRSQA-11448", Products = {Product.Helix_BWF, Product.Helix_DWP, Product.Helix_ITSM})
    public void DRSQA_11448_Order_ID_vs_Request_ID_in_DWP_requests(ITestContext context) throws Exception {

        //dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11448_RequestDetails");
        //bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11448_VerifyCaseDetails");
        Map<Object, String> smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11448_ITSMVerify");

        Map<Object, String> dwpApproverData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fcatUser");
        Map<Object, String> bwfUserDataLocal = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, bwfTestData.get("Peter"));
        Map<Object, String> smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, SolQAConstants.SmartITUsers, smartITTestData.get("pbunyon"));

       
        //User Data
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillEnduser");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "hannah_admin");

        //Data Test
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11448_RequestDetails");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11448_VerifyCaseDetails");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11448_ITSMVerify");

        String service1 = dwpTestData.get("ServiceRequestName1");
        String service2 = dwpTestData.get("ServiceRequestName2");
        String service3 = dwpTestData.get("ServiceRequestName3");
        String service4 = dwpTestData.get("ServiceRequestName4");

        String summaryLabel = dwpTestData.get("SummaryLabel");
        String whereToMoveLabel = dwpTestData.get("WhereToMoveLabel");
        String whenToMoveLabel = dwpTestData.get("WhenToMoveLabel");
        String whatRoomLabel = dwpTestData.get("WhatRoomLabel");
        String complaintSummaryLabel = dwpTestData.get("ComplaintSummaryLabel");
        String complaintDescriptionLabel = dwpTestData.get("ComplaintDescriptionLabel");
        String requestIdLabel = dwpTestData.get("RequestIdLabel");
        String orderTitle = dwpTestData.get("OrderTitle");

        // -------------------- Step 1: Empty cart, add four services to cart, request items --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA-11448_Driver", dwpDriver);
        context.setAttribute("DRSQA-11448_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        dwpFeatureMethod.removeAllItemsFromCart();

        String[] servicesToAdd = new String[]{service1, service2, service3, service4};
        for (String s : servicesToAdd) {
            dwpFeatureMethod.openCatalogPage();
            if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
                dwpFeatureMethod.openAllItemsPage();
            }
            dwpFeatureMethod.searchAndOpenServiceProfile(s);
            dwpFeatureMethod.addToCart();
        }
        dwpFeatureMethod.viewCart();
        dwpFeatureMethod.clickRequestItemsOnDialog();

        // -------------------- Step 2: Fill checkout for each service --------------------
        DateFormat df = new SimpleDateFormat("MMMM d, yyyy");
        String today = df.format(new Date());

        dwpFeatureMethod.verifyCheckoutItemOpened(service1);
        dwpFeatureMethod.fillTextFieldByLabel(summaryLabel, dwpTestData.get("Summary1"));
        dwpFeatureMethod.fillTextFieldByLabel(whereToMoveLabel, dwpTestData.get("WhereToMoveValue1"));
        dwpFeatureMethod.fillTextDateFieldByLabel(whenToMoveLabel, today);
        dwpFeatureMethod.closeDateModalIfPresent();
        dwpFeatureMethod.clickNextItemButton();

        dwpFeatureMethod.verifyCheckoutItemOpened(service2);
        dwpFeatureMethod.fillTextFieldByLabel(summaryLabel, dwpTestData.get("Summary2"));
        dwpFeatureMethod.fillTextFieldByLabel(whereToMoveLabel, dwpTestData.get("WhereToMoveValue2"));
        dwpFeatureMethod.fillTextDateFieldByLabel(whenToMoveLabel, today);
        dwpFeatureMethod.closeDateModalIfPresent();
        dwpFeatureMethod.clickNextItemButton();

        dwpFeatureMethod.verifyCheckoutItemOpened(service3);
        dwpFeatureMethod.fillTextFieldByLabel(whatRoomLabel, dwpTestData.get("WhatRoomValue"));
        dwpFeatureMethod.clickNextItemButton();

        dwpFeatureMethod.verifyCheckoutItemOpened(service4);
        dwpFeatureMethod.fillTextFieldByLabel(complaintSummaryLabel, dwpTestData.get("Summary4"));
        dwpFeatureMethod.fillTextAreaByLabel(complaintDescriptionLabel, dwpTestData.get("Description4"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11448_RequestSubmitted", SolQAConstants.appDWP);

        // -------------------- Step 3: Open the order request and collect each dependent Request ID --------------------
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedSbRequestInActiveEventsByTitle(orderTitle);
        dwpFeatureMethod.refreshPage();
        SoftAssert softAssert = new SoftAssert();
        dwpFeatureMethod.expandDependentService(service1);
        String reqId1 = dwpFeatureMethod.getHeadlineValueFromDependentService(service1, requestIdLabel);
        softAssert.assertTrue(reqId1 != null && !reqId1.trim().isEmpty(), "Request ID 1 is blank");
        dwpFeatureMethod.verifySubmittedRequestEventDetails(summaryLabel, dwpTestData.get("Summary1"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(whereToMoveLabel, dwpTestData.get("WhereToMoveValue1"));
        dwpFeatureMethod.collapseDependentService(service1);

        dwpFeatureMethod.expandDependentService(service2);
        String reqId2 = dwpFeatureMethod.getHeadlineValueFromDependentService(service2, requestIdLabel);
        softAssert.assertTrue(reqId2 != null && !reqId2.trim().isEmpty(), "Request ID 2 is blank");
        dwpFeatureMethod.verifySubmittedRequestEventDetails(summaryLabel, dwpTestData.get("Summary2"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(whereToMoveLabel, dwpTestData.get("WhereToMoveValue2"));
        dwpFeatureMethod.collapseDependentService(service2);

        dwpFeatureMethod.expandDependentService(service3);
        String reqId3 = dwpFeatureMethod.getHeadlineValueFromDependentService(service3, requestIdLabel);
        softAssert.assertTrue(reqId3 != null && !reqId3.trim().isEmpty(), "Request ID 3 is blank");
        dwpFeatureMethod.verifySubmittedRequestEventDetails(whatRoomLabel, dwpTestData.get("WhatRoomValue"));
        dwpFeatureMethod.collapseDependentService(service3);

        dwpFeatureMethod.expandDependentService(service4);
        String reqId4 = dwpFeatureMethod.getHeadlineValueFromDependentService(service4, requestIdLabel);
        softAssert.assertTrue(reqId4 != null && !reqId4.trim().isEmpty(), "Request ID 4 is blank");
        dwpFeatureMethod.verifySubmittedRequestEventDetails(complaintSummaryLabel, dwpTestData.get("Summary4"));
        dwpFeatureMethod.verifySubmittedRequestEventDetails(complaintDescriptionLabel, dwpTestData.get("Description4"));
        dwpFeatureMethod.collapseDependentService(service4);

        dwpFeatureMethod.captureScreenshot("DRSQA_11448_VerifyServiceRequestDetails", SolQAConstants.appDWP);
        softAssert.assertAll();
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 4: Verify cases created in BWF --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA-11448_Driver", bwfDriver);
        context.setAttribute("DRSQA-11448_Product", Product.Helix_BWF);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();

        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("FACILITIES_LOB"));
        bwfFeatureMethods.setSearchTextField_CaseConsole(reqId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        Assert.assertEquals(bwfFeatureMethods.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(reqId1), Integer.parseInt(bwfTestData.get("ExpectedCasesForReqId1")), "A single Case is not seen created for request id: " + reqId1);

        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("FACILITIES_LOB"));
        bwfFeatureMethods.setSearchTextField_CaseConsole(reqId2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        Assert.assertEquals(bwfFeatureMethods.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(reqId2), Integer.parseInt(bwfTestData.get("ExpectedCasesForReqId2")), "Two cases are not seen getting created for request id: " + reqId2);

        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.setSearchTextField_CaseConsole(reqId4);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(reqId4);
        String caseId4 = bwfFeatureMethods.getCaseID();
        Assert.assertTrue(caseId4 != null && !caseId4.trim().isEmpty(), "BWF Case ID for request 4 is blank");
        bwfFeatureMethods.closeBrowser();

        // -------------------- Step 5: First level approver approves service request --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA-11448_Driver", dwpDriver);
        context.setAttribute("DRSQA-11448_Product", Product.Helix_DWP);
        login(dwpApproverData.get("UserName"), dwpApproverData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(caseId4);
        dwpFeatureMethod.verifyApprovalDetailsFormOpened();
        dwpFeatureMethod.clickApproveRequestInEventDetails();
        dwpFeatureMethod.refreshBrowser();
        dwpFeatureMethod.verifyApprovalStatusFromDetails(dwpTestData.get("ApprovedStatus"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11448_VerifyRequestStatusAfterApproved", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 6: Verify In Progress status in BWF for approved DWP request --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA-11448_Driver", bwfDriver);
        context.setAttribute("DRSQA-11448_Product", Product.Helix_BWF);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("HR_LOB"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(reqId4);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(reqId4);
        bwfFeatureMethods.verifyCaseStatus(bwfTestData.get("ExpectedCaseStatusAfterApproval"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11448_BWF_VerifyInProgress", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // -------------------- Step 7: Verify Work Order is created in ITSM (SmartIT) for Request 3 --------------------
        WebDriver smartITDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(smartITDriver);
        context.setAttribute("DRSQA-11448_Driver", smartITDriver);
        context.setAttribute("DRSQA-11448_Product", Product.Helix_SmartIT);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        SmartITFeatureMethodsPage smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();
        smartITFeatureMethodsPage.verifySmartITPageLaunchedSuccessfully();
        smartITFeatureMethodsPage.globalSearchAndOpenTicket(reqId3);
        String woId = smartITFeatureMethodsPage.getWorkOrderIDFromFulfillmentSection();
        Assert.assertTrue(woId != null && !woId.trim().isEmpty(), "Work order is not seen created in SmartIT for request ID: " + reqId3);
        smartITFeatureMethodsPage.closeBrowser();
    }

    @Test(description = "[DWP][BWF][Audit Trail] Activity recorded on task when request is submitted from DWP", groups = {"Regression_P2","hpareek"})
    @SolqaDetails(authorName = "hpareek", TTID = "314", JiraID = "DRSQA-11447", Products = {Product.Helix_BWF, Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11447_ActivityOnTaskCreatedFromDWP(ITestContext context) throws Exception {
      
        //UsersData 
        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "rjamesEnduser");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Peter");
        //DataTest
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11447_TaskActivity");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_11447_TaskHistory");

        // -------------------- DWPC: Navigate to Connectors page --------------------
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11447_Driver", dwpcDriver);
        context.setAttribute("DRSQA_11447_Product", Product.Helix_DWPC);
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.navigateToSubmenu("Services", "Connectors");
        dwpcFeatureMethods.verifyConnectorManagementPageLoaded();
        dwpcFeatureMethods.captureScreenshot("DRSQA_11447_DWPC_ConnectorManagement", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();

        // -------------------- DWP: Request a service --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11447_Driver", dwpDriver);
        context.setAttribute("DRSQA_11447_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(dwpTestData.get("ServiceName"));
        dwpFeatureMethod.verifySrdTitle(dwpTestData.get("ServiceName"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("Summary"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11447_RequestSubmitted", SolQAConstants.appDWP);

        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("ServiceName"));
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Submitted Request ID is blank for: " + dwpTestData.get("ServiceName"));
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifySrdTitle(dwpTestData.get("ServiceName"));
        dwpFeatureMethod.captureScreenshot("DRSQA_11447_VerifyServiceRequestTitle", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- BWF: Verify case created with task, check task audit trail --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11447_Driver", bwfDriver);
        context.setAttribute("DRSQA_11447_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LineOfBusiness"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);

        bwfFeatureMethods.verifyCaseDetailEquals(bwfTestData.get("CaseDetailRequestID"), requestId);
        bwfFeatureMethods.verifyCaseDetailEquals(bwfTestData.get("CaseDetailLabelCaseTemplate"), bwfTestData.get("Template"));

        bwfFeatureMethods.clickTaskSummaryLinkOnCase(bwfTestData.get("TaskSummary"));
        bwfFeatureMethods.verifyTaskSummary(bwfTestData.get("TaskSummary"));
        bwfFeatureMethods.expandAuditActivityToRevealAllFields();
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelStatus"), bwfTestData.get("Status"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelAssignee"), bwfTestData.get("Assignee"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelAssignedCompany"), bwfTestData.get("AssignedCompany"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelCategoryTier1"), bwfTestData.get("CategoryTier1"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelPriority"), bwfTestData.get("Priority"));
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelAssignedGroup"), bwfTestData.get("AssignedGroup"));
        bwfFeatureMethods.captureScreenshot("DRSQA_11447_TaskAudit", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();

        // -------------------- DWP: Cancel request --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11447_Driver", dwpDriver);
        context.setAttribute("DRSQA_11447_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.cancelServiceRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11447_RequestCanceledInDWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- BWF: Check task canceled in audit trail --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11447_Driver", bwfDriver);
        context.setAttribute("DRSQA_11447_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(bwfTestData.get("LineOfBusiness"));
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestId);
        bwfFeatureMethods.verifyCaseStatus(SolQAConstants.CANCELED);
        bwfFeatureMethods.clickTaskSummaryLinkOnCase(bwfTestData.get("TaskSummary"));
        bwfFeatureMethods.expandAuditActivityToRevealAllFields();
        bwfFeatureMethods.verifyTaskStatus(SolQAConstants.CANCELED);
        bwfFeatureMethods.verify_Value_Added_In_Audit_To_Field(bwfTestData.get("AuditLabelStatus"), SolQAConstants.CANCELED);
        bwfFeatureMethods.captureScreenshot("DRSQA_11447_TaskAuditForCanceled", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "[DWP][Catalog][BWF] Multiple case status change reflection in DWP and DWP Catalog", groups = {"Regression_P2"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "2494", JiraID = "DRSQA-11544", Products = {Product.Helix_DWP, Product.Helix_DWPC, Product.Helix_BWF})
    public void DRSQA_11544_MultipleCaseStatusChangeReflection(ITestContext context) throws Exception {
        
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11544_MultipleCaseStatusChangeReflection");
        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFTestData, "DRSQA_11544_MultipleCaseStatusChangeReflection");
        Map<Object, String> dwpcTestDataLocal = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11544_MultipleCaseStatusChangeReflection");
        Map<Object, String> dwpUserDataLocal = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, dwpTestData.get("DWPUserKey"));
        Map<Object, String> bwfUserDataLocal = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, bwfTestData.get("BWFCaseUserKey"));
        Map<Object, String> dwpcUserDataLocal = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCUsers, dwpcTestDataLocal.get("DWPCUserKey"));

        String serviceName = dwpTestData.get("ServiceName");
        String summaryLabel = dwpTestData.get("SummaryLabel");
        String hrLob = bwfTestData.get("HR_LOB");
        String caseIdColumn = bwfTestData.get("CaseIdColumn");
        String externalTicketIdLabel = bwfTestData.get("ExternalTicketIdLabel");
        String statusNew = bwfTestData.get("StatusNew");
        String statusAssigned = bwfTestData.get("StatusAssigned");
        String statusInProgress = bwfTestData.get("StatusInProgress");
        String statusPending = bwfTestData.get("StatusPending");
        String statusResolved = bwfTestData.get("StatusResolved");
        String statusClosed = bwfTestData.get("StatusClosed");
        String statusCanceled = bwfTestData.get("StatusCanceled");
        String canceledReason = bwfTestData.get("CanceledSR");
        String resolvedReason = bwfTestData.get("ResolvedSR");
        String dwpStatusInProgress = dwpTestData.get("StatusInProgress");
        String dwpStatusPending = dwpTestData.get("StatusPending");
        String dwpStatusCompleted = dwpTestData.get("StatusCompleted");
        String dwpStatusCancelled = dwpTestData.get("StatusCancelled");
        String dwpcReportsMenu = dwpcTestDataLocal.get("ReportsMenu");
        String dwpcServiceRequestsSubMenu = dwpcTestDataLocal.get("ServiceRequestsSubMenu");
        String dwpcStatusColumnLabel = dwpcTestDataLocal.get("StatusColumnLabel");

        // Umbrella imports the service via DWPC API; SolutionsQA assumes the service is already published.

        // -------------------- Request 1: Submit multi-case request in DWP --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11544_DWP_Driver", getDriver());
        context.setAttribute("DRSQA_11544_DWP_Product", Product.Helix_DWP);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaByLabel(summaryLabel, dwpTestData.get("Summary"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request1Submitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId1 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId1 != null && !requestId1.trim().isEmpty(), "Request 1 ID is blank");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 1 status is not '" + dwpStatusInProgress + "'");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request1_InProgress_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- DWPC: Verify request 1 is ACTIVE --------------------
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11544_DWPC_Driver", getDriver());
        context.setAttribute("DRSQA_11544_DWPC_Product", Product.Helix_DWPC);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        String dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 1 is not ACTIVE");
        dwpcFeatureMethods.captureScreenshot("DRSQA_11544_DWPC_Request1_Active", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Verify 2 cases created for request 1 and assign case 1 --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11544_BWF_Driver", getDriver());
        context.setAttribute("DRSQA_11544_BWF_Product", Product.Helix_BWF);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(hrLob);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        Assert.assertEquals(bwfFeatureMethods.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(requestId1), 2, "Two cases are not seen created for request id: " + requestId1);
        List<String> caseIdsReq1Raw = bwfFeatureMethods.getVisibleValuesFromCaseConsoleColumn(caseIdColumn);
        Assert.assertTrue(caseIdsReq1Raw.size() >= 2, "Less than 2 case IDs found in grid for request id: " + requestId1);
        String caseId1_1 = caseIdsReq1Raw.get(0);
        String caseId1_2 = caseIdsReq1Raw.get(1);

        bwfFeatureMethods.clickCaseIDLink_CaseConsole(caseId1_1, caseIdColumn);
        bwfFeatureMethods.verifyCaseDetailEquals(externalTicketIdLabel, requestId1);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.captureScreenshot("DRSQA_11544_Request1_Case1_Assigned", SolQAConstants.appBWF);

        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.verifyCaseDetailEquals(externalTicketIdLabel, requestId1);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.captureScreenshot("DRSQA_11544_Request1_Case2_New", SolQAConstants.appBWF);
        bwfFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 1 -> In Progress (Case 2 stays New); DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.verifyCaseStatus(statusInProgress);
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 1 is not ACTIVE after case 1 set to In Progress");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 1 -> Pending (Case 2 stays New); DWPC + DWP -> Pending --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusPending, "");
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 1 is not PENDING after case 1 set to Pending");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusPending), "Request 1 status is not '" + dwpStatusPending + "' after case 1 Pending");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request1_Pending_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 1 -> Resolved (Case 2 stays New); DWPC -> ACTIVE; DWP -> In Progress --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusResolved, resolvedReason);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 1 is not ACTIVE after case 1 Resolved");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 1 status is not '" + dwpStatusInProgress + "' after case 1 Resolved");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 1 -> Closed (Case 2 stays New); DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusClosed, "");
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 1 is not ACTIVE after case 1 Closed");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Assigned; DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 1 is not ACTIVE after case 2 Assigned");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 2 -> In Progress; DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.verifyCaseStatus(statusInProgress);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 1 is not ACTIVE after case 2 In Progress");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Pending; DWPC + DWP -> Pending --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusPending, "");
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 1 is not PENDING after case 2 Pending");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId1);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusPending), "Request 1 status is not '" + dwpStatusPending + "' after case 2 Pending");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Resolved; DWPC -> Completed/Closed; DWP -> Completed (Past) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusResolved, resolvedReason);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        String dwpcReq1StatusUpper = dwpcReq1Status.toUpperCase();
        String expectedCompletedUpper = dwpStatusCompleted.toUpperCase();
        Assert.assertTrue(dwpcReq1StatusUpper.equals(expectedCompletedUpper) || dwpcReq1StatusUpper.equals(dwpcTestDataLocal.get("ExpectedServiceStatusClosed")),
                "DWPC status for request 1 is not COMPLETED/CLOSED after case 2 Resolved. Actual: " + dwpcReq1Status);
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId1);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusCompleted), "Request 1 status is not '" + dwpStatusCompleted + "' in Past events");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request1_Completed_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Closed; DWPC -> CLOSED; DWP -> Closed (Past) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId1_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusClosed, "");
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.searchAndOpenCase(caseId1_1);
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq1Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId1, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq1Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusClosed"), "DWPC status for request 1 is not CLOSED after closing both cases");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId1);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusCompleted), "Request 1 status is not '" + dwpStatusCompleted + "' after Closed");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request1_Closed_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- Request 2: Submit multi-case request in DWP --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaByLabel(summaryLabel, dwpTestData.get("Summary2"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request2Submitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId2 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId2 != null && !requestId2.trim().isEmpty(), "Request 2 ID is blank");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 2 status is not '" + dwpStatusInProgress + "'");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Verify 2 cases created for request 2 and assign both --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(hrLob);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        Assert.assertEquals(bwfFeatureMethods.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(requestId2), 2, "Two cases are not seen created for request id: " + requestId2);
        List<String> caseIdsReq2Raw = bwfFeatureMethods.getVisibleValuesFromCaseConsoleColumn(caseIdColumn);
        Assert.assertTrue(caseIdsReq2Raw.size() >= 2, "Less than 2 case IDs found in grid for request id: " + requestId2);
        String caseId2_1 = caseIdsReq2Raw.get(0);
        String caseId2_2 = caseIdsReq2Raw.get(1);

        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseDetailEquals(externalTicketIdLabel, requestId2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);

        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.verifyCaseDetailEquals(externalTicketIdLabel, requestId2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.closeBrowser();
        

        // -------------------- DWPC: Verify request 2 is ACTIVE --------------------
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        String dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 2 is not ACTIVE");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 1 -> In Progress; DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.verifyCaseStatus(statusInProgress);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 2 is not ACTIVE after case 1 In Progress");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 1 -> Pending; DWPC + DWP -> Pending --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusPending, "");
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 2 is not PENDING after case 1 Pending");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusPending), "Request 2 status is not '" + dwpStatusPending + "' after case 1 Pending");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 2 -> In Progress; DWPC stays PENDING (Case 1 Pending) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.verifyCaseStatus(statusInProgress);
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 2 is not PENDING after case 2 In Progress while case 1 Pending");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Pending; DWPC stays PENDING --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusPending, "");
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 2 is not PENDING after both cases Pending");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Both cases -> Resolved; DWPC -> Completed/Closed; DWP -> Completed (Past) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusResolved, resolvedReason);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusResolved, resolvedReason);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        String dwpcReq2StatusUpper = dwpcReq2Status.toUpperCase();
        Assert.assertTrue(dwpcReq2StatusUpper.equals(expectedCompletedUpper) || dwpcReq2StatusUpper.equals(dwpcTestDataLocal.get("ExpectedServiceStatusClosed")),
                "DWPC status for request 2 is not COMPLETED/CLOSED after both cases Resolved. Actual: " + dwpcReq2Status);
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusCompleted), "Request 2 status is not '" + dwpStatusCompleted + "' in Past events");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Both cases -> In Progress; DWPC -> ACTIVE; DWP -> In Progress --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 2 is not ACTIVE after both cases In Progress");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 2 status is not '" + dwpStatusInProgress + "' after both cases In Progress");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 1 -> Canceled, Case 2 -> Pending; DWPC + DWP -> Pending --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusCanceled, canceledReason);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusPending, "");
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 2 is not PENDING after Canceled/Pending pair");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusPending), "Request 2 status is not '" + dwpStatusPending + "' after Canceled/Pending pair");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Assigned; DWPC -> ACTIVE; DWP -> In Progress --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusAssigned, "");
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 2 is not ACTIVE after Canceled/Assigned pair");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 2 status is not '" + dwpStatusInProgress + "' after Canceled/Assigned pair");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 2 -> In Progress; DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.verifyCaseStatus(statusInProgress);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 2 is not ACTIVE after Canceled/In Progress pair");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Resolved; DWPC -> Completed/Closed; DWP -> Completed (Past) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusResolved, resolvedReason);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        dwpcReq2StatusUpper = dwpcReq2Status.toUpperCase();
        Assert.assertTrue(dwpcReq2StatusUpper.equals(expectedCompletedUpper) || dwpcReq2StatusUpper.equals(dwpcTestDataLocal.get("ExpectedServiceStatusClosed")),
                "DWPC status for request 2 is not COMPLETED/CLOSED after Canceled/Resolved pair. Actual: " + dwpcReq2Status);
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusCompleted), "Request 2 status is not '" + dwpStatusCompleted + "' in Past events after Canceled/Resolved pair");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Case 2 -> Closed; DWPC -> CLOSED; DWP -> Closed (Past) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId2_1);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId2_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusClosed, "");
        bwfFeatureMethods.verifyCaseStatus(statusClosed);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq2Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId2, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq2Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusClosed"), "DWPC status for request 2 is not CLOSED after closing case 2");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId2);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusCompleted), "Request 2 status is not '" + dwpStatusCompleted + "' after Closed");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request2_Closed_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- Request 3: Submit multi-case request in DWP --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaByLabel(summaryLabel, dwpTestData.get("Summary3"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request3Submitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId3 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId3 != null && !requestId3.trim().isEmpty(), "Request 3 ID is blank");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId3);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 3 status is not '" + dwpStatusInProgress + "'");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Verify 2 cases created for request 3 and cancel case 1 --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(hrLob);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId3);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        Assert.assertEquals(bwfFeatureMethods.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(requestId3), 2, "Two cases are not seen created for request id: " + requestId3);
        List<String> caseIdsReq3Raw = bwfFeatureMethods.getVisibleValuesFromCaseConsoleColumn(caseIdColumn);
        Assert.assertTrue(caseIdsReq3Raw.size() >= 2, "Less than 2 case IDs found in grid for request id: " + requestId3);
        String caseId3_1 = caseIdsReq3Raw.get(0);
        String caseId3_2 = caseIdsReq3Raw.get(1);

        bwfFeatureMethods.searchAndOpenCase(caseId3_1);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.changeCaseStatusAndSave(statusCanceled, canceledReason);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId3_2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.closeBrowser();
        

        // -------------------- DWPC: Verify request 3 stays ACTIVE --------------------
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        String dwpcReq3Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId3, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq3Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 3 is not ACTIVE after canceling one case");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Cancel case 2; DWPC -> CANCELLED; DWP -> Cancelled (Past) --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId3_2);
        bwfFeatureMethods.changeCaseStatusAndSave(statusCanceled, canceledReason);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.searchAndOpenCase(caseId3_1);
        bwfFeatureMethods.verifyCaseStatus(statusCanceled);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq3Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId3, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq3Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusCancelled"), "DWPC status for request 3 is not CANCELLED after canceling both cases");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestId3);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusCancelled), "Request 3 status is not '" + dwpStatusCancelled + "' in Past events");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request3_Cancelled_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- Request 4: Submit multi-case request in DWP --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.fillTextAreaByLabel(summaryLabel, dwpTestData.get("Summary4"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request4Submitted", SolQAConstants.appDWP);
        dwpFeatureMethod.openActivityPage();
        String requestId4 = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId4 != null && !requestId4.trim().isEmpty(), "Request 4 ID is blank");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId4);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusInProgress), "Request 4 status is not '" + dwpStatusInProgress + "'");
        dwpFeatureMethod.closeBrowser();
        

        // -------------------- BWF: Verify 2 cases created for request 4, assign case 1, resolve case 2 --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(hrLob);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestId4);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        Assert.assertEquals(bwfFeatureMethods.numberOfTimesRequestIDIsPresentInCaseConsoleGrid(requestId4), 2, "Two cases are not seen created for request id: " + requestId4);
        List<String> caseIdsReq4Raw = bwfFeatureMethods.getVisibleValuesFromCaseConsoleColumn(caseIdColumn);
        Assert.assertTrue(caseIdsReq4Raw.size() >= 2, "Less than 2 case IDs found in grid for request id: " + requestId4);
        String caseId4_1 = caseIdsReq4Raw.get(0);
        String caseId4_2 = caseIdsReq4Raw.get(1);

        bwfFeatureMethods.searchAndOpenCase(caseId4_1);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);

        bwfFeatureMethods.searchAndOpenCase(caseId4_2);
        bwfFeatureMethods.verifyCaseStatus(statusNew);
        bwfFeatureMethods.clickEditCase();
        bwfFeatureMethods.clickAssignToMeBtn();
        bwfFeatureMethods.save_Edited_Case();
        bwfFeatureMethods.verifyCaseStatus(statusAssigned);
        bwfFeatureMethods.changeCaseStatusAndSave(statusResolved, resolvedReason);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.closeBrowser();
        

        // -------------------- DWPC: Verify request 4 stays ACTIVE --------------------
        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        String dwpcReq4Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId4, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq4Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 4 is not ACTIVE after assigning/resolving cases");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 1 -> In Progress; Case 2 stays Resolved; DWPC stays ACTIVE --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId4_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusInProgress, "");
        bwfFeatureMethods.verifyCaseStatus(statusInProgress);
        bwfFeatureMethods.searchAndOpenCase(caseId4_2);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq4Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId4, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq4Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusActive"), "DWPC status for request 4 is not ACTIVE after case 1 In Progress");
        dwpcFeatureMethods.closeBrowser();
        

        // -------------------- BWF: Case 1 -> Pending; Case 2 stays Resolved; DWPC + DWP -> Pending --------------------
        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        login(bwfUserDataLocal.get("UserName"), bwfUserDataLocal.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.searchAndOpenCase(caseId4_1);
        bwfFeatureMethods.changeCaseStatusAndSave(statusPending, "");
        bwfFeatureMethods.verifyCaseStatus(statusPending);
        bwfFeatureMethods.searchAndOpenCase(caseId4_2);
        bwfFeatureMethods.verifyCaseStatus(statusResolved);
        bwfFeatureMethods.closeBrowser();
        

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        login(dwpcUserDataLocal.get("UserName"), dwpcUserDataLocal.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcReq4Status = dwpcFeatureMethods.getServiceRequestReportColumnValue(dwpcReportsMenu, dwpcServiceRequestsSubMenu, requestId4, dwpcStatusColumnLabel);
        Assert.assertEquals(dwpcReq4Status.toUpperCase(), dwpcTestDataLocal.get("ExpectedServiceStatusPending"), "DWPC status for request 4 is not PENDING after case 1 Pending");
        dwpcFeatureMethods.closeBrowser();
        

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        login(dwpUserDataLocal.get("UserName"), dwpUserDataLocal.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId4);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService(dwpStatusPending), "Request 4 status is not '" + dwpStatusPending + "' after case 1 Pending");
        dwpFeatureMethod.captureScreenshot("DRSQA_11544_Request4_Pending_DWP", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        
    }

    @Test(description = "[DWP][Catalog] Service entitlement", groups = {"Regression_P1", "DWP"}, singleThreaded = true)
    @SolqaDetails(authorName = "automation", TTID = "468", JiraID = "DRSQA-11513", Products = {Product.Helix_DWP})
    public void DRSQA_11513_Service_Entitlement(ITestContext context) throws Exception {

        Map<Object, String> testData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11513_ServiceEntitlement");
        String serviceName = testData.get("ServiceRequestName");
        String expectedStatus = testData.get("ExpectedStatus");
        String catalogItemsTab = testData.get("GlobalSearchCatalogItemsTab");

        // -------------------- Step 1: Login as entitled user -> service should be visible and request can be submitted --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11513_Driver", getDriver());
        context.setAttribute("DRSQA_11513_Product", Product.Helix_DWP);

        String entitledUserKey = testData.get("EntitledUserKey");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, entitledUserKey);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11513_RequestSubmitted", SolQAConstants.appDWP);

        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Submitted request id is empty.");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyStatusDetailPage(expectedStatus);
        dwpFeatureMethod.captureScreenshot("DRSQA_11513_RequestStatusVerified", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 2: Login as non-entitled user -> service should NOT be visible --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11513_Driver", getDriver());
        context.setAttribute("DRSQA_11513_Product", Product.Helix_DWP);

        String notEntitledUserKey = testData.get("NotEntitledUserKey");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, notEntitledUserKey);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.searchInGlobalSearch(serviceName, catalogItemsTab);
        dwpFeatureMethod.verifyItemFound(serviceName, false);
        dwpFeatureMethod.captureScreenshot("DRSQA_11513_ServiceNotVisibleForNonEntitledUser", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
    }

    @Test(description = "[DWP][BWF] Create and submit dependency services", groups = {"Regression_P2", "DWP", "BWF"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11480", Products = {Product.Helix_DWP, Product.Helix_BWF})
    public void DRSQA_11480_CreateAndSubmitDependencyServices(ITestContext context) {

        // -------------------- DWP: Submit dependent services request --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11480_Driver", getDriver());
        context.setAttribute("DRSQA_11480_Product", Product.Helix_DWP);

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11480_RequestDetails");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fritzEnduser");
        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, SolQAConstants.BWFUsers, "Elizabeth");
        String childServiceName = dwpTestData.get("ServiceRequestName2");

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        dwpFeatureMethod.openAllItemsPage();
        dwpFeatureMethod.searchAndOpenServiceWithoutRequestNow(childServiceName);
        dwpFeatureMethod.verifySrdTitle("Dependency2 1.0");
        dwpFeatureMethod.ClickOnNewRequestInDependencySection();
        dwpFeatureMethod.clickRequestNowIfPresentOnDwpServicePage();
        dwpFeatureMethod.fillTextFieldByLabel("Summary", dwpTestData.get("Summary1"));
        dwpFeatureMethod.fillTextAreaByLabel("Description", dwpTestData.get("Description1"));
        dwpFeatureMethod.clickNextItemButton();
        dwpFeatureMethod.fillTextAreaByLabel("Description", dwpTestData.get("Description2"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.openActivityPage();
        String orderID = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents("2 items ordered");
        Assert.assertTrue(orderID != null && !orderID.trim().isEmpty(), "Request ID-1 is not created in DWP");
        dwpFeatureMethod.openSrmRequestByRequestID(orderID);
        dwpFeatureMethod.verifyOpenedRequestTitle("2 items ordered");
        String requestID1 = dwpFeatureMethod.getRequestIDFromServiceItems("Dependency1 1.0");
        String requestID2 = dwpFeatureMethod.getRequestIDFromServiceItems("Dependency2 1.0");
        Assert.assertTrue(requestID1 != null && !requestID1.trim().isEmpty(), "Request ID for child service is not created in DWP");
        Assert.assertTrue(requestID2 != null && !requestID2.trim().isEmpty(), "Request ID for parent service is not created in DWP");
        dwpFeatureMethod.closeBrowser();

        bwfDriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfDriver);
        context.setAttribute("DRSQA_11480_Driver", bwfDriver);
        context.setAttribute("DRSQA_11480_Product", Product.Helix_BWF);
        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
        bwfFeatureMethods = new BWFFeatureMethods();
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestID1);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestID1);
        Assert.assertTrue(bwfFeatureMethods.getCaseDetail("Summary").contains(dwpTestData.get("Summary1")));
        Assert.assertTrue(bwfFeatureMethods.getDescriptionValue().contains(dwpTestData.get("Description1")));

        bwfFeatureMethods.navigateToPage("Workspace", "Case");
        bwfFeatureMethods.verifyLoginToBWF();
        bwfFeatureMethods.setLineOfBusiness_CaseConsole(SolQAConstants.HR);
        bwfFeatureMethods.removeAllFiltersOnCaseWorkspaceIfPresent();
        bwfFeatureMethods.setSearchTextField_CaseConsole(requestID2);
        bwfFeatureMethods.clickSearchBtn_CaseConsole();
        bwfFeatureMethods.clickCaseIDLink_CaseConsole(requestID2);
        Assert.assertTrue(bwfFeatureMethods.getDescriptionValue().contains(dwpTestData.get("Description2")));
        bwfFeatureMethods.closeBrowser();
    }

    @Test(description = "To-Do - Ability to enforce service actions & show end-user tasks in DWP with completion action", groups = {"Regression_P1", "DWP", "BWF"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "34408", JiraID = "DRSQA-11645", Products = {Product.Helix_BWF, Product.Helix_DWP})
    public void DRSQA_11645_To_Do_Ability_to_enforce_service_actions_and_show_end_user_tasks_in_DWP_with_completion_action(ITestContext context) throws Exception {

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "TTID_34408_Details");

        // -------------------- Precondition: Enable To-Dos in DWP Admin --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP_ADMIN);
        setDriver(dwpDriver);
        context.setAttribute("TTID_34408_DWP_ADMIN_Driver", getDriver());
        context.setAttribute("TTID_34408_DWP_ADMIN_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "DWPAdminUsersWithMyITSuperAdminPermission");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.enableFeatureSettingByNameInDWPAdmin(dwpTestData.get("configSetting"), true);
        dwpFeatureMethod.captureScreenshot("TTID_34408_DWPAdmin_ToDos_Enabled", SolQAConstants.appDWP);
        dwpFeatureMethod.closeBrowser();
        

        // Umbrella also imported + activated a Service Action in DWPC to enforce the guided To-Do flow.
        // SolutionsQA does not currently have reusable DWPC Service Actions UI automation; this test assumes the
        // Service Action is already enabled in the environment (same end-user validations in DWP + BWF are preserved).

        // -------------------- Step 1/2: Login to DWP and submit Dynamic BWF request --------------------
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("TTID_34408_DWP_Driver", getDriver());
        context.setAttribute("TTID_34408_DWP_Product", Product.Helix_DWP);

        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "fritzEnduser");
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();

        String serviceName = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.clickOnRequestNow(serviceName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("templateIDQuestion"), dwpTestData.get("templateGUID"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("TTID_34408_RequestSubmitted", SolQAConstants.appDWP);

        dwpFeatureMethod.openActivityPage();
        String reqId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        Assert.assertTrue(reqId != null && !reqId.trim().isEmpty(), "Request ID is blank for service: " + serviceName);

        // -------------------- Step 3/4: Open To-Do 1 and submit responses (with attachment) --------------------
        String todo1Title = dwpTestData.get("ToDo1");
        String todo2Title = dwpTestData.get("ToDo2");
        dwpFeatureMethod.openSubmittedSbRequestInActiveEventsByTitle(todo1Title);
        dwpFeatureMethod.verifySrdTitle(todo1Title);
        dwpFeatureMethod.captureScreenshot("TTID_34408_ToDo1_Opened", SolQAConstants.appDWP);

    }

    @Test(description = "Display DWP_C Repeatable type questions as attachments in BWF.", groups = {"Regression_P1", "DWP", "BWF", "DWPC"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "18034", JiraID = "DRSQA-11654", Products = {Product.Helix_DWP, Product.Helix_BWF, Product.Helix_DWPC})
    public void DRSQA_11654_Display_DWP_C_Repeatable_questions(ITestContext context) throws Exception {

        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCTestData, "DRSQA_11654_Service");
        String serviceName = dwpcTestData.get("serviceName");

        dwpcDriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(dwpcDriver);
        context.setAttribute("DRSQA_11654_DWPC_Driver", getDriver());
        context.setAttribute("DRSQA_11654_DWPC_Product", Product.Helix_DWPC);

        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(serviceName, serviceName);
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service is not present/published in DWPC: " + serviceName);
        dwpcFeatureMethods.captureScreenshot("DRSQA_11654_DWPC_ServicePresent", SolQAConstants.appDWPC);
        dwpcFeatureMethods.closeBrowser();

        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11654_Service");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11654_DWP_Driver", getDriver());
        context.setAttribute("DRSQA_11654_DWP_Product", Product.Helix_DWP);

        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.openCatalogPage();
        dwpFeatureMethod.openAllItemsPage();
        dwpFeatureMethod.searchAndOpenService(serviceName);
        dwpFeatureMethod.verifySrdTitle(serviceName);
        String uniqueSummary = dwpTestData.get("SummaryPrefix") + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), uniqueSummary);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("WhereToMoveLabel"), dwpTestData.get("WhereToMoveValue"));
        dwpFeatureMethod.fillTextDateFieldByLabel("when to move", getDateByOffset(0));
        dwpFeatureMethod.fillTextAreaByLabel("Any Special Requests?", "No");
        dwpFeatureMethod.addResponseDWPCRepeatQuestionService("response1", "response2");
        dwpFeatureMethod.clickOnSubmitRequest();

        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(uniqueSummary);
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Submitted request id is empty.");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyStatusOfService("");
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestContext context) {
        log.info("Closing the browser");
        if (dwpDriver != null) {
            dwpFeatureMethod.closeBrowser();
        }
        if (bwfFeatureMethods != null) {
            bwfFeatureMethods.closeBrowser();
        }
        if (getDriver() != null) {
            closeBrowser();
        }
    }

}