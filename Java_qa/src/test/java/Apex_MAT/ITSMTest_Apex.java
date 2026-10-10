package Apex_MAT;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class ITSMTest_Apex extends Launcher {
    public static SolqaLogger log = new SolqaLogger(ITSMTest_Apex.class.getName());
    ReadJson readJson;
    ITSMFeatureMethods itsmFeatureMethods;
    Map<Object, String> itsmUserData, itsmTestData;
    WebDriver itsmDriver;
    
    @Test(description = "[ITSM] Creating and Testing Release Lifecycle in ITSM")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12053", Products = {Product.Helix_ITSM})
    public void DRSQA_12053_ITSM_Creating_and_Testing_Release_Lifecycle_in_ITSM(ITestContext context) throws IOException  {

        log.info("***** Start: DRSQA_12053_ITSM_Creating_and_Testing_Release_Lifecycle_in_ITSM *****");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12053_Driver", getDriver());
        context.setAttribute("DRSQA_12053_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12053_TestData");

        log.info("Step 1 : Login to Remedy Release Management console --> Click Create Release");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Release Management > New Release");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.RELEASE_MANAGEMENT, SolQAConstants.NEW_RELEASE);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_NewRelease", SolQAConstants.appITSM);

        log.info("Step 2 : Populate data in mandatory fields, leave default selection and Save");
        String releaseID = itsmFeatureMethods.createReleaseApex(itsmTestData);
        log.info("Created Release ID: " + releaseID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseCreated", SolQAConstants.appITSM);

        log.info("Step 3 : From Release Management console - Open the release created in Step#2");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.RELEASE_MANAGEMENT, SolQAConstants.SEARCH_RELEASE);
        itsmFeatureMethods.searchRelease(releaseID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_SearchRelease", SolQAConstants.appITSM);

        log.info("Step 4 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to Initiate");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.INITIATE);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.REGISTERED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToRegistered", SolQAConstants.appITSM);

        log.info("Step 5 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to Planning");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.PLANNING);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 6 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to Build");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.BUILD);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 7 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to 'In Progress'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.TEST);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 8 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to 'In Progress'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.DEPLOYMENT);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 9 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.CLOSE_DOWN);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.COMPLETED_STATUS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToCompleted", SolQAConstants.appITSM);

        log.info("Step 10 : Click Next Stage button to update the status of the release request & Refresh the release request view using refresh icon available next to global search box");
        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateTicketStatusUsingNextStageButton();
        itsmFeatureMethods.verifyFieldValue(SolQAConstants.MILESTONE, SolQAConstants.CLOSE_DOWN);
        itsmFeatureMethods.verifyReleaseStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12053_ITSM_ReleaseStatusToCompleted", SolQAConstants.appITSM);

        log.info("***** END: DRSQA_12053_ITSM_Creating_and_Testing_Release_Lifecycle_in_ITSM *****");
    }
    
    @Test(description = "[ITSM] Creating and Testing Problem Lifecycle in ITSM")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12054", Products = {Product.Helix_ITSM})
    public void DRSQA_12054_ITSM_Creating_and_Testing_Problem_Lifecycle_in_ITSM(ITestContext context) throws IOException {

        log.info("***** START : DRSQA_12054_ITSM_Creating_and_Testing_Problem_Lifecycle_in_ITSM *****");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        
        context.setAttribute("DRSQA_12054_Driver", getDriver());
        context.setAttribute("DRSQA_12054_Product", Product.Helix_ITSM);
        
        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12054_TestData");

        log.info("Step 1 : Login to Remedy Problem Management console --> Click New Problem");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Problem Management > New Problem");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.PROBLEM_MANAGEMENT, SolQAConstants.NEW_PROBLEM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_NewProblem", SolQAConstants.appITSM);

        log.info("Step 2 : Populate data in required fields, leave default selection and submit problem");
        String problemID = itsmFeatureMethods.createProblemApex(itsmTestData);
        log.info("Created Problem ID: " + problemID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_ProblemCreated", SolQAConstants.appITSM);

        log.info("Step 3 : From Problem Management console - Open the problem created in Step#2");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.PROBLEM_MANAGEMENT, SolQAConstants.SEARCH_PROBLEM);
        itsmFeatureMethods.searchProblem(problemID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_SearchProblem", SolQAConstants.appITSM);

        log.info("Step 4 : Click Next Stage button to update the status of the problem");
        log.info("Change status to 'Under Review'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.UNDER_REVIEW, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_ProblemStatusToUnderReview", SolQAConstants.appITSM);

        log.info("Step 5 : Click Next Stage button to update the status of the problem");
        log.info("Change status to 'Under Investigation'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.UNDER_INVESTIGATION, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_ProblemStatusToUnderInvestigation", SolQAConstants.appITSM);

        log.info("Step 6 : Click Next Stage button to update the status of the problem");
        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.COMPLETED_STATUS, SolQAConstants.ENHANCEMENT_REQUEST);
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_ProblemStatusToCompleted", SolQAConstants.appITSM);

        log.info("Step 7 : Click Next Stage button to update the status of the problem");
        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateProblemStatus(SolQAConstants.STATUS_CLOSED, "");
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_ProblemStatusToClosed", SolQAConstants.appITSM);

        log.info("Verify the status of the problem");
        itsmFeatureMethods.verifyProblemStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12054_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);

        log.info("***** END : DRSQA_12054_ITSM_Creating_and_Testing_Problem_Lifecycle_in_ITSM *****");
    }
    
    @Test(description = "[ITSM] Create Knowledge article from Knowledge Management console")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12055", Products = {Product.Helix_ITSM})
    public void DRSQA_12055_ITSM_Create_Knowledge_article_from_Knowledge_Management_console(ITestContext context) throws Exception {

        log.info("***** Start: DRSQA_12055_ITSM_Create_Knowledge_article_from_Knowledge_Management_console *****");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12055_Driver", getDriver());
        context.setAttribute("DRSQA_12055_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12055_TestData");

        log.info("Step 1 : Login to Remedy Knowledge Management console --> Click New article --> How To");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Knowledge Management > New Knowledge Article");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.KNOWLEDGE_MANAGEMENT, SolQAConstants.NEW_ARTICLE);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_NewKnowledgeArticle", SolQAConstants.appITSM);

        log.info("Step 2 : Populate data in required fields, leave default selection and submit incident");
        String knowledgeArticleID = itsmFeatureMethods.createKnowledgeArticle(itsmTestData);
        log.info("Created Knowledge Article ID: " + knowledgeArticleID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_KnowledgeArticleCreated", SolQAConstants.appITSM);

        log.info("Step 4 : Update the status of the article to Draft status");
        log.info("Change status to 'Draft'");
        itsmFeatureMethods.updateArticleStatus(SolQAConstants.DRAFT);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_KnowledgeStatusToDraft", SolQAConstants.appITSM);

        log.info("Change status to 'SME Review'");
        itsmFeatureMethods.updateArticleStatus(SolQAConstants.SME_REVIEW);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_KnowledgeStatusToSMEReview", SolQAConstants.appITSM);

        log.info("Step 5 : Update the status of the article to Publish Approval status and refresh the view via Refresh icon next to global search text box");
        log.info("Change status to 'Publish Approval'");
        itsmFeatureMethods.refreshITSMPage();
        itsmFeatureMethods.updateArticleStatus(SolQAConstants.PUBLISH_APPROVAL);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_KnowledgeStatusToPublishApproval", SolQAConstants.appITSM);
        itsmFeatureMethods.refreshITSMPage();

        itsmFeatureMethods.verifyArticleStatus(SolQAConstants.PUBLISHED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_VerifyStatusIsPublished", SolQAConstants.appITSM);
        
        //Knowledge Article is searched only when article is in Published status
        log.info("Step 3 : From Knowledge Management console - Open the article created in Step#2");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.KNOWLEDGE_MANAGEMENT, SolQAConstants.SEARCH_KNOWLEDGE);
        itsmFeatureMethods.searchKnowledgeArticle(knowledgeArticleID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12055_ITSM_SearchKnowledgeArticle", SolQAConstants.appITSM);

        log.info("Verify the article result");
        itsmFeatureMethods.verifyArticleResult();
        
        log.info("***** END: DRSQA_12055_ITSM_Create_Knowledge_article_from_Knowledge_Management_console  *****");
    }
    
    @Test(description = "[ITSM] Create Work Order from Work Order console ITSM")
    @SolqaDetails(authorName = "shudeshp",  JiraID = "DRSQA-12056", Products = {Product.Helix_ITSM})
    public void DRSQA_12056_ITSM_Create_Work_Order_from_Work_Order_console_ITSM(ITestContext context) throws IOException {
        log.info("***** START : DRSQA_12056_ITSM_Create_Work_Order_from_Work_Order_console_ITSM *****");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12056_Driver", getDriver());
        context.setAttribute("DRSQA_12056_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12056_TestData");

        log.info("Step 1 : Login to Remedy Work Order console --> Click Create Work Order");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Work Order Management > New Work Order");
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.NEW_WORK_ORDER);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_NewWorkOrder", SolQAConstants.appITSM);

        log.info("Step 2 : Search & Select customer details");
        log.info("Step 3 : Populate data in required fields, leave default selection and submit work order");
        String workOrderID = itsmFeatureMethods.createWorkOrderApex(itsmTestData);
        log.info("Created Work Order ID: " + workOrderID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_WorkOrderCreated", SolQAConstants.appITSM);

        log.info("Step 4 : From Work Order Management console - Open the work order created in Step#3");
        itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
        itsmFeatureMethods.searchWorkOrder(workOrderID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_SearchWorkOrder", SolQAConstants.appITSM);

        log.info("Change status to 'In Progress'");
        itsmFeatureMethods.updateWorkOrderStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_WorkOrderStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 5 : Update the status to Completed status with reason = Successfull");
        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateWorkOrderStatus(SolQAConstants.COMPLETED_STATUS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_WorkOrderStatusToCompleted", SolQAConstants.appITSM);

        log.info("Step 6 : Update the status to Closed with reason = Customer close");
        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateWorkOrderStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_WorkOrderStatusToClosed", SolQAConstants.appITSM);

        log.info("Verify the status of the work order");
        itsmFeatureMethods.verifyWorkOrderStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12056_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);

        log.info("***** END : DRSQA_12056_ITSM_Create_Work_Order_from_Work_Order_console_ITSM *****");
    }
    
    @Test(description = "[ITSM] Create Change request from Change Management console")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12057", Products = {Product.Helix_ITSM})
    public void DRSQA_12057_Create_ChangeRequest_from_ITSM(ITestContext context) throws Exception {
        log.info("***** Start: DRSQA_12057_Create_ChangeRequest_from_ITSM *****");

        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12057_Driver", getDriver());
        context.setAttribute("DRSQA_12057_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12057_TestData");

        log.info("Step 1: Login to Remedy Change Management console --> Click Create Change");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Change Management > New Change");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.NEW_CHANGE);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_NewChange", SolQAConstants.appITSM);

        log.info("Step 2: Populate data in mandatory fields, leave default selection and save ");
        String changeID = itsmFeatureMethods.createChange(itsmTestData);
        log.info("Created Change ID: " + changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeCreated", SolQAConstants.appITSM);

        log.info("Step 3: From Change Management console - Open the change created in Step#2");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
        itsmFeatureMethods.searchChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_SearchChange", SolQAConstants.appITSM);

        log.info("Step 4: Click Next Stage button to update the status of the change request & Refresh the change request view using refresh icon available next to global search box");
        log.info("Change status to 'Request For Authorization'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.REQUEST_FOR_AUTHORIZATION);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToRequestForAuthorization", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();

        log.info("Step 5: Login to Remedy ITSM as Approvers. Go to Quick Links --> Approval Central & Approve the Change request from Step#4");
        log.info("Approve the change request by first approver");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12057_Driver", getDriver());
        context.setAttribute("DRSQA_12057_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_Approver_Max");

        log.info("******Login to ITSM **********");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_Login", SolQAConstants.appITSM);

        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods.approveChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
        
        log.info("Approve the change request by second approver");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12057_Driver", getDriver());
        context.setAttribute("DRSQA_12057_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_Prisha");

        log.info("******Login to ITSM **********");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_Login", SolQAConstants.appITSM);

        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods.approveChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();

		log.info("Step 6: Login to Remedy Change Management console --> Open Change from Step#2");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
        itsmFeatureMethods.searchChangeRequest(changeID);

        log.info("Change status to 'Planning In Progress'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.PLANNING_IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToPlanningInProgress", SolQAConstants.appITSM);

        log.info("Change status to 'Scheduled For Review'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.SCHEDULED_FOR_REVIEW);
        itsmFeatureMethods.verifyChangeStatus(SolQAConstants.SCHEDULED_FOR_APPROVAL);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToClosed", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
        
        log.info("Step 7: Go to Quick Links --> Approval Central & Approve the Change request from Step#4");
        log.info("Step 9: Click Next Stage button to update the status of the change request & Refresh the change request view using refresh icon available next to global search box");
        log.info("Approve the change request by approver");
        readJson = new ReadJson();
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_Approver_Max");

        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_Login", SolQAConstants.appITSM);

        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods.approveChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
        
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
        itsmFeatureMethods.searchChangeRequest(changeID);

        log.info("Step 8: Click Next Stage button to update the status of the change request & Refresh the change request view using refresh icon available next to global search box");
        log.info("Step 10: Login to Remedy ITSM as Approver. Go to Quick Links --> Approval Central & Approve the Change request from Step#4");
        log.info("Change status to 'Scheduled'");
        itsmFeatureMethods.verifyChangeStatus(SolQAConstants.SCHEDULED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToScheduled", SolQAConstants.appITSM);

        log.info("Step 11: Click Next Stage button to update the status of the change request & Refresh the change request view using refresh icon available next to global search box");
        log.info("Change status to 'Implementation In Progress'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.IMPLEMENTATION_IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToImplementationInProgress", SolQAConstants.appITSM);

        log.info("Step 12: Click Next Stage button to update the status of the change request & Refresh the change request view using refresh icon available next to global search box");
        log.info("Change status to 'Completed'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.COMPLETED_STATUS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToCompleted", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();

        log.info("Approve the change request by approver");
        readJson = new ReadJson();
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_Approver_Max");

        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_Login", SolQAConstants.appITSM);

        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods.approveChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
        
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.CHANGE_MANAGEMENT, SolQAConstants.SEARCH_CHANGE);
        itsmFeatureMethods.searchChangeRequest(changeID);
        
        log.info("Verify status to Completed");
        itsmFeatureMethods.verifyChangeStatus(SolQAConstants.COMPLETED_STATUS);
        
        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateChangeStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12057_ITSM_ChangeStatusToClosed", SolQAConstants.appITSM);

        log.info("Verify the status of the change");
        itsmFeatureMethods.verifyChangeStatus(SolQAConstants.STATUS_CLOSED);

        log.info("***** End: DRSQA_12057_Create_ChangeRequest_from_ITSM *****");
    }
    
    @Test(description = "[ITSM] Create and Test Incident Lifecycle on ITSM")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12058", Products = {Product.Helix_ITSM})
    public void DRSQA_12058_ITSM_Create_and_Test_Incident_Lifecycle_on_ITSM(ITestContext context) throws IOException {

        log.info("***** START : DRSQA_12058_ITSM_Create_and_Test_Incident_Lifecycle_on_ITSM *****");
        
        log.info("Step 1 : Login to Remedy Incident Management console --> Click Create Incident");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12058_Driver", getDriver());
        context.setAttribute("DRSQA_12058_Product", Product.Helix_ITSM);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_SupportAgent_Seth");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMTestData", "DRSQA_12058_TestData");

        log.info("******Step 1: Login to ITSM **********");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_Login", SolQAConstants.appITSM);

        log.info("Navigate to Incident Management > New Incident");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.NEW_INCIDENT);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_NewIncident", SolQAConstants.appITSM);

        log.info("Step 2 : Search & Select customer details");
        log.info("Step 3 : Populate data in required fields, leave default selection and submit incident");
        String incID = itsmFeatureMethods.createIncident(itsmTestData);
        log.info("Created Incident ID: " + incID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_IncidentCreated", SolQAConstants.appITSM);

        log.info("Step 4 : From Incident Management console - Open the incident created in Step#3");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.INCIDENT_MANAGEMENT, SolQAConstants.SEARCH_INCIDENT);
        itsmFeatureMethods.searchIncident(incID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_SearchIncident", SolQAConstants.appITSM);

        log.info("Step 6 : Update the status of the incident to Pending with reason - Monitoring Incident");
        log.info("Change status to 'Pending'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.PENDING, "", SolQAConstants.MONITORING_INCIDENT);
        itsmFeatureMethods.verifyIncidentStatus(SolQAConstants.PENDING);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_ChangeStatusToPending", SolQAConstants.appITSM);
        
        log.info("Step 5 : Click on Next and move incident to In-Progress state");
        log.info("Change status to 'In Progress'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.IN_PROGRESS, SolQAConstants.TEST, "");
        itsmFeatureMethods.verifyIncidentStatus(SolQAConstants.IN_PROGRESS);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_ChangeStatusToInProgress", SolQAConstants.appITSM);

        log.info("Step 7 : Update the status to Resolved with Resolution comment = Resolved sample incident");
        log.info("Change status to 'Resolved'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.RESOLVED, "", SolQAConstants.FUTURE_ENHANCEMENT);
        itsmFeatureMethods.verifyIncidentStatus(SolQAConstants.RESOLVED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_ChangeStatusToResolved", SolQAConstants.appITSM);

        log.info("Step 8 : Update the status to Closed");
        log.info("Change status to 'Closed'");
        itsmFeatureMethods.updateIncidentStatus(SolQAConstants.STATUS_CLOSED, "", "");
        itsmFeatureMethods.verifyIncidentStatus(SolQAConstants.STATUS_CLOSED);
        itsmFeatureMethods.captureScreenshot("DRSQA_12058_ITSM_VerifyStatusIsClosed", SolQAConstants.appITSM);
        
        log.info("***** END : DRSQA_12058_ITSM_Create_and_Test_Incident_Lifecycle_on_ITSM *****");
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
    }
}