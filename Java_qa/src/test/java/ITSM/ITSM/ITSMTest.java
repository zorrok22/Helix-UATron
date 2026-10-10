package ITSM.ITSM;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import pages.ITSM.EMAIL.EmailFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ITSMTest extends Launcher {
    public static SolqaLogger log = new SolqaLogger(ITSMTest.class.getName());
    ReadJson readJson;
    ITSMFeatureMethods itsmFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods2= new ITSMFeatureMethods();
    SmartITFeatureMethodsPage smartITMethods;
    Map<Object, String> itsmUserData, itsmTestData, dwpUserData, dwpTestData;
    WebDriver itsmDriver, itsmDriver2;
    UserUpdater itsmUsers;
    int counter;

    @BeforeTest(alwaysRun=true)
    public void setupUsers() {
        counter = Integer.parseInt(configProp.get("runCounter"));
        if (counter > 0) {
            // Load the JSON file once
            String filename = "ITSMUsers";
            itsmUsers = new UserUpdater();
            String excelFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITSM + File.separator + filename + ".xlsx";
            String jsonFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITSM + File.separator + filename + ".json";

            log.info("Setting up users with runCounter: " + counter);
            log.info("Excel file path: " + excelFilePath);
            log.info("JSON file path: " + jsonFilePath);

            // Load the JSON file
            itsmUsers.loadJsonFile(jsonFilePath);

            // Read the specific row based on runCounter
            // Note: row index 1 corresponds to first data row (after header)
            // So if runCounter is 3, we read row 3 from Excel
            itsmUsers.updateFromExcelRow(excelFilePath, counter);

            log.info("Updated ITSMUsers.json for run counter: " + counter + " using row: " + counter);

            // Verify the update by reading the JSON back
        } else {
            //No change required
            log.info("runCounter is 0, no user updates required");
        }
    }

    @Test(description = "Create Incident from Incident Management console", groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11789", Products = {Product.Helix_ITSM})
    public void DRSQA_11789_Create_Incident_from_ITSM(ITestContext context) throws IOException {

        log.info("*************** Start: DRSQA_11789_Create_Incident_from_ITSM ****************");
        // Use existing WebDriver from ThreadLocal
     
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11789_Driver", getDriver());
        context.setAttribute("DRSQA_11789_Product", Product.Helix_ITSM);
       
        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11789_TestData");

        log.info("******Step 1: Login to ITSM **********");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Incident Management > New Incident");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.NEW_INCIDENT);
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_NewIncident", SolQAConstants.appITSM);

        log.info("Fill Incident Data");
        String incID = itsmFeatureMethods.createIncident(itsmTestData);
        log.info("Created Incident ID: " + incID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_IncidentCreated", SolQAConstants.appITSM);
        log.info("Search for the created Incident");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.SEARCH_INCIDENT);

        itsmFeatureMethods.searchIncident(incID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_SearchIncident", SolQAConstants.appITSM);

        log.info("Change status to 'In Progress'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.IN_PROGRESS, SolQAConstants.TEST, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_ChangeStatusToInProgress", SolQAConstants.appITSM);

        log.info("Change status to 'Resolved'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.RESOLVED, "", SolQAConstants.FUTURE_ENHANCEMENT);
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_ChangeStatusToResolved", SolQAConstants.appITSM);

        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.STATUS_CLOSED, "", "");
        itsmFeatureMethods.verifyIncidentStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11789_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);

        log.info("Close the browser");
        itsmFeatureMethods.closeBrowser();
    }

    //previous owner gpreethi, disabling the usecase as Microsoft email exchange server is not accessible
    @Test(description = "Create Incident from an Email with exchange Server", timeOut = 1800000, groups = {"Regression_P3", "ITSM", "sonalsha"}, enabled = false)
    @SolqaDetails(authorName = "sonalsha", TTID = "9395", JiraID = "DRSQA-11557", Products = {Product.Helix_ITSM})
    public void DRSQA_11557_Create_Incident_from_Email_Exchange_Server(ITestContext context) throws Exception {

        log.info("*************** Start: DRSQA_11557 Create Incident from Email (Exchange) ****************");

        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11557_Data");
        
        String itsmLoginUserKey = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "user", "DRSQA_11557_Data")
                .get("ITSMLoginUserKey").trim();
        String emailSenderUserKey = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "user", "DRSQA_11557_Data")
                .get("EmailSenderUserKey").trim();

        log.info("******Step 1: Send email to create incident **********");

        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", emailSenderUserKey);

        String toAddress = itsmTestData.get("EmailToAddress");
        String subjectBase = itsmTestData.get("EmailSubject");
        String subjectToken = subjectBase + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        Assert.assertNotNull(toAddress, "EmailToAddress is null. Configure it in ITSMTestData.json");
        Assert.assertFalse(toAddress.trim().isEmpty(), "EmailToAddress is empty. Configure it in ITSMTestData.json");
        Assert.assertFalse(toAddress.contains("__"),
                "EmailToAddress is not configured (placeholder found): " + toAddress);
        Assert.assertNotNull(subjectBase, "EmailSubject is null. Configure it in ITSMTestData.json");
        Assert.assertFalse(subjectBase.contains("__"),
                "EmailSubject is not configured (placeholder found): " + subjectBase);
        Assert.assertNotNull(itsmTestData.get("ExpectedTemplateName"), "ExpectedTemplateName is null. Configure it in ITSMTestData.json");
        Assert.assertFalse(itsmTestData.get("ExpectedTemplateName").contains("__"),
                "ExpectedTemplateName is not configured (placeholder found): " + itsmTestData.get("ExpectedTemplateName"));

        WebDriver emailDriver = launchWeb(SolQAConstants.EMAIL, SolQAConstants.EMAIL);
        setDriver(emailDriver);
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.EMAIL);

        EmailFeatureMethods emailFeatureMethods = new EmailFeatureMethods();
        emailFeatureMethods.composeNewEmail(toAddress, subjectToken);
        emailFeatureMethods.verifyRequiredMailResponse(subjectToken);
        String requestId = emailFeatureMethods.getINCIDFromMailMessage(subjectToken);
        Assert.assertNotNull(requestId, "Request ID not found in inbox emails for subject: " + subjectToken);
        log.info("Request ID found from email: " + requestId);
        closeBrowser();

        log.info("******Step 2: Login to ITSM and validate template on created incident **********");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmFeatureMethods = new ITSMFeatureMethods();

        Map<Object, String> itsmLoginUser = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", itsmLoginUserKey);
        login(itsmLoginUser.get("UserName"), itsmLoginUser.get("Password"), SolQAConstants.appITSM);

        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.SEARCH_INCIDENT);
        itsmFeatureMethods.searchIncident(requestId);

        String templateFieldLabel = itsmTestData.get("TemplateFieldLabel");
        String actualTemplate = itsmFeatureMethods.getIncidentFieldValue(templateFieldLabel);

        Assert.assertEquals(actualTemplate, itsmTestData.get("ExpectedTemplateName"),
                "Template name is not present for incident id: " + requestId);

        itsmFeatureMethods.closeBrowser();

        log.info("*************** End: DRSQA_11557 ****************");
    }

    @Test(description = "Create Change request from Change Management console", groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11788", Products = {Product.Helix_ITSM})
    public void DRSQA_11788_Create_ChangeRequest_from_ITSM(ITestContext context) throws Exception {
        log.info("*************** Start: DRSQA_11788_Create_ChangeRequest_from_ITSM ****************");
        // Use existing WebDriver from ThreadLocal
       
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11788_Driver", itsmDriver);
        context.setAttribute("DRSQA_11788_Product", Product.Helix_ITSM);
        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11788_TestData");

        log.info("******Step 1: Login to ITSM **********");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Change Management > New Change");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.NEW_CHANGE);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_NewChange", SolQAConstants.appITSM);

        log.info("Fill Change Data");
        String changeID = itsmFeatureMethods.createChange(itsmTestData);
        log.info("Created Change ID: " + changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeCreated", SolQAConstants.appITSM);

        log.info("Search for the created Change");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
        itsmFeatureMethods.searchChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_SearchChange", SolQAConstants.appITSM);

        log.info("Change status to 'Request For Authorization'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.REQUEST_FOR_AUTHORIZATION);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToRequestForAuthorization", SolQAConstants.appITSM);

        itsmDriver2 = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver2);
        context.setAttribute("DRSQA_11788_Driver", getDriver());
        context.setAttribute("DRSQA_11788_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods2 = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcOpsManagerUser");

        log.info("******Login to ITSM **********");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods2.captureScreenshot("DRSQA_11788_ITSM_Login", SolQAConstants.appITSM);

        itsmFeatureMethods2.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods2.approveChange(changeID);
        itsmFeatureMethods2.captureScreenshot("DRSQA_11788_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods2.closeBrowser();

        itsmFeatureMethods.switchToITSM(itsmDriver);
        log.info("Change status to 'Planning In Progress'");
        itsmFeatureMethods.refreshITSMPage();
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.PLANNING_IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToPlanningInProgress", SolQAConstants.appITSM);

        log.info("Change status to 'Scheduled For Review'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.SCHEDULED_FOR_REVIEW);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToClosed", SolQAConstants.appITSM);

        log.info("Change status to 'Scheduled For Approval'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.SCHEDULED_FOR_APPROVAL);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToScheduledForApproval", SolQAConstants.appITSM);

        log.info("Change status to 'Scheduled'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.SCHEDULED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToScheduled", SolQAConstants.appITSM);

        log.info("Change status to 'Implementation In Progress'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.IMPLEMENTATION_IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToImplementationInProgress", SolQAConstants.appITSM);

        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.COMPLETED_STATUS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToCompleted", SolQAConstants.appITSM);

        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_ChangeStatusToClosed", SolQAConstants.appITSM);

        log.info("Verify the status of the change");
        itsmFeatureMethods.verifyChangeStatus(SolQAConstants.STATUS_CLOSED);

        itsmFeatureMethods.captureScreenshot("DRSQA_11788_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
    }

    @Test(description = "Create Work Order from Work Order console", groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa",  JiraID = "DRSQA-11787", Products = {Product.Helix_ITSM})
    public void DRSQA_11787_Create_WorkOrder_from_ITSM(ITestContext context) throws IOException {
        log.info("*************** Start: DRSQA_11787_Create_WorkOrder_from_ITSM ****************");
       
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11787_Driver", itsmDriver);
        context.setAttribute("DRSQA_11787_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11787_TestData");

        log.info("Step 1/12: Login to ITSM as " + itsmUserData.get("UserName"));
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Work Order Management > New Work Order");
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.NEW_WORK_ORDER);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_NewWorkOrder", SolQAConstants.appITSM);

        log.info("Fill Work Order Data");
        String workOrderID = itsmFeatureMethods.createWorkOrder(itsmTestData);
        log.info("Created Work Order ID: " + workOrderID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_WorkOrderCreated", SolQAConstants.appITSM);

        log.info("Search for the created Work Order");
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
        itsmFeatureMethods.searchWorkOrder(workOrderID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_SearchWorkOrder", SolQAConstants.appITSM);

        log.info("Change status to 'In Progress'");
        itsmFeatureMethods.updateWorkOrderStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_WorkOrderStatusToInProgress", SolQAConstants.appITSM);

        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateWorkOrderStatus(SolQAConstants.COMPLETED_STATUS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_WorkOrderStatusToCompleted", SolQAConstants.appITSM);

        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateWorkOrderStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_WorkOrderStatusToClosed", SolQAConstants.appITSM);

        log.info("Verify the status of the work order");
        itsmFeatureMethods.verifyWorkOrderStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11787_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);

        log.info("Close the browser");
        itsmFeatureMethods.closeBrowser();
    }

    @Test(description = "Create Knowledge article from Knowledge Management console", groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11786", Products = {Product.Helix_ITSM})
    public void DRSQA_11786_Create_KnowledgeArticle(ITestContext context) throws Exception {

        log.info("*************** Start: DRSQA_11786_Create_KnowledgeArticle ****************");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11786_Driver", getDriver());
        context.setAttribute("DRSQA_11786_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11786_TestData");

        log.info("Login to ITSM" + itsmUserData.get("UserName"));
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.verifyITSMLogin();
        itsmFeatureMethods.captureScreenshot("DRSQA_11786_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Knowledge Management > New Knowledge Article");
        itsmFeatureMethods.navigateMenu(SolQAConstants.KNOWLEDGE_MANAGEMENT, SolQAConstants.NEW_ARTICLE);
        itsmFeatureMethods.captureScreenshot("DRSQA_11786_ITSM_NewKnowledgeArticle", SolQAConstants.appITSM);

        log.info("Fill Knowledge Article Data");
        String knowledgeArticleID = itsmFeatureMethods.createKnowledgeArticle(itsmTestData);
        log.info("Created Knowledge Article ID: " + knowledgeArticleID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11786_ITSM_KnowledgeArticleCreated", SolQAConstants.appITSM);

        log.info("Change status to 'Draft'");
        itsmFeatureMethods.updateArticleStatus(SolQAConstants.DRAFT);
        itsmFeatureMethods.captureScreenshot("DRSQA_11786_ITSM_KnowledgeStatusToDraft", SolQAConstants.appITSM);

        log.info("Change status to 'Publish Approval'");
        itsmFeatureMethods.refreshITSMPage();
        itsmFeatureMethods.updateArticleStatus(SolQAConstants.PUBLISH_APPROVAL);
        itsmFeatureMethods.captureScreenshot("DRSQA_11786_ITSM_KnowledgeStatusToPublishApproval", SolQAConstants.appITSM);

        log.info("Search for the created Knowledge Article KAID = " + knowledgeArticleID);
        itsmFeatureMethods.navigateMenu(SolQAConstants.KNOWLEDGE_MANAGEMENT, SolQAConstants.SEARCH_KNOWLEDGE);
        itsmFeatureMethods.searchKnowledgeArticle(knowledgeArticleID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11786_ITSM_SearchKnowledgeArticle", SolQAConstants.appITSM);

        log.info("Verify the article result");
        itsmFeatureMethods.verifyArticleResult();

        log.info("Close the browser");
        itsmFeatureMethods.closeBrowser();
    }

    @Test(description = "Create Problem from Problem Management console",groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11785", Products = {Product.Helix_ITSM})
    public void DRSQA_11785_Create_Problem(ITestContext context) throws IOException {

        log.info("*************** Start: DRSQA_11785_Create_Problem ****************");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11785_Driver", getDriver());
        context.setAttribute("DRSQA_11785_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11785_TestData");

        log.info("Login to ITSM" + itsmUserData.get("UserName"));
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Problem Management > New Problem");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.PROBLEM_MANAGEMENT, SolQAConstants.NEW_PROBLEM);
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_NewProblem", SolQAConstants.appITSM);

        log.info("Fill Problem Data");
        String problemID = itsmFeatureMethods.createProblem(itsmTestData);
        log.info("Created Problem ID: " + problemID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_ProblemCreated", SolQAConstants.appITSM);

        log.info("Search for the created Problem");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.PROBLEM_MANAGEMENT, SolQAConstants.SEARCH_PROBLEM);
        itsmFeatureMethods.searchProblem(problemID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_SearchProblem", SolQAConstants.appITSM);

        log.info("Change status to 'Under Review'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.UNDER_REVIEW, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_ProblemStatusToUnderReview", SolQAConstants.appITSM);

        log.info("Change status to 'Under Investigation'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.UNDER_INVESTIGATION, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_ProblemStatusToUnderInvestigation", SolQAConstants.appITSM);

        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.COMPLETED_STATUS, SolQAConstants.ENHANCEMENT_REQUEST);
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_ProblemStatusToCompleted", SolQAConstants.appITSM);

        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.STATUS_CLOSED, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_ProblemStatusToClosed", SolQAConstants.appITSM);

        log.info("Verify the status of the problem");
        itsmFeatureMethods.verifyProblemStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11785_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);

        log.info("Close the browser");
        itsmFeatureMethods.closeBrowser();
    }

    @Test(description = "Create Release request from Release Management console", groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11784", Products = {Product.Helix_ITSM})
    public void DRSQA_11784_Create_Release(ITestContext context) throws IOException  {

        log.info("*************** Start: DRSQA_11784_Create_Release ****************");
       
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11783_Driver", getDriver());
        context.setAttribute("DRSQA_11783_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11784_TestData");

        log.info("Login to ITSM" + itsmUserData.get("UserName"));
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_Login", SolQAConstants.appITSM);

        log.info("Step 2/12: Navigate to Release Management > New Release");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.RELEASE_MANAGEMENT, SolQAConstants.NEW_RELEASE);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_NewRelease", SolQAConstants.appITSM);

        log.info("Step 3/12: Fill release data and create record");
        String releaseID = itsmFeatureMethods.createRelease(itsmTestData);
        log.info("Step 3/12 (result): Release created; ID=" + releaseID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseCreated", SolQAConstants.appITSM);

        log.info("Step 4/12: Search for the created release");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.RELEASE_MANAGEMENT, SolQAConstants.SEARCH_RELEASE);
        itsmFeatureMethods.searchRelease(releaseID);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_SearchRelease", SolQAConstants.appITSM);

        log.info("Step 5/12: Click on Next and verify status is 'Registered' and verify milestone 'Initiate'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.INITIATE);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.REGISTERED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToRegistered", SolQAConstants.appITSM);

        log.info("Step 6/12: Click on Next and verify status is 'In Progress' and verify milestone 'Planning'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.PLANNING);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 7/12: Click on Next and verify status is 'In Progress' and verify milestone 'Build'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.BUILD);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 8/12: Click on Next and verify status is 'In Progress' and verify milestone 'Test'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.TEST);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 9/12: Click on Next and verify status is 'In Progress' and verify milestone 'Deployment'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.DEPLOYMENT);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 10/12: Click on Next and verify status is 'Completed' and verify milestone 'Close Down'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.CLOSE_DOWN);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.COMPLETED_STATUS);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToCompleted", SolQAConstants.appITSM);

        log.info("Step 11/12: Click on Next and verify status is 'Closed' and verify final status");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.CLOSE_DOWN);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_11784_ITSM_ReleaseStatusToCompleted", SolQAConstants.appITSM);

        log.info("Step 12/12: Close the browser");
        itsmFeatureMethods.closeBrowser();
       
    }

    @Test(description = "CMDB-BMCOps-ExpressCI-Import-UDM", groups = {"MAT", "ITSM"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11816", Products = {Product.Helix_ITSM})
    public void DRSQA_11816_CMDB_BMCOPs_ExpressCI_Import_UDM(ITestContext context) throws Exception {

        log.info("*************** Precondition: Delete existing onbmc-s.bmc record from UDM:RAppPassword form****************");
        log.info("Login to ITSM with admin credentials from properties.config (adminUserName / adminPassword)");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11816_Driver", itsmDriver);
        context.setAttribute("DRSQA_11816_Product", Product.Helix_ITSM);
        readJson = new ReadJson();

        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.verifyITSMLogin();

        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11816_TestData");
        String formName = itsmTestData.get("FormName");
        String serverSearch = itsmTestData.get("ServerNameSearch");
        String serverName = itsmTestData.get("ServerName");

        log.info("Precondition: open " + formName + " (Search), search Server Name '" + serverSearch + "'; delete row only if '" + serverName + "' is present.");
        itsmFeatureMethods.LoadForm(formName, SolQAConstants.SEARCH, CommonMethods.readConfig("ARServerAlias"));
        itsmFeatureMethods.searchServerName(serverSearch);

        if (!itsmFeatureMethods.isServerNameSearchResultRowPresent(serverName)) {
            log.info("No row found for server name '" + serverName + "'; ending precondition without delete.");
        } else {
            log.info("Row found for server name '" + serverName + "'; deleting record before DMT job.");
            itsmFeatureMethods.deleteServerName(serverName);
        }
        itsmFeatureMethods.closeBrowser();
        log.info("*************** End Precondition: Delete existing onbmc-s.bmc record from UDM:RAppPassword form ****************");

        log.info("*************** Start: DRSQA_11816_CMDB_BMCOPs_ExpressCI_Import_UDM ****************");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11816_Driver", itsmDriver);
        context.setAttribute("DRSQA_11816_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11816_TestData");

        log.info("Login to ITSM" + itsmUserData.get("UserName"));
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.captureScreenshot("DRSQA_11816_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Data Management > DMT Job");
        itsmFeatureMethods.navigateToPageFromApplication("Data Management", "Job Console");
        itsmFeatureMethods.captureScreenshot("DRSQA_11816_ITSM_DMTJobConsole", SolQAConstants.appITSM);

        log.info("Fill DMT Job Data and Run the job");
        itsmFeatureMethods.createAndExecuteDMTJob(itsmTestData);

        log.info("Close the browser");
        itsmFeatureMethods.closeBrowser();
    }
    
    @Test(description = "[Core Deployment] Verify the packing list entries in the 'AR System Application Definition' form", groups = {"CORE_DEPLOYMENT", "ITSM", "Regression_P3", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-13330", Products = {Product.Helix_ITSM})
    public void DRSQA_13330_Core_Deployment_Verify_the_packing_list_entries_in_the_AR_System_Application_Definition_form(ITestContext context) throws IOException {

        log.info("***** Start: DRSQA_13330_Core_Deployment_Verify_the_packing_list_entries_in_the_AR_System_Application_Definition_form *****");
     
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_13330_Driver", getDriver());
        context.setAttribute("DRSQA_13330_Product", Product.Helix_ITSM);
       
        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_13330_TestData");

        log.info("Step 1: Login to ITSM");
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_13330_ITSM_Login", SolQAConstants.appITSM);
        
        log.info("Step 2: Open form: AR System ApplicationDefinition");
        itsmFeatureMethods.LoadForm(SolQAConstants.AR_SYSTEM_APPLICATION_DEFINITION, SolQAConstants.DEFAULT_ADMINISTRATOR, configProp.get("ARServerAlias"));
        
        log.info("Search Request ID as %");
        itsmFeatureMethods.searchRequestID("%");
        
        log.info("Step 3: Verify entries in the form 'AR System Application Definition'");
        itsmFeatureMethods.verifyFormNames(itsmTestData.get("expectedFormNames"));

        log.info("***** End : DRSQA_13330_Core_Deployment_Verify_the_packing_list_entries_in_the_AR_System_Application_Definition_form *****");
    }

    @Test(description = "Create incident using Platform API", groups = {"Regression_P1", "ITSM", "hpareek"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-15667", Products = {Product.Helix_ITSM})
    public void DRSQA_15667_API_ITSM_Create_Incident_via_PlatformAPI(ITestContext context) throws Exception {
        log.info("*************** Start: DRSQA_15667_API_ITSM_Create_Incident_via_PlatformAPI ****************");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_15667_Driver", getDriver());
        context.setAttribute("DRSQA_15667_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
        String jsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "SMARTIT" + File.separator + "SMARTITAPI.json";
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.createIncidentViaPlatformAPI(jsonPath, "Create_Incident_DRSQA_15667", itsmUserData.get("UserName"), itsmUserData.get("Password"));
        log.info("*************** End: DRSQA_15667_API_ITSM_Create_Incident_via_PlatformAPI ****************");
    }

    @Test(description = "Create Alarms using NetOps", groups = {"ZDT","MAT", "ITSM"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-11813", Products = {Product.Helix_ITSM})
    public void DRSQA_11813_API_ITSM_Create_Alarms_via_NetOps(ITestContext context) throws Exception {
        log.info("*************** Start: DRSQA_11813_API_ITSM_Create_Alarms_via_NetOps ****************");

        itsmDriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11813_Driver", getDriver());
        context.setAttribute("DRSQA_11813_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "bmcopssupportagent");
        String jsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "SMARTIT" + File.separator + "SMARTITAPI.json";
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.createAlarmsViaNetOps(jsonPath, "NetOpsAlarm_DRSQA_11813", itsmUserData.get("UserName"), itsmUserData.get("Password"));
        log.info("First Alarm is created successfully");

        itsmFeatureMethods.createAlarmsViaNetOps(jsonPath, "NetOpsAlarm_DRSQA_11813", itsmUserData.get("UserName"), itsmUserData.get("Password"));
        log.info("Second Alarm is created successfully");
        
        // Open SmartIT using same driver and verify the incident
        log.info("****** Navigating to SmartIT to verify incident **********");
        smartITMethods = new SmartITFeatureMethodsPage();
        String message ="Polling failure exceeds Critical threshold";
        log.info("Login to ITSM" + itsmUserData.get("UserName"));
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        log.info("****** Searching for incident with message: " + message + " **********");
        smartITMethods.globalSearchAndOpenTicketUsingMessage(message);
        smartITMethods.clickViewDetails();
        smartITMethods.captureScreenshot("DRSQA_11813_ITSM_ViewDetails", SolQAConstants.appSmartIT);

        log.info("****** Verifying alarm details text **********");
        smartITMethods.verifyAlarmDetailsText(message);
        log.info("****** Alarm details text verified successfully **********");
        log.info("*************** End: DRSQA_11813_API_ITSM_Create_Alarms_via_NetOps ****************");
    }

    @Test(description = "DMT - Load People Record Using UDM Job", groups = {"Regression_P1"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11747", Products = {Product.Helix_ITSM})
    public void DRSQA11747_Load_People_Record_Using_UDM_Job(ITestContext context) throws Exception {

        log.info("*************** Start: DRSQA11747_Load_People_Record_Using_UDM_Job ****************");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_11747_Driver", getDriver());
        context.setAttribute("DRSQA_11747_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA11747_data");

        log.info("Step: 1 Login to ITSM with user: " + CommonMethods.readConfig("adminUserName"));
        login(CommonMethods.readConfig("adminUserName"), CommonMethods.readConfig("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA11747_ITSM_Login", SolQAConstants.appITSM);

        log.info("Step 2: Navigate to Data Management > Job Console");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.DATA_MANAGEMENT, SolQAConstants.JOB_CONSOLE);
        itsmFeatureMethods.captureScreenshot("DRSQA11747_ITSM_JobConsole", SolQAConstants.appITSM);

        log.info("Step 3: Create and execute DMT job (People template) and verify job result");
        itsmFeatureMethods.createAndExecuteDMTJob(itsmTestData);
        itsmFeatureMethods.captureScreenshot("DRSQA11747_ITSM_JobCompleted", SolQAConstants.appITSM);

        log.info("Close the browser");
        itsmFeatureMethods.closeBrowser();

        log.info("*************** End: DRSQA11747_Load_People_Record_Using_UDM_Job ****************");
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (itsmFeatureMethods2 != null) {
            itsmFeatureMethods2.closeBrowser();
        }
    }
}